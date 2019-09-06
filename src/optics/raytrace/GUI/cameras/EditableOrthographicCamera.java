package optics.raytrace.GUI.cameras;

import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import net.miginfocom.swing.MigLayout;
import math.*;
import optics.raytrace.GUI.core.*;
import optics.raytrace.GUI.lowLevel.GUIBitsAndBobs;
import optics.raytrace.GUI.lowLevel.LabelledQualityComboBox;
import optics.raytrace.cameras.OrthographicCamera;
import optics.raytrace.core.Camera;
import optics.raytrace.core.Ray;

/**
 * An orthographic camera.
 */
public class EditableOrthographicCamera extends OrthographicCamera
implements CameraWithRayForImagePixel, Camera, IPanelComponent
{
	private static final long serialVersionUID = 8805775061363923451L;

	private QualityType antiAliasingQuality;
	protected int imagePixelsHorizontal, imagePixelsVertical;

	protected JPanel editPanel;	// the aperture panel will contain all the fields, the bottom panel will contain the OK and Cancel buttons
	protected LabelledQualityComboBox antiAliasingQualityPanel;

	/**
	 * Create a new orthographic camera
	 * 
	 * @param name
	 * @param viewDirection
	 * @param horizontalSpanVector3D	vector spanning the projection plane in direction corresponding to horizontal; length is width of displayed range
	 * @param verticalSpanVector3D	vector spanning the projection plane in direction corresponding to vertical; length meaningless
	 * @param imagePixelsHorizontal	number of detector pixels in the horizontal direction
	 * @param imagePixelsVertical	number of detector pixels in the vertical direction
	 */
	public EditableOrthographicCamera(
			String name,
			Vector3D viewDirection,
			Vector3D CCDCentre,
			Vector3D horizontalSpanVector3D, Vector3D verticalSpanVector3D,
			int imagePixelsHorizontal, int imagePixelsVertical,
			int maxTraceLevel,
			QualityType antiAliasingQuality
	)
	{
		super(	name,
				viewDirection, CCDCentre,
				horizontalSpanVector3D, verticalSpanVector3D,
				imagePixelsHorizontal, imagePixelsVertical,
				maxTraceLevel);

		this.imagePixelsHorizontal = imagePixelsHorizontal;
		this.imagePixelsVertical = imagePixelsVertical;
		setAntiAliasingQuality(antiAliasingQuality);
	}

	/**
	 * Create a clone of the original
	 * @param original
	 */
	public EditableOrthographicCamera(EditableOrthographicCamera original)
	{
		super(original);
		
		setImagePixelsHorizontal(original.getImagePixelsHorizontal());
		setImagePixelsVertical(original.getImagePixelsVertical());
		setAntiAliasingQuality(original.getAntiAliasingQuality());
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.cameras.AnyFocusSurfaceCamera#clone()
	 */
	@Override
	public EditableOrthographicCamera clone()
	{
		return new EditableOrthographicCamera(this);
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

	public QualityType getAntiAliasingQuality()
	{
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


	/**
	 * initialise the edit panel
	 */
	@Override
	public void createEditPanel(IPanel iPanel)
	{	
		editPanel = new JPanel();
		editPanel.setLayout(new MigLayout("insets 0"));

		TitledBorder titledBorder = GUIBitsAndBobs.getTitledBorder("Orthographic camera");
		editPanel.setBorder(titledBorder);

		antiAliasingQualityPanel = new LabelledQualityComboBox("Anti-aliasing quality");
		editPanel.add(antiAliasingQualityPanel, "wrap");

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
		antiAliasingQualityPanel.setQuality(antiAliasingQuality);
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.GUI.Editable#acceptValuesInEditPanel()
	 */
	@Override
	public EditableOrthographicCamera acceptValuesInEditPanel()
	{
		setAntiAliasingQuality(antiAliasingQualityPanel.getQuality());
		return this;
	}

	@Override
	public void backToFront(IPanelComponent edited)
	{
	}
	
	@Override
	public Ray getRayForImagePixel(int imagePixelsHorizontal, int imagePixelsVertical, double i, double j)
	{
		return super.getRayForImagePixel(imagePixelsHorizontal, imagePixelsVertical, i, j);
	}
}
