package optics.raytrace.panorama.panorama3DViewer;

import javax.swing.*;

import optics.raytrace.panorama.PanoramaResolutionType;

/**
 * Allows choice of a standard panorama resolutions.
 */
public class PanoramaResolutionComboBox extends JComboBox<PanoramaResolutionType>
{
	private static final long serialVersionUID = 7423561004619236379L;

	public PanoramaResolutionComboBox()
	{
		super(PanoramaResolutionType.values());
	}

	public void setResolution(PanoramaResolutionType resolution)
	{
		setSelectedItem(resolution);
	}

	public PanoramaResolutionType getResolution()
	{
		return (PanoramaResolutionType)(getSelectedItem());
	}
}


