package optics.raytrace.GUI.lowLevel;

import javax.swing.*;

import net.miginfocom.swing.MigLayout;
import optics.DoubleColour;

/**
 * This panel allows editing of a colour.
 */
public class DoubleColourPanel extends JPanel
{
	private static final long serialVersionUID = 1L;
	
	private DoublePanel rPanel, gPanel, bPanel;

	public DoubleColourPanel()
	{
		super();
		setLayout(new MigLayout("insets 0"));
		
		add(new JLabel("R"));
		
		// add a bit of (non-stretchable) space between the image and the status/button line
		// add(Box.createRigidArea(new Dimension(0, 0)));
		
		rPanel = new DoublePanel();
		add(rPanel);

		// add a bit of (non-stretchable) space between the image and the status/button line
		// add(Box.createRigidArea(new Dimension(0, 0)));

		add(new JLabel(", G"));
		
		// add a bit of (non-stretchable) space between the image and the status/button line
		// add(Box.createRigidArea(new Dimension(0, 0)));

		gPanel = new DoublePanel();
		add(gPanel);

		// add a bit of (non-stretchable) space between the image and the status/button line
		// add(Box.createRigidArea(new Dimension(0, 0)));

		add(new JLabel(", B"));
		
		// add a bit of (non-stretchable) space between the image and the status/button line
		// add(Box.createRigidArea(new Dimension(0, 0)));

		bPanel = new DoublePanel();
		add(bPanel);

		// add a bit of (non-stretchable) space between the image and the status/button line
		// add(Box.createRigidArea(new Dimension(0, 0)));
		
		// add a (stretchable) space between the status indicator and the edit button
		// statusAndEditButton.add(Box.createHorizontalGlue());
	}

	public void setDoubleColour(DoubleColour doubleColour)
	{
		rPanel.setNumber(doubleColour.getR());
		gPanel.setNumber(doubleColour.getG());
		bPanel.setNumber(doubleColour.getB());
	}

	public DoubleColour getDoubleColour()
	{
		return new DoubleColour(
				rPanel.getNumber(),
				gPanel.getNumber(),
				bPanel.getNumber()
		);
	}
}


