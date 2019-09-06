package optics.raytrace.demo;

import java.awt.Container;

import math.*;
import optics.raytrace.sceneObjects.Parallelepiped;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectUnion;
import optics.raytrace.sceneObjects.transformations.RotationAroundXAxis;
import optics.raytrace.sceneObjects.transformations.RotationAroundYAxis;
import optics.raytrace.sceneObjects.transformations.Translation;
import optics.raytrace.surfaces.Refractive;
import optics.raytrace.surfaces.SurfaceColour;
import optics.raytrace.utility.CopyModeType;
import optics.raytrace.GUI.nonInteractive.PhotoCanvas;
import optics.raytrace.GUI.nonInteractive.PhotoFrame;
import optics.raytrace.cameras.*;
import optics.raytrace.cameras.PinholeCamera.ExposureCompensationType;
import optics.raytrace.core.*;


/**
 * Test the parallelepiped
 * 
 * Note that in Project>Properties (or alternatively Run>Run Configurations...) the working
 * directory (which can be found in the arguments tab) has to be set to the directory
 * containing all the images, normally
 * ${workspace_loc:TIM/demo/}
 * 
 * Adapted from RayTraceJavaApplication.java
 * 
 * @author	Alasdair Hamilton, Johannes Courtial
 */
public class ParallelepipedDemo
{
	/**
	 * Filename under which main saves the rendered image.
	 * Alter to save the rendered image under a different name.
	 * @return	the filename under which main saves the rendered image.
	 */
	public static String getFilename()
	{
		return "ParallelepipedDemoTemp.bmp";
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
				
		SceneObjectUnion union = new SceneObjectUnion("union", scene, studio);
		SceneObjectContainer container = new SceneObjectContainer("container", scene, studio);
		
		union.addSceneObject(new Parallelepiped(
				"parallelepiped 1",	// description
				new Vector3D(0, 0, 0),	// centre
				new Vector3D(1, 0, 0),	// u
				new Vector3D(0, 1, 0),	// v
				new Vector3D(0, 0, 2),	// w
				new Refractive(1.2, 0.9, true),	// surfaceProperty
				scene,	// parent, 
				studio	// the studio
			));

		union.addSceneObject(new Parallelepiped(
				"parallelepiped 2",	// description
				new Vector3D(0, 0, 0),	// centre
				new Vector3D(2, 0, 0),	// u
				new Vector3D(0, 0.5, 0),	// v
				new Vector3D(0, 0, 0.5),	// w
				SurfaceColour.RED_SHINY,
				// new Refractive(1.2, 0.9),	// surfaceProperty
				scene,	// parent, 
				studio	// the studio
			));
		
		container = new SceneObjectContainer(union, CopyModeType.CLONE_DATA);

		union = union.transform(new RotationAroundYAxis(0.5)).transform(new RotationAroundXAxis(0.4)).transform(new Translation(new Vector3D(1.1, 0, 10)));
		container = container.transform(new RotationAroundYAxis(0.5)).transform(new RotationAroundXAxis(0.4)).transform(new Translation(new Vector3D(-1.1, 0, 10)));
		
		scene.addSceneObject(union);
		scene.addSceneObject(container);
				
		// define the camera
		//
		// Note that the view direction and basis Vector3Ds of the detector are chosen such that
		// the x, y, z axes form a LEFT-handed coordinate system.
		// The reason is that, in the photo, the positive x direction is then to the right,
		// the positive y direction is upwards, and the camera looks in the positive z direction.
		int
			quality = 2,	// 1 = normal, 2 = good, 4 = great
			pixelsX = 640*quality,
			pixelsY = 480*quality,
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
				8,	// focussing distance
				0.,	// aperture radius
				1	// rays per pixel; the more, the less noise the photo is
		);
		
		studio.setScene(scene);
		studio.setCamera(camera);		
		studio.setLights(LightSource.getStandardLightsFromBehind());
		
		return studio;
	}
	

	/**
	 * This method gets called when the Java application starts.
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
