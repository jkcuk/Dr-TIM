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
 * If this surface property is <i>not<i> associated with such a plane, then it bends the light
 * rays such that parallel light rays intersect in the focal plane, and any light rays through N pass through undeviated.
 * (Unlike in the case of a thin hologram, the direction change is independent of wavelength.)
 * The centre of the lens is given directly (and not in terms of the surface's surface-coordinate values,
 * like in the @see IdealThinLensSurfaceSurfaceCoordinates class).
 * 
 * Everything is defined in dimensionless coordinates relative to a number g, which must be finite in inhomogeneous glenses
 * (which have finite focal lengths) and infinite in homogeneous glenses (which have infinite focal lengths).
 * 
 * [1] J. Courtial, "Geometric limits to geometric optical imaging with infinite, planar, non-absorbing sheets", Opt. Commun. <b>282</b>, 2480-2483 (2009)
 * [2] A. C. Hamilton and J. Courtial, "Generalized refraction using lenslet arrays", J. Opt. A: Pure Appl. Opt. <b>11</b>, 065502 (2009)
 * [3] S. Oxburgh, C. D. White, G. Antoniou, and J. Courtial, "Law of refraction for generalised confocal lenslet arrays", Opt. Commun. <b>313</b>, 119-122 (2014)
 * [4] S. Oxburgh and J. Courtial, "Perfect imaging with planar interfaces", J. Opt. Soc. Am. A 30, 2334-2338 (2013)
 * 
 * @author Johannes Courtial
 */
public class GlensSurface extends SurfacePropertyPrimitive
{
	private static final long serialVersionUID = 134597862435L;
	
	// the coordinates of the nodal point and the focal lengths are defined relative to this scaling factor
	protected double g;	// scaling factor

	// the dimensionless coordinates N_g = N/g of the nodal point, N (any light rays incident on N pass through undeviated; in a lens, N=P)
	protected Vector3D nodalPointG;
	
	// a point in the glens plane
	protected Vector3D pointOnGlens;
	
	// a unit vector in the direction of the optical axis, pointing in the direction of +ve space
	protected Vector3D opticalAxisDirectionPos;
	
	// dimensionless focal lengths:
	//	focalLengthPosG (= f^+ / g): the axial coordinate of the +ve-space focal point
	//	focalLengthNegG (= f^- / g): the axial coordinate of the +ve-space focal point
	protected double
		focalLengthNegG, focalLengthPosG;	// focal lengths
	
	/**
	 * Creates an instance of the surface property that refracts light like a glens.
	 * All parameters are explicitly given exactly as they are being stored.
	 * 
	 * @param opticalAxisDirectionPos	the vector aHat
	 * @param g	scaling factor
	 * @param nodalPointG	the dimensionless coordinates N/g of the point, N
	 * @param focalLengthNegG	the dimensionless focal length in -ve space, f-/g
	 * @param focalLengthPosG	the dimensionless focal length in +ve space, f+/g
	 * @param transmissionCoefficient
	 */
	public GlensSurface(
			Vector3D opticalAxisDirectionPos,
			Vector3D pointOnGlens,
			double g,
			Vector3D nodalPointG,
			double focalLengthNegG,
			double focalLengthPosG,
			double transmissionCoefficient,
			boolean shadowThrowing
		) 
	{
		super(transmissionCoefficient, shadowThrowing);
		setParameters(
				opticalAxisDirectionPos, 
				pointOnGlens,
				g,
				nodalPointG,
				focalLengthNegG,
				focalLengthPosG
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
	public GlensSurface(
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
	public GlensSurface(
			Vector3D opticalAxisDirectionPos,
			Vector3D pointOnGlens,
			Vector3D nodalDirection,
			double fNegOverFPos,
			double transmissionCoefficient,
			boolean shadowThrowing
		) 
	{
		super(transmissionCoefficient, shadowThrowing);
		
		setParametersForHomogeneousGlens(
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
	public GlensSurface(
			double transmissionCoefficient,
			boolean shadowThrowing
		)
	{
		super(transmissionCoefficient, shadowThrowing);
		setDefaultParameters();
	}

	
	/**
	 * Constructor that creates an instance of a GlensHologram that images Q- to Q+ and R- to R+.
	 * (no longer true: If this is not possible, then it finds approximate parameters.)
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
	public GlensSurface(
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
			setParametersUsingTwoConjugatePairs(pointOnGlens, opticalAxisDirectionPos, QNeg, QPos, RNeg, RPos);
		} catch (RayTraceException e)
		{
			System.out.flush();
			// System.err.println("GlensHologram::GlensHologram: " + e.getMessage());
			System.err.println("GlensHologram::GlensHologram: pointOnGlens="+pointOnGlens
					+ ", opticalAxisDirectionPos="+opticalAxisDirectionPos
					+ ", Q-="+QNeg
					+ ", Q+="+QPos
					+ ", R-="+RNeg
					+ ", R+="+RPos
			);
			System.err.flush();
			e.printStackTrace();
			// System.exit(-1);
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
	public GlensSurface(
			Vector3D pointOnGlens,
			Vector3D opticalAxisDirectionPos,
			Vector3D nodalPoint,
			Vector3D QNeg, Vector3D QPos,
			double transmissionCoefficient,
			boolean shadowThrowing
		)
	{
		super(transmissionCoefficient, shadowThrowing);
		
		try
		{
			setParametersForInhomogeneousGlensUsingOneConjugatePair(
					pointOnGlens,
					opticalAxisDirectionPos,
					nodalPoint,
					QNeg, QPos
				);
		} catch (RayTraceException e)
		{
			System.out.flush();
			// System.err.println("GlensHologram::GlensHologram: " + e.getMessage());
			System.err.println("GlensHologram::GlensHologram: pointOnGlens="+pointOnGlens
					+ ", opticalAxisDirectionPos="+opticalAxisDirectionPos
					+ ", nodalPoint="+nodalPoint
					+ ", Q-="+QNeg
					+ ", Q+="+QPos
			);
			System.err.flush();
			e.printStackTrace();
			// System.exit(-1);
		}
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
	public GlensSurface(
			Vector3D pointOnGlens,
			Vector3D opticalAxisDirectionPos,
			Vector3D QNeg, Vector3D QPos,
			double transmissionCoefficient,
			boolean shadowThrowing
		)
	{
		super(transmissionCoefficient, shadowThrowing);
		
		setParametersForHomogeneousGlensUsingOneConjugatePair(
			pointOnGlens,
			opticalAxisDirectionPos,
			QNeg, QPos
		);
	}


	/**
	 * Make a clone of the original GlensHologram.
	 * @param original
	 */
	public GlensSurface(GlensSurface original)
	{
		this(
				original.getOpticalAxisDirectionPos(),
				original.getPointOnGlens(),
				original.getG(),
				original.getNodalPointG(),
				original.getFocalLengthNegG(),
				original.getFocalLengthPosG(),
				original.getTransmissionCoefficient(),
				original.isShadowThrowing()
			);		
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public GlensSurface clone()
	{
		return new GlensSurface(this);
	}
	
	// setters and getters
	
	/**
	 * make this a homogeneous, transparent, glens
	 */
	public void setDefaultParameters()
	{
		setParameters(
				new Vector3D(0, 0, 1),	// opticalAxisDirectionPos
				new Vector3D(0, 0, 0),	// point on glens
				Double.POSITIVE_INFINITY,	// g = \infty
				new Vector3D(0, 0, 0),	//nodalPointG
				-1,	// focalLengthNegG
				1	// focalLengthPosG
				);
	}

	/**
	 * Set all parameters of the GlensHologram directly;
	 * the optical axis direction gets normalised
	 * 
	 * @param opticalAxisDirectionPos
	 * @param pointOnGlens
	 * @param g
	 * @param nodalPointG
	 * @param focalLengthNegG
	 * @param focalLengthPosG
	 */
	public void setParameters(
			Vector3D opticalAxisDirectionPos,
			Vector3D pointOnGlens,
			double g,
			Vector3D nodalPointG,
			double focalLengthNegG,
			double focalLengthPosG
		)
	{
//		System.out.println("GlensHologram::setParameters: nodalPoint="+nodalPoint+
//				", optical axis direction (+) = "+opticalAxisDirectionPos+
//				", f- = "+focalLengthNeg+
//				", f+ = "+focalLengthPos);
		
		this.opticalAxisDirectionPos = opticalAxisDirectionPos.getNormalised();
		this.pointOnGlens = pointOnGlens;
		this.g = g;
		this.nodalPointG = nodalPointG;
		this.focalLengthNegG = focalLengthNegG;
		this.focalLengthPosG = focalLengthPosG;
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
		// choose g=1
		setParameters(
				opticalAxisDirectionPos,
				principalPoint,	// pointOnGlens
				1,	// g
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
		// for simplicity, choose g=1
		setParameters(
				opticalAxisDirectionPos,
				calculatePrincipalPoint(opticalAxisDirectionPos, nodalPoint, focalLengthNeg, focalLengthPos),	// point on glens
				1,	// g,
				nodalPoint,	// N/g,
				focalLengthNeg,	// f-/g
				focalLengthPos	// f+/g
			);
	}
	
	/**
	 * Set the parameters in a way suitable for homogeneous glenses.
	 * 
	 * @param opticalAxisDirectionPos	optical-axis direction, not necessarily normalised
	 * @param pointOnGlens
	 * @param nodalDirection	N (arbitrarily normalised)
	 * @param fNegOverFPos	f-/f+
	 */
	public void setParametersForHomogeneousGlens(
			Vector3D opticalAxisDirectionPos,
			Vector3D pointOnGlens,
			Vector3D nodalDirection,
			double fNegOverFPos
		) 
	{
		// the nodal point is a distance (f- + f+) from the glens, so it is at
		// P + (f- + f+) nodalDirection/nodalDirection_a;
		// N/g is therefore
		// N/g = P/g + (f-/g + f+/g) nodalDirection/nodalDirection_a; in the limit g->\infty, this becomes
		// N/g = (f-/g + f+/g) nodalDirection/nodalDirection_a

		double
			focalLengthNegG = fNegOverFPos,
			focalLengthPosG = 1;
		
		setParameters(
				opticalAxisDirectionPos,
				pointOnGlens,
				Double.POSITIVE_INFINITY,	// g,
				nodalDirection.getProductWith(
						(focalLengthNegG+focalLengthPosG)/	// (f-/g + f+/g) / ...
						Vector3D.scalarProduct(nodalDirection, opticalAxisDirectionPos.getNormalised())	// ... / nodalDirection_a
					), // N/g,
				focalLengthNegG,	// f-/g,
				focalLengthPosG	// f+/g
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
	
	public double getG() {
		return g;
	}
	
	public Vector3D getNodalPointG() {
		return nodalPointG;
	}

	public double getFocalLengthNegG() {
		return focalLengthNegG;
	}

	public double getFocalLengthPosG() {
		return focalLengthPosG;
	}
	
	//
	// other cardinal distances
	//
	
	public double getNodalDistance()
	{
		return g*(focalLengthNegG + focalLengthPosG);
	}

	//
	// the cardinal points
	//
	
	/**
	 * @return	a vector with the Cartesian coordinates of the focal point in -ve space
	 */
	public Vector3D getFocalPointNeg()
	{
		// F- = P + f- * aHat = N - (f+ + f-) aHat + f- * aHat = N - f+ aHat = g (N/g - f+/g aHat)
		return Vector3D.difference(
				getNodalPointG(),	// (N/g - ...
				getOpticalAxisDirectionPos().getProductWith(getFocalLengthPosG())	// ... - f+/g aHat) * ...
			).getProductWith(getG());	// ... * g
	}

	/**
	 * @return	a vector with the Cartesian coordinates of the focal point in +ve space
	 */
	public Vector3D getFocalPointPos()
	{
		// F+ = P + f+ * aHat = N - (f+ + f-) aHat + f+ * aHat = N - f- aHat = g (N/g - f-/g aHat)
		return Vector3D.difference(
				getNodalPointG(),	// (N/g - ...
				getOpticalAxisDirectionPos().getProductWith(getFocalLengthNegG())	// ... - f-/g aHat) * ...
			).getProductWith(getG());	// ... * g
	}
	
	
	//
	// methods for calculating the parameters that correspond to given imaging characteristics
	//
		
	/**
	 * Set the parameters such that this glens hologram images Q- to Q+ and R- to R+.
	 * This might require an inhomogeneous glens, a homogeneous glens, or it might be impossible.
	 * (no longer true: If this is not possible, then it finds approximate parameters.)
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
	public void setParametersUsingTwoConjugatePairs(
			Vector3D pointOnGlens,
			Vector3D opticalAxisDirectionPos,
			Vector3D QNeg, Vector3D QPos,
			Vector3D RNeg, Vector3D RPos
		) throws RayTraceException
	{
		// normalise opticalAxisDirectionPos
		opticalAxisDirectionPos = opticalAxisDirectionPos.getNormalised();
		
		// diagnostic information
		String info = "";

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

			// System.out.println("GlensHologram::setParametersUsingTwoConjugatePairs: glens is homogeneous");
			info = info + "Glens is homogeneous. ";

			// a homogeneous glens can image as required only if the straight line through QPos and RPos intersects
			// the glens plane at the same position where the straight line through QNeg and RNeg intersects it [1]

			Vector3D
			// intersection point between the straight line through QPos and RPos and the glens plane
			iPos = Geometry.uniqueLinePlaneIntersection(QPos, Vector3D.difference(RPos, QPos), pointOnGlens, opticalAxisDirectionPos),
			// intersection point between the straight line through QNeg and RNeg and the glens plane
			iNeg = Geometry.uniqueLinePlaneIntersection(QNeg, Vector3D.difference(RNeg, QNeg), pointOnGlens, opticalAxisDirectionPos);

			// is the line through QPos and RPos parallel to the glens plane?
			if(iPos == null)
			{
				// the line through QPos and RPos is parallel to the glens plane

				info = info + "The line through QPos and RPos is parallel to the glens plane (at a=" +
						Vector3D.scalarProduct(Vector3D.difference(QPos, pointOnGlens), opticalAxisDirectionPos) + "). ";

				// is the line through QNeg and RNeg also parallel to the glens plane?
				if(iNeg == null)
				{
					// both lines are parallel to the glens plane

					info = info + "The line through QNeg and RNeg is parallel to the glens plane (at a=" +
						Vector3D.scalarProduct(Vector3D.difference(QNeg, pointOnGlens), opticalAxisDirectionPos) + "). ";

					// are they parallel?  check if the length of their cross product is 0
					if(Vector3D.crossProduct(
							Vector3D.difference(RPos, QPos).getNormalised(),
							Vector3D.difference(RNeg, QNeg).getNormalised()
							).getModSquared() > MyMath.EPSILON)
					{
						// the length of the cross product of the normalised directions of the two lines is not 0, so they are not parallel
						// the required glens does not exist

						setDefaultParameters();

						// throw an exception
						throw new RayTraceException(info + "Cannot find homogeneous glens that images as required.  Returning transparent glens.");
					}
				}
			}
			else
			{
				// the line through QPos and RPos is not parallel to the glens plane

				info = info + "The line through QPos and RPos is not parallel to the glens plane. ";

				if(iNeg == null)
				{
					info = info + "The line through QNeg and RNeg is parallel to the glens plane. ";
				}

				// are the two intersection points iPos and iNeg the same?
				if((iNeg == null) || (Vector3D.difference(iPos, iNeg).getModSquared() > MyMath.EPSILON))
				{
					// the two intersection points are not the same;
					// the required glens does not exist

					setDefaultParameters();

					// throw an exception
					throw new RayTraceException(info + "Cannot find homogeneous glens that images as required.  Returning transparent glens.");
				}
			}

			// the required glens should exist
			setParametersForHomogeneousGlensUsingOneConjugatePair(
					pointOnGlens,
					opticalAxisDirectionPos,
					QNeg, QPos
				);
		}
		else
		{
			// the vectors (QPos - QNeg) and (RPos - RNeg) are *not* parallel, which means that the glens that performs the required imaging ---
			// if there exists one --- is inhomogeneous, so define it as such

			info = info + "Glens is inhomogeneous. ";

			// the nodal point is the position where the lines between QNeg and QPos and between RNeg and RPos intersect
			// (if the lines don't intersect, a RayTraceException is being thrown)
			Vector3D nodalPoint = Geometry.lineLineIntersection(QNeg, Vector3D.difference(QPos, QNeg), RNeg, Vector3D.difference(RPos, RNeg));

			info = info + "Nodal point = " + nodalPoint + ". ";

			setParametersForInhomogeneousGlensUsingOneConjugatePair(
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
			if(errQ > MyMath.EPSILON) info = info + "The image of Q- is "+QNegDash+", not Q+ (="+QPos+"). ";
			if(errR > MyMath.EPSILON) info = info + "The image of R- is "+RNegDash+", not R+ (="+RPos+"). ";

			// throw an exception
			throw new RayTraceException("The GlensHologram does not image as required. "+ info);
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
	 * @throws RayTraceException 
	 */
	public void setParametersForInhomogeneousGlensUsingOneConjugatePair(
			Vector3D pointOnGlens,
			Vector3D opticalAxisDirectionPos,
			Vector3D nodalPoint,
			Vector3D QNeg, Vector3D QPos
		) throws RayTraceException
	{
		// normalise the optical axis direction, and set
		this.opticalAxisDirectionPos = opticalAxisDirectionPos.getNormalised();
		
		// the nodal point is at a finite distance
		// the principal point is the orthographic projection of N into the glens plane
		Vector3D principalPoint = Geometry.orthographicProjection(nodalPoint, pointOnGlens, this.opticalAxisDirectionPos);

		// calculate the axial component of QNeg and QPos, aNeg and aPos
		// a- = (Q- - P).aHat
		double aNeg = Vector3D.scalarProduct(Vector3D.difference(QNeg, principalPoint), this.opticalAxisDirectionPos);
		
		// a+ = (Q+ - P).aHat
		double aPos = Vector3D.scalarProduct(Vector3D.difference(QPos, principalPoint), this.opticalAxisDirectionPos);
		
		// calculate the nodal distance, n = (N - P).aHat
		double n = Vector3D.scalarProduct(Vector3D.difference(nodalPoint, principalPoint), this.opticalAxisDirectionPos);
		
		// calculate the +ve focal length
		// solve the glens equation, f+/a+ + f-/a- = 1, together with the equation f+ + f- = n, to get
		// f+ = a+ (n - a-) / (a+ - a-)
		double focalLengthPos = aPos*(n-aNeg)/(aPos-aNeg);
		
		// similarly, calculate the -ve focal length
		// f- = a- (n - a+) / (a- - a+)
		double focalLengthNeg = aNeg*(n-aPos)/(aNeg-aPos);
		
//		// the +ve focal point is the point where the optical axis gets closest to the
//		// line from the orthographic projection into the glens plane of QNeg to QPos
//
//		// orthographic projection into the glens plane of QNeg
//		Vector3D QNegP = Geometry.orthographicProjection(QNeg, pointOnGlens, opticalAxisDirectionPos);
//
//		// +ve focal point
//		Vector3D FPos = Geometry.lineLineIntersection(	// pointOfClosestApproach(
//				principalPoint, opticalAxisDirectionPos,	// point on optical axis and its direction
//				QNegP, Vector3D.difference(QPos, QNegP)	// point on line from QNegP to QPos and its direction
//				// true
//				);
//
//		// +ve focal length
//		double focalLengthPos = Vector3D.scalarProduct(Vector3D.difference(FPos, principalPoint), opticalAxisDirectionPos);
//
//		// calculate the -ve focal length
//		// the -ve focal point is the point where the optical axis gets closest to the
//		// line from the orthographic projection into the glens plane of QPos to QNeg
//
//		// orthographic projection into the glens plane of QPos
//		Vector3D QPosP = Geometry.orthographicProjection(QPos, pointOnGlens, opticalAxisDirectionPos);
//
//		// +ve focal point
//		Vector3D FNeg = Geometry.lineLineIntersection(	// pointOfClosestApproach(
//				principalPoint, opticalAxisDirectionPos,	// point on optical axis and its direction
//				QPosP, Vector3D.difference(QNeg, QPosP)	// point on line from QPosP to QNeg and its direction
//				// true
//			);
//
//		// +ve focal length
//		double focalLengthNeg = Vector3D.scalarProduct(Vector3D.difference(FNeg, principalPoint), opticalAxisDirectionPos);

		double g = 1;
		
		setParameters(
				opticalAxisDirectionPos,
				pointOnGlens,
				g,
				nodalPoint.getProductWith(1/g),
				focalLengthNeg/g,
				focalLengthPos/g
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
	 * @param opticalAxisDirectionPos	normal to the glens plane, in the direction of the +ve optical axis; not necessarily normalised
	 * @param QNeg
	 * @param QPos
	 */
	public void setParametersForHomogeneousGlensUsingOneConjugatePair(
			Vector3D pointOnGlens,
			Vector3D opticalAxisDirectionPos,
			Vector3D QNeg, Vector3D QPos
		)
	{
		// normalise the optical axis direction, and set
		opticalAxisDirectionPos = opticalAxisDirectionPos.getNormalised();
		
		// in the limit where the focal lengths go to infinity, Q' = Q - a (N/f) = Q - a (N/g)/(f/g);
		// the nodal point is therefore in the direction (N/f) = (Q - Q')/a
		
		// first calculate the a components of QNeg and QPos
		double
			aNeg = Vector3D.scalarProduct(Vector3D.difference(QNeg, pointOnGlens), opticalAxisDirectionPos),
			aPos = Vector3D.scalarProduct(Vector3D.difference(QPos, pointOnGlens), opticalAxisDirectionPos);
		
		Vector3D
			nodalDirection = Vector3D.difference(QNeg, QPos).getProductWith(1/aNeg);
		
		// normalise the nodal direction such that (N/g)_a = 1
		nodalDirection = nodalDirection.getProductWith(1/Vector3D.scalarProduct(nodalDirection, opticalAxisDirectionPos));
		
		// then the component in the a direction of the mapping equation becomes
		// a' = a - a/(f/g), so (a-a')/a = 1/(f/g), so f/g = a/(a-a')
		setParameters(
				opticalAxisDirectionPos,
				pointOnGlens,
				Double.POSITIVE_INFINITY,	// g
				nodalDirection,	// N/g
				aNeg/(aNeg-aPos),
				aPos/(aPos-aNeg)
			);
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
				getOpticalAxisDirectionPos().getWithLength(focalLengthNeg + focalLengthPos)	// ... - (f- + f+) aHat
			);
	}


	/**
	 * @param incidentLightRayDirection
	 * @param intersectionPoint
	 * @return	the direction of the light ray after transmission through the glens
	 */
	public Vector3D getRefractedLightRayDirection(Vector3D incidentLightRayDirection, Vector3D intersectionPoint)
	{
		// calculate direction d' of deflected ray;
		// d' = d + (d_a / f_i) (N - S), where
		// 	d is the incident direction
		// 	d_a is the component of d in the a direction
		// 	f_i is the focal length in image space
		//	N is the nodal point
		//	S is the point where the ray intersects the glens
		// see glens paper glens.pdf

		double focalLengthGO, focalLengthGI;	// dimensionless focal lengths in object and image space
		
		// the component of d in the direction of aHat
		double dA = Vector3D.scalarProduct(incidentLightRayDirection, getOpticalAxisDirectionPos());
		if(dA > 0)
		{
			// the ray is incident from -ve space, so image space is +ve space
			focalLengthGO = getFocalLengthNegG();	// f/g
			focalLengthGI = getFocalLengthPosG();	// f'/g
		}
		else
		{
			// the ray is incident from +ve space, so image space is -ve space
			focalLengthGO = getFocalLengthPosG();	// f/g
			focalLengthGI = getFocalLengthNegG();	// f'/g
		}
		
		Vector3D outgoingLightRayDirection;
		
		// calculate new light-ray direction
		if(Math.abs(getG()) == Double.POSITIVE_INFINITY)
		{
			// g = \infty; the formula for the new ray direction simplifies to
			// -d (f/g)/(f'/g) + (N/g)*d_a/(f'/g)
			outgoingLightRayDirection = Vector3D.sum(
					incidentLightRayDirection.getProductWith(-focalLengthGO/focalLengthGI),	// -((f/g) / (f'/g)) d + ...
					getNodalPointG().getProductWith(dA/focalLengthGI)	// ... (N/g) * (d_a / (f'/g))
				);	// .getNormalised();	// finally, normalise the whole lot
		}
		else
		{
			// g is finite; use the full formula for the new ray direction,
			// -d (f/g)/(f'/g) + (N/g - S/g)*d_a/(f'/g)
			outgoingLightRayDirection = Vector3D.sum(
					incidentLightRayDirection.getProductWith(-focalLengthGO/focalLengthGI),	// -((f/g) / (f'/g)) d + ...
					Vector3D.difference(
							getNodalPointG(),	// (N/g - ...
							intersectionPoint.getProductWith(1/getG())					// ... S/g) ...
					).getProductWith(dA/focalLengthGI)	// ... * (d_a / (f'/g))
				);	// .getNormalised();	// finally, normalise the whole lot
		}
		
//		// check if the outgoing ray leaves in the direction of the other side of the hologram
//		if(	Vector3D.scalarProduct(incidentLightRayDirection, opticalAxisDirectionPos)*
//			Vector3D.scalarProduct(outgoingLightRayDirection, opticalAxisDirectionPos) < 0 )
//		{
//			try {
//				throw new RayTraceException("The outgoing ray leaves in the direction of the same side as the incident light ray.");
//			} catch (RayTraceException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
		
		return outgoingLightRayDirection;
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
//		if(i.o.getDescription().endsWith("diagonal glens"))
//		{
//		System.out.println("GlensHologram:getColour: traceLevel="+traceLevel+
//				", intersected scene object = "+i.o.getDescription()+
//				", ray = "+ray+
//				", GlensHologram = "+toString());
//		System.out.println("refracted light-ray direction = "+getRefractedLightRayDirection(ray.getD(), i.p));
//		}
		
		if (traceLevel <= 0) return DoubleColour.BLACK;
						
		// launch a new ray from here
		return scene.getColourAvoidingOrigin(
			ray.getBranchRay(i.p, getRefractedLightRayDirection(ray.getD(), i.p), i.t),
			i.o,
			l,
			scene,
			traceLevel-1,
			raytraceExceptionHandler
		).multiply(
			// ((getTransmissionCoefficient() < 0.8) && (i.getRayOrientation(ray) == Orientation.INWARDS))?0.96:
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
	 * @param direction	POS2NEG (+ve space to -ve space) or NEG2POS (-ve space to +ve space)
	 * @return	the image position
	 */
	public Vector3D getImagePosition(Vector3D objectPosition, ImagingDirection direction)
	{
		double objectSpaceFocalLengthG;	// f/g
		
		// is the object position in +ve or -ve space?
		switch(direction)
		{
		case POS2NEG:
			// the object position is in +ve space
			// and the image position is in -ve space
			objectSpaceFocalLengthG = getFocalLengthPosG();
			break;
		case NEG2POS:
		default:
			// the object position is in -ve space
			// and the image position is in +ve space
			objectSpaceFocalLengthG = getFocalLengthNegG();
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
						objectPosition.getProductWith(1/getG()),	// (Q/g - ...
						getNodalPointG()	// ... - N/g) * ...
					).getProductWith(a/(objectSpaceFocalLengthG-a/getG()))	// ... * (a/(f/g - a/g))
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
		return "GlensHologram [g=" + g + ", nodalPointG=" + nodalPointG + ", opticalAxisDirectionPos=" + opticalAxisDirectionPos + ", focalLengthNegG="
				+ focalLengthNegG + ", focalLengthPosG=" + focalLengthPosG + "]";
	}
}
