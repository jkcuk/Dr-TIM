package optics.raytrace.simplicialComplex;

import java.util.ArrayList;
import java.util.Collections;

import math.Vector3D;
import math.simplicialComplex.Edge;
import math.simplicialComplex.Face;
import math.simplicialComplex.IndexArray;
import math.simplicialComplex.MappingSimplicialComplex;
import math.simplicialComplex.Simplex;
import math.simplicialComplex.SimplicialComplex;
import optics.raytrace.GUI.sceneObjects.EditableParametrisedTriangle;
import optics.raytrace.GUI.sceneObjects.EditableSceneObjectCollection;
import optics.raytrace.core.Orientation;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.Studio;
import optics.raytrace.core.SurfaceProperty;
import optics.raytrace.core.SurfacePropertyPrimitive;
import optics.raytrace.exceptions.InconsistencyException;
import optics.raytrace.imagingElements.ImagingElement;
import optics.raytrace.surfaces.SurfaceColour;

/**
 * A simplicial complex in which each face represents an imaging element designed such that there is a unique mapping between each simplex and the outside.
 * This is done by replacing all <i>Face</i> objects in the simplicial complex with <i>ImagingFace</i> objects, which have associated imaging properties.
 * @see optics.raytrace.simplicialComplex.ImagingFace
 * @author johannes
 */
public abstract class ImagingSimplicialComplex
extends MappingSimplicialComplex
{
	private static final long serialVersionUID = 3308825249260985675L;

	/**
	 * The list of virtual-space positions that correspond to the (physical-space) vertex positions.
	 */
	ArrayList<? extends Vector3D> verticesV;
	
	public ImagingSimplicialComplex(
			ArrayList<? extends Vector3D> arrayList,
			ArrayList<? extends Vector3D> verticesV2,
			ArrayList<? extends Edge> arrayList2,
			ArrayList<Face> faces,
			ArrayList<Simplex> simplices
		)
	throws InconsistencyException
	{
		super(arrayList, arrayList2, faces, simplices);
		
		// set the virtual-space positions of the vertices, which determine the imaging properties of the faces
		setVerticesV(verticesV2);
		
		// calculate the imaging properties of the faces, replacing all faces with <i>ImagingFace</i> objects, which have associated imaging properties
		setImagingPropertiesOfFaces();
	}
	
	/**
	 * Create a simplicial complex with homogeneous planar imaging surfaces from <i>simplicialComplex</i> and <i>verticesV</i>
	 * @param simplicialComplex
	 * @param verticesV
	 * @throws InconsistencyException
	 */
	public ImagingSimplicialComplex(
			SimplicialComplex simplicialComplex,
			ArrayList<? extends Vector3D> verticesV
		)
	throws InconsistencyException
	{
		this(
				simplicialComplex.getVertices(),
				verticesV,
				simplicialComplex.getEdges(),
				simplicialComplex.getFaces(),
				simplicialComplex.getSimplices()
			);
	}


	//
	// setters & getters
	//
	
	public ArrayList<? extends Vector3D> getVerticesV()
	{
		return verticesV;
	}

	public void setVerticesV(ArrayList<? extends Vector3D> verticesV)
	{
		this.verticesV = verticesV;
	}
	
	public Vector3D getVertexV(int vertexIndex)
	{
		return verticesV.get(vertexIndex);
	}
	
	
	/**
	 * @param pointOnPlane
	 * @param outwardsNormal
	 * @param innerPosition
	 * @param outerPosition
	 * @return	an imaging element in the plane described by <i>pointOnPlane</i> and <i>outwardsNormal</i> that images <i>innerPosition</i> (in inside space) to <i>outerPosition</i> (in outside space)
	 */
	public abstract ImagingElement getImagingElement(
			Vector3D pointOnPlane,
			Vector3D outwardsNormal,	// opticalAxisDirectionPos; take the outwards-facing normal
			Vector3D innerPosition,	// QNeg; the physical-space position of vertex #<i>innerVertexIndex</i>
			Vector3D outerPosition	// QPos; the virtual-space position of vertex #<i>innerVertexIndex</i>, imaged by the outer lenses
		);
		
	/**
	 * @param face
	 * @param imagingElement
	 * @return	<i>face</i>, with added imaging properties defined by <i>imagingElement</i>
	 * @throws InconsistencyException 
	 */
	public abstract ImagingFace getImagingFace(Face face, ImagingElement imagingElement)
	throws InconsistencyException;
	
	
	/**
	 * Infer the imaging properties of the homogeneous planar imaging surfaces that form the faces.
	 * The idea is that surface 1, call it S_1, in an outside face has to image the physical-space position of the "other" vertex of its
	 * inner simplex (i.e. the vertex of the inner simplex that is not a vertex of the face) into the corresponding virtual-space position,
	 * which determines all parameters of S_1.
	 * Next, we look at one of the other faces of the inner simplex of the outside face, i.e. we work our way inwards.  In combination with
	 * S_1, the surface in the current face, call it S_2, must image between the physical and virtual-space positions of the "other" vertex
	 * of its inner simplex.
	 * In this way, we work our way through all faces.
	 * @param surfaceTransmissionCoefficient
	 * @param surfaceShadowThrowing
	 * @throws InconsistencyException 
	 */
	protected void setImagingPropertiesOfFaces()
	throws InconsistencyException
	{
		// go through all faces in this simplicial complex, starting with the outside ones and working "inwards", i.e. towards a higher number
		// of other faces on the way to the outside
		
		// first, work out what the highest number of faces is to the outside
		int maxNoOfFacesToOutside = -1;
		// go through all the faces...
		for(Face face : faces)
		{
			// ... and if the current face's <i>noOfFacesToOutside</i> is greater than <i>maxNoOfFacesToOutside</i>,
			// set <i>maxNoOfFacesToOutside</i> to the current face's <i>noOfFacesToOutside</i>
			maxNoOfFacesToOutside = Math.max(face.getNoOfFacesToOutside(), maxNoOfFacesToOutside);
			
			// System.out.println("LensSimplicialComplex::setImagingPropertiesOfFaces: face #"+this.getFaceIndex(face)
			// +" noOfFacesToOutside="+face.getNoOfFacesToOutside());
		}
		// <i>maxNoOfFacesToOutside</i> is now the highest number of faces to the outside in the simplicial complex.
		// System.out.println("ImagingSimplicialComplex::setImagingPropertiesOfFaces: maxNoOfFacesToOutside="+maxNoOfFacesToOutside);
		
		// now go through all possible values of <i>noOfFacesToOutside</i>, starting with 0, ...
		for(int noOfFacesToOutside = 0; noOfFacesToOutside <= maxNoOfFacesToOutside; noOfFacesToOutside++)
		{
			// System.out.println("\nImagingSimplicialComplex::setImagingPropertiesOfFaces: noOfFacesToOutside="+noOfFacesToOutside+"\n");
			// ... go through all faces...
			for(Face face : faces)
			{
				// ... whose <i>noOfFacesToOutside</i> equals <i>noOfFacesToOutside</i>, ...
				if(face.getNoOfFacesToOutside() == noOfFacesToOutside)
				{
					// System.out.println("\nImagingSimplicialComplex::setImagingPropertiesOfFaces: face#"+face.getIndex());

					// ... and create a list of the faces connecting this one to the outside, initially starting from the inside...
					ArrayList<Face> facesFromOutside = face.getFacesBetweenThisAndOutside();
					// ... but then reversing the array, so that it is starting on the outside
					Collections.reverse(facesFromOutside);
					// System.out.println("\nImagingSimplicialComplex::setImagingPropertiesOfFaces: facesFromOutside=");
					// for(Face faceFromOutside : facesFromOutside) System.out.println("  #"+faceFromOutside.getIndex());
					
					// all these faces in combination must then image between the virtual-space and physical-space positions
					// of the "other" vertex of the face's inner simplex, so identify this vertex
					
					// find the inner simplex
					Simplex innerSimplex = simplices.get(face.getInnerNeighbouringSimplexIndex());
					// System.out.println("\nImagingSimplicialComplex::setImagingPropertiesOfFaces: innerSimplex=#"+getSimplexIndex(innerSimplex));
					
					// find the vertex of this inner simplex that is not a vertex of <i>face</i>
					int innerVertexIndex = innerSimplex.getFirstVertexIndexNotIn(face.getVertexIndices());
					// System.out.println("\nImagingSimplicialComplex::setImagingPropertiesOfFaces: innerVertex=#"+innerVertexIndex);
					
					// calculate the image of the virtual-space position of the inner vertex due to the faces between this one and the outside
					Vector3D outerPosition = getVertexV(innerVertexIndex);
					// System.out.println("ImagingSimplicialComplex::setImagingPropertiesOfFaces: outerPosition="+outerPosition);
					for(Face outerFace : facesFromOutside)
					{
						outerPosition = ((ImagingFace)outerFace).getImagingElement().getImagePosition(outerPosition, Orientation.INWARDS);
						// System.out.println("ImagingSimplicialComplex::setImagingPropertiesOfFaces: outerPosition after face#"+this.getFaceIndex(outerFace)+"="+outerPosition);
					}
					
//					System.out.println("LensSimplicialComplex::setImagingPropertiesOfFaces: face #"+this.getFaceIndex(face)
//						+" images inner vertex #"+innerVertexIndex+", "+getVertex(innerVertexIndex) + ", to outer position="+outerPosition);

					// calculate the surface that images between the physical and virtual-space positions of vertex #<i>innerVertexIndex</i>;
					ImagingElement imagingElement = getImagingElement(
							face.getVertex(0),	// pointOnPlane; take any one of the vertices
							face.calculateOutwardsNormal(),	// opticalAxisDirectionPos; take the outwards-facing normal
							getVertex(innerVertexIndex),	// QNeg; the physical-space position of vertex #<i>innerVertexIndex</i>
							outerPosition	// QPos; the virtual-space position of vertex #<i>innerVertexIndex</i>, imaged by the outer lenses
						);
					
					// try this out!
					// System.out.println("ImagingSimplicialComplex::setImagingPropertiesOfFaces: inner position ="+getVertex(innerVertexIndex));
//					System.out.println("ImagingSimplicialComplex::setImagingPropertiesOfFaces: image of outer position ="+
//							imagingElement.getImagePosition(outerPosition, Orientation.INWARDS)
//						);
					
					// System.out.println("LensSimplicialComplex::setImagingPropertiesOfFaces: lens #"+lens);

					
					// and replace the face with the <i>ImagingFace</i> that includes that imagingElement
					faces.set(faces.indexOf(face), 
							getImagingFace(face, imagingElement)
						);
					
//					// first find the vertex of the next outermost face that is not a vertex of this face
//					
//					// create an array of all vertex indices of this face...
//					int[] vertexIndicesThis = face.getVertexIndices();
//					// ... and the next outermost face
//					int[] vertexIndicesNextOutermost = face.getOutermostNeighbouringFace().getVertexIndices();
//					
//					// then go through all the vertex indices in the next outermost face...
//					int v;
//					for(
//							v=0;
//							(v<3) && // keep going while we haven't gone through all indices of the next outermost face...
//							IndexArray.isInArray(vertexIndicesNextOutermost[v], vertexIndicesThis);	// ... and the current one is a vertex index of this face
//							v++
//						);
//					// have we found the "other" vertex index?
//					if(v > 2)
//					{
//						// no, we haven't!
//						throw new InconsistencyException("Cannot find a vertex index of face 2 that is not a vertex index of face 1.  face 1: "+face+"; face 2: "+face.getOutermostNeighbouringFace());
//					}
//					// the other vertex index is
//					int vertexIndex = vertexIndicesNextOutermost[v];
//					
//					// now check the two simplices on either side of this face and pick the one that doesn't contain vertex #vertexIndex
//					int s;
//					for(s=0; (s<2) && face.getSimplex(s).isVertex(vertexIndex); s++);
//					// have we found the "other" simplex index?
//					if(s > 1)
//					{
//						// no, we haven't!
//						throw new InconsistencyException("Cannot find a simplex on either side of face that does not contain vertex #"+vertexIndex+". face: "+face);
//					}
//					Simplex simplex = face.getSimplex(s);
//					
//					// index of the vertex whose virtual-space position the faces have to image to its physical-space position
//					int indexOfVertex = simplex.getFirstVertexIndexNotIn(face.getVertexIndices());
				}
			}
		}
	}

	/**
	 * @param faceIndex
	 * @param transmissionCoefficient
	 * @param shadowThrowing
	 * @param parent
	 * @param studio
	 * @return	a scene object representing face #<faceIndex>
	 */
	public SceneObject getSceneObjectRepresentingFace(
			int faceIndex,
			double transmissionCoefficient,
			boolean shadowThrowing,
			SceneObject parent,
			Studio studio			
		)
	{
		return EditableParametrisedTriangle.makeEditableParametrisedTriangleFromVertices(
				"Face #"+faceIndex,
				faces.get(faceIndex).getVertex(0),	// vertex 1
				faces.get(faceIndex).getVertex(1),	// vertex 2
				faces.get(faceIndex).getVertex(2),	// vertex 3
				((ImagingFace)faces.get(faceIndex)).getImagingElement().toSurfaceProperty(
						transmissionCoefficient,
						shadowThrowing
						),	// surface property 
				parent, // parent
				studio
				);
	}
	
	/**
	 * Adds SceneObject representing this ImagingSimplicialComplex to editableSimplicialComplex.
	 * @see optics.raytrace.core.SceneObject
	 * @param editableSimplicialComplex
	 * @param showImagingSurfaces
	 * @param imagingSurfaceTransmissionCoefficient
	 * @param imagingSurfaceShadowThrowing
	 * @param showVertices
	 * @param vertexSurfaceProperty
	 * @param vertexRadius
	 * @param showVerticesV
	 * @param vertexSurfacePropertyV
	 * @param vertexRadiusV
	 * @param showEdges
	 * @param edgeSurfaceProperty
	 * @param edgeRadius
	 * @param showEdgesV
	 * @param edgeSurfacePropertyV
	 * @param edgeRadiusV
	 * @param showFaces
	 * @param faceSurfaceProperty
	 * @param showFacesV
	 * @param faceSurfacePropertyV
	 * @param parent
	 * @param studio
	 * @return	a scene-object collection that represents the simplicial complex
	 */
	public void populateEditableSceneObjectCollection(
			EditableSceneObjectCollection editableSimplicialComplex,
			double imagingSurfaceTransmissionCoefficient,
			boolean imagingSurfaceShadowThrowing,
			boolean showVertices,
			SurfaceProperty vertexSurfaceProperty,
			double vertexRadius,
			boolean showVerticesV,
			SurfaceProperty vertexSurfacePropertyV,
			double vertexRadiusV,
			boolean showEdges,
			SurfaceProperty edgeSurfaceProperty,
			double edgeRadius,
			boolean showEdgesV,
			SurfaceProperty edgeSurfacePropertyV,
			double edgeRadiusV,
			boolean showFaces,
			SurfaceProperty faceSurfaceProperty,
			boolean showFacesV,
			SurfaceProperty faceSurfacePropertyV,
			SceneObject parent,
			Studio studio
		)
	{
		// create the scene-object collection that will hold the scene objects representing the imaging surfaces
		EditableSceneObjectCollection imagingSurfaces = new EditableSceneObjectCollection("Imaging surfaces", true, editableSimplicialComplex, studio);
		
		// add lenses for the faces, so go through all the faces...
		for(int i=0; i<faces.size(); i++)
		{
			// ..., add a triangle representing the current (ith) one to the scene-object collection <i>lenses</i>, ...
			imagingSurfaces.addSceneObject(
					getSceneObjectRepresentingFace(
							i,	// faceIndex
							imagingSurfaceTransmissionCoefficient,	// transmissionCoefficient
							imagingSurfaceShadowThrowing,	// shadowThrowing
							imagingSurfaces, // parent
							studio			
						)
					);
//			imagingSurfaces.addSceneObject(EditableParametrisedTriangle.makeEditableParametrisedTriangleFromVertices(
//					"Face #"+i,
//					faces.get(i).getVertex(0),	// vertex 1
//					faces.get(i).getVertex(1),	// vertex 2
//					faces.get(i).getVertex(2),	// vertex 3
//					((ImagingFace)faces.get(i)).getImagingElement().toSurfaceProperty(
//							imagingSurfaceTransmissionCoefficient,
//							imagingSurfaceShadowThrowing
//						),	// surface property 
//					imagingSurfaces, // parent
//					studio
//				));
		}
		// ... and add <i>imagingSurfaces</i> to </i>editableLensSimplicialComples</i>
		editableSimplicialComplex.addSceneObject(imagingSurfaces);
		
		// create and add the editable scene-object collection representing the physical-space simplicial complex
		editableSimplicialComplex.addSceneObject(super.getEditableSceneObjectCollection("Physical-space simplicial complex", showVertices, vertexSurfaceProperty, vertexRadius, showEdges, edgeSurfaceProperty, edgeRadius, showFaces, faceSurfaceProperty, editableSimplicialComplex, studio));

		// To create the editable scene-object collection representing the virtual space simplicial complex,
		// create a <i>SimplicialComplex</i> representing virtual space.
		// This shares all edges, faces and simplices with this <i>LensSimplicialComplex</i>.
		// To create the simplicial complex representing virtual space, first clone all the edges, faces and simplices
		// (as these have internal variables pointing to the simplicial complex they are part of), ...
		ArrayList<Edge> edges2 = new ArrayList<Edge>();
		for(Edge edge : edges) edges2.add(edge.clone());

		ArrayList<Face> faces2 = new ArrayList<Face>();
		for(Face face : faces) faces2.add(face.clone());

		ArrayList<Simplex> simplices2 = new ArrayList<Simplex>();
		for(Simplex simplex : simplices) simplices2.add(simplex.clone());

		// ... then create a new simplicial complex from the virtual-space vertex positions and the cloned edges, faces and simplices
		// and add it to the scene-object collection representing this lens simplicial complex
		try {
			editableSimplicialComplex.addSceneObject(
					new SimplicialComplex(
							verticesV,	// vertices
							edges2,	// edges
							faces2,
							simplices2
						).getEditableSceneObjectCollection(
								"Virtual-space simplicial complex",
								showVerticesV,
								vertexSurfacePropertyV,
								vertexRadiusV,
								showEdgesV,
								edgeSurfacePropertyV,
								edgeRadiusV,
								showFacesV,
								faceSurfacePropertyV,
								editableSimplicialComplex,
								studio
							)
			);
		} catch (InconsistencyException e) {
			e.printStackTrace();
		}
	}
	

	public void populateEditableSceneObjectCollection(
			EditableSceneObjectCollection editableSimplicialComplex,
			SceneObject parent,
			Studio studio
		)
	{
		populateEditableSceneObjectCollection(
				editableSimplicialComplex,
				SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT,	// imagingSurfaceTransmissionCoefficient
				false,	// imagingSurfaceShadowThrowing
				false,	// showVertices,
				SurfaceColour.BLUE_SHINY,	// vertexSurfaceProperty
				0.031,	// vertexRadius
				false,	// showVerticesV,
				SurfaceColour.RED_SHINY,	// vertexSurfacePropertyV
				0.03,	// vertexRadiusV
				false,	// showEdges,
				SurfaceColour.BLUE_SHINY,	// edgeSurfaceProperty
				0.021,	// edgeRadius
				false,	// showEdgesV,
				SurfaceColour.RED_SHINY,	// edgeSurfacePropertyV
				0.02,	// edgeRadiusV
				false,	// showFaces,
				SurfaceColour.LIGHT_BLUE_SHINY,	// faceSurfaceProperty
				false,	// showFacesV,
				SurfaceColour.LIGHT_RED_SHINY,	// faceSurfacePropertyV
				parent,
				studio
			);
	}


	@Override
	public Vector3D mapToOutside(int simplexIndex, Vector3D position)
	{
		// if <simplexIndex> is the index of the outside...
		if(simplexIndex == IndexArray.OUTSIDE)
		{
			// ... return the original position
			return position;
		}
			
		try {
			ArrayList<Face> facesFromInside;

			// create a list of the faces connecting this one to the outside, starting from the inside
			facesFromInside = getSimplex(simplexIndex).getFacesBetweenThisAndOutside();
					
			// calculate the image of the virtual-space position of the inner vertex due to the faces between this one and the outside
			Vector3D image = position;
			for(Face face : facesFromInside)
			{
				image = ((ImagingFace)face).getImagingElement().getImagePosition(image, Orientation.OUTWARDS);
			}

			return image;
		} catch (InconsistencyException e) {
			e.printStackTrace();
			return null;
		}
	}


	@Override
	public Vector3D mapFromOutside(int simplexIndex, Vector3D position)
	{
		// System.out.println("ImagingSimplicialComplex:mapFromOutside:: simplexIndex="+simplexIndex+", position =" + position);

		// if <simplexIndex> is the index of the outside...
		if(simplexIndex == IndexArray.OUTSIDE)
		{
			// ... return the original position
			return position;
		}
			
		try {
			ArrayList<Face> facesFromOutside;

			// create a list of the faces connecting this one to the outside, initially starting from the inside...
			facesFromOutside = getSimplex(simplexIndex).getFacesBetweenThisAndOutside();
			
			// ... but then reversing the array, so that it is starting on the outside
			Collections.reverse(facesFromOutside);
					
			// calculate the image of the virtual-space position of the inner vertex due to the faces between this one and the outside
			Vector3D image = position;
			for(Face face : facesFromOutside)
			{
				image = ((ImagingFace)face).getImagingElement().getImagePosition(image, Orientation.INWARDS);
			}

			return image;
		} catch (InconsistencyException e) {
			e.printStackTrace();
			return null;
		}
	}

}
