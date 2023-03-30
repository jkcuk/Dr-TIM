package optics.raytrace.surfaces;

import math.Vector3D;
import optics.raytrace.core.Orientation;

/**
 * A phase hologram of one part of a Lohmann cylindrical lens.
 * A Lohmann cylindrical lens consists of two parts, each introducing a phase profile of the form
 * 
 *     phiL(a, u, v) = a/6 (u-deltaU/2)^3 (2 Pi / lambda).
 *     
 * When two of these parts are combined, one with parameter +a the other with -a, and shifted relative to each other in the u direction by deltaU, then the phase profile becomes
 * 
 *     phi = phiL(a, u - deltaU/2, v) + phiL(-a, u + deltaU/2, v)
 *         = - a pi/lambda (deltaU u^2 + deltaU^3/12).
 *         
 * The first, quadratic, term is that of a cylindrical lens; the second is independent of position and therefore does not correspond to a light-ray-direction change.
 * 
 * Comparison of the quadratic first term with the phase profile of a lens of focal length f,
 * 
 *     phiLens = - pi u^2 / (lambda f) + const.
 *     
 * reveals that the combination of the two parts of the Lohmann cylindrical lens corresponds to a cylindrical lens of focal length f, where
 * 
 *     a deltaU = 1/f = P.
 * 
 * The parameter a is therefore the factor of proportionality between the u-offset, deltaU, and the focal power of the resulting cylindrical lens, P = 1/f, so a is cylindrical focal power per offset.
 * 
 * @author johannes, Maik
 *
 */
public class PhaseHologramOfLohmannCylindricalLensPart extends PhaseHologram
{
	private static final long serialVersionUID = 8703867092027120348L;

	/**
	 * the a parameter; if two Lohmann-cylindrical-lens parts are combined, one with parameter +a, the other with -a, and offset relative to each other in the u direction by deltaU,
	 * then a is the focal power of the resulting cylindrical lens per offset between the parts, i.e. a = (1/f) / deltaU
	 */
	private double focalPowerOverDeltaU;
	
	/**
	 * A principal point, more precisely a point on the principal axis of the cylindrical lens.
	 * That principal axis points in the v direction.
	 * The "principal point" (in fact, the whole principal axis) should lie on the surface.
	 */
	private Vector3D principalPoint;
	
	/**
	 * unit vector in the u direction, i.e. the direction in the plane of the hologram in which the two parts of a Lohmann cylindrical lens have to be offset relative to each other to form a cylindrical lens.
	 * The u direction is also the direction in which the cylindrical lens focusses.
	 */
	private Vector3D uHat;
	
//	/**
//	 * We don't actually need the unit vector in the v direction, hence this is commented out.
//	 * unit vector in the v direction, i.e. the direction in the plane of the hologram perpendicular to the offset direction;
//	 * an offset between the parts in this direction doesn't do anything
//	 */
//	private Vector3D vHat;
	
	//
	// constructors etc.
	//

	public PhaseHologramOfLohmannCylindricalLensPart(
			double focalPowerOverDeltaU,
			Vector3D principalPoint,
			Vector3D uHat,
//			Vector3D vHat,
			double throughputCoefficient,
			boolean shadowThrowing
		)
	{
		super(
				throughputCoefficient,
				false,	// reflective
				shadowThrowing
			);
		setFocalPowerOverDeltaU(focalPowerOverDeltaU);
		setPrincipalPoint(principalPoint);
		setuHat(uHat);
//		setvHat(vHat);
	}

	public PhaseHologramOfLohmannCylindricalLensPart(PhaseHologramOfLohmannCylindricalLensPart original) {
		super(original);
		setFocalPowerOverDeltaU(original.getFocalPowerOverDeltaU());
		setPrincipalPoint(original.getPrincipalPoint());
		setuHat(original.getuHat());
//		setvHat(original.getvHat());
	}
	
	@Override
	public PhaseHologramOfLohmannCylindricalLensPart clone()
	{
		return new PhaseHologramOfLohmannCylindricalLensPart(this);
	}


	//
	// setters & getters
	//
	
	public double getFocalPowerOverDeltaU() {
		return focalPowerOverDeltaU;
	}

	public void setFocalPowerOverDeltaU(double focalPowerOverDeltaU) {
		this.focalPowerOverDeltaU = focalPowerOverDeltaU;
	}

	public Vector3D getPrincipalPoint() {
		return principalPoint;
	}

	public void setPrincipalPoint(Vector3D principalPoint) {
		this.principalPoint = principalPoint;
	}

	public Vector3D getuHat() {
		return uHat;
	}

	/**
	 * Set uHat to the argument, normalised
	 * @param uHat
	 */
	public void setuHat(Vector3D uHat) {
		this.uHat = uHat.getNormalised();
	}

//	public Vector3D getvHat() {
//		return vHat;
//	}
//
//	/**
//	 * Set vHat to the argument, normalised
//	 * @param vHat
//	 */
//	public void setvHat(Vector3D vHat) {
//		this.vHat = vHat.getNormalised();
//	}
	
	
	//
	// PhaseHologram methods
	//

	@Override
	public Vector3D getTangentialDirectionComponentChangeTransmissive(
			Vector3D surfacePosition,
			Vector3D surfaceNormal
		)
	{	
		// This method needs to return the part of the phase gradient that is tangential to the hologram plane, divided by 2 Pi/lambda.
		// The expression for the phase is
		// 
		//     phiL(a, u, v) = a u^3/6 (2 Pi / lambda),
		//
		// so the two components of the tangential phase gradient, divided by (2 Pi / lambda), are
		// 
		//     (d/du phiL) / (2 Pi / lambda) = a u^2/2
		//     (d/dv phiL) / (2 Pi / lambda) = 0
		//
		// (Here a is called focalPowerOverDeltaX.)
		
		Vector3D rVec = Vector3D.difference(surfacePosition, principalPoint);
		double u = Vector3D.scalarProduct(rVec, uHat);
		
		return uHat.getProductWith(0.5*focalPowerOverDeltaU*u*u);
	}

	@Override
	public Vector3D getTangentialDirectionComponentChangeReflective(Orientation incidentLightRayOrientation,
			Vector3D surfacePosition, Vector3D surfaceNormal)
	{
		// TODO Not sure this makes sense completely...
		return getTangentialDirectionComponentChangeTransmissive(surfacePosition, surfaceNormal);
	}

}
