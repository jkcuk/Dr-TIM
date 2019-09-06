package optics.raytrace.GUI.lowLevel;

import javax.swing.*;

/**
 * Allows choice of aperture size.
 */
public class ApertureSizeComboBox extends JComboBox<ApertureSizeType>
{
	private static final long serialVersionUID = -1925989211387529549L;

	public ApertureSizeComboBox()
	{
		super(ApertureSizeType.values());
	}

	public void setApertureSize(ApertureSizeType apertureSize)
	{
		setSelectedItem(apertureSize);
	}

	public ApertureSizeType getApertureSize()
	{
		return (ApertureSizeType)getSelectedItem();
	}
}


