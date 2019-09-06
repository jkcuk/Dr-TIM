package optics.raytrace.research.LensStar;

import java.awt.*;
import java.text.DecimalFormat;

import math.*;
import optics.raytrace.sceneObjects.*;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.surfaces.SemiTransparent;
import optics.raytrace.surfaces.SurfaceColourLightSourceIndependent;
import optics.raytrace.cameras.PinholeCamera.ExposureCompensationType;
import optics.raytrace.core.*;
import optics.DoubleColour;
import optics.raytrace.GUI.cameras.EditableRelativisticAnyFocusSurfaceCamera;
import optics.raytrace.GUI.cameras.QualityType;
import optics.raytrace.GUI.lowLevel.ApertureSizeType;
import optics.raytrace.GUI.nonInteractive.PhotoCanvas;
import optics.raytrace.GUI.nonInteractive.PhotoFrame;
import optics.raytrace.GUI.sceneObjects.EditableRayTrajectory;
import optics.raytrace.GUI.sceneObjects.EditableLensStar;


/**
 * Based on NonInteractiveTIMMovieMaker.
 * 
 * Calculates a movie of the ray trajectory through a star of lenses, with the trajectory changing.
 * 
 * @author Johannes Courtial
 */
public class LensStarTrajectoryPlotter
{
	/**
	 * @return	the number of frames in the movie
	 */
	public static int getNoOfFrames()
	{
		return 10;
	}
	
	/**
	 * Filename under which main saves the rendered image.
	 * Alter to save the rendered image under a different name.
	 * @return	the filename under which main saves the rendered image.
	 */
	public static String getFilename(int frame)
	{
		return "StarOfLensesTrajectory"+	// the name
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
		// scene.addSceneObject(SceneObjectClass.getChequerboardFloor(scene, studio));
				
		// add any other scene objects
		
		// Tim's head, behind the cloak
//		scene.addSceneObject(new EditableTimHead(
//				"Tim's head",
//				new Vector3D(0, 0, 15),
//				scene,
//				studio
//			));

		// the star of lenses
		EditableLensStar starOfLenses = new EditableLensStar(
				"Star of lenses",	// description
				5,	// numberOfLenses
				true,	// show all lenses
				-1,	// number of shown lenses; -1 for all
				0,	// start index of shown lenses
				1.0,	// focalLength
				0,	// principalPointDistance
				new Vector3D(0, 0, 10),	// centre
				new Vector3D(0, 1, 0),	// intersectionDirection
				new Vector3D(1, 0, 10),	// pointOnLens1
				MyMath.HUGE,	// starRadius,
				2,	// starLength,
				0.7,	// lensTransmissionCoefficient
				true,	// showEdges,
				0.01,	// edgeRadius
				SemiTransparent.RED_SHINY_SEMITRANSPARENT,	// edgeSurfaceProperty
				scene,	// parent,
				studio
			);
		scene.addSceneObject(starOfLenses);
		
		// double angle = 0.375*Math.PI;
				
		scene.addSceneObject(new EditableRayTrajectory(
						"ray trajectory",
						new Vector3D(-0.5-2.0*frame/getNoOfFrames(), 0, 10),	// start point
						0,	// start time
						new Vector3D(0, 0, 1),	// initial direction
						0.01,	// radius
						new SurfaceColourLightSourceIndependent(DoubleColour.GREEN, true),
						100,	// max trace level
						true,	// reportToConsole
						scene,
						studio
				)
			);
		
		studio.setScene(scene);

		// trace the rays with trajectory through the scene
		studio.traceRaysWithTrajectory();

		// scene.removeSceneObject(starOfLenses);
		
		// make the lenses effectively plane bits of glass
		starOfLenses.setFocalLength(MyMath.HUGE);
		starOfLenses.populateSceneObjectCollection();


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
		
		EditableRelativisticAnyFocusSurfaceCamera camera = new EditableRelativisticAnyFocusSurfaceCamera(
				"Camera",
				new Vector3D(2, 3, 0),	// centre of aperture
				new Vector3D(-.2, -.3, 1),	// viewDirection
				new Vector3D(0, 1, 0),	// top direction vector
				20,	// horiontalViewAngle in degrees; 2*MyMath.rad2deg(Math.atan(2./10.)) gives same view angle as in previous version
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
		
		for(int frame = 0; frame < getNoOfFrames(); frame++)
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
