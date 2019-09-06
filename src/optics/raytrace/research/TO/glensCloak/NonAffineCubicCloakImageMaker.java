package optics.raytrace.research.TO.glensCloak;

import java.awt.*;
import java.text.DecimalFormat;

import math.*;
import optics.raytrace.sceneObjects.*;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.surfaces.SurfaceColour;
import optics.raytrace.cameras.PinholeCamera.ExposureCompensationType;
import optics.raytrace.core.*;
import optics.DoubleColour;
import optics.raytrace.GUI.cameras.EditableRelativisticAnyFocusSurfaceCamera;
import optics.raytrace.GUI.cameras.QualityType;
import optics.raytrace.GUI.lowLevel.ApertureSizeType;
import optics.raytrace.GUI.nonInteractive.PhotoCanvas;
import optics.raytrace.GUI.nonInteractive.PhotoFrame;
import optics.raytrace.GUI.sceneObjects.EditableBoxCloak;
import optics.raytrace.GUI.sceneObjects.EditableScaledParametrisedSphere;
import optics.raytrace.GUI.sceneObjects.EditableSceneObjectCollection;
import optics.raytrace.GUI.sceneObjects.EditableTimHead;
import optics.raytrace.GUI.sceneObjects.boxCloaks.BoxCloakType;


/**
 * Based on NonInteractiveTIMMovieMaker.
 * 
 * Calculates the view through two different (non-affine) cubic cloaks.
 * 
 * @author Johannes Courtial
 */
public class NonAffineCubicCloakImageMaker
{
	/**
	 * @return	the number of frames in the movie
	 */
	public static int getNoOfFrames()
	{
		return 5;
	}
	
	/**
	 * Filename under which main saves the rendered image.
	 * Alter to save the rendered image under a different name.
	 * @return	the filename under which main saves the rendered image.
	 */
	public static String getFilename(int frame)
	{
		return "NonAffineCubicCloaks_"+	// the name
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
		
		// first create a non-shadow-throwing surface property for the sphere
		
		// a cloaked green sphere
		scene.addSceneObject(new EditableScaledParametrisedSphere(
				"sphere without shadow",	// description
				new Vector3D(0, 0, 10),	// centre,
				0.5,	// radius,
				new Vector3D(0, 1, 0),	// pole,
				new Vector3D(1, 0, 0),	// phi0Direction,
				0, 1,	// sThetaMin, sThetaMax,
				0, 1,	// sPhiMin, sPhiMax,
				new SurfaceColour(DoubleColour.WHITE, DoubleColour.BLACK, false),	// surfaceProperty,
				scene,	// parent, 
				studio
			));
		
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

		double innerVolumeSizeEM;
		boolean showFrames;
		Vector3D apertureCentre;

		// a cloak
		switch(frame)
		{
		case 1:
			// show the cylinder frame only
			innerVolumeSizeEM = 0.4;
			showFrames = true;
			// standard side-on view
			apertureCentre = new Vector3D(2.3, 3.1415, 0);
			break;
		case 2:
			// cloak with central EM-space volume half-size
			innerVolumeSizeEM = 0.4;
			showFrames = false;
			// standard side-on view
			apertureCentre = new Vector3D(2.3, 3.1415, 0);
			break;
		case 3:
			// cloak with central EM-space volume 10% of the size of the corresponding physical-space volume
			innerVolumeSizeEM = 0.08;
			showFrames = false;
			// standard side-on view
			apertureCentre = new Vector3D(2.3, 3.1415, 0);
			break;
		case 4:
			// cloak with central EM-space volume 10% of the size of the corresponding physical-space volume
			innerVolumeSizeEM = 0.08;
			showFrames = false;
			// standard side-on view
			apertureCentre = Vector3D.difference(
					new Vector3D(0, 0, 10),
					(new Vector3D(-1.2, -0.4, 2.1)).getWithLength(Math.sqrt(2.3*2.3+3.1415*3.1415+10*10))
				);
			break;
		case 5:
		default:
			// cloak with central EM-space volume 10% of the size of the corresponding physical-space volume
			innerVolumeSizeEM = 0.4;
			showFrames = false;
			// standard side-on view
			apertureCentre = Vector3D.difference(
					new Vector3D(0, 0, 10),
					(new Vector3D(-1.2, -0.4, 2.1)).getWithLength(Math.sqrt(2.3*2.3+3.1415*3.1415+10*10))
				);
			break;
		}
		System.out.println("aperture centre: " + apertureCentre);
		
		EditableBoxCloak cloak = new EditableBoxCloak(
						"Box cloak",
						new Vector3D(0, 0, 10),	// centre
						new Vector3D(0, 0, -1),	// front direction
						new Vector3D(1, 0, 0),	// right direction
						new Vector3D(0, 1, 0),	// top direction
						2.0,	// side length
						innerVolumeSizeEM,	// inner volume size in EM space
						0.8,	// inner volume size in physical space
						0.8,	// interface transmission coefficient
						BoxCloakType.CUBIC,	// cloak type
						showFrames,	// show frames
						0.01,	// frame radius / side length
						SurfaceColour.RED_SHINY,	// frame surface property
						false,	// show placeholder surfaces
						scene,
						studio
			);
		if(frame == 1)
		{
			// hide the windows
			cloak.setSceneObjectVisible(cloak.getWindows(), false);
		}
		scene.addSceneObject(cloak);


		// define the camera
		//
		// Note that the view direction and basis Vector3Ds of the detector are chosen such that
		// the x, y, z axes form a LEFT-handed coordinate system.
		// The reason is that, in the photo, the positive x direction is then to the right,
		// the positive y direction is upwards, and the camera looks in the positive z direction.
		int
		pixelsX = 640,
		pixelsY = 480;

		

		Vector3D
			viewDirection = Vector3D.difference(
					new Vector3D(0, 0, 10),	// cloak centre
					apertureCentre
				);
		
		SceneObject focusScene = new Plane(
				"focussing plane",
				new Vector3D(0, 0, 10),	// point on plane; cloak centre
				viewDirection,	// normal to plane
				(SurfaceProperty)null,
				null,	// parent
				Studio.NULL_STUDIO
		);

		EditableRelativisticAnyFocusSurfaceCamera camera = new EditableRelativisticAnyFocusSurfaceCamera(
				"Camera",
				apertureCentre,	// centre of aperture
				viewDirection,	// viewDirection
				new Vector3D(0, 1, 0),	// top direction vector
				22,	// horiontalViewAngle in degrees; 2*MyMath.rad2deg(Math.atan(2./10.)) gives same view angle as in previous version
				new Vector3D(0, 0, 0),	// beta
				pixelsX, pixelsY,	// logical number of pixels
				ExposureCompensationType.EC0,	// exposure compensation +0
				100,	// maxTraceLevel
				focusScene,
				new EditableSceneObjectCollection("camera-frame scene", false, null, studio),	// cameraFrameScene,
				ApertureSizeType.MEDIUM,	// aperture size
				QualityType.GREAT,	// blur quality
				QualityType.GOOD	// anti-aliasing quality
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
		
		for(int frame = 5; frame <= getNoOfFrames(); frame++)
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
