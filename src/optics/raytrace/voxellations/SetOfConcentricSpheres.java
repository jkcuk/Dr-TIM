package optics.raytrace.voxellations;



import optics.raytrace.core.SceneObject;
import optics.raytrace.sceneObjects.ParametrisedSphere;
import optics.raytrace.sceneObjects.Sphere;
import math.Vector3D;

/**
 * @author E Orife, Johannes Courtial, based on SetOfEquidistantParallelPlanes class
 * 
 * A set of concentric spheres.  
 * 
 * Old methods kept from SetOfEquidistantParallelPlanes for reference.
 * 
 * The spaces between the spheres define the "voxels", which are spherical shells.
 * Suitable for simulating problems with spherical symmetry.
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
public class SetOfConcentricSpheres extends SetOfSurfaces
{
	private static final long serialVersionUID = 7295714936909681707L;

	/**
	 * Common centre of all spheres
	 */
	protected Vector3D centre;

	/**
	 * Radius of sphere with index 0
	 */
	protected double radiusOfSphere0;
	
	/**
	 * Separation between the spheres
	 */
	protected double separation;

	
	// constructor
	
	/**
	 * @param centre	common centre of all spheres
	 * @param radiusOfSphere0	radius of sphere number zero
	 * @param separation	separation in radius between neighbouring spheres
	 */
	public SetOfConcentricSpheres(Vector3D centre, double radiusOfSphere0, double separation)
	{
		super();
		
		setCentre(centre);
		setRadiusOfSphere0(radiusOfSphere0);
		setSeparation(separation);
	}
	
	/**
	 * @param sphere0
	 * @param separation
	 * @return	a set of concentric spheres
	 */
	public static SetOfConcentricSpheres fromSphere(Sphere sphere0, double separation)
	{
		return new SetOfConcentricSpheres(sphere0.getCentre(),sphere0.getRadius(),separation);
	}
	
	
	// the methods that make this class useful
	
	/**
	 * Return the sphere with index <i>i</i>.
	 * The object returned is intended to be used for finding the intersection point between the sphere and a ray.
	 * It is <i>not</i> linked into a scene or studio, and nor does it have a surface property.
	 * @param i
	 * @return	if <i>i</i> is an integer, the sphere with index <i>i</i>, otherwise a sphere at a position corresponding to the index <i>i</i>
	 * @throws	Exception if the index r corresponds to a sphere of negative radius
	 */
	@Override
	public SceneObject getSurface(double i)
	{
		double r = getRadius1(i);
		if(r <= 0.0) return null;
		
		return new ParametrisedSphere(
				"Sphere #"+i+", radius="+r,	// description
				centre,	// centre
				r,	// radius
				null,	// surface property
				null,	// parent
				null	// studio
			);
	}
	
	/**
	 * @param i
	 * @return	radius of sphere number i; note that the radius can be negative, which needs to be checked!
	 */
	public double getRadius1(double i)
	// throws Exception
	{
		return radiusOfSphere0 + i*separation;
	}
	
	/**
	 * Returns the "sphere index" of the position.
	 * If the position lies on a sphere, then the index is the number of that sphere.
	 * If the position lies half-way between spheres 3 and 4, then the index is 3.5.
	 * @param position
	 * @return	"sphere index" of position
	 **/
	@Override
	public double getSurfaceIndex(Vector3D position)
	{
		// calculate the distance from the centre of the position
		double r = Vector3D.difference(position, centre).getLength();
		
		return (r-radiusOfSphere0) / separation;
	}

//	/**
//	 * The voxel with index <i>i</i> is lies between the surfaces with indices <i>i</i>-1 and <i>i</i>
//	 * @param position
//	 * @return	voxel indices
//	 */
//	@Override
//	public int getVoxelIndex(Vector3D position)
//	{
//		return (int)(Math.floor(getSurfaceIndex(position)+1));
//	}

	
	// setters & getters
	
	public Vector3D getCentre() {
		return centre;
	}

	public void setCentre(Vector3D centre) {
		this.centre = centre;
	}

	public double getRadiusOfSphere0() {
		return radiusOfSphere0;
	}

	public void setRadiusOfSphere0(double radiusOfSphere0) {
		this.radiusOfSphere0 = radiusOfSphere0;
	}

	public double getSeparation() {
		return separation;
	}

	public void setSeparation(double separation) {
		this.separation = separation;
	}
}
