package optics.raytrace.cameras;

import java.io.Serializable;

import math.*;
import optics.DoubleColour;
import optics.raytrace.GUI.core.CameraWithRayForImagePixel;
import optics.raytrace.core.*;
import optics.raytrace.exceptions.RayTraceException;

/**
 * Represents a pinhole camera: a particularly simple camera with an infinitely small aperture, so
 * everything is in focus.
 * Note that, unlike in a "real" pinhole camera, the detector chip is <i>in front of</i> the pinhole position.
 * This then makes it easier to establish the field of view etc., and makes the description of the pinhole camera
 * more similar to that of a lenticular-array camera, an anaglyph camera, and an autostereogram camera.
 * 
 * It's simple, but it does have exposure compensation!
 * 
 * @author Johannes Courtial
 */
public class PinholeCamera extends CameraClass implements CameraWithRayForImagePixel, Serializable
{
	private static final long serialVersionUID = 1968661638257108328L;

	/* 
	 * These should only be accessed by set and get methods,
	 * or terrible things happen.
	 */
	protected Vector3D pinholePosition;
	
	/**
	 * @author johannes
	 * Describes the camera's exposure-compensation value
	 */
	public enum ExposureCompensationType
	{
		ECp10_0_3("+10", 10.0),
		ECp9_2_3("+9 2/3", 9.0+2.0/3.0),
		ECp9_1_3("+9 1/3", 9.0+1.0/3.0),
		ECp9_0_3("+9", 9.0),
		ECp8_2_3("+8 2/3", 8.0+2.0/3.0),
		ECp8_1_3("+8 1/3", 8.0+1.0/3.0),
		ECp8_0_3("+8", 8.0),
		ECp7_2_3("+7 2/3", 7.0+2.0/3.0),
		ECp7_1_3("+7 1/3", 7.0+1.0/3.0),
		ECp7_0_3("+7", 7.0),
		ECp6_2_3("+6 2/3", 6.0+2.0/3.0),
		ECp6_1_3("+6 1/3", 6.0+1.0/3.0),
		ECp6_0_3("+6", 6.0),
		ECp5_2_3("+5 2/3", 5.0+2.0/3.0),
		ECp5_1_3("+5 1/3", 5.0+1.0/3.0),
		ECp5_0_3("+5", 5.0),
		ECp4_2_3("+4 2/3", 4.0+2.0/3.0),
		ECp4_1_3("+4 1/3", 4.0+1.0/3.0),
		ECp4_0_3("+4", 4.0),
		ECp3_2_3("+3 2/3", 3.0+2.0/3.0),
		ECp3_1_3("+3 1/3", 3.0+1.0/3.0),
		ECp3_0_3("+3", 3.0),
		ECp2_2_3("+2 2/3", 2.0+2.0/3.0),
		ECp2_1_3("+2 1/3", 2.0+1.0/3.0),
		ECp2_0_3("+2", 2.0),
		ECp1_2_3("+1 2/3", 1.0+2.0/3.0),
		ECp1_1_3("+1 1/3", 1.0+1.0/3.0),
		ECp1_0_3("+1", 1.0),
		ECp0_2_3("+2/3", 2.0/3.0),
		ECp0_1_3("+1/3", 1.0/3.0),
		EC0("0",0),
		ECm0_1_3("-1/3", -1.0/3.0),
		ECm0_2_3("-2/3", -2.0/3.0),
		ECm1_0_3("-1", -1.0),
		ECm1_1_3("-1 1/3", -1.0-1.0/3.0),
		ECm1_2_3("-1 2/3", -1.0-2.0/3.0),
		ECm2_0_3("-2", -2.0),
		ECm2_1_3("-2 1/3", -2.0-1.0/3.0),
		ECm2_2_3("-2 2/3", -2.0-2.0/3.0),
		ECm3_0_3("-3", -3.0),
		ECm3_1_3("-3 1/3", -3.0-1.0/3.0),
		ECm3_2_3("-3 2/3", -3.0-2.0/3.0),
		ECm4_0_3("-4", -4.0),
		ECm4_1_3("-4 1/3", -4.0-1.0/3.0),
		ECm4_2_3("-4 2/3", -4.0-2.0/3.0),
		ECm5_0_3("-5", -5.0),
		ECm5_1_3("-5 1/3", -5.0-1.0/3.0),
		ECm5_2_3("-5 2/3", -5.0-2.0/3.0),
		ECm6_0_3("-6", -6.0),
		ECm6_1_3("-6 1/3", -6.0-1.0/3.0),
		ECm6_2_3("-6 2/3", -6.0-2.0/3.0),
		ECm7_0_3("-7", -7.0),
		ECm7_1_3("-7 1/3", -7.0-1.0/3.0),
		ECm7_2_3("-7 2/3", -7.0-2.0/3.0),
		ECm8_0_3("-8", -8.0),
		ECm8_1_3("-8 1/3", -8.0-1.0/3.0),
		ECm8_2_3("-8 2/3", -8.0-2.0/3.0),
		ECm9_0_3("-9", -9.0),
		ECm9_1_3("-9 1/3", -9.0-1.0/3.0),
		ECm9_2_3("-9 2/3", -9.0-2.0/3.0),
		ECm10_0_3("-10", -10.0);
		
		private String description;
		private double value;
		private ExposureCompensationType(String description, double value)
		{
			this.description = description;
			this.value = value;
		}
		@Override
		public String toString() {return description;}
		public double getValue() {return value;}
		
		/**
		 * @return	the factor by which the intensity gets multiplied for this EC value
		 */
		public double toIntensityFactor() {return Math.pow(2.0, value);}
	}
	
	/**
	 * this camera's exposure-compensation value
	 */
	protected ExposureCompensationType exposureCompensation = ExposureCompensationType.EC0;

	
	/**
	 * Create a new pinhole camera
	 * 
	 * @param pinholePosition	position of the pinhole
	 * @param centreOfView	position to the centre of the detector array
	 * @param horizontalSpanVector	Vector3D running along the width of the detector array
	 * @param verticalSpanVector	Vector3D running along the height of the detector array
	 * @param detectorPixelsHorizontal	number of detector pixels in the horizontal direction
	 * @param detectorPixelsVertical	number of detector pixels in the vertical direction
	 */
	public PinholeCamera(
			String description,
			Vector3D pinholePosition,
			Vector3D centreOfView, 
			Vector3D horizontalSpanVector, Vector3D verticalSpanVector,
			int detectorPixelsHorizontal, int detectorPixelsVertical,
			ExposureCompensationType exposureCompensation,
			int maxTraceLevel
		)
	{
		super(	description,
				centreOfView,	// detector centre
				horizontalSpanVector, verticalSpanVector,
				detectorPixelsHorizontal, detectorPixelsVertical,
				maxTraceLevel);
		
		this.pinholePosition = pinholePosition;
		this.exposureCompensation = exposureCompensation;
	}

	/**
	 * Create a clone of the original
	 * @param original
	 */
	public PinholeCamera(PinholeCamera original)
	{
		super(original);
		this.pinholePosition = original.getPinholePosition().clone();
		this.exposureCompensation = original.getExposureCompensation();
	}
	
	/* (non-Javadoc)
	 * @see optics.raytrace.cameras.Camera#clone()
	 */
	@Override
	public PinholeCamera clone()
	{
		return new PinholeCamera(this);
	}
	
	public Vector3D getCentreOfView()
	{
		return getDetectorCentre();
	}

	public void setCentreOfView(Vector3D centreOfView)
	{
		setDetectorCentre(centreOfView);
	}
	
	public Vector3D getPinholePosition()
	{
		return pinholePosition;
	}

	public void setPinholePosition(Vector3D pinholePosition)
	{
		this.pinholePosition = pinholePosition;
	}

	/**
	 * @return the (non-normalised) vector from the pinhole position to the detector centre
	 */
	public Vector3D getViewDirection()
	{
		return Vector3D.difference(getCentreOfView(), getPinholePosition());
	}
	
	public ExposureCompensationType getExposureCompensation() {
		return exposureCompensation;
	}

	public void setExposureCompensation(ExposureCompensationType exposureCompensation) {
		this.exposureCompensation = exposureCompensation;
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.Camera#getRayForPixel(int, int)
	 */
	@Override
	public Ray getRayForPixel(double i, double j)
	{
		// find ray direction by subtracting the pinhole position from the pixel position
		// (as the detector is in front of the pinhole)
		return new Ray(
				pinholePosition, // start point
				Vector3D.difference(getCCD().getPositionOnPixel(i,j), pinholePosition),	// direction
				0,	// start time of ray --- not important here (?)
				false	// reportToConsole
			);
	}
	
	/* (non-Javadoc)
	 * @see optics.raytrace.Camera#getCentralRayForPixel(int, int)
	 */
	@Override
	public Ray getCentralRayForPixel(double i, double j)
	{
		// find ray direction by subtracting the pinhole position from the pixel position
		// (as the detector is in front of the pinhole)
		return new Ray(
				pinholePosition, // start point
				Vector3D.difference(getCCD().getPixelCentrePosition(i,j), pinholePosition),	// direction
				0,	// start time of ray --- not important here (?)
				false	// reportToConsole
			);
	}
	
	/**
	 * In case the rendered image is shown at a different size (imagePixelsHorizontal x imagePixelsVertical),
	 * return a light ray corresponding to image pixel (i,j).
	 * 
	 * In editable cameras, this method implements a method asked for by the EditableCamera interface.
	 * @see optics.raytrace.GUI.core.CameraWithRayForImagePixel#getRayForImagePixel(int, int, int, int)
	 * 
	 * @param imagePixelsHorizontal
	 * @param imagePixelsVertical
	 * @param i
	 * @param j
	 * @return a light ray that corresponds to image pixel (i,j)
	 */
	@Override
	public Ray getRayForImagePixel(int imagePixelsHorizontal, int imagePixelsVertical, double i, double j)
	{
//		System.out.println("getRayForImagePixel:: equivalent pixel number = "+
//				((double)i * ((double)getDetectorPixelsHorizontal()/(double)imagePixelsHorizontal)) + ", " +
//				((double)j * ((double)getDetectorPixelsVertical()/(double)imagePixelsVertical))
//			);
		
		return new Ray(
				getPinholePosition(), // start point
				Vector3D.difference(
						getCCD().getPositionOnPixel(
								(i * ((double)getDetectorPixelsHorizontal()/(double)imagePixelsHorizontal)),
								(j * ((double)getDetectorPixelsVertical()/(double)imagePixelsVertical))
							),
						getPinholePosition()
					),	// direction
				0,	// start time of ray --- not important here (?)
				false	// reportToConsole
			);
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.CCD#calculateColour(int, int)
	 */
	@Override
	public DoubleColour calculatePixelColour(double i, double j, SceneObject scene, LightSource lights)
	throws RayTraceException
	{
		Ray r=getRayForPixel(i,j);

		return scene.getColour(
				r, lights, scene, getMaxTraceLevel(),
				getRaytraceExceptionHandler()
			).multiply(exposureCompensation.toIntensityFactor());
	}

	@Override
	public String toString()
	{
		return getDescription() + " [PinholeCamera]";
	}
}