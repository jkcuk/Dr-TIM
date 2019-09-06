package optics.raytrace.test;

import java.awt.*;

import math.*;
import optics.raytrace.sceneObjects.*;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.cameras.*;
import optics.raytrace.cameras.PinholeCamera.ExposureCompensationType;
import optics.raytrace.core.*;
import optics.raytrace.exceptions.RayTraceException;
import optics.raytrace.GUI.nonInteractive.PhotoCanvas;
import optics.raytrace.GUI.nonInteractive.PhotoFrame;


/**
 * The main method renders the image defined by createStudio(), saves it to a file
 * (whose name is given by the constant FILENAME), and displays it in a new window.
 * 
 * @author  E Orife, based on example by Johannes Courtial
 */
public class SurfaceOfMetricLuneburgLensTest
{
	/**
	 * Filename under which main saves the rendered image.
	 * Alter to save the rendered image under a different name.
	 * @return	the filename under which main saves the rendered image.
	 */
	public static String getFilename()
	{
		try {
			return  Class.forName(
					Thread.currentThread().getStackTrace()[1].getClassName()).getSimpleName()+".bmp";
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
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
		scene.addSceneObject(SceneObjectClass.getChequerboardFloor(scene, studio));	// the checkerboard floor
		scene.addSceneObject(SceneObjectClass.getSkySphere(scene, studio));	// the sky

		// add any other scene objects
		
		scene.addSceneObject(new LuneburgLens(
				"Luneburg lens",	// description
				new Vector3D(-1, 0, 10),	// centre
				1,	// radius
				1,	// ratioNSurfaceNSurrounding
				0,	// transparentTunnelRadius
				0.96,	// transmission coefficient
				true,	// shadow-throwing
				scene, studio));

		ParametrisedSphere sphere = new ParametrisedSphere(
				"layered Luneburg lens",	// description
				new Vector3D(1, 0, 10),	// centre
				1,	// radius
				null,	// placeholder --- replace in a minute
				scene, 
				studio
		);
		
		SurfaceOfMetricLuneburgLens s = new SurfaceOfMetricLuneburgLens(
				sphere,
				1000,	// number of shells
				0.96,	// random transmission coefficient
				true	// shadow-throwing
			);

//		// quick test:  does the MetricInterface class work as intended?
//		// simulate, using the metric interface, a sphere with refractive index 2...
//		MetricInterface s = new MetricInterface(
//				MetricInterface.getMetricTensorForRefractiveIndex(2),	// metricTensorInside
//				MetricInterface.euclideanMetricTensor,	// metricTensorOutside
//				GlobalOrLocalCoordinateSystemType.GLOBAL_BASIS,	// basis
//				RefractionType.POSITIVE_REFRACTION,	// refractionType
//				false,	// allowImaginaryOpticalPathLengths
//				0.96	// transmissionCoefficient
//			);
//
//		// ... and compare this to a sphere with refractive index 2, simulated the "normal" way
//		scene.addSceneObject(new ParametrisedSphere(
//				"sphere with refractive index 2",	// description
//				new Vector3D(-1, 0, 10),	// centre
//				1,	// radius
//				new Refractive(2, 0.96),	// surface property
//				scene, 
//				studio
//		));


		
		// now give the sphere that marvellous surface property
		sphere.setSurfaceProperty(s);

		scene.addSceneObject(sphere);

		// for test purposes, define a ray...
		Ray r = new Ray(
				new Vector3D(0.5, 0, 0),	// start point
				new Vector3D(0, 0, 1),	// direction
				0	// time
			);
		// ... and launch it at the sphere
		try {
			sphere.getColour(r,
					LightSource.getStandardLightsFromBehind(),
					sphere, 100,	// trace level
					new DefaultRaytraceExceptionHandler()
			);
		} catch (RayTraceException e) {
			e.printStackTrace();
		}
		
		// define the camera
		//
		// Note that the view direction and basis Vector3Ds of the detector are chosen such that
		// the x, y, z axes form a LEFT-handed coordinate system.
		// The reason is that, in the photo, the positive x direction is then to the right,
		// the positive y direction is upwards, and the camera looks in the positive z direction.
		int
		pixelsX =  640,
		pixelsY =  480,
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
				10,	// focusing distance
				0.0,	// aperture radius
				1	// rays per pixel; the more, the less noise/(noisy?) the photo (has?)/is
		);

		studio.setScene(scene);
		studio.setLights(LightSource.getStandardLightsFromBehind());
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
		// studio.savePhoto(getFilename(), "bmp");

		// display the image on the screen
		container.add(new PhotoCanvas(studio.getPhoto()));
		container.validate();
	}
}
