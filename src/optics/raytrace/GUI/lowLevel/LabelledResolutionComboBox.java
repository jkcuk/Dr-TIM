package optics.raytrace.GUI.lowLevel;

import javax.swing.*;

import net.miginfocom.swing.MigLayout;
import optics.raytrace.GUI.cameras.ResolutionType;

/**
 * A label and a panel for editing a resolution.
 */
public class LabelledResolutionComboBox extends JPanel
{	
	private static final long serialVersionUID = -5698762012775908057L;

	private ResolutionComboBox resolutionComboBox;

	public LabelledResolutionComboBox(String text)
	{
		super();
		setLayout(new MigLayout("insets 0"));

		add(new JLabel(text));	// add a space to make it prettier!
		// add(Box.createRigidArea(new Dimension(0,0)));
		resolutionComboBox = new ResolutionComboBox();
		add(resolutionComboBox);
	}

	public void setResolution(ResolutionType resolution)
	{
		resolutionComboBox.setResolution(resolution);
	}

	public ResolutionType getResolution()
	{
		return resolutionComboBox.getResolution();
	}
	
	@Override
	public void setEnabled(boolean enabled)
	{
		resolutionComboBox.setEnabled(enabled);
	}
}