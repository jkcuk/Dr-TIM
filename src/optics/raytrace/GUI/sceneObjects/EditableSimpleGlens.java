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
import optics.raytrace.core.SurfacePropertyPrimitive;
import optics.raytrace.core.SurfacePropertyWithControllableShadow;
import optics.raytrace.core.Transformation;
import optics.raytrace.sceneObjects.SimpleGlens;


/**
 * A gLens.
 * 
 * @author Johannes
 *
 */
public class EditableSimpleGlens extends SimpleGlens implements IPanelComponent
{
	private static final long serialVersionUID = 6823870472608076036L;

	private JPanel editPanel;
	private LabelledStringPanel descriptionPanel;
	private LabelledVector3DPanel apertureCentrePanel;
	private LabelledVector3DPanel opticalAxisDirectionPosPanel;
	private LabelledDoublePanel apertureRadiusPanel;
	private LabelledDoublePanel focalLengthNegPanel, focalLengthPosPanel;
	private LabelledDoublePanel transmissionCoefficientPanel;
	private JCheckBox shadowThrowingCheckBox;


	
	/**
	 * Creates an instance of an editable circular glens.
	 * All parameters are explicitly given exactly as they are being stored.
	 * 
	 * @param description
	 * @param opticalAxisDirectionPos
	 * @param principalPoint
	 * @param nodalPoint
	 * @param focalLengthNeg
	 * @param focalLengthPos
	 * @param apertureRadius
	 * @param transmissionCoefficient
	 * @param shadowThrowing
	 * @param parent
	 * @param studio
	 */
	public EditableSimpleGlens(
			String description,
			Vector3D opticalAxisDirectionPos,
			Vector3D principalPoint,	// not necessarily the principal point!
			Vector3D nodalPoint,
			double focalLengthNeg,
			double focalLengthPos,
			double apertureRadius,
			double transmissionCoefficient,
			boolean shadowThrowing,
			SceneObject parent,
			Studio studio
		)
	{
		super(
				description,
				opticalAxisDirectionPos,
				principalPoint,
				nodalPoint,
				focalLengthNeg,
				focalLengthPos,
				apertureRadius,
				transmissionCoefficient,
				shadowThrowing,
				parent,
				studio
			);
	}
	
	public EditableSimpleGlens(SceneObject parent, Studio studio)
	{
		this(
				"simple glens",	// description
				new Vector3D(0, 0, 1),	// opticalAxisDirectionPos
				new Vector3D(0, 0, 5),	// centre
				new Vector3D(0, 0, 5),	// nodal point
				-2.5,	// focalLengthNeg
				2.5,	// focalLengthPos
				1,	// aperture radius
				SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT,	// transmissionCoefficient
				true,	// shadowThrowing
				parent,
				studio
		);
	}
	
	/**
	 * Create a clone of original
	 * @param original
	 */
	public EditableSimpleGlens(EditableSimpleGlens original)
	{
		super(original);
	}
	
	/* (non-Javadoc)
	 * @see optics.raytrace.sceneObjects.ParametrisedSphere#clone()
	 */
	@Override
	public EditableSimpleGlens clone()
	{
		return new EditableSimpleGlens(this);
	}

	/**
	 * initialise the edit panel
	 */
	@Override
	public void createEditPanel(IPanel iPanel)
	{	
		editPanel = new JPanel();
		// editPanel.setLayout(new BorderLayout());
		editPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Glens"));
		
		editPanel.setLayout(new MigLayout("insets 0"));
			
		// a text field containing the description
		descriptionPanel = new LabelledStringPanel("Description");
		editPanel.add(descriptionPanel, "wrap");
		
		opticalAxisDirectionPosPanel = new LabelledVector3DPanel("Direction of optical axis, in positive direction");
		editPanel.add(opticalAxisDirectionPosPanel, "wrap");

		apertureCentrePanel = new LabelledVector3DPanel("Aperture centre & principal point");
		editPanel.add(apertureCentrePanel, "wrap");
		
		focalLengthNegPanel = new LabelledDoublePanel("Focal length in -ve space");
		focalLengthNegPanel.setNumber(-1);
		editPanel.add(focalLengthNegPanel, "wrap");

		focalLengthPosPanel = new LabelledDoublePanel("Focal length in +ve space");
		focalLengthPosPanel.setNumber(1);
		editPanel.add(focalLengthPosPanel, "wrap");

		apertureRadiusPanel = new LabelledDoublePanel("Aperture radius");
		editPanel.add(apertureRadiusPanel, "wrap");
		
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
		apertureCentrePanel.setVector3D(getCentre());
		opticalAxisDirectionPosPanel.setVector3D(getOpticalAxisDirectionPos());
		apertureRadiusPanel.setNumber(getRadius());
		
		// the glens is inhomogeneous

			// set the focal lengths according to the variables
			focalLengthNegPanel.setNumber(getFocalLengthNeg());
			focalLengthPosPanel.setNumber(getFocalLengthPos());
			
			// set the nodal direction and the focal-length ratio to some standard values
			// nodalDirectionPanel.setVector3D(getOpticalAxisDirectionPos());
			// fNegOverFPosPanel.setNumber(-1);
		
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
	public EditableSimpleGlens acceptValuesInEditPanel()
	{
		setDescription(descriptionPanel.getString());

		setParameters(
				opticalAxisDirectionPosPanel.getVector3D(),
				apertureCentrePanel.getVector3D(),	// principal point
				focalLengthNegPanel.getNumber(), 
				focalLengthPosPanel.getNumber()
				);
		
		setRadius(apertureRadiusPanel.getNumber());
		setTransmissionCoefficient(transmissionCoefficientPanel.getNumber());
		if(getSurfaceProperty() instanceof SurfacePropertyWithControllableShadow)
		{
			((SurfacePropertyWithControllableShadow)getSurfaceProperty()).setShadowThrowing(shadowThrowingCheckBox.isSelected());
		}

		return this;
	}
	
	@Override
	public EditableSimpleGlens transform(Transformation t)
	{		
		return new EditableSimpleGlens(
				getDescription(),
				t.transformDirection(getOpticalAxisDirectionPos()),
				t.transformPosition(getPrincipalPoint()),
				t.transformPosition(getNodalPoint()),
				getFocalLengthNeg(),
				getFocalLengthPos(),
				getRadius(),
				getTransmissionCoefficient(),
				isShadowThrowing(),
				getParent(), 
				getStudio()
			);
	}

	@Override
	public void backToFront(IPanelComponent edited) {
	}

//	@Override
//	public void actionPerformed(ActionEvent e)
//	{
//		System.out.println("Action!");
//		if(e.getSource().equals(homogeneousCheckBox))
//		{
//			showRelevantPanels();
//		}
//	}

//	@Override
//	public void stateChanged(ChangeEvent e)
//	{
//		System.out.println("Action!");
//		if(e.getSource().equals(homogeneousCheckBox))
//		{
//			showRelevantPanels();
//		}
//	}

}
