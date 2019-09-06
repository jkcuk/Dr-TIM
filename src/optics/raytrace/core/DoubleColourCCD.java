package optics.raytrace.core;

import java.io.*;
import java.awt.image.*;
import javax.imageio.*; 

import optics.DoubleColour;
import math.Vector3D;

/**
 * Represents a light detector array, like a CCD.  The provided functionality allows for the
 * tracing of an image as well as for the resultant image to be saved to file.  Key to
 * the functionality is the implementation of the calculateColour method provided by child
 * classes of this.
 */
public class DoubleColourCCD implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8414451097193426750L;

	private Vector3D
		cornerPosition;	// position of corner of detector

	protected Vector3D
		horizontalSpanVector3D,	// Vector3D that spans the detector in the horizontal direction
		verticalSpanVector3D;		// Vector3D that spans the detector in the vertical direction

	protected int 
		detectorPixelsHorizontal,	// number of pixels in the horizontal direction
		detectorPixelsVertical;		// number of pixels in the vertical direction
	
	private DoubleColour[][]
		image;	// the image "recorded" by the detector
	
	/**
	 * Construct a detector array from its position, the orientation of the 
	 * detector is then given by the horizontal and Vector3D spans.  
	 * A corresponding image pixellation is also stored in terms of 
	 * detectorPixelsHorizontal and detectorPixelsVertical respectively and
	 * gives the resolution at which the image is rendered.
	 * @param cornerPosition
	 * @param horizontalSpanVector3D This orientates the horizontal within any rendered image.
	 * @param verticalSpanVector3D This orientates the vertical within any rendered image.
	 * @param detectorPixelsHorizontal The number of horizontal pixels in the scene.
	 * @param detectorPixelsVertical The number of vertical pixels in the scene.
	 */
	public DoubleColourCCD(Vector3D cornerPosition, Vector3D horizontalSpanVector3D, Vector3D verticalSpanVector3D,
			int detectorPixelsHorizontal, int detectorPixelsVertical) {

		this.cornerPosition = cornerPosition;
		this.horizontalSpanVector3D = horizontalSpanVector3D;
		this.verticalSpanVector3D = verticalSpanVector3D;
		this.detectorPixelsHorizontal = detectorPixelsHorizontal;
		this.detectorPixelsVertical = detectorPixelsVertical;
	}

	/**
	 * A default constructor of this form is required in order for child
	 * classes to implement the Serializable interface.
	 */
	protected DoubleColourCCD() {
		super();
	}

	/////////////////////////
	// GET AND SET METHODS //
	/////////////////////////
	
	/**
	 * Get the position of the detector centre.  
	 * The horizontal and vertical span Vector3Ds have to be set for this to work properly.
	 * @return	the position of the centre of the detector
	 */
	public Vector3D getCentrePosition() {
		return cornerPosition.getSumWith(
				horizontalSpanVector3D.getProductWith(0.5)).getSumWith(
						verticalSpanVector3D.getProductWith(0.5));
	}

	/**
	 * Set the position of the detector centre.  
	 * The horizontal and vertical span Vector3Ds have to be set for this to work properly.
	 * @return	nothing
	 */
	public void setCentrePosition(Vector3D centrePosition) {
		cornerPosition = centrePosition.getSumWith(
				horizontalSpanVector3D.getProductWith(-0.5)).getSumWith(
						verticalSpanVector3D.getProductWith(-0.5));
	}

	public Vector3D getCornerPosition() {
		return cornerPosition;
	}

	public void setCornerPosition(Vector3D cornerPosition) {
		this.cornerPosition = cornerPosition;
	}

	public DoubleColour[][] getImage() {
		return image;
	}
	
	public DoubleColour getPixelColour(int i, int j)
	{
		return image[i][j];
	}

	public void setPixelColour(int i, int j, DoubleColour c)
	{
		image[i][j] = c;
	}

	/**
	 * Translates the (previously calculated) image into a BufferedImage.
	 * 
	 * @return the image
	 */
	public BufferedImage getBufferedImage() {
		BufferedImage bImage = new BufferedImage(
				getDetectorPixelsHorizontal(), 
				getDetectorPixelsVertical(), 
				BufferedImage.TYPE_INT_RGB);

		for (int j=0; j<getDetectorPixelsVertical(); j++) {
			for (int i=0; i<getDetectorPixelsHorizontal(); i++) {
				bImage.setRGB(i, j, image[i][j].getRGB());
			}
		}
		return bImage;
	}

	public void setImage(DoubleColour[][] image) {
		this.image = image;
	}

	public Vector3D getHorizontalSpanVector3D() {
		return horizontalSpanVector3D;
	}

	public void setHorizontalSpanVector3D(Vector3D horizontalSpanVector3D) {
		this.horizontalSpanVector3D = horizontalSpanVector3D;
	}

	public Vector3D getVerticalSpanVector3D() {
		return verticalSpanVector3D;
	}

	public void setVerticalSpanVector3D(Vector3D verticalSpanVector3D) {
		this.verticalSpanVector3D = verticalSpanVector3D;
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


	///////////////////
	// OTHER METHODS //
	///////////////////

	/**
	 * get a pixel's 3D position
	 * @param i The horizontal distance in pixels.
	 * @param j The vertical distance in pixels.
	 */
	public Vector3D pixelPosition(int i, int j)
	{
		return cornerPosition.getSumWith(
				horizontalSpanVector3D.getProductWith(i/(detectorPixelsHorizontal-1.0))
		).getSumWith(
				verticalSpanVector3D.getProductWith(j/(detectorPixelsVertical-1.0))
		);
//		double width = i/(detectorPixelsHorizontal-1.0);
//		double height = j/(detectorPixelsVertical-1.0);
//		Vector3D rightShift = horizontalSpanVector3D.multiply(width);
//		Vector3D upShift = verticalSpanVector3D.multiply(height);
//		return getCornerPosition().add(rightShift).add(upShift);
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
						DoubleColour doubleColour = image[i][j];
						printStream.printf("%13.9e, %13.9e, %13.9e\n", 
								doubleColour.getR(), 
								doubleColour.getG(), 
								doubleColour.getB());
					}
					System.out.printf("\rheight = %5d / %5d.\t", j, getDetectorPixelsVertical());
					printStream.println();
				}
				printStream.close();
				System.out.println("\rdone.\t\t\t\t\t\t");
			} else
			{
				File outputfile = new File(filename);
				ImageIO.write(getBufferedImage(), format, outputfile);
			}
		} catch (IOException e) {
			System.err.println("CCD::saveImage::Error saving image");
		}
	}
}
