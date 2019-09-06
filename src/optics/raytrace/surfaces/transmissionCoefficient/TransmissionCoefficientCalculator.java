package optics.raytrace.surfaces.transmissionCoefficient;

import java.io.Serializable;

import math.Vector3D;
import optics.raytrace.core.Orientation;
import optics.raytrace.core.RaySceneObjectIntersection;


/**
 * @author johannes
 * An abstract class that allows complex calculations of the transmission coefficient.
 */
public abstract class TransmissionCoefficientCalculator implements Serializable, Cloneable
{
	private static final long serialVersionUID = -7362978870840400773L;

	/**
	 * @param incidentRayDirection
	 * @param intersection
	 * @param orientation	INWARDS or OUTWARDS
	 * @return	the transmission coefficient, calculated for the given <incidentRayDirection>
	 */
	public abstract double calculateTransmissionCoefficient(Vector3D incidentRayDirection, RaySceneObjectIntersection intersection, Orientation orientation);	
}