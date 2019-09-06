package optics.raytrace.GUI.sceneObjects;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
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
import optics.raytrace.core.SurfaceProperty;
import optics.raytrace.core.Transformation;
import optics.raytrace.sceneObjects.SphericalCap;

/**
 * An editable spherical cap
 * 
 * @author Johannes
 *
 */
public class EditableSphericalCap extends EditableSceneObjectCollection implements ActionListener
{
	private static final long serialVersionUID = -8527706318210407229L;

	/**
	 * This is there so that the interactive version can have a spherical cap representing the eye pupil
	 * and whose radius can be adjusted when the eye's pupil size is being adjusted.
	 */
	private boolean noClone = false;

	// GUI panels
	private transient LabelledVector3DPanel capCentreLine, sphereCentreLine;
	private transient LabelledDoublePanel apertureRadiusLine;
	private transient JCheckBox closedCheckBox;
	private transient SurfacePropertyPanel surfacePropertyPanel;
	private transient JButton convertButton;

	public EditableSphericalCap(
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
		super(description, true, parent, studio);

		setSceneObjectContainer(new SphericalCap(description, capCentre, sphereCentre, apertureRadius, closed, surfaceProperty, parent, studio));
	}
	
	/**
	 * Create a clone of original
	 * @param original
	 */
	public EditableSphericalCap(EditableSphericalCap original)
	{
		super(original);
		if(noClone) System.out.println("Warning: cloning disc despite noClone flag being switched on!");
	}

	@Override
	public EditableSphericalCap clone()
	{
		if(noClone) return this;
		else return new EditableSphericalCap(this);
	}
	
	public boolean isNoClone() {
		return noClone;
	}

	public void setNoClone(boolean noClone) {
		this.noClone = noClone;
	}

	public void refreshSceneObjects()
	{
		SphericalCap oldSphericalCap = (SphericalCap)getSceneObjectContainer();
		oldSphericalCap.addElements();
	}

	// setters and getters

	public double getApertureRadius() {
		return ((SphericalCap)getSceneObjectContainer()).getApertureRadius();
	}

	public void setApertureRadius(double apertureRadius) {
		((SphericalCap)getSceneObjectContainer()).setApertureRadius(apertureRadius);
	}

	public Vector3D getCapCentre() {
		return ((SphericalCap)getSceneObjectContainer()).getCapCentre();
	}

	public void setCapCentre(Vector3D capCentre) {
		((SphericalCap)getSceneObjectContainer()).setCapCentre(capCentre);
	}

	public Vector3D getSphereCentre() {
		return ((SphericalCap)getSceneObjectContainer()).getSphereCentre();
	}

	public void setSphereCentre(Vector3D sphereCentre) {
		((SphericalCap)getSceneObjectContainer()).setSphereCentre(sphereCentre);
	}

	public boolean isClosed() {
		return ((SphericalCap)getSceneObjectContainer()).isClosed();
	}

	public void setClosed(boolean closed) {
		((SphericalCap)getSceneObjectContainer()).setClosed(closed);
	}

	public SurfaceProperty getSurfaceProperty() {
		return ((SphericalCap)getSceneObjectContainer()).getSurfaceProperty();
	}

	public void setSurfaceProperty(SurfaceProperty surfaceProperty) {
		((SphericalCap)getSceneObjectContainer()).setSurfaceProperty(surfaceProperty);
	}
	
	
	@Override
	public EditableSphericalCap transform(Transformation t)
	{
		return new EditableSphericalCap(
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
	

	/**
	 * initialise the edit panel
	 */
	@Override
	public void createEditPanel(IPanel iPanel)
	{	
		this.iPanel = iPanel;
		
		editPanel = new JPanel();
		editPanel.setLayout(new MigLayout("insets 0"));

		editPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Spherical cap"));

		// a text field containing the description
		descriptionPanel = new LabelledStringPanel("Description");
		editPanel.add(descriptionPanel, "wrap");

		capCentreLine = new LabelledVector3DPanel("Cap centre");
		editPanel.add(capCentreLine, "wrap");
		
		sphereCentreLine = new LabelledVector3DPanel("Sphere centre");
		editPanel.add(sphereCentreLine, "wrap");

		apertureRadiusLine = new LabelledDoublePanel("Aperture radius");
		editPanel.add(apertureRadiusLine, "wrap");
		
		closedCheckBox = new JCheckBox("Surface closed");
		closedCheckBox.setAlignmentX(Component.CENTER_ALIGNMENT);
		editPanel.add(closedCheckBox, "wrap");
		
		surfacePropertyPanel = new SurfacePropertyPanel(getStudio().getScene());
		editPanel.add(surfacePropertyPanel, "wrap");
		surfacePropertyPanel.setIPanel(iPanel);

		
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
		
		capCentreLine.setVector3D(getCapCentre());
		sphereCentreLine.setVector3D(getSphereCentre());
		apertureRadiusLine.setNumber(getApertureRadius());
		closedCheckBox.setSelected(isClosed());
		surfacePropertyPanel.setSurfaceProperty(getSurfaceProperty());
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.GUI.Editable#acceptValuesInEditPanel()
	 */
	@Override
	public EditableSphericalCap acceptValuesInEditPanel()
	{
		setDescription(descriptionPanel.getString());

		setCapCentre(capCentreLine.getVector3D());
		setSphereCentre(sphereCentreLine.getVector3D());
		setApertureRadius(apertureRadiusLine.getNumber());
		setClosed(closedCheckBox.isSelected());
		setSurfaceProperty(surfacePropertyPanel.getSurfaceProperty());
		
		// add the (changed) elements
		((SphericalCap)getSceneObjectContainer()).addElements();
		
		return this;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		acceptValuesInEditPanel();	// accept any changes
		EditableSceneObjectCollection container = new EditableSceneObjectCollection(this);
		iPanel.replaceFrontComponent(container, "Edit ex-spherical-cap");
		container.setValuesInEditPanel();
	}
	
	@Override
	public String toString()
	{
          return "<EditableSphericalCap \""+
          	sceneObjectContainer.getDescription() + "\", " +
          	getNumberOfSceneObjects() + " objects>";
          // return description;
	}
}