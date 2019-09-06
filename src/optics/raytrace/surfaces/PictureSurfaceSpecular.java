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
public class PictureSurfaceSpecular extends SurfaceProperty implements SurfacePropertyWithControllableShadow
{
	private static final long serialVersionUID = 2626739439744067482L;

	// Each position on the ParametrisedSurface is parametrised by two parameters, x and y.
	// Note that x and y are not necessarily Cartesian coordinates!
	// The following variables define the range of x and y parameters the picture gets mapped into.
	private double
		xMin, xMax,	// x range
		yMin, yMax;	// y range
	private int width, height;	// width and height (in pixels) of the image
	
	// the image is interpreted as the specular colour;
	// this is the diffuse colour that completes a SurfaceColour
	private DoubleColour diffuseColour;
	
	/**
	 * the picture itself is held as a 2D array of ints, each representing a 24-bit RGB value
	 * (Previously it was held as a 2D array of DoubleColours, which slowed everything down immensely.)
	 */
//	DoubleColour[][] picture;
//	int[][] picture;
	private BufferedImage picture;
	
	private boolean shadowThrowing;
	
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
	public PictureSurfaceSpecular(String filename, double xMin, double xMax, double yMin, double yMax, DoubleColour diffuseColour, boolean shadowThrowing)
	{	
		super();

		setPicture(filename);
		
		this.xMin = xMin;
		this.xMax = xMax;
		this.yMin = yMin;
		this.yMax = yMax;
		this.diffuseColour = diffuseColour;
		
		this.shadowThrowing = shadowThrowing;
	}

	/**
	 * Constructor that clones the original.
	 * Note that the picture is <b>not</b> copied; instead, the BufferedImage picture in
	 * the cloned PictureSurfaceSpecular is the same as the BufferedImage picture in the original.
	 * @param original
	 */
	public PictureSurfaceSpecular(PictureSurfaceSpecular original)
	{
		super();

		xMin = original.getxMin();
		xMax = original.getxMax();
		yMin = original.getyMin();
		yMax = original.getyMax();
		setPicture(original.getPicture());
	}
	
	/*
	 * Clones this PictureSurfaceSpecular.
	 * Note that the picture is <b>not</b> copied; instead, the BufferedImage picture in
	 * the cloned PictureSurfaceSpecular is the same as the BufferedImage picture in the original.
	 * @see java.lang.Object#clone()
	 */
	@Override
	public PictureSurfaceSpecular clone()
	{
		return new PictureSurfaceSpecular(this);
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
			System.err.println("PictureSurfaceSpecular::setPicture: Error while loading image '" +filename+"'" );
			picture = null;
			// System.exit(1);
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

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public DoubleColour getDiffuseColour() {
		return diffuseColour;
	}

	public void setDiffuseColour(DoubleColour diffuseColour) {
		this.diffuseColour = diffuseColour;
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
			
			if(picture == null) return DoubleColour.ORANGE;
			
			if(l == null) return DoubleColour.BLACK;

			//System.out.println("mapped pixel: [" + (int)((x-xMin)/(xMax - xMin) * width) + "][" + (int)((y-yMin)/(yMax - yMin) * height) + "]");
			DoubleColour colour = new DoubleColour(
					picture.getRGB(
							(int)((x-xMin)/(xMax - xMin) * width),
							(int)((y-yMin)/(yMax - yMin) * height)
					));

			return l.getColour(new SurfaceColour(diffuseColour, colour, true), scene, i, ray, traceLevel);
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

	@Override
	public boolean isShadowThrowing() {
		return shadowThrowing;
	}


	@Override
	public void setShadowThrowing(boolean shadowThrowing) {
		this.shadowThrowing = shadowThrowing;
	}
}