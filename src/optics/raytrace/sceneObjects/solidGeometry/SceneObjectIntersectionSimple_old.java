package optics.raytrace.sceneObjects.solidGeometry;

import java.io.Serializable;

import optics.raytrace.core.Ray;
import optics.raytrace.core.RaySceneObjectIntersection;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.SceneObjectPrimitive;
import optics.raytrace.core.Studio;
import optics.raytrace.core.Transformation;
import optics.raytrace.utility.CopyModeType;
import math.Vector3D;

/**
 * Intersection between two or more SceneObjects.
 * (Used to be called "SceneObjectIntersection", but that is now a different class -- see below)
 * 
 * Note that SceneObjectIntersection can treat more complex scene-object intersections involving 
 * inverse objects ("negative" scene-objects), invisible objects, etc.  It also works more reliably!
 * 
 * @author Johannes Courtial
 */ 
public class SceneObjectIntersectionSimple_old extends SceneObjectContainer implements Serializable
{
	private static final long serialVersionUID = 4130876508581884349L;

	/**
	 * Create an empty intersection.
	 * Scene objects can be added later using the add(SceneObject) method.
	 * 
	 * @param description
	 * 
	 * @see optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer#getSumWith(optics.raytrace.core.SceneObject)
	 */
	public SceneObjectIntersectionSimple_old(String description, SceneObject parent, Studio studio)
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
	public SceneObjectIntersectionSimple_old(String description, SceneObject s1, SceneObject s2, SceneObject parent, Studio studio)
	{
		super(description, parent, s1, s2, studio);
	}
	
	public SceneObjectIntersectionSimple_old(SceneObjectContainer container, CopyModeType copyMode)
	{
		super(container, copyMode);
	}

	public SceneObjectIntersectionSimple_old(SceneObjectContainer container)
	{
		this(container, CopyModeType.CLONE_DATA);
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.sceneObjects.SceneObjectContainer#clone()
	 */
	@Override
	public SceneObjectIntersectionSimple_old clone()
	{
		return new SceneObjectIntersectionSimple_old(this, CopyModeType.CLONE_DATA);
	}
	
	/**
	 * Checks if the position p is inside all scene objects with index j!=i.
	 * (This method gets called in getClosestRayIntersection when a ray
	 * intersects with scene object #i at position p, and the program then
	 * needs to find out if p is inside all the other scene objects.)
	 * 
	 * @param p	position
	 * @param interpupillaryDistance	index that doesn't need checking
	 * @return	true if p is inside all scene objects other than the one with index i
	 */
	private boolean insideObjects(Vector3D p, int excludeIndex)
	{
		// go through all intersecting scene objects...
		for(int j=0; j<sceneObjects.size(); j++)
		{
			// ... apart from the one with index <excludeIndex>
			if(j != excludeIndex)
			{
				// if p is not inside any of them then it's not in the intersection
				if(!sceneObjects.get(j).insideObject(p)) return false;
			}
		}
		
		// this point is reached only if p has not been outside any of the scene objects,
		// so it's been inside all of them
		return true;
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.SceneObjectContainer#getClosestRayIntersectionAvoidingOrigin(optics.raytrace.Ray, optics.raytrace.SceneObject)
	 */
	@Override
	public RaySceneObjectIntersection getClosestRayIntersectionAvoidingOrigin(Ray ray, SceneObjectPrimitive excludeObject)
	{
		RaySceneObjectIntersection intersection, intersectionClosest;
		double distance2, distanceClosest2;	// distances squared
		
		// System.out.println("SceneObjectIntersection::getClosestRayIntersectionAvoidingOrigin: start...");
	
		// initialise the information about the closest intersection so far
		intersectionClosest = RaySceneObjectIntersection.NO_INTERSECTION;
		distanceClosest2 = Double.POSITIVE_INFINITY;
		
		// go through all the intersecting scene objects
		for(int i=0; (i<sceneObjects.size()) && visibilities.get(i); i++)
		{
			// calculate the intersection point
			intersection = sceneObjects.get(i).getClosestRayIntersectionAvoidingOrigin(ray, excludeObject);

			// is there an intersection point?
			while(intersection != RaySceneObjectIntersection.NO_INTERSECTION)
			{
				// yes

				// check that the intersection point is inside all other intersecting scene objects			
				if(insideObjects(intersection.p, i))
				{
					// yes

					// calculate the distance (squared) to the intersection point
					distance2 = intersection.p.getDifferenceWith(ray.getP()).getModSquared();
					
					// is the distance to this intersection point smaller than the smallest one so far?
					if(distance2 < distanceClosest2)
					{
						// yes, so make the new intersection point the closest one so far
						distanceClosest2 = distance2;
						intersectionClosest = intersection;
					}
					
					intersection = RaySceneObjectIntersection.NO_INTERSECTION;	// stop looking for intersections with this object
				}
				else
				{
					// try the next intersection with the same object
					intersection = sceneObjects.get(i).getNextClosestRayIntersectionAvoidingOrigin(ray, excludeObject, intersection);
//					intersection = sceneObjects.get(i).getNextClosestRayIntersection(ray, intersection);
				}
			}
		}

		// System.out.println("SceneObjectIntersection::getClosestRayIntersectionAvoidingOrigin: ...finish");

		return intersectionClosest;    
	}
	
	/* (non-Javadoc)
	 * @see optics.raytrace.SceneObjectContainer#getClosestRayIntersectionAvoidingOrigin(optics.raytrace.Ray, optics.raytrace.SceneObject)
	 */
	@Override
	public RaySceneObjectIntersection getClosestRayIntersectionWithShadowThrowingSceneObjectAvoidingOrigin(Ray ray, SceneObjectPrimitive excludeObject)
	{
		RaySceneObjectIntersection intersection, intersectionClosest;
		double distance2, distanceClosest2;	// distances squared
	
		// initialise the information about the closest intersection so far
		intersectionClosest = RaySceneObjectIntersection.NO_INTERSECTION;
		distanceClosest2 = Double.POSITIVE_INFINITY;
		
		// go through all the intersecting scene objects
		for(int i=0; (i<sceneObjects.size()) && visibilities.get(i); i++)
		{
			// calculate the intersection point
			intersection = sceneObjects.get(i).getClosestRayIntersectionWithShadowThrowingSceneObjectAvoidingOrigin(ray, excludeObject);

			// is there an intersection point?
			while(intersection != RaySceneObjectIntersection.NO_INTERSECTION)
			{
				// yes

				// check that the intersection point is inside all other intersecting scene objects			
				if(insideObjects(intersection.p, i))
				{
					// yes
					
					// calculate the distance (squared) to the intersection point
					distance2 = intersection.p.getDifferenceWith(ray.getP()).getModSquared();
					
					// is the distance to this intersection point smaller than the smallest one so far?
					if(distance2 < distanceClosest2)
					{
						// yes, so make the new intersection point the closest one so far
						distanceClosest2 = distance2;
						intersectionClosest = intersection;
					}
					
					intersection = RaySceneObjectIntersection.NO_INTERSECTION;	// stop looking for intersections with this object
				}
				else
				{
					// try the next intersection with the same object
					intersection = sceneObjects.get(i).getNextClosestRayIntersectionWithShadowThrowingSceneObjectAvoidingOrigin(ray, excludeObject, intersection);
//					intersection = sceneObjects.get(i).getNextClosestRayIntersection(ray, intersection);
				}
			}
		}

		return intersectionClosest;    
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.SceneObjectContainer#transform(optics.raytrace.Transformation)
	 */
	@Override
	public SceneObjectIntersectionSimple_old transform(Transformation t)
	{
		SceneObjectIntersectionSimple_old soc = new SceneObjectIntersectionSimple_old(description + "(transformed)", getParent(), getStudio());
		
		for(int i=0; i<sceneObjects.size(); i++)
		{
			soc.addSceneObject(sceneObjects.get(i).transform(t), visibilities.get(i));
		}
		
		return soc;
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.SceneObjectContainer#insideObject(optics.raytrace.Vector3D)
	 */
	@Override
	public boolean insideObject(Vector3D p)
	{
		// p has to be inside *all* intersecting scene objects
		return insideObjects(p, -1);
	}
	
	@Override
	public String getType()
	{
		return "Intersection";
	}
}
