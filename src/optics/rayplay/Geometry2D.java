package optics.rayplay;

import math.Vector2D;

public class Geometry2D {
	
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
		// where n is a normal to the line.  Taking the scalar product with (b-a) on both sides, and using (b-a).n = 0, gives
		//   a.(b-a) + lambda (b-a).(b-a) = p.(b-a),
		// or
		//   lambda = (p-a).(b-a) / (b-a).(b-a).
		double lambda = Vector2D.scalarProduct(ab, ab) / Vector2D.scalarProduct(ab, ab);
		
		// The point
		//   pL = a + lambda (b-a)
		// is then the point on the (infinite) straight line through a and b closest to p.
		// If 0 <= lambda <= 1, it lies on the line segment
		if((0 <= lambda) && (lambda <= 1))
		{
			// The point pL lies on the line segment
			
			// The distance between the point and the line is then the distance between pL and p
			
			// construct a normalised normal to the line
			Vector2D nn = ab.getPerpendicularVector().getNormalised();
			
			return Math.abs(Vector2D.scalarProduct(ap, nn));
		}
		else if(lambda < 0)
		{
			// The distance between p and the line segment is the distance between p and a
			return Vector2D.distance(p, l.a);
		}
		else
		{
			// lambda > 1; the distance between p and the line segment is the distance between p and b
			return Vector2D.distance(p, l.b);
		}
	}

}
