package optics.raytrace.GUI.core;

import optics.raytrace.core.Ray;

/**
 * 
 * 
 * @author Johannes Courtial
 *
 */
public interface CameraWithRayForImagePixel // extends Camera, IPanelComponent
{
	/**
	 * Calculate a light ray that has originated at a particular image pixel.
	 * This functionality is being used by RaytracingImageCanvas to establish which object the image shows
	 * in a particular direction.
	 * 
	 * @param imagePixelsHorizontal
	 * @param imagePixelsVertical
	 * @param i	horizontal pixel index
	 * @param j	vertical pixel index
	 * @return	light ray that originated at pixel (i, j)
	 */
	public Ray getRayForImagePixel(int imagePixelsHorizontal, int imagePixelsVertical, double i, double j);
}