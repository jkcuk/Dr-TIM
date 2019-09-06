package optics.raytrace.sceneObjects.transformations;

import math.Vector3D;
import Jama.Matrix;

/**
 * A transformation that describes rotation by an angle alpha around the z axis.
 * 
 * @author Johannes Courtial
 */
public class RotationAroundZAxis extends LinearTransformation
{
	/**
	 * Create a transformation that describes rotation by an angle alpha around the z axis
	 * 
	 * @param alpha	the rotation angle
	 */
	public RotationAroundZAxis(double alpha)
	{
		super();
		double
			cos = Math.cos(alpha),
			sin = Math.sin(alpha);
		double m[][] = {{cos, -sin, 0}, {sin, cos, 0}, {0, 0, 1}};
		setA(new Matrix(m));
		setB(Vector3D.O);		   
	}
}
