package optics.raytrace.research.TO.shiftyCloak;

import java.awt.event.ActionEvent;
import java.io.PrintStream;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import math.*;
import net.miginfocom.swing.MigLayout;
import optics.raytrace.sceneObjects.*;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.surfaces.Striped;
import optics.raytrace.surfaces.SurfaceColour;
import optics.raytrace.surfaces.SurfaceColourLightSourceIndependent;
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
import optics.raytrace.GUI.sceneObjects.EditableCubicShiftyCloak;
import optics.raytrace.GUI.sceneObjects.EditableSimpleTetrahedralShiftyCloak;
import optics.raytrace.GUI.sceneObjects.EditableTetrahedralShiftyCloak;


public class PolyhedralShiftyCloakVisualiser extends NonInteractiveTIMEngine
{
	// outer cloak / inner cloak
	
	public enum ShiftyPolyhedralCloakType
	{
		CUBIC("Cubic"),
		TETRAHEDRAL("Tetrahedral"),
		TETRAHEDRAL_SIMPLICIAL("Tetrahedral (all cells simplicial)");

		private String description;

		private ShiftyPolyhedralCloakType(String description) {this.description = description;}	
		@Override
		public String toString() {return description;}
	}

	private ShiftyPolyhedralCloakType shiftyPolyhedralCloakType;
	private boolean showShiftyCloakO, showShiftyCloakI, showShiftyCloakInterfacesO, showShiftyCloakInterfacesI, showShiftyCloakIImage;
	private Vector3D centre;
	private double outsideCubeSideLengthO, insideCubeSideLengthO, outsideCubeSideLengthI, insideCubeSideLengthI;
	private Vector3D deltaO, deltaI;
	private boolean showFramesO, showFramesI;

	
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
	
	/**
	 * the colours of images of the spheres
	 */
	private DoubleColour[] sphereImageColours;
	
	/**
	 * visibilities of the images due to both cloaks of the spheres
	 */
	private boolean[] sphereImageVisibilities;

	
	//
	// the camera's movie mode
	//
	
	/**
	 * direction of the rotation axis, which passes through the view centre, of the camera when in movie mode
	 */
	private Vector3D cameraRotationAxisDirection;

	
	
	public PolyhedralShiftyCloakVisualiser()
	{
		nonInteractiveTIMAction = NonInteractiveTIMActionEnum.INTERACTIVE;
		renderQuality = RenderQualityEnum.DRAFT;
		
		// camera parameters; these are often set (or altered) in createStudio()
		cameraViewDirection = new Vector3D(0, -1.5, 10);
		cameraViewCentre = new Vector3D(0, 0, 0);
		cameraDistance = 10;
		cameraFocussingDistance = 10;
		cameraHorizontalFOVDeg = 10;
		cameraMaxTraceLevel = 1000;
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

		// the shifty cloaks
		shiftyPolyhedralCloakType = ShiftyPolyhedralCloakType.TETRAHEDRAL;
		
		centre = new Vector3D(0, 0, 0);

		// Outer shifty cloak
		showShiftyCloakO = true;
		showShiftyCloakInterfacesO = true;
		outsideCubeSideLengthO = 1;
		insideCubeSideLengthO = 0.8;
		deltaO = new Vector3D(0, .2, 0);
		showFramesO = true;

		// Inner shifty cloak
		showShiftyCloakI = false;
		showShiftyCloakInterfacesI = false;
		outsideCubeSideLengthI = 0.7;
		insideCubeSideLengthI = 0.5;
		deltaI = new Vector3D(0.2, 0, 0);
		showFramesI = true;
		showShiftyCloakIImage = false;


		// trajectories
		showTrajectory1 = false;
		showTrajectory2 = false;
		showTrajectory3 = false;
		trajectory1StartPoint = new Vector3D(-10,  0.3, 0.01);
		trajectory2StartPoint = new Vector3D(-10,  0.0, 0.01);
		trajectory3StartPoint = new Vector3D(-10, -0.3, 0.01);
		trajectory1StartDirection = new Vector3D(1, 0, 0);
		trajectory2StartDirection = new Vector3D(1, 0, 0);
		trajectory3StartDirection = new Vector3D(1, 0, 0);
		trajectoryRadius = 0.005;
		trajectoryMaxTraceLevel = 100;
		reportToConsole = false;
		traceRaysWithTrajectory = false;	// don't automatically trace rays with trajectory, but do this in a bespoke way

		
		// rest of the scene
		studioInitialisation = StudioInitialisationType.MINIMALIST;	// the backdrop

		sphereCentres = new Vector3D[3];
		sphereRadii = new double[3];
		sphereColours = new DoubleColour[3];
		sphereVisibilities = new boolean[3];
		sphereImageColours = new DoubleColour[3];
		sphereImageVisibilities = new boolean[3];
		
		sphereCentres[0] = new Vector3D(-0.15, 0, 0);
		sphereRadii[0] = 0.05;
		sphereColours[0] = new DoubleColour(1, 0, 0);
		sphereVisibilities[0] = false;
		sphereImageColours[0] = new DoubleColour(1, 0.4, 0.4);
		sphereImageVisibilities[0] = false;

		sphereCentres[1] = new Vector3D(0, 0, 0);
		sphereRadii[1] = 0.05;
		sphereColours[1] = new DoubleColour(0, 1, 0);
		sphereVisibilities[1] = false;
		sphereImageColours[1] = new DoubleColour(0.4, 1, 0.4);
		sphereImageVisibilities[1] = false;

		sphereCentres[2] = new Vector3D(.15, 0, 0);
		sphereRadii[2] = 0.05;
		sphereColours[2] = new DoubleColour(0, 0, 1);
		sphereVisibilities[2] = false;
		sphereImageColours[2] = new DoubleColour(0.4, 0.4, 1);
		sphereImageVisibilities[2] = false;

		
		windowTitle = "Dr TIM's shifty-polyhedral-cloak visualiser";
		windowWidth = 1250;
		windowHeight = 650;

	}

	@Override
	public String getFirstPartOfFilename()
	{
		return "ShiftyPolyhedralCloakVisualiser"	// the name
				;
	}
	
	@Override
	public void writeParameters(PrintStream printStream)
	{
		// write any parameters not defined in NonInteractiveTIMEngine
		printStream.println("Scene initialisation");
		printStream.println();
		
		// the shifty cloaks
		
		printStream.println("shiftyPolyhedralCloakType = "+shiftyPolyhedralCloakType);
		printStream.println("centre = "+centre);
		printStream.println("showShiftyCloakO = "+showShiftyCloakO);
		printStream.println("showShiftyCloakInterfacesO = "+showShiftyCloakInterfacesO);
		printStream.println("outsideCubeSideLengthO = "+outsideCubeSideLengthO);
		printStream.println("insideCubeSideLengthO = "+insideCubeSideLengthO);
		printStream.println("deltaO = "+deltaO);
		printStream.println("showFramesO = "+showFramesO);
		printStream.println("showShiftyCloakI = "+showShiftyCloakI);
		printStream.println("showShiftyCloakInterfacesI = "+showShiftyCloakInterfacesI);
		printStream.println("outsideCubeSideLengthI = "+outsideCubeSideLengthI);
		printStream.println("insideCubeSideLengthI = "+insideCubeSideLengthI);
		printStream.println("deltaI = "+deltaI);
		printStream.println("showFramesI = "+showFramesI);
		printStream.println("showShiftyCloakIImage = "+showShiftyCloakIImage);


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
			printStream.println("sphereImageColours["+i+"] = "+sphereImageColours[i]);
			printStream.println("sphereImageVisibilities["+i+"] = "+sphereImageVisibilities[i]);
		}

		printStream.println();
		
		// movie parameters
		printStream.println("movie = "+movie);
		printStream.println("cameraRotationAxisDirection = "+cameraRotationAxisDirection);


		// write all parameters defined in NonInteractiveTIMEngine
		super.writeParameters(printStream);		
	}

	
	private SceneObject cloakO, cloakI;
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
		
		// first the coloured spheres and their images
		for(int i=0; i<3; i++)
		{
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
			scene.addSceneObject(
					new ParametrisedSphere(
							"Image of coloured sphere #"+i,	// description
							Vector3D.sum(sphereCentres[i], deltaI, deltaO),	// centre
							sphereRadii[i],	// radius
							Vector3D.sum(deltaI, deltaO),	// new Vector3D(0, 1, 0),	// pole
							new Vector3D(1, 0, 0),	// phi0Direction
							new Striped(
									new SurfaceColour(sphereImageColours[i], DoubleColour.WHITE, false),
									new SurfaceColour(DoubleColour.WHITE, DoubleColour.WHITE, false),
									Math.PI/6.
							),	// surface property: sphereColours[i], made shiny; don't throw shadow
							scene,
							studio
						),
					sphereImageVisibilities[i]
				);
		}
		
		double frameRadius = 0.01*outsideCubeSideLengthO;
		
		// create non-shadow-throwing surface properties
		SurfaceProperty frameSurfacePropertyO = new SurfaceColour(DoubleColour.GREY80, DoubleColour.WHITE, false);
		SurfaceProperty frameSurfacePropertyI = new SurfaceColour(DoubleColour.GREY40, DoubleColour.WHITE, false);
		SurfaceProperty frameSurfacePropertyIImage = 
				new Striped(
						frameSurfacePropertyI,
						new SurfaceColour(DoubleColour.WHITE, DoubleColour.WHITE, false),
						2.*frameRadius
				);

		
		// add shifty cloaks

		// calculate the directions of the edges of the cubes the regular tetrahedra share four vertices with
		Vector3D edge1 = new Vector3D( 1/Math.sqrt(2.), 1/Math.sqrt(3.),-1/Math.sqrt(6.));
		Vector3D edge2 = new Vector3D(-1/Math.sqrt(2.), 1/Math.sqrt(3.),-1/Math.sqrt(6.));
		Vector3D edge3 = Vector3D.crossProduct(edge1, edge2);

		switch(shiftyPolyhedralCloakType)
		{
		case TETRAHEDRAL_SIMPLICIAL:
			cloakO = new EditableTetrahedralShiftyCloak(
					"Outer shifty cloak",	// description
					centre,
					edge1,	// new Vector3D(1, 0, 0),	// u
					edge2,	// new Vector3D(0, 1, 0),	// v
					edge3,	// new Vector3D(0, 0, 1),	// w
					false,	// asymmetricConfiguration
					outsideCubeSideLengthO,	// sideLength
					insideCubeSideLengthO,	// sideLengthI
					deltaO,	// delta
					true,	// showInterfaces
					0.96,	// interfaceTransmissionCoefficient
					false,	// showFrames
					frameRadius,	// frameRadius
					frameSurfacePropertyO,	// frameSurfaceProperty		
					scene, studio
					);

			cloakI = new EditableTetrahedralShiftyCloak(
					"Inner shifty cloak",	// description
					centre,
					edge1,	// new Vector3D(1, 0, 0),	// u
					edge2,	// new Vector3D(0, 1, 0),	// v
					edge3.getReverse(),	// new Vector3D(0, 0, -1),	// w; invert to ensure outside of inner shifty cloak has the same orientation as inside of outer shifty cloak
					false,	// asymmetricConfiguration
					outsideCubeSideLengthI,	// sideLength
					insideCubeSideLengthI,	// sideLengthI
					deltaI,	// delta
					true,	// showInterfaces
					0.96,	// interfaceTransmissionCoefficient
					false,	// showFrames
					frameRadius,	// frameRadius
					frameSurfacePropertyI,	// frameSurfaceProperty		
					scene, studio
					);

			//  also add the image of the inner cloak due to the outer cloak
			scene.addSceneObject(new EditableTetrahedralShiftyCloak(
					"Image of inner shifty cloak due to outer cloak",	// description
					Vector3D.sum(centre, deltaO),
					edge1,	// new Vector3D(1, 0, 0),	// u
					edge2,	// new Vector3D(0, 1, 0),	// v
					edge3.getReverse(),	// new Vector3D(0, 0, -1),	// w; invert to ensure outside of inner shifty cloak has the same orientation as inside of outer shifty cloak
					false,	// asymmetricConfiguration
					outsideCubeSideLengthI,	// sideLength
					insideCubeSideLengthI,	// sideLengthI
					deltaI,	// delta
					false,	// showInterfaces
					1,	// interfaceTransmissionCoefficient
					true,	// showFrames
					0.99*frameRadius,	// frameRadius
					frameSurfacePropertyIImage,	// frameSurfaceProperty		
					scene, studio
					),
					showShiftyCloakIImage
					);

			break;
		case TETRAHEDRAL:
			cloakO = new EditableSimpleTetrahedralShiftyCloak(
					"Outer shifty cloak",	// description
					centre,
					edge1,	// new Vector3D( 1/Math.sqrt(2.), 1/Math.sqrt(3.),-1/Math.sqrt(6.)),	// new Vector3D(1, 0, 0),	// u
					edge2,	// new Vector3D(-1/Math.sqrt(2.), 1/Math.sqrt(3.),-1/Math.sqrt(6.)),	// new Vector3D(0, 1, 0),	// v
					outsideCubeSideLengthO,	// sideLength
					insideCubeSideLengthO,	// sideLengthI
					deltaO,	// delta
					true,	// showInterfaces
					0.96,	// interfaceTransmissionCoefficient
					false,	// showFrames
					frameRadius,	// frameRadius
					frameSurfacePropertyO,	// frameSurfaceProperty		
					scene, studio
					);

			cloakI = new EditableSimpleTetrahedralShiftyCloak(
					"Inner shifty cloak",	// description
					centre,
					edge1,	// new Vector3D(1, 0, 0),	// u
					edge2,	// new Vector3D(0, 1, 0),	// v
					outsideCubeSideLengthI,	// sideLength
					insideCubeSideLengthI,	// sideLengthI
					deltaI,	// delta
					true,	// showInterfaces
					0.96,	// interfaceTransmissionCoefficient
					false,	// showFrames
					frameRadius,	// frameRadius
					frameSurfacePropertyI,	// frameSurfaceProperty		
					scene, studio
					);

			//  also add the image of the inner cloak due to the outer cloak
			scene.addSceneObject(new EditableSimpleTetrahedralShiftyCloak(
					"Image of inner shifty cloak due to outer cloak",	// description
					Vector3D.sum(centre, deltaO),
					edge1,	// new Vector3D(1, 0, 0),	// u
					edge2,	// new Vector3D(0, 1, 0),	// v
					outsideCubeSideLengthI,	// sideLength
					insideCubeSideLengthI,	// sideLengthI
					deltaI,	// delta
					false,	// showInterfaces
					1,	// interfaceTransmissionCoefficient
					true,	// showFrames
					0.99*frameRadius,	// frameRadius
					frameSurfacePropertyIImage,	// frameSurfaceProperty		
					scene, studio
					),
					showShiftyCloakIImage
					);
			break;
		case CUBIC:
		default:
			cloakO = new EditableCubicShiftyCloak(
					"Outer shifty cloak",	// description
					centre,
					new Vector3D(1, 0, 0),	// uDirection
					new Vector3D(0, 1, 0),	// vDirection
					outsideCubeSideLengthO,	// sideLengthOutside
					insideCubeSideLengthO,	// sideLengthInside
					deltaO,	// delta
					true,	// showInterfaces
					0.96,	// interfaceTransmissionCoefficient
					false,	// showFrames
					frameRadius,	// frameRadius
					frameSurfacePropertyO,	// frameSurfaceProperty		
					scene, studio
					);

			cloakI = new EditableCubicShiftyCloak(
					"Inner shifty cloak",	// description
					centre,
					new Vector3D(1, 0, 0),	// uDirection
					new Vector3D(0, 1, 0),	// vDirection
					outsideCubeSideLengthI,	// sideLength
					insideCubeSideLengthI,	// sideLengthI
					deltaI,	// delta
					true,	// showInterfaces
					0.96,	// interfaceTransmissionCoefficient
					false,	// showFrames
					frameRadius,	// frameRadius
					frameSurfacePropertyI,	// frameSurfaceProperty		
					scene, studio
					);

			//  also add the image of the inner cloak due to the outer cloak
			scene.addSceneObject(new EditableCubicShiftyCloak(
					"Image of inner shifty cloak due to outer cloak",	// description
					Vector3D.sum(centre, deltaO),
					new Vector3D(1, 0, 0),	// uDirection
					new Vector3D(0, 1, 0),	// vDirection
					outsideCubeSideLengthI,	// sideLength
					insideCubeSideLengthI,	// sideLengthI
					deltaI,	// delta
					false,	// showInterfaces
					1,	// interfaceTransmissionCoefficient
					true,	// showFrames
					0.99*frameRadius,	// frameRadius
					frameSurfacePropertyIImage,	// frameSurfaceProperty		
					scene, studio
					),
					showShiftyCloakIImage
					);
			break;
		}

		scene.addSceneObject(cloakO, showShiftyCloakO);
		scene.addSceneObject(cloakI, showShiftyCloakI);
		
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
	
		// remove the shifty cloaks...
		scene.removeSceneObject(cloakO);
		scene.removeSceneObject(cloakI);
		
		// ... and add them again, this time showing interfaces and frames as requires
		switch(shiftyPolyhedralCloakType)
		{
		case TETRAHEDRAL_SIMPLICIAL:
		cloakO = new EditableTetrahedralShiftyCloak(
				"Outer shifty cloak",	// description
				centre,
				edge1,	// new Vector3D(1, 0, 0),	// u
				edge2,	// new Vector3D(0, 1, 0),	// v
				edge3,	// new Vector3D(0, 0, 1),	// w
				false,	// asymmetricConfiguration
				outsideCubeSideLengthO,	// sideLength
				insideCubeSideLengthO,	// sideLengthI
				deltaO,	// delta
				showShiftyCloakInterfacesO,	// showInterfaces
				0.96,	// interfaceTransmissionCoefficient
				showFramesO,	// showFrames
				frameRadius,	// frameRadius
				frameSurfacePropertyO,	// frameSurfaceProperty		
				scene, studio
			);
		
		cloakI = new EditableTetrahedralShiftyCloak(
				"Inner shifty cloak",	// description
				centre,
				edge1,	// new Vector3D(1, 0, 0),	// u
				edge2,	// new Vector3D(0, 1, 0),	// v
				edge3.getReverse(),	// new Vector3D(0, 0, -1),	// w; invert to ensure outside of inner shifty cloak has the same orientation as inside of outer shifty cloak
				false,	// asymmetricConfiguration
				outsideCubeSideLengthI,	// sideLength
				insideCubeSideLengthI,	// sideLengthI
				deltaI,	// delta
				showShiftyCloakInterfacesI,	// showInterfaces
				0.96,	// interfaceTransmissionCoefficient
				showFramesI,	// showFrames
				frameRadius,	// frameRadius
				frameSurfacePropertyI,	// frameSurfaceProperty		
				scene, studio
			);
		break;
		case TETRAHEDRAL:
		cloakO = new EditableSimpleTetrahedralShiftyCloak(
				"Outer shifty cloak",	// description
				centre,
				edge1,	// new Vector3D( 1/Math.sqrt(2.), 1/Math.sqrt(3.),-1/Math.sqrt(6.)),	// new Vector3D(1, 0, 0),	// u
				edge2,	// new Vector3D(-1/Math.sqrt(2.), 1/Math.sqrt(3.),-1/Math.sqrt(6.)),	// new Vector3D(0, 1, 0),	// v
				outsideCubeSideLengthO,	// sideLength
				insideCubeSideLengthO,	// sideLengthI
				deltaO,	// delta
				showShiftyCloakInterfacesO,	// showInterfaces
				0.96,	// interfaceTransmissionCoefficient
				showFramesO,	// showFrames
				frameRadius,	// frameRadius
				frameSurfacePropertyO,	// frameSurfaceProperty		
				scene, studio
			);
		
		cloakI = new EditableSimpleTetrahedralShiftyCloak(
				"Inner shifty cloak",	// description
				centre,
				edge1,	// new Vector3D(1, 0, 0),	// u
				edge2,	// new Vector3D(0, 1, 0),	// v
				outsideCubeSideLengthI,	// sideLength
				insideCubeSideLengthI,	// sideLengthI
				deltaI,	// delta
				showShiftyCloakInterfacesI,	// showInterfaces
				0.96,	// interfaceTransmissionCoefficient
				showFramesI,	// showFrames
				frameRadius,	// frameRadius
				frameSurfacePropertyI,	// frameSurfaceProperty		
				scene, studio
			);
		break;
		case CUBIC:
		default:
		cloakO = new EditableCubicShiftyCloak(
				"Outer shifty cloak",	// description
				centre,
				new Vector3D(1, 0, 0),	// uDirection
				new Vector3D(0, 1, 0),	// vDirection
				outsideCubeSideLengthO,	// sideLengthOutside
				insideCubeSideLengthO,	// sideLengthInside
				deltaO,	// delta
				showShiftyCloakInterfacesO,	// showInterfaces
				0.96,	// interfaceTransmissionCoefficient
				showFramesO,	// showFrames
				frameRadius,	// frameRadius
				frameSurfacePropertyO,	// frameSurfaceProperty		
				scene, studio
			);
		
		cloakI = new EditableCubicShiftyCloak(
				"Inner shifty cloak",	// description
				centre,
				new Vector3D(1, 0, 0),	// uDirection
				new Vector3D(0, 1, 0),	// vDirection
				outsideCubeSideLengthI,	// sideLength
				insideCubeSideLengthI,	// sideLengthI
				deltaI,	// delta
				showShiftyCloakInterfacesI,	// showInterfaces
				0.96,	// interfaceTransmissionCoefficient
				showFramesI,	// showFrames
				frameRadius,	// frameRadius
				frameSurfacePropertyI,	// frameSurfaceProperty		
				scene, studio
			);
		}

		scene.addSceneObject(cloakO, showShiftyCloakO);
		scene.addSceneObject(cloakI, showShiftyCloakI);
		
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
	
	// shifty cloaks
	private JComboBox<ShiftyPolyhedralCloakType> shiftyPolyhedralCloakTypeComboBox;
	private LabelledVector3DPanel centrePanel;
		
	// outer shifty device
	private JCheckBox showShiftyCloakOCheckBox, showShiftyCloakInterfacesOCheckBox, showFramesOCheckBox;
	private LabelledVector3DPanel deltaOPanel;
	private LabelledDoublePanel outsideCubeSideLengthOPanel, insideCubeSideLengthOPanel;

	// inner shifty device
	private JCheckBox showShiftyCloakICheckBox, showShiftyCloakInterfacesICheckBox, showFramesICheckBox, showShiftyCloakIImageCheckBox;
	private LabelledVector3DPanel deltaIPanel;
	private LabelledDoublePanel outsideCubeSideLengthIPanel, insideCubeSideLengthIPanel;

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
	private LabelledDoubleColourPanel[] sphereColourPanels, sphereImageColourPanels;
	private JCheckBox[] sphereVisibilityCheckBoxes, sphereImageVisibilityCheckBoxes;


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
		// the shifty cloaks
		//
		
		JPanel shiftyCloaksPanel = new JPanel();
		shiftyCloaksPanel.setLayout(new MigLayout("insets 0"));

		shiftyPolyhedralCloakTypeComboBox = new JComboBox<ShiftyPolyhedralCloakType>(ShiftyPolyhedralCloakType.values());
		shiftyPolyhedralCloakTypeComboBox.setSelectedItem(shiftyPolyhedralCloakType);
		shiftyCloaksPanel.add(GUIBitsAndBobs.makeRow("Cloak geometry", shiftyPolyhedralCloakTypeComboBox), "span");

		centrePanel = new LabelledVector3DPanel("Centre");
		centrePanel.setVector3D(centre);
		shiftyCloaksPanel.add(centrePanel, "wrap");
		
		// the outer shifty cloak
		JPanel outerShiftyCloakPanel = new JPanel();
		outerShiftyCloakPanel.setLayout(new MigLayout("insets 0"));
		outerShiftyCloakPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Outer shifty cloak"));

		showShiftyCloakOCheckBox = new JCheckBox("Show");
		showShiftyCloakOCheckBox.setSelected(showShiftyCloakO);
		outerShiftyCloakPanel.add(showShiftyCloakOCheckBox, "wrap");
		
		showShiftyCloakInterfacesOCheckBox = new JCheckBox("Show interfaces");
		showShiftyCloakInterfacesOCheckBox.setSelected(showShiftyCloakInterfacesO);
		outerShiftyCloakPanel.add(showShiftyCloakInterfacesOCheckBox, "wrap");
		
		deltaOPanel = new LabelledVector3DPanel("Apparent shift of inner cell");
		deltaOPanel.setVector3D(deltaO);
		outerShiftyCloakPanel.add(deltaOPanel, "wrap");

		outsideCubeSideLengthOPanel = new LabelledDoublePanel("Side length of (cubic) outside");
		outsideCubeSideLengthOPanel.setNumber(outsideCubeSideLengthO);
		outerShiftyCloakPanel.add(outsideCubeSideLengthOPanel, "wrap");

		insideCubeSideLengthOPanel = new LabelledDoublePanel("Side length of inner cube");
		insideCubeSideLengthOPanel.setNumber(insideCubeSideLengthO);
		outerShiftyCloakPanel.add(insideCubeSideLengthOPanel, "wrap");

		showFramesOCheckBox = new JCheckBox("Show frames");
		showFramesOCheckBox.setSelected(showFramesO);
		outerShiftyCloakPanel.add(showFramesOCheckBox, "wrap");
		
		shiftyCloaksPanel.add(outerShiftyCloakPanel, "wrap");


		// the inner shifty cloak
		JPanel innerShiftyCloakPanel = new JPanel();
		innerShiftyCloakPanel.setLayout(new MigLayout("insets 0"));
		innerShiftyCloakPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Inner shifty cloak"));

		showShiftyCloakICheckBox = new JCheckBox("Show");
		showShiftyCloakICheckBox.setSelected(showShiftyCloakI);
		innerShiftyCloakPanel.add(showShiftyCloakICheckBox, "wrap");

		showShiftyCloakInterfacesICheckBox = new JCheckBox("Show interfaces");
		showShiftyCloakInterfacesICheckBox.setSelected(showShiftyCloakInterfacesI);
		innerShiftyCloakPanel.add(showShiftyCloakInterfacesICheckBox, "wrap");
		
		deltaIPanel = new LabelledVector3DPanel("Apparent shift of inner cell");
		deltaIPanel.setVector3D(deltaI);
		innerShiftyCloakPanel.add(deltaIPanel, "wrap");

		outsideCubeSideLengthIPanel = new LabelledDoublePanel("Side length of (cubic) outside");
		outsideCubeSideLengthIPanel.setNumber(outsideCubeSideLengthI);
		innerShiftyCloakPanel.add(outsideCubeSideLengthIPanel, "wrap");

		insideCubeSideLengthIPanel = new LabelledDoublePanel("Side length of inner cube");
		insideCubeSideLengthIPanel.setNumber(insideCubeSideLengthI);
		innerShiftyCloakPanel.add(insideCubeSideLengthIPanel, "wrap");

		showFramesICheckBox = new JCheckBox("Show frames");
		showFramesICheckBox.setSelected(showFramesI);
		innerShiftyCloakPanel.add(showFramesICheckBox, "wrap");
		
		showShiftyCloakIImageCheckBox = new JCheckBox("Show image due to outer cloak");
		showShiftyCloakIImageCheckBox.setSelected(showShiftyCloakIImage);
		innerShiftyCloakPanel.add(showShiftyCloakIImageCheckBox, "wrap");

		shiftyCloaksPanel.add(innerShiftyCloakPanel, "wrap");

		
		tabbedPane.addTab("Shifty cloaks", shiftyCloaksPanel);

		
		
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
		sphereImageColourPanels = new LabelledDoubleColourPanel[3];
		sphereImageVisibilityCheckBoxes = new JCheckBox[3];
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

			sphereImageColourPanels[i] = new LabelledDoubleColourPanel("Colour of image");
			sphereImageColourPanels[i].setDoubleColour(sphereImageColours[i]);
			spherePanels[i].add(sphereImageColourPanels[i], "wrap");

			sphereImageVisibilityCheckBoxes[i] = new JCheckBox("Image visible");
			sphereImageVisibilityCheckBoxes[i].setSelected(sphereImageVisibilities[i]);
			spherePanels[i].add(sphereImageVisibilityCheckBoxes[i], "wrap");

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
		
		// shifty cloaks
		
		shiftyPolyhedralCloakType = (ShiftyPolyhedralCloakType)(shiftyPolyhedralCloakTypeComboBox.getSelectedItem());
		centre = centrePanel.getVector3D();
		showShiftyCloakO = showShiftyCloakOCheckBox.isSelected();
		showShiftyCloakInterfacesO = showShiftyCloakInterfacesOCheckBox.isSelected();
		deltaO = deltaOPanel.getVector3D();
		outsideCubeSideLengthO = outsideCubeSideLengthOPanel.getNumber();
		insideCubeSideLengthO = insideCubeSideLengthOPanel.getNumber();
		showFramesO = showFramesOCheckBox.isSelected();
		showShiftyCloakI = showShiftyCloakICheckBox.isSelected();
		showShiftyCloakInterfacesI = showShiftyCloakInterfacesICheckBox.isSelected();
		deltaI = deltaIPanel.getVector3D();
		outsideCubeSideLengthI = outsideCubeSideLengthIPanel.getNumber();
		insideCubeSideLengthI = insideCubeSideLengthIPanel.getNumber();
		showFramesI = showFramesICheckBox.isSelected();
		showShiftyCloakIImage = showShiftyCloakIImageCheckBox.isSelected();
			

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
			sphereImageColours[i] = sphereImageColourPanels[i].getDoubleColour();
			sphereImageVisibilities[i] = sphereImageVisibilityCheckBoxes[i].isSelected();
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
  		(new PolyhedralShiftyCloakVisualiser()).run();
  	}
 }
