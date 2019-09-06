package optics.raytrace.surfaces;

import math.Vector3D;
import optics.raytrace.core.Orientation;

/**
 * A phase hologram of a radial lenticular array, i.e. an array of cylindrical lenses whose cylinder axes are aligned in the radial direction.
 * This component is described by its <i>centre</i>;
 * the focal length, <i>f</i>, of each cylindrical lens;
 * the number, <i>n</i>, of cylindrical lenses in the array (each cylindrical lens then covers an angle 360 degrees / <i>n</i>);
 * and the direction of the cylinder axis of the 0th cylindrical lens, <i>d0</i>.
 * @author johannes
 *
 */
public class PhaseHologramOfRadialLenticularArray extends PhaseHologram
{
	private static final long serialVersionUID = 8817860810079026713L;

	/**
	 * the centre of the radial array, which should lie on the surface
	 */
	private Vector3D centre;
	
	/**
	 * the focal length of each cylindrical lens
	 */
	private double f;

	/**
	 * the number of cylindrical lenses that form the array; each cylindrical lens then covers an angle 360 degrees / <i>n</i>
	 */
	private double n;

	/**
	 * the direction of the cylinder axis of the 0th cylindrical lens
	 */
	private Vector3D d0;


	
	//
	// constructors etc.
	//

	/**
	 * @param centre
	 * @param f
	 * @param n
	 * @param d0
	 * @param throughputCoefficient
	 * @param reflective
	 * @param shadowThrowing
	 */
	public PhaseHologramOfRadialLenticularArray(Vector3D centre, double f, double n, Vector3D d0, double throughputCoefficient, boolean reflective, boolean shadowThrowing)
	{
		super(throughputCoefficient, reflective, shadowThrowing);
		this.centre = centre;
		this.f = f;
		this.n = n;
		this.d0 = d0;
	}

	/**
	 * @param original
	 */
	public PhaseHologramOfRadialLenticularArray(PhaseHologramOfRadialLenticularArray original)
	{
		this(
				original.getCentre(),
				original.getF(),
				original.getN(),
				original.getD0(),
				original.getTransmissionCoefficient(),
				original.isReflective(),
				original.isShadowThrowing()
			);
	}
	
	@Override
	public PhaseHologramOfRadialLenticularArray clone()
	{
		return new PhaseHologramOfRadialLenticularArray(this);
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

	public double getF() {
		return f;
	}

	public void setF(double f) {
		this.f = f;
	}

	public double getN() {
		return n;
	}

	public void setN(double n) {
		this.n = n;
	}

	public Vector3D getD0() {
		return d0;
	}

	public void setD0(Vector3D d0) {
		this.d0 = d0;
	}


	
	//
	// PhaseHologram methods
	// 
	
	@Override
	public Vector3D getTangentialDirectionComponentChangeTransmissive(Vector3D surfacePosition,
			Vector3D surfaceNormal)
	{	
		// construct a Cartesian coordinate system such that the cylinder axis of the 0th cylindrical lens lies in the x direction
		Vector3D xHat = d0.getPartPerpendicularTo(surfaceNormal).getNormalised();
		Vector3D yHat = Vector3D.crossProduct(surfaceNormal, xHat).getNormalised();
		
		// calculate the x and y coordinates of the surface position
		Vector3D centreToSurfacePosition = Vector3D.difference(surfacePosition, centre).getPartPerpendicularTo(surfaceNormal);
		double x = Vector3D.scalarProduct(centreToSurfacePosition, xHat);
		double y = Vector3D.scalarProduct(centreToSurfacePosition, yHat);
		
		// calculate the polar coordinates
		// double r = Math.sqrt(x*x + y*y);
		double phi = Math.atan2(y, x);
		
		// calculate the number, i, of the cylindrical lens on which the surface position lies...
		double dPhi = 2.*Math.PI/n;
		double i = Math.floor(phi/dPhi+0.5);
		// ... and the corresponding angle phiAxis of the cylinder axis of the ith cylindrical lens
		double phiAxis = i*dPhi;
		
		// calculate the direction of the phase gradient of this cylindrical lens
		Vector3D phaseGradientDirection = Vector3D.sum(
				xHat.getProductWith(Math.cos(phiAxis+0.5*Math.PI)),
				yHat.getProductWith(Math.sin(phiAxis+0.5*Math.PI))
			);
		
		// A distance r from the lens axis, the lens phase is given by Phi(r) = -(pi r^2)(lambda f).
		// This means that the phase gradient there is in the direction perpendicular to the axis and of magnitude dPhi/dr = -2 r pi/(lambda f).
		// This method needs to return the phase gradient divided by 2 pi/lambda, i.e. r / f.
		
		// Vector3D.difference(surfacePosition, centre).getPartPerpendicularTo(surfaceNormal) gives a vector that is tangential to the surface
		// and of length r
		return Vector3D.difference(surfacePosition, centre).getPartParallelTo(phaseGradientDirection).getProductWith(-1/f);
	}

	@Override
	public Vector3D getTangentialDirectionComponentChangeReflective(Orientation incidentLightRayOrientation,
			Vector3D surfacePosition, Vector3D surfaceNormal)
	{
		// same change in the tangential component as in transmission
		return getTangentialDirectionComponentChangeTransmissive(surfacePosition, surfaceNormal);
	}

}
