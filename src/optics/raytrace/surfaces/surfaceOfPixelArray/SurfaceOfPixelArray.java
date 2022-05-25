package optics.raytrace.surfaces.surfaceOfPixelArray;

import optics.DoubleColour;
import optics.raytrace.core.LightSource;
import optics.raytrace.core.Orientation;
import optics.raytrace.core.Ray;
import optics.raytrace.core.RaySceneObjectIntersection;
import optics.raytrace.core.RayWithTrajectory;
import optics.raytrace.core.RaytraceExceptionHandler;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.SurfaceProperty;
import optics.raytrace.exceptions.RayTraceException;
import optics.raytrace.sceneObjects.WrappedSceneObject;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.voxellations.SetOfSurfaces;
import optics.raytrace.voxellations.SetOfSurfaces.OutwardsNormalOrientation;

/**
 * Surface around a voxellated volume in which each voxel contains a number of components.
 * 
 * @author Johannes Courtial, Maik Locher
 */
public abstract class SurfaceOfPixelArray extends SurfaceProperty
{
	private static final long serialVersionUID = 215412709197590033L;

	/**
	 * the sets of voxellations, all of type SetOfSurfaces
	 */
	protected SetOfSurfaces[] voxellations;
	
	/**
	 * the (surface of the) scene-object this surface property is associated with
	 */
	protected SceneObject boundingBox;

	/**
	 * the scene, including the object with this surface property
	 */
	private SceneObject scene;
	
	/**
	 * @param voxellations
	 * @param boundingBox
	 * @param maxSteps
	 * @param transmissionCoefficient
	 * @param shadowThrowing
	 */
	public SurfaceOfPixelArray(
			SetOfSurfaces[] voxellations,
			SceneObject boundingBox,
			SceneObject scene
		)
	{
		this.voxellations = voxellations;
		this.boundingBox = boundingBox;
		this.scene = scene;
	}
	
	/**
	 * Clone the original transformation-optics-element surface
	 * @param original
	 */
	public SurfaceOfPixelArray(SurfaceOfPixelArray original)
	{
		this(
				original.getVoxellations(),
				original.getBoundingBox(),
				original.getScene()
			);
	}
	
	
	// override these methods to customise the behaviour of the ray inside the surface
	
	/**
	 * @param voxelIndices
	 * @return	the SceneObject representing the refractive lens corresponding to the voxel with the given voxelIndices
	 */
	public abstract SceneObject getSceneObjectsInPixel(int[] voxelIndices);

	
	
	// getters & setters
	
	public SetOfSurfaces[] getVoxellations() {
		return voxellations;
	}

	public void setVoxellations(SetOfSurfaces[] voxellations) {
		this.voxellations = voxellations;
	}

	public SceneObject getBoundingBox()
	{
		return boundingBox;
	}
	
	public void setBoundingBox(SceneObject boundingBox)
	{
		this.boundingBox = boundingBox;
	}
	
	public SceneObject getScene() {
		return scene;
	}

	public void setScene(SceneObject scene) {
		this.scene = scene;
	}

	
	//
	// SurfaceProperty methods
	//

	/* (non-Javadoc)
	 * @see optics.raytrace.core.SurfaceProperty#getColour(optics.raytrace.core.Ray, optics.raytrace.core.RaySceneObjectIntersection, optics.raytrace.core.SceneObject, optics.raytrace.core.LightSource, int)
	 */
	@Override
	public DoubleColour getColour(Ray r, RaySceneObjectIntersection i, SceneObject scene, LightSource l, int traceLevel, RaytraceExceptionHandler raytraceExceptionHandler)
	throws RayTraceException
	{
		if (traceLevel < 0) return DoubleColour.BLACK;
		
		// check if the ray is exiting the volume bounded by the surface
		if(Orientation.getOrientation(r.getD(), i.getNormalisedOutwardsSurfaceNormal()) == Orientation.OUTWARDS)
		{
			// the (test) ray hit the bounding box from the inside, so it started within the bounding box;
			// we need to trace through any pixels inside the bounding box
			
			if(r.isRayWithTrajectory())
				// the last intersection point is with the outer surface, but it was just an intersection of the "test ray";
				// remove the intersection
				((RayWithTrajectory)r).removeLastIntersectionPoint();
		}

		// trace the ray through a collection of scene objects that includes the components making up the pixel and the boundary of the voxel
		
		// calculate the indices of the voxel we are currently tracing in
		int voxelIndices[] = new int[voxellations.length];
		for(int v=0; v<voxellations.length; v++)
			voxelIndices[v] = voxellations[v].getVoxelIndex(r.getP());
		
		return getColourStartingInPixel(voxelIndices, r, i, scene, l, traceLevel, raytraceExceptionHandler);
	}
		
	public DoubleColour getColourStartingInPixel(int voxelIndices[], Ray r, RaySceneObjectIntersection i, SceneObject scene, LightSource l, int traceLevel, RaytraceExceptionHandler raytraceExceptionHandler)
	throws RayTraceException
	{
		// create the "pixel scene"
		
		// create a collection of scene objects...
		SceneObjectContainer s = new SceneObjectContainer(
				null,	// description
				null,	// parent
				null	// studio
			);
		
		// ... and populate it with the surface associated with this surface property, ...
		s.addSceneObject(
				new WrappedSceneObject(
						boundingBox,	// sceneObject to be wrapped
						new BoundingBoxSurface(scene)	// surfaceProperty
						),
				true
				);
		
		// ... the surfaces of the voxel, ...
		for(int v=0; v<voxellations.length; v++)
		{
			int voxelIndicesOutside[] = new int[voxellations.length];
			for(int v2=0; v2<voxellations.length; v2++) voxelIndicesOutside[v2] = voxelIndices[v2];
			// add the surfaces that mark the boundary with the neighbouring voxels
			for(OutwardsNormalOrientation o:OutwardsNormalOrientation.values())
			try {
				s.addSceneObject(
						voxellations[v].getBoundaryBetweenVoxels(
								voxelIndices[v], 
								o.getSign(),
								new SurfaceSeparatingVoxels(this, voxelIndices, v, o)	// surfaceProperty
							),
						false
					);
			} catch (Exception e) {
				// not sure under which circumstances this would happen; print the stack trace
				System.err.println("SurfaceOfVoxellatedLensArray::getColourUponStartingWithinVolume: exception?!");
				e.printStackTrace();
			}
		}
		
		// add the refractive component corresponding to this voxel
		s.addSceneObject(getSceneObjectsInPixel(voxelIndices), false);
		
		// and raytrace through the "pixel scene"
		return s.getColourAvoidingOrigin(
				r,	// ray
				i.o,	// originObject
				l,	// lightSource
				s,	// scene
				traceLevel-1,
				raytraceExceptionHandler
//				new SurfaceOfPixelArrayRaytraceExceptionHandler(
//						scene,	// normalScene,
//						raytraceExceptionHandler	// normalRaytraceExceptionHandler
//					)	// raytraceExceptionHandler
			);
	}
}