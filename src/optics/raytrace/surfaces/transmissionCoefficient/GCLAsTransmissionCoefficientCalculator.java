package optics.raytrace.surfaces.transmissionCoefficient;

import math.Vector3D;
import optics.raytrace.core.Orientation;
import optics.raytrace.core.RaySceneObjectIntersection;
import optics.raytrace.surfaces.GeneralisedConfocalLensletArrays;


/**
 * @author johannes
 *
 * This class provides methods that facilitate the calculation of the transmission coefficient of GCLAs.
 * Each lenslet is assumed to have a rectangular aperture with its side aligned with the <u> and <v> directions.
 * 
 * References:
 * [1] A. C. Hamilton and J. Courtial, "Generalized refraction using lenslet arrays", J. Opt. A: Pure Appl. Opt. 11, 065502 (2009)
 * [2] E. N. Cowie and J. Courtial, "Engineering the field of view of generalised confocal lenslet arrays", in preparation (2017)
 */
public class GCLAsTransmissionCoefficientCalculator extends TransmissionCoefficientCalculator
{
	private static final long serialVersionUID = 6228825873375447651L;

	/**
	 * the GCLAs this applies to;
	 * gives access to unit vectors in <a>, <u> and <v> directions and parameters <eta_u>, <eta_v>, <delta_u>, <delta_v>
	 */
	private GeneralisedConfocalLensletArrays generalisedConfocalLensletArrays;

	/**
	 * aperture width in the <u> direction of the 1st lenslet, divided by f1
	 */
	double sigma1U;

	/**
	 * aperture width in the <v> direction of the 1st lenslet, divided by f1
	 */
	double sigma1V;

	/**
	 * aperture width in the <u> direction of the 2nd lenslet, divided by f1
	 */
	double sigma2U;
	
	/**
	 * aperture width in the <v> direction of the 2nd lenslet, divided by f1
	 */
	double sigma2V;
	
	/**
	 * offset in the <u> direction of the 1st lenslet's aperture centre from its optical axis, divided by f1
	 */
	double alpha1U;
	
	/**
	 * offset in the <v> direction of the 1st lenslet's aperture centre from its optical axis, divided by f1
	 */
	double alpha1V;
	
	/**
	 * offset in the <u> direction of the 2nd lenslet's aperture centre from its optical axis, divided by f1
	 */
	double alpha2U;
	
	/**
	 * offset in the <v> direction of the 2nd lenslet's aperture centre from its optical axis, divided by f1
	 */
	double alpha2V;
	
	
	//
	// constructors
	//
	
	public GCLAsTransmissionCoefficientCalculator(
			GeneralisedConfocalLensletArrays generalisedConfocalLensletArrays,
			double sigma1u, double sigma1v, double sigma2u, double sigma2v,
			double alpha1u, double alpha1v, double alpha2u, double alpha2v)
	{
		super();
		this.generalisedConfocalLensletArrays = generalisedConfocalLensletArrays;
		sigma1U = sigma1u;
		sigma1V = sigma1v;
		sigma2U = sigma2u;
		sigma2V = sigma2v;
		alpha1U = alpha1u;
		alpha1V = alpha1v;
		alpha2U = alpha2u;
		alpha2V = alpha2v;
	}



	//
	// setters & getters
	//

	public GeneralisedConfocalLensletArrays getGeneralisedConfocalLensletArrays() {
		return generalisedConfocalLensletArrays;
	}

	public void setGeneralisedConfocalLensletArrays(GeneralisedConfocalLensletArrays generalisedConfocalLensletArrays) {
		this.generalisedConfocalLensletArrays = generalisedConfocalLensletArrays;
	}

	public double getSigma1U() {
		return sigma1U;
	}

	public void setSigma1U(double sigma1u) {
		sigma1U = sigma1u;
	}

	public double getSigma1V() {
		return sigma1V;
	}

	public void setSigma1V(double sigma1v) {
		sigma1V = sigma1v;
	}

	public double getSigma2U() {
		return sigma2U;
	}

	public void setSigma2U(double sigma2u) {
		sigma2U = sigma2u;
	}

	public double getSigma2V() {
		return sigma2V;
	}

	public void setSigma2V(double sigma2v) {
		sigma2V = sigma2v;
	}

	public double getAlpha1U() {
		return alpha1U;
	}

	public void setAlpha1U(double alpha1u) {
		alpha1U = alpha1u;
	}

	public double getAlpha1V() {
		return alpha1V;
	}

	public void setAlpha1V(double alpha1v) {
		alpha1V = alpha1v;
	}

	public double getAlpha2U() {
		return alpha2U;
	}

	public void setAlpha2U(double alpha2u) {
		alpha2U = alpha2u;
	}

	public double getAlpha2V() {
		return alpha2V;
	}

	public void setAlpha2V(double alpha2v) {
		alpha2V = alpha2v;
	}
	
	
	
	//
	// TransmissionCoefficientCalculator methods
	//

	@Override
	public double calculateTransmissionCoefficient(Vector3D incidentRayDirection, RaySceneObjectIntersection intersection, Orientation orientation)
	{
		// a, u, and v components of the incident light-ray direction
		double
			dA = Vector3D.scalarProduct(incidentRayDirection, generalisedConfocalLensletArrays.getAHat()),
			dU = Vector3D.scalarProduct(incidentRayDirection, generalisedConfocalLensletArrays.getUHat()),
			dV = Vector3D.scalarProduct(incidentRayDirection, generalisedConfocalLensletArrays.getVHat());
		
		return 
				// transmission coefficient in the (a,u) projection
				calculateProjectionTransmissionCoefficient(
						dU,
						dA,
						generalisedConfocalLensletArrays.getEtaU(),
						generalisedConfocalLensletArrays.getDeltaU(),
						alpha1U,
						alpha2U,
						sigma1U,
						sigma2U,
						orientation
					) *
				// transmission coefficient in the (a,v) projection
				calculateProjectionTransmissionCoefficient(
						dV,
						dA,
						generalisedConfocalLensletArrays.getEtaV(),
						generalisedConfocalLensletArrays.getDeltaV(),
						alpha1V,
						alpha2V,
						sigma1V,
						sigma2V,
						orientation
					);
	}
	
	
	/**
	 * @param dT	component of the incident light-ray direction in the <i>u</i> or <i>v</i> direction, here referred to as <i>t</i>
	 * @param dA	component of the incident light-ray direction in the <i>a</i> direction (i.e. along the optical axis)
	 * @param etaT	
	 * @param deltaT
	 * @param alpha1T
	 * @param alpha2T
	 * @param sigma1T
	 * @param sigma2T
	 * @param orientation
	 * @return	the transmission coefficient in one of the two transverse projections
	 */
	public static double calculateProjectionTransmissionCoefficient(
			double dT,
			double dA,
			double etaT,
			double deltaT,
			double alpha1T,
			double alpha2T,
			double sigma1T,
			double sigma2T,
			Orientation orientation
		)
	{
		double eta, delta, alpha1, alpha2, sigma1, sigma2, dtda;
		
		// If the orientation is OUTWARDS, this means that light traverses each telescope such that lens 1 is encountered first.
		// But physically this is only possible if dA>0 (as the a direction points from lens 1 to lens 2).
		if(
				((orientation == Orientation.OUTWARDS) && (dA <= 0)) ||
				((orientation == Orientation.INWARDS) && (dA >= 0))
		)
		{
			// this combination of orientation and sign of dA is physically not possible
			// (new InconsistencyException("Warning: Physically impossible combination of the orientation of the ray and the order of the lenslet arrays encountered.")).printStackTrace();
			System.out.println("GCLAsTransmkissionCoefficient::calculateProjectionTransmissionCoefficient: Warning: Physically impossible combination of the orientation of the ray and the order of the lenslet arrays encountered.");
			return 0;
		}
		
		switch(orientation)
		{
		case INWARDS:
			eta = 1./etaT;
			delta = deltaT / etaT;
			alpha1 = -alpha2T / etaT;
			alpha2 = -alpha1T / etaT;
			sigma1 = -sigma2T / etaT;
			sigma2 = -sigma1T / etaT;
			dtda = -dT/dA;
			break;
		case OUTWARDS:
		default:
			eta = etaT;
			delta = deltaT;
			alpha1 = alpha1T;
			alpha2 = alpha2T;
			sigma1 = sigma1T;
			sigma2 = sigma2T;
			dtda = dT/dA;
		}


		// transverse coordinates of top and bottom of aperture of inwards lens, ...
		double B1 = alpha1 - 0.5*sigma1;
		double T1 = B1 + sigma1;
		
		// ... of outwards lens, ...
		double B2 = delta + alpha2 - 0.5*sigma2;
		double T2 = B2 + sigma2;
		
		// ... and of projection of top and bottom of inwards aperture into the plane of the outwards aperture
		double T1P = dtda - (dtda - T1)*eta;
		double B1P = dtda - (dtda - B1)*eta;
		
		// width of the projection of the inwards aperture into the plane of the outwards aperture
		double sigmaP = Math.abs(T1P - B1P);

		// transverse coordinate of the top of the overlap between the outwards aperture
		// and the projection of the inwards aperture into the plane of the outwards aperture
		double TOverlap = Math.min(
				Math.max(B2, T2),
				Math.max(B1P, T1P)
			);
				
		// transverse coordinate of the bottom of the overlap between the outwards aperture
		// and the projection of the inwards aperture into the plane of the outwards aperture
		double BOverlap = Math.max(
				Math.min(B2, T2),
				Math.min(B1P, T1P)
			);
		
		// length of the overlap between the outwards aperture and the projection of the inwards aperture into the plane of the outwards aperture
		double omega = Math.max(0, TOverlap - BOverlap);
				
		return omega / sigmaP;
	}
	
//	/**
//	 * see section "Transmission coefficient of GCLAs" in "geometry.pdf"
//	 * @param eta
//	 * @param delta
//	 * @param alpha1
//	 * @param alpha2
//	 * @param sigma1
//	 * @param sigma2
//	 * @param dt
//	 * @param da
//	 * @return	the transmission coefficient of GCLAs in one of the projections
//	 */
//	public static double calculate2DGeometricalTransmissionCoefficient(
//			double eta, 
//			double delta, 
//			double alpha1, 
//			double alpha2, 
//			double sigma1, 
//			double sigma2,
//			double dt,	// transverse component of ray direction, i.e. either dU or dV
//			double da
//		)
//	{
//		double dtda = dt/da;
//		
//		// transverse coordinate of the top of the aperture of lens 1, projected into the plane of lens 2
//		double t1P = dtda - (dtda - alpha1 - 0.5*sigma1)*eta;
//		
//		// transverse coordinate of the bottom of the aperture of lens 1, projected into the plane of lens 2
//		double b1P = dtda - (dtda - alpha1 + 0.5*sigma1)*eta;
//		
//		// transverse coordinate of the top of the aperture of lens 2
//		double t2 = delta + alpha2 + 0.5*sigma2;
//
//		// transverse coordinate of the bottom of the aperture of lens 2
//		double b2 = delta + alpha2 - 0.5*sigma2;
//		
//		// width of the aperture of lens 1, projected into the plane of lens 2
//		double projectionWidth = Math.abs(sigma1*eta);
//		
//		// transverse coordinate of the top of the overlap between aperture 2 and the projection of aperture 1 into the plane of lens 2
//		double tOverlap = Math.min(Math.max(t2, b2), Math.max(t1P, b1P));
//
//		// transverse coordinate of the bottom of the overlap between aperture 2 and the projection of aperture 1 into the plane of lens 2
//		double bOverlap = Math.max(Math.min(t2, b2), Math.min(t1P, b1P));
//		
//		// width of the overlap in the plane of lens 2
//		double overlap = Math.max(0, tOverlap - bOverlap);
//		
//		// transmission coefficient
//		return overlap / projectionWidth;
//	}



//	switch(transmissionCoefficientCalculationMethod)
//	{
//	case GEOMETRIC:
//		double thisAlpha1U, thisAlpha2U, thisSigma1U, thisSigma2U, thisAlpha1V, thisAlpha2V, thisSigma1V, thisSigma2V;
//		
//		if(array1First)
//		{
//			// both are pointing the same way, so array 1 is first being encountered
//			thisAlpha1U = getAlpha1U();
//			thisAlpha2U = getAlpha2U();
//			thisAlpha1V = getAlpha1V();
//			thisAlpha2V = getAlpha2V();
//			thisSigma1U = getSigma1U();
//			thisSigma2U = getSigma2U();
//			thisSigma1V = getSigma1V();
//			thisSigma2V = getSigma2V();
//		}
//		else
//		{
//			// they are pointing in opposite directions, so array 2 is being encountered first
//			thisAlpha1U = -getAlpha2U() / getEtaU();
//			thisAlpha2U = -getAlpha1U() / getEtaU();
//			thisAlpha1V = -getAlpha2V() / getEtaV();
//			thisAlpha2V = -getAlpha1V() / getEtaV();
//			thisSigma1U = -getSigma2U() / getEtaU();
//			thisSigma2U = -getSigma1U() / getEtaU();
//			thisSigma1V = -getSigma2V() / getEtaV();
//			thisSigma2V = -getSigma1V() / getEtaV();
//		}
//
//		transmissionCoefficient = 
//			calculate2DGeometricalTransmissionCoefficient(thisEtaU, thisDeltaU, thisAlpha1U, thisAlpha2U, thisSigma1U, thisSigma2U, du, da)
//			* calculate2DGeometricalTransmissionCoefficient(thisEtaV, thisDeltaV, thisAlpha1V, thisAlpha2V, thisSigma1V, thisSigma2V, dv, da);
//		break;
//	case GEOMETRIC_BEST:
//		transmissionCoefficient = 
//			calculate2DGeometricalTransmissionCoefficient(thisEtaU)
//			* calculate2DGeometricalTransmissionCoefficient(thisEtaV);
//		break;
//	case CONSTANT:
//	default:
//		transmissionCoefficient = getTransmissionCoefficient();
//	}

	
	
//	// implement the ideas from the FOV-steering paper
//	
//	/**
//	 * @param a1	aperture centre of lens 1
//	 * @param f1	focal length of lens 1
//	 * @return	principal point of lens 1
//	 */
//	Vector3D calculateP1(Vector3D a1, double f1)
//	{
//		// according to the definition of alpha_1u and alpha_1v,
//		//   A_1 - P_1 = uHat alpha_1u f1 + vHat alpha_1v f1,
//		// so
//		//   P_1 = A_1 - uHat alpha_1u f1 - vHat alpha_1v f1
//		return Vector3D.difference(
//					a1,
//					Vector3D.sum(u.getProductWith(alpha1U*f1), v.getProductWith(alpha1V*f1))
//				);
//	}
//	
//	/**
//	 * @param p1	principal point of lens 1
//	 * @param dC	light-ray direction that corresponds to the centre of the FOV for which the telescopelet of which lens 1 is a part has been designed
//	 * @param f1	focal length of lens 1
//	 * @return	position in the image-sided focal plane of lens 1 to which parallel ray bundles incident with direction dC get focussed
//	 */
//	Vector3D calculateI(Vector3D p1, Vector3D dC, double f1)
//	{
//		// I = P_1 + f_1 d/(d.a)
//		return Vector3D.sum(
//					p1,
//					dC.getProductWith(f1/Vector3D.scalarProduct(dC, a1To2))
//				);
//	}
}