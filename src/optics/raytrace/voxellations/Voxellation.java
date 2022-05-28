package optics.raytrace.voxellations;

import java.io.Serializable;

import math.Vector3D;
import optics.raytrace.core.SceneObject;


/**
 * @author johannes, E. Orife
 * 
 * A set of voxels, separated by surfaces.
 * 
 * This is useful for dealing with "voxellated" volumes.
 */
public abstract class Voxellation implements Serializable
{
	private static final long serialVersionUID = 2545925340323595400L;

	/**
	 * @param position
	 * @return	the index of the voxel the position lies in
	 */
	public abstract int getVoxelIndex(Vector3D position);
		
	/**
	 * Return the surface of voxel with index <i>i</i>.
	 * @param i
	 * @return	the surface of the voxel with index <i>i</i>
	 */
	public abstract SceneObject getSurfaceOfVoxel(int i)
	throws IndexOutOfBoundsException;
	
	/**
	 * Return the surface of voxel <i>i</i>, but don't include surface #<i>avoidSurface</i>
	 * @param i
	 * @return
	 * @throws IndexOutOfBoundsException
	 */
//	public abstract SceneObject getSurfaceOfVoxelAvoidingSurface(int i, int avoidSurface)
//	throws IndexOutOfBoundsException;
	
	public static String toString(int voxellationIndices[])
	{
		String s = "{";
		for(int v=0; v<voxellationIndices.length; v++)
		{
			if(v>0) s+=", ";
			s+=voxellationIndices[v];
		}
		s+="}";
		return s;
	}
	
}
