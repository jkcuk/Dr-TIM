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
		LOGARITHMIC("Logarithmic"),
		//dwu hyperbolic begin--
		HYPERBOLIC("Hyperbolic");

		//dwu hyperbolic end--
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
	 * the cylindrical lens's focal length, in the case of the Archimedean spiral at r=1 and in the case of the
	 * hyperbolic spiral for phi=1;
	 * the cross-section of the cylindrical lens is Phi(r) = (pi d^2)(lambda f), where r is the radial distance
	 */
	private double focalLength1;
	
	/**
	 * the rotation angle of the spiral
	 */
	private double deltaPhi;

	/**
	 * the centre of the cylindrical lens follows either the logarithmic spiral r = exp(b (phi+deltaPhi)), 
	 * or the Archimedean spiral r = b (phi+deltaPhi),
	 * or the hyperbolic spiral r = b/(phi + deltaPhi)
	 */
	private double b;
	//dwu3for powD begin--
	/**
	 *
	 */
//	private double powD;
	//dwu3for powD end--
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
	 * pre-calculated variable in setB()
	 */
	private double b2pi;
	private double nHalf;
	
	// pre-calculated in preCalculateA()
	private double a;
	

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
			//dwu8 forpowDinconstructor begin--
//			double powD,
			//dwu8 forpowDinconstructor end--
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
		//dwu9 forpowDinconstructor begin--
//		setPowD(powD);
		//dwu9 forpowDinconstructor end--
		preCalculateA();
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
		preCalculateA();
		setAlvarezWindingFocusing(false);
		setSceneObject(sceneObject);
	}

	public PhaseHologramOfCylindricalLensSpiral(PhaseHologramOfCylindricalLensSpiral original) {
		super(original);
		setCylindricalLensSpiralType(original.getCylindricalLensSpiralType());
		setFocalLength1(original.getFocalLength1());
		setDeltaPhi(original.getDeltaPhi());
		setB(original.getB());
		//dwu7for getsetinconstructor begin--
//		setPowD(original.getPowD());
		//dwu7for getsetinconstructor end--
		preCalculateA();
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
	//dwu6for getsetPowD begin--
//	public double getPowD() {
//		return powD;
//	}
//	public void setPowD(double powD){this.powD=powD;}
	//dwu6for getsetPowD end--
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

	private void preCalculateA()
	{
		switch(cylindricalLensSpiralType)
		{
		case ARCHIMEDEAN:
			a = 1+b*deltaPhi;
			break;
		case LOGARITHMIC:
		default:
			a = 1*Math.exp(b*deltaPhi);
		}
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
			return a+b*(phi+n*2*Math.PI);
		//dwu1for calculateSpiralDistanceFromCentre hyperbolic begin--
		case HYPERBOLIC:
			return b/((phi+deltaPhi)+n*2*Math.PI);
		//dwu1for calculateSpiralDistanceFromCentre hyperbolic end--
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
		//dwu2for calculateN hyperbolic begin--
		case HYPERBOLIC:
//			return Math.ceil((b-phi*r)/(2*r*Math.PI)-(-phi-2*Math.PI*Math.floor((b-phi*r)/(2*r*Math.PI)))/(2*Math.PI)-
//					(Math.sqrt(phi*phi+2*phi*Math.PI+4*phi*Math.PI*Math.floor((b-phi*r)/(2*r*Math.PI))+4*Math.PI*Math.PI
//							*Math.floor((b-phi*r)/(2*r*Math.PI))+4*Math.PI*Math.PI*Math.floor((b-phi*r)/(2*r*Math.PI))*
//							Math.floor((b-phi*r)/(2*r*Math.PI))))/(2*Math.PI));
//			return Math.ceil((b-(phi+deltaPhi)*r)/(2*r*Math.PI)-(-(phi+deltaPhi)-2*Math.PI*Math.floor((b-(phi+deltaPhi)*r)/(2*r*Math.PI)))/(2*Math.PI)-
//						(Math.sqrt(Math.abs((phi+deltaPhi)*(phi+deltaPhi)+2*(phi+deltaPhi)*Math.PI+4*(phi+deltaPhi)*Math.PI*Math.floor((b-(phi+deltaPhi)*r)/(2*r*Math.PI))+4*Math.PI*Math.PI
//								*Math.floor((b-(phi+deltaPhi)*r)/(2*r*Math.PI))+4*Math.PI*Math.PI*Math.floor((b-(phi+deltaPhi)*r)/(2*r*Math.PI))*
//								Math.floor((b-(phi+deltaPhi)*r)/(2*r*Math.PI)))))/(2*Math.PI));
			return Math.ceil((b-(phi+deltaPhi)*r)/(2*r*Math.PI)-(-(phi+deltaPhi)-2*Math.PI*Math.floor((b-(phi+deltaPhi)*r)/(2*r*Math.PI)))/(2*Math.PI)-
					(Math.sqrt(((phi+deltaPhi)*(phi+deltaPhi)+2*(phi+deltaPhi)*Math.PI+4*(phi+deltaPhi)*Math.PI*Math.floor((b-(phi+deltaPhi)*r)/(2*r*Math.PI))+4*Math.PI*Math.PI
							*Math.floor((b-(phi+deltaPhi)*r)/(2*r*Math.PI))+4*Math.PI*Math.PI*Math.floor((b-(phi+deltaPhi)*r)/(2*r*Math.PI))*
							Math.floor((b-(phi+deltaPhi)*r)/(2*r*Math.PI)))))/(2*Math.PI));
			// this becomes imaginary if b is <7ish
		//dwu2for calculateN hyperbolic end--
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
			//In the Archimedean case the focal length fArch is given by the ratio of the focal length at radius 1 and the radius
			// hence fArch is given by the focalLength divided by the radius at any given point. 
			double fArch = focalLength1/r_n;
			if(alvarezWindingFocusing){
				return 
						(r_n-r)*(2*r*r*x+b*(r_n+r)*y)/(2*focalLength1*r*r);
						// (r_n-r)*(2*b*y*r_n+r*x*(r+r_n))/(2*focalLength1*r*r);
			}else {
				return -(((x/r)+b*y/r2)*(r-r_n))/fArch;
			}
		//dwu4for calculateXDerivative begin--
		case HYPERBOLIC:
		//System.out.println(powD);
				double powD = -1/focalLength1;
				return -(powD*y*(r-r_n)*(r-r_n)/(2*r*r))+((phi+deltaPhi)+2*n*Math.PI)*powD*(r-r_n)*((x/r)-((y*r_n*r_n)/(b*r*r)));
		//dwu4for calculateXDerivative end--
		case LOGARITHMIC:
		default:
			//In the logarithmic case the focal length, fLog, is constant and hence simply the focalLength.
			double fLog = focalLength1;
			if(alvarezWindingFocusing){
				return (-r*r*r*(3*x+b*y)+3*r*r_n*r_n*(x-b*y)+4*b*y*r_n*r_n*r_n)/(6*r_n*fLog*r*r);
			}else {
				return -(((a*b*Math.exp(b*(phi+n*2*Math.PI))*y)/r2 + x/r) * (-a*Math.exp(b*(phi+n*2*Math.PI)) + r))/fLog;
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
			//In the Archimedean case the focal length fArch is given by the ratio of the focal length at radius 1 and the radius
			// hence fArch is given by the focalLength divided by the radius at any given point. 
			double fArch = focalLength1/r_n;
			if(alvarezWindingFocusing){
				return 
						(r_n-r)*(2*r*r*y-b*(r_n+r)*x)/(2*focalLength1*r*r);
						// -(r_n-r)*(2*b*x*r_n-r*y*(r+r_n))/(2*focalLength1*r*r);
			}else {
				return -(((y/r)-b*x/r2)*(r-r_n))/fArch;
			}
		//dwu5for calculateYDerivative begin--
		case HYPERBOLIC:
			double powD = -1/focalLength1;
			return (powD*x*(r-r_n)*(r-r_n)/(2*r*r))+(phi+2*n*Math.PI)*powD*(r-r_n)*((y/r)+((x*r_n*r_n)/(b*r*r)));
		//dwu5for calculateYDerivative begin--
		case LOGARITHMIC:
		default:
			//In the logarithmic case the focal length, fLog, is constant and hence simply the focalLength.
			double fLog = focalLength1;
			if(alvarezWindingFocusing){
				return (r*r*r*(b*x-3*y)+3*r*r_n*r_n*(b*x+y)-4*b*x*r_n*r_n*r_n)/(6*fLog*r*r*r_n);
			}else {
				return -(((-a*b*Math.exp(b*(phi+n*2*Math.PI))*x)/r2 + y/r) * (-a*Math.exp(b*(phi+n*2*Math.PI)) + r))/fLog;
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