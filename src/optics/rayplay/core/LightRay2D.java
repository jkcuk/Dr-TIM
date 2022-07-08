package optics.rayplay.core;

import java.util.ArrayList;

import math.Vector2D;
import optics.rayplay.geometry2D.Circle2D;
import optics.rayplay.geometry2D.Geometry2D;
import optics.rayplay.geometry2D.Ray2D;

/**
 * A light ray.
 * 
 * The underlying ray's start point and direction are those of the current (i.e., so far last) straight-line segment.
 * @author johannes
 *
 */
public class LightRay2D extends Ray2D {
	
	public static final ArrayList<LightRay2D> NO_RAYS = new ArrayList<LightRay2D>();

	private String name;
	
	/**
	 * the initial state of the ray
	 */
	private Ray2D initialState;
	
	/**
	 * true if this is the same ray as another ray, but fired off in the opposite direction;
	 * it is good to know this e.g. when calculating the image of a ray in the cells of an omnidirectional lens,
	 * where both the forward and reverse rays correspond to the same image
	 */
	private boolean reverse;
	
	/**
	 * path length accumulated so far
	 */
	private double pathLength;
	
	/**
	 * list of start points of previous straight-line trajectories
	 */
	private ArrayList<Vector2D> trajectory;
	
	private RayPlay2DPanel rayPlay2DPanel;
	
	/**
	 * the trace level, i.e. the number of additional intersections the ray can make before it is no longer traced
	 */
	private int traceLevel;

	public LightRay2D(
			String name,
			Vector2D startPoint,
			Vector2D direction,
			boolean reverse,
			int maxTraceLevel,
			RayPlay2DPanel rayPlay2DPanel
		)
	{
		super(startPoint, direction);
		
		this.initialState = new Ray2D(startPoint, direction);
		this.name = name;
		this.pathLength = 0;
		this.traceLevel = maxTraceLevel;
		this.reverse = reverse;
		this.rayPlay2DPanel = rayPlay2DPanel;
		
		trajectory = new ArrayList<Vector2D>();
		trajectory.add(startPoint);
	}

	
	// getters & setters
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Ray2D getInitialState() {
		return initialState;
	}

	public void setInitialState(Ray2D initialState) {
		this.initialState = initialState;
	}

	public double getPathLength() {
		return pathLength;
	}

	public void setPathLength(double pathLength) {
		this.pathLength = pathLength;
	}

	public ArrayList<Vector2D> getTrajectory() {
		return trajectory;
	}

	public void setTrajectory(ArrayList<Vector2D> trajectory) {
		this.trajectory = trajectory;
	}

	public int getTraceLevel() {
		return traceLevel;
	}

	public void setTraceLevel(int traceLevel) {
		this.traceLevel = traceLevel;
	}
	
	public boolean isReverse() {
		return reverse;
	}

	public void setReverse(boolean reverse) {
		this.reverse = reverse;
	}

	public RayPlay2DPanel getRayPlay2DPanel() {
		return rayPlay2DPanel;
	}

	public void setRayPlay2DPanel(RayPlay2DPanel rayPlay2DPanel) {
		this.rayPlay2DPanel = rayPlay2DPanel;
	}



	
	// useful methods

	/**
	 * Start a new segment in the ray's trajectory.
	 * Decrease the ray's traceLevel by 1.
	 * @param newStartingPoint
	 * @param newDirection
	 */
	public void startNextSegment(Vector2D newStartingPoint, Vector2D newDirection)
	{
		// calculate the path length from the previous starting point to the new starting point
		pathLength += Vector2D.distance(startPoint, newStartingPoint);
		
		// add the new starting point to the trajectory, i.e. the list of old starting points
		trajectory.add(newStartingPoint);
		
		// set the new starting point and direction
		this.startPoint = newStartingPoint;
		setNormalisedDirection(newDirection);
		
		traceLevel--;
	}
	
	/**
	 * Advance the ray by the given distance.
	 * This starts a new segment in the ray's trajectory, which decreases the ray's traceLevel by 1.
	 * @param distance
	 */
	public void advance(double distance)
	{
		Vector2D newStartingPoint = Vector2D.sum(startPoint, normalisedDirection.getWithLength(distance));
		startNextSegment(newStartingPoint, normalisedDirection);
	}
	
	public void advanceToEnclosingCircle()
	{
		Circle2D enclosingCircle = rayPlay2DPanel.getEnclosingCircle();
		double alpha[] = Geometry2D.getAlphaForLineCircleIntersections(
				startPoint,	// pointOnLine
				normalisedDirection,	// lineDirection
				enclosingCircle.getCentre(),	// circleCentre
				enclosingCircle.getRadius()	// circleRadius
			);
		if(alpha != null)
		{
			// there are intersections, but are they in the "forward" direction?
			double max = Math.max(alpha[0], alpha[1]);
			if(max >= 0) advance(max);
		}
	}

	
	/**
	 * @param opticalComponents
	 * @param lastIntersectionComponent
	 * @return	the intersection between this ray and the optical components (but not the lastIntersectionComponent)
	 */
	public RayComponentIntersection2D calculateIntersectionWith(ArrayList<OpticalComponent2D> opticalComponents, OpticalComponent2D lastIntersectionComponent)
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
				RayComponentIntersection2D i = o.calculateIntersection(this, true, lastIntersectionComponent);
				if(i != null)
				{
					// there is an intersection; is it closer than the current closest intersection?

					double d = Vector2D.distance(getStartPoint(), i.p);
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

	/**
	 * Trace the ray through the optical components
	 * @param opticalComponents
	 */
	public void traceThrough(ArrayList<OpticalComponent2D> opticalComponents)
	{
		// keep a note of the last intersected component
		OpticalComponent2D lastIntersectionComponent = null;
		
		while(traceLevel > 0)
		{
			// find the closest intersection between the ray and the components
			RayComponentIntersection2D i = calculateIntersectionWith(opticalComponents, lastIntersectionComponent);
			
			if(i == null)
			{
				// there is no intersection with any of the components
				// advance(100);
				advanceToEnclosingCircle();
				return;
			}
			else
			{
				// there is an intersection
				lastIntersectionComponent = i.o;
				i.o.stepThroughComponent(this, i);
			}
		}
	}

}
