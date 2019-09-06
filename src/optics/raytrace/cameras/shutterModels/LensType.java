package optics.raytrace.cameras.shutterModels;

/**
 * Specifies the type of camera lens used by the detector-plane shutter model.
 * For lens type IDEAL_LENS, the lens introduces a time delay such that all rays take the same time to travel between an object and image position.
 * For lens type LENS_HOLOGRAM, there no position-dependent time delay.
 * @see optics.raytrace.cameras.shutterModels.DetectorPlaneShutterModel
 *
 * @author johannes
 */
public enum LensType
{
	IDEAL_LENS("Ideal lens"),
	LENS_HOLOGRAM("Lens hologram");
	
	private String description;
	private LensType(String description) {this.description = description;}	
	@Override
	public String toString() {return description;}
}