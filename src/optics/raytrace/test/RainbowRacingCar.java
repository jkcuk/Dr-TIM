package optics.raytrace.test;

import java.awt.*;

import math.*;
import optics.raytrace.sceneObjects.*;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.surfaces.Reflective;
import optics.raytrace.surfaces.SurfaceColour;
import optics.raytrace.surfaces.SurfaceColourTimeDependent;
import optics.raytrace.cameras.PinholeCamera.ExposureCompensationType;
import optics.raytrace.core.*;
import optics.raytrace.GUI.cameras.EditableRelativisticAnyFocusSurfaceCamera;
import optics.raytrace.GUI.cameras.QualityType;
import optics.raytrace.GUI.lowLevel.ApertureSizeType;
import optics.raytrace.GUI.nonInteractive.PhotoCanvas;
import optics.raytrace.GUI.nonInteractive.PhotoFrame;
import optics.raytrace.GUI.sceneObjects.EditableParametrisedCone;
import optics.raytrace.GUI.sceneObjects.EditableParametrisedCylinder;


/**
 * A rainbow racing car for Gregor.
 * 
 * Based on NonInteractiveTIM.
 * 
 * @author Johannes Courtial
 */
public class RainbowRacingCar
{
	
	/**
	 * @param centre
	 * @param axleDirection
	 * @param axleLength
	 * @param wheelRadius
	 * @param axleRadius
	 * @param parent
	 * @param studio
	 * @return a SceneObjectContainer containing cylinders representing two wheels and an axle
	 */
	private static SceneObjectContainer makeWheelsAndAxle(Vector3D centre, double steeringAngle, Vector3D axleDirection,
			Vector3D forward, double axleLength, double wheelRadius, double wheelWidth, double axleRadius,
			SceneObjectContainer parent, Studio studio)
	{
		SceneObjectContainer wheelsAndAxle = new SceneObjectContainer("wheels and axle", parent, studio);
		
		// the axle
		wheelsAndAxle.addSceneObject(new EditableParametrisedCylinder(
				"axle",	// description,
				Vector3D.sum(centre, axleDirection.getWithLength(-0.5*axleLength)),	// startPoint,
				Vector3D.sum(centre, axleDirection.getWithLength( 0.5*axleLength)),// endPoint,
				axleRadius,	// radius,
				Reflective.PERFECT_MIRROR,	// surfaceProperty,
				parent,	// parent, 
				studio
			));
		
		Vector3D wheelNormal = Vector3D.sum(
				axleDirection.getWithLength(Math.cos(steeringAngle)),
				forward.getWithLength(Math.sin(steeringAngle))
			);
		
		// the first wheel
		wheelsAndAxle.addSceneObject(new EditableParametrisedCylinder(
				"wheel 1",	// description,
				Vector3D.sum(centre, axleDirection.getWithLength(-0.48*axleLength+0.5*wheelWidth), wheelNormal.getWithLength(-0.5*wheelWidth)),	// startPoint,
				Vector3D.sum(centre, axleDirection.getWithLength(-0.48*axleLength+0.5*wheelWidth), wheelNormal.getWithLength( 0.5*wheelWidth)),	// endPoint,
				wheelRadius,	// radius,
				SurfaceColour.GREY50_MATT,	// surfaceProperty,
				parent,	// parent, 
				studio
			));
		
		// the second wheel
		wheelsAndAxle.addSceneObject(new EditableParametrisedCylinder(
				"wheel 2",	// description,
				Vector3D.sum(centre, axleDirection.getWithLength(0.48*axleLength+0.5*wheelWidth), wheelNormal.getWithLength(-0.5*wheelWidth)),	// startPoint,
				Vector3D.sum(centre, axleDirection.getWithLength(0.48*axleLength+0.5*wheelWidth), wheelNormal.getWithLength( 0.5*wheelWidth)),	// endPoint,
				wheelRadius,	// radius,
				SurfaceColour.GREY50_MATT,	// surfaceProperty,
				parent,	// parent, 
				studio
			));		
		
		return wheelsAndAxle;
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
// 		scene.addSceneObject(SceneObjectClass.getHeavenSphere(scene, studio));
		scene.addSceneObject(SceneObjectClass.getSkySphere(scene, studio));
		scene.addSceneObject(SceneObjectClass.getChequerboardFloor(0, scene, studio));
				
		// add any other scene objects
		
		Vector3D epiCentre = new Vector3D(0, 0, 10);
		Vector3D front = new Vector3D(0, 0, 1);
		Vector3D up = new Vector3D(0, 1, 0);
		Vector3D right = Vector3D.crossProduct(front, up);
		
		double length = 1;
		double height = 0.3*length;
		double wheelRadius = 0.2*length;
		
		double mainBodyRadius = 0.2*length;
		
		scene.addSceneObject(new EditableParametrisedCylinder(
			"main body",	// description,
			Vector3D.sum(epiCentre, up.getWithLength(height), front.getWithLength(0.5*length)),	// startPoint,
			Vector3D.sum(epiCentre, up.getWithLength(height), front.getWithLength(-0.5*length)),// endPoint,
			mainBodyRadius,	// radius,
			new SurfaceColourTimeDependent(0.1*length, true),	// surfaceProperty,
			scene,	// parent, 
			studio
		));

		scene.addSceneObject(new EditableParametrisedCone(
				"Spitze",	// description,
				Vector3D.sum(epiCentre, up.getWithLength(height), front.getWithLength(0.5*length+mainBodyRadius)),	// apex,
				front.getReverse(),	// axis,
				true,	// open,
				MyMath.deg2rad(45),	// theta,
				mainBodyRadius,	// height,
				new SurfaceColourTimeDependent(0.1*length, true),	// surfaceProperty,
				scene,	// parent, 
				studio
			));

		// the front wheels
		scene.addSceneObject(makeWheelsAndAxle(
				Vector3D.sum(epiCentre, front.getWithLength(0.4*length), up.getWithLength(wheelRadius)),	// centre,
				MyMath.deg2rad(20),	// steeringAngle
				right,	// axleDirection,
				front,	// forward direction
				1*length,	// axleLength,
				wheelRadius,	// wheelRadius,
				0.1*length,	// wheelWidth,
				0.03*length,	// axleRadius,
				scene,	// parent,
				studio));

		// the back wheels
		scene.addSceneObject(makeWheelsAndAxle(
				Vector3D.sum(epiCentre, front.getWithLength(-0.4*length), up.getWithLength(wheelRadius)),	// centre,
				0,	// steeringAngle
				right,	// axleDirection,
				front,	// forward direction
				1.3*length,	// axleLength,
				wheelRadius,	// wheelRadius,
				0.2*length,	// wheelWidth,
				0.03*length,	// axleRadius,
				scene,	// parent,
				studio));

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
		
		Vector3D viewCentre = Vector3D.sum(epiCentre, up.getWithLength(height));
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
