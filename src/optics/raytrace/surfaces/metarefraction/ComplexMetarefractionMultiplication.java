package optics.raytrace.surfaces.metarefraction;

import math.*;

/**
 * 
 * @author Johannes
 *
 */

public class ComplexMetarefractionMultiplication extends ComplexMetarefraction
{
	/**
	 * The factor by which the complex number representing the light-ray direction
	 * gets multiplied on entering the object (i.e. passing through the surface inwards).
	 */
	private Complex inwardsFactor;
	
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
	public ComplexMetarefractionMultiplication(Complex inwardsFactor)
	{
		super();
		this.inwardsFactor = inwardsFactor;
	}

	/**
	 * The factor by which the complex number representing the light-ray direction
	 * gets multiplied on entering the object (i.e. passing through the surface inwards).
	 * 
	 * This factor is the ratio between the inside and outside refractive indices.
	 * Here it can only be real, in which case the multiplication corresponds simply
	 * to Snell's law.  (For complex factors see the other constructor of the same name.)
	 * 
	 * @see ComplexMetarefractionMultiplication
	 * 
	 * @param inwardsFactor the ratio of refractive indices on inside and outside
	 */
	public ComplexMetarefractionMultiplication(double inwardsFactor)
	{
		super();
		this.inwardsFactor = new Complex(inwardsFactor);
	}
	
	@Override
	public Complex complexRefractOutwards(Complex incidentRayDirection)
	{
		// System.out.println(incidentRayDirection + " / " + inwardsFactor + " = " + Complex.division(incidentRayDirection, inwardsFactor));
		return Complex.division(incidentRayDirection, inwardsFactor);
	}

	@Override
	public Complex complexRefractInwards(Complex incidentRayDirection)
	{
		// System.out.println(incidentRayDirection + " * " + inwardsFactor + " = " + Complex.product(incidentRayDirection, inwardsFactor));
		return Complex.product(incidentRayDirection, inwardsFactor);
	}

	public Complex getInwardsFactor() {
		return inwardsFactor;
	}

	public void setInwardsFactor(Complex inwardsFactor) {
		this.inwardsFactor = inwardsFactor;
	}
}
