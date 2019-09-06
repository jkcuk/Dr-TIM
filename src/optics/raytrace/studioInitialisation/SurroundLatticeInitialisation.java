package optics.raytrace.studioInitialisation;

import optics.raytrace.GUI.sceneObjects.EditableCylinderLattice;
import optics.raytrace.core.LightSource;
import optics.raytrace.core.SceneObjectClass;
import optics.raytrace.core.Studio;
import optics.raytrace.core.StudioInitialisation;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;

/**
 * Initialisation of the scene and lights such that the origin is inside a cylinder lattice.
 * @author johannes
 */
public class SurroundLatticeInitialisation extends StudioInitialisation
{
	@Override
	public String getDescription() {
		return "Lattice surrounding camera (useful for relativistic effects)";
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
		
		double cylinderRadius = 0.02;
		
		// a cylinder lattice...
		scene.addSceneObject(new EditableCylinderLattice(
				"cylinder lattice",
				-1.5, 1.5, 4,	// x_min, x_max, no of cylinders => cylinders at x=-1.5, -0.5, +0.5, +1.5
				-1.5, 1.5, 4,	// y_min, y_max, no of cylinders => cylinders at y=-1.5, -0.5, +0.5, +1.5
				-1, 10, 12, // z_min, z_max, no of cylinders => cylinders at z=-1, 0, 1, 2, ..., 10
				cylinderRadius,
				scene,
				studio
		));		
	}
}
