package optics.raytrace.sceneObjects;

import java.io.*;

import math.*;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.Studio;
import optics.raytrace.core.SurfaceProperty;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.utility.CopyModeType;

// Creates a wedge, infinite in Y, and restricted to 90 degrees between x and z axis.

public class Wedge extends SceneObjectContainer implements Serializable
{
	private static final long serialVersionUID = -2730413998525596783L;

	private Vector3D c,u,w;
	private SurfaceProperty surfaceProperty;
	
	private final Vector3D yHeight = new Vector3D(0,100,0);

	//constructor
	public Wedge(
			String description,Vector3D c,Vector3D u, Vector3D v,
			SurfaceProperty surfaceProperty,
			SceneObject parent,
			Studio studio)
	{
		super(description, parent, studio);
		this.c=c;
		this.u=u;
		this.w=v;
		//takes 3 rectangles and adds them to the container sceneObjects
		setup();
		this.surfaceProperty = surfaceProperty;
	}

	public Wedge(String description, SceneObject parent, Studio studio) {
		super(description, parent, studio);
		this.c = new Vector3D(-0.5, 0.0, 9.5);
		this.u = new Vector3D( 1.0, 0.0, 0.0);
		this.w = new Vector3D( 0.0, 0.0, 1.0);
		setup();
		this.surfaceProperty = null;
	}
	
	/**
	 * Create a clone of the original
	 * @param original
	 */
	public Wedge(Wedge original)
	{
		super(original, CopyModeType.CLONE_DATA);
		c = original.getC().clone();
		u = original.getU().clone();
		w = original.getW().clone();
		surfaceProperty = original.getSurfaceProperty().clone();
	}
	
	/* (non-Javadoc)
	 * @see optics.raytrace.sceneObjects.SceneObjectContainer#clone()
	 */
	@Override
	public Wedge clone()
	{
		return new Wedge(this);
	}

	public Vector3D getC() {
		return c;
	}

	public void setC(Vector3D c) {
		this.c = c;
	}

	public Vector3D getU() {
		return u;
	}

	public void setU(Vector3D u) {
		this.u = u;
	}

	public Vector3D getW() {
		return w;
	}

	public void setW(Vector3D w) {
		this.w = w;
	}

	public SurfaceProperty getSurfaceProperty() {
		return surfaceProperty;
	}

	public void setSurfaceProperty(SurfaceProperty surfaceProperty) {
		this.surfaceProperty = surfaceProperty;
	}

	private void setup()
	{
		addSceneObject(new CentredParallelogram(description, c, u, w, getSurfaceProperty(), getParent(), getStudio()));
		addSceneObject(new CentredParallelogram(description, Vector3D.sum(c, Vector3D.scalarTimesVector3D(1/2, u)), u, yHeight, getSurfaceProperty(), getParent(), getStudio())); // Adds parallelogram with centre at u+c, 100 in y direction, and lying along u.
		addSceneObject(new CentredParallelogram(description, Vector3D.sum(c, Vector3D.scalarTimesVector3D(1/2, w)), yHeight, w, getSurfaceProperty(), getParent(), getStudio()));

		// Create centre Vector3D for sloping parallelogram.

		Vector3D slopeVector3D = u.getDifferenceWith(w);
		Vector3D slopeCentre = Vector3D.scalarTimesVector3D(1/2,slopeVector3D).getSumWith(c);

		addSceneObject(new CentredParallelogram(description, slopeCentre, slopeVector3D, yHeight, getSurfaceProperty(), getParent(), getStudio()));
	}

	@Override
	public String toString(){						//outputs wedge's description
		return "<Parallelepiped>\n" + 
		"\t<centre Vector3D="+c+">\n" + 
		"\t<centre u="+u+">\n" + 
		"\t<centre w="+w+">\n" + 
		"</Parallelepiped>\n";
	}
	
	@Override
	public String getType()
	{
		return "Wedge";
	}
}