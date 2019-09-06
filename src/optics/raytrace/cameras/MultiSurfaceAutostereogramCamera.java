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
 * An extended camera that creates an autostereogram.
 * 
 * Extensions:  unlike the "standard" autostereogram camera, this one can deal with more than one
 * surface and more than two eye positions.
 * 
 * The idea of the multiple surfaces is that the observer's brain would be able to flip between perceiving any
 * one of the (two or more) surfaces.
 * 
 * The idea of the multiple eye positions is that the scene can then be perceived in 3D as long as the
 * direction between the observer's (two) eyes coincides with the line connecting any two of the (two or more)
 * eye positions.
 * 
 * Certainly works with one scene and two eyes, when this is just a standard autostereogram camera.
 * 
 * Doesn't work (for me, anyway) with more than one scene, not with the scenes I tried anyway.
 * 
 * Sort of works with three eye positions (but it's harder to see the 3D than with two eye positions);
 * doesn't work for me with more than three eye positions.
 * 
 * Inherits, from the PinholeCamera class, a "pinhole position", which takes on the meaning of
 * the point in the middle between the eyes.
 * 
 * @author Johannes Courtial
 */
public class MultiSurfaceAutostereogramCamera extends PinholeCamera
{
	private static final long serialVersionUID = -2351747043791653592L;

	// parameters
	private double
		dotsPerPixel,
		dotRadius;

	// internal variables
	private Vector3D[] eyePositions;	// the array holding the eye positions
	          
	/**
	 * The scenes are passed to takePhoto (see there)
	 * @param name
	 * @param eyePositions	the eye positions
	 * @param centreOfView
	 * @param horizontalSpanVector
	 * @param verticalSpanVector
	 * @param dotsPerPixel
	 * @param detectorPixelsHorizontal
	 * @param detectorPixelsVertical
	 * @param maxTraceLevel
	 */
	public MultiSurfaceAutostereogramCamera(
			String name,
			Vector3D[] eyePositions,	// eye positions
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
				null,	// pinholePosition placeholder; set in a minute
				centreOfView,	// Vector3D from the centre of the detector array to the pinhole
				horizontalSpanVector, verticalSpanVector,
				detectorPixelsHorizontal, detectorPixelsVertical,
				ExposureCompensationType.EC0,	// exposure compensation is meaningless here
				maxTraceLevel
			);

		// calculate the "pinhole position", i.e. the point between the eyes
		Vector3D betweenTheEyes = new Vector3D(0, 0, 0);
		for(int i=0; i<eyePositions.length; i++)
			betweenTheEyes = Vector3D.sum(betweenTheEyes, eyePositions[i]);
		betweenTheEyes = betweenTheEyes.getProductWith(1./eyePositions.length);
		setPinholePosition(betweenTheEyes);

		setEyePositions(eyePositions);
		setDotsPerPixel(dotsPerPixel);
		setDotRadius(dotRadius);
	}
	
	/**
	 * Create a clone of the original
	 * @param original
	 */
	public MultiSurfaceAutostereogramCamera(MultiSurfaceAutostereogramCamera original)
	{
		super(original);
		
		setEyePositions(original.getEyePositions().clone());
		setDotsPerPixel(original.getDotsPerPixel());
		setDotRadius(original.getDotRadius());
	}
	
	@Override
	public MultiSurfaceAutostereogramCamera clone()
	{
		return new MultiSurfaceAutostereogramCamera(this);
	}
	

	/* setters and getters */
	
	public Vector3D[] getEyePositions() {
		return eyePositions;
	}

	public void setEyePositions(Vector3D[] eyePositions) {
		this.eyePositions = eyePositions;
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
	
	/**
	 * Calculate the saturation factor of a pixel that is a distance r from a dot's centre
	 * @param rSquared	r^2
	 * @return	the saturation factor that corresponds to a distance r 
	 */
	private double getDistanceSaturationFactor(double rSquared)
	{
		// System.out.println(rSquared);
		return expTable[(int)(rSquared/32.0*(expPoints-1)+0.5)];
	}

	/**
	 * @param i
	 * @param j
	 * @param dotHue	a hue in the range 0..2 pi
	 * @param dotSaturation
	 * @return true if any pixels were affected
	 */
	private boolean placeDot(double i, double j, double dotHue, double dotSaturation)
	{
		// ccd.setPixelColour((int)(i+0.5), (int)(j+0.5), colour.getRGB());
		
		double
			oneOverDotRadius = 1./dotRadius,
			cos = Math.cos(dotHue),	// dot hue, interpreted as polar angle, represented in Cartesian coordinates
			sin = Math.sin(dotHue);
		
		// calculate the coordinate range of the affected pixels
		int 
			hMin = Math.max(0, (int)(i - 3*dotRadius + 0.5)),	// (int)(i+0.5) - dotRadius,
			vMin = Math.max(0, (int)(j - 3*dotRadius + 0.5)),	// (int)(j+0.5) - dotRadius,
			hMax = Math.min(ccd.getDetectorPixelsHorizontal(), (int)(i + 3*dotRadius + 0.5)),
			vMax = Math.min(ccd.getDetectorPixelsVertical()  , (int)(j + 3*dotRadius + 0.5));

		// go through all the affected pixels
		for(int h = hMin; h < hMax; h++)
		{
			for(int v = vMin; v < vMax; v++)
			{
				// the saturation factor falls off with distance from the dot centre;
				// calculate the saturation factor corresponding to the current pixel
				double rSquared = MyMath.square(oneOverDotRadius*(i-h)) + MyMath.square(oneOverDotRadius*(j-v));
				double f = dotSaturation*getDistanceSaturationFactor(rSquared);

				// add the Cartesian coordinates of the saturation-weighted dot hue to the pixel's weighted hue
				cosHues[h][v] += f*cos;
				sinHues[h][v] += f*sin;
			}
		}
		
		// have any CCD pixels been affected?
		return (hMax > hMin) && (vMax > vMin);
	}
	
	/**
	 * @param dotPosition
	 * @param dotHue	a hue in the range 0..2 pi
	 * @param dotSaturation
	 * @return true if any pixels were affected
	 */
	private boolean placeDot(Vector3D dotPosition, double dotHue, double dotSaturation)
	{
		Vector2D coordinates = ccd.getSurfaceCoordinates(dotPosition);
		
		return placeDot(
				coordinates.x * (getDetectorPixelsHorizontal()-1.0),	// horizontal coordinate of dot
				coordinates.y * (getDetectorPixelsVertical()-1.0),	// vertical coordinate of dot
				dotHue,
				dotSaturation
			);
	}

	
	/**
	 * @param dotPosition	position of this dot
	 * @param dotHue	hue of this dot (polar angle)
	 * @param dotSaturation	saturation of this dot
	 * @param scenes	the surfaces
	 * @param iEye1Previous	index of the eye 1 from which this dot position was derived
	 * @param iEye2Previous	index of the eye 2 from which this dot position was derived
	 * @param iScenePrevious	index of the scene from which this dot position was derived
	 * @return	the number of dots that have been added to the image
	 */
	private int placeDotAndDaughterDots(
			Vector3D dotPosition,
			double dotHue,
			double dotSaturation,
			SceneObject[] scenes,
			int iEye1Previous,
			int iEye2Previous,
			int iScenePrevious
		)
	{
		int noOfPlacedDots = 0;
		
		// first place the "mother dot"
		boolean pixelsAffected = placeDot(dotPosition, dotHue, dotSaturation);
		
		// if that didn't affect any pixels we can stop here
		if(!pixelsAffected) return(noOfPlacedDots);
		
		// do the following only if placing the "mother dot" actually affected any CCD pixels,
		// i.e. if the placeDot method returns "true"
		noOfPlacedDots++;	// increase the number of placed dots by 1
		
		// the saturation of the daughter dots is reduced by a fixed factor
		dotSaturation *= 0.7;	// 0.5 is too low
		
		// is the saturation of the daughter dots is below the preset threshold, we don't need to draw them
		if(dotSaturation < 0.1)	// this works okay
			return(noOfPlacedDots);
			
		// System.out.println("number of eye positions = " + getEyePositions().length + ", number of surfaces = " + scenes.getNumberOfSceneObjects());
		
		for(int iEye1=0; iEye1 < getEyePositions().length; iEye1++)
			for(int iEye2=0; iEye2 < getEyePositions().length; iEye2++)
				for(int iScene=0; iScene < scenes.length; iScene++)
					// System.out.println("iEye1 = " + iEye1 + ", iEye2 = "+ iEye2 + ", iScene = " + iScene);
					// if the "mother dot" was created from its mother dot (the "mother mother dot") by calculating the point where
					// eye number iEye2's line of vision through the mother mother dot intersects scene number
					// iScene, and then calculating the position on the CCD where eye number iEye1 sees that point,
					// then don't do the following step, because it would simply find the mother mother dot again
					if(
							(iEye1 != iEye2) && 
							((iEye1 != iEye2Previous) || (iEye2 != iEye1Previous) || (iScene != iScenePrevious))
						)
					{
						// System.out.println("iEye1 = " + iEye1 + ", iEye2 = "+ iEye2 + ", iScene = " + iScene);
						
						// calculate the intersection point with the scene the left eye sees in the direction of the dot
						// find the position of the next 
						Vector3D nextDotPosition = findNextDotPosition(
								dotPosition,
								getEyePositions()[iEye1],
								getEyePositions()[iEye2],
								scenes[iScene]
							);

						if(nextDotPosition != null)
							noOfPlacedDots += placeDotAndDaughterDots(
									nextDotPosition,
									dotHue,
									dotSaturation,
									scenes,
									iEye1,
									iEye2,
									iScene
							);
					}
		
		return(noOfPlacedDots);
	}

	/* 
	 * Take a photo.  Here this can't be done for each pixel independently, so overriding the
	 * calculatePixelColour method is not sufficient.  Therefore override the takePhoto method.
	 * @see optics.raytrace.core.Camera#takePhoto(optics.raytrace.core.SceneObject, optics.raytrace.core.LightSource, optics.raytrace.GUI.core.RaytraceWorker)
	 */

	
	/**
	 * @param scenes
	 * @param lights
	 * @param raytraceWorker
	 * @return
	 */
	public BufferedImage takePhoto(
			SceneObject[] scenes,
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
		
		// make sure we get more than the minimum number of dots
		while(nDots < nDotsMax)
		{
			// start with a random colour for this dot (and its "daughter dots")...
			double dotHue = 2*Math.PI*Math.random();
				
			// ... and find a random position for the "mother dot" by placing it on a
			// randomly picked CCD pixel
			double i = Math.random() * (ccd.getDetectorPixelsHorizontal()-1);	// horizontal index of pixel
			double j = Math.random() * (ccd.getDetectorPixelsVertical()-1);	// vertical index of pixel
			Vector3D dotPosition = ccd.getPositionOnPixel((int)(i+0.5), (int)(j+0.5));
			
			// now place the dot and its daughter dots
			nDots += placeDotAndDaughterDots(
					dotPosition,
					dotHue,
					1,	// saturation of the "mother dot"
					scenes,
					// the dot was not created from any "mother dot", so...
					-1,	// ... there is no iEye1Previous, ...
					-1,	// ... nor an iEye2Previous, ...
					-1	// ... nor an iScenePrevious
				);

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
				raytraceWorker.setStatus(description + " rendering... (" + (int)(100.*Math.min(nDots,nDotsMax)/nDotsMax) + "% completed.)");

				// the proper way of giving feedback, which doesn't work though...
				// raytraceWorker.setProgress((int)(100.*j/ccd.getDetectorPixelsVertical()));
				// raytraceWorker.publish();	// doesn't work
			}
			else
			{
				// otherwise print feedback onto the console
				System.out.println("Rendered " + nDots + " out of " + nDotsMax + "dots");
			}

		}	// while(nDots < nDotsMax)

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
	
	/* 
	 * Take a photo.  Here this can't be done for each pixel independently, so overriding the
	 * calculatePixelColour method is not sufficient.  Therefore override the takePhoto method.
	 * @see optics.raytrace.core.Camera#takePhoto(optics.raytrace.core.SceneObject, optics.raytrace.core.LightSource, optics.raytrace.GUI.core.RaytraceWorker)
	 */
	@Override
	public BufferedImage takePhoto(
			SceneObject scene,
			LightSource lights,
			RaytraceWorker raytraceWorker
		)
	{
		SceneObject[] scenes = new SceneObject[1];
		scenes[0] = scene;
		
		return takePhoto(scenes, lights, raytraceWorker);
	}

	
	/**
	 * Find the point where the line of sight from eye 1 to the dot intersects the scene.
	 * Then find the point where the line of sight from eye 2 to that point intersects the autostereogram plane.
	 * This position is then the next dot position.
	 * 
	 * @param dotPosition
	 * @param eye1Position
	 * @param eye2Position
	 * @param scene
	 * @return	the next dot position
	 */
	private Vector3D findNextDotPosition(
			Vector3D dotPosition,
			Vector3D eye1Position,
			Vector3D eye2Position,
			SceneObject scene
		)
	{
		Ray ray = new Ray(
				eye1Position,
				Vector3D.difference(
						dotPosition,
						eye1Position
					),
				0	// start time of ray --- not important here (?)
			);

		Vector3D sceneIntersectionPoint = scene.getClosestRayIntersection(ray).p;
		
		if(sceneIntersectionPoint == null) return null;

		ray = new Ray(
				eye2Position,
				Vector3D.difference(
						sceneIntersectionPoint,
						eye2Position
				),
				0	// start time of ray --- not important here (?)
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
