package optics.raytrace.sceneObjects;

import math.Vector3D;
import optics.raytrace.GUI.sceneObjects.EditableRayTrajectory;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.Studio;
import optics.raytrace.core.SurfaceProperty;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.utility.CopyModeType;

/**
 * A ray trajectory
 * 
 */
public class RayTrajectoryCone extends SceneObjectContainer
{
	private static final long serialVersionUID = 7368161335284470955L;

	private Vector3D coneApex, axisDirection;	// axis direction has to be normalised!
	private double startTime, coneAngle, startDistanceFromConeApex;
	private int numberOfRays;
	private double rayRadius;
	private SurfaceProperty surfaceProperty;
	private int maxTraceLevel;

		
	/**
	 * creates a cone of ray trajectories
	 * 
	 * @param description
	 * @param cone apex
	 * @param startDistanceFromConeApex
	 * @param startTime
	 * @param axisDirection
	 * @param coneAngle
	 * @param numberOfRays
	 * @param rayRadius
	 * @param surfaceProperty	any surface properties
	 * @param maxTraceLevel
	 */
	public RayTrajectoryCone(String description, Vector3D coneApex, double startDistanceFromConeApex, double startTime, Vector3D axisDirection, double coneAngle, int numberOfRays, double rayRadius, SurfaceProperty surfaceProperty, int maxTraceLevel, SceneObject parent, Studio studio)
	{
		super(description, parent, studio);
		
		this.coneApex = coneApex;
		this.startDistanceFromConeApex = startDistanceFromConeApex;
		this.startTime = startTime;
		this.axisDirection = axisDirection.getNormalised();
		this.coneAngle = coneAngle;
		this.numberOfRays = numberOfRays;
		this.rayRadius = rayRadius;
		this.surfaceProperty = surfaceProperty;
		this.maxTraceLevel = maxTraceLevel;
		
		addRayTrajectories();
	}
	
	/**
	 * creates a cone of ray trajectories that start at the cone apex
	 * 
	 * @param description
	 * @param cone apex
	 * @param startTime
	 * @param axisDirection
	 * @param coneAngle
	 * @param numberOfRays
	 * @param rayRadius
	 * @param surfaceProperty	any surface properties
	 * @param maxTraceLevel
	 */
	public RayTrajectoryCone(String description, Vector3D coneApex, double startTime, Vector3D axisDirection, double coneAngle, int numberOfRays, double rayRadius, SurfaceProperty surfaceProperty, int maxTraceLevel, SceneObject parent, Studio studio)
	{
		this(
				description,
				coneApex, 
				0,	// startDistanceFromConeApex
				startTime,
				axisDirection,
				coneAngle,
				numberOfRays,
				rayRadius,
				surfaceProperty,
				maxTraceLevel,
				parent,
				studio
			);
	}
	
	public RayTrajectoryCone(RayTrajectoryCone original)
	{
		super(original, CopyModeType.CLONE_DATA);
		this.coneApex = original.getConeApex().clone();
		this.startDistanceFromConeApex = original.getStartDistanceFromConeApex();
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
	public RayTrajectoryCone clone()
	{
		return new RayTrajectoryCone(this);
	}

	public Vector3D getConeApex() {
		return coneApex;
	}

	public void setConeApex(Vector3D coneApex) {
		this.coneApex = coneApex;
	}

	public double getStartDistanceFromConeApex() {
		return startDistanceFromConeApex;
	}

	public void setStartDistanceFromConeApex(double startDistanceFromConeApex) {
		this.startDistanceFromConeApex = startDistanceFromConeApex;
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
			
			// light-ray direction
			Vector3D rayDirection = Vector3D.sum(
					a,
					u.getProductWith(Math.cos(phi)),
					v.getProductWith(Math.sin(phi))
				);

			addSceneObject(
					new EditableRayTrajectory(
							"trajectory of ray #" + i,
							Vector3D.sum(
									coneApex,
									rayDirection.getWithLength(startDistanceFromConeApex)
								),
							startTime,
							rayDirection,
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
		return "Ray-trajectory cone";
	}
}
