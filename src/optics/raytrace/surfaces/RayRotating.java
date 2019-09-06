package optics.raytrace.surfaces;


import math.Complex;
import optics.raytrace.surfaces.Metarefractive;
import optics.raytrace.surfaces.metarefraction.ComplexMetarefractionMultiplication;


/**
 * @author Johannes Courtial
 */
public class RayRotating extends Metarefractive
{
	private static final long serialVersionUID = 8293840437659961657L;

	/**
	 * @param rotationAngle
	 * @param transmissionCoefficient
	 */
	public RayRotating(
			double rotationAngle,
			double transmissionCoefficient,
			boolean shadowThrowing
	)
	{
		super(
			new ComplexMetarefractionMultiplication(Complex.fromPolar(1, rotationAngle)),
			transmissionCoefficient,
			shadowThrowing
		);
//		this.rotationAngle = rotationAngle;
	}

	public double getRotationAngle()
	{
		return ((ComplexMetarefractionMultiplication)getMetarefraction()).getInwardsFactor().getArg();
	}

	public void setRotationAngle(double rotationAngle)
	{
		((ComplexMetarefractionMultiplication)getMetarefraction()).setInwardsFactor(Complex.fromPolar(1, rotationAngle));
	}
}
