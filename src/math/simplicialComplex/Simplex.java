package math.simplicialComplex;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

import math.Vector3D;
import optics.raytrace.exceptions.InconsistencyException;

/**
 * @author johannes
 * A (tetrahedral) simplex in a simplicial complex
 */
public class Simplex
implements Serializable
{
	private static final long serialVersionUID = 7962644036685151896L;

	/**
	 * the simplicial complex this edge is part of
	 */
	protected SimplicialComplex simplicialComplex;
	
	/**
	 * the indices of the four vertices of this tetrahedral simplex
	 */
	protected int[] vertexIndices;
	
	/**
	 * the indices of the four faces of this simplex
	 */
	protected int[] faceIndices;
	
	/**
	 * index of the outer-neighbour simplex, which is one of the neighbouring simplices,
	 * namely the one through which this simplex is connected to the outside by the fewest faces;
	 * IndexArray.OUTSIDE if the outer neighbour is the outside;
	 * IndexArray.NONE if not set
	 */
	protected int outerNeighbourSimplexIndex;
	
	/**
	 * index of the face that connects this simplex to its outer-neighbour simplex;
	 * IndexArray.NONE if not set
	 */
	protected int outerFaceIndex;

	/**
	 * Constructor that takes an array of vertex indices with exactly 4 elements
	 * @param vertexIndices
	 * @param faceIndices
	 * @param outerNeighbourSimplexIndex
	 * @param outerFaceIndex
	 * @throws InconsistencyException 
	 */
	public Simplex(SimplicialComplex simplicialComplex, int[] vertexIndices, int[] faceIndices, int outerNeighbourSimplexIndex, int outerFaceIndex)
	throws InconsistencyException
	{
		setSimplicialComplex(simplicialComplex);
		setVertexIndices(vertexIndices);
		setFaceIndices(faceIndices);
		setOuterNeighbourSimplexIndex(outerNeighbourSimplexIndex);
		setOuterFaceIndex(outerFaceIndex);
	}
	
	/**
	 * Constructor that creates a simplex in which the arrays vertexIndices and faceIndices are both null
	 * @param simplicialComplex
	 */
	public Simplex(SimplicialComplex simplicialComplex)
	{
		setSimplicialComplex(simplicialComplex);
		
		// indicate that the outer-neighbour-related information hasn't been set yet
		setOuterNeighbourSimplexIndex(IndexArray.NONE);
		setOuterFaceIndex(IndexArray.NONE);
	}
	
	public Simplex(Simplex original)
	throws InconsistencyException
	{
		this(
				original.getSimplicialComplex(),
				original.getVertexIndices(),
				original.getFaceIndices(),
				original.getOuterNeighbourSimplexIndex(),
				original.getOuterFaceIndex()
			);
	}
	
	public Simplex clone()
	{
		try {
			return new Simplex(this);
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
	 * Set the simplex's vertex indices
	 * @param vertexIndices	an array of exactly four ints, the vertex indices
	 * @throws InconsistencyException	if vertexIndices contains != 4 elements or if 2 or more elements are the same
	 */
	public void setVertexIndices(int[] vertexIndices)
	throws InconsistencyException
	{
		// check if there are exactly 4 vertices
		IndexArray.checkArrayLength(vertexIndices, 4);
		
		// check if the vertices are different
		IndexArray.checkElementsAreDifferent(vertexIndices);
		
		// everything is okay; set this.vertices
		this.vertexIndices = vertexIndices;
	}
	
	/**
	 * Set the simplex's vertices
	 * @param vertexIndex1
	 * @param vertexIndex2
	 * @param vertexIndex3
	 * @param vertexIndex4
	 * @throws InconsistencyException	if 2 or more vertex indices are the same
	 */
	public void setVertexIndices(int vertexIndex1, int vertexIndex2, int vertexIndex3, int vertexIndex4)
	throws InconsistencyException
	{
		int[] vertexIndices = new int[4];
		
		vertexIndices[0] = vertexIndex1;
		vertexIndices[1] = vertexIndex2;
		vertexIndices[2] = vertexIndex3;
		vertexIndices[3] = vertexIndex4;
		
		setVertexIndices(vertexIndices);
	}


	public int[] getFaceIndices() {
		return faceIndices;
	}

	/**
	 * Set the simplex's face indices
	 * @param faceIndices	an array of exactly four ints, the face indices
	 * @throws InconsistencyException	if faceIndices contains != 4 elements or if 2 or more elements are the same
	 */
	public void setFaceIndices(int[] faceIndices)
	throws InconsistencyException
	{
		// check if there are exactly 4 faces
		IndexArray.checkArrayLength(faceIndices, 4);
		
		// check if the faces are different
		IndexArray.checkElementsAreDifferent(faceIndices);
		
		// everything is okay; set this.faceIndices
		this.faceIndices = faceIndices;
	}
	
	/**
	 * Set the simplex's faces
	 * @param faceIndex1
	 * @param faceIndex2
	 * @param faceIndex3
	 * @param faceIndex4
	 * @throws InconsistencyException	if 2 or more face indices are the same
	 */
	public void setFaceIndices(int faceIndex1, int faceIndex2, int faceIndex3, int faceIndex4)
	throws InconsistencyException
	{
		int[] faceIndices = new int[4];
		
		faceIndices[0] = faceIndex1;
		faceIndices[1] = faceIndex2;
		faceIndices[2] = faceIndex3;
		faceIndices[3] = faceIndex4;
		
		setVertexIndices(faceIndices);
	}

	/**
	 * @return
	 */
	public int getOuterNeighbourSimplexIndex() {
		return outerNeighbourSimplexIndex;
	}

	public void setOuterNeighbourSimplexIndex(int outerNeighbourSimplexIndex) {
		this.outerNeighbourSimplexIndex = outerNeighbourSimplexIndex;
	}

	public int getOuterFaceIndex() {
		return outerFaceIndex;
	}

	public void setOuterFaceIndex(int outerFaceIndex) {
		this.outerFaceIndex = outerFaceIndex;
	}
	
	
	
	//
	// convenient methods
	//
	
	public ArrayList<Edge> getEdges()
	{
		ArrayList<Edge> edges = new ArrayList<Edge>(6);
		
		// go through all the faces...
		for(int fi=0; fi<4; fi++)
		{
			Face f = getFace(fi);
			
			// and their edges...
			for(int ei=0; ei<3; ei++)
			{
				Edge e = f.getEdge(ei);
				
				// ... and if the current one isn't already in the list of edge indices...
				if(!edges.contains(e))
					// ... add it
					edges.add(e);
			}
		}
		
		return edges;
	}
	
	
	public int inferOuterFaceIndex()
	{
		// go through all faces whose number of faces to the outside has been set and return the one with the lowest number
		int smallestNoOfFacesToOutside = 1000000;	// TODO was -1, which doesn't make any sense!?
		int outerFaceIndex1 = IndexArray.NONE;
		for(int i=0; i<4; i++)
		{
			// get face #i's number of faces to the outside
			int noOfFacesToOutside = simplicialComplex.getFace(faceIndices[i]).getNoOfFacesToOutside();
			
			// does it actually have a number of faces to the outside?
			if(noOfFacesToOutside != IndexArray.NONE)
			{
				// yes, it does; compare it to the smallest number of faces to the outside
				if(outerFaceIndex1 == IndexArray.NONE)
				{
					// no smallest number of faces to the outside has been set yet; set it to this face's number of faces to the outside
					smallestNoOfFacesToOutside = noOfFacesToOutside;
					outerFaceIndex1 = faceIndices[i];
				}
				else
				{
					// there is already a smallest number of faces to the outside; is this one smaller?
					if(noOfFacesToOutside < smallestNoOfFacesToOutside)
					{
						smallestNoOfFacesToOutside = noOfFacesToOutside;
						outerFaceIndex1 = faceIndices[i];
					}
				}
			}
		}
		
		// return the smallest number of faces to the outside that was found, +1
		return outerFaceIndex1;
	}
	
	/**
	 * @return	the number of faces to the outside, inferred from the faces' number of faces to the outside; IndexArray.NONE if none can be inferred
	 */
	public int inferNoOfFacesToOutside()
	{
		int outerFaceIndex1 = inferOuterFaceIndex();
		if(outerFaceIndex1 != IndexArray.NONE)
		{
			return simplicialComplex.getFace(outerFaceIndex1).getNoOfFacesToOutside() + 1;
		}
		
		// no number of faces to the outside can be inferred
		return IndexArray.NONE;
	}

//	public int getNumberOfFacesToOutside()
//	{
//		// if the <i>outerFaceIndex</i> is set, it's easy
//		if(outerFaceIndex != IndexArray.NONE)
//			return simplicialComplex.getFace(outerFaceIndex).getNoOfFacesToOutside() + 1;
//		
//		// if it's not set, go through all faces whose number of faces to the outside has been set and return the lowest number
//		int smallestNoOfFacesToOutside = IndexArray.NONE;
//		for(int i=0; i<4; i++)
//		{
//			// get face #i's number of faces to the outside
//			int noOfFacesToOutside = simplicialComplex.getFace(faceIndices[i]).getNoOfFacesToOutside();
//			
//			// does it actually have a number of faces to the outside?
//			if(noOfFacesToOutside != IndexArray.NONE)
//			{
//				// yes, it does; compare it to the smallest number of faces to the outside
//				if(smallestNoOfFacesToOutside == IndexArray.NONE)
//				{
//					// no smallest number of faces to the outside has been set yet; set it to this face's number of faces to the outside
//					smallestNoOfFacesToOutside = noOfFacesToOutside;
//				}
//				else
//				{
//					// there is already a smallest number of faces to the outside; is this one smaller?
//					if(noOfFacesToOutside < smallestNoOfFacesToOutside) smallestNoOfFacesToOutside = noOfFacesToOutside;
//				}
//			}
//		}
//		
//		// return the smallest number of faces to the outside that was found, +1
//		return smallestNoOfFacesToOutside + 1;
//	}
	
	public Vector3D calculateCentroid()
	{
		return Vector3D.sum(getVertex(0), getVertex(1), getVertex(2), getVertex(3)).getProductWith(1/4.);
	}

	
	/**
	 * @param i
	 * @return	the <i>i</i>th vertex of this simplex
	 */
	public Vector3D getVertex(int i)
	{
		return simplicialComplex.getVertex(vertexIndices[i]);
	}
	
	/**
	 * @param i
	 * @return	the <i>i</i>th face of this simplex
	 */
	public Face getFace(int i)
	{
		return simplicialComplex.getFace(faceIndices[i]);
	}

	public ArrayList<Face> getFacesBetweenThisAndOutside()
	throws InconsistencyException
	{
		// first, find the outer face
		Face outerFace = simplicialComplex.getFace(outerFaceIndex);
		
		// then calculate the number of faces between the outer face and the outside
		ArrayList<Face> facesBetweenThisAndOutside = outerFace.getFacesBetweenThisAndOutside();
		
		// insert the outer face of this simplex at the beginning of this list
		facesBetweenThisAndOutside.add(0, outerFace);
		
		// and return that list
		return facesBetweenThisAndOutside;
	}
	
	//
	// infer missing information
	//
	
	/**
	 * infer the face indices from the vertex indices
	 * @throws InconsistencyException 
	 */
	public void inferFaceIndices()
	throws InconsistencyException
	{
		int[] faceIndices = new int[4];

		// go through all the combinations of three vertices
		int f=0;
		for(int v1=0; v1<4; v1++)
			for(int v2=v1+1; v2<4; v2++)
				for(int v3=v2+1; v3<4; v3++)
					// find the face with vertices #v1, #v2, and #v3
					faceIndices[f++] = simplicialComplex.getIndexOfFaceWithVertices(vertexIndices[v1], vertexIndices[v2], vertexIndices[v3]);
					
		setFaceIndices(faceIndices);
	}
	
	/**
	 * @return	an array of all four vertices of this simplex
	 */
	public Vector3D[] getVertices()
	{
		Vector3D[] vertices = new Vector3D[4];
		
		for(int i=0; i<4; i++) vertices[i] = getVertex(i);
		
		return vertices;
	}
	
	/**
	 * @param point
	 * @return	true if <point> is inside this simplex, false otherwise
	 */
	public boolean pointIsInsideSimplex(Vector3D point)
	{
//		System.out.println("Simplex::pointIsInsideSimplex: this="+toString());

		// first calculate the sum of the tetrahedron's vertex indices
		int simplexVertexIndexSum = 0;
		for(int i=0; i<4; i++) simplexVertexIndexSum += vertexIndices[i];
		
//		System.out.println("Simplex::pointIsInsideSimplex: vertexIndices="+Arrays.toString(vertexIndices));

		// go through all four faces of this simplex
		for(int i=0; i<4; i++)
		{
			// the <i>th face
			Face face = simplicialComplex.getFaces().get(faceIndices[i]);
			
			// work out which vertex of this simplex is not part of the <i>th face...
			
//			System.out.println("Simplex::pointIsInsideSimplex: face.getVertexIndices()="+Arrays.toString(face.getVertexIndices()));

			// ... by calculating the sum of the face's vertex indices...
			int faceVertexIndexSum = 0;
			for(int j=0; j<3; j++) faceVertexIndexSum += face.getVertexIndices()[j];

			// ... and taking the difference between the sum for the tretrahedron and that for the face
			int otherVertexIndex = simplexVertexIndexSum - faceVertexIndexSum;
			
			// get the corresponding vertex
			Vector3D otherVertex = simplicialComplex.getVertices().get(otherVertexIndex);
			
			// are <point> and the other vertex on the same side of the <i>th face?
			if(!face.pointsAreOnSameSide(point, otherVertex))
				// no, so the point is outside the simplex
				return false;
		}
		
		// <point> is on the same side of the other vertex of all four faces, so it lies inside the simplex
		return true;
	}
	
	/**
	 * @param someFaceIndices
	 * @return	the index of the first of this simplex's faces that is listed in <someFaceIndices>; IndexArray.NONE if none is listed
	 */
	public int getFaceIndexInList(ArrayList<Integer> someFaceIndices)
	{
		// go through the faces of this simplex...
		for(int i=0; i<4; i++)
			// ... and if their index is contained in <someFaceIndices>...
			if(someFaceIndices.contains(faceIndices[i]))
				// ... return the index of the listed face
				return faceIndices[i];
		
		// none of the faces of this simplex is listed in <someFaceIndices>
		return IndexArray.NONE;
	}
	
	/**
	 * @param faceIndex
	 * @return	true if <faceIndex> is the index of one of this simplex's faces, false otherwise
	 */
	public boolean isFace(int faceIndex)
	{
		return IndexArray.isInArray(faceIndex, faceIndices);
		
//		// go through all the faces of this simplex, ...
//		for(int i=0; i<4; i++)
//			// and if the index of the <i>th face is <faceIndex>...
//			if(faceIndices[i] == faceIndex)
//				// indicate that <faceIndex> is indeed the index of one of this simplex's faces
//				return true;
//		
//		// <faceIndex> is not the index of one of this simplex's faces
//		return false;
	}
	
	/**
	 * @param vertexIndices
	 * @return	the index of the first vertex of this simplex that is not in <i>vertexIndices</i>; -1 if there is none
	 */
	public int getFirstVertexIndexNotIn(int[] someVertexIndices)
	{
		// go through all the vertices of this simplex...
		for(int v=0; v<4; v++)
		{
			if(!IndexArray.isInArray(vertexIndices[v], someVertexIndices))
				return vertexIndices[v];
		}
		
		return -1;
	}
	
	/**
	 * @param vertexIndex
	 * @return	true if <vertexIndex> is the index of one of this simplex's vertices, false otherwise
	 */
	public boolean isVertex(int vertexIndex)
	{
		return IndexArray.isInArray(vertexIndex, vertexIndices);
	}
	
//	/**
//	 * @return	a list that contains the indices of all simplices that are nearest neighbours of this one (note that the OUTSIDE_INDEX will not be listed here)
//	 */
//	public ArrayList<Integer> getNeighbourSimplexIndices()
//	{
//		// create the list of simplex indices to which the this simplex's nearest neighbours' indices will be added...
//		ArrayList<Integer> nearestNeighbourSimplexIndices = new ArrayList<Integer>();
//		
//		// in a second, we will need this list of faces of this simplex
//		ArrayList<Integer> faceIndicesList = new ArrayList<Integer>();
//		for(int i=0; i<faceIndices.length; i++) faceIndicesList.add(faceIndices[i]);
//		
//		// go through all simplices...
//		for(int i=0; i<simplicialComplex.getSimplices().size(); i++)
//		{
//			Simplex simplex = simplicialComplex.getSimplices().get(i);
//			
//			// ... other than this one, ...
//			if(simplex != this)	
//			{
//				// ... and if it shares a face with this simplex...
//				if(simplex.getFaceIndexInList(faceIndicesList) > 0)
//				{
//					// ... then add it to the list of nearest-neighbour-simplex indices
//					nearestNeighbourSimplexIndices.add(i);
//				}
//			}
//		}
//		
//		// return the list of nearest-neighbour-simplex indices
//		return nearestNeighbourSimplexIndices;
//	}
	
	/**
	 * @return	an array that contains the indices of all simplices that are nearest neighbours of this one (<i>f</i>th element = index of simplex on the other side of <i>f</i>th face)
	 */
	public int[] getNeighbourSimplexIndices()
	{
		// first take note of this simplex's index
		int t = simplicialComplex.getSimplexIndex(this);
		
		// System.out.println("Simplex::getNeighbourSimplexIndices: t = "+t);
	
		// first prepare the array of nearest-neighbour indices;
		// pre-populate this with the values IndexArray.OUTSIDE, as the nearest neighbour is the outside if no simplex that shares a face can be found
		int[] neighbours = new int[4];
		for(int n=0; n<4; n++) neighbours[n] = IndexArray.OUTSIDE;
		
		// go through all the faces of this simplex, ...
		for(int f=0; f<4; f++)
		{
			// ... and find a simplex that also contains the <f>th face, faceIndices[f].
			
			// To do this, go through all simplices (<i>s</i> is the index of the current simplex)...
			int s=0;
			while(
					(s < simplicialComplex.getSimplices().size()) && 
					(s != t) &&	// the simplex with index <s> is not this one
					(!simplicialComplex.getSimplices().get(s).isFace(faceIndices[f])) // the simplex with index <s> does not have the required face
				) s++;
			// ... and if one is found that isn't this one and shares the face with index faceIndices[f]...
			if(s < simplicialComplex.getSimplices().size())
			{
				neighbours[f] = s;
			}
		}
		
		// finished, return the array
		return neighbours;
	}

	
	/**
	 * @param otherSimplex
	 * @return	the index of the face shared between this and <otherSimplex>; IndexArray.NONE if there is none
	 */
	public int sharedFaceIndex(Simplex otherSimplex)
	{
		// create a list of the indices of all faces of this simplex...
		ArrayList<Integer> faceIndicesList = new ArrayList<Integer>();
		for(int i=0; i<faceIndices.length; i++) faceIndicesList.add(faceIndices[i]);

		// ... and return the first that is common with <otherSimplex>
		return otherSimplex.getFaceIndexInList(faceIndicesList);
	}

	@Override
	public String toString() {
		return "Simplex [vertexIndices=" + Arrays.toString(vertexIndices) + ", faceIndices="
				+ Arrays.toString(faceIndices) + ", outerNeighbourSimplexIndex=" + outerNeighbourSimplexIndex
				+ ", outerFaceIndex=" + outerFaceIndex + "]";
	}


}
