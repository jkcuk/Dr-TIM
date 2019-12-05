package optics.raytrace.voxellations;

import math.Vector3D;
import optics.raytrace.core.SceneObject;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;


/**
 * @author johannes, E. Orife
 * 
 * A set of surfaces, e.g. parallel, equidistant, planes.
 * The spaces between the surfaces can then define "voxels".
 * For example, for three sets of planes in which the planes in each set are perpendicular to the planes in both other
 * sets would enclose cuboid voxels.
 * 
 * This is useful for dealing with "voxellated" volumes.
 * An efficient way to trace through such a voxellated volume could be the following:
 * If the starting point of the ray is inside the volume, define a "scene" that consists of
 * the two closest surfaces in each set of surfaces (unless the starting point is on a surface, in which case take the two
 * next closest surfaces) and the volume's boundary surface, and trace to the nearest intersection with this scene.
 */
public abstract class SetOfSurfaces extends Voxellation
{		
	private static final long serialVersionUID = 6218432787524758463L;

	/**
	 * Return the surface with index <i>i</i>.
	 * The object returned is intended to be used for finding the intersection point between the surface and a ray.
	 * It is <i>not</i> linked into a scene or studio, and nor does it have a surface property.
	 * @param i
	 * @return	if <i>i</i> is an integer, the surface with index <i>i</i>, otherwise a surface at a position corresponding to the index <i>i</i>
	 * @throws	null if the index r is out of bounds 
	 */
	public abstract SceneObject getSurface(double i);
	// throws Exception;
		
	/**
	 * Returns the "surface index" of the position.
	 * If the position lies on a surface, then the index is the number of that surface.
	 * If the position lies half-way between surfaces 3 and 4, then the index is 3.5.
	 * @param position
	 * @return	"surface index" of position
	 **/
	public abstract double getSurfaceIndex(Vector3D position);

	/**
	 * The voxel with index <i>i</i> lies between the surfaces with indices <i>i</i>-1 and <i>i</i>
	 * @param position
	 * @return	voxel indices
	 */
	@Override
	public int getVoxelIndex(Vector3D position)
	{
		return (int)(Math.floor(getSurfaceIndex(position)+1));
	}
	
//	/**
//	 * @param r	position relative to the surfaces (e.g. 1.5 = half-way between surfaces 1 and 2)
//	 * @return	surface #floor(r)
//	 * @throws	Exception if the index r is out of bounds 
//	 */
//	public SceneObject getPreviousSurface(double r)
//	throws Exception
//	{
//		double f = Math.floor(r);
//		
//		// does the relative position r correspond to a position on a surface?
//		if(r-f < MyMath.EPSILON)
//		{
//			// r corresponds to a position on a surface; return the previous surface
//			return getSurface(f-1);
//		}
//		else
//		{
//			// r corresponds to a position between two surfaces; return the "previous" one
//			return getSurface(f);
//		}
//	}
//
//	/**
//	 * @param r	position relative to the surfaces (e.g. 1.5 = half-way between surfaces 1 and 2)
//	 * @return	surface #ceil(r)
//	 * @throws	Exception if the index r is out of bounds 
//	 */
//	public SceneObject getNextSurface(double r)
//	throws Exception
//	{
//		double c = Math.ceil(r);
//
//		// does the relative position r correspond to a position on a surface?
//		if(c-r < MyMath.EPSILON)
//		{
//			// r corresponds to a position on a surface; return the next surface
//			return getSurface(c+1);
//		}
//		else
//		{
//			// r corresponds to a position between two surfaces; return the "next" one
//			return getSurface(c);
//		}
//	}

	@Override
	public SceneObject getSurfaceOfVoxel(int i)
	{
		SceneObjectContainer surface = new SceneObjectContainer(
				"surface of voxel #" + i,
				null,	// parent
				null	// studio
			);
				
		// voxel #i is defined to lie between surface #i-1 and surface #i
		
		// System.out.println("i="+i);
		try {
			// System.out.println("getSurface(i-1)="+getSurface(i-1));
			// System.out.println("getSurface(i)="+getSurface(i));
			// if(i>0) 
			surface.addSceneObject(getSurface(i-1));
			surface.addSceneObject(getSurface(i));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// System.exit(-1);
		return surface;
	}
}
