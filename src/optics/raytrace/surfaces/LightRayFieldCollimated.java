package optics.raytrace.surfaces;

import math.Vector3D;
import optics.DoubleColour;
import optics.raytrace.core.RaySceneObjectIntersection;
import optics.raytrace.core.SurfaceProperty;

/**
 * A surface property that represents a field of collimated light rays, 
 * i.e. a uniform field of parallel rays, i.e. the light-ray field corresponding to a uniform plane wave
 * 
 * @author Johannes Courtial
 */
public class LightRayFieldCollimated extends LightRayField
{	
	private static final long serialVersionUID = -1843326604699879775L;

	/**
	 * the colour of the light-ray field
	 */
	private DoubleColour colour;
	
	private Vector3D normalisedLightRayDirection;
		
	//  constructors etc.
	
	public LightRayFieldCollimated(
			DoubleColour colour, 
			Vector3D normalisedLightRayDirection, 
			double angularFuzzinessRad,
			boolean bidirectional
		)
	{
		super(angularFuzzinessRad, bidirectional);
		this.colour = colour;
		this.normalisedLightRayDirection = normalisedLightRayDirection.getNormalised();
	}
	
	public LightRayFieldCollimated(DoubleColour colour, Vector3D normalisedLightRayDirection)
	{
		this(colour, normalisedLightRayDirection, 1000, true);
	}
	
	
	@Override
	public SurfaceProperty clone() {
		return new LightRayFieldCollimated(
				getColour(),
				getNormalisedLightRayDirection(),
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
	 * @return the normalisedLightRayDirection
	 */
	public Vector3D getNormalisedLightRayDirection() {
		return normalisedLightRayDirection;
	}

	/**
	 * @param normalisedLightRayDirection the normalisedLightRayDirection to set
	 */
	public void setNormalisedLightRayDirection(Vector3D normalisedLightRayDirection) {
		this.normalisedLightRayDirection = normalisedLightRayDirection.getNormalised();
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
		return normalisedLightRayDirection;
	}
	
	/**
	 * the colour of of the ray in the field at i.p
	 */
	@Override
	public DoubleColour getRayColour(RaySceneObjectIntersection i)
	{
		return colour;
	}
}

