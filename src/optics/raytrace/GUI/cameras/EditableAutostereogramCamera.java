package optics.raytrace.GUI.cameras;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import net.miginfocom.swing.MigLayout;
import math.*;
import optics.raytrace.GUI.core.*;
import optics.raytrace.GUI.lowLevel.GUIBitsAndBobs;
import optics.raytrace.GUI.lowLevel.LabelledDoublePanel;
import optics.raytrace.GUI.lowLevel.LabelledQualityComboBox;
import optics.raytrace.GUI.lowLevel.LabelledVector3DPanel;
import optics.raytrace.GUI.sceneObjects.EditableSceneObjectCollection;
import optics.raytrace.cameras.AutostereogramCamera;
import optics.raytrace.core.Camera;
import optics.raytrace.core.Ray;
import optics.raytrace.core.SceneObject;

/**
 * An autostereogram ("Magic eye") camera.
 */
public class EditableAutostereogramCamera extends AutostereogramCamera
implements CameraWithRayForImagePixel, Camera, IPanelComponent, ActionListener
{
	private static final long serialVersionUID = -5478157413015399743L;

	private QualityType antiAliasingQuality;
	private double antiAliasingFactor;	// pre-calculated in setAntiAliasingQuality method
	private int imagePixelsHorizontal, imagePixelsVertical;
	private double eyeSeparation, stereogramWidth, dotsPerImagePixel, dotRadiusInImagePixels;
	
	private JPanel editPanel;
	private LabelledVector3DPanel centreOfViewPanel;
	private LabelledDoublePanel eyeSeparationPanel, stereogramWidthPanel; // , dotsPerImagePixelPanel, dotRadiusInImagePixelsPanel;
	private LabelledQualityComboBox antiAliasingQualityPanel;
	private TitledBorder titledBorder;

	private EditableSceneObjectCollection scene;

	/**
	 * @param name
	 * @param eyeSeparation
	 * @param centreOfView
	 * @param stereogramWidth
	 * @param dotsPerPixel
	 * @param dotRadius
	 * @param imagePixelsHorizontal
	 * @param imagePixelsVertical
	 * @param maxTraceLevel
	 * @param antiAliasingQuality
	 */
	public EditableAutostereogramCamera(
			String name,
			double eyeSeparation,
			Vector3D centreOfView,	// the point in the centre of both eyes' field of view
			double stereogramWidth,
			double dotsPerImagePixel,	// dots per pixel
			double dotRadiusInImagePixels,
			int imagePixelsHorizontal, int imagePixelsVertical,
			int maxTraceLevel,
			QualityType antiAliasingQuality
		)
	{
		super(	name,
				new Vector3D(-0.5*eyeSeparation, 0, 0),	// left eye
				new Vector3D(+0.5*eyeSeparation, 0, 0),	// right eye
				centreOfView,	// the point in the centre of both eyes' field of fiew
				new Vector3D(stereogramWidth, 0, 0),	// horizontal span vector
				new Vector3D(0, -stereogramWidth*imagePixelsVertical/imagePixelsHorizontal, 0),	// verticalSpanVector,
				dotsPerImagePixel,	// dots per pixel
				dotRadiusInImagePixels,
				imagePixelsHorizontal, imagePixelsVertical,
				maxTraceLevel
			);
		
		setImagePixelsHorizontal(imagePixelsHorizontal);
		setImagePixelsVertical(imagePixelsVertical);
		setEyeSeparation(eyeSeparation);
		setStereogramWidth(stereogramWidth);
		setDotsPerImagePixel(dotsPerImagePixel);
		setDotRadiusInImagePixels(dotRadiusInImagePixels);
		
		// do this at the end
		setAntiAliasingQuality(antiAliasingQuality);
	}

	/**
	 * Create a clone of the original
	 * @param original
	 */
	public EditableAutostereogramCamera(EditableAutostereogramCamera original)
	{
		super(original);
		
		setImagePixelsHorizontal(original.getImagePixelsHorizontal());
		setImagePixelsVertical(original.getImagePixelsVertical());
		setEyeSeparation(original.getEyeSeparation());
		setStereogramWidth(original.getStereogramWidth());
		setDotsPerImagePixel(original.getDotsPerImagePixel());
		setDotRadiusInImagePixels(original.getDotRadiusInImagePixels());
		
		// do this at the end
		setAntiAliasingQuality(original.getAntiAliasingQuality());
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.cameras.AnyFocusSurfaceCamera#clone()
	 */
	@Override
	public EditableAutostereogramCamera clone()
	{
		return new EditableAutostereogramCamera(this);
	}
	
	
	
	// setters and getters

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

	public double getEyeSeparation() {
		return eyeSeparation;
	}

	public void setEyeSeparation(double eyeSeparation) {
		this.eyeSeparation = eyeSeparation;
	}

	public double getStereogramWidth() {
		return stereogramWidth;
	}

	public void setStereogramWidth(double stereogramWidth) {
		this.stereogramWidth = stereogramWidth;
	}

	public double getDotsPerImagePixel() {
		return dotsPerImagePixel;
	}

	public void setDotsPerImagePixel(double dotsPerImagePixel)
	{
		this.dotsPerImagePixel = dotsPerImagePixel;
		setDotsPerPixel(dotsPerImagePixel/antiAliasingFactor/antiAliasingFactor);
	}

	public double getDotRadiusInImagePixels() {
		return dotRadiusInImagePixels;
	}

	public void setDotRadiusInImagePixels(double dotRadiusInImagePixels) {
		this.dotRadiusInImagePixels = dotRadiusInImagePixels;
		setDotRadius(dotRadiusInImagePixels*antiAliasingFactor);
	}

	public QualityType getAntiAliasingQuality() {
		return antiAliasingQuality;
	}

	public void setAntiAliasingQuality(QualityType antiAliasingQuality)
	{
		this.antiAliasingQuality = antiAliasingQuality;
		antiAliasingFactor = antiAliasingQuality.getAntiAliasingFactor();

		setDetectorPixelsHorizontal((int)(imagePixelsHorizontal*antiAliasingFactor));
		setDetectorPixelsVertical((int)(imagePixelsVertical*antiAliasingFactor));
		// allocatePhotoMemory();
		
		setDotsPerImagePixel(getDotsPerImagePixel());	// quality*10.,	// dots per pixel (on average)
		setDotRadiusInImagePixels(getDotRadiusInImagePixels());	// dot radius, in pixels
	}


	/**
	 * initialise the edit panel
	 */
	@Override
	public void createEditPanel(IPanel iPanel)
	{	
		editPanel = new JPanel();
		editPanel.setLayout(new MigLayout("insets 0"));

		titledBorder = GUIBitsAndBobs.getTitledBorder("3D view (Autostereogram)");
		editPanel.setBorder(titledBorder);
				
		// the look-at point
		centreOfViewPanel = new LabelledVector3DPanel("Centre of view");
		editPanel.add(centreOfViewPanel, "wrap");
		
		// stereogram width
		stereogramWidthPanel = new LabelledDoublePanel("Stereogram width");
		editPanel.add(stereogramWidthPanel, "wrap");
		
		// the eye separation
		eyeSeparationPanel = new LabelledDoublePanel("Eye separation");
		editPanel.add(eyeSeparationPanel, "wrap");
		
//		dotsPerImagePixelPanel = new LabelledDoublePanel(""), dotRadiusInImagePixelsPanel
		
		antiAliasingQualityPanel = new LabelledQualityComboBox("Anti-aliasing quality");
		editPanel.add(antiAliasingQualityPanel);

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
		stereogramWidthPanel.setNumber(getStereogramWidth());
		eyeSeparationPanel.setNumber(getEyeSeparation());
		antiAliasingQualityPanel.setQuality(antiAliasingQuality);
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.GUI.Editable#acceptValuesInEditPanel()
	 */
	@Override
	public EditableAutostereogramCamera acceptValuesInEditPanel()
	{
		setCentreOfView(centreOfViewPanel.getVector3D());	// the point in the centre of both eyes' field of view
		setStereogramWidth(stereogramWidthPanel.getNumber());
		setEyeSeparation(eyeSeparationPanel.getNumber());
		setAntiAliasingQuality(antiAliasingQualityPanel.getQuality());
		return this;
	}

	public SceneObject getScene() {
		return scene;
	}

	public void setScene(EditableSceneObjectCollection scene) {
		this.scene = scene;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
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
