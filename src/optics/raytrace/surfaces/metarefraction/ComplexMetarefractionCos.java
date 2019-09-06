package optics.raytrace.surfaces.metarefraction;

import math.*;

/**
 * 
 * @author Johannes
 *
 */


public final class ComplexMetarefractionCos extends ComplexMetarefraction
{
//	/* (non-Javadoc)
//	 * @see optics.raytrace.surfaces.metarefraction.Metarefraction#clone()
//	 */
//	public ComplexMetarefractionExp clone()
//	{
//		return this;
//	}
	
	/* (non-Javadoc)
	 * @see optics.raytrace.surfaces.metarefraction.ComplexMetarefraction#complexRefractOutwards(math.Complex)
	 */
	@Override
	public Complex complexRefractOutwards(Complex complexIncidentRay)
	{
		return Complex.cos(complexIncidentRay);
	}
	
	/* (non-Javadoc)
	 * @see optics.raytrace.surfaces.metarefraction.ComplexMetarefraction#complexRefractInwards(math.Complex)
	 */
	@Override
	public Complex complexRefractInwards(Complex complexIncidentRay)
	{
		return Complex.arccos(complexIncidentRay);
	}
}
