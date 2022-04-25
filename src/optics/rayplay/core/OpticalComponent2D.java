package optics.rayplay.core;

import java.awt.Graphics2D;

public interface OpticalComponent2D {
	
	public String getName();

	/**
	 * Draw the component.
	 * It might look different if the mouse is near it.
	 * By default, just draws, ignoring the mouse.  Override to change this.
	 * @param p
	 * @param g
	 * @param mouseNear
	 * @param mouseI
	 * @param mouseJ
	 */
	public void draw(RayPlay2DPanel p, Graphics2D g, boolean mouseNear, int mouseI, int mouseJ);

	/**
	 * @param r
	 * @param forwardOnly
	 * @param lastIntersectionComponent	the optical component that was previously intersected (and which might have to be avoided)
	 * @return	the intersection between the ray r and this component
	 */
	public RayComponentIntersection2D calculateIntersection(Ray2D r, boolean forwardOnly, OpticalComponent2D lastIntersectionComponent);
	
	/**
	 * Step the ray through one component.
	 * If the optical component is a simple component such as a lens, then it simply passes through it.
	 * If the optical component is a collection of simple components, then it passes through one simple component.
	 * Should start a new ray segment from the intersection point.
	 * @param r
	 * @param intersectionPoint
	 */
	public void stepThroughComponent(Ray2D r, RayComponentIntersection2D i);
}
