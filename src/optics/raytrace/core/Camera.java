package optics.raytrace.core;

import java.awt.image.*;

import java.io.*;

import optics.DoubleColour;
import optics.raytrace.GUI.core.*;
import optics.raytrace.exceptions.RayTraceException;

/**
 * Represents a camera.
 * 
 * @author Johannes Courtial
 */
public interface Camera extends Serializable, Cloneable
{
	/**
	 * @return this camera's description
	 */
	public String getDescription();

	/**
	 * @return	this camera's RaytraceExceptionHandler
	 */
	public RaytraceExceptionHandler getRaytraceExceptionHandler();

	/**
	 * Calculate a light ray that has originated at a particular camera pixel.
	 * (Note that it is possible that there are different light rays that originate from the same pixel.
	 * This is the case, because a light ray from a pixel can pass through any point on the pixel, and through any point on the aperture.)
	 * 
	 * @param i	horizontal pixel index
	 * @param j	vertical pixel index
	 * @return	light ray that originated at pixel (i, j)
	 */
	public Ray getRayForPixel(double i, double j);

	/**
	 * Calculate a central light ray that has originated at a particular camera pixel.
	 * This ray passes through the centre of the pixel and the centre of the aperture.
	 * 
	 * @param i	horizontal pixel index
	 * @param j	vertical pixel index
	 * @return	light ray that originated at pixel (i, j)
	 */
	public Ray getCentralRayForPixel(double i, double j);

	/**
	 * Call this to render a single pixel.
	 * @param i
	 * @param j
	 * @param scene
	 * @param lights
	 * @return the colour of pixel (i,j)
	 * @throws RayTraceException
	 */
	public DoubleColour calculatePixelColour(double i, double j, SceneObject scene, LightSource lights)
	throws RayTraceException;

	/**
	 * This method renders the entire image.
	 * It keeps raytraceWorker informed about progress.
	 * 
	 * The obvious way to implement it would, of course, involve calling the calculatePixelColour method.
	 * 
	 * @param scene
	 * @param lights
	 * @param raytraceWorker	null, if there is no RaytraceWorker
	 * @return an image of the scene
	 */
	public BufferedImage takePhoto(
			SceneObject scene,
			LightSource lights,
			RaytraceWorker raytraceWorker
		);

	/**
	 * This method renders the entire image.
	 * No RaytraceWorker is being kept informed about progress.
	 * 
	 * @param scene
	 * @param lights
	 * @return an image of the scene
	 */
	public BufferedImage takePhoto(SceneObject scene, LightSource lights);
	
	/**
	 * @return the image
	 */
	public BufferedImage getPhoto();
}
