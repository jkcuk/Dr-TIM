package optics.raytrace.demo;

import java.awt.Container;

import math.*;
import optics.DoubleColour;
import optics.raytrace.surfaces.*;
import optics.raytrace.sceneObjects.DovePrism;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.sceneObjects.transformations.RotationAroundYAxis;
import optics.raytrace.sceneObjects.transformations.Translation;
import optics.raytrace.GUI.nonInteractive.PhotoCanvas;
import optics.raytrace.GUI.nonInteractive.PhotoFrame;
import optics.raytrace.GUI.sceneObjects.EditableRayTrajectoryCone;
import optics.raytrace.cameras.*;
import optics.raytrace.cameras.PinholeCamera.ExposureCompensationType;
import optics.raytrace.core.*;


/**
 * Ray-trajectory-cone demo.
 * Allows a bundle of light rays to be traced through an optical system and the ray
 * trajectories then visualised.
 * 
 * Note that in Project>Properties (or alternatively Run>Run Configurations...) the working
 * directory (which can be found in the arguments tab) has to be set to the directory
 * containing all the images, normally
 * ${workspace_loc:TIM/demo/}
 * 
 * Adapted from RayTraceJavaApplication.java
 */
public class RayTrajectoryConeDemo
{
	/**
	 * Filename under which main saves the rendered image.
	 */
	private static final String FILENAME = "RayTrajectoryConeDemoTemp.bmp";

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
		
		scene.addSceneObject(new DovePrism(
				"Dove prism",	// description
				0.25,	// width
				0.25,	// height
				1,	// length,
				new Refractive(1.4, 0.96, true),	// surface property
				scene,	// parent
				studio	// studio
		).transform(
				new RotationAroundYAxis(MyMath.deg2rad(30))
		).transform(
				new Translation(new Vector3D(0, 0, 10))
		));

		// a light ray with trajectory, which bounces about between the two mirrors;
		// to trace the rays with trajectory through the scene, need to add
		// "studio.traceRaysWithTrajectory();" later
		scene.addSceneObject(
				new EditableRayTrajectoryCone(
						"fuzzy ray trajectory cone",
						new Vector3D(0, 0, 12),	// start point
						0,	// start time
						new Vector3D(0, 1, 0),	// initial direction
						MyMath.deg2rad(10),
						20,	// number of rays
						0.02,	// ray radius
						// SurfaceColour.RED_SHINY,
						new SurfaceOfGlowingCloudPositionDependent(DoubleColour.RED, 0.2, true),	// surface property
						6,	// max trace level
						scene,
						studio
				)
		);
		// TODO there is some sort of problem when a ray with trajectory hits the Dove prism

//		scene.addSceneObject(
//				new EditableRayTrajectoryCone(
//						"solid ray trajectory cone",
//						new Vector3D(-1, -0.3, 10),	// start point
//						new Vector3D(1, 0, 0),	// initial direction
//						MyMath.deg2rad(20),
//						20,
//						0.02,	// radius
//						new SemiTransparent(SurfaceColour.RED_SHINY, 0.75),
//						// new SurfaceOfGlowingCloud(DoubleColour.RED, 0.2),
//						100,	// max trace level
//						scene,
//						studio
//				)
//		);

//		scene.addSceneObject(new ParametrisedCylinder(
//				"whiteish cloud",	// description
//				new Vector3D(MyMath.TINY, 0, 10),	// start point
//				new Vector3D(100,0,10),	// end point
//				0.5,	// radius
//				new SurfaceOfGlowingCloud(DoubleColour.WHITE, 5),
//				// new SurfaceOfTintedSolid(0.5, 0.5, 0.5),
//				scene, 
//				studio
//		));


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

		// a camera with a non-zero aperture size (so it simulates blur)
		ApertureCamera camera = new ApertureCamera(
				"Camera",
				new Vector3D(0, 0, 0),	// centre of aperture
				new Vector3D(0, 0, 3),	// view direction (magnitude is distance to detector centre)
				new Vector3D((double)pixelsX/(double)pixelsY, 0, 0),	// horizontal basis Vector3D
				new Vector3D(0, -1, 0),	// vertical basis Vector3D
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
		// open a window, and take a note of its content pane
		Container container = (new PhotoFrame()).getContentPane();

		// define scene, lights and camera
		Studio studio = createStudio();
		
		// trace the rays with trajectory through the scene
		studio.traceRaysWithTrajectory();
		
		// do the ray tracing
		studio.takePhoto();

		// save the image
		studio.savePhoto(FILENAME, "bmp");

		// display the image on the screen
		container.add(new PhotoCanvas(studio.getPhoto()));
		container.validate();
	}
}
