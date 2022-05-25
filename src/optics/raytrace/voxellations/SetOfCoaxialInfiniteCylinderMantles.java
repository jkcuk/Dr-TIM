package optics.raytrace.voxellations;



import optics.raytrace.core.SceneObject;
import optics.raytrace.core.SurfaceProperty;
import optics.raytrace.sceneObjects.ParametrisedCylinder;
import optics.raytrace.sceneObjects.ParametrisedCylinderMantle;
import math.Vector3D;

/**
 * @author E Orife, Johannes Courtial, based on SetOfEquidistantParallelSpheres class
 * 
 * A set of concentric cylinder mantles.  
 * 
 * The spaces between the cylinder mantles define the "voxels", which are cylindrical shells.
 * Suitable for simulating problems with cylindrical symmetry.
 * 
 * Each set of cylinders is defined by the <b>pointOnAxis</b> on the common cylinder axis, 
 * the <b>axisDirection</b>, and
 * the radius of the 0th cylinder, and the (radial) separation between neighbouring nested cylinders.
 * 
 * This is useful for dealing with "voxellated" volumes.
 * An efficient way to trace through such a voxellated volume could be the following:
 * If the starting point of the ray is inside the volume, define a "scene" that consists of
 * the two closest surfaces in each set of surfaces (unless the starting point is on a surface, in which case take the two
 * next closest surfaces) and the volume's boundary surface, and trace to the nearest intersection with this scene.
 */
public class SetOfCoaxialInfiniteCylinderMantles extends SetOfSurfaces
{
	private static final long serialVersionUID = 355386273277358070L;

	/**
	 * start and end points, which lie on the common cylinder axis
	 */
	protected Vector3D pointOnAxis, axisDirection;
	
	/**
	 * Radius of cylinder with index 0
	 */
	protected double radiusOfCylinder0;
	
	/**
	 * Separation (in radius) between the cylinders
	 */
	protected double separation;

	
	// constructor
	
	/**
	 * @param pointOnAxis
	 * @param axisDirection
	 * @param radiusOfCylinder0
	 * @param separation
	 */
	public SetOfCoaxialInfiniteCylinderMantles(Vector3D pointOnAxis, Vector3D axisDirection, double radiusOfCylinder0, double separation)
	{
		super();
		
		setPointOnAxis(pointOnAxis);
		setAxisDirection(axisDirection);
		setRadiusOfCylinder0(radiusOfCylinder0);
		setSeparation(separation);
		
		System.out.println("SetOfCoaxialInfiniteCylinderMantles constructed: "+this);
	}
	
	public static SetOfCoaxialInfiniteCylinderMantles fromParametrisedCylinder(ParametrisedCylinder cylinder, double separation)
	{
		return new SetOfCoaxialInfiniteCylinderMantles(cylinder.getStartPoint(), cylinder.getNormalisedAxisDirection(), cylinder.getRadius(), separation);
	}
	
	
	// the methods that make this class useful
	
	/**
	 * Return the cylinder mantle with index <i>i</i>.
	 * The object returned is intended to be used for finding the intersection point between the cylinder mantle and a ray.
	 * It is <i>not</i> linked into a scene or studio, and nor does it have a surface property.
	 * @param i
	 * @return	if <i>i</i> is an integer, the cylinder mantle with index <i>i</i>, otherwise a cylinder mantle at a position corresponding to the index <i>i</i>
	 * @throws	Exception if the index r corresponds to a cylinder mantle of negative radius
	 */
	@Override
	public SceneObject getSurface(double i, OutwardsNormalOrientation outwardsNormalOrientation, SurfaceProperty surfaceProperty)
	// throws Exception
	{
		double r = getRadius(i);
		if(r < 0.0) return null;
		
		return new ParametrisedCylinderMantle(
				"Cylinder mantle #"+i,	// description
				Vector3D.sum(pointOnAxis, axisDirection.getProductWith(-10000)),	// start point
				Vector3D.sum(pointOnAxis, axisDirection.getProductWith(10000)),	// end point
				r*outwardsNormalOrientation.getSign(),	// radius;  -ve if the outwards normal orientation is -ve, which is represented by an inverted cylinder (with a -ve radius)
				surfaceProperty,	// surface property
				null,	// parent
				null	// studio
			);
//		return new InfiniteCylinderMantle(
//				"Cylinder mantle #"+i,	// description
//				pointOnAxis,	// point on axis
//				axisDirection,	// axis direction
//				getRadius(i),	// radius
//				null,	// surface property
//				null,	// parent
//				null	// studio
//			);
	}
	
	/**
	 * @param i
	 * @return	radius of sphere number i
	 */
	public double getRadius(double i)
	// throws Exception
	{
		// calculate the "raw" radius...
		return radiusOfCylinder0 + i*separation;
	}
	
	/**
	 * Returns the "cylinder index" of the position.
	 * If the position lies on a sphere, then the index is the number of that sphere.
	 * If the position lies half-way between spheres 3 and 4, then the index is 3.5.
	 * @param position
	 * @return	"sphere index" of position
	 **/
	@Override
	public double getSurfaceIndex(Vector3D position)
	{
		// calculate the distance from the axis of the position
		
		// vector from the cylinder start point (which is on the cylider axis) to the position
		Vector3D sp = Vector3D.difference(position, pointOnAxis);
		
		double r = Vector3D.difference(sp, axisDirection.getProductWith(Vector3D.scalarProduct(sp, axisDirection))).getLength();
		
		return (r-radiusOfCylinder0) / separation;
	}

	
	// setters & getters
	
	public Vector3D getPointOnAxis() {
		return pointOnAxis;
	}

	public void setPointOnAxis(Vector3D pointOnAxis) {
		this.pointOnAxis = pointOnAxis;
	}

	public Vector3D getAxisDirection() {
		return axisDirection;
	}

	public void setAxisDirection(Vector3D axisDirection) {
		this.axisDirection = axisDirection;
	}

	public double getRadiusOfCylinder0() {
		return radiusOfCylinder0;
	}

	public void setRadiusOfCylinder0(double radiusOfCylinder0) {
		this.radiusOfCylinder0 = radiusOfCylinder0;
	}

	public double getSeparation() {
		return separation;
	}

	public void setSeparation(double separation) {
		this.separation = separation;
	}
	
	@Override
	public String toString()
	{
		return "<SetOfCoaxialInfiniteCylinders "+
		"pointOnAxis="+pointOnAxis+
		"axisDirection="+axisDirection+
		"radiusOfCylinder0="+radiusOfCylinder0+
		"separation="+separation+
		">";
	}
}
