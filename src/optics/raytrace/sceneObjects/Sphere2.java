package optics.raytrace.sceneObjects;

import java.io.*;

import math.*;
import optics.raytrace.core.*;


/**
 * Calculates the intersection between a ray and a sphere.
 * 
 * @author Dean et al.
 *
 */

public class Sphere2 extends SceneObjectPrimitive implements Serializable
{
	private static final long serialVersionUID = -3521763314343802512L;

	private Vector3D centre;	//current sphere's center
	private double radius;	//current sphere's radius
	
	/**
	 * if true, the outwards-facing surface normal points towards smaller radius
	 */
	private boolean inverse;
	
	//JTextField centreXPanel, centreYPanel, centreZPanel, radiusPanel;
	// protected JPanel panel;

	// constructor
	public Sphere2(
			String description, Vector3D centre, double radius,
			boolean inverse,
			SurfaceProperty surfaceProperty,
			SceneObject parent,
			Studio studio
		)
	{
		super(description, surfaceProperty, parent, studio);
		this.centre = centre;        //passes current sphere's center and radius
		this.radius = radius;
		this.inverse = inverse;
	}

	public Sphere2(
			String description, Vector3D centre, double radius,
			SurfaceProperty surfaceProperty,
			SceneObject parent,
			Studio studio
		)
	{
		this(description, centre, radius, false, surfaceProperty, parent, studio);
	}

//	public Sphere (String description, SceneObject parent, Studio studio)
//	{
//		super(description, new SurfaceColour(DoubleColour.GREEN, DoubleColour.BLACK), parent, studio);
//		this.centre = new Vector3D(0,0,10);
//		this.radius = 1.0;
//	}
	
	/**
	 * Create a clone of the original
	 * @param original
	 */
	public Sphere2(Sphere2 original)
	{
		this(
				original.description,
				original.getCentre().clone(),
				original.getRadius(),
				original.isInverse(),
				original.getSurfaceProperty().clone(),
				original.getParent(),
				original.getStudio()
			);
	}
	
	/* (non-Javadoc)
	 * @see optics.raytrace.sceneObjects.SceneObject#clone()
	 */
	@Override
	public Sphere2 clone()
	{
		return new Sphere2(this);
	}

	//calculates intersection between a sphere and the Ray r
	//mathematics from http://en.wikipedia.org/w/index.php?title=Ray_tracing_(graphics)&oldid=298033650

	@Override
	public RaySceneObjectIntersection getClosestRayIntersection(Ray ray)
	{		//this is calculating the "term under the square root"
		Vector3D v=Vector3D.difference(ray.getP(), centre);						//which must be greater than 0 for intersection

		// coefficients in the quadratic equation for t
		double 
		quadraticA = ray.getD().getModSquared(),
		quadraticB2 = Vector3D.scalarProduct(v, ray.getD()),	// b/2
		quadraticC = v.getModSquared() - MyMath.square(radius);

		// discriminant/2
		double discriminant2 = quadraticB2*quadraticB2-quadraticA*quadraticC;


		if(discriminant2<0.0) {
			//returns NaN if there is no intersection
			return RaySceneObjectIntersection.NO_INTERSECTION;
		}

		double t1=(-quadraticB2+Math.sqrt(discriminant2))/quadraticA;

		if(t1<0.0) {
			//if t1<0 then t2 must be less than zero
			return RaySceneObjectIntersection.NO_INTERSECTION;
		}

		double t2=(-quadraticB2-Math.sqrt(discriminant2))/quadraticA;
		
		Ray rayAtIntersectionPoint = ray.getAdvancedRay((t2<0.0)?t1:t2);

		return new RaySceneObjectIntersection(
				rayAtIntersectionPoint.getP(),
				this,
				rayAtIntersectionPoint.getT()
			);
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.sceneObjects.SceneObjectPrimitive#getNormalisedSurfaceNormal(math.Vector3D)
	 */
	@Override
	public Vector3D getNormalisedOutwardsSurfaceNormal(Vector3D p)
	{
		// the surface normal at p is simply given by p-c
		return Vector3D.difference(p, centre).getWithLength(inverse?-1:1);	
	}

	@Override
	public Sphere2 transform(Transformation t)
	{
		return new Sphere2(
				description,
				t.transformPosition(centre),
				radius,
				inverse,
				getSurfaceProperty(),
				getParent(),
				getStudio()
		);
	}

	/**
	 * Is the point p inside the sphere?
	 * @param p
	 * @return true if p is inside the sphere, false otherwise
	 */
	@Override
	public boolean insideObject(Vector3D p)
	{
		return (inverse
				?(centre.getDifferenceWith(p).getModSquared() > MyMath.square(radius))
				:(centre.getDifferenceWith(p).getModSquared() < MyMath.square(radius))
			);
	}

	public Vector3D getCentre() {
		return centre;
	}

	public void setCentre(Vector3D centre) {
		this.centre = centre;
	}

	public double getRadius() {
		return radius;
	}

	public void setRadius(double radius) {
		this.radius = radius;
	}

	public boolean isInverse() {
		return inverse;
	}

	public void setInverse(boolean inverse) {
		this.inverse = inverse;
	}

	@Override
	public String toString() {
		return "<Sphere>\n" +
		"\t<centre Vector3D="+centre+">\n" + 
		"\t<radius double="+radius+">\n" + 
		"\t<inverse boolean="+inverse+">\n" +
		"</Sphere>\n";
	}
	
	@Override
	public String getType()
	{
		return "Sphere";
	}
}
