package optics.raytrace.panorama;

/**
 * @author johannes
 * Used to define left side/right side in anaglyph-related classes
 */
public enum Side
{
	LEFT(+1),
	RIGHT(-1),
	CENTRE(0);
	
	private double factor;
	private Side(double factor) {this.factor = factor;}

	/**
	 * @return	-1 for right, +1 for left
	 */
	public double toFactor() {return factor;}
}
