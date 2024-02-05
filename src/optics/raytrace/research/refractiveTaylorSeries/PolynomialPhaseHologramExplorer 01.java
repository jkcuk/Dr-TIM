package optics.raytrace.research.refractiveTaylorSeries;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingWorker;

import math.Geometry;
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
import optics.raytrace.GUI.lowLevel.LabelledVector3DPanel;
import optics.raytrace.core.Ray;
import optics.raytrace.core.SceneObjectPrimitive;
import optics.raytrace.core.Studio;
import optics.raytrace.core.StudioInitialisationType;
import optics.raytrace.exceptions.RayTraceException;
import optics.raytrace.exceptions.SceneException;
import optics.raytrace.research.refractiveTaylorSeries.LawOfRefraction1Parameter.LawOfRefractionType;
import optics.raytrace.sceneObjects.Plane;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.surfaces.Checked;


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
	int noOfIterations;
	RayPairsParameters rayPairsParameters;
//	int noOfDirectionPairs;
//	Vector3D rayPairsParameters.directionsIn[], rayPairsParameters.directionsOut[];
	public enum AlgorithmType {
		RANDOM("Random"),
		SIMULATED_ANNEALING("Simulated annealing");
		
		private String description;
		AlgorithmType(String description) {this.description = description;}
		@Override
		public String toString() {return description;}
	}
	AlgorithmType algorithmType;
	double saInitialTemperature;	// for simulated annealing
	
	//  background
	private StudioInitialisationType studioInitialisation;
	private boolean addZPlane;
	private double zPlaneZ;
	private double zPlaneCheckerboardPeriod;


	/**
	 * Constructor.
	 * Sets all parameters.
	 */
	public PolynomialPhaseHologramExplorer()
	{
		super();
		
		// dcss = new DirectionChangingSurfaceSequence();
		
		surfaceParameters = new SurfaceParameters(1, 3);
		transmissionCoefficient = 0.96;
//		addSurfaces();
		
		// optimisation
		rayPairsParameters = new RayPairsParameters(3, 100);
		noOfIterations = 10;
//		rayPairsParameters.noOfDirectionPairs = 3;
//		directionIn = new Vector3D[rayPairsParameters.noOfDirectionPairs];
//		directionOut = new Vector3D[rayPairsParameters.noOfDirectionPairs];
		for(int i=0; i<rayPairsParameters.noOfDirectionPairs; i++)
		{
			rayPairsParameters.directionsIn[i] = Vector3D.Z;
			rayPairsParameters.directionsOut[i] = Vector3D.Z;
		}
		rayPairsParameters.lawOfRefraction = new LawOfRefraction1Parameter(LawOfRefractionType.RAY_ROTATION, 0);
		algorithmType = AlgorithmType.SIMULATED_ANNEALING;
		saInitialTemperature = 0.1;
		
		studioInitialisation = StudioInitialisationType.HEAVEN;
		addZPlane = true;
		zPlaneZ = 10000;
		zPlaneCheckerboardPeriod = 1000;

		// camera
		cameraViewCentre = new Vector3D(0, 0, 0);
		cameraDistance = 10;
		cameraViewDirection = new Vector3D(0, 0, 1);
		cameraHorizontalFOVDeg = 40;
		cameraApertureSize = ApertureSizeType.MEDIUM;
		cameraFocussingDistance = 10000;

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

		
		printStream.println("studioInitialisation="+studioInitialisation);
		printStream.println("addZPlane="+addZPlane);
		printStream.println("zPlaneZ="+zPlaneZ);
		printStream.println("zPlaneCheckerboardPeriod="+zPlaneCheckerboardPeriod);

		surfaceParameters.writeParameters(printStream);
		printStream.println("transmissionCoefficient="+transmissionCoefficient);
//		printStream.println("numberOfSurfaces="+surfaceParameters.getNoOfSurfaces());
//		printStream.println("polynomialOrder="+surfaceParameters.getPolynomialOrder());
//
//		for(int i =0;i<surfaceParameters.getNoOfSurfaces();i++) {
//			printStream.println("Surface #"+i);
//			printStream.println("  z["+i+"]="+surfaceParameters.getZ()[i]);
//			for(int n=0; n<=surfaceParameters.getPolynomialOrder(); n++)
//				for(int m=0; m<=n; m++)
//					printStream.println("  a["+i+"]["+n+"]["+m+"]="+surfaceParameters.getA()[i][n][m]);
//		}
		
		//  optimisation
		printStream.println("noOfIterations="+noOfIterations);
		printStream.println("noOfDirectionPairs="+rayPairsParameters.noOfDirectionPairs);
		printStream.println("Ray-direction pairs initialisation:");
		printStream.println("  directionsInConeAngleDeg="+MyMath.rad2deg(rayPairsParameters.directionsInConeAngleRad));
		printStream.println("  lawOfRefraction="+rayPairsParameters.lawOfRefraction.toString());
		printStream.println("  noOfRaysPerBundle="+rayPairsParameters.noOfRaysPerBundle);
		printStream.println("  rayStartPointsDiscRadius="+rayPairsParameters.rayStartPointsDiscRadius);

		printStream.print("directionIn={");
		for(int i=0; i<rayPairsParameters.noOfDirectionPairs; i++) {
			printStream.print(rayPairsParameters.directionsIn[i]);
			if(i<rayPairsParameters.noOfDirectionPairs-1) printStream.print(", ");
		}
		printStream.println("}");

		printStream.print("directionOut={");
		for(int i=0; i<rayPairsParameters.noOfDirectionPairs; i++) {
			printStream.print(rayPairsParameters.directionsOut[i]);
			if(i<rayPairsParameters.noOfDirectionPairs-1) printStream.print(", ");
		}
		printStream.println("}");
		printStream.println("algorithmType="+algorithmType);
		printStream.println("saInitialTemperature="+saInitialTemperature);
		printStream.println("noOfIterations="+noOfIterations);
		
		printStream.println("cameraViewCentre="+cameraViewCentre);
		printStream.println("cameraDistance="+cameraDistance);
		printStream.println("cameraViewDirection="+cameraViewDirection);
		printStream.println("cameraHorizontalFOVDeg="+cameraHorizontalFOVDeg);
		printStream.println("cameraApertureSize="+cameraApertureSize);
		printStream.println("cameraFocussingDistance="+cameraFocussingDistance);

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
		studio.setCamera(getStandardCamera());

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

		// add the surfaces from the DirectionChangingSurfaceSequence dcss to  the scene
		for(SceneObjectPrimitive s:dcss.getSceneObjectPrimitivesWithDirectionChangingSurfaces())
		{
			scene.addSceneObject(s);
		}
	}


	/**
	 * @param i
	 * @return	rayPairsParameters.directionsIn[i] if it exists, otherwise a unit vector in the z direction
	 */
	public Vector3D getDirectionInOrZ(int i)
	{
		if(i >= rayPairsParameters.directionsIn.length) return Vector3D.Z;
		
		return rayPairsParameters.directionsIn[i];
	}

	/**
	 * @param i
	 * @return	rayPairsParameters.directionsOut[i] if it exists, otherwise a unit vector in the z direction
	 */
	public Vector3D getDirectionOutOrZ(int i)
	{
		if(i >= rayPairsParameters.directionsOut.length) return Vector3D.Z;
		
		return rayPairsParameters.directionsOut[i];
	}

	/**
	 * run this if the number of direction pairs might have changed
	 */
	public void reshapeDirectionPairsArrays()
	{
		Vector3D newDirectionIn[] = new Vector3D[rayPairsParameters.noOfDirectionPairs];
		Vector3D newDirectionOut[] = new Vector3D[rayPairsParameters.noOfDirectionPairs];
		
		for(int i=0; i<rayPairsParameters.noOfDirectionPairs; i++)
		{
			newDirectionIn[i] = getDirectionInOrZ(i);
			newDirectionOut[i] = getDirectionOutOrZ(i);
		}

		rayPairsParameters.directionsIn = newDirectionIn;
		rayPairsParameters.directionsOut = newDirectionOut;
	}

	/**
	 * run this if the number of direction pairs might have changed
	 */
	public void randomiseDirectionPairs()
	{
		rayPairsParameters.randomiseDirectionsIn();
		rayPairsParameters.randomiseDirectionsOut();
		
//		rayPairsParameters.directionsIn = new Vector3D[rayPairsParameters.noOfDirectionPairs];
//		rayPairsParameters.directionsOut = new Vector3D[rayPairsParameters.noOfDirectionPairs];
//		
//		for(int i=0; i<rayPairsParameters.noOfDirectionPairs; i++)
//		{
//			rayPairsParameters.directionsIn[i]  = new Vector3D(.2*(Math.random()-0.5), .2*(Math.random()-0.5), 1);
//			rayPairsParameters.directionsOut[i] = new Vector3D(.2*(Math.random()-0.5), .2*(Math.random()-0.5), 1);
//		}
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
	LabelledDoublePanel meanAlignmentPanel;
	private DoublePanel directionsInConeAngleDegPanel, rayStartPointsDiscRadiusPanel, saInitialTemperaturePanel;
	final String OPTIMIZE_BUTTON_OPTIMIZE = "Optimise";
	private final String OPTIMIZE_BUTTON_STOP = "Stop";

	// camera stuff
	private LabelledDoublePanel cameraDistancePanel;
	private LabelledVector3DPanel cameraViewDirectionPanel, cameraViewCentrePanel;
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
		noOfIterationsPanel.setNumber(noOfIterations);
		
		saInitialTemperaturePanel = new DoublePanel();
		saInitialTemperaturePanel.setNumber(saInitialTemperature);
		
		optimizeButton = new JButton(OPTIMIZE_BUTTON_OPTIMIZE);
		optimizeButton.setToolTipText("Run the optimisation!");
		optimizeButton.addActionListener(this);
		
		optimisationPanel.add(GUIBitsAndBobs.makeRow(noOfIterationsPanel, "iterations, initial temperature", saInitialTemperaturePanel, optimizeButton), "span");
		
		meanAlignmentPanel = new LabelledDoublePanel("Mean alignment");
		meanAlignmentPanel.getDoublePanel().setText("not calculated");
		meanAlignmentPanel.getDoublePanel().setEditable(false);
		
		updateMeanAlignmentButton = new JButton("Calculate");
		updateMeanAlignmentButton.setToolTipText("Click to (re)calculate mean alignment");
		updateMeanAlignmentButton.addActionListener(this);

		optimisationPanel.add(GUIBitsAndBobs.makeRow(meanAlignmentPanel, updateMeanAlignmentButton),  "wrap");
		
//		// the console panel
//
//		tabbedPane.addTab("Console", MessageConsole.createConsole(30, 70));
//		// create a console
//		// interactiveControlPanel.add(MessageConsole.createConsole(30, 70), "span");
		
		// the camera panel
		
		JPanel cameraPanel = new JPanel();
		cameraPanel.setLayout(new MigLayout("insets 0"));
		tabbedPane.addTab("Camera", cameraPanel);

		// camera stuff
		
		cameraDistancePanel = new LabelledDoublePanel("Camera distance");
		cameraDistancePanel.setNumber(cameraDistance);
		cameraPanel.add(cameraDistancePanel, "span");
		
		cameraViewCentrePanel = new LabelledVector3DPanel("View centre");
		cameraViewCentrePanel.setVector3D(cameraViewCentre);
		cameraPanel.add(cameraViewCentrePanel, "span");
		
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
			
			reshapeDirectionPairsArrays();
			updateDirectionPairsTabbedPane();
		}
		noOfIterations = noOfIterationsPanel.getNumber();
		saInitialTemperature = saInitialTemperaturePanel.getNumber();
		rayPairsParameters.directionsInConeAngleRad = MyMath.deg2rad(directionsInConeAngleDegPanel.getNumber());
		rayPairsParameters.lawOfRefraction = new LawOfRefraction1Parameter(
					(LawOfRefractionType)(lawOfRefractionComboBox.getSelectedItem()),
					lawOfRefractionAdditionalParameterPanel.getNumber()
				);
		rayPairsParameters.noOfRaysPerBundle = noOfRaysPerBundlePanel.getNumber();
		rayPairsParameters.rayStartPointsDiscRadius = rayStartPointsDiscRadiusPanel.getNumber();
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
				simulatedAnnealingWorker.cancel(true);
				optimizeButton.setText(OPTIMIZE_BUTTON_OPTIMIZE);
			}
			else
			{
				optimizeButton.setText(OPTIMIZE_BUTTON_STOP);
				optimise();
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
			randomiseDirectionPairs();
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
			meanAlignmentPanel.setNumber(calculateMeanAlignment(surfaceParameters));
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
//			String parameterDescription = ((LawOfRefractionType)(lawOfRefractionComboBox.getSelectedItem())).getParameterDescription();
//			if(parameterDescription != null) lawOfRefractionAdditionalParameterLabel.setText(parameterDescription);
//			else lawOfRefractionAdditionalParameterLabel.setText("-- no parameters --");
//			lawOfRefractionAdditionalParameterPanel.setEnabled(parameterDescription != null);
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
	}
	
//	int noOfPoints = 100;
//	double radius = 1;
//	ArrayList<Vector2D> v2Ds = Geometry.getRandomPointsInUnitDisk(noOfPoints);
	
	SimulatedAnnealingWorker simulatedAnnealingWorker;
	
	public void optimise()
	{
		setStatus("Optimising...");

		// create a suitable set of starting points
		// v2Ds = Geometry.getRandomPointsInUnitDisk(noOfPoints);
		
		// check if the rayStartPoints have  been initialised
		if(rayPairsParameters.rayStartPoints == null) rayPairsParameters.randomiseRayStartPoints();
		
//		SurfaceParametersAndMeanAligment s = simulatedAnnealing(new SurfaceParametersAndMeanAligment(surfaceParameters, calculateMeanAlignment(surfaceParameters)));
//
//		surfaceParameters = s.surfaceParameters;
//		meanAlignmentPanel.setNumber(s.meanAlignment);
//		
//		System.out.println("surfaceParameters mean alignment = "+calculateMeanAlignment(surfaceParameters));
//
//		surfaceParameters.repopulateSurfaceParametersTabbedPane(surfaceParametersTabbedPane);
		
		simulatedAnnealingWorker = new SimulatedAnnealingWorker(
				new SurfaceParametersAndMeanAligment(surfaceParameters, calculateMeanAlignment(surfaceParameters)), 
				this
//				meanAlignmentPanel, 
//				surfaceParametersTabbedPane
			);
		simulatedAnnealingWorker.execute();
		
//		// surfaceParametersTabbedPane.update(surfaceParametersTabbedPane.getGraphics());
//
//		dirty = false;
//
//		// see CameraClass::takePhoto for example of multithreading
//		
//		int nthreads=Runtime.getRuntime().availableProcessors();
//		if(nthreads > 1) nthreads = nthreads - 1;	// leave one processor free to do GUI stuff
//		
//		// create arrays for the results from the threads to go into
//		SurfaceParameters[] testSurfaceParametersArray = new SurfaceParameters[nthreads];
//		double[] testMeanAlignmentArray = new double[nthreads];
//		
//		// ... and  populate these with the current surface parameters
//		double meanAlignment = calculateMeanAlignment(surfaceParameters);
//		for(int i=0; i<nthreads; i++)
//		{
//			testSurfaceParametersArray[i] = surfaceParameters;
//			testMeanAlignmentArray[i] = meanAlignment;
//		}

		
//		OptimisationWorker[] workers= new OptimisationWorker[nthreads];
//		for(int i=0; i<nthreads; i++)
//		{
//			workers[i]=new OptimisationWorker(testMeanAlignmentArray, testSurfaceParametersArray, 1); //make an array of worker objects, which tell the threads what to do
//		}
//
//		long latestUpdateTimeMillis = System.currentTimeMillis();
//		
//		// now do  the optimisation
//		boolean dirty = false;
//		for (int i=0; i<noOfIterations; ) 
//		{
//			if(algorithmType == AlgorithmType.SIMULATED_ANNEALING)
//			{
//				// the "temperature" during this iteration (starts at 1, goes to zero)
//				T = Math.pow(saInitialTemperature*(1-(((double)j)/((double)noOfIterations))), 4);
//			}
//
//			Thread[] threads=new Thread[nthreads];
//			for(int i=0; i<nthreads; i++) threads[i]=new Thread(workers[i]); //make new threads for the workers
//			for(int i=0; i<nthreads && i+j<noOfIterations; i++){
//				workers[i].setNoOfThread(i);
//				threads[i].start();								//and set them going
//			}
//			try
//			{
//				for(int i=0; i<nthreads; i++) threads[i].join();	//wait for all the workers to finish
//			}
//			catch (InterruptedException e)
//			{
//				e.printStackTrace();
//			}
//			
//			// update the optimum solution
//			for(int i=0; i<nthreads; i++)
//			{
//				if(testMeanAlignmentArray[i] > meanAlignment)
//				{
//					// a thread has found a  better solution
//					meanAlignment  = testMeanAlignmentArray[i];
//					surfaceParameters =  testSurfaceParametersArray[i];
//					dirty =  true;
//				}
//				
//				// if the neighbouring allocation has a higher happiness, make that the new allocation
//				if(happiness2 > happiness)
//				{
//					// make the change
//					allocation = allocation2;
//					happiness = happiness2;
//				}
//				else
//				{
//					// happiness2 <= happiness; make the change sometimes
//					double deltaH = happiness2 - happiness;	// < 0
//					// if(T*(1.-deltaH/minHappiness) + Math.random() > 1)
//					if(T*(1.-deltaH/happiness) + Math.random() > 1)
//					{
//						// System.out.println("Decrease in happiness! "+happiness+"->"+happiness2);
//						allocation = allocation2;
//						happiness = happiness2;
//					}
//				}
//			}
//			
//			// check how long it has been since the latest image update
//			if((System.currentTimeMillis() - latestUpdateTimeMillis) > 100)
//			{
//				if(dirty)
//				{
//					// it's been more than 100ms --- update the UI again
//					meanAlignmentPanel.setNumber(meanAlignment);
//					// meanAlignmentPanel.update(meanAlignmentPanel.getGraphics());
//
//					surfaceParameters.repopulateSurfaceParametersTabbedPane(surfaceParametersTabbedPane);
//					// surfaceParametersTabbedPane.update(surfaceParametersTabbedPane.getGraphics());
//
//					dirty = false;
//				}
//
//				// otherwise print feedback onto the console
//				// setStatus(
//				System.out.println(
//						"Iteration " + (j+nthreads) + " out of " + noOfIterations + " (on "+nthreads+" processors/cores)"
//					);
//				// forceStatusRepaint();
//								
//				latestUpdateTimeMillis = System.currentTimeMillis();
//			}
//	
//		}		
		
//		for(int iteration=0; iteration < noOfIterations; iteration++)
//		{
//			System.out.println("Iteration "+iteration);
//
//			// create a new "test" set of parameters
//			SurfaceParameters testSurfaceParameters = new SurfaceParameters(surfaceParameters.getNoOfSurfaces(), surfaceParameters.getPolynomialOrder());
//			testSurfaceParameters.randomiseSurfaceParameters();
//			
//			double testMeanAlignment = calculateMeanAlignment(testSurfaceParameters);
//			if(testMeanAlignment > meanAlignment)
//			{
//				//  we found a better set of surface parameters
//				System.out.println("Improved set of surface  parameters, iteration "+iteration+", mean alignment "+meanAlignment);
//				
//				// make this  the "current" set of  surface  parameters
//				surfaceParameters = testSurfaceParameters;
//				meanAlignment = testMeanAlignment;
//				meanAlignmentPanel.setNumber(meanAlignment);
//
//				surfaceParameters.repopulateSurfaceParametersTabbedPane(surfaceParametersTabbedPane);
//			}
//		}
		
// 		System.out.println(
//		setStatus(
//				"Done.");
	}
	
	
	class SurfaceParametersAndMeanAligment
	{
		public SurfaceParameters surfaceParameters;
		public double meanAlignment;
		
		public SurfaceParametersAndMeanAligment(SurfaceParameters surfaceParameters, double meanAlignment) {
			super();
			this.surfaceParameters = surfaceParameters;
			this.meanAlignment = meanAlignment;
		}
	}
	
	class SimulatedAnnealingState
	{
		public SurfaceParametersAndMeanAligment s;
		public int iteration;
		public int maxIterations;
		public double T;
		
		public SimulatedAnnealingState(SurfaceParametersAndMeanAligment s, int iteration, int maxIterations, double T, long millis) {
			super();
			this.s = s;
			this.iteration = iteration;
			this.maxIterations = maxIterations;
			this.T = T;
		}
	}

	class SimulatedAnnealingWorker extends SwingWorker<SurfaceParametersAndMeanAligment, SimulatedAnnealingState> {
		SurfaceParametersAndMeanAligment s;
		PolynomialPhaseHologramExplorer pphe;
		//		LabelledDoublePanel meanAlignmentPanel;
		//		JTabbedPane surfaceParametersTabbedPane;
				
		public SimulatedAnnealingWorker(
				SurfaceParametersAndMeanAligment s, 
				PolynomialPhaseHologramExplorer pphe
				//				LabelledDoublePanel meanAlignmentPanel, JTabbedPane surfaceParametersTabbedPane
				)
		{
			super();

			this.s = s;
			this.pphe = pphe;
			//			this.meanAlignmentPanel = meanAlignmentPanel;
			//			this.surfaceParametersTabbedPane = surfaceParametersTabbedPane;
		}

		@Override
		public SurfaceParametersAndMeanAligment doInBackground() {
			return simulatedAnnealing();
		}

		private long startTimeMillis;
		private long lastPublicationTime = -1;

		@Override
		protected void done() {
			try {
				SurfaceParametersAndMeanAligment s = get();
				pphe.meanAlignmentPanel.setNumber(s.meanAlignment);
				s.surfaceParameters.repopulateSurfaceParametersTabbedPane(pphe.surfaceParametersTabbedPane);
				int seconds = (int)Math.floor(1e-3*(System.currentTimeMillis() - startTimeMillis));
				int HH = seconds / 3600;
				int MM = (seconds % 3600) / 60;
				int SS = seconds % 60;
				double fraction = 1e-3*(System.currentTimeMillis() - startTimeMillis) - seconds;
				pphe.setStatus("Done.  Optimisation took "+
						((HH > 0)?String.format("%02dh ", HH):"") +
						((MM > 0)?String.format("%02dm ", MM):"") +
						String.format("%.2fs", SS + fraction)
						// 1e-3*(System.currentTimeMillis() - startTimeMillis)+"s."
				);
			}
			catch (Exception e) {
			}
			optimizeButton.setText(OPTIMIZE_BUTTON_OPTIMIZE);
		}
		
		private void myPublish(SimulatedAnnealingState sas, long time)
		{
			lastPublicationTime = time;
			publish(sas);
		}
		
		@Override
		protected void process(List<SimulatedAnnealingState> sass) {
			try {
				SimulatedAnnealingState sas = sass.get(sass.size() - 1);
				pphe.meanAlignmentPanel.setNumber(sas.s.meanAlignment);
				sas.s.surfaceParameters.repopulateSurfaceParametersTabbedPane(pphe.surfaceParametersTabbedPane);
				
				if(simulatedAnnealingWorker.isCancelled()) pphe.setStatus("Optimisation cancelled");
				else
					pphe.setStatus(
							"Optimising ("+
									String.format("%.0f", Math.floor(100*(double)(sas.iteration)/(double)sas.maxIterations))+
									"%, iteration "+sas.iteration+" out of "+sas.maxIterations+", T="+
									String.format("%.2e", sas.T)+
									")"
							);
			} catch (Exception ignore) {
			}
		}


		public SurfaceParametersAndMeanAligment simulatedAnnealing()
		{
			double T;
			startTimeMillis = System.currentTimeMillis();

			for (int i=0; (i<noOfIterations) && (!isCancelled()); i++) 
			{
				// create a new, modified, set of surface parameters...
				SurfaceParameters surfaceParameters2 = s.surfaceParameters.getNeighbouringSurfaceParameters();

				// ... and calculate its mean alignment
				double meanAlignment2 = calculateMeanAlignment(surfaceParameters2);

				// the "temperature" during this iteration (starts at 1, goes to zero)
				T = saInitialTemperature*Math.pow((1.-(((double)(i+1))/((double)noOfIterations))), 4);

				long time = System.currentTimeMillis();

				// if the neighbouring parameter set has a higher alignment, make that the new parameter set
				if(meanAlignment2 > s.meanAlignment)
				{
					// make the change
					// System.out.println("iteration "+i+", T="+T+", improvement: "+s.meanAlignment+" -> "+meanAlignment2);
					s.surfaceParameters = surfaceParameters2;
					s.meanAlignment = meanAlignment2;
					myPublish(new SimulatedAnnealingState(s, i, noOfIterations, T, time - startTimeMillis), time);
				}
				else
				{
					// meanAlignment2 <= meanAlignment; make the change sometimes
					double deltaA = meanAlignment2 - s.meanAlignment;	// < 0
					// if(T*(1.-deltaH/minHappiness) + Math.random() > 1)
					// if(T*(1.-deltaA/s.meanAlignment) + Math.random() > 1)
					if(Math.random() < T*(1+deltaA/s.meanAlignment))
					{
						// make the change
						s.surfaceParameters = surfaceParameters2;
						s.meanAlignment = meanAlignment2;
						// System.out.println("Making things worse!");
						myPublish(new SimulatedAnnealingState(s, i, noOfIterations, T, time - startTimeMillis), time);
					}
					else
					{
						if(time - lastPublicationTime > 100) 
							myPublish(new SimulatedAnnealingState(s, i, noOfIterations, T, time - startTimeMillis), time);
					}
				}
			}

			return s;
		}
	}
	
	public SurfaceParametersAndMeanAligment simulatedAnnealing(SurfaceParametersAndMeanAligment s)
	{
		double T;

		for (int i=0; i<noOfIterations; i++) 
		{
			// create a new, modified, set of surface parameters...
			SurfaceParameters surfaceParameters2 = s.surfaceParameters.getNeighbouringSurfaceParameters();

			// ... and calculate its mean alignment
			double meanAlignment2 = calculateMeanAlignment(surfaceParameters2);

			// the "temperature" during this iteration (starts at 1, goes to zero)
			T = saInitialTemperature*Math.pow((1.-(((double)(i+1))/((double)noOfIterations))), 4);

			// if the neighbouring parameter set has a higher alignment, make that the new parameter set
			if(meanAlignment2 > s.meanAlignment)
			{
				// make the change
				System.out.println("iteration "+i+", T="+T+", improvement: "+s.meanAlignment+" -> "+meanAlignment2);
				s.surfaceParameters = surfaceParameters2;
				s.meanAlignment = meanAlignment2;
			}
			else
			{
				// meanAlignment2 <= meanAlignment; make the change sometimes
				double deltaA = meanAlignment2 - s.meanAlignment;	// < 0
				// if(T*(1.-deltaH/minHappiness) + Math.random() > 1)
				// if(T*(1.-deltaA/s.meanAlignment) + Math.random() > 1)
				if(Math.random() < T*(1+deltaA/s.meanAlignment))
				{
					// make the change
					s.surfaceParameters = surfaceParameters2;
					s.meanAlignment = meanAlignment2;
					System.out.println("Making things worse!");
				}
			}
		}
		
		return s;
	}
	
	class MeanParallelnessCalculationWorker implements Runnable
	{
		public MeanParallelnessCalculationWorker(DirectionChangingSurfaceSequence dcss, double[] parallelnesses, int workerNo)
		{
			this.dcss = dcss;
			this.parallelnesses = parallelnesses;
			this.workerNo = workerNo;
		}
		
		private DirectionChangingSurfaceSequence dcss;
		private double[] parallelnesses;
		private int rayPairNo;
		private int workerNo;
		
		public void setRayPairNo(int rayPairNo) {this.rayPairNo = rayPairNo;}
		@Override
		public void run() {
			// construct a set of rays with the given starting points
			ArrayList<Vector3D> directionsOut = new ArrayList<Vector3D>(rayPairsParameters.noOfRaysPerBundle);
			for(Vector3D rayStartPoint:rayPairsParameters.rayStartPoints)
				try {
					directionsOut.add(
							dcss.calculateTransmittedRay(
									new Ray(
											rayStartPoint,	// start point
											rayPairsParameters.directionsIn[rayPairNo], 	// direction
											0,	// start time
											false	// reportToConsole
											)	// the incident ray
									).getD()	// the direction of the transmitted ray
							);
				} catch (RayTraceException e) {
					parallelnesses[workerNo] = -1;
					return;
				}

			// return the mean alignment
			parallelnesses[workerNo] = Geometry.calculateParallelness(directionsOut, true);
		}
	}

	/**
	 * Multi-threaded version
	 * @param testSurfaceParameters
	 * @return
	 */
	public double calculateMeanParallelness(SurfaceParameters testSurfaceParameters)
	{
		DirectionChangingSurfaceSequence dcss = testSurfaceParameters.createCorrespondingDirectionChangingSurfaceSequence(transmissionCoefficient);

		int nthreads=Runtime.getRuntime().availableProcessors();
		if(nthreads > 1) nthreads = nthreads - 1;	// leave one processor free to do GUI stuff
		
		MeanParallelnessCalculationWorker[] workers= new MeanParallelnessCalculationWorker[nthreads];
		double[] parallelnesses = new double[nthreads];
		for(int i=0; i<nthreads; i++) workers[i]=new MeanParallelnessCalculationWorker(dcss, parallelnesses, i); //make an array of worker objects, which tell the threads what to do

		// go through all set of direction pairs
		double parallelnessSum = 0;
		int noOfDirectionsPairsSimulated = 0;
		for(int j=0; j<rayPairsParameters.noOfDirectionPairs; )
		{
			// check if the outgoing light-ray  direction exists
			if(rayPairsParameters.directionsOut[j] == Vector3D.NaV) 
			{
				j += 1;	// ignore this direction pair
				break;
			}

			Thread[] threads=new Thread[nthreads];
			int i;
			for(i=0; i<nthreads && j<rayPairsParameters.noOfDirectionPairs; i++) {
				threads[i]=new Thread(workers[i]); //make new threads for the workers
				workers[i].setRayPairNo(j);				//assign one ray pair to each worker object
				threads[i].start();						//and set them going
				j += 1;
			}
			
			int threadsRunning = i;
			try
			{
				for(i=0; i<threadsRunning; i++)
				{
					threads[i].join();	//wait for all the workers to finish
					parallelnessSum += parallelnesses[i];
					noOfDirectionsPairsSimulated += 1;
					if(Double.isNaN(parallelnessSum))
						System.out.println("parallelness = "+parallelnesses[i]+", parallelnessSum = "+parallelnessSum+", noOfDirectionsPairsSimulated = "+noOfDirectionsPairsSimulated);
				}
			}
			catch (InterruptedException e) {}
		}

		// calculate the mean alignment
		return parallelnessSum / noOfDirectionsPairsSimulated;
	}

	
	class MeanAlignmentCalculationWorker implements Runnable
	{
		public MeanAlignmentCalculationWorker(DirectionChangingSurfaceSequence dcss, double[] alignments, int workerNo)
		{
			this.dcss = dcss;
			this.alignments = alignments;
			this.workerNo = workerNo;
		}
		
		private DirectionChangingSurfaceSequence dcss;
		private double[] alignments;
		private int rayPairNo;
		private int workerNo;
		
		public void setRayPairNo(int rayPairNo) {this.rayPairNo = rayPairNo;}
		@Override
		public void run() {
			// construct a set of rays with these starting points
			ArrayList<Ray> raysIn = new ArrayList<Ray>(rayPairsParameters.noOfRaysPerBundle);
			for(Vector3D rayStartPoint:rayPairsParameters.rayStartPoints)
				raysIn.add(
						new Ray(
								rayStartPoint,	// start point
								rayPairsParameters.directionsIn[rayPairNo], 	// direction
								0,	// start time
								false	// reportToConsole
								)
						);

			double alignmentSum = 0;
			int noOfAlignments = 0;
			for(Ray rayIn:raysIn)
			{
				try {
					// simulate transmission through the surfaces
					Ray rayOut = dcss.calculateTransmittedRay(rayIn);
					alignmentSum += Vector3D.scalarProduct(
							rayPairsParameters.directionsOut[rayPairNo], 
							rayOut.getD()
						);
				} catch (RayTraceException e) {
					// if the transmitted ray is evanescent, it will get reflected, so the alignment becomes negative;
					// assume the worst-case scenario (alignment = -1) -- we really don't want evanescent rays!
					alignmentSum -= 1;
				}
				noOfAlignments += 1;
			}
			
			// return the mean alignment
			alignments[workerNo] = alignmentSum / noOfAlignments;
			
//			System.out.println("alignmentSum = "+alignmentSum+", noOfAlignments = "+noOfAlignments);
			
//			// calculate the rays after transmission through the surfaces
//			ArrayList<Ray> raysOut = new ArrayList<Ray>(rayPairsParameters.noOfRaysPerBundle);
//			for(Ray ray:raysIn)
//				try {
//					raysOut.add(dcss.calculateTransmittedRay(ray));
//				}
//			catch (RayTraceException x) {
////				evanescentRays += 1;
//				// System.out.println("Evanescent ray!");
//			}
//
//			// we only care about the directions of the outgoing rays, which we collect into an ArrayList...
//			ArrayList<Vector3D> dOut =  new ArrayList<Vector3D>(rayPairsParameters.noOfRaysPerBundle);
//			for(Ray ray:raysOut) dOut.add(ray.getD());
//
//			// ... whose alignment with the intended direction we then check
//			alignments[workerNo] = Geometry.calculateAlignment(rayPairsParameters.directionsOut[rayPairNo], dOut, true);
		}
	}

	/**
	 * Multi-threaded version
	 * @param testSurfaceParameters
	 * @return
	 */
	public double calculateMeanAlignment(SurfaceParameters testSurfaceParameters)
	{
		DirectionChangingSurfaceSequence dcss = testSurfaceParameters.createCorrespondingDirectionChangingSurfaceSequence(transmissionCoefficient);

		int nthreads=Runtime.getRuntime().availableProcessors();
		if(nthreads > 1) nthreads = nthreads - 1;	// leave one processor free to do GUI stuff
		
		MeanAlignmentCalculationWorker[] workers= new MeanAlignmentCalculationWorker[nthreads];
		double[] alignments = new double[nthreads];
		for(int i=0; i<nthreads; i++) workers[i]=new MeanAlignmentCalculationWorker(dcss, alignments, i); //make an array of worker objects, which tell the threads what to do

		// go through all set of direction pairs
		double alignmentSum = 0;
		int noOfDirectionsPairsSimulated = 0;
		for(int j=0; j<rayPairsParameters.noOfDirectionPairs; )
		{
			// check if the outgoing light-ray  direction exists
			if(rayPairsParameters.directionsOut[j] == Vector3D.NaV) 
			{
				j += 1;	// ignore this direction pair
				break;
			}

			Thread[] threads=new Thread[nthreads];
			int i;
			for(i=0; i<nthreads && j<rayPairsParameters.noOfDirectionPairs; i++) {
				threads[i]=new Thread(workers[i]); //make new threads for the workers
				workers[i].setRayPairNo(j);				//assign one ray pair to each worker object
				threads[i].start();						//and set them going
				j += 1;
			}
			
			int threadsRunning = i;
			try
			{
				for(i=0; i<threadsRunning; i++)
				{
					threads[i].join();	//wait for all the workers to finish
					alignmentSum += alignments[i];
					noOfDirectionsPairsSimulated += 1;
					if(Double.isNaN(alignmentSum))
						System.out.println("alignment = "+alignments[i]+", alignmentSum = "+alignmentSum+", noOfDirectionsPairsSimulated = "+noOfDirectionsPairsSimulated);
				}
			}
			catch (InterruptedException e) {}
		}

		// calculate the mean alignment
		return alignmentSum / noOfDirectionsPairsSimulated;
	}

	
	/**
	 * Single-threaded version
	 * @param testSurfaceParameters
	 * @return
	 */
	public double calculateMeanAlignment_parked(SurfaceParameters testSurfaceParameters)
	{
		DirectionChangingSurfaceSequence dcss = testSurfaceParameters.createCorrespondingDirectionChangingSurfaceSequence(transmissionCoefficient);
//		addSurfaces();
		
		// go through all set of direction pairs
		double alignmentSum = 0;
		int noOfDirectionsPairsSimulated = 0;
//		int evanescentRays = 0;
		for(int i=0; i<rayPairsParameters.noOfDirectionPairs; i++)
		{
			// check if the outgoing light-ray  direction exists
			if(rayPairsParameters.directionsOut[i] == Vector3D.NaV) continue;
			
			// construct a set of rays with these starting points
			ArrayList<Ray> raysIn = new ArrayList<Ray>(rayPairsParameters.noOfRaysPerBundle);
			for(Vector3D rayStartPoint:rayPairsParameters.rayStartPoints)
				raysIn.add(
						new Ray(
								rayStartPoint,	// start point
								rayPairsParameters.directionsIn[i], 	// direction
								0,	// start time
								false	// reportToConsole
								)
						);

			// calculate the rays after transmission through the surfaces
			ArrayList<Ray> raysOut = new ArrayList<Ray>(rayPairsParameters.noOfRaysPerBundle);
			for(Ray ray:raysIn)
				try {
					raysOut.add(dcss.calculateTransmittedRay(ray));
				}
			catch (RayTraceException x) {
//				evanescentRays += 1;
				// System.out.println("Evanescent ray!");
			}

//			ArrayList<Vector3D> dIn =  new ArrayList<Vector3D>(noOfPoints);
//			for(Ray ray:raysIn) dIn.add(ray.getD());

			// we only care about the directions of the outgoing rays, which we collect into an ArrayList...
			ArrayList<Vector3D> dOut =  new ArrayList<Vector3D>(rayPairsParameters.noOfRaysPerBundle);
			for(Ray ray:raysOut) dOut.add(ray.getD());

			//			// System.out.println("r="+r);
			//			System.out.println(
			//					"Input: "+raysIn.size()+" rays, parallelness "+String.format( "%.3f", Geometry.calculateParallelness(dIn, true))+
			//					"; output: "+raysOut.size()+" rays, parallelness "+String.format( "%.3f", Geometry.calculateParallelness(dOut, true))
			//					);

			// ... whose alignment with the intended direction we then check
			double  alignment = Geometry.calculateAlignment(rayPairsParameters.directionsOut[i], dOut, true);
//			System.out.println(
//					"Direction pair "+i+": alignment with "+rayPairsParameters.directionsOut[i]+" "+
//							String.format("%.3f", alignment)
//					);
			alignmentSum += alignment;
			noOfDirectionsPairsSimulated += 1;
		}

		double meanAlignment = alignmentSum / noOfDirectionsPairsSimulated;
//		System.out.println("Mean alignment with outgoing directions "+String.format("%.3f", meanAlignment));
//
//		double evanescentFraction = ((double)evanescentRays) / (rayPairsParameters.noOfDirectionPairs*noOfPoints);
//		System.out.println("Evanescent fraction "+String.format("%.3f", evanescentFraction));
		
		return meanAlignment;
	}
	
	@Override
	public void setStatus(String status)
	{
		super.setStatus(status);
		System.out.println("[ "+status+" ]");
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
