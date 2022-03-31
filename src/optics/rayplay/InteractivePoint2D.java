package optics.rayplay;

import math.Vector2D;

public class InteractivePoint2D extends Vector2D {
	
	/**
	 * radius, in pixels
	 */
	private int radius;

	public InteractivePoint2D(double x, double y, int radius) {
		super(x, y);
		
		this.radius = radius;
	}

	public int getRadius() {
		return radius;
	}

	public void setRadius(int radius) {
		this.radius = radius;
	}

	public void setPosition(Vector2D p)
	{
		this.x = p.x;
		this.y = p.y;
	}
}
