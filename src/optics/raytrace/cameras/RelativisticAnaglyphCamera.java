package optics.raytrace.cameras;

import optics.DoubleColour;
import optics.raytrace.sceneObjects.Plane;
import optics.raytrace.cameras.shutterModels.DetectorPlaneShutterModel;
import optics.raytrace.cameras.shutterModels.FocusSurfaceShutterModel;
import optics.raytrace.cameras.shutterModels.ShutterModel;
import optics.raytrace.cameras.shutterModels.ShutterModelType;
import optics.raytrace.core.LightSource;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.Studio;
import optics.raytrace.core.SurfaceProperty;
import optics.raytrace.exceptions.RayTraceException;
import math.SpaceTimeTransformation.SpaceTimeTransformationType;
import math.Vector3D;

/**
 * A camera that calculates two views, each from a different position.
 * 
 * Represents a binocular camera, which can create simple 3D pictures.
 * Consists essentially of two cameras, one for the left eye, the other for the right eye.
 * 
 * More fancy description, using the "correct" words:
 * Due to binocular disparity, the views are different.
 * With suitable anaglyph glasses, the two different images can be presented to an observer's two eyes.
 * The brain can then derive depth perception from the two different views the two eyes receive, through
 * a process called stereopsis.
 * 
 * Inherits, from the PinholeCamera class, a "pinhole position", which takes on the meaning of
 * the point in the middle between the two eyes.
 * 
 * @author Johannes Courtial
 */
public class RelativisticAnaglyphCamera extends RelativisticAnyFocusSurfaceCamera
{
	private static final long serialVersionUID = -473629130651311108L;
	
	// parameters 
	protected Vector3D eyeSeparation;
	protected boolean colour;
	
	// private variables
	protected RelativisticAnyFocusSurfaceCamera leftCamera, rightCamera;
	          
	
	/**
	 * @param name
	 * @param betweenTheEyes
	 * @param centreOfView
	 * @param horizontalSpanVector
	 * @param verticalSpanVector
	 * @param eyeSeparation
	 * @param detectorPixelsHorizontal
	 * @param detectorPixelsVertical
	 * @param exposureCompensation
	 * @param maxTraceLevel
	 * @param focusScene
	 * @param apertureRadius
	 * @param raysPerPixel
	 * @param colour
	 */
	public RelativisticAnaglyphCamera(
			String name,
			Vector3D betweenTheEyes,	// middle between two eyes
			Vector3D centreOfView,	// the point in the centre of both eyes' field of fiew
			Vector3D horizontalSpanVector,	// a vector along the width of the field of view, pointing to the right
			Vector3D verticalSpanVector,	// a vector along the height of the field of view, pointing upwards
			Vector3D eyeSeparation,	// separation between the eyes
			SpaceTimeTransformationType spaceTimeTransformationType,
			Vector3D beta,	// scene speed
			int detectorPixelsHorizontal, int detectorPixelsVertical,
			ExposureCompensationType exposureCompensation,
			int maxTraceLevel,
            SceneObject focusScene,
            SceneObject cameraFrameScene,
            ShutterModel shutterModel,
            double apertureRadius,
            int raysPerPixel,
            boolean colour
		)
	{
		// run the AnyFocusSurfaceCamera constructor
		super(	name,
				betweenTheEyes,	// pinholePosition,
				centreOfView,	// the point in the centre of the field of view
				horizontalSpanVector, verticalSpanVector,
				spaceTimeTransformationType,
				beta,
				detectorPixelsHorizontal, detectorPixelsVertical,
				exposureCompensation,
				maxTraceLevel,
				focusScene,
				cameraFrameScene,
				shutterModel,
				apertureRadius,
				raysPerPixel
			);

		// memorise any other parameters
		this.eyeSeparation = eyeSeparation;
		this.colour = colour;

		// setup the cameras
		setupCameras();
	}
	
	public RelativisticAnaglyphCamera(
			String name,
			Vector3D betweenTheEyes,	// middle between two eyes
			Vector3D centreOfView,	// the point in the centre of both eyes' field of fiew
			Vector3D horizontalSpanVector,	// a vector along the width of the field of view, pointing to the right
			Vector3D verticalSpanVector,	// a vector along the height of the field of view, pointing upwards
			Vector3D eyeSeparation,	// separation between the eyes
			SpaceTimeTransformationType spaceTimeTransformationType,
			Vector3D beta,	// camera speed, in units of c
			int detectorPixelsHorizontal, int detectorPixelsVertical,
			ExposureCompensationType exposureCompensation,
			int maxTraceLevel,
            boolean colour
		)
	{
		this(	name,
				betweenTheEyes,
				centreOfView,
				horizontalSpanVector, verticalSpanVector,
				eyeSeparation,
				spaceTimeTransformationType,
				beta,
				detectorPixelsHorizontal, detectorPixelsVertical,
				exposureCompensation,
				maxTraceLevel,
				(SceneObject)null,	// focus scene
				(SceneObject)null,	// camera-frame scene
				new FocusSurfaceShutterModel(0),
				0,	// aperture radius
				1,	// rays per pixel
				colour
			);
		
		// focus scene is the plane of the detector
		setFocusScene(new Plane(
				"focussing plane",
				centreOfView,	// point on plane
				Vector3D.crossProduct(horizontalSpanVector, verticalSpanVector),	// normal to plane
				(SurfaceProperty)null,
				null,	// parent
				Studio.NULL_STUDIO
			));
	}

	/**
	 * Create a clone of the original
	 * @param original
	 */
	public RelativisticAnaglyphCamera(RelativisticAnaglyphCamera original)
	{
		super(original);
		
		setLeftCamera(original.getLeftCamera().clone());
		setRightCamera(original.getRightCamera().clone());

		this.eyeSeparation = original.getEyeSeparation();
		this.colour = original.isColour();
	}
	
	@Override
	public RelativisticAnaglyphCamera clone()
	{
		return new RelativisticAnaglyphCamera(this);
	}
	
	public void setupCameras()
	{
		leftCamera = new RelativisticAnyFocusSurfaceCamera(
				"left eye",	// name,
				Vector3D.sum(getBetweenTheEyes(), eyeSeparation.getProductWith(-0.5)),	// centre of aperture,
				getCentreOfView(),	// the point in the centre of the field of view
				getHorizontalSpanVector(), getVerticalSpanVector(),
				getTransformType(),	// .LORENTZ_TRANSFORM,
				getBeta(),
				getDetectorPixelsHorizontal(), getDetectorPixelsVertical(),
				getExposureCompensation(),
				getMaxTraceLevel(),
				getFocusScene(),
				getCameraFrameScene(),
				getShutterModel().clone(),
				getApertureRadius(),
				getRaysPerPixel()
			);
		if(getShutterModel().getShutterModelType() == ShutterModelType.DETECTOR_PLANE_SHUTTER)
		{
			// the detector-plane shutter model has to be associated with a camera to work
			((DetectorPlaneShutterModel)(leftCamera.getShutterModel())).setCamera(leftCamera);
		}

		rightCamera = new RelativisticAnyFocusSurfaceCamera(
				"right eye",	// name,
				Vector3D.sum(getBetweenTheEyes(), eyeSeparation.getProductWith(+0.5)),	// centre of aperture,
				getCentreOfView(),	// the point in the centre of the field of view
				getHorizontalSpanVector(), getVerticalSpanVector(),
				getTransformType(),	// .LORENTZ_TRANSFORM,
				getBeta(),
				getDetectorPixelsHorizontal(), getDetectorPixelsVertical(),
				getExposureCompensation(),
				getMaxTraceLevel(),
				getFocusScene(),
				getCameraFrameScene(),
				getShutterModel(),
				getApertureRadius(),
				getRaysPerPixel()
			);
		if(getShutterModel().getShutterModelType() == ShutterModelType.DETECTOR_PLANE_SHUTTER)
		{
			// the detector-plane shutter model has to be associated with a camera to work
			((DetectorPlaneShutterModel)(rightCamera.getShutterModel())).setCamera(rightCamera);
		}
	}

	/* setters and getters */
	
	/**
	 * @param betweenTheEyes
	 * @param lookAtPoint
	 * @param rightDirection
	 * @param eyeSeparation
	 * @param angleOfViewHorizontal
	 * @param detectorPixelsHorizontal
	 * @param detectorPixelsVertical
	 */
	public void setParameters(
			Vector3D betweenTheEyes,	// middle between two eyes
			Vector3D centreOfView,	// the point in the centre of both eyes' field of fiew
			Vector3D horizontalSpanVector, Vector3D verticalSpanVector,	// a vector pointing to the right
			Vector3D eyeSeparation,	// separation between the eyes
			int detectorPixelsHorizontal, int detectorPixelsVertical,
			boolean colour
		)
	{
		// the superclass's parameters
		setApertureCentre(betweenTheEyes);
		setCentreOfView(centreOfView);
		getCCD().setParameters(centreOfView, horizontalSpanVector, verticalSpanVector, detectorPixelsHorizontal, detectorPixelsVertical);
		
		// memorise any other parameters
		this.eyeSeparation = eyeSeparation;
		this.colour = colour;

		// setup the cameras
		setupCameras();
	}
	
	public Vector3D getBetweenTheEyes()
	{
		return getPinholePosition();
	}
	
	public RelativisticAnyFocusSurfaceCamera getLeftCamera() {
		return leftCamera;
	}

	public void setLeftCamera(RelativisticAnyFocusSurfaceCamera leftCamera) {
		this.leftCamera = leftCamera;
	}

	public RelativisticAnyFocusSurfaceCamera getRightCamera() {
		return rightCamera;
	}

	public void setRightCamera(RelativisticAnyFocusSurfaceCamera rightCamera) {
		this.rightCamera = rightCamera;
	}

	@Override
	public void setApertureRadius(double apertureRadius)
	{
		super.setApertureRadius(apertureRadius);

		if(leftCamera != null) leftCamera.setApertureRadius(apertureRadius);
		if(rightCamera != null) rightCamera.setApertureRadius(apertureRadius);
	}

	@Override
	public void setRaysPerPixel(int raysPerPixel)
	{
		super.setRaysPerPixel(raysPerPixel);
		
		if(leftCamera != null) leftCamera.setRaysPerPixel(raysPerPixel);
		if(rightCamera != null) rightCamera.setRaysPerPixel(raysPerPixel);
	}

	@Override
	public void setMaxTraceLevel(int maxTraceLevel)
	{
		super.setMaxTraceLevel(maxTraceLevel);
		
		if(leftCamera != null) leftCamera.setMaxTraceLevel(maxTraceLevel);
		if(rightCamera != null) rightCamera.setMaxTraceLevel(maxTraceLevel);
	}

	@Override
	public void setFocusScene(SceneObject focusScene)
	{
		super.setFocusScene(focusScene);
		
		if(leftCamera != null) leftCamera.setFocusScene(focusScene);
		if(rightCamera != null) rightCamera.setFocusScene(focusScene);
	}
	
	@Override
	public void setBeta(Vector3D beta)
	{
		super.setBeta(beta);
		
		if(leftCamera != null) leftCamera.setBeta(beta);
		if(rightCamera != null) rightCamera.setBeta(beta);
	}
	
	public boolean isColour() {
		return colour;
	}

	public void setColour(boolean colour) {
		this.colour = colour;
	}

	@Override
	public void setDetectorPixelsHorizontal(int detectorPixelsHorizontal)
	{
		super.setDetectorPixelsHorizontal(detectorPixelsHorizontal);
		if(leftCamera != null) leftCamera.setDetectorPixelsHorizontal(detectorPixelsHorizontal);
		if(rightCamera != null) rightCamera.setDetectorPixelsHorizontal(detectorPixelsHorizontal);
	}

	@Override
	public void setDetectorPixelsVertical(int detectorPixelsVertical)
	{
		super.setDetectorPixelsVertical(detectorPixelsVertical);
		if(leftCamera != null) leftCamera.setDetectorPixelsVertical(detectorPixelsVertical);
		if(rightCamera != null) rightCamera.setDetectorPixelsVertical(detectorPixelsVertical);
	}
	
	public Vector3D getEyeSeparation()
	{
		return eyeSeparation;
	}
	

	/* (non-Javadoc)
	 * @see optics.raytrace.CCD#calculateColour(int, int)
	 */
	@Override
	public DoubleColour calculatePixelColour(double i, double j, SceneObject scene, LightSource lights)
	throws RayTraceException
	{
		DoubleColour
			leftColour = leftCamera.calculatePixelColour(i, j, scene, lights),
			rightColour = rightCamera.calculatePixelColour(i, j, scene, lights);
		
//		// also store together the picture seen by the left and right eye
//		leftCamera.getCCD().setPixelColour(i, j, leftColour.getRGB());
//		rightCamera.getCCD().setPixelColour(i, j, rightColour.getRGB());
		
		if(isColour())
		{
			// from http://en.wikipedia.org/wiki/Anaglyph_image:
			// "In recent simple practice, the left eye image is filtered to remove blue & green.
			// The right eye image is filtered to remove red."
			return new DoubleColour(
					leftColour.getR(),
					rightColour.getG(),
					rightColour.getB()
				);
		}
		else
		{
			// display left image in red, and right image in blue or green & blue;
			// the latter works very well with the combination of my laptop monitor and 3D glasses
			return new DoubleColour(
					leftColour.getLuminance(),
					0, // rightColour.getLuminance(),
					rightColour.getLuminance()	// * DoubleColour.LUMINANCE_R_FACTOR / DoubleColour.LUMINANCE_B_FACTOR
				);
		}
	}
}
