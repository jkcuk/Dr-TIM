package optics.raytrace.sceneObjects;

import java.io.*;

import math.*;
import optics.raytrace.core.*;
import optics.raytrace.surfaces.LuneburgLensSurface;


/**
 * Luneburg lens --- a sphere that bends light in a particular way.
 * All the hard work is actually done by a special surface class called LuneburgLensSurface.
 * 
 * @author Johannes
 *
 */

public class LuneburgLens extends Sphere implements Serializable
{	
	private static final long serialVersionUID = 5355170317227572391L;

	// constructor
	public LuneburgLens(
			String description, Vector3D centre, double radius,
			double ratioNSurfaceNSurrounding,
			double transparentTunnelRadius,
			double transmissionCoefficient,
			boolean shadowThrowing,
			SceneObject parent,
			Studio studio
		)
	{
		super(description, centre, radius, new LuneburgLensSurface(ratioNSurfaceNSurrounding, transparentTunnelRadius, transmissionCoefficient, shadowThrowing), parent, studio);
	}
	
	/**
	 * Create a clone of the original
	 * @param original
	 */
	public LuneburgLens(LuneburgLens original)
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
	public LuneburgLens clone()
	{
		return new LuneburgLens(this);
	}

	@Override
	public LuneburgLens transform(Transformation t)
	{
		return new LuneburgLens(
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
		return "<LuneburgLens>\n" +
		"\t<centre Vector3D="+getCentre()+">\n" + 
		"\t<radius double="+getRadius()+">\n" + 
		"\t<ratioNSurfaceNSurrounding double="+getRatioNSurfaceNSurrounding()+">\n" +
		"\t<transparentTunnelRadius double="+getTransparentTunnelRadius()+">\n" +
		"\t<transmissionCoefficient double="+getTransmissionCoefficient()+">\n" +
		"</EatonLens>\n";
	}
	
	public void setTransmissionCoefficient(double transmissionCoefficient)
	{
		((LuneburgLensSurface)getSurfaceProperty()).setTransmissionCoefficient(transmissionCoefficient);
	}
	
	public double getTransmissionCoefficient()
	{
		return ((LuneburgLensSurface)getSurfaceProperty()).getTransmissionCoefficient();
	}

	public void setRatioNSurfaceNSurrounding(double ratioNSurfaceNSurrounding)
	{
		((LuneburgLensSurface)getSurfaceProperty()).setRatioNSurfaceNSurrounding(ratioNSurfaceNSurrounding);
	}
	
	public double getRatioNSurfaceNSurrounding()
	{
		return ((LuneburgLensSurface)getSurfaceProperty()).getRatioNSurfaceNSurrounding();
	}

	public void setTransparentTunnelRadius(double transparentTunnelRadius)
	{
		((LuneburgLensSurface)getSurfaceProperty()).setTransparentTunnelRadius(transparentTunnelRadius);
	}
	
	public double getTransparentTunnelRadius()
	{
		return ((LuneburgLensSurface)getSurfaceProperty()).getTransparentTunnelRadius();
	}
	
	@Override
	public String getType()
	{
		return "Luneburg lens";
	}
}
