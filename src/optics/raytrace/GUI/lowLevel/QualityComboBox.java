package optics.raytrace.GUI.lowLevel;

import javax.swing.*;

import optics.raytrace.GUI.cameras.QualityType;

/**
 * Allows choice of a quality.
 */
public class QualityComboBox extends JComboBox<QualityType>
{
	private static final long serialVersionUID = -8189286449393472483L;

	public QualityComboBox()
	{
		super(QualityType.values());
	}

	public void setQuality(QualityType quality)
	{
		setSelectedItem(quality);
	}

	public QualityType getQuality()
	{
		return (QualityType)getSelectedItem();
	}
}


