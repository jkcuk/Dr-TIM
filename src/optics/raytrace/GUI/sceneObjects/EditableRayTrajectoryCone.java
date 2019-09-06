package optics.raytrace.GUI.sceneObjects;

import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import math.*;
import optics.raytrace.GUI.core.*;
import optics.raytrace.GUI.lowLevel.GUIBitsAndBobs;
import optics.raytrace.GUI.lowLevel.LabelledDoublePanel;
import optics.raytrace.GUI.lowLevel.LabelledIntPanel;
import optics.raytrace.GUI.lowLevel.LabelledStringPanel;
import optics.raytrace.GUI.lowLevel.LabelledVector3DPanel;
import optics.raytrace.GUI.surfaces.*;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.Studio;
import optics.raytrace.core.SurfaceProperty;
import optics.raytrace.sceneObjects.RayTrajectoryCone;

/**
 * A ray trajectory
 * 
 * @author Johannes
 *
 */
public class EditableRayTrajectoryCone extends RayTrajectoryCone implements IPanelComponent
{
	private static final long serialVersionUID = -4457112093091729547L;

	private JPanel editPanel;
	private LabelledStringPanel descriptionPanel;
	private LabelledVector3DPanel startPointPanel, axisDirectionPanel;
	private LabelledDoublePanel coneAnglePanel;
	private LabelledIntPanel numberOfRaysPanel;
	private LabelledDoublePanel rayRadiusPanel;
	private SurfacePropertyPanel surfacePropertyPanel;
	private LabelledIntPanel maxTraceLevelPanel;
	
	public EditableRayTrajectoryCone(
			String description,
			Vector3D startPoint,
			double startTime,
			Vector3D axisDirection,
			double coneAngle,
			int numberOfRays,
			double rayRadius,
			SurfaceProperty surfaceProperty,
			int maxTraceLevel,
			SceneObject parent, 
			Studio studio
	)
	{
		super(description, startPoint, startTime, axisDirection, coneAngle, numberOfRays, rayRadius, surfaceProperty, maxTraceLevel, parent, studio);
	}
	
	/**
	 * Create a clone of original
	 * @param original
	 */
	public EditableRayTrajectoryCone(EditableRayTrajectoryCone original)
	{
		super(original);
	}
	
	/* (non-Javadoc)
	 * @see optics.raytrace.sceneObjects.ParametrisedCylinder#clone()
	 */
	@Override
	public EditableRayTrajectoryCone clone()
	{
		return new EditableRayTrajectoryCone(this);
	}
	

	
	/**
	 * initialise the edit panel
	 */
	@Override
	public void createEditPanel(IPanel iPanel)
	{	
		editPanel = new JPanel();
		editPanel.setLayout(new MigLayout("insets 0"));
	
		editPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Ray-trajectory cone"));

		// a text field containing the description
		descriptionPanel = new LabelledStringPanel("Description");
		editPanel.add(descriptionPanel, "wrap");
		
		// add a bit of (non-stretchable) space
		// editPanel.add(Box.createRigidArea(new Dimension(10,5)));
		
		startPointPanel = new LabelledVector3DPanel("Start point");
		editPanel.add(startPointPanel, "wrap");
		
		// add a bit of (non-stretchable) space
		// editPanel.add(Box.createRigidArea(new Dimension(10,5)));
		
		axisDirectionPanel = new LabelledVector3DPanel("Direction of cone axis");
		editPanel.add(axisDirectionPanel, "wrap");

		coneAnglePanel = new LabelledDoublePanel("Cone angle (degrees)");
		editPanel.add(coneAnglePanel, "wrap");
		
		numberOfRaysPanel = new LabelledIntPanel("Number of rays");
		editPanel.add(numberOfRaysPanel, "wrap");
		
		rayRadiusPanel = new LabelledDoublePanel("Ray radius");
		editPanel.add(rayRadiusPanel, "wrap");

		surfacePropertyPanel = new SurfacePropertyPanel(getStudio().getScene());
		editPanel.add(surfacePropertyPanel, "wrap");
		surfacePropertyPanel.setIPanel(iPanel);
		
		maxTraceLevelPanel = new LabelledIntPanel("Max. trace level");
		editPanel.add(maxTraceLevelPanel);

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
		startPointPanel.setVector3D(getConeApex());
		axisDirectionPanel.setVector3D(getAxisDirection());
		coneAnglePanel.setNumber(MyMath.rad2deg(getConeAngle()));
		numberOfRaysPanel.setNumber(getNumberOfRays());
		rayRadiusPanel.setNumber(getRayRadius());
		surfacePropertyPanel.setSurfaceProperty(getSurfaceProperty());
		maxTraceLevelPanel.setNumber(getMaxTraceLevel());
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.GUI.Editable#acceptValuesInEditPanel()
	 */
	@Override
	public EditableRayTrajectoryCone acceptValuesInEditPanel()
	{
		setDescription(descriptionPanel.getString());
		
		// start afresh
		sceneObjects.clear();
				
		setConeApex(startPointPanel.getVector3D());
		setAxisDirection(axisDirectionPanel.getVector3D());
		setConeAngle(MyMath.deg2rad(coneAnglePanel.getNumber()));
		setNumberOfRays(numberOfRaysPanel.getNumber());
		setRayRadius(rayRadiusPanel.getNumber());
		
		setSurfaceProperty(surfacePropertyPanel.getSurfaceProperty());
		
		setMaxTraceLevel(maxTraceLevelPanel.getNumber());
		
		// now that all the parameters are set, add the ray trajectories
		addRayTrajectories();

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
