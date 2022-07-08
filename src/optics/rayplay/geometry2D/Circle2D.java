package optics.rayplay.geometry2D;

import math.Vector2D;

public class Circle2D {

	protected Vector2D centre;
	protected double radius;

	public Circle2D(Vector2D centre, double radius)
	{
		this.centre = centre;
		this.radius = radius;
	}


	//
	// getters & setters
	//
	
	public Vector2D getCentre() {
		return centre;
	}

	public void setCentre(Vector2D centre) {
		this.centre = centre;
	}

	public double getRadius() {
		return radius;
	}

	public void setRadius(double radius) {
		this.radius = radius;
	}
	
}
