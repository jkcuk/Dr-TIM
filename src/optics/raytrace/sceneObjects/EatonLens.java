package optics.raytrace.sceneObjects;

import java.io.*;

import math.*;
import optics.raytrace.core.*;
import optics.raytrace.surfaces.EatonLensSurface;


/**
 * Eaton lens --- a sphere that reflects light in a particular way.
 * All the hard work is actually done by a special surface class called EatonLensSurface.
 * 
 * @author Johannes
 *
 */

public class EatonLens extends Sphere implements Serializable
{	
	private static final long serialVersionUID = -7375475595569764921L;

	// constructor
	public EatonLens(
			String description, Vector3D centre, double radius,
			double ratioNSurfaceNSurrounding,
			double transparentTunnelRadius,
			double transmissionCoefficient,
			boolean shadowThrowing,
			SceneObject parent,
			Studio studio
		)
	{
		super(description, centre, radius, new EatonLensSurface(ratioNSurfaceNSurrounding, transparentTunnelRadius, transmissionCoefficient, shadowThrowing), parent, studio);
	}
	
	/**
	 * Create a clone of the original
	 * @param original
	 */
	public EatonLens(EatonLens original)
	{
		this(
				original.description,
				original.getCentre().clone(),
				original.getRadius(),
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
	public EatonLens clone()
	{
		return new EatonLens(this);
	}

	@Override
	public EatonLens transform(Transformation t)
	{
		return new EatonLens(
				description,
				t.transformPosition(getCentre()),
				getRadius(),
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
		return "<EatonLens>\n" +
		"\t<centre Vector3D="+getCentre()+">\n" + 
		"\t<radius double="+getRadius()+">\n" + 
		"\t<ratioNSurfaceNSurrounding double="+getRatioNSurfaceNSurrounding()+">\n" +
		"\t<transparentTunnelRadius double="+getTransparentTunnelRadius()+">\n" +
		"\t<transmissionCoefficient double="+getTransmissionCoefficient()+">\n" +
		"</EatonLens>\n";
	}
	
	public void setTransmissionCoefficient(double transmissionCoefficient)
	{
		((EatonLensSurface)getSurfaceProperty()).setTransmissionCoefficient(transmissionCoefficient);
	}
	
	public double getTransmissionCoefficient()
	{
		return ((EatonLensSurface)getSurfaceProperty()).getTransmissionCoefficient();
	}

	public void setRatioNSurfaceNSurrounding(double ratioNSurfaceNSurrounding)
	{
		((EatonLensSurface)getSurfaceProperty()).setRatioNSurfaceNSurrounding(ratioNSurfaceNSurrounding);
	}
	
	public double getRatioNSurfaceNSurrounding()
	{
		return ((EatonLensSurface)getSurfaceProperty()).getRatioNSurfaceNSurrounding();
	}

	public void setTransparentTunnelRadius(double transparentTunnelRadius)
	{
		((EatonLensSurface)getSurfaceProperty()).setTransparentTunnelRadius(transparentTunnelRadius);
	}
	
	public double getTransparentTunnelRadius()
	{
		return ((EatonLensSurface)getSurfaceProperty()).getTransparentTunnelRadius();
	}

	@Override
	public String getType()
	{
		return "Eaton lens";
	}
}
