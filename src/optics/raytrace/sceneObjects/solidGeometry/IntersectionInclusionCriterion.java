package optics.raytrace.sceneObjects.solidGeometry;

import optics.raytrace.core.RaySceneObjectIntersection;

public interface IntersectionInclusionCriterion
{
	public static IncludeAll iicAll = new IncludeAll();
	public static IncludeShadowThrowingOnly iicSTO = new IncludeShadowThrowingOnly();

	public boolean include(RaySceneObjectIntersection intersection);
}