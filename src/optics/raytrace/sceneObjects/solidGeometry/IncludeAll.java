package optics.raytrace.sceneObjects.solidGeometry;

import optics.raytrace.core.RaySceneObjectIntersection;

public class IncludeAll implements IntersectionInclusionCriterion
{
	@Override
	public boolean include(RaySceneObjectIntersection intersection)
	{
		return true;
	}
}
