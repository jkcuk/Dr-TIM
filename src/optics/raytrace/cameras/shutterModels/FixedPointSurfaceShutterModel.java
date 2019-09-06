package optics.raytrace.cameras.shutterModels;

import math.LorentzTransform;
import math.Vector3D;

/**
 * @author johannes
 *
 * A non-synchronous shutter model in which the shutter is placed either in the detector plane or in the focus surface.
 * The times when different positions on the shutter surface are transparent are chosen such that the Lorentz transformation
 * of the event of a picture ray passing through any point on the focus surface leaves the position unaltered.
 * The focus surface then looks undistorted in the resulting photo.
 */
public class FixedPointSurfaceShutterModel extends ShutterModel
{
	private static final long serialVersionUID = 7271431941893243862L;
	
	/**
	 * the velocity of the camera in the scene frame, divided by c
	 */
	Vector3D beta;

	/**
	 * Creates a fixed-point-surface shutter model
	 */
	public FixedPointSurfaceShutterModel(Vector3D beta)
	{
		super();
		setBeta(beta);
	}
	
	@Override
	public FixedPointSurfaceShutterModel clone()
	{
		return new FixedPointSurfaceShutterModel(getBeta().clone());
	}

	
	//
	// setters & getters
	//
	
	public Vector3D getBeta() {
		return beta;
	}

	public void setBeta(Vector3D beta) {
		this.beta = beta;
	}

	
	/**
	 * In the equation for the spatial part of a Lorentz-transformed event, set that Lorentz-transformed position
	 * to the original position and solve for the time <i>t</i> for which this is satisfied
	 * @param position
	 * @return	the time when the Lorentz transformation doesn't change the position
	 */
	public double getTimeWhenPositionIsStationary(Vector3D position)
	{
		double gamma = LorentzTransform.getGamma(beta);
		double beta2 = Vector3D.scalarProduct(beta, beta);

		return (1-gamma)*Vector3D.scalarProduct(beta, position)/(LorentzTransform.c*gamma*beta2);
	}
	
	//
	// implement abstract ShutterModel methods
	//

	@Override
	public double getAperturePlaneTransmissionTime(Vector3D pointOnEntrancePupil, Vector3D pixelImagePosition,
			boolean pixelImagePositionInFront)
	{
		// the pixel-image position 

		// a vector from the point on the entrance pupil to the pixel-image position
		Vector3D pointOnPupil2Image = pixelImagePosition.getDifferenceWith(pointOnEntrancePupil);

		// calculate the corresponding time when the ray is at the point on the entrance pupil
		return getTimeWhenPositionIsStationary(pixelImagePosition) + (pixelImagePositionInFront?1:-1) * pointOnPupil2Image.getLength() / LorentzTransform.c;
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.cameras.shutterModels.ShutterModel#getShutterModelType()
	 */
	@Override
	public ShutterModelType getShutterModelType()
	{
		return ShutterModelType.FIXED_POINT_SURFACE_SHUTTER;
	}
}
