package optics.raytrace.test;

import java.awt.*;

import math.*;
import optics.raytrace.sceneObjects.*;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.voxellations.SextantsOfCube;
import optics.raytrace.voxellations.Voxellation;
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
 * @author Johannes Courtial, Ejovboke Orife
 */
public class SurfaceOfVoxellatedAbsorberTest
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
		
		// create a SurfaceOfVoxellatedAbsorber surface object
		
		Vector3D centre = new Vector3D(0, 0.3, 10);
		
		Sphere sphere = new Sphere(
				"strange sphere",	// description
				centre,	// centre
				1,	// radius
				null,	// placeholder --- replace in a minute
				scene, 
				studio
		);
		
		Voxellation[] voxellations = new Voxellation[1];
		voxellations[0] = new SextantsOfCube(centre);
		SurfaceProperty s = new SurfaceOf6VoxelAbsorber(
				voxellations,	// voxellations defining the voxels
				sphere,	// the object
				1000,	// maxSteps
				0.96,	// random transmission coefficient
				true	// shadow-throwing
			);

//		// the set of planes that define the voxels
//		Vector3D planesCentre = centre;
//		SetOfEquidistantParallelPlanes[] planeSets = new SetOfEquidistantParallelPlanes[3];
//		double planeSeparation = 0.1;
//		planeSets[0] = new SetOfEquidistantParallelPlanes(
//				planesCentre,	// point on 0th plane
//				Vector3D.X,	// normal to surfaces
//				planeSeparation
//			);
//		planeSets[1] = new SetOfEquidistantParallelPlanes(
//				planesCentre,	// point on 0th plane
//				Vector3D.Y,	// normal to surfaces
//				planeSeparation
//			);
//		planeSets[2] = new SetOfEquidistantParallelPlanes(
//				planesCentre,	// point on 0th plane
//				Vector3D.Z,	// normal to surfaces
//				planeSeparation
//			);
//		
//		SurfaceOfVoxellatedAbsorber s = new SurfaceOfSpecificVoxellatedAbsorber(
//				planeSets,	// the sets of parallel planes defining the voxels
//				sphere,	// the object
//				1000,	// maxSteps
//				0.96	// random transmission coefficient
//			);
		
		// now give the sphere that marvellous surface property
		sphere.setSurfaceProperty(s);

		scene.addSceneObject(sphere);

		// for test purposes, define a ray...
		Ray r = new Ray(
				new Vector3D(0, 0, 0),	// start point
				new Vector3D(0, 0, 1),	// direction
				0,	// time
				false
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
		pixelsX = 640, // 640,
		pixelsY = 480, // 480,
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
				10,	// focussing distance
				0.0,	// aperture radius
				1	// rays per pixel; the more, the less noise the photo is
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
