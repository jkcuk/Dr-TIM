package optics.raytrace.research.TO.lensCloak;

import java.awt.event.ActionEvent;
import java.io.PrintStream;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import math.*;
import net.miginfocom.swing.MigLayout;
import optics.raytrace.sceneObjects.FresnelLensShaped;
import optics.raytrace.sceneObjects.ParametrisedParallelogram;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectUnion;
import optics.raytrace.simplicialComplex.LensType;
import optics.raytrace.surfaces.IdealThinLensSurfaceSimple;
import optics.raytrace.surfaces.Point2PointImagingPhaseHologram;
import optics.raytrace.surfaces.SemiTransparent;
import optics.raytrace.surfaces.SurfaceColour;
import optics.raytrace.surfaces.SurfaceTiling;
import optics.raytrace.exceptions.SceneException;
import optics.DoubleColour;
import optics.raytrace.NonInteractiveTIMActionEnum;
import optics.raytrace.NonInteractiveTIMEngine;
import optics.raytrace.GUI.cameras.RenderQualityEnum;
import optics.raytrace.GUI.lowLevel.ApertureSizeType;
import optics.raytrace.GUI.lowLevel.DoublePanel;
import optics.raytrace.GUI.lowLevel.GUIBitsAndBobs;
import optics.raytrace.GUI.lowLevel.LabelledDoublePanel;
import optics.raytrace.GUI.lowLevel.LabelledVector3DPanel;
import optics.raytrace.GUI.sceneObjects.EditableFramedRectangle;
import optics.raytrace.GUI.sceneObjects.EditableScaledParametrisedSphere;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.Studio;
import optics.raytrace.core.StudioInitialisationType;
import optics.raytrace.core.SurfaceProperty;


/**
 * Simulate the visual appearance of a skew-lens rochester cloak
 * 
 * @author Johannes Courtial
 */
public class SkewLensRochesterCloakVisualiser_02 extends NonInteractiveTIMEngine
{
	//
	// the skew-lens-Rochester cloak
	//
	
	// the principal points lie along the z axis, at positions (0, 0, zi)

	/**
	 * z coordinate of principal point of lens 1
	 */
	private double z1;
	
	/**
	 * z coordinate of principal point of lens 2
	 */
	private double z2;
	
	/**
	 * z coordinate of principal point of lens 3
	 */
	private double z3;
	
	/**
	 * z coordinate of principal point of lens 4
	 */
	private double z4;
	
	// lens L1 is in the plane z = z1, the others intersect with it in the line y=y0, z=z1
	private double y0;

	// focal length of L1 (the other focal lengths are calculated from f1 and the geometry)
	private double f1;

	/**
	 */
	private LensType lensType;

	/**
	 * show lens 1?
	 */
	private boolean showLens1;

	/**
	 * show lens 2?
	 */
	private boolean showLens2;

	/**
	 * show lens 3?
	 */
	private boolean showLens3;

	/**
	 * show lens 4?
	 */
	private boolean showLens4;

	/**
	 * show lens frames?
	 */
	private boolean showLensFrames;

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

	//
	// cameras
	//



	/**
	 * Constructor.
	 * Sets all parameters.
	 */
	public SkewLensRochesterCloakVisualiser_02()
	{
		super();

		// set to true for interactive version

		nonInteractiveTIMAction = NonInteractiveTIMActionEnum.INTERACTIVE;

		// set all parameters

		renderQuality = RenderQualityEnum.DRAFT;

		// the skew-lens-Rochester cloak
		
		z1 = 0;
		z2 = 1;
		z3 = 3;
		z4 = 4;
		y0 = 5;
		f1 = 0.6;
		lensType = LensType.IDEAL_THIN_LENS;
		showLens1 = true;
		showLens2 = true;
		showLens3 = true;
		showLens4 = true;
		showLensFrames = true;
		
		// (outside) camera parameters; the camera is set by getStandardCamera()
		// @see optics.raytrace.NonInteractiveTIMEngine.getStandardCamera()

		cameraViewCentre = new Vector3D(0, 0, 0);
		cameraViewDirection = new Vector3D(0, 0, 1);
		cameraDistance = 10;
		cameraHorizontalFOVDeg = 20;
		//               * @see optics.raytrace.NonInteractiveTIMEngine.cameraMaxTraceLevel
		//               * @see optics.raytrace.NonInteractiveTIMEngine.cameraPixelsX
		//               * @see optics.raytrace.NonInteractiveTIMEngine.cameraPixelsY
		cameraApertureSize = ApertureSizeType.PINHOLE;
		cameraFocussingDistance = cameraDistance;


		// blur quality if test = false; make this better in the non-interactive version, as this is used to produce publication-quality images
		// standardCameraBlurQuality = (interactive?QualityType.GOOD:QualityType.SUPER);        // this is the camera's blur quality if test=false


		// other scene objects

		studioInitialisation = StudioInitialisationType.LATTICE;        // the backdrop
		showPatternedSphere = false;
		patternedSphereCentre = new Vector3D(0, -0.75, 0);
		patternedSphereRadius = 0.05;

		if(nonInteractiveTIMAction == NonInteractiveTIMActionEnum.INTERACTIVE)
		{
			windowTitle = "Dr TIM's skew-lens-Rochester-cloak visualiser";
			windowWidth = 1500;
			windowHeight = 650;
		}
	}


	@Override
	public String getClassName()
	{
		return "SkewLensRochesterCloakVisualiser";      // the name
	}

	@Override
	public void writeParameters(PrintStream printStream)
	{
		// write any parameters not defined in NonInteractiveTIMEngine
		// printStream.println("parameterName = "+parameterName);

		// the skew-lens-Rochester cloak
		printStream.println("z1 = "+z1);
		printStream.println("z2 = "+z2);
		printStream.println("z3 = "+z3);
		printStream.println("z4 = "+z4);
		printStream.println("y0 = "+y0);
		printStream.println("f1 = "+f1);
		printStream.println("lensType = "+lensType);
		printStream.println("showLens1 = "+showLens1);
		printStream.println("showLens2 = "+showLens2);
		printStream.println("showLens3 = "+showLens3);
		printStream.println("showLens4 = "+showLens4);
		printStream.println("showLensFrames = "+showLensFrames);

		// other scene objects

		printStream.println("studioInitialisation = "+studioInitialisation);    // the backdrop
		printStream.println("showPatternedSphere = "+showPatternedSphere);
		printStream.println("patternedSphereCentre = "+patternedSphereCentre);
		printStream.println("patternedSphereRadius = "+patternedSphereRadius);

		// write all parameters defined in NonInteractiveTIMEngine
		super.writeParameters(printStream);
	}

	/**
	 * @param description
	 * @param pz, z component of the principal point
	 * @param lensWidth
	 * @param inclinationAngle
	 * @param focalLength
	 * @param frameRadius
	 * @param frameSurfaceProperty
	 * @param scene
	 * @return
	 */
	private SceneObject createLens(
			String description,
			Vector3D principalPoint,
			double lensWidth,
			double inclinationAngle,
			double focalLength,
			double frameRadius,
			SurfaceProperty frameSurfaceProperty,
			SceneObjectContainer scene
			)
	{
		SceneObjectUnion lens = new SceneObjectUnion(description, scene, studio);
		
		Vector3D widthVector = new Vector3D(lensWidth, 0, 0);
		Vector3D heightVector = new Vector3D(0, lensWidth*Math.cos(inclinationAngle), lensWidth*Math.sin(inclinationAngle));
		
		lens.addSceneObject(getRectangularLens(
				principalPoint,	// centre
				widthVector,	// spanVector1
				heightVector,	// spanVector2
				principalPoint,
				focalLength,	// f
				lensType,
				0.96,	// transmissionCoefficient
				true,	// shadowThrowing
				lens,	// parent
				studio			
			));

//		IdealThinLensSurfaceSimple lensSurface;
//		lensSurface = new IdealThinLensSurfaceSimple(
//				principalPoint, // lensCentre
//				new Vector3D(0, -Math.sin(inclinationAngle), Math.cos(inclinationAngle)), // opticalAxisDirection
//				focalLength, // focalLength
//				0.96, // transmissionCoefficient
//				true // shadowThrowing
//				);

//		EditableFramedRectangle lens;
		lens.addSceneObject(new EditableFramedRectangle(
				description, // description
				Vector3D.sum(
						principalPoint, 
						new Vector3D(-0.5*lensWidth, -0.5*lensWidth*Math.cos(inclinationAngle), -0.5*lensWidth*Math.sin(inclinationAngle))
						), // corner
				new Vector3D(lensWidth, 0, 0), // widthVector
				new Vector3D(0, lensWidth*Math.cos(inclinationAngle), lensWidth*Math.sin(inclinationAngle)), // heightVector
				frameRadius, // frameRadius
				null,	// lensSurface, // windowSurfaceProperty
				frameSurfaceProperty, // frameSurfaceProperty
				showLensFrames, // showFrames
				lens, // parent
				studio
				),
				showLensFrames);

		return lens;
	}
	
	
	/**
	 * @see optics.raytrace.simplicialComplex.IdealThinLensSimplicialComplex#getSceneObjectRepresentingFace(int, double, boolean, optics.raytrace.core.SceneObject, optics.raytrace.core.Studio)
	 * @param transmissionCoefficient
	 * @param shadowThrowing
	 * @param parent
	 * @param studio
	 * @return
	 */
	public SceneObject getRectangularLens(
			Vector3D centre,
			Vector3D spanVector1,
			Vector3D spanVector2,
			Vector3D principalPoint,
			double f,
			LensType lensType,
			double transmissionCoefficient,
			boolean shadowThrowing,
			SceneObject parent,
			Studio studio			
		)
	{
		// modified from @see optics.raytrace.simplicialComplex.IdealThinLensSimplicialComplex#getSceneObjectRepresentingFace(int, double, boolean, optics.raytrace.core.SceneObject, optics.raytrace.core.Studio)

		Vector3D opticalAxisDirection = Vector3D.crossProduct(spanVector1, spanVector2).getNormalised();
		Vector3D frontConjugatePoint = Vector3D.sum(principalPoint, opticalAxisDirection.getProductWith(f));
		Vector3D backConjugatePoint = Vector3D.sum(principalPoint, opticalAxisDirection.getProductWith(-f));
		
		ParametrisedParallelogram parallelogram = new ParametrisedParallelogram(
				"Parallelogram",	// description
				Vector3D.sum(centre, spanVector1.getProductWith(-0.5), spanVector2.getProductWith(-0.5)),	// corner
				spanVector1,
				spanVector2, 
				null,	// surfaceProperty
				parent,
				studio
			);
				
		switch(lensType)
		{
		case NONE:
			return null;
		case SEMITRANSPARENT_PLANE:
			parallelogram.setSurfaceProperty(new SemiTransparent(SurfaceColour.BLUE_SHINY, 0.9));
			return parallelogram;
		case POINT_2_POINT_IMAGING_HOLOGRAM:
			parallelogram.setSurfaceProperty(new Point2PointImagingPhaseHologram(
								frontConjugatePoint,
								backConjugatePoint,
								transmissionCoefficient,	// throughputCoefficient
								false,	// reflective
								shadowThrowing
								));
			return parallelogram;
		case FRESNEL_LENS:
			return new FresnelLensShaped(
					"Fresnel lens", // description,
					centre,	// lensCentre,
					opticalAxisDirection,	// forwardsCentralPlaneNormal,
					frontConjugatePoint,
					backConjugatePoint,
					1.3, // refractiveIndex,
					0.1,	// thickness,
					0.01,	// minimumSurfaceSeparation,
					parallelogram,	// apertureShape,
					true,	// makeStepSurfacesBlack,
					0.96,	// transmissionCoefficient,
					parent,	// parent,
					studio
					);
		case IDEAL_THIN_LENS:
		default:
			parallelogram.setSurfaceProperty(new IdealThinLensSurfaceSimple(
				principalPoint, // lensCentre
				opticalAxisDirection, // opticalAxisDirection
				f, // focalLength
				0.96, // transmissionCoefficient
				true // shadowThrowing
				));
			return parallelogram;
		}
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

		// References:
		// [1] Courtial-et-al-2018a (OE 26, 17872 (2018))

		// four lenses, L1 to L4, arranged like the ones in Fig. 6 in [1], but with the coordinate system changed
		// translation table:
		// L1 (here) = L11 ([1])
		// L2 (here) = L10 ([1])
		// L3 (here) = L9 ([1])
		// L4 (here) = L8 ([1])


		// deltaZ values (which are called deltaY in [1])
		double deltaZ12 = z1-z2;
		double deltaZ21 = z2-z1;
		double deltaZ13 = z1-z3;
		double deltaZ31 = z3-z1;
		double deltaZ43 = z4-z3;
		double deltaZ23 = z2-z3;
		double deltaZ32 = z3-z2;
		double deltaZ41 = z4-z1;
		double deltaZ42 = z4-z2;

		//distances (hypotenuse) 
		double x02 = y0*y0;
		double s1 = Math.sqrt(x02+(z1-z1)*(z1-z1)); 
		double s2 = Math.sqrt(x02+(z2-z1)*(z2-z1));
		double s3 = Math.sqrt(x02+(z3-z1)*(z3-z1));
		double s4 = Math.sqrt(x02+(z4-z1)*(z4-z1));

		// finally, the focal lengths (Eqns (25) from [1])

		double f4 =  f1*((s1*deltaZ43*deltaZ42)/(s4*deltaZ31*deltaZ12))+((y0*deltaZ43*deltaZ41)/(s4*deltaZ31)); // f8 in [1]
		double f3 = -f1*((s1*deltaZ43*deltaZ32)/(s3*deltaZ41*deltaZ12)); // f9 in [1]
		double f2 =  f1*((s1*deltaZ42*deltaZ32)/(s2*deltaZ41*deltaZ13))-((y0*deltaZ23*deltaZ21)/(s2*deltaZ31)); //f10 in [1]
		
		f2Panel.setNumber(f2);
		f3Panel.setNumber(f3);
		f4Panel.setNumber(f4);

		// double innerLensSeparation = 2*f2*(f1+f2)/(f1-f2); // from Rochester-cloak paper

		double lensWidth = 2;

		scene.addSceneObject(createLens(
				"Lens 1", // description
				new Vector3D(0, 0, z1), // principalPointZ
				lensWidth,
				Math.atan2(z1-z1, -y0), // inclinationAngle
				f1, // focalLength
				0.01*lensWidth, // frameRadius
				SurfaceColour.RED_SHINY, // frameSurfaceProperty
				scene
			), showLens1
		);

		scene.addSceneObject(createLens(
				"Lens 2", // description
				new Vector3D(0,0,z2), // principalPointZ
				lensWidth,
				Math.atan2(z2-z1, -y0), // inclinationAngle
				f2, // focalLength
				0.0101*lensWidth,// frameRadius
				SurfaceColour.BLUE_SHINY, // frameSurfaceProperty
				scene
			), showLens2
		);

		scene.addSceneObject(createLens(
				"Lens 3", // description
				new Vector3D(0,0,z3), // principalPointZ
				lensWidth,
				Math.atan2(z3-z1, -y0), // inclinationAngle
				f3, // focalLength
				0.0102*lensWidth,// frameRadius
				SurfaceColour.GREEN_SHINY, // frameSurfaceProperty
				scene
			), showLens3
		);

		scene.addSceneObject(createLens(
				"Lens 4", // description
				new Vector3D(0,0,z4), // principalPointZ
				lensWidth,
				Math.atan2(z4-z1, -y0), // inclinationAngle
				f4, // focalLength
				0.0103*lensWidth,// frameRadius
				SurfaceColour.YELLOW_SHINY, // frameSurfaceProperty
				scene
			), showLens4
		);


		EditableScaledParametrisedSphere patternedSphere = new EditableScaledParametrisedSphere(
				"Patterned sphere", // description
				patternedSphereCentre, // centre
				patternedSphereRadius,
				// radius
				new Vector3D(1, 1, 1),
				// pole
				new Vector3D(1, 0, 0),
				// phi0Direction
				0, Math.PI,
				// sThetaMin, sThetaMax
				-Math.PI, Math.PI,
				// sPhiMin, sPhiMax
				new SurfaceTiling(
						new SurfaceColour(DoubleColour.BLACK, DoubleColour.WHITE, false),
						// like SurfaceColour.BLACK_SHINY, but doesn't throw a shadow
						new SurfaceColour(DoubleColour.GREY90, DoubleColour.WHITE, false),
						// like SurfaceColour.GREY90_SHINY, but not shadow-throwing
						2*Math.PI/6,
						2*Math.PI/6
						),
				scene, studio);
		scene.addSceneObject(patternedSphere, showPatternedSphere);

	}

	//
	// for interactive version
	//

	// skew-lens-Rochester cloak

	private LabelledDoublePanel z1Panel;
	private LabelledDoublePanel z2Panel;
	private LabelledDoublePanel z3Panel;
	private LabelledDoublePanel z4Panel;
	private LabelledDoublePanel y0Panel;
	private LabelledDoublePanel f1Panel;
	private LabelledDoublePanel f2Panel;
	private LabelledDoublePanel f3Panel;
	private LabelledDoublePanel f4Panel;
	private JComboBox<LensType> lensTypeComboBox;
	private JCheckBox showLens1Checkbox;
	private JCheckBox showLens2Checkbox;
	private JCheckBox showLens3Checkbox;
	private JCheckBox showLens4Checkbox;
	private JCheckBox showLensFramesCheckbox;

	// other scene objects
	private JComboBox<StudioInitialisationType> studioInitialisationComboBox;
	private JCheckBox showPatternedSphereCheckBox;
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

		z1Panel = new LabelledDoublePanel("z coordinate of principal point of lens 1");
		z1Panel.setNumber(z1);
		f1Panel = new LabelledDoublePanel(", focal length");
		f1Panel.setNumber(f1);
		lensCloakPanel.add(GUIBitsAndBobs.makeRow(z1Panel, f1Panel), "span");

		z2Panel = new LabelledDoublePanel("z coordinate of principal point of lens 2");
		z2Panel.setNumber(z2);
		f2Panel = new LabelledDoublePanel(", focal length");
		f2Panel.setEnabled(false);
		f2Panel.setText("not initialised");
		lensCloakPanel.add(GUIBitsAndBobs.makeRow(z2Panel, f2Panel), "span");

		z3Panel = new LabelledDoublePanel("z coordinate of principal point of lens 3");
		z3Panel.setNumber(z3);
		f3Panel = new LabelledDoublePanel(", focal length");
		f3Panel.setEnabled(false);
		f3Panel.setText("not initialised");
		lensCloakPanel.add(GUIBitsAndBobs.makeRow(z3Panel, f3Panel), "span");

		z4Panel = new LabelledDoublePanel("z coordinate of principal point of lens 4");
		z4Panel.setNumber(z4);
		f4Panel = new LabelledDoublePanel(", focal length");
		f4Panel.setEnabled(false);
		f4Panel.setText("not initialised");
		lensCloakPanel.add(GUIBitsAndBobs.makeRow(z4Panel, f4Panel), "span");

		y0Panel = new LabelledDoublePanel("y coordinate of intersection line of lenses");
		y0Panel.setNumber(y0);
		lensCloakPanel.add(y0Panel, "span");

		lensTypeComboBox = new JComboBox<LensType>(LensType.values());
		lensTypeComboBox.setSelectedItem(lensType);
		lensCloakPanel.add(GUIBitsAndBobs.makeRow("Lens type", lensTypeComboBox), "span");

		showLens1Checkbox = new JCheckBox("Show lens 1");
		showLens1Checkbox.setSelected(showLens1);
		lensCloakPanel.add(showLens1Checkbox, "span");
		
		showLens2Checkbox = new JCheckBox("Show lens 2");
		showLens2Checkbox.setSelected(showLens2);
		lensCloakPanel.add(showLens2Checkbox, "span");
		
		showLens3Checkbox = new JCheckBox("Show lens 3");
		showLens3Checkbox.setSelected(showLens3);
		lensCloakPanel.add(showLens3Checkbox, "span");
		
		showLens4Checkbox = new JCheckBox("Show lens 4");
		showLens4Checkbox.setSelected(showLens4);
		lensCloakPanel.add(showLens4Checkbox, "span");
		
		showLensFramesCheckbox = new JCheckBox("Show lens frames");
		showLensFramesCheckbox.setSelected(showLensFrames);
		lensCloakPanel.add(showLensFramesCheckbox, "span");


		//
		// Other scene-objects panel
		//

		JPanel otherObjectsPanel = new JPanel();
		otherObjectsPanel.setLayout(new MigLayout("insets 0"));
		tabbedPane.addTab("Other scene objects", otherObjectsPanel);

		studioInitialisationComboBox = new JComboBox<StudioInitialisationType>(StudioInitialisationType.limitedValuesForBackgrounds);
		studioInitialisationComboBox.setSelectedItem(studioInitialisation);
		otherObjectsPanel.add(GUIBitsAndBobs.makeRow("Initialise backdrop to", studioInitialisationComboBox), "span");

		showPatternedSphereCheckBox = new JCheckBox("Show Patterned Sphere");
		showPatternedSphereCheckBox.setSelected(showPatternedSphere);
		otherObjectsPanel.add(showPatternedSphereCheckBox, "span");


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
		z1 = z1Panel.getNumber();
		z2 = z2Panel.getNumber();
		z3 = z3Panel.getNumber();
		z4 = z4Panel.getNumber();
		y0 = y0Panel.getNumber();
		f1 = f1Panel.getNumber();
		lensType = (LensType)(lensTypeComboBox.getSelectedItem());
		showLens1 = showLens1Checkbox.isSelected();
		showLens2 = showLens2Checkbox.isSelected();
		showLens3 = showLens3Checkbox.isSelected();
		showLens4 = showLens4Checkbox.isSelected();
		showLensFrames = showLensFramesCheckbox.isSelected();

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
		(new SkewLensRochesterCloakVisualiser_02()).run();
	}

}