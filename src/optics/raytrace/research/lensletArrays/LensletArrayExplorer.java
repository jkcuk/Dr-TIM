package optics.raytrace.research.lensletArrays;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintStream;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import math.*;
import net.miginfocom.swing.MigLayout;
import optics.raytrace.sceneObjects.LensType;
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
 * Simulate the visual appearance of combinations of lenslet arrays, and the view through them.
 * 
 * @author Johannes Courtial
 */
public class LensletArrayExplorer extends NonInteractiveTIMEngine implements ActionListener
{
	/**
	 * focal length of lenslet array 1 (closer to object, located in plane z=0)
	 */
	private double f1;
	
	/**
	 * focal length of lenslet array 2 (closer to camera)
	 */
	private double f2;
	
	/**
	 * period of lenslet array 1
	 */
	private double period1;

	/**
	 * period of lenslet array 2
	 */
	private double period2;

	/**
	 * angle of normal to lenslet array 1 w.r.t. z axis
	 */
	private double theta1Deg;
	
	/**
	 * angle of normal to lenslet array 2 w.r.t. z axis
	 */
	private double theta2Deg;
			
	/**
	 * rotation angle of LA1 around its normal
	 */
	private double phi1Deg;

	/**
	 * rotation angle of LA2 around its normal
	 */
	private double phi2Deg;
	
	/**
	 * minimum z separation of LA centres
	 */
	private double minimumZSeparation;

	/**
	 * show lenslet array 1
	 */
	private boolean showLensletArray1;

	/**
	 * show lenslet array 2
	 */
	private boolean showLensletArray2;
	
	private LensType lensType;
	
	private boolean shadowThrowing;
	
	/**
	 * show a field lens in the common focal plane (if the telescopes are "focussed" on infinity)
	 * or in the plane of the image of the plane on which the telescopes are focussed;
	 * this works only if both f1 and f2 are positive, period1 = period2, and if theta1 = phi1 = theta2 = phi2 = 0
	 */
	private boolean showFieldLensArray;
	
	/**
	 * if true, focus on an object plane at z=objectZ
	 */
	private boolean focusOnObjectZ;
	
	/**
	 * if <focusOnObjectZ>, the camera focusses on an object plane at z=objectZ
	 */
	private double objectZ;

	
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
	 * Determines how to initialise the backdrop
	 */
	private StudioInitialisationType studioInitialisation;
	
	
	/**
	 * Constructor.
	 * Sets all parameters.
	 */
	public LensletArrayExplorer()
	{
		super();
		
		// set to true for interactive version
		nonInteractiveTIMAction = NonInteractiveTIMActionEnum.INTERACTIVE;
		
		// set all parameters
		
		renderQuality = RenderQualityEnum.DRAFT;
		
		// lenslet-array parameters
		f1 = 0.1;
		f2 = -f1;
		minimumZSeparation = 0.00001;
		period1 = 0.01;
		period2 = 0.01;
		theta1Deg = 0;
		theta2Deg = 0;
		phi1Deg = 0;
		phi2Deg = 0;
		lensType = LensType.IDEAL_THIN_LENS;
		shadowThrowing = true;
		showFieldLensArray = false;
		showLensletArray1 = true;
		showLensletArray2 = true;
		focusOnObjectZ = false;
		objectZ = 8.95;
		simulateDiffractiveBlur = true;
		lambdaNM = 632.8;
		
		studioInitialisation = StudioInitialisationType.LATTICE;	// the backdrop

		// camera
		cameraViewCentre = new Vector3D(0, 0, 0);
		cameraDistance = 8;
		cameraViewDirection = new Vector3D(0, 0, 1);
		cameraHorizontalFOVDeg = 10;
		cameraApertureSize = ApertureSizeType.PINHOLE;
		cameraFocussingDistance = 10;
		
		if(nonInteractiveTIMAction == NonInteractiveTIMActionEnum.INTERACTIVE)
		{
			windowTitle = "Dr TIM's lenslet-array explorer";
			windowWidth = 1500;
			windowHeight = 650;
		}
	}
	

	@Override
	public String getFirstPartOfFilename()
	{
		return "LensletArrayExplorer"	// the name
//				+ (showLensletArray1?
//						" LA1 theta="+theta1Deg+"deg"
//						+" phi="+phi1Deg+"deg"
//						+" f="+f1
//						+" period="+period1
//						:"")
//				+ (showLensletArray2?
//						" LA2 theta="+theta2Deg+"deg"
//						+" phi="+phi2Deg+"deg"
//						+" f="+f2
//						+" period="+period2
//						:"")
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
		
		printStream.println("f1="+f1);
		printStream.println("f2="+f2);
		printStream.println("period1="+period1);
		printStream.println("period2="+period2);
		printStream.println("theta1Deg="+theta1Deg);
		printStream.println("theta2Deg="+theta2Deg);
		printStream.println("phi1Deg="+phi1Deg);
		printStream.println("phi2Deg="+phi2Deg);
		printStream.println("minimumZSeparation="+minimumZSeparation);
		printStream.println("showLensletArray1="+showLensletArray1);
		printStream.println("showLensletArray2="+showLensletArray2);
		printStream.println("lensType="+lensType);
		printStream.println("shadowThrowing="+shadowThrowing);
		printStream.println("showFieldLens="+showFieldLensArray);
		printStream.println("focusOnObjectZ="+focusOnObjectZ);
		printStream.println("objectZ="+objectZ);
		printStream.println("simulateDiffractiveBlur="+simulateDiffractiveBlur);
		printStream.println("lambdaNM="+lambdaNM);
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
		
		double zSeparationFactor;
		if(focusOnObjectZ)
		{
			// to focus the telescopes on an object at object distance o, the separation between the two lenses has to be 1/(1-f1/o) (f1 + f2),
			// so everything is scaled by a factor 1/(1-f1/o)
			// object distance = objectZ (as LA1 is in the plane z=0)
			zSeparationFactor = 1./(1-f1/objectZ);
		}
		else
		{
			zSeparationFactor = 1;
		}
		
		double zSeparation = zSeparationFactor * (f1+f2);
		// ... but when it gets too small, then Dr TIM doesn't recognise that there are two separate lenslet arrays
		if(Math.abs(zSeparation) < minimumZSeparation)
		{
			// in that case, set the z separation to some number (with the same sign as f1+f2) that is large enough
			// so that Dr TIM can tell the two lenslet arrays apart but small enough that the telescope array formed
			// by the two lenslet arrays still works well
			zSeparation = minimumZSeparation*MyMath.signumNever0(f1+f2);
		}
		if(zSeparation < 0)
		{
			// if the two lenslet arrays switch order, the imaging will change; put a warning in the console
			System.err.println("The separation between the two lenslet arrays is negative ("+zSeparation+"), which means they switch order");			
		}

		// System.out.println("zSeparation="+zSeparation);
		
		double theta1 = MyMath.deg2rad(theta1Deg);
		double phi1 = MyMath.deg2rad(phi1Deg);
		Vector3D normal1 = new Vector3D(Math.sin(theta1), 0, Math.cos(theta1));
		Vector3D right1 = Vector3D.crossProduct(normal1, up);
		double c1 = Math.cos(phi1);
		double s1 = Math.sin(phi1);
		
		scene.addSceneObject(new EditableRectangularLensletArray(
				"LA1",	// description
				new Vector3D(0, 0, 0),	// centre
				Vector3D.sum(right1.getProductWith(c1), up.getProductWith(s1)),	// spanVector1
				Vector3D.sum(right1.getProductWith(-s1), up.getProductWith(c1)),	// spanVector2
				f1,	// focalLength
				period1,	// xPeriod
				period1,	// yPeriod
				0,	// xOffset
				0,	// yOffset
				lensType,	// lensType
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

		double theta2 = MyMath.deg2rad(theta2Deg);
		double phi2 = MyMath.deg2rad(phi2Deg);
		Vector3D normal2 = new Vector3D(Math.sin(theta2), 0, Math.cos(theta2));
		Vector3D right2 = Vector3D.crossProduct(normal2, up);
		double c2 = Math.cos(phi2);
		double s2 = Math.sin(phi2);
		
		scene.addSceneObject(new EditableRectangularLensletArray(
				"LA2",	// description
				new Vector3D(0, 0, -zSeparation),	// centre
				Vector3D.sum(right2.getProductWith(c2), up.getProductWith(s2)),	// spanVector1
				Vector3D.sum(right2.getProductWith(-s2), up.getProductWith(c2)),	// spanVector2
				f2,	// focalLength
				period2,	// xPeriod
				period2,	// yPeriod
				0,	// xOffset
				0,	// yOffset
				lensType,
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
		
		// field-lens array
		
		// this works only if both f1 and f2 are positive, period1 = period2, and if theta1 = phi1 = theta2 = phi2 = 0
		if((f1 > 0) && (f2 > 0) && (period1 == period2) && (theta1 == 0) && (phi1 == 0) && (theta2 == 0) && (phi2 == 0))
		{
			// 1/fField == 1/f1 + 1/f2
			// field lens has to be a distance i behind LA1, where 1/objectZ + 1/i = 1/f1, so 1/i = 1/f1 - 1/objectZ
			scene.addSceneObject(new EditableRectangularLensletArray(
					"field-lens array",	// description
					new Vector3D(0, 0, (focusOnObjectZ?-1/(1/f1 - 1/objectZ):-f1)),	// centre
					right1,	// spanVector1
					up,	// spanVector2
					1/(1/(f1*zSeparationFactor) + 1/(f2*zSeparationFactor)),	// focalLength
					period1,	// xPeriod
					period1,	// yPeriod
					0,	// xOffset
					0,	// yOffset
					lensType,
					simulateDiffractiveBlur,
					lambdaNM*1e-9,
					0.96,	// throughputCoefficient
					false,	// reflective
					shadowThrowing,	// shadowThrowing
					scene,	// parent
					studio
				), 
				showFieldLensArray
			);
		}

		// the camera
		// cameraFocussingDistance

		studio.setCamera(getStandardCamera());
	}

	
	
	//
	// for interactive version
	//
	
	public enum LensletArraysInitialisationType
	{
		INIT("Initialise lenslet arrays to..."),
		GABOR("Gabor superlens"),
		MOIRE("Moir\u00E9 magnifier"),
		CLAS("Confocal lenslet arrays");
		
		private String description;
		private LensletArraysInitialisationType(String description) {this.description = description;}	
		@Override
		public String toString() {return description;}
	}

	
	private LabelledDoublePanel f1Panel, f2Panel, minimumZSeprarationPanel, period1Panel, period2Panel;
	private DoublePanel theta1DegPanel, theta2DegPanel, phi1DegPanel, phi2DegPanel, lambdaNMPanel, objectZPanel; // , etaPanel, separationPanel
	private JCheckBox showLensletArray1CheckBox, showLensletArray2CheckBox, shadowThrowingCheckBox, simulateDiffractiveBlurCheckBox, showFieldLensArrayCheckBox, focusOnObjectCheckBox;
	private JComboBox<LensletArraysInitialisationType> lensletArraysInitialisationComboBox;
	private JComboBox<LensType> lensTypeComboBox;
	private JComboBox<StudioInitialisationType> studioInitialisationComboBox;
	private JButton setObjectZ2LastClickZButton;	// etaSOInitialisationButton;	// gaborInitialisationButton, moireInitialisationButton, clasInitialisationButton;

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

		// scenePanel.add(new JLabel("Lenslet-array initialisation"));
		
		JPanel initPanel = new JPanel();
		initPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Initialisation of lenslet arrays"));
		initPanel.setLayout(new MigLayout("insets 0"));
		scenePanel.add(initPanel, "span");

		lensletArraysInitialisationComboBox = new JComboBox<LensletArraysInitialisationType>(LensletArraysInitialisationType.values());
		lensletArraysInitialisationComboBox.setSelectedItem(LensletArraysInitialisationType.INIT);
		lensletArraysInitialisationComboBox.addActionListener(this);
//		lensletArraysInitialisationButton = new JButton("Go");
//		lensletArraysInitialisationButton.addActionListener(this);
		initPanel.add(
				GUIBitsAndBobs.makeRow("Either", 
						lensletArraysInitialisationComboBox
						)
				, "span");

		// TODO
//		etaPanel, separationPanel, objectDistancePanel
//		etaSOInitialisationButton = new JButton()
		
//		scenePanel.add(new JLabel("Lenslet-array initialisation"), "span");
//		JTabbedPane laInitTabbedPane = new JTabbedPane();
//		scenePanel.add(laInitTabbedPane, "span");
//		
//		//
//		// the Gabor superlens initialisation panel
//		//
//		
//		JPanel gaborSuperlensPanel = new JPanel();
//		gaborSuperlensPanel.setLayout(new MigLayout("insets 0"));
//		laInitTabbedPane.addTab("Gabor superlens",
//				gaborSuperlensPanel);
//		
//		gaborInitialisationButton = new JButton("Initialise");
//		gaborInitialisationButton.addActionListener(this);
//		gaborSuperlensPanel.add(gaborInitialisationButton, "span");
//
//		//
//		// the moire magnifier initialisation panel
//		//
//		
//		JPanel moireMagnifierPanel = new JPanel();
//		moireMagnifierPanel.setLayout(new MigLayout("insets 0"));
//		laInitTabbedPane.addTab("Moire magnifier", 
//				moireMagnifierPanel);
//
//		moireInitialisationButton = new JButton("Initialise");
//		moireInitialisationButton.addActionListener(this);
//		moireMagnifierPanel.add(moireInitialisationButton, "span");
//
//		//
//		// the confocal lenslet arrays initialisation panel
//		//
//		
//		JPanel clasPanel = new JPanel();
//		clasPanel.setLayout(new MigLayout("insets 0"));
//		laInitTabbedPane.addTab("Confocal lenslet arrays",
//				clasPanel);
//
//		clasInitialisationButton = new JButton("Initialise");
//		clasInitialisationButton.addActionListener(this);
//		clasPanel.add(clasInitialisationButton, "span");

		//
		// the LA1 panel
		//
		
		JPanel la1Panel = new JPanel();
		la1Panel.setBorder(GUIBitsAndBobs.getTitledBorder("Lenslet array 1 (closer to object)"));
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

		theta1DegPanel = new DoublePanel();
		theta1DegPanel.setNumber(theta1Deg);
		theta1DegPanel.setToolTipText("Angle of the array normal with the z axis");
		la1Panel.add(GUIBitsAndBobs.makeRow("theta", theta1DegPanel, "degrees"), "span");

		phi1DegPanel = new DoublePanel();
		phi1DegPanel.setNumber(phi1Deg);
		phi1DegPanel.setToolTipText("Angle by which the array is rotated around the array normal");
		la1Panel.add(GUIBitsAndBobs.makeRow("phi", phi1DegPanel, "degrees"), "span");



		//
		// the LA2 panel
		//
		
		JPanel la2Panel = new JPanel();
		la2Panel.setBorder(GUIBitsAndBobs.getTitledBorder("Lenslet array 2 (closer to camera)"));
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

		period2Panel = new LabelledDoublePanel("Period");
		period2Panel.setNumber(period2);
		period2Panel.setToolTipText("Period, i.e. distance between neighbouring lenslets");
		la2Panel.add(period2Panel, "span");

		theta2DegPanel = new DoublePanel();
		theta2DegPanel.setNumber(theta2Deg);
		theta2DegPanel.setToolTipText("Angle of the array normal with the z axis");
		la2Panel.add(GUIBitsAndBobs.makeRow("theta", theta2DegPanel, "degrees"), "span");

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

		
		lensTypeComboBox = new JComboBox<LensType>(LensType.values());
		lensTypeComboBox.setSelectedItem(lensType);
		shadowThrowingCheckBox = new JCheckBox("Shadow throwing");
		shadowThrowingCheckBox.setSelected(shadowThrowing);
		commonLAParametersPanel.add(GUIBitsAndBobs.makeRow(lensTypeComboBox, shadowThrowingCheckBox), "span");
		
		showFieldLensArrayCheckBox = new JCheckBox("Add array of field lenses in common focal plane");
		showFieldLensArrayCheckBox.setSelected(showFieldLensArray);
		commonLAParametersPanel.add(showFieldLensArrayCheckBox, "span");
		
		
		//
		
		focusOnObjectCheckBox = new JCheckBox("Focus on object at z=");
		focusOnObjectCheckBox.setSelected(focusOnObjectZ);
		objectZPanel = new DoublePanel();
		objectZPanel.setNumber(objectZ);
		commonLAParametersPanel.add(GUIBitsAndBobs.makeRow(focusOnObjectCheckBox, objectZPanel, new JLabel("(CLAs only)")), "span");
		
		setObjectZ2LastClickZButton = new JButton("Set object distance to that of last click");
		setObjectZ2LastClickZButton.addActionListener(this);
		commonLAParametersPanel.add(setObjectZ2LastClickZButton, "span");
		
		// objectZPanel.setNumber(getLastClickIntersection().p.z);

		// TODO
		//
		
		

		simulateDiffractiveBlurCheckBox = new JCheckBox("");
		simulateDiffractiveBlurCheckBox.setSelected(simulateDiffractiveBlur);
		lambdaNMPanel = new DoublePanel();
		lambdaNMPanel.setNumber(lambdaNM);
		commonLAParametersPanel.add(GUIBitsAndBobs.makeRow(simulateDiffractiveBlurCheckBox, "Simulate diffractive blur for wavelength", lambdaNMPanel, "nm"), "span");

		minimumZSeprarationPanel = new LabelledDoublePanel("Minimum z separation between array centres");
		minimumZSeprarationPanel.setNumber(minimumZSeparation);
		minimumZSeprarationPanel.setToolTipText("The centres of the two lenslet arrays are separated in the z direction by a distance f1 + f2, unless this is less than the minimum z separation");
		commonLAParametersPanel.add(minimumZSeprarationPanel, "span");

		
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
		
		f1 = f1Panel.getNumber();
		f2 = f2Panel.getNumber();
		minimumZSeparation = minimumZSeprarationPanel.getNumber();
		period1 = period1Panel.getNumber();
		period2 = period2Panel.getNumber();
		theta1Deg = theta1DegPanel.getNumber();
		theta2Deg = theta2DegPanel.getNumber();
		phi1Deg = phi1DegPanel.getNumber();
		phi2Deg = phi2DegPanel.getNumber();
		
		showLensletArray1 = showLensletArray1CheckBox.isSelected();
		showLensletArray2 = showLensletArray2CheckBox.isSelected();
		lensType = (LensType)(lensTypeComboBox.getSelectedItem());
		shadowThrowing = shadowThrowingCheckBox.isSelected();
		showFieldLensArray = showFieldLensArrayCheckBox.isSelected();
		focusOnObjectZ = focusOnObjectCheckBox.isSelected();
		objectZ = objectZPanel.getNumber();
		simulateDiffractiveBlur = simulateDiffractiveBlurCheckBox.isSelected();
		lambdaNM = lambdaNMPanel.getNumber();
		studioInitialisation = (StudioInitialisationType)(studioInitialisationComboBox.getSelectedItem());
		
		// cameraViewDirection = cameraViewDirectionPanel.getVector3D();
		cameraDistance = cameraDistancePanel.getNumber();
		cameraViewDirection = cameraViewDirectionPanel.getVector3D();
		cameraHorizontalFOVDeg = cameraHorizontalFOVDegPanel.getNumber();
		cameraApertureSize = (ApertureSizeType)(cameraApertureSizeComboBox.getSelectedItem());
		cameraFocussingDistance = cameraFocussingDistancePanel.getNumber();
	}
	
	/**
	 * initialise the lenslet-array parameters to a Gabor superlens
	 */
	private void gaborInitialisation()
	{
		// lenslet-array parameters
		f1Panel.setNumber(0.1);
		f2Panel.setNumber(-0.1);
		minimumZSeprarationPanel.setNumber(0.00001);
		period1Panel.setNumber(0.0099);
		period2Panel.setNumber(0.01);
		theta1DegPanel.setNumber(0);
		theta2DegPanel.setNumber(0);
		phi1DegPanel.setNumber(0);
		phi2DegPanel.setNumber(0);
		showLensletArray1CheckBox.setSelected(true);
		showLensletArray2CheckBox.setSelected(true);
		showFieldLensArrayCheckBox.setSelected(false);
	}

	/**
	 * initialise the lenslet-array parameters to a Moire magnifier
	 */
	private void moireInitialisation()
	{
		// lenslet-array parameters
		f1Panel.setNumber(-0.1);
		f2Panel.setNumber(0.1);
		minimumZSeprarationPanel.setNumber(0.00001);
		period1Panel.setNumber(0.01);
		period2Panel.setNumber(0.01);
		theta1DegPanel.setNumber(0);
		theta2DegPanel.setNumber(0);
		phi1DegPanel.setNumber(0);
		phi2DegPanel.setNumber(0.1);
		showLensletArray1CheckBox.setSelected(true);
		showLensletArray2CheckBox.setSelected(true);
		showFieldLensArrayCheckBox.setSelected(false);
	}

	/**
	 * initialise the lenslet-array parameters to a Moire magnifier
	 */
	private void clasInitialisation()
	{
		// lenslet-array parameters
		f1Panel.setNumber(-0.05);
		f2Panel.setNumber(0.1);
		minimumZSeprarationPanel.setNumber(0.00001);
		period1Panel.setNumber(0.01);
		period2Panel.setNumber(0.01);
		theta1DegPanel.setNumber(0);
		theta2DegPanel.setNumber(0);
		phi1DegPanel.setNumber(0);
		phi2DegPanel.setNumber(0);
		showLensletArray1CheckBox.setSelected(true);
		showLensletArray2CheckBox.setSelected(true);
		showFieldLensArrayCheckBox.setSelected(false);
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		super.actionPerformed(e);
		
		if(e.getSource().equals(lensletArraysInitialisationComboBox))
		{
			switch((LensletArraysInitialisationType)(lensletArraysInitialisationComboBox.getSelectedItem()))
			{
			case GABOR:
				gaborInitialisation();
				break;
			case MOIRE:
				moireInitialisation();
				break;
			case CLAS:
				clasInitialisation();
				break;
			case INIT:
			default:
				// do nothing
			}
			lensletArraysInitialisationComboBox.setSelectedItem(LensletArraysInitialisationType.INIT);
		}
		else if(e.getSource().equals(setObjectZ2LastClickZButton))
		{
			objectZPanel.setNumber(getLastClickIntersection().p.z);
		}
		
//		if(e.getSource().equals(gaborInitialisationButton))
//		{
//			gaborInitialisation();
//		}
//		else if(e.getSource().equals(moireInitialisationButton))
//		{
//			moireInitialisation();
//		}
//		else if(e.getSource().equals(clasInitialisationButton))
//		{
//			clasInitialisation();
//		}
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
		(new LensletArrayExplorer()).run();
	}
}
