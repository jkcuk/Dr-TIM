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
	
	private Vector3D position;
	
	/**
	 * if true, the rays are directed towards the  position, otherwise away from it.
	 */
	private boolean raysTowardsPosition;
	
	//  constructors etc.
	
	public LightRayFieldRepresentingPointLightSource(
			DoubleColour colour, 
			Vector3D position, 
			boolean raysTowardsPosition, 
			double angularFuzzinessRad
		)
	{
		super(angularFuzzinessRad);
		this.colour = colour;
		this.position = position;
		this.raysTowardsPosition =  raysTowardsPosition;
	}
	
	public LightRayFieldRepresentingPointLightSource(DoubleColour colour, Vector3D position)
	{
		this(colour, position, false, 1000);
	}
	
	
	@Override
	public SurfaceProperty clone() {
		return new LightRayFieldRepresentingPointLightSource(
				getColour(),
				getPosition(),
				isRaysTowardsPosition(),
				getAngularFuzzinessRad()
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
	public Vector3D getPosition() {
		return position;
	}

	/**
	 * @param position the position to set
	 */
	public void setPosition(Vector3D position) {
		this.position = position;
	}

	/**
	 * @return the raysTowardsPosition
	 */
	public boolean isRaysTowardsPosition() {
		return raysTowardsPosition;
	}

	/**
	 * @param raysTowardsPosition the raysTowardsPosition to set
	 */
	public void setRaysTowardsPosition(boolean raysTowardsPosition) {
		this.raysTowardsPosition = raysTowardsPosition;
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
		return Vector3D.difference(i.p, position).getNormalised().getProductWith(raysTowardsPosition?-1:1);
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

