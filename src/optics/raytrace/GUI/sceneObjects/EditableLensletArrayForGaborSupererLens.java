package optics.raytrace.GUI.sceneObjects;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import math.Geometry;
import math.Vector3D;
import net.miginfocom.swing.MigLayout;
import optics.raytrace.GUI.core.IPanel;
import optics.raytrace.GUI.lowLevel.DoublePanel;
import optics.raytrace.GUI.lowLevel.GUIBitsAndBobs;
import optics.raytrace.GUI.lowLevel.LabelledDoublePanel;
import optics.raytrace.GUI.lowLevel.LabelledStringPanel;
import optics.raytrace.GUI.lowLevel.LabelledVector3DPanel;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.Studio;
import optics.raytrace.core.SurfacePropertyPrimitive;
import optics.raytrace.sceneObjects.LensType;
import optics.raytrace.surfaces.IdealThinLensletArrayForGaborSupererLens;
import optics.raytrace.surfaces.PhaseHologramOfLensletArrayForGaborSupererLens;
import optics.raytrace.surfaces.PhaseHologramOfRectangularLensletArrayParametrised;

/**
 * A rectangle whose surface is a rectangular lenslet array for Gabor super-er lenses.
 * 
 * In that rectangular lenslet array, all lenslets have the same focal length.
 * However, compared to the RectangularIdealThinLensletArraySimple, the array is generalised in that a lenslet's principal point
 * does not necessarily coincide with the centre of that lenslet's clear aperture.
 * Specifically, the centres of the clear apertures of the lenslets form a rectangular array, and so do the principal points,
 * but the lattice vectors of these two arrays are, in general, different.
 * The periods in the u and v directions of the array of principal points is given by uPeriodPrincipalPoints and vPeriodPrincipalPoints,
 * those of the array of clear apertures are given by uPeriodApertures and vPeriodApertures.
 * 
 * Based on EditableRectangularLensletArray.
 * 
 * @author johannes
 * @see optics.raytrace.surfaces.IdealThinLensletArrayForGaborSupererLens
 */
public class EditableLensletArrayForGaborSupererLens 
extends EditableScaledParametrisedCentredParallelogram
implements ActionListener
{
	private static final long serialVersionUID = 3376691697246687964L;

	
	private double focalLength;
	
	/**
	 * first lattice vector of the array of clear-aperture centres
	 */
	private Vector3D aperturesLatticeVector1;
	
	/**
	 * second lattice vector of the array of clear-aperture centres
	 */	
	private Vector3D aperturesLatticeVector2;

	/**
	 * first lattice vector of the array of principal points
	 */	
	private Vector3D principalPointsLatticeVector1;

	/**
	 * second lattice vector of the array of principal points
	 */	
	private Vector3D principalPointsLatticeVector2;
	
	/**
	 * centre of the aperture with indices (0, 0)
	 */
	private Vector3D aperture00Centre;
	
	/**
	 * principal point with indices (0, 0)
	 */
	private Vector3D principalPoint00;
	
	private LensType lensType;
	private boolean simulateDiffractiveBlur;
	private double lambda;
	private double throughputCoefficient;
	private boolean shadowThrowing;


	/**
	 * @param description
	 * @param centreRectangle
	 * @param uSpanVector
	 * @param vSpanVector
	 * @param focalLength
	 * @param aperturesLatticeVector1
	 * @param aperturesLatticeVector2
	 * @param principalPointsLatticeVector1
	 * @param principalPointsLatticeVector2
	 * @param aperture00Centre
	 * @param principalPoint00
	 * @param lensType
	 * @param simulateDiffractiveBlur
	 * @param lambda
	 * @param throughputCoefficient
	 * @param shadowThrowing
	 * @param parent
	 * @param studio
	 */
	public EditableLensletArrayForGaborSupererLens(
			String description,
			Vector3D centreRectangle,
			Vector3D uSpanVector,
			Vector3D vSpanVector, 
			double focalLength,
			Vector3D aperturesLatticeVector1,
			Vector3D aperturesLatticeVector2,
			Vector3D principalPointsLatticeVector1,
			Vector3D principalPointsLatticeVector2,
			Vector3D aperture00Centre,
			Vector3D principalPoint00,
			LensType lensType,
			boolean simulateDiffractiveBlur,
			double lambda,
			double throughputCoefficient,
			boolean shadowThrowing,
			SceneObject parent,
			Studio studio
		)
	{
		super(parent, studio);
		
		setDescription(description);
		setCentreAndSpanVectors(
				centreRectangle,	// centre
				uSpanVector,	// spanVector1
				vSpanVector	// spanVector2
			);

		// probably unnecessary
//		setUScaling(-uSpanVector.getLength()/2, uSpanVector.getLength()/2);
//		setVScaling(-vSpanVector.getLength()/2, vSpanVector.getLength()/2);

		this.focalLength = focalLength;
		this.aperturesLatticeVector1 = aperturesLatticeVector1;
		this.aperturesLatticeVector2 = aperturesLatticeVector2;
		this.principalPointsLatticeVector1 = principalPointsLatticeVector1;
		this.principalPointsLatticeVector2 = principalPointsLatticeVector2;
		this.aperture00Centre = aperture00Centre;
		this.principalPoint00 = principalPoint00;
		this.lensType = lensType;
		this.simulateDiffractiveBlur = simulateDiffractiveBlur;
		this.lambda = lambda;
		this.throughputCoefficient = throughputCoefficient;
		this.shadowThrowing = shadowThrowing;

		setSurfaceProperty();
	}
	
	public EditableLensletArrayForGaborSupererLens(
			SceneObject parent,
			Studio studio
		)
	{
		this(
				"Lenslet-array for Gabor super-er lens",	// description
				new Vector3D(0, 0, 5),	// centre of rectangular scene object
				new Vector3D(1, 0, 0),	// spanVector1
				new Vector3D(0, 1, 0),	// spanVector2
				1,	// focalLength
				new Vector3D(0.1, 0, 0),	// aperturesLatticeVector1
				new Vector3D(0, 0.1, 0),	// aperturesLatticeVector2
				new Vector3D(0.1, 0, 0),	// principalPointsLatticeVector1
				new Vector3D(0, 0.1, 0),	// principalPointsLatticeVector2
				new Vector3D(0, 0, 5),	// aperture00Centre
				new Vector3D(0, 0, 5),	// principalPoint00
				LensType.IDEAL_THIN_LENS,
				true,	// simulateDiffractiveBlur
				550e-9,	// lambda
				SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT,	// throughputCoefficient
				true,	// shadowThrowing
				parent,
				studio
			);
	}

	public EditableLensletArrayForGaborSupererLens(EditableLensletArrayForGaborSupererLens original) {
		super(original);
		((PhaseHologramOfRectangularLensletArrayParametrised)getSurfaceProperty()).setSceneObject(this);
	}
	
	
	/* (non-Javadoc)
	 * @see optics.raytrace.sceneObjects.ParametrisedPlane#clone()
	 */
	@Override
	public EditableLensletArrayForGaborSupererLens clone()
	{
		return new EditableLensletArrayForGaborSupererLens(this);
	}


	/**
	 * set the surface property according to the parameters
	 */
	public void setSurfaceProperty()
	{
		switch(lensType)
		{
		case PHASE_HOLOGRAM_OF_LENS:
			setSurfaceProperty(
					new PhaseHologramOfLensletArrayForGaborSupererLens(
							focalLength,
							aperturesLatticeVector1,
							aperturesLatticeVector2,
							principalPointsLatticeVector1,
							principalPointsLatticeVector2,
							aperture00Centre,
							principalPoint00,
							simulateDiffractiveBlur,
							lambda,
							throughputCoefficient,
							shadowThrowing
						)
				);
			break;
		case IDEAL_THIN_LENS:
		default:
			setSurfaceProperty(
					new IdealThinLensletArrayForGaborSupererLens(
							focalLength,
							aperturesLatticeVector1,
							aperturesLatticeVector2,
							principalPointsLatticeVector1,
							principalPointsLatticeVector2,
							aperture00Centre,
							principalPoint00,
							simulateDiffractiveBlur,
							lambda,
							throughputCoefficient,
							shadowThrowing
						)
				);
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
	
	private LabelledDoublePanel lensletArrayFocalLengthPanel, lensletArrayTransmissionCoefficientPanel;
	private LabelledVector3DPanel
		aperturesLatticeVector1Panel, aperturesLatticeVector2Panel,
		principalPointsLatticeVector1Panel, principalPointsLatticeVector2Panel,
		aperture00CentrePanel, principalPoint00Panel;
	private JCheckBox simulateDiffractiveBlurCheckBox, shadowThrowingCheckBox;
	private DoublePanel lambdaNMPanel;
	private JComboBox<LensType> lensTypeComboBox;
	private JButton onRectangleButton;


	
	/**
	 * initialise the edit panel
	 */
	@Override
	public void createEditPanel(IPanel iPanel)
	{	
		editPanel = new JPanel();
		editPanel.setLayout(new MigLayout("insets 0"));
		editPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Lenslet array for Gabor super-er lens"));
		
		//
		// the basic-parameters panel
		//
		
		// a text field containing the description
		descriptionPanel = new LabelledStringPanel("Description");
		editPanel.add(descriptionPanel, "wrap");
		
		// the rectangular scene object
		
		JPanel rectanglePanel = new JPanel();
		rectanglePanel.setLayout(new MigLayout("insets 0"));
		rectanglePanel.setBorder(GUIBitsAndBobs.getTitledBorder("Rectangular outline"));
		editPanel.add(rectanglePanel, "wrap");
		
		centrePanel = new LabelledVector3DPanel("Centre");
		rectanglePanel.add(centrePanel, "wrap");

		widthVectorPanel = new LabelledVector3DPanel("Vector along width");
		rectanglePanel.add(widthVectorPanel, "wrap");

		heightVectorPanel = new LabelledVector3DPanel("Vector along height");
		rectanglePanel.add(heightVectorPanel, "wrap");

		//
		// the lenslet-array parameters
		//
		
		JPanel lensletArrayPanel = new JPanel();
		lensletArrayPanel.setLayout(new MigLayout("insets 0"));
		lensletArrayPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Lenslet array"));
		editPanel.add(lensletArrayPanel, "wrap");
		
		
		JPanel principalPointsArrayPanel = new JPanel();
		principalPointsArrayPanel.setLayout(new MigLayout("insets 0"));
		principalPointsArrayPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Array of principal points"));
		lensletArrayPanel.add(principalPointsArrayPanel, "wrap");

		principalPointsLatticeVector1Panel = new LabelledVector3DPanel("Lattice vector 1");
		// principalPointsLatticeVector1Panel.setVector3D(principalPointsLatticeVector1);
		principalPointsArrayPanel.add(principalPointsLatticeVector1Panel, "wrap");

		principalPointsLatticeVector2Panel = new LabelledVector3DPanel("Lattice vector 2");
		// principalPointsLatticeVector2Panel.setVector3D(principalPointsLatticeVector2);
		principalPointsArrayPanel.add(principalPointsLatticeVector2Panel, "wrap");

		principalPoint00Panel = new LabelledVector3DPanel("Principal point of lens (0, 0)");
		// principalPoint00Panel.setVector3D(principalPoint00);
		principalPointsArrayPanel.add(principalPoint00Panel, "wrap");


		JPanel aperturesArrayPanel = new JPanel();
		aperturesArrayPanel.setLayout(new MigLayout("insets 0"));
		aperturesArrayPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Array of clear-aperture centres"));
		lensletArrayPanel.add(aperturesArrayPanel, "wrap");

		aperturesLatticeVector1Panel = new LabelledVector3DPanel("Lattice vector 1");
		// aperturesLatticeVector1Panel.setVector3D(aperturesLatticeVector1);
		aperturesArrayPanel.add(aperturesLatticeVector1Panel, "wrap");

		aperturesLatticeVector2Panel = new LabelledVector3DPanel("Lattice vector 2");
		// aperturesLatticeVector2Panel.setVector3D(aperturesLatticeVector2);
		aperturesArrayPanel.add(aperturesLatticeVector2Panel, "wrap");

		aperture00CentrePanel = new LabelledVector3DPanel("Clear-aperture centre of lens (0, 0)");
		// aperture00CentrePanel.setVector3D(aperture00Centre);
		aperturesArrayPanel.add(aperture00CentrePanel, "wrap");
		
		onRectangleButton = new JButton("Project into plane of rectangle");
		onRectangleButton.addActionListener(this);
		lensletArrayPanel.add(onRectangleButton, "span");
		
		lensletArrayFocalLengthPanel = new LabelledDoublePanel("Focal length");
		lensletArrayFocalLengthPanel.setNumber(1);
		lensletArrayPanel.add(lensletArrayFocalLengthPanel, "span");
				
		lensletArrayTransmissionCoefficientPanel = new LabelledDoublePanel("Transmission coefficient");
		lensletArrayTransmissionCoefficientPanel.setNumber(SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT);
		lensletArrayPanel.add(lensletArrayTransmissionCoefficientPanel, "span");;

		lensTypeComboBox = new JComboBox<LensType>(LensType.values());
		lensTypeComboBox.setSelectedItem(LensType.IDEAL_THIN_LENS);
		lensletArrayPanel.add(lensTypeComboBox, "span");

		simulateDiffractiveBlurCheckBox = new JCheckBox("");
		simulateDiffractiveBlurCheckBox.setSelected(true);
		lambdaNMPanel = new DoublePanel();
		lambdaNMPanel.setNumber(550.);
		lensletArrayPanel.add(GUIBitsAndBobs.makeRow(simulateDiffractiveBlurCheckBox, "Simulate diffractive blur for wavelength", lambdaNMPanel, "nm"), "span");

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
		aperturesLatticeVector1Panel.setVector3D(aperturesLatticeVector1);
		aperturesLatticeVector2Panel.setVector3D(aperturesLatticeVector2);
		principalPointsLatticeVector1Panel.setVector3D(principalPointsLatticeVector1);
		principalPointsLatticeVector2Panel.setVector3D(principalPointsLatticeVector2);
		aperture00CentrePanel.setVector3D(aperture00Centre);
		principalPoint00Panel.setVector3D(principalPoint00);
		lensletArrayTransmissionCoefficientPanel.setNumber(throughputCoefficient);
		lensTypeComboBox.setSelectedItem(lensType);
		simulateDiffractiveBlurCheckBox.setSelected(simulateDiffractiveBlur);
		lambdaNMPanel.setNumber(lambda*1e9);
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
		aperturesLatticeVector1 = aperturesLatticeVector1Panel.getVector3D();
		aperturesLatticeVector2 = aperturesLatticeVector2Panel.getVector3D();
		principalPointsLatticeVector1 = principalPointsLatticeVector1Panel.getVector3D();
		principalPointsLatticeVector2 = principalPointsLatticeVector2Panel.getVector3D();
		aperture00Centre = aperture00CentrePanel.getVector3D();
		principalPoint00 = principalPoint00Panel.getVector3D();

		throughputCoefficient = lensletArrayTransmissionCoefficientPanel.getNumber();
		lensType = (LensType)(lensTypeComboBox.getSelectedItem());
		simulateDiffractiveBlur = simulateDiffractiveBlurCheckBox.isSelected();
		lambda = lambdaNMPanel.getNumber()*1e-9;
		shadowThrowing = shadowThrowingCheckBox.isSelected();

		setSurfaceProperty();
		
		return this;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource().equals(onRectangleButton))
		{
			Vector3D rectangleCentre = centrePanel.getVector3D();
			Vector3D rectangleNormal = Vector3D.crossProduct(
					widthVectorPanel.getVector3D(),
					heightVectorPanel.getVector3D()
				);
			
			aperture00Centre = Geometry.getPointOnPlaneClosestToPoint(
					rectangleCentre,	// pointOnPlane
					rectangleNormal,	// planeNormal
					aperture00CentrePanel.getVector3D()	// point
				);
			aperture00CentrePanel.setVector3D(aperture00Centre);

			principalPoint00 = Geometry.getPointOnPlaneClosestToPoint(
					rectangleCentre,	// pointOnPlane
					rectangleNormal,	// planeNormal
					principalPoint00Panel.getVector3D()	// point
				);
			principalPoint00Panel.setVector3D(principalPoint00);
		}
		
	}
}
