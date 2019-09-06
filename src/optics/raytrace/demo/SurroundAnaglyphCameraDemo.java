package optics.raytrace.demo;

import java.awt.Container;

import math.*;
import optics.raytrace.panorama.panorama3DGeometry.AbstractPanorama3DGeometry;
import optics.raytrace.panorama.panorama3DGeometry.GeneralisedStandardPanorama3DGeometry;
import optics.raytrace.panorama.panorama3DGeometry.StandardPanorama3DGeometry;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.surfaces.SurfaceColour;
import optics.raytrace.surfaces.SurfaceTiling;
import optics.raytrace.GUI.nonInteractive.PhotoCanvas;
import optics.raytrace.GUI.nonInteractive.PhotoFrame;
import optics.raytrace.GUI.sceneObjects.EditableCylinderLattice;
import optics.raytrace.GUI.sceneObjects.EditableParametrisedCylinder;
import optics.raytrace.GUI.sceneObjects.EditableScaledParametrisedSphere;
import optics.raytrace.cameras.*;
import optics.raytrace.cameras.SurroundAnaglyphCamera.OutputType;
import optics.raytrace.core.*;


/**
 * Surround-anaglyph-camera demo
 * 
 * Note that in Project>Properties (or alternatively Run>Run Configurations...) the working
 * directory (which can be found in the arguments tab) has to be set to the directory
 * containing all the images, normally
 * ${workspace_loc:TIM/demo/}
 * 
 * Adapted from RayTraceJavaApplication.java
 */
public class SurroundAnaglyphCameraDemo
{
	/**
	 * Filename under which main saves the rendered image.
	 * Alter to save the rendered image under a different name.
	 * @return	the filename under which main saves the rendered image.
	 */
	public static String getFilename()
	{
		return "SurroundAnaglyphCameraDemo.bmp";
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
		
//		// the interesting objects: a couple of spheres...
//		SceneObject s1 = new EditableScaledParametrisedSphere(
//				"sphere 1",	// description,
//				new Vector3D(-2, 0, 10),	// centre,
//				1,	// radius,
//				new Vector3D(0.5, 1, 1),	// pole,
//				new Vector3D(1, 0, 0),	// phi0Direction,
//				0, 5,	// sThetaMin, sThetaMax,
//				0, 10,	// sPhiMin, sPhiMax,
//				new SurfaceTiling(SurfaceColour.BLACK_SHINY, SurfaceColour.GREY90_SHINY, 1, 1),	// surfaceProperty,
//				scene,	// parent, 
//				studio
//			);
//		
//		SceneObject s2 = new EditableScaledParametrisedSphere(
//				"sphere 2",	// description,
//				new Vector3D(0, 0, 11),	// centre,
//				1,	// radius,
//				new Vector3D(0.5, 1, 1),	// pole,
//				new Vector3D(1, 0, 0),	// phi0Direction,
//				0, 5,	// sThetaMin, sThetaMax,
//				0, 10,	// sPhiMin, sPhiMax,
//				new SurfaceTiling(SurfaceColour.BLACK_SHINY, SurfaceColour.GREY90_SHINY, 1, 1),	// surfaceProperty,
//				scene,	// parent, 
//				studio
//			);
//		
//		SceneObject s3 = new EditableScaledParametrisedSphere(
//				"sphere 3",	// description,
//				new Vector3D(2, 0, 12),	// centre,
//				1,	// radius,
//				new Vector3D(0.5, 1, 1),	// pole,
//				new Vector3D(1, 0, 0),	// phi0Direction,
//				0, 5,	// sThetaMin, sThetaMax,
//				0, 10,	// sPhiMin, sPhiMax,
//				new SurfaceTiling(SurfaceColour.BLACK_SHINY, SurfaceColour.GREY90_SHINY, 1, 1),	// surfaceProperty,
//				scene,	// parent, 
//				studio
//			);
//
//		SceneObject s4 = new EditableScaledParametrisedSphere(
//				"sphere 4",	// description,
//				new Vector3D(4, 0, 13),	// centre,
//				1,	// radius,
//				new Vector3D(0.5, 1, 1),	// pole,
//				new Vector3D(1, 0, 0),	// phi0Direction,
//				0, 5,	// sThetaMin, sThetaMax,
//				0, 10,	// sPhiMin, sPhiMax,
//				new SurfaceTiling(SurfaceColour.BLACK_SHINY, SurfaceColour.GREY90_SHINY, 1, 1),	// surfaceProperty,
//				scene,	// parent, 
//				studio
//			);
		
//		// ... and a cylinder
//		SceneObject c = new EditableParametrisedCylinder(
//				"cylinder",	// description,
//				new Vector3D(1, -0.8, 0),	// startPoint,
//				new Vector3D(1, -0.8, 1000),	// endPoint,
//				0.2,	// radius,
//				new SurfaceTiling(SurfaceColour.RED_SHINY, SurfaceColour.GREY90_SHINY, 0.2, Math.PI/5),	// surfaceProperty,
//				scene,	// parent, 
//				studio
//			);
		
//		scene.addSceneObject(s1);
//		scene.addSceneObject(s2);
//		scene.addSceneObject(s3);
//		scene.addSceneObject(s4);
		// scene.addSceneObject(c);

//		scene.addSceneObject(new PotatoHead(
//				scene,	// parent, 
//				studio	// the studio
//			));
		
		scene.addSceneObject(new EditableScaledParametrisedSphere(
				"sphere",	// description,
				new Vector3D(0, 0, 10),	// centre,
				1,	// radius,
				new Vector3D(0.5, 1, 1),	// pole,
				new Vector3D(1, 0, 0),	// phi0Direction,
				0, 5,	// sThetaMin, sThetaMax,
				0, 10,	// sPhiMin, sPhiMax,
				SurfaceColour.BLUE_SHINY,	// surfaceProperty,
				scene,	// parent, 
				studio
		));

		// TIM's head doesn't work, for some reason
//		scene.addSceneObject(new EditableTimHead(
//				"TIM's head",
//				new Vector3D(0, 0, 10),
//				scene,
//				studio));
		
		scene.addSceneObject(new EditableCylinderLattice(
				"cylinder lattice",
				-20, 20, 4,
				-.9, 5, 4,
				-20, 20, 4,
				0.1,
				scene,
				studio
		));
		
		scene.addSceneObject(new EditableScaledParametrisedSphere(
				"nearby sphere to the right",	// description,
				new Vector3D(3, 0, 0),	// centre,
				.3,	// radius,
				new Vector3D(0.5, 1, 1),	// pole,
				new Vector3D(1, 0, 0),	// phi0Direction,
				0, 5,	// sThetaMin, sThetaMax,
				0, 10,	// sPhiMin, sPhiMax,
				SurfaceColour.GREEN_MATT,	// surfaceProperty,
				scene,	// parent, 
				studio
		));
		
		// ... and a cylinder
		scene.addSceneObject(new EditableParametrisedCylinder(
				"cylinder",	// description,
				new Vector3D(-5, -0.8, -5),	// startPoint,
				new Vector3D(-5, -0.8, -1000),	// endPoint,
				0.2,	// radius,
				new SurfaceTiling(SurfaceColour.RED_SHINY, SurfaceColour.GREY90_SHINY, 0.2, Math.PI/5),	// surfaceProperty,
				scene,	// parent, 
				studio
			));
		
		scene.addSceneObject(new EditableScaledParametrisedSphere(
				"scene above the camera",	// description,
				new Vector3D(0, 5, 0),	// centre,
				.3,	// radius,
				new Vector3D(0.5, 1, 1),	// pole,
				new Vector3D(1, 0, 0),	// phi0Direction,
				0, 5,	// sThetaMin, sThetaMax,
				0, 10,	// sPhiMin, sPhiMax,
				SurfaceColour.GREEN_MATT,	// surfaceProperty,
				scene,	// parent, 
				studio
		));

//		scene.addSceneObject(new EditableScaledParametrisedSphere(
//				"sphere 2",	// description,
//				new Vector3D(2, 1, 20),	// centre,
//				1,	// radius,
//				new Vector3D(0.5, 1, 1),	// pole,
//				new Vector3D(1, 0, 0),	// phi0Direction,
//				0, 5,	// sThetaMin, sThetaMax,
//				0, 10,	// sPhiMin, sPhiMax,
//				new SurfaceTiling(SurfaceColour.BLACK_SHINY, SurfaceColour.GREY90_SHINY, .25, .25),	// surfaceProperty,
//				scene,	// parent, 
//				studio
//		));

		
		// define the camera
		//
		// Note that the view direction and basis Vector3Ds of the detector are chosen such that
		// the x, y, z axes form a LEFT-handed coordinate system.
		// The reason is that, in the photo, the positive x direction is then to the right,
		// the positive y direction is upwards, and the camera looks in the positive z direction.
		double
			verticalAngleOfView = 180.0;	// in degrees
		int
			quality = 1,	// 1 = normal, 2 = good, 4 = great
			pixelsY = 480*quality,
			pixelsX = (int)(pixelsY*360.0/verticalAngleOfView),
			antiAliasingFactor = 1;
		
//		CameraClass camera = new SurroundAnaglyphCamera(
//				"Surround anaglyph camera",	// description
//				new Vector3D(0, 0, 0),	// betweenTheEyes
//				new Vector3D(1, 0, 0),	// right0deg
//				new Vector3D(0, 1, 0), 	// up
//				0.4,	// eyeSeparation,
//				OutputType.ANAGLYPH_REDBLUE,
//				verticalAngleOfView,	// verticalAngleOfView, in degrees
//				ProjectionType.SPHERICAL,
//				pixelsX*antiAliasingFactor,	pixelsY*antiAliasingFactor,	// imagePixelsHorizontal, imagePixelsVertical,
//				100	// maxTraceLevel
//			);
		
		AbstractPanorama3DGeometry g;
		switch(1)
		{
		case 1:
			g = new StandardPanorama3DGeometry(
					new Vector3D(0, 0, 0),	// M,
					new Vector3D(0, 1, 0),	// theta0,
					new Vector3D(0, 0, -1),	// phi0,
					0.4	// i
					);
			break;
		case 2:
		default:
			g = new GeneralisedStandardPanorama3DGeometry(
					new Vector3D(0, 0, 0),	// M,
					new Vector3D(0, 1, 0),	// theta0,
					new Vector3D(0, 0, -1),	// phi0,
					0.4,	// i
					1	// R
				);
		}

		CameraClass camera = new SurroundAnaglyphCamera(
				"Surround anaglyph camera",	// description,
				g,	// panorama3DGeometry,
				OutputType.ANAGLYPH_REDBLUE,	// outputType,
				pixelsX*antiAliasingFactor,	pixelsY*antiAliasingFactor,	// detectorPixelsHorizontal, detectorPixelsVertical,
				100	// maxTraceLevel
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
		studio.savePhoto(getFilename(), "bmp");

		// display the image on the screen
		container.add(new PhotoCanvas(studio.getPhoto()));
		container.validate();
	}
}
