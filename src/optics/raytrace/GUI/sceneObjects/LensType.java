package optics.raytrace.GUI.sceneObjects;

public enum LensType {
	IDEAL_THIN_LENS("Ideal thin lens"),
	LENS_HOLOGRAM("Phase hologram of lens"),
	SEMITRANSPARENT_PLANE("Semi-transparent plane");

	private String description;
	private LensType(String description)
	{
		this.description = description;
	}
	@Override
	public String toString() {return description;}
}