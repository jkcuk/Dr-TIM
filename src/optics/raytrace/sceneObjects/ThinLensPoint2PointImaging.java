package optics.raytrace.sceneObjects;

import math.Vector3D;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.Studio;
import optics.raytrace.surfaces.Point2PointImagingPhaseHologram;

public class ThinLensPoint2PointImaging extends Disc
{
	private static final long serialVersionUID = -1738399285135454615L;

	private double focalLength;
	
	public ThinLensPoint2PointImaging(String description, Vector3D centre, Vector3D normal, double radius, double focalLength, double transmissionCoefficient, boolean shadowThrowing, SceneObject parent, Studio studio)
	{
		super(
				description,
				centre,
				normal,
				radius,
				new Point2PointImagingPhaseHologram(
						new Vector3D(0,0,0),	// dummy objectPosition --- set below
						new Vector3D(0,0,0),	// dummy imagePosition --- set below
						transmissionCoefficient,	// throughputCoefficient
						false,	// reflecting? no, transmitting
						shadowThrowing	// shadow-throwing
					),
				parent,
				studio
			);
		
		// set the focal length, which also sets the object and image positions
		setFocalLength(focalLength);
	}

	public ThinLensPoint2PointImaging(ThinLensPoint2PointImaging original)
	{
		this(
				original.getDescription(),
				original.getCentre(),
				original.getNormal(),
				original.getRadius(),
				original.getFocalLength(),
				original.getTransmissionCoefficient(),
				original.isShadowThrowing(),
				original.getParent(),
				original.getStudio()
		);
//		super(original);
//		setFocalLength(original.getFocalLength());
//		setTransmissionCoefficient(original.getTransmissionCoefficient());
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.sceneObjects.Plane#clone()
	 */
	@Override
	public ThinLensPoint2PointImaging clone()
	{
		return new ThinLensPoint2PointImaging(this);
	}
	
	public Vector3D calculateInsideSpacePoint()
	{
		// For a positive focal length, the point in the inside space must lie a distance 2 f from the surface, on the inside.
		// Therefore, as the normal points outwards, 
		return Vector3D.sum(getCentre(), getNormal().getWithLength(-2*focalLength));
	}

	public Vector3D calculateOutsideSpacePoint()
	{
		// For a positive focal length, the point in the outside space must lie a distance 2 f from the surface, on the outside.
		// Therefore, as the normal points outwards, 
		return Vector3D.sum(getCentre(), getNormal().getWithLength(2*focalLength));
	}

	public double getFocalLength() {
		return focalLength;
	}

	public void setFocalLength(double focalLength) {
		this.focalLength = focalLength;
		// re-calculate the object and image positions and alter the surface property accordingly
		((Point2PointImagingPhaseHologram)getSurfaceProperty()).setInsideSpacePoint(calculateInsideSpacePoint());
		((Point2PointImagingPhaseHologram)getSurfaceProperty()).setOutsideSpacePoint(calculateOutsideSpacePoint());
	}
	
	public double getTransmissionCoefficient() {
		return ((Point2PointImagingPhaseHologram)getSurfaceProperty()).getTransmissionCoefficient();
	}

	public void setTransmissionCoefficient(double transmissionCoefficient) {
		((Point2PointImagingPhaseHologram)getSurfaceProperty()).setTransmissionCoefficient(transmissionCoefficient);
	}
	
	@Override
	public String getType()
	{
		return "Thin lens";
	}
}
