package optics.raytrace.sceneObjects;

import math.Complex;
import math.Vector3D;

import optics.raytrace.GUI.sceneObjects.EditableScaledParametrisedSphere;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.Studio;
import optics.raytrace.core.SurfaceProperty;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectIntersectionSimple;
import optics.raytrace.surfaces.Refractive;
import optics.raytrace.surfaces.RefractiveComplex;

/**
 * A lens.
 * @author Johannes Courtial
 */
public class SphericalLens extends SceneObjectIntersectionSimple
{
	private static final long serialVersionUID = 3814025657030660127L;

	// parameters
	private double apertureRadius, radiusOfCurvatureFront, radiusOfCurvatureBack;
	private Vector3D centre, directionToFront;
	private SurfaceProperty surfacePropertyFront, surfacePropertyBack;
	private EditableScaledParametrisedSphere sphereFront, sphereBack;

	/**
	 * Default constructor
	 * 
	 * @param description
	 * @param apertureRadius
	 * @param radiusOfCurvatureFront
	 * @param radiusOfCurvatureBack
	 * @param centre
	 * @param directionToFront
	 * @param surfacePropertyFront
	 * @param surfacePropertyBack
	 * @param parent
	 * @param studio
	 */
	public SphericalLens(
			String description,
			double apertureRadius,
			double radiusOfCurvatureFront,
			double radiusOfCurvatureBack,
			Vector3D centre,
			Vector3D directionToFront,
			SurfaceProperty surfacePropertyFront,
			SurfaceProperty surfacePropertyBack,
			SceneObject parent, 
			Studio studio
	)
	{
		// constructor of superclass
		super(description, parent, studio);
		
		// copy the parameters into this instance's variables
		this.apertureRadius = apertureRadius;
		this.radiusOfCurvatureFront = radiusOfCurvatureFront;
		this.radiusOfCurvatureBack = radiusOfCurvatureBack;
		this.centre = centre;
		this.directionToFront = directionToFront;
		this.surfacePropertyFront = surfacePropertyFront;
		this.surfacePropertyBack = surfacePropertyBack;

		addElements();
	}

	/**
	 * Constructor that uses (real) focal length and (real) refractive index as parameters
	 * 
	 * @param description
	 * @param apertureRadius
	 * @param focalLength
	 * @param refractiveIndex
	 * @param centre
	 * @param directionToFront
	 * @param parent
	 * @param studio
	 */
	public SphericalLens(
			String description,
			double apertureRadius,
			double focalLength,
			double refractiveIndex,
			Vector3D centre,
			Vector3D directionToFront,
			SceneObject parent, 
			Studio studio
	)
	{
		// constructor of superclass
		super(description, parent, studio);
		
		// copy the parameters into this instance's variables
		this.apertureRadius = apertureRadius;
		this.radiusOfCurvatureFront = 2*focalLength*(refractiveIndex - 1);
		this.radiusOfCurvatureBack = 2*focalLength*(refractiveIndex - 1);
		this.centre = centre;
		this.directionToFront = directionToFront;
		SurfaceProperty surface = new Refractive(refractiveIndex, 1, true);
		this.surfacePropertyFront = surface;
		this.surfacePropertyBack = surface;

		addElements();
	}

	/**
	 * Constructor that uses complex focal length and mod(refractive index) as parameters
	 *
	 * @param description
	 * @param apertureRadius
	 * @param focalLength
	 * @param modN
	 * @param centre
	 * @param directionToFront
	 * @param parent
	 * @param studio
	 */
	public SphericalLens(
			String description,
			double apertureRadius,
			Complex focalLength,
			double modN,	// modulus
			Vector3D centre,
			Vector3D directionToFront,
			SceneObject parent, 
			Studio studio
	)
	{
		// constructor of superclass
		super(description, parent, studio);
		
		// copy the parameters into this instance's variables
		this.apertureRadius = apertureRadius;
		double cosPhi = Math.cos(-focalLength.getArg());
		double modNMinus1 = -cosPhi + Math.sqrt(cosPhi*cosPhi-1+modN*modN);
		Complex refractiveIndex = Complex.sum(
				Complex.fromPolar(modNMinus1, -focalLength.getArg()),
				new Complex(1,0)
			);
		this.radiusOfCurvatureFront = 2*focalLength.getMod()*modNMinus1;
		this.radiusOfCurvatureBack = 2*focalLength.getMod()*modNMinus1;
		System.out.println("lens \""+description+"\": n="+refractiveIndex+", R="+radiusOfCurvatureFront);
		this.centre = centre;
		this.directionToFront = directionToFront;
		SurfaceProperty surface = new RefractiveComplex(refractiveIndex, 1, true);
		this.surfacePropertyFront = surface;
		this.surfacePropertyBack = surface;

		addElements();
	}

	/**
	 * Create a clone of original
	 * @param original
	 */
	public SphericalLens(SphericalLens original)
	{
		super(original);
		
		// copy the original's parameters
		this.apertureRadius = original.getApertureRadius();
		this.radiusOfCurvatureFront = original.getRadiusOfCurvatureFront();
		this.radiusOfCurvatureBack = original.getRadiusOfCurvatureBack();
		this.centre = original.getCentre().clone();
		this.directionToFront = original.getDirectionToFront().clone();
		this.surfacePropertyFront = original.getSurfacePropertyFront();
		this.surfacePropertyBack = original.getSurfacePropertyBack();
		
		addElements();
	}

	@Override
	public SphericalLens clone()
	{
		return new SphericalLens(this);
	}
	
	public double getApertureRadius() {
		return apertureRadius;
	}

	public void setApertureRadius(double apertureRadius) {
		this.apertureRadius = apertureRadius;
	}

	public double getRadiusOfCurvatureFront() {
		return radiusOfCurvatureFront;
	}

	public void setRadiusOfCurvatureFront(double radiusOfCurvatureFront) {
		this.radiusOfCurvatureFront = radiusOfCurvatureFront;
	}

	public double getRadiusOfCurvatureBack() {
		return radiusOfCurvatureBack;
	}

	public void setRadiusOfCurvatureBack(double radiusOfCurvatureBack) {
		this.radiusOfCurvatureBack = radiusOfCurvatureBack;
	}

	public Vector3D getCentre() {
		return centre;
	}

	public void setCentre(Vector3D centre) {
		this.centre = centre;
	}

	public Vector3D getDirectionToFront() {
		return directionToFront;
	}

	public void setDirectionToFront(Vector3D directionToFront) {
		this.directionToFront = directionToFront;
	}

	public SurfaceProperty getSurfacePropertyFront()
	{
		return surfacePropertyFront;
	}
	
	public void setSurfacePropertyFront(SurfaceProperty surfacePropertyFront)
	{
		this.surfacePropertyFront = surfacePropertyFront;
		sphereFront.setSurfaceProperty(surfacePropertyFront);
	}

	public SurfaceProperty getSurfacePropertyBack()
	{
		return surfacePropertyBack;
	}
	
	public void setSurfacePropertyBack(SurfaceProperty surfacePropertyBack)
	{
		this.surfacePropertyBack = surfacePropertyBack;
		sphereBack.setSurfaceProperty(surfacePropertyBack);
	}

	private void addElements()
	{
		sphereFront = new EditableScaledParametrisedSphere("front surface",
			centre.getSumWith(
				directionToFront.getWithLength(
					-Math.sqrt(
						radiusOfCurvatureFront*radiusOfCurvatureFront
						-apertureRadius * apertureRadius
					)
				)
			),	// centre of sphere
			radiusOfCurvatureFront,
			surfacePropertyFront,
			this,
			getStudio()
		);
		// make the direction to the front the direction to the pole
		sphereFront.setDirections(directionToFront);
		addSceneObject(sphereFront);
		
		sphereBack = new EditableScaledParametrisedSphere("back surface",
			centre.getSumWith(
				directionToFront.getWithLength(
					Math.sqrt(
						radiusOfCurvatureBack*radiusOfCurvatureBack
						-apertureRadius * apertureRadius
					)
				)
			),	// centre of sphere	
			radiusOfCurvatureBack,
			surfacePropertyBack,
			this,
			getStudio()
		);
		sphereBack.setDirections(directionToFront);
		addSceneObject(sphereBack);
	}
	
	@Override
	public String getType()
	{
		return "Spherical lens";
	}
}
