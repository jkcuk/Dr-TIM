package optics.raytrace.slightlyInteractive;

import java.awt.Container;
import java.awt.FlowLayout;

import javax.swing.*;

import math.MyMath;
import math.Vector3D;
import optics.DoubleColour;
import optics.raytrace.GUI.cameras.EditableRelativisticAnyFocusSurfaceCamera;
import optics.raytrace.GUI.cameras.QualityType;
import optics.raytrace.GUI.core.*;
import optics.raytrace.GUI.lowLevel.ApertureSizeType;
import optics.raytrace.GUI.lowLevel.GUIPanelSimple;
import optics.raytrace.GUI.sceneObjects.EditableParametrisedPlane;
import optics.raytrace.GUI.sceneObjects.EditableScaledParametrisedSphere;
import optics.raytrace.GUI.sceneObjects.EditableSceneObjectCollection;
import optics.raytrace.GUI.sceneObjects.EditableTimHead;
import optics.raytrace.GUI.surfaces.SurfacePropertyPanel;
import optics.raytrace.cameras.PinholeCamera.ExposureCompensationType;
import optics.raytrace.core.LightSource;
import optics.raytrace.core.Studio;
import optics.raytrace.surfaces.SurfaceColour;
import optics.raytrace.surfaces.SurfaceColourLightSourceIndependent;
import optics.raytrace.surfaces.Transparent;

/**
 * A cut-down version of TIMInteractiveBits, making this slightly interactive (si)
 * 
 * This is now handled by NonInteractiveTIM and NonInteractiveTIMEngine.
 * 
 * First call the constructor, then alter the canvas size, then call startInteractiveBits().
 * Override the method createStudio to change the initial scene.
 * 
 * @see optics.raytrace.NonInteractiveTIM
 * @see optics.raytrace.NonInteractiveTIMEngine
 * @see optics.raytrace.TIMInteractiveBits
 */
public class TIMsiBits
{
	public static final double
		SIZE_FACTOR = .75;	// <1 = smaller, 1 = normal size, >1 = big;

	public static final int
		IMAGE_CANVAS_SIZE_X = (int)(SIZE_FACTOR*640),
		IMAGE_CANVAS_SIZE_Y = (int)(SIZE_FACTOR*480);

	protected int
		imageCanvasSizeX = IMAGE_CANVAS_SIZE_X,
		imageCanvasSizeY = IMAGE_CANVAS_SIZE_Y;
		
	// should saving of images be allowed?  Set to false if running as an applet
	private boolean allowSaving = true;
	
	// this is the screen container where everything goes
	private Container contentPane;
	
	protected Studio studio;
	
	
	//
	// Override these things
	//
	
	// add any parameters, and the panels for editing them, here
	
	// (none in this bare-bones version)

	/**
	 * Override this method to return a panel that includes fields for editing the parameters
	 * @return
	 */
	public JPanel createParametersPanel()
	{
		return new JPanel();
	}
	
	/**
	 * Override this method to initialise any parameter fields
	 */
	public void setValuesInParametersPanel()
	{
	}

	/**
	 * Override this method to read out any parameter fields
	 */
	public void acceptValuesInParametersPanel()
	{
	}
	

	/**
	 * Alter or override this method to change the scene.
	 * Called both when run as an applet (then this is the initial scene to be rendered)
	 * and when run as a Java application (then this is the scene, which then gets
	 * rendered).
	 */
	public void populateStudio()
	{
		//
		// the lights
		//
		
		studio.setLights(LightSource.getStandardLightsFromBehind());

		//
		// the scene
		//
		
		EditableSceneObjectCollection scene = new EditableSceneObjectCollection("the scene", false, null, studio);
		
		// add the standard scene objects, the sky sphere...
		scene.addSceneObject(new EditableScaledParametrisedSphere(
				"sky",
				new Vector3D(0,0,0),	// centre
				MyMath.HUGE,	// huge radius
				new SurfaceColourLightSourceIndependent(DoubleColour.LIGHT_BLUE, true),
				scene, 
				studio
		));

		// ... and the chequerboard floor
		scene.addSceneObject(new EditableParametrisedPlane(
				"chequerboard floor", 
				new Vector3D(0, -1, 0),	// point on plane
				new Vector3D(0, 0, 1),	// Vector3D 1 that spans plane
				new Vector3D(1, 0, 0),	// Vector3D 2 that spans plane
				// true,	// shadow-throwing
				SurfacePropertyPanel.TILED, // new SurfaceTiling(SurfaceColour.GREY80_SHINY, SurfaceColour.WHITE_SHINY, 1, 1)
				scene,
				studio
		));
		
		// and add Tim's head...
		scene.addSceneObject(new EditableTimHead(
				"Tim's head",
				new Vector3D(0, 0, 10),
				1,	// radius
				new Vector3D(0, 0, -1),	// front direction
				new Vector3D(0, 1, 0),	// top direction
				new Vector3D(1, 0, 0),	// right direction
				scene,
				studio
			));
		
		// ... standing in front of an invisible wall
		EditableParametrisedPlane wall = new EditableParametrisedPlane(
				"invisible wall",
				new Vector3D(0, 0, 12),	// point on plane
				new Vector3D(0, 0, 1),	// normal
				Transparent.PERFECT,
				scene,
				studio
			);
		scene.addSceneObject(wall);
		
		studio.setScene(scene);

		//
		// finally, the camera
		//
		
		EditableSceneObjectCollection focusScene = new EditableSceneObjectCollection("focus scene", false, studio.getScene(), studio);
		focusScene.addSceneObject(new EditableParametrisedPlane(
				"focussing plane",
				new Vector3D(0, 0, 15),	// point on plane
				new Vector3D(0, 0, 1),	// normal to plane
				SurfaceColour.BLACK_SHINY,
				studio.getScene(),
				studio
		));
		EditableRelativisticAnyFocusSurfaceCamera camera = new EditableRelativisticAnyFocusSurfaceCamera(
				"camera",
				new Vector3D(0, 0, 0),	// centre of aperture
				new Vector3D(0, 0, 10),	// lookAtPoint: the point in the centre of the field of view
				new Vector3D(4, 0, 0),	// horizontal span vector
				new Vector3D(0, 0, 0),	// beta
				imageCanvasSizeX, imageCanvasSizeY,	// logical number of pixels
				ExposureCompensationType.EC0,
				100,	// maxTraceLevel
				focusScene,
				new EditableSceneObjectCollection("camera-frame scene", false, studio.getScene(), studio),
				ApertureSizeType.PINHOLE,	// aperture size
				QualityType.RUBBISH,	// blur quality
				QualityType.NORMAL	// anti-aliasing quality
		);
		
		studio.setCamera(camera);
	}
	
	// the amount by which the iPanel is bigger than the image canvas;
	// override if necessary
	protected int getAdditionalWidth()
	{
		return 20;
	}
	
	protected int getAdditionalHeight()
	{
		return 100;
	}

	//
	// Override the stuff to here
	//
	
	
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
	 * 
	 * The internal workings...
	 * 
	 */

	// the panel that shows the breadcrumbs, the status, and the main panel in the centre
	private IPanel iPanel;
	
	// the panel that shows the image and the main buttons
	private GUIPanelSimple renderPanel;
	
	
	public void render()
	{
		if(renderPanel != null) renderPanel.render();
	}


	/**
	 * set up the GUI; don't start any calculations yet!
	 */
	public void createAndShowGUI()
	{
		iPanel = new IPanel(imageCanvasSizeX+getAdditionalWidth(), imageCanvasSizeY+getAdditionalHeight());
		// iPanel = new IPanel();
		
	    // boolean allowSaving = true;	// default: saving allowed
		
		// create a new studio
		studio = new Studio();
		populateStudio();

		// create the GUI component that will handle all the user interaction
	    renderPanel = new GUIPanelSimple(this, imageCanvasSizeX, imageCanvasSizeY, allowSaving, iPanel);

		// Finally, display rayTracerPanel (by adding it to this applet's content pane).
		iPanel.addFrontComponent(renderPanel, "Simple TIM");
		getContentPane().setLayout(new FlowLayout());
		getContentPane().add(iPanel);
	}

	public Studio getStudio() {
		return studio;
	}

	public void setStudio(Studio studio) {
		this.studio = studio;
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
