package optics.raytrace.research.TO;

import java.awt.Container;
import java.text.DecimalFormat;

import math.Vector3D;
import optics.raytrace.GUI.nonInteractive.PhotoCanvas;
import optics.raytrace.GUI.nonInteractive.PhotoFrame;
import optics.raytrace.cameras.ApertureCamera;
import optics.raytrace.cameras.PinholeCamera.ExposureCompensationType;
import optics.raytrace.core.*;
import optics.raytrace.sceneObjects.Sphere;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.surfaces.SurfaceColour;


public class BuildingDistortionMovie
{
	/**
	 * Define scene, lights, and/or camera.
	 * @return a studio, i.e. scene, lights and camera
	 */
	public static Studio createStudio(double x)
	{
		Studio studio = new Studio();

		// the scene
		SceneObjectContainer scene = new SceneObjectContainer("the scene", null, studio);

		// the standard scene objects
		scene.addSceneObject(SceneObjectClass.getChequerboardFloor(scene, studio));	// the checkerboard floor
		scene.addSceneObject(SceneObjectClass.getSkySphere(scene, studio));	// the sky

		// add any other scene objects

		scene.addSceneObject(new Sphere(
				"green sphere",	// description
				new Vector3D(x, 1, 15),	// centre
				1,	// radius
				SurfaceColour.GREEN_SHINY,
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
		pixelsX = 640,
		pixelsY = 480,
		antiAliasingFactor = 1;
		// If antiAliasingFactor is set to N, the image is calculated at resolution
		// N*pixelsX x N*pixelsY.

		// a camera with a non-zero aperture size (so it simulates blur)
		ApertureCamera camera = new ApertureCamera(
				"Camera",
				new Vector3D(0, 0, 0),	// centre of aperture
				new Vector3D(0, 0, 1),	// view direction (magnitude is distance to detector centre)
				new Vector3D(4*(double)pixelsX/pixelsY, 0, 0),	// horizontal basis Vector3D
				new Vector3D(0, -4, 0),	// vertical basis Vector3D
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
		
		PhotoCanvas photoCanvas = null;

		DecimalFormat formatter = new DecimalFormat("000");

		//Loop
		int iteration = 0;
		
		for(double x=-2; x<=2; x+=0.5)	// the normal movie
		{
			String filename = "test" + formatter.format(iteration) + " (x=" + x + ")" +".bmp";
			
			//final String FILENAME2 = TempFilename;
			System.out.println(filename);

			// define scene, lights and camera
			Studio studio = createStudio(x);

			// do the ray tracing
			studio.takePhoto();

			// save the image
			studio.savePhoto(filename, "bmp");
			
			// display the image on the screen
			if(photoCanvas == null)
			{
				photoCanvas = new PhotoCanvas(studio.getPhoto());
				container.add(photoCanvas);
				container.validate();
			}
			else
			{
				// photoCanvas.removeAll();
				photoCanvas.setImage(studio.getPhoto());
			}
			
			iteration++;
		}
	}
}