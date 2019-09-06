package optics.raytrace.GUI.lowLevel;

import javax.swing.*;

/**
 * This panel allows editing of an int.
 */
public class IntPanel extends JTextField
{
	private static final long serialVersionUID = 1L;
	
	public IntPanel()
	{
		super();
		setColumns(4);
		// setPreferredSize(new Dimension(60,25));
	}

	public void setNumber(int number)
	{
		setText(""+number);
	}

	public int getNumber()
	{
		return Integer.parseInt(getText());
	}
}


