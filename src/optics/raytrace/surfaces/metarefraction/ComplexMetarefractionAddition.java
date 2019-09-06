package optics.raytrace.surfaces.metarefraction;

import math.*;

/**
 * @author George
 * @author Johannes
 *
 * Adds a complex number to the incident complex ray. 
 */

public class ComplexMetarefractionAddition extends ComplexMetarefraction
{
	private Complex inwardsOffset;
	
	// Constructor
	public ComplexMetarefractionAddition(Complex inwardsOffset)
	{
		this.inwardsOffset = inwardsOffset;
	}
	
	/* (non-Javadoc)
	 * @see optics.raytrace.surfaces.metarefraction.ComplexMetarefraction#complexRefractOutwards(math.Complex)
	 */
	@Override
	public Complex complexRefractOutwards(Complex incidentRayDirection)
	{
		return Complex.difference(incidentRayDirection, inwardsOffset);
	}
	
	/* (non-Javadoc)
	 * @see optics.raytrace.surfaces.metarefraction.ComplexMetarefraction#complexRefractInwards(math.Complex)
	 */
	@Override
	public Complex complexRefractInwards(Complex incidentRayDirection)
	{
		return Complex.sum(incidentRayDirection, inwardsOffset);
	}
}
