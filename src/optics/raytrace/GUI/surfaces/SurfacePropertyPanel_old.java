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
import math.Vector2D;
import math.Vector3D;
import optics.DoubleColour;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.SurfaceProperty;
import optics.raytrace.core.SurfacePropertyPrimitive;
import optics.raytrace.core.SurfacePropertyWithControllableShadow;
import optics.raytrace.surfaces.ConfocalLensletArrays;
import optics.raytrace.surfaces.GalileoTransformInterface;
import optics.raytrace.surfaces.GeneralisedConfocalLensletArrays;
import optics.raytrace.surfaces.GlensHologram;
import optics.raytrace.surfaces.EatonLensSurfaceAngleFormulation;
import optics.raytrace.surfaces.LorentzTransformInterface;
import optics.raytrace.surfaces.LuneburgLensSurfaceAngleFormulation;
import optics.raytrace.surfaces.MetricInterface;
import optics.raytrace.surfaces.MetricInterface.RefractionType;
import optics.raytrace.surfaces.PhaseConjugating;
import optics.raytrace.surfaces.Point2PointImaging;
import optics.raytrace.surfaces.RayFlipping;
import optics.raytrace.surfaces.RayRotating;
import optics.raytrace.surfaces.Reflective;
import optics.raytrace.surfaces.Refractive;
import optics.raytrace.surfaces.RefractiveComplex;
import optics.raytrace.surfaces.SurfaceColour;
import optics.raytrace.surfaces.SurfaceColourLightSourceIndependent;
import optics.raytrace.surfaces.Teleporting;
import optics.raytrace.surfaces.Teleporting.TeleportationType;
import optics.raytrace.surfaces.Transparent;
import optics.raytrace.utility.Coordinates.CoordinateSystemType;
import optics.raytrace.utility.Coordinates.GlobalOrLocalCoordinateSystemType;
import optics.raytrace.GUI.core.*;
import optics.raytrace.GUI.lowLevel.GUIBitsAndBobs;
import optics.raytrace.GUI.lowLevel.LabelledComplexPanel;
import optics.raytrace.GUI.lowLevel.LabelledDoubleColourPanel;
import optics.raytrace.GUI.lowLevel.LabelledDoublePanel;
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
		COLOURED_GLOWING("Coloured (glowing)"),
		CONFOCAL_LENSLET_ARRAYS("Confocal lenslet arrays"),
		EATON_LENS("Eaton-lens surface"),
		FLIPPING("Flipping"),
		GALILEO_TRANSFORM_INTERFACE("Galileo-transform interface"),
		GENERALISED_CONFOCAL_LENSLET_ARRAYS("Generalised confocal lenslet arrays"),
		GLENS_HOLOGRAM("Glens hologram"),
		LORENTZ_TRANSFORM_INTERFACE("Lorentz-transform interface"),
		LUNEBURG_LENS("Luneburg-lens surface"),
		METRIC_INTERFACE("Metric interface"),
		PHASE_CONJUGATING("Phase-conjugating"),
		PICTURE("Picture"),
		POINT2POINT_IMAGING("Point-to-point imaging hologram"),
		REFLECTIVE("Reflective"),
		REFRACTIVE("Refractive"),
		REFRACTIVE_COMPLEX("Refractive (complex)"),
		ROTATING("Rotating"),
		TELEPORTING("Teleporting"),
		TILED("Tiled"),
		TRANSPARENT("Transparent"),
		TWO_SIDED("Two-sided");
		
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

	// generalised confocal lenslet arrays panel
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
	private LabelledDoublePanel
		gCLAsTransmissionCoefficientPanel;
	private JCheckBox
		gCLAsCalculateGeometricalTransmissionCoefficientCheckBox;

	// glens-hologram panel
	private JPanel glensHologramPanel;
	private LabelledVector3DPanel
		nodalPointVector3DPanel,
		opticalAxisPosVector3DPanel;
	private LabelledDoublePanel
		focalLengthNegPanel,
		focalLengthPosPanel,
		glensTransmissionCoefficientPanel;

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

	// other panels
	private LabelledDoubleColourPanel colourPanel;
	private LabelledDoublePanel
		transmissionCoefficientPanel,
		transmissionCoefficientGalileoPanel,
		phaseConjugatingSurfaceTransmissionCoefficientPanel,
		refractiveIndexRatioPanel,
		rayRotationAnglePanel,
		flipAxisAnglePanel;
		// criticalAngleOfIncidencePanel;
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
	private JCheckBox shadowThrowingCheckBox;
	private IPanel iPanel;
	
	private SceneObject scene;

	/**
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
		
		transmissionCoefficientPanel = new LabelledDoublePanel("Transmission coefficient");
		transmissionCoefficientPanel.setNumber(SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT);
		
		refractiveIndexRatioPanel = new LabelledDoublePanel("Refractive-index ratio (n_inside / n_outside)");
		refractiveIndexRatioPanel.setNumber(1.0);
		
		complexRefractiveIndexRatioPanel = new LabelledComplexPanel("Refractive-index ratio (n_inside / n_outside)");
		complexRefractiveIndexRatioPanel.setNumber(new Complex(1, 0));
		
		rayRotationAnglePanel = new LabelledDoublePanel("Rotation angle (degrees)");
		rayRotationAnglePanel.setNumber(0.0);
		
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
		gCLAsCalculateGeometricalTransmissionCoefficientCheckBox = new JCheckBox("Calculate geometrical transmission coefficient");
		gCLAsCalculateGeometricalTransmissionCoefficientCheckBox.setToolTipText("Calculate the geometrical transmission coefficient?");
		gCLAsCalculateGeometricalTransmissionCoefficientCheckBox.setSelected(true);
		gCLAsPanel.add(gCLAsCalculateGeometricalTransmissionCoefficientCheckBox, "wrap");
		gCLAsTransmissionCoefficientPanel = new LabelledDoublePanel("Transmission coefficient");
		gCLAsTransmissionCoefficientPanel.setNumber(SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT);
		gCLAsPanel.add(gCLAsTransmissionCoefficientPanel);
		gCLAsPanel.validate();
		
		glensHologramPanel = new JPanel();
		glensHologramPanel.setLayout(new MigLayout("insets 0"));
		nodalPointVector3DPanel = new LabelledVector3DPanel("Nodal point, N");
		nodalPointVector3DPanel.setToolTipText("This vector defines the glens's nodal point, N.");
		nodalPointVector3DPanel.setVector3D(new Vector3D(0, 0, 0));
		glensHologramPanel.add(nodalPointVector3DPanel, "wrap");
		opticalAxisPosVector3DPanel = new LabelledVector3DPanel("Optical-axis direction, +ve direction");
		opticalAxisPosVector3DPanel.setToolTipText("This vector defines +ve optical-axis direction.");
		opticalAxisPosVector3DPanel.setVector3D(new Vector3D(0, 0, 1));
		glensHologramPanel.add(opticalAxisPosVector3DPanel, "wrap");
		focalLengthNegPanel = new LabelledDoublePanel("Focal length, -ve space");
		focalLengthNegPanel.setNumber(SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT);
		glensHologramPanel.add(focalLengthNegPanel);
		focalLengthPosPanel = new LabelledDoublePanel("Focal length, +ve space");
		focalLengthPosPanel.setNumber(SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT);
		glensHologramPanel.add(focalLengthPosPanel);
		glensTransmissionCoefficientPanel = new LabelledDoublePanel("Transmission coefficient");
		glensTransmissionCoefficientPanel.setNumber(SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT);
		glensHologramPanel.add(glensTransmissionCoefficientPanel);
		glensHologramPanel.validate();

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
		else if(surfaceProperty instanceof SurfaceColourLightSourceIndependent)
		{
			surfacePropertyComboBox.setSurfacePropertyType(SurfacePropertyType.COLOURED_GLOWING);
			colourPanel.setDoubleColour(((SurfaceColourLightSourceIndependent)surfaceProperty).getColour());
			setOptionalParameterPanelComponent(colourPanel);
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
		else if(surfaceProperty instanceof RayFlipping)
		{
			surfacePropertyComboBox.setSurfacePropertyType(SurfacePropertyType.FLIPPING);
			flipAxisAnglePanel.setNumber(((RayFlipping)surfaceProperty).getFlipAxisAngle()*180/Math.PI);
			setOptionalParameterPanelComponent(flipAxisAnglePanel);
		}
		else if(surfaceProperty instanceof EatonLensSurfaceAngleFormulation)
		{
			surfacePropertyComboBox.setSurfacePropertyType(SurfacePropertyType.EATON_LENS);
			// criticalAngleOfIncidencePanel.setNumber(((EatonLensSurfaceAngleFormulation)surfaceProperty).getCriticalAngle()*180/Math.PI);
			transmissionCoefficientPanel.setNumber(((EatonLensSurfaceAngleFormulation)surfaceProperty).getTransmissionCoefficient());
			// setOptionalParameterPanelComponent(EatonLuneburgLensSurfacePanel);
			setOptionalParameterPanelComponent(transmissionCoefficientPanel);
		}
		else if(surfaceProperty instanceof GalileoTransformInterface)
		{
			surfacePropertyComboBox.setSurfacePropertyType(SurfacePropertyType.GALILEO_TRANSFORM_INTERFACE);
			betaGalileoVector3DPanel.setVector3D(((GalileoTransformInterface)surfaceProperty).getBeta());
			betaGalileoBasisComboBox.setSelectedItem(((GalileoTransformInterface)surfaceProperty).getBasis());
			transmissionCoefficientGalileoPanel.setNumber(((GalileoTransformInterface)surfaceProperty).getTransmissionCoefficient());
			setOptionalParameterPanelComponent(galileoTransformInterfacePanel);
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
		else if(surfaceProperty instanceof ConfocalLensletArrays)
		{
			ConfocalLensletArrays c = (ConfocalLensletArrays)surfaceProperty;
			surfacePropertyComboBox.setSurfacePropertyType(SurfacePropertyType.CONFOCAL_LENSLET_ARRAYS);
			CLAsEtaPanel.setNumber(c.getEta());
			CLAsTransmissionCoefficientPanel.setNumber(c.getTransmissionCoefficient());
			setOptionalParameterPanelComponent(CLAsPanel);
		}
		else if(surfaceProperty instanceof GeneralisedConfocalLensletArrays)
		{
			GeneralisedConfocalLensletArrays c = (GeneralisedConfocalLensletArrays)surfaceProperty;
			surfacePropertyComboBox.setSurfacePropertyType(SurfacePropertyType.GENERALISED_CONFOCAL_LENSLET_ARRAYS);
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
			gCLAsTransmissionCoefficientPanel.setNumber(c.getTransmissionCoefficient());
			gCLAsCalculateGeometricalTransmissionCoefficientCheckBox.setSelected(c.isCalculateGeometricalTransmissionCoefficient());
			setOptionalParameterPanelComponent(gCLAsPanel);
		}
		else if(surfaceProperty instanceof GlensHologram)
		{
			GlensHologram c = (GlensHologram)surfaceProperty;
			surfacePropertyComboBox.setSurfacePropertyType(SurfacePropertyType.GLENS_HOLOGRAM);
			nodalPointVector3DPanel.setVector3D(c.getNodalPoint());
			opticalAxisPosVector3DPanel.setVector3D(c.getOpticalAxisDirectionPos());
			focalLengthNegPanel.setNumber(c.getFocalLengthNeg());
			focalLengthPosPanel.setNumber(c.getFocalLengthPos());
			glensTransmissionCoefficientPanel.setNumber(c.getTransmissionCoefficient());
			setOptionalParameterPanelComponent(glensHologramPanel);
		}
		else if(surfaceProperty instanceof Teleporting)
		{
			surfacePropertyComboBox.setSurfacePropertyType(SurfacePropertyType.TELEPORTING);
			teleportingParametersLine.setDestinationObject(((Teleporting)surfaceProperty).getDestinationObject());
			teleportationTypeComboBox.setSelectedItem(((Teleporting)surfaceProperty).getTeleportationType());
			setOptionalParameterPanelComponent(teleportingParametersPanel);
		}
		else if(surfaceProperty instanceof PhaseConjugating)
		{
			surfacePropertyComboBox.setSurfacePropertyType(SurfacePropertyType.PHASE_CONJUGATING);
			boolean isReflective = (((PhaseConjugating)surfaceProperty).getReflectiveOrTransmissive() == SurfaceProperty.ReflectiveOrTransmissive.REFLECTIVE);
			isPhaseConjugatingSurfaceReflectiveCheckBox.setSelected(isReflective);
			phaseConjugatingSurfaceTransmissionCoefficientPanel.setNumber(((PhaseConjugating)surfaceProperty).getTransmissionCoefficient());
			setOptionalParameterPanelComponent(phaseConjugatingPanel);
		}
		else if(surfaceProperty instanceof Point2PointImaging)
		{
			surfacePropertyComboBox.setSurfacePropertyType(SurfacePropertyType.POINT2POINT_IMAGING);
			point2pointImagingPoint1Panel.setVector3D(((Point2PointImaging)surfaceProperty).getInsideSpacePoint());
			point2pointImagingPoint2Panel.setVector3D(((Point2PointImaging)surfaceProperty).getOutsideSpacePoint());
			boolean isReflective = ((Point2PointImaging)surfaceProperty).isReflective();
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
		}
		else if(surfaceProperty instanceof EditableTwoSidedSurface)
		{
			surfacePropertyComboBox.setSurfacePropertyType(SurfacePropertyType.TWO_SIDED);
			twoSidedSurface = (EditableTwoSidedSurface)surfaceProperty;
//			tilingParametersPanel.setPeriod1(((SurfaceTiling)surfaceProperty).getWidthU());
//			tilingParametersPanel.setPeriod2(((SurfaceTiling)surfaceProperty).getWidthV());
			setOptionalParameterPanelComponent(twoSidedParametersButton);
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
		case COLOURED_GLOWING:
			surfaceProperty = new SurfaceColourLightSourceIndependent(colourPanel.getDoubleColour(), shadowThrowingCheckBox.isSelected());
			break;
		case TILED:
			return surfaceTiling;
		case TRANSPARENT:
			surfaceProperty = new Transparent(transmissionCoefficientPanel.getNumber(), shadowThrowingCheckBox.isSelected());
			// return new Transparent(TRANSMISSION_COEFFICIENT);
			break;
		case TELEPORTING:
			surfaceProperty = new Teleporting(
					teleportingParametersLine.getDestinationObject(),
					SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT,
					(TeleportationType)(teleportationTypeComboBox.getSelectedItem()),
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
		case POINT2POINT_IMAGING:
			surfaceProperty = new Point2PointImaging(
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
		case TWO_SIDED:
			surfaceProperty = twoSidedSurface;
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
			case COLOURED_GLOWING:
				setOptionalParameterPanelComponent(colourPanel);
				break;
			case CONFOCAL_LENSLET_ARRAYS:
				setOptionalParameterPanelComponent(CLAsPanel);
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
			case GENERALISED_CONFOCAL_LENSLET_ARRAYS:
				setOptionalParameterPanelComponent(gCLAsPanel);
				break;
			case GLENS_HOLOGRAM:
				setOptionalParameterPanelComponent(glensHologramPanel);
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
			case PICTURE:
				setOptionalParameterPanelComponent(picturePanel);
				break;
			case POINT2POINT_IMAGING:
				setOptionalParameterPanelComponent(point2pointImagingPanel);
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
			case ROTATING:
				setOptionalParameterPanelComponent(rayRotationAnglePanel);
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
		if(e.getActionCommand().equals(TILING_PARAMS_BUTTON_TEXT))
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