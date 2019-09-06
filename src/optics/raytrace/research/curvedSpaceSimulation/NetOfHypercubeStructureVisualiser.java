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
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectPrimitiveIntersection;
import optics.raytrace.surfaces.ColourFilter;
import optics.raytrace.surfaces.SurfaceColour;
import optics.raytrace.exceptions.InconsistencyException;
import optics.raytrace.exceptions.SceneException;
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
import optics.raytrace.GUI.sceneObjects.NullSpaceWedgeType;
import optics.raytrace.core.LightSource;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.SceneObjectClass;
import optics.raytrace.core.Studio;


/**
 * Helps to understand the structure of the optical realisation of the net of a hypercube
 * 
 * @author Johannes Courtial 
 */
public class NetOfHypercubeStructureVisualiser extends NonInteractiveTIMEngine
{
	
	// most additional parameters are stored in the following EditableNetOfHypercube
	private EditableNetOfHypercube net;
	
	/**
	 * show the null-space wedges
	 */
	private boolean showNullSpaceWedges;

	/**
	 * show edges of the net of the hypercube
	 */
	private boolean showNetEdges;
	
	/**
	 * show edges of the null-space wedges, i.e. the vertices and edges of the sheets forming the null-space wedges
	 */
	private boolean showNullSpaceWedgeEdges;
	
	private boolean showVolumeOfInnerNullSpaceWedge;


	/**
	 * Constructor.
	 * Sets all parameters.
	 */
	public NetOfHypercubeStructureVisualiser()
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
				false,	// showNullSpaceWedges
				1,	// nullSpaceWedgeLegLengthFactor
				0.96,	// refractingSurfaceTransmissionCoefficient
				NullSpaceWedgeType.IDEAL,	// nullSpaceWedgeType
				1,	// numberOfNegativeSpaceWedges
				true,	// showNetStructure
				false,	// showNullSpaceWedgesStructure
				SurfaceColour.BLUE_SHINY,	// netStructureSurfaceProperty
				ColourFilter.CYAN_GLASS,	// netFaceSurfaceProperty
				SurfaceColour.RED_SHINY,	// nullSpaceWedgesStructureSurfaceProperty
				0.02,	// structureTubeRadius
				null,	// parent
				null	// studio
			);
		
		showNullSpaceWedges = false;
		showNetEdges = true;
		showNullSpaceWedgeEdges = false;
		showVolumeOfInnerNullSpaceWedge = true;
		
		// camera
		cameraViewDirection = new Vector3D(0, 0, 1);
		cameraViewCentre = new Vector3D(0, 0, 0);
		cameraDistance = 10;
		cameraFocussingDistance = 10;
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
		windowTitle = "Dr TIM's net-of-hypercube trajectory plotter";
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
		printStream.println("Scene initialisation");
		printStream.println();
		
		printStream.println("showNullSpaceWedges = "+showNullSpaceWedges);
		printStream.println("showNetEdges = "+showNetEdges);
		printStream.println("showNullSpaceWedgeEdges = "+showNullSpaceWedgeEdges);
		printStream.println("showVolumeOfInnerNullSpaceWedge = "+showVolumeOfInnerNullSpaceWedge);
		
		printStream.println();

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
		studio = new Studio();

		// the scene
		SceneObjectContainer scene = new SceneObjectContainer("the scene", null, studio);

		// the standard scene objects
		scene.addSceneObject(SceneObjectClass.getLighterChequerboardFloor(-3, scene, studio));	// the checkerboard floor
		scene.addSceneObject(SceneObjectClass.getSkySphere(scene, studio));	// the sky

		studio.setScene(scene);
		studio.setLights(LightSource.getStandardLightsFromBehind());
		studio.setCamera(getStandardCamera());

		// the net: set its parent to <i>scene</i>...
		net.setParent(scene);
		// ... and get it prepared for light-ray-trajectory tracing, i.e. showing the null-space wedges but hiding all edges
		net.setShowNullSpaceWedges(showNullSpaceWedges);
		net.setShowNetEdges(showNetEdges);
		net.setShowNullSpaceWedgeEdges(showNullSpaceWedgeEdges);

		// now add the scene objects to the net...
		try {
			net.populateSceneObjectCollection();
		} catch (InconsistencyException e) {
			e.printStackTrace();
		}
		// ... and add the net to the scene
		scene.addSceneObject(net);
		
		if(showVolumeOfInnerNullSpaceWedge)
		{
			// create a scene object that represents the volume of the inner null-space wedge
			SceneObjectPrimitiveIntersection volumeOfInnerNullSpaceWedge = new SceneObjectPrimitiveIntersection(
					"volume of inner null-space wedge",	// description
					scene,	// parent
					studio
				);
			
			// add to it all the intersecting planes
			volumeOfInnerNullSpaceWedge.addInvisiblePositiveSceneObjectPrimitive(sceneObjectPrimitive);
			
			scene.addSceneObject(volumeOfInnerNullSpaceWedge);
		}
	}
	
	
	//
	// for interactive version
	//
	
	// GUI panels
	private LabelledVector3DPanel centrePanel, rightDirectionPanel, upDirectionPanel;
	private LabelledDoublePanel sideLengthPanel, edgeRadiusPanel, nullSpaceWedgeLegLengthFactorPanel, nullSpaceWedgeSurfaceTransmissionCoefficientPanel;
	private LabelledIntPanel numberOfNegativeSpaceWedgesPanel;
	private JComboBox<NullSpaceWedgeType> nullSpaceWedgeTypeComboBox;
	private JCheckBox showNullSpaceWedgesCheckBox, showNetEdgesCheckBox, showNullSpaceWedgesEdgesCheckBox, showVolumeOfInnerNullSpaceWedgeCheckBox;
	
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
		
		sideLengthPanel = new LabelledDoublePanel("Side length");
		sideLengthPanel.setNumber(net.getSideLength());
		netPanel.add(sideLengthPanel, "wrap");
		

		rightDirectionPanel = new LabelledVector3DPanel("Rightwards direction");
		rightDirectionPanel.setVector3D(net.getRightDirection());
		netPanel.add(rightDirectionPanel, "wrap");

		upDirectionPanel = new LabelledVector3DPanel("Upwards direction");
		upDirectionPanel.setVector3D(net.getUpDirection());
		netPanel.add(upDirectionPanel, "wrap");
		
		showNullSpaceWedgesCheckBox = new JCheckBox("Show null-space wedges");
		showNullSpaceWedgesCheckBox.setSelected(showNullSpaceWedges);
		netPanel.add(showNullSpaceWedgesCheckBox, "wrap");
		
		nullSpaceWedgeLegLengthFactorPanel = new LabelledDoublePanel("Leg length factor");
		nullSpaceWedgeLegLengthFactorPanel.setNumber(net.getNullSpaceWedgeLegLengthFactor());
		netPanel.add(nullSpaceWedgeLegLengthFactorPanel, "wrap");
		
		nullSpaceWedgeTypeComboBox = new JComboBox<NullSpaceWedgeType>(NullSpaceWedgeType.values());
		nullSpaceWedgeTypeComboBox.setSelectedItem(net.getNullSpaceWedgeType());
		nullSpaceWedgeTypeComboBox.addActionListener(this);
		netPanel.add(nullSpaceWedgeTypeComboBox, "wrap");
		
		nullSpaceWedgeSurfaceTransmissionCoefficientPanel = new LabelledDoublePanel("Transmission coefficient of null-space-wedge surfaces");
		nullSpaceWedgeSurfaceTransmissionCoefficientPanel.setNumber(net.getNullSpaceWedgeSurfaceTransmissionCoefficient());
		netPanel.add(nullSpaceWedgeSurfaceTransmissionCoefficientPanel, "wrap");
		
		numberOfNegativeSpaceWedgesPanel = new LabelledIntPanel("Number of negative-space wedges per null-space wedge");
		numberOfNegativeSpaceWedgesPanel.setNumber(net.getNumberOfNegativeSpaceWedges());
		netPanel.add(numberOfNegativeSpaceWedgesPanel, "wrap");
		
		
		JPanel structureVisualisationPanel = new JPanel();
		structureVisualisationPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Show structure"));
		structureVisualisationPanel.setLayout(new MigLayout("insets 0"));
		
		showNetEdgesCheckBox = new JCheckBox("Show structure (edges and faces) of net of hypercube");
		showNetEdgesCheckBox.setSelected(showNetEdges);
		structureVisualisationPanel.add(showNetEdgesCheckBox, "wrap");

		showNullSpaceWedgesEdgesCheckBox = new JCheckBox("Show edges of null-space wedges");
		showNullSpaceWedgesEdgesCheckBox.setSelected(showNullSpaceWedgeEdges);
		structureVisualisationPanel.add(showNullSpaceWedgesEdgesCheckBox, "wrap");

		edgeRadiusPanel = new LabelledDoublePanel("Tube radius");
		edgeRadiusPanel.setNumber(net.getEdgeRadius());
		structureVisualisationPanel.add(edgeRadiusPanel, "wrap");
		
		showVolumeOfInnerNullSpaceWedgeCheckBox = new JCheckBox("Show volume of inner null-space wedge");
		showVolumeOfInnerNullSpaceWedgeCheckBox.setSelected(showVolumeOfInnerNullSpaceWedge);
		structureVisualisationPanel.add(showVolumeOfInnerNullSpaceWedgeCheckBox, "wrap");
		
		netPanel.add(structureVisualisationPanel, "wrap");

		tabbedPane.addTab("Net", netPanel);
		
		
		//
		// camera panel
		//
		
		JPanel cameraPanel = new JPanel();
		cameraPanel.setLayout(new MigLayout("insets 0"));

		cameraPositionPanel = new LabelledVector3DPanel("Camera position");
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
		cameraPanel.add(GUIBitsAndBobs.makeRow("Camera aperture", cameraApertureSizeComboBox), "span");		
		
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
		showNullSpaceWedges = showNullSpaceWedgesCheckBox.isSelected();
		net.setNullSpaceWedgeLegLengthFactor(nullSpaceWedgeLegLengthFactorPanel.getNumber());
		net.setNullSpaceWedgeSurfaceTransmissionCoefficient(nullSpaceWedgeSurfaceTransmissionCoefficientPanel.getNumber());
		net.setNumberOfNegativeSpaceWedges(numberOfNegativeSpaceWedgesPanel.getNumber());
		net.setNullSpaceWedgeType((NullSpaceWedgeType)nullSpaceWedgeTypeComboBox.getSelectedItem());
		net.setEdgeRadius(edgeRadiusPanel.getNumber());
		showNetEdges = showNetEdgesCheckBox.isSelected();
		showNullSpaceWedgeEdges = showNullSpaceWedgesEdgesCheckBox.isSelected();
		showVolumeOfInnerNullSpaceWedge = showVolumeOfInnerNullSpaceWedgeCheckBox.isSelected();
		
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
		switch(net.getNullSpaceWedgeType())
		{
		case NEGATIVE_SPACE_WEDGES:
			numberOfNegativeSpaceWedgesPanel.setEnabled(true);
			break;
		case IDEAL:
		default:
			numberOfNegativeSpaceWedgesPanel.setEnabled(false);
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		super.actionPerformed(e);
		
		if(e.getSource().equals(nullSpaceWedgeTypeComboBox))
		{
			net.setNullSpaceWedgeType((NullSpaceWedgeType)(nullSpaceWedgeTypeComboBox.getSelectedItem()));
			
			showOrHidePanels();
		}
	}


	public static void main(final String[] args)
	{
		(new NetOfHypercubeStructureVisualiser()).run();
	}
}
