package optics.raytrace.GUI.lowLevel;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import math.MyMath;

import optics.DoubleColour;
import optics.raytrace.GUI.core.CameraWithRayForImagePixel;
import optics.raytrace.GUI.core.IPanel;
import optics.raytrace.GUI.core.IPanelComponent;
import optics.raytrace.GUI.sceneObjects.EditableObjectCoordinateSystem;
import optics.raytrace.GUI.sceneObjects.EditableSceneObjectCollection;
import optics.raytrace.core.One2OneParametrisedObject;
import optics.raytrace.core.ParametrisedObject;
import optics.raytrace.core.Ray;
import optics.raytrace.core.RaySceneObjectIntersection;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.Studio;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.surfaces.SurfaceColour;

/**
 * A BufferedImageCanvas that can give information regarding which object the mouse cursor is hovering over etc.
 * @author Johannes Courtial
 */
public class RaytracingImageCanvas extends BufferedImageCanvas
implements MouseListener, MouseMotionListener, ActionListener
{
	private static final long serialVersionUID = 8594474760681044299L;

	/**
	 * when the user clicks on the image, a ray is traced backwards from the camera in the direction of the click position;
	 * this variable holds the first intersection between this ray and the scene
	 */
	private RaySceneObjectIntersection lastClickIntersection;
	
	private StatusIndicator statusIndicator;
	private JPopupMenu popupMenu;
	private MouseEvent popupMenuMouseEvent;
	private static final String
		EDIT_SCENE_OBJECT_STRING = "Edit scene object",
		ADD_LOCAL_COORDINATE_AXES_STRING = "Add surface-coordinate axes";
	private boolean showEditSceneObjectMenuItem, showAddLocalCoordinateAxesMenuItem;
	
	/**
	 * The IPanel that displays this canvas.
	 * This information is needed so that scene objects can be edited by clicking the mouse.
	 */
	private IPanel iPanel;
    
    private Studio studio;

    /**
     * Create a new panel and display in it the image.
     * @param image
     */
    public RaytracingImageCanvas(
    		BufferedImage image,
    		int imageCanvasSizeX,
    		int imageCanvasSizeY,
    		StatusIndicator statusIndicator,
    		boolean showEditSceneObjectMenuItem,
    		boolean showAddLocalCoordinateAxesMenuItem,
    		Studio studio, IPanel iPanel
    	)
    {
    	super(image, imageCanvasSizeX, imageCanvasSizeY);
    	
    	this.showEditSceneObjectMenuItem = showEditSceneObjectMenuItem;
    	this.showAddLocalCoordinateAxesMenuItem = showAddLocalCoordinateAxesMenuItem;
    	
    	initialiseInformativeBits(statusIndicator, studio, iPanel);
    }

    /**
     * Create a new panel and display in it the image.
     * @param image
     */
    public RaytracingImageCanvas(BufferedImage image, int imageCanvasSizeX, int imageCanvasSizeY, StatusIndicator statusIndicator, Studio studio, IPanel iPanel)
    {
    	super(image, imageCanvasSizeX, imageCanvasSizeY);
    	
    	showEditSceneObjectMenuItem = true;
    	showAddLocalCoordinateAxesMenuItem = true;
    	
    	initialiseInformativeBits(statusIndicator, studio, iPanel);
    }

    /**
     * Create a new panel onto which the rendered image will be displayed.
     */
    public RaytracingImageCanvas(int imageCanvasSizeX, int imageCanvasSizeY, StatusIndicator statusIndicator, Studio studio, IPanel iPanel)
    {
    	super(imageCanvasSizeX, imageCanvasSizeY);
    	
    	showEditSceneObjectMenuItem = true;
    	showAddLocalCoordinateAxesMenuItem = true;
    	
    	initialiseInformativeBits(statusIndicator, studio, iPanel);
    }

    private void initialiseInformativeBits(StatusIndicator statusIndicator, Studio studio, IPanel iPanel)
    {
    	setStatusIndicator(statusIndicator);
    	setStudio(studio);
    	setiPanel(iPanel);
    	
		popupMenu = new JPopupMenu();
		JMenuItem menuItem;
		if(showEditSceneObjectMenuItem)
		{
			menuItem = new JMenuItem(EDIT_SCENE_OBJECT_STRING);
			menuItem.addActionListener(this);
			popupMenu.add(menuItem);
		}
		if(showAddLocalCoordinateAxesMenuItem)
		{
			menuItem = new JMenuItem(ADD_LOCAL_COORDINATE_AXES_STRING);
			menuItem.addActionListener(this);
			popupMenu.add(menuItem);
    	}
    	
    	addMouseListener(this);
    	addMouseMotionListener(this);
    }
    
    /**
	 * when the user clicks on the image, a ray is traced backwards from the camera in the direction of the click position;
	 * this method returns the first intersection between the ray and the scene for the last click
	 * 
     * @return the closest intersection with the scene along the line of sight in which the click position is seen
     */
    public RaySceneObjectIntersection getLastClickIntersection() {
		return lastClickIntersection;
	}

	public StatusIndicator getStatusIndicator()
    {
		return statusIndicator;
	}

	public void setStatusIndicator(StatusIndicator statusIndicator)
	{
		this.statusIndicator = statusIndicator;
	}
    
    public Studio getStudio() {
		return studio;
	}

	public void setStudio(Studio studio) {
		this.studio = studio;
	}

	public IPanel getiPanel() {
		return iPanel;
	}

	public void setiPanel(IPanel iPanel) {
		this.iPanel = iPanel;
	}

	/*
     * (non-Javadoc)
     * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
     */
	@Override
	public void mouseClicked(MouseEvent e)
	{
        // Has button 2 (or alt+button1) been pressed?
		if((e.getModifiers() & InputEvent.BUTTON2_MASK) == InputEvent.BUTTON2_MASK)
		{
			popupMenuMouseEvent = e;
			// button 2 (or alt + button 1) has been clicked
	        popupMenu.show(e.getComponent(), e.getX(), e.getY());
		}
		else if((e.getModifiers() & InputEvent.BUTTON1_MASK) == InputEvent.BUTTON1_MASK)
		{
			// button 1 has been clicked
			if(e.getClickCount() == 1) setTemporaryStatusToLocalCoordinates(e);
			else if(e.getClickCount() == 2)
			{
				// double click

				// intersect the ray with the scene
				editSceneObject(getRaySceneObjectIntersection(e).o);
			}
		}
	}

	/**
	 * edit the given scene object; works only if iPanel != null
	 * @param sceneObject
	 */
	private void editSceneObject(SceneObject sceneObject)
	{
		if(iPanel != null)
		{
			// go the nearest editable ancestor
			while((sceneObject != null) && (!(sceneObject instanceof IPanelComponent)))
				sceneObject = sceneObject.getParent();

			if((sceneObject != null) && (sceneObject instanceof IPanelComponent))
			{
				getStatusIndicator().setStatus("");
				getStatusIndicator().removeTemporaryStatus();

				IPanelComponent iPanelComponent = (IPanelComponent)sceneObject;

				getiPanel().addFrontComponent(iPanelComponent, EDIT_SCENE_OBJECT_STRING);
				iPanelComponent.setValuesInEditPanel();
			}
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseEntered(MouseEvent e) {}

	/*
	 * (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseExited(MouseEvent e)
	{
		getStatusIndicator().removeTemporaryStatus();
	}

	/*
	 * (non-Javadoc)
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	@Override
	public void mousePressed(MouseEvent e)
	{
		setTemporaryStatusToLocalCoordinates(e);
	}

	/*
	 * (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseReleased(MouseEvent e)
	{
		mouseMoved(e);
	}

	@Override
	public void mouseDragged(MouseEvent e)
	{
		setTemporaryStatusToLocalCoordinates(e);
	}

	/*
	 * (non-Javadoc)
	 * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseMoved(MouseEvent e)
	{
		// intersect the ray with the scene
		RaySceneObjectIntersection i=getRaySceneObjectIntersection(e);
		
		String description;
		if(i == RaySceneObjectIntersection.NO_INTERSECTION)
		{
			description = "No scene object in this direction";
		}
		else
		{
			description=i.o.getDescription();
			for(SceneObject o=i.o.getParent(); o!=null; o=o.getParent())
				description = o.getDescription() + " > " + description;
			// finally, add the position
			description = i.p + " on " + description;
		}
		
		// display what the ray intersects with
		getStatusIndicator().setTemporaryStatus("[" + description + "]");
	}

	private RaySceneObjectIntersection getRaySceneObjectIntersection(MouseEvent e)
	{
		// get the coordinates of the mouse in the picture
		Point point=e.getPoint();
		
		// set the camera pixels to the correct size
		// Dimension size = this.getSize();
		
    	CameraWithRayForImagePixel camera = (CameraWithRayForImagePixel)(getStudio().getCamera());
		
//    	System.out.println("size x, y and x,y ="+ 
//    			getImageScreenSize().width + ", " +
//				getImageScreenSize().height + ", " +
//				(point.x - getImageOffsetX()) + ", " +
//				(point.y - getImageOffsetY())
//			);
    	
		// calculate the corresponding camera ray
		Ray ray=camera.getRayForImagePixel(
				getImageScreenSize().width,	// (int)(size.getWidth()),
				getImageScreenSize().height,	// (int)(size.getHeight()),
				point.x - getImageOffsetX(),
				point.y - getImageOffsetY()
			);
		
		// intersect the ray with the scene
		return getStudio().getScene().getClosestRayIntersection(ray);
		
		// TODO add offset here
	}
	
	private void setTemporaryStatusToLocalCoordinates(MouseEvent e)
	{
		// intersect the ray with the scene
		lastClickIntersection = getRaySceneObjectIntersection(e);

		String coordinates;
		if(lastClickIntersection.o instanceof ParametrisedObject)
		{
			ArrayList<String> parameterNames = ((ParametrisedObject)lastClickIntersection.o).getSurfaceCoordinateNames();

			coordinates="[scene object's surface coordinates ("
				+ parameterNames.get(0) + ", " + parameterNames.get(1) + ") = "
				+ ((ParametrisedObject)lastClickIntersection.o).getSurfaceCoordinates(lastClickIntersection.p)
				+ "]";
		}
		else
		{
			coordinates="[scene object not parametrised]";
		}

		// display the surface coordinates
		getStatusIndicator().setTemporaryStatus(coordinates);
	}


	@Override
	public void actionPerformed(ActionEvent arg0)
	{
		if(arg0.getActionCommand().equals(EDIT_SCENE_OBJECT_STRING))
		{
			// the "Edit scene object" popup menu item has been selected
			editSceneObject(getRaySceneObjectIntersection(popupMenuMouseEvent).o);
		}
		else if(arg0.getActionCommand().equals(ADD_LOCAL_COORDINATE_AXES_STRING))
		{
			// the "Add surface-coordinate axes" popup menu item has been selected
			RaySceneObjectIntersection i = getRaySceneObjectIntersection(popupMenuMouseEvent);
			SceneObject sceneObject = i.o;
			
			if(sceneObject instanceof One2OneParametrisedObject)
			{
				SceneObject scene = getStudio().getScene();
				EditableSceneObjectCollection esoc;
				
				if(scene instanceof EditableSceneObjectCollection)
				{
					// scene is an EditableSceneObjectCollection -- good!
					esoc = (EditableSceneObjectCollection)scene;
				}
				else
				{
					// scene is *not* an EditableSceneObjectCollection -- make it one!
					if(scene instanceof SceneObjectContainer)
					{
						// if the scene is a SceneObjectContainer, make it instead an EditableSceneObjectCollection
						esoc = new EditableSceneObjectCollection((SceneObjectContainer)scene, false);
					}
					else 
					{
						// otherwise, make the scene part of an EditableSceneObjectCollection
						esoc = new EditableSceneObjectCollection("the scene", false, null, getStudio());
						esoc.addSceneObject(scene);
					}
					
					// let the studio know that we've changed the scene
					getStudio().setScene(esoc);
				}

				EditableObjectCoordinateSystem coordinateSystem = new EditableObjectCoordinateSystem(
						sceneObject.getDescription() + "'s surface-coordinate system",	// description
						(One2OneParametrisedObject)sceneObject,	// object
						((One2OneParametrisedObject)sceneObject).getSurfaceCoordinates(i.p),	// coordinates
						0.025,	// shaftRadius
						MyMath.deg2rad(30),	// tipAngle
						0.1,	// tipLength
						new SurfaceColour(DoubleColour.RED, DoubleColour.WHITE, true),	//  surfacePropertyU
						new SurfaceColour(DoubleColour.GREEN, DoubleColour.WHITE, true),	//  surfacePropertyV
						new SurfaceColour(DoubleColour.BLUE, DoubleColour.WHITE, true),	//  surfacePropertyN
						esoc,	// parent
						getStudio()
					);

				esoc.addSceneObject(coordinateSystem);
				editSceneObject(coordinateSystem);
				
				getStatusIndicator().setStatus(sceneObject.getDescription() + "'s surface-coordinate system added and ready to be rendered");
				getStatusIndicator().removeTemporaryStatus();
			}
			else
			{
				getStatusIndicator().setStatus("Cannot add surface-coordinate axes as scene object is not suitably parametrised");
				getStatusIndicator().removeTemporaryStatus();				
			}
		}
	}

}
