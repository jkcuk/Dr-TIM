package optics.raytrace.research.TO.idealLensCloak;

import java.awt.*;
import java.text.DecimalFormat;

import math.*;
import optics.raytrace.sceneObjects.Cylinder;
import optics.raytrace.sceneObjects.Sphere;
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
import optics.raytrace.GUI.sceneObjects.EditableArray;
import optics.raytrace.GUI.sceneObjects.EditableIdealLensCloak;
import optics.raytrace.GUI.sceneObjects.EditableParametrisedPlane;
import optics.raytrace.GUI.sceneObjects.EditableTimHead;


/**
 * 
 * @author Johannes Courtial
 */
public class LensTOTetrahedronCylinderGridMovie
{
	public static final boolean SHOW_TETRAHEDRON = true;
	public static final boolean SHOW_FRAMES = false;
	public static final boolean PLACEHOLDER_WINDOWS = false;
	public static final boolean SHOW_TIM = false;
	public static final boolean SPHERE_ARRAY = false;	// in focus plane
	public static final boolean CYLINDER_ARRAY = true;	// in focus plane
	public static final boolean TEST = false;

	/**
	 * @return	the number of frames in the movie
	 */
	public static int getNoOfFrames()
	{
		return 100;
	}

	/**
	 * Filename under which main saves the rendered image.
	 * Alter to save the rendered image under a different name.
	 * @return	the filename under which main saves the rendered image.
	 */
	public static String getFilename(int frame)
	{
		return "LensTOTetrahedron horizontal physical-space planes with"
				+(CYLINDER_ARRAY?" cylinder":"")
				+(SPHERE_ARRAY?" sphere":"")
				+(!SHOW_TETRAHEDRON?" (without tetrahedron)":"")
				+(SHOW_FRAMES?" (with frames)":"")
				+(PLACEHOLDER_WINDOWS?" (with placeholder windows)":"")
				+(SHOW_TIM?" (with Tim)":"")
				+(TEST?" (test)":"")
			    +(new DecimalFormat("000000")).format(frame)	// the number of the frame, converted into a string
				+".bmp";
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
		scene.addSceneObject(SceneObjectClass.getChequerboardFloor(scene, studio));	// the checkerboard floor
		scene.addSceneObject(SceneObjectClass.getSkySphere(scene, studio));	// the sky

		double frameRadius = 0.02;
		
		// the tetrahedral lens-TO device
		if(SHOW_TETRAHEDRON)
		scene.addSceneObject(new EditableIdealLensCloak(
				"Lens TO tetrahedron",
				new Vector3D(0, -1+frameRadius, 10),	// base centre
				new Vector3D(0, 0, -1),	// front direction
				new Vector3D(1, 0, 0),	// right direction
				new Vector3D(0, 1, 0),	// top direction
				2.*2./3,	// base radius
				2.0,	// height
				2*0.42855,	// heightLowerInnerVertexP
				2*0.75,	// heightUpperInnerVertexP
				2*0.2,	// heightLowerInnerVertexE
				0.9,	// interface transmission coefficient
				SHOW_FRAMES,	// show frames
				frameRadius,	// frame radius
				SurfaceColour.RED_SHINY,	// frame surface property
				// PLACEHOLDER_WINDOWS,	// show placeholder surfaces
				(PLACEHOLDER_WINDOWS?LensElementType.GLASS_PANE:LensElementType.IDEAL_THIN_LENS),	// lens-element type
				scene,
				studio	
			));
		
		// and something to look at
		if(SHOW_TIM)
		{
			scene.addSceneObject(new EditableTimHead(
					"Tim's head",	// description
					new Vector3D(0, 0, 13),	// centre
					1,	// radius
					new Vector3D(0, 0, -1),	// front direction
					new Vector3D(0, 1, 0),	// top direction
					new Vector3D(1, 0, 0),	// right direction
					scene,	// parent
					studio
				));
		}
		
		double arrayY = -1+frameRadius+2.0*frame/(getNoOfFrames()-1);
		
		SurfaceColour shadowlessWhite = new SurfaceColour(DoubleColour.WHITE, DoubleColour.WHITE, false);
		
		// here's an optional array of spheres, to see how the magnification varies
		if(SPHERE_ARRAY)
		{
			SceneObjectContainer arrayUnitCell = new SceneObjectContainer(
					"array unit cell",	// description
					scene,	// parent
					studio
				);
			arrayUnitCell.addSceneObject(new Sphere(
					"white sphere",	// description
					new Vector3D(0, 0, 0),	// centre
					0.03,	// radius
					shadowlessWhite,
					scene, 
					studio
				));
			scene.addSceneObject(
					new EditableArray(
							"sphere array",	// description
							-1, 1, 0.1,	// xMin, xMax, dx
							arrayY, arrayY, 1,	// 9, 11.5, 0.2,	// yMin, yMax, dy
							10-2.0*1./3., 10+2.0*2./3., 0.1,	// zMin, zMax, dz
							arrayUnitCell,
							scene, 
							studio
					)
				);
		}
		
		// a cylinder array in a y plane, for direct visualisation of the EM-space structure that corresponds to that plane
		if(CYLINDER_ARRAY)
		{
			double xMin = -1, xMax = 1, zMin = 10-2.0*1./3., zMax = 10+2.0*2./3., delta = 0.1;

			// add the horizontal cylinders
			for(double z=zMin; z<=zMax; z+=delta)
					scene.addSceneObject(new Cylinder(
									"horizontal cylinder, z="+z,	// description
									new Vector3D(xMin, arrayY, z),	// startPoint
									new Vector3D(xMax, arrayY, z),	// endPoint
									0.01,	// radius
									shadowlessWhite,	// surfaceProperty
									scene,	// parent
									studio
								));
			
			// add the vertical cylinders
			for(double x=xMin; x<=xMax; x+=delta)
				scene.addSceneObject(new Cylinder(
								"vertical cylinder, x="+x,	// description
								new Vector3D(x, arrayY, zMin),	// startPoint
								new Vector3D(x, arrayY, zMax),	// endPoint
								0.01,	// radius
								shadowlessWhite,	// surfaceProperty
								scene,	// parent
								studio
							));
		}

//		scene.addSceneObject(new Sphere(
//				"green sphere",	// description
//				new Vector3D(0, 0, 10),	// centre
//				1,	// radius
//				SurfaceColour.GREEN_SHINY,
//				scene, 
//				studio
//		));
	
		
		
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
		
		double angle = 2*Math.PI*312/500;
		Vector3D apertureCentre = Vector3D.sum(
				new Vector3D(0, 1.65, 10),
				Vector3D.X.getProductWith(10*Math.cos(angle)),
				Vector3D.Z.getProductWith(10*Math.sin(angle))
			);
		Vector3D viewDirection = Vector3D.difference(new Vector3D(0, 0, 10), apertureCentre);

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
				ApertureSizeType.PINHOLE,	// aperture size
				TEST?QualityType.BAD:QualityType.GREAT,	// blur quality
				TEST?QualityType.NORMAL:QualityType.GOOD	// QualityType.GREAT	// anti-aliasing quality
		);

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
