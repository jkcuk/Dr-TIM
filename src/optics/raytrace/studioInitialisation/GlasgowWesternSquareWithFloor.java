package optics.raytrace.studioInitialisation;

import math.MyMath;
import math.Vector3D;
import optics.raytrace.GUI.sceneObjects.EditableParametrisedPlane;
import optics.raytrace.GUI.sceneObjects.EditableSky;
import optics.raytrace.GUI.sceneObjects.EditableSky.SkyType;
import optics.raytrace.GUI.surfaces.EditableSurfaceTiling;
import optics.raytrace.core.LightSource;
import optics.raytrace.core.Studio;
import optics.raytrace.core.StudioInitialisation;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.surfaces.SemiTransparent;
import optics.raytrace.surfaces.SurfaceColour;

/**
 * Initialisation of the scene and lights.
 * The scene contains the university western square 360-180 image as a sky property.
 * @author Maik
 */
public class GlasgowWesternSquareWithFloor extends StudioInitialisation
{
	@Override
	public String getDescription() {
		return "University western square";
	}
	
	@Override
	public void initialiseSceneAndLights(SceneObjectContainer scene, Studio studio)
	{
		studio.setLights(LightSource.getStandardLightsFromBehind());

		scene.clear();
		
		// the night sky
		scene.addSceneObject(
				new EditableSky(
						"University square",	// description
						SkyType.UNIVERSITY,
						Vector3D.O,	// centre,
						MyMath.HUGE,	// radius,
						Vector3D.Y,	// pole,
						new Vector3D(-1, 0, 0.476),	// phi0Direction, Set so that the gate is centres when facing along (0,0,1)
						scene, studio
						)
				);
		
		//The chequerboard floor over the grass
		scene.addSceneObject(	new EditableParametrisedPlane(
				"chequerboard floor", 
				new Vector3D(0, -(1+MyMath.TINY), 0),	// point on plane
				new Vector3D(1, 0, 0),	// Vector3D 1 that spans plane
				new Vector3D(0, 0, 1),	// Vector3D 2 that spans plane
				// true,	// shadow-throwing
				new EditableSurfaceTiling(new SemiTransparent(SurfaceColour.GREY50_SHINY,0.75), new SemiTransparent(SurfaceColour.WHITE_SHINY,0.75), 1, 1, studio.getScene()),
				scene,
				studio
		));
	}
}
