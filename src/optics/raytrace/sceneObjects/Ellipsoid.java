package optics.raytrace.sceneObjects;

import java.io.*;

import math.*;
import optics.raytrace.core.*;


/**
 * Supplies a SceneObject that's an ellipsoid.
 * 
 * @author Gordon Wells and Johannes Courtial
 *
 */

public class Ellipsoid extends SceneObjectPrimitive implements Serializable
{
	private static final long serialVersionUID = 4340808065078293753L;

	private Vector3D centre;	// ellipsoid's centre
	private Vector3D a, b, c;	// principal semi-axes -- see https://en.wikipedia.org/wiki/Ellipsoid; not quite sure what happens if these are not orthogonal to each other

	// constructor
	/**
	 * @param description
	 * @param centre
	 * @param a
	 * @param b
	 * @param c
	 * @param surfaceProperty
	 * @param parent
	 * @param studio
	 */
	public Ellipsoid(
			String description,
			Vector3D centre,
			Vector3D a,
			Vector3D b,
			Vector3D c,
			SurfaceProperty surfaceProperty,
			SceneObject parent,
			Studio studio
		)
	{
		super(description, surfaceProperty, parent, studio);
		this.centre = centre;        //passes current ellipsoid's center
		setABC(a, b, c);
	}
	
	/**
	 * Create a clone of the original
	 * @param original
	 */
	public Ellipsoid(Ellipsoid original)
	{
		this(
				original.getDescription(),
				original.getCentre().clone(),
				original.getA().clone(),
				original.getB().clone(),
				original.getC().clone(),
				original.getSurfaceProperty().clone(),
				original.getParent(),
				original.getStudio()
			);
	}
	
	/* (non-Javadoc)
	 * @see optics.raytrace.sceneObjects.SceneObject#clone()
	 */
	@Override
	public Ellipsoid clone()
	{
		return new Ellipsoid(this);
	}
	
	
	// private variables
	
	private Vector3D aHat, bHat, cHat;
	

	
	// getters & setters
	
	public Vector3D getCentre() {
		return centre;
	}

	public void setCentre(Vector3D centre) {
		this.centre = centre;
	}

	public Vector3D getA() {
		return a;
	}

	public Vector3D getB() {
		return b;
	}

	public Vector3D getC() {
		return c;
	}
	
	/**
	 * Set the semi-major axes, first a, then b (making sure it's perpendicular to a), then c (making  sure it's perpendicular to both).
	 * Note that the length of b and c might change in this process!
	 * @param a
	 * @param b
	 * @param c
	 */
	public void setABC(Vector3D a, Vector3D b, Vector3D c) {
		this.a = a;
		this.b = b.getPartPerpendicularTo(a);
		this.c = c.getPartPerpendicularTo(this.a, this.b);
		
		// first calculate the normalised semi-major axes
		aHat = this.a.getNormalised();
		bHat = this.b.getNormalised();
		cHat = this.c.getNormalised();
	}


	// vaguely interesting stuff
	
	
	// transform the direction vector into a coordinate system that is stretched such that the ellipsoid becomes a unit sphere
	public Vector3D stretch(Vector3D v)
	{
		return v.toBasis(a, b, c).fromBasis(aHat, bHat, cHat);
	}

	//calculates intersection between a sphere and the Ray r
	//mathematics from http://en.wikipedia.org/w/index.php?title=Ray_tracing_(graphics)&oldid=298033650

	@Override
	public RaySceneObjectIntersection getClosestRayIntersection(Ray ray)
	{		
		
		// vector from the centre of the ellipsoid to P
		Vector3D v = Vector3D.difference(ray.getP(), centre);
		Vector3D vS = stretch(v);	// v in  the stretched coordinate system
		
		// direction vector
		Vector3D dS = stretch(ray.getD());	// d in  the stretched coordinate system
		
		// coefficients in the quadratic equation for t
		double 
		quadraticA = dS.getModSquared(),
		quadraticB2 = Vector3D.scalarProduct(vS, dS),	// b/2
		quadraticC = vS.getModSquared() - 1;

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
		return stretch(Vector3D.difference(p, centre)).getNormalised();	
		// TODO This is clearly wrong -- think about the geometry and fix it!
	}

	@Override
	public Ellipsoid transform(Transformation t)
	{
		return new Ellipsoid(
				description,
				t.transformPosition(centre),
				t.transformDirection(a),
				t.transformDirection(b),
				t.transformDirection(c),
				getSurfaceProperty(),
				getParent(),
				getStudio()
		);
	}

	/**
	 * Is the point p inside the ellipsoid?
	 * @param p
	 * @return true if p is inside the sphere, false otherwise
	 */
	@Override
	public boolean insideObject(Vector3D p)
	{
		return stretch(Vector3D.difference(p, centre)).getModSquared() < 1.;
	}

	@Override
	public String toString() {
		return "<Ellipsoid>\n" +
		"\t<centre Vector3D="+centre+">\n" + 
		"\t<a Vector3D="+a+">\n" + 
		"\t<b Vector3D="+b+">\n" + 
		"\t<c Vector3D="+c+">\n" + 
		"</Ellipsoid>\n";
	}
	
	@Override
	public String getType()
	{
		return "Ellipsoid";
	}
}
