package optics.raytrace.simplicialComplex;

import java.util.ArrayList;

import math.Vector3D;
import math.simplicialComplex.Edge;
import math.simplicialComplex.Face;
import math.simplicialComplex.Simplex;
import math.simplicialComplex.SimplicialComplex;
import optics.raytrace.exceptions.InconsistencyException;
import optics.raytrace.imagingElements.HomogeneousPlanarImagingSurface;
import optics.raytrace.imagingElements.ImagingElement;

/**
 * A simplicial complex in which each face represents a homogeneous planar imaging surface designed such that there is a unique mapping between each simplex and the outside.
 * This is done by replacing all <i>Face</i> objects in the simplicial complex with <i>HomogeneousPlanarImagingSurfaceFace</i> objects, which have associated imaging properties.
 * @see optics.raytrace.simplicialComplex.HomogeneousPlanarImagingSurfaceFace
 * @author johannes
 */
public class HomogeneousPlanarImagingSurfaceSimplicialComplex
extends ImagingSimplicialComplex
{

	public HomogeneousPlanarImagingSurfaceSimplicialComplex(ArrayList<Vector3D> vertices, ArrayList<Vector3D> verticesV,
			ArrayList<Edge> edges, ArrayList<Face> faces, ArrayList<Simplex> simplices)
	throws InconsistencyException
	{
		super(vertices, verticesV, edges, faces, simplices);
	}
	
	
	public HomogeneousPlanarImagingSurfaceSimplicialComplex(
			SimplicialComplex simplicialComplex,
			ArrayList<Vector3D> verticesV
		)
	throws InconsistencyException
	{
		super(simplicialComplex, verticesV);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 7844209187483332947L;

	@Override
	public ImagingElement getImagingElement(Vector3D pointOnPlane, Vector3D outwardsNormal, Vector3D innerPosition,
			Vector3D outerPosition)
	{
		return new HomogeneousPlanarImagingSurface(
				pointOnPlane,
				outwardsNormal,
				innerPosition,
				outerPosition
			);
	}

	@Override
	public ImagingFace getImagingFace(Face face, ImagingElement imagingElement)
	throws InconsistencyException
	{
		return new HomogeneousPlanarImagingSurfaceFace(face, (HomogeneousPlanarImagingSurface)imagingElement);
	}
}
