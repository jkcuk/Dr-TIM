package optics.raytrace.sceneObjects;

import java.util.Vector;

import math.Vector3D;
import optics.raytrace.GUI.sceneObjects.EditableScaledParametrisedSphere;
import optics.raytrace.core.DefaultRaytraceExceptionHandler;
import optics.raytrace.core.RaySceneObjectIntersection;
import optics.raytrace.core.Ray;
import optics.raytrace.core.RayWithTrajectory;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.SceneObjectPrimitive;
import optics.raytrace.core.Studio;
import optics.raytrace.core.SurfaceProperty;
import optics.raytrace.exceptions.EvanescentException;
import optics.raytrace.exceptions.RayTraceException;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.utility.CopyModeType;

/**
 * A ray trajectory
 * 
 */
public class RayTrajectory extends SceneObjectContainer
{
	private static final long serialVersionUID = -8102022259326767944L;

	private Vector3D startPoint, startDirection;
	private double startTime;
	private double rayRadius;
	private SurfaceProperty surfaceProperty;
	private int maxTraceLevel;
	private boolean reportToConsole;
	/**
	 * if true, place spheres ("dots") along trajectory;
	 * if false, place spheres at intersection points and cylinders in between
	 */
//	private boolean dotted = false;
//	private double dotSeparation;
		
	/**
	 * creates a ray trajectory
	 * 
	 * @param description
	 * @param startPoint
	 * @param startDirection
	 * @param startTime
	 * @param rayRadius
	 * @param surfaceProperty	any surface properties
	 * @param maxTraceLevel
	 * @param reportToConsole
	 */
	public RayTrajectory(
			String description,
			Vector3D startPoint,
			double startTime,
			Vector3D startDirection,
			double rayRadius,
			SurfaceProperty surfaceProperty,
			int maxTraceLevel,
			boolean reportToConsole,
			SceneObject parent,
			Studio studio
		)
	{
		super(description, parent, studio);
		setStartPoint(startPoint);
		setStartTime(startTime);
		setStartDirection(startDirection);
		setRayRadius(rayRadius);
		setSurfaceProperty(surfaceProperty);
		setMaxTraceLevel(maxTraceLevel);
		setReportToConsole(reportToConsole);
		// setDotted(false, 20*rayRadius);
	}
	
	public RayTrajectory(RayTrajectory original)
	{
		super(original, CopyModeType.CLONE_DATA);
		this.startPoint = original.getStartPoint().clone();
		this.startDirection = original.getStartDirection().clone();
		this.startTime = original.getStartTime();
		this.rayRadius = original.getRayRadius();
		this.surfaceProperty = original.getSurfaceProperty();
		this.maxTraceLevel = original.getMaxTraceLevel();
		this.reportToConsole = original.isReportToConsole();
		// setDotted(original.isDotted(), original.getDotSeparation());
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.sceneObjects.SceneObject#clone()
	 */
	@Override
	public RayTrajectory clone()
	{
		return new RayTrajectory(this);
	}

	public Vector3D getStartPoint() {
		return startPoint;
	}

	public void setStartPoint(Vector3D startPoint) {
		this.startPoint = startPoint;
	}

	public Vector3D getStartDirection() {
		return startDirection;
	}

	public void setStartDirection(Vector3D startDirection) {
		this.startDirection = startDirection;
	}

	public double getStartTime() {
		return startTime;
	}

	public void setStartTime(double startTime) {
		this.startTime = startTime;
	}

	public double getRayRadius() {
		return rayRadius;
	}

	public void setRayRadius(double rayRadius) {
		this.rayRadius = rayRadius;
	}
	
	public int getMaxTraceLevel() {
		return maxTraceLevel;
	}

	public void setMaxTraceLevel(int maxTraceLevel) {
		this.maxTraceLevel = maxTraceLevel;
	}

	public boolean isReportToConsole() {
		return reportToConsole;
	}

	public void setReportToConsole(boolean reportToConsole) {
		this.reportToConsole = reportToConsole;
	}

	public SurfaceProperty getSurfaceProperty() {
		return surfaceProperty;
	}

	public void setSurfaceProperty(SurfaceProperty surfaceProperty) {
		this.surfaceProperty = surfaceProperty;
	}
	
//	public void setDotted(boolean dotted, double dotSeparation)
//	{
//		this.dotted = dotted;
//		this.dotSeparation = dotSeparation;
//	}
//	
//	public boolean isDotted()
//	{
//		return dotted;
//	}
//	
//	public double getDotSeparation()
//	{
//		return dotSeparation;
//	}

	protected void trajectory2SceneObjects(RayWithTrajectory ray)
	{
		// now turn the trajectory into scene objects; first, the trajectory of the primary ray
		Vector<Vector3D> intersectionPoints = ray.getIntersectionPoints();
		
//		if(dotted)
//		{
//			double cumulativeDistance = 0;
//			
//			addSceneObject(new Sphere(
//					"first dot",
//					intersectionPoints.get(0),	// centre
//					rayRadius,
//					getSurfaceProperty(),
//					this,
//					getStudio()
//					));
//
//			double latestSpherePlacedAtDistance = 0;
//			
//			// place spheres
//			for(int i=1; i<intersectionPoints.size(); i++)
//			{
//				double segmentLength = Vector3D.getDistance(intersectionPoints.get(i), intersectionPoints.get(i-1));
//				
//				for(double distance = latestSpherePlacedAtDistance + dotSeparation - cumulativeDistance;
//						distance <= segmentLength;
//						distance += dotSeparation)
//				{
//					// create a sphere at the intersection point
//					addSceneObject(new Sphere(
//							"dot between intersections #"+ (i-1) + "and #"+i,
//							Vector3D.sum(
//									intersectionPoints.get(i-1),
//									Vector3D.difference(intersectionPoints.get(i), intersectionPoints.get(i-1)).getWithLength(distance)
//							),	// centre
//							rayRadius,
//							getSurfaceProperty(),
//							this,
//							getStudio()
//							));
//	
//					//
//					latestSpherePlacedAtDistance += dotSeparation;
//				}
//				
//				//
//				cumulativeDistance += segmentLength;				
//			}
//		}
//		else
//		{
			for(int i=0; i<intersectionPoints.size(); i++)
			{
				// create a sphere at the intersection point
				SceneObjectPrimitive s = new EditableScaledParametrisedSphere(
						"intersection #"+i,
						intersectionPoints.get(i),	// centre
						rayRadius,
						getSurfaceProperty(),
						this,
						getStudio()
						);

				// add the sphere to this container
				addSceneObject(s);
			}

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
//		}

		// then the trajectories of the secondary rays
		Vector<RayWithTrajectory> secondaryRays = ray.getBranchRays();
		
		for(int i=0; i<secondaryRays.size(); i++)
		{
			trajectory2SceneObjects(secondaryRays.get(i));
		}
	}
	
	public void calculateTrajectory(SceneObject scene)
	{
		// first, get rid of any scene objects already in this SceneObjectContainer
		clear();
		
		// create ray that keeps track of its trajectory when it's being traced...
		RayWithTrajectory ray = new RayWithTrajectory(startPoint, startDirection, startTime, reportToConsole);

		// ... and trace it through the scene
		try {
			System.out.println("in (ray="+ray+") ...");
			scene.getColour(ray, null, scene, maxTraceLevel, new DefaultRaytraceExceptionHandler());
			System.out.println("...out!");
		}
		catch (RayTraceException e)
		{
			if(e instanceof EvanescentException)
			{	// don't do anything -- evanescent waves are normal!
			}
			else
				e.printStackTrace();
		}
		
		// now turn the trajectory into scene objects
		// now add the scene objects for the new trajectory
		trajectory2SceneObjects(ray);
	}

	/*
	 * Override SceneObjectContainer method to check first if the ray is a RayWithTrajectory,
	 * i.e. another light ray, so that we can do whatever is necessary so that two light rays
	 * don't intersect.
	 * (Incidentally, we don't need to override SceneObjectContainer's getClosestRayIntersection
	 * method because all that does is call this method.)
	 */
	@Override
	public RaySceneObjectIntersection getClosestRayIntersectionAvoidingOrigin(Ray ray, SceneObjectPrimitive originObject)
	{
		// a ray trajectory doesn't intersect with a RayWithTrajectory
		// (otherwise ray trajectories would terminate on other ray trajectories)
		if(ray.isRayWithTrajectory())
			return RaySceneObjectIntersection.NO_INTERSECTION;
		
		return super.getClosestRayIntersectionAvoidingOrigin(ray, originObject);
	}

	@Override
	public String getType()
	{
		return "Ray trajectory";
	}
}
