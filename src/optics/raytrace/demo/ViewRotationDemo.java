package optics.raytrace.demo;

import java.awt.Container;

import math.*;
import optics.raytrace.surfaces.*;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.GUI.nonInteractive.PhotoCanvas;
import optics.raytrace.GUI.nonInteractive.PhotoFrame;
import optics.raytrace.GUI.sceneObjects.EditableCylinderLattice;
import optics.raytrace.GUI.sceneObjects.EditableScaledParametrisedCentredParallelogram;
import optics.raytrace.cameras.*;
import optics.raytrace.cameras.PinholeCamera.ExposureCompensationType;
import optics.raytrace.core.*;


/**
 * View-rotation demo
 * 
 * Note that in Project>Properties (or alternatively Run>Run Configurations...) the working
 * directory (which can be found in the arguments tab) has to be set to the directory
 * containing all the images, normally
 * ${workspace_loc:TIM/demo/}
 * 
 * Adapted from RayTraceJavaApplication.java
 */
public class ViewRotationDemo
{
	/**
	 * Filename under which main saves the rendered image.
	 */
	private static final String FILENAME = "ViewRotationDemoTemp.bmp";

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
		
		// first, something to look at
		scene.addSceneObject(new EditableCylinderLattice(
				"cylinder lattice",
				-1, 1, 4,
				-1, 1, 4,
				10, 25, 4,
				0.02,
				scene,
				studio
		));
		
		// example: 10 degree view rotation with sheets at z=5 and z=10.
		// There are two solutions; comment out one or the other
		
		// first solution for 10 degree view rotation
		
//		// then add ray-rotation windows
//		scene.addSceneObject(new EditableRectangle(
//				"ray-rotating window",
//				new Vector3D(0, 0, 10),	// centre
//				new Vector3D(0, 0, -1),	// normal, pointing towards the camera (so we are on the "outside"; behind the surface is "inside")
//				new Vector3D(3, 0, 0),	// width vector
//				2./3,	// aspect ratio
//				new RayRotating(190*Math.PI/180, SurfacePropertyPanel.TRANSMISSION_COEFFICIENT)
//		));
//		scene.addSceneObject(new EditableRectangle(
//				"ray-rotating window",
//				new Vector3D(0, 0, 5),	// centre
//				new Vector3D(0, 0, -1),	// normal, pointing towards the camera (so we are on the "outside"; behind the surface is "inside")
//				new Vector3D(3, 0, 0),	// width vector
//				2./3,	// aspect ratio
//				new RayRotating(180*Math.PI/180, SurfacePropertyPanel.TRANSMISSION_COEFFICIENT)
//		));
		
		// second solution for 10 degree view rotation
		
		// then add ray-rotation windows
		scene.addSceneObject(new EditableScaledParametrisedCentredParallelogram(
				"ray-rotating window",
				new Vector3D(0, 0, 10),	// centre
				new Vector3D(3, 0, 0),	// width vector
				new Vector3D(0, 2, 0),	// height vector
				new RayRotating(-10*Math.PI/180, SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT, true),
				scene,
				studio
		));
		scene.addSceneObject(new EditableScaledParametrisedCentredParallelogram(
				"ray-rotating window",
				new Vector3D(0, 0, 5),	// centre
				new Vector3D(3, 0, 0),	// width vector
				new Vector3D(0, 2, 0),	// height vector
				new RayRotating(20*Math.PI/180, SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT, true),
				scene,
				studio
		));
		
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
