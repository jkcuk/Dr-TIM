package optics.raytrace.test;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import math.*;
import net.miginfocom.swing.MigLayout;
import optics.raytrace.sceneObjects.Parallelepiped2;
import optics.raytrace.sceneObjects.Plane;
import optics.raytrace.sceneObjects.TimHead;
import optics.raytrace.sceneObjects.TimHeadWithSpecs;
import optics.raytrace.sceneObjects.TimHeadWithSpecs.FrameType;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.surfaces.SurfaceColour;
import optics.raytrace.surfaces.SurfaceOfRefractiveViewRotator;
import optics.raytrace.surfaces.Transparent;
import optics.raytrace.core.*;
import optics.raytrace.exceptions.SceneException;
import optics.raytrace.research.viewRotation.RefractiveViewRotator;
import optics.raytrace.NonInteractiveTIMEngine;
import optics.raytrace.GUI.lowLevel.ApertureSizeType;
import optics.raytrace.GUI.lowLevel.DoublePanel;
import optics.raytrace.GUI.lowLevel.GUIBitsAndBobs;
import optics.raytrace.GUI.lowLevel.LabelledVector3DPanel;



/**
 * Testing the Tim head with specs.
 *  
 * @author Maik
 */
public class TimHeadWithSpecsTest extends NonInteractiveTIMEngine
{	

	// units TODO add them maybe? or not necessary?
	public static double NM = 1e-9;
	public static double UM = 1e-6;
	public static double MM = 1e-3;
	public static double CM = 1e-2;
	public static double M = 1;
	/**
	 * View rotator
	 */
	TimHeadWithSpecs timHead;

	/**
	 * Params for tim head
	 */

	private Vector3D centre;
	private Vector3D frontDirection;
	private Vector3D topDirection;
	private Vector3D rightDirection;
	private double radius;
	private FrameType frameType;
	private SurfaceColour frameColour;
	private Parallelepiped2 boundingBox;
	private Plane specPlane;

	//	"Bounding box of refractive view rotator",	// description
	//	boundingBoxCentre,	// centre
	//	boundingBoxSpanVector1,	// u
	//	boundingBoxSpanVector2,	// v
	//	boundingBoxSpanVector3,	// w
	//	null,	// surfaceProperty -- for now; will be set in a second
	//	parent,
	//	studio
	//);
	//// ... then set its surface property to be a suitable refractive lenslet array
	//surface = new SurfaceOfRefractiveViewRotator(
	//	ocularPlaneNormal,
	//	eyePosition,
	//	ocularPlaneCentre,
	//	rotationAngle,
	//	rotationAxisDirection,
	//	magnificationFactor,
	//	periodVector1,
	//	periodVector2,
	//	viewObject,
	//	refractiveIndex,
	//	wedgeThickness,
	//	surfaceTransmissionCoefficient,
	//	simulateDiffractionBlur,
	//	MaxStepsInArray,	// maxStepsInArray
	//	this,	// bounding box
	//	scene
	//);
	//setSurfaceProperty(surface);


	/**
	 * camera
	 */
	private double cameraRotation;

	private enum SomeColours
	{			WHITE_MATT ("White matt "),//,SurfaceColour.WHITE_MATT),
		WHITE_SHINY ("White shiny "),
		WHITER_MATT ("Whiter matt "),
		WHITER_SHINY ("Whiter shiny "),
		BLACK_MATT ("Black matt "),
		BLACK_SHINY ("Black shiny "),
		GREY50_MATT ("Grey(50) matt "),
		GREY50_SHINY ("Grey(50) shiny "),
		RED_MATT ("Red matt "),
		RED_SHINY ("Red shiny "),
		BLUE_MATT ("Blue matt "),
		BLUE_SHINY ("Blue shiny "),
		GREEN_MATT ("Green matt "),
		GREEN_SHINY ("Green shiny "),
		LIGHT_BLUE_MATT ("Light blue matt "),
		LIGHT_BLUE_SHINY ("Light blue shiny "),
		DARK_BLUE_MATT ("Dark blue matt "),
		DARK_BLUE_SHINY ("Dark blue shiny "),
		LIGHT_RED_MATT ("Light red matt "),
		LIGHT_RED_SHINY ("Light red shiny "),
		DARK_RED_MATT ("Dark red matt "),
		DARK_RED_SHINY ("Dark red shiny "),
		YELLOW_MATT ("Yellow matt "),
		YELLOW_SHINY ("Yellow shiny "),
		CYAN_MATT ("Cyan matt "),
		CYAN_SHINY ("Cyan shiny "),
		BROWN_MATT ("Brown matt "),
		BROWN_SHINY ("Brown shiny "),
		PURPLE_MATT ("Purple matt "),
		PURPLE_SHINY ("Purple shiny ");

		//private SurfaceColour colour;
		private String description;
		private SomeColours(String description) {this.description = description;}	
		//private SomeColours(String description, SurfaceColour colour) {this.description = description; this.colour=colour;}	
		@Override
		public String toString() {return description;}
	}

	private SomeColours colour;



	public TimHeadWithSpecsTest()
	{
		super();

		/**
		 * Setting the paramterers for tims head
		 */
		centre = new Vector3D(0,0,0);
		frontDirection = Vector3D.Z.getProductWith(-1);
		topDirection = Vector3D.Y;
		rightDirection = Vector3D.X.getProductWith(-1);
		radius = 1;
		frameType = FrameType.NOTHING;
		colour = SomeColours.DARK_BLUE_MATT;
		frameColour = SurfaceColour.DARK_BLUE_MATT;


		// camera params
		cameraRotation = 0;
		cameraDistance = 5;
		cameraViewCentre = new Vector3D(0, 0, 0);
		cameraViewDirection = new Vector3D(0, 0, 1);
		cameraTopDirection = new Vector3D(0,1,0);
		cameraHorizontalFOVDeg = 40;
		cameraApertureSize = ApertureSizeType.PINHOLE;
		cameraFocussingDistance = 5;

		windowTitle = "Dr TIM's tim test";
		windowWidth = 1450;
		windowHeight = 750;

		//		"Bounding box of refractive view rotator",	// description
		//		boundingBoxCentre,	// centre
		//		boundingBoxSpanVector1,	// u
		//		boundingBoxSpanVector2,	// v
		//		boundingBoxSpanVector3,	// w
		//		null,	// surfaceProperty -- for now; will be set in a second
		//		parent,
		//		studio

	}

	public void populateStudio()
			throws SceneException
	{
		// System.out.println("LensCloakVisualiser::populateStudio: frame="+frame);

		// the studio
		studio = new Studio();


		//setting the scene
		SceneObjectContainer scene = new SceneObjectContainer("the scene", null, studio);		

		// the standard scene objects
		scene.addSceneObject(SceneObjectClass.getLighterChequerboardFloor(-1, scene, studio)); //the floor
		scene.addSceneObject(SceneObjectClass.getSkySphere(scene, studio));	// the sky	


		switch(colour) {
		case WHITE_MATT:
			frameColour = SurfaceColour.WHITE_MATT;
			break;
		case WHITE_SHINY:
			frameColour = SurfaceColour.WHITE_SHINY;
			break;
		case WHITER_MATT:
			frameColour = SurfaceColour.WHITER_MATT;
			break;
		case WHITER_SHINY:
			frameColour = SurfaceColour.WHITER_SHINY;
			break;
		case BLACK_MATT:
			frameColour = SurfaceColour.BLACK_MATT;
			break;
		case BLACK_SHINY:
			frameColour = SurfaceColour.BLACK_SHINY;
			break;
		case GREY50_MATT:
			frameColour = SurfaceColour.GREY50_MATT;
			break;
		case GREY50_SHINY:
			frameColour = SurfaceColour.GREY50_SHINY;
			break;
		case RED_MATT:
			frameColour = SurfaceColour.RED_MATT;
			break;
		case RED_SHINY:
			frameColour = SurfaceColour.RED_SHINY;
			break;
		case BLUE_MATT:
			frameColour = SurfaceColour.BLUE_MATT;
			break;
		case BLUE_SHINY:
			frameColour = SurfaceColour.BLUE_SHINY;
			break;
		case GREEN_MATT:
			frameColour = SurfaceColour.GREEN_MATT;
			break;
		case GREEN_SHINY:
			frameColour = SurfaceColour.GREEN_SHINY;
			break;
		case LIGHT_BLUE_MATT:
			frameColour = SurfaceColour.LIGHT_BLUE_MATT;
			break;
		case LIGHT_BLUE_SHINY:
			frameColour = SurfaceColour.LIGHT_BLUE_SHINY;
			break;
		case DARK_BLUE_MATT:
			frameColour = SurfaceColour.DARK_BLUE_MATT;
			break;
		case DARK_BLUE_SHINY:
			frameColour = SurfaceColour.DARK_BLUE_SHINY;
			break;
		case LIGHT_RED_MATT:
			frameColour = SurfaceColour.LIGHT_RED_MATT;
			break;
		case LIGHT_RED_SHINY:
			frameColour = SurfaceColour.LIGHT_RED_SHINY;
			break;
		case DARK_RED_MATT:
			frameColour = SurfaceColour.DARK_RED_MATT;
			break;
		case DARK_RED_SHINY:
			frameColour = SurfaceColour.DARK_RED_SHINY;
			break;
		case YELLOW_MATT:
			frameColour = SurfaceColour.YELLOW_MATT;
			break;
		case YELLOW_SHINY:
			frameColour = SurfaceColour.YELLOW_SHINY;
			break;
		case CYAN_MATT:
			frameColour = SurfaceColour.CYAN_MATT;
			break;
		case CYAN_SHINY:
			frameColour = SurfaceColour.CYAN_SHINY;
			break;
		case BROWN_MATT:
			frameColour = SurfaceColour.BROWN_MATT;
			break;
		case BROWN_SHINY:
			frameColour = SurfaceColour.BROWN_SHINY;
			break;
		case PURPLE_MATT:
			frameColour = SurfaceColour.PURPLE_MATT;
			break;
		case PURPLE_SHINY:
			frameColour = SurfaceColour.PURPLE_SHINY;
			break;		
		}

		RefractiveViewRotator leftSpec = new RefractiveViewRotator(
				"Bounding box of refractive view rotator",	// description
				TimHeadWithSpecs.getLeftSpecCentre(new TimHead(
						"Tim head for calculation", 
						centre, 
						radius, 
						frontDirection, 
						topDirection, 
						rightDirection, 
						null, 
						null
						)),
				rightDirection,
				topDirection,	// v
				frontDirection.getProductWith(1.5*MM),	// w

				frontDirection.getProductWith(-1),//ocularPlaneNormal,
				TimHeadWithSpecs.getLeftSpecCentre(new TimHead(
						"Tim head for calculation", 
						centre, 
						radius, 
						frontDirection, 
						topDirection, 
						rightDirection, 
						null, 
						null
						)).getSumWith(frontDirection.getProductWith(-1.64999*CM)),//eyePosition,
				TimHeadWithSpecs.getLeftSpecCentre(new TimHead(
						"Tim head for calculation", 
						centre, 
						radius, 
						frontDirection, 
						topDirection, 
						rightDirection, 
						null, 
						null
						)).getSumWith(frontDirection.getProductWith(-1.4999*MM)),//ocularPlaneCentre,
				20,//rotationAngle,
				new Vector3D(0,0,-1), //rotation axis direction,rotationAxisDirection,
				1,//magnificationFactor,
				new Vector3D(0.5,0,0).getProductWith(MM),//periodVector1,
				new Vector3D(0,0.5,0).getProductWith(MM),//periodVector2,
				new Plane("focus plane", 
						new Vector3D(0, 0, -5), 
						Vector3D.Z.getProductWith(-1), 
						null,
						null, 
						null
						),//viewObject,
				1.5,//refractiveIndex,
				1*MM, //wedgeThickness
				0.9,//surfaceTransmissionCoefficient,
				true,//simulateDiffractionBlur,
				20,	// maxStepsInArray
				// surfaceProperty -- for now; will be set in a second
				scene,
				scene,
				studio
				);






		boundingBox = 	new Parallelepiped2(
				"Bounding box of refractive view rotator",	// description
				TimHeadWithSpecs.getLeftSpecCentre(new TimHead(
						"Tim head for calculation", 
						centre, 
						radius, 
						frontDirection, 
						topDirection, 
						rightDirection, 
						null, 
						null
						)),
				rightDirection,
				topDirection,	// v
				frontDirection.getProductWith(1.5*MM),	// w
				new SurfaceOfRefractiveViewRotator(
						frontDirection.getProductWith(-1),//ocularPlaneNormal,
						TimHeadWithSpecs.getLeftSpecCentre(new TimHead(
								"Tim head for calculation", 
								centre, 
								radius, 
								frontDirection, 
								topDirection, 
								rightDirection, 
								null, 
								null
								)).getSumWith(frontDirection.getProductWith(-1.64999*CM)),//eyePosition,
						TimHeadWithSpecs.getLeftSpecCentre(new TimHead(
								"Tim head for calculation", 
								centre, 
								radius, 
								frontDirection, 
								topDirection, 
								rightDirection, 
								null, 
								null
								)).getSumWith(frontDirection.getProductWith(-1.4999*MM)),//ocularPlaneCentre,
						20,//rotationAngle,
						new Vector3D(0,0,-1), //rotation axis direction,rotationAxisDirection,
						1,//magnificationFactor,
						new Vector3D(0.5,0,0).getProductWith(MM),//periodVector1,
						new Vector3D(0,0.5,0).getProductWith(MM),//periodVector2,
						new Plane("focus plane", 
								new Vector3D(0, 0, -5), 
								Vector3D.Z.getProductWith(-1), 
								null,
								null, 
								null
								),//viewObject,
						1.5,//refractiveIndex,
						1*MM, //wedgeThickness
						0.9,//surfaceTransmissionCoefficient,
						true,//simulateDiffractionBlur,
						300,	// maxStepsInArray
						boundingBox,	// bounding box
						null
						),	// surfaceProperty -- for now; will be set in a second
				scene,
				studio
				);
		specPlane = new Plane(
				"right spec plane",
				TimHeadWithSpecs.getRightSpecCentre(new TimHead(
						"Tim head for calculation", 
						centre, 
						radius, 
						frontDirection, 
						topDirection, 
						rightDirection, 
						null, 
						null
						)),
				frontDirection,
				new Transparent(0.9, false),
				scene,
				studio
				);

		//Tim head
		timHead = new TimHeadWithSpecs(
				"new test subject" , 
				centre,
				radius, 
				frontDirection, 
				topDirection, 
				rightDirection, 
				frameType,
				frameColour,
//				new Transparent(0.15, true),
//								Transparent.SLIGHTLY_ABSORBING, //left
//								Transparent.SLIGHTLY_ABSORBING, //left
//								SurfaceColour.GREY20_SHINY, //left
//								SurfaceColour.GREY20_SHINY, //right
//				leftSpec,
				specPlane,
				specPlane,
				//new Transparent(0.15, true),
				scene, 
				studio);
		scene.addSceneObject(timHead);


		// the camera

		cameraTopDirection = new Vector3D(Math.sin(Math.toRadians(cameraRotation)),Math.cos(Math.toRadians(cameraRotation)),0);
		studio.setCamera(getStandardCamera());		
		studio.setScene(scene);	
		studio.setLights(LightSource.getStandardLightsFromBehind());

	}
	//Tim Stuff
	private LabelledVector3DPanel centrePanel, frontDirectionPanel, topDirectionPanel, rightDirectionPanel;
	private DoublePanel radiusPanel;
	private JComboBox<FrameType> frameTypeComboBox;
	private JComboBox<SomeColours>colourComboBox;

	// camera stuff
	private DoublePanel cameraDistancePanel, cameraRotationPanel;
	private LabelledVector3DPanel cameraViewDirectionPanel;
	private DoublePanel cameraHorizontalFOVDegPanel;
	private JComboBox<ApertureSizeType> cameraApertureSizeComboBox;
	private DoublePanel cameraFocussingDistancePanel;


	protected void createInteractiveControlPanel()
	{
		super.createInteractiveControlPanel();

		// the main tabbed pane, with "Scene" and "Camera" tabs
		JTabbedPane sceneCameraTabbedPane = new JTabbedPane();
		interactiveControlPanel.add(sceneCameraTabbedPane, "span");


		//
		// the component panel
		//

		JPanel scenePanel = new JPanel();
		scenePanel.setLayout(new MigLayout("insets 0"));
		sceneCameraTabbedPane.addTab("Scene", scenePanel);

		radiusPanel = new DoublePanel();
		radiusPanel.setNumber(radius);
		scenePanel.add(GUIBitsAndBobs.makeRow("Head Radius",radiusPanel ),"span");

		centrePanel = new LabelledVector3DPanel("Head Centre");
		centrePanel.setVector3D(centre);
		scenePanel.add(centrePanel, "span");

		frontDirectionPanel = new LabelledVector3DPanel("Front direction");
		frontDirectionPanel.setVector3D(frontDirection);
		scenePanel.add(frontDirectionPanel, "span");

		topDirectionPanel = new LabelledVector3DPanel("Top direction");
		topDirectionPanel.setVector3D(topDirection);
		scenePanel.add(topDirectionPanel, "span");

		rightDirectionPanel = new LabelledVector3DPanel("Right direction");
		rightDirectionPanel.setVector3D(rightDirection);
		scenePanel.add(rightDirectionPanel, "span");

		frameTypeComboBox = new JComboBox<FrameType>(FrameType.values());
		frameTypeComboBox.setSelectedItem(frameType);
		scenePanel.add(GUIBitsAndBobs.makeRow("Frame type", frameTypeComboBox), "span");	


		colourComboBox = new JComboBox<SomeColours>(SomeColours.values());
		colourComboBox.setSelectedItem(colour);
		scenePanel.add(GUIBitsAndBobs.makeRow("Frame colour", colourComboBox), "span");

		//
		// the Camera panel
		//

		JPanel cameraPanel = new JPanel();
		cameraPanel.setLayout(new MigLayout("insets 0"));
		sceneCameraTabbedPane.addTab("Camera", cameraPanel);


		cameraDistancePanel = new DoublePanel();
		cameraDistancePanel.setNumber(cameraDistance);
		cameraPanel.add(GUIBitsAndBobs.makeRow("Camera distance",cameraDistancePanel ),"span");

		cameraRotationPanel = new DoublePanel();
		cameraRotationPanel.setNumber(cameraRotation);
		cameraPanel.add(GUIBitsAndBobs.makeRow("Camera/eye rotation",cameraRotationPanel,"<html>&deg;</html>,"),"span"); 

		cameraViewDirectionPanel = new LabelledVector3DPanel("View direction");
		cameraViewDirectionPanel.setVector3D(cameraViewDirection);
		cameraPanel.add(cameraViewDirectionPanel, "span");

		cameraHorizontalFOVDegPanel = new DoublePanel();
		cameraHorizontalFOVDegPanel.setNumber(cameraHorizontalFOVDeg);
		cameraPanel.add(GUIBitsAndBobs.makeRow("Horizontal FOV", cameraHorizontalFOVDegPanel, "°"), "span");

		cameraApertureSizeComboBox = new JComboBox<ApertureSizeType>(ApertureSizeType.values());
		cameraApertureSizeComboBox.setSelectedItem(cameraApertureSize);
		cameraPanel.add(GUIBitsAndBobs.makeRow("Camera aperture", cameraApertureSizeComboBox), "span");		

		cameraFocussingDistancePanel = new DoublePanel();
		cameraFocussingDistancePanel.setNumber(cameraFocussingDistance);
		cameraPanel.add(GUIBitsAndBobs.makeRow("Focussing distance",cameraFocussingDistancePanel));



	}

	protected void acceptValuesInInteractiveControlPanel()
	{
		//head stuff
		centre = centrePanel.getVector3D();
		frontDirection = frontDirectionPanel.getVector3D();
		topDirection = topDirectionPanel.getVector3D();
		rightDirection = rightDirectionPanel.getVector3D();
		radius = radiusPanel.getNumber();
		frameType = (FrameType)(frameTypeComboBox.getSelectedItem());
		colour = (SomeColours)(colourComboBox.getSelectedItem());

		//Camera and general stuff
		cameraDistance = cameraDistancePanel.getNumber();
		cameraRotation = cameraRotationPanel.getNumber();
		cameraHorizontalFOVDeg = cameraHorizontalFOVDegPanel.getNumber();
		cameraViewDirection = cameraViewDirectionPanel.getVector3D();
		cameraApertureSize = (ApertureSizeType)(cameraApertureSizeComboBox.getSelectedItem());
		cameraFocussingDistance = cameraFocussingDistancePanel.getNumber();		
	}


	public static void main(final String[] args)
	{
		(new TimHeadWithSpecsTest()).run();
	}
}

