package optics.raytrace.surfaces;

import math.*;
import optics.DoubleColour;
import optics.raytrace.core.*;
import optics.raytrace.exceptions.EvanescentException;
import optics.raytrace.exceptions.RayTraceException;


/**
 * 
 * Realises a law of refraction that is formulated in terms of the angles of incidence and refraction
 * 
 * @author Johannes
 *
 */
public class RefractiveAngleFormulation extends SurfacePropertyPrimitive
{	
	private static final long serialVersionUID = -7314064413099454880L;

	/**
	 * @param transmissionCoefficient
	 */
	public RefractiveAngleFormulation(double transmissionCoefficient, boolean shadowThrowing)
	{
		super(transmissionCoefficient, shadowThrowing);
	}
	
	public RefractiveAngleFormulation(RefractiveAngleFormulation old)
	{
		this(old.getTransmissionCoefficient(), old.isShadowThrowing());
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public RefractiveAngleFormulation clone()
	{
		return new RefractiveAngleFormulation(this);
	}
	
	/**
	 * OVERRIDE THIS. Refraction of a ray that passes from the inside to the outside of the surface
	 * @param alphaInside	the angle of incidence, i.e. the angle on the inside.  Note that the coordinate axes are chosen such that THE ANGLE OF INCIDENCE IS ALWAYS POSITIVE!
	 * @return	the angle of refraction, i.e. corresponding the angle on the outside
	 */
	public double alphaOutside(double alphaInside, RaySceneObjectIntersection intersection)
	throws EvanescentException, RayTraceException
	{
		return alphaInside;
	}
	
	/**
	 * OVERRIDE THIS. Refraction of a ray that passes from the outside to the inside of the surface
	 * @param alphaOutside	the angle of incidence, i.e. the angle on the outside.  Note that the coordinate axes are chosen such that THE ANGLE OF INCIDENCE IS ALWAYS POSITIVE!
	 * @return	the angle of refraction, i.e. the corresponding angle on the inside
	 */
	public double alphaInside(double alphaOutside, RaySceneObjectIntersection intersection)
	throws EvanescentException, RayTraceException
	{
		return alphaOutside;
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.core.SurfaceProperty#getColour(optics.raytrace.core.Ray, optics.raytrace.core.RaySceneObjectIntersection, optics.raytrace.core.SceneObject, optics.raytrace.core.LightSource, int)
	 */
	@Override
	public DoubleColour getColour(Ray ray, RaySceneObjectIntersection intersection,
			SceneObject scene, LightSource lights, int traceLevel, RaytraceExceptionHandler raytraceExceptionHandler)
	throws RayTraceException
	{
		if(traceLevel <= 0) return DoubleColour.BLACK;
		
		Vector3D
			D = ray.getD().getNormalised(), // normalised incoming ray direction
			N = intersection.getNormalisedOutwardsSurfaceNormal(), // unit surface normal at point of intersection
			T = D.getPartPerpendicularTo(N).getNormalised();	// unit vector in plane of incidence tangential to surface
		
		double
			cos = Vector3D.scalarProduct(D, N),
			sin = Vector3D.scalarProduct(D, T),
			alpha = Math.asin(sin),	// the angle of incidence
			alphaPrime,
			cosSign;
		
		try {
			// is the ray direction pointing in the same direction as the (outwards-pointing) surface normal?
			if(cos > 0.)
			{				
				// the ray is travelling outwards
				
				cosSign = 1.;
				alphaPrime = alphaOutside(alpha, intersection);
			}
			else
			{
				// the ray is travelling inwards
				
				cosSign = -1;
				alphaPrime = alphaInside(alpha, intersection);
			}
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

		// Return Vector3D to scene basis
		Vector3D newRayDirection =
			Vector3D.sum(
					N.getProductWith(Math.cos(alphaPrime)*cosSign),
					T.getProductWith(Math.sin(alphaPrime))	
				);
		
		// calculate cos(angle of new ray with normal) / cos(angle of old ray with normal);
		// brightness changes by this factor
		// provided the ray directions are normalised, this is simply the modulus of the
		// ratio of the ray-direction components normal to the surface
		// double cosRatio = Math.abs(rayDirectionSurfaceBasisOut.z / rayDirectionSurfaceBasisIn.z);
		//
		// not sure the intensity scales --- see http://www.astronomy.net/articles/29/
		// Also, one of the article's reviewers wrote this:
		// This is also related to the brightening in Fig. 7. In fact, I think that such a brightening should not occur.
		// It is known that brightness of an object does not change if the object is observed by some non-absorbing optical
		// instrument. For example, a sun reflected in a curved metallic surface is equally bright as if it is viewed directly.
		// I expect the same for teleported image. Maybe if the effect of the additional factor in eq. (5) is taken into
		// account together with the other method of calculation of the ray direction, no brightening will occur.

		
		// launch a new ray from here
		
		return scene.getColourAvoidingOrigin(
			ray.getBranchRay(intersection.p, newRayDirection, intersection.t),
			intersection.o,
			lights,
			scene,
			traceLevel-1,
			raytraceExceptionHandler
		).multiply(getTransmissionCoefficient());	// *cosRatio --- see above
	}
}
