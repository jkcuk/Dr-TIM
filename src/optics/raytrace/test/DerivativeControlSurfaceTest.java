package optics.raytrace.test;

import java.util.ArrayList;

import Jama.Matrix;
import math.MyMath;
import math.Vector3D;
import optics.raytrace.core.Orientation;
import optics.raytrace.exceptions.EvanescentException;
import optics.raytrace.surfaces.DerivativeControlSurface;
import optics.raytrace.surfaces.DerivativeControlSurfaceRotating;

/**
 * @author Johannes Courtial
 */
public class DerivativeControlSurfaceTest
{
	/**
	 * The main method, required so that this class can run as a Java application
	 * @param args
	 */
	public static void main(final String[] args)
	{		
		DerivativeControlSurface dcs = new DerivativeControlSurfaceRotating(MyMath.deg2rad(90));
		
		Vector3D pointOnSurface = new Vector3D(1, 2, 10);

		ArrayList<Vector3D> normalisedAxes = dcs.getNormalisedSurfaceCoordinateAxes(pointOnSurface);
		Vector3D u =  normalisedAxes.get(0);
		Vector3D v = normalisedAxes.get(1);
		Vector3D n = dcs.getParametrisedObject().getNormalisedOutwardsSurfaceNormal(pointOnSurface);
		
		// direction vector
		Vector3D vector = new Vector3D(1,2,3);
		
		System.out.print("v2D = ");
		Matrix v2D = dcs.vector3D2JamaVector2D(vector, u,  v);
		v2D.print(2, 2);
		
		try {
			System.out.println("back to 3D "+dcs.jamaVector2D2Vector3D(v2D, Orientation.OUTWARDS, u, v, n)
					+ " (for comparison: normalised initial vector = "+vector.getNormalised()+")"
					);
		} catch (EvanescentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.print("j = ");
		Matrix j = dcs.getJacobianOutwards(pointOnSurface);
		j.print(2, 2);
		
		System.out.print("j.v2D = ");
		j.times(v2D).print(2,2);
		
		try {
			System.out.print(
					"v3D = " + dcs.jamaVector2D2Vector3D(j.times(v2D), Orientation.OUTWARDS, u, v, n) 
				);
		} catch (EvanescentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}
}
