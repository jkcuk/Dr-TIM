package optics.raytrace.surfaces;

import math.*;
import optics.DoubleColour;
import optics.raytrace.core.*;
import optics.raytrace.exceptions.RayTraceException;

public class SimpleRayFlipping extends SurfacePropertyPrimitive
{
	private static final long serialVersionUID = -1564069336728481949L;

	//this is the axis you want to flip over
	private Vector3D flipDirection;
	private double transmissionCoefficient;
	
	public SimpleRayFlipping(Vector3D flipDirection, double transmissionCoefficient, boolean shadowThrowing)
	{
		super(transmissionCoefficient, shadowThrowing);
		this.flipDirection = flipDirection;
	}
	
	public SimpleRayFlipping()
	{
		this(new Vector3D(1,0,0), DEFAULT_TRANSMISSION_COEFFICIENT, true);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public SimpleRayFlipping clone()
	{
		return new SimpleRayFlipping(flipDirection.clone(), transmissionCoefficient, isShadowThrowing());
	}
	
	public Vector3D getFlipDirection() {
		return flipDirection;
	}

	public void setFlipDirection(Vector3D flipDirection) {
		this.flipDirection = flipDirection;
	}

	@Override
	public double getTransmissionCoefficient() {
		return transmissionCoefficient;
	}

	@Override
	public void setTransmissionCoefficient(double transmissionCoefficient) {
		this.transmissionCoefficient = transmissionCoefficient;
	}

	@Override
	public DoubleColour getColour(Ray ray, RaySceneObjectIntersection i, SceneObject scene, LightSource l, int traceLevel, RaytraceExceptionHandler raytraceExceptionHandler)
	throws RayTraceException
	{
		if(traceLevel <= 0) return DoubleColour.BLACK;
		
		Vector3D newRayDirection = ray.getD().getSumWith(flipDirection.getProductWith(-2*flipDirection.getScalarProductWith(ray.getD())/flipDirection.getModSquared()));
		
		// launch a new ray from here
		
		return scene.getColourAvoidingOrigin(
			ray.getBranchRay(i.p, newRayDirection, i.t, ray.isReportToConsole()),
			i.o,
			l,
			scene,
			traceLevel-1,
			raytraceExceptionHandler
		).multiply(transmissionCoefficient);
	}
	
}
