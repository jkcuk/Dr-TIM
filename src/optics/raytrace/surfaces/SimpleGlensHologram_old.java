package optics.raytrace.surfaces;


import math.*;
import optics.DoubleColour;
import optics.raytrace.core.*;
import optics.raytrace.exceptions.RayTraceException;


/**
 * No longer used, now SimpleGlensHologram
 * 
 * A surface property representing a glens hologram.
 * 
 * A glens is a generalised lens, with two different focal lengths [1].
 * It is the most general inhomogeneous planar surface that images every point in object space to a corresponding
 * point in image space, without offsetting the ray position upon transmission.
 * Such surfaces can be approximately realised in the form of planar gCLAs [2,3].
 * 
 * A glens has a +ve and a -ve side;
 * +ve space refers to light rays travelling on the +ve side, -ve space to those on the -ve side.
 * The direction of the optical axis is given by
 * 	opticalAxisDirectionPos: a unit vector in the direction of the optical axis, pointing in the direction of +ve space
 * The cardinal points are as follows:
 * 	principalPoint: the principal point, where the optical axis intersects the plane of the glens
 * 	nodalPoint: the nodal point (any light rays incident on N pass through undeviated; in a lens, N=P)
 * 	focalPointPos (or F+): the focal point in +ve space
 * 	focalPointNeg (or F-): the focal point in -ve space
 * 
 * The parameters of this class (position of nodal point, optical-axis direction, focal lengths)
 * completely define the position of the glens surface: it has to be a plane through the principal point,
 * and the outwards-facing normal has to point in the +ve optical-axis direction.
 * Provided this surface property is associated with such a plane, its behaviour is that of a glens.
 * 
 * This class is also the inhomogeneous equivalent of the
 * @see optics.raytrace.imagingElements.HomogeneousPlanarImagingSurface class, in that it provides a
 * getImagePosition method.
 * 
 * If this surface property is <i>not<i> associated with such a plane, then the hologram bends the light
 * rays such that parallel light rays intersect in the focal plane, and any light rays through N pass through undeviated.
 * (Unlike in the case of a thin hologram, the direction change is independent of wavelength.)
 * The centre of the lens is given directly (and not in terms of the surface's surface-coordinate values,
 * like in the ThinLensHologramSurfaceCoordinates class).
 * 
 * [1] J. Courtial, "Geometric limits to geometric optical imaging with infinite, planar, non-absorbing sheets", Opt. Commun. <b>282</b>, 2480-2483 (2009)
 * [2] A. C. Hamilton and J. Courtial, "Generalized refraction using lenslet arrays", J. Opt. A: Pure Appl. Opt. <b>11</b>, 065502 (2009)
 * [3] S. Oxburgh, C. D. White, G. Antoniou, and J. Courtial, "Law of refraction for generalised confocal lenslet arrays", Opt. Commun. <b>313</b>, 119-122 (2014)

 * 
 * @author Johannes Courtial
 */
public class SimpleGlensHologram_old extends SurfacePropertyPrimitive
{
	private static final long serialVersionUID = 134597862435L;

	// the nodal point, N (any light rays incident on N pass through undeviated; in a lens, N=P)
	protected Vector3D nodalPoint;
	
	// a unit vector in the direction of the optical axis, pointing in the direction of +ve space
	protected Vector3D opticalAxisDirectionPos;
	
	//	focalLengthPos (normally f+): the axial coordinate of the +ve-space focal point
	//	focalLengthNeg (normally f-): the axial coordinate of the +ve-space focal point
	protected double
		focalLengthNeg, focalLengthPos;	// focal lengths
	
	/**
	 * Creates an instance of the surface property that refracts light like a glens.
	 * 
	 * @param opticalAxisDirectionPos	the vector aHat
	 * @param nodalPoint	the point N
	 * @param focalLengthNeg
	 * @param focalLengthPos
	 * @param transmissionCoefficient
	 */
	public SimpleGlensHologram_old(
			Vector3D opticalAxisDirectionPos,
			Vector3D nodalPoint,
			double focalLengthNeg,
			double focalLengthPos,
			double transmissionCoefficient,
			boolean shadowThrowing
		) 
	{
		super(transmissionCoefficient, shadowThrowing);
		setParameters(opticalAxisDirectionPos, nodalPoint, focalLengthNeg, focalLengthPos);
	}
	
	/**
	 * Create what is effectively a transparent surface.
	 * Set its parameters later using @see setParameters.
	 * @param transmissionCoefficient
	 * @param shadowThrowing
	 */
	public SimpleGlensHologram_old(
			double transmissionCoefficient,
			boolean shadowThrowing
		)
	{
		super(transmissionCoefficient, shadowThrowing);
		setParameters(
				new Vector3D(0, 0, 1),	// opticalAxisDirectionPos
				new Vector3D(0, 0, 0),	//nodalPoint
				-MyMath.HUGE,	// focalLengthNeg; make huge to make this effectively a transparent window
				MyMath.HUGE	// focalLengthPos; make huge to make this effectively a transparent window
			);
	}

	
	/**
	 * Constructor that creates an instance of a GlensHologram that images Q- to Q+ and R- to R+.
	 * If this is not possible, then it finds approximate parameters.
	 * The nodal point, N, is set to be the point closest to the lines from Q- to Q+ and from R- to R+
	 * (if they intersect, then this is the intersection point).
	 * The focal lengths are set such the focal points are those points on the optical axis (which passes through
	 * N with opticalAxisDirectionPos) where it comes closest to the line between the orthographic projection of
	 * Q- into the glens plane and Q+, and where it comes closest to the line between the orthographic projection
	 * of Q+ into the glens plane and Q-.
	 * @param pointOnGlens	a point on the glens plane; not necessarily the principal point! 
	 * @param opticalAxisDirectionPos	normal to the glens plane, in the direction of the +ve optical axis
	 * @param QNeg
	 * @param QPos
	 * @param RNeg
	 * @param RPos
	 * @param transmissionCoefficient
	 * @param shadowThrowing
	 * @throws RayTraceException 
	 */
	public SimpleGlensHologram_old(
			Vector3D pointOnGlens,
			Vector3D opticalAxisDirectionPos,
			Vector3D QNeg, Vector3D QPos,
			Vector3D RNeg, Vector3D RPos,
			double transmissionCoefficient,
			boolean shadowThrowing
		) throws RayTraceException
	{
		super(transmissionCoefficient, shadowThrowing);
		
		calculateAndSetParameters(pointOnGlens, opticalAxisDirectionPos, QNeg, QPos, RNeg, RPos);
	}
		
	/**
	 * Constructor that creates an instance of a GlensHologram that has nodal point N and that images Q- to Q+.
	 * If this is not possible, then it finds approximate parameters.
	 * The focal lengths are set such the focal points are those points on the optical axis (which passes through
	 * N with opticalAxisDirectionPos) where it comes closest to the line between the orthographic projection of
	 * Q- into the glens plane and Q+, and where it comes closest to the line between the orthographic projection
	 * of Q+ into the glens plane and Q-.
	 * @param pointOnGlens	a point on the glens plane; not necessarily the principal point! 
	 * @param opticalAxisDirectionPos	normal to the glens plane, in the direction of the +ve optical axis
	 * @param nodalPoint
	 * @param QNeg
	 * @param QPos
	 * @param transmissionCoefficient
	 * @param shadowThrowing
	 */
	public SimpleGlensHologram_old(
			Vector3D pointOnGlens,
			Vector3D opticalAxisDirectionPos,
			Vector3D nodalPoint,
			Vector3D QNeg, Vector3D QPos,
			double transmissionCoefficient,
			boolean shadowThrowing
		)
	{
		super(transmissionCoefficient, shadowThrowing);
		
		calculateAndSetParameters(pointOnGlens, opticalAxisDirectionPos, nodalPoint, QNeg, QPos);
	}

	/**
	 * Make a clone of the original GlensHologram.
	 * @param original
	 */
	public SimpleGlensHologram_old(SimpleGlensHologram_old original)
	{
		this(
				original.getOpticalAxisDirectionPos(),
				original.getNodalPoint(),
				original.getFocalLengthNeg(),
				original.getFocalLengthPos(),
				original.getTransmissionCoefficient(),
				original.isShadowThrowing()
			);		
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public SimpleGlensHologram_old clone()
	{
		return new SimpleGlensHologram_old(this);
	}
	
	// setters and getters
	
	/**
	 * Set all parameters of the GlensHologram;
	 * the optical axis direction get normalised
	 * @param opticalAxisDirectionPos
	 * @param nodalPoint
	 * @param focalLengthNeg
	 * @param focalLengthPos
	 */
	public void setParameters(Vector3D opticalAxisDirectionPos, Vector3D nodalPoint, double focalLengthNeg, double focalLengthPos)
	{
//		System.out.println("GlensHologram::setParameters: nodalPoint="+nodalPoint+
//				", optical axis direction (+) = "+opticalAxisDirectionPos+
//				", f- = "+focalLengthNeg+
//				", f+ = "+focalLengthPos);
		
		this.nodalPoint = nodalPoint;
		this.opticalAxisDirectionPos = opticalAxisDirectionPos.getNormalised();
		this.focalLengthNeg = focalLengthNeg;
		this.focalLengthPos = focalLengthPos;
	}
	
	public Vector3D getNodalPoint() {
		return nodalPoint;
	}

	public void setNodalPoint(Vector3D nodalPoint) {
		this.nodalPoint = nodalPoint;
	}

	public Vector3D getOpticalAxisDirectionPos() {
		return opticalAxisDirectionPos;
	}

	public double getFocalLengthNeg() {
		return focalLengthNeg;
	}

	public double getFocalLengthPos() {
		return focalLengthPos;
	}
	
	//
	// other cardinal distances
	//
	
	public double getNodalDistance()
	{
		return focalLengthNeg + focalLengthPos;
	}

	//
	// the cardinal points
	//
	
	/**
	 * Returns the position of the principal point, P, i.e. the position where the optical axis intersects the
	 * glens plane.
	 * @return	a vector with the Cartesian coordinates of the principal point
	 * 
	 */
	public Vector3D getPrincipalPoint()
	{
		// N = P + n aHat = P + (f+ + f-) aHat, so P = N - (f+ + f-) aHat
		return Vector3D.difference(
				getNodalPoint(),
				getOpticalAxisDirectionPos().getProductWith(getFocalLengthNeg() + getFocalLengthPos())
			);
	}
	
	/**
	 * @return	a vector with the Cartesian coordinates of the focal point in -ve space
	 */
	public Vector3D getFocalPointNeg()
	{
		// F- = P + f- * aHat = N - (f+ + f-) aHat + f- * aHat = N - f+ aHat
		// can also be understood as n = f+ + f-, so f- - n = -f+
		return Vector3D.difference(getNodalPoint(), getOpticalAxisDirectionPos().getProductWith(getFocalLengthPos()));
	}

	/**
	 * @return	a vector with the Cartesian coordinates of the focal point in +ve space
	 */
	public Vector3D getFocalPointPos()
	{
		// F+ = P + f+ * aHat = N - (f+ + f-) aHat + f+ * aHat = N - f- aHat
		// can also be understood as n = f+ + f-, so f+ - n = -f-
		return Vector3D.difference(getNodalPoint(), getOpticalAxisDirectionPos().getProductWith(getFocalLengthNeg()));
	}
	
	
	//
	// methods for calculating the parameters that correspond to given imaging characteristics
	//
	
	/**
	 * Set the parameters such that this glens hologram images Q- to Q+ and R- to R+.
	 * If this is not possible, then it finds approximate parameters.
	 * The nodal point, N, is set to be the point closest to the lines from Q- to Q+ and from R- to R+
	 * (if they intersect, then this is the intersection point).
	 * The focal lengths are set such the focal points are those points on the optical axis (which passes through
	 * N with opticalAxisDirectionPos) where it comes closest to the line between the orthographic projection of
	 * Q- into the glens plane and Q+, and where it comes closest to the line between the orthographic projection
	 * of Q+ into the glens plane and Q-.
	 * @param pointOnGlens	a point on the glens plane; not necessarily the principal point! 
	 * @param opticalAxisDirectionPos	normal to the glens plane, in the direction of the +ve optical axis
	 * @param QNeg
	 * @param QPos
	 * @param RNeg
	 * @param RPos
	 * @throws RayTraceException 
	 */
	public void calculateAndSetParameters(
			Vector3D pointOnGlens,
			Vector3D opticalAxisDirectionPos,
			Vector3D QNeg, Vector3D QPos,
			Vector3D RNeg, Vector3D RPos
		) throws RayTraceException
	{
		// normalise the optical axis direction, and set
		this.opticalAxisDirectionPos = opticalAxisDirectionPos.getNormalised();

		System.err.println("GlensHologram::calculateAndSetParameters: this.opticalAxisDirectionPos = "+this.opticalAxisDirectionPos);

		// the nodal point is the position where the lines between QNeg and QPos and between RNeg and RPos intersect
		// (if there is no intersection point, sets the nodal point instead to the point closest to both lines)
		this.nodalPoint = Geometry.lineLineIntersection(QNeg, Vector3D.difference(QPos, QNeg), RNeg, Vector3D.difference(RPos, RNeg));
		
		System.err.println("GlensHologram::calculateAndSetParameters: this.nodalPoint = "+this.nodalPoint);

		// the principal point is the orthographic projection of N into the glens plane
		Vector3D principalPoint = Geometry.orthographicProjection(nodalPoint, pointOnGlens, opticalAxisDirectionPos);

		System.err.println("GlensHologram::calculateAndSetParameters: principalPoint = "+principalPoint);

		// calculate the +ve focal length
		// the +ve focal point is the point where the optical axis gets closest to the
		// line from the orthographic projection into the glens plane of QNeg to QPos
		
		// orthographic projection into the glens plane of QNeg
		Vector3D QNegP = Geometry.orthographicProjection(QNeg, pointOnGlens, opticalAxisDirectionPos);
		
		// +ve focal point
		Vector3D FPos = Geometry.pointOfClosestApproach(
				principalPoint, opticalAxisDirectionPos,	// point on optical axis and its direction
				QNegP, Vector3D.difference(QPos, QNegP),	// point on line from QNegP to QPos and its direction
				true
		);
		
		// +ve focal length
		this.focalLengthPos = Vector3D.scalarProduct(Vector3D.difference(FPos, principalPoint), this.opticalAxisDirectionPos);
		
		System.err.println("GlensHologram::calculateAndSetParameters: focalLengthPos = "+focalLengthPos);

		// calculate the -ve focal length
		// the -ve focal point is the point where the optical axis gets closest to the
		// line from the orthographic projection into the glens plane of QPos to QNeg
		
		// orthographic projection into the glens plane of QPos
		Vector3D QPosP = Geometry.orthographicProjection(QPos, pointOnGlens, opticalAxisDirectionPos);
		
		// +ve focal point
		Vector3D FNeg = Geometry.pointOfClosestApproach(
				principalPoint, opticalAxisDirectionPos,	// point on optical axis and its direction
				QPosP, Vector3D.difference(QNeg, QPosP),	// point on line from QPosP to QNeg and its direction
				true
		);
		
		// +ve focal length
		this.focalLengthNeg = Vector3D.scalarProduct(Vector3D.difference(FNeg, principalPoint), this.opticalAxisDirectionPos);
		
		System.err.println("GlensHologram::calculateAndSetParameters: focalLengthNeg = "+focalLengthNeg);
	}
		
	/**
	 * Set the parameters such that this GlensHologram has nodal point N and images Q- to Q+.
	 * If this is not possible, then it finds approximate parameters.
	 * The focal lengths are set such the focal points are those points on the optical axis (which passes through
	 * N with opticalAxisDirectionPos) where it comes closest to the line between the orthographic projection of
	 * Q- into the glens plane and Q+, and where it comes closest to the line between the orthographic projection
	 * of Q+ into the glens plane and Q-.
	 * @param pointOnGlens	a point on the glens plane; not necessarily the principal point! 
	 * @param opticalAxisDirectionPos	normal to the glens plane, in the direction of the +ve optical axis
	 * @param nodalPoint
	 * @param QNeg
	 * @param QPos
	 */
	public void calculateAndSetParameters(
			Vector3D pointOnGlens,
			Vector3D opticalAxisDirectionPos,
			Vector3D nodalPoint,
			Vector3D QNeg, Vector3D QPos
		)
	{
		// normalise the optical axis direction, and set
		this.opticalAxisDirectionPos = opticalAxisDirectionPos.getNormalised();
		
		this.nodalPoint = nodalPoint;
		
		// the principal point is the orthographic projection of N into the glens plane
		Vector3D principalPoint = Geometry.orthographicProjection(nodalPoint, pointOnGlens, opticalAxisDirectionPos);

		// calculate the +ve focal length
		// the +ve focal point is the point where the optical axis gets closest to the
		// line from the orthographic projection into the glens plane of QNeg to QPos
		
		// orthographic projection into the glens plane of QNeg
		Vector3D QNegP = Geometry.orthographicProjection(QNeg, pointOnGlens, opticalAxisDirectionPos);
		
		// +ve focal point
		Vector3D FPos = Geometry.pointOfClosestApproach(
				principalPoint, opticalAxisDirectionPos,	// point on optical axis and its direction
				QNegP, Vector3D.difference(QPos, QNegP),	// point on line from QNegP to QPos and its direction
				true
		);
		
		// +ve focal length
		this.focalLengthPos = Vector3D.scalarProduct(Vector3D.difference(FPos, principalPoint), this.opticalAxisDirectionPos);

		// calculate the -ve focal length
		// the -ve focal point is the point where the optical axis gets closest to the
		// line from the orthographic projection into the glens plane of QPos to QNeg
		
		// orthographic projection into the glens plane of QPos
		Vector3D QPosP = Geometry.orthographicProjection(QPos, pointOnGlens, opticalAxisDirectionPos);
		
		// +ve focal point
		Vector3D FNeg = Geometry.pointOfClosestApproach(
				principalPoint, opticalAxisDirectionPos,	// point on optical axis and its direction
				QPosP, Vector3D.difference(QNeg, QPosP),	// point on line from QPosP to QNeg and its direction
				true
		);
		
		// +ve focal length
		this.focalLengthNeg = Vector3D.scalarProduct(Vector3D.difference(FNeg, principalPoint), this.opticalAxisDirectionPos);
	}

	
	
	//
	// a useful static method
	//

	/**
	 * From the principal point, the optical-axis direction, and the focal lengths, calculate the nodal point
	 * @param principalPoint
	 * @param opticalAxisDirectionPos
	 * @param focalLengthNeg
	 * @param focalLengthPos
	 * @return
	 */
	public static Vector3D calculateNodalPoint(Vector3D principalPoint, Vector3D opticalAxisDirectionPos, double focalLengthNeg, double focalLengthPos)
	{
		// N = P + (f+ + f-) aHat
		return Vector3D.sum(principalPoint, opticalAxisDirectionPos.getWithLength(focalLengthPos + focalLengthNeg));
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
		//	N is the nodal point
		//	S is the point where the ray intersects the glens
		// see glens paper glens.pdf
		
		double focalLengthO, focalLengthI;	// focal lengths in object and image space
		
		// the component of d in the direction of aHat
		double dA = Vector3D.scalarProduct(ray.getD(), getOpticalAxisDirectionPos());
		if(dA > 0)
		{
			// the ray is incident from -ve space, so image space is +ve space
			focalLengthO = getFocalLengthNeg();	// f
			focalLengthI = getFocalLengthPos();	// f'
		}
		else
		{
			// the ray is incident from +ve space, so image space is -ve space
			focalLengthO = getFocalLengthPos();	// f
			focalLengthI = getFocalLengthNeg();	// f'
		}
		
		// calculate normalised new light-ray direction
		Vector3D newRayDirection = Vector3D.sum(
				ray.getD().getProductWith(-focalLengthO/focalLengthI),	// -(f_o / f_i) d + ...
				Vector3D.difference(
						getNodalPoint(),	// (N - ...
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
	
	
	
	/**
	 * @param objectPosition
	 * @param direction	GlensImagingDirection.POS2NEG (+ve space to -ve space) or GlensImagingDirection.NEG2POS (-ve space to +ve space)
	 * @return	the image position
	 */
	public Vector3D getImagePosition(Vector3D objectPosition, ImagingDirection direction)
	{
		double objectSpaceFocalLength;
		
		// is the object position in +ve or -ve space?
		switch(direction)
		{
		case POS2NEG:
			// the object position is in +ve space
			// and the image position is in -ve space
			objectSpaceFocalLength = getFocalLengthPos();
			break;
		case NEG2POS:
		default:
			// the object position is in -ve space
			// and the image position is in +ve space
			objectSpaceFocalLength = getFocalLengthNeg();
			break;
		}
		
		// the axial component of the object position, measured from the principal point
		double a = Vector3D.scalarProduct(
				Vector3D.difference(objectPosition, getPrincipalPoint()),	// Q - P
				getOpticalAxisDirectionPos()	// aHat
			);
		
		return Vector3D.sum(
				objectPosition,
				Vector3D.difference(objectPosition, getNodalPoint()).getProductWith(a/(objectSpaceFocalLength-a))
			);
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
			// the image is at infinity; nudge the object position in the direction of the nodal point
			imagePosition = getImagePosition(
					Vector3D.sum(objectPosition, Vector3D.difference(nodalPoint, objectPosition).getWithLength(MyMath.TINY)),
					direction
					);
		}

		return imagePosition;
	}

	@Override
	public String toString() {
		return "GlensHologram [nodalPoint=" + nodalPoint + ", opticalAxisDirectionPos=" + opticalAxisDirectionPos
				+ ", focalLengthNeg=" + focalLengthNeg + ", focalLengthPos=" + focalLengthPos + "]";
	}
}
