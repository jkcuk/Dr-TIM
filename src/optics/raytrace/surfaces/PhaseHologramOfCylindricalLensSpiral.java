package optics.raytrace.surfaces;

import java.util.ArrayList;

import math.Vector2D;
import math.Vector3D;
import optics.raytrace.core.One2OneParametrisedObject;
import optics.raytrace.core.Orientation;

/**
 * A phase hologram of a cylindrical-lens spiral.
 * This is a cylindrical lens of focal length f, bent into a spiral centred at surface coordinates (0, 0).
 * If the spiral type is LOGARITHMIC, then the spiral is of the form r = exp(b phi),
 * where r=sqrt(x^2+y^2) and phi=atan2(y,x) and x and y are the surface coordinates.
 * If the spiral type is ARCHIMEDEAN, then the spiral is of the form r = b phi.
 * If the spiral type is HYPERBOLIC, then the spiral is of the form r = -1/b phi.
 * 
 * The associated SceneObject must be a ParametrisedObject as the getSurfaceCoordinates(Vector3D) method is used to calculate the coordinates on the surface.
 * 
 * @author johannes, Ivan, Di Wu, Maik
 */
public class PhaseHologramOfCylindricalLensSpiral extends PhaseHologram
{
	private static final long serialVersionUID = 5846229655581148838L;

	public enum CylindricalLensSpiralType
	{
		ARCHIMEDEAN("Archimedean"),
		LOGARITHMIC("Logarithmic"),
		HYPERBOLIC("Hyperbolic");

		private String description;
		private CylindricalLensSpiralType(String description) {this.description = description;}	
		@Override
		public String toString() {return description;}
	}

	/**
	 * type of the cylindrical-lens spiral
	 */
	protected CylindricalLensSpiralType cylindricalLensSpiralType;

	/**
	 * the cylindrical lens's focal length, in the case of the Archimedean spiral at r=1 and in the case of the
	 * hyperbolic spiral for phi=1;
	 * the cross-section of the cylindrical lens (without Alvarez winding focussing) is 
	 *   Phi(r) = (pi d^2)(lambda f),
	 * where r is the radial distance
	 */
	protected double focalLength1;
	
	/**
	 * the rotation angle of the spiral
	 */
	protected double deltaPhi;

	/**
	 * the centre of the cylindrical lens follows either the logarithmic spiral r = exp(b (phi+deltaPhi)), 
	 * or the Archimedean spiral r = b (phi+deltaPhi),
	 * or the hyperbolic spiral r = -1/b(phi + deltaPhi)
	 */
	protected double b;

	/**
	 * the winding focusing achieved by changing the "surface" of the cylinder spiral to that of an Alvarez lens. 
	 * More precisely, the surface of two Alvarez lenses with zero separation between them and approximated for small relative rotation angles. 
	 */
	protected boolean alvarezWindingFocusing;
	
	/**
	 * the scene object this surface property is associated with
	 */
	protected One2OneParametrisedObject sceneObject;
	
	
	// internal variables
	
	/**
	 * pre-calculated variable in setB()
	 */
	protected double b2pi;
	protected double nHalf;
		

	//
	// constructors etc.
	//
	/**
	 * @param cylindricalLensSpiralType
	 * @param focalLength1
	 * @param deltaPhi
	 * @param b
	 * @param throughputCoefficient
	 * @param AlvarezWindingFocusing
	 * @param reflective
	 * @param shadowThrowing
	 */
	public PhaseHologramOfCylindricalLensSpiral(
			CylindricalLensSpiralType cylindricalLensSpiralType,
			double focalLength1,
			double deltaPhi,
			double b,
			One2OneParametrisedObject sceneObject,
			double throughputCoefficient,
			boolean alvarezWindingFocusing,
			boolean reflective,
			boolean shadowThrowing
		)
	{
		super(throughputCoefficient, reflective, shadowThrowing);
		setCylindricalLensSpiralType(cylindricalLensSpiralType);
		setFocalLength1(focalLength1);
		setDeltaPhi(deltaPhi);
		setB(b);
		setAlvarezWindingFocusing(alvarezWindingFocusing);
		setSceneObject(sceneObject);
	}

	/**
	 * @param cylindricalLensSpiralType
	 * @param focalLength1
	 * @param deltaPhi
	 * @param b
	 * @param throughputCoefficient
	 * @param reflective
	 * @param shadowThrowing
	 */
	public PhaseHologramOfCylindricalLensSpiral(
			CylindricalLensSpiralType cylindricalLensSpiralType,
			double focalLength1,
			double deltaPhi,
			double b,
			One2OneParametrisedObject sceneObject,
			double throughputCoefficient,
			boolean reflective,
			boolean shadowThrowing
		)
	{
		super(throughputCoefficient, reflective, shadowThrowing);
		setCylindricalLensSpiralType(cylindricalLensSpiralType);
		setFocalLength1(focalLength1);
		setDeltaPhi(deltaPhi);
		setB(b);
		setAlvarezWindingFocusing(false);
		setSceneObject(sceneObject);
	}

	public PhaseHologramOfCylindricalLensSpiral(PhaseHologramOfCylindricalLensSpiral original) {
		super(original);
		setCylindricalLensSpiralType(original.getCylindricalLensSpiralType());
		setFocalLength1(original.getFocalLength1());
		setDeltaPhi(original.getDeltaPhi());
		setB(original.getB());
		setAlvarezWindingFocusing(original.isAlvarezWindingFocusing());
		setSceneObject(original.getSceneObject());
	}
	
	@Override
	public PhaseHologramOfCylindricalLensSpiral clone()
	{
		return new PhaseHologramOfCylindricalLensSpiral(this);
	}


	//
	// setters & getters
	//
	
	
	public CylindricalLensSpiralType getCylindricalLensSpiralType() {
		return cylindricalLensSpiralType;
	}

	public void setCylindricalLensSpiralType(CylindricalLensSpiralType cylindricalLensSpiralType) {
		this.cylindricalLensSpiralType = cylindricalLensSpiralType;
	}

	public double getFocalLength1() {
		return focalLength1;
	}

	public void setFocalLength1(double focalLength1) {
		this.focalLength1 = focalLength1;
	}

	public double getDeltaPhi() {
		return deltaPhi;
	}

	public void setDeltaPhi(double deltaPhi) {
		this.deltaPhi = deltaPhi;
	}

	public double getB() {
		return b;
	}

	public void setB(double b) {
		this.b = b;
		b2pi = 2*b*Math.PI;
		nHalf = Math.log(0.5*(1. + Math.exp(b2pi)))/b2pi;
	}

	public boolean isAlvarezWindingFocusing() {
		return alvarezWindingFocusing;
	}

	public void setAlvarezWindingFocusing(boolean alvarezWindingFocusing) {
		this.alvarezWindingFocusing = alvarezWindingFocusing;
	}

	public One2OneParametrisedObject getSceneObject() {
		return sceneObject;
	}

	public void setSceneObject(One2OneParametrisedObject sceneObject) {
		this.sceneObject = sceneObject;
	}

	
	
	/**
	 * Calculate the distance r from the centre of the logarithmic or archimedean spiral with r = a Exp(b (phi + n*2*pi)) or r = a + b (phi + n*2*pi) respectively.
	 * Note that the values of phi are restricted to the range [0, 2 pi].
	 * @param phi
	 * @param n
	 * @return	r
	 */
	private double calculateSpiralDistanceFromCentre(double phi, double n)
	{
		switch(cylindricalLensSpiralType)
		{
		case ARCHIMEDEAN:
			return b*(phi+deltaPhi+n*2*Math.PI);
		//dwu1for calculateSpiralDistanceFromCentre hyperbolic begin--
		case HYPERBOLIC:
			//return b/(phi+deltaPhi+n*2*Math.PI);
			return -1/(b*(phi+deltaPhi+n*2*Math.PI));
		//dwu1for calculateSpiralDistanceFromCentre hyperbolic end--
		case LOGARITHMIC:
		default:
			return Math.exp(b*(phi+deltaPhi+n*2*Math.PI));			
		}
	}
	
	/**
	 * @param r
	 * @param phi
	 * @return	the number of the winding that corresponds to position (r, phi)
	 */
	public double calculateN(double r, double phi)
	{
		double phiPlus = phi+deltaPhi;
		
		switch(cylindricalLensSpiralType)
		{
		case ARCHIMEDEAN:
			return Math.ceil(-((b*phiPlus - r)/b2pi) - 0.5);
		case HYPERBOLIC:
			//TODO when we change phiPlus to just phi it becomes an even better lens, but this is not physical I think.. 
			//..why and is there a way to make it physical?
			return Math.floor( 0.5 - (1 + Math.sqrt(1+b2pi*b2pi*r*r)+2*b*r*phiPlus)/(2*b2pi*r)); 
		case LOGARITHMIC:
		default:
			return Math.ceil(-((b*phiPlus - Math.log(r))/b2pi) - nHalf);
		}
	}
	
	double calculatePhiUForCentreOfWinding(double r, double phi)
	{
		return phi + deltaPhi + calculateN(r, phi)*2*Math.PI;
	}
	
	
	double calculateXDerivative(double x, double y, double r, double r2, double phi, double n)
	{	
		double r_n = calculateSpiralDistanceFromCentre(phi, n);
		double phiPlus = phi+deltaPhi;
		double psi = phiPlus + 2*Math.PI*n;

		// calculated in Mathematica
		switch(cylindricalLensSpiralType)
		{
		case ARCHIMEDEAN:
			//In the Archimedean case the focal length fArch is given by the ratio of the focal length at radius 1 and the radius
			// hence fArch is given by the focalLength divided by the radius at any given point. 
			if(alvarezWindingFocusing){
				return 
						(r_n-r)*(2*r*r*x+b*(r_n+r)*y)/(2*focalLength1*r*r);
						// (r_n-r)*(2*b*y*r_n+r*x*(r+r_n))/(2*focalLength1*r*r);
			}
			else
			{
				return -(((x/r)+b*y/r2)*(r-r_n))/(focalLength1/r_n);
			}
		case HYPERBOLIC:
			//return ((y*(r-r_n)*(r-r_n)/(2*r*r))-(phiPlus+2*n*Math.PI)*(r-r_n)*((x/r)-((y*r_n*r_n)/(b*r*r))))/focalLength1;
			//return ( (1+b*r_n*phi)*(y-b*r_n*y*phi+2*b*r_n*x*phi*phi) )/(2*b*focalLength1*r_n*r_n*phi*phi);
			return ( (1+b*r*psi)*(y-b*r*y*psi+2*b*r*x*psi*psi) )/(2*b*focalLength1*r*r*psi*psi);
		case LOGARITHMIC:
		default:
			//In the logarithmic case the focal length, fLog, is constant and hence simply the focalLength.
			if(alvarezWindingFocusing){
				return (-r*r*r*(3*x+b*y)+3*r*r_n*r_n*(x-b*y)+4*b*y*r_n*r_n*r_n)/(6*r_n*focalLength1*r*r);
//				return Math.exp(-b*(phi+n*2*Math.PI))*(4*b*Math.exp(3*b*(phi+n*2*Math.PI))*y+3*Math.exp(2*b*(phi+n*2*Math.PI)) *r_n *(x-b*y)-r_n*r_n*r_n*(3*x+b*y))/
//				(6*focalLength1*r_n*r_n);
			}
			else
			{
				return -(((b*Math.exp(b*(phi+n*2*Math.PI))*y)/r2 + x/r) * (-Math.exp(b*(phiPlus+n*2*Math.PI)) + r))/focalLength1;
			}

			
			//The equation below is if the cylindrical focusing were to be added instead of subtracted in lensPhaseOverKWF from spiral lens caluclations.nb
			//This looks wrong but in case anyone is interested:
			//double r_n = calculateSpiralDistanceFromCentre(phi, n);
			//return (4*x - r*(3*x+b*y)/(3*r_n) - r_n*(9*r*(x-b*y)+8*b*y*r_n)/(3*r*r));
			
		}
	}
	
	double calculateYDerivative(double x, double y, double r, double r2, double phi, double n)
	{
		double r_n = calculateSpiralDistanceFromCentre(phi, n);
		double phiPlus = phi+deltaPhi;
		double psi = phiPlus + 2*Math.PI*n;

		// calculated in Mathematica
		switch(cylindricalLensSpiralType)
		{
		case ARCHIMEDEAN:
			//In the Archimedean case the focal length fArch is given by the ratio of the focal length at radius 1 and the radius
			// hence fArch is given by the focalLength divided by the radius at any given point. 
			if(alvarezWindingFocusing){
				return 
						(r_n-r)*(2*r*r*y-b*(r_n+r)*x)/(2*focalLength1*r*r);
						// -(r_n-r)*(2*b*x*r_n-r*y*(r+r_n))/(2*focalLength1*r*r);
			}else {
				return -(((y/r)-b*x/r2)*(r-r_n))/(focalLength1/r_n);
			}
		case HYPERBOLIC:
			//return -((x*(r-r_n)*(r-r_n)/(2*r*r))+(phiPlus+2*n*Math.PI)*(r-r_n)*((y/r)+((x*r_n*r_n)/(b*r*r))))/focalLength1;
			//return ( (1+b*r_n*phi)*(2*b*r_n*y*phi*phi + x*(b*r_n*phi-1)) )/(2*b*focalLength1*r_n*r_n*phi*phi);
			return ( (1+b*r*psi)*(2*b*r*y*psi*psi + x*(b*r*psi-1)) )/(2*b*focalLength1*r*r*psi*psi);
		case LOGARITHMIC:
		default:
			//In the logarithmic case the focal length, fLog, is constant and hence simply the focalLength.
			if(alvarezWindingFocusing){
				return (r*r*r*(b*x-3*y)+3*r*r_n*r_n*(b*x+y)-4*b*x*r_n*r_n*r_n)/(6*focalLength1*r*r*r_n);
//				return Math.exp(-b*(phi+n*2*Math.PI))*(-4*b*Math.exp(3*b*(phi+n*2*Math.PI))*x+3*Math.exp(2*b*(phi+n*2*Math.PI)) *r_n *(b*x + y)+r_n*r_n*r_n*(b*x-3*y))/
//				(6*focalLength1*r_n*r_n);
				
			}else {
				return -(((-b*Math.exp(b*(phiPlus+n*2*Math.PI))*x)/r2 + y/r) * (-Math.exp(b*(phiPlus+n*2*Math.PI)) + r))/focalLength1;
			}
			
			
			//The equation below is if the cylindrical focusing were to be added instead of subtracted in lensPhaseOverKWF from spiral lens caluclations.nb
			//This looks wrong but in case anyone is interested:
			//double r_n = calculateSpiralDistanceFromCentre(phi, n);
			//return -((r_n-r)*(r*r*(-b*x+3*y)+r_n*(b*r*x+9*r*y+8*b*x*r_n)))/(3*r*r*r_n);
		}
	}
	
	/**
	 * @param phiU	the (unbound) angle phi, i.e. it can take values outside of some 2 pi range
	 * @return	the radial distance that corresponds to the value phi
	 */
	double calculateR(double phiU)
	{
		switch(cylindricalLensSpiralType)
		{
		case ARCHIMEDEAN:
			return b*phiU;
		case HYPERBOLIC:
			//return b/phiU;
			return -1/(b*phiU);
		case LOGARITHMIC:
		default:
			return Math.exp(b*phiU);
		}
	}
	
	/**
	 * @param phiU	the (unbound) angle phi, i.e. it can take values outside of some 2 pi range
	 * @return	the focal length of the cylindrical lens that corresponds to the value phi
	 */
	double calculateF(double phiU)
	{
		switch(cylindricalLensSpiralType)
		{
		case ARCHIMEDEAN:
			return focalLength1/calculateR(phiU);
		case HYPERBOLIC:
			//return focalLength1/phiU;
			return focalLength1*calculateR(phiU);
		case LOGARITHMIC:
		default:
			return focalLength1;
		}
	}
	

	/**
	 * @param r
	 * @return	the (unbound) angle phi (i.e. it can take values outside of some 2 pi range) that corresponds to the radial distance r
	 */
	double calculateUnboundPhi(double r)
	{
		switch(cylindricalLensSpiralType)
		{
		case ARCHIMEDEAN:
			// r = b phi, so phi = r/b
			return r/b;
		case HYPERBOLIC:
			// r = b / phi, so phi = b / r
			//return b / r;
			// r = -1 / b phi, so phi = -1 / b r
			return -1 / (b*r);
			
		case LOGARITHMIC:
		default:
			// r = e^(b phi), so ln r = b phi, so phi = ln r / b
			return Math.log(r)/b;
		}
	}
	
	/**
	 * @param phiU	the (unbound) angle phi, i.e. it can take values outside of some 2 pi range
	 * @return	the width of the winding at phi
	 */
	double calculateWindingWidth(double phiU)
	{
		return Math.abs(calculateR(phiU-Math.PI) - calculateR(phiU+Math.PI));
	}
	
	double calculateRadialOffset(double phi, double deltaPhi)
	{
		return calculateR(phi-0.5*deltaPhi) - calculateR(phi+0.5*deltaPhi);
	}
	
	/**
	 * @param r
	 * @param phi
	 * @return	the direction of the spiral line
	 */
	Vector3D calculateSpiralDirection(double r, double phi)
	{
		// the position vector for the position described by (r, phi)
		Vector3D pos = sceneObject.getPointForSurfaceCoordinates(r, phi);
		
		// the surface coordinates of this position, i.e. Cartesian coordinates in the plane of the scene object
		Vector2D xy = sceneObject.getSurfaceCoordinates(pos);
		double x = xy.x;
		double y = xy.y;
		double rr = Math.sqrt(x*x+y*y);
		
		ArrayList<Vector3D> axes = sceneObject.getSurfaceCoordinateAxes(pos);
		// the vector rHat, a unit vector from the centre to pos
		Vector3D rHat = Vector3D.sum(
				axes.get(0).getProductWith(x/rr),
				axes.get(1).getProductWith(y/rr)
			);
		// the vector phiHat, a unit vector pointing in the azimuthal direction
		Vector3D phiHat = Vector3D.sum(
				axes.get(0).getProductWith(-y/rr),
				axes.get(1).getProductWith(x/rr)
			);
		
		// the non-normalised direction of the spiral, given by the direction 
		// (d rv/d phiU) / r, is (see J's lab book entry 14/9/23, p. 154-5)
		// 		1/phiU rHat + phiHat (Archimedean spiral; rHat term dominates at centre (phiU -> 0))
		// 		b rHat + phiHat (logarithmic spiral; direction is same everywhere)
		// 		1/phiU rHat + phiHat (hyperbolic spiral; rHat term insignificant at centre (phiU -> \infty))
		//The hyperbolic spiral of the new form, -1/b phi gives a positive derivative and hence the sign is changed from the lab book entry. Used to be (-1/phiU rHat + phiHat)
		// (phiU is the *unbound* angle phi)
		
		double phiU = 
				calculatePhiUForCentreOfWinding(r, phi);
				// calculateUnboundPhi(r);	// unbound phi
		double rHatFactor;
		switch(cylindricalLensSpiralType)
		{
		case ARCHIMEDEAN:
			rHatFactor = 1./phiU;
			break;
		case HYPERBOLIC:
			//TODO I think this is correct but double check it..
			rHatFactor = 1./phiU;
			break;
		case LOGARITHMIC:
		default:
			rHatFactor = b;
		}
		
		return Vector3D.sum(rHat.getProductWith(rHatFactor), phiHat);
	}
	/**
	 * @param r
	 * @param phi
	 * @return	a (non-normalised) outwards normal to the spiral direction
	 */
	Vector3D calculateSpiralOutwardsNormalDirection(double r, double phi)
	{
		// the position vector for the position described by (r, phi)
		Vector3D pos = sceneObject.getPointForSurfaceCoordinates(r, phi);
		
		// the surface coordinates of this position, i.e. Cartesian coordinates in the plane of the scene object
		Vector2D xy = sceneObject.getSurfaceCoordinates(pos);
		double x = xy.x;
		double y = xy.y;
		double rr = Math.sqrt(x*x+y*y);
		
		ArrayList<Vector3D> axes = sceneObject.getSurfaceCoordinateAxes(pos);
		// the vector rHat, a unit vector from the centre to pos
		Vector3D rHat = Vector3D.sum(
				axes.get(0).getProductWith(x/rr),
				axes.get(1).getProductWith(y/rr)
			);
		// the vector phiHat, a unit vector pointing in the azimuthal direction
		Vector3D phiHat = Vector3D.sum(
				axes.get(0).getProductWith(-y/rr),
				axes.get(1).getProductWith(x/rr)
			);
		
		// the non-normalised outwards normal direction to the spiral is (see J's lab book entry 15/9/23, p. 156)
		// 		rHat - (1/phiU) phiHat (Archimedean spiral)
		// 		rHat - b phiHat (logarithmic spiral)
		// 		rHat - (1/phiU) phiHat (hyperbolic spiral)
		//The hyperbolic spiral of the new form, -1/b phi gives a positive derivative and hence the sign is changed from the lab book entry. Used to be (rHat + (1/phiU) phiHat)
		// (phiU is the *unbound* angle phi)
		
		double phiU = calculateUnboundPhi(r);	// unbound phi
		double phiHatFactor;
		switch(cylindricalLensSpiralType)
		{
		case ARCHIMEDEAN:
			phiHatFactor = -1./phiU;
			break;
		case HYPERBOLIC:
			//TODO think this is correct double check it
			phiHatFactor = -1./phiU;
			break;
		case LOGARITHMIC:
		default:
			phiHatFactor = -b;
		}
		
		return Vector3D.sum(rHat, phiHat.getProductWith(phiHatFactor));
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
		
		double n = calculateN(r, phi);
		double xDerivative = calculateXDerivative(x, y, r, r2, phi, n);
		double yDerivative = calculateYDerivative(x, y, r, r2, phi, n);
		
		// from the derivatives, construct the transverse direction change in global (x, y, z) coordinates
		ArrayList<Vector3D> xHatYHat = sceneObject.getSurfaceCoordinateAxes(surfacePosition);
		return Vector3D.sum(xHatYHat.get(0).getProductWith(xDerivative), xHatYHat.get(1).getProductWith(yDerivative));
	}

	@Override
	public Vector3D getTangentialDirectionComponentChangeReflective(Orientation incidentLightRayOrientation,
			Vector3D surfacePosition, Vector3D surfaceNormal)
	{
		// TODO Not sure this makes sense completely...
		return getTangentialDirectionComponentChangeTransmissive(surfacePosition, surfaceNormal);
	}

}