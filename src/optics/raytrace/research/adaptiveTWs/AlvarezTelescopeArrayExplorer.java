package optics.raytrace.research.adaptiveTWs;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintStream;
import java.text.NumberFormat;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

import math.*;
import net.miginfocom.swing.MigLayout;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.surfaces.PhaseHologramOfRectangularLensletArray;
import optics.raytrace.surfaces.PhaseHologramOfRectangularAlvarezLensPartArray;
import optics.raytrace.exceptions.SceneException;
import optics.raytrace.NonInteractiveTIMActionEnum;
import optics.raytrace.NonInteractiveTIMEngine;
import optics.raytrace.GUI.cameras.RenderQualityEnum;
import optics.raytrace.GUI.lowLevel.ApertureSizeType;
import optics.raytrace.GUI.lowLevel.DoublePanel;
import optics.raytrace.GUI.lowLevel.GUIBitsAndBobs;
import optics.raytrace.GUI.lowLevel.LabelledDoublePanel;
import optics.raytrace.GUI.lowLevel.LabelledVector2DPanel;
import optics.raytrace.GUI.lowLevel.LabelledVector3DPanel;
import optics.raytrace.GUI.sceneObjects.EditableScaledParametrisedCentredParallelogram;
import optics.raytrace.core.Studio;
import optics.raytrace.core.StudioInitialisationType;


/**
 * Simulate the visual appearance of an array of Alvarez telescopes, i.e. a combination of two Alvarez lens arrays in which the central parts move together.
 * 
 * @author Johannes Courtial
 */
public class AlvarezTelescopeArrayExplorer extends NonInteractiveTIMEngine implements ActionListener
{
	/**
	 * pP1 = P'_1, i.e. focal Power / delta1 of Alvarez lens array 1, which is the farther array
	 */
	private double pP1;

	/**
	 * pP2 = P'_2, i.e. focal Power / delta2 of Alvarez lens array 2, which is the closer array
	 */
	private double pP2;
		
	/**
	 * z coordinate of Alvarez lens array 1
	 */
	private double z1;

	/**
	 * z coordinate of Alvarez lens array 2
	 */
	private double z2;
	
	/**
	 * offset (in the x direction) between the parts of Alvarez lens 1 in the reference configuration (deltaX = 0)
	 */
	private double delta01;

	/**
	 * offset (in the x direction) between the parts of Alvarez lens 2 in the reference configuration (deltaX = 0)
	 */
	private double delta02;

	/**
	 * offset in the x direction of the middle, "meat", part of the telescope from the reference configuration;
	 * note that this middle part comprises the "inner" parts of both Alvarez-lens arrays
	 */
	private double deltaX;

	/**
	 * offset in the y direction of the middle, "meat", part of the telescope from the reference configuration;
	 * note that this middle part comprises the "inner" parts of both Alvarez lenses
	 */
	private double deltaY;
	
	/**
	 * rotation angle of the middle part of the Alvarez telescope around the optical axis
	 */
	private double deltaPhiDeg;
	
	/**
	 * period of all arrays
	 */
	private double period;
	
	/**
	 * if true, simulate diffractive blur for light of wavelength lambdaNM
	 */
	private boolean simulateDiffractiveBlur;
	
	/**
	 * wavelength, in nm, of light for which diffractive blur is simulated if simulateDiffractiveBlur = true
	 */
	private double lambdaNM;
	
	/**
	 * z separation of parts
	 */
	private double dz;

	/**
	 * show different parts
	 */
	private boolean
		showLensArray1Part1,
		showLensArray1Part2,
		showLensArray2Part1,
		showLensArray2Part2,
		showLensArray1Equivalent,
		showLensArray2Equivalent;

	private double eta;
	
	// private double t;
	
	//
	// the rest of the scene
	//
	
	/**
	 * Determines how to initialise the backdrop
	 */
	private StudioInitialisationType studioInitialisation;
	
	
	/**
	 * Constructor.
	 * Sets all parameters.
	 */
	public AlvarezTelescopeArrayExplorer()
	{
		super();
		
		// set to true for interactive version
		nonInteractiveTIMAction = NonInteractiveTIMActionEnum.INTERACTIVE;
		
		// set all parameters
		
		renderQuality = RenderQualityEnum.DRAFT;
		
		// parameters
		pP1 = 20000;
		pP2 = 20000;
		delta01 = 0.001;
		delta02 = 0.001;
		deltaX = 0;
		deltaY = 0;
		deltaPhiDeg = 0;
		z1 = 5.1;
		z2 = 5;
		period = 0.01;
		simulateDiffractiveBlur = false;
		lambdaNM = 632.8;
		dz = 0.00001;
		showLensArray1Part1 = true;
		showLensArray1Part2 = true;
		showLensArray2Part1 = true;
		showLensArray2Part2 = true;
		showLensArray1Equivalent = false;
		showLensArray2Equivalent = false;
		eta = -1;
		// t = 0.1;
		
		studioInitialisation = StudioInitialisationType.TIM_HEAD;	// the backdrop

		// camera
		cameraViewCentre = new Vector3D(0, 0, 0);
		cameraDistance = 10;
		cameraViewDirection = new Vector3D(0, 0, 1);
		cameraHorizontalFOVDeg = 12;
		cameraApertureSize = ApertureSizeType.PINHOLE;
		cameraFocussingDistance = 10;
		
		if(nonInteractiveTIMAction == NonInteractiveTIMActionEnum.INTERACTIVE)
		{
			windowTitle = "Dr TIM's Alvarez-telescope-array explorer";
			windowWidth = 1600;
			windowHeight = 850;
		}
	}
	

	@Override
	public String getFirstPartOfFilename()
	{
		return "AlvarezTelescopeArrayExplorer"	// the name
//				+ " a1="+focalPowerOverDeltaX1
//				+ " a2="+focalPowerOverDeltaX2
//				+ " t="+tubeLength
//				+ " dx="+deltaX
//				+ " dy="+deltaY
//				+ " dPhi="+deltaPhiDeg
//				+ (showLens1Part1?" L1P1":"")
//				+ (showLens1Part2?" L1P2":"")
//				+ (showLens2Part1?" L2P1":"")
//				+ (showLens2Part2?" L2P2":"")
//				+ " dz="+dz
//				+ " backdrop="+studioInitialisation.toString()
//				+ " cD="+cameraDistance
//				+ " cVD="+cameraViewDirection
//				+ " cFOV="+cameraHorizontalFOVDeg
//				+ " cAS="+cameraApertureSize
//				+ " cFD="+cameraFocussingDistance
				;
	}
		
	@Override
	public void writeParameters(PrintStream printStream)
	{
		// write any parameters not defined in NonInteractiveTIMEngine
		
		printStream.println("focalPowerOverDeltaX1="+pP1);
		printStream.println("focalPowerOverDeltaX2="+pP2);
		printStream.println("z1="+z1);
		printStream.println("z2="+z2);
		printStream.println("delta01="+delta01);
		printStream.println("delta02="+delta02);
		printStream.println("deltaX="+deltaX);
		printStream.println("deltaY="+deltaY);
		printStream.println("deltaPhiDeg="+deltaPhiDeg);
		printStream.println("period="+period);
//		printStream.println("xOffset="+xOffset);
//		printStream.println("yOffset="+yOffset);
		printStream.println("dz="+dz);
		printStream.println("showLensArray1Part1="+showLensArray1Part1);
		printStream.println("showLensArray1Part2="+showLensArray1Part2);
		printStream.println("showLensArray2Part1="+showLensArray2Part1);
		printStream.println("showLensArray2Part2="+showLensArray2Part2);
		printStream.println("showLensArray1Equivalent="+showLensArray1Equivalent);
		printStream.println("showLensArray2Equivalent="+showLensArray2Equivalent);
		printStream.println("simulateDiffractiveBlur="+simulateDiffractiveBlur);
		printStream.println("lambdaNM="+lambdaNM);
		printStream.println("eta="+eta);
		// printStream.println("t="+t);
		printStream.println("studioInitialisation="+studioInitialisation);

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
		
		Vector3D up = new Vector3D(0, 1, 0);
		double zSeparation = dz;
		
		double deltaPhi = MyMath.deg2rad(deltaPhiDeg);
		Vector3D normal = new Vector3D(0, 0, 1);
		Vector3D right = Vector3D.crossProduct(normal, up).getNormalised();
		
		// Alvarez-lens array 1
		
		Vector3D centreLens1 = new Vector3D(0, 0, z1);
		Vector3D centreLens1Part1 = Vector3D.sum(
				centreLens1,
				new Vector3D(
						-0.5*delta01,	// 0,
						0,	// 0,
						-0.5*zSeparation
					));
		Vector3D xHat1 = right;
		Vector3D yHat1 = up;
		
		scene.addSceneObject(new EditableScaledParametrisedCentredParallelogram(
				"Alvarez-lens array 1 part 1",	// description
				centreLens1Part1,	// centre
				xHat1,	// spanVector1
				yHat1,	// spanVector2
				new PhaseHologramOfRectangularAlvarezLensPartArray(
						centreLens1Part1,	// principalPoint
						xHat1,	// xHat
						yHat1,	// yHat
						pP1,
						period,	// uPeriod
						period,	// vPeriod
						0,	// 0.5*delta0,	// uOffset
						0,	// vOffset
						simulateDiffractiveBlur,
						lambdaNM * 1e-9,
						0.96,	// throughputCoefficient
						true	// shadowThrowing
					),	// surfaceProperty
				scene,	// parent
				studio
			),
			showLensArray1Part1 && !showLensArray1Equivalent
		);

		double cInner = Math.cos(deltaPhi);
		double sInner = Math.sin(deltaPhi);
		Vector3D xHatInner = Vector3D.sum(right.getProductWith(cInner), up.getProductWith(sInner));
		Vector3D yHatInner = Vector3D.sum(right.getProductWith(-sInner), up.getProductWith(cInner));
		
		Vector3D centreLens1Part2 = Vector3D.sum(
				centreLens1,
				new Vector3D(
						+0.5*delta01 + deltaX,	// deltaX,
						deltaY,	// deltaY,
						+0.5*zSeparation
					));
		scene.addSceneObject(new EditableScaledParametrisedCentredParallelogram(
				"Alvarez lens array 1 part 2",	// description
				centreLens1Part2,	// centre
				xHatInner,	// spanVector1
				yHatInner,	// spanVector2
				new PhaseHologramOfRectangularAlvarezLensPartArray(
						centreLens1Part2,	// principalPoint
						xHatInner,	// xHat
						yHatInner,	// yHat
						-pP1,
						period,	// uPeriod
						period,	// vPeriod
						0,	// -0.5*delta0,	// uOffset
						0,	// vOffset
						simulateDiffractiveBlur,
						lambdaNM * 1e-9,
						0.96,	// throughputCoefficient
						true	// shadowThrowing
					),	// surfaceProperty
				scene,	// parent
				studio
			),
			showLensArray1Part2 && !showLensArray1Equivalent
		);

		scene.addSceneObject(new EditableScaledParametrisedCentredParallelogram(
				"Lens array equivalent to Alvarez lens array 1",	// description
				centreLens1,	// centre
				xHat1,	// spanVector1
				yHat1,	// spanVector2
				new PhaseHologramOfRectangularLensletArray(
// 				new RectangularIdealThinLensletArraySimple(
						centreLens1,	// principalPoint
						xHat1,	// xHat
						yHat1,	// yHat
						calculateF1(),	// focalLength
						period,	// uPeriod
						period,	// vPeriod
						0,	// uOffset
						0,	// vOffset
						simulateDiffractiveBlur,
						lambdaNM * 1e-9,
						0.96,	// throughputCoefficient
						false,	// reflective (used only by PhaseHologramOfRectangularLensletArray)						
						true	// shadowThrowing
					),	// surfaceProperty
				scene,	// parent
				studio
			),
			showLensArray1Equivalent
		);


		// Alvarez-lens array 2
		
		Vector3D centreLens2 = new Vector3D(0, 0, z2);
		Vector3D centreLens2Part1 = 
				Vector3D.sum(
						centreLens2, 
						new Vector3D(
								-0.5*delta02 + deltaX ,	// deltaX
								deltaY,	// deltaY
								-0.5*zSeparation
							)
					);
		
		scene.addSceneObject(new EditableScaledParametrisedCentredParallelogram(
				"Alvarez-lens array 2 part 1",	// description
				centreLens2Part1,	// centre
				xHatInner,	// spanVector1
				yHatInner,	// spanVector2
				new PhaseHologramOfRectangularAlvarezLensPartArray(
						centreLens2Part1,	// principalPoint
						xHatInner,	// xHat
						yHatInner,	// yHat
						pP2,
						period,	// uPeriod
						period,	// vPeriod
						0,	// 0.5*delta0,	// uOffset
						0,	// vOffset
						simulateDiffractiveBlur,
						lambdaNM * 1e-9,
						0.96,	// throughputCoefficient
						true	// shadowThrowing
					),	// surfaceProperty
				scene,	// parent
				studio
			),
			showLensArray2Part1 && !showLensArray2Equivalent
		);
		
		Vector3D centreLens2Part2 = Vector3D.sum(
				centreLens2,
				new Vector3D(
						+0.5*delta02,	// 0,
						0,	// 0,
						+0.5*zSeparation
					));
		scene.addSceneObject(new EditableScaledParametrisedCentredParallelogram(
				"Alvarez-lens array 2 part 2",	// description
				centreLens2Part2,	// centre
				xHat1,	// spanVector1
				yHat1,	// spanVector2
				new PhaseHologramOfRectangularAlvarezLensPartArray(
						centreLens2Part2,	// principalPoint
						xHat1,	// xHat
						yHat1,	// yHat
						-pP2,
						period,	// uPeriod
						period,	// vPeriod
						0,	// -0.5*delta0,	// uOffset
						0,	// vOffset
						simulateDiffractiveBlur,
						lambdaNM * 1e-9,
						0.96,	// throughputCoefficient
						true	// shadowThrowing
					),	// surfaceProperty
				scene,	// parent
				studio
			),
			showLensArray2Part2 && !showLensArray2Equivalent
		);
		
		scene.addSceneObject(new EditableScaledParametrisedCentredParallelogram(
				"Lens array equivalent to Alvarez-lens array 2",	// description
				centreLens2,	// centre
				xHat1,	// spanVector1
				yHat1,	// spanVector2
				new PhaseHologramOfRectangularLensletArray(
//				new RectangularIdealThinLensletArraySimple(
						centreLens2,	// principalPoint
						xHat1,	// xHat
						yHat1,	// yHat
						calculateF2(),	// focalLength
						period,	// uPeriod
						period,	// vPeriod
						0,	// uOffset
						0,	// vOffset
						simulateDiffractiveBlur,
						lambdaNM * 1e-9,
						0.96,	// throughputCoefficient
						false,	// reflective (used only by PhaseHologramOfRectangularLensletArray)
						true	// shadowThrowing
					),	// surfaceProperty
				scene,	// parent
				studio
			),
			showLensArray2Equivalent
		);


		// the camera
		cameraViewCentre = centreLens2;
		studio.setCamera(getStandardCamera());
	}
		
	public double calculateF1()
	{
		// return 1/(pP1*(delta0 + deltaX));
		return 1./(pP1*(delta01 + deltaX));
	}

	public double calculateF2()
	{
		// return 1/(pP2*(delta0 - deltaX));
		return 1./(pP2*(delta02 - deltaX));
	}
	
		
//	public double calculateEquivalentFocalLength()
//	{
//		return 1/(focalPowerOverDeltaX*deltaX);
//	}

	
	
	//
	// for interactive version
	//
		
	private LabelledDoublePanel pP1Panel, pP2Panel, z1Panel, z2Panel, dzPanel, delta01Panel, delta02Panel, periodPanel, etaPanel;	// , tPanel;
	private LabelledVector2DPanel meatComponentXYPanel;
	private DoublePanel deltaPhiDegPanel, lambdaNMPanel;
	private JCheckBox showLensArray1Part1CheckBox, showLensArray1Part2CheckBox, showLensArray2Part1CheckBox, showLensArray2Part2CheckBox, simulateDiffractiveBlurCheckBox,
		showLensArray1EquivalentCheckBox, showLensArray2EquivalentCheckBox;
	private JComboBox<StudioInitialisationType> studioInitialisationComboBox;
	private JButton updateInfoButton, initpP12Button, // initpP2Delta0Button,
		setToReferenceConfigurationButton; // calculateF1Button, calculateF2Button, initButton, fromA1AndTCalculateRestButton;
	private JTextArea infoTextArea;

	// camera stuff
	// private LabelledVector3DPanel cameraViewDirectionPanel;
	private LabelledDoublePanel cameraDistancePanel;
	private LabelledVector3DPanel cameraViewDirectionPanel;
	private DoublePanel cameraHorizontalFOVDegPanel;
	private JComboBox<ApertureSizeType> cameraApertureSizeComboBox;
	private LabelledDoublePanel cameraFocussingDistancePanel;
	private JButton focusCameraButton;

	public enum ParametersInitialisationType
	{
		INIT("Initialise parameters..."),
		JUST_TIM("No lens arrays, camera focussed on TIM"),
		ETA2("eta=2, camera focussed on image of TIM"),
		ETA4("eta=4, camera focussed on image of TIM"),
		ETA_1("eta=-1, camera focussed on image of TIM"),
		ETA_2("eta=-2, camera focussed on image of TIM");
//		SIMULATE_DIFFRACTIVE_BLUR_550("Simulate diffractive blur for lambda = 550nm"),
//		NO_DIFFRACTIVE_BLUR("Don't simulate diffractive blur");
//		MAGNIFYING_LARGE_LENSES_FOCUS_ON_TIM("Magnifying, large lenses, camera focussed on image of TIM"),
//		MAGNIFYING_SMALL_LENSES_FOCUS_ON_TIM("Magnifying, small lenses, camera focussed on image of TIM"),
//		DEMAGNIFYING_LARGE_LENSES_FOCUS_ON_TIM("De-magnifying, large lenses, camera focussed on image of TIM"),
//		DEMAGNIFYING_SMALL_LENSES_FOCUS_ON_TIM("De-magnifying, small lenses, camera focussed on image of TIM"),
//		SHOW_LAS_AND_TIM("Initialise camera parameters to show lenses and TIM"),
//		SHOW_LAS_FROM_ABOVE("Initialise camera parameters to show LAs from above");
		
		private String description;
		private ParametersInitialisationType(String description) {this.description = description;}	
		@Override
		public String toString() {return description;}
	}
	
	private JComboBox<ParametersInitialisationType> parametersInitialisationComboBox;


	
	/**
	 * add controls to the interactive control panel;
	 * override to modify
	 */
	@Override
	protected void createInteractiveControlPanel()
	{
		super.createInteractiveControlPanel();
		
		parametersInitialisationComboBox = new JComboBox<ParametersInitialisationType>(ParametersInitialisationType.values());
		parametersInitialisationComboBox.setSelectedItem(ParametersInitialisationType.INIT);
		parametersInitialisationComboBox.addActionListener(this);
		interactiveControlPanel.add(parametersInitialisationComboBox, "span");


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
		// the Alvarez-lens-part-array initialisation panel
		//

		// Alvarez-lens array 1
		
		JPanel AlvarezLensArray1Panel = new JPanel();
		AlvarezLensArray1Panel.setLayout(new MigLayout("insets 0"));
		AlvarezLensArray1Panel.setBorder(GUIBitsAndBobs.getTitledBorder("Alvarez-lens array 1 (farther from camera)"));
		scenePanel.add(AlvarezLensArray1Panel, "span");
		
		pP1Panel = new LabelledDoublePanel("P'");
		pP1Panel.setNumber(pP1);
		// focalPowerOverDeltaX1Panel.addDocumentListener(this);
		pP1Panel.setToolTipText("Focal power / delta_x for Alvarez-lens array 1");
		// ALLensArray1Panel.add(pP1Panel, "span");
		
		z1Panel = new LabelledDoublePanel("z");
		z1Panel.setNumber(z1);
		z1Panel.setToolTipText("z coordinate of Alvarez-lens array 1");
		AlvarezLensArray1Panel.add(GUIBitsAndBobs.makeRow(pP1Panel, z1Panel), "span");

		delta01Panel = new LabelledDoublePanel("delta_0");
		delta01Panel.setNumber(delta01);
		delta01Panel.setToolTipText("Offset (in the x direction) between the two parts of Alvarez-lens array 1 in the reference configuration (delta_x = 0)");
		AlvarezLensArray1Panel.add(delta01Panel, "span");
		
		showLensArray1Part1CheckBox = new JCheckBox("part 1");
		showLensArray1Part1CheckBox.setSelected(showLensArray1Part1);

		showLensArray1Part2CheckBox = new JCheckBox("part 2");
		showLensArray1Part2CheckBox.setSelected(showLensArray1Part2);

		// ALLensArray1Panel.add(GUIBitsAndBobs.makeRow("Show", showLens1Part1CheckBox, showLens1Part2CheckBox), "wrap");
		
		// equivalent lens array
		
		showLensArray1EquivalentCheckBox = new JCheckBox("OR equivalent lens array");
		showLensArray1EquivalentCheckBox.setSelected(showLensArray1Equivalent);
		showLensArray1EquivalentCheckBox.addActionListener(this);
		AlvarezLensArray1Panel.add(GUIBitsAndBobs.makeRow("Show", showLensArray1Part1CheckBox, showLensArray1Part2CheckBox, showLensArray1EquivalentCheckBox), "wrap");

		
		// Alvarez-lens array 2
		
		JPanel AlvarezLensArray2Panel = new JPanel();
		AlvarezLensArray2Panel.setLayout(new MigLayout("insets 0"));
		AlvarezLensArray2Panel.setBorder(GUIBitsAndBobs.getTitledBorder("Alvarez-lens array 2 (closer to camera)"));
		scenePanel.add(AlvarezLensArray2Panel, "span");
		
		pP2Panel = new LabelledDoublePanel("P'");
		pP2Panel.setNumber(pP2);
		pP2Panel.setToolTipText("Focal power / delta_x for Alvarez-lens array 2");
		
		z2Panel = new LabelledDoublePanel("z");
		z2Panel.setNumber(z2);
		z2Panel.setToolTipText("z coordinate of Alvarez-lens array 2");
		AlvarezLensArray2Panel.add(GUIBitsAndBobs.makeRow(pP2Panel, z2Panel), "span");

		delta02Panel = new LabelledDoublePanel("delta_0");
		delta02Panel.setNumber(delta02);
		delta02Panel.setToolTipText("Offset (in the x direction) between the two parts of Alvarez-lens array 2 in the reference configuration (delta_x = 0)");
		AlvarezLensArray2Panel.add(delta02Panel, "span");
		
		showLensArray2Part1CheckBox = new JCheckBox("part 1");
		showLensArray2Part1CheckBox.setSelected(showLensArray2Part1);

		showLensArray2Part2CheckBox = new JCheckBox("part 2");
		showLensArray2Part2CheckBox.setSelected(showLensArray2Part2);

		// ALLensArray2Panel.add(GUIBitsAndBobs.makeRow("Show", showLens2Part1CheckBox, showLens2Part2CheckBox), "wrap");
		
		// equivalent lens array
		
		showLensArray2EquivalentCheckBox = new JCheckBox("OR equivalent lens array");
		showLensArray2EquivalentCheckBox.setSelected(showLensArray2Equivalent);
		showLensArray2EquivalentCheckBox.addActionListener(this);
		// ALLensArray2Panel.add(showLensArray2EquivalentCheckBox, "wrap");
		AlvarezLensArray2Panel.add(GUIBitsAndBobs.makeRow("Show", showLensArray2Part1CheckBox, showLensArray2Part2CheckBox, showLensArray2EquivalentCheckBox), "wrap");


		// initialisation of Alvarez-lens arrays
		
		JPanel initPanel = new JPanel();
		initPanel.setLayout(new MigLayout("insets 0"));
		initPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Initialise Alvarez-lens arrays"));
		scenePanel.add(initPanel, "span");

		etaPanel = new LabelledDoublePanel("eta");
		etaPanel.setNumber(eta);
		
//		tPanel = new LabelledDoublePanel("t");
//		tPanel.setNumber(t);
		
		// initPanel.add(etaPanel, "wrap");
		
		initpP12Button = new JButton("Init both arrays' P' and array 1's delta_0");
		initpP12Button.setToolTipText("Init both arrays' P' and array 1's delta_0 from eta, the separation between the arrays, (z1-z2), and array_2's delta_0");
		initpP12Button.addActionListener(this);
		// initPanel.add(initpP12Button, "span");
		initPanel.add(GUIBitsAndBobs.makeRow(etaPanel, initpP12Button), "span");
		
//		initpP2Delta0Button = new JButton("Init both arrays' delta_0 and P' of array 2 from eta, t and P' of array 1");
//		initpP2Delta0Button.addActionListener(this);
//		initPanel.add(initpP2Delta0Button, "wrap");
		
//		initButton = new JButton("From starred (*) parameters calculate other parameters");
//		initButton.addActionListener(this);
//		scenePanel.add(initButton, "wrap");
		
		
		
		
		// middle "meat" component
		
		JPanel meatComponentPanel = new JPanel();
		meatComponentPanel.setLayout(new MigLayout("insets 0"));
		meatComponentPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Middle (\"Meat\") component"));
		scenePanel.add(meatComponentPanel, "span");

		meatComponentXYPanel = new LabelledVector2DPanel("(delta_x, delta_y)");
		meatComponentXYPanel.setVector2D(new Vector2D(deltaX, deltaY));
		meatComponentXYPanel.setToolTipText("Offset of the middle (or \"meat\") component of the telescope array; delta_x changes the focal power of both Alvarez-lens arrays; delta_y changes the parameter for oblique astigmatism of both Alvarez-lens arrays");
		meatComponentPanel.add(meatComponentXYPanel, "span");
		
//		fromA1AndTCalculateRestButton = new JButton("From P'_1 and tube length calculate rest");
//		fromA1AndTCalculateRestButton.addActionListener(this);
//		scenePanel.add(fromA1AndTCalculateRestButton, "span");
		
//		deltaXPanel = new LabelledDoublePanel("delta_x");
//		deltaXPanel.setNumber(deltaX);
//		deltaXPanel.setToolTipText("x offset of the middle (or \"meat\") component of the telescope; this changes the focal power of both Alvarez-lens arrays");
//		meatComponentPanel.add(deltaXPanel, "span");
//
//		deltaYPanel = new LabelledDoublePanel("delta_y");
//		deltaYPanel.setNumber(deltaY);
//		deltaYPanel.setToolTipText("delta_y, i.e. y offset of inner lens parts relative to outer lens parts; this changes the parameter for oblique astigmatism of both Alvarez-lens arrays");
//		meatComponentPanel.add(deltaYPanel, "span");

		deltaPhiDegPanel = new DoublePanel();
		deltaPhiDegPanel.setNumber(deltaPhiDeg);
		deltaPhiDegPanel.setToolTipText("Angle by which part 1 is rotated relative to part 2 around the optical axis");
		meatComponentPanel.add(GUIBitsAndBobs.makeRow("delta_phi", deltaPhiDegPanel, "degrees"), "span");

		setToReferenceConfigurationButton = new JButton("Set to reference configuration");
		setToReferenceConfigurationButton.addActionListener(this);
		meatComponentPanel.add(setToReferenceConfigurationButton, "span");
		
		
		// other parameters
		
		JPanel otherParametersPanel = new JPanel();
		otherParametersPanel.setLayout(new MigLayout("insets 0"));
		otherParametersPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Other parameters"));
		scenePanel.add(otherParametersPanel, "span");
		
		periodPanel = new LabelledDoublePanel("array period");
		periodPanel.setNumber(period);
		periodPanel.setToolTipText("Period of all Alvarez-lens-part arrays");
		otherParametersPanel.add(periodPanel, "span");
	
		simulateDiffractiveBlurCheckBox = new JCheckBox("");
		simulateDiffractiveBlurCheckBox.setSelected(simulateDiffractiveBlur);
		lambdaNMPanel = new DoublePanel();
		lambdaNMPanel.setNumber(lambdaNM);
		otherParametersPanel.add(GUIBitsAndBobs.makeRow(simulateDiffractiveBlurCheckBox, "Simulate diffractive blur for wavelength", lambdaNMPanel, "nm"), "span");

		dzPanel = new LabelledDoublePanel("Separation between Alvarez-lens-array parts 1 and 2");
		dzPanel.setNumber(dz);
		dzPanel.setToolTipText("Separation, in the z direction, between Alvarez-lens-array parts 1 and 2.  For numerical reasons, this should be around 1e-5");
		otherParametersPanel.add(dzPanel, "span");
		
		
		infoTextArea = new JTextArea(5, 30);
		JScrollPane scrollPane = new JScrollPane(infoTextArea); 
		infoTextArea.setEditable(false);
		infoTextArea.setText("Click on Update button to show info");
		updateInfoButton = new JButton("Update");
		updateInfoButton.addActionListener(this);
		scenePanel.add(GUIBitsAndBobs.makeRow(scrollPane, updateInfoButton), "span");

		
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
		
		cameraDistancePanel = new LabelledDoublePanel("Camera distance (from centre of lens array 2)");
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
		// cameraPanel.add(cameraFocussingDistancePanel);
		
		focusCameraButton = new JButton("Focus on Tim's eyes");
		focusCameraButton.addActionListener(this);
		cameraPanel.add(GUIBitsAndBobs.makeRow(cameraFocussingDistancePanel, focusCameraButton), "span");
	}
	
	private void initialiseToEta(double eta)
	{
		this.eta = eta;
		period = 0.01;
		delta02 = 0.001;
		deltaX = 0;
		deltaY = 0;
		deltaPhiDeg = 0;
		if(eta > 0)
		{
			z1 = 6.1;
			z2 = 6;
		}
		else
		{
			z1 = 6.1;
			z2 = 6;
		}
		dz = 0.00001;
		initpP12AndDelta01();
		
		etaPanel.setNumber(eta);
		periodPanel.setNumber(period);
		delta02Panel.setNumber(delta02);
		meatComponentXYPanel.setVector2D(deltaX, deltaY);
		deltaPhiDegPanel.setNumber(deltaPhiDeg);
		z1Panel.setNumber(z1);
		z2Panel.setNumber(z2);
		dzPanel.setNumber(dz);
		pP1Panel.setNumber(pP1);
		pP2Panel.setNumber(pP2);
		delta01Panel.setNumber(delta01);
		
		showLensArray1Part1CheckBox.setSelected(true);
		showLensArray1Part2CheckBox.setSelected(true);
		showLensArray2Part1CheckBox.setSelected(true);
		showLensArray2Part2CheckBox.setSelected(true);
		showLensArray1EquivalentCheckBox.setSelected(false);
		showLensArray2EquivalentCheckBox.setSelected(false);
		
		cameraFocussingDistancePanel.setNumber(cameraDistance + eta*(9-z2));
		
		studioInitialisationComboBox.setSelectedItem(StudioInitialisationType.TIM_HEAD);
	}
	
	/**
	 * called before rendering;
	 * override when adding fields
	 */
	@Override
	protected void acceptValuesInInteractiveControlPanel()
	{
		super.acceptValuesInInteractiveControlPanel();
		
		pP1 = pP1Panel.getNumber();
		pP2 = pP2Panel.getNumber();
		z1 = z1Panel.getNumber();
		z2 = z2Panel.getNumber();
		delta01 = delta01Panel.getNumber();
		delta02 = delta02Panel.getNumber();
		deltaX = meatComponentXYPanel.getVector2D().x;
		deltaY = meatComponentXYPanel.getVector2D().y;
		deltaPhiDeg = deltaPhiDegPanel.getNumber();
		period = periodPanel.getNumber();
		dz = dzPanel.getNumber();
		
		showLensArray1Part1 = showLensArray1Part1CheckBox.isSelected();
		showLensArray1Part2 = showLensArray1Part2CheckBox.isSelected();
		showLensArray2Part1 = showLensArray2Part1CheckBox.isSelected();
		showLensArray2Part2 = showLensArray2Part2CheckBox.isSelected();
		showLensArray1Equivalent = showLensArray1EquivalentCheckBox.isSelected();
		showLensArray2Equivalent = showLensArray2EquivalentCheckBox.isSelected();
		simulateDiffractiveBlur = simulateDiffractiveBlurCheckBox.isSelected();
		lambdaNM = lambdaNMPanel.getNumber();
		eta = etaPanel.getNumber();
		// t = tPanel.getNumber();
		studioInitialisation = (StudioInitialisationType)(studioInitialisationComboBox.getSelectedItem());
		
		// cameraViewDirection = cameraViewDirectionPanel.getVector3D();
		cameraDistance = cameraDistancePanel.getNumber();
		cameraViewDirection = cameraViewDirectionPanel.getVector3D();
		cameraHorizontalFOVDeg = cameraHorizontalFOVDegPanel.getNumber();
		cameraApertureSize = (ApertureSizeType)(cameraApertureSizeComboBox.getSelectedItem());
		cameraFocussingDistance = cameraFocussingDistancePanel.getNumber();
	}
	
	void initParameters(ParametersInitialisationType p)
	{
		switch(p)
		{
		case JUST_TIM:
			studioInitialisationComboBox.setSelectedItem(StudioInitialisationType.TIM_HEAD);
			
			showLensArray1Part1CheckBox.setSelected(false);
			showLensArray1Part2CheckBox.setSelected(false);
			showLensArray2Part1CheckBox.setSelected(false);
			showLensArray2Part2CheckBox.setSelected(false);
			showLensArray1EquivalentCheckBox.setSelected(false);
			showLensArray2EquivalentCheckBox.setSelected(false);

			// cameraDistancePanel.setNumber(cameraDistance1);
			cameraViewDirectionPanel.setVector3D(new Vector3D(0, 0, 1));
			// cameraHorizontalFOVDegPanel.setNumber(cameraHorizontalFOVDeg);
			cameraApertureSizeComboBox.setSelectedItem(ApertureSizeType.MEDIUM);
			cameraFocussingDistancePanel.setNumber(cameraDistance-z2+9);

			break;
		case ETA2:
			initialiseToEta(2);
			break;
		case ETA4:
			initialiseToEta(4);
			break;
		case ETA_1:
			initialiseToEta(-1);
			break;
		case ETA_2:
			initialiseToEta(-2);
			break;
//		case SIMULATE_DIFFRACTIVE_BLUR_550:
//			simulateDiffractiveBlurCheckBox.setSelected(true);
//			lambdaNMPanel.setNumber(550);
//			break;
//		case NO_DIFFRACTIVE_BLUR:
//			simulateDiffractiveBlurCheckBox.setSelected(false);
//			break;
//		case SHOW_LAS_FROM_ABOVE:
//			cameraViewCentrePanel.setVector3D(new Vector3D(0, 0, 0.5*f01Panel.getNumber()));
//			cameraDistancePanel.setNumber(2);
//			cameraViewDirectionPanel.setVector3D(new Vector3D(0.02, -1, .2));
//			cameraHorizontalFOVDegPanel.setNumber(15);
//			cameraApertureSizeComboBox.setSelectedItem(ApertureSizeType.TINY);
//			cameraFocussingDistancePanel.setNumber(1.5);
//			
//			break;
//		case SHOW_LAS_AND_TIM:
//			cameraViewCentrePanel.setVector3D(new Vector3D(0, 0.5, 0));	// 0.5*f1Panel.getNumber()));
//			cameraDistancePanel.setNumber(0.25);
//			cameraViewDirectionPanel.setVector3D(new Vector3D(0, -.8, 1));
//			cameraHorizontalFOVDegPanel.setNumber(110);
//			cameraApertureSizeComboBox.setSelectedItem(ApertureSizeType.LARGE);
//			cameraFocussingDistancePanel.setNumber(0.25);
//			
//			break;
		case INIT:
		default:
			// do nothing
		}
	}
	
	public void updateInfo()
	{
		NumberFormat nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(8);
		
		double f1 = calculateF1();
		double f2 = calculateF2();
		infoTextArea.setText(
				"f1="+nf.format(f1)+"\n"+
				"f2="+nf.format(f2)+"\n"+
				"eta="+nf.format(-f2/f1)+"\n"+
				"(f1+f2)-(z1-z2)="+nf.format(f1+f2-(z1-z2))+"\n"+
				"(z1>z2)="+(z1>z2)
			);
	}
	
	/**
	 * from eta, delta02 and (z1-z2), calculate pP1, pP2 and delta01
	 */
	public void initpP12AndDelta01()
	{
		pP1 = (eta-1)/(delta02*(z1-z2))*eta;
		pP2 = (eta-1)/(delta02*(z1-z2))/eta;
		// there are two solutions for delta01; check which one is the correct one
		delta01 = -delta02*Math.sqrt(pP2/pP1);	// solution 1
		if(Math.abs(calculateF1()+calculateF2()-(z1-z2)) > MyMath.TINY)
		{
			// solution 1 is not good, as the focal lengths don't sum up to the separation between the Alvarez-lens arrays
			delta01 = delta02*Math.sqrt(pP2/pP1);	// solution 2
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		super.actionPerformed(e);
		
		if(e.getSource().equals(parametersInitialisationComboBox))
		{
			initParameters((ParametersInitialisationType)(parametersInitialisationComboBox.getSelectedItem()));
			parametersInitialisationComboBox.setSelectedItem(ParametersInitialisationType.INIT);
			
			acceptValuesInInteractiveControlPanel();
			updateInfo();
		}
		else if(e.getSource().equals(setToReferenceConfigurationButton))
		{
			meatComponentXYPanel.setVector2D(0, 0);
			deltaPhiDegPanel.setNumber(0);
		}
		else if(e.getSource().equals(initpP12Button))
		{
			acceptValuesInInteractiveControlPanel();
			
			initpP12AndDelta01();

			pP1Panel.setNumber(pP1);
			pP2Panel.setNumber(pP2);
			delta01Panel.setNumber(delta01);

			acceptValuesInInteractiveControlPanel();
			updateInfo();
		}
//		else if(e.getSource().equals(initpP2Delta0Button))
//		{
//			acceptValuesInInteractiveControlPanel();
//			
//			pP2 = pP1/(eta*eta);
//			delta02 = (eta-1)*eta/(pP1*(z1-z2));
//			// there are two solutions for delta01; check which one is the correct one
//			delta01 = -delta02*Math.sqrt(pP2/pP1);	// solution 1
//			// System.out.println("Solution 1: delta01="+delta01+", f1+f2-(z2-z1) ="+(calculateF1()+calculateF2()-(z2-z1)));
//			if(Math.abs(calculateF1()+calculateF2()-(z1-z2)) > MyMath.TINY)
//			{
//				// solution 1 is not good, as the focal lengths don't sum up to the separation between the Alvarez-lens arrays
//				delta01 = delta02*Math.sqrt(pP2/pP1);	// solution 2
//				// System.out.println("Solution 1 is bad; Solution 2: delta01="+delta01+", f1+f2-(z2-z1) ="+(calculateF1()+calculateF2()-(z2-z1)));
//			}
//			
//			pP2Panel.setNumber(pP2);
//			delta02Panel.setNumber(delta02);
//			delta01Panel.setNumber(delta01);
//			
//			acceptValuesInInteractiveControlPanel();
//			updateInfo();
//		}
		else if(e.getSource().equals(updateInfoButton))
		{
			acceptValuesInInteractiveControlPanel();
			updateInfo();
		}
		else if(e.getSource().equals(showLensArray1EquivalentCheckBox))
		{
			showLensArray1Part1CheckBox.setEnabled(!showLensArray1EquivalentCheckBox.isSelected());
			showLensArray1Part2CheckBox.setEnabled(!showLensArray1EquivalentCheckBox.isSelected());
		}
		else if(e.getSource().equals(showLensArray2EquivalentCheckBox))
		{
			showLensArray2Part1CheckBox.setEnabled(!showLensArray2EquivalentCheckBox.isSelected());
			showLensArray2Part2CheckBox.setEnabled(!showLensArray2EquivalentCheckBox.isSelected());
		}
//		else if(e.getSource().equals(initButton))
//		{
//			acceptValuesInInteractiveControlPanel();
//			
//			pP2 = pP1;
//			delta0 = 2./(pP1 * (z2-z1));
//			lens1Part1XY = new Vector2D(-0.5*delta0, 0); 
//			lens1Part2XY = new Vector2D( 0.5*delta0 + deltaX, deltaY);
//			lens2Part1XY = new Vector2D(-0.5*delta0 + deltaX, deltaY);
//			lens2Part2XY = new Vector2D( 0.5*delta0, 0);
//			
//			pP2Panel.setNumber(pP2);
//			delta0Panel.setNumber(delta0);
//			lens1Part1XYPanel.setVector2D(lens1Part1XY);
//			lens1Part2XYPanel.setVector2D(lens1Part2XY);
//			lens2Part1XYPanel.setVector2D(lens2Part1XY);
//			lens2Part2XYPanel.setVector2D(lens2Part2XY);
//			
//			// mark what has been changed
//			pP2Panel.setBackground(Color.gray);
//			delta0Panel.setBackground(Color.gray);
//			lens1Part1XYPanel.setBackground(Color.gray);
//			lens1Part2XYPanel.setBackground(Color.gray);
//			lens2Part1XYPanel.setBackground(Color.gray);
//			lens2Part2XYPanel.setBackground(Color.gray);
//
//
//			// finally, update f1 and f2
//			f1Panel.setNumber(calculateF1());
//			f2Panel.setNumber(calculateF2());
//		}
		else if(e.getSource().equals(focusCameraButton))
		{
			acceptValuesInInteractiveControlPanel();

			double f1 = calculateF1();
			double f2 = calculateF2();
			double eta = -f2/f1;

			cameraFocussingDistancePanel.setNumber(cameraDistance + eta*(8.95-z1));
		}
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
		(new AlvarezTelescopeArrayExplorer()).run();
	}

}
