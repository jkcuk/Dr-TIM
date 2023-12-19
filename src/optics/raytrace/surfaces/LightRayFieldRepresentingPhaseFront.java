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
public class LightRayFieldRepresentingPhaseFront extends LightRayField
{	
	private static final long serialVersionUID = -9108481345387736565L;
	
	/**
	 * the colour of the light-ray field
	 */
	private DoubleColour colour;
	
	
	//  constructors etc.
	
	public LightRayFieldRepresentingPhaseFront(DoubleColour colour, double angularFuzzinessRad)
	{
		super(angularFuzzinessRad);
		this.colour = colour;
	}
	
	public LightRayFieldRepresentingPhaseFront(DoubleColour colour)
	{
		this(colour, 1000);
	}
	
	
	@Override
	public SurfaceProperty clone() {
		return new LightRayFieldRepresentingPhaseFront(getColour(), getAngularFuzzinessRad());
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

	
	// LightRayField methods
	
	/**
	 * @param i
	 * @return	the normalised light-ray direction
	 */
	@Override
	public Vector3D getNormalisedLightRayDirection(RaySceneObjectIntersection i)
	{
		//  placeholder; effectively interprets the surface of the SceneObject this is associated with  as a phase front
		return i.getNormalisedOutwardsSurfaceNormal();
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

