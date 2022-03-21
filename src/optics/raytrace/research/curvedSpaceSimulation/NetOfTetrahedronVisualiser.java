package optics.raytrace.research.curvedSpaceSimulation;

import java.awt.event.ActionEvent;
import java.io.PrintStream;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import math.*;
import net.miginfocom.swing.MigLayout;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.surfaces.ColourFilter;
import optics.raytrace.surfaces.Reflective;
import optics.raytrace.surfaces.SurfaceColour;
import optics.raytrace.surfaces.SurfaceColourLightSourceIndependent;
import optics.raytrace.exceptions.SceneException;
import optics.DoubleColour;
import optics.raytrace.NonInteractiveTIMActionEnum;
import optics.raytrace.NonInteractiveTIMEngine;
import optics.raytrace.GUI.cameras.RenderQualityEnum;
import optics.raytrace.GUI.lowLevel.ApertureSizeType;
import optics.raytrace.GUI.lowLevel.DoublePanel;
import optics.raytrace.GUI.lowLevel.GUIBitsAndBobs;
import optics.raytrace.GUI.lowLevel.LabelledDoublePanel;
import optics.raytrace.GUI.lowLevel.LabelledIntPanel;
import optics.raytrace.GUI.lowLevel.LabelledVector3DPanel;
import optics.raytrace.GUI.sceneObjects.EditableSpaceCancellingWedge;
import optics.raytrace.GUI.sceneObjects.EditableArrow;
import optics.raytrace.GUI.sceneObjects.EditableParametrisedCylinder;
import optics.raytrace.GUI.sceneObjects.EditableRayTrajectory;
import optics.raytrace.GUI.sceneObjects.EditableScaledParametrisedCentredParallelogram;
import optics.raytrace.GUI.sceneObjects.EditableScaledParametrisedSphere;
import optics.raytrace.cameras.OrthographicCamera;
import optics.raytrace.core.Studio;
import optics.raytrace.core.StudioInitialisationType;
import optics.raytrace.core.SurfaceProperty;


/**
 * Plots a number of trajectories through the net of a 3-simplex (i.e. tetrahedron).
 * 
 * @author Johannes Courtial, Dimitris Georgantzis, Giovanni Natale
 */
public class NetOfTetrahedronVisualiser extends NonInteractiveTIMEngine
{
	/**
	 * centre of the central triangle
	 */
	protected Vector3D centre;
	
	/**
	 * rightwards vector (parallel to side 1)
	 */
	protected Vector3D rightDirection;
	
	/**
	 * upwards vector (in direction of vertex opposite side 1)
	 */
	protected Vector3D upDirection;
	
	/**
	 * side length of the tetrahedron
	 */
	protected double sideLength;
	
	/**
	 * show the null-space wedges
	 */
	protected boolean showNullSpaceWedges;
	
	/**
	 * Height of the null-space wedges
	 */
	protected double nullSpaceWedgeHeight;

	/**
	 * Factor by which the minimum leg length of each null-space wedge is multiplied.
	 */
	protected double nullSpaceWedgeLegLengthFactor;
	
	/**
	 * Transmission coefficient of each null-space-wedge surface
	 */
	protected double nullSpaceWedgeSurfaceTransmissionCoefficient;
		
	/**
	 * Curved-space-simulation type, which describes the way the simulation is performed
	 */
	protected GluingType curvedSpaceSimulationType;
	
	/**
	 * Number of negative-space wedges, <i>N</i>, in the null-space wedge
	 */
	protected int numberOfNegativeSpaceWedges;
	
	/**
	 * distance between principal points of lenses 1 and 2 in each lens-based space-cancelling wedge
	 */
	protected double distanceP1P2;

	/**
	 * show edges of the simplicial complex that forms the net of the 4-simplex
	 */
	protected boolean showNetEdges;
	
	/**
	 * show edges of the null-space wedges, i.e. the vertices and edges of the sheets forming the null-space wedges
	 */
	protected boolean showNullSpaceWedgeEdges;

	/**
	 * surface property of the spheres and cylinders representing vertices and edges of the net
	 */
	protected SurfaceProperty netEdgeSurfaceProperty;
	
	/**
	 * surface property of the faces of the net
	 */
	// protected SurfaceProperty netFaceSurfaceProperty;
	
	/**
	 * surface property of the spheres and cylinders representing vertices and edges of the net
	 */
	protected SurfaceProperty nullSpaceWedgeEdgeSurfaceProperty;

	/**
	 * radius of the spheres and cylinders representing the vertices and edges
	 */
	protected double edgeRadius;
	
	/**
	 * show trajectory
	 */
	private boolean showTrajectory1, showTrajectory2, showTrajectory3;
	
	/**
	 * start point of the light-ray trajectory
	 */
	private Vector3D trajectory1StartPoint, trajectory2StartPoint, trajectory3StartPoint;
	
	/**
	 * initial direction of the light-ray trajectory
	 */
	private Vector3D trajectory1StartDirection, trajectory2StartDirection, trajectory3StartDirection;
	
	/**
	 * radius of the trajectory
	 */
	private double trajectoryRadius;
	
	/**
	 * max trace level for trajectory tracing
	 */
	private int trajectoryMaxTraceLevel;
	
	/**
	 * Determines how to initialise the backdrop
	 */
	private StudioInitialisationType studioInitialisation;
	
	/**
	 * show the sphere?
	 */
	private boolean showSphere;
	
	/**
	 * centre of red sphere
	 */
	private Vector3D sphereCentre;

	/**
	 * radius of sphere
	 */
	private double sphereRadius;

	/**
	 * place, in the centre of each face, a "projected" xyz coordinate system centred in the cube
	 */
	private boolean showProjectedCoordinateSystems;
	private double projectedCoordinateSystemsSize;
	
	private boolean showCoordinateSystemForFace[] = new boolean[4];
	
	public enum CameraType
	{
		STANDARD("Standard"),
		ORTHOGRAPHIC("Orthographic");
		
		private String description;

		private CameraType(String description)
		{
			this.description = description;
		}
		
		@Override
		public String toString() {return description;}
	}
	
	/**
	 * either STANDARD or ORTHOGRAPHIC
	 */
	private CameraType cameraType;
	
	/**
	 * width of the FOV of the orthographic camera
	 */
	private double orthographicCameraWidth;
	

	/**
	 * Constructor.
	 * Sets all parameters.
	 */
	public NetOfTetrahedronVisualiser()
	{
		super();
		
		// set to true for interactive version
		nonInteractiveTIMAction = NonInteractiveTIMActionEnum.INTERACTIVE;

		// set all parameters
		
		centre = new Vector3D(0, 0, 0);
		rightDirection = new Vector3D(0, 0, 1);
		upDirection = new Vector3D(0, 1, 0);
		sideLength = 1;
		nullSpaceWedgeHeight = 1;
		nullSpaceWedgeLegLengthFactor = 1.0;
		nullSpaceWedgeSurfaceTransmissionCoefficient = 0.96;
		curvedSpaceSimulationType = GluingType.PERFECT; // SPACE_CANCELLING_WEDGES_WITH_CONTAINMENT_MIRRORS;
		numberOfNegativeSpaceWedges = 2;
		distanceP1P2 = 0.01;
		netEdgeSurfaceProperty = // SurfaceColour.BLUE_SHINY;
			ColourFilter.CYAN_GLASS;
		nullSpaceWedgeEdgeSurfaceProperty = SurfaceColour.BLACK_SHINY;
		edgeRadius = 0.0025;
		
		showNullSpaceWedges = true;
		showNetEdges = true;
		showNullSpaceWedgeEdges = false;
		
		// stuff
		studioInitialisation = StudioInitialisationType.HEAVEN;	// the backdrop
		showSphere = false;
		sphereCentre = new Vector3D(0.2, 0.1, 0.25);
		sphereRadius = 0.1;
		showProjectedCoordinateSystems = true;
		projectedCoordinateSystemsSize = 0.4*sideLength;
		for(int i=0; i<4; i++) showCoordinateSystemForFace[i] = true;

		// trajectory
		showTrajectory1 = false;
		showTrajectory2 = false;
		showTrajectory3 = false;
		trajectory1StartPoint = sphereCentre;
		trajectory2StartPoint = sphereCentre;
		trajectory3StartPoint = sphereCentre;
		trajectory1StartDirection = new Vector3D(4, 0, 10.*Math.sqrt(3.));	// new Vector3D(Math.cos(0.1+2.*Math.PI*0./3.), 0, Math.sin(0.1+2.*Math.PI*0./3.));
		trajectory2StartDirection = new Vector3D(0.5*Math.sqrt(3.), 0, -1);	// new Vector3D(Math.cos(0.1+2.*Math.PI*1./3.), 0, Math.sin(0.1+2.*Math.PI*1./3.));
		trajectory3StartDirection = new Vector3D(-3./2., 0, 0.1*Math.sqrt(3.));	// new Vector3D(Math.cos(0.1+2.*Math.PI*2./3.), 0, Math.sin(0.1+2.*Math.PI*2./3.));
		trajectoryRadius = 0.005;
		trajectoryMaxTraceLevel = 500;
		
		// camera
		cameraType = CameraType.ORTHOGRAPHIC;
		cameraViewDirection = new Vector3D(0, -1, 0);
		cameraViewCentre = new Vector3D(0, 0, 0);
		cameraDistance = 10;
		cameraFocussingDistance = 10;
		cameraHorizontalFOVDeg = 20;
		cameraMaxTraceLevel = 1000;
		cameraPixelsX = 640;
		cameraPixelsY = 480;
		cameraApertureSize = ApertureSizeType.PINHOLE;
		orthographicCameraWidth = 3;

		traceRaysWithTrajectory = false;	// don't automatically trace rays with trajectory
		
		renderQuality = RenderQualityEnum.DRAFT;
		
		// boring parameters
		nonInteractiveTIMAction = NonInteractiveTIMActionEnum.INTERACTIVE;

		// for movie version
		numberOfFrames = 50;
		firstFrame = 0;
		lastFrame = numberOfFrames-1;
		
		// for interactive version
		windowTitle = "Dr TIM's net-of-tetrahedron visualiser";
		windowWidth = 1300;
		windowHeight = 650;
	}

	@Override
	public String getClassName()
	{
		return "NetOfTetrahedronVisualiser"	// the name
				;
	}
	
	@Override
	public void writeParameters(PrintStream printStream)
	{
		// write any parameters not defined in NonInteractiveTIMEngine
		printStream.println("Scene initialisation");
		printStream.println();
		
		printStream.println("centre = "+centre);
		printStream.println("rightDirection = "+rightDirection);
		printStream.println("upDirection = "+upDirection);
		printStream.println("sideLength = "+sideLength);
		printStream.println("showNullSpaceWedges = "+showNullSpaceWedges);
		printStream.println("nullSpaceWedgeHeight = "+nullSpaceWedgeHeight);
		printStream.println("nullSpaceWedgeLegLengthFactor = "+nullSpaceWedgeLegLengthFactor);
		printStream.println("nullSpaceWedgeSurfaceTransmissionCoefficient = "+nullSpaceWedgeSurfaceTransmissionCoefficient);
		printStream.println("curvedSpaceSimulationType = "+curvedSpaceSimulationType);
		printStream.println("numberOfNegativeSpaceWedges = "+numberOfNegativeSpaceWedges);
		printStream.println("distanceP1P2 = "+distanceP1P2);
		printStream.println("showNetEdges = "+showNetEdges);
		printStream.println("showNullSpaceWedgeEdges = "+showNullSpaceWedgeEdges);
		printStream.println("netEdgeSurfaceProperty = "+netEdgeSurfaceProperty);
		printStream.println("nullSpaceWedgeEdgeSurfaceProperty = "+nullSpaceWedgeEdgeSurfaceProperty);
		printStream.println("edgeRadius = "+edgeRadius);
		
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

		printStream.println("showSphere = "+showSphere);
		if(showSphere)
		{		
			printStream.println("sphereCentre = "+sphereCentre);
			printStream.println("sphereRadius = "+sphereRadius);
		}
		
		printStream.println("showProjectedCoordinateSystems = "+showProjectedCoordinateSystems);
		printStream.println("projectedCoordinateSystemsSize = "+projectedCoordinateSystemsSize);
		for(int i=0; i<4; i++)
			printStream.println("showCoordinateSystemForFace["+i+"] = "+showCoordinateSystemForFace[i]);
		
		printStream.println("studioInitialisation = "+studioInitialisation);

		printStream.println();

		printStream.println("cameraType = "+cameraType);
		printStream.println("orthographicCameraWidth = "+orthographicCameraWidth);

		// write all parameters defined in NonInteractiveTIMEngine
		super.writeParameters(printStream);		
	}
	
	
	/* (non-Javadoc)
	 * @see optics.raytrace.NonInteractiveTIMEngine#populateStudio()
	 */
	@Override
	public void populateStudio()
	throws SceneException
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

		double faceCircumRadius = sideLength / Math.sqrt(3.0);	// circumradius of an equilateral triangle of side length s is s/sqrt(3)
		
		switch(cameraType)
		{
		case ORTHOGRAPHIC:
			Vector3D horizontalSpanVector = new Vector3D(0, 0, 1);
			if(cameraViewDirection.getPartPerpendicularTo(horizontalSpanVector).getLength() == 0) horizontalSpanVector = new Vector3D(1, 0, 0);
			studio.setCamera(new OrthographicCamera(
					"Orthographic camera",	// name
					cameraViewDirection,	// viewDirection
					getStandardCameraPosition(),	// CCDCentre
					horizontalSpanVector.getWithLength(orthographicCameraWidth),	// horizontalSpanVector3D
					Vector3D.crossProduct(cameraViewDirection , horizontalSpanVector).getWithLength(orthographicCameraWidth*cameraPixelsY/cameraPixelsX),	// verticalSpanVector3D
					(int)(cameraPixelsX*renderQuality.getAntiAliasingQuality().getAntiAliasingFactor()),	// detectorPixelsHorizontal
					(int)(cameraPixelsY*renderQuality.getAntiAliasingQuality().getAntiAliasingFactor()),	// detectorPixelsVertical
					cameraMaxTraceLevel	// maxTraceLevel
				));
			break;
		case STANDARD:
		default:
			studio.setCamera(getStandardCamera());
		}


		// add the null-space wedges that define the net
		
		// first, create a (u,v,w) coordinate system such that the tetrahedron is in the (u,v) plane and u is the "right" direction and v is "up"
		Vector3D w = upDirection.getNormalised();
		Vector3D u = rightDirection.getPartPerpendicularTo(w).getNormalised();
		Vector3D v = Vector3D.crossProduct(w, u);	// should be normalised
		
		// the scene
		SceneObjectContainer net = new SceneObjectContainer("net of a tetrahedron", null, studio);
		scene.addSceneObject(net);
		
		switch(curvedSpaceSimulationType)
		{
		case MIRROR_APPROXIMATION:
			// the unfolded net lies in the (u, v) plane such that the "central" face, C, is centred at the origin and one of the vertices is in the u direction
			// (see the crap diagram below, and Johannes's lab book p.19 8/11/18)
			//      D
			//    DDD   ^ v
			//   ACDD   |
			// AAACCC    --> u  
			//   ACBB
			//    BBB
			//      B
			// add the mirrors
			// Mirror 1 (MMM):
			//      D
			// MMMMMM   ^ v
			//   ACDD   |
			// AAACCC    --> u  
			//   ACBB
			//    BBB
			//      B
			net.addSceneObject(new EditableScaledParametrisedCentredParallelogram(
					"mirror in +v direction",	// description
					Vector3D.sum(centre, u.getProductWith(-faceCircumRadius/2), v.getProductWith(faceCircumRadius*Math.sqrt(3.)/2.)),	// centre 
					u.getWithLength(3.*faceCircumRadius),	// spanVector1
					w.getProductWith(nullSpaceWedgeHeight),	// spanVector2
					new Reflective(nullSpaceWedgeSurfaceTransmissionCoefficient, true),	// surfaceProperty
					net,	// parent
					studio)
				);
			// Mirror 2 (MMM):
			//      D
			//    DDD   ^ v
			//   ACDD   |
			// AAACCC    --> u  
			//   ACBB
			// MMMMMM
			//      B
			net.addSceneObject(new EditableScaledParametrisedCentredParallelogram(
					"mirror in -v direction",	// description
					Vector3D.sum(centre, u.getProductWith(-faceCircumRadius/2), v.getProductWith(-faceCircumRadius*Math.sqrt(3.)/2.)),	// centre 
					u.getWithLength(3.*faceCircumRadius),	// spanVector1
					w.getProductWith(nullSpaceWedgeHeight),	// spanVector2
					new Reflective(nullSpaceWedgeSurfaceTransmissionCoefficient, true),	// surfaceProperty
					net,	// parent
					studio)
				);
			// Mirror 3 (MMM):
			//      D
			//    DDD   ^ v
			//M  ACDD   |
			//MAAACCC    --> u  
			//M  ACBB
			//    BBB
			//      B
			net.addSceneObject(new EditableScaledParametrisedCentredParallelogram(
					"mirror in -u direction",	// description
					Vector3D.sum(centre, u.getProductWith(-2.*faceCircumRadius)),	// centre 
					v.getWithLength(Math.sqrt(3.)*faceCircumRadius),	// spanVector1
					w.getProductWith(nullSpaceWedgeHeight),	// spanVector2
					new Reflective(nullSpaceWedgeSurfaceTransmissionCoefficient, true),	// surfaceProperty
					net,	// parent
					studio)
				);
			// Mirror 4 (MMM):
			//      D
			//    DDD   ^ v
			//   ACDDM  |
			// AAACCCM   --> u  
			//   ACBBM
			//    BBB
			//      B
			net.addSceneObject(new EditableScaledParametrisedCentredParallelogram(
					"mirror in +u direction",	// description
					Vector3D.sum(centre, u.getProductWith(faceCircumRadius)),	// centre 
					v.getWithLength(Math.sqrt(3.)*faceCircumRadius),	// spanVector1
					w.getProductWith(nullSpaceWedgeHeight),	// spanVector2
					new Reflective(nullSpaceWedgeSurfaceTransmissionCoefficient, true),	// surfaceProperty
					net,	// parent
					studio)
				);
			break;
		default:
			// calculate the centres of the three null-space wedges
			for(int i=0; i<3; i++)
			{
				double phi = i*2.0*Math.PI/3.0;
				double cos = Math.cos(phi);
				double sin = Math.sin(phi);
				
				Vector3D radialDirection = Vector3D.sum(u.getProductWith(cos), v.getProductWith(sin));
				
				net.addSceneObject(new EditableSpaceCancellingWedge(
						"Null-space wedge #"+i,	// description
						Math.PI,	// apexAngle = 180 degrees
						Vector3D.sum(centre, radialDirection.getProductWith(faceCircumRadius)),	// apexEdgeCentre
						w,	// apexEdgeDirection
						radialDirection,	// bisectorDirection
						faceCircumRadius*Math.sqrt(3.0)*nullSpaceWedgeLegLengthFactor,	// legLength, here the side length of an equilateral triangle with the given circumradius
						nullSpaceWedgeHeight,	// apexEdgeLength,
						// SCWedgeLegFaceShape.RECTANGULAR,
						true,	// showRefractingSurfaces,
						MyMath.deg2rad(100),	// containmentMirrorsAngleWithSides
						nullSpaceWedgeSurfaceTransmissionCoefficient,	// surfaceTransmissionCoefficient,
						false,	// showEdges,
						edgeRadius,	// edgeRadius,
						netEdgeSurfaceProperty,	// edgeSurfaceProperty,
						curvedSpaceSimulationType,	// nullSpaceWedgeType
						numberOfNegativeSpaceWedges,
						distanceP1P2,
						scene,	// parent, 
						studio
				));
			}
			break;

		}
		
		
		// stuff
		
		scene.addSceneObject(
				new EditableScaledParametrisedSphere(
						"White sphere",	// description
						sphereCentre,	// centre
						sphereRadius,	// radius
						SurfaceColour.WHITE_SHINY,	// surfaceProperty
						scene,	// parent
						studio
						),
				showSphere
			);
		
		// add coordinate systems...
		double normalsAngle = Math.PI - Math.acos(1/3.);
		Vector3D faceNormal[] = new Vector3D[4];
		faceNormal[0] = new Vector3D(0, projectedCoordinateSystemsSize, 0);
		for(int i=1; i<4; i++)
		{
			double phi = (i+1)*2.0*Math.PI/3.0-0.5*Math.PI;
			double cos = Math.cos(phi);
			double sin = Math.sin(phi);
			
			faceNormal[i] = Matrix3D.rotateVector(
					faceNormal[0],
					normalsAngle,	// rotationAngle
					Vector3D.sum(u.getProductWith(cos), v.getProductWith(sin))
					);
		}
		
		// ... to the central face...
		scene.addSceneObject(
				createCoordinateSystem(
						0,	// faceNumber
						centre,	// origin
						null,	// rotationAxis
						faceNormal,
						scene,
						studio
					),
				showProjectedCoordinateSystems && showCoordinateSystemForFace[0]
			);

		// ... and to the other faces
		double faceInRadius = faceCircumRadius * Math.sin(MyMath.deg2rad(30));
		for(int i=1; i<4; i++)
		{
			double phi = (i+1)*2.0*Math.PI/3.0 + Math.PI;
			double cos = Math.cos(phi);
			double sin = Math.sin(phi);
			
			Vector3D radialDirection = Vector3D.sum(u.getProductWith(cos), v.getProductWith(sin));
			Vector3D origin = Vector3D.sum(centre, radialDirection.getProductWith(2*faceInRadius));
			Vector3D rotationAxis = Vector3D.crossProduct(origin, upDirection).getNormalised();
			
			scene.addSceneObject(
					createCoordinateSystem(
							i,	// faceNumber
							origin,	// origin
							rotationAxis,	// rotationAxis
							faceNormal,
							scene,
							studio
						),
					showProjectedCoordinateSystems && showCoordinateSystemForFace[i]
				);
		}

		
		// the trajectories
		
		scene.addSceneObject(new EditableRayTrajectory(
				"Trajectory 1",
				trajectory1StartPoint,	// start point
				0,	// start time
				trajectory1StartDirection,	// initial direction
				trajectoryRadius,	// radius
				new SurfaceColourLightSourceIndependent(DoubleColour.RED, true),
				trajectoryMaxTraceLevel,	// max trace level
				false,	// reportToConsole
				scene,
				studio
			),
			showTrajectory1
		);

		scene.addSceneObject(new EditableRayTrajectory(
				"Trajectory 2",
				trajectory2StartPoint,	// start point
				0,	// start time
				trajectory2StartDirection,	// initial direction
				trajectoryRadius,	// radius
				new SurfaceColourLightSourceIndependent(DoubleColour.GREEN, true),
				trajectoryMaxTraceLevel,	// max trace level
				false,	// reportToConsole
				scene,
				studio
			),
			showTrajectory2
		);

		scene.addSceneObject(new EditableRayTrajectory(
				"Trajectory 3",
				trajectory3StartPoint,	// start point
				0,	// start time
				trajectory3StartDirection,	// initial direction
				trajectoryRadius,	// radius
				new SurfaceColourLightSourceIndependent(DoubleColour.BLUE, true),
				trajectoryMaxTraceLevel,	// max trace level
				false,	// reportToConsole
				scene,
				studio
			),
			showTrajectory3
		);


		// trace the rays with trajectory through the scene
		studio.traceRaysWithTrajectory();

		
		// prepare the net for standard ray tracing...
		
		// remove anything that was previously added to the net...
		net.clear();

		// ... and add what needs to be added now, i.e. everything with the correct visibility
		switch(curvedSpaceSimulationType)
		{
		case MIRROR_APPROXIMATION:
			// the unfolded net lies in the (u, v) plane such that the "central" face, C, is centred at the origin and one of the vertices is in the u direction
			// (see the crap diagram below, and Johannes's lab book p.19 8/11/18)
			//      D
			//    DDD   ^ v
			//   ACDD   |
			// AAACCC    --> u  
			//   ACBB
			//    BBB
			//      B
			// add the mirrors
			// Mirror 1 (MMM):
			//      D
			// MMMMMM   ^ v
			//   ACDD   |
			// AAACCC    --> u  
			//   ACBB
			//    BBB
			//      B
			net.addSceneObject(new EditableScaledParametrisedCentredParallelogram(
					"mirror in +v direction",	// description
					Vector3D.sum(centre, u.getProductWith(-faceCircumRadius/2), v.getProductWith(faceCircumRadius*Math.sqrt(3.)/2.)),	// centre 
					u.getWithLength(3.*faceCircumRadius),	// spanVector1
					w.getProductWith(nullSpaceWedgeHeight),	// spanVector2
					new Reflective(nullSpaceWedgeSurfaceTransmissionCoefficient, true),	// surfaceProperty
					net,	// parent
					studio)
				);
			// Mirror 2 (MMM):
			//      D
			//    DDD   ^ v
			//   ACDD   |
			// AAACCC    --> u  
			//   ACBB
			// MMMMMM
			//      B
			net.addSceneObject(new EditableScaledParametrisedCentredParallelogram(
					"mirror in -v direction",	// description
					Vector3D.sum(centre, u.getProductWith(-faceCircumRadius/2), v.getProductWith(-faceCircumRadius*Math.sqrt(3.)/2.)),	// centre 
					u.getWithLength(3.*faceCircumRadius),	// spanVector1
					w.getProductWith(nullSpaceWedgeHeight),	// spanVector2
					new Reflective(nullSpaceWedgeSurfaceTransmissionCoefficient, true),	// surfaceProperty
					net,	// parent
					studio)
				);
			// Mirror 3 (MMM):
			//      D
			//    DDD   ^ v
			//M  ACDD   |
			//MAAACCC    --> u  
			//M  ACBB
			//    BBB
			//      B
			net.addSceneObject(new EditableScaledParametrisedCentredParallelogram(
					"mirror in -u direction",	// description
					Vector3D.sum(centre, u.getProductWith(-2.*faceCircumRadius)),	// centre 
					v.getWithLength(Math.sqrt(3.)*faceCircumRadius),	// spanVector1
					w.getProductWith(nullSpaceWedgeHeight),	// spanVector2
					new Reflective(nullSpaceWedgeSurfaceTransmissionCoefficient, true),	// surfaceProperty
					net,	// parent
					studio)
				);
			// Mirror 4 (MMM):
			//      D
			//    DDD   ^ v
			//   ACDDM  |
			// AAACCCM   --> u  
			//   ACBBM
			//    BBB
			//      B
			net.addSceneObject(new EditableScaledParametrisedCentredParallelogram(
					"mirror in +u direction",	// description
					Vector3D.sum(centre, u.getProductWith(faceCircumRadius)),	// centre 
					v.getWithLength(Math.sqrt(3.)*faceCircumRadius),	// spanVector1
					w.getProductWith(nullSpaceWedgeHeight),	// spanVector2
					new Reflective(nullSpaceWedgeSurfaceTransmissionCoefficient, true),	// surfaceProperty
					net,	// parent
					studio)
				);
			break;
		default:
			for(int i=0; i<3; i++)
			{
				double phi = i*2.0*Math.PI/3.0;
				double cos = Math.cos(phi);
				double sin = Math.sin(phi);

				Vector3D radialDirection = Vector3D.sum(u.getProductWith(cos), v.getProductWith(sin));

				scene.addSceneObject(new EditableSpaceCancellingWedge(
						"Null-space wedge #"+i,	// description
						Math.PI,	// apexAngle = 180 degrees
						Vector3D.sum(centre, radialDirection.getProductWith(faceCircumRadius)),	// apexEdgeCentre
						w,	// apexEdgeDirection
						radialDirection,	// bisectorDirection
						faceCircumRadius*Math.sqrt(3.0)*nullSpaceWedgeLegLengthFactor,	// legLength, here the side length of an equilateral triangle with the given circumradius
						nullSpaceWedgeHeight,	// apexEdgeLength,
						// SCWedgeLegFaceShape.RECTANGULAR,
						showNullSpaceWedges,	// showRefractingSurfaces,
						MyMath.deg2rad(100),	// containmentMirrorsAngleWithSides
						nullSpaceWedgeSurfaceTransmissionCoefficient,	// surfaceTransmissionCoefficient,
						showNullSpaceWedgeEdges,	// showEdges,
						edgeRadius,	// edgeRadius,
						nullSpaceWedgeEdgeSurfaceProperty,	// edgeSurfaceProperty,
						curvedSpaceSimulationType,	// nullSpaceWedgeType
						numberOfNegativeSpaceWedges,
						distanceP1P2,
						scene,	// parent, 
						studio
						));
			}
		break;
		}
		
		if(showNetEdges)
		{
			for(int i=0; i<3; i++)
			{
				double
				phi1 = i*2.0*Math.PI/3.0,
				phi2 = (i+1)*2.0*Math.PI/3.0,
				phi3 = (i-1)*2.0*Math.PI/3.0,
				cos1 = Math.cos(phi1),
				cos2 = Math.cos(phi2),
				cos3 = Math.cos(phi3),
				sin1 = Math.sin(phi1),
				sin2 = Math.sin(phi2),
				sin3 = Math.sin(phi3);
				
				Vector3D
					v1 = Vector3D.sum(centre, u.getProductWith(cos1*faceCircumRadius), v.getProductWith(sin1*faceCircumRadius)),
					v2 = Vector3D.sum(centre, u.getProductWith(cos2*faceCircumRadius), v.getProductWith(sin2*faceCircumRadius)),
					v3 = Vector3D.sum(centre, u.getProductWith(cos3*faceCircumRadius), v.getProductWith(sin3*faceCircumRadius)),
					v4 = Geometry.reflectPointOnLine(
							v3,	// point 
							v1,	// pointOnLine
							Vector3D.difference(v2, v1)	// normalToPlane
						);
				
				// vertices
				scene.addSceneObject(
						new EditableScaledParametrisedSphere(
								"Vertex #"+i+" of central net triangle",
								v1,	// centre
								edgeRadius,	// radius
								netEdgeSurfaceProperty,
								scene,
								studio
							)
					);
				scene.addSceneObject(
						new EditableScaledParametrisedSphere(
								"Outer vertex of outer net triangle #"+(i+1),
								v4,	// centre
								edgeRadius,	// radius
								netEdgeSurfaceProperty,
								scene,
								studio
							)
					);

				// edges
				scene.addSceneObject(
						new EditableParametrisedCylinder(
								"Edge #"+i+" of central net triangle",
								v1,	// start point
								v2,	// end point
								edgeRadius,	// radius
								netEdgeSurfaceProperty,
								scene,
								studio
							)
					);

				scene.addSceneObject(
						new EditableParametrisedCylinder(
								"Outer edge #1 of outer net triangle #"+(i+1),
								v1,	// start point
								v4,	// end point
								edgeRadius,	// radius
								netEdgeSurfaceProperty,
								scene,
								studio
							)
					);
				scene.addSceneObject(
						new EditableParametrisedCylinder(
								"Outer edge #2 of outer net triangle #"+(i+1),
								v2,	// start point
								v4,	// end point
								edgeRadius,	// radius
								netEdgeSurfaceProperty,
								scene,
								studio
							)
					);
			}

		}
	}
	
	private static SurfaceColour faceNormalColour[] = {
			SurfaceColour.RED_SHINY,
			SurfaceColour.GREEN_SHINY,
			SurfaceColour.BLUE_SHINY,
			SurfaceColour.YELLOW_SHINY
	};
	
	private SceneObjectContainer createCoordinateSystem(int faceNumber, Vector3D origin, Vector3D rotationAxis, Vector3D[] faceNormal, SceneObjectContainer scene, Studio studio)
	{
		// create a container for the arrows that form the coordinate system to go into
		SceneObjectContainer c = new SceneObjectContainer("Coordinate system for face #"+faceNumber, scene, studio);
		
		double shaftRadius = 0.025*projectedCoordinateSystemsSize;
		double tipLength = 0.15*projectedCoordinateSystemsSize;
		double tipAngle = MyMath.deg2rad(30);
		double normalsAngle = Math.PI - Math.acos(1/3.);
		
		for(int i=0; i<4; i++)
		{
			Vector3D currentFaceNormal;
			if(rotationAxis == null) currentFaceNormal = faceNormal[i];
			else currentFaceNormal = Matrix3D.rotateVector(
					faceNormal[i],	// vector
					normalsAngle,	// rotationAngle
					rotationAxis
				);
					
			c.addSceneObject(new EditableArrow(
					"Axis #"+i,
					Vector3D.sum(origin, currentFaceNormal.getProductWith(-0.5)),	// start point
					Vector3D.sum(origin, currentFaceNormal.getProductWith( 0.5)),	// end point
					shaftRadius,
					tipLength,
					tipAngle,
					faceNormalColour[i],	// surfaceProperty
					c,
					studio
			));
		}
				
		return c;
	}

	
	
	//
	// for interactive version
	//
	
	// GUI panels
	private LabelledVector3DPanel centrePanel, rightDirectionPanel, upDirectionPanel;
	private LabelledDoublePanel sideLengthPanel, distanceP1P2Panel, edgeRadiusPanel, nullSpaceWedgeLegLengthFactorPanel, nullSpaceWedgeSurfaceTransmissionCoefficientPanel;
	private LabelledIntPanel numberOfNegativeSpaceWedgesPanel;
	private JComboBox<GluingType> curvedSpaceSimulationTypeComboBox;
	private JCheckBox showNullSpaceWedgesCheckBox, showNetEdgesCheckBox, showNullSpaceWedgesEdgesCheckBox;
	
	// trajectory
	private JCheckBox showTrajectory1CheckBox, showTrajectory2CheckBox, showTrajectory3CheckBox;
	private LabelledVector3DPanel trajectory1StartPointPanel, trajectory2StartPointPanel, trajectory3StartPointPanel;
	private LabelledVector3DPanel trajectory1StartDirectionPanel, trajectory2StartDirectionPanel, trajectory3StartDirectionPanel;
	private LabelledDoublePanel trajectoryRadiusPanel;
	private LabelledIntPanel trajectoryMaxTraceLevelPanel;

	// stuff
	private JComboBox<StudioInitialisationType> studioInitialisationComboBox;
	private JCheckBox showSphereCheckBox;
	private LabelledVector3DPanel sphereCentrePanel;
	private LabelledDoublePanel sphereRadiusPanel;
	private JCheckBox showProjectedCoordinateSystemsCheckBox;
	private JCheckBox showCoordinateSystemForFaceCheckBox[] = new JCheckBox[4];
	private LabelledDoublePanel projectedCoordinateSystemsSizePanel;


	// camera
	private JTabbedPane cameraTypeTabbedPane;
	// private JComboBox<CameraType> cameraTypeComboBox;
	private LabelledVector3DPanel cameraPositionPanel, cameraViewCentrePanel;
	private DoublePanel cameraHorizontalFOVDegPanel;
	private JComboBox<ApertureSizeType> cameraApertureSizeComboBox;
	private LabelledDoublePanel cameraFocussingDistancePanel, orthographicCameraWidthPanel;
	private LabelledIntPanel cameraMaxTraceLevelPanel;

	

	
	/**
	 * add controls to the interactive control panel;
	 * override to modify
	 */
	@Override
	protected void createInteractiveControlPanel()
	{
		super.createInteractiveControlPanel();
		
		JTabbedPane tabbedPane = new JTabbedPane();
		interactiveControlPanel.add(tabbedPane, "span");
		
		

		//
		// the net panel
		//
		
		JPanel netPanel = new JPanel();
		netPanel.setLayout(new MigLayout("insets 0"));
		
		centrePanel = new LabelledVector3DPanel("Centre");
		centrePanel.setVector3D(centre);
		netPanel.add(centrePanel, "wrap");
		
		sideLengthPanel = new LabelledDoublePanel("Side length");
		sideLengthPanel.setNumber(sideLength);
		netPanel.add(sideLengthPanel, "wrap");
		

		rightDirectionPanel = new LabelledVector3DPanel("Rightwards direction");
		rightDirectionPanel.setVector3D(rightDirection);
		netPanel.add(rightDirectionPanel, "wrap");

		upDirectionPanel = new LabelledVector3DPanel("Upwards direction");
		upDirectionPanel.setVector3D(upDirection);
		netPanel.add(upDirectionPanel, "wrap");
		
		showNullSpaceWedgesCheckBox = new JCheckBox("Show null-space wedges");
		showNullSpaceWedgesCheckBox.setSelected(showNullSpaceWedges);
		netPanel.add(showNullSpaceWedgesCheckBox, "wrap");
		
		nullSpaceWedgeLegLengthFactorPanel = new LabelledDoublePanel("Leg length factor");
		nullSpaceWedgeLegLengthFactorPanel.setNumber(nullSpaceWedgeLegLengthFactor);
		netPanel.add(nullSpaceWedgeLegLengthFactorPanel, "wrap");
		
		curvedSpaceSimulationTypeComboBox = new JComboBox<GluingType>(GluingType.values());
		curvedSpaceSimulationTypeComboBox.setSelectedItem(curvedSpaceSimulationType);
		curvedSpaceSimulationTypeComboBox.addActionListener(this);
		netPanel.add(GUIBitsAndBobs.makeRow("Gluing", curvedSpaceSimulationTypeComboBox), "wrap");
		
		nullSpaceWedgeSurfaceTransmissionCoefficientPanel = new LabelledDoublePanel("Transmission coefficient of null-space-wedge surfaces");
		nullSpaceWedgeSurfaceTransmissionCoefficientPanel.setNumber(nullSpaceWedgeSurfaceTransmissionCoefficient);
		netPanel.add(nullSpaceWedgeSurfaceTransmissionCoefficientPanel, "wrap");
		
		numberOfNegativeSpaceWedgesPanel = new LabelledIntPanel("Number of negative-space wedges per null-space wedge");
		numberOfNegativeSpaceWedgesPanel.setNumber(numberOfNegativeSpaceWedges);
		netPanel.add(numberOfNegativeSpaceWedgesPanel, "wrap");
		
		distanceP1P2Panel = new LabelledDoublePanel("Distance between principal points of lenses 1 and 2 in lens SC wedge");
		distanceP1P2Panel.setNumber(distanceP1P2);
		netPanel.add(distanceP1P2Panel, "wrap");
		
		JPanel structureVisualisationPanel = new JPanel();
		structureVisualisationPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Show structure"));
		structureVisualisationPanel.setLayout(new MigLayout("insets 0"));
		
		showNetEdgesCheckBox = new JCheckBox("Show structure (edges and faces) of net of tetrahedron");
		showNetEdgesCheckBox.setSelected(showNetEdges);
		structureVisualisationPanel.add(showNetEdgesCheckBox, "wrap");

		showNullSpaceWedgesEdgesCheckBox = new JCheckBox("Show edges of null-space wedges");
		showNullSpaceWedgesEdgesCheckBox.setSelected(showNullSpaceWedgeEdges);
		structureVisualisationPanel.add(showNullSpaceWedgesEdgesCheckBox, "wrap");

		edgeRadiusPanel = new LabelledDoublePanel("Tube radius");
		edgeRadiusPanel.setNumber(edgeRadius);
		structureVisualisationPanel.add(edgeRadiusPanel, "wrap");
		
		netPanel.add(structureVisualisationPanel, "wrap");

		tabbedPane.addTab("Net", netPanel);
		
		
		//
		// stuff panel
		//
		
		JPanel stuffPanel = new JPanel();
		stuffPanel.setLayout(new MigLayout("insets 0"));
		
		studioInitialisationComboBox = new JComboBox<StudioInitialisationType>(StudioInitialisationType.limitedValuesForBackgrounds);
		studioInitialisationComboBox.setSelectedItem(studioInitialisation);
		stuffPanel.add(GUIBitsAndBobs.makeRow("Initialise backdrop to", studioInitialisationComboBox), "span");

		JPanel redSpherePanel = new JPanel();
		redSpherePanel.setBorder(GUIBitsAndBobs.getTitledBorder("White sphere"));
		redSpherePanel.setLayout(new MigLayout("insets 0"));

		showSphereCheckBox = new JCheckBox("Show");
		showSphereCheckBox.setSelected(showSphere);
		redSpherePanel.add(showSphereCheckBox, "wrap");
		
		sphereCentrePanel = new LabelledVector3DPanel("Centre");
		sphereCentrePanel.setVector3D(sphereCentre);
		redSpherePanel.add(sphereCentrePanel, "wrap");
		
		sphereRadiusPanel = new LabelledDoublePanel("Radius");
		sphereRadiusPanel.setNumber(sphereRadius);
		redSpherePanel.add(sphereRadiusPanel, "wrap");
		
		stuffPanel.add(redSpherePanel, "wrap");
		
		JPanel coordinateSystemsPanel = new JPanel();
		coordinateSystemsPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Central face normals, projected onto faces"));
		coordinateSystemsPanel.setLayout(new MigLayout("insets 0"));

		showProjectedCoordinateSystemsCheckBox = new JCheckBox("Show");
		showProjectedCoordinateSystemsCheckBox.setSelected(showProjectedCoordinateSystems);
		coordinateSystemsPanel.add(showProjectedCoordinateSystemsCheckBox, "wrap");
		
		projectedCoordinateSystemsSizePanel = new LabelledDoublePanel("Vector length");
		projectedCoordinateSystemsSizePanel.setNumber(projectedCoordinateSystemsSize);
		coordinateSystemsPanel.add(projectedCoordinateSystemsSizePanel, "wrap");

		for(int i=0; i<4; i++)
		{
			showCoordinateSystemForFaceCheckBox[i] = new JCheckBox("Show for face #"+i);
			showCoordinateSystemForFaceCheckBox[i].setSelected(showCoordinateSystemForFace[i]);
			coordinateSystemsPanel.add(showCoordinateSystemForFaceCheckBox[i], "wrap");
		}
		
		stuffPanel.add(coordinateSystemsPanel, "wrap");
		
		tabbedPane.addTab("Stuff", stuffPanel);

		
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

		tabbedPane.addTab("Trajectories", trajectoryPanel);
		
		
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
		
//		cameraTypeComboBox = new JComboBox<CameraType>(CameraType.values());
//		cameraTypeComboBox.setSelectedItem(cameraType);
//		cameraPanel.add(GUIBitsAndBobs.makeRow("Camera type", cameraTypeComboBox), "span");
		
		cameraTypeTabbedPane = new JTabbedPane();
		cameraPanel.add(cameraTypeTabbedPane, "span");
		
		JPanel standardCameraPanel = new JPanel();
		standardCameraPanel.setLayout(new MigLayout("insets 0"));
		
		cameraHorizontalFOVDegPanel = new DoublePanel();
		cameraHorizontalFOVDegPanel.setNumber(cameraHorizontalFOVDeg);
		standardCameraPanel.add(GUIBitsAndBobs.makeRow("Horizontal FOV", cameraHorizontalFOVDegPanel, "°"), "span");
		
		cameraApertureSizeComboBox = new JComboBox<ApertureSizeType>(ApertureSizeType.values());
		cameraApertureSizeComboBox.setSelectedItem(cameraApertureSize);
		standardCameraPanel.add(GUIBitsAndBobs.makeRow("Aperture size", cameraApertureSizeComboBox), "span");		
		
		cameraFocussingDistancePanel = new LabelledDoublePanel("Focussing distance");
		cameraFocussingDistancePanel.setNumber(cameraFocussingDistance);
		standardCameraPanel.add(cameraFocussingDistancePanel, "span");
		
		cameraTypeTabbedPane.addTab("Standard", standardCameraPanel);
		
		JPanel orthographicCameraPanel = new JPanel();
		orthographicCameraPanel.setLayout(new MigLayout("insets 0"));

		orthographicCameraWidthPanel = new LabelledDoublePanel("Horizontal width");
		orthographicCameraWidthPanel.setNumber(orthographicCameraWidth);
		orthographicCameraPanel.add(orthographicCameraWidthPanel, "span");
		
		cameraTypeTabbedPane.addTab("Orthographic", orthographicCameraPanel);
		
		switch(cameraType)
		{
		case ORTHOGRAPHIC:
			cameraTypeTabbedPane.setSelectedIndex(1);
			break;
		case STANDARD:
		default:
			cameraTypeTabbedPane.setSelectedIndex(0);
		}
		
		cameraMaxTraceLevelPanel = new LabelledIntPanel("Max. trace level");
		cameraMaxTraceLevelPanel.setNumber(cameraMaxTraceLevel);
		cameraPanel.add(cameraMaxTraceLevelPanel, "span");

		tabbedPane.addTab("Camera", cameraPanel);
		
		showOrHidePanels();
	}
	
	/**
	 * called before rendering;
	 * override when adding fields
	 */
	@Override
	protected void acceptValuesInInteractiveControlPanel()
	{
		super.acceptValuesInInteractiveControlPanel();
		
		centre = centrePanel.getVector3D();
		sideLength = sideLengthPanel.getNumber();
		rightDirection = rightDirectionPanel.getVector3D();
		upDirection = upDirectionPanel.getVector3D();
		showNullSpaceWedges = showNullSpaceWedgesCheckBox.isSelected();
		nullSpaceWedgeLegLengthFactor = nullSpaceWedgeLegLengthFactorPanel.getNumber();
		nullSpaceWedgeSurfaceTransmissionCoefficient = nullSpaceWedgeSurfaceTransmissionCoefficientPanel.getNumber();
		numberOfNegativeSpaceWedges = numberOfNegativeSpaceWedgesPanel.getNumber();
		distanceP1P2 = distanceP1P2Panel.getNumber();
		curvedSpaceSimulationType = (GluingType)curvedSpaceSimulationTypeComboBox.getSelectedItem();
		edgeRadius = edgeRadiusPanel.getNumber();
		showNetEdges = showNetEdgesCheckBox.isSelected();
		showNullSpaceWedgeEdges = showNullSpaceWedgesEdgesCheckBox.isSelected();
		
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
		
		studioInitialisation = (StudioInitialisationType)(studioInitialisationComboBox.getSelectedItem());
		showSphere = showSphereCheckBox.isSelected();
		sphereCentre = sphereCentrePanel.getVector3D();
		sphereRadius = sphereRadiusPanel.getNumber();
		showProjectedCoordinateSystems = showProjectedCoordinateSystemsCheckBox.isSelected();
		for(int i=0; i<4; i++) showCoordinateSystemForFace[i] = showCoordinateSystemForFaceCheckBox[i].isSelected();
		projectedCoordinateSystemsSize = projectedCoordinateSystemsSizePanel.getNumber();
		
		cameraType = (cameraTypeTabbedPane.getSelectedIndex()==0)?CameraType.STANDARD:CameraType.ORTHOGRAPHIC;
		cameraViewCentre = cameraViewCentrePanel.getVector3D();
		cameraViewDirection = Vector3D.difference(cameraViewCentre, cameraPositionPanel.getVector3D());
		cameraDistance = cameraViewDirection.getLength();
		cameraHorizontalFOVDeg = cameraHorizontalFOVDegPanel.getNumber();
		cameraApertureSize = (ApertureSizeType)(cameraApertureSizeComboBox.getSelectedItem());
		cameraFocussingDistance = cameraFocussingDistancePanel.getNumber();
		cameraMaxTraceLevel = cameraMaxTraceLevelPanel.getNumber();
		orthographicCameraWidth = orthographicCameraWidthPanel.getNumber();
	}
	
	private void showOrHidePanels()
	{
		// show or hide additional parameters as appropriate
		switch(curvedSpaceSimulationType)
		{
		case NEGATIVE_SPACE_WEDGES:
			numberOfNegativeSpaceWedgesPanel.setEnabled(true);
			break;
		case LENSES_COMPLETELY_SYMMETRIC:
		case LENSES_QUITE_SYMMETRIC:
			distanceP1P2Panel.setEnabled(true);
			break;
		case PERFECT:
		case MIRROR_APPROXIMATION:
		default:
			numberOfNegativeSpaceWedgesPanel.setEnabled(false);
			distanceP1P2Panel.setEnabled(false);
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		super.actionPerformed(e);
		
		if(e.getSource().equals(curvedSpaceSimulationTypeComboBox))
		{
			curvedSpaceSimulationType = (GluingType)(curvedSpaceSimulationTypeComboBox.getSelectedItem());
			
			showOrHidePanels();
		}
	}


	public static void main(final String[] args)
	{
		(new NetOfTetrahedronVisualiser()).run();
	}
}
