package optics.raytrace.GUI.lowLevel;

import javax.swing.*;

import optics.raytrace.GUI.cameras.ResolutionType;

/**
 * Allows choice of a standard resolutions.
 */
public class ResolutionComboBox extends JComboBox<ResolutionType>
{
	private static final long serialVersionUID = 2499328969460443261L;

	public ResolutionComboBox()
	{
		super(ResolutionType.values());
	}

	public void setResolution(ResolutionType resolution)
	{
		setSelectedItem(resolution);
	}

	public ResolutionType getResolution()
	{
		return (ResolutionType)(getSelectedItem());
	}
}


