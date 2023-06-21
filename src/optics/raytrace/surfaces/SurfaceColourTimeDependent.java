package optics.raytrace.surfaces;

import optics.DoubleColour;
import optics.raytrace.core.*;


/**
 * @author johannes
 * A SurfaceProperty that represents a different colour depending on the time when a light ray hits it.
 * 
 * By default, the colour cycles through a periodic continuum of colours, with the period given by the variable <period>.
 * Override getColour(time) to customise.
 */
public class SurfaceColourTimeDependent extends SurfacePropertyPrimitive
{
	private static final long serialVersionUID = 1223901449632490054L;

	private double period;
	
	private boolean isLightSourceIndependent;
	
	/**
	 * Override this!
	 * @param time
	 * @return
	 */
	public DoubleColour getColour(double time)
	{
//		double h = time / period - Math.floor(time/period);
//		
//		return DoubleColour.getColourFromHSL(h, 1, 1);
		
		double phase = 2*Math.PI*time / period;
		return new DoubleColour(
				0.5+0.5*Math.sin(phase),	// R
				0.5+0.5*Math.sin(phase + 1./3.*2.*Math.PI),	// G
				0.5+0.5*Math.sin(phase + 2./3.*2.*Math.PI)	// B
			);
	}
	
	public SurfaceColourTimeDependent(double period, boolean isLightSourceIndependent, boolean shadowThrowing)
	{
		super(0, shadowThrowing);
		setPeriod(period);
		setLightSourceIndependent(isLightSourceIndependent);
	}

	public SurfaceColourTimeDependent(double period, boolean shadowThrowing)
	{
		this(period, true, shadowThrowing);
	}
	
	public double getPeriod() {
		return period;
	}

	public void setPeriod(double period) {
		this.period = period;
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
		DoubleColour c = getColour(i.t);
		
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
		return "<SurfaceColourTimeDependent>\n\n</SurfaceColourTimeDependent>\n";
	}

	@Override
	public SurfacePropertyPrimitive clone() {
		return new SurfaceColourTimeDependent(getPeriod(), isLightSourceIndependent(), isShadowThrowing());
	}
}

