package optics.raytrace.test;

import java.io.PrintStream;

import math.*;
import optics.raytrace.NonInteractiveTIMActionEnum;
import optics.raytrace.NonInteractiveTIMEngine;
import optics.raytrace.GUI.cameras.RenderQualityEnum;
import optics.raytrace.core.SceneObject;
import optics.raytrace.exceptions.SceneException;
import optics.raytrace.sceneObjects.TimHead;
import optics.raytrace.sceneObjects.WrappedSceneObject;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.surfaces.Reflective;


/**
 * @author Johannes Courtial
 */
public class WrappedSceneObjectTest extends NonInteractiveTIMEngine
{
	/**
	 * Constructor.
	 * Sets all parameters.
	 */
	public WrappedSceneObjectTest()
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
	public String getClassName()
	{
		return
				"WrappedSceneObjectTest"
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
		super.populateSimpleStudio();
		
		// add anything to the scene by uncommenting the following line...
		SceneObjectContainer scene = (SceneObjectContainer)studio.getScene();
		// ... and then adding scene objects to scene
		
		// scene.addSceneObject(new Sphere("The sphere", new Vector3D(0,0,0), 1, SurfaceColour.CYAN_SHINY, scene, studio));
		SceneObject tim = new TimHead(
				"Dr TIM himself", 	// description
				new Vector3D(0, 0, 0),	// centre
				scene,	// parent
				studio
			);
		SceneObject wrappedTim = new WrappedSceneObject(
				tim,	// sceneObject
				Reflective.PERFECT_MIRROR
				// SurfaceColour.CYAN_SHINY	// surfaceProperty
			);
		scene.addSceneObject(wrappedTim);
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
		(new WrappedSceneObjectTest()).run();
	}
}
