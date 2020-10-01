package optics.raytrace.cameras.shutterModels;

import math.LorentzTransformation;
import math.Vector3D;

/**
 * @author johannes
 *
 * Shutter model in which all rays pass through the position where the lens images the pixel at the same
 * time, the shutter-opening time
 */
public class FocusSurfaceShutterModel extends InstantShutterModel
{
	private static final long serialVersionUID = 6044273268082912640L;

	/**
	 * Creates a focus-surface shutter model (in which the shutter is conceptually located in the focus surface)
	 * that represents a shutter opening at a specific shutter-opening time
	 * @param shutterOpeningTime	the shutter-opening time
	 */
	public FocusSurfaceShutterModel(double shutterOpeningTime)
	{
		super(shutterOpeningTime);
	}
	
	/**
	 * Creates a focus-surface shutter model (in which the shutter is conceptually located in the focus surface)
	 * that represents a shutter opening at shutter-opening time 0
	 */
	public FocusSurfaceShutterModel()
	{
		this(0);
	}

	@Override
	public FocusSurfaceShutterModel clone()
	{
		return new FocusSurfaceShutterModel(getShutterOpeningTime());
	}

	
	//
	// implement abstract ShutterModel methods
	//

	@Override
	public double getAperturePlaneTransmissionTime(Vector3D pointOnEntrancePupil, Vector3D pixelImagePosition,
			boolean pixelImagePositionInFront)
	{
		// in this shutter model, all rays pass through the position where the lens images the pixel at the same
		// time, the shutter-opening time
		
		// a vector from the point on the entrance pupil to the pixel-image position
		Vector3D pointOnPupil2Image = pixelImagePosition.getDifferenceWith(pointOnEntrancePupil);

		// calculate the corresponding time when the ray is at the point on the entrance pupil
		return getShutterOpeningTime() - (pixelImagePositionInFront?-1:1) * pointOnPupil2Image.getLength() / LorentzTransformation.c;
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.cameras.shutterModels.ShutterModel#getShutterModelType()
	 */
	@Override
	public ShutterModelType getShutterModelType()
	{
		return ShutterModelType.FOCUS_SURFACE_SHUTTER;
	}
}
