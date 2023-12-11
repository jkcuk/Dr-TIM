package optics.raytrace.studioInitialisation;

import math.MyMath;
import math.Vector3D;
import optics.raytrace.GUI.sceneObjects.EditableSky;
import optics.raytrace.GUI.sceneObjects.EditableTimHead;
import optics.raytrace.GUI.sceneObjects.EditableSky.SkyType;
import optics.raytrace.core.LightSource;
import optics.raytrace.core.Studio;
import optics.raytrace.core.StudioInitialisation;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;

/**
 * Initialisation of the scene and lights.
 * The scene then contains Tim's head.
 * @author johannes
 */
public class TIMInSpaceInitialisation extends StudioInitialisation
{
	@Override
	public String getDescription() {
		return "Tim in space";
	}
	
	@Override
	public void initialiseSceneAndLights(SceneObjectContainer scene, Studio studio)
	{
		studio.setLights(LightSource.getStandardLightsFromBehind());

		scene.clear();
		
		// the night sky
		scene.addSceneObject(
				new EditableSky(
						"Sky",	// description
						SkyType.NIGHT,
						Vector3D.O,	// centre,
						MyMath.HUGE,	// radius,
						Vector3D.Y,	// pole,
						new Vector3D(-1, 0, 0),	// phi0Direction,
						scene, studio
						)
				);
		
		// Tim's head
		scene.addSceneObject(
				new EditableTimHead(
						"Tim's head",	// description
						new Vector3D(0, 0, 10),	// centre
						1,	// radius
						new Vector3D(0, 0, -1),	// frontDirection
						new Vector3D(0, 1, 0),	// topDirection
						new Vector3D(1, 0, 0),	// rightDirection
						scene, 
						studio
					)
				);
	}
}
