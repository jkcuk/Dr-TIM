package optics.raytrace.research.TO.idealLensCloak;

import java.awt.*;
import java.text.DecimalFormat;

import math.*;
import optics.raytrace.sceneObjects.*;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.surfaces.SurfaceColour;
import optics.raytrace.surfaces.SurfaceColourLightSourceIndependent;
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
import optics.raytrace.GUI.sceneObjects.EditableRayTrajectory;


/**
 * Based on NonInteractiveTIMMovieMaker.
 * 
 * Calculates a movie of ray trajectories in an ideal-lens cloak.
 * The trajectories start inside the cloak.
 * 
 * @author Johannes Courtial
 */
public class FourPiLensTrajectoryFromPointWithinMovieMaker3
{
	/**
	 * @return	the number of frames in the movie
	 */
	public static int getNoOfFrames()
	{
		return 200;
	}
	
	/**
	 * Filename under which main saves the rendered image.
	 * Alter to save the rendered image under a different name.
	 * @return	the filename under which main saves the rendered image.
	 */
	public static String getFilename(int frame)
	{
		return "IdealLensCloakTrajectory3_below_"+	// the name
		      (new DecimalFormat("000000")).format(frame)	// the number of the frame, converted into a string
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
		// scene.addSceneObject(SceneObjectClass.getChequerboardFloor(scene, studio));
				
		// add any other scene objects
		
		// Tim's head, behind the cloak
//		scene.addSceneObject(new EditableTimHead(
//				"Tim's head",
//				new Vector3D(0, 0, 15),
//				scene,
//				studio
//			));

		// a lens-TO tetrahedron
		EditableIdealLensCloak editableIdealLensCloak = 
				new EditableIdealLensCloak(
						"Ideal-lens cloak",
						new Vector3D(0, -1, 10),	// base centre
						new Vector3D(0, 0, -1),	// front direction
						new Vector3D(1, 0, 0),	// right direction
						new Vector3D(0, 1, 0),	// top direction
						2.0*2./3.,	// base radius; if this is 2/3 of height then tetrahedron is regular
						2.0,	// height
						0.5,	// heightLowerInnerVertexP
						1.5,	// heightUpperInnerVertexP
						-1,	// heightLowerInnerVertexE
						0.7,	// interface transmission coefficient
						false,	// show frames
						0.02,	// frame radius
						SurfaceColour.RED_SHINY,	// frame surface property
						// false,	// show placeholder surfaces
						LensElementType.IDEAL_THIN_LENS,	// lens-element type
						scene,
						studio
					);
		
		// remove base lens
//		editableIdealLensCloak.removeFirstSceneObjectWithDescription(
//				"Base outer lens",	// description
//				true	// searchIteratively
//			);

		scene.addSceneObject(editableIdealLensCloak);
		
		double angle = (frame)/(getNoOfFrames()-1.)*Math.PI/2;
				
		scene.addSceneObject(new EditableRayTrajectory(
						"ray trajectory",
						new Vector3D(0, -0.75, 10.01),	// start point
						0,	// start time
						new Vector3D(Math.cos(angle), Math.sin(angle), 0),	// initial direction
						0.01,	// radius
						new SurfaceColourLightSourceIndependent(DoubleColour.GREEN, true),
						100,	// max trace level
						true,	// reportToConsole
						scene,
						studio
				)
			);

		scene.addSceneObject(new EditableRayTrajectory(
				"ray trajectory",
				new Vector3D(0, -0.75, 10.01),	// start point
				0,	// start time
				new Vector3D(-Math.cos(angle), -Math.sin(angle), 0),	// initial direction
				0.01,	// radius
				new SurfaceColourLightSourceIndependent(DoubleColour.RED, true),
				100,	// max trace level
				true,	// reportToConsole
				scene,
				studio
		)
	);

		studio.setScene(scene);

		// trace the rays with trajectory through the scene
		studio.traceRaysWithTrajectory();

		// now replace the lens-TO tetrahedron with one that has placeholder surfaces
		// editableIdealLensCloak.setShowPlaceholderSurfaces(true);
		editableIdealLensCloak.setLensElementType(LensElementType.GLASS_PANE);
		editableIdealLensCloak.populateSceneObjectCollection();
		
//		// remove the "proper" lens-TO tetrahedron...
//		scene.removeSceneObject(editableIdealLensCloak);
//		
//		// ... and add the one with placeholder surfaces
//		scene.addSceneObject( 
//				new EditableIdealLensCloak(
//						"Lens TO tetrahedron with placeholder surfaces",
//						new Vector3D(0, -1, 10),	// base centre
//						new Vector3D(0, 0, -1),	// front direction
//						new Vector3D(1, 0, 0),	// right direction
//						new Vector3D(0, 1, 0),	// top direction
//						2.0*2./3.,	// base radius; if this is 2/3 of height then tetrahedron is regular
//						2.0,	// height
//						0.5,	// heightLowerInnerVertexP
//						1.5,	// heightUpperInnerVertexP
//						1,	// heightLowerInnerVertexE
//						0.96,	// interface transmission coefficient
//						false,	// show frames
//						0.02,	// frame radius
//						SurfaceColour.RED_SHINY,	// frame surface property
//						true,	// show placeholder surfaces
//						scene,
//						studio
//					));


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

		
		SceneObject focusScene = new Plane(
				"focussing plane",
				new Vector3D(0, 0, 10),	// point on plane
				new Vector3D(0, 0, 1),	// normal to plane
				(SurfaceProperty)null,
				null,	// parent
				Studio.NULL_STUDIO
		);
		
		EditableRelativisticAnyFocusSurfaceCamera camera = new EditableRelativisticAnyFocusSurfaceCamera(
				"Camera",
				new Vector3D(2, -3, 0),	// centre of aperture
				new Vector3D(-.2, .3, 1),	// viewDirection
				new Vector3D(0, 1, 0),	// top direction vector
				20,	// horiontalViewAngle in degrees; 2*MyMath.rad2deg(Math.atan(2./10.)) gives same view angle as in previous version
				new Vector3D(0, 0, 0),	// beta
				pixelsX*antiAliasingFactor, pixelsY*antiAliasingFactor,	// logical number of pixels
				ExposureCompensationType.EC0,	// exposure compensation +0
				100,	// maxTraceLevel
				focusScene,
				null,	// cameraFrameScene,
				ApertureSizeType.PINHOLE,	// aperture size
				QualityType.RUBBISH,	// blur quality
				QualityType.NORMAL	// anti-aliasing quality
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
		
		for(int frame = 0; frame < getNoOfFrames(); frame++)
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
