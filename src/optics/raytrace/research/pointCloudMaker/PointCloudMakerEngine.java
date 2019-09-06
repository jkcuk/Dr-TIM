package optics.raytrace.research.pointCloudMaker;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import math.*;


/**
 * A class that facilitates creating a point cloud, the format required by Cyril in Durham to manufacture free-form surfaces.
 * The required format is a tab-separated text file with x, y and z values.
 * The file gets saved in the Dr TIM folder.
 * 
 * In order to work as a JavaApplication, any subclass of this class needs to implement a main method
 * that gets called when the Java application starts. In the simplest case, this is of the form
 *  
 *  	public static void main(final String[] args)
 *  	{
 *  		(new <subclass of PointCloudMakerEngine>()).run();
 *  	}
 *  
 * Set all the variables in the constructor.
 * 
 * Implement the method calculateZ(double x, double y) to define the surface.
 * 
 * Override the method getFilename() to change the filename under which the rendered result will be saved.
 * 
 * @author Johannes Courtial
 */
public abstract class PointCloudMakerEngine
implements Runnable
{
	// all lengths are measured in millimeters here; define a few unit factors
	public static final double CM = 1e1;
	public static final double MM = 1e0;
	public static final double UM = 1e-3;
	public static final double NM = 1e-6;
	
	/**
	 * refractive index of PMMA @ 550nm
	 * from https://refractiveindex.info/?shelf=organic&book=poly(methyl_methacrylate)&page=Szczurowski
	 */
	public static final double N_PMMA_550NM = 1.4924;	

	// parameters
	protected double xMin;
	protected double xMax;
	protected double deltaX;
	protected double yMax;
	protected double yMin;
	protected double deltaY;
	
	/**
	 * if true, an additional file will be created that includes info about the range, i.e. xMin, xMax, yMin, yMax, deltaX, deltaY
	 */
	protected boolean writeRangeInfo;


	/**
	 * Constructor.
	 * Override to change values and/or to set additional parameters
	 */
	public PointCloudMakerEngine()
	{
		// set parameters to default values
		xMin = -1*CM;
		xMax = +1*CM;
		deltaX =125*UM;
		yMax = +1*CM;
		yMin = -1*CM;
		deltaY = 125*UM;
		
		writeRangeInfo = true;
	}
	
	/**
	 * The name of the surface, which forms the beginning of the filename
	 * @return
	 */
	public String getSurfaceName()
	{
		return "Test";
	}

	/**
	 * @return a filename with today's date in it
	 */
	public String getPointCloudFilename()
	{	
		Date today = Calendar.getInstance().getTime();
		// return String.format(getSurfaceName()+"_point_cloud_"+"%tF %<tT.csv", today, today);
		return String.format(getSurfaceName()+"_point_cloud_"+"%tF.csv", today);
	}
	
	public String getRangeInfoFilename()
	{
		Date today = Calendar.getInstance().getTime();
		// return String.format(getSurfaceName()+"_range_info_"+"%tF %<tT.txt", today, today);
		return String.format(getSurfaceName()+"_range_info_"+"%tF.txt", today);
	}
	
	/**
	 * @param x	in mm
	 * @param y	in mm
	 * @return	z	the height of the surface at (x, y), in mm
	 */
	public abstract double calculateZ(double x, double y);
	
	//
	// useful methods
	//
	
	/**
	 * @param phaseDelay
	 * @param n	refractive index of the medium
	 * @param k	wave number of the light
	 * @return	the length of a medium with refractive index n that, when compared to the same length of air, results in the given phase delay
	 */
	public double phaseDelay2z(double phaseDelay, double n, double k)
	{
		// The optical path length (OPL) of a distance dz in air is dz, that in a medium with refractive index n is n dz.
		// The OPL difference is 
		// 	dOPL = (n-1) dz,
		// which corresponds to a phase difference
		// 	dPhi = dOPL/lambda * 2 Pi = dOPL k = (n-1) dz k.
		// Solved for dz to get
		// 	dz = dPhi / ((n-1) k).
		return phaseDelay / ((n-1)*k);
	}

	/**
	 * @param phaseDelayOverK
	 * @param n	refractive index of the medium
	 * @return	the length of a medium with refractive index n that, when compared to the same length of air, results in the given phase delay
	 */
	public double phaseDelay2z(double phaseDelayOverK, double n)
	{
		// The optical path length (OPL) of a distance dz in air is dz, that in a medium with refractive index n is n dz.
		// The OPL difference is 
		// 	dOPL = (n-1) dz,
		// which corresponds to a phase difference
		// 	dPhi = dOPL/lambda * 2 Pi = dOPL k = (n-1) dz k.
		// Solved for dz to get
		// 	dz = dPhi/k / (n-1).
		return phaseDelayOverK / (n-1);
	}

	
	/**
	 * @author	Johannes Courtial
	 */
	@Override
	public void run()
	{
		FileWriter pointCloud;
		/*
		 * If imaging front surface: normal of lensSelector is lensNormal.getReverse() and the coordinates are converted to a RIGHT HANDED
		 * system according to (x, y, z) --> (-x, y, z).
		 * If imaging the back surface: normal of lensSelector is lensNormal and the coordinates are converted to a RIGHT HANDED system 
		 * according to (x, y, z) --> (x, y, -z)
		 */

		System.out.println("Writing point cloud...");

		try {
			pointCloud = new FileWriter(getPointCloudFilename());
			// units are in mm
			// go through an array of x and y positions...
			for(double x = xMin; x <= xMax; x += deltaX)
			{
				//for some values of deltaX and/or deltaY we end up with (xMax-xMin)/DeltaX + (yMax-yMin)/deltaY + 1 many datapoints. 
				for(double y = yMin; y <= yMax; y += deltaY)
				{	//the origin of the rays is defined with respect to the centre of the lens
					pointCloud.write(
							MyMath.doubleToString(x, 9) + "\t" +	// x
							MyMath.doubleToString(y, 9) + "\t" +	// y
							MyMath.doubleToString(calculateZ(x, y), 9) +	// z
							"\r\n"
						);
				}
				//This is unnecessary. Just to indicate how it's progressing
				System.out.println("x = "+MyMath.doubleToString(x, 9));
			}
			pointCloud.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if(writeRangeInfo)
		{
			System.out.println("Writing range info...");

			//This file is written with info for the Python script I'm using to visualise the surface.
			FileWriter pointDensity;
			try {
				pointDensity = new FileWriter(getRangeInfoFilename());
				pointDensity.write(Math.abs(xMax-xMin)+","+deltaX+"\n");
				pointDensity.write(Math.abs(yMax-yMin)+","+deltaY);
				pointDensity.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		
		System.out.println("Done.");
	}	
}
