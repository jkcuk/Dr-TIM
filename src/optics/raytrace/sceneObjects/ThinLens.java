package optics.raytrace.sceneObjects;

import math.Vector3D;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.Studio;
import optics.raytrace.surfaces.IdealThinLensSurfaceSimple;

public class ThinLens extends Disc
{
	private static final long serialVersionUID = -8452720833427405264L;

	public ThinLens(String description, Vector3D centre, Vector3D normal, double radius, double focalLength, double transmissionCoefficient, boolean shadowThrowing, SceneObject parent, Studio studio)
	{
		super(
				description,
				centre,
				normal,
				radius,
				new IdealThinLensSurfaceSimple(
					centre,	// lensCentre,
					normal,	// opticalAxisDirection,
					focalLength,
					transmissionCoefficient,
					shadowThrowing
				),
				parent,
				studio
			);
		
		// set the focal length, which also sets the object and image positions
		setFocalLength(focalLength);
	}

	public ThinLens(ThinLens original)
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
	public ThinLens clone()
	{
		return new ThinLens(this);
	}

	@Override
	public void setCentre(Vector3D centre)
	{
		super.setCentre(centre);
		((IdealThinLensSurfaceSimple)getSurfaceProperty()).setLensCentre(centre);
	}

	@Override
	public void setNormal(Vector3D normal)
	{
		super.setNormal(normal);
		((IdealThinLensSurfaceSimple)getSurfaceProperty()).setOpticalAxisDirection(normal);
	}
	
	public double getFocalLength() {
		return ((IdealThinLensSurfaceSimple)getSurfaceProperty()).getFocalLength();
	}

	public void setFocalLength(double focalLength)
	{
		((IdealThinLensSurfaceSimple)getSurfaceProperty()).setFocalLength(focalLength);
	}
	
	public double getTransmissionCoefficient() {
		return ((IdealThinLensSurfaceSimple)getSurfaceProperty()).getTransmissionCoefficient();
	}

	public void setTransmissionCoefficient(double transmissionCoefficient) {
		((IdealThinLensSurfaceSimple)getSurfaceProperty()).setTransmissionCoefficient(transmissionCoefficient);
	}
	
	@Override
	public String getType()
	{
		return "Thin lens";
	}
}
