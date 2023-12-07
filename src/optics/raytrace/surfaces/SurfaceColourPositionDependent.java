package optics.raytrace.surfaces;

import math.Vector3D;
import optics.DoubleColour;
import optics.raytrace.core.*;


/**
 * @author maik
 * A SurfaceProperty that represents a different colour depending on the position where a light ray hits it.
 * 
 * By default, the colour cycles through a periodic continuum of colours, with the period given by the variable <period>.
 * Override getColour(position) to customise.
 */
public class SurfaceColourPositionDependent extends SurfacePropertyPrimitive
{
	private static final long serialVersionUID = 6083251020832647576L;

	private double period;
	
	private boolean hasIndependentDirection;
	
	private Vector3D independentDirection;
	
	private Vector3D centre;
	
	private boolean isLightSourceIndependent;
	
	/**
	 * Override this!
	 * @param distance
	 * @return
	 */
	public DoubleColour getColour(double distance)
	{
//		double h = distance / period - Math.floor(time/period);
//		
//		return DoubleColour.getColourFromHSL(h, 1, 1);
		
		double phase = 2*Math.PI*(distance) / period;
		return new DoubleColour(
				0.5+0.5*Math.sin(phase),	// R
				0.5+0.5*Math.sin(phase + 1./3.*2.*Math.PI),	// G
				0.5+0.5*Math.sin(phase + 2./3.*2.*Math.PI)	// B
			);
	}
	
	public SurfaceColourPositionDependent(double period, Vector3D centre, Vector3D independentDirection,	
	boolean hasIndependentDirection, boolean isLightSourceIndependent, boolean shadowThrowing)
	{
		super(0, shadowThrowing);
		setPeriod(period);
		setCentre(centre);
		setIndependentDirection(independentDirection);
		setHasIndependentDirection(hasIndependentDirection);
		setLightSourceIndependent(isLightSourceIndependent);
	}

	public SurfaceColourPositionDependent(double period, Vector3D centre, boolean isLightSourceIndependent, boolean shadowThrowing)
	{
		this(period, centre, Vector3D.X, false, false, shadowThrowing);
	}
	
	public double getPeriod() {
		return period;
	}

	public void setPeriod(double period) {
		this.period = period;
	}

	public boolean isHasIndependentDirection() {
		return hasIndependentDirection;
	}

	public void setHasIndependentDirection(boolean hasIndependentDirection) {
		this.hasIndependentDirection = hasIndependentDirection;
	}

	public Vector3D getIndependentDirection() {
		return independentDirection;
	}

	public void setIndependentDirection(Vector3D independentDirection) {
		this.independentDirection = independentDirection;
	}

	public Vector3D getCentre() {
		return centre;
	}

	public void setCentre(Vector3D centre) {
		this.centre = centre;
	}

	public boolean isLightSourceIndependent() {
		return isLightSourceIndependent;
	}

	public void setLightSourceIndependent(boolean isLightSourceIndependent) {
		this.isLightSourceIndependent = isLightSourceIndependent;
	}

	@Override
	public DoubleColour getColour(Ray r, RaySceneObjectIntersection i, SceneObject scene, LightSource l, int traceLevel, RaytraceExceptionHandler raytraceExceptionHandler)
	{
		double distance;
		if(hasIndependentDirection) {
			distance = Vector3D.getDistance(i.p.getPartPerpendicularTo(independentDirection), centre);
		}else {
			distance = Vector3D.getDistance(i.p, centre);
		}
		DoubleColour c = getColour(distance);
		
		if(isLightSourceIndependent) return c;
		else
		{
			// the surface colour is not light-source independent
			// handle through the getColour method of SurfaceColour class
			return new SurfaceColour(c, DoubleColour.WHITE, shadowThrowing).getColour(r, i, scene, l, traceLevel, raytraceExceptionHandler);
		}
	}

	@Override
	public String toString()
	{
		return "<SurfaceColourPositionDependent>\n\n</SurfaceColourPositionDependent>\n";
	}

	@Override
	public SurfacePropertyPrimitive clone() {
		return new SurfaceColourPositionDependent(getPeriod(), getCentre(), getIndependentDirection(), isHasIndependentDirection(), isLightSourceIndependent(), isShadowThrowing());
	}
}

