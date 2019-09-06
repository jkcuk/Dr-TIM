package optics.raytrace.demo;

import java.awt.Container;

import math.*;
import optics.DoubleColour;
import optics.raytrace.surfaces.*;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.GUI.nonInteractive.PhotoCanvas;
import optics.raytrace.GUI.nonInteractive.PhotoFrame;
import optics.raytrace.GUI.sceneObjects.EditableRayTrajectory;
import optics.raytrace.GUI.sceneObjects.EditableScaledParametrisedCentredParallelogram;
import optics.raytrace.cameras.*;
import optics.raytrace.cameras.PinholeCamera.ExposureCompensationType;
import optics.raytrace.core.*;


/**
 * Ray-trajectory demo.
 * Allows a light ray to be traced through an optical system and its trajectory then visualised.
 * 
 * Note that in Project>Properties (or alternatively Run>Run Configurations...) the working
 * directory (which can be found in the arguments tab) has to be set to the directory
 * containing all the images, normally
 * ${workspace_loc:TIM/demo/}
 * 
 * Adapted from RayTraceJavaApplication.java
 */
public class RayTrajectoryDemo
{
	/**
	 * Filename under which main saves the rendered image.
	 */
	private static final String FILENAME = "RayTrajectoryDemoTemp.bmp";

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
		scene.addSceneObject(SceneObjectClass.getChequerboardFloor(scene, studio));	// the checkerboard floor

		//
		// add any other scene objects
		//
		
		// two opposing mirrors
		scene.addSceneObject(new EditableScaledParametrisedCentredParallelogram(
				"bottom mirror",
				new Vector3D(0, -0.99, 100),	// centre
				new Vector3D(0, 0, 200),	// width vector
				new Vector3D(4, 0, 0),	// height vector
				new Reflective(0.8, true),
				scene,
				studio
		));
		scene.addSceneObject(new EditableScaledParametrisedCentredParallelogram(
				"top mirror",
				new Vector3D(0, 2, 100),	// centre
				new Vector3D(0, 0, 200),	// width vector
				new Vector3D(4, 0, 0),	// height vector
				new Reflective(0.8, true),
				scene,
				studio
		));

		// a light ray with trajectory, which bounces about between the two mirrors
		scene.addSceneObject(
				new EditableRayTrajectory(
						"ray trajectory",
						new Vector3D(-1, 0, 10),	// start point
						0,	// start time
						new Vector3D(0, 1, 1),	// initial direction
						0.02,	// radius
						new SurfaceColourLightSourceIndependent(DoubleColour.RED, true),
						100,	// max trace level
						true,	// reportToConsole
						scene,
						studio
				)
		);

		scene.addSceneObject(
				new EditableRayTrajectory(
						"ray trajectory",
						new Vector3D(-1, 0.5, 10),	// start point
						0,	// start time
						new Vector3D(0, 0, 1),	// initial direction
						0.02,	// radius
						new SurfaceColourLightSourceIndependent(DoubleColour.GREEN, true),
						100,	// max trace level
						true,	// reportToConsole
						scene,
						studio
				)
		);


		// define the camera
		//
		// Note that the view direction and basis Vector3Ds of the detector are chosen such that
		// the x, y, z axes form a LEFT-handed coordinate system.
		// The reason is that, in the photo, the positive x direction is then to the right,
		// the positive y direction is upwards, and the camera looks in the positive z direction.
		int
		pixelsX = 800,
		pixelsY = 600,
		antiAliasingFactor = 1;

		// a camera with a non-zero aperture size (so it simulates blur)
		ApertureCamera camera = new ApertureCamera(
				"Camera",
				new Vector3D(0, 0, 0),	// centre of aperture
				new Vector3D(0, 0, 3),	// view direction (magnitude is distance to detector centre)
				new Vector3D(-(double)pixelsX/pixelsY, 0, 0),	// horizontal basis Vector3D
				new Vector3D(0, 1, 0),	// vertical basis Vector3D
				pixelsX*antiAliasingFactor, pixelsY*antiAliasingFactor,	// logical number of pixels
				ExposureCompensationType.EC0,
				100,	// maxTraceLevel
				10,	// focussing distance
				0.0,	// aperture radius
				1	// rays per pixel; the more, the less noise the photo is
		);
		
		studio.setScene(scene);
		studio.setCamera(camera);		
		studio.setLights(LightSource.getStandardLightsFromBehind());
		
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

		// trace the rays with trajectory through the scene
		studio.traceRaysWithTrajectory();
		
		// do the ray tracing
		studio.takePhoto();

		// save the image
		studio.savePhoto(FILENAME, "bmp");

		// display the image on the screen
		container.add(new PhotoCanvas(studio.getPhoto()));
		container.validate();
	}
}
