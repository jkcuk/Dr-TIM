package optics.raytrace.cameras.shutterModels;

import math.Vector3D;

/**
 * @author johannes
 *
 * Shutter model in which all rays pass through the entrance-pupil surface at the same time, namely the shutter-opening time
 */
public class AperturePlaneShutterModel extends InstantShutterModel
{
	private static final long serialVersionUID = -8984202997694797653L;

	/**
	 * Create a entrance-pupil shutter model (in which the shutter is located in the plane of the entrance pupil)
	 * that represents a shutter opening at a specific time
	 * @param shutterOpeningTime	the shutter-opening time
	 */
	public AperturePlaneShutterModel(double shutterOpeningTime)
	{
		super(shutterOpeningTime);
	}
	
	/**
	 * Create a entrance-pupil shutter model (in which the shutter is located in the plane of the entrance pupil)
	 * that represents a shutter opening at time 0
	 */
	public AperturePlaneShutterModel()
	{
		this(0);
	}
	
	@Override
	public AperturePlaneShutterModel clone()
	{
		return new AperturePlaneShutterModel(getShutterOpeningTime());
	}


	//
	// implement abstract ShutterModel methods
	//

	@Override
	public double getAperturePlaneTransmissionTime(Vector3D pointOnEntrancePupil, Vector3D pixelImagePosition,
			boolean pixelImagePositionInFront)
	{
		// in the entrance-pupil shutter model, the shutter is located in the surface of the entrance pupil,
		// and so the time the light ray passes through the entrance pupil is simply the shutter-opening time
		return getShutterOpeningTime();
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.cameras.shutterModels.ShutterModel#getShutterModelType()
	 */
	@Override
	public ShutterModelType getShutterModelType()
	{
		return ShutterModelType.APERTURE_PLANE_SHUTTER;
	}	
}
