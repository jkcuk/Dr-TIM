package optics.raytrace.test;

import math.MyMath;
import optics.raytrace.sceneObjects.Sphere;
import optics.raytrace.surfaces.SurfaceOfVoxellatedRefractor;
import optics.raytrace.voxellations.SetOfConcentricSpheres;
import optics.raytrace.voxellations.Voxellation;

/**
 * @author johannes
 * A Luneburg lens, simulated in terms of thin spherical shells, each of constant refractive index
 */
public class SurfaceOfLayeredLuneburgLens extends SurfaceOfVoxellatedRefractor
{
	private static final long serialVersionUID = -2850155161835966513L;
	
	protected SetOfConcentricSpheres spheres;

	public SurfaceOfLayeredLuneburgLens(
			Sphere surface,
			int numberOfSphericalShells,
			double transmissionCoefficient,
			boolean shadowThrowing
		)
	{
		super(null, surface, 4*numberOfSphericalShells, transmissionCoefficient, shadowThrowing);
		
		Voxellation[] voxellations = new Voxellation[1];
		spheres = new SetOfConcentricSpheres(
				surface.getCentre(),	// common centre of all spheres
				surface.getRadius() + MyMath.TINY,	// radius of sphere #0
				surface.getRadius() / numberOfSphericalShells	// radius difference between neighbouring spheres
			);
		voxellations[0] = spheres;
		setVoxellations(voxellations);
	}

	/* (non-Javadoc)
	 * radial dependence of refractive index from M. Sarbort and T. Tyc,
	 * "Spherical media and geodesic lenses in geometrical optics",
	 * Journal of Optics�14, 075705 (2012)
	 * and from http://en.wikipedia.org/wiki/Luneburg_lens
	 * @see optics.raytrace.surfaces.SurfaceOfVoxellatedRefractor#getRefractiveIndex(int[])
	 */
	@Override
	public double getRefractiveIndex1(int[] voxelIndices)
	// throws Exception
	{
		// calculate the radius of the spherical shell represented by the voxel the ray is currently in
		double r = spheres.getRadius1(voxelIndices[0]+0.5);
		if(r<=0.0) return Double.NaN;
		
		double rOverR = r / ((Sphere)surface).getRadius();
		return Math.sqrt(2-rOverR*rOverR); 
	}


}
