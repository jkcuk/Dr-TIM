package optics.raytrace.sceneObjects.solidGeometry;

import java.util.ArrayList;

import math.*;
import optics.raytrace.core.One2OneParametrisedObject;
import optics.raytrace.core.SceneObjectPrimitive;

public class One2OneParametrisedSceneObjectPrimitiveInverse extends SceneObjectPrimitiveInverse
implements One2OneParametrisedObject
{
	private static final long serialVersionUID = -1828102193810291130L;

	/**
	 * Creates the inverse of a scene object primitive
	 * 
	 * @param sop	scene object primitive
	 */
	public One2OneParametrisedSceneObjectPrimitiveInverse(SceneObjectPrimitive sop)
	{
		super(sop);
	}
	
	public One2OneParametrisedSceneObjectPrimitiveInverse clone()
	{
		return new One2OneParametrisedSceneObjectPrimitiveInverse(sop.clone());
	}
	
	public static SceneObjectPrimitiveInverse getSuitableSceneObjectPrimitiveInverse(SceneObjectPrimitive sop)
	{
		if(sop instanceof One2OneParametrisedObject) return new One2OneParametrisedSceneObjectPrimitiveInverse(sop);
		else return new SceneObjectPrimitiveInverse(sop);
	}

	@Override
	public ArrayList<String> getSurfaceCoordinateNames() {
		return ((One2OneParametrisedObject)sop).getSurfaceCoordinateNames();
	}

	@Override
	public Vector2D getSurfaceCoordinates(Vector3D p) {
		return ((One2OneParametrisedObject)sop).getSurfaceCoordinates(p);
	}

	@Override
	public ArrayList<Vector3D> getSurfaceCoordinateAxes(Vector3D p) {
		return ((One2OneParametrisedObject)sop).getSurfaceCoordinateAxes(p);
	}

	@Override
	public Vector3D getPointForSurfaceCoordinates(double u, double v) {
		return ((One2OneParametrisedObject)sop).getPointForSurfaceCoordinates(u,v);
	}
	
	@Override
	public String getType()
	{
		return "Inverse";
	}
}
