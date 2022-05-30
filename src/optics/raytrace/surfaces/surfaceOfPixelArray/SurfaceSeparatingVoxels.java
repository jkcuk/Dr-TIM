package optics.raytrace.surfaces.surfaceOfPixelArray;

import optics.DoubleColour;
import optics.raytrace.core.*;
import optics.raytrace.exceptions.RayTraceException;
import optics.raytrace.voxellations.Voxellation;
import optics.raytrace.voxellations.SetOfSurfaces.OutwardsNormalOrientation;


/**
 * A surface property specifically for the surfaces that separate voxels in a voxellation, written for use in the "SurfaceOfPixelArray" SurfaceProperty.
 * A ray that hits the surface associated with this surface property (which should only happen from the inside) continues tracing in the pixel on the
 * other side of the surface.
 * 
 * @author Johannes Courtial
 */
/**
 * @author johannes
 *
 */
public class SurfaceSeparatingVoxels extends SurfaceProperty
{
	private static final long serialVersionUID = 7114392782036388908L;

	protected SurfaceOfPixelArray surfaceOfPixelArray;

	protected int voxellationIndicesOnInside[];

	/**
	 * this surface belongs to one particular voxellation;
	 * the voxellationNumber is the index of that voxellation in the voxellationIndices array
	 */
	protected int voxellationNumber;
	
	/**
	 * does this surface point towards the voxel with the more POSITIVE or more NEGATIVE index?
	 */
	protected OutwardsNormalOrientation outwardsNormalOrientation;
	

	public SurfaceSeparatingVoxels(
			SurfaceOfPixelArray surfaceOfPixelArray,
			int voxellationIndicesOnInside[],
			int voxellationNumber,
			OutwardsNormalOrientation outwardsNormalOrientation
		)
	{
		this.surfaceOfPixelArray = surfaceOfPixelArray;
		this.voxellationIndicesOnInside = voxellationIndicesOnInside;
		this.voxellationNumber = voxellationNumber;
		this.outwardsNormalOrientation = outwardsNormalOrientation;
	}

	@Override
	public SurfaceProperty clone() {
		return new SurfaceSeparatingVoxels(surfaceOfPixelArray, voxellationIndicesOnInside, voxellationNumber, outwardsNormalOrientation);
	}


	//
	// SurfaceProperty methods
	//
	
	@Override
	public DoubleColour getColour(Ray r, RaySceneObjectIntersection i, SceneObject scene, LightSource l, int traceLevel,
			RaytraceExceptionHandler raytraceExceptionHandler)
					throws RayTraceException
	{
		// return SurfaceColour.CYAN_SHINY.getColour(r, i, scene, l, traceLevel, raytraceExceptionHandler);
		
		if(traceLevel <= 0) return DoubleColour.BLACK;
		
		// calculate voxel indices of new voxel on the other side of this surface
		// first, copy the indices of the voxel on the inside, ...
		int voxellationIndicesOnOutside[] = new int[voxellationIndicesOnInside.length];
		for(int v=0; v<voxellationIndicesOnInside.length; v++) voxellationIndicesOnOutside[v] = voxellationIndicesOnInside[v];
		// ... then alter the relevant one
		voxellationIndicesOnOutside[voxellationNumber] += outwardsNormalOrientation.getSign();

		if(r.isReportToConsole())
		{
			System.out.println(
					"Ray passing through surface "+i.o.description
					+", from voxel "+Voxellation.toString(voxellationIndicesOnInside)
					+" to voxel "+Voxellation.toString(voxellationIndicesOnOutside)
					+", traceLevel="+traceLevel
					+" (SurfaceSeparatingVoxels::getColour)"
				);
		}
		
		return surfaceOfPixelArray.getColourStartingInPixel(
				(Orientation.getRayOrientation(r, i) == Orientation.OUTWARDS)?voxellationIndicesOnOutside:voxellationIndicesOnInside,
				r.getBranchRay(i.p, r.getD(), i.t, r.isReportToConsole()),	// .getAdvancedRay(MyMath.TINY),
				i,
				scene,
				l,
				traceLevel-1,
				raytraceExceptionHandler
			);
	}

	@Override
	public boolean isShadowThrowing() {
		return false;
	}
}
