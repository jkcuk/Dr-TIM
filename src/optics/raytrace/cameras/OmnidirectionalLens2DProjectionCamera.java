package optics.raytrace.cameras;

import java.io.Serializable;

import math.*;
import optics.DoubleColour;
import optics.raytrace.GUI.core.CameraWithRayForImagePixel;
import optics.raytrace.GUI.sceneObjects.EditableIdealLensCloak;
import optics.raytrace.core.*;
import optics.raytrace.exceptions.RayTraceException;

/**
 * Represents a projection of the inside (and one half of the outside) of a 3-sided omnidirectional lens.
 * 
 * @author Johannes Courtial
 */
public class OmnidirectionalLens2DProjectionCamera extends CameraClass implements Serializable
{

	/* 
	 * These should only be accessed by set and get methods,
	 * or terrible things happen.
	 */
	protected EditableIdealLensCloak idealLensCloak;
	
	protected double hProjectionPlane;
	

	
	/**
	 * Create a new OmnidirectionalLens2DProjectionCamera
	 */
	public OmnidirectionalLens2DProjectionCamera(
			String description,
			EditableIdealLensCloak idealLensCloak,
			double hProjectionPlane, 
			Vector3D horizontalSpanVector, Vector3D verticalSpanVector,
			int detectorPixelsHorizontal, int detectorPixelsVertical
		)
	{
		super(	description,
				centreOfView,	// detector centre
				horizontalSpanVector, verticalSpanVector,
				detectorPixelsHorizontal, detectorPixelsVertical,
				maxTraceLevel);
		
		this.idealLensCloak = idealLensCloak;
		this.hProjectionPlane = hProjectionPlane;
	}

	/**
	 * Create a clone of the original
	 * @param original
	 */
	public OmnidirectionalLens2DProjectionCamera(OmnidirectionalLens2DProjectionCamera original)
	{
		super(original);
		this.pinholePosition = original.getPinholePosition().clone();
		this.exposureCompensation = original.getExposureCompensation();
	}
	
	/* (non-Javadoc)
	 * @see optics.raytrace.cameras.Camera#clone()
	 */
	@Override
	public OmnidirectionalLens2DProjectionCamera clone()
	{
		return new OmnidirectionalLens2DProjectionCamera(this);
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