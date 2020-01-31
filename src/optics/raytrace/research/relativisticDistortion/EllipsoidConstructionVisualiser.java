package optics.raytrace.research.relativisticDistortion;

import java.io.PrintStream;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;

import math.Vector3D;
import optics.raytrace.NonInteractiveTIMActionEnum;
import optics.raytrace.NonInteractiveTIMEngine;
import optics.raytrace.GUI.cameras.RenderQualityEnum;
import optics.raytrace.GUI.lowLevel.ApertureSizeType;
import optics.raytrace.GUI.lowLevel.GUIBitsAndBobs;
import optics.raytrace.GUI.lowLevel.LabelledVector3DPanel;
import optics.raytrace.core.Studio;
import optics.raytrace.core.StudioInitialisationType;
import optics.raytrace.exceptions.SceneException;
import optics.raytrace.sceneObjects.Ellipsoid;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.surfaces.RelativisticDistortionEllipsoidConstructionSurface;


/**
 * A very basic example of NonInteractiveTIM.
 * 
 * @author Johannes Courtial
 */
public class EllipsoidConstructionVisualiser extends NonInteractiveTIMEngine
{
	
	private Vector3D betaHat;
	private double beta;
	
	private boolean simulateAsEllipsoidConstruction;
	
	/**
	 * Determines how to initialise the backdrop
	 */
	private StudioInitialisationType studioInitialisation;

	
	/**
	 * Constructor.
	 * Sets all parameters.
	 */
	public EllipsoidConstructionVisualiser()
	{
		super();
		
		renderQuality = RenderQualityEnum.DRAFT;
		
		nonInteractiveTIMAction = NonInteractiveTIMActionEnum.INTERACTIVE;
		movie = false;
		numberOfFrames = 50;
		firstFrame = 0;
		lastFrame = numberOfFrames-1;
		
		betaHat = new Vector3D(0, 0, 1);
		beta = 0.1;
		
		studioInitialisation = StudioInitialisationType.TIM_HEAD;	// the backdrop

		simulateAsEllipsoidConstruction = true;
		
		// camera parameters are set in createStudio()
	}

	
	/* (non-Javadoc)
	 * @see optics.raytrace.NonInteractiveTIMEngine#getFirstPartOfFilename()
	 */
	@Override
	public String getFirstPartOfFilename()
	{
		return
				"EllipsoidConstructionVisualiser"
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
		// double phi = -0.25+(movie?2.*Math.PI*frame/(numberOfFrames+1):0);
		cameraViewDirection = Vector3D.Z;
		//		new Vector3D(-Math.sin(phi), -.2, Math.cos(phi)).getNormalised();
		cameraDistance = 1;	// camera is located at (0, 0, 0)
		cameraViewCentre = cameraViewDirection;	// this places the camera at the origin
		cameraFocussingDistance = 10;
		cameraHorizontalFOVDeg = 40;
		cameraMaxTraceLevel = 100;
		cameraPixelsX = 640;
		cameraPixelsY = 480;
		cameraApertureSize = 
				ApertureSizeType.PINHOLE;
				// ApertureSizeType.SMALL;

		studio = new Studio();

		// the scene
		SceneObjectContainer scene = new SceneObjectContainer("the scene", null, studio);

		// initialise the scene and lights
		StudioInitialisationType.initialiseSceneAndLights(
				studioInitialisation,
				scene,
				studio
			);

		if(simulateAsEllipsoidConstruction)
		{
			// scene.addSceneObject(new Sphere("Sphere", new Vector3D(0, 0, 10), 1, SurfaceColour.CYAN_SHINY, scene, studio));

			Vector3D aHat = betaHat;
			Vector3D bHat = Vector3D.getANormal(aHat);
			Vector3D cHat = Vector3D.crossProduct(aHat, bHat);

			double gamma = 1./Math.sqrt(1.-beta*beta);

			// System.out.println("EllipsoidConstructionVisualiser::populateStudio: gamma = "+gamma);

			Ellipsoid ellipsoid = new Ellipsoid(
					"Ellipsoid",	// description
					betaHat.getProductWith(beta),	// centre
					aHat,	// a
					bHat.getProductWith(gamma),	// b
					cHat.getProductWith(gamma),	// c
					null,	// surfaceProperty -- set in a minute
					scene,	// parent
					studio
					);
			ellipsoid.setSurfaceProperty(
					// SurfaceColourLightSourceIndependent.GREEN
					new RelativisticDistortionEllipsoidConstructionSurface(scene, betaHat, beta, ellipsoid)
					);

			studio.setScene(ellipsoid);
			cameraBeta = new Vector3D(0, 0, 0);
		}
		else
		{
			studio.setScene(scene);
			cameraBeta = betaHat.getProductWith(beta);
		}
		studio.setCamera(getStandardCamera());

		
		// add anything to the scene by uncommenting the following line...
		// SceneObjectContainer scene = (SceneObjectContainer)studio.getScene();
		// ... and then adding scene objects to scene
	}

	
	// interactive stuff
	
	private JComboBox<StudioInitialisationType> studioInitialisationComboBox;
	private LabelledVector3DPanel betaPanel;
	private JCheckBox simulateAsEllipsoidConstructionCheckBox;

	
	/**
	 * add controls to the interactive control panel;
	 * override to modify
	 */
	@Override
	protected void createInteractiveControlPanel()
	{
		super.createInteractiveControlPanel();
		
		studioInitialisationComboBox = new JComboBox<StudioInitialisationType>(StudioInitialisationType.limitedValuesForInteractiveTIM);
		studioInitialisationComboBox.setSelectedItem(studioInitialisation);
		interactiveControlPanel.add(GUIBitsAndBobs.makeRow("Initialise backdrop to", studioInitialisationComboBox), "span");
		
		betaPanel = new LabelledVector3DPanel("Beta");
		betaPanel.setVector3D(betaHat.getProductWith(beta));
		interactiveControlPanel.add(betaPanel, "span");

		simulateAsEllipsoidConstructionCheckBox = new JCheckBox("Simulate as ellipsoid construction?");
		simulateAsEllipsoidConstructionCheckBox.setSelected(simulateAsEllipsoidConstruction);
		interactiveControlPanel.add(simulateAsEllipsoidConstructionCheckBox, "span");
	}
	
	/**
	 * called before rendering;
	 * override when adding fields
	 */
	@Override
	protected void acceptValuesInInteractiveControlPanel()
	{
		super.acceptValuesInInteractiveControlPanel();
				
		studioInitialisation = (StudioInitialisationType)(studioInitialisationComboBox.getSelectedItem());
		
		Vector3D betaV = betaPanel.getVector3D();
		betaHat = betaV.getNormalised();
		beta = betaV.getLength();
		
		simulateAsEllipsoidConstruction = simulateAsEllipsoidConstructionCheckBox.isSelected();
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
		(new EllipsoidConstructionVisualiser()).run();
	}
}
