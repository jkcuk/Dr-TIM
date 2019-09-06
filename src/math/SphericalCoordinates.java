package math;

/**
 * Spherical coordinates: r, phi, theta
 * @author johannes
 */
public class SphericalCoordinates extends CoordinateSystem
{

	private Vector3D origin;
	
	private Vector3D theta0Direction;
	
	private Vector3D theta90Phi0Direction;
	
	private Vector3D theta90Phi90Direction;
	
	
	// constructors
	
	/**
	 * @param origin	the centre of the coordinate system, r=0
	 * @param theta0Direction	the direction to the "north pole"
	 * @param theta90Phi0Direction	direction from the centre to the point on the equator with phi=0
	 * @param theta90Phi90Direction	direction from the centre to the point on the equator with phi=90
	 */
	public SphericalCoordinates(
			Vector3D origin,
			Vector3D theta0Direction,
			Vector3D theta90Phi0Direction,
			Vector3D theta90Phi90Direction
		)
	{
		super();
		this.origin = origin;
		this.theta0Direction = theta0Direction;
		this.theta90Phi0Direction = theta90Phi0Direction;
		this.theta90Phi90Direction = theta90Phi90Direction;
	}

	public SphericalCoordinates()
	{
		this(
				Vector3D.O,	// origin
				Vector3D.Y,	// theta0Direction
				Vector3D.X,	// theta90Phi0Direction
				Vector3D.Z	// theta90Phi90Direction
			);
	}
	
	
	// setters & getters
	
	public Vector3D getOrigin() {
		return origin;
	}

	public void setOrigin(Vector3D origin) {
		this.origin = origin;
	}

	public Vector3D getTheta0Direction() {
		return theta0Direction;
	}

	public void setTheta0Direction(Vector3D theta0Direction) {
		this.theta0Direction = theta0Direction;
	}

	public Vector3D getTheta90Phi0Direction() {
		return theta90Phi0Direction;
	}

	public void setTheta90Phi0Direction(Vector3D theta90Phi0Direction) {
		this.theta90Phi0Direction = theta90Phi0Direction;
	}

	public Vector3D getTheta90Phi90Direction() {
		return theta90Phi90Direction;
	}

	public void setTheta90Phi90Direction(Vector3D theta90Phi90Direction) {
		this.theta90Phi90Direction = theta90Phi90Direction;
	}


	// Coordinates methods

	@Override
	public Vector3D fromXYZ(Vector3D xyz)
	{
		return new Vector3D(
				
			);
	}

	@Override
	public Vector3D toXYZ(Vector3D v)
	{
		double r = v.x;
		double phi = v.y;
		double theta = v.z;
		return Vector3D.sum(
				origin,
				theta0Direction.getProductWith(Math.cos(theta)),
				theta90Phi0Direction.getProductWith(Math.sin(theta)*Math.cos(phi)),
				theta90Phi90Direction.getProductWith(Math.sin(theta)*Math.sin(phi))
			);
	}

}
