package optics.raytrace.sceneObjects;

import java.io.*;

import math.*;
import optics.raytrace.core.*;
import optics.raytrace.surfaces.GGRINLensSurface;


/**
 * GGRIN lens [1].
 * [1] M. Sarbort and T. Tyc, "Spherical media and geodesic lenses in geometrical optics", Journal of Optics�14, 075705�(2012)
 * http://stacks.iop.org/2040-8986/14/i=7/a=075705
 * 
 * @author Johannes
 *
 */

public class GGRINLens extends Sphere implements Serializable
{	
	private static final long serialVersionUID = -3220797081683427064L;

	// constructor
	public GGRINLens(
			String description, Vector3D centre, double radius,
			double r1,
			double r2,
			double alpha,
			double ratioNSurfaceNSurrounding,
			double transparentTunnelRadius,
			double transmissionCoefficient,
			boolean shadowThrowing,
			SceneObject parent,
			Studio studio
		)
	{
		super(description, centre, radius, new GGRINLensSurface(r1, r2, alpha, ratioNSurfaceNSurrounding, transparentTunnelRadius, transmissionCoefficient, shadowThrowing), parent, studio);
	}
	
	/**
	 * Create a clone of the original
	 * @param original
	 */
	public GGRINLens(GGRINLens original)
	{
		this(
				original.description,
				original.getCentre().clone(),
				original.getRadius(),
				original.getR(),
				original.getRPrime(),
				original.getPhi(),
				original.getRatioNSurfaceNSurrounding(),
				original.getTransparentTunnelRadius(),
				original.getTransmissionCoefficient(),
				original.isShadowThrowing(),
				original.getParent(),
				original.getStudio()
			);
	}
	
	/* (non-Javadoc)
	 * @see optics.raytrace.sceneObjects.SceneObject#clone()
	 */
	@Override
	public GGRINLens clone()
	{
		return new GGRINLens(this);
	}

	@Override
	public GGRINLens transform(Transformation t)
	{
		return new GGRINLens(
				description,
				t.transformPosition(getCentre()),
				getRadius(),
				getR(),
				getRPrime(),
				getPhi(),
				getRatioNSurfaceNSurrounding(),
				getTransparentTunnelRadius(),
				getTransmissionCoefficient(),
				isShadowThrowing(),
				getParent(),
				getStudio()
		);
	}

	@Override
	public String toString() {
		return "<GGRIN Lens>\n" +
		"\t<centre Vector3D="+getCentre()+">\n" + 
		"\t<radius double="+getRadius()+">\n" + 
		"\t<r1 double="+getR()+">\n" + 
		"\t<r2 double="+getRPrime()+">\n" + 
		"\t<alpha double="+getPhi()+">\n" + 
		"\t<ratioNSurfaceNSurrounding double="+getRatioNSurfaceNSurrounding()+">\n" +
		"\t<transmissionCoefficient double="+getTransmissionCoefficient()+">\n" +
		"</EatonLens>\n";
	}
	
	public void setR(double r1)
	{
		((GGRINLensSurface)getSurfaceProperty()).setR1(r1);
	}

	public double getR()
	{
		return ((GGRINLensSurface)getSurfaceProperty()).getR1();
	}
	
	public void setRPrime(double r2)
	{
		((GGRINLensSurface)getSurfaceProperty()).setR2(r2);
	}
	
	public double getRPrime()
	{
		return ((GGRINLensSurface)getSurfaceProperty()).getR2();
	}
	
	public void setPhi(double alpha)
	{
		((GGRINLensSurface)getSurfaceProperty()).setAlpha(alpha);
	}

	public double getPhi()
	{
		return ((GGRINLensSurface)getSurfaceProperty()).getAlpha();
	}

	public void setTransmissionCoefficient(double transmissionCoefficient)
	{
		((GGRINLensSurface)getSurfaceProperty()).setTransmissionCoefficient(transmissionCoefficient);
	}
	
	public double getTransmissionCoefficient()
	{
		return ((GGRINLensSurface)getSurfaceProperty()).getTransmissionCoefficient();
	}

	public void setRatioNSurfaceNSurrounding(double ratioNSurfaceNSurrounding)
	{
		((GGRINLensSurface)getSurfaceProperty()).setRatioNSurfaceNSurrounding(ratioNSurfaceNSurrounding);
	}
	
	public double getRatioNSurfaceNSurrounding()
	{
		return ((GGRINLensSurface)getSurfaceProperty()).getRatioNSurfaceNSurrounding();
	}

	public void setTransparentTunnelRadius(double transparentTunnelRadius)
	{
		((GGRINLensSurface)getSurfaceProperty()).setTransparentTunnelRadius(transparentTunnelRadius);
	}
	
	public double getTransparentTunnelRadius()
	{
		return ((GGRINLensSurface)getSurfaceProperty()).getTransparentTunnelRadius();
	}
	
	@Override
	public String getType()
	{
		return "GGRIN lens";
	}
}
