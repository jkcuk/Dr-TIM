package optics.raytrace.surfaces.surfaceOfPixelArray;

import optics.DoubleColour;
import optics.raytrace.core.*;
import optics.raytrace.exceptions.RayTraceException;
import optics.raytrace.surfaces.SurfaceColour;


/**
 * A surface property specifically for the bounding box associated with the "SurfaceOfPixelArray" SurfaceProperty.
 * A ray that hits the surface associated with this surface property from the inside (it should never be hit from the outside) passes straight through
 * and traces through the scene.
 * 
 * @author Johannes Courtial
 */
public class BoundingBoxSurface extends SurfaceProperty
{
	private static final long serialVersionUID = 7048620400194509473L;

	private SceneObject scene;

	public BoundingBoxSurface(SceneObject scene)
	{
		this.scene = scene;
	}

	@Override
	public DoubleColour getColour(Ray r, RaySceneObjectIntersection i, SceneObject scene, LightSource l, int traceLevel,
			RaytraceExceptionHandler raytraceExceptionHandler)
					throws RayTraceException
	{
		return SurfaceColour.YELLOW_SHINY.getColour(r, i, scene, l, traceLevel, raytraceExceptionHandler);
		
//		if(traceLevel <= 0) return DoubleColour.BLACK;
//
//		// is the orientation the right way round?
//		if(Orientation.getRayOrientation(r, i) == Orientation.INWARDS)
//		{
//			// this should never happen
//			throw new RayTraceException("Ray intersecting BoundingBoxSurface *inwards*, which should never happen!?");
//		}
//		
//		// the orientation is outwards, which is what this is designed for;
//		// launch a new ray from here
//		return scene.getColourAvoidingOrigin(
//				r.getBranchRay(
//						i.p,
//						r.getD(),
//						i.t,
//						r.isReportToConsole()
//						),
//				i.o,
//				l,
//				scene,
//				traceLevel-1,
//				raytraceExceptionHandler
//				);
	}

	@Override
	public SurfaceProperty clone() {
		return new BoundingBoxSurface(scene);
	}

	@Override
	public boolean isShadowThrowing() {
		return false;
	}
}
