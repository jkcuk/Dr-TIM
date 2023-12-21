package optics.raytrace.surfaces;

import math.Vector3D;
import optics.DoubleColour;
import optics.raytrace.core.RaySceneObjectIntersection;
import optics.raytrace.core.SurfaceProperty;

/**
 * A surface property that represents a phase front
 * 
 * @author Johannes Courtial
 */
public class LightRayFieldRepresentingPointLightSource extends LightRayField
{	
	private static final long serialVersionUID = -4691567369762438262L;

	/**
	 * the colour of the light-ray field
	 */
	private DoubleColour colour;
	
	private Vector3D pointLightSourcePosition;
	
	/**
	 * if true, the rays are directed towards the  source, otherwise away from it.
	 */
	private boolean raysTowardsSource;
	
	//  constructors etc.
	
	public LightRayFieldRepresentingPointLightSource(
			DoubleColour colour, 
			Vector3D pointLightSourcePosition, 
			boolean raysTowardsSource, 
			double angularFuzzinessRad,
			boolean bidirectional
		)
	{
		super(angularFuzzinessRad, bidirectional);
		this.colour = colour;
		this.pointLightSourcePosition = pointLightSourcePosition;
		this.raysTowardsSource =  raysTowardsSource;
	}
	
	public LightRayFieldRepresentingPointLightSource(DoubleColour colour, Vector3D position)
	{
		this(colour, position, false, 1000, true);
	}
	
	
	@Override
	public SurfaceProperty clone() {
		return new LightRayFieldRepresentingPointLightSource(
				getColour(),
				getPointLightSourcePosition(),
				isRaysTowardsPosition(),
				getAngularFuzzinessRad(),
				isBidirectional()
			);
	}

	
	
	
	// setters & getters

	/**
	 * @return the colour
	 */
	public DoubleColour getColour() {
		return colour;
	}

	/**
	 * @param colour the colour to set
	 */
	public void setColour(DoubleColour colour) {
		this.colour = colour;
	}

	/**
	 * @return the position
	 */
	public Vector3D getPointLightSourcePosition() {
		return pointLightSourcePosition;
	}

	/**
	 * @param position the position to set
	 */
	public void setPointLightSourcePosition(Vector3D pointLightSourcePosition) {
		this.pointLightSourcePosition = pointLightSourcePosition;
	}

	/**
	 * @return the raysTowardsSource
	 */
	public boolean isRaysTowardsPosition() {
		return raysTowardsSource;
	}

	/**
	 * @param raysTowardsSource the raysTowardsSource to set
	 */
	public void setRaysTowardsPosition(boolean raysTowardsSource) {
		this.raysTowardsSource = raysTowardsSource;
	}

	
	// LightRayField methods
	

	/**
	 * @param i
	 * @return	the normalised light-ray direction
	 */
	@Override
	public Vector3D getNormalisedLightRayDirection(RaySceneObjectIntersection i)
	{
		//  placeholder; effectively interprets the surface of the SceneObject this is associated with  as a phase front
		return Vector3D.difference(i.p, pointLightSourcePosition).getNormalised().getProductWith(raysTowardsSource?-1:1);
	}
	
	/**
	 * the colour of of the ray in the field at i.p
	 */
	@Override
	public DoubleColour getRayColour(RaySceneObjectIntersection i)
	{
		return colour;
	}

//	@Override
//	public void setShadowThrowing(boolean shadowThrowing) {
//		// don't do anything
//	}
}

