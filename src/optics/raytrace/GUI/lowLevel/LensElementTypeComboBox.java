package optics.raytrace.GUI.lowLevel;

import javax.swing.*;

/**
 * Allows choice of lens-element type.
 */
public class LensElementTypeComboBox extends JComboBox<LensElementType>
{
	private static final long serialVersionUID = -3583737238044457874L;

	public LensElementTypeComboBox(boolean showGlassPaneOption)
	{
		super(LensElementType.values());
		if(!showGlassPaneOption)
		{
			removeItem(LensElementType.GLASS_PANE);
		}
	}

	public void setLensElementType(LensElementType lensElementType)
	{
		setSelectedItem(lensElementType);
	}

	public LensElementType getLensElementType()
	{
		return (LensElementType)getSelectedItem();
	}
}


