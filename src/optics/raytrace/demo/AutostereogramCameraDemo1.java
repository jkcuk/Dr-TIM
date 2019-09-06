package optics.raytrace.demo;

import java.awt.Container;

import math.*;
import optics.raytrace.sceneObjects.Plane;
import optics.raytrace.sceneObjects.TimHead;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.surfaces.SurfaceColour;
import optics.raytrace.GUI.nonInteractive.PhotoCanvas;
import optics.raytrace.GUI.nonInteractive.PhotoFrame;
import optics.raytrace.cameras.*;
import optics.raytrace.core.*;


/**
 * Autostereogram-camera demo 1
 * 
 * Note that in Project>Properties (or alternatively Run>Run Configurations...) the working
 * directory (which can be found in the arguments tab) has to be set to the directory
 * containing all the images, normally
 * ${workspace_loc:TIM/demo/}
 * 
 * Adapted from RayTraceJavaApplication.java
 */
public class AutostereogramCameraDemo1
{
	/**
	 * Filename under which main saves the rendered image.
	 * Alter to save the rendered image under a different name.
	 * @return	the filename under which main saves the rendered image.
	 */
	public static String getFilename()
	{
		return "AutostereogramCameraDemoTemp.bmp";
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

		//
		// add any other scene objects
		//
		
		scene.addSceneObject(new Plane(
				"background plane",	// description,
				new Vector3D(0, 0, 11),	// pointOnPlane,
				new Vector3D(0, 0, 1),	// normal, 
				SurfaceColour.RED_MATT,	// surfaceProperty,
				scene,	// parent,
				studio	// studio
			));
		
		scene.addSceneObject(
				new TimHead(
						"potato head",
						new Vector3D(0, 0, 10),	// centre
						scene,	// parent, 
						studio	// the studio
					));
		
		
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
		double
			stereogramWidth = 2.0,
			eyeSeparation = 0.8;	// 0.1 or so for poster; 0.8 or so for small image
		// the visible area in the group's posters is of size 70cm x 50cm
		
		CameraClass camera = new AutostereogramCamera(
				"autostereogram camera",	// name,
				new Vector3D(-0.5*eyeSeparation, 0, 0),	// left eye
				new Vector3D( 0.5*eyeSeparation, 0, 0),	// right eye
				new Vector3D(0, 0, 5),	// centre of autostereogram,
				new Vector3D(stereogramWidth, 0, 0),	// horizontal span vector
				new Vector3D(0, -stereogramWidth*pixelsY/pixelsX, 0),	// vertical span vector
				1.0/quality/quality,	// quality*10.,	// dots per pixel (on average)
				1.5*quality,	// dot radius, in pixels
				pixelsX*antiAliasingFactor,	pixelsY*antiAliasingFactor,	// imagePixelsHorizontal, imagePixelsVertical,
				100	// maxTraceLevel
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
