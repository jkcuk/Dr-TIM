package optics.raytrace.sceneObjects.transformations;

import math.Vector3D;
import Jama.Matrix;

/**
 * A transformation that describes rotation by an angle alpha around the x axis.
 * 
 * @author Johannes Courtial
 */
public class RotationAroundXAxis extends LinearTransformation
{
	/**
	 * Create a transformation that describes rotation by an angle alpha around the x axis
	 * 
	 * @param alpha	the rotation angle
	 */
	public RotationAroundXAxis(double alpha)
	{
		super();
		double
			cos = Math.cos(alpha),
			sin = Math.sin(alpha);
		// TODO fix this matrix
		double m[][] = {{1, 0, 0}, {0, cos, -sin}, {0, sin, cos}};
		setA(new Matrix(m));
		setB(Vector3D.O);		   
	}
}
