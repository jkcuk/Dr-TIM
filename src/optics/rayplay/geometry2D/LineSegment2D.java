package optics.rayplay.geometry2D;

import math.Vector2D;

/**
 * A line segment connecting points a and b.
 * The points a and b through which the underlying line passes become the end points of the line segment.
 * 
 * @author johannes
 *
 */
public class LineSegment2D extends Line2D
{	
	/**
	 * create line segment connecting points a and b
	 * @param a
	 * @param b
	 */
	public LineSegment2D(
			Vector2D a,
			Vector2D b
		)
	{
		super(a, b);
	}
		
	
	
	// useful methods
	
	public Vector2D getMidpoint()
	{
		return Vector2D.sum(a, b).getProductWith(0.5);
	}
	
	public boolean passesThroughPoint(Vector2D p)
	{
		return Geometry2D.isPointOnLineSegment(this, p);
	}

}
