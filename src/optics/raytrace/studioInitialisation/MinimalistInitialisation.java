package optics.raytrace.studioInitialisation;

import math.MyMath;
import optics.raytrace.core.LightSource;
import optics.raytrace.core.SceneObjectClass;
import optics.raytrace.core.Studio;
import optics.raytrace.core.StudioInitialisation;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;

/**
 * Bare-bones initialisation of the scene and lights.
 * @author johannes
 */
public class MinimalistInitialisation extends StudioInitialisation
{
	private double floorY;
	
	@Override
	public String getDescription() {
		return "Minimalist (floor & sky)";
	}
	
	@Override
	public void initialiseSceneAndLights(SceneObjectContainer scene, Studio studio)
	{
		studio.setLights(LightSource.getStandardLightsFromBehind());
		
		scene.clear();
		
		// add the standard scene objects
		scene.addSceneObject(SceneObjectClass.getSkySphere(scene, studio));
		scene.addSceneObject(SceneObjectClass.getLighterChequerboardFloor(floorY, scene, studio));
	}
	
	public MinimalistInitialisation(double floorY)
	{
		this.floorY = floorY;
	}
	
	public MinimalistInitialisation()
	{
		this(-1-MyMath.TINY);
	}
}
