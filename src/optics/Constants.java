package optics;

/**
 * A few constants related to optics.
 * 
 * @author Johannes Courtial
 */
public class Constants
{
	/**
	 * The speed of light, in SI units (m/s).
	 * (From http://en.wikipedia.org/wiki/Speed_of_light)
	 */
	public static final double SPEED_OF_LIGHT = 299792458.;

	/**
	 * The refractive index of vacuum.
	 * (From http://en.wikipedia.org/wiki/List_of_refractive_indices) 
	 */
	public static final double REFRACTIVE_INDEX_VACUUM = 1.;

	/**
	 * The refractive index of air at 0 ¡C and 1 atm for lambda = 589.29nm.
	 * (From http://en.wikipedia.org/wiki/List_of_refractive_indices) 
	 */
	public static final double REFRACTIVE_INDEX_AIR = 1.000293;

	/**
	 * The refractive index of water at 20¡C for lambda = 589.29nm.
	 * (From http://en.wikipedia.org/wiki/List_of_refractive_indices) 
	 */
	public static final double REFRACTIVE_INDEX_WATER = 1.3330;

	/**
	 * The refractive index of fused silica at "room temperature" for lambda = 589.29nm.
	 * (From http://en.wikipedia.org/wiki/List_of_refractive_indices) 
	 */
	public static final double REFRACTIVE_INDEX_FUSED_SILICA = 1.458;
}
