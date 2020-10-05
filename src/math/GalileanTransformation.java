package math;

import optics.raytrace.exceptions.RayTraceException;

/**
 * @author johannes, ruaridh 
 * A class for comparing non-relativistic (but dynamic) raytracing to relativistic ray tracing.
 */
public class GalileanTransformation
extends SpaceTimeTransformation
{
	// constructor
	
	public GalileanTransformation(Vector3D beta)
	{
		super(beta);
	}
	
	
	@Override
	public SpaceTimeTransformationType getSpaceTimeTransformationType() {
		return SpaceTimeTransformationType.GALILEAN_TRANSFORMATION;
	}
	
	/* (non-Javadoc)
	 * @see math.SpaceTimeTransformation#getTransformedPosition(math.Vector3D, double)
	 */
	@Override
	public Vector3D getTransformedPosition(Vector3D x, double t) {
		// x' = x - beta c t
		return Vector3D.sum(
				x,
				beta.getProductWith(-c*t)
			);
	}


	/* (non-Javadoc)
	 * @see math.SpaceTimeTransformation#getTransformedTime(math.Vector3D, double)
	 */
	@Override
	public double getTransformedTime(Vector3D x, double t) {
		return t;
	}


	/* (non-Javadoc)
	 * @see math.SpaceTimeTransformation#getTransformedLightRayDirection(math.Vector3D)
	 */
	@Override
	public Vector3D getTransformedLightRayDirection(Vector3D d) {
		// d' = dHat + beta
		// return Vector3D.sum(d.getNormalised(), beta);
		
		// return getTransformedPosition(d, -d.getLength()/c);
		
		// transform "the other way round", such that d = d'Hat - beta
		double a = Vector3D.scalarProduct(d, d);
		double b = 2*Vector3D.scalarProduct(d, beta);
		double c = Vector3D.scalarProduct(beta, beta) - 1;
		double discriminant = b*b - 4*a*c;	// discriminant
		if(discriminant < 0)
		{
			// panic of some sort
			(new RayTraceException("Negative discriminant -- panic!")).printStackTrace();
			return null;
		}
		return Vector3D.sum(
				d.getProductWith((-b+Math.sqrt(discriminant))/(2*a)),
				beta
			);
	}

	
	// static methods

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
		// the second event is the ray passing through the position O+d at t=-|d|/c 
		return getTransformedPosition(d, -d.getLength()/c, beta);
	}
}
