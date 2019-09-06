package optics.raytrace.imagingElements;

import java.io.Serializable;

import math.Vector3D;
import optics.raytrace.core.Orientation;
import optics.raytrace.core.SurfaceProperty;

/**
 * Represents an imaging element
 * 
 * @author Johannes Courtial
 */
public interface ImagingElement extends Serializable, Cloneable
{
	/**
	 * Calculate the image position that corresponds to the given object position.
	 * This might depend on which way the light passes through the element, given by the orientation parameter.
	 * @param objectPosition	Cartesian vector to object position
	 * @param orientation	INWARDS or OUTWARDS
	 * @return	image position
	 */
	public Vector3D getImagePosition(Vector3D objectPosition, Orientation orientation);
	
	public SurfaceProperty toSurfaceProperty(
			double imagingSurfaceTransmissionCoefficient,
			boolean imagingSurfaceShadowThrowing
		);
}
