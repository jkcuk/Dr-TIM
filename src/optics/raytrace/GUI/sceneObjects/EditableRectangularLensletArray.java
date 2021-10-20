package optics.raytrace.GUI.sceneObjects;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import math.Vector3D;
import net.miginfocom.swing.MigLayout;
import optics.raytrace.GUI.core.IPanel;
import optics.raytrace.GUI.lowLevel.DoublePanel;
import optics.raytrace.GUI.lowLevel.GUIBitsAndBobs;
import optics.raytrace.GUI.lowLevel.LabelledDoublePanel;
import optics.raytrace.GUI.lowLevel.LabelledStringPanel;
import optics.raytrace.GUI.lowLevel.LabelledVector2DPanel;
import optics.raytrace.GUI.lowLevel.LabelledVector3DPanel;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.Studio;
import optics.raytrace.core.SurfacePropertyPrimitive;
import optics.raytrace.sceneObjects.LensType;
import optics.raytrace.surfaces.RectangularIdealThinLensletArraySimple;
import optics.raytrace.surfaces.PhaseHologramOfRectangularLensletArray;

/**
 * A rectangle whose surface area is a phase hologram of a rectangular lenslet array
 * @author johannes
 */
public class EditableRectangularLensletArray 
extends EditableScaledParametrisedCentredParallelogram
{
	private static final long serialVersionUID = -8167701964397597829L;

	private double focalLength;
	private double uPeriod;
	private double vPeriod;
	private double uOffset;
	private double vOffset;
	private LensType lensType;
	private boolean simulateDiffractiveBlur;
	private double lambda;
	private double throughputCoefficient;
	private boolean shadowThrowing;

	
	public EditableRectangularLensletArray(
			String description,
			Vector3D centre, 
			Vector3D spanVector1,
			Vector3D spanVector2, 
			double focalLength,
			double uPeriod,
			double vPeriod,
			double uOffset,
			double vOffset,
			LensType lensType,
			boolean simulateDiffractiveBlur,
			double lambda,
			double throughputCoefficient,
			boolean reflective,
			boolean shadowThrowing,
			SceneObject parent,
			Studio studio
		)
	{
		super(parent, studio);
		
		setDescription(description);
		setCentreAndSpanVectors(
				centre,	// centre
				spanVector1,
				spanVector2
			);

		// probably unnecessary
		setUScaling(-spanVector1.getLength()/2, spanVector1.getLength()/2);
		setVScaling(-spanVector2.getLength()/2, spanVector2.getLength()/2);

		this.focalLength = focalLength;
		this.uPeriod = uPeriod;
		this.vPeriod = vPeriod;
		this.uOffset = uOffset;
		this.vOffset = vOffset;
		this.lensType = lensType;
		this.simulateDiffractiveBlur = simulateDiffractiveBlur;
		this.lambda = lambda;
		this.throughputCoefficient = throughputCoefficient;
		this.shadowThrowing = shadowThrowing;

		setSurfaceProperty();

//		super(
//				description,
//				centre, 
//				spanVector1,
//				spanVector2, 
//				-spanVector1.getLength()/2, spanVector1.getLength()/2,	// suMin, suMax
//				-spanVector2.getLength()/2, spanVector2.getLength()/2,	// svMin, svMax
//				null,	// surfaceProperty, null for the moment
//				parent,
//				studio
//			);
	}
	
	/**
	 * set the surface property according to the parameters
	 */
	public void setSurfaceProperty()
	{
		switch(lensType)
		{
		case PHASE_HOLOGRAM_OF_LENS:
			setSurfaceProperty(new PhaseHologramOfRectangularLensletArray(
					getCentre(),
					getSpanVector1(),	// uHat
					getSpanVector2(),	// vHat
					focalLength,
					uPeriod,	// uPeriod
					vPeriod,	// vPeriod
					uOffset,	// uOffset
					vOffset,	// vOffset
					simulateDiffractiveBlur,
					lambda,
					throughputCoefficient,
					false,	// reflective,
					shadowThrowing
					)
				);
			break;
		case IDEAL_THIN_LENS:
		default:
			setSurfaceProperty(new RectangularIdealThinLensletArraySimple(
					getCentre(),
					getSpanVector1(),	// uHat
					getSpanVector2(),	// vHat
					focalLength,
					uPeriod,	// uPeriod
					vPeriod,	// vPeriod
					uOffset,	// uOffset
					vOffset,	// vOffset
					simulateDiffractiveBlur,
					lambda,
					throughputCoefficient,
					shadowThrowing
					)
				);
		}
	}


	public EditableRectangularLensletArray(
			String description,
			Vector3D centre, 
			Vector3D spanVector1,
			Vector3D spanVector2, 
			double focalLength,
			double uPeriod,
			double vPeriod,
			double xOffset,
			double yOffset,
			LensType lensType,
			double throughputCoefficient,
			boolean reflective,
			boolean shadowThrowing,
			SceneObject parent,
			Studio studio
		)
	{
		this(
				description,
				centre,
				spanVector1,
				spanVector2,
				focalLength,
				uPeriod,
				vPeriod,
				xOffset,
				yOffset,
				lensType,
				false,	// simulateDiffractiveBlur
				632.8e-9,	// lambbda
				throughputCoefficient,
				reflective,
				shadowThrowing,
				parent,
				studio
			);
//		super(
//				description,
//				centre, 
//				spanVector1,
//				spanVector2, 
//				-spanVector1.getLength()/2, spanVector1.getLength()/2,	// suMin, suMax
//				-spanVector2.getLength()/2, spanVector2.getLength()/2,	// svMin, svMax
//				null,	// surfaceProperty, null for the moment
//				parent,
//				studio
//			);
//		
//		setSurfaceProperty(new PhaseHologramOfRectangularLensletArray(
//				focalLength,
//				uPeriod,
//				vPeriod,
//				xOffset,
//				yOffset,
//				this,	// sceneObject
//				throughputCoefficient,
//				reflective,
//				shadowThrowing
//			)
//		);
//
//		setSurfaceProperty(new PhaseHologramOfRectangularLensletArraySimple(
//				centre,
//				spanVector1,	// uHat
//				spanVector2,	// vHat
//				focalLength,
//				uPeriod,	// uPeriod
//				vPeriod,	// vPeriod
//				xOffset,	// uOffset
//				yOffset,	// vOffset
//				simulateDiffractiveBlur,
//				lambda,
//				throughputCoefficient,
//				reflective,
//				shadowThrowing
//			)
//		);
	}
	
	public EditableRectangularLensletArray(
			SceneObject parent,
			Studio studio
		)
	{
		this(
				"Lenslet array",	// description
				new Vector3D(0, 0, 10),	// centre
				new Vector3D(1, 0, 0),	// spanVector1
				new Vector3D(0, 1, 0),	// spanVector2
				1,	// focalLength
				0.1,	// uPeriod
				0.1,	// vPeriod
				0,	// xOffset
				0,	// yOffset
				LensType.IDEAL_THIN_LENS,
				SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT,	// throughputCoefficient
				false,	// reflective
				true,	// shadowThrowing
				parent,
				studio
			);
	}

	public EditableRectangularLensletArray(EditableRectangularLensletArray original) {
		super(original);
		// ((PhaseHologramOfRectangularLensletArrayParametrised)getSurfaceProperty()).setSceneObject(this);
	}
	
	
	/* (non-Javadoc)
	 * @see optics.raytrace.sceneObjects.ParametrisedPlane#clone()
	 */
	@Override
	public EditableRectangularLensletArray clone()
	{
		return new EditableRectangularLensletArray(this);
	}


	
	/**
	 * 
	 * Editable-interface stuff
	 * 
	 */
	
	/**
	 * variables
	 */
	private LabelledDoublePanel lensletArrayFocalLengthPanel, lensletArrayTransmissionCoefficientPanel;
	private DoublePanel lambdaNMPanel;
	private LabelledVector2DPanel lensletArrayPeriodPanel, lensletArrayOffsetPanel;
	private JCheckBox simulateDiffractiveBlurCheckBox, shadowThrowingCheckBox;
	private JComboBox<LensType> lensTypeComboBox;


	
	/**
	 * initialise the edit panel
	 */
	@Override
	public void createEditPanel(IPanel iPanel)
	{	
		editPanel = new JPanel();
		editPanel.setLayout(new MigLayout("insets 0"));
		editPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Lenslet array"));
		
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
		// the lenslet-array parameters
		//
		
		lensletArrayFocalLengthPanel = new LabelledDoublePanel("Focal length");
		lensletArrayFocalLengthPanel.setNumber(1);
		editPanel.add(lensletArrayFocalLengthPanel, "span");
		
		lensletArrayPeriodPanel = new LabelledVector2DPanel("Period in (u, v)");
		lensletArrayPeriodPanel.setVector2D(0.1, 0.1);
		editPanel.add(lensletArrayPeriodPanel, "span");
		
		lensletArrayOffsetPanel = new LabelledVector2DPanel("Offset in (u, v)");
		lensletArrayOffsetPanel.setVector2D(0, 0);
		editPanel.add(lensletArrayOffsetPanel, "span");
		
		lensletArrayTransmissionCoefficientPanel = new LabelledDoublePanel("Transmission coefficient");
		lensletArrayTransmissionCoefficientPanel.setNumber(SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT);
		editPanel.add(lensletArrayTransmissionCoefficientPanel, "span");;

		lensTypeComboBox = new JComboBox<LensType>(LensType.values());
		lensTypeComboBox.setSelectedItem(LensType.IDEAL_THIN_LENS);
		editPanel.add(lensTypeComboBox, "span");

		simulateDiffractiveBlurCheckBox = new JCheckBox("");
		simulateDiffractiveBlurCheckBox.setSelected(true);
		lambdaNMPanel = new DoublePanel();
		lambdaNMPanel.setNumber(550.);
		editPanel.add(GUIBitsAndBobs.makeRow(simulateDiffractiveBlurCheckBox, "Simulate diffractive blur for wavelength", lambdaNMPanel, "nm"), "span");

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
		lensletArrayFocalLengthPanel.setNumber(focalLength);
		lensletArrayPeriodPanel.setVector2D(uPeriod, vPeriod);
		lensletArrayOffsetPanel.setVector2D(uOffset, vOffset);
		lensTypeComboBox.setSelectedItem(lensType);
		simulateDiffractiveBlurCheckBox.setSelected(simulateDiffractiveBlur);
		lambdaNMPanel.setNumber(lambda*1e9);
		lensletArrayTransmissionCoefficientPanel.setNumber(throughputCoefficient);
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
		
		focalLength = lensletArrayFocalLengthPanel.getNumber();
		uPeriod = lensletArrayPeriodPanel.getVector2D().x;
		vPeriod = lensletArrayPeriodPanel.getVector2D().y;
		uOffset = lensletArrayOffsetPanel.getVector2D().x;
		vOffset = lensletArrayOffsetPanel.getVector2D().y;
		throughputCoefficient = lensletArrayTransmissionCoefficientPanel.getNumber();
		lensType = (LensType)(lensTypeComboBox.getSelectedItem());
		simulateDiffractiveBlur = simulateDiffractiveBlurCheckBox.isSelected();
		lambda = lambdaNMPanel.getNumber()*1e-9;
		shadowThrowing = shadowThrowingCheckBox.isSelected();

		setSurfaceProperty();
		
		return this;
	}
}
