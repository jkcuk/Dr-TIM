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
public class PhaseHologramOfLogarithmicCylindricalLensSpiral_02 extends PhaseHologram
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
	 * @param focalLength
	 * @param a
	 * @param b
	 * @param throughputCoefficient
	 * @param reflective
	 * @param shadowThrowing
	 */
	public PhaseHologramOfLogarithmicCylindricalLensSpiral_02(
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

	public PhaseHologramOfLogarithmicCylindricalLensSpiral_02(PhaseHologramOfLogarithmicCylindricalLensSpiral_02 original) {
		super(original);
		setFocalLength(original.getFocalLength());
		setA(original.getA());
		setB(original.getB());
		setSceneObject(original.getSceneObject());
	}
	
	@Override
	public PhaseHologramOfLogarithmicCylindricalLensSpiral_02 clone()
	{
		return new PhaseHologramOfLogarithmicCylindricalLensSpiral_02(this);
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
		b2pi = 2*b*Math.PI;
		nHalf = Math.log(0.5*(1. + Math.exp(b2pi)))/b2pi;
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
		return a*Math.exp(b*(phi+n*2*Math.PI));
	}
	
	public double calculateN(double r, double phi)
	{
		return Math.ceil(-((b*phi - Math.log(r/a))/b2pi) - nHalf);
	}
	
	public double lensHeight(double r, double phi)
	{
		return MyMath.square(r-calculateSpiralDistanceFromCentre(phi, calculateN(r, phi)));
		// return MyMath.square(r-calculateSpiralDistanceFromCentre(phi+calculateDeltaPhi(r, phi)));
	}
	
	private double calculateXDerivative(double x, double y, double r, double r2, double phi, double n)
	{
		// calculated in Mathematica
		return 2*((a*b*Math.exp(b*(phi+n*2*Math.PI))*y)/r2 + x/r) * (-a*Math.exp(b*(phi+n*2*Math.PI)) + r);
	}

	private double calculateYDerivative(double x, double y, double r, double r2, double phi, double n)
	{
		// calculated in Mathematica
		return 2*((-a*b*Math.exp(b*(phi+n*2*Math.PI))*x)/r2 + y/r) * (-a*Math.exp(b*(phi+n*2*Math.PI)) + r);
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
		
		double n = calculateN(r, phi);
		double xDerivative = calculateXDerivative(x, y, r, r2, phi, n);
		double yDerivative = calculateYDerivative(x, y, r, r2, phi, n);
		
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
