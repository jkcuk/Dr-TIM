package optics.raytrace.studioInitialisation;

import math.MyMath;
import math.Vector3D;
import optics.raytrace.GUI.sceneObjects.EditableCylinderLattice;
import optics.raytrace.GUI.sceneObjects.EditableFramedRectangle;
import optics.raytrace.core.LightSource;
import optics.raytrace.core.SceneObjectClass;
import optics.raytrace.core.Studio;
import optics.raytrace.core.StudioInitialisation;
import optics.raytrace.core.SurfacePropertyPrimitive;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.surfaces.RayRotating;
import optics.raytrace.surfaces.SurfaceColour;

/**
 * Bare-bones initialisation of the scene and lights.
 * @author johannes
 */
public class METATOYInitialisation extends StudioInitialisation
{
	@Override
	public String getDescription() {
		return "METATOY science (lattice behind METATOY window)";
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
				-1+cylinderRadius, 1-cylinderRadius, 4,
				-1+cylinderRadius, 1-cylinderRadius, 4,
				11, 38, 4, // this puts the "transverse" cylinders into the planes z=11, 20, 29, 38
				0.02,
				scene,
				studio
		));
		
		SurfacePropertyPrimitive windowSurface = new RayRotating(MyMath.deg2rad(60), SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT, true);
		windowSurface.setShadowThrowing(false);
		
		// ... behind a ray-rotating window
		EditableFramedRectangle window = new EditableFramedRectangle(
				"METATOY window",	// description
				new Vector3D(-1.5, -1+cylinderRadius, 10),	// corner
				new Vector3D(3, 0, 0),	// widthVector
				new Vector3D(0, 2, 0),	// heightVector
				0.025,	// frame radius
				windowSurface,	// windowSurfaceProperty
				SurfaceColour.GREY50_SHINY,	// frame surface property
				true,	// show frame
				scene,
				studio
			);
		scene.addSceneObject(window);
	}
}
