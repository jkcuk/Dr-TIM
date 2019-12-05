package optics.raytrace.research.adaptiveIntegralLens;

import java.awt.event.ActionEvent;
import java.io.PrintStream;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import math.*;
import net.miginfocom.swing.MigLayout;
import optics.raytrace.sceneObjects.Sphere;
import optics.raytrace.sceneObjects.ParametrisedDisc.DiscParametrisationType;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.surfaces.PhaseHologramOfLens;
import optics.raytrace.surfaces.SurfaceColour;
import optics.raytrace.surfaces.PhaseHologramOfCylindricalLensSpiral;
import optics.raytrace.surfaces.PhaseHologramOfCylindricalLensSpiral.CylindricalLensSpiralType;
import optics.raytrace.exceptions.SceneException;
import optics.DoubleColour;
import optics.raytrace.NonInteractiveTIMActionEnum;
import optics.raytrace.NonInteractiveTIMEngine;
import optics.raytrace.GUI.cameras.RenderQualityEnum;
import optics.raytrace.GUI.lowLevel.ApertureSizeType;
import optics.raytrace.GUI.lowLevel.DoublePanel;
import optics.raytrace.GUI.lowLevel.GUIBitsAndBobs;
import optics.raytrace.GUI.lowLevel.LabelledDoubleColourPanel;
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
	 * offset of the centre of spiral lens 2 compared to where it should be
	 */
	private Vector3D spiralLens2AdditionalOffset;
	
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
	 * the centres of spheres that can be placed in the scene
	 */
	private Vector3D[] sphereCentres;
	
	/**
	 * the radii of spheres that can be placed in the scene
	 */
	private double[] sphereRadii;
	
	/**
	 * the colours of spheres that can be placed in the scene
	 */
	private DoubleColour[] sphereColours;
	
	/**
	 * the visibilities of spheres that can be placed in the scene
	 */
	private boolean[] sphereVisibilities;

	
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
		spiralLens2AdditionalOffset = new Vector3D(0, 0, 0);
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
		
		sphereCentres = new Vector3D[3];
		sphereRadii = new double[3];
		sphereColours = new DoubleColour[3];
		sphereVisibilities = new boolean[3];
		
		sphereCentres[0] = new Vector3D(-0.2, 0, 1);
		sphereRadii[0] = 0.01;
		sphereColours[0] = new DoubleColour(1, 0, 0);
		sphereVisibilities[0] = false;

		sphereCentres[1] = new Vector3D(0, 0, 1);
		sphereRadii[1] = 0.01;
		sphereColours[1] = new DoubleColour(0, 1, 0);
		sphereVisibilities[1] = false;

		sphereCentres[2] = new Vector3D(.2, 0, 1);
		sphereRadii[2] = 0.01;
		sphereColours[2] = new DoubleColour(0, 0, 1);
		sphereVisibilities[2] = false;


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
		return "SpiralLensVisualiser" // the name
//				+ " type="+cylindricalLensSpiralType.toString()
//				+ " centre="+centre
//				+ " b="+b
//				+ " f="+f
//				+ " fMin="+fMin
//				+ " rotationAngle="+rotationAngleDeg+"°"
//				+ " d12="+distanceOfLens2BehindLens1
//				+ (showLens1?" lens 1 shown":"")
//				+ (showLens2?" lens 2 shown":"")
//				+ (showComparisonLens?" comparison lens f="+comparisonLensF+" shown":"")
				;
	}
	
	@Override
	public void writeParameters(PrintStream printStream)
	{
		printStream.println("cylindricalLensSpiralType = "+cylindricalLensSpiralType);
		printStream.println("centre = "+centre);
		printStream.println("spiralLens2AdditionalOffset = "+spiralLens2AdditionalOffset);
		printStream.println("b = "+b);
		printStream.println("f = "+f);
		printStream.println("fMin = "+fMin);
		printStream.println("rotationAngleDeg = "+rotationAngleDeg);
		printStream.println("distanceOfLens2BehindLens1 = "+distanceOfLens2BehindLens1);
		printStream.println("showLens1 = "+showLens1);
		printStream.println("showLens2 = "+showLens2);
		printStream.println("showComparisonLens = "+showComparisonLens);
		printStream.println("comparisonLensF = "+comparisonLensF);
		
		printStream.println();

		// rest of scene
		
		printStream.println("studioInitialisation = "+studioInitialisation);
		for(int i=0; i<3; i++)
		{
			printStream.println("sphereCentres["+i+"] = "+sphereCentres[i]);
			printStream.println("sphereRadii["+i+"] = "+sphereRadii[i]);
			printStream.println("sphereColours["+i+"] = "+sphereColours[i]);
			printStream.println("sphereVisibilities["+i+"] = "+sphereVisibilities[i]);
		}

		printStream.println();
		
		printStream.println("cameraViewDirection = "+cameraViewDirection);
		printStream.println("cameraHorizontalFOVDeg = "+cameraHorizontalFOVDeg);
		
		// write all parameters defined in NonInteractiveTIMEngine
		// super.writeParameters(printStream);		
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

		Vector3D spiralLens2Centre = Vector3D.sum(centre, lensNormal.getWithLength(distanceOfLens2BehindLens1), spiralLens2AdditionalOffset);
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
		
		// add any other scene objects
		
		// first the coloured spheres that can be added to the scene
		for(int i=0; i<3; i++)
			scene.addSceneObject(
					new Sphere(
							"Coloured sphere #"+i,	// description
							sphereCentres[i],	// centre
							sphereRadii[i],	// radius
							new SurfaceColour(sphereColours[i], DoubleColour.WHITE, false),	// surface property: sphereColours[i], made shiny; don't throw shadow
							scene,
							studio
						),
					sphereVisibilities[i]
				);

		// the camera
		studio.setCamera(getStandardCamera());
	}

	
	
	//
	// for interactive version
	//
	
	private JComboBox<CylindricalLensSpiralType> cylindricalLensSpiralTypeComboBox;
	private LabelledVector3DPanel centrePanel, spiralLens2AdditionalOffsetPanel;
	private LabelledDoublePanel bPanel, fPanel, fMinPanel, distanceOfLens2BehindLens1Panel;
	private DoublePanel comparisonLensFPanel;
	private DoublePanel rotationAngleDegPanel;
	private JCheckBox showLens1CheckBox, showLens2CheckBox, showComparisonLensCheckBox;
	private JButton calculateCombinedFocalLengthButton, calculateDistanceOfLens2BehindLens1Button, focusOnTIMEyesButton;
	
	private JComboBox<StudioInitialisationType> studioInitialisationComboBox;
	private LabelledVector3DPanel[] sphereCentrePanels;
	private LabelledDoublePanel[] sphereRadiusPanels;
	private LabelledDoubleColourPanel[] sphereColourPanels;
	private JCheckBox[] sphereVisibilityCheckBoxes;

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
		
		JTabbedPane tabbedPane = new JTabbedPane();
		interactiveControlPanel.add(tabbedPane, "span");

		// the "Lens" panel
		
		JPanel lensPanel = new JPanel();
		lensPanel.setLayout(new MigLayout("insets 0"));
		tabbedPane.addTab("Lens", lensPanel);
				
		cylindricalLensSpiralTypeComboBox = new JComboBox<CylindricalLensSpiralType>(CylindricalLensSpiralType.values());
		cylindricalLensSpiralTypeComboBox.setSelectedItem(cylindricalLensSpiralType);
		lensPanel.add(GUIBitsAndBobs.makeRow("Spiral type", cylindricalLensSpiralTypeComboBox), "span");

		centrePanel = new LabelledVector3DPanel("Centre");
		centrePanel.setVector3D(centre);
		lensPanel.add(centrePanel, "span");
		
		bPanel = new LabelledDoublePanel("b parameter of spiral");
		bPanel.setNumber(b);
		lensPanel.add(bPanel, "span");
		
		fPanel = new LabelledDoublePanel("f of cylindrical lens");
		fPanel.setNumber(f);
		lensPanel.add(fPanel, "span");

		rotationAngleDegPanel = new DoublePanel();
		rotationAngleDegPanel.setNumber(rotationAngleDeg);
		lensPanel.add(GUIBitsAndBobs.makeRow("Rotation angle between parts", rotationAngleDegPanel, "°"), "span");
		
		JPanel windingFocussingPanel = new JPanel();
		windingFocussingPanel.setLayout(new MigLayout("insets 0"));
		windingFocussingPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Winding focussing"));

		fMinPanel = new LabelledDoublePanel("Smallest focal length for which winding can be focussed");
		fMinPanel.setNumber(fMin);
		windingFocussingPanel.add(fMinPanel, "span");

		distanceOfLens2BehindLens1Panel = new LabelledDoublePanel("Distance of part 2 behind lens 1");
		distanceOfLens2BehindLens1Panel.setNumber(distanceOfLens2BehindLens1);
		windingFocussingPanel.add(distanceOfLens2BehindLens1Panel);
		
		calculateDistanceOfLens2BehindLens1Button = new JButton("Optimise");
		calculateDistanceOfLens2BehindLens1Button.setToolTipText("Perform pixel focussing; works only if the focal length of the combination of the integral lens is greater than the smallest focal length for which winding can be focussed");
		calculateDistanceOfLens2BehindLens1Button.addActionListener(this);
		windingFocussingPanel.add(calculateDistanceOfLens2BehindLens1Button,"span");
		
		lensPanel.add(windingFocussingPanel, "span");
		
		spiralLens2AdditionalOffsetPanel = new LabelledVector3DPanel("Additional offset of part 2");
		spiralLens2AdditionalOffsetPanel.setVector3D(spiralLens2AdditionalOffset);
		lensPanel.add(spiralLens2AdditionalOffsetPanel, "span");
		
		showLens1CheckBox = new JCheckBox("Show part 1");
		showLens1CheckBox.setSelected(showLens1);
		lensPanel.add(showLens1CheckBox, "span");

		showLens2CheckBox = new JCheckBox("Show part 2");
		showLens2CheckBox.setSelected(showLens2);
		lensPanel.add(showLens2CheckBox, "span");

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
		
		lensPanel.add(GUIBitsAndBobs.makeRow(showComparisonLensCheckBox, comparisonLensFPanel, calculateCombinedFocalLengthButton), "wrap");
		
		
		// the "Backdrop" panel
		
		JPanel backdropPanel = new JPanel();
		backdropPanel.setLayout(new MigLayout("insets 0"));
		tabbedPane.addTab("Backdrop", backdropPanel);

		studioInitialisationComboBox = new JComboBox<StudioInitialisationType>(StudioInitialisationType.limitedValuesForBackgrounds);
		studioInitialisationComboBox.setSelectedItem(studioInitialisation);
		backdropPanel.add(GUIBitsAndBobs.makeRow("Initialise backdrop to", studioInitialisationComboBox), "span");

		JPanel spherePanels[] = new JPanel[3];
		sphereCentrePanels = new LabelledVector3DPanel[3];
		sphereRadiusPanels = new LabelledDoublePanel[3];
		sphereColourPanels = new LabelledDoubleColourPanel[3];
		sphereVisibilityCheckBoxes = new JCheckBox[3];
		for(int i=0; i<3; i++)
		{
			spherePanels[i] = new JPanel();
			spherePanels[i].setLayout(new MigLayout("insets 0"));
			spherePanels[i].setBorder(GUIBitsAndBobs.getTitledBorder("Sphere #"+(i+1)));
			
			sphereCentrePanels[i] = new LabelledVector3DPanel("Centre");
			sphereCentrePanels[i].setVector3D(sphereCentres[i]);
			spherePanels[i].add(sphereCentrePanels[i], "wrap");

			sphereRadiusPanels[i] = new LabelledDoublePanel("Radius");
			sphereRadiusPanels[i].setNumber(sphereRadii[i]);
			spherePanels[i].add(sphereRadiusPanels[i], "wrap");

			sphereColourPanels[i] = new LabelledDoubleColourPanel("Colour");
			sphereColourPanels[i].setDoubleColour(sphereColours[i]);
			spherePanels[i].add(sphereColourPanels[i], "wrap");

			sphereVisibilityCheckBoxes[i] = new JCheckBox("Visible");
			sphereVisibilityCheckBoxes[i].setSelected(sphereVisibilities[i]);
			spherePanels[i].add(sphereVisibilityCheckBoxes[i], "wrap");
			
			backdropPanel.add(spherePanels[i], "wrap");
		}

		
		// the "Camera" panel
		
		JPanel cameraPanel = new JPanel();
		// cameraPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Camera"));
		cameraPanel.setLayout(new MigLayout("insets 0"));
		// interactiveControlPanel.add(cameraPanel, "span");
		tabbedPane.addTab("Camera", cameraPanel);

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
		spiralLens2AdditionalOffset =spiralLens2AdditionalOffsetPanel.getVector3D();
		showLens1 = showLens1CheckBox.isSelected();
		showLens2 = showLens2CheckBox.isSelected();
		showComparisonLens = showComparisonLensCheckBox.isSelected();
		comparisonLensF = comparisonLensFPanel.getNumber();
		
		studioInitialisation = (StudioInitialisationType)(studioInitialisationComboBox.getSelectedItem());
		for(int i=0; i<3; i++)
		{
			sphereCentres[i] = sphereCentrePanels[i].getVector3D();
			sphereRadii[i] = sphereRadiusPanels[i].getNumber();
			sphereColours[i] = sphereColourPanels[i].getDoubleColour();
			sphereVisibilities[i] = sphereVisibilityCheckBoxes[i].isSelected();
		}
		
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
