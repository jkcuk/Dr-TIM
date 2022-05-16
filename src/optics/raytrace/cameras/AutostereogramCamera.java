package optics.raytrace.cameras;

import java.awt.Color;
import java.awt.image.BufferedImage;

import optics.DoubleColour;
import optics.raytrace.GUI.core.RaytraceWorker;
import optics.raytrace.core.LightSource;
import optics.raytrace.core.Ray;
import optics.raytrace.core.SceneObject;
import optics.raytrace.exceptions.RayTraceException;
import math.MyMath;
import math.Vector2D;
import math.Vector3D;

/**
 * A camera that creates an autostereogram.
 * 
 * Inherits, from the PinholeCamera class, a "pinhole position", which takes on the meaning of
 * the point in the middle between the two eyes.
 * 
 * @author Johannes Courtial
 */
public class AutostereogramCamera extends PinholeCamera
{
	private static final long serialVersionUID = 5570212384052985524L;

	// parameters
	private double
		dotsPerPixel,
		dotRadius;

	// internal variables
	private Vector3D leftEyePosition, rightEyePosition;
	          
	/**
	 * @param name
	 * @param leftEyePosition
	 * @param rightEyePosition
	 * @param centreOfView
	 * @param horizontalSpanVector
	 * @param verticalSpanVector
	 * @param dotsPerPixel
	 * @param detectorPixelsHorizontal
	 * @param detectorPixelsVertical
	 * @param maxTraceLevel
	 */
	public AutostereogramCamera(
			String name,
			Vector3D leftEyePosition,	// left eye
			Vector3D rightEyePosition,	// right eye
			Vector3D centreOfView,	// the point in the centre of both eyes' field of view
			Vector3D horizontalSpanVector, Vector3D verticalSpanVector,
			double dotsPerPixel,	// dots per pixel
			double dotRadius,
			int detectorPixelsHorizontal, int detectorPixelsVertical,
			int maxTraceLevel
		)
	{
		// run the PinholeCamera constructor
		super(	name,
				Vector3D.sum(leftEyePosition, rightEyePosition).getProductWith(0.5),	// pinholePosition,
				centreOfView,	// Vector3D from the centre of the detector array to the pinhole
				horizontalSpanVector, verticalSpanVector,
				detectorPixelsHorizontal, detectorPixelsVertical,
				ExposureCompensationType.EC0,	// exposure compensation is meaningless here
				maxTraceLevel
			);

		setLeftEyePosition(leftEyePosition);
		setRightEyePosition(rightEyePosition);
		setDotsPerPixel(dotsPerPixel);
		setDotRadius(dotRadius);
	}
	
	/**
	 * Create a clone of the original
	 * @param original
	 */
	public AutostereogramCamera(AutostereogramCamera original)
	{
		super(original);
		
		setLeftEyePosition(original.getLeftEyePosition().clone());
		setRightEyePosition(original.getRightEyePosition().clone());
		setDotsPerPixel(original.getDotsPerPixel());
		setDotRadius(original.getDotRadius());
	}
	
	@Override
	public AutostereogramCamera clone()
	{
		return new AutostereogramCamera(this);
	}
	

	/* setters and getters */
	
	public Vector3D getLeftEyePosition() {
		return leftEyePosition;
	}

	public void setLeftEyePosition(Vector3D leftEyePosition) {
		this.leftEyePosition = leftEyePosition;
	}

	public Vector3D getRightEyePosition() {
		return rightEyePosition;
	}

	public void setRightEyePosition(Vector3D rightEyePosition) {
		this.rightEyePosition = rightEyePosition;
	}
		
	public double getDotsPerPixel() {
		return dotsPerPixel;
	}

	public void setDotsPerPixel(double dotsPerPixel) {
		this.dotsPerPixel = dotsPerPixel;
	}
	
	public double getDotRadius() {
		return dotRadius;
	}

	public void setDotRadius(double dotRadius) {
		this.dotRadius = dotRadius;
	}
	
	private double
		cosHues[][], sinHues[][];
	
	// a pre-calculated exp
	private double expTable[];
	private double expPoints;

	/**
	 * Pre-calculate an array of numbers representing the function exp(-rSquared) in the range rSquared=0 to 16,
	 * where rSquared is the squared ratio of distance from the dot's centre and its radius.
	 * @param expPoints
	 */
	private void preCalculateExp(int expPoints)
	{
		this.expPoints = expPoints;
		expTable = new double[expPoints];
		
		for(int i=0; i<expPoints; i++)
		{
			double rSquared = 32.*i/(expPoints-1);
			expTable[i] = Math.exp(-rSquared);
		}
	}
	
	private double getDotSaturation(double rSquared)
	{
		// System.out.println(rSquared);
		return expTable[(int)(rSquared/32.0*(expPoints-1)+0.5)];
	}

	/**
	 * @param i
	 * @param j
	 * @param dotHue	a hue in the range 0..2 pi
	 */
	private void placeDot(double i, double j, double dotHue)
	{
		// ccd.setPixelColour((int)(i+0.5), (int)(j+0.5), colour.getRGB());
		
		int 
			hMin = Math.max(0, (int)(i - 3*dotRadius + 0.5)),	// (int)(i+0.5) - dotRadius,
			vMin = Math.max(0, (int)(j - 3*dotRadius + 0.5)),	// (int)(j+0.5) - dotRadius,
			hMax = Math.min(ccd.getDetectorPixelsHorizontal(), (int)(i + 3*dotRadius +0.5)),
			vMax = Math.min(ccd.getDetectorPixelsVertical()  , (int)(j + 3*dotRadius +0.5));
		double
			oneOverDotRadius = 1./dotRadius,
			cos = Math.cos(dotHue),
			sin = Math.sin(dotHue),
			f;
		
		for(int h = hMin; h < hMax; h++)
		{
			for(int v = vMin; v < vMax; v++)
			{
				double rSquared = MyMath.square(oneOverDotRadius*(i-h)) + MyMath.square(oneOverDotRadius*(j-v));
//				if(rSquared > 32)
//				{
//					System.out.println("i="+i);
//					System.out.println("j="+j);
//					System.out.println("h="+h);
//					System.out.println("v="+v);
//					System.out.println("dotRadius="+dotRadius);
//				}
				f = getDotSaturation(rSquared);

				cosHues[h][v] += f*cos;
				sinHues[h][v] += f*sin;
			}
		}
	}

	/* 
	 * Take a photo.  Here this can't be done for each pixel independently, so overriding the
	 * calculatePixelColour method is not sufficient.  Therefore override the takePhoto method.
	 * 
	 * This method is implemented more elegantly in the MultiSurfaceAutostereogramCamera class.
	 * 
	 * @see optics.raytrace.core.Camera#takePhoto(optics.raytrace.core.SceneObject, optics.raytrace.core.LightSource, optics.raytrace.GUI.core.RaytraceWorker)
	 */
	@Override
	public BufferedImage takePhoto(
			SceneObject scene,
			LightSource lights,
			RaytraceWorker raytraceWorker
		)
	{
		cosHues = new double[ccd.getDetectorPixelsHorizontal()][ccd.getDetectorPixelsVertical()];
		sinHues = new double[ccd.getDetectorPixelsHorizontal()][ccd.getDetectorPixelsVertical()];
		
		// make all the pixels red initially
		for (int j=0; j<getDetectorPixelsVertical(); j++)
			for (int i=0; i<getDetectorPixelsHorizontal(); i++)
			{
				cosHues[i][j] = 0.3;
				sinHues[i][j] = 0.;
			}

		// now calculate the hues
		
		// pre-calculate the saturation curve of a dot
		preCalculateExp(1000);
		
		int 
			nDots = 0,	// the number of dots
			nDotsMax = (int)(getDotsPerPixel() * ccd.getDetectorPixelsHorizontal() * ccd.getDetectorPixelsVertical());
			// ... and the number of dots we are aiming for
		
		double i, j, dotHue;
		Vector3D startStereogramIntersectionPoint, stereogramIntersectionPoint;
		Vector2D coordinates;
		while(nDots < nDotsMax)
		{
			// start with a random colour...
			dotHue = 2*Math.PI*Math.random();
				
			// ... on a random pixel on the CCD, whose the random indices are
			i = Math.random() * (ccd.getDetectorPixelsHorizontal()-1);
			j = Math.random() * (ccd.getDetectorPixelsVertical()-1);
			
			startStereogramIntersectionPoint = ccd.getPositionOnPixel((int)(i+0.5), (int)(j+0.5));
			
			stereogramIntersectionPoint = startStereogramIntersectionPoint;
			for(int counter = 0;
				counter < 100 &&
				i >= 0 && i < getDetectorPixelsHorizontal() &&
				j >= 0 && j < getDetectorPixelsVertical();
				counter++
			)
			{
				placeDot(i, j, dotHue);
				nDots++;

				stereogramIntersectionPoint = findNextStereogramIntersectionPoint(
						stereogramIntersectionPoint,
						getLeftEyePosition(),
						getRightEyePosition(),
						scene
					);

				if(stereogramIntersectionPoint == null)
				{
					i = -1;
					j = -1;
				}
				else
				{
					coordinates = ccd.getSurfaceCoordinates(stereogramIntersectionPoint);
					i = (int)(coordinates.x * (getDetectorPixelsHorizontal()-1.0) + 0.5);
					j = (int)(coordinates.y * (getDetectorPixelsVertical()-1.0) + 0.5);
				}
			}

			stereogramIntersectionPoint = findNextStereogramIntersectionPoint(
					startStereogramIntersectionPoint,
					getRightEyePosition(),
					getLeftEyePosition(),
					scene
				);

			coordinates = ccd.getSurfaceCoordinates(stereogramIntersectionPoint);
			if(stereogramIntersectionPoint == null)
			{
				i = -1;
				j = -1;
			}
			else
			{
				coordinates = ccd.getSurfaceCoordinates(stereogramIntersectionPoint);
				i = (int)(coordinates.x * (getDetectorPixelsHorizontal()-1.0) + 0.5);
				j = (int)(coordinates.y * (getDetectorPixelsVertical()-1.0) + 0.5);
			}

			for(int counter = 0;
				counter < 100 &&
				i >= 0 && i < getDetectorPixelsHorizontal() &&
				j >= 0 && j < getDetectorPixelsVertical();
				counter++
			)
			{
				placeDot(i, j, dotHue);
				nDots++;

				stereogramIntersectionPoint = findNextStereogramIntersectionPoint(
						stereogramIntersectionPoint,
						getRightEyePosition(),
						getLeftEyePosition(),
						scene
					);

				coordinates = ccd.getSurfaceCoordinates(stereogramIntersectionPoint);
				if(stereogramIntersectionPoint == null)
				{
					i = -1;
					j = -1;
				}
				else
				{
					coordinates = ccd.getSurfaceCoordinates(stereogramIntersectionPoint);
					i = (int)(coordinates.x * (getDetectorPixelsHorizontal()-1.0) + 0.5);
					j = (int)(coordinates.y * (getDetectorPixelsVertical()-1.0) + 0.5);
				}
			}
			
			// is there is a RaytraceWorker?
			if(raytraceWorker != null)
			{
				// check whether the calculation has been cancelled
				if(raytraceWorker.isCancelled()) return ccd.getImage();

				//
				// give some feedback on progress
				//
				
				// the simplest way of giving feedback (note that the method being called
				// has to be thread-safe!)
				raytraceWorker.setStatus(description + " rendering... (" + (int)(100.*nDots/nDotsMax) + "% completed.)");

				// the proper way of giving feedback, which doesn't work though...
				// raytraceWorker.setProgress((int)(100.*j/ccd.getDetectorPixelsVertical()));
				// raytraceWorker.publish();	// doesn't work
			}
			else
			{
				// otherwise print feedback onto the console
				System.out.println("Rendered " + nDots + " out of " + nDotsMax + "dots");
			}

		}

		// turn all the hues into colours

		// check if the memory for the image has been allocated correctly;
		// if not, allocate the memory
		BufferedImage image = ccd.getImage();
		if(image == null)
		{
			// no image memory has been allocated, so do it now
			ccd.allocateImageMemory();
		}
		else if(
			(image.getWidth() != ccd.getDetectorPixelsHorizontal()) ||
			(image.getHeight() != ccd.getDetectorPixelsVertical())
		)
		{
			// the allocated memory has the wrong size or shape; re-allocate the memory
			ccd.allocateImageMemory();
		}
		
		for(int h = 0; h<ccd.getDetectorPixelsHorizontal(); h++)
			for (int v=0; v<ccd.getDetectorPixelsVertical(); v++)
			{
				double hue = Math.atan2(sinHues[h][v], cosHues[h][v])/(2*Math.PI) + 0.5;
				ccd.setPixelColour(h, v, Color.HSBtoRGB((float)hue, 1, 1));
			}
		
		return ccd.getImage();
	}
	
	private Vector3D findNextStereogramIntersectionPoint(
			Vector3D stereogramIntersectionPoint,
			Vector3D eye1Position,
			Vector3D eye2Position,
			SceneObject scene
		)
	{
		Ray ray = new Ray(
				eye1Position,
				Vector3D.difference(
						stereogramIntersectionPoint,
						eye1Position),
				0,	// start time of ray --- not important here (?)
				false	// reportToConsole
		);

		Vector3D sceneIntersectionPoint = scene.getClosestRayIntersection(ray).p;
		
		ray = new Ray(
				eye2Position,
				Vector3D.difference(
						sceneIntersectionPoint,
						eye2Position
				),
				0,	// start time of ray --- not important here (?)
				false	// reportToConsole
		).getAdvancedRay(-MyMath.HUGE);

		return ccd.getClosestRayIntersection(ray).p;
	}


	/* (non-Javadoc)
	 * @see optics.raytrace.CCD#calculateColour(int, int)
	 */
	@Override
	public DoubleColour calculatePixelColour(double i, double j, SceneObject scene, LightSource lights)
	throws RayTraceException
	{
		return DoubleColour.ORANGE;
	}

}
