package optics.raytrace.sceneObjects.solidGeometry;

import optics.raytrace.core.RaySceneObjectIntersection;

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
