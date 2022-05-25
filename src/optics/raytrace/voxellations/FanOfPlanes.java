package optics.raytrace.voxellations;

import optics.raytrace.core.SurfaceProperty;
import optics.raytrace.sceneObjects.Plane;
import math.Geometry;
import math.Vector3D;

/**
 * @author johannes
 * 
 * A set of planes that intersect along a common line and which intersect another line (normally perpendicular to the first line), 
 * the intersection-point line, at equidistant intervals.
 * The spaces between the planes define the "voxels".
 * We need this to create refractive Gabor super-er lenses.
 * 
 * Each set of planes is defined by two points, <b>c1</b> and <b>c2</b>, on the common line along which all planes intersect,
 * and the points <b>p0</b> and <b>p1</b>, where the planes with indices 0 and 1, respectively intersect the intersection-point line.
 * Plane <i>n</i> then intersects the intersection-point line at position <b>p0</b> + <i>n</i> (<b>p1</b> - <b>p0</b>).
 */
public class FanOfPlanes extends SetOfSurfaces
{
	private static final long serialVersionUID = -6538250559990353903L;

	/**
	 * First point on line through all planes
	 */
	protected Vector3D c1;

	/**
	 * Second point on line through all planes
	 */
	protected Vector3D c2;

	/**
	 * Third point through plane #0
	 */
	protected Vector3D p0;
	
	/**
	 * Third point through plane #1
	 */
	protected Vector3D p1;
	
	
	// constructor
	
	public FanOfPlanes(Vector3D c1, Vector3D c2, Vector3D p0, Vector3D p1)
	{
		super();
		
		this.c1 = c1;
		this.c2 = c2;
		this.p0 = p0;
		this.p1 = p1;
	}
	
	// the methods that make this class useful
	
	/**
	 * Return the plane with index <i>i</i>.
	 * The object returned is intended to be used for finding the intersection point between the plane and a ray.
	 * It is <i>not</i> linked into a scene or studio, and nor does it have a surface property.
	 * @param i
	 * @return	if <i>i</i> is an integer, the plane with index <i>i</i>, otherwise a plane at a position corresponding to the index <i>i</i>
	 */
	@Override
	public Plane getSurface(double i, OutwardsNormalOrientation outwardsNormalOrientation, SurfaceProperty surfaceProperty)
	{
		Vector3D p0top1 = Vector3D.difference(p1, p0);
		Vector3D normal = Plane.getNormalToPlaneThroughPoints(c1, c2, Vector3D.sum(p0, p0top1.getProductWith(i)));
		// check that the normal is pointing in the direction defined by outwardsNormalOrientation
		if(Math.signum(Vector3D.scalarProduct(normal, p0top1)) != outwardsNormalOrientation.getSign())
		{
			normal = normal.getReverse();
		}
		return new Plane(
				"Plane #"+i,	// description 
				c1,	// pointOnPlane
				normal,	// normal
				surfaceProperty,	// surface property
				null,	// parent
				null	// studio
			);
//		return new Plane(
//				"Plane #"+i,	// description
//				c1,	// point1OnPlane
//				c2,	// point2OnPlane
//				Vector3D.sum(p0, Vector3D.difference(p1, p0).getProductWith(i)),	// point3OnPlane
//				null,	// surface property
//				null,	// parent
//				null	// studio
//			);
	}
	
	/**
	 * Returns the "plane index" of the position.
	 * If the position lies on a plane, then the index is the number of that plane.
	 * If the position lies half-way between planes 3 and 4, then the index is 3.5.
	 * @param position
	 * @return	"plane index" of position
	 **/
	@Override
	public double getSurfaceIndex(Vector3D position)
	{
		// the position defines a plane, which passes also through c1 and c2;
		// that plane intersects the intersection-point line at position p;
		// solve p = p0 + i (p1 - p0) for i to find the index
		return Geometry.getFactorToLinePlaneIntersection(
				p0,	// pointOnLine
				Vector3D.difference(p1, p0),	// directionOfLine,
				c1,	// pointOnPlane,
				Plane.getNormalToPlaneThroughPoints(c1, c2, position)	// normalToPlane
			);
	}
	
	/**
	 * The voxel with index <i>i</i> lies between the planes with indices <i>i</i> and <i>i</i>+1
	 * @param position
	 * @return	voxel indices
	 */
	@Override
	public int getVoxelIndex(Vector3D position)
	{
		// System.out.println("position = "+position+", getPlaneIndex= " + getPlaneIndex(position));
		return (int)(Math.floor(getSurfaceIndex(position)+1));
	}

	

	// setters & getters
	
	public Vector3D getC1() {
		return c1;
	}

	public void setC1(Vector3D c1) {
		this.c1 = c1;
	}

	public Vector3D getC2() {
		return c2;
	}

	public void setC2(Vector3D c2) {
		this.c2 = c2;
	}

	public Vector3D getP0() {
		return p0;
	}

	public void setP0(Vector3D p0) {
		this.p0 = p0;
	}

	public Vector3D getP1() {
		return p1;
	}

	public void setP1(Vector3D p1) {
		this.p1 = p1;
	}

}
