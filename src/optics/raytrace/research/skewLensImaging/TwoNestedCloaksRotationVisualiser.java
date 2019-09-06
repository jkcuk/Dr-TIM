package optics.raytrace.research.skewLensImaging;

import java.awt.event.ActionEvent;
import java.io.PrintStream;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import math.*;
import net.miginfocom.swing.MigLayout;
import optics.DoubleColour;
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
import optics.raytrace.GUI.sceneObjects.EditableArrow;
import optics.raytrace.GUI.sceneObjects.EditableLensSimplicialComplex;
import optics.raytrace.GUI.sceneObjects.EditableRayTrajectory;
import optics.raytrace.GUI.sceneObjects.EditableScaledParametrisedSphere;
import optics.raytrace.cameras.PinholeCamera.ExposureCompensationType;
import optics.raytrace.core.Studio;
import optics.raytrace.core.StudioInitialisationType;
import optics.raytrace.exceptions.SceneException;
import optics.raytrace.sceneObjects.Plane;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.surfaces.SurfaceColour;
import optics.raytrace.surfaces.SurfaceColourLightSourceIndependent;
import optics.raytrace.surfaces.SurfaceTiling;


/**
 * Doesn't actually do what we want it to do, but three nested cloaks might!
 * 
 * @author Johannes Courtial
 */
public class TwoNestedCloaksRotationVisualiser extends NonInteractiveTIMEngine
{
	// parameters
	private boolean showInner, showOuter, showLensesInner, showLensesOuter, showFramesInner, showFramesOuter;
	
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
	// the rest of the scene
	//
	
	/**
	 * Determines how to initialise the backdrop
	 */
	private StudioInitialisationType studioInitialisation;
	
	/**
	 * If true, draw an arrow with its tip at the position of the inside-camera position, pointing in the direction the camera views
	 */
	private boolean indicateInsideCameraPosition;
	
	/**
	 * The patterned sphere is an additional object to study.
	 * It is normally placed inside the cloak.
	 */
	private boolean showPatternedSphere;
	
	/**
	 * Position of the centre of the patterned sphere.
	 */
	private Vector3D patternedSphereCentre;
	
	/**
	 * Radius of the patterned sphere
	 */
	private double patternedSphereRadius;

	
	//
	// cameras
	//
	
	private enum CameraType
	{
		INSIDE("Inside, looking out"),
		OUTSIDE("Outside");
		
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
	// the inside camera
	//
	
	/**
	 * the direction in which the camera looks out
	 */
	private Vector3D insideCameraViewDirection;
	
	/**
	 * the aperture-centre position of the inside camera
	 */
	private Vector3D insideCameraApertureCentre;
	
	/**
	 * focussing distance of the inside camera
	 */
	private double insideCameraFocussingDistance;
	
	/**
	 * the inside camera's horizontal field of view, in degrees
	 */
	private double insideCameraHorizontalFOVDeg;
	
	/**
	 * the inside camera's aperture size
	 */
	private ApertureSizeType insideCameraApertureSize;

	
	/**
	 * Constructor.
	 * Sets all parameters.
	 */
	public TwoNestedCloaksRotationVisualiser()
	{
		super();
		
		showInner = true;
		showOuter = true;
		showLensesInner = false;
		showLensesOuter = false;
		showFramesInner = true;
		showFramesOuter = true;
		
		// light-ray trajectories
		
		// first, switch of NonInteractiveTIM's automatic tracing of rays with trajectory, as this doesn't work
		traceRaysWithTrajectory = false;	// we do this ourselves
		showTrajectory = false;
		trajectoryStartPoint = new Vector3D(0.9, 0.04, 0);
		trajectoryStartDirection = new Vector3D(0, 0, 1);
		trajectoryRadius = 0.025;
		trajectoryMaxTraceLevel = 100;

		// other scene objects
		
		studioInitialisation = StudioInitialisationType.HEAVEN;	// the backdrop
		indicateInsideCameraPosition = false;
		showPatternedSphere = false;
		patternedSphereCentre = new Vector3D(0, -0.75, 0);
		patternedSphereRadius = 0.05;

		activeCamera = CameraType.OUTSIDE;
		
		// (outside) camera parameters; the camera is set by getStandardCamera()
		// @see optics.raytrace.NonInteractiveTIMEngine.getStandardCamera()
		cameraViewCentre = new Vector3D(0, 0, 0);
		cameraViewDirection = new Vector3D(0.15, 0, 1);
		cameraDistance = 10;
		cameraHorizontalFOVDeg = 40;
//		 * @see optics.raytrace.NonInteractiveTIMEngine.cameraMaxTraceLevel
//		 * @see optics.raytrace.NonInteractiveTIMEngine.cameraPixelsX
//		 * @see optics.raytrace.NonInteractiveTIMEngine.cameraPixelsY
		cameraApertureSize = ApertureSizeType.PINHOLE;
		cameraFocussingDistance = cameraDistance;
		
		// inside-camera parameters
		insideCameraApertureCentre = new Vector3D(0.9, 0.04, 0);
		insideCameraViewDirection = new Vector3D(0, 0, 1);
		insideCameraFocussingDistance = 10;
		insideCameraHorizontalFOVDeg = 40;
		insideCameraApertureSize = ApertureSizeType.PINHOLE;

		
		renderQuality = RenderQualityEnum.DRAFT;
		
		nonInteractiveTIMAction = NonInteractiveTIMActionEnum.INTERACTIVE;
//		nonInteractiveTIMAction = NonInteractiveTIMActionEnum.MOVIE;
		firstFrame = 0;

		// camera parameters are set in createStudio()
		if(nonInteractiveTIMAction == NonInteractiveTIMActionEnum.INTERACTIVE)
		{
			windowTitle = "Dr TIM's Nested-cloak-rotation visualiser";
			windowWidth = 1500;
			windowHeight = 650;
		}
	}

	
	/* (non-Javadoc)
	 * @see optics.raytrace.NonInteractiveTIMEngine#getFirstPartOfFilename()
	 */
	@Override
	public String getFirstPartOfFilename()
	{
		return
				"NestedCloakRotation"
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
		printStream.println("camera = "+activeCamera);
		if(activeCamera==CameraType.INSIDE)
		{
			printStream.println("ac = "+insideCameraApertureCentre);
			printStream.println(" vd "+insideCameraViewDirection);
			printStream.println(" hFOV "+insideCameraHorizontalFOVDeg+"°");
			printStream.println(" as "+insideCameraApertureSize);
			printStream.println(" fd "+insideCameraFocussingDistance);
		}
		else
		{
			printStream.println(" vc "+cameraViewCentre);
			printStream.println(" vd "+cameraViewDirection);
			printStream.println(" cd "+cameraDistance);
			printStream.println(" hFOV "+cameraHorizontalFOVDeg+"°");
			printStream.println(" as "+cameraApertureSize);
			printStream.println(" fd "+cameraFocussingDistance);
		}
		
		// write all parameters defined in NonInteractiveTIMEngine
		super.writeParameters(printStream);
	}
	
	
	/**
	 * the inner cloak; this is of type EditableLensSimplicialComplex as it supplies methods such as getIndexOfSimplexContainingPosition
	 */
	private EditableLensSimplicialComplex innerCloak;
	
	
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
		
		double beta = -Math.PI/3.;	// the rotation angle
		
		// the inner cloak
		double fDInner = 0.15;
		double h1Inner = 0.3;
		double hInner = 0.7;
		double rInner = 2;
		double h2Inner = 0.452;
		double h1InnerV = 1/(1/h1Inner - 1/fDInner);	// virtual-space height of lower inner vertex
//		EditableIdealLensCloak innerCloak_01 = new EditableIdealLensCloak(
//				"inner cloak",	// description
//				new Vector3D(0, 0, 0),	// baseCentre
//				new Vector3D(1, 0, 0),	// frontDirection
//				new Vector3D(0, 0, 1),	// rightDirection
//				new Vector3D(0, 1, 0),	// topDirection
//				rInner,	// baseRadius
//				hInner,		// height
//				h1Inner,	// heightLowerInnerVertexP
//				h2Inner,	// heightUpperInnerVertexP
//				h1InnerV,	// heightLowerInnerVertexE
//				0.96,	// gCLAsTransmissionCoefficient
//				showFramesInner,	// showFrames
//				0.01,	// frameRadius
//				SurfaceColour.RED_SHINY,	// frameSurfaceProperty
//				// boolean showPlaceholderSurfaces,
//				(showLensesInner?LensElementType.IDEAL_THIN_LENS:LensElementType.GLASS_PANE),	// lensElementType
//				scene,	// parent
//				studio
//		);
		// create a new lens simplicial complex...
		innerCloak = new EditableLensSimplicialComplex(
				"inner cloak",	// description
				scene,	// parent
				studio
			);
		// ... and initialise it for tracing of rays with trajectories
		innerCloak.setLensTypeRepresentingFace(optics.raytrace.simplicialComplex.LensType.IDEAL_THIN_LENS);
		innerCloak.setShowStructureP(false);
		innerCloak.setShowStructureV(false);
		// initialise
		innerCloak.initialiseToOmnidirectionalLens(
				h1Inner / hInner,	// physicalSpaceFractionalLowerInnerVertexHeight
				h2Inner / hInner,	// physicalSpaceFractionalUpperInnerVertexHeight
				h1InnerV / hInner,	// virtualSpaceFractionalLowerInnerVertexHeight
				new Vector3D(0, hInner, 0),	// topVertex
				new Vector3D(0, 0, 0),	// baseCentre
				new Vector3D(-rInner, 0, 0)	// baseVertex1
			);
		scene.addSceneObject(innerCloak, showInner);
		
		// the outer cloak
		double fDOuter = 0.12;
		double h1Outer = 1.5;	// has to be big enough so that inner cloak fits into cell 0
		double rOuter = 4;	// has to be big enough so that inner cloak fits into cell 0
		double hOuter = 2*h1Outer;
		double h2Outer = 1.5*h1Outer;
		Vector3D baseCentreOuter = new Vector3D(0.12, -0.508, 0);
		EditableLensSimplicialComplex outerCloak = new EditableLensSimplicialComplex(
				"outer cloak",	// description
				scene,	// parent
				studio
			);
		// ... and initialise it for tracing of rays with trajectories
		outerCloak.setLensTypeRepresentingFace(optics.raytrace.simplicialComplex.LensType.IDEAL_THIN_LENS);
		outerCloak.setShowStructureP(false);
		outerCloak.setShowStructureV(false);
		// initialise
		outerCloak.initialiseToOmnidirectionalLens(
				h1Outer / hOuter,	// physicalSpaceFractionalLowerInnerVertexHeight
				h2Outer / hOuter,	// physicalSpaceFractionalUpperInnerVertexHeight
				1/(1/h1Outer - 1/fDOuter) / hOuter,	// virtualSpaceFractionalLowerInnerVertexHeight
				Vector3D.sum(
						baseCentreOuter,
						new Vector3D(hOuter*Math.sin(0.5*beta), hOuter*Math.cos(0.5*beta), 0)
					),	// topVertex
				baseCentreOuter,	// baseCentre
				Vector3D.sum(
						baseCentreOuter,
						new Vector3D(-rOuter*Math.cos(0.5*beta), rOuter*Math.sin(0.5*beta), 0)
					)	// baseVertex1
			);
//		EditableIdealLensCloak outerCloak_01 = new EditableIdealLensCloak(
//				"outer cloak",	// description
//				new Vector3D(0.12, -0.508, 0),	// baseCentre
//				new Vector3D(Math.cos(0.5*beta), -Math.sin(0.5*beta), 0),	// frontDirection
//				new Vector3D(0, 0, 1),	// rightDirection
//				new Vector3D(Math.sin(0.5*beta), Math.cos(0.5*beta), 0),	// topDirection
//				rOuter,	// baseRadius
//				2*h1Outer,		// height
//				h1Outer,	// heightLowerInnerVertexP
//				1.5*h1Outer,	// heightUpperInnerVertexP
//				1/(1/h1Outer - 1/fDOuter),	// heightLowerInnerVertexE
//				0.96,	// gCLAsTransmissionCoefficient
//				showFramesOuter,	// showFrames
//				0.01,	// frameRadius
//				SurfaceColour.GREEN_SHINY,	// frameSurfaceProperty
//				// boolean showPlaceholderSurfaces,
//				(showLensesOuter?LensElementType.IDEAL_THIN_LENS:LensElementType.GLASS_PANE),	// lensElementType
//				scene,	// parent
//				studio
//		);
		scene.addSceneObject(outerCloak, showOuter);
		
		// trace rays with trajectories
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

		
		// initialise the cloaks again, this time according to the parameters
		innerCloak.setLensTypeRepresentingFace(
				showLensesInner?
				optics.raytrace.simplicialComplex.LensType.IDEAL_THIN_LENS:
				optics.raytrace.simplicialComplex.LensType.NONE
			);
		innerCloak.setShowStructureP(showFramesInner);
		innerCloak.setShowStructureV(false);
		// initialise
		innerCloak.initialiseToOmnidirectionalLens(
				h1Inner / hInner,	// physicalSpaceFractionalLowerInnerVertexHeight
				h2Inner / hInner,	// physicalSpaceFractionalUpperInnerVertexHeight
				h1InnerV / hInner,	// virtualSpaceFractionalLowerInnerVertexHeight
				new Vector3D(0, hInner, 0),	// topVertex
				new Vector3D(0, 0, 0),	// baseCentre
				new Vector3D(-rInner, 0, 0)	// baseVertex1
			);
		
		outerCloak.setLensTypeRepresentingFace(
				showLensesOuter?
				optics.raytrace.simplicialComplex.LensType.IDEAL_THIN_LENS:
				optics.raytrace.simplicialComplex.LensType.NONE
			);
		outerCloak.setShowStructureP(showFramesOuter);
		outerCloak.setShowStructureV(false);
		outerCloak.setSurfacePropertyP(SurfaceColour.GREEN_SHINY);
		// initialise
		outerCloak.initialiseToOmnidirectionalLens(
				h1Outer / hOuter,	// physicalSpaceFractionalLowerInnerVertexHeight
				h2Outer / hOuter,	// physicalSpaceFractionalUpperInnerVertexHeight
				1/(1/h1Outer - 1/fDOuter) / hOuter,	// virtualSpaceFractionalLowerInnerVertexHeight
				Vector3D.sum(
						baseCentreOuter,
						new Vector3D(hOuter*Math.sin(0.5*beta), hOuter*Math.cos(0.5*beta), 0)
					),	// topVertex
				baseCentreOuter,	// baseCentre
				Vector3D.sum(
						baseCentreOuter,
						new Vector3D(-rOuter*Math.cos(0.5*beta), rOuter*Math.sin(0.5*beta), 0)
					)	// baseVertex1
			);

		
		// the arrow indicating the position of the inside camera
		if(activeCamera != CameraType.INSIDE)
			scene.addSceneObject(
					new EditableArrow(
							"Arrow pointing to inside-camera position",
							Vector3D.sum(insideCameraApertureCentre, insideCameraViewDirection.getWithLength(-1)),	// start point
							insideCameraApertureCentre,	// end point
							0.05,	// shaft radius
							0.2,	// tip length
							MyMath.deg2rad(30),	// tip angle
							new SurfaceColour(DoubleColour.WHITE, DoubleColour.WHITE, true),
							scene, studio
							),
					indicateInsideCameraPosition	// visibility
					);
		
		EditableScaledParametrisedSphere patternedSphere = new EditableScaledParametrisedSphere(
				"Patterned sphere", // description
				patternedSphereCentre, // centre
				patternedSphereRadius,	// radius
				new Vector3D(1, 1, 1),	// pole
				new Vector3D(1, 0, 0),	// phi0Direction
				0, Math.PI,	// sThetaMin, sThetaMax
				-Math.PI, Math.PI,	// sPhiMin, sPhiMax
				new SurfaceTiling(
						new SurfaceColour(DoubleColour.BLACK, DoubleColour.WHITE, false),	// like SurfaceColour.BLACK_SHINY, but doesn't throw a shadow
						new SurfaceColour(DoubleColour.GREY90, DoubleColour.WHITE, false),	// like SurfaceColour.GREY90_SHINY, but not shadow-throwing
						2*Math.PI/6,
						2*Math.PI/6
					),
				scene, studio);
		scene.addSceneObject(patternedSphere, showPatternedSphere);

		
		switch(activeCamera)
		{
		case INSIDE:
			studio.setCamera(
					new EditableRelativisticAnyFocusSurfaceCamera(
							"Inside camera",
							insideCameraApertureCentre,	// centre of aperture
							insideCameraViewDirection,	// viewDirection
							new Vector3D(0, 1, 0),	// top direction vector
							insideCameraHorizontalFOVDeg,	// horiontalViewAngle in degrees; 2*MyMath.rad2deg(Math.atan(2./10.)) gives same view angle as in previous version
							new Vector3D(0, 0, 0),	// beta
							cameraPixelsX, cameraPixelsY,	// logical number of pixels
							ExposureCompensationType.EC0,	// exposure compensation +0
							cameraMaxTraceLevel,	// maxTraceLevel
							new Plane(
									"focus plane",	// description
									Vector3D.sum(insideCameraApertureCentre, insideCameraViewDirection.getWithLength(insideCameraFocussingDistance)),	// pointOnPlane
									insideCameraViewDirection,	// normal
									null,	// surfaceProperty
									null,	// parent
									null	// studio
								),	// focus scene
							null,	// cameraFrameScene,
							insideCameraApertureSize,	// aperture size
							renderQuality.getBlurQuality(),	// blur quality
							renderQuality.getAntiAliasingQuality()	// anti-aliasing quality
						)
				);
			break;
		case OUTSIDE:
		default:
			studio.setCamera(getStandardCamera());
		}

	}
	
	
	
	//
	// GUI stuff
	//
	
	

	private JCheckBox showInnerCheckbox;
	private JCheckBox showOuterCheckbox;
	private JCheckBox showLensesInnerCheckbox;
	private JCheckBox showLensesOuterCheckbox;
	private JCheckBox showFramesInnerCheckbox;
	private JCheckBox showFramesOuterCheckbox;
	
	// light-ray trajectory
	private JCheckBox showTrajectoryCheckBox;
	private LabelledVector3DPanel trajectoryStartPointPanel;
	private LabelledVector3DPanel trajectoryStartDirectionPanel;
	private JButton setTrajectoryAccordingToInsideCamera;
	private LabelledDoublePanel	trajectoryRadiusPanel;
	private LabelledIntPanel trajectoryMaxTraceLevelPanel;

	// other scene objects
	private JComboBox<StudioInitialisationType> studioInitialisationComboBox;
	private JCheckBox indicateInsideCameraPositionCheckBox;
	private JCheckBox showPatternedSphereCheckBox;
	private LabelledVector3DPanel patternedSphereCentrePanel;
	private LabelledDoublePanel patternedSphereRadiusPanel;

	// cameras
	private JComboBox<CameraType> activeCameraComboBox;

	// main (outside) camera
	private LabelledVector3DPanel cameraViewCentrePanel;
	// private JButton setCameraViewCentreToCloakCentroidButton;
	private JButton setCameraViewCentreToInsideCameraPositionButton;
	private LabelledVector3DPanel cameraViewDirectionPanel;
	private LabelledDoublePanel cameraDistancePanel;
	private DoublePanel cameraHorizontalFOVDegPanel;
	private JComboBox<ApertureSizeType> cameraApertureSizeComboBox;
	private LabelledDoublePanel cameraFocussingDistancePanel;

	// inside camera
	private LabelledVector3DPanel insideCameraApertureCentrePanel;
	private JTextField insideCameraApertureCentreInfoTextField;
	private JTextField pointOfInterestTextField;
	private JButton updateInsideCameraApertureCentreInfoButton;
	private LabelledVector3DPanel insideCameraViewDirectionPanel;
	private DoublePanel insideCameraHorizontalFOVDegPanel;
	private JComboBox<ApertureSizeType> insideCameraApertureSizeComboBox;
	private LabelledDoublePanel insideCameraFocussingDistancePanel;

	
	@Override
	protected void createInteractiveControlPanel()
	{
		super.createInteractiveControlPanel();

		JTabbedPane tabbedPane = new JTabbedPane();
		interactiveControlPanel.add(tabbedPane, "span");

		JPanel cloaksPanel = new JPanel();
		cloaksPanel.setLayout(new MigLayout("insets 0"));
		tabbedPane.addTab("Cloaks", cloaksPanel);

		JPanel innerLensCloakPanel = new JPanel();
		innerLensCloakPanel.setLayout(new MigLayout("insets 0"));
		innerLensCloakPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Inner cloak"));
		cloaksPanel.add(innerLensCloakPanel, "wrap");

		showInnerCheckbox = new JCheckBox("Show");
		showInnerCheckbox.setSelected(showInner);
		innerLensCloakPanel.add(showInnerCheckbox, "span");

		showLensesInnerCheckbox = new JCheckBox("Show lenses");
		showLensesInnerCheckbox.setSelected(showLensesInner);
		innerLensCloakPanel.add(showLensesInnerCheckbox, "span");

		showFramesInnerCheckbox = new JCheckBox("Show frames");
		showFramesInnerCheckbox.setSelected(showFramesInner);
		innerLensCloakPanel.add(showFramesInnerCheckbox, "span");

		JPanel outerLensCloakPanel = new JPanel();
		outerLensCloakPanel.setLayout(new MigLayout("insets 0"));
		outerLensCloakPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Outer cloak"));
		cloaksPanel.add(outerLensCloakPanel, "wrap");

		showOuterCheckbox = new JCheckBox("Show");
		showOuterCheckbox.setSelected(showOuter);
		outerLensCloakPanel.add(showOuterCheckbox, "span");

		showLensesOuterCheckbox = new JCheckBox("Show lenses");
		showLensesOuterCheckbox.setSelected(showLensesOuter);
		outerLensCloakPanel.add(showLensesOuterCheckbox, "span");

		showFramesOuterCheckbox = new JCheckBox("Show frames");
		showFramesOuterCheckbox.setSelected(showFramesOuter);
		outerLensCloakPanel.add(showFramesOuterCheckbox, "span");
		
		
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
		
		setTrajectoryAccordingToInsideCamera = new JButton("Set to inside camera position and view direction");
		setTrajectoryAccordingToInsideCamera.addActionListener(this);
		trajectoryPanel.add(setTrajectoryAccordingToInsideCamera, "span");
		
		trajectoryRadiusPanel = new LabelledDoublePanel("Trajectory radius");
		trajectoryRadiusPanel.setNumber(trajectoryRadius);
		trajectoryPanel.add(trajectoryRadiusPanel, "span");
		
		trajectoryMaxTraceLevelPanel = new LabelledIntPanel("Max. trace level");
		trajectoryMaxTraceLevelPanel.setNumber(trajectoryMaxTraceLevel);
		trajectoryPanel.add(trajectoryMaxTraceLevelPanel, "span");
		

		//
		// Other scene-objects panel
		//
		
		JPanel otherObjectsPanel = new JPanel();
		otherObjectsPanel.setLayout(new MigLayout("insets 0"));
		tabbedPane.addTab("Other scene objects", otherObjectsPanel);

		studioInitialisationComboBox = new JComboBox<StudioInitialisationType>(StudioInitialisationType.limitedValuesForBackgrounds);
		studioInitialisationComboBox.setSelectedItem(studioInitialisation);
		otherObjectsPanel.add(GUIBitsAndBobs.makeRow("Initialise backdrop to", studioInitialisationComboBox), "span");
		
		indicateInsideCameraPositionCheckBox = new JCheckBox("Show arrow (from inside-camera view direction) indicating inside-camera position");
		// indicateInsideCameraPositionCheckBox.setToolTipText("The inside-camera position is indicated by the tip of an arrow with direction (-insideCameraViewDirection)");
		indicateInsideCameraPositionCheckBox.setSelected(indicateInsideCameraPosition);
		otherObjectsPanel.add(indicateInsideCameraPositionCheckBox, "span");
		
		JPanel patternedSpherePanel = new JPanel();
		patternedSpherePanel.setBorder(GUIBitsAndBobs.getTitledBorder("Patterned sphere"));
		patternedSpherePanel.setLayout(new MigLayout("insets 0"));
		otherObjectsPanel.add(patternedSpherePanel, "span");
		
		showPatternedSphereCheckBox = new JCheckBox("Show");
		showPatternedSphereCheckBox.setSelected(showPatternedSphere);
		patternedSpherePanel.add(showPatternedSphereCheckBox, "span");
		
		patternedSphereCentrePanel = new LabelledVector3DPanel("Centre");
		patternedSphereCentrePanel.setVector3D(patternedSphereCentre);
		patternedSpherePanel.add(patternedSphereCentrePanel, "span");
		
		patternedSphereRadiusPanel = new LabelledDoublePanel("Radius");
		patternedSphereRadiusPanel.setNumber(patternedSphereRadius);
		patternedSpherePanel.add(patternedSphereRadiusPanel, "span");

		
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

		
		// main (outside) camera
		
		JPanel outsideCameraPanel = new JPanel();
		outsideCameraPanel.setLayout(new MigLayout("insets 0"));
		camerasTabbedPane.addTab("Outside camera", outsideCameraPanel);
		
		JPanel cameraViewCentreJPanel = new JPanel();
		cameraViewCentreJPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Centre of view"));
		cameraViewCentreJPanel.setLayout(new MigLayout("insets 0"));
		outsideCameraPanel.add(cameraViewCentreJPanel, "span");

		cameraViewCentrePanel = new LabelledVector3DPanel("Position");
		cameraViewCentrePanel.setVector3D(cameraViewCentre);
		cameraViewCentreJPanel.add(cameraViewCentrePanel, "span");
		
		setCameraViewCentreToInsideCameraPositionButton = new JButton("Set to position of inside camera");
		setCameraViewCentreToInsideCameraPositionButton.addActionListener(this);
		
		cameraViewCentreJPanel.add(setCameraViewCentreToInsideCameraPositionButton, "span");
		
		cameraViewDirectionPanel = new LabelledVector3DPanel("View direction");
		cameraViewDirectionPanel.setVector3D(cameraViewDirection);
		outsideCameraPanel.add(cameraViewDirectionPanel, "span");
		
		cameraDistancePanel = new LabelledDoublePanel("Camera distance");
		cameraDistancePanel.setNumber(cameraDistance);
		outsideCameraPanel.add(cameraDistancePanel, "span");
		
		cameraHorizontalFOVDegPanel = new DoublePanel();
		cameraHorizontalFOVDegPanel.setNumber(cameraHorizontalFOVDeg);
		outsideCameraPanel.add(GUIBitsAndBobs.makeRow("Horizontal FOV", cameraHorizontalFOVDegPanel, "°"), "span");
		
		cameraApertureSizeComboBox = new JComboBox<ApertureSizeType>(ApertureSizeType.values());
		cameraApertureSizeComboBox.setSelectedItem(cameraApertureSize);
		outsideCameraPanel.add(GUIBitsAndBobs.makeRow("Camera aperture", cameraApertureSizeComboBox), "span");		
		
		cameraFocussingDistancePanel = new LabelledDoublePanel("Focussing distance");
		cameraFocussingDistancePanel.setNumber(cameraFocussingDistance);
		outsideCameraPanel.add(cameraFocussingDistancePanel, "span");
		
		
		// inside camera
		
		JPanel insideCameraPanel = new JPanel();
		insideCameraPanel.setLayout(new MigLayout("insets 0"));
		camerasTabbedPane.addTab("Inside camera", insideCameraPanel);
		
		insideCameraApertureCentrePanel = new LabelledVector3DPanel("Aperture centre");
		insideCameraApertureCentrePanel.setVector3D(insideCameraApertureCentre);
		insideCameraPanel.add(insideCameraApertureCentrePanel, "span");
		
		insideCameraApertureCentreInfoTextField = new JTextField(40);
		insideCameraApertureCentreInfoTextField.setEditable(false);
		insideCameraApertureCentreInfoTextField.setText("Click on Update button to show info");
		updateInsideCameraApertureCentreInfoButton = new JButton("Update");
		updateInsideCameraApertureCentreInfoButton.addActionListener(this);
		insideCameraPanel.add(GUIBitsAndBobs.makeRow(insideCameraApertureCentreInfoTextField, updateInsideCameraApertureCentreInfoButton), "span");
		
		insideCameraViewDirectionPanel = new LabelledVector3DPanel("View direction");
		insideCameraViewDirectionPanel.setVector3D(insideCameraViewDirection);
		insideCameraPanel.add(insideCameraViewDirectionPanel, "span");
		
		insideCameraHorizontalFOVDegPanel = new DoublePanel();
		insideCameraHorizontalFOVDegPanel.setNumber(insideCameraHorizontalFOVDeg);
		insideCameraPanel.add(GUIBitsAndBobs.makeRow("Horizontal FOV", insideCameraHorizontalFOVDegPanel, "°"), "span");
		
		insideCameraApertureSizeComboBox = new JComboBox<ApertureSizeType>(ApertureSizeType.values());
		insideCameraApertureSizeComboBox.setSelectedItem(insideCameraApertureSize);
		insideCameraPanel.add(GUIBitsAndBobs.makeRow("Camera aperture", insideCameraApertureSizeComboBox), "span");		
		
		insideCameraFocussingDistancePanel = new LabelledDoublePanel("Focussing distance");
		insideCameraFocussingDistancePanel.setNumber(insideCameraFocussingDistance);
		insideCameraPanel.add(insideCameraFocussingDistancePanel, "span");
		
		pointOfInterestTextField = new JTextField(20);
		pointOfInterestTextField.setEditable(false);
//		pointOfInterestTextField.setText(getRotationPoint().toString());
		insideCameraPanel.add(pointOfInterestTextField, "span");

//		setCameraApertureCentreToPointOfInterestButton = new JButton("Set to point about which image rotates");
//		setCameraApertureCentreToPointOfInterestButton.addActionListener(this);
//		insideCameraPanel.add(setCameraApertureCentreToPointOfInterestButton);

	}
	
	@Override
	protected void acceptValuesInInteractiveControlPanel()
	{
		// inner cloak
		showInner = showInnerCheckbox.isSelected();
		showLensesInner = showLensesInnerCheckbox.isSelected();
		showFramesInner = showFramesInnerCheckbox.isSelected();

		// outer cloak
		showOuter = showOuterCheckbox.isSelected();
		showLensesOuter = showLensesOuterCheckbox.isSelected();
		showFramesOuter = showFramesOuterCheckbox.isSelected();
		
		// trajectory
		
		showTrajectory = showTrajectoryCheckBox.isSelected();
		trajectoryStartPoint = trajectoryStartPointPanel.getVector3D();
		trajectoryStartDirection = trajectoryStartDirectionPanel.getVector3D();
		trajectoryRadius = trajectoryRadiusPanel.getNumber();
		trajectoryMaxTraceLevel = trajectoryMaxTraceLevelPanel.getNumber();

		// other scene objects
		studioInitialisation = (StudioInitialisationType)(studioInitialisationComboBox.getSelectedItem());
		indicateInsideCameraPosition = indicateInsideCameraPositionCheckBox.isSelected();
		showPatternedSphere = showPatternedSphereCheckBox.isSelected();
		patternedSphereCentre = patternedSphereCentrePanel.getVector3D();
		patternedSphereRadius = patternedSphereRadiusPanel.getNumber();
		
		// cameras
		activeCamera = (CameraType)(activeCameraComboBox.getSelectedItem());

		cameraViewCentre = cameraViewCentrePanel.getVector3D();
		cameraViewDirection = cameraViewDirectionPanel.getVector3D();
		cameraDistance = cameraDistancePanel.getNumber();
		cameraHorizontalFOVDeg = cameraHorizontalFOVDegPanel.getNumber();
		cameraApertureSize = (ApertureSizeType)(cameraApertureSizeComboBox.getSelectedItem());
		cameraFocussingDistance = cameraFocussingDistancePanel.getNumber();
		
		insideCameraApertureCentre = insideCameraApertureCentrePanel.getVector3D();
		insideCameraViewDirection = insideCameraViewDirectionPanel.getVector3D();
		insideCameraHorizontalFOVDeg = insideCameraHorizontalFOVDegPanel.getNumber();
		insideCameraApertureSize = (ApertureSizeType)(insideCameraApertureSizeComboBox.getSelectedItem());
		insideCameraFocussingDistance = insideCameraFocussingDistancePanel.getNumber();
	}
	
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource().equals(updateInsideCameraApertureCentreInfoButton))
		{
			acceptValuesInInteractiveControlPanel();
			try {
				populateStudio();
			} catch (SceneException e1) {
				e1.printStackTrace();
			}
			insideCameraApertureCentreInfoTextField.setText(
					"Inside camera is centred in simplex #"+
							innerCloak.getLensSimplicialComplex().getIndexOfSimplexContainingPosition(insideCameraApertureCentre)
						);
		}
		else if(e.getSource().equals(setCameraViewCentreToInsideCameraPositionButton))
		{
			cameraViewCentrePanel.setVector3D(insideCameraApertureCentrePanel.getVector3D());
		}
		else if(e.getSource().equals(setTrajectoryAccordingToInsideCamera))
		{
			trajectoryStartPointPanel.setVector3D(insideCameraApertureCentrePanel.getVector3D());
			trajectoryStartDirectionPanel.setVector3D(insideCameraViewDirectionPanel.getVector3D());
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
		(new TwoNestedCloaksRotationVisualiser()).run();
	}
}
