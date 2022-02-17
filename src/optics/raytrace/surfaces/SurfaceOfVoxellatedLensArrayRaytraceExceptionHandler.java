package optics.raytrace.surfaces;

import optics.DoubleColour;
import optics.raytrace.core.LightSource;
import optics.raytrace.core.Ray;
import optics.raytrace.core.RaytraceExceptionHandler;
import optics.raytrace.core.SceneObject;
import optics.raytrace.exceptions.RayTraceException;

public class SurfaceOfVoxellatedLensArrayRaytraceExceptionHandler
implements RaytraceExceptionHandler
{
	private SceneObject normalScene;
	private RaytraceExceptionHandler normalRaytraceExceptionHandler;
	private int normalTraceLevel;
	
	public SurfaceOfVoxellatedLensArrayRaytraceExceptionHandler(
			SceneObject normalScene,
			int normalTraceLevel,
			RaytraceExceptionHandler normalRaytraceExceptionHandler
		)
	{
		this.normalScene = normalScene;
		this.normalTraceLevel = normalTraceLevel;
		this.normalRaytraceExceptionHandler = normalRaytraceExceptionHandler;
	}

	@Override
	public DoubleColour getColourOfRayFromNowhere(Ray ray, SceneObject originObject, LightSource l, SceneObject scene,
			int traceLevel)
	throws RayTraceException
	{
		return normalScene.getColour(ray, l, originObject, normalTraceLevel, normalRaytraceExceptionHandler);
	}

}
