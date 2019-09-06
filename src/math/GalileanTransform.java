package math;

/**
 * @author johannes, ruaridh 
 * A class for comparing non-relativistic (but dynamic) raytracing to relativistic ray tracing.
 */
public class GalileanTransform
// extends SpaceTimeTransform
{
	/**
	 * speed of light, in m/s (from http://en.wikipedia.org/wiki/Speed_of_light)
	 */
	public static final double c = 1;	// 299792458;

	/**
	 * @param x	position of event in frame 1
	 * @param t	time of event in frame 1
	 * @param beta	velocity/c of frame 2 in frame 1
	 * @return x', i.e. the (Galilean-transformed) position of the event in frame 2
	 */
	public static Vector3D getTransformedPosition(Vector3D x, double t, Vector3D beta)
	{
		// x' = x - beta c t
		return Vector3D.difference(
				x,
				beta.getProductWith(c*t)
			);
	}
	
	/**
	 * @param x	position of event in frame 1
	 * @param t	time of event in frame 1
	 * @param beta	velocity/c of frame 2 in frame 1
	 * @return	t', i.e. the (Galilean-transformed) time of the event in frame 2 (which is the same as t)
	 */
	public static double getTransformedTime(Vector3D x, double t, Vector3D beta)
	{
		return t;
	}

	/**
	 * @param d	light-ray direction in frame 1
	 * @param beta	velocity/c of frame 1 in frame 2
	 * @return d', i.e. the (Galilean-transformed) light-ray direction in frame 2
	 */
	public static Vector3D getTransformedLightRayDirection(Vector3D d, Vector3D beta)
	{
//		double beta2 = Vector3D.scalarProduct(beta, beta);
//		// is the speed zero?
//		if(beta2 == 0.0)
//		{
//			// then just return the initial light-ray direction
//			return d;
//		}
		
		// first method: draw a straight line through two "events" that are the ray passing through two points;
		// the first event is the ray passing through the origin, O, at t=0;
		// the second event is the ray passing through the position O+d at t=|d|/c 
		return getTransformedPosition(d, -d.getLength()/c, beta);
	}
}
