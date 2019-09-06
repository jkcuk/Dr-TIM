package optics.raytrace.surfaces;

import optics.raytrace.core.RaySceneObjectIntersection;
import optics.raytrace.exceptions.EvanescentException;
import optics.raytrace.exceptions.RayTraceException;


/**
 * 
 * Refraction at a confocal lenslet array, formulated in terms of the angles of incidence and refraction
 * 
 * @author Johannes
 *
 */
public class ConfocalLensletArraysAngleFormulation extends RefractiveAngleFormulation
{	
	private static final long serialVersionUID = -9157101578792250919L;

	/**
	 * defined as the eta for refraction from the outside to the inside
	 */
	private double inwardsEta;
	
	/**
	 * @param transmissionCoefficient
	 */
	public ConfocalLensletArraysAngleFormulation(
			double inwardsEta, 
			double transmissionCoefficient,
			boolean shadowThrowing
		)
	{
		super(transmissionCoefficient, shadowThrowing);
		this.inwardsEta = inwardsEta;
	}
	
	public ConfocalLensletArraysAngleFormulation(ConfocalLensletArraysAngleFormulation old)
	{
		super(old);
		this.inwardsEta = old.getInwardsEta();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public ConfocalLensletArraysAngleFormulation clone()
	{
		return new ConfocalLensletArraysAngleFormulation(this);
	}
	
	/**
	 * Refraction of a ray that passes from the inside to the outside of the surface
	 * @param alphaInside	the angle of incidence, i.e. the angle on the inside.  Note that the coordinate axes are chosen such that THE ANGLE OF INCIDENCE IS ALWAYS POSITIVE!
	 * @return	the angle of refraction, i.e. corresponding the angle on the outside
	 */
	@Override
	public double alphaOutside(double alphaInside, RaySceneObjectIntersection intersection)
	throws EvanescentException, RayTraceException
	{
		return Math.atan(Math.tan(alphaInside)*getInwardsEta());
	}
	
	/**
	 * Refraction of a ray that passes from the outside to the inside of the surface
	 * @param alphaOutside	the angle of incidence, i.e. the angle on the outside.  Note that the coordinate axes are chosen such that THE ANGLE OF INCIDENCE IS ALWAYS POSITIVE!
	 * @return	the angle of refraction, i.e. the corresponding angle on the inside
	 */
	@Override
	public double alphaInside(double alphaOutside, RaySceneObjectIntersection intersection)
	throws EvanescentException, RayTraceException
	{
		return Math.atan(Math.tan(alphaOutside)/getInwardsEta());
	}

	public double getInwardsEta() {
		return inwardsEta;
	}

	public void setInwardsEta(double inwardsEta) {
		this.inwardsEta = inwardsEta;
	}
}
