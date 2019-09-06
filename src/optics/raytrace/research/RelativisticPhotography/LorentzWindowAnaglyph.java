package optics.raytrace.research.RelativisticPhotography;

import java.awt.*;

import math.*;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.studioInitialisation.SurroundLatticeInitialisation;
import optics.raytrace.surfaces.LorentzTransformInterface;
import optics.raytrace.surfaces.SurfaceColour;
import optics.raytrace.utility.CoordinateSystems.CoordinateSystemType;
import optics.raytrace.cameras.PinholeCamera.ExposureCompensationType;
import optics.raytrace.cameras.shutterModels.ArbitraryPlaneShutterModel;
import optics.raytrace.cameras.RelativisticAnyFocusSurfaceCamera.TransformType;
import optics.raytrace.core.*;
import optics.raytrace.TIMInteractiveBits;
import optics.raytrace.GUI.cameras.EditableRelativisticAnaglyphCamera;
import optics.raytrace.GUI.cameras.QualityType;
import optics.raytrace.GUI.lowLevel.ApertureSizeType;
import optics.raytrace.GUI.nonInteractive.PhotoCanvas;
import optics.raytrace.GUI.nonInteractive.PhotoFrame;
import optics.raytrace.GUI.sceneObjects.EditableNinkyNonkSilhouette;
import optics.raytrace.GUI.sceneObjects.EditableParametrisedPlane;
import optics.raytrace.GUI.sceneObjects.EditableSceneObjectCollection;


/**
 * 
 * Derived from @see optics.raytrace.NonInteractiveTIM
 * 
 * @author Johannes Courtial
 */
public class LorentzWindowAnaglyph
{
	public static final boolean SAVE = false;
	
	public static final boolean BETA_0 = false;
	public static final boolean LORENTZ_WINDOW = false;
	public static final boolean TEST = true;
	
	public static Vector3D beta = new Vector3D(0.1, 0, 0.99);


	/**
	 * Filename under which main saves the rendered image.
	 * Alter to save the rendered image under a different name.
	 * @return	the filename under which main saves the rendered image.
	 */
	public static String getFilename()
	{
		String s = "anaglyph of scene "
				+(LORENTZ_WINDOW?"through Lorentz Window ":"")
				+(BETA_0?"at rest":("moving with beta="+beta));
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

		new SurroundLatticeInitialisation().initialiseSceneAndLights(scene, studio);

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
		
		Vector3D shutterPlanePoint = new Vector3D(0.111049, 0., 1.09939);
		
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
				
		double anaglyphViewDistance = 5;
		EditableRelativisticAnaglyphCamera camera = new EditableRelativisticAnaglyphCamera(
				"Anaglyph camera",
				new Vector3D(0, 0, 0),	// betweenTheEyes: middle between two eyes
				new Vector3D(0, 0, anaglyphViewDistance),	// centre of view: the point in the centre of both eyes' field of view
				new Vector3D(2*anaglyphViewDistance*Math.tan(MyMath.deg2rad(TIMInteractiveBits.HORIZONTAL_VIEW_ANGLE/2)), 0, 0),	// horizontal span vector
				new Vector3D(0.4, 0, 0),	// eyeSeparation: separation between the eyes
				(BETA_0?new Vector3D(0, 0, 0):beta),	// beta
				pixelsX, pixelsY,	// logical number of pixels
				ExposureCompensationType.EC0,
				1000,	// maxTraceLevel
				new EditableParametrisedPlane(
						"focussing plane",
						new Vector3D(0, 0, 9.5),	// point on plane
						new Vector3D(0, 0, 1),	// normal to plane
						SurfaceColour.BLACK_SHINY,
						scene,
						studio
				),	// focusScene,
				cameraFrameScene,
				ApertureSizeType.PINHOLE,	// aperture size
				false,	// colour
				TEST?QualityType.BAD:QualityType.GREAT,	// blur quality
				TEST?QualityType.NORMAL:QualityType.GOOD	// anti-aliasing quality
		);
		
		camera.setShutterModel(new ArbitraryPlaneShutterModel(
				shutterPlanePoint,	// point in shutter plane
				beta,	// normal to shutter plane
				-1	// shutter-opening time
			));
		camera.setTransformType(TransformType.LORENTZ_TRANSFORM);
		camera.setupCameras();

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
