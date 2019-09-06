package optics.raytrace.surfaces;

import java.util.ArrayList;

import optics.DoubleColour;
import optics.raytrace.core.LightSource;
import optics.raytrace.core.ParametrisedObject;
import optics.raytrace.core.Ray;
import optics.raytrace.core.RaySceneObjectIntersection;
import optics.raytrace.core.RaytraceExceptionHandler;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.SurfacePropertyPrimitive;
import optics.raytrace.exceptions.EvanescentException;
import optics.raytrace.exceptions.RayTraceException;
import optics.raytrace.utility.CoordinateSystems;
import math.Vector2D;
import math.Vector3D;

/**
 * Perform refraction like a phase hologram with a given phase gradient.
 * Not at all tested.
 * Basis of Point2PointImagingSurface.
 * @see optics.raytrace.surfaces.Point2PointImaging
 * @author Johannes Courtial
 */


/**
 * @author johannes
 *
 */
public class PhaseHologram_old extends SurfacePropertyPrimitive
{
	private static final long serialVersionUID = 9175566265243355290L;

	/**
	 * If reflecting = false, the phase hologram works in transmission.
	 * If reflecting = true, the phase hologram works in reflection.
	 */
	protected boolean reflecting = false;	// transmissive by default
	
	/**
	 * Override this method to define a specific phase hologram.
	 * Returns the local direction gradient in the plane tangential to the point.
	 * The direction gradient is the phase gradient, i.e. the change in the normalised direction vector,
	 * which is the phase-gradient change divided by (2 pi/lambda).
	 * 
	 * @param surfaceCoordinates	the coordinates (in the object's coordinate system) of the point
	 * @return	the local direction gradient in the plane tangential to the point
	 */
	public Vector2D getLocalDirectionGradient(Vector2D surfaceCoordinates)
	{
		return new Vector2D(0, 0);
	}
	
	/**
	 * Standard constructor
	 */
	public PhaseHologram_old(double throughputCoefficient, boolean reflecting, boolean shadowThrowing)
	{
		super(throughputCoefficient, shadowThrowing);
		this.reflecting = reflecting;
	}
	
	/**
	 * This constructor clones the original
	 * @param original
	 */
	public PhaseHologram_old(PhaseHologram_old original)
	{
		this(original.getTransmissionCoefficient(), original.isReflecting(), original.isShadowThrowing());
	}
	
	@Override
	public PhaseHologram_old clone()
	{
		return new PhaseHologram_old(this);
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
	
		// Get details of the surface

		// get the parent AnisotropicBiaxialSurface object that this surface property is associated with.
		ParametrisedObject surface = (ParametrisedObject)intersection.o;
		
		// get the point on the surface where they incident light ray intersects the surface
		Vector3D intersectionPoint = intersection.p;
		
		// get the normalised surface basis for the intersection point on the surface
		ArrayList<Vector3D>	normalisedSurfaceBasis = CoordinateSystems.getNormalisedSurfaceBasis(surface, intersectionPoint);
		
		// the incident light-ray direction, in the "global" basis...
		Vector3D d = ray.getD().getNormalised();
		
		// ... and in the normalised surface basis
		Vector3D dSurfaceBasis = d.toBasis(normalisedSurfaceBasis);

		// this will be the refracted light ray
		Vector3D dPrimeSurfaceBasis;
		
		// calculate the altered light-ray direction, first in the normalised surface basis
		try {
			dPrimeSurfaceBasis = refract(dSurfaceBasis, surface.getSurfaceCoordinates(intersectionPoint));
		}
		catch (EvanescentException e)
		{
			// this is normal -- return the reflected ray
			// (Don't multiply by the transmission coefficient, as this is TIR!)
			return Reflective.getReflectedColour(ray, intersection, scene, lights, traceLevel, raytraceExceptionHandler);
		}
		catch (RayTraceException e)
		{
			// some other exception; print out some useful information...
			e.printStackTrace();
			
			// ... and colour the point in question orange
			return DoubleColour.ORANGE;
		}

		// return Vector3D to scene basis
		Vector3D dPrime = dPrimeSurfaceBasis.fromBasis(normalisedSurfaceBasis);
		
		// calculate cos(angle of new ray with normal) / cos(angle of old ray with normal);
		// brightness changes by this factor
		// provided the ray directions are normalised, this is simply the modulus of the
		// ratio of the ray-direction components normal to the surface
		// double cosRatio = Math.abs(rayDirectionSurfaceBasisOut.z / rayDirectionSurfaceBasisIn.z);
		//
		// not sure the intensity scales --- see http://www.astronomy.net/articles/29/
		// Also, one of the article's reviewers wrote this:
		// This is also related to the brightening in Fig. 7. In fact, I think that such a brightening should not occur.
		// It is known that brightness of an object does not change if the object is observed by some non-absorbing optical
		// instrument. For example, a sun reflected in a curved metallic surface is equally bright as if it is viewed directly.
		// I expect the same for teleported image. Maybe if the effect of the additional factor in eq. (5) is taken into
		// account together with the other method of calculation of the ray direction, no brightening will occur.

		
		// launch a new ray from here
		
		return scene.getColourAvoidingOrigin(
			ray.getBranchRay(intersectionPoint, dPrime, intersection.t),
			intersection.o,
			lights,
			scene,
			traceLevel-1,
			raytraceExceptionHandler
		).multiply(getTransmissionCoefficient());	// *cosRatio --- see above
	}

	/**
	 * From the incident light-ray direction, in the surface's normalised coordinate system, calculate the outgoing light-ray
	 * direction, again in the surface's normalised coordinate system.
	 * The surface's normalised coordinate system consists of the two directions returned by the surface (both normalised),
	 * and the (also normalised) surface normal.
	 * 
	 * @param incidentRayDirection	the incident light-ray direction, in the surface's normalised coordinate system
	 * @param surfacePointCoordinates	the point on the surface, in the surface's coordinate system
	 * @return	the outgoing light-ray direction, in the surface's normalised coordinate system
	 * @throws RayTraceException
	 */
	public Vector3D refract(Vector3D incidentRayDirection, Vector2D surfacePointCoordinates) throws RayTraceException
	{
		// just in case the incident light-ray direction in the surface's normalised coordinate system isn't normalised,
		// which can happen when the basis vectors don't form an orthonormal system
		Vector3D incidentRayDirectionNormalised = incidentRayDirection.getNormalised();
		
		// collect the incident ray's components in the plane tangential to the surface at the surface point
		Vector2D incidentRayTangentialComponents = new Vector2D(incidentRayDirectionNormalised.x, incidentRayDirectionNormalised.y);
		
		// the actual phase-hologram bit
		Vector2D outgoingRayTangentialComponents = Vector2D.sum(
				incidentRayTangentialComponents,
				getLocalDirectionGradient(surfacePointCoordinates)
			);
		
		/**
		 * Transform complex number back into the Vector3D it represents in the basis of the surface
		 *
		 * Start by calculating z coordinate from complex term. Assuming normalisation,
		 * z = sqrt(1-[Re(c')^2 + Im(c')^2])
		 *
		 */
	
		// Introduce variable modSquared for error testing/z Vector3D calculation.
		double modSquared = outgoingRayTangentialComponents.getModSquared();
								
		// check for evanescent ray
		if( modSquared > 1 ) throw new EvanescentException("PhaseHologramRefraction::refract: refracted ray is evanescent");

		// Otherwise return normalised Vector3D. Also maintain direction of z component.
		return(new Vector3D(
				outgoingRayTangentialComponents.x,
				outgoingRayTangentialComponents.y,
				(reflecting?-1:+1) *	// change sign if reflecting
				Math.signum(incidentRayDirection.z) *	// sign has to be that of the incident ray
				Math.sqrt(1-modSquared) // length of the z component of the outgoing ray
			));
	}
	
	//
	// getters and setters
	//

	/**
	 * Is the phase hologram working in transmission or reflection?
	 * @return	true if working in reflection, false if working in transmission
	 */
	public boolean isReflecting() {
		return reflecting;
	}

	/**
	 * Set whether or not the phase hologram is working in transmission or reflection
	 * @param reflecting	true if working in reflection, false if working in transmission
	 */
	public void setReflecting(boolean reflecting) {
		this.reflecting = reflecting;
	}
} 