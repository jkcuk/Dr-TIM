package optics.raytrace.sceneObjects.solidGeometry;

import java.io.Serializable;
import java.util.ArrayList;

import optics.raytrace.core.Ray;
import optics.raytrace.core.RaySceneObjectIntersection;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.SceneObjectClass;
import optics.raytrace.core.SceneObjectPrimitive;
import optics.raytrace.core.Studio;
import optics.raytrace.core.Transformation;
import math.Vector3D;

/**
 * An intersection of scene objects that acts as a combined scene object.
 * 
 * The combined scene object is the intersection of all scene objects in the list
 * <positiveSceneObjects> --- the "positive" scene objects ---, and all scene objects
 * in the list <negativeSceneObjects> --- the "negative" scene objects.
 * This means that for a point to lie inside the combined scene object it has to lie inside every single positive
 * scene object and outside every single negative scene object.
 * 
 * For a point on the surface of scene object A, which is either one of the positive or negative
 * scene objects (it doesn't make sense for it to be both), that point must lie inside all positive
 * scene objects other than A and outside all negative scene objects other than A.
 * 
 * @author Johannes Courtial
 */ 
public class SceneObjectIntersection2 extends SceneObjectClass
implements Serializable
{
	private static final long serialVersionUID = -4276323488622984454L;

	/**
	 * list of the positive scene objects
	 * (for a point to lie inside the combined scene object, it has to lie inside every single positive scene object)
	 */
	protected ArrayList<SceneObject> positiveSceneObjects;

	/**
	 * list of the negative scene objects
	 * (for a point to lie inside the combined scene object, it has to lie outside every single negative scene object)
	 */
	protected ArrayList<SceneObject> negativeSceneObjects;

	/**
	 * Constructor that sets all internal parameters
	 * @param description
	 * @param positiveSceneObjects
	 * @param negativeSceneObjects
	 * @param parent
	 * @param studio
	 */
	public SceneObjectIntersection2(
			String description,
			ArrayList<SceneObject> positiveSceneObjects,
			ArrayList<SceneObject> negativeSceneObjects,
			SceneObject parent,
			Studio studio
		)
	{
		super(description, parent, studio);
		
		// initialise the lists of positive and negative scene objects
		this.positiveSceneObjects = new ArrayList<SceneObject>();
		this.negativeSceneObjects  = new ArrayList<SceneObject>();

		if(positiveSceneObjects != null) addPositiveSceneObjects(positiveSceneObjects);
		if(negativeSceneObjects != null) addNegativeSceneObjects(negativeSceneObjects);
	}

	/**
	 * Create an empty scene-object combination.
	 * Scene objects can be added later using the addPositiveSceneObject and addNegativeSceneObject methods.
	 * 
	 * @param description
	 */
	public SceneObjectIntersection2(String description, SceneObject parent, Studio studio)
	{
		this(description, null, null, parent, studio);
	}
	
	/**
	 * Makes a copy of the original SceneObjectCombination.
	 * Note that the positive and negative scene objects in the copy are the same as those in the original.
	 * (I.e. they are not clones.)
	 * 
	 * @param original
	 */
	public SceneObjectIntersection2(SceneObjectIntersection2 original)
	{
		this(
				original.getDescription(),
				original.getPositiveSceneObjects(),
				original.getNegativeSceneObjects(),
				original.getParent(),
				original.getStudio()
			);
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.core.SceneObjectClass#clone()
	 */
	@Override
	public SceneObjectIntersection2 clone()
	{
		return new SceneObjectIntersection2(this);
	}
	
	
	//
	// setters & getters
	//
	
	/**
	 * @return	the list of the positive scene objects
	 */
	public ArrayList<SceneObject> getPositiveSceneObjects() {
		return positiveSceneObjects;
	}

	/**
	 * Add the SceneObjects in <sceneObjectPrimitives> to the list of positive scene objects.
	 * 
	 * @param sceneObjectPrimitives
	 */
	public void addPositiveSceneObjects(ArrayList<SceneObject> sceneObjectPrimitives)
	{
		for(SceneObject sceneObjectPrimitive: sceneObjectPrimitives)
		{
			addPositiveSceneObject(sceneObjectPrimitive);
		}
	}
	
	/**
	 * Add <sceneObjectPrimitive> to the list of positive scene objects.
	 * 
	 * @param sceneObjectPrimitive
	 */
	public void addPositiveSceneObject(SceneObject sceneObjectPrimitive)
	{
		// make sure the scene object has this as parent...
		sceneObjectPrimitive.setParent(this);
		// ... and this's studio as studio
		sceneObjectPrimitive.setStudio(getStudio());

		// add it to the list of positive scene objects
		positiveSceneObjects.add(sceneObjectPrimitive);
	}

	/**
	 * @return	the list of negative scene objects
	 */
	public ArrayList<SceneObject> getNegativeSceneObjects() {
		return negativeSceneObjects;
	}

	/**
	 * Add the SceneObjects in <sceneObjectPrimitives> to the list of negative scene objects.
	 * 
	 * @param sceneObjectPrimitives
	 */
	public void addNegativeSceneObjects(ArrayList<SceneObject> sceneObjectPrimitives)
	{
		for(SceneObject sceneObjectPrimitive: sceneObjectPrimitives)
		{
			addNegativeSceneObject(sceneObjectPrimitive);
		}
	}
	
	/**
	 * Add <sceneObjectPrimitive> to the list of negative scene objects.
	 * 
	 * @param sceneObjectPrimitive
	 */
	public void addNegativeSceneObject(SceneObject sceneObjectPrimitive)
	{
		// make sure the scene object has this as parent...
		sceneObjectPrimitive.setParent(this);
		// ... and this's studio as studio
		sceneObjectPrimitive.setStudio(getStudio());

		// add it to the list of negative scene objects
		negativeSceneObjects.add(sceneObjectPrimitive);
	}
	
	/**
	 * clear all positive and negative scene objects contained in this combination
	 */
	public void clear()
	{
		positiveSceneObjects.clear();
		negativeSceneObjects.clear();
	}

	
	/**
	 * @param raySceneObjectIntersection
	 * @return	true if the position associated with the ray-scene-object intersection lies inside all positive scene objects other than the one involved in the intersection
	 */
	private boolean insideAllPositiveSceneObjects(RaySceneObjectIntersection raySceneObjectIntersection)
	{
		// let <sceneObjectPrimitive> be in turn every one of the positive scene objects
		for(SceneObject sceneObjectPrimitive:positiveSceneObjects)
		{
			// is <sceneObjectPrimitive> the scene object that was involved in the ray-scene-object intersection?
			// The one that was involved in the ray-scene-object intersection should not be checked, as the intersection
			// lies on its surface, and therefore lies neither inside nor outside; the question of whether or not the
			// intersection lies inside the object therefore returns just numerical noise
			if(sceneObjectPrimitive != raySceneObjectIntersection.o)
			{
				// no, it is a different scene object
				
				// is the position of the ray-scene-object intersection inside sceneObjectPrimitive?
				if(!sceneObjectPrimitive.insideObject(raySceneObjectIntersection.p))
				{
					// no, it's outside
					
					// the raySceneObjectIntersection clearly does not lie inside this particular scene object,
					// and therefore it is not inside all positive scene objects
					return false;
				}
			}
		}
		
		// the raySceneObjectIntersection lies inside all positive scene objects (other than the one that was involved in the intersection)
		return true;
	}
	
	/**
	 * @param raySceneObjectIntersection
	 * @return	true if the position associated with the ray-scene-object intersection lies outside all negative scene objects other than the one involved in the intersection
	 */
	private boolean outsideAllNegativeSceneObjects(RaySceneObjectIntersection raySceneObjectIntersection)
	{
		// let <sceneObjectPrimitive> be in turn every one of the negative scene objects
		for(SceneObject sceneObjectPrimitive:negativeSceneObjects)
		{
			// is <sceneObjectPrimitive> the scene object that was involved in the ray-scene-object intersection?
			// The one that was involved in the ray-scene-object intersection should not be checked, as the intersection
			// lies on its surface, and therefore lies neither inside nor outside; the question of whether or not the
			// intersection lies inside the object therefore returns just numerical noise
			if(sceneObjectPrimitive != raySceneObjectIntersection.o)
			{
				// no, it is a different scene object
				
				// is the position of the ray-scene-object intersection outside sceneObjectPrimitive?
				if(sceneObjectPrimitive.insideObject(raySceneObjectIntersection.p))
				{
					// no, it's inside
					
					// the raySceneObjectIntersection clearly does not lie outside this particular scene object,
					// and therefore it is not outside all negative scene objects
					return false;
				}
			}
		}
		
		// the raySceneObjectIntersection lies outside all negative scene objects (other than the one that was involved in the intersection)
		return true;
	}
	
	private class IntersectionAndDistance
	{
		public RaySceneObjectIntersection intersection;
		public double distance2;
		
		public IntersectionAndDistance()
		{
			intersection = RaySceneObjectIntersection.NO_INTERSECTION;
			distance2 = Double.POSITIVE_INFINITY;
		}
	}
	
	private void lookForBetterIntersection(
			IntersectionAndDistance currentlyBestIntersection,
			SceneObject sceneObject,
			Ray ray,
			SceneObjectPrimitive excludeObject,
			boolean shadowThrowingObjectsOnly
		)
	{
		// calculate the intersection
		RaySceneObjectIntersection intersection = 
				shadowThrowingObjectsOnly
				?sceneObject.getClosestRayIntersectionWithShadowThrowingSceneObjectAvoidingOrigin(ray, excludeObject)
				:sceneObject.getClosestRayIntersectionAvoidingOrigin(ray, excludeObject);

		// is there an intersection point?
		while(intersection != RaySceneObjectIntersection.NO_INTERSECTION)
		{
			// yes

			// check that the intersection point is on the surface, i.e. inside all positive scene objects other than the
			// one involved in the intersection and outside all negative scene objects other then the one involved in the
			// intersection
			if(insideAllPositiveSceneObjects(intersection) && outsideAllNegativeSceneObjects(intersection))
			{
				// yes

				// calculate the distance (squared) to the intersection point
				double distance2 = intersection.p.getDifferenceWith(ray.getP()).getModSquared();
				
				// is the distance to this intersection point smaller than the smallest one so far?
				if(distance2 < currentlyBestIntersection.distance2)
				{
					// yes, so make the new intersection point the closest one so far
					currentlyBestIntersection.distance2 = distance2;
					currentlyBestIntersection.intersection = intersection;
				}
				
				// don't look for further intersections with this object
				intersection = RaySceneObjectIntersection.NO_INTERSECTION;
			}
			else
			{
				// try the next intersection with the same object
				intersection = 
						shadowThrowingObjectsOnly
						?sceneObject.getNextClosestRayIntersectionWithShadowThrowingSceneObjectAvoidingOrigin(ray, excludeObject, intersection)
						:sceneObject.getNextClosestRayIntersectionAvoidingOrigin(ray, excludeObject, intersection);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see optics.raytrace.SceneObjectContainer#getClosestRayIntersectionAvoidingOrigin(optics.raytrace.Ray, optics.raytrace.SceneObject)
	 */
	@Override
	public RaySceneObjectIntersection getClosestRayIntersectionAvoidingOrigin(Ray ray, SceneObjectPrimitive excludeObject)
	{
		// initialise the information about the closest intersection so far
		IntersectionAndDistance closestIntersectionAndDistance = new IntersectionAndDistance();
		
		// go through the positive...
		for(SceneObject sceneObjectPrimitive:positiveSceneObjects) 
			lookForBetterIntersection(closestIntersectionAndDistance, sceneObjectPrimitive, ray, excludeObject, false);
		
		// ... and negative scene objects
		for(SceneObject sceneObjectPrimitive:negativeSceneObjects) 
			lookForBetterIntersection(closestIntersectionAndDistance, sceneObjectPrimitive, ray, excludeObject, false);

		return closestIntersectionAndDistance.intersection;    
	}
	
	/* (non-Javadoc)
	 * @see optics.raytrace.SceneObjectContainer#getClosestRayIntersectionAvoidingOrigin(optics.raytrace.Ray, optics.raytrace.SceneObject)
	 */
	@Override
	public RaySceneObjectIntersection getClosestRayIntersectionWithShadowThrowingSceneObjectAvoidingOrigin(Ray ray, SceneObjectPrimitive excludeObject)
	{
		// initialise the information about the closest intersection so far
		IntersectionAndDistance closestIntersectionAndDistance = new IntersectionAndDistance();
		
		// go through the shadow-throwing constructive...
		for(SceneObject sceneObject:positiveSceneObjects)
			lookForBetterIntersection(closestIntersectionAndDistance, sceneObject, ray, excludeObject, true);
		
		// ... and destructive scene objects
		for(SceneObject sceneObjectPrimitive:negativeSceneObjects) 
			lookForBetterIntersection(closestIntersectionAndDistance, sceneObjectPrimitive, ray, excludeObject, true);

		return closestIntersectionAndDistance.intersection;    
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.SceneObjectContainer#transform(optics.raytrace.Transformation)
	 */
	@Override
	public SceneObjectIntersection2 transform(Transformation t)
	{
		SceneObjectIntersection2 transformed = new SceneObjectIntersection2(description + "(transformed)", getParent(), getStudio());
		
		// add the transformed "in"...
		for(SceneObject sceneObjectPrimitive:positiveSceneObjects)
			addPositiveSceneObject(sceneObjectPrimitive.transform(t));
		
		// ... and "out" scene objects
		for(SceneObject sceneObjectPrimitive:negativeSceneObjects)
			addNegativeSceneObject(sceneObjectPrimitive.transform(t));
		
		return transformed;
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.SceneObjectContainer#insideObject(optics.raytrace.Vector3D)
	 */
	@Override
	public boolean insideObject(Vector3D p)
	{
		// create a ray-scene-object intersection at the given position vector;
		// set intersecting SceneObject to null
		RaySceneObjectIntersection raySceneObjectIntersection = new RaySceneObjectIntersection(p, null, 0);
		
		// p has to be inside all "in" scene objects and outside all "out" scene objects
		return insideAllPositiveSceneObjects(raySceneObjectIntersection) && outsideAllNegativeSceneObjects(raySceneObjectIntersection);
	}

	@Override
	public RaySceneObjectIntersection getClosestRayIntersection(Ray ray) {
		return getClosestRayIntersectionAvoidingOrigin(ray, (SceneObjectPrimitive)null);
	}

	@Override
	public RaySceneObjectIntersection getClosestRayIntersectionWithShadowThrowingSceneObject(Ray ray)
	{
		return getClosestRayIntersectionWithShadowThrowingSceneObjectAvoidingOrigin(ray, (SceneObjectPrimitive)null);
	}

	@Override
	public ArrayList<SceneObjectPrimitive> getSceneObjectPrimitives()
	{
		ArrayList<SceneObjectPrimitive> SOPs = new ArrayList<SceneObjectPrimitive>();
		
		for(SceneObject sceneObject:positiveSceneObjects) SOPs.addAll(sceneObject.getSceneObjectPrimitives());
		for(SceneObject sceneObject:negativeSceneObjects) SOPs.addAll(sceneObject.getSceneObjectPrimitives());

		return SOPs;
	}
	
	@Override
	public String getType()
	{
		return "Intersection";
	}
}
