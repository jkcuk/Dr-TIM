package optics.raytrace.GUI.cameras;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import net.miginfocom.swing.MigLayout;
import math.*;
import math.SpaceTimeTransformation.SpaceTimeTransformationType;
import optics.raytrace.GUI.cameras.shutterModels.ShutterModelPanel;
import optics.raytrace.GUI.core.*;
import optics.raytrace.GUI.lowLevel.ApertureSizeType;
import optics.raytrace.GUI.lowLevel.BlurPanel;
import optics.raytrace.GUI.lowLevel.ButtonsPanel;
import optics.raytrace.GUI.lowLevel.GUIBitsAndBobs;
import optics.raytrace.GUI.lowLevel.LabelledQualityComboBox;
import optics.raytrace.GUI.lowLevel.LabelledVector3DPanel;
import optics.raytrace.GUI.sceneObjects.EditableSceneObjectCollection;
import optics.raytrace.cameras.RelativisticAnaglyphCamera;
import optics.raytrace.cameras.shutterModels.AperturePlaneShutterModel;
import optics.raytrace.core.Camera;
import optics.raytrace.core.Ray;
import optics.raytrace.core.SceneObject;

/**
 * A 3D camera that can focus on arbitrary surfaces (and not just planes, like normal cameras).
 */
public class EditableRelativisticAnaglyphCamera extends RelativisticAnaglyphCamera
implements CameraWithRayForImagePixel, Camera, IPanelComponent, ActionListener, NeedsSceneSetBeforeEditing
{
	private static final long serialVersionUID = -5892748392792512464L;

	private QualityType antiAliasingQuality, blurQuality;
	private ApertureSizeType apertureSize;
	private int imagePixelsHorizontal, imagePixelsVertical;

	private JPanel editPanel;
	private JPanel focussingPanel;
	private LabelledVector3DPanel centreOfViewPanel, eyeSeparationPanel;
	// private JComboBox<ExposureCompensationType> exposureCompensationComboBox;	// TODO link this in
	private LabelledVector3DPanel betaPanel;	// the speed of the camera, in units of c, relative to the scene
	private ShutterModelPanel shutterModelPanel;
	private JComboBox<SpaceTimeTransformationType> transformComboBox;
	private JCheckBox colourPanel;
	private LabelledQualityComboBox antiAliasingQualityPanel;
	private BlurPanel blurPanel;

	/**
	 * the component to revalidate and repaint when the size of this panel changes
	 */
	private IPanel iPanel;

	private EditableSceneObjectCollection scene;

	/**
	 * @param name
	 * @param betweenTheEyes
	 * @param centreOfView
	 * @param horizontalSpanVector
	 * @param verticalSpanVector
	 * @param eyeSeparation
	 * @param imagePixelsHorizontal
	 * @param imagePixelsVertical
	 * @param exposureCompensation
	 * @param maxTraceLevel
	 * @param focusScene
	 * @param apertureSize
	 * @param colour
	 * @param blurQuality
	 * @param antiAliasingQuality
	 */
	public EditableRelativisticAnaglyphCamera(
			String name,
			Vector3D betweenTheEyes,	// middle between two eyes
			Vector3D centreOfView,	// the point in the centre of both eyes' field of view
			Vector3D horizontalSpanVector,	// a vector pointing to the right
			Vector3D verticalSpanVector,
			Vector3D eyeSeparation,	// separation between the eyes
			SpaceTimeTransformationType spaceTimeTransformationType,
			Vector3D beta,
			int imagePixelsHorizontal, int imagePixelsVertical,
			ExposureCompensationType exposureCompensation,
			int maxTraceLevel,
            SceneObject focusScene,
            SceneObject cameraFrameScene,
            ApertureSizeType apertureSize,
 			boolean colour,
			QualityType blurQuality,
			QualityType antiAliasingQuality
	)
	{
		super(	name,
				betweenTheEyes,	// pinhole position is aperture Centre
				centreOfView,
				horizontalSpanVector, verticalSpanVector,
				eyeSeparation,
				spaceTimeTransformationType,
				beta,
				imagePixelsHorizontal, imagePixelsVertical,
				exposureCompensation,
				maxTraceLevel,
				focusScene,
				cameraFrameScene,	// scene in camera frame
				new AperturePlaneShutterModel(),
				0.,	// placeholder aperture radius
				1,	// placeholder rays per pixel
				colour
		);
		
		setImagePixelsHorizontal(imagePixelsHorizontal);
		setImagePixelsVertical(imagePixelsVertical);
		setAntiAliasingQuality(antiAliasingQuality);
		setBlur(apertureSize, blurQuality);
	}
	
	/**
	 * Slightly simpler constructor that calculates a sensible vertical span vector
	 * @param name
	 * @param betweenTheEyes
	 * @param centreOfView
	 * @param horizontalSpanVector
	 * @param eyeSeparation
	 * @param imagePixelsHorizontal
	 * @param imagePixelsVertical
	 * @param exposureCompensation
	 * @param maxTraceLevel
	 * @param focusScene
	 * @param cameraFrameScene
	 * @param apertureSize
	 * @param colour
	 * @param blurQuality
	 * @param antiAliasingQuality
	 */
	public EditableRelativisticAnaglyphCamera(
			String name,
			Vector3D betweenTheEyes,	// middle between two eyes
			Vector3D centreOfView,	// the point in the centre of both eyes' field of view
			Vector3D horizontalSpanVector,	// a vector pointing to the right
			Vector3D eyeSeparation,	// separation between the eyes
			SpaceTimeTransformationType spaceTimeTransformationType,
			Vector3D beta,
			int imagePixelsHorizontal, int imagePixelsVertical,
			ExposureCompensationType exposureCompensation,
			int maxTraceLevel,
            SceneObject focusScene,
            SceneObject cameraFrameScene,
            ApertureSizeType apertureSize,
 			boolean colour,
			QualityType blurQuality,
			QualityType antiAliasingQuality
		)
	{
		this(	name,
				betweenTheEyes,	// middle between two eyes
				centreOfView,	// the point in the centre of both eyes' field of view
				horizontalSpanVector,	// a vector pointing to the right
				Vector3D.crossProduct(
						horizontalSpanVector,
						Vector3D.difference(centreOfView, betweenTheEyes)
					).getWithLength(horizontalSpanVector.getLength() * imagePixelsVertical / imagePixelsHorizontal),
				eyeSeparation,	// separation between the eyes
				spaceTimeTransformationType,
				beta,
				imagePixelsHorizontal, imagePixelsVertical,
				exposureCompensation,
				maxTraceLevel,
	            focusScene,
	            cameraFrameScene,
	 			apertureSize,
	 			colour,
				blurQuality,
				antiAliasingQuality
			);
	}


	/**
	 * Create a clone of the original
	 * @param original
	 */
	public EditableRelativisticAnaglyphCamera(EditableRelativisticAnaglyphCamera original)
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
	public EditableRelativisticAnaglyphCamera clone()
	{
		return new EditableRelativisticAnaglyphCamera(this);
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

	@Override
	public void setDetectorPixelsHorizontal(int detectorPixelsHorizontal)
	{
		super.setDetectorPixelsHorizontal(detectorPixelsHorizontal);
		
		getLeftCamera().setDetectorPixelsHorizontal(detectorPixelsHorizontal);
		getRightCamera().setDetectorPixelsHorizontal(detectorPixelsHorizontal);
	}

	@Override
	public void setDetectorPixelsVertical(int detectorPixelsVertical)
	{
		super.setDetectorPixelsVertical(detectorPixelsVertical);
		
		getLeftCamera().setDetectorPixelsVertical(detectorPixelsVertical);
		getRightCamera().setDetectorPixelsVertical(detectorPixelsVertical);
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
		int raysPerPixel = (apertureSize == ApertureSizeType.PINHOLE)?1:blurQuality.getRaysPerPixel();

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
		editPanel.setLayout(new MigLayout("insets 0"));
		editPanel.setBorder(GUIBitsAndBobs.getTitledBorder("3D view"));
		
		JTabbedPane tabbedPane = new JTabbedPane();
		
		JPanel defaultParametersPanel = new JPanel();
		defaultParametersPanel.setLayout(new MigLayout("insets 0"));
		
		colourPanel = new JCheckBox("Colour");
		colourPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		defaultParametersPanel.add(colourPanel, "wrap");
		
		// the centre of view
		centreOfViewPanel = new LabelledVector3DPanel("Centre of view");
		defaultParametersPanel.add(centreOfViewPanel, "wrap");
		
		// the eye separation
		eyeSeparationPanel = new LabelledVector3DPanel("Eye separation");
		defaultParametersPanel.add(eyeSeparationPanel, "wrap");

		focussingPanel = new JPanel();
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
		relativisticPanel.add(shutterModelPanel, "wrap");
		
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
		colourPanel.setSelected(isColour());
		centreOfViewPanel.setVector3D(getCentreOfView());
		eyeSeparationPanel.setVector3D(getEyeSeparation());
		betaPanel.setVector3D(getBeta());
		shutterModelPanel.setShutterModel(getShutterModel());
		transformComboBox.setSelectedItem(getTransformType());
		blurPanel.setBlur(apertureSize, blurQuality);
		antiAliasingQualityPanel.setQuality(antiAliasingQuality);
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.GUI.Editable#acceptValuesInEditPanel()
	 */
	@Override
	public EditableRelativisticAnaglyphCamera acceptValuesInEditPanel()
	{
		setParameters(
				getBetweenTheEyes(),	// middle between two eyes
				centreOfViewPanel.getVector3D(),	// the point in the centre of both eyes' field of view
				getHorizontalSpanVector(), getVerticalSpanVector(),
				eyeSeparationPanel.getVector3D(),	// separation between the eyes
				getDetectorPixelsHorizontal(), getDetectorPixelsVertical(),
				colourPanel.isSelected()
			);
		setSpaceTimeTransformation((SpaceTimeTransformationType)(transformComboBox.getSelectedItem()), betaPanel.getVector3D());
		setShutterModel(shutterModelPanel.getShutterModel());
		setBlur(blurPanel.getApertureSize(), blurPanel.getBlurQuality());
		setAntiAliasingQuality(antiAliasingQualityPanel.getQuality());
		
		// setup the two cameras
		setupCameras();
		
		return this;
	}

	public SceneObject getScene()
	{
		return scene;
	}

	@Override
	public void setScene(EditableSceneObjectCollection scene)
	{
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
			IPanelComponent iPanelComponent = (IPanelComponent)getFocusScene();
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
	public void backToFront(IPanelComponent edited)
	{
		// setFocusScene((SceneObject)edited);
	}
	
	@Override
	public Ray getRayForImagePixel(int imagePixelsHorizontal, int imagePixelsVertical, double i, double j)
	{
		return super.getRayForImagePixel(imagePixelsHorizontal, imagePixelsVertical, i, j);
	}
}
