package optics.raytrace.surfaces;

import optics.DoubleColour;
import optics.raytrace.core.LightSource;
import optics.raytrace.core.Orientation;
import optics.raytrace.core.Ray;
import optics.raytrace.core.RaySceneObjectIntersection;
import optics.raytrace.core.RaytraceExceptionHandler;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.SurfacePropertyPrimitive;
import optics.raytrace.exceptions.EvanescentException;
import optics.raytrace.exceptions.InconsistencyException;
import optics.raytrace.exceptions.RayTraceException;
import math.Vector3D;

/**
 * Perform refraction like a phase hologram.
 * A phase hologram changes the local phase gradient by a (local) constant and then adjusts the length of the normal component.
 * Here it's not the local phase gradient that is given, but the local change in the component tangential to the surface of the
 * normalised light-ray direction.
 * The direction change is the phase-gradient change divided by (2 pi/lambda).
 * @see optics.raytrace.surfaces.Point2PointImagingPhaseHologram
 * @see optics.raytrace.surfaces.PhaseHologram_old
 * @author Johannes Courtial
 */
public abstract class PhaseHologram extends SurfacePropertyPrimitive
{
	private static final long serialVersionUID = 4098640070851926470L;

	/**
	 * If reflective = false, the phase hologram works in transmission.
	 * If reflective = true, the phase hologram works in reflection.
	 */
	protected boolean reflective = false;	// transmissive by default
	
	// Implement the following method to define a specific phase hologram.

	/**
	 * Returns the local change in the tangential component of the normalised light-ray direction for a transmissive hologram.
	 * Note that this change is the same irrespective of the direction the light ray travels through the surface.
	 * The local change in the tangential light-ray direction should always lie in the plane tangential to the surface point
	 * (but this is not checked; does anything interesting happen if it doesn't lie in the tangent plane?).
	 * The direction gradient is the phase gradient, i.e. the change in the normalised direction vector,
	 * which is the phase-gradient change divided by (2 pi/lambda).
	 * 
	 * @param surfacePosition	the point on the surface
	 * @return	the local change in the tangential light-ray direction
	 */
	public abstract Vector3D getTangentialDirectionComponentChangeTransmissive(Vector3D surfacePosition, Vector3D surfaceNormal);

	/**
	 * Returns the local change in the tangential component of the normalised light-ray direction for a reflective hologram.
	 * Note that it depends whether or not the incident light ray travels inwards or outwards.
	 * The local change in the tangential light-ray direction should always lie in the plane tangential to the surface point
	 * (but this is not checked; does anything interesting happen if it doesn't lie in the tangent plane?).
	 * The direction gradient is the phase gradient, i.e. the change in the normalised direction vector,
	 * which is the phase-gradient change divided by (2 pi/lambda).
	 * 
	 * @param orientation	Orientation.OUTWARDS if incident light ray is travelling outwards, Orientation.INWARDS if inwards
	 * @param surfacePosition	the point on the surface
	 * @return	the local change in the tangential light-ray direction
	 */
	public abstract Vector3D getTangentialDirectionComponentChangeReflective(
			Orientation incidentLightRayOrientation,
			Vector3D surfacePosition,
			Vector3D surfaceNormal);

	/**
	 * Standard constructor
	 */
	public PhaseHologram(double throughputCoefficient, boolean reflective, boolean shadowThrowing)
	{
		super(throughputCoefficient, shadowThrowing);
		setReflective(reflective);
	}
	
	/**
	 * This constructor clones the original
	 * @param original
	 */
	public PhaseHologram(PhaseHologram original)
	{
		this(original.getTransmissionCoefficient(), original.isReflective(), original.isShadowThrowing());
	}
	
	@Override
	public abstract PhaseHologram clone();
	
	/**
	 * Calculate the normalised outgoing light-ray direction for the given incident light-ray direction,
	 * tangential-direction-component change (i.e. transverse phase gradient) and surface orientation.
	 * @param incidentNormalisedRayDirection
	 * @param tangentialDirectionComponentChange
	 * @param normalisedOutwardsSurfaceNormal
	 * @param isReflective
	 * @return outgoing normalised light-ray direction
	 * @throws EvanescentException
	 */
	public static Vector3D getOutgoingNormalisedRayDirection(
			Vector3D incidentNormalisedRayDirection,
			Vector3D tangentialDirectionComponentChange,
			Vector3D normalisedOutwardsSurfaceNormal,
			boolean isReflective
		)
	throws EvanescentException
	{
		// the component tangential to the plane
		Vector3D dTangential = incidentNormalisedRayDirection.getPartPerpendicularTo(normalisedOutwardsSurfaceNormal);
		
		// add to this deltaDTangential to get the component tangential to the plane of the outgoing light-ray direction
		Vector3D dPrimeTangential = Vector3D.sum(dTangential, tangentialDirectionComponentChange);
		
		// length squared of the new tangential component
		double dPrimeTangentialLength2 = dPrimeTangential.getModSquared();
		
		// check if this tangential component of the outgoing light-ray direction corresponds to an evanescent ray
		if(dPrimeTangentialLength2 > 1)
		{
			throw new EvanescentException("PhaseHologram::getOutgoingNormalisedRayDirection: Phase-hologram refraction gives evanescent ray.");
		}
			
		// calculate the component of the outgoing light-ray direction in the direction of the surface normal
		double dPrimeNormalComponent =
			// calculate the length, ...
			Math.sqrt(1 - dPrimeTangentialLength2)
			// ... and give it the same sign as the incident light-ray direction (i.e. keep going "inwards" or "outwards") ...
			* Math.signum(Vector3D.scalarProduct(incidentNormalisedRayDirection, normalisedOutwardsSurfaceNormal))
			// ... unless the surface is reflective, in which case reverse the sign
			* (isReflective?-1:1);

		// finally, calculate the full outgoing light-ray direction
		return Vector3D.sum(
				dPrimeTangential,
				normalisedOutwardsSurfaceNormal.getProductWith(dPrimeNormalComponent)
			);
	}

	/**
	 * Calculate the normalised outgoing light-ray direction for the given incident light-ray direction after
	 * interaction with a phase hologram.  Locally, the phase hologram introduces a phase gradient that turns
	 * the given incident model light-ray direction into the given outgoing model light-ray direction.
	 * @param incidentNormalisedRayDirection
	 * @param incidentNormalisedModelRayDirection
	 * @param outgoingNormalisedModelRayDirection
	 * @param outwardsSurfaceNormal
	 * @return outgoing normalised light-ray direction
	 * @throws InconsistencyException 
	 * @throws EvanescentException 
	 */
	public static Vector3D getOutgoingNormalisedRayDirection(
			Vector3D incidentNormalisedRayDirection,
			Vector3D incidentNormalisedModelRayDirection,
			Vector3D outgoingNormalisedModelRayDirection,
			Vector3D normalisedOutwardsSurfaceNormal,
			boolean reflective
		) throws InconsistencyException, EvanescentException
	{
		// first check for consistency
		Orientation
			incidentOrientation1 = Orientation.getOrientation(incidentNormalisedModelRayDirection, normalisedOutwardsSurfaceNormal),
			outgoingOrientation1 = Orientation.getOrientation(outgoingNormalisedModelRayDirection, normalisedOutwardsSurfaceNormal);
		if(
				// something is wrong if the surface is reflective but the incident and outgoing rays have the same orientation,
				// i.e. if they both travel through the surface "inwards" or "outwards"...
				(reflective && (incidentOrientation1 == outgoingOrientation1)) ||
				// ... or if the surface is not reflective but the incident and outgoing rays have opposite orientations
				(!reflective && (incidentOrientation1 != outgoingOrientation1))
			)
		{
			throw new InconsistencyException("PhaseHologram::getOutgoingNormalisedRayDirection: orientation of incident and outgoing model rays inconsistent with " + (reflective?"reflective":"non-reflective") + " surface.");
		}
		
		// calculate the change in the tangential component of the normalised light-ray direction
		Vector3D tangentialDirectionComponentChange = getTangentialDirectionComponentChange(
				incidentNormalisedModelRayDirection,
				outgoingNormalisedModelRayDirection,
				normalisedOutwardsSurfaceNormal
			);
		
		return getOutgoingNormalisedRayDirection(
				incidentNormalisedRayDirection,
				tangentialDirectionComponentChange,
				normalisedOutwardsSurfaceNormal,
				reflective
			);
	}
	
	/**
	 * Calculate the change in the tangential component of the light-ray direction that turns the given incident ray direction
	 * into the given outgoing ray direction.
	 * @param incidentNormalisedModelRayDirection
	 * @param outgoingNormalisedModelRayDirection
	 * @param outwardsSurfaceNormal
	 * @return the change in the tangential component of the light-ray direction
	 */
	public static Vector3D getTangentialDirectionComponentChange(
			Vector3D incidentNormalisedModelRayDirection,
			Vector3D outgoingNormalisedModelRayDirection,
			Vector3D outwardsSurfaceNormal
		)
	{
		// calculate the tangential component of (outgoing ray direction) - (incident ray direction)
		return Vector3D.difference(
				outgoingNormalisedModelRayDirection,
				incidentNormalisedModelRayDirection
			).getPartPerpendicularTo(outwardsSurfaceNormal);
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

		// get the object that this surface property is associated with
		// SceneObjectPrimitive surface = intersection.o;
		
		// get the point on the surface where they incident light ray intersects the surface
		Vector3D p = intersection.p;

		// get the surface normal at the intersection point; note that the surface normal always points outwards
		Vector3D n = intersection.getNormalisedOutwardsSurfaceNormal();
		
		//
		// first find the in-plane component of the change in the normalised light-ray direction
		//
		
		Vector3D dPrime;
		
		try
		{
			Vector3D tangentialDirectionComponentChange;
			Vector3D d = ray.getD().getNormalised();	// incident light-ray direction
			
			if(reflective)
			{
				tangentialDirectionComponentChange = getTangentialDirectionComponentChangeReflective(Orientation.getOrientation(d, n), p, n);
			}
			else
			{
				tangentialDirectionComponentChange = getTangentialDirectionComponentChangeTransmissive(p, n);
			}
			
			dPrime = getOutgoingNormalisedRayDirection(
					d,	// incidentNormalisedRayDirection,
					tangentialDirectionComponentChange,
					n,	// normalisedOutwardsSurfaceNormal
					reflective
				);

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
				ray.getBranchRay(p, dPrime, intersection.t),
				intersection.o,
				lights,
				scene,
				traceLevel-1,
				raytraceExceptionHandler
			).multiply(getTransmissionCoefficient());	// *cosRatio --- see above
		}
		catch(EvanescentException e)
		{
			// this is normal -- return the reflected ray
			// (Don't multiply by the transmission coefficient, as this is TIR!)
			return Reflective.getReflectedColour(ray, intersection, scene, lights, traceLevel, raytraceExceptionHandler);
		}
	}
	
	
	//
	// getters and setters
	//

	/**
	 * Is the phase hologram working in transmission or reflection?
	 * @return	true if working in reflection, false if working in transmission
	 */
	public boolean isReflective() {
		return reflective;
	}

	/**
	 * Set whether or not the phase hologram is working in transmission or reflection
	 * @param reflective	true if working in reflection, false if working in transmission
	 */
	public void setReflective(boolean reflective) {
		this.reflective = reflective;
	}
} 