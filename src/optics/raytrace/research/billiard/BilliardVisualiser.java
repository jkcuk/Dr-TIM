package optics.raytrace.research.billiard;

import java.awt.event.ActionEvent;
import java.io.PrintStream;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import math.*;
import net.miginfocom.swing.MigLayout;
import optics.raytrace.sceneObjects.*;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectIntersection;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectUnion;
import optics.raytrace.surfaces.ColourFilter;
import optics.raytrace.surfaces.IdealThinCylindricalMirrorSurfaceSimple;
import optics.raytrace.surfaces.IdealThinSphericalMirrorSurfaceSimple;
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
 * Visualise the view on various billiards, including the planar part of a spherical wedge billiard [1].
 * 
 * This consists of a spherical wedge of wedge angle theta.
 * The two planar parts form, after flattening, a disc of radius r, centred on the origin, lying in the y=0 plane.
 * We visualise here the view on these flattened planar parts, with an added third dimension, here y.
 * The effect of the spherical part is taken care of by a cylindrical wall around the disc.
 * 
 * References:
 * [1] T. Tyc and D. Cidlinsky, "Spherical wedge billiard: From chaos to fractals and Talbot carpets", PRE 106, 054202 (2022)
 * 
 * 
 * The main method renders the image defined by createStudio(), saves it to a file
 * (whose name is given by the constant FILENAME), and displays it in a new window.
 * 
 * @author  Johannes Courtial
 */
public class BilliardVisualiser extends NonInteractiveTIMEngine
{
	// the wedge billiard
	
	/**
	 * height of the wall around the billiard
	 */
	private double wallHeight;
	
	public enum BilliardType
	{
		MIRRORS("Mirror walls", "Four cylindrical/spherical/imaging mirrors"),
		SPHERICAL_WEDGE("Spherical wedge", "Surface of a spherical segment [Tyc & Cidlinsky, PRE 106, 054202 (2022)]");

		public String name, description;

		private BilliardType(String name, String description)
		{
			this.name = name;
			this.description = description;
		}	
		@Override
		public String toString() {return name;}
	}
	
	/**
	 * show lenses, or don't show lenses but rotate camera so that lattice is rotated in the same way
	 */
	protected BilliardType billiardType;

	
	private double cylinderRadius;
	
	// spherical-wedge billiard
	
	/**
	 * the wedge angle of the spherical wedge, in degrees
	 */
	private double phiDeg;
	
	private double roundTripTransmissionCoefficient;
	
	private boolean showEdge;
	private double edgeRadius;
	
	// mirror billiard
	
	// this consists of four spherical mirrors, two whose optical axis is the x axis, two whose optical axis is the z axis
	
	// x coordinates of the principal points of the mirrors whose optical axis is the x axis
	private double p1x, p3x;
	
	// z coordinates of the principal points of the mirrors whose optical axis is the z axis
	private double p2z, p4z;
	
	// y coordinates of the principal points of the mirrors (in case they are spherical
	private double p1y, p2y, p3y, p4y;
	
	// spherical mirrors if true, cylindrical otherwise
	private boolean sphericalMirrors;
	
	// radii of curvature of the four  mirrors
	private double r1, r2, r3, r4;
	
	private boolean showM1, showM2, showM3, showM4;
	
	// planar, perfectly imaging, mirrors if true, spherical/cylindrical otherwise
	private boolean planarImagingMirrors;
	
	// camera
	
	private Vector3D cameraPosition;
	private double movieRotationAngleDeg;
	private Vector3D movieRotationAxis;
	
	// already defined: 
	//	cameraViewCentre
	//	cameraFocussingDistance
	//	cameraHorizontalFOVDeg
	//	cameraMaxTraceLevel
	//	cameraApertureSize
	//	cameraExposureCompensation
	
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

	
	public BilliardVisualiser()
	{
		nonInteractiveTIMAction = NonInteractiveTIMActionEnum.INTERACTIVE;
		renderQuality = RenderQualityEnum.DRAFT;
		
		numberOfFrames = 10;
		firstFrame = 0;
		lastFrame = numberOfFrames-1;
		movie = false;
		movieRotationAngleDeg = 10;
		movieRotationAxis = new Vector3D(0, 1, 0);

		
		// the billiards
		wallHeight = 6;
		roundTripTransmissionCoefficient = 0.96;
		
		// the spherical wedge billiard
		billiardType = BilliardType.SPHERICAL_WEDGE;
		cylinderRadius = 5;
		phiDeg = 90;
		showEdge = true;
		edgeRadius = 0.02;
		
		// mirror billiard
//		p1x = -1;
//		p3x = +2;
//		p2z = -3;
//		p4z = +1;
//		r1 = 1.9;
//		r2 = 6;
//		r3 = 4;
//		r4 = 2;
		p1x = -3;
		p3x = +3;
		p2z = -2;
		p4z = +2;
		r1 = 2;
		r2 = 100000;
		r3 = 2;
		r4 = 100000;
		showM1 = true;
		showM2 = true;
		showM3 = true;
		showM4 = true;
		
		p1y = 0.5*wallHeight;
		p2y = 0.5*wallHeight;
		p3y = 0.5*wallHeight;
		p4y = 0.5*wallHeight;
		sphericalMirrors = false;
		planarImagingMirrors = false;
		
		// camera parameters; these are often set (or altered) in createStudio()
		// camera inside
//		cameraViewCentre = new Vector3D(1, -0.1, 0);
//		cameraPosition = new Vector3D(0, 1, 0);
//		cameraHorizontalFOVDeg = 20;
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
		
		windowTitle = "Dr TIM's billiard visualiser";
		windowWidth = 1550;
		windowHeight = 850;

	}

	@Override
	public String getClassName()
	{
		return "BilliardVisualiser"	// the name
				;
	}
	
	@Override
	public void writeParameters(PrintStream printStream)
	{
		// write any parameters not defined in NonInteractiveTIMEngine
		printStream.println("Scene initialisation");
		printStream.println();
		
		
		// billiards
		
		printStream.println("wallHeight = "+wallHeight);
		printStream.println("roundTripTransmissionCoefficient = "+roundTripTransmissionCoefficient);
		
		printStream.println("billiardType = "+billiardType);

		// spherical wedge billiard
		printStream.println("cylinderRadius = "+cylinderRadius);
		printStream.println("phiDeg = "+phiDeg);
		printStream.println("showEdge = "+showEdge);
		printStream.println("edgeRadius = "+edgeRadius);

		// cylindrical-mirror billiard
		printStream.println("p1x = "+p1x);
		printStream.println("p2z = "+p2z);
		printStream.println("p3x = "+p3x);
		printStream.println("p4z = "+p4z);
		printStream.println("p1y = "+p1y);
		printStream.println("p2y = "+p2y);
		printStream.println("p3y = "+p3y);
		printStream.println("p4y = "+p4y);
		printStream.println("r1 = "+r1);
		printStream.println("r2 = "+r2);
		printStream.println("r3 = "+r3);
		printStream.println("r4 = "+r4);
		printStream.println("showM1 = "+showM1);
		printStream.println("showM2 = "+showM2);
		printStream.println("showM3 = "+showM3);
		printStream.println("showM4 = "+showM4);
		printStream.println("sphericalMirrors = "+sphericalMirrors);
		printStream.println("planarImagingMirrors = "+planarImagingMirrors);

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
	 * Return a scene object that, when intersected with similar objects, represents the inside of a cylindrical or spherical mirror.
	 * If radius > 0, i.e. if the mirror is concave, then return the union of a cylinder/sphere and the half-space whose
	 * border lies on the cylinder axis/sphere centre and which includes the "wrong" half of the cylinder/sphere.
	 * If radius < 0 (convex mirror), just return the cylinder.
	 * @param description
	 * @param principalPoint
	 * @param opticalAxisInwards
	 * @param cylinderAxis
	 * @param radius
	 * @return a SceneObject which, when intersected with the SceneObjects representing the other mirrors, results in the billiard mirror
	 */
	private SceneObject getMirrorInside(
			String description,
			Vector3D principalPoint,
			Vector3D opticalAxisInwards,
			Vector3D cylinderAxis,
			double reflectionCoefficient,
			boolean shadowThrowing,
			double radius,
			Studio studio
		)
	{
		Vector3D centre = Vector3D.sum(principalPoint, opticalAxisInwards.getNormalised().getProductWith(radius));
		SceneObject s;
		if(sphericalMirrors)
			s = new Sphere(
					description,
					centre,	// centre
					Math.abs(radius),	// radius
					new Reflective(
							reflectionCoefficient,
							shadowThrowing
						),	// surfaceProperty,
					null,	// parent -- set later
					studio
				);
		else
			s = new CylinderMantle(
				description,
				centre,	// startPoint,
				Vector3D.sum(centre, cylinderAxis),	// endPoint,
				Math.abs(radius),	// radius,
				true,	// infinite
				// SurfaceColour.PURPLE_SHINY,
				new Reflective(
						reflectionCoefficient,
						shadowThrowing
					),	// surfaceProperty,
				null,	// parent -- set later
				studio
			);

		if(radius < 0) return s;

		// radius > 0
		SceneObjectUnion u = new SceneObjectUnion(
		// SceneObjectPrimitiveIntersection i = new SceneObjectPrimitiveIntersection(
				description,
				null,	// parent -- set later
				studio
			);
		u.addSceneObject(s);
		u.addSceneObject(new Plane(
				"boundary of half space ensuring correct part of \""+description+"\" is shown",	// description,
				centre,	// pointOnPlane,
				opticalAxisInwards.getReverse(),	// normal, 
				SurfaceColour.RED_MATT,	// surfaceProperty; this surface should never be visible, so give it a "warning" colour
				null,	// parent -- will be set when adding to i
				studio
				));
		return u;
	}
	
		/**
		 * Returns a planar mirror behaving like a thin ideal cylindrical/spherical lens in reflection.
		 * @param description
		 * @param principalPoint
		 * @param opticalAxisInwards
		 * @param cylinderAxis
		 * @param reflectionCoefficient
		 * @param shadowThrowing
		 * @param focalLength
		 * @param studio
		 * @return
		 */
		private SceneObject getPlanarMirrorInside(
				String description,
				Vector3D principalPoint,
				Vector3D opticalAxisOutwards,
				Vector3D cylinderAxis,
				double reflectionCoefficient,
				boolean shadowThrowing,
				double focalLength,
				Studio studio
			)
		{
			SurfaceProperty surface;
			if(sphericalMirrors)
			{
				surface = new IdealThinSphericalMirrorSurfaceSimple(
						principalPoint,
						opticalAxisOutwards,	// opticalAxisDirection
						focalLength,
						reflectionCoefficient,
						shadowThrowing
					);
			}
			else
			{
				surface = new IdealThinCylindricalMirrorSurfaceSimple(
						principalPoint,	// lensCentre,
						opticalAxisOutwards,	// opticalAxisDirection,
						Vector3D.crossProduct(opticalAxisOutwards, cylinderAxis),	// gradientDirection
						focalLength,
						reflectionCoefficient,
						shadowThrowing
					);
			}
			return new Plane(
					description,
					principalPoint,	// pointOnPlane,
					opticalAxisOutwards,	// normal, 
					surface,	// surfaceProperty
					null,	// parent -- will be set when adding to i
					studio
					);
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
		
		switch(billiardType)
		{
		case SPHERICAL_WEDGE:
			// the wedge billiard			
			// the disc is formed by the relevant part of the checkerboard floor; all I need to do is add the cylinder that
			// simulates propagation on the spherical part of the surface
			CylinderMantle cylinder = new CylinderMantle(
					"cylinder modelling effect of spherical part",	// description
					new Vector3D(0, 0, 0),	// startPoint
					up.getProductWith(wallHeight),	//  endPoint
					cylinderRadius,	// radius
					new SphericalWedgeBilliardSphericalPart(
							phiDeg,
							up,	// upDirection,
							new Vector3D(0, 0, 1),	// edgeDirection,
							cylinderRadius,	// radius,
							roundTripTransmissionCoefficient,	// transmissionCoefficient
							false	// shadowThrowing
							),	// surfaceProperty
					scene,	// parent
					studio
					);
			scene.addSceneObject(cylinder);

			// cylinder along the edge
			scene.addSceneObject(
					new Cylinder(
							"Edge",	// description
							new Vector3D(0, 0, -cylinderRadius),	// start point
							new Vector3D(0, 0,  cylinderRadius),	// end point
							edgeRadius,	// radius
							SurfaceColour.WHITER_SHINY,	// surface colour
							scene, studio
							),
					showEdge
					);
			break;
		case MIRRORS:
			// add the mirrors
//		printStream.println("p1x = "+p1x);
//		printStream.println("p3x = "+p3x);
//		printStream.println("p2z = "+p2z);
//		printStream.println("p4z = "+p4z);
//		printStream.println("r1 = "+r1);
//		printStream.println("r2 = "+r2);
//		printStream.println("r3 = "+r3);
//		printStream.println("r4 = "+r4);
			// create a scene-object intersection, ...
			SceneObjectIntersection i = new SceneObjectIntersection(
					"Mirrors",	// description
					scene,	// parent
					studio
				);
			// ..., add it to the scene, ...
			scene.addSceneObject(i);
			
			// ... and add the four mirrors
			
			// calculate the principal points
			Vector3D p1 = new Vector3D(p1x, p1y, 0);
			Vector3D p2 = new Vector3D(0, p2y, p2z);
			Vector3D p3 = new Vector3D(p3x, p3y, 0);
			Vector3D p4 = new Vector3D(0, p4y, p4z);
			
			if(planarImagingMirrors)
			{
				if(showM1)
				{
					i.addPositiveSceneObject(
							getPlanarMirrorInside(
									"Mirror 1",	// description,
									p1,	// principalPoint,
									new Vector3D(p1x-p3x, 0, 0),	// opticalAxisOutwards,
									up,	// cylinderAxis
									roundTripTransmissionCoefficient,	// reflectionCoefficient,
									false,	// shadowThrowing,
									0.5*r1,	// focalLength,
									studio
								));
				}
				if(showM2)
				{
					i.addPositiveSceneObject(
							getPlanarMirrorInside(
									"Mirror 2",	// description,
									p2,	// principalPoint,
									new Vector3D(0, 0, p2z-p4z),	// opticalAxisOutwards,
									up,	// cylinderAxis
									roundTripTransmissionCoefficient,	// reflectionCoefficient,
									false,	// shadowThrowing,
									0.5*r2,	// focalLength,
									studio
								));
				}
				if(showM3)
				{
					i.addPositiveSceneObject(
							getPlanarMirrorInside(
									"Mirror 3",	// description,
									p3,	// principalPoint,
									new Vector3D(p3x-p1x, 0, 0),	// opticalAxisOutwards,
									up,	// cylinderAxis
									roundTripTransmissionCoefficient,	// reflectionCoefficient,
									false,	// shadowThrowing,
									0.5*r3,	// focalLength,
									studio
								));
				}
				if(showM4)
				{
					i.addPositiveSceneObject(
							getPlanarMirrorInside(
									"Mirror 4",	// description,
									p4,	// principalPoint,
									new Vector3D(0, 0, p4z-p2z),	// opticalAxisOutwards,
									up,	// cylinderAxis
									roundTripTransmissionCoefficient,	// reflectionCoefficient,
									false,	// shadowThrowing,
									0.5*r4,	// focalLength,
									studio
								));
				}
			}
			else
			{
				// mirror 1
				if(showM1)
				{
					SceneObject s = getMirrorInside(
							"Mirror 1 (r="+r1+")",	// description,
							p1,	// principalPoint,
							Vector3D.difference(p3, p1),	// opticalAxisInwards,
							up,	// cylinderAxis,
							roundTripTransmissionCoefficient,	// reflectionCoefficient,
							false,	// shadowThrowing,
							r1,	// radius,
							studio
							);
					if(r1 < 0) i.addNegativeSceneObject(s);
					else i.addPositiveSceneObject(s);
				}

				// mirror 2
				if(showM2)
				{
					SceneObject s = getMirrorInside(
							"Mirror 2 (r="+r2+")",	// description,
							p2,	// principalPoint,
							Vector3D.difference(p4, p2),	// opticalAxisInwards,
							up,	// cylinderAxis,
							roundTripTransmissionCoefficient,	// reflectionCoefficient,
							false,	// shadowThrowing,
							r2,	// radius,
							studio
							);
					if(r2 < 0) i.addNegativeSceneObject(s);
					else i.addPositiveSceneObject(s);
				}

				// mirror 3
				if(showM3)
				{
					SceneObject s = getMirrorInside(
							"Mirror 3 (r="+r3+")",	// description,
							p3,	// principalPoint,
							Vector3D.difference(p1, p3),	// opticalAxisInwards,
							up,	// cylinderAxis,
							roundTripTransmissionCoefficient,	// reflectionCoefficient,
							false,	// shadowThrowing,
							r3,	// radius,
							studio
							);
					if(r3 < 0) i.addNegativeSceneObject(s);
					else i.addPositiveSceneObject(s);
				}

				// mirror 4
				if(showM4)
				{
					SceneObject s = getMirrorInside(
							"Mirror 4 (r="+r4+")",	// description,
							p4,	// principalPoint,
							Vector3D.difference(p2, p4),	// opticalAxisInwards,
							up,	// cylinderAxis,
							roundTripTransmissionCoefficient,	// reflectionCoefficient,
							false,	// shadowThrowing,
							r4,	// radius,
							studio
							);
					if(r4 < 0) i.addNegativeSceneObject(s);
					else i.addPositiveSceneObject(s);
				}
			}

			// the "top lid" that cuts off the (otherwise infinitely high) cylinders
			i.addInvisiblePositiveSceneObject(
					// 			i.addPositiveSceneObject(
					new Plane(
							"lid (to cut off the (otherwise infinitely high) cylinders)",	// description,
							new Vector3D(0, wallHeight, 0),	// pointOnPlane,
							up,	// normal, 
							ColourFilter.GREEN,	// Transparent.PERFECT,	// surfaceProperty -- doesn't matter since invisible anyway
							i,	// parent,
							studio
							));
			break;
		default:
		}

		
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

		// System.exit(-1);

//		// for test purposes, define a ray...
//		Ray r = new RayWithTrajectory(
//				new Vector3D(0.3, 0, -10),	// start point
//				new Vector3D(0, 0, 1),	// direction
//				0,	// time
//				true	// trajectoriesReportToConsole
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
	
	
	
	// GUI
	
	// billiards
	private JTabbedPane billiardTypeTabbedPane;	// JComboBox<BilliardType> billiardTypeComboBox;

	private LabelledDoublePanel cylinderHeightPanel, roundTripTransmissionCoefficientPanel;
	
	// spherical wedge billiard
	private DoublePanel phiDegPanel;
	private LabelledDoublePanel cylinderRadiusPanel, edgeRadiusPanel;
	private JCheckBox showEdgeCheckBox;
	
	// mirror billiard
	private LabelledDoublePanel p1xPanel, p3xPanel, p2zPanel, p4zPanel, p1yPanel, p2yPanel, p3yPanel, p4yPanel, rOrF1Panel, rOrF2Panel, rOrF3Panel, rOrF4Panel;
	private JCheckBox showM1CheckBox, showM2CheckBox, showM3CheckBox, showM4CheckBox, sphericalMirrorsCheckBox, planarImagingMirrorsCheckBox;
	
	private boolean useFocalLengths = false;
	private JCheckBox useFocalLengthsCheckBox;

//	printStream.println("p1x = "+p1x);
//	printStream.println("p3x = "+p3x);
//	printStream.println("p2z = "+p2z);
//	printStream.println("p4z = "+p4z);
//	printStream.println("r1 = "+r1);
//	printStream.println("r2 = "+r2);
//	printStream.println("r3 = "+r3);
//	printStream.println("r4 = "+r4);


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
		// billiards panel
		//
		
		JPanel billiardPanel = new JPanel();
		billiardPanel.setLayout(new MigLayout("insets 0"));
		tabbedPane.addTab("Billiard", billiardPanel);
		
		billiardTypeTabbedPane = new JTabbedPane();
		// billiardTypeTabbedPane.addChangeListener(this);
		billiardPanel.add(billiardTypeTabbedPane, "span");

		// the spherical-wedge-billiard panel
		
		JPanel sphericalWedgeBilliardPanel = new JPanel();
		sphericalWedgeBilliardPanel.setLayout(new MigLayout("insets 0"));
		billiardTypeTabbedPane.addTab(BilliardType.SPHERICAL_WEDGE.name, sphericalWedgeBilliardPanel);
		
		sphericalWedgeBilliardPanel.add(new JLabel(BilliardType.SPHERICAL_WEDGE.description), "wrap");
		
		phiDegPanel = new DoublePanel();
		phiDegPanel.setNumber(phiDeg);
		sphericalWedgeBilliardPanel.add(GUIBitsAndBobs.makeRow("Wedge angle, phi", phiDegPanel, "°"), "span");

//		phiDegPanel = new LabelledDoublePanel("Wedge angle, phi");
//		phiDegPanel.add(new JLabel("degrees"));
//		phiDegPanel.setNumber(phiDeg);
//		sphericalWedgeBilliardPanel.add(phiDegPanel, "wrap");

		cylinderRadiusPanel = new LabelledDoublePanel("Radius of disc");
		cylinderRadiusPanel.setNumber(cylinderRadius);
		sphericalWedgeBilliardPanel.add(cylinderRadiusPanel, "wrap");
		
		JPanel edgePanel = new JPanel();
		edgePanel.setLayout(new MigLayout("insets 0"));
		edgePanel.setBorder(GUIBitsAndBobs.getTitledBorder("Edge (along z axis)"));
		
		showEdgeCheckBox = new JCheckBox("Show");
		showEdgeCheckBox.setSelected(showEdge);
		edgePanel.add(showEdgeCheckBox, "wrap");

		edgeRadiusPanel = new LabelledDoublePanel("Radius");
		edgeRadiusPanel.setNumber(edgeRadius);
		edgePanel.add(edgeRadiusPanel, "wrap");
		
		sphericalWedgeBilliardPanel.add(edgePanel, "wrap");

		// the mirrors-billiard panel
		
		JPanel mirrorsBilliardPanel = new JPanel();
		mirrorsBilliardPanel.setLayout(new MigLayout("insets 0"));
		billiardTypeTabbedPane.addTab(BilliardType.MIRRORS.name, mirrorsBilliardPanel);

		mirrorsBilliardPanel.add(new JLabel(BilliardType.MIRRORS.description), "wrap");
		
		sphericalMirrorsCheckBox = new JCheckBox("Spherical mirrors (instead of cylindrical mirrors)");
		sphericalMirrorsCheckBox.setSelected(sphericalMirrors);
		sphericalMirrorsCheckBox.addActionListener(this);
		mirrorsBilliardPanel.add(sphericalMirrorsCheckBox, "span");
		
		planarImagingMirrorsCheckBox = new JCheckBox("Planar, perfectly imaging, mirrors (instead of curved mirrors)");
		planarImagingMirrorsCheckBox.setSelected(planarImagingMirrors);
		planarImagingMirrorsCheckBox.addActionListener(this);
		mirrorsBilliardPanel.add(planarImagingMirrorsCheckBox, "span");

		useFocalLengthsCheckBox = new JCheckBox("Use focal lengths instead of radii of curvature");
		useFocalLengthsCheckBox.setSelected(useFocalLengths);
		useFocalLengthsCheckBox.addActionListener(this);
		mirrorsBilliardPanel.add(useFocalLengthsCheckBox, "span");
		
		JPanel cylindricalMirrorPanel1 = new JPanel();
		cylindricalMirrorPanel1.setLayout(new MigLayout("insets 0"));
		cylindricalMirrorPanel1.setBorder(GUIBitsAndBobs.getTitledBorder("Mirror 1"));
		mirrorsBilliardPanel.add(cylindricalMirrorPanel1, "wrap");

		showM1CheckBox = new JCheckBox("Show");
		showM1CheckBox.setSelected(showM1);
		cylindricalMirrorPanel1.add(showM1CheckBox);
		rOrF1Panel = new LabelledDoublePanel("Radius of curvature");
		rOrF1Panel.setNumber(r1);
		cylindricalMirrorPanel1.add(rOrF1Panel, "span");

		p1xPanel = new LabelledDoublePanel("x coordinate of principal point");
		p1xPanel.setNumber(p1x);
		cylindricalMirrorPanel1.add(p1xPanel, "span");
		
		p1yPanel = new LabelledDoublePanel("y coordinate of principal point");
		p1yPanel.setNumber(p1y);
		p1yPanel.setEnabled(sphericalMirrors);
		cylindricalMirrorPanel1.add(p1yPanel, "span");


		JPanel cylindricalMirrorPanel2 = new JPanel();
		cylindricalMirrorPanel2.setLayout(new MigLayout("insets 0"));
		cylindricalMirrorPanel2.setBorder(GUIBitsAndBobs.getTitledBorder("Mirror 2"));
		mirrorsBilliardPanel.add(cylindricalMirrorPanel2, "wrap");

		showM2CheckBox = new JCheckBox("Show");
		showM2CheckBox.setSelected(showM2);
		cylindricalMirrorPanel2.add(showM2CheckBox);
		rOrF2Panel = new LabelledDoublePanel("Radius of curvature");
		rOrF2Panel.setNumber(r2);
		cylindricalMirrorPanel2.add(rOrF2Panel, "span");

		p2zPanel = new LabelledDoublePanel("z coordinate of principal point");
		p2zPanel.setNumber(p2z);
		cylindricalMirrorPanel2.add(p2zPanel, "span");

		p2yPanel = new LabelledDoublePanel("y coordinate of principal point");
		p2yPanel.setNumber(p2y);
		p2yPanel.setEnabled(sphericalMirrors);
		cylindricalMirrorPanel2.add(p2yPanel, "span");


		JPanel cylindricalMirrorPanel3 = new JPanel();
		cylindricalMirrorPanel3.setLayout(new MigLayout("insets 0"));
		cylindricalMirrorPanel3.setBorder(GUIBitsAndBobs.getTitledBorder("Mirror 3"));
		mirrorsBilliardPanel.add(cylindricalMirrorPanel3, "wrap");

		showM3CheckBox = new JCheckBox("Show");
		showM3CheckBox.setSelected(showM3);
		cylindricalMirrorPanel3.add(showM3CheckBox);
		rOrF3Panel = new LabelledDoublePanel("Radius of curvature");
		rOrF3Panel.setNumber(r3);
		cylindricalMirrorPanel3.add(rOrF3Panel, "span");

		p3xPanel = new LabelledDoublePanel("x coordinate of principal point");
		p3xPanel.setNumber(p3x);
		cylindricalMirrorPanel3.add(p3xPanel, "span");

		p3yPanel = new LabelledDoublePanel("y coordinate of principal point");
		p3yPanel.setNumber(p3y);
		p3yPanel.setEnabled(sphericalMirrors);
		cylindricalMirrorPanel3.add(p3yPanel, "span");


		JPanel cylindricalMirrorPanel4 = new JPanel();
		cylindricalMirrorPanel4.setLayout(new MigLayout("insets 0"));
		cylindricalMirrorPanel4.setBorder(GUIBitsAndBobs.getTitledBorder("Mirror 4"));
		mirrorsBilliardPanel.add(cylindricalMirrorPanel4, "wrap");

		showM4CheckBox = new JCheckBox("Show");
		showM4CheckBox.setSelected(showM4);
		cylindricalMirrorPanel4.add(showM4CheckBox);
		rOrF4Panel = new LabelledDoublePanel("Radius of curvature");
		rOrF4Panel.setNumber(r4);
		cylindricalMirrorPanel4.add(rOrF4Panel, "span");

		p4zPanel = new LabelledDoublePanel("z coordinate of principal point");
		p4zPanel.setNumber(p4z);
		cylindricalMirrorPanel4.add(p4zPanel, "span");

		p4yPanel = new LabelledDoublePanel("y coordinate of principal point");
		p4yPanel.setNumber(p4y);
		p4yPanel.setEnabled(sphericalMirrors);
		cylindricalMirrorPanel4.add(p4yPanel, "span");


		// common parameters
		
		cylinderHeightPanel = new LabelledDoublePanel("Wall height");
		cylinderHeightPanel.setNumber(wallHeight);
		billiardPanel.add(cylinderHeightPanel, "wrap");
				
		roundTripTransmissionCoefficientPanel = new LabelledDoublePanel("Round-trip transmission coefficient");
		roundTripTransmissionCoefficientPanel.setNumber(roundTripTransmissionCoefficient);
		billiardPanel.add(roundTripTransmissionCoefficientPanel, "wrap");
		

		
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
		
		// billiard

		int billiardTypeIndex = billiardTypeTabbedPane.getSelectedIndex();
		String tabTitle = billiardTypeTabbedPane.getTitleAt(billiardTypeIndex);
		for(BilliardType bt:BilliardType.values())
		{
			if(bt.name.equals(tabTitle)) 
			{
				billiardType = bt;
				break;
			}
		}
		System.out.println("billiardType = "+billiardType);

		wallHeight = cylinderHeightPanel.getNumber();
		roundTripTransmissionCoefficient = roundTripTransmissionCoefficientPanel.getNumber();

		// spherical-wedge billiard
		phiDeg = phiDegPanel.getNumber();
		cylinderRadius = cylinderRadiusPanel.getNumber();
		showEdge = showEdgeCheckBox.isSelected();
		edgeRadius = edgeRadiusPanel.getNumber();
		
		// mirror billiard
		sphericalMirrors = sphericalMirrorsCheckBox.isSelected();
		planarImagingMirrors = planarImagingMirrorsCheckBox.isSelected();
		p1x = p1xPanel.getNumber();
		p2z = p2zPanel.getNumber();
		p3x = p3xPanel.getNumber();
		p4z = p4zPanel.getNumber();
		p1y = p1yPanel.getNumber();
		p2y = p2yPanel.getNumber();
		p3y = p3yPanel.getNumber();
		p4y = p4yPanel.getNumber();
		r1 = (useFocalLengths?2:1)*rOrF1Panel.getNumber();
		r2 = (useFocalLengths?2:1)*rOrF2Panel.getNumber();
		r3 = (useFocalLengths?2:1)*rOrF3Panel.getNumber();
		r4 = (useFocalLengths?2:1)*rOrF4Panel.getNumber();
		showM1 = showM1CheckBox.isSelected();
		showM2 = showM2CheckBox.isSelected();
		showM3 = showM3CheckBox.isSelected();
		showM4 = showM4CheckBox.isSelected();

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

	private void dealWithUseFocalLengthsChange()
	{
		boolean wasFocalLengths = useFocalLengths;
		useFocalLengths = useFocalLengthsCheckBox.isSelected();
		if(wasFocalLengths != useFocalLengths)
		{
			// the value has changed
			if(useFocalLengths)
			{
				// convert radii of curvature to (paraxial) focal lengths
				rOrF1Panel.setNumber(0.5*rOrF1Panel.getNumber());
				rOrF2Panel.setNumber(0.5*rOrF2Panel.getNumber());
				rOrF3Panel.setNumber(0.5*rOrF3Panel.getNumber());
				rOrF4Panel.setNumber(0.5*rOrF4Panel.getNumber());
				rOrF1Panel.getLabel().setText("Focal length");
				rOrF2Panel.getLabel().setText("Focal length");
				rOrF3Panel.getLabel().setText("Focal length");
				rOrF4Panel.getLabel().setText("Focal length");
			}
			else
			{
				// convert (paraxial) focal lengths to radii of curvature
				rOrF1Panel.setNumber(2*rOrF1Panel.getNumber());
				rOrF2Panel.setNumber(2*rOrF2Panel.getNumber());
				rOrF3Panel.setNumber(2*rOrF3Panel.getNumber());
				rOrF4Panel.setNumber(2*rOrF4Panel.getNumber());
				rOrF1Panel.getLabel().setText("Radius of curvature");
				rOrF2Panel.getLabel().setText("Radius of curvature");
				rOrF3Panel.getLabel().setText("Radius of curvature");
				rOrF4Panel.getLabel().setText("Radius of curvature");
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		super.actionPerformed(e);
		
		if(e.getSource().equals(sphericalMirrorsCheckBox))
		{
			boolean localSphericalMirrors = sphericalMirrorsCheckBox.isSelected();
			p1yPanel.setEnabled(localSphericalMirrors);
			p2yPanel.setEnabled(localSphericalMirrors);
			p3yPanel.setEnabled(localSphericalMirrors);
			p4yPanel.setEnabled(localSphericalMirrors);
		}
		else if(e.getSource().equals(planarImagingMirrorsCheckBox))
		{
			useFocalLengthsCheckBox.setSelected(true);
			dealWithUseFocalLengthsChange();
		}
		else if(e.getSource().equals(useFocalLengthsCheckBox))
		{
			dealWithUseFocalLengthsChange();
		}
	}

 	public static void main(final String[] args)
   	{
  		(new BilliardVisualiser()).run();
  	}
 }
