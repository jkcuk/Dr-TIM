package optics.raytrace.research.FOVSteering;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import math.*;
import net.miginfocom.swing.MigLayout;
import optics.raytrace.sceneObjects.Plane;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.surfaces.GeneralisedConfocalLensletArrays;
import optics.raytrace.surfaces.SurfaceColour;
import optics.raytrace.surfaces.transmissionCoefficient.GCLAsTransmissionCoefficientCalculator;
import optics.raytrace.surfaces.transmissionCoefficient.OptimisedGCLAsTransmissionCoefficientCalculator;
import optics.raytrace.utility.CoordinateSystems.GlobalOrLocalCoordinateSystemType;
import optics.raytrace.core.*;
import optics.raytrace.exceptions.SceneException;
import optics.raytrace.NonInteractiveTIMActionEnum;
import optics.raytrace.NonInteractiveTIMEngine;
import optics.raytrace.GUI.cameras.EditableRelativisticAnyFocusSurfaceCamera;
import optics.raytrace.GUI.cameras.RenderQualityEnum;
import optics.raytrace.GUI.lowLevel.ApertureSizeType;
import optics.raytrace.GUI.lowLevel.GUIBitsAndBobs;
import optics.raytrace.GUI.lowLevel.LabelledDoublePanel;
import optics.raytrace.GUI.lowLevel.LabelledVector2DPanel;
import optics.raytrace.GUI.lowLevel.LabelledVector3DPanel;
import optics.raytrace.cameras.PinholeCamera.ExposureCompensationType;
import optics.raytrace.GUI.sceneObjects.EditableFramedRectangle;


/**
 * Simulate the view through GCLAs with different parameters describing the aperture centres.
 * 
 * @author Johannes Courtial
 */
public class TransmissionCoefficientVisualiser extends NonInteractiveTIMEngine
{
	// parameters describing the GCLAs
	
	/**
	 * the position on the side of array `1' through which the light rays pass for which transmission is optimised
	 */
	private Vector3D pointAtFOVCentre;
	
	/**
	 * "parallelity" parameter that describes how to optimise the aperture placement;
	 * it describes how parallel the line through the aperture centres is
	 * to the line through the principal points:
	 * p=1 means it is parallel, 
	 * p=0 means it is parallel to the a axis (and not the line through the principal points) 
	 */
	private double parallelityParameter;
	
	/**
	 * unit vector in the direction of the optical axis of the two lenslets in each telescopelet, pointing from the plane of the first lenslets to that of the second
	 */
	private Vector3D aHat;
	
	/**
	 * unit vector in the first transverse direction; note that the sides of the parallelogram-shaped lenslet apertures are aligned with the <u> and <v> directions
	 */
	private Vector3D uHat;
	
	/**
	 * unit vector in the second transverse direction; note that the sides of the parallelogram-shaped lenslet apertures are aligned with the <u> and <v> directions
	 */
	private Vector3D vHat;

	/**
	 * -f_2u/f_1u, i.e. negative ratio of focal lengths of 2nd and first lenses in the (a,u) projection [1,5]
	 */
	private double etaU;

	/**
	 * -f_2v/f_1v, i.e. negative ratio of focal lengths of 2nd and first lenses in the (a,v) projection [1,5]
	 */
	private double etaV;

	/**
	 * deltaU = dU / f1, i.e. the offset in the <u> direction of the optical axis of the 2nd lenslet from that of the first, dU, divided by f1
	 * (see Eqn (4) in [1])
	 */
	private double deltaU;
	
	/**
	 * deltaV = dV / f1, i.e. the offset in the <v> direction of the optical axis of the 2nd lenslet from that of the first, dV, divided by f1
	 * (see Eqn (4) in [1])
	 */
	private double deltaV;

	/**
	 * aperture width in the <u> direction of the 1st lenslet, divided by f1
	 */
	private double sigma1U;

	/**
	 * aperture width in the <v> direction of the 1st lenslet, divided by f1
	 */
	private double sigma1V;

	/**
	 * aperture width in the <u> direction of the 2nd lenslet, divided by f1
	 */
	private double sigma2U;
	
	/**
	 * aperture width in the <v> direction of the 2nd lenslet, divided by f1
	 */
	private double sigma2V;
	
	private double alpha1U;
	private double alpha1V;
	private double alpha2U;
	private double alpha2V;
	
	public enum AlphaChoice {
		ZERO("<html>All &alpha;<sub>*</sub> = 0</html>"),	// all alphas are zero
		MANUAL("Manual"),
		OPTIMISED("Optimised for viewing position");
		
		private String description;
		private AlphaChoice(String description) {this.description = description;}
		@Override
		public String toString() {return description;}
		
		public AlphaChoice string2AlphaChoice(String string) {
			if(string == ZERO.toString()) return ZERO;
			if(string == MANUAL.toString()) return MANUAL;
			return OPTIMISED;
		}
	}
	
	/**
	 * determines how to calculate the alpha coefficients of the GCLAs
	 */
	private AlphaChoice alphaChoice;
	
	// parameters describing the camera and the lights
	
	/**
	 * the centre of the camera's aperture
	 */
	private Vector3D apertureCentre;
	
	/**
	 * the lights' brightness factor (1=normal)
	 */
	private double brightnessFactor;


	
	
	/**
	 * Constructor.
	 * Sets all parameters.
	 */
	public TransmissionCoefficientVisualiser()
	{
		super();
		
		// set to true for interactive version
		nonInteractiveTIMAction = NonInteractiveTIMActionEnum.INTERACTIVE;
		
		// set all parameters
		
		renderQuality = RenderQualityEnum.DRAFT;
		
		aHat = new Vector3D(0, 0, 1);
		uHat = new Vector3D(1, 0, 0);
		vHat = new Vector3D(0, 1, 0);
		etaU = -0.2;
		etaV = -0.1;
		deltaU = 0.6;	// 0.2;
		deltaV = 0.1;	// -0.4;
		sigma1U = 1;
		sigma1V = 1;
		sigma2U = 1;
		sigma2V = 1;
		
		alphaChoice =
				AlphaChoice.ZERO;
//				AlphaChoice.MANUAL;
//				AlphaChoice.OPTIMISED;

		// AlphaChoice.MANUAL parameters
		alpha1U = 0;
		alpha1V = 0;
		alpha2U = -0.15;	// -0.6;
		alpha2V = -0.15;	// -0.1;

		double distanceInFront = 2;	// 0.8;
		apertureCentre =
				new Vector3D(0, 0, 10-distanceInFront);	// straight in front
//				new Vector3D(1, 0, 10-distanceInFront);	// a bit to the right
//				new Vector3D(10, 0, 0);	// 45 degrees off to the right
		cameraHorizontalFOVDeg = 2*MyMath.rad2deg(Math.atan(10./distanceInFront*Math.tan(MyMath.deg2rad(10))));
		brightnessFactor = 1;
		
		// AlphaChoice.OPTIMISED parameters
		pointAtFOVCentre =
				new Vector3D(0, 0, 8);	// straight in front
//				apertureCentre;	// always optimised for camera position
//				new Vector3D(0, 0, -1000);	// 45 degrees off to the right
		parallelityParameter = 0;


		
		System.out.println("TransmissionCoefficientVisualiser::TransmissionCoefficientVisualiser: F-numbers are as follows:" +
				"N_1u=" + 1/sigma1U +
				", N_1v=" + 1/sigma1V +
				", N_2u=" + Math.abs(etaU)/sigma2U +
				", N_2v=" + Math.abs(etaV)/sigma2V
			);
		
		// boring parameters
		nonInteractiveTIMAction = NonInteractiveTIMActionEnum.INTERACTIVE;

		// for movie version
		numberOfFrames = 50;
		firstFrame = 0;
		lastFrame = numberOfFrames-1;
		
		// camera parameters are set in populateStudio() method
		
		// blur quality if test = false; make this better in the non-interactive version, as this is used to produce publication-quality images
		// standardCameraBlurQuality = (interactive?QualityType.GOOD:QualityType.SUPER);	// this is the camera's blur quality if test=false
		
		// for interactive version
		windowTitle = "Dr TIM's telescope-window transmission-coefficient visualiser";
		windowWidth = 1200;
		windowHeight = 650;
	}
	

	@Override
	public String getClassName()
	{
		return "TransmissionCoefficientVisualiser"	// the name
				+ ((alphaChoice == AlphaChoice.ZERO)?" alpha=0":"")
				+ ((alphaChoice == AlphaChoice.MANUAL)?" alpha1U="+alpha1U+" alpha1V="+alpha1V+" alpha2U="+alpha2U+" alpha2V="+alpha2V:"")
				+ ((alphaChoice == AlphaChoice.OPTIMISED)?" pointAtFOVCentre "+pointAtFOVCentre:"")
				+ ((alphaChoice == AlphaChoice.OPTIMISED)?" parallelityParameter "+parallelityParameter:"")
				+ " apertureCentre "+apertureCentre
				+ " brightnessFactor "+brightnessFactor
				;
	}
	
	
	@Override
	public void populateStudio()
	throws SceneException
	{
		// super.populateSimpleStudio();

		studio = new Studio();

		// the scene
		SceneObjectContainer scene = new SceneObjectContainer("the scene", null, studio);
		studio.setScene(scene);

		// the standard scene objects
		// scene.addSceneObject(SceneObjectClass.getHeavenSphere(scene, studio));
		scene.addSceneObject(SceneObjectClass.getSkySphere(brightnessFactor, scene, studio));
		scene.addSceneObject(SceneObjectClass.getLighterChequerboardFloor(scene, studio));
				
		// add any other scene objects
		
		
		GeneralisedConfocalLensletArrays gCLAs = new GeneralisedConfocalLensletArrays(
				aHat,	// a
				uHat,	// u
				vHat,	// v
				etaU,
				etaV,
				deltaU,
				deltaV,
				GlobalOrLocalCoordinateSystemType.GLOBAL_BASIS,	// basis
				null,	// transmissionCoefficientCalculator
				null,	// pixellation
				false	// shadowThrowing
			);
		switch(alphaChoice)
		{
		case ZERO:
			gCLAs.setTransmissionCoefficientCalculator(
					new GCLAsTransmissionCoefficientCalculator(
							gCLAs,	// generalisedConfocalLensletArrays
							sigma1U,
							sigma1V,
							sigma2U,
							sigma2V,
							0, 0, 0, 0	// alpha1U, alpha1V, alpha2U, alpha2V
						)
				);
			break;
		case MANUAL:
			gCLAs.setTransmissionCoefficientCalculator(
					new GCLAsTransmissionCoefficientCalculator(
							gCLAs,	// generalisedConfocalLensletArrays
							sigma1U,
							sigma1V,
							sigma2U,
							sigma2V,
							alpha1U, alpha1V, alpha2U, alpha2V
						)
				);
			break;
		case OPTIMISED:
		default:
			gCLAs.setTransmissionCoefficientCalculator(
					new OptimisedGCLAsTransmissionCoefficientCalculator(
							gCLAs,	// generalisedConfocalLensletArrays
							sigma1U,
							sigma1V,
							sigma2U,
							sigma2V,
							parallelityParameter,
							pointAtFOVCentre
						)
				);
		}
		EditableFramedRectangle gCLAsWindow = new EditableFramedRectangle(
				"GCLAs",	// description
				new Vector3D(-1, -1, 10),	// corner
				new Vector3D(2, 0, 0),	// widthVector
				new Vector3D(0, 2, 0),	// heightVector
				0.01,	// radius
				gCLAs,	// windowSurfaceProperty
				SurfaceColour.GREY50_MATT,	// frameSurfaceProperty
				true,	// showFrames
				scene,	// parent
				studio
		);
		scene.addSceneObject(gCLAsWindow);


		
		// define the camera
		//
		// Note that the view direction and basis Vector3Ds of the detector are chosen such that
		// the x, y, z axes form a LEFT-handed coordinate system.
		// The reason is that, in the photo, the positive x direction is then to the right,
		// the positive y direction is upwards, and the camera looks in the positive z direction.
		int
		pixelsX = 640,
		pixelsY = 480;
		// If antiAliasingFactor is set to N, the image is calculated at resolution
		// N*pixelsX x N*pixelsY.

		
		
		CameraClass camera;

		// focus on the plane of the GCLAs
		SceneObject focusScene = new Plane(
				"focussing plane",
				gCLAsWindow.getCorner(),	// point on plane
				gCLAsWindow.getPane().getNormalisedOutwardsSurfaceNormal(gCLAsWindow.getCorner()),
				// ((EditableScaledParametrisedCentredParallelogram)gCLAsWindow.getFirstSceneObjectWithDescription("window", false)).getNormalisedOutwardsSurfaceNormal(gCLAsWindow.getCorner()),	// normal to plane
				(SurfaceProperty)null,
				null,	// parent
				Studio.NULL_STUDIO
				);

		camera = new EditableRelativisticAnyFocusSurfaceCamera(
				"Camera",
				apertureCentre,	// centre of aperture
				Vector3D.difference(new Vector3D(0, 0, 10), apertureCentre),	// viewDirection
				new Vector3D(0, 1, 0),	// top direction vector
				cameraHorizontalFOVDeg,	// horiontalViewAngle in degrees; 2*MyMath.rad2deg(Math.atan(2./10.)) gives same view angle as in previous version
				new Vector3D(0, 0, 0),	// beta
				pixelsX, pixelsY,	// logical number of pixels
				ExposureCompensationType.EC0,	// exposure compensation +0
				100,	// maxTraceLevel
				focusScene,
				null,	// cameraFrameScene,
				ApertureSizeType.SMALL,	// aperture size
				renderQuality.getBlurQuality(),	// blur quality
				renderQuality.getAntiAliasingQuality()	// anti-aliasing quality
				);

		studio.setLights(LightSource.getStandardLightsFromBehind(brightnessFactor));
		studio.setCamera(camera);
	}

	
	
	//
	// for interactive version
	//
	
	private LabelledVector3DPanel pointAtFOVCentrePanel;
//	private LabelledDoublePanel parallelityParameterPanel;
	private LabelledVector3DPanel aHatPanel;
	private LabelledVector3DPanel uHatPanel;
	private LabelledVector3DPanel vHatPanel;
	private LabelledVector2DPanel etaPanel;
	private LabelledVector2DPanel deltaPanel;
	private LabelledVector2DPanel sigma1Panel;
	private LabelledVector2DPanel sigma2Panel;
	private LabelledVector2DPanel alpha1Panel;
	private LabelledVector2DPanel alpha2Panel;
//	private JComboBox alphaChoiceComboBox;
	private LabelledVector3DPanel apertureCentrePanel;
	private LabelledDoublePanel brightnessFactorPanel;
	
	JTabbedPane apertureParametersTabbedPane;
	JPanel manualParametersPanel, optimisedParametersPanel;
	JLabel zeroParametersLabel;

//	Vector3D pointAtFOVCentre;
//	double parallelityParameter;
//	private Vector3D aHat;
//	private Vector3D uHat;
//	private Vector3D vHat;
//	private double etaU;
//	private double etaV;
//	private double deltaU;
//	private double deltaV;
//	private double sigma1U;
//	private double sigma1V;
//	private double sigma2U;
//	private double sigma2V;	
//	private double alpha1U;
//	private double alpha1V;
//	private double alpha2U;
//	private double alpha2V;
//	private AlphaChoice alphaChoice;
//	Vector3D apertureCentre;
//	double brightnessFactor;

	
	/**
	 * add controls to the interactive control panel;
	 * override to modify
	 */
	@Override
	protected void createInteractiveControlPanel()
	{
		super.createInteractiveControlPanel();
		
		// create a tabbed pane that contains the telescope-window controls
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.setBorder(GUIBitsAndBobs.getTitledBorder("Telescope window"));
		interactiveControlPanel.add(tabbedPane, "span");
		
		// refraction-parameter tab
		JPanel refractionParametersPanel = new JPanel();
		refractionParametersPanel.setLayout(new MigLayout("insets 0"));
		tabbedPane.add("Refraction", refractionParametersPanel);
		
		aHatPanel = new LabelledVector3DPanel(
				// "aHat = "
				"<html><b>a</b>&#x302; = </html>"
			); // "aHat"
		aHatPanel.setToolTipText("Direction of optical axis of telescopelets");
		aHatPanel.setVector3D(aHat);
		refractionParametersPanel.add(aHatPanel, "span");

		uHatPanel = new LabelledVector3DPanel(
				// "uHat = "
				"<html><b>u</b>&#x302; = </html>"
			);	// "uHat"
		uHatPanel.setToolTipText(
				// "First direction normal to aHat"
				"<html>First direction normal to <b>a</b>&#x302;</html>"
			);
		uHatPanel.setVector3D(uHat);
		refractionParametersPanel.add(uHatPanel, "span");

		vHatPanel = new LabelledVector3DPanel(
				// "vHat = "
				"<html><b>v</b>&#x302; = </html>"
			);	// "vHat"
		vHatPanel.setToolTipText(
				// "Second direction normal to aHat"
				"<html>Second direction normal to <b>a</b>&#x302;</html>"
			);
		vHatPanel.setVector3D(vHat);
		refractionParametersPanel.add(vHatPanel, "span");

		etaPanel = new LabelledVector2DPanel(
				"<html>(<i>&eta;</i><sub><i>u</i></sub>, <i>&eta;</i><sub><i>v</i></sub>) = </html>"
			);
		etaPanel.setToolTipText(
				"<html><i>&eta;</i><sub><i>i</i></sub> = (-1) times focal-length ratio of telescopelets in <i>i</i> direction</html>"
			);
		etaPanel.setVector2D(etaU, etaV);
		refractionParametersPanel.add(etaPanel, "span");

		deltaPanel = new LabelledVector2DPanel(
				"<html>(<i>&delta;</i><sub><i>u</i></sub>, <i>&delta;</i><sub><i>v</i></sub>) = </html>"
			);
		deltaPanel.setToolTipText(
				"<html><i>&delta;</i><sub><i>i</i></sub> = (offset between the two lenslets' optical axes in <i>i</i> direction)/(<i>f</i><sub>1</sub>)"
			);
		deltaPanel.setVector2D(deltaU, deltaV);
		refractionParametersPanel.add(deltaPanel, "span");

		// aperture-parameter tab
		apertureParametersTabbedPane = new JTabbedPane();
		tabbedPane.add("Clear-aperture placement", apertureParametersTabbedPane);

		// this is another tabbed pane, which decides on the algorithm that is used to set the aperture parameters, and sets any parameters
		
		// zero tab
		zeroParametersLabel = new JLabel("No additional parameters");
		apertureParametersTabbedPane.add(AlphaChoice.ZERO.toString(), zeroParametersLabel);

		// manual tab
		manualParametersPanel = new JPanel();
		manualParametersPanel.setLayout(new MigLayout("insets 0"));
		apertureParametersTabbedPane.add(AlphaChoice.MANUAL.toString(), manualParametersPanel);
		
		sigma1Panel = new LabelledVector2DPanel(
				"<html>(<i>&sigma;</i><sub><i>1u</i></sub>, <i>&sigma;</i><sub><i>1v</i></sub>) = </html>"
			);
		sigma1Panel.setToolTipText("sigma_1i = ...");
		sigma1Panel.setVector2D(sigma1U, sigma1V);
		manualParametersPanel.add(sigma1Panel, "span");

		sigma2Panel = new LabelledVector2DPanel(
				"<html>(<i>&sigma;</i><sub><i>2u</i></sub>, <i>&sigma;</i><sub><i>2v</i></sub>) = </html>"
			);
		sigma2Panel.setToolTipText("sigma_2i = ...");
		sigma2Panel.setVector2D(sigma2U, sigma2V);
		manualParametersPanel.add(sigma2Panel, "span");
		
		alpha1Panel = new LabelledVector2DPanel(
				"<html>(<i>&alpha;</i><sub><i>1u</i></sub>, <i>&alpha;</i><sub><i>1v</i></sub>) = </html>"
			);
		alpha1Panel.setToolTipText("alpha_1i = ...");
		alpha1Panel.setVector2D(alpha1U, alpha1V);
		manualParametersPanel.add(alpha1Panel, "span");

		alpha2Panel = new LabelledVector2DPanel(
				"<html>(<i>&alpha;</i><sub><i>2u</i></sub>, <i>&alpha;</i><sub><i>2v</i></sub>) = </html>"
			);
		alpha2Panel.setToolTipText("alpha_2i = ...");
		alpha2Panel.setVector2D(alpha2U, alpha2V);
		manualParametersPanel.add(alpha2Panel, "span");

		// optimised tab
		optimisedParametersPanel = new JPanel();
		optimisedParametersPanel.setLayout(new MigLayout("insets 0"));
		apertureParametersTabbedPane.add(AlphaChoice.OPTIMISED.toString(), optimisedParametersPanel);

		pointAtFOVCentrePanel = new LabelledVector3DPanel("Point at FOV centre");
		pointAtFOVCentrePanel.setVector3D(pointAtFOVCentre);
		optimisedParametersPanel.add(pointAtFOVCentrePanel, "span");
		
//		LabelledDoublePanel parallelityParameterPanel;
//		JComboBox alphaChoiceComboBox;
		
		switch(alphaChoice)
		{
		case ZERO:
			apertureParametersTabbedPane.setSelectedComponent(zeroParametersLabel);
			break;
		case MANUAL:
			apertureParametersTabbedPane.setSelectedComponent(manualParametersPanel);
			break;
		case OPTIMISED:
		default:
			apertureParametersTabbedPane.setSelectedComponent(optimisedParametersPanel);
		}
		
		// the camera
		
		JPanel cameraPanel = new JPanel();
		cameraPanel.setLayout(new MigLayout("insets 0"));
		cameraPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Camera"));
		interactiveControlPanel.add(cameraPanel, "span");
		
		apertureCentrePanel = new LabelledVector3DPanel("Aperture centre = ");
		apertureCentrePanel.setToolTipText("Aperture centre; framed sheet is centred at (0, 0, 10)");
		apertureCentrePanel.setVector3D(apertureCentre);
		cameraPanel.add(apertureCentrePanel, "span");
		
		brightnessFactorPanel = new LabelledDoublePanel("Brightness factor (i.e. exposure compensation) = ");
		brightnessFactorPanel.setToolTipText("Factor by which light intensity appears brighter");
		brightnessFactorPanel.setNumber(brightnessFactor);
		cameraPanel.add(brightnessFactorPanel, "span");
	}
	
	/**
	 * called before rendering;
	 * override when adding fields
	 */
	protected void acceptValuesInInteractiveControlPanel()
	{
		super.acceptValuesInInteractiveControlPanel();
		
		aHat = aHatPanel.getVector3D();
		uHat = uHatPanel.getVector3D();
		vHat = vHatPanel.getVector3D();
		etaU = etaPanel.getVector2D().x;
		etaV = etaPanel.getVector2D().y;
		deltaU = deltaPanel.getVector2D().x;
		deltaV = deltaPanel.getVector2D().y;
		
		if(apertureParametersTabbedPane.getSelectedComponent().equals(zeroParametersLabel)) alphaChoice = AlphaChoice.ZERO;
		else if(apertureParametersTabbedPane.getSelectedComponent().equals(manualParametersPanel)) alphaChoice = AlphaChoice.MANUAL;
		else if(apertureParametersTabbedPane.getSelectedComponent().equals(optimisedParametersPanel)) alphaChoice = AlphaChoice.OPTIMISED;

		sigma1U = sigma1Panel.getVector2D().x;
		sigma1V = sigma1Panel.getVector2D().y;
		sigma2U = sigma2Panel.getVector2D().x;
		sigma2V = sigma2Panel.getVector2D().y;
		alpha1U = alpha1Panel.getVector2D().x;
		alpha1V = alpha1Panel.getVector2D().y;
		alpha2U = alpha2Panel.getVector2D().x;
		alpha2V = alpha2Panel.getVector2D().y;
		pointAtFOVCentre = pointAtFOVCentrePanel.getVector3D();
//		parallelityParameter = parallelityParameterPanel.getNumber();
//		JComboBox alphaChoiceComboBox;
		apertureCentre = apertureCentrePanel.getVector3D();
		brightnessFactor = brightnessFactorPanel.getNumber();
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
		(new TransmissionCoefficientVisualiser()).run();
	}
}
