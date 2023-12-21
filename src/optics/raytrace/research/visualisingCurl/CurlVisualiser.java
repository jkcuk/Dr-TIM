package optics.raytrace.research.visualisingCurl;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintStream;
import java.text.NumberFormat;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

import math.*;
import net.miginfocom.swing.MigLayout;
import optics.DoubleColour;
import optics.raytrace.NonInteractiveTIMActionEnum;
import optics.raytrace.NonInteractiveTIMEngine;
import optics.raytrace.GUI.cameras.RenderQualityEnum;
import optics.raytrace.GUI.lowLevel.ApertureSizeType;
import optics.raytrace.GUI.lowLevel.DoublePanel;
import optics.raytrace.GUI.lowLevel.GUIBitsAndBobs;
import optics.raytrace.GUI.lowLevel.LabelledDoubleColourPanel;
import optics.raytrace.GUI.lowLevel.LabelledDoublePanel;
import optics.raytrace.GUI.lowLevel.LabelledVector3DPanel;
import optics.raytrace.GUI.sceneObjects.EditableArrow;
import optics.raytrace.GUI.sceneObjects.EditableFramedRectangle;
import optics.raytrace.GUI.sceneObjects.EditableParametrisedPlane;
import optics.raytrace.core.Ray;
import optics.raytrace.core.RaySceneObjectIntersection;
import optics.raytrace.core.Studio;
import optics.raytrace.core.StudioInitialisationType;
import optics.raytrace.core.SurfacePropertyPrimitive;
import optics.raytrace.exceptions.RayTraceException;
import optics.raytrace.exceptions.SceneException;
import optics.raytrace.sceneObjects.Sphere;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.surfaces.DirectionChanging;
import optics.raytrace.surfaces.LightRayField;
import optics.raytrace.surfaces.LightRayFieldCollimated;
import optics.raytrace.surfaces.LightRayFieldRepresentingParaxialOpticalVortex;
import optics.raytrace.surfaces.LightRayFieldRepresentingPointLightSource;
import optics.raytrace.surfaces.RayRotating;
import optics.raytrace.surfaces.SurfaceColour;
import optics.raytrace.surfaces.Transparent;


/**
 * @author Johannes Courtial
 */
public class CurlVisualiser extends NonInteractiveTIMEngine
implements ActionListener
{
	
	public enum WindowType
	{
		RR_SHEET("Ray-rotation sheet"),
		TRANSPARENT("Transparent");
		// TODO add ray flipping
		// TODO add hologram
		
		private String description;
		private WindowType(String description) {this.description = description;}	
		@Override
		public String toString() {return description;}
	}
	private WindowType windowType;
	private double windowZ;
	
	public enum LightFieldType
	{
		POINT_OBJECT("Point(ish) object", null),
		FIELD_FROM_POINT("Fuzzy light-ray field", "Point source"),
		COLLIMATED_RAYS("Fuzzy light-ray field", "Collimated rays"),
		VORTEX_RAYS("Fuzzy light-ray field", "Optical vortex");
		
		private String description1, description2;
		private LightFieldType(String description1, String description2) {
			this.description1 = description1;
			this.description2 = description2;
		}	
		public String getDescription1() {return description1;}
		public String getDescription2() {return description2;}
	}
	private LightFieldType lightFieldType;
	
	//  point object
	private Vector3D pointObjectPosition;	
	private double pointObjectSphereRadius;

	// light-ray field
	private double lightRayFieldFuzzinessRad;
	private DoubleColour lightRayFieldColour;

	// field from point
	private Vector3D fieldFromPointPosition;	
	
	// collimated  light rays
	private Vector3D collimatedLightRaysDirection;
	
	// optical vortex
	private Vector3D pointOnVortexLine;
	private Vector3D normalisedVortexLineDirection;
	private double mOverK;
	

	private double windowTransmissionCoefficient;

	private double rrAngleDeg;
	
	private Vector3D referencePosition;
	private double delta;
	
	/**
	 * Determines how to initialise the backdrop
	 */
	private StudioInitialisationType studioInitialisation;

	
	
	/**
	 * Constructor.
	 * Sets all parameters.
	 */
	public CurlVisualiser()
	{
		super();
		
		lightFieldType =  LightFieldType.POINT_OBJECT;
		
		pointObjectPosition =  new Vector3D(0, 0, 10);
		pointObjectSphereRadius = 0.1;
		
		lightRayFieldFuzzinessRad = MyMath.deg2rad(1);
		lightRayFieldColour = DoubleColour.RED;

		fieldFromPointPosition = new Vector3D(0, 0, 10);	

		collimatedLightRaysDirection = new Vector3D(0, 0, -1);
		
		pointOnVortexLine = new Vector3D(0, 0, 0);
		normalisedVortexLineDirection = new Vector3D(0, 0, -1);
		mOverK = 1;

		
		windowType = WindowType.RR_SHEET;
		windowTransmissionCoefficient = SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT;
		windowZ = 0;
		rrAngleDeg = 90;
		
		referencePosition = new Vector3D(0, 0, windowZ);
		delta = 0.1;
		
		studioInitialisation = StudioInitialisationType.MINIMALIST;	// the backdrop
		
		// camera
		
		renderQuality = RenderQualityEnum.DRAFT;
		
		nonInteractiveTIMAction = NonInteractiveTIMActionEnum.INTERACTIVE;
		
		cameraViewDirection = new Vector3D(0, 0, 1);
		cameraViewCentre = new Vector3D(0, 0, windowZ);
		cameraDistance = 5;	// camera is located at (0, 0, 0)
		cameraFocussingDistance = 5;
		cameraHorizontalFOVDeg = 32;
		cameraMaxTraceLevel = 100;
		cameraPixelsX = 640;
		cameraPixelsY = 480;
		cameraApertureSize = 
				ApertureSizeType.PINHOLE;
				// ApertureSizeType.SMALL;

		
		// the window
		windowTitle = "Dr TIM's curl visualiser";
		windowWidth = 1650;
		windowHeight = 650;

	}

	
	/* (non-Javadoc)
	 * @see optics.raytrace.NonInteractiveTIMEngine#getFirstPartOfFilename()
	 */
	@Override
	public String getClassName()
	{
		return
				"CurlVisualiser"
				;
	}
	
	/*
	 * Write all parameters to a .txt file
	 * @see optics.raytrace.NonInteractiveTIMEngine#writeParameters(java.io.PrintStream)
	 */
	@Override
	public void writeParameters(PrintStream printStream)
	{
		// write any parameters not defined in NonInteractiveTIMEngine, each parameter is saved like this:
		// printStream.println("parameterName = "+parameterName);

		// write all parameters defined in NonInteractiveTIMEngine
		super.writeParameters(printStream);
		
		printStream.println("lightFieldType = "+lightFieldType);
		printStream.println("pointObjectPosition = "+pointObjectPosition);
		printStream.println("pointObjectSphereRadius = "+pointObjectSphereRadius);
		printStream.println("lightRayFieldFuzzinessRad = "+lightRayFieldFuzzinessRad);
		printStream.println("lightRayFieldColour = "+lightRayFieldColour);
		printStream.println("fieldFromPointPosition = "+fieldFromPointPosition);
		printStream.println("collimatedLightRaysDirection = "+collimatedLightRaysDirection);
		
		printStream.println("pointOnVortexLine = "+pointOnVortexLine);
		printStream.println("normalisedVortexLineDirection = "+normalisedVortexLineDirection);
		printStream.println("mOverK = "+mOverK);

		printStream.println("windowType = "+windowType);
		printStream.println("windowTransmissionCoefficient = "+windowTransmissionCoefficient);
		printStream.println("windowZ = "+windowZ);
		printStream.println("rrAngleDeg = "+rrAngleDeg);
		printStream.println("delta = "+delta);
		printStream.println("studioInitialisation = "+studioInitialisation);

	}
	
	private EditableFramedRectangle window;
	private DirectionChanging windowSurfaceProperty;
	private String windowSurfaceDescription;
	
	private void setWindow()
	{
		switch(windowType)
		{
		case RR_SHEET:
			windowSurfaceProperty = new RayRotating(
					MyMath.deg2rad(rrAngleDeg),	// rotationAngle
					windowTransmissionCoefficient,	// transmissionCoefficient
					false	// shadowThrowing
			);
			break;
//		case GLENS:
//			windowSurfaceProperty
		case TRANSPARENT:
		default:
			windowSurfaceProperty = new Transparent(windowTransmissionCoefficient, false);
		}
		windowSurfaceDescription = windowType.toString();
		
		window = new EditableFramedRectangle(
				"window with "+windowSurfaceDescription+" surface property",	// description
				new Vector3D(-1, -1, windowZ),	// corner
				new Vector3D(2, 0, 0),	// widthVector
				new Vector3D(0, 2, 0),	// heightVector
				0.01,	// frameRadius
				windowSurfaceProperty,	// windowSurfaceProperty
				SurfaceColour.GREY30_MATT,	// frameSurfaceProperty
				true,	// showFrames
				null,	// parent
				studio
				);
	}
	
	/* (non-Javadoc)
	 * @see optics.raytrace.NonInteractiveTIMEngine#populateStudio()
	 */
	@Override
	public void populateStudio()
	throws SceneException
	{
		// camera

		studio = new Studio();

		// the scene
		SceneObjectContainer scene = new SceneObjectContainer("the scene", null, studio);

		StudioInitialisationType.initialiseSceneAndLights(
				studioInitialisation,
				scene,
				studio
				);

//		// the standard scene objects
//		scene.addSceneObject(SceneObjectClass.getLighterChequerboardFloor(scene, studio));	// the checkerboard floor
//		scene.addSceneObject(SceneObjectClass.getSkySphere(scene, studio));	// the sky

		// add any other scene objects
		
		//  the light field
		switch(lightFieldType)
		{
		case FIELD_FROM_POINT:
		case COLLIMATED_RAYS:
		case VORTEX_RAYS:
			LightRayField lrf;
			switch(lightFieldType)
			{
			case FIELD_FROM_POINT:
				lrf  =  new LightRayFieldRepresentingPointLightSource(
						lightRayFieldColour,	// colour, 
						fieldFromPointPosition,	// position, 
						false,	// raysTowardsPosition, 
						lightRayFieldFuzzinessRad,	// angular fuzziness
						true	// bidirectional
					);
				break;
			case VORTEX_RAYS:
				lrf = new LightRayFieldRepresentingParaxialOpticalVortex(
						lightRayFieldColour,	// colour 
						pointOnVortexLine, 
						normalisedVortexLineDirection,
						mOverK, 
						lightRayFieldFuzzinessRad,
						false	// bidirectional
					);
				break;
			case COLLIMATED_RAYS:
			default:
				lrf = new LightRayFieldCollimated(
						lightRayFieldColour,	// colour
						collimatedLightRaysDirection,	// normalisedLightRayDirection, 
						lightRayFieldFuzzinessRad,	// angularFuzzinessRad
						true	// bidirectional
					);
			}
			scene.addSceneObject(
					new EditableParametrisedPlane(
							"Plane in which light-ray field is represented",	// description
							Vector3D.sum(referencePosition, new Vector3D(0, 0, MyMath.TINY)),	// pointOnPlane
							Vector3D.X,	// v1
							Vector3D.Y,	// v2
							lrf,	// sp
							scene,	// parent
							studio
						)
				);
			break;
		case POINT_OBJECT:
		default:
			//  red sphere
			scene.addSceneObject(new Sphere(
					"Sphere at position of point light source", 
					pointObjectPosition, 
					pointObjectSphereRadius,
					new SurfaceColour(lightRayFieldColour, DoubleColour.WHITE, true),
					scene, studio
				));
		}
		
		// the window
		setWindow();
		scene.addSceneObject(window);
		
//		// coordinate system in the plane of the window
//		scene.addSceneObject(new EditableCylinderLattice(
//			"coordinate system",	// description
//			-0.2, 0.2, 3,	// xMin, xMax, nX
//			-0.2, 0.2, 3,	// yMin, yMax, nY
//			5, 5, 1,	// zMin, zMax, nZ
//			0.005,	// radius
//			scene,	// parent
//			studio
//			));
		
		double  arrowLength = 0.2;
		
		scene.addSceneObject(new EditableArrow(
				"Arrow pointing at reference position",	// description
				Vector3D.sum(
						referencePosition,
						new Vector3D(-arrowLength, -arrowLength, 0)
					),	// startPoint
				referencePosition,	// endPoint
				SurfaceColour.GREEN_SHINY,	// surfaceProperty,
				scene,	// parent
				studio
			));

		Vector3D rightMeasurementPosition = Vector3D.sum(referencePosition, new Vector3D(delta, 0, 0));
		scene.addSceneObject(new EditableArrow(
				"Arrow pointing at position shifted from ref.  position by delta to the right",	// description
				Vector3D.sum(rightMeasurementPosition, new Vector3D(arrowLength, 0, 0)),	// startPoint
				rightMeasurementPosition,	// endPoint
				SurfaceColour.GREEN_SHINY,	// surfaceProperty,
				scene,	// parent
				studio
			));

		Vector3D topMeasurementPosition = Vector3D.sum(referencePosition, new Vector3D(0, delta, 0));
		scene.addSceneObject(new EditableArrow(
				"Arrow pointing at position shifted from ref. position by delta upwards",	// description
				Vector3D.sum(topMeasurementPosition, new Vector3D(0, arrowLength, 0)),	// startPoint
				topMeasurementPosition,	// endPoint
				SurfaceColour.GREEN_SHINY,	// surfaceProperty,
				scene,	// parent
				studio
			));

		studio.setScene(scene);
		// studio.setLights(LightSource.getStandardLightsFromBehind());
		studio.setCamera(getStandardCamera());
				
	}
	
	
	//  interactive stuff

	JComboBox<StudioInitialisationType> studioInitialisationComboBox;
	
	JTabbedPane windowTabbedPane;	
	private LabelledDoublePanel windowTransmissionCoefficientPanel;
	
	// RR sheet
	private DoublePanel rrAngleDegPanel;

	
	JTabbedPane lightFieldTabbedPane1, lightFieldTabbedPane2;
	
	//  point object ray field
	private LabelledVector3DPanel pointObjectPositionPanel; 
	private LabelledDoublePanel pointObjectSphereRadiusPanel;
	
	// light ray  fields

	private DoublePanel fieldFromPointFuzzinessDegPanel;
	private LabelledDoubleColourPanel lightRayFieldColourPanel;
	
	// field from point
	private LabelledVector3DPanel fieldFromPointPositionPanel;

	// collimated light rays
	private LabelledVector3DPanel collimatedLightRaysDirectionPanel;
	
	// vortex light rays
	private LabelledVector3DPanel pointOnVortexLinePanel; 
	private LabelledVector3DPanel normalisedVortexLineDirectionPanel;
	private LabelledDoublePanel mOverKPanel; 

	
	// curl measurement
	private LabelledDoublePanel deltaPanel;
	private JTextArea measurementResults;
	private JButton calculateButton;

	//  private JComboBox<StudioInitialisationType> studioInitialisationComboBox;

	// camera stuff
	// private LabelledVector3DPanel cameraViewDirectionPanel;
	private LabelledDoublePanel cameraDistancePanel;
	private LabelledVector3DPanel cameraViewDirectionPanel, cameraViewCentrePanel;
	private DoublePanel cameraHorizontalFOVDegPanel;
	private JComboBox<ApertureSizeType> cameraApertureSizeComboBox;
	private LabelledDoublePanel cameraFocussingDistancePanel;
	private JButton alignImageWithReferencePositionButton, alignImageWithRightMeasurementPositionButton, alignImageWithTopMeasurementPositionButton;


	
	/**
	 * add controls to the interactive control panel;
	 * override to modify
	 */
	@Override
	protected void createInteractiveControlPanel()
	{
		super.createInteractiveControlPanel();

		// the main tabbed pane, with "Scene" and "Camera" tabs
		JTabbedPane mainTabbedPane = new JTabbedPane();
		interactiveControlPanel.add(mainTabbedPane, "span");
		
		
		//
		// the light-field panel
		//

		JPanel lightFieldAndScenePanel = new JPanel();
		lightFieldAndScenePanel.setLayout(new MigLayout("insets 0"));
		// scenePanel.setLayout(new BorderLayout());
		mainTabbedPane.addTab("Light field (& scene)", lightFieldAndScenePanel);
		
		studioInitialisationComboBox = new JComboBox<StudioInitialisationType>(StudioInitialisationType.limitedValuesForBackgrounds);
		studioInitialisationComboBox.setSelectedItem(studioInitialisation);
		lightFieldAndScenePanel.add(GUIBitsAndBobs.makeRow("Backdrop", studioInitialisationComboBox), "wrap");

		// the light-field tabbed pane
		
		lightFieldTabbedPane1 = new JTabbedPane();
		lightFieldAndScenePanel.add(lightFieldTabbedPane1, "wrap");

		// point object
		
		JPanel pointObjectPanel = new JPanel();
		pointObjectPanel.setLayout(new MigLayout("insets 0"));
		
		pointObjectPositionPanel = new LabelledVector3DPanel("Point light source position"); 
		pointObjectPositionPanel.setVector3D(pointObjectPosition);
		pointObjectPanel.add(pointObjectPositionPanel, "wrap");
		
		pointObjectSphereRadiusPanel = new LabelledDoublePanel("Radius of red sphere at point-light-source position");
		pointObjectSphereRadiusPanel.setNumber(pointObjectSphereRadius);
		pointObjectPanel.add(pointObjectSphereRadiusPanel, "wrap");

		lightFieldTabbedPane1.addTab(LightFieldType.POINT_OBJECT.getDescription1(), pointObjectPanel);
		
		// light-ray field
		
		JPanel lightRayFieldPanel = new JPanel();
		lightRayFieldPanel.setLayout(new MigLayout("insets 0"));
		
		lightFieldTabbedPane2 = new JTabbedPane();
		lightRayFieldPanel.add(lightFieldTabbedPane2, "wrap");

		
		// field from point light source
		fieldFromPointPositionPanel = new LabelledVector3DPanel("Point light source position"); 
		fieldFromPointPositionPanel.setVector3D(fieldFromPointPosition);
		lightFieldTabbedPane2.addTab(LightFieldType.FIELD_FROM_POINT.getDescription2(), fieldFromPointPositionPanel);
		
		//  collimated rays
		collimatedLightRaysDirectionPanel = new LabelledVector3DPanel("Direction");
		collimatedLightRaysDirectionPanel.setVector3D(collimatedLightRaysDirection);
		lightFieldTabbedPane2.addTab(LightFieldType.COLLIMATED_RAYS.getDescription2(), collimatedLightRaysDirectionPanel);
		
		// vortex rays
		JPanel vortexRaysPanel = new JPanel();
		vortexRaysPanel.setLayout(new MigLayout("insets 0"));
		lightFieldTabbedPane2.addTab(LightFieldType.VORTEX_RAYS.getDescription2(), vortexRaysPanel);

		pointOnVortexLinePanel = new LabelledVector3DPanel("Point on vortex line"); 
		pointOnVortexLinePanel.setVector3D(pointOnVortexLine);
		vortexRaysPanel.add(pointOnVortexLinePanel, "span");
		
		normalisedVortexLineDirectionPanel = new LabelledVector3DPanel("Vortex-line direction");
		normalisedVortexLineDirectionPanel.setVector3D(normalisedVortexLineDirection);
		vortexRaysPanel.add(normalisedVortexLineDirectionPanel, "span");
		
		mOverKPanel = new LabelledDoublePanel("m/k"); 
		mOverKPanel.setNumber(mOverK);
		vortexRaysPanel.add(mOverKPanel, "span");
		
		fieldFromPointFuzzinessDegPanel = new DoublePanel();
		fieldFromPointFuzzinessDegPanel.setNumber(MyMath.rad2deg(lightRayFieldFuzzinessRad));
		lightRayFieldPanel.add(GUIBitsAndBobs.makeRow("Ray fuzziness", fieldFromPointFuzzinessDegPanel, "°"), "span");

		lightFieldTabbedPane1.addTab(LightFieldType.FIELD_FROM_POINT.getDescription1(), lightRayFieldPanel);

		setTab(lightFieldType);
		
		lightRayFieldColourPanel = new LabelledDoubleColourPanel("Colour");
		lightRayFieldColourPanel.setDoubleColour(lightRayFieldColour);
		lightFieldAndScenePanel.add(lightRayFieldColourPanel, "wrap");
		

		
		//
		// the window panel
		//

		JPanel windowPanel = new JPanel();
		windowPanel.setLayout(new MigLayout("insets 0"));
		mainTabbedPane.addTab("Window", windowPanel);

		// the window tabbed pane
		
		windowTabbedPane = new JTabbedPane();
		windowPanel.add(windowTabbedPane, "wrap");

		// ray-rotation sheet
		
		JPanel rayRotationSheetPanel = new JPanel();
		// rayRotationSheetPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Ray-rotation sheet"));
		rayRotationSheetPanel.setLayout(new MigLayout("insets 0"));

		// rayRotationSheetPanel.add(new JLabel("Ray-rotation sheet,"), "wrap");

		rrAngleDegPanel = new DoublePanel();
		rrAngleDegPanel.setNumber(rrAngleDeg);
		rayRotationSheetPanel.add(
				GUIBitsAndBobs.makeRow("Ray-rotation angle (around window normal)", rrAngleDegPanel, "<html>&deg;</html>"), "wrap");

		windowTabbedPane.addTab(WindowType.RR_SHEET.toString(), rayRotationSheetPanel);
		
		// transparent window
		
		windowTabbedPane.addTab(WindowType.TRANSPARENT.toString(), new JLabel("No additional parameters"));

		setTab(windowType);

		windowTransmissionCoefficientPanel = new LabelledDoublePanel("Transmission coefficient");
		windowTransmissionCoefficientPanel.setNumber(windowTransmissionCoefficient);
		windowPanel.add(windowTransmissionCoefficientPanel, "wrap");
		
		//
		// the curl-measurement panel
		//
		
		JPanel curlPanel = new JPanel();
		curlPanel.setLayout(new MigLayout("insets 0"));
		mainTabbedPane.addTab("Curl", curlPanel);

		deltaPanel = new LabelledDoublePanel("Distance of measurement points from reference point");
		deltaPanel.setNumber(delta);
		curlPanel.add(deltaPanel, "wrap");

		measurementResults = new JTextArea(8, 40);
		measurementResults.setText("Press \"Measure\" button to measure");
		curlPanel.add(measurementResults);

		calculateButton = new JButton("Measure");
		calculateButton.addActionListener(this);
		curlPanel.add(calculateButton, "wrap");
		

		//
		// the Camera panel
		//
		
		JPanel cameraPanel = new JPanel();
		cameraPanel.setLayout(new MigLayout("insets 0"));
		mainTabbedPane.addTab("Camera", cameraPanel);

		// camera stuff
		
//		cameraViewDirectionPanel = new LabelledVector3DPanel("View direction");
//		cameraViewDirectionPanel.setVector3D(cameraViewDirection);
//		cameraPanel.add(cameraViewDirectionPanel, "span");
		
		cameraDistancePanel = new LabelledDoublePanel("Camera distance");
		cameraDistancePanel.setNumber(cameraDistance);
		cameraPanel.add(cameraDistancePanel, "span");
		
		cameraViewCentrePanel = new LabelledVector3DPanel("View centre");
		cameraViewCentrePanel.setVector3D(cameraViewCentre);
		cameraPanel.add(cameraViewCentrePanel, "span");
		
		cameraViewDirectionPanel = new LabelledVector3DPanel("View direction");
		cameraViewDirectionPanel.setVector3D(cameraViewDirection);
		cameraPanel.add(cameraViewDirectionPanel, "span");
		
		cameraPanel.add(new JLabel("Place camera to align image of point light source with..."), "wrap");
		
		alignImageWithReferencePositionButton = new JButton("reference position");
		alignImageWithReferencePositionButton.addActionListener(this);
		//  cameraPanel.add(alignImageWithReferencePositionButton);
		
		alignImageWithRightMeasurementPositionButton = new JButton("right measurement position");
		alignImageWithRightMeasurementPositionButton.addActionListener(this);
		// cameraPanel.add(alignImageWithRightMeasurementPositionButton);

		alignImageWithTopMeasurementPositionButton = new JButton("top measurement position");
		alignImageWithTopMeasurementPositionButton.addActionListener(this);
		cameraPanel.add(GUIBitsAndBobs.makeRow(alignImageWithReferencePositionButton, alignImageWithRightMeasurementPositionButton, alignImageWithTopMeasurementPositionButton), "wrap");
		
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
	
	private void setTab(WindowType windowType)
	{
		for(int i=0; i<windowTabbedPane.getTabCount(); i++)
		{
			if(windowTabbedPane.getTitleAt(i).equals(windowType.toString())) windowTabbedPane.setSelectedIndex(i);;
		}
	}
	
	private WindowType getWindowType()
	{
		String selectedText = windowTabbedPane.getTitleAt(windowTabbedPane.getSelectedIndex());
		for(WindowType wt : WindowType.values())
		{
			if(wt.toString().equals(selectedText)) return wt;
		}
		return null;
	}
	

	private void setTab(LightFieldType lightFieldType)
	{
		int i;
		for(i=0; i<lightFieldTabbedPane1.getTabCount(); i++)
		{
			if(lightFieldTabbedPane1.getTitleAt(i).equals(lightFieldType.getDescription1())) lightFieldTabbedPane1.setSelectedIndex(i);
		}
		
		// deal with second tabbed pane, if required
		if(lightFieldType.getDescription2() == null) return;
		
		for(i=0; i<lightFieldTabbedPane2.getTabCount(); i++)
		{
			if(lightFieldTabbedPane2.getTitleAt(i).equals(lightFieldType.getDescription2())) lightFieldTabbedPane2.setSelectedIndex(i);
		}		
	}
	
	private LightFieldType getLightFieldType()
	{
		String selectedText1 = lightFieldTabbedPane1.getTitleAt(lightFieldTabbedPane1.getSelectedIndex());
		String selectedText2 = lightFieldTabbedPane2.getTitleAt(lightFieldTabbedPane2.getSelectedIndex());
		for(LightFieldType lt : LightFieldType.values())
		{
			if(lt.getDescription1().equals(selectedText1))
			{
				if(
						(lt.getDescription2() == null) ||
						(lt.getDescription2().equals(selectedText2))
				)
				return lt;
			}
		}
		// TODO deal with second tabbed pane
		return null;
	}
	
	/**
	 * called before rendering;
	 * override when adding fields
	 */
	@Override
	protected void acceptValuesInInteractiveControlPanel()
	{
		super.acceptValuesInInteractiveControlPanel();
		
		studioInitialisation = (StudioInitialisationType)(studioInitialisationComboBox.getSelectedItem());

		lightFieldType = getLightFieldType();
		switch(lightFieldType)
		{
		case FIELD_FROM_POINT:
		case COLLIMATED_RAYS:
		case VORTEX_RAYS:
			lightRayFieldFuzzinessRad = MyMath.deg2rad(fieldFromPointFuzzinessDegPanel.getNumber());
			switch(lightFieldType)
			{
			case FIELD_FROM_POINT:
				fieldFromPointPosition = fieldFromPointPositionPanel.getVector3D();
				break;
			case VORTEX_RAYS:
				pointOnVortexLine = pointOnVortexLinePanel.getVector3D(); 
				normalisedVortexLineDirection = normalisedVortexLineDirectionPanel.getVector3D();
				mOverK = mOverKPanel.getNumber(); 
				break;
			case COLLIMATED_RAYS:
			default:
				collimatedLightRaysDirection = collimatedLightRaysDirectionPanel.getVector3D();
			}
			break;
		case POINT_OBJECT:
		default:
			pointObjectPosition = pointObjectPositionPanel.getVector3D();
			pointObjectSphereRadius = pointObjectSphereRadiusPanel.getNumber();
		}
		lightRayFieldColour = lightRayFieldColourPanel.getDoubleColour();
		
		windowType  = getWindowType();
		// switch(windowTabbedPane.getSelectedIndex())
		switch(windowType)
		{
		// case 0:
		case RR_SHEET:
			// windowType = WindowType.RR_SHEET;
			rrAngleDeg = rrAngleDegPanel.getNumber();
			break;
		case TRANSPARENT:
		default:
		}
		setWindow();
		windowTransmissionCoefficient = windowTransmissionCoefficientPanel.getNumber();

		delta = deltaPanel.getNumber();

		//  studioInitialisation = (StudioInitialisationType)(studioInitialisationComboBox.getSelectedItem());
		
		// cameraViewDirection = cameraViewDirectionPanel.getVector3D();
		cameraDistance = cameraDistancePanel.getNumber();
		cameraViewDirection = cameraViewDirectionPanel.getVector3D();
		cameraViewCentre = cameraViewCentrePanel.getVector3D();
		cameraHorizontalFOVDeg = cameraHorizontalFOVDegPanel.getNumber();
		cameraApertureSize = (ApertureSizeType)(cameraApertureSizeComboBox.getSelectedItem());
		cameraFocussingDistance = cameraFocussingDistancePanel.getNumber();
	}

	/**
	 * @param position
	 * @return ray direction in the light-ray field at the given position, *after* transmission through the window
	 */
	public Vector3D calculateRayDirectionInFieldAt(Vector3D position)
	{
		Vector3D rayDirectionBeforeTransmissionThroughWindow;
		switch(lightFieldType)
		{
		case FIELD_FROM_POINT:
		case COLLIMATED_RAYS:
		case VORTEX_RAYS:
			lightRayFieldFuzzinessRad = MyMath.deg2rad(fieldFromPointFuzzinessDegPanel.getNumber());
			switch(lightFieldType)
			{
			case FIELD_FROM_POINT:
				rayDirectionBeforeTransmissionThroughWindow = new LightRayFieldRepresentingPointLightSource(
						DoubleColour.RED,	// colour, 
						fieldFromPointPosition,	// position, 
						false,	// raysTowardsPosition, 
						lightRayFieldFuzzinessRad,	// angular fuzziness
						true	// bidirectional
					).getNormalisedLightRayDirection(new RaySceneObjectIntersection(position, null, 0));
				break;
			case VORTEX_RAYS:
				rayDirectionBeforeTransmissionThroughWindow = new LightRayFieldRepresentingParaxialOpticalVortex(
						lightRayFieldColour,	// colour 
						pointOnVortexLine, 
						normalisedVortexLineDirection,
						mOverK, 
						lightRayFieldFuzzinessRad,
						false	// bidirectional
					).getNormalisedLightRayDirection(new RaySceneObjectIntersection(position, null, 0));
				break;
			case COLLIMATED_RAYS:
			default:
				rayDirectionBeforeTransmissionThroughWindow = new LightRayFieldCollimated(
						lightRayFieldColour,	// colour
						collimatedLightRaysDirection,	// normalisedLightRayDirection, 
						lightRayFieldFuzzinessRad,	// angularFuzzinessRad
						true	// bidirectional
					).getNormalisedLightRayDirection(new RaySceneObjectIntersection(position, null, 0));
			}
			break;		
		case POINT_OBJECT:
		default:
			rayDirectionBeforeTransmissionThroughWindow = Vector3D.difference(position, pointObjectPosition);
			break;
		}
		
		Vector3D direction = null;
		try {
			direction = windowSurfaceProperty.getOutgoingLightRayDirection(
					new Ray(position, rayDirectionBeforeTransmissionThroughWindow, 0, false), 
					new RaySceneObjectIntersection(position, window.getPane(), 0), 
					null, null, 100, null
				).getNormalised();
		} catch (RayTraceException e) {
			e.printStackTrace();
		}
		
		//  make sure the z component of the returned direction is negative
		if(direction.z > 0) return direction.getReverse();
		return direction;
	}
	
	Vector3D makeZNegative(Vector3D v)
	{
		if(v.z > 0) return v.getReverse();
		return v;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		super.actionPerformed(e);

		NumberFormat nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(8);
		
		if(e.getSource().equals(calculateButton))
		{
			acceptValuesInInteractiveControlPanel();

			String s = "Light-ray direction at...";

			// light-ray direction that hits reference position
			Vector3D d0 = calculateRayDirectionInFieldAt(referencePosition);
			//						windowSurfaceProperty.getOutgoingLightRayDirection(
			//						new Ray(lightRayFieldPointSourcePosition, Vector3D.difference(referencePosition, lightRayFieldPointSourcePosition), 0, false), 
			//						new RaySceneObjectIntersection(referencePosition, window.getPane(), 0), 
			//						null, null, 100, null
			//					).getNormalised();
			s += "\n  Reference position: " + d0;

			Vector3D rightPosition = Vector3D.sum(referencePosition, new Vector3D(delta,  0, 0));
			Vector3D dR = calculateRayDirectionInFieldAt(rightPosition);
			//						windowSurfaceProperty.getOutgoingLightRayDirection(
			//						new Ray(lightRayFieldPointSourcePosition, Vector3D.difference(rightPosition, lightRayFieldPointSourcePosition), 0, false), 
			//						new RaySceneObjectIntersection(rightPosition, window.getPane(), 0), 
			//						null, null, 100, null
			//					).getNormalised();
			s += "\n  Right measurement position: " + dR;

			Vector3D topPosition = Vector3D.sum(referencePosition, new Vector3D(0, delta, 0));
			Vector3D dT = calculateRayDirectionInFieldAt(topPosition);
			//					windowSurfaceProperty.getOutgoingLightRayDirection(
			//						new Ray(lightRayFieldPointSourcePosition, Vector3D.difference(topPosition, lightRayFieldPointSourcePosition), 0, false), 
			//						new RaySceneObjectIntersection(topPosition, window.getPane(), 0), 
			//						null, null, 100, null
			//					).getNormalised();
			s += "\n  Top measurement position: " + dT;

			// calculate derivatives
			s += "\nd d'_x / d y = " + nf.format((dT.x - d0.x) / delta);
			s += "\nd d'_y / d x = " + nf.format((dR.y - d0.y) / delta);
			s += "\ncurl_z = " + nf.format(((dR.y - d0.y) / delta - (dT.x - d0.x) / delta));

			measurementResults.setText(s);
		}
		else if(e.getSource().equals(alignImageWithReferencePositionButton))
		{
			acceptValuesInInteractiveControlPanel();

			Vector3D d = calculateRayDirectionInFieldAt(referencePosition);
			cameraViewDirectionPanel.setVector3D(d.getReverse());
			cameraViewCentrePanel.setVector3D(referencePosition);
		}
		else if(e.getSource().equals(alignImageWithRightMeasurementPositionButton))
		{
			acceptValuesInInteractiveControlPanel();

			Vector3D rightPosition = Vector3D.sum(referencePosition, new Vector3D(delta,  0, 0));
			Vector3D d = calculateRayDirectionInFieldAt(rightPosition);
			cameraViewDirectionPanel.setVector3D(d.getReverse());
			cameraViewCentrePanel.setVector3D(rightPosition);
		}
		else if(e.getSource().equals(alignImageWithTopMeasurementPositionButton))
		{
			acceptValuesInInteractiveControlPanel();

			Vector3D topPosition = Vector3D.sum(referencePosition, new Vector3D(0, delta, 0));
			Vector3D d = calculateRayDirectionInFieldAt(topPosition);
			cameraViewDirectionPanel.setVector3D(d.getReverse());
			cameraViewCentrePanel.setVector3D(topPosition);
		}
	}


	/**
	 * The main method, required so that this class can run as a Java application
	 * @param args
	 */
	public static void main(final String[] args)
	{
//        Runnable r = new NonInteractiveTIM();
//
//        EventQueue.invokeLater(r);
		(new CurlVisualiser()).run();
	}
}
