package optics.raytrace.sceneObjects;

import math.Vector3D;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.Studio;
import optics.raytrace.surfaces.ComplexThinLensHologram;

public class ComplexThinLens extends Disc
{
	// This is the object code for an idealised complex this lens that is constructed from two
	// rotation vectors travelling in and out of the lens. These rotation vectors are then added
	// to obtain an angle factor, which is used to rotate the light around the optical axis by
	// a certain angle.

	private static final long serialVersionUID = 2122361890824174065L;

	public ComplexThinLens(String description, Vector3D lensCentre, Vector3D normal, double radius, double angleFactor, double transmissionCoefficient, boolean shadowThrowing, SceneObject parent, Studio studio)
	{
		super(
				description,
				lensCentre,
				normal,
				radius,
				new ComplexThinLensHologram(
					lensCentre,	// lensCentre,
					angleFactor,
					transmissionCoefficient,
					shadowThrowing
				),
				parent,
				studio
			);

	}
	
	public ComplexThinLens(ComplexThinLens original)
	{
		this(
				original.getDescription(),
				original.getCentre(),
				original.getNormal(),
				original.getRadius(),
				original.getAngleFactor(),
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
	public ComplexThinLens clone()
	{
		return new ComplexThinLens(this);
	}

	public void setLensCentre(Vector3D centre)
	{
		super.setCentre(centre);
		((ComplexThinLensHologram)getSurfaceProperty()).setLensCentre(centre);
	}
	
	public Vector3D getLensCentre()
	{
		return super.getCentre();
	}
	
	@Override
	public void setCentre(Vector3D centre)
	{
		System.err.println("ComplexThinLens::setCentre: Use setLensCentre instead.");
		System.exit(-1);
	}

	@Override
	public Vector3D getCentre()
	{
		System.err.println("ComplexThinLens::getCentre: Use getLensCentre instead.");
		System.exit(-1);
		return new Vector3D(0,0,0);
	}

	public double getAngleFactor()
	{
		return ((ComplexThinLensHologram)getSurfaceProperty()).getAngleFactor();
	}
		
	public void setAngleFactor(double angleFactor)
	{
		((ComplexThinLensHologram)getSurfaceProperty()).setAngleFactor(angleFactor);
	}
	
	public double getTransmissionCoefficient()
	{
		return ((ComplexThinLensHologram)getSurfaceProperty()).getTransmissionCoefficient();
	}

	public void setTransmissionCoefficient(double transmissionCoefficient)
	{
		((ComplexThinLensHologram)getSurfaceProperty()).setTransmissionCoefficient(transmissionCoefficient);
	}
	
	@Override
	public String getType()
	{
		return "Complex thin lens";
	}



}
