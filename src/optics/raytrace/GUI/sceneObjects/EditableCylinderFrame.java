package optics.raytrace.GUI.sceneObjects;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
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
import math.*;

/**
 * A cylinder frame -- a parallelepiped of cylinders with spheres in the corners.
 * @author Johannes Courtial
 */
public class EditableCylinderFrame extends EditableSceneObjectCollection implements ActionListener
{
	private static final long serialVersionUID = -2794677272148152090L;

	// parameters
	private Vector3D
		corner,	// assume this is the bottom left corner
		widthVector, heightVector;
	private double radius;
	private SurfaceProperty surfaceProperty;

	// GUI panels
//	private JPanel editPanel;
//	private StringLine descriptionPanel;
	private LabelledVector3DPanel cornerPanel, widthVectorPanel, heightVectorPanel;
	private LabelledDoublePanel radiusPanel;
	private JButton convertButton;
	private SurfacePropertyPanel surfacePropertyPanel;

	public EditableCylinderFrame(
			String description,
			Vector3D corner,
			Vector3D widthVector,
			Vector3D heightVector,
			double radius,
			SurfaceProperty surfaceProperty,
			SceneObject parent, 
			Studio studio
	)
	{
		// constructor of superclass
		super(description, true, parent, studio);
		
		// copy the parameters into this instance's variables
		this.corner = corner;
		this.widthVector = widthVector;
		this.heightVector = heightVector;
		this.radius = radius;
		this.surfaceProperty = surfaceProperty;

		addCylindersAndSpheres();
	}

	/**
	 * Create a clone of original
	 * @param original
	 */
	public EditableCylinderFrame(EditableCylinderFrame original)
	{
		super(original);
		
		// copy the original's parameters
		this.corner = original.corner;
		this.widthVector = original.widthVector;
		this.heightVector = original.heightVector;
		this.radius = original.radius;
		this.surfaceProperty = original.surfaceProperty;
	}

	@Override
	public EditableCylinderFrame clone()
	{
		return new EditableCylinderFrame(this);
	}

	public Vector3D getCorner() {
		return corner;
	}

	public void setCorner(Vector3D corner) {
		this.corner = corner;
	}

	public Vector3D getWidthVector() {
		return widthVector;
	}

	public void setWidthVector(Vector3D widthVector) {
		this.widthVector = widthVector;
	}

	public Vector3D getHeightVector() {
		return heightVector;
	}

	public void setHeightVector(Vector3D heightVector) {
		this.heightVector = heightVector;
	}

	public double getRadius() {
		return radius;
	}

	public void setRadius(double radius) {
		this.radius = radius;
	}
	
	public SurfaceProperty getSurfaceProperty() {
		return surfaceProperty;
	}

	public void setSurfaceProperty(SurfaceProperty surfaceProperty) {
		this.surfaceProperty = surfaceProperty;
	}

	private void addCylindersAndSpheres()
	{
		// create all the cylinders
		
		Vector3D
			bottomLeft = getCorner(),
			bottomRight = Vector3D.sum(bottomLeft, getWidthVector()),
			topLeft = Vector3D.sum(bottomLeft, getHeightVector()),
			topRight = Vector3D.sum(bottomRight, getHeightVector());

		addSceneObject(new EditableParametrisedCylinder(
				"bottom cylinder",
				bottomLeft,	// bottom left corner is start point
				bottomRight,	// end point
				getRadius(),	// radius
				getSurfaceProperty(),
				this,
				getStudio()
		));

		addSceneObject(new EditableParametrisedCylinder(
				"top cylinder",
				topLeft,	// start point
				topRight,	// end point
				getRadius(),	// radius
				getSurfaceProperty(),
				this,
				getStudio()
		));

		addSceneObject(new EditableParametrisedCylinder(
				"left cylinder",
				bottomLeft,	// start point
				topLeft,	// end point
				getRadius(),	// radius
				getSurfaceProperty(),
				this,
				getStudio()
		));

		addSceneObject(new EditableParametrisedCylinder(
				"right cylinder",
				bottomRight,	// start point
				topRight,	// end point
				getRadius(),	// radius
				getSurfaceProperty(),
				this,
				getStudio()
		));
		
		// add the spheres in the corners
		
		addSceneObject(new EditableScaledParametrisedSphere(
				"sphere in bottom left corner",
				bottomLeft,	// centre
				getRadius(),	// radius
				getSurfaceProperty(),
				this,
				getStudio()
		));

		addSceneObject(new EditableScaledParametrisedSphere(
				"sphere in top left corner",
				topLeft,	// centre
				getRadius(),	// radius
				getSurfaceProperty(),
				this,
				getStudio()
		));

		addSceneObject(new EditableScaledParametrisedSphere(
				"sphere in top right corner",
				topRight,	// centre
				getRadius(),	// radius
				getSurfaceProperty(),
				this,
				getStudio()
		));

		addSceneObject(new EditableScaledParametrisedSphere(
				"sphere in bottom right corner",
				bottomRight,	// centre
				getRadius(),	// radius
				getSurfaceProperty(),
				this,
				getStudio()
		));
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

		editPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Cylinder frame"));

		// a text field containing the description
		descriptionPanel = new LabelledStringPanel("Description");
		editPanel.add(descriptionPanel, "wrap");

		cornerPanel = new LabelledVector3DPanel("(Bottom left) corner");
		editPanel.add(cornerPanel, "wrap");
		
		widthVectorPanel = new LabelledVector3DPanel("Vector along width");
		editPanel.add(widthVectorPanel, "wrap");

		heightVectorPanel = new LabelledVector3DPanel("Vector along height");
		editPanel.add(heightVectorPanel, "wrap");

		// the radius

		radiusPanel = new LabelledDoublePanel("Cylinder radius");
		editPanel.add(radiusPanel, "wrap");
		
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
		cornerPanel.setVector3D(getCorner());
		widthVectorPanel.setVector3D(getWidthVector());
		heightVectorPanel.setVector3D(getHeightVector());
		radiusPanel.setNumber(getRadius());
		surfacePropertyPanel.setSurfaceProperty(surfaceProperty);		
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.GUI.Editable#acceptValuesInEditPanel()
	 */
	@Override
	public EditableCylinderFrame acceptValuesInEditPanel()
	{
		setDescription(descriptionPanel.getString());

		// start afresh
		getSceneObjects().clear();
		
		setCorner(cornerPanel.getVector3D());
		setWidthVector(widthVectorPanel.getVector3D());
		setHeightVector(heightVectorPanel.getVector3D());
		setRadius(radiusPanel.getNumber());
		setSurfaceProperty(surfacePropertyPanel.getSurfaceProperty());

		// get rid of anything that's in this SceneObjectContainer at the moment...
		clear();
		
		// ... and add the cylinders
		addCylindersAndSpheres();
		
		return this;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		acceptValuesInEditPanel();	// accept any changes
		EditableSceneObjectCollection container = new EditableSceneObjectCollection(this);
		iPanel.replaceFrontComponent(container, "Edit ex-cylinder frame");
		container.setValuesInEditPanel();
	}

	@Override
	public void backToFront(IPanelComponent edited)
	{
		if(edited instanceof SurfaceProperty)
		{
			// the surface property has been edited
			setSurfaceProperty((SurfaceProperty)edited);
			surfacePropertyPanel.setSurfaceProperty(getSurfaceProperty());
		}
	}
}
