package optics.raytrace.research.viewRotation;

import java.awt.BorderLayout;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import math.*;
import net.miginfocom.swing.MigLayout;
import optics.raytrace.sceneObjects.ParametrisedDisc;
import optics.raytrace.sceneObjects.Plane;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.surfaces.AzimuthalPixelatedFresnelWedge;
import optics.raytrace.surfaces.IdealThinCylindricalLensSurfaceSimple;
import optics.raytrace.surfaces.IdealThinSpacePlateSurface;
import optics.raytrace.surfaces.IdealisedDovePrismArray;
import optics.raytrace.surfaces.PhaseHologramOfCylindricalLens;
import optics.raytrace.surfaces.PhaseHologramOfRadialLenticularArray;
import optics.raytrace.surfaces.PhaseHologramOfRectangularLensletArray;
import optics.raytrace.surfaces.RotationallySymmetricPhaseHologram;
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
import optics.raytrace.core.LightSource;
import optics.raytrace.core.SceneObjectClass;
import optics.raytrace.core.Studio;
import optics.raytrace.core.StudioInitialisationType;
import optics.raytrace.core.SurfaceProperty;
import optics.raytrace.core.SurfacePropertyPrimitive;


/**
 * Simulate the visual appearance of various view-rotating components, and the view through them.
 * 
 * @author Johannes Courtial
 */
public class ViewRotationExplorerWithUnitsSpacePlateTest extends NonInteractiveTIMEngine
{
	// units
	public static double NM = 1e-9;
	public static double UM = 1e-6;
	public static double MM = 1e-3;
	public static double CM = 1e-2;
	public static double M = 1;


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
	 * Camera view direction controlled by angles
	 */
	private double sideAngle, upAngle;
	/**
	 * Camera type to be used. anaglyph camera if true else standard. 
	 */
	private boolean anaglyphCamera;

	/**
	 * The eye separationg in the anaglyph case
	 */
	Vector3D eyeSeparation = new Vector3D(60*MM,0,0);

	/**
	 * The centre of the component, usually at origin but changes when using the anaglyph camera
	 */
	Vector3D componentCentre;

	/**
	 * Determines how to initialise the backdrop
	 */
	private StudioInitialisationType studioInitialisation;

	private boolean customBackground;
	private double objectRotationAngle;
	private double objectDistance;

	
	private boolean spacePlate;
	private double extraDistance;

	/**
	 * Constructor.
	 * Sets all parameters.
	 */
	public ViewRotationExplorerWithUnitsSpacePlateTest()
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

		//pixelated Fresnel wedge
		aFwBPixel = -9;
		latticeSpanVector1 = new Vector3D(0.1,0,0).getProductWith(MM);
		latticeSpanVector2 = new Vector3D(0,0.1,0).getProductWith(MM);
		diffractiveBlurPixelatedFresnelWedge = true;

		//refractive pixelated fresnel wedge
		refractiveLatticeSpanVector1 = new Vector3D(0.5,0,0).getProductWith(MM);
		refractiveLatticeSpanVector2 = new Vector3D(0,0.5,0).getProductWith(MM);
		rotationAngle = 10;//degrees
		diffractuveBlurRefractiveFresnelWedge = false;
		boundingBoxCentre = new Vector3D (0,0,1.4999).getProductWith(MM);
		boundingBoxSpanVector1 = new Vector3D(5,0,0).getProductWith(CM);
		boundingBoxSpanVector2 = new Vector3D(0,5,0).getProductWith(CM);
		boundingBoxSpanVector3 = new Vector3D(0,0,3).getProductWith(MM);
		thickness = 1*MM;
		refractiveIndex = 1.5;//glass
		surfaceTransmissionCoefficient = 1;
		maxTrace = 200;


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
		customBackground = true;
		objectRotationAngle = 0;
		objectDistance = 2*M;

		// camera
		anaglyphCamera = false;
		cameraRotation = 10;
		sideAngle = 0;
		upAngle = 0;
		cameraViewCentre = new Vector3D(0, 0, 0);
		cameraDistance = 1.5*CM;
		cameraViewDirection = new Vector3D(0, 0, 1);
		cameraTopDirection = new Vector3D(0,1,0);
		cameraHorizontalFOVDeg = 130;
		cameraApertureSize = ApertureSizeType.EYE;
		cameraFocussingDistance = 1*M;
		
		
		eyePosition = new Vector3D(0,0,-1.5).getProductWith(CM);
		componentCentre = Vector3D.O;
		objectCentre = Vector3D.O;


		if(nonInteractiveTIMAction == NonInteractiveTIMActionEnum.INTERACTIVE)
		{
			windowTitle = "Dr TIM's view-rotation explorer";
			windowWidth = 1500;
			windowHeight = 650;
		}
		
		//space plate
		spacePlate = true;
		extraDistance = 1*CM;
	}


	@Override
	public String getClassName()
	{
		String componentParams = "";
		switch(viewRotationComponentType)
		{
		case AZIMUTHAL_FRESNEL_WEDGE:
			componentParams =
			" aFwB="+aFwB
			+ " aFwN="+aFwN;	// (aFwSimulateHonestly?" aFwN="+aFwN:"");
			break;
		case PIXELATED_AZIMUTHAL_FRESNEL_WEDGE:
			componentParams =
			" aFwB="+aFwBPixel
			+ "latticeSpanVector1=" +latticeSpanVector1
			+ "latticeSpanVector2=" +latticeSpanVector2;
			break;
		case MOIRE_ROTATOR:
			componentParams =
			" mmF="+mmF
			+ " mmPitch="+mmPitch
			+ " mmDeltaPhiDeg="+mmDeltaPhiDeg
			+ " LA1 "+(mmShowLA1?"on":"off")
			+ " LA2 "+(mmShowLA2?"on":"off");
			break;
		case RADIAL_LAS:
			componentParams = 
			" rLAsF="+rLAsF
			+ " rLAsN="+rLAsN
			+ " rLAsDeltaPhiDeg="+rLAsDeltaPhiDeg
			+ " LA1 "+(rLAsShowLA1?"on":"off")
			+ " LA2 "+(rLAsShowLA2?"on":"off");
			break;
		case RR_SHEET:
			componentParams =
			" rrAngleDeg="+rrAngleDeg
			+ " rrPeriod="+rrPeriod
			+ " DPA1 "+(rrShowDPA1?"on":"off")
			+ " DPA2 "+(rrShowDPA2?"on":"off");
			break;
		case CYLINDRICAL_LENS_TELESCOPES:
			componentParams =
			" cltFocalLength="+cltFocalLength
			+ " cltRotationAngleDeg="+cltRotationAngleDeg
			+ " cltCylindricalLensType="+cltCylindricalLensType;
			break;
		case REFRACTIVE_PIXELATED_FRESNEL_WEDGE:
			componentParams = 
			"span vectors="+refractiveLatticeSpanVector1+","+ refractiveLatticeSpanVector2
			+ "rotation Angle="+rotationAngle
			+ "with diffraction ="+diffractuveBlurRefractiveFresnelWedge
			+ "bounding Box centre ="+boundingBoxCentre
			+ "Bounidng Box span vetcors ="	+boundingBoxSpanVector1+","+boundingBoxSpanVector2+","+	boundingBoxSpanVector3
			+ "wedge thickness =" +thickness
			+ "eye positon ="+eyePosition
			+ "refractive index="+refractiveIndex;

		case NOTHING:
			componentParams = "";
			break;
		}

		return "ViewRotationExplorer"	// the name
				+ componentParams
				+ " backdrop="+studioInitialisation.toString()
				+ " cD="+cameraDistance
				+ " cVD="+cameraViewDirection
				+ " cFOV="+cameraHorizontalFOVDeg
				+ " cAS="+cameraApertureSize
				+ " cFD="+cameraFocussingDistance
				;
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
			studio.setLights(LightSource.getStandardLightsFromBehind());
			scene.addSceneObject(SceneObjectClass.getSkySphere(scene, studio));
			scene.addSceneObject(SceneObjectClass.getLighterChequerboardFloor(scene, studio));

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
//			}

		} else {
			// initialise the scene and lights
			StudioInitialisationType.initialiseSceneAndLights(
					studioInitialisation,
					scene,
					studio
					);
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
							objectCentre,	// centre
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
					1,// magnificationFactor,
					refractiveLatticeSpanVector1,
					refractiveLatticeSpanVector2,
					new Plane("focus plane", 
							new Vector3D(0, 0, objectDistance), 
							Vector3D.Z, 
							null,
							null, 
							null
							),
					refractiveIndex,
					thickness,
					surfaceTransmissionCoefficient, //trasnmission coef
					diffractuveBlurRefractiveFresnelWedge,
					maxTrace,
					scene, 
					scene,
					studio
					));

		case NOTHING:
			// don't add anything
			break;
		}
		
		//Add a space plate
		if(spacePlate) {
		scene.addSceneObject( new ParametrisedDisc(
				"Space Plate",	// description
				objectCentre.getSumWith(Vector3D.Z.getProductWith(-cameraDistance/8)),	// centre
				Vector3D.Z,	// normal
				1*CM,	// radius
				new IdealThinSpacePlateSurface(
						objectCentre.getSumWith(Vector3D.Z.getProductWith(-cameraDistance/8)),	// centre
						Vector3D.Z.getProductWith(extraDistance),// surfaceNormal,
						surfaceTransmissionCoefficient,
						false// shadowThrowing
						),	// surfaceProperty
//				new Teleporting( new ParametrisedPlane("destination plane", 
//						objectCentre.getSumWith(Vector3D.Z.getProductWith(+extraDistance-cameraDistance/8)),// 
//						Vector3D.Z.getProductWith(1), 
//						null, null, null
//						)),
				scene,	// parent
				studio				
				)
				);
		}

		// the cameras TODO double check quality factors...
		//eye radius given by: https://hypertextbook.com/facts/2002/AniciaNdabahaliye1.shtml
		//to be about 1.225 cm in radius
		double eyeRadius = 1.225*CM;
		double allRadius = (eyeRadius+cameraDistance);
		if(anaglyphCamera) {
//			By default:
//			cameraPixelsX = 640
//			cameraPixelsY = 480
			int
			pixelsX = cameraPixelsX*(int)renderQuality.getAntiAliasingQuality().getAntiAliasingFactor(),
			pixelsY = cameraPixelsY*(int)renderQuality.getAntiAliasingQuality().getAntiAliasingFactor(),
			raysPerPixel = renderQuality.getBlurQuality().getRaysPerPixel();

			CyclodeviationAnaglyphCamera cyclodeviationAnaglyphCamera = new CyclodeviationAnaglyphCamera(
					"Cyclodeviated anaglyph camera",// name,
					Vector3D.sum(Vector3D.O,Vector3D.Z.getWithLength(-cameraDistance)),// betweenTheEyes,	// middle between two eyes
					Geometry.rotate(Geometry.rotate(new Vector3D(0,0,objectDistance), Vector3D.Y, Math.toRadians(-sideAngle)),Vector3D.X,Math.toRadians(-upAngle)),// centreOfView,	// the point in the centre of both eyes' field of view
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
					cameraApertureSize.getApertureRadius(),// apertureRadius,
					raysPerPixel,// raysPerPixel,
					true// colour
					);
			studio.setCamera(cyclodeviationAnaglyphCamera);
		}else {

			if(sideAngle != 0 || upAngle != 0) {
				//in spherical coordinates where the view along the z axis is upAngle = 0 sideAngle = 0 
				cameraViewDirection = Geometry.rotate(Geometry.rotate(Vector3D.Z, Vector3D.Y, Math.toRadians(-sideAngle)),
						Vector3D.X,	Math.toRadians(-upAngle));
			}

			Vector3D topDirection = Vector3D.Y.getPartPerpendicularTo(cameraViewDirection);
			Vector3D cameraCentre;
			if (cameraApertureSize == ApertureSizeType.EYE) {

				//The eye centre position
				cameraCentre = Vector3D.O.getSumWith(Vector3D.Z.getWithLength(-allRadius));
				cameraViewCentre = cameraCentre.getSumWith(cameraViewDirection.getWithLength(allRadius));
			}else {
				cameraCentre = Vector3D.O.getSumWith(Vector3D.Z.getWithLength(-cameraDistance));
				cameraViewCentre = cameraCentre.getSumWith(cameraViewDirection.getWithLength(cameraDistance));
			}
			cameraTopDirection = Geometry.rotate(topDirection, cameraViewDirection, Math.toRadians(cameraRotation)).getSumWith(cameraCentre); 

			studio.setCamera(getStandardCamera());
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

	//
	// for interactive version
	//

	JTabbedPane viewRotatingComponentTabbedPane, backgroundTabbedPane;

	// azimuthal Fresnel wedge
	private DoublePanel aFwBPanel;
	// private JCheckBox aFwSimulateHonestlyCheckBox;
	private IntPanel aFwNPanel;

	//Pixelated azimuthal fresnel wedge
	private DoublePanel aFwBPixelPanel;
	private Vector3DPanel latticeSpanVector1Panel, latticeSpanVector2Panel;
	private JCheckBox diffractiveBlurPixelatedFresnelWedgeCheckBox;

	//refractive fresnel wedge
	private DoublePanel rotationAnglePanel, thicknessPanel, refractiveIndexPanel, surfaceTransmissionCoefficientPanel;
	private Vector3DPanel refractiveLatticeSpanVector1Panel, refractiveLatticeSpanVector2Panel, boundingBoxCentrePanel, boundingBoxSpanVector1Panel,
	boundingBoxSpanVector2Panel, boundingBoxSpanVector3Panel, eyePositionPanel;
	private JCheckBox diffractuveBlurRefractiveFresnelWedgeCheckBox;
	private IntPanel maxTracePanel;

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
	private DoublePanel objectRotationAnglePanel, objectDistancePanel;

	// camera stuff
	// private LabelledVector3DPanel cameraViewDirectionPanel;
	private DoublePanel cameraDistancePanel, cameraRotationPanel, sideAnglePanel, upAnglePanel;
	private LabelledVector3DPanel cameraViewDirectionPanel;
	private DoublePanel cameraHorizontalFOVDegPanel;
	private JComboBox<ApertureSizeType> cameraApertureSizeComboBox;
	private DoublePanel cameraFocussingDistancePanel;
	private JCheckBox anaglyphCameraCheckBox;

	
	//space Plate#
	
	private DoublePanel extraDistancePanel;
	private JCheckBox  spacePlateCheckBox;



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
						)
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

		refractiveIndexPanel = new DoublePanel();
		refractiveIndexPanel.setNumber(refractiveIndex);
		refractiveFresnelWedgePanel.add(GUIBitsAndBobs.makeRow("refractive index", refractiveIndexPanel),"wrap");

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
		cameraPanel.add(GUIBitsAndBobs.makeRow("Camera/eye rotation",cameraRotationPanel,"<html>&deg;</html>,"),"span"); 

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
		cameraPanel.add(GUIBitsAndBobs.makeRow("Camera aperture", cameraApertureSizeComboBox), "span");		

		cameraFocussingDistancePanel = new DoublePanel();
		cameraFocussingDistancePanel.setNumber(cameraFocussingDistance/M);
		cameraPanel.add(GUIBitsAndBobs.makeRow("Focussing distance",cameraFocussingDistancePanel,"m"),"span");

		anaglyphCameraCheckBox = new JCheckBox();
		anaglyphCameraCheckBox.setSelected(anaglyphCamera);
		cameraPanel.add(GUIBitsAndBobs.makeRow("Set to a cyclodeviated anaglyph camera",anaglyphCameraCheckBox,""));
		

		spacePlateCheckBox = new JCheckBox();
		spacePlateCheckBox.setSelected(spacePlate);
		cameraPanel.add(GUIBitsAndBobs.makeRow("Add a space plate",spacePlateCheckBox,""));
		
		extraDistancePanel = new DoublePanel();
		extraDistancePanel.setNumber(extraDistance/CM);
		cameraPanel.add(GUIBitsAndBobs.makeRow(" Distance added by space plate", extraDistancePanel, "cm"), "span");
	}
	
	//	private JCheckBox  spacePlateCheckBox;
		

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

		aFwBPixel = aFwBPixelPanel.getNumber();
		latticeSpanVector1 = latticeSpanVector1Panel.getVector3D().getProductWith(MM);
		latticeSpanVector2 = latticeSpanVector2Panel.getVector3D().getProductWith(MM);
		diffractiveBlurPixelatedFresnelWedge = diffractiveBlurPixelatedFresnelWedgeCheckBox.isSelected();

		//refractive fresnel wedge
		rotationAngle = rotationAnglePanel.getNumber();
		thickness = thicknessPanel.getNumber()*MM;
		refractiveIndex = refractiveIndexPanel.getNumber();
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
		objectRotationAngle = objectRotationAnglePanel.getNumber();
		objectDistance = objectDistancePanel.getNumber()*M;

		// cameraViewDirection = cameraViewDirectionPanel.getVector3D();
		anaglyphCamera = anaglyphCameraCheckBox.isSelected();
		cameraRotation = cameraRotationPanel.getNumber();
		cameraDistance = cameraDistancePanel.getNumber()*CM;
		cameraViewDirection = cameraViewDirectionPanel.getVector3D();
		cameraHorizontalFOVDeg = cameraHorizontalFOVDegPanel.getNumber();
		cameraApertureSize = (ApertureSizeType)(cameraApertureSizeComboBox.getSelectedItem());
		cameraFocussingDistance = cameraFocussingDistancePanel.getNumber()*M;
		upAngle = upAnglePanel.getNumber();
		sideAngle = sideAnglePanel.getNumber();
		
		spacePlate = spacePlateCheckBox.isSelected();
		extraDistance = extraDistancePanel.getNumber()*CM;

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
		(new ViewRotationExplorerWithUnitsSpacePlateTest()).run();
	}
}
