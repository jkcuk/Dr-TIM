package optics.raytrace.GUI.surfaces;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;
import math.Complex;
import math.MyMath;
import math.Vector2D;
import math.Vector3D;
import optics.DoubleColour;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.SurfaceProperty;
import optics.raytrace.core.SurfacePropertyPrimitive;
import optics.raytrace.core.SurfacePropertyWithControllableShadow;
import optics.raytrace.surfaces.ColourFilter;
import optics.raytrace.surfaces.ConfocalLensletArrays;
import optics.raytrace.surfaces.GalileoTransformInterface;
import optics.raytrace.surfaces.GCLAsWithApertures;
import optics.raytrace.surfaces.GCLAsWithApertures.GCLAsTransmissionCoefficientCalculationMethodType;
import optics.raytrace.surfaces.GlensSurface;
import optics.raytrace.surfaces.IdealThinLensSurface;
import optics.raytrace.surfaces.IdealisedDovePrismArray;
import optics.raytrace.surfaces.LightRayFieldRepresentingPhaseFront;
import optics.raytrace.surfaces.EatonLensSurfaceAngleFormulation;
import optics.raytrace.surfaces.LorentzTransformInterface;
import optics.raytrace.surfaces.LuneburgLensSurfaceAngleFormulation;
import optics.raytrace.surfaces.MetricInterface;
import optics.raytrace.surfaces.MetricInterface.RefractionType;
import optics.raytrace.surfaces.PhaseConjugating;
import optics.raytrace.surfaces.PhaseHologramOfCrossedLinearPowerLenticularArrays;
import optics.raytrace.surfaces.PhaseHologramOfCylindricalLens;
import optics.raytrace.surfaces.PhaseHologramOfLinearPowerLenticularArray;
import optics.raytrace.surfaces.PhaseHologramOfRadialLenticularArray;
import optics.raytrace.surfaces.Pixellation;
import optics.raytrace.surfaces.Point2PointImagingPhaseHologram;
import optics.raytrace.surfaces.Rainbow;
import optics.raytrace.surfaces.RayFlipping;
import optics.raytrace.surfaces.RayRotating;
import optics.raytrace.surfaces.RayRotatingAboutArbitraryAxisDirection;
import optics.raytrace.surfaces.RayRotatingInPlaneOfIncidence;
import optics.raytrace.surfaces.RectangularIdealThinLensletArray;
import optics.raytrace.surfaces.Reflective;
import optics.raytrace.surfaces.Refractive;
import optics.raytrace.surfaces.RefractiveComplex;
import optics.raytrace.surfaces.RotationallySymmetricPhaseHologram;
import optics.raytrace.surfaces.SurfaceColour;
import optics.raytrace.surfaces.SurfaceColourLightSourceIndependent;
import optics.raytrace.surfaces.SurfaceColourTimeDependent;
import optics.raytrace.surfaces.Teleporting;
import optics.raytrace.surfaces.Teleporting.TeleportationType;
import optics.raytrace.surfaces.Transparent;
import optics.raytrace.surfaces.VariableEtaConfocalLensletArrays;
import optics.raytrace.utility.CoordinateSystems.CoordinateSystemType;
import optics.raytrace.utility.CoordinateSystems.GlobalOrLocalCoordinateSystemType;
import optics.raytrace.GUI.core.*;
import optics.raytrace.GUI.lowLevel.DoublePanel;
import optics.raytrace.GUI.lowLevel.GUIBitsAndBobs;
import optics.raytrace.GUI.lowLevel.IntPanel;
import optics.raytrace.GUI.lowLevel.LabelledComplexPanel;
import optics.raytrace.GUI.lowLevel.LabelledDoubleColourPanel;
import optics.raytrace.GUI.lowLevel.LabelledDoublePanel;
import optics.raytrace.GUI.lowLevel.LabelledScientificDoublePanel;
import optics.raytrace.GUI.lowLevel.LabelledSymmetricMatrix3DPanel;
import optics.raytrace.GUI.lowLevel.LabelledVector2DPanel;
import optics.raytrace.GUI.lowLevel.LabelledVector3DPanel;
import optics.raytrace.GUI.lowLevel.Vector3DPanel;

/**
 * Allows choice of a surface property.
 */
public class SurfacePropertyPanel extends JPanel implements ActionListener
{
	private static final long serialVersionUID = 5404568055735746609L;

	public static final SurfaceProperty
		TILED = new EditableSurfaceTiling(SurfaceColour.GREY50_SHINY, SurfaceColour.WHITE_SHINY, 1, 1, null);
	
	// the options that come up in the combo box for selecting the class of surface property
	public enum SurfacePropertyType
	{
		COLOURED("Coloured"),
		COLOUR_FILTER("Colour filter"),
		COLOURED_GLOWING("Coloured (glowing)"),
		COLOURED_TIME_DEPENDENT("Coloured (time-dependent)"),
		CLAS("Confocal lenslet arrays"),
		CROSSED_LENTICULAR_ARRAYS_WITH_LINEAR_POWER("Crossed lenticular arrays with linearly-varying focussing power"),
		CYLINDRICAL_LENS_HOLOGRAM("Cylindrical-lens hologram"),
		EATON_LENS("Eaton-lens surface"),
		FLIPPING("Flipping"),
		GALILEO_TRANSFORM_INTERFACE("Galileo-transform interface"),
		GENERALISED_CLAS("Generalised confocal lenslet arrays"),
		GLENS_HOLOGRAM("Glens hologram"),
		IDEAL_THIN_LENS_SURFACE("Ideal-thin-lens hologram"),
		IDEALISED_DOVE_PRISM_ARRAY("Idealised Dove-prism array"),
		LENSLET_ARRAY("Lenslet-array"),
		LENTICULAR_ARRAY_WITH_LINEAR_POWER("Lenticular array with linearly-varying focussing power"),
		LORENTZ_TRANSFORM_INTERFACE("Lorentz-transform interface"),
		LUNEBURG_LENS("Luneburg-lens surface"),
		METRIC_INTERFACE("Metric interface"),
		PHASE_CONJUGATING("Phase-conjugating"),
		PHASE_FRONT("Phase front"),
		PICTURE("Picture"),
		PIXELLATION("Pixellation"),
		POINT2POINT_IMAGING("Point-to-point imaging hologram"),
		RADIAL_LENTICULAR_ARRAY("Radial lenticular array"),
		RAINBOW("Rainbow"),
		REFLECTIVE("Reflective"),
		REFRACTIVE("Refractive"),
		REFRACTIVE_COMPLEX("Refractive (complex)"),
		ROTATIONALLY_SYMMETRIC_PHASE_HOLOGRAM("Rotationally symmetric phase hologram"),
		ROTATING("Rotating"),
		ROTATING_AROUND_ARBITRARY_AXIS_DIRECTION("Rotating around arbitrary axis direction"),
		ROTATING_IN_PLANE_OF_INCIDENCE("Rotating in plane of incidence"),
		TELEPORTING("Teleporting"),
		TILED("Tiled"),
		TRANSPARENT("Transparent"),
		TWO_SIDED("Two-sided"),
		VARIABLE_ETA_CLAS("Variable-eta confocal lenslet arrays (CLAs)");
		
		private String description;
		private SurfacePropertyType(String description) {this.description = description;}	
		@Override
		public String toString() {return description;}
	}

	private SurfacePropertyComboBox surfacePropertyComboBox;
	
	private JPanel optionalParametersPanel;

	// confocal lenslet arrays panel
	private JPanel CLAsPanel;
	private LabelledDoublePanel
		CLAsEtaPanel,
		CLAsTransmissionCoefficientPanel;
//	private JCheckBox
//		CLAsCalculateGeometricalTransmissionCoefficientCheckBox;

	// generalised confocal lenslet arrays (GCLAs) panel
	private JPanel gCLAsPanel;
	private LabelledVector3DPanel
		aVector3DPanel,
		uVector3DPanel,
		vVector3DPanel;
	private JComboBox<GlobalOrLocalCoordinateSystemType>
		gCLAsBasisComboBox;
	private LabelledVector2DPanel
		etaPanel,
		deltaPanel;
	private JComboBox<GCLAsTransmissionCoefficientCalculationMethodType> gCLAsTransmissionCoefficientMethodComboBox;
	private LabelledDoublePanel
		gCLAsConstantTransmissionCoefficientPanel;
	private LabelledScientificDoublePanel
		gCLAsPixelSideLengthPanel,
		gCLAsLambdaPanel;
	private JCheckBox
		gCLAsSimulateDiffractiveBlurCheckBox,
		gCLAsSimulateRayOffsetCheckBox;
//		gCLAsCalculateGeometricalTransmissionCoefficientCheckBox;
	
	// glens-hologram panel
	private JPanel glensHologramPanel;
	private LabelledVector3DPanel
		opticalAxisPosVector3DPanel,
		nodalPointFVector3DPanel,
		pointOnGlensVector3DPanel;
	private LabelledDoublePanel
		meanFPanel,
		focalLengthNegFPanel,
		focalLengthPosFPanel,
		glensTransmissionCoefficientPanel;

	// lens-hologram panel
	private JPanel lensHologramPanel;
	private LabelledVector3DPanel
		opticalAxisVector3DPanel,
		principalPointVector3DPanel;
	private LabelledDoublePanel
		focalLengthPanel,
		lensTransmissionCoefficientPanel;

	// cylindrical-lens-hologram panel
	private JPanel cylindricalLensHologramPanel;
	private LabelledVector3DPanel
		pointOnAxisVector3DPanel,
		phaseGradientDirectionVector3DPanel;
	private LabelledDoublePanel
		cylindricalLensHologramFocalLengthPanel,
		cylindricalLensHologramTransmissionCoefficientPanel;
	
//	// lenslet-array panel
	private JPanel lensletArrayPanel;
	private LabelledDoublePanel lensletArrayFocalLengthPanel, lensletArrayTransmissionCoefficientPanel, lensletArrayLambdaPanel;
	private LabelledVector2DPanel lensletArrayPeriodPanel, lensletArrayOffsetPanel;
	private JCheckBox lensletArraySimulateDiffractiveBlurCheckBox;

	
	// LENTICULAR_ARRAY_WITH_LINEAR_POWER panel
	private JPanel lenticularArrayWithLinearPowerPanel;
	private LabelledVector3DPanel lawlpP0Panel, lawlpUHatPanel;
	private LabelledDoublePanel lawlpPeriodPanel, lawlpDPduPanel;

	// CROSSED_LENTICULAR_ARRAYS_WITH_LINEAR_POWER panel
	private JPanel crossedLenticularArraysWithLinearPowerPanel;
	private LabelledVector3DPanel clawlpP0Panel, clawlpUHatPanel, clawlpVHatPanel;
	private LabelledDoublePanel clawlpPeriodPanel, clawlpDPduPanel;

	// metric interface panel
	private JPanel metricInterfacePanel;
	private LabelledSymmetricMatrix3DPanel
		insideMetricTensorPanel,
		outsideMetricTensorPanel;
	private JComboBox<GlobalOrLocalCoordinateSystemType>
		metricTensorsBasisComboBox;
	private LabelledDoublePanel metricInterfaceTransmissionCoefficientPanel;
	private JComboBox<RefractionType> refractionTypeComboBox;
	private JCheckBox allowImaginaryOpticalPathLengthsCheckBox;
	
	// rainbow panel
	private JPanel rainbowPanel;
	private LabelledDoublePanel rainbowSaturationPanel, rainbowLightnessPanel;
	private Vector3DPanel rainbowLightSourcePositionPanel;
	
	// pixellation panel
	private JPanel pixellationPanel;
	private LabelledDoublePanel pixellationPixelSideLengthUPanel, pixellationPixelSideLengthVPanel, pixellationLambdaPanel;
	private JCheckBox pixellationSimulateDiffractiveBlurCheckBox, pixellationSimulateRayOffsetCheckBox;
	
	// ray rotation around arbitrary axis direction panel
	private JPanel rayRotationAroundArbitraryAxisDirectionPanel;
	private LabelledDoublePanel rayRotationAngleArbitraryAxisDirectionPanel;
	private LabelledVector3DPanel rayRotationAxisPanel;
	private JComboBox<GlobalOrLocalCoordinateSystemType> rayRotationAxisDirectionBasisComboBox;
	
	// rotationally symmetric phase hologram panel
	private JPanel rotationallySymmetricPhaseHologramPanel;
	private LabelledVector3DPanel rsphCentrePanel;
	private DoublePanel rsphBPanel, rsphCPanel, rsphSPanel, rsphTPanel;
	private JCheckBox rsphSimulateHonestlyCheckBox;
	private IntPanel rsphNPanel;
	private Vector3DPanel rsphD0Panel;

	// idealised Dove-prism array
	private JPanel idealisedDovePrismArrayPanel;
	private LabelledVector3DPanel idpaDovePrism0CentrePanel;
	private LabelledVector3DPanel idpaInversionDirectionPanel;
	private LabelledDoublePanel idpaPeriodPanel;
	private LabelledDoublePanel idpaTransmissionCoefficientPanel;

	// radial lenticular array
	private JPanel phaseHologramOfRadialLenticularArrayPanel;
	private LabelledVector3DPanel rlapCentrePanel, rlapD0Panel;
	private LabelledDoublePanel rlapFPanel, rlapNPanel;
	
	// SurfaceColourTimeDependent panel
	private LabelledDoublePanel surfaceColourTimeDependentPanel;

	// other panels
	private LabelledDoubleColourPanel colourPanel, phaseFrontColourPanel;
	private LabelledDoublePanel
		transmissionCoefficientPanel,
		transmissionCoefficientGalileoPanel,
		phaseConjugatingSurfaceTransmissionCoefficientPanel,
		refractiveIndexRatioPanel,
		rayRotationAnglePanel,
		flipAxisAnglePanel,
		angularFuzzinessDegPanel;
		// criticalAngleOfIncidencePanel;
	private JCheckBox bidirectionalRaysCheckBox;
	private LabelledComplexPanel complexRefractiveIndexRatioPanel;
	// private TilingParametersLine tilingParametersPanel;
	private JButton tilingParametersButton, twoSidedParametersButton, choosePictureFileButton;
	public static final String
		TILING_PARAMS_BUTTON_TEXT = "Edit tiling parameters",
		TWO_SIDED_PARAMS_BUTTON_TEXT = "Edit parameters of two-sided surface";
	private EditableSurfaceTiling surfaceTiling;
	private EditableTwoSidedSurface twoSidedSurface;
	private JComboBox<TeleportationType> teleportationTypeComboBox;
	private LabelledTeleportingTargetsComboBox teleportingParametersLine;
	// the options for coordinate bases
	private JComboBox<CoordinateSystemType> betaBasisComboBox, betaGalileoBasisComboBox;
	private JPanel
		disabledParametersPanel,
		galileoTransformInterfacePanel,
		lorentzTransformInterfacePanel,
		phaseConjugatingPanel,
		phaseFrontPanel,
		point2pointImagingPanel,
		picturePanel,
		teleportingParametersPanel;
		// EatonLuneburgLensSurfacePanel;
	private JLabel point2pointImagingPoint1Label, point2pointImagingPoint2Label;
	private Vector3DPanel
		point2pointImagingPoint1Panel,
		point2pointImagingPoint2Panel;
	private LabelledVector3DPanel
		betaVector3DPanel, betaGalileoVector3DPanel;	// for Lorentz-transform interface
	private JCheckBox
		point2pointImagingIsReflectiveCheckBox,
		isPhaseConjugatingSurfaceReflectiveCheckBox;
	private LabelledVector2DPanel
		pictureCorner,
		pictureSize;
	private EditablePictureSurfaceDiffuse pictureSurface;
	private File pictureFile;
	private boolean pictureFileChanged;
	private JTextField pictureFileNameField;
	private JCheckBox pictureSurfaceTiledCheckbox;
	
	// variable-eta-CLAs panel
	private JPanel variableEtaCLAsPanel;
	private LabelledDoublePanel
		variableEtaCLAsEtaAtPPanel,
		variableEtaCLAsTransmissionCoefficientPanel;
	private LabelledVector3DPanel
		variableEtaPointPPanel,
		variableEtaGradEtaPanel;
//	private JCheckBox
//		CLAsCalculateGeometricalTransmissionCoefficientCheckBox;

	
	private JCheckBox shadowThrowingCheckBox;
	private IPanel iPanel;
	
	private SceneObject scene;
	
	// private One2OneParametrisedObject sceneObject;

	/**
	 * @param sceneObject	the scene object the surface property is associated with
	 * @param scene	the scene object(s) to which Teleporting can teleport
	 */
	public SurfacePropertyPanel(String description, boolean showFrame, SceneObject scene)
	{
		super();
		setScene(scene);
		
		// this.iPanel = iPanel;
		
		setLayout(new MigLayout("insets 0"));
		if(showFrame) setBorder(GUIBitsAndBobs.getTitledBorder(description));

		surfacePropertyComboBox = new SurfacePropertyComboBox();
		add(GUIBitsAndBobs.makeRow("Surface type", surfacePropertyComboBox), "wrap");

		shadowThrowingCheckBox = new JCheckBox("Shadow-throwing");
		shadowThrowingCheckBox.setAlignmentX(Component.CENTER_ALIGNMENT);
		add(shadowThrowingCheckBox, "wrap");

		optionalParametersPanel = new JPanel();
		add(optionalParametersPanel);

		//
		// initialise the optional-parameter panels
		//

		colourPanel = new LabelledDoubleColourPanel("Colour");
		colourPanel.setDoubleColour(DoubleColour.WHITE);
		
		surfaceColourTimeDependentPanel = new LabelledDoublePanel("Time-change period");
		surfaceColourTimeDependentPanel.setNumber(1);
		
		transmissionCoefficientPanel = new LabelledDoublePanel("Transmission coefficient");
		transmissionCoefficientPanel.setNumber(SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT);
		
		refractiveIndexRatioPanel = new LabelledDoublePanel("Refractive-index ratio (n_inside / n_outside)");
		refractiveIndexRatioPanel.setNumber(1.0);
		
		complexRefractiveIndexRatioPanel = new LabelledComplexPanel("Refractive-index ratio (n_inside / n_outside)");
		complexRefractiveIndexRatioPanel.setNumber(new Complex(1, 0));
		
		rayRotationAnglePanel = new LabelledDoublePanel("Rotation angle (degrees)");
		rayRotationAnglePanel.setNumber(0.0);
		
		rayRotationAroundArbitraryAxisDirectionPanel = new JPanel();
		rayRotationAroundArbitraryAxisDirectionPanel.setLayout(new MigLayout("insets 0"));
		rayRotationAngleArbitraryAxisDirectionPanel = new LabelledDoublePanel("Rotation angle");
		rayRotationAngleArbitraryAxisDirectionPanel.setNumber(0.0);
		rayRotationAroundArbitraryAxisDirectionPanel.add(rayRotationAngleArbitraryAxisDirectionPanel, "wrap");
		rayRotationAxisPanel = new LabelledVector3DPanel("Normalised rotation-axis direction");
		rayRotationAxisPanel.setVector3D(new Vector3D(0, 0, 1));
		rayRotationAroundArbitraryAxisDirectionPanel.add(rayRotationAxisPanel, "wrap");
		rayRotationAxisDirectionBasisComboBox = new JComboBox<GlobalOrLocalCoordinateSystemType>(GlobalOrLocalCoordinateSystemType.values());
		rayRotationAxisDirectionBasisComboBox.setSelectedItem(GlobalOrLocalCoordinateSystemType.LOCAL_OBJECT_BASIS);
		rayRotationAroundArbitraryAxisDirectionPanel.add(rayRotationAxisDirectionBasisComboBox, "wrap");
		rayRotationAroundArbitraryAxisDirectionPanel.validate();
		
		// rotationally symmetric phase hologram
		
		rotationallySymmetricPhaseHologramPanel = new JPanel();
		rotationallySymmetricPhaseHologramPanel.setLayout(new MigLayout("insets 0"));
//		rotationallySymmetricPhaseHologramPanel.add(new JLabel(
//				"<html>The phase gradient is specified separately in the azimuthal and radial direction, whereby the former comes in two parts."+
//				"<ul>"+
//				"<li>The first part of the azimuthal phase gradient is of the form d<i>&Phi;</i>/d<i>a</i> = <i>k</i>*<i>b</i>*<i>r</i>^<i>c</i>,"+
//				"where <i>k</i>=(2 &pi;/&lambda;) and <i>a</i> = <i>r</i> &phi; is the coordinate in the azimuthal direction.</li>"+
//				"<li>The second part of the azimuthal phase gradient is of the form d<i>&Phi;</i>/d<i>&phi;</i> = <i>k</i>*<i>g</i>*<i>r</i>^<i>h</i>,"+
//				"where <i>&phi;</i> is the azimuthal angle.</li>"+
//				"<li>The radial phase gradient is of the form d<i>&Phi;</i>/dr = <i>k</i>*<i>s</i>*<i>r</i>^<i>t</i>, where <i>k</i>=(2 &pi;/&lambda;) again.</li>"+
//				"</ul></html>"
//			), "wrap");
		rsphCentrePanel = new LabelledVector3DPanel("Centre");
		rsphCentrePanel.setVector3D(new Vector3D(0, 0, 10));
		rotationallySymmetricPhaseHologramPanel.add(rsphCentrePanel, "wrap");
		rsphBPanel = new DoublePanel();
		rsphBPanel.setNumber(0);
		rsphCPanel = new DoublePanel();
		rsphCPanel.setNumber(0);
		rotationallySymmetricPhaseHologramPanel.add(
				GUIBitsAndBobs.makeRow(
						"<html>d<i>&Phi;</i>/d<i>&phi;</i> = <i>k</i>*</html>",
						rsphBPanel,
						"<html>*<i>r</i>^</html>",
						rsphCPanel,
						","
					),
				"wrap"
			);
		rsphSPanel = new DoublePanel();
		rsphSPanel.setNumber(0);
		rsphTPanel = new DoublePanel();
		rsphTPanel.setNumber(0);
		rotationallySymmetricPhaseHologramPanel.add(
				GUIBitsAndBobs.makeRow(
						"<html>d<i>&Phi;</i>/d<i>r</i> = <i>k</i>*</html>",
						rsphSPanel,
						"<html>*<i>r</i>^</html>",
						rsphTPanel
					),
				"wrap"
			);
		rsphSimulateHonestlyCheckBox = new JCheckBox("Simulate honestly");
		rsphSimulateHonestlyCheckBox.setSelected(true);
		rotationallySymmetricPhaseHologramPanel.add(rsphSimulateHonestlyCheckBox, "wrap");
		rsphNPanel = new IntPanel();
		rsphNPanel.setNumber(8);
		rotationallySymmetricPhaseHologramPanel.add(
				GUIBitsAndBobs.makeRow(
						"Divide up into",
						rsphNPanel,
						"segments,"
					),
				"wrap"
			);
		rsphD0Panel = new Vector3DPanel();
		rsphD0Panel.setVector3D(new Vector3D(1, 0, 0));
		rotationallySymmetricPhaseHologramPanel.add(
				GUIBitsAndBobs.makeRow(
						"the 1st of which is centred in direction",
						rsphD0Panel
					)
			);

		
		// radial lenticular array
		
		phaseHologramOfRadialLenticularArrayPanel = new JPanel();
		phaseHologramOfRadialLenticularArrayPanel.setLayout(new MigLayout("insets 0"));
		rlapCentrePanel = new LabelledVector3DPanel("Centre");
		rlapCentrePanel.setVector3D(new Vector3D(0, 0, 10));
		phaseHologramOfRadialLenticularArrayPanel.add(rlapCentrePanel, "wrap");
		rlapFPanel = new LabelledDoublePanel("focal length of each cylindrical lens");
		rlapFPanel.setNumber(1);
		phaseHologramOfRadialLenticularArrayPanel.add(rlapFPanel, "wrap");
		rlapNPanel = new LabelledDoublePanel("number of cylindrical lenses");
		rlapNPanel.setNumber(4);
		phaseHologramOfRadialLenticularArrayPanel.add(rlapNPanel, "wrap");
		rlapD0Panel = new LabelledVector3DPanel("Direction of cylinder axis of 0th cylindrical lens");
		rlapD0Panel.setVector3D(new Vector3D(1, 0, 0));
		phaseHologramOfRadialLenticularArrayPanel.add(rlapD0Panel, "wrap");
		
		flipAxisAnglePanel = new LabelledDoublePanel("Angle of flip direction w.r.t. standard direction (degrees)");
		flipAxisAnglePanel.setNumber(0.0);
		
		tilingParametersButton = new JButton(TILING_PARAMS_BUTTON_TEXT);
		tilingParametersButton.addActionListener(this);
//		tilingParametersPanel = new TilingParametersLine();
//		tilingParametersPanel.setPeriod1(1.0);
//		tilingParametersPanel.setPeriod2(1.0);
		
		teleportingParametersPanel = new JPanel();
		teleportingParametersPanel.setLayout(new MigLayout("insets 0"));
		teleportingParametersLine = new LabelledTeleportingTargetsComboBox(getScene());
		teleportingParametersPanel.add(teleportingParametersLine, "wrap");
		teleportationTypeComboBox = new JComboBox<TeleportationType>(TeleportationType.values());
		teleportationTypeComboBox.setSelectedItem(TeleportationType.PERFECT);
		JPanel teleportationTypePanel = new JPanel(new FlowLayout());
		teleportationTypePanel.add(new JLabel("Teleportation type"));
		teleportationTypePanel.add(teleportationTypeComboBox);
		teleportationTypePanel.validate();
		teleportingParametersPanel.add(teleportationTypePanel);
		teleportingParametersPanel.validate();
		
		twoSidedParametersButton = new JButton(TWO_SIDED_PARAMS_BUTTON_TEXT);
		twoSidedParametersButton.addActionListener(this);
		
		CLAsPanel = new JPanel();
		CLAsPanel.setLayout(new MigLayout("insets 0"));
		CLAsEtaPanel = new LabelledDoublePanel("eta (for outwards refraction)");
		CLAsEtaPanel.setNumber(1);
		CLAsPanel.add(CLAsEtaPanel, "wrap");
		CLAsTransmissionCoefficientPanel = new LabelledDoublePanel("Transmission coefficient");
		CLAsTransmissionCoefficientPanel.setNumber(SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT);
		CLAsPanel.add(CLAsTransmissionCoefficientPanel);
		CLAsPanel.validate();

		gCLAsPanel = new JPanel();
		gCLAsPanel.setLayout(new MigLayout("insets 0"));
		aVector3DPanel = new LabelledVector3DPanel("a");
		aVector3DPanel.setToolTipText("This vector defines the direction of the lenslets' optical axes.  It points from array 1 to array 2, and its length is irrelevant.");
		aVector3DPanel.setVector3D(new Vector3D(0, 0, 1));
		gCLAsPanel.add(aVector3DPanel, "wrap");
		uVector3DPanel = new LabelledVector3DPanel("u");
		uVector3DPanel.setToolTipText("This vector defines the 'u' direction, which is perpendicular to a direction. Only the part perpendicular to a is used, and the length is irrelevant.");
		uVector3DPanel.setVector3D(new Vector3D(1, 0, 0));
		gCLAsPanel.add(uVector3DPanel, "wrap");
		vVector3DPanel = new LabelledVector3DPanel("v");
		vVector3DPanel.setToolTipText("This vector defines the 'v' direction, which is perpendicular to a direction. Only the part perpendicular to a and u is used, and the length is irrelevant.");
		vVector3DPanel.setVector3D(new Vector3D(0, 1, 0));
		gCLAsPanel.add(vVector3DPanel, "wrap");
		gCLAsBasisComboBox = new JComboBox<GlobalOrLocalCoordinateSystemType>(GlobalOrLocalCoordinateSystemType.values());
		gCLAsBasisComboBox.setSelectedItem(GlobalOrLocalCoordinateSystemType.LOCAL_OBJECT_BASIS);
		JPanel gCLAsBasisPanel = new JPanel(new FlowLayout());
		gCLAsBasisPanel.add(new JLabel("vectors in"));
		gCLAsBasisPanel.add(gCLAsBasisComboBox);
		gCLAsBasisPanel.validate();
		gCLAsPanel.add(gCLAsBasisPanel, "wrap");
		etaPanel = new LabelledVector2DPanel("(eta_u, eta_v)");
		etaPanel.setToolTipText("The eta values in the u and v directions.  These numbers determine the focal-length ratio.");
		etaPanel.setVector2D(1, 1);
		gCLAsPanel.add(etaPanel, "wrap");
		deltaPanel = new LabelledVector2DPanel("(delta_u, delta_v)");
		deltaPanel.setToolTipText("The delta values in the u and v directions.  These numbers define the offset between corresponding lenslets' optical axes.");
		deltaPanel.setVector2D(0, 0);
		gCLAsPanel.add(deltaPanel, "wrap");
		
		JTabbedPane imperfectionsTabbedPane = new JTabbedPane();
		JPanel gCLAsTransmissionCoefficientPanel = new JPanel();
		gCLAsTransmissionCoefficientPanel.setLayout(new MigLayout("insets 0"));
		gCLAsTransmissionCoefficientMethodComboBox = new JComboBox<GCLAsTransmissionCoefficientCalculationMethodType>(GCLAsTransmissionCoefficientCalculationMethodType.values());
		gCLAsTransmissionCoefficientMethodComboBox.setSelectedItem(GCLAsTransmissionCoefficientCalculationMethodType.CONSTANT);
		gCLAsTransmissionCoefficientMethodComboBox.addActionListener(this);
		gCLAsTransmissionCoefficientPanel.add(gCLAsTransmissionCoefficientMethodComboBox, "wrap");
		gCLAsConstantTransmissionCoefficientPanel = new LabelledDoublePanel("Transmission coefficient");
		gCLAsConstantTransmissionCoefficientPanel.setNumber(SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT);
		gCLAsTransmissionCoefficientPanel.add(gCLAsConstantTransmissionCoefficientPanel, "wrap");
		gCLAsPanel.add(gCLAsTransmissionCoefficientPanel);
		imperfectionsTabbedPane.add("Transmission coefficient", gCLAsTransmissionCoefficientPanel);
		// imperfectionsTabbedPane.add("Blur", outsideMetricTensorPanel);
		JPanel gCLAsPixellationPanel = new JPanel();
		gCLAsPixellationPanel.setLayout(new MigLayout("insets 0"));
		gCLAsSimulateDiffractiveBlurCheckBox = new JCheckBox("Simulate diffractive blur");
		gCLAsSimulateDiffractiveBlurCheckBox.setAlignmentX(Component.CENTER_ALIGNMENT);
		gCLAsSimulateDiffractiveBlurCheckBox.setSelected(false);
		gCLAsPixellationPanel.add(gCLAsSimulateDiffractiveBlurCheckBox, "wrap");
		gCLAsSimulateRayOffsetCheckBox = new JCheckBox("Simulate ray offset");
		gCLAsSimulateRayOffsetCheckBox.setAlignmentX(Component.CENTER_ALIGNMENT);
		gCLAsSimulateRayOffsetCheckBox.setSelected(false);
		gCLAsPixellationPanel.add(gCLAsSimulateRayOffsetCheckBox, "wrap");
		gCLAsPixelSideLengthPanel = new LabelledScientificDoublePanel("Side length of square pixels");
		gCLAsPixelSideLengthPanel.setNumber(2e-3);
		gCLAsPixellationPanel.add(gCLAsPixelSideLengthPanel, "wrap");
		gCLAsLambdaPanel = new LabelledScientificDoublePanel("Wavelength for which diffractive blur is simulated");
		gCLAsLambdaPanel.setNumber(564e-9);
		gCLAsPixellationPanel.add(gCLAsLambdaPanel, "wrap");
		imperfectionsTabbedPane.add("Pixellation", gCLAsPixellationPanel);
		gCLAsPanel.add(imperfectionsTabbedPane, "wrap");
//		gCLAsCalculateGeometricalTransmissionCoefficientCheckBox = new JCheckBox("Calculate geometrical transmission coefficient");
//		gCLAsCalculateGeometricalTransmissionCoefficientCheckBox.setToolTipText("Calculate the geometrical transmission coefficient?");
//		gCLAsCalculateGeometricalTransmissionCoefficientCheckBox.setSelected(true);
//		gCLAsPanel.add(gCLAsCalculateGeometricalTransmissionCoefficientCheckBox, "wrap");
		gCLAsPanel.validate();

		//
		// GlensHologram
		// 
		
		glensHologramPanel = new JPanel();
		glensHologramPanel.setLayout(new MigLayout("insets 0"));
		nodalPointFVector3DPanel = new LabelledVector3DPanel("N/G (N=Nodal point)");
		nodalPointFVector3DPanel.setToolTipText("This vector defines the glens's nodal point, N, divided by G.");
		nodalPointFVector3DPanel.setVector3D(new Vector3D(0, 0, 0));
		glensHologramPanel.add(nodalPointFVector3DPanel, "wrap");
		pointOnGlensVector3DPanel = new LabelledVector3DPanel("Point on glens");
		pointOnGlensVector3DPanel.setToolTipText("A point on the glens");
		pointOnGlensVector3DPanel.setVector3D(new Vector3D(0, 0, 0));
		glensHologramPanel.add(pointOnGlensVector3DPanel, "wrap");
		opticalAxisPosVector3DPanel = new LabelledVector3DPanel("Optical-axis direction, +ve direction");
		opticalAxisPosVector3DPanel.setToolTipText("This vector defines +ve optical-axis direction.");
		opticalAxisPosVector3DPanel.setVector3D(new Vector3D(0, 0, 1));
		glensHologramPanel.add(opticalAxisPosVector3DPanel, "wrap");
		meanFPanel = new LabelledDoublePanel("Scaling parameter, G");
		meanFPanel.setToolTipText("A parameter that makes lengths dimensionless and finite.");
		meanFPanel.setNumber(1);
		glensHologramPanel.add(meanFPanel, "wrap");
		focalLengthNegFPanel = new LabelledDoublePanel("Focal length / G, -ve space");
		focalLengthNegFPanel.setNumber(-1);
		glensHologramPanel.add(focalLengthNegFPanel, "wrap");
		focalLengthPosFPanel = new LabelledDoublePanel("Focal length / G, +ve space");
		focalLengthPosFPanel.setNumber(1);
		glensHologramPanel.add(focalLengthPosFPanel, "wrap");
		glensTransmissionCoefficientPanel = new LabelledDoublePanel("Transmission coefficient");
		glensTransmissionCoefficientPanel.setNumber(SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT);
		glensHologramPanel.add(glensTransmissionCoefficientPanel);
		glensHologramPanel.validate();
		
		//
		// lensHologram
		// 
		
		lensHologramPanel = new JPanel();
		lensHologramPanel.setLayout(new MigLayout("insets 0"));
		principalPointVector3DPanel = new LabelledVector3DPanel("Principal point");
		principalPointVector3DPanel.setToolTipText("This vector defines the ideal thin lens's principal point.");
		principalPointVector3DPanel.setVector3D(new Vector3D(0, 0, 0));
		lensHologramPanel.add(principalPointVector3DPanel, "wrap");
		opticalAxisVector3DPanel = new LabelledVector3DPanel("Optical-axis direction");
		opticalAxisVector3DPanel.setToolTipText("This vector defines the optical-axis direction.");
		opticalAxisVector3DPanel.setVector3D(new Vector3D(0, 0, 1));
		lensHologramPanel.add(opticalAxisVector3DPanel, "wrap");
		focalLengthPanel = new LabelledDoublePanel("Focal length");
		focalLengthPanel.setNumber(-1);
		lensHologramPanel.add(focalLengthPanel, "wrap");
		lensTransmissionCoefficientPanel = new LabelledDoublePanel("Transmission coefficient");
		lensTransmissionCoefficientPanel.setNumber(SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT);
		lensHologramPanel.add(lensTransmissionCoefficientPanel);
		lensHologramPanel.validate();
		
		//
		// cylindrical-lens hologram
		//
		
		cylindricalLensHologramPanel = new JPanel();
		cylindricalLensHologramPanel.setLayout(new MigLayout("insets 0"));
		pointOnAxisVector3DPanel = new LabelledVector3DPanel("Point on lens's central line");
		pointOnAxisVector3DPanel.setVector3D(new Vector3D(0, 0, 0));
		cylindricalLensHologramPanel.add(pointOnAxisVector3DPanel, "wrap");
		phaseGradientDirectionVector3DPanel = new LabelledVector3DPanel("Direction of phase gradient");
		phaseGradientDirectionVector3DPanel.setVector3D(new Vector3D(1, 0, 0));
		cylindricalLensHologramPanel.add(phaseGradientDirectionVector3DPanel, "wrap");
		cylindricalLensHologramFocalLengthPanel = new LabelledDoublePanel("Focal length");
		cylindricalLensHologramFocalLengthPanel.setNumber(1);
		cylindricalLensHologramPanel.add(cylindricalLensHologramFocalLengthPanel, "wrap");
		cylindricalLensHologramTransmissionCoefficientPanel = new LabelledDoublePanel("Transmission coefficient");
		cylindricalLensHologramTransmissionCoefficientPanel.setNumber(SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT);
		cylindricalLensHologramPanel.add(cylindricalLensHologramTransmissionCoefficientPanel, "wrap");
		
		//
		// lenslet-array panel
		//
		
		lensletArrayPanel = new JPanel(new MigLayout("insets 0"));
		lensletArrayFocalLengthPanel = new LabelledDoublePanel("Focal length");
		lensletArrayFocalLengthPanel.setNumber(1);
		lensletArrayPanel.add(lensletArrayFocalLengthPanel, "wrap"); 
		lensletArrayPeriodPanel = new LabelledVector2DPanel("Period in (x,y)");
		lensletArrayPeriodPanel.setVector2D(0.1, 0.1);
		lensletArrayPanel.add(lensletArrayPeriodPanel, "wrap");
		lensletArrayOffsetPanel = new LabelledVector2DPanel("Offset in (x,y)");
		lensletArrayOffsetPanel.setVector2D(0, 0);
		lensletArrayPanel.add(lensletArrayOffsetPanel, "wrap");
		lensletArraySimulateDiffractiveBlurCheckBox = new JCheckBox("Simulate diffractive blur");
		lensletArraySimulateDiffractiveBlurCheckBox.setSelected(true);
		lensletArrayLambdaPanel = new LabelledDoublePanel("Lambda");
		lensletArrayLambdaPanel.setNumber(632.8e-9);
		lensletArrayPanel.add(GUIBitsAndBobs.makeRow(lensletArraySimulateDiffractiveBlurCheckBox, lensletArrayLambdaPanel), "wrap");

		lensletArrayTransmissionCoefficientPanel = new LabelledDoublePanel("Transmission coefficient");
		lensletArrayTransmissionCoefficientPanel.setNumber(SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT);
		lensletArrayPanel.add(lensletArrayTransmissionCoefficientPanel, "wrap");

		//
		// lenticular array with linearly-varying focussing power
		//
		
		lenticularArrayWithLinearPowerPanel = new JPanel(new MigLayout("insets 0"));
		lawlpP0Panel = new LabelledVector3DPanel("Point on cylinder axis of 0th cylindrical lens");
		lawlpP0Panel.setVector3D(new Vector3D(0, 0, 0));
		lenticularArrayWithLinearPowerPanel.add(lawlpP0Panel, "wrap");
		lawlpUHatPanel = new LabelledVector3DPanel("Direction of periodicity");
		lawlpUHatPanel.setVector3D(new Vector3D(1, 0, 0));
		lenticularArrayWithLinearPowerPanel.add(lawlpUHatPanel, "wrap");
		lawlpPeriodPanel = new LabelledDoublePanel("Period");
		lawlpPeriodPanel.setNumber(1);
		lenticularArrayWithLinearPowerPanel.add(lawlpPeriodPanel, "wrap");
		lawlpDPduPanel = new LabelledDoublePanel("Focussing power / u");
		lawlpDPduPanel.setNumber(1);
		lenticularArrayWithLinearPowerPanel.add(lawlpDPduPanel, "wrap");

		//
		// crossed lenticular arrays with linearly-varying focussing power
		//
		
		crossedLenticularArraysWithLinearPowerPanel = new JPanel(new MigLayout("insets 0"));
		clawlpP0Panel = new LabelledVector3DPanel("Point on cylinder axis of 0th cylindrical lens");
		clawlpP0Panel.setVector3D(new Vector3D(0, 0, 0));
		crossedLenticularArraysWithLinearPowerPanel.add(clawlpP0Panel, "wrap");
		clawlpUHatPanel = new LabelledVector3DPanel("First direction of periodicity");
		clawlpUHatPanel.setVector3D(new Vector3D(1, 0, 0));
		crossedLenticularArraysWithLinearPowerPanel.add(clawlpUHatPanel, "wrap");
		clawlpVHatPanel = new LabelledVector3DPanel("Second direction of periodicity");
		clawlpVHatPanel.setVector3D(new Vector3D(0, 1, 0));
		crossedLenticularArraysWithLinearPowerPanel.add(clawlpVHatPanel, "wrap");
		clawlpPeriodPanel = new LabelledDoublePanel("Period");
		clawlpPeriodPanel.setNumber(1);
		crossedLenticularArraysWithLinearPowerPanel.add(clawlpPeriodPanel, "wrap");
		clawlpDPduPanel = new LabelledDoublePanel("Focussing power / u");
		clawlpDPduPanel.setNumber(1);
		crossedLenticularArraysWithLinearPowerPanel.add(clawlpDPduPanel, "wrap");

		//
		// idealised Dove-prism array panel
		//
		
		idealisedDovePrismArrayPanel = new JPanel(new MigLayout("insets 0"));
		idpaDovePrism0CentrePanel = new LabelledVector3DPanel("Centre of 0th Dove prism");
		idpaDovePrism0CentrePanel.setVector3D(new Vector3D(0, 0, 0));
		idealisedDovePrismArrayPanel.add(idpaDovePrism0CentrePanel, "wrap");
		idpaInversionDirectionPanel = new LabelledVector3DPanel("Inversion direction");
		idpaInversionDirectionPanel.setVector3D(new Vector3D(1, 0, 0));
		idealisedDovePrismArrayPanel.add(idpaInversionDirectionPanel, "wrap");
		idpaPeriodPanel = new LabelledDoublePanel("Period");
		idpaPeriodPanel.setNumber(0.1);
		idealisedDovePrismArrayPanel.add(idpaPeriodPanel, "wrap");
		idpaTransmissionCoefficientPanel = new LabelledDoublePanel("Transmission coefficient");
		idpaTransmissionCoefficientPanel.setNumber(SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT);
		idealisedDovePrismArrayPanel.add(idpaTransmissionCoefficientPanel, "wrap");

		
		//
		// pixellation
		//
		
		pixellationPanel = new JPanel(new MigLayout("insets 0"));
		pixellationPixelSideLengthUPanel = new LabelledDoublePanel("Pixel side length 1");
		pixellationPixelSideLengthUPanel.setNumber(1e-4);
		pixellationPanel.add(pixellationPixelSideLengthUPanel, "wrap");
		pixellationPixelSideLengthVPanel = new LabelledDoublePanel("Pixel side length 2");
		pixellationPixelSideLengthVPanel.setNumber(1e-4);
		pixellationPanel.add(pixellationPixelSideLengthVPanel, "wrap");
		pixellationLambdaPanel = new LabelledDoublePanel("Wavelength", DoublePanel.SCIENTIFIC_PATTERN);
		pixellationLambdaPanel.setNumber(632.8e-9);
		pixellationLambdaPanel.getDoublePanel().setColumns(6);
		pixellationPanel.add(pixellationLambdaPanel, "wrap");
		pixellationSimulateDiffractiveBlurCheckBox = new JCheckBox("Simulate diffractive blur");
		pixellationSimulateDiffractiveBlurCheckBox.setSelected(true);
		pixellationPanel.add(pixellationSimulateDiffractiveBlurCheckBox, "wrap");
		pixellationSimulateRayOffsetCheckBox = new JCheckBox("Simulate ray offset");
		pixellationSimulateRayOffsetCheckBox.setSelected(true);
		pixellationPanel.add(pixellationSimulateRayOffsetCheckBox, "wrap");
		pixellationPanel.validate();
		

		galileoTransformInterfacePanel = new JPanel();
		galileoTransformInterfacePanel.setLayout(new MigLayout("insets 0"));
		transmissionCoefficientGalileoPanel = new LabelledDoublePanel("Transmission coefficient");
		transmissionCoefficientGalileoPanel.setNumber(SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT);
		betaGalileoVector3DPanel = new LabelledVector3DPanel("beta");
		betaGalileoVector3DPanel.setToolTipText("The speed of the second frame relative to the first, in units of c");
		betaGalileoVector3DPanel.setVector3D(new Vector3D(0, 0, 0));
		galileoTransformInterfacePanel.add(betaGalileoVector3DPanel, "wrap");
		betaGalileoBasisComboBox = new JComboBox<CoordinateSystemType>(CoordinateSystemType.values());
		betaGalileoBasisComboBox.setSelectedItem(CoordinateSystemType.NORMALSED_LOCAL_OBJECT_BASIS);
		JPanel betaGalileoBasisPanel = new JPanel(new FlowLayout());
		betaGalileoBasisPanel.add(new JLabel("beta in"));
		betaGalileoBasisPanel.add(betaGalileoBasisComboBox);
		betaGalileoBasisPanel.validate();
		galileoTransformInterfacePanel.add(betaGalileoBasisPanel, "wrap");
		galileoTransformInterfacePanel.add(transmissionCoefficientGalileoPanel);
		galileoTransformInterfacePanel.validate();

		lorentzTransformInterfacePanel = new JPanel();
		lorentzTransformInterfacePanel.setLayout(new MigLayout("insets 0"));
		betaVector3DPanel = new LabelledVector3DPanel("beta");
		betaVector3DPanel.setToolTipText("The speed of the second frame relative to the first, in units of c");
		betaVector3DPanel.setVector3D(new Vector3D(0, 0, 0));
		lorentzTransformInterfacePanel.add(betaVector3DPanel, "wrap");
		betaBasisComboBox = new JComboBox<CoordinateSystemType>(CoordinateSystemType.values());
		betaBasisComboBox.setSelectedItem(CoordinateSystemType.NORMALSED_LOCAL_OBJECT_BASIS);
		JPanel betaBasisPanel = new JPanel(new FlowLayout());
		betaBasisPanel.add(new JLabel("beta in"));
		betaBasisPanel.add(betaBasisComboBox);
		betaBasisPanel.validate();
		lorentzTransformInterfacePanel.add(betaBasisPanel, "wrap");
		lorentzTransformInterfacePanel.add(transmissionCoefficientPanel);
		lorentzTransformInterfacePanel.validate();

		metricInterfacePanel = new JPanel();
		metricInterfacePanel.setLayout(new MigLayout("insets 0"));
		JTabbedPane metricTensorsTabbedPane = new JTabbedPane();
		insideMetricTensorPanel = new LabelledSymmetricMatrix3DPanel("g =");
		insideMetricTensorPanel.setMatrix3D(MetricInterface.getMetricTensorForRefractiveIndex(1.0));
		outsideMetricTensorPanel = new LabelledSymmetricMatrix3DPanel("h =");
		outsideMetricTensorPanel.setMatrix3D(MetricInterface.getMetricTensorForRefractiveIndex(1.0));
		metricTensorsTabbedPane.add("inside", insideMetricTensorPanel);
		metricTensorsTabbedPane.add("outside", outsideMetricTensorPanel);
		metricInterfacePanel.add(metricTensorsTabbedPane, "wrap");
		metricTensorsBasisComboBox = new JComboBox<GlobalOrLocalCoordinateSystemType>(GlobalOrLocalCoordinateSystemType.values());
		metricTensorsBasisComboBox.setSelectedItem(GlobalOrLocalCoordinateSystemType.LOCAL_OBJECT_BASIS);
		JPanel metricTensorsBasisPanel = new JPanel(new FlowLayout());
		metricTensorsBasisPanel.add(new JLabel("metric tensors in"));
		metricTensorsBasisPanel.add(metricTensorsBasisComboBox);
		metricInterfacePanel.add(metricTensorsBasisPanel, "wrap");
		refractionTypeComboBox = new JComboBox<RefractionType>(RefractionType.values());
		refractionTypeComboBox.setSelectedItem(RefractionType.POSITIVE_REFRACTION);
		metricInterfacePanel.add(refractionTypeComboBox, "wrap");
		allowImaginaryOpticalPathLengthsCheckBox = new JCheckBox("Allow imaginary optical path lengths");
		allowImaginaryOpticalPathLengthsCheckBox.setAlignmentX(Component.CENTER_ALIGNMENT);
		allowImaginaryOpticalPathLengthsCheckBox.setSelected(false);
		metricInterfacePanel.add(allowImaginaryOpticalPathLengthsCheckBox, "wrap");
		metricInterfaceTransmissionCoefficientPanel = new LabelledDoublePanel("Transmission coefficient");
		metricInterfaceTransmissionCoefficientPanel.setNumber(SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT);
		metricInterfacePanel.add(metricInterfaceTransmissionCoefficientPanel);
		metricInterfacePanel.validate();
		
		phaseConjugatingPanel = new JPanel();
		phaseConjugatingPanel.setLayout(new MigLayout("insets 0"));
		isPhaseConjugatingSurfaceReflectiveCheckBox = new JCheckBox("Reflective");
		isPhaseConjugatingSurfaceReflectiveCheckBox.setAlignmentX(Component.CENTER_ALIGNMENT);
		isPhaseConjugatingSurfaceReflectiveCheckBox.setSelected(true);
		phaseConjugatingPanel.add(isPhaseConjugatingSurfaceReflectiveCheckBox, "wrap");
		phaseConjugatingSurfaceTransmissionCoefficientPanel = new LabelledDoublePanel("Transmission/reflection coefficient");
		phaseConjugatingSurfaceTransmissionCoefficientPanel.setNumber(SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT);
		phaseConjugatingPanel.add(phaseConjugatingSurfaceTransmissionCoefficientPanel);
		phaseConjugatingPanel.validate();
		
		phaseFrontPanel = new JPanel();
		phaseFrontPanel.setLayout(new MigLayout("insets 0"));
		phaseFrontColourPanel = new LabelledDoubleColourPanel("Colour");
		phaseFrontColourPanel.setDoubleColour(DoubleColour.RED);
		phaseFrontPanel.add(phaseFrontColourPanel, "wrap");
		angularFuzzinessDegPanel = new LabelledDoublePanel("Angular fuzziness (deg)");
		angularFuzzinessDegPanel.setNumber(1);
		phaseFrontPanel.add(angularFuzzinessDegPanel);
		bidirectionalRaysCheckBox =  new JCheckBox("Bidirectional rays");
		bidirectionalRaysCheckBox.setSelected(false);
		phaseFrontPanel.add(bidirectionalRaysCheckBox);

		point2pointImagingPanel = new JPanel();
		point2pointImagingPanel.setLayout(new MigLayout("insets 0"));
		point2pointImagingPoint1Label = new JLabel("Inside-space position");
		point2pointImagingPoint1Panel = new Vector3DPanel();
		point2pointImagingPoint1Panel.setVector3D(new Vector3D(0, 0, 0));
		point2pointImagingPanel.add(GUIBitsAndBobs.makeRow(point2pointImagingPoint1Label, point2pointImagingPoint1Panel), "wrap");
		point2pointImagingPoint2Label = new JLabel("Outside-space position");
		point2pointImagingPoint2Panel = new Vector3DPanel();
		point2pointImagingPoint2Panel.setVector3D(new Vector3D(0, 0, 0));
		point2pointImagingPanel.add(GUIBitsAndBobs.makeRow(point2pointImagingPoint2Label, point2pointImagingPoint2Panel), "wrap");
		point2pointImagingIsReflectiveCheckBox = new JCheckBox("Reflective");
		point2pointImagingIsReflectiveCheckBox.setAlignmentX(Component.CENTER_ALIGNMENT);
		point2pointImagingIsReflectiveCheckBox.addActionListener(this);
		point2pointImagingIsReflectiveCheckBox.setSelected(false);
		point2pointImagingPanel.add(point2pointImagingIsReflectiveCheckBox);
		point2pointImagingPanel.validate();
		
		rainbowPanel = new JPanel();
		rainbowPanel.setLayout(new MigLayout("insets 0"));
		point2pointImagingPoint1Label = new JLabel("Inside-space position");
		rainbowLightSourcePositionPanel = new Vector3DPanel();
		rainbowLightSourcePositionPanel.setVector3D(new Vector3D(100,300,-500));	// position of the standard Phong light source
		rainbowPanel.add(GUIBitsAndBobs.makeRow(new JLabel("Light-source position"), rainbowLightSourcePositionPanel), "wrap");
		rainbowSaturationPanel = new LabelledDoublePanel("Saturation");
		rainbowSaturationPanel.setNumber(1);
		rainbowPanel.add(rainbowSaturationPanel, "wrap");
		rainbowLightnessPanel = new LabelledDoublePanel("Lightness");
		rainbowLightnessPanel.setNumber(0.25);
		rainbowPanel.add(rainbowLightnessPanel, "wrap");
		rainbowPanel.validate();
		
		picturePanel = new JPanel();
		picturePanel.setLayout(new MigLayout("insets 0"));
		pictureCorner = new LabelledVector2DPanel("Corner (object coordinates)");
		pictureCorner.setVector2D(new Vector2D(0, 0));
		picturePanel.add(pictureCorner, "wrap");
		pictureSize = new LabelledVector2DPanel("Size (object coordinates)");
		pictureSize.setVector2D(new Vector2D(1, 1));
		picturePanel.add(pictureSize, "wrap");
		choosePictureFileButton = new JButton("Choose...");
		choosePictureFileButton.addActionListener(this);
		pictureFileNameField = new JTextField(20);
		pictureFileNameField.setText("-- not selected --");
		picturePanel.add(GUIBitsAndBobs.makeRow("Picture file", pictureFileNameField, choosePictureFileButton), "wrap");
		pictureSurfaceTiledCheckbox = new JCheckBox("Tiled");
		pictureSurfaceTiledCheckbox.setAlignmentX(Component.CENTER_ALIGNMENT);
		pictureSurfaceTiledCheckbox.setSelected(true);
		picturePanel.add(pictureSurfaceTiledCheckbox);
		picturePanel.validate();
		
		// variable-eta CLAs panel
		
		variableEtaCLAsPanel = new JPanel(new MigLayout("insets 0"));
		variableEtaCLAsEtaAtPPanel = new LabelledDoublePanel("eta at point P");
		variableEtaCLAsEtaAtPPanel.setNumber(1);
		variableEtaCLAsPanel.add(variableEtaCLAsEtaAtPPanel, "wrap");
		variableEtaPointPPanel = new LabelledVector3DPanel("Point P");
		variableEtaPointPPanel.setVector3D(new Vector3D(0, 0, 0));
		variableEtaCLAsPanel.add(variableEtaPointPPanel, "wrap");
		variableEtaGradEtaPanel = new LabelledVector3DPanel("grad(eta)");
		variableEtaGradEtaPanel.setVector3D(new Vector3D(1, 0, 0));
		variableEtaCLAsPanel.add(variableEtaGradEtaPanel, "wrap");
		variableEtaCLAsTransmissionCoefficientPanel = new LabelledDoublePanel("Transmission coefficient");
		variableEtaCLAsTransmissionCoefficientPanel.setNumber(SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT);
		variableEtaCLAsPanel.add(variableEtaCLAsTransmissionCoefficientPanel, "wrap");
		variableEtaCLAsPanel.validate();
		
		JLabel NAP = new JLabel("-- no additional parameters --");
		// NAP.setEnabled(false);
		disabledParametersPanel = new JPanel();
		disabledParametersPanel.add(NAP);
		
		// setOptionalParameterPanelComponent(refractiveIndexRatioPanel);
		
		// setPreferredSize(new Dimension(400, 140));
		
		validate();
	}

	public SurfacePropertyPanel(String description, SceneObject scene)
	{
		this(description, true, scene);
	}

	public SurfacePropertyPanel(SceneObject scene)
	{
		this("Surface", scene);
	}
	
	
	//
	// getters & setters
	//
	
//	public One2OneParametrisedObject getSceneObject() {
//		return sceneObject;
//	}
//
//	public void setSceneObject(One2OneParametrisedObject sceneObject) {
//		this.sceneObject = sceneObject;
//	}

	public void addButtonsActionListener(ActionListener actionListener)
	{
		tilingParametersButton.addActionListener(actionListener);
	}

	public void setIPanel(IPanel iPanel)
	{
		this.iPanel = iPanel;
	}

	private void setOptionalParameterPanelComponent(Component newComponent)
	{
		// remove any component currently in the optional-parameter panel
		while(optionalParametersPanel.getComponentCount() > 0) optionalParametersPanel.remove(0);

		// now add the new component
		if(newComponent != null)
		{
			optionalParametersPanel.add(newComponent);
			optionalParametersPanel.revalidate();
		}

		if(iPanel != null)
		{
			// validate the enclosing panel
			iPanel.mainPanelChanged();
		}
	}
	
	public void setSurfaceProperty(SurfaceProperty surfaceProperty)
	{
		surfaceTiling = new EditableSurfaceTiling(SurfaceColour.GREY50_SHINY, SurfaceColour.WHITE_SHINY, 1, 1, getScene());
		teleportingParametersLine.refreshSceneObjectPrimitivesList();
		pictureSurface = new EditablePictureSurfaceDiffuse((File)null, false, 0, 1, 0, 1);
		twoSidedSurface = new EditableTwoSidedSurface(SurfaceColour.BLACK_MATT, SurfaceColour.WHITE_MATT, getScene());
		
		if(surfaceProperty instanceof SurfacePropertyWithControllableShadow)
		{
			shadowThrowingCheckBox.setVisible(true);
			shadowThrowingCheckBox.setSelected(((SurfacePropertyWithControllableShadow)surfaceProperty).isShadowThrowing());
		}
		else
		{
			shadowThrowingCheckBox.setVisible(false);
		}

		if(surfaceProperty instanceof SurfaceColour)
		{
			surfacePropertyComboBox.setSurfacePropertyType(SurfacePropertyType.COLOURED);
			colourPanel.setDoubleColour(((SurfaceColour)surfaceProperty).getDiffuseColour());
			setOptionalParameterPanelComponent(colourPanel);
		}
		else if(surfaceProperty instanceof ColourFilter)
		{
			surfacePropertyComboBox.setSurfacePropertyType(SurfacePropertyType.COLOUR_FILTER);
			colourPanel.setDoubleColour(((ColourFilter)surfaceProperty).getRgbTransmissionCoefficients());
			setOptionalParameterPanelComponent(colourPanel);
		}
		else if(surfaceProperty instanceof SurfaceColourLightSourceIndependent)
		{
			surfacePropertyComboBox.setSurfacePropertyType(SurfacePropertyType.COLOURED_GLOWING);
			colourPanel.setDoubleColour(((SurfaceColourLightSourceIndependent)surfaceProperty).getColour());
			setOptionalParameterPanelComponent(colourPanel);
		}
		else if(surfaceProperty instanceof SurfaceColourTimeDependent)
		{
			surfacePropertyComboBox.setSurfacePropertyType(SurfacePropertyType.COLOURED_TIME_DEPENDENT);
			surfaceColourTimeDependentPanel.setNumber(((SurfaceColourTimeDependent)surfaceProperty).getPeriod());
			setOptionalParameterPanelComponent(surfaceColourTimeDependentPanel);
		}
		else if(surfaceProperty instanceof ConfocalLensletArrays)
		{
			ConfocalLensletArrays c = (ConfocalLensletArrays)surfaceProperty;
			surfacePropertyComboBox.setSurfacePropertyType(SurfacePropertyType.CLAS);
			CLAsEtaPanel.setNumber(c.getEta());
			CLAsTransmissionCoefficientPanel.setNumber(c.getTransmissionCoefficient());
			setOptionalParameterPanelComponent(CLAsPanel);
		}
		else if(surfaceProperty instanceof PhaseHologramOfCrossedLinearPowerLenticularArrays)
		{
			surfacePropertyComboBox.setSurfacePropertyType(SurfacePropertyType.CROSSED_LENTICULAR_ARRAYS_WITH_LINEAR_POWER);
			clawlpP0Panel.setVector3D(((PhaseHologramOfCrossedLinearPowerLenticularArrays)surfaceProperty).getP0());
			clawlpUHatPanel.setVector3D(((PhaseHologramOfCrossedLinearPowerLenticularArrays)surfaceProperty).getuHat());
			clawlpVHatPanel.setVector3D(((PhaseHologramOfCrossedLinearPowerLenticularArrays)surfaceProperty).getvHat());
			clawlpPeriodPanel.setNumber(((PhaseHologramOfCrossedLinearPowerLenticularArrays)surfaceProperty).getPeriod());
			clawlpDPduPanel.setNumber(((PhaseHologramOfCrossedLinearPowerLenticularArrays)surfaceProperty).getdPdu());
			setOptionalParameterPanelComponent(crossedLenticularArraysWithLinearPowerPanel);
		}
		else if(surfaceProperty instanceof EatonLensSurfaceAngleFormulation)
		{
			surfacePropertyComboBox.setSurfacePropertyType(SurfacePropertyType.EATON_LENS);
			// criticalAngleOfIncidencePanel.setNumber(((EatonLensSurfaceAngleFormulation)surfaceProperty).getCriticalAngle()*180/Math.PI);
			transmissionCoefficientPanel.setNumber(((EatonLensSurfaceAngleFormulation)surfaceProperty).getTransmissionCoefficient());
			// setOptionalParameterPanelComponent(EatonLuneburgLensSurfacePanel);
			setOptionalParameterPanelComponent(transmissionCoefficientPanel);
		}
		else if(surfaceProperty instanceof PhaseHologramOfCylindricalLens)
		{
			surfacePropertyComboBox.setSurfacePropertyType(SurfacePropertyType.CYLINDRICAL_LENS_HOLOGRAM);
			pointOnAxisVector3DPanel.setVector3D(((PhaseHologramOfCylindricalLens)surfaceProperty).getPrincipalPoint());
			phaseGradientDirectionVector3DPanel.setVector3D(((PhaseHologramOfCylindricalLens)surfaceProperty).getPhaseGradientDirection());
			cylindricalLensHologramFocalLengthPanel.setNumber(((PhaseHologramOfCylindricalLens)surfaceProperty).getFocalLength());
			cylindricalLensHologramTransmissionCoefficientPanel.setNumber(((PhaseHologramOfCylindricalLens)surfaceProperty).getTransmissionCoefficient());
			setOptionalParameterPanelComponent(cylindricalLensHologramPanel);
		}
		else if(surfaceProperty instanceof RayFlipping)
		{
			surfacePropertyComboBox.setSurfacePropertyType(SurfacePropertyType.FLIPPING);
			flipAxisAnglePanel.setNumber(((RayFlipping)surfaceProperty).getFlipAxisAngle()*180/Math.PI);
			setOptionalParameterPanelComponent(flipAxisAnglePanel);
		}
		else if(surfaceProperty instanceof GalileoTransformInterface)
		{
			surfacePropertyComboBox.setSurfacePropertyType(SurfacePropertyType.GALILEO_TRANSFORM_INTERFACE);
			betaGalileoVector3DPanel.setVector3D(((GalileoTransformInterface)surfaceProperty).getBeta());
			betaGalileoBasisComboBox.setSelectedItem(((GalileoTransformInterface)surfaceProperty).getBasis());
			transmissionCoefficientGalileoPanel.setNumber(((GalileoTransformInterface)surfaceProperty).getTransmissionCoefficient());
			setOptionalParameterPanelComponent(galileoTransformInterfacePanel);
		}
		else if(surfaceProperty instanceof GCLAsWithApertures)
		{
			GCLAsWithApertures c = (GCLAsWithApertures)surfaceProperty;
			surfacePropertyComboBox.setSurfacePropertyType(SurfacePropertyType.GENERALISED_CLAS);
			aVector3DPanel.setVector3D(c.getAHat());
			uVector3DPanel.setVector3D(c.getUHat());
			vVector3DPanel.setVector3D(c.getVHat());
			etaPanel.setVector2D(c.getEtaU(), c.getEtaV());
//			etaUPanel.setNumber(c.getEtaU());
//			etaVPanel.setNumber(c.getEtaV());
			deltaPanel.setVector2D(c.getDeltaU(), c.getDeltaV());
//			deltaUPanel.setNumber(c.getDeltaU());
//			deltaVPanel.setNumber(c.getDeltaV());
			gCLAsBasisComboBox.setSelectedItem(c.getBasis());
			gCLAsConstantTransmissionCoefficientPanel.setNumber(c.getTransmissionCoefficient());
			gCLAsTransmissionCoefficientMethodComboBox.setSelectedItem(c.getTransmissionCoefficientMethod());
			// gCLAsCalculateGeometricalTransmissionCoefficientCheckBox.setSelected(c.isCalculateGeometricalTransmissionCoefficient());
			gCLAsPixelSideLengthPanel.setNumber(c.getPixelSideLength());
			gCLAsLambdaPanel.setNumber(c.getLambda());
			gCLAsSimulateDiffractiveBlurCheckBox.setSelected(c.isSimulateDiffractiveBlur());
			gCLAsSimulateRayOffsetCheckBox.setSelected(c.isSimulateRayOffset());;

			setOptionalParameterPanelComponent(gCLAsPanel);
		}
		else if(surfaceProperty instanceof GlensSurface)
		{
			GlensSurface c = (GlensSurface)surfaceProperty;
			surfacePropertyComboBox.setSurfacePropertyType(SurfacePropertyType.GLENS_HOLOGRAM);
			nodalPointFVector3DPanel.setVector3D(c.getNodalPointG());
			pointOnGlensVector3DPanel.setVector3D(c.getPointOnGlens());
			opticalAxisPosVector3DPanel.setVector3D(c.getOpticalAxisDirectionPos());
			meanFPanel.setNumber(c.getG());
			focalLengthNegFPanel.setNumber(c.getFocalLengthNegG());
			focalLengthPosFPanel.setNumber(c.getFocalLengthPosG());
			glensTransmissionCoefficientPanel.setNumber(c.getTransmissionCoefficient());
			setOptionalParameterPanelComponent(glensHologramPanel);
		}
		else if(surfaceProperty instanceof IdealThinLensSurface)
		{
			IdealThinLensSurface c = (IdealThinLensSurface)surfaceProperty;
			surfacePropertyComboBox.setSurfacePropertyType(SurfacePropertyType.IDEAL_THIN_LENS_SURFACE);
			opticalAxisVector3DPanel.setVector3D(c.getOpticalAxisDirectionPos());
			principalPointVector3DPanel.setVector3D(c.getPrincipalPoint());
			focalLengthPanel.setNumber(c.getFocalLength());
			lensTransmissionCoefficientPanel.setNumber(c.getTransmissionCoefficient());
			setOptionalParameterPanelComponent(lensHologramPanel);
		}
		else if(surfaceProperty instanceof IdealisedDovePrismArray)
		{
			IdealisedDovePrismArray d = (IdealisedDovePrismArray)surfaceProperty;
			surfacePropertyComboBox.setSurfacePropertyType(SurfacePropertyType.IDEALISED_DOVE_PRISM_ARRAY);
			idpaDovePrism0CentrePanel.setVector3D(d.getDovePrism0Centre());
			idpaInversionDirectionPanel.setVector3D(d.getInversionDirection());
			idpaPeriodPanel.setNumber(d.getPeriod());
			idpaTransmissionCoefficientPanel.setNumber(d.getTransmissionCoefficient());
			setOptionalParameterPanelComponent(idealisedDovePrismArrayPanel);
		}
		else if(surfaceProperty instanceof RectangularIdealThinLensletArray)
		{
			RectangularIdealThinLensletArray la = (RectangularIdealThinLensletArray)surfaceProperty;
			surfacePropertyComboBox.setSurfacePropertyType(SurfacePropertyType.LENSLET_ARRAY);
			lensletArrayFocalLengthPanel.setNumber(la.getFocalLength());
			lensletArrayTransmissionCoefficientPanel.setNumber(la.getTransmissionCoefficient());
			lensletArrayPeriodPanel.setVector2D(la.getxPeriod(), la.getyPeriod());
			lensletArrayOffsetPanel.setVector2D(la.getxOffset(), la.getyOffset());
			lensletArrayLambdaPanel.setNumber(la.getLambda());;
			lensletArraySimulateDiffractiveBlurCheckBox.setSelected(la.isSimulateDiffractiveBlur());
			setOptionalParameterPanelComponent(lensletArrayPanel);
		}
		else if(surfaceProperty instanceof PhaseHologramOfLinearPowerLenticularArray)
		{
			surfacePropertyComboBox.setSurfacePropertyType(SurfacePropertyType.LENTICULAR_ARRAY_WITH_LINEAR_POWER);
			lawlpP0Panel.setVector3D(((PhaseHologramOfLinearPowerLenticularArray)surfaceProperty).getP0());
			lawlpUHatPanel.setVector3D(((PhaseHologramOfLinearPowerLenticularArray)surfaceProperty).getuHat());
			lawlpPeriodPanel.setNumber(((PhaseHologramOfLinearPowerLenticularArray)surfaceProperty).getPeriod());
			lawlpDPduPanel.setNumber(((PhaseHologramOfLinearPowerLenticularArray)surfaceProperty).getdPdu());
			setOptionalParameterPanelComponent(lenticularArrayWithLinearPowerPanel);
		}
		else if(surfaceProperty instanceof LorentzTransformInterface)
		{
			surfacePropertyComboBox.setSurfacePropertyType(SurfacePropertyType.LORENTZ_TRANSFORM_INTERFACE);
			betaVector3DPanel.setVector3D(((LorentzTransformInterface)surfaceProperty).getBeta());
			betaBasisComboBox.setSelectedItem(((LorentzTransformInterface)surfaceProperty).getBasis());
			transmissionCoefficientPanel.setNumber(((LorentzTransformInterface)surfaceProperty).getTransmissionCoefficient());
			setOptionalParameterPanelComponent(lorentzTransformInterfacePanel);
		}
		else if(surfaceProperty instanceof LuneburgLensSurfaceAngleFormulation)
		{
			surfacePropertyComboBox.setSurfacePropertyType(SurfacePropertyType.LUNEBURG_LENS);
			// criticalAngleOfIncidencePanel.setNumber(((LuneburgLensSurfaceAngleFormulation)surfaceProperty).getCriticalAngle()*180/Math.PI);
			transmissionCoefficientPanel.setNumber(((LuneburgLensSurfaceAngleFormulation)surfaceProperty).getTransmissionCoefficient());
			// setOptionalParameterPanelComponent(EatonLuneburgLensSurfacePanel);
			setOptionalParameterPanelComponent(transmissionCoefficientPanel);
		}
		else if(surfaceProperty instanceof MetricInterface)
		{
			surfacePropertyComboBox.setSurfacePropertyType(SurfacePropertyType.METRIC_INTERFACE);
			insideMetricTensorPanel.setMatrix3D(((MetricInterface)surfaceProperty).getMetricTensorInside());
			outsideMetricTensorPanel.setMatrix3D(((MetricInterface)surfaceProperty).getMetricTensorOutside());
			metricTensorsBasisComboBox.setSelectedItem(((MetricInterface)surfaceProperty).getBasis());
			refractionTypeComboBox.setSelectedItem(((MetricInterface)surfaceProperty).getRefractionType());
			allowImaginaryOpticalPathLengthsCheckBox.setSelected(((MetricInterface)surfaceProperty).isAllowImaginaryOpticalPathLengths());
			transmissionCoefficientPanel.setNumber(((MetricInterface)surfaceProperty).getTransmissionCoefficient());
			setOptionalParameterPanelComponent(metricInterfacePanel);
		}
		else if(surfaceProperty instanceof PhaseConjugating)
		{
			surfacePropertyComboBox.setSurfacePropertyType(SurfacePropertyType.PHASE_CONJUGATING);
			boolean isReflective = (((PhaseConjugating)surfaceProperty).getReflectiveOrTransmissive() == SurfaceProperty.ReflectiveOrTransmissive.REFLECTIVE);
			isPhaseConjugatingSurfaceReflectiveCheckBox.setSelected(isReflective);
			phaseConjugatingSurfaceTransmissionCoefficientPanel.setNumber(((PhaseConjugating)surfaceProperty).getTransmissionCoefficient());
			setOptionalParameterPanelComponent(phaseConjugatingPanel);
		}
		else if(surfaceProperty instanceof LightRayFieldRepresentingPhaseFront)
		{
			LightRayFieldRepresentingPhaseFront s = (LightRayFieldRepresentingPhaseFront)surfaceProperty;
			surfacePropertyComboBox.setSurfacePropertyType(SurfacePropertyType.PHASE_FRONT);
			phaseFrontColourPanel.setDoubleColour(s.getColour());
			angularFuzzinessDegPanel.setNumber(MyMath.rad2deg(s.getAngularFuzzinessRad()));
			bidirectionalRaysCheckBox.setSelected(s.isBidirectional());
			setOptionalParameterPanelComponent(phaseFrontPanel);
		}
		else if(surfaceProperty instanceof PhaseHologramOfRadialLenticularArray)
		{
			surfacePropertyComboBox.setSurfacePropertyType(SurfacePropertyType.RADIAL_LENTICULAR_ARRAY);
			rlapCentrePanel.setVector3D(((PhaseHologramOfRadialLenticularArray)surfaceProperty).getCentre());
			rlapFPanel.setNumber(((PhaseHologramOfRadialLenticularArray)surfaceProperty).getF());
			rlapNPanel.setNumber(((PhaseHologramOfRadialLenticularArray)surfaceProperty).getN());
			rlapD0Panel.setVector3D(((PhaseHologramOfRadialLenticularArray)surfaceProperty).getD0());
			setOptionalParameterPanelComponent(phaseHologramOfRadialLenticularArrayPanel);
		}
		else if(surfaceProperty instanceof Point2PointImagingPhaseHologram)
		{
			surfacePropertyComboBox.setSurfacePropertyType(SurfacePropertyType.POINT2POINT_IMAGING);
			point2pointImagingPoint1Panel.setVector3D(((Point2PointImagingPhaseHologram)surfaceProperty).getInsideSpacePoint());
			point2pointImagingPoint2Panel.setVector3D(((Point2PointImagingPhaseHologram)surfaceProperty).getOutsideSpacePoint());
			boolean isReflective = ((Point2PointImagingPhaseHologram)surfaceProperty).isReflective();
			point2pointImagingIsReflectiveCheckBox.setSelected(isReflective);
			updatePoint2pointImagingPanel(isReflective);
			setOptionalParameterPanelComponent(point2pointImagingPanel);
		}
		else if(surfaceProperty instanceof EditablePictureSurfaceDiffuse)
		{
			pictureSurface = (EditablePictureSurfaceDiffuse)surfaceProperty;
			surfacePropertyComboBox.setSurfacePropertyType(SurfacePropertyType.PICTURE);
			pictureCorner.setVector2D(new Vector2D(pictureSurface.getxMin(), pictureSurface.getyMin()));
			pictureSize.setVector2D(new Vector2D(pictureSurface.getxMax() - pictureSurface.getxMin(), pictureSurface.getyMax() - pictureSurface.getyMin()));
			pictureFile = pictureSurface.getPictureFile();
			pictureFileChanged = false;
			pictureFileNameField.setText(pictureSurface.getFilename());
			pictureSurfaceTiledCheckbox.setSelected(pictureSurface.isTiled());
			setOptionalParameterPanelComponent(picturePanel);
		}
		else if(surfaceProperty instanceof Pixellation)
		{
			surfacePropertyComboBox.setSurfacePropertyType(SurfacePropertyType.PIXELLATION);
			Pixellation pixellation = (Pixellation)surfaceProperty;
			pixellationPixelSideLengthUPanel.setNumber(pixellation.getPixelSideLengthU());
			pixellationPixelSideLengthVPanel.setNumber(pixellation.getPixelSideLengthV());
			pixellationLambdaPanel.setNumber(pixellation.getLambda());
			pixellationSimulateDiffractiveBlurCheckBox.setSelected(pixellation.isSimulateDiffractiveBlur());
			pixellationSimulateRayOffsetCheckBox.setSelected(pixellation.isSimulateRayOffset());
			setOptionalParameterPanelComponent(pixellationPanel);
		}
		else if(surfaceProperty instanceof Rainbow)
		{
			surfacePropertyComboBox.setSurfacePropertyType(SurfacePropertyType.RAINBOW);
			Rainbow rainbow = (Rainbow)surfaceProperty;
			rainbowSaturationPanel.setNumber(rainbow.getSaturation());
			rainbowLightnessPanel.setNumber(rainbow.getLightness());
			rainbowLightSourcePositionPanel.setVector3D(rainbow.getLightSourcePosition());
			setOptionalParameterPanelComponent(rainbowPanel);
		}
		else if(surfaceProperty instanceof Reflective)
		{
			surfacePropertyComboBox.setSurfacePropertyType(SurfacePropertyType.REFLECTIVE);
			setOptionalParameterPanelComponent(disabledParametersPanel);
		}
		else if(surfaceProperty instanceof Refractive)
		{
			surfacePropertyComboBox.setSurfacePropertyType(SurfacePropertyType.REFRACTIVE);
			refractiveIndexRatioPanel.setNumber(((Refractive)surfaceProperty).getInsideOutsideRefractiveIndexRatio());
			setOptionalParameterPanelComponent(refractiveIndexRatioPanel);
		}
		else if(surfaceProperty instanceof RefractiveComplex)
		{
			surfacePropertyComboBox.setSurfacePropertyType(SurfacePropertyType.REFRACTIVE_COMPLEX);
			complexRefractiveIndexRatioPanel.setNumber(((RefractiveComplex)surfaceProperty).getInsideOutsideRefractiveIndexRatio());
			setOptionalParameterPanelComponent(complexRefractiveIndexRatioPanel);
		}
		else if(surfaceProperty instanceof RayRotating)
		{
			surfacePropertyComboBox.setSurfacePropertyType(SurfacePropertyType.ROTATING);
			rayRotationAnglePanel.setNumber(((RayRotating)surfaceProperty).getRotationAngle()*180/Math.PI);
			setOptionalParameterPanelComponent(rayRotationAnglePanel);
		}
		else if(surfaceProperty instanceof RayRotatingAboutArbitraryAxisDirection)
		{
			surfacePropertyComboBox.setSurfacePropertyType(SurfacePropertyType.ROTATING_AROUND_ARBITRARY_AXIS_DIRECTION);
			rayRotationAngleArbitraryAxisDirectionPanel.setNumber(((RayRotatingAboutArbitraryAxisDirection)surfaceProperty).getOutwardsRotationAngle()*180/Math.PI);
			rayRotationAxisPanel.setVector3D(((RayRotatingAboutArbitraryAxisDirection)surfaceProperty).getRotationAxisUnitVector());
			rayRotationAxisDirectionBasisComboBox.setSelectedItem(((RayRotatingAboutArbitraryAxisDirection)surfaceProperty).getBasis());
			setOptionalParameterPanelComponent(rayRotationAroundArbitraryAxisDirectionPanel);			
		}
		else if(surfaceProperty instanceof RayRotatingInPlaneOfIncidence)
		{
			surfacePropertyComboBox.setSurfacePropertyType(SurfacePropertyType.ROTATING_IN_PLANE_OF_INCIDENCE);
			rayRotationAnglePanel.setNumber(((RayRotatingInPlaneOfIncidence)surfaceProperty).getRotationAngle()*180/Math.PI);
			setOptionalParameterPanelComponent(rayRotationAnglePanel);
		}
		else if(surfaceProperty instanceof RotationallySymmetricPhaseHologram)
		{
			surfacePropertyComboBox.setSurfacePropertyType(SurfacePropertyType.ROTATIONALLY_SYMMETRIC_PHASE_HOLOGRAM);
			rsphCentrePanel.setVector3D(((RotationallySymmetricPhaseHologram)surfaceProperty).getCentre());
			rsphBPanel.setNumber(((RotationallySymmetricPhaseHologram)surfaceProperty).getB());
			rsphCPanel.setNumber(((RotationallySymmetricPhaseHologram)surfaceProperty).getC());
			rsphSPanel.setNumber(((RotationallySymmetricPhaseHologram)surfaceProperty).getS());
			rsphTPanel.setNumber(((RotationallySymmetricPhaseHologram)surfaceProperty).getT());
			rsphSimulateHonestlyCheckBox.setSelected(((RotationallySymmetricPhaseHologram)surfaceProperty).isSimulateHonestly());
			rsphNPanel.setNumber(((RotationallySymmetricPhaseHologram)surfaceProperty).getN());
			rsphD0Panel.setVector3D(((RotationallySymmetricPhaseHologram)surfaceProperty).getD0());
			setOptionalParameterPanelComponent(rotationallySymmetricPhaseHologramPanel);
		}
		else if(surfaceProperty instanceof Teleporting)
		{
			surfacePropertyComboBox.setSurfacePropertyType(SurfacePropertyType.TELEPORTING);
			teleportingParametersLine.setDestinationObject(((Teleporting)surfaceProperty).getDestinationObject());
			teleportationTypeComboBox.setSelectedItem(((Teleporting)surfaceProperty).getTeleportationType());
			setOptionalParameterPanelComponent(teleportingParametersPanel);
		}
		else if(surfaceProperty instanceof EditableSurfaceTiling)
		{
			surfacePropertyComboBox.setSurfacePropertyType(SurfacePropertyType.TILED);
			surfaceTiling = (EditableSurfaceTiling)surfaceProperty;
//			tilingParametersPanel.setPeriod1(((SurfaceTiling)surfaceProperty).getWidthU());
//			tilingParametersPanel.setPeriod2(((SurfaceTiling)surfaceProperty).getWidthV());
			setOptionalParameterPanelComponent(tilingParametersButton);
		}
		else if(surfaceProperty instanceof Transparent)
		{
			surfacePropertyComboBox.setSurfacePropertyType(SurfacePropertyType.TRANSPARENT);
			transmissionCoefficientPanel.setNumber(((Transparent)surfaceProperty).getTransmissionCoefficient());
			setOptionalParameterPanelComponent(transmissionCoefficientPanel);
			// setOptionalParameterPanelComponent(disabledParametersPanel);
		}
		else if(surfaceProperty instanceof EditableTwoSidedSurface)
		{
			surfacePropertyComboBox.setSurfacePropertyType(SurfacePropertyType.TWO_SIDED);
			twoSidedSurface = (EditableTwoSidedSurface)surfaceProperty;
//			tilingParametersPanel.setPeriod1(((SurfaceTiling)surfaceProperty).getWidthU());
//			tilingParametersPanel.setPeriod2(((SurfaceTiling)surfaceProperty).getWidthV());
			setOptionalParameterPanelComponent(twoSidedParametersButton);
		}
		else if(surfaceProperty instanceof VariableEtaConfocalLensletArrays)
		{
			surfacePropertyComboBox.setSurfacePropertyType(SurfacePropertyType.VARIABLE_ETA_CLAS);
			variableEtaCLAsEtaAtPPanel.setNumber(((VariableEtaConfocalLensletArrays)surfaceProperty).getInwardsEtaAtP());
			variableEtaPointPPanel.setVector3D(((VariableEtaConfocalLensletArrays)surfaceProperty).getPointP());
			variableEtaGradEtaPanel.setVector3D(((VariableEtaConfocalLensletArrays)surfaceProperty).getGradEta());
			variableEtaCLAsTransmissionCoefficientPanel.setNumber(((VariableEtaConfocalLensletArrays)surfaceProperty).getTransmissionCoefficient());
			setOptionalParameterPanelComponent(variableEtaCLAsPanel);
		}
	}

	public SurfaceProperty getSurfaceProperty()
	{
		SurfacePropertyType surfacePropertyType = surfacePropertyComboBox.getSurfacePropertyType();

		SurfaceProperty surfaceProperty = null;
		
		switch(surfacePropertyType)
		{
		case COLOURED:
			// return a shiny version of the colour
			surfaceProperty = new SurfaceColour(
					colourPanel.getDoubleColour(),
					DoubleColour.WHITE,	    // specular component; white = shiny
					shadowThrowingCheckBox.isSelected()	// shadow-throwing
				);
			break;
		case COLOUR_FILTER:
			surfaceProperty = new ColourFilter(
					colourPanel.getDoubleColour(),
					shadowThrowingCheckBox.isSelected()	// shadow-throwing
				);
			break;
		case COLOURED_GLOWING:
			surfaceProperty = new SurfaceColourLightSourceIndependent(colourPanel.getDoubleColour(), shadowThrowingCheckBox.isSelected());
			break;
		case COLOURED_TIME_DEPENDENT:
			surfaceProperty = new SurfaceColourTimeDependent(surfaceColourTimeDependentPanel.getNumber(), shadowThrowingCheckBox.isSelected());
			break;
		case CLAS:
			surfaceProperty = new ConfocalLensletArrays(
					CLAsEtaPanel.getNumber(),
					CLAsTransmissionCoefficientPanel.getNumber(),
					shadowThrowingCheckBox.isSelected()
				);
			break;
		case CYLINDRICAL_LENS_HOLOGRAM:
			surfaceProperty = new PhaseHologramOfCylindricalLens(
					cylindricalLensHologramFocalLengthPanel.getNumber(),	// focal length
					pointOnAxisVector3DPanel.getVector3D(),	// point on central line
					phaseGradientDirectionVector3DPanel.getVector3D(),	// phase-gradient direction
					cylindricalLensHologramTransmissionCoefficientPanel.getNumber(),	// transmission coefficient
					false,	// reflective
					shadowThrowingCheckBox.isSelected()
				);
			break;
		case CROSSED_LENTICULAR_ARRAYS_WITH_LINEAR_POWER:
			surfaceProperty = new PhaseHologramOfCrossedLinearPowerLenticularArrays(
					clawlpP0Panel.getVector3D(),	// p0
					clawlpUHatPanel.getVector3D(),	// uHat
					clawlpVHatPanel.getVector3D(),	// cHat
					clawlpDPduPanel.getNumber(),	// dPdu
					clawlpPeriodPanel.getNumber(),	// period
					SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT,	// throughputCoefficient
					false,	// reflective,
					shadowThrowingCheckBox.isSelected()	// shadowThrowing
				);
			break;
		case EATON_LENS:
			surfaceProperty = new EatonLensSurfaceAngleFormulation(
					// criticalAngleOfIncidencePanel.getNumber()*Math.PI/180,
					transmissionCoefficientPanel.getNumber(),
					shadowThrowingCheckBox.isSelected()
				);
			break;
		case FLIPPING:
			surfaceProperty = new RayFlipping(flipAxisAnglePanel.getNumber()*Math.PI/180, SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT, shadowThrowingCheckBox.isSelected());
			break;
		case GALILEO_TRANSFORM_INTERFACE:
			surfaceProperty = new GalileoTransformInterface(
					betaGalileoVector3DPanel.getVector3D(),
					(CoordinateSystemType)(betaGalileoBasisComboBox.getSelectedItem()),
					transmissionCoefficientGalileoPanel.getNumber(),
					shadowThrowingCheckBox.isSelected()
				);
			break;
		case GENERALISED_CLAS:
			Vector2D
				eta = etaPanel.getVector2D(),
				delta = deltaPanel.getVector2D();
			surfaceProperty = new GCLAsWithApertures(
					aVector3DPanel.getVector3D(),
					uVector3DPanel.getVector3D(),
					vVector3DPanel.getVector3D(),
					eta.x,
					eta.y,
//					etaUPanel.getNumber(),
//					etaVPanel.getNumber(),
					delta.x,
					delta.y,
//					deltaUPanel.getNumber(),
//					deltaVPanel.getNumber(),
					(GlobalOrLocalCoordinateSystemType)(gCLAsBasisComboBox.getSelectedItem()),
					gCLAsConstantTransmissionCoefficientPanel.getNumber(),
					// gCLAsCalculateGeometricalTransmissionCoefficientCheckBox.isSelected(),
					(GCLAsTransmissionCoefficientCalculationMethodType)(gCLAsTransmissionCoefficientMethodComboBox.getSelectedItem()),
					shadowThrowingCheckBox.isSelected(),
					gCLAsPixelSideLengthPanel.getNumber(),	// pixelSideLength
					gCLAsLambdaPanel.getNumber(),	// lambda
					gCLAsSimulateDiffractiveBlurCheckBox.isSelected(),	// simulateDiffractiveBlur
					gCLAsSimulateRayOffsetCheckBox.isSelected()	// simulateRayOffset
				);
			break;
		case GLENS_HOLOGRAM:
			surfaceProperty = new GlensSurface(
					opticalAxisPosVector3DPanel.getVector3D(),	// opticalAxisDirectionPos,
					pointOnGlensVector3DPanel.getVector3D(),
					meanFPanel.getNumber(),
					nodalPointFVector3DPanel.getVector3D(),	// nodalPoint,
					focalLengthNegFPanel.getNumber(),	// focalLengthNeg,
					focalLengthPosFPanel.getNumber(),	// focalLengthPos,
					glensTransmissionCoefficientPanel.getNumber(),	// transmissionCoefficient,
					true	// shadowThrowing
				);
			break;
		case IDEAL_THIN_LENS_SURFACE:
			surfaceProperty = new IdealThinLensSurface(
					opticalAxisVector3DPanel.getVector3D(),	// opticalAxisDirection
					principalPointVector3DPanel.getVector3D(),	// principalPoint
					focalLengthPanel.getNumber(),	// focalLength
					lensTransmissionCoefficientPanel.getNumber(),	// transmissionCoefficient
					true	// shadowThrowing
				);
			break;
		case IDEALISED_DOVE_PRISM_ARRAY:
			surfaceProperty = new IdealisedDovePrismArray(
					idpaDovePrism0CentrePanel.getVector3D(),	// dovePrism0Centre
					idpaInversionDirectionPanel.getVector3D(),	// inversionDirection
					idpaPeriodPanel.getNumber(),	// period
					idpaTransmissionCoefficientPanel.getNumber(),	// transmissionCoefficient
					true	// shadowThrowing
				);
			break;
		case LENSLET_ARRAY:
			surfaceProperty = new RectangularIdealThinLensletArray(
					lensletArrayFocalLengthPanel.getNumber(),	// focalLength
					lensletArrayPeriodPanel.getVector2D().x,	// xPeriod
					lensletArrayPeriodPanel.getVector2D().y,	// yPeriod
					lensletArrayOffsetPanel.getVector2D().x,	// xOffset
					lensletArrayOffsetPanel.getVector2D().y,	// yOffset
					lensletArraySimulateDiffractiveBlurCheckBox.isSelected(),	// simulatedDiffractiveBlur
					lensletArrayLambdaPanel.getNumber(),	// lambda
					lensletArrayTransmissionCoefficientPanel.getNumber(),	// throughputCoefficient
					true	// shadowThrowing
				);
			break;
		case LENTICULAR_ARRAY_WITH_LINEAR_POWER:
			surfaceProperty = new PhaseHologramOfLinearPowerLenticularArray(
					lawlpP0Panel.getVector3D(),	// p0
					lawlpUHatPanel.getVector3D(),	// uHat
					lawlpDPduPanel.getNumber(),	// dPdu
					lawlpPeriodPanel.getNumber(),	// period
					SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT,	// throughputCoefficient
					false,	// reflective,
					shadowThrowingCheckBox.isSelected()	// shadowThrowing
				);
			break;
		case LORENTZ_TRANSFORM_INTERFACE:
			surfaceProperty = new LorentzTransformInterface(
					betaVector3DPanel.getVector3D(),
					(CoordinateSystemType)(betaBasisComboBox.getSelectedItem()),
					transmissionCoefficientPanel.getNumber(),
					shadowThrowingCheckBox.isSelected()
				);
			break;
		case LUNEBURG_LENS:
			surfaceProperty = new LuneburgLensSurfaceAngleFormulation(
					// criticalAngleOfIncidencePanel.getNumber()*Math.PI/180,
					transmissionCoefficientPanel.getNumber(),
					shadowThrowingCheckBox.isSelected()
				);
			break;
		case METRIC_INTERFACE:
			surfaceProperty = new MetricInterface(
					insideMetricTensorPanel.getMatrix3D(),
					outsideMetricTensorPanel.getMatrix3D(),
					(GlobalOrLocalCoordinateSystemType)(metricTensorsBasisComboBox.getSelectedItem()),
					(RefractionType)(refractionTypeComboBox.getSelectedItem()),
					allowImaginaryOpticalPathLengthsCheckBox.isSelected(),
					transmissionCoefficientPanel.getNumber(),
					shadowThrowingCheckBox.isSelected()
				);
			break;
		case PHASE_CONJUGATING:
			surfaceProperty = new PhaseConjugating(
					isPhaseConjugatingSurfaceReflectiveCheckBox.isSelected()?SurfaceProperty.ReflectiveOrTransmissive.REFLECTIVE:SurfaceProperty.ReflectiveOrTransmissive.TRANSMISSIVE,
					phaseConjugatingSurfaceTransmissionCoefficientPanel.getNumber(),
					shadowThrowingCheckBox.isSelected()
				);
			break;
		case PHASE_FRONT:
			surfaceProperty = new LightRayFieldRepresentingPhaseFront(
					phaseFrontColourPanel.getDoubleColour(),
					MyMath.deg2rad(angularFuzzinessDegPanel.getNumber()),
					bidirectionalRaysCheckBox.isSelected()
				);
			break;
		case POINT2POINT_IMAGING:
			surfaceProperty = new Point2PointImagingPhaseHologram(
					point2pointImagingPoint1Panel.getVector3D(),
					point2pointImagingPoint2Panel.getVector3D(),
					SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT,
					point2pointImagingIsReflectiveCheckBox.isSelected(),
					shadowThrowingCheckBox.isSelected()
				);
			break;
		case PICTURE:
			Vector2D
				corner = pictureCorner.getVector2D(),
				size = pictureSize.getVector2D();
			pictureSurface.setxMin(corner.x);
			pictureSurface.setxMax(corner.x + size.x);
			pictureSurface.setyMin(corner.y);
			pictureSurface.setyMax(corner.y + size.y);
			if(pictureFileChanged)
			{
				pictureSurface.setPicture(pictureFile);
				pictureFileChanged = false;
			}
			pictureSurface.setTiled(pictureSurfaceTiledCheckbox.isSelected());
			surfaceProperty = pictureSurface;
			break;
		case PIXELLATION:
			surfaceProperty = new Pixellation(
					pixellationPixelSideLengthUPanel.getNumber(),	// pixelSideLength
					pixellationPixelSideLengthVPanel.getNumber(),	// pixelSideLength
					pixellationLambdaPanel.getNumber(),	// lambda
					pixellationSimulateDiffractiveBlurCheckBox.isSelected(),	// simulateDiffractiveBlur
					pixellationSimulateRayOffsetCheckBox.isSelected()	// simulateRayOffset
				);
			break;
		case RADIAL_LENTICULAR_ARRAY:
			surfaceProperty = new PhaseHologramOfRadialLenticularArray(
					rlapCentrePanel.getVector3D(),	// centre
					rlapFPanel.getNumber(),	// f
					rlapNPanel.getNumber(),	// n
					rlapD0Panel.getVector3D(),	// d0
					SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT,	// throughputCoefficient
					false,	// reflective
					shadowThrowingCheckBox.isSelected()	// shadowThrowing
				);
			break;
		case RAINBOW:
			surfaceProperty = new Rainbow(
					rainbowSaturationPanel.getNumber(),
					rainbowLightnessPanel.getNumber(),
					rainbowLightSourcePositionPanel.getVector3D()
				);
			break;
		case REFLECTIVE:
			surfaceProperty = new Reflective(SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT, shadowThrowingCheckBox.isSelected());
			break;
		case REFRACTIVE:
			surfaceProperty = new Refractive(refractiveIndexRatioPanel.getNumber(), SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT, shadowThrowingCheckBox.isSelected());
			break;
		case REFRACTIVE_COMPLEX:
			surfaceProperty = new RefractiveComplex(complexRefractiveIndexRatioPanel.getNumber(), SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT, shadowThrowingCheckBox.isSelected());
			break;
		case ROTATING:
			surfaceProperty = new RayRotating(rayRotationAnglePanel.getNumber()*Math.PI/180., SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT, shadowThrowingCheckBox.isSelected());
			break;
		case ROTATING_AROUND_ARBITRARY_AXIS_DIRECTION:
			surfaceProperty = new RayRotatingAboutArbitraryAxisDirection(
					rayRotationAngleArbitraryAxisDirectionPanel.getNumber()*Math.PI/180,	// rotation angle
					rayRotationAxisPanel.getVector3D(),	// rotation axis unit vector
					(GlobalOrLocalCoordinateSystemType)(rayRotationAxisDirectionBasisComboBox.getSelectedItem()),
					SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT, shadowThrowingCheckBox.isSelected()
				);
			break;
		case ROTATING_IN_PLANE_OF_INCIDENCE:
			surfaceProperty = new RayRotatingInPlaneOfIncidence(
					rayRotationAnglePanel.getNumber()*Math.PI/180.,	// rotationAngle
					SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT,	// transmissionCoefficient
					true	// shadowThrowing
				);
			break;
		case ROTATIONALLY_SYMMETRIC_PHASE_HOLOGRAM:
			surfaceProperty = new RotationallySymmetricPhaseHologram(
					rsphCentrePanel.getVector3D(),	// centre
					rsphBPanel.getNumber(),	// b
					rsphCPanel.getNumber(),	// c
					rsphSPanel.getNumber(),	// s
					rsphTPanel.getNumber(),	// t
					rsphSimulateHonestlyCheckBox.isSelected(),	// simulateHonestly
					rsphNPanel.getNumber(),	// n
					rsphD0Panel.getVector3D(),	// d0
					SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT,	// throughputCoefficient
					false,	// reflective
					shadowThrowingCheckBox.isSelected()	// shadowThrowing
				);
			break;
		case TELEPORTING:
			surfaceProperty = new Teleporting(
					teleportingParametersLine.getDestinationObject(),
					SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT,
					(TeleportationType)(teleportationTypeComboBox.getSelectedItem()),
					shadowThrowingCheckBox.isSelected()
				);
			break;
		case TILED:
			return surfaceTiling;
		case TRANSPARENT:
			surfaceProperty = new Transparent(transmissionCoefficientPanel.getNumber(), shadowThrowingCheckBox.isSelected());
			// return new Transparent(TRANSMISSION_COEFFICIENT);
			break;
		case TWO_SIDED:
			surfaceProperty = twoSidedSurface;
			break;
		case VARIABLE_ETA_CLAS:
			surfaceProperty = new VariableEtaConfocalLensletArrays(
					variableEtaCLAsEtaAtPPanel.getNumber(),	// inwardsEtaAtP
					variableEtaPointPPanel.getVector3D(),	// pointP
					variableEtaGradEtaPanel.getVector3D(),	// dRdEta
					variableEtaCLAsTransmissionCoefficientPanel.getNumber(),	// transmissionCoefficient
					shadowThrowingCheckBox.isSelected()	// shadowThrowing
				);
			break;
		}

		if(surfaceProperty != null)
		{
			if(surfaceProperty instanceof SurfacePropertyWithControllableShadow)
			{
				((SurfacePropertyWithControllableShadow)surfaceProperty).setShadowThrowing(shadowThrowingCheckBox.isSelected());
			}
		}

		return surfaceProperty;
	}

	/**
	 * A little inner class describing the combo box for selecting a surface-property class
	 */
	class SurfacePropertyComboBox extends JComboBox<SurfacePropertyType> implements ActionListener
	{
		private static final long serialVersionUID = 7398035768553054607L;

		public SurfacePropertyComboBox()
		{
			super(SurfacePropertyType.values());

			addActionListener(this);
		}

		public void setSurfacePropertyType(SurfacePropertyType surfaceProperty)
		{
			setSelectedItem(surfaceProperty);
		}

		public SurfacePropertyType getSurfacePropertyType()
		{
			return (SurfacePropertyType)getSelectedItem();
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			SurfacePropertyType surfacePropertyType = getSurfacePropertyType();

			switch(surfacePropertyType)
			{
			case COLOURED:
				setOptionalParameterPanelComponent(colourPanel);
				break;
			case COLOUR_FILTER:
				setOptionalParameterPanelComponent(colourPanel);
				break;
			case COLOURED_GLOWING:
				setOptionalParameterPanelComponent(colourPanel);
				break;
			case COLOURED_TIME_DEPENDENT:
				setOptionalParameterPanelComponent(surfaceColourTimeDependentPanel);
				break;
			case CLAS:
				setOptionalParameterPanelComponent(CLAsPanel);
				break;
			case CROSSED_LENTICULAR_ARRAYS_WITH_LINEAR_POWER:
				setOptionalParameterPanelComponent(crossedLenticularArraysWithLinearPowerPanel);
				break;
			case CYLINDRICAL_LENS_HOLOGRAM:
				setOptionalParameterPanelComponent(cylindricalLensHologramPanel);
				break;
			case EATON_LENS:
				// setOptionalParameterPanelComponent(EatonLuneburgLensSurfacePanel);
				setOptionalParameterPanelComponent(transmissionCoefficientPanel);
				break;
			case FLIPPING:
				setOptionalParameterPanelComponent(flipAxisAnglePanel);
				break;
			case GALILEO_TRANSFORM_INTERFACE:
				setOptionalParameterPanelComponent(galileoTransformInterfacePanel);
				break;
			case GENERALISED_CLAS:
				setOptionalParameterPanelComponent(gCLAsPanel);
				break;
			case GLENS_HOLOGRAM:
				setOptionalParameterPanelComponent(glensHologramPanel);
				break;
			case IDEAL_THIN_LENS_SURFACE:
				setOptionalParameterPanelComponent(lensHologramPanel);
				break;
			case IDEALISED_DOVE_PRISM_ARRAY:
				setOptionalParameterPanelComponent(idealisedDovePrismArrayPanel);
				break;
			case LENSLET_ARRAY:
				setOptionalParameterPanelComponent(lensletArrayPanel);
				break;
			case LENTICULAR_ARRAY_WITH_LINEAR_POWER:
				setOptionalParameterPanelComponent(lenticularArrayWithLinearPowerPanel);
				break;
			case LORENTZ_TRANSFORM_INTERFACE:
				setOptionalParameterPanelComponent(lorentzTransformInterfacePanel);
				break;
			case LUNEBURG_LENS:
				// setOptionalParameterPanelComponent(EatonLuneburgLensSurfacePanel);
				setOptionalParameterPanelComponent(transmissionCoefficientPanel);
				break;
			case METRIC_INTERFACE:
				setOptionalParameterPanelComponent(metricInterfacePanel);
				break;
			case PHASE_CONJUGATING:
				setOptionalParameterPanelComponent(phaseConjugatingPanel);
				break;
			case PHASE_FRONT:
				setOptionalParameterPanelComponent(phaseFrontPanel);
				break;
			case POINT2POINT_IMAGING:
				setOptionalParameterPanelComponent(point2pointImagingPanel);
				break;
			case PICTURE:
				setOptionalParameterPanelComponent(picturePanel);
				break;
			case PIXELLATION:
				setOptionalParameterPanelComponent(pixellationPanel);
				break;
			case RADIAL_LENTICULAR_ARRAY:
				setOptionalParameterPanelComponent(phaseHologramOfRadialLenticularArrayPanel);
				break;
			case RAINBOW:
				setOptionalParameterPanelComponent(rainbowPanel);
				break;
			case REFLECTIVE:
				setOptionalParameterPanelComponent(disabledParametersPanel);
				break;
			case REFRACTIVE:
				setOptionalParameterPanelComponent(refractiveIndexRatioPanel);
				break;
			case REFRACTIVE_COMPLEX:
				setOptionalParameterPanelComponent(complexRefractiveIndexRatioPanel);
				break;
			case ROTATIONALLY_SYMMETRIC_PHASE_HOLOGRAM:
				setOptionalParameterPanelComponent(rotationallySymmetricPhaseHologramPanel);
				break;
			case ROTATING:
			case ROTATING_IN_PLANE_OF_INCIDENCE:
				setOptionalParameterPanelComponent(rayRotationAnglePanel);
				break;
			case ROTATING_AROUND_ARBITRARY_AXIS_DIRECTION:
				setOptionalParameterPanelComponent(rayRotationAroundArbitraryAxisDirectionPanel);
				break;
			case TELEPORTING:
				setOptionalParameterPanelComponent(teleportingParametersPanel);
				break;
			case TILED:
				setOptionalParameterPanelComponent(tilingParametersButton);
				break;
			case TRANSPARENT:
				setOptionalParameterPanelComponent(transmissionCoefficientPanel);
				// setOptionalParameterPanelComponent(disabledParametersPanel);
				break;
			case TWO_SIDED:
				setOptionalParameterPanelComponent(twoSidedParametersButton);
				break;
			case VARIABLE_ETA_CLAS:
				setOptionalParameterPanelComponent(variableEtaCLAsPanel);
				break;
			}
			
			SurfaceProperty surfaceProperty = getSurfaceProperty();
			shadowThrowingCheckBox.setVisible(surfaceProperty instanceof SurfacePropertyWithControllableShadow);
		}
	}
	
	/**
	 * ensures that the panels for point 1 and point 2 in the point-to-point-imaging surface are labelled correctly
	 * @param isReflective
	 */
	private void updatePoint2pointImagingPanel(boolean isReflective)
	{
		if(isReflective)
		{
			point2pointImagingPoint1Label.setText("Object position");
			point2pointImagingPoint2Label.setText("Image position");
		}
		else
		{
			point2pointImagingPoint1Label.setText("Inside-space position");
			point2pointImagingPoint2Label.setText("Outside-space position");
		}

		point2pointImagingPanel.revalidate();
	}


	@Override
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() == gCLAsTransmissionCoefficientMethodComboBox)
		{
			gCLAsConstantTransmissionCoefficientPanel.setEnabled(
					(GCLAsTransmissionCoefficientCalculationMethodType)(gCLAsTransmissionCoefficientMethodComboBox.getSelectedItem())
					== GCLAsTransmissionCoefficientCalculationMethodType.CONSTANT
				);
		}
		else if(e.getActionCommand().equals(TILING_PARAMS_BUTTON_TEXT))
		{
			EditableSurfaceTiling edit = surfaceTiling;
			iPanel.addFrontComponent(edit, "Edit tiling");
			edit.setValuesInEditPanel();
		}
		else if(e.getActionCommand().equals(TWO_SIDED_PARAMS_BUTTON_TEXT))
		{
			EditableTwoSidedSurface edit = twoSidedSurface;
			iPanel.addFrontComponent(edit, "Edit two-sided surface");
			edit.setValuesInEditPanel();
		}
		else if(e.getSource() == choosePictureFileButton)
		{
			//Create a file chooser
			JFileChooser fc = new JFileChooser();

			// file chooser info: http://docs.oracle.com/javase/tutorial/uiswing/components/filechooser.html
			int returnVal = fc.showOpenDialog(this);

	        if (returnVal == JFileChooser.APPROVE_OPTION)
	        {
	        	pictureFile = fc.getSelectedFile();
				pictureFileNameField.setText(pictureFile.getName());
				pictureFileChanged = true;
	        }
		}
		else if(e.getSource() == point2pointImagingIsReflectiveCheckBox)
		{
			updatePoint2pointImagingPanel(point2pointImagingIsReflectiveCheckBox.isSelected());
		}
	}

	public SceneObject getScene()
	{
		return scene;
	}

	public void setScene(SceneObject scene)
	{
		this.scene = scene;
	}
}