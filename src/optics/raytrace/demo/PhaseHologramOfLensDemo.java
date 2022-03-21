package optics.raytrace.demo;

import java.awt.EventQueue;

import math.*;
import optics.raytrace.sceneObjects.ParametrisedCentredParallelogram;
import optics.raytrace.sceneObjects.Sphere;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.surfaces.PhaseHologramOfLens;
import optics.raytrace.surfaces.SurfaceColour;
import optics.raytrace.NonInteractiveTIMActionEnum;
import optics.raytrace.NonInteractiveTIMEngine;
import optics.raytrace.GUI.cameras.RenderQualityEnum;
import optics.raytrace.GUI.lowLevel.ApertureSizeType;
import optics.raytrace.core.*;
import optics.raytrace.exceptions.SceneException;


/**
 * PhaseHologramOfLens surface demo.
 * 
 * Note that in Project>Properties (or alternatively Run>Run Configurations...) the working
 * directory (which can be found in the arguments tab) has to be set to the directory
 * containing all the images, normally
 * ${workspace_loc:TIM/demo/}
 * 
 * Adapted from Point2PointImagingDemo.java
 */
public class PhaseHologramOfLensDemo
extends NonInteractiveTIMEngine
{
	/**
	 * Constructor.
	 * Sets all parameters.
	 */
	public PhaseHologramOfLensDemo()
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
	public String getClassName()
	{
		return
				"PhaseHologramOfLensDemo"
				;
	}
	
	/* (non-Javadoc)
	 * @see optics.raytrace.NonInteractiveTIMEngine#populateStudio()
	 */
	@Override
	public void populateStudio()
	throws SceneException
	{
		cameraViewDirection = new Vector3D(0, 0, 1);
		cameraViewCentre = new Vector3D(0, 0, 10);
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
		
		// add anything to the scene by uncommenting the following line...
		SceneObjectContainer scene = (SceneObjectContainer)studio.getScene();
		// ... and then adding scene objects to scene
		
		// the standard scene objects
		scene.addSceneObject(SceneObjectClass.getSkySphere(scene, studio));	// the sky
		scene.addSceneObject(SceneObjectClass.getChequerboardFloor(scene, studio));	// the checkerboard floor

		//
		// add any other scene objects
		//
		
		PhaseHologramOfLens h = new PhaseHologramOfLens(
				5,	// focal length
				new Vector3D(0, 0, 10),	// principal point
				1,	// throughput factor
				false,	// true = works in reflection, false = works in transmission
				true	// shadow-throwing
			);

		scene.addSceneObject(new ParametrisedCentredParallelogram(
				"rectangle",
				new Vector3D(0, 0, 10),	// centre
				new Vector3D(1,0,0),
				new Vector3D(0,1,0),
				h,
				scene,
				studio
			));

		scene.addSceneObject(new Sphere(
				"green sphere",	// description,
				new Vector3D(0, 0, 20),	// centre,
				0.1,	// radius,
				// p2pImagingSurfaceReflective,
				SurfaceColour.GREEN_MATT,	// surfaceProperty,
				scene,	// parent, 
				studio
		));
	}

	
	/**
	 * The main method, required so that this class can run as a Java application
	 * @param args
	 */
	public static void main(final String[] args)
	{
        Runnable r = new PhaseHologramOfLensDemo();

        EventQueue.invokeLater(r);
	}

}
