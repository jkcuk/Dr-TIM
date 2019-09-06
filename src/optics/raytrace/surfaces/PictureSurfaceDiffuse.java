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
public class PictureSurfaceDiffuse extends SurfaceProperty implements SurfacePropertyWithControllableShadow
{
	private static final long serialVersionUID = -6883416335681441632L;

	// Each position on the ParametrisedSurface is parametrised by two parameters, x and y.
	// Note that x and y are not necessarily Cartesian coordinates!
	// The following variables define the range of x and y parameters the picture gets mapped into.
	private double
		xMin, xMax,	// x range
		yMin, yMax;	// y range
	private int width, height;	// width and height (in pixels) of the image
	private boolean tiled;	// repeat the image over and over?
	
	// the image is interpreted as the diffuse colour;
	// this is the specular colour that completes a SurfaceColour
	private DoubleColour specularColour;
	
	/**
	 * the picture itself is held as a 2D array of ints, each representing a 24-bit RGB value
	 * (Previously it was held as a 2D array of DoubleColours, which slowed everything down immensely.)
	 * Null if no picture is initialised or if there was a problem.
	 */
//	DoubleColour[][] picture;
//	int[][] picture;
	private BufferedImage picture;
	
	private boolean shadowThrowing;
	
	/**
	 * Loads a bitmap and sets the range of x and y (the two parameters in terms of which the surface is parametrised) into which the picture will be mapped.
	 *
	 * @param filename
	 * @param tiled	repeat the picture over and over?
	 * @param xMin
	 * @param xMax
	 * @param yMin
	 * @param yMax
	 * @param specularColour
	 */
	public PictureSurfaceDiffuse(File file, boolean tiled, double xMin, double xMax, double yMin, double yMax, DoubleColour specularColour, boolean shadowThrowing)
	{	
		super();

		setPicture(file);
		
		this.tiled = tiled;
		this.xMin = xMin;
		this.xMax = xMax;
		this.yMin = yMin;
		this.yMax = yMax;
		this.specularColour = specularColour;
		
		this.shadowThrowing = shadowThrowing;
	}

	/**
	 * Loads a bitmap and sets the range of x and y (the two parameters in terms of which the surface is parametrised) into which the picture will be mapped.
	 *
	 * @param filename
	 * @param tiled
	 * @param xMin
	 * @param xMax
	 * @param yMin
	 * @param yMax
	 * @param specularColour
	 */
	public PictureSurfaceDiffuse(String filename, boolean tiled, double xMin, double xMax, double yMin, double yMax, DoubleColour specularColour)
	{	
		super();

		setPicture(filename);
		
		this.tiled = tiled;
		this.xMin = xMin;
		this.xMax = xMax;
		this.yMin = yMin;
		this.yMax = yMax;
		this.specularColour = specularColour;
		shadowThrowing = true;
	}
		
	/**
	 * Loads a URL and sets the range of x and y (the two parameters in terms of which the surface is parametrised) into which the picture will be mapped.
	 *
	 * @param imgURL
	 * @param tiled	
	 * @param xMin
	 * @param xMax
	 * @param yMin
	 * @param yMax
	 * @param specularColour
	 */
	public PictureSurfaceDiffuse(java.net.URL imgURL, boolean tiled, double xMin, double xMax, double yMin, double yMax, DoubleColour specularColour)
	{	
		super();

		setPicture(imgURL);
		
		this.tiled = tiled;
		this.xMin = xMin;
		this.xMax = xMax;
		this.yMin = yMin;
		this.yMax = yMax;
		this.specularColour = specularColour;
		shadowThrowing = true;
	}
		
	/**
	 * Constructor that clones the original.
	 * Note that the picture is <b>not</b> copied; instead, the BufferedImage picture in
	 * the cloned PictureSurfaceDiffuse is the same as the BufferedImage picture in the original.
	 * @param original
	 */
	public PictureSurfaceDiffuse(PictureSurfaceDiffuse original)
	{
		super();

		setPicture(original.getPicture());

		tiled = original.isTiled();
		xMin = original.getxMin();
		xMax = original.getxMax();
		yMin = original.getyMin();
		yMax = original.getyMax();
		specularColour = original.getSpecularColour();
		shadowThrowing = true;
	}
	
	/*
	 * Clones this PictureSurfaceDiffuse.
	 * Note that the picture is <b>not</b> copied; instead, the BufferedImage picture in
	 * the cloned PictureSurfaceDiffuse is the same as the BufferedImage picture in the original.
	 * @see java.lang.Object#clone()
	 */
	@Override
	public PictureSurfaceDiffuse clone()
	{
		return new PictureSurfaceDiffuse(this);
	}

//	public DoubleColour[][] getPicture() {
	public BufferedImage getPicture() {
		return picture;
	}

	public void setPicture(File file)
	{
		if(file == null)
		{
			picture = null;
			width = 0;
			height = 0;
		}
		else
		{
			// Load image and handle any error encountered.
			try {
				picture = ImageIO.read(file);
				
				// Get the image size
				width = picture.getWidth();
				height = picture.getHeight();
			}
			catch (IOException e)
			{
				System.err.println("PictureSurfaceDiffuse::setPicture: Error while loading image '" + file.getName() +"'" );
				// e.printStackTrace();
				picture = null;
				width = 0;
				height = 0;
				// System.exit(1);
			}
		}
	}

	public void setPicture(String filename)
	{
		setPicture(new File(filename));
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
		else
		{
			width = 0;
			height = 0;
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
	        System.err.println("PictureSurfaceDiffuse::setPicture: Couldn't find file " + imageURL);
	        picture = null;
	        width = 0;
	        height = 0;
	        // setPicture(new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB));
	    }

	}

	public boolean isTiled() {
		return tiled;
	}

	public void setTiled(boolean tiled) {
		this.tiled = tiled;
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

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public DoubleColour getSpecularColour() {
		return specularColour;
	}

	public void setSpecularColour(DoubleColour specularColour) {
		this.specularColour = specularColour;
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
		
		if(isTiled())
		{
			if(picture == null) return DoubleColour.ORANGE;	// no picture? make it orange
			if(l == null) return DoubleColour.BLACK;	// no lights? make it black

			DoubleColour colour = new DoubleColour(
					picture.getRGB(
							((int)((x-xMin)/(xMax - xMin) * (width-1))) % width,	// TODO	sort out the strange behaviour of the modulo function for negative values
							((int)((y-yMin)/(yMax - yMin) * (height-1))) % height	// TODO	sort out the strange behaviour of the modulo function for negative values
					));

			return l.getColour(new SurfaceColour(colour, specularColour, true), scene, i, ray, traceLevel);
		}
		else
		{
			if(MyMath.isBetween(x, xMin, xMax) && MyMath.isBetween(y, yMin, yMax))	// (x,y) in range?
			{
				// yes, x and y are in the range onto which the image is mapped

				if(picture == null) return DoubleColour.ORANGE;	// no picture? make it orange
				if(l == null) return DoubleColour.BLACK;	// no lights? make it black

				//System.out.println("mapped pixel: [" + (int)((x-xMin)/(xMax - xMin) * width) + "][" + (int)((y-yMin)/(yMax - yMin) * height) + "]");
				DoubleColour colour = new DoubleColour(
						picture.getRGB(
								(int)((x-xMin)/(xMax - xMin) * (width-1)),
								(int)((y-yMin)/(yMax - yMin) * (height-1))
						));

				return l.getColour(new SurfaceColour(colour, specularColour, true), scene, i, ray, traceLevel);
			}
			else
			{
				// the intersection is outside the area covered by the picture;
				// the surface is then transparent, so keep tracing
				// launch a new ray from here

				return scene.getColourAvoidingOrigin(
						ray.getBranchRay(
								i.p,
								ray.getD(),
								i.t
						),
						i.o,
						l,
						scene,
						traceLevel-1,
						raytraceExceptionHandler
				);
			}
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