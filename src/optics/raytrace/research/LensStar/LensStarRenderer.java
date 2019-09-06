package optics.raytrace.research.LensStar;

import java.awt.*;

import math.*;
import optics.raytrace.sceneObjects.Plane;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.surfaces.SurfaceColour;
import optics.raytrace.core.*;
import optics.raytrace.lights.AmbientLight;
import optics.raytrace.lights.LightSourceContainer;
import optics.raytrace.lights.PhongLightSource;
import optics.DoubleColour;
import optics.raytrace.GUI.cameras.EditableRelativisticAnyFocusSurfaceCamera;
import optics.raytrace.GUI.cameras.QualityType;
import optics.raytrace.GUI.lowLevel.ApertureSizeType;
import optics.raytrace.GUI.nonInteractive.PhotoCanvas;
import optics.raytrace.GUI.nonInteractive.PhotoFrame;
import optics.raytrace.cameras.PinholeCamera.ExposureCompensationType;
import optics.raytrace.GUI.sceneObjects.EditableLensStar;
import optics.raytrace.GUI.sceneObjects.EditableScaledParametrisedSphere;


/**
 * Shows a lens star.
 * 
 * @author Johannes Courtial
 */
public class LensStarRenderer
{
	public enum LensStarRendererCameraPositionType
	{
		STANDARD("Standard view"),
		INSIDE("Inside view"),
		CENTRE_HEIGHT("Outside view, centre height"),
		TOP("Top view");
		
		private String description;
		private LensStarRendererCameraPositionType(String description) {this.description = description;}
		public String getDescription() {return description;}
	}
	
	public static final int NUMBER_OF_LENSES = 5;
	public static final boolean SHOW_LENS_EDGES = true;
	public static final boolean SHOW_SPHERE = true;
	public static final double SPHERE_DISTANCE = 1;
	public static final LensStarRendererCameraPositionType
		// CAMERA_POSITION = LensStarRendererCameraPositionType.INSIDE;
		// CAMERA_POSITION = LensStarRendererCameraPositionType.STANDARD;
		// CAMERA_POSITION = LensStarRendererCameraPositionType.CENTRE_HEIGHT;
		CAMERA_POSITION = LensStarRendererCameraPositionType.TOP;
	public static final boolean TEST = false;

	/**
	 * Filename under which main saves the rendered image.
	 * Alter to save the rendered image under a different name.
	 * @return	the filename under which main saves the rendered image.
	 */
	public static String getFilename()
	{
		return "LensStar"	// the name
			+ " number of lenses "+NUMBER_OF_LENSES
			+ " lens edges " + (SHOW_LENS_EDGES?"on":"off")
			+ (SHOW_SPHERE?" sphere distance "+SPHERE_DISTANCE:"")
			+ " ("+CAMERA_POSITION.getDescription()+")"
			+ (TEST?" (test)":"")
		    +".bmp";	// the extension
	}
	

	/**
	 * Define scene, lights, and/or camera.
	 * @return a studio, i.e. scene, lights and camera
	 */
	public static Studio createStudio()
	{
		Studio studio = new Studio();
		
		double starLength = 0.5;
		double edgeRadius = 0.005;

		// the scene
		SceneObjectContainer scene = new SceneObjectContainer("the scene", null, studio);

		// the standard scene objects
		scene.addSceneObject(SceneObjectClass.getSkySphere(scene, studio));
		scene.addSceneObject(SceneObjectClass.getLighterChequerboardFloor(-starLength/2 - edgeRadius, scene, studio));
				
		// add any other scene objects
		
		// the lens star
		EditableLensStar lensStar = new EditableLensStar(
				"Lens star",	// description
				NUMBER_OF_LENSES,	// numberOfLenses
				true,	// show all lenses
				NUMBER_OF_LENSES,	// number of shown lenses; -1 for all
				0,	// start index of shown lenses
				EditableLensStar.getFocalLengthForRadiusOfRegularPolygonTrajectory(
						NUMBER_OF_LENSES,	// numberOfLenses
						1	// radiusOfRegularPolygonTrajectory
					),	// focalLength
				0,	// principalPointDistance
				new Vector3D(0, 0, 0),	// centre
				new Vector3D(0, 1, 0),	// intersectionDirection
				new Vector3D(1, 0, 0),	// pointOnLens1
				// (Math.floorMod(NUMBER_OF_LENSES,2)==1)?new Vector3D(0,0,-1):new Vector3D(1, 0, 0),	// pointOnLens1
				1.5,	// starRadius,
				starLength,	// starLength,
				0.99,	// lensTransmissionCoefficient
				SHOW_LENS_EDGES,	// showEdges,
				edgeRadius,	// edgeRadius
				SurfaceColour.GREY50_MATT,	// edgeSurfaceProperty
				scene,	// parent,
				studio
			);
		scene.addSceneObject(lensStar);
		
		if(SHOW_SPHERE)
		{
			scene.addSceneObject(new EditableScaledParametrisedSphere(
					"Simulated point light source",	// description,
					new Vector3D(0, 0, SPHERE_DISTANCE),	// centre,
					0.05,	// radius,
					SurfaceColour.RED_SHINY,	// surfaceProperty,
					scene,	// parent, 
					studio
				));
		}
		
		studio.setScene(scene);

		// trace the rays with trajectory through the scene
		studio.traceRaysWithTrajectory();


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

		Vector3D cameraPosition, viewDirection;
		double horizontalAngleOfView;
		ApertureSizeType apertureSize;
		double zFocus;
		
		switch(CAMERA_POSITION)
		{
		case CENTRE_HEIGHT:
			viewDirection = new Vector3D(0, 0, 1);
			double outsideCameraDistance = 5;
			cameraPosition = viewDirection.getWithLength(-outsideCameraDistance);
			horizontalAngleOfView = 20;
			apertureSize = ApertureSizeType.MEDIUM;
			zFocus = -1;	// image of the red sphere
			break;
		case INSIDE:
			viewDirection = new Vector3D(0, 0, 1);
			cameraPosition = new Vector3D(-1, 0, 0);
			horizontalAngleOfView = 45;
			apertureSize = ApertureSizeType.TINY;
			zFocus = 2;
			break;
		case TOP:
			viewDirection = new Vector3D(0, -1, 0.01);
			outsideCameraDistance = 10;
			cameraPosition = viewDirection.getWithLength(-outsideCameraDistance);
			horizontalAngleOfView = 20;
			apertureSize = ApertureSizeType.SMALL;
			zFocus = 0;
			break;
		case STANDARD:
		default:
			viewDirection = new Vector3D(-.1, -.2, 1);
			outsideCameraDistance = 10;
			cameraPosition = viewDirection.getWithLength(-outsideCameraDistance);
			horizontalAngleOfView = 20;
			apertureSize = ApertureSizeType.SMALL;
			zFocus = -1;
		}
		
		SceneObject focusScene = new Plane(
				"focussing plane",
				new Vector3D(0, 0, zFocus),	// point on plane; this is actually the position where the sphere is imaged to
				viewDirection,	// normal to plane
				(SurfaceProperty)null,
				null,	// parent
				Studio.NULL_STUDIO
		);
		
		EditableRelativisticAnyFocusSurfaceCamera camera = new EditableRelativisticAnyFocusSurfaceCamera(
				"Camera",
				cameraPosition,	// centre of aperture
				viewDirection,	// viewDirection
				new Vector3D(0, 1, 0),	// top direction vector
				horizontalAngleOfView,	// horiontalViewAngle in degrees; 2*MyMath.rad2deg(Math.atan(2./10.)) gives same view angle as in previous version
				new Vector3D(0, 0, 0),	// beta
				pixelsX, pixelsY,	// logical number of pixels
				ExposureCompensationType.EC0,	// exposure compensation +0
				20,	// maxTraceLevel
				focusScene,
				null,	// cameraFrameScene,
				apertureSize,	// aperture size
				(apertureSize==ApertureSizeType.PINHOLE?QualityType.NORMAL:(TEST?QualityType.NORMAL:QualityType.GREAT)),	// blur quality
				(TEST?QualityType.NORMAL:QualityType.GOOD)	// anti-aliasing quality
		);

		// the lights
		LightSourceContainer lights = new LightSourceContainer("lights");
		lights.add(new AmbientLight("background light", DoubleColour.GREY20));
		double phongLightSourceDistance = 1000;
		double phongLightSourcePhi = MyMath.deg2rad(-130);	// azimuthal angle w.r.t. forward direction
		double phongLightSourceTheta = MyMath.deg2rad(60);	// angle above the horizon
		lights.add(new PhongLightSource(
				"point light souce",
				new Vector3D(
						Math.cos(phongLightSourceTheta)*Math.sin(phongLightSourcePhi),
						Math.sin(phongLightSourceTheta),
						Math.cos(phongLightSourceTheta)*Math.cos(phongLightSourcePhi)
						
					).getWithLength(phongLightSourceDistance),
				DoubleColour.GREY80, DoubleColour.GREY80, 40.
			));

		studio.setLights(lights);
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
