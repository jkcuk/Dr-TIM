package optics.raytrace.core;

import math.*;

/**
 * This class represents a ray of light with known direction and original position Vector3Ds.
 * This class is mutable and must be interacted with through the set and get methods.
 * 
 * @author Dean et al.
 *
 */
public class Ray {
	/**
	 * The starting position of this light ray.
	 */
	private Vector3D p;

	/**
	 * The normalized light ray direction.
	 */
	private Vector3D d;
	
	/**
	 * The k vector; in a medium, this is, in general, different from the normalised light-ray direction, which is given by the Hamilton equation
	 * d x_i / d \tau = \partial H / \partial k_i (Eqn (41a) in [1]), where (x_1, x_2, x_3) = (x, y, z), \tau parametrises the ray, H is the Hamiltonian, and
	 * k_i is the momentum corresponding to x_i.
	 * Note that the length of k is such that k.g.k = det(g) (Eqn (43) in [1]), where g is the metric tensor of the medium.
	 * References:
	 * [1] D. Schurig, J. B. Pendry and D. R. Smith, "Calculation of material properties and ray tracing in transformation media",
	 *     Opt. Express 14, 9794-9804 (2006)
	 */
	private Vector3D k;
	
	/**
	 * The time when the ray reaches p.
	 * This is used for relativistic ray tracing (see RelativisticAnyFocusSurfaceCamera)
	 */
	private double t;

//	public static final Ray
//		NO_RAY = new Ray((Vector3D)null, (Vector3D)null, 0);
	
	/**
	 * A light ray is defined by an initial position and a direction in which it propagates.
	 * @param p The position of the light ray.
	 * @param d The direction in which the ray propagates.
	 * @param t	The time when the ray is at position p.
	 */
	public Ray(Vector3D p, Vector3D d, double t)
	{
		this.p = p;	// setP(p);
		setD(d);
		this.t = t;
	}

	/**
	 * A light ray travelling in an anisotropic medium
	 * @param p The position of the light ray.
	 * @param k The wavenumber of the ray, whose length is given by k.g.k = det(g) (Eqn (43) in [1]), where g is the metric tensor of the medium 
	 * @param d The direction in which the ray propagates.
	 * @param t	The time when the ray is at position p.
	 */
	public Ray(Vector3D p, Vector3D k, Vector3D d, double t)
	{
		this.p = p;	// setP(p);
		setK(k);
		setD(d);
		this.t = t;
	}
	
	/**
	 * Make a copy of the original ray
	 * @param original
	 */
	public Ray(Ray original)
	{
		this(
				original.p,
				original.k,
				original.d,
				original.t
			);
	}

	/**
	 * faster alternative to "instanceof RayWithTrajectory" that reqiures no introspection
	 * @return
	 */
	public boolean isRayWithTrajectory()
	{
		return false;
	}
	
	/**
	 * Return a branch of this ray with initial position p and direction d.
	 * This function is there so that it can be overridden by the RayWithTrajectory class.
	 * @param p	ray start position
	 * @param d	ray direction
	 * @param t	ray start time
	 * @return
	 */
	public Ray getBranchRay(Vector3D p, Vector3D d, double t)
	{
		return new Ray(p, d, t);
	}
	
	public Ray getBranchRay(Vector3D p, Vector3D k, Vector3D d, double t)
	{
		return new Ray(p, k, d, t);
	}
	
	/**
	 * This method is the default access method for initial ray position.
	 * @return The position of the light ray.
	 */
	public Vector3D getP()
	{
		return p;
	}

	/**
	 * Set the current position of this light ray.
	 * @param p The new light ray position.
	 */
	public void setP(Vector3D p)
	{
		this.p = p;
	}

	/**
	 * The default access method for the light ray direction.
	 * @return The normalised light ray direction.
	 */
	public Vector3D getD()
	{
		return d;
	}

	/**
	 * Normalises and sets the ray direction.  
	 * Zero length direction Vector3Ds are not handled.
	 * 
	 * @param d The (non-normalised) ray direction
	 */
	public void setD(Vector3D d)
	{
		this.d = d.getNormalised();
	}

	public Vector3D getK() {
		return k;
	}

	public void setK(Vector3D k) {
		this.k = k;
	}

	public double getT() {
		return t;
	}

	public void setT(double t) {
		this.t = t;
	}

	/**
	 * Advance this light ray a distance <i>a</i> in the direction <b>d</b> to a
	 * new position <b>p</b> + <i>a</i>*<b>d</b>.
	 * This method will  change this light ray.
	 * @see getAdvancedRay
	 * @param a The distance over which the ray is to advance.
	 */
	public Ray advance(double a)
	{
		setP(p.getSumWith(d.getProductWith(a)));
		
		// as the ray is a backwards-traced ray, "advancing" it means going backwards in time
		setT(t-a/SpaceTimeTransformation.c);
		return this;
	}
	
	/**
	 * Return a light ray similar to this one, but advanced by a distance <i>a</i> in
	 * direction <b>getD()</b> to a new position <b>getP()</b> + <i>a</i>*<b>getD()</b>.
	 * This method will not change this light ray and will return a new one that has been advanced.
	 * @param a The distance over which the ray is to advance.
	 * @return A new ray that has been advanced by the <i>a</i>*<b>d</b>.
	 */
	public Ray getAdvancedRay(double a)
	{
		// TODO this is wrong in a medium
		// as the ray is a backwards-traced ray, "advancing" it means going backwards in time
		return new Ray(p.getSumWith(d.getProductWith(a)), d, t-a/SpaceTimeTransformation.c);
	}

	/**
	 * Return a light ray with the same starting point and time, but the reverse ray direction.
	 */
	public Ray getReversedRay()
	{
		if(k != null)
			return new Ray(p, k.getReverse(), d.getReverse(), t);
		else
			return new Ray(p, d.getReverse(), t);
	}

	/**
	 * Return a string representation of the light ray using the syntax ray(p, d)
	 * @return The string representation.
	 */ 
	@Override
	public String toString()
	{
		return "<ray, p = " + p + (k!=null?", k = " + k:"") + ", d = " + d + ", t = " + t + ">";
	}
}

