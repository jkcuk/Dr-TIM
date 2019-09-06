package optics.raytrace.demo;

import java.awt.Container;

import math.*;
import optics.raytrace.sceneObjects.Plane;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.surfaces.SurfaceColour;
import optics.raytrace.surfaces.SurfaceTiling;
import optics.raytrace.GUI.nonInteractive.PhotoCanvas;
import optics.raytrace.GUI.nonInteractive.PhotoFrame;
import optics.raytrace.GUI.sceneObjects.EditableParametrisedCylinder;
import optics.raytrace.GUI.sceneObjects.EditableScaledParametrisedSphere;
import optics.raytrace.cameras.*;
import optics.raytrace.cameras.PinholeCamera.ExposureCompensationType;
import optics.raytrace.core.*;


/**
 * Thin lens demo
 * 
 * Note that in Project>Properties (or alternatively Run>Run Configurations...) the working
 * directory (which can be found in the arguments tab) has to be set to the directory
 * containing all the images, normally
 * ${workspace_loc:TIM/demo/}
 * 
 * Adapted from RayTraceJavaApplication.java
 */
public class AnyFocusSurfaceCameraDemo
{
	/**
	 * Filename under which main saves the rendered image.
	 * Alter to save the rendered image under a different name.
	 * @return	the filename under which main saves the rendered image.
	 */
	public static String getFilename()
	{
		return "AnyFocusSurfaceCameraDemoTemp.bmp";
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
		scene.addSceneObject(SceneObjectClass.getChequerboardFloor(scene, studio));	// the checkerboard floor

		//
		// add any other scene objects
		//
		
		// the interesting objects: a couple of spheres...
		SceneObject s1 = new EditableScaledParametrisedSphere(
				"sphere 1",	// description,
				new Vector3D(-2, 0, 6),	// centre,
				1,	// radius,
				new Vector3D(0.5, 1, 1),	// pole,
				new Vector3D(1, 0, 0),	// phi0Direction,
				0, 5,	// sThetaMin, sThetaMax,
				0, 10,	// sPhiMin, sPhiMax,
				new SurfaceTiling(SurfaceColour.BLACK_SHINY, SurfaceColour.GREY90_SHINY, 1, 1),	// surfaceProperty,
				scene,	// parent, 
				studio
			);
		
		SceneObject s2 = new EditableScaledParametrisedSphere(
				"sphere 2",	// description,
				new Vector3D(0, 0, 8),	// centre,
				1,	// radius,
				new Vector3D(0.5, 1, 1),	// pole,
				new Vector3D(1, 0, 0),	// phi0Direction,
				0, 5,	// sThetaMin, sThetaMax,
				0, 10,	// sPhiMin, sPhiMax,
				new SurfaceTiling(SurfaceColour.BLACK_SHINY, SurfaceColour.GREY90_SHINY, 1, 1),	// surfaceProperty,
				scene,	// parent, 
				studio
			);
		
		SceneObject s3 = new EditableScaledParametrisedSphere(
				"sphere 3",	// description,
				new Vector3D(2, 0, 10),	// centre,
				1,	// radius,
				new Vector3D(0.5, 1, 1),	// pole,
				new Vector3D(1, 0, 0),	// phi0Direction,
				0, 5,	// sThetaMin, sThetaMax,
				0, 10,	// sPhiMin, sPhiMax,
				new SurfaceTiling(SurfaceColour.BLACK_SHINY, SurfaceColour.GREY90_SHINY, 1, 1),	// surfaceProperty,
				scene,	// parent, 
				studio
			);

		SceneObject s4 = new EditableScaledParametrisedSphere(
				"sphere 4",	// description,
				new Vector3D(4, 0, 12),	// centre,
				1,	// radius,
				new Vector3D(0.5, 1, 1),	// pole,
				new Vector3D(1, 0, 0),	// phi0Direction,
				0, 5,	// sThetaMin, sThetaMax,
				0, 10,	// sPhiMin, sPhiMax,
				new SurfaceTiling(SurfaceColour.BLACK_SHINY, SurfaceColour.GREY90_SHINY, 1, 1),	// surfaceProperty,
				scene,	// parent, 
				studio
			);
		
		// ... and a cylinder
		SceneObject c = new EditableParametrisedCylinder(
				"cylinder",	// description,
				new Vector3D(1, -0.8, 0),	// startPoint,
				new Vector3D(1, -0.8, 1000),	// endPoint,
				0.2,	// radius,
				new SurfaceTiling(SurfaceColour.RED_SHINY, SurfaceColour.GREY90_SHINY, 0.2, Math.PI/5),	// surfaceProperty,
				scene,	// parent, 
				studio
			);
		
		scene.addSceneObject(s1);
		scene.addSceneObject(s2);
		scene.addSceneObject(s3);
		scene.addSceneObject(s4);
		scene.addSceneObject(c);
		
		// define what the camera will focus on
		SceneObjectContainer focus = new SceneObjectContainer("focussing scene", null, studio);
		focus.addSceneObject(s1);
		focus.addSceneObject(s3);
		focus.addSceneObject(c);
		focus.addSceneObject(new Plane("focus plane", new Vector3D(0, 0, 40), new Vector3D(0, 0, 1), SurfaceProperty.NO_SURFACE_PROPERTY, scene, studio));
		
		// define the camera
		//
		// Note that the view direction and basis Vector3Ds of the detector are chosen such that
		// the x, y, z axes form a LEFT-handed coordinate system.
		// The reason is that, in the photo, the positive x direction is then to the right,
		// the positive y direction is upwards, and the camera looks in the positive z direction.
		int
			quality = 4,	// 1 = normal, 2 = good, 4 = great
			pixelsX = 640*quality,
			pixelsY = 480*quality,
			antiAliasingFactor = 1;
		
		CameraClass camera = new AnyFocusSurfaceCamera(
				"camera",	// name,
				new Vector3D(0, 0, 0),	// apertureCentre,
				new Vector3D(0, 0, 1.5),	// viewDirection,
				new Vector3D(-(double)pixelsX/pixelsY, 0, 0),	// horizontalSpanVector3D,
				new Vector3D(0, 1, 0),	// verticalSpanVector3D,
				pixelsX*antiAliasingFactor, pixelsY*antiAliasingFactor,	// detectorPixelsHorizontal, detectorPixelsVertical, 
				ExposureCompensationType.EC0,
				100,	// maxTraceLevel,
				focus,	// focusScene,
				0.1,	// apertureRadius,
				100	// raysPerPixel
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
		studio.savePhoto(getFilename(), "bmp");

		// display the image on the screen
		container.add(new PhotoCanvas(studio.getPhoto()));
		container.validate();
	}
}
