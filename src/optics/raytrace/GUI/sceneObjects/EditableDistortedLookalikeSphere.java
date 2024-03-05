package optics.raytrace.GUI.sceneObjects;

import javax.swing.JComboBox;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import math.Vector3D;
import math.SpaceTimeTransformation.SpaceTimeTransformationType;
import optics.raytrace.GUI.core.IPanel;
import optics.raytrace.GUI.core.IPanelComponent;
import optics.raytrace.GUI.lowLevel.GUIBitsAndBobs;
import optics.raytrace.GUI.lowLevel.LabelledDoublePanel;
import optics.raytrace.GUI.lowLevel.LabelledStringPanel;
import optics.raytrace.GUI.lowLevel.LabelledVector3DPanel;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.Studio;
import optics.raytrace.core.Transformation;
import optics.raytrace.sceneObjects.DistortedLookalikeSphere;
import optics.raytrace.surfaces.DistortedLookalikeSphereSurfaceProperty;


/**
 * A lookalike sphere, distorted such that, from the inside, the world looks relativistically distorted.
 * 
 * @author Johannes
 *
 */
public class EditableDistortedLookalikeSphere extends DistortedLookalikeSphere implements IPanelComponent
{
	private static final long serialVersionUID = -2320321856612042223L;

	private JPanel editPanel;
	private LabelledStringPanel descriptionPanel;
	private LabelledVector3DPanel cameraPositionPanel;
	private LabelledVector3DPanel betaPanel;
	private LabelledDoublePanel lookalikeSphereRadiusPanel;
	private LabelledDoublePanel transmissionCoefficientPanel;
	private JComboBox<SpaceTimeTransformationType> spaceTimeTransformationComboBox;


	/**
	 * @param description
	 * @param cameraPosition
	 * @param beta
	 * @param spaceTimeTransformation
	 * @param lookalikeSphereRadius
	 * @param transmissionCoefficient
	 * @param parent
	 * @param studio
	 */
	public EditableDistortedLookalikeSphere(
			String description,
			Vector3D cameraPosition, 
			Vector3D beta, 
			SpaceTimeTransformationType spaceTimeTransformation, 
			double lookalikeSphereRadius,
			double transmissionCoefficient,
			SceneObject parent,
			Studio studio
		)
	{
		super(description, cameraPosition, beta, spaceTimeTransformation, lookalikeSphereRadius, transmissionCoefficient, parent, studio);
	}
	
	/**
	 * @param description
	 * @param parent
	 * @param studio
	 */
	public EditableDistortedLookalikeSphere(
			String description,
			SceneObject parent, 
			Studio studio
		)
	{
		this(	description,
				Vector3D.O,	// cameraPosition, 
				Vector3D.O,	// beta, 
				SpaceTimeTransformationType.LORENTZ_TRANSFORMATION,	// transformType, 
				1,	// lookalikeSphereRadius,
				1,	// transmissionCoefficient
				parent, studio
			);
	}

	/**
	 * Create a clone of original
	 * @param original
	 */
	public EditableDistortedLookalikeSphere(EditableDistortedLookalikeSphere original)
	{
		this(
				original.description,
				original.getCameraPosition(),
				original.getBeta(),
				original.getTransformType(),
				original.getLookalikeSphereRadius(),
				original.getTransmissionCoefficient(),
				original.getParent(),
				original.getStudio()
			);
	}
	
	/* (non-Javadoc)
	 * @see optics.raytrace.sceneObjects.ParametrisedSphere#clone()
	 */
	@Override
	public EditableDistortedLookalikeSphere clone()
	{
		return new EditableDistortedLookalikeSphere(this);
	}

	/**
	 * initialise the edit panel
	 */
	@Override
	public void createEditPanel(IPanel iPanel)
	{	
		editPanel = new JPanel();
		editPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Distorted lookalike sphere"));
		editPanel.setLayout(new MigLayout("insets 0"));
			
		// a text field containing the description
		descriptionPanel = new LabelledStringPanel("Description");
		editPanel.add(descriptionPanel, "wrap");
		
//		Vector3D cameraPosition, 
//		Vector3D beta, 
//		SpaceTimeTransformationType transformType, 
//		double lookalikeSphereRadius,

		// add a bit of (non-stretchable) space
		// editPanel.add(Box.createRigidArea(new Dimension(10,5)));
		
		cameraPositionPanel = new LabelledVector3DPanel("Camera position");
		editPanel.add(cameraPositionPanel, "wrap");
		
		betaPanel = new LabelledVector3DPanel("Beta");
		editPanel.add(betaPanel, "wrap");

		spaceTimeTransformationComboBox = new JComboBox<SpaceTimeTransformationType>(SpaceTimeTransformationType.values());
		editPanel.add(GUIBitsAndBobs.makeRow("Space-time transformation", spaceTimeTransformationComboBox), "wrap");
		
		// add a bit of (non-stretchable) space
		// editPanel.add(Box.createRigidArea(new Dimension(10,5)));

		lookalikeSphereRadiusPanel = new LabelledDoublePanel("Radius in beta direction");
		editPanel.add(lookalikeSphereRadiusPanel, "wrap");

		transmissionCoefficientPanel = new LabelledDoublePanel("Brightness factor");
		editPanel.add(transmissionCoefficientPanel, "wrap");

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
		cameraPositionPanel.setVector3D(getCameraPosition());
		betaPanel.setVector3D(getBeta()); 
		spaceTimeTransformationComboBox.setSelectedItem(getTransformType());
		lookalikeSphereRadiusPanel.setNumber(getLookalikeSphereRadius());
		transmissionCoefficientPanel.setNumber(getTransmissionCoefficient());
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.GUI.Editable#acceptValuesInEditPanel()
	 */
	@Override
	public EditableDistortedLookalikeSphere acceptValuesInEditPanel()
	{
		setDescription(descriptionPanel.getString());
		setCameraPosition(cameraPositionPanel.getVector3D());
		setBeta(betaPanel.getVector3D());
		setTransformType((SpaceTimeTransformationType)spaceTimeTransformationComboBox.getSelectedItem());
		setLookalikeSphereRadius(lookalikeSphereRadiusPanel.getNumber());

		calculateEllipsoidParameters();

		setTransmissionCoefficient(transmissionCoefficientPanel.getNumber());
		((DistortedLookalikeSphereSurfaceProperty)getSurfaceProperty()).setTransmissionCoefficient(getTransmissionCoefficient());
		
		return this;
	}
	
	@Override
	public EditableDistortedLookalikeSphere transform(Transformation t)
	{
		return new EditableDistortedLookalikeSphere(
				getDescription(),
				t.transformPosition(getCameraPosition()),	// cameraPosition, 
				t.transformDirection(getBeta()),	// beta, 
				getTransformType(),	// spaceTimeTransformation, 
				getLookalikeSphereRadius(),
				getTransmissionCoefficient(),
				getParent(), 
				getStudio()
			);
	}

	@Override
	public void backToFront(IPanelComponent edited) {
	}

}
