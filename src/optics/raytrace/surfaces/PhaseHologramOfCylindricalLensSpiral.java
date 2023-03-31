package optics.raytrace.surfaces;

import java.util.ArrayList;

import math.Vector2D;
import math.Vector3D;
import optics.raytrace.core.One2OneParametrisedObject;
import optics.raytrace.core.Orientation;

/**
 * A phase hologram of a cylindrical-lens spiral.
 * This is a cylindrical lens of focal length f, bent into a spiral centred at surface coordinates (0, 0).
 * If the spiral type is LOGARITHMIC, then the spiral is of the form r = a exp(b phi),
 * where r=sqrt(x^2+y^2) and phi=atan2(y,x) and x and y are the surface coordinates.
 * If the spiral type is ARCHIMEDEAN, then the spiral is of the form r = a + b phi.
 * 
 * The associated SceneObject must be a ParametrisedObject as the getSurfaceCoordinates(Vector3D) method is used to calculate the coordinates on the surface.
 * 
 * @author johannes, Ivan
 */
public class PhaseHologramOfCylindricalLensSpiral extends PhaseHologram
{
	private static final long serialVersionUID = 5846229655581148838L;

	public enum CylindricalLensSpiralType
	{
		ARCHIMEDEAN("Archimedean"),
		LOGARITHMIC("Logarithmic");
		
		private String description;
		private CylindricalLensSpiralType(String description) {this.description = description;}	
		@Override
		public String toString() {return description;}
	}

	/**
	 * type of the cylindrical-lens spiral
	 */
	private CylindricalLensSpiralType cylindricalLensSpiralType;

	/**
	 * the cylindrical lens's focal length (at distance t=1);
	 * the cross-section of the cylindrical lens is Phi(t) = (pi d^2)(lambda f), where t is the transverse direction
	 */
	private double focalLength;
	
	/**
	 * the centre of the cylindrical lens follows either the logarithmic spiral r = a exp(b phi), or the Archimedean spiral r = a + b phi
	 */
	private double a;

	/**
	 * the centre of the cylindrical lens follows either the logarithmic spiral r = a exp(b phi), or the Archimedean spiral r = a + b phi
	 */
	private double b;
	
	/**
	 * the winding focusing achieved by changing the "surface" of the cylinder spiral to that of an Alvarez lens. 
	 * More precisely, the surface of two Alvarez lenses with zero separation between them and approximated for small relative rotation angles. 
	 */
	private boolean alvarezWindingFocusing;
	
	/**
	 * the scene object this surface property is associated with
	 */
	private One2OneParametrisedObject sceneObject;
	
	
	// internal variables
	
	/**
	 * pre-calculated variable in setB();
	 */
	private double b2pi;
	private double nHalf;
	

	//
	// constructors etc.
	//
	/**
	 * @param cylindricalLensSpiralType
	 * @param focalLength
	 * @param a
	 * @param b
	 * @param throughputCoefficient
	 * @param AlvarezWindingFocusing
	 * @param reflective
	 * @param shadowThrowing
	 */
	public PhaseHologramOfCylindricalLensSpiral(
			CylindricalLensSpiralType cylindricalLensSpiralType,
			double focalLength,
			double a,
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
		setFocalLength(focalLength);
		setA(a);
		setB(b);
		setAlvarezWindingFocusing(alvarezWindingFocusing);
		setSceneObject(sceneObject);
	}

	/**
	 * @param cylindricalLensSpiralType
	 * @param focalLength
	 * @param a
	 * @param b
	 * @param throughputCoefficient
	 * @param reflective
	 * @param shadowThrowing
	 */
	public PhaseHologramOfCylindricalLensSpiral(
			CylindricalLensSpiralType cylindricalLensSpiralType,
			double focalLength,
			double a,
			double b,
			One2OneParametrisedObject sceneObject,
			double throughputCoefficient,
			boolean reflective,
			boolean shadowThrowing
		)
	{
		super(throughputCoefficient, reflective, shadowThrowing);
		setCylindricalLensSpiralType(cylindricalLensSpiralType);
		setFocalLength(focalLength);
		setA(a);
		setB(b);
		setAlvarezWindingFocusing(false);
		setSceneObject(sceneObject);
	}

	public PhaseHologramOfCylindricalLensSpiral(PhaseHologramOfCylindricalLensSpiral original) {
		super(original);
		setCylindricalLensSpiralType(original.getCylindricalLensSpiralType());
		setFocalLength(original.getFocalLength());
		setA(original.getA());
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

	public double getFocalLength() {
		return focalLength;
	}

	public void setFocalLength(double focalLength) {
		this.focalLength = focalLength;
	}

	public double getA() {
		return a;
	}

	public void setA(double a) {
		this.a = a;
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
	 * Calculate the distance r from the centre of the logarithmic spiral r = a Exp(b (phi + n*2*pi)).
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
			return a + b*(phi+n*2*Math.PI);
		case LOGARITHMIC:
		default:
			return a*Math.exp(b*(phi+n*2*Math.PI));			
		}
	}
	
	public double calculateN(double r, double phi)
	{
		switch(cylindricalLensSpiralType)
		{
		case ARCHIMEDEAN:
			return Math.ceil(-((b*phi - r + a)/b2pi) - 0.5);
		case LOGARITHMIC:
		default:
			return Math.ceil(-((b*phi - Math.log(r/a))/b2pi) - nHalf);
		}
	}
	
//	public double lensHeight1(double r, double phi)
//	{
//		switch(cylindricalLensSpiralType)
//		{
//		case ARCHIMEDEAN:
//			return 
//		case LOGARITHMIC:
//		default:
//			return MyMath.square(r-calculateSpiralDistanceFromCentre(phi, calculateN(r, phi)));
//		// return MyMath.square(r-calculateSpiralDistanceFromCentre(phi+calculateDeltaPhi(r, phi)));
//	}
	
	private double calculateXDerivative(double x, double y, double r, double r2, double phi, double n)
	{	
		double r_n = calculateSpiralDistanceFromCentre(phi, n);
	
		// calculated in Mathematica
		switch(cylindricalLensSpiralType)
		{
		case ARCHIMEDEAN:
			if(alvarezWindingFocusing){
				return (r-r_n)*(-b*r*(3+r)*y+r_n*(3*r*(2+r)*x+b*(9+5*r)*y-(3*r*x+4*b*y)*r_n))/(3*r*r*r_n);
			}else {
				return 2*((x/r)+b*y/r2)*(r-a-b*(n*2*Math.PI + phi));
			}
		case LOGARITHMIC:
		default:

			if(alvarezWindingFocusing){
				return (r*(x+b*y/3)/r_n) + ((-x+b*y)*r_n/r) - (4*b*y*r_n*r_n/(3*r*r));
			}else {
				return 2*((a*b*Math.exp(b*(phi+n*2*Math.PI))*y)/r2 + x/r) * (-a*Math.exp(b*(phi+n*2*Math.PI)) + r);
			}

			
			//The equation below is if the cylindrical focusing were to be added instead of subtracted in lensPhaseOverKWF from spiral lens caluclations.nb
			//This looks wrong but in case anyone is interested:
			//double r_n = calculateSpiralDistanceFromCentre(phi, n);
			//return (4*x - r*(3*x+b*y)/(3*r_n) - r_n*(9*r*(x-b*y)+8*b*y*r_n)/(3*r*r));
			
		}
	}

	private double calculateYDerivative(double x, double y, double r, double r2, double phi, double n)
	{
		double r_n = calculateSpiralDistanceFromCentre(phi, n);
		// calculated in Mathematica
		switch(cylindricalLensSpiralType)
		{
		case ARCHIMEDEAN:
			
			if(alvarezWindingFocusing){
				return (r-r_n)*(b*r*(3+r)*x+r_n*(-b*(9+5*r)*x+3*r*(2+r)*y+(4*b*x-3*r*y)*r_n))/(3*r*r*r_n);
			}else {
				return 2*((y/r)-b*x/r2)*(r-a-b*(n*2*Math.PI + phi));
			}
		case LOGARITHMIC:
		default:
			
			if(alvarezWindingFocusing){
				return -(r*r*r*(b*x-3*y) + 3*r*(b*x+y)*r_n*r_n - 4*b*x*r_n*r_n*r_n)/(3*r*r*r_n);
			}else {
				return 2*((-a*b*Math.exp(b*(phi+n*2*Math.PI))*x)/r2 + y/r) * (-a*Math.exp(b*(phi+n*2*Math.PI)) + r);
			}
			
			
			//The equation below is if the cylindrical focusing were to be added instead of subtracted in lensPhaseOverKWF from spiral lens caluclations.nb
			//This looks wrong but in case anyone is interested:
			//double r_n = calculateSpiralDistanceFromCentre(phi, n);
			//return -((r_n-r)*(r*r*(-b*x+3*y)+r_n*(b*r*x+9*r*y+8*b*x*r_n)))/(3*r*r*r_n);
		}
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
		
		double f;
		switch(cylindricalLensSpiralType)
		{
		case ARCHIMEDEAN:
			f = focalLength / calculateSpiralDistanceFromCentre(phi, n);
			break;
		case LOGARITHMIC:
		default:
			f = focalLength;
		}
		
		ArrayList<Vector3D> xHatYHat = sceneObject.getSurfaceCoordinateAxes(surfacePosition);
		return Vector3D.sum(xHatYHat.get(0).getProductWith(xDerivative), xHatYHat.get(1).getProductWith(yDerivative)).getProductWith(-0.5/f);
	}

	@Override
	public Vector3D getTangentialDirectionComponentChangeReflective(Orientation incidentLightRayOrientation,
			Vector3D surfacePosition, Vector3D surfaceNormal)
	{
		// TODO Not sure this makes sense completely...
		return getTangentialDirectionComponentChangeTransmissive(surfacePosition, surfaceNormal);
	}

}
