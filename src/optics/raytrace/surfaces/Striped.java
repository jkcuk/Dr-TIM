package optics.raytrace.surfaces;

import optics.DoubleColour;
import optics.raytrace.core.LightSource;
import optics.raytrace.core.ParametrisedObject;
import optics.raytrace.core.Ray;
import optics.raytrace.core.RaySceneObjectIntersection;
import optics.raytrace.core.RaytraceExceptionHandler;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.SurfaceProperty;
import optics.raytrace.exceptions.RayTraceException;
import math.Vector2D;

public class Striped extends SurfaceProperty
{
	private static final long serialVersionUID = -8660123684987047639L;

	private SurfaceProperty surfaceProperty1, surfaceProperty2;	// the surface properties of the alternating stripes
	private double width;	// the width of the stripes
	
	/**
	 * Creates a new Striped surface property.
	 * 
	 * @param surfaceProperty1	surface properties of type-1 stripes 
	 * @param surfaceProperty2	surface properties of type-2 stripes
	 * @param width	width of the stripes
	 */
	public Striped(SurfaceProperty surfaceProperty1, SurfaceProperty surfaceProperty2, double width)
	{
		this.surfaceProperty1 = surfaceProperty1;
		this.surfaceProperty2 = surfaceProperty2;
		this.width = width;
	}
	
	public Striped() {
		this.surfaceProperty1 = new SurfaceColour(DoubleColour.BLACK, DoubleColour.WHITE, true);
		this.surfaceProperty2 = new SurfaceColour(DoubleColour.GREY90, DoubleColour.WHITE, true);
		this.width = 1;
	}
	
	public Striped(Striped original)
	{
		this.surfaceProperty1 = original.getSurfaceProperty1();
		this.surfaceProperty2 = original.getSurfaceProperty2();
		this.width = original.getWidth();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Striped clone()
	{
		return new Striped(surfaceProperty1.clone(), surfaceProperty2.clone(), width);
	}
	
	public SurfaceProperty getSurfaceProperty1() {
		return surfaceProperty1;
	}

	public void setSurfaceProperty1(SurfaceProperty surfaceProperty1) {
		this.surfaceProperty1 = surfaceProperty1;
	}

	public SurfaceProperty getSurfaceProperty2() {
		return surfaceProperty2;
	}

	public void setSurfaceProperty2(SurfaceProperty surfaceProperty2) {
		this.surfaceProperty2 = surfaceProperty2;
	}

	public double getWidth() {
		return width;
	}

	public void setWidth(double width) {
		this.width = width;
	}

	@Override
	public DoubleColour getColour(Ray r, RaySceneObjectIntersection i, SceneObject scene, LightSource l, int traceLevel, RaytraceExceptionHandler raytraceExceptionHandler)
	throws RayTraceException
	{
		// get the surface coordinates in terms of the intersected primitive scene object's surface coordinates
		Vector2D v = ((ParametrisedObject)(i.o)).getSurfaceCoordinates(i.p);
		
		// and return a colour accordingly
		return(((Math.signum(((Math.abs(v.x/width) % 2)-1)*v.x) > 0)?surfaceProperty1:surfaceProperty2).getColour(r, i, scene, l, traceLevel-1, raytraceExceptionHandler));
	}

	@Override
	public boolean isShadowThrowing() {
		return surfaceProperty1.isShadowThrowing() || surfaceProperty2.isShadowThrowing();
	}
}
