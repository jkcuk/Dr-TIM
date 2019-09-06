package optics.raytrace.research.RelativisticPhotography;

import java.awt.*;

import math.*;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.surfaces.SurfaceColour;
import optics.raytrace.cameras.PinholeCamera.ExposureCompensationType;
import optics.raytrace.cameras.shutterModels.FixedPointSurfaceShutterModel;
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
import optics.raytrace.GUI.sceneObjects.EditableSceneObjectCollection;


/**
 * 
 * Derived from @see optics.raytrace.NonInteractiveTIM
 * 
 * @author Johannes Courtial
 */
public class FixedPointSurfaceShutterModelTest
{
	public static final boolean SAVE = true;
	
	public static final boolean BETA_0 = true;
	// public static final boolean LORENTZ_WINDOW = true;
	public static final ApertureSizeType APERTURE_SIZE = 
			// ApertureSizeType.PINHOLE;
			// ApertureSizeType.SMALL;
			ApertureSizeType.MEDIUM;
			// ApertureSizeType.LARGE;
			// ApertureSizeType.HUGE;
			// ApertureSizeType.HUGER;
//	public static final boolean IS_FOCUSSING_SPHERES_RADIUS_CUSTOM = true;
	public static final double FOCUSSING_DISTANCE = 8;
	public static final boolean TEST = false;
	
	public static Vector3D beta = new Vector3D(0.1, 0, 0.99);


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
				+"focussed at z="+FOCUSSING_DISTANCE + " "
				+"and focus-surface fixed shutter model "
				+(BETA_0?"at rest":("moving with beta="+beta))
				+(TEST?" (test)":"");
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
//			cameraFrameScene.addSceneObject(new EditableParametrisedPlane(
//				"Lorentz window",	// description
//				shutterPlanePoint,	// pointOnPlane
//				beta,	// normal
//				new LorentzTransformInterface(
//						beta,
//						CoordinateSystemType.GLOBAL_BASIS,
//						0.96,	// transmission coefficient
//						false	// shadow-throwing?
//					),	// SurfaceProperty
//				cameraFrameScene,	// parent,
//				null	// studio
//				));
//		}
		
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
						new Vector3D(0, 0, FOCUSSING_DISTANCE),	// point on plane
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
		FixedPointSurfaceShutterModel shutterModel = new FixedPointSurfaceShutterModel(beta);		
		camera.setShutterModel(shutterModel);
		
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
