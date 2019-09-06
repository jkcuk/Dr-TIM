package optics.raytrace.GUI.lowLevel;


public enum LensElementType {
	/**
	 * an ideal thin lens, which changes light-ray direction like in principal-ray diagrams
	 */
	IDEAL_THIN_LENS("Ideal thin lens"),
	/**
	 * a phase hologram of a lens, set up to perform perfect 2f-2f imaging
	 */
	PHASE_HOLOGRAM_OF_LENS("Phase hologram of lens"),
	/**
	 * a symmetric glass lens
	 */
	SYMMETRIC_GLASS_LENS("Symmetric glass lens"),
	/**
	 * a pane of glass, which does not change light-ray direction -- not a good lens
	 */
	GLASS_PANE("Glass pane");
	
	private String description;
	
	private LensElementType(String description)
	{
		this.description = description;
	}
	
	@Override
	public String toString()
	{
		return description;
	}
}