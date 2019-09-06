package optics.raytrace.core;

import optics.DoubleColour;

public class DefaultRaytraceExceptionHandler implements RaytraceExceptionHandler
{
	@Override
	public DoubleColour getColourOfRayFromNowhere(Ray ray,
			SceneObject originObject, LightSource l, SceneObject scene,
			int traceLevel)
	{
		return DoubleColour.BLACK;
	}
}
