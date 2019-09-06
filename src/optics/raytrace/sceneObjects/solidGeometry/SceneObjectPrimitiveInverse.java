package optics.raytrace.sceneObjects.solidGeometry;

import java.io.Serializable;

import math.*;
import optics.DoubleColour;
import optics.raytrace.core.LightSource;
import optics.raytrace.core.Ray;
import optics.raytrace.core.RaySceneObjectIntersection;
import optics.raytrace.core.RaytraceExceptionHandler;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.SceneObjectPrimitive;
import optics.raytrace.core.Transformation;
import optics.raytrace.exceptions.RayTraceException;

public class SceneObjectPrimitiveInverse extends SceneObjectPrimitive implements Serializable
{
	private static final long serialVersionUID = -2860478127481229527L;

	protected SceneObjectPrimitive sop;
	
	/**
	 * Creates the inverse of a scene object primitive
	 * 
	 * @param sop	scene object primitive
	 */
	public SceneObjectPrimitiveInverse(SceneObjectPrimitive sop)
	{
		super(sop.description+" (inverted)", sop.getSurfaceProperty(), sop.getParent(), sop.getStudio());
		this.sop = sop;
	}
	
	public SceneObjectPrimitiveInverse clone()
	{
		return new SceneObjectPrimitiveInverse(sop.clone());
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.SceneObjectPrimitive#getColourAtIntersection(optics.raytrace.Ray, optics.raytrace.RaySceneObjectIntersection, optics.raytrace.SceneObject, optics.raytrace.LightSource, int)
	 */
	@Override
	public DoubleColour getColourAtIntersection(Ray incomingRay, 
			RaySceneObjectIntersection i, SceneObject scene, LightSource l, int traceLevel, RaytraceExceptionHandler raytraceExceptionHandler)
	throws RayTraceException
	{
		// TODO Is this right?  What happens if the object surface is reflective?
		return sop.getColourAtIntersection(incomingRay, i, scene, l, traceLevel, raytraceExceptionHandler);
	}
	
	/* (non-Javadoc)
	 * @see optics.raytrace.SceneObjectPrimitive#getNormalisedSurfaceNormal(optics.raytrace.Vector3D)
	 */
	public Vector3D getNormalisedOutwardsSurfaceNormal(Vector3D p)
	{
		// return a Vector3D pointing in the opposite direction from the surface normal of sop
		return sop.getNormalisedOutwardsSurfaceNormal(p).getReverse();
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.SceneObject#getClosestRayIntersection(optics.raytrace.Ray)
	 */
	public RaySceneObjectIntersection getClosestRayIntersection(Ray ray)
	{
		RaySceneObjectIntersection i = sop.getClosestRayIntersection(ray);
		if(i == RaySceneObjectIntersection.NO_INTERSECTION)
			return RaySceneObjectIntersection.NO_INTERSECTION;
		
		return new RaySceneObjectIntersection(
				i.p,
				this,
				i.t
		);
	}

	public RaySceneObjectIntersection getClosestRayIntersectionAvoidingOrigin(Ray ray, SceneObject originObject)
	{
		if((this == originObject) || (sop == originObject))
			// avoid calculating the intersection where the ray originated,
			// calculate the intersection with a slightly advanced ray
			return getClosestRayIntersection(ray.getAdvancedRay(MyMath.TINY));

		return getClosestRayIntersection(ray);
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.SceneObject#insideObject(optics.raytrace.Vector3D)
	 */
	public boolean insideObject(Vector3D p)
	{
		return !sop.insideObject(p);
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.SceneObject#transform(optics.raytrace.Transformation)
	 */
	public SceneObjectPrimitiveInverse transform(Transformation t)
	{
		return new SceneObjectPrimitiveInverse(sop.transform(t));
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "SceneObjectPrimitiveInverse [inverse of " + sop + "]";
	}
	
	@Override
	public String getType()
	{
		return "Inverse";
	}
}
