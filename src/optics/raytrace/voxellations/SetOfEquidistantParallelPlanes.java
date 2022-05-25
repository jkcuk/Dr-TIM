package optics.raytrace.voxellations;

import optics.raytrace.core.SurfaceProperty;
import optics.raytrace.sceneObjects.Plane;
import math.Vector3D;

/**
 * @author johannes
 * 
 * A set of parallel, equidistant, planes.  The spaces between the planes define the "voxels".
 * For example, for three such sets in which the planes in each set are perpendicular to
 * the planes in both other sets would enclose cuboid voxels.
 * 
 * Each set of planes is defined by a point <b>p</b> on the plane with index 0, the normalised normal <b>n</b>
 * to the planes, and the separation <i>s</i> between planes with indices <i>n</i> and <i>n</i>+1.
 * 
 * This is useful for dealing with "voxellated" volumes.
 * An efficient way to trace through such a voxellated volume could be the following:
 * If the starting point of the ray is inside the volume, define a "scene" that consists of
 * the two closest planes in each set of planes (unless the starting point is on a plane, in which case take the two
 * next closest planes) and the volume's boundary surface, and trace to the nearest intersection with this scene.
 */
public class SetOfEquidistantParallelPlanes extends SetOfSurfaces
{
	private static final long serialVersionUID = 6300830909695912140L;

	/**
	 * Point on plane with index 0
	 */
	protected Vector3D p;
	
	/**
	 * Normalised normal to the planes, pointing in the direction of more positive plane indices
	 */
	protected Vector3D n;
	
	/**
	 * Separation between the planes
	 */
	protected double s;
	
	// constructor
	
	/**
	 * @param pointOnPlane0	point on the 0th plane
	 * @param normal	vector that is perpendicular to the planes
	 * @param separation	separation between neighbouring planes
	 */
	public SetOfEquidistantParallelPlanes(Vector3D pointOnPlane0, Vector3D normal, double separation)
	{
		super();
		
		setP(pointOnPlane0);
		setN(normal);
		setS(separation);
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
		return new Plane(
				"Plane #"+i,	// description
				Vector3D.sum(p, n.getProductWith(i*s*outwardsNormalOrientation.getSign())),	// point on plane
				n,	// normal
				surfaceProperty,	// surface property
				null,	// parent
				null	// studio
			);
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
//		System.out.println("position = "+position+
//				", p = "+ p +
//				", n = " + n +
//				", s = " + s +
//				", (position-p)="+Vector3D.difference(position, p)+
//				", (position-p).n = "+Vector3D.scalarProduct(Vector3D.difference(position, p), n) +
//				", (position-p).n/s = "+Vector3D.scalarProduct(Vector3D.difference(position, p), n) / s);
		return Vector3D.scalarProduct(Vector3D.difference(position, p), n) / s;
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

	
//	/**
//	 * @param r	position relative to the planes (e.g. 1.5 = half-way between planes 1 and 2)
//	 * @return	plane #floor(r)
//	 */
//	@Override
//	public Plane getPreviousSurface(double r)
//	{
//		double f = Math.floor(r);
//		
//		// does the relative position r correspond to a position on a plane?
//		if(r-f < MyMath.EPSILON)
//		{
//			// r corresponds to a position on a plane; return the previous plane
//			return getSurface(f-1);
//		}
//		else
//		{
//			// r corresponds to a position between two planes; return the "previous" one
//			return getSurface(f);
//		}
//	}
//
//	/**
//	 * @param r	position relative to the planes (e.g. 1.5 = half-way between planes 1 and 2)
//	 * @return	plane #ceil(r)
//	 */
//	@Override
//	public Plane getNextSurface(double r)
//	{
//		double c = Math.ceil(r);
//		
//		// does the relative position r correspond to a position on a plane?
//		if(c-r < MyMath.EPSILON)
//		{
//			// r corresponds to a position on a plane; return the next plane
//			return getSurface(c+1);
//		}
//		else
//		{
//			// r corresponds to a position between two planes; return the "next" one
//			return getSurface(c);
//		}
//	}

	// setters & getters
	
	public Vector3D getP() {
		return p;
	}

	public void setP(Vector3D p) {
		this.p = p;
	}

	public Vector3D getN() {
		return n;
	}

	/**
	 * Normalise <b>n</b> and store it
	 * @param n
	 */
	public void setN(Vector3D n)
	{
		this.n = n.getNormalised();
	}

	public double getS() {
		return s;
	}

	public void setS(double s) {
		this.s = s;
	}
}
