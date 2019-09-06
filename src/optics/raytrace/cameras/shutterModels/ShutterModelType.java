package optics.raytrace.cameras.shutterModels;

/**
 * Allows selection of the type of shutter model
 * @see optics.raytrace.cameras.shutterModels.ShutterModel
 * 
 * @author johannes
 */
public enum ShutterModelType
{
	/**
	 * Describes a shutter model in which the shutter is located in the camera's aperture plane.
	 * In the camera frame, the entire shutter surface opens simultaneously for an instant at an arbitrary time.
	 */
	APERTURE_PLANE_SHUTTER("Aperture-plane shutter"),
	/**
	 * Describes a shutter model in which the shutter is located in an arbitrary plane.
	 * In the camera frame, the entire shutter surface opens simultaneously for an instant at an arbitrary time.
	 */
	ARBITRARY_PLANE_SHUTTER("Arbitrary-plane shutter"),
	/**
	 * Describes a shutter model in which the shutter is located in the plane of the camera's detector.
	 * In the camera frame, the entire shutter surface opens simultaneously for an instant at an arbitrary time.
	 */
	DETECTOR_PLANE_SHUTTER("Detector-plane shutter"),	// normally called "focal-plane shutter"
	/**
	 * Describes a shutter model in which the shutter is conceptually located in surface on which the camera is focussed.
	 * In the camera frame, the entire shutter surface opens simultaneously for an instant at an arbitrary time.
	 */
	FOCUS_SURFACE_SHUTTER("Focus-surface shutter"),	// http://en.wikipedia.org/wiki/Single-lens_reflex_camera
	/**
	 * Describes a shutter model in which the timing of the photo rays is such that the spatial parts of the events
	 * of the photo rays passing through the positions on which the camera is focussed do not change when Lorentz-transformed.
	 * The shutter does not necessarily open simultaneously in the camera frame.
	 */
	FIXED_POINT_SURFACE_SHUTTER("Focus-surface shutter (all points in focus surface are fixed points)");
	
	private String description;
	private ShutterModelType(String description) {this.description = description;}	
	@Override
	public String toString() {return description;}
}