package math;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import optics.raytrace.exceptions.InconsistencyException;

/**
 * @author johannes
 * A simplicial complex with a finite volume.
 */
public class SimplicialComplexOld
{
	/**
	 * The vertices of the simplicial complex
	 */
	protected Vector3D[] vertices;
	
	/**
	 * The edges of the simplices.
	 * Each edge is described by an array of two integers, namely the indices of the vertices at the start and end of the edge.
	 * <edgesVertices> is an array of these arrays representing the edges.
	 */
	protected int[][] edgesVertices;
	
	/**
	 * The faces of the simplices.
	 * Each face is described by an array of three integers, namely the indices of the vertices of (triangular) simplex face.
	 * <facesVertices> is an array of these arrays representing the faces.
	 */
	protected int[][] facesVertices;
	
	/**
	 * The faces of the simplices, described in terms of their edges.
	 * Each face is described by an array of three integers, namely the indices of the edges of the (triangular) simplex face.
	 * <facesEdges> is an array of these arrays representing the faces.
	 */
	protected int[][] facesEdges;
	
	/**
	 * The simplices.
	 * Each simplex is described by an array of four integers, namely the indices of the four vertices.
	 * <simplicesVertices> is an array of these arrays representing the simplices.
	 */
	protected int[][] simplicesVertices;
	
	/**
	 * The simplices, described in terms of their faces.
	 * Each simplex is described by an array of four integers, namely the indices of the four faces.
	 * <simplicesFaces> is an array of these arrays representing the simplices.
	 */
	protected int[][] simplicesFaces;
	
	
	//
	// constructors
	//
	
	/**
	 * Constructor creating a simplicial complex from all arrays.
	 * @param vertices
	 * @param edgesVertices
	 * @param facesVertices
	 * @param facesEdges
	 * @param simplicesVertices
	 * @param simplicesFaces
	 * @throws InconsistencyException 
	 */
	public SimplicialComplex(
			Vector3D[] vertices,
			int[][] edgesVertices,
			int[][] facesVertices,
			int[][] facesEdges,
			int[][] simplicesVertices,
			int[][] simplicesFaces
		) throws InconsistencyException
	{
		setVertices(vertices);
		setEdgesVertices(edgesVertices);
		setFacesVertices(facesVertices);
		setFacesEdges(facesEdges);
		setSimplicesVertices(simplicesVertices);
		setSimplicesFaces(simplicesFaces);
	}
	
	/**
	 * create an empty simplicial complex
	 * @throws InconsistencyException 
	 */
	public SimplicialComplex() throws InconsistencyException
	{
		this(null, null, null, null, null, null);
	}
	
	
	//
	// different ways to create simplicial complexes
	//
	
	public static SimplicialComplex getSimplicialComplexFromVerticesAndEdges(
			Vector3D[] vertices,
			int[][] edgesVertices
		) throws InconsistencyException
	{
		// create an empty simplicial complex...
		SimplicialComplex s = new SimplicialComplex();
		
		// ... and populate it
		
		// first set the vertices
		s.setVertices(vertices);

		// set the edges
		s.setEdgesVertices(edgesVertices);
		
		// calculate the faces
		s.inferFacesFromEdges();
		
		// calculate the simplices
		s.inferSimplicesFromFaces();
		
		return s;
	}
	
	//
	// getters & setters
	//

	public Vector3D[] getVertices()
	{
		return vertices;
	}

	/**
	 * Set the vertices.
	 * Generally, this is done first when constructing a simplicial complex. 
	 * @param vertices	a (non-null) array of Vector3Ds that describes the positions of the vertices
	 * @throws InconsistencyException 
	 */
	public void setVertices(Vector3D[] vertices) throws InconsistencyException
	{
		// check that vertices is not null
		if(vertices == null) throw new InconsistencyException("Array of vertices should not be null, but it is.");
		
		// set this simplicial complex's vertices to vertices
		this.vertices = vertices;
	}

	public int[][] getEdgesVertices() {
		return edgesVertices;
	}

	/**
	 * Set the edges and perform basic consistency checks.
	 * The vertices have to be set first!
	 * @param edges	a (non-null) array of int[]s, each containing the indices of the vertices at the two ends of one edge
	 * @throws InconsistencyException 
	 */
	public void setEdgesVertices(int[][] edgesVertices) throws InconsistencyException
	{
		// check that edgesVertices is not null
		if(edgesVertices == null) throw new InconsistencyException("Array <edgesVertices> should not be null, but it is.");

		// set this simplicial complex's edgesVertices to edgesVertices
		this.edgesVertices = edgesVertices;
		
		// perform a few basic consistency checks
		checkEdgesVertices();
	}

	public int[][] getFacesVertices() {
		return facesVertices;
	}

	public void setFacesVertices(int[][] facesVertices) throws InconsistencyException
	{
		// check that facesVertices is not null
		if(facesVertices == null) throw new InconsistencyException("Array <facesVertices> should not be null, but it is.");

		// set this simplicial complex's facesVertices to facesVertices
		this.facesVertices = facesVertices;
	}

	public int[][] getFacesEdges() {
		return facesEdges;
	}


	public void setFacesEdges(int[][] facesEdges) throws InconsistencyException
	{
		// check that facesEdges is not null
		if(facesEdges == null) throw new InconsistencyException("Array <facesEdges> should not be null, but it is.");

		// set this simplicial complex's facesEdges to facesEdges
		this.facesEdges = facesEdges;
	}


	public int[][] getSimplicesVertices() {
		return simplicesVertices;
	}

	public void setSimplicesVertices(int[][] simplicesVertices) throws InconsistencyException
	{
		// check that simplicesVertices is not null
		if(simplicesVertices == null) throw new InconsistencyException("Array <simplicesVertices> should not be null, but it is.");

		// set this simplicial complex's simplicesVertices to simplicesVertices
		this.simplicesVertices = simplicesVertices;
	}


	public int[][] getSimplicesFaces() {
		return simplicesFaces;
	}


	public void setSimplicesFaces(int[][] simplicesFaces) throws InconsistencyException
	{
		// check that simplicesFaces is not null
		if(simplicesFaces == null) throw new InconsistencyException("Array <simplicesFaces> should not be null, but it is.");

		// set this simplicial complex's simplicesFaces to simplicesFaces
		this.simplicesFaces = simplicesFaces;
	}
	
	
	//
	// checks
	//

	/**
	 * Perform a few consistency checks of <edgesVertices>
	 * @throws InconsistencyException 
	 */
	public void checkEdgesVertices() throws InconsistencyException
	{
		if(edgesVertices != null)
		{
			// do a few checks

			// check that each edge contains two ints
			if(edgesVertices.length > 0)
			{
				// there is at least one edge; check it
				if(edgesVertices[0].length != 2)
				{
					// the length of the edge is wrong
					
					// throw an InconsistencyException
					throw(new InconsistencyException("<edges> must be an array of pairs of integers."));
				}
			}

			// check that all vertices are referenced at least three times, as each is the vertex of at least one simplex
			// create an array of ints that will hold the number of references to each vertex in the list of edges...
			int[] vertexCount = new int[vertices.length];
			// ... and set all these reference counts initially to zero
			for(int i=0; i<vertexCount.length; i++) vertexCount[i] = 0;
			// go through all edges...
			for(int i=0; i<edgesVertices.length; i++)
			{
				// and check that the two vertex indices aren't the same
				if(edgesVertices[i][0] == edgesVertices[i][1])
				{
					// oh dear, the two vertex indices are the same

					// throw an InconsistencyException
					throw(new InconsistencyException("The vertices that form edge " + i + " should be different, but instead both are " + edgesVertices[i][0] + "."));
				}
				
				// ... and increase the reference count of the vertex at the start by 1...
				vertexCount[edgesVertices[i][0]]++;
				// ... and at the end by 1
				vertexCount[edgesVertices[i][1]]++;
				// note that any vertex indices that are out of bounds will throw up an error here!
			}
			// check that all reference counts are >= 3
			for(int i=0; i<vertexCount.length; i++)
			{
				if(vertexCount[i] < 3)
				{
					// vertex i is referenced fewer than 3 times
					
					// throw an InconsistencyException
					throw(new InconsistencyException("Vertex #" + i + " should be referenced in the list of edges >= 3 times, but is referenced only " + vertexCount[i] + " times."));
				}
			}
		}
	}
	
	/**
	 * Perform a few consistency checks of <facesVertices>
	 * @throws InconsistencyException 
	 */
	public void checkFacesVertices() throws InconsistencyException
	{
		if(facesVertices != null)
		{
			// do a few checks

			// check that each face contains three vertices
			if(facesVertices.length > 0)
			{
				// there is at least one face; check it
				if(facesVertices[0].length != 3)
				{
					// the number of vertices in the face is wrong
					
					// throw an InconsistencyException
					throw(new InconsistencyException("<facesVertices> must be an array of triples of integers, but isn't."));
				}
			}

			// check that all edges are referenced at least two times, as each is the edge of at least one simplex
			// create an array of ints that will hold the number of references to each edge in the list of faces...
			int[] edgeCount = new int[edgesVertices.length];
			// ... and set all these reference counts initially to zero
			for(int i=0; i<edgeCount.length; i++) edgeCount[i] = 0;
			// go through all faces...
			for(int i=0; i<facesVertices.length; i++)
			{
				// and check that the two vertex indices aren't the same
				if(facesVertices[i][0] == facesVertices[i][1])
				{
					// oh dear, the two vertex indices are the same

					// throw an InconsistencyException
					throw(new InconsistencyException("The vertices that form edge " + i + " should be different, but instead both are " + edgesVertices[i][0] + "."));
				}
				
				// ... and increase the reference count of the vertex at the start by 1...
				vertexCount[edgesVertices[i][0]]++;
				// ... and at the end by 1
				vertexCount[edgesVertices[i][1]]++;
				// note that any vertex indices that are out of bounds will throw up an error here!
			}
			// check that all reference counts are >= 3
			for(int i=0; i<vertexCount.length; i++)
			{
				if(vertexCount[i] < 3)
				{
					// vertex i is referenced fewer than 3 times
					
					// throw an InconsistencyException
					throw(new InconsistencyException("Vertex #" + i + " should be referenced in the list of edges >= 3 times, but is referenced only " + vertexCount[i] + " times."));
				}
			}
		}
	}
	
	//
	// infer information
	//
	
	/**
	 * create <facesEdges> and <facesVertices> from <edgesVertices>
	 * @throws InconsistencyException 
	 */
	public void inferFacesFromEdges() throws InconsistencyException
	{
		// create an ArrayList that will be an ArrayList of the
		ArrayList<int[]> facesEdges = new ArrayList<int[]>();
		ArrayList<int[]> facesVertices = new ArrayList<int[]>();

		// go through all vertices
		for(int v=0; v<vertices.length; v++)
		{
			// first find all edges with vertex #v and other vertex #w, where w>v
			// (if w<v, then that face will already have been detected earlier)...
			
			// ... by creating an array that will hold the edge indices...
			ArrayList<Integer> edgesAtVertex = new ArrayList<Integer>();
			
			// ... and populating it by going through all the edges
			for(int e=0; e<edgesVertices.length; e++)
			{
				// does edge #e start or finish at vertex #v, and if so is the index of the other vertex >v?
				if(otherVertexOfEdge(e, v) > v)
				{
					// yes
					
					// add it to the list of edges that start or finish at this vertex
					edgesAtVertex.add(e);
				}
			}
			
			// second, go through all pairs of edges that start or finish at vertex #v;
			// each such pair represents two of the three edges of a face that meets at vertex #v
			for(int e1=0; e1<edgesAtVertex.size(); e1++)
				for(int e2=e1+1; e2<edgesAtVertex.size(); e2++)
				{
					// the array of vertex indices for this face
					int[] faceVertices = new int[3];
				
					// of course, vertex #v is one of the indices for this face
					faceVertices[0] = v;
					
					// find the indices of the other vertices involved in the face
					faceVertices[1] = otherVertexOfEdge(edgesAtVertex.get(e1), v);
					faceVertices[2] = otherVertexOfEdge(edgesAtVertex.get(e2), v);
					
					// this means that there is a face that involves vertices v, ov1, and ov2
					
					// add that face to the list <facesVertices>
					facesVertices.add(faceVertices);
					
					// construct the array of edge indices for this face
					int[] faceEdges = new int[3];
					
					// of course, edges #edgesAtVertex.get(e1) and #edgesAtVertex.get(e2) are edges for this face
					faceEdges[0] = edgesAtVertex.get(e1);
					faceEdges[1] = edgesAtVertex.get(e2);
					
					// find the edge that involves the two "other" vertices
					faceEdges[2] = findEdgeWithVertices(faceVertices[1], faceVertices[2]);
					
					// add that face to the list <facesEdges>
					facesEdges.add(faceEdges);
				}
		}
		
		// set the eponymous arrays to contain the elements of the ArrayLists <facesVertices> and <facesEdges>
		// that were just created
		this.facesVertices = facesVertices.toArray(this.facesVertices);
		this.facesEdges = facesEdges.toArray(this.facesEdges);
		
//		this.facesVertices = new int[facesVertices.size()][3];
//		this.facesEdges = new int[facesEdges.size()][3];
//		for(int f=0; f<facesVertices.size(); f++)
//		{
//			for(int i=0; i<3; i++)
//			{
//				this.facesVertices[f][i] = facesVertices.get(f)[i];
//				this.facesEdges[f][i] = facesEdges.get(f)[i];
//			}
//		}
	}

	/**
	 * create <simplicesFaces> and <simplicesVertices> from faces
	 * @throws InconsistencyException 
	 */
	public void inferSimplicesFromFaces() throws InconsistencyException
	{
		// create ArrayLists that will hold the information about the simplices
		ArrayList<int[]> simplicesFaces = new ArrayList<int[]>();
		ArrayList<int[]> simplicesVertices = new ArrayList<int[]>();

		// go through all vertices
		for(int v=0; v<vertices.length; v++)
		{
			// first find all faces with vertex #v and other vertices #v1 and #v2, where v1, v2 > v
			// (if v1, v2 < v, then the simplices we are finding will already have been found earlier)...
			
			// ... by creating an array that will hold the face indices...
			ArrayList<Integer> facesAtVertex = new ArrayList<Integer>();
			
			// ... and populating it by going through all the faces
			for(int f=0; f<facesVertices.length; f++)
			{
				// is vertex #v one of the vertices of face #f, and if so are the indices of the other vertex >v?
				if(smallerOtherVertexOfFace(f, v) > v)
				{
					// yes
					
					// add it to the list of faces that meet at this vertex
					facesAtVertex.add(f);
				}
			}
			
			// second, go through all triples of faces that start or finish at vertex #v;
			// each such triple represents three of the four faces of a simplex with vertex #v
			for(int f1=0; f1<facesAtVertex.size(); f1++)
				for(int f2=f1+1; f2<facesAtVertex.size(); f2++)
					for(int f3=f2+1; f3<facesAtVertex.size(); f3++)
					{
						// we found a new simplex!
						
						Set<Integer> simplexVertices = new HashSet<Integer>(4);
						
						// add all all three vertices of the three faces with indices f1, f2, f3
						for(int i=0; i<3; i++)
						{
							simplexVertices.add(facesVertices[f1][i]);
							simplexVertices.add(facesVertices[f2][i]);
							simplexVertices.add(facesVertices[f3][i]);
						}
						
						// add that face to the list <simplicesVertices>
						simplicesVertices.add(simplexVertices.toArray());

						// construct the array of face indices for this simplex
						int[] simplexFaces = new int[4];

						// of course, faces #facesAtVertex.get(f1 to f3) are faces of this simplex
						simplexFaces[0] = facesAtVertex.get(f1);
						simplexFaces[1] = facesAtVertex.get(f2);
						simplexFaces[2] = facesAtVertex.get(f3);

						// find the face that involves the three "other" vertices
						simplexFaces[3] = findFaceWithVertices(faceVertices[1], faceVertices[2]);

						// add that face to the list <facesEdges>
						simplicesFaces.add(simplexFaces);
					}
		}
		
		// set the eponymous arrays to contain the elements of the ArrayLists <simplicesVertices> and <simplicesEdges>
		// that were just created
		this.simplicesVertices = simplicesVertices.toArray(this.simplicesVertices);
		this.simplicesFaces = simplicesFaces.toArray(this.simplicesFaces);
	}

	

	/**
	 * @param edgeIndex
	 * @param vertexIndex1	index of one of the two vertices of this edge
	 * @return	the index of the other vertex or -1 if vertex #vertexIndex1 is not actually one of the end vertices of edge #edgeIndex
	 */
	public int otherVertexOfEdge(int edgeIndex, int oneVertexIndex)
	{
		if(edgesVertices[edgeIndex][0] == oneVertexIndex) return edgesVertices[edgeIndex][1];
		else if(edgesVertices[edgeIndex][1] == oneVertexIndex) return edgesVertices[edgeIndex][0];
		else return -1;
	}

	/**
	 * @param faceIndex
	 * @param oneVertexIndex	index of one of the three vertices of this face
	 * @return	the smaller one of the indices of the other vertices, or -1 if vertex #oneVertexIndex is not actually one of the end vertices of face #faceIndex
	 */
	public int smallerOtherVertexOfFace(int faceIndex, int oneVertexIndex)
	{
		// find the index in the list of vertices of this face that is <oneVertexIndex>
		int i;
		for(i=2; (i>=0) && (facesVertices[faceIndex][i] != oneVertexIndex); i--);
		
		if(i < 0) 
			// oneVertexIndex is not actually in the list of vertex indices for this face
			return -1;
		
		// find the smaller one of the other two vertex indices
		int smallerVertexIndex = vertices.length;	// initiate to a value larger than largest possible value
		// go through the list of vertex indices for this face
		for(int j=0; j<3; j++)
		{
			if(i!=j)
			{
				// this index is one of the two that is not oneVertexIndex
				if(facesVertices[faceIndex][j] < smallerVertexIndex) smallerVertexIndex = facesVertices[faceIndex][j];
			}
		}

		// return the smaller vertex index
		return smallerVertexIndex;
	}

	/**
	 * @param edgeIndex
	 * @param vertexIndex
	 * @return	true if edge #edgeIndex involves vertex #vertexIndex, false otherwise
	 */
	public boolean edgeInvolvesVertex(int edgeIndex, int vertexIndex)
	{
		return (
				// is vertex #vertexIndex the start vertex of edge #edgesIndex?
				(edgesVertices[edgeIndex][0] == vertexIndex) ||
				// is vertex #vertexIndex the end vertex of edge #edgesIndex?
				(edgesVertices[edgeIndex][1] == vertexIndex)
			);
	}

	/**
	 * @param vertexIndex1	index of the first vertex
	 * @param vertexIndex2	index of the second vertex
	 * @return the index of the edge that ends in the two vertices
	 * @throws InconsistencyException if there is no edge that involves those two indices
	 */
	public int findEdgeWithVertices(int vertexIndex1, int vertexIndex2) throws InconsistencyException
	{
		// go through all edges
		for(int i=0; i<edgesVertices.length; i++)
		{
			if(edgeInvolvesVertex(i, vertexIndex1) && edgeInvolvesVertex(i, vertexIndex2))
			{
				// edge #i involves both vertices; return it
				return i;
			}
		}
		
		// there is no edge that involves the two vertices
		throw new InconsistencyException("There should be an edge that involves vertices #" + vertexIndex1 + " and #" + vertexIndex2 + ", but there is none.");
	}
}
