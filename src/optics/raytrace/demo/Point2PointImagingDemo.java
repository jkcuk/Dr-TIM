package optics.raytrace.demo;

import java.awt.Container;

import math.*;
import optics.raytrace.sceneObjects.Cylinder;
import optics.raytrace.sceneObjects.Parallelepiped;
import optics.raytrace.sceneObjects.ParametrisedCentredParallelogram;
import optics.raytrace.sceneObjects.Sphere;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.sceneObjects.transformations.RotationAroundXAxis;
import optics.raytrace.sceneObjects.transformations.RotationAroundYAxis;
import optics.raytrace.sceneObjects.transformations.RotationAroundZAxis;
import optics.raytrace.sceneObjects.transformations.Translation;
import optics.raytrace.surfaces.Point2PointImagingPhaseHologram;
import optics.raytrace.surfaces.SurfaceColour;
import optics.raytrace.GUI.nonInteractive.PhotoCanvas;
import optics.raytrace.GUI.nonInteractive.PhotoFrame;
import optics.raytrace.cameras.*;
import optics.raytrace.cameras.PinholeCamera.ExposureCompensationType;
import optics.raytrace.core.*;


/**
 * Point-to-point-imaging surface demo.
 * 
 * A point-to-point-imaging surface is a phase hologram that images two specific points into each other.
 * In this example, those two points are the camera position and the position of the red sphere in the top right quadrant.
 * If the surface property works, the square, the cylinder, and the cube should all be various shades of red.
 * The sphere should be green. 
 * 
 * Note that in Project>Properties (or alternatively Run>Run Configurations...) the working
 * directory (which can be found in the arguments tab) has to be set to the directory
 * containing all the images, normally
 * ${workspace_loc:TIM/demo/}
 * 
 * Adapted from AutostereogramCameraDemo2.java
 */
public class Point2PointImagingDemo
{
	/**
	 * Filename under which main saves the rendered image.
	 * Alter to save the rendered image under a different name.
	 * @return	the filename under which main saves the rendered image.
	 */
	public static String getFilename()
	{
		return "Point2PointImagingDemoTemp.bmp";
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
		
		Vector3D
			point1 = new Vector3D(0, 0, 0),
			point2 = new Vector3D(1, 0.5, 5);
		
		// define a suitable reflective point-to-point-imaging surface property
		SurfaceProperty p2pImagingSurfaceReflective = new Point2PointImagingPhaseHologram(
				point1, point2,	// the points that are being imaged into each other
				2,	// throughput factor
				true,	// true = works in reflection, false = works in transmission
				true	// shadow-throwing
			);

		// ... and a transmissive point-to-point-imaging surface property
		SurfaceProperty p2pImagingSurfaceTransmissive = new Point2PointImagingPhaseHologram(
				point1, point2,	// the points that are being imaged into each other
				1,	// throughput factor
				false,	// true = works in reflection, false = works in transmission
				true	// shadow-throwing
			);

		// put a suitable object in the position of point 2
		scene.addSceneObject(new Sphere(
				"the object at the position of the image",
				point2,	// centre
				0.01,	// radius
				SurfaceColour.YELLOW_MATT,
				scene,
				studio
			));
		
//		scene.addSceneObject(new Plane(
//				"plane",	// description,
//				new Vector3D(0, 0, 11),	// pointOnPlane,
//				new Vector3D(0, 0, 1),	// normal, 
//				SurfaceColour.RED_MATT,	// surfaceProperty,
//				scene,	// parent,
//				studio	// studio
//			));
		
		scene.addSceneObject(new ParametrisedCentredParallelogram(
				"rectangle",
				new Vector3D(-0.5, 0.2, 3),
				new Vector3D(0.5,0,0),
				new Vector3D(0,0.5,0),
				p2pImagingSurfaceTransmissive,
				// SurfaceColour.BLUE_SHINY,
				scene,
				studio
			));

		scene.addSceneObject(new Sphere(
				"green sphere",	// description,
				new Vector3D(-0.6, -0.5, 10),	// centre,
				0.6,	// radius,
				// p2pImagingSurfaceReflective,
				SurfaceColour.GREEN_MATT,	// surfaceProperty,
				scene,	// parent, 
				studio
		));

		scene.addSceneObject(new Parallelepiped(
				"Parallelepiped",	// description,
				new Vector3D(0, 0, 0),	// centre,
				new Vector3D(1, 0, 0),	// u,
				new Vector3D(0, 1, 0),	// v,
				new Vector3D(0, 0, 1),	// w,
				p2pImagingSurfaceReflective,
				// SurfaceColour.WHITE_SHINY,	// surfaceProperty,
				scene,	// parent,
				studio
			).transform(new RotationAroundXAxis(MyMath.deg2rad(30))
			).transform(new RotationAroundYAxis(MyMath.deg2rad(40))
			).transform(new RotationAroundZAxis(MyMath.deg2rad(60))
			).transform(new Translation(new Vector3D(0.7, 0.1, 9.2)))
		);
		
		scene.addSceneObject(new Cylinder(
				"cylinder",	// description,
				new Vector3D(-1.2, 0.5, 10),	// startPoint,
				new Vector3D(-0.2, 0.7, 9.),	// endPoint,
				0.5,	// radius,
				p2pImagingSurfaceReflective,
				// SurfaceColour.GREEN_MATT,	// surfaceProperty,
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
		// open a window, and take a note of its content pane
		Container container = (new PhotoFrame()).getContentPane();

		// define scene, lights and camera
		Studio studio = createStudio();

		// do the ray tracing
		studio.takePhoto();

		// save the image
		// studio.savePhoto(getFilename(), "bmp");

		// display the image on the screen
		container.add(new PhotoCanvas(studio.getPhoto()));
		container.validate();
	}
}
