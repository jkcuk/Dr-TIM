package optics.raytrace.teaching.electricityAndMagnetism;

import java.awt.EventQueue;
import java.util.ArrayList;

import javax.swing.JCheckBox;

import math.*;
import optics.DoubleColour;
import optics.raytrace.NonInteractiveTIMActionEnum;
import optics.raytrace.NonInteractiveTIMEngine;
import optics.raytrace.GUI.cameras.RenderQualityEnum;
import optics.raytrace.GUI.lowLevel.ApertureSizeType;
import optics.raytrace.GUI.lowLevel.GUIBitsAndBobs;
import optics.raytrace.GUI.lowLevel.IntPanel;
import optics.raytrace.GUI.sceneObjects.EditableArrow;
import optics.raytrace.GUI.sceneObjects.EditableScaledParametrisedSphere;
import optics.raytrace.core.LightSource;
import optics.raytrace.core.Studio;
import optics.raytrace.exceptions.SceneException;
import optics.raytrace.sceneObjects.CylinderTube;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.surfaces.SurfaceColour;
import optics.raytrace.surfaces.SurfaceColourLightSourceIndependent;


/**
 * Plots a metallic loop in a B field
 * 
 * @author Johannes Courtial
 */
public class WireLoopInBField extends NonInteractiveTIMEngine
{
	/**
	 * Constructor.
	 * Sets all parameters.
	 */
	public WireLoopInBField()
	{
		super();
		
		renderQuality = RenderQualityEnum.DRAFT;
		nonInteractiveTIMAction = NonInteractiveTIMActionEnum.INTERACTIVE;

		// for movie version
		numberOfFrames = 20;
		firstFrame = 0;
		lastFrame = numberOfFrames-1;
		
		movie = false;

		// camera parameters are set in createStudio()
	}

	
	/* (non-Javadoc)
	 * @see optics.raytrace.NonInteractiveTIMEngine#getFirstPartOfFilename()
	 */
	@Override
	public String getClassName()
	{
		return
				"WireLoopInBField"
				;
	}
	
	/* (non-Javadoc)
	 * @see optics.raytrace.NonInteractiveTIMEngine#populateStudio()
	 */
	@Override
	public void populateStudio()
	throws SceneException
	{
		// varies from 0 to (almost) pi
		double alpha = (movie?Math.PI*frame/(numberOfFrames+1):0);
		
		double phi=-0.25;
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

		studio = new Studio();

		// the scene
		SceneObjectContainer scene = new SceneObjectContainer("the scene", null, studio);

		studio.setScene(scene);
		studio.setLights(LightSource.getStandardLightsFromBehind());
		studio.setCamera(getStandardCamera());
		
		
		
		scene.addSceneObject(new EditableScaledParametrisedSphere(
				"sky",
				new Vector3D(0,0,0),	// centre
				MyMath.HUGE,	// huge radius
				new SurfaceColourLightSourceIndependent(DoubleColour.WHITE, true),
				scene,
				studio
			));

		// the wire loop
		double wireRadius = 0.05;
		double loopWidth = 1;
		double loopHeight = 1;
		double loopAngle = MyMath.deg2rad(60) + alpha;
		double cos = Math.cos(loopAngle);
		double sin = Math.sin(loopAngle);
		ArrayList<Vector3D> loopVertices = new ArrayList<Vector3D>();
		loopVertices.add(new Vector3D(-loopWidth  , cos*3*wireRadius, sin*3*wireRadius));
		loopVertices.add(new Vector3D(-loopWidth/2, cos*3*wireRadius, sin*3*wireRadius));
		loopVertices.add(new Vector3D(-loopWidth/2, cos*loopHeight/2, sin*loopHeight/2));
		loopVertices.add(new Vector3D( loopWidth/2, cos*loopHeight/2, sin*loopHeight/2));
		loopVertices.add(new Vector3D( loopWidth/2,-cos*loopHeight/2,-sin*loopHeight/2));
		loopVertices.add(new Vector3D(-loopWidth/2,-cos*loopHeight/2,-sin*loopHeight/2));
		loopVertices.add(new Vector3D(-loopWidth/2,-cos*3*wireRadius,-sin*3*wireRadius));
		loopVertices.add(new Vector3D(-loopWidth  ,-cos*3*wireRadius,-sin*3*wireRadius));
		scene.addSceneObject(new CylinderTube(
				"wire loop",	// description
				loopVertices,	// vertices
				0,	// startTime
				wireRadius,	// radius
				true,	// spheresAtEnds
				SurfaceColour.GREY50_SHINY,	// Reflective.PERFECT_MIRROR,	// surfaceProperty
				scene,	// parent
				studio
			));
		
		// the B field
		double bFieldWidth = 3.5;
		double bFieldDepth = 3.5;
		double bFieldHeight = 2;
		double vectorRadius = 0.02;
		for(double x = -bFieldWidth/2; x <= bFieldWidth/2; x += bFieldWidth/4)
			for(double z = -bFieldDepth/2; z <= bFieldDepth/2; z += bFieldDepth/4)
				scene.addSceneObject(new EditableArrow(
						"B field vector",	// description
						new Vector3D(x, -bFieldHeight/2, z),	// startPoint
						new Vector3D(x,  bFieldHeight/2, z),	// endPoint
						vectorRadius,	// shaftRadius
						0.1*bFieldHeight,	// tipLength
						MyMath.deg2rad(30),	// tipAngle
						SurfaceColour.RED_MATT,	// surfaceProperty
						scene,	// parent
						studio
					));
		
//		// the rotation axis
//		scene.addSceneObject(new EditableParametrisedCylinder(
//				"rotation axis",	// description
//				new Vector3D(-10, 0, 0),	// startPoint
//				new Vector3D( 10, 0, 0),	// endPoint
//				vectorRadius,	// radius
//				SurfaceColour.BLACK_MATT,	// surfaceProperty
//				scene,	// parent
//				studio
//		));

	}

	
	private JCheckBox movieCheckBox;
	private IntPanel numberOfFramesPanel;
	
	
	/**
	 * add controls to the interactive control panel;
	 * override to modify
	 */
	@Override	
	protected void createInteractiveControlPanel()	
	{	
		super.createInteractiveControlPanel();	
		
		movieCheckBox = new JCheckBox();
		movieCheckBox.setSelected(movie);
		
		numberOfFramesPanel = new IntPanel();
		numberOfFramesPanel.setNumber(numberOfFrames);
		
		interactiveControlPanel.add(GUIBitsAndBobs.makeRow(movieCheckBox, "create movie with", numberOfFramesPanel, "frames"));
	}
	
	/**
	 * called before rendering;
	 * override when adding fields
	 */
	@Override
	protected void acceptValuesInInteractiveControlPanel()
	{
		super.acceptValuesInInteractiveControlPanel();
		
		movie = movieCheckBox.isSelected();
		numberOfFrames = numberOfFramesPanel.getNumber();
		lastFrame = numberOfFrames-1;
	}
	
	
	/**
	 * The main method, required so that this class can run as a Java application
	 * @param args
	 */
	public static void main(final String[] args)
	{
        Runnable r = new WireLoopInBField();

        EventQueue.invokeLater(r);
	}
}
