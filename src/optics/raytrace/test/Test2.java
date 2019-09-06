package optics.raytrace.test;

import math.Vector2D;
import math.Vector3D;
import optics.raytrace.exceptions.RayTraceException;
import optics.raytrace.surfaces.MetricInterface;
import optics.raytrace.surfaces.MetricInterface.RefractionType;

/**
 * For testing of individual bits
 */
public class Test2
{
	public static double atan2(Vector2D vec){
		return Math.atan2(vec.y,vec.x)+Math.PI/4;
	}
	//yz plane
	public static double getXIndex(Vector3D vec){
		//question? true:false
		boolean even=Math.abs(vec.y)<Math.abs(vec.z),_0or2=vec.z>0;
		if(!even)return vec.y>0 ? 1 : 3;
		else return _0or2 ? 0 : 2;
	}
	//xz plane
	public static double getYIndex(Vector3D vec){
		//question? true:false
		boolean even=Math.abs(vec.x)<Math.abs(vec.z),_0or2=vec.z>0;
		if(!even)return vec.x>0 ? 1 : 3;
		else return _0or2 ? 0 : 2;
	}
	//xy plane
	public static double getZIndex(Vector3D vec){
		//question? true:false
		boolean even=Math.abs(vec.x)<Math.abs(vec.y),_0or2=vec.y>0;
		if(!even)return vec.x>0 ? 1 : 3;
		else return _0or2 ? 0 : 2;
	}
	/**
	 * @author	Alasdair Hamilton, Johannes Courtial. Mod by E Orife
	 */
	public static void main(final String[] args)
	{
		try {
			System.out.println("refracted ray direction = " +
				MetricInterface.getRefractedLightRayDirectionSurfaceCoordinates(
					new Vector3D(1,0,1),	// d
					MetricInterface.getDiagonalMetricTensor(1,1,1),	// g
					MetricInterface.getDiagonalMetricTensor(1,1,1),	// h
					RefractionType.POSITIVE_REFRACTION,	// refractionType
					false	// allowImaginaryOpticalPathLengths
				)
			);
			System.out.println("refracted ray direction = " +
					MetricInterface.getRefractedLightRayDirectionSurfaceCoordinates(
						new Vector3D(1,0,1),	// d
						MetricInterface.getDiagonalMetricTensor(1,1,1),	// g
						MetricInterface.getDiagonalMetricTensor(1,1,1),	// h
						RefractionType.NEGATIVE_REFRACTION,	// refractionType
						false	// allowImaginaryOpticalPathLengths
					)
				);
			
		} catch (RayTraceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		System.out.println("x-index for:"+new Vector3D(0,1,0)+";"+getXIndex(new Vector3D(0,1,0)));
		System.out.println("x-index for:"+new Vector3D(0,0,-1)+";"+getXIndex(new Vector3D(0,0,-1)));
		System.out.println("x-index for:"+new Vector3D(0,-1,0)+";"+getXIndex(new Vector3D(0,-1,0)));
		System.out.println("x-index for:"+new Vector3D(0,0,1)+";"+getXIndex(new Vector3D(0,0,1)));
		System.out.println("y-index for:"+new Vector3D(1,0,0)+";"+getYIndex(new Vector3D(1,0,0)));
		System.out.println("y-index for:"+new Vector3D(0,0,1)+";"+getYIndex(new Vector3D(0,0,1)));
		System.out.println("y-index for:"+new Vector3D(-1,0,0)+";"+getYIndex(new Vector3D(-1,0,0)));
		System.out.println("y-index for:"+new Vector3D(0,0,-1)+";"+getYIndex(new Vector3D(0,0,-1)));
		System.out.println("z-index for:"+new Vector3D(1,0,0)+";"+getZIndex(new Vector3D(1,0,0)));
		System.out.println("z-index for:"+new Vector3D(0,1,0)+";"+getZIndex(new Vector3D(0,1,0)));
		System.out.println("z-index for:"+new Vector3D(-1,0,0)+";"+getZIndex(new Vector3D(-1,0,0)));
		System.out.println("z-index for:"+new Vector3D(0,-1,0)+";"+getZIndex(new Vector3D(0,-1,0)));
		
	}
}
