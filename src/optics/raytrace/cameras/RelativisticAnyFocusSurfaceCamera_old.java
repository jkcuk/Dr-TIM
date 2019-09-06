package optics.raytrace.cameras;

import optics.DoubleColour;
import optics.raytrace.cameras.shutterModels.LensType;
import optics.raytrace.core.DefaultRaytraceExceptionHandler;
import optics.raytrace.core.LightSource;
import optics.raytrace.core.Ray;
import optics.raytrace.core.RaytraceExceptionHandler;
import optics.raytrace.core.SceneObject;
import optics.raytrace.exceptions.RayTraceException;
import math.GalileanTransform;
import math.Geometry;
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
public class RelativisticAnyFocusSurfaceCamera_old extends AnyFocusSurfaceCamera implements RaytraceExceptionHandler
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
    protected double detectorDistance;
    
	/**
	 * This camera first raytraces in the cameraFrameScene and only then in the scene.
	 * When raytracing in the camera frame, this instance of the RelativisticAnyFocusSurfaceCamera handles
	 * raytrace exceptions itself.
	 * When raytracing in the scene frame, sceneFrameRaytraceExceptionHandler handles raytrace exceptions.
	 */
	protected RaytraceExceptionHandler sceneFrameRaytraceExceptionHandler;
	
	public enum ShutterModelType
	{
		ARBITRARY_PLANE_SHUTTER("Arbitrary-plane shutter"),
		DETECTOR_PLANE_SHUTTER("Detector-plane shutter"),	// normally called "focal-plane shutter"
		ENTRANCE_PUPIL_SHUTTER("Entrance-pupil shutter"),
		FOCUS_SURFACE_SHUTTER("Focus-surface shutter");	// http://en.wikipedia.org/wiki/Single-lens_reflex_camera
		
		private String description;
		private ShutterModelType(String description) {this.description = description;}	
		@Override
		public String toString() {return description;}
	}

	/**
	 * For DETECTOR_PLANE_SHUTTER, the shutter opens at time shutterOpeningTime in the film plane;
	 * for ENTRANCE_PUPIL_SHUTTER, it opens at time shutterOpeningTime on the entrance pupil.
	 */
	protected ShutterModelType shutterModel;
	protected double shutterOpeningTime;
	// details of the plane for the arbitrary-plane shutter model
	protected Vector3D pointInShutterPlane, normalToShutterPlane;
	
	protected LensType lensType;

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
	
    public RelativisticAnyFocusSurfaceCamera_old(
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
            ShutterModelType shutterModel,
            double shutterOpeningTime,
			TransformType transformType,
            LensType lensType,
            double detectorDistance,	// in the detector-plane shutter model, the detector is this distance behind the entrance pupil
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
    	setShutterOpeningTime(shutterOpeningTime);
    	// default values for the arbitrary-plane shutter model, in case that gets selected
    	setPointInShutterPlane(apertureCentre);
    	setNormalToShutterPlane(Vector3D.difference(centreOfView, apertureCentre));
    	setTransformType(transformType);
    	setLensType(lensType);
    	setDetectorDistance(detectorDistance);
    }
    

	public RelativisticAnyFocusSurfaceCamera_old(RelativisticAnyFocusSurfaceCamera_old original)
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
				original.getShutterOpeningTime(),
				original.getTransformType(),
				original.getLensType(),
				original.getDetectorDistance(),
				original.getApertureRadius(),
				original.getRaysPerPixel()
			);
	}
	

	@Override
	public RelativisticAnyFocusSurfaceCamera_old clone()
	{
		return new RelativisticAnyFocusSurfaceCamera_old(this);
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

	public double getDetectorDistance() {
		return detectorDistance;
	}


	public void setDetectorDistance(double detectorDistance) {
		this.detectorDistance = detectorDistance;
	}

	public ShutterModelType getShutterModel() {
		return shutterModel;
	}


	public void setShutterModel(ShutterModelType shutterModel) {
		this.shutterModel = shutterModel;
	}

	public double getShutterOpeningTime() {
		return shutterOpeningTime;
	}


	public void setShutterOpeningTime(double shutterOpeningTime) {
		this.shutterOpeningTime = shutterOpeningTime;
	}

	public Vector3D getPointInShutterPlane() {
		return pointInShutterPlane;
	}


	public void setPointInShutterPlane(Vector3D pointOnShutterPlane) {
		this.pointInShutterPlane = pointOnShutterPlane;
	}


	public Vector3D getNormalToShutterPlane() {
		return normalToShutterPlane;
	}


	/**
	 * sets this.normalToShutterPlane to normalToShutterPlane, normalised
	 * @param normalToShutterPlane
	 */
	public void setNormalToShutterPlane(Vector3D normalToShutterPlane) {
		this.normalToShutterPlane = normalToShutterPlane.getNormalised();
	}


	public TransformType getTransformType() {
		return transformType;
	}


	public void setTransformType(TransformType transformType) {
		this.transformType = transformType;
	}

	public LensType getLensType() {
		return lensType;
	}


	public void setLensType(LensType lensType) {
		this.lensType = lensType;
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
		
		// calculate the time t when the ray passes through the point on the entrance pupil
		double t;
		switch(shutterModel)
		{
		case ENTRANCE_PUPIL_SHUTTER:
			// in this shutter model, all rays pass through the entrance pupil at the same time,
			// namely the shutter-opening time
			t = getShutterOpeningTime();
			break;
		case ARBITRARY_PLANE_SHUTTER:
			// in this shutter model, all rays pass through the arbitrary plane at the same time, namely the shutter-opening time
			Vector3D normalisedPhysicalRayDirection = pointOnPupil2Image.getWithLength(pixelImagePositionInFront?-1:1);
			double lengthToShutter = Geometry.getFactorToLinePlaneIntersection(
					pointOnEntrancePupil,	// pointOnLine
					normalisedPhysicalRayDirection,	// directionOfLine, here the normalised physical ray direction
					pointInShutterPlane,	// pointOnPlane
					normalToShutterPlane	// normalToPlane
				);
			// check that the length is correct
//			System.out.println("should be zero: " + Vector3D.scalarProduct(
//					Vector3D.difference(
//							Vector3D.sum(pointOnEntrancePupil, normalisedPhysicalRayDirection.getProductWith(lengthToShutter)),
//							pointInShutterPlane
//						),
//					normalToShutterPlane
//				));
			// the time the light ray passes through the entrance pupil is then
			t = getShutterOpeningTime() - lengthToShutter / LorentzTransform.c;
//			t = getShutterOpeningTime() - Geometry.getFactorToLinePlaneIntersection(
//					pointOnEntrancePupil,	// pointOnLine
//					pointOnPupil2Image.getWithLength(pixelImagePositionInFront?1:-1),	// directionOfLine, here the normalised physical ray direction
//					pointOnShutterPlane,	// pointOnPlane
//					normalToShutterPlane	// normalToPlane
//				)/LorentzTransform.c;
			// System.out.println("RelativisticAnyFocusSurfaceCamera::getRay: normalToShutterPlane="+normalToShutterPlane+", t="+t);
			break;
		case FOCUS_SURFACE_SHUTTER:
			// in this shutter model, all rays pass through the position where the lens images the pixel at the same
			// time, the shutter-opening time again;
			// calculate the corresponding time when the ray is at the point on the entrance pupil
			t = getShutterOpeningTime() - (pixelImagePositionInFront?-1:1) * pointOnPupil2Image.getLength() / LorentzTransform.c;
			break;
		case DETECTOR_PLANE_SHUTTER:
		default:
			// calculate the position of the detector pixel
			
			// vector from centre of entrance pupil to pixel-image position
			Vector3D ci = Vector3D.difference(pixelImagePosition, getApertureCentre());
			// component of vector ci in view direction
			double ciV = Vector3D.scalarProduct(ci, getViewDirection().getNormalised());
			
			// vector from detector-pixel position to centre of entrance pupil
			Vector3D pc = ci.getProductWith(getDetectorDistance() / ciV);
			
			// detector-pixel position
			Vector3D p = Vector3D.difference(getApertureCentre(), pc);

			// vector from the detector-pixel position to the point on the entrance pupil, e
			Vector3D pe = Vector3D.difference(pointOnEntrancePupil, p);

			switch(lensType)
			{
			case FRESNEL_LENS:
				t = getShutterOpeningTime() - pe.getLength() / LorentzTransform.c;
				break;
			case IDEAL_LENS:
			default:
				// If the lens is perfectly imaging, then all light rays from the detector pixel to its image position
				// take the same time to get there.  This works by the lens introducing a position-dependent time delay,
				// deltaT, which is biggest in the centre.
				// We define here deltaT to be 0 in the lens centre, and therefore *negative* elsewhere.

				// Call the pixel position p, the point on the entrance pupil e, and the image position i.
				// Then the time light takes from i to e, |ei|/c, plus the time delay, deltaT, plus the time
				// light takes from e to p, |pe|/c, has to equal the time from i to p (through the centre of the lens),
				// |pi|/c.
				// Therefore deltaT = 1/c (|pi| - |pe| - |ei|).
				Vector3D pi = Vector3D.difference(pixelImagePosition, p);
				Vector3D ei = pointOnPupil2Image;
				double deltaT = (pi.getLength() - pe.getLength() - (pixelImagePositionInFront?1:-1) * ei.getLength()) / LorentzTransform.c;

				// the time the (backwards-traced) light ray leaves the entrance pupil is then
				t = getShutterOpeningTime() - pe.getLength() / LorentzTransform.c - deltaT;
			}
			
			// old code that I don't understand any longer
//			// vector from centre of entrance pupil to pixel-image position
//			Vector3D ci = Vector3D.difference(pixelImagePosition, getApertureCentre());
//			// component of vector ci in view direction
//			double ciV = -Vector3D.scalarProduct(ci, getViewDirection().getNormalised());
//			double ciLength = ci.getLength();
//			double piLength = ciLength * (getDetectorDistance() + ciV)/ciV;
//			t = getShutterOpeningTime() + (pixelImagePositionInFront?1:-1) * (piLength - ciLength) / LorentzTransform.c;
			break;
		}
		
		return new Ray(
			pointOnEntrancePupil, 
			pixelImagePositionInFront
				?(pointOnPupil2Image)
				:(pointOnPupil2Image.getReverse()),
			t
		);
	}

	/**
	 * In case the rendered image is shown at a different size (imagePixelsHorizontal x imagePixelsVertical),
	 * return a light ray corresponding to image pixel (i,j).
	 * 
	 * In editable cameras, this method implements a method asked for by the EditableCamera interface.
	 * @see optics.raytrace.GUI.core.EditableCamera#getRayForImagePixel(int, int, int, int)
	 * 
	 * @param imagePixelsHorizontal
	 * @param imagePixelsVertical
	 * @param i
	 * @param j
	 * @return a light ray that corresponds to image pixel (i,j)
	 */
	@Override
	protected Ray getRayForImagePixel(int imagePixelsHorizontal, int imagePixelsVertical, double i, double j)
	{
		// in the camera frame
		
		// from AnyfocusSurfaceCamera.calculatePixelColour
		boolean pixelImagePositionInFront = true;
		
		Vector3D pixelImagePosition = getPixelImagePosition(
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