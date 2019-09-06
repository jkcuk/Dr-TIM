package optics.raytrace.sceneObjects;

import math.Vector3D;

import optics.raytrace.GUI.sceneObjects.EditableParametrisedPlane;
import optics.raytrace.GUI.sceneObjects.EditableScaledParametrisedSphere;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.Studio;
import optics.raytrace.core.SurfaceProperty;
import optics.raytrace.core.Transformation;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectIntersection;
import optics.raytrace.surfaces.Transparent;

/**
 * A spherical cap (see http://en.wikipedia.org/wiki/Spherical_cap).
 * @author Johannes Courtial, Blair
 */
public class SphericalCap extends SceneObjectIntersection
{
	private static final long serialVersionUID = -5189699837637801956L;

	// parameters
	private Vector3D capCentre, sphereCentre;
	private double apertureRadius;
	private boolean closed;
	private SurfaceProperty surfaceProperty;
	private EditableScaledParametrisedSphere sphere;
	private EditableParametrisedPlane plane;

	/**
	 * Default constructor
	 * 
	 * @param description
	 * @param apertureRadius
	 * @param capCentre
	 * @param sphereCentre
	 * @param closed
	 * @param surfaceProperty
	 * @param parent
	 * @param studio
	 */
	public SphericalCap(
			String description,
			Vector3D capCentre,
			Vector3D sphereCentre,
			double apertureRadius,
			boolean closed,
			SurfaceProperty surfaceProperty,
			SceneObject parent, 
			Studio studio
	)
	{
		// constructor of superclass
		super(description, parent, studio);
		
		setApertureRadius(apertureRadius);
		setCapCentre(capCentre);
		setSphereCentre(sphereCentre);
		setClosed(closed);
		setSurfaceProperty(surfaceProperty);

		addElements();
	}


	/**
	 * Create a clone of original
	 * @param original
	 */
	public SphericalCap(SphericalCap original)
	{
		super(original);
		
		setApertureRadius(original.getApertureRadius());
		setCapCentre(original.getCapCentre());
		setSphereCentre(original.getSphereCentre());
		setClosed(original.isClosed());
		setSurfaceProperty(original.getSurfaceProperty());
		
		addElements();
	}

	@Override
	public SphericalCap clone()
	{
		return new SphericalCap(this);
	}

	// setters and getters

	public double getApertureRadius() {
		return apertureRadius;
	}

	public void setApertureRadius(double apertureRadius) {
		this.apertureRadius = apertureRadius;
	}

	public Vector3D getCapCentre() {
		return capCentre;
	}

	public void setCapCentre(Vector3D capCentre) {
		this.capCentre = capCentre;
	}

	public Vector3D getSphereCentre() {
		return sphereCentre;
	}

	public void setSphereCentre(Vector3D sphereCentre) {
		this.sphereCentre = sphereCentre;
	}

	public boolean isClosed() {
		return closed;
	}

	public void setClosed(boolean closed) {
		this.closed = closed;
	}

	public SurfaceProperty getSurfaceProperty() {
		return surfaceProperty;
	}

	public void setSurfaceProperty(SurfaceProperty surfaceProperty) {
		this.surfaceProperty = surfaceProperty;
	}

	public void addElements()
	{
		Vector3D
			centre2cap = Vector3D.difference(getCapCentre(), getSphereCentre());
		
		double
			R = centre2cap.getLength(),
			r = getApertureRadius();
		
		Vector3D
			n = centre2cap.getNormalised(),
			v1 = Vector3D.getANormal(n),	// one of the vectors perpendicular to n
			v2 = Vector3D.crossProduct(v1, n);	// another vector perpendicular to n

		// get rid of anything that's in this SceneObjectContainer at the moment...
		clear();

		sphere = new EditableScaledParametrisedSphere(
				"sphere",	// description,
				getSphereCentre(),	// centre
				R,	// radius
				n,	// pole,
				v1,	// phi0Direction,
				0, Math.PI, -Math.PI, Math.PI,	// theta_min, theta_max, phi_min, phi_max
				getSurfaceProperty(),	// surfaceProperty,
				this,	// parent, 
				getStudio()
			);
		addSceneObject(sphere);
		
		plane = new EditableParametrisedPlane(
				"plane",	// description,
				Vector3D.sum(
						getCapCentre(),
						n.getProductWith(Math.sqrt(R*R - r*r)-R)
					),	// pointOnPlane,
				v1,	// vector 1 spanning plane
				v2,	// vector 2 spanning plane
				// isClosed(),	// shadow-throwing if there is a "lid" on the cap, i.e. if it's closed
				(isClosed()?getSurfaceProperty():Transparent.PERFECT),	// surface property,
				this,	// parent,
				getStudio()
			);
		addSceneObject(plane);
	}
	
	@Override
	public SphericalCap transform(Transformation t)
	{
		return new SphericalCap(
				getDescription(),
				t.transformPosition(getCapCentre()),
				t.transformPosition(getSphereCentre()),
				getApertureRadius(),
				isClosed(),
				getSurfaceProperty(),
				getParent(), 
				getStudio()
			);
	}
	
	@Override
	public String getType()
	{
		return "Spherical cap";
	}
}
