package optics.raytrace.research.curvedSpaceSimulation;

import math.*;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.surfaces.RayFlipping;
import optics.raytrace.surfaces.RayRotating;
import optics.raytrace.surfaces.RayRotatingAboutArbitraryAxisDirection;
import optics.raytrace.surfaces.SurfaceColour;
import optics.raytrace.surfaces.SurfaceColourLightSourceIndependent;
import optics.raytrace.utility.CoordinateSystems.GlobalOrLocalCoordinateSystemType;
import optics.raytrace.core.*;
import optics.raytrace.exceptions.SceneException;
import optics.DoubleColour;
import optics.raytrace.NonInteractiveTIMActionEnum;
import optics.raytrace.NonInteractiveTIMEngine;
import optics.raytrace.GUI.cameras.EditableOrthographicCameraTop;
import optics.raytrace.GUI.cameras.RenderQualityEnum;
import optics.raytrace.GUI.lowLevel.ApertureSizeType;
import optics.raytrace.GUI.sceneObjects.EditableRayTrajectory;
import optics.raytrace.GUI.sceneObjects.EditableCylinderLattice;
import optics.raytrace.GUI.sceneObjects.EditableFramedRectangle;
import optics.raytrace.GUI.sceneObjects.EditableParametrisedCylinder;


/**
 * Two sheets, each performing negative refraction and at an angle alpha/2 relative to each other, correspond to a curved space with a deficit angle alpha.
 * 
 * @author Johannes Courtial
 */
public class NegativeRefractionWedge extends NonInteractiveTIMEngine
{
	// additional parameters
	
	/**
	 * Deficit angle of the wedge, in degrees
	 */
	protected double deficitAngle;
	
	/**
	 * If true, both sheets perform negative refraction only in one direction -- they flip only one of the transverse components of light-ray direction, i.e. they are ray-flipping sheets.
	 * (These can be approximated with Dove-prism sheets.)
	 * If false, both sheets perform proper negative refraction, i.e. they flip both transverse components of light-ray direction.
	 */
	protected boolean rayFlippingSheets;
	
	/**
	 * If true, replace the wedge with a single RR sheet that rotated by the deficit angle.
	 */
	protected boolean replaceWedgeWithRRSheet;
	
	/**
	 * If true, show frames around each surface, otherwise don't
	 */
	protected boolean showSurfaceFrames;
	
	/**
	 * If true, shows an orthographic projection from above ("ceiling projection")
	 */
	protected boolean ceilingProjection;
	
	/**
	 * Constructor.
	 * Sets all parameters.
	 */
	public NegativeRefractionWedge()
	{
		super();
		
		// set all parameters
		deficitAngle = 10.;
		replaceWedgeWithRRSheet = false;
		rayFlippingSheets = false;
		showSurfaceFrames = true;
		ceilingProjection = true;
		
		renderQuality = RenderQualityEnum.DRAFT;
		
		// boring parameters
		nonInteractiveTIMAction = NonInteractiveTIMActionEnum.INTERACTIVE;

		numberOfFrames = 50;
		firstFrame = 0;
		lastFrame = numberOfFrames-1;
		
		// camera parameters are set in populateStudio() method
	}

	@Override
	public String getClassName()
	{
		return "NegativeRefractionWedge"	// the name
				+ " deficit angle "+deficitAngle+"°"
				+ (replaceWedgeWithRRSheet?" (approximated by RR sheet)":"")
				+ (((!replaceWedgeWithRRSheet)&&rayFlippingSheets)?" (ray-flipping)":"")
				+ (showSurfaceFrames?" (surface frames shown)":"")
				+ " view="+(ceilingProjection?"ceiling":"observer")
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
	
	@SuppressWarnings("unused")
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
		SurfaceProperty frameSurfaceProperty = SurfaceColour.GREY30_MATT;
		
		double windowWidth = 1.;
		double frameRadius = 0.01;
		
		if(replaceWedgeWithRRSheet)
		{
			// RR sheet
			scene.addSceneObject(
					new EditableFramedRectangle(
							"RR sheet",	// description
							new Vector3D(0, 0, 10),	// corner
							new Vector3D(windowWidth*Math.cos(0), 0, -windowWidth*Math.sin(0)),	// widthVector
							new Vector3D(0, 1, 0),	// heightVector
							frameRadius,	// radius
							new RayRotatingAboutArbitraryAxisDirection(
									MyMath.deg2rad(-deficitAngle),	// outwards rotation angle
									new Vector3D(0, 1, 0),	// rotationAxisUnitVector
									GlobalOrLocalCoordinateSystemType.GLOBAL_BASIS,	// basis
									0.96,	// transmissionCoefficient
									true	// shadowThrowing
								),	// windowSurfaceProperty
							frameSurfaceProperty,	// frameSurfaceProperty
							showSurfaceFrames,	// showFrames
							scene,	// parent
							studio
					)
				);
		}
		else
		{
			// the negative-refraction wedge
			SurfaceProperty wedgeSurfaces = (rayFlippingSheets?
					new RayFlipping(
							0,	// flipAxisAngle
							0.96,	// transmissionCoefficient
							true	// shadowThrowing
						):
					new RayRotating(
							Math.PI,	// rotation angle
							0.96,	// transmissionCoefficient
							true	// shadowThrowing
					)
				);
			
			// half the deficit angle, in radians
			double delta2 = MyMath.deg2rad(deficitAngle/2.);
			
			// sheet 1
			scene.addSceneObject(
					new EditableFramedRectangle(
							"Wedge front surface",	// description
							new Vector3D(0, 0, 10),	// corner
							new Vector3D(windowWidth*Math.cos(delta2), 0, -windowWidth*Math.sin(delta2)),	// spanVector2
							new Vector3D(0, 1, 0),	// spanVector1
							frameRadius,	// frame radius
							wedgeSurfaces,	// window surface property
							frameSurfaceProperty,	// frame surface property
							showSurfaceFrames,	// showFrames
							scene,	// parent
							studio
							)
					);

			// sheet 2
			scene.addSceneObject(
					new EditableFramedRectangle(
							"Wedge back surface",	// description
							new Vector3D(0, 0, 10),	// corner
							new Vector3D(windowWidth*Math.cos(0), 0, -windowWidth*Math.sin(0)),	// spanVector2
							new Vector3D(0, 1, 0),	// spanVector1
							frameRadius,	// frame radius
							wedgeSurfaces,	// window surface property
							frameSurfaceProperty,	// frame surface property
							showSurfaceFrames,	// showFrames
							scene,	// parent
							studio
							)
					);
		}
		
		// rotation axis
		scene.addSceneObject(new EditableParametrisedCylinder(
				"rotation axis, i.e. epsilon-cone's bone",	// description
				new Vector3D(0, -1, 10),	// startPoint
				new Vector3D(0, 1, 10),	// endPoint
				1.2*frameRadius,	// radius
				SurfaceColour.DARK_RED_SHINY,	// surfaceProperty
				scene,	// parent
				studio
		));
		
		// the cubes
		scene.addSceneObject(new EditableCylinderLattice(
				"cylinder cube, seen through wedge",	// description
				0.2, 0.8, 2,	// xMin, xMax, nX
				0.2, 0.8, 2,	// yMin, yMax, nY
				10.2, 10.8, 2,	// zMin, zMax, nZ
				0.01,	// radius
				scene,	// parent
				studio
		));

		scene.addSceneObject(new EditableCylinderLattice(
				"cylinder cube, seen directly",	// description
				0.2, 0.8, 2,	// xMin, xMax, nX
				-0.8, -0.2, 2,	// yMin, yMax, nY
				10.2, 10.8, 2,	// zMin, zMax, nZ
				0.01,	// radius
				scene,	// parent
				studio
		));
		
		// define the camera
		//
		// Note that the view direction and basis Vector3Ds of the detector are chosen such that
		// the x, y, z axes form a LEFT-handed coordinate system.
		// The reason is that, in the photo, the positive x direction is then to the right,
		// the positive y direction is upwards, and the camera looks in the positive z direction.
		cameraPixelsX = 640;
		cameraPixelsY = 480;
		
		CameraClass camera;
		
		if(ceilingProjection)
		{
			SceneObjectContainer trajectories = new SceneObjectContainer("ray trajectories", scene, studio); 
			// addTrajectories(numberOfRRSheets, trajectories, studio);
			scene.addSceneObject(trajectories);

			// trace the rays with trajectory through the scene
			studio.traceRaysWithTrajectory();

			// the camera
			camera = new EditableOrthographicCameraTop(
					"Ceiling view",
					0,	// xCentre
					10,	// zCentre
					2.5,	// zLength
					cameraPixelsX, cameraPixelsY,	// logical number of pixels
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
			// camera parameters; these are often set (or altered) in createStudio()
			cameraViewDirection = new Vector3D(0, 0, 1);
			cameraViewCentre = new Vector3D(0, 0, 10);
			cameraDistance = 10;
			cameraFocussingDistance = 10;
			cameraHorizontalFOVDeg = 20;
			cameraMaxTraceLevel = 100;
			cameraApertureSize = ApertureSizeType.PINHOLE;

			camera = getStandardCamera();
		}

		studio.setLights(LightSource.getStandardLightsFromBehind());
		studio.setCamera(camera);
	}

	public static void main(final String[] args)
	{
		(new NegativeRefractionWedge()).run();
	}
}
