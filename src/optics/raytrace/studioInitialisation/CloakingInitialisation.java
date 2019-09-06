package optics.raytrace.studioInitialisation;

import math.Vector3D;
import optics.DoubleColour;
import optics.raytrace.GUI.sceneObjects.EditableBoxCloak;
import optics.raytrace.GUI.sceneObjects.EditableScaledParametrisedSphere;
import optics.raytrace.GUI.sceneObjects.EditableTimHead;
import optics.raytrace.core.LightSource;
import optics.raytrace.core.SceneObjectClass;
import optics.raytrace.core.Studio;
import optics.raytrace.core.StudioInitialisation;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.surfaces.SurfaceColour;

/**
 * Bare-bones initialisation of the scene and lights.
 * @author johannes
 */
public class CloakingInitialisation extends StudioInitialisation
{
	@Override
	public String getDescription() {
		return "Cloaking";
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
		
		// first create a non-shadow-throwing surface property for the sphere
		
		// a cloaked green sphere
		scene.addSceneObject(new EditableScaledParametrisedSphere(
				"sphere without shadow",	// description
				new Vector3D(0, 0, 10),	// centre,
				0.5,	// radius,
				new Vector3D(0, 1, 0),	// pole,
				new Vector3D(1, 0, 0),	// phi0Direction,
				0, 1,	// sThetaMin, sThetaMax,
				0, 1,	// sPhiMin, sPhiMax,
				new SurfaceColour(DoubleColour.WHITE, DoubleColour.BLACK, false),	// surfaceProperty,
				scene,	// parent, 
				studio
			));
		
		// Tim's head, behind the cloak
		scene.addSceneObject(new EditableTimHead(
				"Tim's head",
				new Vector3D(0, 0, 15),
				1,	// radius
				new Vector3D(0, 0, -1),	// front direction
				new Vector3D(0, 1, 0),	// top direction
				new Vector3D(1, 0, 0),	// right direction
				scene,
				studio
			));

		// a cloak
		scene.addSceneObject(
			new EditableBoxCloak(
			// new EditableGCLAsCloak(
				scene,	// parent,
				studio
			));
	}
}
