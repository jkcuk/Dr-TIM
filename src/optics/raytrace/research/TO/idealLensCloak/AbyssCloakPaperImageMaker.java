package optics.raytrace.research.TO.idealLensCloak;

import java.awt.*;

import math.*;
import optics.raytrace.sceneObjects.*;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.surfaces.SurfaceColour;
import optics.raytrace.cameras.PinholeCamera.ExposureCompensationType;
import optics.raytrace.core.*;
import optics.DoubleColour;
import optics.raytrace.GUI.cameras.EditableRelativisticAnyFocusSurfaceCamera;
import optics.raytrace.GUI.cameras.QualityType;
import optics.raytrace.GUI.lowLevel.ApertureSizeType;
import optics.raytrace.GUI.lowLevel.LensElementType;
import optics.raytrace.GUI.nonInteractive.PhotoCanvas;
import optics.raytrace.GUI.nonInteractive.PhotoFrame;
import optics.raytrace.GUI.sceneObjects.EditableIdealLensCloak;
import optics.raytrace.GUI.sceneObjects.EditableParametrisedPlane;
import optics.raytrace.GUI.sceneObjects.EditableTimHead;


/**
 * Based on NonInteractiveTIMMovieMaker.
 * 
 * Calculates a movie of the view through an lens-TO tetrahedron, with the viewpoint changing.
 * 
 * @author Johannes Courtial
 */
public class AbyssCloakPaperImageMaker
{
	public static final boolean SHOW_TETRAHEDRON = true;
	public static final boolean SHOW_FRAMES = false;
	public static final boolean PLACEHOLDER_WINDOWS = false;
	public static final boolean SHOW_TIM = true;
	public static final boolean SHOW_SPHERE = true;
	public static final boolean TOP_VIEW = false;
	public static final boolean TEST = false;
	public static final boolean SAVE = true;

	/**
	 * Filename under which main saves the rendered image.
	 * Alter to save the rendered image under a different name.
	 * @return	the filename under which main saves the rendered image.
	 */
	public static String getFilename()
	{
		return "Abyss cloak paper image"	// the name
				+(SHOW_SPHERE?" (with sphere)":"")
				+(!SHOW_TETRAHEDRON?" (without tetrahedron)":"")
				+(SHOW_FRAMES?" (with frames)":"")
				+(PLACEHOLDER_WINDOWS?" (with placeholder windows)":"")
				+(SHOW_TIM?" (with Tim)":"")
				+(TOP_VIEW?" (top view)":"")
				+(TEST?" (test)":"")
				+".bmp";	// the extension
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
		scene.addSceneObject(SceneObjectClass.getLighterChequerboardFloor(-1, scene, studio));
		scene.addSceneObject(SceneObjectClass.getSkySphere(scene, studio));	// the sky

		double frameRadius = 0.02;
		
		// parameters of cloak/shrinker
		Vector3D
			baseCentre = new Vector3D(0, -1+frameRadius, 10),
			topDirection = new Vector3D(0, 1, 0);
		double
			R = 2.*2./3,	// base radius
			h = 2.0,	// height of top vertex above base
			h1P = h*0.5,	// heightLowerInnerVertexP; initial solution: h1P = h*0.42855
			h1E = -h*0.5,	// heightLowerInnerVertexE; initial solution: h1E = h*0.2
			h2P = h*0.75;	// heightUpperInnerVertexP; initial solution: h1P = h*0.75
		
		// the tetrahedral lens-TO device
		scene.addSceneObject(new EditableIdealLensCloak(
				"Ideal-lens cloak",
				baseCentre,	// base centre
				new Vector3D(0, 0, -1),	// front direction
				new Vector3D(1, 0, 0),	// right direction
				topDirection,	// top direction
				R,	// base radius
				h,	// height
				h1P,	// heightLowerInnerVertexP
				h2P,	// heightUpperInnerVertexP
				h1E,	// heightLowerInnerVertexE
				PLACEHOLDER_WINDOWS?1:0.9,	// interface transmission coefficient
				SHOW_FRAMES,	// show frames
				frameRadius,	// frame radius
				SurfaceColour.RED_SHINY,	// frame surface property
				// PLACEHOLDER_WINDOWS,	// show placeholder surfaces
				(PLACEHOLDER_WINDOWS?LensElementType.GLASS_PANE:LensElementType.IDEAL_THIN_LENS),
				scene,
				studio	
			));
		
		// and something to look at
		if(SHOW_TIM)
		{
			scene.addSceneObject(new EditableTimHead(
					"Tim's head",	// description
					new Vector3D(0, 0, 12),	// centre
					1,	// radius
					new Vector3D(0, 0, -1),	// front direction
					new Vector3D(0, 1, 0),	// top direction
					new Vector3D(1, 0, 0),	// right direction
					scene,	// parent
					studio
				));
		}
		
		SurfaceColour nonShadowThrowingColour = 
				new SurfaceColour(DoubleColour.WHITE, DoubleColour.WHITE, false);
				// new SurfaceColour(DoubleColour.GREY20, DoubleColour.WHITE, false);
				// new SurfaceColour(DoubleColour.DARK_RED, DoubleColour.WHITE, false);
		
		if(SHOW_SPHERE)
		{
			double sphereRadius = h1P*0.15;
			scene.addSceneObject(new Sphere(
					"white sphere",	// description
					Vector3D.sum(baseCentre, topDirection.getWithLength(h1P-frameRadius-2*sphereRadius)),	// centre
					sphereRadius,	// radius
					nonShadowThrowingColour,
					scene, 
					studio
				));
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
		
		double cameraDistance = Math.sqrt(10.*10.+MyMath.square(1.88));
		double phi = MyMath.deg2rad(10);
		// double theta = Math.atan2(-1.88, 10);
		double theta = TOP_VIEW?MyMath.deg2rad(-70):Math.atan2(-1.88, 10);
		double ct = Math.cos(theta), st = Math.sin(theta);
		Vector3D viewDirection = new Vector3D(-ct*Math.sin(phi), st, ct*Math.cos(phi));
		Vector3D apertureCentre = Vector3D.difference(new Vector3D(0, 0, 10), viewDirection.getProductWith(cameraDistance));
		// Vector3D apertureCentre = new Vector3D(10*Math.sin(alpha), 1.88, 10-10*Math.cos(alpha));
		// Vector3D viewDirection = Vector3D.difference(new Vector3D(0, 0, 10), apertureCentre);

		// a camera with a non-zero aperture size (so it simulates blur)
		EditableRelativisticAnyFocusSurfaceCamera camera = new EditableRelativisticAnyFocusSurfaceCamera(
				"Camera",
				apertureCentre,	// centre of aperture
				viewDirection,	// viewDirection
				new Vector3D(0, 1, 0),	// top direction vector
				20,	// horiontalViewAngle in degrees; 2*MyMath.rad2deg(Math.atan(2./10.)) gives same view angle as in previous version
				new Vector3D(0, 0, 0),	// beta
				pixelsX, pixelsY,	// logical number of pixels
				ExposureCompensationType.EC0,	// exposure compensation +0
				1000,	// maxTraceLevel
				new EditableParametrisedPlane(
						"focussing plane",
						new Vector3D(0, 0, 10),	// point on plane
						viewDirection,	// normal to plane
						SurfaceColour.BLACK_SHINY,
						scene,
						studio
				),	// focusScene,
				new SceneObjectContainer("camera-frame scene", null, studio),	//cameraFrameScene,
				ApertureSizeType.SMALL,	// aperture size
				TEST?QualityType.BAD:QualityType.GREAT,	// blur quality
				TEST?QualityType.NORMAL:QualityType.GOOD	// QualityType.GREAT	// anti-aliasing quality
		);
		
		// System.out.println("Aperture radius="+camera.getApertureRadius());

		studio.setScene(scene);
		studio.setLights(LightSource.getStandardLightsFromBehind());
		studio.setCamera(camera);

		return studio;
	}

	/**
	 * This method gets called when the Java application starts.
	 * 
	 * @see createStudio
	 * @author	Alasdair Hamilton, Johannes Courtial
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
		if(SAVE) studio.savePhoto(getFilename(), "bmp");

		// display the image on the screen
		container.add(new PhotoCanvas(studio.getPhoto()));
		container.validate();
	}
}
