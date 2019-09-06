package optics.raytrace.surfaces.metarefraction;

import math.*;


/**
 * @author George Constable, Johannes Courtial
 *
 * JulieSetTransform 
 * 
 */
public class JuliaSetTransform extends ComplexMetarefraction
{
	// Introduce Variables
	
	private int iterations;
	private Complex constant;
	private double normalisation;
	
	/**
	 * @param constant
	 * @param iterations
	 * @param normalisation
	 */
	public JuliaSetTransform(Complex constant, int iterations, double normalisation)
	{
		this.constant = constant;
		this.iterations = iterations;
		this.normalisation = normalisation;
	}
	
//	/* (non-Javadoc)
//	 * @see optics.raytrace.surfaces.metarefraction.Metarefraction#clone()
//	 */
//	public JuliaSetTransform clone()
//	{
//		return new JuliaSetTransform(constant.clone(), iterations, normalisation);
//	}
	
	/* (non-Javadoc)
	 * @see optics.raytrace.surfaces.metarefraction.ComplexMetarefraction#complexRefractInwards(math.Complex)
	 */
	@Override
	public Complex complexRefractInwards(Complex incidentRayDirection)
	{
		// initialise first argument of Julia set as complexIncidentRay
		Complex c = Complex.product(normalisation, incidentRayDirection) ;
	
		// do the Julia iteration
		for(int n=0; n<iterations; n++)
		{			
			c = Complex.sum(Complex.square(c), constant);		
		}
		
		// do a slightly dodgy thing which can be interpreted as 
		// transmission through a fairly extreme refractive-index ratio
		c = Complex.product(1/normalisation, c);
		
		return c;
	}

	@Override
	public Complex complexRefractOutwards(Complex incidentRayDirection)
	{
		System.out.println("JuliaSetTransform.complexRefractOutwards: inwards refraction can't be reversed!");
		return new Complex(0, 0);
	}
}

	