package optics.raytrace.sceneObjects;

import math.*;
import optics.raytrace.core.*;

public class ParametrisedInvertedSphere extends ParametrisedSphere
{
	private static final long serialVersionUID = -311435975242598610L;

	/**
	 * @param description
	 * @param c	centre of the sphere
	 * @param r	radius
	 * @param pole	direction from the centre to the north pole
	 * @param phi0Direction	direction from the centre to the intersection between zero-degree meridian and equator
	 * @param surfaceProperty	surface properties
	 */
	public ParametrisedInvertedSphere(String description,
			Vector3D c,
			double r,
			Vector3D pole,
			Vector3D phi0Direction,
			SurfaceProperty surfaceProperty,
			SceneObject parent, 
			Studio studio
	)
	{
		super(description, c, r, pole, phi0Direction, surfaceProperty, parent, studio);
	}

	/**
	 * Constructor that uses standard directions.
	 * 
	 * @param description
	 * @param c	centre of the sphere
	 * @param r	radius
	 * @param surfaceProperty	surface properties
	 */
	public ParametrisedInvertedSphere(
			String description,
			Vector3D c,
			double r,
			SurfaceProperty surfaceProperty,
			SceneObject parent, 
			Studio studio
	)
	{
		super(description, c, r, surfaceProperty, parent, studio);
	}
	
	/**
	 * Create a clone of the original
	 * @param original
	 */
	public ParametrisedInvertedSphere(ParametrisedInvertedSphere original)
	{
		super(original);
	}
	
	/* (non-Javadoc)
	 * @see optics.raytrace.sceneObjects.Sphere#clone()
	 */
	@Override
	public ParametrisedInvertedSphere clone()
	{
		return new ParametrisedInvertedSphere(this);
	}
	
	@Override
	public Vector3D getNormalisedOutwardsSurfaceNormal(Vector3D position)
	{
		return super.getNormalisedOutwardsSurfaceNormal(position).getReverse();
	}
	
	@Override
	public boolean insideObject(Vector3D position)
	{
		return !(super.insideObject(position));
	}
	
	@Override
	public RaySceneObjectIntersection getClosestRayIntersection(Ray ray)
	{
		RaySceneObjectIntersection i = super.getClosestRayIntersection(ray);
		if(i != RaySceneObjectIntersection.NO_INTERSECTION) i.o = this;
		return i;
	}

		
	@Override
	public ParametrisedInvertedSphere transform(Transformation t)
	{
		return new ParametrisedInvertedSphere(
				description,
				t.transformPosition(getCentre()),
				getRadius(),
				pole,
				phi0Direction,
				getSurfaceProperty(),
				getParent(),
				getStudio()
		);
	}

	@Override
	public String toString() {
		return "<ParametrisedInvertedSphere>\n" + 
		"\tcentre = " + getCentre() + "\n" + 
		"\tradius = " + getRadius() + "\n" + 
		"<ParametrisedInvertedSphere>";
	}
	
	@Override
	public String getType()
	{
		return "Inverted sphere";
	}
}
