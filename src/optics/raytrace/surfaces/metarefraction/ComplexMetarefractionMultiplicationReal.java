package optics.raytrace.surfaces.metarefraction;

import math.*;

/**
 * 
 * @author Johannes
 *
 */

public class ComplexMetarefractionMultiplicationReal extends ComplexMetarefraction
{
	/**
	 * The factor by which the complex number representing the light-ray direction
	 * gets multiplied on entering the object (i.e. passing through the surface inwards).
	 */
	private double inwardsFactor;
	
	/**
	 * The factor by which the complex number representing the light-ray direction
	 * gets multiplied on entering the object (i.e. passing through the surface inwards).
	 * 
	 * This factor is the ratio between the inside and outside refractive indices.
	 * It can be real or complex; if it is real, the multiplication corresponds simply
	 * to Snell's law; if it is complex, the modulus corresponds to Snell's-law refraction,
	 * the argument describes ray rotation about the surface normal.
	 * 
	 * @param inwardsFactor the ratio of refractive indices on inside and outside
	 */
	public ComplexMetarefractionMultiplicationReal(double inwardsFactor)
	{
		super();
		this.inwardsFactor = inwardsFactor;
	}
	
	@Override
	public Complex complexRefractOutwards(Complex incidentRayDirection)
	{
		return Complex.division(incidentRayDirection, inwardsFactor);
	}

	@Override
	public Complex complexRefractInwards(Complex incidentRayDirection)
	{
		return Complex.product(inwardsFactor, incidentRayDirection);
	}

	public double getInwardsFactor() {
		return inwardsFactor;
	}

	public void setInwardsFactor(double inwardsFactor) {
		this.inwardsFactor = inwardsFactor;
	}
}
