package optics.raytrace.surfaces;

import math.Vector3D;
import math.ODE.IntegrationType;
import optics.raytrace.core.SceneObject;

public class SurfaceOfLissajousLens extends SurfaceOfRadiallySymmetricRefractiveIndexDistribution {

	private static final long serialVersionUID = -2461467106472439411L;

	protected double alpha;
	
	protected double beta;
	
	public SurfaceOfLissajousLens(
			Vector3D centre, 
			double alpha,
			double beta,
			SceneObject surface,
			double deltaTau,
			double deltaXMax,
			int maxSteps,
			IntegrationType integrationType,
			double transmissionCoefficient,
			boolean shadowThrowing)
	{
		super(centre, surface, deltaTau, deltaXMax, maxSteps, integrationType, transmissionCoefficient, shadowThrowing);

		this.alpha = alpha;
		this.beta = beta;
	}

	public SurfaceOfLissajousLens(SurfaceOfLissajousLens original) {
		this(
				original.getCentre(),
				original.getAlpha(),
				original.getBeta(),
				original.getSurface(),
				original.getDeltaTau(),
				original.getDeltaXMax(),
				original.getMaxSteps(),
				original.getIntegrationType(),
				original.getTransmissionCoefficient(),
				original.isShadowThrowing()
			);
	}

	// setters & getters
	
	public double getAlpha() {
		return alpha;
	}

	public void setAlpha(double alpha) {
		this.alpha = alpha;
	}

	public double getBeta() {
		return beta;
	}

	public void setBeta(double beta) {
		this.beta = beta;
	}

	
	/**
	 * Calculate the refractive index as a function of distance from the centre;
	 * override to make this interesting
	 * @param r
	 * @return
	 */
	@Override
	public double calculateN(double r)
	{		
		double rOverBeta = r/beta;
		if(2 < Math.pow(rOverBeta, 2)) {
		//System.err.println("WARNING: Negative refractive index detected! at radius:"+r);
		return -Math.sqrt(-2 + rOverBeta*rOverBeta);
	}

		return Math.sqrt(2-rOverBeta*rOverBeta);
	}

	
	/**
	 * Calculate dn/dr as a function of distance from the centre;
	 * override to make this interesting
	 * @param r
	 * @return
	 */
	@Override
	public double calculatedNdr(double r)
	{
		return -Math.sqrt(2)*r/(calculateN(r)*beta*beta);
	}

	
}
