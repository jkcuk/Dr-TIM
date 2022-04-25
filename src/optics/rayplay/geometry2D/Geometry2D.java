package optics.rayplay.geometry2D;

import math.MyMath;
import math.Vector2D;

public class Geometry2D {
	
	public static double linePointDistance(Line2D s, Vector2D p)
	{
		return Vector2D.scalarProduct(
				s.getNormal(true),
				Vector2D.difference(p, s.a)
			);
	}
	
	/**
	 * @param l	line segment
	 * @param p	the point
	 * @return	the distance between p and the line segment
	 */
	public static double lineSementPointDistance(
			LineSegment2D l,
			Vector2D p
		)
	{
		Vector2D ab = Vector2D.difference(l.b, l.a);
		Vector2D ap = Vector2D.difference(p, l.a);
		
		// Start with
		//   a + lambda (b-a) + mu n = p,
		// or
		//   lambda (b-a) + mu n = (p-a),
		// where n is a normal to the line.  Taking the scalar product with (b-a) on both sides, and using (b-a).n = 0, gives
		//   lambda (b-a).(b-a) = (p-a).(b-a),
		// or
		//   lambda = (p-a).(b-a) / (b-a).(b-a).
		double lambda = Vector2D.scalarProduct(ap, ab) / Vector2D.scalarProduct(ab, ab);
		
		// The point
		//   pL = a + lambda (b-a)
		// is then the point on the (infinite) straight line through a and b closest to p.
		if(lambda < 0)
		{
			// lambda < 0: The distance between p and the line segment is the distance between p and a
			return Vector2D.distance(p, l.a);
		}
		else if(lambda <= 1)
		{
			// 0 <= lambda <= 1: The point pL lies on the line segment
			
			// The distance between the point and the line is then the distance between pL and p
			
			// construct a normalised normal to the line
			Vector2D nn = ab.getPerpendicularVector().getNormalised();
			
			return Math.abs(Vector2D.scalarProduct(ap, nn));
		}
		else
		{
			// lambda > 1; the distance between p and the line segment is the distance between p and b
			return Vector2D.distance(p, l.b);
		}
	}
	
	/**
	 * @param s
	 * @param p
	 * @return	true if the point p is on the infinite straight line through a and b
	 */
	public static boolean isPointOnLine(Line2D s, Vector2D p)
	{
		return linePointDistance(s, p) <= MyMath.TINY;
	}

	public static boolean isPointOnLineSegment(LineSegment2D s, Vector2D p)
	{
		return lineSementPointDistance(s, p) <= MyMath.TINY;
	}
	
	public static Vector2D getPointOnLineClosestToPoint(Line2D l, Vector2D p)
	{
		Vector2D ab = Vector2D.difference(l.b, l.a);
		Vector2D ap = Vector2D.difference(p, l.a);

		// Start with
		//   a + lambda (b-a) + mu n = p,
		// or
		//   lambda (b-a) + mu n = (p-a),
		// where n is a normal to the line.  Taking the scalar product with (b-a) on both sides, and using (b-a).n = 0, gives
		//   lambda (b-a).(b-a) = (p-a).(b-a),
		// or
		//   lambda = (p-a).(b-a) / (b-a).(b-a).
		double lambda = Vector2D.scalarProduct(ap, ab) / Vector2D.scalarProduct(ab, ab);

		// The point
		//   pL = a + lambda (b-a)
		// is then the point on the (infinite) straight line through a and b closest to p.
		return Vector2D.sum(l.a,  ab.getProductWith(lambda));
	}


	/**
	 * @param s1
	 * @param s2
	 * @return	the intersection of the two line segments;
	 * null if the lines are parallel or if the intersection is outside of the segments
	 */
	public static Vector2D lineSegmentLineSegmentIntersection2D(LineSegment2D s1, LineSegment2D s2)
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
		Vector2D d1 = s1.getA2B();
		Vector2D a2 = s2.a;
		Vector2D d2 = s2.getA2B();
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
	

}
