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
 * An elliptic cylinder without end caps.
 * 
 */
public class EllipticCylinderMantle extends SceneObjectPrimitive
{

	private static final long serialVersionUID = -3717018728796195151L;
	
	private Vector3D startPoint, endPoint;
	private Vector3D spanA, spanC;
	private boolean infinite;
	
	// variables to speed things up -- pre-calculate in validate
	private double length;	// |endPoint - startPoint|
	private Vector3D axis;	// normalised axis direction

	/**
	 * creates an elliptic cylinder mantle
	 * 
	 * @param description
	 * @param startPoint
	 * @param endPoint
	 * @param spanA
	 * @param spanC
	 * @param infinite
	 * @param surfaceProperty
	 * @param parent
	 * @param studio
	 */
	public EllipticCylinderMantle(
			String description,
			Vector3D startPoint,
			Vector3D endPoint,
			Vector3D spanA,
			Vector3D spanC,
			boolean infinite,
			SurfaceProperty surfaceProperty,
			SceneObject parent,
			Studio studio
		)
	{
		super(description, surfaceProperty, parent, studio);

		setStartPoint(startPoint);
		setEndPoint(endPoint);
		setSpanA(spanA);
		setSpanC(spanC);
		setInfinite(infinite);
		validate();
		setABC(spanA, axis.getNormalised(), spanC);
	}

	/**
	 * creates an elliptic cylinder mantle, this time using the focal lengths, which must be shorter than the length of the span vector A
	 * @param description
	 * @param startPoint
	 * @param endPoint
	 * @param spanA
	 * @param focalLength
	 * @param surfaceProperty
	 * @param parent
	 * @param studio
	 */
	public EllipticCylinderMantle(
			String description,
			Vector3D startPoint,
			Vector3D endPoint,
			Vector3D spanA,
			double focalLength,
			SurfaceProperty surfaceProperty,
			SceneObject parent,
			Studio studio
		)
	{
		this(
				description,
				startPoint,
				endPoint,
				spanA,
				calculateSpanC(startPoint, endPoint, spanA, focalLength),
				false,	// infinite
				surfaceProperty,
				parent,
				studio
			);
	}

	public EllipticCylinderMantle(EllipticCylinderMantle original)
	{
		super(original);
		
		setStartPoint(original.getStartPoint().clone());
		setEndPoint(original.getEndPoint().clone());
		setSpanA(original.getSpanA().clone());
		setSpanC(original.getSpanC().clone());
		setInfinite(original.isInfinite());
		
		validate();
	}


	@Override
	public EllipticCylinderMantle clone()
	{
		return new EllipticCylinderMantle(this);
	}
	
	//some variables needed to calculate the coordinate transform
	// private variables
	
	private Vector3D aHat, bHat, cHat;
//	private double a2, b2, c2;

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
	
	public Vector3D getSpanA() {
		return spanA;
	}

	public void setSpanA(Vector3D spanA) {
		this.spanA = spanA;
	}

	public Vector3D getSpanC() {
		return spanC;
	}

	public void setSpanC(Vector3D spanC) {
		this.spanC = spanC;
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
	
	private static Vector3D calculateSpanC(Vector3D startPoint, Vector3D endPoint, Vector3D spanA, double focalLength) {
		
		Vector3D normalVectorC = Vector3D.crossProduct(spanA, Vector3D.difference(endPoint, startPoint)).getNormalised();
		double c = Math.sqrt(spanA.getModSquared() - focalLength*focalLength);
		
		return normalVectorC.getProductWith(c);
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
	
	/**
	 * Set the semi-major axes, first b, then a (making sure it's perpendicular to b), then c 
	 * (making  sure it's perpendicular to both and of length 1 as this is an elliptic cylinder mantle not an ellipsoid).
	 * Note that the length of spanA and spanB might change in this process!
	 * @param a
	 * @param b
	 * @param c
	 */
	public void setABC(Vector3D a, Vector3D b, Vector3D c) {
		this.spanA = a.getPartPerpendicularTo(b);
		this.spanC = c.getPartPerpendicularTo(this.spanA, b);
		
		// first calculate the normalised semi-major axes
		aHat = this.spanA.getNormalised();
		bHat = b.getNormalised();
		cHat = this.spanC.getNormalised();
		
//		// for the calculation of the normal
//		a2 = this.spanA.getModSquared();
//		b2 = b.getModSquared();
//		c2 = this.spanC.getModSquared();
	}


	// vaguely interesting stuff
	
	
	// transform the direction vector into a coordinate system that is stretched such that the ellipsoid becomes a unit sphere
	public Vector3D stretch(Vector3D v)
	{
		return v.toBasis(spanA, axis.getNormalised(), spanC).fromBasis(aHat, bHat, cHat);
	}
 
	// doesn't quite work (yet) -- only calculates intersection with outside of cylinder
	@Override
	public RaySceneObjectIntersection getClosestRayIntersection(Ray ray)
	{
		Vector3D v=Vector3D.difference(ray.getP(), startPoint);
		Vector3D vt = stretch(v);
		Vector3D vP = vt.getDifferenceWith(vt.getProjectionOnto(axis));	// part of v that's perpendicular to a
		Vector3D rayDS = stretch(ray.getD());
		Vector3D dP = rayDS.getDifferenceWith(rayDS.getProjectionOnto(axis));	// part of ray.d that's perpendicular to a
		
		// coefficients in the quadratic equation for t
		double a = dP.getModSquared();
		
		if(a==0.0) return RaySceneObjectIntersection.NO_INTERSECTION;	// would give division by zero later

		double
			b2 = Vector3D.scalarProduct(vP, dP),	// b/2
			c = vP.getModSquared() - 1,
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
		Vector3D v = Vector3D.difference(p, startPoint).toBasis(aHat,bHat,cHat);	
		Vector3D ellipsoidNormal = new Vector3D (v.x/(spanA.getModSquared()),0,v.z/(spanC.getModSquared())).fromBasis(aHat,bHat,cHat);
		return ellipsoidNormal.getNormalised();
	}
	
	@Override
	public EllipticCylinderMantle transform(Transformation t)
	{
		return new EllipticCylinderMantle(
				description,
				t.transformPosition(startPoint),
				t.transformPosition(endPoint),
				t.transformDirection(spanA),
				t.transformDirection(spanC),
				isInfinite(),
				getSurfaceProperty(),
				getParent(),
				getStudio()
		);
	}
	
	@Override
	public boolean insideObject(Vector3D p)
	{
		Vector3D v = Vector3D.difference(p, startPoint);
		Vector3D vt = stretch(v);
		
		// check if p is within the right part of the (infinitely long) cylinder
		double w=Vector3D.scalarProduct(axis, v);
		if(!infinite) 
			if(w<0 || w>length) return false;	// no, it isn't

		Vector3D u = vt.getDifferenceWith(axis.getProductWith(w));	// v, projected into a plane perpendicular to h
		return (1 - u.getModSquared()) > 0;
				// u.getModSquared() <= radius*radius;
	}

	@Override
	public String getType()
	{
		return "Elliptic cylinder mantle";
	}
}
