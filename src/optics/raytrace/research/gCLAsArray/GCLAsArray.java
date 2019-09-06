package optics.raytrace.research.gCLAsArray;
import java.awt.*;

import math.*;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.surfaces.SurfaceColour;
import optics.raytrace.surfaces.GCLAsWithApertures;
import optics.raytrace.utility.CoordinateSystems.GlobalOrLocalCoordinateSystemType;
import optics.raytrace.cameras.*;
import optics.raytrace.cameras.PinholeCamera.ExposureCompensationType;
import optics.raytrace.core.*;
import optics.raytrace.GUI.nonInteractive.PhotoCanvas;
import optics.raytrace.GUI.nonInteractive.PhotoFrame;
import optics.raytrace.GUI.sceneObjects.EditableFramedRectangle;
/**
 * Example for running The METATOY Raytracer (TIM) as a non-interactive Java application.
 * 
 * The main method renders the image defined by createStudio(), saves it to a file
 * (whose name is given by the constant FILENAME), and displays it in a new window.
 *
 * Change the method createStudio() to change scene, lights and/or camera.
 * 
 * Change the method getFilename() to save the image under a different name.
 * 
 * Change the main method if you want the Java application to do something different altogether.
 * 
 * @author Johannes Courtial, Lena Mertens
 */
public class GCLAsArray
{
	/**
	 * Filename under which main saves the rendered image.
	 * Alter to save the rendered image under a different name.
	 * @return	the filename under which main saves the rendered image.
	 */
	public static String getFilename()
	{
		return "gCLAsArrayTemp.bmp";
	}
	
	/**
	 * Define scene, lights, and/or camera.
	 * @return a studio, i.e. scene, lights and camera
	 */
	public static Studio createStudio()
	{
		Studio studio = new Studio();

		// the scene
		SceneObjectContainer scene = new SceneObjectContainer("the scene", null, studio);

		// the standard scene objects
		scene.addSceneObject(SceneObjectClass.getChequerboardFloor(scene, studio));	// the checkerboard floor
		scene.addSceneObject(SceneObjectClass.getSkySphere(scene, studio));	// the sky

		// add any other scene objects
		
		
		// the windows in a z=const. plane
		for(int z=8; z<=12; z=z+2)
		{
			for(int x=-2; x<2; x=x+2)
			{
				// the surface property of the window
				GCLAsWithApertures windowSurfaceProperty = new GCLAsWithApertures(
						new Vector3D(0, 0, 1),	// aHat
						new Vector3D(0, 1, 0),	// uHat
						new Vector3D(1, 0, 0),	// vHat
						2,	// etaU,
						2,	// etaV,
						0,	// deltaU,
						0,	// deltaV,
						GlobalOrLocalCoordinateSystemType.GLOBAL_BASIS,	// basis,
						0.96,	// transmissionCoefficient,
						false	// shadowThrowing
					);

				// now add the window to the scene
				scene.addSceneObject(new EditableFramedRectangle(
						"gCLAs window at x="+x+", z="+z,	// description
						new Vector3D(x, -1, z),	// corner,
						new Vector3D(2, 0, 0),	// widthVector,
						new Vector3D(0, 2, 0),	// heightVector,
						0.03,	// radius,
						windowSurfaceProperty,
						SurfaceColour.DARK_RED_SHINY,	// frameSurfaceProperty,
						true,	// showFrames,
						scene,	// parent, 
						studio
				));
			}
		}

		// the windows in an x=const. plane
		for(int z=8; z<12; z=z+2)
		{
			for(int x=-2; x<=2; x=x+2)
			{
				// the surface property of the window
				GCLAsWithApertures windowSurfaceProperty = new GCLAsWithApertures(
						new Vector3D(1, 0, 0),	// aHat
						new Vector3D(0, 1, 0),	// uHat
						new Vector3D(0, 0, -1),	// vHat
						2,	// etaU,
						2,	// etaV,
						0,	// deltaU,
						0,	// deltaV,
						GlobalOrLocalCoordinateSystemType.GLOBAL_BASIS,	// basis,
						0.96,	// transmissionCoefficient,
						false	// shadowThrowing
					);

				// now add the window to the scene
				scene.addSceneObject(new EditableFramedRectangle(
						"gCLAs window at x="+x+", z="+z,	// description
						new Vector3D(x, -1, z),	// corner,
						new Vector3D(0, 0, 2),	// widthVector,
						new Vector3D(0, 2, 0),	// heightVector,
						0.03,	// radius,
						windowSurfaceProperty,
						SurfaceColour.RED_SHINY,	// frameSurfaceProperty,
						true,	// showFrames,
						scene,	// parent, 
						studio
				));
			}
		}


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
				new Vector3D(0, 3, -2),	// centre of aperture
				new Vector3D(0, -.25, 1),	// view direction (magnitude is distance to detector centre)
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