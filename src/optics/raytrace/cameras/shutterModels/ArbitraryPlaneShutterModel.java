package optics.raytrace.cameras.shutterModels;

import math.Geometry;
import math.LorentzTransform;
import math.Vector3D;

/**
 * @author johannes
 *
 * Shutter model in which all rays pass through the specified plane at the same time, namely the shutter-opening time.
 * 
 * The shutter plane is specified by a point in the shutter plane and the normal to the shutter plane
 */
public class ArbitraryPlaneShutterModel extends InstantShutterModel
{
	private static final long serialVersionUID = 6638446088710547411L;

	// details of the plane for the arbitrary-plane shutter model, which is specified by a point in the shutter plane
	// and the normal to the shutter plane
	
	/**
	 * point in the shutter plane
	 */
	protected Vector3D pointInShutterPlane;
	
	/**
	 * (normalised) normal to the shutter plane
	 */
	protected Vector3D normalToShutterPlane;
	
	//
	// constructor
	//

	/**
	 * Create an arbitrary-plane shutter model (in which the shutter is located in an arbitrary, but given, plane)
	 * that represents a shutter opening at a specific shutter-opening time.
	 * The shutter plane is specified by a point in the shutter plane and the normal to the shutter plane.
	 * @param pointInShutterPlane	the point in the shutter plane
	 * @param normalToShutterPlane	direction normal to the shutter plane (before normalisation)
	 * @param shutterOpeningTime	the shutter-opening time
	 */
	public ArbitraryPlaneShutterModel(Vector3D pointInShutterPlane, Vector3D normalToShutterPlane, double shutterOpeningTime)
	{
		super(shutterOpeningTime);
		setPointInShutterPlane(pointInShutterPlane);
		setNormalToShutterPlane(normalToShutterPlane);
	}
	
	@Override
	public ArbitraryPlaneShutterModel clone()
	{
		return new ArbitraryPlaneShutterModel(getPointInShutterPlane(), getNormalToShutterPlane(), getShutterOpeningTime());
	}

	
	//
	// getters & setters
	//


	/**
	 * The shutter plane is specified by a point in the shutter plane and the normal to the shutter plane.
	 * This method returns the point in the shutter plane.
	 * @return	the point in the shutter plane
	 */
	public Vector3D getPointInShutterPlane() {
		return pointInShutterPlane;
	}

	/**
	 * The shutter plane is specified by a point in the shutter plane and the normal to the shutter plane.
	 * This method sets the point in the shutter plane.
	 * @param pointInShutterPlane	the point in the shutter plane
	 */
	public void setPointInShutterPlane(Vector3D pointInShutterPlane) {
		this.pointInShutterPlane = pointInShutterPlane;
	}

	/**
	 * The shutter plane is specified by a point in the shutter plane and the normal to the shutter plane.
	 * This method returns the (normalised) shutter-plane normal.
	 * @return	the normal to the shutter plane
	 */
	public Vector3D getNormalToShutterPlane() {
		return normalToShutterPlane;
	}

	/**
	 * The shutter plane is specified by a point in the shutter plane and the normal to the shutter plane.
	 * This method sets the shutter-plane normal to the given vector, normalised.
	 * @param normalToShutterPlane	direction normal to the shutter plane (before normalisation)
	 */
	public void setNormalToShutterPlane(Vector3D normalToShutterPlane) {
		this.normalToShutterPlane = normalToShutterPlane.getNormalised();
	}

	
	//
	// implement abstract ShutterModel methods
	//

	@Override
	public double getAperturePlaneTransmissionTime(Vector3D pointOnEntrancePupil, Vector3D pixelImagePosition,
			boolean pixelImagePositionInFront)
	{
		// in this shutter model, all rays pass through the arbitrary plane at the same time, namely the shutter-opening time

		// a vector from the point on the entrance pupil to the pixel-image position
		Vector3D pointOnPupil2Image = pixelImagePosition.getDifferenceWith(pointOnEntrancePupil);

		Vector3D normalisedPhysicalRayDirection = pointOnPupil2Image.getWithLength(pixelImagePositionInFront?-1:1);
		double lengthToShutter = Geometry.getFactorToLinePlaneIntersection(
				pointOnEntrancePupil,	// pointOnLine
				normalisedPhysicalRayDirection,	// directionOfLine, here the normalised physical ray direction
				pointInShutterPlane,	// pointOnPlane
				normalToShutterPlane	// normalToPlane
			);
		// check that the length is correct
//		System.out.println("should be zero: " + Vector3D.scalarProduct(
//				Vector3D.difference(
//						Vector3D.sum(pointOnEntrancePupil, normalisedPhysicalRayDirection.getProductWith(lengthToShutter)),
//						pointInShutterPlane
//					),
//				normalToShutterPlane
//			));
		// the time the light ray passes through the entrance pupil is then
		return getShutterOpeningTime() - lengthToShutter / LorentzTransform.c;
//		t = getShutterOpeningTime() - Geometry.getFactorToLinePlaneIntersection(
//				pointOnEntrancePupil,	// pointOnLine
//				pointOnPupil2Image.getWithLength(pixelImagePositionInFront?1:-1),	// directionOfLine, here the normalised physical ray direction
//				pointOnShutterPlane,	// pointOnPlane
//				normalToShutterPlane	// normalToPlane
//			)/LorentzTransform.c;
		// System.out.println("RelativisticAnyFocusSurfaceCamera::getRay: normalToShutterPlane="+normalToShutterPlane+", t="+t);
	}
	
	/* (non-Javadoc)
	 * @see optics.raytrace.cameras.shutterModels.ShutterModel#getShutterModelType()
	 */
	@Override
	public ShutterModelType getShutterModelType()
	{
		return ShutterModelType.ARBITRARY_PLANE_SHUTTER;
	}
}
