package optics.raytrace.cameras.shutterModels;

import java.io.Serializable;

import math.Vector3D;

/**
 * Shutter model for relativistic cameras.
 * 
 * The shutter model calculates the time when a backwards-traced ray passes through the camera's entrance pupil.
 * This depends on the location of the shutter, when the shutter opens, etc.
 * 
 * The time of the "event" of a backwards-traced ray encountering the last object in the camera scene
 * (i.e. the collection of objects at rest relative to the camera) is relevant as it is this event that
 * gets Lorentz-transformed from the camera frame into the scene frame.

 * @author johannes
 * 
 */
public abstract class ShutterModel implements Serializable
{
	private static final long serialVersionUID = 5205809539986183448L;

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public abstract ShutterModel clone();
	
	/**
	 * Calculate the time when a backwards-traced light ray passes through the camera's entrance pupil.
	 * @param pointOnEntrancePupil	position where the light ray passes through the entrance pupil
	 * @param pixelImagePosition	position that is imaged to the relevant detector pixel
	 * @param pixelImagePositionInFront	is the pixel-image position in front of the entrance pupil?
	 * @return	time when the light ray passes through the camera's entrance pupil
	 */
	public abstract double getAperturePlaneTransmissionTime(Vector3D pointOnEntrancePupil, Vector3D pixelImagePosition, boolean pixelImagePositionInFront);

	/**
	 * @return	the shutter-model type
	 */
	public abstract ShutterModelType getShutterModelType();	

	@Override
	public String toString() {
		return getShutterModelType().toString();
	}
}
