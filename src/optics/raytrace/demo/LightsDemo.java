package optics.raytrace.demo;

import java.awt.Container;

import math.*;
import optics.DoubleColour;
import optics.raytrace.lights.LightSourceContainer;
import optics.raytrace.lights.PhongLightSource;
import optics.raytrace.surfaces.*;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.GUI.nonInteractive.PhotoCanvas;
import optics.raytrace.GUI.nonInteractive.PhotoFrame;
import optics.raytrace.GUI.sceneObjects.EditableParametrisedPlane;
import optics.raytrace.GUI.sceneObjects.EditableScaledParametrisedSphere;
import optics.raytrace.cameras.*;
import optics.raytrace.cameras.PinholeCamera.ExposureCompensationType;
import optics.raytrace.core.*;


/**
 * Lights demo
 * 
 * Note that in Project>Properties (or alternatively Run>Run Configurations...) the working
 * directory (which can be found in the arguments tab) has to be set to the directory
 * containing all the images, normally
 * ${workspace_loc:TIM/demo/}
 * 
 * Adapted from NonInteractiveTIM.java
 */
public class LightsDemo
{
	/**
	 * Filename under which main saves the rendered image.
	 * Alter to save the rendered image under a different name.
	 * @return	the filename under which main saves the rendered image.
	 */
	public static String getFilename()
	{
		return "LightsDemoTemp.bmp";
	}

	/**
	 * Define scene, lights, and/or camera.
	 * @return the studio, i.e. scene, lights and camera
	 */
	public static Studio createStudio()
	{		
		Studio studio = new Studio();
		
		// the scene
		SceneObjectContainer scene = new SceneObjectContainer("the scene", null, studio);

		// the standard scene objects
		scene.addSceneObject(SceneObjectClass.getSkySphere(scene, studio));	// the sky
		// scene.addSceneObject(SceneObjectClass.getChequerboardFloor(scene, studio));	// the checkerboard floor
		scene.addSceneObject(new EditableParametrisedPlane(
					"chequerboard floor", 
					new Vector3D(0, -1, 0),	// point on plane
					new Vector3D(1, 0, 0),	// Vector3D 1 that spans plane
					new Vector3D(0, 0, 1),	// Vector3D 2 that spans plane
					// true,	// shadow-throwing
					new SurfaceTiling(SurfaceColour.GREY80_SHINY , SurfaceColour.WHITE_SHINY, 1, 1),
					scene,
					studio
			));

		//
		// add any other scene objects
		//
		
		// make a nice surface
		SurfacePropertyAverage surface = new SurfacePropertyAverage(
				SurfaceColour.BLUE_SHINY,
				new Reflective(0.5, true)
			);
		
		// first, something to look at
		scene.addSceneObject(new EditableScaledParametrisedSphere(
				"sphere",
				new Vector3D(0, 0, 10),	// centre
				1,	// radius
				new Vector3D(0.5, 0.5, 1),	// pole
				new Vector3D(1,0,0),	// phi0Direction
				0, Math.PI,	// sThetaMin, sThetaMax
				-Math.PI, Math.PI,	// sPhiMin, sPhiMax
				surface,	// surfaceProperty
				scene,	// parent
				studio
		));

		// define the camera
		//
		// Note that the view direction and basis Vector3Ds of the detector are chosen such that
		// the x, y, z axes form a LEFT-handed coordinate system.
		// The reason is that, in the photo, the positive x direction is then to the right,
		// the positive y direction is upwards, and the camera looks in the positive z direction.
		int
		pixelsX = 640*4,
		pixelsY = 480*4,
		antiAliasingFactor = 1;

		// a camera with a non-zero aperture size (so it simulates blur)
		ApertureCamera camera = new ApertureCamera(
				"Camera",
				new Vector3D(0, 2, 0),	// centre of aperture
				new Vector3D(0, -0.7, 3),	// view direction (magnitude is distance to detector centre)
				new Vector3D(-(double)pixelsX/pixelsY, 0, 0),	// horizontal basis Vector3D
				new Vector3D(0, 1, 0),	// vertical basis Vector3D
				pixelsX*antiAliasingFactor, pixelsY*antiAliasingFactor,	// logical number of pixels
				ExposureCompensationType.EC0,
				100,	// maxTraceLevel
				10,	// focussing distance
				0.0,	// aperture radius
				1	// rays per pixel; the more, the less noise the photo is
		);
		
		LightSourceContainer lights = new LightSourceContainer("lights");
		// lights.add(new AmbientLight("background light", DoubleColour.GREY20));
		lights.add(new PhongLightSource("green point light souce", new Vector3D(50*Math.cos(0),100,10+50*Math.sin(0)), DoubleColour.GREEN, DoubleColour.GREEN, 40.));
		lights.add(new PhongLightSource("red point light souce", new Vector3D(50*Math.cos(2*Math.PI/3.),100,10+50*Math.sin(2*Math.PI/3.)), DoubleColour.RED, DoubleColour.RED, 40.));
		lights.add(new PhongLightSource("blue point light souce", new Vector3D(50*Math.cos(2*Math.PI*2./3.),100,10+50*Math.sin(2*Math.PI*2./3.)), DoubleColour.BLUE, DoubleColour.BLUE, 40.));
		
		studio.setScene(scene);
		studio.setCamera(camera);		
		studio.setLights(lights);
		
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
