package optics.raytrace.surfaces;

import math.MyMath;
import optics.DoubleColour;
import optics.raytrace.core.LightSource;
import optics.raytrace.core.Ray;
import optics.raytrace.core.RaySceneObjectIntersection;
import optics.raytrace.core.RaytraceExceptionHandler;
import optics.raytrace.core.SceneObject;
import optics.raytrace.exceptions.InconsistencyException;
import optics.raytrace.exceptions.RayTraceException;
import optics.raytrace.voxellations.Voxellation;

/**
 * Surface around a voxellated volume in which the boundaries between voxels are coloured.
 * 
 * @author Johannes Courtial
 */
public class SurfaceOfVolumeWithColouredVoxelBoundaries extends SurfaceOfVoxellatedVolume
{
	private static final long serialVersionUID = 6654435086921268390L;
	
	/**
	 * the colour of the voxel boundaries
	 */
	private DoubleColour voxelBoundaryColour;

	/**
	 * Creates a new surface property that marks a surface as the boundary surface defining a voxellated volume.
	 * @param planeSets	the sets of equidistant, parallel, planes that define the voxels
	 * @param transmissionCoefficient	transmission coefficient on entering and exiting volume
	 */
	public SurfaceOfVolumeWithColouredVoxelBoundaries(Voxellation[] voxellations, SceneObject surface, DoubleColour voxelBoundaryColour, int maxSteps, double transmissionCoefficient, boolean shadowThrowing)
	{
		super(voxellations, surface, maxSteps, transmissionCoefficient, shadowThrowing);
		
		this.voxelBoundaryColour = voxelBoundaryColour;
	}
	
	/**
	 * Clone the original surface
	 * @param original
	 */
	public SurfaceOfVolumeWithColouredVoxelBoundaries(SurfaceOfVolumeWithColouredVoxelBoundaries original)
	{
		this(
				original.getVoxellations(),
				original.getSurface(),
				original.getVoxelBoundaryColour(),
				original.getMaxSteps(),
				original.getTransmissionCoefficient(),
				original.isShadowThrowing()
			);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public SurfaceOfVolumeWithColouredVoxelBoundaries clone()
	{
		return new SurfaceOfVolumeWithColouredVoxelBoundaries(this);
	}
	
	
	
	// getters & setters
	
	public DoubleColour getVoxelBoundaryColour() {
		return voxelBoundaryColour;
	}

	public void setVoxelBoundaryColour(DoubleColour voxelBoundaryColour) {
		this.voxelBoundaryColour = voxelBoundaryColour;
	}

	
	
	/**
	 * What to do upon entering the volume.
	 * @param ray
	 * @param i
	 * @param scene
	 * @param l
	 * @param traceLevel
	 * @param raytraceExceptionHandler
	 * @return
	 * @throws RayTraceException
	 */
	@Override
	public DoubleColour getColourUponEnteringVolume(Ray r, RaySceneObjectIntersection i, SceneObject scene, LightSource l, int stepsLeft, int traceLevel, RaytraceExceptionHandler raytraceExceptionHandler)
	throws RayTraceException
	{
		if(stepsLeft < 0) return DoubleColour.BLACK;
		
		// System.out.println("getColourUponEnteringVolume: r="+r+", i="+i);
		
		// calculate the ray immediately after it has entered the volume;
		// here, the ray passes through the bounding surface undeviated
		Ray r2 = r.getBranchRay(
				i.p,
				r.getD(),
				i.t,
				r.isReportToConsole()
		);

		// then do whatever rays do within the volume
		return getColourUponStartingWithinVolume(r2, i, scene, l, stepsLeft-1, traceLevel, raytraceExceptionHandler).multiply(getTransmissionCoefficient());
	}

	
	/**
	 * What to do upon starting inside the volume.
	 * @param r1
	 * @param i1
	 * @param scene
	 * @param l
	 * @param traceLevel
	 * @param raytraceExceptionHandler
	 * @return
	 * @throws RayTraceException
	 */
	@Override
	public DoubleColour getColourUponStartingWithinVolume(Ray r, RaySceneObjectIntersection i, SceneObject scene, LightSource l, int stepsLeft, int traceLevel, RaytraceExceptionHandler raytraceExceptionHandler)
	throws RayTraceException
	{
		if(stepsLeft < 0) return DoubleColour.BLACK;
		
		// calculate the next intersection between the ray and the voxel surface
		RaySceneObjectIntersection i2 = getIntersectionWithVoxelSurface(r.getAdvancedRay(MyMath.TINY), null);
		
		if(i2 == RaySceneObjectIntersection.NO_INTERSECTION)
		{
			// this shouldn't happen
			throw new InconsistencyException("No intersection with voxel surface.  This should not happen!");
		}
				
		// check if the intersection is with the surface of the volume
		if(surface.getSceneObjectPrimitives().contains(i2.o))
		{
			// the intersection is with the surface; leave the volume
			return getColourUponLeavingVolume(
					r, // .getAdvancedRay(MyMath.TINY),	// advance the ray to avoid intersecting with the origin again
					i2, scene, l, stepsLeft, traceLevel, raytraceExceptionHandler);
		}
		else
		{
			// the intersection is with one of the planes; the light ray gets absorbed, i.e. return black
			return getColourUponIntersectingWithPlane(r, i2, scene, l, stepsLeft-1, traceLevel, raytraceExceptionHandler);
		}
	}



	/**
	 * What to do upon intersecting one of the planes inside the volume.
	 * @param r1
	 * @param i1
	 * @param scene
	 * @param l
	 * @param traceLevel
	 * @param raytraceExceptionHandler
	 * @return
	 * @throws RayTraceException
	 */
	public DoubleColour getColourUponIntersectingWithPlane(Ray r, RaySceneObjectIntersection i, SceneObject scene, LightSource l, int stepsLeft, int traceLevel, RaytraceExceptionHandler raytraceExceptionHandler)
	throws RayTraceException
	{
		return voxelBoundaryColour;
	}

	/**
	 * What to do upon leaving the volume.
	 * Here, this should never be called.
	 * @param ray
	 * @param i
	 * @param scene
	 * @param l
	 * @param traceLevel
	 * @param raytraceExceptionHandler
	 * @return
	 * @throws RayTraceException
	 */
	public DoubleColour getColourUponLeavingVolume(Ray r, RaySceneObjectIntersection i, SceneObject scene, LightSource l, int stepsLeft, int traceLevel, RaytraceExceptionHandler raytraceExceptionHandler)
	throws RayTraceException
	{
		// here, the ray passes through the bounding surface undeviated
		Ray r2 = r.getBranchRay(
				i.p,
				r.getD(),
				i.t,
				r.isReportToConsole()
		);

		// continue tracing the ray through the scene
		// multiply its intensity by the transmission coefficient of the bounding surface
		return scene.getColourAvoidingOrigin(
				r2, i.o, l, scene, traceLevel-1, raytraceExceptionHandler).multiply(getTransmissionCoefficient()
			);
	}
}
