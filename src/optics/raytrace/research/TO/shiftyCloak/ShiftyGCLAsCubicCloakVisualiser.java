package optics.raytrace.research.TO.shiftyCloak;

import java.awt.event.ActionEvent;
import java.io.PrintStream;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import math.*;
import net.miginfocom.swing.MigLayout;
import optics.raytrace.sceneObjects.*;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.surfaces.SurfaceColour;
import optics.raytrace.surfaces.SurfaceColourLightSourceIndependent;
import optics.raytrace.cameras.PinholeCamera.ExposureCompensationType;
import optics.raytrace.core.*;
import optics.DoubleColour;
import optics.raytrace.NonInteractiveTIMActionEnum;
import optics.raytrace.NonInteractiveTIMEngine;
import optics.raytrace.GUI.cameras.RenderQualityEnum;
import optics.raytrace.GUI.lowLevel.ApertureSizeType;
import optics.raytrace.GUI.lowLevel.DoublePanel;
import optics.raytrace.GUI.lowLevel.GUIBitsAndBobs;
import optics.raytrace.GUI.lowLevel.IntPanel;
import optics.raytrace.GUI.lowLevel.LabelledDoubleColourPanel;
import optics.raytrace.GUI.lowLevel.LabelledDoublePanel;
import optics.raytrace.GUI.lowLevel.LabelledIntPanel;
import optics.raytrace.GUI.lowLevel.LabelledVector3DPanel;
import optics.raytrace.GUI.sceneObjects.EditableCubicShiftyCloak;


public class ShiftyGCLAsCubicCloakVisualiser extends NonInteractiveTIMEngine
{
	// outer cloak / inner cloak
	private boolean showShiftyCloakO, showShiftyCloakI, showShiftyCloakGCLAsO, showShiftyCloakGCLAsI, asymmetricConfiguration;
	private Vector3D centre;
	private double outsideCubeSideLengthO, insideCubeSideLengthO, outsideCubeSideLengthI, insideCubeSideLengthI;
	private Vector3D deltaO, deltaI;
	private boolean showFramesO, showFramesI;

	
	// trajectories
	
	/**
	 * show trajectories
	 */
	private boolean showTrajectory1, showTrajectory2, showTrajectory3;
	
	/**
	 * start point of the light-ray trajectories
	 */
	private Vector3D trajectory1StartPoint, trajectory2StartPoint, trajectory3StartPoint;
	
	/**
	 * initial direction of the light-ray trajectories
	 */
	private Vector3D trajectory1StartDirection, trajectory2StartDirection, trajectory3StartDirection;
	
	/**
	 * radius of the trajectories
	 */
	private double trajectoryRadius;
	
	/**
	 * max trace level for trajectory tracing
	 */
	private int trajectoryMaxTraceLevel;
	
	/**
	 * report raytracing progress to console
	 */
	private boolean reportToConsole;
	
	
	
	// rest of  the scene
	
	/**
	 * Determines how to initialise the backdrop
	 */
	private StudioInitialisationType studioInitialisation;

	/**
	 * the centres of spheres that can be placed in the scene
	 */
	private Vector3D[] sphereCentres;
	
	/**
	 * the radii of spheres that can be placed in the scene
	 */
	private double[] sphereRadii;
	
	/**
	 * the colours of spheres that can be placed in the scene
	 */
	private DoubleColour[] sphereColours;
	
	/**
	 * the visibilities of spheres that can be placed in the scene
	 */
	private boolean[] sphereVisibilities;

	
	//
	// the camera's movie mode
	//
	
	/**
	 * direction of the rotation axis, which passes through the view centre, of the camera when in movie mode
	 */
	private Vector3D cameraRotationAxisDirection;

	
	
	public ShiftyGCLAsCubicCloakVisualiser()
	{
		nonInteractiveTIMAction = NonInteractiveTIMActionEnum.INTERACTIVE;
		renderQuality = RenderQualityEnum.DRAFT;
		
		// camera parameters; these are often set (or altered) in createStudio()
		cameraViewDirection = new Vector3D(0, -1.5, 10);
		cameraViewCentre = new Vector3D(0, 0, 0);
		cameraDistance = 10;
		cameraFocussingDistance = 10;
		cameraHorizontalFOVDeg = 10;
		cameraMaxTraceLevel = 1000;
		cameraPixelsX = 640;
		cameraPixelsY = 480;
		cameraApertureSize = ApertureSizeType.PINHOLE;
		cameraExposureCompensation = ExposureCompensationType.EC0;
		
		movie = false;
		// if movie = true, then the following are relevant:
		numberOfFrames = 10;
		firstFrame = 0;
		lastFrame = numberOfFrames-1;
		cameraRotationAxisDirection = new Vector3D(0, 1, 0);

//		private boolean showShiftyCloakGCLAsO, showShiftyCloakGCLAsI;
//		private Vector3D centre;
//		private double outsideCubeSideLengthO, insideCubeSideLengthO, outsideCubeSideLengthI, insideCubeSideLengthI;
//		private Vector3D deltaO, deltaI;
//		private boolean showFramesO, showFramesI;

		// the shifty cloaks
		centre = new Vector3D(0, 0, 0);
		asymmetricConfiguration = true;

		// Outer shifty cloak
		showShiftyCloakO = true;
		showShiftyCloakGCLAsO = false;
		outsideCubeSideLengthO = 1;
		insideCubeSideLengthO = 0.8;
		deltaO = new Vector3D(0, .2, 0);
		showFramesO = true;

		// Inner shifty cloak
		showShiftyCloakI = false;
		showShiftyCloakGCLAsI = false;
		outsideCubeSideLengthI = 0.3;
		insideCubeSideLengthI = 0.09;
		deltaI = new Vector3D(1, 0, 0);
		showFramesI = true;


		// trajectories
		showTrajectory1 = true;
		showTrajectory2 = true;
		showTrajectory3 = true;
		trajectory1StartPoint = new Vector3D(-10,  0.3, 0.01);
		trajectory2StartPoint = new Vector3D(-10,  0.0, 0.01);
		trajectory3StartPoint = new Vector3D(-10, -0.3, 0.01);
		trajectory1StartDirection = new Vector3D(1, 0, 0);
		trajectory2StartDirection = new Vector3D(1, 0, 0);
		trajectory3StartDirection = new Vector3D(1, 0, 0);
		trajectoryRadius = 0.005;
		trajectoryMaxTraceLevel = 100;
		reportToConsole = false;
		traceRaysWithTrajectory = false;	// don't automatically trace rays with trajectory, but do this in a bespoke way

		
		// rest of the scene
		studioInitialisation = StudioInitialisationType.MINIMALIST;	// the backdrop

		sphereCentres = new Vector3D[3];
		sphereRadii = new double[3];
		sphereColours = new DoubleColour[3];
		sphereVisibilities = new boolean[3];
		
		sphereCentres[0] = new Vector3D(-0.03, 0, 0);
		sphereRadii[0] = 0.01;
		sphereColours[0] = new DoubleColour(1, 0, 0);
		sphereVisibilities[0] = true;

		sphereCentres[1] = new Vector3D(0, 0, 0);
		sphereRadii[1] = 0.01;
		sphereColours[1] = new DoubleColour(0, 1, 0);
		sphereVisibilities[1] = true;

		sphereCentres[2] = new Vector3D(.03, 0, 0);
		sphereRadii[2] = 0.01;
		sphereColours[2] = new DoubleColour(0, 0, 1);
		sphereVisibilities[2] = true;

		
		windowTitle = "Dr TIM's shifty-GCLAs-cloak visualiser";
		windowWidth = 1250;
		windowHeight = 650;

	}

	@Override
	public String getClassName()
	{
		return "ShiftyGCLAsCloakVisualiser"	// the name
				;
	}
	
	@Override
	public void writeParameters(PrintStream printStream)
	{
		// write any parameters not defined in NonInteractiveTIMEngine
		printStream.println("Scene initialisation");
		printStream.println();
		
		// the shifty cloaks
		
		printStream.println("centre = "+centre);
		printStream.println("asymmetricConfiguration = "+asymmetricConfiguration);
		printStream.println("showShiftyCloakO = "+showShiftyCloakO);
		printStream.println("showShiftyCloakGCLAsO = "+showShiftyCloakGCLAsO);
		printStream.println("outsideCubeSideLengthO = "+outsideCubeSideLengthO);
		printStream.println("insideCubeSideLengthO = "+insideCubeSideLengthO);
		printStream.println("deltaO = "+deltaO);
		printStream.println("showFramesO = "+showFramesO);
		printStream.println("showShiftyCloakI = "+showShiftyCloakI);
		printStream.println("showShiftyCloakGCLAsI = "+showShiftyCloakGCLAsI);
		printStream.println("outsideCubeSideLengthI = "+outsideCubeSideLengthI);
		printStream.println("insideCubeSideLengthI = "+insideCubeSideLengthI);
		printStream.println("deltaI = "+deltaI);
		printStream.println("showFramesI = "+showFramesI);


		// trajectories
		
		printStream.println("showTrajectory1 = "+showTrajectory1);
		if(showTrajectory1)
		{
			printStream.println("trajectory1StartPoint = "+trajectory1StartPoint);
			printStream.println("trajectory1StartDirection = "+trajectory1StartDirection);
		}

		printStream.println("showTrajectory2 = "+showTrajectory2);
		if(showTrajectory2)
		{
			printStream.println("trajectory2StartPoint = "+trajectory2StartPoint);
			printStream.println("trajectory2StartDirection = "+trajectory2StartDirection);
		}
		
		printStream.println("showTrajectory3 = "+showTrajectory3);
		if(showTrajectory3)
		{
			printStream.println("trajectory3StartPoint = "+trajectory3StartPoint);
			printStream.println("trajectory3StartDirection = "+trajectory3StartDirection);
		}

		if(showTrajectory1 || showTrajectory2 || showTrajectory3)
		{
			printStream.println("trajectoryRadius = "+trajectoryRadius);
			printStream.println("trajectoryMaxTraceLevel = "+trajectoryMaxTraceLevel);
		}
		
		printStream.println("reportToConsole = "+reportToConsole);
		
		// rest of scene
		
		printStream.println("studioInitialisation = "+studioInitialisation);
		for(int i=0; i<3; i++)
		{
			printStream.println("sphereCentres["+i+"] = "+sphereCentres[i]);
			printStream.println("sphereRadii["+i+"] = "+sphereRadii[i]);
			printStream.println("sphereColours["+i+"] = "+sphereColours[i]);
			printStream.println("sphereVisibilities["+i+"] = "+sphereVisibilities[i]);
		}

		printStream.println();
		
		// movie parameters
		printStream.println("movie = "+movie);
		printStream.println("cameraRotationAxisDirection = "+cameraRotationAxisDirection);


		// write all parameters defined in NonInteractiveTIMEngine
		super.writeParameters(printStream);		
	}

	
	private EditableCubicShiftyCloak cloakO, cloakI;
	private Vector3D frame0CameraViewDirection;

	/**
	 * Define scene, lights, and/or camera.
	 * @return a studio, i.e. scene, lights and camera
	 */
	@Override
	public void populateStudio()
	{
		studio = new Studio();

		// the scene
		SceneObjectContainer scene = new SceneObjectContainer("the scene", null, studio);
		studio.setScene(scene);

		// the standard scene objects
		// initialise the scene and lights
		StudioInitialisationType.initialiseSceneAndLights(
				studioInitialisation,
				scene,
				studio
			);

		// add any other scene objects
		
		// first the coloured spheres that can be added to the scene
		for(int i=0; i<3; i++)
			scene.addSceneObject(
					new Sphere(
							"Coloured sphere #"+i,	// description
							sphereCentres[i],	// centre
							sphereRadii[i],	// radius
							new SurfaceColour(sphereColours[i], DoubleColour.WHITE, false),	// surface property: sphereColours[i], made shiny; don't throw shadow
							scene,
							studio
						),
					sphereVisibilities[i]
				);
		
		double frameRadius = 0.01*outsideCubeSideLengthO;
		
		// add outer shifty cloak
		cloakO = new EditableCubicShiftyCloak(
				"Outer shifty GCLAs cloak",	// description
				centre,
				new Vector3D(1, 0, 0),	// uDirection
				new Vector3D(0, 1, 0),	// vDirection
				outsideCubeSideLengthO,	// sideLengthOutside
				insideCubeSideLengthO,	// sideLengthInside
				deltaO,	// delta
				true,	// showGCLAs
				0.96,	// gCLAsTransmissionCoefficient
				false,	// showFrames
				frameRadius,	// frameRadius
				SurfaceColour.GREEN_SHINY,	// frameSurfaceProperty		
				scene, studio
			);
		scene.addSceneObject(cloakO, showShiftyCloakO);

		// add inner shifty cloak
		cloakI = new EditableCubicShiftyCloak(
				"Inner shifty GCLAs cloak",	// description
				centre,
				new Vector3D(1, 0, 0),	// uDirection
				new Vector3D(0, 1, 0),	// vDirection
				outsideCubeSideLengthI,	// sideLength
				insideCubeSideLengthI,	// sideLengthI
				deltaI,	// delta
				true,	// showGCLAs
				0.96,	// gCLAsTransmissionCoefficient
				false,	// showFrames
				frameRadius,	// frameRadius
				SurfaceColour.BLUE_SHINY,	// frameSurfaceProperty		
				scene, studio
			);
		scene.addSceneObject(cloakI, showShiftyCloakI);
		
		// trace the ray trajectories
		
		if(showTrajectory1)
		scene.addSceneObject(new RayTrajectory(
				"Trajectory 1",
				trajectory1StartPoint,	// start point
				0,	// start time
				trajectory1StartDirection,	// initial direction
				trajectoryRadius,	// radius
				new SurfaceColourLightSourceIndependent(DoubleColour.RED, false),	// don't throw shadow
				trajectoryMaxTraceLevel,	// max trace level
				reportToConsole,	// reportToConsole
				scene,
				studio
			)
		);

		if(showTrajectory2)
		scene.addSceneObject(new RayTrajectory(
				"Trajectory 2",
				trajectory2StartPoint,	// start point
				0,	// start time
				trajectory2StartDirection,	// initial direction
				trajectoryRadius,	// radius
				new SurfaceColourLightSourceIndependent(DoubleColour.GREEN, false),	// don't throw shadow
				trajectoryMaxTraceLevel,	// max trace level
				reportToConsole,	// reportToConsole
				scene,
				studio
			)
		);

		if(showTrajectory3)
		scene.addSceneObject(new RayTrajectory(
				"Trajectory 3",
				trajectory3StartPoint,	// start point
				0,	// start time
				trajectory3StartDirection,	// initial direction
				trajectoryRadius,	// radius
				new SurfaceColourLightSourceIndependent(DoubleColour.BLUE, false),	// don't throw shadow
				trajectoryMaxTraceLevel,	// max trace level
				reportToConsole,	// reportToConsole
				scene,
				studio
			)
		);


		// trace the rays with trajectory through the scene
		studio.traceRaysWithTrajectory();
	
		// remove outer shifty cloak and add it again
		scene.removeSceneObject(cloakO);
		cloakO = new EditableCubicShiftyCloak(
				"Outer shifty GCLAs cloak",	// description
				centre,
				new Vector3D(1, 0, 0),	// uDirection
				new Vector3D(0, 1, 0),	// vDirection
				outsideCubeSideLengthO,	// sideLength
				insideCubeSideLengthO,	// sideLengthI
				deltaO,	// delta
				showShiftyCloakGCLAsO,	// showGCLAs
				0.96,	// gCLAsTransmissionCoefficient
				showFramesO,	// showFrames
				frameRadius,	// frameRadius
				SurfaceColour.GREY40_SHINY,	// frameSurfaceProperty		
				scene, studio
			);
		scene.addSceneObject(cloakO, showShiftyCloakO);

		// remove inner shifty cloak and add it again
		scene.removeSceneObject(cloakI);
		cloakI = new EditableCubicShiftyCloak(
				"Inner shifty GCLAs cloak",	// description
				centre,
				new Vector3D(1, 0, 0),	// uDirection
				new Vector3D(0, 1, 0),	// vDirection
				outsideCubeSideLengthI,	// sideLength
				insideCubeSideLengthI,	// sideLengthI
				deltaI,	// delta
				showShiftyCloakGCLAsI,	// showGCLAs
				0.96,	// gCLAsTransmissionCoefficient
				showFramesI,	// showFrames
				frameRadius,	// frameRadius
				SurfaceColour.GREY80_SHINY,	// frameSurfaceProperty		
				scene, studio
			);
		scene.addSceneObject(cloakI, showShiftyCloakI);
		
		studio.setScene(scene);
		
		if(movie)
		{
			// calculate view position that corresponds to the current frame
			// initial camera position
			Vector3D initialCameraPosition = Vector3D.sum(cameraViewCentre, frame0CameraViewDirection.getWithLength(-cameraDistance));
			// the camera will move in a circle; calculate its centre
			Vector3D centreOfCircle = Geometry.getPointOnLineClosestToPoint(
					cameraViewCentre,	// pointOnLine
					cameraRotationAxisDirection,	// directionOfLine
					initialCameraPosition	// point
				);
			// construct two unit vectors that span the plane of the circle in which the camera will move
			Vector3D uHat = Vector3D.difference(initialCameraPosition, centreOfCircle).getNormalised();
			Vector3D vHat = Vector3D.crossProduct(cameraRotationAxisDirection, uHat).getNormalised();

			// define the azimuthal angle phi that parametrises the circle
			double phi = 2.*Math.PI*frame/numberOfFrames;
			System.out.println("LensCloakVisualiser::populateStudio: phi="+phi+"(="+MyMath.rad2deg(phi)+"deg)");
			
			// finally, calculate the view direction
			cameraViewDirection = Vector3D.difference(
					cameraViewCentre, 
					Vector3D.sum(centreOfCircle, uHat.getProductWith(Math.cos(phi)*cameraDistance), vHat.getProductWith(Math.sin(phi)*cameraDistance))
				);
		}
		studio.setCamera(getStandardCamera());
	}
	
	
	
	// GUI
	
	// shifty cloaks
	private LabelledVector3DPanel centrePanel;
		
	// outer shifty device
	private JCheckBox showShiftyCloakOCheckBox, showShiftyCloakGCLAsOCheckBox, showFramesOCheckBox;
	private LabelledVector3DPanel deltaOPanel;
	private LabelledDoublePanel outsideCubeSideLengthOPanel, insideCubeSideLengthOPanel;

	// inner shifty device
	private JCheckBox showShiftyCloakICheckBox, showShiftyCloakGCLAsICheckBox, showFramesICheckBox;
	private LabelledVector3DPanel deltaIPanel;
	private LabelledDoublePanel outsideCubeSideLengthIPanel, insideCubeSideLengthIPanel;

	// trajectory
	private JCheckBox showTrajectory1CheckBox, showTrajectory2CheckBox, showTrajectory3CheckBox, reportToConsoleCheckBox;
	private LabelledVector3DPanel trajectory1StartPointPanel, trajectory2StartPointPanel, trajectory3StartPointPanel;
	private LabelledVector3DPanel trajectory1StartDirectionPanel, trajectory2StartDirectionPanel, trajectory3StartDirectionPanel;
	private LabelledDoublePanel trajectoryRadiusPanel;
	private LabelledIntPanel trajectoryMaxTraceLevelPanel;

	// rest of scene
	private JComboBox<StudioInitialisationType> studioInitialisationComboBox;
	private LabelledVector3DPanel[] sphereCentrePanels;
	private LabelledDoublePanel[] sphereRadiusPanels;
	private LabelledDoubleColourPanel[] sphereColourPanels;
	private JCheckBox[] sphereVisibilityCheckBoxes;


	// camera
	private LabelledVector3DPanel cameraPositionPanel, cameraViewCentrePanel;
	private DoublePanel cameraHorizontalFOVDegPanel;
	private JComboBox<ApertureSizeType> cameraApertureSizeComboBox;
	private LabelledDoublePanel cameraFocussingDistancePanel;
	private LabelledIntPanel cameraMaxTraceLevelPanel;
	
	private JCheckBox movieCheckBox;
	private LabelledVector3DPanel cameraRotationAxisDirectionPanel;
	private IntPanel numberOfFramesPanel, firstFramePanel, lastFramePanel;


	
	@Override
	protected void createInteractiveControlPanel()
	{
		super.createInteractiveControlPanel();
		
		JTabbedPane tabbedPane = new JTabbedPane();
		interactiveControlPanel.add(tabbedPane, "span");
		
		

		//
		// the shifty cloaks
		//
		
		JPanel shiftyCloaksPanel = new JPanel();
		shiftyCloaksPanel.setLayout(new MigLayout("insets 0"));

		centrePanel = new LabelledVector3DPanel("Centre");
		centrePanel.setVector3D(centre);
		shiftyCloaksPanel.add(centrePanel, "wrap");
		
		// the outer shifty cloak
		JPanel outerShiftyCloakPanel = new JPanel();
		outerShiftyCloakPanel.setLayout(new MigLayout("insets 0"));
		outerShiftyCloakPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Outer shifty cloak"));

		showShiftyCloakOCheckBox = new JCheckBox("Show");
		showShiftyCloakOCheckBox.setSelected(showShiftyCloakO);
		outerShiftyCloakPanel.add(showShiftyCloakOCheckBox, "wrap");
		
		showShiftyCloakGCLAsOCheckBox = new JCheckBox("Show GCLAs");
		showShiftyCloakGCLAsOCheckBox.setSelected(showShiftyCloakGCLAsO);
		outerShiftyCloakPanel.add(showShiftyCloakGCLAsOCheckBox, "wrap");
		
		deltaOPanel = new LabelledVector3DPanel("Apparent shift of inner cube");
		deltaOPanel.setVector3D(deltaO);
		outerShiftyCloakPanel.add(deltaOPanel, "wrap");

		outsideCubeSideLengthOPanel = new LabelledDoublePanel("Side length of (cubic) outside");
		outsideCubeSideLengthOPanel.setNumber(outsideCubeSideLengthO);
		outerShiftyCloakPanel.add(outsideCubeSideLengthOPanel, "wrap");

		insideCubeSideLengthOPanel = new LabelledDoublePanel("Side length of inner cube");
		insideCubeSideLengthOPanel.setNumber(insideCubeSideLengthO);
		outerShiftyCloakPanel.add(insideCubeSideLengthOPanel, "wrap");

		showFramesOCheckBox = new JCheckBox("Show frames");
		showFramesOCheckBox.setSelected(showFramesO);
		outerShiftyCloakPanel.add(showFramesOCheckBox, "wrap");
		
		shiftyCloaksPanel.add(outerShiftyCloakPanel, "wrap");


		// the inner shifty cloak
		JPanel innerShiftyCloakPanel = new JPanel();
		innerShiftyCloakPanel.setLayout(new MigLayout("insets 0"));
		innerShiftyCloakPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Inner shifty cloak"));

		showShiftyCloakICheckBox = new JCheckBox("Show");
		showShiftyCloakICheckBox.setSelected(showShiftyCloakI);
		innerShiftyCloakPanel.add(showShiftyCloakICheckBox, "wrap");

		showShiftyCloakGCLAsICheckBox = new JCheckBox("Show GCLAs");
		showShiftyCloakGCLAsICheckBox.setSelected(showShiftyCloakGCLAsI);
		innerShiftyCloakPanel.add(showShiftyCloakGCLAsICheckBox, "wrap");
		
		deltaIPanel = new LabelledVector3DPanel("Apparent shift of inner cube");
		deltaIPanel.setVector3D(deltaI);
		innerShiftyCloakPanel.add(deltaIPanel, "wrap");

		outsideCubeSideLengthIPanel = new LabelledDoublePanel("Side length of (cubic) outside");
		outsideCubeSideLengthIPanel.setNumber(outsideCubeSideLengthI);
		innerShiftyCloakPanel.add(outsideCubeSideLengthIPanel, "wrap");

		insideCubeSideLengthIPanel = new LabelledDoublePanel("Side length of inner cube");
		insideCubeSideLengthIPanel.setNumber(insideCubeSideLengthI);
		innerShiftyCloakPanel.add(insideCubeSideLengthIPanel, "wrap");

		showFramesICheckBox = new JCheckBox("Show frames");
		showFramesICheckBox.setSelected(showFramesI);
		innerShiftyCloakPanel.add(showFramesICheckBox, "wrap");
		
		shiftyCloaksPanel.add(innerShiftyCloakPanel, "wrap");

		
		tabbedPane.addTab("Shifty cloaks", shiftyCloaksPanel);

		
		
		//
		// trajectory panel
		//
		
		JPanel trajectoryPanel = new JPanel();
		trajectoryPanel.setLayout(new MigLayout("insets 0"));
		
		JTabbedPane trajectoriesTabbedPane = new JTabbedPane();
		trajectoryPanel.add(trajectoriesTabbedPane, "span");

		// trajectory 1
		JPanel trajectory1Panel = new JPanel();
		trajectory1Panel.setLayout(new MigLayout("insets 0"));

		showTrajectory1CheckBox = new JCheckBox("Show trajectory 1");
		showTrajectory1CheckBox.setSelected(showTrajectory1);
		trajectory1Panel.add(showTrajectory1CheckBox, "wrap");

		trajectory1StartPointPanel = new LabelledVector3DPanel("Start point");
		trajectory1StartPointPanel.setVector3D(trajectory1StartPoint);
		trajectory1Panel.add(trajectory1StartPointPanel, "span");

		trajectory1StartDirectionPanel = new LabelledVector3DPanel("Initial direction");
		trajectory1StartDirectionPanel.setVector3D(trajectory1StartDirection);
		trajectory1Panel.add(trajectory1StartDirectionPanel, "span");
		
		trajectoriesTabbedPane.addTab("Trajectory 1", trajectory1Panel);
		
		// trajectory 2
		JPanel trajectory2Panel = new JPanel();
		trajectory2Panel.setLayout(new MigLayout("insets 0"));

		showTrajectory2CheckBox = new JCheckBox("Show trajectory 2");
		showTrajectory2CheckBox.setSelected(showTrajectory2);
		trajectory2Panel.add(showTrajectory2CheckBox, "wrap");

		trajectory2StartPointPanel = new LabelledVector3DPanel("Start point");
		trajectory2StartPointPanel.setVector3D(trajectory2StartPoint);
		trajectory2Panel.add(trajectory2StartPointPanel, "span");

		trajectory2StartDirectionPanel = new LabelledVector3DPanel("Initial direction");
		trajectory2StartDirectionPanel.setVector3D(trajectory2StartDirection);
		trajectory2Panel.add(trajectory2StartDirectionPanel, "span");
		
		trajectoriesTabbedPane.addTab("Trajectory 2", trajectory2Panel);

		// trajectory 3
		JPanel trajectory3Panel = new JPanel();
		trajectory3Panel.setLayout(new MigLayout("insets 0"));

		showTrajectory3CheckBox = new JCheckBox("Show trajectory 3");
		showTrajectory3CheckBox.setSelected(showTrajectory3);
		trajectory3Panel.add(showTrajectory3CheckBox, "wrap");

		trajectory3StartPointPanel = new LabelledVector3DPanel("Start point");
		trajectory3StartPointPanel.setVector3D(trajectory3StartPoint);
		trajectory3Panel.add(trajectory3StartPointPanel, "span");

		trajectory3StartDirectionPanel = new LabelledVector3DPanel("Initial direction");
		trajectory3StartDirectionPanel.setVector3D(trajectory3StartDirection);
		trajectory3Panel.add(trajectory3StartDirectionPanel, "span");
		
		trajectoriesTabbedPane.addTab("Trajectory 3", trajectory3Panel);

		trajectoryRadiusPanel = new LabelledDoublePanel("Radius");
		trajectoryRadiusPanel.setNumber(trajectoryRadius);
		trajectoryPanel.add(trajectoryRadiusPanel, "span");
		
		trajectoryMaxTraceLevelPanel = new LabelledIntPanel("Max. trace level");
		trajectoryMaxTraceLevelPanel.setNumber(trajectoryMaxTraceLevel);
		trajectoryPanel.add(trajectoryMaxTraceLevelPanel, "span");
		
		reportToConsoleCheckBox = new JCheckBox("Report raytracing progress to console");
		reportToConsoleCheckBox.setSelected(reportToConsole);
		trajectoryPanel.add(reportToConsoleCheckBox, "wrap");

		tabbedPane.addTab("Trajectories", trajectoryPanel);
		
		//
		// rest of scene panel
		//
		
		JPanel restOfScenePanel = new JPanel();
		restOfScenePanel.setLayout(new MigLayout("insets 0"));
		
		studioInitialisationComboBox = new JComboBox<StudioInitialisationType>(StudioInitialisationType.limitedValuesForBackgrounds);
		studioInitialisationComboBox.setSelectedItem(studioInitialisation);
		restOfScenePanel.add(GUIBitsAndBobs.makeRow("Initialise backdrop to", studioInitialisationComboBox), "span");
		
		JPanel spherePanels[] = new JPanel[3];
		sphereCentrePanels = new LabelledVector3DPanel[3];
		sphereRadiusPanels = new LabelledDoublePanel[3];
		sphereColourPanels = new LabelledDoubleColourPanel[3];
		sphereVisibilityCheckBoxes = new JCheckBox[3];
		for(int i=0; i<3; i++)
		{
			spherePanels[i] = new JPanel();
			spherePanels[i].setLayout(new MigLayout("insets 0"));
			spherePanels[i].setBorder(GUIBitsAndBobs.getTitledBorder("Sphere #"+(i+1)));
			
			sphereCentrePanels[i] = new LabelledVector3DPanel("Centre");
			sphereCentrePanels[i].setVector3D(sphereCentres[i]);
			spherePanels[i].add(sphereCentrePanels[i], "wrap");

			sphereRadiusPanels[i] = new LabelledDoublePanel("Radius");
			sphereRadiusPanels[i].setNumber(sphereRadii[i]);
			spherePanels[i].add(sphereRadiusPanels[i], "wrap");

			sphereColourPanels[i] = new LabelledDoubleColourPanel("Colour");
			sphereColourPanels[i].setDoubleColour(sphereColours[i]);
			spherePanels[i].add(sphereColourPanels[i], "wrap");

			sphereVisibilityCheckBoxes[i] = new JCheckBox("Visible");
			sphereVisibilityCheckBoxes[i].setSelected(sphereVisibilities[i]);
			spherePanels[i].add(sphereVisibilityCheckBoxes[i], "wrap");
			
			restOfScenePanel.add(spherePanels[i], "wrap");
		}
		
		tabbedPane.addTab("Rest of scene", restOfScenePanel);

		//
		// camera panel
		//
		
		JPanel cameraPanel = new JPanel();
		cameraPanel.setLayout(new MigLayout("insets 0"));

		cameraPositionPanel = new LabelledVector3DPanel("Aperture centre");
		cameraPositionPanel.setVector3D(Vector3D.difference(cameraViewCentre, cameraViewDirection.getWithLength(cameraDistance)));
		cameraPanel.add(cameraPositionPanel, "span");

		cameraViewCentrePanel = new LabelledVector3DPanel("Centre of view");
		cameraViewCentrePanel.setVector3D(cameraViewCentre);
		cameraPanel.add(cameraViewCentrePanel, "span");
		
		cameraHorizontalFOVDegPanel = new DoublePanel();
		cameraHorizontalFOVDegPanel.setNumber(cameraHorizontalFOVDeg);
		cameraPanel.add(GUIBitsAndBobs.makeRow("Horizontal FOV", cameraHorizontalFOVDegPanel, "°"), "span");
		
		cameraApertureSizeComboBox = new JComboBox<ApertureSizeType>(ApertureSizeType.values());
		cameraApertureSizeComboBox.setSelectedItem(cameraApertureSize);
		cameraApertureSizeComboBox.addActionListener(this);
		cameraPanel.add(GUIBitsAndBobs.makeRow("Aperture size", cameraApertureSizeComboBox), "span");		
		
		cameraFocussingDistancePanel = new LabelledDoublePanel("Focussing distance");
		cameraFocussingDistancePanel.setNumber(cameraFocussingDistance);
		cameraPanel.add(cameraFocussingDistancePanel, "span");

		cameraMaxTraceLevelPanel = new LabelledIntPanel("Max. trace level");
		cameraMaxTraceLevelPanel.setNumber(cameraMaxTraceLevel);
		cameraPanel.add(cameraMaxTraceLevelPanel, "span");
		
		// movie panel
		
		JPanel moviePanel = new JPanel();
		moviePanel.setBorder(GUIBitsAndBobs.getTitledBorder("Movie"));
		moviePanel.setLayout(new MigLayout("insets 0"));
		cameraPanel.add(moviePanel, "span");

		movieCheckBox = new JCheckBox("Create movie");
		movieCheckBox.setSelected(movie);
		moviePanel.add(movieCheckBox, "span");
		
		cameraRotationAxisDirectionPanel = new LabelledVector3DPanel("Direction of rotation axis");
		cameraRotationAxisDirectionPanel.setVector3D(cameraRotationAxisDirection);
		moviePanel.add(cameraRotationAxisDirectionPanel, "span");
		
		numberOfFramesPanel = new IntPanel();
		numberOfFramesPanel.setNumber(numberOfFrames);
		
		firstFramePanel = new IntPanel();
		firstFramePanel.setNumber(firstFrame);
		
		lastFramePanel = new IntPanel();
		lastFramePanel.setNumber(lastFrame);

		moviePanel.add(GUIBitsAndBobs.makeRow("Calculate frames", firstFramePanel, "to", lastFramePanel, "out of", numberOfFramesPanel), "wrap");

		tabbedPane.addTab("Camera", cameraPanel);
	}
	
	@Override
	protected void acceptValuesInInteractiveControlPanel()
	{
		super.acceptValuesInInteractiveControlPanel();
		
		// shifty cloaks
		
		centre = centrePanel.getVector3D();
		showShiftyCloakO = showShiftyCloakOCheckBox.isSelected();
		showShiftyCloakGCLAsO = showShiftyCloakGCLAsOCheckBox.isSelected();
		deltaO = deltaOPanel.getVector3D();
		outsideCubeSideLengthO = outsideCubeSideLengthOPanel.getNumber();
		insideCubeSideLengthO = insideCubeSideLengthOPanel.getNumber();
		showFramesO = showFramesOCheckBox.isSelected();
		showShiftyCloakI = showShiftyCloakICheckBox.isSelected();
		showShiftyCloakGCLAsI = showShiftyCloakGCLAsICheckBox.isSelected();
		deltaI = deltaIPanel.getVector3D();
		outsideCubeSideLengthI = outsideCubeSideLengthIPanel.getNumber();
		insideCubeSideLengthI = insideCubeSideLengthIPanel.getNumber();
		showFramesI = showFramesICheckBox.isSelected();
			

		// trajectories
		
		showTrajectory1 = showTrajectory1CheckBox.isSelected();
		trajectory1StartPoint = trajectory1StartPointPanel.getVector3D();
		trajectory1StartDirection = trajectory1StartDirectionPanel.getVector3D();
		showTrajectory2 = showTrajectory2CheckBox.isSelected();
		trajectory2StartPoint = trajectory2StartPointPanel.getVector3D();
		trajectory2StartDirection = trajectory2StartDirectionPanel.getVector3D();
		showTrajectory3 = showTrajectory3CheckBox.isSelected();
		trajectory3StartPoint = trajectory3StartPointPanel.getVector3D();
		trajectory3StartDirection = trajectory3StartDirectionPanel.getVector3D();
		trajectoryRadius = trajectoryRadiusPanel.getNumber();
		trajectoryMaxTraceLevel = trajectoryMaxTraceLevelPanel.getNumber();
		reportToConsole = reportToConsoleCheckBox.isSelected();
		
		// rest of scene
		
		studioInitialisation = (StudioInitialisationType)(studioInitialisationComboBox.getSelectedItem());
		for(int i=0; i<3; i++)
		{
			sphereCentres[i] = sphereCentrePanels[i].getVector3D();
			sphereRadii[i] = sphereRadiusPanels[i].getNumber();
			sphereColours[i] = sphereColourPanels[i].getDoubleColour();
			sphereVisibilities[i] = sphereVisibilityCheckBoxes[i].isSelected();
		}
		
		// camera
		
		cameraViewCentre = cameraViewCentrePanel.getVector3D();
		cameraViewDirection = Vector3D.difference(cameraViewCentre, cameraPositionPanel.getVector3D());
		cameraDistance = cameraViewDirection.getLength();
		cameraHorizontalFOVDeg = cameraHorizontalFOVDegPanel.getNumber();
		cameraApertureSize = (ApertureSizeType)(cameraApertureSizeComboBox.getSelectedItem());
		cameraFocussingDistance = cameraFocussingDistancePanel.getNumber();
		cameraMaxTraceLevel = cameraMaxTraceLevelPanel.getNumber();
		
		movie = movieCheckBox.isSelected();
		cameraRotationAxisDirection = cameraRotationAxisDirectionPanel.getVector3D();
		numberOfFrames = numberOfFramesPanel.getNumber();
		firstFrame = firstFramePanel.getNumber();
		lastFrame = lastFramePanel.getNumber();
		
		frame0CameraViewDirection = cameraViewDirection;
	}


	@Override
	public void actionPerformed(ActionEvent e)
	{
		super.actionPerformed(e);
		
//		if(e.getSource().equals(testButton))
//		{
//			// initialise spaceAroundBlackHole according to the current parameters
//			acceptValuesInInteractiveControlPanel();
//			populateStudio();
//			
//			infoTextField.setText("Don't press the button!");
//			
//			Vector3D testPosition = testPositionPanel.getVector3D();
//			System.out.println("test position "+testPosition+" inside cloak = "+cloak.insideObject(testPosition));
//			System.out.println("n tensor at test position = ");
//			Matrix n = surfaceOfTOAbyssCloak.calculateEpsilonMuTensor(testPosition);
//			for(int r=0; r<n.getRowDimension(); r++)
//			{
//				System.out.print(" \t");
//				for(int c=0; c<n.getColumnDimension(); c++) System.out.print(" \t"+n.get(r, c));
//				System.out.println(" ");
//			}
//			// infoTextField.setText("Refractive index at test position = "+spaceAroundBlackHoleMetric.getEpsilonMuTensor(testPositionPanel.getVector3D()).get(0, 0));
//		}
	}

 	public static void main(final String[] args)
   	{
  		(new ShiftyGCLAsCubicCloakVisualiser()).run();
  	}
 }
