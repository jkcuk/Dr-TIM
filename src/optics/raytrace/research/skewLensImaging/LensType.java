package optics.raytrace.research.skewLensImaging;

public enum LensType {
	IDEAL_THIN_LENS("Ideal thin lens"),
	FRESNEL_LENS("Fresnel lens (eye-position-image optimised)"),
	LENS_HOLOGRAM("Phase hologram of lens"),
	LENS_HOLOGRAM_EYE("Phase hologram of lens (eye-position-image optimised)");

	private String description;
	private LensType(String description)
	{
		this.description = description;
	}
	@Override
	public String toString() {return description;}
}