package optics.raytrace.research.viewRotation;

import java.awt.BorderLayout;
import java.io.PrintStream;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import math.*;
import net.miginfocom.swing.MigLayout;
import optics.raytrace.sceneObjects.Plane;
import optics.raytrace.sceneObjects.SnellenChart;
import optics.raytrace.sceneObjects.SnellenChart.ChartType;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.surfaces.AzimuthalPixelatedFresnelWedge;
import optics.raytrace.surfaces.IdealThinCylindricalLensSurfaceSimple;
import optics.raytrace.surfaces.IdealisedDovePrismArray;
import optics.raytrace.surfaces.PhaseHologramOfCylindricalLens;
import optics.raytrace.surfaces.PhaseHologramOfRadialLenticularArray;
import optics.raytrace.surfaces.PhaseHologramOfRectangularLensletArray;
import optics.raytrace.surfaces.RotationallySymmetricPhaseHologram;
import optics.raytrace.surfaces.SurfaceOfRefractiveViewRotator.DerivativeControlType;
import testImages.TestImage;
import optics.raytrace.exceptions.SceneException;
import optics.raytrace.NonInteractiveTIMActionEnum;
import optics.raytrace.NonInteractiveTIMEngine;
import optics.raytrace.GUI.cameras.RenderQualityEnum;
import optics.raytrace.GUI.lowLevel.ApertureSizeType;
import optics.raytrace.GUI.lowLevel.DoublePanel;
import optics.raytrace.GUI.lowLevel.GUIBitsAndBobs;
import optics.raytrace.GUI.lowLevel.IntPanel;
import optics.raytrace.GUI.lowLevel.LabelledVector3DPanel;
import optics.raytrace.GUI.lowLevel.Vector3DPanel;
import optics.raytrace.GUI.sceneObjects.EditableCylinderLattice;
import optics.raytrace.GUI.sceneObjects.EditableScaledParametrisedDisc;
import optics.raytrace.cameras.AnyFocusSurfaceCamera;
import optics.raytrace.core.Studio;
import optics.raytrace.core.StudioInitialisationType;
import optics.raytrace.core.SurfaceProperty;
import optics.raytrace.core.SurfacePropertyPrimitive;


/**
 * Simulate the visual appearance of various view-rotating components, and the view through them.
 * 
 * @author Johannes Courtial
 */
public class ViewRotationExplorerWithUnits extends NonInteractiveTIMEngine
{
	private static final long serialVersionUID = -4854046178671066946L;
	// units
	public static double NM = 1*1e-9;
	public static double UM = 1*1e-6;
	public static double MM = 1*1e-3;
	public static double CM = 1*1e-2;
	public static double M = 1*1;


	public enum ViewRotationComponentType
	{
		REFRACTIVE_PIXELATED_FRESNEL_WEDGE("refractive pixelated fresnel wedge"),
		AZIMUTHAL_FRESNEL_WEDGE("Azimuthal Fresnel wedge"),
		PIXELATED_AZIMUTHAL_FRESNEL_WEDGE("Non-countinous Azimuthal Fresnel wedge"),
		MOIRE_ROTATOR("Moiré rotator"),
		RADIAL_LAS("Complementary radial lenticular arrays"),
		RR_SHEET("Ray-rotation sheet"),
		CYLINDRICAL_LENS_TELESCOPES("Pair of rotated cylindrical-lens telescopes"),
		NOTHING("Nothing");

		private String description;
		private ViewRotationComponentType(String description) {this.description = description;}	
		@Override
		public String toString() {return description;}
	}

	private ViewRotationComponentType viewRotationComponentType;
	
	
	public enum ViewObjectType
	{
		LATTICE("Lattice"),
		TEST_IMAGE("Test Image"),
		SNELLEN_CHART("Snellen Chart"),
		NOTHING("Nothing");

		private String description;
		private ViewObjectType(String description) {this.description = description;}	
		@Override
		public String toString() {return description;}
	}
	

	private ViewObjectType viewObjectType;
	
	private TestImage testImage;
	
	private ChartType chartType;
	

	/**
	 * A very general object centre, where the component will sit. this does not have to be the centre of rotation. 
	 */
	private Vector3D objectCentre;
	
	//
	// azimuthal Fresnel wedge
	//

	// relevant RotationallySymmetricPhaseHologram parameters

	/**
	 * The parameter <i>b</i>, which (together with <i>c</i>) determines the part of the azimuthal phase gradient proportional to the azimuthal distance.
	 * The azimuthal phase gradient is of the form d<i>&Phi;</i>/d<i>a</i> = <i>k</i>*<i>b</i>*<i>r</i><sup><i>c</i></sup>,
	 * where <i>a</i> is the component in the azimuthal direction and <i>k</i>=(2 &pi;/&lambda;)
	 */
	private double aFwB;

	/**
	 * if true, simulate the radial phase gradient associated with the radially-varying azimuthal phase gradient
	 */
	// private boolean aFwSimulateHonestly;

	/**
	 * the number of segments; each segment covers an angle 360 degrees / <i>n</i>
	 */
	private int aFwN;
	
	/**
	 * if true, show two Fresnel wedges; if false,  show just one
	 */
	private boolean aFwShowSecondWedge;
	
	/**
	 * separation between the two Fresnel wedges, in case the second one is shown
	 */
	private double aFwSeparation;

	//
	// Additional variables for the pixelated fresnel wedge
	//

	/**
	 * the lattice span vectors of the hologram
	 */
	private Vector3D latticeSpanVector1, latticeSpanVector2;

	/**
	 * The parameter <i>b</i>, which (together with <i>c</i>) determines the part of the azimuthal phase gradient proportional to the azimuthal distance.
	 * The azimuthal phase gradient is of the form d<i>&Phi;</i>/d<i>a</i> = <i>k</i>*<i>b</i>*<i>r</i><sup><i>c</i></sup>,
	 * where <i>a</i> is the component in the azimuthal direction and <i>k</i>=(2 &pi;/&lambda;)
	 * This is all the same over one pixel.
	 */
	private double aFwBPixel;
	/**
	 * diffraction blur gets added when true
	 */
	private boolean diffractiveBlurPixelatedFresnelWedge;

	//
	//refractive fresnel wedge to view rotate
	//

	/**
	 * Span vectors of the array
	 */
	private Vector3D refractiveLatticeSpanVector1, refractiveLatticeSpanVector2;
	/**
	 * Bounding box settings
	 */
	private Vector3D boundingBoxCentre, boundingBoxSpanVector1, boundingBoxSpanVector2, boundingBoxSpanVector3;
	private double startSpanVector, stopSpanVector;
	
	/**
	 * wedge thickness and refractive index
	 */
	private double thickness, refractiveIndex; 
	/**
	 * position of the eye to allow for independent camera movement. 
	 */
	private Vector3D eyePosition;
	/**
	 * the angle of rotation anti-clockwise when positive, in degrees.
	 */
	private double rotationAngle;
	/**
	 * The plane position at which the rotation should be "ideal"
	 */
	private Vector3D designDistancePlane;
	
	/**
	 * Focus camera to "image" position automatically.
	 */
	private boolean autoFocus;
	
	/**
	 * The magnification factor
	 */
	private double magnificationFactor;

	/**
	 * Diffractive blur activated when true
	 */
	private boolean diffractuveBlurRefractiveFresnelWedge;

	/**
	 * transmission coefficient of refractive material
	 */
	private double surfaceTransmissionCoefficient;

	/**
	 * max steps/ trace level
	 */
	private int maxTrace;

	//
	// moiré rotator
	//

	/**
	 * specifies the focal lengths of the two lenslet arrays in the moiré magnifier, which have focal lengths +/- f
	 */
	private double mmF;

	/**
	 * the pitch of the two lenslet arrays
	 */
	private double mmPitch;

	/**
	 * the relative rotation angle between the two arrays
	 */
	private double mmDeltaPhiDeg;

	private boolean mmShowLA1, mmShowLA2;



	//
	// pairs of radial lenticular arrays
	//

	/**
	 * the focal length of each cylindrical lens in the radial lenticular array
	 */
	private double rLAsF;

	/**
	 * the number of cylindrical lenses that form the array; each cylindrical lens then covers an angle 360 degrees / <i>n</i>
	 */
	private int rLAsN;

	/**
	 * the relative rotation angle between the two arrays
	 */
	private double rLAsDeltaPhiDeg;

	private boolean rLAsShowLA1, rLAsShowLA2;


	//
	// ray-rotation sheet
	//

	/**
	 * the angle by which the two Dove-prism sheets are rotated w.r.t. each other, which is half the ray-rotation angle
	 */
	private double rrAngleDeg;

	/**
	 * period of the Dove-prism sheets that make up the RR sheet
	 */
	private double rrPeriod;

	// show the arrays?
	private boolean rrShowDPA1, rrShowDPA2;


	//
	// pair of rotated cylindrical-lens telescopes
	//

	/**
	 * the focal length of the cylindrical-lens telescopes
	 */
	private double cltFocalLength;

	/**
	 * the angle by which the combination of the two telescopes rotates;
	 * this angle is twice the angle by which the two telescopes are rotated relative to each other
	 */
	private double cltRotationAngleDeg;

	public enum CylindricalLensType
	{
		IDEAL("Ideal cylindrical lenses"),
		PHASE_HOLOGRAM("Phase holograms of cylindrical lenses");

		private String description;
		private CylindricalLensType(String description) {this.description = description;}	
		@Override
		public String toString() {return description;}
	}

	/**
	 * how are the cylindrical lenses realised?
	 */
	private CylindricalLensType cltCylindricalLensType;


	//
	// the rest of the scene
	//

	/**
	 * camera rotation
	 */
	private double cameraRotation;
	
	/**
	 * Should the rotated camera move along the new rotated coordinate system or along the unrotated one?
	 */
	private boolean rotateViewSystem;
	
	/**
	 * Camera diffraction 
	 */
	private boolean cameraApertureDiffraction;
	
	/**
	 * Camera aperture size to set manually
	 */
	private boolean setcameraAperture;
	private double cameraAperture;
	
	/**
	 * Camera view direction controlled by angles
	 */
	private double sideAngle, upAngle;
	/**
	 * Camera type to be used. anaglyph camera if true else standard. 
	 */
	private boolean anaglyphCamera;
	
	/**
	 * Should the camera be moved along an 'eyeball' or be stationary in one position as the view direction changes
	 */
	private boolean useEyeballCamera;
	
	/**
	 * The eye separationg in the anaglyph case
	 */
	Vector3D eyeSeparation = new Vector3D(60*MM,0,0);

	/**
	 * The centre of the component, usually at origin but changes when using the anaglyph camera
	 */
	Vector3D componentCentre;
	
	/**
	 * The parameters to add derivative control
	 */
	private DerivativeControlType derivativeControlType;
	private double derivativeControlThickness;
	private double derivativeControlRotation;
	
	/**
	 * Determines how to initialise the backdrop
	 */
	private StudioInitialisationType studioInitialisation;

	private boolean customBackground;
	private double objectRotationAngle;
	private double objectDistance;
	private double objectHeight;

	


	/**
	 * Constructor.
	 * Sets all parameters.
	 */
	public ViewRotationExplorerWithUnits()
	{
		super();

		// set to true for interactive version
		nonInteractiveTIMAction = NonInteractiveTIMActionEnum.INTERACTIVE;

		// set all parameters

		renderQuality = RenderQualityEnum.DRAFT;

		viewRotationComponentType = ViewRotationComponentType.REFRACTIVE_PIXELATED_FRESNEL_WEDGE;

		// init parameters

		// azimuthal Fresnel wedge
		aFwB = -9;
		// aFwSimulateHonestly = true;
		aFwN = 100;
		aFwShowSecondWedge = false;
		aFwSeparation = 1*MM;


		//pixelated Fresnel wedge
		aFwBPixel = -9;
		latticeSpanVector1 = new Vector3D(0.1,0,0).getProductWith(MM);
		latticeSpanVector2 = new Vector3D(0,0.1,0).getProductWith(MM);
		diffractiveBlurPixelatedFresnelWedge = true;

		//refractive pixelated fresnel wedges
		refractiveLatticeSpanVector1 = new Vector3D(0.5,0,0).getProductWith(MM);
		refractiveLatticeSpanVector2 = new Vector3D(0,0.5,0).getProductWith(MM);
		rotationAngle = 10;//degrees
		derivativeControlRotation = 10;
		designDistancePlane = new Vector3D(0,0,1*M);
		diffractuveBlurRefractiveFresnelWedge = true;
		boundingBoxCentre = new Vector3D (0,0,20).getProductWith(MM);
		boundingBoxSpanVector1 = new Vector3D(5,0,0).getProductWith(CM);
		boundingBoxSpanVector2 = new Vector3D(0,5,0).getProductWith(CM);
		boundingBoxSpanVector3 = new Vector3D(0,0,44).getProductWith(MM);
		thickness = 1*MM;
		magnificationFactor = 1;
		refractiveIndex = 1.5;//glass
		surfaceTransmissionCoefficient = 0.95;
		maxTrace = 100;
		derivativeControlType = DerivativeControlType.IDEAL_THIN_LENS;
		derivativeControlThickness = 20*MM;


		// moiré rotator
		mmF = 1*MM;
		mmPitch = 1*MM;
		mmDeltaPhiDeg = .5;
		mmShowLA1 = true;
		mmShowLA2 = true;

		// complementary radial lenticular arrays
		rLAsF = 1*MM;
		rLAsN = 100;
		rLAsDeltaPhiDeg = .5;
		rLAsShowLA1 = true;
		rLAsShowLA2 = true;

		// ray-rotation sheet
		rrAngleDeg = 5;
		rrPeriod = 10*MM;
		rrShowDPA1 = true;
		rrShowDPA2 = true;

		// rotated cylindrical-lens telescopes
		cltFocalLength = 1*MM;
		cltRotationAngleDeg = 5;
		cltCylindricalLensType = CylindricalLensType.IDEAL;

		// rest of scene
		studioInitialisation = StudioInitialisationType.LATTICE;	// the backdrop
		viewObjectType = ViewObjectType.LATTICE;
		testImage = TestImage.EPSRC_LOGO;
		chartType = ChartType.SET;
		customBackground = true;
		objectRotationAngle = 0;
		objectDistance = 1*M;
		objectHeight = 2*M;

		// camera
		anaglyphCamera = false;
		cameraRotation = 10;
		sideAngle = 0;
		upAngle = 0;
		cameraViewCentre = new Vector3D(0, 0, 0.3);
		cameraDistance = 1.5*CM;
		cameraViewDirection = new Vector3D(0, 0, 1);
		cameraTopDirection = new Vector3D(0,1,0);
		cameraHorizontalFOVDeg = 40;
		cameraApertureSize = ApertureSizeType.PINHOLE;
		setcameraAperture = false;
		cameraAperture = 1*MM;
		cameraFocussingDistance = 1*M;
		useEyeballCamera = true;
		autoFocus = false;
		cameraApertureDiffraction = false;
		rotateViewSystem = false;
		
		//Move mode to change pixel span vector frame by frame
		movie = false;
		numberOfFrames = 10;
		firstFrame = 0;
		lastFrame = numberOfFrames-1;
		startSpanVector = 0.1*MM;
		stopSpanVector = 1*MM;
		
		
		eyePosition = new Vector3D(0,0,-1.5).getProductWith(CM);
		componentCentre = Vector3D.O;
		objectCentre = Vector3D.O;



		if(nonInteractiveTIMAction == NonInteractiveTIMActionEnum.INTERACTIVE)
		{
			windowTitle = "Dr TIM's view-rotation explorer";
			windowWidth = 1500;
			windowHeight = 650;
		}
	}

	

	@Override
	public String getClassName()
	{
		return "ViewRotationExplorer";
	}
	
	@Override
	public void writeParameters(PrintStream printStream)
	{	
				// write any parameters not defined in NonInteractiveTIMEngine
				printStream.println("Scene initialisation");
				printStream.println();
				
				printStream.println("View rotation component");
				printStream.println("viewRotation Component Type= "+viewRotationComponentType);
				switch(viewRotationComponentType)
				{
				case AZIMUTHAL_FRESNEL_WEDGE:
					printStream.println("aFwB="+aFwB);
					printStream.println("aFwN="+aFwN);
					printStream.println("aFwShowSecondWedge="+aFwShowSecondWedge);
					printStream.println("aFwSeparation="+aFwSeparation);
					break;
				case PIXELATED_AZIMUTHAL_FRESNEL_WEDGE:
					printStream.println("aFwB="+aFwBPixel);
					printStream.println("latticeSpanVector1=" +latticeSpanVector1.getProductWith(1/MM)+"mm");
					printStream.println("latticeSpanVector2=" +latticeSpanVector2.getProductWith(1/MM)+"mm");
					break;
				case MOIRE_ROTATOR:
					printStream.println("mmF="+mmF/MM+"mm");
					printStream.println("mmPitch="+mmPitch/MM+"mm");
					printStream.println("mmDeltaPhiDeg="+mmDeltaPhiDeg);
					printStream.println("LA1 "+(mmShowLA1?"on":"off"));
					printStream.println("LA2 "+(mmShowLA2?"on":"off"));
					break;
				case RADIAL_LAS:
					printStream.println("rLAsF="+rLAsF/MM+"mm");
					printStream.println("rLAsN="+rLAsN);
					printStream.println("rLAsDeltaPhiDeg="+rLAsDeltaPhiDeg);
					printStream.println("LA1 "+(rLAsShowLA1?"on":"off"));
					printStream.println("LA2 "+(rLAsShowLA2?"on":"off"));
					break;
				case RR_SHEET:
					printStream.println("rrAngleDeg="+rrAngleDeg);
					printStream.println("rrPeriod="+rrPeriod/MM+"mm");
					printStream.println("DPA1 "+(rrShowDPA1?"on":"off"));
					printStream.println("DPA2 "+(rrShowDPA2?"on":"off"));
					break;
				case CYLINDRICAL_LENS_TELESCOPES:
					printStream.println("cltFocalLength="+cltFocalLength/MM+"mm");
					printStream.println("cltRotationAngleDeg="+cltRotationAngleDeg);
					printStream.println("cltCylindricalLensType="+cltCylindricalLensType);
					break;
				case REFRACTIVE_PIXELATED_FRESNEL_WEDGE:
					printStream.println("span vectors="+refractiveLatticeSpanVector1.getProductWith(1/MM)+"mm,"+ refractiveLatticeSpanVector2.getProductWith(1/MM)+"mm");
					printStream.println("rotation Angle="+rotationAngle);
					printStream.println("with diffraction ="+diffractuveBlurRefractiveFresnelWedge);
					printStream.println("bounding Box centre ="+boundingBoxCentre.getProductWith(1/MM)+"mm,");
					printStream.println("Bounidng Box span vetcors ="	+boundingBoxSpanVector1.getProductWith(1/CM)+"cm,"+boundingBoxSpanVector2.getProductWith(1/CM)+"cm,"+boundingBoxSpanVector3.getProductWith(1/MM)+"mm");
					printStream.println("wedge thickness =" +thickness/MM+"mm");
					printStream.println("transmission coefficient =" +surfaceTransmissionCoefficient);
					printStream.println("eye positon ="+eyePosition);
					printStream.println("derivativeControlType ="+derivativeControlType);
					if(derivativeControlType!=DerivativeControlType.NONE) {
						printStream.println("derivativeControlThickness ="+derivativeControlThickness);
						printStream.println("derivativeControlRotation ="+derivativeControlRotation);
						
					}
					printStream.println("refractive index="+refractiveIndex);
					printStream.println("magnificationFactor="+magnificationFactor);
					printStream.println("Design distance plane="+designDistancePlane);
					printStream.println("trace level ="+cameraMaxTraceLevel);
					if(movie)printStream.println("startSpanVector= "+startSpanVector/MM+"mm, and stopSpanVector= "+stopSpanVector/MM+"mm");
					break;
				case NOTHING:
					break;
				}
				printStream.println();
				printStream.println("View Object");
				printStream.println("custom background= "+customBackground);
				printStream.println("view object= "+viewObjectType);
				switch(viewObjectType){
				case SNELLEN_CHART:
					printStream.println("Chart type= "+chartType);
					break;
				case LATTICE:
				case TEST_IMAGE:
				case NOTHING:
					break;
					
			}
				printStream.println("object height= "+ objectHeight);
				printStream.println("object distance= "+objectDistance);
				printStream.println("object rotation angle= "+ objectRotationAngle);
				printStream.println("backdrop= "+studioInitialisation.toString());
				
				printStream.println();
				printStream.println("Camera");
				printStream.println(" cD="+cameraDistance);
				printStream.println(" cVD="+cameraViewDirection);
				printStream.println(" rotateViewSystem="+rotateViewSystem);
				printStream.println(" cVD vertical angle="+upAngle);
				printStream.println(" cVD horizontal angle="+sideAngle);
				printStream.println(" cFOV="+cameraHorizontalFOVDeg);
				printStream.println(" cAS="+cameraApertureSize);
				printStream.println(" custom aperture size="+setcameraAperture);
				if(setcameraAperture)printStream.println(" aperture radius="+ cameraAperture/MM+"mm");
				printStream.println(" cFD="+cameraFocussingDistance);
				printStream.println(" diffractive aperture= "+cameraApertureDiffraction);
				printStream.println("autoFocus="+autoFocus);
				printStream.println(" cRot="+cameraRotation);
				printStream.println(" cEye="+useEyeballCamera);
				printStream.println(" cAnaglypic=" +anaglyphCamera);
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


		//setting the lights and backdrop
		if (customBackground) {
			
			// initialise the scene and lights
			StudioInitialisationType.initialiseSceneAndLights(
					StudioInitialisationType.UNIVERSITY_SQUARE,
					scene,
					studio
					);

			// a cylinder lattice..
			Vector3D uHat, vHat, zHat;
			zHat = new Vector3D(0,0,1);
			uHat = new Vector3D(Math.cos(Math.toRadians(objectRotationAngle)), Math.sin(Math.toRadians(objectRotationAngle)),0);
			vHat= new Vector3D(-Math.sin(Math.toRadians(objectRotationAngle)), Math.cos(Math.toRadians(objectRotationAngle)),0);
			
			switch(viewObjectType){
				case LATTICE:
					scene.addSceneObject(new EditableCylinderLattice(
							"cylinder lattice",
							-0.5*objectHeight, 0.5*objectHeight, 4, uHat,
							-0.5*objectHeight+0.02*M, 0.5*objectHeight+0.02, 4, vHat,
							objectDistance, 4*objectDistance, 4, zHat, // this puts the "transverse" cylinders into the planes z=10, 20, 30, 40
							0.02*M,
							scene,
							studio
							));	
					break;
				case TEST_IMAGE:
					// ... and an object in a given z plane
					double testImageWidth = objectHeight * testImage.getAspectRatio();
					scene.addSceneObject(testImage.getEditableScaledParametrisedCentredParallelogram(
							testImage.toString(), 	// description
							new Vector3D(0, 0, objectDistance),	// centre
							uHat.getWithLength(testImageWidth),	// spanVector1
							vHat.getWithLength(objectHeight),	// spanVector2
							scene,	// parent
							studio));
					break;
				case SNELLEN_CHART:
					
					scene.addSceneObject(new SnellenChart(
							"A snellen chart",// description,
							cameraViewDirection.getWithLength(objectDistance),// centre,
							vHat,//new Vector3D(0,1,0),// upDirection,
							uHat,//new Vector3D(1,0,0),// rightDirection,
							objectHeight,// height,
							eyePosition,// cameraPosition, TODO this is the eye position, not necessarily equal to the camera position!
							chartType,
							scene,// parent,
							studio //studio
							));
				case NOTHING:
					break;
					
			}
				
				
//			}

		} else {
			// initialise the scene and lights
			StudioInitialisationType.initialiseSceneAndLights(
					studioInitialisation,
					scene,
					studio
					);
		}
		//make the very basic movie version...
		if(movie) {
			refractiveLatticeSpanVector1 = new Vector3D(startSpanVector+(stopSpanVector-startSpanVector)*frame/numberOfFrames,0,0);
			refractiveLatticeSpanVector2 = new Vector3D(0,startSpanVector+(stopSpanVector-startSpanVector)*frame/numberOfFrames,0);
		}
		//if switching to the anaglyph camera, the position of the spectacle has to be changed to go in front of the 'eye' 
		if(anaglyphCamera) {
			Vector3D viewCentre = new Vector3D(0,0,objectDistance);
			eyePosition = Vector3D.sum(eyeSeparation.getProductWith(0.5) ,eyePosition);//new Vector3D(0,0,-cameraDistance));
			//Calculate where a ray would hit the component front surface as the view centre is now shifted.
			Vector3D lightRayDirection = Vector3D.difference(viewCentre, eyePosition);
			objectCentre = Vector3D.sum(Vector3D.O, eyeSeparation.getProductWith(0.5));
			try {
				componentCentre =  Geometry.linePlaneIntersection(eyePosition, lightRayDirection, Vector3D.O, new Vector3D(0,0,-1));
			} catch (MathException e) {
				componentCentre = Vector3D.O;
				System.err.println("The Object is not infront of the spectacles(Oopsy Doopsy)");
				e.printStackTrace();
			}

		} else {
			//eyePosition = new Vector3D(0,0,-1.5).getProductWith(CM);
			componentCentre = Vector3D.O;
			objectCentre = Vector3D.O;
		}


		double separation;
		Vector3D centre1, centre2;

		switch(viewRotationComponentType)
		{
		case AZIMUTHAL_FRESNEL_WEDGE:
			scene.addSceneObject(
					new EditableScaledParametrisedDisc(
							"Azimuthal Fresnel wedge",	// description
							objectCentre.getSumWith(new Vector3D(0, 0, aFwShowSecondWedge?-0.5*aFwSeparation:0)),	// centre
							Vector3D.Z,	// normal
							3*CM,	// radius
							new RotationallySymmetricPhaseHologram(
									componentCentre,	// centre
									aFwB,	// b
									2,	// c
									0,	// s
									0,	// t
									true,	// aFwSimulateHonestly,	// simulate honestly?
									aFwN,	// n
									new Vector3D(1, 0, 0),	// d0
									SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT,	// throughputCoefficient
									false,	// reflective
									true	// shadowThrowing
									),	// surfaceProperty
							scene,	// parent
							studio
							)
					);
			
			//  second azimuthal Fresnel wedge, if required
			if(aFwShowSecondWedge)
			{
				scene.addSceneObject(
						new EditableScaledParametrisedDisc(
								"Azimuthal Fresnel wedge",	// description
								objectCentre.getSumWith(new Vector3D(0, 0, +0.5*aFwSeparation)),	// centre
								Vector3D.Z,	// normal
								3*CM,	// radius
								new RotationallySymmetricPhaseHologram(
										componentCentre,	// centre
										-aFwB,	// b
										2,	// c
										0,	// s
										0,	// t
										true,	// aFwSimulateHonestly,	// simulate honestly?
										aFwN,	// n
										new Vector3D(1, 0, 0),	// d0
										SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT,	// throughputCoefficient
										false,	// reflective
										true	// shadowThrowing
										),	// surfaceProperty
								scene,	// parent
								studio
								)
						);
			}
			break;

		case PIXELATED_AZIMUTHAL_FRESNEL_WEDGE:
			scene.addSceneObject(
					new EditableScaledParametrisedDisc(
							"Azimuthal Fresnel wedge",	// description
							objectCentre,	// centre
							Vector3D.Z,	// normal
							3*CM,	// radius
							new AzimuthalPixelatedFresnelWedge(
									componentCentre,	// centre
									latticeSpanVector1, //latticeSpanVector1,
									latticeSpanVector2,// latticeSpanVector2,
									aFwBPixel,	// b
									2,	// c
									0,	// s
									0,	// t
									diffractiveBlurPixelatedFresnelWedge,// simulateDiffractiveBlur,
									632.8e-9,// lambda (wave length)
									SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT,	// throughputCoefficient
									false,	// reflective
									false	// shadowThrowing
									),	// surfaceProperty
							scene,	// parent
							studio
							)
					);

			break;
		case MOIRE_ROTATOR:
			separation = 1e-6;

			centre1 = new Vector3D(0, 0, -0.5*separation);
			scene.addSceneObject(
					new EditableScaledParametrisedDisc(
							"Lenslet array 1",	// description
							objectCentre.getSumWith(centre1),	// centre
							Vector3D.Z,	// normal
							3*CM,	// radius
							new PhaseHologramOfRectangularLensletArray(
									componentCentre.getSumWith(centre1),
									Vector3D.X,	// uHat
									Vector3D.Y,	// vHat
									mmF,	// focalLength
									mmPitch,	// uPeriod
									mmPitch,	// vPeriod
									0,	// uOffset
									0,	// vOffset
									false,	// simulateDiffractiveBlur
									632.8e-9,	// lambda
									SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT,	// throughputCoefficient
									false,	// reflective
									true	// shadowThrowing
									),	// surfaceProperty
							scene,	// parent
							studio
							),
					mmShowLA1
					);

			centre2 = new Vector3D(0, 0, +0.5*separation);
			scene.addSceneObject(
					new EditableScaledParametrisedDisc(
							"Lenslet array 2",	// description
							objectCentre.getSumWith(centre2),	// centre
							Vector3D.Z,	// normal
							3*CM,	// radius
							new PhaseHologramOfRectangularLensletArray(
									componentCentre.getSumWith(centre2),
									Vector3D.sum(
											Vector3D.X.getProductWith(Math.cos(MyMath.deg2rad(mmDeltaPhiDeg))),
											Vector3D.Y.getProductWith(Math.sin(MyMath.deg2rad(mmDeltaPhiDeg)))
											),	// uHat
									Vector3D.sum(
											Vector3D.X.getProductWith(-Math.sin(MyMath.deg2rad(mmDeltaPhiDeg))),
											Vector3D.Y.getProductWith(Math.cos(MyMath.deg2rad(mmDeltaPhiDeg)))
											),	// vHat
									-mmF,	// focalLength
									mmPitch,	// uPeriod
									mmPitch,	// vPeriod
									0,	// uOffset
									0,	// vOffset
									false,	// simulateDiffractiveBlur
									632.8e-9,	// lambda
									SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT,	// throughputCoefficient
									false,	// reflective
									true	// shadowThrowing
									),	// surfaceProperty
							scene,	// parent
							studio
							),
					mmShowLA2
					);
			break;
		case RADIAL_LAS:
			separation = 1e-6;

			centre1 = new Vector3D(0, 0, -0.5*separation);
			scene.addSceneObject(
					new EditableScaledParametrisedDisc(
							"Radial lenticular array 1",	// description
							objectCentre.getSumWith(centre1),	// centre
							Vector3D.Z,	// normal
							3*CM,	// radius
							new PhaseHologramOfRadialLenticularArray(
									componentCentre.getSumWith(centre1),	// centre
									rLAsF,
									rLAsN,
									Vector3D.X,	// d0
									SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT,	// throughputCoefficient
									false,	// reflective
									true	// shadowThrowing
									),	// surfaceProperty
							scene,	// parent
							studio
							),
					rLAsShowLA1
					);

			centre2 = new Vector3D(0, 0, +0.5*separation);
			scene.addSceneObject(
					new EditableScaledParametrisedDisc(
							"Radial lenticular array 2",	// description
							objectCentre.getSumWith(centre2),	// centre
							Vector3D.Z,	// normal
							3*CM,	// radius
							new PhaseHologramOfRadialLenticularArray(
									componentCentre.getSumWith(centre2),	// centre
									-rLAsF,
									rLAsN,
									Vector3D.sum(
											Vector3D.X.getProductWith(Math.cos(MyMath.deg2rad(rLAsDeltaPhiDeg))),
											Vector3D.Y.getProductWith(Math.sin(MyMath.deg2rad(rLAsDeltaPhiDeg)))
											),	// d0
									SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT,	// throughputCoefficient
									false,	// reflective
									true	// shadowThrowing
									),	// surfaceProperty
							scene,	// parent
							studio
							),
					rLAsShowLA2
					);
			break;
		case RR_SHEET:
			separation = 1e-6;

			centre1 = new Vector3D(0, 0, -0.5*separation);
			scene.addSceneObject(
					new EditableScaledParametrisedDisc(
							"Dove-prism array 1",	// description
							objectCentre.getSumWith(centre1),	// centre
							Vector3D.Z,	// normal
							3*CM,	// radius
							new IdealisedDovePrismArray(
									componentCentre.getSumWith(centre1),	// dovePrism0Centre
									new Vector3D(1, 0, 0),	// inversionDirection
									rrPeriod,	// period
									SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT,	// throughputCoefficient
									true	// shadowThrowing
									),	// surfaceProperty
							scene,	// parent
							studio
							),
					rrShowDPA1
					);

			centre2 = new Vector3D(0, 0, +0.5*separation);
			scene.addSceneObject(
					new EditableScaledParametrisedDisc(
							"Dove-prism array 2",	// description
							objectCentre.getSumWith(centre2),	// centre
							Vector3D.Z,	// normal
							3*CM,	// radius
							new IdealisedDovePrismArray(
									componentCentre.getSumWith(centre2),	// dovePrism0Centre
									Vector3D.sum(
											Vector3D.X.getProductWith(Math.cos(MyMath.deg2rad(-rrAngleDeg))),
											Vector3D.Y.getProductWith(Math.sin(MyMath.deg2rad(-rrAngleDeg)))
											),	// inversionDirection
									rrPeriod,	// period
									SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT,	// throughputCoefficient
									true	// shadowThrowing
									),	// surfaceProperty
							scene,	// parent
							studio
							),
					rrShowDPA2
					);
			break;
		case CYLINDRICAL_LENS_TELESCOPES:
			double cltRotationAngleRad2 = 0.5*MyMath.deg2rad(cltRotationAngleDeg);
			Vector3D centre11 = new Vector3D(0, 0, -MyMath.TINY-2*cltFocalLength);
			scene.addSceneObject(
					new EditableScaledParametrisedDisc(
							"telescope 1, cylindrical lens 1",	// description
							objectCentre.getSumWith(centre11),	// centre
							Vector3D.Z,	// normal
							3*CM,	// radius
							getCylindricalLensSurface(
									componentCentre.getSumWith(centre11),	// principalPoint,
									new Vector3D(Math.cos(cltRotationAngleRad2), Math.sin(cltRotationAngleRad2), 0)	// phaseGradientDirection
									),	// surfaceProperty
							scene,	// parent
							studio
							)
					);
			Vector3D centre12 = new Vector3D(0, 0, -MyMath.TINY);
			scene.addSceneObject(
					new EditableScaledParametrisedDisc(
							"telescope 1, cylindrical lens 2",	// description
							objectCentre.getSumWith(centre12),	// centre
							Vector3D.Z,	// normal
							3*CM,	// radius
							getCylindricalLensSurface(
									componentCentre.getSumWith(centre12),	// principalPoint,
									new Vector3D(Math.cos(cltRotationAngleRad2), Math.sin(cltRotationAngleRad2), 0)	// phaseGradientDirection
									),	// surfaceProperty
							scene,	// parent
							studio
							)
					);
			Vector3D centre21 = new Vector3D(0, 0, +MyMath.TINY+2*cltFocalLength);
			scene.addSceneObject(
					new EditableScaledParametrisedDisc(
							"telescope 2, cylindrical lens 1",	// description
							objectCentre.getSumWith(centre21),	// centre
							Vector3D.Z,	// normal
							3*CM,	// radius
							getCylindricalLensSurface(
									componentCentre.getSumWith(centre21),	// principalPoint,
									new Vector3D(Math.cos(-cltRotationAngleRad2), Math.sin(-cltRotationAngleRad2), 0)	// phaseGradientDirection
									),	// surfaceProperty
							scene,	// parent
							studio
							)
					);
			Vector3D centre22 = new Vector3D(0, 0, MyMath.TINY);
			scene.addSceneObject(
					new EditableScaledParametrisedDisc(
							"telescope 2, cylindrical lens 2",	// description
							objectCentre.getSumWith(centre22),	// centre
							Vector3D.Z,	// normal
							3*CM,	// radius
							getCylindricalLensSurface(
									componentCentre.getSumWith(centre22),	// principalPoint,
									new Vector3D(Math.cos(-cltRotationAngleRad2), Math.sin(-cltRotationAngleRad2), 0)	// phaseGradientDirection
									),	// surfaceProperty
							scene,	// parent
							studio
							)
					);
			break;


		case REFRACTIVE_PIXELATED_FRESNEL_WEDGE:
			scene.addSceneObject( new RefractiveViewRotator(
					"refractive view rotator",// description,
					boundingBoxCentre.getSumWith(objectCentre), 
					boundingBoxSpanVector1,
					boundingBoxSpanVector2,
					boundingBoxSpanVector3,
					new Vector3D(0,0,-1),// ocularSurfaceNormal,
					eyePosition,
					componentCentre, //centre
					rotationAngle,
					new Vector3D(0,0,-1), //rotation axis direction
					magnificationFactor,// magnificationFactor,
					refractiveLatticeSpanVector1,
					refractiveLatticeSpanVector2,
					new Plane("focus plane", 
							designDistancePlane,
							Vector3D.Z, 
							null,
							null, 
							null
							),
					derivativeControlType,
					derivativeControlThickness,
					derivativeControlRotation,
					refractiveIndex,
					thickness,
					surfaceTransmissionCoefficient, //trasnmission coef
					diffractuveBlurRefractiveFresnelWedge,
					maxTrace,
					scene, 
					scene,
					studio
					));
			
			//System.out.println("eye position at "+eyePosition);

		case NOTHING:
			// don't add anything
			break;
		}

		// the cameras
		
		//eye radius given by: https://hypertextbook.com/facts/2002/AniciaNdabahaliye1.shtml
		//to be about 1.225 cm in radius
		double eyeRadius = 1.225*CM;
		double allRadius = (eyeRadius+cameraDistance);

		if(autoFocus) cameraFocussingDistance = cameraDistance*(cameraDistance+objectDistance)/(cameraDistance+objectDistance-magnificationFactor*objectDistance);
		double cameraApertureRadius = 0;
		if(setcameraAperture) {
			cameraApertureRadius = cameraAperture;
		}else {
			cameraApertureRadius = cameraApertureSize.getApertureRadius();
		}
		
		if(sideAngle != 0 || upAngle != 0) {
			//in spherical coordinates where the view along the z axis is upAngle = 0 sideAngle = 0 
			cameraViewDirection = Geometry.rotate(Geometry.rotate(Vector3D.Z, Vector3D.Y, Math.toRadians(-sideAngle)),
					Vector3D.X,	Math.toRadians(-upAngle)).getNormalised();
		}
		
		if(rotateViewSystem) cameraViewDirection = Geometry.rotate(cameraViewDirection, Vector3D.Z, Math.toRadians(cameraRotation));

		Vector3D topDirection = Vector3D.Y.getPartPerpendicularTo(cameraViewDirection);
		Vector3D cameraCentre;
		Vector3D cameraAim;
		if (useEyeballCamera) {
			//The cameraCentre is now actually the eye centre position
			cameraCentre = Vector3D.Z.getWithLength(-allRadius);
			cameraAim = cameraCentre.getSumWith(cameraViewDirection.getWithLength(allRadius));
		}else {
			cameraCentre = Vector3D.Z.getWithLength(-cameraDistance);
			cameraAim = cameraCentre.getSumWith(cameraViewDirection.getWithLength(cameraDistance));
		}
		
		if(anaglyphCamera) {
//			By default:
//			cameraPixelsX = 640
//			cameraPixelsY = 480
			//TODO update to allow for off axis viewing...
			int
			pixelsX = cameraPixelsX*(int)renderQuality.getAntiAliasingQuality().getAntiAliasingFactor(),
			pixelsY = cameraPixelsY*(int)renderQuality.getAntiAliasingQuality().getAntiAliasingFactor(),
			raysPerPixel = renderQuality.getBlurQuality().getRaysPerPixel();

			CyclodeviationAnaglyphCamera cyclodeviationAnaglyphCamera = new CyclodeviationAnaglyphCamera(
					"Cyclodeviated anaglyph camera",// name,
					Vector3D.sum(Vector3D.O,Vector3D.Z.getWithLength(-cameraDistance)),// betweenTheEyes,	// middle between two eyes
					Vector3D.sum(Vector3D.O,Vector3D.Z.getWithLength(-cameraDistance),cameraViewDirection.getWithLength(cameraDistance+objectDistance)),// centreOfView,	// the point in the centre of both eyes' field of view
					Geometry.rotate(Vector3D.X, Vector3D.Y, Math.toRadians(-sideAngle)).getWithLength(objectDistance*2*Math.tan(MyMath.deg2rad(cameraHorizontalFOVDeg)/2.)), // horizontalSpanVector,	// a vector along the width of the field of view, pointing to the right
					Geometry.rotate(Vector3D.Y,Geometry.rotate(Vector3D.X, Vector3D.Y, Math.toRadians(-sideAngle)),Math.toRadians(-upAngle))
					.getWithLength(objectDistance*-2*Math.tan(MyMath.deg2rad(cameraHorizontalFOVDeg)/2.) * pixelsY / pixelsX),// verticalSpanVector,	// a vector along the height of the field of view, pointing upwards
					eyeSeparation,	// separation between the eyes
					0,// leftEyeRotationAngle,
					Math.toRadians(cameraRotation),//rightEyeRotationAngle,
					pixelsX,// detectorPixelsHorizontal, 
					pixelsY,// detectorPixelsVertical,
					cameraExposureCompensation,// exposureCompensation default is ExposureCompensationType.EC0;
					maxTrace,// maxTraceLevel,
					cameraFocussingDistance,// focussingDistance,
					cameraApertureRadius,
		            cameraApertureDiffraction,
					550e-9,// lambda, (10x larger for diffraction)
					raysPerPixel,// raysPerPixel,
					true// colour
					);
			studio.setCamera(cyclodeviationAnaglyphCamera);
		}else {
			Vector3D apertureCentre = Vector3D.sum(cameraAim, cameraViewDirection.getWithLength(-cameraDistance));
			cameraTopDirection = Geometry.rotate(topDirection.getNormalised(), cameraViewDirection, Math.toRadians(cameraRotation));//.getSumWith(cameraCentre); 
			cameraViewCentre = Vector3D.sum(apertureCentre, cameraViewDirection);
		
			AnyFocusSurfaceCamera defaultCamera = new AnyFocusSurfaceCamera(
					"Camera",
					apertureCentre,	// centre of aperture
					cameraViewCentre,// view Centre take normalised view direction and add it to the camera pos.
					calculateHorizontalSpanVector(cameraViewDirection, cameraTopDirection.getNormalised(), cameraHorizontalFOVDeg),// horizontalSpanVector, 
					calculateVerticalSpanVector(cameraViewDirection, cameraTopDirection.getNormalised(), cameraHorizontalFOVDeg, cameraPixelsX, cameraPixelsY) ,//verticalSpanVector,
					cameraPixelsX, cameraPixelsY,	// logical number of pixels
					cameraExposureCompensation,	// ExposureCompensationType.EC0,	// exposure compensation +0
					cameraMaxTraceLevel,	// maxTraceLevel
					new Plane(
							"focus plane",	// description
							Vector3D.sum(apertureCentre, cameraViewDirection.getWithLength(cameraFocussingDistance)),	// pointOnPlane
							cameraViewDirection.getNormalised(),	// normal
							null,	// surfaceProperty
							null,	// parent
							null	// studio
						),	// focus scene
		            // double detectorDistance,	// in the detector-plane shutter model, the detector is this distance behind the entrance pupil
		            cameraApertureRadius,// apertureRadius,
		            cameraApertureDiffraction,
					550e-9,// lambda,lambda, (10x larger than normal)
		            renderQuality.getBlurQuality().getRaysPerPixel()// raysPerPixel
		    	);
			//defualtCamera.getViewDirection();
			System.out.println("apertureCentre "+apertureCentre);
			System.out.println("cameraTopDirection "+cameraTopDirection);
			System.out.println("cameraViewCentre "+cameraViewCentre+" vs "+defaultCamera.getCentreOfView());
			System.out.println("cameraViewDirection "+cameraViewDirection+". vs "+ defaultCamera.getViewDirection());
			studio.setCamera(defaultCamera);
		}
	}

	private SurfaceProperty getCylindricalLensSurface(
			Vector3D principalPoint,
			Vector3D phaseGradientDirection
			)
	{
		switch(cltCylindricalLensType)
		{
		case IDEAL:
			return new IdealThinCylindricalLensSurfaceSimple(
					principalPoint,	// lensCentre
					Vector3D.Z,	// opticalAxisDirection
					phaseGradientDirection,	// gradientDirection
					cltFocalLength,
					SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT,	// transmissionCoefficient
					true	// shadowThrowing
					) ;
		case PHASE_HOLOGRAM:
		default:
			return new PhaseHologramOfCylindricalLens(
					cltFocalLength,	// focalLength
					principalPoint,
					phaseGradientDirection,
					SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT,	// throughputCoefficient
					false,	// reflective
					true	// shadowThrowing
					);	// surfaceProperty
		}
	}
	
	private static Vector3D calculateVerticalSpanVector(
			Vector3D viewDirection1,
			Vector3D topDirection1,
			double horizontalViewAngle1,
			int imagePixelsHorizontal1, int imagePixelsVertical1
		)
	{
		return topDirection1.getPartPerpendicularTo(viewDirection1).getWithLength(
				-2*Math.tan(MyMath.deg2rad(horizontalViewAngle1)/2.) * 
				imagePixelsVertical1 / imagePixelsHorizontal1
			);
	}

	private static Vector3D calculateHorizontalSpanVector(
			Vector3D viewDirection1,
			Vector3D topDirection1,
			double horizontalViewAngle1
		)
	{
		return Vector3D.crossProduct(topDirection1, viewDirection1).getWithLength(2*Math.tan(MyMath.deg2rad(horizontalViewAngle1)/2.));
	}
	

	//
	// for interactive version
	//

	JTabbedPane viewRotatingComponentTabbedPane, backgroundTabbedPane;

	// azimuthal Fresnel wedge
	private DoublePanel aFwBPanel, aFwSeparationPanel;
	// private JCheckBox aFwSimulateHonestlyCheckBox;
	private IntPanel aFwNPanel;
	private JCheckBox aFwShowSecondWedgeCheckBox;

	//Pixelated azimuthal fresnel wedge
	private DoublePanel aFwBPixelPanel;
	private Vector3DPanel latticeSpanVector1Panel, latticeSpanVector2Panel;
	private JCheckBox diffractiveBlurPixelatedFresnelWedgeCheckBox;


	//refractive fresnel wedge
	private DoublePanel rotationAnglePanel, thicknessPanel, refractiveIndexPanel, surfaceTransmissionCoefficientPanel, startSpanVectorPanel, stopSpanVectorPanel, magnificationFactorPanel,
	derivativeControlThicknessPanel, derivativeControlRotationPanel;
	private Vector3DPanel refractiveLatticeSpanVector1Panel, refractiveLatticeSpanVector2Panel, boundingBoxCentrePanel, boundingBoxSpanVector1Panel,
	boundingBoxSpanVector2Panel, boundingBoxSpanVector3Panel, eyePositionPanel, designDistancePlanePanel;
	private JCheckBox diffractuveBlurRefractiveFresnelWedgeCheckBox;
	private IntPanel maxTracePanel;
	private JComboBox<DerivativeControlType> derivativeControlTypeComboBox;

	// moiré rotator
	private DoublePanel mmFPanel;
	private DoublePanel mmPitchPanel;
	private DoublePanel mmDeltaPhiDegPanel;
	private JCheckBox mmShowLA1CheckBox, mmShowLA2CheckBox;

	// radial LAs
	private DoublePanel rLAsFPanel;
	private IntPanel rLAsNPanel;
	private DoublePanel rLAsDeltaPhiDegPanel;
	private JCheckBox rLAsShowLA1CheckBox, rLAsShowLA2CheckBox;

	// RR sheet
	private DoublePanel rrAngleDegPanel, rrPeriodPanel;
	private JCheckBox rrShowDPA1CheckBox, rrShowDPA2CheckBox;

	// rotated cylindrical-lens telescopes
	private DoublePanel cltFocalLengthPanel, cltRotationAngleDegPanel;
	private JComboBox<CylindricalLensType> cltCylindricalLensTypeComboBox;


	private JComboBox<StudioInitialisationType> studioInitialisationComboBox;
	private JComboBox<ViewObjectType> viewObjectTypeComboBox;
	private JComboBox<TestImage> testImageComboBox;
	private JComboBox<ChartType> chartTypeComboBox;
	private DoublePanel objectRotationAnglePanel, objectDistancePanel, objectHeightPanel;

	// camera stuff
	// private LabelledVector3DPanel cameraViewDirectionPanel;
	private DoublePanel cameraDistancePanel, cameraRotationPanel, sideAnglePanel, upAnglePanel;
	private LabelledVector3DPanel cameraViewDirectionPanel;
	private DoublePanel cameraHorizontalFOVDegPanel;
	private JComboBox<ApertureSizeType> cameraApertureSizeComboBox;
	private DoublePanel cameraFocussingDistancePanel, cameraAperturePanel;
	private JCheckBox anaglyphCameraCheckBox, useEyeballCameraCheckBox, setcameraApertureCheckBox;
	private JCheckBox movieCheckBox, autoFocusCheckBox, cameraApertureDiffractionCheckBox, rotateViewSystemCheckBox;
	private IntPanel numberOfFramesPanel, firstFramePanel, lastFramePanel;  




	/**
	 * add controls to the interactive control panel;
	 * override to modify
	 */
	@Override	
protected void createInteractiveControlPanel()	
{	
	super.createInteractiveControlPanel();	
	// the main tabbed pane, with "Scene" and "Camera" tabs	
	JTabbedPane backgroundComponentCameraTabbedPane = new JTabbedPane();	
	interactiveControlPanel.add(backgroundComponentCameraTabbedPane, "span");	
	//	
	// the component panel	
	//	
	JPanel scenePanel = new JPanel();	
	scenePanel.setLayout(new BorderLayout());	
	backgroundComponentCameraTabbedPane.addTab("Component", scenePanel);



		// the tabbed pane that enables selection of the view-rotating component
		viewRotatingComponentTabbedPane = new JTabbedPane();
		scenePanel.add(viewRotatingComponentTabbedPane, BorderLayout.CENTER);


		// azimuthal Fresnel wedge

		JPanel azimuthalFresnelWedgePanel = new JPanel();
		azimuthalFresnelWedgePanel.setLayout(new MigLayout("insets 0"));

		aFwBPanel = new DoublePanel();
		aFwBPanel.setNumber(aFwB);
		azimuthalFresnelWedgePanel.add(GUIBitsAndBobs.makeRow("<html>Phase gradient d<i>&Phi;</i>/d<i>&phi;</i> = <i>k</i>*</html>", aFwBPanel, "<html>*<i>r</i><sup>2</sup>,</html>"), "span");
		azimuthalFresnelWedgePanel.add(new JLabel("<html>where &phi; is the azimuthal angle and <i>r</i> is the distance from the centre.</html>"), "wrap");

		aFwNPanel = new IntPanel();
		aFwNPanel.setNumber(aFwN);
		azimuthalFresnelWedgePanel.add(
				GUIBitsAndBobs.makeRow(
						"The clear aperture is divided into",
						aFwNPanel,
						"circular sectors"
						),
				"span"
				);
		
		aFwShowSecondWedgeCheckBox = new JCheckBox("Show second azimuthal Fresnel wedge (experimental)");
		aFwShowSecondWedgeCheckBox.setSelected(aFwShowSecondWedge);
		azimuthalFresnelWedgePanel.add(aFwShowSecondWedgeCheckBox, "span");

		aFwSeparationPanel = new DoublePanel();
		aFwSeparationPanel.setNumber(aFwSeparation/MM);
		azimuthalFresnelWedgePanel.add(
				GUIBitsAndBobs.makeRow("Separation between first and second wedge (if shown)", aFwSeparationPanel, "mm"),
				"span"
			);

		viewRotatingComponentTabbedPane.addTab("Azimuthal Fresnel wedge", azimuthalFresnelWedgePanel);




		JPanel PixelatedAzimuthalFresnelWedgePanel = new JPanel();
		// azimuthalFresnelWedgePanel.setBorder(GUIBitsAndBobs.getTitledBorder("Azimuthal Fresnel wedge"));
		PixelatedAzimuthalFresnelWedgePanel.setLayout(new MigLayout("insets 0"));

		aFwBPixelPanel = new DoublePanel();
		aFwBPixelPanel.setNumber(aFwBPixel);
		PixelatedAzimuthalFresnelWedgePanel.add(GUIBitsAndBobs.makeRow("<html>Phase gradient d<i>&Phi;</i>/d<i>&phi;</i> = <i>k</i>*</html>", aFwBPixelPanel, "<html>*<i>r</i><sup>2</sup>,</html>"), "span");
		PixelatedAzimuthalFresnelWedgePanel.add(new JLabel("<html>where &phi; is the azimuthal angle and <i>r</i> is the distance from the centre.</html>"), "wrap");

		latticeSpanVector1Panel = new Vector3DPanel();
		latticeSpanVector1Panel.setVector3D(latticeSpanVector1.getProductWith(1/MM));
		PixelatedAzimuthalFresnelWedgePanel.add(GUIBitsAndBobs.makeRow("first pixel span vector", latticeSpanVector1Panel,"mm"), "span");

		latticeSpanVector2Panel = new Vector3DPanel();
		latticeSpanVector2Panel.setVector3D(latticeSpanVector2.getProductWith(1/MM));
		PixelatedAzimuthalFresnelWedgePanel.add(GUIBitsAndBobs.makeRow("second pixel span vector",latticeSpanVector2Panel,"mm" ),"span");

		diffractiveBlurPixelatedFresnelWedgeCheckBox = new JCheckBox("Show diffraction blur");
		diffractiveBlurPixelatedFresnelWedgeCheckBox.setSelected(diffractiveBlurPixelatedFresnelWedge);
		PixelatedAzimuthalFresnelWedgePanel.add(diffractiveBlurPixelatedFresnelWedgeCheckBox, "span");

		viewRotatingComponentTabbedPane.addTab("Pixelated azimuthal Fresnel wedge", PixelatedAzimuthalFresnelWedgePanel);





		// moiré rotator

		JPanel moireRotatorPanel = new JPanel();
		moireRotatorPanel.setLayout(new MigLayout("insets 0"));

		mmFPanel = new DoublePanel();
		mmFPanel.setNumber(mmF/MM);
		mmPitchPanel = new DoublePanel();
		mmPitchPanel.setNumber(mmPitch/MM);
		mmDeltaPhiDegPanel = new DoublePanel();
		mmDeltaPhiDegPanel.setNumber(mmDeltaPhiDeg);
		moireRotatorPanel.add(new JLabel("Two confocal, complementary, square lenslet arrays,"), "wrap");
		moireRotatorPanel.add(
				GUIBitsAndBobs.makeRow("<html>focal lengths &plusmn;</html>", mmFPanel, "mm,"), "wrap");
		moireRotatorPanel.add(
				GUIBitsAndBobs.makeRow("each with pitch", mmPitchPanel, "mm,"), "wrap");
		moireRotatorPanel.add(
				GUIBitsAndBobs.makeRow("and rotated w.r.t. each other by an angle", mmDeltaPhiDegPanel, "<html>&deg;</html>"), "wrap");

		mmShowLA1CheckBox = new JCheckBox("Show LA 1");
		mmShowLA1CheckBox.setSelected(mmShowLA1);
		moireRotatorPanel.add(mmShowLA1CheckBox, "wrap");

		mmShowLA2CheckBox = new JCheckBox("Show LA 2");
		mmShowLA2CheckBox.setSelected(mmShowLA2);
		moireRotatorPanel.add(mmShowLA2CheckBox, "wrap");

		viewRotatingComponentTabbedPane.addTab("Moiré rotator", moireRotatorPanel);



		// complementary radial lenticular arrays

		JPanel complementaryRadialLenticularArraysPanel = new JPanel();
		complementaryRadialLenticularArraysPanel.setLayout(new MigLayout("insets 0"));

		rLAsFPanel = new DoublePanel();
		rLAsFPanel.setNumber(rLAsF/MM);
		rLAsNPanel = new IntPanel();
		rLAsNPanel.setNumber(rLAsN);
		rLAsNPanel.setToolTipText("Number of radial cylindrical lenses in each array");
		rLAsDeltaPhiDegPanel = new DoublePanel();
		rLAsDeltaPhiDegPanel.setNumber(rLAsDeltaPhiDeg);
		rLAsDeltaPhiDegPanel.setToolTipText("Relative rotation angle between the two radial cylindrical lenses");
		complementaryRadialLenticularArraysPanel.add(new JLabel("Two confocal, complementary, radial lenticular arrays,"), "wrap");
		complementaryRadialLenticularArraysPanel.add(
				GUIBitsAndBobs.makeRow("<html>focal lengths &plusmn;</html>", rLAsFPanel, "mm,"), "wrap");
		complementaryRadialLenticularArraysPanel.add(
				GUIBitsAndBobs.makeRow("each with", rLAsNPanel, "cylindrical lenses in it,"), "wrap");
		complementaryRadialLenticularArraysPanel.add(
				GUIBitsAndBobs.makeRow("and rotated w.r.t. each other by an angle", rLAsDeltaPhiDegPanel, "<html>&deg;</html>"), "wrap");

		rLAsShowLA1CheckBox = new JCheckBox("Show LA 1");
		rLAsShowLA1CheckBox.setSelected(rLAsShowLA1);
		complementaryRadialLenticularArraysPanel.add(rLAsShowLA1CheckBox, "wrap");

		rLAsShowLA2CheckBox = new JCheckBox("Show LA 2");
		rLAsShowLA2CheckBox.setSelected(rLAsShowLA2);
		complementaryRadialLenticularArraysPanel.add(rLAsShowLA2CheckBox, "wrap");

		viewRotatingComponentTabbedPane.addTab("Radial LAs", complementaryRadialLenticularArraysPanel);


		// ray-rotation sheet

		JPanel rayRotationSheetPanel = new JPanel();
		rayRotationSheetPanel.setLayout(new MigLayout("insets 0"));

		rayRotationSheetPanel.add(new JLabel("Two Dove-prism arrays,"), "wrap");

		rrPeriodPanel = new DoublePanel();
		rrPeriodPanel.setNumber(rrPeriod/MM);
		rrPeriodPanel.setToolTipText("Period of each of the two Dove-prism arrays");
		rayRotationSheetPanel.add(GUIBitsAndBobs.makeRow("each with period", rrPeriodPanel, "mm,"), "span");

		rrAngleDegPanel = new DoublePanel();
		rrAngleDegPanel.setNumber(rrAngleDeg);
		rrAngleDegPanel.setToolTipText("Angle by which Dove-prism sheets are rotated w.r.t. each other; the ray-rotation angle is twice this angle");
		rayRotationSheetPanel.add(
				GUIBitsAndBobs.makeRow("and rotated w.r.t. each other by an angle", rrAngleDegPanel, "<html>&deg;</html>"), "wrap");

		rrShowDPA1CheckBox = new JCheckBox("Show Dove-prism array 1");
		rrShowDPA1CheckBox.setSelected(rrShowDPA1);
		rayRotationSheetPanel.add(rrShowDPA1CheckBox, "wrap");

		rrShowDPA2CheckBox = new JCheckBox("Show Dove-prism array 2");
		rrShowDPA2CheckBox.setSelected(rrShowDPA2);
		rayRotationSheetPanel.add(rrShowDPA2CheckBox, "wrap");

		viewRotatingComponentTabbedPane.addTab("RR sheet", rayRotationSheetPanel);

		// rotated cylindrical-lens telescopes

		JPanel cylindricalLensTelescopesPanel = new JPanel();
		cylindricalLensTelescopesPanel.setLayout(new MigLayout("insets 0"));

		cylindricalLensTelescopesPanel.add(new JLabel("Two cylindrical-lens telescopes,"), "wrap");

		cltRotationAngleDegPanel = new DoublePanel();
		cltRotationAngleDegPanel.setNumber(cltRotationAngleDeg);
		cylindricalLensTelescopesPanel.add(
				GUIBitsAndBobs.makeRow("rotated w.r.t. each other by an angle", cltRotationAngleDegPanel, "<html>&deg;</html>,"), "wrap");

		cltFocalLengthPanel = new DoublePanel();
		cltFocalLengthPanel.setNumber(cltFocalLength/MM);
		cylindricalLensTelescopesPanel.add(
				GUIBitsAndBobs.makeRow("each comprising cylindrical lenses of focal length", cltFocalLengthPanel, "mm,"), "wrap");

		cltCylindricalLensTypeComboBox = new JComboBox<CylindricalLensType>(CylindricalLensType.values());
		cltCylindricalLensTypeComboBox.setSelectedItem(cltCylindricalLensType);
		cylindricalLensTelescopesPanel.add(GUIBitsAndBobs.makeRow("realised as", cltCylindricalLensTypeComboBox), "wrap");

		viewRotatingComponentTabbedPane.addTab("Cylindrical-lens telescopes", cylindricalLensTelescopesPanel);

		//refractive fresnel wedge view rotator
		JPanel refractiveFresnelWedgePanel = new JPanel();
		refractiveFresnelWedgePanel.setLayout(new MigLayout("insets 0"));
		refractiveFresnelWedgePanel.add(new JLabel("Refractive Fresnel Wedges"), "wrap");

		rotationAnglePanel = new DoublePanel();
		rotationAnglePanel.setNumber(rotationAngle);
		refractiveFresnelWedgePanel.add(GUIBitsAndBobs.makeRow("Rotation angle", rotationAnglePanel, "<html>&deg;</html>,"),"wrap");
		
		designDistancePlanePanel = new Vector3DPanel();
		designDistancePlanePanel.setVector3D(designDistancePlane.getProductWith(1/M));
		refractiveFresnelWedgePanel.add(GUIBitsAndBobs.makeRow("Designe plane centre", designDistancePlanePanel, "m"),"wrap");

		refractiveLatticeSpanVector1Panel = new Vector3DPanel();
		refractiveLatticeSpanVector1Panel.setVector3D(refractiveLatticeSpanVector1.getProductWith(1/MM));
		refractiveFresnelWedgePanel.add(GUIBitsAndBobs.makeRow("pixel span vector 1", refractiveLatticeSpanVector1Panel, "mm"),"wrap");

		refractiveLatticeSpanVector2Panel = new Vector3DPanel();
		refractiveLatticeSpanVector2Panel.setVector3D(refractiveLatticeSpanVector2.getProductWith(1/MM));
		refractiveFresnelWedgePanel.add(GUIBitsAndBobs.makeRow("pixel span vector 2", refractiveLatticeSpanVector2Panel, "mm"),"wrap");

		eyePositionPanel = new Vector3DPanel();
		eyePositionPanel.setVector3D(eyePosition.getProductWith(1/CM));
		refractiveFresnelWedgePanel.add(GUIBitsAndBobs.makeRow("eye position", eyePositionPanel, "cm"),"wrap");

		thicknessPanel = new DoublePanel();
		thicknessPanel.setNumber(thickness/MM);
		refractiveFresnelWedgePanel.add(GUIBitsAndBobs.makeRow("wedge thickness", thicknessPanel, "mm"),"wrap");
		
		//derivativeControlThicknessPanel derivativeControlTypeComboBox
		
		derivativeControlTypeComboBox = new JComboBox<DerivativeControlType>(DerivativeControlType.values());
		derivativeControlTypeComboBox.setSelectedItem(derivativeControlType);
		refractiveFresnelWedgePanel.add(GUIBitsAndBobs.makeRow("derivatibe control type",derivativeControlTypeComboBox),"wrap");
		
		derivativeControlThicknessPanel = new DoublePanel();
		derivativeControlThicknessPanel.setNumber(derivativeControlThickness/MM);
		
		derivativeControlRotationPanel = new DoublePanel();
		derivativeControlRotationPanel.setNumber(derivativeControlRotation);
		refractiveFresnelWedgePanel.add(GUIBitsAndBobs.makeRow("derivative control thickness",derivativeControlThicknessPanel,"mm, and rotation", derivativeControlRotationPanel,"<html>&deg;</html>" ),"wrap");

		refractiveIndexPanel = new DoublePanel();
		refractiveIndexPanel.setNumber(refractiveIndex);
		refractiveFresnelWedgePanel.add(GUIBitsAndBobs.makeRow("refractive index", refractiveIndexPanel),"wrap");
		
		magnificationFactorPanel  = new DoublePanel();
		magnificationFactorPanel.setNumber(magnificationFactor);
		refractiveFresnelWedgePanel.add(GUIBitsAndBobs.makeRow("magnification factor", magnificationFactorPanel),"wrap");

		diffractuveBlurRefractiveFresnelWedgeCheckBox = new JCheckBox();
		diffractuveBlurRefractiveFresnelWedgeCheckBox.setSelected(diffractuveBlurRefractiveFresnelWedge);
		refractiveFresnelWedgePanel.add(GUIBitsAndBobs.makeRow("show diffraction", diffractuveBlurRefractiveFresnelWedgeCheckBox),"wrap");

		surfaceTransmissionCoefficientPanel = new DoublePanel();
		surfaceTransmissionCoefficientPanel.setNumber(surfaceTransmissionCoefficient);
		refractiveFresnelWedgePanel.add(GUIBitsAndBobs.makeRow("set trasnmission coefficient to", surfaceTransmissionCoefficientPanel),"wrap");

		maxTracePanel = new IntPanel();
		maxTracePanel.setNumber(maxTrace);
		refractiveFresnelWedgePanel.add(GUIBitsAndBobs.makeRow("trace level", maxTracePanel),"wrap");

		boundingBoxCentrePanel = new Vector3DPanel();
		boundingBoxCentrePanel.setVector3D(boundingBoxCentre.getProductWith(1/MM));
		refractiveFresnelWedgePanel.add(GUIBitsAndBobs.makeRow("bounding box centre", boundingBoxCentrePanel, "mm"),"wrap");

		boundingBoxSpanVector1Panel = new Vector3DPanel();
		boundingBoxSpanVector1Panel.setVector3D(boundingBoxSpanVector1.getProductWith(1/CM));
		refractiveFresnelWedgePanel.add(GUIBitsAndBobs.makeRow("bounding box span vector 1", boundingBoxSpanVector1Panel, "cm"),"wrap");

		boundingBoxSpanVector2Panel = new Vector3DPanel();
		boundingBoxSpanVector2Panel.setVector3D(boundingBoxSpanVector2.getProductWith(1/CM));
		refractiveFresnelWedgePanel.add(GUIBitsAndBobs.makeRow("bounding box span vector 2", boundingBoxSpanVector2Panel, "cm"),"wrap");

		boundingBoxSpanVector3Panel = new Vector3DPanel();
		boundingBoxSpanVector3Panel.setVector3D(boundingBoxSpanVector3.getProductWith(1/MM));
		refractiveFresnelWedgePanel.add(GUIBitsAndBobs.makeRow("bounding box thickness span vector", boundingBoxSpanVector3Panel, "mm"),"wrap");	
		
		movieCheckBox = new JCheckBox();
		movieCheckBox.setSelected(movie);

		numberOfFramesPanel = new IntPanel();
		numberOfFramesPanel.setNumber(numberOfFrames);
		
		firstFramePanel = new IntPanel();
		firstFramePanel.setNumber(firstFrame);
		
		lastFramePanel = new IntPanel();
		lastFramePanel.setNumber(lastFrame);

		
		refractiveFresnelWedgePanel.add(GUIBitsAndBobs.makeRow("Movie: ",movieCheckBox,"Total frames: ",numberOfFramesPanel),"span");
		refractiveFresnelWedgePanel.add(GUIBitsAndBobs.makeRow("starting at ",firstFramePanel," and stopping at ",lastFramePanel),"span");
		
		
		startSpanVectorPanel = new DoublePanel();
		startSpanVectorPanel.setNumber(startSpanVector/MM);
		
		stopSpanVectorPanel = new DoublePanel();
		stopSpanVectorPanel.setNumber(stopSpanVector/MM);
		
		refractiveFresnelWedgePanel.add(GUIBitsAndBobs.makeRow("strating span vector: ", startSpanVectorPanel, "mm, stopping span Vector: ",stopSpanVectorPanel,"mm"),"wrap");

		viewRotatingComponentTabbedPane.addTab("Refractive Fresnel Wedges", refractiveFresnelWedgePanel);


		// nothing

		viewRotatingComponentTabbedPane.addTab("Nothing", new JPanel());

		switch(viewRotationComponentType)
		{
		case AZIMUTHAL_FRESNEL_WEDGE:
			viewRotatingComponentTabbedPane.setSelectedIndex(0);
			break;
		case PIXELATED_AZIMUTHAL_FRESNEL_WEDGE:
			viewRotatingComponentTabbedPane.setSelectedIndex(1);
			break;
		case MOIRE_ROTATOR:
			viewRotatingComponentTabbedPane.setSelectedIndex(2);
			break;
		case RADIAL_LAS:
			viewRotatingComponentTabbedPane.setSelectedIndex(3);
			break;
		case RR_SHEET:
			viewRotatingComponentTabbedPane.setSelectedIndex(4);
			break;
		case CYLINDRICAL_LENS_TELESCOPES:
			viewRotatingComponentTabbedPane.setSelectedIndex(5);
			break;
		case REFRACTIVE_PIXELATED_FRESNEL_WEDGE:
			viewRotatingComponentTabbedPane.setSelectedIndex(6);
			break;
		case NOTHING:
		default:
			viewRotatingComponentTabbedPane.setSelectedIndex(5);
		}

		
				
		//
		// the background panel
		// 
		
		// the tabbed pane that enables selection of the background
		backgroundTabbedPane = new JTabbedPane();
		backgroundComponentCameraTabbedPane.addTab("Background", backgroundTabbedPane);

		studioInitialisationComboBox = new JComboBox<StudioInitialisationType>(StudioInitialisationType.limitedValuesForBackgrounds);
		studioInitialisationComboBox.setSelectedItem(studioInitialisation);
		backgroundTabbedPane.addTab("Standard background", studioInitialisationComboBox);

		JPanel customBackgroundPanel = new JPanel();
		customBackgroundPanel.setLayout(new MigLayout("insets 0"));
		
		
		viewObjectTypeComboBox = new JComboBox<ViewObjectType>(ViewObjectType.values());
		viewObjectTypeComboBox.setSelectedItem(viewObjectType);
		customBackgroundPanel.add(GUIBitsAndBobs.makeRow("View object", viewObjectTypeComboBox), "span");
		
		testImageComboBox = new JComboBox<TestImage>(TestImage.values());
		testImageComboBox.setSelectedItem(testImage);
		customBackgroundPanel.add(GUIBitsAndBobs.makeRow("Test image type", testImageComboBox), "span");
		
		chartTypeComboBox = new JComboBox<ChartType>(ChartType.values());
		chartTypeComboBox.setSelectedItem(chartType);
		customBackgroundPanel.add(GUIBitsAndBobs.makeRow("Snellen chart type", chartTypeComboBox), "span");
		
		objectHeightPanel = new DoublePanel();
		objectHeightPanel.setNumber(objectHeight/M);
		customBackgroundPanel.add(GUIBitsAndBobs.makeRow("Object Height", objectHeightPanel, "m"), "wrap");
		
		objectRotationAnglePanel = new DoublePanel();
		objectRotationAnglePanel.setNumber(objectRotationAngle);
		
		objectDistancePanel = new DoublePanel();
		objectDistancePanel.setNumber(objectDistance/M);
		customBackgroundPanel.add(GUIBitsAndBobs.makeRow("Rotation angle", objectRotationAnglePanel,"<html>&deg;</html>"), "wrap");
		customBackgroundPanel.add(GUIBitsAndBobs.makeRow("Distance", objectDistancePanel, "m"), "wrap");

		backgroundTabbedPane.addTab("Custom lattice", customBackgroundPanel);
		
		if(customBackground) backgroundTabbedPane.setSelectedIndex(1);
		else backgroundTabbedPane.setSelectedIndex(0);
		

		
		//
		// the Camera panel
		//

		JPanel cameraPanel = new JPanel();
		cameraPanel.setLayout(new MigLayout("insets 0"));
		backgroundComponentCameraTabbedPane.addTab("Camera", cameraPanel);

		// camera stuff

		cameraDistancePanel = new DoublePanel();
		cameraDistancePanel.setNumber(cameraDistance/CM);
		cameraPanel.add(GUIBitsAndBobs.makeRow("Camera distance",cameraDistancePanel,"cm" ),"span");

		cameraRotationPanel = new DoublePanel();
		cameraRotationPanel.setNumber(cameraRotation);
		
		rotateViewSystemCheckBox = new JCheckBox();
		rotateViewSystemCheckBox.setSelected(rotateViewSystem);

		cameraPanel.add(GUIBitsAndBobs.makeRow("Camera/eye rotation",cameraRotationPanel,"<html>&deg;</html>. Tick ",rotateViewSystemCheckBox, "to move camera view direction along rotated axis."),"span"); 

		cameraViewDirectionPanel = new LabelledVector3DPanel("View direction");
		cameraViewDirectionPanel.setVector3D(cameraViewDirection);
		cameraPanel.add(cameraViewDirectionPanel, "span");

		upAnglePanel = new DoublePanel();
		upAnglePanel.setNumber(upAngle);
		cameraPanel.add(GUIBitsAndBobs.makeRow("A vertical angle of", upAnglePanel, "°"));

		sideAnglePanel = new DoublePanel();
		sideAnglePanel.setNumber(sideAngle);
		cameraPanel.add(GUIBitsAndBobs.makeRow(" and a horizontal angle of", sideAnglePanel, "°"), "span");

		cameraHorizontalFOVDegPanel = new DoublePanel();
		cameraHorizontalFOVDegPanel.setNumber(cameraHorizontalFOVDeg);
		cameraPanel.add(GUIBitsAndBobs.makeRow("Horizontal FOV", cameraHorizontalFOVDegPanel, "°"), "span");

		cameraApertureSizeComboBox = new JComboBox<ApertureSizeType>(ApertureSizeType.values());
		cameraApertureSizeComboBox.setSelectedItem(cameraApertureSize);
		cameraPanel.add(GUIBitsAndBobs.makeRow("Camera aperture", cameraApertureSizeComboBox));
		
		useEyeballCameraCheckBox = new JCheckBox();
		useEyeballCameraCheckBox.setSelected(useEyeballCamera);
		cameraPanel.add(GUIBitsAndBobs.makeRow("Set camera onto an eye ball",useEyeballCameraCheckBox),"span");
		
		setcameraApertureCheckBox = new JCheckBox();
		setcameraApertureCheckBox.setSelected(setcameraAperture);
		cameraPanel.add(GUIBitsAndBobs.makeRow(setcameraApertureCheckBox,"set camera aperture radius to "));
		
		cameraAperturePanel= new DoublePanel();
		cameraAperturePanel.setNumber(cameraAperture/MM);
		cameraPanel.add(GUIBitsAndBobs.makeRow(cameraAperturePanel,"mm"));
		
		cameraApertureDiffractionCheckBox = new JCheckBox();
		cameraApertureDiffractionCheckBox.setSelected(cameraApertureDiffraction);
		cameraPanel.add(GUIBitsAndBobs.makeRow("aperture diffraction",cameraApertureDiffractionCheckBox),"span");

		cameraFocussingDistancePanel = new DoublePanel();
		cameraFocussingDistancePanel.setNumber(cameraFocussingDistance/M);
		cameraPanel.add(GUIBitsAndBobs.makeRow("Focussing distance",cameraFocussingDistancePanel,"m"));
		
		autoFocusCheckBox = new JCheckBox();
		autoFocusCheckBox.setSelected(autoFocus);
		cameraPanel.add(GUIBitsAndBobs.makeRow("Or auto focus",autoFocusCheckBox),"span");

		anaglyphCameraCheckBox = new JCheckBox();
		anaglyphCameraCheckBox.setSelected(anaglyphCamera);
		cameraPanel.add(GUIBitsAndBobs.makeRow("Set to a cyclodeviated anaglyph camera",anaglyphCameraCheckBox,""));
		
	}

	/**
	 * called before rendering;
	 * override when adding fields
	 */
	protected void acceptValuesInInteractiveControlPanel()
	{
		super.acceptValuesInInteractiveControlPanel();

		switch(viewRotatingComponentTabbedPane.getSelectedIndex())
		{
		case 0:
			viewRotationComponentType = ViewRotationComponentType.AZIMUTHAL_FRESNEL_WEDGE;
			break;
		case 1:
			viewRotationComponentType = ViewRotationComponentType.PIXELATED_AZIMUTHAL_FRESNEL_WEDGE;
			break;
		case 2:
			viewRotationComponentType = ViewRotationComponentType.MOIRE_ROTATOR;
			break;
		case 3:
			viewRotationComponentType = ViewRotationComponentType.RADIAL_LAS;
			break;
		case 4:
			viewRotationComponentType = ViewRotationComponentType.RR_SHEET;
			break;
		case 5:
			viewRotationComponentType = ViewRotationComponentType.CYLINDRICAL_LENS_TELESCOPES;
			break;

		case 6:
			viewRotationComponentType = ViewRotationComponentType.REFRACTIVE_PIXELATED_FRESNEL_WEDGE;
			break;

		case 7:
			viewRotationComponentType = ViewRotationComponentType.NOTHING;
			break;			
		}
		
		switch(backgroundTabbedPane.getSelectedIndex())
		{
		case 0:
			customBackground = false;
			break;
		case 1:
			customBackground = true;
		}

		aFwB = aFwBPanel.getNumber();
		// aFwSimulateHonestly = aFwSimulateHonestlyCheckBox.isSelected();
		aFwN = aFwNPanel.getNumber();
		aFwShowSecondWedge = aFwShowSecondWedgeCheckBox.isSelected();
		aFwSeparation = aFwSeparationPanel.getNumber()*MM;


		aFwBPixel = aFwBPixelPanel.getNumber();
		latticeSpanVector1 = latticeSpanVector1Panel.getVector3D().getProductWith(MM);
		latticeSpanVector2 = latticeSpanVector2Panel.getVector3D().getProductWith(MM);
		diffractiveBlurPixelatedFresnelWedge = diffractiveBlurPixelatedFresnelWedgeCheckBox.isSelected();

		//refractive fresnel wedge
		rotationAngle = rotationAnglePanel.getNumber();
		thickness = thicknessPanel.getNumber()*MM;
		refractiveIndex = refractiveIndexPanel.getNumber();
		magnificationFactor = magnificationFactorPanel.getNumber();
		diffractuveBlurRefractiveFresnelWedge = diffractuveBlurRefractiveFresnelWedgeCheckBox.isSelected();
		refractiveLatticeSpanVector1 = refractiveLatticeSpanVector1Panel.getVector3D().getProductWith(MM);
		refractiveLatticeSpanVector2 = refractiveLatticeSpanVector2Panel.getVector3D().getProductWith(MM);
		boundingBoxCentre = boundingBoxCentrePanel.getVector3D().getProductWith(MM);
		boundingBoxSpanVector1 = boundingBoxSpanVector1Panel.getVector3D().getProductWith(CM);
		boundingBoxSpanVector2 = boundingBoxSpanVector2Panel.getVector3D().getProductWith(CM);
		boundingBoxSpanVector3 = boundingBoxSpanVector3Panel.getVector3D().getProductWith(MM);
		eyePosition = eyePositionPanel.getVector3D().getProductWith(CM);
		surfaceTransmissionCoefficient = surfaceTransmissionCoefficientPanel.getNumber();
		maxTrace = maxTracePanel.getNumber();
		designDistancePlane = designDistancePlanePanel.getVector3D().getProductWith(M);
		movie = movieCheckBox.isSelected();
		numberOfFrames = numberOfFramesPanel.getNumber();
		firstFrame = firstFramePanel.getNumber();
		lastFrame = lastFramePanel.getNumber();
		startSpanVector = startSpanVectorPanel.getNumber()*MM;
		stopSpanVector = stopSpanVectorPanel.getNumber()*MM;
		derivativeControlType = (DerivativeControlType)(derivativeControlTypeComboBox.getSelectedItem());
		derivativeControlThickness = derivativeControlThicknessPanel.getNumber()*MM;
		derivativeControlRotation = derivativeControlRotationPanel.getNumber();

		mmF = mmFPanel.getNumber()*MM;
		mmPitch = mmPitchPanel.getNumber()*MM;
		mmDeltaPhiDeg = mmDeltaPhiDegPanel.getNumber();
		mmShowLA1 = mmShowLA1CheckBox.isSelected();
		mmShowLA2 = mmShowLA2CheckBox.isSelected();

		rLAsF = rLAsFPanel.getNumber()*MM;
		rLAsN = rLAsNPanel.getNumber();
		rLAsDeltaPhiDeg = rLAsDeltaPhiDegPanel.getNumber();
		rLAsShowLA1 = rLAsShowLA1CheckBox.isSelected();
		rLAsShowLA2 = rLAsShowLA2CheckBox.isSelected();

		rrAngleDeg = rrAngleDegPanel.getNumber();
		rrPeriod = rrPeriodPanel.getNumber()*MM;
		rrShowDPA1 = rrShowDPA1CheckBox.isSelected();
		rrShowDPA2 = rrShowDPA2CheckBox.isSelected();

		cltRotationAngleDeg = cltRotationAngleDegPanel.getNumber();
		cltFocalLength = cltFocalLengthPanel.getNumber()*MM;
		cltCylindricalLensType = (CylindricalLensType)(cltCylindricalLensTypeComboBox.getSelectedItem());

		studioInitialisation = (StudioInitialisationType)(studioInitialisationComboBox.getSelectedItem());
		viewObjectType = (ViewObjectType)(viewObjectTypeComboBox.getSelectedItem());
		testImage = (TestImage)(testImageComboBox.getSelectedItem());
		chartType = (ChartType)(chartTypeComboBox.getSelectedItem());
		objectRotationAngle = objectRotationAnglePanel.getNumber();
		objectDistance = objectDistancePanel.getNumber()*M;
		objectHeight = objectHeightPanel.getNumber()*M;

		// cameraViewDirection = cameraViewDirectionPanel.getVector3D();
		anaglyphCamera = anaglyphCameraCheckBox.isSelected();
		cameraRotation = cameraRotationPanel.getNumber();
		cameraDistance = cameraDistancePanel.getNumber()*CM;
		cameraViewDirection = cameraViewDirectionPanel.getVector3D().getNormalised();
		cameraHorizontalFOVDeg = cameraHorizontalFOVDegPanel.getNumber();
		cameraApertureSize = (ApertureSizeType)(cameraApertureSizeComboBox.getSelectedItem());
		setcameraAperture = setcameraApertureCheckBox.isSelected();
		cameraAperture = cameraAperturePanel.getNumber()*MM;
		useEyeballCamera = useEyeballCameraCheckBox.isSelected();
		cameraFocussingDistance = cameraFocussingDistancePanel.getNumber()*M;
		autoFocus = autoFocusCheckBox.isSelected();
		upAngle = upAnglePanel.getNumber();
		sideAngle = sideAnglePanel.getNumber();
		cameraApertureDiffraction = cameraApertureDiffractionCheckBox.isSelected(); 
		rotateViewSystem = rotateViewSystemCheckBox.isSelected();

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
		(new ViewRotationExplorerWithUnits()).run();
	}
}
