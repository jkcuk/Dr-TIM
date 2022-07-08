package optics.rayplay.geometry2D;

import math.Vector2D;

/**
 * An infinite line through points a and b
 * 
 * @author johannes
 *
 */
public class Line2D
{	
	protected Vector2D a;
	protected Vector2D b;

	/**
	 * create line through points a and b
	 * @param a
	 * @param b
	 */
	public Line2D(
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
	
	public Vector2D getA2B()
	{
		return Vector2D.difference(b, a);
	}
	
	/**
	 * @return	normalised direction from A to B
	 */
	public Vector2D getNormalisedDirection()
	{
		return getA2B().getNormalised();
	}
	
	/**
	 * @param normalise
	 * @return	a normal, either not normalised (specifically of length |b-a|, if normalised = false) or normalised (if normalised = true)
	 */
	public Vector2D getNormal(boolean normalise)
	{
		Vector2D d = getA2B();
		Vector2D n = new Vector2D(-d.y, d.x);
		
		if(normalise) return n.getNormalised();
		else return n;
	}
	
	public boolean passesThroughPoint(Vector2D p)
	{
		return Geometry2D.isPointOnLine(this, p);
	}	
}
