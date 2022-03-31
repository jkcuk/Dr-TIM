package optics.rayplay;

import java.awt.Graphics2D;

import math.Vector2D;

public class Lens2D extends LineSegment2D
implements Component2D
{
	
	/**
	 * the principal point;
	 * this should lie on the lens, i.e. on the straight line between the end points;
	 * strange things will happen if it doesn't
	 */
	private Vector2D principalPoint;

	private double focalLength;
	
	
	// constructors
	
	public Lens2D(
			Vector2D principalPoint,
			double focalLength,
			Vector2D endPoint1,
			Vector2D endPoint2
		)
	{
		super(endPoint1, endPoint2);
		
		this.principalPoint = principalPoint;
		this.focalLength = focalLength;
	}

	
	
	// setters & getters
	
	public Vector2D getPrincipalPoint() {
		return principalPoint;
	}

	public void setPrincipalPoint(Vector2D principalPoint)
	{
		if(!isPointOnLine(this, principalPoint))
		this.principalPoint = principalPoint;
	}

	public double getFocalLength() {
		return focalLength;
	}

	public void setFocalLength(double focalLength) {
		this.focalLength = focalLength;
	}

	
	
	// useful methods
	
	@Override
	public void draw(RayPlay2DPanel p, Graphics2D g2) {
		p.drawLine(a, b, g2);
	}


	@Override
	public Vector2D calculateIntersection(Ray2D r, boolean forwardOnly)
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
		Vector2D d1 = getDirection();
		Vector2D a2 = r.getStartingPoint();
		Vector2D d2 = r.getDirection();
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
			// the ray is not parallel to the lens
		
			double alpha1 = (a1d1*d2d2 - a1d2*d1d2 - a2d1*d2d2 + a2d2*d1d2)/denominator;
			if((0 <= alpha1) && (alpha1 <= 1.0))
			{
				// the intersection is within the lens
				
				double alpha2 = (a2d2*d1d1 - a2d1*d1d2 - a1d2*d1d1 + a1d1*d1d2)/denominator;
				if((0 <= alpha2) || !forwardOnly)
				{
					// the intersection is with the actual ray, not its backwards continuation

					// return the intersection point, i.e. either a1 + alpha1 d1 or a2 + alpha2 d2
					return Vector2D.sum(
							a1, 
							d1.getProductWith(alpha1)
						);
				}
			}
		}
		
		// no intersection
		return null;
	}



	@Override
	public void passThroughComponent(Ray2D r, Vector2D intersectionPoint)
	{
		// old ray direction
		Vector2D d = r.getDirection();
		
		// calculate the direction of the optical axis from object to image space
		Vector2D n = getNormal(true);	// normal, but not necessarily pointing from object to image space
		n = n.getProductWith(Math.signum(Vector2D.scalarProduct(d, n)));	// now pointing object to image space
		// System.out.println("normal="+n);
		
		// component of old ray direction along optical axis
		double da = Vector2D.scalarProduct(d, n);
		
		// the ray intersects the image-sided focal plane in the same position as the principal ray through the principal point,
		// which intersects at
		Vector2D p = Vector2D.sum(
				principalPoint,
				d.getProductWith(focalLength/da)
			);
		
		// calculate the new ray direction
		r.startNextSegment(intersectionPoint, Vector2D.difference(p, intersectionPoint).getProductWith(Math.signum(focalLength)));
	}


	
}
