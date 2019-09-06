package optics.raytrace.surfaces;

import optics.DoubleColour;
import optics.raytrace.core.LightSource;
import optics.raytrace.core.Ray;
import optics.raytrace.core.RaySceneObjectIntersection;
import optics.raytrace.core.RaytraceExceptionHandler;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.SurfaceProperty;
import optics.raytrace.core.SurfacePropertyWithControllableShadow;

public class Checked extends SurfaceProperty implements SurfacePropertyWithControllableShadow
{
	private static final long serialVersionUID = 403540305599374189L;

	private DoubleColour colour1, colour2;
	double checkerWidth = 5; // default value
	
	// does this surface throw a shadow?
	private boolean shadowThrowing = true;

	public Checked (DoubleColour colour1, DoubleColour colour2, double checkerWidth, boolean shadowThrowing)
	{
		super();
		this.colour1=colour1;
		this.colour2=colour2;
		this.checkerWidth = checkerWidth;
		this.shadowThrowing = shadowThrowing;
	}
	
	public Checked(Checked original)
	{
		super();
		setColour1(original.getColour1());
		setColour2(original.getColour2());
		setCheckerWidth(original.getCheckerWidth());
		setShadowThrowing(original.isShadowThrowing());
	}
	
	@Override
	public Checked clone()
	{
		return new Checked(this);
	}

	public DoubleColour getColour1() {
		return colour1;
	}

	public void setColour1(DoubleColour colour1) {
		this.colour1 = colour1;
	}

	public DoubleColour getColour2() {
		return colour2;
	}

	public void setColour2(DoubleColour colour2) {
		this.colour2 = colour2;
	}

	public double getCheckerWidth() {
		return checkerWidth;
	}

	public void setCheckerWidth(double checkerWidth) {
		this.checkerWidth = checkerWidth;
	}

	/**
	 * @return true if the scene object throws a shadow, false if it doesn't
	 */
	@Override
	public boolean isShadowThrowing() {
		return shadowThrowing;
	}

	/**
	 * @param shadowThrowing	true if the scene object is supposed to throw a shadow, false if it isn't
	 */
	@Override
	public void setShadowThrowing(boolean shadowThrowing) {
		this.shadowThrowing = shadowThrowing;
	}

	@Override
	public DoubleColour getColour(Ray r, RaySceneObjectIntersection i, SceneObject scene, LightSource l, int traceLevel, RaytraceExceptionHandler raytraceExceptionHandler)
	{
		double checkerWidth2 = checkerWidth/2;
		
		// work out x mod WIDTH, etc then check if it is big or small for x, y, z
		boolean x = (Math.abs(i.p.x) % checkerWidth < checkerWidth2) ? true : false;
		boolean y = (Math.abs(i.p.y) % checkerWidth < checkerWidth2) ? true : false;
		boolean z = (Math.abs(i.p.z) % checkerWidth < checkerWidth2) ? true : false;
		
		// as the modulo function is a bit strange for -ve values, take the absolute value and handle signs manually.
		boolean signX = i.p.x>0;
		boolean signY = i.p.y>0;
		boolean signZ = i.p.z>0;
		
		// take the xor value of the three boolean values
		boolean bright = x ^ y ^ z ^ signX ^ signY ^ signZ;
		
		return bright ? colour1 : colour2;
	}
}
