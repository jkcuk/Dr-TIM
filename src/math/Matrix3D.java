package math;

import java.io.Serializable;

/**
 * @author johannes
 * A collection of static routines for dealing with matrices
 */
public class Matrix3D implements Serializable, Cloneable
{
	private static final long serialVersionUID = 2585168734520972962L;

	public static double[][] getProduct(double[][] a, double[][] b)
	{
		// from http://blog.ryanrampersad.com/2010/01/matrix-multiplication-in-java/
		int
			aRows = a.length,
			aColumns = a[0].length,
			bRows = b.length,
			bColumns = b[0].length;

		if ( aColumns != bRows )
		{
			throw new IllegalArgumentException("A:Rows: " + aColumns + " did not match B:Columns " + bRows + ".");
		}

		double[][] resultant = new double[aRows][bColumns];

		for(int i = 0; i < aRows; i++) { // aRow
			for(int j = 0; j < bColumns; j++) { // bColumn
				for(int k = 0; k < aColumns; k++) { // aColumn
					resultant[i][j] += a[i][k] * b[k][j];
				}
			}  
		}

		return resultant;
	}
	
	public static double[][] getProduct(double[][] a, double[][] b, double[][] c)
	{
		return getProduct(a, getProduct(b, c));
	}
	
	public static double[][] getProduct(double[][] a, double[][] b, double[][] c, double[][] d)
	{
		return getProduct(a, getProduct(b, c, d));
	}

	public static double[][] getProduct(double[][] a, double[][] b, double[][] c, double[][] d, double[][] e)
	{
		return getProduct(a, getProduct(b, c, d, e));
	}

	public static double[][] getTranspose(double m[][])
	{
		// from http://blog.ryanrampersad.com/2010/01/matrix-multiplication-in-java/
		int
			rows = m.length,
			columns = m[0].length;

		double[][] mt = new double[columns][rows];

		for(int i = 0; i < columns; i++) { // aRow
			for(int j = 0; j < rows; j++) { // bColumn
				mt[i][j] += m[j][i];
			}  
		}

		return mt;
	}

	public static double[][] makeDiagonalMatrix(double m11, double m22, double m33)
	{
		double[][] m = {
				{m11, 0, 0},
				{0, m22, 0},
				{0, 0, m33}};
		return m;
	}

	public static double[][] makeColumnVector(double v1, double v2, double v3)
	{
		double[][] v = {
				{v1},
				{v2},
				{v3}};
		return v;
	}
	
	public static double[][] makeColumnVector(Vector3D v)
	{
		return makeColumnVector(v.x, v.y, v.z);
	}

	public static double[][] makeRowVector(double v1, double v2, double v3)
	{
		double[][] v = {{v1, v2, v3}};
		return v;
	}

	public static double[][] makeRowVector(Vector3D v)
	{
		return makeRowVector(v.x, v.y, v.z);
	}
	
	public static double[][] extractColumnVector(double[][] m, int index)
	{
		double[][] v = {
				{m[0][index]},
				{m[1][index]},
				{m[2][index]}};
		return v;
	}

	public static double[][] extractRowVector(double[][] m, int index)
	{
		double[][] v = {{m[index][0],m[index][1],m[index][2]}};
		return v;
	}
	
	public static double toScalar(double[][] m)
	{
		if((m.length != 1) || (m[0].length != 1))
		{
			throw new IllegalArgumentException("Argument is not a scalar.");
		}
		
		// if it is a scalar, return the single element
		return m[0][0];
	}
	
	public static double[][] getMatrixForEulerRotation(double alpha, double beta, double gamma)
	{
		double[][] m = {
				{
					Math.cos(alpha)*Math.cos(beta)*Math.cos(gamma) - Math.sin(alpha)*Math.sin(gamma),
					-Math.cos(gamma)*Math.sin(alpha) - Math.cos(alpha)*Math.cos(beta)*Math.sin(gamma),
					Math.cos(alpha)*Math.sin(beta)
				},
				{
					Math.cos(beta)*Math.cos(gamma)*Math.sin(alpha) + Math.cos(alpha)*Math.sin(gamma),
					Math.cos(alpha)*Math.cos(gamma) - Math.cos(beta)*Math.sin(alpha)*Math.sin(gamma),
					Math.sin(alpha)*Math.sin(beta)
				},
				{
					-Math.cos(gamma)*Math.sin(beta),
					Math.sin(beta)*Math.sin(gamma),
					Math.cos(beta)
				}
		};
		
		return m;
	}
	
	public static double[][] getMatrixForRotation(double rotationAngle, Vector3D rotationAxis)
	{
		Vector3D rHat = rotationAxis.getNormalised();
		
		// matrix elements calculated in Mathematica
		double[][] m = {
				{
					rHat.x*rHat.x + (rHat.y*rHat.y + rHat.z*rHat.z)*Math.cos(rotationAngle),
					2*Math.sin(rotationAngle/2)*(-rHat.z*Math.cos(rotationAngle/2) + rHat.x*rHat.y*Math.sin(rotationAngle/2)),
					rHat.x*rHat.z - rHat.x*rHat.z*Math.cos(rotationAngle) + rHat.y*Math.sin(rotationAngle)
				},
				{
					2*Math.sin(rotationAngle/2)*(rHat.z*Math.cos(rotationAngle/2) + rHat.x*rHat.y*Math.sin(rotationAngle/2)),
					rHat.y*rHat.y + (rHat.x*rHat.x + rHat.z*rHat.z)*Math.cos(rotationAngle),
					rHat.y*rHat.z - rHat.y*rHat.z*Math.cos(rotationAngle) - rHat.x*Math.sin(rotationAngle)
				},
				{
					2*rHat.x*rHat.z*Math.sin(rotationAngle/2)*Math.sin(rotationAngle/2) - rHat.y*Math.sin(rotationAngle),
					(2*rHat.y*Math.sqrt(rHat.x*rHat.x + rHat.y*rHat.y) * rHat.z*Math.sin(rotationAngle/2)*Math.sin(rotationAngle/2) + rHat.x*Math.sqrt(rHat.x*rHat.x + rHat.y*rHat.y)*Math.sin(rotationAngle))/Math.sqrt(rHat.x*rHat.x + rHat.y*rHat.y),
					rHat.z*rHat.z + (rHat.x*rHat.x + rHat.y*rHat.y)*Math.cos(rotationAngle)
				}
		};
		
		return m;
	}

}
