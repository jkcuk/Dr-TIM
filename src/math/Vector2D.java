package math;

import java.io.Serializable;

/**
 * A simple class to hold pairs of numbers.
 * The data are stored in public variables
 *
 * @author Johannes Courtial
 */
public class Vector2D implements Serializable
{
	private static final long serialVersionUID = 4586744457207191051L;
	
	/**
	 * The coordinates of this Vector2D
	 */
	public double x, y;
	
	/**
	 * Create a 2D vector from a pair of double precision numbers.
	 * @param x The x coordinate of this Vector2D.
	 * @param y The y coordinate of this Vector2D.
	 */
	public Vector2D(double x, double y)
	{
		this.x = x;
		this.y = y;
	}

	/**
	 * Calculate the sum of two Vector2Ds.
	 * 
	 * @param a
	 * @param b
	 * @return The sum of the two Vector2Ds, <b>a</b>+<b>b</b>.  
	 */
	public static Vector2D sum(Vector2D a, Vector2D b)
	{
		return new Vector2D(a.x+b.x, a.y+b.y);
	}

	/**
	 * Calculate the sum of three Vector2Ds.
	 * 
	 * @param a
	 * @param b
	 * @param c
	 * @return The sum of the three Vector2Ds, <b>a</b>+<b>b</b>+<b>c</b>.  
	 */
	public static Vector2D sum(Vector2D a, Vector2D b, Vector2D c)
	{
		return new Vector2D(a.x+b.x+c.x, a.y+b.y+c.y);
	}


	/**
	 * The scalar product with this Vector2D.
	 * @param f A scalar with which this Vector2D is multiplied.
	 * @return A Vector2D which is the current instance multiplied by the scalar <i>f</i>, i.e. <i>f</i>.<b>this</b> 
	 */
	public Vector2D getProductWith(double f)
	{		
		return new Vector2D(f*x, f*y);
	}
	
	/**
	 * Calculate the scalar product of two Vector2Ds.
	 * @param a Any Vector2D.
	 * @param b Another Vector2D.
	 * @return The scalar product of the two Vector2Ds <b>a</b> &bull; <b>b</b>
	 */
	public static double scalarProduct(Vector2D a, Vector2D b)
	{
		return a.x*b.x+a.y*b.y;
	}

	/**
	 * Calculate the squared length of the Vector2D.
	 * @return The modulus squared, <i>x</i><sup>2</sup>+<i>y</i><sup>2</sup>.
	 */
	public double getModSquared()
	{	//returns the modulus squared of the current instance
		return x*x+y*y;
	}

	/**
	 * Get the fractional projection length <i>f</i> of this Vector2D onto the vector <b>v</b>.
	 * The component of this vector in the direction of <b>v</b> is then <i>f</i> <b>v</b>.
	 *
	 * @param v The Vector2D onto which <b>this</b> is to be projected.
	 * @return The fractional distance along the Vector2D.
	 */
	public double getFractionalProjectionLength(Vector2D v)
	{
		return  Vector2D.scalarProduct(this, v) / v.getModSquared();
	}


	/**
	 * @return	(y, x) -- a vector with x and y swapped
	 */
	public Vector2D getTranspose()
	{
		return new Vector2D(y, x);
	}
	
	/**
	 * Project onto a two dimensional basis given by <b>v</b><sub>1</sub> and <b>v</b><sub>2</sub>.
	 * This method calculates the coefficients <i>c<sub>1</sub></i> and <i>c<sub>2</sub></i> such that
	 * 		this = <i>c<sub>1</sub></i>*<b>v</b><sub>1</sub> + <i>c<sub>2</sub></i>*<b>v</b><sub>2</sub>.
	 * 
	 * @param v1 The first Vector2D
	 * @param v2 The second Vector2D
	 * @return The two coefficients on the projection plane (<i>c<sub>1</sub>, <i>c<sub>2</sub>).
	 */
	public Vector2D calculateDecomposition(Vector2D v1, Vector2D v2)
	{
		// Want to find c1 and c2 such that
		// 	<b>x</b> = c1 <b>v1</b> + c2 <b>v2</b>	(1)
		// From now on drop <b></b> formatting.
		// Calculate
		// 	(1).v1: x.v1 = c1 v1.v1 + c2 v2.v1	(2)
		// 	(1).v2: x.v2 = c1 v1.v2 + c2 v2.v2	(3)
		// The solution (found by Mathematica) is
		// 	c1 = (v1.v2 * x.v2 - v2.v2 * x.v1) / d,
		// 	c2 = (v1.v2 * x.v1 - v1.v1 * x.v2) / d,
		// where
		// 	d = v1.v1 * v2.v2 - (v1.v2)^2
		double
			v1v1 = v1.getModSquared(),	// v1.v1
			v2v2 = v2.getModSquared(),	// v2.v2
			v1v2 = Vector2D.scalarProduct(v1, v2),	// v1.v2
			xv1 = Vector2D.scalarProduct(this, v1),	// x.v1
			xv2 = Vector2D.scalarProduct(this, v2),	// x.v2
			d = v1v1*v2v2 - v1v2*v1v2;	// common denominator

		return new Vector2D(
				(v1v2*xv2 - v2v2*xv1)/d,
				(v1v2*xv1 - v1v1*xv2)/d
		);
	}


	/**
	 * Change to the basis given by <b>e<sub>1</sub></b> and <b>e<sub>2</sub></b>.
	 * @param e1 The first basis vector
	 * @param e2 The second basis vector
	 * @return A new Vector2D representing the previous Vector2D, but in the specified basis.
	 */
	public Vector2D toBasis(Vector2D e1, Vector2D e2)
	{
		return calculateDecomposition(e1, e2);
	}

	/**
	 * @param e1 The first basis vector
	 * @param e2 The second basis vector
	 * @return A new Vector2D that represents the previous Vector2D, but in the conventional Cartesian basis.
	 */
	public Vector2D fromBasis(Vector2D e1, Vector2D e2)
	{
		return Vector2D.sum(
				e1.getProductWith(x),	// x * e1
				e2.getProductWith(y)	// y * e2
			);
	}

	/**
	 * Represent this Vector3D as a string of text.  The format is <Vector2D x=#, y=#>.
	 * @return A string representation of this 2D Vector3D.
	 */
	@Override
	public String toString()
	{
		return "(" + MyMath.doubleToString(x) + ", " + MyMath.doubleToString(y) + ")";
	}
}
