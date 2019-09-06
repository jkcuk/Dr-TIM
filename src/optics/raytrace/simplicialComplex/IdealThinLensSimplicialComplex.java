package optics.raytrace.simplicialComplex;

import java.util.ArrayList;

import math.Vector3D;
import math.simplicialComplex.Edge;
import math.simplicialComplex.Face;
import math.simplicialComplex.Simplex;
import math.simplicialComplex.SimplicialComplex;
import optics.raytrace.GUI.sceneObjects.EditableParametrisedTriangle;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.Studio;
import optics.raytrace.core.SurfaceProperty;
import optics.raytrace.core.SurfacePropertyPrimitive;
import optics.raytrace.exceptions.InconsistencyException;
import optics.raytrace.imagingElements.ImagingElement;
import optics.raytrace.sceneObjects.FresnelLensShaped;
import optics.raytrace.surfaces.IdealThinLensSurface;
import optics.raytrace.surfaces.Point2PointImagingPhaseHologram;
import optics.raytrace.surfaces.SemiTransparent;
import optics.raytrace.surfaces.SurfaceColour;

/**
 * A simplicial complex in which each face represents an ideal thin lens designed such that there is a unique mapping between each simplex and the outside.
 * This is done by replacing all <i>Face</i> objects in the simplicial complex with <i>IdealThinLensFace</i> objects, which have associated imaging properties.
 * This class can also populate a given scene-object collection with scene objects that represent this ideal-thin-lens structure, or the same structure in which
 * the ideal thin lenses are replaced by more realistic elements, such as phase holograms of lenses and Fresnel lenses.
 * @see optics.raytrace.simplicialComplex.IdealThinLensFace
 * @author johannes
 */
public class IdealThinLensSimplicialComplex
extends ImagingSimplicialComplex
{
	private static final long serialVersionUID = -5571554386457215239L;
	
	/**
	 * Allows the type of SceneObject that represents the faces for raytracing purposes to be selected
	 * @see optics.raytrace.simplicialComplex.ImagingSimplicialComplex#getSceneObjectRepresentingFace(int, double, boolean, optics.raytrace.core.SceneObject, optics.raytrace.core.Studio)
	 */
	private LensType lensTypeRepresentingFace;
	
	/**
	 * Position in outside space from which this simplicial complex looks best.
	 * If lensTypeRepresentingFace = IDEAL_THIN_LENS, the simplicial complex should work perfectly from <i>any</i> outside-space viewing position.
	 * If lensTypeRepresentingFace = POINT_2_POINT_IMAGING_HOLOGRAM, this is not the case, but from the
	 * optimumOutsideSpaceViewingPosition the simplicial complex should look the same as if lensTypeRepresentingFace = IDEAL_THIN_LENS.
	 */
	private Vector3D optimumOutsideSpaceViewingPosition;
	
	/**
	 * @param vertices
	 * @param verticesV
	 * @param edges
	 * @param faces
	 * @param simplices
	 * @param lensTypeRepresentingFace
	 * @param optimumOutsideSpaceViewingPosition
	 * @throws InconsistencyException
	 */
	public IdealThinLensSimplicialComplex(
			ArrayList<? extends Vector3D> vertices, 
			ArrayList<? extends Vector3D> verticesV,
			ArrayList<? extends Edge> edges,
			ArrayList<Face> faces,
			ArrayList<Simplex> simplices,
			LensType lensTypeRepresentingFace,
			Vector3D optimumOutsideSpaceViewingPosition
			)
	throws InconsistencyException
	{
		super(vertices, verticesV, edges, faces, simplices);
		
		setLensTypeRepresentingFace(lensTypeRepresentingFace);
		setOptimumOutsideSpaceViewingPosition(optimumOutsideSpaceViewingPosition);
	}
	
	/**
	 * Constructor that sets lensTypeRepresentingFace variable to IDEAL_THIN_LENS
	 * @param vertices
	 * @param verticesV
	 * @param edges
	 * @param faces
	 * @param simplices
	 * @throws InconsistencyException
	 */
	public IdealThinLensSimplicialComplex(
			ArrayList<? extends Vector3D> vertices, 
			ArrayList<? extends Vector3D> verticesV,
			ArrayList<? extends Edge> edges,
			ArrayList<Face> faces,
			ArrayList<Simplex> simplices
			)
	throws InconsistencyException
	{
		this(vertices, verticesV, edges, faces, simplices,
				LensType.IDEAL_THIN_LENS,
				new Vector3D(0, 0, 0)	// optimum outside viewing position; matters only if lensTypeRepresentingFace is not IDEAL_THIN_LENS
			);
	}

	/**
	 * @param simplicialComplex
	 * @param verticesV
	 * @throws InconsistencyException
	 */
	public IdealThinLensSimplicialComplex(
			SimplicialComplex simplicialComplex,
			ArrayList<? extends Vector3D> verticesV
		)
	throws InconsistencyException
	{
		super(simplicialComplex, verticesV);
		
		setLensTypeRepresentingFace(LensType.IDEAL_THIN_LENS);
		setOptimumOutsideSpaceViewingPosition(new Vector3D(0, 0, 0));
	}

	
	// getters & setters

	public LensType getLensTypeRepresentingFace() {
		return lensTypeRepresentingFace;
	}

	public void setLensTypeRepresentingFace(LensType lensTypeRepresentingFace) {
		this.lensTypeRepresentingFace = lensTypeRepresentingFace;
	}

	public Vector3D getOptimumOutsideSpaceViewingPosition() {
		return optimumOutsideSpaceViewingPosition;
	}

	public void setOptimumOutsideSpaceViewingPosition(Vector3D optimumOutsideSpaceViewingPosition) {
		this.optimumOutsideSpaceViewingPosition = optimumOutsideSpaceViewingPosition;
	}
	
	
	
	/* (non-Javadoc)
	 * @see optics.raytrace.simplicialComplex.ImagingSimplicialComplex#getImagingElement(math.Vector3D, math.Vector3D, math.Vector3D, math.Vector3D)
	 */
	@Override
	public ImagingElement getImagingElement(Vector3D pointOnPlane, Vector3D outwardsNormal, Vector3D innerPosition,
			Vector3D outerPosition)
	{
		IdealThinLensSurface idealThinLensSurface = null;
		
		try {
			idealThinLensSurface = new IdealThinLensSurface(
					pointOnPlane,
					outwardsNormal,
					innerPosition,
					outerPosition,
					SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT,	// transmissionCoefficient; this is a placeholder, as this gets set later
					false	// shadowThrowing; this is a placeholder, as this gets set later
				);
		} catch (InconsistencyException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		return idealThinLensSurface;
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.simplicialComplex.ImagingSimplicialComplex#getImagingFace(math.simplicialComplex.Face, optics.raytrace.imagingElements.ImagingElement)
	 */
	@Override
	public ImagingFace getImagingFace(Face face, ImagingElement imagingElement)
	throws InconsistencyException
	{
		return new IdealThinLensFace(face, (IdealThinLensSurface)imagingElement);
	}
	
	/* (non-Javadoc)
	 * @see optics.raytrace.simplicialComplex.ImagingSimplicialComplex#getSceneObjectRepresentingFace(int, double, boolean, optics.raytrace.core.SceneObject, optics.raytrace.core.Studio)
	 */
	@Override
	public SceneObject getSceneObjectRepresentingFace(
			int faceIndex,
			double transmissionCoefficient,
			boolean shadowThrowing,
			SceneObject parent,
			Studio studio			
		)
	{
		// This gets called after all the imaging properties of the ideal thin lenses have been set...
		
		Face face = null;
		SceneObject sceneObject = null;
		
		switch(lensTypeRepresentingFace)
		{
		case NONE:
			// leave sceneObject point to nothingness
			break;
		case SEMITRANSPARENT_PLANE:
			face = faces.get(faceIndex);
			sceneObject = EditableParametrisedTriangle.makeEditableParametrisedTriangleFromVertices(
						"Face #"+faceIndex,
						face.getVertex(0),	// vertex 1
						face.getVertex(1),	// vertex 2
						face.getVertex(2),	// vertex 3
						false,	// invert the normal to the triangle?
						new SemiTransparent(SurfaceColour.BLUE_SHINY, 0.9), // surface property
						parent, // parent
						studio
						);
			break;
		case POINT_2_POINT_IMAGING_HOLOGRAM:
			// first work out the pair of conjugate points this face images into each other
			face = faces.get(faceIndex);
			
			try {
				Vector3D insideSpacePoint = mapFromOutside(face.getInnerNeighbouringSimplexIndex(), optimumOutsideSpaceViewingPosition);
				Vector3D outsideSpacePoint = mapFromOutside(face.getOuterNeighbouringSimplexIndex(), optimumOutsideSpaceViewingPosition);
				
				System.out.println("IdealThinLensSimplicialComplex:getSceneObjectRepresentingFace"+faceIndex+":: insideSpacePoint="+insideSpacePoint+", outsideSpacePoint="+outsideSpacePoint);
				//The next 4 lines check that the EditableParametrisedTriangle that is about to be constructed has its surface normal pointing in the same direction as the face normals.
				Vector3D vertex1to2 = Vector3D.difference(face.getVertex(1),face.getVertex(0));
				Vector3D vertex1to3 = Vector3D.difference(face.getVertex(2),face.getVertex(0));
				Vector3D triangleNormal = Vector3D.crossProduct(vertex1to2, vertex1to3);
				Vector3D faceNormal = face.calculateOutwardsNormal();
				sceneObject = EditableParametrisedTriangle.makeEditableParametrisedTriangleFromVertices(
						"Face #"+faceIndex,
						face.getVertex(0),	// vertex 1
						face.getVertex(1),	// vertex 2
						face.getVertex(2),	// vertex 3
						Vector3D.scalarProduct(triangleNormal, faceNormal) < 0,	// if triangleNormal.faceNormal < 0, invert the normal to the triangle
						new Point2PointImagingPhaseHologram(
								insideSpacePoint,
								outsideSpacePoint,
								transmissionCoefficient,	// throughputCoefficient
								false,	// reflective
								shadowThrowing
								),	// surface property 
						parent, // parent
						studio
						);
			} catch (InconsistencyException e) {
				e.printStackTrace();
				System.exit(-1);
			}
			break;
		case FRESNEL_LENS:
			// first work out the pair of conjugate points this face images into each other
			face = faces.get(faceIndex);
			Vector3D insideSpacePoint;
			try {
				insideSpacePoint = mapFromOutside(face.getInnerNeighbouringSimplexIndex(), optimumOutsideSpaceViewingPosition);

				Vector3D outsideSpacePoint = mapFromOutside(face.getOuterNeighbouringSimplexIndex(), optimumOutsideSpaceViewingPosition);

				System.out.println("IdealThinLensSimplicialComplex:getSceneObjectRepresentingFace"+faceIndex+":: insideSpacePoint="+insideSpacePoint+", outsideSpacePoint="+outsideSpacePoint);
				//The next 4 lines check that the EditableParametrisedTriangle that is about to be constructed has its surface normal pointing in the same direction as the face normals.
				Vector3D vertex1to2 = Vector3D.difference(face.getVertex(1),face.getVertex(0));
				Vector3D vertex1to3 = Vector3D.difference(face.getVertex(2),face.getVertex(0));
				Vector3D triangleNormal = Vector3D.crossProduct(vertex1to2, vertex1to3);
				Vector3D faceNormal = face.calculateOutwardsNormal();

				EditableParametrisedTriangle faceAperture = EditableParametrisedTriangle.makeEditableParametrisedTriangleFromVertices(
						"Face #"+faceIndex,
						face.getVertex(0),	// vertex 1
						face.getVertex(1),	// vertex 2
						face.getVertex(2),	// vertex 3
						Vector3D.scalarProduct(triangleNormal, faceNormal) < 0,	// if triangleNormal.faceNormal < 0, invert the normal to the triangle
						SurfaceProperty.NO_SURFACE_PROPERTY,
						parent, // parent
						studio
						);
				sceneObject = new FresnelLensShaped(
						"Fresnel lens reresenting face #"+faceIndex, // description,
						face.calculateCentroid(),// lensCentre,
						faceAperture.getSurfaceNormal(),// forwardsCentralPlaneNormal,
						insideSpacePoint, // frontConjugatePoint,
						outsideSpacePoint, // backConjugatePoint,
						1.5,// refractiveIndex,
						0.1,// thickness,
						0.01,// minimumSurfaceSeparation,
						faceAperture,// apertureShape,
						true,// makeStepSurfacesBlack,
						0.96, // transmissionCoefficient,
						parent,// parent,
						studio);
			} catch (InconsistencyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case IDEAL_THIN_LENS:
		default:
			sceneObject = super.getSceneObjectRepresentingFace(
					faceIndex,
					transmissionCoefficient,
					shadowThrowing,
					parent,
					studio
				);
//			return EditableParametrisedTriangle.makeEditableParametrisedTriangleFromVertices(
//					"Face #"+faceIndex,
//					faces.get(faceIndex).getVertex(0),	// vertex 1
//					faces.get(faceIndex).getVertex(1),	// vertex 2
//					faces.get(faceIndex).getVertex(2),	// vertex 3
//					((ImagingFace)faces.get(faceIndex)).getImagingElement().toSurfaceProperty(
//							transmissionCoefficient,
//							shadowThrowing
//							),	// surface property 
//					parent, // parent
//					studio
//					);
		}
		
		return sceneObject;
	}

}
