package optics.raytrace.voxellations; // Eventual package optics.raytrace.surfaces

import math.Vector3D;
import optics.raytrace.core.SceneObject;
import optics.raytrace.sceneObjects.ParametrisedCuboid;

/**
 * A set of nested concentric cubes similar to SetOfConcentricSpheres.class
 * 
 * Each cube is characterised by its "radius", i.e. half the side length.
 * 
 * Recall that the 'faces' and the 'surface' of a polyhedron are different things. In particular, the surface
 * is the set of all the polyhedral faces. This is so as a SetOfIntersectingSurfaces is still a Surface, of
 * the composite object, which is 'composite' of surface primitives (e.g planes, spheres).
 * @author E Orife, conception by J Courtial
 */
public class SetOfConcentricCubes extends SetOfSurfaces
{
	private static final long serialVersionUID = 6100967959040931013L;

	protected Vector3D centre; // common centre of all the cubes

	protected double deltaRadius, // difference in the half-sidelength between neighbouring nested cubes
	                 radiusOfCube0;	// half-sidelength of cube with index 0

	// constructor(s)

	/**
	 * @param centre	common centre of all cubes
	 * @param radiusOfCube0	half-sidelength of cube number zero
	 * @param deltaRadius	separation between neighbouring cubes
	 */
	public SetOfConcentricCubes(Vector3D centre, double radiusOfCube0, double deltaRadius)
	{
		super();
		setCentre(centre);
		setRadiusOfCube0(radiusOfCube0);
		setDeltaRadius(deltaRadius);
	}

	public double getRadius(Vector3D position)
	{
		Vector3D cp = Vector3D.difference(position, getCentre());

		// the "radius" of the cube through the position
		return Math.max(
				Math.max(Math.abs(cp.x), Math.abs(cp.y)),
				Math.abs(cp.z)
			);
	}

	/**
	 * @param index
	 * @return	the "radius" (= half-sidelength) of the cube with given index
	 */
	public double index2Radius(double index)
	{
		return radiusOfCube0 + index * deltaRadius;
	}
	
	public double radius2Index(double radius)
	{
		return (radius - radiusOfCube0) / deltaRadius;
	}

	@Override
	public double getSurfaceIndex(Vector3D position) {
		// from the "radius", subtract the "radius" of cube 0 and divide by the radius difference between
		// neighbouring nested cubes to get the "index"
		return (getRadius(position) - getRadiusOfCube0()) / getDeltaRadius();
	}

	/**
	 * Return the cube with index <i>i</i>.
	 * The object returned is intended to be used for finding the intersection point between the cube and 
	 * a ray.
	 * It is <i>not</i> linked into a scene or studio, and nor does it have a surface property.
	 * @param i
	 * @return	if <i>i</i> is an integer, the cube with index <i>i</i>, otherwise a cube with an in-between sidelength; if the sidelength < 0, return null
	 * @throws	Exception if the index r corresponds to a cube of negative side
	 */
	@Override
	public SceneObject getSurface(double i)
	// throws Exception
	{
		// calculate the side length of the cube with index i
		double d = 2*(radiusOfCube0 + i * deltaRadius);
		if(d <= 0.0) return null;
		
		return new ParametrisedCuboid(
				"Cube #"+i,	// description
				d, d, d, // width, height, depth
				centre,	// centre
				null,	// surface property
				null,	// parent
				null	// studio
			);
	}
	
	
	//
	// setters and getters
	//
	
	/**
	 * @return the centre
	 */
	public Vector3D getCentre() {
		return centre;
	}

	/**
	 * @param centre the centre to set
	 */
	public void setCentre(Vector3D centre) {
		this.centre = centre;
	}

	/**
	 * @return the separation between neighbouring cube surfaces
	 */
	public double getDeltaRadius() {
		return deltaRadius;
	}

	/**
	 * @param deltaRadius the separation in half-sidelength between neighbouring nested cubes
	 */
	public void setDeltaRadius(double deltaRadius) {
		this.deltaRadius = deltaRadius;
	}

	/**
	 * @return the half-sidelength of the 0th cube
	 */
	public double getRadiusOfCube0() {
		return radiusOfCube0;
	}

	/**
	 * @param radiusOfCube0 the half-sidelength of cube number 0
	 */
	public void setRadiusOfCube0(double radiusOfCube0) {
		this.radiusOfCube0 = radiusOfCube0;
	}



}