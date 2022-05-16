package optics.raytrace.test;

import java.awt.Container;

import math.Vector3D;
import optics.DoubleColour;
import optics.raytrace.GUI.nonInteractive.PhotoCanvas;
import optics.raytrace.GUI.nonInteractive.PhotoFrame;
import optics.raytrace.cameras.ApertureCamera;
import optics.raytrace.cameras.PinholeCamera.ExposureCompensationType;
import optics.raytrace.core.DefaultRaytraceExceptionHandler;
import optics.raytrace.core.LightSource;
import optics.raytrace.core.Ray;
import optics.raytrace.core.SceneObjectClass;
import optics.raytrace.core.Studio;
import optics.raytrace.exceptions.RayTraceException;
import optics.raytrace.sceneObjects.ParametrisedSphere;
import optics.raytrace.sceneObjects.Sphere;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.surfaces.SurfaceColour;
import optics.raytrace.surfaces.SurfaceOfVoxellatedMetric;


/**
 * The main method renders the image defined by createStudio(), saves it to a file
 * (whose name is given by the constant FILENAME), and displays it in a new window.
 * 
 * @author  E Orife & J Courtial, based on example by Johannes Courtial
 */
public class SurfaceOfVoxellatedSphericalCloakTest
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
	
		Vector3D centre = new Vector3D(1.1, 0.0, 10);
	
		
//		// the set of planes that define the voxels
//		Vector3D planesCentre = centre.clone();
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
		
		Vector3D cubeCentre = centre.clone();
	
		
		// a cube that tests the SetOfConcentricCubes class
		Sphere cube=new ParametrisedSphere(
				"strange cube",	// description
		         cubeCentre,
				1,	// centre
				null,	// placeholder surface property
				scene, studio
			);
		
//		// construct a surface for the cube
//		SetOfConcentricCubes[] cubeSet=new SetOfConcentricCubes[]{
//				new SetOfConcentricCubes(
//						cubeCentre,
//						0.25,	// centre-to-face distance of zeroth cube
//						0.11111	// separation between nested neighbouring cubes
//					)
//		}; 

		SurfaceOfVoxellatedMetric s = new SurfaceOfVoxellatedSphericalCloak(
				cube,	// (SceneObject) new Refractive(0,0), the object
				60,	// maxSteps
				.25,//r0
				0.96,	// random transmission coefficient
				true	// shadow-throwing
				);

		// now give the cube that marvellous surface property
		cube.setSurfaceProperty(s);
		// cube.setSurfaceProperty(new SurfaceColour(DoubleColour.BLUE, DoubleColour.WHITE));


		scene.addSceneObject(cube);
		
//		// for comparison
//		scene.addSceneObject(new ParametrisedCuboid(
//				"for comparison", // description
//				2, 2, 2,	// width, height, depth
//				new Vector3D(-1.1, 0.3, 10),	// centre
//				new Refractive(2.0, 0.96),	// surface property
//				scene,
//				studio));
//		
//		// Object to be hidden
//		scene.addSceneObject(new Sphere(
//				"hidden object", // description
//				new Vector3D(-1.1, 0.3, 10),	// centre
//			    0.25,	// radius
//				//new Reflective (0.96),	// surface property
//		        new SurfaceColour(DoubleColour.BLUE, null),	// surface property
//				scene,
//				studio));
		
		// Object to be hidden
		scene.addSceneObject(new Sphere(
				"hidden object", // description
				cubeCentre.clone(),	// centre
				.20,	// radius
				//new Reflective (0.96),	// surface property
				new SurfaceColour(DoubleColour.GREEN,DoubleColour.WHITE, true),	// surface property
				scene,
				studio));
		
		cubeCentre.clone();
		// Object to be hidden
		scene.addSceneObject(new Sphere(
				"hidden object", // description
				Vector3D.sum(cubeCentre.clone(), new Vector3D(0,0,5)),	// centre
				2,	// radius
				//new Reflective (0.96),	// surface property
				new SurfaceColour(DoubleColour.RED,DoubleColour.WHITE, true),	// surface property
				scene,
				studio));
		
//		// Object to be hidden
//		scene.addSceneObject(new Sphere(
//				"hidden object", // description
//				centre.clone(),	// centre
//			    cubeSet[0].getRadiusOfCube0(),	// radius
//				new Reflective (0.96),	// surface property
//              //SurfaceProperty.BLUE,	// surface property
//				scene,
//				studio));
		
				
		// for test purposes, define a ray...
		Ray r = new Ray(
				new Vector3D(0, 0, 0),	// start point
				new Vector3D(0, 0, 1),	// direction
				0,	// time
				false
			);
		// ... and launch it at the sphere
		try {
			cube.getColour(r,
					LightSource.getStandardLightsFromBehind(),
					cube, 10,	// trace level
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
				new Vector3D(0, 0, 0),	// start point
				new Vector3D(0, 0, 1),	// view direction (magnitude is distance to detector centre)
				new Vector3D(4*(double)pixelsX/pixelsY, 0, 0),	// horizontal basis Vector3D
				new Vector3D(0, -4, 0),	// vertical basis Vector3D
				pixelsX*antiAliasingFactor, pixelsY*antiAliasingFactor,	// logical number of pixels
				ExposureCompensationType.EC0,
				10,	// maxTraceLevel
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
		 studio.savePhoto(getFilename(), "bmp");

		// display the image on the screen
		container.add(new PhotoCanvas(studio.getPhoto()));
		container.validate();
	}
}