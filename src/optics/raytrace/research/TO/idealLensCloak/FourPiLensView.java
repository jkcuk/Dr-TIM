package optics.raytrace.research.TO.idealLensCloak;

import java.awt.*;

import math.*;
import optics.raytrace.sceneObjects.*;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.surfaces.GlensSurface;
import optics.raytrace.surfaces.SurfaceColour;
import optics.raytrace.cameras.PinholeCamera.ExposureCompensationType;
import optics.raytrace.core.*;
import optics.raytrace.GUI.cameras.EditableRelativisticAnyFocusSurfaceCamera;
import optics.raytrace.GUI.cameras.QualityType;
import optics.raytrace.GUI.lowLevel.ApertureSizeType;
import optics.raytrace.GUI.nonInteractive.PhotoCanvas;
import optics.raytrace.GUI.nonInteractive.PhotoFrame;
import optics.raytrace.GUI.sceneObjects.Editable4PiLens;
import optics.raytrace.GUI.sceneObjects.EditableScaledParametrisedDisc;
import optics.raytrace.GUI.sceneObjects.EditableTimHead;


/**
 * Based on NonInteractiveTIM.
 * 
 * Calculates a movie of the view from a position within an ideal-lens cloak.
 * 
 * @author Johannes Courtial
 */
public class FourPiLensView
{
	public static final double F = 10000;
	public static final int HORIZONTAL_ANGLE_OF_VIEW = 20;
	public static final boolean SHOW_EDGES = true;
	public static final boolean SHOW_L1 = false;	// the principal-plane lens in the 4 pi lens
	public static final boolean SHOW_R1 = true;	// the lens structure that remains after L1 is removed from the cloak
	public static final boolean SHOW_LENS_EQUIVALENT_TO_R1 = false;	// the equivalent lens to the 4 pi lens without PP lens
	public static final boolean SIDE_VIEW = true;
	
	/**
	 * Filename under which main saves the rendered image.
	 * Alter to save the rendered image under a different name.
	 * @return	the filename under which main saves the rendered image.
	 */
	public static String getFilename()
	{
		return "4PiLensView"	// the name
				+ " f " + F
				+ " L1 "+(SHOW_L1?"":"not ") + "shown"
				+ " R1 "+(SHOW_R1?"":"not ") + "shown"
				+ " lens equivalent to R1 "+(SHOW_LENS_EQUIVALENT_TO_R1?"":"not ") + "shown"
				+ " horizontal angle of view " + HORIZONTAL_ANGLE_OF_VIEW
				+ (SHOW_EDGES?" with edges":"")
				+ (SIDE_VIEW?" (side view)":"")
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
		scene.addSceneObject(SceneObjectClass.getSkySphere(scene, studio));
		scene.addSceneObject(SceneObjectClass.getChequerboardFloor(-2, scene, studio));
				
		// add any other scene objects
		
		// Tim's head, behind the cloak
		scene.addSceneObject(new EditableTimHead(
				"Tim's head",
				new Vector3D(0, 0, 10),
				1,	// radius
				new Vector3D(0, 0, -1),	// front direction
				new Vector3D(0, 1, 0),	// top direction
				new Vector3D(1, 0, 0),	// right direction
				scene,
				studio
			));
		
		// 4Pi lens
		Editable4PiLens fourPiLens = new Editable4PiLens(
				"4Pi lens",	// description
				new Vector3D(0, 0, 5),	// principalPoint
				new Vector3D(0, 0, 1),	// opticalAxisDirection
				F,	// focalLengthOutwards
				new Vector3D(0, 1, 0),	// transverseDirection1
				new Vector3D(1, 0, 0),	// transverseDirection2
				1,	// radius
				1,	// length
				SHOW_L1,	// showL1
				SHOW_R1,	// showR1
				1./3.,	// closerInnerVertexDistance
				2./3.,	// fartherInnerVertexDistance
				Editable4PiLens.LensType.IDEAL_THIN_LENS,	// LensType
				SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT,	// individualLensTransmissionCoefficient
				SHOW_EDGES,	// showFrames
				0.01,	// frameRadius
				SurfaceColour.GREY50_SHINY,	// frameSurfaceProperty
				false,	// showPlaceholderSurfaces
				scene, 
				studio
				);

		scene.addSceneObject(fourPiLens);
		
		if(SHOW_LENS_EQUIVALENT_TO_R1)
		{
			// lens equivalent to R1
			EditableScaledParametrisedDisc equivalentLens = new EditableScaledParametrisedDisc(
					"Lens equivalent to R1",	// description
					new Vector3D(0, 0, 5),	// centre
					new Vector3D(0, 0, 1),	// normal
					0.7,	// radius
					new GlensSurface(
							new Vector3D(0, 0, 1),	// opticalAxisDirectionPos
							new Vector3D(0, 0, 5),	// principalPoint
							-F,	// focalLengthNeg
							F,	// focalLengthPos
							SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT,	// transmissionCoefficient
							true	// shadowThrowing
						),	// surface property
					scene, 
					studio
			);

			scene.addSceneObject(equivalentLens);
		}

		studio.setScene(scene);

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

		// something that we can focus on in any direction
		SceneObject focusScene = Plane.zPlane(
				"Focussing plane",	// description
				10,	// z0
				null,	// surfaceProperty
				null,	// parent
				Studio.NULL_STUDIO
			);
		
		EditableRelativisticAnyFocusSurfaceCamera camera = new EditableRelativisticAnyFocusSurfaceCamera(
				"Camera",
				(SIDE_VIEW?new Vector3D(2, 3, 0):new Vector3D(0, 0, 0)),	// centre of aperture
				(SIDE_VIEW?new Vector3D(-.2, -.3, .5):new Vector3D(0, 0, .5)),	// viewDirection
				new Vector3D(0, 1, 0),	// top direction vector
				HORIZONTAL_ANGLE_OF_VIEW,	// horiontalViewAngle in degrees; 2*MyMath.rad2deg(Math.atan(2./10.)) gives same view angle as in previous version
				new Vector3D(0, 0, 0),	// beta
				pixelsX*antiAliasingFactor, pixelsY*antiAliasingFactor,	// logical number of pixels
				ExposureCompensationType.EC0,	// exposure compensation +0
				100,	// maxTraceLevel
				focusScene,
				null,	// cameraFrameScene,
				ApertureSizeType.PINHOLE,	// aperture size
				QualityType.RUBBISH,	// blur quality
				QualityType.GOOD	// anti-aliasing quality
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
