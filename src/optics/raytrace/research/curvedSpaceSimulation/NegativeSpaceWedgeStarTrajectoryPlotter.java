package optics.raytrace.research.curvedSpaceSimulation;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import math.*;
import net.miginfocom.swing.MigLayout;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.surfaces.SurfaceColour;
import optics.raytrace.surfaces.SurfaceColourLightSourceIndependent;
import optics.raytrace.core.*;
import optics.raytrace.exceptions.SceneException;
import optics.raytrace.research.curvedSpaceSimulation.PositiveSpaceOnlyOrthographicCamera.LeftoverSpaceStretchingMethodType;
import optics.DoubleColour;
import optics.raytrace.NonInteractiveTIMActionEnum;
import optics.raytrace.NonInteractiveTIMEngine;
import optics.raytrace.GUI.cameras.EditableOrthographicCamera;
import optics.raytrace.GUI.cameras.RenderQualityEnum;
import optics.raytrace.GUI.lowLevel.DoublePanel;
import optics.raytrace.GUI.lowLevel.GUIBitsAndBobs;
import optics.raytrace.GUI.lowLevel.LabelledDoublePanel;
import optics.raytrace.GUI.lowLevel.LabelledIntPanel;
import optics.raytrace.GUI.sceneObjects.EditableNegativeSpaceWedgeStar;
import optics.raytrace.GUI.sceneObjects.EditableRayTrajectory;


/**
 * Plots a number of trajectories through negative-space-wedge stars.
 * Each negative-space wedge consists of two sheets that perform negative refraction (n_1 = - n_2).
 * 
 * @author Johannes Courtial, Dimitris Georgantzis 
 */
public class NegativeSpaceWedgeStarTrajectoryPlotter extends NonInteractiveTIMEngine
{
	// additional parameters
	
	/**
	 * Number of negative-space wedges in star
	 */
	protected int numberOfNegativeSpaceWedges;
	
	/**
	 * Deficit angle of star, in degrees
	 */
	protected double deficitAngleDeg;
	
	/**
	 * If true, show edges of negative-space wedges in star, otherwise don't
	 */
	protected boolean showNegativeRefractionSheetEdges;
	
	/**
	 * Initial distance of the ray from the centre
	 */
	protected double rayStartDistance;
	
	/**
	 * Initial direction of ray (deviation from vertical), in degrees
	 */
	protected double rayStartAngleDeg;
	
	/**
	 * If true, show only the space that has not been cancelled by the negative-space wedges
	 */
	protected boolean hideCancelledSpace;
	
	/**
	 * Specify the method by which negative-space wedges get cancelled if hideCancelledSpace = true
	 */
	protected LeftoverSpaceStretchingMethodType leftoverSpaceStretchingMethod;
	
	/**
	 * Constructor.
	 * Sets all parameters.
	 */
	public NegativeSpaceWedgeStarTrajectoryPlotter()
	{
		super();
		
		// set to true for interactive version
		nonInteractiveTIMAction = NonInteractiveTIMActionEnum.INTERACTIVE;

		// set all parameters
		numberOfNegativeSpaceWedges = 10;
		deficitAngleDeg = 360.-60.;	// -10.;
		rayStartDistance = 1;
		rayStartAngleDeg = 2;
		showNegativeRefractionSheetEdges = false;
		hideCancelledSpace = true;
		leftoverSpaceStretchingMethod = LeftoverSpaceStretchingMethodType.STRETCH_ANGLE;
		
		renderQuality = RenderQualityEnum.DRAFT;
		
		// boring parameters
		nonInteractiveTIMAction = NonInteractiveTIMActionEnum.INTERACTIVE;

		numberOfFrames = 50;
		firstFrame = 0;
		lastFrame = numberOfFrames-1;
		
		if(nonInteractiveTIMAction == NonInteractiveTIMActionEnum.INTERACTIVE)
		{
			windowTitle = "Dr TIM's negative-space-wedge-star trajectory plotter";
			windowWidth = 1300;
			windowHeight = 650;
		}
	}

	@Override
	public String getClassName()
	{
		return "NegativeSpaceWedgeStarTrajectoryPlotter "	// the name
				+ " deficit angle "+deficitAngleDeg+"°"
				+ " number of negative-space wedges "+numberOfNegativeSpaceWedges
				+ " ray start distance "+rayStartDistance
				+ " ray start angle "+rayStartAngleDeg+"°"
				+ " sheet edges " + (showNegativeRefractionSheetEdges?"on":"off")
				+ (hideCancelledSpace?" (cancelled space hidden by "+leftoverSpaceStretchingMethod+")":"")
				;
	}
	
	private void addTrajectory(
			String name,
			double initialDistance,
			double initialAngle,	// in radians
			DoubleColour colour,
			double radius,
			boolean alsoLauchInOppositeDirection,
			int maxTraceLevel,
			SceneObjectContainer scene,
			Studio studio
		)
	{
		Vector3D direction = new Vector3D(Math.cos(initialAngle), 0, Math.sin(initialAngle));
		
		SurfaceColourLightSourceIndependent c = new SurfaceColourLightSourceIndependent(colour, true);
		
		// a ray trajectory in the positive direction...
		scene.addSceneObject(new EditableRayTrajectory(
				name + " positive direction",
				new Vector3D(0, 0, initialDistance),	// start point
				0,	// start time
				direction,	// initial direction
				radius,	// radius
				c,
				maxTraceLevel,	// max trace level
				false,	// reportToConsole
				scene,
				studio
				)
				);

		if(alsoLauchInOppositeDirection)
		// ... and one in the negative direction
		scene.addSceneObject(new EditableRayTrajectory(
				name + " negative direction",
				new Vector3D(0, 0, initialDistance),	// start point
				0,	// start time
				direction.getReverse(),	// initial direction
				radius,	// radius
				c,
				maxTraceLevel,	// max trace level
				false,	// reportToConsole
				scene,
				studio
				)
				);
	}
	
	/* (non-Javadoc)
	 * @see optics.raytrace.NonInteractiveTIMEngine#populateStudio()
	 */
	@Override
	public void populateStudio()
	throws SceneException
	{
		// super.populateSimpleStudio();

		studio = new Studio();

		// the scene
		SceneObjectContainer scene = new SceneObjectContainer("the scene", null, studio);
		studio.setScene(scene);

		// the standard scene objects
		scene.addSceneObject(SceneObjectClass.getHeavenSphere(scene, studio));
		// scene.addSceneObject(SceneObjectClass.getChequerboardFloor(scene, studio));
				
		// add any other scene objects
		
		EditableNegativeSpaceWedgeStar editableNegativeSpaceWedgeStar = new EditableNegativeSpaceWedgeStar(
				"Negative-space-wedge star",	// description
				deficitAngleDeg,
				numberOfNegativeSpaceWedges,
				true,	// showAllNegativeSpaceWedges
				-1,	// numberOfShownNegativeSpaceWedges
				0,	// shownNegativeSpaceWedgesStartIndex
				new Vector3D(0, 0, 0),	// centre
				new Vector3D(0, 1, 0),	// axisDirection
				new Vector3D(0, 0, 1),	// directionOfCentreOfWedge0
				1000,	// starRadius
				1,	// starLength
				0.96,	// sheetTransmissionCoefficient
				showNegativeRefractionSheetEdges,	// showEdges
				0.01,	// edgeRadius
				SurfaceColour.BLACK_MATT,	// edgeSurfaceProperty
				scene, 
				studio
		);
		scene.addSceneObject(editableNegativeSpaceWedgeStar);

		// a trajectory
		
		SceneObjectContainer trajectories = new SceneObjectContainer("ray trajectories", scene, studio); 
		addTrajectory(
				"trajectory",	// name
				rayStartDistance,	// initial distance
				MyMath.deg2rad(rayStartAngleDeg),	// initial direction
				new DoubleColour(1, 0, 0),	// colour
				0.005,	// radius
				true,	// alsoLauchInOppositeDirection
				100*numberOfNegativeSpaceWedges,	// max trace level
				trajectories,
				studio
			);
		scene.addSceneObject(trajectories);
		

		// trace the rays with trajectory through the scene
		studio.traceRaysWithTrajectory();

		// define the camera
		//
		// Note that the view direction and basis Vector3Ds of the detector are chosen such that
		// the x, y, z axes form a LEFT-handed coordinate system.
		// The reason is that, in the photo, the positive x direction is then to the right,
		// the positive y direction is upwards, and the camera looks in the positive z direction.
		int
		pixelsX = 640,
		pixelsY = 480;
		// If antiAliasingFactor is set to N, the image is calculated at resolution
		// N*pixelsX x N*pixelsY.

		
//		SceneObject focusScene = new Plane(
//				"focussing plane",
//				new Vector3D(0, 0, 10),	// point on plane
//				new Vector3D(0, 0, 1),	// normal to plane
//				(SurfaceProperty)null,
//				null,	// parent
//				Studio.NULL_STUDIO
//		);
//		
//		EditableRelativisticAnyFocusSurfaceCamera camera = new EditableRelativisticAnyFocusSurfaceCamera(
//				"Camera",
//				new Vector3D(2, 3, 0),	// centre of aperture
//				new Vector3D(-.2, -.3, 1),	// viewDirection
//				new Vector3D(0, 1, 0),	// top direction vector
//				20,	// horiontalViewAngle in degrees; 2*MyMath.rad2deg(Math.atan(2./10.)) gives same view angle as in previous version
//				new Vector3D(0, 0, 0),	// beta
//				pixelsX*antiAliasingFactor, pixelsY*antiAliasingFactor,	// logical number of pixels
//				ExposureCompensationType.EC0,	// exposure compensation +0
//				100,	// maxTraceLevel
//				focusScene,
//				null,	// cameraFrameScene,
//				ApertureSizeType.PINHOLE,	// aperture size
//				QualityType.RUBBISH,	// blur quality
//				QualityType.NORMAL	// anti-aliasing quality
//		);
		
		double width = 10;
		
		if(hideCancelledSpace)
		{
			double antiAliasingFactor = renderQuality.getAntiAliasingQuality().getAntiAliasingFactor();
			studio.setCamera(new PositiveSpaceOnlyOrthographicCamera(
					"Camera",	// name
					editableNegativeSpaceWedgeStar,	// negativeSpaceWedgeStar
					leftoverSpaceStretchingMethod,	// leftoverSpaceStretchingMethod
					width,
					width*pixelsY/pixelsX,	// height
					(int)(antiAliasingFactor*pixelsX),	// imagePixelsHorizontal
					(int)(antiAliasingFactor*pixelsY),	// imagePixelsVertical
					100	// maxTraceLevel
					));
		}
		else
		{
			studio.setCamera(new EditableOrthographicCamera(
					"Camera",	// name
					new Vector3D(0, -1, 0),	// viewDirection
					new Vector3D(0, 1000, 0),	// CCDCentre
					new Vector3D(0, 0, width),	// horizontalSpanVector3D
					new Vector3D(width*pixelsY/pixelsX, 0, 0),	// verticalSpanVector3D
					pixelsX,	// imagePixelsHorizontal
					pixelsY,	// imagePixelsVertical
					100,	// maxTraceLevel
					renderQuality.getAntiAliasingQuality()	// anti-aliasing quality
					));
		}

		studio.setLights(LightSource.getStandardLightsFromBehind());
	}
	
	
	//
	// for interactive version
	//
	
	private DoublePanel deficitAngleDegPanel;
	private LabelledIntPanel numberOfNegativeSpaceWedgesPanel;
	private JCheckBox showNegativeRefractionSheetEdgesCheckBox;
	private LabelledDoublePanel rayStartDistancePanel;
	private DoublePanel rayStartAngleDegPanel;
	private JCheckBox hideCancelledSpaceCheckBox;
	private JComboBox<LeftoverSpaceStretchingMethodType> leftoverSpaceStretchingMethodCheckBox;
	

	
	/**
	 * add controls to the interactive control panel;
	 * override to modify
	 */
	@Override
	protected void createInteractiveControlPanel()
	{
		super.createInteractiveControlPanel();
		
		deficitAngleDegPanel = new DoublePanel();
		deficitAngleDegPanel.setNumber(deficitAngleDeg);
		interactiveControlPanel.add(GUIBitsAndBobs.makeRow("Deficit angle", deficitAngleDegPanel, "°"), "span");
		
		numberOfNegativeSpaceWedgesPanel = new LabelledIntPanel("Number of negative-space wedges");
		numberOfNegativeSpaceWedgesPanel.setNumber(numberOfNegativeSpaceWedges);
		interactiveControlPanel.add(numberOfNegativeSpaceWedgesPanel, "span");
		
		showNegativeRefractionSheetEdgesCheckBox = new JCheckBox("Show negative-refraction-sheet edges");
		showNegativeRefractionSheetEdgesCheckBox.setSelected(showNegativeRefractionSheetEdges);
		interactiveControlPanel.add(showNegativeRefractionSheetEdgesCheckBox, "span");
		
		JPanel rayTrajectoryJPanel = new JPanel();
		rayTrajectoryJPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Ray trajectory"));
		rayTrajectoryJPanel.setLayout(new MigLayout("insets 0"));
		interactiveControlPanel.add(rayTrajectoryJPanel, "span");

		rayStartDistancePanel = new LabelledDoublePanel("Initial distance from centre");
		rayStartDistancePanel.setNumber(rayStartDistance);
		rayTrajectoryJPanel.add(rayStartDistancePanel, "span");
		rayStartAngleDegPanel = new DoublePanel();
		rayStartAngleDegPanel.setNumber(rayStartAngleDeg);
		rayTrajectoryJPanel.add(GUIBitsAndBobs.makeRow("Initial angle", rayStartAngleDegPanel, "°"), "span");
		
		JPanel hideCancelledSpaceJPanel = new JPanel();
		hideCancelledSpaceJPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Hiding of cancelled space (top view in higher-dimensional space)"));
		hideCancelledSpaceJPanel.setLayout(new MigLayout("insets 0"));
		interactiveControlPanel.add(hideCancelledSpaceJPanel, "span");
		
		hideCancelledSpaceCheckBox = new JCheckBox("Hide by");
		hideCancelledSpaceCheckBox.setSelected(hideCancelledSpace);
		leftoverSpaceStretchingMethodCheckBox = new JComboBox<LeftoverSpaceStretchingMethodType>(LeftoverSpaceStretchingMethodType.values());
		leftoverSpaceStretchingMethodCheckBox.setSelectedItem(leftoverSpaceStretchingMethod);
		hideCancelledSpaceJPanel.add(GUIBitsAndBobs.makeRow(hideCancelledSpaceCheckBox, leftoverSpaceStretchingMethodCheckBox), "span");
	}
	
	/**
	 * called before rendering;
	 * override when adding fields
	 */
	@Override
	protected void acceptValuesInInteractiveControlPanel()
	{
		super.acceptValuesInInteractiveControlPanel();
		
		deficitAngleDeg = deficitAngleDegPanel.getNumber();
		numberOfNegativeSpaceWedges = numberOfNegativeSpaceWedgesPanel.getNumber();
		showNegativeRefractionSheetEdges = showNegativeRefractionSheetEdgesCheckBox.isSelected();
		rayStartDistance = rayStartDistancePanel.getNumber();
		rayStartAngleDeg = rayStartAngleDegPanel.getNumber();
		hideCancelledSpace = hideCancelledSpaceCheckBox.isSelected();
		leftoverSpaceStretchingMethod = (LeftoverSpaceStretchingMethodType)(leftoverSpaceStretchingMethodCheckBox.getSelectedItem());
	}
	

	public static void main(final String[] args)
	{
		(new NegativeSpaceWedgeStarTrajectoryPlotter()).run();
	}
}
