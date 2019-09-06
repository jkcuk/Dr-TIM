package optics.raytrace.sceneObjects.transformations;

import optics.raytrace.core.Transformation;
import math.Vector3D;

public class SimpleTranslation extends Transformation
{
	protected Vector3D t;
	
	public SimpleTranslation(Vector3D t)
	{
		super();
		this.t = t;
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.Transformation#transformDirection(optics.raytrace.Vector3D)
	 */
	@Override
	public Vector3D transformDirection(Vector3D d)
	{
		return d;
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.Transformation#transformPosition(optics.raytrace.Vector3D)
	 */
	@Override
	public Vector3D transformPosition(Vector3D p)
	{
		return p.getSumWith(t);
	}

}
