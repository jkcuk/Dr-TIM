package optics.raytrace.surfaces.transmissionCoefficient;

import math.Vector3D;
import optics.raytrace.core.Orientation;
import optics.raytrace.core.RaySceneObjectIntersection;
import optics.raytrace.surfaces.GCLAsWithApertures;


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
public class GCLAsBestCaseTransmissionCoefficientCalculator extends TransmissionCoefficientCalculator
{
	private static final long serialVersionUID = -3526340923170636293L;

	/**
	 * the GCLAs this applies to;
	 * gives access to unit vectors in <a>, <u> and <v> directions and parameters <eta_u>, <eta_v>, <delta_u>, <delta_v>
	 */
	private GCLAsWithApertures generalisedConfocalLensletArrays;


	
	//
	// constructors
	//
	
	public GCLAsBestCaseTransmissionCoefficientCalculator(
			GCLAsWithApertures generalisedConfocalLensletArrays)
	{
		super();
		this.generalisedConfocalLensletArrays = generalisedConfocalLensletArrays;
	}



	//
	// setters & getters
	//

	public GCLAsWithApertures getGeneralisedConfocalLensletArrays() {
		return generalisedConfocalLensletArrays;
	}

	public void setGeneralisedConfocalLensletArrays(GCLAsWithApertures generalisedConfocalLensletArrays) {
		this.generalisedConfocalLensletArrays = generalisedConfocalLensletArrays;
	}
	
	
	
	//
	// TransmissionCoefficientCalculator methods
	//

	/**
	 * Calculate the best-case geometrical transmission coefficient, i.e. the maximum of the curves in [1]
	 * [1] T. Maceina, G. Juzeliunas, and J. Courtial, "Quantifying metarefraction with confocal lenslet arrays", Opt. Commun. <b>284</b>, 5008-5019 (2011)
	 * @param incidentRayDirection
	 * @param intersection
	 * @param orientation
	 * @return <i>T</i><sub>u</sub> * <i>T</i><sub>v</sub>, where <i>T</i><sub>t</sub> = 1 if |&eta;<sub>t</sub>| <= 1 and <i>T</i><sub>t</sub> = 1/|&eta;<sub>t</sub>| otherwise
	 */
	@Override
	public double calculateTransmissionCoefficient(Vector3D incidentRayDirection, RaySceneObjectIntersection intersection, Orientation orientation)
	{
		double
			etaU = generalisedConfocalLensletArrays.getEtaU(),
			etaV = generalisedConfocalLensletArrays.getEtaV();
		
		return
				((Math.abs(etaU) >= 1)?1:Math.abs(etaU)) *	// transmission coefficient in the (a,u) projection
				((Math.abs(etaV) >= 1)?1:Math.abs(etaV));	// transmission coefficient in the (a,v) projection
	}
}