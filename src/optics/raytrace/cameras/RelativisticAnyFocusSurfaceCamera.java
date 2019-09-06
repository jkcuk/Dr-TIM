package optics.raytrace.cameras;

import optics.DoubleColour;
import optics.raytrace.cameras.shutterModels.ShutterModel;
import optics.raytrace.core.DefaultRaytraceExceptionHandler;
import optics.raytrace.core.LightSource;
import optics.raytrace.core.Ray;
import optics.raytrace.core.RaytraceExceptionHandler;
import optics.raytrace.core.SceneObject;
import optics.raytrace.exceptions.RayTraceException;
import math.GalileanTransform;
import math.LorentzTransform;
import math.Vector3D;

/**
 * @author johannes
 * A camera which is moving relative to the scene with velocity beta (= v / c).
 * It takes a photo in light rays that pass through the aperture at time t=0 (in the camera frame).
 * The direction of the light ray can be calculated from the coordinates of two events, namely when the
 * light ray passes through the aperture (event 1) and when it hits the detector pixel (event 2), transformed
 * into the coordinate system in which the scene is at rest.
 * [1] A. Howard, S. Dance and L. Kitchen, "Relativistic Ray-Tracing: Simulating the Visual Appearance of Rapidly
 * Moving Objects" (1995)
 */
public class RelativisticAnyFocusSurfaceCamera extends AnyFocusSurfaceCamera implements RaytraceExceptionHandler
{
	private static final long serialVersionUID = -5157996130583972646L;
	
	/**
	 * the velocity of the scene in the camera frame, in units of c (the speed of light)
	 */
	protected Vector3D
		beta = new Vector3D(0, 0, 0);

	/**
     * Any objects that are at rest in the camera frame
     */
    protected SceneObject cameraFrameScene;
    
    /**
     * The distance the detector plane is behind the entrance pupil
     */
    // protected double detectorDistance;
    
	/**
	 * This camera first raytraces in the cameraFrameScene and only then in the scene.
	 * When raytracing in the camera frame, this instance of the RelativisticAnyFocusSurfaceCamera handles
	 * raytrace exceptions itself.
	 * When raytracing in the scene frame, sceneFrameRaytraceExceptionHandler handles raytrace exceptions.
	 */
	protected RaytraceExceptionHandler sceneFrameRaytraceExceptionHandler;
	
	/**
	 * the shutter model, which determines the timing of rays contributing to the photo
	 */
	protected ShutterModel shutterModel;
	
	public enum TransformType
	{
		GALILEAN_TRANSFORM("Galilean transform"),
		LORENTZ_TRANSFORM("Lorentz transform");
		
		private String description;
		private TransformType(String description) {this.description = description;}	
		@Override
		public String toString() {return description;}
	}

	protected TransformType transformType = TransformType.LORENTZ_TRANSFORM;
	
    public RelativisticAnyFocusSurfaceCamera(
    		String description,
            Vector3D apertureCentre,
            Vector3D centreOfView,
            Vector3D horizontalSpanVector, Vector3D verticalSpanVector,
            Vector3D beta,
            int detectorPixelsHorizontal, int detectorPixelsVertical, 
            ExposureCompensationType exposureCompensation,
            int maxTraceLevel,
            SceneObject focusScene,
            SceneObject cameraFrameScene,
            ShutterModel shutterModel,
			TransformType transformType,
            // double detectorDistance,	// in the detector-plane shutter model, the detector is this distance behind the entrance pupil
            double apertureRadius,
            int raysPerPixel
    	)
    {
    	super(
    			description, apertureCentre, centreOfView, horizontalSpanVector, verticalSpanVector,
    			detectorPixelsHorizontal, detectorPixelsVertical, exposureCompensation, maxTraceLevel,
    			focusScene, apertureRadius, raysPerPixel
    		);
    	setRaytraceExceptionHandler(this);
    	setBeta(beta);
    	setCameraFrameScene(cameraFrameScene);
    	setSceneFrameRaytraceExceptionHandler(new DefaultRaytraceExceptionHandler());
    	setShutterModel(shutterModel);
    	setTransformType(transformType);
    	// setDetectorDistance(detectorDistance);
    }
    

	public RelativisticAnyFocusSurfaceCamera(RelativisticAnyFocusSurfaceCamera original)
	{
		this(
				original.getDescription(),
				original.getApertureCentre(),
				original.getCentreOfView(),
				original.getHorizontalSpanVector(),
				original.getVerticalSpanVector(),
				original.getBeta(),
				original.getDetectorPixelsHorizontal(),
				original.getDetectorPixelsVertical(), 
				original.getExposureCompensation(),
				original.getMaxTraceLevel(),
				original.getFocusScene(),
				original.getCameraFrameScene(),
				original.getShutterModel(),
				original.getTransformType(),
				// original.getDetectorDistance(),
				original.getApertureRadius(),
				original.getRaysPerPixel()
			);
	}
	

	@Override
	public RelativisticAnyFocusSurfaceCamera clone()
	{
		return new RelativisticAnyFocusSurfaceCamera(this);
	}

	// getters and setters
	public Vector3D getBeta() {
		return beta;
	}

	public void setBeta(Vector3D beta)
	{
		this.beta = beta;
	}

//	@Override
//	public Ray getCentralRayForPixel(int i, int j)
//	{
//		// find ray direction by subtracting the pinhole position from the pixel position
//		// (as the detector is in front of the pinhole)
//		Vector3D pixelPosition = getCCD().pixelPosition(i,j);
//		
//		Vector3D LorentzTransformedPinholePosition = LorentzTransformedPosition(pinholePosition, 0, beta);
//
//		// calculate the time when the light ray hits the pixel (in the camera frame)
//		Vector3D pinhole2pixel = Vector3D.difference(pixelPosition, pinholePosition);
//		// if the ray passes through the pinhole at t=0, then it hits the pixel at (in the camera frame)
//		double t = Math.sqrt(Vector3D.scalarProduct(pinhole2pixel, pinhole2pixel))/c;
//		
//		return new Ray(
//				LorentzTransformedPinholePosition, // start point
//				Vector3D.difference(
//						LorentzTransformedPosition(pixelPosition, t, beta),
//						LorentzTransformedPinholePosition
//					)	// direction
//			);
//	}
	
    public SceneObject getCameraFrameScene() {
		return cameraFrameScene;
	}


	public void setCameraFrameScene(SceneObject cameraFrameScene) {
		this.cameraFrameScene = cameraFrameScene;
	}


	public RaytraceExceptionHandler getSceneFrameRaytraceExceptionHandler() {
		return sceneFrameRaytraceExceptionHandler;
	}


	public void setSceneFrameRaytraceExceptionHandler(
			RaytraceExceptionHandler sceneFrameRaytraceExceptionHandler) {
		this.sceneFrameRaytraceExceptionHandler = sceneFrameRaytraceExceptionHandler;
	}

//	public double getDetectorDistance() {
//		return detectorDistance;
//	}
//
//
//	public void setDetectorDistance(double detectorDistance) {
//		this.detectorDistance = detectorDistance;
//	}

	public ShutterModel getShutterModel() {
		return shutterModel;
	}


	public void setShutterModel(ShutterModel shutterModel)
	{
		// System.out.println("RelativisticAnyFocusSurfaceCamera:setShutterModel: shutterModel="+shutterModel);
		
		this.shutterModel = shutterModel;
	}

	public TransformType getTransformType() {
		return transformType;
	}


	public void setTransformType(TransformType transformType) {
		this.transformType = transformType;
	}

	/**
	 * holds the scene objects in the scene frame
	 */
	private SceneObject sceneFrameScene;


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
		sceneFrameScene = scene;
		
		// trace through the objects that are at rest in the camera frame;
		// any rays that don't end on an object there are then (through the raytraceExceptionHandler, which is this
		// (i.e. this object)) handled by the getColourOfRayFromNowhere method
		return super.calculatePixelColour(i, j, getCameraFrameScene(), lights);
	}

	/* 
	 * Gets called when ray tracing in the camera frame has finished.
	 * This method then continues tracing the same ray in the scene frame.
	 * @see optics.raytrace.core.RaytraceExceptionHandler#getColourOfRayFromNowhere(optics.raytrace.core.Ray, optics.raytrace.core.SceneObject, optics.raytrace.core.LightSource, optics.raytrace.core.SceneObject, int)
	 */
	@Override
	public DoubleColour getColourOfRayFromNowhere(Ray ray,
			SceneObject originObject, LightSource lights, SceneObject cameraFrameScene,
			int traceLevel)
	throws RayTraceException
	{
		if(sceneFrameScene == null)
		{
			// there is nothing to trace through;
			// let the scene-frame raytrace-exception handler handle this
			return getSceneFrameRaytraceExceptionHandler().getColourOfRayFromNowhere(ray, originObject, lights, sceneFrameScene, traceLevel-1);
		}
	
		// if we're dealing with the non-relativistic situation...
		if((beta.x == 0.0) && (beta.y == 0.0) && (beta.z == 0.0))
		{
			// ... no need to transform any positions
			return sceneFrameScene.getColour(
					ray,
					lights,
					sceneFrameScene,
					traceLevel-1,
					getSceneFrameRaytraceExceptionHandler()
				);
		}

		// now need to transform the ray's current position and direction into the scene frame;
		// in our aperture model, the ray passes through the shutter plane at time t=0, and as in the
		// AnyFocusSurfaceCamera the rays start on a point in the shutter plane, we just need to
		// calculate the optical path length they have travelled and divide it by c to get
		// the time (in the camera frame) they reach their current position (also in the camera
		// frame)
		
		// System.out.println("t="+ray.getT());
		
		Vector3D rayStartPointSceneFrame, rayDirectionSceneFrame;
		double startTimeSceneFrame;
		
		// the velocity/c of the scene frame in the camera frame
		Vector3D minusBeta = beta.getReverse();
		
		switch(transformType)
		{
		case GALILEAN_TRANSFORM:
			// transforming the start position is easy
			rayStartPointSceneFrame = GalileanTransform.getTransformedPosition(ray.getP(), ray.getT(), minusBeta);
			
			// transforming the ray direction is also easy
			rayDirectionSceneFrame = GalileanTransform.getTransformedLightRayDirection(ray.getD(), minusBeta);
			
			startTimeSceneFrame = GalileanTransform.getTransformedTime(ray.getP(), ray.getT(), minusBeta);
			
			break;
		case LORENTZ_TRANSFORM:
		default:
			// transforming the start position is easy
			rayStartPointSceneFrame = LorentzTransform.getTransformedPosition(ray.getP(), ray.getT(), minusBeta);
			
			// transforming the ray direction is also easy
			rayDirectionSceneFrame = LorentzTransform.getTransformedLightRayDirection(ray.getD(), minusBeta);

			startTimeSceneFrame = LorentzTransform.getTransformedTime(ray.getP(), ray.getT(), minusBeta);
		}
		
//		System.out.println(
//				"original ray: P="+ray.getP()+", t="+ray.getT()+", D="+ray.getD()+"; "+
//				"transformed ray: P'="+rayStartPointSceneFrame+", D'="+rayDirectionSceneFrame
//			);
		
		// now we know all we need to know about the ray in the scene frame, so launch it!
		return sceneFrameScene.getColour(
				new Ray(
						rayStartPointSceneFrame,
						rayDirectionSceneFrame,
						startTimeSceneFrame
					),
				lights,
				sceneFrameScene,
				traceLevel-1,
				getSceneFrameRaytraceExceptionHandler()
			);
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.cameras.AnyFocusSurfaceCamera#getRay(math.Vector3D, math.Vector3D, boolean)
	 */
	@Override
	protected Ray getRay(Vector3D pointOnEntrancePupil, Vector3D pixelImagePosition, boolean pixelImagePositionInFront)
	{
		// a vector from the point on the entrance pupil to the pixel-image position
		Vector3D pointOnPupil2Image = pixelImagePosition.getDifferenceWith(pointOnEntrancePupil);
				
		return new Ray(
			pointOnEntrancePupil,	// start position
			pixelImagePositionInFront
				?(pointOnPupil2Image)
				:(pointOnPupil2Image.getReverse()),	// initial direction
			shutterModel.getAperturePlaneTransmissionTime(pointOnEntrancePupil, pixelImagePosition, pixelImagePositionInFront)	// time
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
		// in the camera frame
		
		// from AnyfocusSurfaceCamera.calculatePixelColour
		boolean pixelImagePositionInFront = true;
		
		Vector3D pixelImagePosition = getPixelCentreImagePosition(
				(i * ((double)getDetectorPixelsHorizontal()/(double)imagePixelsHorizontal)),
				(j * ((double)getDetectorPixelsVertical()/(double)imagePixelsVertical))
			);	// position of the image of pixel (i,j)
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
		// not sure how to focus then --- say that there's an error and return null
		if (pixelImagePosition==null)
		{
			System.err.println("RelativisticAnyFocusSurfaceCamera::getRayForImagePixel: no focussing-scene object in this direction; returning green");
			return null;	// catch no-focussing-scene-object-in-this-direction error
		}
		
		return getRay(getPinholePosition(), pixelImagePosition, pixelImagePositionInFront);
//		
//		// the relevant positions...
//		Vector3D pinholePositionCameraFrame = getPinholePosition();
//		Vector3D pixelPositionCameraFrame = getCCD().pixelPosition(
//				(int)((double)getDetectorPixelsHorizontal()/(double)imagePixelsHorizontal*i),
//				(int)((double)getDetectorPixelsVertical()/(double)imagePixelsVertical*j)
//			);
//		// ... and times: the light ray passes through the pinhole position at t = 0, and hits the pixel at
//		double t = Vector3D.difference(pixelPositionCameraFrame, pinholePositionCameraFrame).getLength() / LorentzTransform.c;
//
//		Vector3D pinholePositionSceneFrame = LorentzTransform.getTransformedPosition(pinholePositionCameraFrame, 0, beta);
//		Vector3D pixelPositionSceneFrame = LorentzTransform.getTransformedPosition(pixelPositionCameraFrame, t, beta);
//
//		return new Ray(
//				pinholePositionSceneFrame, // start point
//				Vector3D.difference(pixelPositionSceneFrame, pinholePositionSceneFrame),	// direction
//				0	// start time (in camera frame)
//			);
	}
}