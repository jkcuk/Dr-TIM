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
import optics.raytrace.sceneObjects.Arrow;

/**
 * An editable arrow.
 * 
 * @author Johannes
 *
 */
public class EditableArrow extends Arrow implements IPanelComponent
{
	private static final long serialVersionUID = -2947909453760616590L;

	private JPanel editPanel = null;
	private LabelledStringPanel descriptionPanel;
	private LabelledVector3DPanel startPointPanel, endPointPanel;
	private LabelledDoublePanel shaftRadiusPanel, tipAnglePanel, tipLengthPanel;
	private SurfacePropertyPanel surfacePropertyPanel;

	
	public EditableArrow(
			String description,
			Vector3D startPoint,
			Vector3D endPoint,
			double shaftRadius,
			double tipLength,
			double tipAngle,
			SurfaceProperty surfaceProperty,
			SceneObject parent, 
			Studio studio
		)
	{
		super(description, startPoint, endPoint, shaftRadius, tipLength, tipAngle, surfaceProperty, parent, studio);
	}
	
	public EditableArrow(
			String description,
			Vector3D startPoint,
			Vector3D endPoint,
			SurfaceProperty surfaceProperty,
			SceneObject parent, 
			Studio studio
		)
	{
		this(
				description,
				startPoint,
				endPoint,
				Vector3D.getDistance(endPoint, startPoint)*0.03,	// shaftRadius
				Vector3D.getDistance(endPoint, startPoint)*0.2,	// tipLength
				MyMath.deg2rad(20),	// tipAngle
				surfaceProperty,
				parent,
				studio
				);
	}
	
	/**
	 * Create a clone of original
	 * @param original
	 */
	public EditableArrow(EditableArrow original)
	{
		super(original);
	}
	
	/* (non-Javadoc)
	 * @see optics.raytrace.sceneObjects.ParametrisedCylinder#clone()
	 */
	@Override
	public EditableArrow clone()
	{
		return new EditableArrow(this);
	}
	

	
	/**
	 * initialise the edit panel
	 */
	@Override
	public void createEditPanel(IPanel iPanel)
	{	
		editPanel = new JPanel();
		editPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Arrow"));
		editPanel.setLayout(new MigLayout("insets 0"));

		// a text field containing the description
		descriptionPanel = new LabelledStringPanel("Description");
		editPanel.add(descriptionPanel, "wrap");
				
		startPointPanel = new LabelledVector3DPanel("Start point");
		editPanel.add(startPointPanel, "wrap");
		
		endPointPanel = new LabelledVector3DPanel("End point");
		editPanel.add(endPointPanel, "wrap");
		
		shaftRadiusPanel = new LabelledDoublePanel("Shaft radius");
		editPanel.add(shaftRadiusPanel, "wrap");
		
		tipAnglePanel = new LabelledDoublePanel("Tip angle (degrees)");
		editPanel.add(tipAnglePanel, "wrap");

		tipLengthPanel = new LabelledDoublePanel("Tip length");
		editPanel.add(tipLengthPanel, "wrap");

		surfacePropertyPanel = new SurfacePropertyPanel(getStudio().getScene());
		surfacePropertyPanel.setIPanel(iPanel);
		editPanel.add(surfacePropertyPanel, "wrap");

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
		if(editPanel == null) createEditPanel(null);

		// initialize any fields
		descriptionPanel.setString(getDescription());
		startPointPanel.setVector3D(getStartPoint());
		endPointPanel.setVector3D(getEndPoint());
		shaftRadiusPanel.setNumber(getShaftRadius());
		tipAnglePanel.setNumber(MyMath.rad2deg(getTipAngle()));
		tipLengthPanel.setNumber(getTipLength());
		surfacePropertyPanel.setSurfaceProperty(getSurfaceProperty());
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.GUI.Editable#acceptValuesInEditPanel()
	 */
	@Override
	public EditableArrow acceptValuesInEditPanel()
	{
		setDescription(descriptionPanel.getString());
		
		// start afresh
		sceneObjects.clear();
				
		setStartPoint(startPointPanel.getVector3D());
		setEndPoint(endPointPanel.getVector3D());
		setShaftRadius(shaftRadiusPanel.getNumber());
		setTipAngle(MyMath.deg2rad(tipAnglePanel.getNumber()));
		setTipLength(tipLengthPanel.getNumber());
		
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
