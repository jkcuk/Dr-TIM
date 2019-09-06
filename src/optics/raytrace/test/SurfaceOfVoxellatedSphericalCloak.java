package optics.raytrace.test;

import math.MyMath;
import optics.raytrace.sceneObjects.Sphere;
import optics.raytrace.surfaces.MetricInterface;
import optics.raytrace.surfaces.SurfaceOfVoxellatedMetric;
import optics.raytrace.voxellations.SetOfConcentricSpheres;
import optics.raytrace.voxellations.Voxellation;

/**
 * Surface around a voxellated volume in which the voxels simulate a cloak by using layered
 * spheres as metric interfaces. 
 * 
 * This is similar to what SurfacesOfSpecificVoxellatedAbsorbers/Refractors are to their 
 * superclasses, and so can be thought of as a SurfaceOfSpecificVoxellatedMetric. Tries to
 * follow the Luneburg lens examples closely.
 * 
 * This implementation is extremely naive, and conservative.
 * 
 * @author E Orife
 */

public class SurfaceOfVoxellatedSphericalCloak extends SurfaceOfVoxellatedMetric
{
	private static final long serialVersionUID = 6049484427653412110L;
	protected SetOfConcentricSpheres spheres;
	
	/**
	 * Creates a new surface property that marks a surface as the boundary surface defining a voxellated volume.
	 * @param surface
	 * @param numberOfSphericalShells
	 * @param transmissionCoefficient transmission coefficient on entering and exiting 
	 *        volume.
	 */
	public SurfaceOfVoxellatedSphericalCloak(
			Sphere surface,
			int numberOfSphericalShells,
			double transmissionCoefficient,
			boolean shadowThrowing
		)
	{
		this(surface, 4*numberOfSphericalShells, surface.getRadius()-MyMath.TINY, transmissionCoefficient, shadowThrowing);

//		// Replaces the old code below 
//		super(null, surface, 4*numberOfSphericalShells, transmissionCoefficient);
//
//		spheres = new SetOfConcentricSpheres(
//				surface.getCentre(), // common centre of all spheres
//				surface.getRadius()-MyMath.TINY,	// radius of sphere #0
//				surface.getRadius() / numberOfSphericalShells	// radius difference between neighbouring spheres
//				);
//		setVoxellations(spheres);
	}


	public SurfaceOfVoxellatedSphericalCloak(
			Sphere surface,
			int numberOfSphericalShells,
			double innermostSphere,
			double transmissionCoefficient,
			boolean shadowThrowing
			)
	{
		super(null, surface, 4*numberOfSphericalShells, transmissionCoefficient, shadowThrowing);

		spheres = new SetOfConcentricSpheres(
				surface.getCentre(), // common centre of all spheres
				innermostSphere,	// radius of sphere #0
				(surface.getRadius()-innermostSphere) / numberOfSphericalShells	// radius difference between neighbouring spheres
				);
		setVoxellations(new Voxellation[]{spheres});
	}
	
	
	// override these methods to customise the behaviour of the ray inside the surface
	
	/**
	 * @param voxelIndices
	 * @return	the metric tensor for the voxel with these voxelIndices
	 * TODO Flesh out method body. Not yet finished!
	 * @throws Exception 
	 */
	@Override
	public double[] getMetricTensor(int[] voxelIndices) throws Exception
	{
		
//		 //calculate the radius of the spherical shell represented by the voxel the ray is currently in 
		
//		double r=Double.NaN, rRatioSq=Double.NaN, a, ab_RatioSq=Double.NaN;
//		r = spheres.getRadius(voxelIndices[0]+0.5);
//		rRatioSq = r / (((Sphere)surface).getRadius());
//		a = ((Sphere) surface).getRadius();
//		ab_RatioSq = a/spheres.getRadius(voxelIndices[0]);
//		rRatioSq*=rRatioSq;ab_RatioSq*=ab_RatioSq;
		
		//Stephen's idea : use an arithmetic sequence?
		// See Philbin & Leonhardt, pg.s 167, 211
		double r=spheres.getRadius(voxelIndices[0]+0.5),
			   R= 500,//double R= something:i? R=dr/dr'.
		       //r'=r0+R*dr or r'=r0+i*dr 
			   r_=spheres.getSeparation()*R,
			   // e=alpha^2, where alpha is the gradient, however leave as is for debugging
			   e=(r_/r)*(r_/r)/R;// det e=R*(r_/r)*(r_/r)*1/R*1/R
		//double[] MetricInterface.getDiagonalMetricTensor(double g11, double g22, double g33);

		// double e_ij=1,g_ij=e_ij/ Math.sqrt(ex*ey*ez); double g(i,j)=e(i,j)/sqrt(det e);
		// double e(i,j) = diag(ex,ey,ez); 
		// diag(nx^2,ny^2,nz^2)= diag(ey*ez,ex*ez,ex*ey); // since n^2 = g
		// return g(i,j)=diag(R*(r'/r)^2, 1/R, 1/R)/det e;
		return scalarMult(MetricInterface.getDiagonalMetricTensor(R*(r_/r)*(r_/r), 1/R, 1/R), 
				          1/e);

	}
	
	public double[] scalarMult(double[] array, double a)
	{
		double copy[]=array.clone();
//		System.out.println(Arrays.toString(array)+", and for comparison:"
//                         +Arrays.toString(copy));
		for(int i=copy.length-1;i>-1;i--){// or i==0
			copy[i]*= a;
		} 
			
//		System.out.println(Arrays.toString(array)+", and for comparison:"
//                         +Arrays.toString(copy));
		return copy;
	
	}
	
	public double[] getMetricTensor(double rRatio, double a_Over_b,char x_OR_y)
	{
		if((x_OR_y=='x')||(x_OR_y=='X')) 
			return MetricInterface.getDiagonalMetricTensor(rRatio,a_Over_b,rRatio);
		else if((x_OR_y=='y')||(x_OR_y=='Y'))  
			return MetricInterface.getDiagonalMetricTensor(a_Over_b,rRatio,rRatio);
		else if((x_OR_y=='z')||(x_OR_y=='Z'))  
			return MetricInterface.getDiagonalMetricTensor(rRatio,rRatio,a_Over_b);
		else throw new Error("Error!");
	}
	
	public double rRatio(int[] voxelIndices)
	{
		return Double.NaN;
	}
	
	public double ab_RatioSq(int[] voxelIndices)
	{
		return Double.NaN;
	}
	
	
	
}