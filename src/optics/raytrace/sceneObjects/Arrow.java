package optics.raytrace.sceneObjects;

import java.io.*; 

import math.*;
import optics.raytrace.GUI.sceneObjects.EditableParametrisedCone;
import optics.raytrace.GUI.sceneObjects.EditableParametrisedCylinder;
import optics.raytrace.core.*;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.utility.CopyModeType;

/**
 * An arrow, i.e. a cylinder with a cone.
 * Made from the scene objects Cylinder and Cone (the tip).
 * 
 * @see Cylinder
 * @see ParametrisedCone
 * 
 * @author Johannes
 *
 */
public class Arrow extends SceneObjectContainer implements Serializable
{
	private static final long serialVersionUID = 2856134030716159992L;

	private Vector3D startPoint, endPoint;
	private double shaftRadius, tipLength, tipAngle;
	private SurfaceProperty surfaceProperty;

	public Arrow(
			String description,
			Vector3D startPoint,
			Vector3D endPoint,
			double shaftRadius,
			double tipLength,
			double tipAngle,
			SurfaceProperty surfaceProperty,
			SceneObject parent, 
			Studio studio
		)
	{
		super(description, parent, studio);
		
		// take a note of all the parameters
		setStartPoint(startPoint);
		setEndPoint(endPoint);
		setShaftRadius(shaftRadius);
		setTipLength(tipLength);
		setTipAngle(tipAngle);
		setSurfaceProperty(surfaceProperty);
		
		// add the scene objects
		addSceneObjects();
	}

	/**
	 * Create a clone of the original.
	 * @param original
	 */
	public Arrow(Arrow original)
	{
		super(original, CopyModeType.CLONE_DATA);
		
		setStartPoint(original.getStartPoint().clone());
		setEndPoint(original.getEndPoint().clone());
		setShaftRadius(original.getShaftRadius());
		setTipLength(original.getTipLength());
		setTipAngle(original.getTipAngle());
		setSurfaceProperty(original.getSurfaceProperty().clone());
		
		addSceneObjects();
	}
	
	/* (non-Javadoc)
	 * @see optics.raytrace.sceneObjects.solidGeometry.SceneObjectIntersection#clone()
	 */
	@Override
	public Arrow clone()
	{
		return new Arrow(this);
	}
	
	protected void addSceneObjects()
	{
		Vector3D
			arrowStartToEnd = Vector3D.difference(getEndPoint(), getStartPoint()),
			shaftEnd = Vector3D.difference(getEndPoint(), arrowStartToEnd.getWithLength(getTipLength()));
		
		// the shaft
		addSceneObject(new EditableParametrisedCylinder(
				"shaft",
				getStartPoint(), 
				shaftEnd,
				getShaftRadius(),
				getSurfaceProperty(),
				this,
				getStudio()
			));
		
		// the tip
		addSceneObject(new EditableParametrisedCone(
				"tip",
				getEndPoint(),
				arrowStartToEnd.getProductWith(-1),
				false,	// cone is not open
				getTipAngle(),
				getTipLength(),
				getSurfaceProperty(),
				this,
				getStudio()
			));
	}

	// getters & setters
	
	public Vector3D getStartPoint()
	{
		return startPoint;
	}

	public void setStartPoint(Vector3D startPoint)
	{
		this.startPoint = startPoint;
	}

	public Vector3D getEndPoint()
	{
		return endPoint;
	}

	public void setEndPoint(Vector3D endPoint)
	{
		this.endPoint = endPoint;
	}

	public double getShaftRadius()
	{
		return shaftRadius;
	}

	public void setShaftRadius(double shaftRadius)
	{
		this.shaftRadius = shaftRadius;
	}

	public double getTipLength()
	{
		return tipLength;
	}

	public void setTipLength(double tipLength)
	{
		this.tipLength = tipLength;
	}

	public double getTipAngle()
	{
		return tipAngle;
	}

	public void setTipAngle(double tipAngle)
	{
		this.tipAngle = tipAngle;
	}

	public SurfaceProperty getSurfaceProperty()
	{
		return surfaceProperty;
	}

	public void setSurfaceProperty(SurfaceProperty surfaceProperty)
	{
		this.surfaceProperty = surfaceProperty;
	}
	
	
	/**
	 * @param t
	 * @return
	 */
	@Override
	public Arrow transform(Transformation t)
	{
		return new Arrow(
				description,
				t.transformPosition(startPoint),
				t.transformPosition(endPoint),
				shaftRadius,
				tipLength,
				tipAngle,
				surfaceProperty,
				getParent(), 
				getStudio()
			);
	}

	
	@Override
	public String getType()
	{
		return "Arrow";
	}
}
