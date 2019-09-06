package optics.raytrace.studioInitialisation;

import optics.raytrace.core.LightSource;
import optics.raytrace.core.SceneObjectClass;
import optics.raytrace.core.Studio;
import optics.raytrace.core.StudioInitialisation;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;

/**
 * Bare-bones initialisation of the scene and lights, designed for custom objects to be added.
 * @author johannes
 */
public class CustomInitialisation extends StudioInitialisation
{
	@Override
	public String getDescription()
	{
		return "Custom";
	}
	
	@Override
	public void initialiseSceneAndLights(SceneObjectContainer scene, Studio studio)
	{
		studio.setLights(LightSource.getStandardLightsFromBehind());
		
		scene.clear();
		
		// add the standard scene objects
		scene.addSceneObject(SceneObjectClass.getHeavenSphere(scene, studio));
	}
}
