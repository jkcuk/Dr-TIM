package optics.raytrace.surfaces;


import math.Complex;
import optics.raytrace.surfaces.Metarefractive;
import optics.raytrace.surfaces.metarefraction.ComplexMetarefractionMultiplication;


/**
 * @author Johannes Courtial
 */
public class RefractiveComplex extends Metarefractive
{
	private static final long serialVersionUID = 1928125391610983913L;

	/**
	 * Constructor that takes a complex refractive-index ratio
	 * @param insideOutsideRefractiveIndexRatio
	 * @param transmissionCoefficient
	 */
	public RefractiveComplex(Complex insideOutsideRefractiveIndexRatio, double transmissionCoefficient, boolean shadowThrowing)
	{
		// have to take inverse of ratio between inside refractive index and outside
		// refractive index to calculate factor by which complex numbers need to be multiplied
		// on passing the metarefractive surface from outside to inside
		super(
			new ComplexMetarefractionMultiplication(Complex.division(1., insideOutsideRefractiveIndexRatio)),
			transmissionCoefficient,
			shadowThrowing
		);
	}
		
	public Complex getInsideOutsideRefractiveIndexRatio()
	{
		return Complex.division(1., ((ComplexMetarefractionMultiplication)getMetarefraction()).getInwardsFactor());
	}

	public void setInsideOutsideRefractiveIndexRatio(Complex insideOutsideRefractiveIndexRatio)
	{
		// have to take inverse of ratio between inside refractive index and outside
		// refractive index to calculate factor by which complex numbers need to be multiplied
		// on passing the metarefractive surface from outside to inside
		((ComplexMetarefractionMultiplication)getMetarefraction()).setInwardsFactor(Complex.division(1., insideOutsideRefractiveIndexRatio));
	}
}
