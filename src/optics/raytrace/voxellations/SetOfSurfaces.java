package optics.raytrace.voxellations;

import math.Vector3D;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.SurfaceProperty;
import optics.raytrace.exceptions.RayTraceException;
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
	
	public enum OutwardsNormalOrientation
	{
		POSITIVE("Positive", +1),
		NEGATIVE("Negative", -1);

		private String description;
		private int sign;
		
		private OutwardsNormalOrientation(String description, int sign)
		{
			this.description = description;
			this.sign = sign;
		}
		
		public String toString()
		{
			return description;
		}
		
		public int getSign()
		{
			return sign;
		}
	}

	/**
	 * Return the surface with index <i>i</i>.
	 * The object returned is intended to be used for finding the intersection point between the surface and a ray.
	 * It is <i>not</i> linked into a scene or studio, and nor does it have a surface property.
	 * @param i
	 * @return	if <i>i</i> is an integer, the surface with index <i>i</i>, otherwise a surface at a position corresponding to the index <i>i</i>
	 * @throws	null if the index r is out of bounds 
	 */
	public abstract SceneObject getSurface(double i, OutwardsNormalOrientation outwardsNormalOrientation, SurfaceProperty surfaceProperty);
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
	
	/**
	 * @param i
	 * @param deltaI	either +1 or -1, indicating which one of the two boundary surfaces this is
	 * @return	the surface that forms the boundary between voxels #i and #(i+deltaI), where deltaI is either +1 or -1
	 */
	public SceneObject getBoundaryBetweenVoxels(int i, int deltaI, SurfaceProperty surfaceProperty)
	{
		switch(deltaI)
		{
		case -1:
			// the boundary between voxels i and i-1 is surface i-1
			return getSurface(i-1, OutwardsNormalOrientation.NEGATIVE, surfaceProperty);
		case +1:
			// the boundary between voxels i and i+1 is surface i
			return getSurface(i,   OutwardsNormalOrientation.POSITIVE, surfaceProperty);
		default:
			(new RayTraceException("deltaI should take values -1 or +1, not "+deltaI)).printStackTrace();
			return null;
		}
	}
	
	@Override
	public SceneObject getSurfaceOfVoxel(int i)
	{
		SceneObjectContainer surface = new SceneObjectContainer(
				"surface of voxel #" + i,
				null,	// parent
				null	// studio
			);
				
		// voxel #i is defined to lie between surface #i-1 and surface #i
		try {
			surface.addSceneObject(getBoundaryBetweenVoxels(i, -1, null));
			surface.addSceneObject(getBoundaryBetweenVoxels(i, +1, null));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return surface;
	}	
}
