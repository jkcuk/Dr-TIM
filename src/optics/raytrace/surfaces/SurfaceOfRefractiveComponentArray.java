package optics.raytrace.surfaces;

import math.Vector3D;
import optics.DoubleColour;
import optics.raytrace.core.LightSource;
import optics.raytrace.core.Ray;
import optics.raytrace.core.RaySceneObjectIntersection;
import optics.raytrace.core.RaytraceExceptionHandler;
import optics.raytrace.core.SceneObject;
import optics.raytrace.exceptions.RayTraceException;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.voxellations.Voxellation;

/**
 * Surface around a voxellated volume in which each voxel contains a Component.
 * For simulating an array of refractive Components.
 * 
 * @author Johannes Courtial, Maik Locher
 */
public abstract class SurfaceOfRefractiveComponentArray extends SurfaceOfVoxellatedVolume
{
	private static final long serialVersionUID = -4053813165886823808L;

	/**
	 * Creates a new surface property that marks a surface as the boundary surface defining a voxellated volume.
	 * @param planeSets	the sets of equidistant, parallel, planes that define the voxels
	 * @param transmissionCoefficient	transmission coefficient on entering and exiting volume
	 */
	public SurfaceOfRefractiveComponentArray(
			Voxellation[] voxellations,
			SceneObject boundingBox,
			int maxSteps,
			double transmissionCoefficient,
			boolean shadowThrowing
		)
	{
		super(voxellations, boundingBox, maxSteps, transmissionCoefficient, shadowThrowing);
	}
	
	/**
	 * Clone the original transformation-optics-element surface
	 * @param original
	 */
	public SurfaceOfRefractiveComponentArray(SurfaceOfRefractiveComponentArray original)
	{
		this(
				original.getVoxellations(),
				original.getBoundingBox(),
				original.getMaxSteps(),
				original.getTransmissionCoefficient(),
				original.isShadowThrowing()
			);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public abstract SurfaceOfRefractiveComponentArray clone();
//	{
//		return new SurfaceOfVoxellatedLensArray(this);
//	}
	
	
	// override these methods to customise the behaviour of the ray inside the surface
	
	/**
	 * @param voxelIndices
	 * @return	the SceneObject representing the refractive lens corresponding to the voxel with the given voxelIndices
	 */
	public abstract SceneObject getRefractiveComponent(int[] voxelIndices);

	
	public SceneObject getBoundingBox()
	{
		return super.getSurface();
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
//	@Override
//	public DoubleColour getColourUponStartingWithinVolumeAvoidingVoxellationSurface(Ray r, Voxellation voxellationToAvoid, int surfaceIndex, RaySceneObjectIntersection i, SceneObject scene, LightSource l, int stepsLeft, int traceLevel, RaytraceExceptionHandler raytraceExceptionHandler)
//	throws RayTraceException
//	{
//		if(stepsLeft < 0) return DoubleColour.BLACK;
//	
//		// calculate the voxel indices of the voxel that's ahead
//		int[] voxelIndices = getVoxelIndicesInFront(r, i);
//		
//		//
//		// trace the ray through a collection of scene objects that includes the lens and the boundary of the voxel
//		//
//		
//		// create a collection of scene objects...
//		SceneObjectContainer s = new SceneObjectContainer(
//				null,	// description
//				null,	// parent
//				null	// studio
//			);
//		
//		// ... and populate it with the surface associated with this surface property...
//		s.addSceneObject(getSurface());
//		
//		// ... and the surfaces of the voxel containing the ray's start position in each of the voxellations...
//		
//		// work with the advanced ray; in case we are starting on the surface of a voxel (as we usually are), this ensures
//		// that we are then well inside the voxel
//		// Ray rA = r.getAdvancedRay(MyMath.TINY);
//		
//		// if(planeSets != null)	// planeSets simply mustn't be null!
//		for(Voxellation voxellation : voxellations)
//		{
//			try {
//				if(voxellation == voxellationToAvoid)
//					s.addSceneObject(voxellation.getSurfaceOfVoxelAvoidingSurface(voxellation.getVoxelIndex(r.getP()), surfaceIndex));
//				else
//					s.addSceneObject(voxellation.getSurfaceOfVoxel(voxellation.getVoxelIndex(r.getP())));
//			} catch (Exception e) {
//				// not sure under which circumstances this would happen; print the stack trace
//				System.err.println("SurfaceOfVoxellatedLensArray::getColourUponStartingWithinVolume: exception?!");
//				e.printStackTrace();
//			}
//		}
//		
//		// add the lens corresponding to this voxel
//		RefractiveBoxLens lens = getRefractiveLens(voxelIndices);
//		s.addSceneObject(lens);
//
//		for(; stepsLeft >= 0; stepsLeft--)
//		{
//			// now trace the ray through this collection of scene objects
//			i = s.getClosestRayIntersectionAvoidingOrigin(r, i.o);
//
//			if(i == RaySceneObjectIntersection.NO_INTERSECTION)
//			{
//				// this shouldn't happen
//				return DoubleColour.LIGHT_RED;
//			}
//
//			// deal with the intersection
//			if(surface.getSceneObjectPrimitives().contains(i.o))	// is this intersection with the boundary surface?
//			{
//				// the intersection is with the surface; leave the volume
//				// (multiply by the transmission coefficient because of attenuation upon entering volume)
//				return getColourUponLeavingVolume(
//						r, // .getAdvancedRay(MyMath.TINY),	// advance the ray to avoid intersecting with the origin again
//						i, scene, l, stepsLeft, traceLevel, raytraceExceptionHandler);
//			}
//			else if(lens.getSceneObjectPrimitives().contains(i.o))	// is the intersection with the lens?
//			{
//				// intersection is with the lens
//				RefractiveSimple refractiveSurfaceProperty = (RefractiveSimple)(i.o.getSurfaceProperty());
//				
//				// calculate the refracted light-ray direction
//				Vector3D newRayDirection = RefractiveSimple.getRefractedLightRayDirection(
//						r.getD(),	// incidentLightRayDirection
//						i.getNormalisedOutwardsSurfaceNormal(),	// surfaceNormal
//						refractiveSurfaceProperty.getInsideOutsideRefractiveIndexRatio()	// refractiveIndexRatio
//					);
//
//				// start a new ray from the intersection point, with the new light-ray direction
//				r = r.getBranchRay(
//						i.p,
//						r.getD(),
//						i.t
//				);
//			}
//			else
//			{
//				// the intersection is with one of the surfaces separating neighbouring voxels
//				
//				// trace through the next voxel
//				return getColourUponStartingWithinVolume(r, i, scene, l, stepsLeft, traceLevel, raytraceExceptionHandler);
//			}
//			
//			// if this code is reached, the intersection was with the lens;
//			// keep tracing through the voxel, starting from the intersection point, with the new light-ray direction
//		}
//
//		// if this code is reached, we have run out of steps without hitting the voxel boundary;
//		// return black
//		return DoubleColour.BLACK;
//	}

	@Override
	public DoubleColour getColourUponStartingWithinVolume(Ray r, RaySceneObjectIntersection i, SceneObject scene, LightSource l, int stepsLeft, int traceLevel, RaytraceExceptionHandler raytraceExceptionHandler)
	throws RayTraceException
	{
		// return getColourUponStartingWithinVolumeAvoidingVoxellationSurface(r, null, 0, i, scene, l, stepsLeft, traceLevel, raytraceExceptionHandler);
		
		if(stepsLeft < 0) 
			return DoubleColour.YELLOW; //TODO
	
		// calculate the voxel indices of the voxel that's ahead
		int[] voxelIndices = getVoxelIndicesInFront(r, i);
		
		//
		// trace the ray through a collection of scene objects that includes the lens and the boundary of the voxel
		//
		
		// create a collection of scene objects...
		SceneObjectContainer s = new SceneObjectContainer(
				null,	// description
				null,	// parent
				null	// studio
			);
		
		// ... and populate it with the surface associated with this surface property...
		s.addSceneObject(getSurface());
		
		// ... and the surfaces of the voxel containing the ray's start position in each of the voxellations...
		
		// work with the advanced ray; in case we are starting on the surface of a voxel (as we usually are), this ensures
		// that we are then well inside the voxel
		// Ray rA = r.getAdvancedRay(MyMath.TINY);
		
		// if(planeSets != null)	// planeSets simply mustn't be null!
		SceneObjectContainer v = new SceneObjectContainer(null, null, null);
		
		for(Voxellation voxellation : voxellations)
		{
			try {
				v.addSceneObject(voxellation.getSurfaceOfVoxel(voxellation.getVoxelIndex(r.getP())));
			} catch (Exception e) {
				// not sure under which circumstances this would happen; print the stack trace
				System.err.println("SurfaceOfVoxellatedLensArray::getColourUponStartingWithinVolume: exception?!");
				e.printStackTrace();
			}
		}
		s.addSceneObject(v);
		
		// add the lens corresponding to this voxel
		SceneObject refractiveComponent = getRefractiveComponent(voxelIndices);
		s.addSceneObject(refractiveComponent);
		
//		return s.getColourAvoidingOrigin(
//				r,
//				i.o,	
//				l,	// lights -- shouldn't matter
//				s,	// scene
//				stepsLeft,	// traceLevel,
//				new SurfaceOfVoxellatedLensArrayRaytraceExceptionHandler(
//						scene,	// normalScene,
//						traceLevel,	// normalTraceLevel,
//						raytraceExceptionHandler	// normalRaytraceExceptionHandler
//					)	// raytraceExceptionHandler
//			);

		// for(; stepsLeft >= 0; stepsLeft--)	// gives "dead code" warning, for some reason (?)
		while(stepsLeft >= 0)
		{
			stepsLeft--;
			
			// now trace the ray through this collection of scene objects
			i = s.getClosestRayIntersectionAvoidingOrigin(r, i.o);
			
			if(r.isReportToConsole()) {
				System.out.println("Intersection within surface of refractive component array: "+i);
				System.out.println("Voxel Indicies: "+voxelIndices[0]+","+voxelIndices[1]);
			}

			if(i == RaySceneObjectIntersection.NO_INTERSECTION)
			{
				// this shouldn't happen
				throw new RayTraceException("No intersection!?");
			}

			// deal with the intersection
			if(surface.getSceneObjectPrimitives().contains(i.o))	// is this intersection with the boundary surface?
			{
				// the intersection is with the surface; leave the volume
				// (multiply by the transmission coefficient because of attenuation upon entering volume)
				return getColourUponLeavingVolume(
						r, // .getAdvancedRay(MyMath.TINY),	// advance the ray to avoid intersecting with the origin again
						i, scene, l, stepsLeft, traceLevel, raytraceExceptionHandler);
			}
			else if(refractiveComponent.getSceneObjectPrimitives().contains(i.o))	// is the intersection with the lens?
			{
				// intersection is with the lens
				// return DoubleColour.GREEN;
				
				RefractiveSimple refractiveSurfaceProperty = (RefractiveSimple)(i.o.getSurfaceProperty());
				
				// calculate the refracted light-ray direction
				Vector3D newRayDirection = refractiveSurfaceProperty.getRefractedLightRayDirection(r, i);

				// start a new ray from the intersection point, with the new light-ray direction
				r = r.getBranchRay(
						i.p,
						newRayDirection,
						i.t,
						r.isReportToConsole()
				);
			}
			else if(v.getSceneObjectPrimitives().contains(i.o))	// is the intersection with the voxellation surfaces?
			{
				// the intersection is with one of the surfaces separating neighbouring voxels
				// return DoubleColour.CYAN;
				
				return getColourUponIntersectingWithVoxelBoundary(r, i, scene, l, stepsLeft, traceLevel, raytraceExceptionHandler);

				// trace through the next voxel
				// return getColourUponStartingWithinVolume(r, i, scene, l, stepsLeft-1, traceLevel, raytraceExceptionHandler);
				//System.out.println("SurfaceOfVoxellatedLensArray::getColourUponStartingWithinVolume: i.p="+i);
//				return getColourUponStartingWithinVolume(r.getAdvancedRay(MyMath.TINY), i, scene, l, stepsLeft, traceLevel, raytraceExceptionHandler);
			}
			else
			{
				// Don't know what is happening here
				throw new RayTraceException("Ray intersects with nothing!?");
			}
			
			// if this code is reached, the intersection was with the lens;
			// keep tracing through the voxel, starting from the intersection point, with the new light-ray direction
		}

		// if this code is reached, we have run out of steps without hitting the voxel boundary;
		// return black
		return DoubleColour.RED;
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
	 * What to do upon intersecting one of the surfaces separating neighbouring voxels.
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
	public DoubleColour getColourUponIntersectingWithVoxelBoundary(Ray r, RaySceneObjectIntersection i, SceneObject scene, LightSource l, int stepsLeft, int traceLevel, RaytraceExceptionHandler raytraceExceptionHandler)
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
		return getColourUponStartingWithinVolume(r2, i, scene, l, stepsLeft, traceLevel, raytraceExceptionHandler);
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