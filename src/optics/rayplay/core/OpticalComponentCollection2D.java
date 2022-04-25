package optics.rayplay.core;

import java.awt.Graphics2D;
import java.util.ArrayList;

import math.Vector2D;

/**
 * A collection of optical components
 * 
 * @author johannes
 */
public class OpticalComponentCollection2D
implements OpticalComponent2D
{
	protected String name;
		
	/**
	 * the list of optical components
	 */
	protected ArrayList<OpticalComponent2D> opticalComponents;
	

	// constructor
	
	/**
	 * Create an OpticalComponentCollection2D.
	 * The optical components need to be added separately, using the add method.
	 * @param name
	 */
	public OpticalComponentCollection2D(String name)
	{
		super();
		this.name = name;
		
		// create the opticalComponents ArrayList
		opticalComponents = new ArrayList<OpticalComponent2D>();
	}
	
	
	// setters & getters

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<OpticalComponent2D> getOpticalComponents() {
		return opticalComponents;
	}

	public void setOpticalComponents(ArrayList<OpticalComponent2D> opticalComponents) {
		this.opticalComponents = opticalComponents;
	}

	

	//
	// add/remove optical components
	//
	
	public void add(OpticalComponent2D o)
	{
		opticalComponents.add(o);
	}
	
	public void addAll(ArrayList<OpticalComponent2D> o)
	{
		opticalComponents.addAll(o);
	}

	public void remove(OpticalComponent2D o)
	{
		opticalComponents.remove(o);
	}
	
	public void removeAll(ArrayList<OpticalComponent2D> o)
	{
		opticalComponents.removeAll(o);
	}

	
	//
	// OpticalComponent2D methods
	//

	@Override
	public String getName() {
		return name;
	}

	@Override
	public RayComponentIntersection2D calculateIntersection(Ray2D r, boolean forwardOnly, OpticalComponent2D lastIntersectionComponent)
	{
		RayComponentIntersection2D closestIntersection = null;
		double closestIntersectionDistance = Double.POSITIVE_INFINITY;

		// go through all the optical components that make up this omnidirectional lens
		for(OpticalComponent2D o:opticalComponents)
		{
			// was the last intersection with the current optical component?
			if(o != lastIntersectionComponent)
			{
				// the last intersection was with a different component

				// see if there is an intersection with the current optical component
				RayComponentIntersection2D i = o.calculateIntersection(r, true, lastIntersectionComponent);
				if(i != null)
				{
					// there is an intersection; is it closer than the current closest intersection?

					double d = Vector2D.distance(r.getStartingPoint(), i.p);
					if(d < closestIntersectionDistance)
					{
						// the intersection is closer than the current closest intersection
						closestIntersection = i;
						closestIntersectionDistance = d;
					}
				}
			}
		}
		return closestIntersection;
	}

	@Override
	public void stepThroughComponent(Ray2D r, RayComponentIntersection2D i)
	{
		System.out.println("OpticalComponentCollection2D::stepThroughComponent: this method should not be called.");
	}
	
	/**
	 * Trace the ray through the OpticalComponentCollection2D
	 * @param r
	 */
	public void traceThroughComponents(Ray2D r)
	{
		// keep a note of the last intersected component
		OpticalComponent2D lastIntersectionComponent = null;
		
		while(r.getTraceLevel() > 0)
		{
			// find the closest intersection between the ray and the components
			RayComponentIntersection2D i = calculateIntersection(r, true, lastIntersectionComponent);
			
			if(i == null)
			{
				// there is no intersection with any of the components
				r.advance(10);
				return;
			}
			else
			{
				// there is an intersection
				lastIntersectionComponent = i.o;
				i.o.stepThroughComponent(r, i);
			}
		}
	}

	@Override
	public void draw(RayPlay2DPanel p, Graphics2D g, boolean mouseNear, int mouseI, int mouseJ) {
		// draw all the optical components in this collection
		for(OpticalComponent2D o:opticalComponents)
		{
			o.draw(p, g, mouseNear, mouseI, mouseJ);
		}
	}
}
