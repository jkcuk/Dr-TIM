package optics.raytrace.surfaces.surfaceOfPixelArray;

import optics.DoubleColour;
import optics.raytrace.core.LightSource;
import optics.raytrace.core.Ray;
import optics.raytrace.core.RaytraceExceptionHandler;
import optics.raytrace.core.SceneObject;
import optics.raytrace.exceptions.RayTraceException;

public class SurfaceOfPixelArrayRaytraceExceptionHandler
implements RaytraceExceptionHandler
{
	private SceneObject normalScene;
	private RaytraceExceptionHandler normalRaytraceExceptionHandler;
	
	public SurfaceOfPixelArrayRaytraceExceptionHandler(
			SceneObject normalScene,
			RaytraceExceptionHandler normalRaytraceExceptionHandler
		)
	{
		this.normalScene = normalScene;
		this.normalRaytraceExceptionHandler = normalRaytraceExceptionHandler;
	}

	@Override
	public DoubleColour getColourOfRayFromNowhere(Ray ray, SceneObject originObject, LightSource l, SceneObject scene,
			int traceLevel)
	throws RayTraceException
	{
		return normalScene.getColour(ray, l, originObject, traceLevel, normalRaytraceExceptionHandler);
	}

}
