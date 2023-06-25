package optics.raytrace;

import java.awt.BorderLayout;
import java.awt.Container;
import java.util.ArrayList;

import javax.swing.*;

import math.*;
import math.SpaceTimeTransformation.SpaceTimeTransformationType;
import optics.raytrace.surfaces.Checked;
import optics.raytrace.surfaces.SurfaceColour;
import optics.raytrace.core.*;
import optics.DoubleColour;
import optics.raytrace.GUI.cameras.*;
import optics.raytrace.GUI.core.*;
import optics.raytrace.GUI.lowLevel.ApertureSizeType;
import optics.raytrace.GUI.lowLevel.Format3DType;
import optics.raytrace.GUI.lowLevel.GUIPanel;
import optics.raytrace.GUI.sceneObjects.*;
import optics.raytrace.GUI.surfaces.EditableSurfaceTiling;
import optics.raytrace.cameras.PinholeCamera.ExposureCompensationType;

/**
 * TODO add drawings of the geometry of cylinders etc.
 * 
 * TODO add a transformation panel at the bottom of the dialog for editing SceneObjects
 */


/**
 * Default The METATOY Raytracer (TIM) interactive bits
 * 
 * First call the constructor, then alter the canvas size, then call startInteractiveBits().
 * Override the method createStudio to change the initial scene.
 */
public class TIMInteractiveBits
{
	public static final double
		SIZE_FACTOR = 1,	// <1 = smaller, 1 = normal size, >1 = big;
							// .65 smallest that accommodates all buttons
		HORIZONTAL_VIEW_ANGLE = 20;	// degrees

	public static final int
		IMAGE_CANVAS_SIZE_X = (int)(SIZE_FACTOR*640),
		IMAGE_CANVAS_SIZE_Y = (int)(SIZE_FACTOR*480);

	private int
		imageCanvasSizeX = IMAGE_CANVAS_SIZE_X,
		imageCanvasSizeY = IMAGE_CANVAS_SIZE_Y;
	
	// should saving of images be allowed?  Set to false if running as an applet
	private boolean allowSaving = true;
	
	// keep track of the eye pupil, so that its size can be changed when the aperture
	// size changes
    // TODO temporarily (?) uncommented eye-related stuff
//	private EditableSphericalCap eyePupil;
	
	// this is the screen container where everything goes
	private Container contentPane;
	
	/**
	 * setup and start interactive bits
	 */
	public void startInteractiveBits()
	{
		try
		{
			// Create and show the GUI.
			// The "invokeAndWait" stuff is about doing this safely in the event-dispatch thread.
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					createAndShowGUI();
				}
			});
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		// Now do an initial render.
		// (This will be done in a background thread -- see RayTracerPanel.render().)
		renderPanel.render();
	}

	/**
	 * Alter or override this method to change the scene.
	 * Called both when run as an applet (then this is the initial scene to be rendered)
	 * and when run as a Java application (then this is the scene, which then gets
	 * rendered).
	 */
	public Studio createStudio()
	{
		Studio studio = new Studio();
		
		// studio.setLights(LightSource.getBrightLightsFromBehind());
		// studio.setLights(LightSource.getStandardLightsFromBehind());
		// studio.setLights(new AmbientLight("Dull light", DoubleColour.WHITE));

		// the scene
		EditableSceneObjectCollection scene = new EditableSceneObjectCollection("the scene", false, null, studio);
		
		// add the empty scene to the studio before filling it with scene objects
		// so that calls of the getStudio().getScene() variety (which happen when
		// scene objects are initialised so that the Teleporting ComboBox is filled
		// with the right choices) actually point to a scene!
		studio.setScene(scene);

		// add the eye to the scene
		// TODO temporarily (?) uncommented eye-related stuff
//		double eyeRadius = 0.5;
//		Eye eye = new Eye(
//				"eye",
//				new Vector3D(0, 0, -0.5-3*MyMath.SMALL),	// centre
//				new Vector3D(0, 0, 1),	// view direction
//				eyeRadius,	// radius
//				eyeRadius*0.6,	// iris radius
//				eyeRadius*0.2,	// pupil radius,
//				SurfaceColour.BLUE_SHINY,	// iris colour
//				scene,
//				studio
//		);
//		eyePupil = eye.getPupil();
//		scene.addSceneObject(eye, false);	// make the eye initially invisible

		// populate the scene with other scene objects
		// populateSceneFunEdition(scene.getSceneObjectContainer(), studio);	// "Fun" edition
		StudioInitialisationType.initialiseSceneAndLights(StudioInitialisationType.DEFAULT, scene.getSceneObjectContainer(), studio);		// 2016 Christmas edition
		
		return studio;
	}
	
	/**
	 * @param studio
	 * @return	The standard focus scene
	 */
	public static EditableSceneObjectCollection getFocusScene(Studio studio)
	{
		EditableSceneObjectCollection focusScene = new EditableSceneObjectCollection("focus scene", false, studio.getScene(), studio);
		focusScene.addSceneObject(new EditableParametrisedPlane(
				"focussing plane",
				new Vector3D(0, 0, 10),	// point on plane
				new Vector3D(0, 0, 1),	// normal to plane
				SurfaceColour.BLACK_SHINY,
				studio.getScene(),
				studio
		));
		return focusScene;
	}
	
	public EditableRelativisticAnyFocusSurfaceCamera getEyeViewCamera(
			EditableSceneObjectCollection focusScene,
			EditableSceneObjectCollection cameraFrameScene
		)
	{
//		EditableAnyFocusSurfaceCamera eyeViewCamera = new EditableAnyFocusSurfaceCamera(
//				"Eye view",
//				new Vector3D(0, 0, 0),	// centre of aperture
//				new Vector3D(0, 0, 10),	// lookAtPoint: the point in the centre of the field of view
//				new Vector3D(4, 0, 0),	// horizontal span vector
//				imageCanvasSizeX, imageCanvasSizeY,	// logical number of pixels
//				1000,	// maxTraceLevel
//				focusScene,
//				ApertureSizeComboBox.PINHOLE,	// aperture size
//				QualityComboBox.RUBBISH,	// blur quality
//				QualityComboBox.NORMAL	// anti-aliasing quality
//		);
		EditableRelativisticAnyFocusSurfaceCamera eyeViewCamera = new EditableRelativisticAnyFocusSurfaceCamera(
				"Eye view",
				new Vector3D(0, 0, 0),	// centre of aperture
				new Vector3D(0, 0, 1),	// viewDirection
				new Vector3D(0, 1, 0),	// top direction vector
				HORIZONTAL_VIEW_ANGLE,	// horiontalViewAngle in degrees; 2*MyMath.rad2deg(Math.atan(2./10.)) gives same view angle as in previous version
				new Vector3D(0, 0, 0),	// beta
				imageCanvasSizeX, imageCanvasSizeY,	// logical number of pixels
				ExposureCompensationType.EC0,	// exposure compensation +0
				1000,	// maxTraceLevel
				focusScene,
				cameraFrameScene,
				ApertureSizeType.PINHOLE,	// aperture size
				QualityType.RUBBISH,	// blur quality
				QualityType.NORMAL	// anti-aliasing quality
		);
		
		return eyeViewCamera;
	}
	
	/**
	 * @return a list of cameras ("Views")
	 */
	private ArrayList<CameraClass> createCameras(Studio studio)
	{
		ArrayList<CameraClass> cameras = new ArrayList<CameraClass>();
		
		// the focus scene shared by the eye-view camera and the anaglyph camera
		EditableSceneObjectCollection focusScene = getFocusScene(studio);
		
		// the camera-frame scene
		EditableSceneObjectCollection cameraFrameScene = new EditableSceneObjectCollection("camera-frame scene", false, studio.getScene(), studio);
		// the floor
		cameraFrameScene.addSceneObject(new EditableScaledParametrisedParallelogram(
				"floor",	// description, 
				new Vector3D(-5, -4, 0),	// corner, 
				new Vector3D(10, 0, 0),	// spanVector1,
				new Vector3D(0, 0, 10),	// spanVector2, 
				0,	// suMin,
				10,	// suMax,
				0,	// svMin,
				10,	// svMax,
				new EditableSurfaceTiling(SurfaceColour.GREY80_SHINY, SurfaceColour.WHITE_SHINY, 1, 1, cameraFrameScene),	// surfaceProperty,
				cameraFrameScene,	// parent,
				studio
				),
				false
				);
		// Tim's head
		cameraFrameScene.addSceneObject(new EditableTimHead(
				"Tim's head",	// description,
				new Vector3D(0, -2, 5),	// centre,
				1,	// radius,
				new Vector3D(0, 0, -1),	// frontDirection,
				new Vector3D(0, 1, 0),	// topDirection,
				new Vector3D(1, 0, 0),	// rightDirection,
				cameraFrameScene,	// parent, 
				studio
				),
				false
				);
		// Tim's head's pedestal
		cameraFrameScene.addSceneObject(new EditableCuboid(
				"Tim's head's pedestal",	// description,
				new Vector3D(0, -3.5, 5),	// centre,
				new Vector3D(1, 0, 0),	// centre2centreOfFace1,
				new Vector3D(0, 0.5, 0),	// centre2centreOfFace2,
				new Vector3D(0, 0, 1),	// centre2centreOfFace3,
				SurfaceColour.GREY20_MATT,	// surfaceProperty,
				cameraFrameScene,	// parent,
				studio
				),
				false
				);
		
		// define the cameras
		//
		// Note that the view direction and basis Vector3Ds of the detector are chosen such that
		// the x, y, z axes form a LEFT-handed coordinate system.
		// The reason is that, in the photo, the positive x direction is then to the right,
		// the positive y direction is upwards, and the camera looks in the positive z direction.

		EditableRelativisticAnyFocusSurfaceCamera eyeViewCamera = getEyeViewCamera(focusScene, cameraFrameScene);
		
	    // TODO temporarily (?) uncommented eye-related stuff
//		eyePupil.setApertureRadius(eyeViewCamera.getApertureRadius());
//		eyePupil.refreshSceneObjects();
		
		EditableOrthographicCameraTop topCamera = new EditableOrthographicCameraTop(
				"Top view",
				0,	// xCentre
				10,	// zCentre
				10,	// zLength
				imageCanvasSizeX, imageCanvasSizeY,	// logical number of pixels
				100,	// maxTraceLevel
				QualityType.NORMAL	// anti-aliasing quality
		);

		EditableOrthographicCameraSide sideCamera = new EditableOrthographicCameraSide(
				"Side view",
				0,	// xCentre
				10,	// zCentre
				10,	// zLength
				imageCanvasSizeX, imageCanvasSizeY,	// logical number of pixels
				100,	// maxTraceLevel
				QualityType.NORMAL	// anti-aliasing quality
		);
		
		double anaglyphViewDistance = 5;
		EditableRelativisticAnaglyphCamera anaglyphCamera = new EditableRelativisticAnaglyphCamera(
				"Anaglyph 3D",
				new Vector3D(0, 0, 0),	// betweenTheEyes: middle between two eyes
				new Vector3D(0, 0, anaglyphViewDistance),	// centre of view: the point in the centre of both eyes' field of view
				new Vector3D(2*anaglyphViewDistance*Math.tan(MyMath.deg2rad(HORIZONTAL_VIEW_ANGLE/2)), 0, 0),	// horizontal span vector
				new Vector3D(0.4, 0, 0),	// eyeSeparation: separation between the eyes
				SpaceTimeTransformationType.LORENTZ_TRANSFORMATION,
				new Vector3D(0, 0, 0),	// beta
				imageCanvasSizeX, imageCanvasSizeY,	// logical number of pixels
				ExposureCompensationType.EC0,
				100,	// maxTraceLevel
				focusScene,
				cameraFrameScene,
				ApertureSizeType.PINHOLE,	// aperture size
				false,	// colour
				QualityType.RUBBISH,	// blur quality
				QualityType.NORMAL	// anti-aliasing quality
		); 

		double the3DTVViewDistance = 5;
		EditableRelativistic3DTVCamera the3DTVCamera = new EditableRelativistic3DTVCamera(
				"3D TV",
				new Vector3D(0, 0, 0),	// betweenTheEyes: middle between two eyes
				new Vector3D(0, 0, the3DTVViewDistance),	// centre of view: the point in the centre of both eyes' field of view
				new Vector3D(2*the3DTVViewDistance*Math.tan(MyMath.deg2rad(HORIZONTAL_VIEW_ANGLE/2)), 0, 0),	// horizontal span vector
				new Vector3D(0.2, 0, 0),	// eyeSeparation: separation between the eyes; these TVs are usually HUGE!
				new Vector3D(0, 0, 0),	// beta
				Format3DType.SS1080,
				ExposureCompensationType.EC0,
				100,	// maxTraceLevel
				focusScene,
				cameraFrameScene,
				ApertureSizeType.PINHOLE,	// aperture size
				QualityType.RUBBISH,	// blur quality
				QualityType.NORMAL	// anti-aliasing quality
		); 

		double autostereogramViewDistance = 5;
		EditableAutostereogramCamera autostereogramCamera = new EditableAutostereogramCamera(
				"Autostereogram 3D",	// "Magic eye",
				0.8,	// betweenTheEyes: middle between two eyes
				new Vector3D(0., 0., autostereogramViewDistance),	// lookAtPoint: the point in the centre of both eyes' field of view
				2*autostereogramViewDistance*Math.tan(MyMath.deg2rad(HORIZONTAL_VIEW_ANGLE/2)),	// stereogram width; originally 2
				1,	// dots per image pixel
				1.5,	// radius of dots in image pixels
				imageCanvasSizeX, imageCanvasSizeY,	// logical number of pixels
				100,	// maxTraceLevel
				QualityType.NORMAL	// anti-aliasing quality
		); 

		cameras.add(eyeViewCamera);
		cameras.add(topCamera);
		cameras.add(sideCamera);
		cameras.add(anaglyphCamera);
		cameras.add(the3DTVCamera);
		cameras.add(autostereogramCamera);

		return cameras;
	}


	/**
	 * 
	 * The internal workings...
	 * 
	 */

	// the panel that shows the breadcrumbs, the status, and the main panel in the centre
	private IPanel iPanel;
	
	// the panel that shows the image and the main buttons
	private GUIPanel renderPanel;


	/**
	 * set up the GUI; don't start any calculations yet!
	 */
	public void createAndShowGUI()
	{
		iPanel = new IPanel(imageCanvasSizeX+20, imageCanvasSizeY+100);
		// iPanel = new IPanel();
		
		// define scene and lights; this also defines "eyePupil"
		Studio studio = createStudio();
		
		// define the cameras; this uses "eyePupil"
		ArrayList<CameraClass> cameras = createCameras(studio);

	    // boolean allowSaving = true;	// default: saving allowed

		// create the GUI component that will handle all the user interaction
	    renderPanel = new GUIPanel(studio, cameras, imageCanvasSizeX, imageCanvasSizeY, allowSaving, iPanel);

		// instead of taking an initial photo, create a blank image of the right size...
		// studio.getCamera().allocatePhotoMemory();

		// ... and display it on rayTracerPanel
	    // renderPanel.setSelectedImage();

	    // TODO temporarily (?) uncommented eye-related stuff
//		eyePupil.setNoClone(true);	// don't clone the pupil so that its radius can be adjusted to be that of the camera
//		renderPanel.setEyePupil(eyePupil);
		
		// Finally, display rayTracerPanel (by adding it to this applet's content pane).
		iPanel.addFrontComponent(renderPanel, "Raytracer main");
		
		// give the content pane a border layout and add the iPanel to the centre,
		// so that the iPanel fills the entire content pane
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(iPanel, BorderLayout.CENTER);
	}

	public void setContentPane(Container contentPane) {
		this.contentPane = contentPane;
	}

	public Container getContentPane() {
		return contentPane;
	}

	public int getImageCanvasSizeX() {
		return imageCanvasSizeX;
	}

	public void setImageCanvasSizeX(int imageCanvasSizeX) {
		this.imageCanvasSizeX = imageCanvasSizeX;
	}

	public int getImageCanvasSizeY() {
		return imageCanvasSizeY;
	}

	public void setImageCanvasSizeY(int imageCanvasSizeY) {
		this.imageCanvasSizeY = imageCanvasSizeY;
	}

	public boolean isAllowSaving() {
		return allowSaving;
	}

	public void setAllowSaving(boolean allowSaving) {
		this.allowSaving = allowSaving;
	}
}
