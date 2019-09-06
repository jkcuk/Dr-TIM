package optics.raytrace.research.RelativisticPhotography;

import java.awt.*;

import math.*;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.surfaces.LorentzTransformInterface;
import optics.raytrace.surfaces.SurfaceColour;
import optics.raytrace.utility.CoordinateSystems.CoordinateSystemType;
import optics.raytrace.cameras.PinholeCamera.ExposureCompensationType;
import optics.raytrace.cameras.shutterModels.ArbitraryPlaneShutterModel;
import optics.raytrace.cameras.shutterModels.DetectorPlaneShutterModel;
import optics.raytrace.cameras.shutterModels.LensType;
import optics.raytrace.cameras.shutterModels.ShutterModelType;
import optics.raytrace.cameras.RelativisticAnyFocusSurfaceCamera.TransformType;
import optics.raytrace.core.*;
import optics.raytrace.GUI.cameras.EditableRelativisticAnyFocusSurfaceCamera;
import optics.raytrace.GUI.cameras.QualityType;
import optics.raytrace.GUI.lowLevel.ApertureSizeType;
import optics.raytrace.GUI.nonInteractive.PhotoCanvas;
import optics.raytrace.GUI.nonInteractive.PhotoFrame;
import optics.raytrace.GUI.sceneObjects.EditableNinkyNonkSilhouette;
import optics.raytrace.GUI.sceneObjects.EditableParametrisedPlane;
import optics.raytrace.GUI.sceneObjects.EditableSceneObjectCollection;
import optics.raytrace.GUI.sceneObjects.EditableTimHead;


/**
 * 
 * Derived from @see optics.raytrace.NonInteractiveTIM
 * 
 * @author Johannes Courtial
 */
public class LorentzWindowDistortionCancellation
{
	public static final boolean SAVE = true;
	
	public static final boolean BETA_0 = false;
	public static final ShutterModelType SHUTTER_MODEL_TYPE =
			ShutterModelType.ARBITRARY_PLANE_SHUTTER;
			// ShutterModelType.DETECTOR_PLANE_SHUTTER;
	public static final boolean LORENTZ_WINDOW = false;
	public static final boolean PINHOLE_CAMERA = false;
	public static final boolean TEST = true;
	
	public static Vector3D beta = new Vector3D(0.1, 0, 0.99);	// for "proper" paper
	// public static Vector3D beta = new Vector3D(0.2, -0.1, 0.97);	// for SPIE paper

	/**
	 * Filename under which main saves the rendered image.
	 * Alter to save the rendered image under a different name.
	 * @return	the filename under which main saves the rendered image.
	 */
	public static String getFilename()
	{
		String s = "scene "
				+(LORENTZ_WINDOW?"through Lorentz Window ":"")
				+(PINHOLE_CAMERA?"with pinhole camera ":"with camera with small aperture size ")
				+((SHUTTER_MODEL_TYPE!=ShutterModelType.ARBITRARY_PLANE_SHUTTER)?"with shutter model "+SHUTTER_MODEL_TYPE+" ":"")
				+(BETA_0?"at rest":("moving with beta="+beta));
//		if(!PINHOLE_CAMERA)
//		{
//			s = s+(FOCUSSED_ON_IMAGE?"image":"object");
//		}
		return s+".bmp";
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

		// the standard scene objects
		scene.addSceneObject(SceneObjectClass.getChequerboardFloor(scene, studio));	// the checkerboard floor
		scene.addSceneObject(SceneObjectClass.getSkySphere(scene, studio));	// the sky

		// Tim's head
		scene.addSceneObject(new EditableTimHead(
				"Tim's head",
				new Vector3D(0, 0, 10),
				1,	// radius
				new Vector3D(0, 0, -1),	// front direction
				new Vector3D(0, 1, 0),	// top direction
				new Vector3D(1, 0, 0),	// right direction
				scene, studio));

		// the Ninky Nonk Silhouette
		scene.addSceneObject(new EditableNinkyNonkSilhouette(scene, studio));

	
		// define the camera
		//
		// Note that the view direction and basis Vector3Ds of the detector are chosen such that
		// the x, y, z axes form a LEFT-handed coordinate system.
		// The reason is that, in the photo, the positive x direction is then to the right,
		// the positive y direction is upwards, and the camera looks in the positive z direction.
		int
		pixelsX = 640,
		pixelsY = 480;
		
		// The shutter plane is chosen such that, for some shutter opening time all points in the shutter plane are
		// spatial fixed points of the Lorentz transformation.
		// Note that the shutter plane is stationary in the camera frame, so when transforming into the scene frame
		// the relevant beta' is the velocity of the *scene* in the camera frame divided by c, which is
		// related to beta (the velocity of the camera in the scene frame) through beta' = -beta
		
		// calculated in Mathematics notebook LorentzWindowSimulations.nb
		Vector3D shutterPlanePoint = new Vector3D(0.111, 0, 1.1);	// more precisely new Vector3D(0.111049, 0., 1.09939);
		
		// the camera-frame scene
		EditableSceneObjectCollection cameraFrameScene = new EditableSceneObjectCollection("camera-frame scene", false, scene, studio);
		if(LORENTZ_WINDOW)
		{
			cameraFrameScene.addSceneObject(new EditableParametrisedPlane(
				"Lorentz window",	// description
				shutterPlanePoint,	// pointOnPlane
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
		}
		
		EditableRelativisticAnyFocusSurfaceCamera camera = new EditableRelativisticAnyFocusSurfaceCamera(
				"Camera",
				new Vector3D(0, 0, 0),	// centre of aperture
				new Vector3D(0, 0, 1),	// viewDirection
				new Vector3D(0, 1, 0),	// top direction vector
				20,	// horiontalViewAngle in degrees; 2*MyMath.rad2deg(Math.atan(2./10.)) gives same view angle as in previous version
				(BETA_0?new Vector3D(0, 0, 0):beta),	// beta
				pixelsX, pixelsY,	// logical number of pixels
				ExposureCompensationType.EC0,	// exposure compensation +0
				1000,	// maxTraceLevel
				new EditableParametrisedPlane(
						"focussing plane",
						new Vector3D(0, 0, 9.5),	// point on plane
						new Vector3D(0, 0, 1),	// normal to plane
						SurfaceColour.BLACK_SHINY,
						scene,
						studio
				),	// focusScene,
				cameraFrameScene,	//cameraFrameScene,
				PINHOLE_CAMERA?ApertureSizeType.PINHOLE:ApertureSizeType.SMALL,	// aperture size
				TEST?QualityType.BAD:QualityType.GREAT,	// blur quality
				TEST?QualityType.NORMAL:QualityType.GOOD	// QualityType.GREAT	// anti-aliasing quality
		);
		
		switch(SHUTTER_MODEL_TYPE)
		{
		case DETECTOR_PLANE_SHUTTER:
			// calculate the shutter-opening time such that the photo ray through the aperture centre in the forward direction
			// has the same timing as in the arbitrary-plane-shutter model
			
			// calculate the straight-ahead position in the shutter plane
			Vector3D straightAheadPointOnShutterPlane = Geometry.uniqueLinePlaneIntersection(
					new Vector3D(0, 0, 0),	// centre of aperture = point on line
					new Vector3D(0, 0, 1),	// straight-forward direction = directionOfLine,
					shutterPlanePoint,	// pointOnPlane
					beta	// normalToPlane
				);

			camera.setShutterModel(new DetectorPlaneShutterModel(
					LensType.IDEAL_LENS,
					1,	// detector distance
					camera,
					-1 +
					(
							// distance from the straight-ahead point on the shutter plane to the aperture centre
							Vector3D.difference(straightAheadPointOnShutterPlane, new Vector3D(0, 0, 0)).getLength() +
							1	// camera distance
					)/LorentzTransform.c
					// this shutter time makes this detector-plane shutter model as equivalent as possible
					// with the arbitrary-plane shutter model below
				));
			break;
		case ARBITRARY_PLANE_SHUTTER:
		default:
			camera.setShutterModel(new ArbitraryPlaneShutterModel(
					shutterPlanePoint,	// point in shutter plane
					beta,	// normal to shutter plane
					-1	// shutter-opening time
				));
		}
		camera.setTransformType(TransformType.LORENTZ_TRANSFORM);


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
