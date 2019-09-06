package optics.raytrace.surfaces;

import optics.DoubleColour;
import optics.raytrace.core.*;


public class SurfaceColour extends SurfacePropertyPrimitive
{
	private static final long serialVersionUID = -6350275245959773598L;

	private DoubleColour
		diffuseColour,
		specularColour;

	public static final SurfaceColour
		WHITE_MATT = new SurfaceColour(DoubleColour.WHITE, DoubleColour.BLACK, true),
		WHITE_SHINY = new SurfaceColour(DoubleColour.WHITE, DoubleColour.WHITE, true),
		WHITER_MATT = new SurfaceColour(DoubleColour.WHITER, DoubleColour.BLACK, true),
		WHITER_SHINY = new SurfaceColour(DoubleColour.WHITER, DoubleColour.WHITE, true),
		BLACK_MATT = new SurfaceColour(DoubleColour.BLACK, DoubleColour.BLACK, true),
		BLACK_SHINY = new SurfaceColour(DoubleColour.BLACK, DoubleColour.WHITE, true),
		GREY10_MATT = new SurfaceColour(DoubleColour.GREY10, DoubleColour.BLACK, true),
		GREY10_SHINY = new SurfaceColour(DoubleColour.GREY10, DoubleColour.WHITE, true),
		GREY20_MATT = new SurfaceColour(DoubleColour.GREY20, DoubleColour.BLACK, true),
		GREY20_SHINY = new SurfaceColour(DoubleColour.GREY20, DoubleColour.WHITE, true),
		GREY30_MATT = new SurfaceColour(DoubleColour.GREY30, DoubleColour.BLACK, true),
		GREY30_SHINY = new SurfaceColour(DoubleColour.GREY30, DoubleColour.WHITE, true),
		GREY40_MATT = new SurfaceColour(DoubleColour.GREY40, DoubleColour.BLACK, true),
		GREY40_SHINY = new SurfaceColour(DoubleColour.GREY40, DoubleColour.WHITE, true),
		GREY50_MATT = new SurfaceColour(DoubleColour.GREY50, DoubleColour.BLACK, true),
		GREY50_SHINY = new SurfaceColour(DoubleColour.GREY50, DoubleColour.WHITE, true),
		GREY60_MATT = new SurfaceColour(DoubleColour.GREY60, DoubleColour.BLACK, true),
		GREY60_SHINY = new SurfaceColour(DoubleColour.GREY60, DoubleColour.WHITE, true),
		GREY70_MATT = new SurfaceColour(DoubleColour.GREY70, DoubleColour.BLACK, true),
		GREY70_SHINY = new SurfaceColour(DoubleColour.GREY70, DoubleColour.WHITE, true),
		GREY80_MATT = new SurfaceColour(DoubleColour.GREY80, DoubleColour.BLACK, true),
		GREY80_SHINY = new SurfaceColour(DoubleColour.GREY80, DoubleColour.WHITE, true),
		GREY90_MATT = new SurfaceColour(DoubleColour.GREY90, DoubleColour.BLACK, true),
		GREY90_SHINY = new SurfaceColour(DoubleColour.GREY90, DoubleColour.WHITE, true),
		GREY95_MATT = new SurfaceColour(DoubleColour.GREY95, DoubleColour.BLACK, true),
		GREY95_SHINY = new SurfaceColour(DoubleColour.GREY95, DoubleColour.WHITE, true),
		RED_MATT = new SurfaceColour(DoubleColour.RED, DoubleColour.BLACK, true),
		RED_SHINY = new SurfaceColour(DoubleColour.RED, DoubleColour.WHITE, true),
		BLUE_MATT = new SurfaceColour(DoubleColour.BLUE, DoubleColour.BLACK, true),
		BLUE_SHINY = new SurfaceColour(DoubleColour.BLUE, DoubleColour.WHITE, true),
		GREEN_MATT = new SurfaceColour(DoubleColour.GREEN, DoubleColour.BLACK, true),
		GREEN_SHINY = new SurfaceColour(DoubleColour.GREEN, DoubleColour.WHITE, true),
		LIGHT_BLUE_MATT = new SurfaceColour(DoubleColour.LIGHT_BLUE, DoubleColour.BLACK, true),
		LIGHT_BLUE_SHINY = new SurfaceColour(DoubleColour.LIGHT_BLUE, DoubleColour.WHITE, true),
		DARK_BLUE_MATT = new SurfaceColour(DoubleColour.DARK_BLUE, DoubleColour.BLACK, true),
		DARK_BLUE_SHINY = new SurfaceColour(DoubleColour.DARK_BLUE, DoubleColour.WHITE, true),
		LIGHT_RED_MATT = new SurfaceColour(DoubleColour.LIGHT_RED, DoubleColour.BLACK, true),
		LIGHT_RED_SHINY = new SurfaceColour(DoubleColour.LIGHT_RED, DoubleColour.WHITE, true),
		DARK_RED_MATT = new SurfaceColour(DoubleColour.DARK_RED, DoubleColour.BLACK, true),
		DARK_RED_SHINY = new SurfaceColour(DoubleColour.DARK_RED, DoubleColour.WHITE, true),
		SKIN_MATT = new SurfaceColour(DoubleColour.SKIN, DoubleColour.BLACK, true),
		SKIN_SHINY = new SurfaceColour(DoubleColour.SKIN, DoubleColour.WHITE, true),
		YELLOW_MATT = new SurfaceColour(DoubleColour.YELLOW, DoubleColour.BLACK, true),
		YELLOW_SHINY = new SurfaceColour(DoubleColour.YELLOW, DoubleColour.WHITE, true),
		CYAN_MATT = new SurfaceColour(DoubleColour.CYAN, DoubleColour.BLACK, true),
		CYAN_SHINY = new SurfaceColour(DoubleColour.CYAN, DoubleColour.WHITE, true),
		BROWN_MATT = new SurfaceColour(DoubleColour.BROWN, DoubleColour.BLACK, true),
		BROWN_SHINY = new SurfaceColour(DoubleColour.BROWN, DoubleColour.WHITE, true),
		PURPLE_MATT = new SurfaceColour(DoubleColour.PURPLE, DoubleColour.BLACK, true),
		PURPLE_SHINY = new SurfaceColour(DoubleColour.PURPLE, DoubleColour.WHITE, true);

	public static SurfaceColour getRandom()
	{
		return new SurfaceColour(new DoubleColour(Math.random(), Math.random(), Math.random()), DoubleColour.WHITE, true);
	}
	
	public SurfaceColour(DoubleColour diffuseColour, DoubleColour specularColour, boolean shadowThrowing)
	{
		super(0, shadowThrowing);
		this.diffuseColour = diffuseColour;
		this.specularColour = specularColour;
	}

	public SurfaceColour() {
		super(0, true);
		this.diffuseColour = new  DoubleColour(Math.random(), Math.random(), Math.random());
		this.specularColour = DoubleColour.WHITE;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public SurfaceColour clone()
	{
		return new SurfaceColour(diffuseColour.clone(), specularColour.clone(), isShadowThrowing());
	}


	public DoubleColour getDiffuseColour() {
		return diffuseColour;
	}

	public void setDiffuseColour(DoubleColour diffuseColour) {
		this.diffuseColour = diffuseColour;
	}

	public DoubleColour getSpecularColour() {
		return specularColour;
	}

	public void setSpecularColour(DoubleColour specularColour) {
		this.specularColour = specularColour;
	}

	@Override
	public DoubleColour getColour(Ray r, RaySceneObjectIntersection i, SceneObject scene, LightSource l, int traceLevel, RaytraceExceptionHandler raytraceExceptionHandler)
	{
		if(l == null) return DoubleColour.BLACK;
		
		return l.getColour(this, scene, i, r, traceLevel);
	}

	@Override
	public String toString()
	{
		return "<SurfaceColour>\n\t<diffuseColour DoubleColour="+diffuseColour+">\n\t<specularColour DoubleColour="+specularColour+">\n</SurfaceColour>\n";
	}
}

