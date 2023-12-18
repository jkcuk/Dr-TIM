package optics.raytrace.core;


import java.io.*;

import optics.DoubleColour;
import optics.raytrace.lights.AmbientLight;
import optics.raytrace.lights.LightSourceContainer;
import optics.raytrace.lights.PhongLightSource;
import math.Vector3D;
import optics.raytrace.surfaces.SurfaceColour;

/**
 * In order for a class to be considered a valid light source, it must have some notion of the effect it has on a given light ray.  
 * A running total of the colour is kept of the light ray as well as the depth of the recursion that it has undertaken.  
 * 
 * @author collective
 */
public abstract class LightSource implements Serializable
{
	private static final long serialVersionUID = -746171018973946056L;

	/**
	 * Any light source should have some description
	 */
	protected String description;
	
	public LightSource(String description)
	{
		this.description=description;
	}
	
	/**
	 * Calculate the contribution to the colour of a RaySceneObjectIntersection due to this light source.
	 * @param surfaceColour The colour of the light ray before the effect of this light is taken into account.
	 * @param scene The entire scene to allow recursive ray tracing calls.
	 * @param i The intersection with the object is recorded is stored in this object.
	 * @param r The light ray the colour of which is currently being determined.
	 * @param traceLevel In order to limit computational overhead of an ill-posed visual problem the 
	 * depth of recursion that this light ray has undergone is recorded in order to choose a reasonable
	 * level of complexity at which to give up further ray tracing of this particular light ray.
	 * @return A new, updated colour that takes into account this light source.
	 */
	public abstract DoubleColour getColour(SurfaceColour surfaceColour, SceneObject scene,
			RaySceneObjectIntersection i, Ray r, int traceLevel);

	/**
	 * Create a standard set of light sources (for convenience)
	 * 
	 * @return	standard light sources
	 */
	public static LightSourceContainer getStandardLights()
	{
		LightSourceContainer lights = new LightSourceContainer("lights");
		lights.add(new AmbientLight("background light", DoubleColour.GREY20));
		lights.add(new PhongLightSource("point light souce", new Vector3D(100,300,50), DoubleColour.WHITE, DoubleColour.WHITE, 40.));
		return lights;
	}

	/**
	 * Create a standard set of light sources (for convenience)
	 * 
	 * @return	standard light sources
	 */
	public static LightSourceContainer getStandardLightsFromTheRight()
	{
		LightSourceContainer lights = new LightSourceContainer("lights");
		lights.add(new AmbientLight("background light", DoubleColour.GREY20));
		lights.add(new PhongLightSource("point light souce", new Vector3D(500,400,-100), DoubleColour.WHITE, DoubleColour.WHITE, 40.));
		return lights;
	}

	/**
	 * Create a standard set of light sources (for convenience)
	 * 
	 * @param brightnessFactor	the factor by which the light sources are brighter than "normal"
	 * @return	standard light sources
	 */
	public static LightSourceContainer getStandardLightsFromBehind(double brightnessFactor)
	{
		LightSourceContainer lights = new LightSourceContainer("lights");
		lights.add(new AmbientLight("background light", DoubleColour.GREY20.multiply(brightnessFactor)));
		lights.add(new PhongLightSource("point light souce", new Vector3D(100,300,-500), DoubleColour.WHITE.multiply(brightnessFactor), DoubleColour.WHITE.multiply(brightnessFactor), 40.));
		return lights;
	}

	/**
	 * Create a standard set of light sources (for convenience)
	 * 
	 * @return	standard light sources
	 */
	public static LightSourceContainer getStandardLightsFromBehind()
	{
		return getStandardLightsFromBehind(1);
	}

	/**
	 * Create a standard set of light sources (for convenience)
	 * 
	 * @return	standard light sources
	 */
	public static LightSourceContainer getBrightLightsFromBehind()
	{
		LightSourceContainer lights = new LightSourceContainer("lights");
		lights.add(new AmbientLight("background light", DoubleColour.GREY80));
		lights.add(new PhongLightSource("point light souce", new Vector3D(100,300,-500), DoubleColour.WHITE, DoubleColour.WHITE, 40.));
		return lights;
	}

	/**
	 * Create a standard set of light sources (for convenience)
	 * 
	 * @return	standard light sources
	 */
	public static LightSourceContainer getStandardLightsFromTheFront()
	{
		LightSourceContainer lights = new LightSourceContainer("lights");
		lights.add(new AmbientLight("background light", DoubleColour.GREY20));
		lights.add(new PhongLightSource("point light souce", new Vector3D(100,300,500), DoubleColour.WHITE, DoubleColour.WHITE, 40.));
		return lights;
	}
}
