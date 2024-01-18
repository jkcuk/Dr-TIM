package optics.raytrace.surfaces;

import math.Vector3D;
import optics.raytrace.core.Orientation;

/**
 * A direct implementation of a phase hologram of a cylindrical lens.
 * The phase profile is parabolic with distance from the axis, i.e.
 *     Phi(r) = (pi r^2)(lambda f),
 * where r is the distance from the axis and f is the focal length of the lens.
 * 
 * @author johannes
 *
 */
public class PhaseHologramOfCylindricalLens extends PhaseHologram
{
	private static final long serialVersionUID = -2548292215935271975L;

	/**
	 * the lens's focal length
	 */
	private double focalLength;
	
	/**
	 * the lens's principal point, i.e. centre, which should lie on the surface
	 */
	private Vector3D principalPoint;
	
	/**
	 * direction of the parabolic phase gradient
	 */
	private Vector3D phaseGradientDirection;
	
	//
	// constructors etc.
	//

	public PhaseHologramOfCylindricalLens(
			double focalLength,
			Vector3D principalPoint,
			Vector3D phaseGradientDirection,
			double throughputCoefficient,
			boolean reflective,
			boolean shadowThrowing
		)
	{
		super(throughputCoefficient, reflective, shadowThrowing);
		setFocalLength(focalLength);
		setPrincipalPoint(principalPoint);
		setPhaseGradientDirection(phaseGradientDirection);
	}

	public PhaseHologramOfCylindricalLens(PhaseHologramOfCylindricalLens original) {
		super(original);
		setFocalLength(original.getFocalLength());
		setPrincipalPoint(original.getPrincipalPoint());
		setPhaseGradientDirection(original.getPhaseGradientDirection());
	}
	
	@Override
	public PhaseHologramOfCylindricalLens clone()
	{
		return new PhaseHologramOfCylindricalLens(this);
	}


	//
	// setters & getters
	//
	
	public double getFocalLength() {
		return focalLength;
	}

	public void setFocalLength(double focalLength) {
		this.focalLength = focalLength;
	}

	public Vector3D getPrincipalPoint() {
		return principalPoint;
	}

	public void setPrincipalPoint(Vector3D principalPoint) {
		this.principalPoint = principalPoint;
	}

	public Vector3D getPhaseGradientDirection() {
		return phaseGradientDirection;
	}

	public void setPhaseGradientDirection(Vector3D phaseGradientDirection) {
		this.phaseGradientDirection = phaseGradientDirection.getNormalised();
	}

	@Override
	public Vector3D getTangentialDirectionComponentChangeTransmissive(Vector3D surfacePosition,
			Vector3D surfaceNormal)
	{	
		// A distance r from the lens axis, the lens phase is given by Phi(r) = -(pi r^2)/(lambda f).
		// This means that the phase gradient there is in the direction perpendicular to the axis and of magnitude dPhi/dr = -2 r pi/(lambda f).
		// This method needs to return the phase gradient divided by 2 pi/lambda, i.e. r / f.
		
		// Vector3D.difference(surfacePosition, principalPoint).getPartPerpendicularTo(surfaceNormal) gives a vector that is tangential to the surface
		// and of length r
		return Vector3D.difference(surfacePosition, principalPoint).getPartParallelTo(phaseGradientDirection).getProductWith(-1/focalLength);
	}

	@Override
	public Vector3D getTangentialDirectionComponentChangeReflective(Orientation incidentLightRayOrientation,
			Vector3D surfacePosition, Vector3D surfaceNormal)
	{
		// TODO Not sure this makes sense completely...
		return getTangentialDirectionComponentChangeTransmissive(surfacePosition, surfaceNormal);
	}

}
