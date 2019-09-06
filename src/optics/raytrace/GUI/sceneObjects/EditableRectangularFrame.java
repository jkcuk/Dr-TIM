package optics.raytrace.GUI.sceneObjects;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import math.*;
import optics.raytrace.GUI.core.IPanel;
import optics.raytrace.GUI.core.IPanelComponent;
import optics.raytrace.GUI.lowLevel.GUIBitsAndBobs;
import optics.raytrace.GUI.lowLevel.LabelledDoublePanel;
import optics.raytrace.GUI.lowLevel.LabelledStringPanel;
import optics.raytrace.GUI.lowLevel.LabelledVector3DPanel;
import optics.raytrace.GUI.surfaces.SurfacePropertyPanel;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.Studio;
import optics.raytrace.core.SurfaceProperty;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectIntersection;
import optics.raytrace.surfaces.SurfaceColour;

/**
 * Editable rectangular frame
 * 
 * @author Johannes
 */
public class EditableRectangularFrame extends EditableSceneObjectCollection implements ActionListener
{
	private static final long serialVersionUID = -3865584896629892123L;

	// parameters
	private Vector3D
		centre,	// centre of the cuboid in EM space
		x, y,	// unit vectors that span the plane of the frame
		z;	// unit vector perpendicular to the plane of the frame
	private double
		openingWidth,
		openingHeight,
		frameWidth,	// in addition to the width, above
		frameHeight,
		frameThickness;
	private SurfaceProperty surfaceProperty, insideSurfaceProperty;

	// GUI panels
	private LabelledVector3DPanel centreLine, xLine, yLine;
	private LabelledDoublePanel openingWidthLine, openingHeightLine, frameWidthLine, frameHeightLine, frameThicknessLine;
	private JButton convertButton;
	private SurfacePropertyPanel surfacePropertyPanel, insideSurfacePropertyPanel;
	
	/**
	 * Minimal constructor, assumes default parameters
	 * @param parent
	 * @param studio
	 */
	public EditableRectangularFrame(SceneObject parent, Studio studio)
	{
		this(
				"Rectangular frame",
				new Vector3D(0, 0, 10),	// centre
				new Vector3D(1, 0, 0),	// right direction
				new Vector3D(0, 1, 0),	// up direction
				1.0,	// opening width
				1.0,	// opening height
				0.1,	// frame width
				0.1,	// frame height
				0.1,	// frame thickness
				SurfaceColour.GREY50_MATT,	// outside surface property
				SurfaceColour.BLACK_MATT,	// inside surface property
				parent,
				studio
			);
	}

	public EditableRectangularFrame(
			String description,
			Vector3D centre,	// window centre
			Vector3D x,	// unit vector "to the right"
			Vector3D y,	// unit vector "upwards"
			double openingWidth,
			double openingHeight,
			double frameWidth,
			double frameHeight,
			double frameThickness,
			SurfaceProperty surfaceProperty,
			SurfaceProperty insideSurfaceProperty,
			SceneObject parent, 
			Studio studio
	)
	{
		// this represents is a union of scene objects
		// super(new SceneObjectUnion(description, parent, studio));
		super(description, true, parent, studio);
		
		// copy the parameters into this instance's variables
		setCentre(centre);
		setXY(x, y);
		setOpeningWidth(openingWidth);
		setOpeningHeight(openingHeight);
		setFrameWidth(frameWidth);
		setFrameHeight(frameHeight);
		setFrameThickness(frameThickness);
		setSurfaceProperty(surfaceProperty);
		setInsideSurfaceProperty(insideSurfaceProperty);

		populateSceneObjectCollection();
	}

	/**
	 * Create a clone of original
	 * @param original
	 */
	public EditableRectangularFrame(EditableRectangularFrame original)
	{
		this(
			original.getDescription(),
			original.getCentre().clone(),
			original.getX().clone(),
			original.getY().clone(),
			original.getOpeningWidth(),
			original.getOpeningHeight(),
			original.getFrameWidth(),
			original.getFrameHeight(),
			original.getFrameThickness(),
			original.getSurfaceProperty(),
			original.getInsideSurfaceProperty(),
			original.getParent(),
			original.getStudio()
		);
	}

	@Override
	public EditableRectangularFrame clone()
	{
		return new EditableRectangularFrame(this);
	}

	
	//
	// setters and getters
	//
	

	public Vector3D getCentre() {
		return centre;
	}

	public void setCentre(Vector3D centre) {
		this.centre = centre;
	}

	public Vector3D getX() {
		return x;
	}

	public void setXY(Vector3D x, Vector3D y)
	{
		this.x = x.getNormalised();
		this.y = y.getPartPerpendicularTo(this.x).getNormalised();
		z = Vector3D.crossProduct(this.x, this.y).getNormalised();
	}

	public Vector3D getY() {
		return y;
	}

	public Vector3D getZ() {
		return z;
	}

	public double getOpeningWidth() {
		return openingWidth;
	}

	public void setOpeningWidth(double openingWidth) {
		this.openingWidth = openingWidth;
	}

	public double getOpeningHeight() {
		return openingHeight;
	}

	public void setOpeningHeight(double openingHeight) {
		this.openingHeight = openingHeight;
	}

	public double getFrameWidth() {
		return frameWidth;
	}

	public void setFrameWidth(double frameWidth) {
		this.frameWidth = frameWidth;
	}

	public double getFrameHeight() {
		return frameHeight;
	}

	public void setFrameHeight(double frameHeight) {
		this.frameHeight = frameHeight;
	}

	public double getFrameThickness() {
		return frameThickness;
	}

	public void setFrameThickness(double frameThickness) {
		this.frameThickness = frameThickness;
	}

	public SurfaceProperty getSurfaceProperty() {
		return surfaceProperty;
	}

	public void setSurfaceProperty(SurfaceProperty surfaceProperty) {
		this.surfaceProperty = surfaceProperty;
	}
	
	public SurfaceProperty getInsideSurfaceProperty() {
		return insideSurfaceProperty;
	}

	public void setInsideSurfaceProperty(SurfaceProperty insideSurfaceProperty) {
		this.insideSurfaceProperty = insideSurfaceProperty;
	}
	
	private void addFrontAndBack(EditableSceneObjectCollection sceneObjectContainer)
	{
		sceneObjectContainer.addSceneObject(new EditableParametrisedPlane(
				"Front",	// description
				Vector3D.sum(centre, z.getWithLength(-frameThickness/2)),	// pointOnPlane
				z.getReverse(),	// outwards-facing normal
				surfaceProperty,
				sceneObjectContainer,	// parent
				getStudio()
			));
		
		sceneObjectContainer.addSceneObject(new EditableParametrisedPlane(
				"Back",	// description
				Vector3D.sum(centre, z.getWithLength(frameThickness/2)),	// pointOnPlane
				z,	// outwards-facing normal
				surfaceProperty,
				sceneObjectContainer,	// parent
				getStudio()
			));
	}
	
	private void addLeft(EditableSceneObjectCollection sceneObjectContainer)
	{
		sceneObjectContainer.addSceneObject(new EditableParametrisedPlane(
				"Left",	// description
				Vector3D.sum(centre, x.getWithLength(-0.5*openingWidth-frameWidth)),	// pointOnPlane
				x.getReverse(),	// outwards-facing normal
				surfaceProperty,
				sceneObjectContainer,	// parent
				getStudio()
			));
	}

	private void addRight(EditableSceneObjectCollection sceneObjectContainer)
	{
		sceneObjectContainer.addSceneObject(new EditableParametrisedPlane(
				"Right",	// description
				Vector3D.sum(centre, x.getWithLength(0.5*openingWidth+frameWidth)),	// pointOnPlane
				x,	// outwards-facing normal
				surfaceProperty,
				sceneObjectContainer,	// parent
				getStudio()
			));
	}

	private void addBottom(EditableSceneObjectCollection sceneObjectContainer)
	{
		sceneObjectContainer.addSceneObject(new EditableParametrisedPlane(
				"Bottom",	// description
				Vector3D.sum(centre, y.getWithLength(-0.5*openingHeight-frameHeight)),	// pointOnPlane
				y.getReverse(),	// outwards-facing normal
				surfaceProperty,
				sceneObjectContainer,	// parent
				getStudio()
			));
	}

	private void addTop(EditableSceneObjectCollection sceneObjectContainer)
	{
		sceneObjectContainer.addSceneObject(new EditableParametrisedPlane(
				"Top",	// description
				Vector3D.sum(centre, y.getWithLength(0.5*openingHeight+frameHeight)),	// pointOnPlane
				y,	// outwards-facing normal
				surfaceProperty,
				sceneObjectContainer,	// parent
				getStudio()
			));
	}
	
	private void populateSceneObjectCollection()
	{
		// clear out anything that's already in here before adding the objects (again)
		clear();

		EditableSceneObjectCollection
			left = new EditableSceneObjectCollection(new SceneObjectIntersection("Left side", this, getStudio()), true),
			right = new EditableSceneObjectCollection(new SceneObjectIntersection("Right side", this, getStudio()), true),
			bottom = new EditableSceneObjectCollection(new SceneObjectIntersection("Bottom side", this, getStudio()), true),
			top = new EditableSceneObjectCollection(new SceneObjectIntersection("Top side", this, getStudio()), true);

		// the left side of the frame
		addFrontAndBack(left);
		addLeft(left);
		addTop(left);
		addBottom(left);
		left.addSceneObject(new EditableParametrisedPlane(
				"Inside",	// description
				Vector3D.sum(centre, x.getWithLength(-openingWidth/2)),	// pointOnPlane
				x,	// outwards-facing normal
				(insideSurfaceProperty!=null)?insideSurfaceProperty:surfaceProperty,
				left,	// parent
				getStudio()
			));
		addSceneObject(left);
		
		// the right side of the frame
		addFrontAndBack(right);
		addRight(right);
		addTop(right);
		addBottom(right);
		right.addSceneObject(new EditableParametrisedPlane(
				"Inside",	// description
				Vector3D.sum(centre, x.getWithLength(openingWidth/2)),	// pointOnPlane
				x.getReverse(),	// outwards-facing normal
				(insideSurfaceProperty!=null)?insideSurfaceProperty:surfaceProperty,
				right,	// parent
				getStudio()
			));
		addSceneObject(right);
		
		// the bottom side of the frame
		addFrontAndBack(bottom);
		addBottom(bottom);
		addLeft(bottom);
		addRight(bottom);
		bottom.addSceneObject(new EditableParametrisedPlane(
				"Inside",	// description
				Vector3D.sum(centre, y.getWithLength(-openingHeight/2)),	// pointOnPlane
				y,	// outwards-facing normal
				(insideSurfaceProperty!=null)?insideSurfaceProperty:surfaceProperty,
				bottom,	// parent
				getStudio()
			));
		addSceneObject(bottom);

		// the top side of the frame
		addFrontAndBack(top);
		addTop(top);
		addLeft(top);
		addRight(top);
		top.addSceneObject(new EditableParametrisedPlane(
				"Inside",	// description
				Vector3D.sum(centre, y.getWithLength(openingHeight/2)),	// pointOnPlane
				y.getReverse(),	// outwards-facing normal
				(insideSurfaceProperty!=null)?insideSurfaceProperty:surfaceProperty,
				top,	// parent
				getStudio()
			));
		addSceneObject(top);
	}

	/**
	 * initialise the edit panel
	 */
	@Override
	public void createEditPanel(IPanel iPanel)
	{	
		this.iPanel = iPanel;
		
		editPanel = new JPanel();
		editPanel.setLayout(new MigLayout());

		editPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Rectangular frame"));
		
		// a text field containing the description
		descriptionPanel = new LabelledStringPanel("Description");
		editPanel.add(descriptionPanel, "wrap");
		
		centreLine = new LabelledVector3DPanel("Centre");
		editPanel.add(centreLine, "wrap");

		xLine = new LabelledVector3DPanel("Direction to the right");
		editPanel.add(xLine, "wrap");

		yLine = new LabelledVector3DPanel("Upwards direction");
		editPanel.add(yLine, "wrap");

		JPanel windowOpeningPanel = new JPanel();
		windowOpeningPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Window opening"));

		windowOpeningPanel.setLayout(new MigLayout());
		openingWidthLine = new LabelledDoublePanel("Width");
		windowOpeningPanel.add(openingWidthLine);

		openingHeightLine = new LabelledDoublePanel("Height");
		windowOpeningPanel.add(openingHeightLine);
		
		editPanel.add(windowOpeningPanel, "wrap");
		
		JPanel frameDimensionsPanel = new JPanel();
		frameDimensionsPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Frame"));
		
		frameWidthLine = new LabelledDoublePanel("Width");
		frameDimensionsPanel.add(frameWidthLine);

		frameHeightLine = new LabelledDoublePanel("Height");
		frameDimensionsPanel.add(frameHeightLine);

		frameThicknessLine = new LabelledDoublePanel("Thickness");
		frameDimensionsPanel.add(frameThicknessLine);
		
		editPanel.add(frameDimensionsPanel, "wrap");

		surfacePropertyPanel = new SurfacePropertyPanel("Outside frame surfaces", getStudio().getScene());
		// frameSurfacePropertyPanel.addButtonsActionListener(new SurfacePropertyPanelListener(frameSurfacePropertyPanel));
		editPanel.add(surfacePropertyPanel, "wrap");
		surfacePropertyPanel.setIPanel(iPanel);

		insideSurfacePropertyPanel = new SurfacePropertyPanel("Inside frame surfaces", getStudio().getScene());
		// frameSurfacePropertyPanel.addButtonsActionListener(new SurfacePropertyPanelListener(frameSurfacePropertyPanel));
		editPanel.add(insideSurfacePropertyPanel, "wrap");
		insideSurfacePropertyPanel.setIPanel(iPanel);

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
		xLine.setVector3D(getX());
		yLine.setVector3D(getY());
		openingWidthLine.setNumber(getOpeningWidth());
		openingHeightLine.setNumber(getOpeningHeight());
		frameWidthLine.setNumber(getFrameWidth());
		frameHeightLine.setNumber(getFrameHeight());
		frameThicknessLine.setNumber(getFrameThickness());
		surfacePropertyPanel.setSurfaceProperty(getSurfaceProperty());
		insideSurfacePropertyPanel.setSurfaceProperty(getInsideSurfaceProperty());
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.GUI.Editable#acceptValuesInEditPanel()
	 */
	@Override
	public EditableRectangularFrame acceptValuesInEditPanel()
	{
		setDescription(descriptionPanel.getString());

		setCentre(centreLine.getVector3D());
		setXY(xLine.getVector3D(), yLine.getVector3D());
		setOpeningWidth(openingWidthLine.getNumber());
		setOpeningHeight(openingHeightLine.getNumber());
		setFrameWidth(frameWidthLine.getNumber());
		setFrameHeight(frameHeightLine.getNumber());
		setFrameThickness(frameThicknessLine.getNumber());
		setSurfaceProperty(surfacePropertyPanel.getSurfaceProperty());
		setInsideSurfaceProperty(insideSurfacePropertyPanel.getSurfaceProperty());

		// add the objects
		populateSceneObjectCollection();
		
		return this;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		acceptValuesInEditPanel();	// accept any changes
		EditableSceneObjectCollection container = new EditableSceneObjectCollection(this);
		iPanel.replaceFrontComponent(container, "Edit ex-rectangular frame");
		container.setValuesInEditPanel();
	}

	@Override
	public void backToFront(IPanelComponent edited)
	{
			if(edited instanceof SurfaceProperty)
			{
				// frame surface property has been edited
				setSurfaceProperty((SurfaceProperty)edited);
				surfacePropertyPanel.setSurfaceProperty(getSurfaceProperty());
			}
	}
}