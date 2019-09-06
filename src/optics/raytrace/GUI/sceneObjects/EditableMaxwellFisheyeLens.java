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
import optics.raytrace.sceneObjects.MaxwellFisheyeLens;


/**
 * A Maxwell fisheye "lens", i.e. the spherical inner part of a Maxwell fisheye of a radius such that the surface
 * of the lens is imaged onto itself.
 * 
 * @author Johannes
 *
 */
public class EditableMaxwellFisheyeLens extends MaxwellFisheyeLens implements IPanelComponent
{
	private static final long serialVersionUID = -5863022549687054951L;

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
	public EditableMaxwellFisheyeLens(
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
	public EditableMaxwellFisheyeLens(
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
				true, // shadow-throwing
				parent, studio
			);
	}

	/**
	 * Create a clone of original
	 * @param original
	 */
	public EditableMaxwellFisheyeLens(EditableMaxwellFisheyeLens original)
	{
		super(original);
	}
	
	/* (non-Javadoc)
	 * @see optics.raytrace.sceneObjects.ParametrisedSphere#clone()
	 */
	@Override
	public EditableMaxwellFisheyeLens clone()
	{
		return new EditableMaxwellFisheyeLens(this);
	}

	/**
	 * initialise the edit panel
	 */
	@Override
	public void createEditPanel(IPanel iPanel)
	{	
		editPanel = new JPanel();
		// editPanel.setLayout(new BorderLayout());
		editPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Maxwell fisheye lens"));
		
		editPanel.setLayout(new MigLayout("insets 0"));
			
		// a text field containing the description
		descriptionPanel = new LabelledStringPanel("Description");
		editPanel.add(descriptionPanel, "wrap");
		
		centrePanel = new LabelledVector3DPanel("Centre");
		editPanel.add(centrePanel, "wrap");
		
		radiusPanel = new LabelledDoublePanel("Radius");
		editPanel.add(radiusPanel, "wrap");
		
		ratioNSurfaceNSurroundingPanel = new LabelledDoublePanel("Ratio n_surface / n_surrounding");
		editPanel.add(ratioNSurfaceNSurroundingPanel, "wrap");

		transparentTunnelRadiusPanel = new LabelledDoublePanel("Radius of central transparent tunnel");
		editPanel.add(transparentTunnelRadiusPanel, "wrap");

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
	public EditableMaxwellFisheyeLens acceptValuesInEditPanel()
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
	public EditableMaxwellFisheyeLens transform(Transformation t)
	{
		return new EditableMaxwellFisheyeLens(
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
