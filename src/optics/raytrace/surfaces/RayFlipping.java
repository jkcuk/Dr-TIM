package optics.raytrace.surfaces;

import optics.raytrace.surfaces.metarefraction.ComplexMetarefractionGeneralizedCC;

/**
 * @author Johannes Courtial
 */
public class RayFlipping extends Metarefractive
{	
	private static final long serialVersionUID = -3521786072520607867L;

	public RayFlipping(double transmissionCoefficient, boolean shadowThrowing)
	{
		super(
			new ComplexMetarefractionGeneralizedCC(0.),
			transmissionCoefficient,
			shadowThrowing
		);
	}

	public RayFlipping(double flipAxisAngle, double transmissionCoefficient, boolean shadowThrowing)
	{
		super(
			new ComplexMetarefractionGeneralizedCC(flipAxisAngle),
			transmissionCoefficient,
			shadowThrowing
		);
	}

	public double getFlipAxisAngle() {
		return ((ComplexMetarefractionGeneralizedCC)getMetarefraction()).getImaginaryAxisAngle();
	}

	public void setFlipAxisAngle(double flipAxisAngle) {
		((ComplexMetarefractionGeneralizedCC)getMetarefraction()).setImaginaryAxisAngle(flipAxisAngle);
	}
}