package optics.raytrace.research.viewRotation;

import math.Geometry;
import math.SpaceTimeTransformation.SpaceTimeTransformationType;
import math.Vector3D;
import optics.raytrace.cameras.RelativisticAnaglyphCamera;
import optics.raytrace.cameras.RelativisticAnyFocusSurfaceCamera;
import optics.raytrace.cameras.shutterModels.DetectorPlaneShutterModel;
import optics.raytrace.cameras.shutterModels.FocusSurfaceShutterModel;
import optics.raytrace.cameras.shutterModels.ShutterModel;
import optics.raytrace.cameras.shutterModels.ShutterModelType;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.Studio;
import optics.raytrace.core.SurfaceProperty;
import optics.raytrace.sceneObjects.Plane;

public class CyclodeviationAnaglyphCamera extends RelativisticAnaglyphCamera {

	private static final long serialVersionUID = -6152428405091922823L;

	/**
	 * rotation angle of the left eye, in radians
	 */
	private double leftEyeRotationAngle;

	/**
	 * rotation angle of the right eye, in radians
	 */
	private double rightEyeRotationAngle;
	
	public CyclodeviationAnaglyphCamera(
			String name,
			Vector3D betweenTheEyes,	// middle between two eyes
			Vector3D centreOfView,	// the point in the centre of both eyes' field of fiew
			Vector3D horizontalSpanVector,	// a vector along the width of the field of view, pointing to the right
			Vector3D verticalSpanVector,	// a vector along the height of the field of view, pointing upwards
			Vector3D eyeSeparation,	// separation between the eyes
			double leftEyeRotationAngle,
			double rightEyeRotationAngle,
			SpaceTimeTransformationType spaceTimeTransformationType,
			Vector3D beta,	// scene speed
			int detectorPixelsHorizontal, int detectorPixelsVertical,
			ExposureCompensationType exposureCompensation,
			int maxTraceLevel,
            SceneObject focusScene,
            SceneObject cameraFrameScene,
            ShutterModel shutterModel,
            double apertureRadius,
			boolean diffractiveAperture,
			double lambda,
            int raysPerPixel,
            boolean colour
		)
	{
		// run the AnyFocusSurfaceCamera constructor
		super(
				name,
				betweenTheEyes,	// pinholePosition,
				centreOfView,	// the point in the centre of the field of view
				horizontalSpanVector,
				verticalSpanVector,
				eyeSeparation,
				spaceTimeTransformationType,
				beta,
				detectorPixelsHorizontal, detectorPixelsVertical,
				exposureCompensation,
				maxTraceLevel,
				focusScene,
				cameraFrameScene,
				shutterModel,
				apertureRadius,
				diffractiveAperture,
				lambda,
				raysPerPixel,
				colour
			);

		// memorise any other parameters
		this.leftEyeRotationAngle = leftEyeRotationAngle;
		this.rightEyeRotationAngle = rightEyeRotationAngle;

		// setup the cameras
		setupCameras();
	}
	
	public CyclodeviationAnaglyphCamera(
			String name,
			Vector3D betweenTheEyes,	// middle between two eyes
			Vector3D centreOfView,	// the point in the centre of both eyes' field of fiew
			Vector3D horizontalSpanVector,	// a vector along the width of the field of view, pointing to the right
			Vector3D verticalSpanVector,	// a vector along the height of the field of view, pointing upwards
			Vector3D eyeSeparation,	// separation between the eyes
			double leftEyeRotationAngle,
			double rightEyeRotationAngle,
			int detectorPixelsHorizontal, int detectorPixelsVertical,
			ExposureCompensationType exposureCompensation,
			int maxTraceLevel,
            SceneObject focusScene,
            double apertureRadius,
            int raysPerPixel,
            boolean colour
		)
	{
		this(
				name,
				betweenTheEyes,	// pinholePosition,
				centreOfView,	// the point in the centre of the field of view
				horizontalSpanVector,
				verticalSpanVector,
				eyeSeparation,
				leftEyeRotationAngle,
				rightEyeRotationAngle,
				SpaceTimeTransformationType.LORENTZ_TRANSFORMATION,
				Vector3D.O,	// beta,
				detectorPixelsHorizontal, detectorPixelsVertical,
				exposureCompensation,
				maxTraceLevel,
				focusScene,
				(SceneObject)null,	// cameraFrameScene,
				new FocusSurfaceShutterModel(0),	// shutterModel,
				apertureRadius,
				false,
				1,
				raysPerPixel,
				colour
			);
	}

	public CyclodeviationAnaglyphCamera(
			String name,
			Vector3D betweenTheEyes,	// middle between two eyes
			Vector3D centreOfView,	// the point in the centre of both eyes' field of fiew
			Vector3D horizontalSpanVector,	// a vector along the width of the field of view, pointing to the right
			Vector3D verticalSpanVector,	// a vector along the height of the field of view, pointing upwards
			Vector3D eyeSeparation,	// separation between the eyes
			double leftEyeRotationAngle,
			double rightEyeRotationAngle,
			int detectorPixelsHorizontal, int detectorPixelsVertical,
			ExposureCompensationType exposureCompensation,
			int maxTraceLevel,
            double focussingDistance,
            double apertureRadius,
			boolean diffractiveAperture,
			double lambda,
            int raysPerPixel,
            boolean colour
		)
	{
		this(
				name,
				betweenTheEyes,	// pinholePosition,
				centreOfView,	// the point in the centre of the field of view
				horizontalSpanVector,
				verticalSpanVector,
				eyeSeparation,
				leftEyeRotationAngle,
				rightEyeRotationAngle,
				SpaceTimeTransformationType.LORENTZ_TRANSFORMATION,
				Vector3D.O,	// beta,
				detectorPixelsHorizontal, detectorPixelsVertical,
				exposureCompensation,
				maxTraceLevel,
				(SceneObject)null,	// focus scene
				(SceneObject)null,	// cameraFrameScene,
				new FocusSurfaceShutterModel(0),	// shutterModel,
				apertureRadius,
				diffractiveAperture,
				lambda,
				raysPerPixel,
				colour
			);
		
		// TODO focus scene is a plane through the centreOfView -- improve, make perpendicular to view direction of each eye
		setFocusScene(new Plane(
				"focussing plane",
				centreOfView,	// point on plane
				Vector3D.crossProduct(horizontalSpanVector, verticalSpanVector),	// normal to plane
				(SurfaceProperty)null,
				null,	// parent
				Studio.NULL_STUDIO
			));
	}
	
	public CyclodeviationAnaglyphCamera(
			String name,
			Vector3D betweenTheEyes,	// middle between two eyes
			Vector3D centreOfView,	// the point in the centre of both eyes' field of fiew
			Vector3D horizontalSpanVector,	// a vector along the width of the field of view, pointing to the right
			Vector3D verticalSpanVector,	// a vector along the height of the field of view, pointing upwards
			Vector3D eyeSeparation,	// separation between the eyes
			double leftEyeRotationAngle,
			double rightEyeRotationAngle,
			int detectorPixelsHorizontal, int detectorPixelsVertical,
			ExposureCompensationType exposureCompensation,
			int maxTraceLevel,
            double focussingDistance,
            double apertureRadius,
            int raysPerPixel,
            boolean colour
		)
	{
		this(
				name,
				betweenTheEyes,	// pinholePosition,
				centreOfView,	// the point in the centre of the field of view
				horizontalSpanVector,
				verticalSpanVector,
				eyeSeparation,
				leftEyeRotationAngle,
				rightEyeRotationAngle,
				SpaceTimeTransformationType.LORENTZ_TRANSFORMATION,
				Vector3D.O,	// beta,
				detectorPixelsHorizontal, detectorPixelsVertical,
				exposureCompensation,
				maxTraceLevel,
				(SceneObject)null,	// focus scene
				(SceneObject)null,	// cameraFrameScene,
				new FocusSurfaceShutterModel(0),	// shutterModel,
				apertureRadius,
				false,
				1,
				raysPerPixel,
				colour
			);
		
		// TODO focus scene is a plane through the centreOfView -- improve, make perpendicular to view direction of each eye
		setFocusScene(new Plane(
				"focussing plane",
				centreOfView,	// point on plane
				Vector3D.crossProduct(horizontalSpanVector, verticalSpanVector),	// normal to plane
				(SurfaceProperty)null,
				null,	// parent
				Studio.NULL_STUDIO
			));
	}
	
	@Override
	public void setupCameras()
	{
		Vector3D viewRotationHat = Vector3D.crossProduct(getVerticalSpanVector(), getHorizontalSpanVector()).getNormalised();
		
		leftCamera = new RelativisticAnyFocusSurfaceCamera(
				"left eye",	// name,
				Vector3D.sum(getBetweenTheEyes(), eyeSeparation.getProductWith(-0.5)),	// centre of aperture,
				getCentreOfView(),	// the point in the centre of the field of view
				Geometry.rotate(
						getHorizontalSpanVector(),
						viewRotationHat,
						leftEyeRotationAngle
					),
				Geometry.rotate(
						getVerticalSpanVector(),
						viewRotationHat,
						leftEyeRotationAngle
					),
				getTransformType(),	// .LORENTZ_TRANSFORM,
				getBeta(),
				getDetectorPixelsHorizontal(), getDetectorPixelsVertical(),
				getExposureCompensation(),
				getMaxTraceLevel(),
				getFocusScene(),
				getCameraFrameScene(),
				getShutterModel().clone(),
				getApertureRadius(),
				isDiffractiveAperture(),
				getLambda(),
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
				Geometry.rotate(
						getHorizontalSpanVector(),
						viewRotationHat,
						rightEyeRotationAngle
					),
				Geometry.rotate(
						getVerticalSpanVector(),
						viewRotationHat,
						rightEyeRotationAngle
					),
				getTransformType(),	// .LORENTZ_TRANSFORM,
				getBeta(),
				getDetectorPixelsHorizontal(), getDetectorPixelsVertical(),
				getExposureCompensation(),
				getMaxTraceLevel(),
				getFocusScene(),
				getCameraFrameScene(),
				getShutterModel(),
				getApertureRadius(),
				isDiffractiveAperture(),
				getLambda(),
				getRaysPerPixel()
			);
		if(getShutterModel().getShutterModelType() == ShutterModelType.DETECTOR_PLANE_SHUTTER)
		{
			// the detector-plane shutter model has to be associated with a camera to work
			((DetectorPlaneShutterModel)(rightCamera.getShutterModel())).setCamera(rightCamera);
		}
	}

}
