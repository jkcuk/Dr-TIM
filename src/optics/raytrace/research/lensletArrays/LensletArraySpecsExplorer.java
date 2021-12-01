package optics.raytrace.research.lensletArrays;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintStream;


import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import math.*;
import net.miginfocom.swing.MigLayout;
import optics.raytrace.sceneObjects.LensType;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.exceptions.SceneException;
import optics.raytrace.NonInteractiveTIMActionEnum;
import optics.raytrace.NonInteractiveTIMEngine;
import optics.raytrace.GUI.cameras.RenderQualityEnum;
import optics.raytrace.GUI.lowLevel.ApertureSizeType;
import optics.raytrace.GUI.lowLevel.DoublePanel;
import optics.raytrace.GUI.lowLevel.GUIBitsAndBobs;
import optics.raytrace.GUI.lowLevel.LabelledDoublePanel;
import optics.raytrace.GUI.lowLevel.LabelledVector3DPanel;
import optics.raytrace.GUI.sceneObjects.EditableCylinderLattice;
import optics.raytrace.GUI.sceneObjects.EditableRectangularLensletArray;
import optics.raytrace.GUI.sceneObjects.EditableTimHead;
import optics.raytrace.core.LightSource;
import optics.raytrace.core.SceneObjectClass;
import optics.raytrace.core.Studio;
import optics.raytrace.core.StudioInitialisationType;


/**
 * Simulate the visual appearance of combinations of lenslet arrays, and the view through them.
 * Additionally, floor tiles were converted to metric units such that 1 floor tile = 1 meter.
 * For ease of conversion, everything will be set to cm base units in the iteractive panel
 * 
 * @author Johannes Courtial, Maik Locher
 */

public class LensletArraySpecsExplorer extends NonInteractiveTIMEngine implements ActionListener
{
	/**
	 * focal length of lenslet array 1 (closer to object, located in plane z=0)
	 */
	private double f1;
	
	/**
	 * focal length of lenslet array 2 (closer to camera)
	 */
	private double f2;
	
	/**
	 * period of lenslet array 1
	 */
	private double period1;

	/**
	 * period of lenslet array 2
	 */
	private double period2;

	/**
	 * angle of normal to lenslet array 1 w.r.t. z axis
	 */
	private double theta1Deg;
	
	/**
	 * angle of normal to lenslet array 2 w.r.t. z axis
	 */
	private double theta2Deg;
			
	/**
	 * rotation angle of LA1 around its normal
	 */
	private double phi1Deg;

	/**
	 * rotation angle of LA2 around its normal
	 */
	private double phi2Deg;
	
	/**
	 * minimum z separation of LA centres
	 */
	private double minimumZSeparation;

	/**
	 * show lenslet array 1
	 */
	private boolean showLensletArray1;

	/**
	 * show lenslet array 2
	 */
	private boolean showLensletArray2;
	
	private LensType lensType;
	
	private boolean shadowThrowing;
	
	/**
	 * show a field lens in the common focal plane (if the telescopes are "focussed" on infinity)
	 * or in the plane of the image of the plane on which the telescopes are focussed;
	 * this works only if both f1 and f2 are positive, period1 = period2, and if theta1 = phi1 = theta2 = phi2 = 0
	 */
	private boolean showFieldLensArray;
	
	/**
	 * if true, focus on an object plane at z=objectZ
	 */
	private boolean focusOnObjectZ;
	
	/**
	 * if <focusOnObjectZ>, the camera focusses on an object plane at z=objectZ
	 */
	private double objectZ;

	
	/**
	 * if true, diffractive blur will be simulated
	 */
	private boolean simulateDiffractiveBlur;
	
	/**
	 * wavelength for which diffractive blur is simulated, in nm
	 */
	private double lambdaNM;
	
	/**
	 * A few boolean vaiables used as check boxes to lock and unlock relative movements
	 */
	private boolean lensLock, viewingLock, vectorViewingDirection;
	
	/**
	 * A few vectors for the lens centres.
	 */
	private Vector3D lensCentre, eyeCentre; 
	/**
	 * Some parameters to make the view direction more easy to use, an up and side angle. 
	 */
	private double upAngle, sideAngle;
	/**
	 * the size settings for the arrays
	 */
	private double apertureWidth1, apertureHeight1, apertureWidth2, apertureHeight2;
	
	/**
	 * setting the viewing object parameters
	 */
	private double objectRadius;
	private Vector3D objectCentre;
	private boolean timsHead, lattice, nonEditable;
	
	/**
	 * some parameters which can be set like the radius of the eye and the camera distance in cm
	 */
	private double eyeRadius;
	
	/**
	 * A converter that converts from cm to meters which in turn are equal to floor tiles here. 
	 */
	private double cmToM = 0.01, cameraDistanceCm;
	
	/**
	 * adding the items which define the orientation of the arrays(used to be in scene)
	 */
	private double theta1;
	private double phi1;
	private Vector3D normal1;
	private Vector3D right1;
	private double c1;
	private double s1;
	private Vector3D spanVector1;	// spanVector1
	private Vector3D spanVector2;	// spanVector2
	private double orientation; //sets the orientation for the normal to the first lenslet array. This is used to position the second lenslet array
	private Vector3D up = new Vector3D(0, 1, 0);

	//
	// the rest of the scene
	//
	
	/**
	 * Determines how to initialise the backdrop
	 */
	private StudioInitialisationType studioInitialisation;
	
	
	/**
	 * Constructor.
	 * Sets all parameters.
	 */
	public LensletArraySpecsExplorer()
	{
		super();
		
		// set to true for interactive version
		nonInteractiveTIMAction = NonInteractiveTIMActionEnum.INTERACTIVE;
		
		studioInitialisation = StudioInitialisationType.LATTICE;	// the backdrop
		
		// set all parameters
		
		renderQuality = RenderQualityEnum.DRAFT;
		
		// lenslet-array parameters
		apertureWidth1 = 1*100;
		apertureHeight1 =1*100;
		apertureWidth2 = 1*100;
		apertureHeight2 =1*100;
		f1 = 0.1*100;
		f2 = -f1;
		minimumZSeparation = 0.00001*cmToM;
		period1 = 0.01*100;
		period2 = 0.01*100;
		theta1Deg = 0; //between 0-360
		theta2Deg = 0;
		phi1Deg = 0;
		phi2Deg = 0;
		lensType = LensType.IDEAL_THIN_LENS;
		shadowThrowing = true;
		showFieldLensArray = false;
		showLensletArray1 = true;
		showLensletArray2 = true;
		focusOnObjectZ = false;
		objectZ = 8.95; // in m
		simulateDiffractiveBlur = true;
		lambdaNM = 632.8;
		
		//lenslet centre and other orientation params
		lensCentre = new Vector3D(0,0,0);

		
		
		
		//some true or false variables
		lensLock = true;
		viewingLock = true;
		vectorViewingDirection = false;
		
		//setting editable background stuff
		timsHead = false;
		lattice = true;
		objectRadius = 1; //in meters
		objectCentre = new Vector3D(0,0,10);
		
		

		// mounting the camera to an eye ball
		eyeRadius = 1.25; //average eye radius.
		cameraDistanceCm = 10*100;
		cameraApertureSize = ApertureSizeType.PINHOLE;
		upAngle = 0;
		sideAngle = 0;
		cameraHorizontalFOVDeg = 10;
		cameraFocussingDistance = 10; //in m
		
		
		if(nonInteractiveTIMAction == NonInteractiveTIMActionEnum.INTERACTIVE)
		{
			windowTitle = "Dr TIM's lenslet-array Spectacles explorer";
			windowWidth = 1500;
			windowHeight = 650;
		}
	}
	

	@Override
	public String getFirstPartOfFilename()
	{
		return "LensletArraySpecsExplorer"	// the name
//				+ (showLensletArray1?
//						" LA1 theta="+theta1Deg+"deg"
//						+" phi="+phi1Deg+"deg"
//						+" f="+f1
//						+" period="+period1
//						:"")
//				+ (showLensletArray2?
//						" LA2 theta="+theta2Deg+"deg"
//						+" phi="+phi2Deg+"deg"
//						+" f="+f2
//						+" period="+period2
//						:"")
//				+ " dz="+dz
//				+ " backdrop="+studioInitialisation.toString()
//				+ " cD="+cameraDistance
//				+ " cVD="+cameraViewDirection
//				+ " cFOV="+cameraHorizontalFOVDeg
//				+ " cAS="+cameraApertureSize
//				+ " cFD="+cameraFocussingDistance
				;
	}
	
	@Override
	public void writeParameters(PrintStream printStream)
	{
		// write any parameters not defined in NonInteractiveTIMEngine
		
		printStream.println("f1="+f1+"cm");
		printStream.println("f2="+f2+"cm");
		printStream.println("width1="+apertureWidth1+"cm");
		printStream.println("height1="+apertureHeight1+"cm");
		printStream.println("width2="+apertureWidth2+"cm");
		printStream.println("height2="+apertureHeight2+"cm");
		printStream.println("period1="+period1+"cm");
		printStream.println("period2="+period2+"cm");
		printStream.println("theta1Deg="+theta1Deg+"degrees");
		printStream.println("theta2Deg="+theta2Deg+"degrees");
		printStream.println("phi1Deg="+phi1Deg+"degrees");
		printStream.println("phi2Deg="+phi2Deg+"degrees");
		printStream.println("lensLock="+lensLock);
		printStream.println("minimumZSeparation="+minimumZSeparation+"cm");
		printStream.println("showLensletArray1="+showLensletArray1);
		printStream.println("showLensletArray2="+showLensletArray2);
		printStream.println("lensType="+lensType);
		printStream.println("shadowThrowing="+shadowThrowing);
		printStream.println("showFieldLens="+showFieldLensArray);
		printStream.println("focusOnObjectZ="+focusOnObjectZ);
		printStream.println("objectZ="+objectZ+"m");
		printStream.println("simulateDiffractiveBlur="+simulateDiffractiveBlur);
		printStream.println("lambdaNM="+lambdaNM+"nm");
		printStream.println("nonEditableBackground="+nonEditable);
		printStream.println("studioInitialisation="+studioInitialisation);
		printStream.println("timsHead="+timsHead);
		printStream.println("lattice="+lattice);
		printStream.println("objectRadius="+objectRadius);
		printStream.println("objectCentre="+objectCentre);

		printStream.println();

		// write all parameters defined in NonInteractiveTIMEngine
		super.writeParameters(printStream);		
	}

		
	@Override
	public void populateStudio()
	throws SceneException
	{
		// the studio
		studio = new Studio();

		// the scene
		SceneObjectContainer scene = new SceneObjectContainer("the scene", null, studio);
		studio.setScene(scene);
		
		
		/**
		 * Set the backdrop either with studio in
		 */

		

		
		if (nonEditable) {
			// initialise the scene and lights
			StudioInitialisationType.initialiseSceneAndLights(
					studioInitialisation,
					scene,
					studio
				);
		} else {
			
			studio.setLights(LightSource.getStandardLightsFromBehind());
			scene.addSceneObject(SceneObjectClass.getSkySphere(scene, studio));
			scene.addSceneObject(SceneObjectClass.getLighterChequerboardFloor(scene, studio));
			
			if (lattice) {
				scene.addSceneObject(new EditableCylinderLattice(
						"cylinder lattice",
						-objectRadius, objectRadius, 4,
						-objectRadius+0.02, objectRadius+0.02, 4,
						objectCentre.z, objectCentre.z+40, 4, 
						0.02,
						objectCentre.getDifferenceWith(new Vector3D(0,0,objectCentre.z)),
						scene,
						studio
				));	
			}
			if (timsHead) {
				scene.addSceneObject(new EditableTimHead(
						"Tim's head",
						objectCentre.getSumWith(new Vector3D(0,0,objectRadius)),
						objectRadius,	// radius
						new Vector3D(0, 0, -1),	// front direction
						new Vector3D(0, 1, 0),	// top direction
						new Vector3D(1, 0, 0),	// right direction
						scene,
						studio
					));
			}
		}
		

		
		
		
		
		double zSeparationFactor;
		if(focusOnObjectZ)
		{
			// to focus the telescopes on an object at object distance o, the separation between the two lenses has to be 1/(1-f1/o) (f1 + f2),
			// so everything is scaled by a factor 1/(1-f1/o)
			// object distance = objectZ (as LA1 is in the plane z=0)
			zSeparationFactor = 1./(1-f1*cmToM/objectZ);
		}
		else
		{
			zSeparationFactor = 1;
		}
		
		double zSeparation = zSeparationFactor * (f1+f2)*cmToM;
		// ... but when it gets too small, then Dr TIM doesn't recognise that there are two separate lenslet arrays
		if(Math.abs(zSeparation) < minimumZSeparation)
		{
			// in that case, set the z separation to some number (with the same sign as f1+f2) that is large enough
			// so that Dr TIM can tell the two lenslet arrays apart but small enough that the telescope array formed
			// by the two lenslet arrays still works well
			zSeparation = minimumZSeparation*MyMath.signumNever0((f1+f2)*cmToM);
		}
		if(zSeparation < 0)
		{
			// if the two lenslet arrays switch order, the imaging will change; put a warning in the console
			System.err.println("The separation between the two lenslet arrays is negative ("+zSeparation+"), which means they switch order");			
		}

		// System.out.println("zSeparation="+zSeparation);
		
//		double theta1 = MyMath.deg2rad(theta1Deg);
//		double phi1 = MyMath.deg2rad(phi1Deg);
//		Vector3D normal1 = new Vector3D(Math.sin(theta1), 0, Math.cos(theta1));
//		Vector3D right1 = Vector3D.crossProduct(normal1, up);
//		double c1 = Math.cos(phi1);
//		double s1 = Math.sin(phi1);
//		Vector3D spanVector1 = Vector3D.sum(right1.getProductWith(c1), up.getProductWith(s1));	// spanVector1
//		Vector3D spanVector2 = Vector3D.sum(right1.getProductWith(-s1), up.getProductWith(c1));	// spanVector2
//		double orientation; //sets the orientation for the normal to the first lenslet array. This is used to position the second lenslet array
		
		theta1 = MyMath.deg2rad(theta1Deg);
		phi1 = MyMath.deg2rad(phi1Deg);
		normal1 = new Vector3D(Math.sin(theta1), 0, Math.cos(theta1));
		right1 = Vector3D.crossProduct(normal1, up);
		c1 = Math.cos(phi1);
		s1 = Math.sin(phi1);
		spanVector1 = Vector3D.sum(right1.getProductWith(c1), up.getProductWith(s1));	// spanVector1
		spanVector2 = Vector3D.sum(right1.getProductWith(-s1), up.getProductWith(c1));	// spanVector2
		
		if (theta1Deg >= 90 && theta1Deg < 270) {
			orientation = -1;
		}else {
			orientation = 1;
		}
		
		scene.addSceneObject(new EditableRectangularLensletArray(
				"LA1",	// description
				lensCentre,	// centre
				spanVector1.getWithLength(apertureWidth1*cmToM),	// spanVector1 sets direction and size
				spanVector2.getWithLength(apertureHeight1*cmToM),	// spanVector2 sets direction and size
				f1*cmToM,	// focalLength
				period1*cmToM,	// xPeriod
				period1*cmToM,	// yPeriod
				0,	// xOffset
				0,	// yOffset
				lensType,	// lensType
				simulateDiffractiveBlur,
				lambdaNM*1e-9,
				0.96,	// throughputCoefficient
				false,	// reflective
				shadowThrowing,	// shadowThrowing
				scene,	// parent
				studio
			), 
			showLensletArray1
		);


		if (lensLock) {
			
			double theta2 = theta1;
			double phi2 = phi1;
			Vector3D right2 = right1;
			double c2 = c1;
			double s2 = s1;
	
			scene.addSceneObject(new EditableRectangularLensletArray(
					"LA2",	// description
					lensCentre.getSumWith((((Vector3D.crossProduct(spanVector1,spanVector2))).getWithLength(zSeparation*orientation))),	// centre which is always a distance zseperation away along the normal to first array
					Vector3D.sum(right2.getProductWith(c2), up.getProductWith(s2)).getWithLength(apertureWidth2*cmToM),	// spanVector1 sets direction and size
					Vector3D.sum(right2.getProductWith(-s2), up.getProductWith(c2)).getWithLength(apertureHeight2*cmToM),	// spanVector2 sets direction and size
					f2*cmToM,	// focalLength
					period2*cmToM,	// xPeriod
					period2*cmToM,	// yPeriod
					0,	// xOffset
					0,	// yOffset
					lensType,
					simulateDiffractiveBlur,
					lambdaNM*1e-9,
					0.96,	// throughputCoefficient
					false,	// reflective
					shadowThrowing,	// shadowThrowing
					scene,	// parent
					studio
				), 
				showLensletArray2
			);
			
			// field-lens array
			
			// this works only if both f1 and f2 are positive, period1 = period2, and if theta1 = phi1 = theta2 = phi2 = 0
			if((f1 > 0) && (f2 > 0) && (period1 == period2) && (theta1 == 0) && (phi1 == 0) && (theta2 == 0) && (phi2 == 0))
			{
				// 1/fField == 1/f1 + 1/f2
				// field lens has to be a distance i behind LA1, where 1/objectZ + 1/i = 1/f1, so 1/i = 1/f1 - 1/objectZ
				scene.addSceneObject(new EditableRectangularLensletArray(
						"field-lens array",	// description
						new Vector3D(0, 0, (focusOnObjectZ?-1/(1/(f1*cmToM) - 1/objectZ):-(f1*cmToM))),	// centre
						right1,	// spanVector1
						up,	// spanVector2
						1/(1/(cmToM*f1*zSeparationFactor) + 1/(cmToM*f2*zSeparationFactor)),	// focalLength
						period1*cmToM,	// xPeriod
						period1*cmToM,	// yPeriod
						0,	// xOffset
						0,	// yOffset
						lensType,
						simulateDiffractiveBlur,
						lambdaNM*1e-9,
						0.96,	// throughputCoefficient
						false,	// reflective
						shadowThrowing,	// shadowThrowing
						scene,	// parent
						studio
					), 
					showFieldLensArray
				);
			}
			
		}else {
			
			double theta2 = MyMath.deg2rad(theta2Deg);
			double phi2 = MyMath.deg2rad(phi2Deg);
			Vector3D normal2 = new Vector3D(Math.sin(theta2), 0, Math.cos(theta2));
			Vector3D right2 = Vector3D.crossProduct(normal2, up);
			double c2 = Math.cos(phi2);
			double s2 = Math.sin(phi2);
			
			
			scene.addSceneObject(new EditableRectangularLensletArray(
					"LA2",	// description
					lensCentre.getSumWith(((Vector3D.crossProduct(spanVector1,spanVector2)).getWithLength(zSeparation*orientation))),	// centre which is always a distance zseperation away along the normal to first array
					Vector3D.sum(right2.getProductWith(c2), up.getProductWith(s2)).getWithLength(apertureWidth2*cmToM),	// spanVector1 sets direction and size
					Vector3D.sum(right2.getProductWith(-s2), up.getProductWith(c2)).getWithLength(apertureHeight2*cmToM),	// spanVector2 sets direction and size
					f2*cmToM,	// focalLength
					period2*cmToM,	// xPeriod
					period2*cmToM,	// yPeriod
					0,	// xOffset
					0,	// yOffset
					lensType,
					simulateDiffractiveBlur,
					lambdaNM*1e-9,
					0.96,	// throughputCoefficient
					false,	// reflective
					shadowThrowing,	// shadowThrowing
					scene,	// parent
					studio
				), 
				showLensletArray2
			);
		}

		
		
	
		
		// the camera
		// cameraFocussingDistance 
		cameraDistance = cmToM*cameraDistanceCm; 
		double allRadius = (cmToM*eyeRadius+cameraDistance);
		//makes it so the camera is always looking at the lenslet array along its surface normal. i.e 90 degrees to lenslet.
		if (viewingLock) {
			sideAngle = theta1Deg;
			upAngle = 0;
		}
		//in spherical coordinates where the view along the z axis is upAngle = 0 sideAngle = 0 
		cameraViewDirection = new Vector3D(Math.cos(MyMath.deg2rad(90 - sideAngle))*Math.sin(MyMath.deg2rad(90 - upAngle)), //x
											Math.cos(MyMath.deg2rad(90 - upAngle)), //y
											Math.sin(MyMath.deg2rad(90 - sideAngle))*Math.sin(MyMath.deg2rad(90 - upAngle))); //z

			if (cameraApertureSize == ApertureSizeType.EYE) {
				 eyeCentre = lensCentre.getSumWith((((Vector3D.crossProduct(spanVector1,spanVector2))).getWithLength((allRadius))));
//				 System.out.println(eyeCentre);
//				 System.out.println(allRadius);
//				 System.out.println((Vector3D.crossProduct(spanVector1,spanVector2)));
				 cameraViewCentre = eyeCentre.getSumWith(cameraViewDirection.getWithLength(allRadius));
				 
				 
				 
			}else {
				Vector3D centre = lensCentre.getSumWith((((Vector3D.crossProduct(spanVector1,spanVector2)).getNormalised()).getWithLength(cameraDistance*orientation)));
				cameraViewCentre = centre.getSumWith(cameraViewDirection.getWithLength(cameraDistance));
				//System.out.println(cameraViewCentre);
			}

			

			
//			System.out.println(cameraViewCentre);

		studio.setCamera(getStandardCamera());
	}

	
	
	//
	// for interactive version
	//
	
	public enum LensletArraysInitialisationType
	{
		INIT("Initialise lenslet arrays to..."),
		GABOR("Gabor superlens"),
		MOIRE("Moir\u00E9 magnifier"),
		CLAS("Confocal lenslet arrays");
		
		private String description;
		private LensletArraysInitialisationType(String description) {this.description = description;}	
		@Override
		public String toString() {return description;}
	}

	
	private LabelledDoublePanel f1Panel, f2Panel, minimumZSeprarationPanel, period1Panel, period2Panel, apertureWidth1Panel, apertureHeight1Panel, apertureWidth2Panel, apertureHeight2Panel, objectRadiusPanel;
	private DoublePanel theta1DegPanel, theta2DegPanel, phi1DegPanel, phi2DegPanel, lambdaNMPanel, objectZPanel; // , etaPanel, separationPanel
	private LabelledVector3DPanel objectCentrePanel;
	private JCheckBox showLensletArray1CheckBox, showLensletArray2CheckBox, shadowThrowingCheckBox, simulateDiffractiveBlurCheckBox, showFieldLensArrayCheckBox, focusOnObjectCheckBox;
	private JCheckBox timsHeadCheckBox, latticeCheckBox, nonEditableCheckBox; 
	private JCheckBox lensLockCheckBox, viewingLockCheckBox, vectorViewingDirectionCheckBox;
	private JComboBox<LensletArraysInitialisationType> lensletArraysInitialisationComboBox;
	private JComboBox<LensType> lensTypeComboBox;
	private JComboBox<StudioInitialisationType> studioInitialisationComboBox;


	// camera stuff
	// private LabelledVector3DPanel cameraViewDirectionPanel;
	private LabelledDoublePanel cameraDistanceCmPanel, upAnglePanel, sideAnglePanel;
	private LabelledVector3DPanel cameraViewDirectionPanel;
	private DoublePanel cameraHorizontalFOVDegPanel;
	private JComboBox<ApertureSizeType> cameraApertureSizeComboBox;
	private LabelledDoublePanel cameraFocussingDistancePanel;



	
	/**
	 * add controls to the interactive control panel;
	 * override to modify
	 */
	@Override
	protected void createInteractiveControlPanel()
	{
		super.createInteractiveControlPanel();

		// the main tabbed pane, with "Scene" and "Camera" tabs
		JTabbedPane sceneCameraTabbedPane = new JTabbedPane();
		interactiveControlPanel.add(sceneCameraTabbedPane, "span");
		
		//
		// the Lenslet arrays panel
		//

		JPanel scenePanel = new JPanel();
		scenePanel.setLayout(new MigLayout("insets 0"));
		sceneCameraTabbedPane.addTab("Scene", scenePanel);

		//
		// the Lenslet-arrays-initialisation panel
		//

		// scenePanel.add(new JLabel("Lenslet-array initialisation"));
		
		JPanel initPanel = new JPanel();
		initPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Initialisation of lenslet arrays"));
		initPanel.setLayout(new MigLayout("insets 0"));
		scenePanel.add(initPanel, "span");

		lensletArraysInitialisationComboBox = new JComboBox<LensletArraysInitialisationType>(LensletArraysInitialisationType.values());
		lensletArraysInitialisationComboBox.setSelectedItem(LensletArraysInitialisationType.INIT);
		lensletArraysInitialisationComboBox.addActionListener(this);
//		lensletArraysInitialisationButton = new JButton("Go");
//		lensletArraysInitialisationButton.addActionListener(this);
		initPanel.add(
				GUIBitsAndBobs.makeRow("Either", 
						lensletArraysInitialisationComboBox
						)
				, "span");

		// TODO
//		etaPanel, separationPanel, objectDistancePanel
//		etaSOInitialisationButton = new JButton()
		
//		scenePanel.add(new JLabel("Lenslet-array initialisation"), "span");
//		JTabbedPane laInitTabbedPane = new JTabbedPane();
//		scenePanel.add(laInitTabbedPane, "span");
//		
//		//
//		// the Gabor superlens initialisation panel
//		//
//		
//		JPanel gaborSuperlensPanel = new JPanel();
//		gaborSuperlensPanel.setLayout(new MigLayout("insets 0"));
//		laInitTabbedPane.addTab("Gabor superlens",
//				gaborSuperlensPanel);
//		
//		gaborInitialisationButton = new JButton("Initialise");
//		gaborInitialisationButton.addActionListener(this);
//		gaborSuperlensPanel.add(gaborInitialisationButton, "span");
//
//		//
//		// the moire magnifier initialisation panel
//		//
//		
//		JPanel moireMagnifierPanel = new JPanel();
//		moireMagnifierPanel.setLayout(new MigLayout("insets 0"));
//		laInitTabbedPane.addTab("Moire magnifier", 
//				moireMagnifierPanel);
//
//		moireInitialisationButton = new JButton("Initialise");
//		moireInitialisationButton.addActionListener(this);
//		moireMagnifierPanel.add(moireInitialisationButton, "span");
//
//		//
//		// the confocal lenslet arrays initialisation panel
//		//
//		
//		JPanel clasPanel = new JPanel();
//		clasPanel.setLayout(new MigLayout("insets 0"));
//		laInitTabbedPane.addTab("Confocal lenslet arrays",
//				clasPanel);
//
//		clasInitialisationButton = new JButton("Initialise");
//		clasInitialisationButton.addActionListener(this);
//		clasPanel.add(clasInitialisationButton, "span");

		//
		// the LA1 panel
		//
		
		JPanel la1Panel = new JPanel();
		la1Panel.setBorder(GUIBitsAndBobs.getTitledBorder("Lenslet array 1 (closer to object)"));
		la1Panel.setLayout(new MigLayout("insets 0"));
		// scenePanel.add(la1Panel, "span");
		
		//apertureWidth1Panel, apertureHeight1Panel
		apertureWidth1Panel = new LabelledDoublePanel("Width");
		apertureWidth1Panel.setNumber(apertureWidth1);
		apertureWidth1Panel.setToolTipText("The over all width of the first array");
		la1Panel.add(GUIBitsAndBobs.makeRow(apertureWidth1Panel,"cm"), "span");
		
		apertureHeight1Panel = new LabelledDoublePanel("Height");
		apertureHeight1Panel.setNumber(apertureHeight1);
		apertureHeight1Panel.setToolTipText("The over all height of the first array");
		la1Panel.add(GUIBitsAndBobs.makeRow(apertureHeight1Panel,"cm"), "span");

		f1Panel = new LabelledDoublePanel("f");
		f1Panel.setNumber(f1);
		f1Panel.setToolTipText("Focal length of the lenslets");
		// la1Panel.add(f1Panel, "span");

		showLensletArray1CheckBox = new JCheckBox("Show");
		showLensletArray1CheckBox.setSelected(showLensletArray1);
		la1Panel.add(GUIBitsAndBobs.makeRow(f1Panel,"cm",  showLensletArray1CheckBox), "span");
		
		period1Panel = new LabelledDoublePanel("Period");
		period1Panel.setNumber(period1);
		period1Panel.setToolTipText("Period, i.e. distance between neighbouring lenslets");
		la1Panel.add(GUIBitsAndBobs.makeRow(period1Panel,"cm"), "span");

		theta1DegPanel = new DoublePanel();
		theta1DegPanel.setNumber(theta1Deg);
		theta1DegPanel.setToolTipText("Angle of the array normal with the z axis");
		la1Panel.add(GUIBitsAndBobs.makeRow("theta", theta1DegPanel, "degrees"), "span");

		phi1DegPanel = new DoublePanel();
		phi1DegPanel.setNumber(phi1Deg);
		phi1DegPanel.setToolTipText("Angle by which the array is rotated around the array normal");
		la1Panel.add(GUIBitsAndBobs.makeRow("phi", phi1DegPanel, "degrees"), "span");



		//
		// the LA2 panel
		//
		
		JPanel la2Panel = new JPanel();
		la2Panel.setBorder(GUIBitsAndBobs.getTitledBorder("Lenslet array 2 (closer to camera)"));
		la2Panel.setLayout(new MigLayout("insets 0"));
		// scenePanel.add(la2Panel, "span");
		
		//apertureWidth2Panel, apertureHeight2Panel;
		apertureWidth2Panel = new LabelledDoublePanel("Width");
		apertureWidth2Panel.setNumber(apertureWidth2);
		apertureWidth2Panel.setToolTipText("The over all width of the second array");
		la2Panel.add(GUIBitsAndBobs.makeRow(apertureWidth2Panel,"cm"), "span");
		
		apertureHeight2Panel = new LabelledDoublePanel("Height");
		apertureHeight2Panel.setNumber(apertureHeight2);
		apertureHeight2Panel.setToolTipText("The over all height of the second array");
		la2Panel.add(GUIBitsAndBobs.makeRow(apertureHeight2Panel,"cm"), "span");
		
		f2Panel = new LabelledDoublePanel("f");
		f2Panel.setNumber(f2);
		f2Panel.setToolTipText("Focal length of the lenslets");
		// la2Panel.add(f2Panel, "span");
		
		showLensletArray2CheckBox = new JCheckBox("Show");
		showLensletArray2CheckBox.setSelected(showLensletArray2);
		// la2Panel.add(showLensletArray2CheckBox, "span");
		la2Panel.add(GUIBitsAndBobs.makeRow(f2Panel,"cm",  showLensletArray2CheckBox), "span");

		period2Panel = new LabelledDoublePanel("Period");
		period2Panel.setNumber(period2);
		period2Panel.setToolTipText("Period, i.e. distance between neighbouring lenslets");
		la2Panel.add(GUIBitsAndBobs.makeRow(period2Panel,"cm"), "span");

		// viewingLockCheckBox, vectorViewingDirectionCheckBox;
		
		lensLockCheckBox = new JCheckBox("Lock");
		lensLockCheckBox.setSelected(lensLock);
		lensLockCheckBox.setToolTipText("Locks/sets the angles of the second array to the first one");
		la2Panel.add(lensLockCheckBox, "span");
		
		theta2DegPanel = new DoublePanel();
		theta2DegPanel.setNumber(theta2Deg);
		theta2DegPanel.setToolTipText("Angle of the array normal with the z axis");
		
		phi2DegPanel = new DoublePanel();
		phi2DegPanel.setNumber(phi2Deg);
		phi2DegPanel.setToolTipText("Angle by which the array is rotated around the array normal");
		
		
		
		la2Panel.add(GUIBitsAndBobs.makeRow("theta", theta2DegPanel, "degrees"), "span");
		la2Panel.add(GUIBitsAndBobs.makeRow("phi", phi2DegPanel, "degrees"), "span");


		// add the LA panels
		scenePanel.add(GUIBitsAndBobs.makeRow(la1Panel, la2Panel), "span");
		
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
		
		
		viewingLockCheckBox = new JCheckBox("View array centre");
		viewingLockCheckBox.setSelected(viewingLock);
		viewingLockCheckBox.setToolTipText("Sets the camera viewing angle such that it faces to the array centre");
		cameraPanel.add(viewingLockCheckBox, "span");
		
		cameraApertureSizeComboBox = new JComboBox<ApertureSizeType>(ApertureSizeType.values());
		cameraApertureSizeComboBox.setSelectedItem(cameraApertureSize);
		cameraPanel.add(GUIBitsAndBobs.makeRow("Camera aperture", cameraApertureSizeComboBox), "span");	
		
		cameraDistanceCmPanel = new LabelledDoublePanel("Camera distance");
		cameraDistanceCmPanel.setNumber(cameraDistanceCm);
		cameraPanel.add(GUIBitsAndBobs.makeRow(cameraDistanceCmPanel,"cm"), "span");
		
		cameraHorizontalFOVDegPanel = new DoublePanel();
		cameraHorizontalFOVDegPanel.setNumber(cameraHorizontalFOVDeg);
		cameraPanel.add(GUIBitsAndBobs.makeRow("Horizontal FOV", cameraHorizontalFOVDegPanel, "°"), "span");
		
		cameraFocussingDistancePanel = new LabelledDoublePanel("Focussing distance");
		cameraFocussingDistancePanel.setNumber(cameraFocussingDistance);
		cameraPanel.add(GUIBitsAndBobs.makeRow(cameraFocussingDistancePanel,"m"), "span");
		
		// cameraLockCheckBox, viewingLockCheckBox, vectorViewingDirectionCheckBox;
		

		
		vectorViewingDirectionCheckBox = new JCheckBox("Set vector viewing direction to ");
		vectorViewingDirectionCheckBox.setSelected(vectorViewingDirection);
		vectorViewingDirectionCheckBox.setToolTipText("Allows the viewing direction to be set as a vector");
		cameraPanel.add(vectorViewingDirectionCheckBox);
		
		cameraViewDirectionPanel = new LabelledVector3DPanel("");
		cameraViewDirectionPanel.setVector3D(cameraViewDirection);
		
		upAnglePanel = new LabelledDoublePanel("Vertical view angle");
		upAnglePanel.setNumber(upAngle);
		upAnglePanel.setToolTipText("The up/down angle of the eye/camera where 90 degrees is staright up at the sky");
		
		sideAnglePanel = new LabelledDoublePanel("Horizontal view angle");
		sideAnglePanel.setNumber(sideAngle);
		upAnglePanel.setToolTipText("The side to side angle of the eye/camera where 90 degrees is staright to the right");

		
		cameraPanel.add(cameraViewDirectionPanel, "span");
		cameraPanel.add(GUIBitsAndBobs.makeRow(upAnglePanel,"degrees"));		
		cameraPanel.add(GUIBitsAndBobs.makeRow(sideAnglePanel,"degrees"), "span");
		

		

		
		



		//
		// common LA parameters panel
		//
		
		JPanel commonLAParametersPanel = new JPanel();
		commonLAParametersPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Common LA parameters"));
		commonLAParametersPanel.setLayout(new MigLayout("insets 0"));
		sceneCameraTabbedPane.add(commonLAParametersPanel, "Common LA parameters");

		
		lensTypeComboBox = new JComboBox<LensType>(LensType.values());
		lensTypeComboBox.setSelectedItem(lensType);
		shadowThrowingCheckBox = new JCheckBox("Shadow throwing");
		shadowThrowingCheckBox.setSelected(shadowThrowing);
		commonLAParametersPanel.add(GUIBitsAndBobs.makeRow(lensTypeComboBox, shadowThrowingCheckBox), "span");
		
		showFieldLensArrayCheckBox = new JCheckBox("Add array of field lenses in common focal plane");
		showFieldLensArrayCheckBox.setSelected(showFieldLensArray);
		commonLAParametersPanel.add(showFieldLensArrayCheckBox, "span");
		
		
		//
		
		focusOnObjectCheckBox = new JCheckBox("Focus on object at z=");
		focusOnObjectCheckBox.setSelected(focusOnObjectZ);
		objectZPanel = new DoublePanel();
		objectZPanel.setNumber(objectZ);
		commonLAParametersPanel.add(GUIBitsAndBobs.makeRow(focusOnObjectCheckBox, objectZPanel, new JLabel("m (CLAs only)")), "span");
		

		
		

		simulateDiffractiveBlurCheckBox = new JCheckBox("");
		simulateDiffractiveBlurCheckBox.setSelected(simulateDiffractiveBlur);
		lambdaNMPanel = new DoublePanel();
		lambdaNMPanel.setNumber(lambdaNM);
		commonLAParametersPanel.add(GUIBitsAndBobs.makeRow(simulateDiffractiveBlurCheckBox, "Simulate diffractive blur for wavelength", lambdaNMPanel, "nm"), "span");

		minimumZSeprarationPanel = new LabelledDoublePanel("Minimum z separation between array centres");
		minimumZSeprarationPanel.setNumber(minimumZSeparation);
		minimumZSeprarationPanel.setToolTipText("The centres of the two lenslet arrays are separated in the z direction by a distance f1 + f2, unless this is less than the minimum z separation");
		commonLAParametersPanel.add(GUIBitsAndBobs.makeRow(minimumZSeprarationPanel,"cm"), "span");

		
		//
		// rest-of-the-scene panel
		//
		
		JPanel restOfScenePanel = new JPanel();
		restOfScenePanel.setBorder(GUIBitsAndBobs.getTitledBorder("Background"));
		restOfScenePanel.setLayout(new MigLayout("insets 0"));
		scenePanel.add(restOfScenePanel, "span");
		
		
		nonEditableCheckBox = new JCheckBox("Use basic non-editable background:");
		nonEditableCheckBox.setSelected(nonEditable);
		nonEditableCheckBox.setToolTipText("When this is ticked you can use the items from the drop down menu but no distance or size can be adjusted");
		restOfScenePanel.add(nonEditableCheckBox);
		
		studioInitialisationComboBox = new JComboBox<StudioInitialisationType>(StudioInitialisationType.limitedValuesForBackgrounds);
		studioInitialisationComboBox.setSelectedItem(studioInitialisation);
		restOfScenePanel.add(studioInitialisationComboBox, "span");
		
		
		
		timsHeadCheckBox = new JCheckBox("Use Tims head as a background");
		timsHeadCheckBox.setSelected(timsHead);
		restOfScenePanel.add(timsHeadCheckBox);
		
		latticeCheckBox = new JCheckBox("Use the lattice as a background");
		latticeCheckBox.setSelected(lattice);
		restOfScenePanel.add(latticeCheckBox,"span");
		
		objectRadiusPanel = new LabelledDoublePanel("Set the Radius ");
		objectRadiusPanel.setNumber(objectRadius);
		restOfScenePanel.add(GUIBitsAndBobs.makeRow(objectRadiusPanel,"m"),"span");
		
		objectCentrePanel = new LabelledVector3DPanel("and the front centre");
		objectCentrePanel.setVector3D(objectCentre);
		restOfScenePanel.add(GUIBitsAndBobs.makeRow(objectCentrePanel,"m  of the background for either Tim or Lattice"),"span");
		
		
		
		
		
		
		
		

		
	}
	
	/**
	 * called before rendering;
	 * override when adding fields
	 */
	@Override
	protected void acceptValuesInInteractiveControlPanel()
	{
		super.acceptValuesInInteractiveControlPanel();
		
		apertureHeight1 = apertureHeight1Panel.getNumber();
		apertureWidth1 = apertureWidth1Panel.getNumber();
		apertureHeight2 = apertureHeight2Panel.getNumber();
		apertureWidth2 = apertureWidth2Panel.getNumber();
		f1 = f1Panel.getNumber();
		f2 = f2Panel.getNumber();
		minimumZSeparation = minimumZSeprarationPanel.getNumber();
		period1 = period1Panel.getNumber();
		period2 = period2Panel.getNumber();
		theta1Deg = theta1DegPanel.getNumber();
		phi1Deg = phi1DegPanel.getNumber();
		lensLock = lensLockCheckBox.isSelected();
		if (lensLock) {
			theta2Deg = theta1DegPanel.getNumber();
			phi2Deg = phi1DegPanel.getNumber();
		}else {
			theta2Deg = theta2DegPanel.getNumber();
			phi2Deg = phi2DegPanel.getNumber();
			
		}

		

		
		showLensletArray1 = showLensletArray1CheckBox.isSelected();
		showLensletArray2 = showLensletArray2CheckBox.isSelected();
		lensType = (LensType)(lensTypeComboBox.getSelectedItem());
		shadowThrowing = shadowThrowingCheckBox.isSelected();
		showFieldLensArray = showFieldLensArrayCheckBox.isSelected();
		focusOnObjectZ = focusOnObjectCheckBox.isSelected();
		objectZ = objectZPanel.getNumber();
		simulateDiffractiveBlur = simulateDiffractiveBlurCheckBox.isSelected();
		lambdaNM = lambdaNMPanel.getNumber();
		
		
		nonEditable = nonEditableCheckBox.isSelected();
		studioInitialisation = (StudioInitialisationType)(studioInitialisationComboBox.getSelectedItem());
		timsHead = timsHeadCheckBox.isSelected();
		lattice = latticeCheckBox.isSelected();
		objectCentre = objectCentrePanel.getVector3D();
		objectRadius = objectRadiusPanel.getNumber();
		
		
		// cameraViewDirection = cameraViewDirectionPanel.getVector3D();
		viewingLock = viewingLockCheckBox.isSelected();
		vectorViewingDirection = vectorViewingDirectionCheckBox.isSelected();
		if (viewingLock) {
			
		} else if(vectorViewingDirection) {
			cameraViewDirection = cameraViewDirectionPanel.getVector3D();
			}else {
				upAngle = upAnglePanel.getNumber();
				sideAngle = sideAnglePanel.getNumber();	
			}
		cameraDistanceCm = cameraDistanceCmPanel.getNumber();
		cameraHorizontalFOVDeg = cameraHorizontalFOVDegPanel.getNumber();
		cameraApertureSize = (ApertureSizeType)(cameraApertureSizeComboBox.getSelectedItem());
		cameraFocussingDistance = cameraFocussingDistancePanel.getNumber();
	}
	
	/**
	 * initialise the lenslet-array parameters to a Gabor superlens
	 */
	private void gaborInitialisation()
	{
		// lenslet-array parameters
		f1Panel.setNumber(0.1*100);
		f2Panel.setNumber(-0.1*100);
		minimumZSeprarationPanel.setNumber(0.00001*100);
		period1Panel.setNumber(0.0099*100);
		period2Panel.setNumber(0.01*100);
		theta1DegPanel.setNumber(0);
		theta2DegPanel.setNumber(0);
		phi1DegPanel.setNumber(0);
		phi2DegPanel.setNumber(0);
		showLensletArray1CheckBox.setSelected(true);
		showLensletArray2CheckBox.setSelected(true);
		showFieldLensArrayCheckBox.setSelected(false);
	}

	/**
	 * initialise the lenslet-array parameters to a Moire magnifier
	 */
	private void moireInitialisation()
	{
		// lenslet-array parameters
		f1Panel.setNumber(-0.1*100);
		f2Panel.setNumber(0.1*100);
		minimumZSeprarationPanel.setNumber(0.00001*100);
		period1Panel.setNumber(0.01*100);
		period2Panel.setNumber(0.01*100);
		theta1DegPanel.setNumber(0);
		theta2DegPanel.setNumber(0);
		phi1DegPanel.setNumber(0);
		phi2DegPanel.setNumber(0.1);
		showLensletArray1CheckBox.setSelected(true);
		showLensletArray2CheckBox.setSelected(true);
		showFieldLensArrayCheckBox.setSelected(false);
	}

	/**
	 * initialise the lenslet-array parameters to a Moire magnifier
	 */
	private void clasInitialisation()
	{
		// lenslet-array parameters
		f1Panel.setNumber(-0.05*100);
		f2Panel.setNumber(0.1*100);
		minimumZSeprarationPanel.setNumber(0.00001*100);
		period1Panel.setNumber(0.01*100);
		period2Panel.setNumber(0.01*100);
		theta1DegPanel.setNumber(0);
		theta2DegPanel.setNumber(0);
		phi1DegPanel.setNumber(0);
		phi2DegPanel.setNumber(0);
		showLensletArray1CheckBox.setSelected(true);
		showLensletArray2CheckBox.setSelected(true);
		showFieldLensArrayCheckBox.setSelected(false);
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		super.actionPerformed(e);
		
		if(e.getSource().equals(lensletArraysInitialisationComboBox))
		{
			switch((LensletArraysInitialisationType)(lensletArraysInitialisationComboBox.getSelectedItem()))
			{
			case GABOR:
				gaborInitialisation();
				break;
			case MOIRE:
				moireInitialisation();
				break;
			case CLAS:
				clasInitialisation();
				break;
			case INIT:
			default:
				// do nothing
			}
			lensletArraysInitialisationComboBox.setSelectedItem(LensletArraysInitialisationType.INIT);
		}

		
//		if(e.getSource().equals(gaborInitialisationButton))
//		{
//			gaborInitialisation();
//		}
//		else if(e.getSource().equals(moireInitialisationButton))
//		{
//			moireInitialisation();
//		}
//		else if(e.getSource().equals(clasInitialisationButton))
//		{
//			clasInitialisation();
//		}
	}


	//
	// the main method, so that this can be run as a Java application
	//

	/**
	 * Called when this is run; don't touch!
	 * @param args
	 */
	public static void main(final String[] args)
	{
		(new LensletArraySpecsExplorer()).run();
	}
}