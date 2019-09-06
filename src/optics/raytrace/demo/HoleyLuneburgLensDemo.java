package optics.raytrace.demo;

// import java.awt.Container;

import math.*;
import optics.raytrace.sceneObjects.LuneburgLens;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
// import optics.raytrace.GUI.nonInteractive.PhotoCanvas;
// import optics.raytrace.GUI.nonInteractive.PhotoFrame;
import optics.raytrace.GUI.sceneObjects.EditableCylinderLattice;
import optics.raytrace.cameras.*;
import optics.raytrace.cameras.PinholeCamera.ExposureCompensationType;
import optics.raytrace.core.*;


/**
 * A Luneburg lens with a transparent tunnel in it.
 * 
 * Note that in Project>Properties (or alternatively Run>Run Configurations...) the working
 * directory (which can be found in the arguments tab) has to be set to the directory
 * containing all the images, normally
 * ${workspace_loc:TIM/demo/}
 * 
 * Adapted from RayTraceJavaApplication.java
 */
public class HoleyLuneburgLensDemo
{
	/**
	 * Filename under which main saves the rendered image.
	 * Alter to save the rendered image under a different name.
	 * @return	the filename under which main saves the rendered image.
	 */
	public static String getFilename(int counter)
	{
		return "HoleyLuneburgLensDemo"+counter+".bmp";
	}

	/**
	 * Define scene, lights, and/or camera.
	 * @return the studio, i.e. scene, lights and camera
	 */
	public static Studio createStudio(double xCamera)
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

		double lensRadius = 1;
		double ratioNSurfaceNSurrounding = 1;
		double transparentTunnelRadius = 0.5;
		double transmissionCoefficient = 0.96;
		boolean shadowThrowing = true;
		
		Vector3D lensCentre = new Vector3D(0, 0, 10);

		scene.addSceneObject(
				new LuneburgLens(
						"Luneburg lens",	
						lensCentre,	// centre
						lensRadius,	// radius
						ratioNSurfaceNSurrounding,
						transparentTunnelRadius,
						transmissionCoefficient,	// transmission coefficient
						shadowThrowing,
						scene,	// parent, 
						studio	// the studio
				));
		
		double cylinderRadius = 0.05;
		
		scene.addSceneObject(
				new EditableCylinderLattice(
						"cylinder lattice",
						-1.5, 1.5, 4,	// double xMin, double xMax, int nX,
						-1 + cylinderRadius, 2 + cylinderRadius, 4,	// double yMin, double yMax, int nY,
						12, 24, 4,	// double zMin, double zMax, int nZ,
						0.05,	// double radius,
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
			quality = 1,	// 1 = normal, 2 = good, 4 = great
			pixelsX = 640*quality,
			pixelsY = 480*quality,
			antiAliasingFactor = 2;

		double verticalBasisVectorLength = 4;
		
		// a camera with a non-zero aperture size (so it simulates blur)
		ApertureCamera camera = new ApertureCamera(
				"Camera",
				new Vector3D(xCamera, 0, 0),	// centre of aperture
				lensCentre,	// centreOfViewInFocus
				new Vector3D(-(double)pixelsX/pixelsY*verticalBasisVectorLength, 0, 0),	// horizontal basis Vector3D
				new Vector3D(0, -verticalBasisVectorLength, 0),	// vertical basis Vector3D
				pixelsX*antiAliasingFactor, pixelsY*antiAliasingFactor,	// logical number of pixels
				ExposureCompensationType.EC0,
				1000,	// maxTraceLevel
				0.,	// aperture radius
				1	// rays per pixel; the more, the less noise the photo is
			);
//		ApertureCamera camera = new ApertureCamera(
//				"Camera",
//				new Vector3D(xCamera, 0, 0),	// centre of aperture
//				new Vector3D(0, 0, 2),	// view direction (magnitude is distance to detector centre)
//				new Vector3D(-(double)pixelsX/(double)pixelsY, 0, 0),	// horizontal basis Vector3D
//				new Vector3D(0, -1, 0),	// vertical basis Vector3D
//				pixelsX*antiAliasingFactor, pixelsY*antiAliasingFactor,	// logical number of pixels
//				1000,	// maxTraceLevel
//				8,	// focussing distance
//				0.,	// aperture radius
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
		// Container container = (new PhotoFrame()).getContentPane();

		int counter = 0;
		for(double xCamera = -4; xCamera <= 4; xCamera += 0.2)
		{
			System.out.println("xCamera = " + xCamera);
			
			// define scene, lights and camera
			Studio studio = createStudio(xCamera);

			// do the ray tracing
			studio.takePhoto();

			// save the image
			studio.savePhoto(getFilename(counter++), "bmp");
		}
		
		// display the image on the screen
		// container.add(new PhotoCanvas(studio.getPhoto()));
		// container.validate();
	}
}
