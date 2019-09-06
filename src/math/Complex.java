package math;

 /**
  * 
  * @author George
  *
  * Complex class defines a range of operations in complex space.
  */

public class Complex
{	
	public static final Complex I = new Complex(0, 1);
	
	// real and imaginary parts
	public double r, i;
	
	/**
	 * The complex number r + I i
	 * @param r
	 * @param i
	 */
	public Complex(double r, double i)
	{	
		this.r = r;
		this.i = i;
	}
	
	/**
	 * The complex number r + 0 I
	 * @param r
	 */
	public Complex(double r)
	{
		this.r = r;
		i = 0.;
	}
	
	/**
	 * Create a clone of the original
	 * @param original
	 */
	public Complex(Complex original)
	{
		r = original.r;
		i = original.i;
	}
	
	@Override
	public Complex clone()
	{
		return new Complex(this);
	}
	
	public double getRe()
	{
		return r;
	}
	
	public double getIm()
	{
		return i;
	}
	
	public double getMod()
	{
		return modulus(this);
	}
	
	public double getArg()
	{
		return arg(this);
	}
		 
	/**
	 * @param c
	 * @return c* (the complex conjugate of c)
	 */
	public static Complex conjugate(Complex c)
	{
		return new Complex(c.r, -c.i);
	}
	
	/**
	 * @param a
	 * @param b
	 * @return a+b
	 */
	public static Complex sum(Complex a, Complex b)
	{
		return new Complex(a.r + b.r, a.i +b.i);
	}
	
	/**
	 * @param a
	 * @param b
	 * @return a-b
	 */
	public static Complex difference(Complex a, Complex b)
	{
		return new Complex(a.r-b.r, a.i-b.i);
	}
	
	/**
	 * Product of a real number and a complex number
	 * @param s
	 * @param c
	 * @return s*c
	 */
	public static Complex product(double s, Complex c)
	{
		return new Complex(s*c.r, s*c.i);
	}
	
	/**
	 * Product of two complex numbers
	 * @param a
	 * @param b
	 * @return a*b
	 */
	public static Complex product(Complex a, Complex b)
	{
		return new Complex((a.r * b.r) - (a.i * b.i), (a.r * b.i) + (a.i * b.r));
	}

	//Scalar division of a Complex number 'a' by a scalar number
	public static Complex division(Complex a, double s)
	{
		return new Complex (a.r/s, a.i/s);
	}
	
	
	//Division of 2 complex numbers, c by d.
	public static Complex division(Complex c, Complex d)
	{
		Complex numerator = Complex.product(c,(Complex.conjugate(d)));
		Double denominator = Complex.modulusSquared(d);
		
		return  division(numerator,denominator);
	}
	
	public static Complex division(double d, Complex c)
	{
		return division(new Complex(d, 0), c);
	}

	//Calculate the Modulus Squared of a complex number.
	public static double modulusSquared(Complex a)
	{
		return a.r*a.r + a.i*a.i;
		// return Complex.multiply(a, Complex.conjugate(a)).r;
	}
	
	// Calculate the modulus of a complex number.
	public static double modulus(Complex a)
	{
		return Math.sqrt(Complex.modulusSquared(a));
	}
		
	public static Complex exp(Complex c)
	{
		// exp(c) = exp(r + I i) = exp(r) exp(I i)
		return product(
				Math.exp(c.r),	// exp(r)
				fromPolar(1., c.i)	// exp(I i)
			);
	}
	
	public static Complex ln(Complex z)
	{
		// the principal value of the logarithm of a complex number is defined as
		// Log z:  = ln | z | + iArg z.
		// (see http://en.wikipedia.org/wiki/Complex_logarithm)
		return new Complex(
				Math.log(z.getMod()),	// ln|z|
				z.getArg()
		);
	}
	
	/**
	 * Calculate exp(I phi)
	 * @param phi
	 * @return exp(I phi)
	 */
	public static Complex expI(double phi)
	{
		return new Complex(Math.cos(phi), Math.sin(phi));
	}
	
	// Calculate the argument of a complex number.
	public static double arg(Complex a)
	{
		return Math.atan2(a.i, a.r);
	}
	
	// Calculate the square of a complex number.
	public static Complex square(Complex a)
	{
		return Complex.product(a,a);
	}
	
	// Raising a complex number to arbitrary powers.
	public static Complex power(Complex a, double exponent)
	{
		// Turn imaginary Cartesian into a Vector2D in polar. (Makes argument easier to manipulate)
		Vector2D polarComplex = Complex.toPolar(a);
		
		// Return value in complex Cartesian.
		return Complex.fromPolar(
				Math.pow(polarComplex.x, exponent),	// modulus
				polarComplex.y*exponent	// argument
			);
	}
	
	/**
	 * Calculate the square root of a complex number
	 * @param a
	 * @return sqrt(a)
	 */
	public static Complex sqrt(Complex a)
	{
		return power(a, 0.5);
	}
	
	/**
	 * Calculate the sine of a complex number.
	 * @param a
	 * @return	sin(a)
	 */
	public static Complex sin(Complex a)
	{
		// according to Wikipedia (http://en.wikipedia.org/wiki/Trigonometric_functions),
		// sin(x + iy) = sin x cosh y + i cos x sinh y
		return new Complex(
				Math.sin(a.r)*Math.cosh(a.i),
				Math.cos(a.r)*Math.sinh(a.i)
			);
	}

	/**
	 * Calculate the cosine of a complex number.
	 * @param a
	 * @return	cos(a)
	 */
	public static Complex cos(Complex a)
	{
		// according to Wikipedia (http://en.wikipedia.org/wiki/Trigonometric_functions),
		// cos(x + iy) = cos x cosh y - i sin x sinh y
		return new Complex(
				Math.cos(a.r)*Math.cosh(a.i),
				-Math.sin(a.r)*Math.sinh(a.i)
			);
	}
	
	/**
	 * Calculate the arcsine of a complex number.
	 * @param a
	 * @return	arcsin(a)
	 */
	public static Complex arcsin(Complex a)
	{
		// according to Wikipedia (http://en.wikipedia.org/wiki/Inverse_trigonometric_functions),
		// arcsin(z) = -i ln(i x + sqrt(1-x^2))
		return product(
				new Complex(0, -1),	// -i
				ln(sum(
						product(new Complex(0,1), a),	// i a
						sqrt(difference(new Complex(1,0), square(a)))	// sqrt(1-a^2)
					))
			);
	}

	/**
	 * Calculate the arccosine of a complex number.
	 * @param a
	 * @return	arccos(a)
	 */
	public static Complex arccos(Complex a)
	{
		// according to Wikipedia (http://en.wikipedia.org/wiki/Inverse_trigonometric_functions),
		// arcsin(z) = -i ln(x + i sqrt(1-x^2))
		return product(
				new Complex(0, -1),	// -i
				ln(sum(
						a,
						product(
								new Complex(0,1), // i
								sqrt(difference(new Complex(1,0), square(a)))	// sqrt(1-a^2)
							)
					))
			);
	}
	

	/**
	 * @param c
	 * @return (r, phi) where c = r*exp(I phi) 
	 */
	public static Vector2D toPolar(Complex c)
	{
		double theta = Math.atan2(c.i, c.r);	// changed from atan by JC
		
		return new Vector2D(Complex.modulus(c), theta);
	}
	
	/**
	 * @param r
	 * @param phi
	 * @return r*exp(I phi)
	 */
	public static Complex fromPolar(double r, double phi)
	{
		return new Complex(r*Math.cos(phi),r*Math.sin(phi));
	}

	@Override
	public String toString() {
		if(i >= 0.) return "(" + r + "+" + i + "i)";
		else return "(" + r + i + "i)";
	}	
}
