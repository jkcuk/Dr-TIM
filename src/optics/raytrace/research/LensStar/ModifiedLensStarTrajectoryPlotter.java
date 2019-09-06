package optics.raytrace.research.LensStar;

import java.awt.*;
import java.text.DecimalFormat;

import math.*;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.surfaces.SurfaceColour;
import optics.raytrace.surfaces.SurfaceColourLightSourceIndependent;
import optics.raytrace.core.*;
import optics.DoubleColour;
import optics.raytrace.GUI.cameras.EditableOrthographicCamera;
import optics.raytrace.GUI.cameras.QualityType;
import optics.raytrace.GUI.nonInteractive.PhotoCanvas;
import optics.raytrace.GUI.nonInteractive.PhotoFrame;
import optics.raytrace.GUI.sceneObjects.EditableRayTrajectory;
import optics.raytrace.GUI.sceneObjects.EditableLensStar;


/**
 * Plots a number of trajectories through lens stars.
 * In the limit of the number of lenses going to infinity, the trajectories are conic sections.
 * 
 * @author Johannes Courtial
 */
public class ModifiedLensStarTrajectoryPlotter
{
	public static final double PRINCIPAL_POINT_DISTANCE = -.4;
	public static final int NUMBER_OF_LENSES = 100;
	public static final int MAX_TRACE_LEVEL_MULTIPLIER = 15;
	public static final boolean SHOW_CIRCLE_TRAJECTORY = false;
	public static final boolean SHOW_ELLIPTIC_TRAJECTORY = true;
	public static final boolean SHOW_PARABOLIC_TRAJECTORY = false;
	public static final boolean SHOW_HYPERBOLIC_TRAJECTORY = false;
	public static final boolean SHOW_LENS_EDGES = true;
	public static final boolean TEST = false;
	public static final boolean MOVIE = false;

	/**
	 * @return	the number of frames in the movie
	 */
	public static int getNoOfFrames()
	{
		return 10;
	}
	
	/**
	 * Filename under which main saves the rendered image.
	 * Alter to save the rendered image under a different name.
	 * @return	the filename under which main saves the rendered image.
	 */
	public static String getFilename(int frame)
	{
		return "ModifiedLensStarTrajectory "	// the name
			+ " principal-point distance "+PRINCIPAL_POINT_DISTANCE
			+ (SHOW_CIRCLE_TRAJECTORY?"C":"")
			+ (SHOW_ELLIPTIC_TRAJECTORY?"E":"")
			+ (SHOW_PARABOLIC_TRAJECTORY?"P":"")
			+ (SHOW_HYPERBOLIC_TRAJECTORY?"H":"")
			+ " number of lenses "+NUMBER_OF_LENSES
			+ " trace-level multiplier " + MAX_TRACE_LEVEL_MULTIPLIER
			+ " lens edges " + (SHOW_LENS_EDGES?"on":"off")
			+ (TEST?" (test)":"")
			+ (MOVIE?(new DecimalFormat("000000")).format(frame):"")	// the number of the frame, converted into a string
		    +".bmp";	// the extension
	}
	
	private static void addTrajectory(
			String name,
			double initialAngleWithCircularTrajectory,
			DoubleColour colour,
			double radius,
			boolean alsoLauchInOppositeDirection,
			int maxTraceLevel,
			SceneObjectContainer scene,
			Studio studio
		)
	{
		Vector3D direction = new Vector3D(Math.cos(initialAngleWithCircularTrajectory), 0, Math.sin(initialAngleWithCircularTrajectory));
		
		SurfaceColourLightSourceIndependent s = new SurfaceColourLightSourceIndependent(colour, true);
		
		// a ray trajectory in the positive direction...
		scene.addSceneObject(new EditableRayTrajectory(
				name + " positive direction",
				new Vector3D(0, 0, 1),	// start point
				0,	// start time
				direction,	// initial direction
				radius,	// radius
				s,
				maxTraceLevel,	// max trace level
				false,	// reportToConsole
				scene,
				studio
				)
				);

		if(alsoLauchInOppositeDirection)
		// ... and one in the negative direction
		scene.addSceneObject(new EditableRayTrajectory(
				name + " negative direction",
				new Vector3D(0, 0, 1),	// start point
				0,	// start time
				direction.getReverse(),	// initial direction
				radius,	// radius
				s,
				maxTraceLevel,	// max trace level
				false,	// reportToConsole
				scene,
				studio
				)
				);
	}
	
	private static void addTrajectories(int frameNumber, int numberOfLenses, SceneObjectContainer scene, Studio studio)
	{
		double radius = 0.025;
				
		int maxTraceLevel = (int)Math.round(
				(MOVIE?(frameNumber / (getNoOfFrames()-1.0)):1) 
				* MAX_TRACE_LEVEL_MULTIPLIER*numberOfLenses
			);
		
		// circular trajectory
		if(SHOW_CIRCLE_TRAJECTORY)
		addTrajectory(
				"circle",	// name
				0,	// initial direction
				new DoubleColour(1, 0, 0),	// colour
				radius,
				false,	// alsoLauchInOppositeDirection
				maxTraceLevel,
				scene,
				studio
			);

		// elliptic trajectory
		if(SHOW_ELLIPTIC_TRAJECTORY)
		addTrajectory(
				"elliptic",	// name
				MyMath.deg2rad(0.5*45),	// initial direction
				new DoubleColour(1, 0, 0),	// colour
				radius,
				false,	// alsoLauchInOppositeDirection
				maxTraceLevel,
				scene,
				studio
			);
		
		// parabolic trajectory
		if(SHOW_PARABOLIC_TRAJECTORY)
		addTrajectory(
				"parabola",	// name
				MyMath.deg2rad(45),	// initial direction
				new DoubleColour(1, 0, 0),	// colour
				radius,
				true,	// alsoLauchInOppositeDirection
				maxTraceLevel,
				scene,
				studio
			);

		// hyperbolic trajectories
		if(SHOW_HYPERBOLIC_TRAJECTORY)
		addTrajectory(
				"hyperbola",	// name
				MyMath.deg2rad(1.5*45),	// initial direction
				new DoubleColour(1, 0, 0),	// colour
				radius,
				true,	// alsoLauchInOppositeDirection
				maxTraceLevel,
				scene,
				studio
			);

	}
	
	public static EditableLensStar getLensStar(int numberOfLenses, SceneObject parent, Studio studio)
	{
		return new EditableLensStar(
				"Lens star",	// description
				numberOfLenses,	// numberOfLenses
				true,	// show all lenses
				-1,	// number of shown lenses; -1 for all
				0,	// start index of shown lenses
				EditableLensStar.getFocalLengthForRadiusOfRegularPolygonTrajectory(
						numberOfLenses,	// numberOfLenses
						1	// radiusOfRegularPolygonTrajectory
					),	// focalLength
				PRINCIPAL_POINT_DISTANCE,	// principalPointDistance
				new Vector3D(0, 0, 0),	// centre
				new Vector3D(0, 1, 0),	// intersectionDirection
				(Math.floorMod(numberOfLenses,2)==1)?new Vector3D(0,0,-1):new Vector3D(1, 0, 0),	// pointOnLens1
				MyMath.HUGE,	// starRadius,
				2,	// starLength,
				0.7,	// lensTransmissionCoefficient
				SHOW_LENS_EDGES,	// showEdges,
				0.005,	// edgeRadius
				SurfaceColour.GREY50_MATT,	// edgeSurfaceProperty
				parent,
				studio
			);
	}
	
	/**
	 * Define scene, lights, and/or camera.
	 * @return a studio, i.e. scene, lights and camera
	 */
	public static Studio createStudio(int frame)
	{
		Studio studio = new Studio();

		// the scene
		SceneObjectContainer scene = new SceneObjectContainer("the scene", null, studio);
		studio.setScene(scene);

		// the standard scene objects
		scene.addSceneObject(SceneObjectClass.getHeavenSphere(scene, studio));
		// scene.addSceneObject(SceneObjectClass.getChequerboardFloor(scene, studio));
				
		// add any other scene objects
		
		// the lens star
		EditableLensStar lensStar = getLensStar(NUMBER_OF_LENSES, scene, studio);
		scene.addSceneObject(lensStar);
		
		SceneObjectContainer trajectories = new SceneObjectContainer("ray trajectories", scene, studio); 
		addTrajectories(frame, NUMBER_OF_LENSES, trajectories, studio);
		scene.addSceneObject(trajectories);
		
		// trace the rays with trajectory through the scene
		studio.traceRaysWithTrajectory();

//		// make the lenses effectively plane bits of glass
//		lensStar.setFocalLength(MyMath.HUGE);
//		lensStar.populateSceneObjectCollection();

		// remove the lens star and the previously calculated trajectories so that ray tracing through the second lens star works okay
		scene.removeSceneObject(lensStar);
		scene.removeSceneObject(trajectories);
		
		scene.addSceneObject(trajectories);
		
		// as we removed the lens star earlier, add it here again, if necessary
		if(SHOW_LENS_EDGES) scene.addSceneObject(lensStar);

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

		
//		SceneObject focusScene = new Plane(
//				"focussing plane",
//				new Vector3D(0, 0, 10),	// point on plane
//				new Vector3D(0, 0, 1),	// normal to plane
//				(SurfaceProperty)null,
//				null,	// parent
//				Studio.NULL_STUDIO
//		);
//		
//		EditableRelativisticAnyFocusSurfaceCamera camera = new EditableRelativisticAnyFocusSurfaceCamera(
//				"Camera",
//				new Vector3D(2, 3, 0),	// centre of aperture
//				new Vector3D(-.2, -.3, 1),	// viewDirection
//				new Vector3D(0, 1, 0),	// top direction vector
//				20,	// horiontalViewAngle in degrees; 2*MyMath.rad2deg(Math.atan(2./10.)) gives same view angle as in previous version
//				new Vector3D(0, 0, 0),	// beta
//				pixelsX*antiAliasingFactor, pixelsY*antiAliasingFactor,	// logical number of pixels
//				ExposureCompensationType.EC0,	// exposure compensation +0
//				100,	// maxTraceLevel
//				focusScene,
//				null,	// cameraFrameScene,
//				ApertureSizeType.PINHOLE,	// aperture size
//				QualityType.RUBBISH,	// blur quality
//				QualityType.NORMAL	// anti-aliasing quality
//		);
		
		double width = 10;
		
		EditableOrthographicCamera camera = new EditableOrthographicCamera(
				"Camera",	// name
				new Vector3D(0, -1, 0),	// viewDirection
				new Vector3D(0, 1000, 0),	// CCDCentre
				new Vector3D(0, 0, width),	// horizontalSpanVector3D
				new Vector3D(width*pixelsY/pixelsX, 0, 0),	// verticalSpanVector3D
				pixelsX,	// imagePixelsHorizontal
				pixelsY,	// imagePixelsVertical
				100,	// maxTraceLevel
				TEST?QualityType.BAD:QualityType.GOOD	// antiAliasingQuality	
			);

		studio.setLights(LightSource.getStandardLightsFromBehind());
		studio.setCamera(camera);

		return studio;
	}

	/**
	 * This method gets called when the Java application starts.
	 * 
	 * @see createStudio
	 * @author	Johannes Courtial
	 */
	public static void main(final String[] args)
	{
		// open a window, and take a note of its content pane
		Container container = (new PhotoFrame()).getContentPane();
		
		for(int frame = (MOVIE?0:getNoOfFrames()-1); frame < getNoOfFrames(); frame++)
		{
			// define scene, lights and camera
			Studio studio = createStudio(frame);

			// do the ray tracing
			studio.takePhoto();

			// save the image
			studio.savePhoto(getFilename(frame), "bmp");

			// display the image on the screen
			container.removeAll();
			container.add(new PhotoCanvas(studio.getPhoto()));
			container.validate();
		}
	}
}
