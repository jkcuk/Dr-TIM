package optics.raytrace.sceneObjects;

import java.io.*; 

import math.*;
import optics.raytrace.core.*;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.utility.CopyModeType;

/**
 * A cylinder with lids.
 * Made from the primitive scene objects CylinderTop and two Discs (the lids).
 * 
 * @see CylinderTop
 * @see Disc
 * 
 * @author Dean et al.
 *
 */
public class Cylinder extends SceneObjectContainer implements Serializable
{
	private static final long serialVersionUID = 1085523470099254285L;

	private Vector3D startPoint, endPoint;
	private double radius;
	private SurfaceProperty surfaceProperty;

	/**
	 * @param description
	 * @param startPoint
	 * @param endPoint
	 * @param radius
	 * @param surfaceProperty
	 * @param parent
	 * @param studio
	 */
	public Cylinder(
			String description,
			Vector3D startPoint,
			Vector3D endPoint,
			double radius,
			SurfaceProperty surfaceProperty,
			SceneObject parent,
			Studio studio
		)
	{
		super(description, parent, studio);
		
		setStartPoint(startPoint);
		setEndPoint(endPoint);
		setRadius(radius);
		setSurfaceProperty(surfaceProperty);
		
		addSceneObjects();
	}

	/**
	 * Create a clone of the original.
	 * @param original
	 */
	public Cylinder(Cylinder original)
	{
		super(original, CopyModeType.CLONE_DATA);
		
		setStartPoint(original.getStartPoint().clone());
		setEndPoint(original.getEndPoint().clone());
		setRadius(original.getRadius());
		setSurfaceProperty(original.getSurfaceProperty().clone());
	}
	
	/* (non-Javadoc)
	 * @see optics.raytrace.sceneObjects.solidGeometry.SceneObjectIntersection#clone()
	 */
	@Override
	public Cylinder clone()
	{
		return new Cylinder(this);
	}
	
	public void addSceneObjects()
	{
		Vector3D startToEnd = Vector3D.difference(getEndPoint(), getStartPoint());
		
		addSceneObject(new CylinderMantle("mantle", getStartPoint(), getEndPoint(), getRadius(), getSurfaceProperty(), getParent(), getStudio()));
		addSceneObject(new Disc("start cap", getStartPoint(), startToEnd.getReverse(), getRadius(), getSurfaceProperty(), getParent(), getStudio()));
		addSceneObject(new Disc("end cap", getEndPoint(), startToEnd, getRadius(), getSurfaceProperty(), getParent(), getStudio()));
	}

	public Vector3D getStartPoint() {
		return startPoint;
	}

	public void setStartPoint(Vector3D startPoint) {
		this.startPoint = startPoint;
	}

	public Vector3D getEndPoint() {
		return endPoint;
	}

	public void setEndPoint(Vector3D endPoint) {
		this.endPoint = endPoint;
	}

	public double getRadius() {
		return radius;
	}

	public void setRadius(double radius) {
		this.radius = radius;
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
		return "Cuboid";
	}
}
