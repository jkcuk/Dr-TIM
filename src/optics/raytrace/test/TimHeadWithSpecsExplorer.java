package optics.raytrace.test;

import java.awt.event.ActionEvent;
import java.io.PrintStream;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

import math.MyMath;
import math.Vector3D;
import net.miginfocom.swing.MigLayout;
import optics.raytrace.NonInteractiveTIMActionEnum;
import optics.raytrace.NonInteractiveTIMEngine;
import optics.raytrace.GUI.lowLevel.ApertureSizeType;
import optics.raytrace.GUI.lowLevel.DoublePanel;
import optics.raytrace.GUI.lowLevel.GUIBitsAndBobs;
import optics.raytrace.GUI.lowLevel.IntPanel;
import optics.raytrace.GUI.lowLevel.LabelledDoublePanel;
import optics.raytrace.GUI.lowLevel.LabelledVector3DPanel;
import optics.raytrace.GUI.lowLevel.Vector3DPanel;
import optics.raytrace.GUI.sceneObjects.EditableScaledParametrisedDisc;
import optics.raytrace.core.LightSource;
import optics.raytrace.core.Studio;
import optics.raytrace.core.StudioInitialisationType;
import optics.raytrace.exceptions.SceneException;
import optics.raytrace.research.viewRotation.RefractiveViewRotator;
import optics.raytrace.sceneObjects.ParametrisedDisc.DiscParametrisationType;
import optics.raytrace.sceneObjects.Plane;
import optics.raytrace.sceneObjects.TimHead;
import optics.raytrace.sceneObjects.TimHeadWithSpecs;
import optics.raytrace.sceneObjects.TimHeadWithSpecs.FrameType;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.surfaces.PhaseHologram;
import optics.raytrace.surfaces.PhaseHologramOfCylindricalLensSpiral;
import optics.raytrace.surfaces.PhaseHologramOfCylindricalLensSpiral.CylindricalLensSpiralType;
import optics.raytrace.surfaces.SurfaceOfRefractiveViewRotator.DerivativeControlType;
import optics.raytrace.surfaces.SurfaceColour;
import optics.raytrace.surfaces.Transparent;



/**
 * Looking at a Tim head with different specs. These can be extended to include various spectacles.
 *  
 * @author Maik
 */
public class TimHeadWithSpecsExplorer extends NonInteractiveTIMEngine
{	
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
	private double thickness;
	private FrameType frameType;
	private SurfaceColour frameColour;

	/**
	 * camera rotation
	 */
	private double cameraRotation;

	/*
	 * The frame colour 
	 */
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
	
	private enum SpecType
	{	NOTHING("Nothing"),
		ROTATION("View rotator"),
		SPIRAL_LENS("Spiral Specs"),
		SHADES("Shades");

		//private SurfaceColour colour;
		private String description;
		private SpecType(String description) {this.description = description;}	
		//private SomeColours(String description, SurfaceColour colour) {this.description = description; this.colour=colour;}	
		@Override
		public String toString() {return description;}
	}

	private SpecType specType;
	
	//
	//Spectacle parameters
	//
	/*
	 * Shades params
	 */
	private double shadeFactor;
	
	/*
	 * 	Rotator	
	 */
	private double rotationAngle;
	private Vector3D refractiveLatticeSpanVector1, refractiveLatticeSpanVector2;
	private double wedgeThickness;
	private boolean diffractuveBlurRefractiveFresnelWedge;
	private double surfaceTransmissionCoefficient;
	/*
	 * spiral lenses
	 */
	private double spiralLensSeparation;
	private CylindricalLensSpiralType cylindricalLensSpiralType;
	private boolean windingFocussing;
	private double b;
	private double f;
	private double rotationAngleDeg;
	private boolean showLens1;
	private boolean showLens2;
	
	/**
	 * Determine how to initialise the backdrop
	 */
	private StudioInitialisationType studioInitialisation;



	public TimHeadWithSpecsExplorer()
	{
		super();

		/**
		 * Setting the paramterers for tims head
		 */
		centre = new Vector3D(0,0,0);
		frontDirection = Vector3D.Z.getProductWith(-1);
		topDirection = Vector3D.Y;
		rightDirection = Vector3D.X.getProductWith(-1);
		radius = 0.08*M; //at this radius the eye is approximately of radius 1.25 cm.
		thickness = 0.5*CM;
		frameType = FrameType.NOTHING;
		colour = SomeColours.DARK_BLUE_MATT;
		frameColour = SurfaceColour.DARK_BLUE_MATT;
		
		/*
		 * Specs set up
		 */
		specType = SpecType.NOTHING;
		
		//shade params
		shadeFactor = 0.5;
		
		//rotator
		rotationAngle = 10;
		refractiveLatticeSpanVector1 = new Vector3D(0.5,0,0).getProductWith(MM);
		refractiveLatticeSpanVector2 = new Vector3D(0,0.5,0).getProductWith(MM);
		diffractuveBlurRefractiveFresnelWedge = false;
		wedgeThickness = 1*MM;
		surfaceTransmissionCoefficient = 1;
		cameraMaxTraceLevel = 200;
		
		//spiral lenses
		spiralLensSeparation = 0.05*MM;
		cylindricalLensSpiralType = CylindricalLensSpiralType.LOGARITHMIC;
		b = 0.01;
		f = 0.1*CM;
		rotationAngleDeg = 30;
		windingFocussing = true;
		showLens1 = true;
		showLens2 = true;
		
		// camera params
		cameraRotation = 0;
		cameraDistance = 1*M;
		cameraViewCentre = new Vector3D(0, 0, 0);
		cameraViewDirection = new Vector3D(0, 0, 1);
		cameraTopDirection = new Vector3D(0,1,0);
		cameraHorizontalFOVDeg = 12;
		cameraApertureSize = ApertureSizeType.PINHOLE;
		cameraFocussingDistance = 5*M;
		
		studioInitialisation = StudioInitialisationType.MINIMALIST;	// the backdrop

		if(nonInteractiveTIMAction == NonInteractiveTIMActionEnum.INTERACTIVE)
		{
			windowTitle = "Dr TIM's Spectacular explorer";
			windowWidth = 1500;
			windowHeight = 650;
		}

	}
	
	@Override
	public String getClassName()
	{
		return "TimHeadWithSpecsExplorer"
				;
	}
	
	@Override
	public void writeParameters(PrintStream printStream)
	{
		//tim head params
		printStream.println("centre = "+centre);
		printStream.println("frontDirection = "+frontDirection);
		printStream.println("topDirection = "+topDirection);
		printStream.println("rightDirection = "+rightDirection);
		printStream.println("radius = "+radius+"m");
		printStream.println("thickness = "+thickness+" cm");
		printStream.println("frameType = "+frameType);
		printStream.println("frameColour = "+frameColour);
		
		printStream.println();
		printStream.println("specType ="+specType.toString());
		switch(specType) {
		case NOTHING:
		default:
			break;
		case ROTATION:
			printStream.println("rotationAngle = "+rotationAngle+" ");
			printStream.println("refractiveLatticeSpanVector1 = "+refractiveLatticeSpanVector1+" mm");
			printStream.println("refractiveLatticeSpanVector2 = "+refractiveLatticeSpanVector2+" mm");
			printStream.println("diffractuveBlurRefractiveFresnelWedge = "+diffractuveBlurRefractiveFresnelWedge);
			printStream.println("wedgeThickness = "+wedgeThickness+" mm");
			printStream.println("surfaceTransmissionCoefficient = "+surfaceTransmissionCoefficient);
			break;
		case SPIRAL_LENS:
			printStream.println("cylindricalLensSpiralType = "+cylindricalLensSpiralType);
			printStream.println("spiralLensSeparation = "+spiralLensSeparation);
			printStream.println("b = "+b);
			printStream.println("f = "+f);
			printStream.println("rotationAngleDeg = "+rotationAngleDeg);
			printStream.println("windingFocussing="+windingFocussing);
			printStream.println("showLens1 = "+showLens1);
			printStream.println("showLens2 = "+showLens2);
			break;
		case SHADES:
			printStream.println("shadeFactor = "+shadeFactor);
			break;
		}
		//spec params...
		
		
		printStream.println();
		// rest of scene
		printStream.println("studioInitialisation = "+studioInitialisation);
		printStream.println();
		//camera params
		printStream.println("cameraRotation = "+cameraRotation);
		printStream.println("cameraViewDirection = "+cameraViewDirection);
		printStream.println("cameraHorizontalFOVDeg = "+cameraHorizontalFOVDeg);
		printStream.println("camerFocusingDistance = "+cameraFocussingDistance+"m");
		// write all parameters defined in NonInteractiveTIMEngine
		super.writeParameters(printStream);		
	}

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
		
		SceneObjectContainer leftSpecCont = new SceneObjectContainer(
				"left",	// description
				scene,	// parent
				studio
			);

		SceneObjectContainer rightSpecCont = new SceneObjectContainer(
				"right",	// description
				scene,	// parent
				studio
			);
		
		TimHead tim = new TimHead(
				"Tim head for calculation", 
				centre, 
				radius, 
				frontDirection, 
				topDirection, 
				rightDirection, 
				null, 
				null
				);
		//Tim head with specs
		timHead = new TimHeadWithSpecs(
				"new test subject" , 
				centre,
				radius,
				thickness,
				frontDirection, 
				topDirection, 
				rightDirection, 
				frameType,
				frameColour,
				leftSpecCont,
				rightSpecCont,
				scene, 
				studio);
		
		
		switch(specType){
		case NOTHING:
		default:
			break;
		case ROTATION:
			leftSpecCont.addSceneObject( new RefractiveViewRotator(
					"refractive view rotator left eye",// description,
					TimHeadWithSpecs.getLeftSpecCentre(tim),
					//tim.getRightEye().getCentre(),
					timHead.getRightDirection().getProductWith(2.2*timHead.getMaxSpecRadius(radius)), 
					timHead.getTopDirection().getProductWith(2.2*timHead.getMaxSpecRadius(radius)),
					timHead.getFrontDirection().getProductWith(0.92*thickness),
					timHead.getFrontDirection(),// ocularSurfaceNormal,
					Vector3D.sum(tim.getFrontDirection().getProductWith(tim.getLeftEye().getRadius()),tim.getLeftEye().getCentre()),
					Vector3D.sum(TimHeadWithSpecs.getLeftSpecCentre(tim),tim.getFrontDirection().getProductWith(-0.45*thickness)), //centre
					rotationAngle,
					new Vector3D(0,0,-1), //rotation axis direction
					1,// magnificationFactor,
					refractiveLatticeSpanVector1,
					refractiveLatticeSpanVector2,
					new Plane("focus plane", 
							Vector3D.sum(timHead.getCentre(),timHead.getFrontDirection().getProductWith(1)),
							timHead.getFrontDirection(), 
							null,
							null, 
							null
							),
					1.5,
					wedgeThickness,
					surfaceTransmissionCoefficient, //trasnmission coef
					diffractuveBlurRefractiveFresnelWedge,
					cameraMaxTraceLevel,
					scene, 
					scene,
					studio
					));
			
//System.out.println("spec eye dist is="+Vector3D.getDistance(Vector3D.sum(tim.getFrontDirection().getProductWith(tim.getLeftEye().getRadius()),tim.getLeftEye().getCentre()),
//					Vector3D.sum(TimHeadWithSpecs.getLeftSpecCentre(tim),tim.getFrontDirection().getProductWith(-0.45*thickness))));
			
			rightSpecCont.addSceneObject( new RefractiveViewRotator(
					"refractive view rotator right eye",// description,
					TimHeadWithSpecs.getRightSpecCentre(tim),
					//tim.getRightEye().getCentre(),
					timHead.getRightDirection().getProductWith(2*timHead.getMaxSpecRadius(radius)), 
					timHead.getTopDirection().getProductWith(2*timHead.getMaxSpecRadius(radius)),
					timHead.getFrontDirection().getProductWith(0.98*thickness),
					timHead.getFrontDirection(),// ocularSurfaceNormal,
					Vector3D.sum(tim.getFrontDirection().getProductWith(tim.getRightEye().getRadius()),tim.getRightEye().getCentre()),
					Vector3D.sum(TimHeadWithSpecs.getRightSpecCentre(tim),tim.getFrontDirection().getProductWith(-0.49*thickness)), //centre
					-rotationAngle,
					new Vector3D(0,0,-1), //rotation axis direction
					1,// magnificationFactor,
					refractiveLatticeSpanVector1,
					refractiveLatticeSpanVector2,
					new Plane("focus plane", 
							Vector3D.sum(timHead.getCentre(),timHead.getFrontDirection().getProductWith(cameraDistance)),
							timHead.getFrontDirection(), 
							null,
							null, 
							null
							),
					1.5,
					wedgeThickness,
					surfaceTransmissionCoefficient, //trasnmission coef
					diffractuveBlurRefractiveFresnelWedge,
					cameraMaxTraceLevel,
					scene, 
					scene,
					studio
					));			
			break;
		case SPIRAL_LENS:
			Vector3D spiralLens1LeftCentre = Vector3D.sum(TimHeadWithSpecs.getLeftSpecCentre(tim),tim.getFrontDirection().getProductWith(-spiralLensSeparation/2));
			Vector3D spiralLens2LeftCentre = Vector3D.sum(TimHeadWithSpecs.getLeftSpecCentre(tim),tim.getFrontDirection().getProductWith(spiralLensSeparation/2));
			EditableScaledParametrisedDisc spiralLens1Left = new EditableScaledParametrisedDisc(
					"Left spiral-lens 1",	// description
					spiralLens1LeftCentre,
					tim.getFrontDirection(),	// normal
					timHead.getMaxSpecRadius(radius),	// radius
					new Vector3D(1, 0, 0),	// phi0Direction
					DiscParametrisationType.CARTESIAN,	// parametrisationType
					-timHead.getMaxSpecRadius(radius), timHead.getMaxSpecRadius(radius),	// suMin, suMax
					-timHead.getMaxSpecRadius(radius), timHead.getMaxSpecRadius(radius),	// svMin, svMax
					null,	// surface property
					scene,	// parent
					studio
					);
			PhaseHologram leftHologram1 = new PhaseHologramOfCylindricalLensSpiral(
						cylindricalLensSpiralType,
						f,	// focalLength
						0,	// deltaPhi
						b,
						spiralLens1Left,	// sceneObject
						0.96,	// throughputCoefficient
						windingFocussing,	// alvarezWindingFocusing
						false,	// reflective
						false	// shadowThrowing
						);
		
			spiralLens1Left.setSurfaceProperty(leftHologram1);
			leftSpecCont.addSceneObject(spiralLens1Left, showLens1);
		
				EditableScaledParametrisedDisc spiralLens2Left = new EditableScaledParametrisedDisc(
						"Left spiral-lens 2",	// description
						spiralLens2LeftCentre,
						tim.getFrontDirection(),	// normal
						timHead.getMaxSpecRadius(radius),	// radius
						new Vector3D(1, 0, 0),	// phi0Direction
						DiscParametrisationType.CARTESIAN,	// parametrisationType
						-timHead.getMaxSpecRadius(radius), timHead.getMaxSpecRadius(radius),	// suMin, suMax
						-timHead.getMaxSpecRadius(radius), timHead.getMaxSpecRadius(radius),	// svMin, svMax
						null,	// surface property
						scene,	// parent
						studio
						);
				PhaseHologramOfCylindricalLensSpiral leftHologram2 = new PhaseHologramOfCylindricalLensSpiral(
						cylindricalLensSpiralType,
						-f,	// focalLength
						MyMath.deg2rad(rotationAngleDeg),	// deltaPhi
						b,
						spiralLens2Left,	// sceneObject
						0.96,	// throughputCoefficient
						windingFocussing,	// alvarezWindingFocusing
						false,	// reflective
						false	// shadowThrowing
						);
				spiralLens2Left.setSurfaceProperty(leftHologram2);
				leftSpecCont.addSceneObject(spiralLens2Left, showLens2);
				
				
				Vector3D spiralLens1RightCentre = Vector3D.sum(TimHeadWithSpecs.getRightSpecCentre(tim),tim.getFrontDirection().getProductWith(-spiralLensSeparation/2));
				Vector3D spiralLens2RightCentre = Vector3D.sum(TimHeadWithSpecs.getRightSpecCentre(tim),tim.getFrontDirection().getProductWith(spiralLensSeparation/2));
				EditableScaledParametrisedDisc spiralLens1Right = new EditableScaledParametrisedDisc(
						"Right spiral-lens 1",	// description
						spiralLens1RightCentre,
						tim.getFrontDirection(),	// normal
						timHead.getMaxSpecRadius(radius),	// radius
						new Vector3D(1, 0, 0),	// phi0Direction
						DiscParametrisationType.CARTESIAN,	// parametrisationType
						-timHead.getMaxSpecRadius(radius), timHead.getMaxSpecRadius(radius),	// suMin, suMax
						-timHead.getMaxSpecRadius(radius), timHead.getMaxSpecRadius(radius),	// svMin, svMax
						null,	// surface property
						scene,	// parent
						studio
						);
				PhaseHologram rightHologram1 = new PhaseHologramOfCylindricalLensSpiral(
							cylindricalLensSpiralType,
							f,	// focalLength
							0,	// deltaPhi
							b,
							spiralLens1Right,	// sceneObject
							0.96,	// throughputCoefficient
							windingFocussing,	// alvarezWindingFocusing
							false,	// reflective
							false	// shadowThrowing
							);
			
				spiralLens1Right.setSurfaceProperty(rightHologram1);
				rightSpecCont.addSceneObject(spiralLens1Right, showLens1);

			
					EditableScaledParametrisedDisc spiralLens2Right = new EditableScaledParametrisedDisc(
							"Right spiral-lens 2",	// description
							spiralLens2RightCentre,
							tim.getFrontDirection(),	// normal
							timHead.getMaxSpecRadius(radius),	// radius
							new Vector3D(1, 0, 0),	// phi0Direction
							DiscParametrisationType.CARTESIAN,	// parametrisationType
							-timHead.getMaxSpecRadius(radius), timHead.getMaxSpecRadius(radius),	// suMin, suMax
							-timHead.getMaxSpecRadius(radius), timHead.getMaxSpecRadius(radius),	// svMin, svMax
							null,	// surface property
							scene,	// parent
							studio
							);
					PhaseHologramOfCylindricalLensSpiral rightHologram2 = new PhaseHologramOfCylindricalLensSpiral(
							cylindricalLensSpiralType,
							-f,	// focalLength
							MyMath.deg2rad(rotationAngleDeg),	// deltaPhi
							b,
							spiralLens2Right,	// sceneObject
							0.96,	// throughputCoefficient
							windingFocussing,	// alvarezWindingFocusing
							false,	// reflective
							false	// shadowThrowing
							);
					spiralLens2Right.setSurfaceProperty(rightHologram2);
					rightSpecCont.addSceneObject(spiralLens2Right, showLens2);

			break;
		case SHADES:
			leftSpecCont.addSceneObject(new Plane(
					"focus plane", 
					TimHeadWithSpecs.getLeftSpecCentre(tim),
					frontDirection, 
							new Transparent(shadeFactor, false),
							scene, 
							studio	
					)
			);
			
			rightSpecCont.addSceneObject(new Plane(
					"focus plane", 
					TimHeadWithSpecs.getRightSpecCentre(tim),
					frontDirection, 
							new Transparent(shadeFactor, false),
							scene, 
							studio	
					)
			);
			break;
		}
		
		//adding the final head with specs
		scene.addSceneObject(timHead);


		// the camera

		cameraTopDirection = new Vector3D(Math.sin(Math.toRadians(cameraRotation)),Math.cos(Math.toRadians(cameraRotation)),0);
		studio.setCamera(getStandardCamera());		
		studio.setScene(scene);	
		studio.setLights(LightSource.getStandardLightsFromBehind());

	}
	
	JTabbedPane specsTab;
	
	//Tim Stuff
	private LabelledVector3DPanel centrePanel, frontDirectionPanel, topDirectionPanel, rightDirectionPanel;
	private DoublePanel radiusPanel, thicknessPanel;
	private JComboBox<FrameType> frameTypeComboBox;
	private JComboBox<SomeColours>colourComboBox;
	
	//spec stuff
	private JButton getMaxSpecRadiusButton;
	private JTextArea specRadiusTextArea;
	//shades
	private DoublePanel shadeFactorPanel;
	//rotator
	private DoublePanel rotationAnglePanel, wedgeThicknessPanel, surfaceTransmissionCoefficientPanel;
	private Vector3DPanel refractiveLatticeSpanVector1Panel, refractiveLatticeSpanVector2Panel;
	private JCheckBox diffractuveBlurRefractiveFresnelWedgeCheckBox;
	//spirals
	private JComboBox<CylindricalLensSpiralType> cylindricalLensSpiralTypeComboBox;
	private LabelledDoublePanel bPanel;
	private DoublePanel spiralLensSeparationPanel, fPanel;
	private DoublePanel rotationAngleDegPanel;
	private JCheckBox showLens1CheckBox, showLens2CheckBox, windingFocussingCheckBox;

	// camera stuff
	private DoublePanel cameraDistancePanel, cameraRotationPanel;
	private LabelledVector3DPanel cameraViewDirectionPanel;
	private DoublePanel cameraHorizontalFOVDegPanel;
	private JComboBox<ApertureSizeType> cameraApertureSizeComboBox;
	private DoublePanel cameraFocussingDistancePanel;
	private IntPanel cameraMaxTraceLevelPanel;


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
		sceneCameraTabbedPane.addTab("Tim head", scenePanel);

		radiusPanel = new DoublePanel();
		radiusPanel.setNumber(radius/M);
		scenePanel.add(GUIBitsAndBobs.makeRow("Head radius",radiusPanel," m"),"span");

		specRadiusTextArea = new JTextArea(2, 25);
		JScrollPane scrollPane = new JScrollPane(specRadiusTextArea); 
		specRadiusTextArea.setEditable(false);
		specRadiusTextArea.setText("Maximum spec radius");
		getMaxSpecRadiusButton = new JButton("Spec radius");
		getMaxSpecRadiusButton.addActionListener(this);
		scenePanel.add(GUIBitsAndBobs.makeRow(scrollPane, getMaxSpecRadiusButton), "span");

		centrePanel = new LabelledVector3DPanel("Head centre");
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

		
		//
		// The spec panel
		//
		
		JPanel specPanel = new JPanel();
		specPanel.setLayout(new MigLayout("insets 0"));
		sceneCameraTabbedPane.addTab("Specs", specPanel);
	
		thicknessPanel= new DoublePanel();
		thicknessPanel.setNumber(thickness/CM);
		specPanel.add(GUIBitsAndBobs.makeRow("Specs thickness",thicknessPanel, "cm" ),"span");
		
		frameTypeComboBox = new JComboBox<FrameType>(FrameType.values());
		frameTypeComboBox.setSelectedItem(frameType);
		specPanel.add(GUIBitsAndBobs.makeRow("Frame type", frameTypeComboBox), "span");	

		colourComboBox = new JComboBox<SomeColours>(SomeColours.values());
		colourComboBox.setSelectedItem(colour);
		specPanel.add(GUIBitsAndBobs.makeRow("Frame colour", colourComboBox), "span");
		
		//The specs tabbs
		specsTab = new JTabbedPane();
		specPanel.add(specsTab, "span");
		
		//Nothing tab
		JPanel nothingPanel = new JPanel();
		nothingPanel.setLayout(new MigLayout("insets 0"));
		specsTab.addTab("nothing", nothingPanel);
		
		//view rotator tab
		JPanel rotatorPanel = new JPanel();
		rotatorPanel.setLayout(new MigLayout("insets 0"));
		specsTab.addTab("Rotator", rotatorPanel);
		
		rotationAnglePanel = new DoublePanel();
		rotationAnglePanel.setNumber(rotationAngle);
		rotatorPanel.add(GUIBitsAndBobs.makeRow("Rotation angle", rotationAnglePanel, "<html>&deg;</html>,"),"wrap");

		refractiveLatticeSpanVector1Panel = new Vector3DPanel();
		refractiveLatticeSpanVector1Panel.setVector3D(refractiveLatticeSpanVector1.getProductWith(1/MM));
		rotatorPanel.add(GUIBitsAndBobs.makeRow("pixel span vector 1", refractiveLatticeSpanVector1Panel, "mm"),"wrap");

		refractiveLatticeSpanVector2Panel = new Vector3DPanel();
		refractiveLatticeSpanVector2Panel.setVector3D(refractiveLatticeSpanVector2.getProductWith(1/MM));
		rotatorPanel.add(GUIBitsAndBobs.makeRow("pixel span vector 2", refractiveLatticeSpanVector2Panel, "mm"),"wrap");

		wedgeThicknessPanel = new DoublePanel();
		wedgeThicknessPanel.setNumber(wedgeThickness/MM);
		rotatorPanel.add(GUIBitsAndBobs.makeRow("wedge thickness", wedgeThicknessPanel, "mm"),"wrap");

		diffractuveBlurRefractiveFresnelWedgeCheckBox = new JCheckBox();
		diffractuveBlurRefractiveFresnelWedgeCheckBox.setSelected(diffractuveBlurRefractiveFresnelWedge);
		rotatorPanel.add(GUIBitsAndBobs.makeRow("show diffraction", diffractuveBlurRefractiveFresnelWedgeCheckBox),"wrap");

		surfaceTransmissionCoefficientPanel = new DoublePanel();
		surfaceTransmissionCoefficientPanel.setNumber(surfaceTransmissionCoefficient);
		rotatorPanel.add(GUIBitsAndBobs.makeRow("set trasnmission coefficient to", surfaceTransmissionCoefficientPanel),"wrap");
		
		//spiral lens tab
		JPanel spiralPanel = new JPanel();
		spiralPanel.setLayout(new MigLayout("insets 0"));
		specsTab.addTab("Spiral", spiralPanel);

		cylindricalLensSpiralTypeComboBox = new JComboBox<CylindricalLensSpiralType>(CylindricalLensSpiralType.values());
		cylindricalLensSpiralTypeComboBox.setSelectedItem(cylindricalLensSpiralType);
		spiralPanel.add(GUIBitsAndBobs.makeRow("Spiral type", cylindricalLensSpiralTypeComboBox), "span");
		
		spiralLensSeparationPanel = new DoublePanel();
		spiralLensSeparationPanel.setNumber(spiralLensSeparation/MM);
		spiralPanel.add(GUIBitsAndBobs.makeRow("Spiral separation", spiralLensSeparationPanel, "mm"), "span");
		
		bPanel = new LabelledDoublePanel("b (winding) parameter of spiral");
		bPanel.setNumber(b);
		spiralPanel.add(bPanel, "span");

		fPanel = new DoublePanel();
		fPanel.setNumber(f/CM);
		spiralPanel.add(GUIBitsAndBobs.makeRow("f of cylindrical lens (at r=1 or \u03C6=1)", fPanel, " cm"), "span");

		rotationAngleDegPanel = new DoublePanel();
		rotationAngleDegPanel.setNumber(rotationAngleDeg);
		spiralPanel.add(GUIBitsAndBobs.makeRow("Rotation angle between parts", rotationAngleDegPanel, "°"), "span");

		windingFocussingCheckBox = new JCheckBox();
		windingFocussingCheckBox.setSelected(windingFocussing);
		spiralPanel.add(GUIBitsAndBobs.makeRow("Winding focusing", windingFocussingCheckBox),"wrap");

		showLens1CheckBox = new JCheckBox("Show part 1");
		showLens1CheckBox.setSelected(showLens1);
		spiralPanel.add(showLens1CheckBox, "span");

		showLens2CheckBox = new JCheckBox("Show part 2");
		showLens2CheckBox.setSelected(showLens2);
		spiralPanel.add(showLens2CheckBox, "span");
		
		//shades tab
		JPanel shadePanel = new JPanel();
		shadePanel.setLayout(new MigLayout("insets 0"));
		specsTab.addTab("shades", shadePanel);
		
		shadeFactorPanel= new DoublePanel();
		shadeFactorPanel.setNumber(shadeFactor);
		shadePanel.add(GUIBitsAndBobs.makeRow("Shade factor (0 to 1)",shadeFactorPanel ),"span");
		
		//
		// the Camera panel
		//

		JPanel cameraPanel = new JPanel();
		cameraPanel.setLayout(new MigLayout("insets 0"));
		sceneCameraTabbedPane.addTab("Camera", cameraPanel);


		cameraDistancePanel = new DoublePanel();
		cameraDistancePanel.setNumber(cameraDistance/M);
		cameraPanel.add(GUIBitsAndBobs.makeRow("Camera distance",cameraDistancePanel,"m" ),"span");

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
		cameraFocussingDistancePanel.setNumber(cameraFocussingDistance/M);
		cameraPanel.add(GUIBitsAndBobs.makeRow("Focussing distance",cameraFocussingDistancePanel,"m"),"wrap");
		
		cameraMaxTraceLevelPanel = new IntPanel();
		cameraMaxTraceLevelPanel.setNumber(cameraMaxTraceLevel);
		cameraPanel.add(GUIBitsAndBobs.makeRow("trace level", cameraMaxTraceLevelPanel),"wrap");



	}
	
	

	protected void acceptValuesInInteractiveControlPanel()
	{
		
		/**
		 * called before rendering;
		 * override when adding fields
		 */
		
			super.acceptValuesInInteractiveControlPanel();

			switch(specsTab.getSelectedIndex())
			{
			case 0:
				specType = SpecType.NOTHING;
				break;
			case 1:
				specType = SpecType.ROTATION;
				rotationAngle = rotationAnglePanel.getNumber();
				refractiveLatticeSpanVector1 = refractiveLatticeSpanVector1Panel.getVector3D().getProductWith(MM);
				refractiveLatticeSpanVector2 = refractiveLatticeSpanVector2Panel.getVector3D().getProductWith(MM);
				wedgeThickness = wedgeThicknessPanel.getNumber()*MM;
				diffractuveBlurRefractiveFresnelWedge = diffractuveBlurRefractiveFresnelWedgeCheckBox.isSelected();
				surfaceTransmissionCoefficient = surfaceTransmissionCoefficientPanel.getNumber();
				break;
			case 2:
				specType = SpecType.SPIRAL_LENS;
				spiralLensSeparation = spiralLensSeparationPanel.getNumber()*MM;
				cylindricalLensSpiralType = (CylindricalLensSpiralType)(cylindricalLensSpiralTypeComboBox.getSelectedItem());
				b = bPanel.getNumber();
				f = fPanel.getNumber()*CM;
				rotationAngleDeg = rotationAngleDegPanel.getNumber();
				showLens1 = showLens1CheckBox.isSelected();
				showLens2 = showLens2CheckBox.isSelected();
				windingFocussing = windingFocussingCheckBox.isSelected();
				break;
			case 3:
				specType = SpecType.SHADES;
				shadeFactor = shadeFactorPanel.getNumber();
			}
			
			
		//head stuff
		centre = centrePanel.getVector3D();
		frontDirection = frontDirectionPanel.getVector3D();
		topDirection = topDirectionPanel.getVector3D();
		rightDirection = rightDirectionPanel.getVector3D();
		radius = radiusPanel.getNumber()*M;
		thickness = thicknessPanel.getNumber()*CM;
		frameType = (FrameType)(frameTypeComboBox.getSelectedItem());
		colour = (SomeColours)(colourComboBox.getSelectedItem());

		//Camera and general stuff
		cameraDistance = cameraDistancePanel.getNumber()*M;
		cameraRotation = cameraRotationPanel.getNumber();
		cameraHorizontalFOVDeg = cameraHorizontalFOVDegPanel.getNumber();
		cameraViewDirection = cameraViewDirectionPanel.getVector3D();
		cameraApertureSize = (ApertureSizeType)(cameraApertureSizeComboBox.getSelectedItem());
		cameraFocussingDistance = cameraFocussingDistancePanel.getNumber()*M;	
		cameraMaxTraceLevel = cameraMaxTraceLevelPanel.getNumber();
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{	
		if(e.getSource().equals(getMaxSpecRadiusButton))
		{
			acceptValuesInInteractiveControlPanel();
			specRadiusTextArea.setText("Maximum spec radius is "+timHead.getMaxSpecRadius(radius)/CM+"cm");
		} //TODO does not update frame type... so only executes after render.
		super.actionPerformed(e);
	}
		
		
	public static void main(final String[] args)
	{
		(new TimHeadWithSpecsExplorer()).run();
	}
}

