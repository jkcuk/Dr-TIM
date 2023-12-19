package optics.raytrace.surfaces;

import math.MyMath;
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
	 * angular width of fuzziness
	 */
	private double angularFuzzinessRad;
	
	/**
	 */
	public LightRayField(double angularFuzzinessRad)
	{
		this.angularFuzzinessRad = angularFuzzinessRad;
	}
	
	public LightRayField()
	{
		this(MyMath.deg2rad(1));
	}

	
	// setters & getters
	
	/**
	 * @return the angularFuzzinessRad
	 */
	public double getAngularFuzzinessRad() {
		return angularFuzzinessRad;
	}

	/**
	 * @param angularFuzzinessRad the angularFuzzinessRad to set
	 */
	public void setAngularFuzzinessRad(double angularFuzzinessRad) {
		this.angularFuzzinessRad = angularFuzzinessRad;
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
		
		// double scalarProduct = Vector3D.scalarProduct(fd, rd);
		
		// the angle between the ray direction and the ray in the light field
		double angleRatio = Math.acos(Vector3D.scalarProduct(fd, rd)) / angularFuzzinessRad;
		double directionFactor;
		if(Math.abs(angleRatio) > 0.5) directionFactor = 0;
		else {
			double c = Math.cos(Math.PI*angleRatio);
			directionFactor = c*c;
		}
//		double directionFactor;
//		
//		if(scalarProduct < 0) directionFactor =  0;
//		else if()?Math.pow(Vector3D.scalarProduct(fd, rd), fuzzinessExponent):0;
		
		// continue tracing through the scene, but add to the colour the colour from the light-ray field
		return scene.getColourAvoidingOrigin(
				r.getBranchRay(i.p, r.getD(), i.t, r.isReportToConsole()),
				i.o,
				l,
				scene,
				traceLevel-1,
				raytraceExceptionHandler
			).add(getRayColour(i).multiply(directionFactor));

	}
}

