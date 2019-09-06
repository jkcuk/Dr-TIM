package optics.raytrace.surfaces;

import math.Vector3D;

/**
 * @author johannes
 * This surface property simulates the light-ray direction change due to diffraction by a rectangular pixel.
 */
public class RectangularPixelDiffraction extends Diffusion
{
	private static final long serialVersionUID = 6023321133278951887L;

	public RectangularPixelDiffraction()
	{
		super();
	}
	
	public RectangularPixelDiffraction(RectangularPixelDiffraction original)
	{
		super();
	}
	
	@Override
	public RectangularPixelDiffraction clone()
	{
		return new RectangularPixelDiffraction(this);
	}
	
	//
	// Diffusion methods
	//

	/* (non-Javadoc)
	 * @see optics.raytrace.surfaces.Diffusion#calculateDirectionChange(math.Vector3D, math.Vector3D)
	 */
	@Override
	public Vector3D calculateDirectionChange(
			Vector3D incidentNormalisedRayDirection,
			Vector3D normalisedOutwardsSurfaceNormal
		)
	{
		// TODO
		return null;
	}
	

	
}
