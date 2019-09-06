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
 * The combined scene object is the intersection of all scene-object primitives in the lists
 * <positiveSceneObjectPrimitives> and <invisiblePositiveSceneObjectPrimitives> --- the "positive" scene-object primitives ---, and the inverse of all scene-object primitives
 * in the lists <negativeSceneObjectPrimitives> and <invisibleNegativeSceneObjectPrimitives> --- the "negative" scene-object primitives.
 * This means that for a point to lie inside the combined scene object it has to lie inside every single positive
 * scene-object primitive and outside every single negative scene-object primitive.
 * 
 * For a point on the surface of scene-object primitive A, which is either one of the positive or negative
 * scene-object primitives (it doesn't make sense for it to be both), that point must lie inside all positive
 * scene-object primitives other than A and outside all negative scene-object primitives other than A.
 * 
 * @author Johannes Courtial
 */ 
public class SceneObjectPrimitiveIntersection extends SceneObjectClass
implements Serializable
{
	private static final long serialVersionUID = -3947331077775276771L;

	/**
	 * list of the visible positive scene-object primitives
	 * (for a point to lie inside the combined scene object, it has to lie inside every single positive scene-object primitive)
	 */
	protected ArrayList<SceneObjectPrimitive> positiveSceneObjectPrimitives;

	/**
	 * list of the visible negative scene-object primitives
	 * (for a point to lie inside the combined scene object, it has to lie outside every single negative scene-object primitive)
	 */
	protected ArrayList<SceneObjectPrimitive> negativeSceneObjectPrimitives;

	/**
	 * list of the invisible positive scene-object primitives
	 * (for a point to lie inside the combined scene object, it has to lie inside every single positive scene-object primitive)
	 */
	protected ArrayList<SceneObjectPrimitive> invisiblePositiveSceneObjectPrimitives;

	/**
	 * list of the invisible negative scene-object primitives
	 * (for a point to lie inside the combined scene object, it has to lie outside every single negative scene-object primitive)
	 */
	protected ArrayList<SceneObjectPrimitive> invisibleNegativeSceneObjectPrimitives;

	/**
	 * list of scene-object primitives that are cut such by the other scene-object primitives, but which do not themselves cut the other scene-object primitives
	 */
	protected ArrayList<SceneObjectPrimitive> cutSceneObjectPrimitives;

	/**
	 * Constructor that sets all internal parameters
	 * @param description
	 * @param positiveSceneObjectPrimitives
	 * @param negativeSceneObjectPrimitives
	 * @param invisiblePositiveSceneObjectPrimitives
	 * @param invisibleNegativeSceneObjectPrimitives
	 * @param cutSceneObjectPrimitives
	 * @param parent
	 * @param studio
	 */
	public SceneObjectPrimitiveIntersection(
			String description,
			ArrayList<SceneObjectPrimitive> positiveSceneObjectPrimitives,
			ArrayList<SceneObjectPrimitive> negativeSceneObjectPrimitives,
			ArrayList<SceneObjectPrimitive> invisiblePositiveSceneObjectPrimitives,
			ArrayList<SceneObjectPrimitive> invisibleNegativeSceneObjectPrimitives,
			ArrayList<SceneObjectPrimitive> cutSceneObjectPrimitives,
			SceneObject parent,
			Studio studio
		)
	{
		super(description, parent, studio);
		
		this.positiveSceneObjectPrimitives = positiveSceneObjectPrimitives;
		this.negativeSceneObjectPrimitives = negativeSceneObjectPrimitives;
		this.invisiblePositiveSceneObjectPrimitives = invisiblePositiveSceneObjectPrimitives;
		this.invisibleNegativeSceneObjectPrimitives = invisibleNegativeSceneObjectPrimitives;
		this.cutSceneObjectPrimitives = cutSceneObjectPrimitives;
		
		// initialise the lists of positive and negative scene-object primitives
//		this.positiveSceneObjectPrimitives = new ArrayList<SceneObjectPrimitive>();
//		this.negativeSceneObjectPrimitives  = new ArrayList<SceneObjectPrimitive>();
//		this.invisiblePositiveSceneObjectPrimitives = new ArrayList<SceneObjectPrimitive>();
//		this.invisibleNegativeSceneObjectPrimitives  = new ArrayList<SceneObjectPrimitive>();
//
//		if(positiveSceneObjectPrimitives != null) addPositiveSceneObjectPrimitives(positiveSceneObjectPrimitives);
//		if(negativeSceneObjectPrimitives != null) addNegativeSceneObjectPrimitives(negativeSceneObjectPrimitives);
//		if(invisiblePositiveSceneObjectPrimitives != null) addInvisiblePositiveSceneObjectPrimitives(positiveSceneObjectPrimitives);
//		if(invisibleNegativeSceneObjectPrimitives != null) addInvisibleNegativeSceneObjectPrimitives(negativeSceneObjectPrimitives);
	}
	
	/**
	 * Constructor that sets all visible scene-object primitives (and sets the invisible ones to empty lists)
	 * @param description
	 * @param positiveSceneObjectPrimitives
	 * @param negativeSceneObjectPrimitives
	 * @param parent
	 * @param studio
	 */
	public SceneObjectPrimitiveIntersection(
			String description,
			ArrayList<SceneObjectPrimitive> positiveSceneObjectPrimitives,
			ArrayList<SceneObjectPrimitive> negativeSceneObjectPrimitives,
			SceneObject parent,
			Studio studio
		)
	{
		this(description, positiveSceneObjectPrimitives, negativeSceneObjectPrimitives, null, null, null, parent, studio);
	}


	/**
	 * Create an empty scene-object combination.
	 * Scene objects can be added later using the addPositiveSceneObjectPrimitive and addNegativeSceneObjectPrimitive methods.
	 * 
	 * @param description
	 */
	public SceneObjectPrimitiveIntersection(String description, SceneObject parent, Studio studio)
	{
		this(description, null, null, null, null, null, parent, studio);
	}
	
	/**
	 * Makes a copy of the original SceneObjectCombination.
	 * Note that the positive and negative scene-object primitives in the copy are the same as those in the original.
	 * (I.e. they are not clones.)
	 * 
	 * @param original
	 */
	public SceneObjectPrimitiveIntersection(SceneObjectPrimitiveIntersection original)
	{
		this(
				original.getDescription(),
				original.getPositiveSceneObjectPrimitives(),
				original.getNegativeSceneObjectPrimitives(),
				original.getInvisiblePositiveSceneObjectPrimitives(),
				original.getInvisibleNegativeSceneObjectPrimitives(),
				original.getCutSceneObjectPrimitives(),
				original.getParent(),
				original.getStudio()
			);
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.core.SceneObjectClass#clone()
	 */
	@Override
	public SceneObjectPrimitiveIntersection clone()
	{
		return new SceneObjectPrimitiveIntersection(this);
	}
	
	
	//
	// setters & getters
	//
	
	/**
	 * @return	the list of the positive scene-object primitives
	 */
	public ArrayList<SceneObjectPrimitive> getPositiveSceneObjectPrimitives()
	{
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
		// if necessary, initialise positiveSceneObjectPrimitives
		if(positiveSceneObjectPrimitives == null) positiveSceneObjectPrimitives = new ArrayList<SceneObjectPrimitive>();
			
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
	public ArrayList<SceneObjectPrimitive> getNegativeSceneObjectPrimitives()
	{
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
		// if necessary, initialise negativeSceneObjectPrimitives
		if(negativeSceneObjectPrimitives == null) negativeSceneObjectPrimitives = new ArrayList<SceneObjectPrimitive>();
			
		// make sure the scene object has this as parent...
		sceneObjectPrimitive.setParent(this);
		// ... and this's studio as studio
		sceneObjectPrimitive.setStudio(getStudio());

		// add it to the list of negative scene-object primitives
		negativeSceneObjectPrimitives.add(sceneObjectPrimitive);
	}

	/**
	 * @return	the list of the invisible positive scene-object primitives
	 */
	public ArrayList<SceneObjectPrimitive> getInvisiblePositiveSceneObjectPrimitives() {
		return invisiblePositiveSceneObjectPrimitives;
	}

	/**
	 * Add the SceneObjectPrimitives in <sceneObjectPrimitives> to the list of invisible positive scene-object primitives.
	 * 
	 * @param sceneObjectPrimitives
	 */
	public void addInvisiblePositiveSceneObjectPrimitives(ArrayList<SceneObjectPrimitive> sceneObjectPrimitives)
	{
		for(SceneObjectPrimitive sceneObjectPrimitive: sceneObjectPrimitives)
		{
			addInvisiblePositiveSceneObjectPrimitive(sceneObjectPrimitive);
		}
	}
	
	/**
	 * Add <sceneObjectPrimitive> to the list of invisible positive scene-object primitives.
	 * 
	 * @param sceneObjectPrimitive
	 */
	public void addInvisiblePositiveSceneObjectPrimitive(SceneObjectPrimitive sceneObjectPrimitive)
	{
		// if necessary, initialise invisiblePositiveSceneObjectPrimitives
		if(invisiblePositiveSceneObjectPrimitives == null) invisiblePositiveSceneObjectPrimitives = new ArrayList<SceneObjectPrimitive>();
			
		// make sure the scene object has this as parent...
		sceneObjectPrimitive.setParent(this);
		// ... and this's studio as studio
		sceneObjectPrimitive.setStudio(getStudio());

		// add it to the list of positive scene-object primitives
		invisiblePositiveSceneObjectPrimitives.add(sceneObjectPrimitive);
	}

	/**
	 * @return	the list of invisible negative scene-object primitives
	 */
	public ArrayList<SceneObjectPrimitive> getInvisibleNegativeSceneObjectPrimitives()
	{
		return invisibleNegativeSceneObjectPrimitives;
	}

	/**
	 * Add the SceneObjectPrimitives in <sceneObjectPrimitives> to the list of invisible negative scene-object primitives.
	 * 
	 * @param sceneObjectPrimitives
	 */
	public void addInvisibleNegativeSceneObjectPrimitives(ArrayList<SceneObjectPrimitive> sceneObjectPrimitives)
	{
		for(SceneObjectPrimitive sceneObjectPrimitive: sceneObjectPrimitives)
		{
			addInvisibleNegativeSceneObjectPrimitive(sceneObjectPrimitive);
		}
	}
	
	/**
	 * Add <sceneObjectPrimitive> to the list of invisible negative scene-object primitives.
	 * 
	 * @param sceneObjectPrimitive
	 */
	public void addInvisibleNegativeSceneObjectPrimitive(SceneObjectPrimitive sceneObjectPrimitive)
	{
		// if necessary, initialise invisibleNegativeSceneObjectPrimitives
		if(invisibleNegativeSceneObjectPrimitives == null) invisibleNegativeSceneObjectPrimitives = new ArrayList<SceneObjectPrimitive>();
			
		// make sure the scene object has this as parent...
		sceneObjectPrimitive.setParent(this);
		// ... and this's studio as studio
		sceneObjectPrimitive.setStudio(getStudio());

		// add it to the list of invisible negative scene-object primitives
		invisibleNegativeSceneObjectPrimitives.add(sceneObjectPrimitive);
	}

	/**
	 * @return	the list of cut scene-object primitives
	 */
	public ArrayList<SceneObjectPrimitive> getCutSceneObjectPrimitives() {
		return cutSceneObjectPrimitives;
	}

	/**
	 * Add the SceneObjectPrimitives in <sceneObjectPrimitives> to the list of cut scene-object primitives.
	 * 
	 * @param sceneObjectPrimitives
	 */
	public void addCutSceneObjectPrimitives(ArrayList<SceneObjectPrimitive> sceneObjectPrimitives)
	{
		for(SceneObjectPrimitive sceneObjectPrimitive: sceneObjectPrimitives)
		{
			addCutSceneObjectPrimitive(sceneObjectPrimitive);
		}
	}
	
	/**
	 * Add <sceneObjectPrimitive> to the list of cut scene-object primitives.
	 * 
	 * @param sceneObjectPrimitive
	 */
	public void addCutSceneObjectPrimitive(SceneObjectPrimitive sceneObjectPrimitive)
	{
		// if necessary, initialise cutSceneObjectPrimitives
		if(cutSceneObjectPrimitives == null) cutSceneObjectPrimitives = new ArrayList<SceneObjectPrimitive>();
			
		// make sure the scene object has this as parent...
		sceneObjectPrimitive.setParent(this);
		// ... and this's studio as studio
		sceneObjectPrimitive.setStudio(getStudio());

		// add it to the list of cut scene-object primitives
		cutSceneObjectPrimitives.add(sceneObjectPrimitive);
	}

	/**
	 * clear all positive and negative scene-object primitives contained in this combination
	 */
	public void clear()
	{
		positiveSceneObjectPrimitives = null;
		negativeSceneObjectPrimitives = null;
		invisiblePositiveSceneObjectPrimitives = null;
		invisibleNegativeSceneObjectPrimitives = null;
		cutSceneObjectPrimitives = null;
	}

	
	/**
	 * @param raySceneObjectIntersection
	 * @return	true if the position associated with the ray-scene-object intersection lies inside all positive scene-object primitives other than the one involved in the intersection
	 */
	private boolean insideAllPositiveSceneObjectPrimitives(RaySceneObjectIntersection raySceneObjectIntersection)
	{
		// first create a list of *all* positive scene-object primitives, visible and invisible ones
		ArrayList<SceneObjectPrimitive> allPositiveSOPs = new ArrayList<SceneObjectPrimitive>();
		if(positiveSceneObjectPrimitives != null) allPositiveSOPs.addAll(positiveSceneObjectPrimitives);
		if(invisiblePositiveSceneObjectPrimitives != null) allPositiveSOPs.addAll(invisiblePositiveSceneObjectPrimitives);
		
		// let <sceneObjectPrimitive> be in turn every one of the visible or invisible positive scene-object primitives
		for(SceneObjectPrimitive sceneObjectPrimitive:allPositiveSOPs)
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
		// first create a list of *all* negative scene-object primitives, visible and invisible ones
		ArrayList<SceneObjectPrimitive> allNegativeSOPs = new ArrayList<SceneObjectPrimitive>();
		if(negativeSceneObjectPrimitives != null) allNegativeSOPs.addAll(negativeSceneObjectPrimitives);
		if(invisibleNegativeSceneObjectPrimitives != null) allNegativeSOPs.addAll(invisibleNegativeSceneObjectPrimitives);
		
		// let <sceneObjectPrimitive> be in turn every one of the visible or invisible negative scene-object primitives
		for(SceneObjectPrimitive sceneObjectPrimitive:allNegativeSOPs)
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
	
	/**
	 * @author johannes
	 * a little data structure that stores a ray-scene-object intersection and the corresponding distance (squared)
	 */
	public class IntersectionAndDistance
	{
		public RaySceneObjectIntersection intersection;
		public double distance2;
		
		public IntersectionAndDistance(RaySceneObjectIntersection intersection, double distance2)
		{
			this.intersection = intersection;
			this.distance2 = distance2;
		}
	}
	
	/**
	 * Set <i>currentlyBestIntersection</i> to a closer one, if possible
	 * @param currentlyBestIntersection
	 * @param sceneObjectPrimitive
	 * @param ray
	 * @param excludeObject
	 */
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
				double distance2 = Vector3D.getDistance2(intersection.p, ray.getP());	// intersection.p.getDifferenceWith(ray.getP()).getModSquared();
				
				// is the distance to this intersection point smaller than the smallest one so far?
				if(distance2 < currentlyBestIntersection.distance2)
				{
					// yes, so make the new intersection point the closest one so far
					currentlyBestIntersection.distance2 = distance2;
					currentlyBestIntersection.intersection = intersection;
				}
				
				// don't look for further intersections with this object
				break;	// intersection = RaySceneObjectIntersection.NO_INTERSECTION;
			}
//			else
//			{
				// try the next intersection with the same object
				intersection = sceneObjectPrimitive.getNextClosestRayIntersectionAvoidingOrigin(ray, excludeObject, intersection);
// 			}
		}
	}
	
	/* (non-Javadoc)
	 * @see optics.raytrace.SceneObjectContainer#getClosestRayIntersectionAvoidingOrigin(optics.raytrace.Ray, optics.raytrace.SceneObject)
	 */
	@Override
	public RaySceneObjectIntersection getClosestRayIntersectionAvoidingOrigin(Ray ray, SceneObjectPrimitive excludeObject)
	{
		// initialise the information about the closest intersection so far
		IntersectionAndDistance closestIntersectionAndDistance = new IntersectionAndDistance(RaySceneObjectIntersection.NO_INTERSECTION, Double.POSITIVE_INFINITY);
		
		// go through the (visible) positive, ...
		if(positiveSceneObjectPrimitives != null)
		for(SceneObjectPrimitive sceneObjectPrimitive:positiveSceneObjectPrimitives) 
			lookForBetterIntersection(closestIntersectionAndDistance, sceneObjectPrimitive, ray, excludeObject);
		
		// ... (visible) negative scene-object primitives, ...
		if(negativeSceneObjectPrimitives != null)
		for(SceneObjectPrimitive sceneObjectPrimitive:negativeSceneObjectPrimitives) 
			lookForBetterIntersection(
					closestIntersectionAndDistance,
					sceneObjectPrimitive,	// new SceneObjectPrimitiveInverse(sceneObjectPrimitive),
					ray,
					excludeObject
				);

		// ... and cut scene-object primitives
		if(cutSceneObjectPrimitives != null)
		for(SceneObjectPrimitive sceneObjectPrimitive:cutSceneObjectPrimitives) 
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
		IntersectionAndDistance closestIntersectionAndDistance = new IntersectionAndDistance(RaySceneObjectIntersection.NO_INTERSECTION, Double.POSITIVE_INFINITY);
		
		// go through the shadow-throwing, visible, positive, ...
		if(positiveSceneObjectPrimitives != null)
		for(SceneObjectPrimitive sceneObjectPrimitive:positiveSceneObjectPrimitives)
			if(sceneObjectPrimitive.isShadowThrowing())
				lookForBetterIntersection(closestIntersectionAndDistance, sceneObjectPrimitive, ray, excludeObject);
		
		// ... the shadow-throwing, visible, negative scene-object primitives, ...
		if(negativeSceneObjectPrimitives != null)
		for(SceneObjectPrimitive sceneObjectPrimitive:negativeSceneObjectPrimitives) 
			if(sceneObjectPrimitive.isShadowThrowing())
				lookForBetterIntersection(
						closestIntersectionAndDistance,
						sceneObjectPrimitive,	// new SceneObjectPrimitiveInverse(sceneObjectPrimitive),
						ray,
						excludeObject
					);

		// ... and the shadow-throwing cut scene-object primitives
		if(cutSceneObjectPrimitives != null)
		for(SceneObjectPrimitive sceneObjectPrimitive:cutSceneObjectPrimitives) 
			if(sceneObjectPrimitive.isShadowThrowing())
				lookForBetterIntersection(closestIntersectionAndDistance, sceneObjectPrimitive, ray, excludeObject);

		return closestIntersectionAndDistance.intersection;    
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.SceneObjectContainer#transform(optics.raytrace.Transformation)
	 */
	@Override
	public SceneObjectPrimitiveIntersection transform(Transformation t)
	{
		SceneObjectPrimitiveIntersection transformed = new SceneObjectPrimitiveIntersection(description + "(transformed)", getParent(), getStudio());
		
		// add the transformed visible positive...
		if(positiveSceneObjectPrimitives != null)
		for(SceneObjectPrimitive sceneObjectPrimitive:positiveSceneObjectPrimitives)
			addPositiveSceneObjectPrimitive(sceneObjectPrimitive.transform(t));
		
		// ... and negative scene-object primitives
		if(negativeSceneObjectPrimitives != null)
		for(SceneObjectPrimitive sceneObjectPrimitive:negativeSceneObjectPrimitives)
			addNegativeSceneObjectPrimitive(sceneObjectPrimitive.transform(t));

		// add the transformed invisible positive...
		if(invisiblePositiveSceneObjectPrimitives != null)
		for(SceneObjectPrimitive sceneObjectPrimitive:invisiblePositiveSceneObjectPrimitives)
			addInvisiblePositiveSceneObjectPrimitive(sceneObjectPrimitive.transform(t));
		
		// ... and negative scene-object primitives
		if(invisibleNegativeSceneObjectPrimitives != null)
		for(SceneObjectPrimitive sceneObjectPrimitive:invisibleNegativeSceneObjectPrimitives)
			addInvisibleNegativeSceneObjectPrimitive(sceneObjectPrimitive.transform(t));

		// add the transformed cut scene-object primitives
		if(cutSceneObjectPrimitives != null)
		for(SceneObjectPrimitive sceneObjectPrimitive:cutSceneObjectPrimitives)
			addCutSceneObjectPrimitive(sceneObjectPrimitive.transform(t));
		
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
	 * Here, return only the *visible* scene-object primitives
	 */
	@Override
	public ArrayList<SceneObjectPrimitive> getSceneObjectPrimitives()
	{
		ArrayList<SceneObjectPrimitive> SOPs = new ArrayList<SceneObjectPrimitive>();
		
		if(positiveSceneObjectPrimitives != null) SOPs.addAll(positiveSceneObjectPrimitives);
		if(negativeSceneObjectPrimitives != null) SOPs.addAll(negativeSceneObjectPrimitives);	// TODO add the SceneObjectPrimitiveInverse of each of these
		// SOPs.addAll(invisiblePositiveSceneObjectPrimitives);
		// SOPs.addAll(invisibleNegativeSceneObjectPrimitives);
		if(cutSceneObjectPrimitives != null) SOPs.addAll(cutSceneObjectPrimitives);

		return SOPs;
	}
	
	@Override
	public String getType()
	{
		return "Intersection";
	}
}
