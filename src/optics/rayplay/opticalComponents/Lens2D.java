package optics.rayplay.opticalComponents;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;

import math.Vector2D;
import optics.rayplay.core.OpticalComponent2D;
import optics.rayplay.core.LightRay2D;
import optics.rayplay.core.RayComponentIntersection2D;
import optics.rayplay.core.RayPlay2DPanel;
import optics.rayplay.geometry2D.Bijection2D;
import optics.rayplay.geometry2D.Geometry2D;
import optics.rayplay.geometry2D.Line2D;
import optics.rayplay.graphicElements.LineSegmentGE2D;
import optics.rayplay.util.Colour;
import optics.rayplay.util.DoubleFormatter;

public class Lens2D extends LineSegmentGE2D 
implements OpticalComponent2D, Bijection2D
{
	
	protected String name;
	
	/**
	 * the principal point;
	 * this should lie on the lens, i.e. on the straight line between the end points;
	 * strange things will happen if it doesn't
	 */
	protected Vector2D principalPoint;

	protected double focalLength;
	
	/**
	 * for use by the Bijection2D methods: a vector pointing from inside to outside space
	 */
	// private Vector2D outwardsVector;
	
	
	/**
	 * The end points of the lenses. Can be used to change a lens into a line?//TODO
	 */
	protected Vector2D endPoint1, endPoint2;
	
	
	// constructors
	
	public Lens2D(
			String name,
			Vector2D principalPoint,
			double focalLength,
			Vector2D endPoint1,
			Vector2D endPoint2,
			RayPlay2DPanel rayPlay2DPanel
		)
	{
		super(endPoint1, endPoint2, new BasicStroke(3), Colour.CYAN, 3, "opacity=\"0.7\"", rayPlay2DPanel);
		
		this.name = name;
		this.principalPoint = principalPoint;
		this.focalLength = focalLength;
	}
	
	public Lens2D(String name, RayPlay2DPanel rayPlay2DPanel)
	{
		this(name, null, 1, null, null, rayPlay2DPanel);
	}

	
	
	// setters & getters
	
	@Override
	public String getName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public Vector2D getPrincipalPoint() {
		return principalPoint;
	}

	public Vector2D getEndPoint1() {
		return endPoint1;
	}

	public void setEndPoint1(Vector2D endPoint1) {
		this.endPoint1 = endPoint1;
	}

	public Vector2D getEndPoint2() {
		return endPoint2;
	}

	public void setEndpoint2(Vector2D endPoint2) {
		this.endPoint2 = endPoint2;
	}

	/**
	 * Set the principal point.
	 * The end points need to be set first!
	 * @param principalPoint
	 */
	public void setPrincipalPoint(Vector2D principalPoint)
	{
		if(!Geometry2D.isPointOnLine(this, principalPoint))
		{
			System.out.println("Lens2D::setPrincipalPoint: Warning: principalPoint "+principalPoint+" of lens "+name+" does not lie on the straight line between the end points "+a+" and "+b+"!");
		}
		
		this.principalPoint = principalPoint;
	}

	public double getFocalLength() {
		return focalLength;
	}

	public void setFocalLength(double focalLength) {
		this.focalLength = focalLength;
	}

	public void setEndPoints(Vector2D e1, Vector2D e2)
	{
		this.a = e1;
		this.b = e2;
	}
	
	
	// useful methods
	
	@Override
	public RayComponentIntersection2D calculateIntersection(LightRay2D r, boolean forwardOnly, OpticalComponent2D lastIntersectionComponent)
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
					return new RayComponentIntersection2D(
							this,	// component
							Vector2D.sum(
									a1, 
									d1.getProductWith(alpha1)	// position
									)
							);
				}
			}
		}
		
		// no intersection
		return null;
	}



	@Override
	public void stepThroughComponent(LightRay2D r, RayComponentIntersection2D i)
	{
		// old ray direction
		Vector2D d = r.getNormalisedDirection();
		
		// calculate the direction of the optical axis from object to image space
		Vector2D n = getNormal(true);	// normal, but not necessarily pointing from object to image space
		n = n.getProductWith(Math.signum(Vector2D.scalarProduct(d, n)));	// now pointing object to image space
		// System.out.println("normal="+n);
		
		// component of old ray direction along optical axis
		double da = Vector2D.scalarProduct(d, n);
		
		// System.out.println("Lens2D::stepThroughComponent: principalPoint="+principalPoint+", d="+d);
		
		// the ray intersects the image-sided focal plane in the same position as the principal ray through the principal point,
		// which intersects at
		Vector2D p = Vector2D.sum(
				principalPoint,
				d.getProductWith(focalLength/da)
			);
		
		// calculate the new ray direction
		r.startNextSegment(i.p, Vector2D.difference(p, i.p).getProductWith(Math.signum(focalLength)));
	}
	
	public Line2D getOpticalAxis()
	{
		return new Line2D(
				principalPoint,
				Vector2D.sum(principalPoint, getNormal(true))
			);
	}
	
	public Vector2D getFocalPoint()
	{
		return Vector2D.sum(principalPoint, getNormal(true).getProductWith(focalLength));
	}


	// references for mappings:
	// [1] G. J. Chaplain et al., "Ray optics of generalized lenses", J. Opt. Soc. Am. A 33, 962-969 (2016)
	
	// Eqn (3) in [1] can be written as Q' = N + f/(f-a) (Q-N);
	// for a lens (where N=P) Q' = P + f/(f-a) (Q-P)


	@Override
	public Vector2D mapInwards(Vector2D q)
	{
		Vector2D pq = Vector2D.difference(q, principalPoint);
		Vector2D aHat = getA2B().getPerpendicularVector().getNormalised();
		return Vector2D.sum(
				principalPoint,
				pq.getProductWith(focalLength/(focalLength+Vector2D.scalarProduct(pq, aHat)))
			);	
	}

	@Override
	public Vector2D mapOutwards(Vector2D q)
	{
		Vector2D pq = Vector2D.difference(q, principalPoint);
		Vector2D aHat = getA2B().getPerpendicularVector().getNormalised();
		return Vector2D.sum(
				principalPoint,
				pq.getProductWith(focalLength/(focalLength-Vector2D.scalarProduct(pq, aHat)))
			);	
	}

	@Override
	public boolean isInteractive()
	{
		return true;
	}
		
	@Override
	public void drawAdditionalInfoWhenMouseNear(RayPlay2DPanel p, Graphics2D g, int mouseI, int mouseJ)
	{
		g.setColor(Color.GRAY);
		g.drawString(
				getName()+", f = "+DoubleFormatter.format(getFocalLength()), 
				mouseI+10, mouseJ+5	// x2i(p.x)+10, y2j(p.y)+5
				);
	}

	@Override
	public boolean mousePressed(RayPlay2DPanel rpp, boolean mouseNear, MouseEvent e)
	{
		return false;	// this component hasn't fully handled the event
	}
}
