package optics.raytrace.studioInitialisation;

import math.MyMath;
import math.Vector3D;
import optics.DoubleColour;
import optics.raytrace.GUI.sceneObjects.EditableFramedRectangle;
import optics.raytrace.GUI.sceneObjects.EditableScaledParametrisedCentredParallelogram;
import optics.raytrace.GUI.sceneObjects.EditableScaledParametrisedSphere;
import optics.raytrace.GUI.sceneObjects.EditableSceneObjectCollection;
import optics.raytrace.GUI.surfaces.EditablePictureSurfaceDiffuse;
import optics.raytrace.GUI.surfaces.EditableTwoSidedSurface;
import optics.raytrace.core.SceneObjectClass;
import optics.raytrace.core.Studio;
import optics.raytrace.core.StudioInitialisation;
import optics.raytrace.core.SurfaceProperty;
import optics.raytrace.core.SurfacePropertyPrimitive;
import optics.raytrace.lights.AmbientLight;
import optics.raytrace.lights.LightSourceContainer;
import optics.raytrace.lights.PhongLightSource;
import optics.raytrace.research.autostereogramResonator.AutostereogramResonator;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.surfaces.Point2PointImagingPhaseHologram;
import optics.raytrace.surfaces.SurfaceColour;
import optics.raytrace.surfaces.Transparent;

/**
 * Initialisation to a scene that enables playing with an autostereogram resonator.
 * @author johannes
 *
 */
public class AutostereogramResonatorInitialisation extends StudioInitialisation
{
	private boolean showPicture2;
	
	
	// constructor
	public AutostereogramResonatorInitialisation(boolean showPicture2) {
		super();
		this.showPicture2 = showPicture2;
	}
	
	// getters & setters

	public boolean isShowPicture2() {
		return showPicture2;
	}

	public void setShowPicture2(boolean showPicture2) {
		this.showPicture2 = showPicture2;
	}


	// implement StudioInitialisation methods

	@Override
	public String getDescription() {
		return "Autostereogram resonator [Opt. Commun. 285, 3971 (2012)]";
	}
	
	@Override
	public void initialiseSceneAndLights(SceneObjectContainer scene, Studio studio)
	{
		// studio.setLights(new AmbientLight("Dull light", DoubleColour.WHITE));
		LightSourceContainer lights = new LightSourceContainer("lights");
		lights.add(new AmbientLight("background light", DoubleColour.GREY80));
		// lights.add(new AmbientLight("background light", DoubleColour.WHITE));
		lights.add(new PhongLightSource("point light souce", new Vector3D(100,300,-500), DoubleColour.GREY50, DoubleColour.GREY50, 40.));
		// lights.add(new PhongLightSource("point light souce", new Vector3D(10,50,30), DoubleColour.WHITE, DoubleColour.WHITE, 40.));
		studio.setLights(lights);

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

		double
			e = 1,	// the separation between the eyes, in cm
			a = 10,	// distance from eye to plane A
			s = 12;	// distance from eye to surface S (so length of resonator is s-a)

		//
		// add the resonator mirrors
		//
		
		Vector3D
			eye1 = new Vector3D(0, 0, 0),	// the camera
			eye2 = new Vector3D(e, 0, 0);
		
		scene.addSceneObject(new EditableFramedRectangle(
				"mirror A",
				new Vector3D(-1.5, -1, a),	// corner
				new Vector3D(3, 0, 0),	// width vector
				new Vector3D(0, 2, 0),	// height vector
				0.025,	// frame radius
				new EditableTwoSidedSurface(
						new Transparent(SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT, true),	// looking from the direction of the camera
						new Point2PointImagingPhaseHologram(eye1, eye2, SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT, true, true),
						scene
					),	// window surface
				SurfaceColour.DARK_RED_SHINY,	// frame surface
				true,	// show frame
				scene,
				studio
		));
		
		// Vector3D mirrorSCentre = new Vector3D(0, 0, s);
		
		double sphereRadius = .8;
		double stickingOutBy = .6;
		
//		scene.addSceneObject(new Sphere(
//				"sphere",
//				new Vector3D(8, 3, s + sphereRadius - stickingOutBy),
//				sphereRadius,
//				new Point2PointImaging(eye1, eye2, 1, true),
//				scene,
//				studio
//			));
		
		EditableSceneObjectCollection mirrorS = new EditableSceneObjectCollection("mirror S", true, scene, studio);

		mirrorS.addSceneObject(new EditableFramedRectangle(
				"planar bit",
				new Vector3D(-2.25, -1, s),	// corner
				new Vector3D(4.5, 0, 0),	// width vector
				new Vector3D(0, 2, 0),	// height vector
				0.025,	// frame radius
				new Point2PointImagingPhaseHologram(eye1, eye2, SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT, true, true),	// window surface
				SurfaceColour.DARK_BLUE_SHINY,	// frame surface
				true,	// show frame
				scene,
				studio
		));
		
		mirrorS.addSceneObject(new EditableScaledParametrisedSphere(
				"spherical bulge",
				new Vector3D(0, 0, s + sphereRadius - stickingOutBy),
				sphereRadius,
				new Point2PointImagingPhaseHologram(eye1, eye2, SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT, true, true),
				scene,
				studio
			));

//		scene.addSceneObject(new Parallelepiped(
//				"Parallelepiped",	// description,
//				new Vector3D(0, 0, 0),	// centre,
//				new Vector3D(10, 0, 0),	// u,
//				new Vector3D(0, 10, 0),	// v,
//				new Vector3D(0, 0, 10),	// w,
//				new Point2PointImaging(eye1, eye2, 1, true),
//				scene,	// parent,
//				studio
//			).transform(new RotationAroundXAxis(MyMath.deg2rad(45))
//			).transform(new RotationAroundYAxis(MyMath.deg2rad(30))
//			).transform(new RotationAroundZAxis(MyMath.deg2rad(20))
//			).transform(new Translation(new Vector3D(-9, -2, s+2)))
//		);
		
		scene.addSceneObject(mirrorS);

		// the right picture surface...
		double
			x1Centre = 1.4,
			x2Centre = -1.4,
			height = 2,
			width = 0.2;
		
		java.net.URL imgURL1 = (new AutostereogramResonator()).getClass().getResource("TimHead.jpg");
		SurfaceProperty pictureSurface1 = new EditablePictureSurfaceDiffuse(imgURL1, true, 0, width, 0, width);
		
		scene.addSceneObject(new EditableScaledParametrisedCentredParallelogram(
				"picture 1",
				new Vector3D(x1Centre, 0, a + 2*MyMath.SMALL),	// centre
				new Vector3D(width, 0, 0),	// width vector
				new Vector3D(0, -height, 0),	// height vector
				0,	// suMin
				width,	// suMax
				0,	// svMin
				height,	// svMax
				pictureSurface1,
				scene,
				studio
			));

		java.net.URL imgURL2 = (new AutostereogramResonator()).getClass().getResource("vfSpiral.jpg");
		SurfaceProperty pictureSurface2 = new EditablePictureSurfaceDiffuse(imgURL2, true, 0, width, 0, width);
		
		scene.addSceneObject(
			new EditableScaledParametrisedCentredParallelogram(
				"picture 2",
				new Vector3D(x2Centre, 0, a + 2*MyMath.SMALL),	// centre
				new Vector3D(width, 0, 0),	// width vector
				new Vector3D(0, -height, 0),	// height vector
				0,	// suMin
				width,	// suMax
				0,	// svMin
				height,	// svMax
				pictureSurface2,
				scene,
				studio
			),
			showPicture2
		);
	}


}
