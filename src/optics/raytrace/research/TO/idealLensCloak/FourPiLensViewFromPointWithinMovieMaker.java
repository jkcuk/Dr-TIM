package optics.raytrace.research.TO.idealLensCloak;

import java.awt.*;
import java.text.DecimalFormat;

import math.*;
import optics.raytrace.sceneObjects.*;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.surfaces.SurfaceColour;
import optics.raytrace.cameras.PinholeCamera.ExposureCompensationType;
import optics.raytrace.core.*;
import optics.raytrace.GUI.cameras.EditableRelativisticAnyFocusSurfaceCamera;
import optics.raytrace.GUI.cameras.QualityType;
import optics.raytrace.GUI.lowLevel.ApertureSizeType;
import optics.raytrace.GUI.lowLevel.LensElementType;
import optics.raytrace.GUI.nonInteractive.PhotoCanvas;
import optics.raytrace.GUI.nonInteractive.PhotoFrame;
import optics.raytrace.GUI.sceneObjects.EditableArrow;
import optics.raytrace.GUI.sceneObjects.EditableCylinderLattice;
import optics.raytrace.GUI.sceneObjects.EditableIdealLensCloak;


/**
 * Based on NonInteractiveTIMMovieMaker.
 * 
 * Calculates a movie of the view from a position within an ideal-lens cloak.
 * 
 * @author Johannes Courtial
 */
public class FourPiLensViewFromPointWithinMovieMaker
{
	public static final double H_C_OVER_F = 0.1;
	public static final double H_C_OVER_H_1 = 0.9;
	public static final boolean BOTTOM_LENS_PRESENT = true;
	public static final int HORIZONTAL_ANGLE_OF_VIEW = 90;
	public static final boolean SHOW_EDGES = true;
	public static final double X_OFFSET = 0.0;
	public static final double Y_OFFSET = -0.05;
	public static final boolean LOOK_AT_CLOAK = false;

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
	@SuppressWarnings("unused")
	public static String getFilename(int frame)
	{
		return "IdealLensCloakViewFromPointWithinMovieMaker_"	// the name
				+ (BOTTOM_LENS_PRESENT?"":" without bottom lens")
				+ " hCOverf = " + H_C_OVER_F
				+ " hCOverh1 = " + H_C_OVER_H_1
				+ (X_OFFSET!=0.0?" x offset "+X_OFFSET:"")
				+ (Y_OFFSET!=0.0?" y offset "+Y_OFFSET:"")
				+ " horizontal angle of view " + HORIZONTAL_ANGLE_OF_VIEW
				+ (SHOW_EDGES?" with edges":"")
				+ (LOOK_AT_CLOAK?" looking at cloak":"")
				+ " " + (new DecimalFormat("000000")).format(frame)	// the number of the frame, converted into a string
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
		scene.addSceneObject(SceneObjectClass.getChequerboardFloor(-2, scene, studio));
				
		// add any other scene objects
		
		// Tim's head, behind the cloak
//		scene.addSceneObject(new EditableTimHead(
//				"Tim's head",
//				new Vector3D(6, 0, 5),
//				scene,
//				studio
//			));

		// the cylinder lattice from TIMInteractiveBits's populateSceneRelativisticEdition method
		double cylinderRadius = 0.02;

		// a cylinder lattice...
		scene.addSceneObject(new EditableCylinderLattice(
				"cylinder lattice",
				-2.5, 2.5, 6,	// x_min, x_max, no of cylinders => cylinders at x=+-0.5, +-1.5, +-2.5
				-1.5, 1.5, 4,	// y_min, y_max, no of cylinders => cylinders at y=-1.5, -0.5, +0.5, +1.5
				-2.5, 2.5, 6, // z_min, z_max, no of cylinders => cylinders at z=+-0.5, +-1.5, +-2.5
				cylinderRadius,
				scene,
				studio
				));		

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
			h = 0.5,	// height of the lens cloak
			h1 = h*1./3.,	// height of the lower inner vertex above the base
			h2 = h*2./3.,	// height of the upper inner vertex above the base
			hC = H_C_OVER_H_1*h1,	// height of the camera above the base
			h1E = hC / (H_C_OVER_H_1 - H_C_OVER_F);	// height of the lower inner vertex above the base, in EM space
		
		System.out.println("h1E="+h1E);
		
		// a lens-TO tetrahedron
		EditableIdealLensCloak editableIdealLensCloak = 
				new EditableIdealLensCloak(
						"Ideal-lens cloak",
						new Vector3D(X_OFFSET, Y_OFFSET, -hC),	// base centre
						new Vector3D(0, 1, 0),	// front direction
						new Vector3D(1, 0, 0),	// right direction
						new Vector3D(0, 0, 1),	// top direction
						h*2./3.,	// base radius; if this is 2/3 of height then tetrahedron is regular
						h,	// height
						h1,	// heightLowerInnerVertexP
						h2,	// heightUpperInnerVertexP
						h1E,	// heightLowerInnerVertexE
						0.96,	// interface transmission coefficient
						SHOW_EDGES,	// show frames
						0.01*h,	// frame radius
						SurfaceColour.GREY20_MATT,	// frame surface property
						// LOOK_AT_CLOAK,	// show placeholder surfaces
						(LOOK_AT_CLOAK?LensElementType.GLASS_PANE:LensElementType.IDEAL_THIN_LENS),
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
		
//		scene.addSceneObject(new EditableRayTrajectory(
//						"ray trajectory",
//						new Vector3D(0, -0.75, 10.01),	// start point
//						0,	// start time
//						new Vector3D(Math.cos(angle), Math.sin(angle), 0),	// initial direction
//						0.01,	// radius
//						new SurfaceColourLightSourceIndependent(DoubleColour.GREEN, true),
//						100,	// max trace level
//						true,	// reportToConsole
//						scene,
//						studio
//				)
//			);
//
//		scene.addSceneObject(new EditableRayTrajectory(
//				"ray trajectory",
//				new Vector3D(0, -0.75, 10.01),	// start point
//				0,	// start time
//				new Vector3D(-Math.cos(angle), -Math.sin(angle), 0),	// initial direction
//				0.01,	// radius
//				new SurfaceColourLightSourceIndependent(DoubleColour.RED, true),
//				100,	// max trace level
//				true,	// reportToConsole
//				scene,
//				studio
//		)
//	);

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

		// the azimuthal angle of the centre of the view
		double viewDirectionAngle = ((double)frame)/getNoOfFrames()*2.*Math.PI;
		Vector3D viewDirection = new Vector3D(Math.sin(viewDirectionAngle), 0, Math.cos(viewDirectionAngle));
		
		// something that we can focus on in any direction
		SceneObject focusScene = new Sphere(
				"focussing sphere",	// description
				new Vector3D(0, 0, 0),	// centre
				10,	// radius
				(SurfaceProperty)null,	// surfaceProperty
				null,	// parent
				Studio.NULL_STUDIO
			);
		
		EditableRelativisticAnyFocusSurfaceCamera camera;
		
		if(LOOK_AT_CLOAK)
		{
			// add a little sphere at the position where the camera will be otherwise located
			scene.addSceneObject(
					new EditableArrow(
							"Arrow pointing at camera position",	// description
							new Vector3D(0, h, 0),	// startPoint
							new Vector3D(0, 0, 0),	// endPoint
							0.02*h,	// shaftRadius
							0.1*h,	// tipLength
							MyMath.deg2rad(30),	// tip angle
							SurfaceColour.YELLOW_SHINY,	// surfaceProperty
							scene, 
							studio
						)
//					new EditableScaledParametrisedSphere(
//							"Sphere centred at camera position",	// description
//							new Vector3D(0, 0, 0),	// centre
//							0.05*h,	// radius
//							SurfaceColour.WHITER_SHINY,	// surfaceProperty
//							scene, 
//							studio
//						)
				);
			
			camera = new EditableRelativisticAnyFocusSurfaceCamera(
					"Camera",
					new Vector3D(5, .6, .4),	// centre of aperture
					new Vector3D(-.5, -.06, -.04),	// viewDirection
					new Vector3D(0, 1, 0),	// top direction vector
					20,	// horiontalViewAngle in degrees; 2*MyMath.rad2deg(Math.atan(2./10.)) gives same view angle as in previous version
					new Vector3D(0, 0, 0),	// beta
					pixelsX*antiAliasingFactor, pixelsY*antiAliasingFactor,	// logical number of pixels
					ExposureCompensationType.EC0,	// exposure compensation +0
					100,	// maxTraceLevel
					Plane.zPlane(
							"focus plane",	// description
							0,	// z0
							null,	// surfaceProperty
							null,	// parent
							null	// studio
						),
					null,	// cameraFrameScene,
					ApertureSizeType.PINHOLE,	// aperture size
					QualityType.RUBBISH,	// blur quality
					QualityType.GOOD	// anti-aliasing quality
					);
		}
		else
		{
			camera = new EditableRelativisticAnyFocusSurfaceCamera(
					"Camera",
					new Vector3D(0, 0, 0),	// centre of aperture
					viewDirection,	// viewDirection
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
		}

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
		
		for(int frame = 50;
				frame < getNoOfFrames();
				frame++)
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
