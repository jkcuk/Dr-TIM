package optics.raytrace.research.refractiveTaylorSeries;

import java.awt.event.ActionEvent;
import java.io.PrintStream;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import math.MyMath;
import math.Vector3D;
import net.miginfocom.swing.MigLayout;
import optics.raytrace.NonInteractiveTIMEngine;
import optics.raytrace.GUI.lowLevel.ApertureSizeType;
import optics.raytrace.GUI.lowLevel.DoublePanel;
import optics.raytrace.GUI.lowLevel.GUIBitsAndBobs;
import optics.raytrace.GUI.lowLevel.IntPanel;
import optics.raytrace.GUI.lowLevel.LabelledDoublePanel;
import optics.raytrace.GUI.lowLevel.LabelledIntPanel;
import optics.raytrace.GUI.lowLevel.LabelledVector3DPanel;
import optics.raytrace.GUI.sceneObjects.EditableCylinderLattice;
import optics.raytrace.core.Studio;
import optics.raytrace.core.StudioInitialisationType;
import optics.raytrace.exceptions.SceneException;
import optics.raytrace.sceneObjects.Plane;
import optics.raytrace.sceneObjects.SnellenChart;
import optics.raytrace.sceneObjects.SnellenChart.ChartType;
import optics.raytrace.sceneObjects.TimHead;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;


/**
 * A view rotator consisting of a voxelated array, where each voxel contains a series of holograms to control the derivative, followed by a hologram mimicking the 
 * behaviour of a simple wedge. This relates it closely to the refractiveViewRotator which utilises a glass wedge instead of a hologram. 
 */
public class DerivativeControlledViewRotationExplorer extends NonInteractiveTIMEngine
{
	
	private static final long serialVersionUID = 3405473222678471457L;
	//
	//Pixel construction
	//
	//pixel height and width
	private double pixelPeriodX, pixelPeriodY;
	//The pixel magnification factor
	private double magnificationFactor;
	//should the apparent rotation wedge hologram be added?
	private boolean addWedgeHologram;
	// DirectionChangingSurfaceSequence dcss, we set these here in order to implement them later on...
	SurfaceParameters surfaceParametersTesting;
	//If the surfaces are set up to be a local rotator using lenticular holograms, set the initial ocular surface x coefficient 
	private double x0Coef; 
	//The transmission coefficient of each hologram
	private double transmissionCoefficient;
	//Adding diffraction if required...
	private boolean simulateDiffractionBlur;
	
	//
	//Component construction
	//
	//the width and height of the component
	private double height, width;
	//the normal 
	private Vector3D componentNormal;
	//the eye position
	private Vector3D eyePosition;
	//The center of the central pixel in the ocular surface
	private Vector3D ocularPlaneCentre;
	//The overall apparent rotation amount
	private double rotation;
	//The axis about which the rotation should occure
	private Vector3D rotationAxisDirection;
	//view object distance
	private double viewDistance;
	//the maximum number of steps
	private int maxSteps;
	
	//
	//  background
	//
	private StudioInitialisationType studioInitialisation;
	
	private enum Backdrop 
	{
		NOTHING("Nothing"),
		TIM("Tim"),
		LATTICE("Lattice"),
		SNELLEN_CHART("Snellen chart");

		private String description;
		private Backdrop(String description) {this.description = description;}	
		@Override
		public String toString() {return description;}
	}
	private Backdrop backdrop;
	//Tim params
	private Vector3D timCentre;
	private double timRadius;
	//Lattice params
	private double xMin, xMax, yMin, yMax, zMin, zMax, latticeRadius; 
	private int nX, nY, nZ;
	private	Vector3D latticeCentre;
	//Snellen chart parameters
	private double snellenHeight;
	private Vector3D snellenCentre;
	
	

	/**
	 * Constructor.
	 * Sets all parameters.
	 */
	public DerivativeControlledViewRotationExplorer()
	{
		super();
		//the component
		surfaceParametersTesting = new SurfaceParameters(1, 2);
		transmissionCoefficient = 0.96;
		pixelPeriodX = 0.05;
		pixelPeriodY = 0.05;
		magnificationFactor = 1;
		addWedgeHologram = true;
		transmissionCoefficient = 1;
		simulateDiffractionBlur = false;
		height = 1;
		width = 1;
		componentNormal = Vector3D.Z;
		eyePosition = new Vector3D(0,0,-1);
		ocularPlaneCentre = Vector3D.O;
		rotation =10;
		rotationAxisDirection = Vector3D.Z;
		viewDistance =10;
		maxSteps= 100;
		x0Coef=-1;
		
		// background
		studioInitialisation = StudioInitialisationType.TIM_HEAD;
		backdrop = Backdrop.NOTHING;
		//Tim
		timCentre = new Vector3D(0,0,10);
		timRadius = 1;
		
		//Lattice
		xMin = -2;
		xMax = 2;
		yMin = -1;
		yMax = 2;
		zMin = -2;
		zMax = -8;
		latticeRadius = 0.05; 
		nX = 5;
		nY = 5;
		nZ = 5;
		latticeCentre = new Vector3D(0,0,10);
		
		//Snellen chart
		snellenCentre = new Vector3D(0,0,10);
		snellenHeight = 1;
		
		// camera
		cameraViewCentre = new Vector3D(0, 0, 0);
		cameraDistance = 1;
		cameraViewDirection = new Vector3D(0, 0, 1);
		cameraHorizontalFOVDeg = 40;
		cameraApertureSize = ApertureSizeType.SMALL;
		cameraFocussingDistance = 10;

		windowTitle = "Dr TIM's derivative-controlled view rotation explorer";
		windowWidth = 1400;
		windowHeight = 650;
	}
	

	/* (non-Javadoc)
	 * @see optics.raytrace.NonInteractiveTIMEngine#getFirstPartOfFilename()
	 */
	@Override
	public String getFilename()
	{
		return
				"DerivativeControlledViewRotationExplorer" //TODO why no .bmp
				;
	}

	@Override
	public void writeParameters(PrintStream printStream)
	{
		// write any parameters not defined in NonInteractiveTIMEngine, each parameter is saved like this:
		// printStream.println("parameterName = "+parameterName);

		//The main component i.e the view rotator
		surfaceParametersTesting.writeParameters(printStream);
		printStream.println("addWedgeHologram = "+addWedgeHologram);
		
		if(addWedgeHologram) printStream.println("magnificationFactor = "+magnificationFactor);
		
		printStream.println("transmissionCoefficient="+transmissionCoefficient);
		printStream.println("pixelPeriodx = "+pixelPeriodX);
		printStream.println("pixelPeriody = "+pixelPeriodY);
		printStream.println("simulateDiffractionBlur = "+simulateDiffractionBlur);

		printStream.println("height = "+height);
		printStream.println("width = "+width);
		printStream.println("componentNormal = "+componentNormal);
		printStream.println("eyePosition = "+eyePosition);
		printStream.println("ocularPlaneCentre = "+ocularPlaneCentre);
		printStream.println("rotation = "+rotation);
		printStream.println("rotationAxisDirection = "+rotationAxisDirection);
		printStream.println("viewDistance = "+viewDistance);
		printStream.println("maxSteps = "+maxSteps);

		// background
		printStream.println("studioInitialisation="+studioInitialisation);
		switch(backdrop) {
		case TIM:
			printStream.println("timCentre = "+timCentre);
			printStream.println("timRadius = "+timRadius);
			break;
		case LATTICE:
			printStream.println("latticeCentre = "+latticeCentre);
			printStream.println("xMin = "+xMin);
			printStream.println("xMax = "+xMax);
			printStream.println("yMin = "+yMin);
			printStream.println("yMax = "+yMax);
			printStream.println("zMin = "+zMin);
			printStream.println("zMax = "+zMax);
			printStream.println("latticeRadius = "+latticeRadius);
			printStream.println("nX = "+nX);
			printStream.println("nY = "+nY);
			printStream.println("nZ = "+nZ);	
			break;
		case SNELLEN_CHART:
			printStream.println("snellenCentre = "+snellenCentre);
			printStream.println("snellenHeight = "+snellenHeight);
			break;
		case NOTHING:
		default:
			break;
		}
		
		// camera
		printStream.println("cameraViewCentre="+cameraViewCentre);
		printStream.println("cameraDistance="+cameraDistance);
		printStream.println("cameraViewDirection="+cameraViewDirection);
		printStream.println("cameraHorizontalFOVDeg="+cameraHorizontalFOVDeg);
		printStream.println("cameraApertureSize="+cameraApertureSize);
		printStream.println("cameraFocussingDistance="+cameraFocussingDistance);
		

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
		
		//setting the backdrop
		switch(backdrop) {
		case TIM:
			scene.addSceneObject(new TimHead("Tim head", 
					timCentre, 
					timRadius, 
					Vector3D.Z.getProductWith(-1), //front direction
					Vector3D.Y, //top direction
					Vector3D.X, //right direction
					scene, 
					studio));
			break;
		case LATTICE:
			scene.addSceneObject( new EditableCylinderLattice(
					"lattice",// description,
					xMin, xMax, nX, Vector3D.X,
					yMin, yMax, nY, Vector3D.Y,
					zMin, zMax, nZ, Vector3D.Z,
					latticeRadius,
					latticeCentre,
					true,
					scene,	// parent 
					studio));	
			break;
		case SNELLEN_CHART:
			scene.addSceneObject(new SnellenChart(
					"A snellen chart",// description,
					snellenCentre,// centre,
					Vector3D.Y,//new Vector3D(0,1,0),// upDirection,
					Vector3D.X,//new Vector3D(1,0,0),// rightDirection,
					snellenHeight,// height,
					eyePosition,// cameraPosition, TODO this is the eye position, not necessarily equal to the camera position!
					ChartType.SET,
					scene,// parent,
					studio //studio
					));
			break;
		case NOTHING:
		default:
			break;
		}
		
		//Adding the pixel/voxelated view rotator 
		scene.addSceneObject( new DerivativeControlledViewRotator(
				"view rotator",// description,
				Vector3D.Y.getProductWith(height),// height,
				Vector3D.X.getProductWith(width),// width,
				componentNormal,// componentNormal,
				eyePosition,// eyePosition,
				ocularPlaneCentre,// ocularPlaneCentre,
				rotation,// rotationAngle,
				rotationAxisDirection,// rotationAxisDirection,
				magnificationFactor,// magnificationFactor,
				addWedgeHologram,// addWedgeHologram,
				Vector3D.X.getProductWith(pixelPeriodX),// periodVector1,
				Vector3D.Y.getProductWith(pixelPeriodY),// periodVector2,
				new Plane("focus plane", 
						new Vector3D(0,0,viewDistance),
						Vector3D.Z, 
						null,
						null, 
						null
						),// viewObject,
				surfaceParametersTesting.getNoOfSurfaces(),// noOfSurfaces, 
				surfaceParametersTesting.getPolynomialOrder(),// polynomialOrder, 
				surfaceParametersTesting.getDz(),// dz, 
				surfaceParametersTesting.getA(),// a,
				transmissionCoefficient,// surfaceTransmissionCoefficient,
				simulateDiffractionBlur,// simulateDiffractionBlur,
				maxSteps,// maxStepsInArray,
				scene,// scene,
				scene,// parent, 
				studio// studio
			));
		
//		Vector3D periodVector1 = Vector3D.X.getProductWith(pixelPeriodX);// periodVector1,
//		Vector3D periodVector2 = Vector3D.Y.getProductWith(pixelPeriodY);// periodVector2,
//		Vector3D pointOnPlane0 = Vector3D.sum(ocularPlaneCentre, periodVector1.getProductWith(0.5), periodVector1.getProductWith(0.5));
//		Vector3D pointOnPlane1 = Vector3D.sum(pointOnPlane0, periodVector1, periodVector2);
//		//point where all planes meet to give the optimum voxels shape is the eye position
//			
//		scene.addSceneObject(new Plane(
//				"test",
//				eyePosition,// c1, 
//				eyePosition.getSumWith(periodVector2),// c2,
//				pointOnPlane0,// p0, 
//				SurfaceColour.RED_SHINY,// surfaceProperty,
//				scene,// parent,
//				studio// studio
//				));
		
		
		studio.setCamera(getStandardCamera());
		
	}


	

	//
	// for the interactive version
	//
	
	//Componenet & Pixels
	private LabelledVector3DPanel ocularPlaneCentrePanel, eyePositionPanel; 
	private JButton updateSurfaceParameterFieldsButton, initSurfacesToDoubleTelescopeButton;
	private IntPanel polynomialOrderPanel, noOfSurfacesPanel, maxStepsPanel;
	private DoublePanel telescopicInitialisationRotationAngleDegPanel, viewDistancePanel, rotationPanel, magnificationFactorPanel, pixelPeriodXPanel, pixelPeriodYPanel,
	heightPanel, widthPanel;
	private LabelledDoublePanel transmissionCoefficientPanel, x0CoefPanel;
	JTabbedPane surfaceParametersTabbedPane;
	private JPanel surfacesPanel;
	private JCheckBox simulateDiffractionBlurCheckBox, addWedgeHologramCheckBox;
	
	// background
	private JPanel backdropPanel;
	private JComboBox<StudioInitialisationType> studioInitialisationComboBox;
	private JComboBox<Backdrop> backdropComboBox;
	private LabelledDoublePanel xMinPanel, xMaxPanel, yMinPanel, yMaxPanel, zMinPanel, zMaxPanel, latticeRadiusPanel ,timRadiusPanel, snellenHeightPanel;
	private LabelledIntPanel nXPanel, nYPanel, nZPanel;
	private LabelledVector3DPanel latticeCentrePanel, timCentrePanel, snellenCentrePanel;
	
	
	// main camera
	private LabelledDoublePanel cameraDistancePanel;
	private LabelledVector3DPanel cameraViewDirectionPanel, cameraViewCentrePanel;
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
		
		JTabbedPane tabbedPane = new JTabbedPane();
		interactiveControlPanel.add(tabbedPane, "span");

		// the surfaces panel
		JPanel componentPanel = new JPanel();
		componentPanel.setLayout(new MigLayout("insets 0"));
		tabbedPane.addTab("Component", componentPanel);	
		
		addWedgeHologramCheckBox = new JCheckBox("add wedge hologram");
		addWedgeHologramCheckBox.setSelected(addWedgeHologram);
		componentPanel.add(addWedgeHologramCheckBox,"span");
		
		ocularPlaneCentrePanel = new LabelledVector3DPanel("ocular centre");
		ocularPlaneCentrePanel.setVector3D(ocularPlaneCentre);
		componentPanel.add(ocularPlaneCentrePanel, "span");
		
		eyePositionPanel = new LabelledVector3DPanel("eye/camera position");
		eyePositionPanel.setVector3D(eyePosition);
		componentPanel.add(eyePositionPanel, "span");
		
		heightPanel = new DoublePanel("Rotator height");
		heightPanel.setNumber(height);
		
		widthPanel = new DoublePanel("Rotator width");
		widthPanel.setNumber(width);
		
		componentPanel.add(	GUIBitsAndBobs.makeRow("Rotator height ", heightPanel, " and width ", widthPanel),"span");
		
		viewDistancePanel = new DoublePanel();
		viewDistancePanel.setNumber(viewDistance);
		
		rotationPanel = new DoublePanel();
		rotationPanel.setNumber(rotation);

		componentPanel.add(	GUIBitsAndBobs.makeRow("Plane at distance ", viewDistancePanel, " rotated by", rotationPanel),"span");
	
		magnificationFactorPanel = new DoublePanel();
		magnificationFactorPanel.setNumber(magnificationFactor);
		componentPanel.add(	GUIBitsAndBobs.makeRow("wedge magnification factor ", magnificationFactorPanel),"span");
		
		pixelPeriodXPanel = new DoublePanel();
		pixelPeriodXPanel.setNumber(pixelPeriodX);
		
		pixelPeriodYPanel = new DoublePanel();
		pixelPeriodYPanel.setNumber(pixelPeriodY);
		
		componentPanel.add(	GUIBitsAndBobs.makeRow("pixel x-period ", pixelPeriodXPanel," and y-period ", pixelPeriodYPanel),"span");
		
		surfacesPanel = new JPanel();
		surfacesPanel.setLayout(new MigLayout("insets 0"));
		surfacesPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Derivative control surfaces"));	

		noOfSurfacesPanel = new IntPanel();
		noOfSurfacesPanel.setNumber(surfaceParametersTesting.getNoOfSurfaces());
		
		polynomialOrderPanel = new IntPanel();
		polynomialOrderPanel.setNumber(surfaceParametersTesting.getPolynomialOrder());
		
		updateSurfaceParameterFieldsButton = new JButton("Update fields");
		updateSurfaceParameterFieldsButton.setToolTipText("Update fields for parameters describing surfaces");
		updateSurfaceParameterFieldsButton.addActionListener(this);

		// GUIBitsAndBobs.makeRow(noOfSurfacesPanel, changeSurfacesButton)
		surfacesPanel.add(
				GUIBitsAndBobs.makeRow("", noOfSurfacesPanel, "planar polynomial-phase holograms of order", polynomialOrderPanel, "", updateSurfaceParameterFieldsButton),
				"span");
		
		surfaceParametersTabbedPane = new JTabbedPane();
		surfacesPanel.add(surfaceParametersTabbedPane, "span");
		
		surfaceParametersTesting.repopulateSurfaceParametersTabbedPane(surfaceParametersTabbedPane);
		
		
		telescopicInitialisationRotationAngleDegPanel = new DoublePanel();
		telescopicInitialisationRotationAngleDegPanel.setNumber(0);
		
		initSurfacesToDoubleTelescopeButton = new JButton("Initialise");
		initSurfacesToDoubleTelescopeButton.addActionListener(this);
		
		surfacesPanel.add(GUIBitsAndBobs.makeRow(initSurfacesToDoubleTelescopeButton, "to double telescope that rotates by", telescopicInitialisationRotationAngleDegPanel, "°..."), "wrap");
		
		x0CoefPanel = new LabelledDoublePanel("...with ocular x coefficient");
		x0CoefPanel.setNumber(x0Coef);
		surfacesPanel.add(x0CoefPanel, "span");
		
		componentPanel.add(surfacesPanel, "span");
		
		transmissionCoefficientPanel = new LabelledDoublePanel("Transmission coefficient");
		transmissionCoefficientPanel.setNumber(transmissionCoefficient);
		componentPanel.add(transmissionCoefficientPanel, "span");
		
		simulateDiffractionBlurCheckBox = new JCheckBox("simulate diffraction blur");
		simulateDiffractionBlurCheckBox.setSelected(simulateDiffractionBlur);

		maxStepsPanel= new IntPanel();
		maxStepsPanel.setNumber(maxSteps);

		componentPanel.add(GUIBitsAndBobs.makeRow(simulateDiffractionBlurCheckBox, " and max steps", maxStepsPanel), "wrap");
		
		
		// the background panel
		JPanel backgroundPanel = new JPanel();
		backgroundPanel.setLayout(new MigLayout("insets 0"));
		tabbedPane.addTab("Background", backgroundPanel);

		studioInitialisationComboBox = new JComboBox<StudioInitialisationType>(StudioInitialisationType.limitedValuesForBackgrounds);
		studioInitialisationComboBox.setSelectedItem(studioInitialisation);
		backgroundPanel.add(GUIBitsAndBobs.makeRow("Initialise backdrop to", studioInitialisationComboBox), "span");
		
		backdropComboBox = new JComboBox<Backdrop>(Backdrop.values());
		backdropComboBox.setSelectedItem(backdrop);
		backdropComboBox.addActionListener(this);
		backgroundPanel.add(GUIBitsAndBobs.makeRow("Add custom backdrop", backdropComboBox), "span");
		
		backdropPanel = new JPanel();
		backdropPanel.setLayout(new MigLayout("insets 0"));
		backgroundPanel.add(backdropPanel, "span");
		
		
		
		// the camera panel
		
		JPanel cameraPanel = new JPanel();
		cameraPanel.setLayout(new MigLayout("insets 0"));
		tabbedPane.addTab("Camera", cameraPanel);
		
		// camera stuff
		cameraDistancePanel = new LabelledDoublePanel("Camera distance");
		cameraDistancePanel.setNumber(cameraDistance);
		cameraPanel.add(cameraDistancePanel, "span");
		
		cameraViewCentrePanel = new LabelledVector3DPanel("View centre");
		cameraViewCentrePanel.setVector3D(cameraViewCentre);
		cameraPanel.add(cameraViewCentrePanel, "span");
		
		cameraViewDirectionPanel = new LabelledVector3DPanel("View direction");
		cameraViewDirectionPanel.setVector3D(cameraViewDirection);
		cameraPanel.add(cameraViewDirectionPanel, "span");
		
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
		
		// background
		studioInitialisation = (StudioInitialisationType)(studioInitialisationComboBox.getSelectedItem());
		
		switch(backdrop) {
		case TIM: 
			timCentre = timCentrePanel.getVector3D();
			timRadius = timRadiusPanel.getNumber();
			break;
		case LATTICE:
			latticeCentre = latticeCentrePanel.getVector3D();
			nX = nXPanel.getNumber();
			nY = nYPanel.getNumber();
			nZ = nZPanel.getNumber();
			xMin = xMinPanel.getNumber();
			xMax = xMaxPanel.getNumber();
			yMin = yMinPanel.getNumber();
			yMax = yMaxPanel.getNumber();
			zMin = zMinPanel.getNumber();
			zMax = zMaxPanel.getNumber();
			latticeRadius = latticeRadiusPanel.getNumber();	
			break;
		case SNELLEN_CHART:
			snellenCentre = snellenCentrePanel.getVector3D();
			snellenHeight = snellenHeightPanel.getNumber();
		case NOTHING:
		default:
			break;
		}

		//camera
		cameraDistance = cameraDistancePanel.getNumber();
		cameraViewCentre = cameraViewCentrePanel.getVector3D();
		cameraViewDirection = cameraViewDirectionPanel.getVector3D();
		cameraHorizontalFOVDeg = cameraHorizontalFOVDegPanel.getNumber();
		cameraApertureSize = (ApertureSizeType)(cameraApertureSizeComboBox.getSelectedItem());
		cameraFocussingDistance = cameraFocussingDistancePanel.getNumber();

		
		//Component 
		addWedgeHologram = addWedgeHologramCheckBox.isSelected();
		ocularPlaneCentre = ocularPlaneCentrePanel.getVector3D();
		eyePosition = eyePositionPanel.getVector3D();
		height = heightPanel.getNumber();
		width = widthPanel.getNumber();
		pixelPeriodX = pixelPeriodXPanel.getNumber();
		pixelPeriodY = pixelPeriodYPanel.getNumber();
		maxSteps = maxStepsPanel.getNumber();
		viewDistance = viewDistancePanel.getNumber();
		rotation = rotationPanel.getNumber();
		magnificationFactor = magnificationFactorPanel.getNumber();
		surfaceParametersTesting.acceptGUIEntries(noOfSurfacesPanel, polynomialOrderPanel, surfaceParametersTabbedPane);
		transmissionCoefficient = transmissionCoefficientPanel.getNumber();
		simulateDiffractionBlur = simulateDiffractionBlurCheckBox.isSelected();
		
	}
	

	private void updateBackdropPanel()
	{
		backdropPanel.removeAll();
		
		switch(backdrop) {
		case TIM:
			timCentrePanel = new LabelledVector3DPanel("Tim Centre");
			timCentrePanel.setVector3D(timCentre);
			backdropPanel.add(timCentrePanel, "span");
			
			timRadiusPanel = new LabelledDoublePanel("Head radius");
			timRadiusPanel.setNumber(timRadius);
			backdropPanel.add(timRadiusPanel, "span");
			break;
		case LATTICE:
			latticeCentrePanel= new LabelledVector3DPanel("Lattice centred around");
			latticeCentrePanel.setVector3D(latticeCentre);
			backdropPanel.add(latticeCentrePanel, "span");
			
			latticeRadiusPanel = new LabelledDoublePanel("Cylinder radius");
			latticeRadiusPanel.setNumber(latticeRadius);
			backdropPanel.add(latticeRadiusPanel, "wrap");
			
			xMinPanel = new LabelledDoublePanel("x_min");
			xMinPanel.setNumber(xMin);
			backdropPanel.add(xMinPanel, "split 3");
			
			xMaxPanel = new LabelledDoublePanel(", x_max");
			xMaxPanel.setNumber(xMax);
			backdropPanel.add(xMaxPanel);
			
			nXPanel = new LabelledIntPanel(", no of cylinders");
			nXPanel.setNumber(nX);
			backdropPanel.add(nXPanel, "wrap");

			yMinPanel = new LabelledDoublePanel("y_min");
			yMinPanel.setNumber(yMin);
			backdropPanel.add(yMinPanel, "split 3");
			
			yMaxPanel = new LabelledDoublePanel(", y_max");
			yMaxPanel.setNumber(yMax);
			backdropPanel.add(yMaxPanel);
			
			nYPanel = new LabelledIntPanel(", no of cylinders");
			nYPanel.setNumber(nY);
			backdropPanel.add(nYPanel, "wrap");
			
			zMinPanel = new LabelledDoublePanel("z_min");
			zMinPanel.setNumber(zMin);
			backdropPanel.add(zMinPanel, "split 3");
			
			zMaxPanel = new LabelledDoublePanel(", z_max");
			zMaxPanel.setNumber(zMax);
			backdropPanel.add(zMaxPanel);
			
			nZPanel = new LabelledIntPanel(", no of cylinders");
			nZPanel.setNumber(nZ);
			backdropPanel.add(nZPanel, "wrap");		
			break;
		case SNELLEN_CHART:
			
			snellenCentrePanel= new LabelledVector3DPanel("snellen centre");
			snellenCentrePanel.setVector3D(snellenCentre);
			backdropPanel.add(snellenCentrePanel, "span");
			
			snellenHeightPanel = new LabelledDoublePanel("snellen chart height");
			snellenHeightPanel.setNumber(snellenHeight);
			backdropPanel.add(snellenHeightPanel, "span");
			break;
		case NOTHING:
		default:			
			break;
		}
		backdropPanel.revalidate();
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		super.actionPerformed(e);
		
		if(e.getSource().equals(updateSurfaceParameterFieldsButton))
		{
			acceptValuesInInteractiveControlPanel();
		}else if(e.getSource().equals(initSurfacesToDoubleTelescopeButton))
		{
			// initialise the surfaces to 3 2nd-order surfaces
			surfaceParametersTesting.noOfSurfaces = 3;
			noOfSurfacesPanel.setNumber(surfaceParametersTesting.noOfSurfaces);
			surfaceParametersTesting.polynomialOrder = 2;
			polynomialOrderPanel.setNumber(surfaceParametersTesting.polynomialOrder);
			surfaceParametersTesting.initialiseSurfaceParametersArrays();	// initialise all coefficients to zero
			x0Coef = x0CoefPanel.getNumber();
			
			// first surface: a cylindrical lens aligned with the x direction. 
			//The magnitude of the coefficient will determine the separation between the holograms.
			surfaceParametersTesting.a[0][2][0] = x0Coef;
			
			// third surface: a cylindrical lens whose axis is rotated by alpha/2 w.r.t. the x axis

			// the rotation angle
			double alpha2 = 0.5*MyMath.deg2rad(telescopicInitialisationRotationAngleDegPanel.getNumber());
			double c = Math.cos(alpha2);
			double s = Math.sin(alpha2);
			
			// the vector (x, y), rotated through an angle alpha, becomes (x cos alpha - y sin alpha, y cos alpha + x sin alpha);
			// (x cos alpha - y sin alpha)^2 = x^2 cos^2 alpha  - 2 x y cos alpha sin alpha + y^2 sin^2 alpha 
			double x2coeff = c*c*x0Coef;
			double xycoeff = -2*c*s*x0Coef;
			double y2coeff = s*s*x0Coef;
			
			surfaceParametersTesting.a[2][2][0] = x2coeff;
			surfaceParametersTesting.a[2][2][1] = xycoeff;
			surfaceParametersTesting.a[2][2][2] = y2coeff;
			
			// second surface: sum of the first and second surfaces
			surfaceParametersTesting.a[1][2][0] = x2coeff+x0Coef;
			surfaceParametersTesting.a[1][2][1] = xycoeff;
			surfaceParametersTesting.a[1][2][2] = y2coeff;
			
			//setting the separations
			//for now this is based on the approximate relation between the magnitude of the coefficient and the focal length,
			//given by f = -1/2c where c is the ocular x coefficient. The telescopic length is the sum of the focal lengths. 
			//As our set up is symmetric, we can simply say that the separation,dz, will be 2*f -> dz = -1/c
			//TODO is there a more exact relation? Should we use magnitude as otherwise dz will be negative for positive x coef etc
			surfaceParametersTesting.dz[1] = -1/x0Coef;
			surfaceParametersTesting.dz[2] = -1/x0Coef;
			

			surfaceParametersTesting.repopulateSurfaceParametersTabbedPane(surfaceParametersTabbedPane);
		}else if(e.getSource().equals(backdropComboBox))
		{
			
			backdrop = (Backdrop)(backdropComboBox.getSelectedItem());
			
			updateBackdropPanel();
		}
	}

	/**
	 * The main method, required so that this class can run as a Java application
	 * @param args
	 */
	public static void main(final String[] args)
	{
		(new DerivativeControlledViewRotationExplorer()).run();
	}
}
