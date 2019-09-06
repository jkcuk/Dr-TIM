package optics.raytrace.research.curvedSpaceSimulation;

import math.*;
import optics.raytrace.sceneObjects.Plane;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.surfaces.SurfaceColour;
import optics.raytrace.surfaces.SurfaceColourLightSourceIndependent;
import optics.raytrace.core.*;
import optics.raytrace.exceptions.SceneException;
import optics.DoubleColour;
import optics.raytrace.NonInteractiveTIMActionEnum;
import optics.raytrace.NonInteractiveTIMEngine;
import optics.raytrace.GUI.cameras.EditableOrthographicCameraTop;
import optics.raytrace.GUI.cameras.EditableRelativisticAnyFocusSurfaceCamera;
import optics.raytrace.GUI.cameras.QualityType;
import optics.raytrace.GUI.cameras.RenderQualityEnum;
import optics.raytrace.GUI.lowLevel.ApertureSizeType;
import optics.raytrace.GUI.sceneObjects.EditableRayTrajectory;
import optics.raytrace.GUI.sceneObjects.EditableSceneObjectCollection;
import optics.raytrace.cameras.PinholeCamera.ExposureCompensationType;
import optics.raytrace.GUI.sceneObjects.EditableCylinderLattice;
import optics.raytrace.GUI.sceneObjects.EditableRayRotationSheetStar;


/**
 * Simulation of a cube inside a ray-rotation-sheet star.
 * The cube is seen in light that has performed 0, 1, 2, ... round trips through the "bone" of the RR-sheet star.
 * It is also possible to show the orthographic projection from above, with the light-ray trajectory from the cube to the observer.
 * 
 * @author Johannes Courtial
 */
public class CubeInRayRotationSheetStar extends NonInteractiveTIMEngine
{
	// additional parameters
	
	/**
	 * Number of RR sheets in star
	 */
	protected int numberOfRRSheets;
	
	/**
	 * Deficit angle of star, in degrees
	 */
	protected double deficitAngle;
	
	/**
	 * If true, show edges of RR sheets in star, otherwise don't
	 */
	protected boolean showRRSheetEdges;
	
	/**
	 * If true, shows an orthographic projection from above ("ceiling projection")
	 */
	protected boolean ceilingProjection;
	
	/**
	 * Constructor.
	 * Sets all parameters.
	 */
	public CubeInRayRotationSheetStar()
	{
		super();
		
		// set all parameters
		numberOfRRSheets = 8;
		deficitAngle = 360.-8;
		showRRSheetEdges = true;
		ceilingProjection = false;
		
		renderQuality = RenderQualityEnum.DRAFT;
		
		// boring parameters
		nonInteractiveTIMAction = NonInteractiveTIMActionEnum.INTERACTIVE;

		numberOfFrames = 50;
		firstFrame = 0;
		lastFrame = numberOfFrames-1;
		
		// camera parameters are set in populateStudio() method
	}

	@Override
	public String getFirstPartOfFilename()
	{
		return "CubeInRRSStar "	// the name
				+ " deficit angle "+deficitAngle+"°"
				+ " number of RR sheets "+numberOfRRSheets
				+ " sheet edges " + (showRRSheetEdges?"on":"off")
				+ (ceilingProjection?" ceiling projection ":" observer view ")
				;
	}
	
	private void addTrajectory(
			String name,
			double initialAngle,	// in radians
			DoubleColour colour,
			double radius,
			boolean alsoLauchInOppositeDirection,
			int maxTraceLevel,
			SceneObjectContainer scene,
			Studio studio
		)
	{
		Vector3D direction = new Vector3D(Math.cos(initialAngle), 0, Math.sin(initialAngle));
		
		SurfaceColourLightSourceIndependent c = new SurfaceColourLightSourceIndependent(colour, true);
		
		// a ray trajectory in the positive direction...
		scene.addSceneObject(new EditableRayTrajectory(
				name + " positive direction",
				new Vector3D(1, 0, 0.01),	// start point
				0,	// start time
				direction,	// initial direction
				radius,	// radius
				c,
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
				c,
				maxTraceLevel,	// max trace level
				false,	// reportToConsole
				scene,
				studio
				)
				);
	}
	
	private void addTrajectories(int numberOfRRSheets, SceneObjectContainer scene, Studio studio)
	{
		double radius = 0.025;
		
		addTrajectory(
				"trajectory",	// name
				MyMath.deg2rad(90+180./numberOfRRSheets),	// initial direction
				new DoubleColour(0, 1, 0),	// colour
				radius,
				false,	// alsoLauchInOppositeDirection
				10*numberOfRRSheets,
				scene,
				studio
			);
	}
	
	public EditableRayRotationSheetStar getRRSheetStar(int numberOfRayRotationSheets, double rayRotationAngle, SceneObject parent, Studio studio)
	{
		return new EditableRayRotationSheetStar(
				"Ray-rotation-sheet star",	// description
				numberOfRayRotationSheets,	// numberOfRayRotationSheets
				true,	// showAllRayRotationSheets
				-1,	// numberOfShownRayRotationSheets; -1 for all
				0,	// shownRayRotationSheetsStartIndex
				MyMath.deg2rad(rayRotationAngle),	// rotationAngle
				new Vector3D(0, 1, 0),	// rotationAxisDirection
				new Vector3D(0, 0, 0),	// centre
				new Vector3D(0, 1, 0),	// intersectionDirection
				(Math.floorMod(numberOfRayRotationSheets,2)==1)?new Vector3D(0,0,-1):new Vector3D(1, 0, 0),	// pointOnRayRotationSheet1
				4,	// starRadius,
				1,	// starLength,
				0.7,	// rayRotationSheetTransmissionCoefficient
				showRRSheetEdges,	// showEdges,
				0.01,	// edgeRadius
				SurfaceColour.GREY50_MATT,	// edgeSurfaceProperty
				parent,
				studio
			);
	}
	
	@Override
	public void populateStudio()
	throws SceneException
	{
		// super.populateSimpleStudio();

		studio = new Studio();

		// the scene
		SceneObjectContainer scene = new SceneObjectContainer("the scene", null, studio);
		studio.setScene(scene);

		// the standard scene objects
		// scene.addSceneObject(SceneObjectClass.getHeavenSphere(scene, studio));
		scene.addSceneObject(SceneObjectClass.getSkySphere(scene, studio));
		scene.addSceneObject(SceneObjectClass.getLighterChequerboardFloor(scene, studio));
				
		// add any other scene objects
		
		// the RR sheet star
		EditableSceneObjectCollection rrSheetStar = getRRSheetStar(
				numberOfRRSheets,
				deficitAngle/numberOfRRSheets,
				scene, studio);
		scene.addSceneObject(rrSheetStar);
		
		// the cube
		EditableCylinderLattice cube = new EditableCylinderLattice(
				"cylinder cube",	// description
				1.2, 1.4, 2,	// xMin, xMax, nX
				-0.1, 0.1, 2,	// yMin, yMax, nY
				0.7, 0.9, 2,	// zMin, zMax, nZ
				0.01,	// radius
				scene,	// parent
				studio
		);
		scene.addSceneObject(cube);

		
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

		
		
		CameraClass camera;
		
		if(ceilingProjection)
		{
			SceneObjectContainer trajectories = new SceneObjectContainer("ray trajectories", scene, studio); 
			addTrajectories(numberOfRRSheets, trajectories, studio);
			scene.addSceneObject(trajectories);

			// trace the rays with trajectory through the scene
			studio.traceRaysWithTrajectory();

			// the camera
			camera = new EditableOrthographicCameraTop(
					"Ceiling view",
					0,	// xCentre
					0,	// zCentre
					5,	// zLength
					pixelsX, pixelsY,	// logical number of pixels
					100,	// maxTraceLevel
					renderQuality.getAntiAliasingQuality()	// anti-aliasing quality
			);
//			camera = new EditableOrthographicCamera(
//					"Camera",	// name
//					new Vector3D(0, -1, 0),	// viewDirection
//					new Vector3D(0, 1000, 0),	// CCDCentre
//					new Vector3D(0, 0, width),	// horizontalSpanVector3D
//					new Vector3D(width*pixelsY/pixelsX, 0, 0),	// verticalSpanVector3D
//					pixelsX,	// imagePixelsHorizontal
//					pixelsY,	// imagePixelsVertical
//					100,	// maxTraceLevel
//					(test?QualityType.NORMAL:QualityType.GOOD)	// anti-aliasing quality
//					);
		}
		else
		{
			SceneObject focusScene = new Plane(
					"focussing plane",
					new Vector3D(0, 0, 10),	// point on plane
					new Vector3D(0, 0, 1),	// normal to plane
					(SurfaceProperty)null,
					null,	// parent
					Studio.NULL_STUDIO
					);

			camera = new EditableRelativisticAnyFocusSurfaceCamera(
					"Camera",
					new Vector3D(1, 0, 0.01),	// centre of aperture
					new Vector3D(0, 0, 1),	// viewDirection
					new Vector3D(0, 1, 0),	// top direction vector
					90,	// horiontalViewAngle in degrees; 2*MyMath.rad2deg(Math.atan(2./10.)) gives same view angle as in previous version
					new Vector3D(0, 0, 0),	// beta
					pixelsX, pixelsY,	// logical number of pixels
					ExposureCompensationType.EC0,	// exposure compensation +0
					100*numberOfRRSheets,	// maxTraceLevel
					focusScene,
					null,	// cameraFrameScene,
					ApertureSizeType.PINHOLE,	// aperture size
					QualityType.RUBBISH,	// blur quality
					renderQuality.getAntiAliasingQuality()	// anti-aliasing quality
				);
		}

		studio.setLights(LightSource.getStandardLightsFromBehind());
		studio.setCamera(camera);
	}

	public static void main(final String[] args)
	{
		(new CubeInRayRotationSheetStar()).run();
	}
}
