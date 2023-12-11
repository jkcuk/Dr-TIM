package optics.raytrace.core;

import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;

public abstract class StudioInitialisation
{
//	public static final double
//		SIZE_FACTOR = 1,	// <1 = smaller, 1 = normal size, >1 = big;
//							// .65 smallest that accommodates all buttons
//		HORIZONTAL_VIEW_ANGLE = 20;	// degrees
//
//	public static final int
//		IMAGE_CANVAS_SIZE_X = (int)(SIZE_FACTOR*640),
//		IMAGE_CANVAS_SIZE_Y = (int)(SIZE_FACTOR*480);
//
//	private int
//		imageCanvasSizeX = IMAGE_CANVAS_SIZE_X,
//		imageCanvasSizeY = IMAGE_CANVAS_SIZE_Y;

	public abstract String getDescription();
	
	/**
	 * Set standard lights and initialise scene with the bare minimum.
	 * Override to change.
	 * @param sceneObjectContainer
	 * @param studio
	 */
	public abstract void initialiseSceneAndLights(SceneObjectContainer scene, Studio studio);
	
//	/**
//	 * Override if necessary
//	 * @param cameraFrameScene
//	 * @param studio
//	 */
//	public void initialiseCameraFrameScene(EditableSceneObjectCollection cameraFrameScene, Studio studio)
//	{
//		// clear the camera-frame scene...
//		cameraFrameScene.clear();
//		
//		// ... and add a few cool objects
//		
//		// the floor
//		cameraFrameScene.addSceneObject(new EditableScaledParametrisedParallelogram(
//				"floor",	// description, 
//				new Vector3D(-5, -4, 0),	// corner, 
//				new Vector3D(10, 0, 0),	// spanVector1,
//				new Vector3D(0, 0, 10),	// spanVector2, 
//				0,	// suMin,
//				10,	// suMax,
//				0,	// svMin,
//				10,	// svMax,
//				new EditableSurfaceTiling(SurfaceColour.GREY80_SHINY, SurfaceColour.WHITE_SHINY, 1, 1, cameraFrameScene),	// surfaceProperty,
//				cameraFrameScene,	// parent,
//				studio
//				),
//				false
//				);
//		// Tim's head
//		cameraFrameScene.addSceneObject(new EditableTimHead(
//				"Tim's head",	// description,
//				new Vector3D(0, -2, 5),	// centre,
//				1,	// radius,
//				new Vector3D(0, 0, -1),	// frontDirection,
//				new Vector3D(0, 1, 0),	// topDirection,
//				new Vector3D(1, 0, 0),	// rightDirection,
//				cameraFrameScene,	// parent, 
//				studio
//				),
//				false
//				);
//		// Tim's head's pedestal
//		cameraFrameScene.addSceneObject(new EditableCuboid(
//				"Tim's head's pedestal",	// description,
//				new Vector3D(0, -3.5, 5),	// centre,
//				new Vector3D(1, 0, 0),	// centre2centreOfFace1,
//				new Vector3D(0, 0.5, 0),	// centre2centreOfFace2,
//				new Vector3D(0, 0, 1),	// centre2centreOfFace3,
//				SurfaceColour.GREY20_MATT,	// surfaceProperty,
//				cameraFrameScene,	// parent,
//				studio
//				),
//				false
//				);
//	}
//	
//	/**
//	 * Override if necessary
//	 * @param focusScene
//	 * @param studio
//	 */
//	public CameraClass getEyeViewCamera(EditableSceneObjectCollection focusScene, EditableSceneObjectCollection cameraFrameScene, Studio studio)
//	{
//		//
//		// Note that the view direction and basis Vector3Ds of the detector are chosen such that
//		// the x, y, z axes form a LEFT-handed coordinate system.
//		// The reason is that, in the photo, the positive x direction is then to the right,
//		// the positive y direction is upwards, and the camera looks in the positive z direction.
//
//		return new EditableRelativisticAnyFocusSurfaceCamera(
//				"Eye view",
//				new Vector3D(0, 0, 0),	// centre of aperture
//				new Vector3D(0, 0, 1),	// viewDirection
//				new Vector3D(0, 1, 0),	// top direction vector
//				HORIZONTAL_VIEW_ANGLE,	// horiontalViewAngle in degrees; 2*MyMath.rad2deg(Math.atan(2./10.)) gives same view angle as in previous version
//				new Vector3D(0, 0, 0),	// beta
//				imageCanvasSizeX, imageCanvasSizeY,	// logical number of pixels
//				ExposureCompensationType.EC0,	// exposure compensation +0
//				1000,	// maxTraceLevel
//				focusScene,
//				cameraFrameScene,
//				ApertureSizeType.PINHOLE,	// aperture size
//				QualityType.RUBBISH,	// blur quality
//				QualityType.NORMAL	// anti-aliasing quality
//		);
//	}
//	
//	/**
//	 * Override if necessary
//	 * @param studio
//	 */
//	public CameraClass getTopCamera(Studio studio)
//	{
//		return new EditableOrthographicCameraTop(
//				"Top view",
//				0,	// xCentre
//				10,	// zCentre
//				10,	// zLength
//				imageCanvasSizeX, imageCanvasSizeY,	// logical number of pixels
//				100,	// maxTraceLevel
//				QualityType.NORMAL	// anti-aliasing quality
//				);
//	}
//
//	/**
//	 * Override if necessary
//	 * @param studio
//	 */
//	public CameraClass getSideCamera(Studio studio)
//	{
//		return new EditableOrthographicCameraSide(
//				"Side view",
//				0,	// xCentre
//				10,	// zCentre
//				10,	// zLength
//				imageCanvasSizeX, imageCanvasSizeY,	// logical number of pixels
//				100,	// maxTraceLevel
//				QualityType.NORMAL	// anti-aliasing quality
//				);
//	}
//
//	/**
//	 * Override if necessary
//	 * @param studio
//	 */
//	public CameraClass getAnaglyphCamera(EditableSceneObjectCollection focusScene, EditableSceneObjectCollection cameraFrameScene, Studio studio)
//	{
//		double anaglyphViewDistance = 5;
//		
//		return new EditableRelativisticAnaglyphCamera(
//				"Anaglyph 3D",
//				new Vector3D(0, 0, 0),	// betweenTheEyes: middle between two eyes
//				new Vector3D(0, 0, anaglyphViewDistance),	// centre of view: the point in the centre of both eyes' field of view
//				new Vector3D(2*anaglyphViewDistance*Math.tan(MyMath.deg2rad(HORIZONTAL_VIEW_ANGLE/2)), 0, 0),	// horizontal span vector
//				new Vector3D(0.4, 0, 0),	// eyeSeparation: separation between the eyes
//				SpaceTimeTransformationType.LORENTZ_TRANSFORMATION,
//				new Vector3D(0, 0, 0),	// beta
//				imageCanvasSizeX, imageCanvasSizeY,	// logical number of pixels
//				ExposureCompensationType.EC0,
//				100,	// maxTraceLevel
//				focusScene,
//				cameraFrameScene,
//				ApertureSizeType.PINHOLE,	// aperture size
//				false,	// colour
//				QualityType.RUBBISH,	// blur quality
//				QualityType.NORMAL	// anti-aliasing quality
//				); 
//	}
//
//	/**
//	 * Override if necessary
//	 * @param studio
//	 */
//	public CameraClass get3DTVCamera(EditableSceneObjectCollection focusScene, EditableSceneObjectCollection cameraFrameScene, Studio studio)
//	{
//		double the3DTVViewDistance = 5;
//		
//		return new EditableRelativistic3DTVCamera(
//				"3D TV",
//				new Vector3D(0, 0, 0),	// betweenTheEyes: middle between two eyes
//				new Vector3D(0, 0, the3DTVViewDistance),	// centre of view: the point in the centre of both eyes' field of view
//				new Vector3D(2*the3DTVViewDistance*Math.tan(MyMath.deg2rad(HORIZONTAL_VIEW_ANGLE/2)), 0, 0),	// horizontal span vector
//				new Vector3D(0.2, 0, 0),	// eyeSeparation: separation between the eyes; these TVs are usually HUGE!
//				new Vector3D(0, 0, 0),	// beta
//				Format3DType.SS1080,
//				ExposureCompensationType.EC0,
//				100,	// maxTraceLevel
//				focusScene,
//				cameraFrameScene,
//				ApertureSizeType.PINHOLE,	// aperture size
//				QualityType.RUBBISH,	// blur quality
//				QualityType.NORMAL	// anti-aliasing quality
//				); 
//	}
//
//	/**
//	 * Override if necessary
//	 * @param studio
//	 */
//	public CameraClass getAutostereogramCamera(Studio studio)
//	{
//		double autostereogramViewDistance = 5;
//		return new EditableAutostereogramCamera(
//				"Autostereogram 3D",	// "Magic eye",
//				0.8,	// betweenTheEyes: middle between two eyes
//				new Vector3D(0., 0., autostereogramViewDistance),	// lookAtPoint: the point in the centre of both eyes' field of view
//				2*autostereogramViewDistance*Math.tan(MyMath.deg2rad(HORIZONTAL_VIEW_ANGLE/2)),	// stereogram width; originally 2
//				1,	// dots per image pixel
//				1.5,	// radius of dots in image pixels
//				imageCanvasSizeX, imageCanvasSizeY,	// logical number of pixels
//				100,	// maxTraceLevel
//				QualityType.NORMAL	// anti-aliasing quality
//				); 
//	}
}
