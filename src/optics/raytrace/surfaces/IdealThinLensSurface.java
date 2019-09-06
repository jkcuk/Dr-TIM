package optics.raytrace.surfaces;


import math.*;
import optics.DoubleColour;
import optics.raytrace.core.*;
import optics.raytrace.exceptions.InconsistencyException;
import optics.raytrace.exceptions.RayTraceException;
import optics.raytrace.imagingElements.ImagingElement;


/**
 * A surface property representing the an ideal thin lens.
 * 
 * Like @see optics.raytrace.surfaces.GlensSurface, this class is an inhomogeneous equivalent of the
 * @see optics.raytrace.imagingElements.HomogeneousPlanarImagingSurface class, in that it provides a
 * getImagePosition method.
 * 
 * If this surface property is <i>not<i> associated with such a plane, then the hologram bends the light
 * rays such that parallel light rays intersect in the focal plane, and any light rays through N pass through undeviated.
 * (Unlike in the case of a thin hologram, the direction change is independent of wavelength.)
 * The centre of the lens is given directly (and not in terms of the surface's surface-coordinate values,
 * like in the IdealThinLensSurfaceSurfaceCoordinates class).
 * 
 * @author Johannes Courtial
 */
public class IdealThinLensSurface extends SurfacePropertyPrimitive
implements ImagingElement
{
	private static final long serialVersionUID = 7401210819506650563L;

	// the principal point, P, which is also the principal point
	protected Vector3D principalPoint;
	
	// a unit vector in the direction of the optical axis, pointing in the direction of +ve space
	protected Vector3D opticalAxisDirectionPos;
	
	//	focalLength
	protected double
		focalLength;	// focal length
	
	/**
	 * Creates an instance of the surface property that refracts light like an ideal thin lens.
	 * 
	 * @param opticalAxisDirectionPos	the optical-axis direction
	 * @param principalPoint	the point P
	 * @param focalLength
	 * @param transmissionCoefficient
	 */
	public IdealThinLensSurface(
			Vector3D opticalAxisDirectionPos,
			Vector3D principalPoint,
			double focalLength,
			double transmissionCoefficient,
			boolean shadowThrowing
		) 
	{
		super(transmissionCoefficient, shadowThrowing);
		setParameters(principalPoint, opticalAxisDirectionPos, focalLength);
	}
	
	/**
	 * Constructor that creates an instance of a surface property representing an ideal thin lens that images Q- to Q+.
	 * @param pointOnLens	a point on the lens plane; not necessarily the principal point! 
	 * @param opticalAxisDirectionPos	normal to the lens plane, in the direction of the +ve optical axis
	 * @param QNeg
	 * @param QPos
	 * @param transmissionCoefficient
	 * @param shadowThrowing
	 * @throws InconsistencyException 
	 */
	public IdealThinLensSurface(
			Vector3D pointOnLens,
			Vector3D opticalAxisDirectionPos,
			Vector3D QNeg, Vector3D QPos,
			double transmissionCoefficient,
			boolean shadowThrowing
		)
	throws InconsistencyException
	{
		super(transmissionCoefficient, shadowThrowing);
		
//		System.out.println("IdealThinLensSurface::IdealThinLensSurface: "+
//				"(Q-,Q+)=("+QNeg+","+QPos+")"
//			);

		// normalise the optical axis direction, and set
		this.opticalAxisDirectionPos = opticalAxisDirectionPos.getNormalised();
		
		Vector3D qDiff = Vector3D.difference(QPos, QNeg);
		if(qDiff.getModSquared() != 0.0)
		{
			// the principal point is the position where the line between QNeg and QPos intersects the lens plane.
			this.principalPoint = Geometry.uniqueLinePlaneIntersection(
				QNeg, qDiff,	// a point on the line, and its direction
				pointOnLens, opticalAxisDirectionPos	// a point on the plane, and its normal
			);
		}
		else
		{
			// if QNeg == QPos, the lens doesn't do anything so it doesn't matter where on the lens we put the principal point
			principalPoint = pointOnLens;
		}
		
		// is there such an intersection?
		if(principalPoint == null)
		{
			// no, there isn't!
			throw new InconsistencyException("Principal point cannot be calculated.");
		}
		
		// calculate the object distance
		double o = -Vector3D.scalarProduct(Vector3D.difference(QNeg, principalPoint), this.opticalAxisDirectionPos);
		
		// calculate the image distance
		double i = Vector3D.scalarProduct(Vector3D.difference(QPos, principalPoint), this.opticalAxisDirectionPos);
		
		// calculate the focal length from the object and image distance
		// 1/o + 1/i = 1/f, so
		focalLength = 1/(1/o + 1/i);
//		
//		// the focal point is the point where the optical axis intersects the
//		// line from the orthographic projection of QNeg into the glens plane to QPos
//		
//		// orthographic projection into the lens plane of QNeg, i.e. intersection point with the lens of a light ray through Q- that's parallel to the optical axis
//		Vector3D QNegP = Geometry.orthographicProjection(QNeg, pointOnLens, opticalAxisDirectionPos);
//		
//		System.out.println("IdealThinLensSurface::IdealThinLensSurface: QNegP="+QNegP);
//
//		// +ve focal point
//		Vector3D FPos = Geometry.pointOfClosestApproach(
//				this.principalPoint, opticalAxisDirectionPos,	// point on optical axis and its direction
//				QNegP, Vector3D.difference(QPos, QNegP),	// point on line from QNegP to QPos and its direction
//				true	// give a warning if there is no intersection point
//		);
//		
//		System.out.println("IdealThinLensSurface::IdealThinLensSurface: FPos="+FPos);
//
//		// focal length
//		this.focalLength = Vector3D.scalarProduct(Vector3D.difference(FPos, principalPoint), this.opticalAxisDirectionPos);
		
//		System.out.println("IdealThinLensSurface::IdealThinLensSurface: focalLength="+focalLength);
//		
//		System.out.println("IdealThinLensSurface::IdealThinLensSurface: image of Q-="+getImagePosition(QNeg, ImagingDirection.NEG2POS));
//		System.out.println("IdealThinLensSurface::IdealThinLensSurface: (wrong) image of Q-="+getImagePosition(QNeg, ImagingDirection.POS2NEG));
//		System.out.println("IdealThinLensSurface::IdealThinLensSurface: image of Q+="+getImagePosition(QPos, ImagingDirection.POS2NEG));
	}

	/**
	 * Make a clone of the original IdealThinLensSurface surface property.
	 * @param original
	 */
	public IdealThinLensSurface(IdealThinLensSurface original)
	{
		this(
				original.getOpticalAxisDirectionPos(),
				original.getPrincipalPoint(),
				original.getFocalLength(),
				original.getTransmissionCoefficient(),
				original.isShadowThrowing()
			);		
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public IdealThinLensSurface clone()
	{
		return new IdealThinLensSurface(this);
	}
	
	// setters and getters
	
	/**
	 * Set all parameters of the IdealThinLensSurface surface property;
	 * the optical axis direction get normalised
	 * @param principalPoint
	 * @param opticalAxisDirectionPosPos
	 * @param focalLength
	 */
	public void setParameters(Vector3D principalPoint, Vector3D opticalAxisDirectionPos, double focalLength)
	{
//		System.out.println("IdealThinLensSurface::setParameters: principalPoint="+principalPoint+
//				", optical axis direction (+) = "+opticalAxisDirectionPos+
//				", f = "+focalLength);
		
		this.principalPoint = principalPoint;
		this.opticalAxisDirectionPos = opticalAxisDirectionPos.getNormalised();
		this.focalLength = focalLength;
	}
	
	public Vector3D getPrincipalPoint() {
		return principalPoint;
	}

	public void setPrincipalPoint(Vector3D principalPoint) {
		this.principalPoint = principalPoint;
	}

	public Vector3D getOpticalAxisDirectionPos() {
		return opticalAxisDirectionPos;
	}

	public double getFocalLength() {
		return focalLength;
	}

	

	//
	// the cardinal points
	//
	
	/**
	 * Returns the position of the nodal point, N.
	 * @return	a vector with the Cartesian coordinates of the nodal point
	 * 
	 */
	public Vector3D getNodalPoint()
	{
		return getPrincipalPoint();
	}
	
	/**
	 * @return	a vector with the Cartesian coordinates of the focal point in -ve space
	 */
	public Vector3D getFocalPointNeg()
	{
		return Vector3D.sum(getPrincipalPoint(), getOpticalAxisDirectionPos().getProductWith(-getFocalLength()));
	}

	/**
	 * @return	a vector with the Cartesian coordinates of the focal point in +ve space
	 */
	public Vector3D getFocalPointPos()
	{
		return Vector3D.sum(getPrincipalPoint(), getOpticalAxisDirectionPos().getProductWith(getFocalLength()));
	}
	
	

	
	//
	// implement SurfaceProperty method
	//
	
	/* (non-Javadoc)
	 * @see optics.raytrace.SurfaceProperty#getColour(optics.raytrace.Ray, optics.raytrace.RaySceneObjectIntersection, optics.raytrace.SceneObject, optics.raytrace.LightSource, int)
	 */
	@Override
	public DoubleColour getColour(Ray ray, RaySceneObjectIntersection i, SceneObject scene, LightSource l, int traceLevel, RaytraceExceptionHandler raytraceExceptionHandler)
	throws RayTraceException
	{
		if (traceLevel <= 0) return DoubleColour.BLACK;
		
		// calculate direction d' of deflected ray;
		// d' = d + (d_a / f_i) (N - S), where
		// 	d is the incident direction
		// 	d_a is the component of d in the a direction
		// 	f_i is the focal length in image space
		//	N is the principal point
		//	S is the point where the ray intersects the glens
		// see glens paper glens.pdf
		
		double focalLengthO, focalLengthI;	// focal lengths in object and image space
		
		// the component of d in the direction of aHat
		double dA = Vector3D.scalarProduct(ray.getD(), getOpticalAxisDirectionPos());
		if(dA > 0)
		{
			// the ray is incident from -ve space, so image space is +ve space
			focalLengthO = -getFocalLength();
			focalLengthI =  getFocalLength();
		}
		else
		{
			// the ray is incident from +ve space, so image space is -ve space
			focalLengthO =  getFocalLength();
			focalLengthI = -getFocalLength();
		}
		
		// calculate normalised new light-ray direction
		Vector3D newRayDirection = Vector3D.sum(
				ray.getD().getProductWith(-focalLengthO/focalLengthI),	// -(f_o / f_i) d + ...
				Vector3D.difference(
						getPrincipalPoint(),	// (N - ...
						i.p					// ... S) ...
					).getProductWith(dA/focalLengthI)	// ... * (d_a / f_i)
			).getNormalised();	// finally, normalise the whole lot
		
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
	
	
	public static Vector3D getImagePosition(double focalLength, Vector3D principalPoint, Vector3D opticalAxisDirectionPos, Vector3D objectPosition, ImagingDirection direction)
	{
		double objectSpaceFocalLength;
		
		// is the object position in +ve or -ve space?
		switch(direction)
		{
		case POS2NEG:
			// the object position is in +ve space
			// and the image position is in -ve space
			objectSpaceFocalLength = focalLength;
			break;
		case NEG2POS:
		default:
			// the object position is in -ve space
			// and the image position is in +ve space
			objectSpaceFocalLength = -focalLength;
			break;
		}
		
		// the axial component of the object position, measured from the principal point
		double a = Vector3D.scalarProduct(
				Vector3D.difference(objectPosition, principalPoint),	// Q - P
				opticalAxisDirectionPos	// aHat
			);
		
		return Vector3D.sum(
				objectPosition,
				Vector3D.difference(objectPosition, principalPoint).getProductWith(a/(objectSpaceFocalLength-a))
			);
	}

	
	/**
	 * @param objectPosition
	 * @param direction	ImagingDirection.POS2NEG (+ve space to -ve space) or ImagingDirection.NEG2POS (-ve space to +ve space)
	 * @return	the image position
	 */
	public Vector3D getImagePosition(Vector3D objectPosition, ImagingDirection direction)
	{
		return getImagePosition(getFocalLength(), getPrincipalPoint(), getOpticalAxisDirectionPos(), objectPosition, direction);
//		double objectSpaceFocalLength;
//		
//		// is the object position in +ve or -ve space?
//		switch(direction)
//		{
//		case POS2NEG:
//			// the object position is in +ve space
//			// and the image position is in -ve space
//			objectSpaceFocalLength = getFocalLength();
//			break;
//		case NEG2POS:
//		default:
//			// the object position is in -ve space
//			// and the image position is in +ve space
//			objectSpaceFocalLength = -getFocalLength();
//			break;
//		}
//		
//		// the axial component of the object position, measured from the principal point
//		double a = Vector3D.scalarProduct(
//				Vector3D.difference(objectPosition, getPrincipalPoint()),	// Q - P
//				getOpticalAxisDirectionPos()	// aHat
//			);
//		
//		return Vector3D.sum(
//				objectPosition,
//				Vector3D.difference(objectPosition, getPrincipalPoint()).getProductWith(a/(objectSpaceFocalLength-a))
//			);
	}
	
	/**
	 * The images of objects in the object-sided focal plane are, of course, at infinity.
	 * getImagePosition calculates this correctly, but sometimes it is better to have an image at very large, but
	 * finite, coordinates instead.
	 * @param objectPosition
	 * @param direction
	 * @return	the image that corresponds to the object position, if necessary shifted out of the focal plane
	 */
	public Vector3D getFiniteImagePosition(Vector3D objectPosition, ImagingDirection direction)
	{
		Vector3D
		imagePosition = getImagePosition(objectPosition, direction);

		// check if any of the coordinates are infinite
		if(imagePosition.isComponentInf())
		{
			// the image is at infinity; nudge the object position in the direction of the principal point
			imagePosition = getImagePosition(
					Vector3D.sum(objectPosition, Vector3D.difference(principalPoint, objectPosition).getWithLength(MyMath.TINY)),
					direction
					);
		}

		return imagePosition;
	}
	

	@Override
	public Vector3D getImagePosition(Vector3D objectPosition, Orientation orientation)
	{
		return getImagePosition(objectPosition, orientation.toImagingDirection());
	}
	
	/**
	 * @return	an equivalent glens surface
	 */
	public GlensSurface toGlensSurface()
	{
		return new GlensSurface(
				opticalAxisDirectionPos,
				principalPoint,	// principal point, same as principal point
				focalLength,	// F = mean focal length; same as focal length
				principalPoint.getProductWith(1/focalLength),	// principalPointF = N/F
				-1,	// focalLengthNegF = f-/F
				1,	// focalLengthPosF = f+/F
				getTransmissionCoefficient(),
				isShadowThrowing()
			);
	}
	
	@Override
	public String toString() {
		return "IdealThinLensSurface [principalPoint=" + principalPoint + ", opticalAxisDirectionPos="
				+ opticalAxisDirectionPos + ", focalLength=" + focalLength + "]";
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.imagingElements.ImagingElement#toSurfaceProperty(double, boolean)
	 */
	@Override
	public SurfaceProperty toSurfaceProperty(
			double imagingSurfaceTransmissionCoefficient,
			boolean imagingSurfaceShadowThrowing
		)
	{
		setTransmissionCoefficient(imagingSurfaceTransmissionCoefficient);
		setShadowThrowing(imagingSurfaceShadowThrowing);
		
		return this;
	}

}
