package optics.raytrace.panorama;

import math.Vector3D;


/**
 * @author johannes
 * A class describing a planar screen.  This class can calculate the position of a pixel with indices <x> and <y>.
 * The functionality is pretty similar to the CCD class.
 * @see optics.raytrace.core.CCD
 */
public class Screen
{
	protected Vector3D
		topLeftCorner,	// top-left corner of the planar screen
		widthVector,	// vector from top-left corner to top-right corner of screen; basically the width of the screen
		heightVector;	// vector from top-left corner to bottom-left corner
	protected int
		pixelsH,	// number of pixels in the horizontal direction
		pixelsV;	// number of pixels in the vertical direction
	
	
	
	/**
	 * Default constructor, in which all parameters are explicitly given
	 * (and not checked for being perpendicular to each other etc.!)
	 * @param topLeftCorner	top-left corner of the (planar) screen
	 * @param widthVector	vector from top-left corner to top-right corner of screen
	 * @param heightVector	vector from top-left corner to bottom-left corner
	 * @param pixelsH	number of pixels in the horizontal direction
	 * @param pixelsV	number of pixels in the vertical direction
	 */
	public Screen(Vector3D topLeftCorner, Vector3D widthVector, Vector3D heightVector, int pixelsH, int pixelsV)
	{
		super();
		
		setTopLeftCorner(topLeftCorner);
		setWidthVector(widthVector);
		setHeightVector(heightVector);
		// set the number of pixels in the horizontal and vertical directions
		setPixelsH(pixelsH);
		setPixelsV(pixelsV);
	}
	
	/**
	 * Minimalist constructor for a screen.
	 * The screen is centred at (0, 0, 1), width vector is (1, 0, 0) and height vector is in the -y direction.
	 * @param pixelsH	number of pixels in the horizontal direction
	 * @param pixelsV	number of pixels in the vertical direction
	 */
	public Screen(int pixelsH, int pixelsV)
	{
		this(
				new Vector3D(-0.5, 0.5, 1),	// topLeftCorner
				new Vector3D(1, 0, 0), // widthVector
				new Vector3D(0, -((double)pixelsV)/(pixelsH), 0), 	// heightVector
				pixelsH, pixelsV
			);
	}	

	
	/**
	 * Constructor for a screen centred at <centre>
	 * @param centre	centre of screen
	 * @param normal	normal to the screen, facing away from viewer
	 * @param up	the "up" direction
	 * @param width	width of screen
	 * @param height	height of screen
	 * @param pixelsH	number of pixels in the horizontal direction
	 * @param pixelsV	number of pixels in the vertical direction
	 */
	public Screen(Vector3D centre, Vector3D normal, Vector3D up, double width, double height, int pixelsH, int pixelsV)
	{
		this(pixelsH, pixelsV);

		// set the screen centre and width & height vectors
		orientScreen(centre, normal, up, width, height);
	}	
		
	
	// setters & getters
		
	public Vector3D getTopLeftCorner() {
		return topLeftCorner;
	}

	public void setTopLeftCorner(Vector3D c) {
		topLeftCorner = c;
	}

	public Vector3D getWidthVector() {
		return widthVector;
	}

	public void setWidthVector(Vector3D widthVector) {
		this.widthVector = widthVector;
	}

	public Vector3D getHeightVector() {
		return heightVector;
	}

	public void setHeightVector(Vector3D heightVector) {
		this.heightVector = heightVector;
	}

	public void setPixelsH(int pixelsH) {
		this.pixelsH = pixelsH;
	}
	
	public int getPixelsH()
	{
		return pixelsH;
	}

	public void setPixelsV(int pixelsV) {
		this.pixelsV = pixelsV;
	}
	
	public int getPixelsV()
	{
		return pixelsV;
	}
	
	
	/**
	 * Orient the screen
	 * @param centre	centre of screen
	 * @param normal	normal to the screen, facing away from viewer
	 * @param up	the "up" direction
	 * @param width	width of screen
	 * @param height	height of screen
	 */
	public void orientScreen(Vector3D centre, Vector3D normal, Vector3D up, double width, double height)
	{
		// the width vector is both perpendicular to normal and up, with length w
		setWidthVector(Vector3D.crossProduct(up, normal).getWithLength(width));
		
		// the height vector is both perpendicular to W and normal
		setHeightVector(Vector3D.crossProduct(widthVector, normal).getWithLength(height));
		
		// the top-left corner of the screen is at centre - W/2 - H/2,
		// where centre = betweenTheEyes + d
		setTopLeftCorner(Vector3D.sum(centre, widthVector.getProductWith(-0.5), heightVector.getProductWith(-0.5)));
	}

	public Vector3D getCentre()
	{
		return Vector3D.sum(topLeftCorner, widthVector.getProductWith(0.5), heightVector.getProductWith(0.5));
	}
	
	
	/**
	 * @param x
	 * @param y
	 * @return	the coordinates of the pixel with indices x, y
	 */
	public Vector3D getPixelPosition(double x, double y)
	{
		// C + x/w W + y/h H
		return Vector3D.sum(topLeftCorner, widthVector.getProductWith(x/(pixelsH)), heightVector.getProductWith(y/(pixelsV)));
	}


	@Override
	public String toString() {
		return "Screen [topLeftCorner=" + topLeftCorner + ", widthVector=" + widthVector + ", heightVector=" + heightVector + ", pixelsH=" + pixelsH + ", pixelsV=" + pixelsV + "]";
	}
	
	
}
