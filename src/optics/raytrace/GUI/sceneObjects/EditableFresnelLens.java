package optics.raytrace.GUI.sceneObjects;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import math.*;
import math.geometry.ShapeWithRandomPointAndBoundary;
import optics.raytrace.GUI.core.IPanel;
import optics.raytrace.GUI.lowLevel.GUIBitsAndBobs;
import optics.raytrace.GUI.lowLevel.LabelledDoublePanel;
import optics.raytrace.GUI.lowLevel.LabelledIntPanel;
import optics.raytrace.GUI.lowLevel.LabelledStringPanel;
import optics.raytrace.GUI.lowLevel.LabelledVector3DPanel;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.Studio;
import optics.raytrace.sceneObjects.FresnelLens;

/**
 * An editable Fresnel lens with refractive index <i>refractiveIndex</i> that images a pair of conjugate points into each other, stigmatically.
 * One of these positions is in "front" space (i.e. in the space of light rays travelling in front of the lens), the other in "back" space.
 * The lens is centred around a central plane, given by the <i>lensCentre</i> and the <i>forwardsCentralPlaneNormal</i>, and has a given
 * (approximate) thickness and minimum separation between the two sides.
 * It comprises two <i>FresnelLensSurface</i> objects.
 * 
 * @author Johannes
 */
public class EditableFresnelLens extends FresnelLens implements ActionListener
{
	private static final long serialVersionUID = -91726655446517194L;

	// GUI panels
	protected LabelledVector3DPanel lensCentrePanel, forwardsCentralPlaneNormalPanel, frontConjugatePointPanel, backConjugatePointPanel;
	protected LabelledDoublePanel refractiveIndexPanel, thicknessPanel, minimumSurfaceSeparationPanel, transmissionCoefficientPanel, focalLengthMinFrontPanel, focalLengthMaxFrontPanel, focalLengthMinBackPanel, focalLengthMaxBackPanel;
//	protected LabelledIntPanel numberOfLensSectionsPanel;
	protected JButton convertButton;
	protected JCheckBox makeStepSurfacesBlackCheckBox;
	
	
	//
	// constructors
	//
	/** Constructor with min and max focal lengths on either side set manually
	 * @param description
	 * @param lensCentre
	 * @param forwardsCentralPlaneNormal
	 * @param frontConjugatePoint
	 * @param backConjugatePoint
	 * @param refractiveIndex
	 * @param thickness
	 * @param minimumSurfaceSeparation
	 * @param focalLengthMinFront
	 * @param focalLengthMaxFront
	 * @param focalLengthMinBack
	 * @param focalLengthMaxBack
	 * @param numberOfLensSections
	 * @param makeStepSurfacesBlack
	 * @param transmissionCoefficient
	 * @param parent
	 * @param studio
	 */
	public EditableFresnelLens(
			String description,
			Vector3D lensCentre,
			Vector3D forwardsCentralPlaneNormal,
			Vector3D frontConjugatePoint,
			Vector3D backConjugatePoint,
			double refractiveIndex,
			double thickness,
			double minimumSurfaceSeparation,
			double focalLengthMinFront,
			double focalLengthMaxFront,
			double focalLengthMinBack,
			double focalLengthMaxBack,
			boolean makeStepSurfacesBlack,
			double transmissionCoefficient,
			SceneObject parent,
			Studio studio
		)
	{
		super(description, lensCentre, forwardsCentralPlaneNormal, frontConjugatePoint, backConjugatePoint, refractiveIndex, thickness, minimumSurfaceSeparation,
				focalLengthMinFront, focalLengthMaxFront, focalLengthMinBack, focalLengthMaxBack, makeStepSurfacesBlack, transmissionCoefficient, parent, studio);
	}
	
	/**
	 * Constructor where focal lengths are set using @param apertureShape
	 * @param description
	 * @param lensCentre
	 * @param forwardsCentralPlaneNormal
	 * @param frontConjugatePoint
	 * @param backConjugatePoint
	 * @param refractiveIndex
	 * @param thickness
	 * @param minimumSurfaceSeparation
	 * @param apertureShape
	 * @param makeStepSurfacesBlack
	 * @param transmissionCoefficient
	 * @param parent
	 * @param studio
	 */
	public EditableFresnelLens(
			String description,
			Vector3D lensCentre,
			Vector3D forwardsCentralPlaneNormal,
			Vector3D frontConjugatePoint,
			Vector3D backConjugatePoint,
			double refractiveIndex,
			double thickness,
			double minimumSurfaceSeparation,
			ShapeWithRandomPointAndBoundary apertureShape,
			boolean makeStepSurfacesBlack,
			double transmissionCoefficient,
			SceneObject parent,
			Studio studio
		)
	{
		super(description, lensCentre, forwardsCentralPlaneNormal, frontConjugatePoint, backConjugatePoint, refractiveIndex, thickness, minimumSurfaceSeparation,
				apertureShape, makeStepSurfacesBlack, transmissionCoefficient, parent, studio);
	}

	
	/**
	 * Backwards compatible constructor with the number of lens sections being specified
	 * @param description
	 * @param lensCentre
	 * @param forwardsCentralPlaneNormal
	 * @param frontConjugatePoint
	 * @param backConjugatePoint
	 * @param refractiveIndex
	 * @param thickness
	 * @param minimumSurfaceSeparation
	 * @param focalLengthMinFront
	 * @param focalLengthMaxFront
	 * @param focalLengthMinBack
	 * @param focalLengthMaxBack
	 * @param makeStepSurfacesBlack
	 * @param transmissionCoefficient
	 * @param parent
	 * @param studio
	 */
	public EditableFresnelLens(
			String description,
			Vector3D lensCentre,
			Vector3D forwardsCentralPlaneNormal,
			Vector3D frontConjugatePoint,
			Vector3D backConjugatePoint,
			double refractiveIndex,
			double thickness,
			double minimumSurfaceSeparation,
			int numberOfLensSections,
			boolean makeStepSurfacesBlack,
			double transmissionCoefficient,
			SceneObject parent,
			Studio studio
		)
	{
		super(description, lensCentre, forwardsCentralPlaneNormal, frontConjugatePoint, backConjugatePoint, refractiveIndex, thickness, minimumSurfaceSeparation,
				numberOfLensSections, makeStepSurfacesBlack, transmissionCoefficient, parent, studio);
	}


	/**
	 * Default constructor
	 * @param parent
	 * @param studio
	 */
	public EditableFresnelLens(SceneObject parent, Studio studio)
	{
		super(parent, studio);
	}

	/**
	 * Create a clone of original
	 * @param original
	 */
	public EditableFresnelLens(EditableFresnelLens original)
	{
		super(original);
	}

	@Override
	public EditableFresnelLens clone()
	{
		return new EditableFresnelLens(this);
	}

	

	//
	// GUI stuff
	//
	
	/**
	 * initialise the edit panel
	 */
	@Override
	public void createEditPanel(IPanel iPanel)
	{	
		this.iPanel = iPanel;
		
		editPanel = new JPanel();
		editPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Fresnel lens"));
		editPanel.setLayout(new MigLayout("insets 0"));
		
		// a text field containing the description
		descriptionPanel = new LabelledStringPanel("Description");
		editPanel.add(descriptionPanel, "wrap");

		lensCentrePanel = new LabelledVector3DPanel("Lens centre");
		editPanel.add(lensCentrePanel, "wrap");

		forwardsCentralPlaneNormalPanel = new LabelledVector3DPanel("Forwards normal to central plane");
		editPanel.add(forwardsCentralPlaneNormalPanel, "wrap");

		frontConjugatePointPanel = new LabelledVector3DPanel("Front conjugate point");
		editPanel.add(frontConjugatePointPanel, "wrap");

		backConjugatePointPanel = new LabelledVector3DPanel("Back conjugate point");
		editPanel.add(backConjugatePointPanel, "wrap");

		refractiveIndexPanel = new LabelledDoublePanel("Refractive index");
		editPanel.add(refractiveIndexPanel, "wrap");

		thicknessPanel = new LabelledDoublePanel("Thickness");
		editPanel.add(thicknessPanel, "wrap");

		minimumSurfaceSeparationPanel = new LabelledDoublePanel("Minimum surface separation");
		editPanel.add(minimumSurfaceSeparationPanel, "wrap");

		transmissionCoefficientPanel = new LabelledDoublePanel("Transmission coefficient of each face");
		editPanel.add(transmissionCoefficientPanel, "wrap");
		
		focalLengthMinFrontPanel =  new LabelledDoublePanel("Minimun focal length of the front face");
				editPanel.add(focalLengthMinFrontPanel, "wrap");
		
		focalLengthMaxFrontPanel = new LabelledDoublePanel("Maximum focal length of the front face");
				editPanel.add(focalLengthMaxFrontPanel, "wrap");
		
		focalLengthMinBackPanel = new LabelledDoublePanel("Minimun focal length of the back face");
				editPanel.add(focalLengthMinBackPanel, "wrap");
		
		focalLengthMaxBackPanel = new LabelledDoublePanel("Maximum focal length of the back face");
				editPanel.add(focalLengthMaxBackPanel, "wrap");
		
//		numberOfLensSectionsPanel = new LabelledIntPanel("Number of lens sections per face (approximate)");
//		editPanel.add(numberOfLensSectionsPanel, "wrap");

		makeStepSurfacesBlackCheckBox = new JCheckBox("Make step surfaces black (instead of air/glass interfaces)");
		editPanel.add(makeStepSurfacesBlackCheckBox, "wrap");

		// the convert button

		convertButton = new JButton("Convert to collection of scene objects");
		convertButton.addActionListener(this);
		editPanel.add(convertButton);
		
		// validate the entire edit panel
		editPanel.validate();
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.GUI.Editable#setValuesInEditPanel()
	 */
	@Override
	public void setValuesInEditPanel()
	{
		// initialize any fields
		descriptionPanel.setString(getDescription());

		lensCentrePanel.setVector3D(getLensCentre());
		forwardsCentralPlaneNormalPanel.setVector3D(getForwardsCentralPlaneNormal());
		frontConjugatePointPanel.setVector3D(getFrontConjugatePoint());
		backConjugatePointPanel.setVector3D(getBackConjugatePoint());
		refractiveIndexPanel.setNumber(getRefractiveIndex());
		thicknessPanel.setNumber(getThickness());
		minimumSurfaceSeparationPanel.setNumber(getMinimumSurfaceSeparation());
		transmissionCoefficientPanel.setNumber(getTransmissionCoefficient());
		focalLengthMinFrontPanel.setNumber(getFocalLengthMinFront());
		focalLengthMaxFrontPanel.setNumber(getFocalLengthMaxFront());
		focalLengthMinBackPanel.setNumber(getFocalLengthMinBack());
		focalLengthMaxBackPanel.setNumber(getFocalLengthMaxBack());
//		numberOfLensSectionsPanel.setNumber(getNumberOfLensSections());
		makeStepSurfacesBlackCheckBox.setSelected(isMakeStepSurfacesBlack());
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.GUI.Editable#acceptValuesInEditPanel()
	 */
	@Override
	public EditableFresnelLens acceptValuesInEditPanel()
	{
		setDescription(descriptionPanel.getString());
		setLensCentre(lensCentrePanel.getVector3D());
		setForwardsCentralPlaneNormal(forwardsCentralPlaneNormalPanel.getVector3D());
		setFrontConjugatePoint(frontConjugatePointPanel.getVector3D());
		setBackConjugatePoint(backConjugatePointPanel.getVector3D());
		setRefractiveIndex(refractiveIndexPanel.getNumber());
		setThickness(thicknessPanel.getNumber());
		setMinimumSurfaceSeparation(minimumSurfaceSeparationPanel.getNumber());
		setTransmissionCoefficient(transmissionCoefficientPanel.getNumber());
		setFocalLengthMinFront(focalLengthMinFrontPanel.getNumber());
		setFocalLengthMaxFront(focalLengthMaxFrontPanel.getNumber());
		setFocalLengthMinBack(focalLengthMinBackPanel.getNumber());
		setFocalLengthMaxBack(focalLengthMaxBackPanel.getNumber());
//		setNumberOfLensSections(numberOfLensSectionsPanel.getNumber());
		setMakeStepSurfacesBlack(makeStepSurfacesBlackCheckBox.isSelected());

		// add the objects
		populateSceneObjectCollection();
		
		return this;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		acceptValuesInEditPanel();	// accept any changes
		EditableSceneObjectCollection container = new EditableSceneObjectCollection(this);
		iPanel.replaceFrontComponent(container, "Edit ex-Fresnel-lens");
		container.setValuesInEditPanel();
	}
}