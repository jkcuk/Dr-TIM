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

	/**
	 * @param focalLength
	 * @param aperturesLatticeVector1
	 * @param aperturesLatticeVector2
	 * @param principalPointsLatticeVector1
	 * @param principalPointsLatticeVector2
	 * @param aperture00Centre
	 * @param principalPoint00
	 * @param simulateDiffractiveBlur
	 * @param lambda
	 * @param throughputCoefficient
	 * @param shadowThrowing
	 */
	public IdealThinLensletArrayForGaborSupererLens(
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
		super(throughputCoefficient, shadowThrowing);
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
	public IdealThinLensletArrayForGaborSupererLens(IdealThinLensletArrayForGaborSupererLens original)
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
	public IdealThinLensletArrayForGaborSupererLens clone()
	{
		return new IdealThinLensletArrayForGaborSupererLens(this);
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
		
		// calculate the two indices of the lenslet that is being intersected...
		Vector3D basisVector1 = aperturesLatticeVector1.getPartPerpendicularTo(surfaceNormal).getNormalised();
		Vector3D basisVector2 = aperturesLatticeVector2.getPartPerpendicularTo(surfaceNormal).getNormalised();	// Vector3D.crossProduct(surfaceNormal, uBasisVector).getNormalised();
		Vector3D r = Vector3D.difference(i.p, aperture00Centre);
		Vector3D rUVN = r.toBasis(basisVector1, basisVector2, surfaceNormal);
		double index1 = findLensletIndex(rUVN.x, aperturesLatticeVector1.getLength());
		double index2 = findLensletIndex(rUVN.y, aperturesLatticeVector2.getLength());
		
		// ... and its principal point
		Vector3D principalPoint = Vector3D.sum(
				principalPoint00,
				principalPointsLatticeVector1.getPartPerpendicularTo(surfaceNormal).getProductWith(index1),
				principalPointsLatticeVector2.getPartPerpendicularTo(surfaceNormal).getProductWith(index2)
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
				principalPoint,	// point where optical axis intersects surface
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
						aperturesLatticeVector1.getLength(),	// pixelSideLengthU
						aperturesLatticeVector2.getLength(),	// pixelSideLengthV
						aperturesLatticeVector1.getNormalised(),	// uHat
						aperturesLatticeVector2.getNormalised()	// vHat
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
