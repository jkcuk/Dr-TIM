package optics.raytrace.studioInitialisation;

import math.Vector3D;
import optics.raytrace.GUI.sceneObjects.EditableCylinderFrame;
import optics.raytrace.GUI.sceneObjects.EditableScaledParametrisedCentredParallelogram;
import optics.raytrace.GUI.sceneObjects.EditableScaledParametrisedSphere;
import optics.raytrace.core.LightSource;
import optics.raytrace.core.SceneObjectClass;
import optics.raytrace.core.Studio;
import optics.raytrace.core.StudioInitialisation;
import optics.raytrace.core.SurfacePropertyPrimitive;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.surfaces.RayRotating;
import optics.raytrace.surfaces.Reflective;
import optics.raytrace.surfaces.SurfaceColour;

/**
 * Original initialisation of the scene and lights.
 * @author johannes
 */
public class OriginalInitialisation extends StudioInitialisation
{
	@Override
	public String getDescription() {
		return "Original (shiny spheres behind ray-rotating window)";
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
		
		scene.addSceneObject(new EditableScaledParametrisedSphere(
				"shiny sphere 1",
				new Vector3D(-1, 0, 15),	// centre
				1,	// radius
				new Reflective(SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT, true),
				scene,
				studio
		));
		scene.addSceneObject(new EditableScaledParametrisedSphere(
				"shiny sphere 2",
				new Vector3D(1, 0, 15),	// centre
				1,	// radius
				new Reflective(SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT, true),
				scene,
				studio
		));
		scene.addSceneObject(new EditableScaledParametrisedSphere(
				"shiny sphere 3",
				new Vector3D(0, Math.sqrt(3.), 15),	// centre
				1,	// radius
				new Reflective(SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT, true),
				scene,
				studio
		));

		scene.addSceneObject(new EditableScaledParametrisedCentredParallelogram(
				"ray-rotating window",
				new Vector3D(0, 0, 10),	// centre
				new Vector3D(3, 0, 0),	// width vector
				new Vector3D(0, 2, 0),	// height vector
				new RayRotating(0.5*Math.PI, SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT, true),
				scene,
				studio
		));
		
		scene.addSceneObject(new EditableCylinderFrame(
				"window frame",
				new Vector3D(-1.5, -1, 10),
				new Vector3D(3, 0, 0),
				new Vector3D(0, 2, 0),
				0.025,
				SurfaceColour.GREY50_SHINY,
				scene,
				studio
		));
	}
}
