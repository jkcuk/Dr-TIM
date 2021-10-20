package optics.raytrace.research.PlatonicLens;

import java.awt.*;
import java.text.DecimalFormat;

import math.*;
import optics.raytrace.sceneObjects.*;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.surfaces.SemiTransparent;
import optics.raytrace.surfaces.SurfaceColour;
import optics.raytrace.surfaces.SurfaceColourLightSourceIndependent;
import optics.raytrace.cameras.PinholeCamera.ExposureCompensationType;
import optics.raytrace.core.*;
import optics.DoubleColour;
import optics.raytrace.GUI.cameras.EditableRelativisticAnyFocusSurfaceCamera;
import optics.raytrace.GUI.cameras.QualityType;
import optics.raytrace.GUI.lowLevel.ApertureSizeType;
import optics.raytrace.GUI.nonInteractive.PhotoCanvas;
import optics.raytrace.GUI.nonInteractive.PhotoFrame;
import optics.raytrace.GUI.sceneObjects.EditablePlatonicLens;
import optics.raytrace.GUI.sceneObjects.EditablePlatonicSolid;
import optics.raytrace.GUI.sceneObjects.EditableRayTrajectory;


/**
 * Based on NonInteractiveTIMMovieMaker.
 * 
 * Calculates a movie of the ray trajectory through a Platonic lens, with the trajectory changing.
 * 
 * @author Johannes Courtial
 */
public class PlatonicLensTrajectoryPlotter
{
//	public static final PlatonicSolidType PLATONIC_SOLID_TYPE = PlatonicSolidType.TETRAHEDRON;
//	public static final PlatonicSolidType PLATONIC_SOLID_TYPE = PlatonicSolidType.CUBE;
//	public static final PlatonicSolidType PLATONIC_SOLID_TYPE = PlatonicSolidType.OCTAHEDRON;
//	public static final PlatonicSolidType PLATONIC_SOLID_TYPE = PlatonicSolidType.DODECAHEDRON;
	public static final PlatonicSolidType PLATONIC_SOLID_TYPE = PlatonicSolidType.ICOSAHEDRON;

	private static double getScaleFactor(PlatonicSolidType platonicSolidType)
	{
		switch(platonicSolidType)
		{
		case TETRAHEDRON:
			return 200;
		case CUBE:
			return 10;
		case OCTAHEDRON:
			return 10;
		case DODECAHEDRON:
			return 2;
		default:
			return 1;
		}
	}
	
	/**
	 * @return	the number of frames in the movie
	 */
	public static int getNoOfFrames()
	{
		return 10;
	}
	
	/**
	 * Filename under which main saves the rendered image.
	 * Alter to save the rendered image under a different name.
	 * @return	the filename under which main saves the rendered image.
	 */
	public static String getFilename(int frame)
	{
		return "PlatonicLensTrajectory "	// the name
				+ PLATONIC_SOLID_TYPE.toString() + " "
				+ (new DecimalFormat("000000")).format(frame)	// the number of the frame, converted into a string
				+".bmp";	// the extension
	}
	
	/**
	 * Define scene, lights, and/or camera.
	 * @return a studio, i.e. scene, lights and camera
	 */
	public static Studio createStudio(int frame)
	{
		Studio studio = new Studio();

		// the scene
		SceneObjectContainer scene = new SceneObjectContainer("the scene", null, studio);

		// the standard scene objects
		scene.addSceneObject(SceneObjectClass.getSkySphere(scene, studio));
		// scene.addSceneObject(SceneObjectClass.getChequerboardFloor(scene, studio));
				
		// add any other scene objects
		
		// Tim's head, behind the cloak
//		scene.addSceneObject(new EditableTimHead(
//				"Tim's head",
//				new Vector3D(0, 0, 15),
//				scene,
//				studio
//			));
		
		EditablePlatonicSolid platonicSolid = new EditablePlatonicSolid(
				"Platonic solid",	// description
				PLATONIC_SOLID_TYPE,	// platonicSolid
				new Vector3D(0, 0, 0),	// centre
				BasisVectors3D.getOrthonormalXYZBasisVectors(),	// basisVectors
				100000,	// radius
				true,	// showVertices
				true,	// showEdges
				false,	// showFaces
				0.01,	// vertexRadius
				0.01,	// edgeRadius
				SurfaceColour.DARK_RED_SHINY,	// vertexSurfaceProperty
				SurfaceColour.DARK_RED_SHINY,	// edgeSurfaceProperty
				SemiTransparent.RED_SHINY_SEMITRANSPARENT,	// faceSurfaceProperty
				scene,	// parent
				studio
			);
		
		// the Platonic lens
		EditablePlatonicLens platonicLens = new EditablePlatonicLens(
				"Platonic lens",	// description
				platonicSolid,
				1.0/getScaleFactor(PLATONIC_SOLID_TYPE),	// focalLength
				0.7,	// lensTransmissionCoefficient
				false,	// lensShadowThrowing
				false,	// showPlatonicSolid
				// false,	// showPyramidalCaps
				false,	// showLensEdges
				scene,	// parent,
				studio
			);
		scene.addSceneObject(platonicLens);
		
		Vector3D
			rayStartPosition = new Vector3D(-0.5-1.0*frame/getNoOfFrames(), 0.3, 0),
			rayDirection = new Vector3D(0.2, 0.1, 1);
		scene.addSceneObject(new EditableRayTrajectory(
						"ray trajectory",
						rayStartPosition,	// start point
						0,	// start time
						rayDirection,	// initial direction
						0.01,	// radius
						new SurfaceColourLightSourceIndependent(DoubleColour.GREEN, true),
						100,	// max trace level
						true,	// reportToConsole
						scene,
						studio
				)
			);
		
		studio.setScene(scene);

		// trace the rays with trajectory through the scene
		studio.traceRaysWithTrajectory();

		// scene.removeSceneObject(platonicLens);
		// scene.addSceneObject(platonicSolid);
		
		// make the lenses effectively plane bits of glass, ...
		platonicLens.setFocalLength(MyMath.HUGE);
		// ... make the radius smaller, ...
		platonicSolid.setRadius(2);
		// ... re-populate the scene-object collection with scene objects that represent the new parameters, ...
		platonicSolid.populateSceneObjectCollection();
		// ... and draw all the relevant edges
		platonicLens.setShowPlatonicSolid(true);
		platonicLens.setShowLensEdges(true);
		platonicLens.populateSceneObjectCollection();



		// define the camera
		//
		// Note that the view direction and basis Vector3Ds of the detector are chosen such that
		// the x, y, z axes form a LEFT-handed coordinate system.
		// The reason is that, in the photo, the positive x direction is then to the right,
		// the positive y direction is upwards, and the camera looks in the positive z direction.
		int
		pixelsX = 640,
		pixelsY = 480,
		antiAliasingFactor = 1;
		// If antiAliasingFactor is set to N, the image is calculated at resolution
		// N*pixelsX x N*pixelsY.

		
		SceneObject focusScene = new Plane(
				"focussing plane",
				new Vector3D(0, 0, 0),	// point on plane
				new Vector3D(0, 0, 1),	// normal to plane
				(SurfaceProperty)null,
				null,	// parent
				Studio.NULL_STUDIO
		);
		
		boolean normalView = false;
		Vector3D
			viewDirection = (normalView?Vector3D.crossProduct(rayStartPosition, rayDirection).getNormalised():new Vector3D(-.2, -.3, 1).getNormalised());
		System.out.println("PlatonicLensTrajectoryPlotter::createStudio: viewDirection = "+viewDirection);
		
		EditableRelativisticAnyFocusSurfaceCamera camera = new EditableRelativisticAnyFocusSurfaceCamera(
				"Camera",
				viewDirection.getProductWith(-10.),	// new Vector3D(2, 3, -10),	// centre of aperture
				viewDirection,	// viewDirection
				new Vector3D(0, 1, 0),	// top direction vector
				30,	// horiontalViewAngle in degrees; 2*MyMath.rad2deg(Math.atan(2./10.)) gives same view angle as in previous version
				new Vector3D(0, 0, 0),	// beta
				pixelsX*antiAliasingFactor, pixelsY*antiAliasingFactor,	// logical number of pixels
				ExposureCompensationType.EC0,	// exposure compensation +0
				100,	// maxTraceLevel
				focusScene,
				null,	// cameraFrameScene,
				ApertureSizeType.PINHOLE,	// aperture size
				QualityType.RUBBISH,	// blur quality
				QualityType.NORMAL	// anti-aliasing quality
		);

		studio.setLights(LightSource.getStandardLightsFromBehind());
		studio.setCamera(camera);

		return studio;
	}

	/**
	 * This method gets called when the Java application starts.
	 * 
	 * @see createStudio
	 * @author	Johannes Courtial
	 */
	public static void main(final String[] args)
	{
		// open a window, and take a note of its content pane
		Container container = (new PhotoFrame()).getContentPane();
		
		for(int frame = 0; frame < getNoOfFrames(); frame++)
		{
			// define scene, lights and camera
			Studio studio = createStudio(frame);

			// do the ray tracing
			studio.takePhoto();

			// save the image
			studio.savePhoto(getFilename(frame), "bmp");

			// display the image on the screen
			container.removeAll();
			container.add(new PhotoCanvas(studio.getPhoto()));
			container.validate();
		}
	}
}
