package optics.raytrace.studioInitialisation;

import math.Vector3D;
import optics.DoubleColour;
import optics.raytrace.GUI.sceneObjects.EditableParametrisedPlane;
import optics.raytrace.GUI.sceneObjects.EditableTimHead;
import optics.raytrace.core.Studio;
import optics.raytrace.core.StudioInitialisation;
import optics.raytrace.lights.AmbientLight;
import optics.raytrace.lights.LightSourceContainer;
import optics.raytrace.lights.PhongLightSource;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.surfaces.Transparent;

/**
 * Get everything you need for Halloween with this Halloween initialisation of the scene and lights.
 * @author johannes
 */
public class HalloweenInitialisation extends StudioInitialisation
{
	@Override
	public String getDescription() {
		return "Halloween";
	}
	
	@Override
	public void initialiseSceneAndLights(SceneObjectContainer scene, Studio studio)
	{
		double brightnessFactor = 1;
		LightSourceContainer lights = new LightSourceContainer("lights");
		lights.add(new AmbientLight("background light", DoubleColour.GREY20.multiply(brightnessFactor)));
		lights.add(new PhongLightSource("point light souce", new Vector3D(0,-5,7.5), DoubleColour.RED.multiply(brightnessFactor), DoubleColour.RED.multiply(brightnessFactor), 40.));
		studio.setLights(lights);

		scene.clear();
		
		// add any other scene objects
		
		// Tim's head
		scene.addSceneObject(new EditableTimHead(
				"Tim's head",
				new Vector3D(0, 0, 10),
				1,	// radius
				new Vector3D(0, 0, -1),	// front direction
				new Vector3D(0, 1, 0),	// top direction
				new Vector3D(1, 0, 0),	// right direction
				scene,
				studio
			));
		
		// ... standing in front of an invisible wall
		EditableParametrisedPlane wall = new EditableParametrisedPlane(
				"invisible wall",
				new Vector3D(0, 0, 12),	// point on plane
				new Vector3D(0, 0, 1),	// normal
				Transparent.PERFECT,
				scene,
				studio
			);
		scene.addSceneObject(wall);
	}
}
