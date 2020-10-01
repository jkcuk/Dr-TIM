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
 * SurfaceProperty that turns a sphere into a Luneburg lens.
 * 
 * @see optics.raytrace.core.SceneObjectPrimitive#getNormalisedOutwardsSurfaceNormal(Vector3D p)
 * @author Johannes Courtial
 */
public class LuneburgLensSurface extends SurfacePropertyPrimitive
{
	private static final long serialVersionUID = 1984340728605841182L;

	/**
	 * A Luneburg lens can be embedded into a medium with any refractive index, n.
	 * Its own refractive index reaches that value at its edge (which is nice, as it means there is no refraction there
	 * and there are no Fresnel reflections due to "impedance mismatch").
	 * The parameter ratioNSurfaceNSurrounding = n_edge / n_outside enables us to simulate a Luneburg lens that has been designed
	 * to be embedded in a material with refractive index n_edge but that is embedded in a material with refractive index
	 * n_outside.
	 * (If ratioNSurfaceNSurrounding = 1, the Luneburg lens should work as a Luneburg lens normally does.) 
	 */
	private double ratioNSurfaceNSurrounding;	// refractive-index ratio
	
	/**
	 * A Luneburg lens can be combined with an invisible sphere to act as a Luneburg lens for light rays that hit it
	 * with an impact parameter of greater than transparentTunnelRadius.
	 * Light rays with an impact parameter of less than transparentTunnelRadius pass straight through, and so there
	 * is a transparent tunnel at the centre of the lens.
	 * Note that what matters is the impact parameter *after refraction* (see ratioNSurfaceNSurrounding).
	 */
	private double transparentTunnelRadius;
		
	/**
	 * Creates a new Luneburg-lens surface.
	 * @param ratioNSurfaceNSurrounding	ratio of the refractive index at the edge of the lens and that outside the lens
	 * @param transparentTunnelRadius	radius of the transparent tunnel at the centre of the lens
	 * @param transmissionCoefficient
	 */
	public LuneburgLensSurface(double ratioNSurfaceNSurrounding, double transparentTunnelRadius, double transmissionCoefficient, boolean shadowThrowing)
	{
		super(transmissionCoefficient, shadowThrowing);
		this.ratioNSurfaceNSurrounding = ratioNSurfaceNSurrounding;
		this.transparentTunnelRadius = transparentTunnelRadius;
	}
	
	/**
	 * Creates a new, perfect, Luneburg-lens surface.
	 */
	public LuneburgLensSurface()
	{
		this(
				1.0,	// refractive-index ratio
				0.0,	// transparent tunnel radius
				1.0,	// transmission coefficient
				true	// shadow-throwing
			);
	}

	/**
	 * Clone the original Eaton-lens surface
	 * @param original
	 */
	public LuneburgLensSurface(LuneburgLensSurface original)
	{
		this(
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
	public LuneburgLensSurface clone()
	{
		return new LuneburgLensSurface(this);
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
			throw new RayTraceException("Object with Luneburg-lens surface is not of a sphere!");
		}
		
		Sphere sphere = (Sphere)(i.o);
		double radius = sphere.getRadius();
		
		try
		{
			// first find the light-ray direction after refraction at the edge of the lens
			Vector3D dInside = RefractiveSimple.getRefractedLightRayDirection(r.getD(), sphere.getNormalisedOutwardsSurfaceNormal(i.p), 1/getRatioNSurfaceNSurrounding());

			// the vector from the intersection point to the centre of the sphere...
			Vector3D R = Vector3D.difference(sphere.getCentre(), i.p);

			// calculate the impact parameter
			double rImpact = R.getPartPerpendicularTo(dInside).getLength();
			
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
				// the light-ray direction after refraction, scaled to length R (the radius of the lens)...
				Vector3D RDHat = dInside.getWithLength(radius);
				
				// calculate the new starting point
				newStartingPoint = Vector3D.sum(sphere.getCentre(), RDHat);
		
				// Calculate the new light-ray direction before refraction at the edge of the lens...
				dPrimeInside = Vector3D.difference(sphere.getCentre(), i.p);
			}
		
			// Calculate the new light-ray direction after refraction at the edge of the lens
			Vector3D newRayDirection = RefractiveSimple.getRefractedLightRayDirection(dPrimeInside, sphere.getNormalisedOutwardsSurfaceNormal(newStartingPoint), getRatioNSurfaceNSurrounding());

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

	public double getRatioNSurfaceNSurrounding() {
		return ratioNSurfaceNSurrounding;
	}

	public void setRatioNSurfaceNSurrounding(double ratioNSurfaceNSurrounding) {
		this.ratioNSurfaceNSurrounding = ratioNSurfaceNSurrounding;
	}

	public double getTransparentTunnelRadius() {
		return transparentTunnelRadius;
	}

	public void setTransparentTunnelRadius(double transparentTunnelRadius) {
		this.transparentTunnelRadius = transparentTunnelRadius;
	}
}
