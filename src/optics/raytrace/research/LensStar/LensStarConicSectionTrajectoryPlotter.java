package optics.raytrace.research.LensStar;

import java.awt.*;

import math.*;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.surfaces.Striped;
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
public class LensStarConicSectionTrajectoryPlotter
{
	public static final boolean SHOW_CIRCLE_TRAJECTORY = true;
	public static final boolean SHOW_ELLIPTIC_TRAJECTORY = true;
	public static final boolean SHOW_PARABOLIC_TRAJECTORY = true;
	public static final boolean SHOW_HYPERBOLIC_TRAJECTORY = true;

	// Lens star 1 / 2
	public static final int[] NUMBER_OF_LENSES = {5, 499};
	public static final boolean[] SHOW_LENS_EDGES = {true, false};
	public static final boolean[] STRIPED_TRAJECTORIES = {true, false};
	public static final boolean[] SHOW_TRAJECTORIES = {true, true};
	
	// camera
	public static final double IMAGE_WIDTH = 10;
	public static final double IMAGE_SHIFT_V = 2;

	/**
	 * Filename under which main saves the rendered image.
	 * Alter to save the rendered image under a different name.
	 * @return	the filename under which main saves the rendered image.
	 */
	public static String getFilename()
	{
		String name = 
				"LensStarTrajectory "	// the name
			+ (SHOW_CIRCLE_TRAJECTORY?"C":"")
			+ (SHOW_ELLIPTIC_TRAJECTORY?"E":"")
			+ (SHOW_PARABOLIC_TRAJECTORY?"P":"")
			+ (SHOW_HYPERBOLIC_TRAJECTORY?"H":"")
			+ (SHOW_TRAJECTORIES[0]?" #L="+NUMBER_OF_LENSES[0] + " E=" + (SHOW_LENS_EDGES[0]?"on":"off"):"")
			+ (SHOW_TRAJECTORIES[1]?" #L="+NUMBER_OF_LENSES[1] + " E=" + (SHOW_LENS_EDGES[1]?"on":"off"):"")
			+ " w="+IMAGE_WIDTH
			+ " sv="+IMAGE_SHIFT_V
		    +".bmp";	// the extension
		return name;
	}
	
	private static void addTrajectory(
			String name,
			double initialAngleWithCircularTrajectory,
			DoubleColour colour,
			boolean striped,
			double radius,
			boolean alsoLauchInOppositeDirection,
			int maxTraceLevel,
			SceneObjectContainer scene,
			Studio studio
		)
	{
		Vector3D direction = new Vector3D(Math.cos(initialAngleWithCircularTrajectory), 0, Math.sin(initialAngleWithCircularTrajectory));
		
		SurfaceColourLightSourceIndependent c = new SurfaceColourLightSourceIndependent(colour, true);
		SurfaceProperty s = (striped?new Striped(c, SurfaceColourLightSourceIndependent.WHITE, .1*IMAGE_WIDTH/10.):c);
		
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
	
	private static void addTrajectories(boolean striped, int numberOfLenses, SceneObjectContainer scene, Studio studio)
	{
		double radius = 0.025*IMAGE_WIDTH/10.;
		
		// circular trajectory
		if(SHOW_CIRCLE_TRAJECTORY)
		addTrajectory(
				"circle",	// name
				0,	// initial direction
				new DoubleColour(0, 0, 1),	// colour
				striped,
				radius,
				false,	// alsoLauchInOppositeDirection
				2*numberOfLenses,
				scene,
				studio
			);

		// elliptic trajectory
		if(SHOW_ELLIPTIC_TRAJECTORY)
		addTrajectory(
				"elliptic",	// name
				MyMath.deg2rad(0.5*45),	// initial direction
				new DoubleColour(0.33, 0, 0.66),	// colour
				striped,
				radius,
				false,	// alsoLauchInOppositeDirection
				2*numberOfLenses,
				scene,
				studio
			);
		
		// parabolic trajectory
		if(SHOW_PARABOLIC_TRAJECTORY)
		addTrajectory(
				"parabola",	// name
				MyMath.deg2rad(45),	// initial direction
				new DoubleColour(0.66, 0, 0.33),	// colour
				striped,
				radius,
				true,	// alsoLauchInOppositeDirection
				2*numberOfLenses,
				scene,
				studio
			);

		// hyperbolic trajectories
		if(SHOW_HYPERBOLIC_TRAJECTORY)
		addTrajectory(
				"hyperbola",	// name
				MyMath.deg2rad(1.5*45),	// initial direction
				new DoubleColour(1, 0, 0),	// colour
				striped,
				radius,
				true,	// alsoLauchInOppositeDirection
				2*numberOfLenses,
				scene,
				studio
			);

	}
	
	public static EditableLensStar getLensStar(int numberOfLenses, boolean showEdges, SceneObject parent, Studio studio)
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
				0,	// principalPointDistance
				new Vector3D(0, 0, 0),	// centre
				new Vector3D(0, 1, 0),	// intersectionDirection
				(Math.floorMod(numberOfLenses,2)==1)?new Vector3D(0,0,-1):new Vector3D(1, 0, 0),	// pointOnLens1
				MyMath.HUGE,	// starRadius,
				2,	// starLength,
				0.7,	// lensTransmissionCoefficient
				showEdges,	// showEdges,
				0.005*IMAGE_WIDTH/10.,	// edgeRadius
				SurfaceColour.GREY50_MATT,	// edgeSurfaceProperty
				parent,
				studio
			);
	}
	
	/**
	 * Define scene, lights, and/or camera.
	 * @return a studio, i.e. scene, lights and camera
	 */
	public static Studio createStudio()
	{
		Studio studio = new Studio();

		// the scene
		SceneObjectContainer scene = new SceneObjectContainer("the scene", null, studio);
		studio.setScene(scene);

		// the standard scene objects
		scene.addSceneObject(SceneObjectClass.getHeavenSphere(scene, studio));
		// scene.addSceneObject(SceneObjectClass.getChequerboardFloor(scene, studio));
				
		// add any other scene objects
		
		// Tim's head, behind the cloak
//		scene.addSceneObject(new EditableTimHead(
//				"Tim's head",
//				new Vector3D(0, 0, 15),
//				scene,
//				studio
//			));
		
		// first, trace the trajectories through one lens star at a time
		
		// create a space where all the trajectories go
		SceneObjectContainer trajectories = new SceneObjectContainer("ray trajectories", scene, studio); ;
		
		for(int i=0; i<SHOW_TRAJECTORIES.length; i++)
		{
			// create the trajectories for lens star i		
			if(SHOW_TRAJECTORIES[i])
			{
				// add the lens star, without the edges
				EditableLensStar lensStar = getLensStar(NUMBER_OF_LENSES[i], false, scene, studio);
				scene.addSceneObject(lensStar);

				// set the trajectory starting points
				SceneObjectContainer trajectoriesOfCurrentLensStar = new SceneObjectContainer("ray trajectories for lens star "+i, trajectories, studio); 
				addTrajectories(STRIPED_TRAJECTORIES[i], NUMBER_OF_LENSES[i], trajectoriesOfCurrentLensStar, studio);
				scene.addSceneObject(trajectoriesOfCurrentLensStar);

				// trace the rays with trajectory through the lens star
				studio.traceRaysWithTrajectory();
				
				// add the trajectories of the current lens star to the trajectories
				trajectories.addSceneObject(trajectoriesOfCurrentLensStar);

				// remove the lens star and the previously calculated trajectories so that ray tracing through the second lens star works okay
				scene.removeSceneObject(lensStar);
				scene.removeSceneObject(trajectoriesOfCurrentLensStar);
			}
		}

		scene.addSceneObject(trajectories);
		
		// add the lens star edges again
		for(int i=0; i<SHOW_TRAJECTORIES.length; i++)
		{
			// add the lens star
			EditableLensStar lensStar = getLensStar(NUMBER_OF_LENSES[i], SHOW_LENS_EDGES[i], scene, studio);
			scene.addSceneObject(lensStar);
		}


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
				
		EditableOrthographicCamera camera = new EditableOrthographicCamera(
				"Camera",	// name
				new Vector3D(0, -1, 0),	// viewDirection
				new Vector3D(IMAGE_SHIFT_V, 1000, 0),	// CCDCentre
				new Vector3D(0, 0, IMAGE_WIDTH),	// horizontalSpanVector3D
				new Vector3D(IMAGE_WIDTH*pixelsY/pixelsX, 0, 0),	// verticalSpanVector3D
				pixelsX,	// imagePixelsHorizontal
				pixelsY,	// imagePixelsVertical
				100,	// maxTraceLevel
				QualityType.GREAT	// antiAliasingQuality	
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
		
		// define scene, lights and camera
		Studio studio = createStudio();

		// do the ray tracing
		studio.takePhoto();

		// save the image
		studio.savePhoto(getFilename(), "bmp");

		// display the image on the screen
		container.removeAll();
		container.add(new PhotoCanvas(studio.getPhoto()));
		container.validate();
	}
}
