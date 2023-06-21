package optics.rayplay.geometry2D;

import math.Vector2D;
import optics.rayplay.core.LightRay2D;
import optics.rayplay.core.OpticalComponent2D;
import optics.rayplay.core.RayComponentIntersection2D;

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
		
	
	public void setEndPoints(Vector2D a, Vector2D b)
	{
		this.a = a;
		this.b = b;
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
	
	public Vector2D calculateIntersection(LightRay2D r, boolean forwardOnly)
	{
		// s1 from a1 to b1, s2 from a2 to b2
		// define line directions d1 = b1 - a1, d2 = b2 - a2
		// a1 + alpha1 d1 = a2 + alpha2 d2
		// scalar product with d1: a1.d1 + alpha1 d1.d1 = a2.d1 + alpha2 d2.d1  (1)
		// scalar product with d2: a1.d2 + alpha1 d1.d2 = a2.d2 + alpha2 d2.d2  (2)
		// solution:
		// alpha1 = -((a1d2 d1d2 - a2d2 d1d2 - a1d1 d2d2 + a2d1 d2d2)/(d1d2^2 - d1d1 d2d2))
		// alpha2 = -((a1d2 d1d1 - a2d2 d1d1 - a1d1 d1d2 + a2d1 d1d2)/(d1d2^2 - d1d1 d2d2))
		Vector2D a1 = a;
		Vector2D d1 = getA2B();
		Vector2D a2 = r.getStartPoint();
		Vector2D d2 = r.getNormalisedDirection();
		double a1d1 = Vector2D.scalarProduct(a1, d1);
		double a1d2 = Vector2D.scalarProduct(a1, d2);
		double a2d1 = Vector2D.scalarProduct(a2, d1);
		double a2d2 = Vector2D.scalarProduct(a2, d2);
		double d1d1 = Vector2D.scalarProduct(d1, d1);
		double d1d2 = Vector2D.scalarProduct(d1, d2);
		double d2d2 = Vector2D.scalarProduct(d2, d2);
		
		// denominator = 
		double denominator = d1d2*d1d2 - d1d1*d2d2;
		
		if(denominator != 0.0)
		{
			// the ray is not parallel to the line segment
		
			double alpha1 = (a1d1*d2d2 - a1d2*d1d2 - a2d1*d2d2 + a2d2*d1d2)/denominator;
			if((0 <= alpha1) && (alpha1 <= 1.0))
			{
				// the intersection is within the line segment
				
				double alpha2 = (a2d2*d1d1 - a2d1*d1d2 - a1d2*d1d1 + a1d1*d1d2)/denominator;
				if((0 <= alpha2) || !forwardOnly)
				{
					// the intersection is with the actual ray, not its backwards continuation

					// return the intersection point, i.e. either a1 + alpha1 d1 or a2 + alpha2 d2
					return Vector2D.sum(
									a1, 
									d1.getProductWith(alpha1)	// position
							);
				}
			}
		}
		
		// no intersection
		return null;
	}


}
