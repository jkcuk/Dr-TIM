package optics.raytrace.research.billiard;

import java.io.PrintStream;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import math.*;
import net.miginfocom.swing.MigLayout;
import optics.raytrace.sceneObjects.*;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectIntersection;
import optics.raytrace.sceneObjects.EllipticCylinderMantle;
import optics.raytrace.surfaces.Reflective;
import optics.raytrace.surfaces.SurfaceColour;
import optics.raytrace.surfaces.SurfaceColourLightSourceIndependent;
import optics.raytrace.surfaces.SurfaceColourTimeDependent;
import optics.raytrace.cameras.PinholeCamera.ExposureCompensationType;
import optics.raytrace.core.*;
import optics.DoubleColour;
import optics.raytrace.NonInteractiveTIMActionEnum;
import optics.raytrace.NonInteractiveTIMEngine;
import optics.raytrace.GUI.cameras.EditableRelativisticAnyFocusSurfaceCamera;
import optics.raytrace.GUI.cameras.RenderQualityEnum;
import optics.raytrace.GUI.lowLevel.ApertureSizeType;
import optics.raytrace.GUI.lowLevel.DoublePanel;
import optics.raytrace.GUI.lowLevel.GUIBitsAndBobs;
import optics.raytrace.GUI.lowLevel.LabelledDoubleColourPanel;
import optics.raytrace.GUI.lowLevel.LabelledDoublePanel;
import optics.raytrace.GUI.lowLevel.LabelledIntPanel;
import optics.raytrace.GUI.lowLevel.LabelledVector3DPanel;


/**
 * Visualise the view within the pendrose cavity[TODO get a ref].
 * 
 * This consists of an ellipse with two "mushroom" shaped extrusiones from either side.
 * The elipses are tunable with a provided focal length along with a major axis.
 * 
 * The main method renders the image defined by createStudio(), saves it to a file
 * (whose name is given by the constant FILENAME), and displays it in a new window.
 * 
 * @author Maik
 */
public class PenroseCavityVisualiser extends NonInteractiveTIMEngine
{

	
	/**
	 * height of the wall around the cavity
	 */
	private double wallHeight;
	
	/*
	 * The span vector along the major axis of the outer elliptic cylinder.
	 */
	private Vector3D spanA;
	
	/*
	 * the focal length of the outer ellipse
	 */
	private double focalLength;
	
	/*
	 * The span vector along the major axis of the first and second inner elliptic cylinder respectively.
	 */
	private Vector3D spanA1, spanA2;
	
	/*
	 * the focal length of the first and second inner ellipse respectively.
	 */
	private double focalLength1, focalLength2;
	
	private double mirrorReflectionCoefficient;
	
	
	/*
	 * The height and width of the extrusion cavities.
	 */
	private double cavityDepth1, cavityWidth1, cavityDepth2, cavityWidth2;
	
	/*
	 *set the surfaces to be reflective. If not true they will be blue by default (colour be changed in the code) 
	 */
	private boolean setReflective;
	
	/*
	 * if true, the cavity will also have shadows.
	 */
	private boolean shadowThrowing;
	
	// camera
	
	private Vector3D cameraPosition;
	private double movieRotationAngleDeg;
	private Vector3D movieRotationAxis;
	
	// trajectories
	
	/**
	 * show trajectories
	 */
	private int noOfTrajectories = 3;
	
	private boolean showTrajectory[] = new boolean[noOfTrajectories];
	
	/**
	 * start point of the light-ray trajectories
	 */
	private Vector3D trajectoryStartPoint[] = new Vector3D[noOfTrajectories];
	
	/**
	 * initial direction of the light-ray trajectories
	 */
	private Vector3D trajectoryStartDirection[] = new Vector3D[noOfTrajectories];
	
	private DoubleColour trajectoryColour[] = new DoubleColour[noOfTrajectories];

	/**
	 * radius of the trajectories
	 */
	private double trajectoriesRadius;
	
	private boolean trajectoriesShadowThrowing;
	
	/**
	 * max trace level for trajectory tracing
	 */
	private int trajectoriesMaxTraceLevel;
	
	/**
	 * report raytracing progress to console
	 */
	private boolean trajectoriesReportToConsole;
	
	
	// rest of  the scene
	
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
	
	private boolean[] sphereColoursTimeDependent;
	
	private double[] sphereColoursTimePeriod;

	private boolean[] sphereColoursLightSourceIndependent;

	/**
	 * the visibilities of spheres that can be placed in the scene
	 */
	private boolean[] sphereVisibilities;

	
	public PenroseCavityVisualiser()
	{
		nonInteractiveTIMAction = NonInteractiveTIMActionEnum.INTERACTIVE;
		renderQuality = RenderQualityEnum.DRAFT;
		
		numberOfFrames = 10;
		firstFrame = 0;
		lastFrame = numberOfFrames-1;
		movie = false;
		movieRotationAngleDeg = 10;
		movieRotationAxis = new Vector3D(0, 1, 0);

		
		// the cavity
		wallHeight = 1;
		setReflective = true;
		shadowThrowing = true;
		spanA = new Vector3D(0,0,6);
		
		focalLength = 4;

		spanA1 = spanA2 = new Vector3D(0,0,1);
		focalLength1 = focalLength2 = 0.6;
		
		mirrorReflectionCoefficient = 0.999;
		cavityDepth1 = cavityDepth2 = 1;
		cavityWidth1 = cavityWidth2 = 0.7;
		
		// camera above
		cameraViewCentre = new Vector3D(0, 0, 0.0001);
		// cameraViewDirection = new Vector3D(0, -1, 0.0001);
		cameraPosition = new Vector3D(0, 10, 0);
		cameraHorizontalFOVDeg = 80;

		cameraMaxTraceLevel = 1000;
		cameraPixelsX = 640;
		cameraPixelsY = 480;
		cameraFocussingDistance = 10;
		cameraApertureSize = ApertureSizeType.PINHOLE;
		cameraExposureCompensation = ExposureCompensationType.EC0;
		

		// trajectories
		showTrajectory[0] = false;
		trajectoryStartPoint[0] = new Vector3D(0,  0.5, 0.5);
		trajectoryStartDirection[0] = new Vector3D(1, 0, 0);
		trajectoryColour[0] = new DoubleColour(1, 0, 0);
		
		showTrajectory[1] = false;
		trajectoryStartPoint[1] = new Vector3D(0,  0.5, 1);
		trajectoryStartDirection[1] = new Vector3D(1, 0, 0);
		trajectoryColour[1] = new DoubleColour(0, 1, 0);

		showTrajectory[2] = false;
		trajectoryStartPoint[2] = new Vector3D(0,  0.5, 1.5);
		trajectoryStartDirection[2] = new Vector3D(1, 0, 0);
		trajectoryColour[2] = new DoubleColour(0, 0, 1);

		trajectoriesRadius = 0.005;
		trajectoriesShadowThrowing = false;
		trajectoriesMaxTraceLevel = 100;
		trajectoriesReportToConsole = false;
		traceRaysWithTrajectory = false;	// don't automatically trace rays with trajectory, but do this in a bespoke way

		
		// rest of the scene
		sphereCentres = new Vector3D[3];
		sphereRadii = new double[3];
		sphereColours = new DoubleColour[3];
		sphereColoursTimeDependent = new boolean[3];
		sphereColoursTimePeriod = new double[3];
		sphereColoursLightSourceIndependent = new boolean[3];
		sphereVisibilities = new boolean[3];
		
		double sphereRadius = 0.5;
		
		sphereCentres[0] = new Vector3D(2*sphereRadius, sphereRadius, 0);
		sphereRadii[0] = sphereRadius;
		sphereColours[0] = new DoubleColour(1, 0, 0);
		sphereColoursTimeDependent[0] = false;
		sphereColoursTimePeriod[0] = 1;
		sphereColoursLightSourceIndependent[0] = false;
		sphereVisibilities[0] = true;

		sphereCentres[1] = new Vector3D(-2*sphereRadius, sphereRadius, 0);
		sphereRadii[1] = sphereRadius;
		sphereColours[1] = new DoubleColour(0, 1, 0);
		sphereColoursTimeDependent[1] = false;
		sphereColoursTimePeriod[1] = 1;
		sphereColoursLightSourceIndependent[1] = false;
		sphereVisibilities[1] = true;

		sphereCentres[2] = new Vector3D(0, sphereRadius, 2*sphereRadius);
		sphereRadii[2] = sphereRadius;
		sphereColours[2] = new DoubleColour(0, 0, 1);
		sphereColoursTimeDependent[2] = false;
		sphereColoursTimePeriod[2] = 1;
		sphereColoursLightSourceIndependent[2] = false;
		sphereVisibilities[2] = true;
		
		windowTitle = "Dr TIM's Penrose visualiser";
		windowWidth = 1550;
		windowHeight = 850;

	}

	@Override
	public String getClassName()
	{
		return "PenroseVisualiser"	// the name
				;
	}
	
	@Override
	public void writeParameters(PrintStream printStream)
	{
		// write any parameters not defined in NonInteractiveTIMEngine
		printStream.println("Scene initialisation");
		printStream.println();
		
		
		spanA1 = spanA2 = new Vector3D(0,0,0.5);
		focalLength1 = focalLength2 = 0.3;
		
		mirrorReflectionCoefficient = 0.999;
		cavityDepth1 = cavityDepth2 = 0.5;
		cavityWidth1 = cavityWidth2 = 0.3;
		
		printStream.println("wallHeight = "+wallHeight);
		printStream.println("spanA = "+spanA);
		printStream.println("focalLength = "+focalLength);
		printStream.println("spanA1 = "+spanA1);
		printStream.println("focalLength1 = "+focalLength1);
		printStream.println("cavityHeight1 = "+cavityDepth1);
		printStream.println("cavityWidth1 = "+cavityWidth1);
		printStream.println("spanA2 = "+spanA2);
		printStream.println("focalLength2 = "+focalLength2);
		printStream.println("cavityHeight2 = "+cavityDepth2);
		printStream.println("cavityWidth2 = "+cavityWidth2);
		printStream.println("setReflective = "+setReflective);
		printStream.println("mirrorTransmissionCoefficient = "+mirrorReflectionCoefficient);
		printStream.println("shadowThrowing = "+shadowThrowing);
		

		// trajectories
		
		for(int i=0; i<noOfTrajectories; i++)
		{
			printStream.println("showTrajectory["+i+"] = "+showTrajectory[i]);
			printStream.println("trajectoryStartPoint["+i+"] = "+trajectoryStartPoint[i]);
			printStream.println("trajectoryStartDirection["+i+"] = "+trajectoryStartDirection[i]);
			printStream.println("trajectoryColour["+i+"] = "+trajectoryColour[i]);
		}

		printStream.println("trajectoriesRadius = "+trajectoriesRadius);
		printStream.println("trajectoriesShadowThrowing = "+trajectoriesShadowThrowing);
		printStream.println("trajectoriesMaxTraceLevel = "+trajectoriesMaxTraceLevel);
		printStream.println("trajectoriesReportToConsole = "+trajectoriesReportToConsole);
		
		
		// rest of scene
		
		for(int i=0; i<3; i++)
		{
			printStream.println("sphereCentres["+i+"] = "+sphereCentres[i]);
			printStream.println("sphereRadii["+i+"] = "+sphereRadii[i]);
			printStream.println("sphereColours["+i+"] = "+sphereColours[i]);
			printStream.println("sphereColoursTimeDependent["+i+"] = "+sphereColoursTimeDependent[i]);
			printStream.println("sphereColoursTimePeriod["+i+"] = "+sphereColoursTimePeriod[i]);
			printStream.println("sphereColoursLightSourceIndependent["+i+"] = "+ sphereColoursLightSourceIndependent[i]);
			printStream.println("sphereVisibilities["+i+"] = "+sphereVisibilities[i]);
		}

		printStream.println();

		printStream.println("cameraPosition = "+cameraPosition);
		printStream.println("cameraViewCentre = "+cameraViewCentre);
		printStream.println("movie = "+movie);
		printStream.println("movieRotationAngleDeg = "+movieRotationAngleDeg);
		printStream.println("movieRotationAxis = "+movieRotationAxis);

		// write all parameters defined in NonInteractiveTIMEngine
		super.writeParameters(printStream);		
	}
	
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
		studio.setLights(LightSource.getStandardLightsFromBehind());
		
		// add the standard scene objects
		scene.addSceneObject(SceneObjectClass.getSkySphere(scene, studio));
		scene.addSceneObject(SceneObjectClass.getLighterChequerboardFloor(0, scene, studio));


		// add any other scene objects
		
		// first the coloured spheres that can be added to the scene
		for(int i=0; i<3; i++)
			scene.addSceneObject(
					new Sphere(
							"Coloured sphere #"+i,	// description
							sphereCentres[i],	// centre
							sphereRadii[i],	// radius
							sphereColoursTimeDependent[i]
									?new SurfaceColourTimeDependent(sphereColoursTimePeriod[i], sphereColoursLightSourceIndependent[i], true)
									:new SurfaceColour(sphereColours[i], DoubleColour.WHITE, sphereColoursLightSourceIndependent[i], true),	// surface property: sphereColours[i], made shiny
							scene,
							studio
						),
					sphereVisibilities[i]
				);

		Vector3D up = new Vector3D(0, 1, 0);
		SurfaceProperty surface = new SurfaceColour("blue matt", DoubleColour.BLUE, DoubleColour.BLACK, shadowThrowing);
		if(setReflective) surface = new Reflective(mirrorReflectionCoefficient, shadowThrowing);
			
		// create a scene-object intersection, for the overall cavity
		SceneObjectIntersection cavity = new SceneObjectIntersection(
				"outer cavity",	// description
				scene,	// parent
				studio
			);
		
		Plane lid = new Plane(
		"lid (to cut off the (otherwise infinitely high) components)",	// description,
		new Vector3D(0, wallHeight, 0),	// pointOnPlane,
		up,	// normal, 
		surface,	// Transparent.PERFECT,	// surfaceProperty -- doesn't matter since invisible anyway
		scene,	// parent,
		studio
		);
		
		//create the outer mantle which will hold everything.
		EllipticCylinderMantle ecm	= new EllipticCylinderMantle(
				"penrose elliptic cylinder",// description,
				Vector3D.O, // startPoint,
				up,// endPoint,
				spanA,// spanA,
				focalLength,// focalLength,
				true,// infinite,
				surface,// surfaceProperty,
				scene,// parent,
				studio// studio
			);
		cavity.addPositiveSceneObject(ecm);
		cavity.addInvisiblePositiveSceneObject(lid);
		
		scene.addSceneObject(cavity);
		
		
		
	//creating the extrusions. first one then the other...
		
		SceneObjectContainer extrusion1 = new SceneObjectContainer(
				"Extrusion 1",	// description
				scene,	// parent
				studio
			);
		
			// create a scene-object intersection, for the lower part 
			SceneObjectIntersection extrusion1base = new SceneObjectIntersection(
					"Extrusion 1 base",	// description
					scene,	// parent
					studio
				);
			
			// create a scene-object intersection, for the upper part
			SceneObjectIntersection extrusion1Top = new SceneObjectIntersection(
					"Extrusion 1 top",	// description
					scene,	// parent
					studio
				);

			
			//and fill em up.
			Vector3D startPoint1 = Vector3D.sum(ecm.getSpanC(),ecm.getSpanC().getWithLength(-cavityDepth1));
			EllipticCylinderMantle ecm1	= new EllipticCylinderMantle(
					"elliptic cylinder mantle 1",// description,
					startPoint1 ,// startPoint,
					Vector3D.sum(startPoint1,up),// endPoint,
					spanA1,// spanA,
					focalLength1,// focalLength,
					true,// infinite,
					surface,// surfaceProperty,
					scene,// parent,
					studio// studio
				);
			
			Plane epTop1 = new Plane(	
					"Top of the lower-extrusion part",	// description,
					Vector3D.Z.getProductWith(cavityWidth1/2),	// pointOnPlane,
					Vector3D.Z,	// normal, //TODO this or plus Z
					surface,	// Transparent.PERFECT,	// surfaceProperty should be reflective
					scene,	// parent,
					studio
					);

			Plane epBottom1 = new Plane(		
					"Bottom of the lower-extrusion part",	// description,
					Vector3D.Z.getProductWith(-cavityWidth1/2),	// pointOnPlane,
					Vector3D.Z.getProductWith(-1),	// normal, TODO or minus 1? 
					surface,	// Transparent.PERFECT,	// surfaceProperty should be reflective
					scene,	// parent,
					studio
					);
			
			Plane epInner1 = new Plane(
					"flat side of lower-extrusion part",	// description,
					startPoint1,
					Vector3D.difference(startPoint1,Vector3D.O).getNormalised(),	// normal,
					surface,	// Transparent.PERFECT,	// surfaceProperty should be reflective
					scene,	// parent,
					studio
					);
			
			extrusion1base.addPositiveSceneObject(epTop1);
			extrusion1base.addPositiveSceneObject(epBottom1);
			extrusion1base.addInvisiblePositiveSceneObject(ecm);
			extrusion1base.addInvisibleNegativeSceneObject(epInner1);
			extrusion1base.addInvisiblePositiveSceneObject(lid);
			
			extrusion1Top.addPositiveSceneObject(ecm1);
			extrusion1Top.addPositiveSceneObject(epInner1);
			extrusion1Top.addInvisiblePositiveSceneObject(lid);
			
			extrusion1.addSceneObject(extrusion1base);
			extrusion1.addSceneObject(extrusion1Top);
			
			//and add it to the scene...
			scene.addSceneObject(extrusion1);
			
			
			// and now the second extrusion
			SceneObjectContainer extrusion2 = new SceneObjectContainer(
					"Extrusion 2",	// description
					scene,	// parent
					studio
				);
			
				// create a scene-object intersection, for the lower part 
				SceneObjectIntersection extrusion2base = new SceneObjectIntersection(
						"Extrusion 2 base",	// description
						scene,	// parent
						studio
					);
				
				// create a scene-object intersection, for the upper part
				SceneObjectIntersection extrusion2Top = new SceneObjectIntersection(
						"Extrusion 2 top",	// description
						scene,	// parent
						studio
					);
				
				//and fill em up.
				Vector3D startPoint2 = Vector3D.sum(ecm.getSpanC(),ecm.getSpanC().getWithLength(-cavityDepth2)).getProductWith(-1);
				EllipticCylinderMantle ecm2	= new EllipticCylinderMantle(
						"elliptic cylinder mantle 2",// description,
						startPoint2,// startPoint,
						Vector3D.sum(startPoint2,up),// endPoint,
						spanA2,// spanA,
						focalLength2,// focalLength,
						true,// infinite,
						surface,// surfaceProperty,
						scene,// parent,
						studio// studio
					);
				
				Plane epTop2 = new Plane(	
						"Top of the lower-extrusion part",	// description,
						Vector3D.Z.getProductWith(cavityWidth2/2),	// pointOnPlane,
						Vector3D.Z,	// normal, //TODO this or plus Z
						surface,	// Transparent.PERFECT,	// surfaceProperty should be reflective
						scene,	// parent,
						studio
						);

				Plane epBottom2 = new Plane(		
						"Bottom of the lower-extrusion part",	// description,
						Vector3D.Z.getProductWith(-cavityWidth2/2),	// pointOnPlane,
						Vector3D.Z.getProductWith(-1),	// normal, TODO or minus 1? 
						surface,	// Transparent.PERFECT,	// surfaceProperty should be reflective
						scene,	// parent,
						studio
						);
				
				Plane epInner2 = new Plane(
						"flat side of lower-extrusion part",	// description,
						startPoint2,
						Vector3D.difference(startPoint2,Vector3D.O).getNormalised(),	// normal,
						surface,	// Transparent.PERFECT,	// surfaceProperty should be reflective
						scene,	// parent,
						studio
						);
				
				extrusion2base.addPositiveSceneObject(epTop2);
				extrusion2base.addPositiveSceneObject(epBottom2);
				extrusion2base.addInvisiblePositiveSceneObject(ecm);
				extrusion2base.addInvisibleNegativeSceneObject(epInner2);
				extrusion2base.addInvisiblePositiveSceneObject(lid);
				
				extrusion2Top.addPositiveSceneObject(ecm2);
				extrusion2Top.addPositiveSceneObject(epInner2);
				extrusion2Top.addInvisiblePositiveSceneObject(lid);
				
				extrusion2.addSceneObject(extrusion2base);
				extrusion2.addSceneObject(extrusion2Top);
			
				//and add it to the scene...
				scene.addSceneObject(extrusion2);
		
		// trace the ray trajectories
		
		for(int i=0; i<noOfTrajectories; i++)
		{
			if(showTrajectory[i])
				scene.addSceneObject(new RayTrajectory(
						"Trajectory "+i,
						trajectoryStartPoint[i],	// start point
						0,	// start time
						trajectoryStartDirection[i],	// initial direction
						trajectoriesRadius,	// radius
						new SurfaceColourLightSourceIndependent(trajectoryColour[i], trajectoriesShadowThrowing),
						trajectoriesMaxTraceLevel,	// max trace level
						trajectoriesReportToConsole,	// trajectoriesReportToConsole
						scene,
						studio
						)
						);
		}


		// trace the rays with trajectory through the scene
		studio.traceRaysWithTrajectory();
		
		studio.setScene(scene);
		
		// calculate standard camera parameters from camera position
		
		// cameraViewCentre = Vector3D.sum(cameraPosition, cameraViewDirection.getWithLength(cameraDistance));
		
		cameraViewDirection = Vector3D.difference(cameraViewCentre, cameraPosition);
		if(movie)
		{
			cameraViewDirection = Geometry.rotate(cameraViewDirection, movieRotationAxis.getNormalised(), Math.toRadians(movieRotationAngleDeg*(frame/(numberOfFrames-1.0) - 0.5)));
		}
		// cameraPosition = Vector3D.sum(cameraPosition, Vector3D.crossProduct(cameraViewDirection, Vector3D.Y).getWithLength(3*frame/(numberOfFrames-1)));
		cameraDistance = cameraViewDirection.getLength();
		EditableRelativisticAnyFocusSurfaceCamera camera = getStandardCamera();
		
//		// change the camera position and view direction etc. according to frame number
//		camera.setApertureCentre(
//				Geometry.rotatePositionVector(
//								camera.getApertureCentre(),	// position
//								cameraViewCentre,	// pointOnRotationAxis
//								Vector3D.Y,	// normalisedRotationAxisDirection,
//								Math.toRadians(10*frame/(numberOfFrames-1))	// rotationAngle
//							)
//			);
		studio.setCamera(camera);
	}
		
//	focalLength = 1.5;

//	focalLength1 = focalLength2 = 0.3;
//	
//	mirrorTransmissionCoefficient = 0.999;
//	cavityDepth1 = cavityDepth2 = 0.5;
//	cavityWidth1 = cavityWidth2 = 0.3;
	
	
	private LabelledDoublePanel cylinderHeightPanel, mirrorReflectionCoefficientPanel, focalLengthPanel, focalLength1Panel, focalLength2Panel,
	cavityDepth1Panel, cavityDepth2Panel, cavityWidth1Panel, cavityWidth2Panel;
	private LabelledVector3DPanel spanAPanel, spanA1Panel, spanA2Panel;
	private JCheckBox shadowThrowingCheckBox, setReflectiveCheckBox;



	// trajectory
	private JCheckBox 
		showTrajectoryCheckBox[] = new JCheckBox[noOfTrajectories], trajectoriesReportToConsoleCheckBox, trajectoriesShadowThrowingCheckBox;
	private LabelledVector3DPanel trajectoryStartPointPanel[] = new LabelledVector3DPanel[noOfTrajectories];
	private LabelledVector3DPanel trajectoryStartDirectionPanel[] = new LabelledVector3DPanel[noOfTrajectories];
	private LabelledDoubleColourPanel trajectoryColourPanel[] = new LabelledDoubleColourPanel[noOfTrajectories];
	private LabelledDoublePanel trajectoriesRadiusPanel;
	private LabelledIntPanel trajectoriesMaxTraceLevelPanel;

	// rest of scene
	private LabelledVector3DPanel[] sphereCentrePanels;
	private LabelledDoublePanel[] sphereRadiusPanels, sphereColoursTimePeriodPanels;
	private LabelledDoubleColourPanel[] sphereColourPanels;
	private JCheckBox[] sphereVisibilityCheckBoxes, sphereColoursLightSourceIndependentCheckBoxes;
	private JTabbedPane[] sphereColourTabbedPane;
	private static final String TIME_DEPENDENT_COLOUR = "Time-dependent colour";
	private static final String FIXED_COLOUR = "Fixed colour";


	// camera
	private LabelledVector3DPanel cameraPositionPanel, cameraViewCentrePanel, movieRotationAxisPanel;
	private DoublePanel cameraHorizontalFOVDegPanel;
	private JComboBox<ApertureSizeType> cameraApertureSizeComboBox;
	private LabelledDoublePanel cameraFocussingDistancePanel, movieRotationAngleDegPanel;
	private LabelledIntPanel cameraMaxTraceLevelPanel;
	private JCheckBox movieCheckBox;
	private LabelledIntPanel numberOfFramesPanel;


	
	@Override
	protected void createInteractiveControlPanel()
	{
		super.createInteractiveControlPanel();
		
		JTabbedPane tabbedPane = new JTabbedPane();
		interactiveControlPanel.add(tabbedPane, "span");
		
		//
		// Penroses panel
		//
		
		JPanel penrosePanel = new JPanel();
		penrosePanel.setLayout(new MigLayout("insets 0"));
		tabbedPane.addTab("Penrose", penrosePanel);

		JPanel edgePanel = new JPanel();
		edgePanel.setLayout(new MigLayout("insets 0"));
		edgePanel.setBorder(GUIBitsAndBobs.getTitledBorder("Edge (along z axis)"));
		
		
		// common parameters
		
		cylinderHeightPanel = new LabelledDoublePanel("Wall height");
		cylinderHeightPanel.setNumber(wallHeight);
		penrosePanel.add(cylinderHeightPanel, "wrap");
		
		
		focalLengthPanel = new LabelledDoublePanel("Focal length of the elliptic cylinder");
		focalLengthPanel.setNumber(focalLength);
		penrosePanel.add(focalLengthPanel, "wrap");
		
		spanAPanel = new LabelledVector3DPanel("Elliptic cylinder major axis Span vector (f<)");
		spanAPanel.setVector3D(spanA);
		penrosePanel.add(spanAPanel, "span");
		
		
		focalLength1Panel = new LabelledDoublePanel("Focal length of the 1st cavity elliptic cylinder");
		focalLength1Panel.setNumber(focalLength1);
		penrosePanel.add(focalLength1Panel, "wrap");
		
		spanA1Panel = new LabelledVector3DPanel("1st cavity elliptic cylinder major axis span (f<)");
		spanA1Panel.setVector3D(spanA1);
		penrosePanel.add(spanA1Panel, "span");
		
		
		focalLength2Panel = new LabelledDoublePanel("Focal length of the 2nd cavity elliptic cylinder");
		focalLength2Panel.setNumber(focalLength2);
		penrosePanel.add(focalLength2Panel, "wrap");
		
		spanA2Panel = new LabelledVector3DPanel("2nd cavity elliptic cylinder major axis span (f<)");
		spanA2Panel.setVector3D(spanA2);
		penrosePanel.add(spanA2Panel, "span");
		
		
		cavityDepth1Panel = new LabelledDoublePanel("1st cavity depth");
		cavityDepth1Panel.setNumber(cavityDepth1);
		penrosePanel.add(cavityDepth1Panel, "wrap");
		
		cavityWidth1Panel = new LabelledDoublePanel("1st cavity width");
		cavityWidth1Panel.setNumber(cavityWidth1);
		penrosePanel.add(cavityWidth1Panel, "wrap");
		
		
		cavityDepth2Panel = new LabelledDoublePanel("2nd cavity depth");
		cavityDepth2Panel.setNumber(cavityDepth2);
		penrosePanel.add(cavityDepth2Panel, "wrap");
		
		cavityWidth2Panel = new LabelledDoublePanel("2nd cavity width");
		cavityWidth2Panel.setNumber(cavityWidth2);
		penrosePanel.add(cavityWidth2Panel, "wrap");
		
		setReflectiveCheckBox = new JCheckBox("Reflective with");
		setReflectiveCheckBox.setSelected(setReflective);
		penrosePanel.add(setReflectiveCheckBox,"wrap");
		
		mirrorReflectionCoefficientPanel = new LabelledDoublePanel("mirror reflection coefficent");
		mirrorReflectionCoefficientPanel.setNumber(mirrorReflectionCoefficient);
		penrosePanel.add(mirrorReflectionCoefficientPanel,"wrap");
		
		shadowThrowingCheckBox = new JCheckBox("shadow throwing");
		shadowThrowingCheckBox.setSelected(shadowThrowing);
		penrosePanel.add(shadowThrowingCheckBox, "wrap");
		
		//
		// trajectories panel
		//
		
		JPanel trajectoriesPanel = new JPanel();
		trajectoriesPanel.setLayout(new MigLayout("insets 0"));
		
		JTabbedPane trajectoriesTabbedPane = new JTabbedPane();
		trajectoriesPanel.add(trajectoriesTabbedPane, "span");

		// trajectories
		JPanel trajectoryPanel[] = new JPanel[noOfTrajectories];
		for(int i=0; i<noOfTrajectories; i++)
		{
			trajectoryPanel[i] = new JPanel();
			trajectoryPanel[i].setLayout(new MigLayout("insets 0"));

			showTrajectoryCheckBox[i] = new JCheckBox("Show trajectory");
			showTrajectoryCheckBox[i].setSelected(showTrajectory[i]);
			trajectoryPanel[i].add(showTrajectoryCheckBox[i], "wrap");

			trajectoryStartPointPanel[i] = new LabelledVector3DPanel("Start point");
			trajectoryStartPointPanel[i].setVector3D(trajectoryStartPoint[i]);
			trajectoryPanel[i].add(trajectoryStartPointPanel[i], "span");

			trajectoryStartDirectionPanel[i] = new LabelledVector3DPanel("Initial direction");
			trajectoryStartDirectionPanel[i].setVector3D(trajectoryStartDirection[i]);
			trajectoryPanel[i].add(trajectoryStartDirectionPanel[i], "span");
			
			trajectoryColourPanel[i] = new LabelledDoubleColourPanel("Colour");
			trajectoryColourPanel[i].setDoubleColour(trajectoryColour[i]);
			trajectoryPanel[i].add(trajectoryColourPanel[i], "wrap");

			trajectoriesTabbedPane.addTab("Trajectory "+i, trajectoryPanel[i]);
		}
		
		trajectoriesRadiusPanel = new LabelledDoublePanel("Radius");
		trajectoriesRadiusPanel.setNumber(trajectoriesRadius);
		trajectoriesPanel.add(trajectoriesRadiusPanel, "span");
		
		trajectoriesShadowThrowingCheckBox = new JCheckBox("Shadow throwing");
		trajectoriesShadowThrowingCheckBox.setSelected(trajectoriesShadowThrowing);
		trajectoriesPanel.add(trajectoriesShadowThrowingCheckBox, "span");
		
		trajectoriesMaxTraceLevelPanel = new LabelledIntPanel("Max. trace level");
		trajectoriesMaxTraceLevelPanel.setNumber(trajectoriesMaxTraceLevel);
		trajectoriesPanel.add(trajectoriesMaxTraceLevelPanel, "span");
		
		trajectoriesReportToConsoleCheckBox = new JCheckBox("Report raytracing progress to console");
		trajectoriesReportToConsoleCheckBox.setSelected(trajectoriesReportToConsole);
		trajectoriesPanel.add(trajectoriesReportToConsoleCheckBox, "wrap");

		tabbedPane.addTab("Trajectories", trajectoriesPanel);
		
		//
		// rest of scene panel
		//
		
		JPanel restOfScenePanel = new JPanel();
		restOfScenePanel.setLayout(new MigLayout("insets 0"));
				
		JPanel spherePanels[] = new JPanel[3];
		sphereCentrePanels = new LabelledVector3DPanel[3];
		sphereRadiusPanels = new LabelledDoublePanel[3];
		sphereColourPanels = new LabelledDoubleColourPanel[3];
		sphereColoursLightSourceIndependentCheckBoxes = new JCheckBox[3];
		sphereColoursTimePeriodPanels = new LabelledDoublePanel[3];
		sphereColourTabbedPane = new JTabbedPane[3];

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
			
			sphereColourTabbedPane[i] = new JTabbedPane();
			spherePanels[i].add(sphereColourTabbedPane[i], "wrap");
			
			JPanel fixedColourPanel = new JPanel();
			fixedColourPanel.setLayout(new MigLayout("insets 0"));
			sphereColourTabbedPane[i].add(fixedColourPanel, FIXED_COLOUR);

			sphereColourPanels[i] = new LabelledDoubleColourPanel("Colour");
			sphereColourPanels[i].setDoubleColour(sphereColours[i]);
			fixedColourPanel.add(sphereColourPanels[i]);

			JPanel timeDependentColourPanel = new JPanel();
			timeDependentColourPanel.setLayout(new MigLayout("insets 0"));
			sphereColourTabbedPane[i].add(timeDependentColourPanel, TIME_DEPENDENT_COLOUR);

			sphereColoursTimePeriodPanels[i] = new LabelledDoublePanel("Period");
			sphereColoursTimePeriodPanels[i].setNumber(sphereColoursTimePeriod[i]);
			timeDependentColourPanel.add(sphereColoursTimePeriodPanels[i]);

			sphereVisibilityCheckBoxes[i] = new JCheckBox("Visible");
			sphereVisibilityCheckBoxes[i].setSelected(sphereVisibilities[i]);
			// spherePanels[i].add(sphereVisibilityCheckBoxes[i], "wrap");
			
			sphereColoursLightSourceIndependentCheckBoxes[i] = new JCheckBox("Light-source independent");
			sphereColoursLightSourceIndependentCheckBoxes[i].setSelected(sphereColoursLightSourceIndependent[i]);
			spherePanels[i].add(GUIBitsAndBobs.makeRow(sphereVisibilityCheckBoxes[i], sphereColoursLightSourceIndependentCheckBoxes[i]), "wrap");
			
			restOfScenePanel.add(spherePanels[i], "wrap");
		}
		
		tabbedPane.addTab("Rest of scene", restOfScenePanel);

		//
		// camera panel
		//
		
		JPanel cameraPanel = new JPanel();
		cameraPanel.setLayout(new MigLayout("insets 0"));

		cameraPositionPanel = new LabelledVector3DPanel("Aperture centre");
		cameraPositionPanel.setVector3D(cameraPosition);
		cameraPanel.add(cameraPositionPanel, "span");

		cameraViewCentrePanel = new LabelledVector3DPanel("View centre");
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
		moviePanel.setLayout(new MigLayout("insets 0"));
		moviePanel.setBorder(GUIBitsAndBobs.getTitledBorder("Movie"));

		movieCheckBox = new JCheckBox("Movie");
		movieCheckBox.setSelected(movie);
		moviePanel.add(movieCheckBox, "span");
		
		movieRotationAxisPanel = new LabelledVector3DPanel("Rotation axis");
		movieRotationAxisPanel.setVector3D(movieRotationAxis);
		moviePanel.add(movieRotationAxisPanel, "span");
		
		movieRotationAngleDegPanel = new LabelledDoublePanel("Rotation angle (degree)");
		movieRotationAngleDegPanel.setNumber(movieRotationAngleDeg);
		moviePanel.add(movieRotationAngleDegPanel, "span");
		
		numberOfFramesPanel = new LabelledIntPanel("Number of frames");
		numberOfFramesPanel.setNumber(numberOfFrames);
		moviePanel.add(numberOfFramesPanel, "span");
		
		cameraPanel.add(moviePanel, "span");

		tabbedPane.addTab("Camera", cameraPanel);
	}
	
	@Override
	protected void acceptValuesInInteractiveControlPanel()
	{
		super.acceptValuesInInteractiveControlPanel();
		
//		private LabelledVector3DPanel spanAPanel, spanA1Panel, spanA2Panel;

		wallHeight = cylinderHeightPanel.getNumber();
		mirrorReflectionCoefficient = mirrorReflectionCoefficientPanel.getNumber();
		focalLength = focalLengthPanel.getNumber();
		focalLength1 = focalLength1Panel.getNumber();
		focalLength2 = focalLength2Panel.getNumber();
		cavityDepth1 = cavityDepth1Panel.getNumber();
		cavityDepth2 = cavityDepth2Panel.getNumber();
		cavityWidth1 = cavityWidth1Panel.getNumber();
		cavityWidth2 = cavityWidth2Panel.getNumber();
		spanA = spanAPanel.getVector3D();
		spanA1 = spanA1Panel.getVector3D();
		spanA2 = spanA2Panel.getVector3D();
		setReflective = setReflectiveCheckBox.isSelected();
		shadowThrowing = shadowThrowingCheckBox.isSelected();

		// trajectories
		
		for(int i=0; i<noOfTrajectories; i++)
		{
			showTrajectory[i] = showTrajectoryCheckBox[i].isSelected();
			trajectoryStartPoint[i] = trajectoryStartPointPanel[i].getVector3D();
			trajectoryStartDirection[i] = trajectoryStartDirectionPanel[i].getVector3D();
			trajectoryColour[i] = trajectoryColourPanel[i].getDoubleColour();
		}
		trajectoriesRadius = trajectoriesRadiusPanel.getNumber();
		trajectoriesShadowThrowing = trajectoriesShadowThrowingCheckBox.isSelected();
		trajectoriesMaxTraceLevel = trajectoriesMaxTraceLevelPanel.getNumber();
		trajectoriesReportToConsole = trajectoriesReportToConsoleCheckBox.isSelected();
		
		// rest of scene
		
		for(int i=0; i<3; i++)
		{
			sphereCentres[i] = sphereCentrePanels[i].getVector3D();
			sphereRadii[i] = sphereRadiusPanels[i].getNumber();
			sphereColours[i] = sphereColourPanels[i].getDoubleColour();
			sphereVisibilities[i] = sphereVisibilityCheckBoxes[i].isSelected();
			sphereColoursLightSourceIndependent[i] = sphereColoursLightSourceIndependentCheckBoxes[i].isSelected();
			sphereColoursTimeDependent[i] = sphereColourTabbedPane[i].getTitleAt(sphereColourTabbedPane[i].getSelectedIndex()).equals(TIME_DEPENDENT_COLOUR);
			sphereColoursTimePeriod[i] = sphereColoursTimePeriodPanels[i].getNumber();
		}
		
		// camera
		
		cameraPosition = cameraPositionPanel.getVector3D();
		cameraViewCentre = cameraViewCentrePanel.getVector3D();
		// cameraDistance = cameraViewDirection.getLength();
		cameraHorizontalFOVDeg = cameraHorizontalFOVDegPanel.getNumber();
		cameraApertureSize = (ApertureSizeType)(cameraApertureSizeComboBox.getSelectedItem());
		cameraFocussingDistance = cameraFocussingDistancePanel.getNumber();
		cameraMaxTraceLevel = cameraMaxTraceLevelPanel.getNumber();
		movie = movieCheckBox.isSelected();
		movieRotationAxis = movieRotationAxisPanel.getVector3D();
		movieRotationAngleDeg = movieRotationAngleDegPanel.getNumber();
		
		numberOfFrames = numberOfFramesPanel.getNumber();
		firstFrame = 0;
		lastFrame = numberOfFrames - 1;
	}

 	public static void main(final String[] args)
   	{
  		(new PenroseCavityVisualiser()).run();
  	}
 }
