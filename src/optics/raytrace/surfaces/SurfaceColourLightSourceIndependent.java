package optics.raytrace.surfaces;

import optics.DoubleColour;
import optics.raytrace.core.LightSource;
import optics.raytrace.core.Ray;
import optics.raytrace.core.RaySceneObjectIntersection;
import optics.raytrace.core.RaytraceExceptionHandler;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.SurfaceProperty;
import optics.raytrace.core.SurfacePropertyWithControllableShadow;

/**
 * A surface property that returns always the same colour, independent of any light sources.
 * This is useful for, for example, the sky: we don't want light sources to cast a shadow on the sky...
 * 
 * @author Johannes Courtial
 */
public class SurfaceColourLightSourceIndependent extends SurfaceProperty implements SurfacePropertyWithControllableShadow
{
	private static final long serialVersionUID = 5488070872471801957L;

	private DoubleColour
		colour;
	private boolean shadowThrowing;
	
	public static final SurfaceColourLightSourceIndependent
		WHITE = new SurfaceColourLightSourceIndependent(DoubleColour.WHITE, true),
		BLACK = new SurfaceColourLightSourceIndependent(DoubleColour.BLACK, true),
		GREY10 = new SurfaceColourLightSourceIndependent(DoubleColour.GREY10, true),
		GREY20 = new SurfaceColourLightSourceIndependent(DoubleColour.GREY20, true),
		GREY30 = new SurfaceColourLightSourceIndependent(DoubleColour.GREY30, true),
		GREY40 = new SurfaceColourLightSourceIndependent(DoubleColour.GREY40, true),
		GREY50 = new SurfaceColourLightSourceIndependent(DoubleColour.GREY50, true),
		GREY60 = new SurfaceColourLightSourceIndependent(DoubleColour.GREY60, true),
		GREY70 = new SurfaceColourLightSourceIndependent(DoubleColour.GREY70, true),
		GREY80 = new SurfaceColourLightSourceIndependent(DoubleColour.GREY80, true),
		GREY90 = new SurfaceColourLightSourceIndependent(DoubleColour.GREY90, true),
		RED = new SurfaceColourLightSourceIndependent(DoubleColour.RED, true),
		GREEN = new SurfaceColourLightSourceIndependent(DoubleColour.GREEN, true),
		BLUE = new SurfaceColourLightSourceIndependent(DoubleColour.BLUE, true),
		CYAN = new SurfaceColourLightSourceIndependent(DoubleColour.CYAN, true);
		
	public static SurfaceColourLightSourceIndependent getRandom()
	{
		return new SurfaceColourLightSourceIndependent(new DoubleColour(Math.random(), Math.random(), Math.random()), true);
	}

	/**
	 * creates a new light-source-independent surface colour, that is, a surface colour that is
	 * the same (namely <colour>, the argument of this method) whatever the lighting conditions
	 * 
	 * @param colour
	 */
	public SurfaceColourLightSourceIndependent(DoubleColour colour, boolean shadowThrowing)
	{
		this.colour = colour;
		this.shadowThrowing = shadowThrowing;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public SurfaceColourLightSourceIndependent clone()
	{
		return new SurfaceColourLightSourceIndependent(colour.clone(), isShadowThrowing());
	}
	
	/* (non-Javadoc)
	 * @see optics.raytrace.surfaces.SurfaceProperty#getColour(optics.raytrace.core.Ray, optics.raytrace.core.RaySceneObjectIntersection, optics.raytrace.sceneObjects.SceneObject, optics.raytrace.lights.LightSource, int)
	 */
	@Override
	public DoubleColour getColour(Ray r, RaySceneObjectIntersection i, SceneObject scene, LightSource l, int traceLevel, RaytraceExceptionHandler raytraceExceptionHandler)
	{
		return colour;
	}
	
	// setters & getters
	
	public DoubleColour getColour()
	{
		return colour;
	}

	public void setColour(DoubleColour colour)
	{
		this.colour = colour;
	}
	
	@Override
	public boolean isShadowThrowing() {
		return shadowThrowing;
	}


	@Override
	public void setShadowThrowing(boolean shadowThrowing) {
		this.shadowThrowing = shadowThrowing;
	}
}

