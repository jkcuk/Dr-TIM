package optics.raytrace.surfaces;

import math.*;
import optics.DoubleColour;
import optics.raytrace.core.*;
import optics.raytrace.exceptions.RayTraceException;


/**
 * A surface property representing (specularly) reflective surfaces.
 * 
 * @author Johannes Courtial
 */
public class Reflective extends SurfacePropertyPrimitive
{
	private static final long serialVersionUID = -1372581435577575248L;

	/**
	 * constant representing a 100% reflective surface
	 */
	public static final Reflective PERFECT_MIRROR = new Reflective(1.0, true);
	
	/**
	 * Creates an instance of the surface property representing (specularly) reflective surfaces
	 * 
	 * @param reflectionCoefficient
	 */
	public Reflective(double reflectionCoefficient, boolean shadowThrowing)
	{
		super(reflectionCoefficient, shadowThrowing);
	}
	
	/**
	 * By default make a perfect mirror.
	 */
	public Reflective()
	{
		this(PERFECT_TRANSMISSION_COEFFICIENT, true);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Reflective clone()
	{
		return new Reflective(getTransmissionCoefficient(), isShadowThrowing());
	}
	
	/**
	 * @return the reflectionCoefficient
	 */
	public double getReflectionCoefficient() {
		return getTransmissionCoefficient();
	}

	/**
	 * @param reflectionCoefficient the reflectionCoefficient to set
	 */
	public void setReflectionCoefficient(double reflectionCoefficient)
	{
		setTransmissionCoefficient(reflectionCoefficient);
	}
	
	/**
	 * A static method...
	 * @param ray
	 * @param i
	 * @param scene
	 * @param l
	 * @param traceLevel
	 * @return
	 * @throws RayTraceException
	 */
	public static DoubleColour getReflectedColour(Ray ray, RaySceneObjectIntersection intersection, SceneObject scene, LightSource lights, int traceLevel, RaytraceExceptionHandler raytraceExceptionHandler)
	throws RayTraceException
	{
		// check the trace level is positive; if not, return black
		if (traceLevel <= 0) return DoubleColour.BLACK;
		
		// calculate direction of reflected ray
		Vector3D n = intersection.getNormalisedOutwardsSurfaceNormal();	// surface normal to the object at the intersection point
		
		Vector3D newRayDirection = Vector3D.difference(
			ray.getD(),
			ray.getD().getProjectionOnto(n).getProductWith(2)
		);
		
		// launch a new ray from here
		
		return scene.getColourAvoidingOrigin(
			ray.getBranchRay(intersection.p, newRayDirection, intersection.t, ray.isReportToConsole()),	// creating the new ray using the original ray's getSecondaryRay method ensures the ray trajectory is recorded correctly
			intersection.o,	// the primitive scene object being intersected
			lights,	// the light source(s)
			scene,	// the entire scene
			traceLevel-1,	// launch the new ray with a trace level reduced by 1
			raytraceExceptionHandler
		);
	}


	/* (non-Javadoc)
	 * @see optics.raytrace.SurfaceProperty#getColour(optics.raytrace.Ray, optics.raytrace.RaySceneObjectIntersection, optics.raytrace.SceneObject, optics.raytrace.LightSource, int)
	 */
	@Override
	public DoubleColour getColour(Ray ray, RaySceneObjectIntersection intersection, SceneObject scene, LightSource lights, int traceLevel, RaytraceExceptionHandler raytraceExceptionHandler)
	throws RayTraceException
	{
		return getReflectedColour(ray, intersection, scene, lights, traceLevel, raytraceExceptionHandler).multiply(getTransmissionCoefficient());	// multiply the intensity by the reflection coefficient
	}

	/**
	 * Calculate reflection.
	 * 
	 * @param incidentLightRayDirection	the (not necessarily normalised) incident light-ray direction
	 * @param surfaceNormal	the (not necessarily normalised) surface normal
	 * @return	the outgoing light-ray direction
	 */
	public static Vector3D getReflectedLightRayDirection(Vector3D incidentLightRayDirection, Vector3D surfaceNormal)
	{
		Vector3D newRayDirection = Vector3D.difference(
				incidentLightRayDirection,
				incidentLightRayDirection.getProjectionOnto(surfaceNormal).getProductWith(2)
			);

		// return the reflected light-ray direction
		return newRayDirection;
	}
}
