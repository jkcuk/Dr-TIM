package optics.raytrace.GUI.lowLevel;

import javax.swing.*;

import net.miginfocom.swing.MigLayout;

/**
 * A label and a panel for editing a 3D format.
 */
public class LabelledFormat3DComboBox extends JPanel
{	
	private static final long serialVersionUID = -1148921016268487496L;

	private Format3DComboBox format3DComboBox;

	public LabelledFormat3DComboBox(String text)
	{
		super();
		setLayout(new MigLayout("insets 0"));

		add(new JLabel(text + " "));	// add a space to make it prettier!
		// add(Box.createRigidArea(new Dimension(0,0)));
		format3DComboBox = new Format3DComboBox();
		add(format3DComboBox);
	}

	public void setFormat3D(Format3DType format3D)
	{
		format3DComboBox.setFormat3D(format3D);
	}

	public Format3DType getFormat3D()
	{
		return format3DComboBox.getFormat3D();
	}
	
	@Override
	public void setEnabled(boolean enabled)
	{
		format3DComboBox.setEnabled(enabled);
	}
}