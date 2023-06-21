package optics.raytrace.teaching.electricityAndMagnetism;

import java.awt.*;

import math.*;
import optics.DoubleColour;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectIntersection;
import optics.raytrace.surfaces.SurfaceColour;
import optics.raytrace.surfaces.SurfaceColourLightSourceIndependent;
import optics.raytrace.surfaces.Transparent;
import optics.raytrace.cameras.*;
import optics.raytrace.cameras.PinholeCamera.ExposureCompensationType;
import optics.raytrace.core.*;
import optics.raytrace.GUI.nonInteractive.PhotoCanvas;
import optics.raytrace.GUI.nonInteractive.PhotoFrame;
import optics.raytrace.GUI.sceneObjects.EditableArrow;
import optics.raytrace.GUI.sceneObjects.EditableParametrisedParaboloid;
import optics.raytrace.GUI.sceneObjects.EditableParametrisedPlane;
import optics.raytrace.GUI.sceneObjects.EditableScaledParametrisedCentredParallelogram;
import optics.raytrace.GUI.sceneObjects.EditableScaledParametrisedSphere;


/**
 * Plots a metallic loop in a B field
 * 
 * Adapted from optics.raytrace.NonInteractiveTIM.java
 * 
 * @author Johannes Courtial
 */
public class WireLoopInBField_old
{
	/**
	 * Filename under which main saves the rendered image.
	 * Alter to save the rendered image under a different name.
	 * @return	the filename under which main saves the rendered image.
	 */
	public static String getFilename()
	{
		return "WireLoopInBField.bmp";
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
		// scene.addSceneObject(SceneObjectClass.getLighterChequerboardFloor(scene, studio));	// the checkerboard floor
		// scene.addSceneObject(SceneObjectClass.getSkySphere(scene, studio));	// the sky
		scene.addSceneObject(new EditableScaledParametrisedSphere(
				"sky",
				new Vector3D(0,0,0),	// centre
				MyMath.HUGE,	// huge radius
				new SurfaceColourLightSourceIndependent(DoubleColour.WHITE, true),
				scene,
				studio
			));

		// add any other scene objects

		// add an array of inclined rectangles, representing phase-front pixels
		double dx=.5, dz=.5;
		double normalLength = .5;
		
		for(int i=-2; i<=2; i++)
		{
			for(int j=-2; j<=2; j++)
			{
				// calculate the centre of the pixel
				Vector3D centre = new Vector3D(1.8 + i*dx, 0, 10+j*dz);
				
				// calculate the normal to the phase-front of this pixel
				Vector3D
					normal = new Vector3D(Math.sin(0.75*j*dx), 1, Math.sin(0)).getWithLength(normalLength);
				
				// add an arrow representing the phase-front normal to the scene
				scene.addSceneObject(new EditableArrow(
						"normal",	// description
						centre,	// startPoint
						Vector3D.sum(centre, normal),	// endPoint
						0.02*normalLength,	// shaftRadius
						0.2*normalLength,	// tipLength
						.4,	// tipAngle
						SurfaceColour.RED_SHINY,	// surfaceProperty
						scene,	// parent
						studio
					));

				
				// calculate the span vectors
				Vector3D
					spanVector1 = new Vector3D(dx, -normal.x / normal.y * dx, 0),
					spanVector2 = new Vector3D(0, -normal.z / normal.y * dz, dz);
				
				// add a rectangle representing the phase-front piece to the scene
				scene.addSceneObject(new EditableScaledParametrisedCentredParallelogram(
						"",	// description
						centre, 
						spanVector1,
						spanVector2, 
						0, 1,	// suMin, suMax
						0, 1,	// svMin, svMax
						SurfaceColour.GREY50_SHINY,	// surfaceProperty
						scene,	// parent
						studio
					));
			}
		}

		Vector3D paraboloidVertex = new Vector3D(-1.3, 0.2, 10);
		double paraboloidCoefficient = 0.3;
		SceneObjectIntersection paraboloid = new SceneObjectIntersection("Paraboloid", scene, studio);
		
		paraboloid.addPositiveSceneObject(new EditableParametrisedParaboloid(
				"paraboloid wave front",	// description
				paraboloidVertex,	// vertex
				Vector3D.X,	// uHat
				Vector3D.Z,	// vHat
				new Vector3D(0,-1,0),	// wHat
				paraboloidCoefficient,	// a
				0,	// b
				5,	// height,
				SurfaceColour.GREY50_SHINY,	// surfaceProperty
				paraboloid,	// parent
				studio
			));
		paraboloid.addPositiveSceneObject(new EditableParametrisedPlane(
				"left plane",	// description
				Vector3D.sum(paraboloidVertex, new Vector3D(-1.25, 0, 0)),	// pointOnPlane
				new Vector3D(-1, 0, 0),	// normal
				Transparent.PERFECT,	// SurfaceProperty
				paraboloid,	// parent
				studio
			));
		paraboloid.addPositiveSceneObject(new EditableParametrisedPlane(
				"right plane",	// description
				Vector3D.sum(paraboloidVertex, new Vector3D(1.25, 0, 0)),	// pointOnPlane
				new Vector3D(1, 0, 0),	// normal
				Transparent.PERFECT,	// SurfaceProperty
				paraboloid,	// parent
				studio
			));
		paraboloid.addPositiveSceneObject(new EditableParametrisedPlane(
				"front plane",	// description
				Vector3D.sum(paraboloidVertex, new Vector3D(0, 0, -1.25)),	// pointOnPlane
				new Vector3D(0, 0, -1),	// normal
				Transparent.PERFECT,	// SurfaceProperty
				paraboloid,	// parent
				studio
			));
		paraboloid.addPositiveSceneObject(new EditableParametrisedPlane(
				"back plane",	// description
				Vector3D.sum(paraboloidVertex, new Vector3D(0, 0, 1.25)),	// pointOnPlane
				new Vector3D(0, 0, 1),	// normal
				Transparent.PERFECT,	// SurfaceProperty
				paraboloid,	// parent
				studio
			));
		scene.addSceneObject(paraboloid);

		for(int i=-2; i<=2; i++)
		{
			for(int j=-2; j<=2; j++)
			{
				// calculate the normal to the phase-front of this pixel
				Vector3D
					normal = new Vector3D(2*paraboloidCoefficient*i*dx, 1, 0).getWithLength(normalLength);
				
				// calculate the centre of the pixel
				Vector3D centre = Vector3D.sum(paraboloidVertex, new Vector3D(i*dx, -paraboloidCoefficient*i*dx*i*dx, j*dz));
				//	new Vector3D(-1.3 + i*dx, 0.2 - 0.09*i*i, 10+j*dz);
				
				// add an arrow representing the phase-front normal to the scene
				scene.addSceneObject(new EditableArrow(
						"normal",	// description
						centre,	// startPoint
						Vector3D.sum(centre, normal),	// endPoint
						0.02*normalLength,	// shaftRadius
						0.2*normalLength,	// tipLength
						.4,	// tipAngle
						SurfaceColour.RED_SHINY,	// surfaceProperty
						scene,	// parent
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

		// standard camera (used for image in EPSRC Telescope Windows application)
//		int
//		pixelsX = 640,
//		pixelsY = 480,
//		antiAliasingFactor = 2,
//		raysPerPixel = 100;
//		// If antiAliasingFactor is set to N, the image is calculated at resolution
//		// N*pixelsX x N*pixelsY.
//
//		// a camera with a non-zero aperture size (so it simulates blur)
//		ApertureCamera camera = new ApertureCamera(
//				"Camera",
//				new Vector3D(3, 4, 0),	// centre of aperture
//				new Vector3D(-.3, -.4, 1),	// view direction (magnitude is distance to detector centre)
//				new Vector3D(4.6*(double)pixelsX/(double)pixelsY, 0, 0),	// horizontal basis Vector3D
//				new Vector3D(0, -4, 0),	// vertical basis Vector3D
//				pixelsX*antiAliasingFactor, pixelsY*antiAliasingFactor,	// logical number of pixels
//				100,	// maxTraceLevel
//				10,	// focussing distance
//				0.2,	// aperture radius
//				raysPerPixel	// rays per pixel; the more, the less noise the photo is
//		);

		
//		// VectorField0303201501.bmp
//		int
//		pixelsX = 640,
//		pixelsY = 240,
//		antiAliasingFactor = 2,
//		raysPerPixel = 100;
//		// If antiAliasingFactor is set to N, the image is calculated at resolution
//		// N*pixelsX x N*pixelsY.
//
//		// a camera with a non-zero aperture size (so it simulates blur)
//		ApertureCamera camera = new ApertureCamera(
//				"Camera",
//				new Vector3D(1,4,0),	// new Vector3D(3, 4, 0),	// centre of aperture
//				new Vector3D(-.1, -.4, 1),	// new Vector3D(-.3, -.4, 1),	// view direction (magnitude is distance to detector centre)
//				new Vector3D(2.5*(double)pixelsX/(double)pixelsY, 0, 0),	// horizontal basis Vector3D
//				new Vector3D(0, -2.5, 0),	// vertical basis Vector3D
//				pixelsX*antiAliasingFactor, pixelsY*antiAliasingFactor,	// logical number of pixels
//				100,	// maxTraceLevel
//				10.5,	// focussing distance
//				0.3,	// aperture radius
//				raysPerPixel	// rays per pixel; the more, the less noise the photo is
//		);

		// VectorField0303201502.bmp
		int
		pixelsX = 640,
		pixelsY = 480,
		antiAliasingFactor = 2,
		raysPerPixel = 100;
		// If antiAliasingFactor is set to N, the image is calculated at resolution
		// N*pixelsX x N*pixelsY.

		// a camera with a non-zero aperture size (so it simulates blur)
		ApertureCamera camera = new ApertureCamera(
				"Camera",
				new Vector3D(5,4,0),	// new Vector3D(3, 4, 0),	// centre of aperture
				new Vector3D(-.45, -.4, 1),	// new Vector3D(-.3, -.4, 1),	// view direction (magnitude is distance to detector centre)
				new Vector3D(2.5*pixelsX/pixelsY, 0, 0),	// horizontal basis Vector3D
				new Vector3D(0, -2.5, 0),	// vertical basis Vector3D
				pixelsX*antiAliasingFactor, pixelsY*antiAliasingFactor,	// logical number of pixels
				ExposureCompensationType.EC0,
				100,	// maxTraceLevel
				11.2,	// focussing distance
				0.3,	// aperture radius
				raysPerPixel	// rays per pixel; the more, the less noise the photo is
		);

		studio.setScene(scene);
		// studio.setLights(LightSource.getStandardLightsFromBehind());
		studio.setLights(LightSource.getStandardLightsFromTheRight());
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
