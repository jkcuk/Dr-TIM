package math;

/**
 * @author johannes, ruaridh 
 * A class with implementations GalileanTransformation and LorentzTransformation
 */
public abstract class SpaceTimeTransformation
{
	/**
	 * speed of light, in m/s (from http://en.wikipedia.org/wiki/Speed_of_light)
	 */
	public static final double c = 1;	// 299792458;
	
	public enum SpaceTimeTransformationType
	{
		GALILEAN_TRANSFORMATION("Galilean transformation"),
		LORENTZ_TRANSFORMATION("Lorentz transformation");
		
		private String description;
		private SpaceTimeTransformationType(String description) {this.description = description;}	
		@Override
		public String toString() {return description;}
	}


	/**
	 * v/c
	 */
	protected Vector3D beta;
	

	// constructor
	
	public SpaceTimeTransformation(Vector3D beta)
	{
		setBeta(beta);
	}
	
	
	// getters & setters
	
	public Vector3D getBeta() {
		return beta;
	}

	public void setBeta(Vector3D beta) {
		this.beta = beta;
	}
	
	public boolean isBetaZero() {
		return (beta.x == 0.0) && (beta.y == 0.0) && (beta.z == 0.0);
	}

	
	public abstract SpaceTimeTransformationType getSpaceTimeTransformationType();
	
	/**
	 * @param x	position of event in frame 1
	 * @param t	time of event in frame 1
	 * @return x', i.e. the transformed position of the event in frame 2
	 */
	public abstract Vector3D getTransformedPosition(Vector3D x, double t);

	/**
	 * @param x
	 * @param t
	 * @return
	 */
	public abstract double getTransformedTime(Vector3D x, double t);

	/**
	 * @param d	light-ray direction in frame 1
	 * @return d', i.e. the transformed light-ray direction in frame 2
	 */
	public abstract Vector3D getTransformedLightRayDirection(Vector3D d);

	
//	// inverse transformation
//	
//	/**
//	 * @param x	position of event in frame 1
//	 * @param t	time of event in frame 1
//	 * @return x', i.e. the transformed position of the event in frame 2
//	 */
//	public abstract Vector3D getInverseTransformedPosition(Vector3D x, double t);
//
//	/**
//	 * @param x
//	 * @param t
//	 * @return
//	 */
//	public abstract double getInverseTransformedTime(Vector3D x, double t);
//
//	/**
//	 * @param d	light-ray direction in frame 1
//	 * @return d', i.e. the transformed light-ray direction in frame 2
//	 */
//	public abstract Vector3D getInverseTransformedLightRayDirection(Vector3D d);
	
	
	
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
