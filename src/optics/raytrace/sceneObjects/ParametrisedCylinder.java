package optics.raytrace.sceneObjects;

import math.*;
import optics.raytrace.core.*;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.utility.CopyModeType;

/**
 * A cylinder with lids.
 * Made from the primitive scene objects CylinderTop and two Discs (the lids).
 * 
 * @see CylinderTop
 * @see Disc
 * 
 * @author Dean et al.
 *
 */
public class ParametrisedCylinder extends SceneObjectContainer
// implements ParametrisedSurface, AnisotropicBiaxialSurface, Serializable
{
	private static final long serialVersionUID = 1085523470099254285L;

	private Vector3D startPoint, endPoint;
	private double radius;
	SurfaceProperty surfaceProperty;
	private ParametrisedCylinderMantle parametrisedCylinderMantle;	// keep a reminder of which one of the scene objects is the cylinder mantle
	private ParametrisedDisc startCap, endCap;
	private boolean showEndCaps;

	public ParametrisedCylinder(
			String description,
			Vector3D startPoint,
			Vector3D endPoint,
			double radius,
			boolean showEndCaps,
			SurfaceProperty surfaceProperty,
			SceneObject parent,
			Studio studio
		)
	{
		super(description, parent, studio);
		
		setStartPoint(startPoint);
		setEndPoint(endPoint);
		setRadius(radius);
		setShowEndCaps(showEndCaps);
		setSurfaceProperty(surfaceProperty);

		addSceneObjects();
	}
	
	public ParametrisedCylinder(
			String description,
			Vector3D startPoint,
			Vector3D endPoint,
			double radius,
			SurfaceProperty surfaceProperty,
			SceneObject parent,
			Studio studio
		)
	{
		this(description, startPoint, endPoint, radius, true, surfaceProperty, parent, studio);
	}

	
	/**
	 * Create a clone of the original
	 * @param original
	 */
	public ParametrisedCylinder(ParametrisedCylinder original)
	{
		super(original, CopyModeType.CLONE_DATA);
		
		setStartPoint(original.getStartPoint().clone());
		setEndPoint(original.getEndPoint().clone());
		setRadius(original.getRadius());
		setShowEndCaps(original.isShowEndCaps());
		setSurfaceProperty(original.getSurfaceProperty().clone());
		
		parametrisedCylinderMantle = (ParametrisedCylinderMantle)getSceneObject(0);	// hope this object actually is the parametrised cylinder mantle
	}
	
	/* (non-Javadoc)
	 * @see optics.raytrace.sceneObjects.SceneObjectContainer#clone()
	 */
	@Override
	public ParametrisedCylinder clone()
	{
		return new ParametrisedCylinder(this);
	}
	
	public void addSceneObjects()
	{
		// first remove anything that's already in this SceneObjectContainer, ...
		clear();
		
		// ... and then create and add the fresh objects
		parametrisedCylinderMantle = new ParametrisedCylinderMantle("mantle", getStartPoint(), getEndPoint(), getRadius(), getSurfaceProperty(), getParent(), getStudio());
		Vector3D axis = parametrisedCylinderMantle.getAxis();
		startCap = new ParametrisedDisc("start cap", getStartPoint(), axis.getProductWith(-1), getRadius(), getSurfaceProperty(), this, getStudio());
		endCap = new ParametrisedDisc("end cap", getEndPoint(), axis, getRadius(), getSurfaceProperty(), this, getStudio());
	
		addSceneObject(parametrisedCylinderMantle);
		addSceneObject(startCap, showEndCaps);
		addSceneObject(endCap, showEndCaps);
	}


	public Vector3D getStartPoint() {
		return startPoint;
	}

	public void setStartPoint(Vector3D startPoint) {
		this.startPoint = startPoint;
	}

	public Vector3D getEndPoint() {
		return endPoint;
	}

	public void setEndPoint(Vector3D endPoint) {
		this.endPoint = endPoint;
	}
	
	public Vector3D getNormalisedAxisDirection()
	{
		return Vector3D.difference(endPoint, startPoint).getNormalised();
	}

	public double getRadius() {
		return radius;
	}

	public void setRadius(double radius) {
		this.radius = radius;
	}

	public boolean isShowEndCaps() {
		return showEndCaps;
	}

	public void setShowEndCaps(boolean showEndCaps) {
		this.showEndCaps = showEndCaps;
	}

	public SurfaceProperty getSurfaceProperty() {
		return surfaceProperty;
	}

	public void setSurfaceProperty(SurfaceProperty surfaceProperty) {
		this.surfaceProperty = surfaceProperty;
	}

	public ParametrisedCylinderMantle getParametrisedCylinderMantle() {
		return parametrisedCylinderMantle;
	}

	public void setParametrisedCylinderMantle(
			ParametrisedCylinderMantle parametrisedCylinderMantle) {
		this.parametrisedCylinderMantle = parametrisedCylinderMantle;
	}

	@Override
	public String toString() {
		return "<ParametrisedCylinder, description = " + description + ", startPoint = " + startPoint + ", endPoint = " + endPoint + ", radius = " + radius + ", showEndCaps = " + showEndCaps + ">";
	}
	
	@Override
	public String getType()
	{
		return "Cylinder";
	}

//	@Override
//	public Vector2D getParametersForSurfacePoint(Vector3D p) {
//		return parametrisedCylinderMantle.getParametersForSurfacePoint(p);
//	}
//
//	@Override
//	public ArrayList<Vector3D> getDirectionsForSurfacePoint(Vector3D p) {
//		return parametrisedCylinderMantle.getDirectionsForSurfacePoint(p);		
//	}
}
