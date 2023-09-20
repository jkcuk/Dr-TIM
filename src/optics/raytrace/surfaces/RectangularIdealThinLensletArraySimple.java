package optics.raytrace.surfaces;

import math.MyMath;
import math.Vector3D;
import optics.DoubleColour;
import optics.raytrace.core.LightSource;
import optics.raytrace.core.Ray;
import optics.raytrace.core.RaySceneObjectIntersection;
import optics.raytrace.core.RaytraceExceptionHandler;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.SurfacePropertyPrimitive;
import optics.raytrace.exceptions.RayTraceException;
import optics.raytrace.surfaces.diffraction.SingleSlitDiffraction;

/**
 * A rectangular array of lenslets of focal length f.
 * 
 * This class does not require the associated scene object to be parametrised.
 * 
 * @author johannes
 */
public class RectangularIdealThinLensletArraySimple extends SurfacePropertyPrimitive
{
	private static final long serialVersionUID = 2206232408794176834L;

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

	public RectangularIdealThinLensletArraySimple(
			Vector3D centre,
			Vector3D uHat,
			Vector3D vHat,
			double focalLength,
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
		super(throughputCoefficient, shadowThrowing);
		this.centre = centre;
		this.uHat = uHat;
		this.vHat = vHat;
		this.focalLength = focalLength;
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
	public RectangularIdealThinLensletArraySimple(RectangularIdealThinLensletArraySimple original)
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
				original.isSimulateDiffractiveBlur(),
				original.getLambda(),
				original.getTransmissionCoefficient(),
				original.isShadowThrowing()
			);
	}
	
	@Override
	public RectangularIdealThinLensletArraySimple clone()
	{
		return new RectangularIdealThinLensletArraySimple(this);
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
	
	public double lensHeight(double x, double y)
	{
		return
				MyMath.square((x-uOffset)-uPeriod*Math.floor((x-uOffset)/uPeriod + 0.5)) + 
				MyMath.square((y-vOffset)-vPeriod*Math.floor((y-vOffset)/vPeriod + 0.5));
	}
	
	/* (non-Javadoc)
	 * @see optics.raytrace.SurfaceProperty#getColour(optics.raytrace.Ray, optics.raytrace.RaySceneObjectIntersection, optics.raytrace.SceneObject, optics.raytrace.LightSource, int)
	 */
	@Override
	public DoubleColour getColour(Ray ray, RaySceneObjectIntersection i, SceneObject scene, LightSource l, int traceLevel, RaytraceExceptionHandler raytraceExceptionHandler)
	throws RayTraceException
	{
		if (traceLevel <= 0) return DoubleColour.BLACK;
		
		Vector3D surfaceNormal = i.getNormalisedOutwardsSurfaceNormal();
		// System.out.println("RectangularIdealThinLensletArraySimple::getColour: surfaceNormal="+surfaceNormal);
		
		// calculate the u and v coordinates of the position
		Vector3D uBasisVector = uHat.getPartPerpendicularTo(surfaceNormal).getNormalised();	// .getNormalised();
		Vector3D vBasisVector = vHat.getPartPerpendicularTo(surfaceNormal).getNormalised();	// Vector3D.crossProduct(surfaceNormal, uBasisVector).getNormalised();
		Vector3D r = Vector3D.difference(i.p, centre);
		Vector3D rUVN = r.toBasis(uBasisVector, vBasisVector, surfaceNormal);
		double u = rUVN.x;	// Vector3D.scalarProduct(r, uBasisVector);	// 
		double v = rUVN.y;	// Vector3D.scalarProduct(r, vBasisVector);	// 
		Vector3D lensletCentreUVN = new Vector3D(
				findLensletCentreCoordinate(u, uPeriod, uOffset),
				findLensletCentreCoordinate(v, vPeriod, vOffset),
				0
			);
		Vector3D lensletCentre = Vector3D.sum(
				centre,
				lensletCentreUVN.fromBasis(uBasisVector, vBasisVector, surfaceNormal)
			);
		
		// calculate direction of deflected ray;
		// see thinLensAlgebra.pdf
		
		// scalar product of ray direction and normalised vector in direction of optical axis is what we call dz in thinLensAlgebra.pdf;
		// need absolute value of this in case the normalised surface normal points "the other way"
		double dz = Math.abs(ray.getD().getScalarProductWith(surfaceNormal));
		
		// now calculate the point Q in the image-sided focal plane through which
		// the ray has to pass
		Vector3D Q = Vector3D.sum(
				lensletCentre,	// point where optical axis intersects surface
				ray.getD().getProductWith(getFocalLength()/dz)	// d*f/dz
			);

		// calculate normalised new light-ray direction
		Vector3D newRayDirection = Vector3D.difference(Q, i.p).getNormalised().getProductWith(Math.signum(getFocalLength()));
		
		if(simulateDiffractiveBlur)
		{
				newRayDirection = Vector3D.sum(newRayDirection, SingleSlitDiffraction.getTangentialDirectionComponentChange(
						lambda,
						uPeriod,	// pixelSideLengthU
						vPeriod,	// pixelSideLengthV
						uBasisVector,	// uHat
						vBasisVector	// vHat
					));
		}
		
		// launch a new ray from here
		return scene.getColourAvoidingOrigin(
			ray.getBranchRay(i.p, newRayDirection, i.t, ray.isReportToConsole()),
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
	}
}
