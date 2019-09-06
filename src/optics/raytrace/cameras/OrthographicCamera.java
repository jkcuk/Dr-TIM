package optics.raytrace.cameras;

import java.io.Serializable;

import math.*;
import optics.DoubleColour;
import optics.raytrace.GUI.core.CameraWithRayForImagePixel;
import optics.raytrace.core.*;
import optics.raytrace.exceptions.RayTraceException;

/**
 * Represents an orthographic camera.
 * A photo is an orthographic (parallel) projection into a plane; everything is in focus.
 * 
 * @author Johannes Courtial
 */
public class OrthographicCamera extends CameraClass implements CameraWithRayForImagePixel, Serializable
{
	private static final long serialVersionUID = 2895830794942134690L;

	/* 
	 * These should only be accessed by set and get methods
	 * or terrible things happen.
	 */
	protected Vector3D viewDirection;
	
	/**
	 * Create a new orthographic camera
	 * 
	 * @param viewDirection
	 * @param horizontalSpanVector3D	vector spanning the projection plane in direction corresponding to horizontal; length is width of displayed range
	 * @param verticalSpanVector3D	vector spanning the projection plane in direction corresponding to vertical; length meaningless
	 * @param detectorPixelsHorizontal	number of detector pixels in the horizontal direction
	 * @param detectorPixelsVertical	number of detector pixels in the vertical direction
	 */
	public OrthographicCamera(
			String name,
			Vector3D viewDirection,
			Vector3D CCDCentre,
			Vector3D horizontalSpanVector3D, Vector3D verticalSpanVector3D,
			int detectorPixelsHorizontal, int detectorPixelsVertical,
			int maxTraceLevel)
	{
		super(	name,
				CCDCentre,	// centre of detector
				horizontalSpanVector3D, verticalSpanVector3D,
				detectorPixelsHorizontal, detectorPixelsVertical,
				maxTraceLevel);
		this.viewDirection = viewDirection;
	}

	/**
	 * Create a clone of the original
	 * @param original
	 */
	public OrthographicCamera(OrthographicCamera original)
	{
		super(original);
		this.viewDirection = original.getViewDirection().clone();
	}
	
	/* (non-Javadoc)
	 * @see optics.raytrace.cameras.Camera#clone()
	 */
	@Override
	public OrthographicCamera clone()
	{
		return new OrthographicCamera(this);
	}
	

	//
	// setters and getters
	//
	
	public Vector3D getViewDirection() {
		return viewDirection;
	}

	public void setViewDirection(Vector3D viewDirection) {
		this.viewDirection = viewDirection;
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.Camera#getRayForPixel(int, int)
	 */
	@Override
	public Ray getRayForPixel(double i, double j)
	{
		// find ray direction by subtracting pixel position from pinhole position
		return new Ray(ccd.getPositionOnPixel(i,j), viewDirection, 0);
	}
	
	/* (non-Javadoc)
	 * @see optics.raytrace.Camera#getCentralRayForPixel(int, int)
	 */
	@Override
	public Ray getCentralRayForPixel(double i, double j)
	{
		// find ray direction by subtracting pixel position from pinhole position
		return new Ray(ccd.getPixelCentrePosition(i,j), viewDirection, 0);
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
		return new Ray(
				getCCD().getPositionOnPixel(
						i*getDetectorPixelsHorizontal()/imagePixelsHorizontal,
						j*getDetectorPixelsVertical()/imagePixelsVertical
					),
				viewDirection,
				0	// start time of ray --- not important here (?)
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

		return scene.getColour(r, lights, scene, getMaxTraceLevel(),
				getRaytraceExceptionHandler());
	}

	
	@Override
	public String toString() {
		return "OrthographicCamera [viewDirection=" + viewDirection + ", ccd="
				+ ccd + ", maxTraceLevel=" + maxTraceLevel + "]";
	}
}