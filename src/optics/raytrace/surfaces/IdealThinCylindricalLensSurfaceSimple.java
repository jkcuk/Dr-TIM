package optics.raytrace.surfaces;


import math.*;
import optics.DoubleColour;
import optics.raytrace.core.*;
import optics.raytrace.exceptions.RayTraceException;


/**
 * A surface property representing the surface of an ideal thin cylindrical lens.
 * The behaviour of this surface property is that of a thin cylindrical lens, provided the surface
 * is flat, the optical-axis direction is perpendicular to the surface, and the lensCentre lies on the surface.
 * If it isn't, then the surface bends the light rays such that parallel light rays intersect
 * in the focal plane, and the light ray through the centre passes through undeviated.
 * (Unlike in the case of a thin hologram, the direction change is independent of wavelength.)
 * 
 * @author Johannes Courtial
 */
public class IdealThinCylindricalLensSurfaceSimple extends SurfacePropertyPrimitive
{
	private static final long serialVersionUID = 6144766166217860401L;

	// The centre of the ideal thin lens.
	// Light rays that pass through this point pass through the lens undeviated.
	//
	// Note that this point does not necessarily have to lie on the surface.
	// Normally it should, but if it doesn't then the behaviour is still well-defined.
	Vector3D lensCentre;
	
	// The direction of the optical axis.  (Sign doesn't matter.)
	Vector3D opticalAxisDirection;

	// The direction of the gradient (perpendicular to the focal line).  (Sign doesn't matter.)
	Vector3D gradientDirection;

	double
		focalLength;	// focal length
	
	/**
	 * Creates an instance of the surface property that refracts light like a thin lens
	 * 
	 * @param opticalAxisIntersectionCoordinates
	 * @param focalLength
	 * @param transmissionCoefficient
	 */
	public IdealThinCylindricalLensSurfaceSimple(
			Vector3D lensCentre,
			Vector3D opticalAxisDirection,
			Vector3D gradientDirection,
			double focalLength,
			double transmissionCoefficient,
			boolean shadowThrowing
		) 
	{
		super(transmissionCoefficient, shadowThrowing);
		setLensCentre(lensCentre);
		setOpticalAxisDirection(opticalAxisDirection);
		setGradientDirection(gradientDirection);
		setFocalLength(focalLength);
	}
		
	public IdealThinCylindricalLensSurfaceSimple(IdealThinCylindricalLensSurfaceSimple original)
	{
		this(
				original.getLensCentre(),
				original.getOpticalAxisDirection(),
				original.getGradientDirection(),
				original.getFocalLength(),
				original.getTransmissionCoefficient(),
				original.isShadowThrowing()
			);		
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public IdealThinCylindricalLensSurfaceSimple clone()
	{
		return new IdealThinCylindricalLensSurfaceSimple(this);
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

	public Vector3D getGradientDirection() {
		return gradientDirection;
	}

	public void setGradientDirection(Vector3D gradientDirection) {
		this.gradientDirection = gradientDirection.getPartPerpendicularTo(opticalAxisDirection).getNormalised();
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
		// see geometry.pdf
		
		// put together a coordinate system (origin = lens centre)
		// unit vector in the optical-axis direction
		Vector3D aHat = opticalAxisDirection;
		// unit vector in the gradient direction
		Vector3D gHat = gradientDirection;
		// unit vector in the focal-line direction
		Vector3D lHat = Vector3D.crossProduct(aHat, gHat);
		
		// the distance of the intersection point from the lens centre in the gradientDirection
		double iG = Vector3D.difference(i.p, lensCentre).getScalarProductWith(gHat);
		
		// components of the initial light-ray direction in the a, l and g directions
		double dA = ray.getD().getScalarProductWith(aHat);
		double dL = ray.getD().getScalarProductWith(lHat);
		double dG = ray.getD().getScalarProductWith(gHat);
		
		// calculate normalised new light-ray direction
		// (_L = component in focal-line direction; _G = component in gradient direction; _A = component in axis direction):
		// d' = (dL/dA, dG/dA - iG/f, 1)
		Vector3D newRayDirection = 
				Vector3D.sum(
						lHat.getProductWith(dL/dA),
						gHat.getProductWith(dG/dA-iG/focalLength),
						aHat
					);
		
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
