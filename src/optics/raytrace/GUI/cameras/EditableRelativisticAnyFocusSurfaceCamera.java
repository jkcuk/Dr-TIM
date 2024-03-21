package optics.raytrace.GUI.cameras;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.TitledBorder;

import net.miginfocom.swing.MigLayout;
import math.*;
import math.SpaceTimeTransformation.SpaceTimeTransformationType;
import optics.raytrace.GUI.cameras.shutterModels.ShutterModelPanel;
import optics.raytrace.GUI.core.*;
import optics.raytrace.GUI.lowLevel.ApertureSizeType;
import optics.raytrace.GUI.lowLevel.BlurPanel;
import optics.raytrace.GUI.lowLevel.ButtonsPanel;
import optics.raytrace.GUI.lowLevel.GUIBitsAndBobs;
import optics.raytrace.GUI.lowLevel.LabelledComboBoxPanel;
import optics.raytrace.GUI.lowLevel.LabelledDoublePanel;
import optics.raytrace.GUI.lowLevel.LabelledQualityComboBox;
import optics.raytrace.GUI.lowLevel.LabelledVector3DPanel;
import optics.raytrace.GUI.sceneObjects.EditableSceneObjectCollection;
import optics.raytrace.cameras.RelativisticAnyFocusSurfaceCamera;
import optics.raytrace.cameras.shutterModels.AperturePlaneShutterModel;
import optics.raytrace.cameras.shutterModels.LensType;
import optics.raytrace.core.Camera;
import optics.raytrace.core.Ray;
import optics.raytrace.core.SceneObject;

/**
 * A camera that can focus on arbitrary surfaces (and not just planes, like normal cameras).
 */
public class EditableRelativisticAnyFocusSurfaceCamera extends RelativisticAnyFocusSurfaceCamera
implements CameraWithRayForImagePixel, Camera, IPanelComponent, ActionListener, NeedsSceneSetBeforeEditing
{
	private static final long serialVersionUID = -921319043764488716L;

	// the geometry
	private Vector3D apertureCentre;
	private Vector3D viewDirection;
	private Vector3D topDirection;
	private double horizontalViewAngle;	// in degrees

	private QualityType antiAliasingQuality, blurQuality;
	private ApertureSizeType apertureSize;
	private int imagePixelsHorizontal, imagePixelsVertical;	// note that the number of detector pixels is higher for high anti-aliasing quality

	private JPanel editPanel;
	private LabelledQualityComboBox antiAliasingQualityPanel;
	private BlurPanel blurPanel;
	private TitledBorder titledBorder;
	private LabelledVector3DPanel apertureCentrePanel, viewDirectionPanel, topDirectionPanel;
	private LabelledVector3DPanel betaPanel;	// the speed of the camera, in units of c, relative to the scene
	private LabelledDoublePanel horizontalViewAnglePanel;
	private JComboBox<ExposureCompensationType> exposureCompensationComboBox;
	private ShutterModelPanel shutterModelPanel;
	private JComboBox<SpaceTimeTransformationType> transformComboBox;
	// private JComboBox<LensType> lensTypeComboBox;
	// private LabelledComboBoxPanel lensTypeComboBoxPanel;
	// private LabelledDoublePanel detectorDistancePanel;
	
	/**
	 * the component to revalidate and repaint when the size of this panel changes
	 */
	private IPanel iPanel;

	private EditableSceneObjectCollection scene;

	/**
	 * Specify exactly what the curved-focus camera contains.
	 */
	public EditableRelativisticAnyFocusSurfaceCamera(
			String name,
			Vector3D apertureCentre,
			Vector3D centreOfView,
			Vector3D horizontalSpanVector, Vector3D verticalSpanVector,
			SpaceTimeTransformationType spaceTimeTransformationType,
			Vector3D beta,
			int imagePixelsHorizontal, int imagePixelsVertical, 
			ExposureCompensationType exposureCompensation,
			int maxTraceLevel,
			SceneObject focusScene,
			SceneObject cameraFrameScene,
			ApertureSizeType apertureSize,
			QualityType blurQuality,
			QualityType antiAliasingQuality
	)
	{
		super(	name,
				apertureCentre,	// pinhole position is aperture Centre
				centreOfView,
				horizontalSpanVector, verticalSpanVector,
				spaceTimeTransformationType,
				beta,
				imagePixelsHorizontal, imagePixelsVertical,
				exposureCompensation,
				maxTraceLevel,
				focusScene,
				cameraFrameScene,	// scene in camera frame
				new AperturePlaneShutterModel(),
				// 1.,	// default distance detector sits behind entrance pupil
				0.,	// placeholder aperture radius
				1	// placeholder rays per pixel
		);
		this.imagePixelsHorizontal = imagePixelsHorizontal;
		this.imagePixelsVertical = imagePixelsVertical;
		this.apertureSize = apertureSize;
		setAntiAliasingQuality(antiAliasingQuality);	// also sets detectorPixelsHorizontal and detectorPixelsVertical
		setBlur(apertureSize, blurQuality);
	}
	
	/**
	 * Constructor that finds a suitable vertical span vector by itself
	 * @param name
	 * @param apertureCentre
	 * @param centreOfView
	 * @param horizontalSpanVector
	 * @param imagePixelsHorizontal
	 * @param imagePixelsVertical
	 * @param exposureCompensation
	 * @param maxTraceLevel
	 * @param focusScene
	 * @param apertureSize
	 * @param blurQuality
	 * @param antiAliasingQuality
	 */
	public EditableRelativisticAnyFocusSurfaceCamera(
			String name,
			Vector3D apertureCentre,
			Vector3D centreOfView,
			Vector3D horizontalSpanVector,
			Vector3D beta,
			int imagePixelsHorizontal, int imagePixelsVertical, 
			ExposureCompensationType exposureCompensation,
			int maxTraceLevel,
			SceneObject focusScene,
			SceneObject cameraFrameScene,
			ApertureSizeType apertureSize,
			QualityType blurQuality,
			QualityType antiAliasingQuality
		)
	{
		this(	name,
				apertureCentre,
				centreOfView,
				horizontalSpanVector,
				Vector3D.crossProduct(
						horizontalSpanVector,
						Vector3D.difference(centreOfView, apertureCentre)
					).getWithLength(horizontalSpanVector.getLength() * imagePixelsVertical / imagePixelsHorizontal),
				SpaceTimeTransformationType.LORENTZ_TRANSFORMATION,
				beta,
				imagePixelsHorizontal, imagePixelsVertical,
				exposureCompensation,
				maxTraceLevel,
				focusScene,
				cameraFrameScene,
				apertureSize,
				blurQuality,
				antiAliasingQuality
			);
	}

	/**
	 * Constructor in terms of the view direction and the view angle.
	 * This is the constructor TIMInteractiveBits now uses.
	 * @param name
	 * @param apertureCentre
	 * @param viewDirection
	 * @param topDirection
	 * @param horizontalViewAngle
	 * @param imagePixelsHorizontal
	 * @param imagePixelsVertical
	 * @param exposureCompensation
	 * @param maxTraceLevel
	 * @param focusScene
	 * @param apertureSize
	 * @param blurQuality
	 * @param antiAliasingQuality
	 */
	public EditableRelativisticAnyFocusSurfaceCamera(
			String name,
			Vector3D apertureCentre,
			Vector3D viewDirection,
			Vector3D topDirection,
			double horizontalViewAngle,
			SpaceTimeTransformationType spaceTimeTransformationType,
			Vector3D beta,
			int imagePixelsHorizontal, int imagePixelsVertical, 
			ExposureCompensationType exposureCompensation,
			int maxTraceLevel,
			SceneObject focusScene,
			SceneObject cameraFrameScene,
			ApertureSizeType apertureSize,
			QualityType blurQuality,
			QualityType antiAliasingQuality
		)
	{
		this(	name,
				apertureCentre,
				calculateCentreOfView(apertureCentre, viewDirection),
				calculateHorizontalSpanVector(viewDirection, topDirection, horizontalViewAngle),
				calculateVerticalSpanVector(viewDirection, topDirection, horizontalViewAngle, imagePixelsHorizontal, imagePixelsVertical),
				spaceTimeTransformationType,
				beta,
				imagePixelsHorizontal, imagePixelsVertical,
				exposureCompensation,
				maxTraceLevel,
				focusScene,
				cameraFrameScene,
				apertureSize,
				blurQuality,
				antiAliasingQuality
			);
		// take a note of the geometry parameters
		this.apertureCentre = apertureCentre;
		this.viewDirection = viewDirection;
		this.topDirection = topDirection;
		this.horizontalViewAngle = horizontalViewAngle;
	}
	
	public EditableRelativisticAnyFocusSurfaceCamera(
			String name,
			Vector3D apertureCentre,
			Vector3D viewDirection,
			Vector3D topDirection,
			double horizontalViewAngle,
			Vector3D beta,
			int imagePixelsHorizontal, int imagePixelsVertical, 
			ExposureCompensationType exposureCompensation,
			int maxTraceLevel,
			SceneObject focusScene,
			SceneObject cameraFrameScene,
			ApertureSizeType apertureSize,
			QualityType blurQuality,
			QualityType antiAliasingQuality
		)
	{
		this(
				name,
				apertureCentre,
				viewDirection,
				topDirection,
				horizontalViewAngle,
				SpaceTimeTransformationType.LORENTZ_TRANSFORMATION,
				beta,
				imagePixelsHorizontal,
				imagePixelsVertical,
				exposureCompensation,
				maxTraceLevel,
				focusScene,
				cameraFrameScene,
				apertureSize,
				blurQuality,
				antiAliasingQuality
				);
	}
	
	private static Vector3D calculateCentreOfView(
			Vector3D apertureCentre1,
			Vector3D viewDirection1
		)
	{
		return Vector3D.sum(apertureCentre1, viewDirection1.getNormalised());
	}
	
//	private static Vector3D calculateHorizontalSpanVector(
//			Vector3D viewDirection1,
//			Vector3D rightDirection1,
//			double horizontalViewAngle1
//		)
//	{
//		return rightDirection1.getPartPerpendicularTo(viewDirection1).getWithLength(2*Math.tan(MyMath.deg2rad(horizontalViewAngle1)/2.));
//	}

	public static Vector3D calculateVerticalSpanVector(
			Vector3D viewDirection1,
			Vector3D topDirection1,
			double horizontalViewAngle1,
			int imagePixelsHorizontal1, int imagePixelsVertical1
		)
	{
		return topDirection1.getPartPerpendicularTo(viewDirection1).getWithLength(
				-2*Math.tan(MyMath.deg2rad(horizontalViewAngle1)/2.) * 
				imagePixelsVertical1 / imagePixelsHorizontal1
			);
	}

//	private static Vector3D calculateVerticalSpanVector(
//			Vector3D viewDirection1,
//			Vector3D rightDirection1,
//			double horizontalViewAngle1,
//			int imagePixelsHorizontal1, int imagePixelsVertical1
//		)
//	{
//		return Vector3D.crossProduct(rightDirection1, viewDirection1).getWithLength(2*Math.tan(MyMath.deg2rad(horizontalViewAngle1)/2.) * (double)imagePixelsVertical1 / (double)imagePixelsHorizontal1);
//	}

	public static Vector3D calculateHorizontalSpanVector(
			Vector3D viewDirection1,
			Vector3D topDirection1,
			double horizontalViewAngle1
		)
	{
		return Vector3D.crossProduct(topDirection1, viewDirection1).getWithLength(2*Math.tan(MyMath.deg2rad(horizontalViewAngle1)/2.));
	}

	/**
	 * @param apertureCentre
	 * @param viewDirection
	 * @param rightDirection
	 * @param horizontalViewAngle	in degrees
	 * @param imagePixelsHorizontal
	 * @param imagePixelsVertical
	 */
	public void setGeometry(
			Vector3D apertureCentre,
			Vector3D viewDirection,
			Vector3D topDirection,
			double horizontalViewAngle,
			int imagePixelsHorizontal, int imagePixelsVertical
		)
	{
		this.apertureCentre = apertureCentre;
		this.viewDirection = viewDirection;
		this.topDirection = topDirection;
		this.horizontalViewAngle = horizontalViewAngle;

		setApertureCentre(apertureCentre);
		setCentreOfView(calculateCentreOfView(apertureCentre, viewDirection));
		setSpanVectors(
				calculateHorizontalSpanVector(viewDirection, topDirection, horizontalViewAngle),
				calculateVerticalSpanVector(viewDirection, topDirection, horizontalViewAngle, imagePixelsHorizontal, imagePixelsVertical)
			);
	}
	
	/**
	 * Create a clone of the original
	 * @param original
	 */
	public EditableRelativisticAnyFocusSurfaceCamera(EditableRelativisticAnyFocusSurfaceCamera original)
	{
		super(original);
		setImagePixelsHorizontal(original.getImagePixelsHorizontal());
		setImagePixelsVertical(original.getImagePixelsVertical());
		setAntiAliasingQuality(original.getAntiAliasingQuality());
		setBlur(original.getApertureSize(), original.getBlurQuality());
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.cameras.AnyFocusSurfaceCamera#clone()
	 */
	@Override
	public EditableRelativisticAnyFocusSurfaceCamera clone()
	{
		return new EditableRelativisticAnyFocusSurfaceCamera(this);
	}

	public int getImagePixelsHorizontal() {
		return imagePixelsHorizontal;
	}

	public void setImagePixelsHorizontal(int imagePixelsHorizontal) {
		this.imagePixelsHorizontal = imagePixelsHorizontal;
	}

	public int getImagePixelsVertical() {
		return imagePixelsVertical;
	}

	public void setImagePixelsVertical(int imagePixelsVertical) {
		this.imagePixelsVertical = imagePixelsVertical;
	}

	public QualityType getAntiAliasingQuality() {
		return antiAliasingQuality;
	}

	public void setAntiAliasingQuality(QualityType antiAliasingQuality)
	{
		this.antiAliasingQuality = antiAliasingQuality;

		double antiAliasingFactor = antiAliasingQuality.getAntiAliasingFactor();

		setDetectorPixelsHorizontal((int)(imagePixelsHorizontal*antiAliasingFactor));
		setDetectorPixelsVertical((int)(imagePixelsVertical*antiAliasingFactor));
		
		// not sure why I should (re)allocate the image memory here (?)
		// allocatePhotoMemory();
	}

	public ApertureSizeType getApertureSize() {
		return apertureSize;
	}

	public QualityType getBlurQuality() {
		return blurQuality;
	}

	public void setBlur(ApertureSizeType apertureSize, QualityType blurQuality)
	{
		this.apertureSize = apertureSize;
		this.blurQuality = blurQuality;

		double apertureRadius = apertureSize.getApertureRadius();
		int raysPerPixel =
				// the blur quality only matters if the aperture size is not "Pinhole"
				(apertureSize == ApertureSizeType.PINHOLE)?1:blurQuality.getRaysPerPixel();
				// CORRECTION: this is no longer true, as the blur quality also matters when simulating artefacts that lead to
				// blurring, such as pixellation effects of GCLAs
				// blurQuality.getRaysPerPixel();

		setApertureRadius(apertureRadius);
		setRaysPerPixel(raysPerPixel);
	}

	/**
	 * initialise the edit panel
	 */
	@Override
	public void createEditPanel(IPanel iPanel)
	{	
		this.iPanel = iPanel;
		
		editPanel = new JPanel();

		titledBorder = GUIBitsAndBobs.getTitledBorder("Eye");
		editPanel.setBorder(titledBorder);
		
		JTabbedPane tabbedPane = new JTabbedPane();
		
		JPanel defaultParametersPanel = new JPanel();
		defaultParametersPanel.setLayout(new MigLayout("insets 0"));

//		// the buttons dealing with the focus scene
//		ButtonsPanel focusButtonsPanel = new ButtonsPanel();
//
//		JButton focusOnSceneButton = focusButtonsPanel.addButton("Focus on scene");
//		focusOnSceneButton.addActionListener(this);
//
//		JButton editFocusSceneButton = focusButtonsPanel.addButton("Edit focus scene");
//		editFocusSceneButton.addActionListener(this);
//
//		editPanel.add(focusButtonsPanel);

		// add a bit of (non-stretchable) space
		// aperturePanel.add(Box.createRigidArea(new Dimension(10,5)));
		
		apertureCentrePanel = new LabelledVector3DPanel("Aperture centre");
		defaultParametersPanel.add(apertureCentrePanel, "wrap");
		viewDirectionPanel = new LabelledVector3DPanel("View direction");
		defaultParametersPanel.add(viewDirectionPanel, "wrap");
		topDirectionPanel = new LabelledVector3DPanel("Top direction");
		defaultParametersPanel.add(topDirectionPanel, "wrap");
		horizontalViewAnglePanel = new LabelledDoublePanel("Horizontal angle of view (degrees)");
		defaultParametersPanel.add(horizontalViewAnglePanel, "wrap");

//		blurPanel = new BlurPanel();
//		editPanel.add(blurPanel);

		JPanel focussingPanel = new JPanel();
		focussingPanel.setLayout(new MigLayout("insets 0"));
		focussingPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Focussing"));

		// the buttons dealing with the focus scene
		ButtonsPanel focusButtonsPanel = new ButtonsPanel();

		JButton focusOnSceneButton = focusButtonsPanel.addButton("Focus on scene");
		focusOnSceneButton.addActionListener(this);

		JButton editFocusSceneButton = focusButtonsPanel.addButton("Edit focus scene");
		editFocusSceneButton.addActionListener(this);

		focussingPanel.add(focusButtonsPanel, "wrap");

		blurPanel = new BlurPanel();
		focussingPanel.add(blurPanel, "wrap");
		
		defaultParametersPanel.add(focussingPanel, "wrap");

		antiAliasingQualityPanel = new LabelledQualityComboBox("Anti-aliasing quality");
		defaultParametersPanel.add(antiAliasingQualityPanel, "wrap");
		
		exposureCompensationComboBox = new JComboBox<ExposureCompensationType>(ExposureCompensationType.values());
		defaultParametersPanel.add(new LabelledComboBoxPanel("Exposure compensation", exposureCompensationComboBox), "wrap");
		
		tabbedPane.add(defaultParametersPanel, "Main parameters");

		JPanel relativisticPanel = new JPanel();
		relativisticPanel.setLayout(new MigLayout("insets 0"));

		// relativisticPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Relativistic effects"));
		betaPanel = new LabelledVector3DPanel("Velocity of camera (in scene frame, in units of c)");
		relativisticPanel.add(betaPanel, "wrap");
		JButton editCameraFrameSceneButton = new JButton("Edit camera-frame scene");
		editCameraFrameSceneButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		editCameraFrameSceneButton.addActionListener(this);
		relativisticPanel.add(editCameraFrameSceneButton, "wrap");
		shutterModelPanel = new ShutterModelPanel(this);
		shutterModelPanel.setCameraIPanel(this);
		relativisticPanel.add(shutterModelPanel, "wrap");
		JComboBox<LensType> lensTypeComboBox = new JComboBox<LensType>(LensType.values());
		lensTypeComboBox.setSelectedItem(LensType.IDEAL_LENS);
		// lensTypeComboBoxPanel = new LabelledComboBoxPanel("Camera lens", lensTypeComboBox);
		// relativisticPanel.add(lensTypeComboBoxPanel, "wrap");
//		detectorDistancePanel = new LabelledDoublePanel("Distance of detector behind entrance pupil");
//		detectorDistancePanel.setNumber(1);
//		relativisticPanel.add(detectorDistancePanel, "wrap");
		
		JPanel transformPanel = new JPanel();
		transformPanel.setLayout(new MigLayout("insets 0"));
		transformPanel.add(new JLabel("Transform light rays according to"));
		transformComboBox = new JComboBox<SpaceTimeTransformationType>(SpaceTimeTransformationType.values());
		transformComboBox.addActionListener(this);
		transformComboBox.setSelectedItem(SpaceTimeTransformationType.LORENTZ_TRANSFORMATION);
		transformPanel.add(transformComboBox);
		relativisticPanel.add(transformPanel, "wrap");
		
		tabbedPane.add(relativisticPanel, "Relativistic effects");

		editPanel.add(tabbedPane);

		//
		// validate the panel
		//
		
		editPanel.validate();
	}

	@Override
	public void discardEditPanel()
	{
		editPanel = null;
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.GUI.Editable#getEditPanel()
	 */
	@Override
	public JPanel getEditPanel()
	{
		return editPanel;
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.GUI.Editable#setValuesInEditPanel()
	 */
	@Override
	public void setValuesInEditPanel()
	{
		// initialize any fields
		apertureCentrePanel.setVector3D(apertureCentre);
		viewDirectionPanel.setVector3D(viewDirection);
		topDirectionPanel.setVector3D(topDirection);
		betaPanel.setVector3D(getBeta());
		horizontalViewAnglePanel.setNumber(horizontalViewAngle);
		shutterModelPanel.setShutterModel(getShutterModel());
		// detectorDistancePanel.setNumber(getDetectorDistance());
		transformComboBox.setSelectedItem(getTransformType());
		
		blurPanel.setBlur(apertureSize, blurQuality);
		antiAliasingQualityPanel.setQuality(antiAliasingQuality);
		exposureCompensationComboBox.setSelectedItem(getExposureCompensation());
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.GUI.Editable#acceptValuesInEditPanel()
	 */
	@Override
	public EditableRelativisticAnyFocusSurfaceCamera acceptValuesInEditPanel()
	{
		setGeometry(
				apertureCentrePanel.getVector3D(),
				viewDirectionPanel.getVector3D(),
				topDirectionPanel.getVector3D(),
				horizontalViewAnglePanel.getNumber(),
				imagePixelsHorizontal, imagePixelsVertical
			);
		setBlur(blurPanel.getApertureSize(), blurPanel.getBlurQuality());
		setAntiAliasingQuality(antiAliasingQualityPanel.getQuality());
		setGeometry(
				apertureCentrePanel.getVector3D(),
				viewDirectionPanel.getVector3D(),
				topDirectionPanel.getVector3D(),
				horizontalViewAnglePanel.getNumber(),
				getImagePixelsHorizontal(), getImagePixelsVertical()
			);
		setExposureCompensation((ExposureCompensationType)(exposureCompensationComboBox.getSelectedItem()));
    	setSpaceTimeTransformation((SpaceTimeTransformationType)(transformComboBox.getSelectedItem()), betaPanel.getVector3D());
		setShutterModel(shutterModelPanel.getShutterModel());
		// setDetectorDistance(detectorDistancePanel.getNumber());
		return this;
	}

	public SceneObject getScene() {
		return scene;
	}

	@Override
	public void setScene(EditableSceneObjectCollection scene) {
		this.scene = scene;
	}


	@Override
	public void actionPerformed(ActionEvent e)
	{
		if(e.getActionCommand().equals("Focus on scene"))
		{
			setFocusScene(scene.clone());
		}
		else if(e.getActionCommand().equals("Edit focus scene"))
		{
			// edit the focus scene
			IPanelComponent iPanelComponent = (IPanelComponent)focusScene;
			iPanel.addFrontComponent(iPanelComponent, "Edit focus scene");

//			if(iPanelComponent instanceof EditableSceneObjectCollection)
//			{
//				// when editing the focus scene, don't allow the objects to be intersected etc.
//				((EditableSceneObjectCollection)iPanelComponent).setCombinationModePanelVisible(false);
//			}
			iPanelComponent.setValuesInEditPanel();

			// replace the container's list panel with the component's edit panel
			// setEditPanelComponent(iPanelComponent.getEditPanel(iPanel));
		}
		else if(e.getActionCommand().equals("Edit camera-frame scene"))
		{
			// edit the camera-frame scene
			IPanelComponent iPanelComponent = (IPanelComponent)cameraFrameScene;
			iPanel.addFrontComponent(iPanelComponent, "Edit camera-frame scene");

//			if(iPanelComponent instanceof EditableSceneObjectCollection)
//			{
//				// when editing the focus scene, don't allow the objects to be intersected etc.
//				((EditableSceneObjectCollection)iPanelComponent).setCombinationModePanelVisible(false);
//			}
			iPanelComponent.setValuesInEditPanel();

			// replace the container's list panel with the component's edit panel
			// setEditPanelComponent(iPanelComponent.getEditPanel(iPanel));
		}
	}
	
	@Override
	public Ray getRayForImagePixel(int imagePixelsHorizontal, int imagePixelsVertical, double i, double j)
	{
		return super.getRayForImagePixel(imagePixelsHorizontal, imagePixelsVertical, i, j);
	}

	@Override
	public void backToFront(IPanelComponent edited)
	{
		// setFocusScene((SceneObject)edited);
	}

}
