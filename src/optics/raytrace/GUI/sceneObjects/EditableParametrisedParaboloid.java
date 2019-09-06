package optics.raytrace.GUI.sceneObjects;

import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import math.*;
import optics.raytrace.GUI.core.*;
import optics.raytrace.GUI.lowLevel.GUIBitsAndBobs;
import optics.raytrace.GUI.lowLevel.LabelledDoublePanel;
import optics.raytrace.GUI.lowLevel.LabelledStringPanel;
import optics.raytrace.GUI.lowLevel.LabelledVector3DPanel;
import optics.raytrace.GUI.surfaces.*;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.Studio;
import optics.raytrace.core.SurfaceProperty;
import optics.raytrace.sceneObjects.ParametrisedParaboloid;
import optics.raytrace.surfaces.SurfaceColour;

/**
 * An editable parametrised cone
 * 
 * @author Johannes
 *
 */
public class EditableParametrisedParaboloid extends ParametrisedParaboloid implements IPanelComponent
{
	private static final long serialVersionUID = -7447946019532650745L;

	private JPanel editPanel;
	private LabelledStringPanel descriptionPanel;
	private LabelledVector3DPanel vertexPanel, uHatPanel, vHatPanel, wHatPanel;
	private LabelledDoublePanel aPanel, bPanel, heightPanel;
	private SurfacePropertyPanel surfacePropertyPanel;

	
	public EditableParametrisedParaboloid(
			String description,
			Vector3D vertex,
			Vector3D uHat,
			Vector3D vHat,
			Vector3D wHat,
			double a,
			double b,
			double height,
			SurfaceProperty surfaceProperty,
			SceneObject parent, 
			Studio studio
		)
	{
		super(description, vertex, uHat, vHat, wHat, a, b, height, surfaceProperty, parent, studio);
	}
	
	// default constructor
	public EditableParametrisedParaboloid(String description, SceneObject parent, Studio studio)
	{
		this(
				description,
				new Vector3D(0, 0, 10),	// vertex position
				new Vector3D(1, 0, 0),	// uHat
				new Vector3D(0, 0, 1),	// vHat
				new Vector3D(0, 1, 0),	// wHat
				1, 1,	// a, b
				1,	// height
				SurfaceColour.BLUE_SHINY,	// surfaceProperty
				parent,
				studio
		);
	}
	
	/**
	 * Create a clone of original
	 * @param original
	 */
	public EditableParametrisedParaboloid(EditableParametrisedParaboloid original)
	{
		super(original);
	}
	
	/* (non-Javadoc)
	 * @see optics.raytrace.sceneObjects.ParametrisedCylinder#clone()
	 */
	@Override
	public EditableParametrisedParaboloid clone()
	{
		return new EditableParametrisedParaboloid(this);
	}
	

	
	/**
	 * initialise the edit panel
	 */
	@Override
	public void createEditPanel(IPanel iPanel)
	{	
		editPanel = new JPanel();
		editPanel.setLayout(new MigLayout("insets 0"));
	
		editPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Paraboloid"));

		// a text field containing the description
		descriptionPanel = new LabelledStringPanel("Description");
		editPanel.add(descriptionPanel, "wrap");
		
		// add a bit of (non-stretchable) space
		// editPanel.add(Box.createRigidArea(new Dimension(10,5)));
		
		vertexPanel = new LabelledVector3DPanel("Vertex position");
		editPanel.add(vertexPanel, "wrap");
		
		// add a bit of (non-stretchable) space
		// editPanel.add(Box.createRigidArea(new Dimension(10,5)));
		
		uHatPanel = new LabelledVector3DPanel("uHat direction");
		editPanel.add(uHatPanel, "wrap");

		vHatPanel = new LabelledVector3DPanel("vHat direction");
		editPanel.add(vHatPanel, "wrap");

		wHatPanel = new LabelledVector3DPanel("wHat direction");
		editPanel.add(wHatPanel, "wrap");

		// add a bit of (non-stretchable) space
		// editPanel.add(Box.createRigidArea(new Dimension(10,5)));

		aPanel = new LabelledDoublePanel("a");
		editPanel.add(aPanel, "split 2");

		bPanel = new LabelledDoublePanel("b");
		editPanel.add(bPanel, "wrap");

		// add a bit of (non-stretchable) space
		// editPanel.add(Box.createRigidArea(new Dimension(10,5)));

		heightPanel = new LabelledDoublePanel("Height");
		editPanel.add(heightPanel, "wrap");

		// add a bit of (non-stretchable) space
		// editPanel.add(Box.createRigidArea(new Dimension(10,5)));

		surfacePropertyPanel = new SurfacePropertyPanel(getStudio().getScene());
		editPanel.add(surfacePropertyPanel);
		surfacePropertyPanel.setIPanel(iPanel);
		
		editPanel.validate();
	}

	@Override
	public void discardEditPanel()
	{
		editPanel = null;
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.GUI.Editable#getEditPanel()
	 */
	@Override
	public JPanel getEditPanel()
	{
		return editPanel;
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.GUI.Editable#setValuesInEditPanel()
	 */
	@Override
	public void setValuesInEditPanel()
	{
		// initialize any fields
		descriptionPanel.setString(getDescription());
		vertexPanel.setVector3D(getVertex());
		uHatPanel.setVector3D(getuHat());
		vHatPanel.setVector3D(getvHat());
		wHatPanel.setVector3D(getwHat());
		aPanel.setNumber(getA());
		bPanel.setNumber(getB());
		heightPanel.setNumber(getHeight());
		surfacePropertyPanel.setSurfaceProperty(getSurfaceProperty());
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.GUI.Editable#acceptValuesInEditPanel()
	 */
	@Override
	public EditableParametrisedParaboloid acceptValuesInEditPanel()
	{
		setDescription(descriptionPanel.getString());
		
		setVertex(vertexPanel.getVector3D());
		setCoordinateSystem(uHatPanel.getVector3D(), vHatPanel.getVector3D(), wHatPanel.getVector3D());
		setA(aPanel.getNumber());
		setB(bPanel.getNumber());
		setHeight(heightPanel.getNumber());
		
		setSurfaceProperty(surfacePropertyPanel.getSurfaceProperty());
		
		return this;
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
