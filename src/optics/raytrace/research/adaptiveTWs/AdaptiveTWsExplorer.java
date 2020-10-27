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
import optics.raytrace.sceneObjects.SparseRectangularLensletArray;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.exceptions.SceneException;
import optics.raytrace.NonInteractiveTIMActionEnum;
import optics.raytrace.NonInteractiveTIMEngine;
import optics.raytrace.GUI.cameras.RenderQualityEnum;
import optics.raytrace.GUI.lowLevel.ApertureSizeType;
import optics.raytrace.GUI.lowLevel.DoublePanel;
import optics.raytrace.GUI.lowLevel.GUIBitsAndBobs;
import optics.raytrace.GUI.lowLevel.IntPanel;
import optics.raytrace.GUI.lowLevel.LabelledDoublePanel;
import optics.raytrace.GUI.lowLevel.LabelledVector3DPanel;
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
	 * focal length of 0th lens in array with positive focal lengths
	 */
	private double f0;
	
	/**
	 * eta_1
	 */
	private double eta1;
	
	/**
	 * pitch of lenslet arrays
	 */
	private double pitch;

	/**
	 * show lenslet array 1
	 */
	private boolean showLensletArray1;

	/**
	 * show lenslet array 2
	 */
	private boolean showLensletArray2;
	
	private int nx;
	
	private int ny;
	
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
		f0 = 0.1;
		eta1 = 0.8;
		pitch = 0.1;
		showLensletArray1 = true;
		showLensletArray2 = true;
		nx = 1;
		ny = 1;
		offset1 = new Vector3D(0, 0, 0.00001);
		
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
		// printStream.println("renderQuality = "+renderQuality);
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
		double fn = f0;
		for(int n = 0; n<nx; n++)
		{
			scene.addSceneObject(
					new SparseRectangularLensletArray(
							"LA+"+n,	// description
							new Vector3D(-0.5+n*pitch, -0.5, fn).getSumWith(offset1),	// corner
							right,	// spanVector1
							up,	// spanVector2
							fn,	// focalLength
							pitch,	// xPeriod
							pitch,	// yPeriod
							0.5*pitch,	// xOffset
							0.5*pitch,	// yOffset
							nx,	// nx
							ny,	// ny
							0.96,	// throughputCoefficient
							false,	// reflective
							true,	// shadowThrowing
							scene,	// parent
							studio
							), 
					showLensletArray1
					);

			scene.addSceneObject(
					new SparseRectangularLensletArray(
							"LA-"+n,	// description
							new Vector3D(-0.5+n*pitch, -0.5, fn),	//.getSumWith(offset2),	// corner
							right,	// spanVector1
							up,	// spanVector2
							-fn,	// focalLength
							pitch,	// xPeriod
							pitch,	// yPeriod
							0.5*pitch,	// xOffset
							0.5*pitch,	// yOffset
							nx,	// nx
							ny,	// ny
							0.96,	// throughputCoefficient
							false,	// reflective
							true,	// shadowThrowing
							scene,	// parent
							studio
							), 
					showLensletArray2
					);
			
			fn *= eta1;
		}


		// the camera
		studio.setCamera(getStandardCamera());
	}

	
	
	//
	// for interactive version
	//
	
	public enum LensletArraysInitialisationType
	{
		INIT("Initialise...");
		
		private String description;
		private LensletArraysInitialisationType(String description) {this.description = description;}	
		@Override
		public String toString() {return description;}
	}

	
	private LabelledDoublePanel f0Panel, eta1Panel, pitchPanel;
	private LabelledVector3DPanel offset2Panel;
	private IntPanel nxPanel, nyPanel;
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

		
		nxPanel = new IntPanel();
		nxPanel.setNumber(nx);
		nxPanel.setToolTipText("Only every n_xth lenslet is present in the x direction");		
		nyPanel = new IntPanel();
		nyPanel.setNumber(ny);
		nyPanel.setToolTipText("Only every n_yth lenslet is present in the y direction");
		scenePanel.add(GUIBitsAndBobs.makeRow("(nx, ny) = (", nxPanel, ",", nyPanel, ")"), "span");
		
		f0Panel = new LabelledDoublePanel("f0");
		f0Panel.setNumber(f0);
		f0Panel.setToolTipText("Focal length of 0th lenslet in array with positive focal lengths");
		scenePanel.add(f0Panel, "span");
		
		eta1Panel = new LabelledDoublePanel("eta_1");
		eta1Panel.setNumber(eta1);
		eta1Panel.setToolTipText("eta1");
		scenePanel.add(eta1Panel, "span");

		pitchPanel = new LabelledDoublePanel("pitch");
		pitchPanel.setNumber(pitch);
		pitchPanel.setToolTipText("Pitch, i.e. distance between neighbouring lenslets");
		scenePanel.add(pitchPanel, "span");

		showLensletArray1CheckBox = new JCheckBox();
		showLensletArray1CheckBox.setSelected(showLensletArray1);
		showLensletArray2CheckBox = new JCheckBox();
		showLensletArray2CheckBox.setSelected(showLensletArray2);
		scenePanel.add(GUIBitsAndBobs.makeRow("Show LA1", showLensletArray1CheckBox, ", show LA2", showLensletArray2CheckBox), "span");

		offset2Panel = new LabelledVector3DPanel("Offset of LA1");
		offset2Panel.setVector3D(offset1);
		offset2Panel.setToolTipText("Offset of LA1");
		scenePanel.add(offset2Panel, "span");
		
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
		
		f0 = f0Panel.getNumber();
		eta1 = eta1Panel.getNumber();
		pitch = pitchPanel.getNumber();
		nx = nxPanel.getNumber();
		ny = nyPanel.getNumber();
		offset1 = offset2Panel.getVector3D();
		
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
	

	@Override
	public void actionPerformed(ActionEvent e)
	{
		super.actionPerformed(e);
		
		if(e.getSource().equals(lensletArraysInitialisationComboBox))
		{
			switch((LensletArraysInitialisationType)(lensletArraysInitialisationComboBox.getSelectedItem()))
			{
			case INIT:
			default:
				// do nothing
			}
			lensletArraysInitialisationComboBox.setSelectedItem(LensletArraysInitialisationType.INIT);
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
