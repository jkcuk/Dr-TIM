package optics.raytrace.core;

import optics.DoubleColour;
import optics.raytrace.exceptions.RayTraceException;

/**
 * Called by ray-tracing routines when they encounter exceptions.
 * @author johannes
 *
 */
public interface RaytraceExceptionHandler
{
	/**
	 * A ray-tracing routine should call this method when a ray does not intersect with any scene object.
	 * @param ray
	 * @param originObject
	 * @param l
	 * @param scene
	 * @param traceLevel
	 * @return
	 */
	public DoubleColour getColourOfRayFromNowhere(Ray ray, SceneObject originObject, LightSource l, SceneObject scene, int traceLevel)
	throws RayTraceException;
}
