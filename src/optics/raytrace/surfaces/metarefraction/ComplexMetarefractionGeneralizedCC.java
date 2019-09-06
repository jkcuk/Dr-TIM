package optics.raytrace.surfaces.metarefraction;

import math.Complex;

/**
 * 
 * @author Johannes
 *
 */

public final class ComplexMetarefractionGeneralizedCC extends ComplexMetarefraction
{
	private double imaginaryAxisAngle;
	
	public ComplexMetarefractionGeneralizedCC(double imaginaryAxisAngle)
	{
		super();
		this.imaginaryAxisAngle = imaginaryAxisAngle;	// phi
	}

	/**
	 * @param incidentRayDirection
	 * @return complex number representing to the refracted ray direction in the surface basis. 
	 */
	@Override
	public Complex complexRefractOutwards(Complex incidentRayDirection)
	{
		return Complex.product(
				Complex.conjugate(
						Complex.product(
								incidentRayDirection,
								Complex.expI(imaginaryAxisAngle)
						)
					),
				Complex.expI(-imaginaryAxisAngle)	
			);
	}

	public double getImaginaryAxisAngle() {
		return imaginaryAxisAngle;
	}

	public void setImaginaryAxisAngle(double imaginaryAxisAngle) {
		this.imaginaryAxisAngle = imaginaryAxisAngle;
	}

	/**
	 * @param incidentRayDirection
	 * @return complex number representing to the refracted ray direction in the surface basis. 
	 */
	@Override
	public Complex complexRefractInwards(Complex incidentRayDirection)
	{
		return Complex.product(
				Complex.conjugate(
						Complex.product(
								incidentRayDirection,
								Complex.expI(-imaginaryAxisAngle)
						)
					),
				Complex.expI(imaginaryAxisAngle)	
			);
	}
} 