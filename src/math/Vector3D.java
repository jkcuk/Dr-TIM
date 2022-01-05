package math;

import Jama.*;
import java.io.*;
import java.util.ArrayList;

/**
 * A three dimensional (mathematical) Vector3D which explicitly stores <i><b>x</b></i>, <i><b>y</b></i> and <i><b>z</b></i> data.  
 * Data is stored in a standard Cartesian coordinate system as double-precision numbers.  All operations on the data is 
 * immutable, but the variables are public so may be conveniently varied as required.
 */
public class Vector3D implements Serializable, Cloneable
{
	public class Vector3DWithBonusVector3D {

		public Vector3DWithBonusVector3D() {
			// TODO Auto-generated constructor stub
		}

	}

	private static final long serialVersionUID = 825414651495560186L;
	
	public double x, y, z;	// x, y and z coordinates

	public static final Vector3D
		X = new Vector3D(1, 0, 0),	// unit Vector3D in the x direction
		Y = new Vector3D(0, 1, 0),	// unit Vector3D in the y direction
		Z = new Vector3D(0, 0, 1),	// unit Vector3D in the z direction
		O = new Vector3D(0, 0, 0),	// zero Vector3D
		NaV = new Vector3D(Double.NaN, Double.NaN, Double.NaN);	// not a Vector3D

	/**
	 * Create a Vector3D <i>x</i><b>i</b>+<i>y</i><b>j</b>+<i>z</i><b>k</b>
	 * @param x The distance along the x-axis.
	 * @param y The distance along the y-axis.
	 * @param z The distance along the z-axis.
	 */
	public Vector3D(double x, double y, double z)
	{      //constructor
		this.x=x;
		this.y=y;
		this.z=z;
	}

	/**
	 * Create a Vector3D, <i>x</i><b>i</b>+<i>y</i><b>j</b>+<i>z</i><b>k</b>, parsing string representation of numbers.
	 * @param x The distance along the x-axis.
	 * @param y The distance along the y-axis.
	 * @param z The distance along the z-axis.
	 */
	public Vector3D(String x, String y, String z)
	{
		this(Double.parseDouble(x), Double.parseDouble(y), Double.parseDouble(z));
	}
	
	/**
	 * Make a clone of the original
	 * @param original
	 */
	public Vector3D(Vector3D original)
	{
		this(original.x, original.y, original.z);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Vector3D clone()
	{
		return new Vector3D(this);
	}

	/**
	 * Calculate the scalar product of two Vector3Ds.
	 * @param a Any Vector3D.
	 * @param b Another Vector3D.
	 * @return The scalar product of the two Vector3Ds <b>a</b> &bull; <b>b</b>
	 */
	public static double scalarProduct(Vector3D a, Vector3D b)
	{
		return a.x*b.x+a.y*b.y+a.z*b.z;
	}

	/**
	 * Creates a new Vector3D corresponding to the Jama Vector3D v.  
	 * In order to perform matrix algebra, an external package is used called Jama.
	 * 
	 * @param v
	 */
	public Vector3D(Matrix v)
	{
		this(v.get(0, 0), v.get(1, 0), v.get(2, 0));
	}


	/**
	 * Calculate the cross product of two Vector3Ds.
	 * 
	 * @param a
	 * @param b
	 * @return The cross product of the two Vector3Ds, <i><b>a</b></i>&times;<i><b>b</b></i>.
	 */
	public static Vector3D crossProduct(Vector3D a, Vector3D b)
	{
		return new Vector3D(a.y*b.z-a.z*b.y, a.z*b.x-a.x*b.z, a.x*b.y-a.y*b.x);
	}
	

	/**
	 * Calculate the difference between two Vector3Ds.
	 * 
	 * @param a
	 * @param b
	 * @return The difference between the two Vector3Ds, <b>a</b> - <b>b</b>.
	 */
	public static Vector3D difference(Vector3D a, Vector3D b)
	{
		return new Vector3D(a.x-b.x, a.y-b.y, a.z-b.z);
	}

	/**
	 * Calculate the sum of two Vector3Ds.
	 * 
	 * @param a
	 * @param b
	 * @return The sum of the two Vector3Ds, <b>a</b>+<b>b</b>.  
	 */
	public static Vector3D sum(Vector3D a, Vector3D b)
	{
		return new Vector3D(a.x+b.x, a.y+b.y, a.z+b.z);
	}

	/**
	 * Calculate the sum of three Vector3Ds.
	 * 
	 * @param a
	 * @param b
	 * @param c
	 * @return The sum of the three Vector3Ds, <b>a</b>+<b>b</b>+<b>c</b>.  
	 */
	public static Vector3D sum(Vector3D a, Vector3D b, Vector3D c)
	{
		return new Vector3D(a.x+b.x+c.x, a.y+b.y+c.y, a.z+b.z+c.z);
	}

	/**
	 * Calculate the sum of four Vector3Ds.
	 * 
	 * @param a
	 * @param b
	 * @param c
	 * @param d
	 * @return The sum of the four Vector3Ds, <b>a</b>+<b>b</b>+<b>c</b>+<b>d</b>.  
	 */
	public static Vector3D sum(Vector3D a, Vector3D b, Vector3D c, Vector3D d)
	{
		return new Vector3D(a.x+b.x+c.x+d.x, a.y+b.y+c.y+d.y, a.z+b.z+c.z+d.z);
	}
	
	public static Vector3D mean(Vector3D a, Vector3D b)
	{
		return new Vector3D(0.5*(a.x+b.x), 0.5*(a.y+b.y), 0.5*(a.z+b.z));
	}

	/**
	 * Calculate the product between a scalar and a Vector3D.
	 * 
	 * @param r
	 * @param v
	 * @return The (immutable) scalar product of a Vector3D, <i>r x</i><b>i</b>+<i>r y</i><b>j</b>+<i>r z</i><b>k</b>
	 */
	public static Vector3D scalarTimesVector3D(double r, Vector3D v)
	{
		return new Vector3D(r*v.x, r*v.y, r*v.z);
	}
		
	public static double getDistance(Vector3D a, Vector3D b)
	{
		return Vector3D.difference(a, b).getLength();
	}

	/**
	 * @param a
	 * @param b
	 * @return	the square of the distance between a and b
	 */
	public static double getDistance2(Vector3D a, Vector3D b)
	{
		return Vector3D.difference(a, b).getModSquared();
	}

	/**
	 * Calculate the scalar product of this Vector3D with another Vector3D.
	 * @param v The Vector3D (along with this Vector3D) for which the scalar product is to be calculated.
	 * @return The scalar product, <b>this</b>&bull;<b>v</b>
	 */
	public double getScalarProductWith(Vector3D v)
	{       //returns the scalar product of the current instance with
		return x*v.x+y*v.y+z*v.z;				//Vector3D v
	}
	
	/**
	 * @param xFactor
	 * @param yFactor
	 * @param zFactor
	 * @return	The componentwise  product
	 */
	public Vector3D getComponentwiseProductWith(double xFactor, double yFactor, double zFactor)
	{
		return new Vector3D(xFactor*x, yFactor*y, zFactor*z);
	}
	
	/**
	 * Calculate the absolute value of the vector.
	 * @return a vector where all componets are positive (absolute)
	 */	
	public Vector3D getAbs()
	{
		return new Vector3D(Math.abs(x),Math.abs(y),Math.abs(z));
	}

	/**
	 * Calculate the squared length of the Vector3D.
	 * @return The modulus squared, <i>x</i><sup>2</sup>+<i>y</i><sup>2</sup>+<i>z</i><sup>2</sup>.
	 */
	public double getModSquared()
	{	//returns the modulus squared of the current instance
		return x*x+y*y+z*z;
	}

	/**
	 * Calculate the length of the Vector3D.
	 * @return The magnitude, sqrt(<i>x</i><sup>2</sup>+<i>y</i><sup>2</sup>+<i>z</i><sup>2</sup>).
	 */
	public double getLength()
	{
		//returns the length of the current Vector3D
		return Math.sqrt(x*x+y*y+z*z);
	}
	
	/**
	 * Normalize a Vector3D by dividing it by its original length.
	 * If the vector is the zero vector, a unit vector in the x direction is returned.
	 * @return The normalized Vector3D
	 */
	public Vector3D getNormalised()
	{
		double length = getLength();
		if(length != 0.0) return this.getProductWith(1./getLength());
		
		//  the length of the vector is 0; return (1,0,0)
		return new Vector3D(1, 0, 0);
	}

	/**
	 * The negative of the Vector3D.  Each element is multiplied by minus one.
	 * @return Minus the original Vector3D, -<b>this</b>
	 */
	public Vector3D getReverse()
	{
		return new Vector3D(-x, -y, -z);
	}

	/**
	 * Add a second Vector3D (immutably) to this one.  
	 * @param v The Vector3D to be added.
	 * @return A Vector3D, <b>this</b>+<b>v</b>, which is the current instance minus Vector3D <b>v</b>.
	 */
	public Vector3D getSumWith(Vector3D v)
	{				
		return new Vector3D(x+v.x, y+v.y, z+v.z);
	}

	/**
	 * Subtract a given Vector3D from this current Vector3D.
	 * @param v A Vector3D that is subtracted from <b>this</b>
	 * @return A Vector3D which is the current instance minus the Vector3D <b>v</b>, i.e. <b>this</b>-<b>v</b>.
	 */
	public Vector3D getDifferenceWith(Vector3D v)
	{  			 
		return new Vector3D(x-v.x, y-v.y, z-v.z);
	}

	/**
	 * The scalar product with this Vector3D.
	 * @param f A scalar with which this Vector3D is multiplied.
	 * @return A Vector3D which is the current instance multiplied by the scalar <i>f</i>, i.e. <i>f</i>.<b>this</b> 
	 */
	public Vector3D getProductWith(double f)
	{		
		return new Vector3D(f*x, f*y, f*z);
	}

	/**
	 * The cross product between this and another Vector3D, <b>v</b>.
	 * @param v The second argument in the cross product
	 * @return The cross product of the current instance with Vector3D v, i.e. <i><b>this</b></i>&times;<i><b>v</b></i>.
	public Vector3D crossProduct(Vector3D v) {		
		return new Vector3D((y*v.z)-(z*v.y),(z*v.x)-(x*v.z),(x*v.y)-(y*v.x));
	}

	/**
	 * This method (immutably) projects this Vector3D onto another.  The resulting 
	 * Vector3D is in the same direction as <b>v</b>, but its length is given by the
	 * component of <b>this</b> that points along that direction.
	 * 
	 * @return The projection Vector3D.
	 */
	public Vector3D getProjectionOnto(Vector3D v)
	{
		return v.getProductWith(v.getScalarProductWith(this)/v.getModSquared());
	}
	
	/**
	 * Just another name for getProjectionOnto
	 * @param v
	 * @return
	 */
	public Vector3D getPartParallelTo(Vector3D v)
	{
		return getProjectionOnto(v);
	}
	
	/**
	 * @param v
	 * @return	the part of the vector that's perpendicular to v
	 */
	public Vector3D getPartPerpendicularTo(Vector3D v)
	{
		return getDifferenceWith(getProjectionOnto(v));
	}
	
	/**
	 * @param v1
	 * @param v2
	 * @return	the part of the vector that's perpendicular to v1 and v2
	 */
	public Vector3D getPartPerpendicularTo(Vector3D v1, Vector3D v2)
	{
		// take the part of the vector that is perpendicular to <v1>, and then the part of that vector that is perpendicular to <v2>
		// return getPartPerpendicularTo(v1).getPartPerpendicularTo(v2);
		
		// take the part of the vector that is in the direction of <v1> x <v2>, and therefore perpendicular to both <v1> and <v2>
		return getPartParallelTo(Vector3D.crossProduct(v1, v2));
	}
	
	/**
	 * Change to the basis given by <b>v1</b>, <b>v2</b> and <b>v3</b>.
	 * @param v1 first basis vector
	 * @param v2 second basis vector
	 * @param v3 third basis vector
	 * @return The vector in the (v1, v2, v3) basis
	 */
	public Vector3D toBasis(Vector3D v1, Vector3D v2, Vector3D v3)
	{
		// Want to find c1, c2 and c3 such that
		// 	<b>x</b> = c1 <b>v1</b> + c2 <b>v2</b> + c3 <b>v3</b>	(1)
		// From now on drop <b></b> formatting.
		// Calculate
		// 	(1).v1: x.v1 = c1 v1.v1 + c2 v2.v1 + c3 v3.v1	(2)
		// 	(1).v2: x.v2 = c1 v1.v2 + c2 v2.v2 + c3 v3.v2	(3)
		//	(1).v3: x.v3 = c1 v1.v3 + c2 v2.v3 + c3 v3.v3	(4)
		// The solution (found by Mathematica) is
		// 	c1 = (v2.v2*v3.v3*x.v1 + v1.v3*v2.v3*x.v2 + v1.v2*v2.v3*x.v3 
		//       - v2.v3^2*x.v1 - v1.v2*v3.v3*x.v2 - v1.v3*v2.v2*x.v3) / d,
		// 	c2 = (v1.v3*v2.v3*x.v1 + v1.v1*v3.v3*x.v2 + v1.v2*v1.v3*x.v3
		//       - v1.v2*v3.v3*x.v1 - v1.v3^2*x.v2 - v1.v1*v2.v3*x.v3) / d,
		//  c3 = (v1.v2*v2.v3*xv1 + v1.v2*v1.v3*x.v2 + v1.v1*v2.v2*x.v3
		//	     - v1.v3*v2.v2*x.v1 - v1.v1*v2.v3*x.v2 - v1.v2^2*x.v3) / d
		// where
		// 	d = v1.v1*v2.v2*v3.v3 + 2*v1.v2*v1.v3*v2.v3 - v1.v1*v2.v3^2 - v2.v2*v1.v3^2 - v3v3*v1.v2^2
		double
			v1v1 = v1.getModSquared(),	// v1.v1
			v2v2 = v2.getModSquared(),	// v2.v2
			v3v3 = v3.getModSquared(),	// v3.v3
			v1v2 = Vector3D.scalarProduct(v1, v2),	// v1.v2
			v1v3 = Vector3D.scalarProduct(v1, v3),	// v1.v3
			v2v3 = Vector3D.scalarProduct(v2, v3),	// v2.v3
			xv1 = getScalarProductWith(v1),	// x.v1
			xv2 = getScalarProductWith(v2),	// x.v2
			xv3 = getScalarProductWith(v3),	// x.v3
			d = v1v1*v2v2*v3v3 + 2*v1v2*v1v3*v2v3 - v1v1*v2v3*v2v3 - v2v2*v1v3*v1v3 - v3v3*v1v2*v1v2;	// common denominator
		
		return new Vector3D(
				(v2v2*v3v3*xv1 + v1v3*v2v3*xv2 + v1v2*v2v3*xv3 - v2v3*v2v3*xv1 - v1v2*v3v3*xv2 - v1v3*v2v2*xv3) / d,
				(v1v3*v2v3*xv1 + v1v1*v3v3*xv2 + v1v2*v1v3*xv3 - v1v2*v3v3*xv1 - v1v3*v1v3*xv2 - v1v1*v2v3*xv3) / d,
				(v1v2*v2v3*xv1 + v1v2*v1v3*xv2 + v1v1*v2v2*xv3 - v1v3*v2v2*xv1 - v1v1*v2v3*xv2 - v1v2*v1v2*xv3) / d
			);
	}


	/**
	 * Project back to <b>i</b>, <b>j</b>, <b>k</b> from <b>e</b><sub>x</sub>, <b>e</b><sub>y</sub>, <b>e</b><sub>z</sub>.  
	 * @param e1	basis vector 1
	 * @param e2	basis vector 2
	 * @param e3	basis vector 3
	 * @return x <b>e1</b> + y <b>e2</b> + z <b>e3</b>
	 */
	public Vector3D fromBasis(Vector3D e1, Vector3D e2, Vector3D e3)
	{
		return Vector3D.sum(
				e1.getProductWith(x),	// x * e1
				e2.getProductWith(y),	// y * e2
				e3.getProductWith(z)	// z * e3
			);
	}

	public Vector3D toBasis(ArrayList<Vector3D> basis)
	{
		return toBasis(basis.get(0), basis.get(1), basis.get(2));
	}
	
	public static Vector3D toBasis(Vector3D vector, ArrayList<Vector3D> basis)
	{
		return vector.toBasis(basis.get(0), basis.get(1), basis.get(2));
	}

	public Vector3D fromBasis(ArrayList<Vector3D> basis)
	{
		return fromBasis(basis.get(0), basis.get(1), basis.get(2));
	}

	public static Vector3D fromBasis(Vector3D vector, ArrayList<Vector3D> basis)
	{
		return vector.fromBasis(basis.get(0), basis.get(1), basis.get(2));
	}

	/**
	 * Make this vector a given length
	 * @param length
	 * @return this vector, scaled to the given length
	 */
	public Vector3D getWithLength(double length)
	{
		return scalarTimesVector3D(length, this.getNormalised());
	}

	/**
	 * Get the fractional projection length <i>f</i> of this Vector3D onto the vector <b>v</b>.
	 * The component of this vector in the direction of <b>v</b> is then <i>f</i> <b>v</b>.
	 *
	 * @param v The Vector3D onto which <b>this</b> is to be projected.
	 * @return The fractional distance along the Vector3D.
	 */
	public double getFractionalProjectionLength(Vector3D v)
	{
		return  Vector3D.scalarProduct(this, v) / v.getModSquared();
	}

	/**
	 * Project onto a two dimensional basis given by <b>v</b><sub>1</sub> and <b>v</b><sub>2</sub>.
	 * This method calculates the coefficients <i>c<sub>1</sub></i> and <i>c<sub>2</sub></i> such that
	 * 		this = <i>c<sub>1</sub></i>*<b>v</b><sub>1</sub> + <i>c<sub>2</sub></i>*<b>v</b><sub>2</sub> + <b>n</b>,
	 * where <b>n</b> is perpendicular to both <b>v</b><sub>1</sub> and <b>v</b><sub>2</sub>
	 * 
	 * @param v1 The first Vector3D
	 * @param v2 The second Vector3D
	 * @return The two coefficients on the projection plane (<i>c<sub>1</sub>, <i>c<sub>2</sub>).
	 */
	public Vector2D calculateDecomposition(Vector3D v1, Vector3D v2)
	{
		// calculate the decomposition in terms of  v1, v2, and a vector perpendicular to v1 and v2...
		Vector3D d = toBasis(v1, v2, crossProduct(v1, v2));
				
		// ... and then simply ignore the component corresponding to the third vector
		return new Vector2D(d.x, d.y);
	}
	
	/**
	 * This should be faster than the above implementation of the calculateDecomposition method, but it's also dicky
	 * @param v1
	 * @param v2
	 * @return	rubbish, at least sometimes
	 */
	public Vector2D calculateDecompositionFasterButDicky(Vector3D v1, Vector3D v2)
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
			v1v2 = Vector3D.scalarProduct(v1, v2),	// v1.v2
			xv1 = getScalarProductWith(v1),	// x.v1
			xv2 = getScalarProductWith(v2),	// x.v2
			d = v1v1*v2v2 - v1v2*v1v2;	// common denominator

		return new Vector2D(
				(v1v2*xv2 - v2v2*xv1)/d,
				(v1v2*xv1 - v1v1*xv2)/d
		);
	}

	/**
	 * Convert the type of this Vector3D into a Jama Matrix object representing a column vector
	 * 
	 * @return A Jama column vector representing this optics.raytrace Vector3D
	 */
	public Matrix toJamaColumnVector()
	{
		double[][] components = {{x}, {y}, {z}};
		return new Matrix(components);
	}
	
	/**
	 * Convert the type of this Vector3D into a Jama Matrix object representing a row vector
	 * 
	 * @return A Jama row vector representing this optics.raytrace Vector3D
	 */
	public Matrix toJamaRowVector()
	{
		double[][] components = {{x, y, z}};
		return new Matrix(components);
	}
	
	/**
	 * @param a
	 * @param b
	 * @return	a Jama matrix representing the outer product of a and b
	 */
	public static Matrix outerProduct(Vector3D a, Vector3D b)
	{
		return a.toJamaColumnVector().times(b.toJamaRowVector());
	}

	/**
	 * Represent this Vector3D as a string of text.  The format is <Vector2D x=#, y=#>.
	 * @return A string representation of this 2D Vector3D.
	 */
	@Override
	public String toString()
	{
		return "(" + MyMath.doubleToString(x) + ", " + MyMath.doubleToString(y) + ", " + MyMath.doubleToString(z) + ")";
	}

	/**
	 * @param digits
	 * @return	a string representing the vector, with each component given to <digits> digits
	 */
	public String toString(int digits)
	{
		return "(" + MyMath.doubleToString(x, digits) + ", " + MyMath.doubleToString(y, digits) + ", " + MyMath.doubleToString(z, digits) + ")";
	}

	/**
	 * Find a (normalised) normal to a vector.
	 * If <b>v</b> is the zero vector, return <b>x</b>.
	 * If <b>v</b> is along the x direction, return <b>y</b>.
	 * Otherwise return the part of <b>x</b> that is perpendicular to <b>v</b>.
	 * @param v	the vector
	 * @return	a (normalised) normal to the vector
	 */
	public static Vector3D getANormal(Vector3D v)
	{
		// if the x and y components of v are both zero, i.e. if v is either pointing in the z direction or is equal to zero, return a unit vector in the x direction
		if((v.x == 0.) && (v.y == 0.)) return new Vector3D(1, 0, 0);
		
		// v is neither identical to zero, and nor does it point in the z direction, so v x z gives a vector perpendicular to v; return this vector, normalised
		return Vector3D.crossProduct(v, Z).getNormalised();
		
//		// if v is the zero vector, return x
//		if(v.equals(O)) return new Vector3D(1,0,0);
//		
//		// if v is along the x axis, return y
//		if((v.y == 0.) && (v.z == 0.)) return new Vector3D(0, 1, 0);
//		
//		// otherwise return the part of x that's perpendicular to v
//		return X.getPartPerpendicularTo(v).getNormalised();
	}
	
	public boolean equals(Vector3D v)
	{
		return (x==v.x) && (y==v.y) && (z==v.z);
	}
	
	/**
	 * @return	true if one or more of the components is infinite (positive or negative infinity)
	 */
	public boolean isComponentInf()
	{
		return(
				(Math.abs(x) == Double.POSITIVE_INFINITY) ||
				(Math.abs(y) == Double.POSITIVE_INFINITY) ||
				(Math.abs(z) == Double.POSITIVE_INFINITY)
			);
	}

	/**
	 * @return	true if one or more of the components is "huge" (MyMath.HUGE)
	 */
	public boolean isComponentHuge()
	{
		return(
				(Math.abs(x) == MyMath.HUGE) ||
				(Math.abs(y) == MyMath.HUGE) ||
				(Math.abs(z) == MyMath.HUGE)
			);
	}

	/**
	 * @param min
	 * @param max
	 * @return	a Vector3D with (pseudo)random components ranging between min and max
	 */
	public static Vector3D getRandomVector3D(double min, double max)
	{
		return new Vector3D(
				min+(max-min)*Math.random(),
				min+(max-min)*Math.random(),
				min+(max-min)*Math.random()
			);
	}
}
