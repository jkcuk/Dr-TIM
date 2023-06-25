package optics.raytrace.surfaces;

import java.io.*;

import javax.imageio.*;

import java.awt.image.*;

import optics.DoubleColour;
import optics.raytrace.core.LightSource;
import optics.raytrace.core.ParametrisedObject;
import optics.raytrace.core.Ray;
import optics.raytrace.core.RaySceneObjectIntersection;
import optics.raytrace.core.RaytraceExceptionHandler;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.SurfaceProperty;
import optics.raytrace.core.SurfacePropertyWithControllableShadow;
import optics.raytrace.exceptions.RayTraceException;
import math.MyMath;
import math.Vector2D;


/**
 * A surface property representing surfaces that have a picture on them.
 * The surface has to be a ParametrisedSurface for this to work.
 * 
 * @author George Constable
 */
public class PictureSurface extends SurfaceProperty implements SurfacePropertyWithControllableShadow
{
	private static final long serialVersionUID = 3381998647371752762L;

	// Each position on the ParametrisedSurface is parametrised by two parameters, x and y.
	// Note that x and y are not necessarily Cartesian coordinates!
	// The following variables define the range of x and y parameters the picture gets mapped into.
	private double
		xMin, xMax,	// x range
		yMin, yMax;	// y range
	private int width, height;	// width and height (in pixels) of the image
		
	/**
	 * the picture itself is held as a 2D array of ints, each representing a 24-bit RGB value
	 * (Previously it was held as a 2D array of DoubleColours, which slowed everything down immensely.)
	 */
//	DoubleColour[][] picture;
//	int[][] picture;
	private BufferedImage picture;
	
	private boolean shadowThrowing = true;
	
	private SurfaceProperty surfacePropertyOutsidePicture;
	
	/**
	 * Loads a bitmap and sets the range of x and y (the two parameters in terms of which the surface is parametrised) into which the picture will be mapped.
	 *
	 * @param filename	
	 * @param xMin
	 * @param xMax
	 * @param yMin
	 * @param yMax
	 * @param specularColour
	 */
	public PictureSurface(
			String filename, 
			double xMin, 
			double xMax, 
			double yMin, 
			double yMax, 
			boolean shadowThrowing
		)
	{	
		super();
		
		setPicture(filename);
		
		this.xMin = xMin;
		this.xMax = xMax;
		this.yMin = yMin;
		this.yMax = yMax;
		this.shadowThrowing = shadowThrowing;
		this.surfacePropertyOutsidePicture = Transparent.PERFECT;
	}

	/**
	 * Loads a bitmap and sets the range of x and y (the two parameters in terms of which the surface is parametrised) into which the picture will be mapped.
	 *
	 * @param imageURL	
	 * @param xMin
	 * @param xMax
	 * @param yMin
	 * @param yMax
	 * @param specularColour
	 */
	public PictureSurface(java.net.URL imageURL, double xMin, double xMax, double yMin, double yMax, boolean shadowThrowing)
	{	
		super();
		
		setPicture(imageURL);
		
		this.xMin = xMin;
		this.xMax = xMax;
		this.yMin = yMin;
		this.yMax = yMax;
		this.shadowThrowing = shadowThrowing;
		this.surfacePropertyOutsidePicture = Transparent.PERFECT;
	}

	/**
	 * Constructor that clones the original.
	 * Note that the picture is <b>not</b> copied; instead, the BufferedImage picture in
	 * the cloned PictureSurface is the same as the BufferedImage picture in the original.
	 * @param original
	 */
	public PictureSurface(PictureSurface original)
	{
		super();

		xMin = original.getxMin();
		xMax = original.getxMax();
		yMin = original.getyMin();
		yMax = original.getyMax();
		setPicture(original.getPicture());
		setSurfacePropertyOutsidePicture(original.getSurfacePropertyOutsidePicture());
	}
	
	/*
	 * Clones this PictureSurface.
	 * Note that the picture is <b>not</b> copied; instead, the BufferedImage picture in
	 * the cloned PictureSurface is the same as the BufferedImage picture in the original.
	 * @see java.lang.Object#clone()
	 */
	@Override
	public PictureSurface clone()
	{
		return new PictureSurface(this);
	}
		
	public double getxMin() {
		return xMin;
	}

	public void setxMin(double xMin) {
		this.xMin = xMin;
	}

	public double getxMax() {
		return xMax;
	}

	public void setxMax(double xMax) {
		this.xMax = xMax;
	}

	public double getyMin() {
		return yMin;
	}

	public void setyMin(double yMin) {
		this.yMin = yMin;
	}

	public double getyMax() {
		return yMax;
	}

	public void setyMax(double yMax) {
		this.yMax = yMax;
	}

//	public DoubleColour[][] getPicture() {
	public BufferedImage getPicture() {
		return picture;
	}

	public void setPicture(String filename) {
		// Load image and handle any error encountered.
		try {
			picture = ImageIO.read(new File (filename));
			
			// Get the image size
			width = picture.getWidth();
			height = picture.getHeight();
		} catch (IOException e) {
			System.err.println("PictureSurface::setPicture: Error while loading image '" +filename+"'");
			
			picture = null;
		}
	}

	public void setPicture(BufferedImage picture)
	{
		this.picture = picture;

		if(picture != null)
		{
			// Get the image size
			width = picture.getWidth();
			height = picture.getHeight();
		}
	}

	/**
	 * Set the picture from a URL.  The image can be in the JAR file...
	 * For some reason, .bmp files don't work, but .png files do.
	 * @param imageURL
	 */
	public void setPicture(java.net.URL imageURL)
	{
		// for some reason, .bmp files don't load here, but .png files do
		// java.net.URL imgURL = getClass().getResource(getClass().getSimpleName() + ".png");
	    if (imageURL != null) {
	    	// see http://docs.oracle.com/javase/tutorial/2d/images/loadimage.html
	    	BufferedImage buffI;
	    	
	    	try
	    	{
	    		buffI = ImageIO.read(imageURL);

		        setPicture(buffI);
	    	}
	    	catch (IOException e)
	    	{
				System.err.println("PictureSurfaceDiffuse::setPicture: Error while loading image '" +imageURL+"'" );
				// e.printStackTrace();
				picture = null;
				width = 0;
				height = 0;
				// System.exit(1);
	    	}
	    	
//	    	Image image = new ImageIcon(imageURL, "the picture").getImage();

//	    	// Create empty BufferedImage, sized to Image
//	        BufferedImage buffI = 
//	          new BufferedImage(
//	              image.getWidth(null), 
//	              image.getHeight(null), 
//	              BufferedImage.TYPE_INT_ARGB);
//
//	        // Draw Image into BufferedImage
//	        Graphics g = buffI.getGraphics();
//	        g.drawImage(biffI, 0, 0, null);
//
//	        setPicture(buffI);
	    }
	    else
	    {
	        System.err.println("PictureSurface::setPicture: Couldn't find file " + imageURL);
	        picture = null;
	        width = 0;
	        height = 0;
	        // setPicture(new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB));
	    }

	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public SurfaceProperty getSurfacePropertyOutsidePicture() {
		return surfacePropertyOutsidePicture;
	}

	public void setSurfacePropertyOutsidePicture(SurfaceProperty surfacePropertyOutsidePicture) {
		this.surfacePropertyOutsidePicture = surfacePropertyOutsidePicture;
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.surfaces.SurfaceProperty#getColour(optics.raytrace.core.Ray, optics.raytrace.core.RaySceneObjectIntersection, optics.raytrace.sceneObjects.SceneObject, optics.raytrace.lights.LightSource, int)
	 */
	@Override
	public DoubleColour getColour(Ray ray, RaySceneObjectIntersection i, SceneObject scene, LightSource l, int traceLevel, RaytraceExceptionHandler raytraceExceptionHandler)
	throws RayTraceException
	{
		if(traceLevel <= 0) return DoubleColour.BLACK;
		
		// Check that the parent object is valid in that it has surface parameters.
		ParametrisedObject parent = (ParametrisedObject) i.o;
		//Retrieve coordinates of ray intersection (Returns parameters)
		Vector2D xy = parent.getSurfaceCoordinates(i.p);

		double 
 		x = xy.x,
  		y = xy.y;
  		
		//System.out.println("coordinate on parametrised surface is (" + x + "," + y + ")");
				
		if(MyMath.isBetween(x, xMin, xMax) && MyMath.isBetween(y, yMin, yMax))	// (x,y) in range?
		{
			// yes, x and y are in the range onto which the image is mapped
			
			// if there is no picture present, return orange
			if(picture == null) return DoubleColour.ORANGE;

			//System.out.println("mapped pixel: [" + (int)((x-xMin)/(xMax - xMin) * width) + "][" + (int)((y-yMin)/(yMax - yMin) * height) + "]");
			return new DoubleColour(
					picture.getRGB(
							(int)((x-xMin)/(xMax - xMin) * width),
							(int)((y-yMin)/(yMax - yMin) * height)
					));
		}
		else
		{
			// the intersection is outside the area covered by the picture;
			
			return surfacePropertyOutsidePicture.getColour(ray, i, scene, l, traceLevel, raytraceExceptionHandler);
			
//			// the surface is then transparent, so keep tracing
//			// launch a new ray from here
//			
//			return scene.getColourAvoidingOrigin(
//				ray.getBranchRay(
//						i.p,
//						ray.getD(),
//						i.t,
//						ray.isReportToConsole()
//				),
//				i.o,
//				l,
//				scene,
//				traceLevel-1,
//				raytraceExceptionHandler
//			);
		}
	}

	@Override
	public boolean isShadowThrowing() {
		return shadowThrowing;
	}


	@Override
	public void setShadowThrowing(boolean shadowThrowing) {
		this.shadowThrowing = shadowThrowing;
	}
}