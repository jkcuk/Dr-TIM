package optics.raytrace.simplicialComplex;

import math.simplicialComplex.Face;
import optics.raytrace.exceptions.InconsistencyException;
import optics.raytrace.imagingElements.ImagingElement;

/**
 * A (triangular) face in a simplicial complex representing a triangular imaging imaging such as a lens or imaging GCLAs.
 * @author johannes
 * @see optics.raytrace.imagingElements.ImagingElement
 */
public class ImagingFace
extends Face
{
	private static final long serialVersionUID = -2882808437305498307L;

	/**
	 * the imaging surface that describes the imaging properties
	 */
	protected ImagingElement imagingElement;

	/**
	 * @param face
	 * @param imagingElement
	 * @throws InconsistencyException
	 */
	public ImagingFace(Face face, ImagingElement imagingElement)
	throws InconsistencyException
	{
		super(face);

		setImagingElement(imagingElement);
	}

	public ImagingElement getImagingElement() {
		return imagingElement;
	}

	public void setImagingElement(ImagingElement imagingElement) {
		this.imagingElement = imagingElement;
	}
}
