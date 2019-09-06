package optics.raytrace.test;

import optics.raytrace.core.SceneObject;
import optics.raytrace.surfaces.SurfaceOfVoxellatedAbsorber;
import optics.raytrace.voxellations.Voxellation;

public class SurfaceOfSpecificVoxellatedAbsorber extends
		SurfaceOfVoxellatedAbsorber
{
	private static final long serialVersionUID = 5033608394116768223L;

	int redVoxelIndex = 0;
	
	public SurfaceOfSpecificVoxellatedAbsorber(
			Voxellation[] voxellations, SceneObject surface, int maxSteps,
			double transmissionCoefficient,
			boolean shadowThrowing)
	{
		super(voxellations, surface, maxSteps, transmissionCoefficient, shadowThrowing);
	}

	@Override
	public double getRedAbsorptionCoefficient(int[] voxelIndices)
	{
		return 0; // (voxelIndices[0]==redVoxelIndex)?1:0; 
		//return 0.01*(voxelIndices[0]*voxelIndices[0]); 
	}

	@Override
	public double getGreenAbsorptionCoefficient(int[] voxelIndices)
	{
		return (voxelIndices[0]==redVoxelIndex)?10:0; 
		//return 0.0*(voxelIndices[1]*voxelIndices[1]); 
	}

	@Override
	public double getBlueAbsorptionCoefficient(int[] voxelIndices)
	{
		return (voxelIndices[0]==redVoxelIndex)?10:0; 
		//return 0.01*(voxelIndices[1]*voxelIndices[1]); 
	}
}
