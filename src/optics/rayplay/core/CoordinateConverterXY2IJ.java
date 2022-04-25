package optics.rayplay.core;

/**
 * Defines an interface that allows conversion between simulation coordinates, x and y,
 * and screen coordinates, i and j.
 * 
 * @author johannes
 */
public interface CoordinateConverterXY2IJ
{
	/**
	 * Return the unrounded i coordinate.  Handy for saving as SVG.
	 * @param x
	 * @return	the unrounded, double-precision, i coordinate that corresponds to x
	 */
	public double x2id(double x);

	/**
	 * Return the unrounded j coordinate.  Handy for saving as SVG.
	 * @param y
	 * @return	the unrounded, double-precision, j coordinate that corresponds to y
	 */
	public double y2jd(double y);

	/**
	 * @param x
	 * @return	the i coordinate corresponding to x
	 */
	public int x2i(double x);

	/**
	 * @param y
	 * @return	the j coordinate corresponding to y
	 */
	public int y2j(double y);
	
	/**
	 * @param i
	 * @return	the x coordinate corresponding to i
	 */
	public double i2x(double i);

	/**
	 * @param y
	 * @return	the j coordinate corresponding to y
	 */
	public double j2y(double j);
}
