package optics.raytrace.surfaces;

import java.util.ArrayList;

import math.*;
import optics.DoubleColour;
import optics.raytrace.core.*;
import optics.raytrace.exceptions.RayTraceException;
import optics.raytrace.utility.Coordinates;
import optics.raytrace.utility.Coordinates.GlobalOrLocalCoordinateSystemType;


/**
 * A surface property representing generalised confocal lenslet arrays [1].
 * A derivation of the law of refraction, and of the meaning of the symbols, can be found in [2].
 * The implementation is briefly discussed in [3].
 * The calculation of the best-case transmission coefficient can be found in [4].
 * Very briefly, the symbols are
 *   a -- a vector in the direction of the optical axis (pointing from lens 1 to lens 2);
 *        the length of this vector is irrelevant, but its sign isn't;
 *   u -- a vector in the direction of the cylinder axes of one cylindrical-lens pair;
 *        the length of this vector is irrelevant;
 *   deltau, deltav -- the dimensionless offset in the direction of u and v (= a x u)
 *   etau, etav -- the ratio of the focal lengths in the u and v directions (times (-1))
 * 
 * [1] A. C. Hamilton and J. Courtial, "Generalized refraction using lenslet arrays", J. Opt. A: Pure Appl. Opt. <b>11</b>, 065502 (2009)
 * [2] S. Oxburgh, C. D. White, G. Antoniou, and J. Courtial, "Law of refraction for generalised confocal lenslet arrays", Opt. Commun. <b>313</b>, 119-122 (2014)
 * [3] S. Oxburgh, Tomas Tyc, and J. Courtial, "Dr TIM: Ray-tracer TIM, with additional specialist capabilities", Comp. Phys. Commun. <b>185</b>, 1027-1037 (2014)
 * [4] T. Maceina, G. Juzeliunas, and J. Courtial, "Quantifying metarefraction with confocal lenslet arrays", Opt. Commun. <b>284</b>, 5008-5019 (2011)
 * 
 * @author Johannes Courtial
 */
/**
 * @author johannes
 *
 */
public class GeneralisedConfocalLensletArrays_old extends SurfacePropertyPrimitive
{	
	private static final long serialVersionUID = -3615618303389218702L;

	private Vector3D
		aHat,	// unit vector in the direction of the lenses' optical axes
		uHat,	// unit vector in the u direction (which is perpendicular to aHat)
		vHat;	// unit vector in the v direction (which is perpendicular to aHat and to uHat)

	private double
		deltaU, deltaV,	// o_u / f_{1,u}, o_v / f_{1,v}	[2]
		etaU, etaV;	// -f_{2,u} / f_{1,u}, -f_{2,v} / f_{1,v} [2]
	
//	private boolean
//		calculateGeometricalTransmissionCoefficient;
	
	public enum GCLAsTransmissionCoefficientMethodType
	{
		CONSTANT("Constant"),
		GEOMETRIC_BEST("Geometric (best case, centre of FOV"),
		GEOMETRIC_DETAILED("Geometric (detailed calculation)");
		
		private String description;
		private GCLAsTransmissionCoefficientMethodType(String description) {this.description = description;}	
		@Override
		public String toString() {return description;}
	}

	private GCLAsTransmissionCoefficientMethodType transmissionCoefficientMethod;
	
	/**
	 * The basis in which a and u are specified.
	 */
	private GlobalOrLocalCoordinateSystemType basis;
		
	/**
	 * Creates an instance of the surface property representing generalised confocal lenslet arrays.
	 * @param aHat
	 * @param uHat
	 * @param vHat
	 * @param etaU
	 * @param etaV
	 * @param deltaU
	 * @param deltaV
	 * @param basis
	 * @param transmissionCoefficient
	 */
	public GeneralisedConfocalLensletArrays_old(
			Vector3D aHat,
			Vector3D uHat,
			Vector3D vHat,
			double etaU,
			double etaV,
			double deltaU,
			double deltaV,
			GlobalOrLocalCoordinateSystemType basis,
			double transmissionCoefficient,
			boolean shadowThrowing
		)
	{
		super(transmissionCoefficient, shadowThrowing);
		setAUVHat(aHat, uHat, vHat);
		// setAHatAndUHat(aHat, uHat);
		setEtaU(etaU);
		setEtaV(etaV);
		setDeltaU(deltaU);
		setDeltaV(deltaV);
		setBasis(basis);
		setCalculateGeometricalTransmissionCoefficient(false);
	}
	
	/**
	 * Creates an instance of the surface property representing generalised confocal lenslet arrays.
	 * If calculateGeometricalTransmissionCoefficient to set to true, the transmission
	 * coefficient is being calculated as tU*tV, where tC = 1 if |etaC| <= 1, 1/|etaC| if |etaC| >= 1.
	 * @param aHat
	 * @param uHat
	 * @param vHat
	 * @param etaU
	 * @param etaV
	 * @param deltaU
	 * @param deltaV
	 * @param basis
	 * @param transmissionCoefficient
	 * @param calculateGeometricalTransmissionCoefficient
	 * @param shadowThrowing
	 */
	public GeneralisedConfocalLensletArrays_old(
			Vector3D aHat,
			Vector3D uHat,
			Vector3D vHat,
			double etaU,
			double etaV,
			double deltaU,
			double deltaV,
			GlobalOrLocalCoordinateSystemType basis,
			double transmissionCoefficient,
			boolean calculateGeometricalTransmissionCoefficient,
			boolean shadowThrowing
		)
	{
		super(transmissionCoefficient, shadowThrowing);
		setAUVHat(aHat, uHat, vHat);
		// setAHatAndUHat(aHat, uHat);
		setEtaU(etaU);
		setEtaV(etaV);
		setDeltaU(deltaU);
		setDeltaV(deltaV);
		setBasis(basis);
		setCalculateGeometricalTransmissionCoefficient(calculateGeometricalTransmissionCoefficient);
	}

	/**
	 * Perfect, lossy, CLAs.
	 */
	public GeneralisedConfocalLensletArrays_old(
			double eta
		)
	{
		this(	new Vector3D(0, 0, 1),	// aHat, here pointing in the direction of the 3rd direction of the surface coordinate system, i.e. the surface normal
				new Vector3D(1, 0, 0),	// uHat, here pointing in a direction tangential to the surface
				new Vector3D(0, 1, 0),	// vHat
				eta,	// etaU, i.e. the value of eta that's relevant for focussing in the (u, a) projection
				eta,	// etaV, i.e. the value of eta that's relevant for focussing in the (v, a) projection
				0,	// deltaU
				0,	// deltaV
				GlobalOrLocalCoordinateSystemType.LOCAL_OBJECT_BASIS,
				1.0,	// non-geometrical transmission coefficient
				true,	// geometrical transmission coefficient
				true	// shadow-throwing
			);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public GeneralisedConfocalLensletArrays_old clone()
	{
		return new GeneralisedConfocalLensletArrays_old(
				getAHat(),
				getUHat(),
				getVHat(),
				getEtaU(),
				getEtaV(),
				getDeltaU(),
				getDeltaV(),
				getBasis(),
				getTransmissionCoefficient(),
				isCalculateGeometricalTransmissionCoefficient(),
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
		
		// calculate aHat and uHat in the global (x,y,z) coordinate basis
		Vector3D aHatXYZ, uHatXYZ, vHatXYZ, surfaceNormal;
		switch(basis)
		{
		case GLOBAL_BASIS:
		{
			aHatXYZ = aHat;
			uHatXYZ = uHat;
			vHatXYZ = vHat;
			surfaceNormal = intersection.o.getNormalisedOutwardsSurfaceNormal(intersection.p);
			break;
		}
		case LOCAL_OBJECT_BASIS:
		default:
		{
			SceneObject o = intersection.o;
			if(!(o instanceof One2OneParametrisedObject))
			{
				// some form of panic here
			}
			
			ArrayList<Vector3D> basisVectors = Coordinates.getNormalisedSurfaceBasis((One2OneParametrisedObject)o, intersection.p);
			// as the basis is normalised, a and u remain normalised
			aHatXYZ = aHat.fromBasis(basisVectors);
			uHatXYZ = uHat.fromBasis(basisVectors);
			vHatXYZ = vHat.fromBasis(basisVectors);
			surfaceNormal = basisVectors.get(2);
			break;
		}
		}
		
		// calculate a unit vector in the v direction
		// Vector3D vHatXYZ = Vector3D.crossProduct(aHatXYZ, uHatXYZ);
		
		double thisEtaU, thisEtaV, thisDeltaU, thisDeltaV;
		
		// is the ray encountering lenslet array 1 first, or lenslet array 2?
		double aHatN = Vector3D.scalarProduct(aHatXYZ, surfaceNormal);	// >0 if aHat (which is pointing from array 1 to array 2) is pointing "outwards"
		double dN = Vector3D.scalarProduct(d, surfaceNormal);	// >0 if d is pointing "outwards"
		Vector3D aHatForward;
		// if both are pointing the same way (i.e. either "inwards" or "outwards"), then array 1 is first encountered
		if(aHatN*dN > 0)
		{
			// both are pointing the same way, so array 1 is first being encountered
			thisEtaU = getEtaU();
			thisEtaV = getEtaV();
			thisDeltaU = getDeltaU();
			thisDeltaV = getDeltaV();
			aHatForward = aHatXYZ;
		}
		else
		{
			// they are pointing in opposite directions, so array 2 is being encountered first
			thisEtaU = 1./getEtaU();
			thisEtaV = 1./getEtaV();
			thisDeltaU = thisEtaU*getDeltaU();
			thisDeltaV = thisEtaV*getDeltaV();
			aHatForward = aHatXYZ.getReverse();	// turn round aHat
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
		
		double transmissionCoefficient = getTransmissionCoefficient();
		if(isCalculateGeometricalTransmissionCoefficient())
		{
			// multiply by the geometrical transmission coefficient
			transmissionCoefficient = transmissionCoefficient
					* calculate2DGeometricalTransmissionCoefficient(thisEtaU)
					* calculate2DGeometricalTransmissionCoefficient(thisEtaV);
		}

		// launch a new ray from here
		
		return scene.getColourAvoidingOrigin(
			ray.getBranchRay(intersection.p, newRayDirection, intersection.t),	// creating the new ray using the original ray's getSecondaryRay method ensures the ray trajectory is recorded correctly
			intersection.o,	// the primitive scene object being intersected
			lights,	// the light source(s)
			scene,	// the entire scene
			traceLevel-1,	// launch the new ray with a trace level reduced by 1
			raytraceExceptionHandler
		).multiply(transmissionCoefficient);
	}
	
	/**
	 * Calculate the best-case geometrical transmission coefficient, i.e. the maximum of the curves in [1]
	 * [1] T. Maceina, G. Juzeliunas, and J. Courtial, "Quantifying metarefraction with confocal lenslet arrays", Opt. Commun. <b>284</b>, 5008-5019 (2011)
	 * @param eta
	 * @return 1 if |eta| <= 1, 1/|eta| otherwise
	 */
	private double calculate2DGeometricalTransmissionCoefficient(double eta)
	{
		return (Math.abs(eta) >= 1)?1:Math.abs(eta);
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
	
	public void setAUVHat(Vector3D a, Vector3D u, Vector3D v)
	{
		// normalise the vector a
		this.aHat = a.getNormalised();
		
		// take the part of u that is perpendicular to aHat, and normalise it
		this.uHat = u.getPartPerpendicularTo(aHat).getNormalised();
		
		// take the part of v that is in the direction of aHat x uHat, and therefore perpendicular to aHat and uHat,
		// and normalise it
		this.vHat = v.getPartParallelTo(Vector3D.crossProduct(aHat, uHat)).getNormalised();
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

	public GCLAsTransmissionCoefficientMethodType getTransmissionCoefficientMethod() {
		return transmissionCoefficientMethod;
	}

	public void setTransmissionCoefficientMethod(GCLAsTransmissionCoefficientMethodType transmissionCoefficientMethod) {
		this.transmissionCoefficientMethod = transmissionCoefficientMethod;
	}

//	public boolean isCalculateGeometricalTransmissionCoefficient() {
//		return calculateGeometricalTransmissionCoefficient;
//	}
//
//	public void setCalculateGeometricalTransmissionCoefficient(boolean calculateGeometricalTransmissionCoefficient) {
//		this.calculateGeometricalTransmissionCoefficient = calculateGeometricalTransmissionCoefficient;
//	}
}
