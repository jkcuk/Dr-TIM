package optics.raytrace.demo;

import java.awt.Container;

import math.*;
import optics.raytrace.sceneObjects.Cylinder;
import optics.raytrace.sceneObjects.Parallelepiped;
import optics.raytrace.sceneObjects.Plane;
import optics.raytrace.sceneObjects.Sphere;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.sceneObjects.transformations.RotationAroundXAxis;
import optics.raytrace.sceneObjects.transformations.RotationAroundYAxis;
import optics.raytrace.sceneObjects.transformations.RotationAroundZAxis;
import optics.raytrace.sceneObjects.transformations.Translation;
import optics.raytrace.surfaces.SurfaceColour;
import optics.raytrace.GUI.nonInteractive.PhotoCanvas;
import optics.raytrace.GUI.nonInteractive.PhotoFrame;
import optics.raytrace.cameras.*;
import optics.raytrace.core.*;


/**
 * Test/demo of the MultiSurfaceAutostereogramCamera, an extended camera that creates an autostereogram.
 * 
 * Extensions:  unlike the "standard" autostereogram camera, this one can deal with more than one
 * surface and more than two eye positions.
 * 
 * The idea of the multiple surfaces is that the observer's brain would be able to flip between perceiving any
 * one of the (two or more) surfaces.
 * 
 * The idea of the multiple eye positions is that the scene can then be perceived in 3D as long as the
 * direction between the observer's (two) eyes coincides with the line connecting any two of the (two or more)
 * eye positions.
 * 
 * Certainly works with one scene and two eyes, when this is just a standard autostereogram camera.
 * 
 * Doesn't work (for me, anyway) with more than one scene, not with the scenes I tried anyway.
 * 
 * Sort of works with three eye positions (but it's harder to see the 3D than with two eye positions);
 * doesn't work for me with more than three eye positions.
 * 
 * Note that in Project>Properties (or alternatively Run>Run Configurations...) the working
 * directory (which can be found in the arguments tab) has to be set to the directory
 * containing all the images, normally
 * ${workspace_loc:TIM/demo/}
 * 
 * Adapted from RayTraceJavaApplication.java
 */
public class MultiSurfaceAutostereogramCameraDemo
{
	/**
	 * Filename under which main saves the rendered image.
	 * Alter to save the rendered image under a different name.
	 * @return	the filename under which main saves the rendered image.
	 */
	public static String getFilename()
	{
		return "MultiSurfaceAutostereogramCameraDemoTemp.bmp";
	}

	/**
	 * Define the surfaces, represented as an array of type SceneObject
	 * @return the scenes
	 */
	public static SceneObject[] createScenes(Studio studio)
	{
		SceneObject[] scenes = new SceneObject[1];
		
		// scene 0
		SceneObjectContainer scene0 = new SceneObjectContainer("scene 1", null, studio);

		// the standard scene objects
		scene0.addSceneObject(SceneObjectClass.getSkySphere(scene0, studio));	// the sky
		// scene.addSceneObject(SceneObjectClass.getChequerboardFloor(scene, studio));	// the checkerboard floor

		//
		// add any other scene objects
		//
		
		
		scene0.addSceneObject(new Plane(
				"plane",	// description,
				new Vector3D(0, 0, 11),	// pointOnPlane,
				new Vector3D(0, 0, 1),	// normal, 
				SurfaceColour.RED_MATT,	// surfaceProperty,
				scene0,	// parent,
				studio	// studio
			));
				
		scene0.addSceneObject(new Sphere(
				"sphere",	// description,
				new Vector3D(-0.6, -0.5, 10),	// centre,
				0.6,	// radius,
				SurfaceColour.GREEN_MATT,	// surfaceProperty,
				scene0,	// parent, 
				studio
		));

		scene0.addSceneObject(new Parallelepiped(
				"Parallelepiped",	// description,
				new Vector3D(0, 0, 0),	// centre,
				new Vector3D(1, 0, 0),	// u,
				new Vector3D(0, 1, 0),	// v,
				new Vector3D(0, 0, 1),	// w,
				SurfaceColour.WHITE_SHINY,	// surfaceProperty,
				scene0,	// parent,
				studio
			).transform(new RotationAroundXAxis(MyMath.deg2rad(30))
			).transform(new RotationAroundYAxis(MyMath.deg2rad(40))
			).transform(new RotationAroundZAxis(MyMath.deg2rad(60))
			).transform(new Translation(new Vector3D(0.7, 0.1, 9.2)))
		);
		
		scene0.addSceneObject(new Cylinder(
				"cylinder",	// description,
				new Vector3D(-1.2, 0.5, 10),	// startPoint,
				new Vector3D(-0.2, 0.7, 9.),	// endPoint,
				0.5,	// radius,
				SurfaceColour.GREEN_MATT,	// surfaceProperty,
				scene0,	// parent,
				studio
		));
		
		scenes[0] = scene0;
		
		// add second scene, if desired
//		scenes[1] = new Plane(
//				"plane",	// description,
//				new Vector3D(0, 0, 20),	// pointOnPlane,
//				new Vector3D(0, 0, 1),	// normal, 
//				SurfaceColour.RED_MATT,	// surfaceProperty,
//				null,	// parent,
//				studio	// studio
//			);

		return scenes;
	}
		

	/**
	 * Define the camera
	 * @return the camera
	 */
	public static MultiSurfaceAutostereogramCamera createCamera(Studio studio)
	{
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
		double
			stereogramWidth = 2.0,
			eyeSeparation = 0.8;	// 0.1 or so for poster; 0.8 or so for small image;
		
//		Vector3D[] eyePositions = new Vector3D[2];
//		eyePositions[0] = new Vector3D(-0.5*eyeSeparation, 0, 0);	// left eye
//		eyePositions[1] = new Vector3D( 0.5*eyeSeparation, 0, 0);	// right eye
//		// eyePositions[2] = new Vector3D( 0, eyeSeparation, 0);	// top eye
		
		int noEyePositions = 2;
		Vector3D[] eyePositions = new Vector3D[noEyePositions];
		for(int i=0; i<noEyePositions; i++)
			eyePositions[i] = new Vector3D(
					0.5*eyeSeparation*Math.cos(2*Math.PI*i/noEyePositions),
					0.5*eyeSeparation*Math.sin(2*Math.PI*i/noEyePositions),
					0
				);
		
		MultiSurfaceAutostereogramCamera camera = new MultiSurfaceAutostereogramCamera(
				"multi-surface autostereogram camera",	// name,
				eyePositions,
				new Vector3D(0, 0, 5),	// centre of autostereogram,
				new Vector3D(stereogramWidth, 0, 0),	// horizontal span vector
				new Vector3D(0, -stereogramWidth*pixelsY/pixelsX, 0),	// vertical span vector
				5/quality/quality,	// quality*10.,	// dots per pixel (on average)
				1.5*quality,	// dot radius, in pixels
				pixelsX*antiAliasingFactor,	pixelsY*antiAliasingFactor,	// imagePixelsHorizontal, imagePixelsVertical,
				100	// maxTraceLevel
			);
		
		studio.setCamera(camera);
		
		return camera;
	}
	
		
	/**
	 * Define the lights
	 * @return the lights
	 */
	public static LightSource createLights(Studio studio)
	{
		LightSource lights = LightSource.getStandardLightsFromBehind();
		
		studio.setLights(lights);
		
		return lights;
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

		// create a studio
		Studio studio = new Studio();

		// define scenes
		SceneObject[] scenes = createScenes(studio);
		
		// define lights
		LightSource lights = createLights(studio);
		
		// define the camera
		MultiSurfaceAutostereogramCamera camera = createCamera(studio);

		// do the ray tracing
		camera.takePhoto(scenes, lights, null);

		// save the image
		studio.savePhoto(getFilename(), "bmp");

		// display the image on the screen
		container.add(new PhotoCanvas(studio.getPhoto()));
		container.validate();
	}
}
