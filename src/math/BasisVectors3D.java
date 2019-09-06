package math;

import java.util.ArrayList;

/**
 * @author johannes
 *
 * A class that collects the basis vectors of a 3-dimensional coordinate system
 */
public class BasisVectors3D
{
	/**
	 * true if the basis vectors have to form an orthogonal coordinate system, false otherwise
	 */
	boolean orthogonal;
	
	/**
	 * true if the basis vectors have to be normalised, false otherwise
	 */
	boolean normalised;
	
	// could also introduce a variable that determines if the basis vectors have to form a right-handed coordinate system

	// the basis vectors
	Vector3D u, v, w;
	
	public BasisVectors3D(boolean orthogonal, boolean normalised, Vector3D u, Vector3D v, Vector3D w)
	{
		setOrthogonal(orthogonal);
		setNormalised(normalised);
		setUVW(u, v, w);
	}
	
	@Override
	public BasisVectors3D clone()
	{
		return new BasisVectors3D(orthogonal, normalised, u.clone(), v.clone(), w.clone());
	}
	
	public static BasisVectors3D getOrthonormalXYZBasisVectors()
	{
		return new BasisVectors3D(true, true, new Vector3D(1,0,0), new Vector3D(0,1,0), new Vector3D(0,0,1));
	}

	public boolean isOrthogonal() {
		return orthogonal;
	}

	public void setOrthogonal(boolean orthogonal) {
		this.orthogonal = orthogonal;
	}

	public boolean isNormalised() {
		return normalised;
	}

	public void setNormalised(boolean normalised) {
		this.normalised = normalised;
	}

	public Vector3D getU() {
		return u;
	}

	public Vector3D getV() {
		return v;
	}

	public Vector3D getW() {
		return w;
	}
	
	public Vector3D[] getArrayOfBasisVectors3D()
	{
		return new Vector3D[] {u, v, w};
	}
	
	public ArrayList<Vector3D> getArrayListOfBasisVectors3D()
	{
		ArrayList<Vector3D> al = new ArrayList<Vector3D>(3);
		al.add(u);
		al.add(v);
		al.add(w);
		return al;
	}
	
	/**
	 * @param basisVector
	 * @return	if the coordinate system is normalised, returns the normalised basis vector, otherwise the (unnormalised) basis vector
	 */
	public Vector3D normaliseOrNot(Vector3D basisVector)
	{
		return normalised?basisVector.getNormalised():basisVector;
	}
	
	/**
	 * Sets the basis vectors, ensuring they are orthogonal and normalised if necessary.
	 * Ensuring orthogonality works by first setting u, then projecting v into the plane perpendicular to u,
	 * and then projecting w onto the line perpendicular to u and v.
	 * @param u
	 * @param v
	 * @param w
	 */
	public void setUVW(Vector3D u, Vector3D v, Vector3D w)
	{
		// first, set this.u to the argument u, normalised if necessary
		this.u = normaliseOrNot(u);
	
		if(orthogonal)
		{
			// the coordinate system is supposed to be orthogonal, so make sure that the basis vectors actually are orthogonal
			
			// set this.v to the part of the argument v that is perpendicular to this.u; normalise if necessary
			this.v = normaliseOrNot(v.getPartPerpendicularTo(this.u));
		
			// finally, set this.w to the part of the argument w that is perpendicular to both this.u and this.v
			// (i.e. the part that is parallel to the cross product of this.u and this.v);
			// normalise if necessary
			this.w = normaliseOrNot(w.getPartParallelTo(Vector3D.crossProduct(this.u, this.v)));
		}
		else
		{
			// no need to make sure the basis vectors are orthogonal
			this.v = normaliseOrNot(v);
			this.w = normaliseOrNot(w);
		}
	}
}
