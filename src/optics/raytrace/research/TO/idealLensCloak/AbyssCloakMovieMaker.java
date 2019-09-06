package optics.raytrace.research.TO.idealLensCloak;

import java.awt.*;
import java.text.DecimalFormat;

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
import optics.raytrace.GUI.sceneObjects.EditableScaledParametrisedSphere;
import optics.raytrace.GUI.sceneObjects.EditableTimHead;


/**
 * Based on NonInteractiveTIMMovieMaker.
 * 
 * Calculates a movie of the view through an lens-TO tetrahedron, with the viewpoint changing.
 * 
 * @author Johannes Courtial
 */
public class AbyssCloakMovieMaker
{
	public static final boolean SHOW_TETRAHEDRON = true;
	public static final boolean SHOW_FRAMES = true;	// false;
	public static final boolean PLACEHOLDER_WINDOWS = true;	// false;
	public static final boolean SHOW_TIM = true;
//	public static final boolean SHOW_SPHERE_ARRAY = false;	// in focus plane
//	public static final boolean SHOW_CYLINDER_ARRAY = false;	// in focus plane
	public static final boolean SHOW_SPHERE = true;
	public static final boolean TEST = false;

	/**
	 * @return	the number of frames in the movie
	 */
	public static int getNoOfFrames()
	{
		return (TEST?40:400);
	}
	
	/**
	 * Filename under which main saves the rendered image.
	 * Alter to save the rendered image under a different name.
	 * @return	the filename under which main saves the rendered image.
	 */
	public static String getFilename(int frame)
	{
		return "Cone invisibility viewpoint"	// the name
				+(SHOW_SPHERE?" (with sphere)":"")
//				+(SHOW_CYLINDER_ARRAY?" (with cylinder array)":"")
//				+(SHOW_SPHERE_ARRAY?" (with sphere array)":"")
				+(!SHOW_TETRAHEDRON?" (without tetrahedron)":"")
				+(SHOW_FRAMES?" (with frames)":"")
				+(PLACEHOLDER_WINDOWS?" (with placeholder windows)":"")
				+(SHOW_TIM?" (with Tim)":"")
				+(TEST?" (test)":"")
				+" "+(new DecimalFormat("000000")).format(frame)	// the number of the frame, converted into a string
				+".bmp";	// the extension
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

		// the standard scene objects
		scene.addSceneObject(SceneObjectClass.getSkySphere(scene, studio));
		scene.addSceneObject(SceneObjectClass.getChequerboardFloor(-10.01, scene, studio));
				
		// add any other scene objects
				
		// Tim's head, behind the cloak
		if(SHOW_TIM)
		{
			scene.addSceneObject(new EditableTimHead(
					"Tim's head",
					new Vector3D(0, 0, 13),
					1,	// radius
					new Vector3D(0, 0, -1),	// front direction
					new Vector3D(0, 1, 0),	// top direction
					new Vector3D(1, 0, 0),	// right direction
					scene,
					studio
					));
		}
		
		// a cloaked green sphere
		if(SHOW_SPHERE)
		{
			scene.addSceneObject(new EditableScaledParametrisedSphere(
						"sphere without shadow",	// description
						new Vector3D(0, -1+2.0*1./3.-0.1, 10),	// centre,
						0.05,	// radius,
						new Vector3D(0, 1, 0),	// pole,
						new Vector3D(1, 0, 0),	// phi0Direction,
						0, 1,	// sThetaMin, sThetaMax,
						0, 1,	// sPhiMin, sPhiMax,
						new SurfaceColour(DoubleColour.WHITE, DoubleColour.BLACK, false),	// surfaceProperty,
						scene,	// parent, 
						studio
					));
		}

		// a lens-TO tetrahedron
		if(SHOW_TETRAHEDRON)
		{
			scene.addSceneObject(
				new EditableIdealLensCloak(
						"Lens TO tetrahedron",
						new Vector3D(0, -1, 10),	// base centre
						new Vector3D(0, 0, -1),	// front direction
						new Vector3D(1, 0, 0),	// right direction
						new Vector3D(0, 1, 0),	// top direction
						2.0*2./3.,	// base radius; if this is 2/3 of height then tetrahedron is regular
						2.0,	// height
						2.0*1./3.,	// 0.5,	// heightLowerInnerVertexP
						2.0*2./3.,	// 1.5,	// heightUpperInnerVertexP
						-1.,	// heightLowerInnerVertexE
						1,	// 0.9,	// 0.96,	// interface transmission coefficient
						// normally 0.96, but 0.9 for figures in which we want lenses to be just visible
						// and 1 in those where we want them invisible
						SHOW_FRAMES,	// show frames
						0.02,	// frame radius
						SurfaceColour.RED_SHINY,	// frame surface property
						// PLACEHOLDER_WINDOWS,	// show placeholder surfaces
						(PLACEHOLDER_WINDOWS?LensElementType.GLASS_PANE:LensElementType.IDEAL_THIN_LENS),
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
		pixelsY = 480,
		antiAliasingFactor = 1;
		// If antiAliasingFactor is set to N, the image is calculated at resolution
		// N*pixelsX x N*pixelsY.

		
		// angle in z-y plane (angle = 0 means look in z direction, angle = 90° looks in y direction)
		double angle = -frame*2.0*Math.PI / getNoOfFrames();
		
		Vector3D viewDirection = new Vector3D(-0.25, Math.sin(angle), Math.cos(angle)).getNormalised();
		// Vector3D apertureCentre = new Vector3D(0, 10*Math.sin(angle), 10-10*Math.cos(angle));
		
		SceneObject focusScene = new Plane(
				"focussing plane",
				new Vector3D(0, -2, 10),	// point on plane
				viewDirection,	// normal to plane
				(SurfaceProperty)null,
				null,	// parent
				Studio.NULL_STUDIO
		);

		EditableRelativisticAnyFocusSurfaceCamera camera = new EditableRelativisticAnyFocusSurfaceCamera(
				"Camera",
				Vector3D.sum(
						new Vector3D(0, 0, 10),	// centre of view
						viewDirection.getProductWith(-10.)
					),	// apertureCentre,	// centre of aperture
				viewDirection,	// Vector3D.difference(new Vector3D(0.001, 0, 10), apertureCentre),	// viewDirection
				new Vector3D(0, 1, 0),	// top direction vector
				25,	// horiontalViewAngle in degrees; 2*MyMath.rad2deg(Math.atan(2./10.)) gives same view angle as in previous version
				new Vector3D(0, 0, 0),	// beta
				pixelsX*antiAliasingFactor, pixelsY*antiAliasingFactor,	// logical number of pixels
				ExposureCompensationType.EC0,	// exposure compensation +0
				100,	// maxTraceLevel
				focusScene,
				null,	// new EditableSceneObjectCollection("camera-frame scene", false, scene, studio),	// cameraFrameScene,
				ApertureSizeType.MEDIUM,	// aperture size; for some reason, this doesn't seem to have any effect
				TEST?QualityType.BAD:QualityType.GREAT,	// blur quality
				QualityType.NORMAL	// TEST?QualityType.NORMAL:QualityType.GOOD	// QualityType.GREAT	// anti-aliasing quality
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
	 * @author	Johannes Courtial
	 */
	public static void main(final String[] args)
	{
		// open a window, and take a note of its content pane
		Container container = (new PhotoFrame()).getContentPane();
		
		for(int frame = 23; frame < getNoOfFrames(); frame++)
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
