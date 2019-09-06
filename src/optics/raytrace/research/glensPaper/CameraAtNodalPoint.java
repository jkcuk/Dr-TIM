package optics.raytrace.research.glensPaper;

import java.awt.*;

import math.*;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.surfaces.SurfaceColour;
import optics.raytrace.cameras.PinholeCamera.ExposureCompensationType;
import optics.raytrace.core.*;
import optics.raytrace.GUI.cameras.EditableRelativisticAnyFocusSurfaceCamera;
import optics.raytrace.GUI.cameras.QualityType;
import optics.raytrace.GUI.lowLevel.ApertureSizeType;
import optics.raytrace.GUI.nonInteractive.PhotoCanvas;
import optics.raytrace.GUI.nonInteractive.PhotoFrame;
import optics.raytrace.GUI.sceneObjects.EditableGlens;
import optics.raytrace.GUI.sceneObjects.EditableParametrisedPlane;
import optics.raytrace.GUI.sceneObjects.EditableSceneObjectCollection;


/**
 * Calculate the view through a glens positioned such that the camera is at its nodal point.
 * 
 * Set SHOW_GLENS to true to show the glens, or to false to hide it.
 * Set PINHOLE_CAMERA to true to simulate a pinhole camera, or to false to simulate a medium-aperture camera.
 * Set FOCUSSED_ON_IMAGE to true to focus the camera on the image of the scene produced by the glens, or to false to focus on the scene.
 * Set TEST to true to calculate the image with "bad" blur quality; false for "great" blur quality.
 * 
 * Derived from @see optics.raytrace.NonInteractiveTIM
 * 
 * @author Johannes Courtial
 */
public class CameraAtNodalPoint
{
	public static final boolean SHOW_GLENS = false;
	public static final boolean PINHOLE_CAMERA = true;
	public static final boolean FOCUSSED_ON_IMAGE = true;
	public static final boolean TEST = false;

	/**
	 * Filename under which main saves the rendered image.
	 * Alter to save the rendered image under a different name.
	 * @return	the filename under which main saves the rendered image.
	 */
	public static String getFilename()
	{
		String s = "scene "
				+(SHOW_GLENS?"through glens ":"")
				+"from nodal point "
				+(PINHOLE_CAMERA?"with pinhole camera":"with medium aperture size focussed on ");
		if(!PINHOLE_CAMERA)
		{
			s = s+(FOCUSSED_ON_IMAGE?"image":"object");
		}
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

		// add scene objects to look at, all of them touching, from behind (z>10), the plane z=10
		Util.addObjectsTouchingZ10PlaneToScene(scene);

		// add the glens
		
		// 4) Glens, Principal point & aperture centre (0, 0, 2),
		// Direction of optical axis, in positive direction (0, 0, -1), Aperture radius 1,
		// Focal length in -ve space -2, Focal length in +ve space 4, Transmission coefficient 0.96
		// (should be same as the following:
		// Glens, Principal point & aperture centre (0, 0, 2), Direction of optical axis, in positive direction (0, 0, 1),
		// Aperture radius 1, Focal length in -ve space -4, Focal length in +ve space 2)
		
		scene.addSceneObject(new EditableGlens(
			"Glens",	// description,
			new Vector3D(0, 0, 2),	// principalPoint,	// not necessarily the principal point!
			new Vector3D(0, 0, -1),	// opticalAxisDirectionPos,
			.15,	// apertureRadius,
			-2,	// focalLengthNeg,
			4,	// focalLengthPos,
			0.96,	// transmissionCoefficient,
			false,	// shadowThrowing,
			scene,	// parent, 
			studio
		), SHOW_GLENS);
		
		// define the camera
		//
		// Note that the view direction and basis Vector3Ds of the detector are chosen such that
		// the x, y, z axes form a LEFT-handed coordinate system.
		// The reason is that, in the photo, the positive x direction is then to the right,
		// the positive y direction is upwards, and the camera looks in the positive z direction.
		int
		pixelsX = 640,
		pixelsY = 480;
		
		EditableRelativisticAnyFocusSurfaceCamera camera = new EditableRelativisticAnyFocusSurfaceCamera(
				"Camera",
				new Vector3D(0, 0, 0),	// centre of aperture
				new Vector3D(0, 0, 1),	// viewDirection
				new Vector3D(0, 1, 0),	// top direction vector
				15,	// horiontalViewAngle in degrees; 2*MyMath.rad2deg(Math.atan(2./10.)) gives same view angle as in previous version
				new Vector3D(0, 0, 0),	// beta
				pixelsX, pixelsY,	// logical number of pixels
				ExposureCompensationType.EC0,	// exposure compensation +0
				1000,	// maxTraceLevel
				new EditableParametrisedPlane(
						"focussing plane",
						new Vector3D(0, 0, FOCUSSED_ON_IMAGE?-3.33:10),	// point on plane
						new Vector3D(0, 0, 1),	// normal to plane
						SurfaceColour.BLACK_SHINY,
						scene,
						studio
				),	// focusScene,
				new EditableSceneObjectCollection("camera-frame scene", false, scene, studio),	//cameraFrameScene,
				PINHOLE_CAMERA?ApertureSizeType.PINHOLE:ApertureSizeType.MEDIUM,	// aperture size
				TEST?QualityType.BAD:QualityType.GREAT,	// blur quality
				TEST?QualityType.NORMAL:QualityType.GREAT	// anti-aliasing quality
		);

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
		studio.savePhoto(getFilename(), "bmp");

		// display the image on the screen
		container.add(new PhotoCanvas(studio.getPhoto()));
		container.validate();
	}
}
