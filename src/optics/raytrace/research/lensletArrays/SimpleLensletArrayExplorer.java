package optics.raytrace.research.lensletArrays;

import java.awt.event.ActionListener;
import java.io.PrintStream;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import math.*;
import net.miginfocom.swing.MigLayout;
import optics.raytrace.sceneObjects.LensType;
import optics.raytrace.sceneObjects.TimHead;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.exceptions.SceneException;
import optics.raytrace.NonInteractiveTIMActionEnum;
import optics.raytrace.NonInteractiveTIMEngine;
import optics.raytrace.GUI.cameras.RenderQualityEnum;
import optics.raytrace.GUI.lowLevel.ApertureSizeType;
import optics.raytrace.GUI.lowLevel.DoublePanel;
import optics.raytrace.GUI.lowLevel.GUIBitsAndBobs;
import optics.raytrace.GUI.lowLevel.LabelledDoublePanel;
import optics.raytrace.GUI.lowLevel.LabelledVector3DPanel;
import optics.raytrace.GUI.sceneObjects.EditableRectangularLensletArray;
import optics.raytrace.core.Studio;
import optics.raytrace.core.StudioInitialisationType;


/**
 * Simulate the visual appearance of lenslet arrays, and the view through them.
 * 
 * @author Maik
 */
public class SimpleLensletArrayExplorer extends NonInteractiveTIMEngine implements ActionListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4968810668282196902L;

	/**
	 * lenslet side lengths
	 */
	private double sideLength;
	/**
	 * focal length of lenslet array 1 (closer to camer)
	 */
	private double f1;
	
	/**
	 * focal length of lenslet array 2 (closer to object)
	 */
	private double f2;
	
	/**
	 * period of lenslet array 1
	 */
	private double period1;

	/**
	 * delta of the period such that deltaPeriod = period2 - period1
	 */
	private double deltaPeriod;
	
	/**
	 * The offset from being confocal such that the separation is simply f1+f2+offset
	 */
	private double offset;
	
	/**
	 * rotation angle of LA1 around its normal
	 */
	private double phi1Deg;

	/**
	 * rotation angle of LA2 around its normal
	 */
	private double phi2Deg;
	
	/**
	 * show lenslet array 1
	 */
	private boolean showLensletArray1;

	/**
	 * show lenslet array 2
	 */
	private boolean showLensletArray2;
	
	
	private boolean shadowThrowing;

	/**
	 * if true, diffractive blur will be simulated
	 */
	private boolean simulateDiffractiveBlur;
	
	/**
	 * wavelength for which diffractive blur is simulated, in nm
	 */
	private double lambdaNM;

	//
	// the rest of the scene
	//
	
	/**
	 * Add tim head
	 */
	private boolean addTimHead;
	
	/**
	 * Tim head centre
	 */
	private Vector3D timHeadCentre;
	
	/**
	 * Tim radius 
	 */
	private double timRadius;
	
	/**
	 * Determines how to initialise the backdrop
	 */
	private StudioInitialisationType studioInitialisation;
	
	
	/**
	 * Constructor.
	 * Sets all parameters.
	 */
	public SimpleLensletArrayExplorer()
	{
		super();
		
		// set to true for interactive version
		nonInteractiveTIMAction = NonInteractiveTIMActionEnum.INTERACTIVE;
		
		// set all parameters
		
		renderQuality = RenderQualityEnum.DRAFT;
		
		// lenslet-array parameters
		sideLength = 1;
		f1 = 0.1;
		f2 = -0.05;
		period1 = 0.04;
		deltaPeriod = 0;
		phi1Deg = 0;
		phi2Deg = 0;
		offset = 0;
		shadowThrowing = false;
		showLensletArray1 = true;
		showLensletArray2 = true;
		simulateDiffractiveBlur = false;
		lambdaNM = 632.8;
		
		studioInitialisation = StudioInitialisationType.TIM_HEAD;	// the backdrop
		addTimHead = false;;
		timHeadCentre = new Vector3D(0,0,10);
		timRadius = 1;

		// camera
		cameraViewCentre = new Vector3D(0, 0, 0);
		cameraDistance = 1.7;
		cameraViewDirection = new Vector3D(0, 0, 1);
		cameraHorizontalFOVDeg = 50;
		cameraApertureSize = ApertureSizeType.PINHOLE;
		cameraFocussingDistance = 10;
		
		if(nonInteractiveTIMAction == NonInteractiveTIMActionEnum.INTERACTIVE)
		{
			windowTitle = "Dr TIM's simple lenslet-array explorer";
			windowWidth = 1500;
			windowHeight = 650;
		}
	}
	

	@Override
	public String getClassName()
	{
		return "LensletArrayExplorer"	// the name
				;
	}
	
	@Override
	public void writeParameters(PrintStream printStream)
	{
		// write any parameters not defined in NonInteractiveTIMEngine
		printStream.println("sideLength="+sideLength);
		printStream.println("f1="+f1);
		printStream.println("f2="+f2);
		printStream.println("period1="+period1);
		printStream.println("delta period="+deltaPeriod);
		printStream.println("offset="+offset);
		printStream.println("phi1Deg="+phi1Deg);
		printStream.println("phi2Deg="+phi2Deg);
		printStream.println("showLensletArray1="+showLensletArray1);
		printStream.println("showLensletArray2="+showLensletArray2);
		printStream.println("shadowThrowing="+shadowThrowing);
		printStream.println("simulateDiffractiveBlur="+simulateDiffractiveBlur);
		printStream.println("lambdaNM="+lambdaNM);
		printStream.println("studioInitialisation="+studioInitialisation);
		printStream.println("addTimHead="+addTimHead);
		if(addTimHead) printStream.println("timHeadCentre="+timHeadCentre);
		if(addTimHead) printStream.println("timRadius="+timRadius);

		printStream.println();

		// write all parameters defined in NonInteractiveTIMEngine
		super.writeParameters(printStream);		
	}

		
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
				timHeadCentre,
				timRadius,
				new Vector3D(0, 0, -1),	// frontDirection
				new Vector3D(0, 1, 0), 	// topDirection
				new Vector3D(1, 0, 0),	// rightDirection
				scene,
				studio
				),
				addTimHead);
		
		double zSeparation = f1+f2+offset;
		
		Vector3D up = new Vector3D(0, 1, 0);
		Vector3D normal1 = new Vector3D(0, 0, 1);
		Vector3D right1 = Vector3D.crossProduct(normal1, up);
		double phi1 = MyMath.deg2rad(phi1Deg);
		double c1 = Math.cos(phi1);
		double s1 = Math.sin(phi1);
		
		scene.addSceneObject(new EditableRectangularLensletArray(
				"LA1",	// description
				new Vector3D(0, 0, -zSeparation/2),	// centre
				Vector3D.sum(right1.getProductWith(c1), up.getProductWith(s1)).getWithLength(sideLength),	// spanVector1
				Vector3D.sum(right1.getProductWith(-s1), up.getProductWith(c1)).getWithLength(sideLength),	// spanVector2
				f1,	// focalLength
				period1,	// xPeriod
				period1,	// yPeriod
				0,	// xOffset
				0,	// yOffset
				LensType.IDEAL_THIN_LENS,	// lensType
				simulateDiffractiveBlur,
				lambdaNM*1e-9,
				0.96,	// throughputCoefficient
				false,	// reflective
				shadowThrowing,	// shadowThrowing
				scene,	// parent
				studio
			), 
			showLensletArray1
		);

		double phi2 = MyMath.deg2rad(phi2Deg);
		Vector3D normal2 = new Vector3D(0, 0, 1);
		Vector3D right2 = Vector3D.crossProduct(normal2, up);
		double c2 = Math.cos(phi2);
		double s2 = Math.sin(phi2);
		
		scene.addSceneObject(new EditableRectangularLensletArray(
				"LA2",	// description
				new Vector3D(0, 0, zSeparation/2),	// centre
				Vector3D.sum(right2.getProductWith(c2), up.getProductWith(s2)).getWithLength(sideLength),	// spanVector1
				Vector3D.sum(right2.getProductWith(-s2), up.getProductWith(c2)).getWithLength(sideLength),	// spanVector2
				f2,	// focalLength
				period1 + deltaPeriod,	// xPeriod
				period1+ deltaPeriod,	// yPeriod
				0,	// xOffset
				0,	// yOffset
				LensType.IDEAL_THIN_LENS,
				simulateDiffractiveBlur,
				lambdaNM*1e-9,
				0.96,	// throughputCoefficient
				false,	// reflective
				shadowThrowing,	// shadowThrowing
				scene,	// parent
				studio
			), 
			showLensletArray2
		);
		
		// the camera
		// cameraFocussingDistance

		studio.setCamera(getStandardCamera());
	}

	

	//Lenslet stuff
	private LabelledDoublePanel f1Panel, f2Panel, period1Panel, deltaPeriodPanel, sideLengthPanel;
	private DoublePanel phi1DegPanel, phi2DegPanel, offsetPanel, lambdaNMPanel, timRadiusPanel; // , 
	private LabelledVector3DPanel timHeadCentrePanel;
	private JCheckBox showLensletArray1CheckBox, showLensletArray2CheckBox, simulateDiffractiveBlurCheckBox, addTimHeadCheckBox;
	private JComboBox<StudioInitialisationType> studioInitialisationComboBox;

	// camera stuff
	// private LabelledVector3DPanel cameraViewDirectionPanel;
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

		//
		// the Lenslet-arrays-initialisation panel
		//

		//
		// the LA1 panel
		//
		
		JPanel la1Panel = new JPanel();
		la1Panel.setBorder(GUIBitsAndBobs.getTitledBorder("Ocular lenslet array"));
		la1Panel.setLayout(new MigLayout("insets 0"));
		// scenePanel.add(la1Panel, "span");

		f1Panel = new LabelledDoublePanel("f");
		f1Panel.setNumber(f1);
		f1Panel.setToolTipText("Focal length of the lenslets");
		// la1Panel.add(f1Panel, "span");

		showLensletArray1CheckBox = new JCheckBox("Show");
		showLensletArray1CheckBox.setSelected(showLensletArray1);
		la1Panel.add(GUIBitsAndBobs.makeRow(f1Panel,  showLensletArray1CheckBox), "span");
		
		period1Panel = new LabelledDoublePanel("Period");
		period1Panel.setNumber(period1);
		period1Panel.setToolTipText("Period, i.e. distance between neighbouring lenslets");
		la1Panel.add(period1Panel, "span");
		
		phi1DegPanel = new DoublePanel();
		phi1DegPanel.setNumber(phi1Deg);
		phi1DegPanel.setToolTipText("Angle by which the array is rotated around the array normal");
		la1Panel.add(GUIBitsAndBobs.makeRow("phi", phi1DegPanel, "degrees"), "span");


		//
		// the LA2 panel
		//
		
		JPanel la2Panel = new JPanel();
		la2Panel.setBorder(GUIBitsAndBobs.getTitledBorder("Objective lenslet array"));
		la2Panel.setLayout(new MigLayout("insets 0"));
		// scenePanel.add(la2Panel, "span");
		
		f2Panel = new LabelledDoublePanel("f");
		f2Panel.setNumber(f2);
		f2Panel.setToolTipText("Focal length of the lenslets");
		// la2Panel.add(f2Panel, "span");
		
		showLensletArray2CheckBox = new JCheckBox("Show");
		showLensletArray2CheckBox.setSelected(showLensletArray2);
		// la2Panel.add(showLensletArray2CheckBox, "span");
		la2Panel.add(GUIBitsAndBobs.makeRow(f2Panel,  showLensletArray2CheckBox), "span");

		deltaPeriodPanel = new LabelledDoublePanel("\u0394 Period");
		deltaPeriodPanel.setNumber(deltaPeriod);
		deltaPeriodPanel.setToolTipText("\u0394 period such that \u0394 = period2 - period1");
		la2Panel.add(deltaPeriodPanel, "span");

		phi2DegPanel = new DoublePanel();
		phi2DegPanel.setNumber(phi2Deg);
		phi2DegPanel.setToolTipText("Angle by which the array is rotated around the array normal");
		la2Panel.add(GUIBitsAndBobs.makeRow("phi", phi2DegPanel, "degrees"), "span");

		// add the LA panels
		scenePanel.add(GUIBitsAndBobs.makeRow(la1Panel, la2Panel), "span");


		//
		// common LA parameters panel
		//
		
		JPanel commonLAParametersPanel = new JPanel();
		commonLAParametersPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Common LA parameters"));
		commonLAParametersPanel.setLayout(new MigLayout("insets 0"));
		scenePanel.add(commonLAParametersPanel, "span");
		
		sideLengthPanel = new LabelledDoublePanel("Lenslet array side length");
		sideLengthPanel.setNumber(sideLength);
		commonLAParametersPanel.add(sideLengthPanel, "span");
		
		// the offset between the two lenslet arrays
		offsetPanel = new DoublePanel();
		offsetPanel.setNumber(offset);
		offsetPanel.setToolTipText("Additional offset between the lenslet arrays such that their separation is f1+f2+offset");
		commonLAParametersPanel.add(GUIBitsAndBobs.makeRow("offset", offsetPanel), "span");
		
		simulateDiffractiveBlurCheckBox = new JCheckBox("");
		simulateDiffractiveBlurCheckBox.setSelected(simulateDiffractiveBlur);
		lambdaNMPanel = new DoublePanel();
		lambdaNMPanel.setNumber(lambdaNM);
		commonLAParametersPanel.add(GUIBitsAndBobs.makeRow(simulateDiffractiveBlurCheckBox, "Simulate diffractive blur for wavelength", lambdaNMPanel, "nm"), "span");
		
		
		//
		// rest-of-the-scene panel
		//
		JPanel restOfScenePanel = new JPanel();
		restOfScenePanel.setBorder(GUIBitsAndBobs.getTitledBorder("Rest of scene"));
		restOfScenePanel.setLayout(new MigLayout("insets 0"));
		scenePanel.add(restOfScenePanel, "span");

		studioInitialisationComboBox = new JComboBox<StudioInitialisationType>(StudioInitialisationType.limitedValuesForBackgrounds);
		studioInitialisationComboBox.setSelectedItem(studioInitialisation);
		restOfScenePanel.add(GUIBitsAndBobs.makeRow("Background", studioInitialisationComboBox), "span");
		
		addTimHeadCheckBox = new JCheckBox("Add editable tim head");
		addTimHeadCheckBox.setSelected(addTimHead);
		restOfScenePanel.add(addTimHeadCheckBox, "span");
		
		timHeadCentrePanel = new LabelledVector3DPanel("Tim head centre");
		timHeadCentrePanel.setVector3D(timHeadCentre);
		restOfScenePanel.add(timHeadCentrePanel,"span");
		
		timRadiusPanel = new DoublePanel();
		timRadiusPanel.setNumber(timRadius);
		restOfScenePanel.add(GUIBitsAndBobs.makeRow("Tim head radius", timRadiusPanel), "span");
		
		
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
		
		sideLength = sideLengthPanel.getNumber();
		f1 = f1Panel.getNumber();
		f2 = f2Panel.getNumber();
		period1 = period1Panel.getNumber();
		deltaPeriod = deltaPeriodPanel.getNumber();
		phi1Deg = phi1DegPanel.getNumber();
		phi2Deg = phi2DegPanel.getNumber();
		offset = offsetPanel.getNumber();
		
		showLensletArray1 = showLensletArray1CheckBox.isSelected();
		showLensletArray2 = showLensletArray2CheckBox.isSelected();
		simulateDiffractiveBlur = simulateDiffractiveBlurCheckBox.isSelected();
		lambdaNM = lambdaNMPanel.getNumber();
		studioInitialisation = (StudioInitialisationType)(studioInitialisationComboBox.getSelectedItem());
		addTimHead = addTimHeadCheckBox.isSelected();
		timHeadCentre = timHeadCentrePanel.getVector3D();
		timRadius = timRadiusPanel.getNumber();
		
		// cameraViewDirection = cameraViewDirectionPanel.getVector3D();
		cameraDistance = cameraDistancePanel.getNumber();
		cameraViewDirection = cameraViewDirectionPanel.getVector3D();
		cameraHorizontalFOVDeg = cameraHorizontalFOVDegPanel.getNumber();
		cameraApertureSize = (ApertureSizeType)(cameraApertureSizeComboBox.getSelectedItem());
		cameraFocussingDistance = cameraFocussingDistancePanel.getNumber();
	}
	

	//
	// the main method, so that this can be run as a Java application
	//

	/**
	 * Called when this is run; don't touch!
	 * @param args
	 */
	public static void main(final String[] args)
	{
		(new SimpleLensletArrayExplorer()).run();
	}
}
