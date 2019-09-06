package optics.raytrace.sceneObjects;

import java.io.Serializable;

import optics.raytrace.core.Ray;
import optics.raytrace.core.RaySceneObjectIntersection;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.SceneObjectPrimitive;
import optics.raytrace.core.Studio;
import optics.raytrace.core.SurfaceProperty;
import optics.raytrace.core.Transformation;
import math.Vector3D;

/**
 * an "open" cone
 * 
 * @author Johannes Courtial
 *
 */
public class ConeTop extends SceneObjectPrimitive implements Serializable
{
	private static final long serialVersionUID = 4501045467084780636L;

	// parameters
	
	protected Vector3D
		apex,	// the apex of the cone
		axis;	// normalised direction of the cone axis
	protected double
		theta,	// cone angle
		height;	// height; infinite if height < 0
	
	
	// internal variables
	
	protected double
		tanTheta,	// tan theta -- pre-calculate in setTheta
		tan2Theta,	// tan^2 theta -- pre-calculate in setTheta
		cosTheta;	// cos(theta) -- pre-calculate in setTheta

	/**
	 * @param description
	 * @param apex
	 * @param axis
	 * @param theta
	 * @param height
	 * @param surfaceProperty
	 * @param studio
	 */
	public ConeTop(
			String description,
			Vector3D apex,
			Vector3D axis,
			double theta,
			double height,
			SurfaceProperty surfaceProperty,
			SceneObject parent,
			Studio studio
		)
	{
		super(description, surfaceProperty, parent, studio);

		// take a note of all the values
		// setApex(Vector3D.O);
		setApex(apex);
		// setAxis(Vector3D.Z);
		setAxis(axis);
		setTheta(theta);
		setHeight(height);
	}
	
	public ConeTop(ConeTop original)
	{
		super(original);

		// take a note of all the values
		setApex(original.getApex().clone());
		setAxis(original.getAxis().clone());
		setTheta(original.getTheta());
		setHeight(original.getHeight());
	}
	
	/* (non-Javadoc)
	 * @see optics.raytrace.sceneObjects.SceneObject#clone()
	 */
	@Override
	public ConeTop clone()
	{
		return new ConeTop(this);
	}

	@Override
	public RaySceneObjectIntersection getClosestRayIntersection(Ray r)
	{
		Vector3D
			localZ = getAxis(),	// locally defined new z axis
			localX = Vector3D.getANormal(localZ),	// normalised
			localY = Vector3D.crossProduct(localZ, localX).getNormalised(),
			P = r.getP(),	// ray start point
			PLocal = Vector3D.difference(P, getApex()).toBasis(localX, localY, localZ),	// ray start point in surface coordinates
			d = r.getD(),	// ray direction
			dLocal = d.toBasis(localX, localY, localZ);	// ray direction in scene object's surface coordinate system
		
		//coefficients of d in the quadratic equation
		double
			a = dLocal.x*dLocal.x + dLocal.y*dLocal.y - dLocal.z*dLocal.z*tan2Theta,
			b2 = PLocal.x*dLocal.x + PLocal.y*dLocal.y - PLocal.z*dLocal.z*tan2Theta,	// b/2
			c = PLocal.x*PLocal.x + PLocal.y*PLocal.y - PLocal.z*PLocal.z*tan2Theta;

		if(a==0.0)
			return RaySceneObjectIntersection.NO_INTERSECTION;	// would give division by zero later

		// discriminant/4
		double discriminant4 = b2*b2 - a*c;
		
		// first check if the discriminant is >0; if it isn't, then there is no intersection at all
		if(discriminant4 < 0.0) return RaySceneObjectIntersection.NO_INTERSECTION;
		
		// okay, the discriminant is positive; take its square root, which is what we actually need
		double sqrtDiscriminant2 = Math.sqrt(discriminant4);	// sqrt(discriminant)/2
			
		// calculate the factor delta corresponding to the
		// intersection with the greater delta factor;
		// the further-away intersection is then ray.p + deltaBigger*ray.d
		double deltaBigger=((a>0)?(-b2+sqrtDiscriminant2)/a:(-b2-sqrtDiscriminant2)/a);
		
		//if deltaBigger<0, then the intersection with the lesser delta factor will have to be even more negative;
		// therefore, both intersections will be "behind" the starting point
		if(deltaBigger < 0.0) return RaySceneObjectIntersection.NO_INTERSECTION;

		// calculate the factor deltaSmaller corresponding to the
		// intersection with the lesser delta factor
		double deltaSmaller=((a>0)?(-b2-sqrtDiscriminant2)/a:(-b2+sqrtDiscriminant2)/a);

		Ray rayAtIntersection;
		double w;
		
		// first check if the intersection point with the lesser delta factor is an option
		if(deltaSmaller > 0.0)
		{
			// the intersection with the lesser delta factor lies in front of the starting point, so it might correspond to the intersection point
			
			// calculate the ray advanced by the lesser delta factor
			rayAtIntersection = r.getAdvancedRay(deltaSmaller);
			
			// does this intersection point (with the infinitely long cone top) lie on the bit of the cone top we want?
			w = Vector3D.scalarProduct(getAxis(), Vector3D.difference(rayAtIntersection.getP(), getApex()));			
			if(w>=0 && w<=getHeight()) return new RaySceneObjectIntersection(rayAtIntersection.getP(), this, rayAtIntersection.getT());
		}
		
		// If the program reaches this point, the intersection with the lesser t factor was not the right intersection.
		// Now try the intersection point with the greater t factor.
		
		// calculate the advanced ray
		rayAtIntersection = r.getAdvancedRay(deltaBigger);
		
		// ... and check if it lies on the right part of the (infinitely long) cylinder
		w=Vector3D.scalarProduct(getAxis(), Vector3D.difference(rayAtIntersection.getP(), getApex()));
		if(w>=0 && (w<=getHeight() || getHeight() < 0))	return new RaySceneObjectIntersection(rayAtIntersection.getP(), this, rayAtIntersection.getT());
		
		// neither of the intersection points was right, so return NO_INTERSECTION
		return RaySceneObjectIntersection.NO_INTERSECTION;
	}

	@Override
	public Vector3D getNormalisedOutwardsSurfaceNormal(Vector3D p)
	{
//		double J=A.length()*(Math.cos(angle));
//		double K=Vector3D.difference(V, p).length();
//		Vector3D L=(Vector3D.scalarTimesVector3D(J/K, V.subtract(p))).subtract(A);
//		return L;
		
		Vector3D v = p.getDifferenceWith(apex);

		return Vector3D.crossProduct(Vector3D.crossProduct(axis, v), v).getNormalised();
	}

	@Override
	public ConeTop transform(Transformation t)
	{
		return new ConeTop(description, t.transformPosition(getApex()), t.transformDirection(getAxis()), getTheta(), getHeight(), getSurfaceProperty(), getParent(), getStudio());
	}

	@Override
	public boolean insideObject(Vector3D p)
	{
		Vector3D v = Vector3D.difference(p, getApex());	// p - apex
		double s = Vector3D.scalarProduct(v, getAxis());
		
		// if(v.getNormalised().getScalarProductWith(getAxis()) <= getHeight()*cosTheta)
		if(s >= cosTheta*v.getLength())
		{
			// return (s<0 || s>getHeight());
			// return (s<0 || (getHeight() < 0 || s>getHeight()));
			return (s >= 0 && (getHeight() < 0 || s <= getHeight()));
			// TODO is this correct?
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return description + " [ConeTop]";
	}

	public Vector3D getApex()
	{
		return apex;
	}

	public void setApex(Vector3D apex)
	{
		this.apex = apex;
	}

	public Vector3D getAxis()
	{
		return axis;
	}

	public void setAxis(Vector3D axis)
	{
		this.axis = axis.getNormalised();
	}

	public double getTheta()
	{
		return theta;
	}

	public void setTheta(double theta)
	{
		this.theta = theta;
		
		tanTheta = Math.tan(theta);
		tan2Theta = tanTheta * tanTheta;	// tan^2 theta
		cosTheta = Math.cos(theta);
	}

	public double getHeight()
	{
		return height;
	}

	public void setHeight(double height)
	{
		this.height = height;
	}
	
	@Override
	public String getType()
	{
		return "Cone top";
	}


}
