package optics.raytrace.surfaces;

import math.Vector3D;
import optics.raytrace.core.Ray;
import optics.raytrace.core.RaySceneObjectIntersection;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.SceneObjectPrimitive;
import optics.raytrace.core.Studio;
import optics.raytrace.core.Transformation;

/**
 * A special SceneObject for use with SurfacePropertyLayerStack.
 * 
 * This SceneObject forms the scene when tracing in layers of surface properties.
 * The next layer is always immediately intersected, at the start position.
 * 
 * @author johannes
 *
 */
public class InsideOfSurfacePropertyLayerStack extends SceneObjectPrimitive
{
	private static final long serialVersionUID = -8770066495739711898L;
	
	
	/**
	 * the index of the layer in the stack that was last hit
	 */
	private int indexOfCurrentLayer;
	
	private RaySceneObjectIntersection raySceneObjectIntersection;
	
	/**
	 * @param surfacePropertyLayerStack	the SurfacePropertyLayerStack
	 * @param indexOfCurrentLayer	the layer currently being dealt with
	 * @param sceneObjectPrimitive	the SceneObjectPrimitive whose surface is <surfacePropertyLayerStack>
	 * @param scene	the scene, for further raytracing if/when the ray exits the SurfacePropertyLayerStack
	 * @param studio	the studio, for further raytracing if/when the ray exits the SurfacePropertyLayerStack
	 */
	public InsideOfSurfacePropertyLayerStack(
			SurfacePropertyLayerStack surfacePropertyLayerStack,
			int indexOfCurrentLayer,
			RaySceneObjectIntersection raySceneObjectIntersection,
			SceneObject scene, Studio studio)
	{
		super(null, surfacePropertyLayerStack, scene, studio);
		this.indexOfCurrentLayer = indexOfCurrentLayer;
		this.raySceneObjectIntersection = raySceneObjectIntersection;
	}

	public int getIndexOfCurrentLayer() {
		return indexOfCurrentLayer;
	}

	public void setIndexOfCurrentLayer(int indexOfCurrentLayer) {
		this.indexOfCurrentLayer = indexOfCurrentLayer;
	}

	public RaySceneObjectIntersection getRaySceneObjectIntersection() {
		return raySceneObjectIntersection;
	}

	public void setRaySceneObjectIntersection(RaySceneObjectIntersection raySceneObjectIntersection) {
		this.raySceneObjectIntersection = raySceneObjectIntersection;
	}

	@Override
	public RaySceneObjectIntersection getClosestRayIntersection(Ray ray)
	{
		return new RaySceneObjectIntersection(ray.getP(), raySceneObjectIntersection.o, ray.getT());
	}

	@Override
	public boolean insideObject(Vector3D p) {
		return false;
	}

	@Override
	public SceneObjectPrimitive clone() {
		return null;
	}

	@Override
	public Vector3D getNormalisedOutwardsSurfaceNormal(Vector3D p) {
		return raySceneObjectIntersection.o.getNormalisedOutwardsSurfaceNormal(p);
	}

	@Override
	public SceneObjectPrimitive transform(Transformation t) {
		return this;
	}
	
	@Override
	public String getType()
	{
		return "Layer stack";
	}
}
