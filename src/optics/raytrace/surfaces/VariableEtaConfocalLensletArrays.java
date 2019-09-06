package optics.raytrace.surfaces;

import math.Vector3D;
import optics.raytrace.core.RaySceneObjectIntersection;
import optics.raytrace.exceptions.EvanescentException;
import optics.raytrace.exceptions.RayTraceException;


/**
 * 
 * Refraction at a confocal lenslet array in which the value of eta varies linearly across the aperture.
 * 
 * The value of eta is determined by the value of eta at a given point, P, and a vector that determines the position change over which eta increases by 1.
 * 
 * @author Johannes
 *
 */
public class VariableEtaConfocalLensletArrays extends RefractiveAngleFormulation
{	
	private static final long serialVersionUID = -5772881000908085944L;

	/**
	 * eta at point P, defined as the eta for refraction from the outside to the inside
	 */
	private double inwardsEtaAtP;
	
	/**
	 * the point P at which the value of eta is given
	 */
	private Vector3D pointP;
	
	/**
	 * the eta gradient
	 */
	private Vector3D gradEta;
	
	/**
	 * @param inwardsEtaAtP
	 * @param pointP
	 * @param gradEta
	 * @param transmissionCoefficient
	 * @param shadowThrowing
	 */
	public VariableEtaConfocalLensletArrays(
			double inwardsEtaAtP,
			Vector3D pointP,
			Vector3D gradEta,
			double transmissionCoefficient,
			boolean shadowThrowing
		)
	{
		super(transmissionCoefficient, shadowThrowing);
		this.inwardsEtaAtP = inwardsEtaAtP;
		this.pointP = pointP;
		this.gradEta = gradEta;
	}
	
	public VariableEtaConfocalLensletArrays(VariableEtaConfocalLensletArrays old)
	{
		super(old);
		this.inwardsEtaAtP = old.getInwardsEtaAtP();
		this.pointP = old.getPointP();
		this.gradEta = old.getGradEta();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public VariableEtaConfocalLensletArrays clone()
	{
		return new VariableEtaConfocalLensletArrays(this);
	}
	
	
	/**
	 * @param intersection
	 * @return	the value of eta (inwards) at the intersection
	 */
	public double calculateInwardsEta(RaySceneObjectIntersection intersection)
	{
		return inwardsEtaAtP + Vector3D.scalarProduct(
				Vector3D.difference(intersection.p, pointP),
				gradEta
			);	// /gradEta.getModSquared();
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
		return Math.atan(Math.tan(alphaInside)*calculateInwardsEta(intersection));
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
		return Math.atan(Math.tan(alphaOutside)/calculateInwardsEta(intersection));
	}
	
	
	
	//
	// getters & setters
	//

	public double getInwardsEtaAtP() {
		return inwardsEtaAtP;
	}

	public void setInwardsEtaAtP(double inwardsEtaAtP) {
		this.inwardsEtaAtP = inwardsEtaAtP;
	}

	public Vector3D getPointP() {
		return pointP;
	}

	public void setPointP(Vector3D pointP) {
		this.pointP = pointP;
	}

	public Vector3D getGradEta() {
		return gradEta;
	}

	public void setGradEta(Vector3D gradEta) {
		this.gradEta = gradEta;
	}
	
	
}
