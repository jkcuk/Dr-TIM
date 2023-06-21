package optics.raytrace.research.TO.shiftyCloak;

import java.awt.event.ActionEvent;
import java.io.PrintStream;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import math.*;
import math.ODE.IntegrationType;
import net.miginfocom.swing.MigLayout;
import optics.raytrace.sceneObjects.*;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectIntersectionSimple;
import optics.raytrace.surfaces.SurfaceColour;
import optics.raytrace.surfaces.SurfaceColourLightSourceIndependent;
import optics.raytrace.surfaces.SurfaceOfTOShifter;
import optics.raytrace.surfaces.SurfaceOfTOXShifter;
import optics.raytrace.surfaces.SurfaceOfTOYShifter;
import optics.raytrace.surfaces.Transparent;
import optics.raytrace.surfaces.SurfaceOfTOShifter.ShiftFunctionType;
import optics.raytrace.cameras.PinholeCamera.ExposureCompensationType;
import optics.raytrace.core.*;
import optics.DoubleColour;
import optics.raytrace.NonInteractiveTIMActionEnum;
import optics.raytrace.NonInteractiveTIMEngine;
import optics.raytrace.GUI.cameras.RenderQualityEnum;
import optics.raytrace.GUI.lowLevel.ApertureSizeType;
import optics.raytrace.GUI.lowLevel.DoublePanel;
import optics.raytrace.GUI.lowLevel.GUIBitsAndBobs;
import optics.raytrace.GUI.lowLevel.IntPanel;
import optics.raytrace.GUI.lowLevel.LabelledDoubleColourPanel;
import optics.raytrace.GUI.lowLevel.LabelledDoublePanel;
import optics.raytrace.GUI.lowLevel.LabelledIntPanel;
import optics.raytrace.GUI.lowLevel.LabelledVector3DPanel;


public class SphericalShiftyCloakVisualiser extends NonInteractiveTIMEngine
{
	// outer cloak / inner cloak
	private ShiftyDeviceType shiftyDeviceTypeO, shiftyDeviceTypeI;
	private IntegrationType integrationTypeO, integrationTypeI;
	private boolean showShiftyCloakO, showShiftyCloakI;
	private Vector3D centreO, centreI;
	private double radiusO, innerRadiusO, radiusI, innerRadiusI;
	private Vector3D deltaO, deltaI;
	private boolean indicateOuterSphereO, indicateInnerSphereO, indicateOuterSphereI, indicateInnerSphereI;
	private double deltaTauO, deltaXMaxO, deltaTauI, deltaXMaxI;
	private int simulationMaxStepsO, simulationMaxStepsI;
	
	public enum ShiftyDeviceType
	{
		LINEAR("Linear shift function"),
		QUADRATIC("Quadratic shift function"),
		CUBIC_X("3rd-order polynomial shift function, x direction"),
		CUBIC_Y("3rd-order polynomial shift function, y direction");

		private String description;

		private ShiftyDeviceType(String description) {this.description = description;}	
		@Override
		public String toString() {return description;}
	}

	
	// trajectories
	
	/**
	 * show trajectories
	 */
	private boolean showTrajectory1, showTrajectory2, showTrajectory3;
	
	/**
	 * start point of the light-ray trajectories
	 */
	private Vector3D trajectory1StartPoint, trajectory2StartPoint, trajectory3StartPoint;
	
	/**
	 * initial direction of the light-ray trajectories
	 */
	private Vector3D trajectory1StartDirection, trajectory2StartDirection, trajectory3StartDirection;
	
	/**
	 * radius of the trajectories
	 */
	private double trajectoryRadius;
	
	/**
	 * max trace level for trajectory tracing
	 */
	private int trajectoryMaxTraceLevel;
	
	/**
	 * report raytracing progress to console
	 */
	private boolean reportToConsole;
	
	
	
	// rest of  the scene
	
	/**
	 * Determines how to initialise the backdrop
	 */
	private StudioInitialisationType studioInitialisation;

	/**
	 * the centres of spheres that can be placed in the scene
	 */
	private Vector3D[] sphereCentres;
	
	/**
	 * the radii of spheres that can be placed in the scene
	 */
	private double[] sphereRadii;
	
	/**
	 * the colours of spheres that can be placed in the scene
	 */
	private DoubleColour[] sphereColours;
	
	/**
	 * the visibilities of spheres that can be placed in the scene
	 */
	private boolean[] sphereVisibilities;

	
	//
	// the camera's movie mode
	//
	
	/**
	 * direction of the rotation axis, which passes through the view centre, of the camera when in movie mode
	 */
	private Vector3D cameraRotationAxisDirection;

	
	
	public SphericalShiftyCloakVisualiser()
	{
		nonInteractiveTIMAction = NonInteractiveTIMActionEnum.INTERACTIVE;
		renderQuality = RenderQualityEnum.DRAFT;
		
		// camera parameters; these are often set (or altered) in createStudio()
		cameraViewDirection = new Vector3D(0, -1.5, 10);
		cameraViewCentre = new Vector3D(0, 0, 0);
		cameraDistance = 10;
		cameraFocussingDistance = 10;
		cameraHorizontalFOVDeg = 20;
		cameraMaxTraceLevel = 20;
		cameraPixelsX = 640;
		cameraPixelsY = 480;
		cameraApertureSize = ApertureSizeType.PINHOLE;
		cameraExposureCompensation = ExposureCompensationType.EC0;
		
		movie = false;
		// if movie = true, then the following are relevant:
		numberOfFrames = 10;
		firstFrame = 0;
		lastFrame = numberOfFrames-1;
		cameraRotationAxisDirection = new Vector3D(0, 1, 0);

		// Outer shifty cloak
		showShiftyCloakO = false;
		shiftyDeviceTypeO = ShiftyDeviceType.QUADRATIC;
		centreO = new Vector3D(0, 0, 0);
		radiusO = 1;
		innerRadiusO = 0.5;
		deltaO = new Vector3D(0, 1, 0);
		indicateOuterSphereO = true;
		indicateInnerSphereO = true;
		simulationMaxStepsO = 20000;
		integrationTypeO = IntegrationType.RK4;
		deltaTauO = 0.001;
		deltaXMaxO = 0.001;

		// Inner shifty cloak
		showShiftyCloakI = false;
		shiftyDeviceTypeI = ShiftyDeviceType.QUADRATIC;
		centreI = new Vector3D(0, 0, 0);
		radiusI = 0.49;
		innerRadiusI = 0.25;
		deltaI = new Vector3D(1, 0, 0);
		indicateOuterSphereI = true;
		indicateInnerSphereI = true;
		simulationMaxStepsI = 20000;
		integrationTypeI = IntegrationType.RK4;
		deltaTauI = 0.001;
		deltaXMaxI = 0.001;

		// trajectories
		showTrajectory1 = false;
		showTrajectory2 = false;
		showTrajectory3 = false;
		trajectory1StartPoint = new Vector3D(-10,  0.6, 0);
		trajectory2StartPoint = new Vector3D(-10,  0.0, 0);
		trajectory3StartPoint = new Vector3D(-10, -0.6, 0);
		trajectory1StartDirection = new Vector3D(1, 0, 0);
		trajectory2StartDirection = new Vector3D(1, 0, 0);
		trajectory3StartDirection = new Vector3D(1, 0, 0);
		trajectoryRadius = 0.005;
		trajectoryMaxTraceLevel = 4;
		reportToConsole = false;
		traceRaysWithTrajectory = false;	// don't automatically trace rays with trajectory, but do this in a bespoke way

		
		// rest of the scene
		studioInitialisation = StudioInitialisationType.MINIMALIST;	// the backdrop

		sphereCentres = new Vector3D[3];
		sphereRadii = new double[3];
		sphereColours = new DoubleColour[3];
		sphereVisibilities = new boolean[3];
		
		sphereCentres[0] = new Vector3D(-0.4, 0, 0);
		sphereRadii[0] = 0.1;
		sphereColours[0] = new DoubleColour(1, 0, 0);
		sphereVisibilities[0] = true;

		sphereCentres[1] = new Vector3D(0, 0, 0);
		sphereRadii[1] = 0.1;
		sphereColours[1] = new DoubleColour(0, 1, 0);
		sphereVisibilities[1] = true;

		sphereCentres[2] = new Vector3D(.4, 0, 0);
		sphereRadii[2] = 0.1;
		sphereColours[2] = new DoubleColour(0, 0, 1);
		sphereVisibilities[2] = true;

		
		windowTitle = "Dr TIM's shifty-cloak visualiser";
		windowWidth = 1250;
		windowHeight = 650;

	}

	@Override
	public String getClassName()
	{
		return "ShiftyCloakVisualiser"	// the name
				;
	}
	
	@Override
	public void writeParameters(PrintStream printStream)
	{
		// write any parameters not defined in NonInteractiveTIMEngine
		printStream.println("Scene initialisation");
		printStream.println();
		
		// outer shifty cloak
		printStream.println("shiftyDeviceTypeO = "+shiftyDeviceTypeO);
		printStream.println("integrationTypeO = "+integrationTypeO);
		printStream.println("showShiftyCloakO = "+showShiftyCloakO);
		printStream.println("centreO = "+centreO);
		printStream.println("radiusO = "+radiusO);
		printStream.println("deltaO = "+deltaO);
		printStream.println("innerRadiusO = "+innerRadiusO);
		printStream.println("indicateOuterSphereO = "+indicateOuterSphereO);
		printStream.println("indicateInnerSphereO = "+indicateInnerSphereO);
		printStream.println("simulationMaxStepsO = "+simulationMaxStepsO);
		printStream.println("deltaTauO = "+deltaTauO);
		printStream.println("deltaXMaxO = "+deltaXMaxO);

		// inner shifty cloak
		printStream.println("shiftyDeviceTypeI = "+shiftyDeviceTypeI);
		printStream.println("integrationTypeI = "+integrationTypeI);
		printStream.println("showShiftyCloakI = "+showShiftyCloakI);
		printStream.println("centreI = "+centreI);
		printStream.println("radiusI = "+radiusI);
		printStream.println("deltaI = "+deltaI);
		printStream.println("innerRadiusI = "+innerRadiusI);
		printStream.println("indicateOuterSphereI = "+indicateOuterSphereI);
		printStream.println("indicateInnerSphereI = "+indicateInnerSphereI);
		printStream.println("simulationMaxStepsI = "+simulationMaxStepsI);
		printStream.println("deltaTauI = "+deltaTauI);
		printStream.println("deltaXMaxI = "+deltaXMaxI);


		// trajectories
		
		printStream.println("showTrajectory1 = "+showTrajectory1);
		if(showTrajectory1)
		{
			printStream.println("trajectory1StartPoint = "+trajectory1StartPoint);
			printStream.println("trajectory1StartDirection = "+trajectory1StartDirection);
		}

		printStream.println("showTrajectory2 = "+showTrajectory2);
		if(showTrajectory2)
		{
			printStream.println("trajectory2StartPoint = "+trajectory2StartPoint);
			printStream.println("trajectory2StartDirection = "+trajectory2StartDirection);
		}
		
		printStream.println("showTrajectory3 = "+showTrajectory3);
		if(showTrajectory3)
		{
			printStream.println("trajectory3StartPoint = "+trajectory3StartPoint);
			printStream.println("trajectory3StartDirection = "+trajectory3StartDirection);
		}

		if(showTrajectory1 || showTrajectory2 || showTrajectory3)
		{
			printStream.println("trajectoryRadius = "+trajectoryRadius);
			printStream.println("trajectoryMaxTraceLevel = "+trajectoryMaxTraceLevel);
		}
		
		printStream.println("reportToConsole = "+reportToConsole);
		
		// rest of scene
		
		printStream.println("studioInitialisation = "+studioInitialisation);
		for(int i=0; i<3; i++)
		{
			printStream.println("sphereCentres["+i+"] = "+sphereCentres[i]);
			printStream.println("sphereRadii["+i+"] = "+sphereRadii[i]);
			printStream.println("sphereColours["+i+"] = "+sphereColours[i]);
			printStream.println("sphereVisibilities["+i+"] = "+sphereVisibilities[i]);
		}

		printStream.println();
		
		// movie parameters
		printStream.println("movie = "+movie);
		printStream.println("cameraRotationAxisDirection = "+cameraRotationAxisDirection);


		// write all parameters defined in NonInteractiveTIMEngine
		super.writeParameters(printStream);		
	}

	
//	private SurfaceOfTOShifter surfaceOfTOAbyssCloak;
	private SceneObjectIntersectionSimple cloakO, cloakI;
	private Vector3D frame0CameraViewDirection;

	/**
	 * Define scene, lights, and/or camera.
	 * @return a studio, i.e. scene, lights and camera
	 */
	@Override
	public void populateStudio()
	{
		studio = new Studio();

		// the scene
		SceneObjectContainer scene = new SceneObjectContainer("the scene", null, studio);
		studio.setScene(scene);

		// the standard scene objects
		// initialise the scene and lights
		StudioInitialisationType.initialiseSceneAndLights(
				studioInitialisation,
				scene,
				studio
			);

		// add any other scene objects
		
		// first the coloured spheres that can be added to the scene
		for(int i=0; i<3; i++)
			scene.addSceneObject(
					new Sphere(
							"Coloured sphere #"+i,	// description
							sphereCentres[i],	// centre
							sphereRadii[i],	// radius
							new SurfaceColour(sphereColours[i], DoubleColour.WHITE, false),	// surface property: sphereColours[i], made shiny; don't throw shadow
							scene,
							studio
						),
					sphereVisibilities[i]
				);
		
		// add outer shifty cloak
		switch(shiftyDeviceTypeO)
		{
		case CUBIC_X:
			SurfaceOfTOXShifter surfaceOfXShiftyDevice;

			surfaceOfXShiftyDevice = new SurfaceOfTOXShifter(
					centreO,
					radiusO,
					innerRadiusO,
					deltaO.x,
					deltaTauO,
					deltaXMaxO,
					simulationMaxStepsO,
					integrationTypeO,
					0.96,	// transmissionCoefficient
					false	// shadowThrowing
				);
			
			cloakO = surfaceOfXShiftyDevice.addSphericalShellSceneObjectAsSurface("Shifty device", scene, studio);
			scene.addSceneObject(cloakO);
			break;
		case CUBIC_Y:
			SurfaceOfTOYShifter surfaceOfYShiftyDevice;

			surfaceOfYShiftyDevice = new SurfaceOfTOYShifter(
					centreO,
					radiusO,
					innerRadiusO,
					deltaO.y,
					deltaTauO,
					deltaXMaxO,
					simulationMaxStepsO,
					integrationTypeO,
					0.96,	// transmissionCoefficient
					false	// shadowThrowing
				);
			
			cloakO = surfaceOfYShiftyDevice.addSphericalShellSceneObjectAsSurface("Shifty device", scene, studio);
			scene.addSceneObject(cloakO);
			break;
		case LINEAR:
		case QUADRATIC:
		default:
			SurfaceOfTOShifter surfaceOfShiftyDevice;
			ShiftFunctionType shiftFunctionType;
					switch(shiftyDeviceTypeO)
					{
					case LINEAR:
						shiftFunctionType = ShiftFunctionType.LINEAR;
						break;
					case QUADRATIC:
					default:
						shiftFunctionType = ShiftFunctionType.QUADRATIC;						
					}
			surfaceOfShiftyDevice = new SurfaceOfTOShifter(
					centreO,
					radiusO,
					innerRadiusO,
					deltaO,
					shiftFunctionType,
					deltaTauO,
					deltaXMaxO,
					simulationMaxStepsO,
					integrationTypeO,
					0.96,	// transmissionCoefficient
					false	// shadowThrowing
				);
			
			cloakO = surfaceOfShiftyDevice.addSphericalShellSceneObjectAsSurface("Shifty device", scene, studio);
			scene.addSceneObject(cloakO);
		}

		// add inner shifty cloak
		switch(shiftyDeviceTypeI)
		{
		case CUBIC_X:
			SurfaceOfTOXShifter surfaceOfXShiftyDevice;

			surfaceOfXShiftyDevice = new SurfaceOfTOXShifter(
					centreI,
					radiusI,
					innerRadiusI,
					deltaI.x,
					deltaTauI,
					deltaXMaxI,
					simulationMaxStepsI,
					integrationTypeI,
					0.96,	// transmissionCoefficient
					false	// shadowThrowing
				);
			
			cloakI = surfaceOfXShiftyDevice.addSphericalShellSceneObjectAsSurface("Inner shifty device", scene, studio);
			scene.addSceneObject(cloakI);
			break;
		case CUBIC_Y:
			SurfaceOfTOYShifter surfaceOfYShiftyDevice;

			surfaceOfYShiftyDevice = new SurfaceOfTOYShifter(
					centreI,
					radiusI,
					innerRadiusI,
					deltaI.y,
					deltaTauI,
					deltaXMaxI,
					simulationMaxStepsI,
					integrationTypeI,
					0.96,	// transmissionCoefficient
					false	// shadowThrowing
				);
			
			cloakI = surfaceOfYShiftyDevice.addSphericalShellSceneObjectAsSurface("Inner shifty device", scene, studio);
			scene.addSceneObject(cloakI);
			break;
		case LINEAR:
		case QUADRATIC:
		default:
			SurfaceOfTOShifter surfaceOfShiftyDevice;
			ShiftFunctionType shiftFunctionType;
					switch(shiftyDeviceTypeO)
					{
					case LINEAR:
						shiftFunctionType = ShiftFunctionType.LINEAR;
						break;
					case QUADRATIC:
					default:
						shiftFunctionType = ShiftFunctionType.QUADRATIC;						
					}
			surfaceOfShiftyDevice = new SurfaceOfTOShifter(
					centreI,
					radiusI,
					innerRadiusI,
					deltaI,
					shiftFunctionType,
					deltaTauI,
					deltaXMaxI,
					simulationMaxStepsI,
					integrationTypeI,
					0.96,	// transmissionCoefficient
					false	// shadowThrowing
				);
			
			cloakI = surfaceOfShiftyDevice.addSphericalShellSceneObjectAsSurface("Inner shifty device", scene, studio);
			scene.addSceneObject(cloakI);
		}
		
		// trace the ray trajectories
		
		if(showTrajectory1)
		scene.addSceneObject(new RayTrajectory(
				"Trajectory 1",
				trajectory1StartPoint,	// start point
				0,	// start time
				trajectory1StartDirection,	// initial direction
				trajectoryRadius,	// radius
				new SurfaceColourLightSourceIndependent(DoubleColour.RED, false),	// don't throw shadow
				trajectoryMaxTraceLevel,	// max trace level
				reportToConsole,	// reportToConsole
				scene,
				studio
			)
		);

		if(showTrajectory2)
		scene.addSceneObject(new RayTrajectory(
				"Trajectory 2",
				trajectory2StartPoint,	// start point
				0,	// start time
				trajectory2StartDirection,	// initial direction
				trajectoryRadius,	// radius
				new SurfaceColourLightSourceIndependent(DoubleColour.GREEN, false),	// don't throw shadow
				trajectoryMaxTraceLevel,	// max trace level
				reportToConsole,	// reportToConsole
				scene,
				studio
			)
		);

		if(showTrajectory3)
		scene.addSceneObject(new RayTrajectory(
				"Trajectory 3",
				trajectory3StartPoint,	// start point
				0,	// start time
				trajectory3StartDirection,	// initial direction
				trajectoryRadius,	// radius
				new SurfaceColourLightSourceIndependent(DoubleColour.BLUE, false),	// don't throw shadow
				trajectoryMaxTraceLevel,	// max trace level
				reportToConsole,	// reportToConsole
				scene,
				studio
			)
		);


		// trace the rays with trajectory through the scene
		studio.traceRaysWithTrajectory();

		// System.exit(-1);

//		// for test purposes, define a ray...
//		Ray r = new RayWithTrajectory(
//				new Vector3D(0.3, 0, -10),	// start point
//				new Vector3D(0, 0, 1),	// direction
//				0,	// time
//				true	// reportToConsole
//			);
//		
//		// ... and launch it at the sphere
//		try {
//			sphere.getColour(r,
//					LightSource.getStandardLightsFromBehind(),
//					sphere, 100,	// trace level
//					new DefaultRaytraceExceptionHandler()
//			);
//		} catch (RayTraceException e) {
//			e.printStackTrace();
//		}
		
		// System.exit(1);
		
		if(!showShiftyCloakO)
		{
			scene.removeSceneObject(cloakO);

			// add the outer one of the two spheres that form the surface of the spherical shell, making it slightly absorbing
			ParametrisedSphere outerSphere = (ParametrisedSphere)(cloakO.getFirstSceneObjectWithDescription("Outer sphere", false));
					// cloak.getPositiveSceneObjectPrimitives().get(0);
			outerSphere.setSurfaceProperty(Transparent.SLIGHTLY_ABSORBING);
			scene.addSceneObject(outerSphere, indicateOuterSphereO);

			// add the inner one of the two spheres that form the surface of the spherical shell, making it slightly absorbing
			ParametrisedSphere innerSphere = (ParametrisedSphere)(cloakO.getFirstSceneObjectWithDescription("Inner sphere", false));
				// cloak.getNegativeSceneObjectPrimitives().get(0);
			innerSphere.setSurfaceProperty(Transparent.SLIGHTLY_ABSORBING);
			scene.addSceneObject(innerSphere, indicateInnerSphereO);
		}

		if(!showShiftyCloakI)
		{
			scene.removeSceneObject(cloakI);

			// add the outer one of the two spheres that form the surface of the spherical shell, making it slightly absorbing
			ParametrisedSphere outerSphere = (ParametrisedSphere)(cloakI.getFirstSceneObjectWithDescription("Outer sphere", false));
					// cloak.getPositiveSceneObjectPrimitives().get(0);
			outerSphere.setSurfaceProperty(Transparent.SLIGHTLY_ABSORBING);
			scene.addSceneObject(outerSphere, indicateOuterSphereI);

			// add the inner one of the two spheres that form the surface of the spherical shell, making it slightly absorbing
			ParametrisedSphere innerSphere = (ParametrisedSphere)(cloakI.getFirstSceneObjectWithDescription("Inner sphere", false));
				// cloak.getNegativeSceneObjectPrimitives().get(0);
			innerSphere.setSurfaceProperty(Transparent.SLIGHTLY_ABSORBING);
			scene.addSceneObject(innerSphere, indicateInnerSphereI);
		}

		studio.setScene(scene);
		
		if(movie)
		{
			// calculate view position that corresponds to the current frame
			// initial camera position
			Vector3D initialCameraPosition = Vector3D.sum(cameraViewCentre, frame0CameraViewDirection.getWithLength(-cameraDistance));
			// the camera will move in a circle; calculate its centre
			Vector3D centreOfCircle = Geometry.getPointOnLineClosestToPoint(
					cameraViewCentre,	// pointOnLine
					cameraRotationAxisDirection,	// directionOfLine
					initialCameraPosition	// point
				);
			// construct two unit vectors that span the plane of the circle in which the camera will move
			Vector3D uHat = Vector3D.difference(initialCameraPosition, centreOfCircle).getNormalised();
			Vector3D vHat = Vector3D.crossProduct(cameraRotationAxisDirection, uHat).getNormalised();

			// define the azimuthal angle phi that parametrises the circle
			double phi = 2.*Math.PI*frame/numberOfFrames;
			System.out.println("LensCloakVisualiser::populateStudio: phi="+phi+"(="+MyMath.rad2deg(phi)+"deg)");
			
			// finally, calculate the view direction
			cameraViewDirection = Vector3D.difference(
					cameraViewCentre, 
					Vector3D.sum(centreOfCircle, uHat.getProductWith(Math.cos(phi)*cameraDistance), vHat.getProductWith(Math.sin(phi)*cameraDistance))
				);
		}
		studio.setCamera(getStandardCamera());
	}
	
	
	
	// GUI
	
	// outer shifty device
	private JComboBox<ShiftyDeviceType> shiftyDeviceTypeOComboBox;
	private JComboBox<IntegrationType> integrationTypeOComboBox;
	private JCheckBox showShiftyCloakOCheckBox, indicateOuterSphereOCheckBox, indicateInnerSphereOCheckBox;
	private LabelledVector3DPanel centreOPanel, deltaOPanel;	// , testPositionPanel;
//	private JTextField infoTextField;
//	private JButton testButton;
	private LabelledDoublePanel radiusOPanel, innerRadiusOPanel, deltaTauOPanel, deltaXMaxOPanel;
	private LabelledIntPanel simulationMaxStepsOPanel;

	// inner shifty device
	private JComboBox<ShiftyDeviceType> shiftyDeviceTypeIComboBox;
	private JComboBox<IntegrationType> integrationTypeIComboBox;
	private JCheckBox showShiftyCloakICheckBox, indicateOuterSphereICheckBox, indicateInnerSphereICheckBox;
	private LabelledVector3DPanel centreIPanel, deltaIPanel;	// , testPositionPanel;
//	private JTextField infoTextField;
//	private JButton testButton;
	private LabelledDoublePanel radiusIPanel, innerRadiusIPanel, deltaTauIPanel, deltaXMaxIPanel;
	private LabelledIntPanel simulationMaxStepsIPanel;

	// trajectory
	private JCheckBox showTrajectory1CheckBox, showTrajectory2CheckBox, showTrajectory3CheckBox, reportToConsoleCheckBox;
	private LabelledVector3DPanel trajectory1StartPointPanel, trajectory2StartPointPanel, trajectory3StartPointPanel;
	private LabelledVector3DPanel trajectory1StartDirectionPanel, trajectory2StartDirectionPanel, trajectory3StartDirectionPanel;
	private LabelledDoublePanel trajectoryRadiusPanel;
	private LabelledIntPanel trajectoryMaxTraceLevelPanel;

	// rest of scene
	private JComboBox<StudioInitialisationType> studioInitialisationComboBox;
	private LabelledVector3DPanel[] sphereCentrePanels;
	private LabelledDoublePanel[] sphereRadiusPanels;
	private LabelledDoubleColourPanel[] sphereColourPanels;
	private JCheckBox[] sphereVisibilityCheckBoxes;


	// camera
	private LabelledVector3DPanel cameraPositionPanel, cameraViewCentrePanel;
	private DoublePanel cameraHorizontalFOVDegPanel;
	private JComboBox<ApertureSizeType> cameraApertureSizeComboBox;
	private LabelledDoublePanel cameraFocussingDistancePanel;
	private LabelledIntPanel cameraMaxTraceLevelPanel;
	
	private JCheckBox movieCheckBox;
	private LabelledVector3DPanel cameraRotationAxisDirectionPanel;
	private IntPanel numberOfFramesPanel, firstFramePanel, lastFramePanel;


	
	@Override
	protected void createInteractiveControlPanel()
	{
		super.createInteractiveControlPanel();
		
		JTabbedPane tabbedPane = new JTabbedPane();
		interactiveControlPanel.add(tabbedPane, "span");
		
		

		//
		// the outer shifty device
		//
		
		JPanel outerShiftyCloakPanel = new JPanel();
		outerShiftyCloakPanel.setLayout(new MigLayout("insets 0"));

		showShiftyCloakOCheckBox = new JCheckBox("Show shifty device cloak");
		showShiftyCloakOCheckBox.setSelected(showShiftyCloakO);
		outerShiftyCloakPanel.add(showShiftyCloakOCheckBox, "wrap");
		
		shiftyDeviceTypeOComboBox = new JComboBox<ShiftyDeviceType>(ShiftyDeviceType.values());
		shiftyDeviceTypeOComboBox.setSelectedItem(shiftyDeviceTypeO);
		outerShiftyCloakPanel.add(GUIBitsAndBobs.makeRow("Device type", shiftyDeviceTypeOComboBox), "span");


		centreOPanel = new LabelledVector3DPanel("Centre");
		centreOPanel.setVector3D(centreO);
		outerShiftyCloakPanel.add(centreOPanel, "span");

		radiusOPanel = new LabelledDoublePanel("(Outer) radius");
		radiusOPanel.setNumber(radiusO);
		outerShiftyCloakPanel.add(radiusOPanel, "wrap");
		
		innerRadiusOPanel = new LabelledDoublePanel("Inner radius");
		innerRadiusOPanel.setNumber(innerRadiusO);
		outerShiftyCloakPanel.add(innerRadiusOPanel, "wrap");
		
		deltaOPanel = new LabelledVector3DPanel("Delta (apparent shift)");
		deltaOPanel.setVector3D(deltaO);
		outerShiftyCloakPanel.add(deltaOPanel, "wrap");
		
		indicateOuterSphereOCheckBox = new JCheckBox("Indicate outer sphere");
		indicateOuterSphereOCheckBox.setSelected(indicateOuterSphereO);
		outerShiftyCloakPanel.add(indicateOuterSphereOCheckBox, "wrap");
		
		indicateInnerSphereOCheckBox = new JCheckBox("Indicate inner sphere");
		indicateInnerSphereOCheckBox.setSelected(indicateInnerSphereO);
		outerShiftyCloakPanel.add(indicateInnerSphereOCheckBox, "wrap");
		
		integrationTypeOComboBox = new JComboBox<IntegrationType>(IntegrationType.values());
		integrationTypeOComboBox.setSelectedItem(integrationTypeO);
		outerShiftyCloakPanel.add(GUIBitsAndBobs.makeRow("Integration type", integrationTypeOComboBox), "span");

		deltaTauOPanel = new LabelledDoublePanel("Delta tau");
		deltaTauOPanel.setNumber(deltaTauO);
		outerShiftyCloakPanel.add(deltaTauOPanel, "wrap");
		
		deltaXMaxOPanel = new LabelledDoublePanel("Delta x_max");
		deltaXMaxOPanel.setNumber(deltaXMaxO);
		outerShiftyCloakPanel.add(deltaXMaxOPanel, "wrap");
				
		simulationMaxStepsOPanel = new LabelledIntPanel("Max. number of simulation steps");
		simulationMaxStepsOPanel.setNumber(simulationMaxStepsO);
		outerShiftyCloakPanel.add(simulationMaxStepsOPanel, "wrap");
		
//		testPositionPanel = new LabelledVector3DPanel("Test position");
//		testPositionPanel.setVector3D(new Vector3D(0, 0, 0));
//		testButton = new JButton("Test");
//		testButton.addActionListener(this);
//		abyssCloakPanel.add(GUIBitsAndBobs.makeRow(testPositionPanel, testButton), "span");
//
//		infoTextField  = new JTextField(40);
//		infoTextField.setEditable(false);
//		infoTextField.setText("-- not initialised --");
//		abyssCloakPanel.add(infoTextField, "span");
		
		tabbedPane.addTab("Outer shifty cloak", outerShiftyCloakPanel);
		
		
		//
		// the inner shifty device
		//
		
		JPanel innerShiftyCloakPanel = new JPanel();
		innerShiftyCloakPanel.setLayout(new MigLayout("insets 0"));

		showShiftyCloakICheckBox = new JCheckBox("Show shifty device cloak");
		showShiftyCloakICheckBox.setSelected(showShiftyCloakI);
		innerShiftyCloakPanel.add(showShiftyCloakICheckBox, "wrap");
		
		shiftyDeviceTypeIComboBox = new JComboBox<ShiftyDeviceType>(ShiftyDeviceType.values());
		shiftyDeviceTypeIComboBox.setSelectedItem(shiftyDeviceTypeI);
		innerShiftyCloakPanel.add(GUIBitsAndBobs.makeRow("Device type", shiftyDeviceTypeIComboBox), "span");


		centreIPanel = new LabelledVector3DPanel("Centre");
		centreIPanel.setVector3D(centreI);
		innerShiftyCloakPanel.add(centreIPanel, "span");

		radiusIPanel = new LabelledDoublePanel("(Outer) radius");
		radiusIPanel.setNumber(radiusI);
		innerShiftyCloakPanel.add(radiusIPanel, "wrap");
		
		innerRadiusIPanel = new LabelledDoublePanel("Inner radius");
		innerRadiusIPanel.setNumber(innerRadiusI);
		innerShiftyCloakPanel.add(innerRadiusIPanel, "wrap");
		
		deltaIPanel = new LabelledVector3DPanel("Delta (apparent shift)");
		deltaIPanel.setVector3D(deltaI);
		innerShiftyCloakPanel.add(deltaIPanel, "wrap");
		
		indicateOuterSphereICheckBox = new JCheckBox("Indicate outer sphere");
		indicateOuterSphereICheckBox.setSelected(indicateOuterSphereI);
		innerShiftyCloakPanel.add(indicateOuterSphereICheckBox, "wrap");
		
		indicateInnerSphereICheckBox = new JCheckBox("Indicate inner sphere");
		indicateInnerSphereICheckBox.setSelected(indicateInnerSphereI);
		innerShiftyCloakPanel.add(indicateInnerSphereICheckBox, "wrap");
		
		integrationTypeIComboBox = new JComboBox<IntegrationType>(IntegrationType.values());
		integrationTypeIComboBox.setSelectedItem(integrationTypeI);
		innerShiftyCloakPanel.add(GUIBitsAndBobs.makeRow("Integration type", integrationTypeIComboBox), "span");

		deltaTauIPanel = new LabelledDoublePanel("Delta tau");
		deltaTauIPanel.setNumber(deltaTauI);
		innerShiftyCloakPanel.add(deltaTauIPanel, "wrap");
		
		deltaXMaxIPanel = new LabelledDoublePanel("Delta x_max");
		deltaXMaxIPanel.setNumber(deltaXMaxI);
		innerShiftyCloakPanel.add(deltaXMaxIPanel, "wrap");
				
		simulationMaxStepsIPanel = new LabelledIntPanel("Max. number of simulation steps");
		simulationMaxStepsIPanel.setNumber(simulationMaxStepsI);
		innerShiftyCloakPanel.add(simulationMaxStepsIPanel, "wrap");
		
//		testPositionPanel = new LabelledVector3DPanel("Test position");
//		testPositionPanel.setVector3D(new Vector3D(0, 0, 0));
//		testButton = new JButton("Test");
//		testButton.addActionListener(this);
//		abyssCloakPanel.add(GUIBitsAndBobs.makeRow(testPositionPanel, testButton), "span");
//
//		infoTextField  = new JTextField(40);
//		infoTextField.setEditable(false);
//		infoTextField.setText("-- not initialised --");
//		abyssCloakPanel.add(infoTextField, "span");
		
		tabbedPane.addTab("Inner shifty cloak", innerShiftyCloakPanel);

		
		
		//
		// trajectory panel
		//
		
		JPanel trajectoryPanel = new JPanel();
		trajectoryPanel.setLayout(new MigLayout("insets 0"));
		
		JTabbedPane trajectoriesTabbedPane = new JTabbedPane();
		trajectoryPanel.add(trajectoriesTabbedPane, "span");

		// trajectory 1
		JPanel trajectory1Panel = new JPanel();
		trajectory1Panel.setLayout(new MigLayout("insets 0"));

		showTrajectory1CheckBox = new JCheckBox("Show trajectory 1");
		showTrajectory1CheckBox.setSelected(showTrajectory1);
		trajectory1Panel.add(showTrajectory1CheckBox, "wrap");

		trajectory1StartPointPanel = new LabelledVector3DPanel("Start point");
		trajectory1StartPointPanel.setVector3D(trajectory1StartPoint);
		trajectory1Panel.add(trajectory1StartPointPanel, "span");

		trajectory1StartDirectionPanel = new LabelledVector3DPanel("Initial direction");
		trajectory1StartDirectionPanel.setVector3D(trajectory1StartDirection);
		trajectory1Panel.add(trajectory1StartDirectionPanel, "span");
		
		trajectoriesTabbedPane.addTab("Trajectory 1", trajectory1Panel);
		
		// trajectory 2
		JPanel trajectory2Panel = new JPanel();
		trajectory2Panel.setLayout(new MigLayout("insets 0"));

		showTrajectory2CheckBox = new JCheckBox("Show trajectory 2");
		showTrajectory2CheckBox.setSelected(showTrajectory2);
		trajectory2Panel.add(showTrajectory2CheckBox, "wrap");

		trajectory2StartPointPanel = new LabelledVector3DPanel("Start point");
		trajectory2StartPointPanel.setVector3D(trajectory2StartPoint);
		trajectory2Panel.add(trajectory2StartPointPanel, "span");

		trajectory2StartDirectionPanel = new LabelledVector3DPanel("Initial direction");
		trajectory2StartDirectionPanel.setVector3D(trajectory2StartDirection);
		trajectory2Panel.add(trajectory2StartDirectionPanel, "span");
		
		trajectoriesTabbedPane.addTab("Trajectory 2", trajectory2Panel);

		// trajectory 3
		JPanel trajectory3Panel = new JPanel();
		trajectory3Panel.setLayout(new MigLayout("insets 0"));

		showTrajectory3CheckBox = new JCheckBox("Show trajectory 3");
		showTrajectory3CheckBox.setSelected(showTrajectory3);
		trajectory3Panel.add(showTrajectory3CheckBox, "wrap");

		trajectory3StartPointPanel = new LabelledVector3DPanel("Start point");
		trajectory3StartPointPanel.setVector3D(trajectory3StartPoint);
		trajectory3Panel.add(trajectory3StartPointPanel, "span");

		trajectory3StartDirectionPanel = new LabelledVector3DPanel("Initial direction");
		trajectory3StartDirectionPanel.setVector3D(trajectory3StartDirection);
		trajectory3Panel.add(trajectory3StartDirectionPanel, "span");
		
		trajectoriesTabbedPane.addTab("Trajectory 3", trajectory3Panel);

		trajectoryRadiusPanel = new LabelledDoublePanel("Radius");
		trajectoryRadiusPanel.setNumber(trajectoryRadius);
		trajectoryPanel.add(trajectoryRadiusPanel, "span");
		
		trajectoryMaxTraceLevelPanel = new LabelledIntPanel("Max. trace level");
		trajectoryMaxTraceLevelPanel.setNumber(trajectoryMaxTraceLevel);
		trajectoryPanel.add(trajectoryMaxTraceLevelPanel, "span");
		
		reportToConsoleCheckBox = new JCheckBox("Report raytracing progress to console");
		reportToConsoleCheckBox.setSelected(reportToConsole);
		trajectoryPanel.add(reportToConsoleCheckBox, "wrap");

		tabbedPane.addTab("Trajectories", trajectoryPanel);
		
		//
		// rest of scene panel
		//
		
		JPanel restOfScenePanel = new JPanel();
		restOfScenePanel.setLayout(new MigLayout("insets 0"));
		
		studioInitialisationComboBox = new JComboBox<StudioInitialisationType>(StudioInitialisationType.limitedValuesForBackgrounds);
		studioInitialisationComboBox.setSelectedItem(studioInitialisation);
		restOfScenePanel.add(GUIBitsAndBobs.makeRow("Initialise backdrop to", studioInitialisationComboBox), "span");
		
		JPanel spherePanels[] = new JPanel[3];
		sphereCentrePanels = new LabelledVector3DPanel[3];
		sphereRadiusPanels = new LabelledDoublePanel[3];
		sphereColourPanels = new LabelledDoubleColourPanel[3];
		sphereVisibilityCheckBoxes = new JCheckBox[3];
		for(int i=0; i<3; i++)
		{
			spherePanels[i] = new JPanel();
			spherePanels[i].setLayout(new MigLayout("insets 0"));
			spherePanels[i].setBorder(GUIBitsAndBobs.getTitledBorder("Sphere #"+(i+1)));
			
			sphereCentrePanels[i] = new LabelledVector3DPanel("Centre");
			sphereCentrePanels[i].setVector3D(sphereCentres[i]);
			spherePanels[i].add(sphereCentrePanels[i], "wrap");

			sphereRadiusPanels[i] = new LabelledDoublePanel("Radius");
			sphereRadiusPanels[i].setNumber(sphereRadii[i]);
			spherePanels[i].add(sphereRadiusPanels[i], "wrap");

			sphereColourPanels[i] = new LabelledDoubleColourPanel("Colour");
			sphereColourPanels[i].setDoubleColour(sphereColours[i]);
			spherePanels[i].add(sphereColourPanels[i], "wrap");

			sphereVisibilityCheckBoxes[i] = new JCheckBox("Visible");
			sphereVisibilityCheckBoxes[i].setSelected(sphereVisibilities[i]);
			spherePanels[i].add(sphereVisibilityCheckBoxes[i], "wrap");
			
			restOfScenePanel.add(spherePanels[i], "wrap");
		}
		
		tabbedPane.addTab("Rest of scene", restOfScenePanel);

		//
		// camera panel
		//
		
		JPanel cameraPanel = new JPanel();
		cameraPanel.setLayout(new MigLayout("insets 0"));

		cameraPositionPanel = new LabelledVector3DPanel("Aperture centre");
		cameraPositionPanel.setVector3D(Vector3D.difference(cameraViewCentre, cameraViewDirection.getWithLength(cameraDistance)));
		cameraPanel.add(cameraPositionPanel, "span");

		cameraViewCentrePanel = new LabelledVector3DPanel("Centre of view");
		cameraViewCentrePanel.setVector3D(cameraViewCentre);
		cameraPanel.add(cameraViewCentrePanel, "span");
		
		cameraHorizontalFOVDegPanel = new DoublePanel();
		cameraHorizontalFOVDegPanel.setNumber(cameraHorizontalFOVDeg);
		cameraPanel.add(GUIBitsAndBobs.makeRow("Horizontal FOV", cameraHorizontalFOVDegPanel, "°"), "span");
		
		cameraApertureSizeComboBox = new JComboBox<ApertureSizeType>(ApertureSizeType.values());
		cameraApertureSizeComboBox.setSelectedItem(cameraApertureSize);
		cameraApertureSizeComboBox.addActionListener(this);
		cameraPanel.add(GUIBitsAndBobs.makeRow("Aperture size", cameraApertureSizeComboBox), "span");		
		
		cameraFocussingDistancePanel = new LabelledDoublePanel("Focussing distance");
		cameraFocussingDistancePanel.setNumber(cameraFocussingDistance);
		cameraPanel.add(cameraFocussingDistancePanel, "span");

		cameraMaxTraceLevelPanel = new LabelledIntPanel("Max. trace level");
		cameraMaxTraceLevelPanel.setNumber(cameraMaxTraceLevel);
		cameraPanel.add(cameraMaxTraceLevelPanel, "span");
		
		// movie panel
		
		JPanel moviePanel = new JPanel();
		moviePanel.setBorder(GUIBitsAndBobs.getTitledBorder("Movie"));
		moviePanel.setLayout(new MigLayout("insets 0"));
		cameraPanel.add(moviePanel, "span");

		movieCheckBox = new JCheckBox("Create movie");
		movieCheckBox.setSelected(movie);
		moviePanel.add(movieCheckBox, "span");
		
		cameraRotationAxisDirectionPanel = new LabelledVector3DPanel("Direction of rotation axis");
		cameraRotationAxisDirectionPanel.setVector3D(cameraRotationAxisDirection);
		moviePanel.add(cameraRotationAxisDirectionPanel, "span");
		
		numberOfFramesPanel = new IntPanel();
		numberOfFramesPanel.setNumber(numberOfFrames);
		
		firstFramePanel = new IntPanel();
		firstFramePanel.setNumber(firstFrame);
		
		lastFramePanel = new IntPanel();
		lastFramePanel.setNumber(lastFrame);

		moviePanel.add(GUIBitsAndBobs.makeRow("Calculate frames", firstFramePanel, "to", lastFramePanel, "out of", numberOfFramesPanel), "wrap");

		tabbedPane.addTab("Camera", cameraPanel);
	}
	
	@Override
	protected void acceptValuesInInteractiveControlPanel()
	{
		super.acceptValuesInInteractiveControlPanel();
		
		// outer shifty cloak
		
		shiftyDeviceTypeO = (ShiftyDeviceType)(shiftyDeviceTypeOComboBox.getSelectedItem());
		integrationTypeO = (IntegrationType)(integrationTypeOComboBox.getSelectedItem());
		showShiftyCloakO = showShiftyCloakOCheckBox.isSelected();
		centreO = centreOPanel.getVector3D();
		radiusO = radiusOPanel.getNumber();
		innerRadiusO = innerRadiusOPanel.getNumber();
		deltaO = deltaOPanel.getVector3D();
		indicateOuterSphereO = indicateOuterSphereOCheckBox.isSelected();
		indicateInnerSphereO = indicateInnerSphereOCheckBox.isSelected();
		simulationMaxStepsO = simulationMaxStepsOPanel.getNumber();
		deltaTauO = deltaTauOPanel.getNumber();
		deltaXMaxO = deltaXMaxOPanel.getNumber();

		// inner shifty cloak
		
		shiftyDeviceTypeI = (ShiftyDeviceType)(shiftyDeviceTypeIComboBox.getSelectedItem());
		integrationTypeI = (IntegrationType)(integrationTypeIComboBox.getSelectedItem());
		showShiftyCloakI = showShiftyCloakICheckBox.isSelected();
		centreI = centreIPanel.getVector3D();
		radiusI = radiusIPanel.getNumber();
		innerRadiusI = innerRadiusIPanel.getNumber();
		deltaI = deltaIPanel.getVector3D();
		indicateOuterSphereI = indicateOuterSphereICheckBox.isSelected();
		indicateInnerSphereI = indicateInnerSphereICheckBox.isSelected();
		simulationMaxStepsI = simulationMaxStepsIPanel.getNumber();
		deltaTauI = deltaTauIPanel.getNumber();
		deltaXMaxI = deltaXMaxIPanel.getNumber();

		// trajectories
		
		showTrajectory1 = showTrajectory1CheckBox.isSelected();
		trajectory1StartPoint = trajectory1StartPointPanel.getVector3D();
		trajectory1StartDirection = trajectory1StartDirectionPanel.getVector3D();
		showTrajectory2 = showTrajectory2CheckBox.isSelected();
		trajectory2StartPoint = trajectory2StartPointPanel.getVector3D();
		trajectory2StartDirection = trajectory2StartDirectionPanel.getVector3D();
		showTrajectory3 = showTrajectory3CheckBox.isSelected();
		trajectory3StartPoint = trajectory3StartPointPanel.getVector3D();
		trajectory3StartDirection = trajectory3StartDirectionPanel.getVector3D();
		trajectoryRadius = trajectoryRadiusPanel.getNumber();
		trajectoryMaxTraceLevel = trajectoryMaxTraceLevelPanel.getNumber();
		reportToConsole = reportToConsoleCheckBox.isSelected();
		
		// rest of scene
		
		studioInitialisation = (StudioInitialisationType)(studioInitialisationComboBox.getSelectedItem());
		for(int i=0; i<3; i++)
		{
			sphereCentres[i] = sphereCentrePanels[i].getVector3D();
			sphereRadii[i] = sphereRadiusPanels[i].getNumber();
			sphereColours[i] = sphereColourPanels[i].getDoubleColour();
			sphereVisibilities[i] = sphereVisibilityCheckBoxes[i].isSelected();
		}
		
		// camera
		
		cameraViewCentre = cameraViewCentrePanel.getVector3D();
		cameraViewDirection = Vector3D.difference(cameraViewCentre, cameraPositionPanel.getVector3D());
		cameraDistance = cameraViewDirection.getLength();
		cameraHorizontalFOVDeg = cameraHorizontalFOVDegPanel.getNumber();
		cameraApertureSize = (ApertureSizeType)(cameraApertureSizeComboBox.getSelectedItem());
		cameraFocussingDistance = cameraFocussingDistancePanel.getNumber();
		cameraMaxTraceLevel = cameraMaxTraceLevelPanel.getNumber();
		
		movie = movieCheckBox.isSelected();
		cameraRotationAxisDirection = cameraRotationAxisDirectionPanel.getVector3D();
		numberOfFrames = numberOfFramesPanel.getNumber();
		firstFrame = firstFramePanel.getNumber();
		lastFrame = lastFramePanel.getNumber();
		
		frame0CameraViewDirection = cameraViewDirection;
	}


	@Override
	public void actionPerformed(ActionEvent e)
	{
		super.actionPerformed(e);
		
//		if(e.getSource().equals(testButton))
//		{
//			// initialise spaceAroundBlackHole according to the current parameters
//			acceptValuesInInteractiveControlPanel();
//			populateStudio();
//			
//			infoTextField.setText("Don't press the button!");
//			
//			Vector3D testPosition = testPositionPanel.getVector3D();
//			System.out.println("test position "+testPosition+" inside cloak = "+cloak.insideObject(testPosition));
//			System.out.println("n tensor at test position = ");
//			Matrix n = surfaceOfTOAbyssCloak.calculateEpsilonMuTensor(testPosition);
//			for(int r=0; r<n.getRowDimension(); r++)
//			{
//				System.out.print(" \t");
//				for(int c=0; c<n.getColumnDimension(); c++) System.out.print(" \t"+n.get(r, c));
//				System.out.println(" ");
//			}
//			// infoTextField.setText("Refractive index at test position = "+spaceAroundBlackHoleMetric.getEpsilonMuTensor(testPositionPanel.getVector3D()).get(0, 0));
//		}
	}

 	public static void main(final String[] args)
   	{
  		(new SphericalShiftyCloakVisualiser()).run();
  	}
 }
