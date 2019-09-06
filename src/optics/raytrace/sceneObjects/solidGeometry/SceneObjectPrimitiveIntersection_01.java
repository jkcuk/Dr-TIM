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
 * An intersection of scene-object primitives that acts as a combined scene object.
 * 
 * The combined scene object is the intersection of all scene-object primitives in the list
 * <positiveSceneObjectPrimitives> --- the "positive" scene-object primitives ---, and all scene-object primitives
 * in the list <negativeSceneObjectPrimitives> --- the "negative" scene-object primitives.
 * This means that for a point to lie inside the combined scene object it has to lie inside every single positive
 * scene-object primitive and outside every single negative scene-object primitive.
 * 
 * For a point on the surface of scene-object primitive A, which is either one of the positive or negative
 * scene-object primitives (it doesn't make sense for it to be both), that point must lie inside all positive
 * scene-object primitives other than A and outside all negative scene-object primitives other than A.
 * 
 * @author Johannes Courtial
 */ 
public class SceneObjectPrimitiveIntersection_01 extends SceneObjectClass
implements Serializable
{
	private static final long serialVersionUID = -4276323488622984454L;

	/**
	 * list of the positive scene-object primitives
	 * (for a point to lie inside the combined scene object, it has to lie inside every single positive scene-object primitive)
	 */
	protected ArrayList<SceneObjectPrimitive> positiveSceneObjectPrimitives;

	/**
	 * list of the negative scene-object primitives
	 * (for a point to lie inside the combined scene object, it has to lie outside every single negative scene-object primitive)
	 */
	protected ArrayList<SceneObjectPrimitive> negativeSceneObjectPrimitives;

	/**
	 * Constructor that sets all internal parameters
	 * @param description
	 * @param positiveSceneObjectPrimitives
	 * @param negativeSceneObjectPrimitives
	 * @param parent
	 * @param studio
	 */
	public SceneObjectPrimitiveIntersection_01(
			String description,
			ArrayList<SceneObjectPrimitive> positiveSceneObjectPrimitives,
			ArrayList<SceneObjectPrimitive> negativeSceneObjectPrimitives,
			SceneObject parent,
			Studio studio
		)
	{
		super(description, parent, studio);
		
		// initialise the lists of positive and negative scene-object primitives
		this.positiveSceneObjectPrimitives = new ArrayList<SceneObjectPrimitive>();
		this.negativeSceneObjectPrimitives  = new ArrayList<SceneObjectPrimitive>();

		if(positiveSceneObjectPrimitives != null) addPositiveSceneObjectPrimitives(positiveSceneObjectPrimitives);
		if(negativeSceneObjectPrimitives != null) addNegativeSceneObjectPrimitives(negativeSceneObjectPrimitives);
	}

	/**
	 * Create an empty scene-object combination.
	 * Scene objects can be added later using the addPositiveSceneObjectPrimitive and addNegativeSceneObjectPrimitive methods.
	 * 
	 * @param description
	 */
	public SceneObjectPrimitiveIntersection_01(String description, SceneObject parent, Studio studio)
	{
		this(description, null, null, parent, studio);
	}
	
	/**
	 * Makes a copy of the original SceneObjectCombination.
	 * Note that the positive and negative scene-object primitives in the copy are the same as those in the original.
	 * (I.e. they are not clones.)
	 * 
	 * @param original
	 */
	public SceneObjectPrimitiveIntersection_01(SceneObjectPrimitiveIntersection_01 original)
	{
		this(
				original.getDescription(),
				original.getPositiveSceneObjectPrimitives(),
				original.getNegativeSceneObjectPrimitives(),
				original.getParent(),
				original.getStudio()
			);
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.core.SceneObjectClass#clone()
	 */
	@Override
	public SceneObjectPrimitiveIntersection_01 clone()
	{
		return new SceneObjectPrimitiveIntersection_01(this);
	}
	
	
	//
	// setters & getters
	//
	
	/**
	 * @return	the list of the positive scene-object primitives
	 */
	public ArrayList<SceneObjectPrimitive> getPositiveSceneObjectPrimitives() {
		return positiveSceneObjectPrimitives;
	}

	/**
	 * Add the SceneObjectPrimitives in <sceneObjectPrimitives> to the list of positive scene-object primitives.
	 * 
	 * @param sceneObjectPrimitives
	 */
	public void addPositiveSceneObjectPrimitives(ArrayList<SceneObjectPrimitive> sceneObjectPrimitives)
	{
		for(SceneObjectPrimitive sceneObjectPrimitive: sceneObjectPrimitives)
		{
			addPositiveSceneObjectPrimitive(sceneObjectPrimitive);
		}
	}
	
	/**
	 * Add <sceneObjectPrimitive> to the list of positive scene-object primitives.
	 * 
	 * @param sceneObjectPrimitive
	 */
	public void addPositiveSceneObjectPrimitive(SceneObjectPrimitive sceneObjectPrimitive)
	{
		// make sure the scene object has this as parent...
		sceneObjectPrimitive.setParent(this);
		// ... and this's studio as studio
		sceneObjectPrimitive.setStudio(getStudio());

		// add it to the list of positive scene-object primitives
		positiveSceneObjectPrimitives.add(sceneObjectPrimitive);
	}

	/**
	 * @return	the list of negative scene-object primitives
	 */
	public ArrayList<SceneObjectPrimitive> getNegativeSceneObjectPrimitives() {
		return negativeSceneObjectPrimitives;
	}

	/**
	 * Add the SceneObjectPrimitives in <sceneObjectPrimitives> to the list of negative scene-object primitives.
	 * 
	 * @param sceneObjectPrimitives
	 */
	public void addNegativeSceneObjectPrimitives(ArrayList<SceneObjectPrimitive> sceneObjectPrimitives)
	{
		for(SceneObjectPrimitive sceneObjectPrimitive: sceneObjectPrimitives)
		{
			addNegativeSceneObjectPrimitive(sceneObjectPrimitive);
		}
	}
	
	/**
	 * Add <sceneObjectPrimitive> to the list of negative scene-object primitives.
	 * 
	 * @param sceneObjectPrimitive
	 */
	public void addNegativeSceneObjectPrimitive(SceneObjectPrimitive sceneObjectPrimitive)
	{
		// make sure the scene object has this as parent...
		sceneObjectPrimitive.setParent(this);
		// ... and this's studio as studio
		sceneObjectPrimitive.setStudio(getStudio());

		// add it to the list of negative scene-object primitives
		negativeSceneObjectPrimitives.add(sceneObjectPrimitive);
	}
	
	/**
	 * clear all positive and negative scene-object primitives contained in this combination
	 */
	public void clear()
	{
		positiveSceneObjectPrimitives.clear();
		negativeSceneObjectPrimitives.clear();
	}

	
	/**
	 * @param raySceneObjectIntersection
	 * @return	true if the position associated with the ray-scene-object intersection lies inside all positive scene-object primitives other than the one involved in the intersection
	 */
	private boolean insideAllPositiveSceneObjectPrimitives(RaySceneObjectIntersection raySceneObjectIntersection)
	{
		// let <sceneObjectPrimitive> be in turn every one of the positive scene-object primitives
		for(SceneObjectPrimitive sceneObjectPrimitive:positiveSceneObjectPrimitives)
		{
			// is <sceneObjectPrimitive> the scene-object primitive that was involved in the ray-scene-object intersection?
			// The one that was involved in the ray-scene-object intersection should not be checked, as the intersection
			// lies on its surface, and therefore lies neither inside nor outside; the question of whether or not the
			// intersection lies inside the object therefore returns just numerical noise
			if(sceneObjectPrimitive != raySceneObjectIntersection.o)
			{
				// no, it is a different scene-object primitive
				
				// is the position of the ray-scene-object intersection inside sceneObjectPrimitive?
				if(!sceneObjectPrimitive.insideObject(raySceneObjectIntersection.p))
				{
					// no, it's outside
					
					// the raySceneObjectIntersection clearly does not lie inside this particular scene-object primitive,
					// and therefore it is not inside all positive scene-object primitives
					return false;
				}
			}
		}
		
		// the raySceneObjectIntersection lies inside all positive scene-object primitives (other than the one that was involved in the intersection)
		return true;
	}
	
	/**
	 * @param raySceneObjectIntersection
	 * @return	true if the position associated with the ray-scene-object intersection lies outside all negative scene-object primitives other than the one involved in the intersection
	 */
	private boolean outsideAllNegativeSceneObjectPrimitives(RaySceneObjectIntersection raySceneObjectIntersection)
	{
		// let <sceneObjectPrimitive> be in turn every one of the negative scene-object primitives
		for(SceneObjectPrimitive sceneObjectPrimitive:negativeSceneObjectPrimitives)
		{
			// is <sceneObjectPrimitive> the scene-object primitive that was involved in the ray-scene-object intersection?
			// The one that was involved in the ray-scene-object intersection should not be checked, as the intersection
			// lies on its surface, and therefore lies neither inside nor outside; the question of whether or not the
			// intersection lies inside the object therefore returns just numerical noise
			if(sceneObjectPrimitive != raySceneObjectIntersection.o)
			{
				// no, it is a different scene-object primitive
				
				// is the position of the ray-scene-object intersection outside sceneObjectPrimitive?
				if(sceneObjectPrimitive.insideObject(raySceneObjectIntersection.p))
				{
					// no, it's inside
					
					// the raySceneObjectIntersection clearly does not lie outside this particular scene-object primitive,
					// and therefore it is not outside all negative scene-object primitives
					return false;
				}
			}
		}
		
		// the raySceneObjectIntersection lies outside all negative scene-object primitives (other than the one that was involved in the intersection)
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
			SceneObjectPrimitive sceneObjectPrimitive,
			Ray ray,
			SceneObjectPrimitive excludeObject
		)
	{
		// calculate the intersection
		RaySceneObjectIntersection intersection = sceneObjectPrimitive.getClosestRayIntersectionAvoidingOrigin(ray, excludeObject);

		// is there an intersection point?
		while(intersection != RaySceneObjectIntersection.NO_INTERSECTION)
		{
			// yes

			// check that the intersection point is on the surface, i.e. inside all positive scene-object primitives other than the
			// one involved in the intersection and outside all negative scene-object primitives other then the one involved in the
			// intersection
			if(insideAllPositiveSceneObjectPrimitives(intersection) && outsideAllNegativeSceneObjectPrimitives(intersection))
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
				intersection = sceneObjectPrimitive.getNextClosestRayIntersectionAvoidingOrigin(ray, excludeObject, intersection);
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
		for(SceneObjectPrimitive sceneObjectPrimitive:positiveSceneObjectPrimitives) 
			lookForBetterIntersection(closestIntersectionAndDistance, sceneObjectPrimitive, ray, excludeObject);
		
		// ... and negative scene-object primitives
		for(SceneObjectPrimitive sceneObjectPrimitive:negativeSceneObjectPrimitives) 
			lookForBetterIntersection(closestIntersectionAndDistance, sceneObjectPrimitive, ray, excludeObject);

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
		for(SceneObjectPrimitive sceneObjectPrimitive:positiveSceneObjectPrimitives)
			if(sceneObjectPrimitive.isShadowThrowing())
				lookForBetterIntersection(closestIntersectionAndDistance, sceneObjectPrimitive, ray, excludeObject);
		
		// ... and destructive scene-object primitives
		for(SceneObjectPrimitive sceneObjectPrimitive:negativeSceneObjectPrimitives) 
			if(sceneObjectPrimitive.isShadowThrowing())
				lookForBetterIntersection(closestIntersectionAndDistance, sceneObjectPrimitive, ray, excludeObject);

		return closestIntersectionAndDistance.intersection;    
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.SceneObjectContainer#transform(optics.raytrace.Transformation)
	 */
	@Override
	public SceneObjectPrimitiveIntersection_01 transform(Transformation t)
	{
		SceneObjectPrimitiveIntersection_01 transformed = new SceneObjectPrimitiveIntersection_01(description + "(transformed)", getParent(), getStudio());
		
		// add the transformed "in"...
		for(SceneObjectPrimitive sceneObjectPrimitive:positiveSceneObjectPrimitives)
			addPositiveSceneObjectPrimitive(sceneObjectPrimitive.transform(t));
		
		// ... and "out" scene-object primitives
		for(SceneObjectPrimitive sceneObjectPrimitive:negativeSceneObjectPrimitives)
			addNegativeSceneObjectPrimitive(sceneObjectPrimitive.transform(t));
		
		return transformed;
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.SceneObjectContainer#insideObject(optics.raytrace.Vector3D)
	 */
	@Override
	public boolean insideObject(Vector3D p)
	{
		// create a ray-scene-object intersection at the given position vector;
		// set intersecting SceneObjectPrimitive to null
		RaySceneObjectIntersection raySceneObjectIntersection = new RaySceneObjectIntersection(p, null, 0);
		
		// p has to be inside all "in" scene-object primitives and outside all "out" scene-object primitives
		return insideAllPositiveSceneObjectPrimitives(raySceneObjectIntersection) && outsideAllNegativeSceneObjectPrimitives(raySceneObjectIntersection);
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
		
		SOPs.addAll(positiveSceneObjectPrimitives);
		SOPs.addAll(negativeSceneObjectPrimitives);

		return SOPs;
	}
	
	@Override
	public String getType()
	{
		return "Intersection";
	}
}
