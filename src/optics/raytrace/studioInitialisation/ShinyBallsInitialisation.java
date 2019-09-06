package optics.raytrace.studioInitialisation;

import math.Vector3D;
import optics.raytrace.GUI.sceneObjects.EditableScaledParametrisedSphere;
import optics.raytrace.core.LightSource;
import optics.raytrace.core.SceneObjectClass;
import optics.raytrace.core.Studio;
import optics.raytrace.core.StudioInitialisation;
import optics.raytrace.core.SurfacePropertyPrimitive;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.surfaces.Reflective;

/**
 * Shiny-balls initialisation of the scene and lights.
 * @author johannes
 */
public class ShinyBallsInitialisation extends StudioInitialisation
{
	@Override
	public String getDescription() {
		return "Shiny balls";
	}
	
	@Override
	public void initialiseSceneAndLights(SceneObjectContainer scene, Studio studio)
	{
		studio.setLights(LightSource.getStandardLightsFromBehind());

		scene.clear();
		
		// the standard scene objects
		scene.addSceneObject(SceneObjectClass.getSkySphere(scene, studio));
		scene.addSceneObject(SceneObjectClass.getLighterChequerboardFloor(scene, studio));

		// add any other scene objects
		
		scene.addSceneObject(new EditableScaledParametrisedSphere(
				"shiny sphere 1",
				new Vector3D(-1, 0, 15),	// centre
				1,	// radius
				new Reflective(SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT, true),
				scene,
				studio
		));
		scene.addSceneObject(new EditableScaledParametrisedSphere(
				"shiny sphere 2",
				new Vector3D(1, 0, 15),	// centre
				1,	// radius
				new Reflective(SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT, true),
				scene,
				studio
		));
		scene.addSceneObject(new EditableScaledParametrisedSphere(
				"shiny sphere 3",
				new Vector3D(0, Math.sqrt(3.), 15),	// centre
				1,	// radius
				new Reflective(SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT, true),
				scene,
				studio
		));
	}
}
