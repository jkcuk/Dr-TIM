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
import optics.raytrace.sceneObjects.RayTrajectoryHyperboloid;

/**
 * A ray-trajectory hyperboloid, editable
 * 
 * @author Johannes
 *
 */
public class EditableRayTrajectoryHyperboloid extends RayTrajectoryHyperboloid implements IPanelComponent
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2842686763559359409L;
	private JPanel editPanel;
	private LabelledStringPanel descriptionPanel;
	private LabelledVector3DPanel startPointPanel, axisDirectionPanel;
	private LabelledDoublePanel hyperboloidAnglePanel;
	private LabelledDoublePanel waistRadiusPanel;
	private LabelledIntPanel numberOfRaysPanel;
	private LabelledDoublePanel rayRadiusPanel;
	private SurfacePropertyPanel surfacePropertyPanel;
	private LabelledIntPanel maxTraceLevelPanel;
	
	public EditableRayTrajectoryHyperboloid(
			String description,
			Vector3D startPoint,
			double startTime,
			Vector3D axisDirection,
			double hyperboloidAngle,
			double waistRadius,
			int numberOfRays,
			double rayRadius,
			SurfaceProperty surfaceProperty,
			int maxTraceLevel,
			SceneObject parent, 
			Studio studio
	)
	{
		super(description, startPoint, startTime, axisDirection, hyperboloidAngle, waistRadius, numberOfRays, rayRadius, surfaceProperty, maxTraceLevel, parent, studio);
	}
	
	/**
	 * Create a clone of original
	 * @param original
	 */
	public EditableRayTrajectoryHyperboloid(EditableRayTrajectoryHyperboloid original)
	{
		super(original);
	}
	
	/* (non-Javadoc)
	 * @see optics.raytrace.sceneObjects.ParametrisedCylinder#clone()
	 */
	@Override
	public EditableRayTrajectoryHyperboloid clone()
	{
		return new EditableRayTrajectoryHyperboloid(this);
	}
	

	
	/**
	 * initialise the edit panel
	 */
	@Override
	public void createEditPanel(IPanel iPanel)
	{	
		editPanel = new JPanel();
		editPanel.setLayout(new MigLayout("insets 0"));
	
		editPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Ray-trajectory hyperboloid"));

		// a text field containing the description
		descriptionPanel = new LabelledStringPanel("Description");
		editPanel.add(descriptionPanel, "wrap");
		
		// add a bit of (non-stretchable) space
		// editPanel.add(Box.createRigidArea(new Dimension(10,5)));
		
		startPointPanel = new LabelledVector3DPanel("Hyperboloid centre");
		editPanel.add(startPointPanel, "wrap");
		
		// add a bit of (non-stretchable) space
		// editPanel.add(Box.createRigidArea(new Dimension(10,5)));
		
		axisDirectionPanel = new LabelledVector3DPanel("Direction of hyperboloid axis");
		editPanel.add(axisDirectionPanel, "wrap");

		hyperboloidAnglePanel = new LabelledDoublePanel("Hyperboloid angle (degrees)");
		editPanel.add(hyperboloidAnglePanel, "wrap");
		
		waistRadiusPanel = new LabelledDoublePanel("Waist radius");
		editPanel.add(waistRadiusPanel, "wrap");

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
		startPointPanel.setVector3D(getStartPoint());
		axisDirectionPanel.setVector3D(getAxisDirection());
		hyperboloidAnglePanel.setNumber(MyMath.rad2deg(getHyperboloidAngle()));
		waistRadiusPanel.setNumber(getWaistRadius());
		numberOfRaysPanel.setNumber(getNumberOfRays());
		rayRadiusPanel.setNumber(getRayRadius());
		surfacePropertyPanel.setSurfaceProperty(getSurfaceProperty());
		maxTraceLevelPanel.setNumber(getMaxTraceLevel());
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.GUI.Editable#acceptValuesInEditPanel()
	 */
	@Override
	public EditableRayTrajectoryHyperboloid acceptValuesInEditPanel()
	{
		setDescription(descriptionPanel.getString());
		
		// start afresh
		sceneObjects.clear();
				
		setStartPoint(startPointPanel.getVector3D());
		setAxisDirection(axisDirectionPanel.getVector3D());
		setHyperboloidAngle(MyMath.deg2rad(hyperboloidAnglePanel.getNumber()));
		setWaistRadius(waistRadiusPanel.getNumber());
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
