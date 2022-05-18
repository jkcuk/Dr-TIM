package optics.raytrace.test;

import java.awt.event.ActionEvent;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

import math.*;
import net.miginfocom.swing.MigLayout;
import optics.raytrace.sceneObjects.Plane;
import optics.raytrace.sceneObjects.TimHead;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.surfaces.RefractiveViewRotator;
import optics.raytrace.surfaces.SurfaceColour;
import optics.raytrace.surfaces.SurfaceColourLightSourceIndependent;
import optics.raytrace.core.*;
import optics.raytrace.exceptions.RayTraceException;
import optics.raytrace.exceptions.SceneException;
import optics.raytrace.NonInteractiveTIMEngine;
import optics.raytrace.GUI.lowLevel.ApertureSizeType;
import optics.raytrace.GUI.lowLevel.DoublePanel;
import optics.raytrace.GUI.lowLevel.GUIBitsAndBobs;
import optics.raytrace.GUI.lowLevel.LabelledDoublePanel;
import optics.raytrace.GUI.lowLevel.LabelledIntPanel;
import optics.raytrace.GUI.lowLevel.LabelledVector3DPanel;
import optics.raytrace.GUI.sceneObjects.EditableCylinderLattice;
import optics.raytrace.GUI.sceneObjects.EditableRayTrajectory;



/**
 * Testing the refractive view rotation device.
 *  
 * @author Maik
 */
public class RefractiveViewRotationTest extends NonInteractiveTIMEngine
{	
	/**
	 * View rotator
	 */
	RefractiveViewRotator refractiveViewRotator;
	
	//bounding box settings
	private Vector3D boundingBoxCentre, boundingBoxSpanVector1,boundingBoxSpanVector2, boundingBoxSpanVector3;
	
	//Rotator settings
	private Vector3D frontSurfaceNormal, rotationCentre, periodVector1, periodVector2, centre,eyePosition,rotationAxisDirection;
	
	private double rotationAngle, refractiveIndex, wedgeThickness;
	
	private boolean showTrajectory;


	/**
	 * general things
	 */
	private int maxSteps; 
	private boolean shadowThrowing;
	private double surfaceTransmissionCoefficient;


	
	/**
	 * camera
	 */
	private double cameraRotation;
	
	
	/**
	 * Object params
	 */
	private double objectRotationAngle;
	private double objectDistance;

	
	public RefractiveViewRotationTest()
	{
		super();
			
		/**
		 * Setting the paramterers for a specs setup
		 */

		boundingBoxCentre = new Vector3D(0,0,0.001499999);
		boundingBoxSpanVector1 =new Vector3D(0.05,0,0);
		boundingBoxSpanVector2 = new Vector3D(0,0.05,0);
		boundingBoxSpanVector3 =new Vector3D(0,0,0.003);
		frontSurfaceNormal = new Vector3D(0,0,-1);
		centre = new Vector3D(0,0,0);
		rotationCentre = new Vector3D(0,0,2);
		rotationAngle = 10;
		periodVector1 = new Vector3D(0.001,0,0);
		periodVector2 = new Vector3D(0,0.001,0);
		refractiveIndex = 1.5; //glass
		wedgeThickness = 0.001;
		maxSteps = 100;
		surfaceTransmissionCoefficient = 1;
		shadowThrowing = false;
		eyePosition = new Vector3D(0,0,-0.016);
		rotationAxisDirection = new Vector3D(0,0,-1);
	
		
		// camera params
		cameraRotation = 0;
		cameraDistance = 0.016;
		cameraViewCentre = new Vector3D(0, 0, 0);
		cameraViewDirection = new Vector3D(0, 0, 1);
		cameraTopDirection = new Vector3D(0,1,0);
		cameraHorizontalFOVDeg = 130;
		cameraApertureSize = ApertureSizeType.PINHOLE;
		cameraFocussingDistance = 1;
		showTrajectory = false;
		
		//object
		objectRotationAngle = 0;
		objectDistance = 2;
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
		
		// a cylinder lattice...
		Vector3D uHat, vHat, zHat;
		zHat = new Vector3D(0,0,1);
		uHat = new Vector3D(Math.cos(Math.toRadians(objectRotationAngle)), Math.sin(Math.toRadians(objectRotationAngle)),0);
		vHat= new Vector3D(-Math.sin(Math.toRadians(objectRotationAngle)), Math.cos(Math.toRadians(objectRotationAngle)),0);
		scene.addSceneObject(new EditableCylinderLattice(
				"cylinder lattice",
				-1, 1, 4, uHat,
				-1+0.02, 1+0.02, 4, vHat,
				objectDistance, 4*objectDistance, 4, zHat, // this puts the "transverse" cylinders into the planes z=10, 20, 30, 40
				0.02,
				scene,
				studio
		));	
		
//		//use tim
//		scene.addSceneObject(new TimHead(
//				"tims head",//description
//				new Vector3D(0,0,objectDistance+1),
//				1,	// radius
//				new Vector3D(0, 0, -1),	// frontDirection
//				new Vector3D(0, 1, 0), 	// topDirection
//				new Vector3D(1, 0, 0),	// rightDirection
//				scene,
//				studio
//				
//				));
		
		//view rotator time
//		refractiveViewRotator = new RefractiveViewRotator(
//				"refractive view rotator to test",// description,
//				boundingBoxCentre, 
//				boundingBoxSpanVector1,
//				boundingBoxSpanVector2,
//				boundingBoxSpanVector3,
//				frontSurfaceNormal,
//				eyeDistance,
//				centre,
//				rotationCentre,
//				rotationAngle,
//				periodVector1,
//				periodVector2,
//				refractiveIndex,
//				wedgeThickness,
//				maxSteps, 
//				surfaceTransmissionCoefficient, 
//				shadowThrowing,
//				scene, 
//				studio
//				);
//		scene.addSceneObject(refractiveViewRotator);
		refractiveViewRotator = new RefractiveViewRotator(
				"refractive view rotator to test",// description,
				boundingBoxCentre, 
				boundingBoxSpanVector1,
				boundingBoxSpanVector2,
				boundingBoxSpanVector3,
				frontSurfaceNormal,// ocularSurfaceNormal,
				eyePosition,
				centre,
				rotationAngle,
				rotationAxisDirection,
				1,// magnificationFactor,
				periodVector1,
				periodVector2,
				new Plane("test", 
						new Vector3D(0, 0, 2), 
						Vector3D.Z, 
						null,
						null, 
						null
						),
				refractiveIndex,
				wedgeThickness,
				maxSteps, 
				surfaceTransmissionCoefficient, 
				shadowThrowing,
				scene, 
				studio
				);
		scene.addSceneObject(refractiveViewRotator);
		
		
		
//		scene.addSceneObject(new Plane(
//				"red background surface",
//				new Vector3D(0,0,-10),// pointOnPlane,
//				new Vector3D(0,0,1),// normal, 
//				SurfaceColour.DARK_RED_MATT,//
//				scene,// parent,
//				studio// studio
//				));
		
		
		
//		scene.addSceneObject (new RefractiveTranslationWedge(
//				"Single wedge",// description,
//				centre,
//				frontSurfaceNormal,// normalisedAxisDirection,
//				centre.getSumWith(frontSurfaceNormal.getWithLength(eyeDistance)),// viewingPosition,
//				eyeDistance,// distanceToImagePlane,
//				wedgeThickness,// centralThickness,
//				new Vector3D(2,0,0),// translation,
//				refractiveIndex,
//				surfaceTransmissionCoefficient,
//				shadowThrowing,
//				scene,// parent,
//				studio));
		
		// the camera

		cameraTopDirection = new Vector3D(Math.sin(Math.toRadians(cameraRotation)),Math.cos(Math.toRadians(cameraRotation)),0);
		studio.setCamera(getStandardCamera());
		
		if (showTrajectory) {
			RaySceneObjectIntersection i = raytracingImageCanvas.getLastClickIntersection();
			Vector3D rayAim = i.p;
			Vector3D rayDirection = Vector3D.difference(rayAim, eyePosition);


			double frameRadius = 0.0002;//radius of ray

			// do the tracing of rays with trajectory
			scene.addSceneObject(
					new EditableRayTrajectory(
							"light-ray trajectory",	// description
							eyePosition,	// startPoint
							0,	// startTime
							rayDirection,	// startDirection
							frameRadius,	// rayRadius
							SurfaceColourLightSourceIndependent.GREEN,	// surfaceProperty
							100,	// maxTraceLevel
							true,	// reportToConsole
							scene,	// parent
							studio
							)
					);

			studio.setScene(scene);

			// trace the rays with trajectory through the scene
			studio.traceRaysWithTrajectory();

			//remove all the objects
			//scene.removeSceneObject(refractiveViewRotator);
			
		}
		
		studio.setScene(scene);	
		studio.setLights(LightSource.getStandardLightsFromBehind());
		
	}
	
	// camera stuff
	// private LabelledVector3DPanel cameraViewDirectionPanel;
	private DoublePanel cameraDistancePanel, cameraRotationPanel;
	private LabelledVector3DPanel cameraViewDirectionPanel;
	private DoublePanel cameraHorizontalFOVDegPanel;
	private JComboBox<ApertureSizeType> cameraApertureSizeComboBox;
	private DoublePanel cameraFocussingDistancePanel;
	//general
	private LabelledVector3DPanel boundingBoxCentrePanel, boundingBoxSpanVector1Panel, boundingBoxSpanVector2Panel, boundingBoxSpanVector3Panel;
	private LabelledIntPanel maxStepSpanel;
	private LabelledDoublePanel surfaceTransmissionCoefficientPanel, refractiveIndexPanel;
	private JCheckBox shadowThrowingCheckBox, showTrajectoryPanel;
	private DoublePanel objectRotationAnglePanel, objectDistancePanel;
	//rotator stuff
	private LabelledVector3DPanel frontSurfaceNormalPanel, centrePanel, rotationCentrePanel, periodVector1Panel, periodVector2Panel, eyePositionPanel, rotationAxisDirectionPanel;
	private LabelledDoublePanel	rotationAnglePanel, wedgeThicknesSpanel;
	private JTextArea rayLastClickTextArea;
	private JButton rayLastClickInfo;
	
	
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
		
		boundingBoxCentrePanel = new LabelledVector3DPanel("bounding box centre");
		boundingBoxCentrePanel.setVector3D(boundingBoxCentre);
		scenePanel.add(boundingBoxCentrePanel, "span");
		
		boundingBoxSpanVector1Panel = new LabelledVector3DPanel("bounding box Span vector 1");
		boundingBoxSpanVector1Panel.setVector3D(boundingBoxSpanVector1);
		scenePanel.add(boundingBoxSpanVector1Panel, "span");
		
		boundingBoxSpanVector2Panel = new LabelledVector3DPanel("bounding box Span vector 2");
		boundingBoxSpanVector2Panel.setVector3D(boundingBoxSpanVector2);
		scenePanel.add(boundingBoxSpanVector2Panel, "Span");
		
		boundingBoxSpanVector3Panel = new LabelledVector3DPanel("bounding box Span vector 3");
		boundingBoxSpanVector3Panel.setVector3D(boundingBoxSpanVector3);
		scenePanel.add(boundingBoxSpanVector3Panel, "span");
		
		frontSurfaceNormalPanel = new LabelledVector3DPanel("Front surface normal");
		frontSurfaceNormalPanel.setVector3D(frontSurfaceNormal);
		scenePanel.add(frontSurfaceNormalPanel, "span");
		
		centrePanel = new LabelledVector3DPanel("rotator centre");
		centrePanel.setVector3D(centre);
		scenePanel.add(centrePanel, "span");
		
		rotationCentrePanel = new LabelledVector3DPanel("centre of rotation");
		rotationCentrePanel.setVector3D(rotationCentre);
		scenePanel.add(rotationCentrePanel, "span");
		
		rotationAxisDirectionPanel = new LabelledVector3DPanel("Rotation Axis");
		rotationAxisDirectionPanel.setVector3D(rotationAxisDirection);
		scenePanel.add(rotationAxisDirectionPanel, "span");
		
		periodVector1Panel = new LabelledVector3DPanel("Period Vector 1");
		periodVector1Panel.setVector3D(periodVector1);
		scenePanel.add(periodVector1Panel, "span");
		
		periodVector2Panel = new LabelledVector3DPanel("Period Vector 2");
		periodVector2Panel.setVector3D(periodVector2);
		scenePanel.add(periodVector2Panel, "span");
		
//		eyeDistancePanel = new LabelledDoublePanel("Distance of eye/camera to rotator");
//		eyeDistancePanel.setNumber(eyeDistance);
//		scenePanel.add(eyeDistancePanel, "span");
		
		eyePositionPanel = new LabelledVector3DPanel("Eye Position");
		eyePositionPanel.setVector3D(eyePosition);
		scenePanel.add(eyePositionPanel, "span");
		
		rotationAnglePanel = new LabelledDoublePanel("Rotation in degrees");
		rotationAnglePanel.setNumber(rotationAngle);
		scenePanel.add(rotationAnglePanel, "span");
		
		wedgeThicknesSpanel = new LabelledDoublePanel("Thickness of individual wedges");
		wedgeThicknesSpanel.setNumber(wedgeThickness);
		scenePanel.add(wedgeThicknesSpanel, "span");	
		
		refractiveIndexPanel = new LabelledDoublePanel("refractive index (glass ~1.5)");
		refractiveIndexPanel.setNumber(refractiveIndex);
		scenePanel.add(refractiveIndexPanel, "span");
		
		surfaceTransmissionCoefficientPanel = new LabelledDoublePanel("Transmission coefficient");
		surfaceTransmissionCoefficientPanel.setNumber(surfaceTransmissionCoefficient);
		scenePanel.add(surfaceTransmissionCoefficientPanel, "span");
		
		shadowThrowingCheckBox = new JCheckBox("Shadows");
		shadowThrowingCheckBox.setSelected(shadowThrowing);
		scenePanel.add(shadowThrowingCheckBox, "span");
		
		//
		//Background stuff
		//

		
		objectRotationAnglePanel = new DoublePanel();
		objectRotationAnglePanel.setNumber(objectRotationAngle);
		scenePanel.add(GUIBitsAndBobs.makeRow("Rotation angle", objectRotationAnglePanel,"<html>&deg;</html>,"), "span");
		
		objectDistancePanel = new DoublePanel();
		objectDistancePanel.setNumber(objectDistance);
		scenePanel.add(GUIBitsAndBobs.makeRow("Distance", objectDistancePanel,""), "span");
		
		//
		// the Camera panel
		//
		
		JPanel cameraPanel = new JPanel();
		cameraPanel.setLayout(new MigLayout("insets 0"));
		sceneCameraTabbedPane.addTab("Camera", cameraPanel);

		// camera stuff
		
//		cameraViewDirectionPanel = new LabelledVector3DPanel("View direction");
//		cameraViewDirectionPanel.setVector3D(cameraViewDirection);
//		cameraPanel.add(cameraViewDirectionPanel, "span");
		
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
		
		maxStepSpanel = new LabelledIntPanel("Max steps");
		maxStepSpanel.setNumber(maxSteps);
		scenePanel.add(maxStepSpanel, "span");
		
		
		JPanel rayTracePanel = new JPanel();
		rayTracePanel.setLayout(new MigLayout("insets 0"));
		sceneCameraTabbedPane.addTab("Raytrace", rayTracePanel);
		
		
		showTrajectoryPanel = new JCheckBox("show raytrace");
		showTrajectoryPanel.setSelected(showTrajectory);
		rayTracePanel.add(showTrajectoryPanel,"span");
		
		rayLastClickTextArea = new JTextArea(20, 40);
		JScrollPane scrollPane = new JScrollPane(rayLastClickTextArea); 
		rayLastClickTextArea.setEditable(false);
		rayLastClickTextArea.setText("Click on the aim ray button set the aim of the light ray");
		rayLastClickInfo = new JButton("Aim ray");
		rayLastClickInfo.addActionListener(this);
		rayTracePanel.add(GUIBitsAndBobs.makeRow(scrollPane, rayLastClickInfo), "span");
	}
		
	protected void acceptValuesInInteractiveControlPanel()
	{
		refractiveIndex =refractiveIndexPanel.getNumber();
		boundingBoxCentre = boundingBoxCentrePanel.getVector3D();
		boundingBoxSpanVector1 = boundingBoxSpanVector1Panel.getVector3D();
		boundingBoxSpanVector2 = boundingBoxSpanVector2Panel.getVector3D();
		boundingBoxSpanVector3 = boundingBoxSpanVector3Panel.getVector3D();
		
		eyePosition = eyePositionPanel.getVector3D();
		rotationAxisDirection = rotationAxisDirectionPanel.getVector3D(); 
		
		frontSurfaceNormal = frontSurfaceNormalPanel.getVector3D();
		centre = centrePanel.getVector3D();
		rotationCentre = rotationCentrePanel.getVector3D();
		periodVector1 = periodVector1Panel.getVector3D();
		periodVector2 = periodVector2Panel.getVector3D();
		
//		eyeDistance =eyeDistancePanel.getNumber();
		rotationAngle = rotationAnglePanel.getNumber();
		wedgeThickness =wedgeThicknesSpanel.getNumber();
	
		//Object stuff
		objectRotationAngle =objectRotationAnglePanel.getNumber();
		objectDistance = objectDistancePanel.getNumber();
		
		//Camera and general stuff
		cameraDistance = cameraDistancePanel.getNumber();
		cameraRotation = cameraRotationPanel.getNumber();
		cameraHorizontalFOVDeg = cameraHorizontalFOVDegPanel.getNumber();
		cameraViewDirection = cameraViewDirectionPanel.getVector3D();
		cameraApertureSize = (ApertureSizeType)(cameraApertureSizeComboBox.getSelectedItem());
		cameraFocussingDistance = cameraFocussingDistancePanel.getNumber();
		maxSteps = maxStepSpanel.getNumber();
		surfaceTransmissionCoefficient=surfaceTransmissionCoefficientPanel.getNumber();
		refractiveIndex=refractiveIndexPanel.getNumber();
		shadowThrowing=shadowThrowingCheckBox.isSelected();
		showTrajectory = showTrajectoryPanel.isSelected();
		
	}
	
    public void actionPerformed(ActionEvent e)
    {
            super.actionPerformed(e);
           
            if(e.getSource().equals(rayLastClickInfo))
    		{
            	RaySceneObjectIntersection i = raytracingImageCanvas.getLastClickIntersection();	
    			Vector3D rayAim = i.p;
    			Vector3D rayDirection = Vector3D.difference(rayAim, eyePosition);
    			Ray ray = new Ray(eyePosition, rayDirection, 0, true);
    			
				// Create a stream to hold the output
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				PrintStream ps = new PrintStream(baos);
				// IMPORTANT: Save the old System.out!
				PrintStream old = System.out;
				// Tell Java to use your special stream
				System.setOut(ps);
    			
				try {			
					studio.getScene().getColour(ray, null, studio.getScene(), maxSteps, getStandardCamera());
				} catch (RayTraceException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} 
					// Put things back
    				System.out.flush();
    				System.setOut(old);  
                	rayLastClickTextArea.setText("Ray aiming at "+i.p+"\n"+baos.toString());
    		}

    }
	
	public static void main(final String[] args)
	{
		(new RefractiveViewRotationTest()).run();
	}
}

