package optics.raytrace.research.skewLensImaging;


import java.awt.event.ActionEvent;
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
import optics.raytrace.GUI.lowLevel.DoublePanel;
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
	
	private static final long serialVersionUID = -1307968964739670480L;

	/**
	 * Separation between the principal points in lens 1 and lens 2
	 */
	private double d1, d2;

	/**
	 * angle (in degrees) between the x axis and lens 1 and lens 2 respectively.
	 */
	private double phi1, phi2;
	
	/**
	 * The rotation angle in degrees, such that the angle between the x axis and the third lens L3 is deltaTheta/2
	 */
	protected double deltaTheta1, deltaTheta2;
	
	/**
	 * The type of mapping we want to simulate
	 */
	
	public enum MappingType
	{
		ROTATION("Rotation"),
		LORENTZ("Lorentz Transformation"),
		GALILEI("Galilei Transformation"),
		WIGNER("Wigner Rotation");

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
	 * The common bottom corners of the lenses (two in the case the Wigner rotation). By default this is 0,-1,0 
	 */
	
	private Vector3D commonCorner1, commonCorner2;
	
	/**
	 * Should the lenses show the frames?
	 */
	private boolean showFrames;
	
	/**
	 * should the lenses be visible at all?
	 */
	private boolean showLenses;
	
	/**
	 * additional shift in the x and y axis
	 */
	private double[] extraShift;
	
	/**
	 * Should the individual lorenzers be shown
	 */
	private boolean showLensesL1, showLensesL2;
	
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
		showLensesL1 = true;
		showLensesL2=true;
		d1 = 0.01;
		d2 = 0.01;
		phi1 = -2.5;
		phi2 = 2.5;
		deltaTheta1 = 15;
		deltaTheta2 = -15;
		extraShift = new double[6];
		
		mappingType = MappingType.ROTATION;
		
		showSpheres = true;
		sphereRadiusGreen=0.5;
		sphereRadiusRed=0.5;
		sphereCentreGreen = new Vector3D(0,0,-2);
		sphereCentreRed = new Vector3D(0,0.5,-4);
		
		width = 2;
		height = 2;
		
		commonCorner1 = new Vector3D(0,-1,0);
		commonCorner2 = new Vector3D(0,-1,0);
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
		
		
		printStream.println("mappingType = "+mappingType);		
		printStream.println("showLenses = "+showLenses);
		printStream.println("shadowThrowing = "+shadowThrowing);
		printStream.println("d1 = "+d1);
		printStream.println("deltaTheta1 = "+deltaTheta1);
		printStream.println("commonCorner1 = "+commonCorner1);
		
		switch(mappingType) {
		case WIGNER:
			printStream.println("d2 = "+d2);
			printStream.println("deltaTheta2 = "+deltaTheta2);
			printStream.println("commonCorner2 = "+commonCorner2);
			printStream.println("showLensesL1 ="+showLensesL1);
			printStream.println("showLensesL2 ="+showLensesL2);
			for (int i=0;i<3;i++) {
				printStream.println("L1 principal point x"+i+"shift ="+extraShift[i]);	
			}
			for (int i=0;i<3;i++) {
				printStream.println("L2 principal point y"+i+"shift ="+extraShift[i]);
			}
			
			break;
		case GALILEI:
		case LORENTZ:
		case ROTATION:
		default:
			break;
		}
		
		printStream.println("phi1 = "+phi1);
		printStream.println("phi2 = "+phi2);
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
				
				//defining three alternating frame colours
				SurfaceColour[] frameColours = {
						new SurfaceColour("red matt", DoubleColour.RED, DoubleColour.BLACK, shadowThrowing), 
						new SurfaceColour("yellow matt", DoubleColour.YELLOW, DoubleColour.BLACK, shadowThrowing),
						new SurfaceColour("green matt", DoubleColour.GREEN, DoubleColour.BLACK, shadowThrowing),
						new SurfaceColour("red shiny", DoubleColour.RED, DoubleColour.WHITE, shadowThrowing), 
						new SurfaceColour("yellow shiny", DoubleColour.YELLOW, DoubleColour.WHITE, shadowThrowing),
						new SurfaceColour("green shiny", DoubleColour.GREEN, DoubleColour.WHITE, shadowThrowing)
						};
				
				
				switch(mappingType) {
				case WIGNER:
					//First define three frame colours...
					for (int i = 0; i < 3; i++) {
						
						//calculate the principal point and the respective span vectors.
						Vector3D pL1 =  calculateWignerPrincipalPoints(
								d1, d2, MyMath.deg2rad(deltaTheta1), MyMath.deg2rad(deltaTheta2), height, height, commonCorner1, commonCorner2)[i];
						
						Vector3D pL2 =  calculateWignerPrincipalPoints(
								d1, d2, MyMath.deg2rad(deltaTheta1), MyMath.deg2rad(deltaTheta2), height, height, commonCorner1, commonCorner2)[i+3];

						Vector3D widthVectorL1 = pL1.x == commonCorner1.x ? Vector3D.X.getWithLength(width) : Vector3D.difference(pL1, commonCorner1).getPartParallelTo(Vector3D.X).getWithLength(width);
						Vector3D heightVectorL1 = Vector3D.difference(pL1, commonCorner1).getPartPerpendicularTo(Vector3D.X).getWithLength(height);
						Vector3D normalL1 = Vector3D.crossProduct(heightVectorL1, widthVectorL1).getNormalised();
						
						Vector3D widthVectorL2 = Vector3D.difference(pL2, commonCorner2).getPartPerpendicularTo(Vector3D.Y).getWithLength(width);
						Vector3D heightVectorL2 = pL2.y == commonCorner2.y ? Vector3D.Y.getWithLength(height) : Vector3D.difference(pL2, commonCorner2).getPartParallelTo(Vector3D.Y).getWithLength(height);
						Vector3D normalL2 = Vector3D.crossProduct(heightVectorL2, widthVectorL2).getNormalised();
					
						System.out.println("pL1 ="+pL1+" pL2= "+pL2);
						//NOw to add the lens
						EditableFramedRectangle lens1 = new EditableFramedRectangle(
								"lens "+(i+1),	// description
								commonCorner1,	// corner
								widthVectorL1,	// widthVector
								heightVectorL1,	// heightVector
								frameWidth,	// frameRadius
								new IdealThinLensSurface(
										normalL1,	// opticalAxisDirection
										pL1.getSumWith(new Vector3D(extraShift[i],0,0)),// principalPoint,
										calculateWingerFocalLengths(d1, d2, MyMath.deg2rad(deltaTheta1), MyMath.deg2rad(deltaTheta2))[i],// focalLength 
										SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT,	// transmissionCoefficient
										shadowThrowing	// shadowThrowing
									),
								frameColours[i],	// frameSurfaceProperty
								showFrames,	// showFrames
								scene,	// parent 
								studio
								);
					scene.addSceneObject(lens1, showLensesL1);
					
					EditableFramedRectangle lens2 = new EditableFramedRectangle(
							"lens "+(i+4),	// description
							commonCorner2,	// corner
							widthVectorL2,	// widthVector
							heightVectorL2,	// heightVector
							frameWidth,	// frameRadius
							new IdealThinLensSurface(
									normalL2,	// opticalAxisDirection
									pL2.getSumWith(new Vector3D(0,extraShift[i+3],0)),// principalPoint,
									calculateWingerFocalLengths(d1, d2, MyMath.deg2rad(deltaTheta1), MyMath.deg2rad(deltaTheta2))[i+3],// focalLength 
									SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT,	// transmissionCoefficient
									shadowThrowing	// shadowThrowing
								),
							frameColours[i+3],	// frameSurfaceProperty
							showFrames,	// showFrames
							scene,	// parent 
							studio
							);
				scene.addSceneObject(lens2, showLensesL2);
					}
					break;
				case GALILEI:
				case LORENTZ:
				case ROTATION:
				default:
					for (int i = 0; i < 3; i++) {
						
					//calculate the principal point and the respective span vectors.
					Vector3D p =  calculatePrincipalPoints(d1, MyMath.deg2rad(deltaTheta1), MyMath.deg2rad(phi1), MyMath.deg2rad(phi2), commonCorner1)[i];
					Vector3D widthNormalVector = Vector3D.difference(p, commonCorner1).getPartPerpendicularTo(Vector3D.Y);
					System.out.println("p"+p);
					//Now to add the lens
					EditableFramedRectangle lens = new EditableFramedRectangle(
							"lens "+(i+1),	// description
							commonCorner1,	// corner
							widthNormalVector.getWithLength(width),	// widthVector
							Vector3D.Y.getProductWith(height),	// heightVector
							frameWidth,	// frameRadius
							new IdealThinLensSurface(
									Vector3D.crossProduct(widthNormalVector, Vector3D.Y),	// opticalAxisDirection
									p,// principalPoint,
									calculateFocalLengths(d1, MyMath.deg2rad(deltaTheta1), MyMath.deg2rad(phi1), MyMath.deg2rad(phi2))[i],	// focalLength 
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
					break;
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
					cameraViewDirection = Geometry.rotate(cameraViewDirection, Vector3D.Y, -MyMath.deg2rad(deltaTheta1));
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
		//System.out.println("focalLength"+focalLength[0]);
		return focalLength;
	}
	
	private double[] calculateWingerFocalLengths(double d1,double d2, double deltaTheta1, double deltaTheta2) {
		double focalLengthL11 = d1/2;
		double focalLengthL12 = d1/(4*Math.cos(deltaTheta1/4));
		double focalLengthL13 = d1/2;
		
		double focalLengthL21 = d2/2;
		double focalLengthL22 = d2/(4*Math.cos(deltaTheta2/4));
		double focalLengthL23 = d2/2;
	
		double[] focalLength = {focalLengthL11, focalLengthL12, focalLengthL13, focalLengthL21, focalLengthL22, focalLengthL23};
		//System.out.println("focalLength"+focalLength[3]);
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
	
	private Vector3D[] calculateWignerPrincipalPoints(double d1, double d2, double deltaTheta1, double deltaTheta2, double l, double h, Vector3D commonCorner1, Vector3D commonCorner2) {

		//Pre calculating the R variables again defined in Jakub's second document
		Double r11 = (d1/Math.sin(deltaTheta1/4))*Math.cos(deltaTheta1/4);
		Double r12 = d1/Math.sin(deltaTheta1/4);
		Double r13 = (d1/Math.sin(deltaTheta1/4))*(Math.cos(3*deltaTheta1/4)/Math.cos(deltaTheta1));
		
		Double r21 = -(d2/Math.sin(deltaTheta2/4))*Math.cos(deltaTheta2/4);
		Double r22 = -d2/Math.sin(deltaTheta2/4);
		Double r23 = -(d2/Math.sin(deltaTheta2/4))*(Math.cos(3*deltaTheta2/4)/Math.cos(deltaTheta2));
		
		//Pre calculating xp and yp
		double yP = 0;//h*(1-Math.signum(deltaTheta1))/2; the original setting did not work so we are setting them to zero (for now) and manually setting the x and y offset 
		double xP = 0;//l*(1+Math.signum(deltaTheta2))/2;
		
//		System.out.println("xp="+xP+"  yp="+yP);
//		System.out.println("r11="+r11+" r12="+r12+" r13="+r13);
//		System.out.println("r21="+r21+" r22="+r22+" r23="+r23);
//		System.out.println();
				
		//Calculating the principal points
		Vector3D principalPointL11 = Vector3D.sum(new Vector3D(xP,yP,0), new Vector3D(0,1,0).getProductWith(r11), commonCorner1);
		Vector3D principalPointL12 = Vector3D.sum(new Vector3D(xP,yP,0), new Vector3D(0,Math.cos(deltaTheta1/4),-Math.sin(deltaTheta1/4)).getProductWith(r12),commonCorner1);
		Vector3D principalPointL13 = Vector3D.sum(new Vector3D(xP,yP,0), new Vector3D(0,Math.cos(deltaTheta1/2),-Math.sin(deltaTheta1/2)).getProductWith(r13),commonCorner1);

		Vector3D principalPointL21 = Vector3D.sum(new Vector3D(xP,yP,0), new Vector3D(1,0,0).getProductWith(r21),commonCorner2);
		Vector3D principalPointL22 = Vector3D.sum(new Vector3D(xP,yP,0), new Vector3D(Math.cos(deltaTheta2/4),0,-Math.sin(deltaTheta2/4)).getProductWith(r22),commonCorner2);
		Vector3D principalPointL23 = Vector3D.sum(new Vector3D(xP,yP,0), new Vector3D(Math.cos(deltaTheta2/2),0,-Math.sin(deltaTheta2/2)).getProductWith(r23),commonCorner2);

		Vector3D[] principalPoints = {principalPointL11, principalPointL12, principalPointL13, principalPointL21, principalPointL22, principalPointL23};
		return principalPoints;
	}
	
	
	//
	// GUI panels
	//
	
	//Scene Panels
	private LabelledDoublePanel d1Panel, d2Panel, phi2Panel, phi1Panel, deltaTheta1Panel,deltaTheta2Panel, widthPanel, heightPanel, frameWidthPanel, 
	xMinPanel, xMaxPanel, yMinPanel, yMaxPanel, zMinPanel, zMaxPanel, latticeRadiusPanel ,sphereRadiusRedPanel, sphereRadiusGreenPanel;
	private LabelledIntPanel nXPanel, nYPanel, nZPanel;
	private LabelledVector3DPanel commonCorner1Panel, commonCorner2Panel, sphereCentreGreenPanel, sphereCentreRedPanel, latticeCentrePanel;
	private JComboBox<MappingType> mappingTypeComboBox;
	private JComboBox<StudioInitialisationType> studioInitialisationComboBox;
	private JCheckBox showFramesCheckBox, showLensesCheckBox, shadowThrowingCheckBox, showSpheresCheckBox, showLatticeCheckBox, showLensesL1CheckBox, showLensesL2CheckBox;
	private JPanel customLensPartsPanel;
	private DoublePanel[] extraShiftPanel;

	
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
		mappingTypeComboBox.addActionListener(this);
		lensPanel.add(GUIBitsAndBobs.makeRow("mapping type", mappingTypeComboBox), "span");
		
		customLensPartsPanel = new JPanel();
		customLensPartsPanel.setLayout(new MigLayout("insets 0"));
		lensPanel.add(customLensPartsPanel, "span");
		
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
		
		updateMappingPanel();
	}
	
	//Setting the interactive values
	@Override
	protected void acceptValuesInInteractiveControlPanel()
	{
		super.acceptValuesInInteractiveControlPanel();	


		mappingType = (MappingType)(mappingTypeComboBox.getSelectedItem());
		//First the lens values
		switch(mappingType) { 
		case WIGNER:
			d2 = d2Panel.getNumber();
			deltaTheta2 = deltaTheta2Panel.getNumber();
			commonCorner2 = commonCorner2Panel.getVector3D();
			showLensesL1 = showLensesL1CheckBox.isSelected();
			showLensesL2 = showLensesL2CheckBox.isSelected();
			for(int i =0; i<extraShift.length;i++) {
				extraShift[i] = extraShiftPanel[i].getNumber();
			}
			break;
		case GALILEI:
		case LORENTZ:
		case ROTATION:
		default:
			phi2 = phi2Panel.getNumber();
			phi1 = phi1Panel.getNumber();
			break;
		}
		d1 = d1Panel.getNumber();
		deltaTheta1 = deltaTheta1Panel.getNumber();
		commonCorner1 = commonCorner1Panel.getVector3D();
		width = widthPanel.getNumber();
		height = heightPanel.getNumber();
		frameWidth = frameWidthPanel.getNumber();
		showFrames = showFramesCheckBox.isSelected();
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
	
	private void updateMappingPanel()
	{
		customLensPartsPanel.removeAll();
		
		switch(mappingType) {
		case WIGNER:
			
			JPanel l1Panel = new JPanel();
			l1Panel.setLayout(new MigLayout("insets 0"));
			l1Panel.setBorder(GUIBitsAndBobs.getTitledBorder("L1"));
			
			JPanel l2Panel = new JPanel();
			l2Panel.setLayout(new MigLayout("insets 0"));
			l2Panel.setBorder(GUIBitsAndBobs.getTitledBorder("L2"));

			
			d1Panel = new LabelledDoublePanel("d1 separation between P11 and P12");
			d1Panel.setNumber(d1);
			l1Panel.add(d1Panel, "span");
			
			d2Panel = new LabelledDoublePanel("d2 separation between P21 and P22");
			d2Panel.setNumber(d2);
			l2Panel.add(d2Panel, "span");
			
			deltaTheta1Panel= new LabelledDoublePanel("\u0394 \u03B81");
			deltaTheta1Panel.setNumber(deltaTheta1);
			l1Panel.add(deltaTheta1Panel, "span");
			
			deltaTheta2Panel= new LabelledDoublePanel("\u0394 \u03B82");
			deltaTheta2Panel.setNumber(deltaTheta2);
			l2Panel.add(deltaTheta2Panel, "span");
			
			commonCorner1Panel = new LabelledVector3DPanel("Common corner");
			commonCorner1Panel.setVector3D(commonCorner1);
			l1Panel.add(commonCorner1Panel, "span");
			
			commonCorner2Panel = new LabelledVector3DPanel("Common corner");
			commonCorner2Panel.setVector3D(commonCorner2);
			l2Panel.add(commonCorner2Panel, "span");
			
			extraShiftPanel = new DoublePanel[extraShift.length];
			for(int i =0; i<extraShift.length/2;i++) {
				
				extraShiftPanel[i] = new DoublePanel();
				extraShiftPanel[i].setNumber(extraShift[i]);
				extraShiftPanel[i].setToolTipText("Current principal point at"+calculateWignerPrincipalPoints(
						d1, d2, MyMath.deg2rad(deltaTheta1), MyMath.deg2rad(deltaTheta2), height, height, commonCorner1, commonCorner2)[i].getSumWith(new Vector3D(extraShift[i],0,0)));
				if(i==(int)extraShift.length/2-1) {
					l1Panel.add(GUIBitsAndBobs.makeRow("\u0394x p"+(i+1), extraShiftPanel[i]), "span");
				}else {
					l1Panel.add(GUIBitsAndBobs.makeRow("\u0394x p"+(i+1), extraShiftPanel[i]));
				}
			
			};
			
			for(int i =extraShift.length/2; i<extraShift.length;i++) {
				
				extraShiftPanel[i] = new DoublePanel();
				extraShiftPanel[i].setNumber(extraShift[i]);
				extraShiftPanel[i].setToolTipText("Current principal point at"+calculateWignerPrincipalPoints(
						d1, d2, MyMath.deg2rad(deltaTheta1), MyMath.deg2rad(deltaTheta2), height, height, commonCorner1, commonCorner2)[i].getSumWith(new Vector3D(0,extraShift[i],0)));// principalPoint,

				if(i==(int)extraShift.length-1) {
					l2Panel.add(GUIBitsAndBobs.makeRow("\u0394y p"+(i-2), extraShiftPanel[i]),"span");
				}else {
					l2Panel.add(GUIBitsAndBobs.makeRow("\u0394y p"+(i-2), extraShiftPanel[i]));
				}
			
			};
			
			showLensesL1CheckBox = new JCheckBox("Show L1");
			showLensesL1CheckBox.setSelected(showLensesL1);
			l1Panel.add(showLensesL1CheckBox, "span");
			
			showLensesL2CheckBox = new JCheckBox("Show L2");
			showLensesL2CheckBox.setSelected(showLensesL2);
			l2Panel.add(showLensesL2CheckBox, "span");
			
			
			customLensPartsPanel.add(l1Panel, "span");
			customLensPartsPanel.add(l2Panel, "span");
			

			break;
		case GALILEI:
		case LORENTZ:
		case ROTATION:
			default:
			d1Panel = new LabelledDoublePanel("d separation between P1 and P2");
			d1Panel.setNumber(d1);
			customLensPartsPanel.add(d1Panel, "span");
			
			deltaTheta1Panel= new LabelledDoublePanel("\u0394 \u03B8");
			deltaTheta1Panel.setNumber(deltaTheta1);
			customLensPartsPanel.add(deltaTheta1Panel, "span");
			
			phi1Panel = new LabelledDoublePanel("\u03C6 1");
			phi1Panel.setNumber(phi1);
			customLensPartsPanel.add(phi1Panel);
			
			phi2Panel = new LabelledDoublePanel("and \u03C6 2");
			phi2Panel.setNumber(phi2);
			customLensPartsPanel.add(phi2Panel, "span");
			
			commonCorner1Panel = new LabelledVector3DPanel("Common corner");
			commonCorner1Panel.setVector3D(commonCorner1);
			customLensPartsPanel.add(commonCorner1Panel, "span");
			
			break;

		}
		
		customLensPartsPanel.revalidate();
	}
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		super.actionPerformed(e);
		
		if(e.getSource().equals(mappingTypeComboBox))
		{
			
			mappingType = (MappingType)(mappingTypeComboBox.getSelectedItem());
			
			updateMappingPanel();
		}
	}
	
	public static void main(final String[] args)
	{
		(new ThreeSkewLensRelativisticVisualiser()).run();
	}
}
