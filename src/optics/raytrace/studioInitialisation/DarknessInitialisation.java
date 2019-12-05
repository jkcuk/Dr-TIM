package optics.raytrace.studioInitialisation;

import optics.raytrace.core.LightSource;
import optics.raytrace.core.Studio;
import optics.raytrace.core.StudioInitialisation;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;

/**
 * Bare-bones initialisation of the scene and lights.
 * @author johannes
 */
public class DarknessInitialisation extends StudioInitialisation
{

	@Override
	public String getDescription() {
		return "Darkness";
	}
	
	@Override
	public void initialiseSceneAndLights(SceneObjectContainer scene, Studio studio)
	{
		studio.setLights(LightSource.getStandardLightsFromBehind());
		
		scene.clear();
		
		// add nothing -- leave it nice and black
	}
}
