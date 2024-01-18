package optics;

import optics.raytrace.surfaces.SurfaceColour;

import java.awt.Color;
import java.io.Serializable;



/**
 * An immutable object that represents a colour in double precision.
 * The RGB components are normally between 0 and 1, but they can also be outside this range.
 */
public class DoubleColour implements Serializable
{
	private static final long serialVersionUID = -1242055381447150171L;

	private double red, green, blue;

	private String name;
	
	public static final DoubleColour
	RED	= new DoubleColour("red",1,0,0),
	GREEN	= new DoubleColour("green",0,1,0),
	BLUE	= new DoubleColour("blue",0,0,1),
	BLACK	= new DoubleColour("black",0,0,0),
	WHITE	= new DoubleColour("white",1,1,1),
	WHITER	= new DoubleColour("whiter",2,2,2),
	GREY10 	= new DoubleColour("grey10",0.1, 0.1, 0.1),
	GREY20 	= new DoubleColour("grey20",0.2, 0.2, 0.2),
	GREY30 	= new DoubleColour("grey30",0.3, 0.3, 0.3),
	GREY40 	= new DoubleColour("grey40",0.4, 0.4, 0.4),
	GREY50 	= new DoubleColour("grey50",0.5, 0.5, 0.5),
	GREY60 	= new DoubleColour("grey60",0.6, 0.6, 0.6),
	GREY70 	= new DoubleColour("grey70",0.7, 0.7, 0.7),
	GREY80 	= new DoubleColour("grey80",0.8, 0.8, 0.8),
	GREY90 	= new DoubleColour("grey90",0.9, 0.9, 0.9),
	GREY95 	= new DoubleColour("grey95",0.95, 0.95, 0.95),
//	ORANGE	= new DoubleColour(1,0.5,0),
	BROWN = new DoubleColour("brown",165./256., 42./256., 42./256.),	// from http://kb.iu.edu/data/aetf.html
	YELLOW  = new DoubleColour("yellow",1.0, 1.0, 0.0),
	YELLOW_SUPERBRIGHT  = new DoubleColour("bright yellow",2.0, 2.0, 0.0),
	ORANGE = new DoubleColour("orange",1.0, 165./255., 0),	// Orange (web color) (Hex: #FFA500) (RGB: 255, 165, 0) (from http://simple.wikipedia.org/wiki/Orange)
	PURPLE = new DoubleColour("purple",0.5, 0., 0.5),	// Purple (from https://simple.wikipedia.org/wiki/Purple)
	VIOLET = new DoubleColour("violet",143./255., 0., 1.0),	// Violet (Electric Violet) (Middle Violet) (from https://simple.wikipedia.org/wiki/Violet)
	LILAC = new DoubleColour("lilac",200./255., 162./255., 200./255.),	// Lilac (Hex: #CAA2C8) (RGB: 200, 162, 200) (from https://simple.wikipedia.org/wiki/Purple)
	INDIGO = new DoubleColour("indigo",102./255., 0., 1.),	// Indigo (Electric Indigo) (Hex: #6600FF) (RGB: 102, 0, 255) (from https://simple.wikipedia.org/wiki/Purple)
	LAVENDER = new DoubleColour("lavender",181./255., 126./255., 220./255.),	// Lavender (Floral Lavender) (Maerz & Paul) (Hex: #B57EDC) (RGB: 181,126,220) (from https://simple.wikipedia.org/wiki/Purple)
	LIGHT_BLUE	= new DoubleColour("light blue",0.5, 0.5, 1),
	DARK_BLUE	= new DoubleColour("dark blue",0, 0, 0.5),
	LIGHT_RED 	= new DoubleColour("light red",1, 0.5, 0.5),
	DARK_RED	= new DoubleColour("dark red",0.5, 0, 0),
	LIGHT_GREEN = new DoubleColour("light green", 0.5, 1, 0.5),
	DARK_GREEN	= new DoubleColour("dark green",0, 0.5, 0),
	SKIN = new DoubleColour("skin",239./256., 208./256., 207./256.),
	CYAN = new DoubleColour("cyan",0, 198./255., 200./255.);
	
	
	/**
	 * Create a list of all the double colours. This should be expanded when adding a new colour.
	 */
	public static DoubleColour DoubleColour[]
			= { RED, GREEN, BLUE, BLACK, WHITE, WHITER, GREY10, GREY20, GREY60, GREY70, GREY80, GREY90, GREY95, BROWN, YELLOW, YELLOW_SUPERBRIGHT,
					ORANGE, PURPLE, VIOLET, LILAC, INDIGO, LAVENDER, LIGHT_BLUE, DARK_BLUE, LIGHT_RED, DARK_RED, SKIN, CYAN};
	

	/**
	 * constructor for a colour with a given name
	 * @param name
	 * @param red
	 * @param green
	 * @param blue
	 */
	
	public DoubleColour(String name, double red, double green, double blue) {
		this.name =name;
		//this.red = Math.min(Math.max(red, 0.0), 1.0);
		this.red = red;
		//this.green = Math.min(Math.max(green, 0.0), 1.0);
		this.green = green;
		//this.blue = Math.min(Math.max(blue, 0.0), 1.0);
		this.blue = blue;
	}
	
	/**
	 * constructor where the name is taken form the colour components
	 * @param red
	 * @param green
	 * @param blue
	 */
	public DoubleColour(double red, double green, double blue) {
		this.name ="custom: R="+red+",G="+green+",B="+blue;
		//this.red = Math.min(Math.max(red, 0.0), 1.0);
		this.red = red;
		//this.green = Math.min(Math.max(green, 0.0), 1.0);
		this.green = green;
		//this.blue = Math.min(Math.max(blue, 0.0), 1.0);
		this.blue = blue;
	}

	
	
	/**
	 * Constructor for a DoubleColour from an RGB int.
	 * Currently ignores saturation.
	 * @param RGB
	 */
	public DoubleColour(int RGB)
	{
		set(RGB);
	}
	
	/**
	 * Create a clone of the original
	 * @param original
	 */
	public DoubleColour(DoubleColour original)
	{
		name = original.name;
		red = original.red;
		green = original.green;
		blue = original.blue;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public DoubleColour clone()
	{
		return new DoubleColour(this);
	}

	/***************************************/
	
    /**
     * needed by getColourFromHSL
     * @param p
     * @param q
     * @param t
     * @return
     */
    private static double hue2rgbHelper(double p, double q, double t)
    {
        if(t < 0) t += 1;
        if(t > 1) t -= 1;
        if(t < 1/6.) return p + (q - p) * 6. * t;
        if(t < 1/2.) return q;
        if(t < 2/3.) return p + (q - p) * (2./3. - t) * 6.;
        return p;
    }

	/**
	 * Converts an HSL color value to RGB. Conversion formula
	 * adapted from http://en.wikipedia.org/wiki/HSL_color_space.
	 * Assumes h, s, and l are contained in the set [0, 1] and
	 * returns r, g, and b in the set [0, 255].
	 * 
	 * from http://stackoverflow.com/questions/2353211/hsl-to-rgb-color-conversion
	 */
	public static DoubleColour getColourFromHSL(double h, double s, double l)
	{
	    double r, g, b;

	    if(s == 0)
	    {
	        r = g = b = l; // achromatic
	    }
	    else
	    {
	        double q = (l < 0.5)?(l * (1 + s)):(l + s - l * s);
	        double p = 2 * l - q;
	        r = hue2rgbHelper(p, q, h + 1./3.);
	        g = hue2rgbHelper(p, q, h);
	        b = hue2rgbHelper(p, q, h - 1./3.);
	    }

	    return new DoubleColour("custom: R="+r+",G="+g+",B="+b, r, g, b);
	}

	/**
	 * The red component, restricted to [0,1].
	 * @return the red
	 */
	public double getRestrictedR() {
		// return red;
		return Math.min(Math.max(red, 0.0), 1.0);
	}

	/**
	 * The green component, restricted to [0,1].
	 * @return the green
	 */
	public double getRestrictedG() {
		// return green;
		return Math.min(Math.max(green, 0.0), 1.0);
	}

	/**
	 * The blue component, restricted to [0,1].
	 * @return the blue
	 */
	public double getRestrictedB() {
		// return blue;
		return Math.min(Math.max(blue, 0.0), 1.0);
	}
	
	public int getRGB() {
		return
		// saturation
		(255 << 24) |
		// red component
		(int)(255*getRestrictedR()) << 16 |
		// green component
		(int)(255*getRestrictedG()) << 8 |
		// blue component
		(int)(255*getRestrictedB());
	}
	
	public void set(DoubleColour c)
	{
		red = c.getR();
		green = c.getR();
		blue = c.getB();
	}
	
	public void set(int RGB)
	{
		name = "Custom";
		// ignore saturation, ((RGB >> 24) & 255)/255.
		red = ((RGB >> 16) & 255)/255.;
		green = ((RGB >> 8) & 255)/255.;
		blue = (RGB & 255)/255.;
	}

	/***************************************/	
	/**
	 * @return the name from a colour
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @return the red
	 */
	public double getR() {
		return red;
	}

	/**
	 * @return the green
	 */
	public double getG() {
		return green;
	}

	/**
	 * @return the blue
	 */
	public double getB() {
		return blue;
	}
	
	
	/* Calculation of luminance */
	
	public static final double
		LUMINANCE_R_FACTOR = 0.2126,
		LUMINANCE_G_FACTOR = 0.7152,
		LUMINANCE_B_FACTOR = 0.0722;
	
	/**
	 * see http://stackoverflow.com/questions/596216/formula-to-determine-brightness-of-rgb-color
	 * @return standard luminance
	 */
	public double getLuminance()
	{
		return LUMINANCE_R_FACTOR*getR() + LUMINANCE_G_FACTOR*getG() + LUMINANCE_B_FACTOR*getB();
	}
	
	public DoubleColour getGrayScaleColour()
	{
		double l = getLuminance();
		return new DoubleColour("grey "+l,l, l, l);
	}
	
	public double getRestrictedLuminance() {
		// return blue;
		return Math.min(Math.max(getLuminance(), 0.0), 1.0);
	}
	
	/**
	 * convert colour to luminance;
	 * see http://stackoverflow.com/questions/596216/formula-to-determine-brightness-of-rgb-color
	 * @return standard luminance
	 */
	public static int getLuminance(Color c)
	{
		return (int)(LUMINANCE_R_FACTOR*c.getRed() + LUMINANCE_G_FACTOR*c.getGreen() + LUMINANCE_B_FACTOR*c.getBlue() + 0.5);
	}

	public static DoubleColour complementaryColour(DoubleColour colour)
	{
		return new DoubleColour(colour.getName(),1-colour.red, 1-colour.green, 1-colour.blue);
	}

	/***************************************/
	
	public static DoubleColour sum(DoubleColour c1, DoubleColour c2)
	{
		return new DoubleColour(c1.getName()+"+"+c2.getName(),c1.getR() + c2.getR(), c1.getG() + c2.getG(), c1.getB() + c2.getB());
	}

	public DoubleColour add(DoubleColour c) {
		return new DoubleColour(getName()+"+"+c.getName(), red + c.getR(), green + c.getG(), blue + c.getB());
	}

	public DoubleColour multiply(double f)
	{
		return new DoubleColour(getName()+"*"+f, f*red, f*green, f*blue);
	}
	
	/**
	 * @return a DoubleColour with RGB component (r', g', b') such that r'=sqrt(r), g'=sqrt(g), b'=sqrt(b) (where r, g, b are the RGB components of this)
	 */
	public DoubleColour getSqrt()
	{
		return new DoubleColour("\u221A"+getName() ,Math.sqrt(red), Math.sqrt(green), Math.sqrt(blue));
	}

	/**
	 * multiplies 2 DoubleColours together by their components
	 * 
	 * @param c
	 * @param d
	 * @return (c.r*d.r, c.g*d.g, c.b*d.b)
	 */
	public static DoubleColour multiply(DoubleColour c, DoubleColour d) {
		return new DoubleColour(c.getName()+"*"+d.getName(),
				c.getR()*d.getR(),
				c.getG()*d.getG(),
				c.getB()*d.getB()
			);
	}

	/**
	 * Component-by-component multiplication of a colour by a surface colour.
	 * Only the diffuse component in the surface colour is multiplied by the colour. 
	 * @param c	the colour
	 * @param d	the surface colour
	 * @return	the product of the diffuse component of the surface colour and the colour
	 */
	public static DoubleColour multiply(DoubleColour c, SurfaceColour d) {
		return new DoubleColour(c.getName()+"*"+d.getName(),
				c.getR()*d.getDiffuseColour().getR(), 
				c.getG()*d.getDiffuseColour().getG(), 
				c.getB()*d.getDiffuseColour().getB() 
			);
	}
	
	/**
	 * @param c
	 * @param whiteness a number between 0 (leave colour as is) and 1 (make completely white)
	 * @return	the whitened colour
	 */
	public static DoubleColour whiten(DoubleColour c, double whiteness)
	{
		return new DoubleColour(whiteness+" whiteness "+c.getName(),
				c.getR() + whiteness * (1.-c.getR()),
				c.getG() + whiteness * (1.-c.getG()),
				c.getB() + whiteness * (1.-c.getB())
			);
	}

	@Override
	public String toString() {
		return "<DoubleColour red="+red+" green="+green+" blue="+blue+">";
	}
}
