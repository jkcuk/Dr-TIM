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
import optics.raytrace.surfaces.IdealThinLensSurfaceSimple;
import optics.raytrace.surfaces.Striped;
import optics.raytrace.surfaces.SurfaceColour;
import optics.raytrace.surfaces.SurfaceColourLightSourceIndependent;
import optics.raytrace.surfaces.Transparent;
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
import optics.raytrace.GUI.sceneObjects.EditableTimHead;


public class PolyhedralShiftyCloakJanusVisualiser extends NonInteractiveTIMEngine
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
	private boolean showShiftyCloak1, showShiftyCloak2, showShiftyCloakInterfaces1, showShiftyCloakInterfaces2;
	private Vector3D centre;
	private double outsideCubeSideLength1, insideCubeSideLength1, outsideCubeSideLength2, insideCubeSideLength2;
	private Vector3D delta1, delta2;
	private boolean showFrames1, showFrames2, showShiftyCloak2Image;


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
	 *Tim costum settings
	 */

	private double timDistance, timRadius;
	
	
	public enum ObjectType
	{
		JANUS("Janus"),
		WORMHOLE("Wormhole"),
		NONE("None");

		private String description;

		private ObjectType(String description) {this.description = description;}	
		@Override
		public String toString() {return description;}
	}
	
	public enum JanusType
	{
		SPHERES("spheres"),
		TIM("Tim");

		private String description;

		private JanusType(String description) {this.description = description;}	
		@Override
		public String toString() {return description;}
	}

	private JanusType janusType;
	private ObjectType objectType;

	/**
	 * focal properties of wormhole lenses
	 */
	private double f1, f2;
	private double lensTransmission;
	private Disc lens1, lens2, lens3, lens4;
	
	/**
	 * the centres of object to be placed in the cloak. for now a sphere TODO
	 */
	private Vector3D[] objectCentre;

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



	public PolyhedralShiftyCloakJanusVisualiser()
	{
		nonInteractiveTIMAction = NonInteractiveTIMActionEnum.INTERACTIVE;
		renderQuality = RenderQualityEnum.DRAFT;
		objectType = ObjectType.JANUS;
		janusType = JanusType.SPHERES;

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
		showShiftyCloak2Image = false;
		
		timDistance = 10;
		timRadius = 1;

		movie = false;
		// if movie = true, then the following are relevant:
		numberOfFrames = 10;
		firstFrame = 0;
		lastFrame = numberOfFrames-1;
		cameraRotationAxisDirection = new Vector3D(0, 1, 0);

		// the shifty cloaks
		shiftyPolyhedralCloakType = ShiftyPolyhedralCloakType.CUBIC;

		centre = new Vector3D(0, 0, 0);

		// first  shifty cloak
		showShiftyCloak1 = true;
		showShiftyCloakInterfaces1 = false;
		outsideCubeSideLength1 = 1;
		insideCubeSideLength1 = 0.8;
		delta1 = new Vector3D(0, 0, -1.02);
		showFrames1 = true;

		// second shifty cloak
		showShiftyCloak2 = true;
		showShiftyCloakInterfaces2 = false;
		outsideCubeSideLength2 = 1;
		insideCubeSideLength2 = 0.8;
		delta2 = new Vector3D(1.02, 0, 0);
		showFrames2 = true;


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

		lensTransmission = 1;
		f1 = 0.1;
		f2 = 0.1;

		// rest of the scene
		studioInitialisation = StudioInitialisationType.MINIMALIST;	// the backdrop

		objectCentre = new Vector3D[2];
		sphereRadii = new double[2];
		sphereColours = new DoubleColour[2];
		sphereVisibilities = new boolean[2];
		sphereImageColours = new DoubleColour[2];
		sphereImageVisibilities = new boolean[2];

		objectCentre[0] = Vector3D.difference(centre, delta1);
		sphereRadii[0] = 0.2;
		sphereColours[0] = new DoubleColour(1, 0, 0);
		sphereVisibilities[0] = false;
		sphereImageColours[0] = new DoubleColour(1, 0.4, 0.4);
		sphereImageVisibilities[0] = false;

		objectCentre[1] = Vector3D.difference(centre, delta2);
		sphereRadii[1] = 0.2;
		sphereColours[1] = new DoubleColour(0, 1, 0);
		sphereVisibilities[1] = false;
		sphereImageColours[1] = new DoubleColour(0.4, 1, 0.4);
		sphereImageVisibilities[1] = false;



		windowTitle = "Dr TIM's shifty-polyhedral-cloak-Janus visualiser";
		windowWidth = 1250;
		windowHeight = 650;

	}

	@Override
	public String getClassName()
	{
		return "ShiftyPolyhedralCloakJanusVisualiser"	// the name
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
		printStream.println("showShiftyCloakO = "+showShiftyCloak1);
		printStream.println("showShiftyCloakInterfacesO = "+showShiftyCloakInterfaces1);
		printStream.println("outsideCubeSideLengthO = "+outsideCubeSideLength1);
		printStream.println("insideCubeSideLengthO = "+insideCubeSideLength1);
		printStream.println("deltaO = "+delta1);
		printStream.println("showFramesO = "+showFrames1);
		printStream.println("showShiftyCloakI = "+showShiftyCloak2);
		printStream.println("showShiftyCloakInterfacesI = "+showShiftyCloakInterfaces2);
		printStream.println("outsideCubeSideLengthI = "+outsideCubeSideLength2);
		printStream.println("insideCubeSideLengthI = "+insideCubeSideLength2);
		printStream.println("deltaI = "+delta2);
		printStream.println("showFramesI = "+showFrames2);


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
		printStream.println("object = "+objectType);
		switch(objectType) {
		case JANUS:
			switch (janusType) {
			
			case SPHERES:
				for(int i=0; i<2; i++)
				{
					printStream.println("sphereCentres["+i+"] = "+objectCentre[i]);
					printStream.println("sphereRadii["+i+"] = "+sphereRadii[i]);
					printStream.println("sphereColours["+i+"] = "+sphereColours[i]);
					printStream.println("sphereVisibilities["+i+"] = "+sphereVisibilities[i]);
					printStream.println("sphereImageColours["+i+"] = "+sphereImageColours[i]);
					printStream.println("sphereImageVisibilities["+i+"] = "+sphereImageVisibilities[i]);
				}
				break;
			case TIM:
				for(int i=0; i<2; i++)
			{printStream.println("TimRadius["+i+"] = "+sphereRadii[i]);
				}
				break;
			}
			break;
		case WORMHOLE:
			printStream.println("tim head at distance= "+timDistance);
			printStream.println("and tim radius= "+timRadius);	
			printStream.println("lensTransmission = "+lensTransmission);
			printStream.println("f1 = "+f1);
			printStream.println("f2 = "+f2);
			break;
		case NONE:
			break;
		}
		
	

		printStream.println();

		// movie parameters
		printStream.println("movie = "+movie);
		printStream.println("cameraRotationAxisDirection = "+cameraRotationAxisDirection);


		// write all parameters defined in NonInteractiveTIMEngine
		super.writeParameters(printStream);		
	}


	private SceneObject cloak1, cloak2;
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
		
//		studio.setLights(new AmbientLight("uniform light"// description
//				));

		switch(objectType) {
		case JANUS:
			objectCentre[1] = Vector3D.difference(centre, delta2);
			objectCentre[0] = Vector3D.difference(centre, delta1);
			
			switch(janusType) {
			case SPHERES:

			scene.addSceneObject(
					new ParametrisedSphere(
							"Coloured sphere #"+0,	// description
							objectCentre[0],	// centre
							sphereRadii[0],	// radius
							Vector3D.Y,	// new Vector3D(0, 1, 0),	// pole
							Vector3D.X,	// phi0Direction
							//new SurfaceColour(sphereColours[i], DoubleColour.WHITE, false),	// surface property: sphereColours[i], made shiny; don't throw shadow
							//new Striped(
									new SurfaceColour(sphereImageColours[0], DoubleColour.WHITE, false),
//									new Transparent(),
//									Math.PI/24.
//									),	// surface property: sphereColours[i], made shiny; don't throw shadow
							scene,
							studio
							),
					sphereVisibilities[0]);

			scene.addSceneObject(
					new ParametrisedSphere(
							"Image of coloured sphere #"+0,	// description
							Vector3D.sum(objectCentre[0], delta1),	// centre
							sphereRadii[0],	// radius
							Vector3D.Y,	// new Vector3D(0, 1, 0),	// pole
							Vector3D.X,	// phi0Direction
//							new Striped(
									new SurfaceColour(sphereImageColours[0], DoubleColour.WHITE, false),
//									new Transparent(),
//									Math.PI/24.
//									),	// surface property: sphereColours[i], made shiny; don't throw shadow
							scene,
							studio
							),
					sphereImageVisibilities[0]);

			scene.addSceneObject(
					new ParametrisedSphere(
							"Coloured sphere #"+1,	// description
							objectCentre[1],	// centre
							sphereRadii[1]+MyMath.TINY,	// radius
							Vector3D.Z,	// new Vector3D(0, 1, 0),	// pole
							Vector3D.Y,	// phi0Direction
							//new SurfaceColour(sphereColours[i], DoubleColour.WHITE, false),	// surface property: sphereColours[i], made shiny; don't throw shadow
							new Striped(
									new Transparent(),
									new SurfaceColour(sphereImageColours[1], DoubleColour.WHITE, false),
									Math.PI/24.
									),	// surface property: sphereColours[i], made shiny; don't throw shadow
							scene,
							studio
							),
					sphereVisibilities[1]
					);

			scene.addSceneObject(
					new ParametrisedSphere(
							"Image of coloured sphere #"+1,	// description
							Vector3D.sum(objectCentre[1], delta2),	// centre
							sphereRadii[1]+MyMath.TINY,	// radius
							Vector3D.Z,	// new Vector3D(0, 1, 0),	// pole
							Vector3D.Y,	// phi0Direction
							new Striped(
									new Transparent(),
									new SurfaceColour(sphereImageColours[1], DoubleColour.WHITE, false),
									Math.PI/24.
									),	// surface property: sphereColours[i], made shiny; don't throw shadow
							scene,
							studio
							),
					sphereImageVisibilities[1]
					);
			break;
			case TIM:
				//add two opposing tim heads and make them face in the direction opposite to the shift. 
				scene.addSceneObject(
				new EditableTimHead(
						"Tim head 0",// description,
						objectCentre[0],// centre,
						sphereRadii[0],// radius,
						delta1.getNormalised().getProductWith(-1),// frontDirection,
						Vector3D.Y,// topDirection,
						Vector3D.crossProduct(delta1.getNormalised().getProductWith(-1), Vector3D.Y),// rightDirection,
						scene,
						studio
				));
				
				scene.addSceneObject(
				new EditableTimHead(
						"Tim head 1",// description,
						objectCentre[1],// centre,
						sphereRadii[1],// radius,
						delta2.getNormalised().getProductWith(-1),// frontDirection,
						Vector3D.Y,// topDirection,
						Vector3D.crossProduct(Vector3D.Y, delta2.getNormalised().getProductWith(-1)),// rightDirection,
						scene,
						studio
				));
				break;
			}

			break;
		case WORMHOLE:
			
			scene.addSceneObject(new EditableTimHead(
					"Tim's head",
					new Vector3D(0, 0, timDistance),
					timRadius,	// radius
					new Vector3D(0, 0, -1),	// front direction
					new Vector3D(0, 1, 0),	// top direction
					new Vector3D(1, 0, 0),	// right direction
					scene,
					studio
				));
			
			Vector3D normal = null;
			if(delta1 == Vector3D.O) {
				normal = Vector3D.X;
			}
			else {
				normal = delta1.getNormalised().getProductWith(1);
			}
			objectCentre[0] = Vector3D.difference(centre, normal.getProductWith(insideCubeSideLength2/2.01));
			double d = (2*(f1+f2)*(f2/f1)); //separation along z axis of virtual lenses 2 and 3
			Vector3D lastLens = Vector3D.sum(objectCentre[0], normal.getProductWith(2*f1+2*f2+d));
			if(Vector3D.getDistance(objectCentre[0], lastLens)>= insideCubeSideLength2) {
				System.err.println("Inside lenses outside of inner cube!");
				break;
			}
			lens1 = new Disc(
					"first lens",// description,
					objectCentre[0],// centre,
					normal,
					insideCubeSideLength2/2.01,
					new IdealThinLensSurfaceSimple(
							objectCentre[0],
							normal, //TODO check this 
							f1,	// focalLength,
							lensTransmission,	// transmissionCoefficient
							false	// shadow-throwing
							),// surfaceProperty,
					scene,
					studio
					);

			lens2 = new Disc(
					"second lens",// description,
					Vector3D.sum(objectCentre[0], normal.getProductWith(f1+f2)),// centre,
					normal,
					insideCubeSideLength2/2.01,
					new IdealThinLensSurfaceSimple(
							Vector3D.sum(objectCentre[0], normal.getProductWith(f1+f2)),
							normal,
							f2,	// focalLength,
							lensTransmission,	// transmissionCoefficient
							false	// shadow-throwing
							),// surfaceProperty,
					scene,
					studio
					);


			lens3 = new Disc(
					"third lens",// description,
					Vector3D.sum(objectCentre[0], normal.getProductWith(f1+f2+d)),// centre,
					normal,
					insideCubeSideLength2/2.01,
					new IdealThinLensSurfaceSimple(
							Vector3D.sum(objectCentre[0], normal.getProductWith(f1+f2+d)),
							normal,
							f2,	// focalLength,
							lensTransmission,	// transmissionCoefficient
							false	// shadow-throwing
							),// surfaceProperty,
					scene,
					studio
					);

			lens4 = new Disc(
					"fourth lens",// description,
					lastLens,// centre,
					normal,
					insideCubeSideLength2/2.01,
					new IdealThinLensSurfaceSimple(
							lastLens,
							normal,
							f1,	// focalLength,
							lensTransmission,	// transmissionCoefficient
							false	// shadow-throwing
							),// surfaceProperty,
					scene,
					studio
					);
			
			scene.addSceneObject(lens1);
			scene.addSceneObject(lens2);
			scene.addSceneObject(lens3);
			scene.addSceneObject(lens4);
			break;
		case NONE:
			break;
		}

		double frameRadius = 0.01*outsideCubeSideLength1;

		// create non-shadow-throwing surface properties
		SurfaceProperty frameSurfaceProperty1 = new SurfaceColour(DoubleColour.GREY80, DoubleColour.WHITE, false);
		SurfaceProperty frameSurfaceProperty2 = new SurfaceColour(DoubleColour.GREY40, DoubleColour.WHITE, false);
		SurfaceProperty frameSurfacePropertyIImage = 
				new Striped(
						frameSurfaceProperty2,
						new SurfaceColour(DoubleColour.WHITE, DoubleColour.WHITE, false),
						2.*frameRadius
						);

		// add shifty cloaks

		// calculate the directions of the edges of the cubes the regular tetrahedra share four vertices with
		Vector3D edge1 = new Vector3D( 1/Math.sqrt(2.), 1/Math.sqrt(3.),-1/Math.sqrt(6.));
		Vector3D edge2 = new Vector3D(-1/Math.sqrt(2.), 1/Math.sqrt(3.),-1/Math.sqrt(6.));
		Vector3D edge3 = Vector3D.crossProduct(edge1, edge2);

		Vector3D cloakCenter1 = null;
		Vector3D cloakCenter2 = null;
		boolean hole = false;
		switch(objectType) {
		default:
		case JANUS:
			cloakCenter1 = Vector3D.difference(centre, delta1);
			cloakCenter2 = Vector3D.difference(centre, delta2);
			hole = false;
			break;
		case WORMHOLE:
			delta2 = delta1.getProductWith(-1);
			cloakCenter1 = centre;
			cloakCenter2 = centre;	
			hole = true;
			break;
		case NONE:
			break;
		}


		switch(shiftyPolyhedralCloakType)
		{
		case TETRAHEDRAL_SIMPLICIAL:
			cloak1 = new EditableTetrahedralShiftyCloak(
					"first shifty cloak",	// description
					cloakCenter1,
					edge1,	// new Vector3D(1, 0, 0),	// u
					edge2,	// new Vector3D(0, 1, 0),	// v
					edge3,	// new Vector3D(0, 0, 1),	// w
					false,	// asymmetricConfiguration
					outsideCubeSideLength1,	// sideLength
					insideCubeSideLength1,	// sideLengthI
					delta1,	// delta
					true,	// showInterfaces
					0.96,	// interfaceTransmissionCoefficient
					false,	// showFrames
					frameRadius,	// frameRadius
					frameSurfaceProperty1,	// frameSurfaceProperty		
					scene, studio
					);

			cloak2 = new EditableTetrahedralShiftyCloak(
					"second shifty cloak",	// description
					cloakCenter2,
					edge1,	// new Vector3D(1, 0, 0),	// u
					edge2,	// new Vector3D(0, 1, 0),	// v
					edge3.getReverse(),	// new Vector3D(0, 0, -1),	// w; invert to ensure outside of inner shifty cloak has the same orientation as inside of outer shifty cloak
					false,	// asymmetricConfiguration
					outsideCubeSideLength2,	// sideLength
					insideCubeSideLength2,	// sideLengthI
					delta2,	// delta
					true,	// showInterfaces
					0.96,	// interfaceTransmissionCoefficient
					false,	// showFrames
					frameRadius,	// frameRadius
					frameSurfaceProperty2,	// frameSurfaceProperty		
					scene, studio
					);


			if(hole){//  also add the image of the inner cloak due to the outer cloak
				scene.addSceneObject(new EditableTetrahedralShiftyCloak(
						"Image of inner shifty cloak due to outer cloak",	// description
						Vector3D.sum(cloakCenter1, delta1),
						edge1,	// new Vector3D(1, 0, 0),	// u
						edge2,	// new Vector3D(0, 1, 0),	// v
						edge3.getReverse(),	// new Vector3D(0, 0, -1),	// w; invert to ensure outside of inner shifty cloak has the same orientation as inside of outer shifty cloak
						false,	// asymmetricConfiguration
						outsideCubeSideLength2,	// sideLength
						insideCubeSideLength2,	// sideLengthI
						delta2,	// delta
						false,	// showInterfaces
						0.96,	// interfaceTransmissionCoefficient
						true,	// showFrames
						0.99*frameRadius,	// frameRadius
						frameSurfacePropertyIImage,	// frameSurfaceProperty		
						scene, studio
						),
						showShiftyCloak2Image
						);}
			break;
		case TETRAHEDRAL:
			cloak1 = new EditableSimpleTetrahedralShiftyCloak(
					"Outer shifty cloak",	// description
					cloakCenter1,
					edge1,	// new Vector3D( 1/Math.sqrt(2.), 1/Math.sqrt(3.),-1/Math.sqrt(6.)),	// new Vector3D(1, 0, 0),	// u
					edge2,	// new Vector3D(-1/Math.sqrt(2.), 1/Math.sqrt(3.),-1/Math.sqrt(6.)),	// new Vector3D(0, 1, 0),	// v
					outsideCubeSideLength1,	// sideLength
					insideCubeSideLength1,	// sideLengthI
					delta1,	// delta
					true,	// showInterfaces
					0.96,	// interfaceTransmissionCoefficient
					false,	// showFrames
					frameRadius,	// frameRadius
					frameSurfaceProperty1,	// frameSurfaceProperty		
					scene, studio
					);

			cloak2 = new EditableSimpleTetrahedralShiftyCloak(
					"second shifty cloak",	// description
					cloakCenter2,
					edge1,	// new Vector3D(1, 0, 0),	// u
					edge2,	// new Vector3D(0, 1, 0),	// v
					outsideCubeSideLength2,	// sideLength
					insideCubeSideLength2,	// sideLengthI
					delta2,	// delta
					true,	// showInterfaces
					0.96,	// interfaceTransmissionCoefficient
					false,	// showFrames
					frameRadius,	// frameRadius
					frameSurfaceProperty2,	// frameSurfaceProperty		
					scene, studio
					);

			if(hole){//  also add the image of the inner cloak due to the outer cloak
				scene.addSceneObject(new EditableSimpleTetrahedralShiftyCloak(
						"Image of inner shifty cloak due to outer cloak",	// description
						Vector3D.sum(cloakCenter1, delta1),
						edge1,	// new Vector3D(1, 0, 0),	// u
						edge2,	// new Vector3D(0, 1, 0),	// v
						outsideCubeSideLength2,	// sideLength
						insideCubeSideLength2,	// sideLengthI
						delta2,	// delta
						false,	// showInterfaces
						1,	// interfaceTransmissionCoefficient
						true,	// showFrames
						0.99*frameRadius,	// frameRadius
						frameSurfacePropertyIImage,	// frameSurfaceProperty		
						scene, studio
						),
						showShiftyCloak2Image
						);}

			break;
		case CUBIC:
		default:
			cloak1 = new EditableCubicShiftyCloak(
					"Outer shifty cloak",	// description
					cloakCenter1,
					new Vector3D(1, 0, 0),	// uDirection
					new Vector3D(0, 1, 0),	// vDirection
					outsideCubeSideLength1,	// sideLengthOutside
					insideCubeSideLength1,	// sideLengthInside
					delta1,	// delta
					true,	// showInterfaces
					0.96,	// interfaceTransmissionCoefficient
					false,	// showFrames
					frameRadius,	// frameRadius
					frameSurfaceProperty1,	// frameSurfaceProperty		
					scene, studio
					);

			cloak2 = new EditableCubicShiftyCloak(
					"Inner shifty cloak",	// description
					cloakCenter2,
					new Vector3D(1, 0, 0),	// uDirection
					new Vector3D(0, 1, 0),	// vDirection
					outsideCubeSideLength2,	// sideLength
					insideCubeSideLength2,	// sideLengthI
					delta2,	// delta
					true,	// showInterfaces
					0.96,	// interfaceTransmissionCoefficient
					false,	// showFrames
					frameRadius,	// frameRadius
					frameSurfaceProperty2,	// frameSurfaceProperty		
					scene, studio
					);

			if(hole){//  also add the image of the inner cloak due to the outer cloak
				scene.addSceneObject(new EditableCubicShiftyCloak(
						"Image of inner shifty cloak due to outer cloak",	// description
						Vector3D.sum(cloakCenter1, delta1),
						new Vector3D(1, 0, 0),	// uDirection
						new Vector3D(0, 1, 0),	// vDirection
						outsideCubeSideLength2,	// sideLength
						insideCubeSideLength2,	// sideLengthI
						delta2,	// delta
						false,	// showInterfaces
						0.96,	// interfaceTransmissionCoefficient
						true,	// showFrames
						0.99*frameRadius,	// frameRadius
						frameSurfacePropertyIImage,	// frameSurfaceProperty		
						scene, studio
						),
						showShiftyCloak2Image
						);}
			break;
		}

		scene.addSceneObject(cloak1, showShiftyCloak1);
		scene.addSceneObject(cloak2, showShiftyCloak2);

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
		scene.removeSceneObject(cloak1);
		scene.removeSceneObject(cloak2);

		if(showTrajectory1||showTrajectory2||showTrajectory3) {
			switch(objectType) {
			case JANUS:
			case NONE:
				break;
			case WORMHOLE:
				scene.removeSceneObject(lens1);
				scene.removeSceneObject(lens2);
				scene.removeSceneObject(lens3);
				scene.removeSceneObject(lens4);
				break;
			}
		}

		// ... and add them again, this time showing interfaces and frames as requires
		switch(shiftyPolyhedralCloakType)
		{
		case TETRAHEDRAL_SIMPLICIAL:
			cloak1 = new EditableTetrahedralShiftyCloak(
					"first shifty cloak",	// description
					cloakCenter1,
					edge1,	// new Vector3D(1, 0, 0),	// u
					edge2,	// new Vector3D(0, 1, 0),	// v
					edge3,	// new Vector3D(0, 0, 1),	// w
					false,	// asymmetricConfiguration
					outsideCubeSideLength1,	// sideLength
					insideCubeSideLength1,	// sideLengthI
					delta1,	// delta
					showShiftyCloakInterfaces1,	// showInterfaces
					0.96,	// interfaceTransmissionCoefficient
					showFrames1,	// showFrames
					frameRadius,	// frameRadius
					frameSurfaceProperty1,	// frameSurfaceProperty		
					scene, studio
					);

			cloak2 = new EditableTetrahedralShiftyCloak(
					"second shifty cloak",	// description
					cloakCenter2,
					edge1,	// new Vector3D(1, 0, 0),	// u
					edge2,	// new Vector3D(0, 1, 0),	// v
					edge3.getReverse(),	// new Vector3D(0, 0, -1),	// w; invert to ensure outside of inner shifty cloak has the same orientation as inside of outer shifty cloak
					false,	// asymmetricConfiguration
					outsideCubeSideLength2,	// sideLength
					insideCubeSideLength2,	// sideLengthI
					delta2,	// delta
					showShiftyCloakInterfaces2,	// showInterfaces
					0.96,	// interfaceTransmissionCoefficient
					showFrames2,	// showFrames
					frameRadius,	// frameRadius
					frameSurfaceProperty2,	// frameSurfaceProperty		
					scene, studio
					);
			break;
		case TETRAHEDRAL:
			cloak1 = new EditableSimpleTetrahedralShiftyCloak(
					"first shifty cloak",	// description
					cloakCenter1,
					edge1,	// new Vector3D( 1/Math.sqrt(2.), 1/Math.sqrt(3.),-1/Math.sqrt(6.)),	// new Vector3D(1, 0, 0),	// u
					edge2,	// new Vector3D(-1/Math.sqrt(2.), 1/Math.sqrt(3.),-1/Math.sqrt(6.)),	// new Vector3D(0, 1, 0),	// v
					outsideCubeSideLength1,	// sideLength
					insideCubeSideLength1,	// sideLengthI
					delta1,	// delta
					showShiftyCloakInterfaces1,	// showInterfaces
					0.96,	// interfaceTransmissionCoefficient
					showFrames1,	// showFrames
					frameRadius,	// frameRadius
					frameSurfaceProperty1,	// frameSurfaceProperty		
					scene, studio
					);

			cloak2 = new EditableSimpleTetrahedralShiftyCloak(
					"second shifty cloak",	// description
					cloakCenter2,
					edge1,	// new Vector3D(1, 0, 0),	// u
					edge2,	// new Vector3D(0, 1, 0),	// v
					outsideCubeSideLength2,	// sideLength
					insideCubeSideLength2,	// sideLengthI
					delta2,	// delta
					showShiftyCloakInterfaces2,	// showInterfaces
					0.96,	// interfaceTransmissionCoefficient
					showFrames2,	// showFrames
					frameRadius,	// frameRadius
					frameSurfaceProperty2,	// frameSurfaceProperty		
					scene, studio
					);
			break;
		case CUBIC:
		default:
			cloak1 = new EditableCubicShiftyCloak(
					"first shifty cloak",	// description
					cloakCenter1,
					new Vector3D(1, 0, 0),	// uDirection
					new Vector3D(0, 1, 0),	// vDirection
					outsideCubeSideLength1,	// sideLengthOutside
					insideCubeSideLength1,	// sideLengthInside
					delta1,	// delta
					showShiftyCloakInterfaces1,	// showInterfaces
					0.96,	// interfaceTransmissionCoefficient
					showFrames1,	// showFrames
					frameRadius,	// frameRadius
					frameSurfaceProperty1,	// frameSurfaceProperty		
					scene, studio
					);

			cloak2 = new EditableCubicShiftyCloak(
					"second shifty cloak",	// description
					cloakCenter2,
					new Vector3D(1, 0, 0),	// uDirection
					new Vector3D(0, 1, 0),	// vDirection
					outsideCubeSideLength2,	// sideLength
					insideCubeSideLength2,	// sideLengthI
					delta2,	// delta
					showShiftyCloakInterfaces2,	// showInterfaces
					0.96,	// interfaceTransmissionCoefficient
					showFrames2,	// showFrames
					frameRadius,	// frameRadius
					frameSurfaceProperty2,	// frameSurfaceProperty		
					scene, studio
					);
		}

		scene.addSceneObject(cloak1, showShiftyCloak1);
		scene.addSceneObject(cloak2, showShiftyCloak2);

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
	private LabelledVector3DPanel delta1Panel;
	private LabelledDoublePanel outsideCubeSideLengthOPanel, insideCubeSideLengthOPanel;

	// inner shifty device
	private JCheckBox showShiftyCloakICheckBox, showShiftyCloakInterfacesICheckBox, showFramesICheckBox, showShiftyCloak2ImageCheckBox;
	private LabelledVector3DPanel delta2Panel;
	private LabelledDoublePanel outsideCubeSideLengthIPanel, insideCubeSideLengthIPanel;

	// trajectory
	private JCheckBox showTrajectory1CheckBox, showTrajectory2CheckBox, showTrajectory3CheckBox, reportToConsoleCheckBox;
	private LabelledVector3DPanel trajectory1StartPointPanel, trajectory2StartPointPanel, trajectory3StartPointPanel;
	private LabelledVector3DPanel trajectory1StartDirectionPanel, trajectory2StartDirectionPanel, trajectory3StartDirectionPanel;
	private LabelledDoublePanel trajectoryRadiusPanel;
	private LabelledIntPanel trajectoryMaxTraceLevelPanel;

	// rest of scene
	private JComboBox<StudioInitialisationType> studioInitialisationComboBox;
	private LabelledDoublePanel timDistancePanel, timRadiusPanel;
	private JComboBox<JanusType> janusTypeComboBox;
	private LabelledDoublePanel[] sphereRadiusPanels;
	private LabelledDoubleColourPanel[] sphereColourPanels, sphereImageColourPanels;
	private JCheckBox[] sphereVisibilityCheckBoxes, sphereImageVisibilityCheckBoxes;
	private LabelledDoublePanel f1Panel, f2Panel, lensTransmissionPanel;

	private JTabbedPane objectSelectionPanel;
	



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

		centrePanel = new LabelledVector3DPanel("Janus/wormhole centre");
		centrePanel.setVector3D(centre);
		shiftyCloaksPanel.add(centrePanel, "wrap");

		// the outer shifty cloak
		JPanel outerShiftyCloakPanel = new JPanel();
		outerShiftyCloakPanel.setLayout(new MigLayout("insets 0"));
		outerShiftyCloakPanel.setBorder(GUIBitsAndBobs.getTitledBorder("first shifty cloak"));

		showShiftyCloakOCheckBox = new JCheckBox("Show");
		showShiftyCloakOCheckBox.setSelected(showShiftyCloak1);
		outerShiftyCloakPanel.add(showShiftyCloakOCheckBox, "wrap");

		showShiftyCloakInterfacesOCheckBox = new JCheckBox("Show interfaces");
		showShiftyCloakInterfacesOCheckBox.setSelected(showShiftyCloakInterfaces1);
		outerShiftyCloakPanel.add(showShiftyCloakInterfacesOCheckBox, "wrap");

		delta1Panel = new LabelledVector3DPanel("Apparent shift of first cloak");
		delta1Panel.setVector3D(delta1);
		outerShiftyCloakPanel.add(delta1Panel, "wrap");

		outsideCubeSideLengthOPanel = new LabelledDoublePanel("Side length of (cubic) outside");
		outsideCubeSideLengthOPanel.setNumber(outsideCubeSideLength1);
		outerShiftyCloakPanel.add(outsideCubeSideLengthOPanel, "wrap");

		insideCubeSideLengthOPanel = new LabelledDoublePanel("Side length of inner cube");
		insideCubeSideLengthOPanel.setNumber(insideCubeSideLength1);
		outerShiftyCloakPanel.add(insideCubeSideLengthOPanel, "wrap");

		showFramesOCheckBox = new JCheckBox("Show frames");
		showFramesOCheckBox.setSelected(showFrames1);
		outerShiftyCloakPanel.add(showFramesOCheckBox, "wrap");

		shiftyCloaksPanel.add(outerShiftyCloakPanel, "wrap");


		// the inner shifty cloak
		JPanel innerShiftyCloakPanel = new JPanel();
		innerShiftyCloakPanel.setLayout(new MigLayout("insets 0"));
		innerShiftyCloakPanel.setBorder(GUIBitsAndBobs.getTitledBorder("second shifty cloak"));

		showShiftyCloakICheckBox = new JCheckBox("Show");
		showShiftyCloakICheckBox.setSelected(showShiftyCloak2);
		innerShiftyCloakPanel.add(showShiftyCloakICheckBox, "wrap");

		showShiftyCloakInterfacesICheckBox = new JCheckBox("Show interfaces");
		showShiftyCloakInterfacesICheckBox.setSelected(showShiftyCloakInterfaces2);
		innerShiftyCloakPanel.add(showShiftyCloakInterfacesICheckBox, "wrap");

		delta2Panel = new LabelledVector3DPanel("Apparent shift of second cloak");
		delta2Panel.setVector3D(delta2);
		innerShiftyCloakPanel.add(delta2Panel, "wrap");

		outsideCubeSideLengthIPanel = new LabelledDoublePanel("Side length of (cubic) outside");
		outsideCubeSideLengthIPanel.setNumber(outsideCubeSideLength2);
		innerShiftyCloakPanel.add(outsideCubeSideLengthIPanel, "wrap");

		insideCubeSideLengthIPanel = new LabelledDoublePanel("Side length of inner cube");
		insideCubeSideLengthIPanel.setNumber(insideCubeSideLength2);
		innerShiftyCloakPanel.add(insideCubeSideLengthIPanel, "wrap");

		showFramesICheckBox = new JCheckBox("Show frames");
		showFramesICheckBox.setSelected(showFrames2);
		innerShiftyCloakPanel.add(showFramesICheckBox, "wrap");


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
		
		objectSelectionPanel = new JTabbedPane();
		//objectSelectionPanel.setLayout(new MigLayout("insets 0"));
		//restOfScenePanel.add(spheresPanel, "span");
		restOfScenePanel.add(objectSelectionPanel, "span");

		JPanel spheresPanel = new JPanel();
		spheresPanel.setLayout(new MigLayout("insets 0"));

		JPanel lensPanel = new JPanel();
		lensPanel.setLayout(new MigLayout("insets 0"));
		lensPanel.setBorder(GUIBitsAndBobs.getTitledBorder("wormhole lenses")); //TODO something below does not work with the for loop and tabbed panes...

		objectSelectionPanel.addTab("Janus device",spheresPanel);
		objectSelectionPanel.addTab("Wormhole",lensPanel);

		janusTypeComboBox = new JComboBox<JanusType>(JanusType.values());
		janusTypeComboBox.setSelectedItem(janusType);
		spheresPanel.add(GUIBitsAndBobs.makeRow("Janus type", janusTypeComboBox), "span");

		JPanel spherePanels[] = new JPanel[2];
		sphereRadiusPanels = new LabelledDoublePanel[2];
		sphereColourPanels = new LabelledDoubleColourPanel[2];
		sphereVisibilityCheckBoxes = new JCheckBox[2];
		sphereImageColourPanels = new LabelledDoubleColourPanel[2];
		sphereImageVisibilityCheckBoxes = new JCheckBox[2];
		for(int i=0; i<2; i++)
		{
			spherePanels[i] = new JPanel();
			spherePanels[i].setLayout(new MigLayout("insets 0"));
			spherePanels[i].setBorder(GUIBitsAndBobs.getTitledBorder("Sphere #"+(i+1)));

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

			spheresPanel.add(spherePanels[i], "wrap");
		}	
		
		f1Panel = new LabelledDoublePanel("f1");
		f1Panel.setNumber(f1);
		lensPanel.add(f1Panel, "wrap");

		f2Panel = new LabelledDoublePanel("f2");
		f2Panel.setNumber(f2);
		lensPanel.add(f2Panel, "wrap");

		lensTransmissionPanel = new LabelledDoublePanel("lens transmission coef");
		lensTransmissionPanel.setNumber(lensTransmission);
		lensPanel.add(lensTransmissionPanel, "wrap");

		showShiftyCloak2ImageCheckBox = new JCheckBox("show inner cloak image");
		showShiftyCloak2ImageCheckBox.setSelected(showShiftyCloak2Image);
		lensPanel.add(showShiftyCloak2ImageCheckBox, "span");

		timDistancePanel = new LabelledDoublePanel("tim distance");
		timDistancePanel.setNumber(timDistance);
		lensPanel.add(timDistancePanel);
		
		timRadiusPanel = new LabelledDoublePanel("& tim radius");
		timRadiusPanel.setNumber(timRadius);
		lensPanel.add(timRadiusPanel,"span");
		
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
		showShiftyCloak1 = showShiftyCloakOCheckBox.isSelected();
		showShiftyCloakInterfaces1 = showShiftyCloakInterfacesOCheckBox.isSelected();
		delta1 = delta1Panel.getVector3D();
		outsideCubeSideLength1 = outsideCubeSideLengthOPanel.getNumber();
		insideCubeSideLength1 = insideCubeSideLengthOPanel.getNumber();
		showFrames1 = showFramesOCheckBox.isSelected();
		showShiftyCloak2 = showShiftyCloakICheckBox.isSelected();
		showShiftyCloakInterfaces2 = showShiftyCloakInterfacesICheckBox.isSelected();
		delta2 = delta2Panel.getVector3D();
		outsideCubeSideLength2 = outsideCubeSideLengthIPanel.getNumber();
		insideCubeSideLength2 = insideCubeSideLengthIPanel.getNumber();
		showFrames2 = showFramesICheckBox.isSelected();

		//more objects	
		janusType = (JanusType)(janusTypeComboBox.getSelectedItem());
		f1 = f1Panel.getNumber();
		f2 = f2Panel.getNumber();
		lensTransmission = lensTransmissionPanel.getNumber();
		showShiftyCloak2Image = showShiftyCloak2ImageCheckBox.isSelected();

		switch(objectSelectionPanel.getSelectedIndex())
		{
		case 0:
			objectType = ObjectType.JANUS;
			break;
		case 1:
			objectType = ObjectType.WORMHOLE;
			break;
		}

		//trajectories

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
		timDistance = timDistancePanel.getNumber();
		timRadius = timRadiusPanel.getNumber();
		
		for(int i=0; i<2; i++)
		{
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
		(new PolyhedralShiftyCloakJanusVisualiser()).run();
	}
}
