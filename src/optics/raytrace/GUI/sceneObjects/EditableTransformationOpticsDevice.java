package optics.raytrace.GUI.sceneObjects;

import java.io.Serializable;
import java.util.ArrayList;

import math.Geometry;
import math.MyMath;
import math.Vector3D;
import optics.raytrace.core.Ray;
import optics.raytrace.core.RaySceneObjectIntersection;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.Studio;
import optics.raytrace.core.SurfaceProperty;
import optics.raytrace.exceptions.RayTraceException;
import optics.raytrace.sceneObjects.ParametrisedConvexPolygon;
import optics.raytrace.surfaces.GlensSurface;
import optics.raytrace.surfaces.ImagingDirection;

public class EditableTransformationOpticsDevice
extends EditableSceneObjectCollection
implements Serializable
{
	private static final long serialVersionUID = 79205453894687447L;

	// the Cartesian coordinates of all the vertices, in physical space and in EM space
	protected Vector3D verticesP[], verticesE[];
	
	// the surfaces, in the form {surface 1, surface 2, ...};
	// each surface is of the form {index of vertex 1, index of vertex 2, ...}
	protected int surfaces[][];
	
	protected double interfaceTransmissionCoefficient;	// transmission coefficient of each interface
	protected boolean showFrames;
	protected double frameCylinderRadius;
	protected SurfaceProperty frameSurfaceProperty;

	
	//
	// constructors
	//
	
	/**
	 * @param description
	 * @param verticesP	the Cartesian coordinates of the vertices in physical space
	 * @param verticesE	the Cartesian coordinates of the vertices in EM space
	 * @param surfaces	the surfaces, in the form of a list of surfaces, with each surface being a list of vertex indices
	 * @param interfaceTransmissionCoefficient
	 * @param showFrames
	 * @param frameCylinderRadius
	 * @param frameSurfaceProperty
	 * @param parent
	 * @param studio
	 */
	public EditableTransformationOpticsDevice(
			String description, 
			Vector3D verticesP[], 
			Vector3D verticesE[], 
			int surfaces[][], 
			double interfaceTransmissionCoefficient,	// transmission coefficient of each interface
			boolean showFrames,
			double frameCylinderRadius,
			SurfaceProperty frameSurfaceProperty,
			SceneObject parent, Studio studio
		)
	{
		super(
				description,
				false,	// combination mode is not editable
				parent,
				studio
			);
		
		setVerticesE(verticesE);
		setVerticesP(verticesP);
		setSurfaces(surfaces);
		setInterfaceTransmissionCoefficient(interfaceTransmissionCoefficient);
		setShowFrames(showFrames);
		setFrameCylinderRadius(frameCylinderRadius);
		setFrameSurfaceProperty(frameSurfaceProperty);
		
		try
		{
			addSceneObjects();
		}
		catch (RayTraceException e)
		{
			e.printStackTrace();
		}
	}
	
	
	
	/**
	 * add the scene objects that 
	 */
	public void addSceneObjects()
	throws RayTraceException
	{
		// clear out any SceneObjects already in here
		clear();
		
		// check that all the surfaces are planar
		if(!areSurfacesPlanar())
		{
			// there is at least one non-planar surface; throw an exception
			throw new RayTraceException("Non-planar surface encountered.");
		}
		
		// calculate all GlensHolograms from the imaging properties
		GlensSurface[] glensHolograms = calculateSurfaceGlensHolograms();

		// add all surfaces
		for(int i=0; i<surfaces.length; i++)
		{
			addSceneObject(new ParametrisedConvexPolygon(
					"surface #"+i,	// description,
					getSurfaceNormal(i),
					getSurfaceVertices(i),
					glensHolograms[i],	// surfaceProperty,
					this,	// parent
					getStudio()	// studio
				)
			);
		}
		
		// also add the frames
		addFrames();
	}
	
	
	/**
	 * @return	an array of GlensHolograms, one for each surface
	 * @throws RayTraceException 
	 */
	protected GlensSurface[] calculateSurfaceGlensHolograms() throws RayTraceException
	{
		// initialise the array of surface properties, first by creating a bit of space for it...
		GlensSurface[] glensHolograms = new GlensSurface[surfaces.length];
		// ... and then by making all the entries null
		for(int i=0; i<surfaces.length; i++) glensHolograms[i] = null;
		
		// create a collection of SceneObjects that correspond to the surfaces, for the moment without SurfaceProperties;
		// we'll later use this to find combinations of surfaces that lead from any given surface to the outside
		ParametrisedConvexPolygon surfaceCollection[] = new ParametrisedConvexPolygon[surfaces.length];	// don't need the parent and studio
		for(int i=0; i<surfaces.length; i++)
		{
			surfaceCollection[i] = new ParametrisedConvexPolygon(
					null,	// description,
					getSurfaceNormal(i),
					getSurfaceVertices(i),
					(SurfaceProperty)null,	// surfaceProperty,
					this,	// parent
					(Studio)null	// studio
				);
		}
		
		// until there are no more surfaces whose surface properties have not been calculated...
		int s = findIndexOfSurfaceWithoutProperty(glensHolograms);
		while(s != -1)	// is there still a surface without surface property?
		{
			Vector3D generalDirection = getSurfaceNormal(s);	// use the start surface's surface normal as the "general direction" of the route
			
			// find a combination of surfaces that lead from surface #s to the outside;
			// the index of the outside surface is last in the list
			ArrayList<Integer> route = findRouteOutsideFromSurface(s, generalDirection, surfaceCollection);
			
			// insert, as the first element, the first surface encountered in the opposite direction
			route.add(
					0,	// insert at index 0, i.e. this will be the first element
					findNeighbouringSurface(
							getSurfaceNormal(s).getReverse(),	// in findRouteOutsideFromSurface, the general direction is getSurfaceNormal(s); here, search in the opposite direction
							s,	// start the search for the next surface at surface #s
							surfaceCollection
						)
				);
			
			// a list of GlensHolograms encountered on the way from the outside to surface #s2
			ArrayList<GlensSurface> glensHologramsOnRoute = new ArrayList<GlensSurface>();
			
			// starting from the outside surface, calculate the GlensHolograms for each of the surfaces on the route
			// if these are not already known; do so by considering the imaging properties of each surface
			for(int s2=route.size()-1; s2>0; s2--)
			{
				Vector3D positionP, positionE;
				
				// is the GlensHologram for surface #s2 in the route already known?
				if(glensHolograms[route.get(s2)] == null)
				{
					// the GlensHologram for surface #s2 in the route is not already known; calculate it
					
					// is there a next-innermost surface?
					if(route.get(s2-1) != -1)
					{
						// there is a next-innermost surface
						
						// find a vertex of the next surface, not common with surface #s2, which we can use to establish the imaging properties of the surface
						int vertexIndex = findNonSharedVertexOfSurface2(route.get(s2), route.get(s2-1));
						if(vertexIndex == -1)
						{
							// there is no vertex of surface 2 which is not also shared by surface 1;
							throw new RayTraceException("Surface #"+route.get(s2-1)+" has no vertex that surface #"+route.get(s2)+" doesn't also have.");
						}
					
						// use the corresponding vertex positions as positions in physical and EM space
						positionP = verticesP[vertexIndex];
						positionE = verticesE[vertexIndex];
					}
					else
					{
						// there is no next-innermost surface, which means that surface s2 is an outside surface again;
						// this should happen only if s2 == 1
						if(s2 != 1)
						{
							throw new RayTraceException("Surface index -1 unexpectedly encountered.");
						}
						
						// the series of glens holograms along the route therefore has to image any point back to itself;
						// choose one that doesn't lie on this surface
						positionP = Vector3D.sum(getRandomPositionOnSurface(s2), getSurfaceNormal(s2));
						positionE = positionP;
					}
				
					// the combination of the glens holograms in glensHolograms images the vertex in EM space to an intermediate position,
					// which the surface then images to its physical-space position
					
					// first find that intermediate position, p
					Vector3D p = positionE;
					for(int i=0; i<glensHologramsOnRoute.size(); i++)
					{
						
						p = glensHologramsOnRoute.get(i).getImagePosition(
								p,	// object position
								(Vector3D.scalarProduct(
										glensHologramsOnRoute.get(i).getOpticalAxisDirectionPos(),
										generalDirection
									) > 0)?ImagingDirection.POS2NEG:ImagingDirection.NEG2POS
							);
					}
					
					// the intermediate position, p, then has to be imaged by the surface to positionP
					// TODO
				}
				
				// add the GlensHologram for surface #s2 in the route to glensHolograms
				glensHologramsOnRoute.add(glensHolograms[route.get(s2)]);
			}
			
			// find the next surface without property
			s = findIndexOfSurfaceWithoutProperty(glensHolograms);
		}
		
		// return the list of GlensHolograms
		return glensHolograms;
	}
	
	/**
	 * @param surface1Index
	 * @param surface2Index
	 * @return	the index of the first vertex of surface 2 that is not also a vertex of surface 1; -1 if none is found
	 */
	protected int findNonSharedVertexOfSurface2(int surface1Index, int surface2Index)
	{
		// go through the vertices of surface 2
		for(int i2 = 0; i2 < surfaces[surface2Index].length; i2++)
		{
			// the index of the (i2)th vertex of surface 2
			int i = surfaces[surface2Index][i2];

			// go through the vertices of surface 1 until we come across vertex #i in there
			int i1 = 0;
			while((i1 < surfaces[surface1Index].length) && (surfaces[surface1Index][i1] != i))
			{
				i1++;
			}
				
			// has vertex #i been found in the vertices of surface 1?
			if(i1 == surfaces[surface1Index].length)
			{
				// vertex #i has not been found in the list of vertices of surface 1, so it is unique to vertex 2; return it
				return i;
			}
		}
		
		// none of the vertices of surface 2 is not shared with surface 1; return -1
		return -1;
	}
	
	
	/**
	 * go through all the surfaces and check that they are planar
	 * @return	true if the are, false if they aren't
	 */
	protected boolean areSurfacesPlanar()
	{
		// go through all the surfaces
		for(int s=0; s<surfaces.length; s++)
		{
			// check if the vertices of surface #s lie in a plane
			if(!Geometry.arePointsInPlane(getSurfaceVertices(s)))
			{
				// the vertices of surface #s don't lie in a plane; return false
				return false;
			}
		}
		
		// no non-planar surfaces have been encountered; return true
		return true;
	}
	
	/**
	 * @param surfaceIndex
	 * @return	the normalised surface normal
	 */
	protected Vector3D getSurfaceNormal(int surfaceIndex)
	{
		return Vector3D.crossProduct(
				Vector3D.difference(verticesP[surfaces[surfaceIndex][1]], verticesP[surfaces[surfaceIndex][0]]),
				Vector3D.difference(verticesP[surfaces[surfaceIndex][2]], verticesP[surfaces[surfaceIndex][0]])
			).getNormalised();
	}
	
	/**
	 * @param surfaceIndex
	 * @return	the centre of mass of the vertices of surface #surfaceIndex
	 */
	protected Vector3D getSurfaceCentreOfMass(int surfaceIndex)
	{
		Vector3D sum = new Vector3D(0, 0, 0);
		
		// summ all the vertices...
		for(int i=0; i<surfaces[surfaceIndex].length; i++)
		{
			// add the ith vertex to the sum
			sum = Vector3D.sum(sum, verticesP[surfaces[surfaceIndex][i]]);
		}
		
		// ... and divide by the number of vertices
		return sum.getProductWith(1/surfaces[surfaceIndex].length);
	}
	
	
	/**
	 * @param glensHolograms
	 * @return	the index of the first surface that doesn't have a corresponding GlensHologram in the array glensHolograms; -1 if all surfaces have SurfaceProperties
	 */
	protected int findIndexOfSurfaceWithoutProperty(GlensSurface[] glensHolograms)
	{
		// go through all the surfaces
		for(int s=0; s<glensHolograms.length; s++)
		{
			if(glensHolograms[s] == null)
			{
				// surface #s doesn't have a surface property yet; return it
				return s;
			}
		}
		
		// no surface without corresponding SurfaceProperty was found
		return -1;
	}

	
	/**
	 * Starting from surface #surfaceIndex, find a "route" to the outside
	 * @param surfaceIndex
	 * @param generalDirection
	 * @param surfaceCollection	an array of SceneObjects, one for each surface
	 * @return	a list of the indices of the surfaces encountered along the route
	 */
	protected ArrayList<Integer> findRouteOutsideFromSurface(int surfaceIndex, Vector3D generalDirection, ParametrisedConvexPolygon[] surfaceCollection)
	{
		// create a space where the indices of the surfaces encountered on the way out will be stored
		ArrayList<Integer> route = new ArrayList<Integer>();
		
		// add this surface to the route
		route.add(surfaceIndex);
		
		// find a route to the outside
		route = completeRouteToOutside(
				generalDirection,
				route,	// the route so far
				surfaceCollection	// an array of SceneObjects, one for each surface
			);

		// return the route
		return route;
	}
	
	
	/**
	 * Starting from the last surface in the route so far, complete the route to the outside
	 * @param generalDirection	a direction vector; any steps taken will be in a direction whose scalar product with generalDirection is +ve
	 * @param route	the route so far, in the form of a list of the indices of the surfaces encountered so far
	 * @param surfaceCollection	an array of SceneObjects, one for each surface
	 * @return	the completed route
	 */
	protected ArrayList<Integer> completeRouteToOutside(Vector3D generalDirection, ArrayList<Integer> route, ParametrisedConvexPolygon[] surfaceCollection)
	{
		// index of last surface in the route so far, which is the surface from which we start here
		int surfaceIndex = route.get(route.size()-1);
		
		// find the index of the next surface
		int nextSurfaceIndex = findNeighbouringSurface(generalDirection, surfaceIndex, surfaceCollection);
				
		// is there a next surface?
		if(nextSurfaceIndex == -1)
		{
			// there is no next surface; we have reached the outside!
			// the route is complete; return it
			return route;
		}
		
		// add the nearest surface to the route...
		route.add(nextSurfaceIndex);
		
		// ... and complete the route to the outside from there
		return completeRouteToOutside(generalDirection, route, surfaceCollection);
	}

	
	/**
	 * Starting from the last surface in the route so far, complete the route to the outside
	 * @param generalDirection	a direction vector; any steps taken will be in a direction whose scalar product with generalDirection is +ve
	 * @param surfaceIndex	the index of the current surface
	 * @param surfaceCollection	an array of SceneObjects, one for each surface
	 * @return	the completed route
	 */
	protected int findNeighbouringSurface(Vector3D generalDirection, int surfaceIndex, ParametrisedConvexPolygon[] surfaceCollection)
	{
		// we'll find the two nearest surfaces; here is where we'll store the relevant information
		int iMin;	// index of the nearest surface
		double dMin, dMin2;	// distances to the two nearest surfaces

		do
		{
			// find a starting point on the last surface in the route so far
			Vector3D startingPoint = getRandomPositionOnSurface(surfaceIndex);

			// find a suitable direction; use the surface normal, ...
			Vector3D direction = getSurfaceNormal(surfaceIndex);
			// ... turned round if it does not point in the "general direction" of the route
			if(Vector3D.scalarProduct(direction,  generalDirection) < 0) direction = direction.getReverse();

			//
			// find the two nearest other surfaces in that direction
			// 

			// initialise the index of the nearest surface to "none so far"
			iMin = -1;
			// initialise the minimum distances to "none so far"
			dMin = Double.POSITIVE_INFINITY;
			dMin2 = Double.POSITIVE_INFINITY;

			Ray ray = new Ray(startingPoint, direction, 0);

			// go through all the other surfaces
			for(int i=0; i<surfaces.length; i++)
			{
				// is i the same as s?
				if(i != surfaceIndex)
				{
					// i is not the same as s, so surface #i is different from the surface from which we start here

					// does the ray that starts at startingPoint in direction intersect surface #i?
					RaySceneObjectIntersection intersection = surfaceCollection[i].getClosestRayIntersection(ray);
					if(intersection != RaySceneObjectIntersection.NO_INTERSECTION)
					{
						// the ray does intersect surface #i; calculate the distance to the intersection point
						double distance = Vector3D.difference(intersection.p, startingPoint).getLength();

						// is this distance smaller than, or equal to, the minimum distance so far?
						if(distance <= dMin)
						{
							// yes, distance is smaller than the current minimum distance

							// what used to be the minimum distance is now the second-smallest distance
							dMin2 = dMin;

							// distance is the new minimum distance
							iMin = i;
							dMin = distance;
						}
						else
						{
							// distance is not the minimum distance, but is it the second-smallest distance?
							if(distance < dMin2)
							{
								// yes, distance is smaller than the current second-smallest distance, so it is the new second-smallest distance
								dMin2 = distance;
							}
						}
					}
				}
			}
			
			// has the ray intersected any object?  check by checking if there is now an index of the closest object
			if(iMin == -1)
			{
				// no intersection has been found; return -1 (i.e. "no surface")
				return -1;
			}
			
		// if there are two surfaces at essentially the same distance, we've managed to hit an edge,
		// which makes it hard to establish the correct order in which the surfaces are being encountered
		// (either that, or the surfaces are defined stupidly); try again, with a different starting position
		} while(Math.abs(dMin2-dMin) < MyMath.EPSILON);
		
		// return the index of the next surface
		return iMin;
	}

	
	/**
	 * @param surfaceIndex
	 * @return	a random position on the surface
	 */
	protected Vector3D getRandomPositionOnSurface(int surfaceIndex)
	{
		// find a random position on the (polygonal) surface by calculating a randomly weighed average of the vertex positions
		
		// create a list of weights, each between 0 and 1, such that they add up to 1
		// first reserve the space for the weights, ...
		double weights[] = new double[surfaces[surfaceIndex].length];
		// ... then make up un-normalised random weights, ...
		double weight = 0;
		for(int i=0; i<surfaces[surfaceIndex].length; i++)
		{
			weights[i] = Math.random();
			weight = weight+weights[i];
		}
		// ... and finally normalise them
		for(int i=0; i<surfaces[surfaceIndex].length; i++)
		{
			weights[i] = weights[i] / weight;
		}
		
		// now form the weighted sum
		Vector3D sum = new Vector3D(0, 0, 0);
		
		// sum all the vertices, multiplied by their weights
		for(int i=0; i<surfaces[surfaceIndex].length; i++)
		{
			// add the ith vertex to the sum
			sum = Vector3D.sum(sum, verticesP[surfaces[surfaceIndex][i]].getProductWith(weights[i]));
		}
		
		return sum;
	}
	
	/**
	 * add the frames, i.e. a sphere at each physical-space vertex position and a cylinder along each edge of each surface
	 */
	protected void addFrames()
	{
		// create a collection for the frames
		EditableSceneObjectCollection frames = new EditableSceneObjectCollection(
				"frames",	// description,
				false,	// combinationModeEditable,
				getParent(),
				getStudio()
			);
		
		// add all the frames

		// create a collection for the spheres at the vertices
		EditableSceneObjectCollection spheres = new EditableSceneObjectCollection(
				"spheres at vertices",	// description,
				false,	// combinationModeEditable,
				frames,
				getStudio()
			);

		// create a collection for the cylinders that link the vertices
		EditableSceneObjectCollection cylinders = new EditableSceneObjectCollection(
				"cylinders",	// description,
				false,	// combinationModeEditable,
				frames,
				getStudio()
			);
		
		// add all the spheres at the vertices
		for(int i=0; i<verticesP.length; i++) addFrameSphere("sphere at vertex #"+i, verticesP[i], spheres);
		
		// add all the cylinders joining the vertices
		// for each surface...
		for(int s=0; s<surfaces.length; s++)
		{
			// ... go through all the vertices and draw the cylinder from this one to the next
			for(int i=0; i<surfaces[s].length; i++)
			{
				// if "this" vertex index is i, then the next one is the following:
				int iNext = ((i+1) < surfaces[s].length)?(i+1):0;
				
				// now add a cylinder from vertex i to vertex i+1
				addFrameCylinder(
						"cylinder from surface "+s+"'s vertex #"+i+" to vertex "+iNext,
						verticesP[surfaces[s][i]],
						verticesP[surfaces[s][iNext]],
						cylinders
					);
			}
		}
		
		// add the spheres and the cylinders to frames
		frames.addSceneObject(spheres);
		frames.addSceneObject(cylinders);

		// add the frames to this
		addSceneObject(frames, showFrames);
	}
	
	
	//
	// getters & setters
	//
	
	public Vector3D[] getVerticesP() {
		return verticesP;
	}

	public void setVerticesP(Vector3D[] verticesP) {
		this.verticesP = verticesP;
	}

	public Vector3D[] getVerticesE() {
		return verticesE;
	}

	public void setVerticesE(Vector3D[] verticesE) {
		this.verticesE = verticesE;
	}

	public int[][] getSurfaces() {
		return surfaces;
	}

	public void setSurfaces(int[][] surfaces) {
		this.surfaces = surfaces;
	}

	public double getInterfaceTransmissionCoefficient() {
		return interfaceTransmissionCoefficient;
	}

	public void setInterfaceTransmissionCoefficient(double interfaceTransmissionCoefficient) {
		this.interfaceTransmissionCoefficient = interfaceTransmissionCoefficient;
	}

	public boolean isShowFrames() {
		return showFrames;
	}

	public void setShowFrames(boolean showFrames) {
		this.showFrames = showFrames;
	}

	public double getFrameCylinderRadius() {
		return frameCylinderRadius;
	}

	public void setFrameCylinderRadius(double frameCylinderRadius) {
		this.frameCylinderRadius = frameCylinderRadius;
	}

	public SurfaceProperty getFrameSurfaceProperty() {
		return frameSurfaceProperty;
	}

	public void setFrameSurfaceProperty(SurfaceProperty frameSurfaceProperty) {
		this.frameSurfaceProperty = frameSurfaceProperty;
	}

	
	//
	// handy methods
	//
	
	/**
	 * @param surfaceIndex
	 * @return	an array of vectors pointing to the vertices of surface #surfaceIndex
	 */
	Vector3D[] getSurfaceVertices(int surfaceIndex)
	{
		// create an array with the vertices...
		Vector3D vertices[] = new Vector3D[surfaces[surfaceIndex].length];
		for(int i=0; i<surfaces[surfaceIndex].length; i++) vertices[i] = verticesP[surfaces[surfaceIndex][i]];

		return vertices;
	}
	
	//
	// very handy static methods
	//

	/**
	 * Creates a glens in the shape of a 4-sided convex polygon, with vertices c1 to c4.
	 * The glens images QNeg (in -ve space) to QPos (in +ve space), and RNeg to RPos.
	 * @param description
	 * @param c1	vertex 1
	 * @param c2	vertex 2
	 * @param c3	vertex 3
	 * @param c4	vertex 4
	 * @param neg2posVector	vector pointing in the direction of the glens's +ve space; NOT NECESSARY SURFACE NORMAL!
	 * @param QNeg
	 * @param QPos
	 * @param RNeg
	 * @param RPos
	 * @param parent
	 * @param studio
	 * @return
	 */
	public EditableParametrisedConvexPolygon getConvexPolygonalGlens(
			String description,
			Vector3D vertices[],
			Vector3D neg2posVector,
			Vector3D QNeg, Vector3D QPos, Vector3D RNeg, Vector3D RPos,
			SceneObject parent, Studio studio
		)
	{
		// (vertices[1]-vertices[0]) x (vertices[1]-vertices[2]) should be normal to the polygon plane
		Vector3D normal = Vector3D.crossProduct(
				Vector3D.difference(vertices[1], vertices[0]),
				Vector3D.difference(vertices[1], vertices[2])
			);
		
		// not sure how important this is, but make the normal point outwards, i.e.
		// reverse the normal if normal.outwardsDirection < 0
		if(Vector3D.scalarProduct(normal, neg2posVector) < 0)
		{
			// the normal points inwards; reverse it
			normal = normal.getReverse();
		}
		
		return new EditableParametrisedConvexPolygon(
				description,	// description,
				normal,	// normalToPlane,
				vertices,	// vertices[],
				new GlensSurface(
						vertices[0],	// pointOnGlens,
						normal,	// opticalAxisDirectionPos; "outside" is therefore in positive space
						QNeg, QPos,	// QNeg, QPos,
						RNeg, RPos,	// RNeg, RPos,
						getInterfaceTransmissionCoefficient(),	// transmissionCoefficient,
						false	// shadowThrowing
					),	// surfaceProperty,
				parent, studio
			);
	}

	/**
	 * Creates a glens in the shape of a triangle, with vertices c1 to c3.
	 * The glens images QNeg (in -ve space) to QPos (in +ve space), and RNeg to RPos.
	 * @param description
	 * @param c1	vertex 1
	 * @param c2	vertex 2
	 * @param c3	vertex 3
	 * @param neg2posVector	vector pointing in the direction of the glens's +ve space; NOT NECESSARY SURFACE NORMAL!
	 * @param QNeg
	 * @param QPos
	 * @param RNeg
	 * @param RPos
	 * @param parent
	 * @param studio
	 * @return
	 */
	public EditableParametrisedConvexPolygon getConvexPolygonalGlens(
			String description,
			Vector3D c1, Vector3D c2, Vector3D c3,
			Vector3D neg2posVector,
			Vector3D QNeg, Vector3D QPos, Vector3D RNeg, Vector3D RPos,
			SceneObject parent, Studio studio
		)
	{
		Vector3D vertices[] = new Vector3D[3];
		vertices[0] = c1;
		vertices[1] = c2;
		vertices[2] = c3;

		return getConvexPolygonalGlens(description, vertices, neg2posVector, QNeg, QPos, RNeg, RPos, parent, studio);
	}
	
	/**
	 * Creates a glens in the shape of a convex 4-sided polygon, with vertices c1 to c4.
	 * The glens images QNeg (in -ve space) to QPos (in +ve space), and RNeg to RPos.
	 * @param description
	 * @param c1	vertex 1
	 * @param c2	vertex 2
	 * @param c3	vertex 3
	 * @param c4	vertex 4
	 * @param neg2posVector	vector pointing in the direction of the glens's +ve space; NOT NECESSARY SURFACE NORMAL!
	 * @param QNeg
	 * @param QPos
	 * @param RNeg
	 * @param RPos
	 * @param parent
	 * @param studio
	 * @return
	 */
	public EditableParametrisedConvexPolygon getConvexPolygonalGlens(
			String description,
			Vector3D c1, Vector3D c2, Vector3D c3, Vector3D c4,
			Vector3D neg2posVector,
			Vector3D QNeg, Vector3D QPos, Vector3D RNeg, Vector3D RPos,
			SceneObject parent, Studio studio
		)
	{
		Vector3D vertices[] = new Vector3D[4];
		vertices[0] = c1;
		vertices[1] = c2;
		vertices[2] = c3;
		vertices[3] = c4;

		return getConvexPolygonalGlens(description, vertices, neg2posVector, QNeg, QPos, RNeg, RPos, parent, studio);
	}
	
	protected void addFrameCylinder(String description, Vector3D startPosition, Vector3D endPosition, EditableSceneObjectCollection collection)
	{
		collection.addSceneObject(new EditableParametrisedCylinder(
				description,
				startPosition,	// start point
				endPosition,	// end point
				getFrameCylinderRadius(),	// radius
				getFrameSurfaceProperty(),
				collection,
				getStudio()
		));
	}
	
	protected void addFrameSphere(String description, Vector3D centrePosition, EditableSceneObjectCollection collection)
	{
		collection.addSceneObject(new EditableScaledParametrisedSphere(
				description,
				centrePosition,	// centre
				getFrameCylinderRadius(),	// radius
				getFrameSurfaceProperty(),
				collection,
				getStudio()
		));
	}

}
