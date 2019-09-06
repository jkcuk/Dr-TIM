package optics.raytrace.surfaces;

import java.util.ArrayList;

import math.*;
import optics.DoubleColour;
import optics.raytrace.core.*;
import optics.raytrace.exceptions.EvanescentException;
import optics.raytrace.exceptions.RayTraceException;
import optics.raytrace.utility.CoordinateSystems.GlobalOrLocalCoordinateSystemType;


/**
 * A surface property representing the interface between two spaces with different metrics.
 * The metric tensor of the space in front is g, that of the space behind is h.
 * The metric tensors are specified in terms of the object's coordinate system, i.e.
 * (normalised surface tangent 1, normalised surface tangent 2, normalised outwards surface normal).
 * 
 * @author Johannes Courtial
 */
public class MetricInterface extends SurfacePropertyPrimitive
{	
	private static final long serialVersionUID = 7911749022411285943L;
	
	public static final double[] euclideanMetricTensor = getMetricTensorForRefractiveIndex(1);
	
	/**
	 * The basis in which the metric tensors are specified.
	 */
	// private CoordinateSystemType basis;
	
	public enum RefractionType
	{
		POSITIVE_REFRACTION("Positive refraction"),
		NEGATIVE_REFRACTION("Negative refraction");
		
		private String description;
		private RefractionType(String description) {this.description = description;}
		
		@Override
		public String toString() {return description;}
	}

	/**
	 * The total optical path length is
	 * * the sum of the optical path lengths on either side of the interface for positive refraction and
	 * * the difference of the optical path lengths on either side of the interface for negative refraction.
	 */
	protected RefractionType refractionType;
	
	/**
	 * allowing imaginary optical path lengths admits additional solutions
	 */
	protected boolean allowImaginaryOpticalPathLengths; 

	// symmetric tensors are stored as a 1D array, element (1,1) is stored in position 0,
	// element (1,2) in index in position 1, etc.
	public static final int _11 = 0, _12 = 1, _13 = 2, _22 = 3, _23 = 4, _33 = 5;
	
	/**
	 * The symmetric tensors on the inside and on the outside, in the form
	 */
	private double[] metricTensorInside, metricTensorOutside;

	/**
	 * The basis in which the metric tensors are specified.
	 */
	private GlobalOrLocalCoordinateSystemType basis;

	/**
	 * Creates an instance of the surface property representing (specularly) reflective surfaces
	 * 
	 * @param reflectionCoefficient
	 */
	public MetricInterface(double[] metricTensorInside, double[] metricTensorOutside, GlobalOrLocalCoordinateSystemType basis, RefractionType refractionType, boolean allowImaginaryOpticalPathLengths, double transmissionCoefficient, boolean shadowThrowing)
	{
		super(transmissionCoefficient, shadowThrowing);
		this.metricTensorInside = metricTensorInside;
		this.metricTensorOutside = metricTensorOutside;
		this.basis = basis;
		this.refractionType = refractionType;
		this.allowImaginaryOpticalPathLengths = allowImaginaryOpticalPathLengths;
	}
	
	/**
	 * By default make the interface between air and air.
	 */
	public MetricInterface() {
		this(
				getMetricTensorForRefractiveIndex(1.0),
				getMetricTensorForRefractiveIndex(1.0),
				GlobalOrLocalCoordinateSystemType.LOCAL_OBJECT_BASIS,
				RefractionType.POSITIVE_REFRACTION,
				false,	// don't allow imaginary optical path lengths
				1.0,	// transmission coefficient
				true	// shadow-throwing
			);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public MetricInterface clone()
	{
		return new MetricInterface(
				getMetricTensorInside(),
				getMetricTensorOutside(),
				getBasis(),
				getRefractionType(),
				isAllowImaginaryOpticalPathLengths(),
				getTransmissionCoefficient(),
				isShadowThrowing()
			);
	}

	// a few methods for dealing with the special symmetric-metric-tensor format we're using here
	
	public static double[] getDiagonalMetricTensor(double g11, double g22, double g33)
	{
		double[] g = new double[6];
		g[_11] = g11;
		g[_22] = g22;
		g[_33] = g33;
		g[_12] = g[_13] = g[_23] = 0;
		return g;
	}

	/**
	 * @param g11
	 * @param g12
	 * @param g13
	 * @param g22
	 * @param g23
	 * @param g33
	 * @return the symmetric tensor with the above elements
	 */
	public static double[] getMetricTensor(double g11, double g12, double g13, double g22, double g23, double g33)
	{
		double[] g = new double[6];
		g[_11] = g11;
		g[_12] = g12;
		g[_13] = g13;
		g[_22] = g22;
		g[_23] = g23;
		g[_33] = g33;
		return g;
	}

	/**
	 * Note that the refractive index is squared, so its sign gets lost in this method!
	 * To represent the interface between metrics that represent optical path lengths with opposite signs,
	 * set the refractionSign to RefractionSignType.NEGATIVE_REFRACTION.
	 * @param refractiveIndex
	 * @return
	 */
	public static double[] getMetricTensorForRefractiveIndex(double refractiveIndex)
	{
		double n2 = refractiveIndex*refractiveIndex;
		double[] g = new double[6];
		g[_11] = g[_22] = g[_33] = n2;
		g[_12] = g[_13] = g[_23] = 0;
		return g;
	}
	
	/**
	 * @param g
	 * @param targetBasis
	 * @return the symmetric metric tensor g, transformed into the target basis
	 */
	public static double[] transformMetricTensor(double[] g, ArrayList<Vector3D> targetBasis)
	{
		// create space for the transformed metric tensor (symmetric again)
		double[] gPrime = new double[6];
		
		// the coordinates of u, v and w, the basis vectors
		double
			ux = targetBasis.get(0).x,
			uy = targetBasis.get(0).y,
			uz = targetBasis.get(0).z,
			vx = targetBasis.get(1).x,
			vy = targetBasis.get(1).y,
			vz = targetBasis.get(1).z,
			wx = targetBasis.get(2).x,
			wy = targetBasis.get(2).y,
			wz = targetBasis.get(2).z;

		// set the elements, calculated by Mathematica
		gPrime[_11] = 2*ux*uy*g[_12] + 2*ux*uz*g[_13] + 2*uy*uz*g[_23] + g[_11]*pow2(ux) + g[_22]*pow2(uy) + g[_33]*pow2(uz);
		gPrime[_12] = ux*vx*g[_11] + uy*vx*g[_12] + ux*vy*g[_12] + uz*vx*g[_13] + ux*vz*g[_13] + uy*vy*g[_22] + uz*vy*g[_23] + uy*vz*g[_23] + uz*vz*g[_33];
		gPrime[_13] = ux*wx*g[_11] + uy*wx*g[_12] + ux*wy*g[_12] + uz*wx*g[_13] + ux*wz*g[_13] + uy*wy*g[_22] + uz*wy*g[_23] + uy*wz*g[_23] + uz*wz*g[_33];
		gPrime[_22]	= 2*vx*vy*g[_12] + 2*vx*vz*g[_13] + 2*vy*vz*g[_23] + g[_11]*pow2(vx) + g[_22]*pow2(vy) + g[_33]*pow2(vz);
		gPrime[_23]	= vx*wx*g[_11] + vy*wx*g[_12] + vx*wy*g[_12] + vz*wx*g[_13] + vx*wz*g[_13] + vy*wy*g[_22] + vz*wy*g[_23] + vy*wz*g[_23] + vz*wz*g[_33];
		gPrime[_33] = 2*wx*wy*g[_12] + 2*wx*wz*g[_13] + 2*wy*wz*g[_23] + g[_11]*pow2(wx) + g[_22]*pow2(wy) + g[_33]*pow2(wz);
		
		// ... and return the transformed metric tensor
		return gPrime;
	}
	
	public double[] getMetricTensorInside() {
		return metricTensorInside;
	}

	public void setMetricTensorInside(double[] metricTensorInside) {
		this.metricTensorInside = metricTensorInside;
	}

	public double[] getMetricTensorOutside() {
		return metricTensorOutside;
	}

	public void setMetricTensorOutside(double[] metricTensorOutside) {
		this.metricTensorOutside = metricTensorOutside;
	}
	
	public GlobalOrLocalCoordinateSystemType getBasis() {
		return basis;
	}

	public void setBasis(GlobalOrLocalCoordinateSystemType basis) {
		this.basis = basis;
	}

	public RefractionType getRefractionType() {
		return refractionType;
	}

	public void setRefractionType(RefractionType refractionType) {
		this.refractionType = refractionType;
	}

	public boolean isAllowImaginaryOpticalPathLengths() {
		return allowImaginaryOpticalPathLengths;
	}

	public void setAllowImaginaryOpticalPathLengths(
			boolean allowImaginaryOpticalPathLengths) {
		this.allowImaginaryOpticalPathLengths = allowImaginaryOpticalPathLengths;
	}

	/**
	 * A useful little routine that squares a number
	 * @param x
	 * @return
	 */
	private static double pow2(double x)
	{
		return x*x;
	}

//	private static double evaluatesToZeroIfCorrectSolution(double ex, double ey, double[][] h, double cx, double cy)
//	{
//		double sqrt = Math.sqrt(ex*h[0][2] + ey*h[1][2] + ex*(ex*h[0][0] + ey*h[1][0] + h[2][0]) + ey*(ex*h[0][1] + ey*h[1][1] + h[2][1]) + h[2][2]);
//		double zeroXSolution = (-2*ex*h[0][0] - ey*h[0][1] - h[0][2] - ey*h[1][0] - h[2][0])/(2*sqrt) + cx;
//		double zeroYSolution = (-(ex*h[0][1]) - ex*h[1][0] - 2*ey*h[1][1] - h[1][2] - h[2][1])/(2*sqrt) + cy;
//		return zeroXSolution*zeroXSolution + zeroYSolution*zeroYSolution;
//	}

//	/**
//	 * For checking if the solution found is correct
//	 * @param ex
//	 * @param ey
//	 * @param ez
//	 * @param h
//	 * @param cx
//	 * @param cy
//	 * @return
//	 */
//	public static double evaluatesToZeroIfCorrectSolution(double ex, double ey, double ez, double[] h, double cx, double cy)
//	{
//		// should be zero if the equation for the x derivative holds
//		double zeroX = ex*h[_11] + ey*h[_12] + ez*h[_13] - cx;
//		
//		// should be zero if the equation for the y derivative holds
//		double zeroY = ex*h[_12] + ey*h[_22] + ez*h[_23] - cy;
//		
//		// overall, then, the following should be zero if the equations for both derivatives hold:
//		return pow2(zeroX) + pow2(zeroY);
//	}


	/**
	 * @param d incident light-ray direction, <b>in surface's coordinate system</b>
	 * @param g	metric tensor in incident-light space; <b>in surface's coordinate system</b>
	 * @param h	metric tensor in refracted-light space; <b>in surface's coordinate system</b>
	 * @return direction of refracted light ray, <b>in surface's coordinate system</b>
	 * @throws RayTraceException
	 */
	public static Vector3D getRefractedLightRayDirectionSurfaceCoordinates(
			Vector3D d,
			double[] g,
			double[] h,
			RefractionType refractionType,
			boolean allowImaginaryOpticalPathLengths
		)
	throws RayTraceException
	{
		// calculate direction of refracted ray
		// see Mathematica document "metric tensor and generalised refraction 3D.nb".
		// I converted the expressions into "raw input form" (Cell -> Convert To -> Raw InputForm), and then copied them.
		// dx -> d.x, dy -> d.y, dz -> d.z
		// g11 -> g[0][0], g12 -> g[0][1], g13 -> g[0][2],
		// g21 -> g[1][0], g22 -> g[1][1], g23 -> g[1][2],
		// g31 -> g[2][0], g32 -> g[2][1], g33 -> g[2][2],
		// h11 -> h[0][0], h12 -> h[0][1], h13 -> h[0][2],
		// h21 -> h[1][0], h22 -> h[1][1], h23 -> h[1][2],
		// h31 -> h[2][0], h32 -> h[2][1], h33 -> h[2][2],
		
		double hhhh = pow2(h[_12]) - h[_11]*h[_22];
		
		// the law of refraction is derived for hhhh != 0
		if(hhhh == 0.0)
		{
			throw new RayTraceException("Direction of refracted ray is undetermined.");
		}

		// the term under the square root in the expressions for cx and for cy, (d^T).g.d
		double dgd =
			d.x*(d.x*g[_11] + d.y*g[_12] + d.z*g[_13]) +
			d.y*(d.x*g[_12] + d.y*g[_22] + d.z*g[_23]) +
			d.z*(d.x*g[_13] + d.y*g[_23] + d.z*g[_33]);
		
		if(!allowImaginaryOpticalPathLengths && (dgd < 0))
		{
			throw new EvanescentException("Refracted ray is evanescent (or its optical path length is imaginary, but the switch to allow imaginary optical path lengths is off).");
		}
		
		// +1 for positive refraction, -1 for negative refraction
		double refractionSign = (refractionType == RefractionType.POSITIVE_REFRACTION)?(+1.0):(-1.0);
		
		double sqrtdgd = Math.sqrt(Math.abs(dgd));
		// the sign of dgd goes into the calculation of ez2 --- see below
		
		// calculate cx, i.e. d s_1 / dx at x=0, y=0
		double cx = refractionSign * (d.x*g[_11] + d.y*g[_12] + d.z*g[_13]) / sqrtdgd;
		
		// System.out.println("cx="+cx);

		// calculate cy, i.e. d s_1 / dy at x=0, y=0
		double cy = refractionSign * (d.x*g[_12] + d.y*g[_22] + d.z*g[_23]) / sqrtdgd;
		
		// the coefficients a and c in the quadratic equation for e_z
		double
			a = pow2(h[_13])*h[_22] - 2*h[_12]*h[_13]*h[_23] + pow2(h[_12])*h[_33] + h[_11]*pow2(h[_23]) - h[_11]*h[_22]*h[_33],
			c = - cy*cy*h[_11] + 2*cx*cy*h[_12] - cx*cx*h[_22] + Math.signum(dgd)*(h[_11]*h[_22] - pow2(h[_12]));

		// what's under the square root in e_z; note that the numerator depends on the sign of dgd
		double ez2 = -c/a;
	
		// ez2 is what's inside a square root; check it's positive
		if(ez2 < 0)
		{
			// System.err.println("Imaginary ray direction due to square root of negative number.");
			throw new EvanescentException("Refracted ray is (presumably) evanescent.");
		}

		// both signs of ez are allowed; make sure the sign of the z component e is the same as that of d
		// (so that it exits the window from the opposite side where it enters)
		double
			ez = Math.signum(d.z) * Math.sqrt(ez2);
		
		// calculate Mathematica's solutions for ex and ey
		double
			ex = (cy*h[_12] - cx*h[_22] + (h[_13]*h[_22] - h[_12]*h[_23])*ez) / hhhh,
			ey = (cx*h[_12] - cy*h[_11] + (h[_11]*h[_23] - h[_12]*h[_13])*ez) / hhhh;
		
		// for debugging: test whether the solution really satisfies the equations
//		double
//			checkX = ex*h[_11] + ey*h[_12] + ez*h[_13] - cx,
//			checkY = ex*h[_12] + ey*h[_22] + ez*h[_23] - cy,
//			ehe =
//				ex*(ex*h[_11] + ey*h[_12] + ez*h[_13]) +
//				ey*(ex*h[_12] + ey*h[_22] + ez*h[_23]) +
//				ez*(ex*h[_13] + ey*h[_23] + ez*h[_33]);
//		System.out.println(
//				"ex*h_11 + ey*h_12 + ez*h_13 - cx = " + checkX + " (should be 0), " +
//				"ex*h_12 + ey*h_22 + ez*h_23 - cy = " + checkY + " (should be 0), " +
//				"e^T.h.e = " + ehe + " (should be 1)"
//				);
		
		return new Vector3D(ex, ey, ez);
	}
	
	/**
	 * @param d incident light-ray direction, <b>in surface's coordinate system</b>
	 * @param g	metric tensor in incident-light space; <b>in surface's coordinate system</b>
	 * @param h	metric tensor in refracted-light space; <b>in surface's coordinate system</b>
	 * @return direction of refracted light ray, <b>in surface's coordinate system</b>
	 * @throws RayTraceException
	 */
	public static Vector3D getRefractedLightRayDirection(
			Vector3D d,
			RaySceneObjectIntersection i,
			double[] g,
			double[] h,
			RefractionType refractionType,
			GlobalOrLocalCoordinateSystemType basis,
			boolean allowImaginaryOpticalPathLengths
		)
	throws RayTraceException
	{
		// Retrieve two Vector3Ds, normally tangential to the surface and orthogonal, ...
		ArrayList<Vector3D> surfaceBasis = ((ParametrisedObject)i.o).getSurfaceCoordinateAxes(i.p);
		// ... normalize them, ...
		ArrayList<Vector3D> localBasis = new ArrayList<Vector3D>(3);
		localBasis.add(surfaceBasis.get(0).getNormalised());
		localBasis.add(surfaceBasis.get(1).getNormalised());
		// ... and add the surface normal to complete the local basis
		localBasis.add(i.getNormalisedOutwardsSurfaceNormal());

		// are the metric tensors defined in the global coordinate system?
		if(basis == GlobalOrLocalCoordinateSystemType.GLOBAL_BASIS)
		{
			// yes, so we need to transform the metric tensors into the surface coordinate system
			g = transformMetricTensor(g, localBasis);
			h = transformMetricTensor(h, localBasis);
		}

		// Calculate the incident ray direction as Vector3D in coordinate system of the surface point, ...
		Vector3D dLocalBasis = d.toBasis(localBasis);

		// ... refract it, ...
		Vector3D dPrimeLocalBasis = getRefractedLightRayDirectionSurfaceCoordinates(
				dLocalBasis,
				g, h, // the metric tensors
				refractionType,	// positive or negative refraction?
				allowImaginaryOpticalPathLengths	// still consider refraction if optical path lengths become imaginary?
			);

		// ... and convert it back into the "standard" basis
		return dPrimeLocalBasis.fromBasis(localBasis);
	}
	

	/* (non-Javadoc)
	 * @see optics.raytrace.SurfaceProperty#getColour(optics.raytrace.Ray, optics.raytrace.RaySceneObjectIntersection, optics.raytrace.SceneObject, optics.raytrace.LightSource, int)
	 */
	@Override
	public DoubleColour getColour(Ray ray, RaySceneObjectIntersection intersection, SceneObject scene, LightSource lights, int traceLevel, RaytraceExceptionHandler raytraceExceptionHandler)
	throws RayTraceException
	{
		// check the trace level is positive; if not, return black
		if (traceLevel <= 0) return DoubleColour.BLACK;

		try
		{
			double[] g, h;

			// is the incident light ray passing through the surface inwards or outwards?
			switch(Orientation.getOrientation(ray.getD(), intersection.getNormalisedOutwardsSurfaceNormal()))
			{
			case OUTWARDS:
				g = metricTensorInside;
				h = metricTensorOutside;
				break;
			case INWARDS:
			default:
				g = metricTensorOutside;
				h = metricTensorInside;
			}
			
			Vector3D dPrime = getRefractedLightRayDirection(
					ray.getD(),	// light-ray direction
					intersection,	// intersection with scene object
					g,
					h,
					getRefractionType(),
					getBasis(),
					isAllowImaginaryOpticalPathLengths()
				);
			
			return scene.getColourAvoidingOrigin(
					ray.getBranchRay(intersection.p, dPrime, intersection.t),	// creating the new ray using the original ray's getSecondaryRay method ensures the ray trajectory is recorded correctly
					intersection.o,	// the primitive scene object being intersected
					lights,	// the light source(s)
					scene,	// the entire scene
					traceLevel-1,	// launch the new ray with a trace level reduced by 1
					raytraceExceptionHandler
			).multiply(getTransmissionCoefficient());
		}
		catch (EvanescentException e)
		{
			// this is normal -- return the reflected ray
			// (Don't multiply by the transmission coefficient, as this is TIR!)
			return Reflective.getReflectedColour(ray, intersection, scene, lights, traceLevel, raytraceExceptionHandler);
		}
		catch (RayTraceException e)
		{
			e.printStackTrace();
			return DoubleColour.ORANGE;
		}
	}
}
