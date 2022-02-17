package optics.raytrace.test;

import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import math.*;
import net.miginfocom.swing.MigLayout;
import optics.raytrace.sceneObjects.*;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.surfaces.GaborSupererRefractiveCLAs;
import optics.raytrace.surfaces.SimpleRefractiveCLAs;
import optics.raytrace.cameras.PinholeCamera.ExposureCompensationType;
import optics.raytrace.core.*;
import optics.raytrace.exceptions.SceneException;
import optics.raytrace.NonInteractiveTIMEngine;
import optics.raytrace.GUI.cameras.EditableRelativisticAnyFocusSurfaceCamera;
import optics.raytrace.GUI.lowLevel.ApertureSizeType;
import optics.raytrace.GUI.lowLevel.LabelledDoublePanel;
import optics.raytrace.GUI.lowLevel.LabelledIntPanel;
import optics.raytrace.GUI.lowLevel.LabelledVector3DPanel;
import optics.raytrace.GUI.sceneObjects.EditableLensletArrayForGaborSupererLens;
import optics.raytrace.GUI.sceneObjects.EditableTimHead;


/**
 * Testing the refractive lens(array) quality.
 *  
 * @author Maik
 */
public class RefractiveLensletTestExplorer extends NonInteractiveTIMEngine implements ActionListener
{	
	//camera stuff
	private double cameraAngle; // the angle at which the camera will face towards (0,0,0), + to go left - to go right 
	private double cameraUpAngle; //define an angle with which the camera will view the origin
	private double cameraFOV; //set the focus and thus zoom of camera	
	private double cameraDistance; //set camera distance from (0,0,0) 
	
	
	/**
	 * defining the first array params
	 */
	private double focalLengthArray1, centreThicknessArray1;
	private Vector3D lensClearApertureCentreArray1,  principalPointCentreArray1, uPeriodClearApertureArray1, vPeriodClearApertureArray1, uPeriodPrincipalPointArray1, vPeriodPrincipalPointArray1;
	
	/**
	 * defining the second array params
	 */
	private double focalLengthArray2, centreThicknessArray2;
	private Vector3D lensClearApertureCentreArray2,  principalPointCentreArray2, uPeriodClearApertureArray2, vPeriodClearApertureArray2, uPeriodPrincipalPointArray2, vPeriodPrincipalPointArray2;
	
	/**
	 * Gabor superer lens params
	 */
	private boolean setUpWithCommonPoint, setUpGaborSuperer; 
	private Vector3D commonPlaneInterceptionPoint;
	GaborSupererRefractiveCLAs GaborSupererRefractiveCLAs;
	/**
	 * Basic CLAs params
	 */
	private boolean setUpBasicCLAs, idealisedThickness; 
	SimpleRefractiveCLAs SimpleRefractiveCLAs;
	
	/**
	 * General array parameters
	 */
	private double lensTrans, refractiveIndex;
	private boolean separatedArrays;
	private Vector3D boundingBoxCentre, boundingBoxSpanVector1, boundingBoxSpanVector2, boundingBoxSpanVector3;
	
	/**
	 * General params
	 */
	private boolean showEquivalent;
	private int maxSteps; 

	
	public RefractiveLensletTestExplorer()
	{
		super();
			
		/**
		 * Setting the first array params
		 */
		focalLengthArray1 = 0.07;
		centreThicknessArray1 = 0.003;
		lensClearApertureCentreArray1 = new Vector3D(0,2,0);
		principalPointCentreArray1 = new Vector3D(0,2,0);
		uPeriodClearApertureArray1 = new Vector3D(0.01,0,0);
		vPeriodClearApertureArray1 = new Vector3D(0,0.01,0);
		uPeriodPrincipalPointArray1 = new Vector3D(0.01,0,0);
		vPeriodPrincipalPointArray1 = new Vector3D(0,0.01,0);
		
		/**
		 * Setting the second array params
		 */
		focalLengthArray2 = 0.07;
		centreThicknessArray2 = 0.003;
		lensClearApertureCentreArray2 = new Vector3D(0,2,0.14); 
		principalPointCentreArray2 = new Vector3D(0,2,0.14);
		uPeriodClearApertureArray2 = new Vector3D(0.0098,0,0);
		vPeriodClearApertureArray2 = new Vector3D(0,0.0098,0);
		uPeriodPrincipalPointArray2 = new Vector3D(0.01,0,0);
		vPeriodPrincipalPointArray2 = new Vector3D(0,0.01,0);
		
		/**
		 * Gabor superer lens params
		 */
		setUpWithCommonPoint = true;
		setUpGaborSuperer = true;
		commonPlaneInterceptionPoint = new Vector3D(0,2, Double.POSITIVE_INFINITY);
		
		

		/**
		 * Basic CLAs params
		 */
		setUpBasicCLAs = false;
		idealisedThickness = false;

		
		/**
		 * General parameters
		 */
		lensTrans =1;
		refractiveIndex =1.5;
		separatedArrays = false;
		boundingBoxCentre = new Vector3D(0,2,0.07);
		boundingBoxSpanVector1 = new Vector3D(0.5,0,0);
		boundingBoxSpanVector2 = new Vector3D(0,0.5,0);
		boundingBoxSpanVector3 = new Vector3D(0 ,0 ,1);
		showEquivalent = false;
		maxSteps = 50;
	
		
		// camera params
		cameraAngle = 0;
		cameraUpAngle = 0;
		cameraFOV = 20;
		cameraDistance = 10;
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
		
		if(setUpBasicCLAs) {
			
			uPeriodClearApertureArray2 = uPeriodClearApertureArray1;
			vPeriodClearApertureArray2 = vPeriodClearApertureArray1;
			
			if(idealisedThickness) {
				SimpleRefractiveCLAs = new SimpleRefractiveCLAs(
						"Confocal refractive lenslet array",// description,
						boundingBoxCentre,// boundingBoxCentre,
						boundingBoxSpanVector1,// boundingBoxSpanVector1,
						boundingBoxSpanVector2,// boundingBoxSpanVector2,
						Vector3D.crossProduct(uPeriodPrincipalPointArray1, vPeriodPrincipalPointArray1).getNormalised(),// normalisedOpticalAxisDirection,
						uPeriodClearApertureArray1,// clearApertureArrayBasisVector1, 
						vPeriodClearApertureArray1,// clearApertureArrayBasisVector2,
						uPeriodPrincipalPointArray1,// principalPointArray1BasisVector1,
						vPeriodPrincipalPointArray1,// principalPointArray1BasisVector2,
						uPeriodPrincipalPointArray2,// principalPointArray2BasisVector1,
						vPeriodPrincipalPointArray2,// principalPointArray2BasisVector2,
						lensClearApertureCentreArray1,// lens00ClearApertureCentreArray1, 
						lensClearApertureCentreArray2,// lens00ClearApertureCentreArray2, 
						principalPointCentreArray1, // lens00PrincipalPointArray1,
						principalPointCentreArray2,// lens00PrincipalPointArray2,
						focalLengthArray1,// focalLengthArray1,
						focalLengthArray2,// focalLengthArray2,			
						refractiveIndex,// refractiveIndex,
						centreThicknessArray1,// set to -1 to try and auto calculate, lensletCentreThicknessArray1,
						centreThicknessArray2,// set to -1 to try and auto calculate, lensletCentreThicknessArray2,
						maxSteps, //maxSteps, 
						lensTrans, // surfaceTransmissionCoefficient, 
						false, //shadowThrowing,
						separatedArrays, //two arrays seperated by air
						scene, // parent, 
						studio //studio
						);
			}else {
				SimpleRefractiveCLAs = new SimpleRefractiveCLAs(
						"Confocal refractive lenslet array",// description,
						boundingBoxCentre,// boundingBoxCentre,
						boundingBoxSpanVector1,// boundingBoxSpanVector1,
						boundingBoxSpanVector2,// boundingBoxSpanVector2,
						boundingBoxSpanVector3,// boundingBoxSpanVector3,
						Vector3D.crossProduct(uPeriodPrincipalPointArray1, vPeriodPrincipalPointArray1).getNormalised(),// normalisedOpticalAxisDirection,
						uPeriodClearApertureArray1,// clearApertureArrayBasisVector1, 
						vPeriodClearApertureArray1,// clearApertureArrayBasisVector2,
						uPeriodPrincipalPointArray1,// principalPointArray1BasisVector1,
						vPeriodPrincipalPointArray1,// principalPointArray1BasisVector2,
						uPeriodPrincipalPointArray2,// principalPointArray2BasisVector1,
						vPeriodPrincipalPointArray2,// principalPointArray2BasisVector2,
						lensClearApertureCentreArray1,// lens00ClearApertureCentreArray1, 
						lensClearApertureCentreArray2,// lens00ClearApertureCentreArray2, 
						principalPointCentreArray1, // lens00PrincipalPointArray1,
						principalPointCentreArray2,// lens00PrincipalPointArray2,
						focalLengthArray1,// focalLengthArray1,
						focalLengthArray2,// focalLengthArray2,			
						refractiveIndex,// refractiveIndex,
						centreThicknessArray1,// set to -1 to try and auto calculate, lensletCentreThicknessArray1,
						centreThicknessArray2,// set to -1 to try and auto calculate, lensletCentreThicknessArray2,
						maxSteps, //maxSteps, 
						lensTrans, // surfaceTransmissionCoefficient, 
						false, //shadowThrowing,
						separatedArrays, //two arrays seperated by air
						scene, // parent, 
						studio //studio
						);	
				
			}
			scene.addSceneObject(SimpleRefractiveCLAs);
		}

		if(setUpGaborSuperer) {
			if(setUpWithCommonPoint){
				
				//calc to find corresponding aperture span vectors for second array.
				Vector3D normal = Vector3D.crossProduct(uPeriodPrincipalPointArray1, vPeriodPrincipalPointArray1).getNormalised();
				Vector3D clearAC1ToCommonPoint = commonPlaneInterceptionPoint.getDifferenceWith(lensClearApertureCentreArray1);
				try {
					lensClearApertureCentreArray2 = Geometry.linePlaneIntersection(lensClearApertureCentreArray1, clearAC1ToCommonPoint, principalPointCentreArray2, normal);
				} catch (MathException e) {
					System.err.println("There are no or too many interceptions");
					e.printStackTrace();
				}
				//now to find the span vectors at this point given the span vectors at first array.
				double scaleFactor = Vector3D.getDistance(commonPlaneInterceptionPoint,lensClearApertureCentreArray2)/(clearAC1ToCommonPoint.getLength());
				uPeriodClearApertureArray2 = uPeriodClearApertureArray1.getProductWith(scaleFactor);
				vPeriodClearApertureArray2 = vPeriodClearApertureArray1.getProductWith(scaleFactor);
				System.out.println(	(scaleFactor));
				
				
				GaborSupererRefractiveCLAs= new GaborSupererRefractiveCLAs(	
						"gabor superer type CLA", //description,
						boundingBoxCentre,// boundingBoxCentre,
						boundingBoxSpanVector1,// boundingBoxSpanVector1,
						boundingBoxSpanVector2,// boundingBoxSpanVector2,
						boundingBoxSpanVector3,// boundingBoxSpanVector3,
						Vector3D.crossProduct(uPeriodPrincipalPointArray1, vPeriodPrincipalPointArray1).getNormalised(),// normalisedOpticalAxisDirection,
						commonPlaneInterceptionPoint,// commonPlaneInterceptionPoint,
						lensClearApertureCentreArray1, //lens00ClearApertureCentreArray1,
						uPeriodClearApertureArray1,// clearApertureArrayBasisVector1, 
						vPeriodClearApertureArray1,// clearApertureArrayBasisVector2,
						focalLengthArray1, // focalLengthArray1,
						principalPointCentreArray1, //lens00PrincipalPointArray1Array1,
						uPeriodPrincipalPointArray1,// principalPointArray1BasisVector1Array1,
						vPeriodPrincipalPointArray1,// principalPointArray1BasisVector2Array1,
						centreThicknessArray1, // centreThicknessArray1,
						focalLengthArray2, // focalLengthArray2,
						principalPointCentreArray2, // lens00PrincipalPointArray2,
						uPeriodPrincipalPointArray2,// principalPointArrayBasisVector1Array2,
						vPeriodPrincipalPointArray2,//principalPointArrayBasisVector2Array2,
						centreThicknessArray2,// centreThicknessArray2,
						refractiveIndex, // refractiveIndex,
						lensTrans, // surfaceTransmissionCoefficient,
						false, // shadowThrowing,
						separatedArrays,
						maxSteps, // maxSteps,
						scene, //parent,
						studio// studio
						);
				
			}else {
			GaborSupererRefractiveCLAs= new GaborSupererRefractiveCLAs(
				"gabor superer type CLA", //description,
				boundingBoxCentre,// boundingBoxCentre,
				boundingBoxSpanVector1,// boundingBoxSpanVector1,
				boundingBoxSpanVector2,// boundingBoxSpanVector2,
				boundingBoxSpanVector3,// boundingBoxSpanVector3,
				Vector3D.crossProduct(uPeriodPrincipalPointArray1, vPeriodPrincipalPointArray1).getNormalised(),// normalisedOpticalAxisDirection,
				focalLengthArray1, // focalLengthArray1,
				lensClearApertureCentreArray1, //lens00ClearApertureCentreArray1Array1,
				uPeriodClearApertureArray1,// clearApertureArrayBasisVector1Array1, 
				vPeriodClearApertureArray1,// clearApertureArrayBasisVector2Array1,
				principalPointCentreArray1, //lens00PrincipalPointArray1Array1,
				uPeriodPrincipalPointArray1,// principalPointArray1BasisVector1Array1,
				vPeriodPrincipalPointArray1,// principalPointArray1BasisVector2Array1,
				centreThicknessArray1, // centreThicknessArray1,
				focalLengthArray2,// focalLengthArray2,
				lensClearApertureCentreArray2, // lens00ClearApertureCentreArray2,
				uPeriodClearApertureArray2,// clearApertureArrayBasisVector1Array2,
				vPeriodClearApertureArray2,// clearApertureArrayBasisVector2Array2,
				principalPointCentreArray2, // lens00PrincipalPointArray2,
				uPeriodPrincipalPointArray2,// principalPointArrayBasisVector1Array2,
				vPeriodPrincipalPointArray2,//principalPointArrayBasisVector2Array2,
				centreThicknessArray2, // centreThicknessArray2,
				refractiveIndex,// refractiveIndex,
				lensTrans, // surfaceTransmissionCoefficient,
				false, // shadowThrowing,
				separatedArrays,
				maxSteps, // maxSteps,
				scene, //parent,
				studio// studio
				);				
			}
			scene.addSceneObject(GaborSupererRefractiveCLAs);
		}
		

		

	
	if(showEquivalent) //if true, removes previous component and replaces it with an ideal lenslet array.
	{
		if(setUpBasicCLAs) {
			scene.removeSceneObject(SimpleRefractiveCLAs);		
		}
		if(setUpGaborSuperer) {
			scene.removeSceneObject(GaborSupererRefractiveCLAs);
		}
		
		scene.addSceneObject(new EditableLensletArrayForGaborSupererLens(
				"Equivilant ideal lens array 1",	// description
				principalPointCentreArray1,	// centre
				boundingBoxSpanVector1,//uPeriodPrincipalPointArray1.getNormalised().getWithLength(boundingBoxSpanVector1.getLength()),// boundingBoxSpanVector1,
				boundingBoxSpanVector2,//vPeriodPrincipalPointArray1.getNormalised().getWithLength(boundingBoxSpanVector2.getLength()),// boundingBoxSpanVector2,
				focalLengthArray1,	// focalLength
				uPeriodClearApertureArray1.getLength(),// uPeriodApertures,
				vPeriodClearApertureArray1.getLength(),// vPeriodApertures,
				uPeriodPrincipalPointArray1.getLength(),// uPeriodPrincipalPoints,
				vPeriodPrincipalPointArray1.getLength(),// vPeriodPrincipalPoints,
				lensClearApertureCentreArray1,// centreClearApertureArray,
				principalPointCentreArray1,// centrePrincipalPointArray,
				LensType.IDEAL_THIN_LENS,	// lensType
				false,
				600*1e-9,
				lensTrans,	// throughputCoefficient
				false,	// reflective
				false,	// shadowThrowing
				scene,	// parent
				studio)
				);
		
		
		Vector3D principalPointCentreArray2Test,lensClearApertureCentreArray2Test;
		if (separatedArrays) {
			principalPointCentreArray2Test = principalPointCentreArray2;
			lensClearApertureCentreArray2Test = lensClearApertureCentreArray2;
		}else {
			principalPointCentreArray2Test = principalPointCentreArray1.getSumWith(principalPointCentreArray2.getDifferenceWith(principalPointCentreArray1).getProductWith(1./refractiveIndex));
			lensClearApertureCentreArray2Test =lensClearApertureCentreArray1.getSumWith(lensClearApertureCentreArray2.getDifferenceWith(lensClearApertureCentreArray1).getProductWith(1./refractiveIndex));
		}
		//System.out.println(principalPointCentreArray2Test);
				
		scene.addSceneObject(new EditableLensletArrayForGaborSupererLens(
				"Equivilant ideal lens array 2",	// description
				principalPointCentreArray2Test,	// centre
				boundingBoxSpanVector1,//uPeriodPrincipalPointArray2.getNormalised().getWithLength(boundingBoxSpanVector1.getLength()),// boundingBoxSpanVector1,
				boundingBoxSpanVector2,//vPeriodPrincipalPointArray2.getNormalised().getWithLength(boundingBoxSpanVector2.getLength()),// boundingBoxSpanVector2,
				focalLengthArray2,	// focalLength
				uPeriodClearApertureArray2.getLength(),// uPeriodApertures,
				vPeriodClearApertureArray2.getLength(),// vPeriodApertures,
				uPeriodPrincipalPointArray2.getLength(),// uPeriodPrincipalPoints,
				vPeriodPrincipalPointArray2.getLength(),// vPeriodPrincipalPoints,
				/**
				 * TODO check if below works as calc in parts before
				 */
				lensClearApertureCentreArray2Test,// centreClearApertureArray,
				principalPointCentreArray2Test,// centrePrincipalPointArray,
				LensType.IDEAL_THIN_LENS,	// lensType
				false,
				600*1e-9,
				lensTrans,	// throughputCoefficient
				false,	// reflective
				false,	// shadowThrowing
				scene,	// parent
				studio)
				);

	}

System.out.println("------------------------First array -------------------");
System.out.println("focalLengthArray1 "+focalLengthArray1);
System.out.println("centreThicknessArray1 "+centreThicknessArray1);
System.out.println("lensClearApertureCentreArray1 "+lensClearApertureCentreArray1);
System.out.println("principalPointCentreArray1 "+principalPointCentreArray1);
System.out.println("uPeriodClearApertureArray1 "+uPeriodClearApertureArray1);
System.out.println("vPeriodClearApertureArray1 "+vPeriodClearApertureArray1);
System.out.println("uPeriodPrincipalPointArray1 "+uPeriodPrincipalPointArray1);
System.out.println("vPeriodPrincipalPointArray1 "+vPeriodPrincipalPointArray1);
System.out.println("------------------------second array -------------------");
System.out.println("focalLengthArray2 "+focalLengthArray2);
System.out.println("centreThicknessArray2 "+centreThicknessArray2);
System.out.println("lensClearApertureCentreArray2 "+lensClearApertureCentreArray2);
System.out.println("principalPointCentreArray2 "+principalPointCentreArray2);
System.out.println("uPeriodClearApertureArray2 "+uPeriodClearApertureArray2);
System.out.println("vPeriodClearApertureArray2 "+vPeriodClearApertureArray2);
System.out.println("uPeriodPrincipalPointArray2 "+uPeriodPrincipalPointArray2);
System.out.println("vPeriodPrincipalPointArray2 "+vPeriodPrincipalPointArray2);
System.out.println("------------------------boolean operators -------------------");
System.out.println("setUpWithCommonPoint "+setUpWithCommonPoint);
System.out.println("setUpGaborSuperer "+setUpGaborSuperer);
System.out.println("setUpBasicCLAs "+setUpBasicCLAs);
System.out.println("idealisedThickness "+idealisedThickness);
System.out.println("separatedArrays "+separatedArrays);
System.out.println("showEquivalent "+showEquivalent);
System.out.println("setUpBasicCLAs "+setUpBasicCLAs);
System.out.println("idealisedThickness "+idealisedThickness);
System.out.println("------------------------general and other params -------------------");
System.out.println("commonPlaneInterceptionPoint "+commonPlaneInterceptionPoint);
System.out.println("lensTrans "+lensTrans);
System.out.println("refractiveIndex "+refractiveIndex);
System.out.println("boundingBoxCentre "+boundingBoxCentre);
System.out.println("boundingBoxSpanVector1 "+boundingBoxSpanVector1);
System.out.println("boundingBoxSpanVector2 "+boundingBoxSpanVector2);
System.out.println("boundingBoxSpanVector3 "+boundingBoxSpanVector3);
System.out.println(Vector3D.crossProduct(uPeriodPrincipalPointArray1, vPeriodPrincipalPointArray1).getNormalised());


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
	
	//adding tims head to view through lens.
	scene.addSceneObject(new EditableTimHead(
			"Tim's head",	// description
			new Vector3D(0, 2, -11),//.getSumWith(new Vector3D(0.4,0.5,0)),
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
	}
	//general and camera
	private LabelledDoublePanel cameraAnglePanel, lensTransPanel, cameraZoomPanel, refractiveIndexPanel, cameraUpAnglePanel,cameraDistancePanel;
	private JCheckBox  showEquivCheck, separatedArraysCheck, setUpBasicCLAsCheck, idealisedThicknessCheck, setUpGaborSupererCheck, setUpWithCommonPointCheck;	
	private LabelledVector3DPanel boundingBoxCentrePanel, boundingBoxSpanVector1Panel, boundingBoxSpanVector2Panel, boundingBoxSpanVector3Panel, commonPlaneInterceptionPointPanel;
	private LabelledIntPanel maxStepsPanel;
	//array 1
	private LabelledDoublePanel focalLengthArray1Panel, centreThicknessArray1Panel;
	private LabelledVector3DPanel principalPointCentreArray1Panel, uPeriodPrincipalPointArray1Panel, vPeriodPrincipalPointArray1Panel,
	lensClearApertureCentreArray1Panel, uPeriodClearApertureArray1Panel, vPeriodClearApertureArray1Panel;
	//array 2
	private LabelledDoublePanel focalLengthArray2Panel, centreThicknessArray2Panel;
	private LabelledVector3DPanel principalPointCentreArray2Panel, uPeriodPrincipalPointArray2Panel, vPeriodPrincipalPointArray2Panel,
	lensClearApertureCentreArray2Panel, uPeriodClearApertureArray2Panel, vPeriodClearApertureArray2Panel;

	
	protected void createInteractiveControlPanel()
	{
		super.createInteractiveControlPanel();
		
		JTabbedPane tabbedPane = new JTabbedPane();
		interactiveControlPanel.add(tabbedPane, "span");
		
		
		//general panel
		
		JPanel generalpanel = new JPanel();
		generalpanel.setLayout(new MigLayout("insets 0"));
		tabbedPane.addTab("General", generalpanel);
		
		showEquivCheck = new JCheckBox("Show equal ideal CLAs");
		showEquivCheck.setSelected(showEquivalent);
		generalpanel.add(showEquivCheck, "span");
		
		separatedArraysCheck = new JCheckBox("Set the CLAs up as two arrays seperated by air");
		separatedArraysCheck.setSelected(separatedArrays);
		generalpanel.add(separatedArraysCheck, "span");
		
		setUpBasicCLAsCheck = new JCheckBox("Set up a basic CLA");
		setUpBasicCLAsCheck.setSelected(setUpBasicCLAs);
		generalpanel.add(setUpBasicCLAsCheck);
		
		idealisedThicknessCheck = new JCheckBox("Automatically caluclate the thickness for the basic CLAs");
		idealisedThicknessCheck.setSelected(idealisedThickness);
		generalpanel.add(idealisedThicknessCheck, "span");
		
		setUpGaborSupererCheck = new JCheckBox("Set up a Gabor Superer type CLA");
		setUpGaborSupererCheck.setSelected(setUpGaborSuperer);
		generalpanel.add(setUpGaborSupererCheck);
		
		setUpWithCommonPointCheck = new JCheckBox("Set up the Gabor superer lens using a common plane intersection point");
		setUpWithCommonPointCheck.setSelected(setUpWithCommonPoint);
		generalpanel.add(setUpWithCommonPointCheck, "span");
		
		commonPlaneInterceptionPointPanel = new LabelledVector3DPanel("common point where all planes meet for garbor superer lens");
		commonPlaneInterceptionPointPanel.setVector3D(commonPlaneInterceptionPoint);
		generalpanel.add(commonPlaneInterceptionPointPanel, "span");
		
		refractiveIndexPanel = new LabelledDoublePanel("refractive index of real lens(glass ~1.5)");
		refractiveIndexPanel.setNumber(refractiveIndex);
		generalpanel.add(refractiveIndexPanel, "span");
		
		lensTransPanel = new LabelledDoublePanel("Transmission of lens(es)");
		lensTransPanel.setNumber(lensTrans);
		generalpanel.add(lensTransPanel, "span");
		
		boundingBoxCentrePanel = new LabelledVector3DPanel("bounding box centre");
		boundingBoxCentrePanel.setVector3D(boundingBoxCentre);
		generalpanel.add(boundingBoxCentrePanel, "span");
		
		boundingBoxSpanVector1Panel = new LabelledVector3DPanel("bounding box span vector 1");
		boundingBoxSpanVector1Panel.setVector3D(boundingBoxSpanVector1);
		generalpanel.add(boundingBoxSpanVector1Panel, "span");
		
		boundingBoxSpanVector2Panel = new LabelledVector3DPanel("bounding box span vector 2");
		boundingBoxSpanVector2Panel.setVector3D(boundingBoxSpanVector2);
		generalpanel.add(boundingBoxSpanVector2Panel, "span");
		
		boundingBoxSpanVector3Panel = new LabelledVector3DPanel("bounding box span vector 3 (not needed for basic CLAs if idealisedThickness is checked)");
		boundingBoxSpanVector3Panel.setVector3D(boundingBoxSpanVector3);
		generalpanel.add(boundingBoxSpanVector3Panel, "span");

		//array 1 panel
		
		JPanel array1panel = new JPanel();
		array1panel.setLayout(new MigLayout("insets 0"));
		tabbedPane.addTab("Lens Array 1", array1panel);
		
		focalLengthArray1Panel = new LabelledDoublePanel("Focal Length of lenses");
		focalLengthArray1Panel.setNumber(focalLengthArray1);
		array1panel.add(focalLengthArray1Panel, "span");	
		
		centreThicknessArray1Panel = new LabelledDoublePanel("centre thickness. (set to -1 to auto calc for basic CLAs)");
		centreThicknessArray1Panel.setNumber(centreThicknessArray1);
		array1panel.add(centreThicknessArray1Panel, "span");
		
		principalPointCentreArray1Panel = new LabelledVector3DPanel("Centre of 00 Principal point");
		principalPointCentreArray1Panel.setVector3D(principalPointCentreArray1);
		array1panel.add(principalPointCentreArray1Panel, "span");
		
		uPeriodPrincipalPointArray1Panel = new LabelledVector3DPanel("U span vector of lenses");
		uPeriodPrincipalPointArray1Panel.setVector3D(uPeriodPrincipalPointArray1);
		array1panel.add(uPeriodPrincipalPointArray1Panel, "span");
		
		vPeriodPrincipalPointArray1Panel = new LabelledVector3DPanel("V span vector of lenses");
		vPeriodPrincipalPointArray1Panel.setVector3D(vPeriodPrincipalPointArray1);
		array1panel.add(vPeriodPrincipalPointArray1Panel, "span");
		
		lensClearApertureCentreArray1Panel = new LabelledVector3DPanel("Centre of 00 clear aperture");
		lensClearApertureCentreArray1Panel.setVector3D(lensClearApertureCentreArray1);
		array1panel.add(lensClearApertureCentreArray1Panel, "span");
		
		uPeriodClearApertureArray1Panel = new LabelledVector3DPanel("U span vector of clear aperture");
		uPeriodClearApertureArray1Panel.setVector3D(uPeriodClearApertureArray1);
		array1panel.add(uPeriodClearApertureArray1Panel, "span");
		
		vPeriodClearApertureArray1Panel = new LabelledVector3DPanel("V span vector of clear aperture");
		vPeriodClearApertureArray1Panel.setVector3D(vPeriodClearApertureArray1);
		array1panel.add(vPeriodClearApertureArray1Panel, "span");
		
		//array 2 panel
		
		JPanel array2panel = new JPanel();
		array2panel.setLayout(new MigLayout("insets 0"));
		tabbedPane.addTab("Lens Array 2", array2panel);

		focalLengthArray2Panel = new LabelledDoublePanel("Focal Length of lenses");
		focalLengthArray2Panel.setNumber(focalLengthArray2);
		array2panel.add(focalLengthArray2Panel, "span");
		
		centreThicknessArray2Panel = new LabelledDoublePanel("centre thickness. (set to -1 to auto calc for basic CLAs)");
		centreThicknessArray2Panel.setNumber(centreThicknessArray2);
		array2panel.add(centreThicknessArray2Panel, "span");
		
		principalPointCentreArray2Panel = new LabelledVector3DPanel("Centre of 00 Principal point");
		principalPointCentreArray2Panel.setVector3D(principalPointCentreArray2);
		array2panel.add(principalPointCentreArray2Panel, "span");
		
		uPeriodPrincipalPointArray2Panel = new LabelledVector3DPanel("U span vector of lenses");
		uPeriodPrincipalPointArray2Panel.setVector3D(uPeriodPrincipalPointArray2);
		array2panel.add(uPeriodPrincipalPointArray2Panel, "span");
		
		vPeriodPrincipalPointArray2Panel = new LabelledVector3DPanel("V span vector of lenses");
		vPeriodPrincipalPointArray2Panel.setVector3D(vPeriodPrincipalPointArray2);
		array2panel.add(vPeriodPrincipalPointArray2Panel, "span");
		
		lensClearApertureCentreArray2Panel = new LabelledVector3DPanel("Centre of 00 clear aperture");
		lensClearApertureCentreArray2Panel.setVector3D(lensClearApertureCentreArray2);
		array2panel.add(lensClearApertureCentreArray2Panel, "span");
		
		uPeriodClearApertureArray2Panel = new LabelledVector3DPanel("U span vector of clear aperture");
		uPeriodClearApertureArray2Panel.setVector3D(uPeriodClearApertureArray2);
		array2panel.add(uPeriodClearApertureArray2Panel, "span");
		
		vPeriodClearApertureArray2Panel = new LabelledVector3DPanel("V span vector of clear aperture");
		vPeriodClearApertureArray2Panel.setVector3D(vPeriodClearApertureArray2);
		array2panel.add(vPeriodClearApertureArray2Panel, "span");
		
		
		//camera panel
		
		JPanel panel = new JPanel();
		panel.setLayout(new MigLayout("insets 0"));
		tabbedPane.addTab("camera", panel);
		
		cameraAnglePanel = new LabelledDoublePanel("Angle of which the camera is looking at origin");
		cameraAnglePanel.setNumber(cameraAngle);
		panel.add(cameraAnglePanel, "span");
		
		cameraUpAnglePanel = new LabelledDoublePanel("Angle of which the camera is looking down at the origin");
		cameraUpAnglePanel.setNumber(cameraUpAngle);
		panel.add(cameraUpAnglePanel, "span");
		
		cameraZoomPanel = new LabelledDoublePanel("FOV of camera. Default = 80, decrease to 'zoom'");
		cameraZoomPanel.setNumber(cameraFOV);
		panel.add(cameraZoomPanel, "span");
		
		cameraDistancePanel = new LabelledDoublePanel("Camera distance");
		cameraDistancePanel.setNumber(cameraDistance);
		panel.add(cameraDistancePanel, "span");
		
		maxStepsPanel = new LabelledIntPanel("Max number of steps before returning black");
		maxStepsPanel.setNumber(maxSteps);
		panel.add(maxStepsPanel, "span");
		
		
	}
	protected void acceptValuesInInteractiveControlPanel()
	{
		
		//general
		showEquivalent = showEquivCheck.isSelected();
		separatedArrays = separatedArraysCheck.isSelected();
		setUpBasicCLAs = setUpBasicCLAsCheck.isSelected();
		idealisedThickness = idealisedThicknessCheck.isSelected();
		setUpGaborSuperer = setUpGaborSupererCheck.isSelected();
		setUpWithCommonPoint = setUpWithCommonPointCheck.isSelected();
		commonPlaneInterceptionPoint = commonPlaneInterceptionPointPanel.getVector3D();
		refractiveIndex =refractiveIndexPanel.getNumber();
		lensTrans = lensTransPanel.getNumber();
		boundingBoxCentre = boundingBoxCentrePanel.getVector3D();
		boundingBoxSpanVector1 = boundingBoxSpanVector1Panel.getVector3D();
		boundingBoxSpanVector2 = boundingBoxSpanVector2Panel.getVector3D();
		boundingBoxSpanVector3 = boundingBoxSpanVector3Panel.getVector3D();
		
		
		//Array1
		focalLengthArray1 = focalLengthArray1Panel.getNumber();
		centreThicknessArray1 = centreThicknessArray1Panel.getNumber();
		principalPointCentreArray1 = principalPointCentreArray1Panel.getVector3D();
		uPeriodPrincipalPointArray1 = uPeriodPrincipalPointArray1Panel.getVector3D();
		vPeriodPrincipalPointArray1 = vPeriodPrincipalPointArray1Panel.getVector3D();
		lensClearApertureCentreArray1 = lensClearApertureCentreArray1Panel.getVector3D();
		uPeriodClearApertureArray1 = uPeriodClearApertureArray1Panel.getVector3D();
		vPeriodClearApertureArray1 = vPeriodClearApertureArray1Panel.getVector3D();	

		
		//Array2
		focalLengthArray2 = focalLengthArray2Panel.getNumber();
		centreThicknessArray2 = centreThicknessArray2Panel.getNumber();
		principalPointCentreArray2 = principalPointCentreArray2Panel.getVector3D();
		uPeriodPrincipalPointArray2 = uPeriodPrincipalPointArray2Panel.getVector3D();
		vPeriodPrincipalPointArray2 = vPeriodPrincipalPointArray2Panel.getVector3D();
		lensClearApertureCentreArray2 = lensClearApertureCentreArray2Panel.getVector3D();
		uPeriodClearApertureArray2 = uPeriodClearApertureArray2Panel.getVector3D();
		vPeriodClearApertureArray2 = vPeriodClearApertureArray2Panel.getVector3D();
		
		
		//camera
		cameraAngle = cameraAnglePanel.getNumber();
		cameraUpAngle = cameraUpAnglePanel.getNumber();
		cameraFOV = cameraZoomPanel.getNumber();
		cameraDistance = cameraDistancePanel.getNumber();
		maxSteps = maxStepsPanel.getNumber();
	}
	
	public static void main(final String[] args)
	{
		(new RefractiveLensletTestExplorer()).run();
	}
}

