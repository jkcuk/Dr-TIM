package optics.raytrace.research.refractiveTaylorSeries;

import java.io.PrintStream;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import math.*;
import net.miginfocom.swing.MigLayout;
import optics.raytrace.NonInteractiveTIMActionEnum;
import optics.raytrace.NonInteractiveTIMEngine;
import optics.raytrace.GUI.cameras.RenderQualityEnum;
import optics.raytrace.GUI.lowLevel.ApertureSizeType;
import optics.raytrace.GUI.lowLevel.DoublePanel;
import optics.raytrace.GUI.lowLevel.GUIBitsAndBobs;
import optics.raytrace.GUI.lowLevel.LabelledDoublePanel;
import optics.raytrace.GUI.lowLevel.LabelledVector3DPanel;
import optics.raytrace.core.SceneObjectPrimitive;
import optics.raytrace.core.Studio;
import optics.raytrace.core.StudioInitialisationType;
import optics.raytrace.core.SurfacePropertyPrimitive;
import optics.raytrace.exceptions.SceneException;
import optics.raytrace.sceneObjects.ParametrisedPlane;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.surfaces.EwanDerivativeControlSurfaceExpansion;
import optics.raytrace.surfaces.SurfaceColour;



public class DerivativeControlSurfaceExplorerCOPY extends NonInteractiveTIMEngine
{
	private double theta;
	private double phi;
	private boolean pixellated;
	private double pixelPeriodU;
	private double pixelPeriodV;

	
	public DerivativeControlSurfaceExplorerCOPY()
	{
		super();
		
		//camera params
		renderQuality = RenderQualityEnum.DRAFT;
		nonInteractiveTIMAction = NonInteractiveTIMActionEnum.INTERACTIVE;
//		nonInteractiveTIMAction = NonInteractiveTIMActionEnum.MOVIE;
		cameraViewDirection = new Vector3D(0,0,1);
		cameraHorizontalFOVDeg = 10;
		cameraTopDirection = new Vector3D(0,1,0); 
		cameraDistance = 0;
		traceRaysWithTrajectory = false;
		
		theta = MyMath.deg2rad(90);
		phi = MyMath.deg2rad(90);
		pixellated = false;
		pixelPeriodU =0.1;
		pixelPeriodV = 0.1;
		// camera parameters are set in createStudio()
	}

	
	/* (non-Javadoc)
	 * @see optics.raytrace.NonInteractiveTIMEngine#getFirstPartOfFilename()
	 */
	@Override
	public String getClassName()
	{
		return
				"DerivativeControlSurfaceExplorerCOPY"
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
				StudioInitialisationType.LATTICE,
				scene,
				studio
				);
		// ... and then adding scene objects to scene
		
		
//		DerivativeControlSurfaceRotating dcs = new DerivativeControlSurfaceRotating(theta);
		
		EwanDerivativeControlSurfaceExpansion dcs =  new EwanDerivativeControlSurfaceExpansion(
				new ParametrisedPlane(
						"Plane", // description
						new Vector3D(0, 0, 10),	// pointOnPlane
						new Vector3D(0, 0, 1),	// normal
						new Vector3D(1, 0, 0),	// v1
						new Vector3D(0, 1, 0),	// v2
						SurfaceColour.BLUE_SHINY,	// sp
						null,	// parent
						null	// studio
					),
				SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT,
				false,	//  shadow-throwing
				new Vector3D(0, 0, 0),	// eyePosition
				new Vector3D(0, 0, 40),	// pointOnPlane,
				new Vector3D(0, 0, 1),	// normalisedPlaneNormal, 
				theta,	// rotationAngleRad
			    phi,   // jacAngleRad
			    pixellated, 
			    pixelPeriodU, 
		        pixelPeriodV
			); 
		
		SceneObjectPrimitive s = (SceneObjectPrimitive)(dcs.getParametrisedObject());
		s.setSurfaceProperty(dcs);
		scene.addSceneObject(s);		

		studio.setScene(scene);
	}
	//
	// for interactive version
	//
	
//	private LabelledVector3DPanel centrePanel, frontDirectionPanel, topDirectionPanel;
	private LabelledDoublePanel thetaPanel;
	private LabelledDoublePanel phiPanel; 
//	private LabelledDoubleColourPanel headColourPanel, noseColourPanel, innerEarColourPanel, rightEyeColourPanel, leftEyeColourPanel, whiskerColourPanel;
//	
	// camera
	private LabelledVector3DPanel cameraViewCentrePanel;
	// private JButton setCameraViewCentreToCloakCentroidButton;
	private LabelledVector3DPanel cameraViewDirectionPanel;
	private LabelledDoublePanel cameraDistancePanel;
	private DoublePanel cameraHorizontalFOVDegPanel;
	private JComboBox<ApertureSizeType> cameraApertureSizeComboBox;
	private LabelledDoublePanel cameraFocussingDistancePanel;
//
//	JTabbedPane lensTabbedPane;
//
//
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
		tabbedPane.addTab("Derivative-control surface", catPanel);
	
//		centrePanel = new LabelledVector3DPanel("centre");
//		centrePanel.setVector3D(centre);
//		catPanel.add(centrePanel, "span");
//		
//		frontDirectionPanel = new LabelledVector3DPanel("Front direction");
//		frontDirectionPanel.setVector3D(frontDirection);
//		catPanel.add(frontDirectionPanel, "span");
//		
//		topDirectionPanel = new LabelledVector3DPanel("Top direction");
//		topDirectionPanel.setVector3D(topDirection);
//		catPanel.add(topDirectionPanel, "span");
//		
		thetaPanel = new LabelledDoublePanel("rotation angle (degrees)");
		thetaPanel.setNumber(MyMath.rad2deg(theta));
		catPanel.add(thetaPanel, "span");
		
		phiPanel = new LabelledDoublePanel("jacobian rotation angle (degrees)");
		phiPanel.setNumber(MyMath.rad2deg(-phi));
		catPanel.add(phiPanel, "span");
//		
//		headColourPanel = new LabelledDoubleColourPanel("Head colour");
//		headColourPanel.setDoubleColour(headColour);
//		catPanel.add(headColourPanel, "wrap");
//		
//		noseColourPanel = new LabelledDoubleColourPanel("Nose colour");
//		noseColourPanel.setDoubleColour(noseColour);
//		catPanel.add(noseColourPanel, "wrap");
//		
//		innerEarColourPanel = new LabelledDoubleColourPanel("Inner ear colour");
//		innerEarColourPanel.setDoubleColour(innerEarColour);
//		catPanel.add(innerEarColourPanel, "wrap");
//		
//		rightEyeColourPanel = new LabelledDoubleColourPanel("Right eye colour");
//		rightEyeColourPanel.setDoubleColour(rightEyeColour);
//		catPanel.add(rightEyeColourPanel, "wrap");
//		
//		leftEyeColourPanel = new LabelledDoubleColourPanel("Left eye colour");
//		leftEyeColourPanel.setDoubleColour(leftEyeColour);
//		catPanel.add(leftEyeColourPanel, "wrap");
//		
//		whiskerColourPanel = new LabelledDoubleColourPanel("Whisker colour");
//		whiskerColourPanel.setDoubleColour(whiskerColour);
//		catPanel.add(whiskerColourPanel, "wrap");
//		
//		
		
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
//
	/**
	 * called before rendering;
	 * override when adding fields
	 */
	@Override
	protected void acceptValuesInInteractiveControlPanel()
	{
		super.acceptValuesInInteractiveControlPanel();
//		//cat
//		centre = centrePanel.getVector3D();
//		frontDirection = frontDirectionPanel.getVector3D();
//		topDirection = topDirectionPanel.getVector3D();
		theta = MyMath.deg2rad(thetaPanel.getNumber());
		phi = MyMath.deg2rad(phiPanel.getNumber());
//		headColour =headColourPanel.getDoubleColour();
//		noseColour = noseColourPanel.getDoubleColour();
//		innerEarColour = innerEarColourPanel.getDoubleColour();
//		rightEyeColour = rightEyeColourPanel.getDoubleColour();
//		leftEyeColour = leftEyeColourPanel.getDoubleColour();
//		whiskerColour = whiskerColourPanel.getDoubleColour();
//		
		
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
		(new DerivativeControlSurfaceExplorerCOPY()).run();
	}
}
