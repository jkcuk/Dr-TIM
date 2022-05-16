package optics.raytrace.surfaces;

import java.util.Arrays;

import math.MyMath;
import math.Vector3D;
import optics.DoubleColour;
import optics.raytrace.core.LightSource;
import optics.raytrace.core.Ray;
import optics.raytrace.core.RaySceneObjectIntersection;
import optics.raytrace.core.RayWithTrajectory;
import optics.raytrace.core.RaytraceExceptionHandler;
import optics.raytrace.core.SceneObject;
import optics.raytrace.exceptions.EvanescentException;
import optics.raytrace.exceptions.RayTraceException;
import optics.raytrace.voxellations.Voxellation;

/**
 * Surface around a voxellated volume in which the voxels refract differently.
 * The refractive index of the voxels is described by the method getRefractiveIndex(int[] voxelIndices).
 * 
 * This class does for refraction what SurfaceOfVoxellatedAbsorber does for absorption.
 * @author E Orife, Johannes Courtial
 */
public class SurfaceOfVoxellatedRefractor extends SurfaceOfVoxellatedVolume
{
	private static final long serialVersionUID = -55753589638773204L;
	
	private static final boolean DEBUG = false;

	/**
	 * Creates a new surface property that marks a surface as the boundary surface defining a voxellated refractor.
	 * @param surfaceSets	the sets of surfaces that define the voxels
	 * @param surface	the SceneObject this SurfaceProperty is associated with
	 * @param maxSteps	the maximum number of steps used for tracing rays through the volume before black is returned
	 * @param transmissionCoefficient	transmission coefficient on entering and exiting volume
	 */
	public SurfaceOfVoxellatedRefractor(Voxellation[] voxellations, SceneObject surface, int maxSteps, double transmissionCoefficient, boolean shadowThrowing)
	{
		super(voxellations, surface, maxSteps, transmissionCoefficient, shadowThrowing);
	}
	
	/**
	 * Clone the original transformation-optics-element surface
	 * @param original
	 */
	public SurfaceOfVoxellatedRefractor(SurfaceOfVoxellatedRefractor original)
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
	public SurfaceOfVoxellatedRefractor clone()
	{
		return new SurfaceOfVoxellatedRefractor(this);
	}
	
	
	// override these methods to customise the behaviour of the ray inside the surface
	
	/**
	 * @param voxelIndices
	 * @return	the refractive index for the voxel with these voxelIndices, or Double.NaN if there is a problem
	 */
	public double getRefractiveIndex1(int[] voxelIndices)
	// throws Exception
	{
		return 2;
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
	public DoubleColour getColourUponStartingWithinVolume(
			Ray r,
			RaySceneObjectIntersection i,
			SceneObject scene,
			LightSource l,
			int stepsLeft,
			int traceLevel,
			RaytraceExceptionHandler raytraceExceptionHandler
		)
	throws RayTraceException
	{
		if(stepsLeft < 0) return DoubleColour.BLACK;
		
		// calculate the next intersection between the ray and the plane sets (or the bounding surface)
		RaySceneObjectIntersection i2 = getIntersectionWithVoxelSurface(r.getAdvancedRay(MyMath.TINY), i.o);
		
		if(i2 == RaySceneObjectIntersection.NO_INTERSECTION)
		{
			throw new RayTraceException("No intersection... refractive index zero?");
		}
		
		if(r.isRayWithTrajectory())
		{
			// the last intersection point is with the outer surface; remove that
			// ((RayWithTrajectory)r).removeLastIntersectionPoint();
			
			// instead, add the intersection point with the surface of the voxel the ray starts in
			((RayWithTrajectory)r).addIntersectionPoint(i2.p);
		}
				
		// which voxel has the ray passed through?
		int[] voxelIndices = getVoxelIndices(Vector3D.mean(i.p, i2.p));
		double nVoxel = getRefractiveIndex1(voxelIndices);

		if(nVoxel == Double.NaN)
		{
			(new RayTraceException("Calculation of refractive index of voxel "+voxelIndices+" failed.")).printStackTrace();	// uncomment for debugging
//			System.exit(-1);
			
			// something is wrong --- return a "warning colour"
			return DoubleColour.ORANGE;
		}

			// check if the intersection is with the surface
			if(surface.getSceneObjectPrimitives().contains(i2.o))
			{
				// the intersection is with the surface; leave the volume
				// (multiply by the transmission coefficient because of attenuation upon entering volume)
				return getColourUponLeavingVolume(
						r, i2, nVoxel, scene, l, stepsLeft-1, traceLevel, raytraceExceptionHandler);
			}
			else
			{
				if(DEBUG) System.out.println("SurfaceOfVoxellatedRefractor::getColourUponStartingWithinVolume: r="+r+", surface="+surface+", intersection="+i2);

				// the intersection is with one of the planes; deal with it
				return getColourUponIntersectingWithSurface(
						r, i2, nVoxel, scene, l, stepsLeft-1, traceLevel, raytraceExceptionHandler);
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
		if(stepsLeft < 0) return DoubleColour.BLACK;
		
		if(DEBUG) System.out.println("SurfaceOfVoxellatedRefractor::getColourUponEnteringVolume: r="+r+", i="+i+", stepsLeft="+stepsLeft);

		// First work out which voxel it is behind the surface, so that we can ask for its refractive index
		int[] voxelIndices = getVoxelIndicesInFront(r, i);
		
		// calculate the refractive index of that voxel
		double nVoxel;
		// try {
			nVoxel = getRefractiveIndex1(voxelIndices);
			// if(DEBUG) System.out.println("::getColourUponEnteringVolume: nVoxel="+nVoxel);
			if(DEBUG) System.out.println("SurfaceOfVoxellatedRefractor::getColourUponEnteringVolume: voxelIndices="+Arrays.toString(voxelIndices)+", nVoxel="+nVoxel);
		// }
//		catch (EvanescentException e)
//		{
//			return DoubleColour.BLUE;
//		}
		// catch (Exception e)
		if(nVoxel == Double.NaN)
		{
		 	(new RayTraceException("Calculation of refractive index failed")).printStackTrace();	// uncomment for debugging
			
			// something is wrong --- return a "warning colour"
			return DoubleColour.ORANGE;
		}

		try
		{
			// new light-ray direction
			Vector3D d2 = RefractiveSimple.getRefractedLightRayDirection(
					r.getD(),	// incidentLightRayDirection
					i.getNormalisedOutwardsSurfaceNormal(),	// surfaceNormal
					1/nVoxel	// refractiveIndexRatio n_behind / n_inFront
				);
			
			if(DEBUG) System.out.println("SurfaceOfVoxellatedRefractor::getColourUponEnteringVolume: d2 = "+d2);

			// calculate the ray immediately after it has entered the volume
			// the ray direction gets refracted according to Snell's law
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
	 * What to do upon intersecting one of the surfaces inside the volume.
	 * Override to customise.
	 * @param r
	 * @param i
	 * @param nPrevious	refractive index of the voxel in front of the plane
	 * @param scene
	 * @param l
	 * @param stepsLeft
	 * @param traceLevel
	 * @param raytraceExceptionHandler
	 * @return
	 * @throws RayTraceException
	 */
	public DoubleColour getColourUponIntersectingWithSurface(Ray r, RaySceneObjectIntersection i, double nPrevious, SceneObject scene, LightSource l, int stepsLeft, int traceLevel, RaytraceExceptionHandler raytraceExceptionHandler)
	throws RayTraceException
	{
 		if(stepsLeft < 0) return DoubleColour.GREEN;
		
		if(DEBUG) System.out.println("SurfaceOfVoxellatedRefractor::getColourUponIntersectingWithSurface: r="+r+", i="+i+", stepsLeft="+stepsLeft+", traceLevel="+traceLevel);
		
		// First work out which voxel it is behind the surface, so that we can ask for its refractive index
		int[] voxelIndices = getVoxelIndicesInFront(r, i);
		
		// calculate the refractive index of that voxel
		double nVoxel;
		// try {
			nVoxel = getRefractiveIndex1(voxelIndices);
		// }
		// catch (Exception e)
		if(nVoxel == Double.NaN)
		{
			 (new RayTraceException("Calculation of refractive index failed")).printStackTrace();	// uncomment for debugging

			// something is wrong --- return a "warning colour"
			return DoubleColour.ORANGE;
		}

		try
		{
			// calculate the ray immediately after it has entered the volume
			// the ray direction gets refracted according to Snell's law
			Ray r2 = r.getBranchRay(
					i.p,
					RefractiveSimple.getRefractedLightRayDirection(
							r.getD(),	// incidentLightRayDirection
							i.getNormalisedOutwardsSurfaceNormal(),	// surfaceNormal
							nPrevious/nVoxel	// refractiveIndexRatio n_behind / n_inFront
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
	public DoubleColour getColourUponLeavingVolume(Ray r, RaySceneObjectIntersection i, double nPrevious, SceneObject scene, LightSource l, int stepsLeft, int traceLevel, RaytraceExceptionHandler raytraceExceptionHandler)
	throws RayTraceException
	{
 		// if(stepsLeft < 0) return DoubleColour.BLACK;
		
		if(DEBUG) System.out.println("SurfaceOfVoxellatedRefractor::getColourUponLeavingVolume: r = "+r+", i="+i+", stepsLeft="+stepsLeft);
		
		try
		{
			// calculate the ray immediately after it has left the volume;
			// the ray direction gets refracted according to Snell's law
			Ray r2 = r.getBranchRay(
					i.p,
					RefractiveSimple.getRefractedLightRayDirection(
							r.getD(),	// incidentLightRayDirection
							i.getNormalisedOutwardsSurfaceNormal(),	// surfaceNormal
							nPrevious	// refractiveIndexRatio n_behind / n_inFront; n_inFront = 1 here
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
