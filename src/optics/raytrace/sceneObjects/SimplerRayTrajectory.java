package optics.raytrace.sceneObjects;

import java.util.Vector;

import math.Vector3D;
import optics.raytrace.core.RayWithTrajectory;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.SceneObjectPrimitive;
import optics.raytrace.core.Studio;
import optics.raytrace.core.SurfaceProperty;

/**
 * A ray trajectory that doesn't add spheres at the start point, end point and at intermediate points.
 */
public class SimplerRayTrajectory extends RayTrajectory
{
	private static final long serialVersionUID = 5562651140481299255L;

	/**
	 * creates a simpler ray trajectory
	 * 
	 * @param description
	 * @param startPoint
	 * @param startDirection
	 * @param rayRadius
	 * @param surfaceProperty	any surface properties
	 * @param maxTraceLevel
	 * @param reportToConsole
	 */
	public SimplerRayTrajectory(String description, Vector3D startPoint, double startTime, Vector3D startDirection, double rayRadius, SurfaceProperty surfaceProperty, int maxTraceLevel, boolean reportToConsole, SceneObject parent, Studio studio)
	{
		super(description, startPoint, startTime, startDirection, rayRadius, surfaceProperty, maxTraceLevel, reportToConsole, parent, studio);
	}
	
	public SimplerRayTrajectory(SimplerRayTrajectory original)
	{
		super(original);
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.sceneObjects.SceneObject#clone()
	 */
	@Override
	public SimplerRayTrajectory clone()
	{
		return new SimplerRayTrajectory(this);
	}

	@Override
	public void trajectory2SceneObjects(RayWithTrajectory ray)
	{
		// now turn the trajectory into scene objects; first, the trajectory of the primary ray
		Vector<Vector3D> intersectionPoints = ray.getIntersectionPoints();
		
		for(int i=1; i<intersectionPoints.size(); i++)
		{
			// create a cylinder linking the intersection points
			SceneObjectPrimitive c = new ParametrisedCylinderMantle(
					"piece #"+i,
					intersectionPoints.get(i-1),	// start point
					intersectionPoints.get(i),	// end point .getDifferenceWith(intersectionPoints.get(i-1)),
					getRayRadius(),
					getSurfaceProperty(),
					this,
					getStudio()
				);
			
			// add the cylinder to this container
			addSceneObject(c);
		}

		// then the trajectories of the secondary rays
		Vector<RayWithTrajectory> secondaryRays = ray.getBranchRays();
		
		for(int i=0; i<secondaryRays.size(); i++)
		{
			trajectory2SceneObjects(secondaryRays.get(i));
		}
	}
	
	@Override
	public String getType()
	{
		return "Ray trajectory";
	}
}
