package optics.raytrace.sceneObjects.solidGeometry;

import java.io.Serializable;
import java.util.ArrayList;

import optics.raytrace.core.Ray;
import optics.raytrace.core.RaySceneObjectIntersection;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.SceneObjectPrimitive;
import optics.raytrace.core.Studio;
import optics.raytrace.core.Transformation;
import optics.raytrace.utility.CopyModeType;
import math.Vector3D;

/**
 * An intersection of scene objects that acts as a combined scene object.
 * 
 * A cut-down (and differently organised) version of SceneObjectIntersection.
 * 
 * Any SceneObjects added to this collection are treated as positiveSceneObjects in SceneObjectIntersection.
 * (There are no negative SceneObjects, nor are there invisible SceneObjects etc.)
 * 
 * The combined scene object is the intersection of all scene objects in the collection.
 * This means that for a point to lie inside the combined scene object it has to lie inside every single scene object.
 * 
 * For a point to lie on the surface of the combined scene object, it must lie on the surface of one scene object
 * and it must lie inside all other scene objects.
 * 
 * @author Johannes Courtial
 */ 
public class SceneObjectIntersectionSimple extends SceneObjectContainer
implements Serializable
{
	private static final long serialVersionUID = 2919477640501641427L;

	/**
	 * Create an empty scene-object intersection.
	 * Scene objects can be added later using the addSceneObject method.
	 * 
	 * @param description
	 */
	public SceneObjectIntersectionSimple(String description, SceneObject parent, Studio studio)
	{
		super(description, parent, studio);
	}
	
	/**
	 * Create an intersection between two scene objects.
	 * More scene objects can be added later using the add(SceneObject) method
	 * 
	 * @param description
	 * @param s1	scene object 1
	 * @param s2	scene object 2
	 */
	public SceneObjectIntersectionSimple(String description, SceneObject s1, SceneObject s2, SceneObject parent, Studio studio)
	{
		super(description, parent, s1, s2, studio);
	}


	/**
	 * @param original
	 * @param copyMode one of SHARE_DATA or CLONE_DATA
	 */
	public SceneObjectIntersectionSimple(SceneObjectContainer original, CopyModeType copyMode)
	{
		super(original.getDescription(), original.getParent(), original.getStudio());
		
		// either share or clone the data, depending on copyMode
		switch(copyMode)
		{
		case SHARE_DATA:
			// use the same vectors
			this.sceneObjects = original.sceneObjects;
			this.visibilities = original.visibilities;
			this.visibilitiesWhenTrajectoryTracing = original.visibilitiesWhenTrajectoryTracing;
			break;
		case CLONE_DATA:
		default:
			// use copies of everything
			sceneObjects=new ArrayList<SceneObject>();
			visibilities = new ArrayList<Boolean>();
			visibilitiesWhenTrajectoryTracing = new ArrayList<Boolean>();

			// copy clones of all the scene objects
			for(int i=0; i<original.getNumberOfSceneObjects(); i++)
			{
				addSceneObject(original.getSceneObject(i).clone(), original.isSceneObjectVisible(i));
			}
		}
	}
	
	/**
	 * Create a shallow copy of <original>
	 * @param original
	 */
	public SceneObjectIntersectionSimple(SceneObjectIntersectionSimple original)
	{
		this(original, CopyModeType.SHARE_DATA);
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public SceneObjectIntersectionSimple clone()
	{
		return new SceneObjectIntersectionSimple(this, CopyModeType.SHARE_DATA);
	}


	
	//
	// setters & getters
	//
	
	
	/**
	 * @param raySceneObjectIntersection
	 * @return	true if the position associated with the ray-scene-object intersection lies inside all scene objects other than the one involved in the intersection
	 */
	private boolean insideAllSceneObjects(RaySceneObjectIntersection raySceneObjectIntersection)
	{
		// let <sceneObject> be in turn every one of the scene objects
		for(SceneObject sceneObject:sceneObjects)
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
		
		// the raySceneObjectIntersection lies inside all scene objects (other than the one that was involved in the intersection)
		return true;
	}

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
					if(insideAllSceneObjects(intersection))
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
		
		// go through the sceneObjects
		if(sceneObjects != null)
		for(SceneObject sceneObject:sceneObjects) 
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
	public SceneObjectIntersectionSimple transform(Transformation t)
	{
		SceneObjectIntersectionSimple transformed = new SceneObjectIntersectionSimple(description + "(transformed)", getParent(), getStudio());
		
		// add the transformed scene objects
		if(sceneObjects != null)
		for(SceneObject sceneObject:sceneObjects)
			transformed.addSceneObject(sceneObject.transform(t));
				
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
		return insideAllSceneObjects(raySceneObjectIntersection);
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
		return "Intersection (simple)";
	}
}
