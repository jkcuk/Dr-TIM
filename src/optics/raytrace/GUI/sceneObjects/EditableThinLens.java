package optics.raytrace.GUI.sceneObjects;

import javax.swing.JCheckBox;
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
import optics.raytrace.core.SurfacePropertyWithControllableShadow;
import optics.raytrace.core.Transformation;
import optics.raytrace.sceneObjects.ThinLens;


/**
 * An idealised thin lens.
 * 
 * @author Johannes
 *
 */
public class EditableThinLens extends ThinLens implements IPanelComponent
{
	private static final long serialVersionUID = -3525838509755414593L;

	private JPanel editPanel;
	private LabelledStringPanel descriptionPanel;
	private LabelledVector3DPanel centrePanel;
	private LabelledVector3DPanel normalPanel;
	private LabelledDoublePanel radiusPanel;
	private LabelledDoublePanel focalLengthPanel;
	private LabelledDoublePanel transmissionCoefficientPanel;
	private JCheckBox shadowThrowingCheckBox;


	public EditableThinLens(String description, Vector3D centre, Vector3D normal, double radius, double focalLength, double transmissionCoefficient, boolean shadowThrowing, SceneObject parent, Studio studio)
	{
		super(description, centre, normal, radius, focalLength, transmissionCoefficient, shadowThrowing, parent, studio);
	}
	
	/**
	 * Create a clone of original
	 * @param original
	 */
	public EditableThinLens(EditableThinLens original)
	{
		super(original);
	}
	
	/* (non-Javadoc)
	 * @see optics.raytrace.sceneObjects.ParametrisedSphere#clone()
	 */
	@Override
	public EditableThinLens clone()
	{
		return new EditableThinLens(this);
	}

	/**
	 * initialise the edit panel
	 */
	@Override
	public void createEditPanel(IPanel iPanel)
	{	
		editPanel = new JPanel();
		// editPanel.setLayout(new BorderLayout());
		editPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Thin lens"));
		
		editPanel.setLayout(new MigLayout("insets 0"));
			
		// a text field containing the description
		descriptionPanel = new LabelledStringPanel("Description");
		editPanel.add(descriptionPanel, "wrap");
		
		centrePanel = new LabelledVector3DPanel("Centre");
		editPanel.add(centrePanel, "wrap");

		normalPanel = new LabelledVector3DPanel("Normal (direction of optical axis)");
		editPanel.add(normalPanel, "wrap");

		radiusPanel = new LabelledDoublePanel("Radius");
		editPanel.add(radiusPanel, "wrap");
		
		focalLengthPanel = new LabelledDoublePanel("focal length");
		editPanel.add(focalLengthPanel, "wrap");

		transmissionCoefficientPanel = new LabelledDoublePanel("Transmission coefficient");
		editPanel.add(transmissionCoefficientPanel, "wrap");
		
		shadowThrowingCheckBox = new JCheckBox("Shadow-throwing");
		editPanel.add(shadowThrowingCheckBox);

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
		normalPanel.setVector3D(getNormal());
		radiusPanel.setNumber(getRadius());
		focalLengthPanel.setNumber(getFocalLength());
		transmissionCoefficientPanel.setNumber(getTransmissionCoefficient());
		if(getSurfaceProperty() instanceof SurfacePropertyWithControllableShadow)
		{
			shadowThrowingCheckBox.setVisible(true);
			shadowThrowingCheckBox.setSelected(((SurfacePropertyWithControllableShadow)getSurfaceProperty()).isShadowThrowing());
		}
		else
		{
			shadowThrowingCheckBox.setVisible(false);
		}
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.GUI.Editable#acceptValuesInEditPanel()
	 */
	@Override
	public EditableThinLens acceptValuesInEditPanel()
	{
		setDescription(descriptionPanel.getString());
		setCentre(centrePanel.getVector3D());
		setNormal(normalPanel.getVector3D());
		setRadius(radiusPanel.getNumber());
		setFocalLength(focalLengthPanel.getNumber());
		setTransmissionCoefficient(transmissionCoefficientPanel.getNumber());
		if(getSurfaceProperty() instanceof SurfacePropertyWithControllableShadow)
		{
			((SurfacePropertyWithControllableShadow)getSurfaceProperty()).setShadowThrowing(shadowThrowingCheckBox.isSelected());
		}

		return this;
	}
	
	@Override
	public EditableThinLens transform(Transformation t)
	{
		return new EditableThinLens(
				getDescription(),
				t.transformPosition(getCentre()),
				t.transformDirection(getNormal()),
				getRadius(),
				getFocalLength(),
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
