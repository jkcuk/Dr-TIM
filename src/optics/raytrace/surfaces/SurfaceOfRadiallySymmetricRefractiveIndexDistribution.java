package optics.raytrace.surfaces;

import Jama.Matrix;
import math.Vector3D;
import math.ODE.IntegrationType;
import optics.raytrace.core.SceneObject;

public class SurfaceOfRadiallySymmetricRefractiveIndexDistribution extends SurfaceOfMetricSpace {

	private static final long serialVersionUID = -7727943652568599423L;
	
	/**
	 * centre of the radially symmetric refractive-index distribution
	 */
	protected Vector3D centre;
	
	public SurfaceOfRadiallySymmetricRefractiveIndexDistribution(
			Vector3D centre,
			SceneObject surface,
			double deltaTau,
			double deltaXMax,
			int maxSteps,
			IntegrationType integrationType,
			double transmissionCoefficient,
			boolean shadowThrowing
		) 
	{
		super(surface, deltaTau, deltaXMax, maxSteps, integrationType, transmissionCoefficient, shadowThrowing);
		
		this.centre = centre;
	}

	public SurfaceOfRadiallySymmetricRefractiveIndexDistribution(SurfaceOfRadiallySymmetricRefractiveIndexDistribution original) {
		this(
				original.getCentre(),
				original.getSurface(),
				original.getDeltaTau(),
				original.getDeltaXMax(),
				original.getMaxSteps(),
				original.getIntegrationType(),
				original.getTransmissionCoefficient(),
				original.isShadowThrowing()
			);
	}

	@Override
	public SurfaceOfMetricSpace clone() {
		return new SurfaceOfRadiallySymmetricRefractiveIndexDistribution(this);
	}
	
	// getters & setters
	
	public Vector3D getCentre() {
		return centre;
	}

	public void setCentre(Vector3D centre) {
		this.centre = centre;
	}


	
	/**
	 * Calculate the refractive index as a function of distance from the centre;
	 * override to make this interesting
	 * @param r
	 * @return
	 */
	public double calculateN(double r)
	{
		return 1;
	}
	
	/**
	 * Calculate dn/dr as a function of distance from the centre;
	 * override to make this interesting
	 * @param r
	 * @return
	 */
	public double calculatedNdr(double r)
	{
		return 1;
	}
	

//	@Override
//	public Matrix calculateEpsilonMuTensor(Vector3D x)
//	{
//		double n = calculateN(Vector3D.getDistance(x, centre));
//		
//		// the elements of the metric tensor
//		double[][] vals = {{n, 0, 0},{0, n, 0},{0, 0, n}};
//		
//		return new Matrix(vals);
//	}

	@Override
	public Vector3D dXdTau(Vector3D x, Vector3D k)
	{
		double n = calculateN(Vector3D.getDistance(x, centre));
		
		return k.getProductWith(2*n);
	}

	@Override
	public Vector3D dKdTau(Vector3D x, Vector3D k) {
		double r = Vector3D.getDistance(x, centre);
		double n = calculateN(r);
		double dndr = calculatedNdr(r);
		double k2 = k.getModSquared();
		
		// return x.getProductWith(2*n*n*dndr/r - n*(1-n*n)*dndr/(r*n));
		return x.getProductWith((-k2+3*n*n)*dndr/r);
	}

	@Override
	public Vector3D calculateK(Vector3D x, Vector3D d) {
		// calculate the direction of kv (vector k) from Hamilton's equation d xv / d \tau = \partial H / \partial kv (where xv is vector x)
		// and its length from the requirement that H(kv) = 0;
		
		// the LHS of the first equation is proportional to the light-ray direction, dv;
		// the RHS has already been calculated in the method dXdTau(Vector3D x, Vector3D k), where it was found to be 2 kv n;
		// this means that the direction of kv is that of xv
		
		// the equation H(kv) = 0 simplifies to k^2 = n^2, or simply k = (+/-) n
		
		double n = calculateN(Vector3D.getDistance(x, centre));
		
		return d.getWithLength(n);
	}

	@Override
	public Matrix calculateEpsilonMuTensor(Vector3D x) {
		// TODO Auto-generated method stub
		return null;
	}

}
