package optics.raytrace.sceneObjects;

import java.io.*;

import math.*;
import optics.raytrace.core.*;
import optics.raytrace.exceptions.RayTraceException;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.surfaces.SurfaceColour;


/**
 * Scene object that represents a triangulated surface.
 * The triangles are described by a 2D array of points p_i,j on the surface.
 * There are then two types of triangles:
 * triangles a_ij have vertices p_i,j, p_i,j+1, and p_i+1,j;
 * triangles b_ij have vertices p_i,j, p_i-1,j, and p_i,j-1.
 * Note that these are defined slightly differently from those in [1].
 * Reference: [1] J. Amanatides and K. Choi, Ray Tracing Triangular Meshes, Proceedings of the Eighth Western Computer Graphics Symposium, 43-52 (1997)
 * @author johannes
 */
public class TriangulatedSurface extends SceneObjectPrimitive implements Serializable
{
	private static final long serialVersionUID = 3211361823929089450L;

	// variables

//	// for debugging
//	public boolean showOnlyOneTriangle = false;
//	public int oneTriangleI = 0;
//	public int oneTriangleJ = 0;
	
	/**
	 * 2D array of vertices v_i,j on the surface
	 */
	private Vector3D[][] v;
	
	/**
	 * Plücker coordinates for edges of types 0 to 2; 
	 * PlueckerCoordinates[edgeType][i][j] gives the Plücker coordinate for edge (i,j) of type edgeType
	 * type-1 edge (i,j) is from vertex v_i,j to v_i,j+1
	 */
	private PlueckerCoordinates[][][] e;
	
	/**
	 * if false, outwards normal is in direction edge01 x edge12, otherwise in the opposite direction
	 */
	private boolean invertSurface;



	// constructor
	
	/**
	 * Creates a triangulated surface defined by the vertices in the rectangular array v[][].
	 * This constructor sets its internal array of vertices, this.v, to the argument v, i.e. v is NOT copied.
	 * @param description
	 * @param v
	 * @param surfaceProperty
	 * @param parent
	 * @param studio
	 */
	public TriangulatedSurface(
			String description,
			Vector3D[][] v,
			boolean invertSurface,
			SurfaceProperty surfaceProperty,
			SceneObject parent,
			Studio studio
		)
	{
		super(description, surfaceProperty, parent, studio);
		this.invertSurface = invertSurface;
		setV(v);
	}

	
	/**
	 * Create a clone of the original
	 * @param original
	 */
	public TriangulatedSurface(TriangulatedSurface original)
	{
		this(
				original.description,
				original.getV(),
				original.isInvertSurface(),
				original.getSurfaceProperty().clone(),
				original.getParent(),
				original.getStudio()
			);
	}
	
	/* (non-Javadoc)
	 * @see optics.raytrace.sceneObjects.SceneObject#clone()
	 */
	@Override
	public TriangulatedSurface clone()
	{
		return new TriangulatedSurface(this);
	}
	
	
	
	// setters & getters

	public Vector3D[][] getV() {
		return v;
	}


	public void setV(Vector3D[][] v) {
		this.v = v;
		
		// from the new positions, calculate Plücker coordinates for edges
		e = new PlueckerCoordinates[3][getVIMax()][getVJMax()];
		for(int edgeType=0; edgeType<3; edgeType++)
			for(int i=0; i<getEIMax(edgeType); i++)
				for(int j=0; j<getEJMax(edgeType); j++)
					e[edgeType][i][j] = new PlueckerCoordinates(getVertexForEdge(edgeType, i, j, 0), getVertexForEdge(edgeType, i, j, 1));

	}


	public boolean isInvertSurface() {
		return invertSurface;
	}


	public void setInvertSurface(boolean invertSurface) {
		this.invertSurface = invertSurface;
	}


	public PlueckerCoordinates[][][] getE() {
		return e;
	}


	public void setE(PlueckerCoordinates[][][] e) {
		this.e = e;
	}



	// useful methods
	
	/**
	 * @return	the size of the vertex array in the i dimension
	 */
	public int getVIMax()
	{
		return v.length;
	}
	
	/**
	 * @return	the size of the vertex array in the j dimension
	 */
	public int getVJMax()
	{
		return v[0].length;
	}
	
	/**
	 * @return	the size of the array of edges of the given type in the i dimension
	 */
	public int getEIMax(int edgeType)
	{
		if(edgeType == 0) return getVIMax();
		return getVIMax()-1;
	}

	/**
	 * @return	the size of the array of edges of the given type in the i dimension
	 */
	public int getEJMax(int edgeType)
	{
		if(edgeType == 2) return getVJMax();
		return getVJMax()-1;
	}
	
	/**
	 * @param edgeType	0, 1, or 2
	 * @param i
	 * @param j
	 * @param vertexNumber	0 or 1
	 * @return
	 */
	public Vector3D getVertexForEdge(int edgeType, int i, int j, int vertexNumber)
	{
		switch((edgeType + vertexNumber) % 3)	// vertex 1 of the edge of type 
		{
		case 0:
			// type-0 edge (i,j) is from point p_i,j to p_i,j+1
			return v[i][j];
		case 1:
			// type-1 edge (i,j) is from point p_i,j+1 to p_i+1,j
			return v[i][j+1];
		case 2:
		default:
			// type-2 edge (i,j) is from point p_i+1,j to p_i,j
			return v[i+1][j];
		}
	}

	/**
	 * @param triangleType	0 or 1
	 * @param i
	 * @param j
	 * @param vertexNumber	0, 1, or 2
	 * @return	the vertex given by vertexNumber of triangle (i,j) of the type given by triangleType
	 */
	public Vector3D getVertexForTriangle(int triangleType, int i, int j, int vertexNumber)
	{
		if(triangleType == 0)
		{
			// triangle type 0
			return getVertexForEdge(vertexNumber, i, j, 0);
		}
		else
		{
			// triangle type 1
			switch(vertexNumber % 3)
			{
			case 0:
				return getVertexForEdge(0, i+1, j, 0);
			case 1:
				return getVertexForEdge(1, i, j, 0);
			case 2:
			default:
				return getVertexForEdge(2, i, j+1, 0);
			}
		}
//		switch(vertexNumber % 3)
//		{
//		case 0:
//			return (triangleType % 2 == 0)?(v[i][j]):(v[i+1][j+1]);
//		case 1:
//			return v[i][j+1];
//		case 2:
//		default:
//			return v[i+1][j];
//		}
	}
	
	/**
	 * @param triangleType	0 or 1
	 * @param i
	 * @param j
	 * @return	the outwards normal of triangle (i,j) of the type given by triangleType
	 */
	public Vector3D getOutwardsSurfaceNormal(int triangleType, int i, int j)
	{
		Vector3D edge01 = Vector3D.difference(getVertexForTriangle(triangleType, i, j, 1), getVertexForTriangle(triangleType, i, j, 0));	// vertex 0 to vertex 1
		Vector3D edge12 = Vector3D.difference(getVertexForTriangle(triangleType, i, j, 2), getVertexForTriangle(triangleType, i, j, 1));	// vertex 1 to vertex 2

		// if(triangleType == 0) 
			return Vector3D.crossProduct(edge01, edge12).getWithLength(invertSurface?(-1):(+1));
		// else return Vector3D.crossProduct(edge12, edge01).getNormalised();	// opposite sign, as handedness of edge vectors around triangle is reversed
	}
	
	/**
	 * @param triangleType
	 * @param i
	 * @param j
	 * @return	the centroid of triangle (i, j) of type triangleType
	 */
	public Vector3D getCentroid(int triangleType, int i, int j)
	{
		return Vector3D.sum(
				getVertexForTriangle(triangleType, i, j, 0),
				getVertexForTriangle(triangleType, i, j, 1),
				getVertexForTriangle(triangleType, i, j, 2)
			).getProductWith(1./3.);
	}
	

	/**
	 * @return	a point on the surface, not on a vertex or edge between vertices
	 */
	public Vector3D getPointOnSurface()
	{
		// calculate the centroid of the first triangle
		return Vector3D.sum(
				v[0][0],
				v[1][0],
				v[0][1]
			).getProductWith(1./3.);
	}
	
	
	
	// some graphics methods
	
	public SceneObjectContainer getArrayOfSpheresAtVertices(double sphereRadius, SceneObject parent, Studio studio)
	{
		SceneObjectContainer array = new SceneObjectContainer("Array of spheres at vertices", parent, studio);
		
		for(int i=0; i<getVIMax(); i++)
			for(int j=0; j<getVJMax(); j++)
			{
				array.addSceneObject(new Sphere(
						"Vertex #("+i+","+j+")",	// description
						v[i][j],	// centre
						sphereRadius,	// radius
						SurfaceColour.BLACK_SHINY,	// surfaceProperty
						parent,
						studio
					));
			}
		
		return array;
	}

	public SceneObjectContainer getArrayOfArrowsAtEdges(double shaftRadius, SceneObject parent, Studio studio)
	{
		SceneObjectContainer array = new SceneObjectContainer("Array of arrows at edges", parent, studio);
		
		for(int edgeType=0; edgeType<3; edgeType++)
			for(int i=0; i<getEIMax(edgeType); i++)
				for(int j=0; j<getEJMax(edgeType); j++)
				{
					SurfaceProperty s;
					switch(edgeType)
					{
					case 0:
						s = SurfaceColour.RED_SHINY;
						break;
					case 1:
						s = SurfaceColour.GREEN_SHINY;
						break;
					case 2:
					default:
						s = SurfaceColour.BLUE_SHINY;
					}
					array.addSceneObject(new Arrow(
							"Edge #("+i+","+j+"), type "+edgeType,	// description
							getVertexForEdge(edgeType, i, j, 0),	// startPoint
							getVertexForEdge(edgeType, i, j, 1),	// endPoint
							shaftRadius,
							2*shaftRadius,	// tipLength
							MyMath.deg2rad(45),	// tipAngle
							s,	// surfaceProperty
							parent, 
							studio
							));
				}
		
		return array;
	}

	public SceneObjectContainer getArrayOfCylindersAtEdges(double radius, SurfaceProperty surfaceProperty, SceneObject parent, Studio studio)
	{
		SceneObjectContainer array = new SceneObjectContainer("Array of cylinders at edges", parent, studio);
		
		for(int edgeType=0; edgeType<3; edgeType++)
			for(int i=0; i<getEIMax(edgeType); i++)
				for(int j=0; j<getEJMax(edgeType); j++)
				{
					array.addSceneObject(
							new CylinderMantle(
									"Edge #("+i+","+j+"), type "+edgeType,	// description
									getVertexForEdge(edgeType, i, j, 0),	// startPoint
									getVertexForEdge(edgeType, i, j, 1),	// endPoint
									radius,
									surfaceProperty,
									parent, 
									studio
							)
						);
				}
		
		return array;
	}


	
	private enum TriangleStatus {NOT_SET, PLUECKER_POSITIVE, PLUECKER_NEGATIVE, ELIMINATED}
	
	
	// SceneObject methods
	
	/* See J. Amanatides and K. Choi, Ray Tracing Triangular Meshes, Proceedings of the Eighth Western Computer Graphics Symposium, 43-52 (1997)
	 * @see optics.raytrace.core.SceneObject#getClosestRayIntersection(optics.raytrace.core.Ray)
	 */
	@Override
	public RaySceneObjectIntersection getClosestRayIntersection(Ray ray)
	{
		// create an array of triangleStatus, one per triangle...
		TriangleStatus[][][] triangleStatus = new TriangleStatus[2][getVIMax()-1][getVJMax()-1];
		
//		// ... and initially set them all
//		for(int i=0; i<getVIMax()-1; i++)
//			for(int j=0; j<getVJMax()-1; j++)
//				for(int triangleType=0; triangleType<2; triangleType++)
//					triangleStatus[triangleType][i][j] = TriangleStatus.NOT_SET;
		
		// calculate the Pluecker coordinates of the ray
		PlueckerCoordinates r = new PlueckerCoordinates(ray.getP(), Vector3D.sum(ray.getP(), ray.getD()));

		// for all edges of type 0
		for(int i=0; i<getEIMax(0); i++)
			for(int j=0; j<getEJMax(0); j++)
			{
				// side of the edge on which the ray passes = sign of the following expression
				double side = PlueckerCoordinates.side(r, e[0][i][j]);
				
				// corresponding triangle status
				TriangleStatus thisTriangleStatus = (side>0)?(TriangleStatus.PLUECKER_POSITIVE):(TriangleStatus.PLUECKER_NEGATIVE); 
								
				// set the status of the neighbouring triangles accordingly
				if(i<getVIMax()-1) triangleStatus[0][i][j] = thisTriangleStatus;	// edge (i,j) of type 0 is an edge of triangle (i,j) of type 0...
				if(i>0) triangleStatus[1][i-1][j] = thisTriangleStatus;	// ... and of triangle (i-1, j) of type 1
			}
		
		// for all edges of type 2;
		// it makes some sense for these to come next as it is relatively easy to deal with the fact that the type-1 triangle sometimes does not exist for this edge 
		for(int i=0; i<getEIMax(2); i++)
			for(int j=0; j<getEJMax(2); j++)
			{
				// side of the edge on which the ray passes = sign of the following expression
				double side = PlueckerCoordinates.side(r, e[2][i][j]);
				
				// corresponding triangle status
				TriangleStatus thisTriangleStatus = (side>0)?(TriangleStatus.PLUECKER_POSITIVE):(TriangleStatus.PLUECKER_NEGATIVE); 
								
				// set the status of the neighbouring triangles accordingly
				if(j<getVJMax()-1) if(triangleStatus[0][i][j] != thisTriangleStatus) triangleStatus[0][i][j] = TriangleStatus.ELIMINATED;	// edge (i,j) of type 2 is an edge of triangle (i,j) of type 0...
				if(j>0) if(triangleStatus[1][i][j-1] != thisTriangleStatus) triangleStatus[1][i][j-1] = TriangleStatus.ELIMINATED;	// ... and of triangle (i,j-1) of type 1
			}
		
		double factorToClosestIntersection = MyMath.HUGE;
		Vector3D normalAtClosestIntersection = null;
				
		// for all edges of type 1
		// it makes sense for this to come last as it is hardest to deal with there being no adjacent triangles of type 0 or 1 for the last edge type to be dealt with,
		// and for edges of type 1 there are always adjacent triangles of types 0 and 1
		for(int i=0; i<getEIMax(1); i++)
			for(int j=0; j<getEJMax(1); j++)
			//	if((i<getVIMax()-1) && (j<getVJMax()-1) && ((!showOnlyOneTriangle) || ((i==oneTriangleI) && (j==oneTriangleJ))))
			{
				// side of the edge on which the ray passes = sign of the following expression
				double side = PlueckerCoordinates.side(r, e[1][i][j]);
				
				// corresponding triangle status
				TriangleStatus thisTriangleStatus = (side>0)?(TriangleStatus.PLUECKER_POSITIVE):(TriangleStatus.PLUECKER_NEGATIVE); 
								
				for(int triangleType=0; triangleType<2; triangleType++)
				if(triangleStatus[triangleType][i][j] == thisTriangleStatus)
				{
					// the ray intersects triangle (i, j) of type triangleType
					Vector3D normal = getOutwardsSurfaceNormal(triangleType, i, j);
						
					// calculate the factor by which the ray has to be extended to the intersection
					double factor = Geometry.getFactorToLinePlaneIntersection(
								ray.getP(),	// pointOnLine
								ray.getD(),	// directionOfLine
								getVertexForTriangle(triangleType, i, j, 0),	// pointOnPlane, here vertex #0 of the intersected triangle
								normal	// normalToPlane
							);
						
					// is this point closer than the previous closest point?
					if((factor > 0) && (factor < factorToClosestIntersection))
					{
						// the current intersection is the new closest intersection
						factorToClosestIntersection = factor;
						normalAtClosestIntersection = normal;
					}
				}
			}
				
		if(factorToClosestIntersection == MyMath.HUGE)
		{
			// no intersection
			return RaySceneObjectIntersection.NO_INTERSECTION;
		}
		
		// return the closest intersection
		Ray rayAtIntersectionPoint = ray.getAdvancedRay(factorToClosestIntersection);
		
		return new RaySceneObjectIntersection(
				new Vector3DWithBonusVector3D(rayAtIntersectionPoint.getP(), normalAtClosestIntersection),
				this,
				rayAtIntersectionPoint.getT(),
				ray,
				normalAtClosestIntersection
			);
	}

	/**
	 * Is the point p "inside" the surface?
	 * @param p
	 * @return true if p is "inside" the surface, false otherwise
	 * @see optics.raytrace.core.SceneObject#insideObject(math.Vector3D)
	 */
	@Override
	public boolean insideObject(Vector3D p)
	{
		// find the intersection between a ray from p to a point on the surface (that ideally doesn't lie on an edge)...
		Vector3D direction = Vector3D.difference(getPointOnSurface(), p);
		
		RaySceneObjectIntersection intersection = getClosestRayIntersection(new Ray(
				p,	// position on ray
				direction,
				0	// time when ray is at p
			));

		// calculate the scalar product of the ray direction and the outwards normal
		if(intersection == RaySceneObjectIntersection.NO_INTERSECTION)
		{
			// there is no intersection; problem!
			(new RayTraceException("No intersection between ray and triangulated surface when there should be one.")).printStackTrace();
			System.exit(-1);
		}

		// whether or not p is inside i1 depends on the scalar product of the ray direction and the outwards-facing normal
		return Vector3D.scalarProduct(
					direction,
					intersection.getNormalisedOutwardsSurfaceNormal()
				) < 0;
	}

	
	// SceneObjectPrimitive methods

	/* (non-Javadoc)
	 * @see optics.raytrace.sceneObjects.SceneObjectPrimitive#getNormalisedSurfaceNormal(math.Vector3D)
	 */
	@Override
	public Vector3D getNormalisedOutwardsSurfaceNormal(Vector3D p)
	{
		// hope that p is a Vector3DWithBonusVector3D, with the other vector being the outwards-facing surface normal
		return ((Vector3DWithBonusVector3D)p).getBonusVector();
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.core.SceneObjectPrimitive#transform(optics.raytrace.core.Transformation)
	 */
	@Override
	public TriangulatedSurface transform(Transformation t)
	{
		// create an array of transformed surface positions
		Vector3D[][] pTransformed = new Vector3D[getVIMax()][getVJMax()];
		for(int i=0; i<getVIMax(); i++)
			for(int j=0; j<getVJMax(); j++)
				pTransformed[i][j] = t.transformPosition(v[i][j]);
		
		return new TriangulatedSurface(
				description,
				pTransformed,
				isInvertSurface(),
				getSurfaceProperty(),
				getParent(),
				getStudio()
		);
	}
	
	

	/* (non-Javadoc)
	 * @see optics.raytrace.core.SceneObjectClass#toString()
	 */
	@Override
	public String toString() {
		return "<TriangulatedSurface>..." +
		"</TriangulatedSurface>\n";
	}
	
	/* (non-Javadoc)
	 * @see optics.raytrace.core.SceneObject#getType()
	 */
	@Override
	public String getType()
	{
		return "Triangulated surface";
	}
}
