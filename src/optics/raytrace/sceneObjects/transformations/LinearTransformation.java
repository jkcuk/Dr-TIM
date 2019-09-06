package optics.raytrace.sceneObjects.transformations;

import optics.raytrace.core.Transformation;
import math.Matrix3D;
import math.Vector3D;
import Jama.*;    // from http://math.nist.gov/javanumerics/jama/

public class LinearTransformation extends Transformation
{
	private Matrix A;
    private Vector3D b;

    //constructor
    public LinearTransformation(Matrix A, Vector3D b)
    {
        super();
        setA(A);
        setB(b);
    }
    
    public LinearTransformation()
    {
    	super();
    }
    
    public LinearTransformation(LinearTransformation original)
    {
    	A = original.getA().copy();
    	b = original.getB().clone();
    }
        
    public Matrix getA() {
		return A;
	}

	public void setA(Matrix a) {
		A = a;
	}

	public Vector3D getB() {
		return b;
	}

	public void setB(Vector3D b) {
		this.b = b;
	}
	
	public static Matrix getMatrixForEulerRotation(double alpha, double beta, double gamma)
	{
		return new Matrix(Matrix3D.getMatrixForEulerRotation(alpha, beta, gamma));
	}
	
	public static Matrix getMatrixForRotation(double rotationAngle, Vector3D rotationAxis)
	{
		return new Matrix(Matrix3D.getMatrixForRotation(rotationAngle, rotationAxis));
	}

    @Override
	public Vector3D transformPosition(Vector3D p)
    {
        return new Vector3D(A.times(p.toJamaColumnVector())).getSumWith(b);
    }
    
    @Override
	public Vector3D transformDirection(Vector3D d)
    {
        // unlike transformPosition, this method doesn't offset
        return new Vector3D(A.times(d.toJamaColumnVector()));
    }
    
}