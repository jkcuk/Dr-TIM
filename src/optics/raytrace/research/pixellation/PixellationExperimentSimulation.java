package optics.raytrace.research.pixellation;

import java.awt.Container;

import math.*;
import optics.raytrace.surfaces.*;
import optics.raytrace.surfaces.GCLAsWithApertures.GCLAsTransmissionCoefficientCalculationMethodType;
import optics.raytrace.utility.CoordinateSystems.GlobalOrLocalCoordinateSystemType;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.GUI.cameras.EditableRelativisticAnyFocusSurfaceCamera;
import optics.raytrace.GUI.cameras.QualityType;
import optics.raytrace.GUI.lowLevel.ApertureSizeType;
import optics.raytrace.GUI.nonInteractive.PhotoCanvas;
import optics.raytrace.GUI.nonInteractive.PhotoFrame;
import optics.raytrace.GUI.sceneObjects.EditableFramedRectangle;
import optics.raytrace.GUI.sceneObjects.EditableParametrisedPlane;
import optics.raytrace.cameras.PinholeCamera.ExposureCompensationType;
import optics.raytrace.core.*;


/**
 * Simulate the effects of pixellation we observed experimentally on August 9 and 10, 2016
 * 
 * Note that in Project>Properties (or alternatively Run>Run Configurations...) the working
 * directory (which can be found in the arguments tab) has to be set to the directory
 * containing all the images, normally
 * ${workspace_loc:TIM/demo/}
 * 
 * Adapted from RayTraceJavaApplication.java
 */
public class PixellationExperimentSimulation
{   
	// a few units
	public static final double CM = 1e-2;
	public static final double MM = 1e-3;
	public static final double UM = 1e-6;
	public static final double NM = 1e-9;
	
	public static final boolean TEST = false;
	public static final boolean SAVE = true;
	
	// public static final boolean SHOW_SPHERE = true;
	public static final boolean SIMULATE_DIFFRACTIVE_BLUR = true;	// simulateDiffractiveBlur
	public static final boolean SIMULATE_RAY_OFFSET = true;	// simulateRayOffset
	public static final double PIXEL_SIDE_LENGTH_MM = 0.2;	// pixelSideLength
	public static final double LAMBDA_NM = 564.;	// lambda; 564nm is the wavelength at which the human eye is most sensitive -- see http://hypertextbook.com/facts/2007/SusanZhao.shtml
	public static final double WINDOW_Z_CM = 25;
	public static final double ARRAY_Z_CM = 30;
	public static final double STRIPE_WIDTH_MM = .10;



	/**
	 * Filename under which main saves the rendered image.
	 * Alter to save the rendered image under a different name.
	 * @return	the filename under which main saves the rendered image.
	 */
	public static String getFilename()
	{
		return "PixellationExperimentSimulation"
				+" diffractive blur "+(SIMULATE_DIFFRACTIVE_BLUR?"on":"off")
				+" ray offset "+(SIMULATE_RAY_OFFSET?"on":"off")
				+" pixel sidelength="+PIXEL_SIDE_LENGTH_MM+"mm"
				+" lambda="+LAMBDA_NM+"nm"
				+" z_window="+WINDOW_Z_CM+"cm"
				+" z_array="+ARRAY_Z_CM+"cm"
				+" stripe_width="+STRIPE_WIDTH_MM+"mm"
				+(TEST?" (test)":"")
				+".bmp";
	}

	/**
	 * Define scene, lights, and/or camera.
	 * @return the studio, i.e. scene, lights and camera
	 */
	public static Studio createStudio()
	{		
		Studio studio = new Studio();
		
		// the scene
		SceneObjectContainer scene = new SceneObjectContainer("the scene", null, studio);

		// the standard scene objects
		// scene.addSceneObject(SceneObjectClass.getSkySphere(scene, studio));	// the sky
		//scene.addSceneObject(SceneObjectClass.getChequerboardFloor(scene, studio));	// the checkerboard floor

		double cylinderRadius = 0.02*CM;

//		scene.addSceneObject(new EditableParametrisedPlane(
//				"chequerboard floor", 
//				new Vector3D(0, -.9*CM-cylinderRadius, 0),	// point on plane
//				new Vector3D(1, 0, 0),	// Vector3D 1 that spans plane
//				new Vector3D(0, 0, 1),	// Vector3D 2 that spans plane
//				// true,	// shadow-throwing
//				new SurfaceTiling(SurfaceColour.GREY90_SHINY, SurfaceColour.WHITE_SHINY, 1*CM, 1*CM),
//				scene,
//				studio
//		));


		//
		// add any other scene objects
		//
		
		// first, something to look at
		double stripeWidth = STRIPE_WIDTH_MM*MM;
		for(double x=-15*MM; x<=15*MM; x+=2*stripeWidth)
		{
			scene.addSceneObject(
					new EditableFramedRectangle(
							"glowing stripe",	// description
							new Vector3D(x-0.5*stripeWidth, -1*CM, ARRAY_Z_CM*CM),	// corner,
							new Vector3D(stripeWidth, 0, 0),	// widthVector,
							new Vector3D(0, 2*CM, 0),	// heightVector,
							0,	// radius
							SurfaceColourLightSourceIndependent.WHITE,	// windowSurfaceProperty,
							SurfaceColour.BLACK_MATT,	// frameSurfaceProperty,
							false,	// showFrames,
							scene,	// parent, 
							studio
					)
				);
		}		
		
//		scene.addSceneObject(new EditableCylinderLattice(
//				"cylinder lattice",
//				-1.1*CM, 1.1*CM, 4,
//				-0.9*CM, 0.9*CM, 4,
//				ARRAY_Z_CM*CM+cylinderRadius, ARRAY_Z_CM*CM + 30.*CM+cylinderRadius, 4,
//				cylinderRadius,
//				scene,
//				studio
//		));
		
			double windowWidth = 1*CM;
			
			scene.addSceneObject(new EditableFramedRectangle(
					"pixellated window",
					new Vector3D(-0.5*windowWidth, -0.5*windowWidth, WINDOW_Z_CM*CM),	// corner
					new Vector3D(windowWidth, 0, 0),	// width vector
					new Vector3D(0, windowWidth, 0),	// height vector
					cylinderRadius,	// frame radius
					new GCLAsWithApertures(
							new Vector3D(0, 0, 1),	// aHat
							new Vector3D(1, 0, 0),	// uHat
							new Vector3D(0, 1, 0),	// vHat
							1,	// etaU
							1,	// etaV
							0,	// deltaU
							0,	// deltaV
							GlobalOrLocalCoordinateSystemType.LOCAL_OBJECT_BASIS,	// basis
							0.9,	// constantTransmissionCoefficient
							GCLAsTransmissionCoefficientCalculationMethodType.CONSTANT,	// transmissionCoefficientMethod
							false,	// shadowThrowing
							PIXEL_SIDE_LENGTH_MM*MM,	// pixelSideLength
							LAMBDA_NM*NM,	// lambda
							SIMULATE_DIFFRACTIVE_BLUR,	// simulateDiffractiveBlur
							SIMULATE_RAY_OFFSET	// simulateRayOffset
						),
					SurfaceColour.GREY50_SHINY,	// frame surface
					true,	// show frame
					scene,
					studio
				));
		
		// define the camera
		//
		// Note that the view direction and basis Vector3Ds of the detector are chosen such that
		// the x, y, z axes form a LEFT-handed coordinate system.
		// The reason is that, in the photo, the positive x direction is then to the right,
		// the positive y direction is upwards, and the camera looks in the positive z direction.
		int
		pixelsX = 640,
		pixelsY = 480
		;
		// If antiAliasingFactor is set to N, the image is calculated at resolution
		// N*pixelsX x N*pixelsY.
		
		Vector3D apertureCentre = new Vector3D(0, 0, 0);
		Vector3D viewDirection = Vector3D.difference(new Vector3D(0, 0, 10*CM), apertureCentre);

		// a camera with a non-zero aperture size (so it simulates blur)
		EditableRelativisticAnyFocusSurfaceCamera camera = new EditableRelativisticAnyFocusSurfaceCamera(
				"Camera",
				apertureCentre,	// centre of aperture
				viewDirection,	// viewDirection
				new Vector3D(0, 1, 0),	// top direction vector
				5,	// horiontalViewAngle in degrees; 2*MyMath.rad2deg(Math.atan(2./10.)) gives same view angle as in previous version
				new Vector3D(0, 0, 0),	// beta
				pixelsX, pixelsY,	// logical number of pixels
				ExposureCompensationType.EC0,	// exposure compensation +0
				1000,	// maxTraceLevel
				new EditableParametrisedPlane(
						"focussing plane",
						new Vector3D(0, 0, 10*CM),	// point on plane
						viewDirection,	// normal to plane
						SurfaceColour.BLACK_SHINY,
						scene,
						studio
				),	// focusScene,
				new SceneObjectContainer("camera-frame scene", null, studio),	//cameraFrameScene,
				ApertureSizeType.PINHOLE,	// aperture size
				TEST?QualityType.BAD:QualityType.GREAT,	// blur quality
				TEST?QualityType.NORMAL:QualityType.GOOD	// QualityType.GREAT	// anti-aliasing quality
		);
		// camera.setApertureRadius(.1*MM);
		
		studio.setScene(scene);
		studio.setCamera(camera);		
		studio.setLights(LightSource.getStandardLightsFromBehind());
		
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
		studio.savePhoto(getFilename(), "bmp");

		// display the image on the screen
		container.add(new PhotoCanvas(studio.getPhoto()));
		container.validate();
	}
}

