package optics.raytrace.sceneObjects;

import java.io.Serializable;
import java.util.ArrayList;

import math.Vector3D;
import optics.raytrace.core.ParametrisedObject;
import optics.raytrace.core.Ray;
import optics.raytrace.core.RaySceneObjectIntersection;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.SceneObjectClass;
import optics.raytrace.core.SceneObjectPrimitive;
import optics.raytrace.core.Transformation;
import optics.raytrace.exceptions.SceneException;
import optics.raytrace.surfaces.EitherOrSurface;

/**
 * @author johannes
 * A wrapper for scene objects that allows holes in the surface.
 * Whether or not at a particular point on the surface there is a hole or not is decided by the associated EitherOrSurface.
 */
public class SceneObjectWithHoles extends SceneObjectClass implements Serializable
{
	private static final long serialVersionUID = -5853173337947611075L;

	/**
	 * The *parametrised* scene object this is wrapped around
	 */
	protected SceneObject wrappedSceneObject;
	
	/**
	 * The surface property that decides whether or not a particular point on the object is a hole;
	 * unless inverted, surfaceProperty0 is a hole, surfaceProperty1 isn't
	 */
	private EitherOrSurface eitherOrSurface;
	
	/**
	 * If true, surfaceProperty1 is a hole, surfaceProperty0 isn't;
	 * if false, surfaceProperty0 is a hole, surfaceProperty1 isn't
	 */
	private boolean invert;

	
	//
	// constructors
	//
	
	/**
	 * Create a wrapper around <sceneObject> that switches the surface on or off
	 * @param description
	 * @param sceneObject
	 * @param eitherOrSurface
	 * @param invert
	 * @throws SceneException
	 */
	public SceneObjectWithHoles(String description, SceneObject sceneObject, EitherOrSurface eitherOrSurface, boolean invert)
	throws SceneException
	{
		super(description, sceneObject.getParent(), sceneObject.getStudio());
		
		setWrappedSceneObject(sceneObject);
		setEitherOrSurface(eitherOrSurface);
		setInvert(invert);
	}
	
	/**
	 * Create an "empty" scene object with holes.
	 * The wrapped scene object and the either-or surface must be set later.
	 * @param description
	 */
	public SceneObjectWithHoles(String description)
	{
		super(description, null, null);
		setInvert(false);
	}

	
	public SceneObjectWithHoles(SceneObjectWithHoles original)
	throws SceneException
	{
		this(original.getDescription(), original.getWrappedSceneObject(), original.getEitherOrSurface(), original.isInvert());
	}
	
	/* (non-Javadoc)
	 * @see optics.raytrace.sceneObjects.SceneObject#clone()
	 */
	@Override
	public SceneObjectWithHoles clone()
	{
		try {
			return new SceneObjectWithHoles(this);
		} catch (SceneException e) {
			// this shouldn't happen
			e.printStackTrace();
			return null;
		}
	}

	

	
	//
	// getters & setters
	//

	public SceneObject getWrappedSceneObject() {
		return wrappedSceneObject;
	}

	/**
	 * Check that <sceneObject> is a ParametrisedObject, and if it is, set <sceneObject> to be the wrapped scene object
	 * @param sceneObject
	 * @throws SceneException	if <sceneObject> is not a ParametrisedObject
	 */
	public void setWrappedSceneObject(SceneObject sceneObject) throws SceneException {
		if(sceneObject instanceof ParametrisedObject)
		{
			this.wrappedSceneObject = sceneObject;
			setParent(sceneObject.getParent());
			setStudio(sceneObject.getStudio());
		}
		else
		{
			throw new SceneException("<sceneObject> should be of type ParametrisedObject, but isn't.");
		}
	}

	public EitherOrSurface getEitherOrSurface() {
		return eitherOrSurface;
	}

	public void setEitherOrSurface(EitherOrSurface eitherOrSurface) {
		this.eitherOrSurface = eitherOrSurface;
	}

	public boolean isInvert() {
		return invert;
	}

	public void setInvert(boolean invert) {
		this.invert = invert;
	}

	
	//
	// implement all SceneObjectClass methods
	//
	
	@Override
	public RaySceneObjectIntersection getClosestRayIntersection(Ray ray)
	{
		// start by finding the closest intersection between the ray and the "wrapped" scene-object primitive
		RaySceneObjectIntersection i = wrappedSceneObject.getClosestRayIntersection(ray);
		
		// if there is no intersection, say so
		if(i == RaySceneObjectIntersection.NO_INTERSECTION) return i;
		
		// if the either-or surface says that the intersection point is not a hole...
		if(eitherOrSurface.getSurfaceTypeAtIntersection(i) == (invert?0:1))
			// return the intersection point
			return i;
		
		// if the intersection point is a hole, look for the next intersection
		return getNextClosestRayIntersection(ray, i);
	}

	@Override
	public RaySceneObjectIntersection getClosestRayIntersectionWithShadowThrowingSceneObject(Ray ray)
	{
		// start by finding the closest intersection between the ray and the "wrapped" scene-object primitive
		RaySceneObjectIntersection i = wrappedSceneObject.getClosestRayIntersectionWithShadowThrowingSceneObject(ray);
		
		// if there is no intersection, say so
		if(i == RaySceneObjectIntersection.NO_INTERSECTION) return i;
		
		// if the either-or surface says that the intersection point is not a hole...
		if(eitherOrSurface.getSurfaceTypeAtIntersection(i) == (invert?0:1))
			// return the intersection point
			return i;
		
		// if the intersection point is a hole, look for the next intersection
		return getNextClosestRayIntersectionWithShadowThrowingSceneObject(ray, i);
	}

	@Override
	public RaySceneObjectIntersection getClosestRayIntersectionAvoidingOrigin(Ray ray,
			SceneObjectPrimitive originObject)
	{
		// start by finding the closest intersection between the ray and the "wrapped" scene-object primitive
		RaySceneObjectIntersection i = wrappedSceneObject.getClosestRayIntersectionAvoidingOrigin(ray, originObject);

		// if there is no intersection, say so
		if(i == RaySceneObjectIntersection.NO_INTERSECTION) return i;
		
		// if the either-or surface says that the intersection point is not a hole...
		if(eitherOrSurface.getSurfaceTypeAtIntersection(i) == (invert?0:1))
			// return the intersection point
			return i;
		
		// if the intersection point is a hole, look for the next intersection
		return getNextClosestRayIntersectionAvoidingOrigin(ray, originObject, i);
	}

	@Override
	public RaySceneObjectIntersection getClosestRayIntersectionWithShadowThrowingSceneObjectAvoidingOrigin(Ray ray,
			SceneObjectPrimitive originObject)
	{
		// start by finding the closest intersection between the ray and the "wrapped" scene-object primitive
		RaySceneObjectIntersection i = wrappedSceneObject.getClosestRayIntersectionWithShadowThrowingSceneObjectAvoidingOrigin(ray, originObject);
		
		// if there is no intersection, say so
		if(i == RaySceneObjectIntersection.NO_INTERSECTION) return i;
		
		// if the either-or surface says that the intersection point is not a hole...
		if(eitherOrSurface.getSurfaceTypeAtIntersection(i) == (invert?0:1))
			// return the intersection point
			return i;
		
		// if the intersection point is a hole, look for the next intersection
		return getNextClosestRayIntersectionWithShadowThrowingSceneObjectAvoidingOrigin(ray, originObject, i);
	}

	@Override
	public SceneObject transform(Transformation t)
	{
		try {
			return new SceneObjectWithHoles(getDescription(), getWrappedSceneObject().transform(t), getEitherOrSurface(), isInvert());
		} catch (SceneException e) {
			// this should never happen
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public boolean insideObject(Vector3D p)
	{
		return wrappedSceneObject.insideObject(p);
	}

	@Override
	public ArrayList<SceneObjectPrimitive> getSceneObjectPrimitives()
	{
		return wrappedSceneObject.getSceneObjectPrimitives();
	}
	
	@Override
	public String getType()
	{
		return "Object with holes";
	}
}
