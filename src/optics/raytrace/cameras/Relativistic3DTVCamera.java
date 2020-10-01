package optics.raytrace.cameras;

import optics.DoubleColour;
import optics.raytrace.sceneObjects.Plane;
import optics.raytrace.GUI.lowLevel.Format3DType;
import optics.raytrace.GUI.lowLevel.OrientationType;
import optics.raytrace.cameras.shutterModels.AperturePlaneShutterModel;
import optics.raytrace.cameras.shutterModels.ShutterModel;
import optics.raytrace.core.LightSource;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.Studio;
import optics.raytrace.core.SurfaceProperty;
import optics.raytrace.exceptions.RayTraceException;
import math.SpaceTimeTransformation.SpaceTimeTransformationType;
import math.Vector3D;

/**
 * A camera that calculates two views, each from a different position, and displays them in a frame-packed format
 * (top-and-bottom or left-and-right), for use with 3D TVs.
 * 
 * Represents a binocular camera, which can create simple 3D pictures.
 * Consists essentially of two cameras, one for the left eye, the other for the right eye.
 * But the Relativistic3DTVCamera class is also of type Camera, and as such has a CCD.
 * The number of pixels of that CCD is that of the combined, frame-packed image.
 * 
 * Based on RelativisticAnaglyphCamera class.
 * 
 * Inherits, from the PinholeCamera class, a "pinhole position", which takes on the meaning of
 * the point in the middle between the two eyes.
 * 
 * @author Johannes Courtial
 */
public class Relativistic3DTVCamera extends RelativisticAnyFocusSurfaceCamera
{
	private static final long serialVersionUID = -8731361567893909375L;

	// parameters 
	private Vector3D eyeSeparation;
	
	private OrientationType framePackingOrientation;
	private int framePixelsHorizontal, framePixelsVertical, gapBetweenFrames;	// some 3D formats require a gap of dark pixels between the frames...
	
	// private variables
	private RelativisticAnyFocusSurfaceCamera leftCamera, rightCamera;
	          
	
	/**
	 * @param name
	 * @param betweenTheEyes
	 * @param centreOfView
	 * @param horizontalSpanVector
	 * @param verticalSpanVector
	 * @param eyeSeparation
	 * @param beta
	 * @param framePixelsHorizontal
	 * @param framePixelsVertical
	 * @param framePackingOrientation
	 * @param gapBetweenFrames
	 * @param exposureCompensation
	 * @param maxTraceLevel
	 * @param focusScene
	 * @param cameraFrameScene
	 * @param shutterModel
	 * @param apertureRadius
	 * @param raysPerPixel
	 */
	public Relativistic3DTVCamera(
			String name,
			Vector3D betweenTheEyes,	// middle between two eyes
			Vector3D centreOfView,	// the point in the centre of both eyes' field of fiew
			Vector3D horizontalSpanVector,	// a vector along the width of the field of view, pointing to the right
			Vector3D verticalSpanVector,	// a vector along the height of the field of view, pointing upwards
			Vector3D eyeSeparation,	// separation between the eyes
			Vector3D beta,	// scene speed
			int framePixelsHorizontal, int framePixelsVertical,
			OrientationType framePackingOrientation,
			int gapBetweenFrames,
			ExposureCompensationType exposureCompensation,
			int maxTraceLevel,
            SceneObject focusScene,
            SceneObject cameraFrameScene,
            ShutterModel shutterModel,
            double apertureRadius,
            int raysPerPixel
		)
	{
		// run the AnyFocusSurfaceCamera constructor
		super(	name,
				betweenTheEyes,	// pinholePosition,
				centreOfView,	// the point in the centre of the field of view
				horizontalSpanVector, verticalSpanVector,
				SpaceTimeTransformationType.LORENTZ_TRANSFORMATION,
				beta,
				getPackedImagePixels(OrientationType.HORIZONTAL, framePixelsHorizontal, framePixelsVertical, framePackingOrientation, gapBetweenFrames),
				getPackedImagePixels(OrientationType.VERTICAL, framePixelsHorizontal, framePixelsVertical, framePackingOrientation, gapBetweenFrames),
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
		this.framePixelsHorizontal = framePixelsHorizontal;
		this.framePixelsVertical = framePixelsVertical;
		this.framePackingOrientation = framePackingOrientation;
		this.gapBetweenFrames = gapBetweenFrames;

		// setup the cameras
		setupCameras();
	}

	public Relativistic3DTVCamera(
			String name,
			Vector3D betweenTheEyes,	// middle between two eyes
			Vector3D centreOfView,	// the point in the centre of both eyes' field of fiew
			Vector3D horizontalSpanVector,	// a vector along the width of the field of view, pointing to the right
			Vector3D verticalSpanVector,	// a vector along the height of the field of view, pointing upwards
			Vector3D eyeSeparation,	// separation between the eyes
			Vector3D beta,	// scene speed
			Format3DType format3D,
			ExposureCompensationType exposureCompensation,
			int maxTraceLevel,
            SceneObject focusScene,
            SceneObject cameraFrameScene,
            ShutterModel shutterModel,
            double detectorDistance,
            double apertureRadius,
            int raysPerPixel
		)
	{
		this(
				name,
				betweenTheEyes,
				centreOfView,
				horizontalSpanVector,
				verticalSpanVector,
				eyeSeparation,
				beta,
				format3D.getHPixels(),
				format3D.getVPixels(),
				format3D.getOrientation(),
				format3D.getGap(),
				exposureCompensation,
				maxTraceLevel,
				focusScene,
				cameraFrameScene,
				shutterModel,
				apertureRadius,
				raysPerPixel
			);
	}

	public Relativistic3DTVCamera(
			String name,
			Vector3D betweenTheEyes,	// middle between two eyes
			Vector3D centreOfView,	// the point in the centre of both eyes' field of fiew
			Vector3D horizontalSpanVector,	// a vector along the width of the field of view, pointing to the right
			Vector3D verticalSpanVector,	// a vector along the height of the field of view, pointing upwards
			Vector3D eyeSeparation,	// separation between the eyes
			Vector3D beta,	// camera speed, in units of c
			int framePixelsHorizontal, int framePixelsVertical,
			OrientationType framePackingOrientation,
			int gapBetweenFrames,
			ExposureCompensationType exposureCompensation,
			int maxTraceLevel
		)
	{
		this(	name,
				betweenTheEyes,
				centreOfView,
				horizontalSpanVector, verticalSpanVector,
				eyeSeparation,
				beta,
				framePixelsHorizontal, framePixelsVertical,
				framePackingOrientation,
				gapBetweenFrames,
				exposureCompensation,
				maxTraceLevel,
				(SceneObject)null,	// focus scene
				(SceneObject)null,	// camera-frame scene
				new AperturePlaneShutterModel(0),
				0,	// aperture radius
				1	// rays per pixel
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
	public Relativistic3DTVCamera(Relativistic3DTVCamera original)
	{
		super(original);
		
		setLeftCamera(original.getLeftCamera().clone());
		setRightCamera(original.getRightCamera().clone());

		this.eyeSeparation = original.getEyeSeparation();
		this.framePixelsHorizontal = original.getFramePixelsHorizontal();
		this.framePixelsVertical = original.getFramePixelsVertical();
		this.framePackingOrientation = original.getFramePackingOrientation();
		this.gapBetweenFrames = original.getGapBetweenFrames();
	}
	
	@Override
	public Relativistic3DTVCamera clone()
	{
		return new Relativistic3DTVCamera(this);
	}
	
	public void setupCameras()
	{
		leftCamera = new RelativisticAnyFocusSurfaceCamera(
				"left eye",	// name,
				Vector3D.sum(getBetweenTheEyes(), eyeSeparation.getProductWith(-0.5)),	// centre of aperture,
				getCentreOfView(),	// the point in the centre of the field of view
				getHorizontalSpanVector(), getVerticalSpanVector(),
				SpaceTimeTransformationType.LORENTZ_TRANSFORMATION,
				getBeta(),
				getFramePixelsHorizontal(), getFramePixelsVertical(),
				getExposureCompensation(),
				getMaxTraceLevel(),
				getFocusScene(),
				getCameraFrameScene(),
				getShutterModel(),
				getApertureRadius(),
				getRaysPerPixel()
			);

		rightCamera = new RelativisticAnyFocusSurfaceCamera(
				"right eye",	// name,
				Vector3D.sum(getBetweenTheEyes(), eyeSeparation.getProductWith(+0.5)),	// centre of aperture,
				getCentreOfView(),	// the point in the centre of the field of view
				getHorizontalSpanVector(), getVerticalSpanVector(),
				SpaceTimeTransformationType.LORENTZ_TRANSFORMATION,
				getBeta(),
				getFramePixelsHorizontal(), getFramePixelsVertical(),
				getExposureCompensation(),
				getMaxTraceLevel(),
				getFocusScene(),
				getCameraFrameScene(),
				getShutterModel(),
				getApertureRadius(),
				getRaysPerPixel()
			);
	}

	/* setters and getters */
	
	/**
	 * @param betweenTheEyes
	 * @param lookAtPoint
	 * @param rightDirection
	 * @param eyeSeparation
	 * @param angleOfViewHorizontal
	 * @param framePixelsHorizontal
	 * @param framePixelsVertical
	 */
	public void setParameters(
			Vector3D betweenTheEyes,	// middle between two eyes
			Vector3D centreOfView,	// the point in the centre of both eyes' field of fiew
			Vector3D horizontalSpanVector, Vector3D verticalSpanVector,	// a vector pointing to the right
			Vector3D eyeSeparation,	// separation between the eyes
			int framePixelsHorizontal, int framePixelsVertical
		)
	{
		// the superclass's parameters
		setApertureCentre(betweenTheEyes);
		setCentreOfView(centreOfView);
		getCCD().setParameters(centreOfView, horizontalSpanVector, verticalSpanVector, getPackedImagePixelsHorizontal(), getPackedImagePixelsVertical());
		
		// memorise any other parameters
		this.eyeSeparation = eyeSeparation;

		// setup the cameras
		setupCameras();
	}
	
	public int getFramePixelsHorizontal() {
		return framePixelsHorizontal;
	}

	public int getFramePixelsVertical() {
		return framePixelsVertical;
	}

	public OrientationType getFramePackingOrientation() {
		return framePackingOrientation;
	}

	public void setFramePackingOrientation(OrientationType framePackingOrientation) {
		this.framePackingOrientation = framePackingOrientation;
	}

	public int getGapBetweenFrames() {
		return gapBetweenFrames;
	}

	public void setGapBetweenFrames(int gapBetweenFrames) {
		this.gapBetweenFrames = gapBetweenFrames;
	}

	public Vector3D getBetweenTheEyes()
	{
		return getPinholePosition();
	}
	
	/**
	 * @param orientation
	 * @param framePixelsHorizontal
	 * @param framePixelsVertical
	 * @param framePackingOrientation
	 * @param gapBetweenFrames
	 * @return	the number of pixels in the direction given by orientation in the packed image (e.g. side-by-side frames)
	 */
	public static int getPackedImagePixels(
			OrientationType orientation,
			int framePixelsHorizontal,
			int framePixelsVertical,
			OrientationType framePackingOrientation,
			int gapBetweenFrames
		)
	{
		int n = (orientation == OrientationType.HORIZONTAL)?framePixelsHorizontal:framePixelsVertical;
		return (framePackingOrientation == orientation)?(2*n+gapBetweenFrames):n;
	}
	
	public int getPackedImagePixels(OrientationType orientation, Format3DType format3D)
	{
		return getPackedImagePixels(
				orientation,
				format3D.getHPixels(),
				format3D.getVPixels(),
				format3D.getOrientation(),
				format3D.getGap()
			);
	}
	
	public int getPackedImagePixelsHorizontal()
	{
		return getPackedImagePixels(
				OrientationType.HORIZONTAL,
				getFramePixelsHorizontal(),
				getFramePixelsVertical(),
				getFramePackingOrientation(),
				getGapBetweenFrames()
			);
	}

	public int getPackedImagePixelsVertical()
	{
		return getPackedImagePixels(
				OrientationType.VERTICAL,
				getFramePixelsHorizontal(),
				getFramePixelsVertical(),
				getFramePackingOrientation(),
				getGapBetweenFrames()
			);
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
	
	@Override
	public void setDetectorPixelsHorizontal(int framePixelsHorizontal)
	{
		setFramePixelsHorizontal(framePixelsHorizontal);
	}
	
	public void setFramePixelsHorizontal(int framePixelsHorizontal)
	{
		this.framePixelsHorizontal = framePixelsHorizontal;
		super.setDetectorPixelsHorizontal(getPackedImagePixelsHorizontal());
		if(leftCamera != null) leftCamera.setDetectorPixelsHorizontal(framePixelsHorizontal);
		if(rightCamera != null) rightCamera.setDetectorPixelsHorizontal(framePixelsHorizontal);
	}

	@Override
	public void setDetectorPixelsVertical(int framePixelsVertical)
	{
		setFramePixelsVertical(framePixelsVertical);
	}
	
	public void setFramePixelsVertical(int framePixelsVertical)
	{
		this.framePixelsVertical = framePixelsVertical;
		super.setDetectorPixelsVertical(getPackedImagePixelsVertical());
		if(leftCamera != null) leftCamera.setDetectorPixelsVertical(framePixelsVertical);
		if(rightCamera != null) rightCamera.setDetectorPixelsVertical(framePixelsVertical);
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
//		if(i == 0 && j == 0)
//		{
//			System.out.println(
//					"Relativistic3DTVCamera::calculatePixelColour: "+
//					"orientation="+framePackingOrientation+
//					", frame size="+getFramePixelsHorizontal()+"x"+getFramePixelsVertical()
//				);
//		}
		if(framePackingOrientation == OrientationType.HORIZONTAL)
		{
			// horizontal frame packing
			if(i < getFramePixelsHorizontal())
			{
				// use the left-eye camera
				return leftCamera.calculatePixelColour(i, j, scene, lights);
			}
			else if(i < getFramePixelsHorizontal() + getGapBetweenFrames())
			{
				// the gap between the frames (if there is one)
				return DoubleColour.BLACK;
			}
			else
			{
				// use the right-eye camera
				return rightCamera.calculatePixelColour(i-getFramePixelsHorizontal()-getGapBetweenFrames(), j, scene, lights);
			}
		}
		else
		{
			// vertical frame packing
			if(j < getFramePixelsVertical())
			{
				// use the left-eye camera
				return leftCamera.calculatePixelColour(i, j, scene, lights);
			}
			else if(j < getFramePixelsVertical() + getGapBetweenFrames())
			{
				// the gap between the frames (if there is one)
				return DoubleColour.BLACK;
			}
			else
			{
				// use the right-eye camera
				return rightCamera.calculatePixelColour(i, j-getFramePixelsVertical()-getGapBetweenFrames(), scene, lights);
			}
		}
	}
}