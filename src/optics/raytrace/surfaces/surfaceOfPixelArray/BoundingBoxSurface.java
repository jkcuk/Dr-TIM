package optics.raytrace.surfaces.surfaceOfPixelArray;

import optics.DoubleColour;
import optics.raytrace.core.*;
import optics.raytrace.exceptions.RayTraceException;
import optics.raytrace.sceneObjects.WrappedSceneObjectPrimitive;


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

	protected SceneObject scene;

	public BoundingBoxSurface(SceneObject scene)
	{
		this.scene = scene;
	}

	@Override
	public DoubleColour getColour(Ray r, RaySceneObjectIntersection i, SceneObject scene2, LightSource l, int traceLevel,
			RaytraceExceptionHandler raytraceExceptionHandler)
					throws RayTraceException
	{
		// return SurfaceColour.YELLOW_SHINY.getColour(r, i, scene, l, traceLevel, raytraceExceptionHandler);
		
		if(traceLevel <= 0) return DoubleColour.BLACK;

//		// is the orientation the right way round?
//		if(Orientation.getRayOrientation(r, i) == Orientation.INWARDS)
//		{
//			// this should never happen
//			// System.out.println("TIR?");
//			throw new RayTraceException("Ray intersecting BoundingBoxSurface *inwards*, which should never happen!?");
//		}
		
		if(i.o instanceof WrappedSceneObjectPrimitive)
		{
			// we don't want to intersect the bounding box again, but the getColourAvoidingOrigin method doesn't detect an
			// intersection with it if it is "wrapped", so we need to un-wrap it first
			i.o = ((WrappedSceneObjectPrimitive)i.o).getSceneObjectPrimitive();
		}
		
		// the orientation is outwards, which is what this is designed for;
		// launch a new ray from here
		return scene.getColourAvoidingOrigin(
				r.getBranchRay(
						i.p,
						r.getD(),
						i.t,
						r.isReportToConsole()
						),
				i.o,
				l,
				scene,
				traceLevel-1,
				raytraceExceptionHandler
				);
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
