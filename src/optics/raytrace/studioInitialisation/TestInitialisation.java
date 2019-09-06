package optics.raytrace.studioInitialisation;

import math.Vector3D;
import optics.raytrace.GUI.sceneObjects.EditableNetOfHypercube;
import optics.raytrace.core.LightSource;
import optics.raytrace.core.SceneObjectClass;
import optics.raytrace.core.Studio;
import optics.raytrace.core.StudioInitialisation;
import optics.raytrace.research.curvedSpaceSimulation.GluingType;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.surfaces.ColourFilter;
import optics.raytrace.surfaces.SurfaceColour;

/**
 * For testing purposes.
 * @author johannes
 */
public class TestInitialisation extends StudioInitialisation
{
	@Override
	public String getDescription() {
		return "Test";
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
		scene.addSceneObject(new EditableNetOfHypercube(
				"Net of hypercube",
				new Vector3D(0, 0, 10),	// centre
				new Vector3D(1, 0, 0),	// rightDirection
				new Vector3D(0, 1, 0),	// upDirection
				0.5,	// sideLength,
				false,	// showNullSpaceWedges
				1,	// nullSpaceWedgeLegLengthFactor
				0.96,	// refractingSurfaceTransmissionCoefficient
				GluingType.PERFECT,	// gluingType
				1,	// numberOfNegativeSpaceWedges
				true,	// showNetStructure
				false,	// showNullSpaceWedgesStructure
				SurfaceColour.BLUE_SHINY,	// netStructureSurfaceProperty
				ColourFilter.CYAN_GLASS,	// netFaceSurfaceProperty
				SurfaceColour.RED_SHINY,	// nullSpaceWedgesStructureSurfaceProperty
				0.02,	// structureTubeRadius
				scene,
				studio));
	}
}
