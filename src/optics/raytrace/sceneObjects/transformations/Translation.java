package optics.raytrace.sceneObjects.transformations;

import Jama.Matrix;
import math.Vector3D;

public class Translation extends LinearTransformation
{
	public Translation(Vector3D t)
	{
		super();
		double m[][] = {{1, 0, 0}, {0, 1, 0}, {0, 0, 1}};
		setA(new Matrix(m));
		setB(t);
	}
}
