package optics.raytrace.surfaces;

import java.util.ArrayList;

import math.*;
import optics.DoubleColour;
import optics.raytrace.core.*;
import optics.raytrace.exceptions.EvanescentException;
import optics.raytrace.exceptions.RayTraceException;
import optics.raytrace.utility.CoordinateSystems;
import optics.raytrace.utility.CoordinateSystems.GlobalOrLocalCoordinateSystemType;


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
 * [5] E. N. Cowie and J. Courtial, "Engineering the field of view of generalised confocal lenslet arrays", in preparation (2017)
 * 
 * @author Johannes Courtial
 */
/**
 * @author johannes
 *
 */
public class GCLAsWithApertures extends SurfacePropertyPrimitive
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
	 * offset in the <u> direction of the 1st lenslet's aperture centre from its optical axis, divided by f1
	 */
	private double alpha1U;
	
	/**
	 * offset in the <v> direction of the 1st lenslet's aperture centre from its optical axis, divided by f1
	 */
	private double alpha1V;
	
	/**
	 * offset in the <u> direction of the 2nd lenslet's aperture centre from its optical axis, divided by f1
	 */
	private double alpha2U;
	
	/**
	 * offset in the <v> direction of the 2nd lenslet's aperture centre from its optical axis, divided by f1
	 */
	private double alpha2V;

	/**
	 * aperture width in the <u> direction of the 1st lenslet, divided by f1
	 */
	private double sigma1U;

	/**
	 * aperture width in the <v> direction of the 1st lenslet, divided by f1
	 */
	private double sigma1V;

	/**
	 * aperture width in the <u> direction of the 2nd lenslet, divided by f1
	 */
	private double sigma2U;
	
	/**
	 * aperture width in the <v> direction of the 2nd lenslet, divided by f1
	 */
	private double sigma2V;
	
	
//	private boolean
//		calculateGeometricalTransmissionCoefficient;
	
	public enum GCLAsTransmissionCoefficientCalculationMethodType
	{
		/**
		 * detailed calculation of the average transmission coefficient;
		 * depends on the light-ray direction; does not take into account diffraction
		 */
		GEOMETRIC("Geometric"),
		/**
		 * take transmission coefficient to be that at the centre of the FOV
		 */
		GEOMETRIC_BEST("Geometric, best case (centre of FOV)"),
		/**
		 * set the transmission coefficient to a constant
		 */
		CONSTANT("Constant");
		
		private String description;
		private GCLAsTransmissionCoefficientCalculationMethodType(String description) {this.description = description;}	
		@Override
		public String toString() {return description;}
	}

	/**
	 * determines which method is used to calculate the transmission coefficient
	 */
	private GCLAsTransmissionCoefficientCalculationMethodType transmissionCoefficientCalculationMethod;
	
	// additional parameters
	/**
	 * side length of each "pixel", assuming square pixels whose sides are aligned with the surface-coordinate axes;
	 * used to calculate approximate magnitude of diffractive blur and position offset
	 * @see optics.raytrace.core.ParametrisedObject.getSurfaceCoordinateAxes(Vector3D)
	 */
	private double pixelSideLength;	// side length of square aperture
	// TODO this can be calculated from the sigma variables and the f1 parameters
	
	/**
	 * wavelength of light;
	 * used to calculate approximate magnitude of diffractive blur
	 */
	private double lambda;	// wavelength of light, for diffraction purposes
	
	
	/**
	 * if true, add a random angle that represents diffractive blur to the direction of the outgoing light ray
	 */
	private boolean simulateDiffractiveBlur;
	
	/**
	 * if true, add a random position offset that represents the ray-position offset during transmission
	 */
	private boolean simulateRayOffset;
	
	/**
	 * The basis in which a and u are specified.
	 */
	private GlobalOrLocalCoordinateSystemType basis;
		
	
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
	 * @param alpha1U
	 * @param alpha1V
	 * @param alpha2U
	 * @param alpha2V
	 * @param sigma1U
	 * @param sigma1V
	 * @param sigma2U
	 * @param sigma2V
	 * @param basis
	 * @param constantTransmissionCoefficient
	 * @param transmissionCoefficientMethod
	 * @param shadowThrowing
	 * @param pixelSideLength
	 * @param lambda
	 * @param simulateDiffractiveBlur
	 * @param simulateRayOffset
	 */
	public GCLAsWithApertures(
			Vector3D a,
			Vector3D u,
			Vector3D v,
			double etaU,
			double etaV,
			double deltaU,
			double deltaV,
			double alpha1U,
			double alpha1V,
			double alpha2U,
			double alpha2V,
			double sigma1U,
			double sigma1V,
			double sigma2U,
			double sigma2V,
			GlobalOrLocalCoordinateSystemType basis,
			double constantTransmissionCoefficient,
			GCLAsTransmissionCoefficientCalculationMethodType transmissionCoefficientMethod,
			boolean shadowThrowing,
			double pixelSideLength,
			double lambda,
			boolean simulateDiffractiveBlur,
			boolean simulateRayOffset
		)
	{
		super(constantTransmissionCoefficient, shadowThrowing);
		setAUVHat(a, u, v);
		setEtaU(etaU);
		setEtaV(etaV);
		setDeltaU(deltaU);
		setDeltaV(deltaV);
		setAlpha1U(alpha1U);
		setAlpha1V(alpha1V);
		setAlpha2U(alpha2U);
		setAlpha2V(alpha2V);
		setSigma1U(sigma1U);
		setSigma1V(sigma1V);
		setSigma2U(sigma2U);
		setSigma2V(sigma2V);
		setBasis(basis);
		// setCalculateGeometricalTransmissionCoefficient(calculateGeometricalTransmissionCoefficient);
		setTransmissionCoefficientMethod(transmissionCoefficientMethod);
		setPixelSideLength(pixelSideLength);
		setLambda(lambda);
		setSimulateDiffractiveBlur(simulateDiffractiveBlur);
		setSimulateRayOffset(simulateRayOffset);
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
	public GCLAsWithApertures(
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
				0,	// alpha1U
				0,	// alpha1V
				0,	// alpha2U
				0,	// alpha2V
				1,	// sigma1U
				1,	// sigma1V
				1,	// sigma2U
				1,	// sigma2V
				basis,
				transmissionCoefficient,	// constantTransmissionCoefficient
				GCLAsTransmissionCoefficientCalculationMethodType.CONSTANT,	// transmissionCoefficientMethod
				shadowThrowing,
				2e-3,	// pixelSideLength 2mm, which represents a good compromise between diffraction and pixellation
				564e-9,	// lambda; 564nm is the wavelength at which the human eye is most sensitive -- see http://hypertextbook.com/facts/2007/SusanZhao.shtml
				false,	// simulateDiffractiveBlur
				false	// simulateRayOffset
			);
	}
	
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
	 * @param constantTransmissionCoefficient
	 * @param calculateGeometricalTransmissionCoefficient
	 * @param shadowThrowing
	 */
	public GCLAsWithApertures(
			Vector3D a,
			Vector3D u,
			Vector3D v,
			double etaU,
			double etaV,
			double deltaU,
			double deltaV,
			GlobalOrLocalCoordinateSystemType basis,
			double constantTransmissionCoefficient,
			GCLAsTransmissionCoefficientCalculationMethodType transmissionCoefficientMethod,
			boolean shadowThrowing,
			double pixelSideLength,
			double lambda,
			boolean simulateDiffractiveBlur,
			boolean simulateRayOffset
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
				0,	// alpha1U
				0,	// alpha1V
				0,	// alpha2U
				0,	// alpha2V
				1,	// sigma1U
				1,	// sigma1V
				1,	// sigma2U
				1,	// sigma2V
				basis,
				constantTransmissionCoefficient,
				transmissionCoefficientMethod,
				shadowThrowing,
				pixelSideLength,
				lambda,
				simulateDiffractiveBlur,
				simulateRayOffset
			);
	}

	/**
	 * Perfect, lossy, CLAs.
	 */
	public GCLAsWithApertures(
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
				0,	// alpha1U
				0,	// alpha1V
				0,	// alpha2U
				0,	// alpha2V
				1,	// sigma1U
				1,	// sigma1V
				1,	// sigma2U
				1,	// sigma2V
				GlobalOrLocalCoordinateSystemType.LOCAL_OBJECT_BASIS,
				1.0,	// non-geometrical transmission coefficient
				// true,	// geometrical transmission coefficient
				GCLAsTransmissionCoefficientCalculationMethodType.GEOMETRIC_BEST,
				true,	// shadow-throwing
				2e-3,	// pixelSideLength 2mm, which represents a good compromise between diffraction and pixellation
				564e-9,	// lambda; 564nm is the wavelength at which the human eye is most sensitive -- see http://hypertextbook.com/facts/2007/SusanZhao.shtml
				false,	// simulateDiffractiveBlur
				false	// simulateRayOffset
			);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public GCLAsWithApertures clone()
	{
		return new GCLAsWithApertures(
				getAHat(),
				getUHat(),
				getVHat(),
				getEtaU(),
				getEtaV(),
				getDeltaU(),
				getDeltaV(),
				getAlpha1U(),
				getAlpha1V(),
				getAlpha2U(),
				getAlpha2V(),
				getSigma1U(),
				getSigma1V(),
				getSigma2U(),
				getSigma2V(),
				getBasis(),
				getTransmissionCoefficient(),
				// isCalculateGeometricalTransmissionCoefficient(),
				getTransmissionCoefficientMethod(),
				isShadowThrowing(),
				getPixelSideLength(),
				getLambda(),
				isSimulateDiffractiveBlur(),
				isSimulateRayOffset()
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
			surfaceNormal = intersection.getNormalisedOutwardsSurfaceNormal();
			break;
		}
		case LOCAL_OBJECT_BASIS:
		default:
		{
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
		
		// true if array 1 is encountered first;
		// if aHatN and dN are pointing the same way (i.e. either "inwards" or "outwards"), then array 1 is first encountered
		boolean array1First = aHatN*dN > 0;
		
		if(array1First)
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
			thisDeltaU = getDeltaU() / getEtaU();
			thisDeltaV = getDeltaV() / getEtaV();
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
		
		Vector3D newRayStartPosition = intersection.p;
		
		// simulate imperfections
		if(simulateDiffractiveBlur || simulateRayOffset)
		{
			// the intersected object has to be a ParametrisedObject, which has associated surface-coordinate axes;
			// this is necessary as we assume that the pixel sides are aligned with the surface-coordinate axes
			if(intersection.o instanceof ParametrisedObject)
			{
				// everything is okay: the intersected object is a ParametrisedObject
				Vector3D surfaceCoordinate1Axis, surfaceCoordinate2Axis;
			
				// find the surface-coordinate axes
				ArrayList<Vector3D> surfaceCoordinateAxes = ((ParametrisedObject)(intersection.o)).getSurfaceCoordinateAxes(intersection.p);
				surfaceCoordinate1Axis = surfaceCoordinateAxes.get(0).getNormalised();
				surfaceCoordinate2Axis = surfaceCoordinateAxes.get(1).getNormalised();
			
				if(simulateDiffractiveBlur)
				{
					// simulate diffractive blur
					
					// The first diffraction minimum corresponds to a direction in which light rays that
					// have passed through the slit a transverse distance w/2 apart receive phase shifts
					// that differ by pi.  Such phase shifts are precisely achieved with a phase hologram
					// that corresponds to a transverse phase gradient of pi / (w / 2) = 2 pi / w.
					// 	So the direction of the first diffraction minimum can be calculated by simulating
					// transmission through a phase hologram with this phase gradient.
					// In the PhaseHologram class, phase gradients are given in units of (2 pi/lambda),
					// and so a phase gradient of 2 pi / w becomes (2 pi / w) / (2 pi / lambda) = lambda / w.
					// Here, a uniformly distributed random phase-gradient in the range +/- lambda/w is added
					// in each transverse dimension.
					// (2.*(Math.random()-0.5) gives a uniformly distributes random number in the range -1 to 1.)
					Vector3D tangentialDirectionComponentChange = Vector3D.sum(
							surfaceCoordinate1Axis.getProductWith(lambda/pixelSideLength*2.*(Math.random()-0.5)),
							surfaceCoordinate2Axis.getProductWith(lambda/pixelSideLength*2.*(Math.random()-0.5))
						);
					try
					{
						newRayDirection = PhaseHologram.getOutgoingNormalisedRayDirection(
								newRayDirection.getNormalised(),	// incidentNormalisedRayDirection
								tangentialDirectionComponentChange,	// tangentialDirectionComponentChange
								surfaceNormal,	// normalisedOutwardsSurfaceNormal
								false	// isReflective
							);
					}
					catch(EvanescentException e)
					{
						// this is normal -- return the reflected ray
						// (Don't multiply by the transmission coefficient, as this is TIR!)
						return Reflective.getReflectedColour(ray, intersection, scene, lights, traceLevel-1, raytraceExceptionHandler);
					}
				}

				if(simulateRayOffset)
				{
					// simulate a random ray offset, assuming square pixels whose sides are aligned with the surface-coordinate axes;
					// this offset can be between -pixelSideLength and +pixelSideLength in the direction of these surface-coordinate axes
					// @see optics.raytrace.core.ParametrisedObject.getSurfaceCoordinateAxes(Vector3D)

					newRayStartPosition = Vector3D.sum(
							newRayStartPosition,
							surfaceCoordinate1Axis.getProductWith(pixelSideLength*2.*(Math.random()-0.5)),
							surfaceCoordinate2Axis.getProductWith(pixelSideLength*2.*(Math.random()-0.5))
						);
				}
			}
			else
			{
				// the intersected object is *not* a ParametrisedObject, so the pixel orientation is not defined
				System.out.println("GeneralisedConfocalLensletArrays::getColour: cannot simulate pixellation imperfections as intersected SceneObject is not a ParametrisedObject; perfect light-ray direction used");
			}
		}
		
		double transmissionCoefficient;
		switch(transmissionCoefficientCalculationMethod)
		{
		case GEOMETRIC:
			double thisAlpha1U, thisAlpha2U, thisSigma1U, thisSigma2U, thisAlpha1V, thisAlpha2V, thisSigma1V, thisSigma2V;
			
			if(array1First)
			{
				// both are pointing the same way, so array 1 is first being encountered
				thisAlpha1U = getAlpha1U();
				thisAlpha2U = getAlpha2U();
				thisAlpha1V = getAlpha1V();
				thisAlpha2V = getAlpha2V();
				thisSigma1U = getSigma1U();
				thisSigma2U = getSigma2U();
				thisSigma1V = getSigma1V();
				thisSigma2V = getSigma2V();
			}
			else
			{
				// they are pointing in opposite directions, so array 2 is being encountered first
				thisAlpha1U = -getAlpha2U() / getEtaU();
				thisAlpha2U = -getAlpha1U() / getEtaU();
				thisAlpha1V = -getAlpha2V() / getEtaV();
				thisAlpha2V = -getAlpha1V() / getEtaV();
				thisSigma1U = -getSigma2U() / getEtaU();
				thisSigma2U = -getSigma1U() / getEtaU();
				thisSigma1V = -getSigma2V() / getEtaV();
				thisSigma2V = -getSigma1V() / getEtaV();
			}

			transmissionCoefficient = 
				calculate2DGeometricalTransmissionCoefficient(thisEtaU, thisDeltaU, thisAlpha1U, thisAlpha2U, thisSigma1U, thisSigma2U, du, da)
				* calculate2DGeometricalTransmissionCoefficient(thisEtaV, thisDeltaV, thisAlpha1V, thisAlpha2V, thisSigma1V, thisSigma2V, dv, da);
			break;
		case GEOMETRIC_BEST:
			transmissionCoefficient = 
				calculate2DGeometricalTransmissionCoefficient(thisEtaU)
				* calculate2DGeometricalTransmissionCoefficient(thisEtaV);
			break;
		case CONSTANT:
		default:
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
	
	/**
	 * Calculate the best-case geometrical transmission coefficient, i.e. the maximum of the curves in [1]
	 * [1] T. Maceina, G. Juzeliunas, and J. Courtial, "Quantifying metarefraction with confocal lenslet arrays", Opt. Commun. <b>284</b>, 5008-5019 (2011)
	 * @param eta
	 * @return 1 if |eta| <= 1, 1/|eta| otherwise
	 */
	public static double calculate2DGeometricalTransmissionCoefficient(double eta)
	{
		return (Math.abs(eta) >= 1)?1:Math.abs(eta);
	}
	
	/**
	 * see section "Transmission coefficient of GCLAs" in "geometry.pdf"
	 * @param eta
	 * @param delta
	 * @param alpha1
	 * @param alpha2
	 * @param sigma1
	 * @param sigma2
	 * @param dt
	 * @param da
	 * @return	the transmission coefficient of GCLAs in one of the projections
	 */
	public static double calculate2DGeometricalTransmissionCoefficient(
			double eta, 
			double delta, 
			double alpha1, 
			double alpha2, 
			double sigma1, 
			double sigma2,
			double dt,	// transverse component of ray direction, i.e. either dU or dV
			double da
		)
	{
		double dtda = dt/da;
		
		// transverse coordinate of the top of the aperture of lens 1, projected into the plane of lens 2
		double t1P = dtda - (dtda - alpha1 - 0.5*sigma1)*eta;
		
		// transverse coordinate of the bottom of the aperture of lens 1, projected into the plane of lens 2
		double b1P = dtda - (dtda - alpha1 + 0.5*sigma1)*eta;
		
		// transverse coordinate of the top of the aperture of lens 2
		double t2 = delta + alpha2 + 0.5*sigma2;

		// transverse coordinate of the bottom of the aperture of lens 2
		double b2 = delta + alpha2 - 0.5*sigma2;
		
		// width of the aperture of lens 1, projected into the plane of lens 2
		double projectionWidth = Math.abs(sigma1*eta);
		
		// transverse coordinate of the top of the overlap between aperture 2 and the projection of aperture 1 into the plane of lens 2
		double tOverlap = Math.min(Math.max(t2, b2), Math.max(t1P, b1P));

		// transverse coordinate of the bottom of the overlap between aperture 2 and the projection of aperture 1 into the plane of lens 2
		double bOverlap = Math.max(Math.min(t2, b2), Math.min(t1P, b1P));
		
		// width of the overlap in the plane of lens 2
		double overlap = Math.max(0, tOverlap - bOverlap);
		
		// transmission coefficient
		return overlap / projectionWidth;
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

	public double getAlpha1U() {
		return alpha1U;
	}


	public void setAlpha1U(double alpha1u) {
		alpha1U = alpha1u;
	}


	public double getAlpha1V() {
		return alpha1V;
	}


	public void setAlpha1V(double alpha1v) {
		alpha1V = alpha1v;
	}


	public double getAlpha2U() {
		return alpha2U;
	}


	public void setAlpha2U(double alpha2u) {
		alpha2U = alpha2u;
	}


	public double getAlpha2V() {
		return alpha2V;
	}


	public void setAlpha2V(double alpha2v) {
		alpha2V = alpha2v;
	}


	public double getSigma1U() {
		return sigma1U;
	}


	public void setSigma1U(double sigma1u) {
		sigma1U = sigma1u;
	}


	public double getSigma1V() {
		return sigma1V;
	}


	public void setSigma1V(double sigma1v) {
		sigma1V = sigma1v;
	}


	public double getSigma2U() {
		return sigma2U;
	}


	public void setSigma2U(double sigma2u) {
		sigma2U = sigma2u;
	}


	public double getSigma2V() {
		return sigma2V;
	}


	public void setSigma2V(double sigma2v) {
		sigma2V = sigma2v;
	}


	public GlobalOrLocalCoordinateSystemType getBasis() {
		return basis;
	}

	public void setBasis(GlobalOrLocalCoordinateSystemType basis) {
		this.basis = basis;
	}

	public GCLAsTransmissionCoefficientCalculationMethodType getTransmissionCoefficientMethod() {
		return transmissionCoefficientCalculationMethod;
	}

	public void setTransmissionCoefficientMethod(GCLAsTransmissionCoefficientCalculationMethodType transmissionCoefficientMethod) {
		this.transmissionCoefficientCalculationMethod = transmissionCoefficientMethod;
	}

	public double getPixelSideLength() {
		return pixelSideLength;
	}

	public void setPixelSideLength(double pixelSideLength) {
		this.pixelSideLength = pixelSideLength;
	}

	public double getLambda() {
		return lambda;
	}

	public void setLambda(double lambda) {
		this.lambda = lambda;
	}

	public boolean isSimulateDiffractiveBlur() {
		return simulateDiffractiveBlur;
	}

	public void setSimulateDiffractiveBlur(boolean simulateDiffractiveBlur) {
		this.simulateDiffractiveBlur = simulateDiffractiveBlur;
	}

	public boolean isSimulateRayOffset() {
		return simulateRayOffset;
	}

	public void setSimulateRayOffset(boolean simulateRayOffset) {
		this.simulateRayOffset = simulateRayOffset;
	}

//	public boolean isCalculateGeometricalTransmissionCoefficient() {
//		return calculateGeometricalTransmissionCoefficient;
//	}
//
//	public void setCalculateGeometricalTransmissionCoefficient(boolean calculateGeometricalTransmissionCoefficient) {
//		this.calculateGeometricalTransmissionCoefficient = calculateGeometricalTransmissionCoefficient;
//	}
}
