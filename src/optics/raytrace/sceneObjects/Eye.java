package optics.raytrace.sceneObjects;

import math.*;
import optics.raytrace.GUI.sceneObjects.EditableScaledParametrisedSphere;
import optics.raytrace.GUI.sceneObjects.EditableSceneObjectCollection;
import optics.raytrace.GUI.sceneObjects.EditableSphericalCap;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.Studio;
import optics.raytrace.core.Transformation;
import optics.raytrace.surfaces.SurfaceColour;

/**
 * An eye.
 * @author johannes
 */
public class Eye extends EditableSceneObjectCollection
{
	private static final long serialVersionUID = 5551232979727463173L;
	
	// parameters describing the eye
	private Vector3D centre, viewDirection;
	private double radius, irisRadius, pupilRadius;
	private SurfaceColour irisColour;
	
	// the scene objects
	private EditableScaledParametrisedSphere eyeball;
	private EditableSphericalCap iris, pupil;
	// private Disc iris, pupil;

	/**
	 * Default constructor.
	 * @param description
	 * @param centre
	 * @param viewDirection
	 * @param radius
	 * @param irisRadius
	 * @param pupilRadius
	 * @param irisColour
	 * @param parent
	 * @param studio
	 */
	public Eye(
			String description,
			Vector3D centre,
			Vector3D viewDirection,
			double radius,
			double irisRadius,
			double pupilRadius,
			SurfaceColour irisColour,
			SceneObject parent,
			Studio studio
		)
	{
		super(description, true, parent, studio);
		
		setCentre(centre);
		setViewDirection(viewDirection);
		setRadius(radius);
		setIrisRadius(irisRadius);
		setPupilRadius(pupilRadius);
		setIrisColour(irisColour);
		
		addElements();
	}
	
	/**
	 * Constructor that assumes standard ratios between eyeball radius, iris radius, and pupil radius.
	 * @param description
	 * @param centre
	 * @param viewDirection
	 * @param radius
	 * @param irisColour
	 * @param parent
	 * @param studio
	 */
	public Eye(
			String description,
			Vector3D centre,
			Vector3D viewDirection,
			double radius,
			SurfaceColour irisColour,
			SceneObject parent,
			Studio studio)
	{
		this(description, centre, viewDirection, radius, radius*0.6, radius*0.2, irisColour, parent, studio);
	}

	/**
	 * Constructor that assumes a standard iris colour in addition to standard ratios between eyeball
	 * radius, iris radius, and pupil radius.
	 * @param description
	 * @param centre
	 * @param viewDirection
	 * @param radius
	 * @param parent
	 * @param studio
	 */
	public Eye(
			String description,
			Vector3D centre,
			Vector3D viewDirection,
			double radius,
			SceneObject parent,
			Studio studio)
	{
		this(description, centre, viewDirection, radius, SurfaceColour.BLUE_SHINY, parent, studio);
	}
	
	/**
	 * Makes a copy of the argument.
	 * @param original
	 */
	public Eye(Eye original)
	{
		super(original);
	}
	
	/* (non-Javadoc)
	 * @see optics.raytrace.GUI.sceneObjects.EditableSceneObjectCollection#clone()
	 */
	@Override
	public Eye clone()
	{
		return new Eye(this);
	}
	
	/**
	 * Add the scene elements representing eyeball, iris and pupil.
	 * The parameters (centre, view direction, ...) need to be set for this to work.
	 * Note that this method does not remove any elements already in the scene-object container.
	 */
	private void addElements()
	{
		eyeball = new EditableScaledParametrisedSphere("eyeball", getCentre(), getRadius(), SurfaceColour.WHITE_SHINY, this, getStudio());
		addSceneObject(eyeball);
		
		// iris = new EditableScaledParametrisedDisc("iris", centre.getSumWith(getViewDirection().getWithLength(getRadius()+MyMath.TINY)), getViewDirection(), getIrisRadius(), getIrisColour(), this, getStudio());
		iris = new EditableSphericalCap("iris", centre.getSumWith(getViewDirection().getWithLength(getRadius()+MyMath.TINY)), centre, getIrisRadius(), false, getIrisColour(), this, getStudio());
		addSceneObject(iris);
		
		// pupil = new EditableScaledParametrisedDisc("pupil", centre.getSumWith(getViewDirection().getWithLength(getRadius()+2*MyMath.TINY)), getViewDirection(), getPupilRadius(), SurfaceColour.BLACK_SHINY, this, getStudio());
		pupil = new EditableSphericalCap("pupil", centre.getSumWith(getViewDirection().getWithLength(getRadius()+2*MyMath.TINY)), centre, getPupilRadius(), false, SurfaceColour.BLACK_SHINY, this, getStudio());
		addSceneObject(pupil);
	}

	/**
	 * @return	the centre of the eyeball
	 */
	public Vector3D getCentre() {
		return centre;
	}

	/**
	 * Sets the parameter determining the centre of the eyeball.
	 * The addElements method needs to be invoked for this to have an effect.
	 * @param centre
	 */
	public void setCentre(Vector3D centre) {
		this.centre = centre;
	}

	/**
	 * @return	the direction in which the eye is looking
	 */
	public Vector3D getViewDirection() {
		return viewDirection;
	}

	/**
	 * Sets the parameter determining the direction in which the eye is looking.
	 * The addElements method needs to be invoked for this to have an effect.
	 * @param viewDirection
	 */
	public void setViewDirection(Vector3D viewDirection) {
		this.viewDirection = viewDirection;
	}

	/**
	 * @return	the eyeball radius
	 */
	public double getRadius() {
		return radius;
	}

	/**
	 * Sets the parameter determining the eyeball radius.
	 * The addElements method needs to be invoked for this to have an effect.
	 * @param radius
	 */
	public void setRadius(double radius) {
		this.radius = radius;
	}

	/**
	 * @return	the iris radius
	 */
	public double getIrisRadius() {
		return irisRadius;
	}

	/**
	 * Sets the parameter determining the iris radius.
	 * The addElements method needs to be invoked for this to have an effect.
	 * @param irisRadius
	 */
	public void setIrisRadius(double irisRadius) {
		this.irisRadius = irisRadius;
	}

	/**
	 * @return	the pupil radius
	 */
	public double getPupilRadius() {
		return pupilRadius;
	}

	/**
	 * Sets the parameter determining the pupil radius.
	 * The addElements method needs to be invoked for this to have an effect.
	 * @param pupilRadius
	 */
	public void setPupilRadius(double pupilRadius) {
		this.pupilRadius = pupilRadius;
	}

	/**
	 * @return	the iris colour
	 */
	public SurfaceColour getIrisColour() {
		return irisColour;
	}

	/**
	 * Sets the parameter determining the iris colour.
	 * The addElements method needs to be invoked for this to have an effect.
	 * @param irisColour
	 */
	public void setIrisColour(SurfaceColour irisColour) {
		this.irisColour = irisColour;
	}

	/**
	 * @return	the scene object representing the eyeball
	 */
	public Sphere getEyeball() {
		return eyeball;
	}

	/**
	 * @return	the scene object representing the iris
	 */
	public EditableSphericalCap getIris() {
		return iris;
	}

	/**
	 * @return	the scene object representing the pupil
	 */
	public EditableSphericalCap getPupil() {
		return pupil;
	}
	
	/* (non-Javadoc)
	 * @see optics.raytrace.GUI.sceneObjects.EditableSceneObjectCollection#transform(optics.raytrace.core.Transformation)
	 */
	@Override
	public Eye transform(Transformation t)
	{
		return new Eye(
				getDescription(),	// description,
				t.transformPosition(getCentre()),	// centre,
				t.transformDirection(getViewDirection()),
				getRadius(),
				getIrisRadius(),
				getPupilRadius(),
				getIrisColour(),
				getParent(),
				getStudio()
			);
	}

	@Override
	public String getType()
	{
		return "Eye";
	}
}
