package optics.raytrace.GUI.lowLevel;

import javax.swing.*;

import net.miginfocom.swing.MigLayout;
import optics.DoubleColour;

/**
 * A label and a panel for editing a double colour.
 */
public class LabelledDoubleColourPanel extends JPanel
{
	private static final long serialVersionUID = 1L;
	
	private DoubleColourPanel doubleColourPanel;

	public LabelledDoubleColourPanel(String text)
	{
		super();
		setLayout(new MigLayout("insets 0"));

		add(new JLabel(text + " "));	// add a space to make it prettier!
		// add(Box.createRigidArea(new Dimension(0,0)));
		doubleColourPanel = new DoubleColourPanel();
		add(doubleColourPanel);
		// add(Box.createHorizontalGlue());
	}

	public void setDoubleColour(DoubleColour doubleColour)
	{
		doubleColourPanel.setDoubleColour(doubleColour);
	}

	public DoubleColour getDoubleColour()
	{
		return doubleColourPanel.getDoubleColour();
	}
}