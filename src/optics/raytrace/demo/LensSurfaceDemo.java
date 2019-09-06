package optics.raytrace.demo;

import java.awt.EventQueue;

import math.*;
import optics.raytrace.NonInteractiveTIMActionEnum;
import optics.raytrace.NonInteractiveTIMEngine;
import optics.raytrace.GUI.cameras.RenderQualityEnum;
import optics.raytrace.GUI.lowLevel.ApertureSizeType;
import optics.raytrace.exceptions.SceneException;
import optics.raytrace.sceneObjects.LensSurface_old;
import optics.raytrace.sceneObjects.RayTrajectoryCone;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectPrimitiveIntersection;
import optics.raytrace.surfaces.SurfaceColour;


/**
 * Test/demo of a LensSurface.
 * 
 * @author Johannes Courtial, Jakub Belin
 */
public class LensSurfaceDemo extends NonInteractiveTIMEngine
{
	/**
	 * Constructor.
	 * Sets all parameters.
	 */
	public LensSurfaceDemo()
	{
		super();
		
		renderQuality = RenderQualityEnum.DRAFT;
		nonInteractiveTIMAction = NonInteractiveTIMActionEnum.INTERACTIVE;
		
		numberOfFrames = 50;
		firstFrame = 0;
		lastFrame = numberOfFrames-1;

		// camera parameters are set in createStudio()
	}

	
	/* (non-Javadoc)
	 * @see optics.raytrace.NonInteractiveTIMEngine#getFirstPartOfFilename()
	 */
	@Override
	public String getFirstPartOfFilename()
	{
		return
				"LensSurfaceDemo"
				;
	}
	
	/* (non-Javadoc)
	 * @see optics.raytrace.NonInteractiveTIMEngine#populateStudio()
	 */
	@Override
	public void populateStudio()
	throws SceneException
	{
		double phi = 0;
		cameraViewDirection = new Vector3D(-Math.sin(phi), 0, Math.cos(phi));
		cameraViewCentre = new Vector3D(0, 0, 0);
		cameraDistance = 10;	// camera is located at (0, 0, 0)
		cameraFocussingDistance = 10;
		cameraHorizontalFOVDeg = 20;
		cameraMaxTraceLevel = 100;
		cameraPixelsX = 640;
		cameraPixelsY = 480;
		cameraApertureSize = 
				ApertureSizeType.PINHOLE;
				// ApertureSizeType.SMALL;

		super.populateSimpleStudio();
		
		SceneObjectContainer scene = (SceneObjectContainer)studio.getScene();

		// create a scene-object-primitive intersection, ...
		SceneObjectPrimitiveIntersection sceneObjectPrimitiveIntersection = new SceneObjectPrimitiveIntersection(
				"Scene-object-primitive intersection",	// description
				scene,	// parent
				studio
			);
		scene.addSceneObject(sceneObjectPrimitiveIntersection);


//		// view through two lens surfaces
//		LensSurface lensSurface1 = new LensSurface(
//				"Lens surface 1",	// description
//				new Vector3D(0, 0, -10),	// focus
//				1.5,	// refractiveIndex
//				new Vector3D(0, 0, 1),	// directionInside
//				10,	// focalDistance
//				0.96,	// transmissionCoefficient
//				false,	// shadowThrowing
//				sceneObjectPrimitiveIntersection,	// parent
//				studio
//		);
//		// lensSurface1.setSurfaceProperty(Reflective.PERFECT_MIRROR);
//		sceneObjectPrimitiveIntersection.addPositiveSceneObjectPrimitive(lensSurface1);
//
//		LensSurface lensSurface2 = new LensSurface(
//				"Lens surface 2",	// description
//				new Vector3D(0, 0, 10.5),	// focus
//				1.5,	// refractiveIndex
//				new Vector3D(0, 0, -1),	// directionInside
//				10,	// focalDistance
//				0.96,	// transmissionCoefficient
//				false,	// shadowThrowing
//				sceneObjectPrimitiveIntersection,	// parent
//				studio
//		);
//		// lensSurface.setSurfaceProperty(SurfaceColour.GREEN_SHINY);
//		sceneObjectPrimitiveIntersection.addPositiveSceneObjectPrimitive(lensSurface2);
//		
//		scene.addSceneObject(new Sphere(
//				"tiny sphere, imaged to camera", 	// description
//				new Vector3D(0, 0, 10.5),	// centre
//				0.01,	// radius
//				SurfaceColour.YELLOW_MATT,	// surfaceProperty
//				scene,	// parent
//				studio
//			));
		
		
		// side view of lens surface
		LensSurface_old lensSurface1 = new LensSurface_old(
				"Lens surface 1",	// description
				new Vector3D(-.5, 0, 0),	// principal point
				1,	// focal length
				1.5,	// refractiveIndex
				new Vector3D(1, 0, 0),	// directionInside
				0.96,	// transmissionCoefficient
				false,	// shadowThrowing
				sceneObjectPrimitiveIntersection,	// parent
				studio
				);
		// lensSurfaceSide.setSurfaceProperty(SurfaceColour.CYAN_MATT);
		sceneObjectPrimitiveIntersection.addPositiveSceneObjectPrimitive(lensSurface1);
		// side view of lens surface
		LensSurface_old lensSurface2 = new LensSurface_old(
				"Lens surface 2",	// description
				new Vector3D(.5, 0, 0),	// principal point
				1,	// focal length
				1.5,	// refractiveIndex
				new Vector3D(-1, 0, 0),	// directionInside
				0.96,	// transmissionCoefficient
				false,	// shadowThrowing
				sceneObjectPrimitiveIntersection,	// parent
				studio
				);
		// lensSurfaceSide.setSurfaceProperty(SurfaceColour.CYAN_MATT);
		sceneObjectPrimitiveIntersection.addPositiveSceneObjectPrimitive(lensSurface2);
		// sceneObjectPrimitiveIntersection.addPositiveSceneObjectPrimitive(new Sphere("", new Vector3D(0, 0, 0), 1, SemiTransparent.RED_SHINY_SEMITRANSPARENT, sceneObjectPrimitiveIntersection, studio));
		
		scene.addSceneObject(new RayTrajectoryCone(
				"ray cone",	// description
				new Vector3D(-1.5, 0, 0),	// startPoint
				0,	// startTime
				new Vector3D(1, 0, 0),	// axisDirection
				MyMath.deg2rad(10),	// coneAngle
				10,	// numberOfRays
				0.01,	// rayRadius
				SurfaceColour.RED_SHINY,	// surfaceProperty
				100,	// maxTraceLevel
				scene,	// parent
				studio
			));
	}

	
	/**
	 * The main method, required so that this class can run as a Java application
	 * @param args
	 */
	public static void main(final String[] args)
	{
        Runnable r = new LensSurfaceDemo();

        EventQueue.invokeLater(r);
	}
}
