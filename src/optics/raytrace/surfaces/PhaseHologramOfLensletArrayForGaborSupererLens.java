package optics.raytrace.surfaces;

import math.Vector3D;
import optics.raytrace.core.Orientation;
import optics.raytrace.surfaces.diffraction.SingleSlitDiffraction;

/**
 * A phase hologram of a rectangular array of lenslets of focal length f.
 * 
 * This class does not require the associated scene object to be parametrised.
 * 
 * @author johannes
 */
public class PhaseHologramOfLensletArrayForGaborSupererLens extends PhaseHologram
{
	private static final long serialVersionUID = 5709331854362381231L;

	/**
	 * focal length of each lenslet; the phase cross-section of the lens is Phi(r) = (pi r^2)(lambda f), where r is the distance from the lens centre
	 */
	private double focalLength;
	
	/**
	 * first lattice vector of the array of clear-aperture centres
	 */
	private Vector3D aperturesLatticeVector1;
	
	/**
	 * second lattice vector of the array of clear-aperture centres
	 */	
	private Vector3D aperturesLatticeVector2;

	/**
	 * first lattice vector of the array of principal points
	 */	
	private Vector3D principalPointsLatticeVector1;

	/**
	 * second lattice vector of the array of principal points
	 */	
	private Vector3D principalPointsLatticeVector2;
	
	/**
	 * centre of the aperture with indices (0, 0)
	 */
	private Vector3D aperture00Centre;
	
	/**
	 * principal point with indices (0, 0)
	 */
	private Vector3D principalPoint00;
	
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

	public PhaseHologramOfLensletArrayForGaborSupererLens(
			double focalLength,
			Vector3D aperturesLatticeVector1,
			Vector3D aperturesLatticeVector2,
			Vector3D principalPointsLatticeVector1,
			Vector3D principalPointsLatticeVector2,
			Vector3D aperture00Centre,
			Vector3D principalPoint00,
			boolean simulateDiffractiveBlur,
			double lambda,
			double throughputCoefficient,
			boolean shadowThrowing
		)
	{
		super(throughputCoefficient, false, shadowThrowing);
		this.focalLength = focalLength;
		this.aperturesLatticeVector1 = aperturesLatticeVector1;
		this.aperturesLatticeVector2 = aperturesLatticeVector2;
		this.principalPointsLatticeVector1 = principalPointsLatticeVector1;
		this.principalPointsLatticeVector2 = principalPointsLatticeVector2;
		this.aperture00Centre = aperture00Centre;
		this.principalPoint00 = principalPoint00;
		this.simulateDiffractiveBlur = simulateDiffractiveBlur;
		this.lambda = lambda;
	}

	/**
	 * @param original
	 */
	public PhaseHologramOfLensletArrayForGaborSupererLens(PhaseHologramOfLensletArrayForGaborSupererLens original)
	{
		this(
				original.getFocalLength(),
				original.getAperturesLatticeVector1(),
				original.getAperturesLatticeVector2(),
				original.getPrincipalPointsLatticeVector1(),
				original.getPrincipalPointsLatticeVector2(),
				original.getAperture00Centre(),
				original.getPrincipalPoint00(),
				original.isSimulateDiffractiveBlur(),
				original.getLambda(),
				original.getTransmissionCoefficient(),
				original.isShadowThrowing()
			);
	}
	
	@Override
	public PhaseHologramOfLensletArrayForGaborSupererLens clone()
	{
		return new PhaseHologramOfLensletArrayForGaborSupererLens(this);
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

	public Vector3D getAperturesLatticeVector1() {
		return aperturesLatticeVector1;
	}

	public void setAperturesLatticeVector1(Vector3D aperturesLatticeVector1) {
		this.aperturesLatticeVector1 = aperturesLatticeVector1;
	}

	public Vector3D getAperturesLatticeVector2() {
		return aperturesLatticeVector2;
	}

	public void setAperturesLatticeVector2(Vector3D aperturesLatticeVector2) {
		this.aperturesLatticeVector2 = aperturesLatticeVector2;
	}

	public Vector3D getPrincipalPointsLatticeVector1() {
		return principalPointsLatticeVector1;
	}

	public void setPrincipalPointsLatticeVector1(Vector3D principalPointsLatticeVector1) {
		this.principalPointsLatticeVector1 = principalPointsLatticeVector1;
	}

	public Vector3D getPrincipalPointsLatticeVector2() {
		return principalPointsLatticeVector2;
	}

	public void setPrincipalPointsLatticeVector2(Vector3D principalPointsLatticeVector2) {
		this.principalPointsLatticeVector2 = principalPointsLatticeVector2;
	}

	public Vector3D getAperture00Centre() {
		return aperture00Centre;
	}

	public void setAperture00Centre(Vector3D aperture00Centre) {
		this.aperture00Centre = aperture00Centre;
	}

	public Vector3D getPrincipalPoint00() {
		return principalPoint00;
	}

	public void setPrincipalPoint00(Vector3D principalPoint00) {
		this.principalPoint00 = principalPoint00;
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
	
	private double findLensletIndex(double w, double wPeriodApertures)
	{
		return Math.floor(w/wPeriodApertures+0.5);
	}

//	private double findLensletCentreCoordinate(double u, double uPeriod, double uOffset)
//	{
//		return uPeriod*Math.floor((u-uOffset)/uPeriod+0.5)+uOffset;
//	}
	
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
		// calculate the two indices of the lenslet that is being intersected...
		Vector3D basisVector1 = aperturesLatticeVector1.getPartPerpendicularTo(surfaceNormal).getNormalised();
		Vector3D basisVector2 = aperturesLatticeVector2.getPartPerpendicularTo(surfaceNormal).getNormalised();	// Vector3D.crossProduct(surfaceNormal, uBasisVector).getNormalised();
		Vector3D r = Vector3D.difference(surfacePosition, aperture00Centre);
		Vector3D rUVN = r.toBasis(basisVector1, basisVector2, surfaceNormal);
		double index1 = findLensletIndex(rUVN.x, aperturesLatticeVector1.getLength());
		double index2 = findLensletIndex(rUVN.y, aperturesLatticeVector2.getLength());
		
		// ... and its principal point
		Vector3D principalPoint = Vector3D.sum(
				principalPoint00,
				principalPointsLatticeVector1.getPartPerpendicularTo(surfaceNormal).getProductWith(index1),
				principalPointsLatticeVector2.getPartPerpendicularTo(surfaceNormal).getProductWith(index2)
			);
		
		Vector3D rayDirectionChange = PhaseHologramOfLens.getTangentialDirectionComponentChange(
				principalPoint,
				focalLength,
				surfacePosition,
				surfaceNormal
			);
		
		if(simulateDiffractiveBlur)
		{
				return Vector3D.sum(rayDirectionChange, SingleSlitDiffraction.getTangentialDirectionComponentChange(
						lambda,
						aperturesLatticeVector1.getLength(),	// pixelSideLengthU
						aperturesLatticeVector2.getLength(),	// pixelSideLengthV
						aperturesLatticeVector1.getNormalised(),	// uHat
						aperturesLatticeVector2.getNormalised()	// vHat
					));
		}
		
		return rayDirectionChange;
	}

	@Override
	public Vector3D getTangentialDirectionComponentChangeReflective(Orientation incidentLightRayOrientation,
			Vector3D surfacePosition, Vector3D surfaceNormal)
	{
		return getTangentialDirectionComponentChangeTransmissive(surfacePosition, surfaceNormal);
	}

}
