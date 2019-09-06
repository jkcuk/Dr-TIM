package optics.raytrace.research.diagrams;

import java.awt.*;

import math.*;
import optics.DoubleColour;
import optics.raytrace.sceneObjects.*;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.sceneObjects.transformations.Translation;
import optics.raytrace.surfaces.SurfaceColour;
import optics.raytrace.surfaces.SurfaceColourLightSourceIndependent;
import optics.raytrace.surfaces.SurfacePropertyAverage;
import optics.raytrace.surfaces.Transparent;
import optics.raytrace.cameras.*;
import optics.raytrace.cameras.PinholeCamera.ExposureCompensationType;
import optics.raytrace.core.*;
import optics.raytrace.GUI.nonInteractive.PhotoCanvas;
import optics.raytrace.GUI.nonInteractive.PhotoFrame;
import optics.raytrace.GUI.sceneObjects.EditableRayTrajectory;


/**
 * Example for running The METATOY Raytracer (TIM) as a non-interactive Java application.
 * 
 * The main method renders the image defined by createStudio(), saves it to a file
 * (whose name is given by the constant FILENAME), and displays it in a new window.
 *
 * Change the method createStudio() to change scene, lights and/or camera.
 * 
 * Change the method getFilename() to save the image under a different name.
 * 
 * Change the main method if you want the Java application to do something different altogether.
 * 
 * @author Johannes Courtial
 */
public class CurvedSurfaceImaging
{
	/**
	 * Filename under which main saves the rendered image.
	 * Alter to save the rendered image under a different name.
	 * @return	the filename under which main saves the rendered image.
	 */
	public static String getFilename()
	{
		return "CurvedSurfaceImaging.bmp";
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

		// add any other scene objects
		
		// first design a nice curved surface
		// for the moment, give this surface a surface property that ensures that rays end there
		SphericalCap curvedSurface = 
			new SphericalCap(
					"spherical cap",	// description,
					new Vector3D(0, 0, 0),	// capCentre; translated in a minute,
					new Vector3D(1, 0, 0).getWithLength(1),	// directionToFront, length = radius
					0.8,	// apertureRadius,
					false,	// closed?
					SurfaceColour.BLUE_SHINY,	// surfaceProperty,
					scene,	// parent, 
					studio	// the studio
				).transform(new Translation(new Vector3D(0, 0, 10)));
		scene.addSceneObject(curvedSurface);
		

		// a light ray with trajectory
		scene.addSceneObject(
				new EditableRayTrajectory(
						"ray trajectory",
						new Vector3D(-1, 0, 10),	// start point
						0,	// start time
						new Vector3D(1, .1, 0),	// initial direction
						0.02,	// radius
						new SurfaceColourLightSourceIndependent(DoubleColour.RED, true),
						100,	// max trace level
						false,	// reportToConsole
						scene,
						studio
				)
		);
		
		// trace the rays with trajectory through the scene
		studio.setScene(scene);
		studio.traceRaysWithTrajectory();
		
		// now remove the curved surface...
		scene.removeSceneObject(curvedSurface);
		
		// ... and add it again, but with a different surface property

		// first create that surface property, ...
		SurfacePropertyAverage greenSemiTransparent = new SurfacePropertyAverage();
		greenSemiTransparent.add(SurfaceColour.GREEN_SHINY);
		greenSemiTransparent.add(new Transparent(0.5, true));

		// ... add it to the curved surface, ...
		curvedSurface.setSurfaceProperty(greenSemiTransparent);
		curvedSurface.addElements();	// add all the elements with the new surface property
		
		// ... and add the new curved surface to the scene
		scene.addSceneObject(curvedSurface);
		
		
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

		// a camera with a non-zero aperture size (so it simulates blur)
		ApertureCamera camera = new ApertureCamera(
				"Camera",
				new Vector3D(6, 0, 0),	// centre of aperture
				new Vector3D(-.6, 0, 1),	// view direction (magnitude is distance to detector centre)
				new Vector3D(2*(double)pixelsX/pixelsY, 0, 0),	// horizontal basis Vector3D
				new Vector3D(0, -2, 0),	// vertical basis Vector3D
				pixelsX*antiAliasingFactor, pixelsY*antiAliasingFactor,	// logical number of pixels
				ExposureCompensationType.EC0,
				100,	// maxTraceLevel
				10,	// focussing distance
				0.0,	// aperture radius
				1	// rays per pixel; the more, the less noise the photo is
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
