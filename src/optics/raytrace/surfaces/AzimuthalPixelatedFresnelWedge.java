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
 * @author Maik based on johannes's RotationallySymmetricPhaseHologram code
 *
 */
public class AzimuthalPixelatedFresnelWedge extends PhaseHologram
{

	private static final long serialVersionUID = -3831089866418480597L;

	/**
	 * the phase hologram's centre, which should lie on the surface
	 */
	private Vector3D centre;
	
	/**
	 * the 'pixel' span vectors of the hologram.
	 */
	private Vector3D latticeSpanVector1, latticeSpanVector2;
	
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

	
	//
	// constructors etc.
	//

	/**
	 * @param centre
	 * @param latticeSpanVector1
	 * @param latticeSpanVector2
	 * @param b
	 * @param c
	 * @param s
	 * @param t
	 * @param throughputCoefficient
	 * @param reflective
	 * @param shadowThrowing
	 */
	public AzimuthalPixelatedFresnelWedge(
			Vector3D centre,
			Vector3D latticeSpanVector1,
			Vector3D latticeSpanVector2,
			double b,
			double c,
			double s,
			double t, 
			double throughputCoefficient,
			boolean reflective,
			boolean shadowThrowing
		)
	{
		super(throughputCoefficient, reflective, shadowThrowing);
		this.centre = centre;
		this.latticeSpanVector1 = latticeSpanVector1;
		this.latticeSpanVector2 = latticeSpanVector2;
		this.b = b;
		this.c = c;
		this.s = s;
		this.t = t;
	}

	/**
	 * @param original
	 */
	public AzimuthalPixelatedFresnelWedge(AzimuthalPixelatedFresnelWedge original)
	{
		this(
				original.getCentre(),
				original.getLatticeSpanVector1(),
				original.getLatticeSpanVector2(),
				original.getB(),
				original.getC(),
				original.getS(),
				original.getT(),
				original.getTransmissionCoefficient(),
				original.isReflective(),
				original.isShadowThrowing()
			);
	}
	
	@Override
	public AzimuthalPixelatedFresnelWedge clone()
	{
		return new AzimuthalPixelatedFresnelWedge(this);
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

	public Vector3D getLatticeSpanVector1() {
		return latticeSpanVector1;
	}

	public void setLatticeSpanVector1(Vector3D latticeSpanVector1) {
		this.latticeSpanVector1 = latticeSpanVector1;
	}

	public Vector3D getLatticeSpanVector2() {
		return latticeSpanVector2;
	}

	public void setLatticeSpanVector2(Vector3D latticeSpanVector2) {
		this.latticeSpanVector2 = latticeSpanVector2;
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


	
	//
	// PhaseHologram methods
	// 
	
	@Override
	public Vector3D getTangentialDirectionComponentChangeTransmissive(Vector3D surfacePosition,
			Vector3D surfaceNormal)
	{	
		//Calculating which 'pixel' the light ray is in. 
		Vector3D pointOnLattice = Vector3D.sum(centre,latticeSpanVector1.getProductWith(0.5),latticeSpanVector2.getProductWith(0.5));
		double latticePositionSpanVector1 = Vector3D.scalarProduct(Vector3D.difference(surfacePosition, pointOnLattice), (latticeSpanVector1.getNormalised())) / latticeSpanVector1.getLength();
		double latticePositionSpanVector2 = Vector3D.scalarProduct(Vector3D.difference(surfacePosition, pointOnLattice), (latticeSpanVector2.getNormalised())) / latticeSpanVector2.getLength();
		//index for the pixel
		int indexlatticePositionSpanVector1 = (int)(Math.floor(latticePositionSpanVector1 +1));
		int indexlatticePositionSpanVector2 = (int)(Math.floor(latticePositionSpanVector2 +1));
		//Centre of the pixel which the light ray hits.
		Vector3D surfacePixelPosition = Vector3D.sum(centre, latticeSpanVector1.getProductWith(indexlatticePositionSpanVector1), latticeSpanVector2.getProductWith(indexlatticePositionSpanVector2));
		
		// calculate the distance from the centre, and construct a unit vector in the radial direction (making sure it is perpendicular to the surface normal)
		Vector3D rVec = Vector3D.difference(surfacePixelPosition, centre).getPartPerpendicularTo(surfaceNormal);
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
