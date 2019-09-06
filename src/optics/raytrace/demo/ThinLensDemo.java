package optics.raytrace.demo;

import java.awt.Container;

import math.*;
import optics.raytrace.sceneObjects.Arrow;
import optics.raytrace.sceneObjects.ParametrisedDisc;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.surfaces.SurfaceColour;
import optics.raytrace.surfaces.IdealThinLensSurfaceSurfaceCoordinates;
import optics.raytrace.GUI.nonInteractive.PhotoCanvas;
import optics.raytrace.GUI.nonInteractive.PhotoFrame;
import optics.raytrace.cameras.*;
import optics.raytrace.cameras.PinholeCamera.ExposureCompensationType;
import optics.raytrace.core.*;


/**
 * Thin lens demo
 * 
 * Note that in Project>Properties (or alternatively Run>Run Configurations...) the working
 * directory (which can be found in the arguments tab) has to be set to the directory
 * containing all the images, normally
 * ${workspace_loc:TIM/demo/}
 * 
 * Adapted from RayTraceJavaApplication.java
 */
public class ThinLensDemo
{
	/**
	 * Filename under which main saves the rendered image.
	 * Alter to save the rendered image under a different name.
	 * @return	the filename under which main saves the rendered image.
	 */
	public static String getFilename()
	{
		return "ThinLensDemoTemp.bmp";
	}
	
	public static boolean savePhoto()
	{
		return false;
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
		
//		// first, something to look at
//		scene.addSceneObject(new EditableCylinderLattice(
//				"cylinder lattice",
//				-1, 1, 4,
//				-1, 1, 4,
//				90, 105, 4,
//				0.02,
//				scene,
//				studio
//		));
		
		double
			zO = 12,	// z coordinate of object
			zL = 10,	// z coordinate of lens
			f = 1,	// focal length of lens
			o = zO - zL,	// object distance
			i = 1./(1./f - 1./o),	// image distance
			zI = zL - i;	// z coordinate of image
		
		scene.addSceneObject(new Arrow(
				"an arrow to look at",	// description,
				new Vector3D(0, 0, zO),	// startPoint,
				new Vector3D(0.5, 0, zO),	// endPoint,
				0.02,	// shaftRadius,
				0.1,	// tipLength,
				MyMath.deg2rad(45),	// tipAngle,
				SurfaceColour.RED_SHINY,	// surfaceProperty,
				scene,	// parent, 
				studio	// the studio
			));
		
		scene.addSceneObject(new ParametrisedDisc(
				"thin lens",	//description,
				new Vector3D(0, 0, zL),	// centre,
				new Vector3D(0, 0, 1),	// normal,
				1,	// radius,
				new Vector3D(1, 0, 0),	// phi0Direction,
				new IdealThinLensSurfaceSurfaceCoordinates(
						new Vector2D(0, 0),	// opticalAxisIntersectionCoordinates,
						f,	// focalLength,
						0.9,	// transmissionCoefficient
						true	// shadow-throwing
					),	// surface property
				scene,	// parent, 
				studio	// the studio
		));
		
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
				new Vector3D(0, 0, 4),	// view direction (magnitude is distance to detector centre)
				new Vector3D(-(double)pixelsX/pixelsY, 0, 0),	// horizontal basis Vector3D
				new Vector3D(0, 1, 0),	// vertical basis Vector3D
				pixelsX*antiAliasingFactor, pixelsY*antiAliasingFactor,	// logical number of pixels
				ExposureCompensationType.EC0,
				100,	// maxTraceLevel
				zI,	// focussing distance
				0.4,	// aperture radius
				1	// rays per pixel; the more, the less noisy the photo is
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
		if(savePhoto()) studio.savePhoto(getFilename(), "bmp");

		// display the image on the screen
		container.add(new PhotoCanvas(studio.getPhoto()));
		container.validate();
	}
}
