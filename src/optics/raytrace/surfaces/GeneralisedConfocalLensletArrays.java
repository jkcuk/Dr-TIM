package optics.raytrace.surfaces;

import java.util.ArrayList;

import math.*;
import optics.DoubleColour;
import optics.raytrace.core.*;
import optics.raytrace.exceptions.RayTraceException;
import optics.raytrace.surfaces.transmissionCoefficient.TransmissionCoefficientCalculator;
import optics.raytrace.utility.CoordinateSystems;
import optics.raytrace.utility.CoordinateSystems.GlobalOrLocalCoordinateSystemType;


/**
 * A surface property representing generalised confocal lenslet arrays [1].
 * A derivation of the law of refraction, and of the meaning of the symbols, can be found in [2].
 * The implementation is briefly discussed in [3].
 * The calculation of the best-case transmission coefficient can be found in [4].
 * Very briefly, the quantities are
 *   aHat -- a unit vector in the direction of the optical axis, pointing from lens 1 to lens 2;
 *   uHat -- a unit vector in the direction of the cylinder axes of one cylindrical-lens pair;
 *   vHat -- a unit vector perpendicular to aHat and uHat;
 *   deltau, deltav -- the dimensionless offset in the direction of u and v (= a x u)
 *   etau, etav -- the ratio of the focal lengths in the u and v directions (times (-1))
 * 
 * Clearly, if the telescopes are traversed one way, e.g. lens 1 is encountered first, then lens 2, the generalised law of refraction is different from the case
 * when the telescopes are traversed the other way, in this case lens 2 first, then lens 1.
 * This class is not too concerned about the exact mechanism by which light-ray-direction change is achieved by the surface it represents; it therefore makes
 * sense that the surface represents the generalised law of refraction of telescope windows.
 *   * If light passes the surface <i>outwards</i>, the generalised law of refraction that corresponds to transmission through lens 1 first, lens 2 second, is applied.
 *   * If light passes the surface <i>inwards</i>, the generalised law of refraction that corresponds to transmission through lens 2 first, lens 1 second, is applied.
 * Note that the direction in which the lenses are traversed is therefore <b>not</b> given by the vector aHat, but by the direction of the surface's outward-facing
 * surface normal.
 * 
 * The surface property also has a TransmissionCoefficientCalculator, which can be used to simulate a particular mechanism by which the light-ray-direction change is
 * achieved.  For example, in a telescope window, light cannot traverse telescopes in the 1-to-2 direction if the projection of the incident light-ray direction
 * onto the vector <aHat> points in the 2-to-1 direction. 
 * 
 * [1] A. C. Hamilton and J. Courtial, "Generalized refraction using lenslet arrays", J. Opt. A: Pure Appl. Opt. <b>11</b>, 065502 (2009)
 * [2] S. Oxburgh, C. D. White, G. Antoniou, and J. Courtial, "Law of refraction for generalised confocal lenslet arrays", Opt. Commun. <b>313</b>, 119-122 (2014)
 * [3] S. Oxburgh, Tomas Tyc, and J. Courtial, "Dr TIM: Ray-tracer TIM, with additional specialist capabilities", Comp. Phys. Commun. <b>185</b>, 1027-1037 (2014)
 * [4] T. Maceina, G. Juzeliunas, and J. Courtial, "Quantifying metarefraction with confocal lenslet arrays", Opt. Commun. <b>284</b>, 5008-5019 (2011)
 * [5] E. N. Cowie and J. Courtial, "Engineering the field of view of generalised confocal lenslet arrays", in preparation (2017)
 * 
 * @author Johannes Courtial
 */
public class GeneralisedConfocalLensletArrays extends SurfacePropertyPrimitive
{	
	private static final long serialVersionUID = -3615618303389218702L;
	
	// private static final Random random = new Random();	// use Math.random() instead

	/**
	 * unit vector in the direction of the optical axis of the two lenslets in each telescopelet, pointing from the plane of the first lenslets to that of the second
	 */
	Vector3D aHat;
	
	/**
	 * unit vector in the first transverse direction; note that the sides of the parallelogram-shaped lenslet apertures are aligned with the <u> and <v> directions
	 */
	Vector3D uHat;
	
	/**
	 * unit vector in the second transverse direction; note that the sides of the parallelogram-shaped lenslet apertures are aligned with the <u> and <v> directions
	 */
	Vector3D vHat;

	/**
	 * -f_2u/f_1u, i.e. negative ratio of focal lengths of 2nd and first lenses in the (a,u) projection [1,5]
	 */
	private double etaU;

	/**
	 * -f_2v/f_1v, i.e. negative ratio of focal lengths of 2nd and first lenses in the (a,v) projection [1,5]
	 */
	private double etaV;

	/**
	 * deltaU = dU / f1, i.e. the offset in the <u> direction of the optical axis of the 2nd lenslet from that of the first, dU, divided by f1
	 * (see Eqn (4) in [1])
	 */
	private double deltaU;
	
	/**
	 * deltaV = dV / f1, i.e. the offset in the <v> direction of the optical axis of the 2nd lenslet from that of the first, dV, divided by f1
	 * (see Eqn (4) in [1])
	 */
	private double deltaV;

	/**
	 * The basis in which a and u are specified.
	 */
	private GlobalOrLocalCoordinateSystemType basis;
		
//	public enum GCLAsTransmissionCoefficientCalculationMethodType
//	{
//		/**
//		 * detailed calculation of the average transmission coefficient;
//		 * depends on the light-ray direction; does not take into account diffraction
//		 */
//		GEOMETRIC("Geometric"),
//		/**
//		 * take transmission coefficient to be that at the centre of the FOV
//		 */
//		GEOMETRIC_BEST("Geometric, best case (centre of FOV)"),
//		/**
//		 * set the transmission coefficient to a constant
//		 */
//		CONSTANT("Constant");
//		
//		private String description;
//		private GCLAsTransmissionCoefficientCalculationMethodType(String description) {this.description = description;}	
//		@Override
//		public String toString() {return description;}
//	}

	/**
	 * determines which method is used to calculate the transmission coefficient;
	 * NULL if the surface-property primitive's constant transmission coefficient is to be used
	 */
	private TransmissionCoefficientCalculator transmissionCoefficientCalculator;

	/**
	 * simulate imperfections due to pixellation;
	 * NULL if the surface is perfect
	 */
	private Pixellation pixellation;
	
	
	// constructors
	
	/**
	 * Creates an instance of the surface property representing generalised confocal lenslet arrays.
	 * If calculateGeometricalTransmissionCoefficient to set to true, the transmission
	 * coefficient is being calculated as tU*tV, where tC = 1 if |etaC| <= 1, 1/|etaC| if |etaC| >= 1.
	 * @param a
	 * @param u
	 * @param v
	 * @param etaU
	 * @param etaV
	 * @param deltaU
	 * @param deltaV
	 * @param basis
	 * @param transmissionCoefficientMethod
	 * @param shadowThrowing
	 * @param pixelSideLength
	 * @param lambda
	 * @param simulateDiffractiveBlur
	 * @param simulateRayOffset
	 */
	public GeneralisedConfocalLensletArrays(
			Vector3D a,
			Vector3D u,
			Vector3D v,
			double etaU,
			double etaV,
			double deltaU,
			double deltaV,
			GlobalOrLocalCoordinateSystemType basis,
			TransmissionCoefficientCalculator transmissionCoefficientCalculator,
			Pixellation pixellation,
			boolean shadowThrowing
		)
	{
		super(1.0, shadowThrowing);
		setAUVHat(a, u, v);
		setEtaU(etaU);
		setEtaV(etaV);
		setDeltaU(deltaU);
		setDeltaV(deltaV);
		setBasis(basis);
		setTransmissionCoefficientCalculator(transmissionCoefficientCalculator);
		setPixellation(pixellation);
	}


	/**
	 * Creates an instance of the surface property representing idealised generalised confocal lenslet arrays with a constant transmission coefficient.
	 * @param a
	 * @param u
	 * @param v
	 * @param etaU
	 * @param etaV
	 * @param deltaU
	 * @param deltaV
	 * @param basis
	 * @param transmissionCoefficient
	 * @param shadowThrowing
	 */
	public GeneralisedConfocalLensletArrays(
			Vector3D a,
			Vector3D u,
			Vector3D v,
			double etaU,
			double etaV,
			double deltaU,
			double deltaV,
			GlobalOrLocalCoordinateSystemType basis,
			double transmissionCoefficient,
			boolean shadowThrowing
		)
	{
		this(
				a,
				u,
				v,
				etaU,
				etaV,
				deltaU,
				deltaV,
				basis,
				null,	// transmissionCoefficientCalculator; null = use scene-object primitive's transmission coefficient
				null,	// pixellation; null = no pixellation effects
				shadowThrowing
			);
		
		// set scene-object primitive's transmission coefficient
		setTransmissionCoefficient(transmissionCoefficient);
	}
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public GeneralisedConfocalLensletArrays clone()
	{
		return new GeneralisedConfocalLensletArrays(
				getAHat(),
				getUHat(),
				getVHat(),
				getEtaU(),
				getEtaV(),
				getDeltaU(),
				getDeltaV(),
				getBasis(),
				getTransmissionCoefficientCalculator(),
				getPixellation(),
				isShadowThrowing()
			);
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.SurfaceProperty#getColour(optics.raytrace.Ray, optics.raytrace.RaySceneObjectIntersection, optics.raytrace.SceneObject, optics.raytrace.LightSource, int)
	 */
	@Override
	public DoubleColour getColour(Ray ray, RaySceneObjectIntersection intersection, SceneObject scene, LightSource lights, int traceLevel, RaytraceExceptionHandler raytraceExceptionHandler)
	throws RayTraceException
	{
		// check the trace level is positive; if not, return black
		if (traceLevel <= 0) return DoubleColour.BLACK;
		
		// non-normalised ray direction
		Vector3D d = ray.getD();	// .getNormalised();
		
		Orientation orientation = Orientation.getRayOrientation(ray, intersection);
		
		// calculate aHat and uHat in the global (x,y,z) coordinate basis
		Vector3D aHatXYZ, uHatXYZ, vHatXYZ; // , surfaceNormal;
		switch(basis)
		{
		case GLOBAL_BASIS:
			aHatXYZ = aHat;
			uHatXYZ = uHat;
			vHatXYZ = vHat;
			// surfaceNormal = intersection.o.getNormalisedOutwardsSurfaceNormal(intersection.p);
			break;
		case LOCAL_OBJECT_BASIS:
		default:
			SceneObject o = intersection.o;
			if(!(o instanceof One2OneParametrisedObject))
			{
				// TODO some form of panic here
			}
			
			ArrayList<Vector3D> basisVectors = CoordinateSystems.getNormalisedSurfaceBasis((One2OneParametrisedObject)o, intersection.p);
			// as the basis is normalised, a and u remain normalised
			aHatXYZ = aHat.fromBasis(basisVectors);
			uHatXYZ = uHat.fromBasis(basisVectors);
			vHatXYZ = vHat.fromBasis(basisVectors);
			// surfaceNormal = basisVectors.get(2);
			break;
		}
		
		// calculate a unit vector in the v direction
		// Vector3D vHatXYZ = Vector3D.crossProduct(aHatXYZ, uHatXYZ);
		
		double thisEtaU, thisEtaV, thisDeltaU, thisDeltaV;
		
//		// is the ray encountering lenslet array 1 first, or lenslet array 2?
//		double aHatN = Vector3D.scalarProduct(aHatXYZ, surfaceNormal);	// >0 if aHat (which is pointing from array 1 to array 2) is pointing "outwards"
//		double dN = Vector3D.scalarProduct(d, surfaceNormal);	// >0 if d is pointing "outwards"
		Vector3D aHatForward;
		
//		// true if array 1 is encountered first;
//		// if aHatN and dN are pointing the same way (i.e. either "inwards" or "outwards"), then array 1 is first encountered
//		boolean array1First = aHatN*dN > 0;
		
//		if(array1First)
		switch(orientation)
		{
		case INWARDS:
			// they are pointing in opposite directions, so array 2 is being encountered first
			thisEtaU = 1./getEtaU();
			thisEtaV = 1./getEtaV();
			thisDeltaU = getDeltaU() / getEtaU();
			thisDeltaV = getDeltaV() / getEtaV();
			aHatForward = aHatXYZ.getReverse();	// turn round aHat
			break;
		case OUTWARDS:
		default:
			// both are pointing the same way, so array 1 is first being encountered
			thisEtaU = getEtaU();
			thisEtaV = getEtaV();
			thisDeltaU = getDeltaU();
			thisDeltaV = getDeltaV();
			aHatForward = aHatXYZ;
		}
				
		// calculate aHat, which is aXYZ normalised and directed such that d.aHat > 0
		// Vector3D aHat = aHatXYZ.getNormalised().getProductWith(Math.signum(Vector3D.scalarProduct(d, aHatXYZ)));
		
		// calculate the components of d in the u, v and a directions
		double du = Vector3D.scalarProduct(d, uHatXYZ);
		double dv = Vector3D.scalarProduct(d, vHatXYZ);
		double da = Vector3D.scalarProduct(d, aHatForward);
		
		Vector3D newRayDirection =
			Vector3D.sum(
					uHatXYZ.getProductWith((du/da - thisDeltaU) / thisEtaU),
					vHatXYZ.getProductWith((dv/da - thisDeltaV) / thisEtaV),
					aHatForward
				);
		
		Vector3D newRayStartPosition = intersection.p;
	
		// TODO take into account effects of pixellation
		
		double transmissionCoefficient;

		if(transmissionCoefficientCalculator != null)
		{
			transmissionCoefficient = transmissionCoefficientCalculator.calculateTransmissionCoefficient(d, intersection, orientation);
		}
		else
		{
			// if no transmissionCoefficientCalculator is set, use this surface-property primitive's transmission coefficient 
			transmissionCoefficient = getTransmissionCoefficient();
		}
		
		return scene.getColourAvoidingOrigin(
			// launch a new ray from here
			ray.getBranchRay(newRayStartPosition, newRayDirection, intersection.t),	// creating the new ray using the original ray's getSecondaryRay method ensures the ray trajectory is recorded correctly
			intersection.o,	// the primitive scene object being intersected
			lights,	// the light source(s)
			scene,	// the entire scene
			traceLevel-1,	// launch the new ray with a trace level reduced by 1
			raytraceExceptionHandler
		// multiply by the geometrical transmission coefficient
		).multiply(transmissionCoefficient);
	}
	

	
	

	public Vector3D getAHat() {
		return aHat;
	}

	public Vector3D getUHat() {
		return uHat;
	}
	
	public Vector3D getVHat()
	{
		return vHat;
	}
	
	/**
	 * Set the unit vectors <aHat>, <uHat> and <vHat>.
	 * Sets the class variables to the supplied variables after suitable adjustments, as follows.
	 * <aHat> is set to <a> after normalisation.
	 * <uHat> is set to the part of <u> that is normal to <aHat>, normalised.
	 * <vHat> is set to the part of <v> that is normal to both <aHat> and <uHat>, normalised.
	 * @param a
	 * @param u
	 * @param v
	 */
	public void setAUVHat(Vector3D a, Vector3D u, Vector3D v)
	{
		// normalise the vector <a>
		this.aHat = a.getNormalised();
		
		// take the part of <u> that is perpendicular to <aHat> and normalise it
		this.uHat = u.getPartPerpendicularTo(aHat).getNormalised();
		
		// take the part of <v> that is perpendicular to <aHat> and <uHat> and normalise it
		this.vHat = v.getPartPerpendicularTo(aHat, uHat).getNormalised();
	}

	/**
	 * Set aHat, uHat and vHat according to
	 * 	aHat = a, normalised (i.e. a/|a|),
	 * 	uHat = the part of u that is perpendicular to aHat, normalised,
	 * 	vHat = aHat x uHat.
	 * @param a
	 * @param u
	 */
	public void setAUVHat(Vector3D a, Vector3D u)
	{
		this.aHat = a.getNormalised();
		this.uHat = u.getPartPerpendicularTo(a).getNormalised();
		this.vHat = Vector3D.crossProduct(aHat, uHat);
	}

	public double getDeltaU() {
		return deltaU;
	}

	public void setDeltaU(double deltaU) {
		this.deltaU = deltaU;
	}

	public double getDeltaV() {
		return deltaV;
	}

	public void setDeltaV(double deltaV) {
		this.deltaV = deltaV;
	}

	public double getEtaU() {
		return etaU;
	}

	public void setEtaU(double etaU) {
		this.etaU = etaU;
	}

	public double getEtaV() {
		return etaV;
	}

	public void setEtaV(double etaV) {
		this.etaV = etaV;
	}

	public GlobalOrLocalCoordinateSystemType getBasis() {
		return basis;
	}

	public void setBasis(GlobalOrLocalCoordinateSystemType basis) {
		this.basis = basis;
	}


	public TransmissionCoefficientCalculator getTransmissionCoefficientCalculator() {
		return transmissionCoefficientCalculator;
	}


	public void setTransmissionCoefficientCalculator(TransmissionCoefficientCalculator transmissionCoefficientCalculator) {
		this.transmissionCoefficientCalculator = transmissionCoefficientCalculator;
	}

	public Pixellation getPixellation() {
		return pixellation;
	}

	public void setPixellation(Pixellation pixellation) {
		this.pixellation = pixellation;
	}
}
