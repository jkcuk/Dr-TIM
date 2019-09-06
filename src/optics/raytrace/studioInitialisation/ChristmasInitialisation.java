package optics.raytrace.studioInitialisation;

import math.Vector3D;
import optics.raytrace.GUI.sceneObjects.EditableChristmasTree;
import optics.raytrace.GUI.sceneObjects.EditableParametrisedPlane;
import optics.raytrace.GUI.sceneObjects.EditableSantaSilhouette;
import optics.raytrace.core.LightSource;
import optics.raytrace.core.SceneObjectClass;
import optics.raytrace.core.Studio;
import optics.raytrace.core.StudioInitialisation;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.surfaces.Rainbow;

/**
 * Bare-bones initialisation of the scene and lights.
 * @author johannes
 */
public class ChristmasInitialisation extends StudioInitialisation
{
	@Override
	public String getDescription() {
		return "Christmas";
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
		
		// Christmas tree
		scene.addSceneObject(new EditableChristmasTree(
				"Christmas tree",	// description
				new Vector3D(0, -1, 10),	// baseCentre
				new Vector3D(0, 1, 10),	// tip
				35,	// branchLayerInclinationAngle
				5,	// noOfBranchLayers,
				0.2,	// trunkRadius
				new Vector3D(0, 0, -1),	// front
				true,	// showBaubles
				true,	// showFairyLights
				scene,
				studio
			));
		
		// Santa
		scene.addSceneObject(new EditableSantaSilhouette(
				"Santa Silhouette",	// description
				new Vector3D(-10, 5, 100),	// centre
				new Vector3D(1, 0, 0),	// rightDirection
				new Vector3D(0, 1, 0),	// upDirection
				20,	// width
				scene,
				studio
			));
		
		// a rainbow plane, which is also handy as invisible wall for creating random-dot stereograms
		EditableParametrisedPlane wall = new EditableParametrisedPlane(
				"Rainbow",
				new Vector3D(0, 0, 12),	// point on plane
				new Vector3D(0, 0, 1),	// normal
				new Rainbow(
						1,	// saturation
						.25,	// lightness
						new Vector3D(100,300,-500)	// lightSourcePosition
					),
				scene,
				studio
			);
		scene.addSceneObject(wall);
	}
}
