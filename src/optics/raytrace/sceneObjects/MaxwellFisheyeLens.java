package optics.raytrace.sceneObjects;

import java.io.*;

import math.*;
import optics.raytrace.core.*;
import optics.raytrace.surfaces.MaxwellFisheyeLensSurface;


/**
 * Maxwell fisheye lens --- a sphere that images any point on its surface to the opposite point on the surface.
 * All the hard work is actually done by a special surface class called MaxwellFisheyeLensSurface.
 * 
 * @author Johannes
 *
 */

public class MaxwellFisheyeLens extends Sphere implements Serializable
{	
	private static final long serialVersionUID = -1344378349958517985L;

	// constructor
	public MaxwellFisheyeLens(
			String description, Vector3D centre, double radius,
			double ratioNSurfaceNSurrounding,
			double transparentTunnelRadius,
			double transmissionCoefficient,
			boolean shadowThrowing,
			SceneObject parent,
			Studio studio
		)
	{
		super(description, centre, radius, new MaxwellFisheyeLensSurface(ratioNSurfaceNSurrounding, transparentTunnelRadius, transmissionCoefficient, shadowThrowing), parent, studio);
	}
	
	/**
	 * Create a clone of the original
	 * @param original
	 */
	public MaxwellFisheyeLens(MaxwellFisheyeLens original)
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
	public MaxwellFisheyeLens clone()
	{
		return new MaxwellFisheyeLens(this);
	}

	@Override
	public MaxwellFisheyeLens transform(Transformation t)
	{
		return new MaxwellFisheyeLens(
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
		return "<MaxwellFisheye>\n" +
		"\t<centre Vector3D="+getCentre()+">\n" + 
		"\t<radius double="+getRadius()+">\n" + 
		"\t<ratioNSurfaceNSurrounding double="+getRatioNSurfaceNSurrounding()+">\n" +
		"\t<transparentTunnelRadius double="+getTransparentTunnelRadius()+">\n" +
		"\t<transmissionCoefficient double="+getTransmissionCoefficient()+">\n" +
		"</EatonLens>\n";
	}
	
	public void setTransmissionCoefficient(double transmissionCoefficient)
	{
		((MaxwellFisheyeLensSurface)getSurfaceProperty()).setTransmissionCoefficient(transmissionCoefficient);
	}
	
	public double getTransmissionCoefficient()
	{
		return ((MaxwellFisheyeLensSurface)getSurfaceProperty()).getTransmissionCoefficient();
	}

	public void setRatioNSurfaceNSurrounding(double ratioNSurfaceNSurrounding)
	{
		((MaxwellFisheyeLensSurface)getSurfaceProperty()).setRatioNSurfaceNSurrounding(ratioNSurfaceNSurrounding);
	}
	
	public double getRatioNSurfaceNSurrounding()
	{
		return ((MaxwellFisheyeLensSurface)getSurfaceProperty()).getRatioNSurfaceNSurrounding();
	}

	public void setTransparentTunnelRadius(double transparentTunnelRadius)
	{
		((MaxwellFisheyeLensSurface)getSurfaceProperty()).setTransparentTunnelRadius(transparentTunnelRadius);
	}
	
	public double getTransparentTunnelRadius()
	{
		return ((MaxwellFisheyeLensSurface)getSurfaceProperty()).getTransparentTunnelRadius();
	}
	
	@Override
	public String getType()
	{
		return "Maxwell-fisheye lens";
	}
}
