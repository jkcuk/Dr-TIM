package optics.raytrace.utility;

import java.util.ArrayList;

import optics.raytrace.core.ParametrisedObject;

import math.Vector3D;

/**
 * A collection of useful methods and enum(s) related to coordinate systems.
 * @author johannes
 */
public class CoordinateSystems
{
	public enum GlobalOrLocalCoordinateSystemType
	{
		GLOBAL_BASIS("Global (x,y,z) coordinate basis"),
		LOCAL_OBJECT_BASIS("Surface's local coordinate system");
		
		private String description;
		private GlobalOrLocalCoordinateSystemType(String description) {this.description = description;}	
		@Override
		public String toString() {return description;}
	}

	public enum CoordinateSystemType
	{
		GLOBAL_BASIS("Global (x,y,z) coordinate basis"),
		LOCAL_OBJECT_BASIS("Surface's local coordinate system"),
		NORMALSED_LOCAL_OBJECT_BASIS("Surface's local coordinate system, normalised");
		
		private String description;
		private CoordinateSystemType(String description) {this.description = description;}	
		@Override
		public String toString() {return description;}
	}

	/**
	 * Return a normalised surface basis for a point on an object's surface.
	 * This consists of three normalised vectors, the first pointing in the direction of the object's first surface direction,
	 * the second pointing in the direction of the object's second surface direction, and the third pointing in the direction
	 * of the object's surface normal.
	 * @param object
	 * @param pointOnObject
	 * @return
	 */
	public static ArrayList<Vector3D> getNormalisedSurfaceBasis(ParametrisedObject object, Vector3D pointOnObject)
	{
		// Retrieve two Vector3Ds, normally tangential to the object's surface at the point and orthogonal to each other...
		ArrayList<Vector3D> surfaceBasis = object.getSurfaceCoordinateAxes(pointOnObject);
		// ... normalise them and collect these together with the surface normal into a basis
		ArrayList<Vector3D> surfaceBasis3DNormalised = new ArrayList<Vector3D>(3);
		surfaceBasis3DNormalised.add(surfaceBasis.get(0).getNormalised());
		surfaceBasis3DNormalised.add(surfaceBasis.get(1).getNormalised());

		// ... and add the (already normalised) surface normal as the third basis vector
		surfaceBasis3DNormalised.add(object.getNormalisedOutwardsSurfaceNormal(pointOnObject));

		return surfaceBasis3DNormalised;
	}

	/**
	 * Return a non-normalised surface basis for a point on an object's surface.
	 * This consists of three not necessarily normalised vectors, the first pointing in the direction of the object's first surface direction,
	 * the second pointing in the direction of the object's second surface direction, and the third pointing in the direction
	 * of the object's surface normal.
	 * @param object
	 * @param pointOnObject
	 * @return
	 */
	public static ArrayList<Vector3D> getSurfaceBasis(ParametrisedObject object, Vector3D pointOnObject)
	{
		// Retrieve two Vector3Ds, normally tangential to the object's surface at the point and orthogonal to each other...
		ArrayList<Vector3D> surfaceBasis = object.getSurfaceCoordinateAxes(pointOnObject);
		// ... and collect these together with the surface normal into a basis
		ArrayList<Vector3D> surfaceBasis3D = new ArrayList<Vector3D>(3);
		surfaceBasis3D.add(surfaceBasis.get(0));
		surfaceBasis3D.add(surfaceBasis.get(1));

		// ... and add the (already normalised) surface normal as the third basis vector
		surfaceBasis3D.add(object.getNormalisedOutwardsSurfaceNormal(pointOnObject));

		return surfaceBasis3D;
	}

	public static Vector3D toNormalisedSurfaceBasis(Vector3D directionVector, ParametrisedObject object, Vector3D pointOnObject)
	{
		return directionVector.toBasis(getNormalisedSurfaceBasis(object, pointOnObject));
	}

	public static Vector3D fromNormalisedSurfaceBasis(Vector3D directionVector, ParametrisedObject object, Vector3D pointOnObject)
	{
		return directionVector.fromBasis(getNormalisedSurfaceBasis(object, pointOnObject));
	}
}
