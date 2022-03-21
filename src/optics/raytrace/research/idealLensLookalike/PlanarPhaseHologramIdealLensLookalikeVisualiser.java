package optics.raytrace.research.idealLensLookalike;

import java.awt.event.ActionEvent;
import java.io.PrintStream;

import javax.swing.JButton;
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
import optics.raytrace.GUI.lowLevel.LabelledIntPanel;
import optics.raytrace.GUI.lowLevel.LabelledVector3DPanel;
import optics.raytrace.GUI.sceneObjects.EditableFramedRectangle;
import optics.raytrace.GUI.sceneObjects.EditableRayTrajectory;
import optics.raytrace.GUI.sceneObjects.EditableScaledParametrisedSphere;
import optics.raytrace.core.Studio;
import optics.raytrace.core.StudioInitialisationType;
import optics.raytrace.exceptions.SceneException;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.surfaces.IdealThinLensSurfaceSimple;
import optics.raytrace.surfaces.SurfaceColour;
import optics.raytrace.surfaces.SurfaceColourLightSourceIndependent;


/**
 * A very basic example of NonInteractiveTIM.
 * 
 * @author Johannes Courtial
 */
public class PlanarPhaseHologramIdealLensLookalikeVisualiser extends NonInteractiveTIMEngine
{
	// parameters common to ideal thin lens and phase holograms
	private Vector3D corner;
	private Vector3D conjugatePositionA;
	private Vector3D conjugatePositionB;
	
	// ideal thin lens
	private boolean showIdealThinLens;
	
	// corresponding phase holograms
	double phaseHologram1AngleDeg;
	double phaseHologram1AngleDegTwo;
	double phaseHologram2AngleDeg;
	double phaseHologram2AngleDegTwo;
	private boolean showPhaseHologram1;
	private boolean showPhaseHologram2;
	

	//
	// rest of the scene
	//
	
	/**
	 * Determines how to initialise the backdrop
	 */
	private StudioInitialisationType studioInitialisation;

	private boolean showSphereAtB;

	//
	// light-ray trajectories
	//
	
	private int numberOfPossibleTrajectories = 3;
	
	/**
	 * if true, show a light-ray trajectory
	 */
	private boolean showTrajectory[];
	
	/**
	 * start point of the light-ray trajectory
	 */
	private Vector3D trajectoryStartPoint[];
	
	/**
	 * initial direction of the light-ray trajectory
	 */
	private Vector3D trajectoryStartDirection[];
	
	/**
	 * radius of the tube that represents the trajectory
	 */
	private double trajectoryRadius[];
	
	/**
	 * max. trace level when tracing trajectory
	 */
	private int trajectoryMaxTraceLevel[];

	//
	// cameras
	//
	
	
	/**
	 * Constructor.
	 * Sets all parameters.
	 */
	public PlanarPhaseHologramIdealLensLookalikeVisualiser()
	{
		super();

		// parameters common to ideal thin lens and phase holograms
		corner = new Vector3D(-0.5, -0.5, 0);
		conjugatePositionA = new Vector3D(.5, 0.5, -10);	// {ax, ay, az}
		conjugatePositionB = new Vector3D(0, 1, 10);	// {bx,by,bz}

		// ideal thin lens
		showIdealThinLens = false;

		// equivalent phase holograms
		phaseHologram1AngleDeg = 40;
		phaseHologram2AngleDeg = 20;
		phaseHologram1AngleDegTwo=80;
		phaseHologram2AngleDegTwo=70;
		showPhaseHologram1 = true;
		showPhaseHologram2 = true;

		// light-ray trajectory

		// first, switch of NonInteractiveTIM's automatic tracing of rays with trajectory, as this doesn't work
		traceRaysWithTrajectory = false;	// we do this ourselves
		
		// create arrays for trajectory data
		showTrajectory = new boolean[numberOfPossibleTrajectories];
		trajectoryStartPoint = new Vector3D[numberOfPossibleTrajectories];
		trajectoryStartDirection = new Vector3D[numberOfPossibleTrajectories];
		trajectoryRadius = new double[numberOfPossibleTrajectories];
		trajectoryMaxTraceLevel = new int[numberOfPossibleTrajectories];
		
		// initialise all trajectories
		for(int i=0; i<numberOfPossibleTrajectories; i++)
		{
			showTrajectory[i] = false;
			trajectoryStartPoint[i] = new Vector3D(0, -0.8, 0.01);
			trajectoryStartDirection[i] = new Vector3D(-0.05, -0.05, 1);
			trajectoryRadius[i] = 0.025;
			trajectoryMaxTraceLevel[i] = 1000;
		}


		// rest of the scene
		studioInitialisation = StudioInitialisationType.LATTICE;	// the backdrop
		showSphereAtB = true;

		// cameras
		
		renderQuality = RenderQualityEnum.DRAFT;
		
		// (outside) camera parameters; the camera is set by getStandardCamera()
		// @see optics.raytrace.NonInteractiveTIMEngine.getStandardCamera()
		cameraViewCentre = new Vector3D(0.5, 0.5, 0);
		cameraViewDirection = new Vector3D(0, 0, 1);
		cameraDistance = 10;
		cameraHorizontalFOVDeg = 20;
//		 * @see optics.raytrace.NonInteractiveTIMEngine.cameraMaxTraceLevel
//		 * @see optics.raytrace.NonInteractiveTIMEngine.cameraPixelsX
//		 * @see optics.raytrace.NonInteractiveTIMEngine.cameraPixelsY
		cameraApertureSize = ApertureSizeType.PINHOLE;
		cameraFocussingDistance = cameraDistance;
		
		
		nonInteractiveTIMAction = NonInteractiveTIMActionEnum.INTERACTIVE;
//		nonInteractiveTIMAction = NonInteractiveTIMActionEnum.MOVIE;
		firstFrame = 0;

		// camera parameters are set in createStudio()

		if(nonInteractiveTIMAction == NonInteractiveTIMActionEnum.INTERACTIVE)
		{
			windowTitle = "Dr TIM's planar-phas-hologram-ideal-lens-lookalike visualiser";
			windowWidth = 1500;
			windowHeight = 650;
		}
	}

		
	@Override
	public String getClassName()
	{
		return "PlanarPhaseHologramIdealLensLookalike"	// the name
				;
	}
	
	/**
	 * Save all parameters
	 * @param printStream
	 */
	@Override
	public void writeParameters(PrintStream printStream)
	{
		// write any parameters not defined in NonInteractiveTIMEngine
		// printStream.println("parameterName = "+parameterName);
		
		// parameters common to ideal thin lens and phase holograms
		printStream.println("corner = "+corner);
		printStream.println("conjugatePositionA = "+conjugatePositionA);
		printStream.println("conjugatePositionB = "+conjugatePositionB);

		// ideal thin lens
		printStream.println("showIdealThinLens = "+showIdealThinLens);

		// equivalent phase holograms
		printStream.println("phaseHologram1AngleDeg = "+phaseHologram1AngleDeg);
		printStream.println("phaseHologram2AngleDeg = "+phaseHologram2AngleDeg);
		printStream.println("showPhaseHologram1 = "+showPhaseHologram1);
		printStream.println("showPhaseHologram2 = "+showPhaseHologram2);
		
		// light-ray trajectories
		for(int i=0; i<numberOfPossibleTrajectories; i++)
		{
			printStream.println("showTrajectory["+i+"] = "+showTrajectory[i]);
			printStream.println("trajectoryStartPoint["+i+"] = "+trajectoryStartPoint[i]);
			printStream.println("trajectoryStartDirection["+i+"] = "+trajectoryStartDirection[i]);
			printStream.println("trajectoryRadius["+i+"] = "+trajectoryRadius[i]);
			printStream.println("trajectoryMaxTraceLevel["+i+"] = "+trajectoryMaxTraceLevel[i]);
		}

		// rest of the scene
		printStream.println("studioInitialisation = "+studioInitialisation);	// the backdrop

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
		
		// add more scene objects to scene
			
		// note that the normals must be normalised!
		Vector3D idealThinLensNormal = new Vector3D(0, 0, 1);
		double phaseHologram1Angle = MyMath.deg2rad(phaseHologram1AngleDeg);
		double phaseHologram1AngleTwo = MyMath.deg2rad(phaseHologram1AngleDegTwo);
		Vector3D phaseHologram1Normal = new Vector3D (Math.cos(phaseHologram1AngleTwo),Math.sin(phaseHologram1AngleTwo)* Math.sin(phaseHologram1Angle), Math.sin(phaseHologram1AngleTwo)*Math.cos(phaseHologram1Angle));
		double phaseHologram2Angle = MyMath.deg2rad(phaseHologram2AngleDeg);
		double phaseHologram2AngleTwo = MyMath.deg2rad(phaseHologram2AngleDegTwo);
		Vector3D phaseHologram2Normal = new Vector3D(Math.cos(phaseHologram2AngleTwo), Math.sin(phaseHologram2AngleTwo)*Math.sin(phaseHologram2Angle), Math.sin(phaseHologram2AngleTwo)*Math.cos(phaseHologram2Angle));
				
		// the ideal thin lens
		
		double o = -Vector3D.scalarProduct(Vector3D.difference(conjugatePositionA, corner), idealThinLensNormal);
		double i = Vector3D.scalarProduct(Vector3D.difference(conjugatePositionB, corner), idealThinLensNormal);
		IdealThinLensSurfaceSimple idealThinLensSurface;
		try {
			idealThinLensSurface = new IdealThinLensSurfaceSimple(
					Geometry.linePlaneIntersection(
							conjugatePositionA,	// pointOnLine
							Vector3D.difference(conjugatePositionB, conjugatePositionA),	// directionOfLine
							corner,	// pointOnPlane
							idealThinLensNormal	// normalToPlane
						), // lensCentre
					idealThinLensNormal,	// opticalAxisDirection
					1/(1/o + 1/i),	// focalLength
					0.96,	// transmissionCoefficient
					false	// shadowThrowing
				);
			
			EditableFramedRectangle idealThinLens = new EditableFramedRectangle(
					"ideal thin lens",	// description
					corner,	// corner
					new Vector3D(1, 0, 0),	// widthVector
					new Vector3D(0, 1, 0),	// heightVector
					0.02,	// frameRadius
					idealThinLensSurface, // windowSurfaceProperty
					SurfaceColour.BLUE_SHINY,	// frameSurfaceProperty
					true,	// showFrames
					scene,	// parent
					studio
			);
			scene.addSceneObject(idealThinLens, showIdealThinLens);
		} catch (MathException e) {
			e.printStackTrace();
		}

//		// add a tiny sphere at the position b
		scene.addSceneObject(
				new EditableScaledParametrisedSphere(
						"tiny sphere at image position (b)",	// description
						conjugatePositionB,	// centre
						0.01,	// radius
						SurfaceColour.WHITE_SHINY,	// surfaceProperty
						scene,	// parent 
						studio
						),
				showSphereAtB);
		

		// phase hologram 1
		
		// the u1 axis is along the line where the two phase holograms and the ideal thin lens intersect
		//
		Vector3D u1Hat = Vector3D.crossProduct(phaseHologram1Normal, phaseHologram2Normal).getNormalised();
		
		// the u2 axis is in the plane of hologram 1, perpendicular to u1
		Vector3D u2Hat = Vector3D.crossProduct(phaseHologram1Normal, u1Hat).getNormalised();
		
		EditableFramedRectangle phaseHologram1 = new EditableFramedRectangle(
				"phase hologram 1",	// description
				corner,	// corner
				u1Hat,	// widthVector
				u2Hat,	// heightVector
				0.02002,	// frameRadius
				new PhaseHologramOfIdealLensLookalike(
						conjugatePositionA,	// conjugatePositionInFrontOfThisHologram
						conjugatePositionB,	// conjugatePositionInFrontOfOtherHologram
						idealThinLensNormal,
						phaseHologram1Normal,	// thisPhaseHologramNormal
						phaseHologram2Normal,	// otherPhaseHologramNormal
						corner,	// originOfCoordinateSystem
						u1Hat,	// u1BasisVector
						u2Hat,	// u2BasisVector
						0.96,	// throughputCoefficient,
						true	// shadowThrowing
					), // windowSurfaceProperty
				SurfaceColour.GREEN_SHINY,	// frameSurfaceProperty
				true,	// showFrames
				scene,	// parent
				studio
		);
		scene.addSceneObject(phaseHologram1, showPhaseHologram1);

		// phase hologram 2
		
		// the w1 axis is along the line where the two phase holograms and the ideal thin lens intersect
		Vector3D w1Hat = Vector3D.crossProduct(phaseHologram1Normal, phaseHologram2Normal).getNormalised();;
		
		// the u2 axis is in the plane of hologram 1, perpendicular to u1
		Vector3D w2Hat = Vector3D.crossProduct(phaseHologram2Normal, w1Hat).getNormalised();
		
		EditableFramedRectangle phaseHologram2 = new EditableFramedRectangle(
				"phase hologram 2",	// description
				corner,	// corner
				w1Hat,	// widthVector
				w2Hat,	// heightVector
				0.02001,	// frameRadius
				new PhaseHologramOfIdealLensLookalike(
						conjugatePositionB,	// conjugatePositionInFrontOfThisHologram
						conjugatePositionA,	// conjugatePositionInFrontOfOtherHologram
						idealThinLensNormal,
						phaseHologram2Normal,	// thisPhaseHologramNormal
						phaseHologram1Normal,	// otherPhaseHologramNormal
						corner,	// originOfCoordinateSystem
						w1Hat.getReverse(),	// u1BasisVector
						w2Hat.getReverse(),	// u2BasisVector
						0.96,	// throughputCoefficient,
						true	// shadowThrowing
					), // windowSurfaceProperty
				SurfaceColour.RED_SHINY,	// frameSurfaceProperty
				true,	// showFrames
				scene,	// parent
				studio
		);
		scene.addSceneObject(phaseHologram2, showPhaseHologram2);
		
		for(int t=0; t<numberOfPossibleTrajectories; t++)
		{
			if(showTrajectory[t])
			{
				// do the tracing of rays with trajectory
				scene.addSceneObject(
						new EditableRayTrajectory(
								"light-ray trajectory #"+t,	// description
								trajectoryStartPoint[t],	// startPoint
								0,	// startTime
								trajectoryStartDirection[t],	// startDirection
								trajectoryRadius[t],	// rayRadius
								SurfaceColourLightSourceIndependent.RED,	// surfaceProperty
								// SurfaceColour.RED_SHINY,	// surfaceProperty
								trajectoryMaxTraceLevel[t],	// maxTraceLevel
								true,	// reportToConsole
								scene,	// parent
								studio
								)
						);
			}
		}

		// RayWithTrajectory.traceRaysWithTrajectory(studio.getScene());
		studio.traceRaysWithTrajectory();

		// the camera
		
		studio.setCamera(getStandardCamera());
	}

	
	
	// GUI stuff
	
	// parameters common to ideal thin lens and phase holograms
	private LabelledVector3DPanel cornerPanel, conjugatePositionAPanel, conjugatePositionBPanel;

	// ideal thin lens
	private JCheckBox showIdealThinLensCheckbox;

	// corresponding phase holograms
	private LabelledDoublePanel phaseHologram1AngleDegPanel, phaseHologram2AngleDegPanel;
	private JCheckBox showPhaseHologram1Checkbox, showPhaseHologram2Checkbox;
	
	// light-ray trajectory
	private JCheckBox showTrajectoryCheckBox[];
	private LabelledVector3DPanel trajectoryStartPointPanel[];
	private LabelledVector3DPanel trajectoryStartDirectionPanel[];
	private JButton setToStartAtAButton[];
	private LabelledDoublePanel	trajectoryRadiusPanel[];
	private LabelledIntPanel trajectoryMaxTraceLevelPanel[];

	// rest of the scene
	private JComboBox<StudioInitialisationType> studioInitialisationComboBox;
	private JCheckBox showSphereAtBCheckbox;
	
	// camera
	private LabelledVector3DPanel cameraViewCentrePanel;
	private LabelledVector3DPanel cameraViewDirectionPanel;
	private LabelledDoublePanel cameraDistancePanel;
	private DoublePanel cameraHorizontalFOVDegPanel;
	private JComboBox<ApertureSizeType> cameraApertureSizeComboBox;
	private LabelledDoublePanel cameraFocussingDistancePanel;
	private JButton setCameraPositionToAButton, setCameraPositionToBButton;
	
	
	@Override
	protected void createInteractiveControlPanel()
	{
		super.createInteractiveControlPanel();

		JTabbedPane tabbedPane = new JTabbedPane();
		interactiveControlPanel.add(tabbedPane, "span");

		//
		// ideal thin lens panel
		//
		
		JPanel idealThinLensPanel = new JPanel();
		idealThinLensPanel.setLayout(new MigLayout("insets 0"));
		tabbedPane.addTab("Ideal lens", idealThinLensPanel);

		cornerPanel = new LabelledVector3DPanel("Corner");
		cornerPanel.setVector3D(corner);
		idealThinLensPanel.add(cornerPanel, "span");

		conjugatePositionAPanel = new LabelledVector3DPanel("Conjugate position A");
		conjugatePositionAPanel.setVector3D(conjugatePositionA);
		idealThinLensPanel.add(conjugatePositionAPanel, "span");

		conjugatePositionBPanel = new LabelledVector3DPanel("Conjugate position B");
		conjugatePositionBPanel.setVector3D(conjugatePositionB);
		idealThinLensPanel.add(conjugatePositionBPanel, "span");

		showIdealThinLensCheckbox = new JCheckBox("Show ideal thin lens");
		showIdealThinLensCheckbox.setSelected(showIdealThinLens);
		idealThinLensPanel.add(showIdealThinLensCheckbox, "span");


		//
		// phase holograms panel
		//
		
		JPanel phaseHologramsPanel = new JPanel();
		phaseHologramsPanel.setLayout(new MigLayout("insets 0"));
		tabbedPane.addTab("Phase holograms", phaseHologramsPanel);

		phaseHologram1AngleDegPanel = new LabelledDoublePanel("Angle between phase hologram 1 and lens");
		phaseHologram1AngleDegPanel.setNumber(phaseHologram1AngleDeg);
		phaseHologramsPanel.add(phaseHologram1AngleDegPanel, "span");
		
		phaseHologram2AngleDegPanel = new LabelledDoublePanel("Angle between phase hologram 2 and lens");
		phaseHologram2AngleDegPanel.setNumber(phaseHologram2AngleDeg);
		phaseHologramsPanel.add(phaseHologram2AngleDegPanel, "span");
		
		showPhaseHologram1Checkbox = new JCheckBox("Show phase hologram 1");
		showPhaseHologram1Checkbox.setSelected(showPhaseHologram1);
		phaseHologramsPanel.add(showPhaseHologram1Checkbox, "span");

		showPhaseHologram2Checkbox = new JCheckBox("Show phase hologram 2");
		showPhaseHologram2Checkbox.setSelected(showPhaseHologram2);
		phaseHologramsPanel.add(showPhaseHologram2Checkbox, "span");
		

		//
		// Light-ray trajectories
		//
		
		JPanel trajectoryPanel = new JPanel();
		trajectoryPanel.setLayout(new MigLayout("insets 0"));
		tabbedPane.addTab("Trajectories", trajectoryPanel);

		JTabbedPane trajectoriesTabbedPane = new JTabbedPane();
		trajectoryPanel.add(trajectoriesTabbedPane, "span");

		showTrajectoryCheckBox = new JCheckBox[numberOfPossibleTrajectories];
		trajectoryStartPointPanel = new LabelledVector3DPanel[numberOfPossibleTrajectories];
		trajectoryStartDirectionPanel = new LabelledVector3DPanel[numberOfPossibleTrajectories];
		setToStartAtAButton = new JButton[numberOfPossibleTrajectories];
		trajectoryRadiusPanel = new LabelledDoublePanel[numberOfPossibleTrajectories];
		trajectoryMaxTraceLevelPanel = new LabelledIntPanel[numberOfPossibleTrajectories];
		
		for(int i=0; i<numberOfPossibleTrajectories; i++)
		{
			JPanel trajectoryIPanel = new JPanel();
			trajectoryIPanel.setLayout(new MigLayout("insets 0"));
			trajectoriesTabbedPane.addTab("Trajectory #"+i, trajectoryIPanel);

			showTrajectoryCheckBox[i] = new JCheckBox("Show trajectory");
			showTrajectoryCheckBox[i].setSelected(showTrajectory[i]);
			trajectoryIPanel.add(showTrajectoryCheckBox[i], "wrap");

			trajectoryStartPointPanel[i] = new LabelledVector3DPanel("Start position");
			trajectoryStartPointPanel[i].setVector3D(trajectoryStartPoint[i]);
			trajectoryIPanel.add(trajectoryStartPointPanel[i], "span");

			trajectoryStartDirectionPanel[i] = new LabelledVector3DPanel("Initial direction");
			trajectoryStartDirectionPanel[i].setVector3D(trajectoryStartDirection[i]);
			trajectoryIPanel.add(trajectoryStartDirectionPanel[i], "span");

			setToStartAtAButton[i] = new JButton("Set to start at A");
			setToStartAtAButton[i].addActionListener(this);
			trajectoryIPanel.add(setToStartAtAButton[i], "span");

			trajectoryRadiusPanel[i] = new LabelledDoublePanel("Trajectory radius");
			trajectoryRadiusPanel[i].setNumber(trajectoryRadius[i]);
			trajectoryIPanel.add(trajectoryRadiusPanel[i], "span");

			trajectoryMaxTraceLevelPanel[i] = new LabelledIntPanel("Max. trace level");
			trajectoryMaxTraceLevelPanel[i].setNumber(trajectoryMaxTraceLevel[i]);
			trajectoryIPanel.add(trajectoryMaxTraceLevelPanel[i], "span");
		}
		
		//
		// Other scene-objects panel
		//
		
		JPanel otherObjectsPanel = new JPanel();
		otherObjectsPanel.setLayout(new MigLayout("insets 0"));
		tabbedPane.addTab("Other scene objects", otherObjectsPanel);

		studioInitialisationComboBox = new JComboBox<StudioInitialisationType>(StudioInitialisationType.limitedValuesForBackgrounds);
		studioInitialisationComboBox.setSelectedItem(studioInitialisation);
		otherObjectsPanel.add(GUIBitsAndBobs.makeRow("Initialise backdrop to", studioInitialisationComboBox), "span");

		showSphereAtBCheckbox = new JCheckBox("Show tiny sphere at position B");
		showSphereAtBCheckbox.setSelected(showSphereAtB);
		otherObjectsPanel.add(showSphereAtBCheckbox, "span");
		
		//
		// cameras panel
		//
		
		JPanel cameraPanel = new JPanel();
		cameraPanel.setLayout(new MigLayout("insets 0"));
		tabbedPane.addTab("Cameras", cameraPanel);
		
		cameraViewCentrePanel = new LabelledVector3DPanel("Centre of view");
		cameraViewCentrePanel.setVector3D(cameraViewCentre);
		cameraPanel.add(cameraViewCentrePanel, "span");
			
		cameraViewDirectionPanel = new LabelledVector3DPanel("View direction");
		cameraViewDirectionPanel.setVector3D(cameraViewDirection);
		cameraPanel.add(cameraViewDirectionPanel, "span");
		
		cameraDistancePanel = new LabelledDoublePanel("Camera distance");
		cameraDistancePanel.setNumber(cameraDistance);
		cameraPanel.add(cameraDistancePanel, "span");
		
		setCameraPositionToAButton = new JButton("A");
		setCameraPositionToAButton.addActionListener(this);
		setCameraPositionToBButton = new JButton("B");
		setCameraPositionToBButton.addActionListener(this);
		
		cameraPanel.add(GUIBitsAndBobs.makeRow("Set camera position to", setCameraPositionToAButton, setCameraPositionToBButton), "span");
		
		cameraHorizontalFOVDegPanel = new DoublePanel();
		cameraHorizontalFOVDegPanel.setNumber(cameraHorizontalFOVDeg);
		cameraPanel.add(GUIBitsAndBobs.makeRow("Horizontal FOV", cameraHorizontalFOVDegPanel, "°"), "span");
		
		cameraApertureSizeComboBox = new JComboBox<ApertureSizeType>(ApertureSizeType.values());
		cameraApertureSizeComboBox.setSelectedItem(cameraApertureSize);
		cameraPanel.add(GUIBitsAndBobs.makeRow("Camera aperture", cameraApertureSizeComboBox), "span");		
		
		cameraFocussingDistancePanel = new LabelledDoublePanel("Focussing distance");
		cameraFocussingDistancePanel.setNumber(cameraFocussingDistance);
		cameraPanel.add(cameraFocussingDistancePanel, "span");
		
		// TODO Buttons to set the camera position to A and B
	}
	
	@Override
	protected void acceptValuesInInteractiveControlPanel()
	{
		// parameters common to ideal thin lens and phase holograms
		corner = cornerPanel.getVector3D();
		conjugatePositionA = conjugatePositionAPanel.getVector3D();
		conjugatePositionB = conjugatePositionBPanel.getVector3D();

		// ideal thin lens
		showIdealThinLens = showIdealThinLensCheckbox.isSelected();
		
		// phase holograms
		phaseHologram1AngleDeg = phaseHologram1AngleDegPanel.getNumber();
		phaseHologram2AngleDeg = phaseHologram2AngleDegPanel.getNumber();
		showPhaseHologram1 = showPhaseHologram1Checkbox.isSelected();
		showPhaseHologram2 = showPhaseHologram2Checkbox.isSelected();
		
		// trajectories
		
		for(int i=0; i<numberOfPossibleTrajectories; i++)
		{
			showTrajectory[i] = showTrajectoryCheckBox[i].isSelected();
			trajectoryStartPoint[i] = trajectoryStartPointPanel[i].getVector3D();
			trajectoryStartDirection[i] = trajectoryStartDirectionPanel[i].getVector3D();
			trajectoryRadius[i] = trajectoryRadiusPanel[i].getNumber();
			trajectoryMaxTraceLevel[i] = trajectoryMaxTraceLevelPanel[i].getNumber();
		}

		// other scene objects
		studioInitialisation = (StudioInitialisationType)(studioInitialisationComboBox.getSelectedItem());
		showSphereAtB = showSphereAtBCheckbox.isSelected();
		
		// camera
		cameraViewCentre = cameraViewCentrePanel.getVector3D();
		cameraViewDirection = cameraViewDirectionPanel.getVector3D();
		cameraDistance = cameraDistancePanel.getNumber();
		cameraHorizontalFOVDeg = cameraHorizontalFOVDegPanel.getNumber();
		cameraApertureSize = (ApertureSizeType)(cameraApertureSizeComboBox.getSelectedItem());
		cameraFocussingDistance = cameraFocussingDistancePanel.getNumber();		
	}
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		for(int i=0; i<numberOfPossibleTrajectories; i++)
		{
			if(e.getSource().equals(setToStartAtAButton[i]))
			{
				trajectoryStartPointPanel[i].setVector3D(conjugatePositionAPanel.getVector3D());
				// trajectoryStartDirectionPanel.setVector3D(new Vector3D(0, 0, 1));
			}
		}

		if(e.getSource().equals(setCameraPositionToAButton))
		{
			Vector3D newViewDirection = Vector3D.difference(cameraViewCentrePanel.getVector3D(), conjugatePositionAPanel.getVector3D());
			cameraViewDirectionPanel.setVector3D(newViewDirection);
			cameraDistancePanel.setNumber(newViewDirection.getLength());
		}
		else if(e.getSource().equals(setCameraPositionToBButton))
		{
			Vector3D newViewDirection = Vector3D.difference(cameraViewCentrePanel.getVector3D(), conjugatePositionBPanel.getVector3D());
			cameraViewDirectionPanel.setVector3D(newViewDirection);
			cameraDistancePanel.setNumber(newViewDirection.getLength());
		}
		else super.actionPerformed(e);
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
		(new PlanarPhaseHologramIdealLensLookalikeVisualiser()).run();
	}
}
