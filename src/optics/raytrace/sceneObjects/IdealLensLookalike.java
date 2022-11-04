package optics.raytrace.sceneObjects;

import java.util.ArrayList;

import math.Geometry;
import math.MathException;
import math.Vector3D;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.SceneObjectPrimitive;
import optics.raytrace.core.Studio;
import optics.raytrace.core.SurfaceProperty;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectPrimitiveIntersection;
import optics.raytrace.surfaces.RefractiveSimple;

/**
 * @author johannes
 * A refractive element that changes any object-space ray that passes through the position p1 into the same 
 * image-space ray through position p2 that an ideal lens that images p1 into p2 and which lies in a plane
 * through pI, with span vectors uI and vI, would change the ray into.
 * 
 * When seen from either p1 or p2, this element should distort the scene seen through it identically to that ideal lens.
 */
public class IdealLensLookalike extends SceneObjectPrimitiveIntersection
{
	private static final long serialVersionUID = -5373366715051869579L;

	/**
	 * position which is imaged into p2 by the element
	 */
	private Vector3D p1;
	
	/**
	 * position which is imaged into p1 by the element
	 */
	private Vector3D p2;
	
	private double d1;
	private double d2;
	private int i0;
	private int j0;
	
	SurfaceProperty surfaceProperty1, surfaceProperty2, surfacePropertySides;
	
	/**
	 * n_inside / n_outside, i.e. ratio of refractive indices of the lens
	 */
	private double n;
	
	/**
	 * array of positions on the ideal lens
	 */
	private Vector3D[][] pointsOnIdealLens;
	
	public enum InitOrder {
		ITHENJ("First i, then j"), JTHENI("First j, then i"), AVERAGED("Averaged");
		
		private String description;
		private InitOrder(String description)
		{
			this.description = description;
		}
		
		@Override
		public String toString() {return description;}
	};
	private InitOrder initOrder;


	
	// useful to have
	
	TriangulatedSurface surface1, surface2, side1, side2, side3, side4;

	
	/**
	 * @param description
	 * @param p1
	 * @param p2
	 * @param d1
	 * @param d2
	 * @param i0
	 * @param j0
	 * @param surfaceProperty1
	 * @param surfaceProperty2
	 * @param surfacePropertySides	surface property of sides; if null, the sides are not shown
	 * @param n
	 * @param pointsOnIdealLens
	 * @param initOrder
	 * @param parent
	 * @param studio
	 */
	public IdealLensLookalike(
			String description,
			Vector3D p1,
			Vector3D p2,
			double d1,
			double d2,
			int i0,
			int j0,
			SurfaceProperty surfaceProperty1,
			SurfaceProperty surfaceProperty2,
			SurfaceProperty surfacePropertySides,
			double n,
			Vector3D[][] pointsOnIdealLens,
			InitOrder initOrder,
			SceneObject parent,
			Studio studio
		)
	{
		super(description, parent, studio);
		
		setP1(p1);
		setP2(p2);
		setD1(d1);
		setD2(d2);
		setI0(i0);
		setJ0(j0);
		setSurfaceProperty1(surfaceProperty1);
		setSurfaceProperty2(surfaceProperty2);
		setSurfacePropertySides(surfacePropertySides);
		setN(n);
		setPointsOnIdealLens(pointsOnIdealLens);
		setInitOrder(initOrder);
	
		initialise();
//		switch(initOrder)
//		{
//		case ITHENJ:
//			initialise(0);
//			break;
//		case JTHENI:
//			initialise(1);
//			break;
//		case :
//		default:
//			// first initialise i first, then j
//			initialise(0);
//			// copy the result
//			Vector3D[][] v10 = new Vector3D[pointsOnIdealLens.length][pointsOnIdealLens[0].length];
//			Vector3D[][] v20 = new Vector3D[pointsOnIdealLens.length][pointsOnIdealLens[0].length];
//			for(int i=0; i<pointsOnIdealLens.length; i++)
//				for(int j=0; j<pointsOnIdealLens[0].length; j++)
//				{
//					v10[i][j] = surface1.getV()[i][j];
//					v20[i][j] = surface1.getV()[i][j];
//				}
//			// then initialise j first, then i
//			initialise(1);
//		}
	}
	
	
	
	// setters & getters

	public Vector3D getP1() {
		return p1;
	}

	public void setP1(Vector3D p1) {
		this.p1 = p1;
	}

	public Vector3D getP2() {
		return p2;
	}

	public void setP2(Vector3D p2) {
		this.p2 = p2;
	}

	public double getD1() {
		return d1;
	}

	public void setD1(double d1) {
		this.d1 = d1;
	}

	public double getD2() {
		return d2;
	}

	public void setD2(double d2) {
		this.d2 = d2;
	}

	public int getI0() {
		return i0;
	}

	public void setI0(int i0) {
		this.i0 = i0;
	}

	public int getJ0() {
		return j0;
	}

	public void setJ0(int j0) {
		this.j0 = j0;
	}

	public SurfaceProperty getSurfaceProperty1() {
		return surfaceProperty1;
	}

	public void setSurfaceProperty1(SurfaceProperty surfaceProperty1) {
		this.surfaceProperty1 = surfaceProperty1;
	}

	public SurfaceProperty getSurfaceProperty2() {
		return surfaceProperty2;
	}

	public void setSurfaceProperty2(SurfaceProperty surfaceProperty2) {
		this.surfaceProperty2 = surfaceProperty2;
	}

	public SurfaceProperty getSurfacePropertySides() {
		return surfacePropertySides;
	}

	public void setSurfacePropertySides(SurfaceProperty surfacePropertySides) {
		this.surfacePropertySides = surfacePropertySides;
	}

	public double getN() {
		return n;
	}

	public void setN(double n) {
		this.n = n;
	}

	public Vector3D[][] getPointsOnIdealLens() {
		return pointsOnIdealLens;
	}

	public void setPointsOnIdealLens(Vector3D[][] pointsOnIdealLens) {
		this.pointsOnIdealLens = pointsOnIdealLens;
	}

	public TriangulatedSurface getSurface1() {
		return surface1;
	}

	public TriangulatedSurface getSurface2() {
		return surface2;
	}

	public InitOrder getInitOrder() {
		return initOrder;
	}

	public void setInitOrder(InitOrder initOrder) {
		this.initOrder = initOrder;
	}



	public void initialise()
	{
		// initialise the lists of positive and negative scene-object primitives
		positiveSceneObjectPrimitives = new ArrayList<SceneObjectPrimitive>(2);
		negativeSceneObjectPrimitives  = new ArrayList<SceneObjectPrimitive>();
		
		// TODO add aperture shape here?
		invisiblePositiveSceneObjectPrimitives = new ArrayList<SceneObjectPrimitive>();
		invisibleNegativeSceneObjectPrimitives  = new ArrayList<SceneObjectPrimitive>();

		// calculate array of vertices on both surfaces
		
		// create space for vertices on surface 1...
		Vector3D[][] v1 = new Vector3D[pointsOnIdealLens.length][pointsOnIdealLens[0].length];
		// ... and on surface 2
		Vector3D[][] v2 = new Vector3D[pointsOnIdealLens.length][pointsOnIdealLens[0].length];
		
		// initialise all these vectors to null
		for(int i=0; i<pointsOnIdealLens.length; i++)
			for(int j=0; j<pointsOnIdealLens[0].length; j++)
			{
				v1[i][j] = null;
				v2[i][j] = null;
			}
		
		// initialise the first pair of points
		// the initial point on surface 1 lies between the initial point on the ideal lens and P1,
		// a distance d1 from the initial point on the ideal lens
		v1[i0][j0] = Vector3D.sum(
				pointsOnIdealLens[i0][j0],
				Vector3D.difference(p1, pointsOnIdealLens[i0][j0]).getWithLength(d1)
			);
		// the initial point on surface 2 lies between the initial point on the ideal lens and P2,
		// a distance d1 from the initial point on the ideal lens
		v2[i0][j0] = Vector3D.sum(
				pointsOnIdealLens[i0][j0],
				Vector3D.difference(p2, pointsOnIdealLens[i0][j0]).getWithLength(d2)
			);
		
		// now calculate the remaining points on the surfaces
		if(initOrder != InitOrder.ITHENJ)
		{
			// initOrder is either JTHENI or AVERAGED

			// calculate the points with i=i0, j>j0
			for(int j=j0+1; j<pointsOnIdealLens[0].length; j++) calculateSurfacePoint(i0, j, i0, j-1, v1, v2);
			// calculate the points with i=i0, j<j0
			for(int j=j0-1; j>=0; j--) calculateSurfacePoint(i0, j, i0, j+1, v1, v2);


			// calculate the points with i>i0
			for(int i=i0+1; i<pointsOnIdealLens.length; i++)
			{
				// first calculate the point with i>i0, j=j0, ...
				calculateSurfacePoint(i, j0, i-1, j0, v1, v2);
				// ... then calculate the points for the same i value with j>j0...
				for(int j=j0+1; j<pointsOnIdealLens[0].length; j++) calculateSurfacePoint(i, j, i, j-1, v1, v2);
				// ... and the points for the same i value with j<j0
				for(int j=j0-1; j>=0; j--) calculateSurfacePoint(i, j, i, j+1, v1, v2);
			}

			// ... and those with i<i0
			for(int i=i0-1; i>=0; i--)
			{
				calculateSurfacePoint(i, j0, i+1, j0, v1, v2);
				// calculate the points with j>j0
				for(int j=j0+1; j<pointsOnIdealLens[0].length; j++) calculateSurfacePoint(i, j, i, j-1, v1, v2);
				// calculate the points with j<j0
				for(int j=j0-1; j>=0; j--) calculateSurfacePoint(i, j, i, j+1, v1, v2);
			}
		}
		
		Vector3D[][] v1JTHENI = null;
		Vector3D[][] v2JTHENI = null;
		
		if(initOrder == InitOrder.AVERAGED)
		{
			v1JTHENI = new Vector3D[pointsOnIdealLens.length][pointsOnIdealLens[0].length];
			v2JTHENI = new Vector3D[pointsOnIdealLens.length][pointsOnIdealLens[0].length];
			
			// initialise all these vectors to null
			for(int i=0; i<pointsOnIdealLens.length; i++)
				for(int j=0; j<pointsOnIdealLens[0].length; j++)
				{
					v1JTHENI[i][j] = v1[i][j];
					v2JTHENI[i][j] = v1[i][j];
				}
		}
		
		if(initOrder != InitOrder.JTHENI)
		{
			// initOrder is either ITHENJ or AVERAGED
			
			// calculate the points with i>i0, j=j0
			for(int i=i0+1; i<pointsOnIdealLens.length; i++) calculateSurfacePoint(i, j0, i-1, j0, v1, v2);
			// calculate the points with i<i0, j=j0
			for(int i=i0-1; i>=0; i--) calculateSurfacePoint(i, j0, i+1, j0, v1, v2);


			// calculate the points with j>j0
			for(int j=j0+1; j<pointsOnIdealLens[0].length; j++)
			{
				// first calculate the point with j>j0, i=i0, ...
				calculateSurfacePoint(i0, j, i0, j-1, v1, v2);
				// ... then calculate the points for the same j value with i>i0...
				for(int i=i0+1; i<pointsOnIdealLens.length; i++) calculateSurfacePoint(i, j, i-1, j, v1, v2);
				// ... and the points for the same j value with i<i0
				for(int i=i0-1; i>=0; i--) calculateSurfacePoint(i, j, i+1, j, v1, v2);
			}

			// ... and those with j<j0
			for(int j=j0-1; j>=0; j--)
			{
				calculateSurfacePoint(i0, j, i0, j+1, v1, v2);
				// calculate the points with i>i0
				for(int i=i0+1; i<pointsOnIdealLens.length; i++) calculateSurfacePoint(i, j, i-1, j, v1, v2);
				// calculate the points with i<i0
				for(int i=i0-1; i>=0; i--) calculateSurfacePoint(i, j, i+1, j, v1, v2);
			}
		}
		
		if(initOrder == InitOrder.AVERAGED)
		{
			// initialise all these vectors to null
			for(int i=0; i<pointsOnIdealLens.length; i++)
				for(int j=0; j<pointsOnIdealLens[0].length; j++)
				{
					v1[i][j] = Vector3D.mean(v1[i][j], v1JTHENI[i][j]);
					v2[i][j] = Vector3D.mean(v2[i][j], v2JTHENI[i][j]);
				}
		}
		
//		for(int i=0; i<pointsOnIdealLens.length; i++)
//			for(int j=0; j<pointsOnIdealLens[0].length; j++)
//			{
//				System.out.println("v1["+i+"]["+j+"] = "+v1[i][j]);
//				System.out.println("v2["+i+"]["+j+"] = "+v2[i][j]);
//			}
		
		surface1 = new TriangulatedSurface(
				"Surface 1",	// description
				v1,
				true,	// not inverted
				surfaceProperty1,
				this,	// parent
				getStudio()
			);
		positiveSceneObjectPrimitives.add(surface1);
		
		surface2 = new TriangulatedSurface(
				"Surface 2",	// description
				v2,
				false,	// inverted
				surfaceProperty2,
				this,	// parent
				getStudio()
			);
		positiveSceneObjectPrimitives.add(surface2);
		
		if(surfacePropertySides != null)
		{
			// the side surfaces
			Vector3D[][] vs1 = new Vector3D[pointsOnIdealLens.length][2];
			Vector3D[][] vs3 = new Vector3D[pointsOnIdealLens.length][2];
			for(int i=0; i<pointsOnIdealLens.length; i++)
			{
				vs1[i][0] = v1[i][0];
				vs1[i][1] = v2[i][0];
				vs3[i][0] = v2[i][pointsOnIdealLens[0].length-1];
				vs3[i][1] = v1[i][pointsOnIdealLens[0].length-1];
			}

			Vector3D[][] vs2 = new Vector3D[2][pointsOnIdealLens[0].length];
			Vector3D[][] vs4 = new Vector3D[2][pointsOnIdealLens[0].length];
			for(int j=0; j<pointsOnIdealLens[0].length; j++)
			{
				vs2[0][j] = v1[0][j];
				vs2[1][j] = v2[0][j];
				vs4[0][j] = v2[pointsOnIdealLens.length-1][j];
				vs4[1][j] = v1[pointsOnIdealLens.length-1][j];
			}

			side1 = new TriangulatedSurface(
					"Side 1",	// description
					vs1,
					false,	// not inverted
					surfacePropertySides,
					this,	// parent
					getStudio()
					);
			positiveSceneObjectPrimitives.add(side1);

			side2 = new TriangulatedSurface(
					"Side 2",	// description
					vs2,
					false,	// not inverted
					surfacePropertySides,
					this,	// parent
					getStudio()
					);
			positiveSceneObjectPrimitives.add(side2);

			side3 = new TriangulatedSurface(
					"Side 3",	// description
					vs3,
					false,	// not inverted
					surfacePropertySides,
					this,	// parent
					getStudio()
					);
			positiveSceneObjectPrimitives.add(side3);

			side4 = new TriangulatedSurface(
					"Side 4",	// description
					vs4,
					false,	// not inverted
					surfacePropertySides,
					this,	// parent
					getStudio()
					);
			positiveSceneObjectPrimitives.add(side4);
		}
	}
		
	
	/**
	 * Calculate the surface points v1[i][j] (on surface 1) and v2[i][j] (on surface 2) from the points
	 * v1[iNeighbour][jNeighbour] and v2[iNeighbour][jNeighbour], both of which must be set when this method is called
	 * @param i
	 * @param j
	 * @param iNeighbour
	 * @param jNeighbour
	 * @param v1
	 * @param v2
	 */
	private void calculateSurfacePoint(int i, int j, int iNeighbour, int jNeighbour, Vector3D[][] v1, Vector3D[][] v2)
	{
		// calculate the light-ray directions for the (initialised) neighbour
		
		// light ray direction between P1 and surface 1
		Vector3D normalisedDirectionOutsideSide1 = Vector3D.difference(v1[iNeighbour][jNeighbour], p1).getNormalised();
		// light ray direction between surface 1 and surface 2
		Vector3D normalisedDirectionInsideLens = Vector3D.difference(v2[iNeighbour][jNeighbour], v1[iNeighbour][jNeighbour]).getNormalised();
		// light ray direction between P1 and surface 2
		Vector3D normalisedDirectionOutsideSide2 = Vector3D.difference(p2, v2[iNeighbour][jNeighbour]).getNormalised();

		// calculate the surface normals for element (iNeighbour, jNeighbour) such that the light-ray directions
		// get refracted into each other
		
		// normal to surface 1 at v1[iNeighbour][jNeighbour]
		Vector3D n1 = RefractiveSimple.getNormal(
				normalisedDirectionOutsideSide1,	// normalisedDirectionIn
				normalisedDirectionInsideLens,	// normalisedDirectionOut
				1.0,	// nIn
				n	// nOut
			);

		// normal to surface 2 at v2[iNeighbour][jNeighbour]
		Vector3D n2 = RefractiveSimple.getNormal(
				normalisedDirectionInsideLens,	// normalisedDirectionIn
				normalisedDirectionOutsideSide2,	// normalisedDirectionOut
				n,	// nIn
				1.0	// nOut
			);
		
		try {
			// point v1(i, j) is then the intersection between the plane through v1(iNeighbour, jNeighbour)
			// with normal n1 and the line from p1 to pointsOnIdealLens(i, j)
			v1[i][j] = Geometry.linePlaneIntersection(
							p1,	// pointOnLine
							Vector3D.difference(pointsOnIdealLens[i][j], p1),	// directionOfLine
							v1[iNeighbour][jNeighbour],	// pointOnPlane
							n1	// normalToPlane
							);

			// point v2(i, j) is then the intersection between the plane through v2(iNeighbour, jNeighbour)
			// with normal n2 and the line from p2 to pointsOnIdealLens(i, j)
			v2[i][j] = Geometry.linePlaneIntersection(
							p2,	// pointOnLine
							Vector3D.difference(pointsOnIdealLens[i][j], p2),	// directionOfLine
							v2[iNeighbour][jNeighbour],	// pointOnPlane
							n2	// normalToPlane
							);
		} catch (MathException e) {
			e.printStackTrace();
		}
	}
	
	
//	/**
//	 * Iteratively, improve the surfaces by calculating the normals at the centroid of each triangle
//	 * @param v1
//	 * @param v2
//	 */
//	private void iterate()
//	{
//		// go through all the triangles...
////		surface1.getCentroid(int triangleType, int i, int j)
////		for(int i=0; i<pointsOnIdealLens.length; i++)
////			for(int j=0; j<pointsOnIdealLens[0].length; j++)
//				
//	}
	
}
