package optics.raytrace.sceneObjects.transformations;

import math.Vector3D;
import Jama.Matrix;

/**
 * A transformation that describes rotation by an angle alpha around the y axis.
 * 
 * @author Johannes Courtial
 */
public class RotationAroundYAxis extends LinearTransformation
{
	/**
	 * Create a transformation that describes rotation by an angle alpha around the y axis
	 * 
	 * @param alpha	the rotation angle
	 */
	public RotationAroundYAxis(double alpha)
	{
		super();
		double
			cos = Math.cos(alpha),
			sin = Math.sin(alpha);
		double m[][] = {{cos, 0, -sin}, {0, 1, 0}, {sin, 0, cos}};
		setA(new Matrix(m));
		setB(Vector3D.O);		   
	}
}
