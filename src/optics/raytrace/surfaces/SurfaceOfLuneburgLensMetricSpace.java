package optics.raytrace.surfaces;

import Jama.Matrix;
import math.Vector3D;
import math.ODE.IntegrationType;
import optics.DoubleColour;
import optics.raytrace.core.SceneObject;
import optics.raytrace.sceneObjects.Sphere;

/**
 * Calculations in Mathematica notebook HamiltonianCalculationForRaytracing.nb
 * 
 * @author johannes
 */
public class SurfaceOfLuneburgLensMetricSpace extends SurfaceOfMetricSpace
{
	private static final long serialVersionUID = -1764109264229098236L;

	/**
	 * centre of the Luneburg lens
	 */
	private Vector3D centre;
	
	/**
	 * the radius of the Luneburg lens
	 */
	private double radius;
	
		
	
	// constructors etc.

	/**
	 * Create a surface property that, when applied to the simulationSphere, makes it behave like a Luneburg lens
	 * @param simulationSphere
	 * @param deltaTau
	 * @param maxSteps
	 * @param transmissionCoefficient	the transmission coefficient of the surface
	 */
	public SurfaceOfLuneburgLensMetricSpace(
			Sphere simulationSphere,
			double deltaTau,
			double deltaXMax,
			int maxSteps,
			IntegrationType integrationType,
			double transmissionCoefficient
		)
	{
		super(
				simulationSphere,	// surface
				deltaTau,
				deltaXMax,
				maxSteps,
				integrationType,
				transmissionCoefficient,
				false	// shadowThrowing
			);
		
		setCentre(simulationSphere.getCentre());
		setRadius(simulationSphere.getRadius());
	}

	/**
	 * Create a surface property that, when applied to the surface, makes it behave like the space around a black hole
	 * with Kenyon's (Jakub's) refractive-index distribution
	 * @param centre
	 * @param horizonRadius
	 * @param jParameter
	 * @param surface
	 * @param deltaTau
	 * @param maxSteps
	 * @param transmissionCoefficient
	 * @param shadowThrowing
	 */
	public SurfaceOfLuneburgLensMetricSpace(
			Vector3D centre,
			double radius,
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
		
		setCentre(centre);
		setRadius(radius);
	}
	
	/**
	 * Clone the original transformation-optics-element surface
	 * @param original
	 */
	public SurfaceOfLuneburgLensMetricSpace(SurfaceOfLuneburgLensMetricSpace original)
	{
		this(
				original.getCentre(),
				original.getRadius(),
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
	public SurfaceOfLuneburgLensMetricSpace clone()
	{
		return new SurfaceOfLuneburgLensMetricSpace(this);
	}
	
	
	
	// setters & getters
	
	public Vector3D getCentre() {
		return centre;
	}

	public void setCentre(Vector3D centre) {
		this.centre = centre;
	}

	public double getRadius() {
		return radius;
	}

	public void setRadius(double radius) {
		this.radius = radius;
	}

	
	
	// Kenyon's refractive-index distribution
	
	/**
	 * @param r
	 * @return	refractive index at distance r from the centre of the Luneburg lens
	 */
	public double calculateN(double r)
	{
		double rOverR = r/radius;
		return Math.sqrt(2 - rOverR*rOverR);
	}
	
	
	
	// SurfaceOfMetricSpace methods

	/* (non-Javadoc)
	 * @see optics.raytrace.surfaces.SurfaceOfMetricSpace#getRefractiveIndexTensor(math.Vector3D)
	 */
	public Matrix calculateEpsilonMuTensor(Vector3D x)
	{
		double n = calculateN(Vector3D.getDistance(x, centre));
		
		// the elements of the metric tensor
		double[][] vals = {{n, 0, 0},{0, n, 0},{0, 0, n}};
		
		return new Matrix(vals);
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.surfaces.SurfaceOfMetricSpace#dXdTau(math.Vector3D, math.Vector3D)
	 */
	@Override
	public Vector3D dXdTau(Vector3D x, Vector3D k)
	{
		// calculate from Hamilton's equation d xv / d \tau = \partial H / \partial kv (where xv and kv are vectors);
		// in our case, H = (k_x^2 + k_y^2 + k_z^2) n(x, y, z) - n(x, y, z)^3,
		// so
		// \partial H / \partial kv
		//   = (\partial H / \partial k_x, \partial H / \partial k_y, \partial H / \partial k_z)
		//   = (2 k_x n, 2 k_y n, 2 k_z n) = 2 kv n
		double n = calculateN(Vector3D.getDistance(x, centre));
		
		return k.getProductWith(2.*n);
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.surfaces.SurfaceOfMetricSpace#dKdTau(math.Vector3D, math.Vector3D)
	 */
	@Override
	public Vector3D dKdTau(Vector3D x, Vector3D k)
	{
		// calculate from Hamilton's equation d kv / d \tau = - \partial H / \partial xv (where kv and xv are vectors);
		// in our case, H = k^2 n(x, y, z) - n(x, y, z)^3,
		// so
		// - \partial H / \partial xv
		//   = - k^2 (\partial n / \partial xv) + 3 n^2 (\partial n / \partial xv)
		//   = (3 n^2 - k^2) (\partial n / \partial xv),
		// where
		// \partial n / \partial xv
		//   = - xv / (R^2 n)
		double r = Vector3D.getDistance(x, centre);
		double k2 = k.getModSquared();
		double n = calculateN(r);

		double dndx = - 1/(radius*radius*n);
		
		return Vector3D.difference(x, centre).getProductWith((3.*n*n - k2) * dndx);
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.surfaces.SurfaceOfMetricSpace#calculateK(math.Vector3D, math.Vector3D)
	 */
	@Override
	public Vector3D calculateK(Vector3D x, Vector3D d)
	{
		// calculate the direction of kv (vector k) from Hamilton's equation d xv / d \tau = \partial H / \partial kv (where xv is vector x)
		// and its length from the requirement that H(kv) = 0;
		
		// the LHS of the first equation is proportional to the light-ray direction, dv;
		// the RHS has already been calculated in the method dXdTau(Vector3D x, Vector3D k), where it was found to be 2 kv n;
		// this means that the direction of kv is that of xv
		
		// the equation H(kv) = 0 simplifies to k^2 = n^2, or simply k = (+/-) n
		
		double n = calculateN(Vector3D.getDistance(x, centre));
		
		return d.getWithLength(n);
	}
	
	/* (non-Javadoc)
	 * @see optics.raytrace.surfaces.SurfaceOfMetricSpace#getColourWhenIterationLimitReached()
	 */
	@Override
	public DoubleColour getColourWhenIterationLimitReached()
	{
		return DoubleColour.BLACK;
	}
}
