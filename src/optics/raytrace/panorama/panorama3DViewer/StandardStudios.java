package optics.raytrace.panorama.panorama3DViewer;

import math.Vector3D;
import optics.DoubleColour;
import optics.raytrace.GUI.sceneObjects.EditableCylinderLattice;
import optics.raytrace.GUI.sceneObjects.EditableParametrisedCylinder;
import optics.raytrace.GUI.sceneObjects.EditableScaledParametrisedSphere;
import optics.raytrace.core.LightSource;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.SceneObjectClass;
import optics.raytrace.core.Studio;
import optics.raytrace.lights.AmbientLight;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.surfaces.SurfaceColour;
import optics.raytrace.surfaces.SurfaceTiling;

public class StandardStudios
{
	public static Studio getStandardSceneStudio(double floorDistance)
	{
		Studio studio = new Studio();

		// the scene
		SceneObjectContainer scene = new SceneObjectContainer("the scene", null, studio);

		// the standard scene objects
		scene.addSceneObject(SceneObjectClass.getSkySphere(scene, studio));	// the sky
		scene.addSceneObject(SceneObjectClass.getChequerboardFloor(-floorDistance, scene, studio));	// the checkerboard floor

		//
		// add any other scene objects
		//
				
		scene.addSceneObject(new EditableScaledParametrisedSphere(
				"sphere",	// description,
				new Vector3D(0, 0, 10),	// centre,
				0.5,	// radius,
				new Vector3D(0.5, 1, 1),	// pole,
				new Vector3D(1, 0, 0),	// phi0Direction,
				0, 5,	// sThetaMin, sThetaMax,
				0, 10,	// sPhiMin, sPhiMax,
				SurfaceColour.BLUE_SHINY,	// surfaceProperty,
				scene,	// parent, 
				studio
		));

		// TIM's head doesn't work, for some reason
//		scene.addSceneObject(new EditableTimHead(
//				"TIM's head",
//				new Vector3D(0, 0, 10),
//				scene,
//				studio));
		
		scene.addSceneObject(new EditableCylinderLattice(
				"cylinder lattice",
				-20, 20, 4,
				// -.9, 5, 4,
				// -floorDistance, 40+floorDistance, 4,
				-20, 20, 8,
				-20, 20, 6,
				0.1,
				scene,
				studio
		));
		
		scene.addSceneObject(new EditableScaledParametrisedSphere(
				"nearby sphere to the right",	// description,
				new Vector3D(3, 0, 0),	// centre,
				.5,	// radius,
				new Vector3D(0.5, 1, 1),	// pole,
				new Vector3D(1, 0, 0),	// phi0Direction,
				0, 5,	// sThetaMin, sThetaMax,
				0, 10,	// sPhiMin, sPhiMax,
				SurfaceColour.GREEN_MATT,	// surfaceProperty,
				scene,	// parent, 
				studio
		));
		
		// ... and a cylinder
		scene.addSceneObject(new EditableParametrisedCylinder(
				"cylinder",	// description,
				new Vector3D(-5, -0.8, -5),	// startPoint,
				new Vector3D(-5, -0.8, -1000),	// endPoint,
				0.2,	// radius,
				new SurfaceTiling(SurfaceColour.RED_SHINY, SurfaceColour.GREY90_SHINY, 0.2, Math.PI/5),	// surfaceProperty,
				scene,	// parent, 
				studio
			));
		
		scene.addSceneObject(new EditableScaledParametrisedSphere(
				"sphere above the camera",	// description,
				new Vector3D(0, 5, 0),	// centre,
				.5,	// radius,
				new Vector3D(0.5, 1, 1),	// pole,
				new Vector3D(1, 0, 0),	// phi0Direction,
				0, 5,	// sThetaMin, sThetaMax,
				0, 10,	// sPhiMin, sPhiMax,
				SurfaceColour.GREEN_MATT,	// surfaceProperty,
				scene,	// parent, 
				studio
		));

//		scene.addSceneObject(new EditableParametrisedCylinder(
//				"cylinder below camera",	// description,
//				new Vector3D(-5, 0, -4),	// startPoint,
//				new Vector3D( 5, -floorDistance, 6),	// endPoint,
//				0.2,	// radius,
//				new SurfaceTiling(SurfaceColour.WHITE_SHINY, SurfaceColour.BLUE_SHINY, 0.2, Math.PI/5),	// surfaceProperty,
//				scene,	// parent, 
//				studio
//			));

//		scene.addSceneObject(new EditableScaledParametrisedSphere(
//				"sphere below the camera",	// description,
//				new Vector3D(0, -0.5*floorDistance+.5, 0),	// centre,
//				.5,	// radius,
//				new Vector3D(0.5, 1, 1),	// pole,
//				new Vector3D(1, 0, 0),	// phi0Direction,
//				0, 5,	// sThetaMin, sThetaMax,
//				0, 10,	// sPhiMin, sPhiMax,
//				SurfaceColour.RED_SHINY,	// surfaceProperty,
//				scene,	// parent, 
//				studio
//		));

		studio.setScene(scene);
		
		studio.setLights(LightSource.getStandardLightsFromBehind());
		
		return studio;
	}
	
	public static Studio getInsideSphereStudio(double sphereRadius)
	{
		Studio studio = new Studio();

		// the scene
		SceneObjectContainer scene = new SceneObjectContainer("the scene", null, studio);

		// inside of a sphere

		// the interesting objects: a couple of spheres...
		SceneObject theSphere = new EditableScaledParametrisedSphere(
				"sphere around origin",	// description,
				new Vector3D(0, 0, 0),	// centre,
				sphereRadius,	// radius,
				new Vector3D(0, 1, 0),	// pole,
				new Vector3D(1, 0, 0),	// phi0Direction,
				0, 18,	// sThetaMin, sThetaMax,
				0, 36,	// sPhiMin, sPhiMax,
				new SurfaceTiling(SurfaceColour.BLACK_SHINY, SurfaceColour.GREY90_SHINY, 1, 1),	// surfaceProperty,
				scene,	// parent, 
				studio
				);

		scene.addSceneObject(theSphere);

		studio.setScene(scene);

		// set the lights such that the ambient light is strong enough
		studio.setLights(new AmbientLight("ambient light", DoubleColour.GREY60));

		return studio;
	}
}
