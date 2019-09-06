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
 * An orthographic camera looking sideways.
 */
public class EditableOrthographicCameraSide extends EditableOrthographicCamera
{
	private static final long serialVersionUID = 4353667068193407204L;

	private double yCentre, zCentre, zLength;
	
	private LabelledDoublePanel yCentrePanel, zCentrePanel, zLengthPanel;

	/**
	 * Create a new side-view orthographic camera
	 * 
	 * @param name
	 * @param yCentre
	 * @param zCentre
	 * @param zLength
	 * @param imagePixelsHorizontal	number of detector pixels in the horizontal direction
	 * @param imagePixelsVertical	number of detector pixels in the vertical direction
	 */
	public EditableOrthographicCameraSide(
			String name,
			double yCentre,
			double zCentre,
			double zLength,
			int imagePixelsHorizontal, int imagePixelsVertical,
			int maxTraceLevel,
			QualityType antiAliasingQuality
	)
	{
		super(	name,
				new Vector3D(-1, 0, 0),	// viewDirection
				new Vector3D(0.9*MyMath.HUGE, yCentre, zCentre),	// CCDCentre
				new Vector3D(0, 0, zLength),	// horizontalSpanVector
				new Vector3D(0, -zLength*imagePixelsVertical/imagePixelsHorizontal, 0),	// verticalSpanVector
				imagePixelsHorizontal, imagePixelsVertical,
				maxTraceLevel,
				antiAliasingQuality);

		this.yCentre = yCentre;
		this.zCentre = zCentre;
		this.zLength = zLength;
	}

	/**
	 * Create a clone of the original
	 * @param original
	 */
	public EditableOrthographicCameraSide(EditableOrthographicCameraSide original)
	{
		super(original);
		
		setyCentre(original.getyCentre());
		setzCentre(original.getzCentre());
		setzLength(original.getzLength());
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.cameras.AnyFocusSurfaceCamera#clone()
	 */
	@Override
	public EditableOrthographicCameraSide clone()
	{
		return new EditableOrthographicCameraSide(this);
	}

	
	public double getyCentre() {
		return yCentre;
	}

	public void setyCentre(double yCentre) {
		this.yCentre = yCentre;
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

		yCentrePanel = new LabelledDoublePanel("y coordinate of centre of view");
		editPanel.add(yCentrePanel, "wrap");

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
		yCentrePanel.setNumber(yCentre);
		zCentrePanel.setNumber(zCentre);
		zLengthPanel.setNumber(zLength);
		antiAliasingQualityPanel.setQuality(getAntiAliasingQuality());
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.GUI.Editable#acceptValuesInEditPanel()
	 */
	@Override
	public EditableOrthographicCameraSide acceptValuesInEditPanel()
	{
		setyCentre(yCentrePanel.getNumber());
		setzCentre(zCentrePanel.getNumber());
		setzLength(zLengthPanel.getNumber());
		
		ccd.setCentrePosition(new Vector3D(0.9*MyMath.HUGE, yCentre, zCentre));
		ccd.setSpanVectors(
				new Vector3D(0, 0, zLength),
				new Vector3D(0, -zLength*imagePixelsVertical/imagePixelsHorizontal, 0)
			);
		ccd.validate();
		
		setAntiAliasingQuality(antiAliasingQualityPanel.getQuality());
		return this;
	}
}
