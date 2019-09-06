package math.simplicialComplex;

import optics.raytrace.exceptions.InconsistencyException;

/**
 * An edge with a name.
 * @author johannes
 */
public class NamedEdge extends Edge
{
	private static final long serialVersionUID = 7921319098785637475L;

	/**
	 * The name of the named edge.
	 */
	protected String name;
	
	public NamedEdge(String name, Edge edge)
	{
		super(edge);
		
		this.name = name;
	}
	
	/**
	 * Constructor that creates a named edge from vertex #vertexIndex1 to vertex #vertexIndex2
	 * @param name
	 * @param simplicialComplex
	 * @param vertexIndex1
	 * @param vertexIndex2
	 * @throws InconsistencyException
	 */
	public NamedEdge(String name, SimplicialComplex simplicialComplex, int vertexIndex1, int vertexIndex2)
	throws InconsistencyException
	{
		super(simplicialComplex, vertexIndex1, vertexIndex2);
		
		setName(name);
	}

	/**
	 * Create a copy of the original.
	 * Note that the copy shares the array vertexIndices with the original.
	 * @param original
	 */
	public NamedEdge(NamedEdge original)
	{
		super();
		
		this.simplicialComplex = original.getSimplicialComplex();
		this.vertexIndices = original.getVertexIndices();
		this.name = original.getName();
	}
	
	public NamedEdge clone()
	{
		return new NamedEdge(this);
	}


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}