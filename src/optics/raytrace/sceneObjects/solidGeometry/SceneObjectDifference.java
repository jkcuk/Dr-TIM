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
 * The first scene object minus the other scene objects
 * 
 * @author Johannes Courtial
 */ 
public class SceneObjectDifference extends SceneObjectContainer implements Serializable
{
	private static final long serialVersionUID = 5602983618234844713L;

	/**
	 * Create an empty intersection.
	 * Scene objects can be added later using the add(SceneObject) method.
	 * 
	 * @param description
	 * 
	 * @see optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer#getSumWith(optics.raytrace.core.SceneObject)
	 */
	public SceneObjectDifference(String description, SceneObject parent, Studio studio)
	{
		super(description, parent, studio);
	}

	/**
	 * Create the difference between two scene objects.
	 * More scene objects can be added later using the add(SceneObject) method
	 * 
	 * @param description
	 * @param s1	scene object 1
	 * @param s2	scene object 2
	 */
	public SceneObjectDifference(String description, SceneObject s1, SceneObject s2, SceneObject parent, Studio studio)
	{
		super(description, parent, s1, s2, studio);
	}
	
	public SceneObjectDifference(SceneObjectContainer container, CopyModeType copyMode)
	{
		super(container, copyMode);
	}
	
	/* (non-Javadoc)
	 * @see optics.raytrace.sceneObjects.SceneObjectContainer#clone()
	 */
	@Override
	public SceneObjectDifference clone()
	{
		return new SceneObjectDifference(this, CopyModeType.CLONE_DATA);
	}
	
	/**
	 * Checks if the position p is inside the scene object with index j=0, j!=i.
	 * (The first object is the "plus" object; all the others get taken away from it -- they are "minus" objects.)
	 * (This method gets called in getClosestRayIntersection when a ray
	 * intersects with scene object #i at position p, and the program then
	 * needs to find out if p is inside the first, "plus", scene objects.)
	 * 
	 * @param p	position
	 * @param excludeIndex	index that doesn't need checking
	 * @return	true if p is inside the scene object with index 0, provided excludeIndex != 0
	 */
	private boolean insidePlusObject(Vector3D p, int excludeIndex)
	{
		if(excludeIndex != 0)
		{
			return sceneObjects.get(0).insideObject(p);
		}
		else
		{
			//
			return true;
		}
	}
	
	/**
	 * Checks if the position p is outside all scene objects with index j!=i, j>=1.
	 * (The first object is the "plus" object; all the others get taken away from it -- they are "minus" objects.)
	 * (This method gets called in getClosestRayIntersection when a ray
	 * intersects with scene object #i at position p, and the program then
	 * needs to find out if p is inside all the other scene objects.)
	 * 
	 * @param p	position
	 * @param excludeIndex	index that doesn't need checking
	 * @return	true if p is outside all scene objects with j>=1 other than the one with index i
	 */
	private boolean outsideMinusObjects(Vector3D p, int excludeIndex)
	{
		// go through all intersecting scene objects...
		for(int j=1; j<sceneObjects.size(); j++)
		{
			// ... apart from the one with index <excludeIndex>
			if(j != excludeIndex)
			{
				// if p is not inside any of them then it's not in the intersection
				if(sceneObjects.get(j).insideObject(p)) return false;
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

				// check that the intersection point is on the surface, i.e.
				// inside the first ("plus") object and outside all other ("minus") objects
				if(insidePlusObject(intersection.p, i) && outsideMinusObjects(intersection.p, i))
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
		
		// System.out.println("SceneObjectIntersection::getClosestRayIntersectionAvoidingOrigin: start...");
	
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

				// check that the intersection point is on the surface, i.e.
				// inside the first ("plus") object and outside all other ("minus") objects
				if(insidePlusObject(intersection.p, i) && outsideMinusObjects(intersection.p, i))
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

		return intersectionClosest;    
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.SceneObjectContainer#transform(optics.raytrace.Transformation)
	 */
	@Override
	public SceneObjectDifference transform(Transformation t)
	{
		SceneObjectDifference soc = new SceneObjectDifference(description + "(transformed)", getParent(), getStudio());
		
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
		return insidePlusObject(p, -1) && outsideMinusObjects(p, -1);
	}
	
	@Override
	public String getType()
	{
		return "Difference";
	}
}
