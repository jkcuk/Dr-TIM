package optics.rayplay.core;

import java.util.ArrayList;

/**
 * An optical component.
 * This interface makes available the methods that enable tracing of rays through the component.
 * 
 * If the component can also be drawn on the screen, then it also needs to implement the GraphicElement2D interface.
 * 
 * @author johannes
 * @see optics.rayplay.core.GraphicElement2D
 */
public interface OpticalComponent2D {
	public static final ArrayList<OpticalComponent2D> NO_COMPONENTS = new ArrayList<OpticalComponent2D>();
	
	public String getName();

	/**
	 * @param r
	 * @param forwardOnly
	 * @param lastIntersectionComponent	the optical component that was previously intersected (and which might have to be avoided)
	 * @return	the intersection between the ray r and this component (or a sub-component, in case this optical component is a collection)
	 */
	public RayComponentIntersection2D calculateIntersection(LightRay2D r, boolean forwardOnly, OpticalComponent2D lastIntersectionComponent);
	
	/**
	 * Step the ray through one component.
	 * If the optical component is a (simple) component such as a lens, then it simply passes through it.
	 * If the optical component is a collection of components, then it passes through one of the simple components.
	 * Should start a new ray segment from the intersection point.
	 * @param r
	 * @param intersectionPoint
	 */
	public void stepThroughComponent(LightRay2D r, RayComponentIntersection2D i);
}
