package optics.rayplay.geometry2D;

import math.Vector2D;

/**
 * An ray, i.e. a semi-infinite line
 * 
 * @author johannes
 *
 */
public class Ray2D
{	
	/**
	 * ray start point
	 */
	protected Vector2D startPoint;
	
	/**
	 * (normalised) ray direction
	 */
	protected Vector2D normalisedDirection;

	/**
	 * Create a ray
	 * @param startPoint	ray start point
	 * @param direction	ray direction
	 */
	public Ray2D(
			Vector2D startPoint,
			Vector2D direction
		)
	{
		super();
		
		this.startPoint = startPoint;
		setNormalisedDirection(direction);
	}
	
	
	
	// setters & getters

	public Vector2D getStartPoint() {
		return startPoint;
	}

	public void setStartPoint(Vector2D startPoint) {
		this.startPoint = startPoint;
	}

	public Vector2D getNormalisedDirection() {
		return normalisedDirection;
	}

	public void setNormalisedDirection(Vector2D direction) {
		this.normalisedDirection = direction;
	}
	
	// useful
	
	public Vector2D getSecondPointOnRay()
	{
		return Vector2D.sum(startPoint, normalisedDirection);
	}
}
