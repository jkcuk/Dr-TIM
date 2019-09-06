package optics.raytrace.test;

import math.MyMath;
import optics.raytrace.sceneObjects.ParametrisedCuboid;
import optics.raytrace.surfaces.SurfaceOfVoxellatedRefractor;
import optics.raytrace.voxellations.SetOfConcentricCubes;
import optics.raytrace.voxellations.Voxellation;

/**
 * @author johannes
 * This class is just for fun, to see if it does anything interesting (it doesn't seem to).
 * A "cuboid" Luneburg lens, simulated in terms of thin cuboid shells, each of constant refractive index.
 * The variation of refractive index with "radius" (i.e. half-sidelength) of the cuboid shells is that of the Luneburg lens.
 */
public class SurfaceOfLayeredCuboidLuneburgLens extends SurfaceOfVoxellatedRefractor
{
	private static final long serialVersionUID = -6835151950738545882L;

	protected SetOfConcentricCubes cubes;

	public SurfaceOfLayeredCuboidLuneburgLens(
			ParametrisedCuboid surface,
			int numberOfCuboidShells,
			double transmissionCoefficient,
			boolean shadowThrowing
		)
	{
		super(null, surface, 4*numberOfCuboidShells, transmissionCoefficient, shadowThrowing);
		
		Voxellation[] voxellations = new Voxellation[1];
		cubes = new SetOfConcentricCubes(
				surface.getCentre(),	// common centre of all cubes
				surface.getWidth()/2 - MyMath.TINY,	// radius of cube #0
				surface.getWidth()/2 / numberOfCuboidShells	// radius difference between neighbouring cubes
			);
		voxellations[0] = cubes;
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
	public double getRefractiveIndex(int[] voxelIndices)
	throws Exception
	{
		// calculate the radius of the cuboid shell represented by the voxel the ray is currently in
		double r = cubes.index2Radius(voxelIndices[0]+0.5);
		double rOverR = r / (((ParametrisedCuboid)surface).getWidth()/2);
		return Math.sqrt(2-rOverR*rOverR); 
	}

}
