package optics.raytrace.surfaces;

import Jama.Matrix;
import math.Vector3D;
import math.ODE.IntegrationType;
import optics.DoubleColour;
import optics.raytrace.core.SceneObject;
import optics.raytrace.sceneObjects.Sphere;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;

/**
 * Calculations in J's lab book dated 8/5/19 (and later dates)
 * and in Mathematica notebook HamiltonianCalculationForRaytracing.nb
 * 
 * @author johannes
 */
public class SurfaceOfChenBelinMetricSpace extends SurfaceOfMetricSpace
{
	private static final long serialVersionUID = 7986877955966668742L;
	
	/**
	 * position of the black hole's centre
	 */
	protected Vector3D centre;
	
	/**
	 * the horizon radius of the black hole
	 */
	protected double horizonRadius;
	
	/**
	 * j = 1 corresponds to Kenyon's refractive-index profile;
	 * j = 3/5 corresponds to Jakub's refractive-index profile, in which the photon-sphere radius is 3R
	 */
	protected double jParameter;
		
	
	// constructors etc.

	/**
	 * Create a surface property that, when applied to the simulationSphere, makes it behave like the space around a black hole
	 * with Kenyon's (Jakub's) refractive-index distribution; the centre of the black hole is at the centre of the simulationSphere
	 * @param horizonRadius
	 * @param simulationSphere
	 * @param jParameter
	 * @param deltaTau
	 * @param maxSteps
	 * @param transmissionCoefficient	the transmission coefficient of the surface
	 */
	public SurfaceOfChenBelinMetricSpace(
			double horizonRadius,
			Sphere simulationSphere,
			double jParameter,
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
		setHorizonRadius(horizonRadius);
		setjParameter(jParameter);
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
	public SurfaceOfChenBelinMetricSpace(
			Vector3D centre,
			double horizonRadius,
			double jParameter,
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
		setHorizonRadius(horizonRadius);
		setjParameter(jParameter);
	}
	
	/**
	 * Clone the original transformation-optics-element surface
	 * @param original
	 */
	public SurfaceOfChenBelinMetricSpace(SurfaceOfChenBelinMetricSpace original)
	{
		this(
				original.getCentre(),
				original.getHorizonRadius(),
				original.getjParameter(),
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
	public SurfaceOfChenBelinMetricSpace clone()
	{
		return new SurfaceOfChenBelinMetricSpace(this);
	}
	
	
	
	// setters & getters
	
	public Vector3D getCentre() {
		return centre;
	}

	public void setCentre(Vector3D centre) {
		this.centre = centre;
		insideScene = null;
	}

	public double getHorizonRadius() {
		return horizonRadius;
	}

	public void setHorizonRadius(double horizonRadius) {
		this.horizonRadius = horizonRadius;
		insideScene = null;
	}

	public double getjParameter() {
		return jParameter;
	}

	public void setjParameter(double jParameter) {
		this.jParameter = jParameter;
	}

	
	
	// Kenyon's refractive-index distribution
	
	/**
	 * @param r
	 * @return	Kenyon's refractive index at distance r from the centre of the black hole
	 */
	public double calculateN(double r)
	{
		return Math.pow(jParameter*horizonRadius + r, 3) / (r*r*(r-horizonRadius));
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
		//   = - n R (r + 3 j r - 2 j R)/(r^2 (r - R) (r + j R)) xv
		double r = Vector3D.getDistance(x, centre);
		double k2 = k.getModSquared();
		double n = calculateN(r);

		double dndx = - n*horizonRadius*(r + 3*jParameter*r - 2*jParameter*horizonRadius) / (r*r*(r - horizonRadius)*(r + jParameter*horizonRadius));
		
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

	private SceneObjectContainer insideScene;
	
	/**
	 * Create a new inside scene, which contains only a black sphere representing the event horizon
	 * @return	the inside scene
	 */
	private SceneObjectContainer createInsideScene()
	{
		insideScene = new SceneObjectContainer("Scene inside surface", surface, surface.getStudio());
		
		insideScene.addSceneObject(
			new Sphere(
			"Black sphere corresponding to event horizon",	// description
			centre,	// centre
			horizonRadius,	// radius
			SurfaceColourLightSourceIndependent.BLACK,	// surfaceProperty
			insideScene,	// parent
			insideScene.getStudio()	// studio
		));
		
		return insideScene;
	}
	
	
	@Override
	public SceneObject getInsideScene()
	{
		// has the inside scene been created?
		if(insideScene == null)
		{
			// no -- create it!
			createInsideScene();
		}
		
		return insideScene;
	}

//	/* (non-Javadoc)
//	 * @see optics.raytrace.surfaces.SurfaceOfMetricSpace#getColourWhenEndConditionMet()
//	 */
//	@Override
//	public DoubleColour getColourWhenEndConditionMet()
//	{
//		return DoubleColour.BLACK;
//	}

//	/* (non-Javadoc)
//	 * @see optics.raytrace.surfaces.SurfaceOfMetricSpace#endConditionMet(optics.raytrace.core.Ray)
//	 */
//	public boolean endConditionMet(Ray ray)
//	{
//		if(super.endConditionMet(ray)) return true;
//		
//		double r = Vector3D.getDistance(ray.getP(), centre);
//
//		return r <= horizonRadius;
//	}

}
