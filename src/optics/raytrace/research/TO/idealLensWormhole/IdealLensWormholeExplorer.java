package optics.raytrace.research.TO.idealLensWormhole;

import java.io.PrintStream;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import math.*;
import net.miginfocom.swing.MigLayout;
import optics.DoubleColour;
import optics.raytrace.NonInteractiveTIMActionEnum;
import optics.raytrace.NonInteractiveTIMEngine;
import optics.raytrace.GUI.cameras.RenderQualityEnum;
import optics.raytrace.GUI.lowLevel.ApertureSizeType;
import optics.raytrace.GUI.lowLevel.DoublePanel;
import optics.raytrace.GUI.lowLevel.GUIBitsAndBobs;
import optics.raytrace.GUI.lowLevel.LabelledDoublePanel;
import optics.raytrace.GUI.lowLevel.LabelledVector3DPanel;
import optics.raytrace.GUI.sceneObjects.EditableLensSimplicialComplex;
import optics.raytrace.GUI.sceneObjects.EditableScaledParametrisedSphere;
import optics.raytrace.core.Studio;
import optics.raytrace.core.StudioInitialisationType;
import optics.raytrace.exceptions.SceneException;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.sceneObjects.transformations.SimpleTranslation;
import optics.raytrace.simplicialComplex.LensType;
import optics.raytrace.surfaces.SurfaceColour;
import optics.raytrace.surfaces.SurfaceTiling;


/**
 * Based on NonInteractiveTIM.
 * 
 * @author Johannes Courtial
 */
public class IdealLensWormholeExplorer extends NonInteractiveTIMEngine
{
	// CI = inner Abyss cloak, CO = outer Abyss cloak
	private boolean showLensesCI, showLensesCO;
	private boolean showCylindersCI, showCylindersCO;
	private Vector3D baseCentreCI, baseCentreCO;
	private double heightCI, heightCO;
	// private double virtualSpaceFractionalLowerInnerVertexHeightI, virtualSpaceFractionalLowerInnerVertexHeightO;
	
	//
	// the rest of the scene
	//
	
	/**
	 * Determines how to initialise the backdrop
	 */
	private StudioInitialisationType studioInitialisation;
	
	/**
	 * The patterned sphere is an additional object to study.
	 * It is normally placed inside the cloak.
	 */
	private boolean showPatternedSphere;
	
	/**
	 * Position of the centre of the patterned sphere.
	 */
	private Vector3D patternedSphereCentre;

	/**
	 * Radius of the patterned sphere
	 */
	private double patternedSphereRadius;
	
	
	/**
	 * Constructor.
	 * Sets all parameters.
	 */
	public IdealLensWormholeExplorer()
	{
		super();
		
		showLensesCI = true;
		showLensesCO = true;
		showCylindersCI = false;
		showCylindersCO = false;
		baseCentreCI = new Vector3D(0, 1, 1.35);
		baseCentreCO = new Vector3D(0, 1, 0);
		heightCI = 1;
		heightCO = 5;
//		virtualSpaceFractionalLowerInnerVertexHeightI = -5;
//		virtualSpaceFractionalLowerInnerVertexHeightO = -5;

		
		// other scene objects
		studioInitialisation = StudioInitialisationType.MINIMALIST;	// the backdrop
		showPatternedSphere = false;
		patternedSphereCentre = new Vector3D(0.3, 1.35, 0);
		patternedSphereRadius = 0.1;

		
		// camera parameters
		cameraViewDirection = new Vector3D(0, 0.01, 1.);
		cameraViewCentre = new Vector3D(0, 1, 2.5);
		cameraDistance = 10;	// camera is located at (0, 0, 0)
		cameraFocussingDistance = 10;
		cameraHorizontalFOVDeg = 60;
		cameraMaxTraceLevel = 100;
		cameraPixelsX = 640;
		cameraPixelsY = 480;
		cameraApertureSize = ApertureSizeType.PINHOLE;
				// ApertureSizeType.SMALL;
		
		renderQuality = RenderQualityEnum.DRAFT;
		nonInteractiveTIMAction = NonInteractiveTIMActionEnum.INTERACTIVE;
		
		windowTitle = "Dr TIM's Nested-Abyss-cloak explorer";
		windowWidth = 1400;
		windowHeight = 650;
	}

	
	/* (non-Javadoc)
	 * @see optics.raytrace.NonInteractiveTIMEngine#getFirstPartOfFilename()
	 */
	@Override
	public String getClassName()
	{
		return
				"IdealLensWormholeExplorer"
				;
	}

	@Override
	public void writeParameters(PrintStream printStream)
	{
		// write any parameters not defined in NonInteractiveTIMEngine, each parameter is saved like this:
		// printStream.println("parameterName = "+parameterName);
		printStream.println("showLensesCI = "+showLensesCI);
		printStream.println("showLensesCO = "+showLensesCO);
		printStream.println("showCylindersCI = "+showCylindersCI);
		printStream.println("showCylindersCO = "+showCylindersCO);
		printStream.println("baseCentreCI = "+baseCentreCI);
		printStream.println("baseCentreCO = "+baseCentreCO);
		printStream.println("heightCI = "+heightCI);
		printStream.println("heightCO = "+heightCO);
//		printStream.println("virtualSpaceFractionalLowerInnerVertexHeightI = "+virtualSpaceFractionalLowerInnerVertexHeightI);
//		printStream.println("virtualSpaceFractionalLowerInnerVertexHeightO = "+virtualSpaceFractionalLowerInnerVertexHeightO);
		
		//
		// the rest of the scene
		//
		
		printStream.println("studioInitialisation = "+studioInitialisation);
		printStream.println("showPatternedSphere = "+showPatternedSphere);
		printStream.println("patternedSphereCentre = "+patternedSphereCentre);
		printStream.println("patternedSphereRadius = "+patternedSphereRadius);

		// write all parameters defined in NonInteractiveTIMEngine
		super.writeParameters(printStream);
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.NonInteractiveTIMEngine#populateStudio()
	 */
	@Override
	public void populateStudio()
	throws SceneException
	{
		// the studio
		studio = new Studio();
		
		studio.setCamera(getStandardCamera());

		// the scene
		SceneObjectContainer scene = new SceneObjectContainer("the scene", null, studio);
		studio.setScene(scene);
		
		// initialise the scene and lights
		StudioInitialisationType.initialiseSceneAndLights(
				studioInitialisation,
				scene,
				studio
			);
		
		// ... and then adding scene objects to scene
		EditableScaledParametrisedSphere patternedSphere = new EditableScaledParametrisedSphere(
				"Patterned sphere", // description
				patternedSphereCentre, // centre
				patternedSphereRadius,	// radius
				new Vector3D(1, 1, 1),	// pole
				new Vector3D(1, 0, 0),	// phi0Direction
				0, Math.PI,	// sThetaMin, sThetaMax
				-Math.PI, Math.PI,	// sPhiMin, sPhiMax
				new SurfaceTiling(
						new SurfaceColour(DoubleColour.BLACK, DoubleColour.WHITE, false),	// like SurfaceColour.BLACK_SHINY, but doesn't throw a shadow
						new SurfaceColour(DoubleColour.GREY90, DoubleColour.WHITE, false),	// like SurfaceColour.GREY90_SHINY, but not shadow-throwing
						2*Math.PI/6,
						2*Math.PI/6
					),
				scene, studio);
		scene.addSceneObject(patternedSphere, showPatternedSphere);
		
		// create the inner cloak; first create a lens-simplicial complex...
		EditableLensSimplicialComplex abyssCloakI = new EditableLensSimplicialComplex(
				"Inner Abyss cloak",	// description
				scene,	// parent
				studio
			);
		// ... and initialise it as an ideal-lens cloak
		double vertexRadiusI = 0.02;
		abyssCloakI.setLensTypeRepresentingFace(showLensesCI?LensType.IDEAL_THIN_LENS:LensType.NONE);
		abyssCloakI.setShowStructureP(showCylindersCI);
		abyssCloakI.setVertexRadiusP(vertexRadiusI);
		abyssCloakI.setShowStructureV(false);
		abyssCloakI.setVertexRadiusV(vertexRadiusI);
		abyssCloakI.setSurfacePropertyP(new SurfaceColour(DoubleColour.RED, DoubleColour.WHITE, false));
		
		SimpleTranslation tI = new SimpleTranslation(baseCentreCI);
		
		double baseLensFCI = .2;	// TODO make something proper
		
		double h1PCI = 0.5;	// fractional height of the lower inner vertex
		abyssCloakI.initialiseToOmnidirectionalLens(
				h1PCI,	// physicalSpaceFractionalLowerInnerVertexHeight
				0.75,	// physicalSpaceFractionalUpperInnerVertexHeight
				// 1/(h1P h) + 1/(-h1V h) == 1/f, so
				// -1/h1V = h/f - 1/h1P, or
				// 1/h1V = 1/h1P - h/f, or
				// h1V = 1/(1/h1P - h/f)
				1./(-heightCI/baseLensFCI + 1/h1PCI),	// virtualSpaceFractionalLowerInnerVertexHeight
				tI.transformPosition(new Vector3D(0, 0, heightCI)),	// topVertex
				tI.transformPosition(new Vector3D(0, 0, 0)),	// baseCentre
				tI.transformPosition(new Vector3D(0, -heightCI/Math.sqrt(2.), 0))	// baseVertex1
			);
		
		scene.addSceneObject(abyssCloakI);

		
		// create the outer cloak; first create a lens-simplicial complex...
		EditableLensSimplicialComplex abyssCloakO = new EditableLensSimplicialComplex(
				"Outer Abyss cloak",	// description
				scene,	// parent
				studio
			);
		// ... and initialise it as an ideal-lens cloak
		double vertexRadiusO = 0.02;
		abyssCloakO.setLensTypeRepresentingFace(showLensesCO?LensType.IDEAL_THIN_LENS:LensType.NONE);
		abyssCloakO.setShowStructureP(showCylindersCO);
		abyssCloakO.setVertexRadiusP(vertexRadiusO);
		abyssCloakO.setShowStructureV(false);
		abyssCloakO.setVertexRadiusV(vertexRadiusO);
		abyssCloakO.setSurfacePropertyP(new SurfaceColour(DoubleColour.BLUE, DoubleColour.WHITE, false));
		
		SimpleTranslation tO = new SimpleTranslation(baseCentreCO);
		
		double baseLensFCO = 1;	// TODO make something proper

		double h1PCO = 0.5;	// fractional height of the lower inner vertex
		abyssCloakO.initialiseToOmnidirectionalLens(
				h1PCO,	// physicalSpaceFractionalLowerInnerVertexHeight
				0.75,	// physicalSpaceFractionalUpperInnerVertexHeight
				// 1/(h1P h) + 1/(-h1V h) == 1/f, so
				// -1/h1V = h/f - 1/h1P, or
				// 1/h1V = 1/h1P - h/f, or
				// h1V = 1/(1/h1P - h/f)
				1./(-heightCO/baseLensFCO + 1/h1PCO),	// virtualSpaceFractionalLowerInnerVertexHeight
				tO.transformPosition(new Vector3D(0, 0, heightCO)),	// topVertex
				tO.transformPosition(new Vector3D(0, 0, 0)),	// baseCentre
				tO.transformPosition(new Vector3D(0, heightCO/Math.sqrt(2.), 0))	// baseVertex1
			);
		
		scene.addSceneObject(abyssCloakO);
	}

	
	
	//
	// for interactive version
	//

	// the cloaks
	private JCheckBox showLensesCICheckBox, showLensesCOCheckBox;
	private JCheckBox showCylindersCICheckBox, showCylindersCOCheckBox;
	private LabelledVector3DPanel baseCentreCIPanel, baseCentreCOPanel;
	private LabelledDoublePanel heightCIPanel, heightCOPanel;
	// private LabelledDoublePanel virtualSpaceFractionalLowerInnerVertexHeightIPanel, virtualSpaceFractionalLowerInnerVertexHeightOPanel;
	
	// other scene objects
	private JComboBox<StudioInitialisationType> studioInitialisationComboBox;
	private JCheckBox showPatternedSphereCheckBox;
	private LabelledVector3DPanel patternedSphereCentrePanel;
	private JTextField patternedSphereCentreInfoTextField;
	private JButton patternedSphereCentreInfoButton;
	private LabelledDoublePanel patternedSphereRadiusPanel;

	// camera
	private LabelledVector3DPanel cameraViewCentrePanel;
	// private JButton setCameraViewCentreToCloakCentroidButton;
	private LabelledVector3DPanel cameraViewDirectionPanel;
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

		JPanel cloaksPanel = new JPanel();
		cloaksPanel.setLayout(new MigLayout("insets 0"));
		tabbedPane.addTab("Cloaks", cloaksPanel);

		
		// inner Abyss cloaks

		JPanel innerLensCloakPanel = new JPanel();
		innerLensCloakPanel.setLayout(new MigLayout("insets 0"));
		innerLensCloakPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Inner cloak"));
		cloaksPanel.add(innerLensCloakPanel, "wrap");

		showLensesCICheckBox = new JCheckBox("Show lenses");
		showLensesCICheckBox.setSelected(showLensesCI);
		innerLensCloakPanel.add(showLensesCICheckBox, "span");
		
		showCylindersCICheckBox = new JCheckBox("Show cylinders");
		showCylindersCICheckBox.setSelected(showCylindersCI);
		innerLensCloakPanel.add(showCylindersCICheckBox, "span");
		
		baseCentreCIPanel = new LabelledVector3DPanel("Base centre");
		baseCentreCIPanel.setVector3D(baseCentreCI);
		innerLensCloakPanel.add(baseCentreCIPanel, "span");
		
		heightCIPanel = new LabelledDoublePanel("Height");
		heightCIPanel.setNumber(heightCI);
		innerLensCloakPanel.add(heightCIPanel, "span");
		
//		virtualSpaceFractionalLowerInnerVertexHeightIPanel = new LabelledDoublePanel("fractional virtual-space height above base of lower inner vertex");
//		virtualSpaceFractionalLowerInnerVertexHeightIPanel.setNumber(virtualSpaceFractionalLowerInnerVertexHeightI);
//		innerLensCloakPanel.add(virtualSpaceFractionalLowerInnerVertexHeightIPanel, "span");
		
		// outer Abyss cloak

		JPanel outerLensCloakPanel = new JPanel();
		outerLensCloakPanel.setLayout(new MigLayout("insets 0"));
		outerLensCloakPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Outer cloak"));
		cloaksPanel.add(outerLensCloakPanel, "wrap");

		showLensesCOCheckBox = new JCheckBox("Show lenses");
		showLensesCOCheckBox.setSelected(showLensesCO);
		outerLensCloakPanel.add(showLensesCOCheckBox, "span");
		
		showCylindersCOCheckBox = new JCheckBox("Show cylinders");
		showCylindersCOCheckBox.setSelected(showCylindersCO);
		outerLensCloakPanel.add(showCylindersCOCheckBox, "span");
		
		baseCentreCOPanel = new LabelledVector3DPanel("Base centre");
		baseCentreCOPanel.setVector3D(baseCentreCO);
		outerLensCloakPanel.add(baseCentreCOPanel, "span");
		
		heightCOPanel = new LabelledDoublePanel("Height");
		heightCOPanel.setNumber(heightCO);
		outerLensCloakPanel.add(heightCOPanel, "span");
		
//		virtualSpaceFractionalLowerInnerVertexHeightOPanel = new LabelledDoublePanel("fractional virtual-space height above base of lower inner vertex");
//		virtualSpaceFractionalLowerInnerVertexHeightOPanel.setNumber(virtualSpaceFractionalLowerInnerVertexHeightO);
//		outerLensCloakPanel.add(virtualSpaceFractionalLowerInnerVertexHeightOPanel, "span");
		
		// common to both cloaks
		


		//
		// Other scene-objects panel
		//
		
		JPanel otherObjectsPanel = new JPanel();
		otherObjectsPanel.setLayout(new MigLayout("insets 0"));
		tabbedPane.addTab("Other scene objects", otherObjectsPanel);

		studioInitialisationComboBox = new JComboBox<StudioInitialisationType>(StudioInitialisationType.limitedValuesForBackgrounds);
		studioInitialisationComboBox.setSelectedItem(studioInitialisation);
		otherObjectsPanel.add(GUIBitsAndBobs.makeRow("Initialise backdrop to", studioInitialisationComboBox), "span");
		
		JPanel patternedSpherePanel = new JPanel();
		patternedSpherePanel.setBorder(GUIBitsAndBobs.getTitledBorder("Patterned sphere"));
		patternedSpherePanel.setLayout(new MigLayout("insets 0"));
		otherObjectsPanel.add(patternedSpherePanel, "span");
		
		showPatternedSphereCheckBox = new JCheckBox("Show");
		showPatternedSphereCheckBox.setSelected(showPatternedSphere);
		patternedSpherePanel.add(showPatternedSphereCheckBox, "span");
		
		patternedSphereCentrePanel = new LabelledVector3DPanel("Centre");
		patternedSphereCentrePanel.setVector3D(patternedSphereCentre);
		patternedSpherePanel.add(patternedSphereCentrePanel, "span");

		patternedSphereCentreInfoTextField = new JTextField(40);
		patternedSphereCentreInfoTextField.setEditable(false);
		patternedSphereCentreInfoTextField.setText("Click on Update button to show info");
		patternedSphereCentreInfoButton = new JButton("Update");
		patternedSphereCentreInfoButton.addActionListener(this);
		patternedSpherePanel.add(GUIBitsAndBobs.makeRow(patternedSphereCentreInfoTextField, patternedSphereCentreInfoButton), "span");
		
		patternedSphereRadiusPanel = new LabelledDoublePanel("Radius");
		patternedSphereRadiusPanel.setNumber(patternedSphereRadius);
		patternedSpherePanel.add(patternedSphereRadiusPanel, "span");

		
		//
		// camera panel
		//
		
		JPanel cameraPanel = new JPanel();
		cameraPanel.setLayout(new MigLayout("insets 0"));
		tabbedPane.addTab("Cameras", cameraPanel);
				
		JPanel cameraViewCentreJPanel = new JPanel();
		cameraViewCentreJPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Centre of view"));
		cameraViewCentreJPanel.setLayout(new MigLayout("insets 0"));
		cameraPanel.add(cameraViewCentreJPanel, "span");

		cameraViewCentrePanel = new LabelledVector3DPanel("Position");
		cameraViewCentrePanel.setVector3D(cameraViewCentre);
		cameraViewCentreJPanel.add(cameraViewCentrePanel, "span");
				
		cameraViewDirectionPanel = new LabelledVector3DPanel("View direction");
		cameraViewDirectionPanel.setVector3D(cameraViewDirection);
		cameraPanel.add(cameraViewDirectionPanel, "span");
		
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
		cameraPanel.add(cameraFocussingDistancePanel, "span");
		
	}
	
	/**
	 * called before rendering;
	 * override when adding fields
	 */
	@Override
	protected void acceptValuesInInteractiveControlPanel()
	{
		super.acceptValuesInInteractiveControlPanel();

		showLensesCI = showLensesCICheckBox.isSelected();
		showCylindersCI = showCylindersCICheckBox.isSelected();
		baseCentreCI = baseCentreCIPanel.getVector3D();
		heightCI = heightCIPanel.getNumber();
		// virtualSpaceFractionalLowerInnerVertexHeightI = virtualSpaceFractionalLowerInnerVertexHeightIPanel.getNumber();

		showLensesCO = showLensesCOCheckBox.isSelected();
		showCylindersCO = showCylindersCOCheckBox.isSelected();
		baseCentreCO = baseCentreCOPanel.getVector3D();
		heightCO = heightCOPanel.getNumber();
		// virtualSpaceFractionalLowerInnerVertexHeightO = virtualSpaceFractionalLowerInnerVertexHeightOPanel.getNumber();

		// other scene objects
		studioInitialisation = (StudioInitialisationType)(studioInitialisationComboBox.getSelectedItem());
		showPatternedSphere = showPatternedSphereCheckBox.isSelected();
		patternedSphereCentre = patternedSphereCentrePanel.getVector3D();
		patternedSphereRadius = patternedSphereRadiusPanel.getNumber();

		// cameras
		cameraViewCentre = cameraViewCentrePanel.getVector3D();
		cameraViewDirection = cameraViewDirectionPanel.getVector3D();
		cameraDistance = cameraDistancePanel.getNumber();
		cameraHorizontalFOVDeg = cameraHorizontalFOVDegPanel.getNumber();
		cameraApertureSize = (ApertureSizeType)(cameraApertureSizeComboBox.getSelectedItem());
		cameraFocussingDistance = cameraFocussingDistancePanel.getNumber();

	}
	
		
	/**
	 * The main method, required so that this class can run as a Java application
	 * @param args
	 */
	public static void main(final String[] args)
	{
//        Runnable r = new NonInteractiveTIM();
//
//        EventQueue.invokeLater(r);
		(new IdealLensWormholeExplorer()).run();
	}
}
