package optics.raytrace.research.fourPiLens;

import java.awt.*;

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
 * Calculates ray trajectories from a point located inside an ideal-lens cloak.
 * 
 * @author Johannes Courtial
 */
public class FourPiLensTrajectoriesFromPointWithin
{
	public static final double H_C_OVER_F = 0.4;	// 0.655;
	public static final double H_C_OVER_H_1 = 0.5;
	public static final double HORIZONTAL_ANGLE_OF_VIEW = .5;
	public static final boolean BOTTOM_LENS_PRESENT = true;

	/**
	 * Filename under which main saves the rendered image.
	 * Alter to save the rendered image under a different name.
	 * @return	the filename under which main saves the rendered image.
	 */
	public static String getFilename()
	{
		return "IdealLensCloakTrajectoriesFromPointWithin"	// the name
			+ (BOTTOM_LENS_PRESENT?"":" without bottom lens")
			+ " hCOverf = " + H_C_OVER_F
			+ " hCOverh1 = " + H_C_OVER_H_1
			+ " horizontal angle of view " + HORIZONTAL_ANGLE_OF_VIEW
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
		// scene.addSceneObject(SceneObjectClass.getChequerboardFloor(scene, studio));
				
		// add any other scene objects
		
		// Tim's head, behind the cloak
//		scene.addSceneObject(new EditableTimHead(
//				"Tim's head",
//				new Vector3D(0, 0, 15),
//				scene,
//				studio
//			));

		// The parameter we actually want to give is f and hC/f, the height of the camera above the base
		// *in units of the focal length of the base lens*.
		// The height of the camera has to be less than the height of the lower inner vertex
		// (in physical space), as we want the camera to be in the bottom tetrahedron.
		// The relationship between h1, h1E and f is given by the lens equation:
		// 	1/h1 - 1/h1E = 1/f.
		// Let's put the camera very close to the top of the lower inner tetrahedron:
		// 	hC = (hC/h1) h1,
		// where hC/h1 = 0.99 or something.
		// Then
		// 	(hC/h1)/hC - 1/h1E = 1/f,
		// so
		// 	1/h1E = (hC/h1)/hC - 1/f = ((hC/h1) f - hC) / (f hC) = ((hC/h1) f/hC - 1) / f,
		// and therefore
		// 	h1E = f / [(hC/h1) / (hC/f) - 1].
		// Alternatively, we can calculate
		//  1/h1E = ((hC/h1) f - hC) / (f hC) = ((hC/h1) - hC/f) / hC,
		// and so
		//  h1E = hC / ((hC/h1) - hC/f).
		
		double
			h = 2,	// height of the lens cloak
			h1 = h*1./3.,	// height of the lower inner vertex above the base
			h2 = h*2./3.,	// height of the upper inner vertex above the base
			hC = H_C_OVER_H_1*h1,	// height of the camera above the base
			h1E = hC / (H_C_OVER_H_1 - H_C_OVER_F);	// height of the lower inner vertex above the base, in EM space
		
		System.out.println("h1E="+h1E);
		
		// ideal-lens cloak
		EditableIdealLensCloak editableIdealLensCloak = 
				new EditableIdealLensCloak(
						"Ideal-lens cloak",
						new Vector3D(0, 0, -hC),	// base centre
						new Vector3D(0, 1, 0),	// front direction
						new Vector3D(1, 0, 0),	// right direction
						new Vector3D(0, 0, 1),	// top direction
						h*2./3.,	// base radius; if this is 2/3 of height then tetrahedron is regular
						h,	// height
						h1,	// heightLowerInnerVertexP
						h2,	// heightUpperInnerVertexP
						h1E,	// heightLowerInnerVertexE
						0.96,	// interface transmission coefficient
						false,	// show frames
						0.01*h,	// frame radius
						SurfaceColour.RED_SHINY,	// frame surface property
						// false,	// show placeholder surfaces
						LensElementType.IDEAL_THIN_LENS,	// lens-element type
						scene,
						studio
					);
		
		// remove base lens
		if(!BOTTOM_LENS_PRESENT)
		{
		editableIdealLensCloak.removeFirstSceneObjectWithDescription(
				"Base outer lens",	// description
				true	// searchIteratively
			);
		}

		scene.addSceneObject(editableIdealLensCloak);

		int noOfRays = 50;
		for(int i=0; i<noOfRays; i++)
		{
			double angle = ((double)i)/noOfRays*2.*Math.PI;

			scene.addSceneObject(new EditableRayTrajectory(
					"ray trajectory",
					new Vector3D(0.2, 0, 0),	// start point
					0,	// start time
					new Vector3D(Math.cos(angle), 0, Math.sin(angle)),	// initial direction
					0.01,	// radius
					new SurfaceColourLightSourceIndependent(DoubleColour.GREEN, true),
					20,	// max trace level
					true,	// reportToConsole
					scene,
					studio
					)
					);
		}
		
		studio.setScene(scene);

		// trace the rays with trajectory through the scene
		studio.traceRaysWithTrajectory();

		// now replace the lens-TO tetrahedron with one that has placeholder surfaces
		// editableIdealLensCloak.setShowPlaceholderSurfaces(true);
		editableIdealLensCloak.setLensElementType(LensElementType.GLASS_PANE);
		editableIdealLensCloak.populateSceneObjectCollection();
		


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
				new Vector3D(0, 0, 0),	// point on plane
				new Vector3D(0, 1, 0),	// normal to plane
				(SurfaceProperty)null,
				null,	// parent
				Studio.NULL_STUDIO
		);
		
		EditableRelativisticAnyFocusSurfaceCamera camera = new EditableRelativisticAnyFocusSurfaceCamera(
				"Camera",
				new Vector3D(0, 1000, 0),	// centre of aperture
				new Vector3D(0, -1, 0),	// viewDirection
				new Vector3D(0, 0, 1),	// top direction vector
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
