package optics.raytrace.surfaces;

/**
 * This describes the direction in which imaging happens through a glens hologram.
 * Possible values are NEG2POS (object is in -ve space, image in +ve space)
 * and POS2NEG (object in +ve space, image in -ve space).
 * 
 * @see optics.raytrace.surfaces.IdealThinLensSurface#getImagePosition(Vector3D objectPosition, ImagingDirection direction)
 * @see optics.raytrace.surfaces.GlensSurface#getImagePosition(Vector3D objectPosition, ImagingDirection direction)
 * @author johannes
 */
public enum ImagingDirection
{
	NEG2POS, POS2NEG;
}