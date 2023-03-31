package optics.raytrace.surfaces;

import math.Vector3D;
import optics.raytrace.core.Orientation;

/**
 * A phase hologram of a pair of crossed lenticular arrays (i.e. an array of cylindrical lenses)
 * whose focussing power varies linearly with the value of the <i>u</i> coordinate, whose origin lies on the cylinder axis of the 0th cylindrical lens.
 * As the focussing power varies with <i>u</i>, the focal length of the cylindrical lenses varies with 1/<i>u</i>.
 * 
 * The array does automatic "pixel focussing", i.e. the focussing power of each individual pixel is that of the
 * overall (integral/Fresnel) lens.
 * 
 * This class does not require the associated scene object to be parametrised.
 * 
 * @author johannes
 */
public class PhaseHologramOfCrossedLinearPowerLenticularArrays extends PhaseHologram
{
	private static final long serialVersionUID = 2845471795692816386L;

	/**
	 * a point on the cylinder axis of the 0th cylindrical lens
	 */
	private Vector3D p0;
	
	/**
	 * unit vector in the <i>u</i> direction;
	 * the cylinder axes of the first set of cylindrical lenses are perpendicular to the <i>u</i> direction, and the array is periodic in the <i>u</i> direction
	 */
	private Vector3D uHat;

	/**
	 * unit vector in the <i>v</i> direction;
	 * the cylinder axes of the second set of cylindrical lenses are perpendicular to the <i>v</i> direction, and the array is periodic in the <i>v</i> direction
	 */
	private Vector3D vHat;

	/**
	 * the focal power of the <i>n</i>th cylindrical lens, which is centred at <u> = <i>u<sub>n</sub></i>, is <i>P<sub>n</sub></i> = <i>dPdu</i> <i>u<sub>n</sub></i>;
	 * its focal length is <i>f<sub>n</sub></i> = 1/<i>u<sub>n</sub></i>;
	 * the phase cross-section of the cylindrical lens is <i>&Phi;</i>(<i>u</i>) = (&pi; (<i>u</i>-<i>u<sub>n</sub></i>)^2)(&lambda; <i>f<sub>n</sub></i>)
	 */
	private double dPdu;
	
	/**
	 * period in <i>r</i> direction
	 */
	private double period;
	
	//
	// constructors etc.
	//

	public PhaseHologramOfCrossedLinearPowerLenticularArrays(
			Vector3D p0,
			Vector3D uHat,
			Vector3D vHat,
			double dPdu,
			double period,
			double throughputCoefficient,
			boolean reflective,
			boolean shadowThrowing
		)
	{
		super(throughputCoefficient, reflective, shadowThrowing);
		this.p0 = p0;
		this.uHat = uHat.getNormalised();
		this.vHat = vHat.getNormalised();
		this.dPdu = dPdu;
		this.period = period;
	}

	public PhaseHologramOfCrossedLinearPowerLenticularArrays(PhaseHologramOfCrossedLinearPowerLenticularArrays original)
	{
		this(
				original.getP0(),
				original.getuHat(),
				original.getvHat(),
				original.getdPdu(),
				original.getPeriod(),
				original.getTransmissionCoefficient(),
				original.isReflective(),
				original.isShadowThrowing()
			);
	}
	
	@Override
	public PhaseHologramOfCrossedLinearPowerLenticularArrays clone()
	{
		return new PhaseHologramOfCrossedLinearPowerLenticularArrays(this);
	}


	//
	// setters & getters
	//
	
	public Vector3D getP0() {
		return p0;
	}

	public void setP0(Vector3D p0) {
		this.p0 = p0;
	}

	public Vector3D getuHat() {
		return uHat;
	}

	public void setuHat(Vector3D uHat) {
		this.uHat = uHat.getNormalised();
	}

	public Vector3D getvHat() {
		return vHat;
	}

	public void setvHat(Vector3D vHat) {
		this.vHat = vHat.getNormalised();
	}

	public double getdPdu() {
		return dPdu;
	}

	public void setdPdu(double dPdu) {
		this.dPdu = dPdu;
	}

	public double getPeriod() {
		return period;
	}

	public void setPeriod(double period) {
		this.period = period;
	}


	
	//
	// methods that do stuff
	//
	
	/**
	 * @param u
	 * @return	the index <i>n</i> of the cylindrical lens at the given value of the <i>u</i> coordinate
	 */
	private double calculateCylindricalLensN(double u)
	{
		return Math.floor(u/period+0.5);
	}

	/**
	 * @param n
	 * @return	the <i>u</i> coordinate of the cylinder axis of the <i>n</i>th cylindrical lens
	 */
	private double calculateCylindricalLensCentreU(double n)
	{
		return period*n;
	}
	
	/**
	 * @param u
	 * @return	the focal power of the cylindrical lens centred at the given value of the <i>u</i> coordinate
	 */
	private double calculateFocalPower(double u)
	{
		return dPdu * u;
	}
	
	@Override
	public Vector3D getTangentialDirectionComponentChangeTransmissive(Vector3D surfacePosition,
			Vector3D surfaceNormal)
	{	
		// calculate the u coordinate of the position
		Vector3D localUHat = uHat.getPartPerpendicularTo(surfaceNormal).getNormalised();
		Vector3D localVHat = vHat.getPartPerpendicularTo(surfaceNormal).getNormalised();
		Vector3D r = Vector3D.difference(surfacePosition, p0);
		double u = Vector3D.scalarProduct(r, localUHat);
		double v = Vector3D.scalarProduct(r, localVHat);
		double nU = calculateCylindricalLensN(u);
		double nV = calculateCylindricalLensN(v);
		double uN = calculateCylindricalLensCentreU(nU);
		double vN = calculateCylindricalLensCentreU(nV);
		double uuN = u-uN;
		double vvN = v-vN;
		
		// the second term, proportional to -0.5*dPdu*uuN^2 (or vvN^2) is for one part of a Lohmann cylindrical lens --
		// see PhaseHologramOfLohmannCylindricalLensPart::getTangentialDirectionComponentChangeTransmissive
		return Vector3D.sum(
				localUHat.getProductWith(-uuN*calculateFocalPower(uN)-0.5*dPdu*uuN*uuN),
				localVHat.getProductWith(-vvN*calculateFocalPower(vN)-0.5*dPdu*vvN*vvN)
			);
	}

	@Override
	public Vector3D getTangentialDirectionComponentChangeReflective(Orientation incidentLightRayOrientation,
			Vector3D surfacePosition, Vector3D surfaceNormal)
	{
		return getTangentialDirectionComponentChangeTransmissive(surfacePosition, surfaceNormal);
	}

}
