package optics.raytrace.surfaces;

import math.Vector3D;

/**
 * A SurfaceProperty that can find the pixel indices associated with a given position on the surface.
 * In future, extend to provide method that finds the pixel centre for given indices?
 * 
 * @author johannes
 */
public interface IndexedPixellatedSurface
{
	public int[] calculateIndicesForPosition(Vector3D position);
}
