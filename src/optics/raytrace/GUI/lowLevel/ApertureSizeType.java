package optics.raytrace.GUI.lowLevel;

public enum ApertureSizeType
{
	/**
	 * Describes a pinhole, a point aperture
	 */
	PINHOLE("Pinhole", 0),
	/**
	 * Basically like a pinhole, but allows multiple rays per pixel
	 */
	INFINITESIMAL("Infinitesimal", 0),
	/**
	 * Describes a tiny disk-shaped aperture of radius 0.0125
	 */
	TINY("Tiny", 0.0125),
	/**
	 * Describes a small disk-shaped aperture of radius 0.025
	 */
	SMALL("Small", 0.025),
	/**
	 * Describes a medium-sized disk-shaped aperture of radius 0.05
	 */
	MEDIUM("Medium", 0.05),
	/**
	 * Describes a large disk-shaped aperture of radius 0.1
	 */
	LARGE("Large", 0.1),
	/**
	 * Describes a huge disk-shaped aperture of radius 0.2
	 */
	HUGE("Huge", 0.2),
	/**
	 * Describes a disk-shaped aperture of radius 0.4, useful when a "Huge" aperture is simply not large enough
	 */
	// HUGER("Huger", 0.4);	// for specialist purposes
	/**
	 * Describes a disk-shaped aperture of radius 2mm or 0.002 floor tiles. Useful when simulating the view through an eye. 
	 */
	EYE("Pupil", 0.002);

	private String description;
	private double apertureRadius;
	private ApertureSizeType(String description, double apertureRadius)
	{
		this.description = description;
		this.apertureRadius = apertureRadius;
	}
	public double getApertureRadius() {return apertureRadius;}
	@Override
	public String toString() {return description;}
}