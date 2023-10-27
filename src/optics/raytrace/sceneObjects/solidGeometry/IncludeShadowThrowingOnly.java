package optics.raytrace.sceneObjects.solidGeometry;

import optics.raytrace.core.RaySceneObjectIntersection;

public class IncludeShadowThrowingOnly implements IntersectionInclusionCriterion
{
	@Override
	public boolean include(RaySceneObjectIntersection intersection)
	{
		return intersection.o.isShadowThrowing();
	}
}
