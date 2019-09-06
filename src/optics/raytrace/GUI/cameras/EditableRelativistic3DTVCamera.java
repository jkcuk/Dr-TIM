package optics.raytrace.GUI.cameras;

import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import net.miginfocom.swing.MigLayout;
import math.*;
import optics.raytrace.GUI.cameras.shutterModels.ShutterModelPanel;
import optics.raytrace.GUI.core.*;
import optics.raytrace.GUI.lowLevel.BlurPanel;
import optics.raytrace.GUI.lowLevel.ButtonsPanel;
import optics.raytrace.GUI.lowLevel.Format3DType;
import optics.raytrace.GUI.lowLevel.GUIBitsAndBobs;
import optics.raytrace.GUI.lowLevel.LabelledFormat3DComboBox;
import optics.raytrace.GUI.lowLevel.LabelledQualityComboBox;
import optics.raytrace.GUI.lowLevel.LabelledVector3DPanel;
import optics.raytrace.GUI.lowLevel.OrientationType;
import optics.raytrace.GUI.lowLevel.ApertureSizeType;
import optics.raytrace.GUI.sceneObjects.EditableSceneObjectCollection;
import optics.raytrace.cameras.Relativistic3DTVCamera;
import optics.raytrace.cameras.shutterModels.AperturePlaneShutterModel;
import optics.raytrace.core.Camera;
import optics.raytrace.core.LightSource;
import optics.raytrace.core.Ray;
import optics.raytrace.core.SceneObject;

/**
 * A 3D camera that can focus on arbitrary surfaces (and not just planes, like normal cameras).
 */
public class EditableRelativistic3DTVCamera extends Relativistic3DTVCamera
implements CameraWithRayForImagePixel, Camera, IPanelComponent, ActionListener, NeedsSceneSetBeforeEditing
{
	private static final long serialVersionUID = -8359166765548017499L;

	private QualityType antiAliasingQuality, blurQuality;
	private ApertureSizeType apertureSize;
	private Format3DType format3D;

	private JPanel editPanel;
	private JPanel focussingPanel;
	private LabelledVector3DPanel centreOfViewPanel, eyeSeparationPanel;
	private LabelledVector3DPanel betaPanel;	// the speed of the camera, in units of c, relative to the scene
	private ShutterModelPanel shutterModelPanel;
	private LabelledQualityComboBox antiAliasingQualityPanel;
	private LabelledFormat3DComboBox format3DComboBox;
	private BlurPanel blurPanel;

	/**
	 * the component to revalidate and repaint when the size of this panel changes
	 */
	private IPanel iPanel;

	private EditableSceneObjectCollection scene;
	
	// TODO add exposure compensation editing capabilities --- see EditableRelativisticAnyFocusSurfaceCamera


	/**
	 * @param name
	 * @param betweenTheEyes
	 * @param centreOfView
	 * @param horizontalSpanVector
	 * @param verticalSpanVector
	 * @param eyeSeparation
	 * @param resolution
	 * @param exposureCompensation
	 * @param maxTraceLevel
	 * @param focusScene
	 * @param apertureSize
	 * @param blurQuality
	 * @param antiAliasingQuality
	 */
	public EditableRelativistic3DTVCamera(
			String name,
			Vector3D betweenTheEyes,	// middle between two eyes
			Vector3D centreOfView,	// the point in the centre of both eyes' field of view
			Vector3D horizontalSpanVector,	// a vector pointing to the right
			Vector3D verticalSpanVector,
			Vector3D eyeSeparation,	// separation between the eyes
			Vector3D beta,
			Format3DType format3D,
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
				betweenTheEyes,	// pinhole position is aperture Centre
				centreOfView,
				horizontalSpanVector, verticalSpanVector,
				eyeSeparation,
				beta,
				format3D,
				exposureCompensation,
				maxTraceLevel,
				focusScene,
				cameraFrameScene,	// scene in camera frame
				new AperturePlaneShutterModel(),
				1.,	// default detector distance behind entrance pupil
				0.,	// placeholder aperture radius
				1	// placeholder rays per pixel
		);
		
		setFormat3DAndAntiAliasingQuality(format3D, antiAliasingQuality);
		setBlur(apertureSize, blurQuality);
	}
	
	/**
	 * Slightly simpler constructor that calculates a sensible vertical span vector
	 * @param name
	 * @param betweenTheEyes
	 * @param centreOfView
	 * @param horizontalSpanVector
	 * @param eyeSeparation
	 * @param resolution
	 * @param exposureCompensation
	 * @param maxTraceLevel
	 * @param focusScene
	 * @param cameraFrameScene
	 * @param apertureSize
	 * @param blurQuality
	 * @param antiAliasingQuality
	 */
	public EditableRelativistic3DTVCamera(
			String name,
			Vector3D betweenTheEyes,	// middle between two eyes
			Vector3D centreOfView,	// the point in the centre of both eyes' field of view
			Vector3D horizontalSpanVector,	// a vector pointing to the right
			Vector3D eyeSeparation,	// separation between the eyes
			Vector3D beta,
			Format3DType format3D,
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
				betweenTheEyes,	// middle between two eyes
				centreOfView,	// the point in the centre of both eyes' field of view
				horizontalSpanVector,	// a vector pointing to the right
				Vector3D.crossProduct(
						horizontalSpanVector,
						Vector3D.difference(centreOfView, betweenTheEyes)
					).getWithLength(
							horizontalSpanVector.getLength() / format3D.getPixelAspectRatio() * 
							format3D.getVPixels() / format3D.getHPixels()
					),
				eyeSeparation,	// separation between the eyes
				beta,
				format3D,
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
	 * Create a clone of the original
	 * @param original
	 */
	public EditableRelativistic3DTVCamera(EditableRelativistic3DTVCamera original)
	{
		super(original);
		setFormat3DAndAntiAliasingQuality(original.getFormat3D(), original.getAntiAliasingQuality());
		setBlur(original.getApertureSize(), original.getBlurQuality());
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.cameras.AnyFocusSurfaceCamera#clone()
	 */
	@Override
	public EditableRelativistic3DTVCamera clone()
	{
		return new EditableRelativistic3DTVCamera(this);
	}

	public Format3DType getFormat3D() {
		return format3D;
	}

//	public void setResolution(ResolutionType resolution) {
//		this.resolution = resolution;
//		
//		setDetectorPixelsHorizontal((int)(resolution.getHPixels()*antiAliasingQuality.getAntiAliasingFactor()));
//		setDetectorPixelsVertical((int)(resolution.getVPixels()*antiAliasingQuality.getAntiAliasingFactor()));
//	}

	public QualityType getAntiAliasingQuality() {
		return antiAliasingQuality;
	}

	public void setFormat3DAndAntiAliasingQuality(Format3DType format3D, QualityType antiAliasingQuality)
	{
		this.format3D = format3D;
		this.antiAliasingQuality = antiAliasingQuality;

		double antiAliasingFactor = antiAliasingQuality.getAntiAliasingFactor();
		
		setFramePackingOrientation(format3D.getOrientation());
		setGapBetweenFrames(format3D.getGap());

		setFramePixelsHorizontal((int)(format3D.getHPixels()*antiAliasingFactor));
		setFramePixelsVertical((int)(format3D.getVPixels()*antiAliasingFactor));
		
		setSpanVectors(
				getHorizontalSpanVector(),
				Vector3D.crossProduct(
						getHorizontalSpanVector(),
						Vector3D.difference(getCentreOfView(), getBetweenTheEyes())
				).getWithLength(
						getHorizontalSpanVector().getLength() / format3D.getPixelAspectRatio() * 
						format3D.getVPixels() / format3D.getHPixels()
					)
		);
		
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
		editPanel.setBorder(GUIBitsAndBobs.getTitledBorder("3D TV"));
		
		JTabbedPane tabbedPane = new JTabbedPane();
		JPanel defaultParametersPanel = new JPanel();
		defaultParametersPanel.setLayout(new MigLayout("insets 0"));
				
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

		// add a bit of (non-stretchable) space
		// aperturePanel.add(Box.createRigidArea(new Dimension(10,5)));

		blurPanel = new BlurPanel();
		focussingPanel.add(blurPanel, "wrap");
		
		defaultParametersPanel.add(focussingPanel, "wrap");

		// add a bit of (non-stretchable) space
		// aperturePanel.add(Box.createRigidArea(new Dimension(10,5)));

		antiAliasingQualityPanel = new LabelledQualityComboBox("Anti-aliasing quality");
		defaultParametersPanel.add(antiAliasingQualityPanel, "wrap");
		
		format3DComboBox = new LabelledFormat3DComboBox("3D image format");
		defaultParametersPanel.add(format3DComboBox, "wrap");

		tabbedPane.add(defaultParametersPanel, "Main parameters");

		JPanel relativisticPanel = new JPanel();
		relativisticPanel.setLayout(new MigLayout("insets 0"));
		// relativisticPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Relativistic effects"));

		betaPanel = new LabelledVector3DPanel("Scene speed in camera frame (in units of c)");
		relativisticPanel.add(betaPanel, "wrap");
		JButton editCameraFrameSceneButton = new JButton("Edit camera-frame scene");
		editCameraFrameSceneButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		editCameraFrameSceneButton.addActionListener(this);
		relativisticPanel.add(editCameraFrameSceneButton, "wrap");
		shutterModelPanel = new ShutterModelPanel(this);
		relativisticPanel.add(shutterModelPanel, "wrap");
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
		centreOfViewPanel.setVector3D(getCentreOfView());
		eyeSeparationPanel.setVector3D(getEyeSeparation());
		betaPanel.setVector3D(getBeta());
		shutterModelPanel.setShutterModel(getShutterModel());
		blurPanel.setBlur(apertureSize, blurQuality);
		antiAliasingQualityPanel.setQuality(antiAliasingQuality);
		format3DComboBox.setFormat3D(getFormat3D());
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.GUI.Editable#acceptValuesInEditPanel()
	 */
	@Override
	public EditableRelativistic3DTVCamera acceptValuesInEditPanel()
	{
		setParameters(
				getBetweenTheEyes(),	// middle between two eyes
				centreOfViewPanel.getVector3D(),	// the point in the centre of both eyes' field of view
				getHorizontalSpanVector(), getVerticalSpanVector(),
				eyeSeparationPanel.getVector3D(),	// separation between the eyes
				getDetectorPixelsHorizontal(), getDetectorPixelsVertical()
			);
		setBeta(betaPanel.getVector3D());
		setShutterModel(shutterModelPanel.getShutterModel());
		setBlur(blurPanel.getApertureSize(), blurPanel.getBlurQuality());
		setFormat3DAndAntiAliasingQuality(format3DComboBox.getFormat3D(), antiAliasingQualityPanel.getQuality());
		
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


	// override a few functions to return the image at the screen resolution
	// use something like scaledImage = image.getScaledInstance(actualSize.width, actualSize.height, Image.SCALE_SMOOTH);

	public BufferedImage scaleBufferedImageToFormat3DSize(BufferedImage image, Format3DType format3D)
	{
		if(format3D == null) return image;	// if no format is specified, return the original image
		if(image == null) return image;
		
		// create a re-sized BufferedImage --- see http://stackoverflow.com/questions/11367324/how-do-i-scale-a-bufferedimage
		int
			w = format3D.getPackedImagePixels(OrientationType.HORIZONTAL),
			h = format3D.getPackedImagePixels(OrientationType.VERTICAL);
		BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = resizedImg.createGraphics();
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2.drawImage(image, 0, 0, w, h, null);
		g2.dispose();
		// System.out.println("resizedImg = "+resizedImg);
		return resizedImg;
	}
	
	@Override
	public BufferedImage takePhoto(SceneObject scene, LightSource lights,
			RaytraceWorker raytraceWorker)
	{
		return scaleBufferedImageToFormat3DSize(
				super.takePhoto(scene, lights, raytraceWorker),
				getFormat3D()
			);
	}

	@Override
	public BufferedImage takePhoto(SceneObject scene, LightSource lights) {
		return scaleBufferedImageToFormat3DSize(
				super.takePhoto(scene, lights),
				getFormat3D()
		);
	}

	@Override
	public BufferedImage getPhoto() {
		return scaleBufferedImageToFormat3DSize(
				super.getPhoto(),
				getFormat3D()
		);
	}

	@Override
	public void savePhoto(String filename, String format)
	{
		try
		{
			File outputfile = new File(filename);
			ImageIO.write(getPhoto(), format, outputfile);
		} catch (IOException e) {
			System.err.println("EditableRelativistic3DTVCamera::savePhoto: Error saving image");
		}
	}
	
	@Override
	public Ray getRayForImagePixel(int imagePixelsHorizontal, int imagePixelsVertical, double i, double j)
	{
		return super.getRayForImagePixel(imagePixelsHorizontal, imagePixelsVertical, i, j);
	}
}
