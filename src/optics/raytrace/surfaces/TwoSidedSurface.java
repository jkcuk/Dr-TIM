package optics.raytrace.surfaces;

import math.Vector3D;
import optics.DoubleColour;
import optics.raytrace.core.LightSource;
import optics.raytrace.core.Ray;
import optics.raytrace.core.RaySceneObjectIntersection;
import optics.raytrace.core.RaytraceExceptionHandler;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.SurfaceProperty;
import optics.raytrace.exceptions.RayTraceException;

public class TwoSidedSurface extends SurfaceProperty
{
	private static final long serialVersionUID = 5069414334975817740L;

	private SurfaceProperty
		insideSurfaceProperty,	// applied to rays travelling outwards
		outsideSurfaceProperty;	// applied to rays travelling inwards
	

	public TwoSidedSurface(SurfaceProperty insideSurfaceProperty, SurfaceProperty outsideSurfaceProperty)
	{
		setInsideSurfaceProperty(insideSurfaceProperty);
		setOutsideSurfaceProperty(outsideSurfaceProperty);
	}
	
	public TwoSidedSurface(TwoSidedSurface original)
	{
		setInsideSurfaceProperty(original.getInsideSurfaceProperty().clone());
		setOutsideSurfaceProperty(original.getOutsideSurfaceProperty().clone());
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public TwoSidedSurface clone()
	{
		return new TwoSidedSurface(this);
	}

	/**
	 * Return the colour corresponding to the ray r hitting intersection i.
	 * The scene and traceLevel are provided so that the ray can be traced further, if necessary (e.g. after reflection).
	 * In this case, any secondary rays must be added to the ray, but only if ray is an instance of RayWithTrajectory.
	 * 
	 * @param r
	 * @param i
	 * @param scene
	 * @param traceLevel
	 * @return the colour of the ray r hitting intersection i
	 */
	@Override
	public DoubleColour getColour(Ray r, RaySceneObjectIntersection i, SceneObject scene, LightSource l, int traceLevel, RaytraceExceptionHandler raytraceExceptionHandler)
	throws RayTraceException
	{
		// Check traceLevel is greater than 0.
		if(traceLevel <= 0) return DoubleColour.BLACK;
	
		if(Vector3D.scalarProduct(r.getD(), i.getNormalisedOutwardsSurfaceNormal()) > 0.)
		{
			// the ray is travelling outwards
			return insideSurfaceProperty.getColour(r, i, scene, l, traceLevel, raytraceExceptionHandler);
		}
		else
		{
			// the ray is travelling inwards
			return outsideSurfaceProperty.getColour(r, i, scene, l, traceLevel, raytraceExceptionHandler);
		}
	}
	
	// setters and getters

	public SurfaceProperty getInsideSurfaceProperty() {
		return insideSurfaceProperty;
	}

	public void setInsideSurfaceProperty(SurfaceProperty insideSurfaceProperty) {
		this.insideSurfaceProperty = insideSurfaceProperty;
	}

	public SurfaceProperty getOutsideSurfaceProperty() {
		return outsideSurfaceProperty;
	}

	public void setOutsideSurfaceProperty(SurfaceProperty outsideSurfaceProperty) {
		this.outsideSurfaceProperty = outsideSurfaceProperty;
	}

	@Override
	public boolean isShadowThrowing() {
		return insideSurfaceProperty.isShadowThrowing() || outsideSurfaceProperty.isShadowThrowing();
	}
}

