package optics.raytrace.surfaces;

import math.Vector3D;
import optics.raytrace.core.Orientation;

/**
 * A phase hologram of a Lohmann lens, which has the phase profile Phi(a, x, y) = a/3 (x^3 + y^3).
 * Two of these parts, with a parameters of the same magnitude but opposite sign, add up to a toric lens, depending on how they are shifted relative to each other.
 * The reason is that
 *   a/3 (x-Delta/2)^3 - a/3 (x+Delta/2)^3 = - a Delta x^2 - (term independent of x)
 * Comparison: lens
 *   Phi(r) = - pi r^2 / (lambda f)
 *   
 * UNDER CONSTRUCTION!
 * 
 * @author johannes
 *
 */
public class PhaseHologramOfLohmannLensPart extends PhaseHologram
{

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

	public PhaseHologramOfLohmannLensPart(double focalLength, Vector3D principalPoint, double throughputCoefficient, boolean reflective, boolean shadowThrowing) {
		super(throughputCoefficient, reflective, shadowThrowing);
		setFocalLength(focalLength);
		setPrincipalPoint(principalPoint);
	}

	public PhaseHologramOfLohmannLensPart(PhaseHologramOfLohmannLensPart original) {
		super(original);
		setFocalLength(original.getFocalLength());
		setPrincipalPoint(original.getPrincipalPoint());
	}
	
	@Override
	public PhaseHologramOfLohmannLensPart clone()
	{
		return new PhaseHologramOfLohmannLensPart(this);
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

	@Override
	public Vector3D getTangentialDirectionComponentChangeTransmissive(Vector3D surfacePosition,
			Vector3D surfaceNormal)
	{	
		// A distance r from the principal point, the lens phase is given by Phi(r, phi) = -(pi r^2)(lambda f).
		// This means that the phase gradient there is in the radial direction and of magnitude dPhi/dr = -2 r pi/(lambda f).
		// This method needs to return the phase gradient divided by 2 pi/lambda, i.e. r / f.
		
		// Vector3D.difference(surfacePosition, principalPoint).getPartPerpendicularTo(surfaceNormal) gives a vector that is tangential to the surface
		// and of length r
		return Vector3D.difference(surfacePosition, principalPoint).getPartPerpendicularTo(surfaceNormal).getProductWith(-1/focalLength);
	}

	@Override
	public Vector3D getTangentialDirectionComponentChangeReflective(Orientation incidentLightRayOrientation,
			Vector3D surfacePosition, Vector3D surfaceNormal)
	{
		// TODO Not sure this makes sense completely...
		return getTangentialDirectionComponentChangeTransmissive(surfacePosition, surfaceNormal);
	}

}
