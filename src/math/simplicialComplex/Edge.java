package math.simplicialComplex;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

import optics.raytrace.exceptions.InconsistencyException;

/**
 * @author johannes
 * An edge in a simplicial complex
 */
/**
 * @author johannes
 *
 */
public class Edge
implements Serializable
{
	private static final long serialVersionUID = 6898455221262119767L;

	/**
	 * the simplicial complex this edge is part of
	 */
	protected SimplicialComplex simplicialComplex;
	
	/**
	 * the indices of the vertices that are the two end points
	 */
	protected int[] vertexIndices;
	
	
	//
	// constructors
	//
	
	/**
	 * Constructor that sets the simplicial complex to null and leaves the array of vertex indices null
	 */
	public Edge()
	{
		super();
		
		setSimplicialComplex(null);
	}
			
	/**
	 * Constructor that sets the simplicial complex but leaves the array of vertex indices null
	 * @param simplicialComplex
	 */
	public Edge(SimplicialComplex simplicialComplex)
	{
		super();
		
		setSimplicialComplex(simplicialComplex);
	}
	
	/**
	 * Constructor that takes an array of vertex indices with exactly 2 elements
	 * @param vertexIndices
	 * @throws InconsistencyException 
	 */
	public Edge(SimplicialComplex simplicialComplex, int[] vertexIndices)
	throws InconsistencyException
	{
		this(simplicialComplex);
		
		setVertexIndices(vertexIndices);
	}
	
	/**
	 * Constructor that creates an edge from vertex #vertexIndex1 to vertex #vertexIndex2
	 * @param simplicialComplex
	 * @param vertexIndex1
	 * @param vertexIndex2
	 * @throws InconsistencyException
	 */
	public Edge(SimplicialComplex simplicialComplex, int vertexIndex1, int vertexIndex2)
	throws InconsistencyException
	{
		this(simplicialComplex);
		
		int[] vertexIndices = new int[2];
		vertexIndices[0] = vertexIndex1;
		vertexIndices[1] = vertexIndex2;
		
		setVertexIndices(vertexIndices);
	}

	/**
	 * Constructor that creates an edge from vertex #vertexIndex1 to vertex #vertexIndex2, and doesn't set the simplicial complex
	 * @param vertexIndex1
	 * @param vertexIndex2
	 * @throws InconsistencyException 
	 */
	public Edge(int vertexIndex1, int vertexIndex2)
	throws InconsistencyException
	{
		this();	// for the moment, set the simplicial complex to null
		
		int[] vertexIndices = new int[2];
		vertexIndices[0] = vertexIndex1;
		vertexIndices[1] = vertexIndex2;
		
		setVertexIndices(vertexIndices);
	}
	
//	/**
//	 * Create a copy of the original
//	 * @param original
//	 * @throws InconsistencyException 
//	 */
//	public Edge(Edge original)
//	throws InconsistencyException
//	{
//		this(original.getSimplicialComplex(), original.getVertexIndices()[0], original.getVertexIndices()[1]);
//	}
	
	/**
	 * Create a copy of the original.
	 * Note that the copy shares the array vertexIndices with the original.
	 * @param original
	 */
	public Edge(Edge original)
	{
		super();
		
		this.simplicialComplex = original.getSimplicialComplex();
		this.vertexIndices = original.getVertexIndices();
	}

	
	public Edge clone()
	{
		return new Edge(this);
//		try
//		{
//			return new Edge(this);
//		}
//		catch (InconsistencyException e)
//		{
//			e.printStackTrace();
//			return null;
//		}
	}
	
	
	//
	// getters & setters
	//
	
	public SimplicialComplex getSimplicialComplex() {
		return simplicialComplex;
	}


	public void setSimplicialComplex(SimplicialComplex simplicialComplex) {
		this.simplicialComplex = simplicialComplex;
	}


	public int[] getVertexIndices() {
		return vertexIndices;
	}
	
	/**
	 * @param oneVertexIndex
	 * @return	the other vertex index; IndexArray.NONE if <oneVertexIndex> is not actually one of the vertices of this edge
	 */
	public int getOtherVertexIndex(int oneVertexIndex)
	{
		// go through both vertices of this edge...
		for(int i=0; i<2; i++)
			// ... and check if the current one is <oneVertexIndex>, ...
			if(vertexIndices[i] == oneVertexIndex)
				// ... in which case return the other
				return vertexIndices[1-i];
		
		// if nothing has been returned by now, <oneVertexIndex> is not actually in the list of vertex indices
		return IndexArray.NONE;
	}

	/**
	 * Set the edge's vertex indices
	 * @param vertexIndices	an array of exactly two ints, the vertex indices
	 * @throws InconsistencyException
	 */
	public void setVertexIndices(int[] vertexIndices)
	throws InconsistencyException
	{
		// check if there are exactly 2 vertices
		IndexArray.checkArrayLength(vertexIndices, 2);
		
		// check if the vertices are different
		IndexArray.checkElementsAreDifferent(vertexIndices);
		
		// everything is okay; set this.vertices
		this.vertexIndices = vertexIndices;
	}
	
	/**
	 * Set the edge's vertices
	 * @param vertexIndex1
	 * @param vertexIndex2
	 * @throws InconsistencyException
	 */
	public void setVertexIndices(int vertexIndex1, int vertexIndex2)
	throws InconsistencyException
	{
		int[] vertexIndices = new int[2];
		
		vertexIndices[0] = vertexIndex1;
		vertexIndices[1] = vertexIndex2;
		
		setVertexIndices(vertexIndices);
	}
	
	/**
	 * @param vertexIndex1
	 * @param vertexIndex2
	 * @return	true if <vertexIndex1> and <vertexIndex2> are the vertex indices of this edge, false otherwise
	 */
	public boolean vertexIndicesAre(int vertexIndex1, int vertexIndex2)
	{
		return (
				(Math.min(vertexIndices[0], vertexIndices[1]) == Math.min(vertexIndex1, vertexIndex2)) &&
				(Math.max(vertexIndices[0], vertexIndices[1]) == Math.max(vertexIndex1, vertexIndex2))
			);
	}
	
	
	/**
	 * @return	a list of the indices of all faces intersecting at this edge
	 */
	public ArrayList<Integer> getIntersectingFaceIndices()
	{
		// first find the index of this edge
		int edgeIndex = simplicialComplex.getEdgeIndex(this);
		
		// create an ArrayList into which the indices of the faces intersecting at this edge will go
		ArrayList<Integer> intersectingFaceIndices = new ArrayList<Integer>();
		
		// fill that ArrayList by going through all faces, ...
		for(int f=0; f<simplicialComplex.getFaces().size(); f++)
		{
			// ... checking each face if this edge is one of its edges, ...
			if(simplicialComplex.getFaces().get(f).isEdgeOfThisFace(edgeIndex))
			{
				// ... and adding those faces for which this is the case
				intersectingFaceIndices.add(f);
			}
		}
		
		// return the list of intersectingFaceIndices
		return intersectingFaceIndices;
	}


	@Override
	public String toString() {
		return "Edge [vertexIndices=" + Arrays.toString(vertexIndices)
				+ "]";
	}
	
}
