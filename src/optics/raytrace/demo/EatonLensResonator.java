package optics.raytrace.demo;

import java.awt.Container;
import java.text.DecimalFormat;

import math.*;
import optics.raytrace.sceneObjects.EatonLens;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.GUI.nonInteractive.PhotoCanvas;
import optics.raytrace.GUI.nonInteractive.PhotoFrame;
import optics.raytrace.cameras.*;
import optics.raytrace.cameras.PinholeCamera.ExposureCompensationType;
import optics.raytrace.core.*;


/**
 * A pair of Eaton lenses
 * 
 * Note that in Project>Properties (or alternatively Run>Run Configurations...) the working
 * directory (which can be found in the arguments tab) has to be set to the directory
 * containing all the images, normally
 * ${workspace_loc:TIM/demo/}
 * 
 * Adapted from RayTraceJavaApplication.java
 */
public class EatonLensResonator
{
	/**
	 * Filename under which main saves the rendered image.
	 * Alter to save the rendered image under a different name.
	 * @return	the filename under which main saves the rendered image.
	 */
	public static String getFilename(String frameNumberString)
	{
		return "EatonLensResonator" + frameNumberString + ".bmp";
	}

	/**
	 * Define scene, lights, and/or camera.
	 * @return the studio, i.e. scene, lights and camera
	 */
	public static Studio createStudio(int frameNumber)
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

		double refractiveIndexRatio = 0.5+frameNumber*1./40.;
		
		scene.addSceneObject(
				new EatonLens(
						"front Eaton lens",
						new Vector3D(0, 0, 5),	// centre
						1,	// radius
						refractiveIndexRatio,	// refractive-index ratio
						0,	// transparent tunnel radius
						0.98,	// transmission coefficient
						true,	// shadow-throwing
						scene,	// parent, 
						studio	// the studio
				));

		scene.addSceneObject(
				new EatonLens(
						"back Eaton lens",
						new Vector3D(0, 0, -5),	// centre
						1,	// radius
						refractiveIndexRatio,	// refractive-index ratio
						0,	// transparent tunnel radius
						0.98,	// transmission coefficient
						true,	// shadow-throwing
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
		pixelsX = 640,
		pixelsY = 480,
		antiAliasingFactor = 2;
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
		DecimalFormat formatter = new DecimalFormat("000");

		for(int frameNumber = 11; frameNumber<=20; frameNumber++)
		{
			// open a window, and take a note of its content pane
			Container container = (new PhotoFrame()).getContentPane();

			// define scene, lights and camera
			Studio studio = createStudio(frameNumber);

			// do the ray tracing
			studio.takePhoto();

			// save the image
			studio.savePhoto(getFilename(formatter.format(frameNumber)), "bmp");

			// display the image on the screen
			container.add(new PhotoCanvas(studio.getPhoto()));
			container.validate();
		}
	}
}
