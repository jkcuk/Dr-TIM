package optics.raytrace.surfaces;

import math.Vector2D;
import optics.DoubleColour;
import optics.raytrace.core.*;


/**
 * The scene object needs to be parametrised for this to work.
 * @author johannes
 *
 */
public class HueBrightnessGradientSurface extends SurfacePropertyPrimitive
{
	private static final long serialVersionUID = -996205815957436796L;

	private DoubleColour specularColour;

	public HueBrightnessGradientSurface(DoubleColour specularColour, boolean shadowThrowing)
	{
		super(0, shadowThrowing);
		this.specularColour = specularColour;
	}
	
	public HueBrightnessGradientSurface(boolean shadowThrowing)
	{
		super(0, shadowThrowing);
		this.specularColour = DoubleColour.WHITE;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public HueBrightnessGradientSurface clone()
	{
		return new HueBrightnessGradientSurface(specularColour.clone(), isShadowThrowing());
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
		
		Vector2D surfaceCoordinates = ((ParametrisedObject)i.o).getSurfaceCoordinates(i.p);
		
//		return DoubleColour.getColourFromHSL(
//				surfaceCoordinates.y,	// hue
//				1,	// saturation
//				surfaceCoordinates.x	// lightness
//			);
		return l.getColour(
				new SurfaceColour(
						DoubleColour.getColourFromHSL(
								surfaceCoordinates.y,	// hue
								1,	// saturation
								surfaceCoordinates.x	// lightness
							),
						specularColour,
						isShadowThrowing()
					),
				scene, i, r, traceLevel
			);
	}

	@Override
	public String toString()
	{
		return "<HueBrightnessGradient>\n\t<specularColour DoubleColour="+specularColour+">\n</SurfaceColour>\n";
	}
}

