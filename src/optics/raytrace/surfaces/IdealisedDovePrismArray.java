package optics.raytrace.surfaces;


import math.*;
import optics.DoubleColour;
import optics.raytrace.core.*;
import optics.raytrace.exceptions.RayTraceException;


/**
 * Partial idealisation of a Dove-prism array (a complete idealisation is the RayFlipping class).
 * Splits the surface into stripes, which then get mirrored.
 * 
 * @author Johannes Courtial
 */
public class IdealisedDovePrismArray extends SurfacePropertyPrimitive
{
	private static final long serialVersionUID = 2396364017492528505L;

	/**
	 * The centre of the 0th Dove prism
	 */
	Vector3D dovePrism0Centre;
	
	/**
	 * The normalised inversion direction, i.e. the direction of the component of the light-ray direction that gets inverted on transmission.
	 * This is also the direction in which the array is periodic.
	 * The position also gets "inverted"
	 */
	Vector3D inversionDirection;
	
	/**
	 * Period of the array, in the period direction
	 */
	double period;
	
	/**
	 * @param dovePrism0Centre
	 * @param inversionDirection
	 * @param period
	 * @param transmissionCoefficient
	 * @param shadowThrowing
	 */
	public IdealisedDovePrismArray(
			Vector3D dovePrism0Centre,
			Vector3D inversionDirection,
			double period,
			double transmissionCoefficient,
			boolean shadowThrowing
		) 
	{
		super(transmissionCoefficient, shadowThrowing);
		this.dovePrism0Centre = dovePrism0Centre;
		setInversionDirection(inversionDirection);
		this.period = period;
	}
		
	/**
	 * @param original
	 */
	public IdealisedDovePrismArray(IdealisedDovePrismArray original)
	{
		this(
				original.getDovePrism0Centre(),
				original.getInversionDirection(),
				original.getPeriod(),
				original.getTransmissionCoefficient(),
				original.isShadowThrowing()
			);		
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public IdealisedDovePrismArray clone()
	{
		return new IdealisedDovePrismArray(this);
	}
	
	
	
	//
	// setters and getters
	//
	
	public Vector3D getDovePrism0Centre() {
		return dovePrism0Centre;
	}

	public void setDovePrism0Centre(Vector3D dovePrism0Centre) {
		this.dovePrism0Centre = dovePrism0Centre;
	}

	public Vector3D getInversionDirection() {
		return inversionDirection;
	}

	/**
	 * Normalises <i>inversionDirection</i> and sets inversion direction to this vector.
	 * @param inversionDirection
	 */
	public void setInversionDirection(Vector3D inversionDirection) {
		this.inversionDirection = inversionDirection.getNormalised();
	}

	public double getPeriod() {
		return period;
	}

	public void setPeriod(double period) {
		this.period = period;
	}
	
	
	//
	// SurfaceProperty methods
	//

	/* (non-Javadoc)
	 * @see optics.raytrace.SurfaceProperty#getColour(optics.raytrace.Ray, optics.raytrace.RaySceneObjectIntersection, optics.raytrace.SceneObject, optics.raytrace.LightSource, int)
	 */
	@Override
	public DoubleColour getColour(Ray ray, RaySceneObjectIntersection i, SceneObject scene, LightSource l, int traceLevel, RaytraceExceptionHandler raytraceExceptionHandler)
	throws RayTraceException
	{
		if (traceLevel <= 0) return DoubleColour.BLACK;
		
		// light-ray direction is easy
		Vector3D newRayDirection = Vector3D.sum(
				ray.getD(),
				ray.getD().getPartParallelTo(inversionDirection).getProductWith(-2)
			);
		
		// the change in light-ray position is more complicated
		
		// the distance from the 0th Dove-prism centre to the light-ray intersection...
		Vector3D p = Vector3D.difference(i.p, dovePrism0Centre);
		// ... and its component in the inversion direction
		double pi = Vector3D.scalarProduct(p, inversionDirection);
		// from this, calculate the index <i>n</i> of the Dove prism through which the ray passes
		double n = Math.floor(pi/period+0.5);
		
		// calculate the centre of the nth Dove prism
		Vector3D dovePrismNCentre = Vector3D.sum(dovePrism0Centre, inversionDirection.getProductWith(n*period));
		Vector3D newRayPosition = Vector3D.sum(
				i.p,
				Vector3D.difference(dovePrismNCentre, i.p).getPartParallelTo(inversionDirection).getProductWith(2)
			);
		// TODO if the surface is curved, or if <i>inversionDirection</i> is not in the plane of the surface, this new ray position can lie outside of the surface
		
		// launch a new ray from here
		return scene.getColourAvoidingOrigin(
			ray.getBranchRay(newRayPosition, newRayDirection, i.t),
			i.o,
			l,
			scene,
			traceLevel-1,
			raytraceExceptionHandler
		).multiply(
			getTransmissionCoefficient()
		);
	}
}
