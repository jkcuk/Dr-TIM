package optics.raytrace.test;

import java.io.PrintStream;

import javax.swing.JCheckBox;
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
import optics.raytrace.core.Studio;
import optics.raytrace.core.StudioInitialisationType;
import optics.raytrace.exceptions.SceneException;
import optics.raytrace.sceneObjects.RefractiveCylindricalLensTelescope;
import optics.raytrace.sceneObjects.TimHead;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;



public class SimpleSceneRefractionTestUE5 extends NonInteractiveTIMEngine
{
	private static final long serialVersionUID = 5021417995955905067L;

	/**
	 * Constructor.
	 * Sets all parameters.
	 */
	
	private double rotationAngle;
	
	private double height, width, frontFocalLength, backFocalLength, refractiveIndex;
	
	private double timDistance, timRadius;
	
	private boolean addTelescop1, addTelescop2;

	private StudioInitialisationType studioInitialisation;
	
	public SimpleSceneRefractionTestUE5()
	{
		super();
		//Telescope params
		height = 1;
		width = 1;
		
		frontFocalLength = 1;
		backFocalLength = 1;
		refractiveIndex = 1.5;
		
		addTelescop1 = true;
		addTelescop2 = true;
		
		//backdrop scene
		timDistance = 20;
		timRadius = 2;
		
		//directional params
		rotationAngle = 10;
		
		//camera and explorer
		renderQuality = RenderQualityEnum.DRAFT;
		studioInitialisation = StudioInitialisationType.MINIMALIST;
		
		nonInteractiveTIMAction = NonInteractiveTIMActionEnum.INTERACTIVE;
//		nonInteractiveTIMAction = NonInteractiveTIMActionEnum.MOVIE;
		cameraViewDirection = new Vector3D(0,0,1);
		cameraHorizontalFOVDeg = 20;
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


			// the scene
				SceneObjectContainer scene = new SceneObjectContainer("the scene", null, studio);
				studio.setScene(scene);
				
				// initialise the scene and lights
				StudioInitialisationType.initialiseSceneAndLights(
						studioInitialisation,
						scene,
						studio
					);
				
				scene.addSceneObject(new TimHead(
						"tim head",
						new Vector3D(0,0,timDistance),
						timRadius,
						new Vector3D(0, 0, -1),	// frontDirection
						new Vector3D(0, 1, 0), 	// topDirection
						new Vector3D(1, 0, 0),	// rightDirection
						scene,
						studio
						));
				
				//Adding two refractive cylindrical lens telescopes where one has the central axis rotated by 20 degrees.
				RefractiveCylindricalLensTelescope telescope1 = new RefractiveCylindricalLensTelescope(
				"front telescope",// description,
				height,// height,
				width,// width,
				Vector3D.O,// principalPoint,
				Vector3D.Z.getProductWith(-1),// normalisedOpticalAxisDirection,
				Vector3D.Y,// normalisedCylinderAxisDirection,
				frontFocalLength,// frontFocalLength,
				backFocalLength,// backFocalLength,
				refractiveIndex,// refractiveIndex,
				0.95,// surfaceTransmissionCoefficient,
				false,// shadowThrowing,
				scene,// parent,
				studio// studio
				);
				
				Vector3D rotatedCylindricalAxisDirection = Geometry.rotate(Vector3D.Y, Vector3D.Z, MyMath.deg2rad(rotationAngle));
				RefractiveCylindricalLensTelescope telescope2 = new RefractiveCylindricalLensTelescope(				
						"back telescope",// description,
						height,// height,
						width,// width,
						Vector3D.Z.getProductWith(1.5*refractiveIndex*frontFocalLength+backFocalLength),// principalPoint,
						Vector3D.Z,// normalisedOpticalAxisDirection,
						rotatedCylindricalAxisDirection,// normalisedCylinderAxisDirection,
						frontFocalLength,// frontFocalLength,
						backFocalLength,// backFocalLength,
						refractiveIndex,// refractiveIndex,
						0.95,// surfaceTransmissionCoefficient,
						false,// shadowThrowing,
						scene,// parent,
						studio// studio
						);
				
			scene.addSceneObject(telescope1, addTelescop1);
			scene.addSceneObject(telescope2, addTelescop2);
				
		studio.setCamera(getStandardCamera());
		studio.setScene(scene);
		
	}

	
	//scene stuff
	private LabelledDoublePanel rotationAnglePanel, heightPanel, widthPanel, frontFocalLengthPanel, backFocalLengthPanel, refractiveIndexPanel, timDistancePanel, timRadiusPanel;
	private JCheckBox addTelescop1CheckBox, addTelescop2CheckBox;
	private JComboBox<StudioInitialisationType> studioInitialisationComboBox;

	// camera stuff
	private LabelledDoublePanel cameraDistancePanel;
	private LabelledVector3DPanel cameraViewDirectionPanel;
	private DoublePanel cameraHorizontalFOVDegPanel;
	private JComboBox<ApertureSizeType> cameraApertureSizeComboBox;
	private LabelledDoublePanel cameraFocussingDistancePanel;



	
	/**
	 * add controls to the interactive control panel;
	 * override to modify
	 */
	@Override
	protected void createInteractiveControlPanel()
	{
		super.createInteractiveControlPanel();

		// the main tabbed pane, with "Scene" and "Camera" tabs
		JTabbedPane sceneCameraTabbedPane = new JTabbedPane();
		interactiveControlPanel.add(sceneCameraTabbedPane, "span");
		
		//
		// the Lenslet arrays panel
		//

		JPanel scenePanel = new JPanel();
		scenePanel.setLayout(new MigLayout("insets 0"));
		sceneCameraTabbedPane.addTab("Scene", scenePanel);
		
		heightPanel = new LabelledDoublePanel("height");
		heightPanel.setNumber(height);
		scenePanel.add(heightPanel, "span");
		
		widthPanel = new LabelledDoublePanel("width");
		widthPanel.setNumber(width);
		scenePanel.add(widthPanel, "span");
		
		frontFocalLengthPanel = new LabelledDoublePanel("Front focal length");
		frontFocalLengthPanel.setNumber(frontFocalLength);
		scenePanel.add(frontFocalLengthPanel, "span");
		
		backFocalLengthPanel = new LabelledDoublePanel("Back focal length");
		backFocalLengthPanel.setNumber(backFocalLength);
		scenePanel.add(backFocalLengthPanel, "span");
		
		refractiveIndexPanel= new LabelledDoublePanel("Refractive index");
		refractiveIndexPanel.setNumber(refractiveIndex);
		scenePanel.add(refractiveIndexPanel, "span");
			
		rotationAnglePanel = new LabelledDoublePanel("relative rotation angle");
		rotationAnglePanel.setNumber(rotationAngle);
		scenePanel.add(rotationAnglePanel, "span");
		
		addTelescop1CheckBox= new JCheckBox("Show first telescope");
		addTelescop1CheckBox.setSelected(addTelescop1);
		scenePanel.add(addTelescop1CheckBox, "span");
		
		
		addTelescop2CheckBox= new JCheckBox("Show second telescope");
		addTelescop2CheckBox.setSelected(addTelescop2);
		scenePanel.add(addTelescop2CheckBox, "span");
		
		timDistancePanel = new LabelledDoublePanel("tim distance");
		timDistancePanel.setNumber(timDistance);
		scenePanel.add(timDistancePanel, "span");
		
		timRadiusPanel = new LabelledDoublePanel("tim radius");
		timRadiusPanel.setNumber(timRadius);
		scenePanel.add(timRadiusPanel, "span");
		
		studioInitialisationComboBox = new JComboBox<StudioInitialisationType>(StudioInitialisationType.limitedValuesForBackgrounds);
		studioInitialisationComboBox.setSelectedItem(studioInitialisation);
		scenePanel.add(GUIBitsAndBobs.makeRow("Background", studioInitialisationComboBox), "span");
		
		
		//
		// the Camera panel
		//
		JPanel cameraPanel = new JPanel();
		cameraPanel.setLayout(new MigLayout("insets 0"));
		sceneCameraTabbedPane.addTab("Camera", cameraPanel);

		// camera stuff
		
//		cameraViewDirectionPanel = new LabelledVector3DPanel("View direction");
//		cameraViewDirectionPanel.setVector3D(cameraViewDirection);
//		cameraPanel.add(cameraViewDirectionPanel, "span");
		
		cameraDistancePanel = new LabelledDoublePanel("Camera distance");
		cameraDistancePanel.setNumber(cameraDistance);
		cameraPanel.add(cameraDistancePanel, "span");
		
		cameraViewDirectionPanel = new LabelledVector3DPanel("View direction");
		cameraViewDirectionPanel.setVector3D(cameraViewDirection);
		cameraPanel.add(cameraViewDirectionPanel, "span");
		
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
		
		rotationAngle = rotationAnglePanel.getNumber();
		height = heightPanel.getNumber();
		width = widthPanel.getNumber();
		frontFocalLength = frontFocalLengthPanel.getNumber();
		backFocalLength = backFocalLengthPanel.getNumber();
		refractiveIndex = refractiveIndexPanel.getNumber();
		addTelescop1 = addTelescop1CheckBox.isSelected();
		addTelescop2 = addTelescop2CheckBox.isSelected();
		timDistance = timDistancePanel.getNumber();
		timRadius = timRadiusPanel.getNumber();
		studioInitialisation = (StudioInitialisationType)(studioInitialisationComboBox.getSelectedItem());
		

		
		// cameraViewDirection = cameraViewDirectionPanel.getVector3D();
		cameraDistance = cameraDistancePanel.getNumber();
		cameraViewDirection = cameraViewDirectionPanel.getVector3D();
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
		(new SimpleSceneRefractionTestUE5()).run();
	}
}
