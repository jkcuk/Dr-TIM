package optics.raytrace.test;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

import math.*;
import net.miginfocom.swing.MigLayout;
import optics.raytrace.sceneObjects.*;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.simplicialComplex.LensType;
import optics.raytrace.surfaces.SurfaceColour;
import optics.raytrace.surfaces.SurfaceColourLightSourceIndependent;
import optics.raytrace.cameras.PinholeCamera.ExposureCompensationType;
import optics.raytrace.core.*;
import optics.raytrace.exceptions.SceneException;
import optics.DoubleColour;
import optics.raytrace.NonInteractiveTIMActionEnum;
import optics.raytrace.NonInteractiveTIMEngine;
import optics.raytrace.GUI.cameras.EditableRelativisticAnyFocusSurfaceCamera;
import optics.raytrace.GUI.cameras.RenderQualityEnum;
import optics.raytrace.GUI.lowLevel.ApertureSizeType;
import optics.raytrace.GUI.lowLevel.GUIBitsAndBobs;
import optics.raytrace.GUI.lowLevel.IntPanel;
import optics.raytrace.GUI.lowLevel.LabelledDoublePanel;
import optics.raytrace.GUI.lowLevel.LabelledIntPanel;
import optics.raytrace.GUI.lowLevel.LabelledVector3DPanel;
import optics.raytrace.GUI.sceneObjects.EditableLensSimplicialComplex;
import optics.raytrace.GUI.sceneObjects.EditableRayTrajectory;
import optics.raytrace.GUI.sceneObjects.EditableTimHead;


/**
 * Testing the single ray trace properties of an ideal lens cloak to investigate closed loop trajectories. 
 *  
 * @author Maik
 */
public class IdealLensCloakRayTest extends NonInteractiveTIMEngine implements ActionListener
{	
	
	//cloak stuff
	EditableLensSimplicialComplex cloak, cloakFrame;
	private double baseFocal; //base lens focal length 
	private double h1P; // Height to lower inner vertex in physical space
	private double h2P; //Height to upper inner vertex in physical space
	private double h; //over all height of cloak
	private double r; //base radius
	private LensType lensType;
	private double lensTrans; 
	private boolean CloakFrame; 
	private double cloakRotationAngle;

	
	//ray trace stuff
	private boolean showTrajectory;
	private Vector3D trajectoryDefaultDirection; //defines the default ray direction
	private Vector3D rayAim; // defines the direction when a position of where to look is inputed
	private boolean manualRayDirection;//defines which direction should be set for the ray tracing 
	private double rayAngle;// sets the angle of the ray location, in equal to camera sets it to camera position
	private double rayUpAngle;
	private Vector3D rayPos; //ray trace starting position 
	private Vector3D rayDirection; // raytrace direction
	
	
	//camera stuff
	private double cameraAngle; // the angle at which the camera will face towards (0,0,0), + to go left - to go right 
	private double cameraUpAngle; //define an angle with which the camera will view the origin
	private double cameraFOV; //set the focus and thus zoom of camera	
	private double cameraDistance; //set camera distance from (0,0,0) 
	private int maxSteps;
	
	//movie stuff
	private double startAngleCloak, stopAngleCloak, startAngleRay, stopAngleRay, startAngleCamera, stopAngleCamera;
	private double startUpAngleRay, stopUpAngleRay, startUpAngleCamera, stopUpAngleCamera;
	public enum MovieType
	{
		MOVING_RAY("Ray moves around the cloak"),
		ROTATING_CLOAK("Cloak roatates anticlockwise"),
		CAMERA_MOVING("Camera Moves");
		
		private String description;
		private MovieType(String description) {this.description = description;}	
		@Override
		public String toString() {return description;}
		
	}

	private MovieType movieType;
	

	
	public IdealLensCloakRayTest()
	{
		super();
			
		
		//cloak stuff
		CloakFrame = false; //shows the frame of the cloak
		baseFocal = 1;
		lensType = LensType.IDEAL_THIN_LENS; //set to ideal thin lens by default
		lensTrans = 1; //sets transmission coef of lens to 1 (default)
		h = 3;//over all height of cloak
		h1P = 0.7; // Height to lower inner vertex in physical space
		h2P = 0.9; //Height to upper inner vertex in physical space
		r = h/2; //base radius
		cloakRotationAngle = 0;
		
		//ray trace stuff
		traceRaysWithTrajectory = false; 
		manualRayDirection = false;
		showTrajectory = false;
		trajectoryDefaultDirection = new Vector3D(0,0,1);//sets the manual direction of the ray
		rayAim = new Vector3D(0, 0, 0);//sets the position the ray should go towards
		rayAngle = 0; //setting the ray angle to the camera angle. 
		rayUpAngle = 0;

		// camera params
		cameraAngle = 0;
		cameraUpAngle = 0;
		cameraFOV = 20;
		cameraDistance = 10;
		maxSteps = 50;
		
		//movie stuff
		movie = false;
		numberOfFrames = 10;
		movieType = MovieType.ROTATING_CLOAK;
		startAngleCloak = 0;
		stopAngleCloak = 0;
		
		startAngleRay = 0;
		stopAngleRay = 0;
		startUpAngleRay = 0;
		stopUpAngleRay = 0;
		
		startAngleCamera = 0;
		stopAngleCamera = 0;
		startUpAngleCamera = 0;
		stopUpAngleCamera = 0;
		
		//Tim engine setup
				renderQuality = RenderQualityEnum.DRAFT;//Set the default render quality		
				nonInteractiveTIMAction = NonInteractiveTIMActionEnum.INTERACTIVE;// set to true for interactive version
				if(nonInteractiveTIMAction == NonInteractiveTIMActionEnum.INTERACTIVE)
				{
					windowTitle = "Testing an ideal lens cloak with single ray tracing";
					windowWidth = 1500;
					windowHeight = 650;
				}
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
		scene.addSceneObject(SceneObjectClass.getHeavenSphere(scene, studio)); //the floor
		//scene.addSceneObject(SceneObjectClass.getChequerboardFloor(scene, studio)); //the floor
		//scene.addSceneObject(SceneObjectClass.getSkySphere(scene, studio));	// the sky	
	
		
		Vector3D baseVertex = new Vector3D(r*Math.cos(Math.toRadians(cloakRotationAngle-90)), 2-h/2, r*Math.sin(Math.toRadians(cloakRotationAngle-90)));
		if(movie) {
			
			switch(movieType)
			{
			case ROTATING_CLOAK:
				double partialRotationAngle = startAngleCloak+(stopAngleCloak-startAngleCloak)*frame/numberOfFrames;
				baseVertex = new Vector3D(r*Math.cos(Math.toRadians(partialRotationAngle)), 2-h/2, r*Math.sin(Math.toRadians(partialRotationAngle)));
				break;
			case MOVING_RAY:
				rayAngle = startAngleRay+(stopAngleRay-startAngleRay)*frame/numberOfFrames;
				rayUpAngle = startUpAngleRay+(stopUpAngleRay-startUpAngleRay)*frame/numberOfFrames;
				break;
			case CAMERA_MOVING:
				cameraAngle = startAngleCamera+(stopAngleCamera-startAngleCamera)*frame/numberOfFrames;
				cameraUpAngle = startUpAngleCamera+(stopUpAngleCamera-startUpAngleCamera)*frame/numberOfFrames;
				break;
				
			}
		}
		
		//adding the lens cloak
		
		double frameRadius = 0.005; // radius of cloak frame
		
		
		cloak = new EditableLensSimplicialComplex(
				"outer Abyss cloak",	// description
				scene,	// parent
				studio
			);
		// ... and initialise it as an ideal-lens cloak  IDEAL_THIN_LENS
		cloak.setLensTypeRepresentingFace(lensType);
		cloak.setShowStructureP(CloakFrame);
		cloak.setVertexRadiusP(frameRadius);
		cloak.setShowStructureV(false);
		cloak.setVertexRadiusV(frameRadius);
		cloak.setSurfacePropertyP(new SurfaceColour(DoubleColour.BLUE, DoubleColour.WHITE, false));
		
		cloak.initialiseToOmnidirectionalLens(
				h1P,	// physicalSpaceFractionalLowerInnerVertexHeight
				h2P,	// physicalSpaceFractionalUpperInnerVertexHeight
				1./(-h/baseFocal + 1/h1P),	// virtualSpaceFractionalLowerInnerVertexHeightI,	// virtualSpaceFractionalLowerInnerVertexHeight
				new Vector3D(0, 2+h/2, 0),	// topVertex
				new Vector3D(0, 2-h/2, 0),	// baseCentre
				baseVertex	// baseVertex
			);
		scene.addSceneObject(cloak);
	



		//adding tims head to view through lens.
		scene.addSceneObject(new EditableTimHead(
				"Tim's head",	// description
				new Vector3D(0, 2, 11),//.getSumWith(new Vector3D(0.4,0.5,0)),
				0.2,	// radius
				new Vector3D(0, 0, -1),	// front direction
				new Vector3D(0, 1, 0),	// top direction
				new Vector3D(1, 0, 0),	// right direction
				scene,	// parent
				studio
				));



		/**
		 * setting up a camera
		 */

		int
		quality = 2,	// 1 = normal, 2 = good, 4 = great
		pixelsX = 640*quality,
		pixelsY = 480*quality;

		Vector3D cameraDirection = new Vector3D(-Math.sin(Math.toRadians(cameraAngle))*Math.cos(Math.toRadians(cameraUpAngle)), -Math.sin(Math.toRadians(cameraUpAngle)), Math.cos(Math.toRadians(cameraAngle))*Math.cos(Math.toRadians(cameraUpAngle)));
		Vector3D cameraApertureCentre	= new Vector3D(cameraDistance*Math.sin(Math.toRadians(cameraAngle))*Math.cos(Math.toRadians(cameraUpAngle)), 2+cameraDistance*Math.sin(Math.toRadians(cameraUpAngle)), -cameraDistance*Math.cos(Math.toRadians(cameraAngle))*Math.cos(Math.toRadians(cameraUpAngle)));

		EditableRelativisticAnyFocusSurfaceCamera camera = new EditableRelativisticAnyFocusSurfaceCamera(
				"camera",
				cameraApertureCentre,	// centre of aperture
				cameraDirection,	// viewDirection
				new Vector3D(0, 1, 0),	// top direction vector
				cameraFOV,	// horiontalViewAngle in degrees; 2*MyMath.rad2deg(Math.atan(2./10.)) gives same view angle as in previous version
				new Vector3D(0, 0, 0),	// beta
				pixelsX, pixelsY,	// logical number of pixels
				ExposureCompensationType.EC0,	// exposure compensation +0
				maxSteps,	// maxTraceLevel
				new Plane(
						"focus plane",	// description
						Vector3D.sum(cameraApertureCentre, cameraDirection.getWithLength(cameraFocussingDistance)),	// pointOnPlane
						cameraDirection,	// normal
						null,	// surfaceProperty
						null,	// parent
						null	// studio
						),	// focus scene
				null,	// cameraFrameScene,
				ApertureSizeType.PINHOLE,	// aperture size
				renderQuality.getBlurQuality(),	// blur quality
				renderQuality.getAntiAliasingQuality()	// anti-aliasing quality
				);
		//System.out.println("centre = "+centre);
		studio.setCamera(camera);
		
		studio.setScene(scene);
		
		studio.setLights(LightSource.getStandardLightsFromBehind());
		
		
		/*
		 * setting the ray tracing
		 */
		if(showTrajectory)
		{

			rayPos = new Vector3D (cameraDistance*Math.cos(Math.toRadians(rayUpAngle+90))*Math.sin(Math.toRadians(rayAngle)), 2+cameraDistance*Math.sin(Math.toRadians(rayUpAngle+90)),-cameraDistance*Math.cos(Math.toRadians(rayAngle))*Math.cos(Math.toRadians(rayUpAngle+90))  );//sets the 'automatic' position of the ray ;				
			if(manualRayDirection)
			{System.out.println(rayPos);
				rayDirection = Vector3D.difference(trajectoryDefaultDirection,rayPos);
			}
			else
			{RaySceneObjectIntersection i = raytracingImageCanvas.getLastClickIntersection();
			rayAim = i.p;
			rayDirection = Vector3D.difference(rayAim, rayPos);	
					
			}	
			
		
			
					// do the tracing of rays with trajectory
			scene.addSceneObject(
					new EditableRayTrajectory(
							"light-ray trajectory",	// description
							rayPos,	// startPoint
							0,	// startTime
							rayDirection,	// startDirection
							0.005,	// rayRadius
							SurfaceColourLightSourceIndependent.RED,	// surfaceProperty
							100,	// maxTraceLevel
							true,	// reportToConsole
							scene,	// parent
							studio
							)
					);

			studio.setScene(scene);

			// trace the rays with trajectory through the scene
			studio.traceRaysWithTrajectory();
			
			//remove all the objects and replace with semi transparent ones
			scene.removeSceneObject(cloak);			
			// create the outer cloak; first create a lens-simplicial complex...
						cloakFrame = new EditableLensSimplicialComplex(
								"outer Abyss cloak",	// description
								scene,	// parent
								studio
							);
						// ... and initialise it as an ideal-lens cloak  IDEAL_THIN_LENS
						cloakFrame.setLensTypeRepresentingFace(LensType.SEMITRANSPARENT_PLANE);
						cloakFrame.setShowStructureP(true);
						cloakFrame.setVertexRadiusP(frameRadius);
						cloakFrame.setShowStructureV(false);
						cloakFrame.setVertexRadiusV(frameRadius);
						cloakFrame.setSurfacePropertyP(new SurfaceColour(DoubleColour.DARK_BLUE, DoubleColour.GREY10, false));
						
						cloakFrame.initialiseToOmnidirectionalLens(
								h1P,	// physicalSpaceFractionalLowerInnerVertexHeight
								h2P,	// physicalSpaceFractionalUpperInnerVertexHeight
								1./(-h/baseFocal + 1/h1P),	// virtualSpaceFractionalLowerInnerVertexHeightI,	// virtualSpaceFractionalLowerInnerVertexHeight
								new Vector3D(0, 2+h/2, 0),	// topVertex
								new Vector3D(0, 2-h/2, 0),	// baseCentre
								baseVertex	// baseVertex1
							);
						scene.addSceneObject(cloakFrame);
		}	
		
	}
	//general and camera
	private LabelledDoublePanel cameraAnglePanel, lensTransPanel, cameraZoomPanel, cameraUpAnglePanel,cameraDistancePanel, cloakRotationAnglePanel;
	private LabelledIntPanel maxStepsPanel;
	private LabelledDoublePanel baseLensFPanel, rayAnglePanel, rayUpAnglePanel;
	private LabelledDoublePanel startAnglePanelCloak, stopAnglePanelCloak, 
	startAnglePanelRay, startUpAnglePanelRay, stopAnglePanelRay, stopUpAnglePanelRay,
	startAnglePanelCamera, startUpAnglePanelCamera, stopAnglePanelCamera, stopUpAnglePanelCamera;
	JCheckBox   CloakFrameCheck, showTrajectoryPanel,
	manualRayDirectionCheckBox, movieCheckBox;	
	private JTextArea rayLastClickTextArea;
	private JButton rayLastClickInfo;
	private JComboBox<LensType>  lensTypeComboBox;
	private LabelledVector3DPanel trajectoryDefaultDirectionPanel;
	private IntPanel numberOfFramesPanel, firstFramePanel, lastFramePanel;
	JTabbedPane movieTabbedPane;
	

	
	protected void createInteractiveControlPanel()
	{
		super.createInteractiveControlPanel();
		
		JTabbedPane tabbedPane = new JTabbedPane();
		interactiveControlPanel.add(tabbedPane, "span");
		
		
		//general panel
		
		JPanel generalpanel = new JPanel();
		generalpanel.setLayout(new MigLayout("insets 0"));
		tabbedPane.addTab("General", generalpanel);
		
		
		// cloak
		JPanel cloaksPanel = new JPanel();
		cloaksPanel.setLayout(new MigLayout("insets 1"));
		cloaksPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Cloak"));
		generalpanel.add(cloaksPanel, "wrap");


		CloakFrameCheck = new JCheckBox("Toggles the frames of the outer cloak");
		cloaksPanel.add(CloakFrameCheck, "span");

		baseLensFPanel = new LabelledDoublePanel("Focal length of base lens");
		baseLensFPanel.setNumber(baseFocal);
		cloaksPanel.add(baseLensFPanel, "span");
		
		lensTypeComboBox = new JComboBox<LensType>(LensType.values());
		lensTypeComboBox.setSelectedItem(lensType);
		cloaksPanel.add(GUIBitsAndBobs.makeRow("Lens type", lensTypeComboBox), "span");

		lensTransPanel = new LabelledDoublePanel("Lens transmission coefficient, set between 0-1, default = 1");
		lensTransPanel.setNumber(lensTrans);
		cloaksPanel.add(lensTransPanel, "span");
		
		cloakRotationAnglePanel = new LabelledDoublePanel("anti clockwise cloak rotation angle");
		cloakRotationAnglePanel.setNumber(cloakRotationAngle);
		cloaksPanel.add(cloakRotationAnglePanel, "span");
				
		
		
		//raytrace stuff

		JPanel rayPanel = new JPanel();
		rayPanel.setLayout(new MigLayout("insets 0"));
		rayPanel.setBorder(GUIBitsAndBobs.getTitledBorder("ray trace"));
		generalpanel.add(rayPanel, "wrap");

		showTrajectoryPanel = new JCheckBox("Show the trajectory a of  light ray");
		showTrajectoryPanel.setSelected(showTrajectory);
		rayPanel.add(showTrajectoryPanel, "span");

		//semi automatic parts
		rayAnglePanel = new LabelledDoublePanel("xz angle");
		rayAnglePanel.setNumber(rayAngle);
		rayPanel.add(rayAnglePanel, "span");
		
		
		rayUpAnglePanel = new LabelledDoublePanel("xy angle");
		rayUpAnglePanel.setNumber(rayUpAngle);
		rayPanel.add(rayUpAnglePanel, "span");

		rayLastClickTextArea = new JTextArea(2, 40);
		JScrollPane scrollPane = new JScrollPane(rayLastClickTextArea); 
		rayLastClickTextArea.setEditable(false);
		rayLastClickTextArea.setText("Click on the aim ray button set the aim of the light ray");
		rayLastClickInfo = new JButton("Aim ray");
		rayLastClickInfo.addActionListener(this);
		rayPanel.add(GUIBitsAndBobs.makeRow(scrollPane, rayLastClickInfo), "span");


		manualRayDirectionCheckBox = new JCheckBox("use manual ray direction");
		manualRayDirectionCheckBox.setSelected(manualRayDirection);
		rayPanel.add(manualRayDirectionCheckBox, "span");

		trajectoryDefaultDirectionPanel = new LabelledVector3DPanel("Point to be viewed");
		trajectoryDefaultDirectionPanel.setVector3D(trajectoryDefaultDirection);
		rayPanel.add(trajectoryDefaultDirectionPanel, "span");
		
		
		//camera stuff
		
		JPanel cameraPanel = new JPanel();
		cameraPanel.setLayout(new MigLayout("insets 0"));
		cameraPanel.setBorder(GUIBitsAndBobs.getTitledBorder("camera"));
		generalpanel.add(cameraPanel, "wrap");
		
		cameraAnglePanel = new LabelledDoublePanel("Angle of which the camera is looking at origin");
		cameraAnglePanel.setNumber(cameraAngle);
		cameraPanel.add(cameraAnglePanel, "span");
		
		cameraUpAnglePanel = new LabelledDoublePanel("Angle of which the camera is looking down at the origin");
		cameraUpAnglePanel.setNumber(cameraUpAngle);
		cameraPanel.add(cameraUpAnglePanel, "span");
		
		cameraZoomPanel = new LabelledDoublePanel("FOV of camera. Default = 80, decrease to 'zoom'");
		cameraZoomPanel.setNumber(cameraFOV);
		cameraPanel.add(cameraZoomPanel, "span");
		
		cameraDistancePanel = new LabelledDoublePanel("Camera distance");
		cameraDistancePanel.setNumber(cameraDistance);
		cameraPanel.add(cameraDistancePanel, "span");
		
		maxStepsPanel = new LabelledIntPanel("Max number of steps before returning black");
		maxStepsPanel.setNumber(maxSteps);
		cameraPanel.add(maxStepsPanel, "span");
		
		
		//movie stuff
		JPanel moviePanel = new JPanel();
		moviePanel.setLayout(new MigLayout("insets 0"));
		//moviePanel.setBorder(GUIBitsAndBobs.getTitledBorder("Movie Time"));
		tabbedPane.add(moviePanel, "Movie Time");
	
		movieCheckBox = new JCheckBox("Create movie");
		movieCheckBox.setSelected(movie);
		moviePanel.add(movieCheckBox, "span");
		
		movieTabbedPane = new JTabbedPane();
		moviePanel.add(movieTabbedPane, "span");
		
		
		JPanel movingCloakPanle = new JPanel();
		//movingCloakPanle.setBorder(GUIBitsAndBobs.getTitledBorder("Cloak"));
		movingCloakPanle.setLayout(new MigLayout("insets 0"));		
		
		startAnglePanelCloak = new LabelledDoublePanel("starting angle");
		startAnglePanelCloak.setNumber(startAngleCloak);
		movingCloakPanle.add(startAnglePanelCloak, ""); 
		
		stopAnglePanelCloak = new LabelledDoublePanel("stopping angle");
		stopAnglePanelCloak.setNumber(stopAngleCloak);
		movingCloakPanle.add(stopAnglePanelCloak, "span");
		
		movieTabbedPane.add(movingCloakPanle, "Cloak");
		
		
		JPanel movingRayPanle = new JPanel();
		//movingRayPanle.setBorder(GUIBitsAndBobs.getTitledBorder("Ray"));
		movingRayPanle.setLayout(new MigLayout("insets 0"));
		
		startAnglePanelRay = new LabelledDoublePanel("starting angle");
		startAnglePanelRay.setNumber(startAngleRay);
		movingRayPanle.add(startAnglePanelRay, ""); 
		
		stopAnglePanelRay = new LabelledDoublePanel("stopping angle");
		stopAnglePanelRay.setNumber(stopAngleRay);
		movingRayPanle.add(stopAnglePanelRay, "span");
		
		startUpAnglePanelRay = new LabelledDoublePanel("starting up angle");
		startUpAnglePanelRay.setNumber(startUpAngleRay);
		movingRayPanle.add(startUpAnglePanelRay, ""); 
		
		stopUpAnglePanelRay = new LabelledDoublePanel("stopping up angle");
		stopUpAnglePanelRay.setNumber(stopUpAngleRay);
		movingRayPanle.add(stopUpAnglePanelRay, "span");
		
		movieTabbedPane.add(movingRayPanle,"Ray");
		
		
		JPanel movingCameraPanle = new JPanel();
		//movingCameraPanle.setBorder(GUIBitsAndBobs.getTitledBorder("Camera"));
		movingCameraPanle.setLayout(new MigLayout("insets 0"));
		
		startAnglePanelCamera = new LabelledDoublePanel("starting angle");
		startAnglePanelCamera.setNumber(startAngleCamera);
		movingCameraPanle.add(startAnglePanelCamera, ""); 
		
		stopAnglePanelCamera = new LabelledDoublePanel("stopping angle");
		stopAnglePanelCamera.setNumber(stopAngleCamera);
		movingCameraPanle.add(stopAnglePanelCamera, "span");
		
		startUpAnglePanelCamera = new LabelledDoublePanel("starting up angle");
		startUpAnglePanelCamera.setNumber(startUpAngleCamera);
		movingCameraPanle.add(startUpAnglePanelCamera, ""); 
		
		stopUpAnglePanelCamera = new LabelledDoublePanel("stopping up angle");
		stopUpAnglePanelCamera.setNumber(stopUpAngleCamera);
		movingCameraPanle.add(stopUpAnglePanelCamera, "span");
		
		movieTabbedPane.add(movingCameraPanle,"Camera");
		
		
		numberOfFramesPanel = new IntPanel();
		numberOfFramesPanel.setNumber(numberOfFrames);
		
		firstFramePanel = new IntPanel();
		firstFramePanel.setNumber(firstFrame);
		
		lastFramePanel = new IntPanel();
		lastFramePanel.setNumber(lastFrame);

		moviePanel.add(GUIBitsAndBobs.makeRow("Calculate frames", firstFramePanel, "to", lastFramePanel, "out of", numberOfFramesPanel), "wrap");
	}
		
		@Override
		protected void acceptValuesInInteractiveControlPanel()
		{
			super.acceptValuesInInteractiveControlPanel();
			
			switch(movieTabbedPane.getSelectedIndex())
			{
			case 0:
				movieType = MovieType.ROTATING_CLOAK;
				break;
			case 1:
				movieType = MovieType.MOVING_RAY;
				break;
			case 2:
				movieType = MovieType.CAMERA_MOVING;
				break;
			}
		


		
		//cloak
		lensTrans = lensTransPanel.getNumber();
		CloakFrame = CloakFrameCheck.isSelected();
		baseFocal = baseLensFPanel.getNumber();
		lensType = (LensType)(lensTypeComboBox.getSelectedItem());
		cloakRotationAngle = cloakRotationAnglePanel.getNumber();
		
		
				
		// raytrace
		showTrajectory = showTrajectoryPanel.isSelected();
		rayAngle = rayAnglePanel.getNumber(); //angle
		rayUpAngle = rayUpAnglePanel.getNumber();
		manualRayDirection = manualRayDirectionCheckBox.isSelected(); //checkbox to set to manual direction
		trajectoryDefaultDirection = trajectoryDefaultDirectionPanel.getVector3D();//manual direction of beam
		
		//camera
		cameraAngle = cameraAnglePanel.getNumber();
		cameraUpAngle = cameraUpAnglePanel.getNumber();
		cameraFOV = cameraZoomPanel.getNumber();
		cameraDistance = cameraDistancePanel.getNumber();
		maxSteps = maxStepsPanel.getNumber();
		
		
		//movie stuff
		movie = movieCheckBox.isSelected();
		numberOfFrames = numberOfFramesPanel.getNumber();
		firstFrame = firstFramePanel.getNumber();
		lastFrame = lastFramePanel.getNumber();
		startAngleCloak = startAnglePanelCloak.getNumber();
		stopAngleCloak = stopAnglePanelCloak.getNumber();
		startAngleRay = startAnglePanelRay.getNumber();
		stopAngleRay = stopAnglePanelRay.getNumber();
		startUpAngleRay = startUpAnglePanelRay.getNumber();
		stopUpAngleRay = stopUpAnglePanelRay.getNumber();
		startAngleCamera = startAnglePanelCamera.getNumber();
		stopAngleCamera = stopAnglePanelCamera.getNumber();
		startUpAngleCamera = startUpAnglePanelCamera.getNumber();
		stopUpAngleCamera = stopUpAnglePanelCamera.getNumber();
	}
	
	
	@Override
    public void actionPerformed(ActionEvent e)
    {
            super.actionPerformed(e);
            
            if(e.getSource().equals(rayLastClickInfo))
    		{
            	RaySceneObjectIntersection i = raytracingImageCanvas.getLastClickIntersection();
  
            	rayLastClickTextArea.setText("Ray aiming at "+i.p)
    				;

    		}

    }
	
	public static void main(final String[] args)
	{
		(new IdealLensCloakRayTest()).run();
	}
}

