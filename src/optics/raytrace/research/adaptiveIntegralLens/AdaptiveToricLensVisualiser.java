package optics.raytrace.research.adaptiveIntegralLens;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintStream;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import math.*;
import net.miginfocom.swing.MigLayout;
import optics.raytrace.sceneObjects.ParametrisedDisc.DiscParametrisationType;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.surfaces.PhaseHologramOfCrossedLinearPowerLenticularArrays;
import optics.raytrace.surfaces.PhaseHologramOfLens;
import optics.raytrace.surfaces.PhaseHologramOfRectangularLensletArray;
import optics.raytrace.exceptions.SceneException;
import optics.raytrace.NonInteractiveTIMActionEnum;
import optics.raytrace.NonInteractiveTIMEngine;
import optics.raytrace.GUI.cameras.RenderQualityEnum;
import optics.raytrace.GUI.lowLevel.ApertureSizeType;
import optics.raytrace.GUI.lowLevel.DoublePanel;
import optics.raytrace.GUI.lowLevel.GUIBitsAndBobs;
import optics.raytrace.GUI.lowLevel.LabelledDoublePanel;
import optics.raytrace.GUI.lowLevel.LabelledVector2DPanel;
import optics.raytrace.GUI.lowLevel.LabelledVector3DPanel;
import optics.raytrace.GUI.sceneObjects.EditableScaledParametrisedDisc;
import optics.raytrace.core.Studio;
import optics.raytrace.core.StudioInitialisationType;
import optics.raytrace.core.SurfaceProperty;
import optics.raytrace.core.SurfacePropertyPrimitive;


/**
 * Simulate the visual appearance of an adaptive toric lens, and the view through it.
 * 
 * @author Johannes Courtial
 */
public class AdaptiveToricLensVisualiser extends NonInteractiveTIMEngine implements ActionListener
{
	/**
	 * centre position
	 */
	private Vector3D centre;
	
	/**
	 * enables different lens types to be specified
	 */
	private AdaptiveToricLensType adaptiveToricLensType;
	
	// common parameters
	
	/**
	 * period in <i>u</i> direction
	 */
	private double period;
	
	/**
	 * distance between components
	 */
	private double distanceBetweenComponents;

	private boolean showComponent1;
	private boolean showComponent2;

	// crossed-lenticular-arrays parameters

	/**
	 * the focal power of the <i>n</i>th cylindrical lens, which is centred at <u> = <i>u<sub>n</sub></i>, is <i>P<sub>n</sub></i> = <i>dPdu</i> <i>u<sub>n</sub></i>;
	 * its focal length is <i>f<sub>n</sub></i> = 1/<i>u<sub>n</sub></i>;
	 * the phase cross-section of the cylindrical lens is <i>&Phi;</i>(<i>u</i>) = (&pi; (<i>u</i>-<i>u<sub>n</sub></i>)^2)(&lambda; <i>f<sub>n</sub></i>)
	 */
	private double dPdu;
	
	/**
	 * offset between the arrays that form the integral cylindrical lens 1
	 */
	private double xOffset;

	/**
	 * offset between the arrays that form the integral cylindrical lens 2
	 */
	private double yOffset;

	// stretchy-lenslet-arrays parameters
	
	/**
	 * modulus of the focal length of the two stretchy LAs;
	 * the focal lengths are +/- stretchyLAsF
	 */
	private double stretchyLAsF;
	
	/**
	 * factor by which array 1 is stretched in the u direction
	 */
	private double stretchFactorU;
	
	/**
	 * factor by which array 1 is stretched in the v direction
	 */
	private double stretchFactorV;

	/**
	 * azimuthal angle, i.e. angle w.r.t. x axis, of u direction, in degrees
	 */
	private double phiUDeg;
	
	/**
	 * angle by which component 2 is rotated w.r.t. component 1
	 */
	private double rotationAngleDeg;
	
	
	/**
	 * focal length of comparison lens
	 */
	private double comparisonLensF;
	
	private boolean showComparisonLens;

	//
	// the rest of the scene
	//
	
	/**
	 * Determines how to initialise the backdrop
	 */
	private StudioInitialisationType studioInitialisation;
	
	
	/**
	 * Constructor.
	 * Sets all parameters.
	 */
	public AdaptiveToricLensVisualiser()
	{
		super();
		
		// set to true for interactive version
		nonInteractiveTIMAction = NonInteractiveTIMActionEnum.INTERACTIVE;
		
		// set all parameters
		
		renderQuality = RenderQualityEnum.DRAFT;
		
		adaptiveToricLensType = AdaptiveToricLensType.OFFSET_CONTROLLED;
		
		// common parameters
		centre = new Vector3D(0, 0, 0);
		period = 0.01;
		distanceBetweenComponents = 1e-7;
		comparisonLensF = 1;
		showComponent1 = true;
		showComponent2 = true;
		showComparisonLens = false;

		// crossed-lenticular-arrays parameters
		dPdu = 100;
		xOffset = 0;
		yOffset = 0;
		
		// stretchy-lenslet-arrays parameters
		stretchFactorU = 1;
		stretchFactorV = 1;
		stretchyLAsF = 0.1;
		phiUDeg = 0;
		rotationAngleDeg = 0;

		studioInitialisation = StudioInitialisationType.TIM_HEAD;	// the backdrop

		cameraViewDirection = new Vector3D(0, 0, 1);
		cameraHorizontalFOVDeg = 15;
		cameraFocussingDistance = 20;
		
		if(nonInteractiveTIMAction == NonInteractiveTIMActionEnum.INTERACTIVE)
		{
			windowTitle = "Dr TIM's adaptive-toric-lens visualiser";
			windowWidth = 1500;
			windowHeight = 650;
		}
	}
	

	@Override
	public String getClassName()
	{
		return "AdaptiveToricLensVisualiser"	// the name
				+ (showComponent1||showComponent2?" "+adaptiveToricLensType.getAcronym():"")
				+ " centre="+centre
				+ (showComponent1||showComponent2?" period="+period:"")
				+ (showComponent1||showComponent2?" d12="+distanceBetweenComponents:"")
				+ ((showComponent1||showComponent2)&&adaptiveToricLensType==AdaptiveToricLensType.OFFSET_CONTROLLED?" dPdu="+dPdu:"")
				+ ((showComponent1||showComponent2)&&adaptiveToricLensType==AdaptiveToricLensType.OFFSET_CONTROLLED?" xOffset="+xOffset:"")
				+ ((showComponent1||showComponent2)&&adaptiveToricLensType==AdaptiveToricLensType.OFFSET_CONTROLLED?" yOffset="+yOffset:"")
				+ ((showComponent1||showComponent2)&&adaptiveToricLensType==AdaptiveToricLensType.STRAIN_CONTROLLED?" stretchFactorX="+stretchFactorU:"")
				+ ((showComponent1||showComponent2)&&adaptiveToricLensType==AdaptiveToricLensType.STRAIN_CONTROLLED?" stretchFactorY="+stretchFactorV:"")
				+ ((showComponent1||showComponent2)&&adaptiveToricLensType==AdaptiveToricLensType.STRAIN_CONTROLLED?" stretchyLAsF="+stretchyLAsF:"")
				+ ((showComponent1||showComponent2)&&adaptiveToricLensType==AdaptiveToricLensType.STRAIN_CONTROLLED?" phiUDeg="+phiUDeg:"")
				+ (showComponent1?" component 1 shown":"")
				+ (showComponent2?" component 2 shown":"")
				+ (showComparisonLens?" lens f="+comparisonLensF+" shown":"")
				+ " studioInitialisation="+studioInitialisation
				;
	}
	
	@Override
	public void writeParameters(PrintStream printStream)
	{
		// write any parameters not defined in NonInteractiveTIMEngine
		printStream.println("Scene initialisation");
		printStream.println();
		
		printStream.println("adaptiveToricLensType = "+adaptiveToricLensType);
		printStream.println("centre = "+centre);
		printStream.println("period = "+period);
		printStream.println("distanceBetweenComponents = "+distanceBetweenComponents);
		printStream.println("comparisonLensF = "+comparisonLensF);
		printStream.println("showComponent1 = "+showComponent1);
		printStream.println("showComponent2 = "+showComponent2);
		printStream.println("showComparisonLens = "+showComparisonLens);

		// crossed-lenticular-arrays parameters
		printStream.println("dPdu = "+dPdu);
		printStream.println("xOffset = "+xOffset);
		printStream.println("yOffset = "+yOffset);
		
		// stretchy-lenslet-arrays parameters
				printStream.println("stretchFactorU = "+stretchFactorU);
		printStream.println("stretchFactorV = "+stretchFactorV);
		printStream.println("stretchyLAsF = "+stretchyLAsF);
		printStream.println("phiUDeg = "+phiUDeg);
		printStream.println("rotationAngleDeg = "+rotationAngleDeg);

		printStream.println("studioInitialisation = "+studioInitialisation);

		printStream.println();

		// write all parameters defined in NonInteractiveTIMEngine
		super.writeParameters(printStream);		
	}

		
	@Override
	public void populateStudio()
	throws SceneException
	{
		// the studio
		studio = new Studio();

		// the scene
		SceneObjectContainer scene = new SceneObjectContainer("the scene", null, studio);
		studio.setScene(scene);
		
		// initialise the scene and lights
		StudioInitialisationType.initialiseSceneAndLights(
				studioInitialisation,
				scene,
				studio
			);
		
		// add the lens holograms
		Vector3D componentNormal = new Vector3D(0, 0, 1);
		
		double lensRadius = 1;
		Vector3D component1Centre, component2Centre;
		SurfaceProperty component1SurfaceProperty, component2SurfaceProperty;

		double alpha = MyMath.deg2rad(rotationAngleDeg);
		Vector3D rotatedX = Vector3D.sum(Vector3D.X.getProductWith( Math.cos(alpha)), Vector3D.Y.getProductWith(Math.sin(alpha)));
		Vector3D rotatedY = Vector3D.sum(Vector3D.X.getProductWith(-Math.sin(alpha)), Vector3D.Y.getProductWith(Math.cos(alpha)));

		switch(adaptiveToricLensType)
		{
		case STRAIN_CONTROLLED:
			component1Centre = Vector3D.sum(
					centre,
					new Vector3D(0, 0, -0.5*distanceBetweenComponents)
				);
			// calculate the basis vectors, stretched appropriately
			// calculate xHat, stretched by <stretchFactorU> in the <u> direction and by <stretchFactorV> in the <v> direction
			Vector3D uHat = Vector3D.sum(
					Vector3D.X.getWithLength(Math.cos(MyMath.deg2rad(phiUDeg))),
					Vector3D.Y.getWithLength(Math.sin(MyMath.deg2rad(phiUDeg)))
				);
			Vector3D vHat = Vector3D.sum(
					Vector3D.X.getWithLength(-Math.sin(MyMath.deg2rad(phiUDeg))),
					Vector3D.Y.getWithLength(Math.cos(MyMath.deg2rad(phiUDeg)))
				);
			Vector3D xUVZ = Vector3D.X.toBasis(uHat, vHat, Vector3D.Z);
			Vector3D yUVZ = Vector3D.Y.toBasis(uHat, vHat, Vector3D.Z);
			component1SurfaceProperty = new PhaseHologramOfRectangularLensletArray(
					component1Centre,	// centre
					Vector3D.sum(uHat.getProductWith(xUVZ.x*stretchFactorU), vHat.getProductWith(xUVZ.y*stretchFactorV)),	// uHat
					Vector3D.sum(uHat.getProductWith(yUVZ.x*stretchFactorU), vHat.getProductWith(yUVZ.y*stretchFactorV)),	// vHat
					stretchyLAsF,	// focalLength
					period,	// uPeriod
					period,	// vPeriod
					0,	// uOffset
					0,	// vOffset
					false,	// simulateDiffractiveBlur
					632.8e-9,	// lambda
					SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT,	// throughputCoefficient
					false,	// reflective
					false	// shadowThrowing
				);
			component2Centre = Vector3D.sum(
					centre,
					new Vector3D(0, 0, +0.5*distanceBetweenComponents)
				);
			component2SurfaceProperty = new PhaseHologramOfRectangularLensletArray(
					component2Centre,	// centre
					rotatedX,	// uHat
					rotatedY,	// vHat
					-stretchyLAsF,	// focalLength
					period,	// uPeriod
					period,	// vPeriod
					0,	// uOffset
					0,	// vOffset
					false,	// simulateDiffractiveBlur
					632.8e-9,	// lambda
					SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT,	// throughputCoefficient
					false,	// reflective
					false	// shadowThrowing
				);
			break;
		case OFFSET_CONTROLLED:
		default:
			component1Centre = Vector3D.sum(
					centre,
					new Vector3D(-0.5*xOffset, -0.5*yOffset, -0.5*distanceBetweenComponents)
				);
			component1SurfaceProperty = new PhaseHologramOfCrossedLinearPowerLenticularArrays(
					component1Centre,	// p0
					Vector3D.X,	// uHat
					Vector3D.Y,	// vHat
					dPdu,
					period,
					SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT,	// throughputCoefficient
					false,	// reflective
					false	// shadowThrowing
				);
			component2Centre = Vector3D.sum(
					centre,
					new Vector3D(+0.5*xOffset, +0.5*yOffset, +0.5*distanceBetweenComponents)
				);
			component2SurfaceProperty = new PhaseHologramOfCrossedLinearPowerLenticularArrays(
					component2Centre,	// p0
					rotatedX,	// uHat
					rotatedY,	// vHat
					-dPdu,
					period,
					SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT,	// throughputCoefficient
					false,	// reflective
					false	// shadowThrowing
				);
//			PhaseHologramOfLinearPowerLenticularArray component1XHologram = new PhaseHologramOfLinearPowerLenticularArray(
//			component1Centre,	// p0
//			Vector3D.X,	// uHat
//			dPdu,
//			period,
//			SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT,	// throughputCoefficient
//			false,	// reflective
//			false	// shadowThrowing
//		);
//	PhaseHologramOfLinearPowerLenticularArray component1YHologram = new PhaseHologramOfLinearPowerLenticularArray(
//			component1Centre,	// p0
//			Vector3D.Y,	// uHat
//			dPdu,
//			period,
//			SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT,	// throughputCoefficient
//			false,	// reflective
//			false	// shadowThrowing
//		);
	// new SurfacePropertyLayerStack(component1XHologram, component1YHologram),	// surface property
//			PhaseHologramOfLinearPowerLenticularArray component2XHologram = new PhaseHologramOfLinearPowerLenticularArray(
//			component2Centre,	// p0
//			Vector3D.X,	// uHat
//			-dPdu,
//			period,
//			SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT,	// throughputCoefficient
//			false,	// reflective
//			false	// shadowThrowing
//		);
//	PhaseHologramOfLinearPowerLenticularArray component2YHologram = new PhaseHologramOfLinearPowerLenticularArray(
//			component2Centre,	// p0
//			Vector3D.Y,	// uHat
//			-dPdu,
//			period,
//			SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT,	// throughputCoefficient
//			false,	// reflective
//			false	// shadowThrowing
//		);
			// new SurfacePropertyLayerStack(component2XHologram, component2YHologram),	// surface property
		}
		
		EditableScaledParametrisedDisc component1 = new EditableScaledParametrisedDisc(
				"component 1",	// description
				component1Centre,
				componentNormal,	// normal
				lensRadius,	// radius
				component1SurfaceProperty,	
				scene,	// parent
				studio
		);
		scene.addSceneObject(component1, showComponent1);

		EditableScaledParametrisedDisc component2 = new EditableScaledParametrisedDisc(
				"component 2",	// description
				component2Centre,
				componentNormal,	// normal
				lensRadius,	// radius
				component2SurfaceProperty,
				scene,	// parent
				studio
		);
		scene.addSceneObject(component2, showComponent2);


		// the comparison lens
		
		Vector3D comparisonLensCentre = Vector3D.sum(centre, componentNormal.getWithLength(-MyMath.TINY));
		EditableScaledParametrisedDisc comparisonLens = new EditableScaledParametrisedDisc(
				"comparison lens",	// description
				comparisonLensCentre,
				componentNormal,	// normal
				lensRadius,	// radius
				new Vector3D(1, 0, 0),	// phi0Direction
				DiscParametrisationType.CARTESIAN,	// parametrisationType
				-lensRadius, lensRadius,	// suMin, suMax
				-lensRadius, lensRadius,	// svMin, svMax
				new PhaseHologramOfLens(
						comparisonLensF,	// focal length
						centre,	// principalPoint
						0.96,	// throughputCoefficient
						false,	// reflective
						false	// shadowThrowing
					),	// surface property
				scene,	// parent
				studio
		);
		scene.addSceneObject(comparisonLens, showComparisonLens);

		// the camera
		studio.setCamera(getStandardCamera());
	}

	
	
	//
	// for interactive version
	//
	
	private JComboBox<AdaptiveToricLensType> adaptiveToricLensTypeComboBox;
	private JPanel additionalParametersPanel, crossedLenticularArraysPanel, stretchyLensletArraysPanel;
	private LabelledVector3DPanel centrePanel;
	private LabelledDoublePanel dPduPanel, periodPanel, distanceBetweenComponentsPanel, stretchyLAsFPanel;
	private DoublePanel phiUDegPanel, rotationAngleDegPanel, comparisonLensFPanel;
	private LabelledVector2DPanel offsetPanel, stretchFactorPanel;
	private JCheckBox showComponent1CheckBox, showComponent2CheckBox, showComparisonLensCheckBox;
	private JComboBox<StudioInitialisationType> studioInitialisationComboBox;
// 	private JButton calculateCombinedFocalLengthButton, calculateDistanceOfLens2BehindLens1Button, focusOnTIMEyesButton;

	// camera stuff
	// private LabelledVector3DPanel cameraViewDirectionPanel;
	private LabelledDoublePanel cameraDistancePanel;
	private DoublePanel cameraHorizontalFOVDegPanel;
	private JComboBox<ApertureSizeType> cameraApertureSizeComboBox;
	private LabelledDoublePanel cameraFocussingDistancePanel;


	private void showCorrespondingParametersPanel()
	{
		adaptiveToricLensType = (AdaptiveToricLensType)(adaptiveToricLensTypeComboBox.getSelectedItem());

		// remove the current additional-parameters panel...
		additionalParametersPanel.removeAll();
		
		// ... and add the new one
		switch(adaptiveToricLensType)
		{
		case STRAIN_CONTROLLED:
			additionalParametersPanel.add(stretchyLensletArraysPanel);
			break;
		case OFFSET_CONTROLLED:
		default:
			additionalParametersPanel.add(crossedLenticularArraysPanel);
		}
		interactiveControlPanel.validate();
	}
	
	/**
	 * add controls to the interactive control panel;
	 * override to modify
	 */
	@Override
	protected void createInteractiveControlPanel()
	{
		super.createInteractiveControlPanel();
		
		adaptiveToricLensTypeComboBox = new JComboBox<AdaptiveToricLensType>(AdaptiveToricLensType.values());
		adaptiveToricLensTypeComboBox.setSelectedItem(adaptiveToricLensType);
		adaptiveToricLensTypeComboBox.addActionListener(this);
		interactiveControlPanel.add(GUIBitsAndBobs.makeRow("Lens type", adaptiveToricLensTypeComboBox), "span");
		
		additionalParametersPanel = new JPanel();
		interactiveControlPanel.add(additionalParametersPanel, "span");
		
		crossedLenticularArraysPanel = new JPanel();
		crossedLenticularArraysPanel.setLayout(new MigLayout("insets 0"));

		dPduPanel = new LabelledDoublePanel("<html>Focussing-power gradients &part;<i>C<sub>x</sub></i>/&part;<i>x</i>=&part;<i>C<sub>y</sub></i>/&part;<i>y</i>=</html>");
		dPduPanel.setNumber(dPdu);
		crossedLenticularArraysPanel.add(dPduPanel, "span");
		
		offsetPanel = new LabelledVector2DPanel("<html>Offset between elements (&Delta;<i>x</i>,&Delta;<i>y</i>)=</html>");
		offsetPanel.setVector2D(xOffset, yOffset);
		crossedLenticularArraysPanel.add(offsetPanel, "span");
		
		stretchyLensletArraysPanel = new JPanel();
		stretchyLensletArraysPanel.setLayout(new MigLayout("insets 0"));

		stretchyLAsFPanel= new LabelledDoublePanel("f");
		stretchyLAsFPanel.setNumber(stretchyLAsF);
		stretchyLensletArraysPanel.add(stretchyLAsFPanel, "span");
		
		stretchFactorPanel = new LabelledVector2DPanel("<html>Stretch factors (<i>s<sub>u</sub><i>, <i>s<sub>v</sub></i>)=</html>");
		stretchFactorPanel.setVector2D(stretchFactorU, stretchFactorV);
		stretchyLensletArraysPanel.add(stretchFactorPanel, "span");
		
		phiUDegPanel = new DoublePanel();
		phiUDegPanel.setNumber(phiUDeg);
		stretchyLensletArraysPanel.add(new JLabel("<html>in the <i>u</i> and <i>v</i> directions, which are rotated relative</html>"), "wrap");
		stretchyLensletArraysPanel.add(GUIBitsAndBobs.makeRow("<html>to the <i>x</i> and <i>y</i> directions by &phi;=</html>", phiUDegPanel, "<html>&deg;</html>"), "span");
		
		centrePanel = new LabelledVector3DPanel("Lens centre");
		centrePanel.setVector3D(centre);
		interactiveControlPanel.add(centrePanel, "span");
		
		periodPanel = new LabelledDoublePanel("Array period");
		periodPanel.setNumber(period);
		interactiveControlPanel.add(periodPanel, "wrap");

		distanceBetweenComponentsPanel = new LabelledDoublePanel("<html>Distance between elements &Delta;<i>z</i>=</html>");
		distanceBetweenComponentsPanel.setNumber(distanceBetweenComponents);
		interactiveControlPanel.add(distanceBetweenComponentsPanel, "wrap");
				
		showComponent1CheckBox = new JCheckBox("Show component 1");
		showComponent1CheckBox.setSelected(showComponent1);
		interactiveControlPanel.add(showComponent1CheckBox, "wrap");

		showComponent2CheckBox = new JCheckBox("Show component 2");
		showComponent2CheckBox.setSelected(showComponent2);
		interactiveControlPanel.add(showComponent2CheckBox, "wrap");
		
		rotationAngleDegPanel = new DoublePanel();
		rotationAngleDegPanel.setNumber(rotationAngleDeg);
		// interactiveControlPanel.add(GUIBitsAndBobs.makeRow(showComponent2CheckBox, ", rotated by", rotationAngleDegPanel, "<html>&deg;</html>"), "wrap");

		showComparisonLensCheckBox = new JCheckBox("Show lens, focal length");
		showComparisonLensCheckBox.setSelected(showComparisonLens);
		comparisonLensFPanel = new DoublePanel();
		comparisonLensFPanel.setColumns(8);
		comparisonLensFPanel.setNumber(comparisonLensF);
		interactiveControlPanel.add(GUIBitsAndBobs.makeRow(showComparisonLensCheckBox, comparisonLensFPanel), "span");
		
//		calculateCombinedFocalLengthButton = new JButton("Calculate");
//		calculateCombinedFocalLengthButton.addActionListener(this);
//		interactiveControlPanel.add(calculateCombinedFocalLengthButton, "span");
		

		studioInitialisationComboBox = new JComboBox<StudioInitialisationType>(StudioInitialisationType.limitedValuesForBackgrounds);
		studioInitialisationComboBox.setSelectedItem(studioInitialisation);
		interactiveControlPanel.add(GUIBitsAndBobs.makeRow("Backdrop", studioInitialisationComboBox), "span");
		
		JPanel cameraPanel = new JPanel();
		cameraPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Camera"));
		cameraPanel.setLayout(new MigLayout("insets 0"));
		interactiveControlPanel.add(cameraPanel, "span");

		// camera stuff
		
//		cameraViewDirectionPanel = new LabelledVector3DPanel("View direction");
//		cameraViewDirectionPanel.setVector3D(cameraViewDirection);
//		cameraPanel.add(cameraViewDirectionPanel, "span");
		
		cameraDistancePanel = new LabelledDoublePanel("Camera distance");
		cameraDistancePanel.setNumber(cameraDistance);
		cameraPanel.add(cameraDistancePanel, "span");
		
		cameraHorizontalFOVDegPanel = new DoublePanel();
		cameraHorizontalFOVDegPanel.setNumber(cameraHorizontalFOVDeg);
		cameraPanel.add(GUIBitsAndBobs.makeRow("Horizontal FOV", cameraHorizontalFOVDegPanel, "°"), "span");
		
		cameraApertureSizeComboBox = new JComboBox<ApertureSizeType>(ApertureSizeType.values());
		cameraApertureSizeComboBox.setSelectedItem(cameraApertureSize);
		cameraPanel.add(GUIBitsAndBobs.makeRow("Camera aperture", cameraApertureSizeComboBox), "span");		
		
		cameraFocussingDistancePanel = new LabelledDoublePanel("Focussing distance");
		cameraFocussingDistancePanel.setNumber(cameraFocussingDistance);
		cameraPanel.add(cameraFocussingDistancePanel);
		
//		focusOnTIMEyesButton = new JButton("Focus on Tim's eyes");
//		focusOnTIMEyesButton.addActionListener(this);
//		cameraPanel.add(focusOnTIMEyesButton, "span");
		
		showCorrespondingParametersPanel();
	}
	
	/**
	 * called before rendering;
	 * override when adding fields
	 */
	@Override
	protected void acceptValuesInInteractiveControlPanel()
	{
		super.acceptValuesInInteractiveControlPanel();
		
		adaptiveToricLensType = (AdaptiveToricLensType)(adaptiveToricLensTypeComboBox.getSelectedItem());
		centre = centrePanel.getVector3D();
		dPdu = dPduPanel.getNumber();
		period = periodPanel.getNumber();
		xOffset = offsetPanel.getVector2D().x;
		yOffset = offsetPanel.getVector2D().y;
		stretchyLAsF = stretchyLAsFPanel.getNumber();
		stretchFactorU = stretchFactorPanel.getVector2D().x;
		stretchFactorV = stretchFactorPanel.getVector2D().y;
		phiUDeg = phiUDegPanel.getNumber();
		rotationAngleDeg = rotationAngleDegPanel.getNumber();
		distanceBetweenComponents = distanceBetweenComponentsPanel.getNumber();
		showComponent1 = showComponent1CheckBox.isSelected();
		showComponent2 = showComponent2CheckBox.isSelected();
		showComparisonLens = showComparisonLensCheckBox.isSelected();
		comparisonLensF = comparisonLensFPanel.getNumber();
		studioInitialisation = (StudioInitialisationType)(studioInitialisationComboBox.getSelectedItem());
		
		// cameraViewDirection = cameraViewDirectionPanel.getVector3D();
		cameraDistance = cameraDistancePanel.getNumber();
		cameraHorizontalFOVDeg = cameraHorizontalFOVDegPanel.getNumber();
		cameraApertureSize = (ApertureSizeType)(cameraApertureSizeComboBox.getSelectedItem());
		cameraFocussingDistance = cameraFocussingDistancePanel.getNumber();
	}

//	/**
//	 * calculate F = -f/(1-Exp(b rotationAngle))
//	 * @return	the focal length of the combined lens
//	 */
//	public double calculateF()
//	{
//		return -f/(1-Math.exp(b*MyMath.deg2rad(rotationAngleDeg)));
//	}
//	
//	@Override
//	public void actionPerformed(ActionEvent e)
//	{
//		if(e.getSource().equals(calculateCombinedFocalLengthButton))
//		{
//			acceptValuesInInteractiveControlPanel();
//			comparisonLensFPanel.setNumber(calculateF());
//		}
//		else if(e.getSource().equals(calculateDistanceOfLens2BehindLens1Button))
//		{
//			acceptValuesInInteractiveControlPanel();
//			double f1 = getFOfFocussedCylindricalLens(f);
//			double f2 = getFOfFocussedCylindricalLens(-f);
//			System.out.println("SpiralLensVisualiser::actionPerformed: f1="+f1+", f2="+f2);
//			distanceOfLens2BehindLens1Panel.setNumber(Math.max(-f1*f2*(1/calculateF() - 1/f1 - 1/f2), 0.00001));
//			// distanceOfLens2BehindLens1Panel.setNumber(Math.max(f*f/calculateF(), 0.00001));
//		}
//		else if(e.getSource().equals(focusOnTIMEyesButton))
//		{
//			acceptValuesInInteractiveControlPanel();
//			
//			// calculate the combined focal length of the lens
//			double F = calculateF();
//			
//			// calculate the object distance, i.e. the distance between the lens and Tim's eyes (which are at z=8.95)
//			double o = 8.95-centre.z;
//					
//			// calculate the image distance
//			double i=o*F/(o-F);
//			
//			cameraFocussingDistancePanel.setNumber(cameraDistance - i);
//		}
//		else super.actionPerformed(e);
//	}


	@Override
	public void actionPerformed(ActionEvent e)
	{
		super.actionPerformed(e);
		
		if(e.getSource().equals(adaptiveToricLensTypeComboBox))
		{
			showCorrespondingParametersPanel();
		}
	}


	//
	// the main method, so that this can be run as a Java application
	//

	/**
	 * Called when this is run; don't touch!
	 * @param args
	 */
	public static void main(final String[] args)
	{
		(new AdaptiveToricLensVisualiser()).run();
	}
}
