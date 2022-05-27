package optics.raytrace.sceneObjects;

import math.Vector3D;
import optics.raytrace.core.Ray;
import optics.raytrace.core.RaySceneObjectIntersection;
import optics.raytrace.core.SceneObjectPrimitive;
import optics.raytrace.core.SurfaceProperty;
import optics.raytrace.core.Transformation;

/**
 * A wrapper around another scene object primitive that gives that scene object a different surface property.
 * 
 * @author johannes
 */
public class WrappedSceneObjectPrimitive extends SceneObjectPrimitive
{
	private static final long serialVersionUID = -6681660319712706515L;

	/**
	 * the "wrapped" scene-object primitive
	 */
	private SceneObjectPrimitive sceneObjectPrimitive;
		
	/**
	 * Create a scene object representing <sceneObject>, "wrapped" in <surfaceProperty> 
	 * @param sceneObject
	 * @param surfaceProperty
	 */
	public WrappedSceneObjectPrimitive(
			String description,
			SceneObjectPrimitive sceneObjectPrimitive,
			SurfaceProperty surfaceProperty
		)
	{
		super(description, surfaceProperty, sceneObjectPrimitive.getParent(), sceneObjectPrimitive.getStudio());
		this.sceneObjectPrimitive = sceneObjectPrimitive;
	}
	
	public WrappedSceneObjectPrimitive(
			SceneObjectPrimitive sceneObjectPrimitive,
			SurfaceProperty surfaceProperty
			)
	{
		this(
				sceneObjectPrimitive.getDescription() + " (wrapped)",
				sceneObjectPrimitive,
				surfaceProperty
			);
	}


	public WrappedSceneObjectPrimitive(WrappedSceneObjectPrimitive original) {
		super(original);
		this.sceneObjectPrimitive = original.getSceneObjectPrimitive();
	}

	@Override
	public WrappedSceneObjectPrimitive clone() {
		return new WrappedSceneObjectPrimitive(this);
	}

	
	// getters & setters

	public SceneObjectPrimitive getSceneObjectPrimitive() {
		return sceneObjectPrimitive;
	}

	public void setSceneObjectPrimitive(SceneObjectPrimitive sceneObjectPrimitive) {
		this.sceneObjectPrimitive = sceneObjectPrimitive;
	}

	
	
	
	@Override
	public String getType() {
		return "Wrapped scene-object primitive";
	}

	@Override
	public RaySceneObjectIntersection getClosestRayIntersection(Ray ray) {
		return sceneObjectPrimitive.getClosestRayIntersection(ray);
	}

	@Override
	public boolean insideObject(Vector3D p) {
		return sceneObjectPrimitive.insideObject(p);
	}

	@Override
	public Vector3D getNormalisedOutwardsSurfaceNormal(Vector3D p) {
		return sceneObjectPrimitive.getNormalisedOutwardsSurfaceNormal(p);
	}

	@Override
	public SceneObjectPrimitive transform(Transformation t) {
		return new WrappedSceneObjectPrimitive(
				getDescription() + "(transformed)",
				sceneObjectPrimitive.transform(t),
				getSurfaceProperty()
			)
;
	}
	
}
