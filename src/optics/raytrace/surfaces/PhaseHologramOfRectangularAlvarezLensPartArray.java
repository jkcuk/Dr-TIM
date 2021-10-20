package optics.raytrace.surfaces;

import math.Vector3D;
import optics.raytrace.core.Orientation;
import optics.raytrace.utility.SingleSlitDiffraction;

/**
 * A phase hologram of a rectangular array of Lohmann lens parts.
 * (Two Lohmann lens parts, in combination, act like a lens, and the transverse offset between the lens parts determines the focal length.)
 * 
 * This class does not require the associated scene object to be parametrised.
 * 
 * @author johannes
 */
public class PhaseHologramOfRectangularAlvarezLensPartArray extends PhaseHologram
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
	 * the a parameter of each Lohmann-lens part;
	 * if two Lohmann-lens parts are combined, one with parameter +a, the other with -a, and offset relative to each other in the u direction by deltaU,
	 * then a is the focal power of the resulting lens per offset between the parts, i.e. a = (1/f) / deltaU
	 */
	private double focalPowerOverDeltaU;
	
	/**
	 * period in u direction
	 */
	private double uPeriod;

	/**
	 * period in v direction
	 */
	private double vPeriod;

	/**
	 * offset of the part centre within the unit cell in u direction
	 */
	private double uOffset;

	/**
	 * offset of the part centre within the unit cell in v direction
	 */
	private double vOffset;
	
	/**
	 * if true, add a random angle that represents diffractive blur to the direction of the outgoing light ray
	 */
	private boolean simulateDiffractiveBlur;

	/**
	 * wavelength of light;
	 * used to calculate approximate magnitude of diffractive blur
	 */
	private double lambda;	// wavelength of light, for diffraction purposes

	//
	// constructors etc.
	//

	/**
	 * This only works if the basis vectors uHat and vHat actually span the surface, i.e. they must be perpendicular to the surface normal at every point
	 * @param centre
	 * @param uHat	
	 * @param vHat
	 * @param focalPowerOverDeltaU
	 * @param uPeriod
	 * @param vPeriod
	 * @param uOffset
	 * @param vOffset
	 * @param simulateDiffractiveBlur
	 * @param lambda
	 * @param throughputCoefficient
	 * @param reflective
	 * @param shadowThrowing
	 */
	public PhaseHologramOfRectangularAlvarezLensPartArray(
			Vector3D centre,
			Vector3D uHat,
			Vector3D vHat,
			double focalPowerOverDeltaU,
			double uPeriod,
			double vPeriod,
			double uOffset,
			double vOffset,
			boolean simulateDiffractiveBlur,
			double lambda,
			double throughputCoefficient,
			boolean shadowThrowing
		)
	{
		super(throughputCoefficient, false, shadowThrowing);
		this.centre = centre;
		this.uHat = uHat;
		this.vHat = vHat;
		this.focalPowerOverDeltaU = focalPowerOverDeltaU;
		this.uPeriod = uPeriod;
		this.vPeriod = vPeriod;
		this.uOffset = uOffset;
		this.vOffset = vOffset;
		this.simulateDiffractiveBlur = simulateDiffractiveBlur;
		this.lambda = lambda;
	}

	/**
	 * @param original
	 */
	public PhaseHologramOfRectangularAlvarezLensPartArray(PhaseHologramOfRectangularAlvarezLensPartArray original)
	{
		this(
				original.getCentre(),
				original.getuHat(),
				original.getvHat(),
				original.getFocalPowerOverDeltaU(),
				original.getuPeriod(),
				original.getvPeriod(),
				original.getuOffset(),
				original.getvOffset(),
				original.isSimulateDiffractiveBlur(),
				original.getLambda(),
				original.getTransmissionCoefficient(),
				original.isShadowThrowing()
			);
	}
	
	@Override
	public PhaseHologramOfRectangularAlvarezLensPartArray clone()
	{
		return new PhaseHologramOfRectangularAlvarezLensPartArray(this);
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
		this.vHat = vHat.getNormalised();
	}

	public double getFocalPowerOverDeltaU() {
		return focalPowerOverDeltaU;
	}

	public void setFocalPowerOverDeltaU(double focalPowerOverDeltaU) {
		this.focalPowerOverDeltaU = focalPowerOverDeltaU;
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

	public boolean isSimulateDiffractiveBlur() {
		return simulateDiffractiveBlur;
	}

	public void setSimulateDiffractiveBlur(boolean simulateDiffractiveBlur) {
		this.simulateDiffractiveBlur = simulateDiffractiveBlur;
	}

	public double getLambda() {
		return lambda;
	}

	public void setLambda(double lambda) {
		this.lambda = lambda;
	}

	
	//
	// methods that do stuff
	//
	
	private double findLensletCentreCoordinate(double u, double uPeriod, double uOffset)
	{
		return uPeriod*Math.floor((u-uOffset)/uPeriod+0.5)+uOffset;
	}
	
//	public double lensHeight(double x, double y)
//	{
//		return
//				MyMath.square((x-uOffset)-uPeriod*Math.floor((x-uOffset)/uPeriod + 0.5)) + 
//				MyMath.square((y-vOffset)-vPeriod*Math.floor((y-vOffset)/vPeriod + 0.5));
//	}
	
	@Override
	public Vector3D getTangentialDirectionComponentChangeTransmissive(Vector3D surfacePosition,
			Vector3D surfaceNormal)
	{	
		// calculate the u and v coordinates of the position
		// uHat, vHat and surfaceNormal must all be orthogonal to each other!!!
		Vector3D r = Vector3D.difference(surfacePosition, centre);
		
		// the u and v coordinates on the surface...
		double u = Vector3D.scalarProduct(r, uHat);
		double v = Vector3D.scalarProduct(r, vHat);
		
		// ... and the "local" u and v coordinates of the current Lohmann lens part, which is centred at (uL, vL) = (0, 0)
		// either shift the whole array...
//		double uL = (u-uOffset)-findLensletCentreCoordinate(u, uPeriod, uOffset);
//		double vL = (v-vOffset)-findLensletCentreCoordinate(v, vPeriod, vOffset);
		// ... or shift the lens parts within the unit cell
		double uL = u-findLensletCentreCoordinate(u, uPeriod, 0) - uOffset;
		double vL = v-findLensletCentreCoordinate(v, vPeriod, 0) - vOffset;

		// see PhaseHologramOfLohmannLensPart.getTangentialDirectionComponentChangeTransmissive
		Vector3D newRayDirection = Vector3D.sum(
				uHat.getProductWith(0.5*focalPowerOverDeltaU*(uL*uL+vL*vL)),
				vHat.getProductWith(focalPowerOverDeltaU*uL*vL)
			);
		
		if(simulateDiffractiveBlur)
		{
				return Vector3D.sum(newRayDirection, SingleSlitDiffraction.getTangentialDirectionComponentChange(
						lambda,
						uPeriod,	// pixelSideLengthU
						vPeriod,	// pixelSideLengthV
						uHat,	// uHat
						vHat	// vHat
					));
		}
		
		return newRayDirection;
	}

	@Override
	public Vector3D getTangentialDirectionComponentChangeReflective(Orientation incidentLightRayOrientation,
			Vector3D surfacePosition, Vector3D surfaceNormal)
	{
		return getTangentialDirectionComponentChangeTransmissive(surfacePosition, surfaceNormal);
	}

}
