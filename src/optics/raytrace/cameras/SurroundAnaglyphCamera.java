package optics.raytrace.cameras;

import java.io.Serializable;

import math.*;
import optics.DoubleColour;
import optics.raytrace.core.*;
import optics.raytrace.exceptions.RayTraceException;
import optics.raytrace.panorama.Panorama2D;
import optics.raytrace.panorama.Side;
import optics.raytrace.panorama.panorama3DGeometry.AbstractPanorama3DGeometry;

/**
 * Joerg and Maria would like to produce video frames for VR headsets.
 * 
 * Joerg's idea is to have a pinhole camera for each eye, and so there are two images, as usual;
 * each column represents the view along a vertical line in the centre of the field of view of the eye, for
 * one particular orientation of the head.
 * 
 * As the head rotates, the positions of the pinholes move on a circle around the between-the-eyes position.
 * The direction of the centre of an eye's field of view is given by the vector from the between-the-eyes position
 * to the eye, rotated by 90°.
 * 
 * To calculate the image that corresponds to the left/right eye, set eyeSeparation to a negative/positive value.
 * 
 * I should write a quick Java program that reads in the two images and then displays them as anaglyphs.
 * Or this could just be done in Photoshop, of course.
 * 
 * @author Johannes Courtial
 */
public class SurroundAnaglyphCamera extends CameraClass implements Serializable
{
	private static final long serialVersionUID = -7676364427905682108L;

	// everything that's needed to calculate a ray that needs to be traced for each image pixel
	protected AbstractPanorama3DGeometry panorama3DGeometry;
	
	// the different types of image this camera can produce
	public enum OutputType
	{
		LEFT_EYE("Left-eye image"),
		RIGHT_EYE("Right-eye image"),
		ANAGLYPH_REDBLUE("Red/blue anaglyph"),
		ANAGLYPH_COLOUR("Colour anaglyph");
		
		private String description;
		private OutputType(String description) {this.description = description;}	
		@Override
		public String toString() {return description;}
	}

	// the type of image this camera produces
	protected OutputType outputType;
	
	// for converting between angles and indices
	protected Panorama2D panorama2D;


	/**
	 * Create a new surround anaglyph camera
	 * 
	 * @param detectorPixelsHorizontal	number of detector pixels in the horizontal direction
	 * @param detectorPixelsVertical	number of detector pixels in the vertical direction
	 * @param maxTraceLevel
	 */
	public SurroundAnaglyphCamera(
			String description,
			AbstractPanorama3DGeometry panorama3DGeometry,
			OutputType outputType,
			int detectorPixelsHorizontal, int detectorPixelsVertical,
			int maxTraceLevel
		)
	{
		super(	description,
				Vector3D.O,	// CCD centre --- not used
				Vector3D.X,	// horizontalSpanVector --- not used
				Vector3D.Y,	// verticalSpanVector --- not used
				detectorPixelsHorizontal, detectorPixelsVertical,
				maxTraceLevel);
		
		panorama2D = new Panorama2D(detectorPixelsHorizontal, detectorPixelsVertical);
		setPanorama3DGeometry(panorama3DGeometry);
		setOutputType(outputType);
	}

	/**
	 * Create a clone of the original
	 * @param original
	 */
	public SurroundAnaglyphCamera(SurroundAnaglyphCamera original)
	{
		super(original);
		panorama2D = new Panorama2D(original.getDetectorPixelsHorizontal(), original.getDetectorPixelsVertical());
		setPanorama3DGeometry(original.getPanorama3DGeometry());
		setOutputType(original.getOutputType());
	}
	
	/* (non-Javadoc)
	 * @see optics.raytrace.cameras.Camera#clone()
	 */
	@Override
	public SurroundAnaglyphCamera clone()
	{
		return new SurroundAnaglyphCamera(this);
	}
	
	
	////////////////////////////////////////////////////////////

	public AbstractPanorama3DGeometry getPanorama3DGeometry() {
		return panorama3DGeometry;
	}

	public void setPanorama3DGeometry(AbstractPanorama3DGeometry panorama3dGeometry) {
		panorama3DGeometry = panorama3dGeometry;
	}

	public OutputType getOutputType() {
		return outputType;
	}

	public void setOutputType(OutputType outputType) {
		this.outputType = outputType;
	}


	
	/**
	 * @param i
	 * @param j
	 * @param side
	 * @return
	 */
	public Ray getRayForPixel(double i, double j, Side side)
	{
		// find the angle \phi that corresponds to horizontal pixel position i
		// (i = 0 corresponds to phi = 0, i=getDetectorPixelsHorizontal() corresponds to  phi = 2 pi)
		
		double
			phi = panorama2D.i2phi(i),
			theta = panorama2D.j2theta(j);
		
		return panorama3DGeometry.getRayForAngles(phi, theta, side);
	}
	
	@Override
	public Ray getRayForPixel(double i, double j)
	{
		return getRayForPixel(i, j, Side.CENTRE);	// return ray for point between the eyes
	}
	
	@Override
	public Ray getCentralRayForPixel(double i, double j)
	{
		return getRayForPixel(i, j);	// return ray for point between the eyes
	}

	
	/**
	 * In case the rendered image is shown at a different size (imagePixelsHorizontal x imagePixelsVertical),
	 * return a light ray corresponding to image pixel (i,j).
	 * 
	 * In editable cameras, this method implements a method asked for by the EditableCamera interface.
	 * @see optics.raytrace.GUI.core.CameraWithRayForImagePixel#getRayForImagePixel(int, int, int, int)
	 * 
	 * @param imagePixelsHorizontal
	 * @param imagePixelsVertical
	 * @param i
	 * @param j
	 * @return a light ray that corresponds to image pixel (i,j)
	 */
	public Ray getRayForImagePixel(int imagePixelsHorizontal, int imagePixelsVertical, int i, int j)
	{
		return getRayForPixel(
				(int)((double)getDetectorPixelsHorizontal()/(double)imagePixelsHorizontal*i),
				(int)((double)getDetectorPixelsVertical()/(double)imagePixelsVertical*j)
			);
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.CCD#calculateColour(int, int)
	 */
	@Override
	public DoubleColour calculatePixelColour(double i, double j, SceneObject scene, LightSource lights)
	throws RayTraceException
	{
//		// calculate direction vector and "normal" eye position
//		// define a ray with the eye's position as starting point and the direction vector as ray direction
//		Ray ray;
//		RaySceneObjectIntersection intersection = scene.getClosestRayIntersection(ray);
//		// the intersection point is then
//		intersection.p;
		
		DoubleColour rightColour = DoubleColour.BLUE, leftColour = DoubleColour.GREEN;
		
		if(outputType != OutputType.LEFT_EYE)
		{
			rightColour = scene.getColour(getRayForPixel(i,j,Side.RIGHT), lights, scene, getMaxTraceLevel(), getRaytraceExceptionHandler());
		}
		if(outputType != OutputType.RIGHT_EYE)
		{
			leftColour = scene.getColour(getRayForPixel(i,j,Side.LEFT), lights, scene, getMaxTraceLevel(), getRaytraceExceptionHandler());
		}
		
//		// also store together the picture seen by the left and right eye
//		leftCamera.getCCD().setPixelColour(i, j, leftColour.getRGB());
//		rightCamera.getCCD().setPixelColour(i, j, rightColour.getRGB());
		
		switch(outputType)
		{
		case LEFT_EYE:
			// System.out.print("L");
			return leftColour;
		case RIGHT_EYE:
			// System.out.print("R");
			return rightColour;
		case ANAGLYPH_COLOUR:
			// from http://en.wikipedia.org/wiki/Anaglyph_image:
			// "In recent simple practice, the left eye image is filtered to remove blue & green.
			// The right eye image is filtered to remove red."
			return new DoubleColour(
					leftColour.getR(),
					rightColour.getG(),
					rightColour.getB()
				);
		case ANAGLYPH_REDBLUE:
		default:
			return new DoubleColour(
					leftColour.getLuminance(),
					0,
					rightColour.getLuminance()	// * DoubleColour.LUMINANCE_R_FACTOR / DoubleColour.LUMINANCE_B_FACTOR
				);
		}
	}


	@Override
	public String toString()
	{
		return getDescription() + " [SurroundAnaglyphCamera]";
	}
}