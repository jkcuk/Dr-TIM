package optics.raytrace.surfaces;

import optics.raytrace.core.Orientation;
import optics.raytrace.core.SceneObjectClass;
import math.Vector3D;

/**
 * Perform refraction like a phase hologram that images two given points into each other.
 * In the case of a planar surface, this is then the phase hologram of a lens.
 * @see optics.raytrace.surfaces.PhaseHologram
 * @author Johannes Courtial
 */
/**
 * @author johannes
 *
 */
public class Point2PointImagingPhaseHologram extends PhaseHologram
{
	private static final long serialVersionUID = -2403709669011837122L;

	/**
	 * The two points that are being imaged into each other.
	 * Note that both can be virtual, i.e. the insideSpacePoint can lie on the outside etc.
	 */
	protected Vector3D
		insideSpacePoint, outsideSpacePoint;
	
	/**
	 * Standard constructor
	 */
	public Point2PointImagingPhaseHologram(
			Vector3D insideSpacePoint,
			Vector3D outsideSpacePoint,
			double throughputCoefficient,
			boolean reflective,
			boolean shadowThrowing
		)
	{
		super(throughputCoefficient, reflective, shadowThrowing);
		setInsideSpacePoint(insideSpacePoint);
		setOutsideSpacePoint(outsideSpacePoint);
	}
	
	/**
	 * This constructor clones the original
	 * @param original
	 */
	public Point2PointImagingPhaseHologram(Point2PointImagingPhaseHologram original)
	{
		this(original.getInsideSpacePoint(), original.getOutsideSpacePoint(), original.getTransmissionCoefficient(), original.isReflective(), original.isShadowThrowing());
	}
	
	@Override
	public Point2PointImagingPhaseHologram clone()
	{
		return new Point2PointImagingPhaseHologram(this);
	}	
	
	/* (non-Javadoc)
	 * @see optics.raytrace.surfaces.PhaseHologram#getTangentialDirectionComponentChange(math.Vector3D, math.Vector3D)
	 */
	@Override
	public Vector3D getTangentialDirectionComponentChangeTransmissive(Vector3D surfacePosition, Vector3D normalisedOutwardsSurfaceNormal)
	{
		return getTangentialDirectionComponentChangeTransmissive(surfacePosition, normalisedOutwardsSurfaceNormal, insideSpacePoint, outsideSpacePoint);
	}
	
	@Override
	public Vector3D getTangentialDirectionComponentChangeReflective(
			Orientation incidentLightRayOrientation,
			Vector3D surfacePosition,
			Vector3D outwardsSurfaceNormal
		)
	{
		return getTangentialDirectionComponentChangeReflective(
				incidentLightRayOrientation,
				surfacePosition,
				outwardsSurfaceNormal,
				insideSpacePoint,
				outsideSpacePoint
			);
	}

	/**
	 * Calculate the change in the tangential direction component such that insideSpacePoint is imaged into
	 * outsideSpacePoint.
	 * If the insideSpacePoint is lying on the inside of the surface, then it is a real point
	 * (object or image), otherwise it is virtual.
	 * Similarly, if the outsideSpacePoint is lying on the outside of the surface, it is a real point, otherwise it's virtual.
	 * @param surfacePosition
	 * @param outwardsSurfaceNormal
	 * @param insideSpacePoint
	 * @param outsideSpacePoint
	 * @return
	 */
	public static Vector3D getTangentialDirectionComponentChangeTransmissive(
			Vector3D surfacePosition,
			Vector3D outwardsSurfaceNormal,
			Vector3D insideSpacePoint,
			Vector3D outsideSpacePoint
		)
	{			
		// calculate the direction change using a "model ray" that travels from the insideSpacePoint to the
		// surfacePosition and then to the outsideSpacePoint (the insideSpacePoint and the outsideSpacePoint are imaged
		// into each other by the surface; taking a ray from the outsideSpacePoint to the insideSpacePoint gives the same result)
		
		Vector3D dModel, dPrimeModel;	// incident and outgoing normalised directions of model light ray
		

		//
		// first find the in-plane component of the change in light-ray direction such that insideSpacePoint is imaged
		// to outsideSpacePoint
		//
		
		// vector from the object position to the surface point, ...
		Vector3D object2surface = Vector3D.difference(surfacePosition, insideSpacePoint);
		
		// ... making sure it is pointing outwards and is normalised
		// (If object2surface is pointing outwards, and so the insideSpacePoint is actually on the inside of the surface
		// and therefore a real object.  If it is pointing inwards, insideSpacePoint is actually on the outside of the surface
		// and therefore a virtual object; the model light ray therefore travels towards the object, not away from it.)
		dModel = SceneObjectClass.getOutwardsPointingStraightLineContinuation(
			object2surface,	// straightLineDirection
			outwardsSurfaceNormal	// outwardsNormal
		).getNormalised();
		
		// For a light ray that leaves the surface point in the direction of the image position,
		// the outgoing light-ray direction is
		Vector3D surface2image = Vector3D.difference(outsideSpacePoint, surfacePosition);

		// Making sure this is also pointing outwards
		// (if it is pointing outwards, the outsideSpacePoint is on the outside of the surface and therefore a real image;
		// if it is pointing inwards, the outsideSpacePoint is actually on the inside of the surface
		// and therefore a virtual image; the model light ray therefore travels away from the image, not towards it)
		// and is normalised as before
		dPrimeModel = SceneObjectClass.getOutwardsPointingStraightLineContinuation(
				surface2image,	// straightLineDirection
				outwardsSurfaceNormal	// outwardsNormal
			).getNormalised();
			
		// calculate and return the change in the tangential component of the light-ray direction that corresponds to
		// this pair of incident and outgoing light-ray directions
		return PhaseHologram.getTangentialDirectionComponentChange(dModel, dPrimeModel, outwardsSurfaceNormal);
	}	

	/**
	 * @param incidentLightRayOrientation	SceneObjectClass.DIRECTION_INWARDS or SceneObjectClass.DIRECTION_OUTWARDS
	 * @param surfacePosition
	 * @param outwardsSurfaceNormal
	 * @param objectPosition
	 * @param imagePosition
	 * @return
	 */
	public static Vector3D getTangentialDirectionComponentChangeReflective(
			Orientation incidentLightRayOrientation,
			Vector3D surfacePosition,
			Vector3D outwardsSurfaceNormal,
			Vector3D objectPosition,
			Vector3D imagePosition
		)
	{
		// calculate the direction change using a "model ray" that travels from the objectPoint to the
		// surfacePosition and then to the imagePoint (the objectPoint and the imagePoint are imaged
		// into each other by the surface; taking a ray from the imagePoint to the objectPoint gives the same result)
		
		Vector3D dModel, dPrimeModel;	// incident and outgoing normalised directions of model light ray
		

		//
		// first find the in-plane component of the change in light-ray direction such that insideSpacePoint is imaged
		// to outsideSpacePoint
		//
		
		// vector from the object position to the surface point, ...
		Vector3D object2surface = Vector3D.difference(surfacePosition, objectPosition);
		
		// ... making sure it is pointing outwards and is normalised
		// (If object2surface is pointing outwards, and so the insideSpacePoint is actually on the inside of the surface
		// and therefore a real object.  If it is pointing inwards, insideSpacePoint is actually on the outside of the surface
		// and therefore a virtual object; the model light ray therefore travels towards the object, not away from it.)
		dModel = SceneObjectClass.getStraightLineContinuation(
			incidentLightRayOrientation,
			object2surface,	// straightLineDirection
			outwardsSurfaceNormal	// outwardsNormal
		).getNormalised();
		
		// For a light ray that leaves the surface point in the direction of the image position,
		// the outgoing light-ray direction is
		Vector3D surface2image = Vector3D.difference(imagePosition, surfacePosition);

		// Making sure this is also pointing outwards
		// (if it is pointing outwards, the outsideSpacePoint is on the outside of the surface and therefore a real image;
		// if it is pointing inwards, the outsideSpacePoint is actually on the inside of the surface
		// and therefore a virtual image; the model light ray therefore travels away from the image, not towards it)
		// and is normalised as before
		dPrimeModel = SceneObjectClass.getStraightLineContinuation(
				Orientation.getReverseOrientation(incidentLightRayOrientation),
				surface2image,	// straightLineDirection
				outwardsSurfaceNormal	// outwardsNormal
			).getNormalised();
			
		// calculate and return the change in the tangential component of the light-ray direction that corresponds to
		// this pair of incident and outgoing light-ray directions
		return PhaseHologram.getTangentialDirectionComponentChange(dModel, dPrimeModel, outwardsSurfaceNormal);
	}	

	//
	// getters and setters
	//

	public Vector3D getPoint1() {
		return insideSpacePoint;
	}

	public void setPoint1(Vector3D point1) {
		this.insideSpacePoint = point1;
	}

	public Vector3D getPoint2() {
		return outsideSpacePoint;
	}

	public void setPoint2(Vector3D point2) {
		this.outsideSpacePoint = point2;
	}

	/**
	 * @return	the inside-space point the surface images into the corresponding point in the outside space
	 */
	public Vector3D getInsideSpacePoint() {
		return insideSpacePoint;
	}

	/**
	 * @param insideSpacePoint	the inside space point the surface images into the corresponding point in the outside space
	 */
	public void setInsideSpacePoint(Vector3D insideSpacePoint) {
		this.insideSpacePoint = insideSpacePoint;
	}

	/**
	 * @return	the outside-space point the surface images into the corresponding point in the inside space
	 */
	public Vector3D getOutsideSpacePoint() {
		return outsideSpacePoint;
	}

	/**
	 * @param outsideSpacePoint	the outside-space point the surface images into the corresponding point in the inside space
	 */
	public void setOutsideSpacePoint(Vector3D outsideSpacePoint) {
		this.outsideSpacePoint = outsideSpacePoint;
	}
} 