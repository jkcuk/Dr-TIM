package optics.raytrace.core;

import java.io.Serializable;

import optics.DoubleColour;
import optics.raytrace.exceptions.RayTraceException;

public abstract class SurfaceProperty implements Serializable
{
	private static final long serialVersionUID = -2494620266236104125L;
	
	public enum ReflectiveOrTransmissive {REFLECTIVE, TRANSMISSIVE}
		
	public static final SurfaceProperty
		NO_SURFACE_PROPERTY = null;

	/**
	 * Return the colour corresponding to the ray r hitting intersection i.
	 * The scene and traceLevel are provided so that the ray can be traced further, if necessary (e.g. after reflection).
	 * In this case, any secondary rays must be added to the ray, but only if ray is an instance of RayWithTrajectory.
	 * 
	 * @param r
	 * @param i
	 * @param scene
	 * @param traceLevel
	 * @param raytraceExceptionHandler
	 * @return the colour of the ray r hitting intersection i
	 */
	public abstract DoubleColour getColour(Ray r, RaySceneObjectIntersection i, SceneObject scene, LightSource l, int traceLevel, RaytraceExceptionHandler raytraceExceptionHandler)
	throws RayTraceException;

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public abstract SurfaceProperty clone();

	/**
	 * @return true if the scene object throws a shadow, false if it doesn't
	 */
	public abstract boolean isShadowThrowing();
}

