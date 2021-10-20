package math;

import java.text.DecimalFormat;

/**
 * 
 * In order to simplify any mathematics, such code is stored in static methods within this class.
 * @author Dean Lambert
 * @author Johannes Courtial
 */
public class MyMath
{
	/**
	 * The machine precision should be significantly less than this --- see http://stackoverflow.com/questions/3728246/what-should-be-the-epsilon-value-when-performing-double-value-equal-comparison
	 */
	public static final double EPSILON = 1E-10;
	/**
	 * A reasonably small number on the scale of SI lengths.
	 * If this number is too small, then things like calculateNextClosestRayIntersection etc. behave strangely.
	 */
	public static final double TINY = 1E-4;
	
	/**
	 * A small number, which shows up in the interactive version
	 */
	public static final double SMALL = 0.01;
	
	/**
	 * A reasonably big number on the scale of SI lengths.
	 */
	public static final double HUGE = 1E5;

	/**
	 * Calculates the square of a given double precision number.
	 * @param x The number to be squared
	 * @return The square of the number, i.e. <i>x</i>*<i>x</i>
	 */
	public static double square(double x){
		return x*x;
	}
	
	public static double toPower3(double x) {
		return x*x*x;
	}
	
	
	/**
	 * @param xy (x, y)
	 * @return atan2(y, x)
	 */
	public static double xy2phi(Vector2D xy)
	{
		return Math.atan2(xy.y, xy.x);
	}
	
	/**
	 * This method converts from degrees to radians.
	 * 
	 * @param deg An angle in degrees to be converted.
	 * @return The corresponding angle in radians.
	 */
	public static double deg2rad(double deg)
	{
		return deg/180.*Math.PI;
	}
	
	public static double rad2deg(double rad)
	{
		return rad*180/Math.PI;
	}
	
	/**
	 * basically a Heaviside step function
	 * @param d
	 * @return	signum(d) if d != 0, otherwise 1
	 */
	public static double signumNever0(double d)
	{
		if(d == 0.0) return 1;
		else
			return Math.signum(d);
	}
	
	/**
	 * Is the number x between a and b?
	 * @param x
	 * @param a
	 * @param b
	 * @return true if x is between a and b, false otherwise
	 */
	public static boolean isBetween(double x, double a, double b)
	{
		double
			min = Math.min(a, b),
			max = Math.max(a, b);
		
		return (min <= x) && (x <= max);
	}
	
	/**
	 * @param i
	 * @param j
	 * @return	i mod j
	 */
	public static int mod(int i, int j)
	{
		int m = i % j;
		return (m<0)?(m+j):m;
	}
		
	static public String doubleToString(double x)
	{
		return new DecimalFormat("#0.###").format(x);	// at most three decimals
	}

	static public String doubleToString(double x, int digits)
	{
		String hashes = "";
		for(int i=0; i<digits; i++) hashes += "0";
		return new DecimalFormat("#0."+hashes).format(x);	
	}
}

