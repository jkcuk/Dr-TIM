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
 * A camera that creates an autostereogram that can be rotated, giving different depth profiles.
 * 
 * Inherits, from the PinholeCamera class, a "pinhole position", which takes on the meaning of
 * the point in the middle between the two eyes.
 * 
 * @author Johannes Courtial
 */
public class AutostereogramMovieCamera extends PinholeCamera
{
	private static final long serialVersionUID = 4101535641696927070L;

	// parameters
	private double
		dotRadius,
		eyeSeparation;
	private int
		noOfIterations;
	
	private Vector3D
		leftEyePositions[], rightEyePositions[];
	
	
	/**
	 * @param name
	 * @param betweenTheEyes
	 * @param eyeSeparation
	 * @param centreOfView
	 * @param horizontalSpanVector
	 * @param verticalSpanVector
	 * @param dotRadius
	 * @param detectorPixelsHorizontal
	 * @param detectorPixelsVertical
	 * @param maxTraceLevel
	 */
	public AutostereogramMovieCamera(
			String name,
			Vector3D betweenTheEyes,
			double eyeSeparation,
			Vector3D centreOfView,	// the point in the centre of both eyes' field of view
			Vector3D horizontalSpanVector, Vector3D verticalSpanVector,
			int noOfIterations,
			double dotRadius,
			int detectorPixelsHorizontal, int detectorPixelsVertical,
			int maxTraceLevel
		)
	{
		// run the PinholeCamera constructor
		super(	name,
				betweenTheEyes,	// pinholePosition,
				centreOfView,	// Vector3D from the centre of the detector array to the pinhole
				horizontalSpanVector, verticalSpanVector,
				detectorPixelsHorizontal, detectorPixelsVertical,
				ExposureCompensationType.EC0,	// exposure compensation is meaningless here
				maxTraceLevel
			);

		setEyeSeparation(eyeSeparation);
		setNoOfIterations(noOfIterations);
		setDotRadius(dotRadius);
	}
	
	/**
	 * Create a clone of the original
	 * @param original
	 */
	public AutostereogramMovieCamera(AutostereogramMovieCamera original)
	{
		super(original);
		
		setEyeSeparation(original.getEyeSeparation());
		setNoOfIterations(original.getNoOfIterations());
		setDotRadius(original.getDotRadius());
	}
	
	@Override
	public AutostereogramMovieCamera clone()
	{
		return new AutostereogramMovieCamera(this);
	}
	

	/* setters and getters */
	
	public Vector3D getBetweenTheEyes()
	{
		return getPinholePosition();
	}
	
	public void setBetweenTheEyes(Vector3D betweenTheEyes)
	{
		setPinholePosition(betweenTheEyes);
	}
	
	public double getEyeSeparation() {
		return eyeSeparation;
	}

	public void setEyeSeparation(double eyeSeparation) {
		this.eyeSeparation = eyeSeparation;
	}
	
	public int getNoOfIterations() {
		return noOfIterations;
	}

	public void setNoOfIterations(int noOfIterations) {
		this.noOfIterations = noOfIterations;
	}

	public double getDotRadius() {
		return dotRadius;
	}

	public void setDotRadius(double dotRadius) {
		this.dotRadius = dotRadius;
	}
	
	private double
		cosHues[][], sinHues[][],
		cosHues1[][], sinHues1[][];
	
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
	 * @param dotHueX
	 * @param dotHueY
	 */
	private void placeDot(double i, double j, double dotHueX, double dotHueY)
	{
		int 
			hMin = Math.max(0, (int)(i - 3*dotRadius + 0.5)),	// (int)(i+0.5) - dotRadius,
			vMin = Math.max(0, (int)(j - 3*dotRadius + 0.5)),	// (int)(j+0.5) - dotRadius,
			hMax = Math.min(ccd.getDetectorPixelsHorizontal(), (int)(i + 3*dotRadius +0.5)),
			vMax = Math.min(ccd.getDetectorPixelsVertical()  , (int)(j + 3*dotRadius +0.5));
		double
			oneOverDotRadius = 1./dotRadius,
			f;
		
		// if(dotHue != 0.)
		for(int h = hMin; h < hMax; h++)
		{
			for(int v = vMin; v < vMax; v++)
			{
				double rSquared = MyMath.square(oneOverDotRadius*(i-h)) + MyMath.square(oneOverDotRadius*(j-v));
				f = getDotSaturation(rSquared);

				cosHues[h][v] += f*dotHueX;
				sinHues[h][v] += f*dotHueY;
			}
		}
	}

	/* 
	 * Overriding the Camera class's takePhoto method is not enough here.
	 * @see optics.raytrace.core.Camera#takePhoto(optics.raytrace.core.SceneObject, optics.raytrace.core.LightSource, optics.raytrace.GUI.core.RaytraceWorker)
	 */
	@Override
	public BufferedImage takePhoto(
			SceneObject scene,
			LightSource lights,
			RaytraceWorker raytraceWorker
		)
	{
		throw new RuntimeException("This method shouldn't be called. Naughty!");
	}
	
	
	/**
	 * Take a photo.  Here this can't be done for each pixel independently, so overriding the
	 * calculatePixelColour method is not sufficient.  Therefore override the takePhoto method.
	 * 
	 * @param scene	an array of scenes
	 * @param raytraceWorker
	 * @return
	 */
	public BufferedImage takePhoto(SceneObject scenes[], double angles[])
	{
		int
			h, v,	// the variables used to cycle through all the pixels
			n;	// a general index

		// first check that the number of angles is the same as the number of scenes
		int noOfAngles = angles.length;	// the number of angles
		if(noOfAngles != scenes.length)
			throw new RuntimeException("The number of angles needs to match the number of scenes.");
			
		// pre-calculate the positions of the left and right eyes for the various angles
		leftEyePositions = new Vector3D[noOfAngles];
		rightEyePositions = new Vector3D[noOfAngles];
		for(n = 0; n < noOfAngles; n++)
		{
			double
				rCos = 0.5*getEyeSeparation()*Math.cos(angles[n]),	// r * cos(angle)
				rSin = 0.5*getEyeSeparation()*Math.sin(angles[n]);	// r * sin(angle)
			
			// the left eye on one side, ...
//			leftEyePositions[n] = Vector3D.sum(
//					getBetweenTheEyes(),
//					getHorizontalSpanVector().getWithLength(0.5*getEyeSeparation()),	// getHorizontalSpanVector().getWithLength(rCos),
//					getVerticalSpanVector().getWithLength(0.5*getEyeSeparation()*Math.tan(angles[n]))	// getVerticalSpanVector().getWithLength(rSin)
//				);
			leftEyePositions[n] = Vector3D.sum(
					getBetweenTheEyes(),
					getHorizontalSpanVector().getWithLength(rCos),
					getVerticalSpanVector().getWithLength(rSin)
				);

			// ... and the right eye on the other side
//			rightEyePositions[n] = Vector3D.sum(
//					getBetweenTheEyes(),
//					getHorizontalSpanVector().getWithLength(-0.5*getEyeSeparation()),	// getHorizontalSpanVector().getWithLength(-rCos),
//					getVerticalSpanVector().getWithLength(-0.5*getEyeSeparation()*Math.tan(angles[n]))	// getVerticalSpanVector().getWithLength(-rSin)
//				);
			rightEyePositions[n] = Vector3D.sum(
					getBetweenTheEyes(),
					getHorizontalSpanVector().getWithLength(-rCos),
					getVerticalSpanVector().getWithLength(-rSin)
				);
		}
		
		// pre-calculate the saturation curve of a dot
		preCalculateExp(1000);

		// allocate memory for the arrays of the Cartesian components of the pixel hues
		cosHues = new double[ccd.getDetectorPixelsHorizontal()][ccd.getDetectorPixelsVertical()];
		sinHues = new double[ccd.getDetectorPixelsHorizontal()][ccd.getDetectorPixelsVertical()];
		cosHues1 = new double[ccd.getDetectorPixelsHorizontal()][ccd.getDetectorPixelsVertical()];
		sinHues1 = new double[ccd.getDetectorPixelsHorizontal()][ccd.getDetectorPixelsVertical()];

		for(n = 0; n<1000; n++)
			placeDot(
					(int)(Math.random()*ccd.getDetectorPixelsHorizontal()),
					(int)(Math.random()*ccd.getDetectorPixelsVertical()),
					Math.random()-0.5,
					Math.random()-0.5
				);
		
		// take a note of all the new hues
		for(h = 0; h<ccd.getDetectorPixelsHorizontal(); h++)
			for(v=0; v<ccd.getDetectorPixelsVertical(); v++)
			{
				cosHues1[h][v] = cosHues[h][v];
				sinHues1[h][v] = sinHues[h][v];
			}

		// now calculate the hues
		
		double dotHueX, dotHueY;
		Vector3D startStereogramIntersectionPoint;
		
		for(int i=0; i<getNoOfIterations(); i++)
		{
			// one iteration

			// first make the Cartesian components of all pixel hues 0
			for(h = 0; h<ccd.getDetectorPixelsHorizontal(); h++)
				for(v=0; v<ccd.getDetectorPixelsVertical(); v++)
					cosHues[h][v] = sinHues[h][v] = 0.;

			// go through all the pixels, and place dots around them
			for(h = 0; h<ccd.getDetectorPixelsHorizontal(); h++)
				for(v=0; v<ccd.getDetectorPixelsVertical(); v++)
				{
					// start with pixel #(h,v)'s hue...
					dotHueX = cosHues1[h][v];
					dotHueY = sinHues1[h][v];

					placeDot(h, v, dotHueX, dotHueY);

					// ... and position
					startStereogramIntersectionPoint = ccd.getPositionOnPixel(h, v);

					for(n = 0; n < noOfAngles; n++)
					{
						// calculate the next stereogram intersection point, for view #n
						putDotOnNextStereogramIntersectionPoint(
								startStereogramIntersectionPoint,
								leftEyePositions[n],
								rightEyePositions[n],
								scenes[n],
								dotHueX, dotHueY
						);

						// calculate the other next stereogram intersection point, for view #n
						putDotOnNextStereogramIntersectionPoint(
								startStereogramIntersectionPoint,
								rightEyePositions[n],
								leftEyePositions[n],
								scenes[n],
								dotHueX, dotHueY
						);
					}
				}

			// take a note of all the new hues
			for(h = 0; h<ccd.getDetectorPixelsHorizontal(); h++)
				for(v=0; v<ccd.getDetectorPixelsVertical(); v++)
				{
					cosHues1[h][v] = cosHues[h][v];
					sinHues1[h][v] = sinHues[h][v];
				}

			// otherwise print feedback onto the console
			System.out.println("Completed " + (i+1) + " out of " + getNoOfIterations() + " iterations");
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

		// find the maximum radius in Cartesian hue space
//		double r2Max = 0;	// r squared, maximum
//		for(h = 0; h<ccd.getDetectorPixelsHorizontal(); h++)
//			for(v=0; v<ccd.getDetectorPixelsVertical(); v++)
//			{
//				double r2 = MyMath.square(sinHues[h][v]) + MyMath.square(cosHues[h][v]);
//				if(r2 > r2Max) r2Max = r2;
//			}

		for(h = 0; h<ccd.getDetectorPixelsHorizontal(); h++)
			for(v=0; v<ccd.getDetectorPixelsVertical(); v++)
			{
				double hue = Math.atan2(sinHues[h][v], cosHues[h][v])/(2*Math.PI) + Math.PI;
				// float brightness = (float)Math.sqrt((MyMath.square(sinHues[h][v]) + MyMath.square(cosHues[h][v]))/r2Max);
				float brightness = (float)1.0;
				ccd.setPixelColour(h, v, Color.HSBtoRGB((float)hue, 1, brightness));
			}
		
		return ccd.getImage();
	}
	
	private void putDotOnNextStereogramIntersectionPoint(
			Vector3D stereogramIntersectionPoint,
			Vector3D eye1Position,
			Vector3D eye2Position,
			SceneObject scene,
			double dotHueX, double dotHueY
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

		sceneIntersectionPoint = ccd.getClosestRayIntersection(ray).p;
				
		if(sceneIntersectionPoint != null)
		{
			// calculate the position of the new dot...
			Vector2D coordinates = ccd.getSurfaceCoordinates(sceneIntersectionPoint);
			
			// ... and place it
			placeDot(
					(int)(coordinates.x * (getDetectorPixelsHorizontal()-1.0) + 0.5),
					(int)(coordinates.y * (getDetectorPixelsVertical()-1.0) + 0.5),
					dotHueX, dotHueY
				);
		}
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
