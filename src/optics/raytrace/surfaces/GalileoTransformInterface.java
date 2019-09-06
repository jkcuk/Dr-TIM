package optics.raytrace.surfaces;

import java.util.ArrayList;

import math.*;
import optics.DoubleColour;
import optics.raytrace.core.*;
import optics.raytrace.exceptions.RayTraceException;
import optics.raytrace.utility.CoordinateSystems;
import optics.raytrace.utility.CoordinateSystems.CoordinateSystemType;


/**
 * A surface property that takes the light-ray-direction change according to the Galileo transform as its generalised
 * law of refraction.
 * 
 * @author Johannes Courtial
 */
public class GalileoTransformInterface extends SurfacePropertyPrimitive
{	
	private static final long serialVersionUID = -905973805821474396L;

	private Vector3D
		beta;	// the apparent velocity of the world when seen through the surface, in units of c
	
	/**
	 * The basis in which beta is specified.
	 */
	private CoordinateSystemType basis;
		
	/**
	 * Creates an instance of the surface property representing a Lorentz-transform interface.
	 * @param beta
	 * @param basis
	 * @param transmissionCoefficient
	 */
	public GalileoTransformInterface(
			Vector3D beta,
			CoordinateSystemType basis,
			double transmissionCoefficient,
			boolean shadowThrowing)
	{
		super(transmissionCoefficient, shadowThrowing);
		setBeta(beta);
		setBasis(basis);
	}
		
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public GalileoTransformInterface clone()
	{
		return new GalileoTransformInterface(
				getBeta(),
				getBasis(),
				getTransmissionCoefficient(),
				isShadowThrowing()
			);
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
		
		// ray direction
		Vector3D d = ray.getD();
		
		Vector3D newRayDirection;
		
		if((beta.x == 0.0) && (beta.y == 0.0) && (beta.z == 0.0))
		{
			// beta = 0, so ray direction is unchanged
			newRayDirection = d;
		}
		else
		{
			// calculate beta in the global (x,y,z) coordinate basis
			Vector3D betaXYZ;
			switch(basis)
			{
			case LOCAL_OBJECT_BASIS:
			{
				SceneObject o = intersection.o;
				if(!(o instanceof One2OneParametrisedObject))
				{
					// do something, e.g. panic
					throw new RayTraceException("Scene object is not suitably parametrised!");
				}

				ArrayList<Vector3D> basisVectors = CoordinateSystems.getSurfaceBasis((One2OneParametrisedObject)o, intersection.p);
				betaXYZ = beta.fromBasis(basisVectors);
				break;
			}
			case NORMALSED_LOCAL_OBJECT_BASIS:
			{
				SceneObject o = intersection.o;
				if(!(o instanceof One2OneParametrisedObject))
				{
					// some form of panic here
					throw new RayTraceException("Scene object is not suitably parametrised!");
				}

				ArrayList<Vector3D> basisVectors = CoordinateSystems.getNormalisedSurfaceBasis((One2OneParametrisedObject)o, intersection.p);
				betaXYZ = beta.fromBasis(basisVectors);
				break;
			}
			case GLOBAL_BASIS:
			default:
				betaXYZ = beta;
				break;
			}

			// d + beta
			newRayDirection =
				Vector3D.sum(
						d,
						betaXYZ
				);
		}
		
		// System.out.println("New ray direction = " + newRayDirection);
		
		// launch a new ray from here
		
		return scene.getColourAvoidingOrigin(
			ray.getBranchRay(intersection.p, newRayDirection, intersection.t),	// creating the new ray using the original ray's getSecondaryRay method ensures the ray trajectory is recorded correctly
			intersection.o,	// the primitive scene object being intersected
			lights,	// the light source(s)
			scene,	// the entire scene
			traceLevel-1,	// launch the new ray with a trace level reduced by 1
			raytraceExceptionHandler
		).multiply(getTransmissionCoefficient());
	}

	public Vector3D getBeta() {
		return beta;
	}

	public void setBeta(Vector3D beta) {
		this.beta = beta;
	}

	public CoordinateSystemType getBasis() {
		return basis;
	}

	public void setBasis(CoordinateSystemType basis) {
		this.basis = basis;
	}
}
