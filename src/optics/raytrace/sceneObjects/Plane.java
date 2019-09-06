package optics.raytrace.sceneObjects;

import java.io.*;

import math.*;
import optics.raytrace.core.*;

public class Plane extends SceneObjectPrimitive implements Serializable
{
	private static final long serialVersionUID = -1934860013166564580L;

	private Vector3D pointOnPlane, normal;
			

	/**
	 * @param description
	 * @param pointOnPlane
	 * @param normal	normalised surface normal, pointing in direction of outside
	 * @param surfaceProperty	surface properties
	 */
	public Plane(
			String description,
			Vector3D pointOnPlane,
			Vector3D normal, 
			SurfaceProperty surfaceProperty,
			SceneObject parent,
			Studio studio
		)
	{
		super(description, surfaceProperty, parent, studio);
		setPointOnPlane(pointOnPlane);
		setNormal(normal);
	}

//	public Plane(String description, SceneObject parent, Studio studio) {
//		super(description, new SurfaceColour(DoubleColour.GREEN, DoubleColour.BLACK), parent, studio);
//		setPointOnPlane(new Vector3D(0,1,0));
//		setNormal(new Vector3D(0,1,0));
//	}
	
	/**
	 * Create a clone of the original.
	 * @param original
	 */
	public Plane(Plane original)
	{
		super(
				original.description,
				original.getSurfaceProperty().clone(),
				original.getParent(),
				original.getStudio()
			);
		setPointOnPlane(original.getPointOnPlane().clone());
		setNormal(original.getNormal().clone());
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.sceneObjects.SceneObject#clone()
	 */
	@Override
	public Plane clone()
	{
		return new Plane(this);
	}

	/**
	 * The plane x = x0.
	 * @param description
	 * @param x0	the x coordinate of the plane
	 * @param sp	surface property
	 * @return The plane x=x0
	 */
	public static Plane xPlane(String description, double x0, SurfaceProperty sp, SceneObject parent, Studio studio)
	{
		return new Plane(
				description,
				new Vector3D(x0, 0, 0),	// point on plane
				new Vector3D(1, 0, 0),	// normal to plane
				sp,
				parent,
				studio
		);
	}

//	/**
//	 * The plane x = x0, with standard description and surface properties.
//	 * @param x0
//	 * @return
//	 */
//	public static Plane xPlane(double x0, SurfaceProperty surfaceProperty, SceneObject parent, Studio studio)
//	{
//		return xPlane("Plane x="+x0, x0, surfaceProperty, parent, studio);
//	}

	/**
	 * The plane y = y0.
	 * @param description
	 * @param y0	the y coordinate of the plane
	 * @param surfaceProperty	surface property
	 * @return The plane y=y0
	 */
	public static Plane yPlane(String description, double y0, SurfaceProperty surfaceProperty, SceneObject parent, Studio studio)
	{
		return new Plane(
				description,
				new Vector3D(0, y0, 0),	// point on plane
				new Vector3D(0, 1, 0),	// normal to plane
				surfaceProperty,
				parent,
				studio
		);
	}

//	/**
//	 * The plane y = y0, with standard description and surface properties.
//	 * @param y0
//	 * @return
//	 */
//	public static Plane yPlane(double y0, SceneObject parent, Studio studio)
//	{
//		return yPlane("Plane y="+y0, y0, SurfaceColour.RED_MATT, parent, studio);
//	}

	/**
	 * The plane z = z0.
	 * @param description
	 * @param z0	the z coordinate of the plane
	 * @param surfaceProperty	surface property
	 * @return The plane z=z0
	 */
	public static Plane zPlane(String description, double z0, SurfaceProperty surfaceProperty, SceneObject parent, Studio studio)
	{
		return new Plane(
				description,
				new Vector3D(0, 0, z0),	// point on plane
				new Vector3D(0, 0, 1),	// normal to plane
				surfaceProperty,
				parent,
				studio
		);
	}
	
//	/**
//	 * The plane z = z0, with standard description and surface properties.
//	 * @param z0
//	 * @return
//	 */
//	public static Plane zPlane(double z0, SceneObject parent, Studio studio)
//	{
//		return zPlane("Plane z="+z0, z0, SurfaceColour.RED_MATT, parent, studio);
//	}
	
	/**
	 * @param ray
	 * @param pointOnPlane
	 * @param normalToPlane
	 * @return the ray at the closest ray intersection, null if there is none
	 */
	public static Ray getRayAtClosestRayIntersection(Ray ray, Vector3D pointOnPlane, Vector3D normalToPlane)
	{
		double numerator = Vector3D.scalarProduct(Vector3D.difference(pointOnPlane, ray.getP()), normalToPlane);
		double denominator = Vector3D.scalarProduct(ray.getD(), normalToPlane);

		if (denominator == 0.0) return null;

		// How far from the ray's starting point is the intersection point?
		double lambda = numerator / denominator; 

		// Returns null if there is no intersection
		if (lambda < 0.0 ) return null;

		return ray.getAdvancedRay(lambda);
	}

	@Override
	public RaySceneObjectIntersection getClosestRayIntersection(Ray ray)
	{
		Ray rayAtIntersectionPoint = getRayAtClosestRayIntersection(ray, pointOnPlane, normal);
		if(rayAtIntersectionPoint == null) return RaySceneObjectIntersection.NO_INTERSECTION;
		return new RaySceneObjectIntersection(rayAtIntersectionPoint.getP(), this, rayAtIntersectionPoint.getT());
	}

//	@Override
//	public RaySceneObjectIntersection getClosestRayIntersectionAvoidingOrigin(Ray ray, SceneObject excludeObject) {
//		return getClosestRayIntersection(ray);
//	}

	@Override
	public Vector3D getNormalisedOutwardsSurfaceNormal(Vector3D p)
	{
		return normal;
	}

	@Override
	public String toString()
	{
		return "<Plane, description = "+ description + ", point = " + pointOnPlane + ", normal = " + normal + ">";
	}

	@Override
	public boolean insideObject(Vector3D p) {
		return (p.getDifferenceWith(pointOnPlane).getScalarProductWith(normal) <= 0);
	}

	@Override
	public Plane transform(Transformation t) {
		return new Plane(description, t.transformPosition(pointOnPlane), t.transformDirection(normal), getSurfaceProperty(), getParent(), getStudio());
	}

	public Vector3D getPointOnPlane() {
		return pointOnPlane;
	}

	public void setPointOnPlane(Vector3D pointOnPlane) {
		this.pointOnPlane = pointOnPlane;
	}

	public Vector3D getNormal()
	{
		return normal;
	}

	public void setNormal(Vector3D normal)
	{
		this.normal = normal.getNormalised();
	}
	
	@Override
	public String getType()
	{
		return "Plane";
	}
}

