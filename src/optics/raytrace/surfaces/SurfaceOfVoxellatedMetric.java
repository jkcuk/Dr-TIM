package optics.raytrace.surfaces;

import java.util.Arrays;

import math.MyMath;
import math.Vector3D;
import optics.DoubleColour;
import optics.raytrace.core.LightSource;
import optics.raytrace.core.Ray;
import optics.raytrace.core.RaySceneObjectIntersection;
import optics.raytrace.core.RaytraceExceptionHandler;
import optics.raytrace.core.SceneObject;
import optics.raytrace.exceptions.EvanescentException;
import optics.raytrace.exceptions.RayTraceException;
import optics.raytrace.utility.CoordinateSystems.GlobalOrLocalCoordinateSystemType;
import optics.raytrace.voxellations.Voxellation;

/**
 * Surface around a voxellated volume in which the voxels refract differently upon hitting metric interfaces.
 * 
 * This class does for generalised refraction what SurfaceOfVoxellatedRefractor does for ordinary refraction.
 * Ideally this should be the superclass of SurfaceOfVoxellatedRefractor, but the implementation is too 
 * different not to be problematic.
 * 
 * In particular, a SurfaceOfVoxellatedRefractor uses a refractive index, whereas this class uses a metric 
 * tensor. (TODO: what about a refraction tensor?) This means the method  getRefractiveIndex(int[] 
 * voxelIndices)  need be replaced by something else (for now assumed to be the metric).
 * 
 * @author E Orife, Johannes Courtial
 */
public class SurfaceOfVoxellatedMetric extends SurfaceOfVoxellatedVolume
{
	// new serial UID
	private static final long serialVersionUID = 8382088911676769307L;

	private static final boolean DEBUG = false;

	/**
	 * Creates a new surface property that marks a surface as the boundary surface defining a voxellated refractor.
	 * @param surfaceSets	the sets of surfaces that define the voxels
	 * @param surface	the SceneObject this SurfaceProperty is associated with
	 * @param maxSteps	the maximum number of steps used for tracing rays through the volume before black is returned
	 * @param transmissionCoefficient	transmission coefficient on entering and exiting volume
	 */
	public SurfaceOfVoxellatedMetric(Voxellation[] voxellations, SceneObject surface, int maxSteps, double transmissionCoefficient, boolean shadowThrowing)
	{
		super(voxellations, surface, maxSteps, transmissionCoefficient, shadowThrowing);
	}
	
	/**
	 * Clone the original transformation-optics-element surface
	 * @param original
	 */
	public SurfaceOfVoxellatedMetric(SurfaceOfVoxellatedMetric original)
	{
		this(
				original.getVoxellations(),
				original.getSurface(),
				original.getMaxSteps(),
				original.getTransmissionCoefficient(),
				original.isShadowThrowing()
			);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public SurfaceOfVoxellatedMetric clone()
	{
		return new SurfaceOfVoxellatedMetric(this);
	}
	
	
	// override these methods to customise the behaviour of the ray inside the surface
	
	/**
	 * @param voxelIndices
	 * @return	the refractive index for the voxel with these voxelIndices or 
	 */
	public double[] getMetricTensor(int[] voxelIndices)
	throws Exception
	{
		// for the moment, return a metric tensor that corresponds to refractive index 2
		return MetricInterface.getMetricTensorForRefractiveIndex(2);
	}

	/**
	 * What to do upon starting inside the volume.
	 * @param r
	 * @param i
	 * @param scene
	 * @param l
	 * @param stepsLeft
	 * @param traceLevel
	 * @param raytraceExceptionHandler
	 * @return
	 * @throws RayTraceException
	 */
	@Override
	public DoubleColour getColourUponStartingWithinVolume(Ray r, RaySceneObjectIntersection i, SceneObject scene, LightSource l, int stepsLeft, int traceLevel, RaytraceExceptionHandler raytraceExceptionHandler)
	throws RayTraceException
	{
		if(stepsLeft < 0) return DoubleColour.BLUE;
		
		// calculate the next intersection between the ray and the plane sets (or the bounding surface)
		RaySceneObjectIntersection i2 = getIntersectionWithVoxelSurface(r.getAdvancedRay(MyMath.TINY), i.o);
		
		if(i2 == RaySceneObjectIntersection.NO_INTERSECTION)
		{
			throw new RayTraceException("No intersection... something equivalent to zero refractive index?");
		}
				
		// which voxel has the ray passed through?
		int[] voxelIndices = getVoxelIndices(Vector3D.mean(i.p, i2.p));

		try {
			// check if the intersection is with the surface
			if(surface.getSceneObjectPrimitives().contains(i2.o))
			{
				// the intersection is with the surface; leave the volume
				// (multiply by the transmission coefficient because of attenuation upon entering volume)
				return getColourUponLeavingVolume(
						r, i2, getMetricTensor(voxelIndices), scene, l, stepsLeft-1, traceLevel, raytraceExceptionHandler);
			}
			else
			{
				if(DEBUG) System.out.println("SurfaceOfVoxellatedMetric::getColourUponStartingWithinVolume: r="+r+", surface="+surface+", intersection="+i2+", steps left="+(stepsLeft-1));

				// the intersection is with one of the planes; deal with it
				return getColourUponIntersectingWithPlane(
						r, i2, getMetricTensor(voxelIndices), scene, l, stepsLeft-1, traceLevel, raytraceExceptionHandler);
			}
		}
//		catch (EvanescentException e)
//		{
//			return DoubleColour.BLUE;
//		}
		catch (Exception e)
		{
			// e.printStackTrace();
			// System.exit(-1);
			
			// something is wrong --- return a "warning colour"
			return DoubleColour.ORANGE;
		}
	}


	/**
	 * What to do upon entering the volume.
	 * @param ray
	 * @param i
	 * @param scene
	 * @param l
	 * @param stepsLeft
	 * @param traceLevel
	 * @param raytraceExceptionHandler
	 * @return
	 * @throws RayTraceException
	 */
	@Override
	public DoubleColour getColourUponEnteringVolume(Ray r, RaySceneObjectIntersection i, SceneObject scene, LightSource l, int stepsLeft, int traceLevel, RaytraceExceptionHandler raytraceExceptionHandler)
	throws RayTraceException
	{
		if(stepsLeft < 0) return DoubleColour.GREEN;
		
		if(DEBUG) System.out.println("SurfaceOfVoxellatedMetric::getColourUponEnteringVolume: r="+r+", i="+i);

		// First work out which voxel it is behind the surface, so that we can ask for its refractive index
		int[] voxelIndices = getVoxelIndicesInFront(r, i);
		
		// calculate the metric tensor of that voxel
		double[] gVoxel;
		try {
			gVoxel = getMetricTensor(voxelIndices);
			
			if(DEBUG) System.out.println("SurfaceOfVoxellatedMetric::getColourUponEnteringVolume: voxelIndices="+Arrays.toString(voxelIndices)+", gVoxel="+Arrays.toString(gVoxel));
		}
//		catch (EvanescentException e)
//		{
//			return DoubleColour.BLUE;
//		}
		catch (Exception e)
		{
			// something is wrong --- return a "warning colour"
			return DoubleColour.ORANGE;
		}

		try
		{
			// new light-ray direction
			Vector3D d2 = MetricInterface.getRefractedLightRayDirection(
					r.getD(),	//incident light-ray direction
					i,	// intersection
			        MetricInterface.euclideanMetricTensor,	//metric tensor in incident-light space       
			        gVoxel,	//metric tensor in refracted-light space
			        MetricInterface.RefractionType.POSITIVE_REFRACTION,	// type of refraction
			        GlobalOrLocalCoordinateSystemType.GLOBAL_BASIS,
			        false	//allow complex path lengths?				
				);
			
			if(DEBUG) System.out.println("SurfaceOfVoxellatedMetric::getColourUponEnteringVolume: d2 = "+d2);
			
			// calculate the ray immediately after it has entered the volume
			// the ray direction gets refracted according to (generalised?) Snell's law
			Ray r2 = r.getBranchRay(i.p, d2, i.t, r.isReportToConsole());
			
			// now we're within the volume, so let the getColourUponStartingWithinVolume method do the job
			return getColourUponStartingWithinVolume(r2, i, scene, l, stepsLeft-1, traceLevel, raytraceExceptionHandler).multiply(getTransmissionCoefficient());
		}
		catch(EvanescentException e)
		{
			// TIR --- keep tracing outside the volume
			
			// create a new ray with the reflected light-ray direction
			Ray rTIR = r.getBranchRay(
					i.p,
					Reflective.getReflectedLightRayDirection(
							r.getD(),	// incident light-ray direction
							i.getNormalisedOutwardsSurfaceNormal()	// surfaceNormal
						),
					i.t,
					r.isReportToConsole()
			);
			
			// continue tracing the ray through the scene outside the volume
			return scene.getColourAvoidingOrigin(
					rTIR, i.o, l, scene, traceLevel-1, raytraceExceptionHandler
				);
		}
	}

	/**
	 * What to do upon intersecting one of the planes inside the volume.
	 * Override to customise.
	 * @param r
	 * @param i
	 * @param gPrevious	metric tensor of the voxel in front of the plane
	 * @param scene
	 * @param l
	 * @param stepsLeft
	 * @param traceLevel
	 * @param raytraceExceptionHandler
	 * @return
	 * @throws RayTraceException
	 */
	public DoubleColour getColourUponIntersectingWithPlane(Ray r, RaySceneObjectIntersection i, double[] gPrevious, SceneObject scene, LightSource l, int stepsLeft, int traceLevel, RaytraceExceptionHandler raytraceExceptionHandler)
	throws RayTraceException
	{
 		if(stepsLeft < 0) return DoubleColour.YELLOW;
		
		if(DEBUG) System.out.println("SurfaceOfVoxellatedMetric::getColourUponIntersectingWithPlane: r="+r+", i="+i+", traceLevel="+traceLevel+", stepsLeft="+stepsLeft);
		
		// First work out which voxel it is behind the surface, so that we can ask for its refractive index
		int[] voxelIndices = getVoxelIndicesInFront(r, i);
		
		// calculate the metric tensor of that voxel
		double[] gNext;
		try {
			gNext = getMetricTensor(voxelIndices);
		}
		catch (Exception e)
		{
			// something is wrong --- return a "warning colour"
			return DoubleColour.YELLOW;
		}

		try
		{
			// calculate the ray immediately after it has entered the volume
			// the ray direction gets refracted according to (generalised?) Snell's law
			Ray r2 = r.getBranchRay(
					i.p,					
					MetricInterface.getRefractedLightRayDirection(
							r.getD(),	//incident light-ray direction
							i,	// intersection
							gPrevious,	//metric tensor in incident-light space       
							gNext,	//metric tensor in refracted-light space 
							MetricInterface.RefractionType.POSITIVE_REFRACTION,	// type of refraction
					        GlobalOrLocalCoordinateSystemType.GLOBAL_BASIS,
							false	//allow complex path lengths?				
						),					
					i.t,
					r.isReportToConsole()
				);

			// now we're within the volume, so let the getColourUponStartingWithinVolume method do the job
			return getColourUponStartingWithinVolume(r2, i, scene, l, stepsLeft-1, traceLevel, raytraceExceptionHandler);
		}
		catch(EvanescentException e)
		{
			// TIR --- reflect off plane
			
			// create a new ray with the reflected light-ray direction
			Ray rTIR = r.getBranchRay(
					i.p,
					Reflective.getReflectedLightRayDirection(
							r.getD(),	// incident light-ray direction
							i.getNormalisedOutwardsSurfaceNormal()	// surfaceNormal
						),
					i.t,
					r.isReportToConsole()
			);
			
			// we're still within the volume, so let the getColourUponStartingWithinVolume method do the job
			return getColourUponStartingWithinVolume(rTIR, i, scene, l, stepsLeft-1, traceLevel, raytraceExceptionHandler);
		}
	}

	/**
	 * What to do upon leaving the volume.
	 * In this please-override-me method, the surface acts like a semi-transparent window.
	 * Override to customise.
	 * @param r
	 * @param i
	 * @param nPrevious	refractive index of the medium the ray was in before hitting the intersection
	 * @param scene
	 * @param l
	 * @param stepsLeft
	 * @param traceLevel
	 * @param raytraceExceptionHandler
	 * @return
	 * @throws RayTraceException
	 */
	public DoubleColour getColourUponLeavingVolume(Ray r, RaySceneObjectIntersection i, double[] gPrevious, SceneObject scene, LightSource l, int stepsLeft, int traceLevel, RaytraceExceptionHandler raytraceExceptionHandler)
	throws RayTraceException
	{
 		 if(stepsLeft < 0) return DoubleColour.YELLOW;
		
		 if(DEBUG) System.out.println("SurfaceOfVoxellatedMetric::getColourUponLeavingVolume: r = "+r+", i="+i);
		
		try
		{
			// calculate the ray immediately after it has entered the volume
			// the ray direction gets refracted according to (generalised?) Snell's law
			Ray r2 = r.getBranchRay(
					i.p,					
					MetricInterface.getRefractedLightRayDirection(
							r.getD(),	//incident light-ray direction
							i,	// intersection
							gPrevious,	//metric tensor in incident-light space       
							MetricInterface.euclideanMetricTensor,	//metric tensor in refracted-light space 
							MetricInterface.RefractionType.POSITIVE_REFRACTION,	// type of refraction
					        GlobalOrLocalCoordinateSystemType.GLOBAL_BASIS,
							false	//allow complex path lengths?				
						),					
					i.t,
					r.isReportToConsole()
				);

			// continue tracing the ray through the scene
			// multiply its intensity by the transmission coefficient of the bounding surface
			return scene.getColourAvoidingOrigin(
					r2, i.o, l, scene, traceLevel-1, raytraceExceptionHandler
				).multiply(getTransmissionCoefficient());
		}
		catch (EvanescentException e)
		{
			// TIR --- keep tracing inside the volume
			
			// create a new ray with the reflected light-ray direction
			Ray rTIR = r.getBranchRay(
					i.p,
					Reflective.getReflectedLightRayDirection(
							r.getD(),	// incident light-ray direction
							i.getNormalisedOutwardsSurfaceNormal()	// surfaceNormal
						),
					i.t,
					r.isReportToConsole()
			);
			
			// we're still within the volume, so let the getColourUponStartingWithinVolume method do the job
			return getColourUponStartingWithinVolume(rTIR, i, scene, l, stepsLeft-1, traceLevel, raytraceExceptionHandler);
		}
	}
}