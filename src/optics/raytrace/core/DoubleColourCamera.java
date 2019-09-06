package optics.raytrace.core;

import java.awt.image.*;

import java.io.*;

import optics.DoubleColour;
import math.Vector3D;

/**
 * Represents a camera.
 * This consists of a detector array, and a correspondence between detector-array pixels and
 * light rays, provided by the "getRayForPixel" method
 * (in a real camera, this correspondence is provided by the lens).
 * 
 * @author Johannes Courtial
 */
public abstract class DoubleColourCamera implements Serializable {
	/**
	 * automatically generated
	 */
	protected static final long serialVersionUID = -8844626291121879181L;

	protected int maxTraceLevel;
	protected DoubleColourCCD ccd;
	          
	/**
	 * Create a new camera
	 * 
	 * @param detectorCentre	centre of the detector array
	 * @param horizontalSpanVector3D	Vector3D running along the width of the detector array
	 * @param verticalSpanVector3D	Vector3D running along the height of the detector array
	 * @param detectorPixelsHorizontal	number of detector pixels in the horizontal direction
	 * @param detectorPixelsVertical	number of detector pixels in the vertical direction
	 * @param maxTraceLevel	iteration depth
	 */
	public DoubleColourCamera( Vector3D detectorCentre,
		Vector3D horizontalSpanVector3D,
		Vector3D verticalSpanVector3D,
		int detectorPixelsHorizontal,
		int detectorPixelsVertical,
		int maxTraceLevel) {
		ccd = new DoubleColourCCD(
			detectorCentre.getSumWith(
				horizontalSpanVector3D.getProductWith(-0.5)).getSumWith(
				verticalSpanVector3D.getProductWith(-0.5)),	// calculate the detector's corner position
			horizontalSpanVector3D, verticalSpanVector3D,
			detectorPixelsHorizontal, detectorPixelsVertical
		);
		this.maxTraceLevel = maxTraceLevel;
	}
      
	/**
	 * In order to implement the Serializable interface, this default constructor
	 * is required.  This is only for loading and saving to function correctly.  
	 */
	protected DoubleColourCamera() {
			ccd = new DoubleColourCCD();
	}	


	/**
	 * Calculate a light ray that has originated at a particular camera pixel.
	 * (Note that it is possible that there are different light rays that originate from the same pixel.
	 * This is the case, for example, when the aperture size is finite.)
	 * 
	 * @param i	horizontal pixel index
	 * @param j	vertical pixel index
	 * @return	light ray that originated at pixel (i, j)
	 */
	public abstract Ray getRayForPixel(int i, int j);


	public int getMaxTraceLevel() {
		return maxTraceLevel;
	}

	/**
	 * The max trace level is the number of iterations of propagation
	 * that any light ray may undergo before being ignored as insignificant
	 * or computationally too expensive.
	 * @param maxTraceLevel The number of iterations a ray may undergo
	 */
	public void setMaxTraceLevel(int maxTraceLevel) {
		this.maxTraceLevel = maxTraceLevel;
	}

	
	/**
	 * When a single pixel is to be rendered, for example when rendering 
	 * with the user interface (see RenderWorker) or when rendering a 
	 * non-rectangular image, call this method.
	 */
	public abstract DoubleColour calculatePixelColour(int i, int j, SceneObject scene, LightSource lights);

	
	/**
	 * This method iterates through every pixel in the scene, calls calculateColour 
	 * and stores each value in a buffered image which is then returned.
	 * 
	 * While this is the obvious place to render stuff, a slightly 
	 * modified version of the code is in RenderWorker for UI related
	 * purposes.   For all other cases just use this method.
	 * @return An image of the scene.
	 */
	public DoubleColour[][] takePhoto(SceneObject scene, LightSource lights) {
		ccd.setImage(new DoubleColour[ccd.getDetectorPixelsHorizontal()][ccd.getDetectorPixelsVertical()]);

		for (int j=0; j<ccd.getDetectorPixelsVertical(); j++) {
			for (int i=0; i<ccd.getDetectorPixelsHorizontal(); i++) {
				ccd.setPixelColour(i, j, calculatePixelColour(i, j, scene, lights));
			}
			System.out.printf("\rline %5d / %5d.\t", j, ccd.getDetectorPixelsVertical());
		}
		System.out.println("\rdone.\t\t\t\t\t\t");
		return ccd.getImage();
	}

	/**
	 * Save a photo that was previously taken (and which is now
	 * the detector image) in a given format.  
	 * Possible formats include all those mentioned in javax.imageio.ImageIO.write,
	 * plus CSV (comma-separated).
	 * @param filename The name of the file that the image is saved as.
	 * @param format The format of the image.
	 */
	public void savePhoto(String filename, String format) {
		ccd.saveImage(filename, format);
	}
	
	public int getDetectorPixelsHorizontal() {
		return ccd.getDetectorPixelsHorizontal();
	}

	public void setDetectorPixelsHorizontal(int detectorPixelsHorizontal)
	{
		ccd.setDetectorPixelsHorizontal(detectorPixelsHorizontal);
	}

	public int getDetectorPixelsVertical() {
		return ccd.getDetectorPixelsVertical();
	}
	
	public void setDetectorPixelsVertical(int detectorPixelsVertical)
	{
		ccd.setDetectorPixelsVertical(detectorPixelsVertical);
	}

	/**
	 * Translates the (previously calculated) image into a BufferedImage.
	 * 
	 * @return the image
	 */
	public BufferedImage getBufferedImage()
	{
		return ccd.getBufferedImage();
	}

}
