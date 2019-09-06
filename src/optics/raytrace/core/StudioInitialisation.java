package optics.raytrace.core;

import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;

public abstract class StudioInitialisation
{
	public abstract String getDescription();
	
	/**
	 * Set standard lights and initialise scene with the bare minimum.
	 * Override to change.
	 * @param sceneObjectContainer
	 * @param studio
	 */
	public abstract void initialiseSceneAndLights(SceneObjectContainer scene, Studio studio);
}
