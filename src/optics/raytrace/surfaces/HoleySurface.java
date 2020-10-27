package optics.raytrace.surfaces;

import optics.raytrace.core.RaySceneObjectIntersection;

/**
 * @author johannes
 * If a SurfaceProperty implements this interface, then it can return a boolean value for an intersection with the surface
 * which determines whether or not the intersection is with a hole.
 */
public interface HoleySurface {
	/**
	 * does the given intersection intersect a hole?
	 * @return true if the intersection is with a hole
	 */
	public boolean isHole(RaySceneObjectIntersection i);
}
