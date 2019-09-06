package optics.raytrace.surfaces;


import math.*;
import optics.DoubleColour;
import optics.raytrace.core.*;
import optics.raytrace.exceptions.RayTraceException;


/**
 * A surface property representing a glens hologram.
 * 
 * There is another SurfaceProperty that represents a glens hologram, namely SimpleGlensHologram.  This is simpler, and arguably nicer,
 * but not as general as it cannot deal with infinite focal lengths.
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
 * Everything is defined in dimensionless coordinates relative to the (geometric; others would also work) mean of |f^+| and |f^-|,
 * F = \sqrt{|f^+ f^-|}, e.g. f^+_F = f^+/F and N_F = N/F.
 * 
 * [1] J. Courtial, "Geometric limits to geometric optical imaging with infinite, planar, non-absorbing sheets", Opt. Commun. <b>282</b>, 2480-2483 (2009)
 * [2] A. C. Hamilton and J. Courtial, "Generalized refraction using lenslet arrays", J. Opt. A: Pure Appl. Opt. <b>11</b>, 065502 (2009)
 * [3] S. Oxburgh, C. D. White, G. Antoniou, and J. Courtial, "Law of refraction for generalised confocal lenslet arrays", Opt. Commun. <b>313</b>, 119-122 (2014)
 * [4] S. Oxburgh and J. Courtial, "Perfect imaging with planar interfaces", J. Opt. Soc. Am. A 30, 2334-2338 (2013)
 * 
 * @author Johannes Courtial
 */
public class GlensHologram_old extends SurfacePropertyPrimitive
{
	private static final long serialVersionUID = 134597862435L;
	
	protected double meanF;	// (geometric) mean of |f^+| and |f^-|, i.e. F = \sqrt{|f^+ f^-|}

	// the dimensionless coordinates N_F = N/F of the nodal point, N (any light rays incident on N pass through undeviated; in a lens, N=P)
	protected Vector3D nodalPointF;
	
	// the principal point
	// protected Vector3D principalPoint;
	
	// a point in the glens plane
	protected Vector3D pointOnGlens;
	
	// a unit vector in the direction of the optical axis, pointing in the direction of +ve space
	protected Vector3D opticalAxisDirectionPos;
	
	// dimensionless focal lengths:
	//	focalLengthPosF (= f^+ / F): the axial coordinate of the +ve-space focal point
	//	focalLengthNegF (= f^- / F): the axial coordinate of the +ve-space focal point
	protected double
		focalLengthNegF, focalLengthPosF;	// focal lengths
	
	/**
	 * Creates an instance of the surface property that refracts light like a glens.
	 * All parameters are explicitly given exactly as they are being stored.
	 * 
	 * @param opticalAxisDirectionPos	the vector aHat
	 * @param geometricMeanF	F=\sqrt{|f+ f-|}
	 * @param nodalPointF	the dimensionless coordinates N/F of the point, N
	 * @param focalLengthNegF	the dimensionless focal length in -ve space, f-/F
	 * @param focalLengthPosF	the dimensionless focal length in +ve space, f+/F
	 * @param transmissionCoefficient
	 */
	public GlensHologram_old(
			Vector3D opticalAxisDirectionPos,
			// Vector3D principalPoint,
			Vector3D pointOnGlens,
			double geometricMeanF,
			Vector3D nodalPointF,
			double focalLengthNegF,
			double focalLengthPosF,
			double transmissionCoefficient,
			boolean shadowThrowing
		) 
	{
		super(transmissionCoefficient, shadowThrowing);
		setParameters(
				opticalAxisDirectionPos, 
				// principalPoint,
				pointOnGlens,
				geometricMeanF,
				nodalPointF,
				focalLengthNegF,
				focalLengthPosF
			);
	}
	
	/**
	 * Creates an instance of the surface property that refracts light like a glens.
	 * This constructor is suitable for a glens with finite focal lengths.
	 * 
	 * @param opticalAxisDirectionPos
	 * @param principalPoint
	 * @param focalLengthNeg
	 * @param focalLengthPos
	 * @param transmissionCoefficient
	 * @param shadowThrowing
	 */
	public GlensHologram_old(
			Vector3D opticalAxisDirectionPos,
			Vector3D principalPoint,
			double focalLengthNeg,
			double focalLengthPos,
			double transmissionCoefficient,
			boolean shadowThrowing
		)
	{
		super(transmissionCoefficient, shadowThrowing);
		setParametersUsingPrincipalPoint(
				opticalAxisDirectionPos,
				principalPoint,
				focalLengthNeg,
				focalLengthPos
			);
	}
	
	/**
	 * Creates an instance of the surface property that refracts light like a glens.
	 * This constructor is suitable for a homogeneous glens, with infinite focal lengths.
	 * 
	 * @param opticalAxisDirectionPos
	 * @param pointOnGlens
	 * @param nodalDirection
	 * @param fNegOverFPos
	 * @param transmissionCoefficient
	 * @param shadowThrowing
	 */
	public GlensHologram_old(
			Vector3D opticalAxisDirectionPos,
			Vector3D pointOnGlens,
			Vector3D nodalDirection,
			double fNegOverFPos,
			double transmissionCoefficient,
			boolean shadowThrowing
		) 
	{
		super(transmissionCoefficient, shadowThrowing);
		
		setParameters(
				opticalAxisDirectionPos,
				pointOnGlens,
				nodalDirection,
				fNegOverFPos
			);
	}

	/**
	 * Create what is effectively a transparent surface that doesn't change light-ray direction.
	 * Set its parameters later using @see setParameters.
	 * @param transmissionCoefficient
	 * @param shadowThrowing
	 */
	public GlensHologram_old(
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
	public GlensHologram_old(
			Vector3D pointOnGlens,
			Vector3D opticalAxisDirectionPos,
			Vector3D QNeg, Vector3D QPos,
			Vector3D RNeg, Vector3D RPos,
			double transmissionCoefficient,
			boolean shadowThrowing
		)
	{
		super(transmissionCoefficient, shadowThrowing);
		
		try
		{
			calculateAndSetParameters(pointOnGlens, opticalAxisDirectionPos, QNeg, QPos, RNeg, RPos);
		} catch (RayTraceException e)
		{
			System.out.flush();
			System.err.println("GlensHologram::GlensHologram: " + e.getMessage());
			System.err.println("GlensHologram::GlensHologram: pointOnGlens="+pointOnGlens
					+ ", opticalAxisDirectionPos="+opticalAxisDirectionPos
					+ ", QNeg="+QNeg
					+ ", QPos="+QPos
					+ ", RNeg="+RNeg
					+ ", RPos="+RPos
			);
			System.err.flush();
			e.printStackTrace();
		}
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
	public GlensHologram_old(
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
	 * Constructor that creates an instance of a GlensHologram that represents a homogeneous glens that images Q- to Q+.
	 * @param pointOnGlens	a point on the glens plane
	 * @param opticalAxisDirectionPos	normal to the glens plane, in the direction of the +ve optical axis
	 * @param QNeg
	 * @param QPos
	 * @param transmissionCoefficient
	 * @param shadowThrowing
	 */
	public GlensHologram_old(
			Vector3D pointOnGlens,
			Vector3D opticalAxisDirectionPos,
			Vector3D QNeg, Vector3D QPos,
			double transmissionCoefficient,
			boolean shadowThrowing
		)
	{
		super(transmissionCoefficient, shadowThrowing);
		
		calculateAndSetParameters(
			pointOnGlens,
			opticalAxisDirectionPos,
			QNeg, QPos
		);
	}


	/**
	 * Make a clone of the original GlensHologram.
	 * @param original
	 */
	public GlensHologram_old(GlensHologram_old original)
	{
		this(
				original.getOpticalAxisDirectionPos(),
				// original.getPrincipalPoint(),
				original.getPointOnGlens(),
				original.getMeanF(),
				original.getNodalPointF(),
				original.getFocalLengthNegF(),
				original.getFocalLengthPosF(),
				original.getTransmissionCoefficient(),
				original.isShadowThrowing()
			);		
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public GlensHologram_old clone()
	{
		return new GlensHologram_old(this);
	}
	
	// setters and getters
	
	public void setDefaultParameters()
	{
		setParameters(
				new Vector3D(0, 0, 1),	// opticalAxisDirectionPos
				new Vector3D(0, 0, 0),	// point on glens, here same as principal point
				Double.POSITIVE_INFINITY,	// geometricMeanF = \infty
				new Vector3D(0, 0, 0),	//nodalPointF
				-1,	// focalLengthNegF
				1	// focalLengthPosF
				);
	}

	/**
	 * Set all parameters of the GlensHologram directly;
	 * the optical axis direction gets normalised
	 * 
	 * @param opticalAxisDirectionPos
	 * @param pointOnGlens
	 * @param meanF
	 * @param nodalPoint
	 * @param focalLengthNegF
	 * @param focalLengthPosF
	 */
	public void setParameters(
			Vector3D opticalAxisDirectionPos,
			// Vector3D principalPoint,
			Vector3D pointOnGlens,
			double meanF,
			Vector3D nodalPointF,
			double focalLengthNegF,
			double focalLengthPosF
		)
	{
//		System.out.println("GlensHologram::setParameters: nodalPoint="+nodalPoint+
//				", optical axis direction (+) = "+opticalAxisDirectionPos+
//				", f- = "+focalLengthNeg+
//				", f+ = "+focalLengthPos);
		
		this.opticalAxisDirectionPos = opticalAxisDirectionPos.getNormalised();
		this.pointOnGlens = pointOnGlens;
		this.meanF = meanF;
		this.nodalPointF = nodalPointF;
		this.focalLengthNegF = focalLengthNegF;
		this.focalLengthPosF = focalLengthPosF;
	}

	/**
	 * Set all parameters of the GlensHologram from the optical-axis direction, the principal point, and the focal lengths;
	 * suitable for finite focal lengths, i.e. inhomogeneous glenses
	 * @param opticalAxisDirectionPos
	 * @param pointOnGlens
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
		double F = calculateMean(focalLengthNeg, focalLengthPos);	// geometric mean of |f-| and |f+|
		setParameters(
				opticalAxisDirectionPos,
				principalPoint,	// pointOnGlens
				F,	// meanF,
				calculateNodalPoint(opticalAxisDirectionPos, principalPoint, focalLengthNeg, focalLengthPos).getProductWith(1/F),	// nodalPointF,
				focalLengthNeg / F,
				focalLengthPos / F
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
		double F = calculateMean(focalLengthNeg, focalLengthPos);	// geometric mean of |f-| and |f+|
		setParameters(
				opticalAxisDirectionPos,
				calculatePrincipalPoint(opticalAxisDirectionPos, nodalPoint, focalLengthNeg, focalLengthPos),	// point on glens
				F,	// meanF,
				nodalPoint.getProductWith(1/F),	// nodalPointF,
				focalLengthNeg / F,
				focalLengthPos / F
			);
	}
	
	/**
	 * Set the parameters in a way suitable for homogeneous glenses.
	 * 
	 * @param opticalAxisDirectionPos
	 * @param pointOnGlens
	 * @param nodalDirection
	 * @param fNegOverFPos
	 */
	public void setParameters(
			Vector3D opticalAxisDirectionPos,
			Vector3D pointOnGlens,
			Vector3D nodalDirection,
			double fNegOverFPos
		) 
	{
		// the nodal point is a distance (f- + f+) from the glens, so it is at
		// P + (f- + f+) nodalDirection/nodalDirection_a;
		// N/F is therefore
		// N/F = P/F + (f-/F + f+/F) nodalDirection/nodalDirection_a; in the limit F->\infty, this becomes
		// N/F = (f-/F + f+/F) nodalDirection/nodalDirection_a

		double
			focalLengthNegF = calculateAbsFocalLengthNegF(fNegOverFPos),
			focalLengthPosF = calculateAbsFocalLengthPosF(1/fNegOverFPos);
		// TODO check the signs!
		
		setParameters(
				opticalAxisDirectionPos,
				pointOnGlens,
				Double.POSITIVE_INFINITY,	// geometricMeanF,
				nodalDirection.getProductWith(
						(focalLengthNegF+focalLengthPosF)/	// (f-/F + f+/F) / ...
						Vector3D.scalarProduct(nodalDirection, opticalAxisDirectionPos.getNormalised())	// ... / nodalDirection_a
					), // nodalPointF,
				focalLengthNegF,	// focalLengthNegF,
				focalLengthPosF	// focalLengthPosF
			);
	}

	public Vector3D getOpticalAxisDirectionPos() {
		return opticalAxisDirectionPos;
	}

	/**
	 * Returns the point on the glens
	 * @return	a vector with the Cartesian coordinates of the principal point
	 * 
	 */
	public Vector3D getPointOnGlens()
	{
		return pointOnGlens;
	}
	
	public double getMeanF() {
		return meanF;
	}
	
	public Vector3D getNodalPointF() {
		return nodalPointF;
	}

//	public void setNodalPointF(Vector3D nodalPointF) {
//		this.nodalPointF = nodalPointF;
//	}

	public double getFocalLengthNegF() {
		return focalLengthNegF;
	}

	public double getFocalLengthPosF() {
		return focalLengthPosF;
	}
	
	//
	// other cardinal distances
	//
	
	public double getNodalDistance()
	{
		return meanF*(focalLengthNegF + focalLengthPosF);
	}

	//
	// the cardinal points
	//
	
	/**
	 * @return	a vector with the Cartesian coordinates of the focal point in -ve space
	 */
	public Vector3D getFocalPointNeg()
	{
		// F- = P + f- * aHat = N - (f+ + f-) aHat + f- * aHat = N - f+ aHat = F (N/F - f+/F aHat)
		return Vector3D.difference(
				getNodalPointF(),	// (N/F - ...
				getOpticalAxisDirectionPos().getProductWith(getFocalLengthPosF())	// ... - f+/F aHat) * ...
			).getProductWith(getMeanF());	// ... * F
	}

	/**
	 * @return	a vector with the Cartesian coordinates of the focal point in +ve space
	 */
	public Vector3D getFocalPointPos()
	{
		// F+ = P + f+ * aHat = N - (f+ + f-) aHat + f+ * aHat = N - f- aHat = F (N/F - f-/F aHat)
		return Vector3D.difference(
				getNodalPointF(),	// (N/F - ...
				getOpticalAxisDirectionPos().getProductWith(getFocalLengthNegF())	// ... - f-/F aHat) * ...
			).getProductWith(getMeanF());	// ... * F
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
			Vector3D pointOnGlens,
			Vector3D opticalAxisDirectionPos,
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
		if(crossProductLength2 < MyMath.EPSILON)
		{
			// the vectors (QPos - QNeg) and (RPos - RNeg) are parallel, which means that the glens that performs the required imaging ---
			// if there exists one --- is homogeneous, so define it as such
			
			System.out.println("GlensHologram::calculateAndSetParameters: glens is homogeneous");
			
			// the focal lengths are infinite, and so the geometric mean of their absolute values is also infinite
			this.meanF = Double.POSITIVE_INFINITY;
			
			// use pointOnGlens as the principal point
			// this.principalPoint = pointOnGlens;
			
			// normalise the optical axis direction, and set
			this.opticalAxisDirectionPos = opticalAxisDirectionPos.getNormalised();
			
			// in the limit F -> \infty, the mapping equation between object and image space is
			// Q' = Q - a (N/F) / (f/F) = Q - a (N/f), so can calculate N/f = (Q - Q')/a
			
			// first choose -ve space to be object space and +ve space to be image space; then N/f- = (Q- - Q+)/a-
			
			// first calculate a-, ...
			double aNeg = Vector3D.scalarProduct(
					Vector3D.difference(QNeg, pointOnGlens),	// this.principalPoint),	// (Q- - P). ...
					this.opticalAxisDirectionPos	// ... .aHat
				);
			
			// ... and then N/f-
			Vector3D NOverFNeg = Vector3D.difference(QNeg, QPos).getProductWith(1/aNeg);
			
			// now choose +ve space to be object space and -ve space to be image space; then N/f+ = (Q+ - Q-)/a+
			
			// first calculate a+, ...
			double aPos = Vector3D.scalarProduct(
					Vector3D.difference(QPos, pointOnGlens),	// this.principalPoint),	// (Q+ - P). ...
					this.opticalAxisDirectionPos	// ... .aHat
				);
			
			// ... and then N/f+
			Vector3D NOverFPos = Vector3D.difference(QPos, QNeg).getProductWith(1/aPos);
			
			// check that N/f- and N/f+ are parallel
			if(Vector3D.crossProduct(NOverFNeg, NOverFPos).getModSquared() < MyMath.EPSILON)
			{
				// the cross product of N/f- and N/f+ is zero, which means they are parallel;
				// this is a necessary (and sufficient?) condition that the glens that images as required exists
				
				// f-/f+ = |N/f+| / |N/f-|
				double fNegOverFPos = NOverFPos.getLength() / NOverFNeg.getLength();
				
				// calculate f-/F by calculating |f-/F| and choosing the +ve sign
				double fNegOverF = calculateAbsFocalLengthNegF(fNegOverFPos);
				
				setParameters(
						opticalAxisDirectionPos,
						pointOnGlens,
						Double.POSITIVE_INFINITY,	// F
						NOverFNeg.getProductWith(fNegOverF),	// N/F = (N/f-) * (f-/F)
						fNegOverF,
						calculateAbsFocalLengthPosF(1/fNegOverFPos)	// again, choose positive sign
					);
			}
			else
			{
				// the cross product of N/f- and N/f+ is non-zero, so the two vectors are not parallel,
				// which implies that the required glens does not exist

				setDefaultParameters();

				// throw an exception
				throw new RayTraceException("Glens must be homogeneous, but cannot find homogeneous glens that images as required.  Returning transparent glens.");
				// Output a warning message
				// System.err.println("GlensHologram::calculateAndSetParameters: ");
			}
		}
		else
		{
			// the vectors (QPos - QNeg) and (RPos - RNeg) are *not* parallel, which means that the glens that performs the required imaging ---
			// if there exists one --- is inhomogeneous, so define it as such

			// the nodal point is the position where the lines between QNeg and QPos and between RNeg and RPos intersect
			// (if the lines don't intersect, a RayTraceException is being thrown)
			Vector3D nodalPoint = Geometry.lineLineIntersection(QNeg, Vector3D.difference(QPos, QNeg), RNeg, Vector3D.difference(RPos, RNeg));

			calculateAndSetParameters(
					pointOnGlens,
					opticalAxisDirectionPos,
					nodalPoint,
					QNeg, QPos
				);
		}

		// check if the two object-image pairs are conjugate
		Vector3D
			QNegDash = getImagePosition(QNeg, ImagingDirection.NEG2POS),
			RNegDash = getImagePosition(RNeg, ImagingDirection.NEG2POS);
		double
			errQ = Vector3D.difference(QNegDash, QPos).getModSquared(),
			errR = Vector3D.difference(RNegDash, RPos).getModSquared();
		
		if(errQ + errR > MyMath.EPSILON)
		{
			String message = "";
			
			if(errQ > MyMath.EPSILON) message = message + "The image of Q- is "+QNegDash+", not Q+ (="+QPos+"). ";
			if(errR > MyMath.EPSILON) message = message + "The image of R- is "+RNegDash+", not R+ (="+RPos+"). ";

			// throw an exception
			throw new RayTraceException("The GlensHologram does not image as required. "+message);
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
			Vector3D pointOnGlens,
			Vector3D opticalAxisDirectionPos,
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

		// the geometric mean of |f-| and |f+|
		double meanF = calculateMean(focalLengthNeg, focalLengthPos);
		
		setParameters(
				opticalAxisDirectionPos,
				principalPoint,
				meanF,
				nodalPoint.getProductWith(1/meanF),
				focalLengthNeg/meanF,
				focalLengthPos/meanF
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


	/**
	 * Set the parameters such that this GlensHologram represents a homogeneous glens that images Q- to Q+.
	 * @param pointOnGlens	a point on the glens plane
	 * @param opticalAxisDirectionPos	normal to the glens plane, in the direction of the +ve optical axis
	 * @param QNeg
	 * @param QPos
	 */
	public void calculateAndSetParameters(
			Vector3D pointOnGlens,
			Vector3D opticalAxisDirectionPos,
			Vector3D QNeg, Vector3D QPos
		)
	{
		// normalise the optical axis direction, and set
		this.opticalAxisDirectionPos = opticalAxisDirectionPos.getNormalised();
		
		// in the limit where the focal lengths go to infinity, Q' = Q - a (N/f);
		// the nodal point is therefore in the direction (N/f) = (Q - Q')/a
		
		// first calculate the a components of QNeg and QPos
		double
			aNeg = Vector3D.scalarProduct(QNeg, this.opticalAxisDirectionPos),
			aPos = Vector3D.scalarProduct(QPos, this.opticalAxisDirectionPos);
		
		setParameters(
				opticalAxisDirectionPos,
				pointOnGlens,
				Vector3D.difference(QNeg, QPos).getProductWith(1/aNeg),	// nodal direction
				aNeg / aPos // fNegOverFPos
			);
		
		// TODO check this!
	}

	
	//
	// useful static methods
	//
	
	public static double calculateMean(double a, double b)
	{
		return Math.sqrt(Math.abs(a*b));
	}
	
	/**
	 * given f-/f+, and assuming that the mean of |f-| and |f+| is infinite, calculate |f-/F|
	 * @param fNegOverFPos
	 * @return
	 */
	public static double calculateAbsFocalLengthNegF(double fNegOverFPos)
	{
		// given f-/f+, and assuming that the mean of |f-| and |f+| is infinite, calculate |f-/F|
		// |f-/F|^2 = |f-|^2/|f- f+| = |f- / f+|
		return Math.sqrt(Math.abs(fNegOverFPos));
	}

	/**
	 * given f+/f-, and assuming that the mean of |f-| and |f+| is infinite, calculate |f+/F|
	 * @param fPosOverFNeg
	 * @return
	 */
	public static double calculateAbsFocalLengthPosF(double fPosOverFNeg)
	{
		// |f+/F|^2 = |f+|^2/|f- f+| = |f+ / f-|
		return Math.sqrt(Math.abs(fPosOverFNeg));
	}

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
		
		double focalLengthFO, focalLengthFI;	// dimensionless focal lengths in object and image space
		
		// the component of d in the direction of aHat
		double dA = Vector3D.scalarProduct(ray.getD(), getOpticalAxisDirectionPos());
		if(dA > 0)
		{
			// the ray is incident from -ve space, so image space is +ve space
			focalLengthFO = getFocalLengthNegF();	// f/F
			focalLengthFI = getFocalLengthPosF();	// f'/F
		}
		else
		{
			// the ray is incident from +ve space, so image space is -ve space
			focalLengthFO = getFocalLengthPosF();	// f/F
			focalLengthFI = getFocalLengthNegF();	// f'/F
		}
		
		Vector3D newRayDirection;
		
		// calculate normalised new light-ray direction
		if(Math.abs(getMeanF()) == Double.POSITIVE_INFINITY)
		{
			// F = \infty; the formula for the new ray direction simplifies to
			// -d (f/F)/(f'/F) + (N/F)*d_a/(f'/F)
			newRayDirection = Vector3D.sum(
					ray.getD().getProductWith(-focalLengthFO/focalLengthFI),	// -((f/F) / (f'/F)) d + ...
					getNodalPointF().getProductWith(dA/focalLengthFI)	// ... (N/F) * (d_a / (f'/F))
				);	// .getNormalised();	// finally, normalise the whole lot
		}
		else
		{
			// F is finite; use the full formula for the new ray direction,
			// -d (f/F)/(f'/F) + (N/F - S/F)*d_a/(f'/F)
			newRayDirection = Vector3D.sum(
					ray.getD().getProductWith(-focalLengthFO/focalLengthFI),	// -((f/F) / (f'/F)) d + ...
					Vector3D.difference(
							getNodalPointF(),	// (N/F - ...
							i.p.getProductWith(1/getMeanF())					// ... S/F) ...
							).getProductWith(dA/focalLengthFI)	// ... * (d_a / (f'/F))
					);	// .getNormalised();	// finally, normalise the whole lot
		}
		
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
		double objectSpaceFocalLengthF;	// f/F
		
		// is the object position in +ve or -ve space?
		switch(direction)
		{
		case POS2NEG:
			// the object position is in +ve space
			// and the image position is in -ve space
			objectSpaceFocalLengthF = getFocalLengthPosF();
			break;
		case NEG2POS:
		default:
			// the object position is in -ve space
			// and the image position is in +ve space
			objectSpaceFocalLengthF = getFocalLengthNegF();
			break;
		}
		
		// the distance of the object position from the glens plane
		double a = Vector3D.scalarProduct(
				Vector3D.difference(objectPosition, getPointOnGlens()),	// Q - P
				getOpticalAxisDirectionPos()	// aHat
			);
		
		return Vector3D.sum(
				objectPosition,
				Vector3D.difference(
						objectPosition.getProductWith(1/getMeanF()),	// (Q/F - ...
						getNodalPointF()	// ... - N/F) * ...
					).getProductWith(a/(objectSpaceFocalLengthF-a/getMeanF()))	// ... * (a/(f/F - a/F))
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
		return "GlensHologram [meanF=" + meanF + ", nodalPointF=" + nodalPointF + ", opticalAxisDirectionPos=" + opticalAxisDirectionPos + ", focalLengthNegF="
				+ focalLengthNegF + ", focalLengthPosF=" + focalLengthPosF + "]";
	}
}
