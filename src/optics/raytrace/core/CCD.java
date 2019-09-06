package optics.raytrace.core;

import java.io.*;
import java.awt.image.*;
import javax.imageio.*; 

import optics.DoubleColour;
import optics.raytrace.sceneObjects.ParametrisedParallelogram;
import optics.raytrace.surfaces.SurfaceColour;

import math.Vector3D;

/**
 * Represents a rectangular light detector array, like a CCD.
 * The provided functionality allows for the raytracing of an image as well as for the
 * resultant image to be saved to file.
 * Key to the functionality is the implementation of the calculateColour method provided by child
 * classes of this class.
 */
public class CCD extends ParametrisedParallelogram
implements Serializable, Cloneable
{
	private static final long serialVersionUID = 4989170084261233448L;

	protected int 
		detectorPixelsHorizontal = 1,	// number of pixels in the horizontal direction
		detectorPixelsVertical = 1;		// number of pixels in the vertical direction
	
	private BufferedImage
		image;	// the image "recorded" by the detector
	
	/**
	 * Construct a detector array from its position, the orientation of the 
	 * detector is then given by the horizontal and Vector3D spans.  
	 * A corresponding image pixellation is also stored in terms of 
	 * detectorPixelsHorizontal and detectorPixelsVertical respectively and
	 * gives the resolution at which the image is rendered.
	 * @param topLeftCorner	top left corner position
	 * @param horizontalSpanVector vector spanning the width of the detector, from left to right
	 * @param verticalSpanVector vector spanning the height of the detector, from top to bottom
	 * @param detectorPixelsHorizontal The number of horizontal pixels in the scene.
	 * @param detectorPixelsVertical The number of vertical pixels in the scene.
	 */
	public CCD(
			Vector3D topLeftCorner,
			Vector3D horizontalSpanVector, Vector3D verticalSpanVector,
			int detectorPixelsHorizontal, int detectorPixelsVertical)
	{
		super(	"CCD",	// description
				topLeftCorner,	// corner position
				horizontalSpanVector,	// span vector 1
				verticalSpanVector,	// span vector 2
				SurfaceColour.BLACK_SHINY,	// surface property
				(SceneObject)null,	// parent
				(Studio)null	// studio
			);

		setDetectorPixelsHorizontal(detectorPixelsHorizontal);
		setDetectorPixelsVertical(detectorPixelsVertical);
	}

	/**
	 * A default constructor of this form is required in order for child
	 * classes to implement the Serializable interface.
	 */
//	protected CCD() {
//		super();
//	}
	
	/**
	 * Create a clone of the original.
	 * Doesn't clone the image.
	 * @param original
	 */
	public CCD(CCD original)
	{
		super(original);
		
		this.setDetectorPixelsHorizontal(original.getDetectorPixelsHorizontal());
		this.setDetectorPixelsVertical(original.getDetectorPixelsVertical());
		setImage(original.getImage());	// don't clone -- copy
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public CCD clone()
	{
		return new CCD(this);
	}

	/////////////////////////
	// GET AND SET METHODS //
	/////////////////////////
	
	public BufferedImage getImage() {
		return image;
	}
	
	public int getPixelColour(int i, int j)
	{
		return image.getRGB(i, j);
	}

	public void setPixelColour(int i, int j, int rgb)
	{
		image.setRGB(i, j, rgb);
	}

	public void setImage(BufferedImage image) {
		this.image = image;
	}

	public Vector3D getHorizontalSpanVector() {
		return getSpanVector1();
	}

	public Vector3D getVerticalSpanVector() {
		return getSpanVector2();
	}

	public int getDetectorPixelsHorizontal() {
		return detectorPixelsHorizontal;
	}

	public void setDetectorPixelsHorizontal(int detectorPixelsHorizontal) {
		this.detectorPixelsHorizontal = detectorPixelsHorizontal;
	}

	public int getDetectorPixelsVertical() {
		return detectorPixelsVertical;
	}

	public void setDetectorPixelsVertical(int detectorPixelsVertical) {
		this.detectorPixelsVertical = detectorPixelsVertical;
	} 
	
	/**
	 * allocate memory for the image
	 */
	public void allocateImageMemory()
	{
		image = new BufferedImage(
				detectorPixelsHorizontal,
				detectorPixelsVertical,
				BufferedImage.TYPE_INT_RGB
			);
		for (int j=0; j<getDetectorPixelsVertical(); j++) {
			for (int i=0; i<getDetectorPixelsHorizontal(); i++)
			{
				setPixelColour(i, j, DoubleColour.GREY80.getRGB());
			}
		}
	}


	///////////////////
	// OTHER METHODS //
	///////////////////

	/**
	 * get a random 3D position of a given pixel's area; allows anti-aliasing...
	 * @param i The horizontal distance in pixels.
	 * @param j The vertical distance in pixels.
	 */
	public Vector3D getPositionOnPixel(double i, double j)
	{
		return getPixelCentrePosition(i+Math.random()-0.5, j+Math.random()-0.5);
//		return getCorner().getSumWith(
//				getHorizontalSpanVector().getProductWith(((double)i)/(getDetectorPixelsHorizontal()-1.0))
//		).getSumWith(
//				getVerticalSpanVector().getProductWith(((double)j)/(getDetectorPixelsVertical()-1.0))
//		);
	}
	
	/**
	 * get the 3D position of a pixel's centre
	 * @param i The horizontal distance in pixels.
	 * @param j The vertical distance in pixels.
	 */
	public Vector3D getPixelCentrePosition(double i, double j)
	{
		return getPointForSurfaceCoordinates(
				i/(getDetectorPixelsHorizontal()-1.0),
				j/(getDetectorPixelsVertical()-1.0)
			);
//		return getCorner().getSumWith(
//				getHorizontalSpanVector().getProductWith(((double)i)/(getDetectorPixelsHorizontal()-1.0))
//		).getSumWith(
//				getVerticalSpanVector().getProductWith(((double)j)/(getDetectorPixelsVertical()-1.0))
//		);
	}
	
	public double getAspectRatio()
	{
		return (double)detectorPixelsHorizontal / (double)detectorPixelsVertical;
	}

	/**
	 * This method will save the detector image in a given format.
	 * Possible formats include all those mentioned in javax.imageio.ImageIO.write,
	 * plus CSV (comma-separated).
	 * @param filename The name of the file that the image is saved as.
	 * @param format The format of the image.
	 */
	public void saveImage(String filename, String format) {
		try {
			if(format == "CSV") {
				FileOutputStream fileOutputStream = new FileOutputStream(filename);
				PrintStream printStream = new PrintStream(fileOutputStream);
				System.out.println("saving a CSV file..." + toString());
				for (int j=0; j<getDetectorPixelsVertical(); j++) {
					for (int i=0; i<getDetectorPixelsHorizontal(); i++) {
						int c = image.getRGB(i, j);
						printStream.printf("%d, %d, %d\n", 
								((c >> 16) & 255),	// red
								((c >> 8) & 255),	// green
								(c & 255));	// blue
					}
					System.out.printf("\rheight = %5d / %5d.\t", j, getDetectorPixelsVertical());
					printStream.println();
				}
				printStream.close();
				System.out.println("\rdone.\t\t\t\t\t\t");
			} else
			{
				File outputfile = new File(filename);
				ImageIO.write(image, format, outputfile);
			}
		} catch (IOException e) {
			System.err.println("CCD::saveImage::Error saving image");
		}
	}
}
