package optics.rayplay;

import java.awt.Graphics2D;

import math.Vector2D;

public interface Component2D {

	public void draw(RayPlay2DPanel p, Graphics2D g2);

	public Vector2D calculateIntersection(Ray2D r, boolean forwardOnly);
	
	/**
	 * Pass the ray through the component 
	 * @param r
	 * @param intersectionPoint
	 */
	public void passThroughComponent(Ray2D r, Vector2D intersectionPoint);
}
