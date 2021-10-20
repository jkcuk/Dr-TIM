package optics.raytrace.sceneObjects;

public enum LensType {
	/**
	 * an ideal thin lens, which changes light-ray direction like in principal-ray diagrams
	 */
	IDEAL_THIN_LENS("Ideal thin lens"),
	/**
	 * a phase hologram of a lens, set up to perform perfect 2f-2f imaging
	 */
	PHASE_HOLOGRAM_OF_LENS("Phase hologram of lens");
	
	private String description;
	
	private LensType(String description)
	{
		this.description = description;
	}
	
	@Override
	public String toString()
	{
		return description;
	}
}