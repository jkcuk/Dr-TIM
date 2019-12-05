package math.simplicialComplex;

import java.io.PrintStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;

import math.Vector3D;
import optics.raytrace.GUI.sceneObjects.EditableParametrisedCylinder;
import optics.raytrace.GUI.sceneObjects.EditableParametrisedTriangle;
import optics.raytrace.GUI.sceneObjects.EditableScaledParametrisedSphere;
import optics.raytrace.GUI.sceneObjects.EditableSceneObjectCollection;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.Studio;
import optics.raytrace.core.SurfaceProperty;
import optics.raytrace.exceptions.InconsistencyException;

/**
 * @author johannes
 * A simplicial complex.
 * In principle, a simplicial complex is completely determined by the vertex positions (which are stored in the array <vertices>)
 * and the indices of the vertices in either the edges, faces, or simplices.
 * But for speedy use later, lots of information can be pre-calculated.
 */
public class SimplicialComplex
implements Serializable
{
	private static final long serialVersionUID = -8198203701378540661L;

	/**
	 * the vertices of the simplicial complex
	 */
	protected ArrayList<? extends Vector3D> vertices;

	/**
	 * the edges of the simplicial complex
	 */
	protected ArrayList<? extends Edge> edges;
	
	/**
	 * the faces of the simplicial complex
	 */
	protected ArrayList<Face> faces;
	
	/**
	 * the simplices of the simplicial complex
	 */
	protected ArrayList<Simplex> simplices;

	
	//
	// constructors
	//
	
	/**
	 * Constructor for a simplicial complex that sets all internal variables directly
	 * @param vertices
	 * @param edges
	 * @param faces
	 * @param simplices
	 * @throws InconsistencyException	if consistency checks fail
	 */
	public SimplicialComplex(
			ArrayList<? extends Vector3D> vertices,
			ArrayList<? extends Edge> edges,
			ArrayList<Face> faces,
			ArrayList<Simplex> simplices
	)
	throws InconsistencyException
	{
		super();
		
		setVertices(vertices);
		setEdges(edges);
		setFaces(faces);
		setSimplices(simplices);
	}
	
	/**
	 * Constructor for a simplicial complex in which the vertices, edges, faces and simplices are all null.
	 */
	public SimplicialComplex()
	throws InconsistencyException
	{
		this(null, null, null, null);
	}
	
	/**
	 * Method for constructing a simplicial complex in which the vertices and edges are set directly and the rest is inferred
	 * @param vertices
	 * @param edges
	 * @throws InconsistencyException	if consistency checks fail
	 * @returns	a simplicial complex with the given vertices and edges
	 */
	public static SimplicialComplex getSimplicialComplexFromVerticesAndEdges(
			ArrayList<? extends Vector3D> vertices,
			ArrayList<? extends Edge> edges
	)
	throws InconsistencyException
	{
		SimplicialComplex s = new SimplicialComplex();
		
		s.setVertices(vertices);
		s.setEdges(edges);
		
		s.inferFacesFromEdges();
//		System.out.println("SimplicialComplex::getSimplicialComplexFromVerticesAndEdges: no of faces="+s.getFaces().size());
//		for(int f=0; f<s.getFaces().size(); f++)
//			System.out.println("SimplicialComplex::getSimplicialComplexFromVerticesAndEdges: face #"+f+"="+s.getFaces().get(f));
		
		s.inferSimplicesFromFaces();
//		System.out.println("SimplicialComplex::getSimplicialComplexFromVerticesAndEdges: no of simplices="+s.getSimplices().size());
//		for(int f=0; f<s.getSimplices().size(); f++)
//			System.out.println("SimplicialComplex::getSimplicialComplexFromVerticesAndEdges: simplex #"+f+"="+s.getSimplices().get(f));
		
		return s;
	}
	
//	@SuppressWarnings("unchecked")
//	public SimplicialComplex(SimplicialComplex original) throws InconsistencyException
//	{
//		this(
//				(ArrayList<? extends Vector3D>)(original.vertices.clone()),
//				(ArrayList<? extends Edge>)(original.edges.clone()),
//				(ArrayList<Face>)(original.faces.clone()),
//				(ArrayList<Simplex>)(original.simplices.clone())
//			);
//	}
	
	
	//
	// setters & getters
	//
	
	public ArrayList<? extends Vector3D> getVertices() {
		return vertices;
	}

	public void setVertices(ArrayList<? extends Vector3D> vertices) {
		this.vertices = vertices;
	}

	public ArrayList<? extends Edge> getEdges() {
		return edges;
	}

	/**
	 * Set the edges and perform basic consistency checks.
	 * The vertices must have been set previously.
	 * @param edges2
	 * @throws InconsistencyException	if the consistency checks fail
	 */
	public void setEdges(ArrayList<? extends Edge> edges)
	throws InconsistencyException
	{
		this.edges = edges;
		
		if(edges != null)
		{
			// make sure that all edges link back to this simplicial complex
			for(Edge edge : edges) edge.setSimplicialComplex(this);

			checkEdges();
		}
	}



	public ArrayList<Face> getFaces() {
		return faces;
	}



	/**
	 * Set the faces and perform basic consistency checks.
	 * The edges must have been set previously.
	 * @param faces
	 * @throws InconsistencyException	if the consistency checks fail
	 */
	public void setFaces(ArrayList<Face> faces)
	throws InconsistencyException
	{
		this.faces = faces;
		
		if(faces != null)
		{
			// make sure that all faces link back to this simplicial complex
			for(Face face : faces) face.setSimplicialComplex(this);

			checkFaces();
		}
	}



	public ArrayList<Simplex> getSimplices() {
		return simplices;
	}



	public void setSimplices(ArrayList<Simplex> simplices)
	{
		this.simplices = simplices;
		
		if(simplices != null)
		{
			// make sure that all simplices link back to this simplicial complex
			for(Simplex simplex : simplices) simplex.setSimplicialComplex(this);
		}
	}
	
	public Vector3D getVertex(int vertexIndex)
	{
		return vertices.get(vertexIndex);
	}

	public Edge getEdge(int edgeIndex)
	{
		return edges.get(edgeIndex);
	}

	public Face getFace(int faceIndex)
	{
		return faces.get(faceIndex);
	}

	public Simplex getSimplex(int simplexIndex)
	{
		return simplices.get(simplexIndex);
	}


	//
	// a few consistency checks
	//
	
	/**
	 * Perform a few consistency checks of the edges
	 * @throws InconsistencyException 
	 */
	public void checkEdges()
	throws InconsistencyException
	{
		// first check if edges is non-null
		if(edges == null)
			throw new InconsistencyException("The ArrayList edges should be non-null, but is null.");

		// check that all vertices are referenced at least three times, as each is the vertex of at least one simplex
		// and in each simplex three edges meet at each vertex
		
		// create an array of ints that will hold the number of references to each vertex in the list of edges...
		int[] vertexReferences = new int[vertices.size()];
		
		// ... and set all these reference counts initially to zero
		for(int i=0; i<vertexReferences.length; i++) vertexReferences[i] = 0;
		
		// go through all edges...
		for(Edge edge:edges)
		{
			// go through both vertices...
			for(int i=0; i<2; i++)
				// ... and increase the reference count of the vertex by 1
				vertexReferences[edge.getVertexIndices()[i]]++;
			// note that any vertex indices that are out of bounds will throw up an error here!
		}

		// check that all reference counts are >= 3
		for(int i=0; i<vertexReferences.length; i++)
		{
			if(vertexReferences[i] < 3)
			{
				// vertex i is referenced fewer than 3 times
				
				// throw an InconsistencyException
				throw(new InconsistencyException("Vertex #" + i + " should be referenced in the list of edges >= 3 times, but is referenced only " + vertexReferences[i] + " times."));
			}
		}
	}
	
	/**
	 * Perform a few consistency checks of the faces
	 * @throws InconsistencyException 
	 */
	public void checkFaces()
	throws InconsistencyException
	{
		// first check if faces is non-null
		if(faces == null)
			throw new InconsistencyException("The ArrayList faces should be non-null, but is null.");

		// check that all vertices are referenced at least three times, as each is the vertex of at least one simplex
		// and in each simplex three faces meet at each vertex;
		// also check that all edges are referenced at least two times
		
		// create an array of ints that will hold the number of references to each vertex in the list of faces...
		int[] vertexReferences = new int[vertices.size()];
		// ... and one that will hold the number of references to each edge...
		int[] edgeReferences = new int[edges.size()];
		
		// ... and set all these reference counts initially to zero
		for(int i=0; i<vertexReferences.length; i++) vertexReferences[i] = 0;
		for(int i=0; i<edgeReferences.length; i++) edgeReferences[i] = 0;
		
		// go through all faces...
		for(Face face:faces)
		{
			// go through all three vertices...
			for(int i=0; i<3; i++)
				// ... and increase the reference count of the vertex by 1
				vertexReferences[face.getVertexIndices()[i]]++;
			// note that any vertex indices that are out of bounds will throw up an error here!

			// go through all three edges...
			for(int i=0; i<3; i++)
				// ... and increase the reference count of the edge by 1
				edgeReferences[face.getEdgeIndices()[i]]++;
			// note that any vertex indices that are out of bounds will throw up an error here!
		}

		// check that all vertex reference counts are >= 3
		for(int i=0; i<vertexReferences.length; i++)
		{
			if(vertexReferences[i] < 3)
			{
				// vertex i is referenced fewer than 3 times
				
				// throw an InconsistencyException
				throw(new InconsistencyException("Vertex #" + i + " should be referenced in the list of faces >= 3 times, but is referenced only " + vertexReferences[i] + " times."));
			}
		}

		// check that all edge reference counts are >= 2
		for(int i=0; i<edgeReferences.length; i++)
		{
			if(edgeReferences[i] < 2)
			{
				// edge i is referenced fewer than 2 times
				
				// throw an InconsistencyException
				throw(new InconsistencyException("Edge #" + i + " should be referenced in the list of faces >= 2 times, but is referenced only " + edgeReferences[i] + " times."));
			}
		}
	}

	
	/**
	 * @param vertex
	 * @return	the index of <vertex> if it is contained in <vertices>, IndexArray.NONE otherwise
	 */
	public int getVertexIndex(Vector3D vertex)
	{
		// go through all the vertices...
		for(int v=0; v<vertices.size(); v++)
		{
			// ... and if one equals <vertex>...
			if(vertices.get(v) == vertex)
			{
				// ... return its index
				return v;
			}
		}
		
		// none of the vertices equals <vertex>, so
		return IndexArray.NONE;
	}

	/**
	 * @param edge
	 * @return	the index of <edge> if it is contained in <edges>, IndexArray.NONE otherwise
	 */
	public int getEdgeIndex(Edge edge)
	{
		// go through all the edges...
		for(int e=0; e<edges.size(); e++)
		{
			// ... and if one equals <edge>...
			if(edges.get(e) == edge)
			{
				// ... return its index
				return e;
			}
		}
		
		// none of the edges equals <edge>, so
		return IndexArray.NONE;
	}

	/**
	 * @param face
	 * @return	the index of <face> if it is contained in <faces>, IndexArray.NONE otherwise
	 */
	public int getFaceIndex(Face face)
	{
		// go through all the faces...
		for(int f=0; f<faces.size(); f++)
		{
			// ... and if one equals <face>...
			if(faces.get(f) == face)
			{
				// ... return its index
				return f;
			}
		}
		
		// none of the faces equals <face>, so
		return IndexArray.NONE;
	}

	/**
	 * @param simplex
	 * @return	the index of <simplex> if it is contained in <simplices>, IndexArray.NONE otherwise
	 */
	public int getSimplexIndex(Simplex simplex)
	{
		// go through all the simplices...
		for(int s=0; s<simplices.size(); s++)
		{
			// ... and if one equals <simplex>...
			if(simplices.get(s) == simplex)
			{
				// ... return its index
				return s;
			}
		}
		
		// none of the simplices equals <simplex>, so
		return IndexArray.NONE;
	}

	
	//
	// infer information
	//
	
	/**
	 * Creates <faces> from <edges>
	 * @throws InconsistencyException 
	 */
	public void inferFacesFromEdges()
	throws InconsistencyException
	{
		// first empty the list of faces
		if(faces == null)
			faces = new ArrayList<Face>();
		else
			faces.clear();

		// go through all vertices
		for(int v=0; v<vertices.size(); v++)
		{
			// first find all edges with vertex #v and other vertex #w, where w>v
			// (if w<v, then that face will already have been detected earlier)...
			
			// ... by creating an array that will hold the edge indices...
			ArrayList<Integer> indicesOfEdgesAtVertex = new ArrayList<Integer>();
			
			// ... and populating it by going through all the edges
			for(int e=0; e<edges.size(); e++)
			{
				// does edge #e start or finish at vertex #v, and if so is the index of the other vertex >v?
				if(edges.get(e).getOtherVertexIndex(v) > v)
				{
					// yes
					
					// add it to the list of edges that start or finish at this vertex
					indicesOfEdgesAtVertex.add(e);
				}
			}
			
			// System.out.println("SimplicialComplex::inferFacesFromEdges: indices of edges meeting at vertex #"+v+": " + indicesOfEdgesAtVertex.toString());
			
			// second, go through all pairs of edges that start or finish at vertex #v;
			// each such pair represents two of the three edges of a face that meets at vertex #v
			for(int e1=0; e1<indicesOfEdgesAtVertex.size(); e1++)
				for(int e2=e1+1; e2<indicesOfEdgesAtVertex.size(); e2++)
				{
					// create an empty face
					Face face = new Face(this);
					
//					System.out.println("SimplicialComplex::inferFacesFromEdges: e1=" + e1 + ", e2=" + e2);
//					System.out.println("SimplicialComplex::inferFacesFromEdges: edges: "+edges.get(indicesOfEdgesAtVertex.get(e1)) + ", " + edges.get(indicesOfEdgesAtVertex.get(e2)));
//					System.out.println("SimplicialComplex::inferFacesFromEdges: other vertex indices: "+edges.get(indicesOfEdgesAtVertex.get(e1)).getOtherVertexIndex(v) + ", " + edges.get(indicesOfEdgesAtVertex.get(e2)).getOtherVertexIndex(v));
				
					// set the face's vertex indices...
					face.setVertexIndices(v, edges.get(indicesOfEdgesAtVertex.get(e1)).getOtherVertexIndex(v), edges.get(indicesOfEdgesAtVertex.get(e2)).getOtherVertexIndex(v));
					
					// ... and from these infer the edge indices
					try {
						// see if it is possible to infer the edges...
						face.inferEdgeIndices();
						
						// ... and if it is, add the face to the list of faces
						faces.add(face);
					} catch (InconsistencyException e) {}
				}
		}
	}
	
	public boolean listOfSimplicesDoesNotContainSimplex(ArrayList<Simplex> simplices, Simplex simplex)
	{
		// go through all the simplices in the list of simplices, and see if there is one with the same vertex indices;
		// if there is one, return false
		
		// go through all the simplices in the list...
		for(Simplex s:simplices)
		{
			// ... and get the list of vertex indices in the current simplex
			int[] vertices = s.getVertexIndices();
			
			// go through all these vertex indices...
			int v = 0;
			while(v<4)
			{
				// is the vth vertex of the current simplex not a vertex of <simplex>?
				if(!simplex.isVertex(vertices[v]))
				{
					// one of the vertex indices is not a vertex index of <simplex>
					break;
				}
				
				v++;
			}
			
			if(v == 4)
			{
				// all vertex indices of s are also in simplex, so the list of simplices *does* contain simplex
				return false;
			}
		}
		
		return true;
	}

	/**
	 * populate <simplices> from <faces>
	 * @throws InconsistencyException 
	 */
	public void inferSimplicesFromFaces()
	throws InconsistencyException
	{
		// first empty the list of simplices
		if(simplices == null)
			simplices = new ArrayList<Simplex>();
		else
			simplices.clear();
		
		// go through all vertices
		for(int v=0; v<vertices.size(); v++)
		{
			// first find all faces with vertex #v
//			// and other vertices #v1 and #v2, where v1, v2 > v
//			// (if v1, v2 < v, then the simplices we are finding will already have been found earlier)...
			
			// ... by creating an array that will hold the face indices...
			ArrayList<Integer> indicesOfFacesAtVertex = new ArrayList<Integer>();
			
			// ... and populating it by going through all the faces
			for(int f=0; f<faces.size(); f++)
			{
//				// is vertex #v one of the vertices of face #f, and if so are the indices of the other vertex >v?
//				if(faces.get(f).getSmallerOtherVertexIndex(v) > v)
				// is vertex #v one of the vertices of face #f?
				if(faces.get(f).containsVertexIndex(v))
				{
					// yes, add the face to the list of faces that meet at this vertex
					indicesOfFacesAtVertex.add(f);
				}
			}
			
			// System.out.println("SimplicialComplex::inferSimplicesFromFaces: v="+v+", indicesOfFacesAtVertex="+indicesOfFacesAtVertex);
			
			// second, go through all triples of faces that start or finish at vertex #v;
			// each such triple represents three of the four faces of a simplex with vertex #v
			for(int f1=0; f1<indicesOfFacesAtVertex.size(); f1++)
				for(int f2=f1+1; f2<indicesOfFacesAtVertex.size(); f2++)
					for(int f3=f2+1; f3<indicesOfFacesAtVertex.size(); f3++)
					{
						// test the new simplex
						
						// create an empty simplex
						Simplex simplex = new Simplex(this);
						
						// System.out.println("SimplicialComplex::inferSimplicesFromFaces: f1=" + f1 + ", f2=" + f2 + ", f3=" + f3);
						// System.out.println("SimplicialComplex::inferSimplicesFromFaces: faces: "+faces.get(indicesOfFacesAtVertex.get(f1)) + ", " + faces.get(indicesOfFacesAtVertex.get(f2)) + ", " + faces.get(indicesOfFacesAtVertex.get(f3)));
						// System.out.println("SimplicialComplex::inferSimplicesFromFaces: vertex indices: "
						// 		+ Arrays.toString(faces.get(indicesOfFacesAtVertex.get(f1)).getVertexIndices()) + ", "
						// 		+ Arrays.toString(faces.get(indicesOfFacesAtVertex.get(f2)).getVertexIndices()) + ", "
						// 		+ Arrays.toString(faces.get(indicesOfFacesAtVertex.get(f3)).getVertexIndices())
						// 	);

						// ... and from these infer the face indices
						try {
							// set the simplex's vertex indices to the union set of the vertices of faces #f1, #f2 and #f3;
							// note that this throws an exception if this union set is not of length 4, as it should be
							simplex.setVertexIndices(IndexArray.unionSet(
									faces.get(indicesOfFacesAtVertex.get(f1)).getVertexIndices(),
									faces.get(indicesOfFacesAtVertex.get(f2)).getVertexIndices(),
									faces.get(indicesOfFacesAtVertex.get(f3)).getVertexIndices()
								));
							
							// see if it is possible to infer the face indices ...
							simplex.inferFaceIndices();
							
//							System.out.println("SimplicialComplex::inferSimplicesFromFaces: simplex.getFaceIndices()=" + simplex.getFaceIndices());

							// ... and if all other vertices lie outside the new simplex and this simplex isn't already in the list, ...
							if(allOtherVerticesLieOutside(simplex) && listOfSimplicesDoesNotContainSimplex(simplices, simplex))
								// ... and if it is, add the simplex to the list of simplices
								simplices.add(simplex);
						} catch (InconsistencyException e)
						{
							// don't do anything here; nothing is wrong, instead there is simply no simplex to add
						}
					}
		}
		
		// System.out.println("SimplicialComplex::inferSimplicesFromFaces: simplices = "+simplices);
				
		// now that all the simplices have been created, infer the simplices on either side of each face
		inferSimplexIndicesForFaces();

		// System.out.println("SimplicialComplex::inferSimplicesFromFaces: simplex indices have been inferred");

		// complete the simplices by inferring outwards information
		inferOutwardsInformation();

		// System.out.println("SimplicialComplex::inferSimplicesFromFaces: outwards information has been inferred");
		// System.out.println("SimplicialComplex::inferSimplicesFromFaces: faces = "+faces);
	}

	/**
	 * @param simplex
	 * @return	true if all other vertices lie outside <simplex>, false otherwise
	 */
	public boolean allOtherVerticesLieOutside(Simplex simplex)
	{
		// go through all vertices
		for(int i=0; i<vertices.size(); i++)
		{
			// first check if vertex #i is one of the vertices of <simplex>
			if(!IndexArray.isInArray(i, simplex.getVertexIndices()))
			{
				// vertex #i is not one of the vertices of <simplex>
				
				// check if it lies inside <simplex>
				if(simplex.pointIsInsideSimplex(vertices.get(i)))
				{
					// vertex #i lies inside <simplex>
					return false;
				}
			}
		}
		
		// all vertices that are not vertices of <simplex> do indeed lie outside <simplex>
		return true;
	}

	/**
	 * @param vertexIndex1	index of the first vertex
	 * @param vertexIndex2	index of the second vertex
	 * @return the index of the edge that ends in the two vertices
	 * @throws InconsistencyException if there is no edge that involves those two indices
	 */
	public int getIndexOfEdgeWithVertices(int vertexIndex1, int vertexIndex2)
	throws InconsistencyException
	{
		// go through all edges
		for(int i=0; i<edges.size(); i++)
		{
			if(edges.get(i).vertexIndicesAre(vertexIndex1, vertexIndex2))
			{
				// edge #i involves both vertices; return it
				return i;
			}
		}
		
		// there is no edge that involves the two vertices
		throw new InconsistencyException("There should be an edge that involves vertices #" + vertexIndex1 + " and #" + vertexIndex2 + ", but there isn't.");
	}

	
	/**
	 * @param vertexIndex1	index of the first vertex
	 * @param vertexIndex2	index of the second vertex
	 * @param vertexIndex3	index of the third vertex
	 * @return the index of the face with vertices #vertexIndex1, #vertexIndex2, and #vertexIndex3
	 * @throws InconsistencyException if there is no face with those vertices
	 */
	public int getIndexOfFaceWithVertices(int vertexIndex1, int vertexIndex2, int vertexIndex3)
	throws InconsistencyException
	{
		// go through all faces
		for(int i=0; i<faces.size(); i++)
		{
			if(faces.get(i).vertexIndicesAre(vertexIndex1, vertexIndex2, vertexIndex3))
			{
				// edge #i involves both vertices; return it
				return i;
			}
		}
		
		// there is no face with those vertices
		throw new InconsistencyException("There should be a face with vertices #" + vertexIndex1 + ", #" + vertexIndex2 + ", and #" + vertexIndex3 + ", but there isn't.");
	}
	
	/**
	 * Find the index of the simplex in this simplicial complex that contains <i>position</i>.
	 * If none is found, return <i>IndexArray.OUTSIDE</i>.
	 * @param position
	 * @return	the index of the simplex containing <i>position</i>; IndexArray.OUTSIDE if <i>position</i> lies inside none of the simplices
	 */
	public int getIndexOfSimplexContainingPosition(Vector3D position)
	{
		// go through all the simplices, ...
		for(int i=0; i<simplices.size(); i++)
		{
			// ... check if the position lies inside the current, ith, one; ...
			if(simplices.get(i).pointIsInsideSimplex(position))
			{
				// ... if it does, return the index of the current simplex; ...
				return i;
			}
		}
		
		// ... if position lies in none of the simplices, return 
		return IndexArray.OUTSIDE;
	}
	
	/**
	 * This method can be called before outside information has been inferred
	 * @return	an ArrayList with the indices of the outside faces
	 */
	private ArrayList<Integer> getOutsideFaceIndicesBeforeAllInfoHasBeenInferred()
	{
		// go through all the simplices and count how often each face is referenced;
		// inside faces are referenced twice, outside faces once
		
		// set up an array that will contain the reference counts for each face
		int[] faceRefs = new int[faces.size()];
		for(int i=0; i<faceRefs.length; i++) faceRefs[i] = 0;
		
		// now go through all the simplices...
		for(Simplex simplex : simplices)
		{
			// ... and increase the reference count of all the faces in the simplex
			int[] simplexFaceIndices = simplex.getFaceIndices();	// should be of length 4
			for(int i=0; i<4; i++)
			{
				// increase the reference count of the <i>th face of the simplex
				faceRefs[simplexFaceIndices[i]]++;
			}
		}
		
		// now collect the indices of all faces that are referenced only once, i.e. the outside faces
		ArrayList<Integer> outsideFaceIndices = new ArrayList<Integer>();
		for(int i=0; i<faceRefs.length; i++)
			// is there exactly one reference to the face with index i?
			if(faceRefs[i] == 1)
				// yes; add i to the list of outside-face indices
				outsideFaceIndices.add(i);
		
		// return the list of outside-face indices
		return outsideFaceIndices;
	}

	/**
	 * This method has to be called after outside information has been inferred
	 * @return	an ArrayList with the indices of the outside faces
	 */
	public ArrayList<Integer> getOutsideFaceIndices()
	{
		// now collect the indices of all faces that are referenced only once, i.e. the outside faces
		ArrayList<Integer> outsideFaceIndices = new ArrayList<Integer>();
		for(int i=0; i<faces.size(); i++)
			// is there exactly one reference to the face with index i?
			if(getFace(i).getNoOfFacesToOutside() == 0)
				// yes; add i to the list of outside-face indices
				outsideFaceIndices.add(i);
		
		// return the list of outside-face indices
		return outsideFaceIndices;
	}
	
	/**
	 * @return	a list containing the indices of all outside vertices in the simplicial complex
	 */
	public ArrayList<Integer> getOutsideVertexIndices()
	{
		// first get the list of the indices of all outside faces
		ArrayList<Integer> outsideFaceIndices = getOutsideFaceIndices();
		
		// now create a HashSet (an implementation of the Set interface, which is a collection with no duplicates) that will contain the
		// indices of all outside vertices...
		HashSet<Integer> outsideVertexIndices = new HashSet<Integer>();
		
		// ... and add to it the indices of all vertices involved in outside faces
		for(int outsideFaceIndex : outsideFaceIndices)
		{
			// get the indices of the vertices of the current outside face...
			int[] currentOutsideFaceVertexIndices = getFace(outsideFaceIndex).getVertexIndices();
			
			// ... and add them to the set of outside-vertex indices
			for(int i=0; i<3; i++) outsideVertexIndices.add(currentOutsideFaceVertexIndices[i]);
		}
		
		// convert the set (HashSet) of outside-vertex indices to a list (ArrayList) and return
		return new ArrayList<Integer>(outsideVertexIndices);
	}
	
	public Vector3D calculateCentroidOfSurface()
	{
		ArrayList<Integer> outsideVertexIndices = getOutsideVertexIndices();
				
		// calculate the vector sum of all outside-vertex positions...
		Vector3D outsideVertexPositionsVectorSum = new Vector3D(0, 0, 0);
		for(int outsideVertexIndex : outsideVertexIndices)
		{
			outsideVertexPositionsVectorSum = Vector3D.sum(outsideVertexPositionsVectorSum, getVertex(outsideVertexIndex));
//			System.out.println("SimplicialComplex:calculateCentroidOfSurface: getVertex(outsideVertexIndex)="+getVertex(outsideVertexIndex)+
//					", outsideVertexPositionsVectorSum="+outsideVertexPositionsVectorSum);
		}
		
//		System.out.println("SimplicialComplex:calculateCentroidOfSurface: outsideVertexIndices.size()="+outsideVertexIndices.size());
		
		// ... and divide it by the number of outside vertices to get the centroid; return this
		return outsideVertexPositionsVectorSum.getProductWith(1./outsideVertexIndices.size());
	}
	
	/**
	 * Add information about each simplex's outer-neighbour simplex and the face that connects each simplex to its
	 * outer-neighbour simplex, and add information about the number of faces to the outside from each face.
	 * A simplex's outer-neighbour simplex is (one of) its nearest-neighbour simplices through which the simplex is
	 * connected to the outside by the fewest faces.
	 */
	public void inferOutwardsInformation()
	{
		// To prepare everything, delete all outwards information
		for(int i=0; i<faces.size(); i++)
		{
			getFace(i).setNoOfFacesToOutside(IndexArray.NONE);
		}
		for(int i=0; i<simplices.size(); i++)
		{
			getSimplex(i).setOuterFaceIndex(IndexArray.NONE);
			getSimplex(i).setOuterNeighbourSimplexIndex(IndexArray.NONE);
		}
		
		// First, find the indices of all outside faces, ...
		ArrayList<Integer> outsideFaceIndices = getOutsideFaceIndicesBeforeAllInfoHasBeenInferred();
		
		// System.out.println("SimplicialComplex::inferOutwardsInformation: outsideFaceIndices="+outsideFaceIndices);
		
		int noOfFacesToOutside = 0;
		
		// ... mark the number of faces to the outside as zero from these outside faces, ...
		for(int outsideFaceIndex : outsideFaceIndices)
		{
			faces.get(outsideFaceIndex).setNoOfFacesToOutside(noOfFacesToOutside);
		}
		
		int noOfChangedSimplices;
		do
		{
			// System.out.println("SimplicialComplex::inferOutwardsInformation: noOfFacesToOutside="+noOfFacesToOutside);
			
			// reset the number of changed simplices
			noOfChangedSimplices = 0;
			
			// go through all simplices...
			for(Simplex simplex : simplices)
			{
				// System.out.println("  SimplicialComplex::inferOutwardsInformation: simplex index="+getSimplexIndex(simplex));

				// ... whose outwards information has not been set, ...
				if(simplex.getOuterFaceIndex() == IndexArray.NONE)
				{
					// ... and infer its outer face index
					int outerFaceIndex = simplex.inferOuterFaceIndex();

					// System.out.println("    SimplicialComplex::inferOutwardsInformation: outerFaceIndex="+outerFaceIndex);

					// has inferring the outer-face index actually succeeded?
					if(outerFaceIndex != IndexArray.NONE)
					{
						// yes

						// System.out.println("    SimplicialComplex::inferOutwardsInformation: getFace(outerFaceIndex).getNoOfFacesToOutside()="+getFace(outerFaceIndex).getNoOfFacesToOutside());

						// check if this face's number of faces to the outside = <i>noOfFacesToOutside</i>
						if(getFace(outerFaceIndex).getNoOfFacesToOutside() == noOfFacesToOutside)
						{
							// yes; set this simplex's outwards information, first its outer face...
							simplex.setOuterFaceIndex(outerFaceIndex);
						
							// ... and then its outer neighbour simplex index
							simplex.setOuterNeighbourSimplexIndex(
									IndexArray.getFirstOtherIndex(
											this.getSimplexIndex(simplex),	// the index of <i>simplex</i>
											getFace(outerFaceIndex).getSimplexIndices()
											)	// the index of the simplex neighbouring face #<i>outerFaceIndex</i> that is *not* <i>simplex</i>
									);
						
							// set this simplex's other faces' outwards information
							for(int i=0; i<4; i++)
							{
								// find the <i>i</i>th face of the simplex
								Face face = simplex.getFace(i);
							
								// has this face's outward information been set?
								if(face.getNoOfFacesToOutside() == IndexArray.NONE)
								{
									face.setNoOfFacesToOutside(noOfFacesToOutside + 1);
								}
							}
							
							// increase the number of changed simplices
							noOfChangedSimplices++;
						}
					}
				}
			}
			
			// increase the number of faces to the outside by 1
			noOfFacesToOutside++;			
		}
		while(noOfChangedSimplices > 0);
//		
//		// ... and mark the outer neighbour of each simplex that has an outside face as the outside
//		// and its outer face as the outside face, and set <i>noOfFacesToOutside</i> for the remaining faces to 1
//		noOfFacesToOutside++;	// now has value 1
//		// Do this by going through all simplices, ...
//		for(Simplex simplex : simplices)
//		{			
//			// ...and if a simplex has an outside face, ...
//			int outsideFaceIndex = simplex.getFaceIndexInList(outsideFaceIndices);
//			
//			// System.out.println("SimplicialComplex::inferOutwardsInformation: outsideFaceIndex = "+outsideFaceIndex);
//			
//			if(outsideFaceIndex != IndexArray.NONE)
//			{
//				// ...then add the relevant outwards information to the simplex ...
//				simplex.setOuterFaceIndex(outsideFaceIndex);
//				simplex.setOuterNeighbourSimplexIndex(IndexArray.OUTSIDE);
//				
//				// ...and set <i>noOfFacesToOutside</i> for the faces that have not already been marked as outside faces as 1; ...
//				for(int i=0; i<4; i++)
//				{
//					// take the <i>i</i>th face
//					Face face = simplex.getFace(i);
//					
//					// if its <i>noOfFacesToOutside</i> variable has not been set yet, set it to 1
//					if(face.getNoOfFacesToOutside() == IndexArray.NONE) face.setNoOfFacesToOutside(noOfFacesToOutside);
//				}
//			}
//			else
//			{
//				// ... otherwise mark the outwards information as not set.
//				simplex.setOuterFaceIndex(IndexArray.NONE);
//				simplex.setOuterNeighbourSimplexIndex(IndexArray.NONE);				
//			}
//			
//			// System.out.println("SimplicialComplex::inferOutwardsInformation: simplex = "+simplex);
//		}
//		
//		// Next, iterate "inwards"!
//		
//		// Create a list of all the simplices that have been changed in the current iteration
//		ArrayList<Simplex> changedSimplices = new ArrayList<Simplex>();
//		
//		do
//		{
//			// the number of faces to the outside will be one more for the next set of faces
//			noOfFacesToOutside++;
//			
//			// clear the list of simplices that have been changed in this iteration
//			changedSimplices.clear();
//
//			// Go through all simplices...
//			for(Simplex simplex : simplices)
//			{
//				// System.out.println("SimplicialComplex::inferOutwardsInformation: simplex = "+simplex);
//
//				// ... that lack outwards information, ...
//				if(simplex.getOuterFaceIndex() == IndexArray.NONE)
//				{
//					// System.out.println("SimplicialComplex::inferOutwardsInformation: ");
//					
//					// ... find the indices of the current simplex's neighbours, ...
//					int[] neighbourSimplexIndices = simplex.getNeighbourSimplexIndices();
//					
//					// System.out.println("SimplicialComplex::inferOutwardsInformation: neighbourSimplexIndices = "+neighbourSimplexIndices);
//					
//					// ... identify the first of the nearest-neighbour simplices *with* outwards information
//					// that hasn't been changed in the current iteration, ...
//					int outerNeighbourSimplexIndex = IndexArray.NONE;
//					for(int i=0; (i<4) && (outerNeighbourSimplexIndex == IndexArray.NONE); i++)
//					{
//						int neighbourSimplexIndex = neighbourSimplexIndices[i];
//						Simplex neighbour = simplices.get(neighbourSimplexIndex);
//						if(
//								(neighbour.getOuterFaceIndex() != IndexArray.NONE)	// neighbour's outwards information has been set...
//								&& (!changedSimplices.contains(neighbour))	// ... and neighbour is not in list of simplices that have been changed in this iteration
//						)
//						{
//							outerNeighbourSimplexIndex = neighbourSimplexIndex;
//						}
//					}
//
//					// ... and if there is such a neighbour...
//					if(outerNeighbourSimplexIndex != IndexArray.NONE)
//					{
//						// ... set <simplex>'s outwards information towards this neighbour, ...
//						simplex.setOuterNeighbourSimplexIndex(outerNeighbourSimplexIndex);
//						simplex.setOuterFaceIndex(simplex.sharedFaceIndex(simplices.get(outerNeighbourSimplexIndex)));
//					
//						// ... add the changed simplex to the list of changed simplices, ...
//						changedSimplices.add(simplices.get(outerNeighbourSimplexIndex));
//						
//						// ...and set <i>noOfFacesToOutside</i> for the faces that have not already been marked as outside faces as <i>noOfFacesToOutside</i>
//						for(int i=0; i<4; i++)
//						{
//							// take the <i>i</i>th face
//							Face face = simplex.getFace(i);
//							
//							// if its <i>noOfFacesToOutside</i> variable has not been set yet, set it to 1
//							if(face.getNoOfFacesToOutside() == IndexArray.NONE) face.setNoOfFacesToOutside(noOfFacesToOutside);
//						}
//					}
//				}
//			}
//		}
//		// repeat if simplices have been changed in the iteration that just finished
//		while (changedSimplices.size() > 0);
	}
	
	
	/**
	 * after all faces and all simplices have been created, running this method completes the information, stored in each face,
	 * about which simplices it is separating
	 */
	public void inferSimplexIndicesForFaces()
	throws InconsistencyException
	{
		// go through all faces
		for(int f=0; f<faces.size(); f++)
		{
			// prepare an empty array into which the indices of the simplices on either side will go
			int[] neighbouringSimplexIndices = new int[2];
			int neighbouringSimplicesFound = 0;
			
			// go through all simplices, ...
			for(int s=0; s<simplices.size(); s++)
			{
				// ... and if face #f is one of simplex #s's faces...
				if(simplices.get(s).isFace(f))
				{
					if(neighbouringSimplicesFound == 2)
					{
						System.out.println("SimplicialComplex::inferSimplexIndicesForFaces: simplicial complex ="+this);
						System.out.flush();
						throw new InconsistencyException("Face #"+f+" has more than two neighbouring simplices, namely "+neighbouringSimplexIndices[0]+", "+neighbouringSimplexIndices[1]+", and "+s+"."); // ).printStackTrace();
						// System.exit(-1);
					}
					
					// ... add s to the list of indices of simplices on either side of face #f
					neighbouringSimplexIndices[neighbouringSimplicesFound++] = s;
				}
			}
			
			// if only one simplex has been found of which face #f is a face, then it must be the outside on the other side
			if(neighbouringSimplicesFound == 1) neighbouringSimplexIndices[neighbouringSimplicesFound++] = IndexArray.OUTSIDE;
			
			// set face #f's array of simplex indices to <simplexIndices>
			faces.get(f).setSimplexIndices(neighbouringSimplexIndices);
		}
	}
	
	
	//
	// other methods
	//
	
	/**
	 * @param showVertices
	 * @param vertexSurfaceProperty
	 * @param vertexRadius
	 * @param description
	 * @param showEdges
	 * @param edgeSurfaceProperty
	 * @param edgeRadius
	 * @param showFaces
	 * @param faceSurfaceProperty
	 * @param parent
	 * @param studio
	 * @return	a scene-object collection that represents the simplicial complex
	 */
	public EditableSceneObjectCollection getEditableSceneObjectCollection(
			String description,
			boolean showVertices,
			SurfaceProperty vertexSurfaceProperty,
			double vertexRadius,
			boolean showEdges,
			SurfaceProperty edgeSurfaceProperty,
			double edgeRadius,
			boolean showFaces,
			SurfaceProperty faceSurfaceProperty,
			SceneObject parent,
			Studio studio
		)
	{
		// create the scene-object collection that will hold the scene objects representing this simplicial complex
		EditableSceneObjectCollection cSimplicialComplex = new EditableSceneObjectCollection(description, true, parent, studio);
		
		// create the scene-object collection that will hold the scene objects representing the vertices...
		EditableSceneObjectCollection cVertices = new EditableSceneObjectCollection("Vertices", true, cSimplicialComplex, studio);
		
		// ... and add it to cSimplicialComplex
		cSimplicialComplex.addSceneObject(cVertices, showVertices);
		

		// create the scene-object collection that will hold the scene objects representing the edges...
		EditableSceneObjectCollection cEdges = new EditableSceneObjectCollection("Edges", true, cSimplicialComplex, studio);
		
		// ... and add it to cSimplicialComplex
		cSimplicialComplex.addSceneObject(cEdges, showEdges);
		
		// create the scene-object collection that will hold the scene objects representing the faces
		EditableSceneObjectCollection cFaces = new EditableSceneObjectCollection("Faces", true, cSimplicialComplex, studio);

		// ... and add it to cSimplicialComplex
		cSimplicialComplex.addSceneObject(cFaces, showFaces);
		
		// to add spheres for the vertices, go through all the vertices...
		for(int i=0; i<vertices.size(); i++)
		{
			// ... and add a sphere representing the current (ith) one to the scene-object collection cVertices
			cVertices.addSceneObject(new EditableScaledParametrisedSphere(
					"Vertex #"+i,
					vertices.get(i),	// centre 
					vertexRadius,	// radius
					vertexSurfaceProperty,	// surface property 
					cVertices, // parent
					studio
				));
		}

		// to add cylinders for the edges, go through all the edges...
		for(int i=0; i<edges.size(); i++)
		{
			// ... and add a cylinder representing the current (ith) one to the scene-object collection cEdges
			cEdges.addSceneObject(new EditableParametrisedCylinder(
					"Edge #"+i,
					vertices.get(edges.get(i).getVertexIndices()[0]),	// start point
					vertices.get(edges.get(i).getVertexIndices()[1]),	// end point
					edgeRadius,	// radius
					edgeSurfaceProperty,	// surface property 
					cEdges, // parent
					studio
				));
		}

		// to add triangles for the faces, go through all the faces...
		for(int i=0; i<faces.size(); i++)
		{
			// ... and add a triangle representing the current (ith) one to the scene-object collection cFaces
			cFaces.addSceneObject(EditableParametrisedTriangle.makeEditableParametrisedTriangleFromVertices(
					"Face #"+i,
					faces.get(i).getVertex(0),	// vertex 1
					faces.get(i).getVertex(1),	// vertex 2
					faces.get(i).getVertex(2),	// vertex 3
					faceSurfaceProperty,	// surface property 
					cFaces, // parent
					studio
				));
		}

		// return the scene-object collection representing the whole simplicial complex
		return cSimplicialComplex;
	}
	

	@Override
	public String toString() {
		return "SimplicialComplex [vertices=" + vertices + ", edges=" + edges + ", faces=" + faces + ", simplices="
				+ simplices + "]";
	}
	
	public void printNicely(PrintStream printStream)
	{
		printStream.println("Simplices:");
		int i=0;
		for(Simplex simplex : simplices)
			printStream.println("  #"+(i++)+":"+simplex.toString());

		printStream.println("Faces:");
		i=0;
		for(Face face : faces)
		{
			printStream.println("  #"+(i++)+":"+face.toString());
			try {
				printStream.println("    connected to the outside through faces "+face.getIndicesOfFacesBetweenThisAndOutside());
			} catch (InconsistencyException e) {
				e.printStackTrace();
			}
		}

		printStream.println("Edges:");
		i=0;
		for(Edge edge : edges)
			printStream.println("  #"+(i++)+":"+edge.toString());

		printStream.println("Vertices:");
		i=0;
		for(Vector3D vertex : vertices)
			printStream.println("  #"+(i++)+":"+vertex.toString());

	}
}
