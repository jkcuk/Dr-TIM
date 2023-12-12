package optics.raytrace.core;

import java.io.Serializable;

import math.*;

/**
 * Intersection between a ray and a scene object.
 * This has a position, a SceneObjectPrimitive that the ray intersects with, and the time of the intersection.
 * @author johannes
 *
 */
public class RaySceneObjectIntersection implements Serializable
{
	private static final long serialVersionUID = -3196938345012999677L;
	
	public Vector3D p;		//this is the position of the intersection
	public SceneObjectPrimitive o;		//this is the primitive object that intersects with ray
	public double t;	// the time of the intersection (for relativistic raytracing)
	
	// variables that may sometimes be set
	private Vector3D normalisedOutwardsNormal;	// optional: this is the normalsed outwards-facing normal to the intersected surface at the intersection point
	private Ray ray;	
	
	public static final RaySceneObjectIntersection
		NO_INTERSECTION = new RaySceneObjectIntersection((Vector3D)null, (SceneObjectPrimitive)null, -1);
	
	//constructor
	public RaySceneObjectIntersection(Vector3D p, SceneObjectPrimitive o, double t, Ray ray, Vector3D outwardsNormal)
	{
		this.p=p;
		this.o=o;
		this.t = t;
		this.ray = ray;
		setNormalisedOutwardsNormal(outwardsNormal);
	}

	public RaySceneObjectIntersection(Vector3D p, SceneObjectPrimitive o, double t)
	{
		this(p, o, t, null, null);
	}
			
	// getters & setters
	
	public Vector3D getStoredNormalisedOutwardsNormal() {
		return normalisedOutwardsNormal;
	}

	public void setNormalisedOutwardsNormal(Vector3D outwardsNormal) {
		if(outwardsNormal != null) this.normalisedOutwardsNormal = outwardsNormal.getNormalised();
		else this.normalisedOutwardsNormal = outwardsNormal;
	}

	
	/**
	 * @param ray
	 * @return	INWARDS or OUTWARDS
	 */
	public Orientation getRayOrientation(Ray ray)
	{
		return Orientation.getOrientation(ray, this);
	}
	
	public Orientation getOrientation()
	{
		return Orientation.getOrientation(ray, this);
	}
	
	public Vector3D getNormalisedOutwardsSurfaceNormal()
	{
		if(normalisedOutwardsNormal != null)
		{
			// the outwards-facing surface normal is stored in the intersection, so just return it
			return normalisedOutwardsNormal;
		}
		else
		{
			// we need to calculate the outwards-facing surface normal
			return o.getNormalisedOutwardsSurfaceNormal(p);
		}
	}

	@Override
	public String toString()
	{
		return "<RaySceneObjectIntersection>\n"+
		"<position>"+p+"</position>\n"+
		"<object>"+o+"</object>\n"+
		"<time>"+t+"</time>\n"+
		"</RaySceneObjectIntersection>";
	}
	
	public String toOneLiner()
	{
		return "p="+p+", o="+o.description+", t="+t; 
	}

}
