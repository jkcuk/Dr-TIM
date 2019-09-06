package optics.raytrace.panorama;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

import optics.DoubleColour;
import optics.raytrace.panorama.panorama3DGeometry.AbstractPanorama3DGeometry;
import optics.raytrace.panorama.panorama3DGeometry.StandardPanorama3DGeometry;
import optics.raytrace.utility.MyImageIO;
import math.Vector3D;



/**
 * @author johannes
 * A pair of panoramic images, one for the left eye, the other for the right eye,
 * which together correspond to a 3D panorama.
 * There is also a description of the geometry used to create (and display) these
 * panoramas.
 */
public class Panorama3D
{
	// the 4π panoramic images for the left and right eyes
	protected Panorama2D leftPanorama, rightPanorama;
	
	/**
	 * The geometry that should be used for interpreting this Panorama3D.
	 * This geometry tells us how the two Panorama2D images for the left and right eye were calculated
	 * and how they should/can be interpreted.
	 */
	protected AbstractPanorama3DGeometry panorama3DGeometry;
	
	// default constructor
	public Panorama3D()
	{
		super();
		setLeftPanorama(new Panorama2D());
		setRightPanorama(new Panorama2D());
		setPanorama3DGeometry(new StandardPanorama3DGeometry());
	}
	
	//
	// load images
	//
	
	// prompt the user to pick two images
	// from http://docs.oracle.com/javase/tutorial/uiswing/components/filechooser.html
	
	public void selectAndLoadLeftPanorama()
	{
		leftPanorama.setImage(MyImageIO.selectAndLoadImage("Select left panoramic image (.bmp)"));
	}

	public void selectAndLoadRightPanorama()
	{
		rightPanorama.setImage(MyImageIO.selectAndLoadImage("Select right panoramic image (.bmp)"));
	}


	public Panorama2D getLeftPanorama() {
		return leftPanorama;
	}

	public void setLeftPanorama(Panorama2D leftPanorama) {
		this.leftPanorama = leftPanorama;
	}

	public Panorama2D getRightPanorama() {
		return rightPanorama;
	}

	public void setRightPanorama(Panorama2D rightPanorama) {
		this.rightPanorama = rightPanorama;
	}

	public AbstractPanorama3DGeometry getPanorama3DGeometry() {
		return panorama3DGeometry;
	}

	public void setPanorama3DGeometry(AbstractPanorama3DGeometry panorama3dGeometry) {
		panorama3DGeometry = panorama3dGeometry;
	}

	
	
	/**
	 * @return	an image that contains the left and right panoramas above each other
	 */
	public BufferedImage getTopBottomImage()
	{		
		// in case the left and right panorama images have different widths, make both of them the width of the bigger one
		int width = Math.max(leftPanorama.getImage().getWidth(), rightPanorama.getImage().getWidth());
	    BufferedImage topBottomImage = new BufferedImage(width, width, BufferedImage.TYPE_INT_RGB);

		Image 
			leftImage = leftPanorama.getImage().getScaledInstance(width, width/2, Image.SCALE_DEFAULT),
			rightImage = rightPanorama.getImage().getScaledInstance(width, width/2, Image.SCALE_DEFAULT);
		
	    Graphics2D g2d = topBottomImage.createGraphics();
	    g2d.drawImage(leftImage, 0, 0, null);
	    g2d.drawImage(rightImage, 0, width/2, null);
	    g2d.dispose();
	    
	    return topBottomImage;
	}

	
	/**
	 * @return	an anaglyph representing the Panorama3D, with the horizontal coordinate representing angle phi and the vertical coordinate representing angle theta
	 */
	public BufferedImage createSurroundAnaglyph(int anaglyphWidth, int anaglyphHeight)
	{
		BufferedImage anaglyph = new BufferedImage(
				anaglyphWidth, anaglyphHeight,
				BufferedImage.TYPE_INT_RGB
				);

		// go through all the pixels in the anaglyph image, in the outer loop the rows, in the inner loop the columns
		for (int j=0; j<anaglyphHeight; j++)
		{
			// calculate the angle theta that corresponds to row j
			double theta = Math.PI*j/anaglyphHeight;
			for (int i=0; i<anaglyphWidth; i++)
			{
				// calculate the angle phi that corresponds to column i
				double phi = 2*Math.PI*i/anaglyphWidth;
				
				// look up the colours in the left and right panoramas that correspond to the angles theta and phi
				Color
					leftColour = new Color(leftPanorama.getRGBByAngles(phi, theta)),
					rightColour = new Color(rightPanorama.getRGBByAngles(phi, theta));

				// colour version
				// anaglyph.setRGB(i, j, new Color(leftColour.getRed(), rightColour.getGreen(), rightColour.getBlue()).getRGB());

				// standard monochrome version; display left image in red, and right image in blue
				anaglyph.setRGB(i, j, new Color(DoubleColour.getLuminance(leftColour), 0, DoubleColour.getLuminance(rightColour)).getRGB());

				// Tom Tyc's monochrome version; display left image in red, and right image in green + blue;
				// this works very well with the combination of my monitor and 3D glasses
				// anaglyph.setRGB(i, j, new Color(getLuminance(leftColour), getLuminance(rightColour), getLuminance(rightColour)).getRGB());
			}
		}

		return anaglyph;
	}


	/**
	 * @param screen	geometry of the planar screen on which to project the anaglyph
	 * @return	the anaglyph, projected onto the screen
	 */
	public BufferedImage createPlanarAnaglyph(Screen screen)
	{
		int
			width = screen.getPixelsH(),
			height = screen.getPixelsV();
		
		// create a bit of space in memory for the image to go
		BufferedImage anaglyph = new BufferedImage(
				width, height,
				BufferedImage.TYPE_INT_RGB
			);

		// go through all the pixels on the (virtual) screen, j being vertical index, i being horizontal index
		for (int j=0; j<height; j++)
		{
			for (int i=0; i<width; i++)
			{
				// calculate the position of the screen pixel with those coordinates
				Vector3D pixelPosition = screen.getPixelPosition(i, j);
				
				// calculate the directions in which the two eyes see this pixel
				PhiTheta
					leftDirection = panorama3DGeometry.getAnglesForPosition(pixelPosition, Side.LEFT, screen.getCentre()),
					rightDirection = panorama3DGeometry.getAnglesForPosition(pixelPosition, Side.RIGHT, screen.getCentre());
				
				Color
					leftColour = new Color(leftPanorama.getRGBByAngles(leftDirection)),
					rightColour = new Color(rightPanorama.getRGBByAngles(rightDirection));
//					rightColour = new Color(leftPanorama.getRGBByAngles(leftDirection)),
//					leftColour = new Color(rightPanorama.getRGBByAngles(rightDirection));
				// TODO sort this out -- there is something wrong with the way the Panorama3DGeometries calculate the angles

				// colour version
				// anaglyph.setRGB(i, j, new Color(leftColour.getRed(), rightColour.getGreen(), rightColour.getBlue()).getRGB());

				// standard monochrome version; display left image in red, and right image in blue
				anaglyph.setRGB(i, j, new Color(DoubleColour.getLuminance(leftColour), 0, DoubleColour.getLuminance(rightColour)).getRGB());

				// Tom Tyc's monochrome version; display left image in red, and right image in green + blue;
				// this works very well with the combination of my monitor and 3D glasses
				// anaglyph.setRGB(i, j, new Color(getLuminance(leftColour), getLuminance(rightColour), getLuminance(rightColour)).getRGB());
			}
		}

		return anaglyph;
	}


}
