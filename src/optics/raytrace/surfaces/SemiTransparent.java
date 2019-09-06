package optics.raytrace.surfaces;

import optics.raytrace.core.SurfaceProperty;

/**
 * Combines a surface property with the Transparent surface property, to make it semi-transparent.
 */
public class SemiTransparent extends SurfacePropertyAverage
// implements SurfaceProperty, Serializable
{
	private static final long serialVersionUID = -8319166581121966830L;

	public static final SemiTransparent
		RED_SHINY_SEMITRANSPARENT = new SemiTransparent(SurfaceColour.RED_SHINY, 0.75),
		GREEN_SHINY_SEMITRANSPARENT = new SemiTransparent(SurfaceColour.GREEN_SHINY, 0.75),
		BLUE_SHINY_SEMITRANSPARENT = new SemiTransparent(SurfaceColour.BLUE_SHINY, 0.75);
	
	private double transmissionCoefficient;

	/**
	 * Create an empty collection of surface properties.
	 * Additional surface properties can be added later using the add(SurfaceProperty sp) method.
	 * 
	 * @see optics.raytrace.surfaces.SemiTransparent#add(optics.raytrace.core.SurfaceProperty)
	 */
	public SemiTransparent(SurfaceProperty surfaceProperty, double transmissionCoefficient)
	{
		super(surfaceProperty, new Transparent(transmissionCoefficient, false));
		
		setTransmissionCoefficient(transmissionCoefficient);
	}
	
	/**
	 * Create a clone of the original
	 * @param original
	 */
	public SemiTransparent(SemiTransparent original)
	{
		super(original);
		
		setTransmissionCoefficient(original.getTransmissionCoefficient());
	}

	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public SemiTransparent clone()
	{
		return new SemiTransparent(this);
	}

	
	// setters and getters
	
	public double getTransmissionCoefficient() {
		return transmissionCoefficient;
	}

	public void setTransmissionCoefficient(double transmissionCoefficient) {
		this.transmissionCoefficient = transmissionCoefficient;
	}
	
	@Override
	public boolean isShadowThrowing() {
		return false;
	}
}
