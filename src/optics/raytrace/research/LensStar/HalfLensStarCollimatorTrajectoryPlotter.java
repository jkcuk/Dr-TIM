package optics.raytrace.research.LensStar;

import java.awt.*;

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
 * Plots a number of trajectories that demonstrate collimation by half a lens star.
 * The number of lenses in the full lens star has to be an integer multiple of 4.
 * The point light source is being placed symmetrically on the regular-(half)-polygon trajectory.
 * 
 * @author Johannes Courtial
 */
public class HalfLensStarCollimatorTrajectoryPlotter
{
	public static final int NUMBER_OF_LENSES = 60;	// works only for multiples of four
	public static final boolean SHOW_LENS_EDGES = true;
	public static final int NUMBER_OF_RAYS = 30;

	/**
	 * Filename under which main saves the rendered image.
	 * Alter to save the rendered image under a different name.
	 * @return	the filename under which main saves the rendered image.
	 */
	public static String getFilename()
	{
		return "HalfLensStarCollimatorTrajectory"	// the name
			+ " number of lenses "+NUMBER_OF_LENSES
			+ " lens edges " + (SHOW_LENS_EDGES?"on":"off")
			+ " number of rays "+NUMBER_OF_RAYS
		    +".bmp";	// the extension
	}
	
	private static void addTrajectory(
			String name,
			double initialAngleWithCircularTrajectory,
			DoubleColour colour,
			SceneObjectContainer scene,
			Studio studio
		)
	{
		Vector3D direction = new Vector3D(Math.cos(initialAngleWithCircularTrajectory), 0, Math.sin(initialAngleWithCircularTrajectory));
		
		// a ray trajectory in the positive direction...
		scene.addSceneObject(new EditableRayTrajectory(
				name,
				new Vector3D(0, 0, 1),	// start point
				0,	// start time
				direction,	// initial direction
				0.02,	// radius
				new SurfaceColourLightSourceIndependent(colour, true),
				2*NUMBER_OF_LENSES,	// max trace level
				false,	// reportToConsole
				scene,
				studio
				)
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

		// the standard scene objects
		// scene.addSceneObject(SceneObjectClass.getSkySphere(scene, studio));
		scene.addSceneObject(SceneObjectClass.getHeavenSphere(scene, studio));
		// scene.addSceneObject(SceneObjectClass.getChequerboardFloor(scene, studio));
				
		// add any other scene objects
		
		// the lens star
		double azimuthalAngleOfLens1 = (Math.floorMod(NUMBER_OF_LENSES,2)==1)?0:Math.PI/NUMBER_OF_LENSES;
		EditableLensStar lensStar = new EditableLensStar(
				"Half lens star",	// description
				NUMBER_OF_LENSES,	// numberOfLenses
				false,	// show all lenses
				NUMBER_OF_LENSES / 2,	// number of shown lenses; -1 for all
				NUMBER_OF_LENSES / 4,	// start index of shown lenses
				EditableLensStar.getFocalLengthForRadiusOfRegularPolygonTrajectory(
						NUMBER_OF_LENSES,	// numberOfLenses
						1	// radiusOfRegularPolygonTrajectory
					),	// focalLength
				0,	// principalPointDistance
				new Vector3D(0, 0, 0),	// centre
				new Vector3D(0, 1, 0),	// intersectionDirection
				new Vector3D(-Math.sin(azimuthalAngleOfLens1), 0, -Math.cos(azimuthalAngleOfLens1)),	// pointOnLens1
				// (Math.floorMod(NUMBER_OF_LENSES,2)==1)?new Vector3D(0,0,-1):new Vector3D(1, 0, 0),	// pointOnLens1
				MyMath.HUGE,	// starRadius,
				2,	// starLength,
				0.7,	// lensTransmissionCoefficient
				SHOW_LENS_EDGES,	// showEdges,
				0.005,	// edgeRadius
				SurfaceColour.GREY50_MATT,	// edgeSurfaceProperty
				scene,	// parent,
				studio
			);
		scene.addSceneObject(lensStar);
		
		// a bunch of trajectories
		for(int i=0; i<NUMBER_OF_RAYS; i++)
		{
		addTrajectory(
				"Trajectory #"+i,	// name
				2.*Math.PI*i/NUMBER_OF_RAYS,	// initial direction
				new DoubleColour(1, 0, 0),	// colour
				scene,
				studio
			);
		}

		studio.setScene(scene);

		// trace the rays with trajectory through the scene
		studio.traceRaysWithTrajectory();

		// scene.removeSceneObject(starOfLenses);
		
		// make the lenses effectively plane bits of glass
		lensStar.setFocalLength(MyMath.HUGE);
		lensStar.populateSceneObjectCollection();


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
				QualityType.GOOD	// antiAliasingQuality	
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
