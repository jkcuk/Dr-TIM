package optics.raytrace.research.FOVSteering;

import java.awt.*;

import math.*;
import optics.raytrace.sceneObjects.ParametrisedCylinderMantle;
import optics.raytrace.sceneObjects.ParametrisedPlane;
import optics.raytrace.sceneObjects.ParametrisedSphere;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectIntersection;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectIntersection2;
import optics.raytrace.surfaces.Refractive;
import optics.raytrace.surfaces.Striped;
import optics.raytrace.surfaces.SurfaceColour;
import optics.raytrace.surfaces.SurfaceOfTintedSolid;
import optics.raytrace.surfaces.SurfacePropertyAverage;
import optics.raytrace.surfaces.SurfacePropertyLayerStack;
import optics.raytrace.cameras.PinholeCamera.ExposureCompensationType;
import optics.raytrace.core.*;
import optics.DoubleColour;
import optics.raytrace.GUI.cameras.EditableRelativisticAnyFocusSurfaceCamera;
import optics.raytrace.GUI.cameras.QualityType;
import optics.raytrace.GUI.lowLevel.ApertureSizeType;
import optics.raytrace.GUI.nonInteractive.PhotoCanvas;
import optics.raytrace.GUI.nonInteractive.PhotoFrame;
import optics.raytrace.GUI.sceneObjects.EditableParametrisedCylinder;
import optics.raytrace.GUI.sceneObjects.EditableParametrisedPlane;
import optics.raytrace.GUI.sceneObjects.EditableSceneObjectCollection;


/**
 * Show an off-centre part of a lens.
 * Set TEST to true to calculate the image with "bad" blur quality; false for "great" blur quality.
 * 
 * Derived from @see optics.raytrace.NonInteractiveTIM
 * 
 * @author Johannes Courtial
 */
public class FOVSteeringOffCentrePartOfLens
{
	public static final boolean TEST = false;
	public static final boolean SHOW_OPTICAL_AXIS = false;
	public static final boolean SHOW_ENTIRE_LENS = true;

	/**
	 * Filename under which main saves the rendered image.
	 * Alter to save the rendered image under a different name.
	 * @return	the filename under which main saves the rendered image.
	 */
	public static String getFilename()
	{
		return "FOVSteeringOffCentrePartOfLens3"+".bmp";
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
		scene.addSceneObject(SceneObjectClass.getLighterChequerboardFloor(scene, studio));	// the checkerboard floor
		scene.addSceneObject(SceneObjectClass.getSkySphere(scene, studio));	// the sky

		// characterise the lens in terms of its radius of curvature, ...
		double radiusOfCurvature = 8;
		// ... its aperture radius, ...
		double apertureRadius = 1;
		// ... and the thickness of its sides
		double sideThickness = 0.05;
		
		// from that, calculate the distance from the central plane of the centre of the spherical surface
		double centreDistance = Math.sqrt(radiusOfCurvature*radiusOfCurvature-apertureRadius*apertureRadius)-0.5*sideThickness;
		
		
		// make part of a lens
		double squareCentreOffsetX = 0.*apertureRadius; 
		double squareCentreOffsetY = -0.5*apertureRadius;	// +0.5*apertureRadius for top part, -0.5*aperture radius for bottom part
		double squareWidth = 0.7*apertureRadius;
		
		SceneObjectIntersection partOfLens = new SceneObjectIntersection(
				"Part of lens with square aperture",	// description
				scene,	// parent
				studio
			);
		SurfaceOfTintedSolid tinted = new SurfaceOfTintedSolid(
				DoubleColour.complementaryColour(DoubleColour.CYAN),
				false
			);
		Refractive refractive = new Refractive(1.4, 1, true);
		SurfaceProperty partOfLensSurface =
			// SurfaceColour.CYAN_SHINY;
				// ColourFilter.CYAN;
				// new ColourFilter(DoubleColour.CYAN, false);
				new SurfacePropertyLayerStack(
						// new Refractive_Old(1.4, true),
						refractive,
						tinted
						// new ColourFilter(DoubleColour.CYAN.getSqrt(), false)
					);
		partOfLens.addSceneObject(
				new ParametrisedSphere(
						"Front spherical surface",	// description
						new Vector3D(0, 0, -centreDistance),	// c
						radiusOfCurvature+MyMath.TINY,	// r
						partOfLensSurface,	// surfaceProperty
						partOfLens,	// parent
						studio
				));
		partOfLens.addSceneObject(
				new ParametrisedSphere(
						"Back spherical surface",	// description
						new Vector3D(0, 0, centreDistance),	// c
						radiusOfCurvature+MyMath.TINY,	// r
						partOfLensSurface,	// surfaceProperty
						partOfLens,	// parent
						studio
				));
		if(SHOW_ENTIRE_LENS)
		{
			partOfLens.addSceneObject(
					new ParametrisedCylinderMantle(
					"Edge of lens",	// description
					new Vector3D(0, 0, -centreDistance),	// startPoint
					new Vector3D(0, 0, centreDistance),	// endPoint
					apertureRadius,	// radius,
					partOfLensSurface,	// surfaceProperty
					partOfLens,	// parent
					studio
					));
		}
		else
		{
			partOfLens.addSceneObject(
					new ParametrisedPlane(
							"Right",	// description
							new Vector3D(squareCentreOffsetX+0.5*squareWidth, 0, 0),	// pointOnPlane
							Vector3D.X,	// normal
							partOfLensSurface,	// tinted,	// surfaceProperty
							partOfLens,	// parent
							studio
							));
			partOfLens.addSceneObject(
					new ParametrisedPlane(
							"Left",	// description
							new Vector3D(squareCentreOffsetX-0.5*squareWidth, 0, 0),	// pointOnPlane
							Vector3D.X.getReverse(),	// normal
							partOfLensSurface,	// tinted,	// surfaceProperty
							partOfLens,	// parent
							studio
							));
			partOfLens.addSceneObject(
					new ParametrisedPlane(
							"Top",	// description
							new Vector3D(0, squareCentreOffsetY+0.5*squareWidth, 0),	// pointOnPlane
							Vector3D.Y,	// normal
							partOfLensSurface,	// tinted,	// surfaceProperty
							partOfLens,	// parent
							studio
							));
			partOfLens.addSceneObject(
					new ParametrisedPlane(
							"Bottom",	// description
							new Vector3D(0, squareCentreOffsetY-0.5*squareWidth, 0),	// pointOnPlane
							Vector3D.Y.getReverse(),	// normal
							partOfLensSurface,	// tinted,	// surfaceProperty
							partOfLens,	// parent
							studio
							));
		}

		scene.addSceneObject(partOfLens);

		
		// make a "complete" lens
		SceneObjectIntersection2 completeLens = new SceneObjectIntersection2(
				"Complete lens",	// description
				scene,	// parent
				studio
			);

		SurfaceProperty completeLensSurface =
				// new SemiTransparent(SurfaceColour.CYAN_SHINY, 0.99);
				// new SemiTransparent(SurfaceColour.GREY10_SHINY, 0.8);
				// new ColourFilter(DoubleColour.CYAN, false);
				// new ColourFilter(DoubleColour.GREY60, false);
				// new Refractive(1.4, 0.96, true);
//				new SurfaceOfTintedSolid(
//						DoubleColour.complementaryColour(DoubleColour.GREY10.getSqrt()),
//						false
//					);
				new SurfacePropertyAverage(
						new DoubleColour(0.01, 0.01, 0.01),	// diffuseColour
						DoubleColour.WHITE,	// specularColour
						0.8,	// transmissionCoefficient
						0.1,	// reflectionCoefficient
						false	// shadowThrowing
					);
				// refractive;


		
		completeLens.addPositiveSceneObject(
				new ParametrisedSphere(
						"First spherical surface",	// description
						new Vector3D(0, 0, -centreDistance),	// c
						radiusOfCurvature,	// r
						completeLensSurface,	// surfaceProperty
						completeLens,	// parent
						studio
				));
		completeLens.addPositiveSceneObject(
				new ParametrisedSphere(
						"Second spherical surface",	// description
						new Vector3D(0, 0, centreDistance),	// c
						radiusOfCurvature,	// r
						completeLensSurface,	// surfaceProperty
						completeLens,	// parent
						studio
				));
		completeLens.addNegativeSceneObject(partOfLens);
		completeLens.addPositiveSceneObject(
				new ParametrisedCylinderMantle(
				"Edge of lens",	// description
				new Vector3D(0, 0, -centreDistance),	// startPoint
				new Vector3D(0, 0, centreDistance),	// endPoint
				apertureRadius,	// radius,
				completeLensSurface,	// surfaceProperty
				completeLens,	// parent
				studio
				));
		
		scene.addSceneObject(completeLens, !SHOW_ENTIRE_LENS);
		
		// optical axis
		EditableParametrisedCylinder opticalAxis = new EditableParametrisedCylinder(
				"Optical axis",	// description
				new Vector3D(0, 0, -1000),	// startPoint
				new Vector3D(0, 0,  1000),	// endPoint
				0.01,	// radius
				new Striped(SurfaceColour.BLACK_SHINY, SurfaceColour.WHITE_SHINY, 0.1),	// surfaceProperty
				scene,
				studio
		);
		scene.addSceneObject(opticalAxis, SHOW_OPTICAL_AXIS);
		
		
		// define the camera
		//
		// Note that the view direction and basis Vector3Ds of the detector are chosen such that
		// the x, y, z axes form a LEFT-handed coordinate system.
		// The reason is that, in the photo, the positive x direction is then to the right,
		// the positive y direction is upwards, and the camera looks in the positive z direction.
		int
		pixelsX = 640,
		pixelsY = 480;
		
		Vector3D viewDirection = 
				// new Vector3D(0, -.2, 1);
				new Vector3D(.9, -0.33, 1);
		double cameraDistance = 10;
		
		EditableRelativisticAnyFocusSurfaceCamera camera = new EditableRelativisticAnyFocusSurfaceCamera(
				"Camera",
				viewDirection.getWithLength(-cameraDistance),	// centre of aperture
				viewDirection,	// viewDirection
				new Vector3D(0, 1, 0),	// top direction vector
				20,	// horiontalViewAngle in degrees; 2*MyMath.rad2deg(Math.atan(2./10.)) gives same view angle as in previous version
				new Vector3D(0, 0, 0),	// beta
				pixelsX, pixelsY,	// logical number of pixels
				ExposureCompensationType.EC0,	// exposure compensation +0
				200,	// maxTraceLevel
				new EditableParametrisedPlane(
						"focussing plane",
						new Vector3D(0, 0, 0),	// point on plane
						viewDirection,	// normal to plane
						SurfaceColour.BLACK_SHINY,
						scene,
						studio
				),	// focusScene,
				new EditableSceneObjectCollection("camera-frame scene", false, scene, studio),	//cameraFrameScene,
				ApertureSizeType.MEDIUM,	// aperture size
				TEST?QualityType.BAD:QualityType.GREAT,	// blur quality
				TEST?QualityType.BAD:QualityType.GREAT	// anti-aliasing quality
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
