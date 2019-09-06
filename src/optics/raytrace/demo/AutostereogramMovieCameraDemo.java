package optics.raytrace.demo;

import java.awt.Container;

import math.*;
import optics.raytrace.sceneObjects.Plane;
import optics.raytrace.sceneObjects.Sphere;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.surfaces.SurfaceColour;
import optics.raytrace.GUI.nonInteractive.PhotoCanvas;
import optics.raytrace.GUI.nonInteractive.PhotoFrame;
import optics.raytrace.cameras.*;
import optics.raytrace.core.*;


/**
 * Autostereogram-movie-camera demo
 * 
 * Note that in Project>Properties (or alternatively Run>Run Configurations...) the working
 * directory (which can be found in the arguments tab) has to be set to the directory
 * containing all the images, normally
 * ${workspace_loc:TIM/demo/}
 * 
 * Adapted from RayTraceJavaApplication.java
 */
public class AutostereogramMovieCameraDemo
{
	/**
	 * Filename under which main saves the rendered image.
	 * Alter to save the rendered image under a different name.
	 * @return	the filename under which main saves the rendered image.
	 */
	public static String getFilename()
	{
		return "AutostereogramMovieCameraDemoTemp.bmp";
	}

	/**
	 * @param n
	 * @return	the scene that corresponds to angle #n
	 */
	public static SceneObject createScene(double angle)
	{		
		// the scene
		SceneObjectContainer scene = new SceneObjectContainer("scene for angle "+angle, null, null);

		scene.addSceneObject(new Plane(
				"background plane",	// description,
				new Vector3D(0, 0, 11),	// pointOnPlane,
				new Vector3D(0, 0, 1),	// normal, 
				SurfaceColour.RED_MATT,	// surfaceProperty,
				scene,	// parent,
				null	// studio
			));
		
		scene.addSceneObject(new Sphere(
				"sphere",
				new Vector3D(0., 0., 10),	// centre
				.75,	// radius
				SurfaceColour.RED_MATT,	// surfaceProperty,
				scene,	// parent,
				null	// studio
			));
		
		return scene;
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
		// define the camera
		//
		// Note that the view direction and basis Vector3Ds of the detector are chosen such that
		// the x, y, z axes form a LEFT-handed coordinate system.
		// The reason is that, in the photo, the positive x direction is then to the right,
		// the positive y direction is upwards, and the camera looks in the positive z direction.
		int
			quality = 1,	// 1 = normal, 2 = good, 4 = great
			pixelsX = 640*quality,
			pixelsY = 480*quality,
			antiAliasingFactor = 1;
		double
			stereogramWidth = 2.0,
			eyeSeparation = 0.8;	// 0.1 or so for poster; 0.8 or so for small image
		// the visible area in the group's posters is of size 70cm x 50cm
		
		AutostereogramMovieCamera camera = new AutostereogramMovieCamera(
				"autostereogram movie camera",	// name,
				new Vector3D(0, 0, 0),	// between-the-eyes position
				eyeSeparation,	// eye separation
				new Vector3D(0, 0, 5),	// centre of autostereogram,
				new Vector3D(stereogramWidth, 0, 0),	// horizontal span vector
				new Vector3D(0, -stereogramWidth*pixelsY/pixelsX, 0),	// vertical span vector
				20,	// no of iterations
				.75,	// dot radius, in pixels
				pixelsX*antiAliasingFactor,	pixelsY*antiAliasingFactor,	// imagePixelsHorizontal, imagePixelsVertical,
				100	// maxTraceLevel
			);
		
		int numberOfAngles = 2;
		SceneObject scenes[] = new SceneObject[numberOfAngles];
		double angles[] = new double[numberOfAngles];
		for(int n=0; n<numberOfAngles; n++)
		{
			double angle = Math.PI*(n-0.5*(numberOfAngles-1))/numberOfAngles;
			System.out.println("angle["+n+"]="+angle);
			angles[n] = angle;
			scenes[n] = createScene(0.*angle);
		}
		
		// do the ray tracing
		camera.takePhoto(scenes, angles);

		// save the image
		camera.savePhoto(getFilename(), "bmp");

		// display the image on the screen
		container.add(new PhotoCanvas(camera.getPhoto()));
		container.validate();
	}
}
