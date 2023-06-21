package optics.raytrace.sceneObjects.solidGeometry;

import java.io.Serializable;

import math.*;
import optics.raytrace.core.Ray;
import optics.raytrace.core.RaySceneObjectIntersection;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.SceneObjectPrimitive;
import optics.raytrace.core.Studio;
import optics.raytrace.core.Transformation;
import optics.raytrace.utility.CopyModeType;

/**
 * Union of two or more SceneObjects.
 * The inside of the union is defined as the inside of any of the SceneObjects that form part of it.
 * The resulting SceneObject consists of a surface that surrounds the inside of the union; the inside itself
 * has no structure.
 * HASN'T BEEN TESTED
 * 
 * @author Johannes Courtial
 */
public class SceneObjectUnion extends SceneObjectContainer implements Serializable
{
	private static final long serialVersionUID = -3237883887395121075L;

	/**
	 * Create an empty union.
	 * Scene objects can be added later using the add(SceneObject) method.
	 * 
	 * @param description
	 * 
	 * @see optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer#getSumWith(optics.raytrace.core.SceneObject)
	 */
	public SceneObjectUnion(String description, SceneObject parent, Studio studio)
	{
		super(description, parent, studio);
	}

	/**
	 * Create a union of two scene objects.
	 * More scene objects can be added later using the add(SceneObject) method
	 * 
	 * @param description
	 * @param s1	scene object 1
	 * @param s2	scene object 2
	 */
	public SceneObjectUnion(String description, SceneObject s1, SceneObject s2, Studio studio)
	{
		super(description, s1, s2, studio);
	}
	
	/**
	 * Create a clone of the original
	 * @param original
	 */
	public SceneObjectUnion(SceneObjectContainer original, CopyModeType copyMode)
	{
		super(original, copyMode);
	}
	
	/* (non-Javadoc)
	 * @see optics.raytrace.sceneObjects.SceneObjectContainer#clone()
	 */
	@Override
	public SceneObjectUnion clone()
	{
		return new SceneObjectUnion(this, CopyModeType.CLONE_DATA);
	}

	/**
	 * Checks if the position p is inside any scene object with index j!=i.
	 * (This method gets called in getClosestRayIntersection when a ray
	 * intersects with scene object #i at position p, and the program then
	 * needs to find out if p is inside any other scene object.)
	 * 
	 * @param p	position
	 * @return	index of the first scene object p is inside of; -1 if there is none
	 */
	private int insideAnyObject(Vector3D p, int excludeIndex)
	{
		// go through all intersecting scene objects...
		for(int j=0; j<sceneObjects.size(); j++)
		{
			// ... apart from the one with index <excludeIndex>
			if(j != excludeIndex)
			{
				// if p is inside scene object #j, return j
				if(sceneObjects.get(j).insideObject(p)) return j;
			}
		}
		
		// this point is reached only if p has not been outside all of the other scene objects
		return -1;
	}

//	/* (non-Javadoc)
//	 * @see optics.raytrace.SceneObjectContainer#getClosestRayIntersection(optics.raytrace.Ray)
//	 */
//	@Override
//	public RaySceneObjectIntersection getClosestRayIntersection(Ray ray)
//	{
//		return getClosestRayIntersectionAvoidingOrigin(ray, (SceneObjectPrimitive)null);
//	}

	/* (non-Javadoc)
	 * @see optics.raytrace.SceneObjectContainer#getClosestRayIntersectionAvoidingOrigin(optics.raytrace.Ray, optics.raytrace.SceneObject)
	 */
	@Override
	public RaySceneObjectIntersection getClosestRayIntersectionAvoidingOrigin(Ray ray, SceneObjectPrimitive excludeObject)
	{
		RaySceneObjectIntersection intersection, intersectionClosest;
		double distance2, distanceClosest2;	// distances squared
		
		// initialise the information about the closest intersection so far
		intersectionClosest = RaySceneObjectIntersection.NO_INTERSECTION;
		distanceClosest2 = Double.POSITIVE_INFINITY;
		
		// go through all the intersecting scene objects
		for(int i=0; i<sceneObjects.size(); i++)
		{
			if(visibilities.get(i))
			{
				// calculate the intersection point
				intersection = sceneObjects.get(i).getClosestRayIntersectionAvoidingOrigin(ray, excludeObject);

				// is there an intersection point?
				while(intersection != RaySceneObjectIntersection.NO_INTERSECTION)
				{
					// yes

					// check that the intersection point is not inside any other scene object in this intersection (as we are interested only in intersections
					// with the outside surface of the union)
					if(insideAnyObject(intersection.p, i) == -1)
					{
						// the intersection point is not inside any other scene object

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
					}
				}
			}
		}

		return intersectionClosest;    
	}
	
	@Override
	public RaySceneObjectIntersection getClosestRayIntersectionWithShadowThrowingSceneObjectAvoidingOrigin(Ray ray, SceneObjectPrimitive excludeObject)
	{
		RaySceneObjectIntersection intersection, intersectionClosest;
		double distance2, distanceClosest2;	// distances squared
		
		// initialise the information about the closest intersection so far
		intersectionClosest = RaySceneObjectIntersection.NO_INTERSECTION;
		distanceClosest2 = Double.POSITIVE_INFINITY;
		
		// go through all the intersecting scene objects
		for(int i=0; i<sceneObjects.size(); i++)
		{
			if(visibilities.get(i))
			{
				// calculate the intersection point
				intersection = sceneObjects.get(i).getClosestRayIntersectionWithShadowThrowingSceneObjectAvoidingOrigin(ray, excludeObject);

				// is there an intersection point?
				while(intersection != RaySceneObjectIntersection.NO_INTERSECTION)
				{
					// yes

					// check that the intersection point is not inside any other scene object in this intersection (as we are interested only in intersections
					// with the outside surface of the union)
					if(insideAnyObject(intersection.p, i) == -1)
					{
						// the intersection point is not inside any other scene object

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
					}
				}
			}
		}

		return intersectionClosest;    
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.SceneObjectContainer#transform(optics.raytrace.Transformation)
	 */
	@Override
	public SceneObjectUnion transform(Transformation t)
	{
		SceneObjectUnion u = new SceneObjectUnion(description + "(transformed)", getParent(), getStudio());
		
		for(int i=0; i<sceneObjects.size(); i++)
		{
			u.addSceneObject(sceneObjects.get(i).transform(t));
		}
		
		return u;
	}
	
	@Override
	public String getType()
	{
		return "Union";
	}
}
