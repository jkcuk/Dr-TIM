package optics.raytrace.research.TO.extendedRochesterCloak;

import java.awt.*;
import java.text.DecimalFormat;

import math.*;
import optics.raytrace.sceneObjects.*;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.cameras.PinholeCamera.ExposureCompensationType;
import optics.raytrace.core.*;
import optics.raytrace.GUI.cameras.EditableRelativisticAnyFocusSurfaceCamera;
import optics.raytrace.GUI.cameras.QualityType;
import optics.raytrace.GUI.lowLevel.ApertureSizeType;
import optics.raytrace.GUI.nonInteractive.PhotoCanvas;
import optics.raytrace.GUI.nonInteractive.PhotoFrame;
import optics.raytrace.GUI.sceneObjects.EditableRochesterCloak;
import optics.raytrace.GUI.sceneObjects.EditableTimHead;


/**
 * Based on NonInteractiveTIMMovieMaker.
 * 
 * Calculates a movie of the view through an extended Rochester cloak, with the viewpoint changing.
 * 
 * @author Johannes Courtial
 */
public class ExtendedRochesterCloakViewpointMovieMaker
{
	/**
	 * @return	the number of frames in the movie
	 */
	public static int getNoOfFrames()
	{
		return 100;
	}
	
	/**
	 * Filename under which main saves the rendered image.
	 * Alter to save the rendered image under a different name.
	 * @return	the filename under which main saves the rendered image.
	 */
	public static String getFilename(int frame)
	{
		return "ExtendedRochesterCloak"+	// the name
		      (new DecimalFormat("000000")).format(frame)	// the number of the frame, converted into a string
		      +".bmp";	// the extension
	}
	
	/**
	 * Define scene, lights, and/or camera.
	 * @return a studio, i.e. scene, lights and camera
	 */
	public static Studio createStudio(int frame)
	{
		Studio studio = new Studio();

		// the scene
		SceneObjectContainer scene = new SceneObjectContainer("the scene", null, studio);

		// the standard scene objects
		scene.addSceneObject(SceneObjectClass.getSkySphere(scene, studio));
		scene.addSceneObject(SceneObjectClass.getChequerboardFloor(scene, studio));
				
		// add any other scene objects
		
//		// first create a non-shadow-throwing surface property for the sphere
//		
//		// a cloaked green sphere
//		scene.addSceneObject(new EditableScaledParametrisedSphere(
//				"sphere without shadow",	// description
//				new Vector3D(0, 0, 10),	// centre,
//				0.5,	// radius,
//				new Vector3D(0, 1, 0),	// pole,
//				new Vector3D(1, 0, 0),	// phi0Direction,
//				0, 1,	// sThetaMin, sThetaMax,
//				0, 1,	// sPhiMin, sPhiMax,
//				new SurfaceColour(DoubleColour.WHITE, DoubleColour.BLACK, false),	// surfaceProperty,
//				scene,	// parent, 
//				studio
//			));
		
		// Tim's head, behind the cloak
		scene.addSceneObject(new EditableTimHead(
				"Tim's head",
				new Vector3D(0, 0, 15),
				1,	// radius
				new Vector3D(0, 0, -1),	// front direction
				new Vector3D(0, 1, 0),	// top direction
				new Vector3D(1, 0, 0),	// right direction
				scene,
				studio
			));

		// a cloak
		scene.addSceneObject(
			new EditableRochesterCloak(
				scene,	// parent,
				studio
			));


		// define the camera
		//
		// Note that the view direction and basis Vector3Ds of the detector are chosen such that
		// the x, y, z axes form a LEFT-handed coordinate system.
		// The reason is that, in the photo, the positive x direction is then to the right,
		// the positive y direction is upwards, and the camera looks in the positive z direction.
		int
		pixelsX = 640,
		pixelsY = 480,
		antiAliasingFactor = 1;
		// If antiAliasingFactor is set to N, the image is calculated at resolution
		// N*pixelsX x N*pixelsY.

		
		SceneObject focusScene = new Plane(
				"focussing plane",
				new Vector3D(0, 0, 10),	// point on plane
				new Vector3D(0, 0, 1),	// normal to plane
				(SurfaceProperty)null,
				null,	// parent
				Studio.NULL_STUDIO
		);

		double angle = frame * 0.5*Math.PI / getNoOfFrames();
		
		EditableRelativisticAnyFocusSurfaceCamera camera = new EditableRelativisticAnyFocusSurfaceCamera(
				"Camera",
				new Vector3D(0.001, 10*Math.sin(angle), 10-10*Math.cos(angle)),	// centre of aperture
				new Vector3D(0, -Math.sin(angle), Math.cos(angle)),	// viewDirection
				new Vector3D(0, 1, 0),	// top direction vector
				25,	// horiontalViewAngle in degrees; 2*MyMath.rad2deg(Math.atan(2./10.)) gives same view angle as in previous version
				new Vector3D(0, 0, 0),	// beta
				pixelsX*antiAliasingFactor, pixelsY*antiAliasingFactor,	// logical number of pixels
				ExposureCompensationType.EC0,	// exposure compensation +0
				100,	// maxTraceLevel
				focusScene,
				null,	// cameraFrameScene,
				ApertureSizeType.PINHOLE,	// aperture size
				QualityType.RUBBISH,	// blur quality
				QualityType.NORMAL	// anti-aliasing quality
		);

		
//		// a camera with a non-zero aperture size (so it simulates blur)
//		ApertureCamera camera = new ApertureCamera(
//				"Camera",
//				new Vector3D(0, 0, 0),	// centre of aperture
//				new Vector3D(0, 0, 1),	// view direction (magnitude is distance to detector centre)
//				new Vector3D(4*(double)pixelsX/(double)pixelsY, 0, 0),	// horizontal basis Vector3D
//				new Vector3D(0, -4, 0),	// vertical basis Vector3D
//				pixelsX*antiAliasingFactor, pixelsY*antiAliasingFactor,	// logical number of pixels
//				ExposureCompensationType.EC0,
//				100,	// maxTraceLevel
//				10,	// focussing distance
//				0.0,	// aperture radius
//				1	// rays per pixel; the more, the less noise the photo is
//		);

		studio.setScene(scene);
		studio.setLights(LightSource.getStandardLightsFromBehind());
		studio.setCamera(camera);

		return studio;
	}

	/**
	 * This method gets called when the Java application starts.
	 * 
	 * @see createStudio
	 * @author	Johannes Courtial
	 */
	public static void main(final String[] args)
	{
		// open a window, and take a note of its content pane
		Container container = (new PhotoFrame()).getContentPane();
		
		for(int frame = 0; frame <= getNoOfFrames(); frame++)
		{
			// define scene, lights and camera
			Studio studio = createStudio(frame);

			// do the ray tracing
			studio.takePhoto();

			// save the image
			studio.savePhoto(getFilename(frame), "bmp");

			// display the image on the screen
			container.removeAll();
			container.add(new PhotoCanvas(studio.getPhoto()));
			container.validate();
		}
	}
}
