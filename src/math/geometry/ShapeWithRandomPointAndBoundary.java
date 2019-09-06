package math.geometry;

import java.io.Serializable;

import math.Vector3D;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.Transformation;

/**
 * @author gpf07
 */

public interface ShapeWithRandomPointAndBoundary extends Serializable
{
	/**
	 * @return a random point on this shape (for the purposes of sampling the aperture of a Fresnel lens into which it is to be cut down
	 * so that one can compute the maximum and minimum focal lengths needed to cover the aperture of the Fresnel lens with lens sections
	 * but not more.)
	 */
	
	public Vector3D getRandomPointOnShape();
	
	/**
	 * @return the boundary of the shape (a surface perpendicular to the shape, enclosing the shape) as a SceneObject. This is used by @see FresnelLensShaped
	 * to cut down the lens sections into an aperture of the desired shape.
	 */
	
	public SceneObject getBoundary(double boundaryLength);
	
	/**
	 * @param t
	 * @return	The ShapeWithRandomPointAndBoundary, transformed according to a geometrical transformation.
	 */
	public ShapeWithRandomPointAndBoundary transform(Transformation t);
}
