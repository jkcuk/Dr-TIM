package optics.raytrace.core;

import math.Vector3D;
import optics.raytrace.surfaces.ImagingDirection;

/**
 * This describes the sense in which light rays pass through a surface.
 * Possible values are INWARDS and OUTWARDS.
 * 
 * Note that each SceneObjectPrimitive in TIM has an inside an an outside, defined by the
 * getNormalisedOutwardsSurfaceNormal method.
 * 
 * @see optics.raytrace.core.SceneObjectPrimitive#getNormalisedOutwardsSurfaceNormal(Vector3D p)
 * @author johannes
 */
public enum Orientation
{
	INWARDS(ImagingDirection.POS2NEG, -1),
	OUTWARDS(ImagingDirection.NEG2POS, 1);
	
	private ImagingDirection imagingDirection;
	
	/**
	 * the sign of the scalar product between light-ray direction and outwards-facing surface normal
	 */
	private double scalarProductSign;
	
	private Orientation(ImagingDirection imagingDirection, double scalarProductSign)
	{
		this.imagingDirection = imagingDirection;
		this.scalarProductSign = scalarProductSign;
	}
	
	/**
	 * Is the ray direction pointing outwards or inwards?
	 * @param rayDirection
	 * @param outwardsNormal
	 * @return	OUTWARDS if pointing outwards, INWARDS if pointing inwards
	 */
	public static Orientation getOrientation(Vector3D rayDirection, Vector3D outwardsNormal)
	{
		if(Vector3D.scalarProduct(rayDirection, outwardsNormal) > 0.)
		{
			return Orientation.OUTWARDS;
		}
		else
		{
			return Orientation.INWARDS;
		}
	}
		
	/**
	 * @param ray
	 * @param i
	 * @return	INWARDS or OUTWARDS
	 */
	public static Orientation getOrientation(Ray ray, RaySceneObjectIntersection i)
	{
		if((ray == null) || (i==null)) return null;
		return getOrientation(ray.getD(), i.getNormalisedOutwardsSurfaceNormal());
	}

	/**
	 * @param ray
	 * @param outwardsNormal
	 * @return
	 */
	public static Orientation getOrientation(Ray ray, Vector3D outwardsNormal)
	{
		if(ray == null) return null;
		return getOrientation(ray.getD(), outwardsNormal);
	}

	public static Orientation getReverseOrientation(Orientation orientation)
	{
		if(orientation == Orientation.OUTWARDS)
		{
			return Orientation.INWARDS;
		}
		else
		{
			return Orientation.OUTWARDS;
		}
	}
		
	public ImagingDirection toImagingDirection()
	{
		return imagingDirection;
	}
	
	public double getScalarProductSign()
	{
		return scalarProductSign;
	}
}