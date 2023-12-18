package optics.raytrace.surfaces;

import math.Vector3D;
import optics.raytrace.core.*;
import optics.raytrace.exceptions.RayTraceException;


/**
 * A surface property representing (partially) transparent surfaces
 * 
 * @author Johannes Courtial
 */
public class Transparent extends DirectionChanging
{
	private static final long serialVersionUID = 3111776468671161260L;
	
	/**
	 * constant representing a 100% transparent surface, not shadow-throwing
	 */
	public static final Transparent PERFECT = new Transparent(1.0, false);
	
	public static final Transparent SLIGHTLY_ABSORBING = new Transparent(SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT, false);
	
	/**
	 * Creates a new surface property for a (partially) transparent surface
	 * 
	 * @param transmissionCoefficient
	 */
	public Transparent(double transmissionCoefficient, boolean shadowThrowing)
	{
		super(transmissionCoefficient, shadowThrowing);
	}
		
	public Transparent() {
		this(1.0, false);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Transparent clone()
	{
		return new Transparent(getTransmissionCoefficient(), isShadowThrowing());
	}

	
	@Override
	public Vector3D getOutgoingLightRayDirection(Ray ray, RaySceneObjectIntersection intersection, SceneObject scene,
			LightSource lights, int traceLevel, RaytraceExceptionHandler raytraceExceptionHandler)
			throws RayTraceException {
		// simply return the direction of the incident ray
		return ray.getD();
	}

//	/* (non-Javadoc)
//	 * @see optics.raytrace.SurfaceProperty#getColour(optics.raytrace.Ray, optics.raytrace.RaySceneObjectIntersection, optics.raytrace.SceneObject, optics.raytrace.LightSource, int)
//	 */
//	@Override
//	public DoubleColour getColour(Ray ray, RaySceneObjectIntersection i, SceneObject scene, LightSource l, int traceLevel, RaytraceExceptionHandler raytraceExceptionHandler)
//	throws RayTraceException
//	{
//		if(traceLevel <= 0) return DoubleColour.BLACK;
//
//		// launch a new ray from here
//			
//		return scene.getColourAvoidingOrigin(
//			ray.getBranchRay(
//					i.p,
//					ray.getD(),
//					i.t,
//					ray.isReportToConsole()
//			),
//			i.o,
//			l,
//			scene,
//			traceLevel-1,
//			raytraceExceptionHandler
//		).multiply(getTransmissionCoefficient());
//	}
}
