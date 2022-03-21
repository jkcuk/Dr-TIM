package optics.raytrace.research.adaptiveTWs;

import java.io.PrintStream;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import math.*;
import net.miginfocom.swing.MigLayout;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.surfaces.LensHologram;
import optics.raytrace.surfaces.PhaseHologramOfLohmannLensPart;
import optics.raytrace.exceptions.SceneException;
import optics.raytrace.NonInteractiveTIMActionEnum;
import optics.raytrace.NonInteractiveTIMEngine;
import optics.raytrace.GUI.cameras.RenderQualityEnum;
import optics.raytrace.GUI.lowLevel.ApertureSizeType;
import optics.raytrace.GUI.lowLevel.DoublePanel;
import optics.raytrace.GUI.lowLevel.GUIBitsAndBobs;
import optics.raytrace.GUI.lowLevel.LabelledDoublePanel;
import optics.raytrace.GUI.lowLevel.LabelledVector3DPanel;
import optics.raytrace.GUI.sceneObjects.EditableScaledParametrisedCentredParallelogram;
import optics.raytrace.GUI.sceneObjects.EditableScaledParametrisedDisc;
import optics.raytrace.core.Studio;
import optics.raytrace.core.StudioInitialisationType;


/**
 * Simulate the visual appearance of a holographic Alvarez-Lohmann lens, and the view through it.
 * 
 * @author Johannes Courtial
 */
public class AlvarezLohmannLensExplorer extends NonInteractiveTIMEngine implements DocumentListener
{
	/**
	 * focalPowerOverDeltaX, i.e. the constant of proportionality between the offset in the x direction between the two components of a Lohmann lens and the resulting focal power
	 */
	private double focalPowerOverDeltaX;
		
	/**
	 * deltaX, i.e. offset in x direction of part 1 relative to part 2
	 */
	private double deltaX;

	/**
	 * deltaY, i.e. offset in y direction of part 1 relative to part 2
	 */
	private double deltaY;
			
	/**
	 * rotation angle of part 1 around its normal
	 */
	private double deltaPhiDeg;
	
	/**
	 * z separation of parts
	 */
	private double dz;

	/**
	 * show part 1
	 */
	private boolean showPart1;

	/**
	 * show part 2
	 */
	private boolean showPart2;
	
	private boolean showCorrespondingLens;

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
	public AlvarezLohmannLensExplorer()
	{
		super();
		
		// set to true for interactive version
		nonInteractiveTIMAction = NonInteractiveTIMActionEnum.INTERACTIVE;
		
		// set all parameters
		
		renderQuality = RenderQualityEnum.DRAFT;
		
		// parameters
		focalPowerOverDeltaX = 1;
		dz = 0.00001;
		deltaX = 0;
		deltaY = 0;
		deltaPhiDeg = 0;
		showPart1 = true;
		showPart2 = true;
		showCorrespondingLens = false;
		
		studioInitialisation = StudioInitialisationType.LATTICE;	// the backdrop

		// camera
		cameraViewCentre = new Vector3D(0, 0, 0);
		cameraDistance = 8;
		cameraViewDirection = new Vector3D(0, 0, 1);
		cameraHorizontalFOVDeg = 10;
		cameraApertureSize = ApertureSizeType.PINHOLE;
		cameraFocussingDistance = 10;
		
		if(nonInteractiveTIMAction == NonInteractiveTIMActionEnum.INTERACTIVE)
		{
			windowTitle = "Dr TIM's Alvarez-Lohmann-lens explorer";
			windowWidth = 1500;
			windowHeight = 650;
		}
	}
	

	@Override
	public String getClassName()
	{
		return "AlvarezLohmannLensExplorer"	// the name
//				+ "pOverDeltaX="+focalPowerOverDeltaX
//				+ " dx="+deltaX
//				+ " dy="+deltaY
//				+ " dPhi="+deltaPhiDeg
//				+ (showPart1?" part1":"")
//				+ (showPart2?" part2":"")
//				+ (showCorrespondingLens?" lens":"")
//				+ " dz="+dz
//				+ " backdrop="+studioInitialisation.toString()
//				+ " cD="+cameraDistance
//				+ " cVD="+cameraViewDirection
//				+ " cFOV="+cameraHorizontalFOVDeg
//				+ " cAS="+cameraApertureSize
//				+ " cFD="+cameraFocussingDistance
				;
	}
	
	@Override
	public void writeParameters(PrintStream printStream)
	{
		// write any parameters not defined in NonInteractiveTIMEngine
		
		printStream.println("focalPowerOverDeltaX="+focalPowerOverDeltaX);
		printStream.println("deltaX="+deltaX);
		printStream.println("deltaY="+deltaY);
		printStream.println("deltaPhiDeg="+deltaPhiDeg);
		printStream.println("dz="+dz);
		printStream.println("showPart1="+showPart1);
		printStream.println("showPart2="+showPart2);
		printStream.println("studioInitialisation="+studioInitialisation);

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
		
		Vector3D up = new Vector3D(0, 1, 0);
		double zSeparation = dz;
		
		double deltaPhi = MyMath.deg2rad(deltaPhiDeg);
		Vector3D normal = new Vector3D(0, 0, 1);
		Vector3D right = Vector3D.crossProduct(normal, up).getNormalised();
		
		Vector3D centre1 = new Vector3D(-0.5*deltaX, -0.5*deltaY, -0.5*zSeparation);
		double c1 = Math.cos(0.5*deltaPhi);
		double s1 = Math.sin(0.5*deltaPhi);
		Vector3D xHat1 = Vector3D.sum(right.getProductWith(c1), up.getProductWith(s1));
		Vector3D yHat1 = Vector3D.sum(right.getProductWith(-s1), up.getProductWith(c1));
		
		scene.addSceneObject(new EditableScaledParametrisedCentredParallelogram(
				"Hologram of Alvarez-Lohmann lens part 1",	// description
				centre1,	// centre
				xHat1,	// spanVector1
				yHat1,	// spanVector2
				new PhaseHologramOfLohmannLensPart(
						focalPowerOverDeltaX,
						centre1,	// principalPoint
						xHat1,	// xHat
						yHat1,	// yHat
						0.96,	// throughputCoefficient
						true	// shadowThrowing
					),	// surfaceProperty
				scene,	// parent
				studio
			),
			showPart1
		);

		Vector3D centre2 = new Vector3D(0.5*deltaX, 0.5*deltaY, +0.5*zSeparation);
		double c2 = Math.cos(-0.5*deltaPhi);
		double s2 = Math.sin(-0.5*deltaPhi);
		Vector3D xHat2 = Vector3D.sum(right.getProductWith(c2), up.getProductWith(s2));
		Vector3D yHat2 = Vector3D.sum(right.getProductWith(-s2), up.getProductWith(c2));
		
		scene.addSceneObject(new EditableScaledParametrisedCentredParallelogram(
				"Hologram of Alvarez-Lohmann lens part 2",	// description
				centre2,	// centre
				xHat2,	// spanVector1
				yHat2,	// spanVector2
				new PhaseHologramOfLohmannLensPart(
						-focalPowerOverDeltaX,
						centre2,	// principalPoint
						xHat2,	// xHat
						yHat2,	// yHat
						0.96,	// throughputCoefficient
						true	// shadowThrowing
					),	// surfaceProperty
				scene,	// parent
				studio
			),
			showPart2
		);
		
		Vector3D lensCentre = new Vector3D(0, 0, 0);
		Vector3D lensNormal = new Vector3D(0, 0, 1);
		double f = calculateEquivalentFocalLength();
		
		scene.addSceneObject(new EditableScaledParametrisedDisc(
				"Equivalent lens (f="+f,	// description
				lensCentre,	// centre
				lensNormal,	// normal
				0.5,	// radius
				new LensHologram(
						lensNormal,	// opticalAxisDirectionOutwards
						lensCentre,	// principalPoint
						f,	// focalLength
						0.96,	// transmissionCoefficient
						true	// shadowThrowing
						) ,	// surfaceProperty
				scene,	// parent
				studio
				),
				showCorrespondingLens
				);

		// the camera
		studio.setCamera(getStandardCamera());
	}
	
	public double calculateEquivalentFocalLength()
	{
		return 1/(focalPowerOverDeltaX*deltaX);
	}

	
	
	//
	// for interactive version
	//
	
	private LabelledDoublePanel focalPowerOverDeltaXPanel, dzPanel, deltaXPanel, deltaYPanel;
	private DoublePanel deltaPhiDegPanel, equivalentFocalLengthPanel;
	private JCheckBox showPart1CheckBox, showPart2CheckBox, showCorrespondingLensCheckBox;
	private JComboBox<StudioInitialisationType> studioInitialisationComboBox;

	// camera stuff
	// private LabelledVector3DPanel cameraViewDirectionPanel;
	private LabelledDoublePanel cameraDistancePanel;
	private LabelledVector3DPanel cameraViewDirectionPanel;
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

		// the main tabbed pane, with "Scene" and "Camera" tabs
		JTabbedPane sceneCameraTabbedPane = new JTabbedPane();
		interactiveControlPanel.add(sceneCameraTabbedPane, "span");
		
		//
		// the Lenslet arrays panel
		//

		JPanel scenePanel = new JPanel();
		scenePanel.setLayout(new MigLayout("insets 0"));
		sceneCameraTabbedPane.addTab("Scene", scenePanel);

		//
		// the Lohmann-lens-initialisation panel
		//

		focalPowerOverDeltaXPanel = new LabelledDoublePanel("focal power / delta_x");
		focalPowerOverDeltaXPanel.setNumber(focalPowerOverDeltaX);
		focalPowerOverDeltaXPanel.addDocumentListener(this);
		focalPowerOverDeltaXPanel.setToolTipText("Focal power / delta_x");
		scenePanel.add(focalPowerOverDeltaXPanel, "span");
		
		deltaXPanel = new LabelledDoublePanel("delta_x");
		deltaXPanel.setNumber(deltaX);
		deltaXPanel.addDocumentListener(this);
		deltaXPanel.setToolTipText("delta_x, i.e. x offset of part 1 relative to part 2; this changes the focal power of the combination");
		scenePanel.add(deltaXPanel, "span");

		deltaYPanel = new LabelledDoublePanel("delta_y");
		deltaYPanel.setNumber(deltaY);
		deltaYPanel.setToolTipText("delta_y, i.e. y offset of part 1 relative to part 2; this changes the parameter for oblique astigmatism of the combination");	// TODO which aberration is this?
		scenePanel.add(deltaYPanel, "span");

		deltaPhiDegPanel = new DoublePanel();
		deltaPhiDegPanel.setNumber(deltaPhiDeg);
		deltaPhiDegPanel.setToolTipText("Angle by which part 1 is rotated relative to part 2 around the optical axis");
		scenePanel.add(GUIBitsAndBobs.makeRow("delta_phi", deltaPhiDegPanel, "degrees"), "span");

		showPart1CheckBox = new JCheckBox("Show phase hologram of front part");
		showPart1CheckBox.setSelected(showPart1);
		scenePanel.add(showPart1CheckBox, "span");

		showPart2CheckBox = new JCheckBox("Show phase hologram of back part");
		showPart2CheckBox.setSelected(showPart2);
		scenePanel.add(showPart2CheckBox, "span");
		
		showCorrespondingLensCheckBox = new JCheckBox("");
		showCorrespondingLensCheckBox.setSelected(showCorrespondingLens);
		equivalentFocalLengthPanel = new DoublePanel();
		equivalentFocalLengthPanel.setEnabled(false);
		equivalentFocalLengthPanel.setNumber(calculateEquivalentFocalLength());
		scenePanel.add(GUIBitsAndBobs.makeRow(showCorrespondingLensCheckBox, "Show corresponding lens of focal length ", equivalentFocalLengthPanel), "span");

		dzPanel = new LabelledDoublePanel("delta_z");
		dzPanel.setNumber(dz);
		dzPanel.setToolTipText("The two parts are separated in the z direction by delta_z");
		scenePanel.add(dzPanel, "span");
		
		studioInitialisationComboBox = new JComboBox<StudioInitialisationType>(StudioInitialisationType.limitedValuesForBackgrounds);
		studioInitialisationComboBox.setSelectedItem(studioInitialisation);
		scenePanel.add(GUIBitsAndBobs.makeRow("Background", studioInitialisationComboBox), "span");
		

		//
		// the Camera panel
		//
		
		JPanel cameraPanel = new JPanel();
		cameraPanel.setLayout(new MigLayout("insets 0"));
		sceneCameraTabbedPane.addTab("Camera", cameraPanel);

		// camera stuff
		
//		cameraViewDirectionPanel = new LabelledVector3DPanel("View direction");
//		cameraViewDirectionPanel.setVector3D(cameraViewDirection);
//		cameraPanel.add(cameraViewDirectionPanel, "span");
		
		cameraDistancePanel = new LabelledDoublePanel("Camera distance");
		cameraDistancePanel.setNumber(cameraDistance);
		cameraPanel.add(cameraDistancePanel, "span");
		
		cameraViewDirectionPanel = new LabelledVector3DPanel("View direction");
		cameraViewDirectionPanel.setVector3D(cameraViewDirection);
		cameraPanel.add(cameraViewDirectionPanel, "span");
		
		cameraHorizontalFOVDegPanel = new DoublePanel();
		cameraHorizontalFOVDegPanel.setNumber(cameraHorizontalFOVDeg);
		cameraPanel.add(GUIBitsAndBobs.makeRow("Horizontal FOV", cameraHorizontalFOVDegPanel, "°"), "span");
		
		cameraApertureSizeComboBox = new JComboBox<ApertureSizeType>(ApertureSizeType.values());
		cameraApertureSizeComboBox.setSelectedItem(cameraApertureSize);
		cameraPanel.add(GUIBitsAndBobs.makeRow("Camera aperture", cameraApertureSizeComboBox), "span");		
		
		cameraFocussingDistancePanel = new LabelledDoublePanel("Focussing distance");
		cameraFocussingDistancePanel.setNumber(cameraFocussingDistance);
		cameraPanel.add(cameraFocussingDistancePanel);
	}
	
	/**
	 * called before rendering;
	 * override when adding fields
	 */
	@Override
	protected void acceptValuesInInteractiveControlPanel()
	{
		super.acceptValuesInInteractiveControlPanel();
		
		focalPowerOverDeltaX = focalPowerOverDeltaXPanel.getNumber();
		deltaX = deltaXPanel.getNumber();
		deltaY = deltaYPanel.getNumber();
		deltaPhiDeg = deltaPhiDegPanel.getNumber();
		dz = dzPanel.getNumber();
		
		showPart1 = showPart1CheckBox.isSelected();
		showPart2 = showPart2CheckBox.isSelected();
		showCorrespondingLens = showCorrespondingLensCheckBox.isSelected();
		studioInitialisation = (StudioInitialisationType)(studioInitialisationComboBox.getSelectedItem());
		
		// cameraViewDirection = cameraViewDirectionPanel.getVector3D();
		cameraDistance = cameraDistancePanel.getNumber();
		cameraViewDirection = cameraViewDirectionPanel.getVector3D();
		cameraHorizontalFOVDeg = cameraHorizontalFOVDegPanel.getNumber();
		cameraApertureSize = (ApertureSizeType)(cameraApertureSizeComboBox.getSelectedItem());
		cameraFocussingDistance = cameraFocussingDistancePanel.getNumber();
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
		(new AlvarezLohmannLensExplorer()).run();
	}


	@Override
	public void insertUpdate(DocumentEvent e) {
		acceptValuesInInteractiveControlPanel();
		equivalentFocalLengthPanel.setNumber(calculateEquivalentFocalLength());		
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		acceptValuesInInteractiveControlPanel();
		equivalentFocalLengthPanel.setNumber(calculateEquivalentFocalLength());		
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		acceptValuesInInteractiveControlPanel();
		equivalentFocalLengthPanel.setNumber(calculateEquivalentFocalLength());		
	}
}
