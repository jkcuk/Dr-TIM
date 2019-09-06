package optics.raytrace.GUI.sceneObjects;

public enum EditablePinchTransformationWindowElementType
{
	/**
	 * Describes homogeneous GCLAs.  The geometric loss at each component is correctly calculated.
	 */
	HOMOGENEOUS("Homogeneous GCLAs"),
	/**
	 * Describes homogeneous GCLAs.  The geometric loss at each component is ignored.
	 */
	HOMOGENEOUS_NO_GEOMETRIC_LOSS("Homogeneous GCLAs, no geometric loss");
	/**
	 * Describes a small disk-shaped aperture of radius 0.025
	 */
	// LENS("Lens");	// doesn't work, at least so far

	private String description;
	private EditablePinchTransformationWindowElementType(String description)
	{
		this.description = description;
	}
	@Override
	public String toString() {return description;}
}