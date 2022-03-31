package optics.rayplay;

import math.Vector2D;

/**
 * An infinite straight line in 2D, defined by a normalised normal nHat = (a, b) to the line and the distance d from the origin.
 * Any point on the line then satisfies the equation
 *   (x, y).(a, b) = d,
 * or
 *   a x + b y + c = 0
 * where c = -d.
 * 
 * @author johannes
 *
 */
public class Line2D
{	
	private double a;
	private double b;
	private double c;

	/**
	 * create line, setting parameters a, b, c directly;
	 * a, b will be scaled such that a^2 + b^2 = 1
	 * @param a
	 * @param b
	 * @param c	-distance from origin
	 */
	public Line2D(
			double a,
			double b,
			double c
		)
	{
		super();
		
		setAB(a, b);
		setC(c);
	}
	
	/**
	 * create line, giving normal and distance from origin
	 * @param normal	normal, not necessarily normalised
	 * @param intersect
	 */
	public Line2D(
			Vector2D normal,
			double intersect
		)
	{
		super();
		
		setNormal(normal);
		setIntersect(intersect);
	}
	
	/**
	 * create line, giving two points on the line
	 * @param point1
	 * @param point2
	 */
	public Line2D(
			Vector2D point1,
			Vector2D point2
		)
	{
		super();
		
		// d = point2 - point1; Cartesian coordinates: (dx, dy) = (point2x - point1x, point2y - point1y)
		// d points along the line; a normal to the line is therefore
		// n = (-dy, dx)
		Vector2D d = Vector2D.difference(point2, point1);
		
		setNormal(new Vector2D(-d.y, d.x));
		setIntersect(Vector2D.scalarProduct(getNormalisedNormal(), point1));
	}
	
	
	// setters & getters
	
	public double getA() {
		return a;
	}

	public double getB() {
		return b;
	}

	public void setAB(double a, double b) {
		double l = Math.sqrt(a*a + b*b);
		this.a = a/l;
		this.b = b/l;
	}

	public double getC() {
		return c;
	}

	public void setC(double c) {
		this.c = c;
	}

	public Vector2D getNormalisedNormal() {
		return new Vector2D(a, b);
	}

	public void setNormal(Vector2D normal) {
		Vector2D n = normal.getNormalised();
		a = n.x;
		b = n.y;
	}

	public double getIntersect() {
		return -c;
	}

	public void setIntersect(double intersect) {
		this.c = -intersect;
	}

	
	// useful methods
	
	/**
	 * @param line1
	 * @param line2
	 * @return	the intersection of the two infinite straight lines
	 */
	public static Vector2D calculateIntersection(Line2D line1, Line2D line2)
	{
		// see https://en.wikipedia.org/wiki/Line%E2%80%93line_intersection
		// in homogeneous coordinates, the intersection is (a3, b3, c3) = (b1 c2 - b2 c1, a2 c1 - a1 c2, a1 b2 - a2 b1),
		// so in Cartesian coordinates, the intersection is (a3/c3, b3/c3)
		double a3 = line1.b*line2.c - line1.c*line2.b;
		double b3 = line1.c*line2.a - line1.a*line2.c;
		double c3 = line1.a*line2.b - line1.b*line2.a;
		return new Vector2D(a3/c3, b3/c3);
	}
	
	public static double linePointDistance(Line2D line, Vector2D point)
	{
		return Vector2D.scalarProduct(line.getNormalisedNormal(), point) - line.getIntersect();
	}
	
	public static boolean isPointOnLine(Line2D line, Vector2D point)
	{
		return linePointDistance(line, point) == 0.;
	}

}
