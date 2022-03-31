package optics.rayplay;

import math.Vector2D;

/**
 * A line segment connecting points a and b
 * 
 * @author johannes
 *
 */
public class LineSegment2D
{	
	protected Vector2D a;
	protected Vector2D b;

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
		super();
		
		this.a = a;
		this.b = b;
	}
		
	
	
	// setters & getters
	
	public Vector2D getA() {
		return a;
	}

	public void setA(Vector2D a) {
		this.a = a;
	}

	public Vector2D getB() {
		return b;
	}

	public void setB(Vector2D b) {
		this.b = b;
	}


	
	// useful methods
	
	public Vector2D getDirection()
	{
		return Vector2D.difference(b, a);
	}
	
	/**
	 * @param normalise
	 * @return	a normal, either not normalised (specifically of length |b-a|, if normalised = false) or normalised (if normalised = true)
	 */
	public Vector2D getNormal(boolean normalise)
	{
		Vector2D d = getDirection();
		Vector2D n = new Vector2D(-d.y, d.x);
		
		if(normalise) return n.getNormalised();
		else return n;
	}
	
	/**
	 * @param s1
	 * @param s2
	 * @return	the intersection of the two line segments;
	 * null if the lines are parallel or if the intersection is outside of the segments
	 */
	public static Vector2D calculateIntersection(LineSegment2D s1, LineSegment2D s2)
	{
		// s1 from a1 to b1, s2 from a2 to b2
		// define line directions d1 = b1 - a1, d2 = b2 - a2
		// a1 + alpha1 d1 = a2 + alpha2 d2
		// scalar product with d1: a1.d1 + alpha1 d1.d1 = a2.d1 + alpha2 d2.d1  (1)
		// scalar product with d2: a1.d2 + alpha1 d1.d2 = a2.d2 + alpha2 d2.d2  (2)
		// solution:
		// alpha1 = -((a1d2 d1d2 - a2d2 d1d2 - a1d1 d2d2 + a2d1 d2d2)/(d1d2^2 - d1d1 d2d2))
		// alpha2 = -((a1d2 d1d1 - a2d2 d1d1 - a1d1 d1d2 + a2d1 d1d2)/(d1d2^2 - d1d1 d2d2))
		Vector2D a1 = s1.a;
		Vector2D d1 = s1.getDirection();
		Vector2D a2 = s2.a;
		Vector2D d2 = s2.getDirection();
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
			// the lines are not parallel
		
			double alpha1 = (a1d1*d2d2 - a1d2*d1d2 - a2d1*d2d2 + a2d2*d1d2)/denominator;
			if((0 <= alpha1) && (alpha1 <= 1.0))
			{
				// the intersection is within line segment 1
				
				double alpha2 = (a2d2*d1d1 - a2d1*d1d2 - a1d2*d1d1 + a1d1*d1d2)/denominator;
				if((0 <= alpha2) && (alpha2 <= 1.0))
				{
					// the intersection is within line segment 2

					// return the intersection point, i.e. either a1 + alpha1 d1 or a2 + alpha2 d2
					return Vector2D.sum(
							a1, 
							d1.getProductWith(alpha1)
						);
				}
			}
		}
		
		// either the lines are parallel, or the intersection does not lie in the actual segments
		return null;
	}
	
	public static double linePointDistance(LineSegment2D s, Vector2D p)
	{
		return Vector2D.scalarProduct(
				s.getNormal(true),
				Vector2D.difference(p, s.a)
			);
	}
	
	/**
	 * @param s
	 * @param p
	 * @return	true if the point p is on the infinite straight line through a and b
	 */
	public static boolean isPointOnLine(LineSegment2D s, Vector2D p)
	{
		return linePointDistance(s, p) == 0.;
	}

}
