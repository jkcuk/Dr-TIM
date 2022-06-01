package optics.raytrace.surfaces.surfaceOfPixelArray;

import math.Vector3D;
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
import optics.raytrace.utility.SingleSlitDiffraction;
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
	 * the "global" scene, including the object with this surface property
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

	public SurfaceSeparatingVoxels getSurfaceSeparatingVoxels(
			int voxellationIndicesOnInside[],
			int voxellationNumber,
			OutwardsNormalOrientation outwardsNormalOrientation
		)
	{
		return new SurfaceSeparatingVoxels(this, voxellationIndicesOnInside, voxellationNumber, outwardsNormalOrientation);
	}

	public BoundingBoxSurface getBoundingBoxSurface(
			int voxellationIndicesOnInside[],
			SceneObject scene
		)
	{
		return new BoundingBoxSurface(scene, this);
	}
	
	// for diffraction, which is (approximately) simulated in the BoundingBoxSurface when leaving the bounding box
	
	/**
	 * override to simulate diffraction
	 * @return	true to simulate diffraction, false otherwise
	 */
	public boolean isSimulateDiffraction()
	{
		return false;
	}
	
	public double getLambda()
	{
		return 550e-9;	// green
	}
	
	public double getPixelSideLengthU()
	{
		return 1;
	}
	
	public double getPixelSideLengthV()
	{
		return 1;
	}
	
	public Vector3D getuHat()
	{
		return Vector3D.X;
	}
	
	public Vector3D getvHat()
	{
		return Vector3D.Y;
	}



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
	public DoubleColour getColour(Ray r, RaySceneObjectIntersection i, SceneObject scene_ignore, LightSource l, int traceLevel, RaytraceExceptionHandler raytraceExceptionHandler)
			throws RayTraceException
	{
		if (traceLevel < 0) return DoubleColour.BLACK;

		Ray r2;
		boolean enterPixelFromOutside;

		// check if the ray is exiting the volume bounded by the surface
		if(Orientation.getOrientation(r.getD(), i.getNormalisedOutwardsSurfaceNormal()) == Orientation.OUTWARDS)
		{
			// the (test) ray hit the bounding box from the inside, so it started within the bounding box;
			// we need to trace through any pixels inside the bounding box

			if(r.isRayWithTrajectory())
				// the last intersection point is with the outer surface, but it was just an intersection of the "test ray";
				// remove the intersection
				((RayWithTrajectory)r).removeLastIntersectionPoint();

			r2 = r;
			enterPixelFromOutside = false;
		}
		else
		{
			r2 = r.getBranchRay(i.p, r.getD(), i.t, r.isReportToConsole());
			enterPixelFromOutside = true;
		}

		// trace the ray through a collection of scene objects that includes the components making up the pixel and the boundary of the voxel

		// calculate the indices of the voxel we are currently tracing in
		int voxelIndices[] = new int[voxellations.length];
		for(int v=0; v<voxellations.length; v++)
			voxelIndices[v] = voxellations[v].getVoxelIndex(r2.getP());

		if(enterPixelFromOutside) return getColourEnteringPixelFromOutside(voxelIndices, r2, i, scene, l, traceLevel, raytraceExceptionHandler);
		else return getColourStartingInPixel(voxelIndices, r2, i, scene, l, traceLevel, raytraceExceptionHandler);
	}

	public DoubleColour getColourEnteringPixelFromOutside(int voxelIndices[], Ray r, RaySceneObjectIntersection i, SceneObject scene_ignore, LightSource l, int traceLevel, RaytraceExceptionHandler raytraceExceptionHandler)
	throws RayTraceException
	{
		return getColourStartingInPixel(voxelIndices, r, i, scene_ignore, l, traceLevel, raytraceExceptionHandler);
	}

	public DoubleColour getColourStartingInPixel(int voxelIndices[], Ray r, RaySceneObjectIntersection i, SceneObject scene_ignore, LightSource l, int traceLevel, RaytraceExceptionHandler raytraceExceptionHandler)
	throws RayTraceException
	{
		// create the "pixel scene"

//		for(int j=0; j<voxelIndices.length; j++)
//			System.out.println("voxel index ["+j+"] = "+voxelIndices[j]);
		
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
						getBoundingBoxSurface(
								voxelIndices,
								scene	// surfaceProperty
							)
						),
				true
				);

		// ... the surfaces of the voxel, ...
		for(int v=0; v<voxellations.length; v++)
		{
			// add the surfaces that mark the boundary with the neighbouring voxels
			for(OutwardsNormalOrientation o:OutwardsNormalOrientation.values())
				try {
					s.addSceneObject(
							voxellations[v].getBoundaryBetweenVoxels(
									voxelIndices[v], 
									o.getSign(),
									// SurfaceColour.RED_SHINY
									getSurfaceSeparatingVoxels(
											voxelIndices,	// voxellationIndicesOnInside[],
											v,	// voxellationNumber,
											o	// outwardsNormalOrientation
										)	// surfaceProperty
									),
							true
							);
				} catch (Exception e) {
					// not sure under which circumstances this would happen; print the stack trace
					System.err.println("SurfaceOfVoxellatedLensArray::getColourUponStartingWithinVolume: exception?!");
					e.printStackTrace();
				}
		}

		// add the refractive component corresponding to this voxel
		SceneObject sceneObjectsInPixel = getSceneObjectsInPixel(voxelIndices);
		s.addSceneObject(sceneObjectsInPixel, true);
		
		// and raytrace through the "pixel scene"
		return s.getColourAvoidingOrigin(
				r,	// ray
				i.o,	// originObject
				l,	// lightSource
				s,	// scene
				traceLevel-1,
				raytraceExceptionHandler
				);
	}
	
	
	/**
	 * calculating shadows would be complicated, so -- for the moment -- we don't
	 */
	@Override
	public boolean isShadowThrowing() {
		return false;
	}
}