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
 * A cylinder without end caps.
 * 
 * @see Cylinder
 * @see Disc
 */
public class CylinderMantle extends SceneObjectPrimitive
{
	private static final long serialVersionUID = -3454424816209030609L;

	private Vector3D startPoint, endPoint;
	private double radius;
	private boolean infinite;
	
	// variables to speed things up -- pre-calculate in validate
	private double length;	// |endPoint - startPoint|
	private Vector3D axis;	// normalised axis direction

	/**
	 * creates a cylinder mantle, that is, a cylinder without end caps
	 * 
	 * @param description
	 * @param startPoint	one end of the cylinder axis
	 * @param endPoint	the other end of the cylinder axis
	 * @param r	radius
	 * @param surfaceProperty	any surface properties
	 */
	public CylinderMantle(
			String description,
			Vector3D startPoint,
			Vector3D endPoint,
			double radius,
			boolean infinite,
			SurfaceProperty surfaceProperty,
			SceneObject parent,
			Studio studio
		)
	{
		super(description, surfaceProperty, parent, studio);

		setStartPoint(startPoint);
		setEndPoint(endPoint);
		setRadius(radius);
		setInfinite(infinite);
		
		validate();
	}

	/**
	 * creates a cylinder mantle, that is, a cylinder without end caps
	 * 
	 * @param description
	 * @param startPoint	one end of the cylinder axis
	 * @param endPoint	the other end of the cylinder axis
	 * @param r	radius
	 * @param surfaceProperty	any surface properties
	 */
	public CylinderMantle(
			String description,
			Vector3D startPoint,
			Vector3D endPoint,
			double radius,
			SurfaceProperty surfaceProperty,
			SceneObject parent,
			Studio studio
		)
	{
		this(
				description,
				startPoint,
				endPoint,
				radius,
				false,	// infinite
				surfaceProperty,
				parent,
				studio
			);
	}

	public CylinderMantle(CylinderMantle original)
	{
		super(original);
		
		setStartPoint(original.getStartPoint().clone());
		setEndPoint(original.getEndPoint().clone());
		setRadius(original.getRadius());
		setInfinite(original.isInfinite());
		
		validate();
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.sceneObjects.SceneObject#clone()
	 */
	@Override
	public CylinderMantle clone()
	{
		return new CylinderMantle(this);
	}

	//calculates intersection between a cylinder and ray
	//mathematics from http://en.wikipedia.org/w/index.php?title=Ray_tracing_(graphics)&oldid=298033650

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

	public double getRadius()
	{
		return radius;
	}

	public void setRadius(double radius)
	{
		this.radius = radius;
	}

	
	public boolean isInfinite() {
		return infinite;
	}

	public void setInfinite(boolean infinite) {
		this.infinite = infinite;
	}

	/**
	 * run once all the variables have been set
	 */
	public void validate()
	{
		Vector3D startToEnd = Vector3D.difference(getEndPoint(), getStartPoint());
		
		length = startToEnd.getLength();
		axis = startToEnd.getNormalised();
	}

	/**
	 * @return	length of the cylinder
	 */
	public double getLength()
	{
		return length;
	}

	/**
	 * @return	normalised axis direction
	 */
	public Vector3D getAxis()
	{
		return axis;
	}

	// doesn't quite work (yet) -- only calculates intersection with outside of cylinder
	@Override
	public RaySceneObjectIntersection getClosestRayIntersection(Ray ray)
	{
		Vector3D v=Vector3D.difference(ray.getP(), startPoint);
		Vector3D vP = v.getDifferenceWith(v.getProjectionOnto(axis));	// part of v that's perpendicular to a
		Vector3D dP = ray.getD().getDifferenceWith(ray.getD().getProjectionOnto(axis));	// part of ray.d that's perpendicular to a
		
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
		double w;
		
		// first check if the intersection point with the lesser t factor is an option
		if(tSmaller > 0.0)
		{
			// the intersection with the lesser t factor lies in front of the starting point, so it might correspond to the intersection point
			
			// calculate the ray advanced to the intersection point
			rayAtIntersectionPoint = ray.getAdvancedRay(tSmaller);
			
			// does this intersection point (with the infinitely long cylinder) lie on the bit of the cylinder we want?
			if(infinite) 
				return new RaySceneObjectIntersection(rayAtIntersectionPoint.getP(), this, rayAtIntersectionPoint.getT());
			// finite
			w=Vector3D.scalarProduct(axis, Vector3D.difference(rayAtIntersectionPoint.getP(), startPoint));
			if(w>=0 && w<=length)
				return new RaySceneObjectIntersection(rayAtIntersectionPoint.getP(), this, rayAtIntersectionPoint.getT());
		}
		
		// If the program reaches this point, the intersection with the lesser t factor was not the right intersection.
		// Now try the intersection point with the greater t factor.
		
		// calculate the ray advanced to the intersection point
		rayAtIntersectionPoint = ray.getAdvancedRay(tBigger);

		// if the cylinder is infinitely long (in both directions), then we have found an intersection
		if(infinite) return new RaySceneObjectIntersection(rayAtIntersectionPoint.getP(), this, rayAtIntersectionPoint.getT());
		
		// if it is not infinitely long, we need to check if the intersection lies on the right part of the (infinitely long) cylinder
		w=Vector3D.scalarProduct(axis, Vector3D.difference(rayAtIntersectionPoint.getP(), startPoint));
		if(w>=0 && w<=length)
			return new RaySceneObjectIntersection(rayAtIntersectionPoint.getP(), this, rayAtIntersectionPoint.getT());
		
		// neither of the intersection points was right, so return NO_INTERSECTION
		return RaySceneObjectIntersection.NO_INTERSECTION;
	}

	@Override
	public Vector3D getNormalisedOutwardsSurfaceNormal(Vector3D p)
	{
		Vector3D Ap = Vector3D.difference(p, startPoint);
		return Ap.getDifferenceWith(Ap.getProjectionOnto(axis)).getWithLength(Math.signum(radius));	// the surface normal at p is simply given by p-A, projected into plane perpendicular to a
	}
	
	@Override
	public CylinderMantle transform(Transformation t)
	{
		return new CylinderMantle(
				description,
				t.transformPosition(startPoint),
				t.transformPosition(endPoint),
				radius,
				getSurfaceProperty(),
				getParent(),
				getStudio()
			);
	}
	
	@Override
	public boolean insideObject(Vector3D p)
	{
		Vector3D v = Vector3D.difference(p, startPoint);
		
		// check if p is within the right part of the (infinitely long) cylinder
		double w=Vector3D.scalarProduct(axis, v);
		if(!infinite) 
			if(w<0 || w>length) return false;	// no, it isn't

		Vector3D u = v.getDifferenceWith(axis.getProductWith(w));	// v, projected into a plane perpendicular to h
		return (radius*radius - u.getModSquared())*Math.signum(radius) > 0;
				// u.getModSquared() <= radius*radius;
	}

	@Override
	public String getType()
	{
		return "Cylinder mantle";
	}
}
