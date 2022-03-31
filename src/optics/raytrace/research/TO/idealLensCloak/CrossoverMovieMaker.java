package optics.raytrace.research.TO.idealLensCloak;

import java.awt.*;
import java.text.DecimalFormat;

import math.*;
import optics.raytrace.sceneObjects.*;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.surfaces.GlensSurface;
import optics.raytrace.surfaces.ImagingDirection;
import optics.raytrace.surfaces.SurfaceColour;
import optics.raytrace.cameras.PinholeCamera.ExposureCompensationType;
import optics.raytrace.core.*;
import optics.raytrace.GUI.cameras.EditableRelativisticAnyFocusSurfaceCamera;
import optics.raytrace.GUI.cameras.QualityType;
import optics.raytrace.GUI.lowLevel.ApertureSizeType;
import optics.raytrace.GUI.lowLevel.LensElementType;
import optics.raytrace.GUI.nonInteractive.PhotoCanvas;
import optics.raytrace.GUI.nonInteractive.PhotoFrame;
import optics.raytrace.GUI.sceneObjects.EditableIdealLensCloak;
import optics.raytrace.GUI.sceneObjects.EditableRayTrajectory;
import optics.raytrace.GUI.sceneObjects.EditableTriangle;


/**
 * Based on NonInteractiveTIMMovieMaker.
 * 
 * Calculates a movie of a pair of ray trajectories:
 * one that starts from a point inside an ideal-lens cloak,
 * and another one that starts from the image (in outside space) of the point, in the corresponding direction.
 * 
 * "Normally", these trajectories are the same, but they can be different when the trajectory starting inside
 * forms a closed loop.
 * 
 * @author Johannes Courtial
 */
public class CrossoverMovieMaker
{
	// the height of the point above the base lens, in physical space and in EM space
	public static final double H_P_P = 0.2;	// 0.2
	public static final double H_P_E = 0.8;	// 0.8;	// virtual space, i.e. in outside space
	
	public static final QualityType BLUR_QUALITY = 
			QualityType.RUBBISH;
			// QualityType.GOOD;	// blur quality
	public static final QualityType ANTI_ALIASING_QUALITY = 
			QualityType.NORMAL;
			// QualityType.GOOD;	// anti-aliasing quality

	
	/**
	 * @return	the number of frames in the movie
	 */
	public static int getNoOfFrames()
	{
		return 200;
	}
	
	/**
	 * Filename under which main saves the rendered image.
	 * Alter to save the rendered image under a different name.
	 * @return	the filename under which main saves the rendered image.
	 */
	public static String getFilename(int frame)
	{
		return "Crossover"	// the name -- was "AvoidedCrossing", i.e. nonsense
			+ " hP " + H_P_P
			+ " hP' " + H_P_E
		    + (new DecimalFormat("000000")).format(frame)	// the number of the frame, converted into a string
		    +".bmp";	// the extension
	}
	
	private static void launchRayTrajectoryInOppositeDirections(
			String name,
			Vector3D startPosition,
			Vector3D initialDirection,
			double radius,
			SurfaceProperty surfaceProperty,
			// DoubleColour colour,
			SceneObjectContainer scene,
			Studio studio
		)
	{
		// lauch the forward ray...
		scene.addSceneObject(new EditableRayTrajectory(
				name + " (forward)",
				startPosition,	// start point
				0,	// start time
				initialDirection,	// initial direction
				radius,	// radius
				surfaceProperty, 	// new SurfaceColourLightSourceIndependent(colour, true),
				20,	// max trace level
				true,	// reportToConsole
				scene,
				studio
				)
			);

		// ... and the backward ray
		scene.addSceneObject(new EditableRayTrajectory(
				name + " (backward)",
				startPosition,	// start point
				0,	// start time
				initialDirection.getReverse(),	// initial direction
				radius,	// radius
				surfaceProperty, 	// new SurfaceColourLightSourceIndependent(colour, true),
				20,	// max trace level
				true,	// reportToConsole
				scene,
				studio
				)
			);
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
		scene.addSceneObject(SceneObjectClass.getHeavenSphere(scene, studio));
		// scene.addSceneObject(SceneObjectClass.getChequerboardFloor(scene, studio));
				
		// add any other scene objects
		
		// an ideal-lens cloak

		double h = 2.0;	// height of the cloak
		double h1 = h*1./3.;	// height of the lower inner vertex above the base lens
		double h2 = h*2./3.;	// height of the upper inner vertex above the base lens
		Vector3D topDirection = new Vector3D(0, 1, 0);
		Vector3D baseCentre = topDirection.getWithLength(-h/2);	// this centres the cloak at the origin
		
		// Design the cloak such that it images the camera position in the physical "inside space"
		// (the space inside the cell just above the base lens) to the corresponding EM-space position
		// in "outside space".
		// The camera is placed at a position a distance H_C_P above the base lens,
		// which images it to a position a distance H_C_E above the base lens
		
		// first calculate the focal length of the base lens that would image accordingly;
		// lens equation:
		//   1/H_C_P + 1/(-H_C_E) = 1/f
		//   (H_C_E - H_C_P) / (H_C_P H_C_E) = 1/f
		// solve for f:
		//   f = H_C_P H_C_E / (H_C_E - H_C_P)
		double f = H_P_P*H_P_E / (H_P_E - H_P_P);
		
		// then calculate the height of the image of the lower inner vertex, as this is the parameter
		// the EditableIdealLensCloak takes
		// lens equation:
		//   1/h1 + 1/(-h1E) = 1/f
		// solve for h1E:
		//   1/h1E = 1/h1 - 1/f = (f - h1) / (h1 f)
		//   h1E = h1 f / (f - h1)
		double h1E = h1*f / (f - h1);
		
		EditableIdealLensCloak editableIdealLensCloak = 
				new EditableIdealLensCloak(
						"Ideal-lens cloak",
						baseCentre,	// base centre
						new Vector3D(0, 0, -1),	// front direction
						new Vector3D(1, 0, 0),	// right direction
						topDirection,	// top direction
						h*2./3.,	// base radius; if this is 2/3 of height then tetrahedron is regular
						h,	// height
						h1,	// heightLowerInnerVertexP
						h2,	// heightUpperInnerVertexP
						h1E,	// heightLowerInnerVertexE
						0.9,	// interface transmission coefficient
						false,	// show frames
						0.01,	// frame radius
						SurfaceColour.GREY50_SHINY,	// frame surface property
						// false,	// show placeholder surfaces
						LensElementType.IDEAL_THIN_LENS,	// lens-element type
						scene,
						studio
					);
		
		// remove base lens
//		editableIdealLensCloak.removeFirstSceneObjectWithDescription(
//				"Base outer lens",	// description
//				true	// searchIteratively
//			);

		scene.addSceneObject(editableIdealLensCloak);
		
		// launch the rays from the pointLightSourcePosition position and one through its image
		
		Vector3D pointLightSourcePosition = Vector3D.sum(
				baseCentre,
				topDirection.getWithLength(H_P_P),
				new Vector3D(0, 0, 0.01)	// a tiny bit of "noise", to avoid hitting edges
			);
		
		// the "inside" ray's initial direction
		double angle = ((double)frame)/getNoOfFrames()*Math.PI;
		Vector3D initialDirectionInside = new Vector3D(Math.cos(angle), Math.sin(angle), 0);
		
		// launch ray trajectories from the inside position;
		// the radius of the cylinders representing the segments of this trajectory are slightly smaller
		// than those of the cylinders representing the segments of the other trajectory, started outside,
		// so that the outside trajectory covers the inside one whenver they overlap
		launchRayTrajectoryInOppositeDirections(
				"Ray trajectory starting at camera position",	// name
				pointLightSourcePosition,	// startPosition
				initialDirectionInside,	// initialDirection
				0.009,	// radius
				SurfaceColour.BLUE_SHINY,	// .BLUE,	// colour
				scene,
				studio
			);

		// calculate a second position on the ray in "inside" space
		Vector3D secondPositionOnInsideRay = Vector3D.sum(
				pointLightSourcePosition,
				initialDirectionInside.getWithLength(0.1)
			);
		
		// ... and calculate its image due to the base lens, by first getting the object representing the base lens...
		GlensSurface lensHologram = (GlensSurface)(
				(EditableTriangle)(
						editableIdealLensCloak.getFirstSceneObjectWithDescription(
								"Base outer lens",	// description
								true	// searchIteratively
							))	// the EditableTriangle describing the base lens...
			).getSurfaceProperty();	// ... and its surface property which is a GlensHologram
		// ... and then asking it to calculate the image of <pointLightSourcePosition>...
		Vector3D imageOfPointLightSourcePosition = // Vector3D.sum(baseCentre, topDirection.getWithLength(H_C_E));
				lensHologram.getImagePosition(
						pointLightSourcePosition,
						ImagingDirection.NEG2POS	// direction
					);

		// ... and of <secondPositionOnInsideRay>
		Vector3D secondPositionOnOutsideRay = lensHologram.getImagePosition(
				secondPositionOnInsideRay,
				ImagingDirection.NEG2POS	// direction
			);
		
		// now launch a ray that passes through both outside-space positions, but start it from "well back",
		// so that only one ray is required
		Vector3D outsideRayDirection = Vector3D.difference(
				secondPositionOnOutsideRay,
				imageOfPointLightSourcePosition
			);
		
		// now launch the ray
		scene.addSceneObject(new EditableRayTrajectory(
				"outside ray",
				Vector3D.difference(
						imageOfPointLightSourcePosition,
						outsideRayDirection.getWithLength(100*h)
					),	// start point
				0,	// start time
				outsideRayDirection,	// initial direction
				0.01,	// radius
				SurfaceColour.RED_SHINY,	//  SurfaceColourLightSourceIndependent(DoubleColour.RED, true),
				20,	// max trace level
				true,	// reportToConsole
				scene,
				studio
				)
			);

		
		studio.setScene(scene);

		// trace the rays with trajectory through the scene
		studio.traceRaysWithTrajectory();

		// now replace the lens-TO tetrahedron with one that has placeholder surfaces
		// editableIdealLensCloak.setShowPlaceholderSurfaces(true);
		editableIdealLensCloak.setLensElementType(LensElementType.GLASS_PANE);
		editableIdealLensCloak.setShowFrames(true);
		editableIdealLensCloak.populateSceneObjectCollection();
		


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
		
		double cameraDistanceXZ = Math.sqrt(10*10+2*2);
		Vector3D apertureCentre = 
				// new Vector3D(10, 1.5, -2);
				new Vector3D(2, 1.5, -10);
		Vector3D centreOfView = new Vector3D(0, 0, 0);
		EditableRelativisticAnyFocusSurfaceCamera camera = new EditableRelativisticAnyFocusSurfaceCamera(
				"Camera",
				apertureCentre,	// centre of aperture
				Vector3D.difference(centreOfView, apertureCentre),	//new Vector3D(-.2, -.15, 1),	// viewDirection
				new Vector3D(0, 1, 0),	// top direction vector
				20,	// horiontalViewAngle in degrees; 2*MyMath.rad2deg(Math.atan(2./10.)) gives same view angle as in previous version
				new Vector3D(0, 0, 0),	// beta
				pixelsX*antiAliasingFactor, pixelsY*antiAliasingFactor,	// logical number of pixels
				ExposureCompensationType.EC0,	// exposure compensation +0
				100,	// maxTraceLevel
				focusScene,
				null,	// cameraFrameScene,
				ApertureSizeType.PINHOLE,	// aperture size
				BLUR_QUALITY,	// QualityType.GOOD,	// blur quality
				ANTI_ALIASING_QUALITY	// QualityType.GOOD	// anti-aliasing quality
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
		
		for(int frame = 0;
				frame < getNoOfFrames();
				frame++)
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
