package optics.raytrace.studioInitialisation;

import math.Vector3D;
import optics.raytrace.GUI.sceneObjects.EditableText;
import optics.raytrace.core.LightSource;
import optics.raytrace.core.SceneObjectClass;
import optics.raytrace.core.Studio;
import optics.raytrace.core.StudioInitialisation;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.surfaces.SurfaceColour;

/**
 * Initialisation of the scene and lights.
 * The scene is initialised to a number of planes labelled by distance
 * @author johannes
 */
public class DistanceLabelledPlanes2Initialisation extends StudioInitialisation
{
	public static final double CM = 1e-2;
	public static final double MM = 1e-3;
	public static final double UM = 1e-6;
	public static final double NM = 1e-9;

	@Override
	public String getDescription() {
		return "Distance-labelled planes 1m - 10m";
	}
	
	private static void addZPlaneText(double zInM, SceneObjectContainer scene, Studio studio)
	{
		scene.addSceneObject(EditableText.getCentredEditableText(
				"Text in plane z="+zInM+" m",	// description
				"<i>z</i>&thinsp;=&thinsp;"+zInM+"&thinsp;m",	// text
				new Vector3D(0, -11*CM, zInM),	// centre
				// new Vector3D(-2*CM, (-0.16*zInCM+0.6)*CM, zInCM*CM-zShift),	// bottomLeftCorner
				new Vector3D(1, 0, 0),	// rightDirection
				new Vector3D(0, 1, 0),	// upDirection
				1024,	// fontSize
				"Times",	// fontFamily
				10*CM,	// textHeight
				SurfaceColour.BLACK_SHINY,	// textSurfaceProperty
				scene,
				studio
			));
	}

	
	@Override
	public void initialiseSceneAndLights(SceneObjectContainer scene, Studio studio)
	{
		studio.setLights(LightSource.getStandardLightsFromBehind());
		
		scene.clear();
		
		// add the standard scene objects
		scene.addSceneObject(SceneObjectClass.getHeavenSphere(scene, studio));
		
		addZPlaneText(
				0.5,	// zInM
				scene,
				studio
				);

		addZPlaneText(
				1,	// zInM
				scene,
				studio
				);

		addZPlaneText(
				2,	// zInM
				scene,
				studio
				);

		addZPlaneText(
				5,	// zInM
				scene,
				studio
				);

		addZPlaneText(
				10,	// zInM
				scene,
				studio
				);

		addZPlaneText(
				20,	// zInM
				scene,
				studio
				);

	}
}
