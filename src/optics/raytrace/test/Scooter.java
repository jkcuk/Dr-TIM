package optics.raytrace.test;

import java.awt.*;

import math.*;
import optics.raytrace.sceneObjects.*;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.surfaces.SurfaceColour;
import optics.raytrace.surfaces.SurfaceColourTimeDependent;
import optics.raytrace.cameras.PinholeCamera.ExposureCompensationType;
import optics.raytrace.core.*;
import optics.DoubleColour;
import optics.raytrace.GUI.cameras.EditableRelativisticAnyFocusSurfaceCamera;
import optics.raytrace.GUI.cameras.QualityType;
import optics.raytrace.GUI.lowLevel.ApertureSizeType;
import optics.raytrace.GUI.nonInteractive.PhotoCanvas;
import optics.raytrace.GUI.nonInteractive.PhotoFrame;
import optics.raytrace.GUI.sceneObjects.EditableParametrisedCylinder;
import optics.raytrace.GUI.sceneObjects.EditableScaledParametrisedCentredParallelogram;


/**
 * A scooter for Bea.
 * 
 * Based on NonInteractiveTIM.
 * 
 * @author Johannes Courtial
 */
public class Scooter
{
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
// 		scene.addSceneObject(SceneObjectClass.getHeavenSphere(scene, studio));
		scene.addSceneObject(SceneObjectClass.getSkySphere(scene, studio));
		scene.addSceneObject(SceneObjectClass.getChequerboardFloor(0, scene, studio));
				
		// add any other scene objects
		
		Vector3D epiCentre = new Vector3D(0, 0, 0);
		Vector3D front = new Vector3D(0, 0, 1);
		Vector3D up = new Vector3D(0, 1, 0);
		Vector3D right = Vector3D.crossProduct(front, up);
		
		double length = 1;
		double height = 1;
		double width = 0.3*length;
		double wheelWidth = 0.15*width;
		double wheelRadius = 0.1*length;
		double handleBarWidth = 0.6*length;
		double steeringAngle = MyMath.deg2rad(20);
		
		// the kickboard
		scene.addSceneObject(
				new EditableScaledParametrisedCentredParallelogram(
						"kickboard",	// description, 
						Vector3D.sum(epiCentre, up.getWithLength(wheelRadius)),	// centre, 
						front.getWithLength(length),	// spanVector1,
						right.getWithLength(0.2*length),	// spanVector2, 
						SurfaceColour.CYAN_SHINY,	// surfaceProperty,
						scene,	// parent,
						studio
		));
		
		Vector3D handleBarDirection = Vector3D.sum(
				right.getWithLength(Math.cos(steeringAngle)),
				front.getWithLength(Math.sin(steeringAngle))
			);
		
		// the front wheel
		scene.addSceneObject(new EditableParametrisedCylinder(
				"front wheel",	// description,
				Vector3D.sum(epiCentre, front.getWithLength(0.5*length), up.getWithLength(wheelRadius),	handleBarDirection.getWithLength(-0.5*wheelWidth)),	// startPoint,
				Vector3D.sum(epiCentre, front.getWithLength(0.5*length), up.getWithLength(wheelRadius),	handleBarDirection.getWithLength( 0.5*wheelWidth)),// endPoint,
				wheelRadius,	// radius,
				SurfaceColour.GREY50_MATT,	// surfaceProperty,
				scene,	// parent, 
				studio
			));

		// the back wheel
		scene.addSceneObject(new EditableParametrisedCylinder(
				"back wheel",	// description,
				Vector3D.sum(epiCentre, front.getWithLength(-0.5*length), up.getWithLength(wheelRadius), right.getWithLength(-0.5*wheelWidth)),	// startPoint,
				Vector3D.sum(epiCentre, front.getWithLength(-0.5*length), up.getWithLength(wheelRadius), right.getWithLength( 0.5*wheelWidth)),// endPoint,
				wheelRadius,	// radius,
				SurfaceColour.GREY50_MATT,	// surfaceProperty,
				scene,	// parent, 
				studio
			));

		// the cylinder holding the handle bars
		scene.addSceneObject(new EditableParametrisedCylinder(
				"handle bar holder tube",	// description,
				Vector3D.sum(epiCentre, front.getWithLength(0.5*length), up.getWithLength(wheelRadius)),	// startPoint,
				Vector3D.sum(epiCentre, front.getWithLength(0.5*length), up.getWithLength(wheelRadius+height)),// endPoint,
				0.55*wheelWidth,	// radius,
				new SurfaceColour(new DoubleColour(1, 0, 1), DoubleColour.WHITE, true),	// surfaceProperty,
				scene,	// parent, 
				studio
			));

		// handle bars
		scene.addSceneObject(new EditableParametrisedCylinder(
				"handle bars",	// description,
				Vector3D.sum(epiCentre, front.getWithLength(0.5*length), up.getWithLength(wheelRadius+height), handleBarDirection.getWithLength(-0.5*handleBarWidth)),	// startPoint,
				Vector3D.sum(epiCentre, front.getWithLength(0.5*length), up.getWithLength(wheelRadius+height), handleBarDirection.getWithLength( 0.5*handleBarWidth)),// endPoint,
				0.55*wheelWidth,	// radius,
				new SurfaceColourTimeDependent(0.1*length, true),
				// new SurfaceColour(new DoubleColour(0.3, 0.3, 1), DoubleColour.WHITE, true),	// surfaceProperty,
				scene,	// parent, 
				studio
			));

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
		
		Vector3D viewCentre = Vector3D.sum(epiCentre, up.getWithLength(0.5*(wheelRadius+height)));
		Vector3D viewDirection = new Vector3D(-1, -0.2, 0.1);
		double cameraDistance = 10;
		
		EditableRelativisticAnyFocusSurfaceCamera camera = new EditableRelativisticAnyFocusSurfaceCamera(
				"Camera",
				Vector3D.difference(viewCentre, viewDirection.getWithLength(cameraDistance)),	// centre of aperture
				viewDirection,	// viewDirection
				new Vector3D(0, 1, 0),	// top direction vector
				10,	// horiontalViewAngle in degrees; 2*MyMath.rad2deg(Math.atan(2./10.)) gives same view angle as in previous version
				new Vector3D(0, 0, 0),	// beta
				pixelsX*antiAliasingFactor, pixelsY*antiAliasingFactor,	// logical number of pixels
				ExposureCompensationType.EC0,	// exposure compensation +0
				100,	// maxTraceLevel
				new Plane(
						"focussing plane",	// description
						viewCentre,	// point on plane
						viewDirection,	// normal
						null,	// surface property
						null,	// parent
						Studio.NULL_STUDIO
					),
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
		
		// define scene, lights and camera
		Studio studio = createStudio();

		// do the ray tracing
		studio.takePhoto();

		// display the image on the screen
		container.removeAll();
		container.add(new PhotoCanvas(studio.getPhoto()));
		container.validate();
	}
}
