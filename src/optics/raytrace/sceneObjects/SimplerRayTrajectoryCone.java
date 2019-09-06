package optics.raytrace.sceneObjects;

import math.Vector3D;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.Studio;
import optics.raytrace.core.SurfaceProperty;

/**
 * A ray-trajectory cone made from simpler ray trajectories 
 */
public class SimplerRayTrajectoryCone extends RayTrajectoryCone
{
	private static final long serialVersionUID = -2181945095014084720L;

	/**
	 * creates a cone of ray trajectories
	 * 
	 * @param description
	 * @param startPoint
	 * @param startTime
	 * @param axisDirection
	 * @param coneAngle
	 * @param numberOfRays
	 * @param rayRadius
	 * @param surfaceProperty	any surface properties
	 * @param maxTraceLevel
	 */
	public SimplerRayTrajectoryCone(String description, Vector3D startPoint, double startTime, Vector3D axisDirection, double coneAngle, int numberOfRays, double rayRadius, SurfaceProperty surfaceProperty, int maxTraceLevel, SceneObject parent, Studio studio)
	{
		super(description, startPoint, startTime, axisDirection, coneAngle, numberOfRays, rayRadius, surfaceProperty, maxTraceLevel, parent, studio);
	}
	
	public SimplerRayTrajectoryCone(SimplerRayTrajectoryCone original)
	{
		super(original);
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.sceneObjects.SceneObject#clone()
	 */
	@Override
	public SimplerRayTrajectoryCone clone()
	{
		return new SimplerRayTrajectoryCone(this);
	}

	@Override
	public void addRayTrajectories()
	{
		// get rid of anything already in this SceneObjectContainer
		clear();
		
		Double sin = Math.sin(getConeAngle());

		// Three vectors which form an orthogonal system.
		// a points in the direction of the axis and is of length cos(coneAngle);
		// u and v are both of length sin(coneAngle).
		Vector3D
			a = getAxisDirection().getWithLength(Math.cos(getConeAngle())),
			u = Vector3D.getANormal(getAxisDirection()).getWithLength(sin),
			v = Vector3D.crossProduct(getAxisDirection(), u).getWithLength(sin);
		
		for(int i=0; i<getNumberOfRays(); i++)
		{
			Double phi = 2*Math.PI*i/getNumberOfRays();
			addSceneObject(
					new SimplerRayTrajectory(
							"trajectory of ray #" + i,
							getConeApex(),
							getStartTime(),
							Vector3D.sum(
									a,
									u.getProductWith(Math.cos(phi)),
									v.getProductWith(Math.sin(phi))
								),
							getRayRadius(),
							getSurfaceProperty(),
							getMaxTraceLevel(),
							false,	// reportToConsole
							this,	// parent
							getStudio()
						)
				);
		}
	}
	
	@Override
	public String getType()
	{
		return "Ray-trajectory cone";
	}
}
