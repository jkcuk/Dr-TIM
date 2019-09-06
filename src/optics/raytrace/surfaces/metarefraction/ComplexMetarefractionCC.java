package optics.raytrace.surfaces.metarefraction;

import math.Complex;

/**
 * 
 * @author Johannes
 *
 */

public final class ComplexMetarefractionCC extends ComplexMetarefraction
{
	/**
	 * @param incidentRayDirection
	 * @return complex number representing to the refracted ray direction in the surface basis. 
	 */
	@Override
	public Complex complexRefractOutwards(Complex incidentRayDirection)
	{
		return Complex.conjugate(incidentRayDirection);
	}

	/**
	 * @param incidentRayDirection
	 * @return complex number representing to the refracted ray direction in the surface basis. 
	 */
	@Override
	public Complex complexRefractInwards(Complex incidentRayDirection)
	{
		return Complex.conjugate(incidentRayDirection);
	}
} 