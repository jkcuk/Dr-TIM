package optics.raytrace.surfaces.metarefraction;

import optics.raytrace.exceptions.RayTraceException;
import math.Vector3D;


/** Define Variables and methods of abstract class.
 */
public abstract class Metarefraction implements Cloneable
{
	/**
	 * Perform generalized light-ray-direction change.
	 * Applied when crossing the surface from the inside to the outside
	 * @param incidentRayDirection	the incident light-ray direction in the normalised surface basis
	 * @return changed light-ray direction, again in the normalised surface basis
	 */
	public abstract Vector3D refractOutwards(Vector3D incidentRayDirection) throws RayTraceException;
	
	/**
	 * Perform generalized light-ray-direction change.
	 * Applied when crossing the surface from the inside to the outside
	 * @param incidentRayDirection	the incident light-ray direction in the normalised surface basis
	 * @return changed light-ray direction, again in the normalised surface basis
	 */
	public abstract Vector3D refractInwards(Vector3D incidentRayDirection) throws RayTraceException;
	
//	/* (non-Javadoc)
//	 * @see java.lang.Object#clone()
//	 */
//	public abstract Metarefraction clone();
}