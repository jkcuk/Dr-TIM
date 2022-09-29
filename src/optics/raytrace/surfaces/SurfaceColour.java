package optics.raytrace.surfaces;

import optics.DoubleColour;
import optics.raytrace.core.*;


public class SurfaceColour extends SurfacePropertyPrimitive
{
	private static final long serialVersionUID = -6350275245959773598L;

	private DoubleColour
		diffuseColour,
		specularColour;
	private String name;

	public static final SurfaceColour
		WHITE_MATT = new SurfaceColour("white matt", DoubleColour.WHITE, DoubleColour.BLACK, true),
		WHITE_SHINY = new SurfaceColour("white shiny", DoubleColour.WHITE, DoubleColour.WHITE, true),
		WHITER_MATT = new SurfaceColour("whiter matt", DoubleColour.WHITER, DoubleColour.BLACK, true),
		WHITER_SHINY = new SurfaceColour("whiter shiny", DoubleColour.WHITER, DoubleColour.WHITE, true),
		BLACK_MATT = new SurfaceColour("black matt", DoubleColour.BLACK, DoubleColour.BLACK, true),
		BLACK_SHINY = new SurfaceColour("black shiny", DoubleColour.BLACK, DoubleColour.WHITE, true),
		GREY10_MATT = new SurfaceColour("grey10 matt", DoubleColour.GREY10, DoubleColour.BLACK, true),
		GREY10_SHINY = new SurfaceColour("grey10 shiny", DoubleColour.GREY10, DoubleColour.WHITE, true),
		GREY20_MATT = new SurfaceColour("grey20 matt", DoubleColour.GREY20, DoubleColour.BLACK, true),
		GREY20_SHINY = new SurfaceColour("grey20 shiny", DoubleColour.GREY20, DoubleColour.WHITE, true),
		GREY30_MATT = new SurfaceColour("grey30 matt", DoubleColour.GREY30, DoubleColour.BLACK, true),
		GREY30_SHINY = new SurfaceColour("grey30 shiny", DoubleColour.GREY30, DoubleColour.WHITE, true),
		GREY40_MATT = new SurfaceColour("grey40 matt", DoubleColour.GREY40, DoubleColour.BLACK, true),
		GREY40_SHINY = new SurfaceColour("grey40 shiny", DoubleColour.GREY40, DoubleColour.WHITE, true),
		GREY50_MATT = new SurfaceColour("grey50 matt", DoubleColour.GREY50, DoubleColour.BLACK, true),
		GREY50_SHINY = new SurfaceColour("grey50 shiny", DoubleColour.GREY50, DoubleColour.WHITE, true),
		GREY60_MATT = new SurfaceColour("grey60 matt", DoubleColour.GREY60, DoubleColour.BLACK, true),
		GREY60_SHINY = new SurfaceColour("grey60 shiny", DoubleColour.GREY60, DoubleColour.WHITE, true),
		GREY70_MATT = new SurfaceColour("grey70 matt", DoubleColour.GREY70, DoubleColour.BLACK, true),
		GREY70_SHINY = new SurfaceColour("grey70 shiny", DoubleColour.GREY70, DoubleColour.WHITE, true),
		GREY80_MATT = new SurfaceColour("grey80 matt", DoubleColour.GREY80, DoubleColour.BLACK, true),
		GREY80_SHINY = new SurfaceColour("grey80 shiny", DoubleColour.GREY80, DoubleColour.WHITE, true),
		GREY90_MATT = new SurfaceColour("grey90 matt", DoubleColour.GREY90, DoubleColour.BLACK, true),
		GREY90_SHINY = new SurfaceColour("grey90 shiny", DoubleColour.GREY90, DoubleColour.WHITE, true),
		GREY95_MATT = new SurfaceColour("grey95 matt", DoubleColour.GREY95, DoubleColour.BLACK, true),
		GREY95_SHINY = new SurfaceColour("grey95 shiny", DoubleColour.GREY95, DoubleColour.WHITE, true),
		RED_MATT = new SurfaceColour("red matt", DoubleColour.RED, DoubleColour.BLACK, true),
		RED_SHINY = new SurfaceColour("red shiny", DoubleColour.RED, DoubleColour.WHITE, true),
		BLUE_MATT = new SurfaceColour("blue matt", DoubleColour.BLUE, DoubleColour.BLACK, true),
		BLUE_SHINY = new SurfaceColour("blue shiny", DoubleColour.BLUE, DoubleColour.WHITE, true),
		GREEN_MATT = new SurfaceColour("green matt", DoubleColour.GREEN, DoubleColour.BLACK, true),
		GREEN_SHINY = new SurfaceColour("green shiny", DoubleColour.GREEN, DoubleColour.WHITE, true),
		LIGHT_BLUE_MATT = new SurfaceColour("light blue matt", DoubleColour.LIGHT_BLUE, DoubleColour.BLACK, true),
		LIGHT_BLUE_SHINY = new SurfaceColour("light blue shiny", DoubleColour.LIGHT_BLUE, DoubleColour.WHITE, true),
		DARK_BLUE_MATT = new SurfaceColour("dark blue matt", DoubleColour.DARK_BLUE, DoubleColour.BLACK, true),
		DARK_BLUE_SHINY = new SurfaceColour("dark blue shiny", DoubleColour.DARK_BLUE, DoubleColour.WHITE, true),
		LIGHT_RED_MATT = new SurfaceColour("light red matt", DoubleColour.LIGHT_RED, DoubleColour.BLACK, true),
		LIGHT_RED_SHINY = new SurfaceColour("light red shiny", DoubleColour.LIGHT_RED, DoubleColour.WHITE, true),
		DARK_RED_MATT = new SurfaceColour("dark red matt", DoubleColour.DARK_RED, DoubleColour.BLACK, true),
		DARK_RED_SHINY = new SurfaceColour("dark red shiny", DoubleColour.DARK_RED, DoubleColour.WHITE, true),
		SKIN_MATT = new SurfaceColour("skin matt", DoubleColour.SKIN, DoubleColour.BLACK, true),
		SKIN_SHINY = new SurfaceColour("skin shiny", DoubleColour.SKIN, DoubleColour.WHITE, true),
		YELLOW_MATT = new SurfaceColour("yellow matt", DoubleColour.YELLOW, DoubleColour.BLACK, true),
		YELLOW_SHINY = new SurfaceColour("yellow shiny", DoubleColour.YELLOW, DoubleColour.WHITE, true),
		CYAN_MATT = new SurfaceColour("cyan matt", DoubleColour.CYAN, DoubleColour.BLACK, true),
		CYAN_SHINY = new SurfaceColour("cyan shiny", DoubleColour.CYAN, DoubleColour.WHITE, true),
		BROWN_MATT = new SurfaceColour("brown matt", DoubleColour.BROWN, DoubleColour.BLACK, true),
		BROWN_SHINY = new SurfaceColour("brown shiny", DoubleColour.BROWN, DoubleColour.WHITE, true),
		PURPLE_MATT = new SurfaceColour("purple matt", DoubleColour.PURPLE, DoubleColour.BLACK, true),
		PURPLE_SHINY = new SurfaceColour("purple shiny", DoubleColour.PURPLE, DoubleColour.WHITE, true);
	
	
	//an array of all surface colours. If a new one is added it should be added here too.
	public static SurfaceColour SurfaceColour[] = 
		{WHITE_MATT, WHITE_SHINY, WHITER_MATT, WHITER_SHINY, BLACK_MATT, BLACK_SHINY, GREY10_MATT, GREY10_SHINY, GREY20_MATT, GREY20_SHINY, GREY30_MATT, GREY30_SHINY, 
				GREY40_MATT, GREY40_SHINY, GREY50_MATT, GREY50_SHINY, GREY60_MATT, GREY60_SHINY, GREY70_MATT, GREY70_SHINY, GREY80_MATT, GREY80_SHINY, GREY90_MATT, GREY90_SHINY,
				GREY95_MATT, GREY95_SHINY, RED_MATT, RED_SHINY, BLUE_MATT, BLUE_SHINY, GREEN_MATT, GREEN_SHINY, LIGHT_BLUE_MATT, LIGHT_BLUE_SHINY, DARK_BLUE_MATT, DARK_BLUE_SHINY,
				LIGHT_RED_MATT, LIGHT_RED_SHINY, DARK_RED_MATT, DARK_RED_SHINY, SKIN_MATT, SKIN_SHINY, YELLOW_MATT, YELLOW_SHINY, CYAN_MATT, CYAN_SHINY, BROWN_MATT, BROWN_SHINY,
				PURPLE_MATT, PURPLE_SHINY};

	public static SurfaceColour getRandom()
	{
		return new SurfaceColour("Random colour",new DoubleColour(Math.random(), Math.random(), Math.random()), DoubleColour.WHITE, true);
	}
	
	public SurfaceColour(String name ,DoubleColour diffuseColour, DoubleColour specularColour, boolean shadowThrowing)
	{
		super(0, shadowThrowing);
		this.name = name;
		this.diffuseColour = diffuseColour;
		this.specularColour = specularColour;
	}
	
	public SurfaceColour(DoubleColour diffuseColour, DoubleColour specularColour, boolean shadowThrowing)
	{
		super(0, shadowThrowing);
		this.name = "diffuseColour: "+diffuseColour.getName()+", specularColour: "+specularColour.getName();
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
		return new SurfaceColour(name ,diffuseColour.clone(), specularColour.clone(), isShadowThrowing());
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

