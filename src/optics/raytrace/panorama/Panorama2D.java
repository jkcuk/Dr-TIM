package optics.raytrace.panorama;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import math.MyMath;



/**
 * @author johannes
 * A 2D panoramic image, i.e. an image that is interpreted as representing
 * a range of azimuthal angles between 0 and 2π in the horizontal direction
 * and a range of polar angles between 0 and π in the vertical direction.
 */
public class Panorama2D
{
	/**
	 * The image, which is interpreted as representing
	 * a range of azimuthal angles between 0 and 2π in the horizontal direction
	 * and a range of polar angles between 0 and π in the vertical direction.
	 */
	protected BufferedImage image;
	
	// pre-calculated
	protected int imageWidth, imageHeight;
	
	
	/**
	 * default constructor; set image using setImage method
	 */
	public Panorama2D()
	{
		super();
	}
	
	/**
	 * Creates a Panorama2D object that corresponds to the given image,
	 * which is interpreted as representing
	 * a range of azimuthal angles between 0 and 2π in the horizontal direction
	 * and a range of polar angles between 0 and π in the vertical direction.
	 * @param image
	 */
	public Panorama2D(BufferedImage image)
	{
		super();
		
		setImage(image);
	}
	
	/**
	 * Creates a Panorama2D object with a given imageWidth and imageHeight.
	 * The image itself is null.
	 * @param imageWidth
	 * @param imageHeight
	 */
	public Panorama2D(int imageWidth, int imageHeight)
	{
		super();
		
		this.image = null;
		this.imageWidth = imageWidth;
		this.imageHeight = imageHeight;
	}
	

	// getters & setters
	
	public BufferedImage getImage() {
		return image;
	}
	
	// from http://stackoverflow.com/questions/3514158/how-do-you-clone-a-bufferedimage
	public static final BufferedImage clone(BufferedImage image) {
	    BufferedImage clone = new BufferedImage(image.getWidth(),
	            image.getHeight(), image.getType());
	    Graphics2D g2d = clone.createGraphics();
	    g2d.drawImage(image, 0, 0, null);
	    g2d.dispose();
	    return clone;
	}

	/**
	 * Set the image, and imageWidth & imageHeight.
	 * If image is null, set the image to null and imageWidth and imageHeight to 0.
	 * @param image
	 */
	public void setImage(BufferedImage image)
	{
		this.image = clone(image);

		if(image != null)
		{
			imageWidth = image.getWidth();
			imageHeight = image.getHeight();
		}
		else
		{
			imageWidth = 0;
			imageHeight = 0;
		}
	}
	
	//
	// conversion between angles to indices
	//
	
	public double i2phi(double i)
	{
		return 2*Math.PI*i / imageWidth;
	}
	
	public double j2theta(double j)
	{
		return Math.PI * j / imageHeight;
	}
	
	/**
	 * @param phi	the azimuthal angle
	 * @return	the corresponding horizontal index, i
	 */
	public int phi2i(double phi)
	{
		return (imageWidth>0)?MyMath.mod((int)(phi / (2*Math.PI) * imageWidth + 0.5), imageWidth):0;
	}

	/**
	 * @param theta	the polar angle
	 * @return	the corresponding vertical index, j
	 */
	public int theta2j(double theta)
	{
		return (imageHeight>0)?MyMath.mod((int)(theta / Math.PI * imageHeight + 0.5), imageHeight):0;
	}


	//
	// retrive colour corresponding to a given set of indices or directions
	//
	
	/**
	 * @param i
	 * @param j
	 * @return	the colour of image pixel (i, j), in RGB format (convert to Color using new Color(int)), 0 if image == null
	 */
	public int getRGBByIndices(int i, int j)
	{
		return (image!=null)?image.getRGB(i, j):0;
	}
	
	/**
	 * @param phi	the azimuthal angle
	 * @param theta	the polar angle
	 * @return	the colour of image pixel representing the direction (phi, theta), in RGB format (convert to Color using new Color(int))
	 */
	public int getRGBByAngles(double phi, double theta)
	{
		return getRGBByIndices(phi2i(phi), theta2j(theta));
	}
	
	public int getRGBByAngles(PhiTheta phiTheta)
	{
		return getRGBByIndices(phi2i(phiTheta.getPhi()), theta2j(phiTheta.getTheta()));
	}
}
