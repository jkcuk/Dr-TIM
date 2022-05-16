package optics.raytrace.surfaces;


import math.*;
import optics.DoubleColour;
import optics.raytrace.core.*;
import optics.raytrace.exceptions.RayTraceException;


/**
 * A surface property representing a glens hologram.
 * 
 * There is another SurfaceProperty that represents a glens hologram, namely GlensHologram.  This is more complicated, and arguably not
 * as nice, but more general as it can deal with infinite focal lengths.
 * 
 * A glens is a generalised lens, with two different focal lengths [1].
 * It is the most general inhomogeneous planar surface that images every point in object space to a corresponding
 * point in image space, without offsetting the ray position upon transmission.
 * Such surfaces can be approximately realised in the form of planar GCLAs [2,3].
 * This is a generalisation of homogeneous imaging GCLAs [4], and of thin lenses.
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
 * like in the @see ThinLensHologramSurfaceCoordinates class).
 * 
 * [1] J. Courtial, "Geometric limits to geometric optical imaging with infinite, planar, non-absorbing sheets", Opt. Commun. <b>282</b>, 2480-2483 (2009)
 * [2] A. C. Hamilton and J. Courtial, "Generalized refraction using lenslet arrays", J. Opt. A: Pure Appl. Opt. <b>11</b>, 065502 (2009)
 * [3] S. Oxburgh, C. D. White, G. Antoniou, and J. Courtial, "Law of refraction for generalised confocal lenslet arrays", Opt. Commun. <b>313</b>, 119-122 (2014)
 * [4] S. Oxburgh and J. Courtial, "Perfect imaging with planar interfaces", J. Opt. Soc. Am. A 30, 2334-2338 (2013)
 * 
 * @author Johannes Courtial
 */
public class SimpleGlensHologram extends SurfacePropertyPrimitive
{
	private static final long serialVersionUID = -3071691449673977767L;

	// the coordinates of the nodal point, N (any light rays incident on N pass through undeviated; in a lens, N=P)
	protected Vector3D nodalPoint;
		
	// a unit vector in the direction of the optical axis, pointing in the direction of +ve space
	protected Vector3D opticalAxisDirectionPos;
	
	// dimensionless focal lengths:
	//	focalLengthPos (= f^+): the axial coordinate of the +ve-space focal point
	//	focalLengthNeg (= f^-): the axial coordinate of the +ve-space focal point
	protected double
		focalLengthNeg, focalLengthPos;	// focal lengths
	
	/**
	 * Creates an instance of the surface property that refracts light like a glens.
	 * All parameters are explicitly given exactly as they are being stored.
	 * 
	 * @param opticalAxisDirectionPos	the vector aHat
	 * @param nodalPoint	the coordinates of the nodal point, N
	 * @param focalLengthNeg	the focal length in -ve space, f-
	 * @param focalLengthPos	the focal length in +ve space, f+
	 * @param transmissionCoefficient
	 */
	public SimpleGlensHologram(
			Vector3D opticalAxisDirectionPos,
			Vector3D nodalPoint,
			double focalLengthNeg,
			double focalLengthPos,
			double transmissionCoefficient,
			boolean shadowThrowing
		) 
	{
		super(transmissionCoefficient, shadowThrowing);
		setParameters(
				opticalAxisDirectionPos, 
				nodalPoint,
				focalLengthNeg,
				focalLengthPos
			);
	}
		
	/**
	 * Create what is effectively a transparent surface that doesn't change light-ray direction.
	 * Set its parameters later using @see setParameters.
	 * @param transmissionCoefficient
	 * @param shadowThrowing
	 */
	public SimpleGlensHologram(
			double transmissionCoefficient,
			boolean shadowThrowing
		)
	{
		super(transmissionCoefficient, shadowThrowing);
		setDefaultParameters();
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
	 */
	public SimpleGlensHologram(
			Vector3D opticalAxisDirectionPos,
			Vector3D pointOnGlens,
			Vector3D QNeg, Vector3D QPos,
			Vector3D RNeg, Vector3D RPos,
			double transmissionCoefficient,
			boolean shadowThrowing
		)
	{
		super(transmissionCoefficient, shadowThrowing);
		
		try
		{
			calculateAndSetParameters(opticalAxisDirectionPos, pointOnGlens, QNeg, QPos, RNeg, RPos);
		} catch (RayTraceException e)
		{
			System.out.println(e.getMessage()+" Returning hologram for glens that does nothing.");
			setDefaultParameters();
			// e.printStackTrace();
		}
	}
		
	/**
	 * Constructor that creates an instance of a GlensHologram that has nodal point N and that images Q- to Q+.
	 * If this is not possible, then it finds approximate parameters.
	 * The focal lengths are set such the focal points are those points on the optical axis (which passes through
	 * N with opticalAxisDirectionPos) where it comes closest to the line between the orthographic projection of
	 * Q- into the glens plane and Q+, and where it comes closest to the line between the orthographic projection
	 * of Q+ into the glens plane and Q-.
	 * @param opticalAxisDirectionPos	normal to the glens plane, in the direction of the +ve optical axis
	 * @param pointOnGlens	a point on the glens plane; not necessarily the principal point! 
	 * @param nodalPoint
	 * @param QNeg
	 * @param QPos
	 * @param transmissionCoefficient
	 * @param shadowThrowing
	 */
	public SimpleGlensHologram(
			Vector3D opticalAxisDirectionPos,
			Vector3D pointOnGlens,
			Vector3D nodalPoint,
			Vector3D QNeg, Vector3D QPos,
			double transmissionCoefficient,
			boolean shadowThrowing
		)
	{
		super(transmissionCoefficient, shadowThrowing);
		
		calculateAndSetParameters(opticalAxisDirectionPos, pointOnGlens, nodalPoint, QNeg, QPos);
	}

	/**
	 * Make a clone of the original GlensHologram.
	 * @param original
	 */
	public SimpleGlensHologram(SimpleGlensHologram original)
	{
		this(
				original.getOpticalAxisDirectionPos(),
				// original.getPrincipalPoint(),
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
	@Override
	public SimpleGlensHologram clone()
	{
		return new SimpleGlensHologram(this);
	}
	
	// setters and getters
	
	public void setDefaultParameters()
	{
		setParameters(
				new Vector3D(0, 0, 1),	// opticalAxisDirectionPos
				new Vector3D(0, 0, 0),	//nodalPoint
				-1,	// focalLengthNeg
				1	// focalLengthPos
				);
	}

	/**
	 * Set all parameters of the GlensHologram directly;
	 * the optical axis direction gets normalised
	 * 
	 * @param opticalAxisDirectionPos
	 * @param nodalPoint
	 * @param focalLengthNeg
	 * @param focalLengthPos
	 */
	public void setParameters(
			Vector3D opticalAxisDirectionPos,
			Vector3D nodalPoint,
			double focalLengthNeg,
			double focalLengthPos
		)
	{
//		System.out.println("GlensHologram::setParameters: nodalPoint="+nodalPoint+
//				", optical axis direction (+) = "+opticalAxisDirectionPos+
//				", f- = "+focalLengthNeg+
//				", f+ = "+focalLengthPos);
		
		this.opticalAxisDirectionPos = opticalAxisDirectionPos.getNormalised();
		this.nodalPoint = nodalPoint;
		this.focalLengthNeg  = focalLengthNeg;
		this.focalLengthPos = focalLengthPos;
	}

	/**
	 * Set all parameters of the GlensHologram from the optical-axis direction, the principal point, and the focal lengths;
	 * suitable for finite focal lengths, i.e. inhomogeneous glenses
	 * @param opticalAxisDirectionPos
	 * @param focalLengthNeg
	 * @param focalLengthPos
	 */
	public void setParametersUsingPrincipalPoint(
			Vector3D opticalAxisDirectionPos,
			Vector3D principalPoint,
			double focalLengthNeg,
			double focalLengthPos
		)
	{
		setParameters(
				opticalAxisDirectionPos,
				calculateNodalPoint(opticalAxisDirectionPos, principalPoint, focalLengthNeg, focalLengthPos),	// nodalPoint,
				focalLengthNeg,
				focalLengthPos
			);
	}


	/**
	 * Set all parameters of the GlensHologram from the optical-axis direction, the principal point, and the focal lengths;
	 * suitable for finite focal lengths, i.e. inhomogeneous glenses
	 * @param opticalAxisDirectionPos
	 * @param nodalPoint
	 * @param focalLengthNeg
	 * @param focalLengthPos
	 */
	public void setParametersUsingNodalPoint(
			Vector3D opticalAxisDirectionPos,
			Vector3D nodalPoint,
			double focalLengthNeg,
			double focalLengthPos
		)
	{
		setParameters(
				opticalAxisDirectionPos,
				nodalPoint,
				focalLengthNeg,
				focalLengthPos
			);
	}
	
	public Vector3D getOpticalAxisDirectionPos() {
		return opticalAxisDirectionPos;
	}

	public Vector3D getNodalPoint() {
		return nodalPoint;
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
	 * @return	a vector with the Cartesian coordinates of the focal point in -ve space
	 */
	public Vector3D getFocalPointNeg()
	{
		// F- = P + f- * aHat = N - (f+ + f-) aHat + f- * aHat = N - f+ aHat
		return Vector3D.difference(
				getNodalPoint(),	// N - ...
				getOpticalAxisDirectionPos().getProductWith(getFocalLengthPos())	// ... - f+ aHat
			);
	}
	
	public Vector3D getPrincipalPoint()
	{
		return calculatePrincipalPoint(opticalAxisDirectionPos, nodalPoint, focalLengthNeg, focalLengthPos);
	}

	/**
	 * @return	a vector with the Cartesian coordinates of the focal point in +ve space
	 */
	public Vector3D getFocalPointPos()
	{
		// F+ = P + f+ * aHat = N - (f+ + f-) aHat + f+ * aHat = N - f- aHat
		return Vector3D.difference(
				getNodalPoint(),	// N - ...
				getOpticalAxisDirectionPos().getProductWith(getFocalLengthNeg())	// ... - f- aHat
			);
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
	 * Assumes (but doesn't check!) that Q-, Q+, R-, and R+ are not all collinear and that Q- != Q+ and R- != R+.
	 * @param pointOnGlens	a point on the glens plane; not necessarily the principal point! 
	 * @param opticalAxisDirectionPos	normal to the glens plane, in the direction of the +ve optical axis
	 * @param QNeg
	 * @param QPos
	 * @param RNeg
	 * @param RPos
	 * @throws RayTraceException 
	 */
	public void calculateAndSetParameters(
			Vector3D opticalAxisDirectionPos,
			Vector3D pointOnGlens,
			Vector3D QNeg, Vector3D QPos,
			Vector3D RNeg, Vector3D RPos
		) throws RayTraceException
	{
		// assume that QNeg, QPos, RNeg, and RPos are not all collinear
		// check if the vectors (QPos - QNeg) and (RPos - RNeg) are parallel...
		Vector3D
			QNeg2Pos = Vector3D.difference(QPos, QNeg).getNormalised(),
			RNeg2Pos = Vector3D.difference(RPos, RNeg).getNormalised();
		// ... by calculating the squared length of their cross product
		double crossProductLength2 = Vector3D.crossProduct(QNeg2Pos, RNeg2Pos).getModSquared();
		
		// are the vectors parallel?
		if(crossProductLength2 <= MyMath.EPSILON)
		{
			// the vectors are parallel, which means that the glens that performs the required imaging --- if there
			// exists one --- is homogeneous
			
			// throw an exception
			throw new RayTraceException("Glens must be homogeneous.");
		}
		else
		{
			// the nodal point is the position where the lines between QNeg and QPos and between RNeg and RPos intersect
			// (if there is no intersection point, sets the nodal point instead to the point closest to both lines)
			Vector3D nodalPoint = Geometry.lineLineIntersection(QNeg, Vector3D.difference(QPos, QNeg), RNeg, Vector3D.difference(RPos, RNeg));

			calculateAndSetParameters(
					opticalAxisDirectionPos,
					pointOnGlens,
					nodalPoint,
					QNeg, QPos
				);
		}
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
			Vector3D opticalAxisDirectionPos,
			Vector3D pointOnGlens,
			Vector3D nodalPoint,
			Vector3D QNeg, Vector3D QPos
		)
	{
		// normalise the optical axis direction, and set
		this.opticalAxisDirectionPos = opticalAxisDirectionPos.getNormalised();
		
		// the nodal point is at a finite distance
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
		double focalLengthPos = Vector3D.scalarProduct(Vector3D.difference(FPos, principalPoint), this.opticalAxisDirectionPos);

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
		double focalLengthNeg = Vector3D.scalarProduct(Vector3D.difference(FNeg, principalPoint), this.opticalAxisDirectionPos);

		setParameters(
				opticalAxisDirectionPos,
				nodalPoint,
				focalLengthNeg,
				focalLengthPos
			);

//		this.nodalPoint = nodalPoint;
//		
//		// the principal point is the orthographic projection of N into the glens plane
//		Vector3D principalPoint = Geometry.orthographicProjection(nodalPoint, pointOnGlens, opticalAxisDirectionPos);
//
//		// calculate the +ve focal length
//		// the +ve focal point is the point where the optical axis gets closest to the
//		// line from the orthographic projection into the glens plane of QNeg to QPos
//		
//		// orthographic projection into the glens plane of QNeg
//		Vector3D QNegP = Geometry.orthographicProjection(QNeg, pointOnGlens, opticalAxisDirectionPos);
//		
//		// +ve focal point
//		Vector3D FPos = Geometry.pointOfClosestApproach(
//				principalPoint, opticalAxisDirectionPos,	// point on optical axis and its direction
//				QNegP, Vector3D.difference(QPos, QNegP),	// point on line from QNegP to QPos and its direction
//				true
//		);
//		
//		// +ve focal length
//		this.focalLengthPos = Vector3D.scalarProduct(Vector3D.difference(FPos, principalPoint), this.opticalAxisDirectionPos);
//
//		// calculate the -ve focal length
//		// the -ve focal point is the point where the optical axis gets closest to the
//		// line from the orthographic projection into the glens plane of QPos to QNeg
//		
//		// orthographic projection into the glens plane of QPos
//		Vector3D QPosP = Geometry.orthographicProjection(QPos, pointOnGlens, opticalAxisDirectionPos);
//		
//		// +ve focal point
//		Vector3D FNeg = Geometry.pointOfClosestApproach(
//				principalPoint, opticalAxisDirectionPos,	// point on optical axis and its direction
//				QPosP, Vector3D.difference(QNeg, QPosP),	// point on line from QPosP to QNeg and its direction
//				true
//		);
//		
//		// +ve focal length
//		this.focalLengthNeg = Vector3D.scalarProduct(Vector3D.difference(FNeg, principalPoint), this.opticalAxisDirectionPos);
	}

	
	
	//
	// useful static methods
	//
	
	/**
	 * From the optical-axis direction, the principal point, and the focal lengths, calculate the nodal point
	 * @param opticalAxisDirectionPos
	 * @param principalPoint
	 * @param focalLengthNeg
	 * @param focalLengthPos
	 * @return
	 */
	public static Vector3D calculateNodalPoint(Vector3D opticalAxisDirectionPos, Vector3D principalPoint, double focalLengthNeg, double focalLengthPos)
	{
		// N = P + (f+ + f-) aHat
		return Vector3D.sum(principalPoint, opticalAxisDirectionPos.getWithLength(focalLengthPos + focalLengthNeg));
	}

	/**
	 * From the optical-axis direction, the nodal point, and the focal lengths, calculate the principal point
	 * @param opticalAxisDirectionPos
	 * @param nodalPoint
	 * @param focalLengthNeg
	 * @param focalLengthPos
	 * @return	a vector with the Cartesian coordinates of the principal point
	 */
	public Vector3D calculatePrincipalPoint(Vector3D opticalAxisDirectionPos, Vector3D nodalPoint, double focalLengthNeg, double focalLengthPos)
	{
		// N = P + n aHat = P + (f+ + f-) aHat, so P = N - (f+ + f-) aHat
		return Vector3D.difference(
				nodalPoint,	// N - ...
				getOpticalAxisDirectionPos().getProductWith(focalLengthNeg + focalLengthPos)	// ... - (f- + f+) aHat
			);
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
		
		double focalLengthO, focalLengthI;	// dimensionless focal lengths in object and image space
		
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
		
		// -d f/f' + (N - S)*d_a/f'
		Vector3D newRayDirection = Vector3D.sum(
					ray.getD().getProductWith(-focalLengthO/focalLengthI),	// -(f/f') d + ...
					Vector3D.difference(
							getNodalPoint(),	// (N - ...
							i.p					// ... S) ...
						).getProductWith(dA/focalLengthI)	// ... * (d_a/f')
					);	// .getNormalised();	// finally, normalise the whole lot
		
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
	
	
	
	/**
	 * @param objectPosition
	 * @param direction	GlensImagingDirection.POS2NEG (+ve space to -ve space) or GlensImagingDirection.NEG2POS (-ve space to +ve space)
	 * @return	the image position
	 */
	public Vector3D getImagePosition(Vector3D objectPosition, ImagingDirection direction)
	{
		double objectSpaceFocalLength;	// f
		
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
		
		// the distance of the object position from the glens plane
		double a = Vector3D.scalarProduct(
				Vector3D.difference(objectPosition, getPrincipalPoint()),	// Q - P
				getOpticalAxisDirectionPos()	// aHat
			);
		
		return Vector3D.sum(
				objectPosition,	// Q + ...
				Vector3D.difference(
						objectPosition,	// ... + (Q - ...
						getNodalPoint()	// ... - N) * ...
					).getProductWith(a/(objectSpaceFocalLength-a))	// ... * (a/(f - a))
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
		Vector3D imagePosition = getImagePosition(objectPosition, direction);

		// check if any of the coordinates are infinite
		if(imagePosition.isComponentInf())
		{
			// the image is at infinity; nudge the object position in the axial direction
			imagePosition = getImagePosition(
					Vector3D.sum(
							objectPosition,	// Q + ...
							getOpticalAxisDirectionPos().getProductWith(MyMath.TINY) // ... delta*aHat
							// Vector3D.difference(getPrincipalPoint(), objectPosition).getWithLength(MyMath.TINY)	// ... Delta * (P-Q)
						),
					direction
				);
		}

		return imagePosition;
	}

	@Override
	public String toString() {
		return "SimpleGlensHologram [nodalPoint=" + nodalPoint
				+ ", opticalAxisDirectionPos=" + opticalAxisDirectionPos + ", focalLengthNeg=" + focalLengthNeg
				+ ", focalLengthPos=" + focalLengthPos + "]";
	}
}
