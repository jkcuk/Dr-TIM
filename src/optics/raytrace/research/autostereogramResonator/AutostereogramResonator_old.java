package optics.raytrace.research.autostereogramResonator;

import java.awt.Container;

import math.*;
import optics.DoubleColour;
import optics.raytrace.lights.AmbientLight;
import optics.raytrace.surfaces.*;
import optics.raytrace.sceneObjects.Parallelepiped;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.sceneObjects.transformations.RotationAroundXAxis;
import optics.raytrace.sceneObjects.transformations.RotationAroundYAxis;
import optics.raytrace.sceneObjects.transformations.RotationAroundZAxis;
import optics.raytrace.sceneObjects.transformations.Translation;
import optics.raytrace.GUI.nonInteractive.PhotoCanvas;
import optics.raytrace.GUI.nonInteractive.PhotoFrame;
import optics.raytrace.GUI.sceneObjects.EditableParametrisedPlane;
import optics.raytrace.GUI.sceneObjects.EditableScaledParametrisedSphere;
import optics.raytrace.GUI.surfaces.EditablePictureSurfaceDiffuse;
import optics.raytrace.cameras.*;
import optics.raytrace.cameras.PinholeCamera.ExposureCompensationType;
import optics.raytrace.core.*;


/**
 * Autostereogram resonator
 * 
 * Note that in Project>Properties (or alternatively Run>Run Configurations...) the working
 * directory (which can be found in the arguments tab) has to be set to the directory
 * containing all the images, normally
 * ${workspace_loc:TIM/demo/}
 * 
 * Adapted from RayTraceJavaApplication.java
 */
public class AutostereogramResonator_old
{
	/**
	 * the simulation parameters
	 */
	private static double
		e = 6,	// the separation between the eyes, in cm
		a = 50,	// distance from eye to plane A
		s = 67;	// distance from eye to surface S (so length of resonator is s-a)
	
	/**
	 * Filename under which main saves the rendered image.
	 */
	private static final String FILENAME = "autostereogramResonator.bmp";
	
	private static final boolean SAVE_FILE = true;

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
		// scene.addSceneObject(SceneObjectClass.getChequerboardFloor(studio));	// the checkerboard floor

		//
		// add the resonator mirrors
		//
		
		Vector3D mirrorACentre = new Vector3D(0, 0, a);
		
		scene.addSceneObject(new EditableParametrisedPlane(
				"mirror A",
				mirrorACentre,	// centre
				new Vector3D(1, 0, 0),	// width vector
				new Vector3D(0, 1, 0),	// height vector
				// false,	// shadow-throwing?
				new TwoSidedSurface(new Transparent(), new Reflective()),
				scene,
				studio
		));
		
		scene.addSceneObject(new EditableParametrisedPlane(
				"lens in front of mirror A",
				new Vector3D(0, 0, a - MyMath.TINY),	// centre
				new Vector3D(1, 0, 0),	// width vector
				new Vector3D(0, 1, 0),	// height vector
				// false,	// shadow-throwing?
				new IdealThinLensSurfaceSurfaceCoordinates(
						new Vector2D(0, 0),	// opticalAxisIntersectionCoordinates
						a,	// focalLength,
						1,	// transmissionCoefficient
						true	// shadow-throwing
					),
				scene,
				studio
		));
		
		scene.addSceneObject(new EditableParametrisedPlane(
				"lens behind mirror A",
				new Vector3D(0, 0, a + MyMath.TINY),	// centre
				new Vector3D(1, 0, 0),	// width vector
				new Vector3D(0, 1, 0),	// height vector
				// false,	// shadow-throwing?
				new IdealThinLensSurfaceSurfaceCoordinates(
						new Vector2D(0, 0),	// opticalAxisIntersectionCoordinates
						-a,	// focalLength,
						1,	// transmissionCoefficient
						true	// shadow-throwing
					),
				scene,
				studio
		));

		// Vector3D mirrorSCentre = new Vector3D(0, 0, s);
		
		Vector3D
			eye1 = new Vector3D(-e/2, 0, 0),
			eye2 = new Vector3D(e/2, 0, 0);
		
		double sphereRadius = 8;
		double stickingOutBy = 7;
		
//		scene.addSceneObject(new Sphere(
//				"sphere",
//				new Vector3D(8, 3, s + sphereRadius - stickingOutBy),
//				sphereRadius,
//				new Point2PointImaging(eye1, eye2, 1, true),
//				scene,
//				studio
//			));

		scene.addSceneObject(new EditableScaledParametrisedSphere(
				"sphere",
				new Vector3D(8, 3, s + sphereRadius - stickingOutBy),
				sphereRadius,
				new Point2PointImagingPhaseHologram(eye1, eye2, 1, true, true),
				scene,
				studio
			));

		scene.addSceneObject(new Parallelepiped(
				"Parallelepiped",	// description,
				new Vector3D(0, 0, 0),	// centre,
				new Vector3D(10, 0, 0),	// u,
				new Vector3D(0, 10, 0),	// v,
				new Vector3D(0, 0, 10),	// w,
				new Point2PointImagingPhaseHologram(eye1, eye2, 1, true, true),
				scene,	// parent,
				studio
			).transform(new RotationAroundXAxis(MyMath.deg2rad(45))
			).transform(new RotationAroundYAxis(MyMath.deg2rad(30))
			).transform(new RotationAroundZAxis(MyMath.deg2rad(20))
			).transform(new Translation(new Vector3D(-9, -2, s+2)))
		);

		scene.addSceneObject(new EditableParametrisedPlane(
				"mirror S",
				new Vector3D(0, 0, s),	// centre
				new Vector3D(1, 0, 0),	// width vector
				new Vector3D(0, 1, 0),	// height vector
				// false,	// shadow-throwing?
				new Point2PointImagingPhaseHologram(eye1, eye2, 1, true, true),
				scene,
				studio
		));
		
		// the picture surface...
		double
			aspectRatio = 153./1930.,
			width = 1.6,
			xLeft = 12,
			height = width / aspectRatio,
			xRight = xLeft + width,	// display mirror image, as we're effectively seeing it in a mirror!
			yBottom = -height/2,
			yTop = +height/2;
		
		java.net.URL imgURL = (new AutostereogramResonator_old()).getClass().getResource("AutostereogramResonatorImage.jpg");
		SurfaceProperty pictureSurface = new EditablePictureSurfaceDiffuse(imgURL, true, xLeft, xRight, yTop, yBottom);

		// ... and the object with that particular picture surface
		scene.addSceneObject(new EditableParametrisedPlane(
				"picture",
				new Vector3D(0, 0, a + 2*MyMath.TINY),	// centre
				new Vector3D(1, 0, 0),	// width vector
				new Vector3D(0, 1, 0),	// height vector
				// false,	// shadow-throwing?
				pictureSurface,
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
		pixelsX = 400,
		pixelsY = 300,
		antiAliasingFactor = 2;

		// a camera with a non-zero aperture size (so it simulates blur)
		Vector3D apertureCentre = new Vector3D(-e/2, 0, 0);
		
		ApertureCamera camera = new ApertureCamera(
				"One eye",
				apertureCentre,	// centre of aperture
				Vector3D.difference(mirrorACentre, apertureCentre).getWithLength(a),	// view direction (magnitude is distance to detector centre)
				new Vector3D(20*(double)pixelsX/pixelsY, 0, 0),	// horizontal basis Vector3D
				new Vector3D(0, -20, 0),	// vertical basis Vector3D
				pixelsX*antiAliasingFactor, pixelsY*antiAliasingFactor,	// logical number of pixels
				ExposureCompensationType.EC0,
				1000,	// maxTraceLevel
				a,	// focussing distance
				0.0,	// aperture radius
				1	// rays per pixel; the more, the less noise the photo is
		);
		
		studio.setScene(scene);
		studio.setCamera(camera);		
		// studio.setLights(LightSource.getStandardLightsFromBehind(1));
		studio.setLights(new AmbientLight("background light", DoubleColour.WHITER));
		
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
		if(SAVE_FILE) studio.savePhoto(FILENAME, "bmp");

		// display the image on the screen
		container.add(new PhotoCanvas(studio.getPhoto()));
		container.validate();
	}
}
