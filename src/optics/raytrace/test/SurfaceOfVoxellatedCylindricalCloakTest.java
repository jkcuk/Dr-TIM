package optics.raytrace.test;

import java.awt.Container;

import math.Vector3D;
import optics.raytrace.GUI.nonInteractive.PhotoCanvas;
import optics.raytrace.GUI.nonInteractive.PhotoFrame;
import optics.raytrace.cameras.ApertureCamera;
import optics.raytrace.cameras.PinholeCamera.ExposureCompensationType;
import optics.raytrace.core.DefaultRaytraceExceptionHandler;
import optics.raytrace.core.LightSource;
import optics.raytrace.core.Ray;
import optics.raytrace.core.SceneObjectClass;
import optics.raytrace.core.Studio;
import optics.raytrace.core.SurfaceProperty;
import optics.raytrace.exceptions.RayTraceException;
import optics.raytrace.sceneObjects.ParametrisedCylinder;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.voxellations.Voxellation;


/**
 * The main method renders the image defined by createStudio(), saves it to a file
 * (whose name is given by the constant FILENAME), and displays it in a new window.
 * 
 * @author  E Orife & J Courtial, based on example by Johannes Courtial
 */
public class SurfaceOfVoxellatedCylindricalCloakTest
{
//	public static void performanceTest(String[] argue){
//		
//		double before=System.currentTimeMillis(),after,duration;
//		Scanner scanner = null; int i=0;
//		System.out.println("Number of data points:"+argue.length);
//		PrintWriter out;
//		int maxLevel;
//		/** 
//		 * Do something here.
//		 */
//		do{// open a window, and take a note of its content pane
//			Container container = (new PhotoFrame()).getContentPane();
//
//			maxLevel=Integer.parseInt(scanner.next().trim());
//			// define scene, lights and camera
//			Studio studio = createStudio(maxLevel);
//
//			// do the ray tracing
//			studio.takePhoto();
//
//			// save the image
//			//studio.savePhoto(getFilename(), "bmp");
//
//			Object object=studio.toString();
//			// display the image on the screen
//			container.add(new PhotoCanvas(studio.getPhoto()));
//			container.validate();
//			i++;
//			}
//		while(i<argue.length-1);//has more
//		//Don't forget to check the time!
//		after=System.currentTimeMillis();
//		duration=after-before;
//		System.out.println(duration+","+maxLevel);
//	}
	
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
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Define scene, lights, and/or camera.
	 * @return a studio, i.e. scene, lights and camera
	 */
	public static Studio createStudio(int maxLevel)
	{
		Studio studio = new Studio();

		// the scene
		SceneObjectContainer scene = new SceneObjectContainer("the scene", null, studio);

		// the standard scene objects
		scene.addSceneObject(SceneObjectClass.getChequerboardFloor(scene, studio));	// the checkerboard floor
		scene.addSceneObject(SceneObjectClass.getSkySphere(scene, studio));	// the sky	
		
		ParametrisedCylinder cylinder=new ParametrisedCylinder(
				"strange cylinder",//description
//				Vector3D.O,
//				new Vector3D(0,0,2),
				new Vector3D(0, 0, 15),	// start point
				new Vector3D(2, 2, 13),	// end point
				1,
				null,	// placeholder --- replace in a minute
				scene, 
				studio
				);

		
		Voxellation[] voxellations = new Voxellation[1];
		//voxellations[0] = new SextantsOfCube(centre);
		voxellations[0] = new SectorsOfCylinder(cylinder, 2);

		
		SurfaceProperty s = new SurfaceOfSpecificVoxellatedAbsorber(
				voxellations,	// voxellations defining the voxels
				cylinder,	// the object
				maxLevel,	// maxSteps
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
		
//		// now give the sphere that marvellous surface property
//		sphere.setSurfaceProperty(s);
//
//		scene.addSceneObject(sphere);
		
		// now give the cylinder that marvellous surface property
		cylinder.setSurfaceProperty(s);
		cylinder.addSceneObjects();	// make sure all the scene objects making up the cylinder have that surface property

		scene.addSceneObject(cylinder);

		
//		scene.addSceneObject(new Cylinder(
//				"shiny cylinder",
//				new Vector3D(0, 0, 15),	// start point
//				new Vector3D(2, 2, 13),	// end point
//				1,	// radius
//				new Reflective(0.9),
//				scene,
//				studio
//		));
		
		// for test purposes, define a ray...
		Ray r = new Ray(
				new Vector3D(0, 0, 0),	// start point
				new Vector3D(0, 0, 1),	// direction
				0,	// time
				false
			);
		// ... and launch it at the cylinder
		try {
			cylinder.getColour(r,
					LightSource.getStandardLightsFromBehind(),
					cylinder, 1000,	// trace level
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
	
//		Vector3D startPoint1 = new Vector3D(-1.1, 0.0, 10),
//		         startPoint2 = new Vector3D(1.1, 0.0, 10),
//		         endPoint1 = new Vector3D(-1.1, 1.0, 10),
//                 endPoint2 = new Vector3D(1.1, 1.0, 10);
//
//		
////		// the set of planes that define the voxels
////		Vector3D planesCentre = centre.clone();
////		SetOfEquidistantParallelPlanes[] planeSets = new SetOfEquidistantParallelPlanes[3];
////		double planeSeparation = 0.1;
////		planeSets[0] = new SetOfEquidistantParallelPlanes(
////				planesCentre,	// point on 0th plane
////				Vector3D.X,	// normal to surfaces
////				planeSeparation
////			);
////		planeSets[1] = new SetOfEquidistantParallelPlanes(
////				planesCentre,	// point on 0th plane
////				Vector3D.Y,	// normal to surfaces
////				planeSeparation
////			);
////		planeSets[2] = new SetOfEquidistantParallelPlanes(
////				planesCentre,	// point on 0th plane
////				Vector3D.Z,	// normal to surfaces
////				planeSeparation
////			);
////
//	
//		
//		// a cylinder that tests the SetOfConcentricCubes class
//		 ParametrisedCylinder cylinder=new  ParametrisedCylinder(
//				"strange cylinder",	// description
//		        startPoint1, //start/end point
//				endPoint1, //end/start point
//				1,	// radius
//				null,	// placeholder surface property
//				scene, studio
//			);
//		
////		// construct a surface for the cylinder
////		SetOfCoaxialCylinders[] cylinderSet=new SetOfCoaxialCylinders[]{
////				new SetOfCoaxialCylinders(
////						cylinderCentre,
////						0.25,	// centre-to-face distance of zeroth cylinder
////						0.11111	// separation between nested neighbouring cylinders
////					)
////		}; 
//
//		SurfaceOfVoxellatedMetric s = new SurfaceOfVoxellatedCylindricalCloak(
//				cylinder,	// (SceneObject) new Refractive(0,0), the object
//				60,	// maxSteps
//				6,// number of sectors
//				.28,//r0
//				0.96	// random transmission coefficient
//				);
//
//		// now give the cylinder that marvellous surface property
//		cylinder.setSurfaceProperty(s);
//		// cube.setSurfaceProperty(new SurfaceColour(DoubleColour.BLUE, DoubleColour.WHITE));
//		cylinder.addSceneObjects();	// make sure all the scene objects making up the cylinder have that surface property
//		
//		scene.addSceneObject(cylinder);
//		
//		// for comparison
//		scene.addSceneObject(new Cylinder(
//				"for comparison", // description
//				startPoint2,//start/end point
//				endPoint2,//end/start point
//				1,	// radius
//				//new Reflective (0.96),	// surface property
//				new Refractive(Math.sqrt(2), 0.96),	// surface property
//				//new SurfaceColour(DoubleColour.RED,DoubleColour.WHITE),	// surface property
//				scene,
//				studio));
//		scene.addSceneObject(new Sphere(
//				"non-hidden object", // description
//				Vector3D.mean(startPoint2,endPoint2),	// centre
//			    0.25,	// radius
//				//new Reflective (0.96),	// surface property
//		        //new SurfaceColour(DoubleColour.GREEN,DoubleColour.WHITE),
//		        new SurfaceColour(DoubleColour.BLUE, null),	// surface property
//				scene,
//				studio));
//		
//		// Object to be hidden
//		scene.addSceneObject(new Sphere(
//				"hidden object", // description
//				Vector3D.mean(cylinder.getStartPoint(),cylinder.getEndPoint()),//centre
//				.252,	// radius
//				//new Reflective (0.96),	// surface property
//				new SurfaceColour(DoubleColour.RED,DoubleColour.WHITE),	// surface property
//				scene,
//				studio));
//		
////		// Object to be hidden
////		scene.addSceneObject(new Cylinder(
////				"hidden object", // description
////				centre.clone(),	// centre
////			    cylinderSet[0].getRadiusOfCylinder0(),	// radius
////				new Reflective (0.96),	// surface property
////              //SurfaceProperty.BLUE,	// surface property
////				scene,
////				studio));
		
	
//		scene.addSceneObject(new LuneburgLens(
//				"Luneburg lens",	// description
//				new Vector3D(-1, 0, 10),	// centre
//				1,	// radius
//				1,	// ratioNSurfaceNSurrounding
//				0,	// transparentTunnelRadius
//				0.96,	// transmission coefficient
//				scene, studio));
		
//		scene.addSceneObject(new ParametrisedSphere(
//				"Some sphere",	// description
//				new Vector3D(-1, 0, 10),	// centre
//				1,	// radius
//				new Refractive (1.5,0.96),	// surface property
//		        //new SurfaceColour(DoubleColour.GREEN,DoubleColour.WHITE),
//		        //new SurfaceColour(DoubleColour.BLUE, null),	// surface property
//				scene, studio));
//		
//		scene.addSceneObject(new ParametrisedSphere(
//				"non-hidden object", // description
//				new Vector3D(-1, 0, 10),	// centre
//				0.25,	// radius
//				//new Reflective (0.96),	// surface property
//				//new SurfaceColour(DoubleColour.GREEN,DoubleColour.WHITE),
//				new SurfaceColour(DoubleColour.BLUE, null),	// surface property
//				scene,
//				studio));
		
//		ParametrisedCylinder cylinder = new ParametrisedCylinder(
//				"layered cylindrical Luneburg lens",	// description
//				new Vector3D(0, -.50, 10),	// start point
//				new Vector3D(0, 07, 10),	// end point
//				1,	// radius
//				null,	// placeholder --- replace in a minute
//				scene, 
//				studio
//		);
//		
////		SurfaceOfLayeredCylindricalLuneburgLens s = new SurfaceOfLayeredCylindricalLuneburgLens(
////				cylinder,
////				3,	// number of shells
////				0.96	// random transmission coefficient
////			);
//		
//		SurfaceOfVoxellatedMetric s = new SurfaceOfVoxellatedCylindricalCloak(
//				cylinder,	// (SceneObject) new Refractive(0,0), the object
//				10,//60,	// maxSteps
//				6,// number of sectors
//				.28,//r0
//				0.96	// random transmission coefficient
//				);
//		
//		// now give the cylinder that marvellous surface property
//		cylinder.setSurfaceProperty(s);
//		cylinder.addSceneObjects();	// make sure all the scene objects making up the cylinder have that surface property
//
//		scene.addSceneObject(cylinder);
//
//		// Object to be hidden
//		scene.addSceneObject(new Sphere(
//				"hidden object", // description
//				Vector3D.mean(cylinder.getStartPoint(),cylinder.getEndPoint()),//centre
//				.252,	// radius
//				//new Reflective (0.96),	// surface property
//				new SurfaceColour(DoubleColour.RED,DoubleColour.WHITE),	// surface property
//				scene,
//				studio));
		
		ParametrisedCylinder cylinder=new ParametrisedCylinder(
				"strange cylinder",	//description
				new Vector3D(0, 0, 15),	// start point
				new Vector3D(1, 1, 13),	// end point
				1,	// radius
				null,	// placeholder --- replace in a minute
				scene, 
				studio
				);

		
		Voxellation[] voxellations = new Voxellation[1];
		//voxellations[0] = new SextantsOfCube(centre);
		voxellations[0] = new SectorsOfCylinder(cylinder, 5);
//		SurfaceProperty s = new SurfaceOf6VoxelAbsorber(
//				voxellations,	// voxellations defining the voxels
//				sphere,	// the object
//				1000,	// maxSteps
//				0.96	// random transmission coefficient
//			);
		
		SurfaceProperty s = new SurfaceOfSpecificVoxellatedAbsorber(
				voxellations,	// voxellations defining the voxels
				cylinder,	// the object
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
		
//		// now give the sphere that marvellous surface property
//		sphere.setSurfaceProperty(s);
//
//		scene.addSceneObject(sphere);
		
		// now give the cylinder that marvellous surface property
		cylinder.setSurfaceProperty(s);
		cylinder.addSceneObjects();	// make sure all the scene objects making up the cylinder have that surface property

		scene.addSceneObject(cylinder);

		
//		scene.addSceneObject(new Cylinder(
//				"shiny cylinder",
//				new Vector3D(0, 0, 15),	// start point
//				new Vector3D(2, 2, 13),	// end point
//				1,	// radius
//				new Reflective(0.9),
//				scene,
//				studio
//		));
		
		// for test purposes, define a ray...
		Ray r = new Ray(
				new Vector3D(0, 0, 0),	// start point
				new Vector3D(0, 0, 1),	// direction
				0,	// time
				false
			);
		// ... and launch it at the cylinder
		try {
			cylinder.getColour(r,
					LightSource.getStandardLightsFromBehind(),
					cylinder,
					1000,	// trace level
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
		//studio.savePhoto(getFilename(), "bmp");

		// display the image on the screen
		container.add(new PhotoCanvas(studio.getPhoto()));
		container.validate();
		// System.out.println("studio:"+studio.toString());
	}
}