package optics.raytrace.research.GaborSupererLens;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintStream;
import java.text.NumberFormat;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

import math.*;
import net.miginfocom.swing.MigLayout;
import optics.raytrace.sceneObjects.LensType;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.surfaces.SurfaceColour;
import optics.raytrace.exceptions.RayTraceException;
import optics.raytrace.exceptions.SceneException;
import optics.raytrace.NonInteractiveTIMActionEnum;
import optics.raytrace.NonInteractiveTIMEngine;
import optics.raytrace.GUI.cameras.RenderQualityEnum;
import optics.raytrace.GUI.lowLevel.ApertureSizeType;
import optics.raytrace.GUI.lowLevel.DoublePanel;
import optics.raytrace.GUI.lowLevel.GUIBitsAndBobs;
import optics.raytrace.GUI.lowLevel.LabelledDoublePanel;
import optics.raytrace.GUI.lowLevel.LabelledVector2DPanel;
import optics.raytrace.GUI.lowLevel.LabelledVector3DPanel;
import optics.raytrace.GUI.sceneObjects.EditableLensletArrayForGaborSupererLens;
import optics.raytrace.GUI.sceneObjects.EditableScaledParametrisedSphere;
import optics.raytrace.core.Studio;
import optics.raytrace.core.StudioInitialisationType;


/**
 * Simulate the visual appearance of a Gabor super-er lens, compare with Gabor superlens, etc.
 * 
 * @author Johannes Courtial
 */
/**
 * @author johannes
 *
 */
/**
 * @author johannes
 *
 */
public class GaborSupererLensExplorer extends NonInteractiveTIMEngine implements ActionListener
{
	/**
	 * focal length of lenslet array 1
	 */
	private double f1;
	
	/**
	 * focal length of lenslet array 2
	 */
	private double f2;
	
	/**
	 * period of the array of clear apertures in lenslet array 1
	 */
	private double aperturesPeriod1;

	/**
	 * period of the array of clear apertures in lenslet array 2
	 */
	private double aperturesPeriod2;

	/**
	 * period of the array of principal points in lenslet array 1
	 */
	private double principalPointsPeriod1;

	/**
	 * period of the array of principal points in lenslet array 2
	 */
	private double principalPointsPeriod2;
	
	/**
	 * (x, y) offset of the centre of the clear-aperture array of LA 1 from the centre of the component
	 */
	private Vector2D xyOffsetClearApertureArray1;

	/**
	 * (x, y) offset of the centre of the clear-aperture array of LA 2 from the centre of the component
	 */
	private Vector2D xyOffsetClearApertureArray2;

	/**
	 * (x, y) offset of the centre of the principal-point array of LA 1 from the centre of the component
	 */
	private Vector2D xyOffsetPrincipalPointArray1;

	/**
	 * (x, y) offset of the centre of the principal-poin array of LA 2 from the centre of the component
	 */
	private Vector2D xyOffsetPrincipalPointArray2;

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
	
	/**
	 * if true, diffractive blur will be simulated
	 */
	private boolean simulateDiffractiveBlur;
	
	/**
	 * wavelength for which diffractive blur is simulated, in nm
	 */
	private double lambdaNM;

	//
	// the rest of the scene
	//
	
	/**
	 * show a small white sphere at the position the camera position is imaged to
	 */
	private boolean showSphereAtImageOfCamera;
	
	/**
	 * Determines how to initialise the backdrop
	 */
	private StudioInitialisationType studioInitialisation;
	
	/**
	 * aperture centre of the camera
	 */
	private Vector3D cameraPosition;
	
	
	
	/**
	 * Constructor.
	 * Sets all parameters.
	 */
	public GaborSupererLensExplorer()
	{
		super();
		
		// set to true for interactive version
		nonInteractiveTIMAction = NonInteractiveTIMActionEnum.INTERACTIVE;
		
		// set all parameters
		
		renderQuality = RenderQualityEnum.DRAFT;
		
		// lenslet-array parameters
		f1 = 0.003;
		f2 = f1;
		minimumZSeparation = 0.00001;
		aperturesPeriod1 = 0.001;
		aperturesPeriod2 = 0.000997;
		principalPointsPeriod1 = 0.001;
		principalPointsPeriod2 = 0.000997;
		xyOffsetPrincipalPointArray1 = new Vector2D(0, 0);
		xyOffsetPrincipalPointArray2 = new Vector2D(0, 0);
		xyOffsetClearApertureArray1 = xyOffsetPrincipalPointArray1;
		xyOffsetClearApertureArray2 = xyOffsetPrincipalPointArray2;
		lensType = LensType.IDEAL_THIN_LENS;
		showLensletArray1 = true;
		showLensletArray2 = true;
		simulateDiffractiveBlur = true;
		lambdaNM = 550;
		
		showSphereAtImageOfCamera = false;
		studioInitialisation = StudioInitialisationType.LATTICE;	// the backdrop

		// camera
		cameraViewCentre = new Vector3D(0, 0, 0);
		cameraPosition = new Vector3D(0, 0, -8);
		cameraHorizontalFOVDeg = 10;
		cameraApertureSize = ApertureSizeType.PINHOLE;
		cameraFocussingDistance = 10;
		
		if(nonInteractiveTIMAction == NonInteractiveTIMActionEnum.INTERACTIVE)
		{
			windowTitle = "Dr TIM's Gabor-super-er-lens explorer";
			windowWidth = 1500;
			windowHeight = 850;
		}
	}
	

	@Override
	public String getFirstPartOfFilename()
	{
		return "GaborSupererLensExplorer"	// the name
				;
	}
	
	@Override
	public void writeParameters(PrintStream printStream)
	{
		// write any parameters not defined in NonInteractiveTIMEngine
		
		printStream.println("f1="+f1);
		printStream.println("f2="+f2);
		printStream.println("aperturesPeriod1="+aperturesPeriod1);
		printStream.println("aperturesPeriod2="+aperturesPeriod2);
		printStream.println("principalPointsPeriod1="+principalPointsPeriod1);
		printStream.println("principalPointsPeriod2="+principalPointsPeriod2);
		printStream.println("xyOffsetClearApertureArray1="+xyOffsetClearApertureArray1);
		printStream.println("xyOffsetClearApertureArray2="+xyOffsetClearApertureArray2);
		printStream.println("xyOffsetPrincipalPointArray1="+xyOffsetPrincipalPointArray1);
		printStream.println("xyOffsetPrincipalPointArray2="+xyOffsetPrincipalPointArray2);
		printStream.println("dz="+minimumZSeparation);
		printStream.println("showLensletArray1="+showLensletArray1);
		printStream.println("showLensletArray2="+showLensletArray2);
		printStream.println("lensType="+lensType);
		printStream.println("simulateDiffractiveBlur="+simulateDiffractiveBlur);
		printStream.println("lambdaNM="+lambdaNM);
		printStream.println("showSphereAtImageOfCamera="+showSphereAtImageOfCamera);
		printStream.println("studioInitialisation="+studioInitialisation);
		printStream.println("cameraPosition="+cameraPosition);

		printStream.println();

		// write all parameters defined in NonInteractiveTIMEngine
		super.writeParameters(printStream);		
	}
	
	
	// private variables; use calculateAllParameters() to calculate
	
	private double zSeparation;
	private Vector3D
		normal,	// normal common to the plane of lenslet arrays 1 and 2 and the common focal plane, F
		centreOfRectangularOutlineOfLA1,	// centre of the rectangular outline of lenslet array 1...
		centreOfRectangularOutlineOfLA2,	// ... and 2
		centreOfClearApertureOfLenslet00OfLA1,	// centre of the array of clear apertures (i.e. coordinate of the centre of the clear aperture of lenslet (0,0)) of lenslet array 1...
		centreOfClearApertureOfLenslet00OfLA2,	// ... and 2
		principalPointOfLenslet00OfLA1,	// centre of the array of principal points (i.e. coordinate of the principal point of lenslet (0,0)) of lenslet array 1...
		principalPointOfLenslet00OfLA2,	// ... and 2
		principalPointOfLenslet10OfLA1,	// coordinate of the principal point of lenslet (1,0) of lenslet array 1...
		principalPointOfLenslet10OfLA2,	// ... and 2
		pointOnF;	// a point on the common focal plane, F
	
	private void calculateAllParameters()
	{
		// the z separation is ideally f1 + f2...
		zSeparation = f1+f2;
		
		// ... but when it gets too small, then Dr TIM doesn't recognise that there are two separate lenslet arrays
		if(Math.abs(zSeparation) < minimumZSeparation)
		{
			// in that case, set the z separation to some number (with the same sign as f1+f2) that is large enough
			// so that Dr TIM can tell the two lenslet arrays apart but small enough that the telescope array formed
			// by the two lenslet arrays still works well
			zSeparation = minimumZSeparation*MyMath.signumNever0(f1+f2);
		}
		if(zSeparation < 0)
		{
			// if the two lenslet arrays switch order, the imaging will change; put a warning in the console
			System.err.println("The separation between the two lenslet arrays is negative ("+zSeparation+"), which means they switch order");			
		}

		// normalised normal common to the plane of lenslet arrays 1 and 2 and the common focal plane, F;
		// must point from lenslet array 1 to lenslet array 2!
		normal = Vector3D.Z;

		// centre of the rectangular outlines of lenslet array 1 and 2
		centreOfRectangularOutlineOfLA1 = new Vector3D(0, 0, -0.5*zSeparation);
		centreOfRectangularOutlineOfLA2 = new Vector3D(0, 0, +0.5*zSeparation);
		
		// centre of the array of clear apertures (i.e. coordinate of the centre of the clear aperture of lenslet (0,0)) of lenslet arrays 1 and 2
		centreOfClearApertureOfLenslet00OfLA1 = Vector3D.sum(centreOfRectangularOutlineOfLA1, new Vector3D(xyOffsetClearApertureArray1.x, xyOffsetClearApertureArray1.y, 0));
		centreOfClearApertureOfLenslet00OfLA2 = Vector3D.sum(centreOfRectangularOutlineOfLA2, new Vector3D(xyOffsetClearApertureArray2.x, xyOffsetClearApertureArray2.y, 0));

		// centre of the array of principal points (i.e. coordinate of the principal point of lenslet (0,0)) of lenslet arrays 1 and 2
		principalPointOfLenslet00OfLA1 = Vector3D.sum(centreOfRectangularOutlineOfLA1, new Vector3D(xyOffsetPrincipalPointArray1.x, xyOffsetPrincipalPointArray1.y, 0));
		principalPointOfLenslet00OfLA2 = Vector3D.sum(centreOfRectangularOutlineOfLA2, new Vector3D(xyOffsetPrincipalPointArray2.x, xyOffsetPrincipalPointArray2.y, 0));

		// coordinate of the principal point of lenslet (1,0) of lenslet arrays 1 and 2
		principalPointOfLenslet10OfLA1 = Vector3D.sum(centreOfRectangularOutlineOfLA1, new Vector3D(xyOffsetPrincipalPointArray1.x + principalPointsPeriod1, xyOffsetPrincipalPointArray1.y, 0));
		principalPointOfLenslet10OfLA2 = Vector3D.sum(centreOfRectangularOutlineOfLA2, new Vector3D(xyOffsetPrincipalPointArray2.x + principalPointsPeriod2, xyOffsetPrincipalPointArray2.y, 0));

		// a point on the common focal plane, F
		pointOnF = Vector3D.sum(centreOfRectangularOutlineOfLA1, normal.getProductWith(f1));
	}
	
	/**
	 * @param object
	 * @return	the image position that corresponds to the given object position
	 */
	public Vector3D calculateImage(Vector3D object)
	{
		calculateAllParameters();
		
		try {
			// the points I00 and I10 are given by the intersection with the plane F of the line through <object> and P00 and P10, respectively
			Vector3D i00 = Geometry.linePlaneIntersection(
					object,	// pointOnLine,
					Vector3D.difference(principalPointOfLenslet00OfLA1, object),	// directionOfLine,
					pointOnF,	// pointOnPlane,
					normal	// normalToPlane
					);
			Vector3D i10 = Geometry.linePlaneIntersection(
					object,	// pointOnLine,
					Vector3D.difference(principalPointOfLenslet10OfLA1, object),	// directionOfLine,
					pointOnF,	// pointOnPlane,
					normal	// normalToPlane
					);
			
			// the image lies on the intersection between the lines through i00 and P00 of LA2 and through i10 and P10 of LA2

			try {
				// or use pointClosestToBothLines?
				return Geometry.lineLineIntersection(
						i00,	// pointOnLine1,
						Vector3D.difference(principalPointOfLenslet00OfLA2, i00),	// directionOfLine1,
						i10,	// pointOnLine2,
						Vector3D.difference(principalPointOfLenslet10OfLA2, i10)	// directionOfLine2
						);
			} catch (RayTraceException e) {
				// no intersection, which must be because the image is at infinity
				return new Vector3D(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
			}
		} catch (MathException e) {
			e.printStackTrace();
			return Vector3D.NaV;
		}
	}
	
	public void makeSuperlens()
	{
		// simply align the clear-aperture arrays with the principal-point arrays
		
		// make the offset of the clear-aperture arrays the same as those of the principal-point arrays...
		xyOffsetClearApertureArray1 = xyOffsetPrincipalPointArray1;
		xyOffsetClearApertureArray1Panel.setVector2D(xyOffsetClearApertureArray1);

		xyOffsetClearApertureArray2 = xyOffsetPrincipalPointArray2;
		xyOffsetClearApertureArray2Panel.setVector2D(xyOffsetClearApertureArray2);

		// ... and make the periods of the clear-aperture arrays the same as those of the principal-point arrays
		aperturesPeriod1 = principalPointsPeriod1;
		aperturesPeriod1Panel.setNumber(aperturesPeriod1);

		aperturesPeriod2 = principalPointsPeriod2;
		aperturesPeriod2Panel.setNumber(aperturesPeriod2);
	}

	public void makeSupererLensOptimisedForCameraPosition()
	{
		calculateAllParameters();
		
		try {
			// first construct I00, the intersection of the line through the camera position and P00 with F
			Vector3D i00 = Geometry.linePlaneIntersection(
					cameraPosition,	// pointOnLine,
					Vector3D.difference(principalPointOfLenslet00OfLA1, cameraPosition),	// directionOfLine,
					pointOnF,	// pointOnPlane,
					normal	// normalToPlane
					);
			
			// next, construct A00 of LA1 and LA2, which is the intersection between the plane of LA1 or LA2
			// and the line through I00 with
			// EITHER the direction from the camera position to its image
			// Vector3D cameraImage = calculateImage(cameraPosition);
			// Vector3D cameraPosition2Image = Vector3D.difference(cameraImage, cameraPosition).getNormalised();
			// Vector3D directionThroughA00I00 = Vector3D.difference(cameraImage, cameraPosition).getNormalised();
			// OR (better, I think) the same direction as the normal to the lenslet arrays
			Vector3D directionThroughA00I00 = normal;
			
			centreOfClearApertureOfLenslet00OfLA1 = Geometry.linePlaneIntersection(
					i00,	// pointOnLine,
					directionThroughA00I00,	// directionOfLine,
					centreOfRectangularOutlineOfLA1,	// pointOnPlane,
					normal	// normalToPlane
					);
			centreOfClearApertureOfLenslet00OfLA2 = Geometry.linePlaneIntersection(
					i00,	// pointOnLine,
					directionThroughA00I00,	// directionOfLine,
					centreOfRectangularOutlineOfLA2,	// pointOnPlane,
					normal	// normalToPlane
					);
			
			xyOffsetClearApertureArray1 = new Vector2D(centreOfClearApertureOfLenslet00OfLA1.x, centreOfClearApertureOfLenslet00OfLA1.y);
			xyOffsetClearApertureArray1Panel.setVector2D(xyOffsetClearApertureArray1);

			xyOffsetClearApertureArray2 = new Vector2D(centreOfClearApertureOfLenslet00OfLA2.x, centreOfClearApertureOfLenslet00OfLA2.y);
			xyOffsetClearApertureArray2Panel.setVector2D(xyOffsetClearApertureArray2);

			// calculate the object distance
			double o = Vector3D.scalarProduct(Vector3D.difference(centreOfRectangularOutlineOfLA1, cameraPosition), normal);
			
			// and the image distance
			// double i = Vector3D.scalarProduct(Vector3D.difference(cameraImage, centreOfRectangularOutlineOfLA2), normal);

			aperturesPeriod1 = principalPointsPeriod1*(o+f1)/o;
			aperturesPeriod2 = aperturesPeriod1;
			aperturesPeriod1Panel.setNumber(aperturesPeriod1);
			aperturesPeriod2Panel.setNumber(aperturesPeriod2);			
		} catch (MathException e) {
			e.printStackTrace();
		}		
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
		
		// initialise the scene and lights
		StudioInitialisationType.initialiseSceneAndLights(
				studioInitialisation,
				scene,
				studio
			);
		
		calculateAllParameters();
		
		scene.addSceneObject(
				new EditableLensletArrayForGaborSupererLens(
						"LA1",	// description,
						centreOfRectangularOutlineOfLA1,	// centreRectangle
						Vector3D.X,	// uSpanVector,
						Vector3D.Y,	// vSpanVector, 
						f1,	// focalLength,
						new Vector3D(aperturesPeriod1, 0, 0),	// uPeriodApertures,
						new Vector3D(0, aperturesPeriod1, 0),	// vPeriodApertures,
						new Vector3D(principalPointsPeriod1, 0, 0),	// uPeriodPrincipalPoints,
						new Vector3D(0, principalPointsPeriod1, 0),	// vPeriodPrincipalPoints,
						centreOfClearApertureOfLenslet00OfLA1, // centreClearApertureArray
						principalPointOfLenslet00OfLA1, // centrePrincipalPointArray
						lensType,
						simulateDiffractiveBlur,
						lambdaNM*1e-9,	// lambda,
						0.96,	// throughputCoefficient,
						true,	// shadowThrowing,
						scene,	// parent,
						studio
						), 
				showLensletArray1
				);

		scene.addSceneObject(
				new EditableLensletArrayForGaborSupererLens(
						"LA2",	// description,
						centreOfRectangularOutlineOfLA2,	// centreRectangle
						Vector3D.X,	// uSpanVector,
						Vector3D.Y,	// vSpanVector, 
						f2,	// focalLength,
						new Vector3D(aperturesPeriod2, 0, 0),	// uPeriodApertures,
						new Vector3D(0, aperturesPeriod2, 0),	// vPeriodApertures,
						new Vector3D(principalPointsPeriod2, 0, 0),	// uPeriodPrincipalPoints,
						new Vector3D(0, principalPointsPeriod2, 0),	// vPeriodPrincipalPoints,
						centreOfClearApertureOfLenslet00OfLA2, // centreClearApertureArray
						principalPointOfLenslet00OfLA2, // centrePrincipalPointArray
						lensType,
						simulateDiffractiveBlur,
						lambdaNM*1e-9,	// lambda,
						0.96,	// throughputCoefficient,
						true,	// shadowThrowing,
						scene,	// parent,
						studio
						),
				showLensletArray2
				);
		
		scene.addSceneObject(
				new EditableScaledParametrisedSphere(
						"Sphere at image of camera position",	// description,
						calculateImage(cameraPosition),	// centre,
						0.01,	// radius,
						SurfaceColour.WHITE_SHINY,	// surfaceProperty,
						scene, studio),
				showSphereAtImageOfCamera
				);

		// the camera
		
		cameraViewDirection = Vector3D.difference(cameraViewCentre, cameraPosition);
		cameraDistance = cameraViewDirection.getLength();

		studio.setCamera(getStandardCamera());
	}

	
	
	//
	// for interactive version
	//
	
	public enum LensletArraysInitialisationType
	{
		INIT("Initialise lenslet arrays to..."),
		GABOR_SUPER_LENS("Gabor superlens"),
		GABOR_SUPERER_LENS("Gabor super-er lens");
		
		private String description;
		private LensletArraysInitialisationType(String description) {this.description = description;}	
		@Override
		public String toString() {return description;}
	}

	
	private LabelledDoublePanel f1Panel, f2Panel, minimumZSeprarationPanel, aperturesPeriod1Panel, aperturesPeriod2Panel, principalPointsPeriod1Panel, principalPointsPeriod2Panel;
	private LabelledVector2DPanel xyOffsetClearApertureArray1Panel, xyOffsetClearApertureArray2Panel, xyOffsetPrincipalPointArray1Panel, xyOffsetPrincipalPointArray2Panel;
	private DoublePanel lambdaNMPanel;
	private JCheckBox showLensletArray1CheckBox, showLensletArray2CheckBox, simulateDiffractiveBlurCheckBox, showSphereAtImageOfCameraCheckBox;
	private JComboBox<LensletArraysInitialisationType> lensletArraysInitialisationComboBox;
	private JComboBox<LensType> lensTypeComboBox;
	private JComboBox<StudioInitialisationType> studioInitialisationComboBox;
	private JTextArea infoTextArea;
	private JButton updateInfoButton, makeSupererLensButton, makeSuperlensButton;
	// private JButton lensletArraysInitialisationButton;	// gaborInitialisationButton, moireInitialisationButton, clasInitialisationButton;

	// camera stuff
	// private LabelledVector3DPanel cameraViewDirectionPanel;
	private LabelledVector3DPanel cameraPositionPanel;
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

		JPanel lensletArraysPanel = new JPanel();
		lensletArraysPanel.setLayout(new MigLayout("insets 0"));
		sceneCameraTabbedPane.addTab("Lenslet arrays", lensletArraysPanel);

		//
		// the Lenslet-arrays-initialisation panel
		//

		// scenePanel.add(new JLabel("Lenslet-array initialisation"));
		
//		lensletArraysInitialisationComboBox = new JComboBox<LensletArraysInitialisationType>(LensletArraysInitialisationType.values());
//		lensletArraysInitialisationComboBox.setSelectedItem(LensletArraysInitialisationType.INIT);
//		lensletArraysInitialisationComboBox.addActionListener(this);
//		lensletArraysPanel.add(
//				lensletArraysInitialisationComboBox
//			, "span");


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
		la1Panel.setBorder(GUIBitsAndBobs.getTitledBorder("Lenslet array 1 (closer to camera)"));
		la1Panel.setLayout(new MigLayout("insets 0"));
		lensletArraysPanel.add(la1Panel, "span");

		f1Panel = new LabelledDoublePanel("f");
		f1Panel.setNumber(f1);
		f1Panel.setToolTipText("Focal length of the lenslets");
		// la1Panel.add(f1Panel, "span");
		
		showLensletArray1CheckBox = new JCheckBox("Show");
		showLensletArray1CheckBox.setSelected(showLensletArray1);
		// la1Panel.add(showLensletArray1CheckBox, "span");
		la1Panel.add(GUIBitsAndBobs.makeRow(f1Panel, showLensletArray1CheckBox), "span");
		
		aperturesPeriod1Panel = new LabelledDoublePanel("Period of clear-aperture array");
		aperturesPeriod1Panel.setNumber(aperturesPeriod1);
		aperturesPeriod1Panel.setToolTipText("Period, i.e. distance between the clear apertures of neighbouring lenslets");
		la1Panel.add(aperturesPeriod1Panel, "span");

		principalPointsPeriod1Panel = new LabelledDoublePanel("Period of principal-point array");
		principalPointsPeriod1Panel.setNumber(principalPointsPeriod1);
		principalPointsPeriod1Panel.setToolTipText("Period of array of principal points");
		la1Panel.add(principalPointsPeriod1Panel, "span");
		
		xyOffsetClearApertureArray1Panel = new LabelledVector2DPanel("(x,y) of centre of clear aperture of lenslet (0, 0)");
		xyOffsetClearApertureArray1Panel.setVector2D(xyOffsetClearApertureArray1);
		xyOffsetClearApertureArray1Panel.setToolTipText("x and y coordinates of the centre of the clear aperture of the lenslet with indices (0,0)");
		la1Panel.add(xyOffsetClearApertureArray1Panel, "span");

		xyOffsetPrincipalPointArray1Panel = new LabelledVector2DPanel("(x,y) of principal point of lenslet (0, 0)");
		xyOffsetPrincipalPointArray1Panel.setVector2D(xyOffsetPrincipalPointArray1);
		xyOffsetPrincipalPointArray1Panel.setToolTipText("x and y coordinates of the centre of the clear aperture of the lenslet with indices (0,0)");
		la1Panel.add(xyOffsetPrincipalPointArray1Panel, "span");

		//
		// the LA2 panel
		//
		
		JPanel la2Panel = new JPanel();
		la2Panel.setBorder(GUIBitsAndBobs.getTitledBorder("Lenslet array 2 (farther from camera)"));
		la2Panel.setLayout(new MigLayout("insets 0"));
		lensletArraysPanel.add(la2Panel, "span");
		
		f2Panel = new LabelledDoublePanel("f");
		f2Panel.setNumber(f2);
		f2Panel.setToolTipText("Focal length of the lenslets");
		// la2Panel.add(f2Panel, "span");

		showLensletArray2CheckBox = new JCheckBox("Show");
		showLensletArray2CheckBox.setSelected(showLensletArray2);
		// la2Panel.add(showLensletArray2CheckBox, "span");
		la2Panel.add(GUIBitsAndBobs.makeRow(f2Panel, showLensletArray2CheckBox), "span");

		aperturesPeriod2Panel = new LabelledDoublePanel("Period of clear-aperture array");
		aperturesPeriod2Panel.setNumber(aperturesPeriod2);
		aperturesPeriod2Panel.setToolTipText("Period, i.e. distance between the clear apertures of neighbouring lenslets");
		la2Panel.add(aperturesPeriod2Panel, "span");

		principalPointsPeriod2Panel = new LabelledDoublePanel("Period of principal-point array");
		principalPointsPeriod2Panel.setNumber(principalPointsPeriod2);
		principalPointsPeriod2Panel.setToolTipText("Period of array of principal points");
		la2Panel.add(principalPointsPeriod2Panel, "span");

		xyOffsetClearApertureArray2Panel = new LabelledVector2DPanel("(x,y) of centre of clear aperture of lenslet (0, 0)");
		xyOffsetClearApertureArray2Panel.setVector2D(xyOffsetClearApertureArray2);
		xyOffsetClearApertureArray2Panel.setToolTipText("x and y coordinates of the centre of the clear aperture of the lenslet with indices (0,0)");
		la2Panel.add(xyOffsetClearApertureArray2Panel, "span");

		xyOffsetPrincipalPointArray2Panel = new LabelledVector2DPanel("(x,y) of principal point of lenslet (0, 0)");
		xyOffsetPrincipalPointArray2Panel.setVector2D(xyOffsetPrincipalPointArray2);
		xyOffsetPrincipalPointArray2Panel.setToolTipText("x and y coordinates of the centre of the clear aperture of the lenslet with indices (0,0)");
		la2Panel.add(xyOffsetPrincipalPointArray2Panel, "span");


		// other, common, LA parameters
		
		JPanel commonLAPanel = new JPanel();
		commonLAPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Other, common, lenslet-array parameters"));
		commonLAPanel.setLayout(new MigLayout("insets 0"));
		lensletArraysPanel.add(commonLAPanel, "span");

		lensTypeComboBox = new JComboBox<LensType>(LensType.values());
		lensTypeComboBox.setSelectedItem(lensType);
		lensTypeComboBox.setEnabled(true);
		commonLAPanel.add(GUIBitsAndBobs.makeRow("Lenslet type", lensTypeComboBox), "span");

		simulateDiffractiveBlurCheckBox = new JCheckBox("");
		simulateDiffractiveBlurCheckBox.setSelected(simulateDiffractiveBlur);
		lambdaNMPanel = new DoublePanel();
		lambdaNMPanel.setNumber(lambdaNM);
		commonLAPanel.add(GUIBitsAndBobs.makeRow(simulateDiffractiveBlurCheckBox, "Simulate diffractive blur for wavelength", lambdaNMPanel, "nm"), "span");

		minimumZSeprarationPanel = new LabelledDoublePanel("Minimum z separation between array centres");
		minimumZSeprarationPanel.setNumber(minimumZSeparation);
		minimumZSeprarationPanel.setToolTipText("The centres of the two lenslet arrays are separated in the z direction by a distance f1 + f2, unless this is less than the minimum z separation");
		commonLAPanel.add(minimumZSeprarationPanel, "span");
		
		
		makeSuperlensButton = new JButton("Make superlens");
		makeSuperlensButton.setToolTipText("Set (x,y) of lensets (0, 0) of bboth arrays to (0, 0); set periods of clear-aperture arrays to those of principal-point arrays");
		makeSuperlensButton.addActionListener(this);
		lensletArraysPanel.add(makeSuperlensButton, "span");
		
		makeSupererLensButton = new JButton("Make super-er lens optimised for camera position");
		makeSupererLensButton.setToolTipText("Set (x,y) of lenslets (0, 0) of both arrays and periods of principal-point arrays and of clear-aperture array 2 from period of clear-aperture array of LA1 and camera position and its image");
		makeSupererLensButton.addActionListener(this);
		lensletArraysPanel.add(makeSupererLensButton, "span");
		
		//
		// the Stuff panel
		//

		JPanel stuffPanel = new JPanel();
		stuffPanel.setLayout(new MigLayout("insets 0"));
		sceneCameraTabbedPane.addTab("Other stuff", stuffPanel);
		
		showSphereAtImageOfCameraCheckBox = new JCheckBox("Show white sphere at image of camera position");
		showSphereAtImageOfCameraCheckBox.setSelected(showSphereAtImageOfCamera);
		stuffPanel.add(showSphereAtImageOfCameraCheckBox, "wrap");

		studioInitialisationComboBox = new JComboBox<StudioInitialisationType>(StudioInitialisationType.limitedValuesForBackgrounds);
		studioInitialisationComboBox.setSelectedItem(studioInitialisation);
		stuffPanel.add(GUIBitsAndBobs.makeRow("Background", studioInitialisationComboBox), "span");

		
		infoTextArea = new JTextArea(5, 30);
		JScrollPane scrollPane = new JScrollPane(infoTextArea); 
		infoTextArea.setEditable(false);
		infoTextArea.setText("Click on Update button to show info");
		updateInfoButton = new JButton("Update");
		updateInfoButton.addActionListener(this);
		stuffPanel.add(GUIBitsAndBobs.makeRow(scrollPane, updateInfoButton), "span");

		lensletArraysPanel.add(new JLabel("TODO"), "wrap");		
		lensletArraysPanel.add(new JLabel("* Add option to show corresponding glens"), "wrap");
		lensletArraysPanel.add(new JLabel("* Show parameters of corresponding glens; draw optical axis and focal points"), "wrap");

		
		//
		// the Camera panel
		//
		
		JPanel cameraPanel = new JPanel();
		cameraPanel.setLayout(new MigLayout("insets 0"));
		sceneCameraTabbedPane.addTab("Camera", cameraPanel);

		// camera stuff
		
		cameraPositionPanel = new LabelledVector3DPanel("Position");
		cameraPositionPanel.setVector3D(cameraPosition);
		cameraPanel.add(cameraPositionPanel, "span");
		
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
	
	/**
	 * called before rendering;
	 * override when adding fields
	 */
	@Override
	protected void acceptValuesInInteractiveControlPanel()
	{
		super.acceptValuesInInteractiveControlPanel();
		
		f1 = f1Panel.getNumber();
		f2 = f2Panel.getNumber();
		minimumZSeparation = minimumZSeprarationPanel.getNumber();
		aperturesPeriod1 = aperturesPeriod1Panel.getNumber();
		aperturesPeriod2 = aperturesPeriod2Panel.getNumber();
		principalPointsPeriod1 = principalPointsPeriod1Panel.getNumber();
		principalPointsPeriod2 = principalPointsPeriod2Panel.getNumber();
		xyOffsetClearApertureArray1 = xyOffsetClearApertureArray1Panel.getVector2D();
		xyOffsetClearApertureArray2 = xyOffsetClearApertureArray2Panel.getVector2D();
		xyOffsetPrincipalPointArray1 = xyOffsetPrincipalPointArray1Panel.getVector2D();
		xyOffsetPrincipalPointArray2 = xyOffsetPrincipalPointArray2Panel.getVector2D();

		
		showLensletArray1 = showLensletArray1CheckBox.isSelected();
		showLensletArray2 = showLensletArray2CheckBox.isSelected();
		lensType = (LensType)(lensTypeComboBox.getSelectedItem());
		simulateDiffractiveBlur = simulateDiffractiveBlurCheckBox.isSelected();
		lambdaNM = lambdaNMPanel.getNumber();
		showSphereAtImageOfCamera = showSphereAtImageOfCameraCheckBox.isSelected();
		studioInitialisation = (StudioInitialisationType)(studioInitialisationComboBox.getSelectedItem());
		
		// cameraViewDirection = cameraViewDirectionPanel.getVector3D();
		cameraPosition = cameraPositionPanel.getVector3D();
		cameraHorizontalFOVDeg = cameraHorizontalFOVDegPanel.getNumber();
		cameraApertureSize = (ApertureSizeType)(cameraApertureSizeComboBox.getSelectedItem());
		cameraFocussingDistance = cameraFocussingDistancePanel.getNumber();
		
		updateInfo();
	}
	
	/**
	 * initialise the lenslet-array parameters to a Gabor superlens
	 */
	private void gaborInitialisation()
	{
		// lenslet-array parameters
		f1Panel.setNumber(0.1);
		f2Panel.setNumber(-0.1);
		minimumZSeprarationPanel.setNumber(0.00001);
		aperturesPeriod1Panel.setNumber(0.0099);
		aperturesPeriod2Panel.setNumber(0.01);
		principalPointsPeriod1Panel.setNumber(0.0099);
		principalPointsPeriod2Panel.setNumber(0.01);
		xyOffsetClearApertureArray1Panel.setVector2D(0, 0);
		xyOffsetClearApertureArray2Panel.setVector2D(0, 0);
		xyOffsetPrincipalPointArray1Panel.setVector2D(0, 0);
		xyOffsetPrincipalPointArray2Panel.setVector2D(0, 0);
		showLensletArray1CheckBox.setSelected(true);
		showLensletArray2CheckBox.setSelected(true);
	}

	public void updateInfo()
	{
		NumberFormat nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(8);
		
		infoTextArea.setText(
				"image of camera position="+calculateImage(cameraPosition)
				// "f1="+nf.format(f1)+"\n"+
			);
	}


	@Override
	public void actionPerformed(ActionEvent e)
	{
		super.actionPerformed(e);
		
		if(e.getSource().equals(lensletArraysInitialisationComboBox))
		{
			switch((LensletArraysInitialisationType)(lensletArraysInitialisationComboBox.getSelectedItem()))
			{
			case GABOR_SUPER_LENS:
				gaborInitialisation();
				break;
			case INIT:
			default:
				// do nothing
			}
			lensletArraysInitialisationComboBox.setSelectedItem(LensletArraysInitialisationType.INIT);
		}
		else if(e.getSource().equals(makeSupererLensButton))
		{
			acceptValuesInInteractiveControlPanel();
			makeSupererLensOptimisedForCameraPosition();			
		}
		else if(e.getSource().equals(makeSuperlensButton))
		{
			acceptValuesInInteractiveControlPanel();
			makeSuperlens();						
		}
		else if(e.getSource().equals(updateInfoButton))
		{
			acceptValuesInInteractiveControlPanel();
		}
		
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
		(new GaborSupererLensExplorer()).run();
	}
}
