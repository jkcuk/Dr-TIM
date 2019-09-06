package optics.raytrace.surfaces;


import math.*;
import optics.DoubleColour;
import optics.raytrace.core.*;
import optics.raytrace.exceptions.RayTraceException;


/**
 * A surface property representing the surface of an ideal thin lens.
 * The behaviour of this surface property is that of a thin lens, provided the surface
 * is flat, the optical-axis direction is perpendicular to the surface, and the lensCentre lies on the surface.
 * If it isn't, then the surface bends the light rays such that parallel light rays intersect
 * in the focal plane, and the light ray through the centre passes through undeviated.
 * (Unlike in the case of a thin hologram, the direction change is independent of wavelength.)
 * The centre of the lens is given directly (and not in terms of the surface's surface-coordinate values,
 * like in the ThinLensHologramSurfaceCoordinates class).
 * 
 * @author Johannes Courtial
 */
public class IdealThinLensSurfaceSimple extends SurfacePropertyPrimitive
{
	private static final long serialVersionUID = -6713839984788002927L;

	// The centre of the ideal thin lens.
	// Light rays that pass through this point pass through the lens undeviated.
	//
	// Note that this point does not necessarily have to lie on the surface.
	// Normally it should, but if it doesn't then the behaviour is still well-defined.
	Vector3D lensCentre;
	
	// The direction of the optical axis.  (Sign doesn't matter.)
	Vector3D opticalAxisDirection;
	
	double
		focalLength;	// focal length
	
	/**
	 * Creates an instance of the surface property that refracts light like a thin lens
	 * 
	 * @param opticalAxisIntersectionCoordinates
	 * @param focalLength
	 * @param transmissionCoefficient
	 */
	public IdealThinLensSurfaceSimple(
			Vector3D lensCentre,
			Vector3D opticalAxisDirection,
			double focalLength,
			double transmissionCoefficient,
			boolean shadowThrowing
		) 
	{
		super(transmissionCoefficient, shadowThrowing);
		setLensCentre(lensCentre);
		setOpticalAxisDirection(opticalAxisDirection);
		setFocalLength(focalLength);
	}
		
	public IdealThinLensSurfaceSimple(IdealThinLensSurfaceSimple original)
	{
		this(
				original.getLensCentre(),
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
	public IdealThinLensSurfaceSimple clone()
	{
		return new IdealThinLensSurfaceSimple(this);
	}
	
	// setters and getters
	
	public Vector3D getLensCentre() {
		return lensCentre;
	}

	public void setLensCentre(Vector3D lensCentre) {
		this.lensCentre = lensCentre;
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
		
		// scalar product of ray direction and normalised vector in direction of optical axis is what we call dz in thinLensAlgebra.pdf;
		// need absolute value of this in case the normalised surface normal points "the other way"
		double dz = Math.abs(ray.getD().getScalarProductWith(getOpticalAxisDirection()));
		
		// now calculate the point Q in the image-sided focal plane through which
		// the ray has to pass
		Vector3D Q = Vector3D.sum(
				getLensCentre(),	// point where optical axis intersects surface
				ray.getD().getProductWith(getFocalLength()/dz)	// d*f/dz
			);

		// calculate normalised new light-ray direction
		Vector3D newRayDirection = Vector3D.difference(Q, i.p).getNormalised().getProductWith(Math.signum(getFocalLength()));
		
		// launch a new ray from here
		return scene.getColourAvoidingOrigin(
			ray.getBranchRay(i.p, newRayDirection, i.t),
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
