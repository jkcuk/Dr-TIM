package optics.raytrace.simplicialComplex;

import math.simplicialComplex.Face;
import optics.raytrace.exceptions.InconsistencyException;
import optics.raytrace.imagingElements.HomogeneousPlanarImagingSurface;

/**
 * A (triangular) face in a simplicial complex representing a triangular homogeneous planar imaging surface.
 * @author johannes
 * @see optics.raytrace.imagingElements.HomogeneousPlanarImagingSurface
 */
public class HomogeneousPlanarImagingSurfaceFace
extends ImagingFace
{
	private static final long serialVersionUID = -6504534327033616658L;

	/**
	 * @param face
	 * @param homogeneousPlanarImagingSurface
	 * @throws InconsistencyException
	 */
	public HomogeneousPlanarImagingSurfaceFace(Face face, HomogeneousPlanarImagingSurface homogeneousPlanarImagingSurface)
	throws InconsistencyException
	{
		super(face, homogeneousPlanarImagingSurface);
	}

	public HomogeneousPlanarImagingSurface getHomogeneousPlanarImagingSurface() {
		return (HomogeneousPlanarImagingSurface)getImagingElement();
	}

	public void setHomogeneousPlanarImagingSurface(HomogeneousPlanarImagingSurface homogeneousPlanarImagingSurface) {
		setImagingElement(homogeneousPlanarImagingSurface);
	}
}
