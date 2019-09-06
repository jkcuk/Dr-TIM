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
import optics.raytrace.sceneObjects.ComplexThinLens;


/**
 * An idealised complex thin lens.
 * 
 * @author Johannes
 *
 */
public class EditableComplexThinLens extends ComplexThinLens implements IPanelComponent
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1458441507762179936L;

	private JPanel editPanel;
	private LabelledStringPanel descriptionPanel;
	private LabelledVector3DPanel centrePanel, normalPanel;
	private LabelledDoublePanel radiusPanel;
	private LabelledDoublePanel anglefactorPanel;
	private LabelledDoublePanel transmissionCoefficientPanel;
	private JCheckBox shadowThrowingCheckBox;

	
	public EditableComplexThinLens(SceneObject parent, Studio studio)
	{
		this(
				"Complex thin lens",	// description
				new Vector3D(0, 0, 5), // centre
				new Vector3D(0, 0, 1),	// normal
				1, // radius
				0, // angle factor
				0.96,	// transmission coefficient
				true, // shadow-throwing?
				parent, studio
		);
	}

	public EditableComplexThinLens(String description, Vector3D centre, Vector3D normal, double radius, double angleFactor, double transmissionCoefficient, boolean shadowThrowing, SceneObject parent, Studio studio)
	{
		super(description, centre, normal, radius, angleFactor, transmissionCoefficient, shadowThrowing, parent, studio);
	}
	
	/**
	 * Create a clone of original
	 * @param original
	 */
	public EditableComplexThinLens(EditableComplexThinLens original)
	{
		super(original);
	}
	
	/* (non-Javadoc)
	 * @see optics.raytrace.sceneObjects.ParametrisedSphere#clone()
	 */
	@Override
	public EditableComplexThinLens clone()
	{
		return new EditableComplexThinLens(this);
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

		normalPanel = new LabelledVector3DPanel("Normal");
		editPanel.add(normalPanel, "wrap");

		radiusPanel = new LabelledDoublePanel("Radius");
		editPanel.add(radiusPanel, "wrap");
		
		anglefactorPanel = new LabelledDoublePanel("Angle Factor");
		editPanel.add(anglefactorPanel, "wrap");

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
		centrePanel.setVector3D(getLensCentre());
		normalPanel.setVector3D(getNormal());
		radiusPanel.setNumber(getRadius());
		anglefactorPanel.setNumber(getAngleFactor());
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
	public EditableComplexThinLens acceptValuesInEditPanel()
	{
		setDescription(descriptionPanel.getString());
		setLensCentre(centrePanel.getVector3D());
		setNormal(normalPanel.getVector3D());
		setRadius(radiusPanel.getNumber());
		setAngleFactor(anglefactorPanel.getNumber());
		setTransmissionCoefficient(transmissionCoefficientPanel.getNumber());
		if(getSurfaceProperty() instanceof SurfacePropertyWithControllableShadow)
		{
			((SurfacePropertyWithControllableShadow)getSurfaceProperty()).setShadowThrowing(shadowThrowingCheckBox.isSelected());
		}

		return this;
	}
	
	@Override
	public EditableComplexThinLens transform(Transformation t)
	{
		return new EditableComplexThinLens(
				getDescription(),
				t.transformPosition(getLensCentre()),
				t.transformDirection(getNormal()),
				getRadius(),
				getAngleFactor(),
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
