package optics.raytrace.research.viewRotation;

import java.awt.BorderLayout;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import math.*;
import net.miginfocom.swing.MigLayout;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.surfaces.AzimuthalPixelatedFresnelWedge;
import optics.raytrace.surfaces.IdealThinCylindricalLensSurfaceSimple;
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
import optics.raytrace.GUI.lowLevel.LabelledDoublePanel;
import optics.raytrace.GUI.lowLevel.LabelledVector3DPanel;
import optics.raytrace.GUI.sceneObjects.EditableScaledParametrisedDisc;
import optics.raytrace.core.Studio;
import optics.raytrace.core.StudioInitialisationType;
import optics.raytrace.core.SurfaceProperty;
import optics.raytrace.core.SurfacePropertyPrimitive;


/**
 * Simulate the visual appearance of various view-rotating components, and the view through them.
 * 
 * @author Johannes Courtial
 */
public class ViewRotationExplorer extends NonInteractiveTIMEngine
{
	public enum ViewRotationComponentType
	{
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
	 * Determines how to initialise the backdrop
	 */
	private StudioInitialisationType studioInitialisation;
	
	
	/**
	 * Constructor.
	 * Sets all parameters.
	 */
	public ViewRotationExplorer()
	{
		super();
		
		// set to true for interactive version
		nonInteractiveTIMAction = NonInteractiveTIMActionEnum.INTERACTIVE;
		
		// set all parameters
		
		renderQuality = RenderQualityEnum.DRAFT;
		
		viewRotationComponentType = ViewRotationComponentType.AZIMUTHAL_FRESNEL_WEDGE;
		
		// init parameters

		// azimuthal Fresnel wedge
		aFwB = -0.02;
		// aFwSimulateHonestly = true;
		aFwN = 100;
		
		//pixelated Fresnel wedge
		aFwBPixel = -0.02;
		latticeSpanVector1 = new Vector3D(0.05,0,0);
		latticeSpanVector2 = new Vector3D(0,0.05,0);
		diffractiveBlurPixelatedFresnelWedge = false;
		
		// moiré rotator
		mmF = 0.1;
		mmPitch = 0.01;
		mmDeltaPhiDeg = .1;
		mmShowLA1 = true;
		mmShowLA2 = true;
		
		// complementary radial lenticular arrays
		rLAsF = 0.1;
		rLAsN = 100;
		rLAsDeltaPhiDeg = .1;
		rLAsShowLA1 = true;
		rLAsShowLA2 = true;
		
		// ray-rotation sheet
		rrAngleDeg = 5;
		rrPeriod = 0.01;
		rrShowDPA1 = true;
		rrShowDPA2 = true;
		
		// rotated cylindrical-lens telescopes
		cltFocalLength = 1;
		cltRotationAngleDeg = 5;
		cltCylindricalLensType = CylindricalLensType.IDEAL;

		// rest of scene
		studioInitialisation = StudioInitialisationType.LATTICE;	// the backdrop

		// camera
		cameraViewCentre = new Vector3D(0, 0, 0);
		cameraDistance = 10;
		cameraViewDirection = new Vector3D(0, 0, 1);
		cameraHorizontalFOVDeg = 15;
		cameraApertureSize = ApertureSizeType.PINHOLE;
		cameraFocussingDistance = 20;
		
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
		
		// initialise the scene and lights
		StudioInitialisationType.initialiseSceneAndLights(
				studioInitialisation,
				scene,
				studio
			);
		
		double separation;
		Vector3D centre1, centre2;
		
		switch(viewRotationComponentType)
		{
		case AZIMUTHAL_FRESNEL_WEDGE:
			scene.addSceneObject(
					new EditableScaledParametrisedDisc(
							"Azimuthal Fresnel wedge",	// description
							Vector3D.O,	// centre
							Vector3D.Z,	// normal
							1,	// radius
							new RotationallySymmetricPhaseHologram(
									Vector3D.O,	// centre
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
							Vector3D.O,	// centre
							Vector3D.Z,	// normal
							1,	// radius
							new AzimuthalPixelatedFresnelWedge(
									Vector3D.O,	// centre
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
									true	// shadowThrowing
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
							centre1,	// centre
							Vector3D.Z,	// normal
							1,	// radius
							new PhaseHologramOfRectangularLensletArray(
									centre1,
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
							centre2,	// centre
							Vector3D.Z,	// normal
							1,	// radius
							new PhaseHologramOfRectangularLensletArray(
									centre2,
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
							centre1,	// centre
							Vector3D.Z,	// normal
							1,	// radius
							new PhaseHologramOfRadialLenticularArray(
									centre1,	// centre
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
							centre2,	// centre
							Vector3D.Z,	// normal
							1,	// radius
							new PhaseHologramOfRadialLenticularArray(
									centre2,	// centre
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
							centre1,	// centre
							Vector3D.Z,	// normal
							1,	// radius
							new IdealisedDovePrismArray(
									centre1,	// dovePrism0Centre
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
							centre2,	// centre
							Vector3D.Z,	// normal
							1,	// radius
							new IdealisedDovePrismArray(
									centre2,	// dovePrism0Centre
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
//			scene.addSceneObject(
//					new EditableScaledParametrisedDisc(
//							"Ray-rotation sheet",	// description
//							Vector3D.O,	// centre
//							Vector3D.Z,	// normal
//							1,	// radius
//							new RayRotating(
//									MyMath.deg2rad(rrSheetAlphaDeg),
//									SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT,
//									true	// shadowThrowing
//								),	// surfaceProperty
//							scene,	// parent
//							studio
//							)
//					);
//			break;
		case CYLINDRICAL_LENS_TELESCOPES:
			double cltRotationAngleRad2 = 0.5*MyMath.deg2rad(cltRotationAngleDeg);
			Vector3D centre11 = new Vector3D(0, 0, -MyMath.TINY-2*cltFocalLength);
			// Vector3D centre11 = new Vector3D(0, 0, -MyMath.TINY-0.5*cltFocalLength);
			scene.addSceneObject(
					new EditableScaledParametrisedDisc(
							"telescope 1, cylindrical lens 1",	// description
							centre11,	// centre
							Vector3D.Z,	// normal
							1,	// radius
							getCylindricalLensSurface(
									centre11,	// principalPoint,
									new Vector3D(Math.cos(cltRotationAngleRad2), Math.sin(cltRotationAngleRad2), 0)	// phaseGradientDirection
								),	// surfaceProperty
							scene,	// parent
							studio
							)
					);
			Vector3D centre12 = new Vector3D(0, 0, -MyMath.TINY);
			// Vector3D centre12 = new Vector3D(0, 0, -MyMath.TINY+0.5*cltFocalLength);
			scene.addSceneObject(
					new EditableScaledParametrisedDisc(
							"telescope 1, cylindrical lens 2",	// description
							centre12,	// centre
							Vector3D.Z,	// normal
							1,	// radius
							getCylindricalLensSurface(
									centre12,	// principalPoint,
									new Vector3D(Math.cos(cltRotationAngleRad2), Math.sin(cltRotationAngleRad2), 0)	// phaseGradientDirection
								),	// surfaceProperty
							scene,	// parent
							studio
							)
					);
			Vector3D centre21 = new Vector3D(0, 0, +MyMath.TINY+2*cltFocalLength);
			// Vector3D centre21 = new Vector3D(0, 0, +MyMath.TINY-0.5*cltFocalLength);
			scene.addSceneObject(
					new EditableScaledParametrisedDisc(
							"telescope 2, cylindrical lens 1",	// description
							centre21,	// centre
							Vector3D.Z,	// normal
							1,	// radius
							getCylindricalLensSurface(
									centre21,	// principalPoint,
									new Vector3D(Math.cos(-cltRotationAngleRad2), Math.sin(-cltRotationAngleRad2), 0)	// phaseGradientDirection
								),	// surfaceProperty
							scene,	// parent
							studio
							)
					);
			Vector3D centre22 = new Vector3D(0, 0, MyMath.TINY);
			// Vector3D centre22 = new Vector3D(0, 0, +MyMath.TINY+0.5*cltFocalLength);
			scene.addSceneObject(
					new EditableScaledParametrisedDisc(
							"telescope 2, cylindrical lens 2",	// description
							centre22,	// centre
							Vector3D.Z,	// normal
							1,	// radius
							getCylindricalLensSurface(
									centre22,	// principalPoint,
									new Vector3D(Math.cos(-cltRotationAngleRad2), Math.sin(-cltRotationAngleRad2), 0)	// phaseGradientDirection
								),	// surfaceProperty
							scene,	// parent
							studio
							)
					);
			break;
		case NOTHING:
			// don't add anything
			break;
		}

		// the camera
		studio.setCamera(getStandardCamera());
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
		
	JTabbedPane viewRotatingComponentTabbedPane;
	// JTabbedPane windowTabbedPane;

	// azimuthal Fresnel wedge
	private DoublePanel aFwBPanel;
	// private JCheckBox aFwSimulateHonestlyCheckBox;
	private IntPanel aFwNPanel;
	
	//Pixelated azimuthal fresnel wedge
	private DoublePanel aFwBPixelPanel;
	private LabelledVector3DPanel latticeSpanVector1Panel, latticeSpanVector2Panel;
	private JCheckBox diffractiveBlurPixelatedFresnelWedgeCheckBox;
	
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

	// camera stuff
	// private LabelledVector3DPanel cameraViewDirectionPanel;
	private LabelledDoublePanel cameraDistancePanel;
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
		// the component panel
		//

		JPanel scenePanel = new JPanel();
		scenePanel.setLayout(new BorderLayout());
		sceneCameraTabbedPane.addTab("Scene", scenePanel);
		
		
		
		// the tabbed pane that enables selection of the view-rotating component
		viewRotatingComponentTabbedPane = new JTabbedPane();
		scenePanel.add(viewRotatingComponentTabbedPane, BorderLayout.CENTER);

		



		// ray-rotation sheet
		
		JPanel rayRotationSheetPanel = new JPanel();
		// rayRotationSheetPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Ray-rotation sheet"));
		rayRotationSheetPanel.setLayout(new MigLayout("insets 0"));

		rayRotationSheetPanel.add(new JLabel("Two Dove-prism arrays,"), "wrap");

		rrPeriodPanel = new DoublePanel();
		rrPeriodPanel.setNumber(rrPeriod);
		rrPeriodPanel.setToolTipText("Period of each of the two Dove-prism arrays");
		rayRotationSheetPanel.add(GUIBitsAndBobs.makeRow("each with period", rrPeriodPanel, ","), "span");

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
		cltFocalLengthPanel.setNumber(cltFocalLength);
		cylindricalLensTelescopesPanel.add(
				GUIBitsAndBobs.makeRow("each comprising cylindrical lenses of focal length", cltFocalLengthPanel, ","), "wrap");

		cltCylindricalLensTypeComboBox = new JComboBox<CylindricalLensType>(CylindricalLensType.values());
		cltCylindricalLensTypeComboBox.setSelectedItem(cltCylindricalLensType);
		cylindricalLensTelescopesPanel.add(GUIBitsAndBobs.makeRow("realised as", cltCylindricalLensTypeComboBox), "wrap");

		viewRotatingComponentTabbedPane.addTab("Cylindrical-lens telescopes", cylindricalLensTelescopesPanel);


		// nothing
		
		viewRotatingComponentTabbedPane.addTab("Nothing", new JPanel());

		
		studioInitialisationComboBox = new JComboBox<StudioInitialisationType>(StudioInitialisationType.limitedValuesForBackgrounds);
		studioInitialisationComboBox.setSelectedItem(studioInitialisation);
		scenePanel.add(GUIBitsAndBobs.makeRow("Background", studioInitialisationComboBox), BorderLayout.SOUTH);
		

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
		
		cameraDistancePanel = new LabelledDoublePanel("Camera distance");
		cameraDistancePanel.setNumber(cameraDistance);
		cameraPanel.add(cameraDistancePanel, "span");
		
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
			viewRotationComponentType = ViewRotationComponentType.NOTHING;
			break;

		}

		aFwB = aFwBPanel.getNumber();
		// aFwSimulateHonestly = aFwSimulateHonestlyCheckBox.isSelected();
		aFwN = aFwNPanel.getNumber();
		
		aFwBPixel = aFwBPixelPanel.getNumber();
		latticeSpanVector1 = latticeSpanVector1Panel.getVector3D();
		latticeSpanVector2 = latticeSpanVector2Panel.getVector3D();
		diffractiveBlurPixelatedFresnelWedge = diffractiveBlurPixelatedFresnelWedgeCheckBox.isSelected();

		mmF = mmFPanel.getNumber();
		mmPitch = mmPitchPanel.getNumber();
		mmDeltaPhiDeg = mmDeltaPhiDegPanel.getNumber();
		mmShowLA1 = mmShowLA1CheckBox.isSelected();
		mmShowLA2 = mmShowLA2CheckBox.isSelected();
		
		rLAsF = rLAsFPanel.getNumber();
		rLAsN = rLAsNPanel.getNumber();
		rLAsDeltaPhiDeg = rLAsDeltaPhiDegPanel.getNumber();
		rLAsShowLA1 = rLAsShowLA1CheckBox.isSelected();
		rLAsShowLA2 = rLAsShowLA2CheckBox.isSelected();

		rrAngleDeg = rrAngleDegPanel.getNumber();
		rrPeriod = rrPeriodPanel.getNumber();
		rrShowDPA1 = rrShowDPA1CheckBox.isSelected();
		rrShowDPA2 = rrShowDPA2CheckBox.isSelected();

		cltRotationAngleDeg = cltRotationAngleDegPanel.getNumber();
		cltFocalLength = cltFocalLengthPanel.getNumber();
		cltCylindricalLensType = (CylindricalLensType)(cltCylindricalLensTypeComboBox.getSelectedItem());

		studioInitialisation = (StudioInitialisationType)(studioInitialisationComboBox.getSelectedItem());
		
		// cameraViewDirection = cameraViewDirectionPanel.getVector3D();
		cameraDistance = cameraDistancePanel.getNumber();
		cameraViewDirection = cameraViewDirectionPanel.getVector3D();
		cameraHorizontalFOVDeg = cameraHorizontalFOVDegPanel.getNumber();
		cameraApertureSize = (ApertureSizeType)(cameraApertureSizeComboBox.getSelectedItem());
		cameraFocussingDistance = cameraFocussingDistancePanel.getNumber();
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
		(new ViewRotationExplorer()).run();
	}
}
