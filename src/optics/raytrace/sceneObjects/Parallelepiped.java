package optics.raytrace.sceneObjects;

import java.io.*;

import math.*;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.Studio;
import optics.raytrace.core.SurfaceProperty;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectIntersectionSimple;

/**
 * A parallelepiped defined as the intersection of the "inside" of 6 planes
 * 
 * @author Johannes Courtial
 *
 */
public class Parallelepiped extends SceneObjectIntersectionSimple implements Serializable
{
	private static final long serialVersionUID = 437637501928513867L;

	private Vector3D centre, u, v, w;
	private SurfaceProperty surfaceProperty;

	/**
	 * @param description
	 * @param centre
	 * @param u
	 * @param v
	 * @param w
	 * @param surfaceProperty
	 * @param parent
	 * @param studio
	 */
	public Parallelepiped(
			String description,
			Vector3D centre,
			Vector3D u,
			Vector3D v,
			Vector3D w,
			SurfaceProperty surfaceProperty,
			SceneObject parent,
			Studio studio
		)
	{
		super(description, parent, studio);
		
		setCentre(centre);
		setUVW(u, v, w);
		setSurfaceProperty(surfaceProperty);

		setup();
	}
	
	/**
	 * Creates a clone of the original.
	 * @param original
	 */
	public Parallelepiped(Parallelepiped original)
	{
		super(original);
		
		setCentre(original.getCentre().clone());
		setUVW(	original.getU().clone(),
				original.getV().clone(),
				original.getW().clone()
			);
		setSurfaceProperty(original.getSurfaceProperty().clone());
	}
	
	/* (non-Javadoc)
	 * @see optics.raytrace.sceneObjects.SceneObjectContainer#clone()
	 */
	@Override
	public Parallelepiped clone()
	{
		return new Parallelepiped(this);
	}

	private void setup()
	{	
		addSceneObject(new ParametrisedPlane("side 1", Vector3D.sum(centre, u.getProductWith( 0.5)), v, w, getSurfaceProperty(), getParent(), getStudio()));
		addSceneObject(new ParametrisedPlane("side 2", Vector3D.sum(centre, u.getProductWith(-0.5)), w, v, getSurfaceProperty(), getParent(), getStudio()));
		addSceneObject(new ParametrisedPlane("side 3", Vector3D.sum(centre, v.getProductWith( 0.5)), w, u, getSurfaceProperty(), getParent(), getStudio()));
		addSceneObject(new ParametrisedPlane("side 4", Vector3D.sum(centre, v.getProductWith(-0.5)), u, w, getSurfaceProperty(), getParent(), getStudio()));
		addSceneObject(new ParametrisedPlane("side 5", Vector3D.sum(centre, w.getProductWith( 0.5)), u, v, getSurfaceProperty(), getParent(), getStudio()));
		addSceneObject(new ParametrisedPlane("side 6", Vector3D.sum(centre, w.getProductWith(-0.5)), v, u, getSurfaceProperty(), getParent(), getStudio()));
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

	public Vector3D getV() {
		return v;
	}

	public Vector3D getW() {
		return w;
	}

	public void setUVW(Vector3D u, Vector3D v, Vector3D w)
	{
		this.u = u;
		this.v = v;
		if(Vector3D.scalarProduct(Vector3D.crossProduct(u, v), w) >= 0.)
		{
			// w points in a similar direction to u x v
			this.w = w;
		}
		else
		{
			// w points in the opposite diretion to u x v
			this.w = w.getProductWith(-1.);
		}
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
