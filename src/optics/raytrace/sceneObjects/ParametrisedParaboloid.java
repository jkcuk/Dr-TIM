package optics.raytrace.sceneObjects;

import java.io.Serializable;
import java.util.ArrayList;

import optics.raytrace.core.One2OneParametrisedObject;
import optics.raytrace.core.Ray;
import optics.raytrace.core.RaySceneObjectIntersection;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.SceneObjectPrimitive;
import optics.raytrace.core.Studio;
import optics.raytrace.core.SurfaceProperty;
import optics.raytrace.core.Transformation;
import math.Vector2D;
import math.Vector3D;

/**
 * a paraboloid, given in a suitable (u, v, w) coordinate system by the equation w = a u^2 + b v^2,
 * parametrised by u and v
 * 
 * @author Johannes Courtial
 *
 */
public class ParametrisedParaboloid extends SceneObjectPrimitive implements One2OneParametrisedObject, Serializable
{
	private static final long serialVersionUID = 1672316387834536265L;

	protected Vector3D
		vertex,	// the position of the vertex
		uHat,	// normalised direction of the u axis
		vHat,	// normalised direction of the v axis
		wHat;	// normalised direction of the w axis
	protected double
		a, b,	// paraboloid parameters
		height;	// height
	
	public ParametrisedParaboloid(String description, Vector3D vertex, Vector3D uHat, Vector3D vHat, Vector3D wHat, double a, double b, double height, SurfaceProperty surfaceProperty, SceneObject parent, Studio studio)
	{
		super(description, surfaceProperty, parent, studio);

		// take a note of all the values
		setVertex(vertex);
		setCoordinateSystem(uHat, vHat, wHat);
		setA(a);
		setB(b);
		setHeight(height);
	}
	
	public ParametrisedParaboloid(ParametrisedParaboloid original)
	{
		this(
				original.getDescription(),
				original.getVertex(),
				original.getuHat(),
				original.getvHat(),
				original.getwHat(),
				original.getA(),
				original.getB(),
				original.getHeight(),
				original.getSurfaceProperty(),
				original.getParent(),
				original.getStudio()
		);
	}
	
	/* (non-Javadoc)
	 * @see optics.raytrace.sceneObjects.SceneObject#clone()
	 */
	@Override
	public ParametrisedParaboloid clone()
	{
		return new ParametrisedParaboloid(this);
	}

	@Override
	public RaySceneObjectIntersection getClosestRayIntersection(Ray r)
	{
		Vector3D
			Puvw = Vector3D.difference(r.getP(), getVertex()).toBasis(uHat, vHat, wHat),	// ray start point in surface coordinates
			duvw = r.getD().toBasis(uHat, vHat, wHat);	// ray direction in scene object's surface coordinate system
		
		//coefficients of delta in the quadratic equation
		double
			A = a * duvw.x * duvw.x + b* duvw.y * duvw.y,
			B = 2*(a*Puvw.x*duvw.x + b*Puvw.y*duvw.y) - duvw.z,
			C = a*Puvw.x*Puvw.x + b*Puvw.y*Puvw.y - Puvw.z;

		if(A==0.0)
			return RaySceneObjectIntersection.NO_INTERSECTION;	// would give division by zero later

		// discriminant
		double discriminant = B*B - 4*A*C;
		
		// first check if the discriminant is >0; if it isn't, then there is no intersection at all
		if(discriminant < 0.0) return RaySceneObjectIntersection.NO_INTERSECTION;
		
		// okay, the discriminant is positive; take its square root, which is what we actually need
		double sqrtDiscriminant = Math.sqrt(discriminant);
			
		// calculate the factor delta corresponding to the
		// intersection with the greater delta factor;
		// the further-away intersection is then ray.p + deltaBigger*ray.d
		double deltaBigger=((A>0)?(-B+sqrtDiscriminant)/(2*A):(-B-sqrtDiscriminant)/(2*A));
		
		//if deltaBigger<0, then the intersection with the lesser delta factor will have to be even more negative;
		// therefore, both intersections will be "behind" the starting point
		if(deltaBigger < 0.0) return RaySceneObjectIntersection.NO_INTERSECTION;

		// calculate the factor deltaSmaller corresponding to the
		// intersection with the lesser delta factor
		double deltaSmaller=((A>0)?(-B-sqrtDiscriminant)/(2*A):(-b+sqrtDiscriminant)/(2*A));

		Ray rayAtIntersection;
		double w;
		
		// first check if the intersection point with the lesser delta factor is an option
		if(deltaSmaller > 0.0)
		{
			// the intersection with the lesser delta factor lies in front of the starting point, so it might correspond to the intersection point
			
			// calculate the ray advanced by the lesser delta factor
			rayAtIntersection = r.getAdvancedRay(deltaSmaller);
			
			// does this intersection point (with the infinitely long cone top) lie on the bit of the cone top we want?
			w = Vector3D.scalarProduct(getwHat(), Vector3D.difference(rayAtIntersection.getP(), getVertex()));			
			if(w>=0 && w<=getHeight()) return new RaySceneObjectIntersection(rayAtIntersection.getP(), this, rayAtIntersection.getT());
		}
		
		// If the program reaches this point, the intersection with the lesser t factor was not the right intersection.
		// Now try the intersection point with the greater t factor.
		
		// calculate the advanced ray
		rayAtIntersection = r.getAdvancedRay(deltaBigger);
		
		// ... and check if it lies on the right part of the paraboloid
		w=Vector3D.scalarProduct(getwHat(), Vector3D.difference(rayAtIntersection.getP(), getVertex()));
		if(w>=0 && w<=getHeight())	return new RaySceneObjectIntersection(rayAtIntersection.getP(), this, rayAtIntersection.getT());
		
		// neither of the intersection points was right, so return NO_INTERSECTION
		return RaySceneObjectIntersection.NO_INTERSECTION;
	}

	@Override
	public Vector3D getNormalisedOutwardsSurfaceNormal(Vector3D p)
	{
		// calculate the point p in the uvw coordinate system
		Vector3D Puvw = Vector3D.difference(p, getVertex()).toBasis(uHat, vHat, wHat);
		
		// in the uvw coordinate system, the outwards surface normal is then
		Vector3D Nuvw = new Vector3D(2*getA()*Puvw.x, 2*getB()*Puvw.y, -1);
		
		// return this vector in the xyz coordinate system
		return Nuvw.fromBasis(getuHat(), getvHat(), getwHat()).getNormalised();
	}

	@Override
	public ParametrisedParaboloid transform(Transformation t)
	{
		return new ParametrisedParaboloid(
				description,
				t.transformPosition(getVertex()),
				t.transformDirection(getuHat()),
				t.transformDirection(getvHat()),
				t.transformDirection(getwHat()),
				getA(), getB(), getHeight(), getSurfaceProperty(), getParent(), getStudio());
	}

	@Override
	public boolean insideObject(Vector3D p)
	{
		// calculate the point p in the uvw coordinate system
		Vector3D Puvw = Vector3D.difference(p, getVertex()).toBasis(uHat, vHat, wHat);

		double f = getA()*Puvw.x*Puvw.x + getB()*Puvw.y*Puvw.y - Puvw.z;
		
		if(f < 0)
		{
			// p is inside the (infinite) paraboloid
			return (0 <= Puvw.z && Puvw.z <= getHeight());
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return description + " [Paraboloid]";
	}


	public Vector3D getVertex() {
		return vertex;
	}

	public void setVertex(Vector3D vertex) {
		this.vertex = vertex;
	}

	public Vector3D getuHat() {
		return uHat;
	}

	public Vector3D getvHat() {
		return vHat;
	}

	public Vector3D getwHat() {
		return wHat;
	}

	/**
	 * @param uHat
	 * @param vHat
	 * @param wHat
	 * Sets the coordinate system.  wHat gets normalised; uHat is made perpendicular to wHat and normalised;
	 * vHat is made perpendicular to wHat and uHat and normalised.
	 */
	public void setCoordinateSystem(Vector3D uHat, Vector3D vHat, Vector3D wHat)
	{
		this.wHat = wHat.getNormalised();
		this.uHat = uHat.getPartPerpendicularTo(this.wHat).getNormalised();
		this.vHat = vHat.getPartParallelTo(Vector3D.crossProduct(this.uHat, this.wHat)).getNormalised();
	}

	public double getA() {
		return a;
	}

	public void setA(double a) {
		this.a = a;
	}

	public double getB() {
		return b;
	}

	public void setB(double b) {
		this.b = b;
	}

	public double getHeight()
	{
		return height;
	}

	public void setHeight(double height)
	{
		this.height = height;
	}
	
	//
	// parametrise the paraboloid
	//
	
	/* (non-Javadoc)
	 * @see optics.raytrace.surfaces.ParametrisedSurface#getParametersForSurfacePoint(math.Vector3D)
	 */
	@Override
	public Vector2D getSurfaceCoordinates(Vector3D p)
	{
		// calculate the point p in the uvw coordinate system
		Vector3D Puvw = Vector3D.difference(p, getVertex()).toBasis(uHat, vHat, wHat);
		
		return(new Vector2D(
				Puvw.x,	// the first parameter is the coordinate along the u axis
				Puvw.y	// the second parameter is the coordinate along the v axis
			));
	}
	
	/**
	 * @return the names of the parameters, e.g. ("theta", "phi")
	 */
	@Override
	public ArrayList<String> getSurfaceCoordinateNames()
	{
		ArrayList<String> parameterNames = new ArrayList<String>(2);
		parameterNames.add("u");
		parameterNames.add("v");
		
		return parameterNames;
	}

	@Override
	public Vector3D getPointForSurfaceCoordinates(double u, double v)
	{
		return Vector3D.sum(
				getVertex(),
				new Vector3D(u, v, getA()*u*u + getB()*v*v).fromBasis(getuHat(), getvHat(), getwHat())
			);
	}

	/**
	 * Returns the u and v directions
	 * 
	 * @see optics.raytrace.core.ParametrisedObject#getSurfaceCoordinateAxes(math.Vector3D)
	 */
	@Override
	public ArrayList<Vector3D> getSurfaceCoordinateAxes(Vector3D p)
	{
		ArrayList<Vector3D> ds = new ArrayList<Vector3D>(2);

		ds.add(0, getuHat());
		ds.add(1, getvHat());

		return ds;
	}

	@Override
	public String getType()
	{
		return "Paraboloid";
	}
}
