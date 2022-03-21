package optics.raytrace.research.TO.ChenBelinBlackHole;

import java.awt.event.ActionEvent;
import java.io.PrintStream;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import math.*;
import math.ODE.IntegrationType;
import net.miginfocom.swing.MigLayout;
import optics.raytrace.sceneObjects.*;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.surfaces.SurfaceOfChenBelinMetricSpace;
import optics.raytrace.surfaces.SurfaceColour;
import optics.raytrace.surfaces.SurfaceColourLightSourceIndependent;
import optics.raytrace.surfaces.SurfacePropertyAverageWeighted;
import optics.raytrace.surfaces.Transparent;
import optics.raytrace.cameras.PinholeCamera.ExposureCompensationType;
import optics.raytrace.core.*;
import optics.DoubleColour;
import optics.raytrace.NonInteractiveTIMActionEnum;
import optics.raytrace.NonInteractiveTIMEngine;
import optics.raytrace.GUI.cameras.RenderQualityEnum;
import optics.raytrace.GUI.lowLevel.ApertureSizeType;
import optics.raytrace.GUI.lowLevel.DoublePanel;
import optics.raytrace.GUI.lowLevel.GUIBitsAndBobs;
import optics.raytrace.GUI.lowLevel.LabelledDoubleColourPanel;
import optics.raytrace.GUI.lowLevel.LabelledDoublePanel;
import optics.raytrace.GUI.lowLevel.LabelledIntPanel;
import optics.raytrace.GUI.lowLevel.LabelledVector3DPanel;
import optics.raytrace.GUI.sceneObjects.SphericallySymmetricRefractiveIndexDistributionSimulationType;


/**
 * The main method renders the image defined by createStudio(), saves it to a file
 * (whose name is given by the constant FILENAME), and displays it in a new window.
 * 
 * @author  Johannes Courtial
 */
public class SpaceAroundBlackHoleVisualiser extends NonInteractiveTIMEngine
{
	// black hole
	
	private SphericallySymmetricRefractiveIndexDistributionSimulationType simulationType;
	private IntegrationType integrationType;
	private boolean showBlackHole;
	private Vector3D blackHoleCentre;
	private double blackHoleHorizonRadius, blackHoleJParameter;
	private boolean indicateBlackHoleHorizon, indicatePhotonSphere;
	private double blackHoleSimulationSphereRadius, blackHoleDeltaTau, blackHoleDeltaXMax;
	private int blackHoleSimulationSphereNumberOfSphericalLayers, blackHoleSimulationMaxSteps;
	
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

	
	public SpaceAroundBlackHoleVisualiser()
	{
		nonInteractiveTIMAction = NonInteractiveTIMActionEnum.INTERACTIVE;
		renderQuality = RenderQualityEnum.DRAFT;
		
		numberOfFrames = 10;
		firstFrame = 0;
		lastFrame = numberOfFrames-1;
		
		// camera parameters; these are often set (or altered) in createStudio()
		cameraViewDirection = new Vector3D(-.3, -.2, 1);
		cameraViewCentre = new Vector3D(0, 0, 0);
		cameraDistance = 10;
		cameraFocussingDistance = 10;
		cameraHorizontalFOVDeg = 20;
		cameraMaxTraceLevel = 10000;
		cameraPixelsX = 640;
		cameraPixelsY = 480;
		cameraApertureSize = ApertureSizeType.PINHOLE;
		cameraExposureCompensation = ExposureCompensationType.EC0;
		
		// black hole

		showBlackHole = false;
		blackHoleCentre = new Vector3D(0, 0, 0);
		blackHoleHorizonRadius = 0.01;
		blackHoleJParameter = 1;
		indicateBlackHoleHorizon = true;
		indicatePhotonSphere = true;
		blackHoleSimulationSphereRadius = 1;
		simulationType = SphericallySymmetricRefractiveIndexDistributionSimulationType.SMOOTH_N;
		blackHoleSimulationSphereNumberOfSphericalLayers = 100;
		blackHoleSimulationMaxSteps = 1000;
		integrationType = IntegrationType.RK4;
		blackHoleDeltaTau = 0.001;
		blackHoleDeltaXMax = 0.25*blackHoleHorizonRadius;
		

		// trajectories
		showTrajectory1 = false;
		showTrajectory2 = false;
		showTrajectory3 = false;
		trajectory1StartPoint = new Vector3D(0,  0.5, 0);
		trajectory2StartPoint = new Vector3D(-10,  0.1, 0);
		trajectory3StartPoint = new Vector3D(-10, -0.3, 0);
		trajectory1StartDirection = new Vector3D(1, 0, 0);
		trajectory2StartDirection = new Vector3D(1, 0, 0);
		trajectory3StartDirection = new Vector3D(1, 0, 0);
		trajectoryRadius = 0.005;
		trajectoryMaxTraceLevel = 2000;
		reportToConsole = true;
		traceRaysWithTrajectory = false;	// don't automatically trace rays with trajectory, but do this in a bespoke way

		// rest of the scene
		studioInitialisation = StudioInitialisationType.MINIMALIST;	// the backdrop

		sphereCentres = new Vector3D[3];
		sphereRadii = new double[3];
		sphereColours = new DoubleColour[3];
		sphereVisibilities = new boolean[3];
		
		sphereCentres[0] = new Vector3D(0, 0, 10);
		sphereRadii[0] = 1;
		sphereColours[0] = new DoubleColour(1, 0, 0);
		sphereVisibilities[0] = true;

		sphereCentres[1] = new Vector3D(0, 0, 20);
		sphereRadii[1] = 1;
		sphereColours[1] = new DoubleColour(0, 1, 0);
		sphereVisibilities[1] = true;

		sphereCentres[2] = new Vector3D(0, 0, 40);
		sphereRadii[2] = 1;
		sphereColours[2] = new DoubleColour(0, 0, 1);
		sphereVisibilities[2] = true;

		
		windowTitle = "Dr TIM's space-around-a-black-hole visualiser";
		windowWidth = 1250;
		windowHeight = 650;

	}

	@Override
	public String getClassName()
	{
		return "SpaceAroundBlackHoleVisualiser"	// the name
				;
	}
	
	@Override
	public void writeParameters(PrintStream printStream)
	{
		// write any parameters not defined in NonInteractiveTIMEngine
		printStream.println("Scene initialisation");
		printStream.println();
		
		// black hole
		
		printStream.println("simulationType = "+simulationType);
		printStream.println("integrationType = "+integrationType);
		printStream.println("showBlackHole = "+showBlackHole);
		printStream.println("blackHoleCentre = "+blackHoleCentre);
		printStream.println("blackHoleHorizonRadius = "+blackHoleHorizonRadius);
		printStream.println("blackHoleJParameter = "+blackHoleJParameter);
		printStream.println("indicateBlackHoleHorizon = "+indicateBlackHoleHorizon);
		printStream.println("indicatePhotonSphere = "+indicatePhotonSphere);
		printStream.println("blackHoleSimulationSphereRadius = "+blackHoleSimulationSphereRadius);
		printStream.println("blackHoleSimulationSphereNumberOfSphericalLayers = "+blackHoleSimulationSphereNumberOfSphericalLayers);
		printStream.println("blackHoleSimulationMaxSteps = "+blackHoleSimulationMaxSteps);
		printStream.println("blackHoleDeltaTau = "+blackHoleDeltaTau);
		printStream.println("blackHoleDeltaXMax = "+blackHoleDeltaXMax);
		

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

		// write all parameters defined in NonInteractiveTIMEngine
		super.writeParameters(printStream);		
	}

	
	private SpaceAroundBlackHoleSphericalShells spaceAroundBlackHoleSphericalShells;
	private SurfaceOfChenBelinMetricSpace spaceAroundBlackHoleMetric;
	
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
							new SurfaceColour(sphereColours[i], DoubleColour.WHITE, true),	// surface property: sphereColours[i], made shiny
							scene,
							studio
						),
					sphereVisibilities[i]
				);
				
		// the sphere filled with the refractive-index distribution that simulates a black hole
		
		Sphere sphere = new Sphere(
				"Refractive-index simulation of a black hole",	// description
				blackHoleCentre,	// centre
				blackHoleSimulationSphereRadius,	// radius
				null,	// placeholder --- replace in a minute
				scene, 
				studio
		);

		switch(simulationType)
		{
		case SPHERICAL_SHELLS_CONSTANT_N:
			spaceAroundBlackHoleSphericalShells = new SpaceAroundBlackHoleSphericalShells(
					blackHoleHorizonRadius,	// horizon radius
					blackHoleJParameter,	// jParameter
					sphere,
					blackHoleSimulationSphereNumberOfSphericalLayers,	// number of shells
					blackHoleSimulationMaxSteps,	// maxSimulationSteps
					0.96,	// random transmission coefficient
					false	// shadow-throwing
					);
			// now give the sphere that marvellous surface property
			sphere.setSurfaceProperty(spaceAroundBlackHoleSphericalShells);
			break;
		case SMOOTH_N:
			spaceAroundBlackHoleMetric = new SurfaceOfChenBelinMetricSpace(
					blackHoleHorizonRadius,	// horizonRadius
					sphere,	// simulationSphere
					blackHoleJParameter,	// jParameter
					blackHoleDeltaTau,	// deltaTau
					blackHoleDeltaXMax,
					blackHoleSimulationMaxSteps,	// maxSteps
					integrationType,
					0.96	// transmissionCoefficient
					);
			// now give the sphere that marvellous surface property
			sphere.setSurfaceProperty(spaceAroundBlackHoleMetric);
			
			// TODO uncomment this again
//			// get a copy of the inside scene (which, at the moment, requires (re)creating it)...
//			SceneObjectContainer insideScene = (SceneObjectContainer)(spaceAroundBlackHoleMetric.getInsideScene());
//			
//			// ... and add to it the camera; I think this is causing trouble at the moment, for some reason (which I thought was related to the surface properties,
//			// but now that I have made them all simple it still causes trouble...?!)
//			insideScene.addSceneObject(
//					new EditableCameraShape(
//							getStandardCamera(),
//							1,	// width
//							SurfaceColourLightSourceIndependent.GREY50,	// surfacePropertyBody
//							SurfaceColourLightSourceIndependent.GREY30,	// surfacePropertyLens
//							SurfaceColourLightSourceIndependent.CYAN,	// surfacePropertyGlass
//							insideScene,	// parent
//							studio
//						)					
//				);
			
//			insideScene.addSceneObject(
//					new EditableParametrisedCylinder(
//							"cylinder",	// description,
//							blackHoleCentre,	// startPoint,
//							Vector3D.sum(
//									blackHoleCentre,
//									Vector3D.difference(getStandardCamera., b)
//								) endPoint,
//							double radius,
//							SurfaceProperty surfaceProperty,
//							SceneObject parent, 
//							Studio studio
//					)
//				);		
		}

		scene.addSceneObject(sphere);
		
		// trace the ray trajectories
		
		if(showTrajectory1)
		scene.addSceneObject(new RayTrajectory(
				"Trajectory 1",
				trajectory1StartPoint,	// start point
				0,	// start time
				trajectory1StartDirection,	// initial direction
				trajectoryRadius,	// radius
				new SurfaceColourLightSourceIndependent(DoubleColour.RED, true),
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
				new SurfaceColourLightSourceIndependent(DoubleColour.GREEN, true),
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
				new SurfaceColourLightSourceIndependent(DoubleColour.BLUE, true),
				trajectoryMaxTraceLevel,	// max trace level
				reportToConsole,	// reportToConsole
				scene,
				studio
			)
		);


		// trace the rays with trajectory through the scene
		studio.traceRaysWithTrajectory();

		// System.exit(-1);

//		// for test purposes, define a ray...
//		Ray r = new RayWithTrajectory(
//				new Vector3D(0.3, 0, -10),	// start point
//				new Vector3D(0, 0, 1),	// direction
//				0,	// time
//				true	// reportToConsole
//			);
//		
//		// ... and launch it at the sphere
//		try {
//			sphere.getColour(r,
//					LightSource.getStandardLightsFromBehind(),
//					sphere, 100,	// trace level
//					new DefaultRaytraceExceptionHandler()
//			);
//		} catch (RayTraceException e) {
//			e.printStackTrace();
//		}
		
		// System.exit(1);
		
		if(!showBlackHole)
		{
			// give the black-hole simulation sphere the appearance of a slightly absorbing sphere
			sphere.setSurfaceProperty(Transparent.SLIGHTLY_ABSORBING);
			// scene.removeSceneObject(sphere);
		}

		// also add a sphere representing the horizon
		Sphere horizonSphere = new Sphere(
				"black-hole horizon",	// description
				blackHoleCentre,	// centre
				blackHoleHorizonRadius,	// radius
				new Transparent(0.75, false),	// quite an absorbing sphere
				scene, 
				studio
		);
		scene.addSceneObject(horizonSphere, indicateBlackHoleHorizon);
		
		// also add a sphere representing the photon sphere
		Sphere photonSphere = new Sphere(
				"photon sphere",	// description
				blackHoleCentre,	// centre
				blackHoleHorizonRadius*(1+blackHoleJParameter+Math.sqrt(1+blackHoleJParameter+blackHoleJParameter*blackHoleJParameter)),	// radius
				new SurfacePropertyAverageWeighted(
						new SurfaceColour(DoubleColour.RED, DoubleColour.WHITE, false),	// surfaceProperty1
						0.2,	// weighting of surface property 1
						Transparent.PERFECT,	// surfaceProperty2
						0.8	// weighting of surface property 2
					),	// new Transparent(0.9, false),	// semi-transparent, greenish
				scene, 
				studio
		);
		scene.addSceneObject(photonSphere, indicatePhotonSphere);
		

		studio.setScene(scene);
		studio.setCamera(getStandardCamera());
	}
	
	
	
	// GUI
	
	// black hole
	private JComboBox<IntegrationType> integrationTypeComboBox;
	private JCheckBox showBlackHoleCheckBox, indicateBlackHoleHorizonCheckBox, indicatePhotonSphereCheckBox;
	private LabelledVector3DPanel blackHoleCentrePanel, testPositionPanel;
	private JTextField infoTextField;
	private JButton testButton;
	private LabelledDoublePanel blackHoleHorizonRadiusPanel, blackHoleJParameterPanel, blackHoleSimulationSphereRadiusPanel, blackHoleDeltaTauPanel, blackHoleDeltaXMaxPanel;
	private LabelledIntPanel blackHoleSimulationSphereNumberOfSphericalLayersPanel, blackHoleSimulationMaxStepsPanel;
	private JTabbedPane simulationTypeTabbedPane;
	
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

	
	@Override
	protected void createInteractiveControlPanel()
	{
		super.createInteractiveControlPanel();
		
		JTabbedPane tabbedPane = new JTabbedPane();
		interactiveControlPanel.add(tabbedPane, "span");
		
		

		//
		// the black-hole panel
		//
		
		JPanel blackHolePanel = new JPanel();
		blackHolePanel.setLayout(new MigLayout("insets 0"));

		showBlackHoleCheckBox = new JCheckBox("Show black hole");
		showBlackHoleCheckBox.setSelected(showBlackHole);
		blackHolePanel.add(showBlackHoleCheckBox, "wrap");

		blackHoleCentrePanel = new LabelledVector3DPanel("Centre");
		blackHoleCentrePanel.setVector3D(blackHoleCentre);
		blackHolePanel.add(blackHoleCentrePanel, "span");

		blackHoleHorizonRadiusPanel = new LabelledDoublePanel("Horizon radius");
		blackHoleHorizonRadiusPanel.setNumber(blackHoleHorizonRadius);
		blackHolePanel.add(blackHoleHorizonRadiusPanel, "wrap");
		
		blackHoleJParameterPanel = new LabelledDoublePanel("J parameter");
		blackHoleJParameterPanel.setNumber(blackHoleJParameter);
		blackHolePanel.add(blackHoleJParameterPanel, "wrap");
		
		indicateBlackHoleHorizonCheckBox = new JCheckBox("Indicate horizon sphere");
		indicateBlackHoleHorizonCheckBox.setSelected(indicateBlackHoleHorizon);
		blackHolePanel.add(indicateBlackHoleHorizonCheckBox, "wrap");
		
		indicatePhotonSphereCheckBox = new JCheckBox("Indicate photon sphere");
		indicatePhotonSphereCheckBox.setSelected(indicatePhotonSphere);
		blackHolePanel.add(indicatePhotonSphereCheckBox, "wrap");
		
		blackHoleSimulationSphereRadiusPanel = new LabelledDoublePanel("Refractive index simulation within sphere of radius");
		blackHoleSimulationSphereRadiusPanel.setNumber(blackHoleSimulationSphereRadius);
		blackHolePanel.add(blackHoleSimulationSphereRadiusPanel, "wrap");
		
		// start simulation-type tabbed pane
		
		simulationTypeTabbedPane = new JTabbedPane();
		blackHolePanel.add(simulationTypeTabbedPane, "span");

		// the spherical-shells panel
		
		JPanel sphericalShellsPanel = new JPanel();
		sphericalShellsPanel.setLayout(new MigLayout("insets 0"));
		// simulationTypeTabbedPane.addTab("Spherical shells with n=const.", sphericalShellsPanel);
		simulationTypeTabbedPane.addTab(SphericallySymmetricRefractiveIndexDistributionSimulationType.SPHERICAL_SHELLS_CONSTANT_N.toString(), sphericalShellsPanel);

		blackHoleSimulationSphereNumberOfSphericalLayersPanel = new LabelledIntPanel("Number of simulated refractive-index layers");
		blackHoleSimulationSphereNumberOfSphericalLayersPanel.setNumber(blackHoleSimulationSphereNumberOfSphericalLayers);
		sphericalShellsPanel.add(blackHoleSimulationSphereNumberOfSphericalLayersPanel, "wrap");
		
		
		// the smooth-refractive-index-distribution panel

		JPanel smoothRefractiveIndexPanel = new JPanel();
		smoothRefractiveIndexPanel.setLayout(new MigLayout("insets 0"));		
		simulationTypeTabbedPane.addTab(SphericallySymmetricRefractiveIndexDistributionSimulationType.SMOOTH_N.toString(), smoothRefractiveIndexPanel);

		integrationTypeComboBox = new JComboBox<IntegrationType>(IntegrationType.values());
		integrationTypeComboBox.setSelectedItem(integrationType);
		smoothRefractiveIndexPanel.add(GUIBitsAndBobs.makeRow("Integration type", integrationTypeComboBox), "span");

		blackHoleDeltaTauPanel = new LabelledDoublePanel("Delta tau");
		blackHoleDeltaTauPanel.setNumber(blackHoleDeltaTau);
		smoothRefractiveIndexPanel.add(blackHoleDeltaTauPanel, "wrap");
		
		blackHoleDeltaXMaxPanel = new LabelledDoublePanel("Delta x_max");
		blackHoleDeltaXMaxPanel.setNumber(blackHoleDeltaXMax);
		smoothRefractiveIndexPanel.add(blackHoleDeltaXMaxPanel, "wrap");
		
		for(int i=0; i<simulationTypeTabbedPane.getTabCount(); i++)
			if(simulationTypeTabbedPane.getTitleAt(i).equals(simulationType.toString()))
				simulationTypeTabbedPane.setSelectedIndex(i);

		
		
		// end simulation-type tabbed pane

		
		blackHoleSimulationMaxStepsPanel = new LabelledIntPanel("Max. number of simulation steps");
		blackHoleSimulationMaxStepsPanel.setNumber(blackHoleSimulationMaxSteps);
		blackHolePanel.add(blackHoleSimulationMaxStepsPanel, "wrap");
		
		testPositionPanel = new LabelledVector3DPanel("Test position");
		testPositionPanel.setVector3D(new Vector3D(0, 0, 0));
		testButton = new JButton("Test");
		testButton.addActionListener(this);
		blackHolePanel.add(GUIBitsAndBobs.makeRow(testPositionPanel, testButton), "span");

		infoTextField  = new JTextField(40);
		infoTextField.setEditable(false);
		infoTextField.setText("-- not initialised --");
		blackHolePanel.add(infoTextField, "span");
		
		tabbedPane.addTab("Black hole", blackHolePanel);
		
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

		tabbedPane.addTab("Camera", cameraPanel);
	}
	
	@Override
	protected void acceptValuesInInteractiveControlPanel()
	{
		super.acceptValuesInInteractiveControlPanel();
		
		// black hole
		
		String simulationTypeSelectedTitle = simulationTypeTabbedPane.getTitleAt(simulationTypeTabbedPane.getSelectedIndex());
		for(SphericallySymmetricRefractiveIndexDistributionSimulationType s:SphericallySymmetricRefractiveIndexDistributionSimulationType.values())
			if(simulationTypeSelectedTitle.equals(s.toString())) simulationType = s;
		
		integrationType = (IntegrationType)(integrationTypeComboBox.getSelectedItem());
		showBlackHole = showBlackHoleCheckBox.isSelected();
		blackHoleCentre = blackHoleCentrePanel.getVector3D();
		blackHoleHorizonRadius = blackHoleHorizonRadiusPanel.getNumber();
		blackHoleJParameter = blackHoleJParameterPanel.getNumber();
		indicateBlackHoleHorizon = indicateBlackHoleHorizonCheckBox.isSelected();
		indicatePhotonSphere = indicatePhotonSphereCheckBox.isSelected();
		blackHoleSimulationSphereRadius = blackHoleSimulationSphereRadiusPanel.getNumber();
		blackHoleSimulationSphereNumberOfSphericalLayers = blackHoleSimulationSphereNumberOfSphericalLayersPanel.getNumber();
		blackHoleSimulationMaxSteps = blackHoleSimulationMaxStepsPanel.getNumber();
		blackHoleDeltaTau = blackHoleDeltaTauPanel.getNumber();
		blackHoleDeltaXMax = blackHoleDeltaXMaxPanel.getNumber();
		
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
	}


	@Override
	public void actionPerformed(ActionEvent e)
	{
		super.actionPerformed(e);
		
		if(e.getSource().equals(testButton))
		{
			// initialise spaceAroundBlackHole according to the current parameters
			acceptValuesInInteractiveControlPanel();
			populateStudio();
			
			if(simulationType == SphericallySymmetricRefractiveIndexDistributionSimulationType.SPHERICAL_SHELLS_CONSTANT_N)
			{
				int[] voxelIndices = spaceAroundBlackHoleSphericalShells.getVoxelIndices(testPositionPanel.getVector3D());
				// try {
					infoTextField.setText(
							"layer number = "+voxelIndices[0]
									+", refractive index = "+ spaceAroundBlackHoleSphericalShells.getRefractiveIndex1(voxelIndices)
							);
				// } catch (Exception e1) {
					// TODO Auto-generated catch block
				// 	e1.printStackTrace();
				// }
			}
			else
			{
				infoTextField.setText("Refractive index at test position = "+spaceAroundBlackHoleMetric.calculateEpsilonMuTensor(testPositionPanel.getVector3D()).get(0, 0));
			}
		}
	}

 	public static void main(final String[] args)
   	{
  		(new SpaceAroundBlackHoleVisualiser()).run();
  	}
 }
