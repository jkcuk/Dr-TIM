package optics.raytrace.sceneObjects;

import java.io.*;

import math.*;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.Studio;
import optics.raytrace.core.SurfaceProperty;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.utility.CopyModeType;

/**
 * A parallelepiped made up of 6 parallelograms
 * 
 * @author Johannes Courtial
 *
 */
public class ParallelepipedFromParallelograms extends SceneObjectContainer implements Serializable
{
	private static final long serialVersionUID = -5329775204995476447L;

	private Vector3D centre, u, v, w;
	private SurfaceProperty surfaceProperty;

	//constructor
	public ParallelepipedFromParallelograms(String description,Vector3D centre,Vector3D u, Vector3D v, Vector3D w,SurfaceProperty surfaceProperty, SceneObject parent, Studio studio)
	{
		super(description, parent, studio);
		this.centre=centre;
		this.u=u;
		this.v=v;
		this.w=w;
		//takes 6 rectangles and adds them to the container sceneObjects
		this.surfaceProperty = surfaceProperty;
		setup();
	}

	/**
	 * Creates a clone of the original.
	 * @param original
	 */
	public ParallelepipedFromParallelograms(ParallelepipedFromParallelograms original)
	{
		super(original, CopyModeType.CLONE_DATA);
		
		centre = original.getCentre().clone();
		u = original.getU().clone();
		v = original.getV().clone();
		w = original.getW().clone();
		surfaceProperty = original.getSurfaceProperty().clone();
	}
	
	/* (non-Javadoc)
	 * @see optics.raytrace.sceneObjects.SceneObjectContainer#clone()
	 */
	@Override
	public ParallelepipedFromParallelograms clone()
	{
		return new ParallelepipedFromParallelograms(this);
	}

	private void setup()
	{
		addSceneObject(new CentredParallelogram("side 1", Vector3D.sum(centre, u.getProductWith(-0.5)), v, w, getSurfaceProperty(), getParent(), getStudio()));
		addSceneObject(new CentredParallelogram("side 2", Vector3D.sum(centre, u.getProductWith( 0.5)), v, w, getSurfaceProperty(), getParent(), getStudio()));
		addSceneObject(new CentredParallelogram("side 3", Vector3D.sum(centre, v.getProductWith(-0.5)), u, w, getSurfaceProperty(), getParent(), getStudio()));
		addSceneObject(new CentredParallelogram("side 4", Vector3D.sum(centre, v.getProductWith( 0.5)), u, w, getSurfaceProperty(), getParent(), getStudio()));
		addSceneObject(new CentredParallelogram("side 5", Vector3D.sum(centre, w.getProductWith(-0.5)), u, v, getSurfaceProperty(), getParent(), getStudio()));
		addSceneObject(new CentredParallelogram("side 6", Vector3D.sum(centre, w.getProductWith( 0.5)), u, v, getSurfaceProperty(), getParent(), getStudio()));
	}

	public Vector3D getCentre() {
		return centre;
	}

	public void setCentre(Vector3D centre) {
		this.centre = centre;
	}

	public Vector3D getU() {
		return u;
	}

	public void setU(Vector3D u) {
		this.u = u;
	}

	public Vector3D getV() {
		return v;
	}

	public void setV(Vector3D v) {
		this.v = v;
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

	@Override
	public String toString(){						//outputs cuboid's description
		return "Parallelepiped";
	}
	
	@Override
	public String getType()
	{
		return "Parallelepiped";
	}

}
