package optics.raytrace.demo;

import java.awt.Container;

import math.*;
import optics.raytrace.sceneObjects.SphericalCap;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.surfaces.RayRotating;
import optics.raytrace.surfaces.SurfaceColour;
import optics.raytrace.GUI.nonInteractive.PhotoCanvas;
import optics.raytrace.GUI.nonInteractive.PhotoFrame;
import optics.raytrace.GUI.sceneObjects.EditableRayTrajectoryCone;
import optics.raytrace.cameras.*;
import optics.raytrace.cameras.PinholeCamera.ExposureCompensationType;
import optics.raytrace.core.*;


/**
 * Spherical ray-rotating surface, the "building block" of a complex lens
 * 
 * Note that in Project>Properties (or alternatively Run>Run Configurations...) the working
 * directory (which can be found in the arguments tab) has to be set to the directory
 * containing all the images, normally
 * ${workspace_loc:TIM/demo/}
 * 
 * Adapted from RayTraceJavaApplication.java
 */
public class SphericalRotatingSurfaceRayTrajectoryConeDemo
{
	/**
	 * Filename under which main saves the rendered image.
	 */
	private static final String FILENAME = "SphericalRotatingSurfaceRayTrajectoryDemoTemp.bmp";

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

//		double radius = 10;
//		SceneObject sphere = new ParametrisedSphere(
//				"sphere",	// description,
//				new Vector3D(-radius,1,10),	// centre
//				radius,	// radius
//				new Vector3D(0,0,1),	// pole,
//				new Vector3D(1,0,0),	// phi0Direction,
//				new RayRotating(MyMath.deg2rad(90), 1),	// surfaceProperty,
//				scene,	// parent, 
//				studio
//			);
//
//		SceneObject plane = new Plane(
//				"plane",	// description,
//				new Vector3D(-0.5,1,0),	// pointOnPlane,
//				new Vector3D(-1,0,0),	// normal, 
//				new Transparent(1.0),	// surfaceProperty,
//				scene,	// parent,
//				studio
//			);
//
//		scene.addSceneObject(new SceneObjectIntersection(
//				"spherical surface",
//				sphere,
//				plane,
//				scene,	// parent
//				studio
//			));
		
		scene.addSceneObject(new SphericalCap(
				"sphere cap",	// description,
				new Vector3D(0, 0, 10),	// capCentre,
				new Vector3D(-4, 0, 10),	// directionToFront,
				1,	// apertureRadius,
				false,	// closed?
				new RayRotating(MyMath.deg2rad(90), 0.9, true),	// surfaceProperty,
				scene,	// parent, 
				studio
			));

//		scene.addSceneObject(new EditableLens(
//				"complex lens",	// description,
//				2,	// apertureRadius,
//				10,	// radiusOfCurvatureFront,
//				10,	// radiusOfCurvatureBack,
//				new Vector3D(0, 1, 10),	// centre,
//				new Vector3D(1, 0, 0),	// directionToFront,
//				new RayRotating(MyMath.deg2rad(90), 0.9),	// surfacePropertyFront,
//				new RayRotating(MyMath.deg2rad(90), 0.9),	// surfacePropertyBack,
//				scene,	// parent, 
//				studio
//		));

			
//		scene.addSceneObject(new EditableLens(
//				"complex lens",	// description
//				1,	// aperture radius
//				Complex.fromPolar(2.,0.5*Math.PI),	// focal length
//				1.5,	// modulus of refractive index
//				new Vector3D(0, 0, 10),	// centre
//				new Vector3D(1, 0, 0),	// direction to front
//				scene,
//				studio
//		));
		
//		scene.addSceneObject(new EditableRectangle(
//				"ray-rotation window",
//				new Vector3D(0, 0, 10),	// centre
//				new Vector3D(1, 0, 0),	// normal
//				new Vector3D(0, 0, 1),	// width vector
//				1,	// aspect ratio
//				new RayRotating(MyMath.deg2rad(150), 0.9)
//		));

		// a light ray with trajectory, which bounces about between the two mirrors;
		// to trace the rays with trajectory through the scene, need to add
		// "studio.traceRaysWithTrajectory();" later
		scene.addSceneObject(
				new EditableRayTrajectoryCone(
						"ray trajectory cone 1",
						new Vector3D(-4, 0, 10),	// start point
						0,	// start time
						new Vector3D(1, 0, 0),	// axis direction
						MyMath.deg2rad(10),
						10,
						0.02,	// radius
						// SurfacePropertyContainer.RED_SHINY_SEMITRANSPARENT,
						SurfaceColour.RED_SHINY,
						10,	// max trace level
						scene, 
						studio
				)
		);

		scene.addSceneObject(
				new EditableRayTrajectoryCone(
						"ray trajectory cone 2",
						new Vector3D(-2, 0, 10),	// start point
						0,	// start time
						new Vector3D(1, 0, 0),	// axis direction
						MyMath.deg2rad(20),
						10,
						0.02,	// radius
						// SurfacePropertyContainer.RED_SHINY_SEMITRANSPARENT,
						SurfaceColour.GREEN_SHINY,
						10,	// max trace level
						scene, 
						studio
				)
		);
		
		scene.addSceneObject(
				new EditableRayTrajectoryCone(
						"ray trajectory cone 1",
						new Vector3D(-8, 0, 10),	// start point
						0,	// start time
						new Vector3D(1, 0, 0),	// axis direction
						MyMath.deg2rad(5),
						10,
						0.02,	// radius
						// SurfacePropertyContainer.RED_SHINY_SEMITRANSPARENT,
						SurfaceColour.WHITE_SHINY,
						10,	// max trace level
						scene, 
						studio
				)
		);

		//		scene.addSceneObject(
//				new EditableRayTrajectoryCone(
//						"ray trajectory cone 2",
//						new Vector3D(-4, 0, 10),	// start point
//						new Vector3D(1, 0, 0),	// axis direction
//						MyMath.deg2rad(5),
//						10,
//						0.02,	// radius
//						// SurfacePropertyContainer.RED_SHINY_SEMITRANSPARENT,
//						SurfaceColour.GREEN_SHINY,
//						100,	// max trace level
//						scene, 
//						studio
//				)
//		);
//
//		scene.addSceneObject(
//				new EditableRayTrajectoryCone(
//						"ray trajectory cone 3",
//						new Vector3D(-4, 2, 10),	// start point
//						new Vector3D(1, 0, 0),	// axis direction
//						MyMath.deg2rad(10),
//						10,
//						0.02,	// radius
//						// SurfacePropertyContainer.RED_SHINY_SEMITRANSPARENT,
//						SurfaceColour.RED_SHINY,
//						100,	// max trace level
//						scene, 
//						studio
//				)
//		);
//		scene.addSceneObject(
//				new EditableRayTrajectoryCone(
//						"ray trajectory cone 4",
//						new Vector3D(-4, 2, 10),	// start point
//						new Vector3D(1, 0, 0),	// axis direction
//						MyMath.deg2rad(5),
//						10,
//						0.02,	// radius
//						// SurfacePropertyContainer.RED_SHINY_SEMITRANSPARENT,
//						SurfaceColour.GREEN_SHINY,
//						100,	// max trace level
//						scene, 
//						studio
//				)
//		);

		// define the camera
		//
		// Note that the view direction and basis Vector3Ds of the detector are chosen such that
		// the x, y, z axes form a LEFT-handed coordinate system.
		// The reason is that, in the photo, the positive x direction is then to the right,
		// the positive y direction is upwards, and the camera looks in the positive z direction.
		int
//			pixelsX = 300,	// draft mode
//			pixelsY = 200,	// draft mode
			quality = 1,	// 1 = normal, 2 = good, 4 = great
			pixelsX = 640*quality,
			pixelsY = 480*quality,
			antiAliasingFactor = 1;

		// a camera with a non-zero aperture size (so it simulates blur)
		ApertureCamera camera = new ApertureCamera(
				"Camera",
				new Vector3D(0, 0, 0),	// centre of aperture
				new Vector3D(0, 0, 1.5),	// view direction (magnitude is distance to detector centre)
				new Vector3D(-(double)pixelsX/pixelsY, 0, 0),	// horizontal basis Vector3D
				new Vector3D(0, -1, 0),	// vertical basis Vector3D
				pixelsX*antiAliasingFactor, pixelsY*antiAliasingFactor,	// logical number of pixels
				ExposureCompensationType.EC0,
				100,	// maxTraceLevel
				10,	// focussing distance
				0.0,	// aperture radius
				1	// rays per pixel; only has an effect if the aperture radius is >0; then the more rays, the less noisy the photo is
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
		System.out.println("Tracing rays with trajectory...");
		studio.traceRaysWithTrajectory();
		
		// do the ray tracing
		System.out.println("Taking photo...");
		studio.takePhoto();

		// save the image
		System.out.println("Saving photo...");
		studio.savePhoto(FILENAME, "bmp");

		// display the image on the screen
		container.add(new PhotoCanvas(studio.getPhoto()));
		container.validate();
	}
}
