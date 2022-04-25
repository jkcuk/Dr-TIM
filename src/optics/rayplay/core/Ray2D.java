package optics.rayplay.core;

import java.util.ArrayList;

import math.Vector2D;

public class Ray2D {

	/**
	 * starting point of the current (last, so far) straight-line segment
	 */
	private Vector2D startingPoint;
	
	/**
	 * direction of the current (last, so far) straight-line segment
	 */
	private Vector2D direction;
	
	/**
	 * path length accumulated so far
	 */
	private double pathLength;
	
	/**
	 * list of start points of previous straight-line trajectories
	 */
	private ArrayList<Vector2D> trajectory;
	
	/**
	 * the trace level, i.e. the number of additional intersections the ray can make before it is no longer traced
	 */
	private int traceLevel;

	public Ray2D(
			Vector2D startingPoint,
			Vector2D direction
		)
	{
		super();
		
		setStartingPoint(startingPoint);
		setDirection(direction);
		this.pathLength = 0;
		this.traceLevel = 255;
		
		trajectory = new ArrayList<Vector2D>();
		trajectory.add(startingPoint);
	}

	
	// getters & setters
	
	public Vector2D getStartingPoint() {
		return startingPoint;
	}

	public void setStartingPoint(Vector2D startingPoint) {
		this.startingPoint = startingPoint;
	}

	public Vector2D getDirection() {
		return direction;
	}

	public void setDirection(Vector2D direction) {
		this.direction = direction.getNormalised();
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
		pathLength += Vector2D.distance(startingPoint, newStartingPoint);
		
		// add the new starting point to the trajectory, i.e. the list of old starting points
		trajectory.add(newStartingPoint);
		
		// set the new starting point and direction
		this.startingPoint = newStartingPoint;
		this.direction = newDirection;
		
		traceLevel--;
	}
	
	/**
	 * Advance the ray by the given distance.
	 * This starts a new segment in the ray's trajectory, which decreases the ray's traceLevel by 1.
	 * @param distance
	 */
	public void advance(double distance)
	{
		Vector2D newStartingPoint = Vector2D.sum(startingPoint, direction.getWithLength(distance));
		startNextSegment(newStartingPoint, direction);
	}
	
}
