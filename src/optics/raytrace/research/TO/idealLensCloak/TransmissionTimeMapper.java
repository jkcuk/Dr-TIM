package optics.raytrace.research.TO.idealLensCloak;

import java.awt.*;

import math.*;
import optics.raytrace.sceneObjects.*;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.surfaces.SurfaceColour;
import optics.raytrace.surfaces.SurfaceColourTimeDependent;
import optics.raytrace.cameras.PinholeCamera.ExposureCompensationType;
import optics.raytrace.core.*;
import optics.raytrace.GUI.cameras.EditableRelativisticAnyFocusSurfaceCamera;
import optics.raytrace.GUI.cameras.QualityType;
import optics.raytrace.GUI.lowLevel.ApertureSizeType;
import optics.raytrace.GUI.lowLevel.LensElementType;
import optics.raytrace.GUI.nonInteractive.PhotoCanvas;
import optics.raytrace.GUI.nonInteractive.PhotoFrame;
import optics.raytrace.GUI.sceneObjects.EditableIdealLensCloak;
import optics.raytrace.GUI.sceneObjects.EditableScaledParametrisedSphere;


/**
 * Based on NonInteractiveTIMMovieMaker.
 * 
 * Plots a colour representation of the time of light rays to get from an object to an image
 * 
 * @author Johannes Courtial
 */
public class TransmissionTimeMapper
{
	// the height of the point above the base lens, in physical space and in EM space
	public static final double H_P_P = 0.2;
	public static final double H_P_E = 0.8;	// 0.8;
		
	/**
	 * Filename under which main saves the rendered image.
	 * Alter to save the rendered image under a different name.
	 * @return	the filename under which main saves the rendered image.
	 */
	public static String getFilename()
	{
		return "TransmissionTimeMap"	// the name
			+ " hP " + H_P_P
			+ " hP' " + H_P_E
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
		scene.addSceneObject(
				new EditableScaledParametrisedSphere(
						"Sphere with time-dependent colour",
						new Vector3D(0,0,0),	// centre
						20,	// huge radius
						new SurfaceColourTimeDependent(0.5, true),
						scene,
						studio
					)
			);
		
		// scene.addSceneObject(SceneObjectClass.getChequerboardFloor(scene, studio));
				
		// add any other scene objects
		
		// an ideal-lens cloak

		double h = 2.0;	// height of the cloak
		double h1 = h*1./3.;	// height of the lower inner vertex above the base lens
		double h2 = h*2./3.;	// height of the upper inner vertex above the base lens
		Vector3D topDirection = new Vector3D(0, 1, 0);
		Vector3D baseCentre = Vector3D.sum(
				topDirection.getWithLength(-h/2),
				new Vector3D(0, 0, 10)
			);	// this centres the cloak at the origin
		
		// Design the cloak such that it images the camera position in the physical "inside space"
		// (the space inside the cell just above the base lens) to the corresponding EM-space position
		// in "outside space".
		// The camera is placed at a position a distance H_C_P above the base lens,
		// which images it to a position a distance H_C_E above the base lens
		
		// first calculate the focal length of the base lens that would image accordingly;
		// lens equation:
		//   1/H_C_P + 1/(-H_C_E) = 1/f
		//   (H_C_E - H_C_P) / (H_C_P H_C_E) = 1/f
		// solve for f:
		//   f = H_C_P H_C_E / (H_C_E - H_C_P)
		double f = H_P_P*H_P_E / (H_P_E - H_P_P);
		
		// then calculate the height of the image of the lower inner vertex, as this is the parameter
		// the EditableIdealLensCloak takes
		// lens equation:
		//   1/h1 + 1/(-h1E) = 1/f
		// solve for h1E:
		//   1/h1E = 1/h1 - 1/f = (f - h1) / (h1 f)
		//   h1E = h1 f / (f - h1)
		double h1E = h1*f / (f - h1);
		
		EditableIdealLensCloak editableIdealLensCloak = 
				new EditableIdealLensCloak(
						"Ideal-lens cloak",
						baseCentre,	// base centre
						new Vector3D(0, 0, -1),	// front direction
						new Vector3D(1, 0, 0),	// right direction
						topDirection,	// top direction
						h*2./3.,	// base radius; if this is 2/3 of height then tetrahedron is regular
						h,	// height
						h1,	// heightLowerInnerVertexP
						h2,	// heightUpperInnerVertexP
						h1E,	// heightLowerInnerVertexE
						1,	// 0.9,	// interface transmission coefficient
						false,	// show frames
						0.01,	// frame radius
						SurfaceColour.GREY50_SHINY,	// frame surface property
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

		studio.setScene(scene);

		// now replace the lens-TO tetrahedron with one that has placeholder surfaces
//		editableIdealLensCloak.setShowPlaceholderSurfaces(true);
//		editableIdealLensCloak.setShowFrames(true);
//		editableIdealLensCloak.populateSceneObjectCollection();
		


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
				new Vector3D(0, 03, 0),	// centre of aperture
				new Vector3D(0, -.3, 1),	// viewDirection
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
