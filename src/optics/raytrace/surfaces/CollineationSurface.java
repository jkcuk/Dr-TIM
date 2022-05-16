package optics.raytrace.surfaces;

import optics.DoubleColour;
import optics.raytrace.exceptions.RayTraceException;
import optics.raytrace.core.LightSource;
import optics.raytrace.core.Ray;
import optics.raytrace.core.RaySceneObjectIntersection;
import optics.raytrace.core.RaytraceExceptionHandler;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.SurfacePropertyPrimitive;
import math.Vector3D;

/**
 * @author Nikolajs Precenieks, Johannes Courtial
 * 
 * A surface property representing a surface that changes light-ray direction AND
 * position such that all object space is imaged into all image space, one-to-one
 * and onto, according to a collineation.
 */

public class CollineationSurface extends SurfacePropertyPrimitive
{
	private static final long serialVersionUID = 2821243674877473627L;

	protected double 
	a1, b1, c1, d1,
	a2, b2, c2, d2,
	a3, b3, c3, d3,
	a,  b,  c,  d;
	
	//Constructor
	public CollineationSurface(
			double a1, double b1, double c1, double d1, 
			double a2, double b2, double c2, double d2, 
			double a3, double b3, double c3, double d3,
			double a,  double b,  double c,  double d,  
			double transmissionCoefficient, boolean shadowThrowing)
	{
		super(transmissionCoefficient, shadowThrowing);
		setCollineationCoefficients(
				a1, b1, c1, d1,
				a2, b2, c2, d2,
				a3, b3, c3, d3,
				a,  b,  c,  d
			);
	}

	public CollineationSurface(CollineationSurface original)
	{
		super(original.getTransmissionCoefficient(), original.isShadowThrowing());
		setCollineationCoefficients(
				original.getA1(),
				original.getB1(),
				original.getC1(),
				original.getD1(),
				original.getA2(),
				original.getB2(),
				original.getC2(),
				original.getD2(),
				original.getA3(),
				original.getB3(),
				original.getC3(),
				original.getD3(),
				original.getA(),
				original.getB(),
				original.getC(),
				original.getD()
			);
	}
	
	@Override
	public CollineationSurface clone()
	{
		return new CollineationSurface(this);
	}

	// getters & setters
	
	public double getA1() {
		return a1;
	}

	public double getB1() {
		return b1;
	}

	public double getC1() {
		return c1;
	}

	public double getD1() {
		return d1;
	}

	public double getA2() {
		return a2;
	}

	public double getB2() {
		return b2;
	}

	public double getC2() {
		return c2;
	}

	public double getD2() {
		return d2;
	}

	public double getA3() {
		return a3;
	}

	public double getB3() {
		return b3;
	}

	public double getC3() {
		return c3;
	}

	public double getD3() {
		return d3;
	}

	public double getA() {
		return a;
	}

	public double getB() {
		return b;
	}

	public double getC() {
		return c;
	}

	public double getD() {
		return d;
	}

	public void setCollineationCoefficients(
			double a1, double b1, double c1, double d1, 
			double a2, double b2, double c2, double d2, 
			double a3, double b3, double c3, double d3,
			double a,  double b,  double c,  double d
		)
	{
		// check that one of a, b, c, d is <> 0?  Other requirements?
		this.a1 = a1;
		this.b1 = b1;
		this.c1 = c1;
		this.d1 = d1;
		this.a2 = a2;
		this.b2 = b2;
		this.c2 = c2;
		this.d2 = d2;
		this.a3 = a3;
		this.b3 = b3;
		this.c3 = c3;
		this.d3 = d3;
		this.a = a;
		this.b = b;
		this.c = c;
		this.d = d;
	}
	
	/**
	 * @param p
	 * @return	the collineation of the point p
	 */
	public Vector3D getCollineation(Vector3D p)
	{
		double denominator = a*p.x + b*p.y + c*p.z + d;
		
		return new Vector3D(
				(a1*p.x + b1*p.y + c1*p.z + d1)/denominator,
				(a2*p.x + b2*p.y + c2*p.z + d2)/denominator,
				(a3*p.x + b3*p.y + c3*p.z + d3)/denominator
			);
	}


	/* (non-Javadoc)
	 * @see optics.raytrace.surfaces.SurfaceProperty#getColour(optics.raytrace.core.Ray, optics.raytrace.core.RaySceneObjectIntersection, optics.raytrace.sceneObjects.SceneObject, optics.raytrace.lights.LightSource, int)
	 */
	@Override
	public DoubleColour getColour(Ray ray, RaySceneObjectIntersection intersection, SceneObject scene, LightSource lights, int traceLevel, RaytraceExceptionHandler raytraceExceptionHandler)
	throws RayTraceException
	{
		// Check traceLevel is greater than 0.
		if(traceLevel <= 0) return DoubleColour.BLACK;
	
		// Calculate, from the intersection point intersection.p and the light-ray
		// direction, ray.d, and also from the surface normal, 
		// intersection.o.getNormalisedOutwardsSurfaceNormal(intersection.p),
		// a newRayStartPoint that lies on the same surface and also on the ray on which
		// the images of two points on the incident ray lie, and the corresponding
		// newRayDirection.
		
		// calculate the image of two points on the incident ray
		// first point: the intersection point
		Vector3D p1Prime = getCollineation(intersection.p);
		
		// second point: the intersection point plus the light-ray-direction vector
		Vector3D p2Prime = getCollineation(Vector3D.sum(intersection.p, ray.getD()));
		
		// the outgoing light ray has to pass through these two points, and leave
		// from the same surface
		
		// construct a test ray that starts from p1Prime in the direction of p2Prime
		Ray testRay = new Ray(p1Prime, Vector3D.difference(p2Prime, p1Prime), intersection.t, false);
		
		// does this test ray intersect the surface?
		RaySceneObjectIntersection testIntersection = intersection.o.getClosestRayIntersection(testRay);
		
		if(testIntersection == RaySceneObjectIntersection.NO_INTERSECTION)
		{
			// see if there is an intersection in the other direction
			testIntersection = intersection.o.getClosestRayIntersection(testRay.getReversedRay());
			
			if(testIntersection == RaySceneObjectIntersection.NO_INTERSECTION)
			{
				// there is still no intersection with this surface; return black
				return DoubleColour.BLACK;
			}
		}
		
		// make sure that the outgoing light ray leaves the surface in the direction
		// of the opposite side from which the incoming light ray arrives, i.e.
		// make sure the sign of the scalar product of the incoming light-ray direction and the
		// local surface normal is that of the outgoing light-ray direction and the
		// local surface normal
		Vector3D outgoingLightRayDirection;
		
		if(
				Math.signum(
						Vector3D.scalarProduct(
								ray.getD(),	// ray direction
								intersection.getNormalisedOutwardsSurfaceNormal()	// surface normal at the intersection point with the incoming ray
							)
					) ==
				Math.signum(
						Vector3D.scalarProduct(
								testRay.getD(),	// direction of test ray
								testIntersection.getNormalisedOutwardsSurfaceNormal()	// surface normal at the point where the outgoing light ray leaves
							)
					)
			)
		{
			// both light rays are either inwards-bound or outwards bound;
			// the direction of the outgoing light ray is that of the test ray
			outgoingLightRayDirection = testRay.getD();
		}
		else
		{
			// one light ray is inwards-bound, the other is outwards bound;
			// the direction of the outgoing light ray is the reverse of that of the test ray
			outgoingLightRayDirection = testRay.getD().getReverse();
		}
		
		// launch a new ray from the intersection point between the surface and the test ray,
		// with the same ray direction as the test ray
		
		return scene.getColourAvoidingOrigin(
			ray.getBranchRay(testIntersection.p, outgoingLightRayDirection, intersection.t, ray.isReportToConsole()),
			intersection.o,
			lights,
			scene,
			traceLevel-1,
			raytraceExceptionHandler
		).multiply(getTransmissionCoefficient());	// *cosRatio --- see above
	}
}



