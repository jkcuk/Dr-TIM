package optics.raytrace.sceneObjects;

import java.io.*;
import java.util.ArrayList;

import javax.imageio.*;

import java.awt.image.*;

import optics.raytrace.core.ParametrisedObject;
import optics.raytrace.core.Ray;
import optics.raytrace.core.RaySceneObjectIntersection;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.SceneObjectClass;
import optics.raytrace.core.SceneObjectPrimitive;
import optics.raytrace.core.Transformation;
import math.MyMath;
import math.Vector3D;
import math.Vector2D;


/**
 * A wrapper class for scene objects which "masks" scene objects with a binary mask.
 * The mask is represented by a picture file; blue RGB component >= 127 means surface
 * is present there, otherwise it isn't.
 * The scene object has to be a ParametrisedSurface for this to work.
 * 
 * NOT TESTED!
 * 
 * @author Johannes Courtial
 */
public class MaskedSceneObject extends SceneObjectClass 
{
	private static final long serialVersionUID = -5455327577548740269L;

	private SceneObject sceneObject;	// the scene object being masked
	private ParametrisedObject parametrisedSurface;	// also the scene object

	// Each position on the ParametrisedSurface is parametrised by two parameters, x and y.
	// Note that x and y are not necessarily Cartesian coordinates!
	// The following variables define the range of x and y parameters the picture gets mapped into.
	private double
		xMin, xMax,	// x range
		yMin, yMax;	// y range

	private int maskImagePixelWidth, maskImagePixelHeight;	// width and height (in pixels) of the image

	/**
	 * the mask image
	 */
	private BufferedImage maskImage;
	private String filename;

	/**
	 * Loads a mask image, sets the range of x and y
	 * (the two parameters in terms of which the surface is parametrised)
	 * into which the mask image will be mapped,
	 * and sets the surface property that corresponds to the mask image having a
	 * colour that indicates that the surface is present there (the blue RGB component is >127).
	 * 
	 * @param sceneObject the scene object to be masked
	 * @param filename the image being interpreted as the mask
	 * @param xMin
	 * @param xMax
	 * @param yMin
	 * @param yMax
	 */
	public MaskedSceneObject(
			SceneObject sceneObject,
			String filename, 
			double xMin, double xMax, double yMin, double yMax
	)
	{	
		super("<MaskedSceneObject> " + sceneObject.getDescription() + "</MaskedSceneObject>", sceneObject.getParent(), sceneObject.getStudio());

		// check if the scene object implements ParametrisedSurface, as required
		//		if(!(sceneObject instanceof ParametrisedSurface))
		//			throw new RayTraceException("sceneObject does not implement ParametrisedSurface");

		setSceneObject(sceneObject);

		setMaskImage(filename);

		this.xMin = xMin;
		this.xMax = xMax;
		this.yMin = yMin;
		this.yMax = yMax;
	}
	
	/**
	 * Constructor that clones the original.
	 * Note that the maskImage is <b>not</b> copied; instead, the BufferedImage picture in
	 * the cloned MaskedSceneObject is the same as the BufferedImage maskImage in the original.
	 * @param original
	 */
	public MaskedSceneObject(MaskedSceneObject original)
	{
		super(original.description, original.getParent(), original.getStudio());
		
		setSceneObject(original.getSceneObject().clone());

		xMin = original.getxMin();
		xMax = original.getxMax();
		yMin = original.getyMin();
		yMax = original.getyMax();

		setMaskImage(original.getMaskImage());
		filename = original.getFilename();
	}
	
	/*
	 * Clones this MaskedSceneObject.
	 * Note that the maskImage is <b>not</b> copied; instead, the BufferedImage picture in
	 * the cloned MaskedSceneObject is the same as the BufferedImage maskImage in the original.
	 * @see java.lang.Object#clone()
	 */
	@Override
	public MaskedSceneObject clone()
	{
		return new MaskedSceneObject(this);
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

	public BufferedImage getMaskImage() {
		return maskImage;
	}

	public void setMaskImage(String filename) {
		// Load image and handle any error encountered.
		try {
			maskImage = ImageIO.read(new File (filename));


			// Get the image size
			maskImagePixelWidth = maskImage.getWidth();
			maskImagePixelHeight = maskImage.getHeight();

			this.filename = filename;
		} catch (IOException e) {
			System.err.println("MaskedSceneObject::setMaskImage: Error while loading image '" +filename+"'" );
			maskImage = null;
			// System.exit(1);
		}
	}
	
	public void setMaskImage(BufferedImage maskImage)
	{
		this.maskImage = maskImage;
		
		if(maskImage != null)
		{
			// Get the image size
			maskImagePixelWidth = maskImage.getWidth();
			maskImagePixelHeight = maskImage.getHeight();
		}
	}

	public int getMaskImagePixelWidth() {
		return maskImagePixelWidth;
	}

	public int getMaskImagePixelHeight() {
		return maskImagePixelHeight;
	}

	public SceneObject getSceneObject() {
		return sceneObject;
	}

	public void setSceneObject(SceneObject sceneObject)
	{
		// Check that the sceneObject object is valid in that it has surface parameters.
		// This will throw some form of exception if it isn't.
		this.parametrisedSurface = (ParametrisedObject)sceneObject;

		// sceneObject seems to be of the right type, so continue
		this.sceneObject = sceneObject;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	/**
	 * At what point on the object did the ray intersect?  If the intersection didn't take place then 
	 * RaySceneObjectIntersection.NO_INTERSECTION should be returned.
	 * @param ray 
	 * @return The closest intersection between the ray and this SceneObject or any SceneObject contained in it.
	 */
	@Override
	public RaySceneObjectIntersection getClosestRayIntersection(Ray ray)
	{
		// calculate the intersection with the unmasked object
		RaySceneObjectIntersection i = sceneObject.getClosestRayIntersection(ray);

		while(i != RaySceneObjectIntersection.NO_INTERSECTION)
		{
			// There is an intersection with the unmasked object.
			// Check whether or not the intersection point is masked or not

			//Retrieve coordinates of ray intersection (Returns parameters)
			Vector2D xy = parametrisedSurface.getSurfaceCoordinates(i.p);

			double 
			x = xy.x,
			y = xy.y;

			//System.out.println("coordinate on parametrised surface is (" + x + "," + y + ")");

			if(MyMath.isBetween(x, xMin, xMax) && MyMath.isBetween(y, yMin, yMax))	// (x,y) in range?
			{
				// yes, x and y are in the range onto which the image is mapped
				
				// if the image is not defined, return the intersection
				if(maskImage == null) return i;

				// get the relevant colour from the mask image
				int colour = maskImage.getRGB(
						(int)((x-xMin)/(xMax - xMin) * maskImagePixelWidth),
						(int)((y-yMin)/(yMax - yMin) * maskImagePixelHeight)
				);

				// is the blue component (colour & 255) >= 127?
				if((colour & 255) >= 127)
				{
					// yes, so this is an intersection
					return i;
				}
			}
			
			// check the next intersection between the ray and this object
			i = sceneObject.getNextClosestRayIntersection(ray, i);
		}

		// there is no intersection with the unmasked object, so there is certainly
		// no intersection with the masked object
		return RaySceneObjectIntersection.NO_INTERSECTION;
	}

	@Override
	public RaySceneObjectIntersection getClosestRayIntersectionWithShadowThrowingSceneObject(Ray ray)
	{
		// calculate the intersection with the unmasked object
		RaySceneObjectIntersection i = sceneObject.getClosestRayIntersectionWithShadowThrowingSceneObject(ray);

		while(i != RaySceneObjectIntersection.NO_INTERSECTION)
		{
			// There is an intersection with the unmasked object.
			// Check whether or not the intersection point is masked or not

			//Retrieve coordinates of ray intersection (Returns parameters)
			Vector2D xy = parametrisedSurface.getSurfaceCoordinates(i.p);

			double 
			x = xy.x,
			y = xy.y;

			//System.out.println("coordinate on parametrised surface is (" + x + "," + y + ")");

			if(MyMath.isBetween(x, xMin, xMax) && MyMath.isBetween(y, yMin, yMax))	// (x,y) in range?
			{
				// yes, x and y are in the range onto which the image is mapped

				// if the image is not defined, return the intersection
				if(maskImage == null) return i;

				// get the relevant colour from the mask image
				int colour = maskImage.getRGB(
						(int)((x-xMin)/(xMax - xMin) * maskImagePixelWidth),
						(int)((y-yMin)/(yMax - yMin) * maskImagePixelHeight)
				);

				// is the blue component (colour & 255) >= 127?
				if((colour & 255) >= 127)
				{
					// yes, so this is an intersection
					return i;
				}
			}

			// check the next intersection between the ray and this object
			i = sceneObject.getNextClosestRayIntersectionWithShadowThrowingSceneObject(ray, i);
		}

		// there is no intersection with the unmasked object, so there is certainly
		// no intersection with the masked object
		return RaySceneObjectIntersection.NO_INTERSECTION;
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.sceneObjects.SceneObject#getClosestRayIntersectionAvoidingOrigin(optics.raytrace.core.Ray, optics.raytrace.sceneObjects.SceneObject)
	 */
	@Override
	public RaySceneObjectIntersection getClosestRayIntersectionAvoidingOrigin(Ray ray, SceneObjectPrimitive originObject)
	{
		// from SceneObjectPrimitive.getClosestRayIntersectionAvoidingOrigin
		if(sceneObject.getSceneObjectPrimitives().contains(originObject))
			// avoid calculating the intersection where the ray originated, calculate the intersection with a slightly advanced ray
			return getClosestRayIntersection(ray.getAdvancedRay(MyMath.TINY));
		return getClosestRayIntersection(ray);
	}

	@Override
	public RaySceneObjectIntersection getClosestRayIntersectionWithShadowThrowingSceneObjectAvoidingOrigin(Ray ray, SceneObjectPrimitive originObject)
	{
		// from SceneObjectPrimitive.getClosestRayIntersectionAvoidingOrigin
		if(sceneObject.getSceneObjectPrimitives().contains(originObject))
			// avoid calculating the intersection where the ray originated, calculate the intersection with a slightly advanced ray
			return getClosestRayIntersectionWithShadowThrowingSceneObject(ray.getAdvancedRay(MyMath.TINY));
		return getClosestRayIntersectionWithShadowThrowingSceneObject(ray);
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.sceneObjects.SceneObject#transform(optics.raytrace.sceneObjects.transformations.Transformation)
	 */
	@Override
	public SceneObject transform(Transformation t)
	{
		return new MaskedSceneObject(sceneObject.transform(t), filename, xMin, xMax, yMin, yMax);
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.sceneObjects.SceneObject#insideObject(math.Vector3D)
	 */
	@Override
	public boolean insideObject(Vector3D p)
	{
		return sceneObject.insideObject(p);
	}
	
	@Override
	public ArrayList<SceneObjectPrimitive> getSceneObjectPrimitives()
	{
		ArrayList<SceneObjectPrimitive> SOPs = new ArrayList<SceneObjectPrimitive>();
		
		SOPs.addAll(sceneObject.getSceneObjectPrimitives());

		return SOPs;
	}
	
	@Override
	public String getType()
	{
		return "Masked scene object";
	}
}