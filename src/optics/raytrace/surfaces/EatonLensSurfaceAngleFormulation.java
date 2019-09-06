package optics.raytrace.surfaces;

import optics.raytrace.core.RaySceneObjectIntersection;
import optics.raytrace.exceptions.EvanescentException;
import optics.raytrace.exceptions.RayTraceException;


/**
 * 
 * Refraction at the surface of an Eaton lens, formulated in terms of the angles of incidence and refraction
 * 
 * @author Johannes
 *
 */
public class EatonLensSurfaceAngleFormulation extends RefractiveAngleFormulation
{	
	private static final long serialVersionUID = -5695099528798814800L;
	
	// private double criticalAngle = 0;

	/**
	 * @param transmissionCoefficient
	 */
	public EatonLensSurfaceAngleFormulation(
			// double criticalAngle, 
			double transmissionCoefficient,
			boolean shadowThrowing
		)
	{
		super(transmissionCoefficient, shadowThrowing);
		// this.criticalAngle = criticalAngle;
	}
	
	public EatonLensSurfaceAngleFormulation(EatonLensSurfaceAngleFormulation old)
	{
		super(old);
		// this.criticalAngle = old.getCriticalAngle();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public EatonLensSurfaceAngleFormulation clone()
	{
		return new EatonLensSurfaceAngleFormulation(this);
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
		// if(alphaInside < getCriticalAngle()) return alphaInside;
		return alphaInside - Math.PI/2;
		// the "-" sign is there because the angles of incidence are always positive in TIM
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
		return alphaOutside - Math.PI/2;
	}

//	public double getCriticalAngle() {
//		return criticalAngle;
//	}
//
//	public void setCriticalAngle(double criticalAngle) {
//		this.criticalAngle = criticalAngle;
//	}
}
