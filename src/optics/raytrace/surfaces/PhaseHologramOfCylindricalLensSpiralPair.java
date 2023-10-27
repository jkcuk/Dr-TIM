package optics.raytrace.surfaces;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import math.Complex;
import math.Vector2D;
import math.Vector3D;
import optics.raytrace.core.One2OneParametrisedObject;
import optics.raytrace.core.Orientation;
import optics.raytrace.surfaces.PhaseHologramOfCylindricalLensSpiral.CylindricalLensSpiralType;

/**
 * The combination of two phase holograms of cylindrical-lens spirals, separated by zero distance.
 * The two are being treated together here to enable diffraction to be taken into account.
 * 
 * The associated SceneObject must be a ParametrisedObject as the getSurfaceCoordinates(Vector3D) method is used to calculate the coordinates on the surface.
 * 
 * @author johannes
 */
public class PhaseHologramOfCylindricalLensSpiralPair extends PhaseHologram
{	
	private static final long serialVersionUID = -8335070027134043169L;

	/**
	 * the two cylindrical-lens spirals phase holograms
	 */
	protected PhaseHologramOfCylindricalLensSpiral cylindricalLensSpiralPH[] = new PhaseHologramOfCylindricalLensSpiral[2];
	
	/**
	 * type of the cylindrical-lens spiral
	 */
	protected CylindricalLensSpiralType cylindricalLensSpiralType;

	/**
	 * cylindrical lens's focal length in part 1, 
	 * in the case of the Archimedean spiral at r=1 and 
	 * in the case of the hyperbolic spiral for phi=1;
	 * focal length of cyl. lens in part 2 is -focalLengthPart1
	 */
	protected double focalLength1Part1;
	
	/**
	 * the rotation angle of the spiral in part 1
	 */
	protected double deltaPhiPart1;

	/**
	 * the rotation angle of the spiral in part 2
	 */
	protected double deltaPhiPart2;

	/**
	 * the centre of the cylindrical lens follows either the logarithmic spiral r = exp(b (phi+deltaPhi)), 
	 * or the Archimedean spiral r = b (phi+deltaPhi),
	 * or the hyperbolic spiral r = b/(phi + deltaPhi)
	 */
	protected double b;

	/**
	 * the winding focusing achieved by changing the "surface" of the cylinder spiral to that of an Alvarez lens. 
	 * More precisely, the surface of two Alvarez lenses with zero separation between them and approximated for small relative rotation angles. 
	 */
	protected boolean alvarezWindingFocusing;
	
	/**
	 * if true, calculates the direction change wave-optically (using assumptions that are *sometimes* satisfied)
	 */
	protected boolean simulateDiffraction;
	
	/**
	 * wavelength for which diffraction is simulated
	 */
	protected double lambda;
	
	/**
	 * the scene object this surface property is associated with
	 */
	protected One2OneParametrisedObject sceneObject;
		

	//
	// constructors etc.
	//
	/**
	 * @param cylindricalLensSpiralType
	 * @param focalLengthPart1	focal length (at r=1 (Archimedean spiral) or phi=1 (hyperbolic spiral)) of part 1; part 2 has -focalLength1
	 * @param deltaPhiPart1	rotation angle of part 1
	 * @param deltaPhiPart1	rotation angle of part 2
	 * @param b
	 * @param throughputCoefficient
	 * @param AlvarezWindingFocusing
	 * @param simulateDiffraction
	 * @param lambda
	 * @param reflective
	 * @param shadowThrowing
	 */
	public PhaseHologramOfCylindricalLensSpiralPair(
			CylindricalLensSpiralType cylindricalLensSpiralType,
			double focalLength1Part1,
			double deltaPhiPart1,
			double deltaPhiPart2,
			double b,
			One2OneParametrisedObject sceneObject,
			double throughputCoefficient,
			boolean alvarezWindingFocusing,
			boolean simulateDiffraction,
			double lambda,
			boolean reflective,
			boolean shadowThrowing
		)
	{
		super(throughputCoefficient, reflective, shadowThrowing);

		setCylindricalLensSpiralType(cylindricalLensSpiralType);
		setFocalLength1Part1(focalLength1Part1);
		setDeltaPhiPart1(deltaPhiPart1);
		setDeltaPhiPart2(deltaPhiPart2);
		setB(b);
		setSceneObject(sceneObject);
		setAlvarezWindingFocusing(alvarezWindingFocusing);
		setSimulateDiffraction(simulateDiffraction);
		setLambda(lambda);
		
		setupParts();
	}
	
	public void setupParts()
	{
		// set the PhaseHologramOfCylindricalLensSpiral for the two parts
		cylindricalLensSpiralPH[0] = new PhaseHologramOfCylindricalLensSpiral(
				cylindricalLensSpiralType,
				focalLength1Part1,
				deltaPhiPart1,
				b,
				sceneObject,
				1,	// transmissionCoefficient,
				alvarezWindingFocusing,
				reflective,
				shadowThrowing
			);
		cylindricalLensSpiralPH[1] = new PhaseHologramOfCylindricalLensSpiral(
				cylindricalLensSpiralType,
				-focalLength1Part1,
				deltaPhiPart2,
				b,
				sceneObject,
				1,	// throughputCoefficient,
				alvarezWindingFocusing,
				reflective,
				shadowThrowing
			);
	}

	public PhaseHologramOfCylindricalLensSpiralPair(PhaseHologramOfCylindricalLensSpiralPair original)
	{
		this(
				original.getCylindricalLensSpiralType(),
				original.getFocalLength1Part1(),
				original.getDeltaPhiPart1(),
				original.getDeltaPhiPart2(),
				original.getB(),
				original.getSceneObject(),
				original.getTransmissionCoefficient(),
				original.isAlvarezWindingFocusing(),
				original.isSimulateDiffraction(),
				original.getLambda(),
				original.isReflective(),
				original.isShadowThrowing()
	
			);
	}
	
	@Override
	public PhaseHologramOfCylindricalLensSpiralPair clone()
	{
		return new PhaseHologramOfCylindricalLensSpiralPair(this);
	}


	//
	// setters & getters
	//
	

	
	public PhaseHologramOfCylindricalLensSpiral getCylindricalLensSpiralPH(int i) {
		return cylindricalLensSpiralPH[i];
	}

	public void setCylindricalLensSpiralPH(int i, PhaseHologramOfCylindricalLensSpiral cylindricalLensSpiralPH) {
		this.cylindricalLensSpiralPH[i] = cylindricalLensSpiralPH;
	}

	public CylindricalLensSpiralType getCylindricalLensSpiralType() {
		return cylindricalLensSpiralType;
	}

	public void setCylindricalLensSpiralType(CylindricalLensSpiralType cylindricalLensSpiralType) {
		this.cylindricalLensSpiralType = cylindricalLensSpiralType;
	}

	public double getFocalLength1Part1() {
		return focalLength1Part1;
	}

	public void setFocalLength1Part1(double focalLength1Part1) {
		this.focalLength1Part1 = focalLength1Part1;
	}

	public double getDeltaPhiPart1() {
		return deltaPhiPart1;
	}

	public void setDeltaPhiPart1(double deltaPhiPart1) {
		this.deltaPhiPart1 = deltaPhiPart1;
	}

	public double getDeltaPhiPart2() {
		return deltaPhiPart2;
	}

	public void setDeltaPhiPart2(double deltaPhiPart2) {
		this.deltaPhiPart2 = deltaPhiPart2;
	}

	public double getB() {
		return b;
	}

	public void setB(double b) {
		this.b = b;
	}

	public boolean isAlvarezWindingFocusing() {
		return alvarezWindingFocusing;
	}

	public void setAlvarezWindingFocusing(boolean alvarezWindingFocusing) {
		this.alvarezWindingFocusing = alvarezWindingFocusing;
	}

	public boolean isSimulateDiffraction() {
		return simulateDiffraction;
	}

	public void setSimulateDiffraction(boolean simulateDiffraction) {
		this.simulateDiffraction = simulateDiffraction;
//		System.out.println("simulateDiffraction="+simulateDiffraction);
//		System.exit(-1);
	}

	public double getLambda() {
		return lambda;
	}

	public void setLambda(double lambda) {
		this.lambda = lambda;
	}

	public One2OneParametrisedObject getSceneObject() {
		return sceneObject;
	}

	public void setSceneObject(One2OneParametrisedObject sceneObject) {
		this.sceneObject = sceneObject;
	}

	
	/**
	 * Consider a single "phase sawtooth" of the form exp(I phi(x)) where phi(x) is a sawtooth that
	 * comprises a straight line from (0,0) to (p w, phiPW) and another straight line from there
	 * to (w, 0).  Outside of [0, w) the function is 0.  
	 * This returns the Fourier transform of this function.
	 * @param w
	 * @param p	position of the peak [0,1]
	 * @param phiPW
	 * @param deltaK
	 * @return
	 */
	public double singleSawtoothFT2(double w, double p, double phiPW, double deltaK)
	{
		// see DiffractionAdaptiveSpecs.nb
		double phiPWprkw = phiPW - p*deltaK*w;
		Complex expIphiPWprkw = Complex.expI(phiPWprkw);
		
		// ((1 - E^(I (phiPW - p k w))) p w)/(phiPW - p k w) 
		// + ((E^(-I k w) - E^(I (phiPW - p k w))) (w - p w))/(phiPW + k (w - p w))

		Complex ft = Complex.sum(
				Complex.product(
						p*w/phiPWprkw,
						Complex.difference(new Complex(1, 0), expIphiPWprkw)
						),
				Complex.product(
						w*(1-p)/(phiPW+deltaK*w*(1-p)),
						Complex.difference(
								Complex.expI(-deltaK*w),
								expIphiPWprkw
								)
						)
		);
		
		// we want the modulus squared of the FT, so
		return Complex.modulusSquared(ft);
	}
	
	public Set<Double> getRelevantTransverseKs(double w, double p, double phiPW, long noOfKs)
	{
		HashSet<Double> s = new HashSet<Double>();
		
		long n1 = Math.round(phiPW/(2*Math.PI*p));
		long n2 = Math.round(phiPW/(2*Math.PI*(p-1)));
		
		for(long n=n1-noOfKs; n<=n1+noOfKs; n++) s.add(2*Math.PI*n/w);
		for(long n=n2-noOfKs; n<=n2+noOfKs; n++) s.add(2*Math.PI*n/w);
		
		return s;
	}
	
	@Override
	public Vector3D getTangentialDirectionComponentChangeTransmissive(
			Vector3D surfacePosition,
			Vector3D surfaceNormal)
	{	
		// calculate the x and y coordinates of the position; for this to work, the scene object must be sensibly parametrised
		Vector2D xy = sceneObject.getSurfaceCoordinates(surfacePosition);
		double x = xy.x;
		double y = xy.y;
		
		// ... and r and phi
		double r2 = x*x + y*y;
		double r = Math.sqrt(r2);
		double phi = Math.atan2(y, x);
		
		double n1 = cylindricalLensSpiralPH[0].calculateN(r, phi);
		double n2 = cylindricalLensSpiralPH[1].calculateN(r, phi);
		
		if(simulateDiffraction)
		{
			// calculate the direction of the phase gradient, i.e. the direction in which the light-ray direction is changed;
			// this is the mean of the outwards normal directions of the two spirals
			Vector3D nHat = Vector3D.sum(
					cylindricalLensSpiralPH[0].calculateSpiralOutwardsNormalDirection(r, phi),
					cylindricalLensSpiralPH[1].calculateSpiralOutwardsNormalDirection(r, phi)
				).getNormalised();
					
			// TODO calculate w, p, phiPW
			double phiU1 = cylindricalLensSpiralPH[0].calculatePhiUForCentreOfWinding(r, phi);
			double phiU2 = cylindricalLensSpiralPH[1].calculatePhiUForCentreOfWinding(r, phi);
			double phiU = 0.5*(phiU1 + phiU2);
			double w = 
					cylindricalLensSpiralPH[0].calculateWindingWidth(phiU1) + 
					cylindricalLensSpiralPH[1].calculateWindingWidth(phiU2);
			double deltaR = cylindricalLensSpiralPH[0].calculateRadialOffset(phiU, deltaPhiPart2 - deltaPhiPart1);
			double p = deltaR/w;
			double phiPW = deltaR*(2*Math.PI/lambda)*deltaR/cylindricalLensSpiralPH[0].calculateF(phiU);
			// TODO this should be the focal length of the lens that forms that winding, *not* f at r(phi)!
			// TODO use the mean of the two focal lengths?
					
//					(deltaX k x)/f
//					phiPeak = phiWedge[rH[b, phi], d*w, focalLengthH[f1, phi], k550nm];
			// -((dR k r)/f)
			
			// create a list of transverse Delta k values that we need to consider
			ArrayList<Double> deltaKList = new ArrayList<Double>();
			deltaKList.addAll(getRelevantTransverseKs(w, p, phiPW, 6));
			
			// calculate the corresponding intensities (i.e. likelihoods that the transverse k of transmitted rays has deltaK added)
			double sumOfSingleSawtoothFT2s = 0;
			for(Double deltaK:deltaKList) sumOfSingleSawtoothFT2s += singleSawtoothFT2(w, p, phiPW, deltaK);
			ArrayList<Double> pList = new ArrayList<Double>();
			for(Double deltaK:deltaKList)
				pList.add(singleSawtoothFT2(w, p, phiPW, deltaK)/sumOfSingleSawtoothFT2s);
			double deltaK;
			try {
				deltaK = deltaKList.get(math.Probability.getIndexWithProbability(pList));
			} catch (Exception e) {
				// this shouldn't happen
				e.printStackTrace();
				System.exit(-1);
				deltaK = deltaKList.get(0);
			}
			return nHat.getProductWith(deltaK/(2*Math.PI*lambda));
		}
		else
		{
			double xDerivative = cylindricalLensSpiralPH[0].calculateXDerivative(x, y, r, r2, phi, n1) + cylindricalLensSpiralPH[1].calculateXDerivative(x, y, r, r2, phi, n2);
			double yDerivative = cylindricalLensSpiralPH[0].calculateYDerivative(x, y, r, r2, phi, n1) + cylindricalLensSpiralPH[1].calculateYDerivative(x, y, r, r2, phi, n2);

			// from the derivatives, construct the transverse direction change in global (x, y, z) coordinates
			ArrayList<Vector3D> xHatYHat = sceneObject.getSurfaceCoordinateAxes(surfacePosition);
			return Vector3D.sum(xHatYHat.get(0).getProductWith(xDerivative), xHatYHat.get(1).getProductWith(yDerivative));
		}
	}

	@Override
	public Vector3D getTangentialDirectionComponentChangeReflective(Orientation incidentLightRayOrientation,
			Vector3D surfacePosition, Vector3D surfaceNormal)
	{
		// TODO Not sure this makes sense completely...
		return getTangentialDirectionComponentChangeTransmissive(surfacePosition, surfaceNormal);
	}

}