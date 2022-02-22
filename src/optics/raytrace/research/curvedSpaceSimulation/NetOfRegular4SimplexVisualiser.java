package optics.raytrace.research.curvedSpaceSimulation;

import java.awt.event.ActionEvent;

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
import optics.raytrace.GUI.sceneObjects.EditableNetOfRegular4Simplex;
import optics.raytrace.GUI.sceneObjects.EditableRayTrajectory;
import optics.raytrace.GUI.sceneObjects.EditableScaledParametrisedSphere;
import optics.raytrace.core.Studio;
import optics.raytrace.core.StudioInitialisationType;


/**
 * Plots a number of trajectories through the net of a regular 4-simplex.
 * Note that a regular 4-simplex is a special case of a symmetric 4-simplex, and so this class is now deprecated.
 * 
 * @author Johannes Courtial 
 * @deprecated	Use the {@link optics.raytrace.research.curvedSpaceSimulation.NetOfSymmetric4SimplexVisualiser} class instead
 */
@Deprecated
public class NetOfRegular4SimplexVisualiser extends NonInteractiveTIMEngine
{
	// most additional parameters are stored in the following EditableNetOfRegular4Simplex
	private EditableNetOfRegular4Simplex net;
	
	/**
	 * show the space-cancelling wedges
	 */
	private boolean showSpaceCancellingWedges;

	/**
	 * show edges of the simplicial complex that forms the net of the 4-simplex
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
	public NetOfRegular4SimplexVisualiser()
	{
		super();
		
		// set to true for interactive version
		nonInteractiveTIMAction = NonInteractiveTIMActionEnum.INTERACTIVE;

		// set all parameters
		
		// the parameters for the net of the regular 4-simplex are held in the EditableNetOfRegular4Simplex net; create this and set the parameters
		net = new EditableNetOfRegular4Simplex(
				"Net of 4-simplex",
				new Vector3D(0, 0, 0),	// centroid
				1,	// edgeLength,
				new Vector3D(0, 1, 0),	// directionToVertex0, previously (0, 0, 1)
				new Vector3D(1, 0, 0),	// directionVertex1ToVertex2
				true,	// showSpaceCancellingWedges
				1,	// spaceCancellingWedgeLegLengthFactor
				0.96,	// refractingSurfaceTransmissionCoefficient
				GluingType.PERFECT,	// gluingType
				1,	// numberOfNegativeSpaceWedges
				false,	// showNetStructure
				false,	// showSpaceCancellingWedgesStructure
				SurfaceColour.BLUE_SHINY,	// netStructureSurfaceProperty
				ColourFilter.CYAN_GLASS,	// netFaceSurfaceProperty
				SurfaceColour.RED_SHINY,	// spaceCancellingWedgesStructureSurfaceProperty
				0.02,	// structureTubeRadius
				null,	// parent
				null	// studio
			);
		
		showSpaceCancellingWedges = true;
		showNetEdges = false;
		showSpaceCancellingWedgeEdges = false;
		
		// trajectory
		showTrajectory = false;
		trajectoryStartPoint = new Vector3D(0, 0, 0);
		trajectoryStartDirection = new Vector3D(.1, 1, 0);
		trajectoryRadius = 0.001;
		trajectoryMaxTraceLevel = 100;
		
		// stuff
		studioInitialisation = StudioInitialisationType.HEAVEN;	// the backdrop
		showSphere = false;
		sphereCentre = new Vector3D(0.2, 0, 0);
		sphereRadius = 0.1;

		// camera
		cameraViewDirection = new Vector3D(0, 0, 1);
		cameraViewCentre = new Vector3D(0, 0, 0);
		cameraDistance = 10;
		cameraFocussingDistance = 10;
		cameraHorizontalFOVDeg = 20;
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
		windowTitle = "Dr TIM's net-of-regular-4-simplex visualiser";
		windowWidth = 1300;
		windowHeight = 650;
	}

	@Override
	public String getFirstPartOfFilename()
	{
		return "NetOfRegular4SimplexVisualiser"	// the name
				;
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
				new SurfaceColourLightSourceIndependent(DoubleColour.GREEN, true),
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

		// the camera
		studio.setCamera(getStandardCamera());
	}
	
	
	//
	// for interactive version
	//
	
	// GUI panels
	private LabelledVector3DPanel centroidPanel, directionToVertex0Panel, directionVertex1ToVertex2Panel;
	private LabelledDoublePanel edgeLengthPanel, edgeRadiusPanel, spaceCancellingWedgeLegLengthFactorPanel, spaceCancellingWedgeSurfaceTransmissionCoefficientPanel;
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
		
		edgeLengthPanel = new LabelledDoublePanel("Edge length");
		edgeLengthPanel.setNumber(net.getEdgeLength());
		netPanel.add(edgeLengthPanel, "wrap");
		

		directionToVertex0Panel = new LabelledVector3DPanel("Direction to vertex #0");
		directionToVertex0Panel.setVector3D(net.getDirectionToVertex0());
		netPanel.add(directionToVertex0Panel, "wrap");

		directionVertex1ToVertex2Panel = new LabelledVector3DPanel("Direction vertex #1 to vertex #2");
		directionVertex1ToVertex2Panel.setVector3D(net.getDirectionVertex1ToVertex2());
		netPanel.add(directionVertex1ToVertex2Panel, "wrap");
		
		showSpaceCancellingWedgesCheckBox = new JCheckBox("Show space-cancelling wedges");
		showSpaceCancellingWedgesCheckBox.setSelected(showSpaceCancellingWedges);
		showSpaceCancellingWedgesCheckBox.addActionListener(this);
		netPanel.add(showSpaceCancellingWedgesCheckBox, "wrap");
		
		spaceCancellingWedgeLegLengthFactorPanel = new LabelledDoublePanel("Leg length factor");
		spaceCancellingWedgeLegLengthFactorPanel.setNumber(net.getSpaceCancellingWedgeLegLengthFactor());
		netPanel.add(spaceCancellingWedgeLegLengthFactorPanel, "wrap");
		
		gluingTypeComboBox = new JComboBox<GluingType>(GluingType.values());
		gluingTypeComboBox.setSelectedItem(net.getGluingType());
		gluingTypeComboBox.addActionListener(this);
		// netPanel.add(gluingTypeComboBox, "wrap");
		netPanel.add(GUIBitsAndBobs.makeRow("Null-space-wedge type", gluingTypeComboBox), "wrap");
		
		spaceCancellingWedgeSurfaceTransmissionCoefficientPanel = new LabelledDoublePanel("Transmission coefficient of space-cancelling-wedge surfaces");
		spaceCancellingWedgeSurfaceTransmissionCoefficientPanel.setNumber(net.getSpaceCancellingWedgeSurfaceTransmissionCoefficient());
		netPanel.add(spaceCancellingWedgeSurfaceTransmissionCoefficientPanel, "wrap");
		
		numberOfNegativeSpaceWedgesPanel = new LabelledIntPanel("Number of negative-space wedges per space-cancelling wedge");
		numberOfNegativeSpaceWedgesPanel.setNumber(net.getNumberOfNegativeSpaceWedges());
		netPanel.add(numberOfNegativeSpaceWedgesPanel, "wrap");
		
		
		JPanel structureVisualisationPanel = new JPanel();
		structureVisualisationPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Show structure"));
		structureVisualisationPanel.setLayout(new MigLayout("insets 0"));
		
		showNetEdgesCheckBox = new JCheckBox("Show structure (edges and faces) of net of 4-simplex");
		showNetEdgesCheckBox.setSelected(showNetEdges);
		showNetEdgesCheckBox.addActionListener(this);
		structureVisualisationPanel.add(showNetEdgesCheckBox, "wrap");

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
		net.setDirections(directionToVertex0Panel.getVector3D(), directionVertex1ToVertex2Panel.getVector3D());
		showSpaceCancellingWedges = showSpaceCancellingWedgesCheckBox.isSelected();
		net.setSpaceCancellingWedgeLegLengthFactor(spaceCancellingWedgeLegLengthFactorPanel.getNumber());
		net.setSpaceCancellingWedgeSurfaceTransmissionCoefficient(spaceCancellingWedgeSurfaceTransmissionCoefficientPanel.getNumber());
		net.setNumberOfNegativeSpaceWedges(numberOfNegativeSpaceWedgesPanel.getNumber());
		net.setGluingType((GluingType)gluingTypeComboBox.getSelectedItem());
		net.setEdgeRadius(edgeRadiusPanel.getNumber());
		showNetEdges = showNetEdgesCheckBox.isSelected();
		showSpaceCancellingWedgeEdges = showSpaceCancellingWedgesEdgesCheckBox.isSelected();
		
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
		spaceCancellingWedgeLegLengthFactorPanel.setEnabled(showSpaceCancellingWedges || showSpaceCancellingWedgeEdges);
		spaceCancellingWedgeSurfaceTransmissionCoefficientPanel.setEnabled(showSpaceCancellingWedges);
		numberOfNegativeSpaceWedgesPanel.setEnabled((showSpaceCancellingWedges || showSpaceCancellingWedgeEdges) && (net.getGluingType() == GluingType.NEGATIVE_SPACE_WEDGES));
		gluingTypeComboBox.setEnabled(showSpaceCancellingWedges || showSpaceCancellingWedgeEdges);
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
		
		if(e.getSource().equals(showSpaceCancellingWedgesCheckBox))
		{
			acceptValuesInInteractiveControlPanel();
			showOrHideControlPanels();
		}
		else if(e.getSource().equals(showNetEdgesCheckBox))
		{
			acceptValuesInInteractiveControlPanel();
			showOrHideControlPanels();
		}
		else if(e.getSource().equals(showSpaceCancellingWedgesEdgesCheckBox))
		{
			acceptValuesInInteractiveControlPanel();
			showOrHideControlPanels();
		}
		else if(e.getSource().equals(gluingTypeComboBox))
		{
			acceptValuesInInteractiveControlPanel();
			showOrHideControlPanels();
		}
		else if(e.getSource().equals(showSphereCheckBox))
		{
			acceptValuesInInteractiveControlPanel();
			showOrHideControlPanels();
		}
		else if(e.getSource().equals(showTrajectoryCheckBox))
		{
			acceptValuesInInteractiveControlPanel();
			showOrHideControlPanels();
		}
		else if(e.getSource().equals(cameraApertureSizeComboBox))
		{
			acceptValuesInInteractiveControlPanel();
			showOrHideControlPanels();
		}
	}

	public static void main(final String[] args)
	{
		(new NetOfRegular4SimplexVisualiser()).run();
	}
}
