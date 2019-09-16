package optics.raytrace.research.curvedSpaceSimulation;

import java.awt.event.ActionEvent;
import java.io.PrintStream;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import math.*;
import net.miginfocom.swing.MigLayout;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.surfaces.ColourFilter;
import optics.raytrace.surfaces.SurfaceColour;
import optics.raytrace.surfaces.SurfaceColourLightSourceIndependent;
import optics.raytrace.exceptions.InconsistencyException;
import optics.raytrace.exceptions.SceneException;
import optics.DoubleColour;
import optics.raytrace.NonInteractiveTIMActionEnum;
import optics.raytrace.NonInteractiveTIMEngine;
import optics.raytrace.GUI.cameras.RenderQualityEnum;
import optics.raytrace.GUI.lowLevel.ApertureSizeType;
import optics.raytrace.GUI.lowLevel.DoublePanel;
import optics.raytrace.GUI.lowLevel.GUIBitsAndBobs;
import optics.raytrace.GUI.lowLevel.LabelledDoublePanel;
import optics.raytrace.GUI.lowLevel.LabelledIntPanel;
import optics.raytrace.GUI.lowLevel.LabelledVector3DPanel;
import optics.raytrace.GUI.sceneObjects.EditableNetOfSymmetric4Simplex;
import optics.raytrace.GUI.sceneObjects.EditableRayTrajectory;
import optics.raytrace.GUI.sceneObjects.EditableScaledParametrisedSphere;
import optics.raytrace.core.Studio;
import optics.raytrace.core.StudioInitialisationType;


/**
 * Plots a number of trajectories through the net of a symmetric 4-simplex.
 * 
 * @author Johannes Courtial 
 */
public class NetOfSymmetric4SimplexVisualiser extends NonInteractiveTIMEngine
{
	// most additional parameters are stored in the following EditableNetOfRegular4Simplex
	private EditableNetOfSymmetric4Simplex net;
	
	/**
	 * show the null-space wedges
	 */
	private boolean showNullSpaceWedges;

	/**
	 * show edges of the simplicial complex that forms the net of the 4-simplex
	 */
	private boolean showNetEdges;

	/**
	 * show faces of the simplicial complex that forms the net of the 4-simplex
	 */
	private boolean showNetFaces;

	/**
	 * show edges of the null-space wedges, i.e. the vertices and edges of the sheets forming the null-space wedges
	 */
	private boolean showNullSpaceWedgeEdges;

	/**
	 * Curved-space-simulation type, which describes the way the simulation is performed
	 */
	protected GluingType curvedSpaceSimulationType;

	/**
	 * show trajectory
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
	 * radius of the trajectory
	 */
	private double trajectoryRadius;
	
	/**
	 * max trace level for trajectory tracing
	 */
	private int trajectoryMaxTraceLevel;
	
	/**
	 * Determines how to initialise the backdrop
	 */
	private StudioInitialisationType studioInitialisation;

	/**
	 * show the sphere?
	 */
	private boolean showSphere;
	
	/**
	 * centre of red sphere
	 */
	private Vector3D sphereCentre;

	/**
	 * radius of sphere
	 */
	private double sphereRadius;
	
//	/**
//	 * for debugging; if this variable takes a positive value, show only null-space wedge #showOnlyNullSpaceWedgeNo
//	 */
	private int setShowOnlyNullSpaceWedgeNo;	// TODO for debugging
	

	/**
	 * Constructor.
	 * Sets all parameters.
	 */
	public NetOfSymmetric4SimplexVisualiser()
	{
		super();
		
		// set to true for interactive version
		nonInteractiveTIMAction = NonInteractiveTIMActionEnum.INTERACTIVE;

		// set all parameters
		
		curvedSpaceSimulationType = GluingType.NEGATIVE_SPACE_WEDGES_WITH_CONTAINMENT_MIRRORS;

		showNullSpaceWedges = false;
		showNetEdges = true;
		showNetFaces = true;
		showNullSpaceWedgeEdges = false;
 		setShowOnlyNullSpaceWedgeNo =  -1;	// TODO for debugging
		
		// the parameters for the net of the symmetric 4-simplex are held in the EditableNetOfRegular4Simplex net; create this and set the parameters
		net = new EditableNetOfSymmetric4Simplex(
				"Net of symmetric 4-simplex",
				new Vector3D(0, 0, 0),	// centroid
				1,	// edgeLength,
				1,	// outerTetrahedronHeightFactor
				new Vector3D(1, 0, 0),	// normal to face 1 of enclosing cube
				new Vector3D(0, 1, 0),	// normal to face 1 of enclosing cube
				new Vector3D(0, 0, 1),	// normal to face 1 of enclosing cube
				showNullSpaceWedges,	// showNullSpaceWedges
				1,	// nullSpaceWedgeLegLengthFactor
				1,	// refractingSurfaceTransmissionCoefficient
				curvedSpaceSimulationType,	// gluingType
				1,	// numberOfNegativeSpaceWedges
				showNetEdges,	// showNetStructure
				showNetFaces,
				showNullSpaceWedgeEdges,	// showNullSpaceWedgesStructure
				SurfaceColour.GREEN_SHINY,	// netStructureSurfaceProperty
				ColourFilter.LIGHT_CYAN_GLASS,	// netFaceSurfaceProperty
				SurfaceColour.RED_SHINY,	// nullSpaceWedgesStructureSurfaceProperty
				0.02,	// structureTubeRadius
				null,	// parent
				null	// studio
			);

		// trajectory
		showTrajectory = false;
		trajectoryStartPoint = new Vector3D(0, 0, 0);
		trajectoryStartDirection = new Vector3D(.1, 1, 0);
		trajectoryRadius = 0.001;
		trajectoryMaxTraceLevel = 100;
		
		// stuff
		studioInitialisation = StudioInitialisationType.HEAVEN;	// the backdrop
		showSphere = false;
		sphereCentre = new Vector3D(0.2, -0.1, 0);
		sphereRadius = 0.05;

		// camera
		Vector3D cameraApertureCentre = new Vector3D(4, 4, -6);
		cameraViewCentre = new Vector3D(0, 0.1, 0);
		cameraViewDirection = Vector3D.difference(cameraViewCentre, cameraApertureCentre);
		cameraDistance = cameraViewDirection.getLength();
		cameraFocussingDistance = cameraViewDirection.getLength();
		cameraHorizontalFOVDeg = 15;
		cameraMaxTraceLevel = 100;
		cameraPixelsX = 640;
		cameraPixelsY = 480;
		cameraApertureSize = ApertureSizeType.PINHOLE;

		traceRaysWithTrajectory = false;	// don't automatically trace rays with trajectory
		
		renderQuality = RenderQualityEnum.DRAFT;
		
		// boring parameters
		nonInteractiveTIMAction = NonInteractiveTIMActionEnum.INTERACTIVE;

		// for movie version
		numberOfFrames = 50;
		firstFrame = 0;
		lastFrame = numberOfFrames-1;
		
		// for interactive version
		windowTitle = "Dr TIM's net-of-symmetric-4-simplex visualiser";
		windowWidth = 1300;
		windowHeight = 650;
	}

	@Override
	public String getFirstPartOfFilename()
	{
		return "NetOfSymmetric4SimplexVisualiser"	// the name
				;
	}
	
	
	
	@Override
	public void writeParameters(PrintStream printStream)
	{
		// write any parameters not defined in NonInteractiveTIMEngine
		printStream.println("Scene initialisation");
		printStream.println();
		
		printStream.println("showNullSpaceWedges = "+showNullSpaceWedges);
		printStream.println("curvedSpaceSimulationType = "+curvedSpaceSimulationType);
		printStream.println("showNetEdges = "+showNetEdges);
		printStream.println("showNetFaces = "+showNetFaces);
		printStream.println("showNullSpaceWedgeEdges = "+showNullSpaceWedgeEdges);
		
		printStream.println("showSphere = "+showSphere);
		if(showSphere)
		{		
			printStream.println("sphereCentre = "+sphereCentre);
			printStream.println("sphereRadius = "+sphereRadius);
		}
		
		printStream.println("studioInitialisation = "+studioInitialisation);

		printStream.println();

		// write all parameters defined in NonInteractiveTIMEngine
		super.writeParameters(printStream);		
	}

	
	private void addMirrors()
	{
		// TODO add code that adds mirrors here
		// SceneObjectContainer scene = (SceneObjectContainer)studio.getScene();
		// scene.addSceneObject();
	}
	
	/* (non-Javadoc)
	 * @see optics.raytrace.NonInteractiveTIMEngine#populateStudio()
	 */
	@Override
	public void populateStudio()
	throws SceneException
	{
		// super.populateSimpleStudio();
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
		
		//  make it a bit brighter
//		LightSourceContainer lights = new LightSourceContainer("lights");
//		lights.add(new AmbientLight("background light", DoubleColour.GREY60));
//		lights.add(new PhongLightSource("point light souce", new Vector3D(100,300,-500), DoubleColour.WHITE, DoubleColour.WHITE, 40.));
//		lights.add(new PhongLightSource("red point light souce", new Vector3D(100,300,500), DoubleColour.RED, DoubleColour.RED, 40.));
//		lights.add(new PhongLightSource("blue point light souce", new Vector3D(-500,300,00), DoubleColour.BLUE, DoubleColour.BLUE, 40.));
//		studio.setLights(lights);


		// the net: set its parent to <i>scene</i>...
		net.setParent(scene);
		net.setGluingType(curvedSpaceSimulationType);	// (curvedSpaceSimulationType==GluingType.NEGATIVE_SPACE_WEDGES?GluingType.NEGATIVE_SPACE_WEDGES:GluingType.PERFECT));
		net.setShowNetEdges(false);
		net.setShowNetFaces(false);
		net.setShowNullSpaceWedgeEdges(false);
 		net.setShowOnlyNullSpaceWedgeNo(setShowOnlyNullSpaceWedgeNo);
		// ... and get it prepared for light-ray-trajectory tracing
		switch(curvedSpaceSimulationType)
		{
		case NEGATIVE_SPACE_WEDGES:
		case NEGATIVE_SPACE_WEDGES_SYMMETRIC:
		case NEGATIVE_SPACE_WEDGES_WITH_CONTAINMENT_MIRRORS:
		case PERFECT:
			// showing the null-space wedges but hiding all edges
			net.setShowNullSpaceWedges(true);
			break;
		case MIRROR_APPROXIMATION:
			net.setShowNullSpaceWedges(false);
			addMirrors();
		}

		// now add the scene objects to the net...
		try {
			net.populateSceneObjectCollection();
		} catch (InconsistencyException e) {
			e.printStackTrace();
		}
		// ... and add the net to the scene
		scene.addSceneObject(net);
		
		
		// stuff
		
		scene.addSceneObject(
				new EditableScaledParametrisedSphere(
						"White sphere",	// description
						sphereCentre,	// centre
						sphereRadius,	// radius
						SurfaceColour.WHITE_SHINY,	// surfaceProperty
						scene,	// parent
						studio
						),
				showSphere
			);
		
		

		// a trajectory
		
		scene.addSceneObject(new EditableRayTrajectory(
				"Trajectory",
				trajectoryStartPoint,	// start point
				0,	// start time
				trajectoryStartDirection,	// initial direction
				trajectoryRadius,	// radius
				new SurfaceColourLightSourceIndependent(DoubleColour.RED, true),
				trajectoryMaxTraceLevel,	// max trace level
				false,	// reportToConsole
				scene,
				studio
			),
			showTrajectory
		);
		

		// trace the rays with trajectory through the scene
		studio.traceRaysWithTrajectory();

		
		// prepare the net for standard ray tracing...
		net.setShowNetEdges(showNetEdges);
		net.setShowNetFaces(showNetFaces);
		switch(curvedSpaceSimulationType)
		{
		case NEGATIVE_SPACE_WEDGES:
		case NEGATIVE_SPACE_WEDGES_SYMMETRIC:
		case NEGATIVE_SPACE_WEDGES_WITH_CONTAINMENT_MIRRORS:
		case PERFECT:
			net.setShowNullSpaceWedges(showNullSpaceWedges);
			net.setShowNullSpaceWedgeEdges(showNullSpaceWedgeEdges);
			break;
		case MIRROR_APPROXIMATION:
			net.setShowNullSpaceWedges(false);
			net.setShowNullSpaceWedgeEdges(false);
			addMirrors();
		}

		// ... and add the scene objects to the net
		try {
			net.populateSceneObjectCollection();
		} catch (InconsistencyException e) {
			e.printStackTrace();
		}

		// the camera
		studio.setCamera(getStandardCamera());
	}
	
	
	//
	// for interactive version
	//
	
	// GUI panels
	private LabelledVector3DPanel centroidPanel, normal2enclosingCube1Panel, normal2enclosingCube2Panel, normal2enclosingCube3Panel;
	private LabelledDoublePanel edgeLengthPanel, outerTetrahedronHeightFactorPanel, edgeRadiusPanel, nullSpaceWedgeLegLengthFactorPanel, nullSpaceWedgeSurfaceTransmissionCoefficientPanel;
	private LabelledIntPanel numberOfNegativeSpaceWedgesPanel, setShowOnlyNullSpaceWedgeNoPanel;	// TODO for debugging
//	private JComboBox<GluingType> gluingTypeComboBox;
	private JComboBox<GluingType> curvedSpaceSimulationTypeComboBox;
	private JCheckBox showNullSpaceWedgesCheckBox, showNetEdgesCheckBox, showNetFacesCheckBox, showNullSpaceWedgesEdgesCheckBox;
	
	// trajectory
	private JCheckBox showTrajectoryCheckBox;
	private LabelledVector3DPanel trajectoryStartPointPanel, trajectoryStartDirectionPanel;
	private LabelledDoublePanel trajectoryRadiusPanel;
	private LabelledIntPanel trajectoryMaxTraceLevelPanel;

	// stuff
	private JComboBox<StudioInitialisationType> studioInitialisationComboBox;
	private JCheckBox showSphereCheckBox;
	private LabelledVector3DPanel sphereCentrePanel;
	private LabelledDoublePanel sphereRadiusPanel;

	// main (outside) camera
	private LabelledVector3DPanel cameraPositionPanel, cameraViewCentrePanel;
	private DoublePanel cameraHorizontalFOVDegPanel;
	private JComboBox<ApertureSizeType> cameraApertureSizeComboBox;
	private LabelledDoublePanel cameraFocussingDistancePanel;
	private LabelledIntPanel cameraMaxTraceLevelPanel;

	

	
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
		
		

		//
		// the net panel
		//
		
		JPanel netPanel = new JPanel();
		netPanel.setLayout(new MigLayout("insets 0"));
		
		centroidPanel = new LabelledVector3DPanel("Centroid");
		centroidPanel.setVector3D(net.getCentroid());
		netPanel.add(centroidPanel, "wrap");
		
		edgeLengthPanel = new LabelledDoublePanel("Edge length of central tetrahedron");
		edgeLengthPanel.setNumber(net.getEdgeLength());
		netPanel.add(edgeLengthPanel, "wrap");
		
		outerTetrahedronHeightFactorPanel = new LabelledDoublePanel("(Height of outer tetrahedra)/(height of central tetrahedron)");
		outerTetrahedronHeightFactorPanel.setNumber(net.getOuterTetrahedronHeightFactor());
		netPanel.add(outerTetrahedronHeightFactorPanel, "wrap");


		normal2enclosingCube1Panel = new LabelledVector3DPanel("Normal to face 1 of enclosing cube");
		normal2enclosingCube1Panel.setVector3D(net.getNormal2enclosingCube1());
		netPanel.add(normal2enclosingCube1Panel, "wrap");

		normal2enclosingCube2Panel = new LabelledVector3DPanel("Normal to face 2 of enclosing cube");
		normal2enclosingCube2Panel.setVector3D(net.getNormal2enclosingCube2());
		netPanel.add(normal2enclosingCube2Panel, "wrap");
		
		normal2enclosingCube3Panel = new LabelledVector3DPanel("Normal to face 3 of enclosing cube");
		normal2enclosingCube3Panel.setVector3D(net.getNormal2enclosingCube3());
		netPanel.add(normal2enclosingCube3Panel, "wrap");
		
		showNullSpaceWedgesCheckBox = new JCheckBox("Show null-space wedges");
		showNullSpaceWedgesCheckBox.setSelected(showNullSpaceWedges);
		showNullSpaceWedgesCheckBox.addActionListener(this);
		netPanel.add(showNullSpaceWedgesCheckBox, "wrap");
		
		setShowOnlyNullSpaceWedgeNoPanel = new LabelledIntPanel("Show only null-space wedge (-1 = all) #");	// TODO for debugging
		setShowOnlyNullSpaceWedgeNoPanel.setNumber(setShowOnlyNullSpaceWedgeNo);
		netPanel.add(setShowOnlyNullSpaceWedgeNoPanel, "wrap");
		
		nullSpaceWedgeLegLengthFactorPanel = new LabelledDoublePanel("Leg length factor");
		nullSpaceWedgeLegLengthFactorPanel.setNumber(net.getNullSpaceWedgeLegLengthFactor());
		netPanel.add(nullSpaceWedgeLegLengthFactorPanel, "wrap");
		
//		gluingTypeComboBox = new JComboBox<GluingType>(GluingType.values());
//		gluingTypeComboBox.setSelectedItem(net.getGluingType());
//		gluingTypeComboBox.addActionListener(this);
//		// netPanel.add(gluingTypeComboBox, "wrap");
//		netPanel.add(GUIBitsAndBobs.makeRow("Null-space-wedge type", gluingTypeComboBox), "wrap");
		
		curvedSpaceSimulationTypeComboBox = new JComboBox<GluingType>(GluingType.values());
		curvedSpaceSimulationTypeComboBox.setSelectedItem(curvedSpaceSimulationType);
		curvedSpaceSimulationTypeComboBox.addActionListener(this);
		netPanel.add(GUIBitsAndBobs.makeRow("Simulation type", curvedSpaceSimulationTypeComboBox), "wrap");
		
		nullSpaceWedgeSurfaceTransmissionCoefficientPanel = new LabelledDoublePanel("Transmission coefficient of null-space-wedge surfaces");
		nullSpaceWedgeSurfaceTransmissionCoefficientPanel.setNumber(net.getNullSpaceWedgeSurfaceTransmissionCoefficient());
		netPanel.add(nullSpaceWedgeSurfaceTransmissionCoefficientPanel, "wrap");
		
		numberOfNegativeSpaceWedgesPanel = new LabelledIntPanel("Number of negative-space wedges per null-space wedge");
		numberOfNegativeSpaceWedgesPanel.setNumber(net.getNumberOfNegativeSpaceWedges());
		netPanel.add(numberOfNegativeSpaceWedgesPanel, "wrap");
		
		
		JPanel structureVisualisationPanel = new JPanel();
		structureVisualisationPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Show structure"));
		structureVisualisationPanel.setLayout(new MigLayout("insets 0"));
		
		showNetEdgesCheckBox = new JCheckBox("Show edges of net of 4-simplex");
		showNetEdgesCheckBox.setSelected(showNetEdges);
		showNetEdgesCheckBox.addActionListener(this);
		structureVisualisationPanel.add(showNetEdgesCheckBox, "wrap");

		showNetFacesCheckBox = new JCheckBox("Show faces of net of 4-simplex");
		showNetFacesCheckBox.setSelected(showNetFaces);
		showNetFacesCheckBox.addActionListener(this);
		structureVisualisationPanel.add(showNetFacesCheckBox, "wrap");

		showNullSpaceWedgesEdgesCheckBox = new JCheckBox("Show edges of null-space wedges");
		showNullSpaceWedgesEdgesCheckBox.setSelected(showNullSpaceWedgeEdges);
		showNullSpaceWedgesEdgesCheckBox.addActionListener(this);
		structureVisualisationPanel.add(showNullSpaceWedgesEdgesCheckBox, "wrap");

		edgeRadiusPanel = new LabelledDoublePanel("Tube radius");
		edgeRadiusPanel.setNumber(net.getEdgeRadius());
		structureVisualisationPanel.add(edgeRadiusPanel, "wrap");
		
		netPanel.add(structureVisualisationPanel, "wrap");

		tabbedPane.addTab("Net", netPanel);
		
		
		//
		// stuff panel
		//
		
		JPanel stuffPanel = new JPanel();
		stuffPanel.setLayout(new MigLayout("insets 0"));
		
		studioInitialisationComboBox = new JComboBox<StudioInitialisationType>(StudioInitialisationType.limitedValuesForBackgrounds);
		studioInitialisationComboBox.setSelectedItem(studioInitialisation);
		stuffPanel.add(GUIBitsAndBobs.makeRow("Initialise backdrop to", studioInitialisationComboBox), "span");
		
		JPanel redSpherePanel = new JPanel();
		redSpherePanel.setBorder(GUIBitsAndBobs.getTitledBorder("White sphere"));
		redSpherePanel.setLayout(new MigLayout("insets 0"));

		showSphereCheckBox = new JCheckBox("Show");
		showSphereCheckBox.setSelected(showSphere);
		showSphereCheckBox.addActionListener(this);
		redSpherePanel.add(showSphereCheckBox, "wrap");
		
		sphereCentrePanel = new LabelledVector3DPanel("Centre");
		sphereCentrePanel.setVector3D(sphereCentre);
		redSpherePanel.add(sphereCentrePanel, "wrap");
		
		sphereRadiusPanel = new LabelledDoublePanel("Radius");
		sphereRadiusPanel.setNumber(sphereRadius);
		redSpherePanel.add(sphereRadiusPanel, "wrap");
		
		stuffPanel.add(redSpherePanel, "wrap");
		
		tabbedPane.addTab("Stuff", stuffPanel);

		
		//
		// trajectory panel
		//
		
		JPanel trajectoryPanel = new JPanel();
		trajectoryPanel.setLayout(new MigLayout("insets 0"));
		
		showTrajectoryCheckBox = new JCheckBox("Show trajectory");
		showTrajectoryCheckBox.setSelected(showTrajectory);
		showTrajectoryCheckBox.addActionListener(this);
		trajectoryPanel.add(showTrajectoryCheckBox, "wrap");

		trajectoryStartPointPanel = new LabelledVector3DPanel("Start point");
		trajectoryStartPointPanel.setVector3D(trajectoryStartPoint);
		trajectoryPanel.add(trajectoryStartPointPanel, "span");

		trajectoryStartDirectionPanel = new LabelledVector3DPanel("Initial direction");
		trajectoryStartDirectionPanel.setVector3D(trajectoryStartDirection);
		trajectoryPanel.add(trajectoryStartDirectionPanel, "span");
		
		trajectoryRadiusPanel = new LabelledDoublePanel("Radius");
		trajectoryRadiusPanel.setNumber(trajectoryRadius);
		trajectoryPanel.add(trajectoryRadiusPanel, "span");
		
		trajectoryMaxTraceLevelPanel = new LabelledIntPanel("Max. trace level");
		trajectoryMaxTraceLevelPanel.setNumber(trajectoryMaxTraceLevel);
		trajectoryPanel.add(trajectoryMaxTraceLevelPanel, "span");

		tabbedPane.addTab("Trajectory", trajectoryPanel);
		
		
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
		
		showOrHideControlPanels();
	}
	
	/**
	 * called before rendering;
	 * override when adding fields
	 */
	@Override
	protected void acceptValuesInInteractiveControlPanel()
	{
		super.acceptValuesInInteractiveControlPanel();
		
		net.setCentroid(centroidPanel.getVector3D());
		net.setEdgeLength(edgeLengthPanel.getNumber());
		net.setOuterTetrahedronHeightFactor(outerTetrahedronHeightFactorPanel.getNumber());
		net.setDirections(normal2enclosingCube1Panel.getVector3D(), normal2enclosingCube2Panel.getVector3D(), normal2enclosingCube3Panel.getVector3D());
		showNullSpaceWedges = showNullSpaceWedgesCheckBox.isSelected();
		net.setNullSpaceWedgeLegLengthFactor(nullSpaceWedgeLegLengthFactorPanel.getNumber());
		net.setNullSpaceWedgeSurfaceTransmissionCoefficient(nullSpaceWedgeSurfaceTransmissionCoefficientPanel.getNumber());
		net.setNumberOfNegativeSpaceWedges(numberOfNegativeSpaceWedgesPanel.getNumber());
		// net.setGluingType((GluingType)gluingTypeComboBox.getSelectedItem());
		curvedSpaceSimulationType = (GluingType)curvedSpaceSimulationTypeComboBox.getSelectedItem();
		net.setEdgeRadius(edgeRadiusPanel.getNumber());
		showNetEdges = showNetEdgesCheckBox.isSelected();
		showNetFaces = showNetFacesCheckBox.isSelected();
		showNullSpaceWedgeEdges = showNullSpaceWedgesEdgesCheckBox.isSelected();
 		setShowOnlyNullSpaceWedgeNo = setShowOnlyNullSpaceWedgeNoPanel.getNumber();
		
		studioInitialisation = (StudioInitialisationType)(studioInitialisationComboBox.getSelectedItem());
		showSphere = showSphereCheckBox.isSelected();
		sphereCentre = sphereCentrePanel.getVector3D();
		sphereRadius = sphereRadiusPanel.getNumber();
		
		showTrajectory = showTrajectoryCheckBox.isSelected();
		trajectoryStartPoint = trajectoryStartPointPanel.getVector3D();
		trajectoryStartDirection = trajectoryStartDirectionPanel.getVector3D();
		trajectoryRadius = trajectoryRadiusPanel.getNumber();
		trajectoryMaxTraceLevel = trajectoryMaxTraceLevelPanel.getNumber();
		
		cameraViewCentre = cameraViewCentrePanel.getVector3D();
		cameraViewDirection = Vector3D.difference(cameraViewCentre, cameraPositionPanel.getVector3D());
		cameraDistance = cameraViewDirection.getLength();
		cameraHorizontalFOVDeg = cameraHorizontalFOVDegPanel.getNumber();
		cameraApertureSize = (ApertureSizeType)(cameraApertureSizeComboBox.getSelectedItem());
		cameraFocussingDistance = cameraFocussingDistancePanel.getNumber();
		cameraMaxTraceLevel = cameraMaxTraceLevelPanel.getNumber();
	}
	
	private void showOrHideControlPanels()
	{
		showNullSpaceWedgesCheckBox.setEnabled(curvedSpaceSimulationType != GluingType.MIRROR_APPROXIMATION);
		nullSpaceWedgeLegLengthFactorPanel.setEnabled((curvedSpaceSimulationType == GluingType.NEGATIVE_SPACE_WEDGES) && (showNullSpaceWedges || showNullSpaceWedgeEdges));
		nullSpaceWedgeSurfaceTransmissionCoefficientPanel.setEnabled(showNullSpaceWedges);
		numberOfNegativeSpaceWedgesPanel.setEnabled((curvedSpaceSimulationType != GluingType.MIRROR_APPROXIMATION) && (showNullSpaceWedges || showNullSpaceWedgeEdges));
		// gluingTypeComboBox.setEnabled(showNullSpaceWedges || showNullSpaceWedgeEdges);
		edgeRadiusPanel.setEnabled(showNullSpaceWedgeEdges || showNetEdges);
		
		sphereCentrePanel.setEnabled(showSphere);
		sphereRadiusPanel.setEnabled(showSphere);

		trajectoryStartPointPanel.setEnabled(showTrajectory);
		trajectoryStartDirectionPanel.setEnabled(showTrajectory);
		trajectoryRadiusPanel.setEnabled(showTrajectory);
		trajectoryMaxTraceLevelPanel.setEnabled(showTrajectory);
	
		cameraFocussingDistancePanel.setEnabled(cameraApertureSize != ApertureSizeType.PINHOLE);
	}
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		super.actionPerformed(e);
		
		if(
				e.getSource().equals(showNullSpaceWedgesCheckBox) ||
				e.getSource().equals(showNetEdgesCheckBox) ||
				e.getSource().equals(showNetFacesCheckBox) ||
				e.getSource().equals(showNullSpaceWedgesEdgesCheckBox) ||
				e.getSource().equals(showSphereCheckBox) ||
				e.getSource().equals(showTrajectoryCheckBox) ||
				e.getSource().equals(cameraApertureSizeComboBox)
			)
		{
			acceptValuesInInteractiveControlPanel();
			showOrHideControlPanels();
		}
//		else if(e.getSource().equals(gluingTypeComboBox))
//		{
//			acceptValuesInInteractiveControlPanel();
//			showOrHideControlPanels();
//		}
		if(e.getSource().equals(curvedSpaceSimulationTypeComboBox))
		{
			curvedSpaceSimulationType = (GluingType)(curvedSpaceSimulationTypeComboBox.getSelectedItem());
			showOrHideControlPanels();
		}
	}

	public static void main(final String[] args)
	{
		(new NetOfSymmetric4SimplexVisualiser()).run();
	}
}
