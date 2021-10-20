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
import optics.raytrace.GUI.lowLevel.LabelledVector2DPanel;
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
 * Specifically, the clear apertures of the lenslets form a rectangular array, and so do the principal points,
 * but the periods of these two arrays are, in general, different.
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
	private double uPeriodApertures;
	private double vPeriodApertures;
	private double uPeriodPrincipalPoints;
	private double vPeriodPrincipalPoints;
	private Vector3D centreClearApertureArray;
	private Vector3D centrePrincipalPointArray;
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
	 * @param uPeriodApertures
	 * @param vPeriodApertures
	 * @param uPeriodPrincipalPoints
	 * @param vPeriodPrincipalPoints
	 * @param centreClearApertureArray
	 * @param centrePrincipalPointArray
	 * @param lensType
	 * @param simulateDiffractiveBlur
	 * @param lambda
	 * @param throughputCoefficient
	 * @param reflective
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
			double uPeriodApertures,
			double vPeriodApertures,
			double uPeriodPrincipalPoints,
			double vPeriodPrincipalPoints,
			Vector3D centreClearApertureArray,
			Vector3D centrePrincipalPointArray,
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
				centreRectangle,	// centre
				uSpanVector,	// spanVector1
				vSpanVector	// spanVector2
			);

		// probably unnecessary
//		setUScaling(-uSpanVector.getLength()/2, uSpanVector.getLength()/2);
//		setVScaling(-vSpanVector.getLength()/2, vSpanVector.getLength()/2);

		this.focalLength = focalLength;
		this.uPeriodApertures = uPeriodApertures;
		this.vPeriodApertures = vPeriodApertures;
		this.uPeriodPrincipalPoints = uPeriodPrincipalPoints;
		this.vPeriodPrincipalPoints = vPeriodPrincipalPoints;
		this.centreClearApertureArray = centreClearApertureArray;
		this.centrePrincipalPointArray = centrePrincipalPointArray;
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
				0.1,	// uPeriodApertures
				0.1,	// vPeriodApertures
				0.1,	// uPeriodPrincipalPoints
				0.1,	// vPeriodPrincipalPoints
				new Vector3D(0, 0, 5),	// centre of clear-aperture array
				new Vector3D(0, 0, 5),	// centre of principal-point array
				LensType.IDEAL_THIN_LENS,
				true,	// simulateDiffractiveBlur
				550e-9,	// lambda
				SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT,	// throughputCoefficient
				false,	// reflective
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
			// TODO add the phase-hologram version
			setSurfaceProperty(
					new PhaseHologramOfLensletArrayForGaborSupererLens(
							centreClearApertureArray,	// centreClearApertureArray,
							centrePrincipalPointArray,
							getSpanVector1(),	// uHat,
							getSpanVector2(),	// vHat,
							focalLength,
							uPeriodApertures,
							vPeriodApertures,
							uPeriodPrincipalPoints,
							vPeriodPrincipalPoints,
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
							centreClearApertureArray,	// centreClearApertureArray,
							centrePrincipalPointArray,
							getSpanVector1(),	// uHat,
							getSpanVector2(),	// vHat,
							focalLength,
							uPeriodApertures,
							vPeriodApertures,
							uPeriodPrincipalPoints,
							vPeriodPrincipalPoints,
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
	
	private LabelledVector3DPanel centreClearApertureArrayPanel, centrePrincipalPointArrayPanel;
	private LabelledDoublePanel lensletArrayFocalLengthPanel, lensletArrayTransmissionCoefficientPanel;
	private LabelledVector2DPanel aperturesPeriodPanel, principalPointsPeriodPanel;
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

		centreClearApertureArrayPanel = new LabelledVector3DPanel("Centre of clear-aperture array");
		centreClearApertureArrayPanel.setVector3D(new Vector3D(0, 0, 0));
		lensletArrayPanel.add(centreClearApertureArrayPanel, "span");
		
		centrePrincipalPointArrayPanel = new LabelledVector3DPanel("Centre of principal-point array");
		centrePrincipalPointArrayPanel.setVector3D(new Vector3D(0, 0, 0));
		lensletArrayPanel.add(centrePrincipalPointArrayPanel, "span");
		
		onRectangleButton = new JButton("Project into plane of rectangle");
		onRectangleButton.addActionListener(this);
		lensletArrayPanel.add(onRectangleButton, "span");
		
		lensletArrayFocalLengthPanel = new LabelledDoublePanel("Focal length");
		lensletArrayFocalLengthPanel.setNumber(1);
		lensletArrayPanel.add(lensletArrayFocalLengthPanel, "span");
		
		aperturesPeriodPanel = new LabelledVector2DPanel("Period of clear-aperture array in (u,v)");
		aperturesPeriodPanel.setVector2D(0.1, 0.1);
		lensletArrayPanel.add(aperturesPeriodPanel, "span");
		
		principalPointsPeriodPanel = new LabelledVector2DPanel("Period of principal-point array in (u,v)");
		principalPointsPeriodPanel.setVector2D(0.1, 0.1);
		lensletArrayPanel.add(principalPointsPeriodPanel, "span");
		
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
		centreClearApertureArrayPanel.setVector3D(centreClearApertureArray);
		centrePrincipalPointArrayPanel.setVector3D(centrePrincipalPointArray);
		lensletArrayFocalLengthPanel.setNumber(focalLength);
		aperturesPeriodPanel.setVector2D(uPeriodApertures, vPeriodApertures);
		principalPointsPeriodPanel.setVector2D(uPeriodPrincipalPoints, vPeriodPrincipalPoints);
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
		
		centreClearApertureArray = centreClearApertureArrayPanel.getVector3D();
		centrePrincipalPointArray = centrePrincipalPointArrayPanel.getVector3D();
		focalLength = lensletArrayFocalLengthPanel.getNumber();
		uPeriodApertures = aperturesPeriodPanel.getVector2D().x;
		vPeriodApertures = aperturesPeriodPanel.getVector2D().y;
		uPeriodPrincipalPoints = principalPointsPeriodPanel.getVector2D().x;
		vPeriodPrincipalPoints = principalPointsPeriodPanel.getVector2D().y;
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
			
			centreClearApertureArray = Geometry.getPointOnPlaneClosestToPoint(
					rectangleCentre,	// pointOnPlane
					rectangleNormal,	// planeNormal
					centreClearApertureArrayPanel.getVector3D()	// point
				);
			centreClearApertureArrayPanel.setVector3D(centreClearApertureArray);

			centrePrincipalPointArray = Geometry.getPointOnPlaneClosestToPoint(
					rectangleCentre,	// pointOnPlane
					rectangleNormal,	// planeNormal
					centrePrincipalPointArrayPanel.getVector3D()	// point
				);
			centrePrincipalPointArrayPanel.setVector3D(centrePrincipalPointArray);
		}
		
	}
}
