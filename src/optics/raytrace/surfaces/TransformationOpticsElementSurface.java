package optics.raytrace.surfaces;

import optics.DoubleColour;
import optics.raytrace.core.LightSource;
import optics.raytrace.core.Ray;
import optics.raytrace.core.RaySceneObjectIntersection;
import optics.raytrace.core.RaytraceExceptionHandler;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.SurfacePropertyPrimitive;
import optics.raytrace.exceptions.RayTraceException;
import math.Vector3D;
import math.ODE.Derivatives;

/**
 * SurfaceProperty that describe a transformation-optics element.
 * Unlike (most) other surface properties, what matters here is what's inside the object.
 * The idea here is to define the material properties on a raster of points, surround each of those points by a voxel
 * and assume the material is homogeneous in each voxel, and then calculate the ray trajectory by calculating refraction
 * at the interface between voxels according to Eqns (42) and (43) in Ref. [1] (or similar).
 * 
 * This should be generalised to a surface whose inside is filled with sets of parallel, equidistant, planes
 * (see SetOfEquidistantParallelPlanes class), and the spaces between the
 * planes define the "voxels".  For example, for three such sets in which the planes in each set are perpendicular to
 * the planes in both other sets would enclose cuboid voxels.
 * 
 * An efficient way to trace through such voxels could be the following:
 * If the starting point of the ray is inside the transformation-optics surface, define a "scene" that consists of
 * the two closest planes in each set of planes (unless the starting point is on a plane, in which case take the two
 * next closest planes) and the transformation-optics-element surface, and trace to the nearest intersection with this
 * scene.
 * At the surface, refract according to the metric tensor interface.
 * Keep doing this until the ray ends up on the transformation-optics-element surface again; normally the ray would
 * then exit the element.
 * 
 * [1] D. Schurig, J. B. Pendry and D. R. Smith, "Calculation of material properties and ray tracing in transformation media",
 * Opt. Express� 14� 9794-9804� (2006)
 * 
 * @see optics.raytrace.core.SceneObjectPrimitive#getNormalisedSurfaceNormal(Vector3D p)
 * @author Johannes Courtial
 */
public class TransformationOpticsElementSurface extends SurfacePropertyPrimitive
{
	// [1] D. Schurig, J. B. Pendry and D. R. Smith, "Calculation of material properties and ray tracing in
	// transformation media", Opt. Express 14, 9794-9804 (2006)

	private static final long serialVersionUID = -353501707653746879L;

	/**
	 * An interface with a call-back method that calculates dx/dTau and dk/dTau.
	 * This then allows numerical integration of the (Hamilton-oid) Eqns (41) in [1].
	 * The standard way to calculate these is
	 * 		dx_i / dTau = \frac{\partial H}{\partial k_i},
	 * 		dk_i / dTau = -\frac{\partial H}{\partial x_i}.
	 */
	private Derivatives xAndKDerivatives;
	// TODO need, somehow, a suitable coordinate system
	
	// TODO Fresnel reflections etc.
	
	// TODO refraction at a general discontinuity -- see [1]; for the moment, assume perfect index-matching
	private double ratioNSurfaceNSurrounding;	// refractive-index ratio
	
	private Vector3D drdt, dkdt;

	/**
	 * Creates a new transformation-optics-element surface.
	 * @param xAndKDerivatives	An interface with a call-back method that calculates dx/dTau and dk/dTau. This then allows numerical integration of the (Hamilton-oid) Eqns (41) in [1].
	 * @param ratioNSurfaceNSurrounding	ratio of the refractive index at the edge of the lens and that outside the lens
	 * @param transmissionCoefficient
	 */
	public TransformationOpticsElementSurface(Derivatives xAndKDerivatives, double ratioNSurfaceNSurrounding, double transmissionCoefficient, boolean shadowThrowing)
	{
		super(transmissionCoefficient, shadowThrowing);
		setxAndKDerivatives(xAndKDerivatives);
		setRatioNSurfaceNSurrounding(ratioNSurfaceNSurrounding);
	}
	
	/**
	 * Creates a new, perfect, transformation-optics-element surface.
	 */
	public TransformationOpticsElementSurface(Derivatives xAndKDerivatives)
	{
		this(
				xAndKDerivatives,
				1.0,	// refractive-index ratio
				1.0,	// transmission coefficient
				true	// shadow-throwing
			);
	}

	/**
	 * Clone the original transformation-optics-element surface
	 * @param original
	 */
	public TransformationOpticsElementSurface(TransformationOpticsElementSurface original)
	{
		this(
				original.getxAndKDerivatives(),
				original.getRatioNSurfaceNSurrounding(),
				original.getTransmissionCoefficient(),
				original.isShadowThrowing()
			);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public TransformationOpticsElementSurface clone()
	{
		return new TransformationOpticsElementSurface(this);
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.core.SurfaceProperty#getColour(optics.raytrace.core.Ray, optics.raytrace.core.RaySceneObjectIntersection, optics.raytrace.core.SceneObject, optics.raytrace.core.LightSource, int)
	 */
	@Override
	public DoubleColour getColour(Ray ray, RaySceneObjectIntersection i, SceneObject scene, LightSource l, int traceLevel, RaytraceExceptionHandler raytraceExceptionHandler)
	throws RayTraceException
	{
		if (traceLevel <= 0) return DoubleColour.BLACK;
		
		// TODO
		// for the moment
		return DoubleColour.ORANGE;
//				
//		// take an initial step, then check whether the ray is inside the object, i.o
//		SceneObject o = i.o;
//		
//		// the initial position
//		Vector3D r = i.p;
//		
//		// the initial k vector
//		// TODO k does not always point in the direction of the light-ray direction.  Fix this!
//		Vector3D k = ray.getD();
//		
//		// now do raytracing inside the medium; use the differential-equation solvers in math.ODE
//		RungeKutta.calculateStep
//		
//		try
//		{
//			// first find the light-ray direction after refraction at the edge of the lens
//			Vector3D dInside = RefractionGeometry.refract(ray.getD(), R, 1/getRatioNSurfaceNSurrounding());
//
//			// ... and its part that is perpendicular to the ray direction
//			Vector3D RPerpendicular = R.getPartPerpendicularTo(dInside);
//			
//			// calculate the new starting point
//			Vector3D newStartingPoint = Vector3D.sum(i.p, RPerpendicular.getProductWith(2));
//			
//			// Calculate the new light-ray direction before refraction at the edge of the lens...
//			Vector3D dPrimeInside = dInside.getReverse();
//			
//			// ... and after refraction
//			Vector3D newRayDirection = RefractionGeometry.refract(dPrimeInside, s.getNormalisedSurfaceNormal(newStartingPoint), getRatioNSurfaceNSurrounding());
//			
//			// launch a new ray from here
//			return scene.getColourAvoidingOrigin(
//					ray.getBranchRay(newStartingPoint, newRayDirection, t),
//					i.o,	// object to avoid
//					l,
//					scene,
//					traceLevel-1,
//					raytraceExceptionHandler
//				).multiply(getTransmissionCoefficient());
//		}
//		catch (EvanescentException e)
//		{
//			// this is normal -- return the reflected ray
//			// (Don't multiply by the transmission coefficient, as this is TIR!)
//			return Reflective.getReflectedColour(ray, i, scene, l, traceLevel, raytraceExceptionHandler);
//		}

	}
	
	/**
	 * sets the variables drdt and dkdt
	 * @param t	the current time (normally ignored --- our objects are not normally time-dependent)
	 * @param r	the position vector
	 * @param k	the k vector
	 */
	public void calculateDerivatives(double t, Vector3D r, Vector3D k)
	{
		double f[] = new double[6];	// the r vector and the k vector in a format that the Derivatives interface likes
		double dfdt[] = new double[6];	// dr/dt and dk/dt in such a format

		// initialise x
		f[0] = r.x;
		f[1] = r.y;
		f[2] = r.z;
		
		// initialise k
		f[3] = k.x;
		f[4] = k.y;
		f[5] = k.z;

		xAndKDerivatives.calculateDerivatives(t, f, dfdt);
		
		drdt.x = dfdt[0];
		drdt.y = dfdt[1];
		drdt.z = dfdt[2];
		
		dkdt.x = dfdt[3];
		dkdt.y = dfdt[4];
		dkdt.z = dfdt[5];
	}


	public Derivatives getxAndKDerivatives() {
		return xAndKDerivatives;
	}

	public void setxAndKDerivatives(Derivatives xAndKDerivatives) {
		this.xAndKDerivatives = xAndKDerivatives;
	}

	public double getRatioNSurfaceNSurrounding() {
		return ratioNSurfaceNSurrounding;
	}

	public void setRatioNSurfaceNSurrounding(double ratioNSurfaceNSurrounding) {
		this.ratioNSurfaceNSurrounding = ratioNSurfaceNSurrounding;
	}
}
