package optics.raytrace.surfaces;


import math.*;
import optics.DoubleColour;
import optics.raytrace.core.*;
import optics.raytrace.exceptions.RayTraceException;


/**
 * A surface property representing the surface of an ideal thin spherical mirror, i.e. an ideal thin lens in reflection.
 * @see optics.raytrace.surfaces.IdealThinLensSurfaceSimple
 * (Unlike in the case of a thin hologram, the direction change is independent of wavelength.)
 * 
 * @author Johannes Courtial
 */
public class IdealThinSphericalMirrorSurfaceSimple extends SurfacePropertyPrimitive
{
	private static final long serialVersionUID = 6465911660818716890L;

	// 
	/**
	 * principal point
	 * Note that this point does not necessarily have to lie on the surface.
	 * Normally it should, but if it doesn't then the behaviour is still well-defined.
	 */
	Vector3D principalPoint;
	
	/**
	 * a vector parallel to the optical axis (sign doesn't matter)
	 */
	Vector3D opticalAxisDirection;
	
	double
		focalLength;	// focal length
	
	/**
	 * Creates an instance of the surface property that reflects light like an ideal thin imaging mirror
	 * 
	 * @param principalPoint
	 * @param opticalAxisDirection
	 * @param focalLength
	 * @param reflectionCoefficient
	 * @param shadowThrowing
	 */
	public IdealThinSphericalMirrorSurfaceSimple(
			Vector3D principalPoint,
			Vector3D opticalAxisDirection,
			double focalLength,
			double reflectionCoefficient,
			boolean shadowThrowing
		) 
	{
		super(reflectionCoefficient, shadowThrowing);
		setPrincipalPoint(principalPoint);
		setOpticalAxisDirection(opticalAxisDirection);
		setFocalLength(focalLength);
	}
		
	public IdealThinSphericalMirrorSurfaceSimple(IdealThinSphericalMirrorSurfaceSimple original)
	{
		this(
				original.getPrincipalPoint(),
				original.getOpticalAxisDirection(),
				original.getFocalLength(),
				original.getTransmissionCoefficient(),
				original.isShadowThrowing()
			);		
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public IdealThinSphericalMirrorSurfaceSimple clone()
	{
		return new IdealThinSphericalMirrorSurfaceSimple(this);
	}
	
	// setters and getters
	
	public Vector3D getPrincipalPoint() {
		return principalPoint;
	}

	public void setPrincipalPoint(Vector3D lensCentre) {
		this.principalPoint = lensCentre;
	}

	public Vector3D getOpticalAxisDirection() {
		return opticalAxisDirection;
	}

	/**
	 * Normalise and set optical-axis direction.
	 * @param opticalAxisDirection
	 */
	public void setOpticalAxisDirection(Vector3D opticalAxisDirection) {
		this.opticalAxisDirection = opticalAxisDirection.getNormalised();
	}

	public double getFocalLength() {
		return focalLength;
	}

	public void setFocalLength(double focalLength) {
		this.focalLength = focalLength;
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.SurfaceProperty#getColour(optics.raytrace.Ray, optics.raytrace.RaySceneObjectIntersection, optics.raytrace.SceneObject, optics.raytrace.LightSource, int)
	 */
	@Override
	public DoubleColour getColour(Ray ray, RaySceneObjectIntersection i, SceneObject scene, LightSource l, int traceLevel, RaytraceExceptionHandler raytraceExceptionHandler)
	throws RayTraceException
	{
		if (traceLevel <= 0) return DoubleColour.BLACK;
		
		// calculate direction of deflected ray;
		// see thinLensAlgebra.pdf
		// @see optics.raytrace.surfaces.IdealThinLensSurfaceSimple#getColour
		
		// scalar product of ray direction and normalised vector in direction of optical axis is what we call dz in thinLensAlgebra.pdf;
		// need absolute value of this in case the normalised surface normal points "the other way"
		double dz = Math.abs(ray.getD().getScalarProductWith(getOpticalAxisDirection()));
		
		// now calculate the point Q in the image-sided focal plane through which
		// the ray has to pass
		Vector3D Q = Vector3D.sum(
				getPrincipalPoint(),	// point where optical axis intersects surface
				ray.getD().getProductWith(getFocalLength()/dz)	// d*f/dz
			);

		// calculate normalised new light-ray direction before mirroring at the principal plane...
		Vector3D newRayDirection = Vector3D.difference(Q, i.p).getNormalised().getProductWith(Math.signum(getFocalLength()));
		
		// ... and after mirroring
		newRayDirection = Vector3D.difference(
				newRayDirection,
				newRayDirection.getProjectionOnto(getOpticalAxisDirection()).getProductWith(2)
			);
		
		// launch a new ray from here
		return scene.getColourAvoidingOrigin(
			ray.getBranchRay(i.p, newRayDirection, i.t, ray.isReportToConsole()),
			i.o,
			l,
			scene,
			traceLevel-1,
			raytraceExceptionHandler
		).multiply(
			getTransmissionCoefficient()
			// * Math.abs(newRayDirection.getScalarProductWith(n))/dz // cos(angle of new ray with normal) / cos(angle of old ray with normal)
			//
			// not sure the intensity scales --- see http://www.astronomy.net/articles/29/
			// Also, one of the article's reviewers wrote this:
			// This is also related to the brightening in Fig. 7. In fact, I think that such a brightening should not occur.
			// It is known that brightness of an object does not change if the object is observed by some non-absorbing optical
			// instrument. For example, a sun reflected in a curved metallic surface is equally bright as if it is viewed directly.
			// I expect the same for teleported image. Maybe if the effect of the additional factor in eq. (5) is taken into
			// account together with the other method of calculation of the ray direction, no brightening will occur.
		);
	}
}
