package optics.raytrace.surfaces;

import math.Vector3D;
import optics.raytrace.core.Orientation;

/**
 * A phase hologram of a class of rotationally symmetric phase holograms.
 * The phase gradient is specified separately in the azimuthal and radial direction.
 * It is defined to be proportional to <i>k</i>=2 &pi;/&lambda;, as follows:
 * <ul>
 * <li>The first part of the azimuthal phase gradient is of the form d<i>&Phi;</i>/d<i>&phi;</i> = <i>k</i>*<i>b</i>*<i>r</i>^<i>c</i>,
 * where &phi; is the azimuthal angle.</li>
 * <li>The radial phase gradient is of the form d<i>&Phi;</i>/dr = <i>k</i>*<i>s</i>*<i>r</i>^<i>t</i>.</li>
 * </ul>
 * 
 * Note that an azimuthal phase gradient in general requires an associated radial phase gradient.
 * This gets simulated if <i>simulateHonestly</i>=true, which requires the phase hologram to be somehow "pixellated",
 * here into <i>n</i> circular sectors.
 * Where neighbouring sectors meet, the phase is discontinuous.
 *
 * Special cases:
 * <ul>
 * <li>For <i>b</i>=0 and <i>t</i>=1, <i>s</i>=1/<i>f</i>, this is a lens of focal length <i>f</i>
 * (which has a phase profile of the form <i>Phi</i> = (&pi; <i>r</i>^2)/(&lambda; <i>f</i>),
 * so d<i>&Phi;</i>/d<i>r</i> = (2 &pi;)/(&lambda; <i>f</i>) <i>r</i> = <i>k</i> (1/<i>f</i>) <i>r</i>.</li>
 * 
 * <li>For <i>s</i>=0 and <i>c</i>=0, <i>b</i>=<i>l</i>, this is a charge-<i>l</i> vortex phase mask
 * (with d<i>Phi</i>/d<i>&phi;</i> = <i>l</i>)</li>
 * 
 * <li>For <i>s</i>=0 and <i>c</i>=2, it corresponds to an idealised moiré rotator.</li>
 * </ul>
 * @author johannes
 *
 */
public class RotationallySymmetricPhaseHologram extends PhaseHologram
{
	private static final long serialVersionUID = 7613557477195682216L;

	/**
	 * the phase hologram's centre, which should lie on the surface
	 */
	private Vector3D centre;
	
	/**
	 * The parameter <i>b</i>, which (together with <i>c</i>) determines the part of the azimuthal phase gradient proportional to the azimuthal distance.
	 * The azimuthal phase gradient is of the form d<i>&Phi;</i>/d<i>&phi;</i> = <i>k</i>*<i>b</i>*<i>r</i><sup><i>c</i></sup>,
	 * where <i>k</i>=(2 &pi;/&lambda;)
	 */
	private double b;

	/**
	 * The parameter <i>c</i>, which (together with <i>b</i>) determines the part of the azimuthal phase gradient proportional to the azimuthal distance.
	 * The azimuthal phase gradient is of the form d<i>&Phi;</i>/d<i>&phi;</i> = <i>k</i>*<i>b</i>*<i>r</i><sup><i>c</i></sup>,
	 * where <i>k</i>=(2 &pi;/&lambda;)
	 */
	private double c;

	/**
	 * The parameter <i>s</i>, which (together with <i>t</i>) determines the radial phase gradient.
	 * The radial phase gradient is of the form d<i>&Phi;</i>/d<i>r</i> = <i>k</i>*<i>s</i>*<i>r</i><sup><i>t</i></sup>, where <i>k</i>=(2 &pi;/&lambda;).
	 */
	private double s;

	/**
	 * The parameter <i>t</i>, which (together with <i>s</i>) determines the radial phase gradient.
	 * The radial phase gradient is of the form d<i>&Phi;</i>/d<i>r</i> = <i>k</i>*<i>s</i>*<i>r</i><sup><i>t</i></sup>, where <i>k</i>=(2 &pi;/&lambda;).
	 */
	private double t;
	
	/**
	 * if true, simulate the radial phase gradient associated with the radially-varying azimuthal phase gradient
	 */
	private boolean simulateHonestly;

	/**
	 * the number of circular sectors; each sector covers an angle 360 degrees / <i>n</i>
	 */
	private int n;

	/**
	 * the direction of the bisector of the 0th segment
	 */
	private Vector3D d0;

	
	//
	// constructors etc.
	//

	/**
	 * @param centre
	 * @param b
	 * @param c
	 * @param s
	 * @param t
	 * @param simulateHonestly
	 * @param n
	 * @param d0
	 * @param throughputCoefficient
	 * @param reflective
	 * @param shadowThrowing
	 */
	public RotationallySymmetricPhaseHologram(
			Vector3D centre,
			double b,
			double c,
			double s,
			double t, 
			boolean simulateHonestly,
			int n,
			Vector3D d0,
			double throughputCoefficient,
			boolean reflective,
			boolean shadowThrowing
		)
	{
		super(throughputCoefficient, reflective, shadowThrowing);
		this.centre = centre;
		this.b = b;
		this.c = c;
		this.s = s;
		this.t = t;
		this.simulateHonestly = simulateHonestly;
		this.n = n;
		this.d0 = d0;
	}

	/**
	 * @param original
	 */
	public RotationallySymmetricPhaseHologram(RotationallySymmetricPhaseHologram original)
	{
		this(
				original.getCentre(),
				original.getB(),
				original.getC(),
				original.getS(),
				original.getT(),
				original.isSimulateHonestly(),
				original.getN(),
				original.getD0(),
				original.getTransmissionCoefficient(),
				original.isReflective(),
				original.isShadowThrowing()
			);
	}
	
	@Override
	public RotationallySymmetricPhaseHologram clone()
	{
		return new RotationallySymmetricPhaseHologram(this);
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

	public double getB() {
		return b;
	}

	public void setB(double b) {
		this.b = b;
	}

	public double getC() {
		return c;
	}

	public void setC(double c) {
		this.c = c;
	}

	public double getS() {
		return s;
	}

	public void setS(double s) {
		this.s = s;
	}

	public double getT() {
		return t;
	}

	public void setT(double t) {
		this.t = t;
	}

	public boolean isSimulateHonestly() {
		return simulateHonestly;
	}

	public void setSimulateHonestly(boolean simulateHonestly) {
		this.simulateHonestly = simulateHonestly;
	}

	public int getN() {
		return n;
	}

	public void setN(int n) {
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
		// calculate the distance from the centre, and construct a unit vector in the radial direction (making sure it is perpendicular to the surface normal)
		Vector3D rVec = Vector3D.difference(surfacePosition, centre).getPartPerpendicularTo(surfaceNormal);
		double r = rVec.getLength();
		Vector3D rHat = rVec.getNormalised();
		
		// initialise the direction-component change
		Vector3D tangentialDirectionComponentChange = new Vector3D(0, 0, 0);
		
		if(b != 0)
		{
			// there is a change in the azimuthal direction component
			
			// construct a unit vector in the azimuthal direction
			Vector3D phiHat = Vector3D.crossProduct(surfaceNormal, rHat).getNormalised();
			
			tangentialDirectionComponentChange = Vector3D.sum(
					tangentialDirectionComponentChange, 
					phiHat.getProductWith(b*Math.pow(r,c-1))
				);

			if(simulateHonestly)
			{
				// take into account the concomitant radial light-ray-direction change also

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
				double dPhi = 2.*Math.PI/n;	// angular width of each segment
				double i = Math.floor(phi/dPhi+0.5);
				// ... and the corresponding angle deltaPhi relative to the cylinder axis of the ith cylindrical lens
				double deltaPhi = phi - i*dPhi;

				tangentialDirectionComponentChange = Vector3D.sum(
						tangentialDirectionComponentChange, 
						rHat.getProductWith(b*deltaPhi*Math.pow(r,c-1))
					);
			}
		}
		
		if(s != 0)
		{
			// there is a change in the radial direction component
			
			tangentialDirectionComponentChange = Vector3D.sum(tangentialDirectionComponentChange, rHat.getProductWith(s*Math.pow(r,t)));
		}
		
		return tangentialDirectionComponentChange;
	}

	@Override
	public Vector3D getTangentialDirectionComponentChangeReflective(Orientation incidentLightRayOrientation,
			Vector3D surfacePosition, Vector3D surfaceNormal)
	{
		// same change in the tangential component as in transmission
		return getTangentialDirectionComponentChangeTransmissive(surfacePosition, surfaceNormal);
	}

}
