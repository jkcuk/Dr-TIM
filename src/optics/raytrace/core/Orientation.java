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
	INWARDS(ImagingDirection.POS2NEG),
	OUTWARDS(ImagingDirection.NEG2POS);
	
	private ImagingDirection imagingDirection;
	
	private Orientation(ImagingDirection imagingDirection)
	{
		this.imagingDirection = imagingDirection;
	}
	
	/**
	 * @param ray
	 * @param i
	 * @return	INWARDS or OUTWARDS
	 */
	public static Orientation getRayOrientation(Ray ray, RaySceneObjectIntersection i)
	{
		if((ray == null) || (i==null)) return null;
		return getOrientation(ray.getD(), i.getNormalisedOutwardsSurfaceNormal());
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
}