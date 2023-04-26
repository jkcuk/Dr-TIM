package optics.raytrace.imagingElements;

import optics.raytrace.core.Orientation;
import optics.raytrace.core.SurfaceProperty;
import optics.raytrace.core.SurfacePropertyPrimitive;
import optics.raytrace.surfaces.GCLAsWithApertures;
import optics.raytrace.surfaces.GCLAsWithApertures.GCLAsTransmissionCoefficientCalculationMethodType;
import optics.raytrace.utility.CoordinateSystems.GlobalOrLocalCoordinateSystemType;

import math.Vector3D;

/**
 * A collection of useful methods related to imaging with homogeneous planar surfaces, as described in Ref. [1].
 * Such surfaces can be approximately realised in the form of planar gCLAs [2,3].
 * 
 * [1] S. Oxburgh and J. Courtial, "Perfect imaging with planar interfaces", J. Opt. Soc. Am. A <b>30</b>, 2334-2338 (2013)
 * [2] A. C. Hamilton and J. Courtial, "Generalized refraction using lenslet arrays", J. Opt. A: Pure Appl. Opt. <b>11</b>, 065502 (2009)
 * [3] S. Oxburgh, C. D. White, G. Antoniou, and J. Courtial, "Law of refraction for generalised confocal lenslet arrays", Opt. Commun. <b>313</b>, 119-122 (2014)
 * 
 * @author johannes
 */
public class HomogeneousPlanarImagingSurface
implements ImagingElement
{
	private static final long serialVersionUID = 4369352767831580357L;

	// parameters that describe the surface
	private Vector3D pointOnPlane, aHat, uHat, vHat;
	private double eta, deltaU, deltaV;

	//
	// constructors
	//
	
	/**
	 * Create a homogeneous planar imaging surface.
	 * The parameters are described in Ref. [1].
	 * 
	 * [1] S. Oxburgh, C. D. White, G. Antoniou, and J. Courtial, "Law of refraction for generalised confocal lenslet arrays", Opt. Commun. <b>313</b>, 119-122 (2014)
	 * 
	 * @param pointOnPlane	a point on the surface
	 * @param a	the outwards-facing surface normal
	 * @param u	one of the span vectors of the plane
	 * @param v	the second span vector of the plane
	 * @param eta	eta, as defined for gCLAs
	 * @param deltaU	deltaU, as defined in [1]
	 * @param deltaV	deltaV, as defined in [1]
	 */
	public HomogeneousPlanarImagingSurface(
			Vector3D pointOnPlane,
			Vector3D a,
			Vector3D u,
			Vector3D v,
			double eta,
			double deltaU,
			double deltaV
		)
	{
		this.pointOnPlane = pointOnPlane;
		setAUV(a, u, v);
		this.eta = eta;
		this.deltaU = deltaU;
		this.deltaV = deltaV;
	}
	
	/**
	 * Constructor which calculates eta, deltaU and deltaV from the given conjugate positions.
	 * @param pointOnPlane
	 * @param a	the outwards-facing surface normal
	 * @param u
	 * @param v
	 * @param insideSpacePosition	inside-space position conjugate to <i>outsideSpacePosition</i>
	 * @param outsideSpacePosition	outside-space position conjugate to <i>insideSpacePosition</i>
	 */
	public HomogeneousPlanarImagingSurface(
			Vector3D pointOnPlane,
			Vector3D a,
			Vector3D u,
			Vector3D v,
			Vector3D insideSpacePosition,
			Vector3D outsideSpacePosition
		)
	{
		// copy the parameters that can simply be copied
		this.pointOnPlane = pointOnPlane;
		setAUV(a, u, v);
		
		// now calculate eta, deltaU and deltaV
		setParametersFromConjugatePositions(
				insideSpacePosition,
				outsideSpacePosition
			);
	}
	
	/**
	 * Constructor which calculates eta, deltaU and deltaV from the given conjugate positions.
	 * The directions of <i>u</i> and <i>v</i> are set to arbitrary normals to <i>a</i>.
	 * @param pointOnPlane
	 * @param a	the outwards-facing surface normal
	 * @param insideSpacePosition	inside-space position conjugate to <i>outsideSpacePosition</i>
	 * @param outsideSpacePosition	outside-space position conjugate to <i>insideSpacePosition</i>
	 */
	public HomogeneousPlanarImagingSurface(
			Vector3D pointOnPlane,
			Vector3D a,
			Vector3D insideSpacePosition,
			Vector3D outsideSpacePosition
		)
	{
		this.pointOnPlane = pointOnPlane;
		Vector3D u = Vector3D.getANormal(a);
		Vector3D v = Vector3D.crossProduct(a, u);
		setAUV(a, u, v);
		
		// now calculate eta, deltaU and deltaV
		setParametersFromConjugatePositions(
				insideSpacePosition,
				outsideSpacePosition
			);
	}
	
	
	
	//
	// getters & setters
	//
	
	public Vector3D getPointOnPlane() {
		return pointOnPlane;
	}

	public void setPointOnPlane(Vector3D pointOnPlane) {
		this.pointOnPlane = pointOnPlane;
	}
	
	public void setAUV(Vector3D a, Vector3D u, Vector3D v)
	{
		// normalise the surface normal
		aHat = a.getNormalised();
		
		// take the part of u that is perpendicular to aHat, and normalise it
		uHat = u.getPartPerpendicularTo(aHat).getNormalised();
		
		// take the part of v that is in the direction of aHat x uHat, and therefore perpendicular to uHat and nHat,
		// and normalise it
		vHat = v.getPartParallelTo(Vector3D.crossProduct(aHat, uHat)).getNormalised();
	}

	public Vector3D getuHat() {
		return uHat;
	}

//	public void setuHat(Vector3D uHat) {
//		this.uHat = uHat;
//	}

	public Vector3D getvHat() {
		return vHat;
	}

//	public void setvHat(Vector3D vHat) {
//		this.vHat = vHat;
//	}

	public Vector3D getaHat() {
		return aHat;
	}

//	public void setAHat(Vector3D nHat) {
//		this.aHat = aHat;
//	}

	public double getEta() {
		return eta;
	}

	public void setEta(double eta) {
		this.eta = eta;
	}

	public double getDeltaU() {
		return deltaU;
	}

	public void setDeltaU(double deltaU) {
		this.deltaU = deltaU;
	}

	public double getDeltaV() {
		return deltaV;
	}

	public void setDeltaV(double deltaV) {
		this.deltaV = deltaV;
	}
	
	
	//
	// imaging methods
	//
	
	/**
	 * calculates eta, deltaU and deltaV from the given conjugate positions.
	 * @param insideSpacePosition	inside-space position conjugate to <i>outsideSpacePosition</i>
	 * @param outsideSpacePosition	outside-space position conjugate to <i>insideSpacePosition</i>
	 */
	public void setParametersFromConjugatePositions(
			Vector3D insideSpacePosition,
			Vector3D outsideSpacePosition
		)
	{
		// calculate the <i>a</i> coordinate of P, i.e. the distance of P outside the plane
		double aP = Vector3D.scalarProduct(
				Vector3D.difference(insideSpacePosition, pointOnPlane),
				aHat
			);
		
		// gCLAs image according to the equation [2]
		// 	(P'_u, P'_v, P'_a) = (P_u, P_v, P_a) - aP (\delta_u, \delta_v, 1-\eta),
		// so
		//	\delta_u = (P_u - P'_u) / aP,
		// 	\delta_v = (P_v - P'_v) / aP,
		//	1-\eta = (P_a - P'_a) / aP, i.e. \eta = 1 - (P_a - P'_a) / aP

		// the vector P - P'
		Vector3D image2object = Vector3D.difference(insideSpacePosition, outsideSpacePosition);
		
		// calculate the gCLAs parameters
		deltaU = Vector3D.scalarProduct(image2object, uHat) / aP;
		deltaV = Vector3D.scalarProduct(image2object, vHat) / aP;
		eta = 1 - Vector3D.scalarProduct(image2object, aHat) / aP;
	}

	/**
	 * For a given object position, calculate the corresponding image position
	 * @param objectPosition
	 * @param orientation	Orientation.INWARDS or Orientation.OUTWARDS
	 * @return	the image position
	 */
	public Vector3D getImagePosition(Vector3D objectPosition, Orientation orientation)
	{
		double thisEta, thisDeltaU, thisDeltaV;
		Vector3D aHatForward;

		if(orientation == Orientation.OUTWARDS)
		{
			// the imaging is from inside space to outside space
			thisEta = getEta();
			thisDeltaU = getDeltaU();
			thisDeltaV = getDeltaV();
			aHatForward = aHat;
		}
		else
		{
			// the imaging is from outside space to inside space; calculate the parameters of the turned-around GCLAs
			// see geometry.tex
			thisEta = 1./getEta();
			thisDeltaU = getDeltaU() / getEta();
			thisDeltaV = getDeltaV() / getEta();
			aHatForward = aHat.getReverse();	// turn round aHat
		}

		// calculate the <i>a</i> coordinate of P
		double aP = Vector3D.scalarProduct(
				Vector3D.difference(objectPosition, pointOnPlane),
				aHatForward
			);

		// gCLAs image according to the equation [2]
		// 	(P'_u, P'_v, P'_n) = (P_u, P_v, P_n) - aP (\delta_u, \delta_v, 1-\eta),
		// or
		//	P' = P - aP \delta_u uHat - aP \delta_v vHat - aP (1-\eta) aHat
		return Vector3D.sum(
				objectPosition,
				uHat.getProductWith(-aP*thisDeltaU),
				vHat.getProductWith(-aP*thisDeltaV),
				aHatForward.getProductWith(-aP*(1-thisEta))
			);
	}
	
	/**
	 * For a given object position in inside space, calculate the corresponding image position in outward space
	 * @param objectPosition
	 * @return	the image position
	 */
	public Vector3D getImagePosition(Vector3D objectPosition)
	{
		return getImagePosition(objectPosition, Orientation.OUTWARDS);
	}

	
	/**
	 * Create a gCLAs surface property with the same properties as this surface
	 * @return	the GeneralisedConfocalLensletArrays surface property
	 */
	public GCLAsWithApertures toGCLAs(
			double transmissionCoefficient,
			GCLAsTransmissionCoefficientCalculationMethodType transmissionCoefficientMethod,
			// boolean calculateGeometricalTransmissionCoefficient,
			boolean shadowThrowing
		)
	{
		return new GCLAsWithApertures(
			aHat,
			uHat,
			vHat,
			eta,	// etaU
			eta,	// etaV
			deltaU,
			deltaV,
			GlobalOrLocalCoordinateSystemType.GLOBAL_BASIS,	// basis
			transmissionCoefficient,
			// calculateGeometricalTransmissionCoefficient,
			transmissionCoefficientMethod,
			shadowThrowing,
			2e-3,	// pixelSideLength
			564e-9,	// lambda; 564nm is the wavelength at which the human eye is most sensitive -- see http://hypertextbook.com/facts/2007/SusanZhao.shtml
			false,	// simulateDiffractiveBlur
			false	// simulateRayOffset
		);
	}

	public GCLAsWithApertures toGCLAs()
	{
		return toGCLAs(
			SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT,	// transmissionCoefficient
			// true,	// calculateGeometricalTransmissionCoefficient
			GCLAsTransmissionCoefficientCalculationMethodType.GEOMETRIC_BEST,
			true	// shadowThrowing
		);
	}
	
	@Override
	public SurfaceProperty toSurfaceProperty(
			double imagingSurfaceTransmissionCoefficient,
			boolean imagingSurfaceShadowThrowing
		)
	{
		return toGCLAs(imagingSurfaceTransmissionCoefficient, GCLAsTransmissionCoefficientCalculationMethodType.CONSTANT, imagingSurfaceShadowThrowing);
	}
	
	
	@Override
	public String toString()
	{
		return "[Homogeneous planar imaging surface" +
			", point on plane = " + pointOnPlane +
			", aHat = " + aHat +
			", uHat = " + uHat +
			", vHat = " + vHat +
			", eta = " + eta +
			", deltaU = " + deltaU +
			", deltaV = " + deltaV +
			"]";
	}
	

//	//
//	// static methods that calculate imaging for homogeneous surfaces
//	//
//
//	/**
//	 * @param objectPosition
//	 * @param pointOnPlane	a point on the planar gCLAs
//	 * @param u	first vector that spans plane of gCLAs
//	 * @param v	second vector that spans plane of gCLAs
//	 * @param n	outwards-facing normal to the plane of the gCLAs
//	 * @param deltaU	standard gCLAs parameter
//	 * @param deltaV	standard gCLAs parameter
//	 * @param eta	standard gCLAs parameter
//	 * @return	the position of the image of a point light source at the object position, formed by planar gCLAs
//	 */
//	public static Vector3D getImagePosition(Vector3D objectPosition, Vector3D pointOnPlane, Vector3D u, Vector3D v, Vector3D n, double deltaU, double deltaV, double eta)
//	{
//		Vector3D uHat, vHat, nHat;
//		
//		// normalise the surface normal
//		nHat = n.getNormalised();
//		
//		// take the part of u that is perpendicular to nHat, and normalise it
//		uHat = u.getPartPerpendicularTo(nHat).getNormalised();
//		
//		// take the part of v that is in the direction of nHat x uHat, and therefore perpendicular to uHat and nHat,
//		// and normalise it
//		vHat = v.getPartParallelTo(Vector3D.crossProduct(nHat, uHat)).getNormalised();
//				
//		// calculate the distance of P from the plane, d
//		double d = Vector3D.scalarProduct(
//				Vector3D.difference(objectPosition, pointOnPlane),
//				nHat
//			);
//
//		// gCLAs image according to the equation [2]
//		// 	(P'_u, P'_v, P'_n) = (P_u, P_v, P_n) - d (\delta_u, \delta_v, 1-\eta),
//		// or
//		//	P' - P - d \delta_u uHat - d \delta_v vHat - d (1-\eta) nHat
//		return Vector3D.sum(
//				objectPosition,
//				uHat.getProductWith(-d*deltaU),
//				vHat.getProductWith(-d*deltaV),
//				nHat.getProductWith(-d*(1-eta))
//			);
//	}
//	
//	/**
//	 * @param objectPosition
//	 * @param imagePosition
//	 * @param pointOnPlane	a point on the plane
//	 * @param u	one of the vectors that spans the plane
//	 * @param v	the other vector that spans the plane (note that u and v should be perpendicular)
//	 * @param n	the outwards-facing surface normal
//	 * @return	the gCLAs surface property which, when applied to the plane, images the object position to the image position
//	 */
//	public static GeneralisedConfocalLensletArrays getImagingHomogeneousSurfacePropertyForPlane(Vector3D objectPosition, Vector3D imagePosition, Vector3D pointOnPlane, Vector3D u, Vector3D v, Vector3D n)
//	{
//		Vector3D uHat, vHat, nHat;
//		
//		// normalise the surface normal
//		nHat = n.getNormalised();
//		
//		// take the part of u that is perpendicular to nHat, and normalise it
//		uHat = u.getPartPerpendicularTo(nHat).getNormalised();
//		
//		// take the part of v that is in the direction of nHat x uHat, and therefore perpendicular to uHat and nHat,
//		// and normalise it
//		vHat = v.getPartParallelTo(Vector3D.crossProduct(nHat, uHat)).getNormalised();
//				
//		// calculate the distance of P from the plane, d
//		double d = Vector3D.scalarProduct(
//				Vector3D.difference(objectPosition, pointOnPlane),
//				nHat
//			);
//		
//		// gCLAs image according to the equation [2]
//		// 	(P'_u, P'_v, P'_n) = (P_u, P_v, P_n) - d (\delta_u, \delta_v, 1-\eta),
//		// so
//		//	\delta_u = (P_u - P'_u) / d,
//		// 	\delta_v = (P_v - P'_v) / d,
//		//	1-\eta = (P_n - P'_n) / d, i.e. \eta = 1 - (P_n - P'_n) / d
//
//		// the vector P - P'
//		Vector3D image2object = Vector3D.difference(objectPosition, imagePosition);
//		
//		// calculate the gCLAs parameters
//		double deltaU = Vector3D.scalarProduct(image2object, uHat) / d;
//		double deltaV = Vector3D.scalarProduct(image2object, vHat) / d;
//		double eta = 1 - Vector3D.scalarProduct(image2object, nHat) / d;
//		
//		// return the surface property
//		return new GeneralisedConfocalLensletArrays(
//				nHat,	// aHat
//				uHat,
//				vHat,
//				eta,	// etaU
//				eta,	// etaV
//				deltaU,
//				deltaV,
//				GlobalOrLocalCoordinateSystemType.GLOBAL_BASIS,	// basis
//				SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT,	// transmissionCoefficient
//				true	// shadowThrowing
//			);
//	}
}
