package optics.raytrace.sceneObjects;

import java.io.*;

import math.*;
import optics.DoubleColour;
import optics.raytrace.core.*;
import optics.raytrace.exceptions.RayTraceException;
import optics.raytrace.surfaces.*;
import optics.raytrace.sceneObjects.solidGeometry.*;

/**
 * @author johannes
 * A Dove prism.
 * Based on Cuboid.java
 * TODO there seems to be some problem with tracing light rays through this
 */
public class DovePrism extends SceneObjectIntersectionSimple implements Serializable
{
	private static final long serialVersionUID = 6922560367142786046L;

	private double width, height, length;

	SurfaceProperty sp;

	/**
	 * Create a Dove prism centred on the origin.
	 * The Dove prism can be rotated and translated later using the transform method.
	 * 
	 * @param description
	 * @param width	size in the x dimension
	 * @param height	size in the y dimension
	 * @param length	size in the z dimension
	 * @param sp	surface property
	 * 
	 * @see optics.raytrace.core.SceneObject#transform(optics.raytrace.sceneObjects.transformations.Transformation)
	 */
	public DovePrism(String description, double width, double height, double length, SurfaceProperty sp, SceneObject parent, Studio studio)
	{
		super(description, parent, studio);

		this.width = width;
		this.height = height;   
		this.length = length;

		this.sp = sp;

		setup();
	}

	public DovePrism(String description, SceneObject parent, Studio studio)
	{
		super(description, parent, studio);

		this.width = 1.0;
		this.height = 1.0;
		this.length = 1.0;

		this.sp = new SurfaceColour(DoubleColour.LIGHT_BLUE, DoubleColour.BLACK, true);
		
		setup();
	}
	
	/**
	 * Create a clone of the original.
	 * @param original
	 */
	public DovePrism(DovePrism original)
	{
		super(original);
		
		width = original.width;
		height = original.height;
		length = original.length;
		sp = original.sp.clone();
	}
	
	/* (non-Javadoc)
	 * @see optics.raytrace.sceneObjects.solidGeometry.SceneObjectIntersection#clone()
	 */
	@Override
	public DovePrism clone()
	{
		return new DovePrism(this);
	}

	/**
	 * @return the width
	 */
	 public double getWidth() {
		return width;
	}

	/**
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
	  * @param height the height to set
	  */
	 public void setHeight(double height) {
		 this.height = height;
	 }

	 /**
	  * @return the length
	  */
	 public double getLength() {
		 return length;
	 }

	 /**
	  * @param length the length to set
	  */
	 public void setLength(double length) {
		 this.length = length;
	 }

	 private void setup() {
		 clear();

		 addSceneObject(new ParametrisedPlane("left side",	new Vector3D(-width/2, 0, 0), new Vector3D(-1, 0, 0), sp, getParent(), getStudio()));
		 addSceneObject(new ParametrisedPlane("right side",	new Vector3D(+width/2, 0, 0), new Vector3D(1, 0, 0), sp, getParent(), getStudio()));
		 addSceneObject(new ParametrisedPlane("bottom side",	new Vector3D(0, -height/2, 0), new Vector3D(0, -1, 0), sp, getParent(), getStudio()));
		 addSceneObject(new ParametrisedPlane("top side",	new Vector3D(1, +height/2, 0), new Vector3D(0, 1, 0), sp, getParent(), getStudio()));
		 addSceneObject(new ParametrisedPlane("front side",	new Vector3D(0, 0, -length/2), new Vector3D(0, 1/Math.sqrt(2.), -1/Math.sqrt(2.)), sp, getParent(), getStudio()));
		 addSceneObject(new ParametrisedPlane("back side",	new Vector3D(0, 0, +length/2), new Vector3D(0, 1/Math.sqrt(2.), 1/Math.sqrt(2.)), sp, getParent(), getStudio()));
	 }

	 @Override
	public String toString() {
		 return "<Dove prism>\n" +
		 "\t<width double="+width+">\n" + 
		 "\t<height double="+height+">\n" + 
		 "\t<length double="+length+">\n" + 
		 "</Dove prism>";
	 }

	 @Override
	 public DoubleColour getColour(Ray ray, LightSource l, SceneObject scene, int traceLevel, RaytraceExceptionHandler raytraceExceptionHandler)
	 throws RayTraceException
	 {
		 // System.out.println("DovePrism!");
		 
		 return super.getColour(ray, l, scene, traceLevel, raytraceExceptionHandler);
	 }

	@Override
	public String getType()
	{
		return "Dove prism";
	}
}
