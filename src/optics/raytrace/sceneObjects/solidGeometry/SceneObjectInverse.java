package optics.raytrace.sceneObjects.solidGeometry;

import java.io.Serializable;
import java.util.ArrayList;

import optics.raytrace.core.Ray;
import optics.raytrace.core.RaySceneObjectIntersection;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.SceneObjectClass;
import optics.raytrace.core.SceneObjectPrimitive;
import optics.raytrace.core.Transformation;
import math.Vector3D;

/**
 * Inverse of a scene object.
 * TODO Doesn't quite work (yet)...
 * 
 * @author Johannes Courtial
 */
public class SceneObjectInverse extends SceneObjectClass implements Serializable
{
	private static final long serialVersionUID = -3904085348051992163L;

	private SceneObject sceneObject;

	/**
	 * Create the inverse of a scene object
	 * 
	 * @param sceneObject
	 */
	public SceneObjectInverse(SceneObject sceneObject)
	{
		super("Inverted scene object", sceneObject.getParent(), sceneObject.getStudio());
		this.sceneObject = sceneObject;
	}
	
	/**
	 * Create a clone of the original
	 * @param original
	 */
	public SceneObjectInverse(SceneObjectInverse original)
	{
		super(original.description, original.getParent(), original.getStudio());
		sceneObject = original.sceneObject.clone();
	}
	
	/* (non-Javadoc)
	 * @see optics.raytrace.sceneObjects.SceneObject#clone()
	 */
	public SceneObjectInverse clone()
	{
		return new SceneObjectInverse(this);
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.SceneObject#getClosestRayIntersection(optics.raytrace.Ray)
	 */
	@Override
	public RaySceneObjectIntersection getClosestRayIntersection(Ray ray)
	{
		RaySceneObjectIntersection i = sceneObject.getClosestRayIntersection(ray);
		if(i == RaySceneObjectIntersection.NO_INTERSECTION)
			return RaySceneObjectIntersection.NO_INTERSECTION;

		// return an altered intersection that puts the intersecting object in a
		// inverse wrapper to return surface normals pointing in the opposite direction
		return new RaySceneObjectIntersection(
				i.p,
				One2OneParametrisedSceneObjectPrimitiveInverse.getSuitableSceneObjectPrimitiveInverse(i.o),
				i.t
			);
	}

	@Override
	public RaySceneObjectIntersection getClosestRayIntersectionWithShadowThrowingSceneObject(Ray ray)
	{
		RaySceneObjectIntersection i = sceneObject.getClosestRayIntersectionWithShadowThrowingSceneObject(ray);
		if(i == RaySceneObjectIntersection.NO_INTERSECTION)
			return RaySceneObjectIntersection.NO_INTERSECTION;

		// return an altered intersection that puts the intersecting object in a
		// inverse wrapper to return surface normals pointing in the opposite direction
		return new RaySceneObjectIntersection(
				i.p,
				One2OneParametrisedSceneObjectPrimitiveInverse.getSuitableSceneObjectPrimitiveInverse(i.o),
				i.t
			);
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.SceneObject#getClosestRayIntersectionAvoidingOrigin(optics.raytrace.Ray, optics.raytrace.SceneObject)
	 */
	@Override
	public RaySceneObjectIntersection getClosestRayIntersectionAvoidingOrigin(Ray ray, SceneObjectPrimitive originObject)
	{
		RaySceneObjectIntersection i = sceneObject.getClosestRayIntersectionAvoidingOrigin(ray, originObject);
		if(i == RaySceneObjectIntersection.NO_INTERSECTION)
			return RaySceneObjectIntersection.NO_INTERSECTION;

		// return an altered intersection that puts the intersecting object in a
		// inverse wrapper to return surface normals pointing in the opposite direction
		return new RaySceneObjectIntersection(
				i.p,
				One2OneParametrisedSceneObjectPrimitiveInverse.getSuitableSceneObjectPrimitiveInverse(i.o),
				i.t
			);
	}

	@Override
	public RaySceneObjectIntersection getClosestRayIntersectionWithShadowThrowingSceneObjectAvoidingOrigin(Ray ray, SceneObjectPrimitive originObject)
	{
		RaySceneObjectIntersection i = sceneObject.getClosestRayIntersectionWithShadowThrowingSceneObjectAvoidingOrigin(ray, originObject);
		if(i == RaySceneObjectIntersection.NO_INTERSECTION)
			return RaySceneObjectIntersection.NO_INTERSECTION;

		// return an altered intersection that puts the intersecting object in a
		// inverse wrapper to return surface normals pointing in the opposite direction
		return new RaySceneObjectIntersection(
				i.p,
				One2OneParametrisedSceneObjectPrimitiveInverse.getSuitableSceneObjectPrimitiveInverse(i.o),
				i.t
			);
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.SceneObjectContainer#transform(optics.raytrace.Transformation)
	 */
	@Override
	public SceneObjectInverse transform(Transformation t)
	{
		return new SceneObjectInverse(sceneObject.transform(t));
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.SceneObject#insideObject(optics.raytrace.Vector3D)
	 */
	@Override
	public boolean insideObject(Vector3D p)
	{
		return !sceneObject.insideObject(p);
	}
	
	@Override
	public ArrayList<SceneObjectPrimitive> getSceneObjectPrimitives()
	{
		ArrayList<SceneObjectPrimitive> SOPs = new ArrayList<SceneObjectPrimitive>();
		
		for (SceneObjectPrimitive sop: sceneObject.getSceneObjectPrimitives())
		{
			SOPs.add(One2OneParametrisedSceneObjectPrimitiveInverse.getSuitableSceneObjectPrimitiveInverse(sop));
		}
		// SOPs.addAll(sceneObject.getSceneObjectPrimitives());

		return SOPs;
	}
	
	@Override
	public String getType()
	{
		return "Inverse";
	}
}
