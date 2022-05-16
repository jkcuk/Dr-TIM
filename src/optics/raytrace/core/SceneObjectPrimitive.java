package optics.raytrace.core;

import java.io.Serializable;
import java.util.ArrayList;

import optics.DoubleColour;
import math.MyMath;
import math.Vector3D;
import optics.raytrace.exceptions.RayTraceException;

public abstract class SceneObjectPrimitive extends SceneObjectClass implements Serializable
{
	private static final long serialVersionUID = 1025818875890971407L;

	private SurfaceProperty surfaceProperty;	//surface property/properties

	public SceneObjectPrimitive(String description, SurfaceProperty surfaceProperty, SceneObject parent, Studio studio)
	{
		super(description, parent, studio);
		
		setSurfaceProperty(surfaceProperty);
	}
	
	public SceneObjectPrimitive(SceneObjectPrimitive original)
	{
		super(original);
		
		setSurfaceProperty(original.getSurfaceProperty().clone());
	}
	
	/* (non-Javadoc)
	 * @see optics.raytrace.sceneObjects.SceneObject#clone()
	 */
	@Override
	public abstract SceneObjectPrimitive clone();

	public SurfaceProperty getSurfaceProperty()
	{
		return surfaceProperty;
	}

	public void setSurfaceProperty(SurfaceProperty surfaceProperty)
	{
		this.surfaceProperty = surfaceProperty;
	}


	/**
	 * @return true if the scene object throws a shadow, false if it doesn't
	 */
	public boolean isShadowThrowing() {
		if(surfaceProperty == null) return false;
		return surfaceProperty.isShadowThrowing();
	}
	
	/**
	 * Calculates the colour a specific incoming light ray would "see" if it hits the primitive scene object
	 * at a specific intersection point.
	 * 
	 * @param r	incoming light ray
	 * @param i	intersection between incoming light ray and primitive scene object
	 * @param scene	scene object(s) making up the scene to be rendered
	 * @param l	light source(s) illuminating the scene
	 * @param traceLevel	recursion limit
	 * @return	colour under which intersection is seen
	 */
//	protected DoubleColour getColourAtIntersection(RaySceneObjectIntersection intersection, Ray ray, LightSource l, SceneObject scene, int traceLevel, RaytraceExceptionHandler raytraceExceptionHandler)
	public DoubleColour getColourAtIntersection(Ray r, RaySceneObjectIntersection i, SceneObject scene, LightSource l, int traceLevel, RaytraceExceptionHandler raytraceExceptionHandler)
	throws RayTraceException
	{
		if(r.isReportToConsole())
		{
			System.out.println("Intersection: "+i+", normal: "+getNormalisedOutwardsSurfaceNormal(i.p));
		}

		// null surfaces should be shown as black silhouettes
		if (surfaceProperty==null)
		{
			return DoubleColour.BLACK;
		}

		return surfaceProperty.getColour(r, i, scene, l, traceLevel, raytraceExceptionHandler);
	}

	/**
	 * Calculates the surface normal at point p so that reflected rays etc. can be calculated.
	 * 
	 * The point p is assumed to be on the surface; the behaviour for p not being on the surface undefined.
	 * 
	 * The surface normal is normalised.
	 * It points in the direction of the SceneObject's outside.
	 * (Note that some objects, such as a plane, don't have an outside as such.  In that case, inside
	 * and outside are defined by the direction of the surface normal.)
	 * 
	 * @param p the point on the surface
	 * @return the surface normal at point p
	 */
	 public abstract Vector3D getNormalisedOutwardsSurfaceNormal(Vector3D p);

	/**
	 * Returns the SceneObjectPrimitive, transformed according to a geometrical transformation.
	 * 
	 * @param t	the geometrical transformation
	 * @return the transformed SceneObjectPrimitive
	 */
	@Override
	public abstract SceneObjectPrimitive transform(Transformation t);
	
	@Override
	public RaySceneObjectIntersection getClosestRayIntersectionAvoidingOrigin(Ray ray, SceneObjectPrimitive originObject)
	{
//		if(this == originObject)
//			// avoid calculating the intersection where the ray originated, calculate the intersection with a slightly advanced ray
//			return getClosestRayIntersection(ray.getAdvancedRay(MyMath.TINY));
//		return getClosestRayIntersection(ray);
		 RaySceneObjectIntersection i;
		 double distance = 0;
		 // make  a copy of the ray, which will be advanced repeatedly until a distinct intersection is found
		 Ray rayCopy = new Ray(ray);
		 
		 // try 100 times...
		 for(int j=0; j<100; j++)
		 {
			 // find the closest ray intersection
			 i = getClosestRayIntersection(rayCopy);
			 
			 // if there is no intersection, return it
			 if(i == RaySceneObjectIntersection.NO_INTERSECTION) return i;
			 
			 // there is an intersection;
			 // if the intersected scene object is different from originObject, the intersection is different; return it
			 if(i.o != originObject) return i;
			 
			 // the intersected scene object is the same as originObject;
			 // calculate the distance of the intersection from the ray's start point
			 distance = Vector3D.getDistance(i.p, ray.getP());
			 
			 // if the distance is > TINY, consider the intersection different
			 if(distance >= MyMath.TINY) return i;
			 
			 // no distinct intersection found so far; advance the ray copy
			 rayCopy.advance(MyMath.TINY);
		 }
		 
		 return RaySceneObjectIntersection.NO_INTERSECTION;
	}
	
	@Override
	public RaySceneObjectIntersection getClosestRayIntersectionWithShadowThrowingSceneObject(
			Ray ray) 
	{
		if(isShadowThrowing()) return getClosestRayIntersection(ray);
		else return RaySceneObjectIntersection.NO_INTERSECTION;
	}

	@Override
	public RaySceneObjectIntersection getClosestRayIntersectionWithShadowThrowingSceneObjectAvoidingOrigin(
			Ray ray, SceneObjectPrimitive originObject)
	{
		if(isShadowThrowing()) return getClosestRayIntersectionAvoidingOrigin(ray, originObject);
		else return RaySceneObjectIntersection.NO_INTERSECTION;
	}

	@Override
	public RaySceneObjectIntersection getNextClosestRayIntersectionWithShadowThrowingSceneObject(
			Ray ray, RaySceneObjectIntersection i)
	{
		if(isShadowThrowing()) return getNextClosestRayIntersection(ray, i);
		else return RaySceneObjectIntersection.NO_INTERSECTION;
	}

	@Override
	public RaySceneObjectIntersection getNextClosestRayIntersectionWithShadowThrowingSceneObjectAvoidingOrigin(
			Ray ray, SceneObjectPrimitive originObject, RaySceneObjectIntersection i)
	{
		if(isShadowThrowing()) return getNextClosestRayIntersectionAvoidingOrigin(ray, originObject, i);
		else return RaySceneObjectIntersection.NO_INTERSECTION;
	}

	
	@Override
	public ArrayList<SceneObjectPrimitive> getSceneObjectPrimitives()
	{
		ArrayList<SceneObjectPrimitive> SOPs = new ArrayList<SceneObjectPrimitive>();
		
		SOPs.add(this);
		
		return SOPs;
	}
}
