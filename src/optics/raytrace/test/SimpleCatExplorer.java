package optics.raytrace.test;

import java.io.PrintStream;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import math.*;
import net.miginfocom.swing.MigLayout;
import optics.DoubleColour;
import optics.raytrace.NonInteractiveTIMActionEnum;
import optics.raytrace.NonInteractiveTIMEngine;
import optics.raytrace.GUI.cameras.RenderQualityEnum;
import optics.raytrace.GUI.lowLevel.ApertureSizeType;
import optics.raytrace.GUI.lowLevel.DoublePanel;
import optics.raytrace.GUI.lowLevel.GUIBitsAndBobs;
import optics.raytrace.GUI.lowLevel.LabelledDoubleColourPanel;
import optics.raytrace.GUI.lowLevel.LabelledDoublePanel;
import optics.raytrace.GUI.lowLevel.LabelledVector3DPanel;
import optics.raytrace.core.Studio;
import optics.raytrace.core.StudioInitialisationType;
import optics.raytrace.exceptions.SceneException;
import optics.raytrace.sceneObjects.CatHead;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;



public class SimpleCatExplorer extends NonInteractiveTIMEngine
{

	private Vector3D centre, frontDirection, topDirection;
	private double radius;

	private DoubleColour headColour, noseColour, rightEyeColour, leftEyeColour, whiskerColour, innerEarColour;
	
	
	public SimpleCatExplorer()
	{
		super();
		//cat params
		centre = new Vector3D(0,0.1,9);// centre, 
		radius = 0.8;// radius, 
		frontDirection = new Vector3D(0,0,-1);// frontDirection, 
		topDirection =new Vector3D(0,1,0);// topDirection,
		headColour = DoubleColour.BLACK;
		noseColour = DoubleColour.SKIN;
		innerEarColour = DoubleColour.GREY50;
		rightEyeColour = new DoubleColour("green-Cyan", 0.32, 1.12, 1.04);
		leftEyeColour = new DoubleColour("green-Cyan", 0.32, 1.12, 1.04);
		whiskerColour = DoubleColour.WHITE;
		
		//camera params
		renderQuality = RenderQualityEnum.DRAFT;
		nonInteractiveTIMAction = NonInteractiveTIMActionEnum.INTERACTIVE;
//		nonInteractiveTIMAction = NonInteractiveTIMActionEnum.MOVIE;
		cameraViewDirection = new Vector3D(0,0,1);
		cameraHorizontalFOVDeg = 10;
		cameraTopDirection = new Vector3D(0,1,0); 
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
		studio.setCamera(getStandardCamera());

		// the scene
		SceneObjectContainer scene = new SceneObjectContainer("the scene", null, studio);
		studio.setScene(scene);
		
		// initialise the scene and lights
		StudioInitialisationType.initialiseSceneAndLights(
				StudioInitialisationType.MINIMALIST_LOWER_FLOOR,
				scene,
				studio
				);
		// ... and then adding scene objects to scene
		
		
		
		scene.addSceneObject(new CatHead(
				"cat head",// description, 
				centre,// centre, 
				radius,// radius, 
				frontDirection,// frontDirection, 
				topDirection,// topDirection,
				headColour,// headColour,
				noseColour,// noseColour,
				innerEarColour,//innerEarColour
				rightEyeColour,// rightEyeColour,		
				leftEyeColour,//new DoubleColour("green-Cyan", 64/255,254/255,208/255),// leftEyeColour,
				whiskerColour,// whiskerColour,
				scene,// parent, 
				studio// studio)
				)
				);		

		studio.setScene(scene);
	}
	//
	// for interactive version
	//
	
	//cat
	private LabelledVector3DPanel centrePanel, frontDirectionPanel, topDirectionPanel;
	private LabelledDoublePanel radiusPanel;
	private LabelledDoubleColourPanel headColourPanel, noseColourPanel, innerEarColourPanel, rightEyeColourPanel, leftEyeColourPanel, whiskerColourPanel;
	
	// camera
	private LabelledVector3DPanel cameraViewCentrePanel;
	// private JButton setCameraViewCentreToCloakCentroidButton;
	private LabelledVector3DPanel cameraViewDirectionPanel;
	private LabelledDoublePanel cameraDistancePanel;
	private DoublePanel cameraHorizontalFOVDegPanel;
	private JComboBox<ApertureSizeType> cameraApertureSizeComboBox;
	private LabelledDoublePanel cameraFocussingDistancePanel;

	JTabbedPane lensTabbedPane;


	/**
	 * add controls to the interactive control panel;
	 * override to modify
	 */
	@Override
	protected void createInteractiveControlPanel()
	{
		super.createInteractiveControlPanel();

		JTabbedPane tabbedPane = new JTabbedPane();
		interactiveControlPanel.add(tabbedPane, "span");

		JPanel catPanel = new JPanel();
		catPanel.setLayout(new MigLayout("insets 0"));
		tabbedPane.addTab("cat", catPanel);
	
		centrePanel = new LabelledVector3DPanel("centre");
		centrePanel.setVector3D(centre);
		catPanel.add(centrePanel, "span");
		
		frontDirectionPanel = new LabelledVector3DPanel("Front direction");
		frontDirectionPanel.setVector3D(frontDirection);
		catPanel.add(frontDirectionPanel, "span");
		
		topDirectionPanel = new LabelledVector3DPanel("Top direction");
		topDirectionPanel.setVector3D(topDirection);
		catPanel.add(topDirectionPanel, "span");
		
		radiusPanel = new LabelledDoublePanel("Cat radius");
		radiusPanel.setNumber(radius);
		catPanel.add(radiusPanel, "span");
		
		headColourPanel = new LabelledDoubleColourPanel("Head colour");
		headColourPanel.setDoubleColour(headColour);
		catPanel.add(headColourPanel, "wrap");
		
		noseColourPanel = new LabelledDoubleColourPanel("Nose colour");
		noseColourPanel.setDoubleColour(noseColour);
		catPanel.add(noseColourPanel, "wrap");
		
		innerEarColourPanel = new LabelledDoubleColourPanel("Inner ear colour");
		innerEarColourPanel.setDoubleColour(innerEarColour);
		catPanel.add(innerEarColourPanel, "wrap");
		
		rightEyeColourPanel = new LabelledDoubleColourPanel("Right eye colour");
		rightEyeColourPanel.setDoubleColour(rightEyeColour);
		catPanel.add(rightEyeColourPanel, "wrap");
		
		leftEyeColourPanel = new LabelledDoubleColourPanel("Left eye colour");
		leftEyeColourPanel.setDoubleColour(leftEyeColour);
		catPanel.add(leftEyeColourPanel, "wrap");
		
		whiskerColourPanel = new LabelledDoubleColourPanel("Whisker colour");
		whiskerColourPanel.setDoubleColour(whiskerColour);
		catPanel.add(whiskerColourPanel, "wrap");
		
		
		
		JPanel cameraPanel = new JPanel();
		cameraPanel.setLayout(new MigLayout("insets 0"));
		tabbedPane.addTab("Camera", cameraPanel);

		JPanel cameraViewCentreJPanel = new JPanel();
		cameraViewCentreJPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Centre of view"));
		cameraViewCentreJPanel.setLayout(new MigLayout("insets 0"));
		cameraPanel.add(cameraViewCentreJPanel, "span");

		cameraViewCentrePanel = new LabelledVector3DPanel("Position");
		cameraViewCentrePanel.setVector3D(cameraViewCentre);
		cameraViewCentreJPanel.add(cameraViewCentrePanel, "span");

		cameraViewDirectionPanel = new LabelledVector3DPanel("View direction");
		cameraViewDirectionPanel.setVector3D(cameraViewDirection);
		cameraPanel.add(cameraViewDirectionPanel, "span");

		cameraDistancePanel = new LabelledDoublePanel("Camera distance");
		cameraDistancePanel.setNumber(cameraDistance);
		cameraPanel.add(cameraDistancePanel, "span");

		cameraHorizontalFOVDegPanel = new DoublePanel();
		cameraHorizontalFOVDegPanel.setNumber(cameraHorizontalFOVDeg);
		cameraPanel.add(GUIBitsAndBobs.makeRow("Horizontal FOV", cameraHorizontalFOVDegPanel, "°"), "span");

		cameraApertureSizeComboBox = new JComboBox<ApertureSizeType>(ApertureSizeType.values());
		cameraApertureSizeComboBox.setSelectedItem(cameraApertureSize);
		cameraPanel.add(GUIBitsAndBobs.makeRow("Camera aperture", cameraApertureSizeComboBox), "span");		

		cameraFocussingDistancePanel = new LabelledDoublePanel("Focussing distance");
		cameraFocussingDistancePanel.setNumber(cameraFocussingDistance);
		cameraPanel.add(cameraFocussingDistancePanel);

	}

	/**
	 * called before rendering;
	 * override when adding fields
	 */
	@Override
	protected void acceptValuesInInteractiveControlPanel()
	{
		super.acceptValuesInInteractiveControlPanel();
		//cat
		centre = centrePanel.getVector3D();
		frontDirection = frontDirectionPanel.getVector3D();
		topDirection = topDirectionPanel.getVector3D();
		radius = radiusPanel.getNumber();
		headColour =headColourPanel.getDoubleColour();
		noseColour = noseColourPanel.getDoubleColour();
		innerEarColour = innerEarColourPanel.getDoubleColour();
		rightEyeColour = rightEyeColourPanel.getDoubleColour();
		leftEyeColour = leftEyeColourPanel.getDoubleColour();
		whiskerColour = whiskerColourPanel.getDoubleColour();
		
		
		// cameras
		cameraViewCentre = cameraViewCentrePanel.getVector3D();
		cameraViewDirection = cameraViewDirectionPanel.getVector3D();
		cameraDistance = cameraDistancePanel.getNumber();
		cameraHorizontalFOVDeg = cameraHorizontalFOVDegPanel.getNumber();
		cameraApertureSize = (ApertureSizeType)(cameraApertureSizeComboBox.getSelectedItem());
		cameraFocussingDistance = cameraFocussingDistancePanel.getNumber();
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
		(new SimpleCatExplorer()).run();
	}
}
