package optics.raytrace.studioInitialisation;

import optics.raytrace.core.LightSource;
import optics.raytrace.core.SceneObjectClass;
import optics.raytrace.core.Studio;
import optics.raytrace.core.StudioInitialisation;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;

/**
 * Bare-bones initialisation of the scene and lights.
 * @author johannes
 */
public class HeavenInitialisation extends StudioInitialisation
{
//	private String description;
//	
//	public HeavenInitialisation()
//	{
//		super();
//		description = "Heaven";
//	}
//
//	public HeavenInitialisation(String description)
//	{
//		super();
//		this.description = description;
//	}

	@Override
	public String getDescription() {
//		return description;
		return "Heaven";
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
