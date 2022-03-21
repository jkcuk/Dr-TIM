package optics.raytrace.test;

import math.*;
import optics.raytrace.NonInteractiveTIMEngine;
import optics.raytrace.GUI.sceneObjects.EditableFramedRectangle;
import optics.raytrace.exceptions.SceneException;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.surfaces.SurfaceColour;
import optics.raytrace.surfaces.SurfaceOfBaffledPixellatedVolume;


/**
 * Perform a simple test with the surface of a bafflex pixellated volume
 * 
 * @author Johannes Courtial
 */
public class SurfaceOfBaffledPixellatedVolumeTest extends NonInteractiveTIMEngine
{
	/**
	 * Constructor.
	 * Sets all parameters.
	 */
	public SurfaceOfBaffledPixellatedVolumeTest()
	{
		super();
		
		// set any other parameters
		cameraViewDirection = new Vector3D(0, 0, 1);
		cameraViewCentre = new Vector3D(0, 0, 0);
		cameraDistance = 10;
		cameraFocussingDistance = 10;
		cameraHorizontalFOVDeg = 20;

	}

	
	/* (non-Javadoc)
	 * @see optics.raytrace.NonInteractiveTIMEngine#getFirstPartOfFilename()
	 */
	@Override
	public String getClassName()
	{
		return
				"SurfaceOfBaffledPixellatedVolumeTest"
				;
	}
	
	/* (non-Javadoc)
	 * @see optics.raytrace.NonInteractiveTIMEngine#populateStudio()
	 */
	@Override
	public void populateStudio()
	throws SceneException
	{
		super.populateSimpleStudio();
		SceneObjectContainer scene = (SceneObjectContainer)(studio.getScene());
		
		// add anything to the scene by uncommenting the following line...
		// SceneObjectContainer scene = (SceneObjectContainer)studio.getScene();
		// ... and then adding scene objects to scene
		
		// two scene objects...
		// the width and height vectors must be chosen such that the space behind the window is *inside*
		EditableFramedRectangle window1 = new EditableFramedRectangle(
				"window 1",	// description
				new Vector3D(0.5, -0.5, -0.5),	// corner
				new Vector3D(-1, 0, 0),	// widthVector
				new Vector3D(0, 1, 0),	// heightVector
				0.01,	// frameRadius
				null,	// windowSurfaceProperty -- set later
				SurfaceColour.BLUE_SHINY,	// frameSurfaceProperty
				true,	// showFrames
				scene,	// parent
				studio
		);
		// as the width vector now points from right to left, the corresponding scaling range has to be reversed so that the 
		// left side of the window corresponds to u=0 and the right side to u=1
		// (same as window 2, in which the width vector points from left to right)
		window1.getPane().setUScaling(1, 0);

		// the width and height vectors must be chosen such that the space in front of the window is inside
		EditableFramedRectangle window2 = new EditableFramedRectangle(
				"window 2",	// description
				new Vector3D(-0.5, -0.5, 0.5),	// corner
				new Vector3D(1, 0, 0),	// widthVector
				new Vector3D(0, 1, 0),	// heightVector
				0.01,	// frameRadius
				null,	// windowSurfaceProperty
				SurfaceColour.GREEN_SHINY,	// frameSurfaceProperty
				true,	// showFrames
				scene,	// parent
				studio
		);

		// ... and their surfaces
		SurfaceOfBaffledPixellatedVolume surface1 = new SurfaceOfBaffledPixellatedVolume(
				window1.getPane(),	// sceneObject
				null,	// otherPixellatedSurface -- set later
				0.1,	// uPeriod
				0.1,	// vPeriod
				0.01,	// uOffset
				0.02,	// vOffset
				true,	// showBaffles
				0.96,	// transmissionCoefficient
				true	// shadowThrowing
			);

		SurfaceOfBaffledPixellatedVolume surface2 = new SurfaceOfBaffledPixellatedVolume(
				window2.getPane(),	// sceneObject
				surface1,	// otherPixellatedSurface
				0.1,	// uPeriod
				0.1,	// vPeriod
				0.,	// uOffset
				0.,	// vOffset
				true,	// showBaffles
				0.96,	// transmissionCoefficient
				true	// shadowThrowing
			);

		surface1.setOtherPixellatedSurface(surface2);

		window1.setPaneSurfaceProperty(surface1);
		window2.setPaneSurfaceProperty(surface2);
		
		scene.addSceneObject(window1);
		scene.addSceneObject(window2);
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
		(new SurfaceOfBaffledPixellatedVolumeTest()).run();
	}
}
