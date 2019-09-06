package optics.raytrace.surfaces;

import optics.DoubleColour;
import optics.raytrace.core.LightSource;
import optics.raytrace.core.Ray;
import optics.raytrace.core.RaySceneObjectIntersection;
import optics.raytrace.core.RaytraceExceptionHandler;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.SurfacePropertyPrimitive;
import optics.raytrace.exceptions.RayTraceException;
import math.Vector3D;

/**
 * Perform refraction like a phase hologram that images two given points into each other.
 * In the case of a planar surface, this is then the phase hologram of a lens.
 * Based a bit on PhaseHologramRefractive.
 * @see optics.raytrace.surfaces.PhaseHologram_old
 * @author Johannes Courtial
 */
public class Point2PointImaging_old extends SurfacePropertyPrimitive
{
	private static final long serialVersionUID = -6538620101451818694L;

	/**
	 * If reflecting = false, the phase hologram works in transmission.
	 * If reflecting = true, the phase hologram works in reflection.
	 */
	protected boolean reflecting = false;	// transmissive by default
	
	/**
	 * The two points that are being imaged into each other.
	 * Note that both can be virtual, i.e. the insideSpacePoint can lie on the outside etc.
	 */
	protected Vector3D
		insideSpacePoint, outsideSpacePoint;
	
	/**
	 * Standard constructor
	 */
	public Point2PointImaging_old(Vector3D insideSpacePoint, Vector3D outsideSpacePoint, double throughputCoefficient, boolean reflecting, boolean shadowThrowing)
	{
		super(throughputCoefficient, shadowThrowing);
		setInsideSpacePoint(insideSpacePoint);
		setOutsideSpacePoint(outsideSpacePoint);
		setReflecting(reflecting);
	}
	
	/**
	 * This constructor clones the original
	 * @param original
	 */
	public Point2PointImaging_old(Point2PointImaging_old original)
	{
		this(original.getInsideSpacePoint(), original.getOutsideSpacePoint(), original.getTransmissionCoefficient(), original.isReflecting(), original.isShadowThrowing());
	}
	
	@Override
	public Point2PointImaging_old clone()
	{
		return new Point2PointImaging_old(this);
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
		
		// the points that are imaged into one another, but which one is which depends on whether the ray is hitting
		// the surface from the inside or from the outside
		Vector3D object, image;
		
		// is the ray hitting the surface from the inside?
		if(Vector3D.scalarProduct(n, ray.getD()) > 0.)
		{
			// ray is hitting the surface from the inside
			// (normal component of ray is pointing in same direction as outwards surface normal)
			object = insideSpacePoint;
			image = outsideSpacePoint;
		}
		else
		{
			// ray is hitting the surface from the outside
			object = outsideSpacePoint;
			image = insideSpacePoint;
		}
		
		//
		// first find the in-plane component of the change in light-ray direction such that point1 is imaged into point2
		//
		
		// For a light ray that reaches P from the object position, the incident light-ray direction is
		Vector3D dModel = Vector3D.difference(p, object).getNormalised();
		
		// For a light ray that leaves P in the direction of the image position, the outgoing light-ray direction is
		Vector3D dPrimeModel = Vector3D.difference(image, p).getNormalised();
		
		// the change in the direction vector, ...
		Vector3D deltaD = Vector3D.difference(dPrimeModel, dModel);
		
		// ... and the component tangential to the plane, i.e. perpendicular to the normal
		Vector3D deltaDTangential = deltaD.getPartPerpendicularTo(n);
		
		//
		// add this tangential component to the incident light-ray direction;
		// this is what a phase hologram does
		// (well, it adds a tangential phase gradient, i.e. 2 pi/lambda times the ray-direction change
		//
		
		// the incident light-ray direction
		Vector3D d = ray.getD().getNormalised();
		
		// the component tangential to the plane
		Vector3D dTangential = d.getPartPerpendicularTo(n);
		
		// the sign of a direction is +1 if the component along n points in the same direction as n, otherwise it's -1
		double dDotNSign = Math.signum(Vector3D.scalarProduct(d, n));
		double dPrimeDotNSign = dDotNSign*(reflecting?-1:1);
		double dModelDotNSign = Math.signum(Vector3D.scalarProduct(dModel, n));
		double dPrimeModelDotNSign = Math.signum(Vector3D.scalarProduct(dPrimeModel, n));
		double dFactor = dDotNSign * dModelDotNSign;	// +1 if objectPosition is real, -1 if objectPosition is imaginary
		double dPrimeFactor = dPrimeDotNSign * dPrimeModelDotNSign;	// +/-1 if imagePosition is real/imaginary
		
		// add to this deltaDTangential to get the component tangential to the plane of the outgoing light-ray direction
		Vector3D dPrimeTangential = Vector3D.sum(dTangential.getProductWith(dFactor), deltaDTangential).getProductWith(dPrimeFactor);
		
		// length squared of the new tangential component
		double dPrimeTangentialLength2 = dPrimeTangential.getModSquared();
		
		// check if this tangential component of the outgoing light-ray direction corresponds to an evanescent ray
		if(dPrimeTangentialLength2 > 1)
		{
			// this is normal -- return the reflected ray
			// (Don't multiply by the transmission coefficient, as this is TIR!)
			return Reflective.getReflectedColour(ray, intersection, scene, lights, traceLevel, raytraceExceptionHandler);
		}
		
		// calculate the length of the normal component
		double dPrimeNormalLength =
			Math.sqrt(1 - dPrimeTangentialLength2)	// length of normal component
			* dPrimeDotNSign;
//			* (reflecting?-1:+1)	// change sign if reflecting
//			* Math.signum(Vector3D.scalarProduct(d, n)); // sign has to be that of the incident ray
		
		// finally, calculate the full outgoing light-ray direction
		Vector3D dPrime = Vector3D.sum(
				dPrimeTangential,
				n.getProductWith(dPrimeNormalLength)
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
	
	
	//
	// getters and setters
	//

	/**
	 * @return	the inside-space point the surface images into the corresponding point in the outside space
	 */
	public Vector3D getInsideSpacePoint() {
		return insideSpacePoint;
	}

	/**
	 * @param insideSpacePoint	the inside space point the surface images into the corresponding point in the outside space
	 */
	public void setInsideSpacePoint(Vector3D insideSpacePoint) {
		this.insideSpacePoint = insideSpacePoint;
	}

	/**
	 * @return	the outside-space point the surface images into the corresponding point in the inside space
	 */
	public Vector3D getOutsideSpacePoint() {
		return outsideSpacePoint;
	}

	/**
	 * @param outsideSpacePoint	the outside-space point the surface images into the corresponding point in the inside space
	 */
	public void setOutsideSpacePoint(Vector3D outsideSpacePoint) {
		this.outsideSpacePoint = outsideSpacePoint;
	}

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