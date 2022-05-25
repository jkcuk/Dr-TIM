package optics.raytrace.sceneObjects;

import java.util.ArrayList;

import math.Vector3D;
import optics.DoubleColour;
import optics.raytrace.core.LightSource;
import optics.raytrace.core.Ray;
import optics.raytrace.core.RaySceneObjectIntersection;
import optics.raytrace.core.RayWithTrajectory;
import optics.raytrace.core.RaytraceExceptionHandler;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.SceneObjectClass;
import optics.raytrace.core.SceneObjectPrimitive;
import optics.raytrace.core.SurfaceProperty;
import optics.raytrace.core.Transformation;
import optics.raytrace.exceptions.RayTraceException;

/**
 * A wrapper around another scene object that gives that scene object a different surface property.
 * 
 * @author johannes
 */
public class WrappedSceneObject extends SceneObjectClass {
	private static final long serialVersionUID = -7224184286798134063L;

	/**
	 * the "wrapped" scene object
	 */
	private SceneObject sceneObject;
	
	/**
	 * the surface property that is being wrapped around the scene object
	 */
	private SurfaceProperty surfaceProperty;
	
	/**
	 * Create a scene object representing <sceneObject>, "wrapped" in <surfaceProperty> 
	 * @param description
	 * @param sceneObject
	 * @param surfaceProperty
	 */
	public WrappedSceneObject(
			String description,
			SceneObject sceneObject,
			SurfaceProperty surfaceProperty
			)
	{
		super(description, sceneObject.getParent(), sceneObject.getStudio());
		this.sceneObject = sceneObject;
		this.surfaceProperty = surfaceProperty;
	}
	
	public WrappedSceneObject(
			SceneObject sceneObject,
			SurfaceProperty surfaceProperty
			)
	{
		this(
				sceneObject.getDescription() + " (wrapped)",
				sceneObject,
				surfaceProperty
			);
	}


	public WrappedSceneObject(WrappedSceneObject original) {
		super(original);
		this.sceneObject = original.getSceneObject();
		this.surfaceProperty = original.getSurfaceProperty();
	}

	@Override
	public WrappedSceneObject clone() {
		return new WrappedSceneObject(this);
	}

	
	// getters & setters

	public SceneObject getSceneObject() {
		return sceneObject;
	}

	public void setSceneObject(SceneObject sceneObject) {
		this.sceneObject = sceneObject;
	}
	
	public SurfaceProperty getSurfaceProperty() {
		return surfaceProperty;
	}

	public void setSurfaceProperty(SurfaceProperty surfaceProperty) {
		this.surfaceProperty = surfaceProperty;
	}


	
	// overridden SceneObjectClass methods (i.e. the interesting bit)
	
	/**
	 * For a specific intersection point, calculate the corresponding colour
	 * @param intersection
	 * @param ray
	 * @param l
	 * @param scene
	 * @param traceLevel
	 * @return the colour
	 * @throws RayTraceException
	 */
	protected DoubleColour getColourAtIntersection(RaySceneObjectIntersection intersection, Ray ray, LightSource l, SceneObject scene, int traceLevel, RaytraceExceptionHandler raytraceExceptionHandler)
	throws RayTraceException
	{
		// if there is no intersection...
		if (intersection == RaySceneObjectIntersection.NO_INTERSECTION)
		{
			// ... let the RaytraceExceptionHandler handle it
			return raytraceExceptionHandler.getColourOfRayFromNowhere(ray, intersection.o, l, scene, traceLevel);
		}

		// just in case: do something if, for some reason, the object is null
		if (intersection.o==null)
		{
			throw new RayTraceException("Unexpected null scene object.");
		}

		if(ray.isRayWithTrajectory()) ((RayWithTrajectory)ray).addIntersectionPoint(intersection.p);
		
		// different here:  return the colour as calculated by the "wrapped-around" surface property
		return surfaceProperty.getColour(ray, intersection, scene, l, traceLevel-1, raytraceExceptionHandler);
		// return intersection.o.getColourAtIntersection(ray, intersection, scene, l, traceLevel-1, raytraceExceptionHandler);
	}

	
	// other SceneObjectClass methods

	@Override
	public String getType() {
		return "Scene-object surface wrapper";
	}

	@Override
	public RaySceneObjectIntersection getClosestRayIntersection(Ray ray) {
		return sceneObject.getClosestRayIntersection(ray);
	}

	@Override
	public boolean insideObject(Vector3D p) {
		return sceneObject.insideObject(p);
	}

	@Override
	public WrappedSceneObject transform(Transformation t) {
		return new WrappedSceneObject(
				sceneObject.getDescription() + ", transformed",	// description
				sceneObject.transform(t),	// sceneObject
				surfaceProperty	// surfaceProperty -- does this need to be transformed?
				);
	}

	@Override
	public RaySceneObjectIntersection getClosestRayIntersectionWithShadowThrowingSceneObject(Ray ray) {
		return sceneObject.getClosestRayIntersectionWithShadowThrowingSceneObject(ray);
	}

	@Override
	public RaySceneObjectIntersection getClosestRayIntersectionAvoidingOrigin(Ray ray,
			SceneObjectPrimitive originObject) {
		return sceneObject.getClosestRayIntersectionAvoidingOrigin(ray, originObject);
	}

	@Override
	public RaySceneObjectIntersection getClosestRayIntersectionWithShadowThrowingSceneObjectAvoidingOrigin(Ray ray,
			SceneObjectPrimitive originObject) {
		return sceneObject.getClosestRayIntersectionWithShadowThrowingSceneObjectAvoidingOrigin(ray, originObject);
	}

	@Override
	public ArrayList<SceneObjectPrimitive> getSceneObjectPrimitives() {
		return sceneObject.getSceneObjectPrimitives();
	}


}
