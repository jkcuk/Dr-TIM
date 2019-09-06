package optics.raytrace.surfaces;


import math.*;
import optics.DoubleColour;
import optics.raytrace.core.*;
import optics.raytrace.exceptions.RayTraceException;


/**
 * A surface property representing the surface of a thin lens.
 * The behaviour of this surface property is that of a thin lens, provided the surface
 * is flat.  If it isn't, the behaviour is undefined.
 * (Unlike in the case of a thin hologram, the direction change is independent of wavelength.)
 * The centre of the lens is defined in terms of the surface's surface-coordinate values.
 * 
 * @author Johannes Courtial, blair
 */
public class IdealThinLensSurfaceSurfaceCoordinates extends SurfacePropertyPrimitive
{
	private static final long serialVersionUID = 4071702831966138318L;

	// The surface coordinates of the point where the lens's optical axis intersects
	// the surface.
	// Note that the surface needs to be parametrised for this to work!
	Vector2D opticalAxisIntersectionCoordinates;
	double
		focalLength,
		transmissionCoefficient;			//this is the transmission coefficient
	
	/**
	 * Creates an instance of the surface property that refracts light like a thin lens
	 * 
	 * @param opticalAxisIntersectionCoordinates
	 * @param focalLength
	 * @param transmissionCoefficient
	 */
	public IdealThinLensSurfaceSurfaceCoordinates(
			Vector2D opticalAxisIntersectionCoordinates,
			double focalLength,
			double transmissionCoefficient,
			boolean shadowThrowing
		) 
	{
		super(transmissionCoefficient, shadowThrowing);
		setOpticalAxisIntersectionCoordinates(opticalAxisIntersectionCoordinates);
		setFocalLength(focalLength);
	}
	
	/**
	 * Create a lens with transmission coefficient 1 and its optical axis intersecting at
	 * surface coordinates (0, 0)
	 */
	public IdealThinLensSurfaceSurfaceCoordinates(double focalLength)
	{
		this(
				new Vector2D(0., 0.),	// intersection point between optical axis and surface, in surface coordinates
				focalLength,
				1.,	// transmission coefficient
				true	// shadow-throwing
			);
	}
	
	public IdealThinLensSurfaceSurfaceCoordinates(IdealThinLensSurfaceSurfaceCoordinates original)
	{
		this(
				original.getOpticalAxisIntersectionCoordinates(),
				original.getFocalLength(),
				original.getTransmissionCoefficient(),
				original.isShadowThrowing()
			);		
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public IdealThinLensSurfaceSurfaceCoordinates clone()
	{
		return new IdealThinLensSurfaceSurfaceCoordinates(this);
	}
	
	// setters and getters
	
	public Vector2D getOpticalAxisIntersectionCoordinates() {
		return opticalAxisIntersectionCoordinates;
	}

	public void setOpticalAxisIntersectionCoordinates(
			Vector2D opticalAxisIntersectionCoordinates)
	{
		this.opticalAxisIntersectionCoordinates = opticalAxisIntersectionCoordinates;
	}

	public double getFocalLength() {
		return focalLength;
	}

	public void setFocalLength(double focalLength) {
		this.focalLength = focalLength;
	}

	@Override
	public double getTransmissionCoefficient() {
		return transmissionCoefficient;
	}

	@Override
	public void setTransmissionCoefficient(double transmissionCoefficient) {
		this.transmissionCoefficient = transmissionCoefficient;
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
		
		// check that the object is parametrised
		if(!(i.o instanceof One2OneParametrisedObject))
		{
			throw new RayTraceException("IdealThinLensSurfaceSurfaceCoordinates::getColour: object not suitably parametrised!");
		}
		
		// object is suitably parametrised, calculate the point where the optical axis
		// intersects the surface
		Vector2D uv = getOpticalAxisIntersectionCoordinates();
		Vector3D
			O = ((One2OneParametrisedObject)(i.o)).getPointForSurfaceCoordinates(uv.x, uv.y),
			n = i.getNormalisedOutwardsSurfaceNormal();
		
		// scalar product of ray direction and normalised surface normal is what we call dz in thinLensAlgebra.pdf;
		// need absolute value of this in case the normalised surface normal points "the other way"
		double dz = Math.abs(ray.getD().getScalarProductWith(n));
		
		// now calculate the point Q in the image-sided focal plane through which
		// the ray has to pass
		Vector3D Q = Vector3D.sum(
				O,	// point where optical axis intersects surface
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
