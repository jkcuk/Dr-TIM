package optics.raytrace.surfaces;

import math.Vector3D;
import optics.DoubleColour;
import optics.raytrace.core.LightSource;
import optics.raytrace.core.Orientation;
import optics.raytrace.core.Ray;
import optics.raytrace.core.RaySceneObjectIntersection;
import optics.raytrace.core.RaytraceExceptionHandler;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.SurfacePropertyPrimitive;
import optics.raytrace.exceptions.RayTraceException;
import optics.raytrace.imagingElements.InhomogeneousPlanarImagingSurface;

/**
 * @author johannes
 * This class has been replaced by GlensSurface.
 */
public class GeneralisedLens extends SurfacePropertyPrimitive
{
	private static final long serialVersionUID = 7307699361386427113L;

	protected InhomogeneousPlanarImagingSurface ipis;
	
	/**
	 * A GeneralisedLens from an inhomogeneous planar imaging surface
	 * @param ipis
	 * @param transmissionCoefficient
	 * @param shadowThrowing
	 */
	public GeneralisedLens(InhomogeneousPlanarImagingSurface ipis, double transmissionCoefficient, boolean shadowThrowing)
	{
		super(transmissionCoefficient, shadowThrowing);

		this.ipis = ipis;
	}
	
	public GeneralisedLens(Vector3D pointOnPlane, Vector3D u, Vector3D v, Vector3D n, double eta, double f,
			double uCOverF, double vCOverF, double transmissionCoefficient, boolean shadowThrowing)
	{
		super(transmissionCoefficient, shadowThrowing);
		
		ipis = new InhomogeneousPlanarImagingSurface(pointOnPlane, u, v, n, eta, f, uCOverF, vCOverF);
	}

	public GeneralisedLens(Vector3D pointOnPlane, Vector3D u, Vector3D v, Vector3D n, Vector3D objectPosition,
			Vector3D imagePosition, InhomogeneousPlanarImagingSurface.ParameterCalculationMethod parameterCalculation, double parameterValue,
			double transmissionCoefficient, boolean shadowThrowing)
	{
		super(transmissionCoefficient, shadowThrowing);

		ipis = new InhomogeneousPlanarImagingSurface(pointOnPlane, u, v, n, objectPosition, imagePosition, parameterCalculation, parameterValue);
	}

	/**
	 * Clone the GeneralisedLens and its InhomogeneousPlanarImagingSurface
	 * @see optics.raytrace.core.SurfacePropertyPrimitive#clone()
	 */
	@Override
	public GeneralisedLens clone()
	{
		return new GeneralisedLens(ipis.getPointOnPlane(), ipis.getuHat(), ipis.getvHat(), ipis.getnHat(),
				ipis.getEta(), ipis.getF(), ipis.getuCOverF(), ipis.getvCOverF(),
				getTransmissionCoefficient(), isShadowThrowing());
	}
	
	

	@Override
	public DoubleColour getColour(Ray r, RaySceneObjectIntersection i, SceneObject scene, LightSource l,
			int traceLevel, RaytraceExceptionHandler raytraceExceptionHandler)
	throws RayTraceException
	{
		// check the trace level is positive; if not, return black
		if (traceLevel <= 0) return DoubleColour.BLACK;
		
		// use the imaging properties of the inhomogeneous planar imaging surface to calculate the refracted ray direction
		
		double thisF, thisEta;
		Vector3D thisLensCentreOverF;
		
		// is the ray approaching inwards or outwards?
		if(i.getRayOrientation(r) == Orientation.INWARDS)
		{
			thisF = ipis.getF();
			thisEta = ipis.getEta();
			thisLensCentreOverF = ipis.getLensCentreOverF();
		}
		else
		{
			// f and g switch roles, and eta goes to 1/eta
			thisF = ipis.getF() * ipis.getEta();
			thisEta = 1/ipis.getEta();
			
			// the position of the lens centre has to be divided by the "outward f", which is the
			// "inward f" * "inward eta".  But getLensCentreOverF returns the position of the
			// lens centre divided by "inward f", so we need to divide this further by "inward eta",
			// or alternatively multiply with "outward eta"
			thisLensCentreOverF = ipis.getLensCentreOverF().getProductWith(thisEta);
		}

		
		Vector3D cMinusIOverF;
		
		if(Double.isInfinite(thisF))
		{
			cMinusIOverF = new Vector3D(0,0,0);
		}
		else
		{
			cMinusIOverF = Vector3D.difference(thisLensCentreOverF, i.p.getProductWith(1/thisF));
		}
		
		// a vector in the direction of +d or minus d
		Vector3D d1 = Vector3D.sum(
				cMinusIOverF,
				ipis.getnHat().getProductWith(thisEta - 1),
				r.getD().getProductWith(1/Vector3D.scalarProduct(r.getD(), ipis.getnHat()))
		);

		// does the vector point in the right direction?
		if(Vector3D.scalarProduct(r.getD(), ipis.getnHat()) / Vector3D.scalarProduct(d1, ipis.getnHat()) < 0)
		{
			// vector points the wrong way; turn it round
			d1 = d1.getReverse();
		}
		
		// launch a new ray from here
		
		return scene.getColourAvoidingOrigin(
			r.getBranchRay(i.p, d1, i.t),	// creating the new ray using the original ray's getSecondaryRay method ensures the ray trajectory is recorded correctly
			i.o,	// the primitive scene object being intersected
			l,	// the light source(s)
			scene,	// the entire scene
			traceLevel-1,	// launch the new ray with a trace level reduced by 1
			raytraceExceptionHandler
		).multiply(getTransmissionCoefficient());
	}
}
