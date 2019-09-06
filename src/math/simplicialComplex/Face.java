package math.simplicialComplex;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import math.Vector3D;
import optics.raytrace.exceptions.InconsistencyException;

/**
 * @author johannes
 * A (triangular) face in a simplicial complex
 */
public class Face
implements Serializable
{
	private static final long serialVersionUID = 5264756735273680959L;

	/**
	 * the simplicial complex this edge is part of
	 */
	protected SimplicialComplex simplicialComplex;
	
	/**
	 * the indices of the three vertices of this triangular face
	 */
	protected int[] vertexIndices;
	
	/**
	 * the indices of the three edges of this triangular face
	 */
	protected int[] edgeIndices;
	
	/**
	 * the indices of the two simplices on either side of this face
	 */
	protected int[] simplexIndices;
	
	/**
	 * number of faces between this one and the outside;
	 * 0 if this is an outside face;
	 * -1 if not yet set
	 */
	protected int noOfFacesToOutside;
	
	
	//
	// constructors
	//
	
	/**
	 * Constructor that takes an array of vertex indices with exactly 3 elements
	 * @param vertexIndices
	 * @throws InconsistencyException 
	 */
	public Face(SimplicialComplex simplicialComplex, int[] vertexIndices, int[] edgeIndices, int[] simplexIndices, int noOfFacesToOutside)
	throws InconsistencyException
	{
		setSimplicialComplex(simplicialComplex);
		setVertexIndices(vertexIndices);
		setEdgeIndices(edgeIndices);
		setSimplexIndices(simplexIndices);
		setNoOfFacesToOutside(noOfFacesToOutside);
	}
	
	/**
	 * Constructor that creates a face with the lists of vertexIndices and edgeIndices both set to null.
	 * @param simplicialComplex
	 */
	public Face(SimplicialComplex simplicialComplex)
	{
		setSimplicialComplex(simplicialComplex);
		
		// mark noOfFacesToOutside to -1 (not set)
		setNoOfFacesToOutside(-1);
	}
	
	public Face(Face original)
	throws InconsistencyException
	{
		this(
				original.getSimplicialComplex(),
				original.getVertexIndices(),
				original.getEdgeIndices(),
				original.getSimplexIndices(),
				original.getNoOfFacesToOutside()
			);
	}
	
	public Face clone()
	{
		try {
			return new Face(this);
		} catch (InconsistencyException e) {
			e.printStackTrace();
			return null;
		}
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
	 * Set the face's vertex indices
	 * @param vertexIndices	an array of exactly three ints, the vertex indices
	 * @throws InconsistencyException	if vertexIndices contains != 3 elements or if 2 or more elements are the same
	 */
	public void setVertexIndices(int[] vertexIndices)
	throws InconsistencyException
	{
		// check if there are exactly 3 vertices
		IndexArray.checkArrayLength(vertexIndices, 3);
		
		// check if the vertices are different
		IndexArray.checkElementsAreDifferent(vertexIndices);
		
		// everything is okay; set this.vertices
		this.vertexIndices = vertexIndices;
	}
	
	/**
	 * Set the face's vertices
	 * @param vertexIndex1
	 * @param vertexIndex2
	 * @param vertexIndex3
	 * @throws InconsistencyException
	 */
	public void setVertexIndices(int vertexIndex1, int vertexIndex2, int vertexIndex3)
	throws InconsistencyException
	{
		// System.out.println("Face::setVertexIndices: vertex indices: " + vertexIndex1 + ", " + vertexIndex2 + ", " + vertexIndex3);

		int[] vertexIndices = new int[3];
		
		vertexIndices[0] = vertexIndex1;
		vertexIndices[1] = vertexIndex2;
		vertexIndices[2] = vertexIndex3;
		
		setVertexIndices(vertexIndices);
	}

	public int[] getEdgeIndices() {
		return edgeIndices;
	}

	/**
	 * Set the face's edge indices
	 * @param edgeIndices	an array of exactly three ints, the edge indices
	 * @throws InconsistencyException	if edgeIndices contains != 3 elements or if 2 or more elements are the same
	 */
	public void setEdgeIndices(int[] edgeIndices)
	throws InconsistencyException
	{
		// check if there are exactly 3 edges
		IndexArray.checkArrayLength(edgeIndices, 3);
		
		// check if the edges are different
		IndexArray.checkElementsAreDifferent(edgeIndices);
		
		// everything is okay; set this.edgeIndices
		this.edgeIndices = edgeIndices;
	}

	/**
	 * Set the face's edges
	 * @param edgeIndex1
	 * @param edgeIndex2
	 * @param edgeIndex3
	 * @throws InconsistencyException
	 */
	public void setEdgeIndices(int edgeIndex1, int edgeIndex2, int edgeIndex3)
	throws InconsistencyException
	{
		int[] edgeIndices = new int[3];
		
		edgeIndices[0] = edgeIndex1;
		edgeIndices[1] = edgeIndex2;
		edgeIndices[2] = edgeIndex3;
		
		setEdgeIndices(edgeIndices);
	}
	
	
	public int getNoOfFacesToOutside() {
		return noOfFacesToOutside;
	}

	public void setNoOfFacesToOutside(int noOfFacesToOutside) {
		this.noOfFacesToOutside = noOfFacesToOutside;
	}

	/**
	 * @param oneVertexIndex	index of one of the three vertices of this face
	 * @return	the smaller one of the indices of the other vertices, or IndexArray.NONE if vertex #oneVertexIndex is not actually one of the end vertices of this face
	 */
	public int getSmallerOtherVertexIndex(int oneVertexIndex)
	{
		// set i to the index in the list of vertices of this face that is <oneVertexIndex>,
		// or IndexArray.NONE if <oneVertexIndex> is not actually in the list of vertex indices
		int i;
		for(i=2; (i>=0) && (vertexIndices[i] != oneVertexIndex); i--);
		
		if(i < 0) 
			// <oneVertexIndex> is not actually in the list of vertex indices for this face
			return IndexArray.NONE;
		
		// find the smaller one of the other two vertex indices
		int smallerVertexIndex = vertexIndices.length;	// initiate to a value larger than largest possible value
		// go through the list of vertex indices for this face
		for(int j=0; j<3; j++)
		{
			if(i!=j)
			{
				// this index is one of the two that is not oneVertexIndex
				if(vertexIndices[j] < smallerVertexIndex) smallerVertexIndex = vertexIndices[j];
			}
		}

		// return the smaller vertex index
		return smallerVertexIndex;
	}

	/**
	 * @return	the indices of the two simplices on either side of this face
	 */
	public int[] getSimplexIndices() {
		return simplexIndices;
	}

	/**
	 * set the indices of the two simplices on either side of this face
	 */
	public void setSimplexIndices(int[] simplexIndices) {
		this.simplexIndices = simplexIndices;
	}
	
	public Vector3D calculateCentroid()
	{
		return Vector3D.sum(getVertex(0), getVertex(1), getVertex(2)).getProductWith(1/3.);
	}
	
	/**
	 * @return	a normalised, outwards-facing, normal to the face
	 * @throws InconsistencyException	if this is neither an outside face nor is any of the neighbouring faces separated from the outside by fewer faces
	 */
	public Vector3D calculateOutwardsNormal()
	throws InconsistencyException
	{
		// first, find a normal by taking the cross product of two side vectors 
		Vector3D normal = Vector3D.crossProduct(
				Vector3D.difference(getVertex(1), getVertex(0)),
				Vector3D.difference(getVertex(2), getVertex(0))
			);
	
		// next, check if it is pointing inwards or outwards
		// do this by calculating the vector from the centroid of the inner neighbouring simplex to the centroid of this face,
		// which points outwards, ...
		Vector3D outwards = Vector3D.difference(
				calculateCentroid(),	// centroid of this face
				simplicialComplex.getSimplex(getInnerNeighbouringSimplexIndex()).calculateCentroid()	// centroid of the inner neighbouring simplex
			);
		
		// ... and then comparing the normal to this outwards-facing direction
		return normal.getWithLength(Math.signum(Vector3D.scalarProduct(normal, outwards)));
	}

	/**
	 * infer the edge indices from the vertex indices
	 * @throws InconsistencyException 
	 */
	public void inferEdgeIndices()
	throws InconsistencyException
	{
		int[] edgeIndices = new int[3];

		// create edges that connect all vertices
		for(int i=0; i<3; i++)
			// find the edge that connects the ith with the (i+1)st vertex (modulo 3)
			edgeIndices[i] = simplicialComplex.getIndexOfEdgeWithVertices(vertexIndices[i], vertexIndices[(i+1)%3]);
		
		setEdgeIndices(edgeIndices);
	}
	
	/**
	 * @param vertexIndex1
	 * @param vertexIndex2
	 * @param vertexIndex3
	 * @return	true if <vertexIndex1>, <vertexIndex2>, and  <vertexIndex3> are the vertex indices of this face, false otherwise
	 */
	public boolean vertexIndicesAre(int vertexIndex1, int vertexIndex2, int vertexIndex3)
	{
		Set<Integer> vertexIndicesThis = new HashSet<Integer>();
		Set<Integer> vertexIndicesTest = new HashSet<Integer>();

		// add all vertex indices of this face to <vertexIndicesThis>
		for(int i=0; i<vertexIndices.length; i++) vertexIndicesThis.add(vertexIndices[i]);
		
		vertexIndicesTest.add(vertexIndex1);
		vertexIndicesTest.add(vertexIndex2);
		vertexIndicesTest.add(vertexIndex3);

		return (vertexIndicesThis.equals(vertexIndicesTest));
	}

	/**
	 * @param i
	 * @return	the <i>i</i>th vertex
	 */
	public Vector3D getVertex(int i)
	{
		return simplicialComplex.getVertices().get(vertexIndices[i]);
	}
	
	/**
	 * @param i
	 * @return	the <i>i</i>th edge
	 */
	public Edge getEdge(int i)
	{
		return simplicialComplex.getEdges().get(edgeIndices[i]);
	}
	
	/**
	 * @param i
	 * @return	the <i>i</i>th simplex
	 */
	public Simplex getSimplex(int i)
	{
		return simplicialComplex.getSimplices().get(simplexIndices[i]);
	}
	
	/**
	 * @return	an array of all three vertices of this face
	 */
	public Vector3D[] getVertices()
	{
		Vector3D[] vertices = new Vector3D[3];
		
		for(int i=0; i<3; i++) vertices[i] = getVertex(i);
		
		return vertices;
	}
	
	/**
	 * @param edgeIndex
	 * @return	true if <edgeIndex> is the index of one of this face's edges, false otherwise
	 */
	public boolean isEdgeOfThisFace(int edgeIndex)
	{
		// go through all the edges of this face, ...
		for(int i=0; i<3; i++)
			// and if the index of the <i>th edge is <edgeIndex>...
			if(edgeIndices[i] == edgeIndex)
				// indicate that <edgeIndex> is indeed the index of one of this face's edges
				return true;
		
		// <edgeIndex> is not the index of one of this face's edges
		return false;
	}
	
	/**
	 * @return	the index of this face
	 */
	public int getIndex()
	{
		return simplicialComplex.getFaceIndex(this);
	}
	
	
	/**
	 * Return a normal to the face.
	 * Note that this normal is not necessarily normalised, nor is it necessarily pointing outwards (or inwards).
	 * @return	a normal to the face
	 */
	public Vector3D getNormal()
	{
		return Vector3D.crossProduct(
				Vector3D.difference(getVertex(1), getVertex(0)),
				Vector3D.difference(getVertex(2), getVertex(0))
			);
	}

	
	/**
	 * @param point1
	 * @param point2
	 * @return	true if the two points are on the same side of this face, false otherwise
	 */
	public boolean pointsAreOnSameSide(Vector3D point1, Vector3D point2)
	{
		// first calculate the vertices
		Vector3D[] vertices = getVertices();
		
		// first calculate a normal to this (triangular) face
		Vector3D normal = getNormal();
		
		// calculate (point1 - vertices[0]).normal...
		double dot1 = Vector3D.scalarProduct(Vector3D.difference(point1, vertices[0]), normal);
		
		// ... and (point2 - vertices[0]).normal
		double dot2 = Vector3D.scalarProduct(Vector3D.difference(point2, vertices[0]), normal);
		
		// the points are on the same side of this face if those two dot products have the same sign, i.e. if
		return (dot1*dot2) >= 0;
	}
	
	/**
	 * If both neighbouring simplices are separated from the outside by the same number of faces, return the lesser one of the
	 * indices of these neighbouring simplices.
	 * @return	index of outer neighbouring simplex; OUTSIDE if the outer neighbour is the outside
	 * @throws InconsistencyException	if this is neither an outside face nor is any of the neighbouring faces separated from the outside by fewer faces
	 */
	public int getOuterNeighbouringSimplexIndex()
	throws InconsistencyException
	{
		// first check if this is an outside face...
		if(noOfFacesToOutside == 0)
		{
			// ... and if it is, return IndexArray.OUTSIDE to indicate this
			return IndexArray.OUTSIDE;
		}

		// go through the two neighbouring simplices, starting (for consistency) with the one with the lowest index, ...
		int[] orderedSimplexIndices = new int[2];
		orderedSimplexIndices[0] = Math.min(simplexIndices[0], simplexIndices[1]);
		orderedSimplexIndices[1] = Math.max(simplexIndices[0], simplexIndices[1]);
		for(int s=0; s<2; s++)
		{
			// get the <i>s</i>th neighbouring simplex ...
			Simplex simplex = simplicialComplex.getSimplices().get(orderedSimplexIndices[s]);

			// ... and the number of faces to the outside of its outermost face, ...
			int noOfFacesToOutsideOfNeighbouringSimplex = simplicialComplex.faces.get(simplex.getOuterFaceIndex()).getNoOfFacesToOutside();

			// ... and if this number is less than this face's <i>noOfFacesToOutside</i>, then simplex #orderedSimplexIndices[s] is
			// the outermost neighbouring simplex
			if(noOfFacesToOutsideOfNeighbouringSimplex < noOfFacesToOutside)
				return orderedSimplexIndices[s];
		}
		
		System.err.println("Face::getOuterNeighbouringSimplexIndex: face #"+simplicialComplex.getFaceIndex(this)+" = "+this);
		System.err.println("Face::getOuterNeighbouringSimplexIndex: neighbouring simplex #"+this.getSimplexIndices()[0]+" = "+simplicialComplex.getSimplex(this.getSimplexIndices()[0]));
		System.err.println("Face::getOuterNeighbouringSimplexIndex: neighbouring simplex #"+this.getSimplexIndices()[1]+" = "+simplicialComplex.getSimplex(this.getSimplexIndices()[1]));
		
		System.err.println("Face::getOuterNeighbouringSimplexIndex: simplicial complex =");
		simplicialComplex.printNicely(System.err);
		
		// this isn't an outside face, and none of the neighbouring faces is separated from the outside by fewer faces than this one --- aaargh!
		throw new InconsistencyException(
				"Face #"+ simplicialComplex.getFaceIndex(this) +" isn't an outside face, and none of the neighbouring simplices is separated from the outside by fewer faces than this one."
			);
	}
	
	/**
	 * @return	index of the inner neighbouring simplex
	 * @throws InconsistencyException	if this is neither an outside face nor is any of the neighbouring faces separated from the outside by fewer faces
	 */
	public int getInnerNeighbouringSimplexIndex()
	throws InconsistencyException
	{
		return simplexIndices[(getOuterNeighbouringSimplexIndex() == simplexIndices[0])?1:0];
	}
	
	/**
	 * @return	one of the faces of the neighbouring simplices that are separated from the outside by 1 fewer faces; null if this is an outside face
	 * @throws InconsistencyException	if this is neither an outside face nor is any of the neighbouring faces separated from the outside by fewer faces
	 */
	public Face getOutermostNeighbouringFace()
	throws InconsistencyException
	{
		// first check if this is an outside face...
		if(noOfFacesToOutside == 0)
		{
			// ... and if it is, return null to indicate this
			return null;
		}
		
		// go through the two neighbouring simplices, starting (for consistency) with the one with the lowest index, ...
		int[] orderedSimplexIndices = new int[2];
		orderedSimplexIndices[0] = Math.min(simplexIndices[0], simplexIndices[1]);
		orderedSimplexIndices[1] = Math.max(simplexIndices[0], simplexIndices[1]);
		for(int s=0; s<2; s++)
		{
			Simplex simplex = simplicialComplex.getSimplices().get(orderedSimplexIndices[s]);
			
			// ... go through the four faces of that simplex, ...
			for(int f=0; f<4; f++)
			{
				Face face = simplex.getFace(f);
				
				// ... and return the first one that is separated from the outside by fewer faces
				if(face.getNoOfFacesToOutside() < noOfFacesToOutside)
				{
					return face;
				}
			}
		}
		
		// this isn't an outside face, and none of the neighbouring faces is separated from the outside by fewer faces than this one --- aaargh!
		throw new InconsistencyException(
				"This face, #"+ simplicialComplex.getFaceIndex(this) +", isn't an outside face, and none of the neighbouring faces is separated from the outside by fewer faces than this one!");
	}
	
	/**
	 * @return	a list of faces between this face and the outside (inside to outside)
	 * @throws InconsistencyException 
	 */
	public ArrayList<Face> getFacesBetweenThisAndOutside()
	throws InconsistencyException
	{
		// create an ArrayList for the faces to go into
		ArrayList<Face> facesToOutside = new ArrayList<Face>();
		
		for(Face face = getOutermostNeighbouringFace(); face != null; face = face.getOutermostNeighbouringFace())
		{
			facesToOutside.add(face);
		}
		
		return facesToOutside;
	}
	
	/**
	 * @return	a list of the indices of the faces between this face and the outside
	 * @throws InconsistencyException 
	 */
	public ArrayList<Integer> getIndicesOfFacesBetweenThisAndOutside()
	throws InconsistencyException
	{
		ArrayList<Integer> indicesOfFacesToOutside = new ArrayList<Integer>();
		
		for(Face face : getFacesBetweenThisAndOutside())
		{
			indicesOfFacesToOutside.add(simplicialComplex.getFaceIndex(face));
		}
		
		return indicesOfFacesToOutside;
	}
	

	@Override
	public String toString() {
		return "Face [vertexIndices=" + Arrays.toString(vertexIndices)
				+ ", edgeIndices=" + Arrays.toString(edgeIndices) + ", simplexIndices="
				+ Arrays.toString(simplexIndices) + ", noOfFacesToOutside=" + noOfFacesToOutside + "]";
	}

}
