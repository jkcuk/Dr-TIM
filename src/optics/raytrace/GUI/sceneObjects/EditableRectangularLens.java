package optics.raytrace.GUI.sceneObjects;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import math.Vector3D;
import net.miginfocom.swing.MigLayout;
import optics.raytrace.GUI.core.IPanel;
import optics.raytrace.GUI.lowLevel.GUIBitsAndBobs;
import optics.raytrace.GUI.lowLevel.LabelledDoublePanel;
import optics.raytrace.GUI.lowLevel.LabelledStringPanel;
import optics.raytrace.GUI.lowLevel.LabelledVector3DPanel;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.Studio;
import optics.raytrace.core.SurfacePropertyPrimitive;
import optics.raytrace.sceneObjects.LensType;
import optics.raytrace.surfaces.IdealThinLensSurfaceSimple;
import optics.raytrace.surfaces.PhaseHologramOfLens;

/**
 * A rectangle whose surface area is a lens array
 * @author johannes
 */
public class EditableRectangularLens 
extends EditableScaledParametrisedCentredParallelogram
{
	private static final long serialVersionUID = 7357161369137473790L;

	private double focalLength;
	private Vector3D principalPoint;
	private LensType lensType;
	private double throughputCoefficient;
	private boolean shadowThrowing;

	
	public EditableRectangularLens(
			String description,
			Vector3D clearApertureCentre, 
			Vector3D spanVector1,
			Vector3D spanVector2, 
			Vector3D principalPoint,
			double focalLength,
			LensType lensType,
			double throughputCoefficient,
			boolean shadowThrowing,
			SceneObject parent,
			Studio studio
		)
	{
		super(parent, studio);
		
		setDescription(description);
		setCentreAndSpanVectors(
				clearApertureCentre,	// centre
				spanVector1,
				spanVector2
			);

		this.principalPoint = principalPoint;
		this.focalLength = focalLength;
		this.lensType = lensType;
		this.throughputCoefficient = throughputCoefficient;
		this.shadowThrowing = shadowThrowing;

		setSurfaceProperty();
	}
	
	
	/**
	 * A lens whose clear-aperture centre coincides with its principal point
	 * @param description
	 * @param centre
	 * @param spanVector1
	 * @param spanVector2
	 * @param focalLength
	 * @param lensType
	 * @param throughputCoefficient
	 * @param shadowThrowing
	 * @param parent
	 * @param studio
	 */
	public EditableRectangularLens(
			String description,
			Vector3D centre, 
			Vector3D spanVector1,
			Vector3D spanVector2, 
			double focalLength,
			LensType lensType,
			double throughputCoefficient,
			boolean shadowThrowing,
			SceneObject parent,
			Studio studio
		)
	{
		this(
				description,
				centre,	// clear-aperture centre
				spanVector1,
				spanVector2,
				centre,	// principal point
				focalLength,
				lensType,
				throughputCoefficient,
				shadowThrowing,
				parent,
				studio
			);
	}

	
	
	public EditableRectangularLens(
			SceneObject parent,
			Studio studio
		)
	{
		this(
				"Lens",	// description
				new Vector3D(0, 0, 10),	// clear-aperture centre and principal point
				new Vector3D(1, 0, 0),	// spanVector1
				new Vector3D(0, 1, 0),	// spanVector2
				1,	// focalLength
				LensType.IDEAL_THIN_LENS,
				SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT,	// throughputCoefficient
				true,	// shadowThrowing
				parent,
				studio
			);
	}

	public EditableRectangularLens(EditableRectangularLens original)
	{
		this(
				original.getDescription(),
				original.getClearApertureCentre(), 
				original.getSpanVector1(),
				original.getSpanVector2(), 
				original.getPrincipalPoint(),
				original.getFocalLength(),
				original.getLensType(),
				original.getThroughputCoefficient(),
				original.isShadowThrowing(),
				original.getParent(),
				original.getStudio()
			);
	}
	
	
	/* (non-Javadoc)
	 * @see optics.raytrace.sceneObjects.ParametrisedPlane#clone()
	 */
	@Override
	public EditableRectangularLens clone()
	{
		return new EditableRectangularLens(this);
	}


	
	// getters & setters
	
	public Vector3D getClearApertureCentre() {
		return getCentre();
	}
		
	public double getFocalLength() {
		return focalLength;
	}


	public void setFocalLength(double focalLength) {
		this.focalLength = focalLength;
	}


	public Vector3D getPrincipalPoint() {
		return principalPoint;
	}


	public void setPrincipalPoint(Vector3D principalPoint) {
		this.principalPoint = principalPoint;
	}


	public LensType getLensType() {
		return lensType;
	}


	public void setLensType(LensType lensType) {
		this.lensType = lensType;
	}


	public double getThroughputCoefficient() {
		return throughputCoefficient;
	}


	public void setThroughputCoefficient(double throughputCoefficient) {
		this.throughputCoefficient = throughputCoefficient;
	}


	public boolean isShadowThrowing() {
		return shadowThrowing;
	}


	public void setShadowThrowing(boolean shadowThrowing) {
		this.shadowThrowing = shadowThrowing;
	}


	/**
	 * set the surface property according to the parameters
	 */
	public void setSurfaceProperty()
	{
		switch(lensType)
		{
		case PHASE_HOLOGRAM_OF_LENS:
			setSurfaceProperty(new PhaseHologramOfLens(
					focalLength,
					principalPoint,
					throughputCoefficient,
					false,	// reflective
					shadowThrowing
				));
			break;
		case IDEAL_THIN_LENS:
		default:
			setSurfaceProperty(new IdealThinLensSurfaceSimple(
					principalPoint,	// lensCentre,
					Vector3D.crossProduct(getSpanVector1(), getSpanVector2()),	// opticalAxisDirection,
					focalLength,
					throughputCoefficient,	// transmissionCoefficient,
					shadowThrowing
				));
		}
	}


	
	/**
	 * 
	 * Editable-interface stuff
	 * 
	 */
	
	/**
	 * variables
	 */
	private LabelledVector3DPanel principalPointPanel;
	private LabelledDoublePanel focalLengthPanel, transmissionCoefficientPanel;
	private JCheckBox shadowThrowingCheckBox;
	private JComboBox<LensType> lensTypeComboBox;


	
	/**
	 * initialise the edit panel
	 */
	@Override
	public void createEditPanel(IPanel iPanel)
	{	
		editPanel = new JPanel();
		editPanel.setLayout(new MigLayout("insets 0"));
		editPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Rectangular lens"));
		
		//
		// the basic-parameters panel
		//
		
		// a text field containing the description
		descriptionPanel = new LabelledStringPanel("Description");
		editPanel.add(descriptionPanel, "wrap");
				
		centrePanel = new LabelledVector3DPanel("Centre");
		editPanel.add(centrePanel, "wrap");

		widthVectorPanel = new LabelledVector3DPanel("Vector along width (u direction)");
		editPanel.add(widthVectorPanel, "wrap");

		heightVectorPanel = new LabelledVector3DPanel("Vector along height (v direction)");
		editPanel.add(heightVectorPanel, "wrap");

		//
		// the lens parameters
		//
		
		focalLengthPanel = new LabelledDoublePanel("Focal length");
		focalLengthPanel.setNumber(1);
		editPanel.add(focalLengthPanel, "span");
		
		principalPointPanel = new LabelledVector3DPanel("Principal point");
		principalPointPanel.setVector3D(new Vector3D(0, 0, 10));
		editPanel.add(principalPointPanel, "span");
				
		transmissionCoefficientPanel = new LabelledDoublePanel("Transmission coefficient");
		transmissionCoefficientPanel.setNumber(SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT);
		editPanel.add(transmissionCoefficientPanel, "span");;

		lensTypeComboBox = new JComboBox<LensType>(LensType.values());
		lensTypeComboBox.setSelectedItem(LensType.IDEAL_THIN_LENS);
		editPanel.add(lensTypeComboBox, "span");

		shadowThrowingCheckBox = new JCheckBox("Shadow-throwing");
		shadowThrowingCheckBox.setSelected(true);
		editPanel.add(shadowThrowingCheckBox, "wrap");

		editPanel.validate();
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.GUI.Editable#setValuesInEditPanel()
	 */
	@Override
	public void setValuesInEditPanel()
	{
		descriptionPanel.setString(getDescription());
		centrePanel.setVector3D(getCentre());
		widthVectorPanel.setVector3D(getSpanVector1());
		heightVectorPanel.setVector3D(getSpanVector2());
		
		// initialize any fields
		focalLengthPanel.setNumber(focalLength);
		principalPointPanel.setVector3D(principalPoint);
		lensTypeComboBox.setSelectedItem(lensType);
		shadowThrowingCheckBox.setSelected(shadowThrowing);
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.GUI.Editable#acceptValuesInEditPanel()
	 */
	@Override
	public EditableScaledParametrisedCentredParallelogram acceptValuesInEditPanel()
	{
		// don't use super.acceptValuesInEditPanel(); as this sets the surface property and the scaling;
		// instead, copy the relevant code from it
		setDescription(descriptionPanel.getString());
		
		setCentreAndSpanVectors(
				centrePanel.getVector3D(),
				widthVectorPanel.getVector3D(),
				heightVectorPanel.getVector3D()
			);
		
		focalLength = focalLengthPanel.getNumber();
		principalPoint = principalPointPanel.getVector3D();
		throughputCoefficient = transmissionCoefficientPanel.getNumber();
		lensType = (LensType)(lensTypeComboBox.getSelectedItem());
		shadowThrowing = shadowThrowingCheckBox.isSelected();

		setSurfaceProperty();
		
		return this;
	}
}
