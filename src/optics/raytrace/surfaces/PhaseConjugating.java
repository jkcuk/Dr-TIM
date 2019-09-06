package optics.raytrace.surfaces;

import math.*;
import optics.DoubleColour;
import optics.raytrace.core.*;
import optics.raytrace.exceptions.RayTraceException;


/**
 * A surface property representing phase-conjugating surfaces.
 * The surface can be reflective (then it's basically negative refraction) or reflective (which just reverses light-ray direction).
 * 
 * @author Johannes Courtial
 */
public class PhaseConjugating extends SurfacePropertyPrimitive
{
	private static final long serialVersionUID = 7695596955609176036L;

	private ReflectiveOrTransmissive reflectiveOrTransmissive;
	
	/**
	 * constant representing a 100% reflective surface
	 */
	public static final PhaseConjugating PERFECT_PHASE_CONJUGATING_MIRROR = new PhaseConjugating(ReflectiveOrTransmissive.REFLECTIVE, PERFECT_TRANSMISSION_COEFFICIENT, true);
	
	/**
	 * Creates an instance of the surface property representing (specularly) reflective surfaces
	 * 
	 * @param transmissionCoefficient
	 */
	public PhaseConjugating(ReflectiveOrTransmissive reflectiveOrTransmissive, double transmissionCoefficient, boolean shadowThrowing)
	{
		super(transmissionCoefficient, shadowThrowing);
		setReflectiveOrTransmissive(reflectiveOrTransmissive);
	}
	
	/**
	 * By default make a perfect phase-conjugating mirror.
	 */
	public PhaseConjugating()
	{
		this(ReflectiveOrTransmissive.REFLECTIVE, PERFECT_TRANSMISSION_COEFFICIENT, true);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public PhaseConjugating clone()
	{
		return new PhaseConjugating(
				getReflectiveOrTransmissive(),
				getTransmissionCoefficient(),
				isShadowThrowing()
			);
	}
	
	public ReflectiveOrTransmissive getReflectiveOrTransmissive() {
		return reflectiveOrTransmissive;
	}

	public void setReflectiveOrTransmissive(ReflectiveOrTransmissive reflectiveOrTransmissive) {
		this.reflectiveOrTransmissive = reflectiveOrTransmissive;
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
	public static DoubleColour getColour(Ray ray, RaySceneObjectIntersection intersection, SceneObject scene, LightSource lights, int traceLevel, ReflectiveOrTransmissive type, RaytraceExceptionHandler raytraceExceptionHandler)
	throws RayTraceException
	{
		// check the trace level is positive; if not, return black
		if (traceLevel <= 0) return DoubleColour.BLACK;
		
		Vector3D newRayDirection;
		
		switch(type)
		{
		case REFLECTIVE:
			newRayDirection = ray.getD().getReverse();
			break;
		case TRANSMISSIVE:
		default:
			// calculate direction of reflected ray
			Vector3D n = intersection.getNormalisedOutwardsSurfaceNormal();	// surface normal to the object at the intersection point
			newRayDirection = Vector3D.difference(
				ray.getD().getPartParallelTo(n),
				ray.getD().getPartPerpendicularTo(n)
			);
			break;
		}
		
		// launch a new ray from here
		
		return scene.getColourAvoidingOrigin(
			ray.getBranchRay(intersection.p, newRayDirection, intersection.t),	// creating the new ray using the original ray's getSecondaryRay method ensures the ray trajectory is recorded correctly
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
		return getColour(ray, intersection, scene, lights, traceLevel, reflectiveOrTransmissive, raytraceExceptionHandler).multiply(getTransmissionCoefficient());	// multiply the intensity by the reflection coefficient
	}
}
