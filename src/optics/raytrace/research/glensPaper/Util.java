package optics.raytrace.research.glensPaper;

import math.*;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.surfaces.SurfaceColour;
import optics.raytrace.surfaces.SurfaceTiling;
import optics.DoubleColour;
import optics.raytrace.GUI.sceneObjects.EditableParametrisedCone;
import optics.raytrace.GUI.sceneObjects.EditableParametrisedCylinder;
import optics.raytrace.GUI.sceneObjects.EditableScaledParametrisedSphere;


/**
 * A collection of (at least 1) useful method(s)
 * 
 * @author Johannes Courtial
 */
public class Util
{
	/**
	 * Define scene, lights, and/or camera.
	 * @return a studio, i.e. scene, lights and camera
	 */
	public static void addObjectsTouchingZ10PlaneToScene(SceneObjectContainer scene)
	{
		// add scene objects to look at, all of them touching, from behind (z>10), the plane z=10
		
		// 1) Sphere, Centre (-0.35, 0.2, 10.3), Radius 0.3,
		// Surface type Tiled, Tile type 1 Coloured RGB = (0.5, 0.5, 0.5), Tile type 2 Coloured RGB = (1,1,1),
		// Period in u direction 1.047 (= pi/3), Period in v direction 1.047
		scene.addSceneObject(new EditableScaledParametrisedSphere(
				"Sphere touching plane z=10",	// description,
				new Vector3D(-0.35, 0.2, 10.3),	// centre,
				0.3,	// radius,
				new Vector3D(0, 0, 1),	// pole,
				new Vector3D(1, 0, 0),	// phi0Direction,
				0,	// sThetaMin,
				Math.PI,	// sThetaMax,
				-Math.PI,	// sPhiMin,
				Math.PI,	// sPhiMax,
				new SurfaceTiling(
						new SurfaceColour(DoubleColour.GREY50, DoubleColour.WHITE, true),	// surfaceProperty1,
						new SurfaceColour(DoubleColour.WHITE, DoubleColour.WHITE, true),	// surfaceProperty2,
						Math.PI/3,	// widthU,
						Math.PI/3	// widthV
					),	// surfaceProperty,
				scene,	// parent, 
				scene.getStudio()
			));
		
		// 2) Cone, Apex (0, 0, 10), Axis direction (0.577, 0.577, 0.577) ((1,1,1), normalised),
		// Cone angle 45°, Height 0.7,
		// Surface type Tiled, Tile type 1 Coloured RGB = (1,0,0), Tile type 2 Coloured RGB = (1, 0.5, 0.5),
		// Period in u direction 0.2, Period in v direction 1.047
		scene.addSceneObject(new EditableParametrisedCone(
				"Cone touching plane z=10",	// description,
				new Vector3D(0, 0, 10),	// apex,
				new Vector3D(1, 1, 1),	// axis,
				false,	// open,
				MyMath.deg2rad(45),	// theta,
				0.7,	// height,
				new SurfaceTiling(
						SurfaceColour.RED_SHINY,	// surfaceProperty1,
						new SurfaceColour(new DoubleColour(1, 0.5, 0.5), DoubleColour.WHITE, true),	// surfaceProperty2,
						0.2,	// widthU,
						Math.PI/3	// widthV
					),	// surfaceProperty,
				scene,	// parent, 
				scene.getStudio()
			));
		
		// 3) Cylinder, Start point (-0.5, -0.25, 10.2), End point (0.4, -0.5, 10.2), Radius 0.2,
		// Surface type Tiled, Tile type 1 Coloured RGB = (0, 0, 0.5), Tile type 2 Coloured RGB = (0.5, 0.5, 1),
		// Period in u direction 0.2, Period in v direction 1.047
		scene.addSceneObject(new EditableParametrisedCylinder(
				"Cylinder touching plane z=10",	// description,
				new Vector3D(-0.5, -0.25, 10.2),	// startPoint,
				new Vector3D(0.4, -0.5, 10.2),	// endPoint,
				0.2,	// radius,
				new SurfaceTiling(
						new SurfaceColour(new DoubleColour(0, 0, 0.5), DoubleColour.WHITE, true),	// surfaceProperty1,
						new SurfaceColour(new DoubleColour(0.5, 0.5, 1), DoubleColour.WHITE, true),	// surfaceProperty2,
						0.2,	// widthU,
						Math.PI/3	// widthV
					),	// surfaceProperty,
				scene,	// parent, 
				scene.getStudio()
			));
	}
}
