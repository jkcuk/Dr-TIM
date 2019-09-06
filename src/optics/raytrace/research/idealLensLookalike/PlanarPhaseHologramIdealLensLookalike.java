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
import optics.raytrace.GUI.cameras.EditableRelativisticAnyFocusSurfaceCamera;
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
import optics.raytrace.cameras.PinholeCamera.ExposureCompensationType;
import optics.raytrace.core.Studio;
import optics.raytrace.core.StudioInitialisationType;
import optics.raytrace.exceptions.SceneException;
import optics.raytrace.sceneObjects.Plane;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.surfaces.IdealThinLensSurfaceSimple;
import optics.raytrace.surfaces.SurfaceColour;
import optics.raytrace.surfaces.SurfaceColourLightSourceIndependent;


/**
 * A very basic example of NonInteractiveTIM.
 * 
 * @author Johannes Courtial
 */
public class PlanarPhaseHologramIdealLensLookalike extends NonInteractiveTIMEngine
{
	private Vector3D conjugatePositionA, conjugatePositionB;
	private boolean showL, showH1, showH2, showSphereAtB;
	
	//
	// light-ray trajectories
	//
	
	/**
	 * if true, show a light-ray trajectory
	 */
	private boolean showTrajectory;
	
	/**
	 * start point of the light-ray trajectory
	 */
	private Vector3D trajectoryStartPoint;
	
	/**
	 * initial direction of the light-ray trajectory
	 */
	private Vector3D trajectoryStartDirection;
	
	/**
	 * radius of the tube that represents the trajectory
	 */
	private double trajectoryRadius;
	
	/**
	 * max. trace level when tracing trajectory
	 */
	private int trajectoryMaxTraceLevel;

	//
	// cameras
	//
	
	private enum CameraType
	{
		SIDE("Side, looking at the lenses from the side"),
		A("Camera at point A");
		
		private String description;
		private CameraType(String description) {this.description = description;}
		@Override
		public String toString() {return description;}
	}
	
	/**
	 * Determines which camera to use to generate the photo
	 */
	private CameraType activeCamera;
	
	//
	// the side camera
	//
	
	/**
	 * the direction in which the camera looks out
	 */
	private Vector3D sideCameraViewDirection;
	
	/**
	 * the aperture-centre position of the inside camera
	 */
	private Vector3D sideCameraApertureCentre;
	
	/**
	 * focussing distance of the inside camera
	 */
	private double sideCameraFocussingDistance;
	
	/**
	 * the inside camera's horizontal field of view, in degrees
	 */
	private double sideCameraHorizontalFOVDeg;
	
	/**
	 * the inside camera's aperture size
	 */
	private ApertureSizeType sideCameraApertureSize;

	
	
	/**
	 * Constructor.
	 * Sets all parameters.
	 */
	public PlanarPhaseHologramIdealLensLookalike()
	{
		super();
		
		// the points the ideal thin lens images into each other
		conjugatePositionA = new Vector3D(.5, 0.5, -10);	// {ax, ay, az}
		conjugatePositionB = new Vector3D(0, 1, 10);	// {bx,by,bz}


		showL = false;
		showH1 = true;
		showH2 = true;
		showSphereAtB = true;
		
		// light-ray trajectory

		// first, switch of NonInteractiveTIM's automatic tracing of rays with trajectory, as this doesn't work
		traceRaysWithTrajectory = false;	// we do this ourselves
		showTrajectory = false;
		trajectoryStartPoint = new Vector3D(0, -0.8, 0.01);
		trajectoryStartDirection = new Vector3D(1, 0, 0);
		trajectoryRadius = 0.025;
		trajectoryMaxTraceLevel = 1000;

		// cameras
		
		renderQuality = RenderQualityEnum.DRAFT;
		
		activeCamera = CameraType.A;
		
		// side-camera parameters
		sideCameraApertureCentre = new Vector3D(10, 0, 0);
		sideCameraViewDirection = new Vector3D(-1, 0, 0);
		sideCameraFocussingDistance = 10;
		sideCameraHorizontalFOVDeg = 20;
		sideCameraApertureSize = ApertureSizeType.PINHOLE;


		// (outside) camera parameters; the camera is set by getStandardCamera()
		// @see optics.raytrace.NonInteractiveTIMEngine.getStandardCamera()
		cameraViewCentre = new Vector3D(0.5, 0.5, 0);
		cameraViewDirection = new Vector3D(0, 0, 1);
		cameraDistance = 10;
		cameraHorizontalFOVDeg = 10;
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
	public String getFirstPartOfFilename()
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
		
		// write any parameters not defined in NonInteractiveTIMEngine
		// printStream.println("parameterName = "+parameterName);
		printStream.println("activeCamera ="+activeCamera);
		
		// inside-camera parameters
		printStream.println("sideCameraApertureCentre = "+sideCameraApertureCentre);
		printStream.println("sideCameraViewDirection = "+sideCameraViewDirection);
		printStream.println("sideCameraFocussingDistance = "+sideCameraFocussingDistance);
		printStream.println("sideCameraHorizontalFOVDeg = "+sideCameraHorizontalFOVDeg);
		printStream.println("sideCameraApertureSize = "+sideCameraApertureSize);

		printStream.println("showL = "+showL);
		printStream.println("showH1 = "+showH1);
		printStream.println("showH2 = "+showH2);
		
		// light-ray trajectory
		printStream.println("showTrajectory = "+showTrajectory);
		printStream.println("trajectoryStartPoint = "+trajectoryStartPoint);
		printStream.println("trajectoryStartDirection = "+trajectoryStartDirection);
		printStream.println("trajectoryRadius = "+trajectoryRadius);
		printStream.println("trajectoryMaxTraceLevel = "+trajectoryMaxTraceLevel);


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
				StudioInitialisationType.MINIMALIST_LOWER_FLOOR,
				scene,
				studio
			);
		
		// add more scene objects to scene
			
		// note that the normals must be normalised!
		Vector3D idealThinLensNormal = new Vector3D(0, 0, 1);
		double phaseHologram1Angle = MyMath.deg2rad(40);
		Vector3D phaseHologram1Normal = new Vector3D(0, Math.sin(phaseHologram1Angle), Math.cos(phaseHologram1Angle));
		double phaseHologram2Angle = MyMath.deg2rad(20);
		Vector3D phaseHologram2Normal = new Vector3D(0, Math.sin(phaseHologram2Angle), Math.cos(phaseHologram2Angle));
		
		
		// the common corner of all three elements
		Vector3D corner = new Vector3D(-0, -0, 0);
		
		
		// the ideal thin lens
		
		double o = -Vector3D.scalarProduct(conjugatePositionA, idealThinLensNormal);
		double i = Vector3D.scalarProduct(conjugatePositionB, idealThinLensNormal);
		IdealThinLensSurfaceSimple idealThinLensSurface;
		try {
			idealThinLensSurface = new IdealThinLensSurfaceSimple(
					Geometry.linePlaneIntersection(
							conjugatePositionA,	// pointOnLine
							Vector3D.difference(conjugatePositionB, conjugatePositionA),	// directionOfLine
							new Vector3D(0, 0, 0),	// pointOnPlane
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
					SurfaceColour.CYAN_SHINY,	// frameSurfaceProperty
					true,	// showFrames
					scene,	// parent
					studio
			);
			scene.addSceneObject(idealThinLens, showL);
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
		Vector3D u1Hat = Vector3D.crossProduct(phaseHologram1Normal, idealThinLensNormal).getNormalised();
		
		// the u2 axis is in the plane of hologram 1, perpendicular to u1
		Vector3D u2Hat = Vector3D.crossProduct(phaseHologram1Normal, u1Hat).getNormalised();
		
		EditableFramedRectangle phaseHologram1 = new EditableFramedRectangle(
				"phase hologram 1",	// description
				corner,	// corner
				u1Hat,	// widthVector
				u2Hat,	// heightVector
				0.02001,	// frameRadius
				new PhaseHologramOfIdealLensLookalike(
						conjugatePositionA,	// conjugatePositionInFrontOfThisHologram
						conjugatePositionB,	// conjugatePositionInFrontOfOtherHologram
						idealThinLensNormal,
						phaseHologram1Normal,	// thisPhaseHologramNormal
						phaseHologram2Normal,	// otherPhaseHologramNormal
						Vector3D.O,	// originOfCoordinateSystem
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
		scene.addSceneObject(phaseHologram1, showH1);

		// phase hologram 2
		
		// the w1 axis is along the line where the two phase holograms and the ideal thin lens intersect
		Vector3D w1Hat = u1Hat;
		
		// the u2 axis is in the plane of hologram 1, perpendicular to u1
		Vector3D w2Hat = Vector3D.crossProduct(phaseHologram2Normal, w1Hat).getNormalised();
		
		EditableFramedRectangle phaseHologram2 = new EditableFramedRectangle(
				"phase hologram 2",	// description
				corner,	// corner
				w1Hat,	// widthVector
				w2Hat,	// heightVector
				0.02002,	// frameRadius
				new PhaseHologramOfIdealLensLookalike(
						conjugatePositionB,	// conjugatePositionInFrontOfThisHologram
						conjugatePositionA,	// conjugatePositionInFrontOfOtherHologram
						idealThinLensNormal,
						phaseHologram2Normal,	// thisPhaseHologramNormal
						phaseHologram1Normal,	// otherPhaseHologramNormal
						Vector3D.O,	// originOfCoordinateSystem
						w1Hat,	// u1BasisVector
						w2Hat,	// u2BasisVector
						0.96,	// throughputCoefficient,
						true	// shadowThrowing
					), // windowSurfaceProperty
				SurfaceColour.RED_SHINY,	// frameSurfaceProperty
				true,	// showFrames
				scene,	// parent
				studio
		);
		scene.addSceneObject(phaseHologram2, showH2);
		
		if(showTrajectory)
		{
			// do the tracing of rays with trajectory
			scene.addSceneObject(
					new EditableRayTrajectory(
							"light-ray trajectory",	// description
							trajectoryStartPoint,	// startPoint
							0,	// startTime
							trajectoryStartDirection,	// startDirection
							trajectoryRadius,	// rayRadius
							SurfaceColourLightSourceIndependent.RED,	// surfaceProperty
							// SurfaceColour.RED_SHINY,	// surfaceProperty
							trajectoryMaxTraceLevel,	// maxTraceLevel
							true,	// reportToConsole
							scene,	// parent
							studio
							)
					);

			// RayWithTrajectory.traceRaysWithTrajectory(studio.getScene());
			studio.traceRaysWithTrajectory();
		}

		// the camera
		
		switch(activeCamera)
		{
		case SIDE:
			studio.setCamera(
					new EditableRelativisticAnyFocusSurfaceCamera(
							"Side camera",
							sideCameraApertureCentre,	// centre of aperture
							sideCameraViewDirection,	// viewDirection
							new Vector3D(0, 1, 0),	// top direction vector
							sideCameraHorizontalFOVDeg,	// horiontalViewAngle in degrees; 2*MyMath.rad2deg(Math.atan(2./10.)) gives same view angle as in previous version
							new Vector3D(0, 0, 0),	// beta
							cameraPixelsX, cameraPixelsY,	// logical number of pixels
							ExposureCompensationType.EC0,	// exposure compensation +0
							cameraMaxTraceLevel,	// maxTraceLevel
							new Plane(
									"focus plane",	// description
									Vector3D.sum(sideCameraApertureCentre, sideCameraViewDirection.getWithLength(sideCameraFocussingDistance)),	// pointOnPlane
									sideCameraViewDirection,	// normal
									null,	// surfaceProperty
									null,	// parent
									null	// studio
								),	// focus scene
							null,	// cameraFrameScene,
							sideCameraApertureSize,	// aperture size
							renderQuality.getBlurQuality(),	// blur quality
							renderQuality.getAntiAliasingQuality()	// anti-aliasing quality
						)
				);
			break;
		case A:
		default:
			studio.setCamera(getStandardCamera());
		}
	}

	private JCheckBox showLCheckbox, showH1Checkbox, showH2Checkbox, showSphereAtBCheckbox;
	
	// light-ray trajectory
	private JCheckBox showTrajectoryCheckBox;
	private LabelledVector3DPanel trajectoryStartPointPanel;
	private LabelledVector3DPanel trajectoryStartDirectionPanel;
	private JButton setToStartAtAButton;
	private LabelledDoublePanel	trajectoryRadiusPanel;
	private LabelledIntPanel trajectoryMaxTraceLevelPanel;

	// cameras
	private JComboBox<CameraType> activeCameraComboBox;

	// main (outside) camera
	private LabelledVector3DPanel cameraViewCentrePanel;
	private LabelledVector3DPanel cameraViewDirectionPanel;
	private LabelledDoublePanel cameraDistancePanel;
	private DoublePanel cameraHorizontalFOVDegPanel;
	private JComboBox<ApertureSizeType> cameraApertureSizeComboBox;
	private LabelledDoublePanel cameraFocussingDistancePanel;

	// inside camera
	private LabelledVector3DPanel sideCameraApertureCentrePanel;
	private LabelledVector3DPanel sideCameraViewDirectionPanel;
	private DoublePanel sideCameraHorizontalFOVDegPanel;
	private JComboBox<ApertureSizeType> sideCameraApertureSizeComboBox;
	private LabelledDoublePanel sideCameraFocussingDistancePanel;

	
	@Override
	protected void createInteractiveControlPanel()
	{
		super.createInteractiveControlPanel();

		JTabbedPane tabbedPane = new JTabbedPane();
		interactiveControlPanel.add(tabbedPane, "span");

		//
		// components panel
		//
		
		JPanel componentsPanel = new JPanel();
		componentsPanel.setLayout(new MigLayout("insets 0"));
		tabbedPane.addTab("Components", componentsPanel);

		showLCheckbox = new JCheckBox("Show ideal thin lens L");
		showLCheckbox.setSelected(showL);
		componentsPanel.add(showLCheckbox, "span");

		showH1Checkbox = new JCheckBox("Show phase hologram 1 (H1)");
		showH1Checkbox.setSelected(showH1);
		componentsPanel.add(showH1Checkbox, "span");

		showH2Checkbox = new JCheckBox("Show phase hologram 2 (H2)");
		showH2Checkbox.setSelected(showH2);
		componentsPanel.add(showH2Checkbox, "span");

		showSphereAtBCheckbox = new JCheckBox("Show tiny sphere at position B");
		showSphereAtBCheckbox.setSelected(showSphereAtB);
		componentsPanel.add(showSphereAtBCheckbox, "span");
		
		//
		// Light-ray trajectories
		//
		
		JPanel trajectoryPanel = new JPanel();
		trajectoryPanel.setLayout(new MigLayout("insets 0"));
		tabbedPane.addTab("Trajectory", trajectoryPanel);

		showTrajectoryCheckBox = new JCheckBox("Show trajectory");
		showTrajectoryCheckBox.setSelected(showTrajectory);
		trajectoryPanel.add(showTrajectoryCheckBox, "wrap");

		trajectoryStartPointPanel = new LabelledVector3DPanel("Start position");
		trajectoryStartPointPanel.setVector3D(trajectoryStartPoint);
		trajectoryPanel.add(trajectoryStartPointPanel, "span");

		trajectoryStartDirectionPanel = new LabelledVector3DPanel("Initial direction");
		trajectoryStartDirectionPanel.setVector3D(trajectoryStartDirection);
		trajectoryPanel.add(trajectoryStartDirectionPanel, "span");

		setToStartAtAButton = new JButton("Set to start at A");
		setToStartAtAButton.addActionListener(this);
		trajectoryPanel.add(setToStartAtAButton, "span");
		
		trajectoryRadiusPanel = new LabelledDoublePanel("Trajectory radius");
		trajectoryRadiusPanel.setNumber(trajectoryRadius);
		trajectoryPanel.add(trajectoryRadiusPanel, "span");
		
		trajectoryMaxTraceLevelPanel = new LabelledIntPanel("Max. trace level");
		trajectoryMaxTraceLevelPanel.setNumber(trajectoryMaxTraceLevel);
		trajectoryPanel.add(trajectoryMaxTraceLevelPanel, "span");

		//
		// cameras panel
		//
		
		JPanel cameraPanel = new JPanel();
		cameraPanel.setLayout(new MigLayout("insets 0"));
		tabbedPane.addTab("Cameras", cameraPanel);
		
		activeCameraComboBox = new JComboBox<CameraType>(CameraType.values());
		activeCameraComboBox.setSelectedItem(activeCamera);
		cameraPanel.add(GUIBitsAndBobs.makeRow("Active camera", activeCameraComboBox), "span");

		JTabbedPane camerasTabbedPane = new JTabbedPane();
		cameraPanel.add(camerasTabbedPane, "span");

		
		// camera at A
		
		JPanel cameraAtAPanel = new JPanel();
		cameraAtAPanel.setLayout(new MigLayout("insets 0"));
		camerasTabbedPane.addTab("Camera at A", cameraAtAPanel);
		
		JPanel cameraViewCentreJPanel = new JPanel();
		cameraViewCentreJPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Centre of view"));
		cameraViewCentreJPanel.setLayout(new MigLayout("insets 0"));
		cameraAtAPanel.add(cameraViewCentreJPanel, "span");

		cameraViewCentrePanel = new LabelledVector3DPanel("Position");
		cameraViewCentrePanel.setVector3D(cameraViewCentre);
		cameraViewCentreJPanel.add(cameraViewCentrePanel, "span");
			
		cameraViewDirectionPanel = new LabelledVector3DPanel("View direction");
		cameraViewDirectionPanel.setVector3D(cameraViewDirection);
		cameraAtAPanel.add(cameraViewDirectionPanel, "span");
		
		cameraDistancePanel = new LabelledDoublePanel("Camera distance");
		cameraDistancePanel.setNumber(cameraDistance);
		cameraAtAPanel.add(cameraDistancePanel, "span");
		
		cameraHorizontalFOVDegPanel = new DoublePanel();
		cameraHorizontalFOVDegPanel.setNumber(cameraHorizontalFOVDeg);
		cameraAtAPanel.add(GUIBitsAndBobs.makeRow("Horizontal FOV", cameraHorizontalFOVDegPanel, "°"), "span");
		
		cameraApertureSizeComboBox = new JComboBox<ApertureSizeType>(ApertureSizeType.values());
		cameraApertureSizeComboBox.setSelectedItem(cameraApertureSize);
		cameraAtAPanel.add(GUIBitsAndBobs.makeRow("Camera aperture", cameraApertureSizeComboBox), "span");		
		
		cameraFocussingDistancePanel = new LabelledDoublePanel("Focussing distance");
		cameraFocussingDistancePanel.setNumber(cameraFocussingDistance);
		cameraAtAPanel.add(cameraFocussingDistancePanel, "span");
		
		
		// inside camera
		
		JPanel sideCameraPanel = new JPanel();
		sideCameraPanel.setLayout(new MigLayout("insets 0"));
		camerasTabbedPane.addTab("Side camera", sideCameraPanel);
		
		sideCameraApertureCentrePanel = new LabelledVector3DPanel("Aperture centre");
		sideCameraApertureCentrePanel.setVector3D(sideCameraApertureCentre);
		sideCameraPanel.add(sideCameraApertureCentrePanel, "span");
		
		sideCameraViewDirectionPanel = new LabelledVector3DPanel("View direction");
		sideCameraViewDirectionPanel.setVector3D(sideCameraViewDirection);
		sideCameraPanel.add(sideCameraViewDirectionPanel, "span");
		
		sideCameraHorizontalFOVDegPanel = new DoublePanel();
		sideCameraHorizontalFOVDegPanel.setNumber(sideCameraHorizontalFOVDeg);
		sideCameraPanel.add(GUIBitsAndBobs.makeRow("Horizontal FOV", sideCameraHorizontalFOVDegPanel, "°"), "span");
		
		sideCameraApertureSizeComboBox = new JComboBox<ApertureSizeType>(ApertureSizeType.values());
		sideCameraApertureSizeComboBox.setSelectedItem(sideCameraApertureSize);
		sideCameraPanel.add(GUIBitsAndBobs.makeRow("Camera aperture", sideCameraApertureSizeComboBox), "span");		
		
		sideCameraFocussingDistancePanel = new LabelledDoublePanel("Focussing distance");
		sideCameraFocussingDistancePanel.setNumber(sideCameraFocussingDistance);
		sideCameraPanel.add(sideCameraFocussingDistancePanel, "span");
	}
	
	@Override
	protected void acceptValuesInInteractiveControlPanel()
	{
		showL = showLCheckbox.isSelected();
		showH1 = showH1Checkbox.isSelected();
		showH2 = showH2Checkbox.isSelected();
		showSphereAtB = showSphereAtBCheckbox.isSelected();
		
		// trajectory
		
		showTrajectory = showTrajectoryCheckBox.isSelected();
		trajectoryStartPoint = trajectoryStartPointPanel.getVector3D();
		trajectoryStartDirection = trajectoryStartDirectionPanel.getVector3D();
		trajectoryRadius = trajectoryRadiusPanel.getNumber();
		trajectoryMaxTraceLevel = trajectoryMaxTraceLevelPanel.getNumber();

		// cameras
		activeCamera = (CameraType)(activeCameraComboBox.getSelectedItem());

		cameraViewCentre = cameraViewCentrePanel.getVector3D();
		cameraViewDirection = cameraViewDirectionPanel.getVector3D();
		cameraDistance = cameraDistancePanel.getNumber();
		cameraHorizontalFOVDeg = cameraHorizontalFOVDegPanel.getNumber();
		cameraApertureSize = (ApertureSizeType)(cameraApertureSizeComboBox.getSelectedItem());
		cameraFocussingDistance = cameraFocussingDistancePanel.getNumber();
		
		sideCameraApertureCentre = sideCameraApertureCentrePanel.getVector3D();
		sideCameraViewDirection = sideCameraViewDirectionPanel.getVector3D();
		sideCameraHorizontalFOVDeg = sideCameraHorizontalFOVDegPanel.getNumber();
		sideCameraApertureSize = (ApertureSizeType)(sideCameraApertureSizeComboBox.getSelectedItem());
		sideCameraFocussingDistance = sideCameraFocussingDistancePanel.getNumber();
	}
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource().equals(setToStartAtAButton))
		{
			trajectoryStartPointPanel.setVector3D(conjugatePositionA);
			trajectoryStartDirectionPanel.setVector3D(new Vector3D(0, 0, 1));
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
		(new PlanarPhaseHologramIdealLensLookalike()).run();
	}
}
