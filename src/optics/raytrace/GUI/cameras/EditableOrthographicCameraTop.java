package optics.raytrace.GUI.cameras;

import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import net.miginfocom.swing.MigLayout;
import math.*;
import optics.raytrace.GUI.core.IPanel;
import optics.raytrace.GUI.lowLevel.GUIBitsAndBobs;
import optics.raytrace.GUI.lowLevel.LabelledDoublePanel;
import optics.raytrace.GUI.lowLevel.LabelledQualityComboBox;

/**
 * An orthographic camera looking down.
 */
public class EditableOrthographicCameraTop extends EditableOrthographicCamera
{
	private static final long serialVersionUID = 4333725421723946205L;

	private double xCentre, zCentre, zLength;
	
	private LabelledDoublePanel xCentrePanel, zCentrePanel, zLengthPanel;

	/**
	 * Create a new top-view orthographic camera
	 * 
	 * @param name
	 * @param xCentre
	 * @param zCentre
	 * @param zLength
	 * @param imagePixelsHorizontal	number of detector pixels in the horizontal direction
	 * @param imagePixelsVertical	number of detector pixels in the vertical direction
	 */
	public EditableOrthographicCameraTop(
			String name,
			double xCentre,
			double zCentre,
			double zLength,
			int imagePixelsHorizontal, int imagePixelsVertical,
			int maxTraceLevel,
			QualityType antiAliasingQuality
	)
	{
		super(	name,
				new Vector3D(0, -1, 0),	// viewDirection
				new Vector3D(xCentre, 0.9*MyMath.HUGE, zCentre),	// CCDCentre
				new Vector3D(zLength*imagePixelsHorizontal/imagePixelsVertical, 0, 0),	// horizontalSpanVector
				new Vector3D(0, 0, -zLength),	// verticalSpanVector
				imagePixelsHorizontal, imagePixelsVertical,
				maxTraceLevel,
				antiAliasingQuality);

		this.xCentre = xCentre;
		this.zCentre = zCentre;
		this.zLength = zLength;
	}

	/**
	 * Create a clone of the original
	 * @param original
	 */
	public EditableOrthographicCameraTop(EditableOrthographicCameraTop original)
	{
		super(original);
		
		setxCentre(original.getxCentre());
		setzCentre(original.getzCentre());
		setzLength(original.getzLength());
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.cameras.AnyFocusSurfaceCamera#clone()
	 */
	@Override
	public EditableOrthographicCameraTop clone()
	{
		return new EditableOrthographicCameraTop(this);
	}

	
	public double getxCentre() {
		return xCentre;
	}

	public void setxCentre(double xCentre) {
		this.xCentre = xCentre;
	}

	public double getzCentre() {
		return zCentre;
	}

	public void setzCentre(double zCentre) {
		this.zCentre = zCentre;
	}

	public double getzLength() {
		return zLength;
	}

	public void setzLength(double zLength) {
		this.zLength = zLength;
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

		xCentrePanel = new LabelledDoublePanel("x coordinate of centre of view");
		editPanel.add(xCentrePanel, "wrap");

		zCentrePanel = new LabelledDoublePanel("z coordinate of centre of view");
		editPanel.add(zCentrePanel, "wrap");

		zLengthPanel = new LabelledDoublePanel("z range");
		editPanel.add(zLengthPanel, "wrap");

		antiAliasingQualityPanel = new LabelledQualityComboBox("Anti-aliasing quality");
		editPanel.add(antiAliasingQualityPanel);
		
		//
		// validate the panel
		//
		
		editPanel.validate();
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.GUI.Editable#setValuesInEditPanel()
	 */
	@Override
	public void setValuesInEditPanel()
	{
		// initialize any fields
		xCentrePanel.setNumber(xCentre);
		zCentrePanel.setNumber(zCentre);
		zLengthPanel.setNumber(zLength);
		antiAliasingQualityPanel.setQuality(getAntiAliasingQuality());
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.GUI.Editable#acceptValuesInEditPanel()
	 */
	@Override
	public EditableOrthographicCameraTop acceptValuesInEditPanel()
	{
		setxCentre(xCentrePanel.getNumber());
		setzCentre(zCentrePanel.getNumber());
		setzLength(zLengthPanel.getNumber());
		ccd.setCentrePosition(new Vector3D(xCentre, 0.9*MyMath.HUGE, zCentre));
		ccd.setSpanVectors(
				new Vector3D(zLength*imagePixelsHorizontal/imagePixelsVertical, 0, 0),
				new Vector3D(0, 0, -zLength)
			);
		ccd.validate();

		setAntiAliasingQuality(antiAliasingQualityPanel.getQuality());
		return this;
	}
}
