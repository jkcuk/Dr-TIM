package optics.raytrace.demo;

import java.awt.Container;

import math.*;
import math.SpaceTimeTransformation.SpaceTimeTransformationType;
import optics.raytrace.surfaces.*;
import optics.raytrace.sceneObjects.*;
import optics.raytrace.GUI.nonInteractive.PhotoCanvas;
import optics.raytrace.GUI.nonInteractive.PhotoFrame;
import optics.raytrace.cameras.*;
import optics.raytrace.cameras.PinholeCamera.ExposureCompensationType;
import optics.raytrace.cameras.shutterModels.FocusSurfaceShutterModel;
import optics.raytrace.core.*;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;


/**
 * Note that in Project>Properties (or alternatively Run>Run Configurations...) the working
 * directory (which can be found in the arguments tab) has to be set to the directory
 * containing all the images, normally
 * ${workspace_loc:TIM/demo/}
 * 
 * Adapted from RayTraceJavaApplication.java
 */
public class UniversityCrest3D
{
	/**
	 * Filename under which main saves the rendered image.
	 */
	private static final String FILENAME = "UniversityCrest3DTemp.bmp";

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
		// scene.addSceneObject(SceneObjectClass.getSkySphere(scene, studio));	// the sky
		// scene.addSceneObject(SceneObjectClass.getChequerboardFloor(scene, studio));	// the checkerboard floor

		// add any other scene objects
		
		SceneObject crestPlane = new ParametrisedPlane(
				"crest plane",
				new Vector3D(0, 0, 10),
				new Vector3D(1, 0, 0),
				new Vector3D(0, -1, 0),
				SurfaceColour.RED_SHINY,
				scene,
				studio
			);


		scene.addSceneObject(new MaskedSceneObject(
				crestPlane,
				"UoGCrest.gif",
				-1, 1,
				-1, 1
			));

		scene.addSceneObject(new Plane(
				"background plane",
				new Vector3D(0, 0, 10.5),
				new Vector3D(0, 0, 1),	// normal
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
		
		boolean
			preview = false;
		
		int
			pixelsX, pixelsY;
		double
			antiAliasingFactor, quality;
		
		if(preview)
		{
			quality = 0.5;
			antiAliasingFactor = 1;
		}
		else
		{
			quality = 2;
			antiAliasingFactor = 1;
		}
		pixelsX = (int)(quality*800);
		pixelsY = (int)(quality*600);

		double
			physicalWidth = 70.,	// physical width (in cm) at which image is intended to be shown; 70cm = poster
			fieldOfViewWidth = 3.2;
	
		AnyFocusSurfaceCamera camera = new AnyFocusSurfaceCamera(
				"Camera",
				new Vector3D(0, 0, 0),	// centre of aperture
				new Vector3D(0, 0, 10),	// centre of view (magnitude is distance to detector centre)
				// horizontal basis Vector3D; make the x component negative so that, in the photo,
				// the positive x direction is to the right
				new Vector3D(fieldOfViewWidth, 0, 0),
				new Vector3D(0, -fieldOfViewWidth*pixelsY/pixelsX, 0),	// vertical basis Vector3D
				(int)(pixelsX*antiAliasingFactor), (int)(pixelsY*antiAliasingFactor),	// logical number of pixels
				ExposureCompensationType.EC0,
				10,	// maxTraceLevel
				crestPlane,	// object(s) to focus on
				0.2,	// aperture radius
				10 // rays per pixel in test version
				// 100	// rays per pixel in full version
		);
		
		//Why is this here?  Claims unused
		@SuppressWarnings("unused")
		RelativisticAnaglyphCamera anaglyphCamera = new RelativisticAnaglyphCamera(
				"Camera",	// name
				new Vector3D(0, 0, 0),	// betweenTheEyes, middle between two eyes
				new Vector3D(0., 0, 10),	// centre of view, the point in the centre of both eyes' field of view
				new Vector3D(fieldOfViewWidth, 0, 0),	// rightDirection,	a vector pointing to the right
				new Vector3D(0, -fieldOfViewWidth*pixelsY/pixelsX, 0),
				new Vector3D(6./physicalWidth, 0, 0),	// eyeSeparation, separation between the eyes; 6. = eye separation in cm?
				SpaceTimeTransformationType.LORENTZ_TRANSFORMATION,
				new Vector3D(0, 0, 0),	// camera speed (in units of c)
				(int)(pixelsX*antiAliasingFactor), (int)(pixelsY*antiAliasingFactor),	// logical number of pixels
				ExposureCompensationType.EC0,
				10,	// maxTraceLevel
	            crestPlane,	// focusScene
	            (SceneObject)null,	// scene in camera frame
	            new FocusSurfaceShutterModel(),
	            0.,	// apertureRadius
	            1,	// raysPerPixel
	            false	// colour
		);
		
		double
		eyeSeparation = 0.8;	// 0.1 or so for poster; 0.8 or so for small image
		// the visible area in the group's posters is of size 70cm x 50cm
	
		
		// Why is this here?  Claims unused
		@SuppressWarnings("unused")
		CameraClass autostereogramCamera = new AutostereogramCamera(
				"autostereogram camera",	// name,
				new Vector3D(-0.5*eyeSeparation, 0, 0),	// left eye
				new Vector3D( 0.5*eyeSeparation, 0, 0),	// right eye
				new Vector3D(0, 0, 5),	// centre of autostereogram,
				new Vector3D(0.5*fieldOfViewWidth, 0, 0),	// horizontal span vector
				new Vector3D(0, -0.5*fieldOfViewWidth*pixelsY/pixelsX, 0),	// vertical span vector
				2.0/quality/quality,	// quality*10.,	// dots per pixel (on average)
				1.*quality,	// dot radius, in pixels
				(int)(pixelsX*antiAliasingFactor),	(int)(pixelsY*antiAliasingFactor),	// imagePixelsHorizontal, imagePixelsVertical,
				100	// maxTraceLevel
			);

		
		studio.setScene(scene);
		studio.setCamera(camera);
		// studio.setCamera(anaglyphCamera);
		// studio.setCamera(autostereogramCamera);
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
		studio.savePhoto(FILENAME, "bmp");

		// display the image on the screen
		container.add(new PhotoCanvas(studio.getPhoto()));
		container.validate();
	}
}
