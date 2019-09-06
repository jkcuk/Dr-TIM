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

public class SurfaceTiling extends SurfaceProperty
{
	private static final long serialVersionUID = 8289080580886993772L;

	private SurfaceProperty surfaceProperty1, surfaceProperty2;	// the surface properties of the tiles
	private double widthU, widthV;	// the widths of the tiles in the u and v directions
	
	/**
	 * Creates a new SurfaceTiling.
	 * 
	 * @param surfaceProperty1	surface properties of type-1 tiles 
	 * @param surfaceProperty2	surface properties of type-2 tiles
	 * @param widthU	width of the tiles in the u direction
	 * @param widthV	width of the tiles in the v direction
	 */
	public SurfaceTiling(SurfaceProperty surfaceProperty1, SurfaceProperty surfaceProperty2, double widthU, double widthV)
	{
		this.surfaceProperty1 = surfaceProperty1;
		this.surfaceProperty2 = surfaceProperty2;
		this.widthU = widthU;
		this.widthV = widthV;
	}
	
	public SurfaceTiling() {
		this.surfaceProperty1 = new SurfaceColour(DoubleColour.BLACK, DoubleColour.WHITE, true);
		this.surfaceProperty2 = new SurfaceColour(DoubleColour.GREY90, DoubleColour.WHITE, true);
		this.widthU = 1;
		this.widthV = 1;
	}
	
	public SurfaceTiling(SurfaceTiling original)
	{
		this.surfaceProperty1 = original.getSurfaceProperty1();
		this.surfaceProperty2 = original.getSurfaceProperty2();
		this.widthU = original.getWidthU();
		this.widthV = original.getWidthV();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public SurfaceTiling clone()
	{
		return new SurfaceTiling(surfaceProperty1.clone(), surfaceProperty2.clone(), widthU, widthV);
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

	public double getWidthU() {
		return widthU;
	}

	public void setWidthU(double widthU) {
		this.widthU = widthU;
	}

	public double getWidthV() {
		return widthV;
	}

	public void setWidthV(double widthV) {
		this.widthV = widthV;
	}

	@Override
	public DoubleColour getColour(Ray r, RaySceneObjectIntersection i, SceneObject scene, LightSource l, int traceLevel, RaytraceExceptionHandler raytraceExceptionHandler)
	throws RayTraceException
	{
		// get the surface coordinates in terms of the intersected primitive scene object's surface coordinates
		Vector2D v = ((ParametrisedObject)(i.o)).getSurfaceCoordinates(i.p);
		
		// and return a colour accordingly
		return(((Math.signum(((Math.abs(v.x/widthU) % 2)-1)*((Math.abs(v.y/widthV) % 2)-1)*v.x*v.y) > 0)?surfaceProperty1:surfaceProperty2).getColour(r, i, scene, l, traceLevel-1, raytraceExceptionHandler));
	}

	@Override
	public boolean isShadowThrowing() {
		return surfaceProperty1.isShadowThrowing() || surfaceProperty2.isShadowThrowing();
	}
}
