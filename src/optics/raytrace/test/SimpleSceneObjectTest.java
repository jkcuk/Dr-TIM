package optics.raytrace.test;

import java.io.PrintStream;

import math.*;
import math.ODE.IntegrationType;
import optics.DoubleColour;
import optics.raytrace.NonInteractiveTIMActionEnum;
import optics.raytrace.NonInteractiveTIMEngine;
import optics.raytrace.GUI.cameras.RenderQualityEnum;
import optics.raytrace.GUI.sceneObjects.EditableRayTrajectory;
import optics.raytrace.core.LightSource;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.SceneObjectClass;
import optics.raytrace.core.Studio;
import optics.raytrace.exceptions.SceneException;
import optics.raytrace.sceneObjects.Parallelepiped;
import optics.raytrace.sceneObjects.Parallelepiped2;
import optics.raytrace.sceneObjects.Plane;
import optics.raytrace.sceneObjects.Sphere;
import optics.raytrace.sceneObjects.TimHead;
import optics.raytrace.sceneObjects.WrappedSceneObject;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.surfaces.Reflective;
import optics.raytrace.surfaces.SemiTransparent;
import optics.raytrace.surfaces.SurfaceColour;
import optics.raytrace.surfaces.SurfaceColourLightSourceIndependent;
import optics.raytrace.surfaces.SurfaceOfLissajousLens;



public class SimpleSceneObjectTest extends NonInteractiveTIMEngine
{
	/**
	 * Constructor.
	 * Sets all parameters.
	 */
	public SimpleSceneObjectTest()
	{
		super();
		
		renderQuality = RenderQualityEnum.DRAFT;
		
		nonInteractiveTIMAction = NonInteractiveTIMActionEnum.INTERACTIVE;
//		nonInteractiveTIMAction = NonInteractiveTIMActionEnum.MOVIE;
		cameraViewDirection = new Vector3D(0,-1,0);
		cameraHorizontalFOVDeg = 20;
		cameraTopDirection = new Vector3D(0,0,1); 
		cameraDistance = 10;
		traceRaysWithTrajectory = false;

		// camera parameters are set in createStudio()
	}

	
	/* (non-Javadoc)
	 * @see optics.raytrace.NonInteractiveTIMEngine#getFirstPartOfFilename()
	 */
	@Override
	public String getClassName()
	{
		return
				"SimpleSceneObjectTest"
				;
	}
	
	/*
	 * Write all parameters to a .txt file
	 * @see optics.raytrace.NonInteractiveTIMEngine#writeParameters(java.io.PrintStream)
	 */
	@Override
	public void writeParameters(PrintStream printStream)
	{
		// write any parameters not defined in NonInteractiveTIMEngine, each parameter is saved like this:
		// printStream.println("parameterName = "+parameterName);

		// write all parameters defined in NonInteractiveTIMEngine
		super.writeParameters(printStream);
	}
	
	/* (non-Javadoc)
	 * @see optics.raytrace.NonInteractiveTIMEngine#populateStudio()
	 */
	@Override
	public void populateStudio()
	throws SceneException
	{
		// the studio
		studio = new Studio();


		//setting the scene
		SceneObjectContainer scene = new SceneObjectContainer("the scene", null, studio);		

		// the standard scene objects
//		scene.addSceneObject(SceneObjectClass.getHeavenSphere(scene, studio)); //the floor
		scene.addSceneObject(SceneObjectClass.getChequerboardFloor(scene, studio)); //the floor
		scene.addSceneObject(SceneObjectClass.getSkySphere(scene, studio));	// the sky	
		studio.setLights(LightSource.getStandardLightsFromBehind());
		studio.setCamera(getStandardCamera());
		// ... and then adding scene objects to scene
		
		// scene.addSceneObject(new Sphere("The sphere", new Vector3D(0,0,0), 1, SurfaceColour.CYAN_SHINY, scene, studio));
//		Parallelepiped2 lissajousLens = new Parallelepiped2(
//				"lissajous lens on a parallelepiped", 	// description
//				new Vector3D(0, 0, 0),	// centre
//				new Vector3D(0.25, 0, 0), new Vector3D(0, 0.01, 0), new Vector3D(0, 0, 0.25), 
//				null, 
//				scene,	// parent
//				studio
//			);
		
		Sphere sphere = new Sphere(
				"lissajous lens on sphere", 	// description
				new Vector3D(0, 0, 0),	// centre
				0.25,
				null, //will be overwritten in a bit
				scene,
				studio
				);
		
		Plane lissajousPlane = new Plane(
				"lissajous lens",
				Vector3D.O,
				Vector3D.X.getProductWith(1),
				null, //will be overwritten in a bit
				scene,
				studio
				);
		
				
//		LissajousLensSurface2 lissajousLensSurface = new LissajousLensSurface2(
//						new Vector3D(0, 0, 0),// lissajousCentre, 
//						lissajousPlane,
//						//sphere,
//						//lissajousLens,
//						4,3,
//						0.01,//MyMath.TINY,//stepSize
//						5000,// max steps
//						1,false);
		
		SurfaceOfLissajousLens newLissajousLensSurface = new SurfaceOfLissajousLens(
				new Vector3D(0, 0, 0),// centre, 
				2,// alpha,
				2,// beta,
				lissajousPlane,// surface,
				0.001,// deltaTau,
				0.1,// deltaXMax,
				5000,// maxSteps,
				IntegrationType.RK4,// integrationType,
				1,false);
		
//		sphere.setSurfaceProperty(lissajousLensSurface);
		lissajousPlane.setSurfaceProperty(newLissajousLensSurface);
		
//		System.out.println("lissajous centre "+lissajousLensSurface.getCentre()+ " sphere centre "+ sphere.getCentre());
		
//		lissajousLens.setSurfaceProperty(lissajousLensSurface);
				
//				new LissajousLensSurface(new Vector3D(0.1, 0, 0),10,0.75,
//						0.001,//MyMath.TINY,//stepSize
//						1000,// max steps
//						1,false), 
		
//		new Sphere(
//				"lissajous lens sphere", 	// description
//				new Vector3D(0, 0, 0),	// centre
//				0.25,null, null, null
//				)
				
//		scene.addSceneObject(lissajousLens);
//		scene.addSceneObject(sphere);
		
		scene.addSceneObject(lissajousPlane);
		
		studio.setScene(scene);
		
		// do the tracing of rays with trajectory
		scene.addSceneObject(
				new EditableRayTrajectory(
						"light-ray trajectory",	// description
						new Vector3D(1, 0, -0.2),	// startPoint
						0,	// startTime
						new Vector3D(-1, 0, -0.15),	// startDirection
						0.00401,	// rayRadius
						new SurfaceColourLightSourceIndependent(DoubleColour.RED, false),	// surfaceProperty
						100,	// maxTraceLevel
						true,	// reportToConsole
						scene,	// parent
						studio
						)
				);
		studio.traceRaysWithTrajectory();
//		// trace the rays with trajectory through the scene
//		
//		sphere.setSurfaceProperty(new SemiTransparent(new SurfaceColour("black matt", DoubleColour.BLACK, DoubleColour.BLACK, true), 0.5));
		lissajousPlane.setSurfaceProperty(new SemiTransparent(new SurfaceColour("black matt", DoubleColour.BLACK, DoubleColour.BLACK, true), 0.5));
		
//		scene.removeSceneObject(sphere);
//		scene.removeSceneObject(lissajousLens);
		
//		scene.addSceneObject(new Parallelepiped2(
//				"parallelepiped", 	// description
//				new Vector3D(0, 0, 0),	// centre
//				new Vector3D(0.25, 0, 0), new Vector3D(0, 0.01, 0), new Vector3D(0, 0, 0.25), 
//				new SemiTransparent(new SurfaceColour("black matt", DoubleColour.BLACK, DoubleColour.BLACK, true), 0.5), 
//				scene,	// parent
//				studio
//			));
	
//		scene.addSceneObject(new Sphere(
//				"lissajous lens on sphere", 	// description
//				new Vector3D(0, 0, 0),	// centre
//				0.25,
//				new SemiTransparent(new SurfaceColour("black matt", DoubleColour.BLACK, DoubleColour.BLACK, true), 0.5),
//				scene,
//				studio
//				));
		
		//studio.setScene(scene);
		
	}

	
	/**
	 * The main method, required so that this class can run as a Java application
	 * @param args
	 */
	public static void main(final String[] args)
	{
//        Runnable r = new NonInteractiveTIM();
//
//        EventQueue.invokeLater(r);
		(new SimpleSceneObjectTest()).run();
	}
}
