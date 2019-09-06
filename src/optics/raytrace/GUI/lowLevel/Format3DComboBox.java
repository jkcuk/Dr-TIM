package optics.raytrace.GUI.lowLevel;

import javax.swing.*;

/**
 * Allows choice of a standard 3D formats.
 */
public class Format3DComboBox extends JComboBox<Format3DType>
{
	private static final long serialVersionUID = -8682429154676938960L;

	public Format3DComboBox()
	{
		super(Format3DType.values());
	}

	public void setFormat3D(Format3DType format3D)
	{
		setSelectedItem(format3D);
	}

	public Format3DType getFormat3D()
	{
		return (Format3DType)(getSelectedItem());
	}
}


