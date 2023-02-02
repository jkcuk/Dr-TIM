package optics.raytrace.surfaces;

import math.Vector3D;
import optics.raytrace.core.Orientation;

/**
 * Just a simple prism which changes the direction along one axis with a given magnitude. 
 */
public class PhaseHologramOfSimplePrism extends PhaseHologram
{
	
	private static final long serialVersionUID = -8374278145251001941L;

	/**
	 * a direction vector along the line of the tangental component change.
	 */
	private Vector3D uDirection;

	/**
	 * magnitude of the direction change
	 */
	private double magnitude;
	
	//
	// constructors etc.
	//

	/**
	 * @param centre
	 * @param throughputCoefficient
	 * @param reflective
	 * @param shadowThrowing
	 */
	public PhaseHologramOfSimplePrism(
			Vector3D uDirection,
			double magnitude,
			double throughputCoefficient,
			boolean reflective,
			boolean shadowThrowing
		)
	{
		super(throughputCoefficient, reflective, shadowThrowing);
		this.magnitude = magnitude;
		this.uDirection = uDirection;
	}

	/**
	 * @param original
	 */
	public PhaseHologramOfSimplePrism(PhaseHologramOfSimplePrism original)
	{
		this(
				original.getuDirection(),
				original.getMagnitude(),
				original.getTransmissionCoefficient(),
				original.isReflective(),
				original.isShadowThrowing()
			);
	}
	
	@Override
	public PhaseHologramOfSimplePrism clone()
	{
		return new PhaseHologramOfSimplePrism(this);
	}


	//
	// setters & getters
	//
	public Vector3D getuDirection() {
		return uDirection;
	}

	public void setuDirection(Vector3D uDirection) {
		this.uDirection = uDirection;
	}

	public double getMagnitude() {
		return magnitude;
	}

	public void setMagnitude(double magnitude) {
		this.magnitude = magnitude;
	}
	
	//
	// PhaseHologram methods
	// 
	
	@Override
	public Vector3D getTangentialDirectionComponentChangeTransmissive(Vector3D surfacePosition,
			Vector3D surfaceNormal)
	{				
		return uDirection.getProductWith(magnitude);
	}

	@Override
	public Vector3D getTangentialDirectionComponentChangeReflective(Orientation incidentLightRayOrientation,
			Vector3D surfacePosition, Vector3D surfaceNormal)
	{
		// same change in the tangential component as in transmission
		System.err.println("why u refelctt");
		return getTangentialDirectionComponentChangeTransmissive(surfacePosition, surfaceNormal);
	}

}
