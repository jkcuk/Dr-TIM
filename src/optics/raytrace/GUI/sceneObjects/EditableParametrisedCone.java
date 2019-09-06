package optics.raytrace.GUI.sceneObjects;

import javax.swing.JCheckBox;
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
import optics.raytrace.sceneObjects.ParametrisedCone;

/**
 * An editable parametrised cone
 * 
 * @author Johannes
 *
 */
public class EditableParametrisedCone extends ParametrisedCone implements IPanelComponent
{
	private static final long serialVersionUID = -2947909453760616590L;

	private JPanel editPanel;
	private LabelledStringPanel descriptionPanel;
	private LabelledVector3DPanel apexPanel, axisPanel;
	private JCheckBox openCheckBox;
	private LabelledDoublePanel thetaPanel, heightPanel;
	private SurfacePropertyPanel surfacePropertyPanel;

	
	public EditableParametrisedCone(
			String description,
			Vector3D apex,
			Vector3D axis,
			boolean open,
			double theta,
			double height,
			SurfaceProperty surfaceProperty,
			SceneObject parent, 
			Studio studio
		)
	{
		super(description, apex, axis, open, theta, height, surfaceProperty, parent, studio);
	}
	
	/**
	 * Create a clone of original
	 * @param original
	 */
	public EditableParametrisedCone(EditableParametrisedCone original)
	{
		super(original);
	}
	
	/* (non-Javadoc)
	 * @see optics.raytrace.sceneObjects.ParametrisedCylinder#clone()
	 */
	@Override
	public EditableParametrisedCone clone()
	{
		return new EditableParametrisedCone(this);
	}
	

	
	/**
	 * initialise the edit panel
	 */
	@Override
	public void createEditPanel(IPanel iPanel)
	{	
		editPanel = new JPanel();
		editPanel.setLayout(new MigLayout("insets 0"));
	
		editPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Cone"));

		// a text field containing the description
		descriptionPanel = new LabelledStringPanel("Description");
		editPanel.add(descriptionPanel, "wrap");
		
		// add a bit of (non-stretchable) space
		// editPanel.add(Box.createRigidArea(new Dimension(10,5)));
		
		apexPanel = new LabelledVector3DPanel("Apex");
		editPanel.add(apexPanel, "wrap");
		
		// add a bit of (non-stretchable) space
		// editPanel.add(Box.createRigidArea(new Dimension(10,5)));
		
		axisPanel = new LabelledVector3DPanel("Axis direction");
		editPanel.add(axisPanel, "wrap");
		
		openCheckBox = new JCheckBox("Open?");
		editPanel.add(openCheckBox, "wrap");

		// add a bit of (non-stretchable) space
		// editPanel.add(Box.createRigidArea(new Dimension(10,5)));

		thetaPanel = new LabelledDoublePanel("Cone angle (degrees)");
		editPanel.add(thetaPanel, "wrap");

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

//		editPanel = new JPanel();
//		editPanel.setLayout(new BoxLayout(editPanel, BoxLayout.Y_AXIS));
//	
//		editPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Cone"));
//
//		// a text field containing the description
//		descriptionPanel = new LabelledStringPanel("Description");
//		editPanel.add(descriptionPanel);
//		
//		// add a bit of (non-stretchable) space
//		// editPanel.add(Box.createRigidArea(new Dimension(10,5)));
//		
//		apexPanel = new LabelledVector3DPanel("Apex");
//		editPanel.add(apexPanel);
//		
//		// add a bit of (non-stretchable) space
//		// editPanel.add(Box.createRigidArea(new Dimension(10,5)));
//		
//		axisPanel = new LabelledVector3DPanel("Axis direction");
//		editPanel.add(axisPanel);
//
//		// add a bit of (non-stretchable) space
//		// editPanel.add(Box.createRigidArea(new Dimension(10,5)));
//
//		thetaPanel = new LabelledDoublePanel("Cone angle (degrees)");
//		editPanel.add(thetaPanel);
//
//		// add a bit of (non-stretchable) space
//		// editPanel.add(Box.createRigidArea(new Dimension(10,5)));
//
//		heightPanel = new LabelledDoublePanel("Height");
//		editPanel.add(heightPanel);
//
//		// add a bit of (non-stretchable) space
//		// editPanel.add(Box.createRigidArea(new Dimension(10,5)));
//
//		surfacePropertyPanel = new SurfacePropertyPanel(getStudio().getScene());
//		editPanel.add(surfacePropertyPanel);
//		surfacePropertyPanel.setIPanel(iPanel);
//		
//		editPanel.validate();
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
		apexPanel.setVector3D(getApex());
		axisPanel.setVector3D(getAxis());
		openCheckBox.setSelected(isOpen());
		thetaPanel.setNumber(MyMath.rad2deg(getTheta()));
		heightPanel.setNumber(getHeight());
		surfacePropertyPanel.setSurfaceProperty(getSurfaceProperty());
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.GUI.Editable#acceptValuesInEditPanel()
	 */
	@Override
	public EditableParametrisedCone acceptValuesInEditPanel()
	{
		setDescription(descriptionPanel.getString());
		
		// start afresh
		sceneObjects.clear();
				
		setApex(apexPanel.getVector3D());
		setAxis(axisPanel.getVector3D());
		setOpen(openCheckBox.isSelected());
		setTheta(MyMath.deg2rad(thetaPanel.getNumber()));
		setHeight(heightPanel.getNumber());
		
		setSurfaceProperty(surfacePropertyPanel.getSurfaceProperty());
		
		addSceneObjects();
		
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
