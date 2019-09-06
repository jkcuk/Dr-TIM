package optics.raytrace.sceneObjects;

import java.io.*;

import math.*;
import optics.DoubleColour;
import optics.raytrace.core.*;
import optics.raytrace.surfaces.*;

public class Disc extends Plane implements Serializable
{
	private static final long serialVersionUID = -3970953742260445841L;

	private double radius;
	
	/**
	 * This is there so that the interactive version can have a disc representing the eye pupil
	 * and whose radius can be adjusted when the eye's pupil size is being adjusted.
	 */
	private boolean noClone = false;

	/**
	 * @param description
	 * @param centre
	 * @param normal surface normal, pointing in direction of outside
	 * @param radius
	 * @param surface
	 */
	public Disc(String description, Vector3D centre, Vector3D normal, double radius, SurfaceProperty surface, SceneObject parent, Studio studio)
	{
		super(description, centre, normal, surface, parent, studio);
		//this.centre = centre;
		this.radius = radius;
	}

	public Disc(String description, SceneObject parent, Studio studio)
	{
		super(description, new Vector3D(0,1,10), new Vector3D(0,1,0), new SurfaceColour(DoubleColour.LIGHT_BLUE, DoubleColour.BLACK, true), parent, studio);
		this.radius = 1.0;
	}
	
	public Disc(Disc original)
	{
		super(original);
		radius = original.getRadius();
		if(noClone) System.out.println("Warning: cloning disc despite noClone flag being switched on!");
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.sceneObjects.Plane#clone()
	 */
	@Override
	public Disc clone()
	{
		if(noClone) return this;
		else return new Disc(this);
	}
	
	public boolean isNoClone() {
		return noClone;
	}

	public void setNoClone(boolean noClone) {
		this.noClone = noClone;
	}

	public double getRadius() {
		return radius;
	}

	public void setRadius(double radius) {
		this.radius = radius;
	}

	public Vector3D getCentre() {
		//return centre;
		return getPointOnPlane();
	}

	public void setCentre(Vector3D centre) {
		//this.centre = centre;
		setPointOnPlane(centre);
	}

	@Override
	public RaySceneObjectIntersection getClosestRayIntersection(Ray ray)
	{
		RaySceneObjectIntersection i = super.getClosestRayIntersection(ray);
		if(i == RaySceneObjectIntersection.NO_INTERSECTION) return i;
		if(i.p.getDifferenceWith(getPointOnPlane()).getModSquared() > radius*radius) return RaySceneObjectIntersection.NO_INTERSECTION;
		return i;
	}

	/**
	 * Returns false as this is an object without volume.
	 * 
	 * @see optics.raytrace.core.SceneObject#insideObject(math.Vector3D)
	 */
	@Override
	public boolean insideObject(Vector3D p)
	{
		return false;
	}

	@Override
	public Disc transform(Transformation t) {
		return new Disc(description, t.transformPosition(getCentre()), t.transformDirection(getNormal()), radius, getSurfaceProperty(), getParent(), getStudio());
	}

	@Override
	public String toString() {						//outputs disc's info along with a description
		return description + " [Disc]";
	}
	
	@Override
	public String getType()
	{
		return "Disc";
	}
}
