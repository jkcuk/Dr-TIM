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
import optics.raytrace.core.Transformation;
import optics.raytrace.sceneObjects.ParametrisedCylinder;

/**
 * A cylinder with lids.
 * Made from the primitive scene objects CylinderTop and two Discs (the lids).
 * 
 * @see CylinderTop
 * @see Disc
 * 
 * @author Johannes
 *
 */
public class EditableParametrisedCylinder extends ParametrisedCylinder implements IPanelComponent
{
	private static final long serialVersionUID = -764387131851979799L;

	private JPanel editPanel;
	private LabelledStringPanel descriptionPanel;
	private LabelledVector3DPanel startPointPanel, endPointPanel;
	private LabelledDoublePanel radiusPanel;
	private SurfacePropertyPanel surfacePropertyPanel;
	private JCheckBox showEndCapsCheckBox;

	
	/**
	 * @param description
	 * @param startPoint
	 * @param endPoint
	 * @param radius
	 * @param surfaceProperty
	 * @param parent
	 * @param studio
	 */
	public EditableParametrisedCylinder(
			String description,
			Vector3D startPoint,
			Vector3D endPoint,
			double radius,
			SurfaceProperty surfaceProperty,
			SceneObject parent, 
			Studio studio
	)
	{
		this(description, startPoint, endPoint, radius, true, surfaceProperty, parent, studio);
	}

	/**
	 * @param description
	 * @param startPoint
	 * @param endPoint
	 * @param radius
	 * @param surfaceProperty
	 * @param parent
	 * @param studio
	 */
	public EditableParametrisedCylinder(
			String description,
			Vector3D startPoint,
			Vector3D endPoint,
			double radius,
			boolean showEndCaps,
			SurfaceProperty surfaceProperty,
			SceneObject parent, 
			Studio studio
	)
	{
		super(description, startPoint, endPoint, radius, showEndCaps, surfaceProperty, parent, studio);
	}
	
	/**
	 * Create a clone of original
	 * @param original
	 */
	public EditableParametrisedCylinder(EditableParametrisedCylinder original)
	{
		super(original);
	}
	
	/* (non-Javadoc)
	 * @see optics.raytrace.sceneObjects.ParametrisedCylinder#clone()
	 */
	@Override
	public EditableParametrisedCylinder clone()
	{
		return new EditableParametrisedCylinder(this);
	}
	

	
	/**
	 * initialise the edit panel
	 */
	@Override
	public void createEditPanel(IPanel iPanel)
	{	
		editPanel = new JPanel();
		editPanel.setLayout(new MigLayout("insets 0"));
	
		editPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Cylinder"));

		// a text field containing the description
		descriptionPanel = new LabelledStringPanel("Description");
		editPanel.add(descriptionPanel, "wrap");
		
		startPointPanel = new LabelledVector3DPanel("Start point");
		editPanel.add(startPointPanel, "wrap");
		
		endPointPanel = new LabelledVector3DPanel("End point");
		editPanel.add(endPointPanel, "wrap");

		radiusPanel = new LabelledDoublePanel("Radius");
		editPanel.add(radiusPanel, "wrap");
		
		showEndCapsCheckBox = new JCheckBox("Show end caps");
		editPanel.add(showEndCapsCheckBox, "wrap");

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
		startPointPanel.setVector3D(getStartPoint());
		endPointPanel.setVector3D(getEndPoint());
		radiusPanel.setNumber(getRadius());
		showEndCapsCheckBox.setSelected(isShowEndCaps());
		surfacePropertyPanel.setSurfaceProperty(getSurfaceProperty());
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.GUI.Editable#acceptValuesInEditPanel()
	 */
	@Override
	public EditableParametrisedCylinder acceptValuesInEditPanel()
	{
		setDescription(descriptionPanel.getString());
		
		// start afresh
		sceneObjects.clear();
				
		setStartPoint(startPointPanel.getVector3D());
		setEndPoint(endPointPanel.getVector3D());
		setRadius(radiusPanel.getNumber());
		setShowEndCaps(showEndCapsCheckBox.isSelected());
		
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
	
	@Override
	public EditableParametrisedCylinder transform(Transformation t)
	{
		return new EditableParametrisedCylinder(
				getDescription(),
				t.transformPosition(getStartPoint()),
				t.transformPosition(getEndPoint()),
				getRadius(),
				isShowEndCaps(),
				getSurfaceProperty(),
				getParent(), 
				getStudio()
		);
	}
}
