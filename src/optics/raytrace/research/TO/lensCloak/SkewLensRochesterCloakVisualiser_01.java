package optics.raytrace.research.TO.lensCloak;

import java.awt.event.ActionEvent;
import java.io.PrintStream;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import math.*;
import net.miginfocom.swing.MigLayout;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.surfaces.IdealThinLensSurfaceSimple;
import optics.raytrace.surfaces.SurfaceColour;
import optics.raytrace.exceptions.SceneException;
import optics.raytrace.NonInteractiveTIMActionEnum;
import optics.raytrace.NonInteractiveTIMEngine;
import optics.raytrace.GUI.cameras.RenderQualityEnum;
import optics.raytrace.GUI.lowLevel.ApertureSizeType;
import optics.raytrace.GUI.lowLevel.DoublePanel;
import optics.raytrace.GUI.lowLevel.GUIBitsAndBobs;
import optics.raytrace.GUI.lowLevel.LabelledDoublePanel;
import optics.raytrace.GUI.lowLevel.LabelledVector3DPanel;
import optics.raytrace.GUI.sceneObjects.EditableFramedRectangle;
import optics.raytrace.core.Studio;
import optics.raytrace.core.StudioInitialisationType;
import optics.raytrace.core.SurfaceProperty;


/**
 * Simulate the visual appearance of a skew-lens rochester cloak
 * 
 * @author Johannes Courtial
 */
public class SkewLensRochesterCloakVisualiser_01 extends NonInteractiveTIMEngine
{
	//
	// the skew-lens-Rochester cloak
	//
	
	/**
	 * the angle by which the normals of all the lenses are inclined relative to the optical axis 
	 */
	double lensInclinationAngleDeg;
	
	/**
	 * magnification factor of both lens telescopes
	 */
	double telescopeMagnification;
	
	//
	// the rest of the scene
	//
	
	/**
	 * Determines how to initialise the backdrop
	 */
	private StudioInitialisationType studioInitialisation;
	
	
	
	//
	// cameras
	//
	

	
	/**
	 * Constructor.
	 * Sets all parameters.
	 */
	public SkewLensRochesterCloakVisualiser_01()
	{
		super();
		
		// set to true for interactive version
		nonInteractiveTIMAction = NonInteractiveTIMActionEnum.INTERACTIVE;
		
		// set all parameters
		
		renderQuality = RenderQualityEnum.DRAFT;
		
		// the skew-lens-Rochester cloak
		lensInclinationAngleDeg = 10; 
		telescopeMagnification = 2;
		
		// (outside) camera parameters; the camera is set by getStandardCamera()
		// @see optics.raytrace.NonInteractiveTIMEngine.getStandardCamera()
		cameraViewCentre = new Vector3D(0, 0, 0);
		cameraViewDirection = new Vector3D(0.15, 0, 1);
		cameraDistance = 10;
		cameraHorizontalFOVDeg = 20;
//		 * @see optics.raytrace.NonInteractiveTIMEngine.cameraMaxTraceLevel
//		 * @see optics.raytrace.NonInteractiveTIMEngine.cameraPixelsX
//		 * @see optics.raytrace.NonInteractiveTIMEngine.cameraPixelsY
		cameraApertureSize = ApertureSizeType.PINHOLE;
		cameraFocussingDistance = cameraDistance;

		
		// blur quality if test = false; make this better in the non-interactive version, as this is used to produce publication-quality images
		// standardCameraBlurQuality = (interactive?QualityType.GOOD:QualityType.SUPER);	// this is the camera's blur quality if test=false


		// other scene objects
		
		studioInitialisation = StudioInitialisationType.LATTICE;	// the backdrop

		
		if(nonInteractiveTIMAction == NonInteractiveTIMActionEnum.INTERACTIVE)
		{
			windowTitle = "Dr TIM's skew-lens-Rochester-cloak visualiser";
			windowWidth = 1500;
			windowHeight = 650;
		}
	}
	

	@Override
	public String getFirstPartOfFilename()
	{
		return "SkewLensRochesterCloakVisualiser";	// the name
	}

	@Override
	public void writeParameters(PrintStream printStream)
	{
		// write any parameters not defined in NonInteractiveTIMEngine
		// printStream.println("parameterName = "+parameterName);
		
		// the skew-lens-Rochester cloak
		printStream.println("lensInclinationAngleDeg = "+lensInclinationAngleDeg);
		printStream.println("telescopeMagnification = "+telescopeMagnification);
		
		// other scene objects

		printStream.println("studioInitialisation = "+studioInitialisation);	// the backdrop
	
		// write all parameters defined in NonInteractiveTIMEngine
		super.writeParameters(printStream);
	}

	private EditableFramedRectangle createLens(
			String description,
			double principalPointZ,
			double lensWidth,
			double inclinationAngle,
			double focalLength,
			double frameRadius,
			SurfaceProperty frameSurfaceProperty,
			SceneObjectContainer scene
		)
	{
		IdealThinLensSurfaceSimple lensSurface;
		lensSurface = new IdealThinLensSurfaceSimple(
				new Vector3D(0, 0, principalPointZ),	// lensCentre
				new Vector3D(0, Math.sin(inclinationAngle), Math.cos(inclinationAngle)),	// opticalAxisDirection
				focalLength,	// focalLength
				0.96,	// transmissionCoefficient
				true	// shadowThrowing
			);

		EditableFramedRectangle lens;
		lens = new EditableFramedRectangle(
				description,	// description
				new Vector3D(-0.5*lensWidth, -0.5*lensWidth*Math.cos(inclinationAngle), principalPointZ-0.5*lensWidth*Math.sin(inclinationAngle)),	// corner
				new Vector3D(lensWidth, 0, 0),	// widthVector
				new Vector3D(0, lensWidth*Math.cos(inclinationAngle), -lensWidth*Math.sin(inclinationAngle)),	// heightVector
				frameRadius,	// frameRadius
				lensSurface,	// windowSurfaceProperty
				frameSurfaceProperty,	// frameSurfaceProperty
				true,	// showFrames
				scene,	// parent
				studio
		);
		
		return lens;
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
		
		studio.setCamera(getStandardCamera());
		
		// now add something fascinating
		
		// two telescopes; the lenses in each telescope are inclined by +/- alpha
		double alpha = MyMath.deg2rad(lensInclinationAngleDeg);
		double lensWidth = 2;
		double axialSeparation = 1;
		double telescope1CornerZ = -1;
		
		scene.addSceneObject(createLens(
				"Telescope 1, lens 1",	// description
				telescope1CornerZ+0.5*axialSeparation,	// principalPointZ
				lensWidth,
				alpha,	// inclinationAngle
				axialSeparation
				*telescopeMagnification/(telescopeMagnification+1)	// divide axial separation into (telescopeMagnification+1) pieces,
							// with the projected focal length of this lens being telescopeMagnification of those;
							// then, in order to add up to the axial separation, the projected focal length of the other lens
							// has to be the remaining piece; the ratio of these (projected and actual) focal lengths is then
							// telescopeMagnification
				*Math.cos(alpha)	// multiply by cos(alpha) to get from the projected to the (unprojected) focal length
				,	// focalLength
				0.01*lensWidth,	// frameRadius
				SurfaceColour.RED_SHINY,	// frameSurfaceProperty
				scene
			));

		scene.addSceneObject(createLens(
				"Telescope 1, lens 2",	// description
				telescope1CornerZ-0.5*axialSeparation,	// principalPointZ
				lensWidth,
				-alpha,	// inclinationAngle
				axialSeparation
				*1/(telescopeMagnification+1)	// see lens 1
				*Math.cos(alpha)	// multiply by cos(alpha) to get from the projected to the (unprojected) focal length
				,	// focalLength
				0.011*lensWidth,	// frameRadius
				SurfaceColour.BLUE_SHINY,	// frameSurfaceProperty
				scene
			));
	}
	
	
	//
	// for interactive version
	//
	
	// skew-lens-Rochester cloak
	private LabelledDoublePanel lensInclinationAngleDegPanel, telescopeMagnificationPanel;
	
	// other scene objects
	private JComboBox<StudioInitialisationType> studioInitialisationComboBox;
	
	// camera
	private LabelledVector3DPanel cameraViewCentrePanel;
	private JButton setCameraViewCentreToCloakCentroidButton;
	private JButton setCameraViewCentreToPatternedSphereCentreButton;
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
		
		//
		// skew-lens-Rochester-cloak panel
		//
		
		JPanel lensCloakPanel = new JPanel();
		lensCloakPanel.setLayout(new MigLayout("insets 0"));
		tabbedPane.addTab("Skew-lens-Rochester cloak", lensCloakPanel);
		
		lensInclinationAngleDegPanel = new LabelledDoublePanel("lens inclination (degrees)");
		lensInclinationAngleDegPanel.setNumber(lensInclinationAngleDeg);
		lensCloakPanel.add(lensInclinationAngleDegPanel, "wrap");
		
		telescopeMagnificationPanel = new LabelledDoublePanel("telescope magnification");
		telescopeMagnificationPanel.setNumber(telescopeMagnification);
		lensCloakPanel.add(telescopeMagnificationPanel, "wrap");
		
		
		//
		// Other scene-objects panel
		//
		
		JPanel otherObjectsPanel = new JPanel();
		otherObjectsPanel.setLayout(new MigLayout("insets 0"));
		tabbedPane.addTab("Other scene objects", otherObjectsPanel);

		studioInitialisationComboBox = new JComboBox<StudioInitialisationType>(StudioInitialisationType.limitedValuesForBackgrounds);
		studioInitialisationComboBox.setSelectedItem(studioInitialisation);
		otherObjectsPanel.add(GUIBitsAndBobs.makeRow("Initialise backdrop to", studioInitialisationComboBox), "span");
		

		
		//
		// camera panel
		//
		
		JPanel cameraPanel = new JPanel();
		cameraPanel.setLayout(new MigLayout("insets 0"));
		tabbedPane.addTab("Camera", cameraPanel);
						
		JPanel cameraViewCentreJPanel = new JPanel();
		cameraViewCentreJPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Centre of view"));
		cameraViewCentreJPanel.setLayout(new MigLayout("insets 0"));
		cameraPanel.add(cameraViewCentreJPanel, "span");

		cameraViewCentrePanel = new LabelledVector3DPanel("Position");
		cameraViewCentrePanel.setVector3D(cameraViewCentre);
		cameraViewCentreJPanel.add(cameraViewCentrePanel, "span");
		
		setCameraViewCentreToCloakCentroidButton = new JButton("Set to cloak centroid");
		setCameraViewCentreToCloakCentroidButton.addActionListener(this);
		
		setCameraViewCentreToPatternedSphereCentreButton = new JButton("Set to centre of patterned sphere");
		setCameraViewCentreToPatternedSphereCentreButton.addActionListener(this);
		
		cameraViewCentreJPanel.add(
				GUIBitsAndBobs.makeRow(
						setCameraViewCentreToCloakCentroidButton,
						setCameraViewCentreToPatternedSphereCentreButton
				),
				"span"
			);
		
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
		
		// skew-lens-Rochester cloak
		lensInclinationAngleDeg = lensInclinationAngleDegPanel.getNumber();
		telescopeMagnification = telescopeMagnificationPanel.getNumber();
		
		// other scene objects
		studioInitialisation = (StudioInitialisationType)(studioInitialisationComboBox.getSelectedItem());
		
		// cameras
		cameraViewCentre = cameraViewCentrePanel.getVector3D();
		cameraViewDirection = cameraViewDirectionPanel.getVector3D();
		cameraDistance = cameraDistancePanel.getNumber();
		cameraHorizontalFOVDeg = cameraHorizontalFOVDegPanel.getNumber();
		cameraApertureSize = (ApertureSizeType)(cameraApertureSizeComboBox.getSelectedItem());
		cameraFocussingDistance = cameraFocussingDistancePanel.getNumber();
	}
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		super.actionPerformed(e);
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
		(new SkewLensRochesterCloakVisualiser()).run();
	}
}
