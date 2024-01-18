package optics.raytrace.surfaces;

import optics.DoubleColour;
import optics.raytrace.exceptions.EvanescentException;
import optics.raytrace.exceptions.RayTraceException;
import optics.raytrace.core.LightSource;
import optics.raytrace.core.Ray;
import optics.raytrace.core.RaySceneObjectIntersection;
import optics.raytrace.core.RaytraceExceptionHandler;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.SurfacePropertyPrimitive;
import math.Vector3D;

/**
 * A light-ray-direction-changing surface property
 */

public abstract class DirectionChanging extends SurfacePropertyPrimitive
{
	private static final long serialVersionUID = 2509700127546032408L;

	//Constructor
	public DirectionChanging(double transmissionCoefficient, boolean shadowThrowing)
	{
		super(transmissionCoefficient, shadowThrowing);
	}

	public DirectionChanging(DirectionChanging original)
	{
		super(original.getTransmissionCoefficient(), original.isShadowThrowing());
	}
	
	public static Vector3D getTIRDirection(Ray ray, RaySceneObjectIntersection intersection, SceneObject scene, LightSource lights, int traceLevel, RaytraceExceptionHandler raytraceExceptionHandler)
	{
		return Reflective.getReflectedLightRayDirection(ray.getD(), intersection.getNormalisedOutwardsSurfaceNormal());
	}
	
	/**
	 * Override to make this work.
	 * Throw an EvanescentException if TIR occurs, and it will be dealt with correctly.
	 * @param ray
	 * @param intersection
	 * @param scene
	 * @param lights
	 * @param traceLevel
	 * @param raytraceExceptionHandler
	 * @return
	 * @throws RayTraceException
	 */
	public abstract Vector3D getOutgoingLightRayDirection(Ray ray, RaySceneObjectIntersection intersection, SceneObject scene, LightSource lights, int traceLevel, RaytraceExceptionHandler raytraceExceptionHandler)
	throws RayTraceException;

	/* (non-Javadoc)
	 * @see optics.raytrace.surfaces.SurfaceProperty#getColour(optics.raytrace.core.Ray, optics.raytrace.core.RaySceneObjectIntersection, optics.raytrace.sceneObjects.SceneObject, optics.raytrace.lights.LightSource, int)
	 */
	@Override
	public DoubleColour getColour(Ray ray, RaySceneObjectIntersection intersection, SceneObject scene, LightSource lights, int traceLevel, RaytraceExceptionHandler raytraceExceptionHandler)
	throws RayTraceException
	{
		// Check traceLevel is greater than 0.
		if(traceLevel <= 0) return DoubleColour.BLACK;
	
		try {
			Vector3D newRayDirection = getOutgoingLightRayDirection(ray, intersection, scene, lights, traceLevel, raytraceExceptionHandler);
			
			// launch a new ray from here
			
			return scene.getColourAvoidingOrigin(
				ray.getBranchRay(intersection.p, newRayDirection, intersection.t, ray.isReportToConsole()),
				intersection.o,
				lights,
				scene,
				traceLevel-1,
				raytraceExceptionHandler
			).multiply(getTransmissionCoefficient());
		} catch (EvanescentException e) {
			return Reflective.getReflectedColour(ray, intersection, scene, lights, traceLevel, raytraceExceptionHandler);
		}
	}
}



