package optics.raytrace.sceneObjects;

import java.io.*;
import java.util.ArrayList;

import math.*;
import optics.raytrace.GUI.sceneObjects.EditableParametrisedCylinder;
import optics.raytrace.GUI.sceneObjects.EditableScaledParametrisedDisc;
import optics.raytrace.GUI.sceneObjects.EditableScaledParametrisedSphere;
import optics.raytrace.GUI.sceneObjects.EditableSceneObjectCollection;
import optics.raytrace.GUI.sceneObjects.EditableSphericalCap;
import optics.raytrace.core.*;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.surfaces.SurfaceColour;

/**
 * TIM's head.
 * The head radius is 1.
 * 
 * @see Cylinder
 * @see ParametrisedCone
 * 
 * @author Johannes
 */
public class TimHead extends SceneObjectContainer implements Serializable
{
	private static final long serialVersionUID = 4519037981791645667L;
	
	private Vector3D centre;
	private Vector3D frontDirection;
	private Vector3D topDirection;
	private Vector3D rightDirection;
	private double radius;

	public EditableScaledParametrisedSphere head, nose;
//	public Sphere hair;
	public Eye
		leftEye, rightEye;
 	public EditableSceneObjectCollection cap;
 	public EditableParametrisedCylinder leftEyeBrow, rightEyeBrow, upperLip, lowerLip;
	public EditableScaledParametrisedDisc leftEar, rightEar;

	public TimHead(String description, Vector3D centre, double radius, Vector3D frontDirection, Vector3D topDirection, Vector3D rightDirection, SceneObject parent, Studio studio)
	{
		super(description, parent, studio);
		
		setCentre(centre);
		setRadius(radius);
		setFrontDirection(frontDirection);
		setTopDirection(topDirection);
		setRightDirection(rightDirection);

		// add the scene objects
		addElements();
	}

	public TimHead(String description, Vector3D centre, SceneObject parent, Studio studio)
	{
		this(
				description,
				centre, 
				1,	// radius
				new Vector3D(0, 0, -1),	// frontDirection
				new Vector3D(0, 1, 0), 	// topDirection
				new Vector3D(1, 0, 0),	// rightDirection
				parent,
				studio
			);
	}

	
	/**
	 * Create a clone of the original.
	 * @param original
	 */
	public TimHead(TimHead original)
	{
		this(
				original.getDescription(),
				original.getCentre(),
				original.getRadius(),
				original.getFrontDirection(),
				original.getTopDirection(),
				original.getRightDirection(),
				original.getParent(),
				original.getStudio()
			);
	}
	
	/* (non-Javadoc)
	 * @see optics.raytrace.sceneObjects.solidGeometry.SceneObjectIntersection#clone()
	 */
	@Override
	public TimHead clone()
	{
		return new TimHead(this);
	}
	
	public Vector3D getCentre() {
		return centre;
	}

	public void setCentre(Vector3D centre) {
		this.centre = centre;
	}

	public double getRadius() {
		return radius;
	}

	public void setRadius(double radius) {
		this.radius = radius;
	}

	public Vector3D getFrontDirection() {
		return frontDirection;
	}

	public void setFrontDirection(Vector3D frontDirection) {
		this.frontDirection = frontDirection;
	}

	public Vector3D getTopDirection() {
		return topDirection;
	}

	public void setTopDirection(Vector3D topDirection) {
		this.topDirection = topDirection;
	}

	public Vector3D getRightDirection() {
		return rightDirection;
	}

	public void setRightDirection(Vector3D rightDirection) {
		this.rightDirection = rightDirection;
	}

	public EditableScaledParametrisedSphere getHead() {
		return head;
	}

	public void setHead(EditableScaledParametrisedSphere head) {
		// remove the old head
		removeSceneObject(this.head);
		
		// ... and add the new one
		this.head = head;
		addSceneObject(head);
	}

	public EditableScaledParametrisedSphere getNose() {
		return nose;
	}

	public void setNose(EditableScaledParametrisedSphere nose) {
		// remove the old nose
		removeSceneObject(this.nose);
		
		// ... and add the new one
		this.nose = nose;
		addSceneObject(nose);
	}

	public Eye getLeftEye() {
		return leftEye;
	}

	public void setLeftEye(Eye leftEye) {
		// remove the old left eye
		removeSceneObject(this.leftEye);
		
		// ... and add the new one
		this.leftEye = leftEye;
		addSceneObject(leftEye);
	}

	public Eye getRightEye() {
		return rightEye;
	}

	public void setRightEye(Eye rightEye) {
		// remove the old right eye
		removeSceneObject(this.rightEye);
		
		// ... and add the new one
		this.rightEye = rightEye;
		addSceneObject(rightEye);
	}

	public EditableSceneObjectCollection getCap() {
		return cap;
	}

	public void setCap(EditableSceneObjectCollection cap) {
		// remove the old cap
		removeSceneObject(this.cap);
		
		// ... and add the new one
		this.cap = cap;
		addSceneObject(cap);
	}

	public EditableParametrisedCylinder getLeftEyeBrow() {
		return leftEyeBrow;
	}

	public void setLeftEyeBrow(EditableParametrisedCylinder leftEyeBrow) {
		// remove the old left eye brow
		removeSceneObject(this.leftEyeBrow);
		
		// ... and add the new one
		this.leftEyeBrow = leftEyeBrow;
		addSceneObject(leftEyeBrow);
	}

	public EditableParametrisedCylinder getRightEyeBrow() {
		return rightEyeBrow;
	}

	public void setRightEyeBrow(EditableParametrisedCylinder rightEyeBrow) {
		// remove the old right eye brow
		removeSceneObject(this.rightEyeBrow);
		
		// ... and add the new one
		this.rightEyeBrow = rightEyeBrow;
		addSceneObject(rightEyeBrow);
	}

	public EditableParametrisedCylinder getUpperLip() {
		return upperLip;
	}

	public void setUpperLip(EditableParametrisedCylinder upperLip) {
		// remove the old upper lip
		removeSceneObject(this.upperLip);
		
		// ... and add the new one
		this.upperLip = upperLip;
		addSceneObject(upperLip);
	}

	public EditableParametrisedCylinder getLowerLip() {
		return lowerLip;
	}

	public void setLowerLip(EditableParametrisedCylinder lowerLip) {
		// remove the old lower lip
		removeSceneObject(this.lowerLip);
		
		// ... and add the new one
		this.lowerLip = lowerLip;
		addSceneObject(lowerLip);
	}

	public EditableScaledParametrisedDisc getLeftEar() {
		return leftEar;
	}

	public void setLeftEar(EditableScaledParametrisedDisc leftEar) {
		// remove the old left year
		removeSceneObject(this.leftEar);
		
		// ... and add the new one
		this.leftEar = leftEar;
		addSceneObject(leftEar);
	}

	public EditableScaledParametrisedDisc getRightEar() {
		return rightEar;
	}

	public void setRightEar(EditableScaledParametrisedDisc rightEar) {
		// remove the old right ear
		removeSceneObject(this.rightEar);
		
		// ... and add the new one
		this.rightEar = rightEar;
		addSceneObject(rightEar);
	}

	public void addElements()
	{
		clear();
		
		// first create a coordinate system from the given directions
		ArrayList<Vector3D> basis = new ArrayList<Vector3D>(3);
		basis.add(rightDirection.getPartPerpendicularTo(topDirection, frontDirection).getNormalised());
		basis.add(topDirection.getNormalised());
		basis.add(frontDirection.getPartPerpendicularTo(topDirection).getWithLength(-1));
				
		head = new EditableScaledParametrisedSphere("head", centre, radius, SurfaceColour.SKIN_SHINY, this, getStudio());
//		hair = new Sphere("hair", new Vector3D(0, 0.2, 0.1), .95, new SurfaceColour(DoubleColour.BROWN, DoubleColour.BLACK), this, getStudio());
		cap = new EditableSceneObjectCollection(new SceneObjectContainer("cap", this, getStudio()), true);
		cap.addSceneObject(
				new EditableSphericalCap(
						"top",
						new Vector3D(0.1694, 0.9608, 0.2614).getProductWith(radius).fromBasis(basis).getSumWith(centre),	// capCentre
						centre,	// sphereCentre
						0.9*radius,	// apertureRadius
						false,	// closed
						SurfaceColour.BLUE_MATT,
						cap,	// parent
						getStudio()
					));
		cap.addSceneObject(
				new EditableScaledParametrisedDisc(
						"visor",
						new Vector3D(0.1736, 0.6333, -0.5468).getProductWith(radius).fromBasis(basis).getSumWith(centre),	// centre
						new Vector3D(0.1677, 0.9513, 0.2588).fromBasis(basis),	// normal
						0.7*radius,	// radius
						SurfaceColour.BLUE_MATT,
						cap,	// parent
						getStudio()
					)
				);
		// cap = cap.transform(new RotationAroundYAxis(MyMath.deg2rad(5))).transform(new RotationAroundXAxis(MyMath.deg2rad(15))).transform(new RotationAroundZAxis(MyMath.deg2rad(-10))).transform(translation);
		nose = new EditableScaledParametrisedSphere(
				"nose",
				new Vector3D(0, 0, -1).getProductWith(radius).fromBasis(basis).getSumWith(centre),	// centre
				0.2*radius,	// radius
				SurfaceColour.SKIN_SHINY,
				this,
				getStudio()
			);
		rightEye = new Eye(
				"right eye",
				new Vector3D(-0.25, .25, -0.9).getProductWith(radius).fromBasis(basis).getSumWith(centre),	// centre
				new Vector3D(0, 0, -1).fromBasis(basis),	// viewDirection
				0.15*radius,	// radius
				this,
				getStudio()
			);
		leftEye = new Eye(
				"left eye",
				new Vector3D(0.25, .25, -0.9).getProductWith(radius).fromBasis(basis).getSumWith(centre),	// centre
				new Vector3D(0, 0, -1).fromBasis(basis),	// viewDirection
				0.15*radius,	// radius
				this,
				getStudio()
			);
		rightEyeBrow = new EditableParametrisedCylinder(
				"right eye brow",
				new Vector3D(-0.45, 0.5, -0.85).getProductWith(radius).fromBasis(basis).getSumWith(centre),	// start point
				new Vector3D(-0.05, 0.55, -0.9).getProductWith(radius).fromBasis(basis).getSumWith(centre),	// end point
				0.025*radius,
				SurfaceColour.BLACK_MATT,
				this,
				getStudio()
			);
		leftEyeBrow = new EditableParametrisedCylinder(
				"left eye brow",
				new Vector3D(0.45, 0.5, -0.85).getProductWith(radius).fromBasis(basis).getSumWith(centre),	// start point
				new Vector3D(0.05, 0.55, -0.9).getProductWith(radius).fromBasis(basis).getSumWith(centre),	// end point
				0.025*radius,
				SurfaceColour.BLACK_MATT,
				this,
				getStudio()
			);
		upperLip = new EditableParametrisedCylinder(
				"upper lip",
				new Vector3D(-0.33, -0.25, -1).getProductWith(radius).fromBasis(basis).getSumWith(centre),	// start point
				new Vector3D(0.33, -0.25, -1).getProductWith(radius).fromBasis(basis).getSumWith(centre),	// end point
				0.05*radius,
				SurfaceColour.RED_SHINY,
				this,
				getStudio()
			);
		lowerLip = new EditableParametrisedCylinder(
				"lower lip",
				new Vector3D(-0.33, -0.35, -1).getProductWith(radius).fromBasis(basis).getSumWith(centre),	// start point
				new Vector3D(0.33, -0.35, -1).getProductWith(radius).fromBasis(basis).getSumWith(centre),	// end point
				0.05*radius,
				SurfaceColour.RED_SHINY,
				this,
				getStudio()
			);
		rightEar = new EditableScaledParametrisedDisc(
				"right ear",
				new Vector3D(-1, 0, 0).getProductWith(radius).fromBasis(basis).getSumWith(centre),	// centre
				new Vector3D(0, 0, 1).fromBasis(basis),	// normal
				0.2*radius,
				SurfaceColour.SKIN_SHINY,
				this,
				getStudio()
			);
		leftEar = new EditableScaledParametrisedDisc(
				"left ear",
				new Vector3D(1, 0, 0).getProductWith(radius).fromBasis(basis).getSumWith(centre),	// centre
				new Vector3D(0, 0, 1).fromBasis(basis),	// normal
				0.2*radius,
				SurfaceColour.SKIN_SHINY,
				this,
				getStudio()
			);
		
		addSceneObject(head);
		// addSceneObject(hair);
		addSceneObject(cap);
		addSceneObject(nose);
		addSceneObject(leftEye);
		addSceneObject(rightEye);
		addSceneObject(leftEyeBrow);
		addSceneObject(rightEyeBrow);
		addSceneObject(upperLip);
		addSceneObject(lowerLip);
		addSceneObject(leftEar);
		addSceneObject(rightEar);
	}

	@Override
	public TimHead transform(Transformation t)
	{
		TimHead h = new TimHead(
				getDescription(),
				t.transformPosition(getCentre()),
				getParent(),
				getStudio()
			);
		
		// get rid of all the scene objects in h
		h.clear();
		
		h.setHead(getHead().transform(t));
		h.setNose(getNose().transform(t));
		h.setLeftEye(getLeftEye().transform(t));
		h.setRightEye(getRightEye().transform(t));
	 	h.setCap(getCap().transform(t));
	 	h.setLeftEyeBrow(getLeftEyeBrow().transform(t));
	 	h.setRightEyeBrow(getRightEyeBrow().transform(t));
	 	h.setUpperLip(getUpperLip().transform(t));
	 	h.setLowerLip(getLowerLip().transform(t));
		h.setLeftEar(getLeftEar().transform(t));
		h.setRightEar(getRightEar().transform(t));
		
		return h;
	}
	
	@Override
	public String getType()
	{
		return "Head";
	}
}