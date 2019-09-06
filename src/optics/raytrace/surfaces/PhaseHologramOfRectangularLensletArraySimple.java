package optics.raytrace.surfaces;

import math.MyMath;
import math.Vector3D;
import optics.raytrace.core.Orientation;

/**
 * A phase hologram of a rectangular array of lenslets of focal length f.
 * 
 * This class does not require the associated scene object to be parametrised.
 * 
 * @author johannes
 */
public class PhaseHologramOfRectangularLensletArraySimple extends PhaseHologram
{
	private static final long serialVersionUID = 5709331854362381231L;

	private Vector3D centre;
	
	/**
	 * the direction of the <i>u</i> direction
	 */
	private Vector3D uHat;

	/**
	 * the direction of the <i>v</i> direction
	 */
	private Vector3D vHat;

	/**
	 * focal length of each lenslet; the phase cross-section of the lens is Phi(r) = (pi r^2)(lambda f), where r is the distance from the lens centre
	 */
	private double focalLength;
	
	/**
	 * period in u direction
	 */
	private double uPeriod;

	/**
	 * period in v direction
	 */
	private double vPeriod;

	/**
	 * offset in u direction
	 */
	private double uOffset;

	/**
	 * offset in v direction
	 */
	private double vOffset;
	
	//
	// constructors etc.
	//

	/**
	 * @param centre
	 * @param uHat
	 * @param vHat
	 * @param focalLength
	 * @param uPeriod
	 * @param vPeriod
	 * @param uOffset
	 * @param vOffset
	 * @param throughputCoefficient
	 * @param reflective
	 * @param shadowThrowing
	 */
	public PhaseHologramOfRectangularLensletArraySimple(
			Vector3D centre,
			Vector3D uHat,
			Vector3D vHat,
			double focalLength,
			double uPeriod,
			double vPeriod,
			double uOffset,
			double vOffset,
			double throughputCoefficient,
			boolean reflective,
			boolean shadowThrowing
		)
	{
		super(throughputCoefficient, reflective, shadowThrowing);
		this.centre = centre;
		this.uHat = uHat;
		this.vHat = vHat;
		this.focalLength = focalLength;
		this.uPeriod = uPeriod;
		this.vPeriod = vPeriod;
		this.uOffset = uOffset;
		this.vOffset = vOffset;
	}

	/**
	 * @param original
	 */
	public PhaseHologramOfRectangularLensletArraySimple(PhaseHologramOfRectangularLensletArraySimple original)
	{
		this(
				original.getCentre(),
				original.getuHat(),
				original.getvHat(),
				original.getFocalLength(),
				original.getuPeriod(),
				original.getvPeriod(),
				original.getuOffset(),
				original.getvOffset(),
				original.getTransmissionCoefficient(),
				original.isReflective(),
				original.isShadowThrowing()
			);
	}
	
	@Override
	public PhaseHologramOfRectangularLensletArraySimple clone()
	{
		return new PhaseHologramOfRectangularLensletArraySimple(this);
	}


	//
	// setters & getters
	//
	
	public Vector3D getCentre() {
		return centre;
	}

	public void setCentre(Vector3D centre) {
		this.centre = centre;
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
		this.vHat = vHat;
	}

	public double getFocalLength() {
		return focalLength;
	}

	public void setFocalLength(double focalLength) {
		this.focalLength = focalLength;
	}

	public double getuPeriod() {
		return uPeriod;
	}

	public void setuPeriod(double uPeriod) {
		this.uPeriod = uPeriod;
	}

	public double getvPeriod() {
		return vPeriod;
	}

	public void setvPeriod(double vPeriod) {
		this.vPeriod = vPeriod;
	}

	public double getuOffset() {
		return uOffset;
	}

	public void setuOffset(double uOffset) {
		this.uOffset = uOffset;
	}

	public double getvOffset() {
		return vOffset;
	}

	public void setvOffset(double vOffset) {
		this.vOffset = vOffset;
	}

	
	//
	// methods that do stuff
	//
	
	private double findLensletCentreCoordinate(double u, double uPeriod, double uOffset)
	{
		return uPeriod*Math.floor((u-uOffset)/uPeriod+0.5)+uOffset;
	}
	
	public double lensHeight(double x, double y)
	{
		return
				MyMath.square((x-uOffset)-uPeriod*Math.floor((x-uOffset)/uPeriod + 0.5)) + 
				MyMath.square((y-vOffset)-vPeriod*Math.floor((y-vOffset)/vPeriod + 0.5));
	}
	
	@Override
	public Vector3D getTangentialDirectionComponentChangeTransmissive(Vector3D surfacePosition,
			Vector3D surfaceNormal)
	{	
		// calculate the u and v coordinates of the position
		Vector3D uBasisVector = uHat.getPartPerpendicularTo(surfaceNormal);	// .getNormalised();
		Vector3D vBasisVector = vHat.getPartPerpendicularTo(surfaceNormal);	// Vector3D.crossProduct(surfaceNormal, uBasisVector).getNormalised();
		Vector3D r = Vector3D.difference(surfacePosition, centre);
		Vector3D rUVN = r.toBasis(uBasisVector, vBasisVector, surfaceNormal);
		double u = rUVN.x;	// Vector3D.scalarProduct(r, uBasisVector);
		double v = rUVN.y;	// Vector3D.scalarProduct(r, vBasisVector);
		
		double xDerivative = (u-uOffset)-findLensletCentreCoordinate(u, uPeriod, uOffset);
		double yDerivative = (v-vOffset)-findLensletCentreCoordinate(v, vPeriod, vOffset);
		
		return Vector3D.sum(uBasisVector.getProductWith(-xDerivative/focalLength), vBasisVector.getProductWith(-yDerivative/focalLength));
	}

	@Override
	public Vector3D getTangentialDirectionComponentChangeReflective(Orientation incidentLightRayOrientation,
			Vector3D surfacePosition, Vector3D surfaceNormal)
	{
		return getTangentialDirectionComponentChangeTransmissive(surfacePosition, surfaceNormal);
	}

}
