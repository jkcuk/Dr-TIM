package optics.raytrace.GUI.cameras;

import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import math.*;
import optics.raytrace.GUI.core.*;
import optics.raytrace.GUI.lowLevel.GUIBitsAndBobs;
import optics.raytrace.GUI.lowLevel.LabelledDoublePanel;
import optics.raytrace.GUI.lowLevel.LabelledIntPanel;
import optics.raytrace.cameras.ApertureCamera;
import optics.raytrace.core.Camera;
import optics.raytrace.core.Ray;

/**
 * A normal camera with a circular aperture, focussed on a plane in front of the camera.
 */
public class EditableApertureCamera extends ApertureCamera implements CameraWithRayForImagePixel, Camera, IPanelComponent
{
	private static final long serialVersionUID = 701691212156406139L;

	private JPanel editPanel;
	private LabelledDoublePanel focusDistancePanel, apertureRadiusPanel;
	private LabelledIntPanel raysPerPixelPanel;

	// TODO add exposure compensation editing capabilities --- see EditableRelativisticAnyFocusSurfaceCamera
	
	public EditableApertureCamera(
			String name,
			Vector3D apertureCentre,
			Vector3D viewDirection,
			Vector3D horizontalSpanVector, Vector3D verticalSpanVector,
			int detectorPixelsHorizontal, int detectorPixelsVertical, 
			ExposureCompensationType exposureCompensation,
			int maxTraceLevel,
			double focusDistance,
			double apertureRadius,
			int raysPerPixel
		)
	{
		super(	name,
				apertureCentre,	// pinhole position is aperture Centre
				viewDirection,
				horizontalSpanVector, verticalSpanVector,
				detectorPixelsHorizontal, detectorPixelsVertical, 
				exposureCompensation,
				maxTraceLevel,
				focusDistance,
				apertureRadius,
				raysPerPixel
		);
	}

	/**
	 * Create a clone of the original
	 * @param original
	 */
	public EditableApertureCamera(EditableApertureCamera original)
	{
		super(original);
	}
	
	/* (non-Javadoc)
	 * @see optics.raytrace.cameras.EditableApertureCamera#clone()
	 */
	@Override
	public EditableApertureCamera clone()
	{
		return new EditableApertureCamera(this);
	}

	/**
	 * initialise the edit panel
	 */
	@Override
	public void createEditPanel(IPanel iPanel)
	{	
		editPanel = new JPanel();
		editPanel.setLayout(new MigLayout("insets 0"));

		editPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Camera"));

		focusDistancePanel = new LabelledDoublePanel("focus distance");
		editPanel.add(focusDistancePanel, "wrap");

		apertureRadiusPanel = new LabelledDoublePanel("aperture radius");
		editPanel.add(apertureRadiusPanel, "wrap");
		
		raysPerPixelPanel = new LabelledIntPanel("rays per pixel");
		raysPerPixelPanel.setToolTipText(
				"Determines the number of rays used to calculate a pixel. " +
				"Each ray passes through a random point on the aperture, " +
				"so this parameter is useful only if the aperture radius " +
				"is greater than zero. More rays per pixels mean a less " +
				"grainy appearance of the photo, but also a longer render " +
				"time (which is proportional to the number of rays per " +
				"pixel). " +
				"Sensible values are in the range 1 to 100.");
		editPanel.add(raysPerPixelPanel);
		
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
		// ignore the repaintComponent
		return editPanel;
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.GUI.Editable#setValuesInEditPanel()
	 */
	@Override
	public void setValuesInEditPanel()
	{
		// initialize any fields
		focusDistancePanel.setNumber(focusDistance);
		apertureRadiusPanel.setNumber(apertureRadius);
		raysPerPixelPanel.setNumber(raysPerPixel);
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.GUI.Editable#acceptValuesInEditPanel()
	 */
	@Override
	public EditableApertureCamera acceptValuesInEditPanel()
	{
		setFocusDistance(focusDistancePanel.getNumber());
		setApertureRadius(apertureRadiusPanel.getNumber());
		setRaysPerPixel(raysPerPixelPanel.getNumber());
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
