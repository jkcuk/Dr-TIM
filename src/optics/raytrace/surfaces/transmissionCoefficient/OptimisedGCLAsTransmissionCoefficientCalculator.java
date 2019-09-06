package optics.raytrace.surfaces.transmissionCoefficient;

import math.Vector3D;
import optics.raytrace.core.Orientation;
import optics.raytrace.core.RaySceneObjectIntersection;
import optics.raytrace.surfaces.GeneralisedConfocalLensletArrays;


/**
 * @author johannes
 *
 * This class provides methods that facilitate the calculation of the transmission coefficient of GCLAs
 * whose apertures have been placed such that they are optimised for a bundle of light rays that, on
 * one side of the GCLAs (the side of array `1'), passes through a given point <i>pointAtFOVCentre</i>.
 * Seen from the GCLAs, this point is therefore at the centre of the field of view direction on that side.
 * 
 * Each lenslet is assumed to have a rectangular aperture with its side aligned with the <u> and <v> directions.
 * 
 * References:
 * [1] A. C. Hamilton and J. Courtial, "Generalized refraction using lenslet arrays", J. Opt. A: Pure Appl. Opt. 11, 065502 (2009)
 * [2] E. N. Cowie and J. Courtial, "Engineering the field of view of generalised confocal lenslet arrays", in preparation (2017)
 */
public class OptimisedGCLAsTransmissionCoefficientCalculator extends TransmissionCoefficientCalculator
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
	 * "parallelity" parameter that describes how parallel the line through the aperture centres is
	 * to the line through the principal points:
	 * p=1 means it is parallel, 
	 * p=0 means it is parallel to the a axis (and not the line through the principal points) 
	 */
	double parallelityParameter;
	
	/**
	 * the position on the side of array `1' through which the light rays pass for which transmission is optimised
	 */
	Vector3D pointAtFOVCentre;
	
	
	//
	// constructors
	//

	/**
	 * @param generalisedConfocalLensletArrays
	 * @param sigma1u
	 * @param sigma1v
	 * @param sigma2u
	 * @param sigma2v
	 * @param parallelityParameter
	 * @param pointAtFOVCentre
	 */
	public OptimisedGCLAsTransmissionCoefficientCalculator(
			GeneralisedConfocalLensletArrays generalisedConfocalLensletArrays, double sigma1u,
			double sigma1v, double sigma2u, double sigma2v, double parallelityParameter, Vector3D pointAtFOVCentre)
	{
		super();
		this.generalisedConfocalLensletArrays = generalisedConfocalLensletArrays;
		sigma1U = sigma1u;
		sigma1V = sigma1v;
		sigma2U = sigma2u;
		sigma2V = sigma2v;
		this.parallelityParameter = parallelityParameter;
		this.pointAtFOVCentre = pointAtFOVCentre;
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
		this.sigma1U = sigma1u;
	}

	public double getSigma1V() {
		return sigma1V;
	}

	public void setSigma1V(double sigma1v) {
		this.sigma1V = sigma1v;
	}

	public double getSigma2U() {
		return sigma2U;
	}

	public void setSigma2U(double sigma2u) {
		this.sigma2U = sigma2u;
	}

	public double getSigma2V() {
		return sigma2V;
	}

	public void setSigma2V(double sigma2v) {
		this.sigma2V = sigma2v;
	}

	public double getParallelityParameter() {
		return parallelityParameter;
	}

	public void setParallelityParameter(double parallelityParameter) {
		this.parallelityParameter = parallelityParameter;
	}

	public Vector3D getPointAtFOVCentre() {
		return pointAtFOVCentre;
	}

	public void setPointAtFOVCentre(Vector3D pointAtFOVCentre) {
		this.pointAtFOVCentre = pointAtFOVCentre;
	}

	
	
	//
	// TransmissionCoefficientCalculator methods
	//


	/* (non-Javadoc)
	 * @see optics.raytrace.surfaces.transmissionCoefficient.TransmissionCoefficientCalculator#calculateTransmissionCoefficient(math.Vector3D, optics.raytrace.core.RaySceneObjectIntersection, optics.raytrace.core.Orientation)
	 */
	@Override
	public double calculateTransmissionCoefficient(Vector3D incidentRayDirection, RaySceneObjectIntersection intersection, Orientation orientation)
	{
		// calculate the light-ray direction at the centre of the field of view;
		// don't worry if this is pointing "inwards" or "outwards", as only the ratio of
		// components matters (and so any -1 factor due to the opposite orientation cancels)
		Vector3D centreOfFOV = Vector3D.difference(intersection.p, getPointAtFOVCentre());
				
		// calculate the components of the light-ray direction at the centre of the field of view
		double
			c1a = Vector3D.scalarProduct(centreOfFOV, generalisedConfocalLensletArrays.getAHat()),
			c1u = Vector3D.scalarProduct(centreOfFOV, generalisedConfocalLensletArrays.getUHat()),
			c1v = Vector3D.scalarProduct(centreOfFOV, generalisedConfocalLensletArrays.getVHat());
		
		// components of the incident light-ray direction
		double
			da = Vector3D.scalarProduct(incidentRayDirection, generalisedConfocalLensletArrays.getAHat()),
			du = Vector3D.scalarProduct(incidentRayDirection, generalisedConfocalLensletArrays.getUHat()),
			dv = Vector3D.scalarProduct(incidentRayDirection, generalisedConfocalLensletArrays.getVHat());
		
		return 
				// transmission coefficient in the (a,u) projection
				GCLAsTransmissionCoefficientCalculator.calculateProjectionTransmissionCoefficient(
						du,
						da,
						generalisedConfocalLensletArrays.getEtaU(),
						generalisedConfocalLensletArrays.getDeltaU(),
						calculateAlpha1(
								c1u/c1a,
								generalisedConfocalLensletArrays.getEtaU(),
								generalisedConfocalLensletArrays.getDeltaU()
							),	// alpha1U
						calculateAlpha2(
								c1u/c1a,
								generalisedConfocalLensletArrays.getEtaU(),
								generalisedConfocalLensletArrays.getDeltaU()
							),	// alpha2U
						sigma1U,
						sigma2U,
						orientation
					) *
				// transmission coefficient in the (a,v) projection
				GCLAsTransmissionCoefficientCalculator.calculateProjectionTransmissionCoefficient(
						dv,
						da,
						generalisedConfocalLensletArrays.getEtaV(),
						generalisedConfocalLensletArrays.getDeltaV(),
						calculateAlpha1(
								c1v/c1a,
								generalisedConfocalLensletArrays.getEtaV(),
								generalisedConfocalLensletArrays.getDeltaV()
							),	// alpha1V
						calculateAlpha2(
								c1v/c1a,
								generalisedConfocalLensletArrays.getEtaV(),
								generalisedConfocalLensletArrays.getDeltaV()
							),	// alpha2V
						sigma1V,
						sigma2V,
						orientation
					);
	}
	
	/**
	 * Find the parameter &alpha;<sub>1u</sub> or &alpha;<sub>1v</sub> such that it corresponds to aperture
	 * placement for which the incident light direction
	 * <b>c</b> = (<i>c</i><sub>a</sub>,<i>c</i><sub>u</sub>,<i>c</i><sub>v</sub>) is at the centre
	 * of the field of view.
	 * @param c1Tc1a	the ratio of <i>c</i><sub>t</sub>/<i>c</i><sub>a</sub>, where t is either u or v
	 * @param eta	the parameter &eta;<sub>t</sub>, where t is either u or v
	 * @param delta	the parameter &delta;<sub>t</sub>, where t is either u or v
	 * @return	the aperture-offset parameter &alpha;<sub>1t</sub>, where t is either u or v
	 */
	public double calculateAlpha1(
			double c1Tc1a,
			double eta,
			double delta
		)
	{
//		System.out.println("OptimisedGCLAsTransmissionCoefficientCalculator::calculateAlpha1: c1Tc1a="+c1Tc1a+
//				", eta="+eta+
//				", delta="+delta+
//				", alpha1="+(c1Tc1a - parallelityParameter*delta/(1-eta)));
		return c1Tc1a - parallelityParameter*delta/(1-eta);
	}

	/**
	 * Find the parameter &alpha;<sub>2u</sub> or &alpha;<sub>2v</sub> such that it corresponds to aperture
	 * placement for which the incident light direction
	 * <b>c</b> = (<i>c</i><sub>a</sub>,<i>c</i><sub>u</sub>,<i>c</i><sub>v</sub>) is at the centre
	 * of the field of view.
	 * @param c1Tc1a	the ratio of <i>c</i><sub>t</sub>/<i>c</i><sub>a</sub>, where t is either u or v
	 * @param eta	the parameter &eta;<sub>t</sub>, where t is either u or v
	 * @param delta	the parameter &delta;<sub>t</sub>, where t is either u or v
	 * @return	the aperture-offset parameter &alpha;<sub>2t</sub>, where t is either u or v
	 */
	public double calculateAlpha2(
			double c1Tc1a,
			double eta,
			double delta
		)
	{
//		System.out.println("OptimisedGCLAsTransmissionCoefficientCalculator::calculateAlpha2: c1Tc1a="+c1Tc1a+
//				", eta="+eta+
//				", delta="+delta+
//				", alpha2="+(c1Tc1a - parallelityParameter*eta*delta/(1-eta) - (1-parallelityParameter)*delta));
		return c1Tc1a - parallelityParameter*eta*delta/(1-eta) - (1-parallelityParameter)*delta;
	}
}