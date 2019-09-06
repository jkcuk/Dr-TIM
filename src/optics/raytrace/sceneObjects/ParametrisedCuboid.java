package optics.raytrace.sceneObjects;

import java.io.*;

import math.*;
import optics.DoubleColour;
import optics.raytrace.core.*;
import optics.raytrace.surfaces.*;
import optics.raytrace.sceneObjects.solidGeometry.*;

public class ParametrisedCuboid extends SceneObjectIntersection implements Serializable
{
	private static final long serialVersionUID = -3315907591510079916L;

	private double width, height, depth;
	protected Vector3D centre;

	protected SurfaceProperty surfaceProperty;

	/**
	 * Create a cuboid.
	 * The cuboid can be rotated later using the transform method.
	 * 
	 * @param description
	 * @param width	size in the x dimension
	 * @param height	size in the y dimension
	 * @param depth	size in the z dimension
	 * @param centre	centre
	 * @param surfaceProperty	surface property
	 * 
	 * @see optics.raytrace.core.SceneObject#transform(optics.raytrace.sceneObjects.transformations.Transformation)
	 */
	public ParametrisedCuboid(String description, double width, double height, double depth, Vector3D centre, SurfaceProperty surfaceProperty, SceneObject parent, Studio studio)
	{
		super(description, parent, studio);

		this.width = width;
		this.height = height;   
		this.depth = depth;

		this.centre = centre;

		this.surfaceProperty = surfaceProperty;

		// create all the scene objects that make up this cuboid
		setup();
	}

	public ParametrisedCuboid(String description, SceneObject parent, Studio studio)
	{
		super(description, parent, studio);

		this.width = 1.0;
		this.height = 1.0;
		this.depth = 1.0;

		this.centre = new Vector3D(0,0,10);
		this.surfaceProperty = new SurfaceColour(DoubleColour.LIGHT_BLUE, DoubleColour.BLACK, true);
		setup();
	}
	
	/**
	 * Create a clone of the original.
	 * @param original
	 */
	public ParametrisedCuboid(ParametrisedCuboid original)
	{
		super(original);
		
		width = original.width;
		height = original.height;
		depth = original.depth;
		centre = original.centre.clone();
		surfaceProperty = original.surfaceProperty.clone();
	}
	
	/* (non-Javadoc)
	 * @see optics.raytrace.sceneObjects.solidGeometry.SceneObjectIntersection#clone()
	 */
	@Override
	public ParametrisedCuboid clone()
	{
		return new ParametrisedCuboid(this);
	}

	/**
	 * make sure you call setup() when all parameters are set
	 * @param centre
	 */
	public void setCentre(Vector3D centre) {
		this.centre = centre;
	}

	public Vector3D getCentre() {
		return centre;
	}        

	/**
	 * @return the width
	 */
	 public double getWidth() {
		return width;
	}

	/**
	 * make sure you call setup() when all parameters are set
	 * @param width the width to set
	 */
	 public void setWidth(double width) {
		this.width = width;
	}

	/**
	 * @return the height
	 */
	 public double getHeight() {
		 return height;
	 }

	 /**
	  * make sure you call setup() when all parameters are set
	  * @param height the height to set
	  */
	 public void setHeight(double height) {
		 this.height = height;
	 }

	 /**
	  * @return the depth
	  */
	 public double getDepth() {
		 return depth;
	 }

	 /**
	  * make sure you call setup() when all parameters are set
	  * @param depth the depth to set
	  */
	 public void setDepth(double depth) {
		 this.depth = depth;
	 }

	 public SurfaceProperty getSurfaceProperty() {
		return surfaceProperty;
	}

	/**
	 * make sure you call setup() when all parameters are set
	 * @param surfaceProperty
	 */
	public void setSurfaceProperty(SurfaceProperty surfaceProperty) {
		this.surfaceProperty = surfaceProperty;
	}

	public void setup() {
		 clear();

		 addSceneObject(new ParametrisedPlane("left side", Vector3D.sum(centre, new Vector3D(-width/2, 0, 0)), new Vector3D(-1, 0, 0), surfaceProperty, getParent(), getStudio()));
		 addSceneObject(new ParametrisedPlane("right side", Vector3D.sum(centre, new Vector3D(+width/2, 0, 0)), new Vector3D(1, 0, 0), surfaceProperty, getParent(), getStudio()));
		 addSceneObject(new ParametrisedPlane("bottom side", Vector3D.sum(centre, new Vector3D(0, -height/2, 0)), new Vector3D(0, -1, 0), surfaceProperty, getParent(), getStudio()));
		 addSceneObject(new ParametrisedPlane("top side", Vector3D.sum(centre, new Vector3D(1, +height/2, 0)), new Vector3D(0, 1, 0), surfaceProperty, getParent(), getStudio()));
		 addSceneObject(new ParametrisedPlane("front side", Vector3D.sum(centre, new Vector3D(0, 0, -depth/2)), new Vector3D(0, 0, -1), surfaceProperty, getParent(), getStudio()));
		 addSceneObject(new ParametrisedPlane("back side", Vector3D.sum(centre, new Vector3D(0, 0, +depth/2)), new Vector3D(0, 0, 1), surfaceProperty, getParent(), getStudio()));
	 }

	 @Override
	public String toString() {
		 return "<Cuboid>\n" + 
		 "\t<centre Vector3D="+centre+">\n" + 
		 "\t<width double="+width+">\n" + 
		 "\t<height double="+height+">\n" + 
		 "\t<depth double="+depth+">\n" + 
		 "</Cuboid>";
	 }

		@Override
		public String getType()
		{
			return "Cuboid";
		}
}
