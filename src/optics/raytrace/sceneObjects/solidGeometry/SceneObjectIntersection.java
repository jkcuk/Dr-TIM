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
 * A modification of SceneObjectPrimitiveIntersection.
 * 
 * The combined scene object is the intersection of all scene objects in the lists
 * <positiveSceneObjects> and <invisiblePositiveSceneObjects> --- the "positive" scene objects ---, and the inverse of all scene objects
 * in the lists <negativeSceneObjects> and <invisibleNegativeSceneObjects> --- the "negative" scene objects.
 * This means that for a point to lie inside the combined scene object it has to lie inside every single positive
 * scene object and outside every single negative scene object.
 * 
 * For a point on the surface of scene object A, which is either one of the positive or negative
 * scene objects (it doesn't make sense for it to be both), that point must lie inside all positive
 * scene objects other than A and outside all negative scene objects other than A.
 * 
 * @author Johannes Courtial
 */ 
public class SceneObjectIntersection extends SceneObjectClass
implements Serializable
{
	private static final long serialVersionUID = -1432359320339156645L;

	/**
	 * list of the visible positive scene objects
	 * (for a point to lie inside the combined scene object, it has to lie inside every single (visible or invisible) positive scene object)
	 */
	protected ArrayList<SceneObject> positiveSceneObjects;

	/**
	 * list of the visible negative scene objects
	 * (for a point to lie inside the combined scene object, it has to lie outside every single (visible or invisible) negative scene object)
	 */
	protected ArrayList<SceneObject> negativeSceneObjects;

	/**
	 * list of the invisible positive scene objects
	 * (for a point to lie inside the combined scene object, it has to lie inside every single (visible or invisible) positive scene object)
	 */
	protected ArrayList<SceneObject> invisiblePositiveSceneObjects;

	/**
	 * list of the invisible negative scene objects
	 * (for a point to lie inside the combined scene object, it has to lie outside every single (visible or invisible) negative scene object)
	 */
	protected ArrayList<SceneObject> invisibleNegativeSceneObjects;

	/**
	 * list of scene objects that are clipped by the (visible or invisible) positive and negative scene objects, but which do not themselves clip the other scene objects
	 */
	protected ArrayList<SceneObject> clippedSceneObjects;

	/**
	 * Constructor that sets all internal parameters
	 * @param description
	 * @param positiveSceneObjects
	 * @param negativeSceneObjects
	 * @param invisiblePositiveSceneObjects
	 * @param invisibleNegativeSceneObjects
	 * @param clippedSceneObjects
	 * @param parent
	 * @param studio
	 */
	public SceneObjectIntersection(
			String description,
			ArrayList<SceneObject> positiveSceneObjects,
			ArrayList<SceneObject> negativeSceneObjects,
			ArrayList<SceneObject> invisiblePositiveSceneObjects,
			ArrayList<SceneObject> invisibleNegativeSceneObjects,
			ArrayList<SceneObject> clippedSceneObjects,
			SceneObject parent,
			Studio studio
		)
	{
		super(description, parent, studio);
		
		this.positiveSceneObjects = positiveSceneObjects;
		this.negativeSceneObjects = negativeSceneObjects;
		this.invisiblePositiveSceneObjects = invisiblePositiveSceneObjects;
		this.invisibleNegativeSceneObjects = invisibleNegativeSceneObjects;
		this.clippedSceneObjects = clippedSceneObjects;
		
		// initialise the lists of positive and negative scene objects
//		this.positiveSceneObjects = new ArrayList<SceneObject>();
//		this.negativeSceneObjects  = new ArrayList<SceneObject>();
//		this.invisiblePositiveSceneObjects = new ArrayList<SceneObject>();
//		this.invisibleNegativeSceneObjects  = new ArrayList<SceneObject>();
//
//		if(positiveSceneObjects != null) addPositiveSceneObjects(positiveSceneObjects);
//		if(negativeSceneObjects != null) addNegativeSceneObjects(negativeSceneObjects);
//		if(invisiblePositiveSceneObjects != null) addInvisiblePositiveSceneObjects(positiveSceneObjects);
//		if(invisibleNegativeSceneObjects != null) addInvisibleNegativeSceneObjects(negativeSceneObjects);
	}
	
	/**
	 * Constructor that sets all visible scene objects (and sets the invisible ones to empty lists)
	 * @param description
	 * @param positiveSceneObjects
	 * @param negativeSceneObjects
	 * @param parent
	 * @param studio
	 */
	public SceneObjectIntersection(
			String description,
			ArrayList<SceneObject> positiveSceneObjects,
			ArrayList<SceneObject> negativeSceneObjects,
			SceneObject parent,
			Studio studio
		)
	{
		this(description, positiveSceneObjects, negativeSceneObjects, null, null, null, parent, studio);
	}


	/**
	 * Create an empty scene-object combination.
	 * Scene objects can be added later using the addPositiveSceneObject and addNegativeSceneObject methods.
	 * 
	 * @param description
	 */
	public SceneObjectIntersection(String description, SceneObject parent, Studio studio)
	{
		this(description, null, null, null, null, null, parent, studio);
	}
	
	/**
	 * Makes a copy of the original SceneObjectCombination.
	 * Note that the positive and negative scene objects in the copy are the same as those in the original.
	 * (I.e. they are not clones.)
	 * 
	 * @param original
	 */
	public SceneObjectIntersection(SceneObjectIntersection original)
	{
		this(
				original.getDescription(),
				original.getPositiveSceneObjects(),
				original.getNegativeSceneObjects(),
				original.getInvisiblePositiveSceneObjects(),
				original.getInvisibleNegativeSceneObjects(),
				original.getClippedSceneObjects(),
				original.getParent(),
				original.getStudio()
			);
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.core.SceneObjectClass#clone()
	 */
	@Override
	public SceneObjectIntersection clone()
	{
		return new SceneObjectIntersection(this);
	}
	
	
	//
	// setters & getters
	//
	
	/**
	 * @return	the list of the positive scene objects
	 */
	public ArrayList<SceneObject> getPositiveSceneObjects()
	{
		return positiveSceneObjects;
	}

	/**
	 * Add the SceneObjects in <sceneObjects> to the list of positive scene objects.
	 * 
	 * @param sceneObjects
	 */
	public void addPositiveSceneObjects(ArrayList<SceneObject> sceneObjects)
	{
		for(SceneObject sceneObject: sceneObjects)
		{
			addPositiveSceneObject(sceneObject);
		}
	}
	
	/**
	 * Add <sceneObject> to the list of positive scene objects.
	 * 
	 * @param sceneObject
	 */
	public void addPositiveSceneObject(SceneObject sceneObject)
	{
		// if necessary, initialise positiveSceneObjects
		if(positiveSceneObjects == null) positiveSceneObjects = new ArrayList<SceneObject>();
			
		// make sure the scene object has this as parent...
		sceneObject.setParent(this);
		// ... and this's studio as studio
		sceneObject.setStudio(getStudio());

		// add it to the list of positive scene objects
		positiveSceneObjects.add(sceneObject);
	}

	/**
	 * @return	the list of negative scene objects
	 */
	public ArrayList<SceneObject> getNegativeSceneObjects()
	{
		return negativeSceneObjects;
	}

	/**
	 * Add the SceneObjects in <sceneObjects> to the list of negative scene objects.
	 * 
	 * @param sceneObjects
	 */
	public void addNegativeSceneObjects(ArrayList<SceneObject> sceneObjects)
	{
		for(SceneObject sceneObject: sceneObjects)
		{
			addNegativeSceneObject(sceneObject);
		}
	}
	
	/**
	 * Add <sceneObject> to the list of negative scene objects.
	 * 
	 * @param sceneObject
	 */
	public void addNegativeSceneObject(SceneObject sceneObject)
	{
		// if necessary, initialise negativeSceneObjects
		if(negativeSceneObjects == null) negativeSceneObjects = new ArrayList<SceneObject>();
			
		// make sure the scene object has this as parent...
		sceneObject.setParent(this);
		// ... and this's studio as studio
		sceneObject.setStudio(getStudio());

		// add it to the list of negative scene objects
		negativeSceneObjects.add(sceneObject);
	}

	/**
	 * @return	the list of the invisible positive scene objects
	 */
	public ArrayList<SceneObject> getInvisiblePositiveSceneObjects() {
		return invisiblePositiveSceneObjects;
	}

	/**
	 * Add the SceneObjects in <sceneObjects> to the list of invisible positive scene objects.
	 * 
	 * @param sceneObjects
	 */
	public void addInvisiblePositiveSceneObjects(ArrayList<SceneObject> sceneObjects)
	{
		for(SceneObject sceneObject: sceneObjects)
		{
			addInvisiblePositiveSceneObject(sceneObject);
		}
	}
	
	/**
	 * Add <sceneObject> to the list of invisible positive scene objects.
	 * 
	 * @param sceneObject
	 */
	public void addInvisiblePositiveSceneObject(SceneObject sceneObject)
	{
		// if necessary, initialise invisiblePositiveSceneObjects
		if(invisiblePositiveSceneObjects == null) invisiblePositiveSceneObjects = new ArrayList<SceneObject>();
			
		// make sure the scene object has this as parent...
		sceneObject.setParent(this);
		// ... and this's studio as studio
		sceneObject.setStudio(getStudio());

		// add it to the list of positive scene objects
		invisiblePositiveSceneObjects.add(sceneObject);
	}

	/**
	 * @return	the list of invisible negative scene objects
	 */
	public ArrayList<SceneObject> getInvisibleNegativeSceneObjects()
	{
		return invisibleNegativeSceneObjects;
	}

	/**
	 * Add the SceneObjects in <sceneObjects> to the list of invisible negative scene objects.
	 * 
	 * @param sceneObjects
	 */
	public void addInvisibleNegativeSceneObjects(ArrayList<SceneObject> sceneObjects)
	{
		for(SceneObject sceneObject: sceneObjects)
		{
			addInvisibleNegativeSceneObject(sceneObject);
		}
	}
	
	/**
	 * Add <sceneObject> to the list of invisible negative scene objects.
	 * 
	 * @param sceneObject
	 */
	public void addInvisibleNegativeSceneObject(SceneObject sceneObject)
	{
		// if necessary, initialise invisibleNegativeSceneObjects
		if(invisibleNegativeSceneObjects == null) invisibleNegativeSceneObjects = new ArrayList<SceneObject>();
			
		// make sure the scene object has this as parent...
		sceneObject.setParent(this);
		// ... and this's studio as studio
		sceneObject.setStudio(getStudio());

		// add it to the list of invisible negative scene objects
		invisibleNegativeSceneObjects.add(sceneObject);
	}

	/**
	 * @return	the list of clipped scene objects
	 */
	public ArrayList<SceneObject> getClippedSceneObjects() {
		return clippedSceneObjects;
	}

	/**
	 * Add the SceneObjects in <sceneObjects> to the list of clipped scene objects.
	 * 
	 * @param sceneObjects
	 */
	public void addClippedSceneObjects(ArrayList<SceneObject> sceneObjects)
	{
		for(SceneObject sceneObject: sceneObjects)
		{
			addClippedSceneObject(sceneObject);
		}
	}
	
	/**
	 * Add <sceneObject> to the list of clipped scene objects.
	 * 
	 * @param sceneObject
	 */
	public void addClippedSceneObject(SceneObject sceneObject)
	{
		// if necessary, initialise clippedSceneObjects
		if(clippedSceneObjects == null) clippedSceneObjects = new ArrayList<SceneObject>();
			
		// make sure the scene object has this as parent...
		sceneObject.setParent(this);
		// ... and this's studio as studio
		sceneObject.setStudio(getStudio());

		// add it to the list of clipped scene objects
		clippedSceneObjects.add(sceneObject);
	}

	/**
	 * clear all positive and negative scene objects contained in this combination
	 */
	public void clear()
	{
		positiveSceneObjects = null;
		negativeSceneObjects = null;
		invisiblePositiveSceneObjects = null;
		invisibleNegativeSceneObjects = null;
		clippedSceneObjects = null;
	}

	
	/**
	 * @param raySceneObjectIntersection
	 * @return	true if the position associated with the ray-scene-object intersection lies inside all positive scene objects other than the one involved in the intersection
	 */
	private boolean insideAllPositiveSceneObjects(RaySceneObjectIntersection raySceneObjectIntersection)
	{
		// first create a list of *all* positive scene objects, visible and invisible ones
		ArrayList<SceneObject> allPositiveSOPs = new ArrayList<SceneObject>();
		if(positiveSceneObjects != null) allPositiveSOPs.addAll(positiveSceneObjects);
		if(invisiblePositiveSceneObjects != null) allPositiveSOPs.addAll(invisiblePositiveSceneObjects);
		
		// let <sceneObject> be in turn every one of the visible or invisible positive scene objects
		for(SceneObject sceneObject:allPositiveSOPs)
		{
			// is <sceneObject> the scene object that was involved in the ray-scene-object intersection?
			// The one that was involved in the ray-scene-object intersection should not be checked, as the intersection
			// lies on its surface, and therefore lies neither inside nor outside; the question of whether or not the
			// intersection lies inside the object therefore returns just numerical noise
			if(!sceneObject.getSceneObjectPrimitives().contains(raySceneObjectIntersection.o))
			{
				// no, it is a different scene object
				
				// is the position of the ray-scene-object intersection inside sceneObject?
				if(!sceneObject.insideObject(raySceneObjectIntersection.p))
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
		// first create a list of *all* negative scene objects, visible and invisible ones
		ArrayList<SceneObject> allNegativeSOPs = new ArrayList<SceneObject>();
		if(negativeSceneObjects != null) allNegativeSOPs.addAll(negativeSceneObjects);
		if(invisibleNegativeSceneObjects != null) allNegativeSOPs.addAll(invisibleNegativeSceneObjects);
		
		// let <sceneObject> be in turn every one of the visible or invisible negative scene objects
		for(SceneObject sceneObject:allNegativeSOPs)
		{
			// is <sceneObject> the scene object that was involved in the ray-scene-object intersection?
			// The one that was involved in the ray-scene-object intersection should not be checked, as the intersection
			// lies on its surface, and therefore lies neither inside nor outside; the question of whether or not the
			// intersection lies inside the object therefore returns just numerical noise
			if(!sceneObject.getSceneObjectPrimitives().contains(raySceneObjectIntersection.o))
			{
				// no, it is a different scene object
				
				// is the position of the ray-scene-object intersection outside sceneObject?
				if(sceneObject.insideObject(raySceneObjectIntersection.p))
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
		
//	/**
//	 * Set <i>currentlyBestIntersection</i> to a closer one, if possible
//	 * @param currentlyBestIntersection
//	 * @param sceneObjectPrimitive
//	 * @param ray
//	 * @param excludeObject
//	 */
//	private void lookForBetterIntersection(
//			IntersectionAndDistance currentlyBestIntersection,
//			SceneObject sceneObjectPrimitive,
//			Ray ray,
//			SceneObject excludeObject
//		)
//	{
//		// calculate the intersection
//		RaySceneObjectIntersection intersection = sceneObjectPrimitive.getClosestRayIntersectionAvoidingOrigin(ray, excludeObject);
//
//		// is there an intersection point?
//		if(intersection != RaySceneObjectIntersection.NO_INTERSECTION)
//		{
//			// yes
//
//			// calculate the distance (squared) to the intersection point
//			double distance2 = Vector3D.getDistance2(intersection.p, ray.getP());	// intersection.p.getDifferenceWith(ray.getP()).getModSquared();
//			
//			// is the distance to this intersection point smaller than the smallest one so far?
//			if(distance2 < currentlyBestIntersection.distance2)
//			{
//				// yes
//
//				// check that the intersection point is on the surface, i.e. inside all positive scene objects other than the
//				// one involved in the intersection and outside all negative scene objects other then the one involved in the
//				// intersection
//				if(insideAllPositiveSceneObjects(intersection) && outsideAllNegativeSceneObjects(intersection))
//				{
//					// yes, so make the new intersection point the closest one so far
//					currentlyBestIntersection.distance2 = distance2;
//					currentlyBestIntersection.intersection = intersection;
//					
//					return;
//				}
//			}
//		}
//	}
	
	
	

	private void lookForBetterIntersection(
			IntersectionAndDistance currentlyBestIntersection,
			SceneObject sceneObject,
			Ray ray,
			SceneObjectPrimitive excludeObject,
			IntersectionInclusionCriterion iic
			)
	{
		// calculate the closest intersection
		RaySceneObjectIntersection intersection = sceneObject.getClosestRayIntersectionAvoidingOrigin(ray, excludeObject);


		int counter = 100;	// TODO without the counter, the program sometimes gets stuck in the while loop below; understand this!
		// while there is an intersection point...
		while((intersection != RaySceneObjectIntersection.NO_INTERSECTION) && (counter-- > 0))
		{
			// System.out.println("SceneObjectIntersection::lookForBetterIntersection: counter="+counter++);
			
			if(iic.include(intersection))
			{
				// calculate the distance (squared) to the intersection point
				double distance2 = Vector3D.getDistance2(intersection.p, ray.getP()); // intersection.p.getDifferenceWith(ray.getP()).getModSquared();


				// is the distance to this intersection point smaller than the smallest one so far?
				if(distance2 < currentlyBestIntersection.distance2)
				{
					// yes

					// check that the intersection point is on the surface, i.e. inside all positive scene objects other than the
					// one involved in the intersection and outside all negative scene objects other then the one involved in the
					// intersection
					if(insideAllPositiveSceneObjects(intersection) && outsideAllNegativeSceneObjects(intersection))
					{
						// yes, so make the new intersection point the closest one so far
						currentlyBestIntersection.distance2 = distance2;
						currentlyBestIntersection.intersection = intersection;


						return;
					}
				}
				else {

					// the distance to the intersection with this object is already greater than the distance of the closest intersection;
					// no need to look for the next, even more distant, intersection
					return;
				}
			}

			// it could be that the current intersection is clipped, but that the next one isn't;
			intersection = sceneObject.getNextClosestRayIntersectionAvoidingOrigin(ray, excludeObject, intersection);
		}
	}

	public RaySceneObjectIntersection getClosestRayIntersectionAvoidingOrigin(Ray ray, SceneObjectPrimitive excludeObject, IntersectionInclusionCriterion iic)
	{
		// initialise the information about the closest intersection so far
		IntersectionAndDistance closestIntersectionAndDistance = new IntersectionAndDistance(RaySceneObjectIntersection.NO_INTERSECTION, Double.POSITIVE_INFINITY);
		
		// go through the (visible) positive, ...
		if(positiveSceneObjects != null)
		for(SceneObject sceneObject:positiveSceneObjects) 
			lookForBetterIntersection(closestIntersectionAndDistance, sceneObject, ray, excludeObject, iic);
		
		// ... (visible) negative scene objects, ...
		if(negativeSceneObjects != null)
		for(SceneObject sceneObject:negativeSceneObjects) 
			lookForBetterIntersection(
					closestIntersectionAndDistance,
					sceneObject,	// new SceneObjectInverse(sceneObjectPrimitive),
					ray,
					excludeObject,
					iic
				);

		// ... and clipped scene objects
		if(clippedSceneObjects != null)
		for(SceneObject sceneObject:clippedSceneObjects) 
			lookForBetterIntersection(closestIntersectionAndDistance, sceneObject, ray, excludeObject, iic);

		return closestIntersectionAndDistance.intersection;    
	}

	
	/* (non-Javadoc)
	 * @see optics.raytrace.SceneObjectContainer#getClosestRayIntersectionAvoidingOrigin(optics.raytrace.Ray, optics.raytrace.SceneObject)
	 */
	@Override
	public RaySceneObjectIntersection getClosestRayIntersectionAvoidingOrigin(Ray ray, SceneObjectPrimitive excludeObject)
	{
		return getClosestRayIntersectionAvoidingOrigin(ray, excludeObject, IntersectionInclusionCriterion.iicAll);
	}
	
	/* (non-Javadoc)
	 * @see optics.raytrace.SceneObjectContainer#getClosestRayIntersectionAvoidingOrigin(optics.raytrace.Ray, optics.raytrace.SceneObject)
	 */
	@Override
	public RaySceneObjectIntersection getClosestRayIntersectionWithShadowThrowingSceneObjectAvoidingOrigin(Ray ray, SceneObjectPrimitive excludeObject)
	{
		return getClosestRayIntersectionAvoidingOrigin(ray, excludeObject, IntersectionInclusionCriterion.iicSTO);
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.SceneObjectContainer#transform(optics.raytrace.Transformation)
	 */
	@Override
	public SceneObjectIntersection transform(Transformation t)
	{
		SceneObjectIntersection transformed = new SceneObjectIntersection(description + "(transformed)", getParent(), getStudio());
		
		// add the transformed visible positive...
		if(positiveSceneObjects != null)
		for(SceneObject sceneObject:positiveSceneObjects)
			transformed.addPositiveSceneObject(sceneObject.transform(t));
		
		// ... and negative scene objects
		if(negativeSceneObjects != null)
		for(SceneObject sceneObject:negativeSceneObjects)
			transformed.addNegativeSceneObject(sceneObject.transform(t));

		// add the transformed invisible positive...
		if(invisiblePositiveSceneObjects != null)
		for(SceneObject sceneObject:invisiblePositiveSceneObjects)
			transformed.addInvisiblePositiveSceneObject(sceneObject.transform(t));
		
		// ... and negative scene objects
		if(invisibleNegativeSceneObjects != null)
		for(SceneObject sceneObject:invisibleNegativeSceneObjects)
			transformed.addInvisibleNegativeSceneObject(sceneObject.transform(t));

		// add the transformed clipped scene objects
		if(clippedSceneObjects != null)
		for(SceneObject sceneObject:clippedSceneObjects)
			transformed.addClippedSceneObject(sceneObject.transform(t));
		
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
	public RaySceneObjectIntersection getClosestRayIntersection(Ray ray)
	{
		return getClosestRayIntersectionAvoidingOrigin(ray, (SceneObjectPrimitive)null);
	}

	@Override
	public RaySceneObjectIntersection getClosestRayIntersectionWithShadowThrowingSceneObject(Ray ray)
	{
		return getClosestRayIntersectionWithShadowThrowingSceneObjectAvoidingOrigin(ray, (SceneObjectPrimitive)null);
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.core.SceneObject#getSceneObjectPrimitives()
	 * Returns visible and invisible scene-object primitives
	 */
	@Override
	public ArrayList<SceneObjectPrimitive> getSceneObjectPrimitives()
	{
		ArrayList<SceneObjectPrimitive> SOPs = new ArrayList<SceneObjectPrimitive>();
		
		if(positiveSceneObjects != null) SOPs.addAll(getSceneObjectPrimitives(positiveSceneObjects));
		if(negativeSceneObjects != null) SOPs.addAll(getSceneObjectPrimitives(negativeSceneObjects));	// TODO add the SceneObjectInverse of each of these
		if(invisiblePositiveSceneObjects != null) SOPs.addAll(getSceneObjectPrimitives(invisiblePositiveSceneObjects));
		if(invisibleNegativeSceneObjects != null) SOPs.addAll(getSceneObjectPrimitives(invisibleNegativeSceneObjects));
		if(clippedSceneObjects != null) SOPs.addAll(getSceneObjectPrimitives(clippedSceneObjects));

		return SOPs;
	}
	
	/**
	 * @param sceneObjects	list of scene objects
	 * @return an ArrayList containing all the SceneObjectPrimitives making up the list of scene objects
	 */
	public ArrayList<SceneObjectPrimitive> getSceneObjectPrimitives(ArrayList<SceneObject> sceneObjects)
	{
		ArrayList<SceneObjectPrimitive> SOPs = new ArrayList<SceneObjectPrimitive>();
		
		for(SceneObject s:sceneObjects)
		{
			if(s instanceof SceneObjectPrimitive) SOPs.add((SceneObjectPrimitive)s);
			else SOPs.addAll(s.getSceneObjectPrimitives());			
		}
		
		return SOPs;
	}
	
	@Override
	public String getType()
	{
		return "Intersection";
	}
}
