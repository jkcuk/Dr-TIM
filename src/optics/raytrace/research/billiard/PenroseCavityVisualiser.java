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
 * Visualise the view within the pendrose cavity.
 * 
 * This consists of a split ellipse with two "mushroom" shaped extrusiones from either side.
 * 
 * @author Maik
 */
public class PenroseCavityVisualiser extends NonInteractiveTIMEngine
{
	
	private static final long serialVersionUID = 4570690288081015390L;

	/**
	 * height of the wall around the cavity
	 */
	private double wallHeight;
	
	/*
	 * The span vector along the smi major axis of the outer elliptic cylinder.
	 */
	private Vector3D a;
	
	/*
	 * the focal length of the outer ellipse
	 */
	private double focalLength;
	
	/*
	 * d, the length along the semi minor axis of the out ellipse, should be larger than the smei minor axis length. 
	 */
	private double d;
	
	private double mirrorReflectionCoefficient;
	
	
	/*
	 * c(s) represent the semi minor axis of the ellipsoidal extrusions on side 1 and 2 respectively
	 * h(s) represents the straight side length perpendicular to the planar extrusions. (As shown in 
	 * Kim, J., Kim, J., Seo, J. et al. Observation of a half-illuminated mode in an open Penrose cavity. Sci Rep 12, 9798 (2022). https://doi.org/10.1038/s41598-022-13963-y
	 */
	private double c1, h1, c2, h2;
	
	/*
	 *set the surfaces to be reflective. If not true they will be blue by default (colour be changed in the code) 
	 */
	private boolean setReflective;
	
	/*
	 * if true, the cavity will also have shadows.
	 */
	private boolean shadowThrowing;
	
	/**
	 * Should the cavity be infinitely tall?
	 */
	private boolean infinite;
	
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
		a = new Vector3D(6,0,0);
		
		focalLength = 5;

		d = 7;
		
		mirrorReflectionCoefficient = 0.999;
		c1 = 1;
		c2 = 1;
		h1 = 3;
		h2 = 3;
		
		infinite = false;

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
		
		printStream.println("wallHeight = "+wallHeight);
		printStream.println("a = "+a);
		printStream.println("focalLength = "+focalLength);
		printStream.println("d = "+d);
		printStream.println("c1 = "+c1);
		printStream.println("h1 = "+h1);
		printStream.println("c2 = "+c2);
		printStream.println("h2 = "+h2);
		printStream.println("setReflective = "+setReflective);
		printStream.println("mirrorTransmissionCoefficient = "+mirrorReflectionCoefficient);
		printStream.println("shadowThrowing = "+shadowThrowing);
		printStream.println("infinite = "+infinite);
		

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

		SurfaceProperty surface = new SurfaceColour("blue matt", DoubleColour.BLUE, DoubleColour.BLACK, shadowThrowing);
		if(setReflective) surface = new Reflective(mirrorReflectionCoefficient, shadowThrowing);		
				
				EllipticPenroseCavity penroseCavity = new EllipticPenroseCavity(
						"penrose cavity",// description,
						Vector3D.O,// startPoint,
						Vector3D.Y.getProductWith(wallHeight),// endPoint,			
						infinite,// infinite,
						a,// a,
						focalLength,// f,
						d,// d,
						h1,// h1,
						c1,// c1,
						h2,// h2,
						c2,// c2,
						surface,// surfaceProperty,
						scene,// parent,
						studio// studio
					);

		scene.addSceneObject(penroseCavity);
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
	
	
	private LabelledDoublePanel dPanel, cylinderHeightPanel, mirrorReflectionCoefficientPanel, focalLengthPanel, 
	c1Panel, c2Panel, h1Panel, h2Panel;
	private LabelledVector3DPanel spanAPanel;
	private JCheckBox shadowThrowingCheckBox, setReflectiveCheckBox, infiniteCheckBox;



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
		spanAPanel.setVector3D(a);
		penrosePanel.add(spanAPanel, "span");
		
		
		dPanel = new LabelledDoublePanel("d (half width of the cavity along the outer semi minor axis)");
		dPanel.setNumber(d);
		penrosePanel.add(dPanel, "span");
		
		
		c1Panel = new LabelledDoublePanel("c1 (semi minor axis of 1st extrusion)");
		c1Panel.setNumber(c1);
		penrosePanel.add(c1Panel, "wrap");
		
		h1Panel = new LabelledDoublePanel("h1 (side extrusion height)");
		h1Panel.setNumber(h1);
		penrosePanel.add(h1Panel, "wrap");
		
		
		c2Panel = new LabelledDoublePanel("c2 (semi minor axis of 2nd extrusion)");
		c2Panel.setNumber(c2);
		penrosePanel.add(c2Panel, "wrap");
		
		h2Panel = new LabelledDoublePanel("h1 (side extrusion height)");
		h2Panel.setNumber(h2);
		penrosePanel.add(h2Panel, "wrap");
		
		setReflectiveCheckBox = new JCheckBox("Reflective with");
		setReflectiveCheckBox.setSelected(setReflective);
		penrosePanel.add(setReflectiveCheckBox,"wrap");
		
		mirrorReflectionCoefficientPanel = new LabelledDoublePanel("mirror reflection coefficent");
		mirrorReflectionCoefficientPanel.setNumber(mirrorReflectionCoefficient);
		penrosePanel.add(mirrorReflectionCoefficientPanel,"wrap");
		
		shadowThrowingCheckBox = new JCheckBox("shadow throwing");
		shadowThrowingCheckBox.setSelected(shadowThrowing);
		penrosePanel.add(shadowThrowingCheckBox, "wrap");
		
		infiniteCheckBox= new JCheckBox("make it infinitely tall");
		infiniteCheckBox.setSelected(infinite);
		penrosePanel.add(infiniteCheckBox, "wrap");
		
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
		c1 = c1Panel.getNumber();
		c2 = c2Panel.getNumber();
		h1 = h1Panel.getNumber();
		h2 = h2Panel.getNumber();
		a = spanAPanel.getVector3D();
		d = dPanel.getNumber();
		setReflective = setReflectiveCheckBox.isSelected();
		shadowThrowing = shadowThrowingCheckBox.isSelected();
		infinite = infiniteCheckBox.isSelected();

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
