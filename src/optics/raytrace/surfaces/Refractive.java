package optics.raytrace.surfaces;

import optics.raytrace.surfaces.Metarefractive;
import optics.raytrace.surfaces.metarefraction.ComplexMetarefractionMultiplicationReal;


/**
 * A refractive surface.
 * The scene object needs to be parametrised for this to work.
 * To refract the surface of scene objects that are not parametrised, use RefractiveSimple.
 * 
 * @author Johannes Courtial
 */
public class Refractive extends Metarefractive
{
	private static final long serialVersionUID = -7296983197906169469L;

	/**
	 * Constructor that takes a real refractive-index ratio between inside and outside
	 * @param insideOutsideRefractiveIndexRatio n_inside / n_outside
	 * @param transmissionCoefficient
	 */
	public Refractive(double insideOutsideRefractiveIndexRatio, double transmissionCoefficient, boolean shadowThrowing)
	{
		super(
			new ComplexMetarefractionMultiplicationReal(1./insideOutsideRefractiveIndexRatio),
			transmissionCoefficient,
			shadowThrowing
		);
	}
		
	public double getInsideOutsideRefractiveIndexRatio()
	{
		return 1./((ComplexMetarefractionMultiplicationReal)getMetarefraction()).getInwardsFactor();
	}

	public void setInsideOutsideRefractiveIndexRatio(double insideOutsideRefractiveIndexRatio)
	{
		// have to take inverse of ratio between inside refractive index and outside
		// refractive index to calculate factor by which complex numbers need to be multiplied
		// on passing the metarefractive surface from outside to inside
		((ComplexMetarefractionMultiplicationReal)getMetarefraction()).setInwardsFactor(1./insideOutsideRefractiveIndexRatio);
	}
}
