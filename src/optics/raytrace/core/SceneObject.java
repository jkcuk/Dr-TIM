package optics.raytrace.core;

import java.util.ArrayList;

import optics.DoubleColour;

import optics.raytrace.exceptions.RayTraceException;
import math.Vector3D;

/**
 * Represents a three dimensional object in space (such as a sphere) or a 
 * collection of such objects that make up a scene.  This is the key part
 * of the ray tracer that can find the intersection(s) between any specific
 * ray and the object, and what colour that intersection point would have.
 * 
 * @author Johannes Courtial
 */
public interface SceneObject
{
	/**
	 * A string describing the type
	 * @return
	 */
	public String getType();
	
	/**
	 * Each scene object has a string describing it.
	 * @return the description of the scene object
	 */
	public String getDescription();

	/**
	 * Each scene object has a string describing it.
	 * @param description the scene object's description
	 */
	public void setDescription(String description);
		
	/**
	 * At what point on the object did the ray intersect?  If the intersection didn't take place then 
	 * RaySceneObjectIntersection.NO_INTERSECTION should be returned.
	 * @param ray 
	 * @return The closest intersection between the ray and this SceneObject or any SceneObject contained in it.
	 */
	public RaySceneObjectIntersection getClosestRayIntersection(Ray ray);
	
	/**
	 * Just like getClosestRayIntersection, but only consider shadow-throwing scene objects.
	 * @param ray
	 * @return The closest intersection between the ray and this SceneObject (if it is shadow-throwing) or any shadow-throwing SceneObject contained in it.
	 */
	public RaySceneObjectIntersection getClosestRayIntersectionWithShadowThrowingSceneObject(Ray ray);
	
	/**
	 * Calculates the closest intersection between the ray and this SceneObject or any SceneObjects contained
	 * within it.
	 * 
	 * The SceneObject originObject is the SceneObject on which the ray originated, so it should intersect  
	 * with the ray at the ray's starting point.
	 * We are not interested in this point, but we are interested in other points where ray and originObject might intersect.
	 * This is why originObject is being passed around.
	 * 
	 * @param ray		the ray
	 * @param originObject	the SceneObject on which ray originated
	 * @return			the closest intersection between the ray and this SceneObject
	 */
	public RaySceneObjectIntersection getClosestRayIntersectionAvoidingOrigin(Ray ray, SceneObjectPrimitive originObject);

	public RaySceneObjectIntersection getClosestRayIntersectionWithShadowThrowingSceneObjectAvoidingOrigin(Ray ray, SceneObjectPrimitive originObject);

	/**
	 * Finds the (n+1)th-closest intersection between the ray and this.
	 * 
	 * This is useful if the nth-closest intersection is not what we were looking for.
	 * 
	 * @param ray	ray
	 * @param i	the nth-closest intersection
	 * @return	the (n+1)th-closest intersection
	 */
	public RaySceneObjectIntersection getNextClosestRayIntersection(Ray ray, RaySceneObjectIntersection i);

	public RaySceneObjectIntersection getNextClosestRayIntersectionWithShadowThrowingSceneObject(Ray ray, RaySceneObjectIntersection i);

	/**
	 * Finds the (n+1)th-closest intersection between the ray and this.
	 * 
	 * This is useful if the nth-closest intersection is not what we were looking for.
	 * 
	 * @param ray	ray
	 * @param originObject	the SceneObject on which the ray originated
	 * @param i	the nth-closest intersection
	 * @return	the (n+1)th-closest intersection
	 */
	public RaySceneObjectIntersection getNextClosestRayIntersectionAvoidingOrigin(Ray ray, SceneObjectPrimitive originObject, RaySceneObjectIntersection i);

	public RaySceneObjectIntersection getNextClosestRayIntersectionWithShadowThrowingSceneObjectAvoidingOrigin(Ray ray, SceneObjectPrimitive originObject, RaySceneObjectIntersection i);

	/**
	 * Returns the SceneObject, transformed according to a geometrical transformation.
	 * 
	 * @param t	the geometrical transformation
	 * @return the transformed SceneObject
	 */
	public SceneObject transform(Transformation t);

	/**
	* Is the position p inside the SceneObject?
	* 
	* @param p	a position
	* @return true if p is inside the Scene, false otherwise
	*/
	public boolean insideObject(Vector3D p);

	/**
	 * What is the colour of the intersection point between the ray and this object?
	 * The light source(s) illuminating the object are also important.
	 * 
	 * @param ray ray with which the object is seen
	 * @param l light source(s) illuminating the object
	 * @param scene	the entire scene, in case it's required for further ray tracing
	 * @param traceLevel
	 * @return the colour of the intersection point
	 */
	public DoubleColour getColour(Ray ray, LightSource l, SceneObject scene, int traceLevel, RaytraceExceptionHandler raytraceExceptionHandler)
	throws RayTraceException;
	
//	/**
//	 * Trace a light ray that records its trajectory through the scene.
//	 * @param ray
//	 * @param l
//	 * @param scene
//	 * @param traceLevel
//	 * @param raytraceExceptionHandler
//	 * @throws RayTraceException
//	 */
//	public void traceRayWithTrajectory(RayWithTrajectory ray, LightSource l, SceneObject scene, int traceLevel, RaytraceExceptionHandler raytraceExceptionHandler)
//	throws RayTraceException;
	
	/**
	 * What is the colour of the intersection point between the ray and this object?
	 * The light source(s) illuminating  object is also important.
	 * 
	 * @param ray ray with which the object is seen
	 * @param l light source(s) illuminating the object
	 * @param scene	the entire scene, in case it's required for further ray tracing
	 * @param traceLevel
	 * @return the colour of the intersection point
	 */
	public DoubleColour getColourAvoidingOrigin(Ray ray, SceneObjectPrimitive originObject, LightSource l, SceneObject scene, int traceLevel, RaytraceExceptionHandler raytraceExceptionHandler)
	throws RayTraceException;
	
	public abstract SceneObject clone();
	
	/**
	 * What are the SceneObjectPrimitives included in this SceneObject?
	 * @return	an ArrayList of all SceneObjectPrimitives included
	 */
	public abstract ArrayList<SceneObjectPrimitive> getSceneObjectPrimitives();
	
	/**
	 * Any scene object must be aware of the studio as a whole.
	 * (This was introduced when the Teleporting surface property was introduced.
	 * In its interactive incarnation, it needs to be able to choose one scene
	 * object from all objects in the scene, so something needs to be aware of the
	 * scene as a whole.)
	 * @return	the studio
	 */
	public abstract Studio getStudio();
	
	public abstract void setStudio(Studio studio);
	
	/**
	 * @return	the scene object above this one in the hierarchy
	 */
	public abstract SceneObject getParent();
	
	public abstract void setParent(SceneObject parent);
}
