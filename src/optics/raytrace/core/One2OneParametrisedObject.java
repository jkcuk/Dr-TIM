package optics.raytrace.core;

import math.Vector3D;

/**
 * Parametrises the surface of the SceneObject so that patterns etc. can be applied.
 * The parametrisation is one-to-one, so it can be inverted.
 * For example, a sphere could be described by two angles.
 */
public interface One2OneParametrisedObject extends ParametrisedObject
{
	/**
	 * Calculate the point on the surface that corresponds to the two surface coordinates p.
	 * Inverse parametrisation, in a sense.
	 * @param u	first surface coordinate
	 * @param v	second surface coordinate
	 * @return the point on the surface
	 * @author Johannes
	 */
	public abstract Vector3D getPointForSurfaceCoordinates(double u, double v);
}