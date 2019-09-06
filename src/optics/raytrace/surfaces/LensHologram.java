package optics.raytrace.surfaces;


import math.*;


/**
 * A surface property representing a phase hologram of a parabolic lens.
 * 
 * @author Johannes Courtial
 */
public class LensHologram extends Point2PointImagingPhaseHologram
{
	private static final long serialVersionUID = 5959003496172317107L;

	// the principal point, P, which is also the principal point
	protected Vector3D principalPoint;
	
	// a unit vector in the direction of the optical axis, pointing in the outwards direction
	protected Vector3D opticalAxisDirectionOutwards;
	
	//	focalLength
	protected double focalLength;	// focal length
	
	/**
	 * Creates an instance of a lens hologram.
	 * 
	 * @param opticalAxisDirectionOutwards	the vector aHat
	 * @param principalPoint	the point P
	 * @param focalLength
	 * @param transmissionCoefficient
	 */
	public LensHologram(
			Vector3D opticalAxisDirectionOutwards,
			Vector3D principalPoint,
			double focalLength,
			double transmissionCoefficient,
			boolean shadowThrowing
		) 
	{
		super(
				Vector3D.sum(principalPoint, opticalAxisDirectionOutwards.getWithLength(-2*focalLength)),	// insideSpacePoint
				Vector3D.sum(principalPoint, opticalAxisDirectionOutwards.getWithLength( 2*focalLength)),	// outsideSpacePoint
				transmissionCoefficient,	// throughputCoefficient
				false,	// reflective
				shadowThrowing	// shadowThrowing
			);
	}

	/**
	 * Make a clone of the original LensHologram surface property.
	 * @param original
	 */
	public LensHologram(LensHologram original)
	{
		this(
				original.getOpticalAxisDirectionOutwards(),
				original.getPrincipalPoint(),
				original.getFocalLength(),
				original.getTransmissionCoefficient(),
				original.isShadowThrowing()
			);		
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public LensHologram clone()
	{
		return new LensHologram(this);
	}
	
	// setters and getters
	
	/**
	 * Set all parameters of the IdealThinLensSurface surface property;
	 * the optical axis direction get normalised
	 * @param principalPoint
	 * @param opticalAxisDirectionOutwards
	 * @param focalLength
	 */
	public void setParameters(Vector3D principalPoint, Vector3D opticalAxisDirectionOutwards, double focalLength)
	{
//		System.out.println("IdealThinLensSurface::setParameters: principalPoint="+principalPoint+
//				", optical axis direction (+) = "+opticalAxisDirectionPos+
//				", f = "+focalLength);
		
		this.principalPoint = principalPoint;
		this.opticalAxisDirectionOutwards = opticalAxisDirectionOutwards.getNormalised();
		this.focalLength = focalLength;
	}
	
	public Vector3D getPrincipalPoint() {
		return principalPoint;
	}

	public void setPrincipalPoint(Vector3D principalPoint) {
		this.principalPoint = principalPoint;
	}

	public Vector3D getOpticalAxisDirectionOutwards() {
		return opticalAxisDirectionOutwards;
	}

	public double getFocalLength() {
		return focalLength;
	}
}
