package optics.raytrace.surfaces;

import math.Vector3D;
import optics.DoubleColour;
import optics.raytrace.core.LightSource;
import optics.raytrace.core.Ray;
import optics.raytrace.core.RaySceneObjectIntersection;
import optics.raytrace.core.RaytraceExceptionHandler;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.SurfaceProperty;
import optics.raytrace.exceptions.RayTraceException;

/**
 * @author johannes
 * This surface property simulates a diffusing, but otherwise transparent, surface.
 * It changes the direction of transmitted light rays according to some probability distribution.
 * If this probability distribution is a sinc^2, then this can be used to simulate single-slit diffraction.
 */
public abstract class Diffusion extends SurfaceProperty
{
	private static final long serialVersionUID = 5610013294957869681L;



	/**
	 * This method describes the ray-direction change
	 * @param incidentNormalisedRayDirection
	 * @param normalisedOutwardsSurfaceNormal
	 * @return
	 */
	public abstract Vector3D calculateDirectionChange(
			Vector3D incidentNormalisedRayDirection,
			Vector3D normalisedOutwardsSurfaceNormal
		);
	

	//
	// SurfaceProperty methods
	//
	
	@Override
	public DoubleColour getColour(
			Ray ray,
			RaySceneObjectIntersection intersection,
			SceneObject scene,
			LightSource lights,
			int traceLevel,
			RaytraceExceptionHandler raytraceExceptionHandler
	)
	throws RayTraceException
	{
		if(traceLevel <= 0) return DoubleColour.BLACK;
		
		Vector3D rayDirection = calculateDirectionChange(
						ray.getD().getNormalised(),	// incidentNormalisedRayDirection
						intersection.getNormalisedOutwardsSurfaceNormal()	// normalisedOutwardsSurfaceNormal
					);

		// launch a new ray from here
			
		return scene.getColourAvoidingOrigin(
						ray.getBranchRay(
								intersection.p,	// start position
								rayDirection,	// ray direction
								intersection.t	// start time
								),
						intersection.o,
						lights,
						scene,
						traceLevel-1,
						raytraceExceptionHandler
				);
	}



	@Override
	public boolean isShadowThrowing() {
		return false;
	}
}
