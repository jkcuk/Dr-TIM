package optics.raytrace.research.RelativisticPhotography;

import java.awt.*;

import math.*;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.surfaces.LorentzTransformInterface;
import optics.raytrace.surfaces.SurfaceColour;
import optics.raytrace.utility.CoordinateSystems.CoordinateSystemType;
import optics.raytrace.cameras.PinholeCamera.ExposureCompensationType;
import optics.raytrace.cameras.shutterModels.DetectorPlaneShutterModel;
import optics.raytrace.cameras.shutterModels.AperturePlaneShutterModel;
import optics.raytrace.cameras.shutterModels.FocusSurfaceShutterModel;
import optics.raytrace.cameras.shutterModels.InstantShutterModel;
import optics.raytrace.cameras.shutterModels.LensType;
import optics.raytrace.cameras.shutterModels.ShutterModel;
import optics.raytrace.cameras.shutterModels.ShutterModelType;
import optics.raytrace.cameras.RelativisticAnyFocusSurfaceCamera.TransformType;
import optics.raytrace.core.*;
import optics.raytrace.GUI.cameras.EditableRelativisticAnyFocusSurfaceCamera;
import optics.raytrace.GUI.cameras.QualityType;
import optics.raytrace.GUI.lowLevel.ApertureSizeType;
import optics.raytrace.GUI.nonInteractive.PhotoCanvas;
import optics.raytrace.GUI.nonInteractive.PhotoFrame;
import optics.raytrace.GUI.sceneObjects.EditableCylinderLattice;
import optics.raytrace.GUI.sceneObjects.EditableNinkyNonkSilhouette;
import optics.raytrace.GUI.sceneObjects.EditableParametrisedPlane;
import optics.raytrace.GUI.sceneObjects.EditableScaledParametrisedSphere;
import optics.raytrace.GUI.sceneObjects.EditableSceneObjectCollection;


/**
 * 
 * Derived from @see optics.raytrace.NonInteractiveTIM
 * 
 * @author Johannes Courtial
 */
public class ShutterModelFocussing
{
	/**
	 * if TRUE, the final image will be saved
	 */
	public static final boolean SAVE = true;
	
	/**
	 * if TRUE, the camera is treated as being at rest (but the spheres placed such that a moving camera focusses on them
	 * will still be placed according to the value of beta)
	 */
	public static final boolean BETA_0 = false;
	// public static final boolean LORENTZ_WINDOW = true;
	public static final ApertureSizeType APERTURE_SIZE = 
			// ApertureSizeType.PINHOLE;
			// ApertureSizeType.SMALL;
			ApertureSizeType.MEDIUM;
			// ApertureSizeType.LARGE;
			// ApertureSizeType.HUGE;
			// ApertureSizeType.HUGER;
	public static final ShutterModelType SHUTTER_MODEL_TYPE =
			// ShutterModelType.DETECTOR_PLANE_SHUTTER;
			ShutterModelType.APERTURE_PLANE_SHUTTER;
			// ShutterModelType.FOCUS_SURFACE_SHUTTER;
	public static final LensType LENS_TYPE =	// relevant for detector-plane shutter model only
			LensType.IDEAL_LENS;
			// LensType.LENS_HOLOGRAM;
	public static final boolean IS_FOCUSSING_SPHERES_RADIUS_CUSTOM = false;
	public static final double CUSTOM_FOCUSSING_SPHERES_RADIUS = 0.005;
	public static final boolean WIDE_ANGLE = false;
	public static final boolean TEST = true;
	
	// public static Vector3D beta = new Vector3D(0.1, 0, 0.99);	// for "proper" paper
	public static Vector3D beta = new Vector3D(0.2, -0.1, 0.97);	// for SPIE paper


	/**
	 * Filename under which main saves the rendered image.
	 * Alter to save the rendered image under a different name.
	 * @return	the filename under which the main method saves the rendered image.
	 */
	public static String getFilename()
	{
		String s = "scene "
//				+(LORENTZ_WINDOW?"through Lorentz Window ":"")
				+"with camera with " + APERTURE_SIZE + " aperture size "
				+"and shutter model " + SHUTTER_MODEL_TYPE + " "
				+(SHUTTER_MODEL_TYPE == ShutterModelType.DETECTOR_PLANE_SHUTTER?"("+LENS_TYPE+") ":"")
				+(BETA_0?"at rest":("moving with beta="+beta))
				+(IS_FOCUSSING_SPHERES_RADIUS_CUSTOM?" sphere radius="+CUSTOM_FOCUSSING_SPHERES_RADIUS:"")
				+(WIDE_ANGLE?" (wide-angle)":"")
				+(TEST?" (test)":"");
//		if(!PINHOLE_CAMERA)
//		{
//			s = s+(FOCUSSED_ON_IMAGE?"image":"object");
//		}
		return s+".bmp";
	}
	
	
	/**
	 * Calculate the time when the light ray from a given position in the camera frame passes through 
	 * a given position on the entrance pupil, and then calculates the spatial part of the event of that
	 * light ray passing through the position in the camera frame, Lorentz-transformed into the scene frame.
	 * @param pointOnEntrancePupilCameraFrame	position on the entrance pupil (in the camera frame)
	 * @param positionCameraFrame	position (in the camera frame)
	 * @param shutterModel	the camera's shutter model
	 * @return	the spatial part of the event of the light ray passing through the position, in the scene frame
	 */
	public static Vector3D getPositionInSceneFrame(Vector3D pointOnEntrancePupilCameraFrame, Vector3D positionCameraFrame, ShutterModel shutterModel)
	{
		double
			positionTimeCameraFrame = 
			shutterModel.getAperturePlaneTransmissionTime(
					pointOnEntrancePupilCameraFrame,	// pointOnEntrancePupil,
					positionCameraFrame,	// pixelImagePosition,
				true	// pixelImagePositionInFront
				) -
			Vector3D.difference(positionCameraFrame, pointOnEntrancePupilCameraFrame).getLength() / LorentzTransform.c;
		
		// System.out.println("ShutterModelFocussing::getPositionInSceneFrame: positionTimeCameraFrame = "+positionTimeCameraFrame);

		return LorentzTransform.getTransformedPosition(
				positionCameraFrame,
				positionTimeCameraFrame,
				beta.getReverse()
			);
	}

	
	/**
	 * Define scene, lights, and/or camera.
	 * @return a studio, i.e. scene, lights and camera
	 */
	public static Studio createStudio()
	{
		Studio studio = new Studio();

		// the scene
		SceneObjectContainer scene = new SceneObjectContainer("the scene", null, studio);

		// start by defining the camera so that we can calculate, from the shutter model, what is in focus and then place something there
		
		// define the camera
		//
		// Note that the view direction and basis Vector3Ds of the detector are chosen such that
		// the x, y, z axes form a LEFT-handed coordinate system.
		// The reason is that, in the photo, the positive x direction is then to the right,
		// the positive y direction is upwards, and the camera looks in the positive z direction.
		int
		pixelsX = 640,
		pixelsY = 480;
		
		
		// the camera-frame scene
		EditableSceneObjectCollection cameraFrameScene = new EditableSceneObjectCollection("camera-frame scene", false, scene, studio);
//		if(LORENTZ_WINDOW)
//		{
			cameraFrameScene.addSceneObject(new EditableParametrisedPlane(
				"Lorentz window",	// description
				new Vector3D(0, 0, 0.01),	// point slightly in front of aperture plane
				beta,	// normal
				new LorentzTransformInterface(
						beta,
						CoordinateSystemType.GLOBAL_BASIS,
						0.96,	// transmission coefficient
						false	// shadow-throwing?
					),	// SurfaceProperty
				cameraFrameScene,	// parent,
				null	// studio
				));
//		}
		
		EditableRelativisticAnyFocusSurfaceCamera camera = new EditableRelativisticAnyFocusSurfaceCamera(
				"Camera",
				new Vector3D(0, 0, 0),	// centre of aperture
				new Vector3D(0, 0, 1),	// viewDirection
				new Vector3D(0, 1, 0),	// top direction vector
				(WIDE_ANGLE?120:20),	// horiontalViewAngle in degrees; 2*MyMath.rad2deg(Math.atan(2./10.)) gives same view angle as in previous version
				(BETA_0?new Vector3D(0, 0, 0):beta),	// beta
				pixelsX, pixelsY,	// logical number of pixels
				ExposureCompensationType.EC0,	// exposure compensation +0
				1000,	// maxTraceLevel
				new EditableParametrisedPlane(
						"focussing plane",
						new Vector3D(0, 0, 10),	// point on plane
						new Vector3D(0, 0, 1),	// normal to plane
						SurfaceColour.BLACK_SHINY,
						scene,
						studio
				),	// focusScene,
				cameraFrameScene,	//cameraFrameScene,
				APERTURE_SIZE,	// aperture size
				TEST?QualityType.BAD:QualityType.GREAT,	// blur quality
				TEST?QualityType.NORMAL:QualityType.GOOD	// QualityType.GREAT	// anti-aliasing quality
		);
		camera.setTransformType(TransformType.LORENTZ_TRANSFORM);

		// the reference shutter model
		DetectorPlaneShutterModel referenceShutterModel = new DetectorPlaneShutterModel(
				LensType.IDEAL_LENS,
				1,	// detector distance
				camera,
				1.12	// shutter-opening time
			);
		
		// set the camera's shutter model
		Vector3D pointOnLensAperture = new Vector3D(0, 0, 0);
		Vector3D pixelImagePosition = new Vector3D(0, 0, 10);
		double referenceShutterModelAperturePlaneTransmissionTime = referenceShutterModel.getAperturePlaneTransmissionTime(
				pointOnLensAperture,	// pointOnAperturePlane,
				pixelImagePosition,	// pixelImagePosition,
				true	// pixelImagePositionInFront
			);

		InstantShutterModel shutterModel;
		switch(SHUTTER_MODEL_TYPE)
		{
		case APERTURE_PLANE_SHUTTER:
			// create a camera with a entrance-pupil shutter model with the shutter-opening time chosen such that
			// the shutter model is equivalent to the reference shutter model
			shutterModel = new AperturePlaneShutterModel(referenceShutterModelAperturePlaneTransmissionTime);
			System.out.println("Aperture-plane shutter opening time = "+referenceShutterModelAperturePlaneTransmissionTime);
			break;
		case FOCUS_SURFACE_SHUTTER:
			// create a camera with a focus-surface shutter model with the shutter-opening time chosen such that
			// the shutter model is equivalent to the reference shutter model
			shutterModel = new FocusSurfaceShutterModel(0);
			double shutterModelEntrancePupilTransmissionTime = shutterModel.getAperturePlaneTransmissionTime(
					pointOnLensAperture,	// pointOnEntrancePupil,
					pixelImagePosition,	// pixelImagePosition,
					true	// pixelImagePositionInFront
				);
			double focusSurfaceShutterOpeningTime = referenceShutterModelAperturePlaneTransmissionTime -
					shutterModelEntrancePupilTransmissionTime;
			shutterModel.setShutterOpeningTime(focusSurfaceShutterOpeningTime);
			System.out.println("Focus-surface shutter opening time = "+focusSurfaceShutterOpeningTime);
			break;
		case DETECTOR_PLANE_SHUTTER:
		default:
			shutterModel = new DetectorPlaneShutterModel(
					LENS_TYPE,
					1,	// detector distance
					camera,
					1.12	// shutter-opening time
				);
		}
		camera.setShutterModel(shutterModel);

		// add an array of small red spheres in a surface that's in focus
		Vector3D pointOnEntrancePupilCameraFrame = new Vector3D(0, 0, 0);
	
		for(double x=-1; x<=1; x+=0.25)
			for(double y=-1; y<=1; y+=0.25)
			{
				scene.addSceneObject(new EditableScaledParametrisedSphere(
						"Sphere, centred in focussing surface",	// description,
						getPositionInSceneFrame(pointOnEntrancePupilCameraFrame, new Vector3D(x, y, 10), shutterModel),	// centre,
						(IS_FOCUSSING_SPHERES_RADIUS_CUSTOM?CUSTOM_FOCUSSING_SPHERES_RADIUS:0.025),	// radius,
						SurfaceColour.WHITER_SHINY,	// RED_SHINY,	// surfaceProperty,
						scene,	// parent, 
						studio
					));
				
				// optional: arrays of spheres just in front of and behind the focussing surface
				// (comment out if not required)
				/*
				scene.addSceneObject(new EditableScaledParametrisedSphere(
						"Sphere, in front of focussing surface",	// description,
						getPositionInSceneFrame(pointOnEntrancePupilCameraFrame, new Vector3D(x, y, 10-0.002), shutterModel),	// centre,
						(IS_FOCUSSING_SPHERES_RADIUS_CUSTOM?CUSTOM_FOCUSSING_SPHERES_RADIUS:0.025),	// radius,
						SurfaceColour.GREEN_SHINY,	// surfaceProperty,
						scene,	// parent, 
						studio
					));
				scene.addSceneObject(new EditableScaledParametrisedSphere(
						"Sphere, behind focussing surface",	// description,
						getPositionInSceneFrame(pointOnEntrancePupilCameraFrame, new Vector3D(x, y, 10+0.002), shutterModel),	// centre,
						(IS_FOCUSSING_SPHERES_RADIUS_CUSTOM?CUSTOM_FOCUSSING_SPHERES_RADIUS:0.025),	// radius,
						SurfaceColour.BLUE_SHINY,	// surfaceProperty,
						scene,	// parent, 
						studio
					));
				*/
			}
		
		// add everything else
		
		// the standard scene objects
		scene.addSceneObject(SceneObjectClass.getChequerboardFloor(scene, studio));	// the checkerboard floor
		scene.addSceneObject(SceneObjectClass.getSkySphere(scene, studio));	// the sky

		// the cylinder lattice from TIMInteractiveBits's populateSceneRelativisticEdition method
		double cylinderRadius = 0.02;

		// a cylinder lattice...
		scene.addSceneObject(new EditableCylinderLattice(
				"cylinder lattice",
				-1.5, 1.5, 4,	// x_min, x_max, no of cylinders => cylinders at x=-1.5, -0.5, +0.5, +1.5
				-1.5, 1.5, 4,	// y_min, y_max, no of cylinders => cylinders at y=-1.5, -0.5, +0.5, +1.5
				-1, 10, 12, // z_min, z_max, no of cylinders => cylinders at z=-1, 0, 1, 2, ..., 10
				cylinderRadius,
				scene,
				studio
				));		

		// Tim's head
		// scene.addSceneObject(new EditableTimHead("Tim's head", new Vector3D(0, 0, 10), scene, studio));

		// the Ninky Nonk Silhouette
		scene.addSceneObject(new EditableNinkyNonkSilhouette(scene, studio));


		studio.setScene(scene);
		studio.setLights(LightSource.getStandardLightsFromBehind());
		studio.setCamera(camera);

		return studio;
	}


	/**
	 * This method gets called when the Java application starts.
	 * 
	 * @see createStudio
	 * @author	Alasdair Hamilton, Johannes Courtial
	 */
	public static void main(final String[] args)
	{
		// open a window, and take a note of its content pane
		Container container = (new PhotoFrame()).getContentPane();

		// define scene, lights and camera
		Studio studio = createStudio();

		// do the ray tracing
		studio.takePhoto();

		// save the image
		if(SAVE) studio.savePhoto(getFilename(), "bmp");

		// display the image on the screen
		container.add(new PhotoCanvas(studio.getPhoto()));
		container.validate();
	}
}
