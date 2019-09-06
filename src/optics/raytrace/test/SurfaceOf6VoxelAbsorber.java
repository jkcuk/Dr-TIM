package optics.raytrace.test;

import optics.raytrace.core.SceneObject;
import optics.raytrace.surfaces.SurfaceOfVoxellatedAbsorber;
import optics.raytrace.voxellations.Voxellation;

public class SurfaceOf6VoxelAbsorber extends SurfaceOfVoxellatedAbsorber
{
	private static final long serialVersionUID = 8817704929457409918L;

	// constructor
	public SurfaceOf6VoxelAbsorber(
			Voxellation[] voxellations, SceneObject surface, int maxSteps,
			double transmissionCoefficient, boolean shadowThrowing)
	{
		super(voxellations, surface, maxSteps, transmissionCoefficient, shadowThrowing);
	}
	
	int redVoxelIndex = 5;

	@Override
	public double getRedAbsorptionCoefficient(int[] voxelIndices)
	{
		return (voxelIndices[0]==redVoxelIndex)?0:0.01; 
	}

	@Override
	public double getGreenAbsorptionCoefficient(int[] voxelIndices)
	{
		return (voxelIndices[0]==redVoxelIndex)?10:0.01; 
	}

	@Override
	public double getBlueAbsorptionCoefficient(int[] voxelIndices)
	{
		return (voxelIndices[0]==redVoxelIndex)?10:0.01; 
	}
}
