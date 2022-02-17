package optics.raytrace.voxellations;

import java.io.Serializable;
import java.util.ArrayList;

import math.Vector3D;
import optics.DoubleColour;
import optics.raytrace.core.LightSource;
import optics.raytrace.core.Ray;
import optics.raytrace.core.RaySceneObjectIntersection;
import optics.raytrace.core.RaytraceExceptionHandler;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.SceneObjectPrimitive;
import optics.raytrace.core.Studio;
import optics.raytrace.core.Transformation;
import optics.raytrace.exceptions.RayTraceException;

/**
 * A wrapper for a scene object that allows it to store the voxellation and details of the surface within the voxellation
 * it represents.
 * 
 * @author johannes
 *
 */
public class SceneObjectPartOfVoxellation implements SceneObject, Serializable, Cloneable {
	private static final long serialVersionUID = 3258425451359397210L;

	/**
	 * the "wrapped" scene object
	 */
	private SceneObject sceneObject;
	
	/**
	 * the voxellation this surface is part of
	 */
	private Voxellation partOf;
	
	/**
	 * surface index
	 */
	private int surfaceIndex;
	

	public SceneObjectPartOfVoxellation(SceneObject sceneObject, Voxellation partOf, int surfaceIndex)
	{
		this.sceneObject = sceneObject;
		this.partOf = partOf;
		this.surfaceIndex = surfaceIndex;
	}
	

	//
	// getters & setters
	//

	public SceneObject getSceneObject() {
		return sceneObject;
	}

	public void setSceneObject(SceneObject sceneObject) {
		this.sceneObject = sceneObject;
	}

	public Voxellation getPartOf() {
		return partOf;
	}

	public void setPartOf(Voxellation partOf) {
		this.partOf = partOf;
	}

	public int getSurfaceIndex() {
		return surfaceIndex;
	}

	public void setSurfaceIndex(int surfaceIndex) {
		this.surfaceIndex = surfaceIndex;
	}



	
	//
	// SceneObject methods
	//



	@Override
	public String getType() {
		return sceneObject.getType();
	}

	@Override
	public String getDescription() {
		return sceneObject.getDescription();
	}

	@Override
	public void setDescription(String description) {
		sceneObject.setDescription(description);
	}

	@Override
	public RaySceneObjectIntersection getClosestRayIntersection(Ray ray) {
		return sceneObject.getClosestRayIntersection(ray);
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
	public RaySceneObjectIntersection getNextClosestRayIntersection(Ray ray, RaySceneObjectIntersection i) {
		return sceneObject.getNextClosestRayIntersection(ray, i);
	}

	@Override
	public RaySceneObjectIntersection getNextClosestRayIntersectionWithShadowThrowingSceneObject(Ray ray,
			RaySceneObjectIntersection i) {
		return sceneObject.getNextClosestRayIntersectionWithShadowThrowingSceneObject(ray, i);
	}

	@Override
	public RaySceneObjectIntersection getNextClosestRayIntersectionAvoidingOrigin(Ray ray,
			SceneObjectPrimitive originObject, RaySceneObjectIntersection i) {
		return sceneObject.getNextClosestRayIntersectionAvoidingOrigin(ray, originObject, i);
	}

	@Override
	public RaySceneObjectIntersection getNextClosestRayIntersectionWithShadowThrowingSceneObjectAvoidingOrigin(Ray ray,
			SceneObjectPrimitive originObject, RaySceneObjectIntersection i) {
		return sceneObject.getNextClosestRayIntersectionWithShadowThrowingSceneObjectAvoidingOrigin(ray, originObject, i);
	}

	@Override
	public SceneObject transform(Transformation t) {
		sceneObject = sceneObject.transform(t);	// TODO is this correct? I am transforming *this* project
		return this;
	}

	@Override
	public boolean insideObject(Vector3D p) {
		return sceneObject.insideObject(p);
	}

	@Override
	public DoubleColour getColour(Ray ray, LightSource l, SceneObject scene, int traceLevel,
			RaytraceExceptionHandler raytraceExceptionHandler) throws RayTraceException {
		return sceneObject.getColour(ray, l, scene, traceLevel, raytraceExceptionHandler);
	}

	@Override
	public DoubleColour getColourAvoidingOrigin(Ray ray, SceneObjectPrimitive originObject, LightSource l,
			SceneObject scene, int traceLevel, RaytraceExceptionHandler raytraceExceptionHandler)
			throws RayTraceException {
		return sceneObject.getColourAvoidingOrigin(ray, originObject, l, scene, traceLevel, raytraceExceptionHandler);
	}

	/**
	 * creates a new wrapper, but everything inside is the same!
	 */
	@Override
	public SceneObject clone() {
		return new SceneObjectPartOfVoxellation(sceneObject, partOf, surfaceIndex);
	}

	@Override
	public ArrayList<SceneObjectPrimitive> getSceneObjectPrimitives() {
		return sceneObject.getSceneObjectPrimitives();
	}

	@Override
	public Studio getStudio() {
		return sceneObject.getStudio();
	}

	@Override
	public void setStudio(Studio studio) {
		sceneObject.setStudio(studio);
	}

	@Override
	public SceneObject getParent() {
		return sceneObject.getParent();
	}

	@Override
	public void setParent(SceneObject parent) {
		sceneObject.setParent(parent);		
	}

}
