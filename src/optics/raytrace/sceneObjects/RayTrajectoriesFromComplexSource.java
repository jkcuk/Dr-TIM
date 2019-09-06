package optics.raytrace.sceneObjects;

import math.Vector3D;
import optics.raytrace.GUI.sceneObjects.EditableRayTrajectory;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.Studio;
import optics.raytrace.core.SurfaceProperty;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.utility.CopyModeType;

/**
 * A hyperboloid of ray trajectories from a complex source
 * 
 */
public class RayTrajectoriesFromComplexSource extends SceneObjectContainer
{
	private static final long serialVersionUID = 7368161335284470955L;

	private Vector3D startPoint, axisDirection;	// axis direction has to be normalised!
	private double startTime, coneAngle;
	private int numberOfRays;
	private double rayRadius;
	private SurfaceProperty surfaceProperty;
	private int maxTraceLevel;

		
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
	public RayTrajectoriesFromComplexSource(String description, Vector3D startPoint, double startTime, Vector3D axisDirection, double coneAngle, int numberOfRays, double rayRadius, SurfaceProperty surfaceProperty, int maxTraceLevel, SceneObject parent, Studio studio)
	{
		super(description, parent, studio);
		
		this.startPoint = startPoint;
		this.startTime = startTime;
		this.axisDirection = axisDirection.getNormalised();
		this.coneAngle = coneAngle;
		this.numberOfRays = numberOfRays;
		this.rayRadius = rayRadius;
		this.surfaceProperty = surfaceProperty;
		this.maxTraceLevel = maxTraceLevel;
		
		addRayTrajectories();
	}
	
	public RayTrajectoriesFromComplexSource(RayTrajectoriesFromComplexSource original)
	{
		super(original, CopyModeType.CLONE_DATA);
		this.startPoint = original.getStartPoint().clone();
		this.startTime = original.getStartTime();
		this.axisDirection = original.getAxisDirection().clone();
		this.coneAngle = original.getConeAngle();
		this.numberOfRays = original.getNumberOfRays();
		this.rayRadius = original.getRayRadius();
		this.surfaceProperty = original.getSurfaceProperty();
		this.maxTraceLevel = original.getMaxTraceLevel();

		addRayTrajectories();
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.sceneObjects.SceneObject#clone()
	 */
	@Override
	public RayTrajectoriesFromComplexSource clone()
	{
		return new RayTrajectoriesFromComplexSource(this);
	}

	public Vector3D getStartPoint() {
		return startPoint;
	}

	public void setStartPoint(Vector3D startPoint) {
		this.startPoint = startPoint;
	}

	public double getStartTime() {
		return startTime;
	}

	public void setStartTime(double startTime) {
		this.startTime = startTime;
	}

	public Vector3D getAxisDirection() {
		return axisDirection;
	}

	public void setAxisDirection(Vector3D axisDirection) {
		this.axisDirection = axisDirection.getNormalised();
	}

	public double getConeAngle() {
		return coneAngle;
	}

	public void setConeAngle(double coneAngle) {
		this.coneAngle = coneAngle;
	}
	
	public int getNumberOfRays() {
		return numberOfRays;
	}

	public void setNumberOfRays(int numberOfRays) {
		this.numberOfRays = numberOfRays;
	}

	public double getRayRadius() {
		return rayRadius;
	}

	public void setRayRadius(double rayRadius) {
		this.rayRadius = rayRadius;
	}

	public SurfaceProperty getSurfaceProperty() {
		return surfaceProperty;
	}

	public void setSurfaceProperty(SurfaceProperty surfaceProperty) {
		this.surfaceProperty = surfaceProperty;
	}

	public int getMaxTraceLevel() {
		return maxTraceLevel;
	}

	public void setMaxTraceLevel(int maxTraceLevel) {
		this.maxTraceLevel = maxTraceLevel;
	}
	
	public void addRayTrajectories()
	{
		// get rid of anything already in this SceneObjectContainer
		clear();
		
		Double sin = Math.sin(coneAngle);

		// Three vectors which form an orthogonal system.
		// a points in the direction of the axis and is of length cos(coneAngle);
		// u and v are both of length sin(coneAngle).
		Vector3D
		a = axisDirection.getWithLength(Math.cos(coneAngle)),
		u = Vector3D.getANormal(axisDirection).getWithLength(sin),
		v = Vector3D.crossProduct(axisDirection, u).getWithLength(sin);
		
		for(int i=0; i<numberOfRays; i++)
		{
			Double phi = 2*Math.PI*i/numberOfRays;
			addSceneObject(
					new EditableRayTrajectory(
							"trajectory of ray #" + i,
							startPoint,
							startTime,
							Vector3D.sum(
									a,
									u.getProductWith(Math.cos(phi)),
									v.getProductWith(Math.sin(phi))
								),
							rayRadius,
							surfaceProperty,
							maxTraceLevel,
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
		return "Ray trajectories (complex source)";
	}
}
