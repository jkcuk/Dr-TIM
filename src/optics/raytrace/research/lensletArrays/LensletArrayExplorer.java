package optics.raytrace.research.lensletArrays;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import math.*;
import net.miginfocom.swing.MigLayout;
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
	 * focal length of lenslet array 1
	 */
	private double f1;
	
	/**
	 * focal length of lenslet array 2
	 */
	private double f2;
	
	/**
	 * pitch of lenslet array 1
	 */
	private double pitch1;

	/**
	 * pitch of lenslet array 2
	 */
	private double pitch2;

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
	 * z separation of LA centres = f1+f2+dz
	 */
	private double dz;

	/**
	 * show lenslet array 1
	 */
	private boolean showLensletArray1;

	/**
	 * show lenslet array 2
	 */
	private boolean showLensletArray2;

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
		dz = 0.00001;
		pitch1 = 0.01;
		pitch2 = 0.01;
		theta1Deg = 0;
		theta2Deg = 0;
		phi1Deg = 0;
		phi2Deg = 0;
		showLensletArray1 = true;
		showLensletArray2 = true;
		
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
				+ (showLensletArray1?
						" LA1 theta="+theta1Deg+"deg"
						+" phi="+phi1Deg+"deg"
						+" f="+f1
						+" pitch="+pitch1
						:"")
				+ (showLensletArray2?
						" LA2 theta="+theta2Deg+"deg"
						+" phi="+phi2Deg+"deg"
						+" f="+f2
						+" pitch="+pitch2
						:"")
				+ " dz="+dz
				+ " backdrop="+studioInitialisation.toString()
				+ " cD="+cameraDistance
				+ " cVD="+cameraViewDirection
				+ " cFOV="+cameraHorizontalFOVDeg
				+ " cAS="+cameraApertureSize
				+ " cFD="+cameraFocussingDistance
				;
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
		double zSeparation = f1+f2+dz;
		
		double theta1 = MyMath.deg2rad(theta1Deg);
		double phi1 = MyMath.deg2rad(phi1Deg);
		Vector3D normal1 = new Vector3D(Math.sin(theta1), 0, Math.cos(theta1));
		Vector3D right1 = Vector3D.crossProduct(normal1, up);
		double c1 = Math.cos(phi1);
		double s1 = Math.sin(phi1);
		
		scene.addSceneObject(new EditableRectangularLensletArray(
				"LA1",	// description
				new Vector3D(0, 0, -0.5*zSeparation),	// centre
				Vector3D.sum(right1.getProductWith(c1), up.getProductWith(s1)),	// spanVector1
				Vector3D.sum(right1.getProductWith(-s1), up.getProductWith(c1)),	// spanVector2
				f1,	// focalLength
				pitch1,	// xPeriod
				pitch1,	// yPeriod
				0,	// xOffset
				0,	// yOffset
				0.96,	// throughputCoefficient
				false,	// reflective
				true,	// shadowThrowing
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
				new Vector3D(0, 0, +0.5*zSeparation),	// centre
				Vector3D.sum(right2.getProductWith(c2), up.getProductWith(s2)),	// spanVector1
				Vector3D.sum(right2.getProductWith(-s2), up.getProductWith(c2)),	// spanVector2
				f2,	// focalLength
				pitch2,	// xPeriod
				pitch2,	// yPeriod
				0,	// xOffset
				0,	// yOffset
				0.96,	// throughputCoefficient
				false,	// reflective
				true,	// shadowThrowing
				scene,	// parent
				studio
			), 
			showLensletArray2
		);

		// the camera
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

	
	private LabelledDoublePanel f1Panel, f2Panel, dzPanel, pitch1Panel, pitch2Panel;
	private DoublePanel theta1DegPanel, theta2DegPanel, phi1DegPanel, phi2DegPanel;
	private JCheckBox showLensletArray1CheckBox, showLensletArray2CheckBox;
	private JComboBox<LensletArraysInitialisationType> lensletArraysInitialisationComboBox;
	private JComboBox<StudioInitialisationType> studioInitialisationComboBox;
	// private JButton lensletArraysInitialisationButton;	// gaborInitialisationButton, moireInitialisationButton, clasInitialisationButton;

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
		
		lensletArraysInitialisationComboBox = new JComboBox<LensletArraysInitialisationType>(LensletArraysInitialisationType.values());
		lensletArraysInitialisationComboBox.setSelectedItem(LensletArraysInitialisationType.INIT);
		lensletArraysInitialisationComboBox.addActionListener(this);
//		lensletArraysInitialisationButton = new JButton("Go");
//		lensletArraysInitialisationButton.addActionListener(this);
		scenePanel.add(
//				GUIBitsAndBobs.makeRow("Initialise LAs to", 
				lensletArraysInitialisationComboBox
	//			, lensletArraysInitialisationButton)
			, "span");


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
		la1Panel.setBorder(GUIBitsAndBobs.getTitledBorder("Lenslet array 1"));
		la1Panel.setLayout(new MigLayout("insets 0"));
		scenePanel.add(la1Panel, "span");

		f1Panel = new LabelledDoublePanel("f");
		f1Panel.setNumber(f1);
		f1Panel.setToolTipText("Focal length of the lenslets");
		la1Panel.add(f1Panel, "span");
		
		pitch1Panel = new LabelledDoublePanel("pitch");
		pitch1Panel.setNumber(pitch1);
		pitch1Panel.setToolTipText("Pitch, i.e. distance between neighbouring lenslets");
		la1Panel.add(pitch1Panel, "span");

		theta1DegPanel = new DoublePanel();
		theta1DegPanel.setNumber(theta1Deg);
		theta1DegPanel.setToolTipText("Angle of the array normal with the z axis");
		la1Panel.add(GUIBitsAndBobs.makeRow("theta", theta1DegPanel, "degrees"), "span");

		phi1DegPanel = new DoublePanel();
		phi1DegPanel.setNumber(phi1Deg);
		phi1DegPanel.setToolTipText("Angle by which the array is rotated around the array normal");
		la1Panel.add(GUIBitsAndBobs.makeRow("phi", phi1DegPanel, "degrees"), "span");

		showLensletArray1CheckBox = new JCheckBox("Show");
		showLensletArray1CheckBox.setSelected(showLensletArray1);
		la1Panel.add(showLensletArray1CheckBox, "span");
		


		//
		// the LA2 panel
		//
		
		JPanel la2Panel = new JPanel();
		la2Panel.setBorder(GUIBitsAndBobs.getTitledBorder("Lenslet array 2"));
		la2Panel.setLayout(new MigLayout("insets 0"));
		scenePanel.add(la2Panel, "span");
		
		f2Panel = new LabelledDoublePanel("f");
		f2Panel.setNumber(f2);
		f2Panel.setToolTipText("Focal length of the lenslets");
		la2Panel.add(f2Panel, "span");
		
		pitch2Panel = new LabelledDoublePanel("pitch");
		pitch2Panel.setNumber(pitch2);
		pitch2Panel.setToolTipText("Pitch, i.e. distance between neighbouring lenslets");
		la2Panel.add(pitch2Panel, "span");

		theta2DegPanel = new DoublePanel();
		theta2DegPanel.setNumber(theta2Deg);
		theta2DegPanel.setToolTipText("Angle of the array normal with the z axis");
		la2Panel.add(GUIBitsAndBobs.makeRow("theta", theta2DegPanel, "degrees"), "span");

		phi2DegPanel = new DoublePanel();
		phi2DegPanel.setNumber(phi2Deg);
		phi2DegPanel.setToolTipText("Angle by which the array is rotated around the array normal");
		la2Panel.add(GUIBitsAndBobs.makeRow("phi", phi2DegPanel, "degrees"), "span");

		showLensletArray2CheckBox = new JCheckBox("Show");
		showLensletArray2CheckBox.setSelected(showLensletArray2);
		la2Panel.add(showLensletArray2CheckBox, "span");


		dzPanel = new LabelledDoublePanel("dz (centre separation = f1+f2+dz)");
		dzPanel.setNumber(dz);
		dzPanel.setToolTipText("The centres of the two lenslet arrays are separated in the z direction by a distance f1 + f2 + dz");
		scenePanel.add(dzPanel, "span");
		
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
		
		f1 = f1Panel.getNumber();
		f2 = f2Panel.getNumber();
		dz = dzPanel.getNumber();
		pitch1 = pitch1Panel.getNumber();
		pitch2 = pitch2Panel.getNumber();
		theta1Deg = theta1DegPanel.getNumber();
		theta2Deg = theta2DegPanel.getNumber();
		phi1Deg = phi1DegPanel.getNumber();
		phi2Deg = phi2DegPanel.getNumber();
		
		showLensletArray1 = showLensletArray1CheckBox.isSelected();
		showLensletArray2 = showLensletArray2CheckBox.isSelected();
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
		dzPanel.setNumber(0.00001);
		pitch1Panel.setNumber(0.01);
		pitch2Panel.setNumber(0.0099);
		theta1DegPanel.setNumber(0);
		theta2DegPanel.setNumber(0);
		phi1DegPanel.setNumber(0);
		phi2DegPanel.setNumber(0);
		showLensletArray1CheckBox.setSelected(true);
		showLensletArray2CheckBox.setSelected(true);
	}

	/**
	 * initialise the lenslet-array parameters to a Moire magnifier
	 */
	private void moireInitialisation()
	{
		// lenslet-array parameters
		f1Panel.setNumber(0.1);
		f2Panel.setNumber(-0.1);
		dzPanel.setNumber(0.00001);
		pitch1Panel.setNumber(0.01);
		pitch2Panel.setNumber(0.01);
		theta1DegPanel.setNumber(0);
		theta2DegPanel.setNumber(0);
		phi1DegPanel.setNumber(0);
		phi2DegPanel.setNumber(0.1);
		showLensletArray1CheckBox.setSelected(true);
		showLensletArray2CheckBox.setSelected(true);
	}

	/**
	 * initialise the lenslet-array parameters to a Moire magnifier
	 */
	private void clasInitialisation()
	{
		// lenslet-array parameters
		f1Panel.setNumber(0.1);
		f2Panel.setNumber(-0.05);
		dzPanel.setNumber(0.00001);
		pitch1Panel.setNumber(0.01);
		pitch2Panel.setNumber(0.01);
		theta1DegPanel.setNumber(0);
		theta2DegPanel.setNumber(0);
		phi1DegPanel.setNumber(0);
		phi2DegPanel.setNumber(0);
		showLensletArray1CheckBox.setSelected(true);
		showLensletArray2CheckBox.setSelected(true);
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
