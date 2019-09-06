package optics.raytrace.test;

import optics.raytrace.core.SceneObject;
import optics.raytrace.surfaces.SurfaceOfVoxellatedRefractor;
import optics.raytrace.voxellations.SetOfSurfaces;

// Author E Orife, based on SurfaceOfSpecificVoxellatedAbsorber example
public class SurfaceOfSpecificVoxellatedRefractor extends SurfaceOfVoxellatedRefractor
{
	private static final long serialVersionUID = 394361765699058331L;

	// constructor
	public SurfaceOfSpecificVoxellatedRefractor(
			SetOfSurfaces[] surfaceSets, SceneObject surface, int maxSteps,
			double transmissionCoefficient,
			boolean shadowThrowing)
	{
		super(surfaceSets, surface, maxSteps, transmissionCoefficient, shadowThrowing);
	}

	@Override
	public double getRefractiveIndex(int[] voxelIndices)
	{
		return 2; // +0.1*(voxelIndices[1]*voxelIndices[1]); 
	}


}
