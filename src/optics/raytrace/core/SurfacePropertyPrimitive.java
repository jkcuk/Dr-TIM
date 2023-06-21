package optics.raytrace.core;


public abstract class SurfacePropertyPrimitive extends SurfaceProperty implements SurfacePropertyWithControllableShadow
{
	private static final long serialVersionUID = -7202500338852725151L;

	public static final SurfacePropertyPrimitive
		NO_SURFACE_PROPERTY = null;
	public static final double
		PERFECT_TRANSMISSION_COEFFICIENT = 1.0,
		DEFAULT_TRANSMISSION_COEFFICIENT = 0.96;

	// transmission OR reflection coefficient
	private double transmissionCoefficient = DEFAULT_TRANSMISSION_COEFFICIENT;

	// does this surface throw a shadow?
	protected boolean shadowThrowing = true;

	public SurfacePropertyPrimitive(double transmissionCoefficient, boolean shadowThrowing)
	{
		setTransmissionCoefficient(transmissionCoefficient);
		setShadowThrowing(shadowThrowing);
	}
	
	public SurfacePropertyPrimitive()
	{
		this(DEFAULT_TRANSMISSION_COEFFICIENT, true);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public abstract SurfacePropertyPrimitive clone();

	public double getTransmissionCoefficient() {
		return transmissionCoefficient;
	}

	public void setTransmissionCoefficient(double transmissionCoefficient) {
		this.transmissionCoefficient = transmissionCoefficient;
	}
	
	/**
	 * @return true if the scene object throws a shadow, false if it doesn't
	 */
	@Override
	public boolean isShadowThrowing() {
		return shadowThrowing;
	}

	/**
	 * @param shadowThrowing	true if the scene object is supposed to throw a shadow, false if it isn't
	 */
	// @Override
	@Override
	public void setShadowThrowing(boolean shadowThrowing) {
		this.shadowThrowing = shadowThrowing;
	}
}

