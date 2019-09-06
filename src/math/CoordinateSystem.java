package math;

public abstract class CoordinateSystem
{
	/**
	 * @param xyz	a vector whose components are given in the global (x, y, z) coordinate system
	 * @return	the same vector, but with components given in the coordinates defined by this class
	 */
	public abstract Vector3D fromXYZ(Vector3D xyz);
	
	/**
	 * @param v	a vector whose components are given in the coordinates defined by this class
	 * @return	the same vector, but with components are given in the global (x, y, z) coordinate system
	 */
	public abstract Vector3D toXYZ(Vector3D v);
}
