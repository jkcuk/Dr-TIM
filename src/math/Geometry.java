package math;

import Jama.Matrix;
import optics.raytrace.exceptions.RayTraceException;

/**
 * @author johannes
 * A collection of geometry-related methods that 
 */
public class Geometry
{
	/**
	 * @param pointOnLine
	 * @param directionOfLine
	 * @param point
	 * @return	the distance between the line and the point
	 */
	public static double linePointDistance(Vector3D pointOnLine, Vector3D directionOfLine, Vector3D point)
	{
		return Vector3D.difference(point, pointOnLine).getPartPerpendicularTo(directionOfLine).getLength();
	}
	
	public static Vector3D getPointOnLineClosestToPoint(Vector3D pointOnLine, Vector3D directionOfLine, Vector3D point)
	{
		return Vector3D.sum(
				pointOnLine,
				directionOfLine.getWithLength(
						Vector3D.scalarProduct(
								directionOfLine.getNormalised(),
								Vector3D.difference(point, pointOnLine)
							)
					)
			);
	}
	
	/**
	 * @param pointOnPlane
	 * @param planeNormal
	 * @param point
	 * @return	the distance between the plane and the point
	 */
	public static double planePointDistance(Vector3D pointOnPlane, Vector3D planeNormal, Vector3D point)
	{
		return Vector3D.scalarProduct(
				Vector3D.difference(point, pointOnPlane),
				planeNormal.getNormalised()
			);
	}
	
	public static Vector3D getPointOnPlaneClosestToPoint(Vector3D pointOnPlane, Vector3D planeNormal, Vector3D point)
	{
		Vector3D nHat = planeNormal.getNormalised();
		
		return Vector3D.sum(
				point,
				nHat.getProductWith(Vector3D.scalarProduct(Vector3D.difference(pointOnPlane, point), nHat))
			);
	}
	
	/**
	 * @param points
	 * @return	true if the points are in the same plane, false otherwise
	 */
	public static boolean arePointsInPlane(Vector3D points[])
	{
		// are there three or fewer points?
		if(points.length < 4) return true;
		
		// there are four or more points, so we need to check whether or not these are co-planar
		
		// calculate a normal to the plane through the first three points
		Vector3D normal = Vector3D.crossProduct(
				Vector3D.difference(points[1], points[0]),
				Vector3D.difference(points[2], points[0])
			);
		
		// for any additional points, check that their distance from the plane of the first three points is zero (numerically, anyway)
		for(int i=3; i<points.length; i++)
		{
			if(planePointDistance(points[0], normal, points[i]) > MyMath.EPSILON) return false; 
		}
		
		// no points outside of the plane of the first three points have been encountered; return true
		return true;
	}
	
	/**
	 * @param pointOnLine
	 * @param directionOfLine
	 * @param pointOnPlane
	 * @param normalToPlane
	 * @return	the point where the line intersects the plane, if there is a single intersection point; pointOnLine, if there are infinitely many intersection points; null if there is no intersection point
	 */
	public static Vector3D linePlaneIntersection(
			Vector3D pointOnLine, Vector3D directionOfLine,
			Vector3D pointOnPlane, Vector3D normalToPlane
		)
	throws MathException
	{
		// distance of pointOnLine and plane
		double numerator = Vector3D.scalarProduct(Vector3D.difference(pointOnPlane, pointOnLine), normalToPlane);
		
		// cos alpha, where alpha = angle between plane normal and line direction;
		// 0 if line is parallel to plane
		double denominator = Vector3D.scalarProduct(directionOfLine, normalToPlane);

		if (denominator == 0.0)
		{
			// the line is parallel to the plane
			
			// check if it lies in the plane
			if(numerator == 0.0)
			{
				// the line is in the plane
				
				throw new MathException("The line is in the plane, so there are infinitely many intersection points.");
			}
			else
			{
				// there is no intersection between the line and the plane
				
				return null;
			}
		}

		return Vector3D.sum(
				pointOnLine,
				Vector3D.scalarTimesVector3D(numerator / denominator, directionOfLine)
			);
	}
	

	/**
	 * @param pointOnLine
	 * @param directionOfLine
	 * @param pointOnPlane
	 * @param normalToPlane
	 * @return	the unique point where the line intersects the plane, if there is a single intersection point; null if there isn't
	 */
	public static Vector3D uniqueLinePlaneIntersection(
			Vector3D pointOnLine, Vector3D directionOfLine,
			Vector3D pointOnPlane, Vector3D normalToPlane
		)
	{
		// cos alpha, where alpha = angle between plane normal and line direction;
		// 0 if line is parallel to plane
		double denominator = Vector3D.scalarProduct(directionOfLine, normalToPlane);

		if (denominator == 0.0)
		{
			// there is either no intersection, or infinitely many intersections, so no unique intersection
			return null;
		}

		// distance of pointOnLine and plane
		double numerator = Vector3D.scalarProduct(Vector3D.difference(pointOnPlane, pointOnLine), normalToPlane);
		
		return Vector3D.sum(
				pointOnLine,
				Vector3D.scalarTimesVector3D(numerator / denominator, directionOfLine)
			);		
	}

	
	/**
	 * @param pointOnLine
	 * @param directionOfLine
	 * @param pointOnPlane
	 * @param normalToPlane
	 * @return	the factor a such that pointOnLine + a*directionOfLine lies on the plane
	 */
	public static double getFactorToLinePlaneIntersection(
			Vector3D pointOnLine, Vector3D directionOfLine,
			Vector3D pointOnPlane, Vector3D normalToPlane
		)
	{
		// for any point S on the plane, (S-pointOnPlane).normalToPlane = 0;
		// substitute S = pointOnLine + a*directionOfLine to get
		// (pointOnLine + a*directionOfLine - pointOnPlane).normalToPlane = 0, or
		// (pointOnLine - pointOnPlane).normalToPlane + a*directionOfLine.normalToPlane = 0, and so
		// a = (pointOnPlane - pointOnLine).normalToPlane / directionOfLine.normalToPlane
		double denominator = Vector3D.scalarProduct(directionOfLine, normalToPlane);

		if (denominator == 0.0)
		{
			// line is perpendicular to plane
			return Double.POSITIVE_INFINITY;
		}

		// return a
		return Vector3D.scalarProduct(Vector3D.difference(pointOnPlane, pointOnLine), normalToPlane) / denominator; 
	}
	
	/**
	 * Calculate the intersection point of two straight lines.
	 * If there is no such intersection, a RayTraceException is thrown.
	 * See https://en.wikipedia.org/wiki/Line--line_intersection
	 * @param pointOnLine1
	 * @param directionOfLine1
	 * @param pointOnLine2
	 * @param directionOfLine2
	 * @return	the point closest to both lines
	 * @throws RayTraceException 
	 */
	public static Vector3D lineLineIntersection(
			Vector3D pointOnLine1,	// p_1
			Vector3D directionOfLine1,	// v_1
			Vector3D pointOnLine2,
			Vector3D directionOfLine2
		) throws RayTraceException
	{
		Matrix
			i = Jama.Matrix.identity(3, 3),	// identity matrix
			v1 = directionOfLine1.getNormalised().toJamaColumnVector(),	//
			v2 = directionOfLine2.getNormalised().toJamaColumnVector(),
			n1 = i.minus(v1.times(v1.transpose())),	// I - v_1 v_1^T
			// d1 = i.minus(Vector3D.outerProduct(directionOfLine1, directionOfLine1)),	// I - v_1 v_1^T
			n2 = i.minus(v2.times(v2.transpose()));	// I - v_2 v_2^T
			// d2 = i.minus(Vector3D.outerProduct(directionOfLine2, directionOfLine2));	// I - v_2 v_2^T
		
		Vector3D intersectionPoint = new Vector3D( 
				n1.plus(n2).inverse().times(	// (sum_i I - v_i v_i^T)^{-1} *
						n1.times(pointOnLine1.toJamaColumnVector()).plus(
								n2.times(pointOnLine2.toJamaColumnVector())	// sum_i (I - v_i v_i^T) p_i
							)
					)
				);
		
		// calculate the distance of this point to one of the lines
		double distance = linePointDistance(pointOnLine1, directionOfLine1, intersectionPoint);
		// if(distance != 0)
		if(distance > MyMath.EPSILON)
		{
			// The distance is non-zero / quite big
			throw new RayTraceException("The two lines don't intersect.  "
					+ "Point on line 1=" + pointOnLine1
					+ ", direction of line 1=" + directionOfLine1
					+ ", point on line 2=" + pointOnLine2
					+ ", direction of line 2=" + directionOfLine2
					+ ". The resulting `intersection point', " + intersectionPoint + " is a distance "+distance+" from the lines.");
		}

		return intersectionPoint;
	}
	
	/**
	 * Calculate the intersection point of two straight lines.
	 * If there is no such intersection, the null vector is returned along with an error message.
	 * See https://en.wikipedia.org/wiki/Line--line_intersection
	 * @param pointOnLine1
	 * @param directionOfLine1
	 * @param pointOnLine2
	 * @param directionOfLine2
	 * @return	the point closest to both lines
	 */
	public static Vector3D lineLineIntersectionNoRayTraceException(
			Vector3D pointOnLine1,	// p_1
			Vector3D directionOfLine1,	// v_1
			Vector3D pointOnLine2,
			Vector3D directionOfLine2
		)
	{
		Matrix
			i = Jama.Matrix.identity(3, 3),	// identity matrix
			v1 = directionOfLine1.getNormalised().toJamaColumnVector(),	//
			v2 = directionOfLine2.getNormalised().toJamaColumnVector(),
			n1 = i.minus(v1.times(v1.transpose())),	// I - v_1 v_1^T
			// d1 = i.minus(Vector3D.outerProduct(directionOfLine1, directionOfLine1)),	// I - v_1 v_1^T
			n2 = i.minus(v2.times(v2.transpose()));	// I - v_2 v_2^T
			// d2 = i.minus(Vector3D.outerProduct(directionOfLine2, directionOfLine2));	// I - v_2 v_2^T
		
		Vector3D intersectionPoint = new Vector3D( 
				n1.plus(n2).inverse().times(	// (sum_i I - v_i v_i^T)^{-1} *
						n1.times(pointOnLine1.toJamaColumnVector()).plus(
								n2.times(pointOnLine2.toJamaColumnVector())	// sum_i (I - v_i v_i^T) p_i
							)
					)
				);
		
		// calculate the distance of this point to one of the lines
		double distance = linePointDistance(pointOnLine1, directionOfLine1, intersectionPoint);
		// if(distance != 0)
		if(distance > MyMath.EPSILON)
		{// The distance is non-zero / quite big
			System.err.println("The lines do not intercept. Returns null");
			intersectionPoint = null;
		}

		return intersectionPoint;
	}
	
	
	/**
	 * Calculate the point closest (in a least-squares sense) to both lines.
	 * If the lines intersect, then this is the intersection point
	 * See https://en.wikipedia.org/wiki/Line--line_intersection
	 * @param pointOnLine1
	 * @param directionOfLine1
	 * @param pointOnLine2
	 * @param directionOfLine2
	 * @param skewWarning	if true, gives a warning if the lines don't actually intersect
	 * @return	the point closest to both lines
	 */
	public static Vector3D pointClosestToBothLines(
			Vector3D pointOnLine1,	// p_1
			Vector3D directionOfLine1,	// v_1
			Vector3D pointOnLine2,
			Vector3D directionOfLine2,
			boolean skewWarning
		)
	{
		Matrix
			i = Jama.Matrix.identity(3, 3),	// identity matrix
			v1 = directionOfLine1.getNormalised().toJamaColumnVector(),	//
			v2 = directionOfLine2.getNormalised().toJamaColumnVector(),
			n1 = i.minus(v1.times(v1.transpose())),	// I - v_1 v_1^T
			// d1 = i.minus(Vector3D.outerProduct(directionOfLine1, directionOfLine1)),	// I - v_1 v_1^T
			n2 = i.minus(v2.times(v2.transpose()));	// I - v_2 v_2^T
			// d2 = i.minus(Vector3D.outerProduct(directionOfLine2, directionOfLine2));	// I - v_2 v_2^T
		
		Vector3D intersectionPoint = new Vector3D( 
				n1.plus(n2).inverse().times(	// (sum_i I - v_i v_i^T)^{-1} *
						n1.times(pointOnLine1.toJamaColumnVector()).plus(
								n2.times(pointOnLine2.toJamaColumnVector())	// sum_i (I - v_i v_i^T) p_i
							)
					)
				);
		
		if(skewWarning)
		{
			// calculate the distance of this point to one of the lines
			double distance = linePointDistance(pointOnLine1, directionOfLine1, intersectionPoint);
			if(distance > MyMath.EPSILON)
			{
				// The distance is non-zero / quite big.  Give a warning.
				String warning = "Warning: The two lines don't intersect.  The `intersection point' is a distance "+distance+" from the lines.";
				System.err.println("Geometry::lineLineIntersection: "+warning);
				// (new RayTraceException(warning)).printStackTrace();
			}
		}

		return intersectionPoint;
	}

	/**
	 * Calculate the intersection coordinates of the point on the first of two straight lines where the two come closest.
	 * See http://math.stackexchange.com/questions/538958/finding-coordinates-of-closest-approach
	 * @param pointOnLine1
	 * @param directionOfLine1
	 * @param pointOnLine2
	 * @param directionOfLine2
	 * @param skewWarning	if true, gives a warning if the lines don't actually intersect
	 * @return	the point on line 1 that is closest to line 2
	 */
	public static Vector3D pointOfClosestApproach(
			Vector3D pointOnLine1,	// E_1
			Vector3D directionOfLine1,	// E'_1
			Vector3D pointOnLine2,	// E_2
			Vector3D directionOfLine2,	// E'_2
			boolean skewWarning
		)
	{
		// line 1: E_1 + k E'_1, line 2: E_2 + mu E'_2
		// equations for k, mu that parametrise points where the lines are closest:
		// (E'_1)^2 k - E'_1.E'_2 mu == - E.E'_1
		// E'_1.E'_2 k - (E'_2)^2 mu == - E.E'_2
		// where E = E_1 - E_2
		// (source: http://math.stackexchange.com/questions/538958/finding-coordinates-of-closest-approach)
		// Solving the equations for k gives
		// k = (E'_2^2 E.E'_1 - E'_1.E'_2 E.E'_2)/((E'_1.E'_2)^2 - E'_1^2 E'_2^2)
		Vector3D e = Vector3D.difference(pointOnLine1, pointOnLine2);
		double
			e1e2 = Vector3D.scalarProduct(directionOfLine1, directionOfLine2),
			e1e1 = directionOfLine1.getModSquared(),
			e2e2 = directionOfLine2.getModSquared(),
			k = (e2e2*Vector3D.scalarProduct(e, directionOfLine1) - e1e2*Vector3D.scalarProduct(e, directionOfLine2)) /
				(e1e2*e1e2 - e1e1*e2e2);
		
		Vector3D pointOnLine1ClosestToLine2 = Vector3D.sum(pointOnLine1, directionOfLine1.getProductWith(k));
		
		if(skewWarning)
		{
			// calculate the distance of this point from line 2
			double distance = linePointDistance(pointOnLine2, directionOfLine2, pointOnLine1ClosestToLine2);
			if(distance > MyMath.TINY)
			{
				String warning = "Warning: The two lines don't intersect.  The point on line 1 that is closest to line 2 is a distance "+distance+" from line 2.";
				// The distance is quite big.  Give a warning.
				System.err.println("Geometry::pointOfClosestApproach: "+warning);
				// (new RayTraceException(warning)).printStackTrace();
			}
		}

		return pointOnLine1ClosestToLine2;
	}
		
	/**
	 * @param p1	point on first plane
	 * @param n1	normal to first plane
	 * @param p2	point on 2nd plane
	 * @param n2	normal to 2nd plane
	 * @param p0	some point
	 * @return	the point on the straight-line intersection between the two planes that is closest to p0
	 * Note that the direction of the plane-plane intersection is n1 x n2
	 */
	public static Vector3D pointOnPlanePlaneIntersection(Vector3D p1, Vector3D n1, Vector3D p2, Vector3D n2, Vector3D p0)
	{
		// see John Krumm's solution on http://math.stackexchange.com/questions/475953/how-to-calculate-the-intersection-of-two-planes
		double[][] mArray = {
				{2.,   0.,   0.,   n1.x, n2.x},
				{0.,   2.,   0.,   n1.y, n2.y},
				{0.,   0.,   2.,   n1.z, n2.z},
				{n1.x, n1.y, n1.z, 0.,   0.},
				{n2.x, n2.y, n2.z, 0.,   0.}
		};
		double[][] rArray = {{2.*p0.x}, {2.*p0.y}, {2.*p0.z}, {Vector3D.scalarProduct(p1, n1)}, {Vector3D.scalarProduct(p2,  n2)}};
		Matrix m = new Matrix(mArray);
		Matrix r = new Matrix(rArray);
		Matrix x = m.solve(r);
		
		return new Vector3D(x.get(0,0), x.get(1, 0), x.get(2, 0));
	}
	
	/**
	 * @param p1	point on plane 1
	 * @param n1	normal to plane 1
	 * @param p2	point on plane 2
	 * @param n2	normal to plane 2
	 * @param p3	point on plane 3
	 * @param n3	normal to plane 3
	 * @return	the position of the intersection of planes 1, 2 and 3
	 */
	public static Vector3D threePlaneIntersection(
			Vector3D p1,	// point on plane 1
			Vector3D n1,	// normal to plane 1
			Vector3D p2,	// point on plane 2
			Vector3D n2,	// normal to plane 2
			Vector3D p3,	// point on plane 3
			Vector3D n3		// normal to plane 3
		)
	{
		// A point p on plane 1, defined by a point p1 on the plane and a vector n1 normal to it, satisfies the equation
		// 	(p - p1).n1 = 0.
		// We are looking for a point p that satisfies such an equation for three planes, so we need to solve the system of equations
		//	(p - p1).n1 = 0,
		//	(p - p2).n2 = 0,
		//	(p - p3).n3 = 0.
		// We solve this by re-writing the equations in the form
		//	n1.p = n1.p1,
		//	n2.p = n2.p2,
		//	n3.p = n3.p3,
		// and the re-writing the LHS in matrix form:
		//	M.p = b,
		// where n1, n2 and n3 form the rows of the matrix M, and the vector b = (n1.p1, n2.p2, n3.p3)^T.
		// This solution is
		//	p = M^(-1).b.
		
		// create the JAMA matrix M
		double[][] mArray = {{n1.x, n1.y, n1.z}, {n2.x, n2.y, n2.z}, {n3.x, n3.y, n3.z}};
		Matrix m = new Matrix(mArray);
		
		// create the JAMA vector b
		double[][] bArray = {{Vector3D.scalarProduct(n1, p1)}, {Vector3D.scalarProduct(n2, p2)}, {Vector3D.scalarProduct(n3, p3)}};
		Matrix b = new Matrix(bArray);
		
		// solve for p
		Matrix p = m.solve(b);
		
		// turn p into a Vector3D
		return new Vector3D(p.get(1, 1), p.get(2, 1), p.get(3, 1));
	}
	
	public Line3D planePlaneIntersection(
			Plane3D p1,	// plane 1
			Plane3D p2	// plane 2
	)
	{
		// the planes are given by their normalised normals and the offset from the origin;
		
		// calculate the direction of the intersection line
		Vector3D n3 = Vector3D.crossProduct(p1.getNormal(), p2.getNormal());
		
		// 
		return new Line3D(
				pointClosestToBothLines(
						p1.calculatePointOnPlane(),	// pointOnLine1
						Vector3D.crossProduct(n3, p1.getNormal()),	// directionOfLine1
						p2.calculatePointOnPlane(),	// pointOnLine2
						Vector3D.crossProduct(n3, p2.getNormal()),	// directionOfLine2
						false	// skewWarning
					),	// pointOnLine
				n3	// directionOfLine
			);
	}

	/**
	 * @param point
	 * @param pointInPlane
	 * @param normalToPlane
	 * @return	the vector to the point, orthographically projected into the plane
	 */
	public static Vector3D orthographicProjection(Vector3D point, Vector3D pointInPlane, Vector3D normalToPlane)
	{
		// pp = point + k*normal is a point in the plane;
		// k*normal can be calculated as (pointInPlane - point).normal * normal / |normal|^2
		return Vector3D.sum(
				point,
				normalToPlane.getProductWith(	// normal * ...
						Vector3D.scalarProduct(
								Vector3D.difference(pointInPlane, point),
								normalToPlane
						) / normalToPlane.getModSquared()
				)	// ... * (pointInPlane - point).normal / |normal|^2
		);
	}
	
	/**
	 * @param point
	 * @param pointInPlane
	 * @param normalToPlane
	 * @return	a vector from the point to the nearest point on the plane described by <i>pointInPlane</i> and <i>normalToPlane</i>
	 */
	public static Vector3D getVectorToNearestPointOnPlane(Vector3D point, Vector3D pointInPlane, Vector3D normalToPlane)
	{
		return normalToPlane.getProductWith(	// normal * ...
				Vector3D.scalarProduct(
						Vector3D.difference(pointInPlane, point),
						normalToPlane
				) / normalToPlane.getModSquared()	// ... * (pointInPlane - point).normal / |normal|^2
		);
	}
	
	/**
	 * @param v
	 * @param rotationAxisNormalised
	 * @param rotationAngle
	 * @return	the direction vector <v>, rotated by the angle <rotationAngle> round the axis <rotationAxisNormalised>
	 */
	public static Vector3D rotate(Vector3D v, Vector3D rotationAxisNormalised, double rotationAngle)
	{
		Vector3D u = rotationAxisNormalised;
		
		// pre-calculate cos, sin, (1-cos)
		double c = Math.cos(rotationAngle);
		double s = Math.sin(rotationAngle);
		double c1 = 1-c;
		
		// pre-calculate squared rotation-axis components
		double
			ux2 = u.x*u.x,
			uy2 = u.y*u.y,
			uz2 = u.z*u.z;
		
		// calculate the rotated vector
		// see https://en.wikipedia.org/wiki/Rotation_matrix#Rotation_matrix_from_axis_and_angle
		return new Vector3D(
				(c+ux2*c1)*v.x + (u.x*u.y*c1 - u.z*s)*v.y + (u.x*u.z*c1 + u.y*s)*v.z,
				(u.y*u.x*c1 + u.z*s)*v.x + (c+uy2*c1)*v.y + (u.y*u.z*c1 - u.x*s)*v.z,
				(u.z*u.x*c1 - u.y*s)*v.x + (u.z*u.y*c1 + u.x*s)*v.y + (c+uz2*c1)*v.z
			);
	}
	
	/**
	 * @param point
	 * @param pointOnPlane
	 * @param normalToPlane
	 * @return	the point <i>point</i>, mirrored at the plane
	 */
	public static Vector3D reflectPointOnPlane(Vector3D point, Vector3D pointOnPlane, Vector3D normalToPlane)
	{
		// point + 2*(pointOnPlane - point)_perp
		return Vector3D.sum(
				point,
				Vector3D.difference(pointOnPlane, point).getPartParallelTo(normalToPlane).getProductWith(2)
			);
	}
	
	/**
	 * @param point
	 * @param pointOnLine
	 * @param directionOfLine
	 * @return	the point <i>point</i>, mirrored on the line
	 */
	public static Vector3D reflectPointOnLine(Vector3D point, Vector3D pointOnLine, Vector3D directionOfLine)
	{
		return Vector3D.sum(
				point,
				Vector3D.difference(pointOnLine, point).getPartPerpendicularTo(directionOfLine).getProductWith(2)
			);
	}
}
