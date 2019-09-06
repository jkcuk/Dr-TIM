package optics.raytrace.demo;

import java.awt.Container;

import math.*;
import optics.raytrace.surfaces.*;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.GUI.nonInteractive.PhotoCanvas;
import optics.raytrace.GUI.nonInteractive.PhotoFrame;
import optics.raytrace.GUI.sceneObjects.EditableCylinderFrame;
import optics.raytrace.GUI.sceneObjects.EditableScaledParametrisedCentredParallelogram;
import optics.raytrace.GUI.surfaces.EditableSurfaceTiling;
import optics.raytrace.cameras.*;
import optics.raytrace.cameras.PinholeCamera.ExposureCompensationType;
import optics.raytrace.core.*;


/**
 * Ray-rotation experiment
 * 
 * Simulates the ray-rotation experiment performed by Sean Ogilvie, Blair Kirkpatrick,
 * Alasdair C. Hamilton and J. Courtial.  All lengths are in cm.
 * 
 * Note that in Project>Properties (or alternatively Run>Run Configurations...) the working
 * directory (which can be found in the arguments tab) has to be set to the directory
 * containing all the images, normally
 * ${workspace_loc:TIM/demo/}
 * 
 * Adapted from RayTraceJavaApplication.java
 */
public class RayRotationExperiment
{
	/**
	 * the simulation parameters
	 */
	private static double
		dx = 6,	// sideways camera shift, in cm
		alpha = 140;	// ray-rotation angle
	
	/**
	 * Filename under which main saves the rendered image.
	 */
//	private static final String FILENAME = "RayRotationExperimentTemp.bmp";
	private static final String FILENAME = "dx=+06cm.bmp";

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
		// scene.addSceneObject(SceneObjectClass.getChequerboardFloor(studio));	// the checkerboard floor

		//
		// add any other scene objects
		//
		
		// first, something to look at
		scene.addSceneObject(new EditableScaledParametrisedCentredParallelogram(
				"object",
				new Vector3D(0, 0, 25),	// centre
				new Vector3D(0, 0, 50),	// width vector
				new Vector3D(0, 10, 0),	// height vector
				new EditableSurfaceTiling(
						SurfaceColourLightSourceIndependent.BLACK,
						SurfaceColourLightSourceIndependent.WHITE,
						// SurfaceColour.WHITE_MATT,
						5, 5,
						scene),
				scene,
				studio
		));
		
		
		// example: 10 degree view rotation with sheets at z=5 and z=10.
		// There are two solutions; comment out one or the other
		
		// then add a ray-rotation window
		if(true)
		{
			scene.addSceneObject(new EditableScaledParametrisedCentredParallelogram(
					"ray-rotating window",
					new Vector3D(0, 0, 0),	// centre
					new Vector3D(15, 0, 0),	// width vector
					new Vector3D(0, 15, 0),	// height vector
					new RayRotating(alpha*Math.PI/180, SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT, true),
					scene,
					studio
			));

			scene.addSceneObject(new EditableCylinderFrame(
					"window frame",
					new Vector3D(-7.5, -7.5, 0),	// bottom left corner
					new Vector3D(15, 0, 0),	// width vector
					new Vector3D(0, 15, 0),	// height vector
					1,	// radius of cylinders
					SurfaceColour.GREY50_SHINY,
					scene,
					studio
			));
		}

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
		Vector3D apertureCentre = new Vector3D(dx, 0, -100);
		ApertureCamera camera = new ApertureCamera(
				"Camera",
				apertureCentre,	// centre of aperture
				apertureCentre.getWithLength(-4),	// view direction (magnitude is distance to detector centre)
				new Vector3D(-(double)pixelsX/pixelsY, 0, 0),	// horizontal basis Vector3D
				new Vector3D(0, -1, 0),	// vertical basis Vector3D
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

		// do the ray tracing
		studio.takePhoto();

		// save the image
		studio.savePhoto(FILENAME, "bmp");

		// display the image on the screen
		container.add(new PhotoCanvas(studio.getPhoto()));
		container.validate();
	}
}
