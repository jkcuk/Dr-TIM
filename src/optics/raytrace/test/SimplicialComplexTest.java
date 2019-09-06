package optics.raytrace.test;

import optics.raytrace.GUI.sceneObjects.EditableLensSimplicialComplex;
import optics.raytrace.exceptions.InconsistencyException;


/**
 * Do some testing of the SimplicialComplex class.
 * 
 * @author  Johannes Courtial
 */
public class SimplicialComplexTest
{
	/**
	 * This method gets called when the Java application starts.
	 * 
	 * @see createStudio
	 * @author	Johannes Courtial
	 * @throws InconsistencyException 
	 */
	public static void main(final String[] args)
	// throws InconsistencyException
	{
		EditableLensSimplicialComplex editableLensSimplicialComplex = new EditableLensSimplicialComplex("test", null, null);
		editableLensSimplicialComplex.getLensSimplicialComplex().printNicely(System.out);
		
		// for debugging purposes: resulting simplicial complex
//		Simplices:
//			  #0:Simplex [vertexIndices=[0, 1, 2, 3], faceIndices=[0, 1, 4, 9], outerNeighbourSimplexIndex=-10, outerFaceIndex=0]: bottom simplex
//			  #1:Simplex [vertexIndices=[0, 1, 3, 4], faceIndices=[1, 2, 7, 12], outerNeighbourSimplexIndex=0, outerFaceIndex=1]: back inner simplex
//			  #2:Simplex [vertexIndices=[0, 1, 4, 5], faceIndices=[2, 3, 8, 13], outerNeighbourSimplexIndex=-10, outerFaceIndex=3]: back top simplex
//			  #3:Simplex [vertexIndices=[0, 2, 3, 4], faceIndices=[4, 5, 7, 14], outerNeighbourSimplexIndex=0, outerFaceIndex=4]: front left inner simplex
//			  #4:Simplex [vertexIndices=[0, 2, 4, 5], faceIndices=[5, 6, 8, 15], outerNeighbourSimplexIndex=-10, outerFaceIndex=6]: front left top simplex
//			  #5:Simplex [vertexIndices=[1, 2, 3, 4], faceIndices=[9, 10, 12, 14], outerNeighbourSimplexIndex=0, outerFaceIndex=9]: front right inner simplex
//			  #6:Simplex [vertexIndices=[1, 2, 4, 5], faceIndices=[10, 11, 13, 15], outerNeighbourSimplexIndex=-10, outerFaceIndex=11]: front right top simplex
//		Faces:
//			  #0:Face [vertexIndices=[0, 1, 2], edgeIndices=[0, 1, 2], simplexIndices=[0, -10], noOfFacesToOutside=0]: bottom face
//			    connected to the outside through faces []
//			  #1:Face [vertexIndices=[0, 1, 3], edgeIndices=[0, 4, 3], simplexIndices=[0, 1], noOfFacesToOutside=1]: back inner lower roof (back top face of bottom simplex)
//			    connected to the outside through faces [0]
//			  #2:Face [vertexIndices=[0, 1, 4], edgeIndices=[0, 7, 6], simplexIndices=[1, 2], noOfFacesToOutside=1]: back inner upper roof
//			    connected to the outside through faces [3]
//			  #3:Face [vertexIndices=[0, 1, 5], edgeIndices=[0, 10, 9], simplexIndices=[2, -10], noOfFacesToOutside=0]: back top roof (back top outside face)
//			    connected to the outside through faces []
//			  #4:Face [vertexIndices=[0, 2, 3], edgeIndices=[2, 5, 3], simplexIndices=[0, 3], noOfFacesToOutside=1]: left inner lower roof
//			    connected to the outside through faces [0]
//			  #5:Face [vertexIndices=[0, 2, 4], edgeIndices=[2, 8, 6], simplexIndices=[3, 4], noOfFacesToOutside=1]: left inner upper roof
//			    connected to the outside through faces [6]
//			  #6:Face [vertexIndices=[0, 2, 5], edgeIndices=[2, 11, 9], simplexIndices=[4, -10], noOfFacesToOutside=0]: left top roof
//			    connected to the outside through faces []
//			  #7:Face [vertexIndices=[0, 3, 4], edgeIndices=[3, 12, 6], simplexIndices=[1, 3], noOfFacesToOutside=2]: back left lower vertical face
//			    connected to the outside through faces [1, 0]
//			  #8:Face [vertexIndices=[0, 4, 5], edgeIndices=[6, 13, 9], simplexIndices=[2, 4], noOfFacesToOutside=1]: back left upper vertical face
//			    connected to the outside through faces [3]
//			  #9:Face [vertexIndices=[1, 2, 3], edgeIndices=[1, 5, 4], simplexIndices=[0, 5], noOfFacesToOutside=1]: right inner lower roof
//			    connected to the outside through faces [0]
//			  #10:Face [vertexIndices=[1, 2, 4], edgeIndices=[1, 8, 7], simplexIndices=[5, 6], noOfFacesToOutside=1]: right inner upper roof
//			    connected to the outside through faces [11]
//			  #11:Face [vertexIndices=[1, 2, 5], edgeIndices=[1, 11, 10], simplexIndices=[6, -10], noOfFacesToOutside=0]: right top roof
//			    connected to the outside through faces []
//			  #12:Face [vertexIndices=[1, 3, 4], edgeIndices=[4, 12, 7], simplexIndices=[1, 5], noOfFacesToOutside=2]: back right lower vertical face
//			    connected to the outside through faces [1, 0]
//			  #13:Face [vertexIndices=[1, 4, 5], edgeIndices=[7, 13, 10], simplexIndices=[2, 6], noOfFacesToOutside=1]: back right upper vertical face
//			    connected to the outside through faces [3]
//			  #14:Face [vertexIndices=[2, 3, 4], edgeIndices=[5, 12, 8], simplexIndices=[3, 5], noOfFacesToOutside=2]: front lower vertical face
//			    connected to the outside through faces [4, 0]
//			  #15:Face [vertexIndices=[2, 4, 5], edgeIndices=[8, 13, 11], simplexIndices=[4, 6], noOfFacesToOutside=1]: front upper vertical face
//			    connected to the outside through faces [6]
//		Edges:
//			  #0:Edge [vertexIndices=[0, 1]]
//			  #1:Edge [vertexIndices=[1, 2]]
//			  #2:Edge [vertexIndices=[2, 0]]
//			  #3:Edge [vertexIndices=[3, 0]]
//			  #4:Edge [vertexIndices=[3, 1]]
//			  #5:Edge [vertexIndices=[3, 2]]
//			  #6:Edge [vertexIndices=[4, 0]]
//			  #7:Edge [vertexIndices=[4, 1]]
//			  #8:Edge [vertexIndices=[4, 2]]
//			  #9:Edge [vertexIndices=[5, 0]]
//			  #10:Edge [vertexIndices=[5, 1]]
//			  #11:Edge [vertexIndices=[5, 2]]
//			  #12:Edge [vertexIndices=[3, 4]]
//			  #13:Edge [vertexIndices=[4, 5]]
//		Vertices:
//			  #0:(-1, 0, 1): back left bottom vertex
//			  #1:(1, 0, 1): back right bottom vertex
//			  #2:(0, 0, -1): front bottom vertex
//			  #3:(0, 0.3, 0): lower inner vertex
//			  #4:(0, 0.6, 0): upper inner vertex
//			  #5:(0, 1, 0): top vertex


		// for debugging purposes: output created by LensSimplicialComplex::setImagingPropertiesOfFaces:
//		face #0 (bottom face) images inner vertex #3 (lower inner vertex), (0, 0.3, 10), to outer position=(0, 0.1, 10)
//		face #3 (back top roof) images inner vertex #4 (upper inner vertex), (0, 0.6, 10), to outer position=(0, 0.9, 10)
//		face #6 (left top roof) images inner vertex #4 (upper inner vertex), (0, 0.6, 10), to outer position=(0, 0.9, 10)
//		face #11 (right top roof) images inner vertex #4 (upper inner vertex), (0, 0.6, 10), to outer position=(0, 0.9, 10)
//		face #1 (back inner lower roof) images inner vertex #4 (upper inner vertex), (0, 0.6, 10), to outer position=(0, 0.9, 10)
//		face #2 (back inner upper roof) images inner vertex #3 (lower inner vertex), (0, 0.3, 10), to outer position=(0, 0.1, 10)
//		face #4 (left inner lower roof) images inner vertex #4 (upper inner vertex), (0, 0.6, 10), to outer position=(0, 0.9, 10)
//		face #5 (left inner upper roof) images inner vertex #3 (lower inner vertex), (0, 0.3, 10), to outer position=(0, 0.1, 10)
//		face #8 (back left upper vertical face) images inner vertex #2, (0, 0, 9), to outer position=(0, 0, 9)
//		face #9 (right inner lower roof) images inner vertex #4 (upper inner vertex), (0, 0.6, 10), to outer position=(0, 0.9, 10)
//		face #10 (right inner upper roof) images inner vertex #3 (lower inner vertex), (0, 0.3, 10), to outer position=(0, 0.1, 10)
//		face #13 (back right upper vertical face) images inner vertex #2 (front bottom vertex), (0, 0, 9), to outer position=(0, 0, 9)
//		face #15 (front upper vertical face) images inner vertex #1 (back right bottom vertex), (1, 0, 11), to outer position=(1, 0, 11)
//		face #7 (back left lower vertical face) images inner vertex #2 (front bottom vertex), (0, 0, 9), to outer position=(0, 0, 9)
//		face #12 (back right lower vertical face) images inner vertex #2 (front bottom vertex), (0, 0, 9), to outer position=(0, 0, 9)
//		face #14 (front lower vertical face) images inner vertex #1 (back right bottom vertex), (1, 0, 11), to outer position=(1, 0, 11)
		
//		// create a new array of vertices
//		ArrayList<Vector3D> vertices = new ArrayList<Vector3D>();
//		
//		// add a few vertices
//		vertices.add(new Vector3D(-1, 0,  1));	// 0
//		vertices.add(new Vector3D( 1, 0,  1));	// 1
//		vertices.add(new Vector3D( 0, 0, -1));	// 2
//		vertices.add(new Vector3D( 0, 0.3,  0));	// 3
//		vertices.add(new Vector3D( 0, 0.6,  0));	// 4
//		vertices.add(new Vector3D( 0, 1,  0));	// 5
//		
//		// create a new array of edges
//		ArrayList<Edge> edges = new ArrayList<Edge>();
//
//		try {
//
//			// add a few vertices
//			edges.add(new Edge(0, 1));	// 0
//			edges.add(new Edge(1, 2));	// 1
//			edges.add(new Edge(2, 0));	// 2
//			edges.add(new Edge(3, 0));	// 3
//			edges.add(new Edge(3, 1));	// 4
//			edges.add(new Edge(3, 2));	// 5
//			edges.add(new Edge(4, 0));	// 6
//			edges.add(new Edge(4, 1));	// 7	
//			edges.add(new Edge(4, 2));	// 8
//			edges.add(new Edge(5, 0));	// 9
//			edges.add(new Edge(5, 1));	// 10
//			edges.add(new Edge(5, 2));	// 11
//			edges.add(new Edge(3, 4));	// 12
//			edges.add(new Edge(4, 5));	// 13
//
//			//		System.out.println("Edge #0:" + edges.get(0));
//			//		System.out.println("Other vertex index that's not 1: "+edges.get(0).getOtherVertexIndex(1));
//			//		System.out.println("Other vertex index that's not 0: "+edges.get(0).getOtherVertexIndex(0));
//			//		System.out.println("Other vertex index that's not 2: "+edges.get(0).getOtherVertexIndex(2));
//
//			// create a simplicial complex with the given vertices and edges and the faces and simplices inferred
//
//			SimplicialComplex simplicialComplex = SimplicialComplex.getSimplicialComplexFromVerticesAndEdges(vertices, edges);
//			
//			System.out.println("SimplicialComplexTest::main: simplicialComplex = "+simplicialComplex.toString());		
//
//			simplicialComplex.printNicely(System.out);
			
//			System.out.println("Simplices:");
//			int i=0;
//			for(Simplex simplex : simplicialComplex.getSimplices())
//				System.out.println("  #"+(i++)+":"+simplex.toString());
//
//			System.out.println("Faces:");
//			i=0;
//			for(Face face : simplicialComplex.getFaces())
//			{
//				System.out.println("  #"+(i++)+":"+face.toString());
//				System.out.println("    connected to the outside through faces "+face.getIndicesOfFacesBetweenThisAndOutside());
//			}
//
//			System.out.println("Edges:");
//			i=0;
//			for(Edge edge : simplicialComplex.getEdges())
//				System.out.println("  #"+(i++)+":"+edge.toString());
//
//			System.out.println("Vertices:");
//			i=0;
//			for(Vector3D vertex : simplicialComplex.getVertices())
//				System.out.println("  #"+(i++)+":"+vertex.toString());

//		} catch (InconsistencyException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
}
