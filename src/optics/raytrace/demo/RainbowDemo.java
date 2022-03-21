package optics.raytrace.demo;

import java.awt.EventQueue;

import math.*;
import optics.raytrace.NonInteractiveTIMActionEnum;
import optics.raytrace.NonInteractiveTIMEngine;
import optics.raytrace.GUI.cameras.RenderQualityEnum;
import optics.raytrace.GUI.sceneObjects.EditableParametrisedPlane;
import optics.raytrace.exceptions.SceneException;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.surfaces.Rainbow;


/**
 * A test/demo of the Rainbow surface property.
 * 
 * @author Johannes Courtial
 */
public class RainbowDemo extends NonInteractiveTIMEngine
{
	/**
	 * Constructor.
	 * Sets all parameters.
	 */
	public RainbowDemo()
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
				"RainbowDemo"
				;
	}
	
	/* (non-Javadoc)
	 * @see optics.raytrace.NonInteractiveTIMEngine#populateStudio()
	 */
	@Override
	public void populateStudio()
	throws SceneException
	{
		double phi = 0*Math.PI/180.; // -0.25+(movie?2.*Math.PI*frame/(movieNumberOfFrames+1):0);
		cameraViewDirection = new Vector3D(-Math.sin(phi), -.2, Math.cos(phi));
//		cameraViewCentre = new Vector3D(0, 0, 0);
//		cameraDistance = 10;	// camera is located at (0, 0, 0)
//		cameraFocussingDistance = 10;
		cameraHorizontalFOVDeg = 90;
//		cameraMaxTraceLevel = 100;
//		cameraPixelsX = 640;
//		cameraPixelsY = 480;
//		cameraApertureSize = 
//				ApertureSizeType.PINHOLE;
//				// ApertureSizeType.SMALL;

		super.populateSimpleStudio();
		
		SceneObjectContainer scene = (SceneObjectContainer)studio.getScene();
	
		EditableParametrisedPlane rainbowPlane = new EditableParametrisedPlane(
				"Rainbow plane",	// description
				new Vector3D(0, 0, 0),	// pointOnPlane
				new Vector3D(0.5, 0, 1),	// normal
				new Rainbow(
						1,	// saturation
						.25,	// lightness
						new Vector3D(100,300,-500)	// lightSourcePosition
					),	// sp
				scene,	// parent
				studio
			);
		
		// ... and add it to the scene
		scene.addSceneObject(rainbowPlane);
	}

	
	/**
	 * The main method, required so that this class can run as a Java application
	 * @param args
	 */
	public static void main(final String[] args)
	{
        Runnable r = new RainbowDemo();

        EventQueue.invokeLater(r);
	}
}
