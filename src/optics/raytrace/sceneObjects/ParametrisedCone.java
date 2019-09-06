package optics.raytrace.sceneObjects;

import java.io.*;

import math.*;
import optics.raytrace.core.*;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.utility.CopyModeType;

/**
 * 
 * Container class for the curved surface of a cone and the base.
 * 
 * @see ConeTop
 * @see Disc
 * 
 * @author Dean
 *
 */

public class ParametrisedCone extends SceneObjectContainer implements Serializable
{
	private static final long serialVersionUID = -8276823993705120700L;

	private Vector3D
		apex,
		axis;	// the normalised axis direction
	protected boolean open;	// true means base surface is not shown
	private double theta, height;
	private SurfaceProperty surfaceProperty;

	/**
	 * @param description
	 * @param apex
	 * @param axis
	 * @param open
	 * @param theta
	 * @param height
	 * @param surfaceProperty
	 * @param studio
	 */
	public ParametrisedCone(String description, Vector3D apex, Vector3D axis, boolean open, double theta, double height, SurfaceProperty surfaceProperty, SceneObject parent, Studio studio)
	{	
		super(description, parent, studio);
		
		setApex(apex);
		setAxis(axis);
		setOpen(open);
		setTheta(theta);
		setHeight(height);
		setSurfaceProperty(surfaceProperty);

		addSceneObjects();
	}
	
	public ParametrisedCone(ParametrisedCone original)
	{
		// initialise SceneObjectContainer; clone any scene objects in it
		super(original, CopyModeType.CLONE_DATA);
		
		setApex(original.getApex().clone());
		setAxis(original.getAxis().clone());
		setOpen(original.isOpen());
		setTheta(original.getTheta());
		setHeight(original.getHeight());
		setSurfaceProperty(original.getSurfaceProperty().clone());
		
		// addSceneObjects();	// objects were already cloned
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.sceneObjects.SceneObjectContainer#clone()
	 */
	@Override
	public ParametrisedCone clone()
	{
		return new ParametrisedCone(this);
	}
	
	protected void addSceneObjects()
	{
		addSceneObject(new ParametrisedConeTop(getDescription(), getApex(), getAxis(), getTheta(), getHeight(), getSurfaceProperty(), this, getStudio()));

		if(!open)
		{
			// show the base surface
			double r = height*Math.tan(getTheta());	//radius of the disc
			Vector3D c = Vector3D.sum(getApex(), getAxis().getProductWith(getHeight()));	// center of the disc

			addSceneObject(new ParametrisedDisc(getDescription(), c, getAxis(), r, getSurfaceProperty(), this, getStudio()));
		}
	}
	

	@Override
	public String toString()
	{						//outputs cone's description
		return description + " [Cone]";
	}

	public Vector3D getApex() {
		return apex;
	}

	public void setApex(Vector3D apex) {
		this.apex = apex;
	}

	public Vector3D getAxis() {
		return axis;
	}

	public void setAxis(Vector3D axis) {
		this.axis = axis.getNormalised();
	}

	public boolean isOpen() {
		return open;
	}

	public void setOpen(boolean open) {
		this.open = open;
	}

	public double getTheta() {
		return theta;
	}

	public void setTheta(double theta) {
		this.theta = theta;
	}

	public double getHeight() {
		return height;
	}

	public void setHeight(double height) {
		this.height = height;
	}

	public SurfaceProperty getSurfaceProperty() {
		return surfaceProperty;
	}

	public void setSurfaceProperty(SurfaceProperty surfaceProperty) {
		this.surfaceProperty = surfaceProperty;
	}
	
	@Override
	public String getType()
	{
		return "Cone";
	}
}
