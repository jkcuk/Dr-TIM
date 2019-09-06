package optics.raytrace.research.viewRotation;

import math.MyMath;
import optics.raytrace.research.pointCloudMaker.PointCloudMakerEngine;


/**
 * For creating the point clouds that Cyril in Durham needs to manufacture a Fresnel rotator
 * The required format is a tab-separated text file with x, y and z values
 */
public class FresnelRotatorPointCloudMaker extends PointCloudMakerEngine
{
	//
	// additional variables
	//
	
	/**
	 * the pixels are circular sectors of central angle <i>pPhi</i>
	 */
	private double pPhi;
	
	/**
	 * the azimuthal phase gradient factor
	 */
	private double dPhiOverdphiFactor;



	/**
	 * Constructor.
	 * Sets all parameters.
	 */
	public FresnelRotatorPointCloudMaker() {
		// super();
		
		xMin = -1*CM;
		xMax = +1*CM;
		deltaX =125*UM;
		yMax = +1*CM;
		yMin = -1*CM;
		deltaY = 125*UM;
		
		writeRangeInfo = true;

		pPhi = 2.*Math.PI/10;
		dPhiOverdphiFactor = -0.02;
	}

	
	//
	// PointCloudMakerEngine methods
	//
	
	@Override
	public String getSurfaceName()
	{
		return "Fresnel_rotator_pPhi="+MyMath.doubleToString(pPhi, 4);
	}

	/**
	 * @param x
	 * @param y
	 * @return
	 */
	@Override
	public double calculateZ(double x, double y)
	{
		double r2 = x*x + y*y;
		double phi = Math.atan2(y, x);
		
		// first calculate the phase delay, ...
		double phaseDelayOverK = dPhiOverdphiFactor*r2*(((2*Math.PI + phi + 0.5*pPhi) % pPhi) - 0.5*pPhi);	// add 2 pi to phi as % is strange with negative numbers
		// double phaseDelayOverK = r2;
		// double phaseDelayOverK = (2*Math.PI + phi + 0.5*pPhi) % pPhi;
		
		// ... and then convert this into the corresponding surface height
		return phaseDelay2z(phaseDelayOverK,
				N_PMMA_550NM	// n
				);
	}

	
	//
	// the main method, so that this can be run as a Java application
	//

	/**
	 * Called when this is run; don't touch!
	 * @param args
	 */
	public static void main(final String[] args)
	{
		(new FresnelRotatorPointCloudMaker()).run();
	}
}
