package optics.raytrace.surfaces.surfaceOfPixelArray;

import optics.DoubleColour;
import optics.raytrace.core.LightSource;
import optics.raytrace.core.Ray;
import optics.raytrace.core.RaySceneObjectIntersection;
import optics.raytrace.core.RaytraceExceptionHandler;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.SurfaceProperty;
import optics.raytrace.exceptions.RayTraceException;
import optics.raytrace.surfaces.RefractiveSimple;

public class BoundingBoxSurfaceForRefractiveComponent extends BoundingBoxSurface {
	private static final long serialVersionUID = 3473328631960128436L;

	private int voxelIndices[];
	
	public BoundingBoxSurfaceForRefractiveComponent(SceneObject scene, int voxelIndices[], SurfaceOfPixelArray surfaceOfPixelArray, int traceLevel)
	{
		super(scene, surfaceOfPixelArray, traceLevel);
		this.voxelIndices = voxelIndices;
	}
	
	@Override
	public SurfaceProperty clone() {
		return new BoundingBoxSurfaceForRefractiveComponent(scene, voxelIndices, surfaceOfPixelArray, traceLevel);
	}

	@Override
	public DoubleColour getColour(Ray r, RaySceneObjectIntersection i, SceneObject scene2, LightSource l, int traceLevel,
			RaytraceExceptionHandler raytraceExceptionHandler)
					throws RayTraceException
	{
//		// is the orientation the right way round?
//		if(Orientation.getRayOrientation(r, i) == Orientation.INWARDS)
//		{
//			// this should never happen
//			// System.out.println("TIR?");
//			throw new RayTraceException("Ray intersecting BoundingBoxSurface *inwards*, which should never happen!?");
//		}

		Ray r2 = r;
		double t = 1;
		
		// is the ray leaving from the refractive material (typically through the side)?
		if(surfaceOfPixelArray.getSceneObjectsInPixel(voxelIndices).insideObject(i.p))
		{
			
			
			r2 = r.getBranchRay(
					i.p, 
					RefractiveSimple.getRefractedLightRayDirection(
							r.getD(),
							i.getNormalisedOutwardsSurfaceNormal(),
							surfaceOfPixelArray.getRefractiveIndex()
						),
					i.t, r.isReportToConsole()
				);
			t = surfaceOfPixelArray.getSurfaceTransmissionCoefficient(); // TODO
		}

		return super.getColour(r2, i, scene2, l, traceLevel, raytraceExceptionHandler).multiply(t);
	}

}
