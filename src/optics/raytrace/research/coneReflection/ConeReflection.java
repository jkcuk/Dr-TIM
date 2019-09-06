package optics.raytrace.research.coneReflection;

import java.awt.*;

import math.*;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.surfaces.Refractive;
import optics.raytrace.surfaces.SurfaceColour;
import optics.raytrace.cameras.*;
import optics.raytrace.cameras.PinholeCamera.ExposureCompensationType;
import optics.raytrace.core.*;
import optics.raytrace.GUI.nonInteractive.PhotoCanvas;
import optics.raytrace.GUI.nonInteractive.PhotoFrame;
import optics.raytrace.GUI.sceneObjects.EditableParametrisedCone;
import optics.raytrace.GUI.sceneObjects.EditableScaledParametrisedSphere;
import optics.raytrace.GUI.sceneObjects.EditableThinLens;


/**
 * Calculates reflection of a collimated beam from a 45 degree cone.
 * 
 * @author Johannes Courtial, Neal Radwell
 */
public class ConeReflection
{
	/**
	 * Filename under which main saves the rendered image.
	 * Alter to save the rendered image under a different name.
	 * @return	the filename under which main saves the rendered image.
	 */
	public static String getFilename()
	{
		return "ConeReflectionTemp.bmp";
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
		
		// create a collimated beam by placing a small sphere in the focal point of a lens
		scene.addSceneObject(new EditableScaledParametrisedSphere(
			"sphere at focal point of lens",	// description,
			new Vector3D(0, 0, -2),	// centre,
			0.02,	// radius,
			SurfaceColour.GREEN_MATT,	// surfaceProperty,
			scene,	// parent, 
			studio
		));
		
		scene.addSceneObject(new EditableThinLens(
				"lens",	// description,
				new Vector3D(0, 0, -1),	// centre,
				new Vector3D(0, 0, 1),	// normal,
				3,	// radius,
				1,	// focalLength,
				0.96,	// transmissionCoefficient,
				true,	// shadowThrowing,
				scene,	// parent,
				studio
		));

		scene.addSceneObject(new EditableParametrisedCone(
				"BK7 cone",	// description,
				new Vector3D(0, 0, 10),	// apex,
				new Vector3D(0, 0, -1),	// axis; -1 so that the apex is pointing away from us
				false,	// not open, i.e. there is a base to this cone
				MyMath.deg2rad(45-0.1),	// cone angle is 45�
				1,	// height,
				new Refractive(1.51, 0.8, true),	// n_in/n_out = 1.51, reflection coeff=0.8, shadow-throwing,
				scene,	// parent, 
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
				new Vector3D(0.0, 0, 0),	// centre of aperture
				new Vector3D(-0.0, 0, 10),	// view direction (magnitude is distance to detector centre)
				new Vector3D(.5*pixelsX/pixelsY, 0, 0),	// horizontal basis Vector3D
				new Vector3D(0, -.5, 0),	// vertical basis Vector3D
				pixelsX*antiAliasingFactor, pixelsY*antiAliasingFactor,	// logical number of pixels
				ExposureCompensationType.EC0,
				100,	// maxTraceLevel
				20,	// focussing distance
				0, //0.001,	// aperture radius
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