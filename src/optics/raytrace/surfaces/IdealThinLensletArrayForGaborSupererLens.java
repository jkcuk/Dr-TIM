package optics.raytrace.surfaces;

import math.Vector3D;
import optics.DoubleColour;
import optics.raytrace.core.LightSource;
import optics.raytrace.core.Ray;
import optics.raytrace.core.RaySceneObjectIntersection;
import optics.raytrace.core.RaytraceExceptionHandler;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.SurfacePropertyPrimitive;
import optics.raytrace.exceptions.RayTraceException;
import optics.raytrace.utility.SingleSlitDiffraction;

/**
 * A rectangular array of lenslets, all of focal length f.
 * However, compared to the RectangularIdealThinLensletArraySimple, the array is generalised in that the principal point of the lenslets
 * does not necessarily coincide with the centre of the lenslet's clear aperture.
 * Specifically, the clear apertures form a rectangular array, and so do the principal points, but the periods of these two arrays are, in general, different:
 * the periods in the u and v directions of the array of principal points is given by uPeriodPrincipalPoints and vPeriodPrincipalPoints,
 * those of the array of clear apertures are given by uPeriodApertures and vPeriodApertures.
 * 
 * Based on RectangularIdealThinLensletArraySimple.
 * This class does not require the associated scene object to be parametrised.
 * 
 * @author johannes
 */
public class IdealThinLensletArrayForGaborSupererLens extends SurfacePropertyPrimitive
{
	private static final long serialVersionUID = 9123398136080374727L;

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

	/**
	 * @param centreClearApertureArray
	 * @param centrePrincipalPointArray
	 * @param uHat
	 * @param vHat
	 * @param focalLength
	 * @param uPeriodApertures
	 * @param vPeriodApertures
	 * @param uPeriodPrincipalPoints
	 * @param vPeriodPrincipalPoints
	 * @param simulateDiffractiveBlur
	 * @param lambda
	 * @param throughputCoefficient
	 * @param shadowThrowing
	 */
	public IdealThinLensletArrayForGaborSupererLens(
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
		super(throughputCoefficient, shadowThrowing);
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
	public IdealThinLensletArrayForGaborSupererLens(IdealThinLensletArrayForGaborSupererLens original)
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
	public IdealThinLensletArrayForGaborSupererLens clone()
	{
		return new IdealThinLensletArrayForGaborSupererLens(this);
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

//	private double findLensletPrincipalPointCoordinate(double w, double wPeriodApertures, double wPeriodPrincipalPoints)
//	{
//		return wPeriodPrincipalPoints*Math.floor(w/wPeriodApertures+0.5);
//	}
		
	/* (non-Javadoc)
	 * @see optics.raytrace.SurfaceProperty#getColour(optics.raytrace.Ray, optics.raytrace.RaySceneObjectIntersection, optics.raytrace.SceneObject, optics.raytrace.LightSource, int)
	 */
	@Override
	public DoubleColour getColour(Ray ray, RaySceneObjectIntersection i, SceneObject scene, LightSource l, int traceLevel, RaytraceExceptionHandler raytraceExceptionHandler)
	throws RayTraceException
	{
		if (traceLevel <= 0) return DoubleColour.BLACK;
		
		Vector3D surfaceNormal = i.getNormalisedOutwardsSurfaceNormal();
		
		// System.out.println("IdealThinLensletArrayForGaborSupererLens::getColour: surfaceNormal="+surfaceNormal);
		
		// calculate the u and v coordinates of the position
		Vector3D uBasisVector = uHat.getPartPerpendicularTo(surfaceNormal).getNormalised();	// .getNormalised();
		Vector3D vBasisVector = vHat.getPartPerpendicularTo(surfaceNormal).getNormalised();	// Vector3D.crossProduct(surfaceNormal, uBasisVector).getNormalised();
		Vector3D r = Vector3D.difference(i.p, centreClearApertureArray);
		Vector3D rUVN = r.toBasis(uBasisVector, vBasisVector, surfaceNormal);
		double u = rUVN.x;	// Vector3D.scalarProduct(r, uBasisVector);	// 
		double v = rUVN.y;	// Vector3D.scalarProduct(r, vBasisVector);	// 
		
		// either
//		Vector3D lenslet00PrincpalPointUVN = new Vector3D(
//				findLensletPrincipalPointCoordinate(u, uPeriodApertures, uPeriodPrincipalPoints),
//				findLensletPrincipalPointCoordinate(v, vPeriodApertures, vPeriodPrincipalPoints),
//				0
//			);
//		Vector3D lensletPrincipalPoint = Vector3D.sum(
//				centrePrincipalPointArray,
//				lenslet00PrincpalPointUVN.fromBasis(uBasisVector, vBasisVector, surfaceNormal)
//			);
		// or
		Vector3D lensletPrincipalPoint = Vector3D.sum(
				centrePrincipalPointArray,
				uBasisVector.getProductWith(findLensletIndex(u, uPeriodApertures) * uPeriodPrincipalPoints),
				vBasisVector.getProductWith(findLensletIndex(v, vPeriodApertures) * vPeriodPrincipalPoints)
				);
		
		// System.out.println("IdealThinLensletArrayForGaborSupererLens::getColour: lensletCentre="+lensletCentre);
		
		// calculate direction of deflected ray;
		// see thinLensAlgebra.pdf
		
		// scalar product of ray direction and normalised vector in direction of optical axis is what we call dz in thinLensAlgebra.pdf;
		// need absolute value of this in case the normalised surface normal points "the other way"
		double dz = Math.abs(ray.getD().getScalarProductWith(surfaceNormal));
		
		// now calculate the point Q in the image-sided focal plane through which
		// the ray has to pass
		Vector3D Q = Vector3D.sum(
				lensletPrincipalPoint,	// point where optical axis intersects surface
				ray.getD().getProductWith(getFocalLength()/dz)	// d*f/dz
			);

		// System.out.println("IdealThinLensletArrayForGaborSupererLens::getColour: Q="+Q);

		// calculate normalised new light-ray direction
		Vector3D newRayDirection = Vector3D.difference(Q, i.p).getNormalised().getProductWith(Math.signum(getFocalLength()));
		
		// System.out.println("IdealThinLensletArrayForGaborSupererLens::getColour: newRayDirection="+newRayDirection);

		if(simulateDiffractiveBlur)
		{
			// System.out.println("IdealThinLensletArrayForGaborSupererLens::getColour: Warning: For some reason, simulateDiffractiveBlur = true causes this to hang for uPeriodApertures or vPeriodApertures=0.1");
			
				newRayDirection = Vector3D.sum(newRayDirection, SingleSlitDiffraction.getTangentialDirectionComponentChange(
						lambda,
						uPeriodApertures,	// pixelSideLengthU
						vPeriodApertures,	// pixelSideLengthV
						uBasisVector,	// uHat
						vBasisVector	// vHat
					));
				// System.out.println("IdealThinLensletArrayForGaborSupererLens::getColour: newRayDirection (after diffraction)="+newRayDirection);
		}

		// System.out.println("IdealThinLensletArrayForGaborSupererLens::getColour: newRayDirection (after diffraction)="+newRayDirection);
		// System.out.flush();

		// launch a new ray from here
		DoubleColour colour = scene.getColourAvoidingOrigin(
			ray.getBranchRay(i.p, newRayDirection, i.t),
			i.o,
			l,
			scene,
			traceLevel-1,
			raytraceExceptionHandler
		).multiply(
			getTransmissionCoefficient()
			// * Math.abs(newRayDirection.getScalarProductWith(n))/dz // cos(angle of new ray with normal) / cos(angle of old ray with normal)
			//
			// not sure the intensity scales --- see http://www.astronomy.net/articles/29/
			// Also, one of the article's reviewers wrote this:
			// This is also related to the brightening in Fig. 7. In fact, I think that such a brightening should not occur.
			// It is known that brightness of an object does not change if the object is observed by some non-absorbing optical
			// instrument. For example, a sun reflected in a curved metallic surface is equally bright as if it is viewed directly.
			// I expect the same for teleported image. Maybe if the effect of the additional factor in eq. (5) is taken into
			// account together with the other method of calculation of the ray direction, no brightening will occur.
		);
		
		// System.out.println("RectangularIdealThinLensletArraySimple::getColour: colour="+colour);
		
		return colour;
	}
}
