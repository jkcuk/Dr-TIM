package optics.rayplay.core;

import math.Vector2D;

/**
 * The intersection between a ray and an optical component.
 * 
 * @author johannes
 */
public class RayComponentIntersection2D
{
	public OpticalComponent2D o;
	public Vector2D p;

	public RayComponentIntersection2D(OpticalComponent2D o, Vector2D p)
	{
		this.o = o;
		this.p = p;
	}

	public RayComponentIntersection2D()
	{}
}
