package optics.raytrace.research.viewRotation;

import java.awt.Container;

import math.*;
import optics.raytrace.surfaces.*;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.GUI.nonInteractive.PhotoCanvas;
import optics.raytrace.GUI.nonInteractive.PhotoFrame;
import optics.raytrace.GUI.sceneObjects.EditableCylinderLattice;
import optics.raytrace.GUI.sceneObjects.EditableFramedRectangle;
import optics.raytrace.GUI.sceneObjects.EditableParametrisedPlane;
import optics.raytrace.cameras.*;
import optics.raytrace.cameras.PinholeCamera.ExposureCompensationType;
import optics.raytrace.core.*;


/**
 * View-rotation demo
 * 
 * Note that in Project>Properties (or alternatively Run>Run Configurations...) the working
 * directory (which can be found in the arguments tab) has to be set to the directory
 * containing all the images, normally
 * ${workspace_loc:TIM/demo/}
 * 
 * Adapted from RayTraceJavaApplication.java
 */
public class ViewRotation
{   
	/**
	 * One of the two possible solutions for which s = i
	 * @param gamma (in degrees)
	 * @return alpha (in degrees)
	 */
	public static double gamma2alphaSolution1(double gamma)
	{
		return gamma - 180;
	}
	
	/**
	 * One of the two possible solutions for which s = i
	 * @param gamma (in degrees)
	 * @return alpha (in degrees)
	 */
	public static double gamma2alphaSolution2(double gamma)
	{
		return -gamma;
	}

	/**
	 * One of the two possible solutions for which s = i
	 * @param gamma (in degrees)
	 * @return beta (in degrees)
	 */
	public static double gamma2betaSolution1(double gamma)
	{
		return 180;
	}
	
	/**
	 * One of the two possible solutions for which s = i
	 * @param gamma (in degrees)
	 * @return beta (in degrees)
	 */
	public static double gamma2betaSolution2(double gamma)
	{
		return 2*gamma;
	}

	/**
	 * Define scene, lights, and/or camera.
	 * @return the studio, i.e. scene, lights and camera
	 */
	public static Studio createStudio(double alpha, double beta,
			double cameraX, double cameraY, double cameraZ,
			Vector3D centreOfView, double zoomFactor)
	{		
		Studio studio = new Studio();
		
		// the scene
		SceneObjectContainer scene = new SceneObjectContainer("the scene", null, studio);

		// the standard scene objects
		scene.addSceneObject(SceneObjectClass.getSkySphere(scene, studio));	// the sky
		//scene.addSceneObject(SceneObjectClass.getChequerboardFloor(scene, studio));	// the checkerboard floor

		scene.addSceneObject(new EditableParametrisedPlane(
				"chequerboard floor", 
				new Vector3D(0, -1, 0),	// point on plane
				new Vector3D(1, 0, 0),	// Vector3D 1 that spans plane
				new Vector3D(0, 0, 1),	// Vector3D 2 that spans plane
				// true,	// shadow-throwing
				new SurfaceTiling(SurfaceColour.GREY90_SHINY, SurfaceColour.WHITE_SHINY, 1, 1),
				scene,
				studio
		));


		//
		// add any other scene objects
		//
		
		// first, something to look at
		scene.addSceneObject(new EditableCylinderLattice(
				"cylinder lattice",
				-1, 1, 4,
				-1, 1, 4,
				25, 40, 4,
				0.02,
				scene,
				studio
		));
				
		if(true)
		// then add ray-rotation windows
		scene.addSceneObject(new EditableFramedRectangle(
				"back window",
				new Vector3D(-1.5, -1, 20),	// corner
				new Vector3D(3, 0, 0),	// width vector
				new Vector3D(0, 2, 0),	// height vector
				0.025,	// frame radius
				new RayRotating(MyMath.deg2rad(-alpha), SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT, true),
				SurfaceColour.GREY50_SHINY,
				true,	// show frame
				scene,
				studio
		));
		
		if(true)
		scene.addSceneObject(new EditableFramedRectangle(
				"front window",
				new Vector3D(-1.5, -1, 10),	// corner
				new Vector3D(3, 0, 0),	// width vector
				new Vector3D(0, 2, 0),	// height vector
				0.025,	// frame radius
				new RayRotating(MyMath.deg2rad(-beta), SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT, true),
				SurfaceColour.GREY50_SHINY,
				true,	// show frame
				scene,
				studio
		));
		//scene.addSceneObject(new EditableScaledParametrisedCentredParallelogram(
			//	"ray-rotating window",
				//new Vector3D(0, 0, 10),	// centre
				//new Vector3D(3, 0, 0),	// width vector
				//new Vector3D(0, 2, 0),	// height vector
				//new RayRotating(180*Math.PI/180, SurfacePropertyPanel.TRANSMISSION_COEFFICIENT),
				//scene,
				//studio
		//));
		
		// define the camera
		//
		// Note that the view direction and basis Vector3Ds of the detector are chosen such that
		// the x, y, z axes form a LEFT-handed coordinate system.
		// The reason is that, in the photo, the positive x direction is then to the right,
		// the positive y direction is upwards, and the camera looks in the positive z direction.
		int
		pixelsX = 1024,
		pixelsY = 768,
		antiAliasingFactor = 1;

		ApertureCamera camera = new ApertureCamera(
			"camera",	// name
			new Vector3D(cameraX, cameraY, cameraZ),	// apertureCentre
			centreOfView,	// centreOfViewInFocus
			new Vector3D(-3/zoomFactor*pixelsX/pixelsY, 0, 0),	// horizontal basis Vector3D
			new Vector3D(0, -3/zoomFactor, 0),	// vertical basis Vector3D
			pixelsX*antiAliasingFactor, pixelsY*antiAliasingFactor,	// logical number of pixels
			ExposureCompensationType.EC0,
			100,	// maxTraceLevel
			0.0,	// aperture radius
			1	// rays per pixel; the more, the less noise the photo is
		);

//		// a camera with a non-zero aperture size (so it simulates blur)
//		ApertureCamera camera = new ApertureCamera(
//				"Camera",
//				new Vector3D(cameraX, cameraY, cameraZ),	// centre of aperture
//				new Vector3D(0, 0, 1),	// view direction (magnitude is distance to detector centre)
//				new Vector3D(-3*(double)pixelsX/(double)pixelsY, 0, 0),	// horizontal basis Vector3D
//				new Vector3D(0, -3, 0),	// vertical basis Vector3D
//				pixelsX*antiAliasingFactor, pixelsY*antiAliasingFactor,	// logical number of pixels
//				100,	// maxTraceLevel
//				10,	// focussing distance
//				0.0,	// aperture radius
//				1	// rays per pixel; the more, the less noise the photo is
//		);
		
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
		Studio studio = createStudio(
				gamma2alphaSolution1(60),	// alpha (in degrees)
				gamma2betaSolution1(60),	// beta (in degrees)
				0, 0, 5,	// x, y, z coordinates of pinhole
				new Vector3D(0, 0, 10),	// centre of view
				1	// zoom factor
			);

		// do the ray tracing
		studio.takePhoto();

		// save the image
		// String filename = "solution1gamma60.bmp";
		// String filename = "solution2gamma60.bmp";
		// String filename = "RR.bmp"; // choose solution 2, giving 120 degrees
		// String filename = "lattice.bmp";
		String filename = "dz=+5.bmp";
		studio.savePhoto(filename, "bmp");

		// display the image on the screen
		container.add(new PhotoCanvas(studio.getPhoto()));
		container.validate();
	}
}

