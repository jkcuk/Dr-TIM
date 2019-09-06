package math.simplicialComplex;

import java.util.ArrayList;

import math.Vector3D;
import optics.raytrace.exceptions.InconsistencyException;

/**
 * A simplicial complex in which each simplex has associated with it a mapping between the space of that simplex
 * and the outside (cell 0), defined by the methods <i>mapToOutside</i> and <i>mapFromOutside</i>.
 * @author johannes
 */
public abstract class MappingSimplicialComplex
extends SimplicialComplex
{
	private static final long serialVersionUID = 2012769824478265123L;

	public MappingSimplicialComplex(
			ArrayList<? extends Vector3D> arrayList,
			ArrayList<? extends Edge> arrayList2,
			ArrayList<Face> faces,
			ArrayList<Simplex> simplices
		)
	throws InconsistencyException
	{
		super(arrayList, arrayList2, faces, simplices);
	}

	public MappingSimplicialComplex()
	throws InconsistencyException
	{
		super();
	}
	
	/**
	 * @param simplexIndex
	 * @param position
	 * @return	the position <i>position</i> in the space of vertex #<i>vertexIndex</i>, mapped into the outside space
	 */
	public abstract Vector3D mapToOutside(int simplexIndex, Vector3D position);

	/**
	 * @param simplexIndex
	 * @param position
	 * @return	the position <i>position</i> in the outside space, mapped into the space of simplex #<i>simplexIndex</i>
	 */
	public abstract Vector3D mapFromOutside(int simplexIndex, Vector3D position);
	
	/**
	 * Map the given position into outside space.
	 * If the position is inside one of the simplices of the simplicial complex, it is interpreted as inside the space of that simplex and mapped accordingly.
	 * If the position happens to be outside of the simplicial complex, it is interpreted to be already in outside space.
	 * @param position
	 * @return	position, mapped into outside space (if necessary)
	 */
	public Vector3D mapToOutsideSpace(Vector3D position)
	{
		return mapToOutside(
				getIndexOfSimplexContainingPosition(position),	// simplexIndex
				position
			);
	}
	

}
