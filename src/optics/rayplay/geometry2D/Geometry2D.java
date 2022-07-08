package optics.rayplay.geometry2D;

import math.MyMath;
import math.Vector2D;

public class Geometry2D {
	
	public static double linePointDistance(Line2D l, Vector2D p)
	{
		return Math.abs(Vector2D.scalarProduct(
				l.getNormal(true),
				Vector2D.difference(p, l.a)
			));
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
	 * @param a1	point on line 1
	 * @param d1	direction (not necessarily normalised) of line 1
	 * @param a2	point on line 2
	 * @param d2	direction (not necessarily normalised) of line 2
	 * @return	alpha1, a real number such that a1 + alpha1 d1 points to the intersection; Double.POSITIVE_INFINITY if there is no intersection
	 */
	public static double getAlpha1ForLineLineIntersection2D(Vector2D a1, Vector2D d1, Vector2D a2, Vector2D d2)
	{
		// a1 + alpha1 d1 = a2 + alpha2 d2
		// scalar product with d1: a1.d1 + alpha1 d1.d1 = a2.d1 + alpha2 d2.d1  (1)
		// scalar product with d2: a1.d2 + alpha1 d1.d2 = a2.d2 + alpha2 d2.d2  (2)
		// solution:
		// alpha1 = -((a1d2 d1d2 - a2d2 d1d2 - a1d1 d2d2 + a2d1 d2d2)/(d1d2^2 - d1d1 d2d2))
		// alpha2 = -((a1d2 d1d1 - a2d2 d1d1 - a1d1 d1d2 + a2d1 d1d2)/(d1d2^2 - d1d1 d2d2))
		double a1d1 = Vector2D.scalarProduct(a1, d1);
		double a1d2 = Vector2D.scalarProduct(a1, d2);
		double a2d1 = Vector2D.scalarProduct(a2, d1);
		double a2d2 = Vector2D.scalarProduct(a2, d2);
		double d1d1 = Vector2D.scalarProduct(d1, d1);
		double d1d2 = Vector2D.scalarProduct(d1, d2);
		double d2d2 = Vector2D.scalarProduct(d2, d2);
		
		// denominator = 
		double denominator = d1d2*d1d2 - d1d1*d2d2;
		
		if(denominator == 0.0)
		{
			// the lines are parallel -- there is no intersection
			return Double.POSITIVE_INFINITY;
		}
		else
		{
			return (a1d1*d2d2 - a1d2*d1d2 - a2d1*d2d2 + a2d2*d1d2)/denominator;
		}
	}
	
	/**
	 * @param a1	point on line 1
	 * @param d1	direction (not necessarily normalised) of line 1
	 * @param a2	point on line 2
	 * @param d2	direction (not necessarily normalised) of line 2
	 * @return	the intersection point, or null if there is no intersection
	 */
	public static Vector2D lineLineIntersection2D(Vector2D a1, Vector2D d1, Vector2D a2, Vector2D d2)
	{
		double alpha1 = getAlpha1ForLineLineIntersection2D(a1, d1, a2, d2);

		if(alpha1 == Double.POSITIVE_INFINITY)
		{
			// there is no intersection
			return null;
		}
		
		// there is an intersection
		return Vector2D.sum(a1, d1.getProductWith(alpha1));
	}
	
	public static Vector2D lineSegmentLineIntersection2D(LineSegment2D ls, Line2D l)
	{
		// s1 from a1 to b1, s2 from a2 to b2
		// define line directions d1 = b1 - a1, d2 = b2 - a2
		// a1 + alpha1 d1 = a2 + alpha2 d2
		// scalar product with d1: a1.d1 + alpha1 d1.d1 = a2.d1 + alpha2 d2.d1  (1)
		// scalar product with d2: a1.d2 + alpha1 d1.d2 = a2.d2 + alpha2 d2.d2  (2)
		// solution:
		// alpha1 = -((a1d2 d1d2 - a2d2 d1d2 - a1d1 d2d2 + a2d1 d2d2)/(d1d2^2 - d1d1 d2d2))
		// alpha2 = -((a1d2 d1d1 - a2d2 d1d1 - a1d1 d1d2 + a2d1 d1d2)/(d1d2^2 - d1d1 d2d2))
		Vector2D a1 = ls.getA();
		Vector2D d1 = ls.getA2B();
		Vector2D a2 = l.getA();
		Vector2D d2 = l.getNormalisedDirection();
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
			// the line is not parallel to the line segment
		
			double alpha1 = (a1d1*d2d2 - a1d2*d1d2 - a2d1*d2d2 + a2d2*d1d2)/denominator;
			if((0 <= alpha1) && (alpha1 <= 1.0))
			{
				// the intersection is within the line segment
				
				return Vector2D.sum(
						a1, 
						d1.getProductWith(alpha1)	// position
						);
			}
		}
		
		// no intersection
		return null;
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
	
	/**
	 * @param pointOnLine
	 * @param lineDirection
	 * @param circleCentre
	 * @param circleRadius
	 * @return the two values of alpha that describe the intersections, such that pointOnLine + alpha_1,2*lineDirection gives the intersection points; null if there is no intersection
	 */
	public static double[] getAlphaForLineCircleIntersections(Vector2D pointOnLine, Vector2D lineDirection, Vector2D circleCentre, double circleRadius)
	{
		// in the calculation, call
		// 	L = pointOnLine
		//  d = lineDirection
		//  C = circleCentre
		// 	r = circleRadius
		
		// point on line: P = L + alpha d
		// point on circle: |P-C|^2 = (P-C).(P-C) = r^2
		// point on intersection:
		// 	r^2 = |L + alpha d - C|^2 = |CL + alpha d|^2 = (CL + alpha d).(CL + alpha d)
		// 	    = CL.CL + 2 alpha d.CL + alpha^2 d.d
		// where CL = L-C
		// re-formulate:
		//  d.d alpha^2 + 2 d.CL alpha + CL.CL-r^2 = 0
		// or
		//  a alpha^2 + b alpha + c = 0,
		// where
		// 	a = d.d
		// 	b = 2 d.CL
		// 	c = CL.CL-r^2
		// then
		// 	alpha_1,2 = (-b +/- sqrt(b^2 - 4 a c))/(2 a)
		
		Vector2D cl = Vector2D.difference(pointOnLine, circleCentre);
		double a = Vector2D.scalarProduct(lineDirection, lineDirection);
		double b = 2*Vector2D.scalarProduct(lineDirection, cl);
		double c = Vector2D.scalarProduct(cl, cl) - circleRadius*circleRadius;
		
		// discriminant
		double d = b*b - 4*a*c;
		
		if(d >= 0)
		{
			// the line and the circle intersect
			double[] alpha = new double[2];
			double sqrtd = Math.sqrt(d);
			alpha[0] = (-b + sqrtd)/(2*a);
			alpha[1] = (-b - sqrtd)/(2*a);
			return alpha;
		}
		else
		{
			// the line and the circle don't intersect
			return null;
		}
	}

	/**
	 * @param pointOnLine
	 * @param lineDirection
	 * @param circleCentre
	 * @param circleRadius
	 * @return an array containing the two intersection points (if there are any), or null (if there aren't)
	 */
	public static Vector2D[] getLineCircleIntersections(Vector2D pointOnLine, Vector2D lineDirection, Vector2D circleCentre, double circleRadius)
	{
		double[] alpha = getAlphaForLineCircleIntersections(pointOnLine, lineDirection, circleCentre, circleRadius);
		
		if(alpha != null)
		{
			// there are intersections
			Vector2D[] intersection = new Vector2D[2];
			for(int i=0; i<=1; i++)
				intersection[i] = Vector2D.sum(pointOnLine, lineDirection.getProductWith(alpha[i]));
			
			return intersection;
		}
		else
		{
			// there is no intersection
			return null;
		}
	}

	
	public static double calculateAzimuthalCoordinate(Vector2D v, Vector2D xHat, Vector2D yHat)
	{
		return Math.atan2(
				Vector2D.scalarProduct(v, yHat),
				Vector2D.scalarProduct(v, xHat)
			);
	}
	
	public static double calculateAzimuthalCoordinate(Vector2D v)
	{
		return calculateAzimuthalCoordinate(v, Vector2D.X, Vector2D.Y);
	}
	
	public static Vector2D rotate(Vector2D v, double angle)
	{
		double c = Math.cos(angle);
		double s = Math.sin(angle);
		return new Vector2D(
				v.x*c - v.y*s,
				v.x*s + v.y*c
			);
	}
	
	public static Vector2D rotateAroundPoint(Vector2D v, double angle, Vector2D rotationCentre)
	{
		return Vector2D.sum(
				rotationCentre,
				Geometry2D.rotate(
						Vector2D.difference(v, rotationCentre),
						angle
					)
			);
	}
}
