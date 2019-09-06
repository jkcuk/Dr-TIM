package optics.raytrace.surfaces;

import math.Vector3D;
import optics.raytrace.core.Orientation;

/**
 * A phase hologram of a lenticular array (i.e. an array of cylindrical lenses)
 * whose focussing power varies linearly with the value of the <i>u</i> coordinate, whose origin lies on the cylinder axis of the 0th cylindrical lens.
 * As the focussing power varies with <i>u</i>, the focal length of the cylindrical lenses varies with 1/<i>u</i>.
 * 
 * This class does not require the associated scene object to be parametrised.
 * 
 * @author johannes
 */
public class PhaseHologramOfLinearPowerLenticularArray extends PhaseHologram
{
	private static final long serialVersionUID = -5540950453856153461L;

	/**
	 * a point on the cylinder axis of the 0th cylindrical lens
	 */
	private Vector3D p0;
	
	/**
	 * unit vector in the <i>u</i> direction;
	 * the cylinder axes of the cylindrical lenses are perpendicular to the <i>u</i> direction, and the array is periodic in the <i>u</i> direction
	 */
	private Vector3D uHat;
	
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

	public PhaseHologramOfLinearPowerLenticularArray(
			Vector3D p0,
			Vector3D uHat,
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
		this.dPdu = dPdu;
		this.period = period;
	}

	public PhaseHologramOfLinearPowerLenticularArray(PhaseHologramOfLinearPowerLenticularArray original)
	{
		this(
				original.getP0(),
				original.getuHat(),
				original.getdPdu(),
				original.getPeriod(),
				original.getTransmissionCoefficient(),
				original.isReflective(),
				original.isShadowThrowing()
			);
	}
	
	@Override
	public PhaseHologramOfLinearPowerLenticularArray clone()
	{
		return new PhaseHologramOfLinearPowerLenticularArray(this);
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
		Vector3D r = Vector3D.difference(surfacePosition, p0);
		double u = Vector3D.scalarProduct(r, localUHat);
		double n = calculateCylindricalLensN(u);
		double uN = calculateCylindricalLensCentreU(n);
		
		return localUHat.getProductWith(-(u-uN)*calculateFocalPower(uN));
	}

	@Override
	public Vector3D getTangentialDirectionComponentChangeReflective(Orientation incidentLightRayOrientation,
			Vector3D surfacePosition, Vector3D surfaceNormal)
	{
		return getTangentialDirectionComponentChangeTransmissive(surfacePosition, surfaceNormal);
	}

}
