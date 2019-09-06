package optics.raytrace.simplicialComplex;

import math.simplicialComplex.Face;
import optics.raytrace.exceptions.InconsistencyException;
import optics.raytrace.surfaces.IdealThinLensSurface;

/**
 * @author johannes
 * A (triangular) face in a simplicial complex representing a triangular ideal thin lens
 */
public class IdealThinLensFace
extends ImagingFace
{
	private static final long serialVersionUID = 4343889332528392953L;

	/**
	 * @param face
	 * @param idealThinLens
	 * @throws InconsistencyException
	 */
	public IdealThinLensFace(Face face, IdealThinLensSurface idealThinLens)
	throws InconsistencyException
	{
		super(face, idealThinLens);
	}

	public IdealThinLensSurface getLens() {
		return (IdealThinLensSurface)getImagingElement();
	}

	public void setLens(IdealThinLensSurface idealThinLens) {
		setImagingElement(idealThinLens);
	}
}
