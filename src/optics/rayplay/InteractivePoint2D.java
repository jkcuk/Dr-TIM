package optics.rayplay;

import math.Vector2D;

public class InteractivePoint2D {
	
	private Vector2D v;
	
	/**
	 * radius, in pixels
	 */
	private int radius;

	public InteractivePoint2D(double x, double y, int radius) {
		v = new Vector2D(x, y);
		
		this.radius = radius;
	}
	
	public InteractivePoint2D(Vector2D v, int radius)
	{
		this.v = v;
		this.radius = radius;
	}

	public int getRadius() {
		return radius;
	}

	public void setRadius(int radius) {
		this.radius = radius;
	}

	public Vector2D getV() {
		return v;
	}

	public void setV(Vector2D v) {
		this.v = v;
	}

	/**
	 * Set the components of this point to those of v
	 * @param v
	 */
	public void setVComponents(Vector2D v)
	{
		this.v.x = v.x;
		this.v.y = v.y;
	}
}
