package optics.raytrace.surfaces;

import optics.DoubleColour;
import optics.raytrace.core.*;
import optics.raytrace.exceptions.RayTraceException;


/**
 * A colour filter.
 * Multiplies the RGB components of the transmitted ray by the RGB transmission coefficients that define
 * the colour of the colour filter.
 * @author johannes
 */
public class ColourFilter extends SurfacePropertyPrimitive
{
	private static final long serialVersionUID = -618786901486870550L;

	private DoubleColour rgbTransmissionCoefficients;

	public static final ColourFilter
		WHITE = new ColourFilter(DoubleColour.WHITE, true),
		WHITER = new ColourFilter(DoubleColour.WHITER, true),	// actually makes the light brighter
		BLACK = new ColourFilter(DoubleColour.BLACK, true),
		GREY10 = new ColourFilter(DoubleColour.GREY10, true),
		GREY20 = new ColourFilter(DoubleColour.GREY20, true),
		GREY30 = new ColourFilter(DoubleColour.GREY30, true),
		GREY40 = new ColourFilter(DoubleColour.GREY40, true),
		GREY50 = new ColourFilter(DoubleColour.GREY50, true),
		GREY60 = new ColourFilter(DoubleColour.GREY60, true),
		GREY70 = new ColourFilter(DoubleColour.GREY70, true),
		GREY80 = new ColourFilter(DoubleColour.GREY80, true),
		GREY90 = new ColourFilter(DoubleColour.GREY90, true),
		GREY95 = new ColourFilter(DoubleColour.GREY95, true),
		RED = new ColourFilter(DoubleColour.RED, true),
		BLUE = new ColourFilter(DoubleColour.BLUE, true),
		GREEN = new ColourFilter(DoubleColour.GREEN, true),
		LIGHT_BLUE = new ColourFilter(DoubleColour.LIGHT_BLUE, true),
		DARK_BLUE = new ColourFilter(DoubleColour.DARK_BLUE, true),
		LIGHT_RED = new ColourFilter(DoubleColour.LIGHT_RED, true),
		DARK_RED = new ColourFilter(DoubleColour.DARK_RED, true),
		SKIN = new ColourFilter(DoubleColour.SKIN, true),
		YELLOW = new ColourFilter(DoubleColour.YELLOW, true),
		CYAN = new ColourFilter(DoubleColour.CYAN, true),
		CYAN_GLASS = new ColourFilter(DoubleColour.CYAN, false),
		LIGHT_CYAN_GLASS = new ColourFilter(DoubleColour.whiten(DoubleColour.CYAN, 0.7), false);

	public ColourFilter(DoubleColour rgbTransmissionCoefficients, boolean shadowThrowing)
	{
		super(0, shadowThrowing);
		this.rgbTransmissionCoefficients = rgbTransmissionCoefficients;
	}

	/**
	 * a Colour filter with a random colour
	 */
	public ColourFilter() {
		super(0, true);
		this.rgbTransmissionCoefficients = new  DoubleColour(Math.random(), Math.random(), Math.random());
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public ColourFilter clone()
	{
		return new ColourFilter(rgbTransmissionCoefficients.clone(), isShadowThrowing());
	}


	public DoubleColour getRgbTransmissionCoefficients() {
		return rgbTransmissionCoefficients;
	}

	public void setRgbTransmissionCoefficients(DoubleColour rgbTransmissionCoefficients) {
		this.rgbTransmissionCoefficients = rgbTransmissionCoefficients;
	}

	@Override
	public DoubleColour getColour(Ray ray, RaySceneObjectIntersection i, SceneObject scene, LightSource l, int traceLevel, RaytraceExceptionHandler raytraceExceptionHandler)
	throws RayTraceException
	{
		if(traceLevel <= 0) return DoubleColour.BLACK;

		// launch a new ray from here
			
		return DoubleColour.multiply(
				scene.getColourAvoidingOrigin(
						ray.getBranchRay(
								i.p,
								ray.getD(),
								i.t,
								ray.isReportToConsole()
								),
						i.o,
						l,
						scene,
						traceLevel-1,
						raytraceExceptionHandler
						),
				rgbTransmissionCoefficients
				);
	}

	@Override
	public String toString()
	{
		return "<ColourFilter>\n\t<rgbTransmissionCoefficients DoubleColour="+rgbTransmissionCoefficients+">\n</ColourFilter>\n";
	}
}

