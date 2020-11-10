package optics.raytrace.research.adaptiveTWs;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintStream;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import math.*;
import net.miginfocom.swing.MigLayout;
import optics.raytrace.sceneObjects.ScaledParametrisedParallelogram;
import optics.raytrace.sceneObjects.SceneObjectWithHoles;
import optics.raytrace.sceneObjects.SparseRectangularLensletArray;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.surfaces.PhaseHologramOfSparseRectangularLensletArray;
import optics.raytrace.surfaces.SemiTransparent;
import optics.raytrace.surfaces.SurfaceColour;
import optics.raytrace.exceptions.SceneException;
import optics.raytrace.NonInteractiveTIMActionEnum;
import optics.raytrace.NonInteractiveTIMEngine;
import optics.raytrace.GUI.cameras.EditableRelativisticAnyFocusSurfaceCamera;
import optics.raytrace.GUI.cameras.RenderQualityEnum;
import optics.raytrace.GUI.lowLevel.ApertureSizeType;
import optics.raytrace.GUI.lowLevel.DoublePanel;
import optics.raytrace.GUI.lowLevel.GUIBitsAndBobs;
import optics.raytrace.GUI.lowLevel.IntPanel;
import optics.raytrace.GUI.lowLevel.LabelledDoublePanel;
import optics.raytrace.GUI.lowLevel.LabelledIntPanel;
import optics.raytrace.GUI.lowLevel.LabelledVector3DPanel;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.Studio;
import optics.raytrace.core.StudioInitialisationType;


/**
 * Simulate the visual appearance of adaptive TWs.
 * 
 * @author Johannes Courtial
 */
public class AdaptiveTWsExplorer extends NonInteractiveTIMEngine implements ActionListener
{
	/**
	 * eta_1
	 */
	private double eta1;
	
	/**
	 * width of the individual lenses
	 */
	private double lensWidth;

	/**
	 * period (in lens widths) of each lens array
	 */
	private int period;

	/**
	 * focal length of 0th lens in lens array 1
	 */
	private double f01;
	
	/**
	 * focal length of 0th lens in lens array 2
	 */
	private double f02;
	
	/**
	 * if true, black out <noOfBlackedOutLenses> lens per period in lenslet array 2
	 */
	private boolean blackOutLenses;
	
	/**
	 * if <blackOutLenses>=true, black out <noOfBlackedOutLenses> lens per period in lenslet array 2
	 */
	private int noOfBlackedOutLenses;

	/**
	 * show lenslet array 1
	 */
	private boolean showLensletArray1;
	
	private boolean showCommonFocalPlane;

	/**
	 * show lenslet array 2
	 */
	private boolean showLensletArray2;
	
	/**
	 * offset of lens array 1 relative to lens array 2
	 */
	private Vector3D offset1;
	


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
	public AdaptiveTWsExplorer()
	{
		super();
		
		// set to true for interactive version
		nonInteractiveTIMAction = NonInteractiveTIMActionEnum.INTERACTIVE;
		
		// set all parameters
		
		renderQuality = RenderQualityEnum.DRAFT;
		
		// lenslet-array parameters
		eta1 = 0.5;
		f01 = 0.1;
		f02 = -0.1;
		lensWidth = 0.01;
		period = 4;
		blackOutLenses = true;
		noOfBlackedOutLenses = 1;
		showLensletArray1 = true;
		showLensletArray2 = true;
		showCommonFocalPlane = false;
		offset1 = new Vector3D(0, 0, 0.00001);
		
		studioInitialisation = StudioInitialisationType.TIM_HEAD;	// the backdrop

		// camera
		cameraViewCentre = new Vector3D(0, 0, 0);
		cameraDistance = 10;
		cameraViewDirection = new Vector3D(0, 0, 1);
		cameraHorizontalFOVDeg = 10;
		cameraApertureSize = ApertureSizeType.PINHOLE;
		cameraFocussingDistance = 20;
		
		if(nonInteractiveTIMAction == NonInteractiveTIMActionEnum.INTERACTIVE)
		{
			windowTitle = "Dr TIM's adaptive-TWs explorer";
			windowWidth = 1500;
			windowHeight = 650;
		}
	}
	

	@Override
	public String getFirstPartOfFilename()
	{
		return "AdaptiveTWsExplorer";	// the name
	}
	
	@Override
	public void writeParameters(PrintStream printStream)
	{
		// super.writeParameters(printStream);
		
		printStream.println("renderQuality = "+renderQuality);

		printStream.println();
		printStream.println("Scene parameters");
		printStream.println();

		printStream.println("eta1 = "+eta1);
		printStream.println("f01 = "+f01);
		printStream.println("f02 = "+f02);
		printStream.println("lensWidth = "+lensWidth);
		printStream.println("period = "+period);
		printStream.println("blackOutLenses = "+blackOutLenses);
		printStream.println("noOfBlackedOutLenses = "+noOfBlackedOutLenses);
		printStream.println("showLensletArray1 = "+showLensletArray1);
		printStream.println("showLensletArray2 = "+showLensletArray2);
		printStream.println("offset1 = "+offset1);
		printStream.println("showCommonFocalPlane = "+ showCommonFocalPlane);
		
		studioInitialisation = StudioInitialisationType.TIM_HEAD;	// the backdrop

		printStream.println();
		printStream.println("Camera parameters");
		printStream.println();

		// camera
		printStream.println("cameraViewCentre = "+cameraViewCentre);
		printStream.println("cameraDistance = "+cameraDistance);
		printStream.println("cameraViewDirection = "+cameraViewDirection);
		printStream.println("cameraHorizontalFOVDeg = "+cameraHorizontalFOVDeg);
		printStream.println("cameraApertureSize = "+cameraApertureSize);
		printStream.println("cameraFocussingDistance = "+cameraFocussingDistance);
	}

	
	public SceneObjectWithHoles createBlackStripes(
			String description,
			Vector3D corner, 
			Vector3D spanVector1,
			Vector3D spanVector2, 
			double xPeriod,
			double yPeriod,
			double xOffset,
			double yOffset,
			int nx,
			int ny,
			SceneObject parent,
			Studio studio
		)
	{
		SceneObjectWithHoles s = new SceneObjectWithHoles(description + " (holey)");
		
		// create the wrapped SceneObject
		
		ScaledParametrisedParallelogram parallelogram = new ScaledParametrisedParallelogram(
				description,
				corner, 
				spanVector1,
				spanVector2, 
				0, spanVector1.getLength(),	// suMin, suMax
				0, spanVector2.getLength(),	// svMin, svMax
				SurfaceColour.BLACK_MATT,	// surfaceProperty
				parent,
				studio
			);

		// create a HoleySurface...
		PhaseHologramOfSparseRectangularLensletArray sparseLensArray = new PhaseHologramOfSparseRectangularLensletArray(
					1,
					xPeriod,
					yPeriod,
					xOffset,
					yOffset,
					nx,
					ny,
					parallelogram,	// sceneObject
					1,
					false,
					true
				);
		
		try {
			s.setWrappedSceneObject(parallelogram);
		} catch (SceneException e) {
			// this shouldn't happen
			e.printStackTrace();
		}
		s.setHoleySurface(sparseLensArray);

		return s;
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
		Vector3D normal = new Vector3D(0, 0, 1);
		Vector3D right = Vector3D.crossProduct(up, normal);
		
		// add nx arrays
		double fn1 = f01;
		double fn2 = f02;
		for(int n = 0; n<period; n++)
		{
			if(blackOutLenses && (n < noOfBlackedOutLenses))
			{
				scene.addSceneObject(
						createBlackStripes(
								"LA2, baffle instead of lens "+n,	// description,
								new Vector3D(-0.5+n*lensWidth, -0.5, -fn2),	// corner
								right,	// spanVector1
								up,	// spanVector2
								lensWidth,	// xPeriod
								lensWidth,	// yPeriod
								0.5*lensWidth,	// xOffset
								0.5*lensWidth,	// yOffset
								period,	// nx
								1,	// ny
								scene,	// parent,
								studio
								),
						showLensletArray2
						);
			}
			else
			{
				scene.addSceneObject(
						new SparseRectangularLensletArray(
								"LA2, lens "+n,	// description
								new Vector3D(-0.5+n*lensWidth, -0.5, -fn2),	//.getSumWith(offset1),	// corner
								right,	// spanVector1
								up,	// spanVector2
								fn2,	// focalLength
								lensWidth,	// xPeriod
								lensWidth,	// yPeriod
								0.5*lensWidth,	// xOffset
								0.5*lensWidth,	// yOffset
								period,	// nx
								1,	// ny
								0.96,	// throughputCoefficient
								false,	// reflective
								true,	// shadowThrowing
								scene,	// parent
								studio
								), 
						showLensletArray2
						);
			}

			scene.addSceneObject(
					new SparseRectangularLensletArray(
							"LA1, lens "+n,	// description
							new Vector3D(-0.5+n*lensWidth, -0.5, fn1).getSumWith(offset1),	// corner
							right,	// spanVector1
							up,	// spanVector2
							fn1,	// focalLength
							lensWidth,	// xPeriod
							lensWidth,	// yPeriod
							0.5*lensWidth,	// xOffset
							0.5*lensWidth,	// yOffset
							period,	// nx
							1,	// ny
							0.96,	// throughputCoefficient
							false,	// reflective
							true,	// shadowThrowing
							scene,	// parent
							studio
							), 
					showLensletArray1
					);
			

			
			fn1 *= eta1;
			fn2 *= eta1;
		}
		
		// show focal plane
		
		scene.addSceneObject(new ScaledParametrisedParallelogram(
				"Common focal plane",
				new Vector3D(-0.5, -0.5, 0),	// corner 
				right,	// spanVector1
				up,	// spanVector2
				SemiTransparent.RED_SHINY_SEMITRANSPARENT,	// surfaceProperty
				scene,	// parent
				studio
				),
				showCommonFocalPlane
				);



		// the camera
		EditableRelativisticAnyFocusSurfaceCamera camera = getStandardCamera();
		camera.setApertureRadius(cameraApertureSize.getApertureRadius()/10.*cameraFocussingDistance);
		studio.setCamera(camera);
	}

	
	
	//
	// for interactive version
	//
	
	public enum ParametersInitialisationType
	{
		INIT("Initialise parameters..."),
		JUST_TIM("No lens arrays, camera focussed on TIM"),
		MAGNIFYING_LARGE_LENSES_FOCUS_ON_TIM("Magnifying, large lenses, camera focussed on image of TIM"),
		MAGNIFYING_SMALL_LENSES_FOCUS_ON_TIM("Magnifying, small lenses, camera focussed on image of TIM"),
		DEMAGNIFYING_LARGE_LENSES_FOCUS_ON_TIM("De-magnifying, large lenses, camera focussed on image of TIM"),
		DEMAGNIFYING_SMALL_LENSES_FOCUS_ON_TIM("De-magnifying, small lenses, camera focussed on image of TIM"),
		SHOW_LAS_AND_TIM("Initialise camera parameters to show lenses and TIM"),
		SHOW_LAS_FROM_ABOVE("Initialise camera parameters to show LAs from above");
		
		private String description;
		private ParametersInitialisationType(String description) {this.description = description;}	
		@Override
		public String toString() {return description;}
	}
	
	private JComboBox<ParametersInitialisationType> parametersInitialisationComboBox;

	private LabelledDoublePanel f01Panel, f02Panel, eta1Panel, lensWidthPanel;
	private LabelledVector3DPanel offset1Panel;
	private LabelledIntPanel periodPanel;
	private IntPanel noOfBlackedOutLensesPanel;
	private JCheckBox showLensletArray1CheckBox, showLensletArray2CheckBox, blackOutLensesCheckBox, showCommonFocalPlaneCheckBox;
	// private JComboBox<LensletArraysInitialisationType> lensletArraysInitialisationComboBox;
	private JComboBox<StudioInitialisationType> studioInitialisationComboBox;
	// private JButton lensletArraysInitialisationButton;	// gaborInitialisationButton, moireInitialisationButton, clasInitialisationButton;

	// camera stuff
	
	// private LabelledVector3DPanel cameraViewDirectionPanel;
	private LabelledDoublePanel cameraDistancePanel;
	private LabelledVector3DPanel cameraViewCentrePanel, cameraViewDirectionPanel;
	private DoublePanel cameraHorizontalFOVDegPanel;
	private JComboBox<ApertureSizeType> cameraApertureSizeComboBox;
	private LabelledDoublePanel cameraFocussingDistancePanel;
	// private JComboBox<CameraInitialisationType> cameraInitialisationComboBox;




	
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
		
		periodPanel = new LabelledIntPanel("Period (# lenses)");
		periodPanel.setNumber(period);
		periodPanel.setToolTipText("Only every n_xth lenslet is present in the x direction");		
		scenePanel.add(periodPanel, "span");
		
		eta1Panel = new LabelledDoublePanel("eta_1");
		eta1Panel.setNumber(eta1);
		eta1Panel.setToolTipText("eta1");
		scenePanel.add(eta1Panel, "span");

		lensWidthPanel = new LabelledDoublePanel("Width of individual lenses");
		lensWidthPanel.setNumber(lensWidth);
		lensWidthPanel.setToolTipText("Width of individual lenses");
		scenePanel.add(lensWidthPanel, "span");

		// lenslet array 1
		
		JPanel LA1Panel = new JPanel();
		LA1Panel.setLayout(new MigLayout("insets 0"));
		LA1Panel.setBorder(GUIBitsAndBobs.getTitledBorder("Lenslet array 1"));
		scenePanel.add(LA1Panel, "span");

		showLensletArray1CheckBox = new JCheckBox("Show");
		showLensletArray1CheckBox.setSelected(showLensletArray1);
		showLensletArray1CheckBox.setToolTipText("Determines whether or not lenslet array 1 is rendered");
		LA1Panel.add(showLensletArray1CheckBox, "span");

		f01Panel = new LabelledDoublePanel("f01");
		f01Panel.setNumber(f01);
		f01Panel.setToolTipText("Focal length of 0th lenslet in lenslet array 1");
		LA1Panel.add(f01Panel, "span");
		
		offset1Panel = new LabelledVector3DPanel("Offset of LA1");
		offset1Panel.setVector3D(offset1);
		offset1Panel.setToolTipText("Offset of LA1");
		LA1Panel.add(offset1Panel, "span");
		
		// lenslet array 2
		
		JPanel LA2Panel = new JPanel();
		LA2Panel.setLayout(new MigLayout("insets 0"));
		LA2Panel.setBorder(GUIBitsAndBobs.getTitledBorder("Lenslet array 2"));
		scenePanel.add(LA2Panel, "span");

		showLensletArray2CheckBox = new JCheckBox("Show");
		showLensletArray2CheckBox.setSelected(showLensletArray2);
		LA2Panel.add(showLensletArray2CheckBox, "span");

		f02Panel = new LabelledDoublePanel("f02");
		f02Panel.setNumber(f02);
		f02Panel.setToolTipText("Focal length of 0th lenslet in lenslet array 2");
		LA2Panel.add(f02Panel, "span");
		
		blackOutLensesCheckBox = new JCheckBox();
		blackOutLensesCheckBox.setSelected(blackOutLenses);
		blackOutLensesCheckBox.setToolTipText("If checked, black out lenses in lenslet array 2");
		noOfBlackedOutLensesPanel = new IntPanel();
		noOfBlackedOutLensesPanel.setNumber(noOfBlackedOutLenses);
		noOfBlackedOutLensesPanel.setToolTipText("No of lenses in each period to be blacked out in lenslet array 2");
		LA2Panel.add(GUIBitsAndBobs.makeRow(blackOutLensesCheckBox, "Black out", noOfBlackedOutLensesPanel, "lens(es) per period in lenslet array 2"), "span");
		
		showCommonFocalPlaneCheckBox = new JCheckBox("Show common focal plane");
		showCommonFocalPlaneCheckBox.setSelected(showCommonFocalPlane);
		showCommonFocalPlaneCheckBox.setToolTipText("Check to show the common focal plane");
		scenePanel.add(showCommonFocalPlaneCheckBox, "span");
		
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
				
		cameraViewCentrePanel = new LabelledVector3DPanel("View centre");
		cameraViewCentrePanel.setVector3D(cameraViewCentre);
		cameraPanel.add(cameraViewCentrePanel, "span");
		

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
		cameraPanel.add(GUIBitsAndBobs.makeRow("Camera aperture size (scaled by focussing distance)", cameraApertureSizeComboBox), "span");		
		
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
		
		f01 = f01Panel.getNumber();
		f02 = f02Panel.getNumber();
		eta1 = eta1Panel.getNumber();
		lensWidth = lensWidthPanel.getNumber();
		period = periodPanel.getNumber();
		offset1 = offset1Panel.getVector3D();
		blackOutLenses = blackOutLensesCheckBox.isSelected();
		noOfBlackedOutLenses = noOfBlackedOutLensesPanel.getNumber();
		
		showLensletArray1 = showLensletArray1CheckBox.isSelected();
		showLensletArray2 = showLensletArray2CheckBox.isSelected();
		showCommonFocalPlane = showCommonFocalPlaneCheckBox.isSelected();
		studioInitialisation = (StudioInitialisationType)(studioInitialisationComboBox.getSelectedItem());
		
		// cameraViewDirection = cameraViewDirectionPanel.getVector3D();
		cameraViewCentre = cameraViewCentrePanel.getVector3D();
		cameraDistance = cameraDistancePanel.getNumber();
		cameraViewDirection = cameraViewDirectionPanel.getVector3D();
		cameraHorizontalFOVDeg = cameraHorizontalFOVDegPanel.getNumber();
		cameraApertureSize = (ApertureSizeType)(cameraApertureSizeComboBox.getSelectedItem());
		cameraFocussingDistance = cameraFocussingDistancePanel.getNumber();
	}
	

	@Override
	public void actionPerformed(ActionEvent e)
	{
		super.actionPerformed(e);
		
		if(e.getSource().equals(parametersInitialisationComboBox))
		{
			double lensWidth1, eta1, cameraDistance1 = 5;
			switch((ParametersInitialisationType)(parametersInitialisationComboBox.getSelectedItem()))
			{
			case JUST_TIM:
				blackOutLensesCheckBox.setSelected(false);
				showLensletArray1CheckBox.setSelected(false);
				showLensletArray2CheckBox.setSelected(false);
				studioInitialisationComboBox.setSelectedItem(StudioInitialisationType.TIM_HEAD);
				
				cameraViewCentrePanel.setVector3D(new Vector3D(0, 0, 0));
				cameraDistancePanel.setNumber(cameraDistance1);
				cameraViewDirectionPanel.setVector3D(new Vector3D(0, 0, 1));
				cameraHorizontalFOVDegPanel.setNumber(10);
				cameraApertureSizeComboBox.setSelectedItem(ApertureSizeType.MEDIUM);
				cameraFocussingDistancePanel.setNumber(cameraDistance1+9);

				break;
			case MAGNIFYING_SMALL_LENSES_FOCUS_ON_TIM:
				lensWidth1 = 0.002;
				eta1 = 0.5;
				lensWidthPanel.setNumber(lensWidth1);
				f01Panel.setNumber(5*lensWidth1);
				f02Panel.setNumber(-f01Panel.getNumber());
				eta1Panel.setNumber(eta1);
				periodPanel.setNumber(3);
				offset1Panel.setVector3D(new Vector3D(lensWidth1, 0, 0.00001));
				blackOutLensesCheckBox.setSelected(true);
				noOfBlackedOutLensesPanel.setNumber(1);
				showLensletArray1CheckBox.setSelected(true);
				showLensletArray2CheckBox.setSelected(true);
				studioInitialisationComboBox.setSelectedItem(StudioInitialisationType.TIM_HEAD);
				
				cameraViewCentrePanel.setVector3D(new Vector3D(0, 0, 0));
				cameraDistancePanel.setNumber(cameraDistance1);
				cameraViewDirectionPanel.setVector3D(new Vector3D(0, 0, 1));
				cameraHorizontalFOVDegPanel.setNumber(17);
				cameraApertureSizeComboBox.setSelectedItem(ApertureSizeType.MEDIUM);
				cameraFocussingDistancePanel.setNumber(cameraDistance1+eta1*9);

				break;
			case MAGNIFYING_LARGE_LENSES_FOCUS_ON_TIM:
				lensWidth1 = 0.05;
				eta1 = 0.5;
				lensWidthPanel.setNumber(lensWidth1);
				f01Panel.setNumber(5*lensWidth1);
				f02Panel.setNumber(-f01Panel.getNumber());
				eta1Panel.setNumber(eta1);
				periodPanel.setNumber(3);
				offset1Panel.setVector3D(new Vector3D(lensWidth1, 0, 0.00001));
				blackOutLensesCheckBox.setSelected(true);
				noOfBlackedOutLensesPanel.setNumber(1);
				showLensletArray1CheckBox.setSelected(true);
				showLensletArray2CheckBox.setSelected(true);
				studioInitialisationComboBox.setSelectedItem(StudioInitialisationType.TIM_HEAD);
				
				cameraViewCentrePanel.setVector3D(new Vector3D(0, 0, 0));
				cameraDistancePanel.setNumber(cameraDistance1);
				cameraViewDirectionPanel.setVector3D(new Vector3D(0, 0, 1));
				cameraHorizontalFOVDegPanel.setNumber(17);
				cameraApertureSizeComboBox.setSelectedItem(ApertureSizeType.MEDIUM);
				cameraFocussingDistancePanel.setNumber(cameraDistance1+eta1*9);

				break;
			case DEMAGNIFYING_SMALL_LENSES_FOCUS_ON_TIM:
				lensWidth1 = 0.002;	// 0.001;
				eta1 = 2;	// 0.5;
				lensWidthPanel.setNumber(lensWidth1);
				f01Panel.setNumber(-2*lensWidth1);
				f02Panel.setNumber(-f01Panel.getNumber());
				eta1Panel.setNumber(eta1);
				periodPanel.setNumber(3);
				offset1Panel.setVector3D(new Vector3D(lensWidth1, 0, 0.00001));
				blackOutLensesCheckBox.setSelected(true);
				noOfBlackedOutLensesPanel.setNumber(1);
				showLensletArray1CheckBox.setSelected(true);
				showLensletArray2CheckBox.setSelected(true);
				studioInitialisationComboBox.setSelectedItem(StudioInitialisationType.TIM_HEAD);
				
				cameraViewCentrePanel.setVector3D(new Vector3D(0, 0, 0));
				cameraDistancePanel.setNumber(cameraDistance1);
				cameraViewDirectionPanel.setVector3D(new Vector3D(0, 0, 1));
				cameraHorizontalFOVDegPanel.setNumber(17);
				cameraApertureSizeComboBox.setSelectedItem(ApertureSizeType.MEDIUM);
				cameraFocussingDistancePanel.setNumber(cameraDistance1+eta1*9);

				break;
			case DEMAGNIFYING_LARGE_LENSES_FOCUS_ON_TIM:
				lensWidth1 = 0.05;	// 0.001;
				eta1 = 2;	// 0.5;
				lensWidthPanel.setNumber(lensWidth1);
				f01Panel.setNumber(-2*lensWidth1);
				f02Panel.setNumber(-f01Panel.getNumber());
				eta1Panel.setNumber(eta1);
				periodPanel.setNumber(3);
				offset1Panel.setVector3D(new Vector3D(lensWidth1, 0, 0.00001));
				blackOutLensesCheckBox.setSelected(true);
				noOfBlackedOutLensesPanel.setNumber(1);
				showLensletArray1CheckBox.setSelected(true);
				showLensletArray2CheckBox.setSelected(true);
				studioInitialisationComboBox.setSelectedItem(StudioInitialisationType.TIM_HEAD);
				
				cameraViewCentrePanel.setVector3D(new Vector3D(0, 0, 0));
				cameraDistancePanel.setNumber(cameraDistance1);
				cameraViewDirectionPanel.setVector3D(new Vector3D(0, 0, 1));
				cameraHorizontalFOVDegPanel.setNumber(17);
				cameraApertureSizeComboBox.setSelectedItem(ApertureSizeType.MEDIUM);
				cameraFocussingDistancePanel.setNumber(cameraDistance1+eta1*9);

				break;
			case SHOW_LAS_FROM_ABOVE:
				cameraViewCentrePanel.setVector3D(new Vector3D(0, 0, 0.5*f01Panel.getNumber()));
				cameraDistancePanel.setNumber(2);
				cameraViewDirectionPanel.setVector3D(new Vector3D(0.02, -1, .2));
				cameraHorizontalFOVDegPanel.setNumber(15);
				cameraApertureSizeComboBox.setSelectedItem(ApertureSizeType.TINY);
				cameraFocussingDistancePanel.setNumber(1.5);
				
				break;
			case SHOW_LAS_AND_TIM:
				cameraViewCentrePanel.setVector3D(new Vector3D(0, 0.5, 0));	// 0.5*f1Panel.getNumber()));
				cameraDistancePanel.setNumber(0.25);
				cameraViewDirectionPanel.setVector3D(new Vector3D(0, -.8, 1));
				cameraHorizontalFOVDegPanel.setNumber(110);
				cameraApertureSizeComboBox.setSelectedItem(ApertureSizeType.LARGE);
				cameraFocussingDistancePanel.setNumber(0.25);
				
				break;
			case INIT:
			default:
				// do nothing
			}
			parametersInitialisationComboBox.setSelectedItem(ParametersInitialisationType.INIT);
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
		(new AdaptiveTWsExplorer()).run();
	}
}
