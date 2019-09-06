package math;

/**
 * @author johannes, ruaridh 
 * A class with implementations GalileanTransform and LorentzTransform
 */
public class SpaceTimeTransform
{
	/**
	 * speed of light, in m/s (from http://en.wikipedia.org/wiki/Speed_of_light)
	 */
	public static final double c = 1;	// 299792458;

	/**
	 * @param x	position of event in frame 1
	 * @param t	time of event in frame 1
	 * @param beta	velocity/c of frame 2 in frame 1
	 * @return x', i.e. the transformed position of the event in frame 2
	 */
	public static Vector3D getTransformedPosition(Vector3D x, double t, Vector3D beta)
	{
		return x;
	}

	/**
	 * @param x
	 * @param t
	 * @param beta
	 * @return
	 */
	public static double getTransformedTime(Vector3D x, double t, Vector3D beta)
	{
		return t;
	}

	/**
	 * @param d	light-ray direction in frame 1
	 * @param beta	velocity/c of frame 1 in frame 2
	 * @return d', i.e. the transformed light-ray direction in frame 2
	 */
	public static Vector3D getTransformedLightRayDirection(Vector3D d, Vector3D beta)
	{
		return d;
	}
}
