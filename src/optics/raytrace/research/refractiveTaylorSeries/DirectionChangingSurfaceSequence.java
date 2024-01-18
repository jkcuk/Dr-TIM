package optics.raytrace.research.refractiveTaylorSeries;

import java.util.ArrayList;

import math.Vector3D;
import optics.raytrace.core.Ray;
import optics.raytrace.core.RaySceneObjectIntersection;
import optics.raytrace.core.SceneObjectPrimitive;
import optics.raytrace.exceptions.RayTraceException;
import optics.raytrace.surfaces.DirectionChanging;

/**
 * A "stack" of scene objects, each with a direction-changing surface.
 * Light rays pass through these in the given order, intersecting each scene object exactly once.
 */
public class DirectionChangingSurfaceSequence {
	
	/**
	 * the scene objects with the direction-changing surfaces
	 */
	ArrayList<SceneObjectPrimitive> sceneObjectPrimitivesWithDirectionChangingSurfaces;
	

	// constructors
	
	public DirectionChangingSurfaceSequence(
			ArrayList<SceneObjectPrimitive> sceneObjectPrimitivesWithDirectionChangingSurfaces) {
		super();
		this.sceneObjectPrimitivesWithDirectionChangingSurfaces = sceneObjectPrimitivesWithDirectionChangingSurfaces;
	}
	
	/**
	 * create an empty direction-changing surface sequence
	 */
	public DirectionChangingSurfaceSequence() {
		this(new ArrayList<SceneObjectPrimitive>());
	}
	

	//  setters & getters

	/**
	 * @return the sceneObjectPrimitivesWithDirectionChangingSurfaces
	 */
	public ArrayList<SceneObjectPrimitive> getSceneObjectPrimitivesWithDirectionChangingSurfaces() {
		return sceneObjectPrimitivesWithDirectionChangingSurfaces;
	}

	/**
	 * @param sceneObjectPrimitivesWithDirectionChangingSurfaces the sceneObjectPrimitivesWithDirectionChangingSurfaces to set
	 */
	public void setSceneObjectPrimitivesWithDirectionChangingSurfaces(ArrayList<SceneObjectPrimitive> sceneObjectPrimitivesWithDirectionChangingSurfaces) {
		this.sceneObjectPrimitivesWithDirectionChangingSurfaces = sceneObjectPrimitivesWithDirectionChangingSurfaces;
	}

	public void addSceneObjectPrimitiveWithDirectionChangingSurface(SceneObjectPrimitive sceneObjectPrimitive)
	{
		sceneObjectPrimitivesWithDirectionChangingSurfaces.add(sceneObjectPrimitive);
	}

	
	// the "meat"

	/**
	 * For a  given incident ray, calculate the outgoing ray  after transmission through the DirectionChangingSurfaceSequence
	 * @param incidentRay
	 * @return
	 * @throws RayTraceException
	 */
	public Ray calculateTransmittedRay(Ray incidentRay, boolean reportToConsole)
	throws RayTraceException
	{
		Ray currentRay = incidentRay;
		
		if(reportToConsole) System.out.println("DirectionChangingSurfaceSequence::calculateTransmittedRay: incident ray = "+incidentRay);

		// go through all the scene objects
		for(SceneObjectPrimitive s:sceneObjectPrimitivesWithDirectionChangingSurfaces)
		{
			// find the (first) intersection between the current ray and the current scene object, s
			RaySceneObjectIntersection i = s.getClosestRayIntersection(currentRay);
			
			// there *must* be an intersection
			if(i == RaySceneObjectIntersection.NO_INTERSECTION) throw new RayTraceException("No intersection found between current ray and scene object \""+s.getDescription()+"\".");

			if(reportToConsole)  System.out.println("DirectionChangingSurfaceSequence::calculateTransmittedRay: intersection point = "+i.p);

			// there is an intersection; calculate the ray after transmission through s		
			DirectionChanging ds = (DirectionChanging)(s.getSurfaceProperty());
			
			// calculate the light-ray direction after transmission through s...
			Vector3D dPrime = ds.getOutgoingLightRayDirection(currentRay, i, s, null, 100, null);

			if(reportToConsole)  System.out.println("DirectionChangingSurfaceSequence::calculateTransmittedRay: new ray direction = "+dPrime);

			// ... and construct a new ray from this
			currentRay = currentRay.getBranchRay(i.p, dPrime, i.t, false);
			
			if(reportToConsole) System.out.println("DirectionChangingSurfaceSequence::calculateTransmittedRay: ray after transmission through "+s.getDescription()+" = "+currentRay);
		}
		
		return currentRay;
	}
	
	public Ray calculateTransmittedRay(Ray incidentRay)
	throws RayTraceException
	{
		return calculateTransmittedRay(incidentRay, false);
	}

}
