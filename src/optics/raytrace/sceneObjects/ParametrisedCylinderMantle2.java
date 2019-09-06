package optics.raytrace.sceneObjects;

import math.Vector3D;
import math.Vector2D;
import optics.raytrace.core.ParametrisedObject;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.Studio;
import optics.raytrace.core.SurfaceProperty;

/**
 * An attempt to make a cylinder that is parametrised in a "spiral" fashion.
 * (Works to some extent, but didn't spend any time to really get it to work.)
 * 
 * @author Johannes Courtial
 */
public class ParametrisedCylinderMantle2 extends ParametrisedCylinderMantle
implements ParametrisedObject
{
	private static final long serialVersionUID = 6799143430583608131L;

	/**
	 * @param description
	 * @param A
	 * @param a
	 * @param r
	 * @param zeroDeg
	 * @param s
	 */
	public ParametrisedCylinderMantle2(
			String description,
			Vector3D A,
			Vector3D h,
			double r,
			Vector3D zeroDeg,
			SurfaceProperty s,
			SceneObject parent,
			Studio studio)
	{
		super(description, A, h, r, zeroDeg, s, parent, studio);
	}
		
	/* (non-Javadoc)
	 * @see optics.raytrace.sceneObjects.ParametrisedCylinderMantle#clone()
	 */
	@Override
	public ParametrisedCylinderMantle2 clone()
	{
		return (ParametrisedCylinderMantle2)(super.clone());
	}

	@Override
	public Vector2D getSurfaceCoordinates(Vector3D p)
	{
		Vector2D zPhi = super.getSurfaceCoordinates(p);
		double
			z = zPhi.x,
			phi = zPhi.y;
		
		return new Vector2D(z+phi, z-phi);
	}

	@Override
	public String getType()
	{
		return "Cylinder mantle";
	}
}
