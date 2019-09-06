package optics.raytrace.studioInitialisation;

import math.Vector3D;
import optics.DoubleColour;
import optics.raytrace.GUI.sceneObjects.EditableCuboid;
import optics.raytrace.GUI.sceneObjects.EditableCylinderLattice;
import optics.raytrace.GUI.sceneObjects.EditableNinkyNonkSilhouette;
import optics.raytrace.GUI.sceneObjects.EditableParametrisedCylinder;
import optics.raytrace.GUI.sceneObjects.EditableTimHead;
import optics.raytrace.core.SceneObjectClass;
import optics.raytrace.core.Studio;
import optics.raytrace.core.StudioInitialisation;
import optics.raytrace.lights.AmbientLight;
import optics.raytrace.lights.LightSourceContainer;
import optics.raytrace.lights.PhongLightSource;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.surfaces.SurfaceColour;

/**
 * Initialises the scene and lights to a surrealist setting.
 * @author johannes
 */
public class SurrealistInitialisation extends StudioInitialisation
{
	@Override
	public String getDescription() {
		return "Surrealist";
	}
	
	@Override
	public void initialiseSceneAndLights(SceneObjectContainer scene, Studio studio)
	{
		LightSourceContainer lights = new LightSourceContainer("lights");
		lights.add(new AmbientLight("background light", new DoubleColour(0.26, 0.25, 0.25)));	// slightly reddish (warm)
		lights.add(new PhongLightSource("point light souce", new Vector3D(257,142,583), DoubleColour.WHITE.multiply(1), DoubleColour.WHITE.multiply(1), 40.));
		// lights.add(new PhongLightSource("point light souce", new Vector3D(500,300,600), DoubleColour.WHITE.multiply(0.1), DoubleColour.WHITE.multiply(0.1), 40.));
		lights.add(new PhongLightSource("point light souce", new Vector3D(100,300,-500), DoubleColour.WHITE.multiply(0.1), DoubleColour.WHITE.multiply(0.1), 40.));

		studio.setLights(lights);

		scene.clear();
		
		// the standard scene objects
		scene.addSceneObject(SceneObjectClass.getSkySphere(scene, studio));
		scene.addSceneObject(SceneObjectClass.getLighterChequerboardFloor(scene, studio));

		// add any other scene objects
		
		// Tim 1's pedestal
		scene.addSceneObject(
				new EditableCuboid(
						"Tim 1's pedestal",	// description
						new Vector3D(7.5, 0, 50),	// centre
						new Vector3D(0.5, 0, 0),
						new Vector3D(0, 1, 0),
						new Vector3D(0, 0, 0.5),	// basisVectors
						SurfaceColour.GREY70_MATT,	// faceSurfaceProperty
						scene,
						studio
					));

		EditableTimHead tim1 = new EditableTimHead(
				"Tim 1's head",
				new Vector3D(7.5, 2, 50),	// centre
				1,	// radius
				new Vector3D(0, 0, -1),	// front direction
				new Vector3D(0, 1, 0),	// top direction
				new Vector3D(1, 0, 0),	// right direction
				scene,
				studio
			);
		scene.addSceneObject(tim1);

		// 2nd Tim's head
		double timHead2x = 5;
		double timHead2y = 0.5;
		double timHead2z = 16;
		double timHead2radius = 1;
		
		scene.addSceneObject(
				new EditableParametrisedCylinder(
						"Tim 2's pedestal",	// description
						new Vector3D(timHead2x, -1, timHead2z),	// startPoint
						new Vector3D(timHead2x, timHead2y - timHead2radius, timHead2z),	// endPoint
						2,	// radius
						SurfaceColour.GREY70_MATT,	// surfaceProperty
						scene,
						studio
					));
//				new EditableCuboid(
//					"Tim 2's pedestal",	// description
//					new Vector3D(5, -0.25, 16),	// centre
//					new Vector3D(-1, 0, 1).getWithLength(0.25),
//					new Vector3D(0, 0.75, 0),
//					new Vector3D(1, 0, 1).getWithLength(0.25),	// basisVectors
//					SurfaceColour.GREY20_MATT,	// faceSurfaceProperty
//					scene,
//					studio
//				));
		
		EditableTimHead tim2 = new EditableTimHead(
				"Tim 2's head",
				new Vector3D(timHead2x, timHead2y, timHead2z),	// centre
				timHead2radius,	// radius,
				new Vector3D(1, 0, -1),	// front direction
				new Vector3D(0, 1, 0),	// top direction
				new Vector3D(1, 0, 0),	// right direction
				scene,
				studio
			);
		scene.addSceneObject(tim2);

		double cylinderRadius = 0.02;
		
		// a cylinder lattice...
		scene.addSceneObject(new EditableCylinderLattice(
				"cylinder lattice",
				-1, 1, 4,
				-1+cylinderRadius, 1+cylinderRadius, 4,
				10, 210, 5, // this puts the "transverse" cylinders into the planes z=10, 60, 110, 160, 210
				cylinderRadius,
				scene,
				studio
		));		

		scene.addSceneObject(new EditableParametrisedCylinder(
				"Chimney",	// description
				new Vector3D(-30, -1, 150),	// startPoint
				new Vector3D(-30, 30, 150),	// endPoint
				1,	// radius
				SurfaceColour.BROWN_MATT,	// surfaceProperty
				scene,	// parent
				studio
			));
		
		// a Ninky Nonk
		scene.addSceneObject(new EditableNinkyNonkSilhouette(
				"Ninky Nonk",	// description
				new Vector3D(170, 0, 1000),	// bottomLeftCorner
				new Vector3D(1, 0, 0),	// rightDirection
				new Vector3D(0, 1, 0),	// upDirection
				30,	// width
				scene,
				studio
		));		
	}


}
