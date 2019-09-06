package optics.raytrace.surfaces;

import math.Vector3D;
import optics.raytrace.core.Orientation;

/**
 * A phase hologram of a class of rotationally symmetric phase holograms.
 * The phase gradient is specified separately in the azimuthal and radial direction.
 * It is defined to be proportional to <i>k</i>=2 &pi;/&lambda;, as follows:
 * <ul>
 * <li>The first part of the azimuthal phase gradient is of the form d<i>&Phi;</i>/d<i>a</i> = <i>k</i>*<i>b</i>*<i>r</i>^<i>c</i>,
 * where <i>a</i> = <i>r</i> &phi; is the coordinate in the azimuthal direction.</li>
 * <li>The second part of the azimuthal phase gradient is of the form d<i>&Phi;</i>/d<i>&phi;</i> = <i>k</i>*<i>g</i>*<i>r</i>^<i>h</i>.</li>
 * <li>The radial phase gradient is of the form d<i>&Phi;</i>/dr = <i>k</i>*<i>s</i>*<i>r</i>^<i>t</i>.</li>
 * </ul>
 *
 * Special cases:
 * <ul>
 * <li>For <i>b</i>=<i>g</i>=0 and <i>t</i>=1, <i>s</i>=1/<i>f</i>, this is a lens of focal length <i>f</i>
 * (which has a phase profile of the form <i>Phi</i> = (&pi; <i>r</i>^2)/(&lambda; <i>f</i>),
 * so d<i>&Phi;</i>/d<i>r</i> = (2 &pi;)/(&lambda; <i>f</i>) <i>r</i> = <i>k</i> (1/<i>f</i>) <i>r</i>.</li>
 * <li>For <i>a</i>=<i>s</i>=0 and <i>c</i>=-1, this is a spiral phase plate
 * (with 2 &pi; <i>r</i> d<i>Phi</i>/d<i>a</i> = <i>l</i> 2 &pi;, i.e. d<i>Phi</i>/d<i>a</i> = <i>l</i>/<i>r</i> = <i>b</i> <i>r</i>^<i>c</i> with <i>c</i> = -1 and <i>b</i> = <i>l</i>,
 * it is a charge-<i>l</i> vortex phase mask)</li>
 * <li>For <i>s</i>=0 and <i>c</i>=1, it corresponds to an idealised moiré rotator.</li>
 * </ul>
 * @author johannes
 *
 */
public class RotationallySymmetricPhaseHologram_old extends PhaseHologram
{
	private static final long serialVersionUID = 7613557477195682216L;

	/**
	 * the phase hologram's centre, which should lie on the surface
	 */
	private Vector3D centre;
	
	/**
	 * The parameter <i>b</i>, which (together with <i>c</i>) determines the part of the azimuthal phase gradient proportional to the azimuthal distance.
	 * The azimuthal phase gradient is of the form d<i>&Phi;</i>/d<i>a</i> = <i>k</i>*<i>b</i>*<i>r</i><sup><i>c</i></sup>,
	 * where <i>a</i> is the component in the azimuthal direction and <i>k</i>=(2 &pi;/&lambda;)
	 */
	private double b;

	/**
	 * The parameter <i>c</i>, which (together with <i>b</i>) determines the part of the azimuthal phase gradient proportional to the azimuthal distance.
	 * The azimuthal phase gradient is of the form d<i>&Phi;</i>/d<i>a</i> = <i>k</i>*<i>b</i>*<i>r</i><sup><i>c</i></sup>,
	 * where <i>a</i> is the component in the azimuthal direction and <i>k</i>=(2 &pi;/&lambda;)
	 */
	private double c;

	/**
	 * The parameter <i>g</i>, which (together with <i>h</i>) determines the part of the azimuthal phase gradient proportional to the azimuthal angle.
	 * The azimuthal phase gradient is of the form d<i>&Phi;</i>/d<i>&phi;</i> = <i>k</i>*<i>g</i>*<i>r</i><sup><i>h</i></sup>.
	 */
	private double g;

	/**
	 * The parameter <i>h</i>, which (together with <i>g</i>) determines the part of the azimuthal phase gradient proportional to the azimuthal angle.
	 * The azimuthal phase gradient is of the form d<i>&Phi;</i>/d<i>&phi;</i> = <i>k</i>*<i>g</i>*<i>r</i><sup><i>h</i></sup>.
	 */
	private double h;

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

	
	//
	// constructors etc.
	//

	/**
	 * @param centre
	 * @param b
	 * @param c
	 * @param g
	 * @param h
	 * @param s
	 * @param t
	 * @param throughputCoefficient
	 * @param reflective
	 * @param shadowThrowing
	 */
	public RotationallySymmetricPhaseHologram_old(Vector3D centre, double b, double c, double g, double h, double s, double t, double throughputCoefficient, boolean reflective, boolean shadowThrowing)
	{
		super(throughputCoefficient, reflective, shadowThrowing);
		this.centre = centre;
		this.b = b;
		this.c = c;
		this.g = g;
		this.h = h;
		this.s = s;
		this.t = t;
	}

	/**
	 * @param original
	 */
	public RotationallySymmetricPhaseHologram_old(RotationallySymmetricPhaseHologram_old original)
	{
		this(
				original.getCentre(),
				original.getB(),
				original.getC(),
				original.getG(),
				original.getH(),
				original.getS(),
				original.getT(),
				original.getTransmissionCoefficient(),
				original.isReflective(),
				original.isShadowThrowing()
			);
	}
	
	@Override
	public RotationallySymmetricPhaseHologram_old clone()
	{
		return new RotationallySymmetricPhaseHologram_old(this);
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

	public double getG() {
		return g;
	}

	public void setG(double g) {
		this.g = g;
	}

	public double getH() {
		return h;
	}

	public void setH(double h) {
		this.h = h;
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
			
			tangentialDirectionComponentChange = Vector3D.sum(tangentialDirectionComponentChange, phiHat.getProductWith(b*Math.pow(r,c)));
		}
		
		if(g != 0)
		{
			// there is a change in the azimuthal direction component
			
			// construct a vector in the azimuthal direction whose length is given by r
			Vector3D phiR = Vector3D.crossProduct(surfaceNormal, rHat).getWithLength(r);
			
			tangentialDirectionComponentChange = Vector3D.sum(tangentialDirectionComponentChange, phiR.getProductWith(g*Math.pow(r,h)));
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
