package optics.raytrace.cameras;

import java.io.*;

import math.*;
import optics.DoubleColour;
import optics.raytrace.core.*;
import optics.raytrace.exceptions.RayTraceException;
import optics.raytrace.utility.CircularApertureDiffraction;

/**
 * A camera that can focus on arbitrary surfaces (and not just planes, like normal cameras).
 */
public class AnyFocusSurfaceCamera extends PinholeCamera implements Serializable
{
	private static final long serialVersionUID = 8013544320848278619L;

	/**
	 * The object onto which the camera is focussed.
	 */
	protected SceneObject focusScene;	// objects the camera focuses on

	/**
	 * if true:
	 * if there is no intersection with any object in the focus scene that's in front of the camera, see if there is
	 * one behind the camera.
	 */
	protected boolean allowFocussingBehindCamera = true;

	/**
	 * The radius of the camera's aperture.
	 */
	protected double apertureRadius;

	/**
	 * Set the circular aperture such that a light ray will get diffracted accordingly.
	 */
	protected boolean diffractiveAperture;

	/**
	 * The wavelength required for diffraction, if diffractiveAperture is true.
	 */
	protected double lambda;

	/**
	 * The number of rays that should be averaged over to evaluate the 
	 * colour of each pixel in the rendered image.
	 */
	protected int raysPerPixel;

	/**
	 * A constructor which allows for diffraction.
	 * @param description
	 * @param apertureCentre
	 * @param centreOfView
	 * @param horizontalSpanVector
	 * @param verticalSpanVector
	 * @param detectorPixelsHorizontal
	 * @param detectorPixelsVertical
	 * @param exposureCompensation
	 * @param maxTraceLevel
	 * @param focusScene
	 * @param apertureRadius
	 * @param raysPerPixel
	 */
	public AnyFocusSurfaceCamera(
			String description,
			Vector3D apertureCentre,
			Vector3D centreOfView,
			Vector3D horizontalSpanVector, Vector3D verticalSpanVector,
			int detectorPixelsHorizontal, int detectorPixelsVertical, 
			ExposureCompensationType exposureCompensation,
			int maxTraceLevel,
			SceneObject focusScene,
			double apertureRadius,
			boolean diffractiveAperture,
			double lambda,
			int raysPerPixel
			)
	{
		super(	description,
				apertureCentre,	// pinhole position is aperture Centre
				centreOfView,
				horizontalSpanVector, verticalSpanVector,
				detectorPixelsHorizontal, detectorPixelsVertical,
				exposureCompensation,
				maxTraceLevel);

		setFocusScene(focusScene);
		setApertureRadius(apertureRadius);
		setRaysPerPixel(raysPerPixel);
		setDiffractiveAperture(diffractiveAperture);
		setLambda(lambda);        

		if(apertureRadius==0 && diffractiveAperture) {
			System.err.println("WARNING: the aperture size is "+apertureRadius+", turn off diffraction for this size!");
		}
	}


	/**
	 * A constructor which does not take into account aperture diffractive effects.
	 * @param description 
	 * @param apertureCentre
	 * @param centreOfView
	 * @param horizontalSpanVector
	 * @param verticalSpanVector
	 * @param detectorPixelsHorizontal
	 * @param detectorPixelsVertical
	 * @param exposureCompensation
	 * @param maxTraceLevel
	 * @param focusScene
	 * @param apertureRadius
	 * @param raysPerPixel
	 */
	public AnyFocusSurfaceCamera(
			String description,
			Vector3D apertureCentre,
			Vector3D centreOfView,
			Vector3D horizontalSpanVector, Vector3D verticalSpanVector,
			int detectorPixelsHorizontal, int detectorPixelsVertical, 
			ExposureCompensationType exposureCompensation,
			int maxTraceLevel,
			SceneObject focusScene,
			double apertureRadius,
			int raysPerPixel
			)
	{
		super(	description,
				apertureCentre,	// pinhole position is aperture Centre
				centreOfView,
				horizontalSpanVector, verticalSpanVector,
				detectorPixelsHorizontal, detectorPixelsVertical,
				exposureCompensation,
				maxTraceLevel);

		setFocusScene(focusScene);
		setApertureRadius(apertureRadius);
		setRaysPerPixel(raysPerPixel);
		setDiffractiveAperture(false);
		setLambda(1);
	}

	/**
	 * Create a clone of the original
	 * @param original
	 */
	public AnyFocusSurfaceCamera(AnyFocusSurfaceCamera original)
	{
		super(original);
		focusScene = original.getFocusScene().clone();
		apertureRadius = original.getApertureRadius();
		diffractiveAperture = original.isDiffractiveAperture();
		lambda = original.getLambda();
		raysPerPixel = original.getRaysPerPixel();
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.cameras.PinholeCamera#clone()
	 */
	@Override
	public AnyFocusSurfaceCamera clone()
	{
		return new AnyFocusSurfaceCamera(this);
	}


	public SceneObject getFocusScene() {
		return focusScene;
	}

	public void setFocusScene(SceneObject focusScene) {
		this.focusScene = focusScene;
	}

	public Vector3D getApertureCentre()
	{
		return getPinholePosition();
	}

	public void setApertureCentre(Vector3D apertureCentre)
	{
		setPinholePosition(apertureCentre);
	}

	public double getApertureRadius() {
		return apertureRadius;
	}

	public void setApertureRadius(double apertureRadius)
	{
		this.apertureRadius = apertureRadius;
	}

	public boolean isDiffractiveAperture() {
		return diffractiveAperture;
	}

	public void setDiffractiveAperture(boolean diffractiveAperture) {
		this.diffractiveAperture = diffractiveAperture;
	}


	public double getLambda() {
		return lambda;
	}


	public void setLambda(double lambda) {
		this.lambda = lambda;
	}


	public int getRaysPerPixel()
	{
		return raysPerPixel;
	}

	public void setRaysPerPixel(int raysPerPixel)
	{
		this.raysPerPixel = raysPerPixel;
	}

	//	public Ray getCentralRayForPixel(double i, double j)
	//	{
	//		return super.getCentralRayForPixel(i, j);
	//	}

	/**
	 * calculate a random point on the aperture
	 * @return	a random point on the aperture
	 */
	protected Vector3D randomPointOnEntrancePupil()
	{
		double x, y;

		do
		{
			// calculate two random numbers, x and y, with -apertureRadius <= x, y <= +apertureRadius...
			x = apertureRadius*(2*Math.random()-1);
			y = apertureRadius*(2*Math.random()-1);
		}		
		// ... while the coordinates (x,y) don't fall within the circular aperture of radius apertureRadius,
		// that is, UNTIL the coordinates (x,y) fall within the aperture
		while(x*x+y*y > apertureRadius*apertureRadius);

		// the coordinates (x,y) should now fall within the circular aperture of radius apertureRadius

		// turn the coordinates (x,y) into the corresponding Vector3D
		return Vector3D.sum(
				getPinholePosition(),
				ccd.getHorizontalSpanVector().getWithLength(x),
				ccd.getVerticalSpanVector().getWithLength(y)
				);
	}


	/**
	 * Get position of the image of the centre of pixel (i,j).  This determines the perspective.
	 * Here, the position of the image of a pixel is the point on focusScene that would be imaged to pixel (i,j)
	 * by a pinhole camera.
	 * @param i The horizontal coordinate of the image position.
	 * @param j The vertical component of the image position.
	 * @return The position of the image of pixel (i,j).
	 */
	protected Vector3D getPixelCentreImagePosition(double i, double j)
	{
		return focusScene.getClosestRayIntersection(getCentralRayForPixel(i,j)).p;
	}

	/**
	 * Get position of the image of pixel (i,j).  This determines the perspective.
	 * Here, the position of the image of a pixel is the point on focusScene that would be imaged to pixel (i,j)
	 * by a pinhole camera.
	 * @param i The horizontal coordinate of the image position.
	 * @param j The vertical component of the image position.
	 * @return The position of the image of pixel (i,j).
	 */
	protected Vector3D getImagePositionOfPointOnPixel(double i, double j)
	{
		return focusScene.getClosestRayIntersection(getRayForPixel(i,j)).p;
	}

	/**
	 * Finds intersections with focus-scene objects that are _behind_ the camera
	 * @param i
	 * @param j
	 * @return
	 */
	protected Vector3D getPixelImagePositionBehind(double i, double j)
	{
		return focusScene.getClosestRayIntersection(getCentralRayForPixel(i,j).getReversedRay()).p;
	}


	/**
	 * Calculate the colour of a pixel on the detector array.  In case of error, the default is a green pixel.
	 * @param i The horizontal position.
	 * @param j The vertical position.
	 * @return The colour of the pixel.
	 */
	@Override
	public DoubleColour calculatePixelColour(double i, double j, SceneObject scene, LightSource lights)
			throws RayTraceException
	{
		boolean pixelImagePositionInFront = true;

		//		// in case there is nothing in the scene, let the raytrace-exception handler deal with it
		//		if(scene == null)
		//		{
		//			return getRaytraceExceptionHandler().getColourOfRayFromNowhere(
		//					// ray starts on point on entrance pupil, which is important for
		//					// ray tracing by RelativisticAnyFocusSurfaceCamera to work
		//					new Ray(
		//							getPinholePosition(), 
		//							pixelImagePositionInFront
		//							?(pixelImagePosition.getDifferenceWith(getPinholePosition()))
		//							:(pixelImagePosition.getDifferenceWith(getPinholePosition()).getReverse()),
		//							0
		//						),
		//					(SceneObject)null,	// originObject
		//					lights, scene, getMaxTraceLevel()
		//				);
		//		}

		DoubleColour sumColour = new DoubleColour(0,0,0);
		for(int poa=0; poa<raysPerPixel; poa++)
		{
			Vector3D pixelImagePosition = 
					getImagePositionOfPointOnPixel(i, j);
			//  getPixelCentreImagePosition(i,j);	// position of the image of pixel (i,j)
			// is there an image position?
			if(pixelImagePosition == null)
			{
				// no, there is none, not _in front of the camera_, anyway
				// is looking _behind_ the camera allowed?
				if(allowFocussingBehindCamera)
				{
					pixelImagePosition = getPixelImagePositionBehind(i,j);
					if(pixelImagePosition != null) pixelImagePositionInFront = false;
				}
			}

			// is there still no pixel image position?
			// not sure how to focus then --- return a random colour
			if (pixelImagePosition==null)
			{
				System.err.println("AnyFocusSurfaceCamera::calculatePixelColour: no focussing-scene object in this direction; returning green");
				return DoubleColour.GREEN;	// catch some sort of error (not quite sure what sort of error, though)
			}

			Vector3D currentPointOnEntrancePupil = randomPointOnEntrancePupil();
			Ray ray = getRay(currentPointOnEntrancePupil, pixelImagePosition, pixelImagePositionInFront);

			sumColour = sumColour.add(
					(scene == null)?
							// in case there is nothing in the scene, let the raytrace-exception handler deal with it
							getRaytraceExceptionHandler().getColourOfRayFromNowhere(
									ray,
									(SceneObject)null,	// originObject
									lights, scene, maxTraceLevel
									):
										scene.getColour(
												ray,
												lights,
												scene,
												maxTraceLevel,
												getRaytraceExceptionHandler()
												)
					);
		}

		return sumColour.multiply(exposureCompensation.toIntensityFactor()/raysPerPixel);
	}

	/**
	 * Create a ray that intersects the entrance pupil at a given point and passes through the pixelImagePosition
	 * (if pixelImagePositionInFront is true; otherwise, the ray's straight-line continuation passes through the
	 * pixelImagePosition).
	 * @param pointOnEntrancePupil	the position on the entrance pupil
	 * @param pixelImagePosition	the position of the image of the pixel
	 * @param pixelImagePositionInFront	does pixelImagePosition lie in front of the camera?
	 * @return
	 */
	protected Ray getRay(Vector3D pointOnEntrancePupil, Vector3D pixelImagePosition, boolean pixelImagePositionInFront)
	{
		if(diffractiveAperture) {
			//set the normal direction of the light ray before it gets diffracted.
			Vector3D incidentNormalisedRayDirection = pixelImagePositionInFront
					?(pixelImagePosition.getDifferenceWith(pointOnEntrancePupil))
							:(pixelImagePosition.getDifferenceWith(pointOnEntrancePupil).getReverse()).getNormalised();
			return new Ray(
					pointOnEntrancePupil, 
					CircularApertureDiffraction.getDiffractedLightRayDirection(
							incidentNormalisedRayDirection,// lightRayDirectionBeforeDiffraction,
							lambda, //lambda,
							apertureRadius,// apertureRadius,
							getApertureCentre(),
							pointOnEntrancePupil,
							Vector3D.difference(getCentreOfView(), getApertureCentre()).getNormalised()// normalisedApertureNormal
							// here we use the view centre and the aperture centre to calculate the azimuthal direction of the diffracted light ray. 
							// We could also use the ccd span vectors to calculate this.
							// However, we think this would be more computationally intensive.
							),
					0,	// time of this "event"; modify this to change shutter model
					false	// reportToConsole

					); 
		}else {
			return new Ray(
					pointOnEntrancePupil, 
					pixelImagePositionInFront
					?(pixelImagePosition.getDifferenceWith(pointOnEntrancePupil))
							:(pixelImagePosition.getDifferenceWith(pointOnEntrancePupil).getReverse()),
							0,	// time of this "event"; modify this to change shutter model
							false	// reportToConsole
					);
		}
	}

	/**
	 * Summarize the curved-focus camera in a string of text.
	 * @return The string representation of the text.
	 */
	@Override
	public String toString ()
	{
		return description+"[AnyFocusSurfaceCamera]";
	} 
}
