package optics.raytrace.GUI.lowLevel;

import javax.swing.*;

import net.miginfocom.swing.MigLayout;

/**
 * A label and a panel for editing a string.
 */
public class LabelledStringPanel extends JPanel
{
	private static final long serialVersionUID = 1L;
	
	private JTextField textField;

	public LabelledStringPanel(String text)
	{
		super();
		setLayout(new MigLayout("insets 0"));

		add(new JLabel(text)); 
		// add(Box.createRigidArea(new Dimension(0,0)));
		textField = new JTextField();
		textField.setColumns(15);
		add(textField);
		// add(Box.createHorizontalGlue() );
	}

	public void setString(String string)
	{
		textField.setText(string);
	}

	public String getString()
	{
		return textField.getText();
	}
	
	public void setEditable(boolean b)
	{
		textField.setEditable(b);
	}
}