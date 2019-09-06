package optics.raytrace.GUI.lowLevel;

import javax.swing.*;

import optics.raytrace.GUI.cameras.QualityType;
import net.miginfocom.swing.MigLayout;

/**
 * A label and a panel for editing a quality.
 */
public class LabelledQualityComboBox extends JPanel
{
	private static final long serialVersionUID = 1L;
	
	private QualityComboBox qualityComboBox;

	public LabelledQualityComboBox(String text)
	{
		super();
		setLayout(new MigLayout("insets 0"));

		add(new JLabel(text));	// add a space to make it prettier!
		// add(Box.createRigidArea(new Dimension(0,0)));
		qualityComboBox = new QualityComboBox();
		add(qualityComboBox);
		// add(Box.createHorizontalGlue());
	}

	public void setQuality(QualityType quality)
	{
		qualityComboBox.setQuality(quality);
	}

	public QualityType getQuality()
	{
		return qualityComboBox.getQuality();
	}
	
	@Override
	public void setEnabled(boolean enabled)
	{
		qualityComboBox.setEnabled(enabled);
	}
}