package optics.raytrace.surfaces;

import math.MyMath;
import math.Vector3D;
import optics.DoubleColour;
import optics.raytrace.GUI.sceneObjects.EditableSceneObjectCollection;
import optics.raytrace.core.LightSource;
import optics.raytrace.core.Orientation;
import optics.raytrace.core.Ray;
import optics.raytrace.core.RaySceneObjectIntersection;
import optics.raytrace.core.RayWithTrajectory;
import optics.raytrace.core.RaytraceExceptionHandler;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.SceneObjectPrimitive;
import optics.raytrace.core.SurfacePropertyPrimitive;
import optics.raytrace.exceptions.RayTraceException;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.voxellations.Voxellation;

/**
 * SurfaceProperty that marks the surface with this property as the boundary surface defining a volume ---
 * the inside of the surface --- to be a voxellated volume.
 * 
 * The volume is filled with sets of surfaces, e.g. parallel, equidistant, planes.
 * The spaces between the surfaces define the "voxels".
 * For example, for three sets of planes in which the planes in each set are perpendicular to the planes in both other
 * sets would enclose cuboid voxels.
 * 
 * Efficient ray tracing through such voxels works like this.
 * Define a "scene" that consists of
 * 1) the two surfaces from each set of surfaces on either side of the starting position (unless the starting position
 *    is on a surface, in which case take the two surfaces on either side of that surface), and
 * 2) the boundary surface itself.
 * Then calculate the first intersection of the ray with this scene.
 * At each surface, affect the ray in whichever way the represented voxel would affect the ray.
 * Keep doing this until the ray ends up on the boundary surface.
 * 
 * @author Johannes Courtial
 */
public abstract class SurfaceOfVoxellatedVolume extends SurfacePropertyPrimitive
{
	private static final long serialVersionUID = -5449045667985167325L;

	/**
	 * the sets of voxellations
	 */
	protected Voxellation[] voxellations;
	
	/**
	 * the (surface of the) scene-object this surface property is associated with
	 */
	protected SceneObject surface;
	
	/**
	 * the maximum number of steps to be taken inside the volume (before black is returned)
	 */
	protected int maxSteps;

	/**
	 * Creates a new surface property that marks a surface as the boundary surface defining a voxellated volume.
	 * @param planeSets	the sets of equidistant, parallel, planes that define the voxels
	 * @param transmissionCoefficient	transmission coefficient on entering and exiting volume
	 */
	public SurfaceOfVoxellatedVolume(Voxellation[] voxellations, SceneObject surface, int maxSteps, double transmissionCoefficient, boolean shadowThrowing)
	{
		super(transmissionCoefficient, shadowThrowing);
		setVoxellations(voxellations);
		setSurface(surface);
		setMaxSteps(maxSteps);
	}
	
	/**
	 * Clone the original transformation-optics-element surface
	 * @param original
	 */
	public SurfaceOfVoxellatedVolume(SurfaceOfVoxellatedVolume original)
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
	public abstract SurfaceOfVoxellatedVolume clone();
//	{
//		return new SurfaceOfVoxellatedVolume(this);
//	}
	
	
	// override this method to customise the behaviour of the ray inside the surface
	
	/**
	 * What to do upon entering the volume.
	 * Override to customise; an example can be found in SurfaceOfVoxellatedAbsorber.
	 * NEED TO MULTIPLY BY TRANSMISSION COEFFICIENT STILL!
	 * @param ray
	 * @param interpupillaryDistance
	 * @param scene
	 * @param l
	 * @param stepsLeft	the number of steps left to be taken inside the volume before black is returned
	 * @param traceLevel
	 * @param raytraceExceptionHandler
	 * @return
	 * @throws RayTraceException
	 */
	public abstract DoubleColour getColourUponEnteringVolume(Ray r1, RaySceneObjectIntersection i1, SceneObject scene, LightSource l, int stepsLeft, int traceLevel, RaytraceExceptionHandler raytraceExceptionHandler)
	throws RayTraceException;
	
	/**
	 * What to do upon starting inside the volume.
	 * Override to customise; an example can be found in SurfaceOfVoxellatedAbsorber.
	 * @param r
	 * @param i
	 * @param voxelIndicesBefore	the indices of the voxel in front of the intersection point; null for outside
	 * @param scene
	 * @param l
	 * @param stepsLeft	the number of steps left to be taken inside the volume before black is returned
	 * @param traceLevel
	 * @param raytraceExceptionHandler
	 * @return
	 * @throws RayTraceException
	 */
	public abstract DoubleColour getColourUponStartingWithinVolume(Ray r, RaySceneObjectIntersection i, SceneObject scene, LightSource l, int stepsLeft, int traceLevel, RaytraceExceptionHandler raytraceExceptionHandler)
	throws RayTraceException;
	

	/**
	 * What to do upon starting inside the volume, but *not* on the <surfaceIndex>th surface of <voxellation>
	 * Override to customise; an example can be found in SurfaceOfVoxellatedAbsorber.
	 * @param r
	 * @param voxellation
	 * @param surfaceIndex
	 * @param i
	 * @param scene
	 * @param l
	 * @param stepsLeft
	 * @param traceLevel
	 * @param raytraceExceptionHandler
	 * @return
	 * @throws RayTraceException
	 */
//	public abstract DoubleColour getColourUponStartingWithinVolumeAvoidingVoxellationSurface(Ray r, Voxellation voxellation, int surfaceIndex, RaySceneObjectIntersection i, SceneObject scene, LightSource l, int stepsLeft, int traceLevel, RaytraceExceptionHandler raytraceExceptionHandler)
//	throws RayTraceException;

	
	// it shouldn't normally be necessary to override the following methods
	
	/**
	 * For raytracing of a ray inside the volume.
	 * Calculates the first intersection between the ray and a collection of scene objects that includes
	 * the plane sets and the bounding surface.
	 * @param r	the ray
	 * @return	the first intersection
	 */
	protected RaySceneObjectIntersection getIntersectionWithVoxelSurface(Ray r, SceneObjectPrimitive originObject)
	{
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
		for(Voxellation voxellation : voxellations)
		{
			try {
				s.addSceneObject(voxellation.getSurfaceOfVoxel(voxellation.getVoxelIndex(r.getP())));
			} catch (Exception e) {
				// not sure under which circumstances this would happen; print the stack trace
				System.err.println("SurfaceOfVoxellatedVolume::getIntersectionWithVoxelSurface: exception?!");
				e.printStackTrace();
			}
		}
		
		RaySceneObjectIntersection i = s.getClosestRayIntersectionAvoidingOrigin(r, null); // was originObject);
		
		if(i == RaySceneObjectIntersection.NO_INTERSECTION)
		{
			new RayTraceException("SurfaceOfVoxellatedVolume::getIntersectionWithVoxelSurface: no intersection with voxel surface.").printStackTrace();
			System.out.println("ray = " + r);
			for(int j = 0; j < s.getNumberOfSceneObjects(); j++)
			{
				SceneObject s2 = s.getSceneObject(j);
				if(s2 instanceof EditableSceneObjectCollection)
				{
					for(int k = 0; k < ((EditableSceneObjectCollection)s2).getNumberOfSceneObjects(); k++)
					{
						System.out.println("      "+k+": "+((EditableSceneObjectCollection)s2).getSceneObject(k));
					}
				}
				else System.out.println("  "+j+": "+s.getSceneObject(j));
			}
			// System.exit(-1);
		}
		
		// then find the intersection of the ray with this collection of scene objects
		return i;
	}
	
	
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
			// System.out.println("Outwards, position = "+i.p+", traceLevel = "+traceLevel);
			if(r.isRayWithTrajectory())
				// the last intersection point is with the outer surface; remove that
				((RayWithTrajectory)r).removeLastIntersectionPoint();

			// the ray must have originated inside the volume, so trace it from here
			return getColourUponStartingWithinVolume(r, i, scene, l, maxSteps, traceLevel, raytraceExceptionHandler);
		}
		else
		{
			// System.out.println("Inwards, position = "+i.p+", traceLevel = "+traceLevel);

			// the ray is entering the volume
			return getColourUponEnteringVolume(r, i, scene, l, maxSteps, traceLevel, raytraceExceptionHandler);
		}
	}
	
	
	// utility methods
	
	/**
	 * The voxel with index <i>i</i> lies between the planes with indices <i>i</i> and <i>i</i>+1
	 * @param position
	 * @return	voxel indices
	 */
	public int[] getVoxelIndices(Vector3D position)
	{
		// System.out.println("getVoxelIndices: position = "+position);
		// if(planeSets == null) return new int[0];
		
		int index[] = new int[voxellations.length];
		
		// for(SetOfEquidistantParallelPlanes planeSet : planeSets)
		for(int i=0; i < voxellations.length; i++)
		{
			index[i] = voxellations[i].getVoxelIndex(position);
		}
		
		return index;
	}

	/**
	 * @param r
	 * @param i
	 * @return	the indices of the voxel behind the intersection i, in the direction of ray r
	 */
	public int[] getVoxelIndicesInFront(Ray r, RaySceneObjectIntersection i)
	{
		// work out which voxel it is behind the intersection
		
		// calculate the next intersection between the ray (with the initial direction, which doesn't matter here as
		// we just want to know which voxel we are in) and the plane sets (or the bounding surface)
		RaySceneObjectIntersection iTest = getIntersectionWithVoxelSurface(
				new Ray(
						i.p,	// starting point
						r.getD(),	// direction
						i.t,	// starting time
						r.isReportToConsole()
					).getAdvancedRay(MyMath.TINY),
					i.o
				);
		
		if(iTest == RaySceneObjectIntersection.NO_INTERSECTION)
			System.out.println("SurfaceOfVoxellatedVolume::getVoxelIndicesInFront: Warning --- no intersection!");
				
		// which voxel are we entering?
		return getVoxelIndices(Vector3D.mean(i.p, iTest.p));
	}
	
	// setters & getters

	public Voxellation[] getVoxellations() {
		return voxellations;
	}

	public void setVoxellations(Voxellation[] voxellations) {
		this.voxellations = voxellations;
	}

	public SceneObject getSurface() {
		return surface;
	}

	public void setSurface(SceneObject surface) {
		this.surface = surface;
	}

	public int getMaxSteps() {
		return maxSteps;
	}

	public void setMaxSteps(int maxSteps) {
		this.maxSteps = maxSteps;
	}
}
