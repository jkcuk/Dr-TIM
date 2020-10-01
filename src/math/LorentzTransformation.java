package math;

/**
 * @author johannes
 * A class for relativistic ray tracing.
 */
public class LorentzTransformation
extends SpaceTimeTransformation
{
	// pre-calculated variables
	
	private double beta2;	// beta^2 = beta.beta
	private double gamma;	// 1/sqrt(1-beta^2)
	
	
	// constructor
	
	public LorentzTransformation(Vector3D beta)
	{
		super(beta);
	}
	
	
	// getters & setters
	
	@Override
	public void setBeta(Vector3D beta) {
		super.setBeta(beta);
		
		beta2 = Vector3D.scalarProduct(beta, beta);
		gamma = 1./Math.sqrt(1.-beta2);
	}	
	
	
	
	
	
	@Override
	public SpaceTimeTransformationType getSpaceTimeTransformationType() {
		return SpaceTimeTransformationType.LORENTZ_TRANSFORMATION;
	}


	@Override
	public Vector3D getTransformedPosition(Vector3D x, double t) {
		if(beta2 == 0.0) return x;
		
		return Vector3D.sum(
				x,
				beta.getProductWith((gamma-1) * Vector3D.scalarProduct(beta, x) / beta2 - gamma*c*t)
			);
	}


	@Override
	public double getTransformedTime(Vector3D x, double t) {
		if(beta2 == 0.0) return t;

		// from [1]:
		// ct' = gamma (c t - beta.x), so t' = gamma (t - (beta.x)/c)
		return gamma*(t - Vector3D.scalarProduct(beta, x)/c);
	}


	@Override
	public Vector3D getTransformedLightRayDirection(Vector3D d) {
		// first method: draw a straight line through two "events" that are the ray passing through two points;
		// the first event is the ray passing through the origin, O, at t=0;
		// the second event is the ray passing through the position O+d at t=-|d|/c 
		return getTransformedPosition(d, -d.getLength()/c);
	}

//	// inverse Lorentz transformation
//	
//	@Override
//	public Vector3D getInverseTransformedPosition(Vector3D x, double t) {
//		if(beta2 == 0.0) return x;
//		
//		// the inverse transformation is the same as the (forward) transformation, but with the negative beta
//		return Vector3D.sum(
//				x,
//				beta.getProductWith((gamma-1) * Vector3D.scalarProduct(beta, x) / beta2 + gamma*c*t)
//			);
//	}
//
//
//	@Override
//	public double getInverseTransformedTime(Vector3D x, double t) {
//		if(beta2 == 0.0) return t;
//
//		// the inverse transformation is the same as the (forward) transformation, but with the negative beta
//		return gamma*(t + Vector3D.scalarProduct(beta, x)/c);
//	}
//
//
//	@Override
//	public Vector3D getInverseTransformedLightRayDirection(Vector3D d) {
//		// first method: draw a straight line through two "events" that are the ray passing through two points;
//		// the first event is the ray passing through the origin, O, at t=0;
//		// the second event is the ray passing through the position O+d at t=-|d|/c 
//		return getInverseTransformedPosition(d, -d.getLength()/c);
//	}


	/**
	 * @param x	position of event in frame 1
	 * @param t	time of event in frame 1
	 * @param beta	velocity/c of frame 2 in frame 1
	 * @return x', i.e. the (Lorentz-transformed) position of the event in frame 2
	 */
	public static Vector3D getTransformedPosition(Vector3D x, double t, Vector3D beta)
	{
		// System.out.println("getLorentzTransformedPosition("+x+", "+t+", "+beta+")");
		
		double beta2 = Vector3D.scalarProduct(beta, beta);
		if(beta2 == 0.0) return x;
		
		double gamma = 1./Math.sqrt(1.-beta2);
		
		// from [1]:
		// x' = x + (gamma-1) (beta.x) beta / beta^2 - gamma beta c t
		return Vector3D.sum(
				x,
				beta.getProductWith((gamma-1) * Vector3D.scalarProduct(beta, x) / beta2 - gamma*c*t)
			);
	}

	/**
	 * @param x	position of event in frame 1
	 * @param t	time of event in frame 1
	 * @param beta	velocity/c of frame 2 in frame 1
	 * @return t', i.e. the (Lorentz-transformed) time of the event in frame 2
	 */
	public static double getTransformedTime(Vector3D x, double t, Vector3D beta)
	{
		double beta2 = Vector3D.scalarProduct(beta, beta);
		if(beta2 == 0.0) return t;
		
		double gamma = 1./Math.sqrt(1.-beta2);

		// from [1]:
		// ct' = gamma (c t - beta.x), so t' = gamma (t - (beta.x)/c)
		return gamma*(t - Vector3D.scalarProduct(beta, x)/c);
	}

	/**
	 * @param d	light-ray direction in frame 1
	 * @param beta	velocity/c of frame 1 in frame 2
	 * @return d', i.e. the (Lorentz-transformed) light-ray direction in frame 2
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
		// the second event is the ray passing through the position O+d at t=-|d|/c 
		return getTransformedPosition(d, -d.getLength()/c, beta);

//		// normalised light-ray direction
//		Vector3D dHat = d.getNormalised();
//		
//		double gamma = getGamma(beta);
//		
//		return Vector3D.sum(
//				dHat,
//				beta.getProductWith((gamma-1) * Vector3D.scalarProduct(beta, dHat) / beta2),
//				beta.getProductWith(gamma)
//			);
	}
}
