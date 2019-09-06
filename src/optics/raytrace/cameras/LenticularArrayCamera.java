package optics.raytrace.cameras;

import java.io.Serializable;
import java.util.ArrayList;

import math.MyMath;
import math.Vector3D;

import optics.DoubleColour;
import optics.raytrace.core.CameraClass;
import optics.raytrace.core.LightSource;
import optics.raytrace.core.Ray;
import optics.raytrace.core.SceneObject;
import optics.raytrace.exceptions.RayTraceException;


/**
 * Calculates an image suitable for printing on the back of lenticular arrays.
 * 
 * Not a million miles away from taking a picture through a lenticular array (but not quite the same).
 * 
 * @author Blair, Johannes
 */
public class LenticularArrayCamera extends CameraClass implements Serializable
{
	private static final long serialVersionUID = 8367374584761180016L;
	
	/**
	 * The number of image pixels that get viewed through each lens; this number is the number of directions
	 * that are encoded in the postcard
	 */
	private int pixelsPerCylindricalLens;
	
	/**
	 * The focal length of the cylindrical lenslets used for viewing this
	 */
	private double cylindricalLensletFocalLength;
	
	/**
	 * point in central view direction, i.e. away from the observer
	 */
	private Vector3D lenticularArrayNormal;	// pre-calculate in constructor!
		
	/**
	 * the view directions corresponding to the different pixels for each lenslet
	 */
	private ArrayList<Vector3D> viewDirections;
	
	/**
	 * Create a new lenticular-array camera
	 * 
	 * @param lenticularArrayCentre	the centre of the postcard
	 * @param pixelsPerCylindricalLens
	 * @param cylindricalLensletFocalLength
	 * @param lenticularArrayWidthVector	vector along the width of the postcard, pointing <b>to the right</b>
	 * @param lenticularArrayHeightVector	vector along the height of the postcard, pointing <b>upwards</b>
	 * @param imagePixelsHorizontal	number of detector pixels in the horizontal direction
	 * @param imagePixelsVertical	number of detector pixels in the vertical direction
	 */
	public LenticularArrayCamera(
			String name,
			Vector3D lenticularArrayCentre,
			int pixelsPerCylindricalLens,
			double cylindricalLensletFocalLength,
			Vector3D lenticularArrayWidthVector, Vector3D lenticularArrayHeightVector,
			int imagePixelsHorizontal, int imagePixelsVertical,
			int maxTraceLevel
		)
	{
		super(	name,
				lenticularArrayCentre,	// centre of detector
				lenticularArrayWidthVector, lenticularArrayHeightVector,
				imagePixelsHorizontal, imagePixelsVertical,
				maxTraceLevel);
		
		setPixelsPerCylindricalLens(pixelsPerCylindricalLens);
		setCylindricalLensletFocalLength(cylindricalLensletFocalLength);

		calculateLenticularArrayNormal();

		// create an array of cameras
		calculateViewDirections();
	}
	
	public LenticularArrayCamera(LenticularArrayCamera original)
	{
		super(original);
		
		setPixelsPerCylindricalLens(original.getPixelsPerCylindricalLens());
		setCylindricalLensletFocalLength(cylindricalLensletFocalLength);

		calculateLenticularArrayNormal();

		// create an array of cameras
		calculateViewDirections();
	}
	
	/* (non-Javadoc)
	 * @see optics.raytrace.core.Camera#clone()
	 */
	@Override
	public LenticularArrayCamera clone()
	{
		return new LenticularArrayCamera(this);
	}
	
	/**
	 * populate the array of view directions for the different pixels
	 */
	private void calculateViewDirections()
	{
		viewDirections = new ArrayList<Vector3D>();
		
		// first calculate the normalised postcard-width vector
		Vector3D normalisedPostcardWidthVector = getCCD().getHorizontalSpanVector().getNormalised();

		// pre-calculate a few more things
		double
			delta = getCCD().getHorizontalSpanVector().getLength() / getCCD().getDetectorPixelsHorizontal(),	// separation between image pixels
			w = delta * getPixelsPerCylindricalLens();	// width of one cylindrical lenslet
		
		// System.out.println("delta = "+delta+", w = "+w);
		
		for(int i = 0; i < getPixelsPerCylindricalLens(); i++)
		{
			double
				xi = (0.5 + i)*delta - 0.5*w,
				// angleWRTNormal = Math.atan2(-xi, getCylindricalLensletFocalLength());
				angleWRTNormal = Math.atan2(xi, getCylindricalLensletFocalLength());
			
			// System.out.println("xi = "+xi+", angle = "+angleWRTNormal);
				
			viewDirections.add(Vector3D.sum(
						lenticularArrayNormal.getProductWith(Math.cos(angleWRTNormal)),
						normalisedPostcardWidthVector.getProductWith(Math.sin(angleWRTNormal))
					));
		}
	}


	//
	// setters and getters
	//
	
	public double getCylindricalLensletFocalLength() {
		return cylindricalLensletFocalLength;
	}

	public void setCylindricalLensletFocalLength(double cylindricalLensletFocalLength) {
		this.cylindricalLensletFocalLength = cylindricalLensletFocalLength;
	}

	public Vector3D getLenticularArrayCentre()
	{
		return getCCD().getCentrePosition();
	}
	
	public void setLenticularArrayCentre(Vector3D postcardCentre)
	{
		getCCD().setCentrePosition(postcardCentre);
	}
	
	private void calculateLenticularArrayNormal()
	{
		lenticularArrayNormal = Vector3D.crossProduct(getCCD().getHorizontalSpanVector(), getCCD().getVerticalSpanVector()).getNormalised();
	}
	
	public int getPixelsPerCylindricalLens() {
		return pixelsPerCylindricalLens;
	}

	public void setPixelsPerCylindricalLens(int pixelsPerCylindricalLens) {
		this.pixelsPerCylindricalLens = pixelsPerCylindricalLens;
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.Camera#getRayForPixel(int, int)
	 */
	@Override
	public Ray getRayForPixel(double i, double j)
	{
		return new Ray(
				ccd.getPositionOnPixel(i, j),
				viewDirections.get(((int)i) % getPixelsPerCylindricalLens()),
				0	// start time of ray --- not important here (?)
			).getAdvancedRay(-MyMath.HUGE);
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.Camera#getCentralRayForPixel(int, int)
	 */
	@Override
	public Ray getCentralRayForPixel(double i, double j)
	{
		return new Ray(
				ccd.getPixelCentrePosition(i, j),
				viewDirections.get(((int)i) % getPixelsPerCylindricalLens()),
				0	// start time of ray --- not important here (?)
			).getAdvancedRay(-MyMath.HUGE);
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.core.Camera#calculatePixelColour(int, int, optics.raytrace.core.SceneObject, optics.raytrace.core.LightSource)
	 */
	@Override
	public DoubleColour calculatePixelColour(double i, double j, SceneObject scene, LightSource lights)
	throws RayTraceException
	{		
//		return cameras.get(i % getPixelsPerLens()).calculatePixelColour(
//				i/getPixelsPerLens(),
//				j,
//				scene,
//				lights
//			);
		return scene.getColour(getRayForPixel(i, j), lights, scene, getMaxTraceLevel(),
				getRaytraceExceptionHandler());
	}

	@Override
	public String toString() {
		return "LenticularArrayCamera";
	}
}