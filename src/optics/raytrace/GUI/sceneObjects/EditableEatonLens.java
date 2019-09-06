package optics.raytrace.GUI.sceneObjects;

import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import math.Vector3D;
import optics.raytrace.GUI.core.IPanel;
import optics.raytrace.GUI.core.IPanelComponent;
import optics.raytrace.GUI.lowLevel.GUIBitsAndBobs;
import optics.raytrace.GUI.lowLevel.LabelledDoublePanel;
import optics.raytrace.GUI.lowLevel.LabelledStringPanel;
import optics.raytrace.GUI.lowLevel.LabelledVector3DPanel;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.Studio;
import optics.raytrace.core.Transformation;
import optics.raytrace.sceneObjects.EatonLens;


/**
 * An Eaton lens.
 * 
 * @author Johannes
 *
 */
public class EditableEatonLens extends EatonLens implements IPanelComponent
{
	private static final long serialVersionUID = -2335490138146201118L;

	private JPanel editPanel;
	private LabelledStringPanel descriptionPanel;
	private LabelledVector3DPanel centrePanel;
	private LabelledDoublePanel radiusPanel;
	private LabelledDoublePanel ratioNSurfaceNSurroundingPanel;
	private LabelledDoublePanel transparentTunnelRadiusPanel;
	private LabelledDoublePanel transmissionCoefficientPanel;


	/**
	 * @param description
	 * @param centre
	 * @param radius
	 * @param ratioNSurfaceNSurrounding
	 * @param transparentTunnelRadius
	 * @param transmissionCoefficient
	 * @param parent
	 * @param studio
	 */
	public EditableEatonLens(
			String description,
			Vector3D centre,
			double radius,
			double ratioNSurfaceNSurrounding,
			double transparentTunnelRadius,
			double transmissionCoefficient,
			boolean shadowThrowing,
			SceneObject parent, 
			Studio studio
		)
	{
		super(description, centre, radius, ratioNSurfaceNSurrounding, transparentTunnelRadius, transmissionCoefficient, shadowThrowing, parent, studio);
	}
	
	/**
	 * @param description
	 * @param centre
	 * @param radius
	 * @param parent
	 * @param studio
	 */
	public EditableEatonLens(
			String description,
			Vector3D centre,
			double radius,
			SceneObject parent, 
			Studio studio
		)
	{
		this(	description,
				centre,
				radius,
				1,	// refractive-index ratio
				0,	// transparent-tunnel radius
				1,	// transmission coefficient
				true,	// shadow-throwing
				parent, studio
			);
	}

	/**
	 * Create a clone of original
	 * @param original
	 */
	public EditableEatonLens(EditableEatonLens original)
	{
		super(original);
	}
	
	/* (non-Javadoc)
	 * @see optics.raytrace.sceneObjects.ParametrisedSphere#clone()
	 */
	@Override
	public EditableEatonLens clone()
	{
		return new EditableEatonLens(this);
	}

	/**
	 * initialise the edit panel
	 */
	@Override
	public void createEditPanel(IPanel iPanel)
	{	
		editPanel = new JPanel();
		editPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Eaton lens"));
		editPanel.setLayout(new MigLayout("insets 0"));
			
		// a text field containing the description
		descriptionPanel = new LabelledStringPanel("Description");
		editPanel.add(descriptionPanel, "wrap");
		
		// add a bit of (non-stretchable) space
		// editPanel.add(Box.createRigidArea(new Dimension(10,5)));
		
		centrePanel = new LabelledVector3DPanel("Centre");
		editPanel.add(centrePanel, "wrap");
		
		// add a bit of (non-stretchable) space
		// editPanel.add(Box.createRigidArea(new Dimension(10,5)));

		radiusPanel = new LabelledDoublePanel("Radius");
		editPanel.add(radiusPanel, "wrap");
		
		// add a bit of (non-stretchable) space
		// editPanel.add(Box.createRigidArea(new Dimension(10,5)));

		ratioNSurfaceNSurroundingPanel = new LabelledDoublePanel("Ratio n_surface / n_surrounding");
		editPanel.add(ratioNSurfaceNSurroundingPanel, "wrap");

		transparentTunnelRadiusPanel = new LabelledDoublePanel("Radius of central transparent tunnel");
		editPanel.add(transparentTunnelRadiusPanel, "wrap");

		// add a bit of (non-stretchable) space
		// editPanel.add(Box.createRigidArea(new Dimension(10,5)));

		transmissionCoefficientPanel = new LabelledDoublePanel("Transmission coefficient");
		editPanel.add(transmissionCoefficientPanel);

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
		centrePanel.setVector3D(getCentre());
		radiusPanel.setNumber(getRadius());
		ratioNSurfaceNSurroundingPanel.setNumber(getRatioNSurfaceNSurrounding());
		transparentTunnelRadiusPanel.setNumber(getTransparentTunnelRadius());
		transmissionCoefficientPanel.setNumber(getTransmissionCoefficient());
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.GUI.Editable#acceptValuesInEditPanel()
	 */
	@Override
	public EditableEatonLens acceptValuesInEditPanel()
	{
		setDescription(descriptionPanel.getString());
		setCentre(centrePanel.getVector3D());
		setRadius(radiusPanel.getNumber());
		setRatioNSurfaceNSurrounding(ratioNSurfaceNSurroundingPanel.getNumber());
		setTransparentTunnelRadius(transparentTunnelRadiusPanel.getNumber());
		setTransmissionCoefficient(transmissionCoefficientPanel.getNumber());

		return this;
	}
	
	@Override
	public EditableEatonLens transform(Transformation t)
	{
		return new EditableEatonLens(
				getDescription(),
				t.transformPosition(getCentre()),
				getRadius(),
				getRatioNSurfaceNSurrounding(),
				getTransparentTunnelRadius(),
				getTransmissionCoefficient(),
				isShadowThrowing(),
				getParent(), 
				getStudio()
			);
	}

	@Override
	public void backToFront(IPanelComponent edited) {
	}

}
