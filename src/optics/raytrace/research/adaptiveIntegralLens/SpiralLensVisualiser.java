package optics.raytrace.research.adaptiveIntegralLens;

import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import math.*;
import net.miginfocom.swing.MigLayout;
import optics.raytrace.sceneObjects.ParametrisedDisc.DiscParametrisationType;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.surfaces.PhaseHologramOfLens;
import optics.raytrace.surfaces.PhaseHologramOfCylindricalLensSpiral;
import optics.raytrace.surfaces.PhaseHologramOfCylindricalLensSpiral.CylindricalLensSpiralType;
import optics.raytrace.exceptions.SceneException;
import optics.raytrace.NonInteractiveTIMActionEnum;
import optics.raytrace.NonInteractiveTIMEngine;
import optics.raytrace.GUI.cameras.RenderQualityEnum;
import optics.raytrace.GUI.lowLevel.ApertureSizeType;
import optics.raytrace.GUI.lowLevel.DoublePanel;
import optics.raytrace.GUI.lowLevel.GUIBitsAndBobs;
import optics.raytrace.GUI.lowLevel.LabelledDoublePanel;
import optics.raytrace.GUI.lowLevel.LabelledVector3DPanel;
import optics.raytrace.GUI.sceneObjects.EditableScaledParametrisedDisc;
import optics.raytrace.core.Studio;
import optics.raytrace.core.StudioInitialisationType;


/**
 * Simulate the visual appearance of a spiral lens, and the view through it.
 * 
 * @author Johannes Courtial
 */
public class SpiralLensVisualiser extends NonInteractiveTIMEngine
{
	/**
	 * either LOGARITHMIC or ARCHIMEDEAN
	 */
	private CylindricalLensSpiralType cylindricalLensSpiralType;
	
	/**
	 * centre position
	 */
	private Vector3D centre;
	
	/**
	 * the "b" parameter of the spiral
	 */
	private double b;
	
	/**
	 * focal length of the cylindrical lenses (everywhere in the case of the logarithmic spiral lens, at r=1 in the case of the Archimedean spiral lens)
	 */
	private double f;
	
	/**
	 * smallest distance to which the combination can be double-focussed (which means that each winding is focussed to have the same radial focussing power
	 * as the integral lens formed by the two spiral cylindrical lenses);
	 * for this distance, the separation between the two lenses (distanceOfLens2BehindLens1) is zero
	 */
	private double fMin;

	/**
	 * rotation angle between the two spiral lenses, in degrees
	 */
	private double rotationAngleDeg;
	
	/**
	 * distance of lens 2 behind lens 1
	 */
	private double distanceOfLens2BehindLens1;
	
	/**
	 * focal length of comparison lens
	 */
	private double comparisonLensF;
	
	private boolean showLens1;
	private boolean showLens2;
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
	public SpiralLensVisualiser()
	{
		super();
		
		// set to true for interactive version
		nonInteractiveTIMAction = NonInteractiveTIMActionEnum.INTERACTIVE;
		
		// set all parameters
		
		renderQuality = RenderQualityEnum.DRAFT;
		
		cylindricalLensSpiralType = CylindricalLensSpiralType.LOGARITHMIC;
		centre = new Vector3D(0, 0, 0);
		b = 0.01;
		f = .1;
		fMin = -10;
		rotationAngleDeg = 30;
		distanceOfLens2BehindLens1 = Math.max(f*f/calculateF(), 0.00001);
		showLens1 = true;
		showLens2 = true;
		showComparisonLens = false;
		comparisonLensF = 28.59792;
		
		studioInitialisation = StudioInitialisationType.TIM_HEAD;	// the backdrop

		cameraViewDirection = new Vector3D(0, 0, 1);
		cameraHorizontalFOVDeg = 15;
		
		if(nonInteractiveTIMAction == NonInteractiveTIMActionEnum.INTERACTIVE)
		{
			windowTitle = "Dr TIM's spiral-lens visualiser";
			windowWidth = 1500;
			windowHeight = 650;
		}
	}
	

	@Override
	public String getFirstPartOfFilename()
	{
		return "SpiralLensVisualiser"	// the name
				+ " type="+cylindricalLensSpiralType.toString()
				+ " centre="+centre
				+ " b="+b
				+ " f="+f
				+ " fMin="+fMin
				+ " rotationAngle="+rotationAngleDeg+"°"
				+ " d12="+distanceOfLens2BehindLens1
				+ (showLens1?" lens 1 shown":"")
				+ (showLens2?" lens 2 shown":"")
				+ (showComparisonLens?" comparison lens f="+comparisonLensF+" shown":"")
				;
	}
	
	private double getFOfFocussedCylindricalLens(double fCylindrical)
	{
		// Each cylindrical lens, of focal length fCylindrical, has another cylindrical lens "added" to it (it's actually the focussing powers that add) such that,
		// when the two cylindrical lenses touch (i.e. the separation between them is zero), they become a cylindrical lens of focal length fMin. 
		// This can be done symmetrically by "adding" to both spiral cylindrical lenses the same focal length, namely dF = 2*fMin
		double dF = 2*fMin;

		// the focal powers of the cylindrical lens of focal length fCylindrical and that of the additional lens with focal length dF then add up, i.e.
		// the combined focal length is
		return fCylindrical*dF/(fCylindrical+dF);
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
		Vector3D lensNormal = new Vector3D(0, 0, 1);
		
		double lensRadius = 1;

		Vector3D spiralLens1Centre = centre;
		EditableScaledParametrisedDisc spiralLens1 = new EditableScaledParametrisedDisc(
				"spiral-lens 1",	// description
				spiralLens1Centre,
				lensNormal,	// normal
				1,	// radius
				new Vector3D(1, 0, 0),	// phi0Direction
				DiscParametrisationType.CARTESIAN,	// parametrisationType
				-lensRadius, lensRadius,	// suMin, suMax
				-lensRadius, lensRadius,	// svMin, svMax
				null,	// surface property
				scene,	// parent
				studio
		);
		PhaseHologramOfCylindricalLensSpiral hologram1 = new PhaseHologramOfCylindricalLensSpiral(
				cylindricalLensSpiralType,
				getFOfFocussedCylindricalLens(f),	// focalLength
				1,	// a
				b,
				spiralLens1,	// sceneObject
				0.96,	// throughputCoefficient
				false,	// reflective
				false	// shadowThrowing
			);
		spiralLens1.setSurfaceProperty(hologram1);
		scene.addSceneObject(spiralLens1, showLens1);

		Vector3D spiralLens2Centre = Vector3D.sum(centre, lensNormal.getWithLength(distanceOfLens2BehindLens1));
		EditableScaledParametrisedDisc spiralLens2 = new EditableScaledParametrisedDisc(
				"spiral-lens 2",	// description
				spiralLens2Centre,
				lensNormal,	// normal
				lensRadius,	// radius
				new Vector3D(1, 0, 0),	// phi0Direction
				DiscParametrisationType.CARTESIAN,	// parametrisationType
				-lensRadius, lensRadius,	// suMin, suMax
				-lensRadius, lensRadius,	// svMin, svMax
				null,	// surface property
				scene,	// parent
				studio
		);
		double a;
		switch(cylindricalLensSpiralType)
		{
		case ARCHIMEDEAN:
			a = 1+b*MyMath.deg2rad(rotationAngleDeg);
			break;
		case LOGARITHMIC:
		default:
			a = 1*Math.exp(b*MyMath.deg2rad(rotationAngleDeg));
		}
		PhaseHologramOfCylindricalLensSpiral hologram2 = new PhaseHologramOfCylindricalLensSpiral(
				cylindricalLensSpiralType,
				getFOfFocussedCylindricalLens(-f),	// focalLength
				a,	// a
				b,
				spiralLens2,	// sceneObject
				0.96,	// throughputCoefficient
				false,	// reflective
				false	// shadowThrowing
			);
		spiralLens2.setSurfaceProperty(hologram2);
		scene.addSceneObject(spiralLens2, showLens2);


		// the comparison lens
		
		Vector3D comparisonLensCentre = Vector3D.sum(centre, lensNormal.getWithLength(-MyMath.TINY));
		EditableScaledParametrisedDisc comparisonLens = new EditableScaledParametrisedDisc(
				"comparison lens",	// description
				comparisonLensCentre,
				lensNormal,	// normal
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
	
	private JComboBox<CylindricalLensSpiralType> cylindricalLensSpiralTypeComboBox;
	private LabelledVector3DPanel centrePanel;
	private LabelledDoublePanel bPanel, fPanel, fMinPanel, distanceOfLens2BehindLens1Panel;
	private DoublePanel comparisonLensFPanel;
	private DoublePanel rotationAngleDegPanel;
	private JCheckBox showLens1CheckBox, showLens2CheckBox, showComparisonLensCheckBox;
	private JComboBox<StudioInitialisationType> studioInitialisationComboBox;
	private JButton calculateCombinedFocalLengthButton, calculateDistanceOfLens2BehindLens1Button, focusOnTIMEyesButton;

	// camera stuff
	// private LabelledVector3DPanel cameraViewDirectionPanel;
	private LabelledDoublePanel cameraDistancePanel;
	private DoublePanel cameraHorizontalFOVDegPanel;
	private JComboBox<ApertureSizeType> cameraApertureSizeComboBox;
	private LabelledDoublePanel cameraFocussingDistancePanel;



	
	/**
	 * add controls to the interactive control panel;
	 * override to modify
	 */
	@Override
	protected void createInteractiveControlPanel()
	{
		super.createInteractiveControlPanel();
				
		cylindricalLensSpiralTypeComboBox = new JComboBox<CylindricalLensSpiralType>(CylindricalLensSpiralType.values());
		cylindricalLensSpiralTypeComboBox.setSelectedItem(cylindricalLensSpiralType);
		interactiveControlPanel.add(GUIBitsAndBobs.makeRow("Spiral type", cylindricalLensSpiralTypeComboBox), "span");

		centrePanel = new LabelledVector3DPanel("Centre");
		centrePanel.setVector3D(centre);
		interactiveControlPanel.add(centrePanel, "span");
		
		bPanel = new LabelledDoublePanel("b");
		bPanel.setNumber(b);
		interactiveControlPanel.add(bPanel, "span");
		
		fPanel = new LabelledDoublePanel("f");
		fPanel.setNumber(f);
		interactiveControlPanel.add(fPanel, "span");

		rotationAngleDegPanel = new DoublePanel();
		rotationAngleDegPanel.setNumber(rotationAngleDeg);
		interactiveControlPanel.add(GUIBitsAndBobs.makeRow("Rotation angle between lenses", rotationAngleDegPanel, "°"), "span");
		
		JPanel windingFocussingPanel = new JPanel();
		windingFocussingPanel.setLayout(new MigLayout("insets 0"));
		windingFocussingPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Winding focussing"));

		fMinPanel = new LabelledDoublePanel("Smallest focal length on which winding can be focussed");
		fMinPanel.setNumber(fMin);
		windingFocussingPanel.add(fMinPanel, "span");

		distanceOfLens2BehindLens1Panel = new LabelledDoublePanel("Distance of lens 2 behind lens 1");
		distanceOfLens2BehindLens1Panel.setNumber(distanceOfLens2BehindLens1);
		windingFocussingPanel.add(distanceOfLens2BehindLens1Panel);
		
		calculateDistanceOfLens2BehindLens1Button = new JButton("Optimise");
		calculateDistanceOfLens2BehindLens1Button.setToolTipText("Perform pixel focussing; works only if the focal length of the combination of the spiral lenses is positive");
		calculateDistanceOfLens2BehindLens1Button.addActionListener(this);
		windingFocussingPanel.add(calculateDistanceOfLens2BehindLens1Button,"span");
		
		interactiveControlPanel.add(windingFocussingPanel, "span");
		
		showLens1CheckBox = new JCheckBox("Show lens 1");
		showLens1CheckBox.setSelected(showLens1);
		interactiveControlPanel.add(showLens1CheckBox, "span");

		showLens2CheckBox = new JCheckBox("Show lens 2");
		showLens2CheckBox.setSelected(showLens2);
		interactiveControlPanel.add(showLens2CheckBox, "span");

		showComparisonLensCheckBox = new JCheckBox("Show comparison lens of focal length");
		showComparisonLensCheckBox.setSelected(showComparisonLens);
		// interactiveControlPanel.add(showComparisonLensCheckBox);
		
		comparisonLensFPanel = new DoublePanel();
		comparisonLensFPanel.setColumns(8);
		comparisonLensFPanel.setNumber(comparisonLensF);
		// interactiveControlPanel.add(comparisonLensFPanel);
		
		calculateCombinedFocalLengthButton = new JButton("Calculate");
		calculateCombinedFocalLengthButton.addActionListener(this);
		// interactiveControlPanel.add(calculateCombinedFocalLengthButton, "wrap");
		
		interactiveControlPanel.add(GUIBitsAndBobs.makeRow(showComparisonLensCheckBox, comparisonLensFPanel, calculateCombinedFocalLengthButton), "wrap");
		

		studioInitialisationComboBox = new JComboBox<StudioInitialisationType>(StudioInitialisationType.limitedValuesForBackgrounds);
		studioInitialisationComboBox.setSelectedItem(studioInitialisation);
		interactiveControlPanel.add(GUIBitsAndBobs.makeRow("Initialise backdrop to", studioInitialisationComboBox), "span");
		
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
		
		focusOnTIMEyesButton = new JButton("Focus on Tim's eyes");
		focusOnTIMEyesButton.addActionListener(this);
		cameraPanel.add(focusOnTIMEyesButton, "span");
	}
	
	/**
	 * called before rendering;
	 * override when adding fields
	 */
	@Override
	protected void acceptValuesInInteractiveControlPanel()
	{
		super.acceptValuesInInteractiveControlPanel();
		
		cylindricalLensSpiralType = (CylindricalLensSpiralType)(cylindricalLensSpiralTypeComboBox.getSelectedItem());
		centre = centrePanel.getVector3D();
		b = bPanel.getNumber();
		f = fPanel.getNumber();
		rotationAngleDeg = rotationAngleDegPanel.getNumber();
		fMin = fMinPanel.getNumber();
		distanceOfLens2BehindLens1 = distanceOfLens2BehindLens1Panel.getNumber();
		showLens1 = showLens1CheckBox.isSelected();
		showLens2 = showLens2CheckBox.isSelected();
		showComparisonLens = showComparisonLensCheckBox.isSelected();
		comparisonLensF = comparisonLensFPanel.getNumber();
		studioInitialisation = (StudioInitialisationType)(studioInitialisationComboBox.getSelectedItem());
		
		// cameraViewDirection = cameraViewDirectionPanel.getVector3D();
		cameraDistance = cameraDistancePanel.getNumber();
		cameraHorizontalFOVDeg = cameraHorizontalFOVDegPanel.getNumber();
		cameraApertureSize = (ApertureSizeType)(cameraApertureSizeComboBox.getSelectedItem());
		cameraFocussingDistance = cameraFocussingDistancePanel.getNumber();
	}

	/**
	 * calculate F = -f/(1-Exp(b rotationAngle))
	 * @return	the focal length of the combined lens
	 */
	public double calculateF()
	{
		return -f/(1-Math.exp(b*MyMath.deg2rad(rotationAngleDeg)));
	}
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource().equals(calculateCombinedFocalLengthButton))
		{
			acceptValuesInInteractiveControlPanel();
			comparisonLensFPanel.setNumber(calculateF());
		}
		else if(e.getSource().equals(calculateDistanceOfLens2BehindLens1Button))
		{
			acceptValuesInInteractiveControlPanel();
			double f1 = getFOfFocussedCylindricalLens(f);
			double f2 = getFOfFocussedCylindricalLens(-f);
			System.out.println("SpiralLensVisualiser::actionPerformed: f1="+f1+", f2="+f2);
			distanceOfLens2BehindLens1Panel.setNumber(Math.max(-f1*f2*(1/calculateF() - 1/f1 - 1/f2), 0.00001));
			// distanceOfLens2BehindLens1Panel.setNumber(Math.max(f*f/calculateF(), 0.00001));
		}
		else if(e.getSource().equals(focusOnTIMEyesButton))
		{
			acceptValuesInInteractiveControlPanel();
			
			// calculate the combined focal length of the lens
			double F = calculateF();
			
			// calculate the object distance, i.e. the distance between the lens and Tim's eyes (which are at z=8.95)
			double o = 8.95-centre.z;
					
			// calculate the image distance
			double i=o*F/(o-F);
			
			cameraFocussingDistancePanel.setNumber(cameraDistance - i);
		}
		else super.actionPerformed(e);
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
		(new SpiralLensVisualiser()).run();
	}
}
