package optics.raytrace.research.refractiveTaylorSeries;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import math.MyMath;
import math.Vector3D;
import net.miginfocom.swing.MigLayout;
import optics.DoubleColour;
import optics.raytrace.NonInteractiveTIMEngine;
import optics.raytrace.GUI.lowLevel.ApertureSizeType;
import optics.raytrace.GUI.lowLevel.DoublePanel;
import optics.raytrace.GUI.lowLevel.GUIBitsAndBobs;
import optics.raytrace.GUI.lowLevel.IntPanel;
import optics.raytrace.GUI.lowLevel.LabelledDoublePanel;
import optics.raytrace.GUI.lowLevel.LabelledIntPanel;
import optics.raytrace.GUI.lowLevel.LabelledVector3DPanel;
import optics.raytrace.cameras.OrthographicCamera;
import optics.raytrace.core.RayWithTrajectory;
import optics.raytrace.core.SceneObjectPrimitive;
import optics.raytrace.core.Studio;
import optics.raytrace.core.StudioInitialisationType;
import optics.raytrace.exceptions.SceneException;
import optics.raytrace.research.refractiveTaylorSeries.LawOfRefraction1Parameter.LawOfRefractionType;
import optics.raytrace.research.refractiveTaylorSeries.SurfaceParametersOptimisation.AlgorithmType;
import optics.raytrace.sceneObjects.CylinderMantle;
import optics.raytrace.sceneObjects.Plane;
import optics.raytrace.sceneObjects.RayTrajectory;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectIntersection;
import optics.raytrace.surfaces.Checked;
import optics.raytrace.surfaces.ColourFilter;
import optics.raytrace.surfaces.SurfaceColour;


/**
 * Polynomial-phase hologram explorer.
 * 
 * Author: Johannes
 */
public class PolynomialPhaseHologramExplorer extends NonInteractiveTIMEngine
{
	// DirectionChangingSurfaceSequence dcss;
	SurfaceParameters surfaceParameters;
	double transmissionCoefficient;
	
	// optimisation
	RayPairsParameters rayPairsParameters;
	SurfaceParametersOptimisation surfaceParametersOptimisation;
	
	//  background
	private StudioInitialisationType studioInitialisation;
	private boolean addZPlane;
	private double zPlaneZ;
	private double zPlaneCheckerboardPeriod;
	
	/**
	 * allows selection of one of several views (or cameras)
	 */
	public enum CameraType
	{
		EYE("Eye"),
		SIDE("Side");
		
		private String description;
		private CameraType(String description) {this.description = description;}	
		@Override
		public String toString() {return description;}
	}
	
	private CameraType cameraType;

	// side camera
	private Vector3D sideCameraViewCentre;
	private Vector3D sideCameraViewDirection;
	private Vector3D sideCameraUpDirection;
	private double sideCameraWidth;
	
	private double cylinderRadius;
	private int noOfDirectionPairsTraced;
	private int noOfRaysPerBundleTraced;
	private double rayRadius;

	
//	OrthographicCamera(
//			String name,
//			Vector3D viewDirection,
//			Vector3D CCDCentre,
//			Vector3D horizontalSpanVector3D, Vector3D verticalSpanVector3D,
//			int detectorPixelsHorizontal, int detectorPixelsVertical,
//			int maxTraceLevel)

	/**
	 * Constructor.
	 * Sets all parameters.
	 */
	public PolynomialPhaseHologramExplorer()
	{
		super();
		
		surfaceParameters = new SurfaceParameters(1, 3);
		transmissionCoefficient = 0.96;
		
		// optimisation
		rayPairsParameters = new RayPairsParameters(
				100,	// noOfDirectionPairs,
				MyMath.deg2rad(10),	// directionsInConeAngleRad,
				new LawOfRefraction1Parameter(LawOfRefractionType.RAY_ROTATION, 10),	// lawOfRefraction,
				100,	// noOfRaysPerBundle,
				0.1	// rayStartPointsDiscRadius,
			);
		surfaceParametersOptimisation = new SurfaceParametersOptimisation(
				100,	// surfaceParametersOptimisation.noOfIterations, 
				rayPairsParameters,	// rayPairsParameters
				AlgorithmType.SIMULATED_ANNEALING,	// algorithmType, 
				0.1	// surfaceParametersOptimisation.saInitialTemperature
			) ;
		
		// background
		studioInitialisation = StudioInitialisationType.HEAVEN;
		addZPlane = true;
		zPlaneZ = 10000;
		zPlaneCheckerboardPeriod = 1000;

		// cameras
		
		cameraType = CameraType.EYE;
		
		// main camera
		cameraViewCentre = new Vector3D(0, 0, 0);
		cameraDistance = 10;
		cameraViewDirection = new Vector3D(0, 0, 1);
		cameraHorizontalFOVDeg = 40;
		cameraApertureSize = ApertureSizeType.MEDIUM;
		cameraFocussingDistance = 10000;
		
		// side camera
		sideCameraViewCentre = new Vector3D(0, 0, 1);
		sideCameraViewDirection = new Vector3D(-1, 0, 0);	// from the right
		sideCameraUpDirection = new Vector3D(0, 1, 0);
		sideCameraWidth = 4;
		cylinderRadius = 1;
		noOfDirectionPairsTraced = 5;
		noOfRaysPerBundleTraced = 10;
		rayRadius = 0.02;
		traceRaysWithTrajectory = false;

		windowTitle = "Dr TIM's polynomial-phase hologram explorer";
		windowWidth = 1400;
		windowHeight = 650;
	}
	

	/* (non-Javadoc)
	 * @see optics.raytrace.NonInteractiveTIMEngine#getFirstPartOfFilename()
	 */
	@Override
	public String getFilename()
	{
		return
				"PolynomialPhaseHologramExplorer"
				;
	}

	@Override
	public void writeParameters(PrintStream printStream)
	{
		// write any parameters not defined in NonInteractiveTIMEngine, each parameter is saved like this:
		// printStream.println("parameterName = "+parameterName);

		surfaceParameters.writeParameters(printStream);
		printStream.println("transmissionCoefficient="+transmissionCoefficient);
		
		//  optimisation
		rayPairsParameters.writeParameters(printStream);
		surfaceParametersOptimisation.writeParameters(printStream);

		// background
		printStream.println("studioInitialisation="+studioInitialisation);
		printStream.println("addZPlane="+addZPlane);
		printStream.println("zPlaneZ="+zPlaneZ);
		printStream.println("zPlaneCheckerboardPeriod="+zPlaneCheckerboardPeriod);

		// cameras
		
		printStream.println("cameraType="+cameraType);
		
		// eye/main camera
		printStream.println("cameraViewCentre="+cameraViewCentre);
		printStream.println("cameraDistance="+cameraDistance);
		printStream.println("cameraViewDirection="+cameraViewDirection);
		printStream.println("cameraHorizontalFOVDeg="+cameraHorizontalFOVDeg);
		printStream.println("cameraApertureSize="+cameraApertureSize);
		printStream.println("cameraFocussingDistance="+cameraFocussingDistance);
		
		// side camera
		printStream.println("sideCameraViewCentre="+sideCameraViewCentre);
		printStream.println("sideCameraViewDirection="+sideCameraViewDirection);
		printStream.println("sideCameraUpDirection="+sideCameraUpDirection);
		printStream.println("sideCameraWidth="+sideCameraWidth);
		printStream.println("cylinderRadius="+cylinderRadius);
		printStream.println("noOfDirectionPairsTraced="+noOfDirectionPairsTraced);
		printStream.println("noOfRaysPerBundleTraced="+noOfRaysPerBundleTraced);
		printStream.println("rayRadius="+rayRadius);

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
		
		switch(cameraType)
		{
		case SIDE:
//			private Vector3D sideCameraViewCentre;
//			private Vector3D sideCameraViewDirection;
//			private Vector3D sideCameraUpDirection;
//			private double sideCameraWidth;
//			
//			private double cylinderRadius;
//			private int noOfDirectionPairsTraced;
//			private int noOfRaysPerBundleTraced;

			studio.setCamera(new OrthographicCamera(
					"side camera",	// name,
					sideCameraViewDirection,	// viewDirection
					Vector3D.sum(sideCameraViewCentre, sideCameraViewDirection.getWithLength(-100)),	// CCDCentre,
					Vector3D.crossProduct(sideCameraUpDirection, sideCameraViewDirection).getWithLength(sideCameraWidth),	// horizontalSpanVector3D, 
					sideCameraUpDirection.getWithLength(sideCameraWidth*cameraPixelsY/cameraPixelsX),	// verticalSpanVector3D
					(int)(cameraPixelsX*renderQuality.getAntiAliasingQuality().getAntiAliasingFactor()),	// detectorPixelsHorizontal,
					(int)(cameraPixelsY*renderQuality.getAntiAliasingQuality().getAntiAliasingFactor()),	// detectorPixelsVertical,
					cameraMaxTraceLevel	// maxTraceLevel
				));

			break;
		case EYE:
		default:
			studio.setCamera(getStandardCamera());
		}

		if(addZPlane)
			scene.addSceneObject(Plane.zPlane(
					"z plane",	// description, 
					zPlaneZ,	// z0, 
					new Checked(
							DoubleColour.DARK_GREEN,	// colour1, 
							DoubleColour.LIGHT_GREEN,	// colour2,
							zPlaneCheckerboardPeriod,	// checkerWidth,
							true	// shadowThrowing
						),	// surfaceProperty, 
					scene,	// parent, 
					studio
				));
		
		DirectionChangingSurfaceSequence dcss = surfaceParameters.createCorrespondingDirectionChangingSurfaceSequence(transmissionCoefficient);
//		addSurfaces();

		SceneObjectContainer surfaces = new SceneObjectContainer("the surfaces", scene, studio);
		scene.addSceneObject(surfaces);

		// add the surfaces from the DirectionChangingSurfaceSequence dcss to the scene
		for(SceneObjectPrimitive s:dcss.getSceneObjectPrimitivesWithDirectionChangingSurfaces())
		{
//			if(cameraType == CameraType.SIDE) 
//			{
//				// in this case, we want to show only the part of each surface that is within <cylinderRadius> of the z axis
//				SceneObjectIntersection soi = new SceneObjectIntersection("central part of "+s.getDescription(), scene, studio);
//				soi.addPositiveSceneObject(s);
//				soi.addInvisiblePositiveSceneObject(new CylinderMantle(
//						"cylinder centred on z",	// description,
//						Vector3D.O,	// startPoint,
//						Vector3D.Z,	// endPoint,
//						cylinderRadius,	// radius,
//						true,	// infinite,
//						null,	// surfaceProperty,
//						scene,	// parent,
//						studio
//					));
//				surfaces.addSceneObject(soi);
//			}
//			else
				surfaces.addSceneObject(s);
		}
		
		// add rays
		if(cameraType == CameraType.SIDE)
		{
			for(int i=0; i<Math.min(rayPairsParameters.noOfRaysPerBundle, noOfRaysPerBundleTraced); i++)
				for(int j=0; j<Math.min(rayPairsParameters.noOfDirectionPairs, noOfDirectionPairsTraced); j++)
					scene.addSceneObject(
							new RayTrajectory(
									"Trajectory of ray #"+i+", "+j,	// description,
									Vector3D.sum(rayPairsParameters.rayStartPoints[i], rayPairsParameters.directionsIn[j].getWithLength(0)),	// startPoint,
									0,	// startTime,
									rayPairsParameters.directionsIn[j],	// startDirection,
									rayRadius,	// rayRadius,
									SurfaceColour.RED_SHINY,	// surfaceProperty,
									// SurfaceColourLightSourceIndependent.RED,	// surfaceProperty,
									1000,	// maxTraceLevel,
									false,	// reportToConsole,
									scene,	// parent,
									studio
								));
			
			RayWithTrajectory.traceRaysWithTrajectory(studio.getScene());

			// remove all the surfaces...
			surfaces.clear();
			
			// and add them again, but with a simpler SurfaceProperty
			for(SceneObjectPrimitive s:dcss.getSceneObjectPrimitivesWithDirectionChangingSurfaces())
			{
					// in this case, we want to show only the part of each surface that is within <cylinderRadius> of the z axis
					s.setSurfaceProperty(ColourFilter.CYAN_GLASS);
					SceneObjectIntersection soi = new SceneObjectIntersection("central part of "+s.getDescription(), scene, studio);
					soi.addPositiveSceneObject(s);
					soi.addInvisiblePositiveSceneObject(new CylinderMantle(
							"cylinder centred on z",	// description,
							Vector3D.O,	// startPoint,
							Vector3D.Z,	// endPoint,
							cylinderRadius,	// radius,
							true,	// infinite,
							null,	// surfaceProperty,
							scene,	// parent,
							studio
						));
					surfaces.addSceneObject(soi);
			}

		}
		
	}



	//
	// for the interactive version
	//

	
	private JButton updateSurfaceParameterFieldsButton, randomiseSurfaceParametersButton, saveSurfaceParametersButton, loadSurfaceParametersButton;
	private IntPanel polynomialOrderPanel, noOfSurfacesPanel;
	private LabelledDoublePanel transmissionCoefficientPanel;	// zPanel[];
	// private DoublePanel aPanel[][][];
	JTabbedPane surfaceParametersTabbedPane;
	private JPanel surfacesPanel;
	
	// background
	private JComboBox<StudioInitialisationType> studioInitialisationComboBox; 
	private JCheckBox addZPlaneCheckBox;
	private LabelledDoublePanel zPlaneZPanel;
	private LabelledDoublePanel zPlaneCheckerboardPeriodPanel;
	
	// optimisation
	JButton 
		optimizeButton;
	private JButton updateDirectionPairsFieldsButton;
	private JButton initialiseDirectionsInButton;
	private JButton randomiseDirectionPairsButton;
	private JButton updateMeanAlignmentButton;
	private JButton randomiseRayStartPointsButton;
	private IntPanel noOfDirectionPairsPanel, noOfIterationsPanel, noOfRaysPerBundlePanel;
	private JTabbedPane directionsTabbedPane;
	private JComboBox<LawOfRefractionType> lawOfRefractionComboBox;
	private JLabel lawOfRefractionAdditionalParameterLabel;
	private DoublePanel lawOfRefractionAdditionalParameterPanel;
	private LabelledVector3DPanel directionInPanel[], directionOutPanel[];
	private LabelledDoublePanel meanAlignmentPanel;
	private DoublePanel directionsInConeAngleDegPanel, rayStartPointsDiscRadiusPanel, saInitialTemperaturePanel, logMeanAlignmentPanel;
	final String OPTIMIZE_BUTTON_OPTIMIZE = "Optimise";
	private final String OPTIMIZE_BUTTON_STOP = "Stop";

	// camera stuff
	private JComboBox<CameraType> cameraTypeComboBox;
	private JPanel cameraParametersPanel, mainCameraPanel, sideCameraPanel;
	
	// main camera
	private LabelledDoublePanel cameraDistancePanel;
	private LabelledVector3DPanel cameraViewDirectionPanel, cameraViewCentrePanel;
	private DoublePanel cameraHorizontalFOVDegPanel;
	private JComboBox<ApertureSizeType> cameraApertureSizeComboBox;
	private LabelledDoublePanel cameraFocussingDistancePanel;
	
	// side camera
	private LabelledVector3DPanel sideCameraViewCentrePanel;
	private LabelledVector3DPanel sideCameraViewDirectionPanel;
	private LabelledVector3DPanel sideCameraUpDirectionPanel;
	private LabelledDoublePanel sideCameraWidthPanel;
	private LabelledDoublePanel cylinderRadiusPanel;
	private LabelledIntPanel noOfDirectionPairsTracedPanel;
	private LabelledIntPanel noOfRaysPerBundleTracedPanel;
	private LabelledDoublePanel rayRadiusPanel;


	
	/**
	 * add controls to the interactive control panel;
	 * override to modify
	 */
	@Override
	protected void createInteractiveControlPanel()
	{
		super.createInteractiveControlPanel();
		
		JTabbedPane tabbedPane = new JTabbedPane();
		interactiveControlPanel.add(tabbedPane, "span");

		// the surfaces panel
		
		surfacesPanel = new JPanel();
		surfacesPanel.setLayout(new MigLayout("insets 0"));
		tabbedPane.addTab("Surfaces", surfacesPanel);

		noOfSurfacesPanel = new IntPanel();
		noOfSurfacesPanel.setNumber(surfaceParameters.getNoOfSurfaces());
		
		polynomialOrderPanel = new IntPanel();
		polynomialOrderPanel.setNumber(surfaceParameters.getPolynomialOrder());
		
		updateSurfaceParameterFieldsButton = new JButton("Update fields");
		updateSurfaceParameterFieldsButton.setToolTipText("Update fields for parameters describing surfaces");
		updateSurfaceParameterFieldsButton.addActionListener(this);

		// GUIBitsAndBobs.makeRow(noOfSurfacesPanel, changeSurfacesButton)
		surfacesPanel.add(
				GUIBitsAndBobs.makeRow("", noOfSurfacesPanel, "planar polynomial-phase holograms of order", polynomialOrderPanel, "", updateSurfaceParameterFieldsButton),
				"span");
		
		surfaceParametersTabbedPane = new JTabbedPane();
		surfacesPanel.add(surfaceParametersTabbedPane, "span");
		
		surfaceParameters.repopulateSurfaceParametersTabbedPane(surfaceParametersTabbedPane);
		
		randomiseSurfaceParametersButton = new JButton("Randomise");
		randomiseSurfaceParametersButton.addActionListener(this);
		
		saveSurfaceParametersButton = new JButton("Save");
		saveSurfaceParametersButton.addActionListener(this);
		
		loadSurfaceParametersButton = new JButton("Load");
		loadSurfaceParametersButton.addActionListener(this);
		
		surfacesPanel.add(GUIBitsAndBobs.makeRow(randomiseSurfaceParametersButton, loadSurfaceParametersButton, saveSurfaceParametersButton), "span");
		
		transmissionCoefficientPanel = new LabelledDoublePanel("Transmission coefficient");
		transmissionCoefficientPanel.setNumber(transmissionCoefficient);
		surfacesPanel.add(transmissionCoefficientPanel, "span");

		// the background panel
		
		JPanel backgroundPanel = new JPanel();
		backgroundPanel.setLayout(new MigLayout("insets 0"));
		tabbedPane.addTab("Background", backgroundPanel);

		studioInitialisationComboBox = new JComboBox<StudioInitialisationType>(StudioInitialisationType.limitedValuesForBackgrounds);
		studioInitialisationComboBox.setSelectedItem(studioInitialisation);
		backgroundPanel.add(GUIBitsAndBobs.makeRow("Initialise backdrop to", studioInitialisationComboBox), "span");
		
		addZPlaneCheckBox = new JCheckBox("Add z plane,");
		addZPlaneCheckBox.setSelected(addZPlane);
		
		zPlaneZPanel = new LabelledDoublePanel("z = ");
		zPlaneZPanel.setNumber(zPlaneZ);
		
		zPlaneCheckerboardPeriodPanel = new LabelledDoublePanel(", checkerboard period");
		zPlaneCheckerboardPeriodPanel.setNumber(zPlaneCheckerboardPeriod);

		backgroundPanel.add(GUIBitsAndBobs.makeRow(addZPlaneCheckBox, zPlaneZPanel, zPlaneCheckerboardPeriodPanel), "span");
		
		// the optimisation panel

		JPanel optimisationPanel = new JPanel();
		optimisationPanel.setLayout(new MigLayout("insets 0"));
		tabbedPane.addTab("Optimisation", optimisationPanel);
		
		JPanel rayDirectionPairsPanel = new JPanel();
		rayDirectionPairsPanel.setLayout(new MigLayout("insets 0"));
		rayDirectionPairsPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Ray-direction pairs"));
		optimisationPanel.add(rayDirectionPairsPanel, "wrap");

		noOfDirectionPairsPanel = new IntPanel();
		noOfDirectionPairsPanel.setNumber(rayPairsParameters.noOfDirectionPairs);
		
		updateDirectionPairsFieldsButton = new JButton("Update fields");
		updateDirectionPairsFieldsButton.setToolTipText("Update fields for parameters describing direction pairs");
		updateDirectionPairsFieldsButton.addActionListener(this);

		// GUIBitsAndBobs.makeRow(noOfSurfacesPanel, changeSurfacesButton)
		rayDirectionPairsPanel.add(
				GUIBitsAndBobs.makeRow("", noOfDirectionPairsPanel, "pairs of light-ray directions", updateDirectionPairsFieldsButton),
				"span");
		
		directionsTabbedPane = new JTabbedPane();
		rayDirectionPairsPanel.add(directionsTabbedPane, "span");

		updateDirectionPairsTabbedPane();

		JPanel rayDirectionPairsInitPanel = new JPanel();
		rayDirectionPairsInitPanel.setLayout(new MigLayout("insets 0"));
		rayDirectionPairsInitPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Initialisation"));
		rayDirectionPairsPanel.add(rayDirectionPairsInitPanel, "wrap");

		directionsInConeAngleDegPanel = new DoublePanel();
		directionsInConeAngleDegPanel.setNumber(MyMath.rad2deg(rayPairsParameters.directionsInConeAngleRad));
		
		rayDirectionPairsInitPanel.add(
				GUIBitsAndBobs.makeRow(
						"Randomise incident directions on a cone of angle", directionsInConeAngleDegPanel, "° around z;"
					), 
				"wrap"
			);
		
		lawOfRefractionComboBox = new JComboBox<LawOfRefractionType>(LawOfRefractionType.values());
		lawOfRefractionComboBox.setSelectedItem(rayPairsParameters.lawOfRefraction);
		lawOfRefractionComboBox.addActionListener(this);
		
		
		rayDirectionPairsInitPanel.add(
				GUIBitsAndBobs.makeRow(
						"calculate outgoing directions according to", lawOfRefractionComboBox, ","
					), 
				"wrap"
			);
		
		lawOfRefractionAdditionalParameterLabel = new JLabel(rayPairsParameters.lawOfRefraction.lawOfRefractionType.getParameterDescription());
		lawOfRefractionAdditionalParameterPanel = new DoublePanel();
		lawOfRefractionAdditionalParameterPanel.setNumber(rayPairsParameters.lawOfRefraction.parameter);
		rayDirectionPairsInitPanel.add(
				GUIBitsAndBobs.makeRow(
						lawOfRefractionAdditionalParameterLabel, lawOfRefractionAdditionalParameterPanel
					), 
				"wrap"
			);

		initialiseDirectionsInButton = new JButton("Initialise");
		initialiseDirectionsInButton.setToolTipText("Initialise directions");
		initialiseDirectionsInButton.addActionListener(this);
		rayDirectionPairsInitPanel.add(initialiseDirectionsInButton, "push, al right, wrap");
		
//		randomiseDirectionPairsButton = new JButton("Randomise directions");
//		randomiseDirectionPairsButton.addActionListener(this);
//		rayDirectionPairsPanel.add(randomiseDirectionPairsButton, "wrap");
		
		JPanel rayBundlePanel = new JPanel();
		rayBundlePanel.setLayout(new MigLayout("insets 0"));
		rayBundlePanel.setBorder(GUIBitsAndBobs.getTitledBorder("Ray bundles"));
		optimisationPanel.add(rayBundlePanel, "wrap");

		noOfRaysPerBundlePanel = new IntPanel();
		noOfRaysPerBundlePanel.setNumber(rayPairsParameters.noOfRaysPerBundle);
		
		rayStartPointsDiscRadiusPanel = new DoublePanel();
		rayStartPointsDiscRadiusPanel.setNumber(rayPairsParameters.rayStartPointsDiscRadius);
		
		randomiseRayStartPointsButton = new JButton("Randomise");
		randomiseRayStartPointsButton.addActionListener(this);
		
		rayBundlePanel.add(
				GUIBitsAndBobs.makeRow(
						noOfRaysPerBundlePanel, "rays, starting on a disc of radius", rayStartPointsDiscRadiusPanel,
						randomiseRayStartPointsButton
					),
				"span"
			);
		
		noOfIterationsPanel = new IntPanel();
		noOfIterationsPanel.setNumber(surfaceParametersOptimisation.noOfIterations);
		
		saInitialTemperaturePanel = new DoublePanel();
		saInitialTemperaturePanel.setNumber(surfaceParametersOptimisation.saInitialTemperature);
		
		optimizeButton = new JButton(OPTIMIZE_BUTTON_OPTIMIZE);
		optimizeButton.setToolTipText("Run the optimisation!");
		optimizeButton.addActionListener(this);
		
		optimisationPanel.add(GUIBitsAndBobs.makeRow(noOfIterationsPanel, "iterations, initial temperature", saInitialTemperaturePanel, optimizeButton), "span");
		
		meanAlignmentPanel = new LabelledDoublePanel("Mean alignment");
		meanAlignmentPanel.getDoublePanel().setText("not calculated");
		meanAlignmentPanel.getDoublePanel().setEditable(false);
		
		logMeanAlignmentPanel = new DoublePanel();
		logMeanAlignmentPanel.setText("not calculated");
		logMeanAlignmentPanel.setToolTipText("Basically the number of 9s after the decimal point");
		logMeanAlignmentPanel.setEditable(false);
		
		updateMeanAlignmentButton = new JButton("Calculate");
		updateMeanAlignmentButton.setToolTipText("Click to (re)calculate mean alignment");
		updateMeanAlignmentButton.addActionListener(this);

		optimisationPanel.add(GUIBitsAndBobs.makeRow(meanAlignmentPanel, 
				"<html>, -log<sub>10</sub>(1-(<i>mean alignment</i>))</html>",	// ", -log(1-(mean alignment))", 
				logMeanAlignmentPanel, "", updateMeanAlignmentButton),  "wrap");
		
//		// the console panel
//
//		tabbedPane.addTab("Console", MessageConsole.createConsole(30, 70));
//		// create a console
//		// interactiveControlPanel.add(MessageConsole.createConsole(30, 70), "span");
		
		// the camera panel
		
		JPanel cameraPanel = new JPanel();
		cameraPanel.setLayout(new MigLayout("insets 0"));
		tabbedPane.addTab("Camera", cameraPanel);

		cameraTypeComboBox = new JComboBox<CameraType>(CameraType.values());
		cameraTypeComboBox.setSelectedItem(cameraType);
		cameraTypeComboBox.addActionListener(this);
		cameraPanel.add(GUIBitsAndBobs.makeRow("Camera type", cameraTypeComboBox), "span");
		
		cameraParametersPanel = new JPanel();	// holds the parameters of the selected camera
		cameraPanel.add(cameraParametersPanel, "span");
		
		// eye / main camera stuff
		
		mainCameraPanel = new JPanel();
		mainCameraPanel.setLayout(new MigLayout("insets 0"));
		
		cameraDistancePanel = new LabelledDoublePanel("Camera distance");
		cameraDistancePanel.setNumber(cameraDistance);
		mainCameraPanel.add(cameraDistancePanel, "span");
		
		cameraViewCentrePanel = new LabelledVector3DPanel("View centre");
		cameraViewCentrePanel.setVector3D(cameraViewCentre);
		mainCameraPanel.add(cameraViewCentrePanel, "span");
		
		cameraViewDirectionPanel = new LabelledVector3DPanel("View direction");
		cameraViewDirectionPanel.setVector3D(cameraViewDirection);
		mainCameraPanel.add(cameraViewDirectionPanel, "span");
		
		cameraHorizontalFOVDegPanel = new DoublePanel();
		cameraHorizontalFOVDegPanel.setNumber(cameraHorizontalFOVDeg);
		mainCameraPanel.add(GUIBitsAndBobs.makeRow("Horizontal FOV", cameraHorizontalFOVDegPanel, "°"), "span");
		
		cameraApertureSizeComboBox = new JComboBox<ApertureSizeType>(ApertureSizeType.values());
		cameraApertureSizeComboBox.setSelectedItem(cameraApertureSize);
		mainCameraPanel.add(GUIBitsAndBobs.makeRow("Camera aperture", cameraApertureSizeComboBox), "span");		
		
		cameraFocussingDistancePanel = new LabelledDoublePanel("Focussing distance");
		cameraFocussingDistancePanel.setNumber(cameraFocussingDistance);
		mainCameraPanel.add(cameraFocussingDistancePanel);
		
		// side camera stuff
		
		sideCameraPanel = new JPanel();
		sideCameraPanel.setLayout(new MigLayout("insets 0"));

		sideCameraViewCentrePanel = new LabelledVector3DPanel("View centre");
		sideCameraViewCentrePanel.setVector3D(sideCameraViewCentre);
		sideCameraPanel.add(sideCameraViewCentrePanel, "wrap");
		
		sideCameraViewDirectionPanel = new LabelledVector3DPanel("View direction");
		sideCameraViewDirectionPanel.setVector3D(sideCameraViewDirection);
		sideCameraPanel.add(sideCameraViewDirectionPanel, "wrap");
		
		sideCameraUpDirectionPanel = new LabelledVector3DPanel("Up direction");
		sideCameraUpDirectionPanel.setVector3D(sideCameraUpDirection);
		sideCameraPanel.add(sideCameraUpDirectionPanel, "wrap");
		
		sideCameraWidthPanel = new LabelledDoublePanel("Width");
		sideCameraWidthPanel.setNumber(sideCameraWidth);
		sideCameraPanel.add(sideCameraWidthPanel, "wrap");
		
		cylinderRadiusPanel = new LabelledDoublePanel("Restrict surfaces to cylinder of radius");
		cylinderRadiusPanel.setNumber(cylinderRadius);
		sideCameraPanel.add(cylinderRadiusPanel, "wrap");
		
		noOfDirectionPairsTracedPanel = new LabelledIntPanel("No of direction pairs traced");
		noOfDirectionPairsTracedPanel.setNumber(noOfDirectionPairsTraced);
		sideCameraPanel.add(noOfDirectionPairsTracedPanel, "wrap");
		
		noOfRaysPerBundleTracedPanel = new LabelledIntPanel("No of rays per direction traced");
		noOfRaysPerBundleTracedPanel.setNumber(noOfRaysPerBundleTraced);
		sideCameraPanel.add(noOfRaysPerBundleTracedPanel, "wrap");
		
		rayRadiusPanel = new LabelledDoublePanel("Radius of rays");
		rayRadiusPanel.setNumber(rayRadius);
		sideCameraPanel.add(rayRadiusPanel, "wrap");

		
		updateCameraParametersPanel();
	}


	/**
	 * called before rendering;
	 * override when adding fields
	 */
	@Override
	protected void acceptValuesInInteractiveControlPanel()
	{
		super.acceptValuesInInteractiveControlPanel();
		
		// background
		studioInitialisation = (StudioInitialisationType)(studioInitialisationComboBox.getSelectedItem());
		addZPlane = addZPlaneCheckBox.isSelected();
		zPlaneZ = zPlaneZPanel.getNumber();
		zPlaneCheckerboardPeriod = zPlaneCheckerboardPeriodPanel.getNumber();

		cameraDistance = cameraDistancePanel.getNumber();
		cameraViewCentre = cameraViewCentrePanel.getVector3D();
		cameraViewDirection = cameraViewDirectionPanel.getVector3D();
		cameraHorizontalFOVDeg = cameraHorizontalFOVDegPanel.getNumber();
		cameraApertureSize = (ApertureSizeType)(cameraApertureSizeComboBox.getSelectedItem());
		cameraFocussingDistance = cameraFocussingDistancePanel.getNumber();
		
		sideCameraViewCentre = sideCameraViewCentrePanel.getVector3D();
		sideCameraViewDirection = sideCameraViewDirectionPanel.getVector3D();
		sideCameraUpDirection = sideCameraUpDirectionPanel.getVector3D();
		sideCameraWidth = sideCameraWidthPanel.getNumber();
		cylinderRadius = cylinderRadiusPanel.getNumber();
		noOfDirectionPairsTraced = noOfDirectionPairsTracedPanel.getNumber();
		noOfRaysPerBundleTraced = noOfRaysPerBundleTracedPanel.getNumber();
		rayRadius = rayRadiusPanel.getNumber();


		surfaceParameters.acceptGUIEntries(noOfSurfacesPanel, polynomialOrderPanel, surfaceParametersTabbedPane);
		
		transmissionCoefficient = transmissionCoefficientPanel.getNumber();
		
		// optimisation; read the direction  values *before* re-shaping the arrays
		for(int i=0; i<rayPairsParameters.noOfDirectionPairs; i++)
		{
			rayPairsParameters.directionsIn[i] = directionInPanel[i].getVector3D();
			rayPairsParameters.directionsOut[i] = directionOutPanel[i].getVector3D();
		}
		
		if(rayPairsParameters.noOfDirectionPairs != noOfDirectionPairsPanel.getNumber())
		{
			rayPairsParameters.noOfDirectionPairs = noOfDirectionPairsPanel.getNumber();
			
			rayPairsParameters.reshapeDirectionPairsArrays();
			updateDirectionPairsTabbedPane();
		}
		surfaceParametersOptimisation.noOfIterations = noOfIterationsPanel.getNumber();
		surfaceParametersOptimisation.saInitialTemperature = saInitialTemperaturePanel.getNumber();
		rayPairsParameters.directionsInConeAngleRad = MyMath.deg2rad(directionsInConeAngleDegPanel.getNumber());
		rayPairsParameters.lawOfRefraction = new LawOfRefraction1Parameter(
					(LawOfRefractionType)(lawOfRefractionComboBox.getSelectedItem()),
					lawOfRefractionAdditionalParameterPanel.getNumber()
				);
		rayPairsParameters.noOfRaysPerBundle = noOfRaysPerBundlePanel.getNumber();
		rayPairsParameters.rayStartPointsDiscRadius = rayStartPointsDiscRadiusPanel.getNumber();
	}
	

	private void updateDirectionPairsTabbedPane()
	{
		directionInPanel = new LabelledVector3DPanel[rayPairsParameters.noOfDirectionPairs];
		directionOutPanel = new LabelledVector3DPanel[rayPairsParameters.noOfDirectionPairs];
		
		int selectedIndex = directionsTabbedPane.getSelectedIndex();
		if((selectedIndex < 0) || (selectedIndex >= rayPairsParameters.noOfDirectionPairs)) selectedIndex = 0;

		
		// remove any existing tabs
		directionsTabbedPane.removeAll();
		
		// add new tabs
		for(int i=0; i<rayPairsParameters.noOfDirectionPairs; i++)
		{
			JPanel directionPairPanel = new JPanel();
			directionPairPanel.setLayout(new MigLayout("insets 0"));
			directionsTabbedPane.addTab("Direction pair #"+i, new JScrollPane(directionPairPanel));

			directionInPanel[i] = new LabelledVector3DPanel("In");
			directionInPanel[i].setVector3D(rayPairsParameters.directionsIn[i]);
			directionPairPanel.add(directionInPanel[i], "wrap");

			directionOutPanel[i] = new LabelledVector3DPanel("Out");
			directionOutPanel[i].setVector3D(rayPairsParameters.directionsOut[i]);
			directionPairPanel.add(directionOutPanel[i], "wrap");
		}
		
		if(selectedIndex < directionsTabbedPane.getTabCount()) directionsTabbedPane.setSelectedIndex(selectedIndex);
		
		directionsTabbedPane.revalidate();
	}

	private void updateLawOfRefractionAdditionalParameterPanel()
	{
		String parameterDescription = 
				rayPairsParameters.lawOfRefraction.lawOfRefractionType.getParameterDescription();
		//			((LawOfRefractionType)(lawOfRefractionComboBox.getSelectedItem())).getParameterDescription();
		if(parameterDescription != null) lawOfRefractionAdditionalParameterLabel.setText(parameterDescription);
		else lawOfRefractionAdditionalParameterLabel.setText("-- no parameters --");
		lawOfRefractionAdditionalParameterPanel.setEnabled(parameterDescription != null);
	}
	
	private void updateCameraParametersPanel()
	{
		cameraParametersPanel.removeAll();

		switch(cameraType)
		{
		case SIDE:
			cameraParametersPanel.add(sideCameraPanel, "wrap");
			break;
		case EYE:
		default:
			cameraParametersPanel.add(mainCameraPanel);
		}
		
		cameraParametersPanel.revalidate();
	}

	
	private transient JFileChooser fileChooser;

	@Override
	public void actionPerformed(ActionEvent e)
	{
		super.actionPerformed(e);
		
		if(e.getSource().equals(optimizeButton))
		{
			acceptValuesInInteractiveControlPanel();

			// run optimisation
			if(optimizeButton.getText() == OPTIMIZE_BUTTON_STOP)
			{
				surfaceParametersOptimisation.simulatedAnnealingWorker.cancel(true);
				optimizeButton.setText(OPTIMIZE_BUTTON_OPTIMIZE);
			}
			else
			{
				optimizeButton.setText(OPTIMIZE_BUTTON_STOP);
				surfaceParametersOptimisation.optimise(this);
			}
		}
		else if(e.getSource().equals(updateSurfaceParameterFieldsButton))
		{
			acceptValuesInInteractiveControlPanel();
		}
		else if(e.getSource().equals(randomiseSurfaceParametersButton))
		{
			acceptValuesInInteractiveControlPanel();
			surfaceParameters.randomiseSurfaceParameters();
			surfaceParameters.repopulateSurfaceParametersTabbedPane(surfaceParametersTabbedPane);
		}
		else if(e.getSource().equals(updateDirectionPairsFieldsButton))
		{
			acceptValuesInInteractiveControlPanel();
		}
		else if(e.getSource().equals(randomiseDirectionPairsButton))
		{
			acceptValuesInInteractiveControlPanel();
			rayPairsParameters.randomiseDirectionPairs();
			updateDirectionPairsTabbedPane();
		}
		else if(e.getSource().equals(initialiseDirectionsInButton))
		{
			acceptValuesInInteractiveControlPanel();
			setStatus("Randomising incident directions...");
			rayPairsParameters.randomiseDirectionsIn();
			setStatus("Calculating corresponding outgoing directions...");
			rayPairsParameters.calculateDirectionsOut();
			setStatus("Done initialising ray-direction pairs.");
			updateDirectionPairsTabbedPane();
		}			
		else if(e.getSource().equals(updateMeanAlignmentButton))
		{
			acceptValuesInInteractiveControlPanel();
			setMeanAlignment(surfaceParametersOptimisation.calculateMeanAlignment(surfaceParameters));
		}
		else if(e.getSource().equals(randomiseRayStartPointsButton))
		{
			acceptValuesInInteractiveControlPanel();
			setStatus("Randomising ray start points...");
			rayPairsParameters.randomiseRayStartPoints();
			setStatus("Done randomising ray start points.");
		}
		else if(e.getSource().equals(lawOfRefractionComboBox))
		{
			rayPairsParameters.lawOfRefraction = new LawOfRefraction1Parameter(
					(LawOfRefractionType)(lawOfRefractionComboBox.getSelectedItem()),
					lawOfRefractionAdditionalParameterPanel.getNumber()
				);
			updateLawOfRefractionAdditionalParameterPanel();
		}
		else if(e.getSource().equals(saveSurfaceParametersButton))
		{
			if(fileChooser == null) { fileChooser = new JFileChooser(); }
			fileChooser.setSelectedFile(new File(getFilename()+".sur"));
			int returnVal = fileChooser.showSaveDialog(container);
			
			if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                try {
					surfaceParameters.save(file.getAbsolutePath());
	    			setStatus("Surface parameters saved as \""+file.getAbsolutePath()+"\".");
				} catch (IOException e1) {
	    			setStatus("Aborted saving surface parameters as \""+file.getAbsolutePath()+"\" ("+e1.getMessage()+")");
	    			e1.printStackTrace();
				}
            }
			else
			{
				setStatus("Saving cancelled.");
			}
		}
		else if(e.getSource().equals(loadSurfaceParametersButton))
		{
			if(fileChooser == null) { fileChooser = new JFileChooser(); }
			fileChooser.setSelectedFile(new File(getFilename()+".sur"));
			int returnVal = fileChooser.showOpenDialog(container);
			
			if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                try {
					surfaceParameters = SurfaceParameters.load(file.getAbsolutePath());
					surfaceParameters.repopulateSurfaceParametersTabbedPane(surfaceParametersTabbedPane);
					noOfSurfacesPanel.setNumber(surfaceParameters.noOfSurfaces);
					polynomialOrderPanel.setNumber(surfaceParameters.polynomialOrder);
	    			setStatus("Surface parameters loaded from \""+file.getAbsolutePath()+"\".");
				} catch (Exception e1) {
	    			setStatus("Aborted loading surface parameters from \""+file.getAbsolutePath()+"\" ("+e1.getMessage()+")");
	    			e1.printStackTrace();
				}
            }
			else
			{
				setStatus("Loading surface parameters cancelled.");
			}
		}
		else if(e.getSource().equals(cameraTypeComboBox))
		{
			cameraType = (CameraType)(cameraTypeComboBox.getSelectedItem());
			
			updateCameraParametersPanel();
		}
	}

	@Override
	public void setStatus(String status)
	{
		super.setStatus(status);
		System.out.println("[ "+status+" ]");
	}
	
	public void setMeanAlignment(double meanAlignment)
	{
		meanAlignmentPanel.setNumber(meanAlignment);
		logMeanAlignmentPanel.setNumber(-Math.log10(1.-meanAlignment));
	}
	
	/**
	 * The main method, required so that this class can run as a Java application
	 * @param args
	 */
	public static void main(final String[] args)
	{
		(new PolynomialPhaseHologramExplorer()).run();
	}
}
