package optics.raytrace.surfaces;

import optics.raytrace.core.RaySceneObjectIntersection;
import optics.raytrace.exceptions.EvanescentException;
import optics.raytrace.exceptions.RayTraceException;


/**
 * 
 * Refraction at the surface of a Luneburg lens, formulated in terms of the angles of incidence and refraction
 * 
 * @author Johannes
 *
 */
public class LuneburgLensSurfaceAngleFormulation extends RefractiveAngleFormulation
{	
	private static final long serialVersionUID = -7369414046541004453L;

	// private double criticalAngle = 0;

	/**
	 * @param transmissionCoefficient
	 */
	public LuneburgLensSurfaceAngleFormulation(
			// double criticalAngle, 
			double transmissionCoefficient,
			boolean shadowThrowing
		)
	{
		super(transmissionCoefficient, shadowThrowing);
		// this.criticalAngle = criticalAngle;
	}
	
	public LuneburgLensSurfaceAngleFormulation(LuneburgLensSurfaceAngleFormulation old)
	{
		super(old);
		// this.criticalAngle = old.getCriticalAngle();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public LuneburgLensSurfaceAngleFormulation clone()
	{
		return new LuneburgLensSurfaceAngleFormulation(this);
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
		// if(alphaInside < getCriticalAngle()/2) return alphaInside;
		return alphaInside*2;
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
		// if(alphaOutside < getCriticalAngle()) return alphaOutside;
		return alphaOutside/2;
	}

//	public double getCriticalAngle() {
//		return criticalAngle;
//	}
//
//	public void setCriticalAngle(double criticalAngle) {
//		this.criticalAngle = criticalAngle;
//	}
}
