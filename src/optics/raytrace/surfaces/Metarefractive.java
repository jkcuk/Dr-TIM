package optics.raytrace.surfaces;

import java.util.ArrayList;

import optics.DoubleColour;
import optics.raytrace.surfaces.metarefraction.Metarefraction;
import optics.raytrace.exceptions.EvanescentException;
import optics.raytrace.exceptions.RayTraceException;
import optics.raytrace.core.LightSource;
import optics.raytrace.core.ParametrisedObject;
import optics.raytrace.core.Ray;
import optics.raytrace.core.RaySceneObjectIntersection;
import optics.raytrace.core.RaytraceExceptionHandler;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.SurfacePropertyPrimitive;
import math.Vector3D;

/**
 * @author George Constable
 * 
 * Metarefractive redirects an incident ray as required.
 * It calls the refract Method of the class Metarefraction.
 *
 */

public class Metarefractive extends SurfacePropertyPrimitive
{
	private static final long serialVersionUID = -2877378136419962940L;

	protected Metarefraction metarefraction;
	
	//Constructor
	public Metarefractive(Metarefraction metarefraction, double transmissionCoefficient, boolean shadowThrowing)
	{
		super(transmissionCoefficient, shadowThrowing);
		this.metarefraction = metarefraction;
	}

	public Metarefractive(Metarefractive original)
	{
		super(original.getTransmissionCoefficient(), original.isShadowThrowing());
		setMetarefraction(original.getMetarefraction());
	}
	
	@Override
	public Metarefractive clone()
	{
		return new Metarefractive(this);
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.surfaces.SurfaceProperty#getColour(optics.raytrace.core.Ray, optics.raytrace.core.RaySceneObjectIntersection, optics.raytrace.sceneObjects.SceneObject, optics.raytrace.lights.LightSource, int)
	 */
	@Override
	public DoubleColour getColour(Ray ray, RaySceneObjectIntersection intersection, SceneObject scene, LightSource lights, int traceLevel, RaytraceExceptionHandler raytraceExceptionHandler)
	throws RayTraceException
	{
		// Check traceLevel is greater than 0.
		if(traceLevel <= 0) return DoubleColour.BLACK;
	
		//Get details of the metarefracting surface

		//Get the parent AnisotropicBiaxialSurface object that this surface property is associated with.
		ParametrisedObject surface = (ParametrisedObject)intersection.o;
		
		// Retrieve two Vector3Ds, normally tangential to the surface and orthogonal...
		ArrayList<Vector3D> surfaceBasis = surface.getSurfaceCoordinateAxes(intersection.p);
		// ... and normalise them
		ArrayList<Vector3D> surfaceBasisNormalised = new ArrayList<Vector3D>(2);
		surfaceBasisNormalised.add(surfaceBasis.get(0).getNormalised());
		surfaceBasisNormalised.add(surfaceBasis.get(1).getNormalised());

		// Change initial Vector3D direction r to coordinate system of the surface point
		
		Vector3D rayDirectionIn = ray.getD().getNormalised();
		
		// Calculate component in each direction of normalised new coordinate system.
		// Vector3D surfaceNormal = Vector3D.crossProduct(surfaceXY.get(0),surfaceXY.get(1));
		Vector3D surfaceNormal = intersection.getNormalisedOutwardsSurfaceNormal();
		
		//Calculate incoming ray direction as Vector3D in coordinate system of the surface point.
		Vector3D rayDirectionSurfaceBasisIn = rayDirectionIn.toBasis(
					surfaceBasisNormalised.get(0),
					surfaceBasisNormalised.get(1),
					surfaceNormal	// normalised anyway
				);
		
		Vector3D rayDirectionSurfaceBasisOut;
		
		// Callback method Metarefraction, to obtain refracted Vector3D in surface basis.
		try {
			// is the ray direction pointing in the direction as the (outwards-pointing) surface normal?
			if(Vector3D.scalarProduct(ray.getD(), surfaceNormal) > 0.)
			{
				// the ray is travelling outwards
				rayDirectionSurfaceBasisOut = metarefraction.refractOutwards(rayDirectionSurfaceBasisIn);
			}
			else
			{
				// the ray is travelling inwards
				rayDirectionSurfaceBasisOut = metarefraction.refractInwards(rayDirectionSurfaceBasisIn);
			}
		}
		catch (EvanescentException e)
		{
			// this is normal -- return the reflected ray
			// (Don't multiply by the transmission coefficient, as this is TIR!)
			return Reflective.getReflectedColour(ray, intersection, scene, lights, traceLevel, raytraceExceptionHandler);

			// return DoubleColour.BLACK;
		}
		catch (RayTraceException e)
		{
			e.printStackTrace();
			return DoubleColour.ORANGE;
		}

		// Return Vector3D to scene basis
		Vector3D newRayDirection = rayDirectionSurfaceBasisOut.fromBasis(
				surfaceBasisNormalised.get(0),
				surfaceBasisNormalised.get(1),
				surfaceNormal
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

	public Metarefraction getMetarefraction() {
		return metarefraction;
	}

	public void setMetarefraction(Metarefraction metarefraction) {
		this.metarefraction = metarefraction;
	}
}



