package optics.raytrace.cameras.shutterModels;

import math.LorentzTransformation;
import math.Vector3D;
import optics.raytrace.cameras.RelativisticAnyFocusSurfaceCamera;

/**
 * @author johannes
 *
 * Shutter model in which all rays pass through the detector plane at the same time, namely the shutter-opening time
 */
public class DetectorPlaneShutterModel extends InstantShutterModel
{
	private static final long serialVersionUID = -9164550889687155997L;

	/**
	 * type of camera lens; this determines the timing of the rays
	 */
	protected LensType lensType;

    /**
     * The distance the detector plane is behind the entrance pupil
     */
    protected double detectorDistance;

	/**
	 * the camera this shutter model is associated with; this is required to extract necessary geometry parameters
	 */
	protected RelativisticAnyFocusSurfaceCamera camera;
	
	
	//
	// constructor
	//

	/**
	 * Create a detector-plane shutter model (in which the shutter is located in the plane of the detector)
	 * that represents a shutter opening at a specific time
	 * @param lensType
	 * @param detectorDistance
	 * @param camera
	 * @param shutterOpeningTime	the shutter-opening time
	 */
	public DetectorPlaneShutterModel(LensType lensType, double detectorDistance, RelativisticAnyFocusSurfaceCamera camera, double shutterOpeningTime)
	{
		super(shutterOpeningTime);
		
		setLensType(lensType);
		setDetectorDistance(detectorDistance);
		setCamera(camera);
	}
	
	@Override
	public DetectorPlaneShutterModel clone()
	{
		return new DetectorPlaneShutterModel(getLensType(), getDetectorDistance(), getCamera(), getShutterOpeningTime());
	}
	
	
	//
	// setters & getters
	//

	/**
	 * @return	the type of camera lens
	 */
	public LensType getLensType() {
		return lensType;
	}

	/**
	 * @param lensType	the type of camera lens
	 */
	public void setLensType(LensType lensType) {
		this.lensType = lensType;
	}

	public double getDetectorDistance() {
		return detectorDistance;
	}

	public void setDetectorDistance(double detectorDistance) {
		this.detectorDistance = detectorDistance;
	}

	/**
	 * this shutter model is associated with a camera, which is required to extract necessary geometry parameters
	 * @return	the camera this shutter model is associated with
	 */
	public RelativisticAnyFocusSurfaceCamera getCamera() {
		return camera;
	}

	/**
	 * this shutter model is associated with a camera, which is required to extract necessary geometry parameters
	 * @param camera	the camera this shutter model is associated with
	 */
	public void setCamera(RelativisticAnyFocusSurfaceCamera camera) {
		this.camera = camera;
	}


	//
	// implement abstract ShutterModel methods
	//

	@Override
	public double getAperturePlaneTransmissionTime(Vector3D pointOnLensAperture, Vector3D pixelImagePosition,
			boolean pixelImagePositionInFront)
	{
		// a vector from the point on the lens aperture to the pixel-image position
		Vector3D pointOnPupil2Image = pixelImagePosition.getDifferenceWith(pointOnLensAperture);
		
		// calculate the position of the detector pixel
		
		// vector from centre of lens aperture to pixel-image position
		Vector3D ci = Vector3D.difference(pixelImagePosition, camera.getApertureCentre());
		// component of vector ci in view direction
		double ciV = Vector3D.scalarProduct(ci, camera.getViewDirection().getNormalised());
		
		// vector from detector-pixel position to centre of lens aperture
		Vector3D pc = ci.getProductWith(getDetectorDistance() / ciV);
		
		// detector-pixel position
		Vector3D p = Vector3D.difference(camera.getApertureCentre(), pc);

		// vector from the detector-pixel position to the point on the lens aperture, e
		Vector3D pe = Vector3D.difference(pointOnLensAperture, p);

		switch(lensType)
		{
		case LENS_HOLOGRAM:
			return getShutterOpeningTime() - pe.getLength() / LorentzTransformation.c;
		case IDEAL_LENS:
		default:
			// If the lens is perfectly imaging, then all light rays from the detector pixel to its image position
			// take the same time to get there.  This works by the lens introducing a position-dependent time delay,
			// deltaT, which is biggest in the centre.
			// We define here deltaT to be 0 in the lens centre, and therefore *negative* elsewhere.

			// Call the pixel position p, the point on the lens aperture e, and the image position i.
			// Then the time light takes from i to e, |ei|/c, plus the time delay, deltaT, plus the time
			// light takes from e to p, |pe|/c, has to equal the time from i to p (through the centre of the lens),
			// |pi|/c.
			// Therefore deltaT = 1/c (|pi| - |pe| - |ei|).
			Vector3D pi = Vector3D.difference(pixelImagePosition, p);
			Vector3D ei = pointOnPupil2Image;
			double deltaT = (pi.getLength() - pe.getLength() - (pixelImagePositionInFront?1:-1) * ei.getLength()) / LorentzTransformation.c;

			// the time the (backwards-traced) light ray leaves the entrance pupil is then
			return getShutterOpeningTime() - pe.getLength() / LorentzTransformation.c - deltaT;
		}
		
		// old code that I don't understand any longer
//		// vector from centre of entrance pupil to pixel-image position
//		Vector3D ci = Vector3D.difference(pixelImagePosition, getApertureCentre());
//		// component of vector ci in view direction
//		double ciV = -Vector3D.scalarProduct(ci, getViewDirection().getNormalised());
//		double ciLength = ci.getLength();
//		double piLength = ciLength * (getDetectorDistance() + ciV)/ciV;
//		t = getShutterOpeningTime() + (pixelImagePositionInFront?1:-1) * (piLength - ciLength) / LorentzTransform.c;
	}
	
	/* (non-Javadoc)
	 * @see optics.raytrace.cameras.shutterModels.ShutterModel#getShutterModelType()
	 */
	@Override
	public ShutterModelType getShutterModelType()
	{
		return ShutterModelType.DETECTOR_PLANE_SHUTTER;
	}
	
	@Override
	public String toString() {
		return getShutterModelType().toString() + ", " + lensType; // + ", " + camera;
	}
}
