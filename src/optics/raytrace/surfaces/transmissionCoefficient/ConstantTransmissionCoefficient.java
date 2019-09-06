package optics.raytrace.surfaces.transmissionCoefficient;

import math.Vector3D;
import optics.raytrace.core.Orientation;
import optics.raytrace.core.RaySceneObjectIntersection;
import optics.raytrace.core.SurfacePropertyPrimitive;

/**
 * @author johannes
 * A class representing a constant transmission coefficient
 */
public class ConstantTransmissionCoefficient extends TransmissionCoefficientCalculator
{
	private static final long serialVersionUID = 2662045527474149173L;

	/**
	 * surface-property primitive, which has stored in it the constant transmission coefficient
	 */
	private SurfacePropertyPrimitive surfacePropertyPrimitive;
	
	
	//
	// constructors
	//

	/**
	 * @param transmissionCoefficient
	 */
	public ConstantTransmissionCoefficient(SurfacePropertyPrimitive surfacePropertyPrimitive)
	{
		super();
		this.surfacePropertyPrimitive = surfacePropertyPrimitive;
	}
	
	
	//
	// getters & setters
	//
	
	public SurfacePropertyPrimitive getSurfacePropertyPrimitive() {
		return surfacePropertyPrimitive;
	}


	public void setSurfacePropertyPrimitive(SurfacePropertyPrimitive surfacePropertyPrimitive) {
		this.surfacePropertyPrimitive = surfacePropertyPrimitive;
	}

	/**
	 * set the <surfacePropertyPrimitive>'s transmission coefficient to <transmissionCoefficient>
	 * @param transmissionCoefficient
	 */
	public void setTransmissionCoefficient(double transmissionCoefficient)
	{
		surfacePropertyPrimitive.setTransmissionCoefficient(transmissionCoefficient);
	}

	
	//
	// TransmissionCoefficientCalculator methods
	//

	@Override
	public double calculateTransmissionCoefficient(Vector3D incidentRayDirection, RaySceneObjectIntersection intersection, Orientation orientation)
	{
		return surfacePropertyPrimitive.getTransmissionCoefficient();
	}
}
