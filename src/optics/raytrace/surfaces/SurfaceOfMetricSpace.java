package optics.raytrace.surfaces;

import Jama.Matrix;
import math.LorentzTransformation;
import math.MyMath;
import math.Vector3D;
import math.ODE.Derivatives;
import math.ODE.Euler;
import math.ODE.IntegrationType;
import math.ODE.RungeKutta;
import optics.DoubleColour;
import optics.raytrace.core.LightSource;
import optics.raytrace.core.Orientation;
import optics.raytrace.core.Ray;
import optics.raytrace.core.RayWithTrajectory;
import optics.raytrace.core.RaySceneObjectIntersection;
import optics.raytrace.core.RaytraceExceptionHandler;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.SurfacePropertyPrimitive;
import optics.raytrace.exceptions.EvanescentException;
import optics.raytrace.exceptions.RayTraceException;

/**
 * SurfaceProperty that marks the surface with this property as the boundary surface defining a volume ---
 * the inside of the surface --- to be a volume of space with an associated optical metric.
 * 
 * Ray tracing inside this space is by numerical integration of Hamilton's equations (Eqns (41) in [1]),
 * 
 * d x_i / d \tau =  \partial H / \partial k_i,
 * d k_i / d \tau = -\partial H / \partial x_i,
 * 
 * where i=1, 2, 3, k = (k_1, k_2, k_3) is the wave vector at position x = (x_1, x_2, x_3), and the Hamiltonian is (Eqn (40) in [1] with f(x) = 1)
 * 
 * H = (k.n.k - det(n)),
 * 
 * where n is the refractive-index tensor.
 * 
 * This is an abstract class; any non-abstract subclass needs to provide methods that evaluate the partial derivatives on the RHS of the 6 Hamilton's equations above.
 * 
 * References:
 * [1] D. Schurig, J. B. Pendry and D. R. Smith, "Calculation of material properties and ray tracing in transformation media",
 * Opt. Express 14, 9794-9804 (2006)
 * [2] W. H. Press, S. A. Teukolsky, W. T. Vetterling and B. P. Flannery, "Numerical Recipes in C", chapter 16 (Cambridge University Press, 1992)
 * 
 * @author Johannes Courtial
 */
public abstract class SurfaceOfMetricSpace extends SurfacePropertyPrimitive
implements Derivatives
{
	private static final long serialVersionUID = -6338206885245090995L;

	
	/**
	 * the surface (i.e. boundary) of the metric volume;
	 * this needs to be oriented correctly -- i.e. the surface normal needs to point *outwards* -- for this to work correctly!
	 */
	protected SceneObject surface;
	
	/**
	 * step size in tau, which parametrises x and k
	 */
	protected double deltaTau;
	
	/**
	 * max spatial step size, if >0 (otherwise ignored)
	 */
	protected double deltaXMax;

	/**
	 * the maximum number of steps to be taken inside the volume (before black is returned)
	 */
	protected int maxSteps;
	
	/**
	 * algorithm used for numerical integration
	 */
	protected IntegrationType integrationType;
	
	

	/**
	 * Creates a new surface property that marks a surface as the boundary surface defining a voxellated volume.
	 * @param planeSets	the sets of equidistant, parallel, planes that define the voxels
	 * @param transmissionCoefficient	transmission coefficient on entering and exiting volume
	 */
	public SurfaceOfMetricSpace(SceneObject surface, double deltaTau, double deltaXMax, int maxSteps, IntegrationType integrationType, double transmissionCoefficient, boolean shadowThrowing)
	{
		super(transmissionCoefficient, shadowThrowing);
		setSurface(surface);
		setDeltaTau(deltaTau);
		setDeltaXMax(deltaXMax);
		setMaxSteps(maxSteps);
		setIntegrationType(integrationType);
	}
	
	/**
	 * Clone the original transformation-optics-element surface
	 * @param original
	 */
	public SurfaceOfMetricSpace(SurfaceOfMetricSpace original)
	{
		this(
				original.getSurface(),
				original.getDeltaTau(),
				original.getDeltaXMax(),
				original.getMaxSteps(),
				original.getIntegrationType(),
				original.getTransmissionCoefficient(),
				original.isShadowThrowing()
			);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public abstract SurfaceOfMetricSpace clone();
	
	
	//
	// methods describing the metric inside the medium;
	// any non-abstract implementation must override these methods
	//
	
	/**
	 * @param x
	 * @return	the tensor n as defined in Eqn (39) in [1], which equals both the epsilon tensor and the mu tensor
	 */
	public abstract Matrix calculateEpsilonMuTensor(Vector3D x);
	
	/**
	 * @param x
	 * @return	the metric tensor at position x
	 */
	// public abstract Matrix getMetricTensor1(Vector3D x);

	// the following methods define the distribution of the metric inside the volume, by
	// providing the partial derivatives on the RHS of Hamilton's equations (Eqns (41) in [1]),
	// 
	// d x_i / d \tau =  \partial H / \partial k_i,
	// d k_i / d \tau = -\partial H / \partial x_i,
	// 
	// where i=1, 2, 3, k = (k_1, k_2, k_3) is the wave vector at position x = (x_1, x_2, x_3), and the Hamiltonian is (Eqn (40) in [1] with f(x) = 1)
	// 
	// H = (k.n.k - det(n)),
	// 
	// where n is the epsilon=mu tensor.
	// Mathematica can calculate these functions for any given metric tensor.
	
	/**
	 * @return	d x / d \tau = \partial H / \partial k
	 */
	public abstract Vector3D dXdTau(Vector3D x, Vector3D k);
	
	/**
	 * @return	d k / d \tau = - \partial H / \partial x
	 */
	public abstract Vector3D dKdTau(Vector3D x, Vector3D k);
	
	/**
	 * @param x
	 * @param d
	 * @return	the wave vector corresponding to a ray travelling with direction d at position x
	 */
	public abstract Vector3D calculateK(Vector3D x, Vector3D d);
	
	
	/**
	 * Call this to correct the length of k, in case this needs to be done.
	 * The correct length of k is such that  H(x, k) = 0.
	 * Override this method if there is a problem with the length of k
	 * @param x
	 * @param k
	 * @return	k, with correct length
	 */
	public Vector3D getKWithCorrectLength(Vector3D x, Vector3D k)
	{
		return k;
	}
	
	/**
	 * Override if there are additional scene objects inside the simulation surface.
	 * IMPORTANT:  The scene objects should have simple surfaces, of type SurfaceColourLightSourceIndependent, to avoid recursive raytracing inside the simulation surface.
	 * @return	the scene containing any scene objects located inside the simulation surface; null if there are none
	 */
	public SceneObject getInsideScene()
	{
		return null;
	}
	
	
	/**
	 * @param k
	 * @param epsilonMuTensor
	 * @return	the Hamiltonian
	 */
	public double calculateHamiltonian(Vector3D k, Matrix epsilonMuTensor)
	{
		Matrix kColumn = k.toJamaColumnVector();
		Matrix kRow = k.toJamaRowVector();

		// H = k.n.k - det(n), where n is the epsilon-mu tensor ([1], Eqn (40) with f(x) = 1)		
		return kRow.times(epsilonMuTensor.times(kColumn)).get(0, 0) - epsilonMuTensor.det();
	}
	
	
//	/**
//	 * @param ray	the ray
//	 * @return	true if ray meets end condition, which results in returning a black colour, or false if it doesn't
//	 */
//	public boolean endConditionMet(Ray ray)
//	{
//		// is there a scene inside the surface?
//		if(sceneInsideSurface != null)
//		{
//			// yes; is the new ray point inside any object within this scene?
//			return sceneInsideSurface.insideObject(ray.getP());
//		}
//		
//		return false;
//	}
	
	/**
	 * Override to change colour when the iteration limit is reached
	 */
	public DoubleColour getColourWhenIterationLimitReached()
	{
		return DoubleColour.BLACK;
	}
	
//	public DoubleColour getColourWhenEndConditionMet()
//	{
//		return DoubleColour.BLACK;
//	}

	
	
	//
	// internal workings
	//
	
	/* (non-Javadoc)
	 * @see math.ODE.Derivatives#calculateDerivatives(double, double[], double[])
	 */
	public void calculateDerivatives(double t, double f[], double dfdt[])
	{
		// the first three values of f[] are the position, ...
		Vector3D x = new Vector3D(f[0], f[1], f[2]);
		
		// ... the last three are the momentum
		Vector3D k = new Vector3D(f[3], f[4], f[5]);
		
		// calculate dx / dtau...
		Vector3D dxdtau = dXdTau(x, k);
		
		// ... and dk / dtau, ...
		Vector3D dkdtau = dKdTau(x, k);
		
		// ... and construct from these d(x,k) / dt
		dfdt[0] = dxdtau.x;
		dfdt[1] = dxdtau.y;
		dfdt[2] = dxdtau.z;
		dfdt[3] = dkdtau.x;
		dfdt[4] = dkdtau.y;
		dfdt[5] = dkdtau.z;
	}

	//
	// SurfaceProperty methods
	//
	
	/* (non-Javadoc)
	 * @see optics.raytrace.core.SurfaceProperty#getColour(optics.raytrace.core.Ray, optics.raytrace.core.RaySceneObjectIntersection, optics.raytrace.core.SceneObject, optics.raytrace.core.LightSource, int, optics.raytrace.core.RaytraceExceptionHandler)
	 */
	@Override
	public DoubleColour getColour(Ray r, RaySceneObjectIntersection i, SceneObject scene, LightSource l, int traceLevel, RaytraceExceptionHandler raytraceExceptionHandler)
	throws RayTraceException
	{
		// System.out.println("SurfaceOfMetricSpace::getColour("+r+", "+i+", ..., "+traceLevel+", ...)");
		
		if (traceLevel < 0) return DoubleColour.BLACK;
		
		// check if the ray was starting within the volume
		if(surface.insideObject(r.getP()))
		{
			// the ray started inside the volume
			if(r.isRayWithTrajectory())
			{
				// an intersection point was added to the ray when it intersected the surface, but the ray does not actually intersect the surface there,
				// so remove the intersection point
				((RayWithTrajectory)r).removeLastIntersectionPoint();
			}
			
			// calculate the k vector at the ray's start position and the ray's wave vector to this value
			r.setK(calculateK(r.getP(), r.getD()));
			
			// the ray must have originated inside the volume, so trace it from here
			return getColourUponStartingWithinVolume(r, scene, l, maxSteps, traceLevel, raytraceExceptionHandler);
		}
		else
		{
			// the ray started outside of the volume

			// create a new ray, starting from the intersection point with the surface and with the right k vector...
			Ray rI = getRefractedRay(r, i, Orientation.INWARDS);
			
			// try-catch moved to CameraClass::takePhoto
//			try
//			{
				// ... and trace it through the volume; multiply by the transmission coefficient as the ray has passed through the surface once when it entered the volume
				return getColourUponStartingWithinVolume(rI, scene, l, maxSteps, traceLevel, raytraceExceptionHandler).multiply(getTransmissionCoefficient());
//			}
//			catch(StackOverflowError e)
//			{
//				e.printStackTrace();
//				return DoubleColour.YELLOW;
//			}
		}		
	}
	
	/**
	 * Inner class for passing around a k vector and the direction vector simultaneously
	 * @author johannes
	 */
	private class KAndD
	{
		public Vector3D k, d;
		
		public KAndD(Vector3D k, Vector3D d)
		{
			this.k = k;
			this.d = d;
		}
	}
	
	/**
	 * Given the part of k perpendicular to n, calculate the full k vector and the corresponding light-ray direction d
	 * such that the ray's direction d satisfies the condition d.n < 0, i.e. points inwards
	 * @param kP	part of k perpendicular to the vector n
	 * @param n		vector n
	 * @param position	position
	 * @throws RayTraceException 
	 */
	public KAndD calculateKAndDFromKP(Vector3D kP, Vector3D n, Vector3D position)
	throws EvanescentException
	{
		// System.out.println("SurfaceOfMetricSpace::calculateKAndDFromKP("+kP+", "+n+", "+position+")");

		// calculate the length of the normal component of k, kN
		Matrix nTensor = calculateEpsilonMuTensor(position);
		Matrix nColumn = n.toJamaColumnVector();
		Matrix nRow = n.toJamaRowVector();
		Matrix kPColumn = kP.toJamaColumnVector();
		Matrix kPRow = kP.toJamaRowVector();

		double a = nRow.times(nTensor.times(nColumn)).get(0, 0);
		double b = 2.*nRow.times(nTensor.times(kPColumn)).get(0, 0);
		double c = kPRow.times(nTensor.times(kPColumn)).get(0, 0) - nTensor.det();

		// check the discriminant
		double d = b*b-4.*a*c;
		if(d < 0)
		{
			System.out.println("SurfaceOfMetricSpace::calculateKAndDFromKP: Discriminant < 0; ray evanescent (probably?)");
			// discriminant < 0; I think this means that the wave is evanescent
			throw new EvanescentException("Discriminant < 0; ray evanescent (probably?)");
		}
		double sd = Math.sqrt(d);
		double kN1 = (-b+sd)/(2*a);
		double kN2 = (-b-sd)/(2*a);

		// the corresponding k vectors
		Vector3D k1 = Vector3D.sum(kP, n.getProductWith(kN1));
		Vector3D k2 = Vector3D.sum(kP, n.getProductWith(kN2));

		// calculate the corresponding light-ray directions
		Vector3D d1 = dXdTau(
				position,	// position
				k1	// momentum
				);
		Vector3D d2 = dXdTau(
				position,	// position
				k2	// momentum
				);

		// calculate dI1.n < 0 and dI2.n < 0
		boolean b1 = Vector3D.scalarProduct(d1, n) < 0.0;
		boolean b2 = Vector3D.scalarProduct(d2, n) < 0.0;

		// finally, we can calculate the wave vector and light-ray direction in the medium
		KAndD kAndD;
		if(b1 && !b2) kAndD = new KAndD(k1, d1);
		else if(!b1 && b2) kAndD = new KAndD(k2, d2);
		else
		{
			// either both of the light-ray directions point into the correct half-space, or none
			(new RayTraceException("Either both of the calculated light-ray directions point into the correct half-space, or none")).printStackTrace();
			// panic!
			System.exit(-1);			
			kAndD = null;
		}
		
		// System.out.println("SurfaceOfMetricSpace::calculateKAndDFromKP: H = "+calculateHamiltonian(kAndD.k, nTensor)+" (should be 0)");
		
		return kAndD;
	}

	
	/**
	 * For the given incident ray, which is incident on the surface with the given orientation, calculate the corresponding refracted ray
	 * @param incidentRay
	 * @param intersectionWithSurface
	 * @param orientation
	 * @return	the refracted ray
	 * @throws EvanescentException 
	 */
	public Ray getRefractedRay(Ray incidentRay, RaySceneObjectIntersection intersectionWithSurface, Orientation orientation)
	throws EvanescentException, RayTraceException
	{
		// System.out.println("SurfaceOfMetricSpace::getRefractedRay("+incidentRay+", "+intersectionWithSurface+", "+orientation+")");
		
		// soon we need the normalised outwards-facing surface normal
		Vector3D n = intersectionWithSurface.o.getNormalisedOutwardsSurfaceNormal(intersectionWithSurface.p);

		// the k vector and light-ray direction in the new medium, which need to be calculated now
		Vector3D kNew, dNew;
		
		Vector3D k, kT;

		switch(orientation)
		{
		case INWARDS:
			// refract from the outside into the medium just inside the volume, according to the following rules:
			// (1) the transverse component of the wave vector is conserved across the boundary ([1], just before Eqn (42));
			// (2) the longitudinal component of the new wave vector, k, is given by the equation H(k) = 0, ...
			// (3) ... and the condition that \partial H / \partial k . n > 0, where n is the normal pointing *into* the volume

			// outside of the medium, the metric is assumed to be Euclidean (so the medium outside is air), so k points in the direction of d
			// and its length is given by the dispersion relation ([1], Eqn (43) written for g=diag(1, 1, 1))
			//   H = k^2 - 1 = 0,
			// which means that k is of length 1, which in turn means that k simply equals the normalised light-ray direction:
			k = incidentRay.getD();

			// the tangential component of the wave vector inside equals that on the outside ([1], Eqn (42))
			kT = k.getPartPerpendicularTo(n);

			KAndD kAndD = calculateKAndDFromKP(kT, n, intersectionWithSurface.p);
			kNew = kAndD.k;
			dNew = kAndD.d;

			// System.out.println("SurfaceOfMetricSpace::getRefractedRay: Orientation = "+orientation+", kNew = "+kNew+", |kNew| = "+kNew.getLength());

			break;
		case OUTWARDS:
		default:
			// refract from the inside into the air outside according to the following rules:
			// (1) the transverse component of the wave vector is conserved across the boundary ([1], just before Eqn (42));
			// (2) the longitudinal component of the new wave vector, k, is given by the equation H(k) = k^2 - 1 = 0, ...
			// (3) ... and the condition that the light-ray direction point out of the volume
			
			// the k vector inside the medium should already be set
			k = incidentRay.getK();
			
			// the tangential component of the wave vector inside equals that on the outside ([1], Eqn (42))
			kT = k.getPartPerpendicularTo(n);
			double kT2 = kT.getModSquared();

			// System.out.println("SurfaceOfMetricSpace::getRefractedRay: Orientation = "+orientation+", k = "+k+", |k| = "+k.getLength()+", kT = "+kT+", |kT| = "+kT.getLength());
			
			// does TIR occur?
			if(kT2 > 1)
			{
				// the longitudinal component of k would be purely imaginary, so TIR occurs
				throw new EvanescentException("TIR upon leaving volume at position "+intersectionWithSurface.p+", kT2 = "+kT2+", |k| = "+k.getLength());
			}
			
			// no TIR; keep raytracing as normal
			kNew = Vector3D.sum(kT, n.getProductWith(Math.sqrt(1 - kT2)));
			
			dNew = kNew;
		}
				
		// create a new ray, starting from the intersection point with the surface and the new k vector, and return it
		return incidentRay.getBranchRay(intersectionWithSurface.p, kNew, dNew, intersectionWithSurface.t);
	}

	/**
	 * Inner class for passing around a k vector and a position vector simultaneously
	 * @author johannes
	 */
	private class KAndX
	{
		public Vector3D k, x;
		
		public KAndX(Vector3D k, Vector3D x)
		{
			this.k = k;
			this.x = x;
		}
	}

	/**
	 * Calculate deltaX and deltaK, i.e. the change in position and momentum during one integration step
	 * @param r
	 * @param deltaTau
	 * @return	a structure holding deltaK and deltaX
	 */
	private KAndX calculateDeltaKAndDeltaX(Ray r, double deltaTau)
	{
		// set everything up to use the methods in math.ODE

		// collect the three components of the position and the three components of momentum in one array
		double[] f = new double[6];

		// also create an array that will hold the change in position and momentum
		double[] dfdt = new double[6];

		// perform one iteration step
		f[0] = r.getP().x;
		f[1] = r.getP().y;
		f[2] = r.getP().z;
		f[3] = r.getK().x;
		f[4] = r.getK().y;
		f[5] = r.getK().z;

		switch(integrationType)
		{
		case EULER:
			// Euler method
			Euler.calculateDeltaF(
					0,	// t
					deltaTau,	// dt
					f,
					dfdt,
					this	// model
					);
			break;
		case RK4:
		default:
			// Runge-Kutta method
			RungeKutta.calculateDeltaF(
					0,	// t
					deltaTau,	// dt
					f,
					dfdt,
					this	// model
					);
		}

		// return the result
		return new KAndX(
				new Vector3D(dfdt[3], dfdt[4], dfdt[5]),	// dk
				new Vector3D(dfdt[0], dfdt[1], dfdt[2])	// dx
				);
	}
			
	/**
	 * Ray tracing inside the volume
	 * @param r	the light ray; note that the light ray's k vector must have been set
	 * @param scene
	 * @param l
	 * @param stepsLeft	the number of steps left to be taken inside the volume before black is returned
	 * @param traceLevel
	 * @param raytraceExceptionHandler
	 * @return
	 * @throws RayTraceException
	 */
	public DoubleColour getColourUponStartingWithinVolume(Ray r, SceneObject scene, LightSource l, int stepsLeft, int traceLevel, RaytraceExceptionHandler raytraceExceptionHandler)
	throws RayTraceException
	{
		// System.out.println("SurfaceOfMetricSpace::getColourUponStartingWithinVolume("+r+", ..., "+stepsLeft+", "+traceLevel+", ...)");

		if((stepsLeft < 0) || (traceLevel < 0)) return getColourWhenIterationLimitReached();
				
		if(r.getK() == null)
		{
			// the ray's k vector is not set, but it needs to be set.
			new RayTraceException("Ray's k vector not set").printStackTrace();
		}
		
		// trace through the metric
		for(; stepsLeft > 0; stepsLeft--)
		{
			// System.out.println("stepsLeft="+stepsLeft);
			// System.out.println("SurfaceOfMetricSpace:getColourUponStartingWithinVolume: inside steps loop; stepsLeft "+stepsLeft);

			KAndX deltaKDeltaX = calculateDeltaKAndDeltaX(r, deltaTau);
			Vector3D dX = deltaKDeltaX.x;
			Vector3D dK = deltaKDeltaX.k;
			
			// System.out.println("SurfaceOfMetricSpace:getColourUponStartingWithinVolume: initial iteration step taken");

			// are there any scene objects inside the simulation sphere?
			if(getInsideScene() != null)
			{
				// yes, there are scene objects inside the simulation volume
				
				// check if the light ray intersects with one within the max step length
				// first set the light-ray direction
				r.setD(dX);

				// calculate the nearest intersection of the ray with the inside scene
				RaySceneObjectIntersection i = getInsideScene().getClosestRayIntersection(r);
			
				// does the ray intersect the inside scene?
				if(i != RaySceneObjectIntersection.NO_INTERSECTION)
				{
					// yes, but within the max step length?
					if(Vector3D.getDistance(i.p, r.getP()) <= deltaXMax)
					{
						// yes!
						// System.out.println("SurfaceOfMetricSpace:getColourUponStartingWithinVolume: actual intersection with inside scene");
						
						return i.o.getColourAtIntersection(r, i, getInsideScene(), l, traceLevel-1, raytraceExceptionHandler);
					}
					
					// System.out.println("SurfaceOfMetricSpace:getColourUponStartingWithinVolume: intersection with inside scene, but not nearby");
				}
			}
			
			// System.out.println("SurfaceOfMetricSpace:getColourUponStartingWithinVolume: no intersection with inside scene");
			
			// is the step longer than the maximum step length?
			double deltaX = dX.getLength();
			if(deltaX > deltaXMax)
			{
				// yes; take a step of length 0.5*deltaXMax in the same direction
				deltaKDeltaX = calculateDeltaKAndDeltaX(r, deltaTau*0.5*deltaXMax / deltaX);
				dX = deltaKDeltaX.x;
				dK = deltaKDeltaX.k;
			}
			
			// System.out.println("SurfaceOfMetricSpace:getColourUponStartingWithinVolume: after steplength correction");
			
			// calculate the preliminary new position of the ray
			Vector3D newP = Vector3D.sum(r.getP(), dX);
			
			// does the new intersection point lie outside of the volume?
			if(!surface.insideObject(newP))
			{
				// the new intersection point lies outside of the volume;
				// calculate the point where the ray leaves the volume and take another, scaled, integration step

				// set the light-ray direction of the ray, ...
				r.setD(dX);

				// System.out.println("SurfaceOfMetricSpace::getColourUponStartingWithinVolume: r.p = "+r.getP()+", newP = "+newP);
				
				// ... calculate the nearest intersection of the ray with the surface, ...
				RaySceneObjectIntersection i = surface.getClosestRayIntersection(r);

				// System.out.println("SurfaceOfMetricSpace::getColourUponStartingWithinVolume: i.p = "+i.p);

				// ... deal with the case of there being no intersection, in case this is necessary, ...
				if(i == RaySceneObjectIntersection.NO_INTERSECTION)
				{
					// there is no intersection; panic!
					(new RayTraceException("No intersection between ray leaving the volume and the surface of the volume")).printStackTrace();
					return DoubleColour.YELLOW;
				}

				// ... leave the volume
				deltaX = Vector3D.getDistance(i.p, r.getP());
				if(deltaX > 0.0)
				{
					if(deltaX > deltaXMax) (new RayTraceException("deltaX = "+deltaX+" > deltaXMax = "+deltaXMax)).printStackTrace(); // TODO
					// calculate the value of factor by which dX has to be multiplied to stretch from r.getP to the surface
					double factor = deltaX / dX.getLength();

					// take a smaller step in the same direction
					deltaKDeltaX = calculateDeltaKAndDeltaX(r, deltaTau*factor);
					dX = deltaKDeltaX.x;
					dK = deltaKDeltaX.k;
				}

				// update the ray accordingly
				r.setP(i.p);
				r.setD(dX);
				r.setK(Vector3D.sum(r.getK(), dK));

				// System.out.println("SurfaceOfMetricSpace::getColourUponStartingWithinVolume: hitting surface of volume; H = "+calculateHamiltonian(r.getK(), calculateEpsilonMuTensor(i.p))+" (should be 0)");

				
				try
				{
					// refract the new ray and trace it through the rest of the scene
					return scene.getColourAvoidingOrigin(
							getRefractedRay(r, i, Orientation.OUTWARDS).advance(MyMath.TINY),	// TODO	is this advance the right thing to do?
							i.o,
							l,
							scene,
							traceLevel-1,
							raytraceExceptionHandler
						).multiply(getTransmissionCoefficient());	// as the ray is passing through the surface upon leaving the volume
				}
				catch(EvanescentException e)
				{
					// TIR has occured
					// System.out.println("SurfaceOfMetricSpace::getColourUponStartingWithinVolume: TIR!");
					
					// outwards-facing normal at intersection point
					Vector3D n = i.o.getNormalisedOutwardsSurfaceNormal(i.p);
					KAndD kAndD = calculateKAndDFromKP(
							r.getK().getPartPerpendicularTo(n),	// kP
							n,	// n
							i.p	// position
						);
					
					// set the new light-ray position, ...
					r.setP(i.p);
					
					// ...  time, ...
					dX = Vector3D.difference(i.p, r.getP());					
					r.setT(r.getT() - Vector3D.scalarProduct(r.getK(), dX));	// TODO is this correct?
					
					// ... and wave vector
					r.setK(kAndD.k);

					return getColourUponStartingWithinVolume(
							r,
							scene,
							l,
							stepsLeft - 1,
							traceLevel,
							raytraceExceptionHandler
						);

					// return DoubleColour.YELLOW;
				}
			}
			
			// the ray stays inside the volume; update it
			
			// set the new light-ray position, ...
			r.setP(newP);
			
			// ...  time, ...
			r.setT(r.getT() - Vector3D.scalarProduct(r.getK(), dX)/LorentzTransformation.c);	// TODO is this correct?
			
			// System.out.println("SurfaceOfMetricSpace::getColourUponStartingWithinVolume: r.getT() = "+r.getT());

			// ... and wave vector
			r.setK(Vector3D.sum(r.getK(), dK));
			
			// quick sanity check
			double hamiltonian = calculateHamiltonian(r.getK(), calculateEpsilonMuTensor(newP));
			if(Math.abs(hamiltonian) > 0.1) System.out.println("SurfaceOfMetricSpace::getColourUponStartingWithinVolume: Hamiltonian = "+hamiltonian+" != 0");
			
//			// set the ray's current light-ray direction (don't normalise, as we need this to be dx / dtau below)
//			Vector3D dXdTau = dXdTau(r.getP(), r.getK());
//
//			// calculate the squared distance to the next iteration point on the trajectory, provided it does not leave the volume
//			double e2 = tau*tau*dXdTau.getModSquared();	// (taking the square root takes a while, so avoid that)
//			
//			// also calculate the nearest intersection of the ray with the surface, ...
//			r.setD(dXdTau);	// set the light-ray direction
//			RaySceneObjectIntersection i = surface.getClosestRayIntersection(r);	// calculate the ray's closest intersection point with the surface
//			
//			// ... and if there is an intersection...
//			if(i != RaySceneObjectIntersection.NO_INTERSECTION)
//			{
//				// ... calculate its distance from the current point
//				double f2 = Vector3D.getDistance(i.p, r.getP());
//			
//				// which one is closest?
//				if(f2*f2 < e2)
//				{
//					// the outside surface is closest, so leave the volume
//					return scene.getColourAvoidingOrigin(
//							getRefractedRay(r, i, Orientation.OUTWARDS),
//							i.o,
//							l,
//							scene,
//							traceLevel-1,
//							raytraceExceptionHandler
//						);
//				}
//			}
//			
//			// either the closest intersection with the surface is farther than the next iteration point on the trajectory,
//			// or there is no such intersection with the surface in the first place,
//			// so keep iterating inside the volume
//			
//			// set the new light-ray position (using the vector d we calculated earlier)...
//			r.setP(Vector3D.sum(r.getP(), dXdTau.getProductWith(tau)));
//			
//			// ... and momentum
//			r.setK(Vector3D.sum(r.getK(), dKdTau(r.getP(), r.getK()).getProductWith(tau)));
			
			// if(endConditionMet(r)) return getColourWhenEndConditionMet();
		}
		
		// System.out.println("SurfaceOfMetricSpace:getColourUponStartingWithinVolume: Iteration limit reached; returning colour "+getColourWhenIterationLimitReached());
		
		// the ray just fizzles out
		return getColourWhenIterationLimitReached();
	}
	
	
	
	//
	// setters & getters
	//

	public SceneObject getSurface() {
		return surface;
	}

	public void setSurface(SceneObject surface) {
		this.surface = surface;
	}
	
	public double getDeltaTau() {
		return deltaTau;
	}

	public void setDeltaTau(double tau) {
		this.deltaTau = tau;
	}

	public double getDeltaXMax() {
		return deltaXMax;
	}

	public void setDeltaXMax(double deltaXMax) {
		this.deltaXMax = deltaXMax;
	}

	public int getMaxSteps() {
		return maxSteps;
	}

	public void setMaxSteps(int maxSteps) {
		this.maxSteps = maxSteps;
	}

	public IntegrationType getIntegrationType() {
		return integrationType;
	}

	public void setIntegrationType(IntegrationType integrationType) {
		this.integrationType = integrationType;
	}
}
