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
	 * show the space-cancelling wedges
	 */
	private boolean showSpaceCancellingWedges;

	/**
	 * show edges of the simplicial complex that forms the net of the 4-simplex
	 */
	private boolean showNetEdges;

	/**
	 * show faces of the simplicial complex that forms the net of the 4-simplex
	 */
	private boolean showNetFaces;

	/**
	 * show edges of the space-cancelling wedges, i.e. the vertices and edges of the sheets forming the space-cancelling wedges
	 */
	private boolean showSpaceCancellingWedgeEdges;

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
//	 * for debugging; if this variable takes a positive value, show only space-cancelling wedge #showOnlySpaceCancellingWedgeNo
//	 */
	private int setShowOnlySpaceCancellingWedgeNo;	// TODO for debugging
	

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

		showSpaceCancellingWedges = false;
		showNetEdges = true;
		showNetFaces = true;
		showSpaceCancellingWedgeEdges = false;
 		setShowOnlySpaceCancellingWedgeNo =  -1;	// TODO for debugging
		
		// the parameters for the net of the symmetric 4-simplex are held in the EditableNetOfRegular4Simplex net; create this and set the parameters
		net = new EditableNetOfSymmetric4Simplex(
				"Net of symmetric 4-simplex",
				new Vector3D(0, 0, 0),	// centroid
				1,	// edgeLength,
				1,	// outerTetrahedronHeightFactor
				new Vector3D(1, 0, 0),	// normal to face 1 of enclosing cube
				new Vector3D(0, 1, 0),	// normal to face 1 of enclosing cube
				new Vector3D(0, 0, 1),	// normal to face 1 of enclosing cube
				showSpaceCancellingWedges,	// showSpaceCancellingWedges
				1,	// spaceCancellingWedgeLegLengthFactor
				1,	// refractingSurfaceTransmissionCoefficient
				curvedSpaceSimulationType,	// gluingType
				1,	// numberOfNegativeSpaceWedges
				showNetEdges,	// showNetStructure
				showNetFaces,
				showSpaceCancellingWedgeEdges,	// showSpaceCancellingWedgesStructure
				SurfaceColour.GREY20_SHINY,	// netStructureSurfaceProperty
				ColourFilter.LIGHT_CYAN_GLASS,	// netFaceSurfaceProperty
				SurfaceColour.RED_SHINY,	// spaceCancellingWedgesStructureSurfaceProperty
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
		printStream.println("Net");
		printStream.println();

		printStream.println("Centroid = "+net.getCentroid());
		printStream.println("Edge length of central tetrahedron = "+net.getEdgeLength());
		printStream.println("(Height of outer tetrahedra)/(height of central tetrahedron) = "+net.getOuterTetrahedronHeightFactor());
		printStream.println("Normal to face 1 of enclosing cube = "+net.getNormal2enclosingCube1());
		printStream.println("Normal to face 2 of enclosing cube = "+net.getNormal2enclosingCube2());
		printStream.println("Normal to face 3 of enclosing cube = "+net.getNormal2enclosingCube3());
		printStream.println("Show space-cancelling wedges = "+showSpaceCancellingWedges);
		printStream.println("Leg length factor = "+net.getSpaceCancellingWedgeLegLengthFactor());
		printStream.println("Simulation type = "+curvedSpaceSimulationType);
		printStream.println("Transmission coefficient of space-cancelling-wedge surfaces = "+net.getSpaceCancellingWedgeSurfaceTransmissionCoefficient());
		printStream.println("Number of space-cancelling wedges in fan of space-cancelling wedge = "+net.getNumberOfNegativeSpaceWedges());
		printStream.println("Show structure > Show edges of net of 4-simplex = "+showNetEdges);
		printStream.println("Show structure > Show faces of net of 4-simplex = "+showNetFaces);
		printStream.println("Show structure > Show edges of space-cancelling wedges = "+showSpaceCancellingWedgeEdges);
		printStream.println("Show structure > Tube radius = "+net.getEdgeRadius());
		
		printStream.println();
		printStream.println("Stuff");
		printStream.println();

		printStream.println("Initialise backdrop to = "+studioInitialisation);
		printStream.println("White sphere > Show = "+showSphere);
		printStream.println("White sphere > Centre = "+sphereCentre);
		printStream.println("White sphere > Radius = "+sphereRadius);
		
		printStream.println();
		printStream.println("Trajectory");
		printStream.println();

		
		printStream.println("Show trajectory = "+showTrajectory);
		printStream.println("Start point = "+trajectoryStartPoint);
		printStream.println("Initial direction = "+trajectoryStartDirection);
		printStream.println("Radius = "+trajectoryRadius);
		printStream.println("Max. trace level = "+trajectoryMaxTraceLevel);
		
		printStream.println();
		printStream.println("Camera");
		printStream.println();

		printStream.println("Aperture centre = "+Vector3D.sum(cameraViewCentre, cameraViewDirection.getWithLength(-cameraDistance)));
		printStream.println("Centre of view = "+cameraViewCentre);
		printStream.println("Horizontal FOV = "+cameraHorizontalFOVDeg+"°");
		printStream.println("Aperture size = "+cameraApertureSize);
		printStream.println("Focussing distance = "+cameraFocussingDistance);
		printStream.println("Max. trace level = "+cameraMaxTraceLevel);


		// write all parameters defined in NonInteractiveTIMEngine
		// super.writeParameters(printStream);		
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
		net.setShowSpaceCancellingWedgeEdges(false);
 		net.setShowOnlySpaceCancellingWedgeNo(setShowOnlySpaceCancellingWedgeNo);
		// ... and get it prepared for light-ray-trajectory tracing
		switch(curvedSpaceSimulationType)
		{
		case NEGATIVE_SPACE_WEDGES:
		case NEGATIVE_SPACE_WEDGES_SYMMETRIC:
		case NEGATIVE_SPACE_WEDGES_WITH_CONTAINMENT_MIRRORS:
		case PERFECT:
			// showing the space-cancelling wedges but hiding all edges
			net.setShowSpaceCancellingWedges(true);
			break;
		case MIRROR_APPROXIMATION:
			net.setShowSpaceCancellingWedges(false);
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
			net.setShowSpaceCancellingWedges(showSpaceCancellingWedges);
			net.setShowSpaceCancellingWedgeEdges(showSpaceCancellingWedgeEdges);
			break;
		case MIRROR_APPROXIMATION:
			net.setShowSpaceCancellingWedges(false);
			net.setShowSpaceCancellingWedgeEdges(false);
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
	private LabelledDoublePanel edgeLengthPanel, outerTetrahedronHeightFactorPanel, edgeRadiusPanel, spaceCancellingWedgeLegLengthFactorPanel, spaceCancellingWedgeSurfaceTransmissionCoefficientPanel;
	private LabelledIntPanel numberOfNegativeSpaceWedgesPanel;	// , setShowOnlySpaceCancellingWedgeNoPanel;	// TODO for debugging
//	private JComboBox<GluingType> gluingTypeComboBox;
	private JComboBox<GluingType> curvedSpaceSimulationTypeComboBox;
	private JCheckBox showSpaceCancellingWedgesCheckBox, showNetEdgesCheckBox, showNetFacesCheckBox, showSpaceCancellingWedgesEdgesCheckBox;
	
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
		
		showSpaceCancellingWedgesCheckBox = new JCheckBox("Show space-cancelling wedges");
		showSpaceCancellingWedgesCheckBox.setSelected(showSpaceCancellingWedges);
		showSpaceCancellingWedgesCheckBox.addActionListener(this);
		netPanel.add(showSpaceCancellingWedgesCheckBox, "wrap");
		
//		setShowOnlySpaceCancellingWedgeNoPanel = new LabelledIntPanel("Show only space-cancelling wedge (-1 = all) #");	// TODO for debugging
//		setShowOnlySpaceCancellingWedgeNoPanel.setNumber(setShowOnlySpaceCancellingWedgeNo);
//		netPanel.add(setShowOnlySpaceCancellingWedgeNoPanel, "wrap");
		
		spaceCancellingWedgeLegLengthFactorPanel = new LabelledDoublePanel("Leg length factor");
		spaceCancellingWedgeLegLengthFactorPanel.setNumber(net.getSpaceCancellingWedgeLegLengthFactor());
		netPanel.add(spaceCancellingWedgeLegLengthFactorPanel, "wrap");
		
//		gluingTypeComboBox = new JComboBox<GluingType>(GluingType.values());
//		gluingTypeComboBox.setSelectedItem(net.getGluingType());
//		gluingTypeComboBox.addActionListener(this);
//		// netPanel.add(gluingTypeComboBox, "wrap");
//		netPanel.add(GUIBitsAndBobs.makeRow("Null-space-wedge type", gluingTypeComboBox), "wrap");
		
		curvedSpaceSimulationTypeComboBox = new JComboBox<GluingType>(GluingType.values());
		curvedSpaceSimulationTypeComboBox.setSelectedItem(curvedSpaceSimulationType);
		curvedSpaceSimulationTypeComboBox.addActionListener(this);
		netPanel.add(GUIBitsAndBobs.makeRow("Simulation type", curvedSpaceSimulationTypeComboBox), "wrap");
		
		spaceCancellingWedgeSurfaceTransmissionCoefficientPanel = new LabelledDoublePanel("Transmission coefficient of space-cancelling-wedge surfaces");
		spaceCancellingWedgeSurfaceTransmissionCoefficientPanel.setNumber(net.getSpaceCancellingWedgeSurfaceTransmissionCoefficient());
		netPanel.add(spaceCancellingWedgeSurfaceTransmissionCoefficientPanel, "wrap");
		
		numberOfNegativeSpaceWedgesPanel = new LabelledIntPanel("Number of space-cancelling wedges in fan of space-cancelling wedge");
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

		showSpaceCancellingWedgesEdgesCheckBox = new JCheckBox("Show edges of space-cancelling wedges");
		showSpaceCancellingWedgesEdgesCheckBox.setSelected(showSpaceCancellingWedgeEdges);
		showSpaceCancellingWedgesEdgesCheckBox.addActionListener(this);
		structureVisualisationPanel.add(showSpaceCancellingWedgesEdgesCheckBox, "wrap");

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
		showSpaceCancellingWedges = showSpaceCancellingWedgesCheckBox.isSelected();
		net.setSpaceCancellingWedgeLegLengthFactor(spaceCancellingWedgeLegLengthFactorPanel.getNumber());
		net.setSpaceCancellingWedgeSurfaceTransmissionCoefficient(spaceCancellingWedgeSurfaceTransmissionCoefficientPanel.getNumber());
		net.setNumberOfNegativeSpaceWedges(numberOfNegativeSpaceWedgesPanel.getNumber());
		// net.setGluingType((GluingType)gluingTypeComboBox.getSelectedItem());
		curvedSpaceSimulationType = (GluingType)curvedSpaceSimulationTypeComboBox.getSelectedItem();
		net.setEdgeRadius(edgeRadiusPanel.getNumber());
		showNetEdges = showNetEdgesCheckBox.isSelected();
		showNetFaces = showNetFacesCheckBox.isSelected();
		showSpaceCancellingWedgeEdges = showSpaceCancellingWedgesEdgesCheckBox.isSelected();
 		// setShowOnlySpaceCancellingWedgeNo = setShowOnlySpaceCancellingWedgeNoPanel.getNumber();
		
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
		showSpaceCancellingWedgesCheckBox.setEnabled(curvedSpaceSimulationType != GluingType.MIRROR_APPROXIMATION);
		spaceCancellingWedgeLegLengthFactorPanel.setEnabled((curvedSpaceSimulationType == GluingType.NEGATIVE_SPACE_WEDGES) && (showSpaceCancellingWedges || showSpaceCancellingWedgeEdges));
		spaceCancellingWedgeSurfaceTransmissionCoefficientPanel.setEnabled(showSpaceCancellingWedges);
		numberOfNegativeSpaceWedgesPanel.setEnabled((curvedSpaceSimulationType != GluingType.MIRROR_APPROXIMATION) && (showSpaceCancellingWedges || showSpaceCancellingWedgeEdges));
		// gluingTypeComboBox.setEnabled(showSpaceCancellingWedges || showSpaceCancellingWedgeEdges);
		edgeRadiusPanel.setEnabled(showSpaceCancellingWedgeEdges || showNetEdges);
		
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
				e.getSource().equals(showSpaceCancellingWedgesCheckBox) ||
				e.getSource().equals(showNetEdgesCheckBox) ||
				e.getSource().equals(showNetFacesCheckBox) ||
				e.getSource().equals(showSpaceCancellingWedgesEdgesCheckBox) ||
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
