package optics.raytrace.GUI.sceneObjects;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

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
import optics.raytrace.sceneObjects.Glens;


/**
 * A gLens.
 * 
 * @author Johannes
 *
 */
public class EditableGlens extends Glens implements IPanelComponent
{
	private static final long serialVersionUID = -3525838509755414593L;

	private JPanel editPanel;
	private LabelledStringPanel descriptionPanel;
	private LabelledVector3DPanel apertureCentrePanel, nodalDirectionPanel;
	private LabelledVector3DPanel opticalAxisDirectionPosPanel;
	private LabelledDoublePanel apertureRadiusPanel;
	private LabelledDoublePanel focalLengthNegPanel, focalLengthPosPanel;
	private LabelledDoublePanel fNegOverFPosPanel;
	private LabelledDoublePanel transmissionCoefficientPanel;
	private JCheckBox shadowThrowingCheckBox;
	private JTabbedPane homogeneousInhomogeneousTabbedPane;
	private JPanel inhomogeneousPanel, homogeneousPanel;


	
	/**
	 * Creates an instance of an editable circular glens.
	 * All parameters are explicitly given exactly as they are being stored.
	 * 
	 * @param description
	 * @param opticalAxisDirectionPos
	 * @param principalPoint
	 * @param meanF
	 * @param nodalPointF
	 * @param focalLengthNegF
	 * @param focalLengthPosF
	 * @param apertureRadius
	 * @param transmissionCoefficient
	 * @param shadowThrowing
	 * @param parent
	 * @param studio
	 */
	public EditableGlens(
			String description,
			Vector3D opticalAxisDirectionPos,
			Vector3D principalPoint,	// not necessarily the principal point!
			double meanF,
			Vector3D nodalPointF,
			double focalLengthNegF,
			double focalLengthPosF,
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
				meanF,
				nodalPointF,
				focalLengthNegF,
				focalLengthPosF,
				apertureRadius,
				transmissionCoefficient,
				shadowThrowing,
				parent,
				studio
			);
	}

	/**
	 * Creates an instance of an editable circular glens.
	 * This constructor is suitable for glenses with finite focal lengths.
	 * 
	 * @param description
	 * @param principalPoint
	 * @param opticalAxisDirectionPos
	 * @param apertureRadius
	 * @param focalLengthNeg
	 * @param focalLengthPos
	 * @param transmissionCoefficient
	 * @param shadowThrowing
	 * @param parent
	 * @param studio
	 */
	public EditableGlens(
			String description,
			Vector3D principalPoint,
			Vector3D opticalAxisDirectionPos,
			double apertureRadius,
			double focalLengthNeg,
			double focalLengthPos,
			double transmissionCoefficient,
			boolean shadowThrowing,
			SceneObject parent,
			Studio studio
		)
	{
		super(description, principalPoint, opticalAxisDirectionPos, apertureRadius, focalLengthNeg, focalLengthPos, transmissionCoefficient, shadowThrowing, parent, studio);
	}
	
	public EditableGlens(SceneObject parent, Studio studio)
	{
		this(
				"glens",	// description
				new Vector3D(0, 0, 5),	// centre
				new Vector3D(0, 0, 1),	// opticalAxisDirectionPos
				1,	// aperture radius
				-2.5,	// focalLengthNeg
				2.5,	// focalLengthPos
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
	public EditableGlens(EditableGlens original)
	{
		super(original);
	}
	
	/* (non-Javadoc)
	 * @see optics.raytrace.sceneObjects.ParametrisedSphere#clone()
	 */
	@Override
	public EditableGlens clone()
	{
		return new EditableGlens(this);
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

		apertureCentrePanel = new LabelledVector3DPanel("Aperture centre");
		editPanel.add(apertureCentrePanel, "wrap");

		homogeneousInhomogeneousTabbedPane = new JTabbedPane();
		
//		homogeneousCheckBox = new JCheckBox("Homogeneous");
//		homogeneousCheckBox.addActionListener(this);
//		editPanel.add(homogeneousCheckBox, "wrap");
		
		inhomogeneousPanel = new JPanel(new MigLayout("insets 0"));
		homogeneousPanel = new JPanel(new MigLayout("insets 0"));
		
		homogeneousInhomogeneousTabbedPane.add("Inhomogeneous", inhomogeneousPanel);
		homogeneousInhomogeneousTabbedPane.add("Homogeneous", homogeneousPanel);
		
		inhomogeneousPanel.add(new JLabel("(Aperture centre is principal point)"), "wrap");
		
		nodalDirectionPanel = new LabelledVector3DPanel("Nodal direction");
		nodalDirectionPanel.setVector3D(new Vector3D(0, 0, 1));	// set it to some default value
		homogeneousPanel.add(nodalDirectionPanel, "wrap");
		
		focalLengthNegPanel = new LabelledDoublePanel("Focal length in -ve space");
		focalLengthNegPanel.setNumber(-1);
		inhomogeneousPanel.add(focalLengthNegPanel, "wrap");

		focalLengthPosPanel = new LabelledDoublePanel("Focal length in +ve space");
		focalLengthPosPanel.setNumber(1);
		inhomogeneousPanel.add(focalLengthPosPanel, "wrap");

		fNegOverFPosPanel = new LabelledDoublePanel("f-/f+");
		fNegOverFPosPanel.setNumber(-1);
		homogeneousPanel.add(fNegOverFPosPanel, "wrap");

		editPanel.add(homogeneousInhomogeneousTabbedPane, "wrap");
		
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
		
		// is the glens inhomogeneous or homogeneous?
		if(Math.abs(getMeanF()) == Double.POSITIVE_INFINITY)
		{
			// the glens is homogeneous
			
			homogeneousInhomogeneousTabbedPane.setSelectedComponent(homogeneousPanel);

			// set the nodal direction and the focal-length ratio according to the variables
			nodalDirectionPanel.setVector3D(getNodalPointF());
			fNegOverFPosPanel.setNumber(getFocalLengthNegF()/getFocalLengthPosF());
			
			// set the focal lengths to some standard values
			// focalLengthNegPanel.setNumber(-1);
			// focalLengthPosPanel.setNumber(1);
		}
		else
		{
			// the glens is inhomogeneous
			
			homogeneousInhomogeneousTabbedPane.setSelectedComponent(inhomogeneousPanel);

			// set the focal lengths according to the variables
			focalLengthNegPanel.setNumber(getFocalLengthNegF()*getMeanF());
			focalLengthPosPanel.setNumber(getFocalLengthPosF()*getMeanF());
			
			// set the nodal direction and the focal-length ratio to some standard values
			// nodalDirectionPanel.setVector3D(getOpticalAxisDirectionPos());
			// fNegOverFPosPanel.setNumber(-1);
		}
		
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
	public EditableGlens acceptValuesInEditPanel()
	{
		setDescription(descriptionPanel.getString());
		if(homogeneousInhomogeneousTabbedPane.getSelectedComponent().equals(inhomogeneousPanel))
		{
			// the inhomogeneous panel has been selected
			setParametersForInhomogeneousGlens(
					opticalAxisDirectionPosPanel.getVector3D(),
					apertureCentrePanel.getVector3D(),	// principal point
					focalLengthNegPanel.getNumber(), 
					focalLengthPosPanel.getNumber()
				);
		}
		else
		{
			// the homogeneous panel is selected
			setParametersForHomogeneousGlens(
					opticalAxisDirectionPosPanel.getVector3D(),
					apertureCentrePanel.getVector3D(),
					nodalDirectionPanel.getVector3D(),
					fNegOverFPosPanel.getNumber()
				);
		}
		
		setRadius(apertureRadiusPanel.getNumber());
		setTransmissionCoefficient(transmissionCoefficientPanel.getNumber());
		if(getSurfaceProperty() instanceof SurfacePropertyWithControllableShadow)
		{
			((SurfacePropertyWithControllableShadow)getSurfaceProperty()).setShadowThrowing(shadowThrowingCheckBox.isSelected());
		}

		return this;
	}
	
	@Override
	public EditableGlens transform(Transformation t)
	{
		Vector3D newNodalPointF;
		if(Math.abs(getMeanF()) == Double.POSITIVE_INFINITY)
		{
			// homogeneous glens; nodalPointF is basically a direction -- don't care about offsets
			newNodalPointF = t.transformDirection(getNodalPointF());
		}
		else
		{
			// inhomogeneous glens; nodalPointF*F is a position
			newNodalPointF = t.transformPosition(getNodalPointF().getProductWith(getMeanF())).getProductWith(1/getMeanF());
		}
		
		return new EditableGlens(
				getDescription(),
				t.transformDirection(getOpticalAxisDirectionPos()),
				t.transformPosition(getPrincipalPoint()),
				getMeanF(),
				newNodalPointF,
				getFocalLengthNegF(),
				getFocalLengthPosF(),
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
