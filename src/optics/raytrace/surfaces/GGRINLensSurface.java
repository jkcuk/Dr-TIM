package optics.raytrace.surfaces;

import optics.DoubleColour;
import optics.raytrace.core.LightSource;
import optics.raytrace.core.Ray;
import optics.raytrace.core.RaySceneObjectIntersection;
import optics.raytrace.core.RaytraceExceptionHandler;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.SurfacePropertyPrimitive;
import optics.raytrace.exceptions.EvanescentException;
import optics.raytrace.exceptions.RayTraceException;
import optics.raytrace.sceneObjects.Sphere;
import math.LorentzTransformation;
import math.Vector3D;

/**
 * SurfaceProperty that turns a sphere into a GGRIN lens [1].
 * [1] M. Sarbort and T. Tyc, "Spherical media and geodesic lenses in geometrical optics", Journal of Optics�14, 075705�(2012)
 * http://stacks.iop.org/2040-8986/14/i=7/a=075705
 * 
 * @see optics.raytrace.core.SceneObjectPrimitive#getNormalisedOutwardsSurfaceNormal(Vector3D p)
 * @author Johannes Courtial
 */
public class GGRINLensSurface extends SurfacePropertyPrimitive
{
	private static final long serialVersionUID = -6879248811275805436L;

	private double r1;	// distance from the centre of the lens of the point A
	private double r2;	// distance from the centre of the lens of the point A'
	private double alpha;	// angle by which A' is rotated with respect to A
	
	private double ratioNSurfaceNSurrounding;	// refractive-index ratio
	
	/**
	 * An Eaton lens can be combined with an invisible sphere to act as an Eaton lens for light rays that hit it
	 * with an impact parameter of greater than transparentTunnelRadius.
	 * Light rays with an impact parameter of less than transparentTunnelRadius pass straight through, and so there
	 * is a transparent tunnel at the centre of the lens.
	 * Note that what matters is the impact parameter *after refraction* (see ratioNSurfaceNSurrounding).
	 */
	private double transparentTunnelRadius;
	
	/**
	 * Creates a new Luneburg-lens surface.
	 * @param ratioNSurfaceNSurrounding	ratio of the refractive index at the edge of the lens and that outside the lens
	 * @param transmissionCoefficient
	 */
	public GGRINLensSurface(double r1, double r2, double alpha, double ratioNSurfaceNSurrounding, double transparentTunnelRadius, double transmissionCoefficient, boolean shadowThrowing)
	{
		super(transmissionCoefficient, shadowThrowing);
		setR1(r1);
		setR2(r2);
		setAlpha(alpha);
		setRatioNSurfaceNSurrounding(ratioNSurfaceNSurrounding);
		setTransparentTunnelRadius(transparentTunnelRadius);
	}
	
	/**
	 * Creates a new, perfect, Luneburg-lens surface.
	 */
	public GGRINLensSurface()
	{
		this(
				1.0,	// r1
				1.0,	// r2
				0,	// alpha
				1.0,	// refractive-index ratio
				0,	// transparent-tunnel radius
				1.0,	// transmission coefficient
				true	// shadow-throwing
			);
	}

	/**
	 * Clone the original Eaton-lens surface
	 * @param original
	 */
	public GGRINLensSurface(GGRINLensSurface original)
	{
		this(
				original.getR1(),
				original.getR2(),
				original.getAlpha(),
				original.getRatioNSurfaceNSurrounding(),
				original.getTransparentTunnelRadius(),
				original.getTransmissionCoefficient(),
				original.isShadowThrowing()
			);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public GGRINLensSurface clone()
	{
		return new GGRINLensSurface(this);
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.core.SurfaceProperty#getColour(optics.raytrace.core.Ray, optics.raytrace.core.RaySceneObjectIntersection, optics.raytrace.core.SceneObject, optics.raytrace.core.LightSource, int)
	 */
	@Override
	public DoubleColour getColour(Ray r, RaySceneObjectIntersection i, SceneObject scene, LightSource l, int traceLevel, RaytraceExceptionHandler raytraceExceptionHandler)
	throws RayTraceException
	{
		if (traceLevel <= 0) return DoubleColour.BLACK;
				
		// First check that the intersected object is a sphere
		if(!(i.o instanceof Sphere))
		{
			throw new RayTraceException("Object with GGRIN-lens surface is not of a sphere!");
		}
		
		Sphere s = (Sphere)(i.o);
		// double R = s.getRadius();
		
		// the vector from the intersection point to the centre of the sphere
		Vector3D R = Vector3D.difference(s.getCentre(), i.p);

		try
		{
			// first find the light-ray direction after refraction at the edge of the lens
			Vector3D dInside = RefractiveSimple.getRefractedLightRayDirection(r.getD(), s.getNormalisedOutwardsSurfaceNormal(i.p), 1/getRatioNSurfaceNSurrounding());

			// R's part that is perpendicular to the ray direction
			Vector3D RPerpendicular = R.getPartPerpendicularTo(dInside);
			
			// calculate the impact parameter
			double rImpact = RPerpendicular.getLength();
			
			Vector3D newStartingPoint, dPrimeInside;
			
			if(rImpact < getTransparentTunnelRadius())
			{
				// inside the transparent tunnel
				
				// get the part of R that is parallel to dInside
				Vector3D RParallel = R.getPartParallelTo(dInside);
				
				// calculate the new starting point
				newStartingPoint = Vector3D.sum(i.p, RParallel.getProductWith(2));
				
				// Calculate the new light-ray direction before refraction at the edge of the lens...
				dPrimeInside = dInside;
			}
			else
			{
				// the normalised direction d
				Vector3D d = dInside.getNormalised();

				// define position vectors relative to the sphere centre
				Vector3D p = Vector3D.difference(i.p, s.getCentre());

				// the component of p in the direction of d
				double pd = Vector3D.scalarProduct(d, p);

				// the part of p that is perpendicular to d
				Vector3D pPerpendicularPart = Vector3D.difference(p, d.getProductWith(pd));

				// the other normalised direction, e
				Vector3D e = pPerpendicularPart.getNormalised();

				// the part of p in the direction of e
				double pe = Vector3D.scalarProduct(e, p);

				// the d and e coordinates of the vector a = A - C
				double ae = pe;
				double ad = - Math.sqrt(r1*r1 - ae*ae);

				// the d and e coordinates of the vector q = Q - C
				double qe = pe;
				double qd = -pd;

				// the d and e coordinates of the vector i = I - C
				double ie = pe;
				double id = Math.sqrt(r2*r2 - ie*ie);

				// the angle beta by which everything needs to be rotated
				double beta = Math.atan2(ae, ad) - Math.atan2(ie, id) + alpha;
				double cosBeta = Math.cos(beta);
				double sinBeta = Math.sin(beta);

				// the rotated basis vectors
				Vector3D dRotated = Vector3D.sum(d.getProductWith(cosBeta), e.getProductWith(sinBeta));
				Vector3D eRotated = Vector3D.sum(d.getProductWith(-sinBeta), e.getProductWith(cosBeta));

				// calculate the new starting point
				newStartingPoint = Vector3D.sum(s.getCentre(), dRotated.getProductWith(qd), eRotated.getProductWith(qe));

				// Calculate the new light-ray direction before refraction at the edge of the lens...
				dPrimeInside = dRotated;
			}
			
			// ... and after refraction
			Vector3D newRayDirection = RefractiveSimple.getRefractedLightRayDirection(dPrimeInside, s.getNormalisedOutwardsSurfaceNormal(newStartingPoint), getRatioNSurfaceNSurrounding());

			// calculate the time the ray took to arrive there, assuming it has travelled in a straight line
			double dt = Vector3D.difference(newStartingPoint, i.p).getLength() / LorentzTransformation.c;
			
			// launch a new ray from here
			return scene.getColourAvoidingOrigin(
					r.getBranchRay(newStartingPoint, newRayDirection, i.t - dt),	// dt is subtracted as ray is traced backwards
					i.o,	// object to avoid
					l,
					scene,
					traceLevel-1,
					raytraceExceptionHandler
				).multiply(getTransmissionCoefficient());
		}
		catch (EvanescentException e)
		{
			// this is normal -- return the reflected ray
			// (Don't multiply by the transmission coefficient, as this is TIR!)
			return Reflective.getReflectedColour(r, i, scene, l, traceLevel, raytraceExceptionHandler);
		}
	}

	public double getR1() {
		return r1;
	}

	public void setR1(double r1) {
		this.r1 = r1;
	}

	public double getR2() {
		return r2;
	}

	public void setR2(double r2) {
		this.r2 = r2;
	}

	public double getAlpha() {
		return alpha;
	}

	public void setAlpha(double alpha) {
		this.alpha = alpha;
	}

	public double getRatioNSurfaceNSurrounding() {
		return ratioNSurfaceNSurrounding;
	}

	public void setRatioNSurfaceNSurrounding(double ratioNSurfaceNSurrounding) {
		this.ratioNSurfaceNSurrounding = ratioNSurfaceNSurrounding;
	}

	public void setTransparentTunnelRadius(double transparentTunnelRadius) {
		this.transparentTunnelRadius = transparentTunnelRadius;
	}

	public double getTransparentTunnelRadius() {
		return transparentTunnelRadius;
	}
}
