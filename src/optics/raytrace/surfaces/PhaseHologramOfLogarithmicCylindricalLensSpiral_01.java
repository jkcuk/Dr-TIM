package optics.raytrace.surfaces;

import java.util.ArrayList;

import math.MyMath;
import math.Vector2D;
import math.Vector3D;
import optics.raytrace.core.One2OneParametrisedObject;
import optics.raytrace.core.Orientation;

/**
 * A phase hologram of a cylindrical-lens spiral.
 * This is a cylindrical lens of focal length f, bent into a logarithmic spiral centred at surface coordinates (0, 0) and of the form r = a exp(b phi),
 * where r=sqrt(x^2+y^2) and phi=atan2(y,x) and x and y are the surface coordinates.
 * 
 * The associated SceneObject must be a ParametrisedObject as the getSurfaceCoordinates(Vector3D) method is used to calculate the coordinates on the surface.
 * 
 * @author johannes, Ivan
 */
public class PhaseHologramOfLogarithmicCylindricalLensSpiral_01 extends PhaseHologram
{
	private static final long serialVersionUID = 5846229655581148838L;

	/**
	 * the cylindrical lens's focal length;
	 * the cross-section of the cylindrical lens is Phi(t) = (pi t^2)(lambda f), where t is the transverse direction
	 */
	private double focalLength;
	
	/**
	 * the centre of the cylindrical lens follows the logarithmic spiral r = a exp(b phi)
	 */
	private double a;

	/**
	 * the centre of the cylindrical lens follows the logarithmic spiral r = a exp(b phi)
	 */
	private double b;
	
	/**
	 * the scene object this surface property is associated with
	 */
	private One2OneParametrisedObject sceneObject;
	
	//
	// constructors etc.
	//

	/**
	 * @param focalLength
	 * @param a
	 * @param b
	 * @param throughputCoefficient
	 * @param reflective
	 * @param shadowThrowing
	 */
	public PhaseHologramOfLogarithmicCylindricalLensSpiral_01(
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
		setFocalLength(focalLength);
		setA(a);
		setB(b);
		setSceneObject(sceneObject);
	}

	public PhaseHologramOfLogarithmicCylindricalLensSpiral_01(PhaseHologramOfLogarithmicCylindricalLensSpiral_01 original) {
		super(original);
		setFocalLength(original.getFocalLength());
		setA(original.getA());
		setB(original.getB());
		setSceneObject(original.getSceneObject());
	}
	
	@Override
	public PhaseHologramOfLogarithmicCylindricalLensSpiral_01 clone()
	{
		return new PhaseHologramOfLogarithmicCylindricalLensSpiral_01(this);
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
	}
	
	public One2OneParametrisedObject getSceneObject() {
		return sceneObject;
	}

	public void setSceneObject(One2OneParametrisedObject sceneObject) {
		this.sceneObject = sceneObject;
	}

	/**
	 * Calculate the distance r from the centre of the logarithmic spiral r = a Exp(b phi).
	 * Note that the values of phi are not restricted to the range [0, 2 pi].
	 * @param phi
	 * @return	r
	 */
	private double calculateSpiralDistanceFromCentre(double phi)
	{
		return a*Math.exp(b*phi);
	}
	
	/**
	 * The logarithmic spiral that describes the centre of the cylindrical lens is of the form <i>r</i> = <i>a</i> exp(<i>b Phi</i>),
	 * where <i>Phi</i> is an *unwrapped* azimuthal angle that can take on values from -infinity to +infinity.
	 * 
	 * Alternatively, we can write it in the form <i>r</i> = <i>a</i> exp(<i>b (phi + Delta phi)</i>), where <i>phi<i> is the azimuthal angle
	 * restricted (as normal) to some 2 pi range (e.g. -pi to pi), and <i>Delta phi</i> is an integer multiple of 2 pi.
	 * @param r
	 * @param phi
	 * @return
	 */
	private double calculateDeltaPhi(double r, double phi)
	{
		// The logarithmic spiral that describes the centre of the cylindrical lens is of the form r = a exp(b Phi), where Phi is an *unwrapped* azimuthal angle
		// that can take on values from -infinity to +infinity.
		// We can calculate the value of Phi for which the centre of the cylindrical lens is at distance from the centre r by solving the equation for the
		// logarithmic spiral,
		//   r = a exp(b Phi),
		// for Phi, which gives
		//   Phi = ln(r/a)/b.
		double Phi = Math.log(r/a)/b;
		
		// Phi lies between two values of Delta phi, namely between floor(Phi/(2 pi))*(2 pi) and floor(Phi/(2 pi) + 1)*(2 pi)
		double floorPhi = Math.floor(Phi/(2*Math.PI))*2*Math.PI;
		
		// find the correct one, which is the one for which the point with polar coordinates (r, phi) is closest to the logarithmic spiral.
		// phi is the azimuthal angle, somehow restricted (depending on how it is calculated, usually by Math.atan2, which restricts it to -pi to pi).

		// double modPhi = phi - Math.floor(phi/(2*Math.PI))*2*Math.PI;
		// System.out.println("phi="+phi+", modPhi="+modPhi);
		
		return 
			(MyMath.square(r-calculateSpiralDistanceFromCentre(phi+floorPhi)) < MyMath.square(r-calculateSpiralDistanceFromCentre(phi+floorPhi + 2*Math.PI)))
			?floorPhi
			:floorPhi+2*Math.PI;
		// return Math.floor(phiUnwrapped/(2*Math.PI))*2*Math.PI;
//		
//		double deltaPhi = -30*Math.PI;
//		double rMin = MyMath.square(r-calculateSpiralDistanceFromCentre(phi+deltaPhi));
//		do
//		{
//			double rMinNext = MyMath.square(r-calculateSpiralDistanceFromCentre(phi+deltaPhi+2*Math.PI));
//			if(rMinNext > rMin) break;
//			rMin = rMinNext;
//			deltaPhi += 2*Math.PI;
//		}
//		while (deltaPhi < 20*Math.PI);
//		
//		// System.out.println("PhaseHologramOfLogarithmicCylindricalLensSpiral:calculateSpiralDistanceFromCentre: deltaPhi="+deltaPhi+", phiUnwrapped="+phiUnwrapped);
//		return deltaPhi;
	}
	
	public double lensHeight(double r, double phi)
	{
		return MyMath.square(r-calculateSpiralDistanceFromCentre(phi+calculateDeltaPhi(r, phi)));
	}
	
	private double calculateXDerivative(double x, double y, double r, double r2, double phi, double deltaPhi)
	{
//		double r2 = x*x + y*y;
//		double r = Math.sqrt(r2);
//		double phi = Math.atan2(y, x);
		
		// calculated in Mathematica
		return 2*((a*b*Math.exp(b*(deltaPhi+phi))*y)/r2 + x/r) * (-a*Math.exp(b*(deltaPhi+phi)) + r);
	}

	private double calculateYDerivative(double x, double y, double r, double r2, double phi, double deltaPhi)
	{
//		double r2 = x*x + y*y;
//		double r = Math.sqrt(r2);
//		double phi = Math.atan2(y, x);
		
		// calculated in Mathematica
		return 2*((-a*b*Math.exp(b*(deltaPhi+phi))*x)/r2 + y/r) * (-a*Math.exp(b*(deltaPhi+phi)) + r);
	}

	@Override
	public Vector3D getTangentialDirectionComponentChangeTransmissive(Vector3D surfacePosition,
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
		
		double deltaPhi = calculateDeltaPhi(r, phi);
		double xDerivative = calculateXDerivative(x, y, r, r2, phi, deltaPhi);
		double yDerivative = calculateYDerivative(x, y, r, r2, phi, deltaPhi);
		
		ArrayList<Vector3D> xHatYHat = sceneObject.getSurfaceCoordinateAxes(surfacePosition);
		return Vector3D.sum(xHatYHat.get(0).getProductWith(xDerivative), xHatYHat.get(1).getProductWith(yDerivative)).getProductWith(-0.5/focalLength);
	}

	@Override
	public Vector3D getTangentialDirectionComponentChangeReflective(Orientation incidentLightRayOrientation,
			Vector3D surfacePosition, Vector3D surfaceNormal)
	{
		// TODO Not sure this makes sense completely...
		return getTangentialDirectionComponentChangeTransmissive(surfacePosition, surfaceNormal);
	}

}
