package optics.raytrace.GUI.lowLevel;

import java.awt.event.ActionListener;

import javax.swing.*;

import net.miginfocom.swing.MigLayout;

/**
 * A label and a panel for editing aperture size.
 */
public class LabelledApertureSizeComboBox extends JPanel
{
	private static final long serialVersionUID = 1L;
	
	private ApertureSizeComboBox apertureSizeComboBox;

	public LabelledApertureSizeComboBox(String text)
	{
		super();
		setLayout(new MigLayout("insets 0"));
		// setLayout(new BoxLayout(this, BoxLayout.X_AXIS)); 

		add(new JLabel(text));	// add a space to make it prettier!
		// add(Box.createRigidArea(new Dimension(0,0)));
		apertureSizeComboBox = new ApertureSizeComboBox();
		add(apertureSizeComboBox);
		// add(Box.createHorizontalGlue());
	}

	public void setApertureSize(ApertureSizeType apertureSize)
	{
		apertureSizeComboBox.setApertureSize(apertureSize);
	}

	public ApertureSizeType getApertureSize()
	{
		return apertureSizeComboBox.getApertureSize();
	}
	
	public void addActionListener(ActionListener actionListener)
	{
		apertureSizeComboBox.addActionListener(actionListener);
	}
}