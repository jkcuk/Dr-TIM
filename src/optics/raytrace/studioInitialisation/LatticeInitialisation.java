package optics.raytrace.studioInitialisation;

import optics.raytrace.GUI.sceneObjects.EditableCylinderLattice;
import optics.raytrace.core.LightSource;
import optics.raytrace.core.SceneObjectClass;
import optics.raytrace.core.Studio;
import optics.raytrace.core.StudioInitialisation;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;

/**
 * Initialisation of the scene and lights to a sensibly lit cylinder lattice.
 * @author johannes
 */
public class LatticeInitialisation extends StudioInitialisation
{
	@Override
	public String getDescription() {
		return "Lattice";
	}
	
	@Override
	public void initialiseSceneAndLights(SceneObjectContainer scene, Studio studio)
	{
		studio.setLights(LightSource.getStandardLightsFromBehind());

		scene.clear();
		
		// the standard scene objects
		scene.addSceneObject(SceneObjectClass.getSkySphere(scene, studio));
		scene.addSceneObject(SceneObjectClass.getLighterChequerboardFloor(scene, studio));

		double cylinderRadius = 0.02;
		
		// a cylinder lattice...
		scene.addSceneObject(new EditableCylinderLattice(
				"cylinder lattice",
				-1, 1, 4,
				-1+cylinderRadius, 1+cylinderRadius, 4,
				10, 40, 4, // this puts the "transverse" cylinders into the planes z=10, 20, 30, 40
				cylinderRadius,
				scene,
				studio
		));		
	}
}
