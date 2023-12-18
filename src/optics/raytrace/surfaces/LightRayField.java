package optics.raytrace.surfaces;

import math.Vector3D;
import optics.DoubleColour;
import optics.raytrace.core.LightSource;
import optics.raytrace.core.Ray;
import optics.raytrace.core.RaySceneObjectIntersection;
import optics.raytrace.core.RaytraceExceptionHandler;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.SurfaceProperty;
import optics.raytrace.exceptions.RayTraceException;

/**
 * A surface property that represents a light-ray field.
 * At every point, the light-ray field has a particular direction.
 * 
 * @author Johannes Courtial
 */
public abstract class LightRayField extends SurfaceProperty 
// implements SurfacePropertyWithControllableShadow
{
	private static final long serialVersionUID = -7847676117253136484L;
	
	/**
	 * if the exponent is large, the directions of the light-rays in  the field are very  sharp;
	 * if the exponent is small, the directions are very  fuzzy.
	 */
	private double fuzzinessExponent;
	
	/**
	 */
	public LightRayField(double fuzzinessExponent)
	{
		this.fuzzinessExponent = fuzzinessExponent;
	}
	
	public LightRayField()
	{
		this.fuzzinessExponent = 1000;
	}

	
	// setters & getters
	
	/**
	 * @return the exponent
	 */
	public double getFuzzinessExponent() {
		return fuzzinessExponent;
	}

	/**
	 * @param exponent the exponent to set
	 */
	public void setFuzzinessExponent(double fuzzinessExponent) {
		this.fuzzinessExponent = fuzzinessExponent;
	}

	
	// LightRayField methods
	
	/**
	 * Override to customise.
	 * @param point
	 * @param i
	 * @return	the normalised light-ray direction
	 */
	public abstract Vector3D getNormalisedLightRayDirection(RaySceneObjectIntersection i);
//	{
//		//  placeholder; effectively interprets the surface of the SceneObject this is associated with  as a phase front
//		return i.getNormalisedOutwardsSurfaceNormal();
//	}
	
	/**
	 * @param i
	 * @return
	 */
	public abstract DoubleColour getRayColour(RaySceneObjectIntersection i);
	

	/**
	 * Override to change
	 */
	@Override
	public boolean isShadowThrowing() {
		return false;
	}


	/* (non-Javadoc)
	 * @see optics.raytrace.surfaces.SurfaceProperty#getColour(optics.raytrace.core.Ray, optics.raytrace.core.RaySceneObjectIntersection, optics.raytrace.sceneObjects.SceneObject, optics.raytrace.lights.LightSource, int)
	 */
	@Override
	public DoubleColour getColour(Ray r, RaySceneObjectIntersection i, SceneObject scene, LightSource l, int traceLevel, RaytraceExceptionHandler raytraceExceptionHandler)
	throws RayTraceException
	{
		// normalised direction of ray in field
		Vector3D fd = getNormalisedLightRayDirection(i);
		
		// normalised direction of (forwards-traced) ray
		Vector3D rd = r.getD().getReverse();
		
		// continue tracing through the scene, but add to the colour the colour from the light-ray field
		return scene.getColourAvoidingOrigin(
				r.getBranchRay(i.p, r.getD(), i.t, r.isReportToConsole()),
				i.o,
				l,
				scene,
				traceLevel-1,
				raytraceExceptionHandler
			).add(getRayColour(i).multiply(Math.pow(Vector3D.scalarProduct(fd, rd), fuzzinessExponent)));

	}
}

