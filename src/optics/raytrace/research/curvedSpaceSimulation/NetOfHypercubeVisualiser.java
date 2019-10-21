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
import optics.raytrace.GUI.sceneObjects.EditableNetOfHypercube;
import optics.raytrace.GUI.sceneObjects.EditableRayTrajectory;
import optics.raytrace.GUI.sceneObjects.EditableScaledParametrisedSphere;
import optics.raytrace.core.Studio;
import optics.raytrace.core.StudioInitialisationType;


/**
 * Plots a number of trajectories through the net of a hypercube.
 * 
 * @author Johannes Courtial 
 */
public class NetOfHypercubeVisualiser extends NonInteractiveTIMEngine
{
	// most additional parameters are stored in the following EditableNetOfHypercube
	private EditableNetOfHypercube net;
	
	/**
	 * show the space-cancelling wedges
	 */
	private boolean showSpaceCancellingWedges;

	/**
	 * show edges of the net of the hypercube
	 */
	private boolean showNetEdges;
	
	/**
	 * show edges of the space-cancelling wedges, i.e. the vertices and edges of the sheets forming the space-cancelling wedges
	 */
	private boolean showSpaceCancellingWedgeEdges;

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
	

	/**
	 * Constructor.
	 * Sets all parameters.
	 */
	public NetOfHypercubeVisualiser()
	{
		super();
		
		// set to true for interactive version
		nonInteractiveTIMAction = NonInteractiveTIMActionEnum.INTERACTIVE;

		// set all parameters
		
		// the parameters for the net of the hypercube are held in the EditableNetOfHypercube net; create this and set the parameters
		net = new EditableNetOfHypercube(
				"Net of hypercube",
				new Vector3D(0, 0, 0),	// centre
				new Vector3D(1, 0, 0),	// rightDirection,
				new Vector3D(0, 1, 0),	// upDirection
				1,	// sideLength,
				false,	// showSpaceCancellingWedges
				1,	// spaceCancellingWedgeLegLengthFactor
				0.96,	// refractingSurfaceTransmissionCoefficient
				GluingType.PERFECT,	// gluingType
				1,	// numberOfNegativeSpaceWedges
				true,	// showNetStructure
				false,	// showSpaceCancellingWedgesStructure
				SurfaceColour.GREY20_SHINY,	// netStructureSurfaceProperty
				ColourFilter.LIGHT_CYAN_GLASS,	// netFaceSurfaceProperty
				SurfaceColour.RED_SHINY,	// spaceCancellingWedgesStructureSurfaceProperty
				0.002,	// structureTubeRadius
				null,	// parent
				null	// studio
			);
		
		showSpaceCancellingWedges = false;
		showNetEdges = true;
		showSpaceCancellingWedgeEdges = false;
		
		// stuff
		studioInitialisation = StudioInitialisationType.HEAVEN;	// the backdrop
		showSphere = false;
		sphereCentre = new Vector3D(0.2, 0.31415, 0.25);
		sphereRadius = 0.1;

		// trajectory
		showTrajectory = false;
		trajectoryStartPoint = new Vector3D(0, 0, 0);
		trajectoryStartDirection = new Vector3D(.1, 1, 0);
		trajectoryRadius = 0.001;
		trajectoryMaxTraceLevel = 100;
		
		// camera
		cameraViewDirection = new Vector3D(2, -3.4, 7);
		cameraViewCentre = new Vector3D(0, 0.1, 0);
		cameraDistance = cameraViewDirection.getLength();
		cameraFocussingDistance = cameraDistance;
		cameraHorizontalFOVDeg = 40;
		cameraMaxTraceLevel = 1000;
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
		windowTitle = "Dr TIM's net-of-hypercube visualiser";
		windowWidth = 1300;
		windowHeight = 650;
	}

	@Override
	public String getFirstPartOfFilename()
	{
		return "NetOfHypercubeVisualiser"	// the name
				;
	}
	
	@Override
	public void writeParameters(PrintStream printStream)
	{
		// write any parameters not defined in NonInteractiveTIMEngine
		
		printStream.println("Net");
		printStream.println();

		printStream.println("Centre = "+net.getCentre());
		printStream.println("Edge length of central cube = "+net.getSideLength());
		printStream.println("Rightwards direction = "+net.getRightDirection());
		printStream.println("Upwards direction = "+net.getUpDirection());
		printStream.println("Show space-cancelling wedges = "+showSpaceCancellingWedges);
		printStream.println("Leg length factor = "+net.getSpaceCancellingWedgeLegLengthFactor());
		printStream.println("Simulation type = "+net.getGluingType());
		printStream.println("Transmission coefficient of space-cancelling-wedge surfaces = "+net.getSpaceCancellingWedgeSurfaceTransmissionCoefficient());
		printStream.println("Number of space-cancelling wedges in fan of space-cancelling wedge = "+net.getNumberOfNegativeSpaceWedges());
		printStream.println("Show structure > Show edges and faces of net of hypercube = "+showNetEdges);
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
	
	/* (non-Javadoc)
	 * @see optics.raytrace.NonInteractiveTIMEngine#populateStudio()
	 */
	@Override
	public void populateStudio()
	throws SceneException
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

//		
//		// the scene
//		SceneObjectContainer scene = new SceneObjectContainer("the scene", null, studio);
//
//		// the standard scene objects
//		scene.addSceneObject(SceneObjectClass.getLighterChequerboardFloor(-3, scene, studio));	// the checkerboard floor
//		scene.addSceneObject(SceneObjectClass.getSkySphere(scene, studio));	// the sky
//
//		studio.setScene(scene);
//		studio.setLights(LightSource.getStandardLightsFromBehind());
		studio.setCamera(getStandardCamera());

		// the net: set its parent to <i>scene</i>...
		net.setParent(scene);
		// ... and get it prepared for light-ray-trajectory tracing, i.e. showing the space-cancelling wedges but hiding all edges
		net.setShowSpaceCancellingWedges(true);
		net.setShowNetEdges(false);
		net.setShowSpaceCancellingWedgeEdges(false);

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
		net.setShowSpaceCancellingWedges(showSpaceCancellingWedges);
		net.setShowNetEdges(showNetEdges);
		net.setShowSpaceCancellingWedgeEdges(showSpaceCancellingWedgeEdges);

		// ... and add the scene objects to the net
		try {
			net.populateSceneObjectCollection();
		} catch (InconsistencyException e) {
			e.printStackTrace();
		}

	}
	
	
	//
	// for interactive version
	//
	
	// GUI panels
	private LabelledVector3DPanel centrePanel, rightDirectionPanel, upDirectionPanel;
	private LabelledDoublePanel sideLengthPanel, edgeRadiusPanel, spaceCancellingWedgeLegLengthFactorPanel, spaceCancellingWedgeSurfaceTransmissionCoefficientPanel;
	private LabelledIntPanel numberOfNegativeSpaceWedgesPanel;
	private JComboBox<GluingType> gluingTypeComboBox;
	private JCheckBox showSpaceCancellingWedgesCheckBox, showNetEdgesCheckBox, showSpaceCancellingWedgesEdgesCheckBox;
	
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

	// camera
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
		
		centrePanel = new LabelledVector3DPanel("Centre");
		centrePanel.setVector3D(net.getCentre());
		netPanel.add(centrePanel, "wrap");
		
		sideLengthPanel = new LabelledDoublePanel("Side length of central cube");
		sideLengthPanel.setNumber(net.getSideLength());
		netPanel.add(sideLengthPanel, "wrap");
		
		rightDirectionPanel = new LabelledVector3DPanel("Rightwards direction");
		rightDirectionPanel.setVector3D(net.getRightDirection());
		netPanel.add(rightDirectionPanel, "wrap");

		upDirectionPanel = new LabelledVector3DPanel("Upwards direction");
		upDirectionPanel.setVector3D(net.getUpDirection());
		netPanel.add(upDirectionPanel, "wrap");
		
		showSpaceCancellingWedgesCheckBox = new JCheckBox("Show space-cancelling wedges");
		showSpaceCancellingWedgesCheckBox.setSelected(showSpaceCancellingWedges);
		netPanel.add(showSpaceCancellingWedgesCheckBox, "wrap");
		
		spaceCancellingWedgeLegLengthFactorPanel = new LabelledDoublePanel("Leg length factor");
		spaceCancellingWedgeLegLengthFactorPanel.setNumber(net.getSpaceCancellingWedgeLegLengthFactor());
		netPanel.add(spaceCancellingWedgeLegLengthFactorPanel, "wrap");
		
		gluingTypeComboBox = new JComboBox<GluingType>(GluingType.values());
		gluingTypeComboBox.setSelectedItem(net.getGluingType());
		gluingTypeComboBox.addActionListener(this);
		netPanel.add(GUIBitsAndBobs.makeRow("Simulation type", gluingTypeComboBox), "wrap");
		
		spaceCancellingWedgeSurfaceTransmissionCoefficientPanel = new LabelledDoublePanel("Transmission coefficient of space-cancelling-wedge surfaces");
		spaceCancellingWedgeSurfaceTransmissionCoefficientPanel.setNumber(net.getSpaceCancellingWedgeSurfaceTransmissionCoefficient());
		netPanel.add(spaceCancellingWedgeSurfaceTransmissionCoefficientPanel, "wrap");
		
		numberOfNegativeSpaceWedgesPanel = new LabelledIntPanel("Number of space-cancelling wedges in fan of space-cancelling wedge");
		numberOfNegativeSpaceWedgesPanel.setNumber(net.getNumberOfNegativeSpaceWedges());
		netPanel.add(numberOfNegativeSpaceWedgesPanel, "wrap");
		
		
		JPanel structureVisualisationPanel = new JPanel();
		structureVisualisationPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Show structure"));
		structureVisualisationPanel.setLayout(new MigLayout("insets 0"));
		
		showNetEdgesCheckBox = new JCheckBox("Show edges and faces of net of hypercube");
		showNetEdgesCheckBox.setSelected(showNetEdges);
		structureVisualisationPanel.add(showNetEdgesCheckBox, "wrap");

		showSpaceCancellingWedgesEdgesCheckBox = new JCheckBox("Show edges of space-cancelling wedges");
		showSpaceCancellingWedgesEdgesCheckBox.setSelected(showSpaceCancellingWedgeEdges);
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

		StudioInitialisationType[] limitedValuesForBackgrounds = {StudioInitialisationType.HEAVEN, StudioInitialisationType.MINIMALIST_LOWER_FLOOR};
		
		studioInitialisationComboBox = new JComboBox<StudioInitialisationType>(limitedValuesForBackgrounds);
		studioInitialisationComboBox.setSelectedItem(studioInitialisation);
		stuffPanel.add(GUIBitsAndBobs.makeRow("Initialise backdrop to", studioInitialisationComboBox), "span");

		JPanel redSpherePanel = new JPanel();
		redSpherePanel.setBorder(GUIBitsAndBobs.getTitledBorder("White sphere"));
		redSpherePanel.setLayout(new MigLayout("insets 0"));

		showSphereCheckBox = new JCheckBox("Show");
		showSphereCheckBox.setSelected(showSphere);
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
		cameraPanel.add(GUIBitsAndBobs.makeRow("Aperture size", cameraApertureSizeComboBox), "span");		
		
		cameraFocussingDistancePanel = new LabelledDoublePanel("Focussing distance");
		cameraFocussingDistancePanel.setNumber(cameraFocussingDistance);
		cameraPanel.add(cameraFocussingDistancePanel, "span");
		
		cameraMaxTraceLevelPanel = new LabelledIntPanel("Max. trace level");
		cameraMaxTraceLevelPanel.setNumber(cameraMaxTraceLevel);
		cameraPanel.add(cameraMaxTraceLevelPanel, "span");

		tabbedPane.addTab("Camera", cameraPanel);
		
		showOrHidePanels();
	}
	
	/**
	 * called before rendering;
	 * override when adding fields
	 */
	@Override
	protected void acceptValuesInInteractiveControlPanel()
	{
		super.acceptValuesInInteractiveControlPanel();
		
		net.setCentre(centrePanel.getVector3D());
		net.setSideLength(sideLengthPanel.getNumber());
		net.setRightDirection(rightDirectionPanel.getVector3D());
		net.setUpDirection(upDirectionPanel.getVector3D());
		showSpaceCancellingWedges = showSpaceCancellingWedgesCheckBox.isSelected();
		net.setSpaceCancellingWedgeLegLengthFactor(spaceCancellingWedgeLegLengthFactorPanel.getNumber());
		net.setSpaceCancellingWedgeSurfaceTransmissionCoefficient(spaceCancellingWedgeSurfaceTransmissionCoefficientPanel.getNumber());
		net.setNumberOfNegativeSpaceWedges(numberOfNegativeSpaceWedgesPanel.getNumber());
		net.setGluingType((GluingType)gluingTypeComboBox.getSelectedItem());
		net.setEdgeRadius(edgeRadiusPanel.getNumber());
		showNetEdges = showNetEdgesCheckBox.isSelected();
		showSpaceCancellingWedgeEdges = showSpaceCancellingWedgesEdgesCheckBox.isSelected();
		
		showTrajectory = showTrajectoryCheckBox.isSelected();
		trajectoryStartPoint = trajectoryStartPointPanel.getVector3D();
		trajectoryStartDirection = trajectoryStartDirectionPanel.getVector3D();
		trajectoryRadius = trajectoryRadiusPanel.getNumber();
		trajectoryMaxTraceLevel = trajectoryMaxTraceLevelPanel.getNumber();
		
		studioInitialisation = (StudioInitialisationType)(studioInitialisationComboBox.getSelectedItem());
		showSphere = showSphereCheckBox.isSelected();
		sphereCentre = sphereCentrePanel.getVector3D();
		sphereRadius = sphereRadiusPanel.getNumber();
		
		cameraViewCentre = cameraViewCentrePanel.getVector3D();
		cameraViewDirection = Vector3D.difference(cameraViewCentre, cameraPositionPanel.getVector3D());
		cameraDistance = cameraViewDirection.getLength();
		cameraHorizontalFOVDeg = cameraHorizontalFOVDegPanel.getNumber();
		cameraApertureSize = (ApertureSizeType)(cameraApertureSizeComboBox.getSelectedItem());
		cameraFocussingDistance = cameraFocussingDistancePanel.getNumber();
		cameraMaxTraceLevel = cameraMaxTraceLevelPanel.getNumber();
	}
	
	private void showOrHidePanels()
	{
		// show or hide additional parameters as appropriate
		switch(net.getGluingType())
		{
		case SPACE_CANCELLING_WEDGES:
		case SPACE_CANCELLING_WEDGES_SYMMETRIC:
		case SPACE_CANCELLING_WEDGES_WITH_CONTAINMENT_MIRRORS:
			numberOfNegativeSpaceWedgesPanel.setEnabled(true);
			break;
		case PERFECT:
		default:
			numberOfNegativeSpaceWedgesPanel.setEnabled(false);
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		super.actionPerformed(e);
		
		if(e.getSource().equals(gluingTypeComboBox))
		{
			net.setGluingType((GluingType)(gluingTypeComboBox.getSelectedItem()));
			
			showOrHidePanels();
		}
	}


	public static void main(final String[] args)
	{
		(new NetOfHypercubeVisualiser()).run();
	}
}
