package optics.raytrace.surfaces;

import optics.raytrace.core.RaySceneObjectIntersection;
import optics.raytrace.core.SurfacePropertyPrimitive;
import optics.raytrace.exceptions.EvanescentException;
import optics.raytrace.exceptions.RayTraceException;

/**
 * A surface that rotates the incident light ray by a given angle in the plane of incidence.
 * Note that the angle of incidence is always positive, so a positive rotation angle rotates away from the normal.
 * 
 * @author johannes
 */
public class RayRotatingInPlaneOfIncidence extends RefractiveAngleFormulation
{
	private static final long serialVersionUID = -8374340687580380374L;
	
	/**
	 * the rotation angle (in radians); a positive rotation angle rotates the ray away from the normal
	 */
	private double rotationAngle;

	/**
	 * Create a surface property that rotates an incident light ray by a given rotation angle in the plane of incidence.
	 * Note that the angle of incidence is always positive, so a positive rotation angle rotates away from the normal.
	 * 
	 * @param rotationAngle (in radians)
	 * @param transmissionCoefficient
	 * @param shadowThrowing
	 */
	public RayRotatingInPlaneOfIncidence(double rotationAngle, double transmissionCoefficient, boolean shadowThrowing)
	{
		super(transmissionCoefficient, shadowThrowing);

		this.rotationAngle = rotationAngle;
	}

	/**
	 * Create a surface property that rotates an incident light ray by a given rotation angle in the plane of incidence.
	 * Note that the angle of incidence is always positive, so a positive rotation angle rotates away from the normal.
	 * 
	 * If this constructor is used, the transmission coefficient is the default transmission coefficient (96%)
	 * and the surface is shadow-throwing.
	 * 
	 * @param rotationAngle (in radians)
	 */
	public RayRotatingInPlaneOfIncidence(double rotationAngle)
	{
		this(
				rotationAngle,
				SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT,	// transmissionCoefficient
				true	// shadowThrowing
			);
	}

	/**
	 * Create a copy of the old surface property that rotates an incident light ray by a given rotation angle in the plane of incidence.
	 * 
	 * @param old
	 */
	public RayRotatingInPlaneOfIncidence(RayRotatingInPlaneOfIncidence old)
	{
		super(old);
		
		this.rotationAngle = old.getRotationAngle();
	}

	/**
	 * Clone this surface property that rotates an incident light ray by a given rotation angle in the plane of incidence.
	 */
	public RayRotatingInPlaneOfIncidence clone()
	{
		return new RayRotatingInPlaneOfIncidence(this);
	}
	
	//
	// getters & setters
	//
	
	/**
	 * @return	the rotation angle (in radians)
	 */
	public double getRotationAngle()
	{
		return rotationAngle;
	}

	/**
	 * @param rotationAngle
	 */
	public void setRotationAngle(double rotationAngle)
	{
		this.rotationAngle = rotationAngle;
	}


	//
	// RefractiveAngleFormulation methods
	//
	
	/**
	 * Refraction of a ray that passes from the inside to the outside of the surface
	 * @param alphaInside	the angle of incidence, i.e. the angle on the inside.  Note that the coordinate axes are chosen such that THE ANGLE OF INCIDENCE IS ALWAYS POSITIVE!
	 * @return	the angle of refraction, i.e. corresponding the angle on the outside
	 */
	public double alphaOutside(double alphaInside, RaySceneObjectIntersection intersection)
	throws EvanescentException, RayTraceException
	{
		return alphaInside + rotationAngle;
	}
	
	/**
	 * Refraction of a ray that passes from the outside to the inside of the surface
	 * @param alphaOutside	the angle of incidence, i.e. the angle on the outside.  Note that the coordinate axes are chosen such that THE ANGLE OF INCIDENCE IS ALWAYS POSITIVE!
	 * @return	the angle of refraction, i.e. the corresponding angle on the inside
	 */
	public double alphaInside(double alphaOutside, RaySceneObjectIntersection intersection)
	throws EvanescentException, RayTraceException
	{
		return alphaOutside + rotationAngle;
	}	
}
