package optics.raytrace.surfaces;

import math.MyMath;
import math.Vector3D;
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
public class PhaseHologramOfLogarithmicCylindricalLensSpiral_old extends PhaseHologram
{
	private static final long serialVersionUID = 7546387042596973119L;

	private Vector3D centre;
	
	private Vector3D xHat;
	
	private Vector3D yHat;
	
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
	
	//
	// constructors etc.
	//

	/**
	 * @param centre
	 * @param xHat
	 * @param yHat
	 * @param focalLength
	 * @param a
	 * @param b
	 * @param throughputCoefficient
	 * @param reflective
	 * @param shadowThrowing
	 */
	public PhaseHologramOfLogarithmicCylindricalLensSpiral_old(
			Vector3D centre,
			Vector3D xHat,
			Vector3D yHat,
			double focalLength,
			double a,
			double b,
			double throughputCoefficient,
			boolean reflective,
			boolean shadowThrowing
		)
	{
		super(throughputCoefficient, reflective, shadowThrowing);
		setCentre(centre);
		setxHat(xHat);
		setyHat(yHat);
		setFocalLength(focalLength);
		setA(a);
		setB(b);
	}

	public PhaseHologramOfLogarithmicCylindricalLensSpiral_old(PhaseHologramOfLogarithmicCylindricalLensSpiral_old original) {
		super(original);
		setCentre(original.getCentre());
		setxHat(original.getxHat());
		setyHat(original.getyHat());
		setFocalLength(original.getFocalLength());
		setA(original.getA());
		setB(original.getB());
	}
	
	@Override
	public PhaseHologramOfLogarithmicCylindricalLensSpiral_old clone()
	{
		return new PhaseHologramOfLogarithmicCylindricalLensSpiral_old(this);
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

	public Vector3D getxHat() {
		return xHat;
	}

	public void setxHat(Vector3D xHat) {
		this.xHat = xHat.getNormalised();
	}

	public Vector3D getyHat() {
		return yHat;
	}

	public void setyHat(Vector3D yHat) {
		this.yHat = yHat.getNormalised();
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
	
	private double calculateDeltaPhi(double r, double phi)
	{
		double deltaPhi = -30*Math.PI;
		double rMin = MyMath.square(r-calculateSpiralDistanceFromCentre(phi+deltaPhi));
		do
		{
			double rMinNext = MyMath.square(r-calculateSpiralDistanceFromCentre(phi+deltaPhi+2*Math.PI));
			if(rMinNext > rMin) break;
			rMin = rMinNext;
			deltaPhi += 2*Math.PI;
		}
		while (deltaPhi < 20*Math.PI);
		
		return deltaPhi;
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
		// calculate the x and y coordinates of the position
		Vector3D rVector = Vector3D.difference(surfacePosition, centre);
		double x = Vector3D.scalarProduct(rVector, xHat);
		double y = Vector3D.scalarProduct(rVector, yHat);
		
		// ... and r and phi
		double r2 = x*x + y*y;
		double r = Math.sqrt(r2);
		double phi = Math.atan2(y, x);
		
		double deltaPhi = calculateDeltaPhi(r, phi);
		double xDerivative = calculateXDerivative(x, y, r, r2, phi, deltaPhi);
		double yDerivative = calculateYDerivative(x, y, r, r2, phi, deltaPhi);
		
		return Vector3D.sum(xHat.getProductWith(xDerivative), yHat.getProductWith(yDerivative)).getProductWith(1/focalLength);
	}

	@Override
	public Vector3D getTangentialDirectionComponentChangeReflective(Orientation incidentLightRayOrientation,
			Vector3D surfacePosition, Vector3D surfaceNormal)
	{
		// TODO Not sure this makes sense completely...
		return getTangentialDirectionComponentChangeTransmissive(surfacePosition, surfaceNormal);
	}

}
