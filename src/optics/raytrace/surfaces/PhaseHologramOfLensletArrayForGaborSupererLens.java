package optics.raytrace.surfaces;

import math.Vector3D;
import optics.raytrace.core.Orientation;
import optics.raytrace.utility.SingleSlitDiffraction;

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
	 * The centre of the clear-aperture array.  The clear aperture of the lenslet with indices (0, 0) is centred here.
	 */
	private Vector3D centreClearApertureArray;

	/**
	 * The centre of the principal-point array.  The principal point of the lenslet with indices (0, 0) is centred here.
	 */
	private Vector3D centrePrincipalPointArray;
	
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
	 * period in u direction of the array of clear apertures
	 */
	private double uPeriodApertures;

	/**
	 * period in v direction of the array of clear apertures
	 */
	private double vPeriodApertures;
	
	/**
	 * period in the u direction of the rectangular array of principal points
	 */
	private double uPeriodPrincipalPoints;

	/**
	 * period in the v direction of the rectangular array of principal points
	 */
	private double vPeriodPrincipalPoints;
	
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
			Vector3D centreClearApertureArray,
			Vector3D centrePrincipalPointArray,
			Vector3D uHat,
			Vector3D vHat,
			double focalLength,
			double uPeriodApertures,
			double vPeriodApertures,
			double uPeriodPrincipalPoints,
			double vPeriodPrincipalPoints,
			boolean simulateDiffractiveBlur,
			double lambda,
			double throughputCoefficient,
			boolean shadowThrowing
		)
	{
		super(throughputCoefficient, false, shadowThrowing);
		this.centreClearApertureArray = centreClearApertureArray;
		this.centrePrincipalPointArray = centrePrincipalPointArray;
		this.uHat = uHat;
		this.vHat = vHat;
		this.focalLength = focalLength;
		this.uPeriodApertures = uPeriodApertures;
		this.vPeriodApertures = vPeriodApertures;
		this.uPeriodPrincipalPoints = uPeriodPrincipalPoints;
		this.vPeriodPrincipalPoints = vPeriodPrincipalPoints;
		this.simulateDiffractiveBlur = simulateDiffractiveBlur;
		this.lambda = lambda;
	}

	/**
	 * @param original
	 */
	public PhaseHologramOfLensletArrayForGaborSupererLens(PhaseHologramOfLensletArrayForGaborSupererLens original)
	{
		this(
				original.getCentreClearApertureArray(),
				original.getCentrePrincipalPointArray(),
				original.getuHat(),
				original.getvHat(),
				original.getFocalLength(),
				original.getuPeriodApertures(),
				original.getvPeriodApertures(),
				original.getuPeriodPrincipalPoints(),
				original.getvPeriodPrincipalPoints(),
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
	
	public Vector3D getCentreClearApertureArray() {
		return centreClearApertureArray;
	}

	public void setCentreClearApertureArray(Vector3D centreClearApertureArray) {
		this.centreClearApertureArray = centreClearApertureArray;
	}

	public Vector3D getCentrePrincipalPointArray() {
		return centrePrincipalPointArray;
	}

	public void setCentrePrincipalPointArray(Vector3D centrePrincipalPointArray) {
		this.centrePrincipalPointArray = centrePrincipalPointArray;
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

	public double getuPeriodApertures() {
		return uPeriodApertures;
	}

	public void setuPeriodApertures(double uPeriodApertures) {
		this.uPeriodApertures = uPeriodApertures;
	}

	public double getvPeriodApertures() {
		return vPeriodApertures;
	}

	public void setvPeriodApertures(double vPeriodApertures) {
		this.vPeriodApertures = vPeriodApertures;
	}

	public double getuPeriodPrincipalPoints() {
		return uPeriodPrincipalPoints;
	}

	public void setuPeriodPrincipalPoints(double uPeriodPrincipalPoints) {
		this.uPeriodPrincipalPoints = uPeriodPrincipalPoints;
	}

	public double getvPeriodPrincipalPoints() {
		return vPeriodPrincipalPoints;
	}

	public void setvPeriodPrincipalPoints(double vPeriodPrincipalPoints) {
		this.vPeriodPrincipalPoints = vPeriodPrincipalPoints;
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
		System.out.println("Bla");
		
		// calculate the u and v coordinates of the position
		Vector3D uBasisVector = uHat.getPartPerpendicularTo(surfaceNormal).getNormalised();
		Vector3D vBasisVector = vHat.getPartPerpendicularTo(surfaceNormal).getNormalised();	// Vector3D.crossProduct(surfaceNormal, uBasisVector).getNormalised();
		Vector3D r = Vector3D.difference(surfacePosition, centreClearApertureArray);
		Vector3D rUVN = r.toBasis(uBasisVector, vBasisVector, surfaceNormal);
		double u = rUVN.x;	// Vector3D.scalarProduct(r, uBasisVector);
		double v = rUVN.y;	// Vector3D.scalarProduct(r, vBasisVector);
		
		double uDerivative = (Vector3D.scalarProduct(centreClearApertureArray, uBasisVector) + u)-(Vector3D.scalarProduct(centrePrincipalPointArray, uBasisVector) + findLensletIndex(u, uPeriodApertures)*uPeriodPrincipalPoints);
		double vDerivative = (Vector3D.scalarProduct(centreClearApertureArray, vBasisVector) + v)-(Vector3D.scalarProduct(centrePrincipalPointArray, vBasisVector) + findLensletIndex(v, vPeriodApertures)*vPeriodPrincipalPoints);
		
		Vector3D rayDirectionChange = Vector3D.sum(uBasisVector.getProductWith(-uDerivative/focalLength), vBasisVector.getProductWith(-vDerivative/focalLength));
		
		if(simulateDiffractiveBlur)
		{
				return Vector3D.sum(rayDirectionChange, SingleSlitDiffraction.getTangentialDirectionComponentChange(
						lambda,
						uPeriodApertures,	// pixelSideLengthU
						vPeriodApertures,	// pixelSideLengthV
						uBasisVector,	// uHat
						vBasisVector	// vHat
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
