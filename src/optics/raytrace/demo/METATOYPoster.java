package optics.raytrace.demo;

import java.awt.Container;

import math.*;
import math.SpaceTimeTransformation.SpaceTimeTransformationType;
import optics.DoubleColour;
import optics.raytrace.surfaces.*;
import optics.raytrace.sceneObjects.*;
import optics.raytrace.GUI.nonInteractive.PhotoCanvas;
import optics.raytrace.GUI.nonInteractive.PhotoFrame;
import optics.raytrace.cameras.*;
import optics.raytrace.cameras.PinholeCamera.ExposureCompensationType;
import optics.raytrace.cameras.shutterModels.FocusSurfaceShutterModel;
import optics.raytrace.core.*;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.sceneObjects.transformations.*;


/**
 * Render, save and display the official METATOY poster
 * (see http://www.physics.gla.ac.uk/Optics/projects/METATOYs/poster.html).
 * 
 * THIS TAKES A FEW HOURS!
 * 
 * Note that in Project>Properties (or alternatively Run>Run Configurations...) the working
 * directory (which can be found in the arguments tab) has to be set to the directory
 * containing all the images, normally
 * ${workspace_loc:TIM/demo/METATOYPoster}
 * 
 * Adapted from RayTraceJavaApplication.java
 */
public class METATOYPoster
{
	/**
	 * Filename under which main saves the rendered image.
	 */
	private static final String FILENAME = "METATOYPosterTemp.bmp";

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

		// add any other scene objects
		
		// the focus scene
		SceneObjectContainer focus = new SceneObjectContainer("focus scene", null, studio);
		focus.addSceneObject(Plane.zPlane("Plane z=40", 40., (SurfaceProperty)null, scene, studio));
		
		/*
		 * Alasdair's sphere
		 */
		SurfaceProperty AlasdairSurface = new PictureSurfaceDiffuse("AlasdairHamilton.bmp", false, 3.1416, -3.1416, 0, 3.1416, DoubleColour.WHITE);

		ParametrisedSphere2 AlasdairSphere = new ParametrisedSphere2(
				"Alasdair sphere",	// description
				new Vector3D(-4, 1, 14),	// centre
				1,	// radius
				new Vector3D(-0.3,1,0.1),	// direction from the centre to the north pole
				new Vector3D(0,0.1,-1),	// direction from the centre to the intersection between zero-degree meridian and equator
				AlasdairSurface,
				scene,
				studio
		);

		scene.addSceneObject(AlasdairSphere);
		focus.addSceneObject(AlasdairSphere);

		/*
		 * Johannes's sphere
		 */
		SurfaceProperty JohannesSurface = new PictureSurfaceDiffuse("JohannesCourtial.bmp", false, 3.1416, -3.1416, 0, 3.1416, DoubleColour.WHITE);

		ParametrisedSphere2 JohannesSphere = new ParametrisedSphere2(
				"Johannes sphere",	// description
				new Vector3D(2, 0.8, 10),	// centre
				1,	// radius
				new Vector3D(1,1,0),	// direction from the centre to the north pole
				new Vector3D(0,0,-1),	// direction from the centre to the intersection between zero-degree meridian and equator
				JohannesSurface,
				scene,
				studio
		);

		scene.addSceneObject(JohannesSphere);
		focus.addSceneObject(JohannesSphere);

		/*
		 * Dean's sphere
		 */
		SurfaceProperty DeanSurface = new PictureSurfaceDiffuse("DeanLambert.bmp", false, 3.1416, -3.1416, 0, 3.1416, DoubleColour.WHITE);

		ParametrisedSphere2 DeanSphere = new ParametrisedSphere2(
				"Dean sphere",	// description
				new Vector3D(10, 4, 30),	// centre
				1,	// radius
				new Vector3D(-0.3,1,-0.3),	// direction from the centre to the north pole
				new Vector3D(0,0.3,-1),	// direction from the centre to the intersection between zero-degree meridian and equator
				DeanSurface,
				scene,
				studio
		);

		scene.addSceneObject(DeanSphere);
		focus.addSceneObject(DeanSphere);

		/*
		 * Bhuvanesh's sphere
		 */
		SurfaceProperty BhuvaneshSurface = new PictureSurfaceDiffuse("BhuvaneshSundar.bmp", false, 3.1416, -3.1416, 0, 3.1416, DoubleColour.WHITE);

		ParametrisedSphere2 BhuvaneshSphere = new ParametrisedSphere2(
				"Bhuvanesh sphere",	// description
				new Vector3D(-12, 2, 30),	// centre
				1,	// radius
				new Vector3D(0.3,1,0.1),	// direction from the centre to the north pole
				new Vector3D(0,0.1,-1),	// direction from the centre to the intersection between zero-degree meridian and equator
				BhuvaneshSurface,
				scene,
				studio
		);

		scene.addSceneObject(BhuvaneshSphere);
		focus.addSceneObject(BhuvaneshSphere);

		/*
		 * George's sphere
		 */
		SurfaceProperty GeorgeSurface = new PictureSurfaceDiffuse("GeorgeConstable.bmp", false, 2.4, -2.6, 0, 3.1416, DoubleColour.WHITE);

		ParametrisedSphere2 GeorgeSphere = new ParametrisedSphere2(
				"George sphere",	// description
				new Vector3D(-10, 4, 28),	// centre
				1,	// radius
				new Vector3D(-0.3,1,-0.2),	// direction from the centre to the north pole
				new Vector3D(0,-0.2,-1),	// direction from the centre to the intersection between zero-degree meridian and equator
				GeorgeSurface,
				scene,
				studio
		);

		scene.addSceneObject(GeorgeSphere);
		focus.addSceneObject(GeorgeSphere);

		// more interesting objects: a sphere...
		SceneObject s = new ParametrisedSphere(
				"sphere",
				Vector3D.O,	// centre
				1,
				new Vector3D(0.5, 1, 1),	// direction to north pole
				new Vector3D(1, 0, 0),
				// new RayFlipping(0.8)
				// SurfaceColour.RED_SHINY
				new SurfaceTiling(SurfaceColour.BLACK_SHINY, SurfaceColour.GREY90_SHINY, Math.PI/5, Math.PI/5),
				scene,
				studio
			);
		
		// ... translated to a number of places:
		scene.addSceneObject(s.transform(new Translation(new Vector3D(2, 0, 5))));
		scene.addSceneObject(s.transform(new Translation(new Vector3D(-2.5, 0, 6))));
		scene.addSceneObject(s.transform(new Translation(new Vector3D(0.5, 0.1, 8))));
		scene.addSceneObject(s.transform(new Translation(new Vector3D(4, 0.6, 12))));
		scene.addSceneObject(s.transform(new Translation(new Vector3D(-2, 0.5, 30))));
		scene.addSceneObject(s.transform(new Translation(new Vector3D(7, 4, 60))));
		scene.addSceneObject(s.transform(new Translation(new Vector3D(-10, 3, 40))));

		Vector3D
		cylinderStart = new Vector3D(-1, 2., 8.),
		cylinderEnd = new Vector3D(-1, 2, 1000);
		double cylinderRadius = 0.25;
		
		SceneObject cylinder = new ParametrisedCylinderMantle("cylinder",
				cylinderStart,
				cylinderEnd,
				cylinderRadius,
				new Vector3D(1, 0, 0),
				// SurfaceColour.RED_SHINY
				// new SurfaceTiling(SurfaceColour.RED_SHINY, SurfaceColour.GREY90_SHINY, 10000, 2*Math.PI/8)
				new SurfaceTiling(SurfaceColour.RED_SHINY, SurfaceColour.GREY90_SHINY, 0.2, Math.PI/5),
				scene,
				studio
			);
		scene.addSceneObject(cylinder);
		// focus.add(cylinder);
		
		focus.addSceneObject(new CylinderMantle(
				"cylinder bit in front of the window (for focussing)",
				cylinderStart,
				Vector3D.sum(
						cylinderStart,
						Vector3D.difference(cylinderEnd, cylinderStart).getWithLength(3)
					),
				cylinderRadius,
				null,
				scene,
				studio
			));

		SceneObject wwwCylinder = new ParametrisedCylinderMantle("www cylinder",
				new Vector3D(1, -.85, 6),
				new Vector3D(-1.846, -0.85, 5.051),
				0.15,
				new Vector3D(0, -0.2, -1),
				new PictureSurfaceDiffuse("www.gif", false, 3, 0, 3.1416, -3.1416, DoubleColour.WHITE),
				scene,
				studio
			);
		scene.addSceneObject(wwwCylinder);
		focus.addSceneObject(wwwCylinder);

		SceneObject window = new ParametrisedCentredParallelogram(
				"window",
				// new Vector3D(-0.5, 0, -6).add(new Vector3D(-350, 120, -1000).normalise().multiply(5)),
				Vector3D.O,
				new Vector3D(0,3,0),
				new Vector3D(4,0,0),
				new RayFlipping(0.9, true),
				scene,
				studio
			).transform(
				new RotationAroundZAxis(0.3)
			).transform(
				new RotationAroundYAxis(0.0)
			).transform(
				new Translation(new Vector3D(-0.5, 1.5, 11))
			);
		scene.addSceneObject(window);
		// focus.add(window);
		
		// add simultaneous homages to Giorgio de Chirico and In the Night Garden...		
		SceneObject ninkyNonk = new ParametrisedPlane(
				"Ninky Nonk",
				new Vector3D(0, 0, 1000),	// reference point on plane
				new Vector3D(1, 0, 0),
				new Vector3D(0, 1, 0),
				new EitherOrSurface(
						"NinkyNonk.gif",	// aspect ratio is 1027/300
						50, 150, 100*300./1037, 0,
						SurfaceColour.BLACK_MATT,
						Transparent.PERFECT
					),
				scene,
				studio
			);
		scene.addSceneObject(ninkyNonk);
		

		// define the camera
		//
		// Note that the view direction and basis Vector3Ds of the detector are chosen such that
		// the x, y, z axes form a LEFT-handed coordinate system.
		// The reason is that, in the photo, the positive x direction is then to the right,
		// the positive y direction is upwards, and the camera looks in the positive z direction.
		
		boolean
			preview = false,
			binocular = true;
		
		int
			pixelsX, pixelsY, antiAliasingFactor, raysPerPixel;
		
		if(preview)
		{
			pixelsX = 400;
			pixelsY = 300;
			antiAliasingFactor = 1;
			raysPerPixel = 1;
		}
		else
		{
			pixelsX = 4*800;
			pixelsY = 4*600;
			antiAliasingFactor = 1;
			raysPerPixel = 100;
		}

		double
			physicalWidth = 70.,	// physical width (in cm) at which image is intended to be shown; 70cm = poster
			fieldOfViewWidth = 9.;
	
		AnyFocusSurfaceCamera camera = new AnyFocusSurfaceCamera(
				"Camera",
				new Vector3D(0, 1.2, 0),	// centre of aperture
				new Vector3D(0, -0.2, 10),	// view direction (magnitude is distance to detector centre)
				// horizontal basis Vector3D; make the x component negative so that, in the photo,
				// the positive x direction is to the right
				new Vector3D(fieldOfViewWidth, 0, 0),
				new Vector3D(0, -fieldOfViewWidth*pixelsY/pixelsX, 0),	// vertical basis Vector3D
				pixelsX*antiAliasingFactor, pixelsY*antiAliasingFactor,	// logical number of pixels
				ExposureCompensationType.EC0,
				10,	// maxTraceLevel
				focus,	// object(s) to focus on
				0.05,	// aperture radius
				raysPerPixel
		);
		
		RelativisticAnaglyphCamera binocularCamera = new RelativisticAnaglyphCamera(
				"Camera",	// name
				new Vector3D(0, 1.2, 0),	// betweenTheEyes, middle between two eyes
				new Vector3D(0., -0.2, 10),	// centre of view, the point in the centre of both eyes' field of fiew
				new Vector3D(fieldOfViewWidth, 0, 0),	// rightDirection,	a vector pointing to the right
				new Vector3D(0, -fieldOfViewWidth*pixelsY/pixelsX, 0),
				new Vector3D(6./physicalWidth, 0, 0),	// eyeSeparation, separation between the eyes; 6. = eye separation in cm?
				SpaceTimeTransformationType.LORENTZ_TRANSFORMATION,
				new Vector3D(0, 0, 0),	// camera speed (in units of c)
				pixelsX*antiAliasingFactor, pixelsY*antiAliasingFactor,	// logical number of pixels
				ExposureCompensationType.EC0,
				10,	// maxTraceLevel
	            focus,	// focusScene
	            (SceneObject)null,	// scene in camera frame
	            new FocusSurfaceShutterModel(),
	            0.05,	// 0.,	// apertureRadius
	            raysPerPixel,
	            true	// colour
		);
		
		studio.setScene(scene);
		if(binocular)
			studio.setCamera(binocularCamera);
		else
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
		studio.savePhoto(FILENAME, "bmp");

		// display the image on the screen
		container.add(new PhotoCanvas(studio.getPhoto()));
		container.validate();
	}
}
