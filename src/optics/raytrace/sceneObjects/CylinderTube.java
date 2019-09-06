package optics.raytrace.sceneObjects;

import java.util.ArrayList;

import math.Vector3D;
import optics.raytrace.GUI.sceneObjects.EditableScaledParametrisedSphere;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.SceneObjectPrimitive;
import optics.raytrace.core.Studio;
import optics.raytrace.core.SurfaceProperty;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;

/**
 * A series of cylinders, connected by spheres
 * 
 */
public class CylinderTube extends SceneObjectContainer
{
	private static final long serialVersionUID = -8102022259326767944L;

	private ArrayList<Vector3D> vertices;
	private double startTime;
	private double radius;
	private boolean spheresAtEnds;
	private SurfaceProperty surfaceProperty;
		
	/**
	 * creates a cylinder tube
	 * 
	 * @param description
	 * @param vertices
	 * @param startTime
	 * @param radius
	 * @param spheresAtEnds
	 * @param surfaceProperty	any surface properties
	 */
	public CylinderTube(
			String description,
			ArrayList<Vector3D> vertices,
			double startTime,
			double radius,
			boolean spheresAtEnds,
			SurfaceProperty surfaceProperty,
			SceneObject parent,
			Studio studio
		)
	{
		super(description, parent, studio);
		setVertices(vertices);
		setStartTime(startTime);
		setRadius(radius);
		setSpheresAtEnds(spheresAtEnds);
		setSurfaceProperty(surfaceProperty);
		
		populateSceneObjectContainer();
	}
	
	public CylinderTube(CylinderTube original)
	{
		this(original.getDescription(), original.getVertices(), original.getStartTime(), original.getRadius(), original.isSpheresAtEnds(), original.getSurfaceProperty(), original.getParent(), original.getStudio());
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.sceneObjects.SceneObject#clone()
	 */
	@Override
	public CylinderTube clone()
	{
		return new CylinderTube(this);
	}

	public ArrayList<Vector3D> getVertices() {
		return vertices;
	}

	public void setVertices(ArrayList<Vector3D> vertices) {
		this.vertices = vertices;
	}

	public double getStartTime() {
		return startTime;
	}

	public void setStartTime(double startTime) {
		this.startTime = startTime;
	}

	public double getRadius() {
		return radius;
	}

	public void setRadius(double radius) {
		this.radius = radius;
	}
	
	public boolean isSpheresAtEnds() {
		return spheresAtEnds;
	}

	public void setSpheresAtEnds(boolean spheresAtEnds) {
		this.spheresAtEnds = spheresAtEnds;
	}

	public SurfaceProperty getSurfaceProperty() {
		return surfaceProperty;
	}

	public void setSurfaceProperty(SurfaceProperty surfaceProperty) {
		this.surfaceProperty = surfaceProperty;
	}
	
	protected void populateSceneObjectContainer()
	{
		for(int i=(spheresAtEnds?0:1); i<vertices.size()-(spheresAtEnds?0:1); i++)
		{
			// create a sphere at the point
			SceneObjectPrimitive s = new EditableScaledParametrisedSphere(
					"point #"+i,
					vertices.get(i),	// centre
					radius,
					getSurfaceProperty(),
					this,
					getStudio()
					);

			// add the sphere to this container
			addSceneObject(s);
		}

		for(int i=1; i<vertices.size(); i++)
		{
			// create a cylinder linking the intersection vertices
			SceneObjectPrimitive c = new ParametrisedCylinderMantle(
					"cylinder #"+i,
					vertices.get(i-1),	// start point
					vertices.get(i),	// end point .getDifferenceWith(intersectionPoints.get(i-1)),
					getRadius(),
					getSurfaceProperty(),
					this,
					getStudio()
					);

			// add the cylinder to this container
			addSceneObject(c);
		}		
	}

	@Override
	public String getType()
	{
		return "Cylinder tube";
	}
}
