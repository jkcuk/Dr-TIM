package math;

 /**
  * 
  * @author Johannes
  * 
  * A plane in 3D, given by its (normalised) normal and offset from the origin (i.e. offset * normal is a point on the plane)
  */

public class Plane3D
{	
	/**
	 * normal, normalised
	 */
	private Vector3D normal;
	
	/**
	 * offset, such that offset*normal is a point on the plane
	 */
	private double offset;
	
	public Plane3D(Vector3D normal, double offset)
	{	
		setNormal(normal);
		this.offset = offset;
	}
	
	public Plane3D(Vector3D normal, Vector3D pointOnPlane)
	{
		setNormal(normal);
		this.offset = Vector3D.scalarProduct(normal, pointOnPlane);
	}

	public Plane3D(Plane3D original)
	{
		this(original.getNormal(), original.getOffset());
	}
	
	@Override
	public Plane3D clone()
	{
		return new Plane3D(this);
	}
	

	// getters & setters
	

	public Vector3D getNormal() {
		return normal;
	}

	/**
	 * Sets the normal to the normalised argument
	 * @param normal
	 */
	public void setNormal(Vector3D normal) {
		this.normal = normal.getNormalised();
	}

	public double getOffset() {
		return offset;
	}

	public void setOffset(double offset) {
		this.offset = offset;
	}
	
	
	// useful stuff
	
	public Vector3D calculatePointOnPlane()
	{
		return normal.getProductWith(offset);
	}
	
	/**
	 * Calculate the offset such that offset * normalised normal is a point on the plane
	 * @param normal
	 * @param pointOnPlane
	 * @return	offset
	 */
	public static double calculateOffset(Vector3D normal, Vector3D pointOnPlane)
	{
		return Vector3D.scalarProduct(normal.getNormalised(), pointOnPlane);

	}
	
	
	@Override
	public String toString() {
		return "<Plane3D normal=" + normal + ", offset=" + offset + ">";
	}
}
