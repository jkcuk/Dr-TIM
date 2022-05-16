package optics.raytrace.surfaces;

import math.MyMath;
import math.Vector3D;
import optics.DoubleColour;
import optics.raytrace.core.LightSource;
import optics.raytrace.core.Ray;
import optics.raytrace.core.RaySceneObjectIntersection;
import optics.raytrace.core.RaytraceExceptionHandler;
import optics.raytrace.core.SceneObject;
import optics.raytrace.exceptions.RayTraceException;
import optics.raytrace.voxellations.Voxellation;

/**
 * Surface around a voxellated volume in which the voxels absorb differently.
 * The absorption of the voxels is described by the methods get***AbsorptionCoefficient(int[] voxelIndices), where
 * *** stands for Red, Green and Blue.
 * 
 * @author Johannes Courtial
 */
public class SurfaceOfVoxellatedAbsorber extends SurfaceOfVoxellatedVolume
{
	private static final long serialVersionUID = 8378104038386519325L;

	/**
	 * Creates a new surface property that marks a surface as the boundary surface defining a voxellated volume.
	 * @param planeSets	the sets of equidistant, parallel, planes that define the voxels
	 * @param transmissionCoefficient	transmission coefficient on entering and exiting volume
	 */
	public SurfaceOfVoxellatedAbsorber(Voxellation[] voxellations, SceneObject surface, int maxSteps, double transmissionCoefficient, boolean shadowThrowing)
	{
		super(voxellations, surface, maxSteps, transmissionCoefficient, shadowThrowing);
	}
	
	/**
	 * Clone the original transformation-optics-element surface
	 * @param original
	 */
	public SurfaceOfVoxellatedAbsorber(SurfaceOfVoxellatedAbsorber original)
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
	public SurfaceOfVoxellatedAbsorber clone()
	{
		return new SurfaceOfVoxellatedAbsorber(this);
	}
	
	
	// override these methods to customise the behaviour of the ray inside the surface
	
	/**
	 * @param voxelIndices
	 * @return	the absorption coefficient for the red colour component of the voxel with these voxelIndices
	 */
	public double getRedAbsorptionCoefficient(int[] voxelIndices)
	{
		return 0;
	}

	/**
	 * @param voxelIndices
	 * @return	the absorption coefficient for the green colour component of the voxel with these voxelIndices
	 */
	public double getGreenAbsorptionCoefficient(int[] voxelIndices)
	{
		return 0;
	}

	/**
	 * @param voxelIndices
	 * @return	the absorption coefficient for the blue colour component of the voxel with these voxelIndices
	 */
	public double getBlueAbsorptionCoefficient(int[] voxelIndices)
	{
		return 0;
	}
	
	/**
	 * What to do upon starting inside the volume.
	 * Override to customise.
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
		RaySceneObjectIntersection i2 = getIntersectionWithVoxelSurface(r.getAdvancedRay(MyMath.TINY), null);	// was i.o
		
		if(i2 == RaySceneObjectIntersection.NO_INTERSECTION)
		{
			// this shouldn't happen
			return DoubleColour.LIGHT_RED;
		}
		
		// which voxel have we just passed through?
		int[] voxelIndices = getVoxelIndices(Vector3D.mean(i.p, i2.p));
		
		// what distance have we just passed through?
		double distance = Vector3D.difference(i2.p, i.p).getLength();
		
//		System.out.println("getColourUponEnteringVolume: distance="+distance);
		
		DoubleColour c;
		
		// check if the intersection is with the surface
		if(surface.getSceneObjectPrimitives().contains(i2.o))
		{
			// the intersection is with the surface; leave the volume
			// (multiply by the transmission coefficient because of attenuation upon entering volume)
			c = getColourUponLeavingVolume(
					r, // .getAdvancedRay(MyMath.TINY),	// advance the ray to avoid intersecting with the origin again
					i2, scene, l, stepsLeft, traceLevel, raytraceExceptionHandler);
		}
		else
		{
//			System.out.println("r2="+r2+", surface="+surface+", intersection="+i2);

			// the intersection is with one of the planes; deal with it
			// (multiply by the transmission coefficient because of attenuation upon entering volume)
			c = getColourUponIntersectingWithPlane(
					r, i2, scene, l, stepsLeft-1, traceLevel, raytraceExceptionHandler);
		}
		
		// from SurfaceOfTintedSolid
		// multiply each of the colour components by exp(- alpha * distance), where alpha is the
		// absorption coefficient for this colour component
		return new DoubleColour(
				c.getR() * Math.exp(-getRedAbsorptionCoefficient(voxelIndices)*distance),
				c.getG() * Math.exp(-getGreenAbsorptionCoefficient(voxelIndices)*distance),
				c.getB() * Math.exp(-getBlueAbsorptionCoefficient(voxelIndices)*distance)
			);
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
	 * What to do upon intersecting one of the planes inside the volume.
	 * Override to customise.
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
		if(stepsLeft < 0) return DoubleColour.BLACK;
		
		// System.out.println("SurfaceOfVoxellatedAbsorber::getColourUponIntersectingWithPlane: r="+r+", i="+i+", traceLevel="+traceLevel);
		
		// calculate the ray after intersecting the plane;
		// here, the ray passes through the bounding surface undeviated
		Ray r2 = r.getBranchRay(
				i.p,
				r.getD(),
				i.t,
				r.isReportToConsole()
		);
		
		// then do whatever rays do within the volume
		return getColourUponStartingWithinVolume(r2, i, scene, l, stepsLeft-1, traceLevel, raytraceExceptionHandler);
	}

	/**
	 * What to do upon leaving the volume.
	 * In this please-override-me method, the surface acts like a semi-transparent window.
	 * Override to customise.
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
		// if(stepsLeft < 0) return DoubleColour.BLACK;

		// System.out.println("getColourUponLeavingVolume: r = "+r+", i="+i);
		
		// calculate the ray immediately after it has left the volume;
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
