package optics.rayplay.geometry3D;

import math.Vector2D;
import math.Vector3D;
import optics.raytrace.exceptions.RayTraceException;

/**
 * A parallel projection into a given plane.
 * 
 * Any 3D point p is mapped into a 2D position (u, v) given by
 *   <b>p</b> = <b>o</b> + n <b>n</b> + u <b>u</b> + v <b>v</b>, (1)
 * where <b>n</b> is the normal to the plane, and <b>u</b> and <b>v</b> are orthogonal vectors spanning the plane
 * 
 * @author johannes
 */
public class ParallelProjection3Dto2D {
	
	/**
	 * the "origin", <b>o</b>, i.e. the point on the plane that corresponds to (u, v) = (0, 0)
	 */
	public Vector3D o;
	
	/**
	 * the first of the two orthogonal span vectors, <b>u</b> and <b>v</b>
	 */
	public Vector3D u;
	
	/**
	 * the second of the two orthogonal span vectors, <b>u</b> and <b>v</b>
	 */
	public Vector3D v;
	
	/**
	 * <b>u</b> / ||<b>u</b>||<sup>2</sup>
	 */
	public Vector3D u1;
	
	/**
	 * <b>v</b> / ||<b>v</b>||<sup>2</sup>
	 */
	public Vector3D v1;
	
	
	// constructor

	public ParallelProjection3Dto2D(Vector3D o, Vector3D u, Vector3D v)
	// throws RayTraceException
	{
		this.o = o;
		setuv(u, v);
	}
	
	
	// getters & setters
	
	/**
	 * Set <b>u</b> and <b>v</b>
	 * @param u	the first of the two orthogonal span vectors, <b>u</b> and <b>v</b>
	 * @param v	the second of the two orthogonal span vectors, <b>u</b> and <b>v</b>
	 * @throws RayTraceException	if <b>u</b> and <b>v</b> are not orthogonal
	 */
	public void setuv(Vector3D u, Vector3D v)
	// throws RayTraceException
	{
		// set u and v
		this.u = u;
		this.v = v;
		
		// pre-calculate u1 and v1
		u1 = u.getProductWith(1./u.getModSquared());
		v1 = v.getProductWith(1./v.getModSquared());
		
		// check that u and v are orthogonal
		if(Vector3D.scalarProduct(u, v) > 1e-16)
			System.err.println("ParallelProjection3Dto2D::setuv: <b>u</b> and <b>v</b> are not orthogonal: <b>u</b>.<b>v</b>="+Vector3D.scalarProduct(u, v));
	}
	
	public Vector3D getO() {
		return o;
	}

	public void setO(Vector3D o) {
		this.o = o;
	}

	public Vector3D getU() {
		return u;
	}

	public Vector3D getV() {
		return v;
	}




	// useful methods
	
	/**
	 * taking the scalar product of Eqn (1) with <b>u</b> gives
 	 *   (<b>p</b>-<b>o</b>).<b>u</b> = n <b>n</b>.<b>u</b> + u <b>u</b>.<b>u</b> + v <b>v</b>.<b>u</b> = u ||u||^2,
 	 * so
 	 *   u = (<b>p</b>-<b>o</b>).(<b>u</b> / ||u||^2) = (<b>p</b>-<b>o</b>).<b>u1</b>.
	 * @param p	the 3D position that is to be projected
	 * @return	u
	 */
	public double calculateU(Vector3D p)
	{
		return Vector3D.scalarProduct(Vector3D.difference(p, o), u1);
	}

	/**
	 * taking the scalar product of Eqn (1) with <b>v</b> gives
 	 *   (<b>p</b>-<b>o</b>).<b>v</b> = n <b>n</b>.<b>v</b> + u <b>u</b>.<b>v</b> + v <b>v</b>.<b>v</b> = v ||v||^2,
 	 * so
 	 *   v = (<b>p</b>-<b>o</b>).(<b>v</b> / ||v||^2) = (<b>p</b>-<b>o</b>).<b>v1</b>.
	 * @param p	the 3D position that is to be projected
	 * @return	v
	 */
	public double calculateV(Vector3D p)
	{
		return Vector3D.scalarProduct(Vector3D.difference(p, o), v1);
	}

	/**
	 * taking the scalar product of Eqn (1) with <b>u</b> gives
 	 *   (<b>p</b>-<b>o</b>).<b>u</b> = n <b>n</b>.<b>u</b> + u <b>u</b>.<b>u</b> + v <b>v</b>.<b>u</b> = u ||u||^2,
 	 * so
 	 *   u = (<b>p</b>-<b>o</b>).(<b>u</b> / ||u||^2) = (<b>p</b>-<b>o</b>).<b>u1</b>.
 	 * Similarly,
 	 *   v = (<b>p</b>-<b>o</b>).(<b>v</b> / ||v||^2) = (<b>p</b>-<b>o</b>).<b>v1</b>.
	 * @param p	the 3D position that is to be projected
	 * @return	(u, v)
	 */
	public Vector2D project(Vector3D p)
	{
		return new Vector2D(
				calculateU(p),
				calculateV(p)
			);
	}
}
