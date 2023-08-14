package optics.raytrace.research.skewLensImaging;


import java.io.PrintStream;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;


import math.*;
import net.miginfocom.swing.MigLayout;
import optics.raytrace.sceneObjects.Sphere;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.surfaces.SurfaceColour;
import optics.raytrace.surfaces.IdealThinLensSurface;
import optics.raytrace.core.*;
import optics.raytrace.exceptions.SceneException;
import optics.DoubleColour;
import optics.raytrace.NonInteractiveTIMActionEnum;
import optics.raytrace.NonInteractiveTIMEngine;
import optics.raytrace.GUI.cameras.RenderQualityEnum;
import optics.raytrace.GUI.lowLevel.ApertureSizeType;
import optics.raytrace.GUI.lowLevel.GUIBitsAndBobs;
import optics.raytrace.GUI.lowLevel.LabelledDoublePanel;
import optics.raytrace.GUI.lowLevel.LabelledIntPanel;
import optics.raytrace.GUI.lowLevel.LabelledVector3DPanel;
import optics.raytrace.GUI.sceneObjects.EditableCylinderLattice;
import optics.raytrace.GUI.sceneObjects.EditableFramedRectangle;

/**
 * Simulation of a combination of three skew lenses to emulate relativistic effects.
 * 
 * @author Maik with Jakubs math
 */
public class ThreeSkewLensRelativisticVisualiser extends NonInteractiveTIMEngine
{
	
	/**
	 * Separation between the principal points in lens 1 and lens 2
	 */
	private double d;

	/**
	 * angle (in degrees) between the x axis and lens 1 and lens 2 respectively.
	 */
	private double phi1, phi2;
	
	/**
	 * The rotation angle in degrees, such that the angle between the x axis and the third lens L3 is deltaTheta/2
	 */
	protected double deltaTheta;
	
	/**
	 * The type of mapping we want to simulate
	 */
	
	private enum MappingType
	{
		ROTATION("Rotation"),
		LORENTZ("Lorentz Transformation"),
		GALILEI("Galilei Transformation");

		private String description;

		private MappingType(String description) {this.description = description;}	
		@Override
		public String toString() {return description;}
	}
	
	private MappingType mappingType;

	/**
	 * Show the spheres
	 */
	private boolean showSpheres;
	
	/**
	 * Sphere radii, first the green then the red sphere radius respectively.
	 */
	
	private double sphereRadiusGreen, sphereRadiusRed;
	
	/**
	 * The position of the red and green sphere respectively.
	 */
	
	private Vector3D sphereCentreGreen, sphereCentreRed;
	
	/**
	 * Show the lattice
	 */
	private boolean showLattice;
	
	/**
	 * Lattice parameters 
	 */
	private double xMin, xMax, yMin, yMax, zMin, zMax, latticeRadius; 
	private int nX, nY, nZ;
	private	Vector3D latticeCentre;
	
	/**
	 * Lens width and height
	 */
	
	private double width, height;
	
	/**
	 * The common bottom corner of the lenses. By default this is 0,0,0
	 */
	
	private Vector3D commonCorner;
	
	/**
	 * Should the lenses show the frames?
	 */
	private boolean showFrames;
	
	/**
	 * should the lenses be visible at all?
	 */
	private boolean showLenses;
	
	/**
	 * Should the objects have a shadow
	 */
	private boolean shadowThrowing;
	
	/**
	 * The frame radius of the lenses
	 */
	private double frameWidth;
	
	/**
	 * The studio initialisation type for the background scene
	 */
	
	private StudioInitialisationType studioInitialisation;
	
	/**
	 * rotate camera by deltaPhi, centred around the common corner about the Y-axis.
	 */
	private boolean rotateCamera;
	
	public ThreeSkewLensRelativisticVisualiser()
	{
		// set all standard parameters, including those for the camera
		super();
		
		showLenses = true;
		d = 0.01;
		phi1 = -2.5;
		phi2 = 2.5;
		deltaTheta = 15;
		
		mappingType = MappingType.ROTATION;
		
		showSpheres = true;
		sphereRadiusGreen=0.5;
		sphereRadiusRed=0.5;
		sphereCentreGreen = new Vector3D(0,0,-2);
		sphereCentreRed = new Vector3D(0,0.5,-4);
		
		width = 2;
		height = 2;
		
		commonCorner = new Vector3D(0,-1,0);
		showFrames = true;
		frameWidth = 0.01;
		shadowThrowing = false;
		
		showLattice = false;
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
		latticeCentre = Vector3D.O;
		
		rotateCamera = false;
		renderQuality = RenderQualityEnum.DRAFT;
		nonInteractiveTIMAction = NonInteractiveTIMActionEnum.INTERACTIVE;
		cameraViewDirection = new Vector3D(0, 0, -1);
		cameraViewCentre = new Vector3D(0, 0, 0);
		cameraDistance = 10;
		cameraFocussingDistance = 10;
		cameraApertureSize = ApertureSizeType.PINHOLE;
		cameraHorizontalFOVDeg = 25;
		
		studioInitialisation = StudioInitialisationType.MINIMALIST;	// the backdrop
		
		if(nonInteractiveTIMAction == NonInteractiveTIMActionEnum.INTERACTIVE)
		{
			windowTitle = "Dr TIM's 3-skew-lens relativistic visualiser";
			windowWidth = 1500;
			windowHeight = 650;
		}
	}

	@Override
	public String getClassName()
	{
		return "ThreeSkewLensRelativisticVisualiser"	// the name
				;
	}
	
	@Override
	public void writeParameters(PrintStream printStream)
	{
		// write any parameters not defined in NonInteractiveTIMEngine
		printStream.println("Scene initialisation");
		printStream.println();
		
		printStream.println("showLenses = "+showLenses);
		printStream.println("shadowThrowing = "+shadowThrowing);
		printStream.println("d = "+d);
		printStream.println("phi1 = "+phi1);
		printStream.println("phi2 = "+phi2);
		printStream.println("deltaTheta = "+deltaTheta);
		printStream.println("commonCorner = "+commonCorner);
		printStream.println("width = "+width);
		printStream.println("height = "+height);
		printStream.println("mappingType = "+mappingType);
		
		//  Scene
		printStream.println("showSpheres = "+showSpheres);
		printStream.println("sphereRadiusGreen = "+sphereRadiusGreen);
		printStream.println("sphereRadiusRed = "+sphereRadiusRed);
		printStream.println("sphereCentreGreen = "+sphereCentreGreen);
		printStream.println("sphereCentreRed = "+sphereCentreRed);
		
		printStream.println("showLattice = "+showLattice);
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
		
		printStream.println("cameraHorizontalFOVDeg = "+cameraHorizontalFOVDeg);
		printStream.println("RotateCamera = "+rotateCamera);

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

				// initialise the scene and lights
				StudioInitialisationType.initialiseSceneAndLights(
						studioInitialisation,
						scene,
						studio
						);
				
				studio.setLights(LightSource.getStandardLightsFromTheFront());
		
		
				//
				//adding the rectangular lenses
				//
				
				//First define three frame colours...
				SurfaceColour[] frameColours = {
						new SurfaceColour("red matt", DoubleColour.RED, DoubleColour.BLACK, shadowThrowing), 
						new SurfaceColour("yellow matt", DoubleColour.YELLOW, DoubleColour.BLACK, shadowThrowing),
						new SurfaceColour("green matt", DoubleColour.GREEN, DoubleColour.BLACK, shadowThrowing),
						};
				for (int i = 0; i < 3; i++) {
					
				//calculate the principal point and the respective span vectors.
				Vector3D p =  calculatePrincipalPoints(d, MyMath.deg2rad(deltaTheta), MyMath.deg2rad(phi1), MyMath.deg2rad(phi2), commonCorner)[i];
				Vector3D widthNormalVector = Vector3D.difference(p, commonCorner).getPartPerpendicularTo(Vector3D.Y);
				//NOw to add the lens
				EditableFramedRectangle lens = new EditableFramedRectangle(
						"lens "+(i+1),	// description
						commonCorner,	// corner
						widthNormalVector.getWithLength(width),	// widthVector
						Vector3D.Y.getProductWith(height),	// heightVector
						frameWidth,	// frameRadius
						new IdealThinLensSurface(
								Vector3D.crossProduct(widthNormalVector, Vector3D.Y),	// opticalAxisDirection
								p,// principalPoint,
								calculateFocalLengths(d, MyMath.deg2rad(deltaTheta), MyMath.deg2rad(phi1), MyMath.deg2rad(phi2))[i],	// focalLength 
								SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT,	// transmissionCoefficient
								shadowThrowing	// shadowThrowing
							),
						frameColours[i],	// frameSurfaceProperty
						showFrames,	// showFrames
						scene,	// parent 
						studio
						);
				scene.addSceneObject(lens, showLenses);
				}
				
				
				//
				//Adding the spheres
				//
				//The first, green sphere
				scene.addSceneObject(
						new Sphere(
						"Green Sphere",// description, 
						sphereCentreGreen,// centre, 
						sphereRadiusGreen,// radius,
						new SurfaceColour("green shiny", DoubleColour.GREEN, DoubleColour.WHITE, shadowThrowing),// surfaceProperty,
						scene,	// parent 
						studio
						),showSpheres
						);
				
				//The second, red sphere
				scene.addSceneObject(
						new Sphere(
						"Red Sphere",// description, 
						sphereCentreRed,// centre, 
						sphereRadiusRed,// radius,
						new SurfaceColour("red shiny", DoubleColour.RED, DoubleColour.WHITE, shadowThrowing),// surfaceProperty,
						scene,	// parent 
						studio
						),showSpheres
						);	
				
				EditableCylinderLattice lattice=	new EditableCylinderLattice(
						"lattice",// description,
						xMin, xMax, nX, Vector3D.X,
						yMin, yMax, nY, Vector3D.Y,
						zMin, zMax, nZ, Vector3D.Z,
						latticeRadius,
						latticeCentre,
						shadowThrowing,
						scene,	// parent 
						studio
						);
				scene.addSceneObject(lattice, showLattice);
				

				// the camera
				//Vector3D standardCameraViewDirection = cameraViewDirection;
				if(rotateCamera) {
					cameraViewDirection = Geometry.rotate(cameraViewDirection, Vector3D.Y, -MyMath.deg2rad(deltaTheta));
				}
				studio.setCamera(getStandardCamera());
	}
	
	//
	// calculating some parameters based on Jakub's "Three Skew Lens Visualiser" document
	//
	
	/**
	 * Calculate the focal length of each lens with the first index relating to lens 1 and the last to lens 3.
	 * @param d
	 * @param deltaTheta
	 * @param phi1
	 * @param phi2
	 * @return
	 */
	private double[] calculateFocalLengths(double d, double deltaTheta, double phi1, double phi2) {
		double focalLengthL1 = (d/(2*Math.sin(deltaTheta/2))) * Math.sin((deltaTheta/2) + phi1);
		double focalLengthL2 = (d/(2*Math.sin(deltaTheta/2))) * Math.sin((deltaTheta/2) - phi2);
		double focalLengthL3 = (d/(2*Math.sin(deltaTheta/2))) * ( Math.sin((deltaTheta/2) + phi1) * Math.sin((deltaTheta/2) - phi2) )/Math.sin(phi2-phi1);
	
		double[] focalLength = {focalLengthL1, focalLengthL2, focalLengthL3};
		return focalLength;
	}
	
	/**
	 * Calculate the principal point of the lenses such that the first index corresponds to the principal point of lens 1.
	 * @param d
	 * @param deltaTheta
	 * @param phi1
	 * @param phi2
	 * @param commonEdge
	 * @return
	 */
	private Vector3D[] calculatePrincipalPoints(double d, double deltaTheta, double phi1, double phi2, Vector3D commonCorner) {
		//Setting the correct C value depending on the transformation chosen.
		double c;
		switch(mappingType) {
		case GALILEI:
			c = 0;
			break;
		case LORENTZ:
			c = Math.tan(deltaTheta);
			break;
		case ROTATION:
		default:
			c = -Math.sin(deltaTheta);
			break;
		}
		
		//System.out.println("c = "+c);
		//Pre calculating the R variables again defined in Jakub's document
		Double r1 = -(d/Math.sin(phi2-phi1)) * Math.cos(phi2);
		Double r2 = -(d/Math.sin(phi2-phi1)) * Math.cos(phi1);
		Double r3 = -(d/Math.sin(phi2-phi1)) * (2*Math.cos(phi1)*Math.cos(phi2)*Math.sin(deltaTheta) + c*(Math.cos(phi1+phi2)-Math.cos(phi2-phi1-deltaTheta)))/
				(2*Math.sin(deltaTheta)*Math.cos(deltaTheta/2));
		
		
		Vector3D principalPointL1 = Vector3D.sum(new Vector3D(Math.cos(phi1),0,-Math.sin(phi1)).getProductWith(r1),commonCorner, Vector3D.Y.getProductWith(height/2));
		Vector3D principalPointL2 = Vector3D.sum(new Vector3D(Math.cos(phi2),0,-Math.sin(phi2)).getProductWith(r2),commonCorner, Vector3D.Y.getProductWith(height/2));
		Vector3D principalPointL3 = Vector3D.sum(new Vector3D(Math.cos(deltaTheta/2),0,-Math.sin(deltaTheta/2)).getProductWith(r3),commonCorner, Vector3D.Y.getProductWith(height/2));

		Vector3D[] principalPoints = {principalPointL1, principalPointL2, principalPointL3};
		return principalPoints;
	}
	
	//
	// GUI panels
	//
	
	//Scene Panels
	private LabelledDoublePanel dPanel, phi2Panel, phi1Panel, deltaThetaPanel, widthPanel, heightPanel, frameWidthPanel, 
	xMinPanel, xMaxPanel, yMinPanel, yMaxPanel, zMinPanel, zMaxPanel, latticeRadiusPanel ,sphereRadiusRedPanel, sphereRadiusGreenPanel;
	private LabelledIntPanel nXPanel, nYPanel, nZPanel;
	private LabelledVector3DPanel commonCornerPanel, sphereCentreGreenPanel, sphereCentreRedPanel, latticeCentrePanel;
	private JComboBox<MappingType> mappingTypeComboBox;
	private JComboBox<StudioInitialisationType> studioInitialisationComboBox;
	private JCheckBox showFramesCheckBox, showLensesCheckBox, shadowThrowingCheckBox, showSpheresCheckBox, showLatticeCheckBox;
	
	//showLatticeCheckBox, showSpheresCheckBox, latticeCentrePanel, nXPanel, nYPanel, nZPanel; xMinPanel, xMaxPanel, yMinPanel, yMaxPanel, zMinPanel, zMaxPanel, latticeRadiusPanel
	
	//Camera panels
	private LabelledDoublePanel cameraDistancePanel, cameraFocussingDistancePanel, cameraHorizontalFOVDegPanel;
	private LabelledVector3DPanel cameraViewCentrePanel, cameraViewDirectionPanel;
	private JCheckBox  rotateCameraCheckBox;
	private JComboBox<ApertureSizeType> cameraApertureSizeComboBox;
	@Override
	protected void createInteractiveControlPanel()
	{
		super.createInteractiveControlPanel();

		JTabbedPane tabbedPane = new JTabbedPane();
		interactiveControlPanel.add(tabbedPane, "span");
		
		//
		//Scene panel stuff
		//
		
		//first the lenses
		JPanel lensPanel = new JPanel();
		lensPanel.setLayout(new MigLayout("insets 0"));
		tabbedPane.addTab("Lenses", lensPanel);
		
		mappingTypeComboBox = new JComboBox<MappingType>(MappingType.values());
		mappingTypeComboBox.setSelectedItem(mappingType);
		lensPanel.add(GUIBitsAndBobs.makeRow("mapping type", mappingTypeComboBox), "span");
		
		commonCornerPanel = new LabelledVector3DPanel("Lower common corner");
		commonCornerPanel.setVector3D(commonCorner);
		lensPanel.add(commonCornerPanel, "span");

		dPanel = new LabelledDoublePanel("d separation between P1 and P2");
		dPanel.setNumber(d);
		lensPanel.add(dPanel, "span");
		
		phi1Panel = new LabelledDoublePanel("\u03C6 1");
		phi1Panel.setNumber(phi1);
		lensPanel.add(phi1Panel);
		
		phi2Panel = new LabelledDoublePanel("and \u03C6 2");
		phi2Panel.setNumber(phi2);
		lensPanel.add(phi2Panel,"span");
		
		deltaThetaPanel= new LabelledDoublePanel("\u0394 \u03B8");
		deltaThetaPanel.setNumber(deltaTheta);
		lensPanel.add(deltaThetaPanel, "span");
		
		widthPanel = new LabelledDoublePanel("lens width");
		widthPanel.setNumber(width);
		lensPanel.add(widthPanel);
		
		heightPanel= new LabelledDoublePanel("and height");
		heightPanel.setNumber(height);
		lensPanel.add(heightPanel, "span");
		
		showFramesCheckBox = new JCheckBox("Show lens frames");
		showFramesCheckBox.setSelected(showFrames);
		lensPanel.add(showFramesCheckBox);
		
		frameWidthPanel = new LabelledDoublePanel("of width");
		frameWidthPanel.setNumber(frameWidth);
		lensPanel.add(frameWidthPanel, "span");
		
		showLensesCheckBox = new JCheckBox("Show lenses");
		showLensesCheckBox.setSelected(showLenses);
		lensPanel.add(showLensesCheckBox, "span");
		
		
		//Then the spheres, lattice and backdrop
		JPanel scenePanel = new JPanel();
		scenePanel.setLayout(new MigLayout("insets 0"));
		tabbedPane.addTab("Scene", scenePanel);
		
		studioInitialisationComboBox = new JComboBox<StudioInitialisationType>(StudioInitialisationType.values());
		studioInitialisationComboBox.setSelectedItem(studioInitialisation);
		scenePanel.add(GUIBitsAndBobs.makeRow("Studio initialisation", studioInitialisationComboBox), "span");
		
		shadowThrowingCheckBox = new JCheckBox("Shadows");
		shadowThrowingCheckBox.setSelected(shadowThrowing);
		scenePanel.add(shadowThrowingCheckBox, "span");
		
		JPanel spherePanel = new JPanel();
		spherePanel.setLayout(new MigLayout("insets 0"));
		spherePanel.setBorder(GUIBitsAndBobs.getTitledBorder("Spheres"));
		scenePanel.add(spherePanel, "span");
		
		showSpheresCheckBox  = new JCheckBox("show spheres");
		showSpheresCheckBox.setSelected(showSpheres);
		spherePanel.add(showSpheresCheckBox, "span"); 
		
		sphereCentreGreenPanel = new LabelledVector3DPanel("Green sphere at");
		sphereCentreGreenPanel.setVector3D(sphereCentreGreen);
		spherePanel.add(sphereCentreGreenPanel, "span");

		sphereRadiusGreenPanel = new LabelledDoublePanel("of radius");
		sphereRadiusGreenPanel.setNumber(sphereRadiusGreen);
		spherePanel.add(sphereRadiusGreenPanel, "span");
		
		sphereCentreRedPanel = new LabelledVector3DPanel("Red sphere at");
		sphereCentreRedPanel.setVector3D(sphereCentreRed);
		spherePanel.add(sphereCentreRedPanel, "span");

		sphereRadiusRedPanel = new LabelledDoublePanel("of radius");
		sphereRadiusRedPanel.setNumber(sphereRadiusRed);
		spherePanel.add(sphereRadiusRedPanel, "span");
		
		
		JPanel latticePanel = new JPanel();
		latticePanel.setLayout(new MigLayout("insets 0"));
		latticePanel.setBorder(GUIBitsAndBobs.getTitledBorder("Lattice"));
		scenePanel.add(latticePanel, "span");
		
		showLatticeCheckBox = new JCheckBox("Show lattice");
		showLatticeCheckBox.setSelected(showLattice);
		latticePanel.add(showLatticeCheckBox, "span"); 
		
		latticeCentrePanel= new LabelledVector3DPanel("Lattice centred around");
		latticeCentrePanel.setVector3D(latticeCentre);
		latticePanel.add(latticeCentrePanel, "span");
		
		latticeRadiusPanel = new LabelledDoublePanel("Cylinder radius");
		latticeRadiusPanel.setNumber(latticeRadius);
		latticePanel.add(latticeRadiusPanel, "wrap");
		
		xMinPanel = new LabelledDoublePanel("x_min");
		xMinPanel.setNumber(xMin);
		latticePanel.add(xMinPanel, "split 3");
		
		xMaxPanel = new LabelledDoublePanel(", x_max");
		xMaxPanel.setNumber(xMax);
		latticePanel.add(xMaxPanel);
		
		nXPanel = new LabelledIntPanel(", no of cylinders");
		nXPanel.setNumber(nX);
		latticePanel.add(nXPanel, "wrap");

		yMinPanel = new LabelledDoublePanel("y_min");
		yMinPanel.setNumber(yMin);
		latticePanel.add(yMinPanel, "split 3");
		
		yMaxPanel = new LabelledDoublePanel(", y_max");
		yMaxPanel.setNumber(yMax);
		latticePanel.add(yMaxPanel);
		
		nYPanel = new LabelledIntPanel(", no of cylinders");
		nYPanel.setNumber(nY);
		latticePanel.add(nYPanel, "wrap");
		
		zMinPanel = new LabelledDoublePanel("z_min");
		zMinPanel.setNumber(zMin);
		latticePanel.add(zMinPanel, "split 3");
		
		zMaxPanel = new LabelledDoublePanel(", z_max");
		zMaxPanel.setNumber(zMax);
		latticePanel.add(zMaxPanel);
		
		nZPanel = new LabelledIntPanel(", no of cylinders");
		nZPanel.setNumber(nZ);
		latticePanel.add(nZPanel, "wrap");		
		
		//Camera panel stuff
		JPanel cameraPanel = new JPanel();
		cameraPanel.setLayout(new MigLayout("insets 0"));
		tabbedPane.addTab("Camera", cameraPanel);
		
		cameraApertureSizeComboBox = new JComboBox<ApertureSizeType >(ApertureSizeType .values());
		cameraApertureSizeComboBox.setSelectedItem(cameraApertureSize);
		cameraPanel.add(GUIBitsAndBobs.makeRow("Camera aperture", cameraApertureSizeComboBox), "span");
		
		cameraViewCentrePanel = new LabelledVector3DPanel("View centre");
		cameraViewCentrePanel.setVector3D(cameraViewCentre);
		cameraPanel.add(cameraViewCentrePanel, "span");
		
		cameraViewDirectionPanel = new LabelledVector3DPanel("View direction");
		cameraViewDirectionPanel.setVector3D(cameraViewDirection);
		cameraPanel.add(cameraViewDirectionPanel, "span");

		cameraDistancePanel = new LabelledDoublePanel("Camera distance from view centre along view direction");
		cameraDistancePanel.setNumber(cameraDistance);
		cameraPanel.add(cameraDistancePanel, "span");
		
		cameraHorizontalFOVDegPanel = new LabelledDoublePanel("Field of view (in degrees)");
		cameraHorizontalFOVDegPanel.setNumber(cameraHorizontalFOVDeg);
		cameraPanel.add(cameraHorizontalFOVDegPanel, "span");
		
		cameraFocussingDistancePanel = new LabelledDoublePanel("Focussing distance");
		cameraFocussingDistancePanel.setNumber(cameraFocussingDistance);
		cameraPanel.add(cameraFocussingDistancePanel, "span");
		
		rotateCameraCheckBox = new JCheckBox("Rotate camera by \u0394 \u03B8");
		rotateCameraCheckBox.setSelected(rotateCamera);
		cameraPanel.add(rotateCameraCheckBox);
	}
	
	//Setting the interactive values
	@Override
	protected void acceptValuesInInteractiveControlPanel()
	{
		super.acceptValuesInInteractiveControlPanel();	
		
		//First the lens values
		d = dPanel.getNumber();
		phi2 = phi2Panel.getNumber();
		phi1 = phi1Panel.getNumber();
		deltaTheta = deltaThetaPanel.getNumber();
		width = widthPanel.getNumber();
		height = heightPanel.getNumber();
		frameWidth = frameWidthPanel.getNumber();
		showFrames = showFramesCheckBox.isSelected();
		commonCorner = commonCornerPanel.getVector3D();
		mappingType = (MappingType)(mappingTypeComboBox.getSelectedItem());
		showLenses = showLensesCheckBox.isSelected();
		shadowThrowing = shadowThrowingCheckBox.isSelected();
		
		//Now the rest of the scene
		showSpheres = showSpheresCheckBox.isSelected();
		sphereRadiusRed = sphereRadiusRedPanel.getNumber();
		sphereRadiusGreen = sphereRadiusGreenPanel.getNumber();
		sphereCentreGreen = sphereCentreGreenPanel.getVector3D();
		sphereCentreRed =  sphereCentreRedPanel.getVector3D();
		studioInitialisation = (StudioInitialisationType)studioInitialisationComboBox.getSelectedItem();
		
		showLattice = showLatticeCheckBox.isSelected();
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
		
		//and lastly the camera values
		cameraDistance = cameraDistancePanel.getNumber();
		cameraFocussingDistance = cameraFocussingDistancePanel.getNumber();
		cameraHorizontalFOVDeg = cameraHorizontalFOVDegPanel.getNumber();
		cameraViewCentre = cameraViewCentrePanel.getVector3D();
		cameraViewDirection = cameraViewDirectionPanel.getVector3D();
		cameraApertureSize = (ApertureSizeType)cameraApertureSizeComboBox.getSelectedItem();
		rotateCamera = rotateCameraCheckBox.isSelected();

		
	}

	public static void main(final String[] args)
	{
		(new ThreeSkewLensRelativisticVisualiser()).run();
	}
}
