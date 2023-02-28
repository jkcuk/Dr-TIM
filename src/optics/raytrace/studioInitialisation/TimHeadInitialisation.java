package optics.raytrace.studioInitialisation;

import math.Vector3D;
import optics.raytrace.GUI.sceneObjects.EditableParametrisedPlane;
import optics.raytrace.GUI.sceneObjects.EditableTimHead;
import optics.raytrace.core.LightSource;
import optics.raytrace.core.SceneObjectClass;
import optics.raytrace.core.Studio;
import optics.raytrace.core.StudioInitialisation;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.surfaces.Transparent;

/**
 * Initialisation of the scene and lights.
 * The scene then contains Tim's head.
 * @author johannes
 */
public class TimHeadInitialisation extends StudioInitialisation
{
	@Override
	public String getDescription() {
		return "Tim's head";
	}
	
	@Override
	public void initialiseSceneAndLights(SceneObjectContainer scene, Studio studio)
	{
		studio.setLights(LightSource.getStandardLightsFromBehind());

		scene.clear();
		
		// the standard scene objects
		scene.addSceneObject(SceneObjectClass.getSkySphere(scene, studio));
		scene.addSceneObject(SceneObjectClass.getLighterChequerboardFloor(scene, studio));

//		scene.addSceneObject(new EditableScaledParametrisedSphere(
//				"sky",
//				new Vector3D(0,0,0),	// centre
//				MyMath.HUGE,	// huge radius
//				new SurfaceColourLightSourceIndependent(DoubleColour.LIGHT_BLUE, true),
//				scene, 
//				studio
//		));
//
//		scene.addSceneObject(new EditableParametrisedPlane(
//				"chequerboard floor", 
//				new Vector3D(0, -1, 0),	// point on plane
//				new Vector3D(0, 0, 1),	// Vector3D 1 that spans plane
//				new Vector3D(1, 0, 0),	// Vector3D 2 that spans plane
//				// true,	// shadow-throwing
//				SurfacePropertyPanel.TILED, // new SurfaceTiling(SurfaceColour.GREY80_SHINY, SurfaceColour.WHITE_SHINY, 1, 1)
//				scene,
//				studio
//		));
		
		// add any other scene objects
		
		// Tim's head
		scene.addSceneObject(new EditableTimHead(
				"Tim's head",
				new Vector3D(0, 0, 10),
				1,	// radius
				new Vector3D(0, 0, -1),	// front direction
				new Vector3D(0, 1, 0),	// top direction
				new Vector3D(1, 0, 0),	// right direction
				scene,
				studio
			));
		
		// ... standing in front of an invisible wall for the autostereogram.
		EditableParametrisedPlane wall = new EditableParametrisedPlane(
				"invisible wall",
				new Vector3D(0, 0, 12),	// point on plane
				new Vector3D(0, 0, 1),	// normal
				Transparent.PERFECT,
				scene,
				studio
			);
		scene.addSceneObject(wall);
	}
}
