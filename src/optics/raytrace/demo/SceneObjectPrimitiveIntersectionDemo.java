package optics.raytrace.demo;

import java.awt.EventQueue;

import math.*;
import optics.raytrace.NonInteractiveTIMActionEnum;
import optics.raytrace.NonInteractiveTIMEngine;
import optics.raytrace.GUI.cameras.RenderQualityEnum;
import optics.raytrace.exceptions.SceneException;
import optics.raytrace.sceneObjects.Sphere;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectPrimitiveIntersection;
import optics.raytrace.surfaces.SurfaceColour;


/**
 * A test/demo of the SceneObjectPrimitiveIntersection class.
 * 
 * @author Johannes Courtial
 */
public class SceneObjectPrimitiveIntersectionDemo extends NonInteractiveTIMEngine
{
	/**
	 * Constructor.
	 * Sets all parameters.
	 */
	public SceneObjectPrimitiveIntersectionDemo()
	{
		super();
		
		renderQuality = RenderQualityEnum.DRAFT;
		nonInteractiveTIMAction = NonInteractiveTIMActionEnum.INTERACTIVE;

		// camera parameters are set in createStudio()
	}

	
	/* (non-Javadoc)
	 * @see optics.raytrace.NonInteractiveTIMEngine#getFirstPartOfFilename()
	 */
	@Override
	public String getClassName()
	{
		return
				"SceneObjectPrimitiveIntersectionDemo"
				;
	}
	
	/* (non-Javadoc)
	 * @see optics.raytrace.NonInteractiveTIMEngine#populateStudio()
	 */
	@Override
	public void populateStudio()
	throws SceneException
	{
		double phi = -20*Math.PI/180.; // -0.25+(movie?2.*Math.PI*frame/(movieNumberOfFrames+1):0);
		cameraViewDirection = new Vector3D(-Math.sin(phi), -.2, Math.cos(phi));
//		cameraViewCentre = new Vector3D(0, 0, 0);
//		cameraDistance = 10;	// camera is located at (0, 0, 0)
//		cameraFocussingDistance = 10;
//		cameraHorizontalFOV = 20;
//		cameraMaxTraceLevel = 100;
//		cameraPixelsX = 640;
//		cameraPixelsY = 480;
//		cameraApertureSize = 
//				ApertureSizeType.PINHOLE;
//				// ApertureSizeType.SMALL;

		super.populateSimpleStudio();
		
		SceneObjectContainer scene = (SceneObjectContainer)studio.getScene();
		
		// create a scene-object-primitive intersection, ...
		SceneObjectPrimitiveIntersection sceneObjectPrimitiveIntersection = new SceneObjectPrimitiveIntersection(
				"Scene-object-primitive intersection",	// description
				scene,	// parent
				studio
			);
		
		// ... populate it, ...
		sceneObjectPrimitiveIntersection.addPositiveSceneObjectPrimitive(
				new Sphere(
						"positive blue sphere",
						new Vector3D(.5, 0, 0),	// centre
						1,	// radius
						SurfaceColour.BLUE_SHINY,	// surface property
						sceneObjectPrimitiveIntersection,	// parent
						studio
					)
			);
		sceneObjectPrimitiveIntersection.addPositiveSceneObjectPrimitive(
				new Sphere(
						"positive green sphere",
						new Vector3D(-.4, 0, 0),	// centre
						1.1,	// radius
						SurfaceColour.GREEN_SHINY,	// surface property
						sceneObjectPrimitiveIntersection,	// parent
						studio
					)
			);

		sceneObjectPrimitiveIntersection.addNegativeSceneObjectPrimitive(
				new Sphere(
						"negative yellow sphere",
						new Vector3D(-.5, 0, -1),	// centre
						1,	// radius
						SurfaceColour.YELLOW_SHINY,	// surface property
						sceneObjectPrimitiveIntersection,	// parent
						studio
					)
			);
		sceneObjectPrimitiveIntersection.addNegativeSceneObjectPrimitive(
				new Sphere(
						"negative red sphere",
						new Vector3D(-.5, 1, 0),	// centre
						0.8,	// radius
						SurfaceColour.RED_SHINY,	// surface property
						sceneObjectPrimitiveIntersection,	// parent
						studio
					)
			);

		
		// ... and add it to the scene
		scene.addSceneObject(sceneObjectPrimitiveIntersection);
	}

	
	/**
	 * The main method, required so that this class can run as a Java application
	 * @param args
	 */
	public static void main(final String[] args)
	{
        Runnable r = new SceneObjectPrimitiveIntersectionDemo();

        EventQueue.invokeLater(r);
	}
}
