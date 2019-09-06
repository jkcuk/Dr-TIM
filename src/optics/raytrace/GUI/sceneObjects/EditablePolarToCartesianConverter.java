package optics.raytrace.GUI.sceneObjects;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import math.*;
import optics.raytrace.GUI.core.IPanel;
import optics.raytrace.GUI.lowLevel.GUIBitsAndBobs;
import optics.raytrace.GUI.lowLevel.LabelledDoublePanel;
import optics.raytrace.GUI.lowLevel.LabelledStringPanel;
import optics.raytrace.GUI.lowLevel.LabelledVector3DPanel;
import optics.raytrace.GUI.surfaces.*;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.Studio;
import optics.raytrace.core.SurfacePropertyPrimitive;
import optics.raytrace.surfaces.*;
import optics.raytrace.surfaces.Teleporting.TeleportationType;

/**
 * An editable polar-to-Cartesian converter
 * 
 * @author Johannes
 *
 */
public class EditablePolarToCartesianConverter extends EditableSceneObjectCollection implements ActionListener
{
	private static final long serialVersionUID = 2708331073269291283L;

	// parameters
	private Vector3D
		centre,
		polarFaceNormal,
		phi0Direction,	// vector in direction of (polar) phi=0 line
		xDirection;	// vector in direction of (Cartesian) x direction
	private double
		sideLength,
		separation;	// separation between the polar and Cartesian ends

	// GUI panels
	private LabelledVector3DPanel centreLine, polarFaceNormalLine, phi0DirectionLine, xDirectionLine;
	private LabelledDoublePanel sideLengthLine;
	private JButton convertButton;

	public EditablePolarToCartesianConverter(
			String description,
			Vector3D centre,
			Vector3D polarFaceNormal,
			Vector3D phi0Direction,
			Vector3D xDirection,
			double sideLength,
			double separation,
			SceneObject parent, 
			Studio studio
	)
	{
		// constructor of superclass
		super(description, true, parent, studio);
		
		// copy the parameters into this instance's variables
		setCentre(centre);
		setPolarFaceNormal(polarFaceNormal);
		setPhi0Direction(phi0Direction);
		setXDirection(xDirection);
		setSideLength(sideLength);
		setSeparation(separation);

		populateSceneObjectCollection();
	}
	
	public EditablePolarToCartesianConverter(
			String description,
			Vector3D centre,
			Vector3D polarFaceNormal,
			Vector3D phi0Direction,
			Vector3D xDirection,
			double sideLength,
			SceneObject parent, 
			Studio studio
	)
	{
		// set the separation to MyMath.TINY
		this(description, centre, polarFaceNormal, phi0Direction, xDirection, sideLength, MyMath.TINY, parent, studio);
	}


	/**
	 * Create a clone of original
	 * @param original
	 */
	public EditablePolarToCartesianConverter(EditablePolarToCartesianConverter original)
	{
		super(original);
		
		// copy the original's parameters
		setCentre(original.getCentre());
		setPolarFaceNormal(original.getPolarFaceNormal());
		setPhi0Direction(original.getPhi0Direction());
		setXDirection(original.getXDirection());
		setSideLength(original.getSideLength());
		setSeparation(original.getSeparation());
	}

	@Override
	public EditablePolarToCartesianConverter clone()
	{
		return new EditablePolarToCartesianConverter(this);
	}

	
	public Vector3D getCentre() {
		return centre;
	}

	public void setCentre(Vector3D centre) {
		this.centre = centre;
	}

	public Vector3D getPolarFaceNormal() {
		return polarFaceNormal;
	}

	public void setPolarFaceNormal(Vector3D polarFaceNormal) {
		this.polarFaceNormal = polarFaceNormal;
	}

	public Vector3D getPhi0Direction() {
		return phi0Direction;
	}

	public void setPhi0Direction(Vector3D phi0Direction) {
		this.phi0Direction = phi0Direction;
	}

	public Vector3D getXDirection() {
		return xDirection;
	}

	public void setXDirection(Vector3D xDirection) {
		this.xDirection = xDirection;
	}

	public double getSideLength() {
		return sideLength;
	}

	public void setSideLength(double sideLength) {
		this.sideLength = sideLength;
	}

	public double getSeparation() {
		return separation;
	}

	public void setSeparation(double separation) {
		this.separation = separation;
	}

	private void populateSceneObjectCollection()
	{
		// create the objects
		EditableScaledParametrisedDisc polarSide = new EditableScaledParametrisedDisc(
				"Polar end",
				Vector3D.sum(getCentre(), getPolarFaceNormal().getWithLength(0.5*getSeparation())),	// centre
				getPolarFaceNormal(),	// normal
				0.5*getSideLength(),	// radius
				getPhi0Direction().getReverse(),	// direction corresponding to phi = 0 (*)
				0, 1,	// scaled r range
				0, 1,	// scaled phi range
				null,	// surface property; will be set later
				getParent(),	// parent
				getStudio()	// studio
		);
		
		/*
		 * (*) Note that the "phi" here is slightly different from the "phi" in the ParametrisedDisc.
		 * phi (here) is phi (PD), mapped onto the range 0 to 1, but as the minimum of phi (PD) is -pi,
		 * the direction of phi=0 (here) is precisely the opposite direction of phi=0 (PD).
		 */
		
		EditableScaledParametrisedCentredParallelogram cartesianSide = new EditableScaledParametrisedCentredParallelogram(
				"Cartesian end",	// description
				Vector3D.sum(getCentre(), getPolarFaceNormal().getWithLength(-0.5*getSeparation())),	// centre
				getXDirection().getWithLength(getSideLength()),	// span vector 1, in x direction
				Vector3D.crossProduct(getPolarFaceNormal(), getXDirection().getWithLength(getSideLength())),	// spanVector2, 
				0, 1,	// scaled u range
				0, 1,	// scaled v range
				null,	// surface property; will be set later
				getParent(),	// parent
				getStudio()	// studio
		);
		
		polarSide.setSurfaceProperty(new EditableTwoSidedSurface(
				SurfaceColour.BLACK_MATT,	// inside
				new Teleporting(cartesianSide, SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT, TeleportationType.PERFECT, true),	// outside
				getStudio().getScene()
			));
		
		cartesianSide.setSurfaceProperty(new EditableTwoSidedSurface(
				new Teleporting(polarSide, SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT, TeleportationType.PERFECT, true),	// inside
				SurfaceColour.BLACK_MATT,	// outside
				getStudio().getScene()
			));
		
		addSceneObject(polarSide);
		addSceneObject(cartesianSide);
	}

	/**
	 * initialise the edit panel
	 */
	@Override
	public void createEditPanel(IPanel iPanel)
	{	
		this.iPanel = iPanel;
		
		editPanel = new JPanel();
		editPanel.setLayout(new MigLayout("insets 0"));

		editPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Polar-to-Cartesian converter"));

		// a text field containing the description
		descriptionPanel = new LabelledStringPanel("Description");
		editPanel.add(descriptionPanel, "wrap");

		centreLine = new LabelledVector3DPanel("Centre");
		editPanel.add(centreLine, "wrap");
		
		polarFaceNormalLine = new LabelledVector3DPanel("Surface normal (outwards) for polar side");
		editPanel.add(polarFaceNormalLine, "wrap");
		
		phi0DirectionLine = new LabelledVector3DPanel("Direction of phi=0 line (polar side)");
		editPanel.add(phi0DirectionLine, "wrap");
		
		xDirectionLine = new LabelledVector3DPanel("x direction (Cartesian side)");
		editPanel.add(xDirectionLine, "wrap");
		
		sideLengthLine = new LabelledDoublePanel("Side length");
		editPanel.add(sideLengthLine, "wrap");
		
		// the convert button

		convertButton = new JButton("Convert to collection of scene objects");
		convertButton.addActionListener(this);
		editPanel.add(convertButton, "south");

		// validate the entire edit panel
		editPanel.validate();
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.GUI.Editable#setValuesInEditPanel()
	 */
	@Override
	public void setValuesInEditPanel()
	{
		// initialize any fields
		descriptionPanel.setString(getDescription());
		
		centreLine.setVector3D(getCentre());
		polarFaceNormalLine.setVector3D(getPolarFaceNormal());
		phi0DirectionLine.setVector3D(getPhi0Direction());
		xDirectionLine.setVector3D(getXDirection());
		sideLengthLine.setNumber(getSideLength());
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.GUI.Editable#acceptValuesInEditPanel()
	 */
	@Override
	public EditablePolarToCartesianConverter acceptValuesInEditPanel()
	{
		setDescription(descriptionPanel.getString());

		setCentre(centreLine.getVector3D());
		setPolarFaceNormal(polarFaceNormalLine.getVector3D());
		setPhi0Direction(phi0DirectionLine.getVector3D());
		setXDirection(xDirectionLine.getVector3D());
		setSideLength(sideLengthLine.getNumber());

		// get rid of anything that's in this SceneObjectContainer at the moment...
		clear();
		
		// ... and add the objects
		populateSceneObjectCollection();
		
		return this;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		acceptValuesInEditPanel();	// accept any changes
		EditableSceneObjectCollection container = new EditableSceneObjectCollection(this);
		iPanel.replaceFrontComponent(container, "Edit ex-polar-to-Cartesian converter");
		container.setValuesInEditPanel();
	}
}