package optics.raytrace.research.TO.METATOYCloak;

import java.awt.*;

import math.*;
import optics.raytrace.sceneObjects.*;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.surfaces.GCLAsWithApertures;
import optics.raytrace.surfaces.Reflective;
import optics.raytrace.surfaces.SurfaceColour;
import optics.raytrace.utility.CoordinateSystems.GlobalOrLocalCoordinateSystemType;
import optics.raytrace.cameras.*;
import optics.raytrace.cameras.PinholeCamera.ExposureCompensationType;
import optics.raytrace.core.*;
import optics.raytrace.GUI.nonInteractive.PhotoCanvas;
import optics.raytrace.GUI.nonInteractive.PhotoFrame;
import optics.raytrace.GUI.sceneObjects.EditableCylinderLattice;
import optics.raytrace.GUI.sceneObjects.EditableScaledParametrisedCentredParallelogram;


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
 * @author Johannes Courtial
 */
public class METATOYCloak
{
	/**
	 * Filename under which main saves the rendered image.
	 * Alter to save the rendered image under a different name.
	 * @return	the filename under which main saves the rendered image.
	 */
	public static String getFilename()
	{
		return "METATOYCloak.bmp";
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
		
		double cylinderRadius = 0.02;
		
		// a cylinder lattice...
		scene.addSceneObject(new EditableCylinderLattice(
				"cylinder lattice",
				-1+cylinderRadius, 1-cylinderRadius, 4,
				-1+cylinderRadius, 1-cylinderRadius, 4,
				11, 38, 4, // this puts the "transverse" cylinders into the planes z=11, 20, 29, 38
				0.02,
				scene,
				studio
		));
		
		scene.addSceneObject(new Sphere(
				"green sphere",	// description
				new Vector3D(-2, 1, 15),	// centre
				1,	// radius
				SurfaceColour.GREEN_SHINY,
				scene, 
				studio
		));

		scene.addSceneObject(new Cylinder(
				"shiny cylinder",
				new Vector3D(0, 0, 15),	// start point
				new Vector3D(2, 2, 13),	// end point
				1,	// radius
				new Reflective(0.9, true),
				scene,
				studio
		));


		scene.addSceneObject(new EditableScaledParametrisedCentredParallelogram(
				"CLA window",
				new Vector3D(0, 0, 10.5),	// centre
				new Vector3D(3, 0, 0),	// span vector 1
				new Vector3D(0, 2, 0),	// span vector 2
				new GCLAsWithApertures(new Vector3D(0,0,1), new Vector3D(1,0,0), new Vector3D(0,1,0), 0.2, 0, 0, 0.3, GlobalOrLocalCoordinateSystemType.LOCAL_OBJECT_BASIS, 0.9, true),
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
