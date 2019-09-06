package optics.raytrace.core;

import java.util.ArrayList;

import math.Vector3D;
import math.Vector2D;

/**
 * Parametrises the surface of the SceneObject so that patterns etc. can be applied.
 * For example, a sphere could be described by two angles.
 */
public interface ParametrisedObject 
{
	/**
	 * @return the names of the surface parameters, e.g. ("theta", "phi")
	 */
	public ArrayList<String> getSurfaceCoordinateNames(); 

	/**
	 * Parametrises the surface of the SceneObject so that patterns etc. can be applied.
	 * @param p the point on the surface
	 * @return two surface coordinates, in the form of a Vector2D, that describe the point p
	 */
	public Vector2D getSurfaceCoordinates(Vector3D p);

	/**
	 * @param p	the point on the surface
	 * @return two vectors, \partial P / \partial coordinate_1, and \partial P / \partial coordinate_2
	 */
	public ArrayList<Vector3D> getSurfaceCoordinateAxes(Vector3D p);
	
	/**
	 * Calculates the surface normal at point p so that reflected rays etc. can be calculated.
	 * This method is also defined in the SceneObjectPrimitive class.
	 * 
	 * @param p the point on the surface
	 * @return the normalised surface normal at point p
	 */
	public Vector3D getNormalisedOutwardsSurfaceNormal(Vector3D p);
}