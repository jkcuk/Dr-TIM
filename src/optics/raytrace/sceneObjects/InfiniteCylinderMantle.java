package optics.raytrace.sceneObjects;

import math.Vector3D;
import optics.raytrace.core.Ray;
import optics.raytrace.core.RaySceneObjectIntersection;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.SceneObjectPrimitive;
import optics.raytrace.core.Studio;
import optics.raytrace.core.SurfaceProperty;
import optics.raytrace.core.Transformation;

/**
 * An infinitely long cylinder (without end caps, which would be at infinity anyway).
 * Based on CylinderMantle class.
 * 
 * @see CylinderMantle
 * @see Cylinder
 * @see Disc
 */
public class InfiniteCylinderMantle extends SceneObjectPrimitive
{
	private static final long serialVersionUID = 6457815561654353896L;

	private Vector3D pointOnAxis, normalisedAxisDirection;
	private double radius;

	/**
	 * creates an infinitely long cylinder mantle
	 * 
	 * @param description
	 * @param pointOnAxis	one point on the cylinder axis
	 * @param axisDirection	direction of the cylinder axis
	 * @param r	radius
	 * @param surfaceProperty	any surface properties
	 */
	public InfiniteCylinderMantle(String description, Vector3D pointOnAxis, Vector3D axisDirection, double radius, SurfaceProperty surfaceProperty, SceneObject parent, Studio studio)
	{
		super(description, surfaceProperty, parent, studio);

		setPointOnAxis(pointOnAxis);
		setAxisDirection(axisDirection);
		setRadius(radius);
	}
	
	public InfiniteCylinderMantle(InfiniteCylinderMantle original)
	{
		super(original);
		
		setPointOnAxis(original.getPointOnAxis().clone());
		setNormalisedAxisDirection(original.getNormalisedAxisDirection().clone());
		setRadius(original.getRadius());
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.sceneObjects.SceneObject#clone()
	 */
	@Override
	public InfiniteCylinderMantle clone()
	{
		return new InfiniteCylinderMantle(this);
	}

	//calculates intersection between a cylinder and ray
	//mathematics from http://en.wikipedia.org/w/index.php?title=Ray_tracing_(graphics)&oldid=298033650

	public Vector3D getPointOnAxis() {
		return pointOnAxis;
	}

	public void setPointOnAxis(Vector3D pointOnAxis) {
		this.pointOnAxis = pointOnAxis;
	}

	public Vector3D getNormalisedAxisDirection() {
		return normalisedAxisDirection;
	}

	public void setNormalisedAxisDirection(Vector3D normalisedAxisDirection) {
		this.normalisedAxisDirection = normalisedAxisDirection;
	}

	public void setAxisDirection(Vector3D axisDirection) {
		this.normalisedAxisDirection = normalisedAxisDirection.getNormalised();
	}

	public double getRadius()
	{
		return radius;
	}

	public void setRadius(double radius)
	{
		this.radius = radius;
	}

	// doesn't quite work (yet) -- only calculates intersection with outside of cylinder
	@Override
	public RaySceneObjectIntersection getClosestRayIntersection(Ray ray)
	{
		Vector3D v=Vector3D.difference(ray.getP(), pointOnAxis);
		Vector3D vP = v.getDifferenceWith(v.getProjectionOnto(normalisedAxisDirection));	// part of v that's perpendicular to a
		Vector3D dP = ray.getD().getDifferenceWith(ray.getD().getProjectionOnto(normalisedAxisDirection));	// part of ray.d that's perpendicular to a
		
		// coefficients in the quadratic equation for t
		double a = dP.getModSquared();
		
		if(a==0.0) return RaySceneObjectIntersection.NO_INTERSECTION;	// would give division by zero later

		double
			b2 = Vector3D.scalarProduct(vP, dP),	// b/2
			c = vP.getModSquared() - radius*radius,
			discriminant4 = b2*b2 - a*c;	// discriminant/4

		// first check if the discriminant is >0; if it isn't, then there is no intersection at all
		if(discriminant4 < 0.0) return RaySceneObjectIntersection.NO_INTERSECTION;
		
		// okay, the discriminant is positive; take its square root, which is what we actually need
		double sqrtDiscriminant2 = Math.sqrt(discriminant4);	// sqrt(discriminant)/2
			
		// calculate the factor t corresponding to the
		// intersection with the greater t factor;
		// the further-away intersection is then ray.p + tBigger*ray.d
		double tBigger=((a>0)?(-b2+sqrtDiscriminant2)/a:(-b2-sqrtDiscriminant2)/a);
		
		//if tBigger<0, then the intersection with the lesser t factor will have to be even more negative;
		// therefore, both intersections will be "behind" the starting point
		if(tBigger < 0.0) return RaySceneObjectIntersection.NO_INTERSECTION;

		// calculate the factor tSmaller corresponding to the
		// intersection with the lesser t factor
		double tSmaller=((a>0)?(-b2-sqrtDiscriminant2)/a:(-b2+sqrtDiscriminant2)/a);

		Ray rayAtIntersectionPoint;
		
		// first check if the intersection point with the lesser t factor is an option
		if(tSmaller > 0.0)
		{
			// the intersection with the lesser t factor lies in front of the starting point, so it might correspond to the intersection point
			
			// calculate the ray advanced to the intersection point
			rayAtIntersectionPoint = ray.getAdvancedRay(tSmaller);
			
			return new RaySceneObjectIntersection(rayAtIntersectionPoint.getP(), this, rayAtIntersectionPoint.getT());
		}
		
		// If the program reaches this point, the intersection with the lesser t factor was not the right intersection.
		// Now try the intersection point with the greater t factor.
		
		// calculate the ray advanced to the intersection point
		rayAtIntersectionPoint = ray.getAdvancedRay(tBigger);

		return new RaySceneObjectIntersection(rayAtIntersectionPoint.getP(), this, rayAtIntersectionPoint.getT());
	}

	@Override
	public Vector3D getNormalisedOutwardsSurfaceNormal(Vector3D p)
	{
		Vector3D Ap = Vector3D.difference(p, pointOnAxis);
		return Ap.getDifferenceWith(Ap.getProjectionOnto(normalisedAxisDirection)).getNormalised();	// the surface normal at p is simply given by p-A, projected into plane perpendicular to a
	}
	
	@Override
	public InfiniteCylinderMantle transform(Transformation t)
	{
		return new InfiniteCylinderMantle(
				description,
				t.transformPosition(pointOnAxis),
				t.transformDirection(normalisedAxisDirection),
				radius,
				getSurfaceProperty(),
				getParent(),
				getStudio()
			);
	}
	
	@Override
	public boolean insideObject(Vector3D p)
	{
		Vector3D v = Vector3D.difference(p, pointOnAxis);
		
		double w=Vector3D.scalarProduct(normalisedAxisDirection, v);

		Vector3D u = v.getDifferenceWith(normalisedAxisDirection.getProductWith(w));	// v, projected into a plane perpendicular to h
		return u.getModSquared() <= radius*radius;
	}
	
	@Override
	public String getType()
	{
		return "Infinite cylinder mantle";
	}
}
