package optics.raytrace;

import java.io.PrintStream;

import math.*;
import optics.raytrace.NonInteractiveTIMEngine;
import optics.raytrace.GUI.cameras.RenderQualityEnum;
import optics.raytrace.GUI.lowLevel.ApertureSizeType;
import optics.raytrace.exceptions.SceneException;
import optics.raytrace.sceneObjects.Sphere;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.surfaces.SurfaceColour;


/**
 * A very basic example of NonInteractiveTIM.
 * 
 * @author Johannes Courtial
 */
public class NonInteractiveTIM extends NonInteractiveTIMEngine
{
	/**
	 * Constructor.
	 * Sets all parameters.
	 */
	public NonInteractiveTIM()
	{
		super();
		
		renderQuality = RenderQualityEnum.DRAFT;
		
		nonInteractiveTIMAction = NonInteractiveTIMActionEnum.INTERACTIVE;
//		nonInteractiveTIMAction = NonInteractiveTIMActionEnum.MOVIE;
		movie = false;
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
				"NonInteractiveTIM"
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
		double phi = -0.25+(movie?2.*Math.PI*frame/(numberOfFrames+1):0);
		cameraViewDirection = new Vector3D(-Math.sin(phi), -.2, Math.cos(phi));
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
		
		// add anything to the scene by uncommenting the following line...
		SceneObjectContainer scene = (SceneObjectContainer)studio.getScene();
		// ... and then adding scene objects to scene
		
		scene.addSceneObject(new Sphere("The sphere", new Vector3D(0,0,0), 1, SurfaceColour.CYAN_SHINY, scene, studio));
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
		(new NonInteractiveTIM()).run();
	}
}
