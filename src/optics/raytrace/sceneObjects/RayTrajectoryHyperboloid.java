package optics.raytrace.sceneObjects;

import math.Vector3D;
import optics.raytrace.GUI.sceneObjects.EditableRayTrajectory;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.Studio;
import optics.raytrace.core.SurfaceProperty;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.utility.CopyModeType;

/**
 * A hyperboloid of ray trajectories
 * 
 */
public class RayTrajectoryHyperboloid extends SceneObjectContainer
{
	private static final long serialVersionUID = 3933523557333889589L;

	private Vector3D startPoint, axisDirection;	// axis direction has to be normalised!
	private double startTime, hyperboloidAngle, waistRadius;
	private int numberOfRays;
	private double rayRadius;
	private SurfaceProperty surfaceProperty;
	private int maxTraceLevel;

		
	/**
	 * creates a hyperboloid of ray trajectories
	 * 
	 * @param description
	 * @param startPoint
	 * @param startTime
	 * @param axisDirection
	 * @param hyperboloidAngle
	 * @param waistRadius
	 * @param numberOfRays
	 * @param rayRadius
	 * @param surfaceProperty	any surface properties
	 * @param maxTraceLevel
	 */
	public RayTrajectoryHyperboloid(String description, Vector3D startPoint, double startTime, Vector3D axisDirection, double hyperboloidAngle, double waistRadius, int numberOfRays, double rayRadius, SurfaceProperty surfaceProperty, int maxTraceLevel, SceneObject parent, Studio studio)
	{
		super(description, parent, studio);
		
		this.startPoint = startPoint;
		this.startTime = startTime;
		this.axisDirection = axisDirection.getNormalised();
		this.hyperboloidAngle = hyperboloidAngle;
		this.waistRadius = waistRadius;
		this.numberOfRays = numberOfRays;
		this.rayRadius = rayRadius;
		this.surfaceProperty = surfaceProperty;
		this.maxTraceLevel = maxTraceLevel;
		
		addRayTrajectories();
	}
	
	public RayTrajectoryHyperboloid(RayTrajectoryHyperboloid original)
	{
		super(original, CopyModeType.CLONE_DATA);
		this.startPoint = original.getStartPoint().clone();
		this.startTime = original.getStartTime();
		this.axisDirection = original.getAxisDirection().clone();
		this.hyperboloidAngle = original.getHyperboloidAngle();
		this.waistRadius = original.getWaistRadius();
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
	public RayTrajectoryHyperboloid clone()
	{
		return new RayTrajectoryHyperboloid(this);
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

	public double getHyperboloidAngle() {
		return hyperboloidAngle;
	}

	public void setHyperboloidAngle(double hyperboloidAngle) {
		this.hyperboloidAngle = hyperboloidAngle;
	}
	
	public double getWaistRadius() {
		return waistRadius;
	}
	
	public void setWaistRadius(double waistRadius) {
		this.waistRadius = waistRadius;
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
		
		double sin = Math.sin(hyperboloidAngle);

		// Three vectors which form an orthogonal system.
		// a points in the direction of the axis and is of length cos(coneAngle);
		// u and v are both of length sin(coneAngle).
		Vector3D
		a = axisDirection.getWithLength(Math.cos(hyperboloidAngle)),
		u = Vector3D.getANormal(axisDirection).getNormalised(),
		v = Vector3D.crossProduct(axisDirection, u).getNormalised();
		
		for(int i=0; i<numberOfRays; i++)
		{
			double
				phi = 2*Math.PI*i/numberOfRays,
				cosPhi = Math.cos(phi),
				sinPhi = Math.sin(phi);
			addSceneObject(
					new EditableRayTrajectory(
							"trajectory of ray #" + i,
							Vector3D.sum(
									startPoint,
									u.getProductWith(waistRadius*cosPhi),
									v.getProductWith(waistRadius*sinPhi)
								),
							startTime,
							Vector3D.sum(
									a,
									u.getProductWith(-sin*sinPhi),
									v.getProductWith( sin*cosPhi)
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
		return "Ray-trajectory hyperboloid";
	}
}
