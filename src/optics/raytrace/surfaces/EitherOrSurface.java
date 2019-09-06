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
 * A surface property representing surfaces with a binary bitmap (represented by a picture file)
 * which decides which one of two surface properties is applicable at different points.
 * The surface has to be a ParametrisedSurface for this to work.
 * 
 * See also MaskedSceneObject.
 * 
 * @author Johannes Courtial
 */
public class EitherOrSurface extends SurfaceProperty implements SurfacePropertyWithControllableShadow
{
	private static final long serialVersionUID = 7457784376225449911L;

	// Each position on the ParametrisedSurface is parametrised by two parameters, x and y.
	// Note that x and y are not necessarily Cartesian coordinates!
	// The following variables define the range of x and y parameters the picture gets mapped into.
	private double
		xMin, xMax,	// x range
		yMin, yMax;	// y range

	private SurfaceProperty
	surfaceProperty0, surfaceProperty1;	// the surface properties corresponding to black and white in the picture file
	
	private int width, height;	// width and height (in pixels) of the image
		
	/**
	 * the picture itself is held as a 2D array of ints, each representing a 24-bit RGB value
	 * (Previously it was held as a 2D array of DoubleColours, which slowed everything down immensely.)
	 */
	private BufferedImage picture;
	
	private boolean shadowThrowing = true;
	
	/**
	 * Loads a mask image, sets the range of x and y
	 * (the two parameters in terms of which the surface is parametrised)
	 * into which the mask image will be mapped,
	 * and sets the two surface properties that correspond to the mask image having one of
	 * two values:  if the blue RGB component is <127, then surfaceProperty0 is taken as the surface
	 * property, otherwise surfaceProperty1.
	 *
	 * @param filename the image being interpreted as the mask
	 * @param xMin
	 * @param xMax
	 * @param yMin
	 * @param yMax
	 * @param surfaceProperty0 the surface property corresponding to colour black in the mask image
	 * @param surfaceProperty1 the surface property corresponding to colour white in the mask image
	 */
	public EitherOrSurface(
			String filename, 
			double xMin, double xMax, double yMin, double yMax,
			SurfaceProperty surfaceProperty0, SurfaceProperty surfaceProperty1
	)
	{
		super();
		
		setPicture(filename);
		
		this.xMin = xMin;
		this.xMax = xMax;
		this.yMin = yMin;
		this.yMax = yMax;
		
		this.surfaceProperty0 = surfaceProperty0;
		this.surfaceProperty1 = surfaceProperty1;
	}

	/**
	 * @param picture
	 * @param xMin
	 * @param xMax
	 * @param yMin
	 * @param yMax
	 * @param surfaceProperty0
	 * @param surfaceProperty1
	 */
	public EitherOrSurface(
			BufferedImage picture, 
			double xMin, double xMax, double yMin, double yMax,
			SurfaceProperty surfaceProperty0, SurfaceProperty surfaceProperty1
	)
	{
		super();
		
		setPicture(picture);
		
		this.xMin = xMin;
		this.xMax = xMax;
		this.yMin = yMin;
		this.yMax = yMax;
		
		this.surfaceProperty0 = surfaceProperty0;
		this.surfaceProperty1 = surfaceProperty1;
	}

	
	/**
	 * Constructor that clones the original.
	 * Note that the picture is <b>not</b> copied; instead, the BufferedImage picture in
	 * the cloned EitherOrSurface is the same as the BufferedImage picture in the original.
	 * @param original
	 */
	public EitherOrSurface(EitherOrSurface original)
	{
		super();

		xMin = original.getxMin();
		xMax = original.getxMax();
		yMin = original.getyMin();
		yMax = original.getyMax();
		surfaceProperty0 = original.getSurfaceProperty0().clone();
		surfaceProperty1 = original.getSurfaceProperty1().clone();
		setPicture(original.getPicture());	// don't clone -- just copy
	}
	
	@Override
	public EitherOrSurface clone()
	{
		return new EitherOrSurface(this);
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

	public SurfaceProperty getSurfaceProperty0() {
		return surfaceProperty0;
	}

	public void setSurfaceProperty0(SurfaceProperty surfaceProperty0) {
		this.surfaceProperty0 = surfaceProperty0;
	}

	public SurfaceProperty getSurfaceProperty1() {
		return surfaceProperty1;
	}

	public void setSurfaceProperty1(SurfaceProperty surfaceProperty1) {
		this.surfaceProperty1 = surfaceProperty1;
	}

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
			System.out.println("EitherOrSurface::setPicture: Error while loading image '" +filename+"'" );
			e.printStackTrace();
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
	
	/**
	 * @param i
	 * @return	0 or 1 if the position of the intersection <i> falls within the range covered by the image, -1 otherwise
	 */
	public int getSurfaceTypeAtIntersection(RaySceneObjectIntersection i)
	{
		// Check that the intersecting object is valid in that it has surface parameters.
		ParametrisedObject o = (ParametrisedObject) i.o;
		//Retrieve coordinates of ray intersection (Returns parameters)
		Vector2D xy = o.getSurfaceCoordinates(i.p);

		// calculate the individual surface parameters
		double 
 		x = xy.x,
  		y = xy.y;
  		
		if(MyMath.isBetween(x, xMin, xMax) && MyMath.isBetween(y, yMin, yMax))	// (x,y) in range?
		{
			// yes, x and y are in the range onto which the image is mapped

			// get the relevant colour from the mask image
			int colour = picture.getRGB(
							(int)((x-xMin)/(xMax - xMin) * width),
							(int)((y-yMin)/(yMax - yMin) * height)
					);
			
			// let the blue component (colour & 255) decide which surface type it is...
			return ((colour & 255) < 127)?0:1;
		}
		else
		{
			// the intersection is outside the area covered by the picture;
			return -1;
		}
	}
	
	/* (non-Javadoc)
	 * @see optics.raytrace.surfaces.SurfaceProperty#getColour(optics.raytrace.core.Ray, optics.raytrace.core.RaySceneObjectIntersection, optics.raytrace.sceneObjects.SceneObject, optics.raytrace.lights.LightSource, int)
	 */
	@Override
	public DoubleColour getColour(Ray ray, RaySceneObjectIntersection i, SceneObject scene, LightSource l, int traceLevel, RaytraceExceptionHandler raytraceExceptionHandler)
	throws RayTraceException
	{
		if(traceLevel <= 0) return DoubleColour.BLACK;
		
		// find the intersected surface property
		SurfaceProperty intersectedSurfaceProperty;
		switch(getSurfaceTypeAtIntersection(i))
		{
		case -1:
			// the intersection is outside the area covered by the either-or surface
			intersectedSurfaceProperty = Transparent.PERFECT;
			break;
		case 0:
			intersectedSurfaceProperty = surfaceProperty0;
			break;
		case 1:
		default:
			intersectedSurfaceProperty = surfaceProperty1;
		}
		
		// let the intersected surface property do its business
		return intersectedSurfaceProperty.getColour(ray, i, scene, l, traceLevel-1, raytraceExceptionHandler);
//
//		
//		// Check that the parent object is valid in that it has surface parameters.
//		ParametrisedObject parent = (ParametrisedObject) i.o;
//		//Retrieve coordinates of ray intersection (Returns parameters)
//		Vector2D xy = parent.getSurfaceCoordinates(i.p);
//
//		double 
// 		x = xy.x,
//  		y = xy.y;
//  		
//		//System.out.println("coordinate on parametrised surface is (" + x + "," + y + ")");
//		
//		if(MyMath.isBetween(x, xMin, xMax) && MyMath.isBetween(y, yMin, yMax))	// (x,y) in range?
//		{
//			// yes, x and y are in the range onto which the image is mapped
//
//			if(picture == null) return DoubleColour.ORANGE;
//			
//			// get the relevant colour from the mask image
//			int colour = picture.getRGB(
//							(int)((x-xMin)/(xMax - xMin) * width),
//							(int)((y-yMin)/(yMax - yMin) * height)
//					);
//			
//			// let the blue component (colour & 255) decide which surface property it is...
//			SurfaceProperty surfaceProperty = ((colour & 255) < 127)?surfaceProperty0:surfaceProperty1;
//			
//			// ... and let that surface property do its business
//			return surfaceProperty.getColour(ray, i, scene, l, traceLevel-1, raytraceExceptionHandler);
//		}
//		else
//		{
//			// the intersection is outside the area covered by the picture;
//			// the surface is then transparent, so keep tracing
//			return scene.getColourAvoidingOrigin(
//					ray.getBranchRay(
//							i.p,
//							ray.getD(),
//							i.t
//					),
//					i.o,
//					l,
//					scene,
//					traceLevel-1,
//					raytraceExceptionHandler
//				);
//		}
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