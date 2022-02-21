package optics.raytrace.surfaces;

import math.Vector3D;
import optics.raytrace.core.Orientation;

/**
 * A direct implementation of a phase hologram of a lens, starting from the phase profile of a parabolic lens of focal length f, Phi(r, phi) = (pi r^2)(lambda f).
 * 
 * @author johannes
 *
 */
public class PhaseHologramOfLens extends PhaseHologram
{
	private static final long serialVersionUID = -3928507662425233173L;

	/**
	 * the lens's focal length
	 */
	private double focalLength;
	
	/**
	 * the lens's principal point, i.e. centre, which should lie on the surface
	 */
	private Vector3D principalPoint;
	
	//
	// constructors etc.
	//

	public PhaseHologramOfLens(
			double focalLength,
			Vector3D principalPoint,
			double throughputCoefficient,
			boolean reflective,
			boolean shadowThrowing
		)
	{
		super(throughputCoefficient, reflective, shadowThrowing);
		setFocalLength(focalLength);
		setPrincipalPoint(principalPoint);
	}

	public PhaseHologramOfLens(PhaseHologramOfLens original) {
		super(original);
		setFocalLength(original.getFocalLength());
		setPrincipalPoint(original.getPrincipalPoint());
	}
	
	@Override
	public PhaseHologramOfLens clone()
	{
		return new PhaseHologramOfLens(this);
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
	
	/**
	 * @param principalPoint
	 * @param focalLength
	 * @param surfacePosition
	 * @param surfaceNormal
	 * @return	the tangential light-ray direction change experienced by a light ray hitting an ideal lens at the given surface position
	 */
	public static Vector3D getTangentialDirectionComponentChange(
			Vector3D principalPoint,
			double focalLength,
			Vector3D surfacePosition,
			Vector3D surfaceNormal
		)
	{
		// A distance r from the principal point, the lens phase is given by Phi(r, phi) = -(pi r^2)(lambda f).
		// This means that the phase gradient there is in the radial direction and of magnitude dPhi/dr = -2 r pi/(lambda f).
		// This method needs to return the phase gradient divided by 2 pi/lambda, i.e. r / f.
		
		// Vector3D.difference(surfacePosition, principalPoint).getPartPerpendicularTo(surfaceNormal) gives a vector that is tangential to the surface
		// and of length r
		return Vector3D.difference(surfacePosition, principalPoint).getPartPerpendicularTo(surfaceNormal).getProductWith(-1./focalLength);
	}

	@Override
	public Vector3D getTangentialDirectionComponentChangeTransmissive(Vector3D surfacePosition,
			Vector3D surfaceNormal)
	{	
		return getTangentialDirectionComponentChange(principalPoint, focalLength, surfacePosition, surfaceNormal);
	}

	@Override
	public Vector3D getTangentialDirectionComponentChangeReflective(Orientation incidentLightRayOrientation,
			Vector3D surfacePosition, Vector3D surfaceNormal)
	{
		// TODO Not sure this makes sense completely...
		return getTangentialDirectionComponentChangeTransmissive(surfacePosition, surfaceNormal);
	}

}
