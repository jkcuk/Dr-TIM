package optics.raytrace.surfaces.metarefraction;

import math.*;

/**
 * @author George, Johannes
 *
 * ComplexMetarefractionPower raises the complex number representing the incident ray direction
 * to a given power
 */
public class ComplexMetarefractionPower extends ComplexMetarefraction
{
	// Introduce Variables
	
	public double inwardsExponent;
	
	// Constructor
	public ComplexMetarefractionPower(double inwardsExponent)
	{
		this.inwardsExponent = inwardsExponent;
	}
	
//	/* (non-Javadoc)
//	 * @see optics.raytrace.surfaces.metarefraction.Metarefraction#clone()
//	 */
//	public ComplexMetarefractionPower clone()
//	{
//		return new ComplexMetarefractionPower(exponent);
//	}
	
	/* (non-Javadoc)
	 * @see optics.raytrace.surfaces.metarefraction.ComplexMetarefraction#complexRefractInwards(math.Complex)
	 */
	@Override
	public Complex complexRefractInwards(Complex incidentRayDirection)
	{
		return Complex.power(incidentRayDirection, inwardsExponent);
	}
	
	/* (non-Javadoc)
	 * @see optics.raytrace.surfaces.metarefraction.ComplexMetarefraction#complexRefractOutwards(math.Complex)
	 */
	@Override
	public Complex complexRefractOutwards(Complex incidentRayDirection)
	{
		return Complex.power(incidentRayDirection, 1/inwardsExponent);
	}
}
